package us.oh.state.epa.stars2.database.dao;

import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.emissionReport.StringContainer;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCDataImportPollutant;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEUCategory;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCNonChargePollutant;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCPollutant;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface ServiceCatalogDAO extends TransactableDAO {
    /**
     * @return
     * @throws DAOException
     */
    SCEmissionsReport[] retrieveEmissionsReports() throws DAOException;

    /**
     * @param reportId
     * @return
     * @throws DAOException
     */
    SCPollutant[] retrievePollutants(int reportId) throws DAOException;
    
    /**
     * @param reportId
     * @return
     * @throws DAOException
     */
    StringContainer[] retrieveReportExemptions(int reportId) throws DAOException;
 
    /**
     * @param reportId
     * @return
     * @throws DAOException
     */
    StringContainer[] retrieveReportTvClassifications(int reportId) throws DAOException;

    /**
     * @param reportId
     * @return
     * @throws DAOException
     */
    SCEmissionsReport retrieveEmissionsReport(int reportId) throws DAOException;

    /**
     * @param newReport
     * @return
     * @throws DAOException
     */
    SCEmissionsReport createEmissionsReport(SCEmissionsReport newReport)
            throws DAOException;

    /**
     * @param report
     * @return
     * @throws DAOException
     */
    boolean modifyEmissionsReport(SCEmissionsReport report) throws DAOException;

    /**
     * @param newFee
     * @return
     * @throws DAOException
     */
    Fee createFee(Fee newFee) throws DAOException;

    /**
     * @param fee
     * @return
     * @throws DAOException
     */
    boolean modifyFee(Fee fee) throws DAOException;

    /**
     * @param reportId
     * @param feeId
     * @return
     * @throws DAOException
     */
    boolean addFeeToEmissionsReport(int reportId, int feeId)
            throws DAOException;

    /**
     * @param permitId
     * @param feeId
     * @return
     * @throws DAOException
     */
    boolean addFeeToCategory(int categoryId, int feeId) throws DAOException;

    /**
     * @param pollutantCd
     * @return
     * @throws DAOException
     */
    boolean addPollutantToEmissionsReport(SCPollutant pollutant)
            throws DAOException;
    
    /**
     * @param reportId
     * @param exemption
     * @return
     * @throws DAOException
     */
    boolean addExemptionToEmissionsReport(Integer id, String exemption) throws DAOException;

    /**
     * @param reportId
     * @param tvClass
     * @return
     * @throws DAOException
     */
    boolean addTvClassificationToEmissionsReport(Integer id, String tvClass) throws DAOException;
    
    /**
     * @param reportId
     * @throws DAOException
     */
    void removeEmissionsReport(int reportId) throws DAOException;
    
    /**
     * @param reportId
     * @throws DAOException
     */
    void removeFee(int feeId) throws DAOException;
    
    /**
     * @param reportId
     * @throws DAOException
     */
    void removeEmissionsReportFees(int reportId) throws DAOException;

    /**
     * @param reportId
     * @throws DAOException
     */
    void removeEmissionsReportPollutants(int reportId) throws DAOException;
    
    /**
     * @param reportId
     * @throws DAOException
     */
    void removeEmissionsReportExemptions(int reportId) throws DAOException;

    /**
     * @param reportId
     * @throws DAOException
     */
    void removeEmissionsReportTvClassifications(int reportId) throws DAOException;

    /**
     * @param reportId
     * @throws DAOException
     */
    void removeEmissionsReportReminders(int reportId) throws DAOException;

    /**
     * @param categoryId
     * @throws DAOException
     */
    void removeSCCategoryFees(int categoryId) throws DAOException;

    /**
     * @return
     * @throws DAOException
     */
    SimpleDef[] retrieveReportPollutants() throws DAOException;

    /**
     * @param newCategory
     * @return
     * @throws DAOException
     */
    SCEUCategory createSCEUCategory(SCEUCategory newCategory)
            throws DAOException;

    /**
     * @param category
     * @return
     * @throws DAOException
     */
    boolean modifySCEUCategory(SCEUCategory category) throws DAOException;

    /**
     * @return
     * @throws DAOException
     */
    SCEUCategory[] retrieveEUCategories() throws DAOException;

    /**
     * @param categoryId
     * @return
     * @throws DAOException
     */
    SCEUCategory retrieveEUCategory(int categoryId) throws DAOException;

    /**
     * @param feeId
     * @return
     * @throws DAOException
     */
    Fee retrieveFee(Integer feeId) throws DAOException;

    /**
     * @param feeId
     * @return
     * @throws DAOException
     */
    SCEUCategory retrieveCategoryByFeeId(int feeId) throws DAOException;

	boolean addNonChargePollutantToEmissionsReport(SCNonChargePollutant ncPollutant) throws DAOException;

	void removeEmissionsReportNonChargePollutants(int reportId) throws DAOException;

	SCNonChargePollutant[] retrieveNonChargePollutants(int reportId)
			throws DAOException;
	
	// Permit Class (Facility Class)
	List<String> retrievePermitClassCds(Integer reportId)
			throws DAOException;
	
	List<String> createPermitClassXrefs(Integer reportId,
			List<String> permitClassCds) throws DAOException;
	
	void deletePermitClassXrefs(Integer reportId) throws DAOException;
	
	// Facility Type
	List<String> retrieveFacilityTypeCds(Integer reportId)
			throws DAOException;
	
	List<String> createFacilityTypeXrefs(Integer reportId,
			List<String> facilityTypeCds) throws DAOException;
	
	void deleteFacilityTypeXrefs(Integer reportId) throws DAOException;
	
	boolean okToSaveServiceCatalogTemplate(SCEmissionsReport newReport)
			throws DAOException;
	
	Fee[] retrieveReportFees(int reportId) throws DAOException;
	
	Integer retrieveSCEmissionsReportId(Integer reportingYear, String contentTypeCd, String regulatoryRequirementCd)
			throws DAOException;

	Integer retrieveHighestPriorityRptReportId(Integer reportingYear, String contentTypeCd, String facilityId)
			throws DAOException;

	
	/**
	 * 
	 * @param scDataImportPollutant
	 * @return SCDataImportPollutant
	 * @throws DAOException
	 */
	SCDataImportPollutant addDataImportPollutantToEmissionsReport(SCDataImportPollutant scDataImportPollutant)
			throws DAOException;

	/**
	 * 
	 * @param reportId
	 * @throws DAOException
	 * @return none
	 */
	void removeEmissionsReportDataImportPollutants(int reportId) throws DAOException;

	/**
	 * 
	 * @param reportId
	 * @return An array of SCDataImportPollutant
	 * @throws DAOException
	 */
	SCDataImportPollutant[] retrieveDataImportPollutants(int reportId) throws DAOException;

}
