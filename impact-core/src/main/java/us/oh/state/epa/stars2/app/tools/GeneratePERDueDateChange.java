package us.oh.state.epa.stars2.app.tools;

import java.io.File;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
import javax.faces.event.ActionEvent;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.document.CorrespondenceDocument;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.BulkDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.PTIOPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEU;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitInfo;
import us.oh.state.epa.stars2.def.CorrespondenceDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.PTIOPERDueDateDef;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitStatusDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

@SuppressWarnings("serial")
public class GeneratePERDueDateChange extends BulkCorrespondence {

    private TemplateDocument templateDoc;
    private String fileBaseName;

    private String tmpDirName;
    private String urlDirName;

    private FacilityList[] facilities;
    private Facility facility;
    private FormResult[] results;
    private String currentFile;

    private String newPerDueDateCd;
    private Correspondence theCorrespondence;

    private BulkOperationsCatalog _catalog;

    public GeneratePERDueDateChange() {
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
        _catalog = catalog;
        return;
    }

    /**
     * Each bulk operation is responsible for providing a search operation based
     * on the paramters gathered by the BulkOperationsCatalog bean. The search
     * is run when the "Select" button on the Bulk Operations screen is clicked.
     * The BulkOperationsCatalog will then display the results of the search.
     * Below the results is a "Bulk Operation" button.
     */
    public final void searchFacilities(BulkOperationsCatalog catalog)
        throws RemoteException {

        this.catalog = catalog;
        
        catalog.getUser();

        setCorrespondenceDate(catalog.getCorrespondenceDate());
        setNewPERDueDateCd(catalog.getNewPerDueDate());

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

        facilities 
            = getFacilityService().searchFacilities(catalog.getFacilityNm(),
                                            catalog.getFacilityId(),
                                            null,
                                            null, null, null, null, null, null,
                                            null, null, null, null,
                                            null, null, true, null);
        
        setMaximum(facilities.length);
        setValue(facilities.length);

        setSearchStarted(true);
        catalog.setFacilities(facilities);
        setSearchCompleted(true);

    }

    /**
     * When the "Bulk Operation" button is clicked, the BulkOperationsCatalog
     * class will call this method, ensure that the BulkOperation class is
     * placed into the jsf context, and return the value of the getNavigation()
     * method to the jsf controller. Any further requests from the page that is
     * navigated to are handled by this bean in the normal way. The default
     * version does no preliminary work.
     */
    public final void performPreliminaryWork(BulkOperationsCatalog catalog) 
        throws RemoteException {

        facilities = catalog.getSelectedFacilities();
        results = new FormResult[1];

        try {
            tmpDirName = getDocumentService().createTmpDir(getCurrentUser());
            urlDirName = tmpDirName.replace('\\', '/');
        }
        catch (Exception e) {
            logger.error("Could not create tmp directory.", e);
        }

        if (facilities.length == 1) {

            try {

                String facilityId = facilities[0].getFacilityId();
                facility = getFacilityService().retrieveFacility(facilityId);
                    
                List<Permit> permits 
                    = getPermitService().search(null, null, null, PermitTypeDef.NSR, null, null, null, null, facilityId,
                                        null, PermitGlobalStatusDef.ISSUED_FINAL, null, null, null, null, true, null);
                DisplayUtil.displayHitLimit(permits.size());
                PTIOPermit ptio = new PTIOPermit();
                ptio.setPermitID(0);    // set dummy permit ID to avoid problems later on
                ArrayList<PermitEU> allEUs = new ArrayList<PermitEU>();

                for (Permit permit : permits) {
                    PermitInfo pi = getPermitService().retrievePermit(permit.getPermitID());
                    permit = pi.getPermit();
                    for (PermitEUGroup euGroup : permit.getEuGroups()) {
                        for (PermitEU permitEU : euGroup.getPermitEUs()) {
                            if (PermitStatusDef.ACTIVE.equals(permitEU.getPermitStatusCd()) ||
                                    PermitStatusDef.EXTENDED.equals(permitEU.getPermitStatusCd()) ||
                                    PermitStatusDef.EXPIRED.equals(permitEU.getPermitStatusCd())) {
                                allEUs.add(permitEU);
                            }
                        }
                    }
                }

                PermitEUGroup allEUGroup = new PermitEUGroup();
                allEUGroup.setIndividualEUGroup();
                allEUGroup.setPermitEUs(allEUs);
                ptio.addEuGroup(allEUGroup);

                String errors = generateForm(facility, ptio);
                results[0] = new FormResult();
                results[0].setId(facilities[0].getName());
                results[0].setFormURL(DocumentUtil.getFileStoreBaseURL() + urlDirName
                                      + "/" + facility.getFacilityId() + "-" + fileBaseName + ".docx");
                results[0].setFileName(facility.getFacilityId() + "-" + fileBaseName + ".docx");
                results[0].setNotes(errors);

                if (getCorrespondenceTypeCode() != null) {
                    addCorrespondence(facilityId, null, tmpDirName + File.separator 
                                      + facility.getFacilityId() + "-" + fileBaseName + ".docx");
                }

            }
            catch (Exception e) {
                String logStr = "Exception " + e.getClass().getName() + ", Msg = "
                    + e.getMessage();
                logger.error(logStr, e);
                results[0].setNotes(logStr);
            }
        }
        else if (facilities.length > 1) {
            throw new RemoteException("Please select a single facility. "
                                      + "A PER due date may be changed for only one facility at a time.");
        }


        return;
    }

    /** Generate a PER form for a facility. */
    protected String generateForm(Facility facility, Permit permit) throws Exception {

        StringBuffer errors = new StringBuffer();

        try {
            DocumentGenerationBean dataBean = new DocumentGenerationBean();
            if (getCorrespondenceDate() != null && getCorrespondenceDate().length() > 0) {
                dataBean.setCorrespondenceDate(getCorrespondenceDate());
            }

//            String perCd = facility.getPERDueDateCd();
//            String mmdd = PTIOPERDueDateDef.getData().getItems().getItemDesc(perCd);
            String newPerDueDate = PTIOPERDueDateDef.getData().getItems().getItemDesc(getNewPERDueDateCd());
            String[] dateStrings = newPerDueDate.split(",");
            String startDate = "";
            String endDate = "";
            String dueDate = "";
            if (dateStrings.length == 2) {
                String[] startEndDates = dateStrings[0].split("\\-");
                if (startEndDates.length == 2) {
                    startDate = startEndDates[0].trim();
                    endDate = startEndDates[1].trim();
                }
                dueDate = dateStrings[1].substring(4); // strip off leading "Due"
            }

            dataBean.setFacility(facility);
//            dataBean.getProperties().put("perDueDate", mmdd);
//            dataBean.getProperties().put("newPerDueDate", newPerDueDate);
            dataBean.getProperties().put("perDueDate", dueDate);
            dataBean.getProperties().put("reportStartDate", startDate);
            dataBean.getProperties().put("reportEndDate", endDate);
            dataBean.setPermit(permit);

            String pathToFile = tmpDirName + File.separator + facility.getFacilityId() 
                + "-" + fileBaseName + ".docx";

            //DocumentUtil.generateDocument(templateDoc.getPath(), dataBean, pathToFile);
            DocumentUtil.generateAsposeDocument(templateDoc.getTemplateDocPath(), dataBean, pathToFile);
            
            CorrespondenceDocument cDoc = new CorrespondenceDocument();
            cDoc.setFacilityID(facility.getFacilityId());
            cDoc.setLastModifiedBy(getCurrentUserId());
            cDoc.setTemporary(true);
            cDoc.setDescription(getCorrespondenceDescription());

            if (pathToFile != null) {
                int extDot = pathToFile.lastIndexOf(".");
                if (extDot > 0) {
                    cDoc.setExtension(pathToFile.substring(extDot + 1));
                }
                InputStream is = DocumentUtil.getDocumentAsStream(pathToFile);
                cDoc = (CorrespondenceDocument) getDocumentService().uploadTempDocument(cDoc, null, is);
            }
            
            theCorrespondence = new Correspondence();
            theCorrespondence.setDocument(cDoc);
            theCorrespondence.setCorrespondenceTypeCode(getCorrespondenceTypeCode());
            theCorrespondence.setFacilityID(facility.getFacilityId());
        }
        catch (Exception e) {
            logger.error("Unable to generate form: " + e.getMessage(), e);
            throw e;
        }

        return errors.toString();
    }

    public void correspondence(ActionEvent actionEvent) throws RemoteException {

        if (getCorrespondenceDate() == null) {
            _catalog.setErrorMessage("Missing Correspondence Date");
            return;
        }

        StringTokenizer st = new StringTokenizer(getCorrespondenceDate(), "/");
        if (st.countTokens() != 3) {
            _catalog.setErrorMessage("Correspondence Date should be in the form MM/DD/YYYY");
            return;
        }
        try {
            int month = Integer.parseInt(st.nextToken()) - 1;
            int day = Integer.parseInt(st.nextToken());
            int year = Integer.parseInt(st.nextToken());
            
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            
            Timestamp ts = new Timestamp(cal.getTimeInMillis());

            theCorrespondence.setDateGenerated(ts);
        }
        catch (Exception e) {
            String logStr = "A problem was encountered while attemting to change the PER due date. "
                + e.getMessage();
            logger.error(logStr, e);
            _catalog.setErrorMessage(logStr);
            return;
        }
        setCorrespondenceSent(true);
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
        try {
            DocumentUtil.rmDir(tmpDirName);
        }
        catch (Exception e) {
            logger.error("Unable to remove temporary directory " + tmpDirName, e);
        }
        return SUCCESS;
    }

    private String getNewPERDueDateCd() {
        if (newPerDueDateCd == null) {
            setNewPERDueDateCd("0");
        }
        return newPerDueDateCd;
    }

    private void setNewPERDueDateCd(String dueDateCd) {
        if (dueDateCd == null) {
            newPerDueDateCd = "0";
        }
        newPerDueDateCd = dueDateCd;
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
