package us.oh.state.epa.stars2.app.tools;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Calendar;

import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.BulkDef;
import us.oh.state.epa.stars2.def.CorrespondenceDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

public class GenerateBulkEmissionsReport extends BulkCorrespondence {

    private TemplateDocument templateDoc;
    private String fileBaseName;

    private String tmpDirName;
    private String urlDirName;
    private EmissionsReportSearch[] emissionsReports;
    private FormResult[] results;
    private String currentFile;

    public GenerateBulkEmissionsReport() {
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
    public EmissionsReportSearch[] searchEmissionsReports(BulkOperationsCatalog catalog)
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

        EmissionsReportSearch ers = new EmissionsReportSearch();
        ers.setFacilityId(catalog.getFacilityId());
        ers.setFacilityName(catalog.getFacilityNm());
        ers.setCountyCd(catalog.getCounty());
        ers.setDolaaCd(catalog.getDoLaa());
        ers.setYear(catalog.getYear());
        ers.setUnlimitedResults(unlimitedResults());

        emissionsReports = getEmissionsReportService().searchEmissionsReports(ers, false);

        setMaximum(emissionsReports.length);
        setValue(emissionsReports.length);

        setSearchStarted(true);
        setSearchCompleted(true);

        //DisplayUtil.displayHitLimit(emissionsReports.length);
        return emissionsReports;
    }

    /**
     * When the "Bulk Operation" button is clicked, the BulkOperationsCatalog
     * class will call this method, ensure that the BulkOperation class is
     * placed into the jsf context, and return the value of the getNavigation()
     * method to the jsf controller. Any further requests from the page that is
     * navigated to are handled by this bean in the normal way. The default
     * version does no preliminary work.
     */
    public final void performPreliminaryWork(BulkOperationsCatalog catalog) throws IOException {

        emissionsReports = catalog.getSelectedEmissionsReports();
        results = new FormResult[emissionsReports.length + 1];

        try {
            tmpDirName = getDocumentService().createTmpDir(getCurrentUser());
            urlDirName = tmpDirName.replace('\\', '/');
        }
        catch (Exception e) {
            logger.error("Could not create tmp directory.", e);
        }

        if (emissionsReports.length != 0) {
        	DisplayUtil.displayInfo("Generating Documents. This may take several moments. "
                                        + "You may cancel the operation by pressing the \"Reset\" button.");
        	
            for (int i = 0; i < emissionsReports.length; i++) {
                try {
                    String facilityId = emissionsReports[i].getFacilityId();
                    Facility facility = getFacilityService().retrieveFacility(facilityId);
                    EmissionsReport er 
                        = getEmissionsReportService().retrieveEmissionsReport(emissionsReports[i].getEmissionsRptId(),
                                                                      false);

                    String errors = generateForm(facility, er);  // currently returns no errors.
                    results[i+1] = new FormResult();
                    results[i+1].setId(facility.getName());
                    results[i+1].setFormURL(DocumentUtil.getFileStoreBaseURL() + urlDirName
                                            + "/" + facility.getFacilityId() + "-" + fileBaseName + ".docx");
                    results[i+1].setFileName(facility.getFacilityId() + "-" + fileBaseName + ".docx");
                    results[i+1].setNotes(errors);

                    if (getCorrespondenceTypeCode() != null) {
                        addCorrespondence(facilityId, null, tmpDirName + File.separator 
                                          + facility.getFacilityId() + "-" + fileBaseName + ".docx");
                    }

                }catch (IOException ioe) {
					String logStr = "Exception type = "
							+ ioe.getClass().getName() + ", Msg = "
							+ ioe.getMessage();
					logger.error(logStr, ioe);
					if (results[i + 1] != null) {
						results[i + 1].setNotes(logStr);
					}
					throw (ioe);
				} catch (Exception e) {
					String logStr = "Exception type = "
							+ e.getClass().getName() + ", Msg = "
							+ e.getMessage();
					logger.error(logStr, e);
					if (results[i + 1] != null) {
						results[i + 1].setNotes(logStr);
					}
				}
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
        return null;
    }

    /** Generate a form for a facility and/or permit. */
    protected String generateForm(Facility facility, EmissionsReport report) throws Exception {

        StringBuffer errors = new StringBuffer();

        try {
            DocumentGenerationBean dataBean = new DocumentGenerationBean();
            if (getCorrespondenceDate() != null && getCorrespondenceDate().length() > 0) {
                dataBean.setCorrespondenceDate(getCorrespondenceDate());
            }
            dataBean.setFacility(facility);
            dataBean.setEmissionsReport(report);
            /*DocumentUtil.generateDocument(templateDoc.getPath(), dataBean, tmpDirName
                                          + File.separator + facility.getFacilityId() + "-"
                                          + fileBaseName + ".docx");*/
            DocumentUtil.generateAsposeDocument(templateDoc.getTemplateDocPath(), dataBean, tmpDirName
                    + File.separator + facility.getFacilityId() 
                    + "-" + fileBaseName + ".docx");
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            throw e;
        }

        return errors.toString();
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
