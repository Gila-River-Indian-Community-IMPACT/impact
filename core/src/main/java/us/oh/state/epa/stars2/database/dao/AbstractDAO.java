package us.oh.state.epa.stars2.database.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.BaseDBObject;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.framework.config.SQLLoader;
import us.oh.state.epa.stars2.framework.dbConnection.ConnectionManager;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.DataStoreConcurrencyException;
import us.oh.state.epa.stars2.framework.exception.OutOfMemoryDAOException;
import us.oh.state.epa.stars2.framework.util.StopWatch;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.portal.client.ImpactPortalClient;

public abstract class AbstractDAO implements TransactableDAO {

	@Autowired private DataSource dataSource;
	@Autowired private ConnectionManager connMgr;
	@Autowired private ConfigManager configManager;
	@Autowired private SQLLoader sqlLoader;
	
	@Autowired private DataSource aqdsDataSource;
	private String schema = CommonConst.READONLY_SCHEMA; //default dbo schema
	
	private transient Logger logger;

	private Transaction transaction;
	private StopWatch timer;
	
	protected int defaultSearchLimit = 50000;
	protected int defaultHitIncrementCheck = 5000;
	protected double defaultUsedMemoryLimit = 0.99;

	protected String sqlStatement;
	protected List<String> parameterList;
	
	/*
	 * See TFS task 6936 for details
	 * @see AbstractDAO#nextSequenceVal(String, int);
	 * A lock for providing synchronized access to the nextSequenceVal method.
	 * Need to synchronize on a class variable because the nextSequenceVal
	 * method can be called simultaneously from two different sub classes of 
	 * AbstractDAO class and both threads can be trying to access/update the 
	 * last_used_num field for the same sequence id.
	 */
	private static final Integer nextSequenceValMethodLock = new Integer(Integer.MAX_VALUE); 
	
	public AbstractDAO() {
		timer = new StopWatch();
		logger = Logger.getLogger(getClass());
	}

	@PostConstruct
	public void init() {
		
		//Node root = configManager.getNode("app.params.SearchLimit");
		//defaultSearchLimit = root.getAsInt("value", -1);
		
		//root = configManager.getNode("app.params.HitIncrementCheck");
		//defaultHitIncrementCheck = root.getAsInt("value", 5000);
		
		//root = configManager.getNode("app.params.UsedMemoryLimit");
		//defaultUsedMemoryLimit = root.getAsDouble("value", 0.95);
	}

	protected final int MAX_BATCH_SIZE = 
			(int)Config.getEnvEntry("app/bulkOperation/maxBatchSize",32);
	
	protected int calculateBatchSize(int totalSize) {
		return MAX_BATCH_SIZE > totalSize? totalSize : MAX_BATCH_SIZE;
	}
	
	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public SQLLoader getSqlLoader() {
		return sqlLoader;
	}

	public void setSqlLoader(SQLLoader sqlLoader) {
		this.sqlLoader = sqlLoader;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
//	public DAOFactory getDaoFactory() {
//		return daoFactory;
//	}
//
//	public void setDaoFactory(DAOFactory daoFactory) {
//		this.daoFactory = daoFactory;
//	}

	public DataSource getAqdsDataSource() {
		return aqdsDataSource;
	}

	public void setAqdsDataSource(DataSource aqdsDataSource) {
		this.aqdsDataSource = aqdsDataSource;
	}

	public ConnectionManager getConnMgr() {
		return connMgr;
	}

	public void setConnMgr(ConnectionManager connMgr) {
		this.connMgr = connMgr;
	}

	public final String getSchemaQualifer() {
		return configManager.getNode("app.Schemas."+schema).getAsString("name") + '.';
	}

	public final int getDefaultSearchLimit() {
		return this.defaultSearchLimit;
	}

	public final void setDefaultSearchLimit(int defaultSearchLimit) {
		this.defaultSearchLimit = defaultSearchLimit;
	}

	public final String trimLeadingTrailingSpace(String s) {
		String rtn = s;
		if (rtn != null) {
			rtn = s.trim();
		}
		return rtn;
	}

	protected final boolean isStagingDAO() {
		boolean ret = false;
//		DAOFactory daoFactory = null;

//		try {
//			daoFactory = (DAOFactory) CompMgr.newInstance("app.DAOFactory");
//		} catch (UnableToStartException utse) {
//			logger.error(utse.getMessage(), utse);
//		}

//		if (schemaQualifer != null
//				&& daoFactory != null
//				&& schemaQualifer.equals(daoFactory
//						.getSchemaQualifier(CommonConst.STAGING_SCHEMA))) {
//			ret = true;
//		}

		return ret;
	}
	
	//TODO what are these get[numeric-type]() methods for?  is this pre-java 5
	//boxing?

	static public final Integer getInteger(ResultSet rs, String columnName)
			throws SQLException {
		Integer ret = null;

		int tempInt = rs.getInt(columnName);

		if (!rs.wasNull()) {
			ret = new Integer(tempInt);
		}

		return ret;
	}

	static public final Integer getInteger(ResultSet rs, int columnNum)
			throws SQLException {
		Integer ret = null;

		int tempInt = rs.getInt(columnNum);

		if (!rs.wasNull()) {
			ret = new Integer(tempInt);
		}

		return ret;
	}

	static public final Float getFloat(ResultSet rs, String columnName)
			throws SQLException {
		Float ret = null;

		float tempFloat = rs.getFloat(columnName);

		if (!rs.wasNull()) {
			ret = new Float(tempFloat);
		}

		return ret;
	}
	
	static public final Float getFloat(ResultSet rs, int columnNum)
			throws SQLException {
		Float ret = null;

		float tempFloat = rs.getFloat(columnNum);

		if (!rs.wasNull()) {
			ret = new Float(tempFloat);
		}

		return ret;
	}

	static public final Double getDouble(ResultSet rs, String columnName)
			throws SQLException {
		Double ret = null;

		double tempDouble = rs.getDouble(columnName);

		if (!rs.wasNull()) {
			ret = new Double(tempDouble);
		}

		return ret;
	}
	
	static public final Double getDouble(ResultSet rs, int columnNum)
			throws SQLException {
		Double ret = null;

		double tempDouble = rs.getDouble(columnNum);

		if (!rs.wasNull()) {
			ret = new Double(tempDouble);
		}

		return ret;
	}

	static public final Boolean getBoolean(ResultSet rs, String columnName)
			throws SQLException {
		Boolean ret = null;

		boolean tempBool = rs.getBoolean(columnName);

		if (!rs.wasNull()) {
			ret = new Boolean(tempBool);
		}

		return ret;
	}
	
	static public final Long getLong(ResultSet rs, int columnNum)
			throws SQLException {
		Long ret = null;

		long tempLong = rs.getLong(columnNum);

		if (!rs.wasNull()) {
			ret = new Long(tempLong);
		}

		return ret;
	}
	
	static public final Long getLong(ResultSet rs, String columnName)
			throws SQLException {
		Long ret = null;

		long tempLong = rs.getLong(columnName);

		if (!rs.wasNull()) {
			ret = new Long(tempLong);
		}

		return ret;
	}
	
	static public final Short getShort(ResultSet rs, String columnName)
			throws SQLException {
		Short ret = null;

		short tempShort = rs.getShort(columnName);

		if (!rs.wasNull()) {
			ret = new Short(tempShort);
		}

		return ret;
	}

	static public final Short getShort(ResultSet rs, int columnNum)
			throws SQLException {
		Short ret = null;

		short tempShort = rs.getShort(columnNum);

		if (!rs.wasNull()) {
			ret = new Short(tempShort);
		}

		return ret;
	}

	public final void checkNull(Object... inParm) throws DAOException {
		for (int i = 0; i < inParm.length; i++) {
			Object o = inParm[i];
			if (o == null) {
				throw new DAOException("Input parameter[" + i + "] to DAO method null!");
			}
		}
	}

	public void removeRows(String tableName, String columnName,
			Object columnValue) throws DAOException {
		checkNull(tableName);
		checkNull(columnName);
		checkNull(columnValue);

		String table = tableName;
		if (getSchemaQualifer() != null) {
			table = getSchemaQualifer() + tableName;
		}

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw("DELETE FROM " + table + " WHERE "
				+ columnName + " = ?");

		if (columnValue.getClass().equals(Integer.class)) {
			connHandler.setInteger(1, (Integer) columnValue);
		} else if (columnValue.getClass().equals(String.class)) {
			connHandler.setString(1, (String) columnValue);
		} else {
			logger.error("Unsupported Type");
		}

		connHandler.remove();
	}

	public final void setTransaction(Transaction trans) {
		transaction = trans;
	}

	public final Connection getConnection() throws DAOException {
		if (logger.getParent().getLevel().equals(Level.INFO)) {
			timer.start();
		}

		Connection ret = DataSourceUtils.getConnection(dataSource);
		logger.debug("getting connection: " + ret.hashCode());
		logger.debug(" ---> isConnectionTransactional: " + DataSourceUtils.isConnectionTransactional(ret, dataSource));

		return ret;
	}

	public final Connection getAQDSConnection() throws DAOException {
		if (logger.getParent().getLevel().equals(Level.INFO)) {
			timer.start();
		}

		Connection ret = DataSourceUtils.getConnection(aqdsDataSource);
		logger.debug("getting connection to AQDS: " + ret.hashCode());
		logger.debug(" ---> isConnectionTransactional: " + DataSourceUtils.isConnectionTransactional(ret, aqdsDataSource));

		return ret;
	}

	public final Connection getReadOnlyConnection() throws DAOException {
//		if (logger.getParent().getLevel().equals(Level.INFO)) {
//			timer.start();
//		}
//
//		Connection ret = null;

		//TODO analyze read-only & tx behavior
//		if (transaction != null) {
//			logger.debug("Getting a read only connection with transaction.");
//			ret = transaction.getConnection(true);
//		} else {
//			logger.debug("Getting a read only connection without transaction.");
//			ret = getConnectionFromConnManager(true);
//		}
//		assert ret != null;
//		try {
//			ret = dataSource.getConnection();
//		} catch (SQLException e) {
//			handleException(e, ret);
//		}
//
//		return ret;
		return getConnection();
	}

	public final void handleClosing(Connection conn) throws DAOException {
		handleClosing(conn,dataSource);
	}

	public final void handleClosing(Connection conn, DataSource dataSource) throws DAOException {
		if (null == dataSource) {
			dataSource = this.dataSource;
		}
		if (!DataSourceUtils.isConnectionTransactional(conn,dataSource)) {
			logger.debug("conn not transactional; releasing: " + conn.hashCode());
			DataSourceUtils.releaseConnection(conn, dataSource);
		} else {
			logger.debug("conn is currently transactional; not releasing: " + conn.hashCode());			
		}
	}

	public final void handleException(Exception e, Connection conn)
			throws DAOException {

		// Log it and rethrow the exception as a DAOException.
		logger.error("Exception Caught in DAO.", e);
		if (sqlStatement != null) {
			String sqlMsg = "SQL =\n" + sqlStatement;
			if (parameterList != null) {
				for (String param : parameterList) {
					sqlMsg = sqlMsg + "\n" + param;
				}
				sqlMsg = sqlMsg + "\n";
			}
			logger.error(sqlMsg);
		}

		if (e.getClass().getName().endsWith("DataStoreConcurrencyException")) {
			throw new DataStoreConcurrencyException(e.getMessage());
		}
		if (e instanceof OutOfMemoryDAOException) {
			throw (OutOfMemoryDAOException) e;
		}

		DAOException daoe = null;
		if (e.getMessage() == null || e.getMessage().length() < 1) {
			daoe = new DAOException("Error during DAO operation: "
					+ e.getClass().getName() + ". ", e);
		} else {
            if(e instanceof DAOException) throw (DAOException)e;
            daoe = new DAOException(e.getMessage(), e);
		}
		throw daoe;

	}

	public final int executeUpdate(PreparedStatement stmt) throws DAOException {
		int ret = 0;
		StopWatch executeTimer = new StopWatch();
		executeTimer.start();
		// if(ret == 0) {
		// logger.error("ATTEMPTED executeUpdate");
		// throw new DAOException("ATTEMPTED executeUpdate");
		// }
		try {
			ret = stmt.executeUpdate();
			executeTimer.stop();
			logger.debug("Updated executed in " + executeTimer.elapsedTime()
					+ "ms");
		} catch (SQLException sqle) {
			// Log it and rethrow the exception as a DAOException.

			logger.error("Execute Update Failed!", sqle);

			throw new DAOException("Data error " + sqle.getMessage(), sqle);
		}

		if (ret == 0) {
			throw new DataStoreConcurrencyException(
					"Data out of date. Please refresh data, and try again");
		}

		return ret;
	}

	protected final void closeStatement(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Translates an "indicator" value to its boolean equivalent. Indicators are
	 * stored in the database as "Y" (true) or "N" (false).
	 * 
	 * @param ind
	 *            Indicator string from the database.
	 * 
	 * @return boolean Logical equivalent.
	 */
	static public final boolean translateIndicatorToBoolean(String ind) {
		boolean ret = false;

		if ((ind != null) && (ind.length() > 0)) {
			String s = ind.trim();
			s = s.substring(0, 1);

			if (s.compareToIgnoreCase("y") == 0) {
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Translates a <tt>boolean</tt> into the standard "indicator"
	 * representation used in the database. A value of <tt>true</tt> is
	 * translated to "Y" and <tt>false</tt> is translated to "N".
	 * 
	 * @param b
	 *            the boolean value to be translated.
	 * 
	 * @return true => "Y" or false => "N"
	 */
	static public final String translateBooleanToIndicator(Boolean b) {
		if ((b != null) && b) {
			return new String("Y");
		}

		return new String("N");
	}

	/**
	 * 
	 * Retrieves the lastModifed Timestamp from the DB for the given row,
	 * identified by table, column, and id.
	 * 
	 * @param table
	 *            name for requested row.
	 * @param column
	 *            name for requested row.
	 * @param id
	 *            the key used to identify the row.
	 * @return The requested Timestamp or null if row doesn't exist.
	 */
	protected final Integer getLastModified(String table, String column,
			Object id) throws DAOException {
		Integer lastModified = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuffer sql = null;

		try {
			// get connection
			conn = getReadOnlyConnection();

			sql = new StringBuffer();
			sql.append("SELECT last_modified FROM " + table + " ");
			sql.append("WHERE " + column + "=?");

			// prepare
			stmt = conn.prepareStatement(sql.toString());

			if (id.getClass().getName().equals("java.lang.String")) {
				stmt.setString(1, (String) id);
			} else if (id.getClass().getName().equals("java.lang.Integer")) {
				stmt.setInt(1, ((Integer) id).intValue());
			}

			// execute
			rs = stmt.executeQuery();

			if (rs.next()) {
				lastModified = getInteger(rs, 1);
			}
		} catch (SQLException sqle) {
			logger.error("Exception caught in getLastModified", sqle);
		} finally {
			handleClosing(conn);
		}

		return lastModified;
	}

	/**
	 * 
	 * Sets the LastModified Timestamp in the DB object. SetLastModified is
	 * impletemented at the BaseDB level and is a protected method, so that the
	 * lastModified Timestamp just can't be changed by anyone.
	 * 
	 * @param bo
	 *            the object to set the Timestamp in.
	 * @param rs
	 *            the Resultset to get the Timestamp from to set in the BaseDB.
	 */
	static protected final void setLastModified(BaseDB bo, ResultSet rs)
			throws Exception {
		bo.setLastModified(getInteger(rs, "last_modified"));
	}

	/**
	 * 
	 * Sets the LastModified Timestamp in the DB object. SetLastModified is
	 * impletemented at the BaseDB level and is a protected method, so that the
	 * lastModified Timestamp just can't be changed by anyone.
	 * 
	 * @param bo
	 *            the object to set the Timestamp in.
	 * @param lastModified
	 *            the Timestamp to set in the BaseDB.
	 */
	static protected final void setLastModified(BaseDB bo, Integer lastModified)
			throws Exception {
		bo.setLastModified(lastModified);
	}

	protected final String SQLizeString(String value) {
		char[] items = value.toCharArray();
		StringBuffer translated = new StringBuffer();

		for (int i = 0; i < items.length; i++) {
			if (items[i] != '\'') {
				translated.append(items[i]);
			} else {
				translated.append("''");
			}
		}

		return translated.toString();
	}

	/**
	 * 
	 * Sets the LastModified Timestamp in a PreparedStatement for insertion into
	 * DB. The lastmodified must appear twice in the SQL update statement, first
	 * in the "set" section and then in the join:
	 * 
	 * UPDATE someTable SET ... ,last_modified = ? WHERE ... AND last_modifed =
	 * ?
	 * 
	 * This will take care of data currency issues during updates, by insuring
	 * that the last modified column matches.
	 * 
	 * @param ps
	 *            the PreparedStatement to set the Timestamp in. @ return the
	 *            value of last_modified
	 */
	static protected final int setLastModified(PreparedStatement ps,
			int setColumnNum, int joinColumnNum, Integer currentLastModified)
			throws Exception {

		ps.setInt(joinColumnNum, currentLastModified++);

		ps.setInt(setColumnNum, currentLastModified);

		return currentLastModified;
	}

	/**
	 * Returns the current <tt>Transaction</tt> in use by this DAO. If needed,
	 * creates a <tt>Transaction</tt> for use by this DAO.
	 * 
	 * @return A valid, usable <tt>Transaction</tt>.
	 */
	protected final Transaction getTransaction() {
		return transaction;
	}

	protected final void commit(Connection conn) throws DAOException {
		// If we aren't in a transaction then commit it.
		if ((transaction == null)) {
			if (conn != null) {
//				try {
//					conn.commit();
//				} catch (SQLException sqle) {
//					logger.error(sqle.getMessage(), sqle);
//                    throw new DAOException(sqle.getMessage(), sqle);
//				}
			}
		}
	}

	protected final String loadSQL(String path) {
//		String ret = daoFactory.retrieveSQL(path);
		String ret = sqlLoader.find(path, null);
		String replacmentStr = "";

		replacmentStr = getSchemaQualifer();

		ret = ret.replace("%Schema%", replacmentStr);

		sqlStatement = ret;
		logger.debug("SQL = " + ret);

		return ret;
	}

	protected final String replaceSchema(String path) {
		String ret;
		String replacmentStr = "";

		if (getSchemaQualifer() != null) {
			replacmentStr = getSchemaQualifer();
		}

		ret = path.replace("%Schema%", replacmentStr);

		return ret;
	}

	static final Connection getConnectionFromConnManager(boolean readOnly)
			throws DAOException {
		return null;
	}

	public class ConnectionHandler {
		private Connection conn;
		private PreparedStatement pStmt;
		private DataSource dataSource;

		/**
		 * Add parameters to an internal List so they can be logged along with
		 * any error message when there is an exception.
		 * 
		 * @param position
		 * @param value
		 */
		private void addParameterToErrorLog(int position, String value) {
			if (position == 1 || parameterList == null) {
				parameterList = new ArrayList<String>();
			}
			logger.debug("parameter " + position + " = " + value);
			parameterList.add("Parameter " + position + " = " + value);
		}
		
		public ConnectionHandler() {
			super();
		}

		public ConnectionHandler(boolean readOnly) throws DAOException {
//			if (readOnly) {
//				conn = getReadOnlyConnection();
//			} else {
				conn = getConnection();
//			}
		}

		public ConnectionHandler(String sqlString, boolean readOnly)
					throws DAOException {
			try {
				conn = getConnection();
				pStmt = conn.prepareStatement(loadSQL(sqlString),
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
			} catch (SQLException sqlE) {
				logger.error(sqlE.getMessage(), sqlE);
				throw new DAOException("ConnectionHandler failed", sqlE);
			}
		}
		
		public ConnectionHandler(Connection conn, PreparedStatement pStmt) {
			this.conn = conn;
			this.pStmt = pStmt;
		}
		
		public ConnectionHandler(Connection conn, PreparedStatement pStmt, 
				DataSource dataSource) {
			this(conn,pStmt);
			this.dataSource = dataSource;
		}
		
		public final void setSQLString(String sqlString) throws DAOException {
			try {
				String loadedSQLString = loadSQL(sqlString);
				pStmt = conn.prepareStatement(loadedSQLString,
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				sqlStatement = loadedSQLString;
				logger.debug("SQL = " + loadedSQLString);
			} catch (SQLException sqlE) {
				logger.error(sqlE.getMessage(), sqlE);
				throw new DAOException("ConnectionHandler failed", sqlE);
			}
		}

		public final void setSQLStringRaw(String sqlString) throws DAOException {
			try {
				pStmt = conn.prepareStatement(sqlString,
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				sqlStatement = sqlString;
				logger.debug("SQL = " + sqlString);
			} catch (SQLException sqlE) {
				logger.error(sqlE.getMessage(), sqlE);
				throw new DAOException("ConnectionHandler failed", sqlE);
			}
		}

		public final <T extends BaseDB> BaseDBObject retrieve(
				Class<T> returnType) throws DAOException {
			BaseDBObject ret = null;

			if ((conn != null) && (pStmt != null)) {
				try {
					ResultSet rs = pStmt.executeQuery();

					if (rs.next()) {
						ret = returnType.newInstance();

						ret.populate(rs);
					}
				} catch (SQLException e) {
					handleException(e, conn);
				} catch (InstantiationException ie) {
					handleException(ie, conn);
				} catch (IllegalAccessException iae) {
					handleException(iae, conn);
				} finally {
					closeStatement(pStmt);
					handleClosing(conn);
				}
			}

			return ret;
		}

		public final <T extends BaseDB> BaseDBObject retrieve(
				Class<T> returnType, int option) throws DAOException {
			BaseDBObject ret = null;

			if ((conn != null) && (pStmt != null)) {
				try {
					ResultSet rs = pStmt.executeQuery();

					if (rs.next()) {
						ret = returnType.newInstance();

						ret.populate(rs, option);
					}
				} catch (SQLException e) {
					handleException(e, conn);
				} catch (InstantiationException ie) {
					handleException(ie, conn);
				} catch (IllegalAccessException iae) {
					handleException(iae, conn);
				} finally {
					closeStatement(pStmt);
					handleClosing(conn,dataSource);
				}
			}

			return ret;
		}

		public final boolean update() throws DAOException {
			return update(true);
		}

		public final boolean updateNoCheck() throws DAOException {
			return remove();
		}

		public final boolean updateNoClose() throws DAOException {
			return update(false);
		}

		public final void close() throws DAOException {
			if (pStmt != null) {
				closeStatement(pStmt);
			}

			if (conn != null) {
				handleClosing(conn,dataSource);
			}
		}

		private boolean update(boolean closeConnection) throws DAOException {
			boolean modified = false;

			if ((conn != null) && (pStmt != null)) {
				try {
					logger.debug("Executing SQL statement.");
					executeUpdate(pStmt);
					modified = true;
				} catch (Exception e) {
					handleException(e, conn);
				} finally {
					if (closeConnection) {
						logger.debug("Closing SQL statement.");
						closeStatement(pStmt);
						handleClosing(conn,dataSource);
					}
				}
            } else {
                throw new DAOException("update() did nothing");
			}
			return modified;
		}

		/**
		 * This method does not check last modified before removing the rows.
		 * This method should only be used in cases where multiple rows are
		 * being deleted and it is impractical to retrieve and check every
		 * last_modified.
		 * 
		 * @param sqlString
		 * @param parms
		 * @return
		 * @throws DAOException
		 */
		public final boolean remove() throws DAOException {
			boolean modified = false;
			if ((conn != null) && (pStmt != null)) {
				try {
					pStmt.execute();
					modified = true;
				} catch (Exception e) {
					handleException(e, conn);
				} finally {
					closeStatement(pStmt);
					handleClosing(conn,dataSource);
				}
            } else {
                throw new DAOException("remove() did nothing");
			}

			return modified;
		}

		public final <T extends BaseDB> ArrayList<T> retrieveArray(
				Class<T> returnType, int limit) throws DAOException {
			return retrieveArrayInternal(returnType, limit, null);
		}

		public final <T extends BaseDB> ArrayList<T> retrieveArray(
				Class<T> returnType) throws DAOException {
			return retrieveArrayInternal(returnType, -1, null);
		}

		public final <T extends BaseDB> ArrayList<T> retrieveArray(
				Class<T> returnType, int limit, int option) throws DAOException {
			return retrieveArrayInternal(returnType, limit, option);
		}

		private <T extends BaseDB> ArrayList<T> retrieveArrayInternal(
				Class<T> returnType, int maxHits, Integer option)
				throws DAOException {

			int i = 0;
			int j = 0;

			ArrayList<T> ret = new ArrayList<T>();

			//StopWatch connTimer = new StopWatch();
			//StopWatch popTimer = new StopWatch();
			//connTimer.start();

			try {
				ResultSet rs = null;

				if (maxHits > 0) {
					pStmt.setMaxRows(maxHits);
				}

				pStmt.setFetchSize(100);
				rs = pStmt.executeQuery();

				//connTimer.stop();
				//logger.debug("Query done in " + connTimer.elapsedTime() + "ms");
				//connTimer.start();
				T tempObj;

				if (maxHits < 0) {
					while (rs.next()) {

						tempObj = returnType.newInstance();

						if (option != null) {
							tempObj.populate(rs, option);
						} else {
							tempObj.populate(rs);
						}

						ret.add(tempObj);
						i++;

						if (i == defaultHitIncrementCheck) {
							long totalMem = Runtime.getRuntime().totalMemory();
							long freeMem = Runtime.getRuntime().freeMemory();
							double usedMem = (((double) totalMem) - ((double) freeMem))
									/ totalMem;
							if (usedMem > defaultUsedMemoryLimit) {
								ret = null;
								throw new OutOfMemoryError();
							}
							ret.ensureCapacity((j * defaultHitIncrementCheck)
									+ defaultHitIncrementCheck);
							i = 0;
							j++;
						}
					}
				} else {
					//popTimer.start();
					while (rs.next() && i < maxHits) {
						tempObj = returnType.newInstance();

						if (option != null) {
							tempObj.populate(rs, option);
						} else {
							tempObj.populate(rs);
						}

						ret.add(tempObj);
						i++;

						if (i == defaultHitIncrementCheck) {
							long totalMem = Runtime.getRuntime().totalMemory();
							long freeMem = Runtime.getRuntime().freeMemory();
							double usedMem = (((double) totalMem) - ((double) freeMem))
									/ totalMem;
							if (usedMem > defaultUsedMemoryLimit) {
								ret = null;
								throw new OutOfMemoryError();
							}
							ret.ensureCapacity((j * defaultHitIncrementCheck)
									+ defaultHitIncrementCheck);
							i = 0;
							j++;
						}
					}
					//popTimer.stop();
				}
				//connTimer.stop();
				//logger.debug(((j * defaultHitIncrementCheck) + i) + " "
				//		+ returnType.getName() + " objects populated in "
				//		+ popTimer.elapsedTime() + "ms");
				//logger.debug("Retrieve array done " + connTimer.elapsedTime()
				//		+ "ms");
			} catch (OutOfMemoryError oome) {
				ret = null;
				System.gc();
				logger.error("The system is short of memory: Retrieved "
						+ ((j * defaultHitIncrementCheck) + i) + " "
						+ returnType.getName() + " objects.");
				OutOfMemoryDAOException oomE = new OutOfMemoryDAOException(
						"The system is short of memory");
				handleException(oomE, conn);
			} catch (Exception e) {
				ret = null;
				System.gc();
				handleException(e, conn);
			} finally {
				closeStatement(pStmt);
				handleClosing(conn,dataSource);
			}

			if (ret != null) {
				ret.trimToSize();
			}

			return ret;
		}

		public final <T extends Object> Object retrieveJavaObject(
				Class<T> returnType) throws DAOException {
			Object ret = null;

			try {
				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret = populateObject(rs, 1, returnType);
				}
			} catch (Exception e) {
				handleException(e, conn);
			} finally {
				closeStatement(pStmt);
				handleClosing(conn,dataSource);
			}

			return ret;
		}

		@SuppressWarnings("unchecked")
		public final <K extends Object,V extends Object> Map<K,V> retrieveMap(
				Class<K> keyType, Class<V> valueType) throws DAOException {
			Map<K,V> ret = new HashMap<K,V>();

			try {
				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					K key = null;
					V value = null;
					if (keyType.getName().equals("java.lang.Integer")) {
						Integer keyInt = rs.getInt(1);
						key = (K) keyInt;
					} else {
						throw new RuntimeException("keyType not supported: " + keyType);
					}
					if (valueType.getName().equals("java.sql.Timestamp")) {
						value = (V) rs.getTimestamp(2);
					} else if (valueType.getName().equals("java.lang.String")) {
						value = (V) rs.getString(2);
					} else {
						throw new RuntimeException("valueType not supported: " + valueType);						
					}
					ret.put(key, value);
				}
			} catch (Exception e) {
				handleException(e, conn);
			} finally {
				closeStatement(pStmt);
				handleClosing(conn,dataSource);
			}

			return ret;
		}

		public final <T extends Object> ArrayList<T> retrieveJavaObjectArray(
				Class<T> returnType) throws DAOException {
			ArrayList<T> ret = new ArrayList<T>();

			try {
				ResultSet rs = pStmt.executeQuery();

				while (rs.next()) {
					ret.add(populateObject(rs, 1, returnType));
				}
			} catch (Exception e) {
				handleException(e, conn);
			} finally {
				closeStatement(pStmt);
				handleClosing(conn,dataSource);
			}

			return ret;
		}

		public final void setString(int position, String value)
				throws DAOException {
			if (pStmt != null) {
				String v = trimLeadingTrailingSpace(value);
				try {
					pStmt.setString(position, v);
					addParameterToErrorLog(position, v);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					throw new DAOException(e.getMessage());
				}
			}
		}

		public final void setString(int position, Object value)
				throws DAOException {
			if (pStmt != null) {
				try {
					if (value == null) {
						pStmt.setNull(position, java.sql.Types.VARCHAR);
						addParameterToErrorLog(position, "null");
					} else {
						String v = trimLeadingTrailingSpace(value.toString());
						pStmt.setString(position, v);
						addParameterToErrorLog(position, v);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					throw new DAOException(e.getMessage());
				}
			}
		}

		public final void setTimestamp(int position, Timestamp value)
				throws DAOException {
			if (pStmt != null) {
				try {
					pStmt.setTimestamp(position, value);
					if (value != null) {
						addParameterToErrorLog(position, value.toString());
					} else {
						addParameterToErrorLog(position, "null");
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					throw new DAOException(e.getMessage());
				}
			}
		}

		public final void setDate(int position, Date value) throws DAOException {
			if (pStmt != null) {
				try {
					pStmt.setDate(position, value);
					if (value != null) {
						addParameterToErrorLog(position, value.toString());
					} else {
						addParameterToErrorLog(position, "null");
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					throw new DAOException(e.getMessage());
				}
			}
		}

		public final void setInteger(int position, Integer value)
				throws DAOException {
			if (pStmt != null) {
				try {
					if (value != null) {
						pStmt.setInt(position, value);
						addParameterToErrorLog(position, value.toString());
					} else {
						pStmt.setNull(position, java.sql.Types.INTEGER);
						addParameterToErrorLog(position, "null");
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					throw new DAOException(e.getMessage());
				}
			}
		}

		public final void setLong(int position, Long value)
				throws DAOException {
			if (pStmt != null) {
				try {
					if (value != null) {
						pStmt.setLong(position, value);
						addParameterToErrorLog(position, value.toString());
					} else {
						pStmt.setNull(position, java.sql.Types.BIGINT);
						addParameterToErrorLog(position, "null");
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					throw new DAOException(e.getMessage());
				}
			}
		}

		public final void setFloat(int position, Float value)
				throws DAOException {
			if (pStmt != null) {
				try {
					if (value != null) {
						pStmt.setFloat(position, value.floatValue());
						addParameterToErrorLog(position, value.toString());
					} else {
						pStmt.setNull(position, java.sql.Types.FLOAT);
						addParameterToErrorLog(position, "null");
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					throw new DAOException(e.getMessage());
				}
			}
		}
		
		public final void setBigDecimal(int position, BigDecimal value)
				throws DAOException {
			if (pStmt != null) {
				try {
					if (value != null) {
						pStmt.setBigDecimal(position, value);
						addParameterToErrorLog(position, value.toString());
					} else {
						pStmt.setNull(position, java.sql.Types.NUMERIC);
						addParameterToErrorLog(position, "null");
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					throw new DAOException(e.getMessage());
				}
			}
		}

		public final void setDouble(int position, Double value)
				throws DAOException {
			if (pStmt != null) {
				try {
					if (value != null) {
						pStmt.setDouble(position, value.doubleValue());
						addParameterToErrorLog(position, value.toString());
					} else {
						pStmt.setNull(position, java.sql.Types.DOUBLE);
						addParameterToErrorLog(position, "null");
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					throw new DAOException(e.getMessage());
				}
			}
		}

		public final void setBoolean(int position, Boolean value)
				throws DAOException {
			if (pStmt != null) {
				try {
					if (value != null) {
						pStmt.setBoolean(position, value.booleanValue());
						addParameterToErrorLog(position, value.toString());
					} else {
						pStmt.setNull(position, java.sql.Types.BOOLEAN);
						addParameterToErrorLog(position, "null");
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					throw new DAOException(e.getMessage());
				}
			}
		}
		
		public final void setShort(int position, Short value)
				throws DAOException {
			if (pStmt != null) {
				try {
					if (value != null) {
						pStmt.setShort(position, value);
						addParameterToErrorLog(position, value.toString());
					} else {
						pStmt.setNull(position, java.sql.Types.SMALLINT);
						addParameterToErrorLog(position, "null");
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					throw new DAOException(e.getMessage());
				}
			}
		}

		@SuppressWarnings("unchecked")
		private <T extends Object> T populateObject(ResultSet rs, int position,
				Class<T> returnType) {
			T ret = null;

			if ((returnType != null) && (rs != null)) {
				try {
					String className = returnType.getName();
					if (className.equals("java.lang.Integer")) {
						ret = (T) getInteger(rs, position);
					} else if (className.equals("java.lang.String")) {
						ret = (T) rs.getString(position);
					} else if (className.equals("java.sql.Timestamp")) {
						ret = (T) rs.getTimestamp(position);
					} else if (className.equals("java.lang.Float")) {
						ret = (T) getFloat(rs,position);
					} else if (className.equals("java.lang.Double")) {
						ret = (T) getDouble(rs,position);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}

			return ret;
		}

		public void addBatch() throws DAOException {
			if (null != pStmt) {
				try {
					pStmt.addBatch();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					throw new DAOException(e.getMessage());
				}
			} else {
				throw new DAOException("attempt to add batch on null statement");
			}
		}

		public int[] executeBatchUpdate() throws DAOException {
			int[] updateCounts = new int[0];
			if ((conn != null) && (pStmt != null)) {
				try {
					logger.debug("Executing SQL batch update.");
					updateCounts = pStmt.executeBatch();
				} catch (Exception e) {
					handleException(e, conn);
				} finally {
					closeStatement(pStmt);
					handleClosing(conn,dataSource);
				}
            } else {
                throw new DAOException("attempt to execute batch update on null connection and/or statement");
			}
			for (int count : updateCounts) {
				if (count == 0) {
					throw new DataStoreConcurrencyException(
							"Data out of date. Please refresh data, and try again");
				}
			}
			return updateCounts;
		}

		public int[] executeBatchRemove() throws DAOException {
			int[] updateCounts = new int[0];
			if ((conn != null) && (pStmt != null)) {
				try {
					logger.debug("Executing SQL batch remove.");
					updateCounts = pStmt.executeBatch();
				} catch (Exception e) {
					handleException(e, conn);
				} finally {
					closeStatement(pStmt);
					handleClosing(conn,dataSource);
				}
            } else {
                throw new DAOException("attempt to execute batch remove on null connection and/or statement");
			}
			return updateCounts;
		}
	}

	
	/**
	 * For the entered sequence, returns the next value. Increments the last
	 * used number by 1. If the sequence was not found or an error occurred,
	 * returns "null".
	 * 
	 * @param sequence
	 *            - Name of desired sequence
	 * 
	 * @return Integer The next value of the sequence.
	 */
	public Integer nextSequenceVal(String sequence)
			throws DAOException {
		return this.nextSequenceVal(sequence, 1);
	}

	/**
	 * For the entered sequence, returns the next value. Increments the last
	 * used number by 1. If the sequence was not found or an error occurred,
	 * returns "null".
	 * 
	 * @param sequence
	 *            - Name of desired sequence
	 * 
	 * @return Integer The next value of the sequence.
	 */
	protected final Integer nextSequenceVal(String sequence, Integer Id)
			throws DAOException {
		
		//TODO this method does not seem to be doing much?
		
		Integer ret = Id;
		if (Id == null) {
			ret = this.nextSequenceVal(sequence, 1);
		}

		return ret;
	}

	public Integer retrieve(String column) throws DAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuffer sql = null;
		int company_id = 0;

		try {
			// get connection
			conn = getReadOnlyConnection();

			sql = new StringBuffer();
			sql.append("SELECT company_id FROM CM_COMPANY WHERE cmp_id=?");

			// prepare
			stmt = conn.prepareStatement(sql.toString());

			stmt.setString(1, column);

			// execute
			rs = stmt.executeQuery();

			if (rs.next()) {
				company_id = getInteger(rs, 1);
			}
		} catch (SQLException sqle) {
			logger.error("Exception caught in company_id", sqle);
		} finally {
			handleClosing(conn);
		}

		return company_id;
	}

	/**
	 * For the entered sequence, returns the next value. Increments the last
	 * used number using incrementBy. This allows you to get a block of numbers
	 * for batch processing and not have to call nextVal (String Sequence) for
	 * every row.  If the sequence is not found or an error occurs, an
	 * exception is thrown.
	 * <p>
	 * <b>IMPORTANT NOTE:</b> This method has the REQUIRES_NEW transaction
	 * propagation type.  The transaction does not participate in any
	 * transaction that may be open by a caller (e.g. will not be included in a
	 * rollback, etc.).
	 * 
	 * @param sequence
	 *            - Name of desired sequence
	 * @param incrementBy
	 *            - Value that you want the sequence last used number increased
	 *            by.
	 * 
	 * @return Integer the next value of the sequence.
	 */
	@Transactional(
			propagation=Propagation.REQUIRES_NEW
			)
	public Integer nextSequenceVal(String sequence, int incrementBy)
			throws DAOException {
		Integer result = null;
		
		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			ImpactPortalClient impactPortalClient = new ImpactPortalClient();
			result = impactPortalClient.getSequence(sequence);
		} else {
			int sequenceNum = 0;
			ResultSet resultSet = null;

			Connection conn = null;
			PreparedStatement psSelect = null;
			PreparedStatement psUpdate = null;

			try {
				conn = getConnection();
				psSelect = conn.prepareStatement("SELECT last_used_num "
						+ "  FROM " + addSchemaToTable("CM_Sequence_Def")
						+ " WHERE sequence_nm = ?");
				psUpdate = conn.prepareStatement("UPDATE "
						+ addSchemaToTable("CM_Sequence_Def")
						+ "   SET last_used_num = ? "
						+ " WHERE sequence_nm = ?");

				// We have a database Connection. See if we can find this
				// sequence name in the database. If not, throw an Exception.
				// If we do find it, lock it for update.

				synchronized(AbstractDAO.nextSequenceValMethodLock) {
					int updated = 0;
					psSelect.setString(1, sequence);
					resultSet = psSelect.executeQuery();
	
					if (!resultSet.next()) {
						throw new DAOException("Sequence " + sequence
	                            + " was not found.", "Sequence " + sequence
								+ " was not found.");
					}
	
					// Get the current sequence value from the database. This
					// represents the last value used. Increment it by the count
					// and update it to the database. Also, return the incremented
					// value to the user.
	
					sequenceNum = resultSet.getInt(1);
					sequenceNum = sequenceNum + incrementBy;
					
					psUpdate.setInt(1, sequenceNum);
					psUpdate.setString(2, sequence);
					updated = psUpdate.executeUpdate();
	
					// If we can't update the database with the new value, we are
					// in trouble.
	
					if (updated <= 0) {
						throw new DAOException("Sequence " + sequence + " was not "
								+ "updated.");
					}
	
					result = new Integer(sequenceNum);
					resultSet.close();
				}
			} catch (Exception e) {
				handleException(e, conn);
			} finally {
				closeStatement(psUpdate);
				closeStatement(psSelect);
				handleClosing(conn);
			}
		}
		return result;
	}

	/**
	 * 
	 * Example: formatQuery(status, "activity_status_cd", pa.isInStatus())
	 * return: (activity_status_cd = 'A') OR (activity_status_cd = 'S')
	 * 
	 * @param list
	 * @param pattern
	 * @param isIn
	 * @return
	 */
	protected final StringBuffer formatQuery(ArrayList<String> list,
			String pattern, boolean isIn) {
		StringBuffer rString = new StringBuffer();
		if (list.size() > 0) {
			rString.append(" (");
		}

		for (int i = 0; i < list.size(); i++) {
			rString.append(pattern);
			if (isIn) {
				rString.append(" = '");
			} else {
				rString.append(" != '");
			}
			rString.append(list.get(i));
			rString.append("'");
			if (i + 1 == list.size()) {
				rString.append(") ");
			} else if (isIn) {
				rString.append(" OR ");
			} else {
				rString.append(" AND ");
			}
		}
		return rString;
	}

	protected final Timestamp formatEndOfDay(Timestamp date) {
		return Utility.formatEndOfDay(date);
	}

	protected final java.util.Date formatEndOfDay(java.util.Date date) {
		return Utility.formatEndOfDay(date);
	}

	protected final Timestamp formatBeginOfDay(Timestamp date) {
		return Utility.formatBeginOfDay(date);
	}

	protected final java.util.Date formatBeginOfDay(java.util.Date date) {
		return Utility.formatBeginOfDay(date);
	}

	public final String addSchemaToTable(String table) {
		String ret = table;

		if (getSchemaQualifer() != null) {
			ret = getSchemaQualifer() + table;
		}
		return ret;
	}

	public final String convertCommaDelimited(String[] permitTypes) {

		// convert the String array into a comma-delimited string list
		String temp = "";
		for (int i = 0; i < permitTypes.length; i++) {
			if (i == 0) {
				temp = "'" + permitTypes[i] + "'";
			} else {
				temp = temp + "," + "'" + permitTypes[i] + "'";
			}
		}
		return temp;
	}
    
    public final String convertAfsIdToString(Integer id) {
        String negSign = "";
        int v = id;
        if(id < 0) {
            negSign = "-";
            v = -id;
        }
        String digits = Integer.toString(v);
        String result = negSign + "00000".substring(digits.length()) + digits;
        return result;
    }
    
    public final String formatFacilityId (String facilityId)
    {
    	if (!Utility.isNullOrEmpty(facilityId)) {
			String format = "F%06d";
			facilityId = facilityId.trim();

			int tempId;
			try {
				tempId = Integer.parseInt(facilityId);
				facilityId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}
    	return facilityId;
    }
    
    public final String formatId(String prefix, String id) {
    	return formatId(prefix, "%06d", id);
    }
    
    public static final String formatId(String prefix, String digitFormat, String id) {
    	if (!Utility.isNullOrEmpty(id) && !Utility.isNullOrEmpty(prefix)) {
			String format = prefix + digitFormat;
			id = id.trim();
			int tempId;
			try {
				tempId = Integer.parseInt(id);
				id = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}
    	return id;
    }
    
   public class AQDSConnectionHandler {
    	
    	private ConnectionHandler aqdsConnection;
    	private Connection connection;
    	private PreparedStatement pStatement;
    	
    	public AQDSConnectionHandler(String sqlString) throws DAOException {
    		super();
    		try {
    			connection = getAQDSConnection();
    			pStatement = connection.prepareStatement(loadSQL(sqlString), 
    														ResultSet.TYPE_SCROLL_INSENSITIVE,
    														ResultSet.CONCUR_READ_ONLY);
    			aqdsConnection = new ConnectionHandler(connection, pStatement, aqdsDataSource);

    		} catch (SQLException sqlE) {
    			logger.error(sqlE.getMessage(), sqlE);
    			throw new DAOException("AQDS ConnectionHandler failed", sqlE);
    		}
    	}
    	
    	public AQDSConnectionHandler() throws DAOException {
    		super();
    		connection = getAQDSConnection();
    		aqdsConnection = new ConnectionHandler(connection, pStatement, aqdsDataSource);
    	}

		public ConnectionHandler getAqdsConnection() {
			return aqdsConnection;
		}

		public void setAqdsConnection(ConnectionHandler aqdsConnection) {
			this.aqdsConnection = aqdsConnection;
		}

    }
    
}
