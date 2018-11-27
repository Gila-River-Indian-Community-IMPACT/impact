package us.oh.state.epa.stars2.database.dao;

import java.sql.Connection;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.framework.exception.DAOException;

/**
 * Title: Transaction.
 * 
 * <p>
 * Description: Transactions are used in conjunction with DAOs to handle groups
 * of database actions. In general, DAOs look at database transactions as
 * stand-alone, atomic actions. However, classes that use DAOs may need to look
 * at individual actions as part of a group of actions, i.e., "do action A, then
 * do action B, then do action C". If any one of these actions fails, then none
 * of them should be persisted (i.e., do a "rollback").
 * </p>
 * 
 * <p>
 * Transactions should be used as follows:
 * <ul>
 * <li>First, use the <tt>TransactionFactory</tt> to create a
 * <tt>Transaction</tt>.</li>
 * <li>Next, use the <tt>DAOFactory</tt> to create individual DAOs. Each DAO
 * must implement the <tt>TransactableDAO</tt> interface to allow for external
 * transaction control.</li>
 * <li>Call the <tt>setTransaction()</tt> method on each DAO to provide the the
 * DAO with the transaction contoller obtained earlier. The DAO knows how to
 * handle external transaction control.</li>
 * <li>Use the DAOs to perform your operations. When finished, call the
 * <tt>complete()</tt> method on the <tt>Transaction</tt> object to post those
 * changes. If you want to cancel (or rollback) those changes, call the
 * <tt>cancel()</tt> on the <tt>Transaction</tt> object. If needed, the same
 * <tt>Transaction</tt> can be us.oh.state.epa.stars2.frameworkd multiple times
 * to perform operations.</li>
 * <li>When done with the <tt>Transaction</tt>, call <tt>close()</tt> to release
 * resources owned by the transaction.</li>
 * </ul>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 * @version 1.0
 */
@Deprecated
public class Transaction {
	private transient Logger logger;
	private int myId; // Unique Transaction ID
	private Connection conn; // JDBC Connection

	/**
	 * Constructor. Takes an integer ID and a JDBC <tt>Connection</tt>.
	 * 
	 * @param id
	 *            integer ID
	 * @param conn
	 *            JDBC Connection
	 */
	Transaction(int id) {
		logger = Logger.getLogger(getClass());
		myId = id;
		conn = null;
	}

	/**
	 * Returns the integer ID for this object. The integer ID serves only as a
	 * unique identifier for each <tt>Transaction</tt>.
	 * 
	 * @return integer ID
	 */
	public final int getId() {
		return myId;
	}

	/**
	 * Access the JDBC Connection embedded in this object. Unfortunately, the
	 * <tt>Connection</tt> must be exposed so that DAOs can create SQL
	 * statements, etc. The alternative is to attempt to wrap up the
	 * <tt>Connection</tt> interface, but it is too big for that to be
	 * practical.
	 * 
	 * @return JDBC connection.
	 */
	final Connection getConnection(boolean readOnly) throws DAOException {
		Connection ret = null;
//
//		if (conn == null) {
//			conn = AbstractDAO.getConnectionFromConnManager(readOnly);
//			try {
//				conn.setAutoCommit(false);
//			} catch (SQLException e) {
//				throw new DAOException(e.getMessage());
//			}
//		}
//		ret = conn;
//
		return ret;
	}

	final void closeConnection(Connection conn) {
		//logger.warn("NOOP called");

	}

	/**
	 * Completes a transaction. This posts database actions to the database. If
	 * it fails, an exception will be logged and will be thrown to caller.
	 * 
	 * @throws DAOException
	 *             Invalid connection or commit failed.
	 */
	public final void complete() throws DAOException {
//		if (!validConnection()) {
//			logger.error("Invalid JDBC Connection.");
//			throw new DAOException("Invalid JDBC Connection.", (Throwable) null);
//		}
//
//		if (validConnection()) {
//			try {
//				logger.debug("Committing the transaction.");
//				conn.commit();
//			} catch (SQLException e) {
//				logger.error(e.getMessage(), e);
//				throw new DAOException("Commit failed.", e);
//			}
//		}
//		logger.warn("NOOP called");
	}

	/**
	 * Cancels a transaction. This does a rollback on all pending database
	 * actions. Returns <tt>true</tt> if successful, and <tt>false</tt> if it
	 * fails. If it fails, an exception will be logged.
	 * 
	 * @return <tt>true</tt> if successful, <tt>false</tt> if it fails.
	 */

	public final boolean cancel() {
//		boolean ret = false;
//
//		logger.debug("Cancelling transaction");
//
//		if (validConnection()) {
//			try {
//				logger.debug("Rolling back the transaction.");
//				conn.rollback();
//				ret = true;
//			} catch (SQLException e) {
//				logger.error(e.getMessage(), e);
//			}
//		}
//
//		return ret;
//		logger.warn("NOOP called");
		return true;
	}

	/**
	 * Close this transaction. Releases any system resources associated with
	 * this transaction and invalidates this transaction for further use.
	 */
	public final void close() {
//		TransactionFactory.releaseTransaction(this);
//
//		if (conn != null) {
//			try {
//				logger.debug("Closing database connection");
//				conn.close();
//			} catch (SQLException ex) {
//				logger.error(ex.getMessage(), ex);
//			}
//
//			ConnectionManager connMgr = null;
//			try {
//				connMgr = (ConnectionManager) CompMgr
//						.newInstance(ConnectionManager.J2EE);
//			} catch (UnableToStartException e) {
//				throw new RuntimeException(e);
//			}
//
//			connMgr.removeConnFromConnList(conn);
//		}
//
//		conn = null;
//		myId = 0;
//		logger.warn("NOOP called");
	}

	/**
	 * Returns <tt>true</tt> if this transaction has a valid connection. Returns
	 * <tt>false</tt> if this transaction has been closed already.
	 * 
	 * @return <tt>true</tt> if the connection is valid, <tt>false</tt>
	 *         otherwise.
	 */
	public final boolean validConnection() {
		boolean ret = false;
		ret = (conn != null);
		return ret;
	}

	private void closeConnectionFromConnManager(Connection conn) {
//		try {
//			conn.close();
//		} catch (SQLException ex) {
//			logger.error(ex.getMessage(), ex);
//		}
//		logger.warn("NOOP called");
	}

}
