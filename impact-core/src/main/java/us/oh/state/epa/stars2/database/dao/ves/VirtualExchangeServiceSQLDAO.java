package us.oh.state.epa.stars2.database.dao.ves;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.VirtualExchangeServiceDAO;
import us.oh.state.epa.stars2.database.dbObjects.ves.TransferLogEntry;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class VirtualExchangeServiceSQLDAO extends AbstractDAO implements VirtualExchangeServiceDAO {

	private static final String VES_SERVER = (String)Config.getEnvEntry("app/vesServer", "VES_SERVER_NOT_FOUND");
	
	private static final String VES_EIS_DB =  (String)Config.getEnvEntry("app/vesEisDb", "VES_EIS_DB_NOT_FOUND");
	
	private static final String VES_FACID_DB =  (String)Config.getEnvEntry("app/vesFacIdDb", "VES_FACID_DB_NOT_FOUND"); 
	
	@Override
	public void removeCersEntries() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersEntries");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	private void executeRemove(String sql) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(sql);
		
		if (!connHandler.remove()) {
			throw new DAOException("not modified.");
		}
	}

	@Override
	public Integer countCersEntries() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersEntries");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		return executeCount(sql);
	}
	
	@Override
	public void createCersEntry(String cersId, String dataCategory, 
			String username, Integer reportingYear, Timestamp endDate,
			String partnerId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createCersEntry"));
		connHandler.setSQLStringRaw(String.format(SQLizeString(statementSQL.toString()), VES_SERVER, VES_EIS_DB));
		
		connHandler.setString(1,cersId);
		connHandler.setString(2,dataCategory);
		connHandler.setString(3,username);
		connHandler.setString(4,partnerId);
		connHandler.setInteger(5,reportingYear);
		connHandler.setTimestamp(6,endDate);
		
		
		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}
	}

	@Override
	public void removeFacSites() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeFacSites");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}
	
	@Override
	public Integer countFacSites() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countFacSites");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		return executeCount(sql);
	}

	@Override
	public void createFacSites(String cers_id) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createFacSites"));
		connHandler.setSQLStringRaw(String.format(SQLizeString(statementSQL.toString()), VES_SERVER, VES_EIS_DB));

		connHandler.setString(1,cers_id);
		
		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		} 
	}
	
	@Override
	public void removeFacilityViewVars() throws DAOException {
		removeFacilityViewPermitClassCdVars();
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeFacilityViewVars");
		executeRemove(sql);
	}

	@Override
	public void removeFacilityViewPermitClassCdVars() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeFacilityViewPermitClassCdVars");
		executeRemove(sql);
	}

	@Override
	public void createFacilityViewVars(Integer reportingYear, String partnerId,String[] permitClassCds) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createFacilityViewVars"));
		connHandler.setSQLStringRaw(SQLizeString(statementSQL.toString()));
		
		connHandler.setInteger(1,reportingYear);
		connHandler.setString(2,partnerId);

		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}

		createFacilityViewPermitClassCdVars(permitClassCds);
	}
	
//TODO: 
	public void createFacilityViewVars(Integer reportingYear, String partnerId,String[] permitClassCds,int cersId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createFacilityViewVars"));
		connHandler.setSQLStringRaw(SQLizeString(statementSQL.toString()));
		
		connHandler.setInteger(1,reportingYear);
		connHandler.setString(2,partnerId);

		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}

		createFacilityViewPermitClassCdVars(permitClassCds);
	}

	@Override
	public void createFacilityViewPermitClassCdVars(String[] permitClassCds) throws DAOException {
		for (String permitClassCd : permitClassCds) {
			ConnectionHandler connHandler = new ConnectionHandler(true);
			StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createFacilityViewPermitClassCdVar"));
			connHandler.setSQLStringRaw(SQLizeString(statementSQL.toString()));
			
			connHandler.setString(1,permitClassCd);

			if (!connHandler.update()) {
				throw new DAOException("not modified.");
			}
		}
	}

	@Override
	public void removeFacIdens() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeFacIdens");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeFacNaics() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeFacNaics");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeFacSiteAddrs() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeFacSiteAddrs");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeCersEmisUnits() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersEmisUnits");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeCersEmisUnitIdens() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersEmisUnitIdens");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeCersEmisUnitProcs() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersEmisUnitProcs");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeCersEmisUnitProcIdens() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersEmisUnitProcIdens");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeCersEmsUntPrcCtrApcCtMss() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersEmsUntPrcCtrApcCtMss");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeCersEmsUntPrcCtrApcCtPls() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersEmsUntPrcCtrApcCtPls");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeCersEmsUntPrcRlPtApprs() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersEmsUntPrcRlPtApprs");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeCersEmsUntPrcRlPtAppIdns() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersEmsUntPrcRlPtAppIdns");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeCersRelPts() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersRelPts");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeCersRelPtIdens() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersRelPtIdens");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeCersAffls() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersAffls");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeCersAfflOrgs() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersAfflOrgs");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeCersAfflOrgIdens() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersAfflOrgIdens");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeCersAfflOrgAddrs() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersAfflOrgAddrs");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeCersAfflOrgCommuns() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersAfflOrgCommuns");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeCersEmisUnitProcRptPrds() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersEmisUnitProcRptPrds");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public void removeCersEmsUntPrcRptPrdEms() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeCersEmsUntPrcRptPrdEms");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		executeRemove(sql);
	}

	@Override
	public Integer countCersEmsUntPrcRptPrdEms() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersEmsUntPrcRptPrdEms");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countCersEmisUnitProcRptPrds() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersEmisUnitProcRptPrds");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countCersAfflOrgCommuns() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersAfflOrgCommuns");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countCersAfflOrgAddrs() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersAfflOrgAddrs");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countCersAfflOrgIdens() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersAfflOrgIdens");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countCersAfflOrgs() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersAfflOrgs");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countCersAffls() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersAffls");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countCersRelPtIdens() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersRelPtIdens");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countCersRelPts() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersRelPts");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countCersEmsUntPrcRlPtAppIdns() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersEmsUntPrcRlPtAppIdns");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countCersEmsUntPrcRlPtApprs() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersEmsUntPrcRlPtApprs");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countCersEmsUntPrcCtrApcCtPls() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersEmsUntPrcCtrApcCtPls");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countCersEmsUntPrcCtrApcCtMss() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersEmsUntPrcCtrApcCtMss");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countCersEmisUnitProcIdens() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersEmisUnitProcIdens");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countCersEmisUnitProcs() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersEmisUnitProcs");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countCersEmisUnitIdens() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersEmisUnitIdens");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countCersEmisUnits() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countCersEmisUnits");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countFacSiteAddrs() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countFacSiteAddrs");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		
		return executeCount(sql);
	}

	@Override
	public Integer countFacNaics() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countFacNaics");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		return executeCount(sql);
	}

	@Override
	public Integer countFacIdens() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countFacIdens");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_EIS_DB);
		
		return executeCount(sql);
	}

	private Integer executeCount(String sql) throws DAOException {
		Integer ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {

			conn = getReadOnlyConnection();
			pStmt = conn.prepareStatement(sql);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new Integer(AbstractDAO.getInteger(rs, "row_count"));
			}
			rs.close();
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	@Override
	public void createFacIdens() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createFacIdens");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	private void executeCreate(String sql) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(sql);
		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}
	}

	@Override
	public void createFacNaics() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createFacNaics");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createFacSiteAddrs() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createFacSiteAddrs");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createCersEmisUnits() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createCersEmisUnits");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createCersEmisUnitIdens() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createCersEmisUnitIdens");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createCersEmisUnitProcs() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createCersEmisUnitProcs");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createCersEmisUnitProcIdens() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createCersEmisUnitProcIdens");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createCersEmsUntPrcCtrApcCtMss() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createCersEmsUntPrcCtrApcCtMss");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createCersEmsUntPrcCtrApcCtPls() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createCersEmsUntPrcCtrApcCtPls");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createCersEmsUntPrcRptPrdEms() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createCersEmsUntPrcRptPrdEms");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createCersEmisUnitProcRptPrds() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createCersEmisUnitProcRptPrds");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createCersAfflOrgCommuns() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createCersAfflOrgCommuns");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createCersAfflOrgAddrs() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createCersAfflOrgAddrs");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createCersEmsUntPrcRlPtApprs() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createCersEmsUntPrcRlPtApprs");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createCersEmsUntPrcRlPtAppIdns() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createCersEmsUntPrcRlPtAppIdns");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createCersRelPts() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createCersRelPts");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createCersRelPtIdens() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createCersRelPtIdens");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createCersAffls() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createCersAffls");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createCersAfflOrgs() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createCersAfflOrgs");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void createCersAfflOrgIdens() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.createCersAfflOrgIdens");
		sql = String.format(sql, VES_SERVER, VES_EIS_DB);
		
		executeCreate(sql);
	}

	@Override
	public void removeEuProcRptPeriodViewVars() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeEuProcRptPeriodViewVars");
		executeRemove(sql);
	}

	@Override
	public void createEuProcRptPeriodViewVars(Timestamp startDate, Timestamp endDate) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createEuProcRptPeriodViewVars"));
		connHandler.setSQLStringRaw(SQLizeString(statementSQL.toString()));
		
		connHandler.setTimestamp(1,startDate);
		connHandler.setTimestamp(2,endDate);

		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}
	}
	
	@Override
	public void removeFacEmisRprtViewVars() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeFacEmisRprtViewVars");
		executeRemove(sql);		
	}
	
	@Override
	public void createFacEmisRprtViewVars(Integer reportingYear) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createFacEmisRprtViewVars"));
		connHandler.setSQLStringRaw(SQLizeString(statementSQL.toString()));
		
		connHandler.setInteger(1,reportingYear);

		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}
	}

	@Override
	public TransferLogEntry createTransferLogEntry(TransferLogEntry logEntry) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createTransferLogEntry"));
		connHandler.setSQLStringRaw(SQLizeString(statementSQL.toString()));
		
		int i = 1;
		logEntry.setId(nextSequenceVal("S_VS_TRANSFER_LOG_ID").longValue());
		connHandler.setLong(i++, logEntry.getId());
		connHandler.setString(i++,logEntry.getType());
		connHandler.setInteger(i++,logEntry.getReportingYear());
		connHandler.setString(i++,logEntry.getFacilityTypes());
		connHandler.setString(i++,logEntry.getUsername());
		connHandler.setString(i++,logEntry.getStatus());
		connHandler.setString(i++,logEntry.getDomain());
		connHandler.setTimestamp(i++,logEntry.getStartDate());

		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}
		logEntry.setLastModified(1);
		return logEntry;
	}

	@Override
	public void updateTransferLogEntry(TransferLogEntry logEntry) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.updateTransferLogEntry"));
		connHandler.setSQLStringRaw(SQLizeString(statementSQL.toString()));
		
		int i = 1;
		connHandler.setLong(i++, logEntry.getId());
		connHandler.setString(i++,logEntry.getType());
		connHandler.setInteger(i++,logEntry.getReportingYear());
		connHandler.setString(i++,logEntry.getFacilityTypes());
		connHandler.setString(i++,logEntry.getUsername());
		connHandler.setString(i++,logEntry.getStatus());
		connHandler.setString(i++,logEntry.getMessage());
		connHandler.setTimestamp(i++,logEntry.getStartDate());
		connHandler.setTimestamp(i++,logEntry.getEndDate());
		connHandler.setLong(i++,logEntry.getDuration());
		connHandler.setInteger(i++,logEntry.getProgressPercent());
		connHandler.setString(i++,logEntry.getDomain());
		connHandler.setInteger(i++,logEntry.getLastModified() + 1);

		//where clause vars
		connHandler.setLong(i++, logEntry.getId());
		connHandler.setInteger(i++,logEntry.getLastModified());
		
		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}
		logEntry.setLastModified(logEntry.getLastModified() + 1);
	}

	@Override
	public List<TransferLogEntry> retrieveTransferLogEntries() throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"VirtualExchangeServiceEisSQL.retrieveTransferLogEntries", false);
		List<TransferLogEntry> logEntries = connHandler.retrieveArray(TransferLogEntry.class);
		return logEntries;
	}

	@Override
	public TransferLogEntry retrieveTransferLogEntry(long id) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"VirtualExchangeServiceEisSQL.retrieveTransferLogEntry", false);
		connHandler.setLong(1, id);
		TransferLogEntry logEntry = (TransferLogEntry)connHandler.retrieve(TransferLogEntry.class);
		return logEntry;
	}

	@Override
	public List<TransferLogEntry> retrievePendingTransferLogEntries() throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"VirtualExchangeServiceEisSQL.retrievePendingTransferLogEntries", false);
		List<TransferLogEntry> logEntries = connHandler.retrieveArray(TransferLogEntry.class);
		return logEntries;
	}
	
	@Override
	public void removeFacDtlsEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeFacIdFacDtlsEntry");
		sql = String.format(sql, VES_SERVER, VES_FACID_DB);
		executeRemove(sql);		
	}
	
	@Override
	public Integer countFacDtlsEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countFacIdFacDtlsEntry");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_FACID_DB);
		
		return executeCount(sql);
	}

	@Override
	public void createFacIdFacDtlsEntry() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createFacIdFacDtlsEntry"));
		connHandler.setSQLStringRaw(String.format(SQLizeString(statementSQL.toString()), VES_SERVER, VES_FACID_DB));
		
		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}
	}

	@Override
	public void removeFacIdFacEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeFacIdFacEntry");
		sql = String.format(sql, VES_SERVER, VES_FACID_DB);
		executeRemove(sql);		
		
	}

	@Override
	public Integer countFacIdFacEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countFacIdFacEntry");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_FACID_DB);
		
		return executeCount(sql);
	}

	@Override
	public void createFacIdFacEntry() throws DAOException { //TODO: Review this
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createFacIdFacEntry"));
		connHandler.setSQLStringRaw(String.format(statementSQL.toString(), VES_SERVER, VES_FACID_DB)); 
		
		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}
		
	}

	
	@Override
	public void removeFacIdEnvrIntrEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeFacIdEnvrIntrEntry");
		sql = String.format(sql, VES_SERVER, VES_FACID_DB);
		executeRemove(sql);		
	}
	
	@Override
	public Integer countFacIdEnvrIntrEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countFacIdEnvrIntrEntry");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_FACID_DB);
		
		return executeCount(sql);
	}

	@Override
	public void createFacIdEnvrIntrEntry(String partnerId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createFacIdEnvrIntrEntry"));
//		connHandler.setSQLStringRaw(String.format(statementSQL.toString(), VES_EIS_SERVER, VES_EIS_DB,partnerId,VES_EIS_SERVER,VES_FACID_DB)); 
		connHandler.setSQLStringRaw(String.format(statementSQL.toString(), VES_SERVER, VES_FACID_DB,VES_SERVER,VES_FACID_DB)); 
		
		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}
	}

	@Override
	public void removeGeoLocdescEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeGeoLocdescEntry");
		sql = String.format(sql, VES_SERVER, VES_FACID_DB);
		executeRemove(sql);		
	}
	
	@Override
	public Integer countGeoLocdescEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countGeoLocdescEntry");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_FACID_DB);
		
		return executeCount(sql);
	}

	@Override
	public void createGeoLocdescEntry(String partnerId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createGeoLocdescEntry"));
		connHandler.setSQLStringRaw(String.format(statementSQL.toString(), VES_SERVER, VES_FACID_DB,VES_SERVER,VES_FACID_DB));
		
		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}
	}

	@Override
	public void removeFacSicEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeFacSicEntry");
		sql = String.format(sql, VES_SERVER, VES_FACID_DB);
		executeRemove(sql);		
	}
	
	@Override
	public Integer countFacSicEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countFacSicEntry");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_FACID_DB);
		
		return executeCount(sql);
	}

	@Override
	public void createFacSicEntry() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createFacSicEntry"));
		connHandler.setSQLStringRaw(String.format(statementSQL.toString(), VES_SERVER, VES_FACID_DB,VES_SERVER,VES_FACID_DB));

		
		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}
	}

	
	@Override
	public void removeFacNaicsEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeFacNaicsEntry");
		sql = String.format(sql, VES_SERVER, VES_FACID_DB);
		executeRemove(sql);		
	}
	
	@Override
	public Integer countFacNaicsEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countFacNaicsEntry");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_FACID_DB);
		
		return executeCount(sql);
	}

	@Override
	public void createFacIdFacNaicsEntry() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createFacIdFacNaicsEntry"));
		connHandler.setSQLStringRaw(String.format(statementSQL.toString(), VES_SERVER, VES_FACID_DB,VES_SERVER,VES_FACID_DB));
		
		
		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}
	}

	@Override
	public void removeFacAfflEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeFacAfflEntry");
		sql = String.format(sql, VES_SERVER, VES_FACID_DB);
		executeRemove(sql);		
	}
	
	@Override
	public Integer countFacAfflEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countFacAfflEntry");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_FACID_DB);
		
		return executeCount(sql);
	}

	@Override
	public void createFacAfflEntry() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createFacAfflEntry"));
		connHandler.setSQLStringRaw(String.format(SQLizeString(statementSQL.toString()), VES_SERVER, VES_FACID_DB));

		
		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}
	}

	@Override
	public void removeFacIdAfflEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeFacIdAfflEntry");
		sql = String.format(sql, VES_SERVER, VES_FACID_DB);
		executeRemove(sql);		
	}
	
	@Override
	public Integer countFacIdAfflEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countFacIdAfflEntry");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_FACID_DB);
		
		return executeCount(sql);
	}

	@Override
	public void createFacIdAfflEntry() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createFacIdAfflEntry"));
		connHandler.setSQLStringRaw(String.format(SQLizeString(statementSQL.toString()), VES_SERVER, VES_FACID_DB));

		
		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}
	}

	@Override
	public void removeFacIdTelephonicEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeFacIdTelephonicEntry");
		sql = String.format(sql, VES_SERVER, VES_FACID_DB);
		executeRemove(sql);		
	}
	
	@Override
	public Integer countFacIdTelephonicEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countFacIdTelephonicEntry");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_FACID_DB);
		
		return executeCount(sql);
	}

	@Override
	public void createFacIdTelephonicEntry() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createFacIdTelephonicEntry"));
		connHandler.setSQLStringRaw(String.format(statementSQL.toString(), VES_SERVER, VES_FACID_DB));

		
		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}
	}

	@Override
	public void removeFacIdAfflElecAddrEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.removeFacIdAfflElecAddrEntry");
		sql = String.format(sql, VES_SERVER, VES_FACID_DB);
		executeRemove(sql);		
	}
	
	@Override
	public Integer countFacIdAfflElecAddrEntry() throws DAOException {
		String sql = loadSQL("VirtualExchangeServiceEisSQL.countFacIdAfflElecAddrEntry");
		sql = String.format(sqlStatement.toString(), VES_SERVER, VES_FACID_DB);
		
		return executeCount(sql);
	}

	@Override
	public void createFacIdAfflElecAddrEntry() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = new StringBuffer(loadSQL("VirtualExchangeServiceEisSQL.createFacIdAfflElecAddrEntry"));
		connHandler.setSQLStringRaw(String.format(statementSQL.toString(), VES_SERVER, VES_FACID_DB));

		
		if (!connHandler.update()) {
			throw new DAOException("not modified.");
		}
	}


	
}
