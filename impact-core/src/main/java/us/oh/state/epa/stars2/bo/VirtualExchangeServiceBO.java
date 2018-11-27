package us.oh.state.epa.stars2.bo;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.database.dao.VirtualExchangeServiceDAO;
import us.oh.state.epa.stars2.database.dbObjects.ves.TransferLogEntry;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.DataStoreConcurrencyException;
import us.wy.state.deq.impact.app.Status;

@Transactional(rollbackFor = Exception.class)
@Service
public class VirtualExchangeServiceBO extends BaseBO implements VirtualExchangeServiceService {
	
	private static final String PARTNER_ID =  (String)Config.getEnvEntry("app/vesPartnerId", "VES_PARTNER_ID_NOT_FOUND");

	public static class TransferResult {
		
		private int value;
		
		private Integer valueAtFailure;
		
		private long maximum;
		
		private TransferLogEntry logEntry = new TransferLogEntry();

		private Status status;

		public TransferResult() {
			super();
		}

		public TransferResult(String domain) {
			super();
			this.logEntry.setDomain(domain);
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public TransferLogEntry getLogEntry() {
			return logEntry;
		}

		public void setLogEntry(TransferLogEntry logEntry) {
			this.logEntry = logEntry;
		}

		public Status getStatus() {
			return status;
		}

		public void setStatus(Status status) {
			this.status = status;
		}

		public long getMaximum() {
			return maximum;
		}

		public void setMaximum(long maximum) {
			this.maximum = maximum;
		}

		public Integer getValueAtFailure() {
			return valueAtFailure;
		}

		public void setValueAtFailure(Integer valueAtFailure) {
			this.valueAtFailure = valueAtFailure;
		}
		
	}
	
	@Async
	@Transactional(
			propagation=Propagation.NOT_SUPPORTED
			)
	@Override
	public Future<TransferResult> transferEisEiData(TransferResult transferResult, 
			String username, Integer reportingYear, String[] permitClassCds) {
		return doTransfer(transferResult, username, reportingYear, permitClassCds, "EIS-EI");
	}
	
	@Async
	@Transactional(
			propagation=Propagation.NOT_SUPPORTED
			)
	@Override
	public Future<TransferResult> transferEisFacilityData(TransferResult transferResult, 
			String username, Integer reportingYear, String[] permitClassCds) {
		return doTransfer(transferResult, username, reportingYear, permitClassCds, "EIS-Facility");
	}
	
	@Async
	@Transactional(
			propagation=Propagation.NOT_SUPPORTED
			)
	@Override
	public Future<TransferResult> transferFacIdData(TransferResult transferResult, 
			String username) {
		return doTransfer(transferResult, username, null, null, "FACID");
	}


	private Future<TransferResult> doTransfer(TransferResult transferResult, String username, Integer reportingYear,
			String[] permitClassCds, String transferType) {
		AsyncResult<TransferResult> result = new AsyncResult<TransferResult>(transferResult);
		try {
			boolean success = false;
			if ("EIS-EI".equals(transferType)) {
				success = doEisEiTransfer(transferResult, username, reportingYear, permitClassCds);
			} else
			if ("EIS-Facility".equals(transferType)) {
				success = doEisFacilityTransfer(transferResult, username, reportingYear, permitClassCds);
			} else
			if ("FACID".equals(transferType)) {
				success = doFacIdTransfer(transferResult, username);
			}
				
			if (success) {
				transferResult.setStatus(Status.SUCCESS);
				transferResult.getLogEntry().setMessage("Completed successfully.");
			} else {
				return result;
			}
		} catch (Throwable t) {
			transferResult.setStatus(Status.FAILURE);
			transferResult.setValueAtFailure(transferResult.getValue());
			transferResult.getLogEntry().setMessage("Error: " + t.getMessage());
		} finally {
			transferResult.getLogEntry().setEndDate(new Timestamp(new Date().getTime()));
			transferResult.getLogEntry().setDuration(
					transferResult.getLogEntry().getEndDate().getTime() - transferResult.getLogEntry().getStartDate().getTime());
			transferResult.setValue(100);
			try {
				updateTransferLogEntry(transferResult);
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}
	

	private void updateProgress(TransferResult transferResult) throws CanceledTransferException {
		int remaining = (int)(transferResult.getMaximum() - transferResult.getValue());
		int value = (int) Math.round(transferResult.getValue() + (0.1*remaining));
		transferResult.setValue(value);
		try {
			VirtualExchangeServiceDAO virtualExchangeServiceDAO = 
					virtualExchangeServiceDAO();
			TransferLogEntry logEntry = 
					virtualExchangeServiceDAO.retrieveTransferLogEntry(transferResult.getLogEntry().getId());
			if (null != logEntry) {
				if (Status.CANCELED.getValue().equals(logEntry.getStatus())) {
					transferResult.setStatus(Status.CANCELED);
					transferResult.setLogEntry(logEntry);
					transferResult.setValueAtFailure(transferResult.getValue());
					throw new CanceledTransferException();
				}
			} else {
				throw new DAOException("Error occurred while reading transfer log entry");
			}
			updateTransferLogEntry(transferResult);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean doEisEiTransfer(TransferResult transferResult, String username, Integer reportingYear,
			String[] permitClassCds) throws DAOException {

		Calendar reportingCal = Calendar.getInstance();
		reportingCal.set(Calendar.MILLISECOND, 0); // .000

		reportingCal.set(reportingYear, 0, 1, 0, 0, 0); // JAN 01 00:00:00
		Timestamp startDate = new Timestamp(reportingCal.getTimeInMillis());

		reportingCal.set(reportingYear, 11, 31, 0, 0, 0); // DEC 31 00:00:00
		Timestamp endDate = new Timestamp(reportingCal.getTimeInMillis());

		createCersEntry("1", "Point", username, reportingYear, endDate);
		
		createViewVars(reportingYear, permitClassCds, startDate, endDate);
		
		try {
			createFacSites(transferResult);

			createFacIdens(transferResult);

			createFacNaics(transferResult);

			createFacSiteAddrs(transferResult);

			createCersEmisUnits(transferResult);

			createCersEmisUnitIdens(transferResult);

			createCersEmisUnitProcs(transferResult);

			createCersEmisUnitProcIdens(transferResult);

			createCersEmsUntPrcCtrApcCtMss(transferResult);

			createCersEmsUntPrcCtrApcCtPls(transferResult);

			createCersEmsUntPrcRlPtApprs(transferResult);

			createCersEmsUntPrcRlPtAppIdns(transferResult);

			createCersRelPts(transferResult);

			createCersRelPtIdens(transferResult);

			createCersAffls(transferResult);

			createCersAfflOrgs(transferResult);

			createCersAfflOrgIdens(transferResult);

			createCersAfflOrgAddrs(transferResult);

			createCersAfflOrgCommuns(transferResult);

			createCersEmisUnitProcRptPrds(transferResult);

			createCersEmsUntPrcRptPrdEms(transferResult);
		} catch (CanceledTransferException e) {
			return false;
		}
		return true;
	}


	private boolean doEisFacilityTransfer(TransferResult transferResult, String username, Integer reportingYear,
			String[] permitClassCds) throws DAOException {

		Calendar reportingCal = Calendar.getInstance();
		reportingCal.set(Calendar.MILLISECOND, 0); // .000

		reportingCal.set(reportingYear, 0, 1, 0, 0, 0); // JAN 01 00:00:00
		Timestamp startDate = new Timestamp(reportingCal.getTimeInMillis());

		reportingCal.set(reportingYear, 11, 31, 0, 0, 0); // DEC 31 00:00:00
		Timestamp endDate = new Timestamp(reportingCal.getTimeInMillis());

		createCersEntry("0", "FacilityInventory", username, reportingYear, endDate);
		
		createViewVars(reportingYear, permitClassCds, startDate, endDate);
		
		try {
			createFacSites(transferResult);

			createFacIdens(transferResult);

			createFacNaics(transferResult);

			createFacSiteAddrs(transferResult);

			createCersEmisUnits(transferResult);

			createCersEmisUnitIdens(transferResult);

			createCersRelPts(transferResult);

			createCersRelPtIdens(transferResult);

			createCersAffls(transferResult);

			createCersAfflOrgs(transferResult);

			createCersAfflOrgIdens(transferResult);

			createCersAfflOrgAddrs(transferResult);

			createCersAfflOrgCommuns(transferResult);

		} catch (CanceledTransferException e) {
			return false;
		}
		return true;
	}
	
	private boolean doFacIdTransfer(TransferResult transferResult, String username) throws DAOException {

		try {
			createFacIdFacDtlsEntry(transferResult);
			
			createFacIdFacEntry(transferResult);
			
			createFacIdEnvrIntrEntry(transferResult);

			createGeoLocdescEntry(transferResult);

			createFacSicEntry(transferResult);
			
			createFacNaicsEntry(transferResult);
			
			createFacAfflEntry(transferResult);
			
			createFacIdAfflEntry(transferResult);
			
			createFacIdTelephonicEntry(transferResult);

			createFacIdAfflElecAddrEntry(transferResult);
		} catch (CanceledTransferException e) {
			return false;
		}
		return true;
	}
	
	private void createFacIdAfflElecAddrEntry(TransferResult transferResult)
			throws DAOException, CanceledTransferException {
		//FACID_AFFL_ELEC_ADDR
		
		virtualExchangeServiceDAO().removeFacIdAfflElecAddrEntry();
		
		verifyRemoved(virtualExchangeServiceDAO().countFacIdAfflElecAddrEntry());
		
		try {
			virtualExchangeServiceDAO().createFacIdAfflElecAddrEntry();
		} catch (DataStoreConcurrencyException e) {
//		DisplayUtil.displayWarning("No facilities were found that match the given criteria.");
		}

		Integer facIdAfflElecAddrEntryCount = virtualExchangeServiceDAO().countFacIdAfflElecAddrEntry();

		if (null == facIdAfflElecAddrEntryCount) {
			throw new DAOException("create failed; facIdTelephonicEntryCount = " + facIdAfflElecAddrEntryCount);
		}
		updateProgress(transferResult);
	}

	private void createFacIdTelephonicEntry(TransferResult transferResult)
			throws DAOException, CanceledTransferException {
		/* FacIdTelephonicEntry */
		
		/* CERS_EMS_UNT_PRC_RPT_PRD_EMS */
		virtualExchangeServiceDAO().removeFacIdTelephonicEntry();
		
		verifyRemoved(virtualExchangeServiceDAO().countFacIdTelephonicEntry());
		
		try {
			virtualExchangeServiceDAO().createFacIdTelephonicEntry();
		} catch (DataStoreConcurrencyException e) {
//		DisplayUtil.displayWarning("No facilities were found that match the given criteria.");
		}

		Integer facIdTelephonicEntryCount = virtualExchangeServiceDAO().countFacIdTelephonicEntry();

		if (null == facIdTelephonicEntryCount) {
			throw new DAOException("create failed; facIdTelephonicEntryCount = " + facIdTelephonicEntryCount);
		}
		updateProgress(transferResult);
	}

	private void createFacIdAfflEntry(TransferResult transferResult) throws DAOException, CanceledTransferException {
		//	/* Fac Id Affl */
			virtualExchangeServiceDAO().removeFacIdAfflEntry();
			
			verifyRemoved(virtualExchangeServiceDAO().countFacIdAfflEntry());
			
			try {
				virtualExchangeServiceDAO().createFacIdAfflEntry();
			} catch (DataStoreConcurrencyException e) {
		//		DisplayUtil.displayWarning("No facilities were found that match the given criteria.");
			}
		
			Integer facIdAfflCount = virtualExchangeServiceDAO().countFacIdAfflEntry();
		
			if (null == facIdAfflCount) {
				throw new DAOException("create failed; facIdAfflCount = " + facIdAfflCount);
			}
			
			
			updateProgress(transferResult);
	}

	private void createFacAfflEntry(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* Fac Affl */
		virtualExchangeServiceDAO().removeFacAfflEntry();
		
		verifyRemoved(virtualExchangeServiceDAO().countFacAfflEntry());
		
		try {
			virtualExchangeServiceDAO().createFacAfflEntry();
		} catch (DataStoreConcurrencyException e) {
//		DisplayUtil.displayWarning("No facilities were found that match the given criteria.");
		}

		Integer facAfflCount = virtualExchangeServiceDAO().countFacAfflEntry();

		if (null == facAfflCount) {
			throw new DAOException("create failed; facAfflCount = " + facAfflCount);
		}
		
		updateProgress(transferResult);
	}

	private void createFacNaicsEntry(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* Naics Entry */
		virtualExchangeServiceDAO().removeFacNaicsEntry();
		
		verifyRemoved(virtualExchangeServiceDAO().countFacNaicsEntry());
		
		try {
			virtualExchangeServiceDAO().createFacIdFacNaicsEntry();
		} catch (DataStoreConcurrencyException e) {
//		DisplayUtil.displayWarning("No facilities were found that match the given criteria.");
		}

		Integer facNaicsCount = virtualExchangeServiceDAO().countFacNaicsEntry();

		if (null == facNaicsCount) {
			throw new DAOException("create failed; facNaicsCount = " + facNaicsCount);
		}
		
		updateProgress(transferResult);
	}

	private void createFacSicEntry(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* FACID_FAC_FAC_SIC */
		virtualExchangeServiceDAO().removeFacSicEntry();
		
		verifyRemoved(virtualExchangeServiceDAO().countFacSicEntry());
		
		try {
			virtualExchangeServiceDAO().createFacSicEntry();
		} catch (DataStoreConcurrencyException e) {
//		DisplayUtil.displayWarning("No facilities were found that match the given criteria.");
		}

		Integer facSicCount = virtualExchangeServiceDAO().countFacSicEntry();

		if (null == facSicCount) {
			throw new DAOException("create failed; facSiteAddrCount = " + facSicCount);
		}

		updateProgress(transferResult);
	}

	private void createGeoLocdescEntry(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* FACID_FAC_PRI_GEO_LOC_DESC */
		virtualExchangeServiceDAO().removeGeoLocdescEntry();
		
		verifyRemoved(virtualExchangeServiceDAO().countGeoLocdescEntry());

		
		try {
			virtualExchangeServiceDAO().createGeoLocdescEntry(PARTNER_ID);
		} catch (DataStoreConcurrencyException e) {
//		DisplayUtil.displayWarning("No facilities were found that match the given criteria.");
		}

		Integer facGeoLocDescCount = virtualExchangeServiceDAO().countGeoLocdescEntry();

		if (null == facGeoLocDescCount) {
			throw new DAOException("create failed; facGeoLocDescCount = " + facGeoLocDescCount);
		}
		updateProgress(transferResult);
	}

	private void createFacIdEnvrIntrEntry(TransferResult transferResult)
			throws DAOException, CanceledTransferException {
		/* FACID_ENVR_INTR */ 
		
		virtualExchangeServiceDAO().removeFacIdEnvrIntrEntry();

		verifyRemoved(virtualExchangeServiceDAO().countFacIdEnvrIntrEntry());

		try {
			virtualExchangeServiceDAO().createFacIdEnvrIntrEntry(PARTNER_ID);
		} catch (DataStoreConcurrencyException e) {
//		DisplayUtil.displayWarning("No facilities were found that match the given criteria.");
		}

		Integer facIdEnvrIntrCount = virtualExchangeServiceDAO().countFacIdEnvrIntrEntry();

		if (null == facIdEnvrIntrCount) {
			throw new DAOException("create failed; facIdEnvrIntrCount = " + facIdEnvrIntrCount);
		}
		
		updateProgress(transferResult);
	}

	private void createFacIdFacEntry(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* --FACID_FAC */
		virtualExchangeServiceDAO().removeFacIdFacEntry();
		
		verifyRemoved(virtualExchangeServiceDAO().countFacIdFacEntry());

		try {
			virtualExchangeServiceDAO().createFacIdFacEntry();
		} catch (DataStoreConcurrencyException e) {
//		DisplayUtil.displayWarning("No FacDtls were found that match the given criteria.");
		}

		Integer facIdfacCount = virtualExchangeServiceDAO().countFacIdFacEntry();

		
		if (null == facIdfacCount ) {
			throw new DAOException("create failed; facIdfacCount = " + facIdfacCount);
		}
		
		updateProgress(transferResult);
	}

	private void createFacIdFacDtlsEntry(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* FACID_FAC_DTLS entry */
		
		virtualExchangeServiceDAO().removeFacDtlsEntry();
		
		verifyRemoved(virtualExchangeServiceDAO().countFacDtlsEntry());


		try {
			virtualExchangeServiceDAO().createFacIdFacDtlsEntry();
		} catch (DataStoreConcurrencyException e) {
//		DisplayUtil.displayWarning("No FacDtls were found that match the given criteria.");
		}

		Integer facIdDtlsCount = virtualExchangeServiceDAO().countFacDtlsEntry();

		if (null == facIdDtlsCount) {
			throw new DAOException("create failed; facIdDtlsCount = " + facIdDtlsCount);
		}
		
		updateProgress(transferResult);
	}



	private void createCersEmsUntPrcRptPrdEms(TransferResult transferResult)
			throws DAOException, CanceledTransferException {
		/* CERS_EMS_UNT_PRC_RPT_PRD_EMS */
		virtualExchangeServiceDAO().removeCersEmsUntPrcRptPrdEms();

		verifyRemoved(virtualExchangeServiceDAO().countCersEmsUntPrcRptPrdEms());

		try {
			virtualExchangeServiceDAO().createCersEmsUntPrcRptPrdEms();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that
			// match the given criteria.");
		}

		Integer cersEmsUntPrcRptPrdEmCount = virtualExchangeServiceDAO().countCersEmsUntPrcRptPrdEms();

		if (null == cersEmsUntPrcRptPrdEmCount) {
			throw new DAOException("create failed; cersEmsUntPrcRptPrdEmCount = " + cersEmsUntPrcRptPrdEmCount);
		}
		updateProgress(transferResult);
	}


	private void createCersEmisUnitProcRptPrds(TransferResult transferResult)
			throws DAOException, CanceledTransferException {
		/* CERS_EMIS_UNIT_PROC_RPT_PRD */
		virtualExchangeServiceDAO().removeCersEmisUnitProcRptPrds();

		verifyRemoved(virtualExchangeServiceDAO().countCersEmisUnitProcRptPrds());

		try {
			virtualExchangeServiceDAO().createCersEmisUnitProcRptPrds();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that
			// match the given criteria.");
		}

		Integer cersEmisUnitProcRptPrdCount = virtualExchangeServiceDAO().countCersEmisUnitProcRptPrds();

		if (null == cersEmisUnitProcRptPrdCount) {
			throw new DAOException("create failed; cersEmisUnitProcRptPrdCount = " + cersEmisUnitProcRptPrdCount);
		}
		updateProgress(transferResult);
	}


	private void createCersAfflOrgCommuns(TransferResult transferResult)
			throws DAOException, CanceledTransferException {
		/* CERS_AFFL_ORG_COMMUN */
		virtualExchangeServiceDAO().removeCersAfflOrgCommuns();

		verifyRemoved(virtualExchangeServiceDAO().countCersAfflOrgCommuns());

		try {
			virtualExchangeServiceDAO().createCersAfflOrgCommuns();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that match
			// the given criteria.");
		}

		Integer cersAfflOrgCommunCount = virtualExchangeServiceDAO().countCersAfflOrgCommuns();

		if (null == cersAfflOrgCommunCount) {
			throw new DAOException("create failed; cersAfflOrgCommunCount = " + cersAfflOrgCommunCount);
		}
		updateProgress(transferResult);
	}


	private void createCersAfflOrgAddrs(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* CERS_AFFL_ORG_ADDR */
		virtualExchangeServiceDAO().removeCersAfflOrgAddrs();

		verifyRemoved(virtualExchangeServiceDAO().countCersAfflOrgAddrs());

		try {
			virtualExchangeServiceDAO().createCersAfflOrgAddrs();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that match
			// the given criteria.");
		}

		Integer cersAfflOrgAddrCount = virtualExchangeServiceDAO().countCersAfflOrgAddrs();

		if (null == cersAfflOrgAddrCount) {
			throw new DAOException("create failed; cersAfflOrgAddrCount = " + cersAfflOrgAddrCount);
		}
		updateProgress(transferResult);
	}


	private void createCersAfflOrgIdens(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* CERS_AFFL_ORG_IDEN */
		virtualExchangeServiceDAO().removeCersAfflOrgIdens();

		verifyRemoved(virtualExchangeServiceDAO().countCersAfflOrgIdens());

		try {
			virtualExchangeServiceDAO().createCersAfflOrgIdens();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that match
			// the given criteria.");
		}

		Integer cersAfflOrgIdenCount = virtualExchangeServiceDAO().countCersAfflOrgIdens();

		if (null == cersAfflOrgIdenCount) {
			throw new DAOException("create failed; cersAfflOrgIdenCount = " + cersAfflOrgIdenCount);
		}
		updateProgress(transferResult);
	}


	private void createCersAfflOrgs(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* CERS_AFFL_ORG */
		virtualExchangeServiceDAO().removeCersAfflOrgs();

		verifyRemoved(virtualExchangeServiceDAO().countCersAfflOrgs());

		try {
			virtualExchangeServiceDAO().createCersAfflOrgs();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that match
			// the given criteria.");
		}

		Integer cersAfflOrgCount = virtualExchangeServiceDAO().countCersAfflOrgs();

		if (null == cersAfflOrgCount) {
			throw new DAOException("create failed; cersAfflOrgCount = " + cersAfflOrgCount);
		}

		updateProgress(transferResult);
	}


	private void createCersAffls(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* CERS_AFFL */
		virtualExchangeServiceDAO().removeCersAffls();

		verifyRemoved(virtualExchangeServiceDAO().countCersAffls());

		try {
			virtualExchangeServiceDAO().createCersAffls();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that match
			// the given criteria.");
		}

		Integer cersAfflCount = virtualExchangeServiceDAO().countCersAffls();

		if (null == cersAfflCount) {
			throw new DAOException("create failed; cersAfflCount = " + cersAfflCount);
		}

		updateProgress(transferResult);
	}


	private void createCersRelPtIdens(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* CERS_REL_PT_IDEN */
		virtualExchangeServiceDAO().removeCersRelPtIdens();

		verifyRemoved(virtualExchangeServiceDAO().countCersRelPtIdens());

		try {
			virtualExchangeServiceDAO().createCersRelPtIdens();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that match
			// the given criteria.");
		}

		Integer cersRelPtIdenCount = virtualExchangeServiceDAO().countCersRelPtIdens();

		if (null == cersRelPtIdenCount) {
			throw new DAOException("create failed; cersRelPtIdenCount = " + cersRelPtIdenCount);
		}

		updateProgress(transferResult);
	}


	private void createCersRelPts(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* CERS_REL_PT */
		virtualExchangeServiceDAO().removeCersRelPts();

		verifyRemoved(virtualExchangeServiceDAO().countCersRelPts());

		try {
			virtualExchangeServiceDAO().createCersRelPts();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that match
			// the given criteria.");
		}

		Integer cersRelPtCount = virtualExchangeServiceDAO().countCersRelPts();

		if (null == cersRelPtCount) {
			throw new DAOException("create failed; cersRelPtCount = " + cersRelPtCount);
		}

		updateProgress(transferResult);
	}


	private void createCersEmsUntPrcRlPtAppIdns(TransferResult transferResult)
			throws DAOException, CanceledTransferException {
		/* CERS_EMS_UNT_PRC_RL_PT_APP_IDN */
		virtualExchangeServiceDAO().removeCersEmsUntPrcRlPtAppIdns();

		verifyRemoved(virtualExchangeServiceDAO().countCersEmsUntPrcRlPtAppIdns());

		try {
			virtualExchangeServiceDAO().createCersEmsUntPrcRlPtAppIdns();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that
			// match the given criteria.");
		}

		Integer cersEmsUntPrcRlPtAppIdnCount = virtualExchangeServiceDAO().countCersEmsUntPrcRlPtAppIdns();

		if (null == cersEmsUntPrcRlPtAppIdnCount) {
			throw new DAOException("create failed; cersEmsUntPrcRlPtAppIdnCount = " + cersEmsUntPrcRlPtAppIdnCount);
		}
		updateProgress(transferResult);
	}


	private void createCersEmsUntPrcRlPtApprs(TransferResult transferResult)
			throws DAOException, CanceledTransferException {
		/* CERS_EMS_UNT_PRC_RL_PT_APPR */
		virtualExchangeServiceDAO().removeCersEmsUntPrcRlPtApprs();

		verifyRemoved(virtualExchangeServiceDAO().countCersEmsUntPrcRlPtApprs());

		try {
			virtualExchangeServiceDAO().createCersEmsUntPrcRlPtApprs();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that
			// match the given criteria.");
		}

		Integer cersEmsUntPrcRlPtApprCount = virtualExchangeServiceDAO().countCersEmsUntPrcRlPtApprs();

		if (null == cersEmsUntPrcRlPtApprCount) {
			throw new DAOException("create failed; cersEmsUntPrcRlPtApprCount = " + cersEmsUntPrcRlPtApprCount);
		}

		updateProgress(transferResult);
	}


	private void createCersEmsUntPrcCtrApcCtPls(TransferResult transferResult)
			throws DAOException, CanceledTransferException {
		/* CERS_EMS_UNT_PRC_CTR_APC_CT_PL */
		virtualExchangeServiceDAO().removeCersEmsUntPrcCtrApcCtPls();

		verifyRemoved(virtualExchangeServiceDAO().countCersEmsUntPrcCtrApcCtPls());

		try {
			virtualExchangeServiceDAO().createCersEmsUntPrcCtrApcCtPls();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that
			// match the given criteria.");
		}

		Integer cersEmsUntPrcCtrApcCtPlCount = virtualExchangeServiceDAO().countCersEmsUntPrcCtrApcCtPls();

		if (null == cersEmsUntPrcCtrApcCtPlCount) {
			throw new DAOException("create failed; cersEmsUntPrcCtrApcCtPlCount = " + cersEmsUntPrcCtrApcCtPlCount);
		}

		updateProgress(transferResult);
	}


	private void createCersEmsUntPrcCtrApcCtMss(TransferResult transferResult)
			throws DAOException, CanceledTransferException {
		/* CERS_EMS_UNT_PRC_CTR_APC_CT_MS */
		virtualExchangeServiceDAO().removeCersEmsUntPrcCtrApcCtMss();

		verifyRemoved(virtualExchangeServiceDAO().countCersEmsUntPrcCtrApcCtMss());

		try {
			virtualExchangeServiceDAO().createCersEmsUntPrcCtrApcCtMss();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that
			// match the given criteria.");
		}

		Integer cersEmsUntPrcCtrApcCtMsCount = virtualExchangeServiceDAO().countCersEmsUntPrcCtrApcCtMss();

		if (null == cersEmsUntPrcCtrApcCtMsCount) {
			throw new DAOException("create failed; cersEmsUntPrcCtrApcCtMsCount = " + cersEmsUntPrcCtrApcCtMsCount);
		}

		updateProgress(transferResult);
	}


	private void createCersEmisUnitProcIdens(TransferResult transferResult)
			throws DAOException, CanceledTransferException {
		/* Cers Emis Unit Proc Iden */
		virtualExchangeServiceDAO().removeCersEmisUnitProcIdens();

		verifyRemoved(virtualExchangeServiceDAO().countCersEmisUnitProcIdens());

		try {
			virtualExchangeServiceDAO().createCersEmisUnitProcIdens();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that
			// match the given criteria.");
		}

		Integer cersEmisUnitProcIdenCount = virtualExchangeServiceDAO().countCersEmisUnitProcIdens();

		if (null == cersEmisUnitProcIdenCount) {
			throw new DAOException("create failed; cersEmisUnitProcIdenCount = " + cersEmisUnitProcIdenCount);
		}

		updateProgress(transferResult);
	}


	private void createCersEmisUnitProcs(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* Cers Emis Unit Proc */
		virtualExchangeServiceDAO().removeCersEmisUnitProcs();

		verifyRemoved(virtualExchangeServiceDAO().countCersEmisUnitProcs());

		try {
			virtualExchangeServiceDAO().createCersEmisUnitProcs();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that
			// match the given criteria.");
		}

		Integer cersEmisUnitProcCount = virtualExchangeServiceDAO().countCersEmisUnitProcs();

		if (null == cersEmisUnitProcCount) {
			throw new DAOException("create failed; cersEmisUnitProcCount = " + cersEmisUnitProcCount);
		}

		updateProgress(transferResult);
	}


	private void createCersEmisUnitIdens(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* Cers Emis Iden */
		virtualExchangeServiceDAO().removeCersEmisUnitIdens();

		verifyRemoved(virtualExchangeServiceDAO().countCersEmisUnitIdens());

		try {
			virtualExchangeServiceDAO().createCersEmisUnitIdens();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that match
			// the given criteria.");
		}

		Integer cersEmisUnitIdenCount = virtualExchangeServiceDAO().countCersEmisUnitIdens();

		if (null == cersEmisUnitIdenCount) {
			throw new DAOException("create failed; cersEmisUnitIdenCount = " + cersEmisUnitIdenCount);
		}

		updateProgress(transferResult);
	}


	private void createCersEmisUnits(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* Cers Emis Unit */
		virtualExchangeServiceDAO().removeCersEmisUnits();

		verifyRemoved(virtualExchangeServiceDAO().countCersEmisUnits());

		try {
			virtualExchangeServiceDAO().createCersEmisUnits();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that match
			// the given criteria.");
		}

		Integer cersEmisUnitCount = virtualExchangeServiceDAO().countCersEmisUnits();

		if (null == cersEmisUnitCount) {
			throw new DAOException("create failed; cersEmisUnitCount = " + cersEmisUnitCount);
		}

		updateProgress(transferResult);
	}


	private void createFacSiteAddrs(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* Fac Site Addr */
		virtualExchangeServiceDAO().removeFacSiteAddrs();

		verifyRemoved(virtualExchangeServiceDAO().countFacSiteAddrs());

		try {
			virtualExchangeServiceDAO().createFacSiteAddrs();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that match
			// the given criteria.");
		}

		Integer facSiteAddrCount = virtualExchangeServiceDAO().countFacSiteAddrs();

		if (null == facSiteAddrCount) {
			throw new DAOException("create failed; facSiteAddrCount = " + facSiteAddrCount);
		}

		updateProgress(transferResult);
	}


	private void createFacNaics(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* Fac NAICS */
		virtualExchangeServiceDAO().removeFacNaics();

		verifyRemoved(virtualExchangeServiceDAO().countFacNaics());

		try {
			virtualExchangeServiceDAO().createFacNaics();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that match
			// the given criteria.");
		}

		Integer facNaicsCount = virtualExchangeServiceDAO().countFacNaics();

		if (null == facNaicsCount) {
			throw new DAOException("create failed; facNaicsCount = " + facNaicsCount);
		}

		updateProgress(transferResult);
	}


	private void createFacIdens(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* Fac Iden */
		virtualExchangeServiceDAO().removeFacIdens();

		verifyRemoved(virtualExchangeServiceDAO().countFacIdens());

		try {
			virtualExchangeServiceDAO().createFacIdens();
		} catch (DataStoreConcurrencyException e) {
			// DisplayUtil.displayWarning("No facilities were found that match
			// the given criteria.");
		}

		Integer facIdenCount = virtualExchangeServiceDAO().countFacIdens();

		if (null == facIdenCount) {
			throw new DAOException("create failed; facIdenCount = " + facIdenCount);
		}

		updateProgress(transferResult);
	}


	private void createFacSites(TransferResult transferResult) throws DAOException, CanceledTransferException {
		/* Fac Sites */

		virtualExchangeServiceDAO().removeFacSites();

		verifyRemoved(virtualExchangeServiceDAO().countFacSites());
		
		String cers_id = null;
		String transfer_type = transferResult.getLogEntry().getType();
		if (transfer_type.equalsIgnoreCase("EIS-EI")) {
			cers_id = "1";
		} else if (transfer_type.equalsIgnoreCase("EIS-Facility")) {
			cers_id = "0";
		}

		try {
			virtualExchangeServiceDAO().createFacSites(cers_id);
		} catch (DataStoreConcurrencyException e) {
			//// DisplayUtil.displayWarning("No facilities were found that match
			//// the given criteria.");
		}

		Integer facSiteCount = virtualExchangeServiceDAO().countFacSites();

		if (null == facSiteCount) {
			throw new DAOException("create failed; facSiteCount = " + facSiteCount);
		}

		 updateProgress(transferResult);
	}


	private void createViewVars(Integer reportingYear, String[] permitClassCds,
			Timestamp startDate, Timestamp endDate) throws DAOException {
		/* View Variables */
		

		virtualExchangeServiceDAO().removeFacilityViewVars();
		virtualExchangeServiceDAO().createFacilityViewVars(reportingYear, PARTNER_ID, permitClassCds);

		virtualExchangeServiceDAO().removeFacEmisRprtViewVars();
		virtualExchangeServiceDAO().createFacEmisRprtViewVars(reportingYear);

		virtualExchangeServiceDAO().removeEuProcRptPeriodViewVars();
		virtualExchangeServiceDAO().createEuProcRptPeriodViewVars(startDate, endDate);
	}

	private void createCersEntry(String cersId, String dataCategory, String username, 
			Integer reportingYear, Timestamp endDate) throws DAOException {

		/* CERS entry */

		virtualExchangeServiceDAO().removeCersEntries();

		verifyRemoved(virtualExchangeServiceDAO().countCersEntries());

		virtualExchangeServiceDAO().createCersEntry(cersId, dataCategory, username, reportingYear, endDate,
				PARTNER_ID);

		Integer cersCount = virtualExchangeServiceDAO().countCersEntries();

		if (null == cersCount || cersCount != 1) {
			throw new DAOException("create failed; cersCount = " + cersCount);
		}
	}

	@Override
	public void updateTransferLogEntry(TransferResult transferResult) throws DAOException {
		
		transferResult.getLogEntry().setStatus(transferResult.getStatus().getValue());
		int progressPercent = 
				transferResult.getValueAtFailure() != null? 
						transferResult.getValueAtFailure() : transferResult.getValue();
		transferResult.getLogEntry().setProgressPercent(progressPercent);

		VirtualExchangeServiceDAO virtualExchangeServiceDAO = 
				virtualExchangeServiceDAO();
		virtualExchangeServiceDAO.updateTransferLogEntry(transferResult.getLogEntry());
	}

	private void verifyRemoved(Integer count) throws DAOException {
		if (null == count || count > 0) {
			throw new DAOException("remove failed; count = " + count);
		}
	}

	@Override
	public List<TransferLogEntry> retrieveTransferLogEntries() throws DAOException {
		
		VirtualExchangeServiceDAO virtualExchangeServiceDAO = 
				virtualExchangeServiceDAO();
		return virtualExchangeServiceDAO.retrieveTransferLogEntries();
	}

	@Override
	public List<TransferLogEntry> retrievePendingTransferLogEntries() throws DAOException {
		
		VirtualExchangeServiceDAO virtualExchangeServiceDAO = 
				virtualExchangeServiceDAO();
		return virtualExchangeServiceDAO.retrievePendingTransferLogEntries();
	}

	@Override
	public TransferLogEntry createTransferLogEntry(TransferResult transferEisResult) throws DAOException {
		
		transferEisResult.getLogEntry().setStatus(transferEisResult.getStatus().getValue());

		VirtualExchangeServiceDAO virtualExchangeServiceDAO = 
				virtualExchangeServiceDAO();
		return virtualExchangeServiceDAO.createTransferLogEntry(transferEisResult.getLogEntry());
	}

	@Override
	public void updateTransferLogEntry(TransferLogEntry transferLogEntry) throws DAOException {
		VirtualExchangeServiceDAO virtualExchangeServiceDAO = 
				virtualExchangeServiceDAO();
		virtualExchangeServiceDAO.updateTransferLogEntry(transferLogEntry);
	}
	
}
