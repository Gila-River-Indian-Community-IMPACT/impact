package us.wy.state.deq.impact.database.dao.continuousMonitoring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDBObject;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.wy.state.deq.impact.database.dao.ContinuousMonitorDAO;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitorEqt;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitorNote;

@Repository
public class ContinuousMonitorSQLDAO extends AbstractDAO implements
		ContinuousMonitorDAO {

	protected transient Logger logger = Logger.getLogger(this.getClass());

	public ContinuousMonitor createContinuousMonitor(
			ContinuousMonitor continuousMonitor) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.createContinuousMonitor", false);

		checkNull(continuousMonitor);
		int i = 1;
		
		int id = nextSequenceVal("S_Facility_Continuous_Monitor_Id", continuousMonitor.getContinuousMonitorId());
		
		if(null == continuousMonitor.getCorrMonitorId()) {
			int corrMonitorId = nextSequenceVal("S_Continuous_Monitor_Corr_Mon_Id", continuousMonitor.getCorrMonitorId());
			continuousMonitor.setCorrMonitorId(corrMonitorId);
		}
		
		if(Utility.isNullOrEmpty(continuousMonitor.getMonId())) {
			continuousMonitor.setMonId(retrieveNextMonId(continuousMonitor.getFpId()));
		}
		
		connHandler.setInteger(i++, id);
		connHandler.setString(i++, continuousMonitor.getFpId());
		connHandler.setString(i++, continuousMonitor.getMonitorDetails());
		connHandler.setTimestamp(i++, continuousMonitor.getAddDate());
		connHandler.setString(i++, continuousMonitor.getAddlInfo());
		connHandler.setInteger(i++, continuousMonitor.getCreatorId());
		connHandler.setInteger(i++, continuousMonitor.getCorrMonitorId());
		connHandler.setString(i++, continuousMonitor.getMonId());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		continuousMonitor.setContinuousMonitorId(id);
		continuousMonitor.setLastModified(1);

		return continuousMonitor;
	}

	public void deleteContinuousMonitor(ContinuousMonitor ea)
			throws DAOException {

		checkNull(ea);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.deleteContinuousMonitor", false);

		int i = 1;
		
		connHandler.setInteger(i++, ea.getContinuousMonitorId());

		connHandler.remove();

		return;
	}

	public boolean modifyContinuousMonitor(ContinuousMonitor continuousMonitor)
			throws DAOException {
		checkNull(continuousMonitor);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.modifyContinuousMonitor", false);

		int i = 1;
		connHandler.setString(i++, continuousMonitor.getMonitorDetails());
		connHandler.setString(i++, "modified addl info");

		connHandler.setInteger(i++, continuousMonitor.getLastModified() + 1);

		connHandler.setInteger(i++, continuousMonitor.getContinuousMonitorId());
		connHandler.setInteger(i++, continuousMonitor.getLastModified());

		return connHandler.update();
	}

	public ContinuousMonitor retrieveContinuousMonitor(
			Integer continuousMonitorId) throws DAOException {
		ContinuousMonitor ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("ContinuousMonitorSQL.retrieveContinuousMonitor"));

			pStmt.setInt(1, continuousMonitorId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new ContinuousMonitor();
				ret.populate(rs);
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
	
	/**
	 * Retrieve a mapping of monitor id to a comma separated string listing
	 * the limit ids associated with that monitor
	 * 
	 * @return
	 * @throws DAOException
	 */
	private HashMap<Integer, String> retrieveLimitIdsForMonitors(
			String whereClause) throws DAOException {
		HashMap<Integer, String> result = new HashMap<Integer, String>();

		String statementSQL = loadSQL("ContinuousMonitorSQL.retrieveLimitIdsForMonitors");

		if (whereClause == null || whereClause.trim().length() == 0) {
			statementSQL = statementSQL.replaceAll("WHERE_CLAUSE", "");
		} else {
			statementSQL = statementSQL.replaceAll("WHERE_CLAUSE", whereClause);
		}
		
		//logger.debug("Retrieve limit ids for monitors SQL = " + statementSQL);

		ResultSet resultSet = null;
		Connection conn = null;
		PreparedStatement psSelect = null;
		conn = getConnection();
		try {
			psSelect = conn.prepareStatement(statementSQL);
			resultSet = psSelect.executeQuery();

			while (resultSet.next()) {
				Integer monitorId = getInteger(resultSet, 1);
				String activeLimitList = result.get(monitorId);
				if (activeLimitList == null) {
					activeLimitList = resultSet.getString(2);
				} else {
					activeLimitList = activeLimitList + ", "
							+ resultSet.getString(2);
				}
				result.put(monitorId, activeLimitList);
			}
		} catch (SQLException e) {
			logger.debug("WHERE_CLAUSE: " + whereClause, e);
			handleException(e, conn);
		} finally {
			handleClosing(conn);
		}

		return result;
	}

	public List<ContinuousMonitor> searchContinuousMonitors(Integer fpId, String facilityId,
			String facilityName, String doLaaCd, String countyCd,
			Timestamp addDt, Timestamp endDt, String monId, String cmpId)
			throws DAOException {
		List<ContinuousMonitor> results = new ArrayList<ContinuousMonitor>();
		Connection conn = null;
		PreparedStatement pStmt = null;

		StringBuilder statementSQL = new StringBuilder(
				loadSQL("ContinuousMonitorSQL.searchContinuousMonitors"));
		StringBuilder whereClause = new StringBuilder("");
		String conjunction = "AND ";

		if (facilityName != null && facilityName.trim().length() > 0) {
			whereClause.append(conjunction + "LOWER(facility_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(facilityName.trim().replace("*",
					"%")));
			whereClause.append("')");
			conjunction = "\nAND ";
		}
		
		if (fpId != null) {
			whereClause.append(conjunction + " ff.fp_id = ");
			whereClause.append(fpId.intValue());
			conjunction = "\nAND ";
		}

		if (facilityId != null && facilityId.trim().length() > 0) {
			facilityId = formatFacilityId(facilityId);
			whereClause.append(conjunction + "LOWER(ff.facility_id) LIKE ");
			whereClause.append("LOWER('");
			whereClause
					.append(SQLizeString(facilityId.trim().replace("*", "%")));
			whereClause.append("')");
			conjunction = "\nAND ";
		}

		if (countyCd != null) {
			whereClause.append(conjunction + "ca.county_cd = '");
			whereClause.append(countyCd);
			whereClause.append("'");
			conjunction = "\nAND ";
		}
		if (doLaaCd != null) {
			whereClause.append(conjunction + "ff.do_laa_cd = '");
			whereClause.append(doLaaCd);
			whereClause.append("'");
			conjunction = "\nAND ";
		}

		if (cmpId != null) {
			whereClause.append(conjunction + "ccm.cmp_id = '");
			whereClause.append(cmpId);
			whereClause.append("'");
			conjunction = "\nAND ";
		}

		if (monId != null && monId.length() != 0) {
			monId = formatId("CM", monId);
			whereClause.append(" AND LOWER(cm.monitor_id) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(monId.replace("*", "%")));
			whereClause.append("')");
			conjunction = "\nAND ";
		}

		if (addDt != null || endDt != null) {

			whereClause.append(conjunction);
			whereClause.append("(");
			if (addDt != null) {
				whereClause.append(" cme.install_date ");
				whereClause.append(" >= ? ");
				if (endDt != null) {
					whereClause.append(" AND ");
				}
			}
			if (endDt != null) {
				whereClause.append(" cme.install_date ");
				whereClause.append(" <= ? ");
			}
			whereClause.append(" ) ");
			conjunction = "\nAND ";
		}

		StringBuilder sortBy = new StringBuilder(
				"\nORDER BY cm.monitor_id");
		statementSQL.append(whereClause.toString() + " " + sortBy.toString());

		//logger.debug("statementSQL = " + statementSQL);

		try {
			ContinuousMonitor mon = null;
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(replaceSchema(statementSQL.toString()));
			int i = 1;
			if (addDt != null) {
				pStmt.setTimestamp(i, formatBeginOfDay(addDt));
				i++;
			}
			if (endDt != null) {
				pStmt.setTimestamp(i, formatEndOfDay(endDt));
				i++;
			}
			ResultSet rs = pStmt.executeQuery();

			while (rs.next()) {
				mon = new ContinuousMonitor();
				mon.populate(rs);
				results.add(mon);
			}
			rs.close();
			
			// Populate the activeLimits field.
			HashMap<Integer, String> limitIdMap = retrieveLimitIdsForMonitors(whereClause
					.toString());
			for (ContinuousMonitor res : results) {
				res.setActiveLimits(limitIdMap.get(res.getContinuousMonitorId()));
			}

			// If there is only one active physical monitor, make sure that
			// we display the associated Manufacturer and Model Number.
			// Otherwise, we will display the Manufacturer and Model Number
			// for the most-recently created physical monitor, if it exists,
			// and even if it is not active.
			/*TFS: 5767 Moved this logic to SQL
			for (ContinuousMonitor res : results) {
				List<ContinuousMonitorEqt> activeEqtList = retrieveActiveContinuousMonitorEqtList(res
						.getContinuousMonitorId());
				if (activeEqtList.size() == 1) {
					ContinuousMonitorEqt cme = activeEqtList.get(0);
					res.setCurrentManufacturer(cme.getManufacturerName());
					res.setCurrentModelNumber(cme.getModelNumber());
				}

			}*/
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}
		return results;
	}

	/*
	 * public ContinuousMonitorAttachment
	 * createContinuousMonitorAttachment(ContinuousMonitorAttachment ea) throws
	 * DAOException { checkNull(ea); ConnectionHandler connHandler = new
	 * ConnectionHandler(
	 * "ContinuousMonitorSQL.createContinuousMonitorAttachment", false);
	 * 
	 * int i = 1; connHandler.setInteger(i++, ea.getDocumentID());
	 * connHandler.setInteger(i++, ea.getMonitorId());
	 * connHandler.setString(i++, ea.getDocTypeCd()); connHandler.update();
	 * 
	 * ea.setLastModified(1);
	 * 
	 * return ea;
	 * 
	 * }
	 * 
	 * public List<ContinuousMonitorAttachment>
	 * retrieveContinuousMonitorAttachments( int monitorId) throws DAOException
	 * { ConnectionHandler connHandler = new ConnectionHandler(
	 * "ContinuousMonitorSQL.retrieveContinuousMonitorAttachments", true);
	 * connHandler.setInteger(1, monitorId); List<ContinuousMonitorAttachment>
	 * rtn = connHandler.retrieveArray(ContinuousMonitorAttachment.class); //for
	 * (ContinuousMonitorAttachment a : rtn) { //Integer acpId =
	 * ContinuousMonitorAttachmentTypeDef.isACPType(a.getAttachmentType()) ?
	 * a.getDocumentID() : 0; //a.setTradeSecretDocId(acpId); //} return rtn; }
	 * 
	 * public final boolean
	 * modifyContinuousMonitorAttachment(ContinuousMonitorAttachment doc) throws
	 * DAOException { checkNull(doc); ConnectionHandler connHandler = new
	 * ConnectionHandler(
	 * "ContinuousMonitorSQL.modifyContinuousMonitorAttachment", false);
	 * 
	 * int i = 1; connHandler.setString(i++, doc.getDocTypeCd());
	 * connHandler.setInteger(i++, doc.getRefLastModified() + 1);
	 * connHandler.setInteger(i++, doc.getDocumentID());
	 * connHandler.setInteger(i++, doc.getMonitorId());
	 * connHandler.setInteger(i++, doc.getRefLastModified()); return
	 * connHandler.update(); }
	 * 
	 * public void deleteContinuousMonitorAttachment(ContinuousMonitorAttachment
	 * att) throws DAOException { checkNull(att); checkNull(att.getMonitorId());
	 * checkNull(att.getDocumentID()); ConnectionHandler connHandler = new
	 * ConnectionHandler(
	 * "ContinuousMonitorSQL.deleteContinuousMonitorAttachment", false);
	 * connHandler.setInteger(1, att.getMonitorId()); connHandler.setInteger(2,
	 * att.getDocumentID()); connHandler.remove();
	 * 
	 * }
	 */
	public List<ContinuousMonitorNote> retrieveContinuousMonitorNotes(
			int corrMonitorId) throws DAOException {
		checkNull(corrMonitorId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.retrieveContinuousMonitorNotes", true);
		connHandler.setInteger(1, corrMonitorId);
		return connHandler.retrieveArray(ContinuousMonitorNote.class);
	}

	@Override
	public void addContinuousMonitorNote(int noteId, int corrMonitorId)
			throws DAOException {
		checkNull(noteId);
		checkNull(corrMonitorId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.addContinuousMonitorNote", false);
		connHandler.setInteger(1, noteId);
		connHandler.setInteger(2, corrMonitorId);
		connHandler.update();
	}

	public void deleteContinuousMonitorNotes(int corrMonitorId) throws DAOException {
		checkNull(corrMonitorId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.deleteContinuousMonitorNotes", false);
		connHandler.setInteger(1, corrMonitorId);
		connHandler.remove();

	}

	public final List<ContinuousMonitorEqt> retrieveContinuousMonitorEqtList(
			int continuousMonitorId) throws DAOException {

		checkNull(continuousMonitorId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.retrieveContinuousMonitorEqtList", true);

		connHandler.setInteger(1, continuousMonitorId);

		ArrayList<ContinuousMonitorEqt> ret = new ArrayList<ContinuousMonitorEqt>();
		ArrayList<ContinuousMonitorEqt> base = connHandler
				.retrieveArray(ContinuousMonitorEqt.class);
		for (BaseDBObject bd : base) {
			ret.add((ContinuousMonitorEqt) bd);
		}

		return ret;
	}
	
	public final List<ContinuousMonitorEqt> retrieveContinuousMonitorEqtListNewestFirst(
			int continuousMonitorId) throws DAOException {

		checkNull(continuousMonitorId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.retrieveContinuousMonitorEqtListNewestFirst", true);

		connHandler.setInteger(1, continuousMonitorId);

		ArrayList<ContinuousMonitorEqt> ret = new ArrayList<ContinuousMonitorEqt>();
		ArrayList<ContinuousMonitorEqt> base = connHandler
				.retrieveArray(ContinuousMonitorEqt.class);
		for (BaseDBObject bd : base) {
			ret.add((ContinuousMonitorEqt) bd);
		}

		return ret;
	}
	
	public final List<ContinuousMonitorEqt> retrieveActiveContinuousMonitorEqtList(
			int continuousMonitorId) throws DAOException {

		checkNull(continuousMonitorId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.retrieveActiveContinuousMonitorEqtList", true);

		connHandler.setInteger(1, continuousMonitorId);

		ArrayList<ContinuousMonitorEqt> ret = new ArrayList<ContinuousMonitorEqt>();
		ArrayList<ContinuousMonitorEqt> base = connHandler
				.retrieveArray(ContinuousMonitorEqt.class);
		for (BaseDBObject bd : base) {
			ret.add((ContinuousMonitorEqt) bd);
		}

		return ret;
	}

	public final void removeContinuousMonitorEqtList(int continuousMonitorId)
			throws DAOException {

		checkNull(continuousMonitorId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.removeContinuousMonitorEqtList", false);

		connHandler.setInteger(1, continuousMonitorId);
		connHandler.remove();
	}

	public final ContinuousMonitorEqt createContinuousMonitorEqt(
			ContinuousMonitorEqt cme) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.createContinuousMonitorEqt", false);

		checkNull(cme);
		int i = 1;

		int id = nextSequenceVal("S_Facility_Continuous_Monitor_Eqt_Id");
		connHandler.setInteger(i++, id);
		connHandler.setInteger(i++, cme.getContinuousMonitorId());
		connHandler.setString(i++, cme.getManufacturerName());
		connHandler.setString(i++, cme.getModelNumber());
		connHandler.setString(i++, cme.getSerialNumber());
		connHandler.setTimestamp(i++, cme.getQAQCSubmittedDate());
		connHandler.setTimestamp(i++, cme.getQAQCAcceptedDate());
		connHandler.setTimestamp(i++, cme.getInstallDate());
		connHandler.setTimestamp(i++, cme.getRemovalDate());

		connHandler.setString(i++, cme.getAddlInfo());
		connHandler.setInteger(i++, cme.getAddedBy());

		connHandler.update();

		cme.setMonitorEqtId(id);
		cme.setLastModified(1);

		return cme;

	}

	public final void modifyContinuousMonitorEqt(ContinuousMonitorEqt cme)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.modifyContinuousMonitorEqt", false);

		checkNull(cme);
		int i = 1;
		connHandler.setInteger(i++, cme.getMonitorEqtId());
		connHandler.setString(i++, cme.getContinuousMonitorId());

		connHandler.setString(i++, cme.getManufacturerName());
		connHandler.setString(i++, cme.getModelNumber());
		connHandler.setString(i++, cme.getSerialNumber());
		connHandler.setTimestamp(i++, cme.getQAQCSubmittedDate());
		connHandler.setTimestamp(i++, cme.getQAQCAcceptedDate());
		connHandler.setTimestamp(i++, cme.getInstallDate());
		connHandler.setTimestamp(i++, cme.getRemovalDate());
		connHandler.setString(i++, cme.getAddlInfo());
		connHandler.setInteger(i++, cme.getAddedBy());

		connHandler.setInteger(i++, cme.getLastModified() + 1);

		connHandler.setInteger(i++, cme.getMonitorEqtId());
		connHandler.setInteger(i++, cme.getContinuousMonitorId());
		connHandler.setInteger(i++, cme.getLastModified());

		connHandler.update();
	}

	public final void removeContinuousMonitorEqt(ContinuousMonitorEqt cme)
			throws DAOException {

		checkNull(cme);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.removeContinuousMonitorEqt", false);

		int i = 1;
		connHandler.setInteger(i++, cme.getMonitorEqtId());
		connHandler.setInteger(i++, cme.getContinuousMonitorId());

		connHandler.remove();
	}

	public final ContinuousMonitor[] retrieveContinuousMonitorByFpId(
			Integer fpId) throws DAOException {
		checkNull(fpId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.retrieveContinuousMonitorByFpId",
				true);

		connHandler.setString(1, fpId);

		List<ContinuousMonitor> ret = connHandler
				.retrieveArray(ContinuousMonitor.class);

		return ret.toArray(new ContinuousMonitor[0]);
	}
	
	@Override
	public boolean createAssociatedFpEuIdRef(Integer monitorId, Integer emuId)
			throws DAOException {
		checkNull(monitorId);
		checkNull(emuId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.createAssociatedFpEuIdRef", true);
		
		int i = 1;
		connHandler.setInteger(i++, monitorId);
		connHandler.setInteger(i++, emuId);
		
		return connHandler.update();
		
	}
	
	@Override
	public List<Integer> retrieveAssociatedFpEuIdsByMonitorId(Integer monitorId)
		throws DAOException {
		checkNull(monitorId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.retrieveAssociatedFpEuIdsByMonitorId", true);
		
		connHandler.setInteger(1, monitorId);
		
		return connHandler.retrieveJavaObjectArray(Integer.class);
	}
	
	@Override
	public void deleteAssociatedFpEuIdRef(Integer monitorId, Integer emuId)
		throws DAOException {
		checkNull(monitorId);
		checkNull(emuId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.deleteAssociatedFpEuIdRef", true);
		
		int i = 1;
		connHandler.setInteger(i++, monitorId);
		connHandler.setInteger(i++, emuId);
		
		connHandler.remove();
	
	}
	
	@Override
	public void deleteAssociatedFpEuIdsByMonitorId(Integer monitorId)
		throws DAOException {
		checkNull(monitorId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.deleteAssociatedFpEuIdsByMonitorId", true);
		
		connHandler.setInteger(1, monitorId);
	
		connHandler.remove();
	
	}

	@Override
	public boolean createAssociatedFpEgressPointIdRef(Integer monitorId,
			Integer fpNodeId) throws DAOException {
		checkNull(monitorId);
		checkNull(fpNodeId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.createAssociatedFpEgressPointIdRef", true);
		
		int i = 1;
		connHandler.setInteger(i++, monitorId);
		connHandler.setInteger(i++, fpNodeId);
		
		return connHandler.update();
	}

	@Override
	public List<Integer> retrieveAssociatedFpEgressPointIdsByMonitorId(
			Integer monitorId) throws DAOException {
		checkNull(monitorId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.retrieveAssociatedFpEgressPointIdsByMonitorId", true);
		
		connHandler.setInteger(1, monitorId);
		
		return connHandler.retrieveJavaObjectArray(Integer.class);	
	}

	@Override
	public void deleteAssociatedFpEgressPointIdRef(Integer monitorId,
			Integer fpNodeId) throws DAOException {
		checkNull(monitorId);
		checkNull(fpNodeId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.deleteAssociatedFpEgressPointIdRef", true);
		
		int i = 1;
		connHandler.setInteger(i++, monitorId);
		connHandler.setInteger(i++, fpNodeId);
		
		connHandler.remove();
		
	}

	@Override
	public void deleteAssociatedFpEgressPointIdsByMonitorId(Integer monitorId)
			throws DAOException {
		checkNull(monitorId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.deleteAssociatedFpEgressPointIdsByMonitorId", true);
		
		connHandler.setInteger(1, monitorId);
	
		connHandler.remove();
		
	}
	
	@Override
	public List<String> retrieveAssociatedFpEuEpaEmuIdsByMonitorId(Integer monitorId)
		throws DAOException {
		checkNull(monitorId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.retrieveAssociatedFpEuEpaEmuIdsByMonitorId", true);
		
		connHandler.setInteger(1, monitorId);
		
		return connHandler.retrieveJavaObjectArray(String.class);
	}

	@Override
	public List<String> retrieveAssociatedFpReleasePointIdsByMonitorId(
			Integer monitorId) throws DAOException {
		checkNull(monitorId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.retrieveAssociatedFpReleasePointIdsByMonitorId", true);
		
		connHandler.setInteger(1, monitorId);
		
		return connHandler.retrieveJavaObjectArray(String.class);
	}

	@Override
	public ContinuousMonitorEqt createContinuousMonitorEqtWithCreatedDt(
			ContinuousMonitorEqt cme) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.createContinuousMonitorEqtWithCreatedDt", false);

		checkNull(cme);
		int i = 1;

		int id = nextSequenceVal("S_Facility_Continuous_Monitor_Eqt_Id", cme.getMonitorEqtId());
		connHandler.setInteger(i++, id);
		connHandler.setInteger(i++, cme.getContinuousMonitorId());
		connHandler.setString(i++, cme.getManufacturerName());
		connHandler.setString(i++, cme.getModelNumber());
		connHandler.setString(i++, cme.getSerialNumber());
		connHandler.setTimestamp(i++, cme.getQAQCSubmittedDate());
		connHandler.setTimestamp(i++, cme.getQAQCAcceptedDate());
		connHandler.setTimestamp(i++, cme.getInstallDate());
		connHandler.setTimestamp(i++, cme.getRemovalDate());

		connHandler.setString(i++, cme.getAddlInfo());
		connHandler.setInteger(i++, cme.getAddedBy());
		connHandler.setTimestamp(i++, cme.getCreatedOn());

		connHandler.update();

		cme.setMonitorEqtId(id);
		cme.setLastModified(1);

		return cme;
	}

	@Override
	public String retrieveNextMonId(Integer fpId) throws DAOException {
		String ret = null;
		
		checkNull(fpId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.retrieveNextMonId", true);
		
		connHandler.setInteger(1, fpId);
		
		Integer monId = (Integer)connHandler.retrieveJavaObject(Integer.class);
		
		if(null != monId) {
			ret = "CM" + String.format("%03d", monId);
		}
		
		return ret;
		
	}
	
	@Override
	public ContinuousMonitor retrieveContinuousMonitorByFpIdAndCorrId(Integer fpId, 
			Integer corrMonitorId) throws DAOException {
		checkNull(fpId);
		checkNull(corrMonitorId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.retrieveContinuousMonitorByFpIdAndCorrId", true);
		
		connHandler.setInteger(1, fpId);
		connHandler.setInteger(2, corrMonitorId);
		
		return (ContinuousMonitor)connHandler.retrieve(ContinuousMonitor.class);
		
	}

	/**
	 * Returns an array of facility ids having a matching physical monitor
	 */
	@Override
	public Map<Integer, String> facilitiesWithMatchingContinuousMonitorEqt(String manufacturerName,
			String modelNumber, String serialNumber) throws DAOException {

		checkNull(manufacturerName);
		checkNull(modelNumber);
		checkNull(serialNumber);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.facilitiesWithMatchingContinuousMonitorEqt", true);

		int i = 1;
		connHandler.setString(i++, manufacturerName);
		connHandler.setString(i++, modelNumber);
		connHandler.setString(i++, serialNumber);
		
		return connHandler.retrieveMap(Integer.class, String.class);

	}

	@Override
	public List<Integer> retrieveCorrelatedMonitorIds(Integer corrMonitorId,
			Integer monitorId) throws DAOException {
		checkNull(corrMonitorId);
		checkNull(monitorId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.retrieveCorrelatedMonitorIds", true);

		int i = 1;
		connHandler.setInteger(i++, corrMonitorId);
		connHandler.setInteger(i++, monitorId);
		
		return connHandler.retrieveJavaObjectArray(Integer.class);
	}
	
	
	
}
