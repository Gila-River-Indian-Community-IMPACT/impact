package us.oh.state.epa.stars2.database.dao;

import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.ceta.FacilityComplianceStatus;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FacilityHistory;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface CompEnfFacilityDAO extends TransactableDAO {
	/**
	 * Retrieve the FacilityHistory object identified by facilityHistoryId.
	 * @param facilityHistoryId
	 * @return
	 * @throws DAOException
	 */
	FacilityHistory retrieveFacilityHistory(int facilityHistoryId) throws DAOException;
	
	/**
	 * Create a new FacilityHistory record.
	 * @param facilityHistory
	 * @return
	 * @throws DAOException
	 */
	FacilityHistory createFacilityHistory(FacilityHistory facilityHistory) throws DAOException;
	
	/**
	 * Modify an existing FacilityHistory record.
	 * @param facilityHistory
	 * @return
	 * @throws DAOException
	 */
	boolean modifyFacilityHistory(FacilityHistory facilityHistory) throws DAOException;
	
	/**
	 * Delete the FacilityHistory record identified by facilityHistoryId.
	 * @param facilityHistoryId
	 * @throws DAOException
	 */
	void deleteFacilityHistory(int facilityHistoryId) throws DAOException;
	
	/**
	 * Retrieve NESHAPS compliance status information related to the FacilityHistory
	 * object identified by facilityHistoryId.
	 * @param facilityHistoryId
	 * @return
	 * @throws DAOException
	 */
	List<FacilityComplianceStatus> retrieveFacHistNeshaps(int facilityHistoryId) throws DAOException;
	
	/**
	 * Retrieve NSPS compliance status information related to the FacilityHistory
	 * object identified by facilityHistoryId.
	 * @param facilityHistoryId
	 * @return
	 * @throws DAOException
	 */
	List<FacilityComplianceStatus> retrieveFacHistNsps(int facilityHistoryId) throws DAOException;
	
	/**
	 * Retrieve NSR compliance status information related to the FacilityHistory
	 * object identified by facilityHistoryId.
	 * @param facilityHistoryId
	 * @return
	 * @throws DAOException
	 */
	List<FacilityComplianceStatus> retrieveFacHistNsr(int facilityHistoryId) throws DAOException;
	
	/**
	 * Retrieve PSD compliance status information related to the FacilityHistory
	 * object identified by facilityHistoryId.
	 * @param facilityHistoryId
	 * @return
	 * @throws DAOException
	 */
	List<FacilityComplianceStatus> retrieveFacHistPsd(int facilityHistoryId) throws DAOException;
	
	/**
	 * Create a new NESHAPS compliance status record.
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	FacilityComplianceStatus createFacHistNeshaps(FacilityComplianceStatus status) throws DAOException;
	
	/**
	 * Create a new NSPS compliance status record.
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	FacilityComplianceStatus createFacHistNsps(FacilityComplianceStatus status) throws DAOException;
	
	/**
	 * Create a new NSR compliance status record.
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	FacilityComplianceStatus createFacHistNsr(FacilityComplianceStatus status) throws DAOException;
	
	/**
	 * Create a new PSD compliance status record.
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	FacilityComplianceStatus createFacHistPsd(FacilityComplianceStatus status) throws DAOException;
	
	/**
	 * Modify an existing  NESHAPS compliance status record.
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	boolean modifyFacHistNeshaps(FacilityComplianceStatus status) throws DAOException;
	
	/**
	 * Modify an existing  NSPS compliance status record.
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	boolean modifyFacHistNsps(FacilityComplianceStatus status) throws DAOException;
	
	/**
	 * Modify an existing  NSR compliance status record.
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	boolean modifyFacHistNsr(FacilityComplianceStatus status) throws DAOException;
	
	/**
	 * Modify an existing  PSD compliance status record.
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	boolean modifyFacHistPsd(FacilityComplianceStatus status) throws DAOException;
	
	/**
	 * Delete all NESHAPS compliance status records for the FacilityHistory
	 * identified by facilityHistoryId.
	 * @param facilityHistoryId
	 * @throws DAOException
	 */
	void deleteFacHistNeshaps(int facilityHistoryId) throws DAOException;

	
	/**
	 * Delete all NSPS compliance status records for the FacilityHistory
	 * identified by facilityHistoryId.
	 * @param facilityHistoryId
	 * @throws DAOException
	 */
	void deleteFacHistNsps(int facilityHistoryId) throws DAOException;

	
	/**
	 * Delete all NSR compliance status records for the FacilityHistory
	 * identified by facilityHistoryId.
	 * @param facilityHistoryId
	 * @throws DAOException
	 */
	void deleteFacHistNsr(int facilityHistoryId) throws DAOException;

	
	/**
	 * Delete all PSD compliance status records for the FacilityHistory
	 * identified by facilityHistoryId.
	 * @param facilityHistoryId
	 * @throws DAOException
	 */
	void deleteFacHistPsd(int facilityHistoryId) throws DAOException;
}
