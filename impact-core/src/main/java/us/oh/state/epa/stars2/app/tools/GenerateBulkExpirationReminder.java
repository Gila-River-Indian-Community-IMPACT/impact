package us.oh.state.epa.stars2.app.tools;

import java.io.File;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dao.permit.PermitSQLDAO;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplication;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.BulkDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEU;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEUGroup;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.CorrespondenceDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.ExemptStatusDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitStatusDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.TVApplicationPurposeDef;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentUtil;

@SuppressWarnings("serial")
public class GenerateBulkExpirationReminder extends BulkCorrespondence {

    /** 
     * PTIO_EXP const must match key id cm_bulk_def.bulk_id for
     * PTIO/PTO - Expiration Notices.
     */
    public static int PTIO_EXP = 310;

    /** 
     * TV18MO_EXP const must match key id cm_bulk_def.bulk_id for
     * Title V Permit Renewal App Reminder (18 month).
     */
    public static int TV18MO_EXP = 311;

    /** 
     * TV9MO_EXP const must match key id cm_bulk_def.bulk_id for
     * Title V Permit Renewal App Reminder (9 month).
     */
    public static int TV9MO_EXP = 312;
    
    /** 
     * TIV_EXP const must match key id cm_bulk_def.bulk_id for
     * Title IV Acid Rain Permit Expiration Notice.
     */
    //public static int TIV_EXP = 315;

    /** 
     * PTIO_APP_MISSING const must match key id cm_bulk_def.bulk_id for
     * NOV - PTIO Renewal Not Submitted.
     */
    public static int PTIO_APP_MISSING = 230;

    /** 
     * TV_APP_LATE const must match key id cm_bulk_def.bulk_id for
     * NOV - Title V Renewal Submitted Late.
     */
    public static int TV_APP_LATE = 231;

    /** 
     * TV_APP_MISSING const must match key id cm_bulk_def.bulk_id for
     * NOV - Title V Renewal Not Submitted.
     */
    public static int TV_APP_MISSING = 232;

    private Integer bulkID;
    private TemplateDocument templateDoc;
    private String fileBaseName;

    private String tmpDirName;
    private String urlDirName;
    private String currentFile;

    private ArrayList<FacilityPermitData> filteredFacilities;
    private FormResult[] results;

    private static long MILLIS_PER_DAY = 86400000L;
    private String permitType;
    private Timestamp startTime;
    private Timestamp endTime;

    public GenerateBulkExpirationReminder() {
        super();
        setButtonName("Generate Bulk Expiration Reminder");
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
    public final void searchFacilities(BulkOperationsCatalog catalog)
        throws RemoteException {

        this.catalog = catalog;

        setCorrespondenceDate(catalog.getCorrespondenceDate());

        BulkDef bulkOpDef = catalog.getBulkDef();
        bulkID = bulkOpDef.getBulkId();

        Calendar cal = Calendar.getInstance();
        long now = cal.getTimeInMillis();
        startTime = new Timestamp(now);

        if (bulkID.intValue() == PTIO_EXP) {
            endTime = new Timestamp(now + (200L * MILLIS_PER_DAY));
        }
        else if (bulkID.intValue() == PTIO_APP_MISSING) {
            endTime = startTime;
            startTime = null;
        }
        else if (bulkID.intValue() == TV_APP_LATE) {
            permitType = PermitTypeDef.TV_PTO;
            endTime = startTime;
            startTime = null;
        }
        else if (bulkID.intValue() == TV_APP_MISSING) {
            permitType = PermitTypeDef.TV_PTO;
            endTime = startTime;
            startTime = null;
        }
        else if (bulkID.intValue() == TV18MO_EXP) {
            permitType = PermitTypeDef.TV_PTO;
            endTime = new Timestamp(now + (560L * MILLIS_PER_DAY));
        }
        else if (bulkID.intValue() == TV9MO_EXP) {
            permitType = PermitTypeDef.TV_PTO;
            endTime = new Timestamp(now + (300L * MILLIS_PER_DAY));
        }
        //else if (bulkID.intValue() == TIV_EXP) {
        //   permitType = PermitTypeDef.TIV_PTO;
        //    endTime = new Timestamp(now + (560L * MILLIS_PER_DAY));
        //}

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

        FacilityList[] facilities
            = getFacilityService().searchFacilities(catalog.getFacilityNm(),
                                            catalog.getFacilityId(),
                                            null,
                                            null,
                                            catalog.getCounty(),
                                            catalog.getOperatingStatus(),
                                            catalog.getDoLaa(),
                                            catalog.getNaicsCd(),
                                            catalog.getPermittingClassCd(),
                                            catalog.getTvPermitStatus(),
                                            null,
                                            catalog.getCity(),
                                            catalog.getZipCode(), null, null, true, null);

        if (facilities.length == 0) {
            setSearchStarted(true);
            catalog.setFacilities(null);
            setSearchCompleted(true);
            return;
        }

        setMaximum(facilities.length);
        setValue(0);

        setSearchStarted(true);
        filteredFacilities = filterFacilities(facilities, catalog);
        catalog.setFacilities(filteredFacilities.toArray(new FacilityList[0]));
        setSearchCompleted(true);

    }

    private ArrayList<FacilityPermitData> filterFacilities(FacilityList[] facilityBaseList,
                                                           BulkOperationsCatalog catalog) {

        ArrayList<FacilityPermitData> filteredFacilities = new ArrayList<FacilityPermitData>();

        //FacilityService facBO = null;
        PermitService pmtBO = null;
        //CorrespondenceService corrBO = null;
        //ApplicationService appBO = null;

        try {
            //facBO = getFacilityService();
            pmtBO = getPermitService();
            //appBO = getApplicationService();
            //corrBO = getCorrespondenceService();
        }
        catch (Exception e) {
            logger.error("Exception caught while fetching services, msg = "
                         + e.getMessage(), e);
            return null;
        }

        int i = 0;
        for (FacilityList facility : facilityBaseList) {
            try {
                //if (catalog.getSearchThread().isInterrupted()) {
                //    return new ArrayList<FacilityPermitData>();
                //}

                // Ignore if facility is shutdown.
                if (facility.getOperatingStatusCd() != null 
                    && facility.getOperatingStatusCd().equals(OperatingStatusDef.SD)) {
                    setValue(++i);
                    continue;
                }

                // Ignore if Title V reminder and not a Title V facility.
                if ((bulkID.intValue() == TV18MO_EXP 
                        || bulkID.intValue() == TV9MO_EXP
                        //||bulkID.intValue() == TIV_EXP
                        || bulkID.intValue() == TV_APP_LATE 
                        || bulkID.intValue() == TV_APP_MISSING)
                    && (facility.getPermitClassCd() == null 
                        || !facility.getPermitClassCd().equals(PermitClassDef.TV))) {
                    setValue(++i);
                    continue;
                }
                
                // ignore if Facility inventory Title V Permit Status is active or none
                // for TV_APP_LATE and TV_APP_MISSING
                if ((bulkID.intValue() == TV_APP_LATE || bulkID.intValue() == TV_APP_MISSING) &&
                        ("N".equals(facility.getPermitStatusCd()) || "A".equals(facility.getPermitStatusCd()))) {
                    setValue(++i);
                    continue;
                }
                // Or vise versa. Er, no. Don't do this!
                //if ((bulkID.intValue() == PTIO_EXP || bulkID.intValue() == PTIO_APP_MISSING) 
                //    && (facility.getPermitClassCd() == null 
                //        || facility.getPermitClassCd().equals(PermitClassDef.TV))) {
                //    setValue(++i);
                //    continue;
                //}
                
                String facilityId = facility.getFacilityId();

                List<Permit> permits 
                    = getPermitService().search(null, null, null, permitType,
                                        null, null, null, null, facilityId,
                                        null, PermitGlobalStatusDef.ISSUED_FINAL,
                                        PermitSQLDAO.EXPIRATION_DATE, startTime, endTime, null, true, null);

                // Ignore facility if it has no permits.
                if (permits.isEmpty()) {
                    setValue(++i);
                    continue;
                }


                FacilityPermitData fpd = new FacilityPermitData(facility);
                logger.debug("***** Facility ID = " + facility.getFacilityId() + " *****");
                for (Permit candidate : permits) {

                    candidate = pmtBO.retrievePermit(candidate.getPermitNumber());
                    
                    // PTIO/PTO Expiration Notices should only be pulling up
                    // expiring legacy state PTOs or PTIOs #2024
                    if (bulkID.intValue() == PTIO_EXP 
                            && (!PermitTypeDef.NSR.equals(candidate.getPermitType()))) {
                        continue;
                    }
                    
                    // Is the permit active?
                    boolean hasActive = false;
                    boolean isSuperseded = false;
                    for (PermitEUGroup peug : candidate.getEuGroups()) {
                        for (PermitEU peu : peug.getPermitEUs()) {
                            EmissionUnit feu = peu.getFpEU();
                            // EU is not exempt or de minimis (facility inventory)
                            // EU is not shutdown
                            if ((feu.getExemptStatusCd() != null && !ExemptStatusDef.NA.equals(feu.getExemptStatusCd()))
                                    || OperatingStatusDef.SD.equals(feu.getOperatingStatusCd()))
                                continue;

                            if (PermitStatusDef.ACTIVE.equals(peu.getPermitStatusCd()) 
                                    || PermitStatusDef.EXTENDED.equals(peu.getPermitStatusCd())) {
                                hasActive = true;
                                break;
                            }
                            if (PermitStatusDef.SUPERSEDED.equals(peu.getPermitStatusCd())) {
                            	isSuperseded = true;
                            	break;
                            }
                        }
                        // if one EU Group is active, they will all be
                        // so we can break the loop if this is the case
                        if (hasActive) {
                            break;
                        }
                    }
                    // skip superseded permits
                    if (isSuperseded) {
                    	logger.debug(" Excluding superseded permit " + candidate.getPermitNumber() + 
                    			" from expiration reminder.");
                    	continue;
                    }
                    // for all the others, only consider active permits
                    if (bulkID.intValue() != TV_APP_LATE && bulkID.intValue() != TV_APP_MISSING && !hasActive) {
                        logger.debug("Canditate permit " + candidate.getPermitNumber() + " discarded - not active.");
                        continue;
                    }
                    
                    // Have we already sent this reminder for this permit (Reminders only)?
                    if (bulkID.intValue() == PTIO_EXP || bulkID.intValue() == TV18MO_EXP
                        || bulkID.intValue() == TV9MO_EXP 
                        //|| bulkID.intValue() == TIV_EXP
                        || bulkID.intValue() == TV_APP_LATE || bulkID.intValue() == TV_APP_MISSING) {
                        Correspondence[] allCorrespondence 
                            = getCorrespondenceService().searchCorrespondenceByFacility(facilityId);
                        Correspondence corrMatch = null;
                        boolean alreadyGenerated = false;
                        for (Correspondence corr : allCorrespondence) {
                            if (corr.getCorrespondenceTypeCode().equals(getCorrespondenceTypeCode())
                                && corr.getAdditionalInfo() != null 
                                && corr.getAdditionalInfo().contains(candidate.getPermitNumber())) {
                                corrMatch = corr;
                                break;
                            }
                        }
                        if (corrMatch != null) {
                            alreadyGenerated = true;
                            // for TV_APP_LATE and TV_APP_MISSING, previous correspondence is no longer
                            // good if it was issued more than 2 years after the permit expiration date
                            if (bulkID.intValue() == TV_APP_LATE || bulkID.intValue() == TV_APP_MISSING) {
                                GregorianCalendar correspondenceExpDate = new GregorianCalendar();
                                correspondenceExpDate.setTime(candidate.getExpirationDate());
                                correspondenceExpDate.add(Calendar.YEAR, 2);
                                if (corrMatch.getDateGenerated().after(correspondenceExpDate.getTime())) {
                                    alreadyGenerated = false;
                                }
                            }
                        }
                        if (alreadyGenerated) {
                            logger.debug("Canditate permit " + candidate.getPermitNumber() + " discarded - correspondence already issued.");
                            continue;
                        }
                    }
                    
                    TVApplication renewalApp = null;
                    boolean hasValidRenewal = false;
                    if (bulkID.intValue() == TV_APP_LATE || bulkID.intValue() == TV_APP_MISSING) {
                        // see if there is a renewal app for this permit
                        ApplicationSearchResult[] permitApps = 
                            getApplicationService().retrieveApplicationSearchResults(null, null, 
                                    facility.getFacilityId(), null, null, null, null, null, false, 
                                    null, null, null, true);
                        for (ApplicationSearchResult appResult : permitApps) {
                            if (ApplicationTypeDef.TITLE_V_APPLICATION.equals(appResult.getApplicationTypeCd())) {
                                TVApplication app = (TVApplication)getApplicationService().retrieveApplication(appResult.getApplicationId());
                                if (TVApplicationPurposeDef.RENEWAL.equals(app.getTvApplicationPurposeCd())) {
                                    renewalApp = app;
                                    GregorianCalendar expLowerBound = new GregorianCalendar();
                                    expLowerBound.setTime(candidate.getExpirationDate());
                                    if (bulkID.intValue() == TV_APP_LATE) {
                                        //renewal is late if it is submitted after the
                                        // date which is 180 days before the permit expiration date
                                        expLowerBound.add(Calendar.DAY_OF_YEAR, -180);
                                        expLowerBound.set(Calendar.HOUR_OF_DAY, 22);
                                        expLowerBound.set(Calendar.MINUTE, 59);
                                        expLowerBound.set(Calendar.SECOND, 59);
                                        hasValidRenewal = !renewalApp.getReceivedDate().after(expLowerBound.getTime());
                                    } else if (bulkID.intValue() == TV_APP_MISSING) {
                                        // any renewal received more than 540 days before the permit
                                        // expiration date is not considered a valid renewal
                                        expLowerBound.add(Calendar.DAY_OF_YEAR, -540);
                                        hasValidRenewal = renewalApp.getReceivedDate().after(expLowerBound.getTime());
                                    }
                                }
                            }
                            if (hasValidRenewal) {
                                // no need to process other apps once
                                // valid renewal is found
                                break;
                            }
                        }
                    }
                    
                    if (bulkID.intValue() == TV_APP_LATE && renewalApp == null) {
                        logger.debug("No renewal app exists for Permit " 
                                + candidate.getPermitNumber()
                                + " for facility " + facility.getFacilityId());
                        continue;
                    }
                    
                    if (hasValidRenewal) {
                        logger.debug("Permit " + candidate.getPermitNumber()
                                + " for facility " + facility.getFacilityId()
                                + " was renewed by application " + renewalApp.getApplicationNumber()
                                + " on " + renewalApp.getReceivedDate());
                        continue;
                    }

                    fpd.addPermit(candidate);

                }

                if (fpd.getPermits() != null && fpd.getPermits().length > 0) {
                    filteredFacilities.add(fpd);
                }

            }
            catch (Exception e) {
                logger.error("Exception caught while filtering facilities, msg = "
                             + e.getMessage(), e);
            }
            
            setValue(++i);
        }

        return filteredFacilities;
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
        
        FacilityList[] facilities = catalog.getSelectedFacilities();
        results = new FormResult[facilities.length + 1];

        try {
            tmpDirName = getDocumentService().createTmpDir(getCurrentUser());
            urlDirName = tmpDirName.replace('\\', '/');
        }
        catch (Exception e) {
            logger.error("Could not create tmp directory.", e);
        }

        if (facilities.length != 0) {

            for (int i = 0; i < facilities.length; i++) {
                try {

                    FacilityPermitData fpd = null;
                    for (FacilityPermitData fpData : filteredFacilities) {
                        if (fpData.getFacilityId().equals(facilities[i].getFacilityId())) {
                            fpd = fpData;
                            break;
                        }
                    }

                    String facilityId = facilities[i].getFacilityId();
                    Facility facility = getFacilityService().retrieveFacility(facilityId);

                    String permitList = "";
                    Permit concatenatedPermit = null;
                    ArrayList<PermitEU> allEUs = new ArrayList<PermitEU>();

                    for (Permit permit : fpd.getPermits()) {
                        // Create a single list of permit numbers for the correspondence.
                        if (permitList.length() > 0) {
                            permitList = permitList + " ";
                        }
                        permitList = permitList + permit.getPermitNumber();
                        // If we have a PTIO, we want to create a single fake PTIO which
                        // will concatenate the EUs so we can have a single form with
                        // the facility and all permits.
                        if (bulkID.intValue() == PTIO_EXP || bulkID.intValue() == PTIO_APP_MISSING) {
                            if (concatenatedPermit == null) {
                                concatenatedPermit = permit;
                            }
                            for (PermitEUGroup euGroup : permit.getEuGroups()) {
                                allEUs.addAll(euGroup.getPermitEUs());
                            }
                        }
                        else {
                            // Should only have one TV permit.
                            concatenatedPermit = permit;
                            break;
                        }
                    }

                    if (bulkID.intValue() == PTIO_EXP || bulkID.intValue() == PTIO_APP_MISSING) {
                        PermitEUGroup allEUGroup = new PermitEUGroup();
                        allEUGroup.setIndividualEUGroup();
                        allEUGroup.setPermitEUs(allEUs);
                        concatenatedPermit.addEuGroup(allEUGroup);
                    }

                    String errors = generateForm(facility, concatenatedPermit);
                    results[i+1] = new FormResult();
                    results[i+1].setId(facilities[i].getName());
                    results[i+1].setFormURL(DocumentUtil.getFileStoreBaseURL() + urlDirName
                                            + "/" + facility.getFacilityId() + "-" + fileBaseName + ".docx");
                    results[i+1].setFileName(facility.getFacilityId() + "-" + fileBaseName + ".docx");
                    results[i+1].setNotes(errors);

                    if (getCorrespondenceTypeCode() != null) {
                        addCorrespondence(facilityId, permitList, tmpDirName + File.separator 
                                          + facility.getFacilityId() + "-" + fileBaseName + ".docx");
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
    protected String generateForm(Facility facility, Permit permit) throws Exception {

        StringBuffer errors = new StringBuffer();

        try {
            DocumentGenerationBean dataBean = new DocumentGenerationBean();
            if (getCorrespondenceDate() != null && getCorrespondenceDate().length() > 0) {
                dataBean.setCorrespondenceDate(getCorrespondenceDate());
            }
            dataBean.setFacility(facility);
            dataBean.setPermit(permit);
            if (tmpDirName == null)
                tmpDirName = "";
            /*DocumentUtil.generateDocument(templateDoc.getPath(), dataBean, tmpDirName
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
