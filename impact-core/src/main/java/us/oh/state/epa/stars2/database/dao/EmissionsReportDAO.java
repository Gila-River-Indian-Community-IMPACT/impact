package us.oh.state.epa.stars2.database.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import us.oh.state.epa.stars2.app.emissionsReport.UsEpaEisReport;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.DefaultStackParms;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionTotal;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.Emissions;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsMaterialActionUnits;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEU;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportNote;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportPeriod;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsRptInfo;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsVariable;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FacilityAppInfo;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FacilityPermitInfo;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FacilityRptInfo;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FacilityYearPair;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FireRow;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.IntegerPair;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.MultiEstablishment;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.PollutantPair;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.invoice.Invoice;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.util.DbInteger;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.reports.MissingFIREFactor;

public interface EmissionsReportDAO extends TransactableDAO {
    /**
     * @param newReport
     * @return
     * @throws DAOException
     */
    EmissionsReport createEmissionsReport(EmissionsReport newReport)
            throws DAOException;

    /**
     * @param report
     * @return
     * @throws DAOException
     */
    boolean modifyEmissionsReport(EmissionsReport report, Integer replaceId) throws DAOException;
    
    /**
     * @param report
     * @return
     * @throws DAOException
     */
    boolean deleteEmissionsReport(EmissionsReport report) throws DAOException;

    /**
     * @param reportId
     * @return
     * @throws DAOException
     */
    EmissionsReport retrieveEmissionsReport(int reportId) throws DAOException;
    
    /**
     * @param year
     * * @param facilityId
     * @return
     * @throws DAOException
     */
    EmissionsReport retrieveLatestEmissionReport(Integer year, String facilityId)
    throws DAOException ;
    
    /**
     * @param year
     * * @param facilityId
     * @return
     * @throws DAOException
     */
    EmissionsReport retrieveLatestSubmittedEmissionReport(Integer year, String facilityId)
    throws DAOException ;
    
    /**
     * @param reportId
     * @return
     * @throws DAOException
     */
    EmissionTotal[] retrieveEmissionTotals(int reportId)
    throws DAOException;
    
    /**
     * @param reportId
     * @return
     * @throws DAOException
     */
    Emissions[] retrieveEmissions(EmissionsReportPeriod periodId)
    throws DAOException;
    
    /**
     * @param reportId
     * @return
     * @throws DAOException
     */
    void removeReportEUs(int reportId) throws DAOException;

    /**
     * @param fpId
     * @return
     * @throws DAOException
     */
    EmissionsReport[] retrieveEmissionsReports(int fpId) throws DAOException;

    /**
     * @param newPeriod
     * @return
     * @throws DAOException
     */
    EmissionsReportPeriod createEmissionPeriod(EmissionsReportPeriod newPeriod)
            throws DAOException;

    /**
     * @param period
     * @return
     * @throws DAOException
     */
    boolean modifyEmissionPeriod(EmissionsReportPeriod period)
            throws DAOException;

    /**
     * @param emissionPeriodId
     * @return
     * @throws DAOException
     */
    EmissionsReportPeriod retrieveEmissionPeriod(int emissionPeriodId)
            throws DAOException;

    /**
     * @param reportId
     * @return
     * @throws DAOException
     */
    EmissionsReportPeriod[] retrieveEmissionPeriods(int reportId)
            throws DAOException;

    /**
     * @param feeId
     * @return
     * @throws DAOException
     */
    Fee[] retrieveFeeinfo(int feeId)
            throws DAOException;
    
    /**
     * @param pollutantCd
     * @return
     * @throws DAOException
     */
    String[] retrieveSuperCd(String pollutantCd)
            throws DAOException;
    
    /**
     * @param newEmissions
     * @return
     * @throws DAOException
     */
    Emissions createEmissions(Emissions newEmissions) throws DAOException;

    /**
     * @param newEmissions
     * @return EmissioinTotal
     * @throws DAOException
     */
    EmissionTotal createEmissionTotal(EmissionTotal newEmissions)
        throws DAOException ;

    /**
     * @param newReport
     * @return
     * @throws DAOException
     */
    EmissionsReportNote addReportNote(EmissionsReportNote newReport)
            throws DAOException;

    /**
     * @param year1
     * @param year2
     * @return
     * @throws DAOException
     */
    FacilityYearPair[] retrieveStragglerNTV(int year1, int year2)
    throws DAOException;
    
    /**
     * @param emissionsPeriodId
     * @return
     * @throws DAOException
     */
    EmissionsMaterialActionUnits[] retrieveMaterialActionUnits(
            int emissionsPeriodId) throws DAOException;

    /**
     * @param emissionsPeriodId
     * @return
     * @throws DAOException
     */
    EmissionsVariable[] retrieveEmissionsVariables(
            int emissionsPeriodId) throws DAOException;

    /**
     * @param emissionPeriodId
     * @param materialCd
     * @return
     * @throws DAOException
     */
    boolean periodMaterialExists(int emissionPeriodId, String materialCd)
            throws DAOException;

    /**
     * @param newMAU
     * @return
     * @throws DAOException
     */
    EmissionsMaterialActionUnits createMaterialActionUnits(
            EmissionsMaterialActionUnits newMAU) throws DAOException;
    
    /**
     * @param var
     * @return
     * @throws DAOException
     */
    EmissionsVariable createVariable(
            EmissionsVariable var) throws DAOException;

    /**
     * @param emissionPeriodId
     * @param emu_id
     * @param emissionRptId
     * @param processId
     * @throws DAOException
     */
    void addReportPeriod(int emissionPeriodId, Integer emuId,
            Integer euGroupId, int emissionRptId) throws DAOException;

    /**
     * @param euGroupId
     * @param emuId
     * @param emissionRptId
     * @throws DAOException
     */
    void addReportEUGroup(int euGroupId, int emuId, int emissionRptId)
            throws DAOException;

    /**
     * @param euGroupId
     * @param emissionRptId
     * @throws DAOException
     */
    void addEUGroupToReport(int euGroupId, int emissionRptId)
            throws DAOException;

    /**
     * @param emissionRptId
     * @param reportTypeCd
     * @throws DAOException
     */
    void addReportType(int emissionRptId, String reportTypeCd)
            throws DAOException;

    /**
     * @param noteId
     * @return
     * @throws DAOException
     */
    EmissionsReportNote retrieveReportNote(int noteId) throws DAOException;

    /**
     * @param emissionsRptId
     * @return
     * @throws DAOException
     */
    EmissionsReportNote[] retrieveReportNotes(int emissionsRptId)
            throws DAOException;

    /**
     * @param newEu
     * @return
     * @throws DAOException
     */
    EmissionsReportEU createReportEU(EmissionsReportEU newEu)
            throws DAOException;

    /**
     * @param reportId
     * @throws DAOException
     */
    void removeReportPeriods(int reportId) throws DAOException;
    
    /**
     * @param periodId
     * @throws DAOException
     */
    void removeReportPeriod(int periodId) throws DAOException;
    
    /**
     * @param docId
     * @throws DAOException
     */
    public void removeEmissionsDocument(int docId) throws DAOException;
    
    /**
     * @param reportId
     * @throws DAOException
     */
    public void removeEmissionsDocuments(int reportId) throws DAOException;

    /**
     * @param reportId
     * @throws DAOException
     */
    void removeReportTypes(int reportId) throws DAOException;

    /**
     * @param reportId
     * @throws DAOException
     */
    void removeReportNotes(int reportId) throws DAOException;

    /**
     * @param reportId
     * @throws DAOException
     */
    void removeReportEUGroupsFromReport(int reportId) throws DAOException;

    /**
     * @param reportId
     * @throws DAOException
     */
    void removeReportEUGroups(int groupId) throws DAOException;
    
    /**
     * @param reportId
     * @throws DAOException
     */
    void removeReportEUsFromGroups(int reportId) throws DAOException;

    /**
     * @param emuId
     * @return
     * @throws DAOException
     */
    EmissionsReportEU retrieveReportEU(int emissionsRptId, int emuId)
            throws DAOException;

    /**
     * @param emissionsRptId
     * @return
     * @throws DAOException
     */
    EmissionsReportEU[] retrieveReportEUs(int emissionsRptId)
            throws DAOException;

    /**
     * @param newAttachment
     * @return
     * @throws DAOException
     */
    EmissionsDocumentRef createEmissionsDocument(
            EmissionsDocumentRef newAttachment) throws DAOException;
    
    /**
     * @param rptDoc
     * @return
     * @throws DAOException
     */
    public boolean modifyEmissionsDocument(EmissionsDocumentRef rptDoc)
    throws DAOException;
    
    /**
     * @param documentId
     * @return
     * @throws DAOException
     */
    public EmissionsDocumentRef retrieveEmissionsDocument(int documentId)
            throws DAOException;

    /**
     * @param emissionsRptId
     * @return
     * @throws DAOException
     */
    public EmissionsDocumentRef[] retrieveEmissionsDocuments(
            int emissionsRptId) throws DAOException;


    // SimpleIdDef[] retrieveReportReviewers() throws DAOException;

    /**
     * @param newEUGroup
     * @return
     * @throws DAOException
     */
    EmissionsReportEUGroup createReportEUGroup(EmissionsReportEUGroup newEUGroup)
            throws DAOException;

    /**
     * @param euGroup
     * @return
     * @throws DAOException
     */
    boolean modifyReportEUGroup(EmissionsReportEUGroup euGroup)
            throws DAOException;

    /**
     * @param reportEUGroupId
     * @return
     * @throws DAOException
     */
    EmissionsReportEUGroup retrieveReportEUGroup(int reportEUGroupId)
            throws DAOException;
    
    /**
     * @param reportEUGroupId
     * @return
     * @throws DAOException
     */
    EmissionsReportEUGroup[] retrieveReportEUGroupRef(int reportId)
    		throws DAOException;
    
    /**
     * @param periodId
     * @return
     * @throws DAOException
     */
    Emissions[] retrieveGroupEmissions(int periodId)
        throws DAOException;

    /**
     * @param reportId
     * @param reportEUGroupName
     * @return
     * @throws DAOException
     */
    EmissionsReportEUGroup retrieveReportEUGroup(int reportId,
            String reportEUGroupName) throws DAOException;

    /**
     * @param emissionsReportId
     * @return
     * @throws DAOException
     */
    EmissionsReportEUGroup[] retrieveReportEUGroups(int emissionsReportId)
            throws DAOException;

    /**
     * @param emissionsReportId
     * @param emuId
     * @return
     * @throws DAOException
     */
    EmissionsReportEUGroup[] retrieveReportEUGroups(int emissionsReportId,
            int emuId) throws DAOException;

    /**
     * @param String
     * @return
     * @throws DAOException
     */
    EmissionsRptInfo[] retrieveEmissionsRptInfos(String facilityId)
            throws DAOException;
    
    /**
     * @param int
     * @return
     * @throws DAOException
     */
    EmissionsRptInfo[] turnOnReporting(SCEmissionsReport scEmissionsReport)
    throws DAOException;
    
    /**
     * @param int
     * @return
     * @throws DAOException
     */
    //EmissionsRptInfo[] turnOnReportingNtv(int year)
    //throws DAOException;

    /**
     * @param String
     * @param Integer
     * @return
     * @throws DAOException
     */
    EmissionsRptInfo retrieveEmissionsRptInfo(String facilityId, Integer year)
            throws DAOException;

//    /**
//     */
//    EmissionsRptInfo[] retrieveEmissionsRptInfos(String facilityId, String rptRcvdStatusCd,
//                                                        String emissionsRptCd, Integer year,
//                                                        boolean rptEnabled)
//        throws DAOException;

    /**
     * @param String
     * @param EmissionsRptInfo
     * @return
     * @throws DAOException
     */
    EmissionsRptInfo createEmissionsRptInfo(String facilityId,
            EmissionsRptInfo info) throws DAOException;

    /**
     * @param String
     * @param EmissionsRptInfo
     * @return
     * @throws DAOException
     */
    boolean modifyEmissionsRptInfo(String facilityId, EmissionsRptInfo info)
            throws DAOException;
    
    /**
     * @param String
     * @param Integer
     * @return
     * @throws DAOException
     */
    void deleteEmissionsRptInfo(String facilityId, Integer id)
            throws DAOException;

    /**
     * @param searchObj
     * @return
     * @throws DAOException
     */
    EmissionsReportSearch[] searchEmissionsReports(
            EmissionsReportSearch searchObj) throws DAOException;
    
    /**
     * @param searchObj
     * @return
     * @throws DAOException
     */
    EmissionsReportSearch[] searchEmissionsReportForScore(
            EmissionsReportSearch searchObj) throws DAOException;

    /**
     * @param year
     * @param facilityClass
     * @return
     * @throws DAOException
     */
    FacilityRptInfo[] missingEmissionsRptInfo(SCEmissionsReport scEmissionsReport)
            throws DAOException;

    /**
     * @return
     * @throws DAOException
     */
    Integer[] findUnusedEmissionPeriods() throws DAOException;

    /**
     * @param report
     * @throws DAOException
     */
    void deleteEUs(EmissionsReport report) throws DAOException;

    /**
     * @param emissionPeriodId
     * @throws DAOException
     */
    void removeEmissionPeriod(int emissionPeriodId) throws DAOException;
    
    /**
     * @param emissionPeriodId
     * @throws DAOException
     */
    void removeEmissionMaterialActionUnits(int emissionPeriodId) throws DAOException;

    /**
     * @param emissionPeriodId
     * @throws DAOException
     */
    void removeEmissionsVariables(int emissionPeriodId)
    throws DAOException;
    
    /**
     * @param emissionPeriodId
     * @throws DAOException
     */
    void removeEmissionsByPeriodId(int emissionPeriodId) throws DAOException;
    
    /**
     * @param emissionRptId
     * @throws DAOException
     */
    void removeEmissionsByReportId(int emissionsRptId) throws DAOException;

    /**
     * @param row
     * @throws DAOException
     */
    FireRow createFireRow(FireRow row) throws DAOException;

    /**
     * @param EmissionsReportPeriod
     * @para, fuelBased
     * @throws DAOException
     */
    FireRow[] retrieveFireRows(Integer year, EmissionsReportPeriod period) throws DAOException ;

    /**
     * @param sccId
     * @param materialCd
     * @throws DAOException
     */
    FireRow[] retrieveFireRows(String sccId, String materialCd)
            throws DAOException;
    
    /**
     * @param year
     * @param countyCd
     * @throws DAOException
     */
    boolean retrieveCompliance(Integer year, String countyCd) throws DAOException;
    
    /**
     * @throws DAOException
     */
    PollutantPair[] retrievePartOf() throws DAOException;
    
    PollutantPair retrievePollutantParent(String pollutantCode) throws DAOException;
    
    /**
     * @param fireId
     * @return
     * @throws DAOException
     */
    FireRow retrieveFireRow(String fireId) throws DAOException;
    
    /**
     * @param sccId
     * @param materialCd
     * @parm year
     * @return
     * @throws DAOException
     */
    FireRow retrieveFireRow(String sccId, String materialCd, int year) throws DAOException;
    
    /**
     * @param sccId
     * @param materialCd
     * @param pollutantCd
     * @param factor
     * @param formula
     * @param measure
     * @param action
     * @param year
     * @return
     * @throws DAOException
     */
    FireRow[] retrieveFireRow(String sccId, String materialCd,
            String pollutantCd, String factor, String formula, String measure,
            String action, int year) throws DAOException;
    
    /**
     * @param sccId
     * @param materialCd
     * @param pollutantCd
     * @param factor
     * @param formula
     * @param year
     * @return
     * @throws DAOException
     */
     FireRow[] retrieveFireRow(String sccId, String materialCd,
            String pollutantCd, String measure,
            String action, int year) throws DAOException;
    
    /**
     * @param fire
     * @return
     * @throws DAOException
     */
    boolean modifyFireRow(FireRow fire) throws DAOException;
    
    /**
     * @throws DAOException
     */
    void removeInvalidFireRows(int year) throws DAOException;
    
    /**
     * @throws DAOException
     */
    void deprecateFire(int date) throws DAOException;
    
    /**
     * @throws DAOException
     */
    void deprecateFire(int date, String sccId, List<String> materialCds, List<String> pollutantCds ) throws DAOException;
    
    /**
     * @throws DAOException
     */
    DbInteger activeFireRows() throws DAOException;
    
    /**
     * @throws DAOException
     */
    FireRow[] deprecatedFireRows(int year) throws DAOException;
    
    /**
     * @param  sccId
     * @param year
     */
    DbInteger materialForScc(String sccId, Integer year);
    
    /**
     * @param  sccId
     */
    DefaultStackParms retrieveDefaultStackParms(String sccId) throws DAOException;
    
    /**
     * @param counties
     * @param doLaaCd
     * @param facilityId
     * @param firstYear
     * @param lastYear
     * @return
     * @throws DAOException
     */
    Integer[] searchFacilities(List<String> counties, String doLaaCd, String facilityId, int firstYear, int lastYear) throws DAOException;
    
    /**
     * @param year
     * @return
     * @throws DAOException
     */
    MultiEstablishment[] facilitiesWithEisReports(int year) throws DAOException;
    
    /**
     * @param year
     * @return
     * @throws DAOException
     */
    MultiEstablishment[] activeTvFacilities(int year) throws DAOException;
    
    /**
     * @param facilityId
     * @param fpId
     * @param oldName
     * @return
     * @throws DAOException
     */
    DbInteger[] locateUsingOldEuName(String facilityId, int fpId, String oldName) throws DAOException;

    /**
     * @param r
     * @param r2
     * @param i
     * @param i2
     * @param rptId
     * @return
     * @throws DAOException
     */
    void retrieveReportInvoicePair(EmissionsReport r, EmissionsReport r2, Invoice i, Invoice i2, int rptId) throws DAOException;

    /**
     * @param facilityId
     * @return
     * @throws DAOException
     */
    FacilityPermitInfo[] retrieveFacilityPermitInfo(String facilityId) throws DAOException;

    /**
     * @param facilityId
     * @return
     * @throws DAOException
     */
    FacilityAppInfo[] retrieveFacilityAppInfo(String facilityId) throws DAOException;
    
    /**
     * @param facilityId
     * @return
     * @throws DAOException
     */
    IntegerPair[] retriveFacilityPermitAppInfo(String facilityId) throws DAOException;
    
    /**
     * @param year
     * * @param facilityId
     * @return
     * @throws DAOException
     */
    EmissionsReport retrieveLatestTvEmissionReport(Integer year,
            String facilityId) throws DAOException;
    
    /**
     * @param reportId
     * @param noteId
     * @throws DAOException
     */
	public boolean removeNote(Integer reportId, Integer noteId) throws DAOException;
	
	/**
	 * Update the validated flag for the emissions report identified by
	 * <code>reportId</code> setting it to <code>validated</code>.
	 * 
	 * @param reportId
	 *            id of emissions report to be modified.
	 * @param validated
	 *            value for the validated flag.
	 * @throws DAOException
	 */
	public void setEmissionsReportValidatedFlag(Integer reportId, boolean validated) throws DAOException;

	public List<MissingFIREFactor> retrieveMissingFactors(Integer year, String facilityClass)
			throws DAOException;
	
    /**
     * @param sccId
     * @return
     * @throws DAOException
     */
    public boolean checkFireRowByScc(String sccId) throws DAOException;
    
    /**
     * @param report
     * @return
     * @throws DAOException
     */
    public boolean generateEmissionsInventoryXML(UsEpaEisReport report) throws DAOException;
    
    /**
     * @param report
     * @return
     * @throws DAOException
     */
    public boolean generateFacilityInventoryXML(UsEpaEisReport report) throws DAOException;
    
    /**
     * @param report
     * @return
     * @throws DAOException
     */
    public boolean updateTotalEmissions(EmissionsReport report) throws DAOException;

	List<SCEmissionsReport> retrieveAssociatedSCEmissionsReports(
			EmissionsReport report) throws DAOException;

	void disassociateSCEmissionsReports(EmissionsReport report) throws DAOException;
	
    void associateSCEmissionsReport(EmissionsReport report, SCEmissionsReport sc)
            throws DAOException;

	void associateSCEmissionsReports(EmissionsReport newReport,
			Integer scEmissionsReportId) throws DAOException;
    
	/**
	 * @param facility
	 *            id
	 * @param service
	 *            catalog id
	 */
	DbInteger emissionsInventoriesForFacilityAndServiceCatalog(
			String facilityId, Integer id);
	
	EmissionsRptInfo[] retrieveEmissionsRptInfos2(String facilityId, Integer emissionsReportId)
			throws DAOException;
	
	/**
	 * @param sccId
	 * @return
	 * @throws DAOException
	 * 
	 */
	SimpleDef[] retrieveSccMaterials(String sccId) throws DAOException;
	
	/**
	 * @param sccId
	 * @return
	 * @throws DAOException
	 * 
	 */
	SimpleDef[] retrieveSccPollutants(String sccId) throws DAOException;
	
	/**
	 * @param sccId
	 * @return
	 * @throws DAOException
	 * 
	 */
	SimpleDef[] retrieveSccPollutants(String sccId, List<String> materialCds) throws DAOException;
	
	/**
	 * @param tradeSecretDocId
	 * @return EmissionsDocumentRef
	 * @throws DAOException
	 */
	EmissionsDocumentRef retrieveReportDocumentByTradeSecrectDocId(Integer tradeSecretDocId) throws DAOException;

	Map<String, Integer> retrieveEnabledEmissionRptsForYearAndContentType(Integer year, String contentType)
			throws DAOException;

	ArrayList<Integer> retrieveValidEmissionsReportIds(String facilityId, Integer rptYear, String contentTypeCd)
			throws DAOException;

	void updatePriorEIsToInvalidAfterCsvImport(String facilityId, Integer rptYear, String contentTypeCd,
			Integer currentEmissionsRptId) throws DAOException;
	
	public List<SCEmissionsReport> retrieveAssociatedSCEmissionsReports(Integer rptId) throws DAOException;

	public void setActiveEmissionsReportsValidatedFlag(Integer fpId, boolean validated) throws DAOException;

	boolean deleteAssociatedInvoice(EmissionsReport emissionsReport) throws DAOException;

	boolean deleteEmissionsRptInvoiceNotes(EmissionsReport emissionsReport) throws DAOException;

	boolean deleteEmissionsRptInvoiceDetails(EmissionsReport emissionsReport) throws DAOException;

	boolean deleteReferences(String facilityId) throws DAOException;
}
