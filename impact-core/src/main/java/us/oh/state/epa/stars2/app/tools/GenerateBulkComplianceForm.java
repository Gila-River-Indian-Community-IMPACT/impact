package us.oh.state.epa.stars2.app.tools;

import java.io.File;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.BulkDef;
import us.oh.state.epa.stars2.def.ComplianceReportTypeDef;
import us.oh.state.epa.stars2.def.CorrespondenceDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentUtil;

public class GenerateBulkComplianceForm extends BulkCorrespondence {

    private TemplateDocument templateDoc;
    private String fileBaseName;

    private String tmpDirName;
    private String urlDirName;
    private String currentFile;

    private ComplianceReportList[] reports;
    private FormResult[] results;
    private String reportYear;

    public GenerateBulkComplianceForm() {
        super();
        setButtonName("Generate Document(s)");
        setNavigation("dialog:generateBulkForm");
    }

    /**
     * Initialize the BulkOperation. Typically, the BulkOperation may need to
     * initialize default values in one or more of the BulkOperationsCatalog
     * search fields. This method is called by the BulkOperationsCatalog after
     * the BulkOperation is selected, but before the search screen fields are
     * displayed.
     */
    public final void init(BulkOperationsCatalog catalog) {
        return;
    }

    /**
     * Each bulk operation is responsible for providing a search operation based
     * on the paramters gathered by the BulkOperationsCatalog bean. The search
     * is run when the "Select" button on the Bulk Operations screen is clicked.
     * The BulkOperationsCatalog will then display the results of the search.
     * Below the results is a "Bulk Operation" button.
     */
    /**
     * @param catalog
     * @return
     * @throws RemoteException
     */
    public ComplianceReportList[] searchComplianceReports(BulkOperationsCatalog catalog)
        throws RemoteException {

        this.catalog = catalog;
        
        setCorrespondenceDate(catalog.getCorrespondenceDate());

        BulkDef bulkOpDef = catalog.getBulkDef();
        setCorrespondenceTypeCode(bulkOpDef.getCorrespondenceTypeCd());
        DefSelectItems templateDocTypeCdItems = CorrespondenceDef.getTemplateDocTypeCdData().getItems();
        String templateDocTypeCd = templateDocTypeCdItems.getItemDesc(bulkOpDef.getCorrespondenceTypeCd());

        if (templateDocTypeCd != null) {
            templateDoc = getDocumentService().retrieveTemplateDocument(templateDocTypeCd);
        }
        String fPath = templateDoc.getTemplateDocPath();
        int lastSlash = fPath.lastIndexOf('/');
        if (lastSlash < 0) {
            lastSlash = fPath.lastIndexOf('\\');
        }
        int lastDot = fPath.lastIndexOf('.');
        fileBaseName = fPath.substring(lastSlash + 1, lastDot);

        if (catalog.getYear() != null) {
            reportYear = catalog.getYear().toString();
        }

        reports = getComplianceReportService().searchComplianceReports(null, catalog.getFacilityId(),
                                                               catalog.getFacilityNm(),
                                                               null,
                                                               /* ComplianceReportTypeDef.COMPLIANCE_TYPE_TVCC */ null,
                                                               null,  // String reportStatus
                                                               reportYear,
                                                               null,  // String deviationsReported 
                                                               null,  // ByDate
                                                               null,  // Date beginDt
                                                               null,  // Date endDt
                                                               null,  // String reportAccepted
                                                               null,  // String otherTypeCd
                                                               true, // unlimitedResults
                                                               null, // String cmpId
                                                               null, // permitClassCd
                                                               null, // facilityTypeCd
                                                               null); // dapcReviewComments
        setMaximum(reports.length);
        setValue(reports.length);

        setSearchStarted(true);
        setSearchCompleted(true);

        return reports;
    }

    /**
     * When the "Bulk Operation" button is clicked, the BulkOperationsCatalog
     * class will call this method, ensure that the BulkOperation class is
     * placed into the jsf context, and return the value of the getNavigation()
     * method to the jsf controller. Any further requests from the page that is
     * navigated to are handled by this bean in the normal way. The default
     * version does no preliminary work.
     */
    public final void performPreliminaryWork(BulkOperationsCatalog catalog) {

        reports = catalog.getSelectedComplianceReports();
        results = new FormResult[reports.length + 1];

        if (reports.length != 0) {

            try {
                tmpDirName = getDocumentService().createTmpDir(getCurrentUser());
                urlDirName = tmpDirName.replace('\\', '/');
            }
            catch (Exception e) {
                logger.error("Could not create tmp directory.", e);
                results = null;
                return;
            }
            
            for (ComplianceReportList crl : reports) {
                
                int i = 0;
                
                try {
                    
                    String facilityId = crl.getFacilityId();
                    Facility facility = getFacilityService().retrieveFacility(facilityId);
                    
                    String errors = generateForm(facility, crl);
                    results[i+1] = new FormResult();
                    results[i+1].setId(facilityId);
                    results[i+1].setFormURL(DocumentUtil.getFileStoreBaseURL() + urlDirName
                                            + "/" + facilityId + "-" + fileBaseName + ".docx");
                    results[i+1].setFileName(facilityId + "-" + fileBaseName + ".docx");
                    results[i+1].setNotes(errors);
                    
                    addCorrespondence(facilityId, null, tmpDirName + File.separator 
                                      + facilityId + "-" + fileBaseName + ".docx");
                    
                }
                catch (Exception e) {
                    String logStr = "Exception " + e.getClass().getName() + ", Msg = "
                        + e.getMessage();
                    logger.error(logStr, e);
                    results[i+1].setNotes(logStr);
                }
                
                i++;
            }
            
            try {
                
                results[0] = new FormResult();
                results[0].setId("Zip File");
                results[0].setNotes("All Forms");
                Calendar cal = Calendar.getInstance();
                String date = Integer.toString(cal.get(Calendar.YEAR)) + "-" 
                    + Integer.toString((cal.get(Calendar.MONTH) + 1)) + "-"
                    + Integer.toString(cal.get(Calendar.DAY_OF_MONTH)) + "-"
                    + Integer.toString(cal.get(Calendar.HOUR_OF_DAY)) + "-"
                    + Integer.toString(cal.get(Calendar.MINUTE)) + "-"
                    + Integer.toString(cal.get(Calendar.SECOND));
                String zipFileName = fileBaseName + "-" + date + ".zip";
                DocumentUtil.createZipFile(tmpDirName, 
                                           File.separator + "tmp" + File.separator 
                                           + getCurrentUser() + File.separator + zipFileName,
                                           fileBaseName);
                results[0].setFormURL(DocumentUtil.getFileStoreBaseURL() + "/tmp/" + getCurrentUser()
                                      + "/" + zipFileName);
                results[0].setFileName(fileBaseName + ".zip");
            }
            catch (Exception e) {
                String logStr = "Exception " + e.getClass().getName() + ", Msg = "
                    + e.getMessage();
                logger.error(logStr, e);
                results[0].setNotes(logStr);
            }
        }

        return;
    }

    /** Generate a form for a facility and/or permit. */
    protected String generateForm(Facility facility, ComplianceReportList crl) throws Exception {

        StringBuffer errors = new StringBuffer();

        try {
            DocumentGenerationBean dataBean = new DocumentGenerationBean();
            if (getCorrespondenceDate() != null && getCorrespondenceDate().length() > 0) {
                dataBean.setCorrespondenceDate(getCorrespondenceDate());
            }

            dataBean.setFacility(facility);

            String tvccRcvdDate = " ";
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            if (crl.getReceivedDate() != null) {
                tvccRcvdDate = df.format(crl.getReceivedDate());
            }
            dataBean.getProperties().put("tvccRcvdDate", tvccRcvdDate);
            if (reportYear != null) {
                dataBean.getProperties().put("reportYear", reportYear);
            }
           /* DocumentUtil.generateDocument(templateDoc.getPath(), dataBean, tmpDirName
                                          + File.separator + facility.getFacilityId()
                                          + "-" + fileBaseName + ".docx");*/
            
            DocumentUtil.generateAsposeDocument(templateDoc.getTemplateDocPath(), dataBean, tmpDirName
                    + File.separator + facility.getFacilityId() 
                    + "-" + fileBaseName + ".docx");
            
        }
        catch (Exception e) {
            throw e;
        }

        return errors.toString();
    }

    /**
     * When the "Apply Operation" button is clicked, the BulkOperationsCatalog
     * class will call this method and return the value of the getNavigation()
     * method to the jsf controller. Any further requests from the page that is
     * navigated to are handled by this (the derived) class in the normal way.
     * The default version does no preliminary work.
     * 
     * @param catalog
     * @return
     */
    public String performPostWork(BulkOperationsCatalog catalog) {
        try {
            DocumentUtil.rmDir(tmpDirName);
        }
        catch (Exception e) {
            logger.error("Unable to remove temporary directory " + tmpDirName, e);
        }
        return SUCCESS;
    }

    public final FormResult[] getResults() {
        return results;
    }

    public final void setFile(String fileName) {
        currentFile = fileName;
    }

    public final String getFile() {
        return currentFile;
    }

}
