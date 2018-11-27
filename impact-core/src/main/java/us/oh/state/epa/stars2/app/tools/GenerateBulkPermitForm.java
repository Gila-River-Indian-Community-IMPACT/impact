package us.oh.state.epa.stars2.app.tools;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.List;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.BulkDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.CorrespondenceDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentUtil;

@SuppressWarnings("serial")
public class GenerateBulkPermitForm extends BulkCorrespondence {

    private TemplateDocument templateDoc;
    private String fileBaseName;

    private String tmpDirName;
    private String urlDirName;

    private Permit[] permits;

    private FormResult[] results;

    private String currentFile;

    private String reportYear;

    public GenerateBulkPermitForm() {
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


    public final void searchPermits(BulkOperationsCatalog catalog)
        throws RemoteException {

        this.catalog = catalog;
        
        setCorrespondenceDate(catalog.getCorrespondenceDate());

        BulkDef bulkOpDef = catalog.getBulkDef();

        if (catalog.getYear() != null) {
            reportYear = catalog.getYear().toString();
        }

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

        List<Permit> pList = getPermitService().search(catalog.getApplicationNumber(), 
                                               null, null,
                                               catalog.getPermitType(),
                                               catalog.getPermitReason(),
                                               null,
                                               null,
                                               catalog.getPermitNumber(),
                                               catalog.getFacilityId(),
                                               catalog.getFacilityNm(),
                                               catalog.getPermitStatusCd(),
                                               null, null, null, null, true, null);

        setMaximum(pList.size());
        setValue(pList.size());

        setSearchStarted(true);
        catalog.setPermits(pList.toArray(new Permit[0]));
        setSearchCompleted(true);

        return;
            
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

        FacilityService facBO = null;

        try {
            facBO = getFacilityService();
        }
        catch (Exception e) {
            logger.error("Exception caught while fetching services, msg = "
                         + e.getMessage(), e);
            return;
        }

        permits = catalog.getSelectedPermits();
        results = new FormResult[permits.length + 1];

        try {
            tmpDirName = getDocumentService().createTmpDir(getCurrentUser());
            if (tmpDirName != null){
                urlDirName = tmpDirName.replace('\\', '/');
            }else{
                urlDirName = "";
                tmpDirName = "";
            }
        }
        catch (Exception e) {
            logger.error("Could not create tmp directory.", e);
        }

        if (permits.length != 0) {

            for (int i = 0; i < permits.length; i++) {
                try {

                    String facilityId = permits[i].getFacilityId();
                    Facility facility = facBO.retrieveFacility(facilityId);
                    Permit tp = getPermitService().retrievePermit(permits[i].getPermitID()).getPermit();

                    String errors = generateForm(tp, facility);
                    results[i+1] = new FormResult();
                    results[i+1].setId(permits[i].getPermitNumber());
                    results[i+1].setFormURL(DocumentUtil.getFileStoreBaseURL() + urlDirName
                                            + "/" + permits[i].getPermitNumber() + "-" + fileBaseName + ".docx");
                    results[i+1].setFileName(permits[i].getPermitNumber() + "-" + fileBaseName + ".docx");
                    results[i+1].setNotes(errors);

                    if (getCorrespondenceTypeCode() != null) {
                        addCorrespondence(facilityId, permits[i].getPermitNumber(),
                                          tmpDirName + File.separator 
                                          + permits[i].getPermitNumber() + "-" + fileBaseName + ".docx");
                    }

                }
                catch (Exception e) {
                    String logStr = "Exception " + e.getClass().getName() + ", Msg = "
                        + e.getMessage();
                    logger.error(logStr, e);
                    results[i+1].setNotes(logStr);
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

    /** Generate a form for a facility and/or permit. */
    protected String generateForm(Permit permit, Facility facility) throws Exception {

        StringBuffer errors = new StringBuffer();

        try {
            DocumentGenerationBean dataBean = new DocumentGenerationBean();
            if (getCorrespondenceDate() != null && getCorrespondenceDate().length() > 0) {
                dataBean.setCorrespondenceDate(getCorrespondenceDate());
            }
            dataBean.setPermit(permit);
            dataBean.setFacility(facility);
            if (reportYear != null) {
                dataBean.getProperties().put("reportYear", reportYear);
            }
            /*DocumentUtil.generateDocument(templateDoc.getPath(), dataBean, tmpDirName
                                          + File.separator + permit.getPermitNumber()
                                          + "-" + fileBaseName + ".docx");*/
            DocumentUtil.generateAsposeDocument(templateDoc.getTemplateDocPath(), dataBean, tmpDirName
                    + File.separator + permit.getPermitNumber() 
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
