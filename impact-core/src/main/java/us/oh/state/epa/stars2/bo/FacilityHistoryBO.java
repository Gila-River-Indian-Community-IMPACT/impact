package us.oh.state.epa.stars2.bo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.CompEnfFacilityDAO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FacilityComplianceStatus;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FacilityHistory;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.PollutantCompCode;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.FieldAuditLog;
import us.oh.state.epa.stars2.def.AirProgramDef;
import us.oh.state.epa.stars2.def.ComplianceStatusDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Transactional(rollbackFor=Exception.class)
@Service
public class FacilityHistoryBO extends BaseBO implements FacilityHistoryService {

	/**
	 * 
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public FacilityHistory retrieveFacilityHistory(Integer histId)
			throws DAOException {
		if (histId == null)
			return null;
		CompEnfFacilityDAO compEnfFacilityDAO = compEnfFacilityDAO();
		FacilityHistory fh = compEnfFacilityDAO.retrieveFacilityHistory(histId);
		if (fh == null)
			return null;
		if (fh.isNeshaps()) {
			List<FacilityComplianceStatus> csList = compEnfFacilityDAO
					.retrieveFacHistNeshaps(fh.getFacilityHistId());
			fh.setNeshapsCompStatusList(csList);
		}
		if (fh.isNsps()) {
			List<FacilityComplianceStatus> csList = compEnfFacilityDAO
					.retrieveFacHistNsps(fh.getFacilityHistId());
			fh.setNspsCompStatusList(csList);
		}
		if (fh.isNsrNonAttainment()) {
			List<FacilityComplianceStatus> csList = compEnfFacilityDAO
					.retrieveFacHistNsr(fh.getFacilityHistId());
			fh.setNsrNonAttainmentCompStatusList(csList);
		}
		if (fh.isPsd()) {
			List<FacilityComplianceStatus> csList = compEnfFacilityDAO
					.retrieveFacHistPsd(fh.getFacilityHistId());
			fh.setPsdCompStatusList(csList);
		}
		return fh;
	}

	/**
	 * 
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	@Override
	public FacilityHistory createFacilityHistory(FacilityHistory fh, Transaction trans)
			throws DAOException {
		FacilityHistory ret = null;

			CompEnfFacilityDAO compEnfFacilityDAO = compEnfFacilityDAO(trans);
			ret = compEnfFacilityDAO.createFacilityHistory(fh);
			compEnfFacilityDAO.deleteFacHistNeshaps(fh.getFacilityHistId());
			if (fh.isNeshaps()) {
				for (FacilityComplianceStatus cs : fh
						.getNeshapsCompStatusList()) {
					cs.setFacilityHistoryId(ret.getFacilityHistId());
					compEnfFacilityDAO.createFacHistNeshaps(cs);
				}
			}
			compEnfFacilityDAO.deleteFacHistNsps(fh.getFacilityHistId());
			if (fh.isNsps()) {
				for (FacilityComplianceStatus cs : fh.getNspsCompStatusList()) {
					cs.setFacilityHistoryId(ret.getFacilityHistId());
					compEnfFacilityDAO.createFacHistNsps(cs);
				}
			}
			compEnfFacilityDAO.deleteFacHistNsr(fh.getFacilityHistId());
			if (fh.isNsrNonAttainment()) {
				for (FacilityComplianceStatus cs : fh
						.getNsrNonAttainmentCompStatusList()) {
					cs.setFacilityHistoryId(ret.getFacilityHistId());
					compEnfFacilityDAO.createFacHistNsr(cs);
				}
			}
			compEnfFacilityDAO.deleteFacHistPsd(fh.getFacilityHistId());
			if (fh.isPsd()) {
				for (FacilityComplianceStatus cs : fh.getPsdCompStatusList()) {
					cs.setFacilityHistoryId(ret.getFacilityHistId());
					compEnfFacilityDAO.createFacHistPsd(cs);
				}
			}
		return ret;
	}

	/**
	 * 
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public FacilityHistory createFacilityHistory(Integer fpId, Transaction trans)
			throws DAOException {
		FacilityHistory ret = null;
		FacilityDAO facilityDAO = facilityDAO(trans);
		Facility fac = facilityDAO.retrieveFacility(fpId);

		ret = createNewFacilityHistory(fac);
		Timestamp nTime = new Timestamp(System.currentTimeMillis());
		ret.setStartDate(nTime);
		ret = createFacilityHistory(ret, trans);
		
		return ret;
	}

	private FacilityHistory createNewFacilityHistory(Facility fac) {
		FacilityHistory ret = new FacilityHistory();
		if (fac.getAirProgramCompCd() != null) {
			ret.setAirProgramCompCd(fac.getAirProgramCompCd());
		} else {
			ret.setAirProgramCompCd("");
		}
		ret.setMact(fac.isMact());
		ret.setMactCompCd(fac.getMactCompCd());
		ret.setNeshaps(fac.isNeshaps());
		ret.setNsps(fac.isNsps());
		ret.setNsrNonAttainment(fac.isNsrNonattainment());
		ret.setPsd(fac.isPsd());
		ret.setSipCompCd(fac.getSipCompCd());
		ret.setAirProgramCd(fac.getAirProgramCd());
		ret.setAirProgramCompCd(fac.getAirProgramCompCd());

		FacilityComplianceStatus compStat;
		List<FacilityComplianceStatus> neshapsCompStatusList = new ArrayList<FacilityComplianceStatus>();
		;
		if (fac.isNeshaps()) {
			for (PollutantCompCode s : fac.getNeshapsSubpartsCompCds()) {
				compStat = new FacilityComplianceStatus();
				compStat.setPollutantCd(s.getPollutantCd());
				compStat.setComplianceCd(s.getPollutantCompCd());
				neshapsCompStatusList.add(compStat);
			}
			ret.setNeshapsCompStatusList(neshapsCompStatusList);
		}

		return ret;
	}

	/**
	 * 
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void modifyFacilityHistory(FacilityHistory fh) throws DAOException {
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			CompEnfFacilityDAO compEnfFacilityDAO = compEnfFacilityDAO(trans);
			compEnfFacilityDAO.modifyFacilityHistory(fh);
			compEnfFacilityDAO.deleteFacHistNeshaps(fh.getFacilityHistId());
			if (fh.isNeshaps()) {
				for (FacilityComplianceStatus cs : fh
						.getNeshapsCompStatusList()) {
					cs.setFacilityHistoryId(fh.getFacilityHistId());
					compEnfFacilityDAO.createFacHistNeshaps(cs);
				}
			}
			compEnfFacilityDAO.deleteFacHistNsps(fh.getFacilityHistId());
			if (fh.isNsps()) {
				for (FacilityComplianceStatus cs : fh.getNspsCompStatusList()) {
					cs.setFacilityHistoryId(fh.getFacilityHistId());
					compEnfFacilityDAO.createFacHistNsps(cs);
				}
			}
			compEnfFacilityDAO.deleteFacHistNsr(fh.getFacilityHistId());
			if (fh.isNsrNonAttainment()) {
				for (FacilityComplianceStatus cs : fh
						.getNsrNonAttainmentCompStatusList()) {
					cs.setFacilityHistoryId(fh.getFacilityHistId());
					compEnfFacilityDAO.createFacHistNsr(cs);
				}
			}
			compEnfFacilityDAO.deleteFacHistPsd(fh.getFacilityHistId());
			if (fh.isPsd()) {
				for (FacilityComplianceStatus cs : fh.getPsdCompStatusList()) {
					cs.setFacilityHistoryId(fh.getFacilityHistId());
					compEnfFacilityDAO.createFacHistPsd(cs);
				}
			}
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
	}

	/**
	 * 
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void modifyCurrentFacilityHistory(Facility facility, int userId,
			boolean faciltySaveRequired) throws DAOException {
		if (!CompMgr.getAppName().equals(CommonConst.INTERNAL_APP))
			return;

		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		ArrayList<FieldAuditLog> falLogs = new ArrayList<FieldAuditLog>();
		try {
			if (isDifferent(facility.getOldAirProgramCompCd(),
					facility.getAirProgramCompCd())) {
				// FAL
				falLogs.add(new FieldAuditLog("coms", facility.getFacilityId(),
						"Air Program"
								+ AirProgramDef.printableValue(facility
										.getAirProgramCd())
								+ " Compliance Status="
								+ ComplianceStatusDef.printableValue(facility
										.getOldAirProgramCompCd()),
						ComplianceStatusDef.printableValue(facility
								.getAirProgramCompCd())));
			}
			if (isDifferent(facility.getOldSipCompCd(), facility.getSipCompCd())) {
				// FAL
				falLogs.add(new FieldAuditLog("coms", facility.getFacilityId(),
						"SIP Compliance Status="
								+ ComplianceStatusDef.printableValue(facility
										.getOldSipCompCd()),
						ComplianceStatusDef.printableValue(facility
								.getSipCompCd())));

			}
			if (isDifferent(facility.getOldMactCompCd(),
					facility.getMactCompCd())) {
				// FAL
				falLogs.add(new FieldAuditLog("coms", facility.getFacilityId(),
						"MACT Compliance Status="
								+ ComplianceStatusDef.printableValue(facility
										.getOldMactCompCd()),
						ComplianceStatusDef.printableValue(facility
								.getMactCompCd())));
			}
			if (faciltySaveRequired) {
				facilityDAO.modifyFacility(facility);
			}

			// update neshaps Subparts
			facilityDAO.removeFacilityNeshapsSubparts(facility.getFpId());
			if (facility.isNeshaps()) {
				for (PollutantCompCode neshapsSubpart : facility
						.getNeshapsSubpartsCompCds()) {
					neshapsSubpart.setFpId(facility.getFpId());
					if (isDifferent(neshapsSubpart.getOldCompCd(),
							neshapsSubpart.getPollutantCompCd())) {
						// FAL
						falLogs.add(new FieldAuditLog("coms", facility
								.getFacilityId(), "NESHAPS "
								+ neshapsSubpart.getPollutantCd()
								+ " Compliance Status="
								+ ComplianceStatusDef
										.printableValue(neshapsSubpart
												.getOldCompCd()),
								ComplianceStatusDef
										.printableValue(neshapsSubpart
												.getPollutantCompCd())));
					}
					facilityDAO.addFacilityNeshapsSubpart(neshapsSubpart);
				}
			}

			FieldAuditLog[] auditLog = falLogs.toArray(new FieldAuditLog[0]);
			if (userId != -1) {
				infraDAO.createFieldAuditLogs(facility.getFacilityId(),
						facility.getName(), userId, auditLog);
			}
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("facility=" + facility.getFacilityId(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}

	/**
	 * 
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void migrateFacilityHistory(Facility facility, int userId)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);

		try {
			facilityDAO.modifyFacility(facility);

			// update neshaps Subparts
			facilityDAO.removeFacilityNeshapsSubparts(facility.getFpId());
			if (facility.isNeshaps()) {
				for (PollutantCompCode neshapsSubpart : facility
						.getNeshapsSubpartsCompCds()) {
					neshapsSubpart.setFpId(facility.getFpId());
					facilityDAO.addFacilityNeshapsSubpart(neshapsSubpart);
				}
			}

			// update MACT Subparts
			facilityDAO.removeFacilityMACTSubparts(facility.getFpId());
			if (facility.isMact()) {
				for (String mactSubpart : facility.getMactSubparts()) {
					facilityDAO.addFacilityMACTSubpart(facility.getFpId(),
							mactSubpart);
				}
			}

			// update nsps Subparts
			facilityDAO.removeFacilityNSPSSubparts(facility.getFpId());
			if (facility.isNsps()) {
				for (String nspsSubpart : facility.getNspsSubparts()) {
					facilityDAO.addFacilityNSPSSubpart(facility.getFpId(),
							nspsSubpart);
				}
			}
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("facility=" + facility.getFacilityId(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}

	/**
	 * 
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void deleteFacilityHistory(int facilityHistoryId)
			throws DAOException {
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			CompEnfFacilityDAO compEnfFacilityDAO = compEnfFacilityDAO(trans);
			compEnfFacilityDAO.deleteFacHistNeshaps(facilityHistoryId);
			compEnfFacilityDAO.deleteFacHistNsps(facilityHistoryId);
			compEnfFacilityDAO.deleteFacHistNsr(facilityHistoryId);
			compEnfFacilityDAO.deleteFacHistPsd(facilityHistoryId);
			compEnfFacilityDAO.deleteFacilityHistory(facilityHistoryId);
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
	}

}
