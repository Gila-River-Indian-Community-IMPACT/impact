package us.oh.state.epa.stars2.database.dao;

import java.sql.Timestamp;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.ves.TransferLogEntry;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface VirtualExchangeServiceDAO extends TransactableDAO {

	void removeCersEntries() throws DAOException;
	
	void removeFacSites() throws DAOException;

	Integer countCersEntries() throws DAOException;

	void createCersEntry(String cersId, String dataCategory, String username, Integer reportingYear, Timestamp endDate,
			String partnerId) throws DAOException;

	Integer countFacSites() throws DAOException;

	void removeFacIdens() throws DAOException;

	void removeFacNaics() throws DAOException;

	void removeFacSiteAddrs() throws DAOException;

	void removeCersEmisUnits() throws DAOException;

	void removeCersEmisUnitProcs() throws DAOException;

	void removeCersEmisUnitProcIdens() throws DAOException;

	void removeCersEmsUntPrcCtrApcCtMss() throws DAOException;

	void removeCersEmsUntPrcCtrApcCtPls() throws DAOException;

	void removeCersEmsUntPrcRlPtApprs() throws DAOException;

	void removeCersEmsUntPrcRlPtAppIdns() throws DAOException;

	void removeCersRelPts() throws DAOException;

	void removeCersRelPtIdens() throws DAOException;

	void removeCersAffls() throws DAOException;

	void removeCersAfflOrgs() throws DAOException;

	void removeCersAfflOrgIdens() throws DAOException;

	void removeCersAfflOrgAddrs() throws DAOException;

	void removeCersAfflOrgCommuns() throws DAOException;

	void removeCersEmisUnitProcRptPrds() throws DAOException;

	void removeCersEmsUntPrcRptPrdEms() throws DAOException;

	Integer countCersEmsUntPrcRptPrdEms() throws DAOException;

	Integer countCersEmisUnitProcRptPrds() throws DAOException;

	Integer countCersAfflOrgCommuns() throws DAOException;

	Integer countCersAfflOrgAddrs() throws DAOException;

	Integer countCersAfflOrgIdens() throws DAOException;

	Integer countCersAfflOrgs() throws DAOException;

	Integer countCersAffls() throws DAOException;

	Integer countCersRelPtIdens() throws DAOException;

	Integer countCersRelPts() throws DAOException;

	Integer countCersEmsUntPrcRlPtAppIdns() throws DAOException;

	Integer countCersEmsUntPrcRlPtApprs() throws DAOException;

	Integer countCersEmsUntPrcCtrApcCtPls() throws DAOException;

	Integer countCersEmsUntPrcCtrApcCtMss() throws DAOException;

	Integer countCersEmisUnitProcIdens() throws DAOException;

	Integer countCersEmisUnitProcs() throws DAOException;

	Integer countCersEmisUnits() throws DAOException;

	Integer countFacSiteAddrs() throws DAOException;

	Integer countFacNaics() throws DAOException;

	Integer countFacIdens() throws DAOException;

	void createFacilityViewVars(Integer reportingYear, String partnerId, String[] permitClassCds) throws DAOException;
	
	void createFacilityViewVars(Integer reportingYear, String partnerId, String[] permitClassCds, int cersId) throws DAOException;

	void removeFacilityViewVars() throws DAOException;

	void createFacilityViewPermitClassCdVars(String[] permitClassCds) throws DAOException;

	void removeFacilityViewPermitClassCdVars() throws DAOException;

	Integer countCersEmisUnitIdens() throws DAOException;

	void removeCersEmisUnitIdens() throws DAOException;

	void createFacSites(String cers_id) throws DAOException;

	void createFacIdens() throws DAOException;

	void createFacNaics() throws DAOException;

	void createFacSiteAddrs() throws DAOException;

	void createCersEmisUnits() throws DAOException;

	void createCersEmisUnitIdens() throws DAOException;

	void createCersEmisUnitProcs() throws DAOException;

	void createCersEmisUnitProcIdens() throws DAOException;

	void createCersEmsUntPrcCtrApcCtMss() throws DAOException;

	void createCersEmsUntPrcCtrApcCtPls() throws DAOException;

	void createCersEmsUntPrcRptPrdEms() throws DAOException;

	void createCersEmisUnitProcRptPrds() throws DAOException;

	void createCersAfflOrgCommuns() throws DAOException;

	void createCersAfflOrgAddrs() throws DAOException;

	void createCersEmsUntPrcRlPtApprs() throws DAOException;

	void createCersEmsUntPrcRlPtAppIdns() throws DAOException;

	void createCersRelPts() throws DAOException;

	void createCersRelPtIdens() throws DAOException;

	void createCersAffls() throws DAOException;

	void createCersAfflOrgs() throws DAOException;

	void createCersAfflOrgIdens() throws DAOException;

	void removeEuProcRptPeriodViewVars() throws DAOException;

	void createEuProcRptPeriodViewVars(Timestamp startDate, Timestamp endDate) throws DAOException;

	void removeFacEmisRprtViewVars() throws DAOException;

	void createFacEmisRprtViewVars(Integer reportingYear) throws DAOException;

	TransferLogEntry createTransferLogEntry(TransferLogEntry logEntry) throws DAOException;

	void updateTransferLogEntry(TransferLogEntry logEntry) throws DAOException;

	List<TransferLogEntry> retrieveTransferLogEntries() throws DAOException;

	List<TransferLogEntry> retrievePendingTransferLogEntries() throws DAOException;

	TransferLogEntry retrieveTransferLogEntry(long id) throws DAOException;

	void removeFacDtlsEntry() throws DAOException;

	Integer countFacDtlsEntry() throws DAOException;

	void createFacIdFacDtlsEntry() throws DAOException;

	void removeFacIdEnvrIntrEntry() throws DAOException;

	Integer countFacIdEnvrIntrEntry() throws DAOException;

	void createFacIdEnvrIntrEntry(String partnerId) throws DAOException;

	void removeGeoLocdescEntry() throws DAOException;

	Integer countGeoLocdescEntry() throws DAOException;

	void createGeoLocdescEntry(String partnerId) throws DAOException;

	void removeFacSicEntry() throws DAOException;

	Integer countFacSicEntry() throws DAOException;

	void createFacSicEntry() throws DAOException;

	void removeFacNaicsEntry() throws DAOException;

	Integer countFacNaicsEntry() throws DAOException;

	void createFacIdFacNaicsEntry() throws DAOException;

	void removeFacAfflEntry() throws DAOException;

	Integer countFacAfflEntry() throws DAOException;

	void createFacAfflEntry() throws DAOException;

	void removeFacIdAfflEntry() throws DAOException;

	Integer countFacIdAfflEntry() throws DAOException;

	void createFacIdAfflEntry() throws DAOException;

	void removeFacIdTelephonicEntry() throws DAOException;

	Integer countFacIdTelephonicEntry() throws DAOException;

	void createFacIdTelephonicEntry() throws DAOException;

	void removeFacIdFacEntry() throws DAOException;

	Integer countFacIdFacEntry() throws DAOException;

	void createFacIdFacEntry()throws DAOException;

	void removeFacIdAfflElecAddrEntry() throws DAOException;

	Integer countFacIdAfflElecAddrEntry() throws DAOException;

	void createFacIdAfflElecAddrEntry() throws DAOException;

	
}
