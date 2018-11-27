package us.oh.state.epa.stars2.app.tools;

import java.io.File;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;

import us.oh.state.epa.stars2.webcommon.compliance.PerReportingPeriod;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.BulkDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.PTIOPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEU;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitInfo;
import us.oh.state.epa.stars2.def.ComplianceReportTypeDef;
import us.oh.state.epa.stars2.def.CorrespondenceDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.PTIOPERDueDateDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentUtil;

@SuppressWarnings("serial")
public class GeneratePERForms extends BulkCorrespondence {

    /** 
     * PER_NOV const must match key id cm_bulk_def.bulk_id for
     * NOV - PER Late Letter.
     */
    public static int PER_NOV = 210;
    public static int PER_NOV_NOT_LOGGED = 214;
    public static int PER_REMINDER_LETTER = 212;
    public static int FEPTIO_PER_REMINDER = 215;
    public static int FEPTIO_PER_LATE_LETTER = 216;

    private Integer bulkID;
    private TemplateDocument templateDoc;
    private String fileBaseName;

    private String tmpDirName;
    private String urlDirName;

    private String perDueDateCd;
    private String perDueDate;

    private int dueYear;
    private int dueMonth;
    private int dueDay;
    private long dueTime;

    private int startYear;
    private int startMonth;
    private int startDay;
    private Timestamp startTime;

    private int endYear;
    private int endMonth;
    private int endDay;
    private Timestamp endTime;

    private FacilityList[] facilities;
    private ArrayList<FacilityList> filteredFacilities;
    private FormResult[] results;
    private String currentFile;

    public GeneratePERForms() {
        super();
        setButtonName("Generate PER Forms");
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
        this.catalog = catalog;
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

        if (catalog.getPerDueDate() == null) {
            setSearchStarted(true);
            catalog.setFacilities(null);
            setSearchCompleted(true);
            return;
        }
        setPERDueDateCd(catalog.getPerDueDate());
        
        if (catalog.getYear() != null) {
            setPERYear(catalog.getYear().toString());
        }
        setDesiredDates();
        
        String permitClassCd = null;

        // limit facilities to FEPTIO if FEPTIO reminder is being run
        if (bulkID.intValue() == FEPTIO_PER_REMINDER || bulkID.intValue() == FEPTIO_PER_LATE_LETTER) {
        	permitClassCd = PermitClassDef.SMTV;
        } else {
        	permitClassCd = catalog.getPermittingClassCd();
        }
    	
        facilities 
            = getFacilityService().searchFacilities(catalog.getFacilityNm(),
                                            catalog.getFacilityId(),
                                            null,
                                            null,
                                            catalog.getCounty(),
                                            catalog.getOperatingStatus(),
                                            catalog.getDoLaa(),
                                            null, permitClassCd, null, null, 
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

    private ArrayList<FacilityList> filterFacilities(FacilityList[] facilityBaseList,
                                                     BulkOperationsCatalog catalog) {

        ArrayList<FacilityList> filteredList = new ArrayList<FacilityList>();

        int i = 0;
        for (FacilityList facilityList : facilityBaseList) {
            setValue(++i);
            try {
                if (catalog.getSearchThread().isInterrupted()) {
                    return new ArrayList<FacilityList>();
                }
                
                if (bulkID.intValue() == PER_REMINDER_LETTER && PermitClassDef.SMTV.equals(facilityList.getPermitClassCd())) {
                	// skip FEPTIO facilities for "regular" PER Reminder letter
                	continue;
                }
                
                // get list of PER due dates valid for this facility
                boolean candidateFacility = false;
                List<PerReportingPeriod> perList = new ArrayList<PerReportingPeriod>();
				getComplianceReportService().retrievePerReportingPeriods(facilityList.getFacilityId(), perList );

				// if there is a period currently active for this facility,
				// find it and see if it matches the period specified.
				for (PerReportingPeriod per : perList) {
					Calendar perEndDate = Calendar.getInstance();
					perEndDate.setTime(per.getEndDate());
					if (getPERDueDateCd().equals(per.getDueDateCd()) && endYear == perEndDate.get(Calendar.YEAR)) {
						candidateFacility = per.getDueDateCd().equals(getPERDueDateCd());
						break;
					}
				}

                if (!candidateFacility) {
                    continue;
                }
                    
                List<Permit> permits 
                    = getPermitService().searchPERs(facilityList.getFacilityId(), startTime, endTime);

                if (permits.isEmpty()) {
                    continue;
                }

                // Notice Of Violation...
                if (bulkID.intValue() == PER_NOV || bulkID.intValue() == PER_NOV_NOT_LOGGED || bulkID.intValue() == FEPTIO_PER_LATE_LETTER) {
                    boolean validReport = false;
                    ComplianceReportList[] crlArray 
                        = getComplianceReportService().searchComplianceReportByFacility(facilityList.getFacilityId());

                    /*
                    for (ComplianceReportList crl : crlArray) {

                        if (!crl.getReportType().equals(ComplianceReportTypeDef.COMPLIANCE_TYPE_PER)) {
                            continue; // Next ComplianceReportList, not a PER type.
                        }
                        
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(startTime);
                        int formStartYear = cal.get(Calendar.YEAR);
                        int formStartMonth = cal.get(Calendar.MONTH);
                        int formStartDay = cal.get(Calendar.DAY_OF_MONTH);

                        cal.setTime(crl.getPerStartDate());
                        int rptStartYear = cal.get(Calendar.YEAR);
                        int rptStartMonth = cal.get(Calendar.MONTH);
                        int rptStartDay = cal.get(Calendar.DAY_OF_MONTH);

                        if (formStartYear != rptStartYear && formStartMonth != rptStartMonth
                            && formStartDay != rptStartDay) {
                            continue; // Next ComplianceReportList, wrong start of period.
                        }
                        
                        if (crl.getReceivedDate() != null
                            && crl.getReceivedDate().compareTo(new Timestamp(dueTime)) <= 0) {
                            validReport = true;
                            break; // Report was on time.
                        }

                    }
                    */

                    if (validReport) {
                        continue; // Next facility.
                    }
                }

                filteredList.add(facilityList);

            } 
            catch (Exception e) {
                logger.error("Exception caught while filtering facilities, msg = "
                             + e.getMessage(), e);
                catalog.setErrorMessage("Unable to correctly filter the facility list: "
                                         + e.getMessage());
            }
        }

        return filteredList;

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

        facilities = catalog.getSelectedFacilities();
        results = new FormResult[facilities.length + 1];

        try {
            tmpDirName = getDocumentService().createTmpDir(getCurrentUser());
            urlDirName = tmpDirName.replace('\\', '/');
        }
        catch (Exception e) {
            catalog.setErrorMessage("Could not create the tmp directory."
                                     + e.getMessage());
            logger.error("Could not create tmp directory.", e);

        }

        if (facilities.length != 0) {

            for (int i = 0; i < facilities.length; i++) {
                try {

                    String facilityId = facilities[i].getFacilityId();
                    Facility facility = getFacilityService().retrieveFacility(facilityId);
                    
                    List<Permit> permits 
                        = getPermitService().searchPERs(facilityId, startTime, endTime);
                    
                    PTIOPermit ptio = new PTIOPermit();
                    ArrayList<PermitEU> allEUs = new ArrayList<PermitEU>();
                    TreeMap<String, PermitEU> euSortMap = new TreeMap<String, PermitEU>();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

                    for (Permit permit : permits) {
                        PermitInfo fullPermitInfo = getPermitService().retrievePermit(permit.getPermitID());
                        Permit fullPermit = fullPermitInfo.getPermit();
                        for (PermitEUGroup euGroup : fullPermit.getEuGroups()) {
                            for (PermitEU permitEU : euGroup.getPermitEUs()) {
                            	for (PermitEUGroup srcPermitEUGroup : permit.getEuGroups()) {
                                    for (PermitEU srcPermitEU : srcPermitEUGroup.getPermitEUs()) {
                                        if (permitEU.getFpEU().getCorrEpaEmuId().equals(srcPermitEU.getFpEU().getCorrEpaEmuId())) {
                                        	permitEU.setEffectiveDate(srcPermitEU.getEffectiveDate()); // restore effective date
                                        	String effDate = permitEU.getEffectiveDate() != null ? dateFormat.format(permitEU.getEffectiveDate()) : "";
                                        	euSortMap.put(permitEU.getFpEU().getEpaEmuId() + effDate, permitEU);
                                            ptio.setPermitID(permit.getPermitID());
                                            ptio.setPermitNumber(permit.getPermitNumber());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // add all permit EUs sorted by EPA EMU Id and effective date
                    for (String key : euSortMap.keySet()) {
                    	allEUs.add(euSortMap.get(key));
                    }

                    PermitEUGroup allEUGroup = new PermitEUGroup();
                    allEUGroup.setIndividualEUGroup();
                    allEUGroup.setPermitEUs(allEUs);
                    ptio.addEuGroup(allEUGroup);

                    String errors = generateForm(facility, ptio);
                    results[i+1] = new FormResult();
                    results[i+1].setId(facilities[i].getName());
                    results[i+1].setFormURL(DocumentUtil.getFileStoreBaseURL() + urlDirName
                                            + "/" + facility.getFacilityId() + "-" + fileBaseName + ".docx");
                    results[i+1].setFileName(facility.getFacilityId() + "-" + fileBaseName + ".docx");
                    results[i+1].setNotes(errors);

                    if (getCorrespondenceTypeCode() != null) {
                        addCorrespondence(facilityId, null, tmpDirName + File.separator 
                                          + facility.getFacilityId() + "-" + fileBaseName + ".docx");
                    }

                }
                catch (Exception e) {
                    String logStr = "Exception " + e.getClass().getName() + ", Msg = "
                        + e.getMessage();
                    logger.error(logStr, e);
                    if (results[i+1] != null) {
                    	results[i+1].setNotes(logStr);
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
        try {
            DocumentUtil.rmDir(tmpDirName);
        }
        catch (Exception e) {
            logger.error("Unable to remove temporary directory " + tmpDirName, e);
        }
        return SUCCESS;
    }

    private String getPERDueDateCd() {
        if (perDueDateCd == null) {
            setPERDueDateCd("0");
        }
        return perDueDateCd;
    }

    private void setPERDueDateCd(String dueDateCd) {
        if (dueDateCd == null) {
            perDueDateCd = "0";
        }
        perDueDateCd = dueDateCd;
        setDesiredDates();
    }

    /* Starting year of PER. */
    private void setPERYear(String year) {
        // Make an assumption about the desired year if the startYear is null.
        if (year == null) {
            Calendar today = Calendar.getInstance();
            endYear = today.get(Calendar.YEAR);
        }
        else {
            endYear = Integer.parseInt(year);
        }
        setDesiredDates();
    }

    private void setDesiredDates() {

        if (endYear == 0) {
            Calendar today = Calendar.getInstance();
            endYear = today.get(Calendar.YEAR);
        }

        if (perDueDateCd == null) {
            perDueDateCd = "0";
            return;
        }

        if (perDueDateCd.equals("0")) {
            return;
        }

        DefSelectItems endDates = PTIOPERDueDateDef.getEndOfPeriod().getItems();
        String endDate = endDates.getItemDesc(getPERDueDateCd());
        endMonth = Integer.parseInt(endDate.substring(0, 2));
        endDay = Integer.parseInt(endDate.substring(2));

        if (endMonth == 12 && endDay == 31) {
            startYear = endYear;
            dueYear = endYear + 1;
        }
        else {
            startYear = endYear - 1;
            dueYear = endYear;
        }

        DefSelectItems dueDates = PTIOPERDueDateDef.getDueDateMonthDay().getItems();
        String dueDate = dueDates.getItemDesc(getPERDueDateCd());
        dueMonth = Integer.parseInt(dueDate.substring(0, 2));
        dueDay = Integer.parseInt(dueDate.substring(2));

        Calendar cal = Calendar.getInstance();
        cal.set(dueYear, dueMonth - 1, dueDay);
        dueTime = cal.getTimeInMillis();

        DefSelectItems startDates = PTIOPERDueDateDef.getStartOfPeriod().getItems();
        String startDate = startDates.getItemDesc(getPERDueDateCd());
        startMonth = Integer.parseInt(startDate.substring(0, 2));
        startDay = Integer.parseInt(startDate.substring(2));

        cal.set(startYear, startMonth - 1, startDay);
        startTime = new Timestamp(cal.getTimeInMillis());

        cal.set(endYear, endMonth - 1, endDay);
        endTime = new Timestamp(cal.getTimeInMillis());

        perDueDate = PTIOPERDueDateDef.getData().getItems().getItemDesc(getPERDueDateCd());
        if (perDueDate != null && perDueDate.trim().length() > 0) {
        	// per due date is formatted as: <start> - <end>, Due <due>
        	// we only want the portion after the word "Due"
        	String[] parts = perDueDate.split(",");
        	if (parts.length == 2) {
        		perDueDate = parts[1];
        		perDueDate = perDueDate.replaceFirst("\\s+Due\\s+", "") + 
        			", " + Integer.toString(dueYear);
        	}
        }
    }

    /** Generate a PER form for a facility. */
    protected String generateForm(Facility facility, Permit permit) throws Exception {

        StringBuffer errors = new StringBuffer();

        try {
            DocumentGenerationBean dataBean = new DocumentGenerationBean();
            if (getCorrespondenceDate() != null && getCorrespondenceDate().length() > 0) {
                dataBean.setCorrespondenceDate(getCorrespondenceDate());
            }

            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

            String reportStartDate = df.format(startTime);
            dataBean.getProperties().put("reportStartDate", reportStartDate);

            String reportEndDate = df.format(endTime);
            dataBean.getProperties().put("reportEndDate", reportEndDate);

            dataBean.setFacility(facility);
            dataBean.getProperties().put("perDueDate", perDueDate);
            dataBean.setPermit(permit);

            /*DocumentUtil.generateDocument(templateDoc.getPath(), dataBean, tmpDirName
                                          + File.separator + facility.getFacilityId() 
                                          + "-" + fileBaseName + ".docx");*/
            DocumentUtil.generateAsposeDocument(templateDoc.getTemplateDocPath(), dataBean, tmpDirName
                    + File.separator + facility.getFacilityId() 
                    + "-" + fileBaseName + ".docx");
        }
        catch (Exception e) {
            logger.error("Unable to generate PER form: " + e.getMessage(), e);
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

