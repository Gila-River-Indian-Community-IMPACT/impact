package us.oh.state.epa.stars2.app.tools;

import java.io.File;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.faces.event.ActionEvent;

import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.document.CorrespondenceDocument;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsRptInfo;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.BulkDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;
import us.oh.state.epa.stars2.def.ContactTypeDef;
import us.oh.state.epa.stars2.def.CorrespondenceDef;
import us.oh.state.epa.stars2.def.CorrespondenceDirectionDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EmissionReportsDef;
import us.oh.state.epa.stars2.def.EmissionReportsRealDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.ContactUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.reports.ErNTVBase;

@SuppressWarnings("serial")
public class GenerateBulkEmRptReminder extends BulkCorrespondence {
	private InfrastructureService infrastructureService;

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

    static class ERDef {

        Integer _bulkID;
        String _rptCategory;
        String _receivedStatus;
        boolean _pastDue;
        boolean _ozoneAttainment;

        public ERDef(String rptCategory, String receivedStatus,
                     boolean pastDue, boolean ozone) {

            _rptCategory = rptCategory;
            _receivedStatus = receivedStatus;
            _pastDue = pastDue;
            _ozoneAttainment = ozone;

        }

    }

    static class CorrWithERYear extends Correspondence {

        Integer _yearOne;
        Integer _yearTwo;
        Integer _fpId;
        String _fullAddr;
        String _reportingTypeCd;

    }

    static HashMap<Integer, ERDef> allERs = new HashMap<Integer, ERDef>();

    static {

        allERs.put(190, new ERDef(EmissionReportsDef.TV, ReportReceivedStatusDef.REMINDER_SENT,
                                  true, true));
        allERs.put(191, new ERDef(EmissionReportsDef.TV, ReportReceivedStatusDef.REMINDER_SENT,
                                  true, false));

        allERs.put(192, new ERDef(EmissionReportsDef.SMTV, ReportReceivedStatusDef.REMINDER_SENT,
                                  true, true));
        allERs.put(193, new ERDef(EmissionReportsDef.SMTV, ReportReceivedStatusDef.REMINDER_SENT,
                                  true, false));

        allERs.put(196, new ERDef(EmissionReportsDef.TV, ReportReceivedStatusDef.REPORT_NOT_REQUESTED,
                                  false, true));
        allERs.put(197, new ERDef(EmissionReportsDef.TV, ReportReceivedStatusDef.REPORT_NOT_REQUESTED,
                                  false, false));

        allERs.put(194, new ERDef(EmissionReportsDef.SMTV, ReportReceivedStatusDef.REPORT_NOT_REQUESTED,
                                  false, true));
        allERs.put(195, new ERDef(EmissionReportsDef.SMTV, ReportReceivedStatusDef.REPORT_NOT_REQUESTED,
                                  false, false));

        allERs.put(198, new ERDef(EmissionReportsDef.NTV, ReportReceivedStatusDef.REPORT_NOT_REQUESTED,
                                  false, true));
        allERs.put(199, new ERDef(EmissionReportsDef.NTV, ReportReceivedStatusDef.REMINDER_SENT,
                                  false, false));
    }

    private static boolean debug = false;
    private Integer bulkID = null;
    private static String separator = " []  ";

    private TemplateDocument templateDoc;
    private String fileBaseName;

    private String receivedStatus;
    private boolean ozoneAttainment;
    private Integer reportYear;
    
    Integer yearEven = null;
    Integer yearOdd = null;

    private String tmpDirName;
    private String urlDirName;

    private FacilityList[] facilities;
    private ArrayList<FacilityList> filteredFacilities;
    private ArrayList<FormResult> results;
    private String currentFile;

    private String currentUser = InfrastructureDefs.getCurrentUserAttrs().getUserName();
    
    //    //  Number of tons where emissions must be enumerated.
    //    private String mustEnumerateFER = getParameter("ER_NTV_FER_Must_Enumerate");
    //    private int minValue = Integer.parseInt(mustEnumerateFER);
    //    
    //    // Number of tons where ES report is needed.
    //    private String mustEnumerateES = getParameter("ER_NTV_ES_Must_Enumerate");

    public GenerateBulkEmRptReminder() {
        super();
        setButtonName("Generate Emissions Inventory Form");
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

        setCorrespondenceTypeCode(bulkOpDef.getCorrespondenceTypeCd());
        DefSelectItems templateDocTypeCdItems = CorrespondenceDef.getTemplateDocTypeCdData().getItems();
        String templateDocTypeCd = templateDocTypeCdItems.getItemDesc(bulkOpDef.getCorrespondenceTypeCd());

        if (templateDocTypeCd != null) {
            templateDoc = getDocumentService().retrieveTemplateDocument(templateDocTypeCd);
        }
        //String fPath = templateDoc.getPath();
        String fPath = templateDoc.getTemplateDocPath();
        int lastSlash = fPath.lastIndexOf('/');
        if (lastSlash < 0) {
            lastSlash = fPath.lastIndexOf('\\');
        }
        int lastDot = fPath.lastIndexOf('.');
        fileBaseName = fPath.substring(lastSlash + 1, lastDot);

        receivedStatus = allERs.get(bulkID)._receivedStatus;
        ozoneAttainment = allERs.get(bulkID)._ozoneAttainment;

        if (catalog.getYear() != null) {
            reportYear = catalog.getYear();
        }
        else {
            Calendar cal = Calendar.getInstance();
            reportYear = new Integer(cal.get(Calendar.YEAR));
        }
        if(debug) {
            logger.error("Debug/Trace: Year = " + reportYear);
        }
        
        if(reportYear % 2 == 1) {
            //Is odd
            yearOdd = reportYear;
            yearEven = reportYear -1;
        } else {
            // Is even
            yearEven = reportYear;
            yearOdd = reportYear + 1;
        }

        facilities = getFacilityService().searchFacilities(catalog.getFacilityNm(),
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


        setMaximum(facilities.length);
        setValue(0);

        setSearchStarted(true);
        filteredFacilities = filterFacilities(facilities, catalog);
        catalog.setFacilities(filteredFacilities.toArray(new FacilityList[0]));
        setSearchCompleted(true);

    }

    protected ArrayList<FacilityList> filterFacilities(FacilityList[] facilityBaseList,
                                                       BulkOperationsCatalog catalog) 
        throws RemoteException {

        ArrayList<FacilityList> filteredByEmRptInfo = new ArrayList<FacilityList>();

        int i = 0;
        for (FacilityList facility : facilityBaseList) {

            try {
                //if (catalog.getSearchThread().isInterrupted()) {
                 //   return new ArrayList<FacilityList>();
               // }

                if(debug) {
                    logger.error("Debug/Trace: before filtering found " + facility.getFacilityId());
                }

                // Filter by attainment
                if (bulkID != 198 && bulkID != 199) { 
                    if (getEmissionsReportService().retrieveCompliance(reportYear,
                                                               facility.getCountyCd()) != ozoneAttainment) {
                        if(debug) {
                            logger.error("Debug/Trace: Attainment skip " + facility.getFacilityId() 
                                         + " " + facility.getCountyCd() + " " + ozoneAttainment);
                        }
                        setValue(++i);
                        continue; // skip this one
                    }
                }
                
                // filter based upon reporting category and state in
                // table fp_yearly_reporting_category
                EmissionsRptInfo eriYear 
                    = getEmissionsReportService().getEmissionsRptInfo(reportYear, facility.getFacilityId());
                if(eriYear == null) {
                    eriYear = new EmissionsRptInfo();
                }
                
                EmissionsRptInfo eriYearEven = eriYear;
                EmissionsRptInfo eriYearOdd = eriYear;
                if(bulkID == 198 || bulkID == 199) {
                    if(reportYear % 2 == 1) {
                        //Is odd
                        eriYearEven 
                            = getEmissionsReportService().getEmissionsRptInfo(yearEven, facility.getFacilityId());
                        if(eriYearEven == null) {
                            eriYearEven = new EmissionsRptInfo();
                        }
                    }
                    else {
                        // Is even
                        eriYearOdd 
                            = getEmissionsReportService().getEmissionsRptInfo(yearOdd, facility.getFacilityId());
                        if(eriYearOdd == null) {
                            eriYearOdd = new EmissionsRptInfo();
                        }
                    }
                    if(receivedStatus.equals(eriYearEven.getState())
                       && eriYearEven.getReportingEnabled()) {
                        facility.setYearEven(yearEven);
                    }
                    if(receivedStatus.equals(eriYearOdd.getState())
                       && eriYearOdd.getReportingEnabled()) {
                        facility.setYearOdd(yearOdd);
                    }
                    if(facility.getYearEven() == null && facility.getYearOdd() == null) {
                        setValue(++i);
                        continue; // skip this one
                    } else if(facility.getYearEven() != null && facility.getYearOdd() != null){
                        facility.setNtvSelected(true);
                    } else {
                        facility.setNtvSelected(true);
                        if(bulkID == 199) {
                            if(facility.getYearEven() == null &&
                                    ReportReceivedStatusDef.isSubmittedCode(eriYearEven.getState())
                                    ) {
                                facility.setNtvSelected(false);
                            }
                            if(facility.getYearOdd() == null &&
                                    ReportReceivedStatusDef.isSubmittedCode(eriYearOdd.getState())
                                    ) {
                                facility.setNtvSelected(false);
                            }
                        }
                    }
                    facility.setReportingTypeCd(EmissionReportsDef.NTV);
                } 
                else {
                    facility.setYear(reportYear);
                    if(eriYear == null
                       || !receivedStatus.equals(eriYear.getState())
                       || !eriYear.getReportingEnabled()) {
                        if(debug) {
                            String s = "null";
                            if(eriYear != null) {
                                s = " " 
                                    + receivedStatus + "/" + eriYear.getState() + "/" + eriYear.getReportingEnabled();
                            }
                            logger.error("Debug/Trace: before filtering found " + facility.getFacilityId() + s);
                        }
                        setValue(++i);
                        continue;  // skip this one
                    }
                }
                filteredByEmRptInfo.add(facility);
                if(debug) {
                    logger.error("Debug/Trace: filtering kept " + facility.getFacilityId());
                }
            }
            catch (Exception e) {
                logger.error("Exception caught while filtering facilities, msg = "
                             + e.getMessage(), e);
            }
            
            setValue(++i);

        }
            
        return filteredByEmRptInfo;

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
        results = new ArrayList<FormResult>(facilities.length + 1);

        try {
            tmpDirName = getDocumentService().createTmpDir(currentUser);
            urlDirName = tmpDirName.replace('\\', '/');
        }
        catch (Exception e) {
            logger.error("Could not create tmp directory.", e);
        }

        if (facilities.length != 0) {

            for (int i = 0; i < facilities.length; i++) {

                String facilityId = facilities[i].getFacilityId();
                Iterator<FacilityList> fplit = filteredFacilities.iterator();

                while (fplit.hasNext()) {
                    //   try {
                    FacilityList nextPermit = fplit.next();
                    if (nextPermit.getFacilityId().equals(facilityId)) {
                        generateForm(nextPermit);
                    }
                }
            }

            FormResult fR = new FormResult();
            try {
                fR.setId("Zip File");
                fR.setNotes("All Forms");
                Calendar cal = Calendar.getInstance();
                String date = Integer.toString(cal.get(Calendar.YEAR)) + "-" 
                    + Integer.toString((cal.get(Calendar.MONTH) + 1)) + "-"
                    + Integer.toString(cal.get(Calendar.DAY_OF_MONTH)) + "-"
                    + Integer.toString(cal.get(Calendar.HOUR_OF_DAY)) + "-"
                    + Integer.toString(cal.get(Calendar.MINUTE)) + "-"
                    + Integer.toString(cal.get(Calendar.SECOND));
                String zipFileName = fileBaseName + date + ".zip";
                DocumentUtil.createZipFile(tmpDirName, 
                                           File.separator + "tmp" + File.separator 
                                           + currentUser + File.separator + zipFileName,
                                           fileBaseName);
                fR.setFormURL(DocumentUtil.getFileStoreBaseURL() + "/tmp/" + currentUser
                              + "/" + zipFileName);
                fR.setFileName(fileBaseName + ".zip");
            }
            catch (Exception e) {
                String logStr = "Exception " + e.getClass().getName() + ", Msg = "
                    + e.getMessage();
                logger.error(logStr, e);
                fR.setNotes(logStr);
            }
            results.add(0, fR);
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
    protected void generateForm(FacilityList facility) {
        StringBuffer errors = new StringBuffer();
        //        Calendar cal = Calendar.getInstance();
        //        Timestamp anchor;

        try {
            DocumentGenerationBean dataBean = new DocumentGenerationBean();
            DocumentGenerationBean dataBean2 = new DocumentGenerationBean();
            if (getCorrespondenceDate() != null && getCorrespondenceDate().length() > 0) {
                dataBean.setCorrespondenceDate(getCorrespondenceDate());
                dataBean2.setCorrespondenceDate(getCorrespondenceDate());
            }

            // Get remaining information needed for report generation
            // Create Facility object to hold only attributes of interest.
            Facility fP = new Facility(facility);

            // Get contact information for facility
            fP.setAllContacts(getFacilityService().retrieveFacilityContacts(
                                                                    facility.getFacilityId()));

            // Determine correct billing and primary contact and set
            // them into facility as "current" so they are accessed
            // from the dataBean during document generation.

            if (bulkID == 198 || bulkID == 199) {
                boolean nonAttainmentEven = false;
                boolean nonAttainmentOdd = false;
                //                ReportTemplates locatedEvenScReports;
                //                ReportTemplates locatedOddScReports;
                //                Collection<SelectItem> lst;
                //                ArrayList<String> feeListEven = null;
                //                ArrayList<String> feeListOdd = null;
                // Get pollutants and fees for each year.
                if(facility.getYearEven() != null) {
                    //                    locatedEvenScReports = getEmissionsReportService()
                    //                    .retrieveSCEmissionsReports(facility.getYearEven(),
                    //                            EmissionReportsDef.NTV, false);
                    //                    locatedEvenScReports.displayMsgs();
                    //                    lst = locatedEvenScReports.getScFER().getFeePickList(minValue, "; enumerate");
                    //                    feeListEven = new ArrayList<String>(lst.size());
                    //                    for(SelectItem si : lst) {
                    //                        feeListEven.add(si.getDescription());
                    //                    }
                    //  Fee structure not generated
                    //                    DocumentGenerationBean FeeStructEven = new DocumentGenerationBean();
                    //                    FeeStructEven.getProperties().put("EmissionsReportingYear", facility.getYearEven().toString());
                    //                    ArrayList<DocumentGenerationBean> feeItems = new ArrayList<DocumentGenerationBean>();
                    //                    for(SelectItem si : lst) {
                    //                        DocumentGenerationBean feeItem = new DocumentGenerationBean();
                    //                        feeItem.getProperties().put("FeeItem", si.getDescription());
                    //                        feeItems.add(feeItem);
                    //                    }
                    //                    dataBean.getChildCollections().put("Fee Structure", feeItems);

                    nonAttainmentEven = !getEmissionsReportService().retrieveCompliance(facility.getYearEven(),
                                                                                facility.getPhyAddr().getCountyCd());
                }

                if(facility.getYearOdd()!= null ) {
                    //                    locatedOddScReports = getEmissionsReportService()
                    //                    .retrieveSCEmissionsReports(facility.getYearOdd(),
                    //                            EmissionReportsDef.NTV, false);
                    //                    locatedOddScReports.displayMsgs();
                    //                    lst = locatedOddScReports.getScFER().getFeePickList(minValue, "; enumerate");
                    //                    feeListOdd = new ArrayList<String>(lst.size());
                    //                    for(SelectItem si : lst) {
                    //                        feeListOdd.add(si.getDescription());
                    //                    }
                    nonAttainmentOdd = !getEmissionsReportService().retrieveCompliance(facility.getYearOdd(),
                                                                               facility.getPhyAddr().getCountyCd());
                }

                // Populate the collections
                ArrayList<DocumentGenerationBean> pastNTV = new ArrayList<DocumentGenerationBean>();
                DocumentGenerationBean pastNTVe = new DocumentGenerationBean();
                DocumentGenerationBean pastNTVo = new DocumentGenerationBean();

                // Get previous years information.
                String prevEvenEmissions = ErNTVBase.NO_REPORT;
                EmissionsReport rPrevEven = getEmissionsReportService().
                    retrieveLatestEmissionReport(yearEven - 2, facility.getFacilityId());
                if(rPrevEven != null) {
                    Float prevRptEvenYearTons = rPrevEven.getTotalEmissions();
                    if(prevRptEvenYearTons == null) {
                        prevEvenEmissions = ErNTVBase.NO_EMISSIONS_REPORT;
                        if(rPrevEven.getFeeId() != null) {
                            Fee f = getInfrastructureService().retrieveFee(rPrevEven.getFeeId());
                            if(f != null) {
                                prevEvenEmissions = f.getDescription(EmissionReportsRealDef.NTV, 0, "");
                            }
                        }
                    } else {
                        double d = prevRptEvenYearTons.doubleValue();
                        prevEvenEmissions = EmissionsReport.numberToString(d) + " Tons";
                    }
                }

                String prevOddEmissions = ErNTVBase.NO_REPORT;
                EmissionsReport rPrevOdd = getEmissionsReportService().
                    retrieveLatestEmissionReport(yearOdd - 2, facility.getFacilityId());
                if(rPrevOdd != null) {
                    Float prevRptOddYearTons = rPrevOdd.getTotalEmissions();
                    if(prevRptOddYearTons == null) {
                        prevOddEmissions = ErNTVBase.NO_EMISSIONS_REPORT;
                        if(rPrevOdd.getFeeId() != null) {
                            Fee f = getInfrastructureService().retrieveFee(rPrevOdd.getFeeId());
                            if(f != null) {
                                prevOddEmissions = f.getDescription(EmissionReportsRealDef.NTV, 0, "");
                            }
                        }
                    } else {
                        double d = prevRptOddYearTons.doubleValue();
                        prevOddEmissions = EmissionsReport.numberToString(d) + " Tons";
                    }
                }
                pastNTVe.getProperties().put("reportingYear", Integer.toString(yearEven.intValue() - 2));
                pastNTVe.getProperties().put("emissionRange", prevEvenEmissions);
                pastNTVo.getProperties().put("reportingYear", Integer.toString(yearOdd.intValue() - 2));
                pastNTVo.getProperties().put("emissionRange", prevOddEmissions);
                pastNTV.add(pastNTVe);
                pastNTV.add(pastNTVo);

                boolean separateReports = false;
                if(facility.getYearEven() != null &&
                   facility.getYearOdd()!= null && fP.ownershipChange(facility.getYearOdd())) {
                    separateReports = true;
                }

                if(facility.getYearEven() != null && (separateReports || facility.getYearOdd()== null)) {
                    // Single year even report
                    dataBean.getProperties().put("reportingYear1", facility.getYearEven().toString());
                    dataBean.getChildCollections().put("pastNTV", pastNTV);
                    ArrayList<DocumentGenerationBean> ntvInfo = new ArrayList<DocumentGenerationBean>();
                    ArrayList<DocumentGenerationBean> ntvES = new ArrayList<DocumentGenerationBean>();
                    DocumentGenerationBean ntvESYearElm = new DocumentGenerationBean();
                    ArrayList<DocumentGenerationBean> ntvESYear = new ArrayList<DocumentGenerationBean>();
                    DocumentGenerationBean a = new DocumentGenerationBean();
                    a.getProperties().put("reportingYear", facility.getYearEven().toString());
                    //ntvInfo.add(a);
                    if(nonAttainmentEven) {
                        ntvES.add(a);
                    }
                    dataBean.getChildCollections().put("ntvInfo", ntvInfo);
                    ntvESYearElm.getChildCollections().put("ntvES", ntvES);
                    ntvESYear.add(ntvESYearElm);
                    dataBean.getChildCollections().put("ntvESyear", ntvESYear);
                    
                    String s = setContacts(fP, facility, facility.getYearEven(), true).toString();
                    errors.append(s);
                    dataBean.setFacility(fP);

                    String fullAddr = getFullMailingAddress(dataBean);

                    String yearE = facility.getYearEven().toString();
                    if(yearE.length() == 4) {
                        yearE = yearE.substring(2, 4);
                    }
                    String fileEnding = fP.getFacilityId() + "-" + fileBaseName
                        + "_" + yearE + ".docx";
                    if(s.length() == 0) {
                        /*DocumentUtil.generateDocument(templateDoc.getPath(), dataBean, tmpDirName
                                                      + File.separator + fileEnding);*/
                    	DocumentUtil.generateAsposeDocument(templateDoc.getTemplateDocPath(), dataBean, tmpDirName
                                + File.separator + facility.getFacilityId() 
                                + File.separator + ".docx");
                    }
                    addToGenerated(facility, fileEnding, errors.toString(),
                                   facility.getYearEven(), null, fullAddr);
                }
                if(facility.getYearOdd() != null && (separateReports || facility.getYearEven()== null)) {
                    // Single year odd report
                    dataBean2.getProperties().put("reportingYear1", facility.getYearOdd().toString());
                    dataBean2.getChildCollections().put("pastNTV", pastNTV);
                    ArrayList<DocumentGenerationBean> ntvInfo = new ArrayList<DocumentGenerationBean>();
                    ArrayList<DocumentGenerationBean> ntvES = new ArrayList<DocumentGenerationBean>();
                    DocumentGenerationBean ntvESYearElm = new DocumentGenerationBean();
                    ArrayList<DocumentGenerationBean> ntvESYear = new ArrayList<DocumentGenerationBean>();
                    DocumentGenerationBean a = new DocumentGenerationBean();
                    a.getProperties().put("reportingYear", facility.getYearOdd().toString());
                    //ntvInfo.add(a);
                    if(nonAttainmentOdd) {
                        ntvES.add(a);
                    }
                    dataBean2.getChildCollections().put("ntvInfo", ntvInfo);
                    ntvESYearElm.getChildCollections().put("ntvES", ntvES);
                    ntvESYear.add(ntvESYearElm);
                    dataBean2.getChildCollections().put("ntvESyear", ntvESYear);
                    
                    String s = setContacts(fP, facility, facility.getYearOdd(), true).toString();
                    errors.append(s);
                    dataBean2.setFacility(fP);
                    String fullAddr = getFullMailingAddress(dataBean2);
                    String yearO = facility.getYearOdd().toString();
                    if(yearO.length() == 4) {
                        yearO = yearO.substring(2, 4);
                    }
                    String fileEnding = fP.getFacilityId() + "-" + fileBaseName
                        + "_" + yearO + ".docx";
                    if(s.length() == 0) {
                        //DocumentUtil.generateDocument(templateDoc.getPath(), dataBean2, tmpDirName
                        //                              + File.separator + fileEnding);
                    	DocumentUtil.generateAsposeDocument(templateDoc.getTemplateDocPath(), dataBean2, tmpDirName
                                + File.separator + fileEnding); 
                               
                    }
                    addToGenerated(facility, fileEnding, errors.toString(),
                                   null, facility.getYearOdd(), fullAddr);
                }
                if(facility.getYearEven() != null && facility.getYearOdd()!= null
                   && !separateReports) {
                    // Two year report
                    dataBean.getProperties().put("reportingYear1", facility.getYearEven().toString());
                    dataBean.getProperties().put("reportingYear2", facility.getYearOdd().toString());
                    dataBean.getChildCollections().put("pastNTV", pastNTV);
                    ArrayList<DocumentGenerationBean> ntvInfo = new ArrayList<DocumentGenerationBean>();
                    ArrayList<DocumentGenerationBean> ntvES = new ArrayList<DocumentGenerationBean>();
                    DocumentGenerationBean ntvESYearElm = new DocumentGenerationBean();
                    ArrayList<DocumentGenerationBean> ntvESYear = new ArrayList<DocumentGenerationBean>();
                    DocumentGenerationBean a = new DocumentGenerationBean();
                    a.getProperties().put("reportingYear", facility.getYearEven().toString());
                    //ntvInfo.add(a);
                    if(nonAttainmentEven) {
                        ntvES.add(a);
                    }
                    a = new DocumentGenerationBean();
                    a.getProperties().put("reportingYear", facility.getYearOdd().toString());
                    if(nonAttainmentOdd) {
                        ntvES.add(a);
                    }
                    //ntvInfo.add(a);
                    DocumentGenerationBean a2 = new DocumentGenerationBean();
                    a2.getProperties().put("reportingYear2", facility.getYearOdd().toString());
                    ntvInfo.add(a2);

                    dataBean.getChildCollections().put("ntvInfo", ntvInfo);
                    ntvESYearElm.getChildCollections().put("ntvES", ntvES);
                    ntvESYear.add(ntvESYearElm);
                    dataBean.getChildCollections().put("ntvESyear", ntvESYear);
                    
                    String s = setContacts(fP, facility, facility.getYearOdd(), true).toString();
                    errors.append(s);
                    dataBean.setFacility(fP);
                    String fullAddr = getFullMailingAddress(dataBean);
                    String yearE = facility.getYearEven().toString();
                    if(yearE.length() == 4) {
                        yearE = yearE.substring(2, 4);
                    }
                    String yearO = facility.getYearOdd().toString();
                    if(yearO.length() == 4) {
                        yearO = yearO.substring(2, 4);
                    }
                    String fileEnding = fP.getFacilityId() + "-" + fileBaseName
                        + "_" + yearE + yearO + ".docx";
                    if(s.length() == 0) {
                        //DocumentUtil.generateDocument(templateDoc.getPath(), dataBean, tmpDirName
                        //                              + File.separator + fileEnding);
                        DocumentUtil.generateAsposeDocument(templateDoc.getTemplateDocPath(), dataBean, tmpDirName
                                + File.separator + fileEnding);
                    }
                    addToGenerated(facility, fileEnding, errors.toString(),
                                   facility.getYearEven(), facility.getYearOdd(), fullAddr);
                }
            } else { // is TV or SMTV
                String s = setContacts(fP, facility, facility.getYear(), false).toString();
                errors.append(s);
                dataBean.setFacility(fP);
                String fullAddr = getFullMailingAddress(dataBean);

                String fileEnding = fP.getFacilityId() + "-" + fileBaseName + ".docx";
                if(s.length() == 0) {
                    //DocumentUtil.generateDocument(templateDoc.getPath(), dataBean, tmpDirName
                    //                              + File.separator + fileEnding);
                    DocumentUtil.generateAsposeDocument(templateDoc.getTemplateDocPath(), dataBean, tmpDirName
                            + File.separator + fileEnding);
                }
                addToGenerated(facility, fileEnding, errors.toString(),
                               facility.getYear(), null, fullAddr);
            }
        }
        catch (Exception e) {
            String logStr = "Exception " + e.getClass().getName()
                + ", Msg = " + e.getMessage();
            logger.error(logStr, e);
            FormResult fR = new FormResult();
            fR.setId(facility.getFacilityId());
            fR.setNotes(logStr);
            results.add(fR);
        }
    }
    
    String getFullMailingAddress(DocumentGenerationBean b) {
        String contactName =
            b.getProperties().get("contactName");
        String contactAddrLine1 =
            b.getProperties().get("contactAddrLine1");
        String contactAddrLine2 =
            b.getProperties().get("contactAddrLine2");
        String contactAddrLine3 =
            b.getProperties().get("contactAddrLine3");
        StringBuffer fullAddr = new StringBuffer();
        if(contactName != null) {
            fullAddr.append(contactName);
        }
        String separate = ", ";
        if(contactAddrLine1 != null) {
            if(fullAddr.length() != 0) {
                fullAddr.append(separate);
                fullAddr.append(contactAddrLine1);
            } else {
                fullAddr.append(contactAddrLine1);
            }
        }
        if(contactAddrLine2 != null) {
            if(fullAddr.length() != 0) {
                fullAddr.append(separate);
                fullAddr.append(contactAddrLine2);
            } else {
                fullAddr.append(contactAddrLine2);
            }
        }
        if(contactAddrLine3 != null) {
            if(fullAddr.length() != 0) {
                fullAddr.append(separate);
                fullAddr.append(contactAddrLine3);
            } else {
                fullAddr.append(contactAddrLine3);
            }
        }
        return fullAddr.toString();
    }

    private StringBuffer setContacts(Facility fP, FacilityList facility, Integer year, boolean isNTV) {
        StringBuffer errors = new StringBuffer();
        Calendar cal = Calendar.getInstance();
        
        cal.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        Timestamp anchor = new Timestamp(cal.getTimeInMillis());
        anchor.setNanos(0);
        Timestamp contactRefDate = fP.latestOwnerRefDate(anchor);
       /* ContactUtil activePrimary = fP.getActiveContact(ContactTypeDef.PRIM, contactRefDate);
        if(activePrimary != null) {
            fP.setPrimaryContact(activePrimary.getContact());
        } else {*/
            errors.append("ERROR: No active Primary Contact for report." + separator);
        //}
        
        if(isNTV) {
            ContactUtil activeBilling = fP.getActiveContact(ContactTypeDef.BILL, contactRefDate);                    
            if(activeBilling != null) {
                fP.setBillingContact(activeBilling.getContact());
            }
            //OWNER is Obsolete in IMPACT
            /*ContactUtil activeOwner = fP.getActiveContact(ContactTypeDef.OWNR, contactRefDate);  
            HashMap<Integer, Contact> owner = new HashMap<Integer, Contact>();
            if(activeOwner != null) {
                owner.put(1, activeOwner.getContact());
            }*/
            //TODO: set owners for bulk em rpt reminder
            //fP.setOwners(owner);
        }
        return errors;
    }
    
    final private void addToGenerated(FacilityList facility, String fileEnding, String errors,
                                      Integer year1, Integer year2, String fullAddr) {
        FormResult fR = new FormResult();
        fR.setId(facility.getFacilityId());
        fR.setNotes(errors);
        if(errors.length() == 0) {
            fR.setFormURL(DocumentUtil.getFileStoreBaseURL() 
                          + urlDirName + "/" + fileEnding);
            fR.setFileName(fileEnding);
            createCorrespondence(facility, null, 
                                 tmpDirName + File.separator + fileEnding,
                                 year1, year2, fullAddr);
            if(debug) {
                logger.error("Debug/Trace:  Facility " + facility.getFacilityId() + " included "
                             + ":" + fR.getNotes() + ":" + fR.getFormURL());
            }
        } else {
            fR.setFormURL("");
            fR.setFileName("");
            if(debug) {
                logger.error("Debug/Trace:  Facility " + facility.getFacilityId() + " NOT included "
                             + ":" + fR.getNotes() + ":" + fR.getFormURL());
            }
        }
        results.add(fR);
    }
    
    public final void correspondence(ActionEvent actionEvent) {

        if (getCorrespondenceDate() == null) {
            DisplayUtil.displayError("Missing Correspondence Date");
            return;
        }

        StringTokenizer st = new StringTokenizer(getCorrespondenceDate(), "/");
        if (st.countTokens() != 3) {
            DisplayUtil.displayError("Correspondence Date should be in the form MM/DD/YYYY");
            return;
        }
        try {
            int month = Integer.parseInt(st.nextToken()) - 1;
            int day = Integer.parseInt(st.nextToken());
            int year = Integer.parseInt(st.nextToken());

            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);

            for (Correspondence corr : getCorrespondence()) {
                CorrWithERYear cwy = (CorrWithERYear) corr;
                Timestamp ts = new Timestamp(cal.getTimeInMillis());
                corr.setDateGenerated(ts);
                Integer yearOne = cwy._yearOne;
                Integer yearTwo = cwy._yearTwo;
                Integer fpId = cwy._fpId;
                String fullAddr = cwy._fullAddr;
                String reportingTypeCd = cwy._reportingTypeCd;
                String toState;
                if(bulkID == 190 || bulkID == 191 
                   || bulkID == 192 || bulkID == 193
               //    || bulkID == 195 || bulkID == 197
                   || bulkID == 199) {
                    toState = ReportReceivedStatusDef.SECOND_REMINDER_SENT;
                } else {
                    toState = ReportReceivedStatusDef.REMINDER_SENT;
                }
                getEmissionsReportService().setEmissionsRptInfoReminder(yearOne, yearTwo, fullAddr, reportingTypeCd,
                                                                toState, corr.getFacilityID(), fpId, cwy, InfrastructureDefs.getCurrentUserId());
            }
        }
        catch (Exception e) {
            String logStr = "Problem while storing correspondence. " + "Exception type = "
                + e.getClass().getName() + ", Msg = "
                + e.getMessage();
            logger.error(logStr, e);
            DisplayUtil.displayError(logStr);
        }

        setCorrespondenceSent(true);
        return;

    }

    public final FormResult[] getResults() {
        return results.toArray(new FormResult[0]);
    }

    public final void setFile(String fileName) {
        currentFile = fileName;
    }

    public final String getFile() {
        return currentFile;
    }

    public final void createCorrespondence(FacilityList facility, String permitNumber,
                                           String pathToFile, Integer yearOne, Integer yearTwo, String fullAddr) {

        try {

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
            CorrWithERYear correspondence = new CorrWithERYear();
            correspondence.setDocument(cDoc);
            correspondence.setCorrespondenceTypeCode(getCorrespondenceTypeCode());
            correspondence.setFacilityID(facility.getFacilityId());
            if (permitNumber != null) {
                correspondence.setAdditionalInfo(permitNumber);
            }
            correspondence._yearOne = yearOne;
            correspondence._yearTwo = yearTwo;
            correspondence._fpId = facility.getFpId();
            correspondence._fullAddr = fullAddr;
            correspondence._reportingTypeCd = facility.getReportingTypeCd();
            String one = "";
            if(yearOne != null) {
                one = yearOne.toString();
            }
            String two = "";
            if(yearTwo != null) {
                two = yearTwo.toString();
            }
            String space = "";
            if(yearOne != null && yearTwo !=  null) {
                space = " and ";
            }
            correspondence.setDirectionCd(CorrespondenceDirectionDef.OUTGOING);
            correspondence.setAdditionalInfo(one + space + two);
            addCorrespondence(correspondence);
        }
        catch (Exception e) {
            logger.error("Exception caught while trying to addCorrespondence: " + e.getMessage(), e);
        }
    }

}
