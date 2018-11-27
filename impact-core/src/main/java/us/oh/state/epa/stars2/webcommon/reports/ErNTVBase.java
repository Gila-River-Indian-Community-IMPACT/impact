package us.oh.state.epa.stars2.webcommon.reports;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.document.TmpDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionTotal;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsRptInfo;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.NTVContactType;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;
import us.oh.state.epa.stars2.def.ContactTypeDef;
import us.oh.state.epa.stars2.def.EmissionReportsRealDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.exception.ValidationException;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.webcommon.AppValidationMsg;
import us.oh.state.epa.stars2.webcommon.ContactUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.facility.FacilityProfileBase;
import us.oh.state.epa.stars2.webcommon.facility.FacilityValidation;

/*
 * No facility inventory will be saved with the NTV report.
 * Emissions will be totals--not stack or fugitive.
 * The facility does not need to specify any EUs.
 * NTV does not need to handle non-chargeable pollutants.
 * A received date is needed with NTV reports (but not SMTV or TV).Collection<SelectItem>
 * The FpId in the report object will be used to determine the FacilityId.
 * NTV report workflow has a DO/LAA approval step and CO approval step.
 * Prior to the CO approval step, the CO reviewer can update the report.
 * Only NTV reports have both DO/LAA and CO review steps.
 * SMTV and TV reports have only DO/LAA approval.
 * 
 * NTV reports need notes and attachments.  These will be associated with
 * one of the two report objects.  If these are split into separate
 * reports (separate owners), then the notes and attachments will
 * be duplicated on each report object.
 * 
 * The cutoff of 50 tons to enumerate the actual tonnage and the
 * cutoff of 25 tons for the ES reports are numbers which will not change.
 * 
 * Pollutants in an FER or ES or the fee structure can not be changed on odd
 * years.  Therefore they remain the same over any two year billing cycle.
 * HOWEVER, the design/implementation will not use that assumption and instead
 * will retrieve the report defintion for each year (even and odd).
 * 
 * DESIGN
 * Each report year data is kept in a separate EmissionsReport object.
 * On a search screen, 
 * 
 * Whether none, one or two years are provided to report upon depends upon:
 *   The report year specifies category NTV and reporting is enabled,
 *   Transfer of ownership and the date of transfer,
 *   Who is providing the report (old or new owner).
 *   Whether the party agrees to pay for both old and new owner.
 *   If thefacility was shutdown and when.
 *
 * If reporting is not possible for either year, because of shutdown date,
 * ownership change and date, then they still submit the report with only
 * ownership/shutdown information.
 * 
 * When the DO/LAA approves the report, the ownership and shutdown information
 * are entered into the facility information.  After that, this information
 * cannot be changed in the report (Stars2Admin cannot change it either)--
 * any changes would need to be made directly in the facility.
 * 
 * The emissions information can be modified until CO approval.  After that,
 * only Stars2Admin can change the emissions in the report.  A printed NTV
 * report need include only the emissions information--not ownership, address
 *  or shutdown info.
 *  
 *  Even when ownership change is known in facility inventory, a two year report
 *  is created.  This makes it possible to pay for both years.  However, the
 *  transfer date is entered automatically and cannot be removed.  Upon submission
 *  and if pay-both-years not checked, one of the reports is dropped.  This
 *  makes it possible for the other year to be filled out.  Prior to submission
 *  there is not a problem because either the report is being done in the portal
 *  and cannot be seen or is being done by AQD and they will not take long
 *  to enter/submit it.   If both are being done in portal or both on
 *  AQD side, then the second cannot be created until the first is submitted,
 *  since both years will be "out" and editable.
 *  
 *  For an old report, the correct primary and billing contact to use is the most
 *  recent contact after December 31st of the reporting year but before the first
 *  ownership change.  There is an ownership change when there is a Start Date
 *  for an owner.  End Dates are not examined.
 *  
 *  If a new billing address is specified for an old report, that address is not
 *  put into the facility; instead it is picked up by Invoicing from the NTV
 *  report itself.  Both the report and the invoice will refer to the same
 *  contact object.
 *  
 *  If primary or billing is specified for a current report, they will be put into
 *  the facility starting December 31st of the first reporting year in the NTV
 *  report.  The previous contact will be ended on December 30th.
 *  
 *  For display of contacts supply the following clues:
 *  new name -- if no matching existing contact (with same name)
 *  existing name or same name  -- to indicate if that contact already has that role
 *  different or same info -- to indicate if the contact has its info changed.
 *  
 *  Exactly when to render contacts:
 *  Always show currnt owners, current billing and current pdrimary.
 *  
 *  ownership change false : shutdown false-->Show updated owner/billing/primary
 *  
 *  ownership change false : shutdown true : report(s) due -> updated owner/billing/primary
 *  
 *  ownership change false : shutdown true : reports not due -> updated owner/primary
 *  
 *  ownership change true & new owner owes both: shutdown false-->oldForwarding/newOwner/billing/primary
 *  
 *  ownership change true & prev owner owes both: shutdown false-->oldForwarding/newOwner/billing
 *  
 *  ownership change true & new owner checks both: shutdown false-->
 *  
 *  ownership change true & prev owner checks both: shutdown false-->
 */

public abstract class ErNTVBase extends ReportBaseCommon {
	
	private static final long serialVersionUID = 6711728842318081661L;

	private final static String NTV_REPORT = "emissionReport";
    // Number of tons where emissions must be enumerated.
    String mustEnumerateFER = SystemPropertyDef.getSystemPropertyValue("ER_NTV_FER_Must_Enumerate", null);
    Integer minValue = SystemPropertyDef.getSystemPropertyValueAsInteger("ER_NTV_FER_Must_Enumerate", null);
    
    // Number of tons where ES report is needed.
    String mustEnumerateES = SystemPropertyDef.getSystemPropertyValue("ER_NTV_ES_Must_Enumerate", null);
    
    protected NtvReport ntvReport = null;

    private List<EmissionTotal> emissionsFer1 = null;
    private List<EmissionTotal> emissionsFer2 = null;
    private List<EmissionTotal> emissionsES1 = null;
    private List<EmissionTotal> emissionsES2 = null;
    private String displayTotal1 = " ";
    private String displayTotal2 = " ";
    
    private boolean nonAttainment1;  // what does county say?
    private boolean nonAttainment2;  // what does county say?
    
    boolean includeES1;  // used to set whether ES included.
    boolean includeES2;  // used to set whether ES included.
    
    /* Updated Billing Contact
     * In facility inventory
     *    Cannot be supplied if report for later year, newer owner or
     *    another report for the year that is not completed.
     * For this report only
     *    Cannot be supplied if another report for the year that
     *    is not completed.  Otherwise can be supplied when
     *    report for later year (a permanent condition) or newer
     *    owner (a permanent condition)
     */
    
    boolean allowPrimaryAddress; 
    boolean allowContactChange;  // false if there are more recent reports or
                             // or more recent owners
    // TREAT below as newestOwner filling out report--already known or not.
    boolean existingPurchaseOwner;   // true if known current owner and indicate bought.
                                     // if true, can provide updated owner info
    boolean needNewOwnerInfo;  // true if we allow new owner info, forwarding address info, etc.
                               // True if ownership change and (not more recent reports
                               // or more recent owners) and is a new transfer
                               // date (later than any known).
    
    // These are for what the ES pollutants would be if report included.
    private List<EmissionTotal> emissionsES1Gray = null;
    private List<EmissionTotal> emissionsES2Gray = null; 

    private transient Collection<SelectItem> fee1PickList = null;
    private transient Collection<SelectItem> fee2PickList = null;
    private Integer fee1;
    private Integer fee2;
    
    private boolean evenAdditionalEmissions;
    private boolean oddAdditionalEmissions;
    
    ContactUtil activeBilling = null;
    ContactUtil activePrimary = null;

    private StringBuffer currentOwnersAddresses;
    private StringBuffer currentBillingAddress;
    private StringBuffer currentPrimaryAddress;
    private String billingDateRange;
    private String primaryDateRange;
    
    private StringBuffer prevOwnerAddress;
    private StringBuffer newOwnerAddress;
    private StringBuffer newBillingAddress;
    private StringBuffer newPrimaryAddress;

    private Timestamp contactRefDate;  // what effective date to use

    private List<SelectItem> currentPrevOwnerPickList = null;
    private List<SelectItem> currentNewOwnerPickList = null;
    private List<SelectItem> currentNewBillingPickList = null;
    private List<SelectItem> currentNewPrimaryPickList = null;

    List<SelectItem> pickListTransfers;
    Timestamp pickedTransferDate = null;  // Set from jsp
    Timestamp knownShutdownDate; // From Facility inventory
    boolean needApproveConfirm = false;
    String approveConfirmText = SystemPropertyDef.getSystemPropertyValue("NTV_ApproveWarningMsg", null);
    
    private boolean shutdownIndicator;
    private boolean ownerChangeIndicator;
    private boolean systemOwnerChangeIndicator;
    Integer purchaseSold = null;  // for radio buttons to set this.

    boolean soldReducedRequirement;
    boolean shutdownReducedRequirement;
    private String displayMsg;
    static final String noRptNeededMsg = "No reporting required, delete this report.";
    static final String ownerChangeMsg = "Because of ownership change, no emissions reporting needed, complete what is known of address changes and submit this report";
    static final String ownerChangeMsgOldRpt = "Because of ownership change, no emissions reporting needed, please communicate to AQD any contact information you have for the party that may owe this report.";
    static final String shutdownMsg = "Because of facility shutdown,  no emissions reporting needed, complete what is known of address changes and submit this report.";
    static final String ownerShutdownMsg = "Because of ownership change and facility shutdown, no emissions reporting needed, complete what is known of address changes and submit this report.";

    static public final String NO_REPORT = "no approved report";
    static public final String NO_EMISSIONS_REPORT = "no emissions reported";

    Contact contact;  // Used for each kind of contact.
    NTVContactType contactType;
    boolean contactModify;
    
    EmissionsReport prevRptEven;
    EmissionsReport prevRptOdd;
    Integer prevRptEvenYear;
    Float prevRptEvenYearTons;
    String prevRptEvenYearString;
    Integer prevRptOddYear;
    Float prevRptOddYearTons;
    String prevRptOddYearString;
    
    // Info about years being modified.
    boolean decideModRpt = false;
    Integer selectModRpt = null;
    EmissionsRptInfo evenYearModInfo = null;
    EmissionsRptInfo oddYearModInfo = null;
    EmissionsRptInfo evenUsefull = null;
    EmissionsRptInfo oddUsefull = null;
    
    // How do contacts compare
    String billingCompare;
    //String thisBillOnly;
    String primaryCompare;
    
	private FacilityService facilityService;
	private InfrastructureService infrastructureService;
	private boolean includeAllAttachments = true;
	
	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    public ErNTVBase() {
        super();
    }

//  Code is in the derived class to invoke getReport(ReportProfile rpb)
    protected abstract EmissionsReport getReport1();

    protected abstract EmissionsReport getReport2();

    public EmissionsReport getReport() {
        getReport1(this);
        return report;
    }
    public EmissionsReport getPrimaryReport() {
        getReport1(this);
        return report;
    }

    public EmissionsReport getReport2(ErNTVBase rpb) {
        getReport1(rpb);
        return report2;
    }
    
    public EmissionsReport getReport1(ErNTVBase rpb) {
        EmissionsReport rtn;
        try {
            setErr(false);
            rtn = getReport1Internal(rpb);
        }  catch(Exception e) {
            handleException(reportId, e);
            rtn = null;
            setErr(true);
        }
        return rtn;
    }

    private EmissionsReport getReport1Internal(ErNTVBase rpb) {
        if ((ntvReport == null) || savedReportId != reportId
                || (!reportId.equals(ntvReport.getProvidedRptId()))) {
            ReportSearch rs = (ReportSearch)FacesUtil
            .getManagedBean("reportSearch");
            rs.setPopupRedirectOutcome(null);
            passedValidation = null;
            try {
                ntvReport = getEmissionsReportService().retrieveNtvEmissionsReport(reportId, inStaging);
                if(ntvReport == null) {
                    setErr(true);
                    DisplayUtil.displayError("Failed to Read Report " + reportId);
                    logger.error("Failed to Read Report "
                            + reportId + ", inStaging=" + inStaging);
                    return null;
                }
                savedReportId = reportId;  // Save info based upon reportA
 
                report1 = ntvReport.getReport1();
                report2 = ntvReport.getReport2();
                report = ntvReport.getPrimary();
                if(report == null) {
                    setErr(true);
                    DisplayUtil.displayError("Failed to Correctly Read Report " + reportId);
                    logger.error("Failed to Correctly Read Report "
                            + reportId + ", inStaging=" + inStaging);
                    return null;
                }
                attachments = report.getAttachments();
                
                facility = getFacilityService().retrieveFacilityProfile(report.getFpId(), false);
                if(facility == null) {
                    setErr(true);
                    DisplayUtil.displayError("Failed to Read Facility with fpId " + report.getFpId());
                    logger.error("Failed to Read Facility with fpId "
                            + report.getFpId() + ", inStaging=" + inStaging);
                    return null;
                }
                if(!facility.getVersionId().equals(-1)) {
                    Facility cFacility = null;
                    cFacility = getFacilityService().retrieveFacility(facility.getFacilityId());
                    if(cFacility == null) {
                        setErr(true);
                        DisplayUtil.displayError("Failed to Read current Facility "
                                + facility.getFacilityId());
                        logger.error("Failed to Read current Facility "
                                + facility.getFacilityId());
                        return null;
                    }
                    facility = cFacility;
                }
                fpId = facility.getFpId();
                // keep report referring to current facility inventory
                // up until approval.
                if(report.getRptApprovedStatusDate() == null) {
                    if(report1 != null) {
                        report1.setFpId(fpId);
                    }
                    if(report2 != null) {
                        report2.setFpId(fpId);
                    }
                }
                
                // Get facility owners
                Contact[] c = facility.getOwners();
                currentOwnersAddresses = new StringBuffer();
                for(Contact con : c) {
                    con.appendContactInfo(currentOwnersAddresses);
                    currentOwnersAddresses.append("<br><br>");
                }
                if(c.length > 0) {
                    currentOwnersAddresses.delete(currentOwnersAddresses.length() - 2, currentOwnersAddresses.length());
                }
                revisedReportNotProcessedMsg = null;
                if(isInternalApp() && report.getReportModified() != null) {
                    
                    EmissionsReport r =getEmissionsReportService().
                            retrieveEmissionsReportRow(
                            report.getReportModified(), false);
                    if(r == null) {
                        String errStr = "Failed to read previous report " + report.getReportModified();
                        DisplayUtil.displayError(errStr);
                        logger.error(errStr);
                        return null;
                    }
                    if(!r.processed()) {
                        revisedReportNotProcessedMsg = "This report is a revision of report "
                            + report.getReportModified() + " which has not been approved/rejected";
                        DisplayUtil.displayInfo(revisedReportNotProcessedMsg);
                    }
                }
                displayMsg = null;
                soldReducedRequirement = false;
                shutdownReducedRequirement = false;

                // Determine if NTV reporting allowed for the years based upon category
                // and state of yearly reporting table

                if(report1 == null && report2 == null) {
                    displayMsg = noRptNeededMsg;  // TODO this will never happen
                } else {
                    // Determine ownership change choices
                    pickListTransfers = facility.
                        ownershipChange(ntvReport.getLeftPoint());
                    pickedTransferDate = null;

                    if(report.getTransferDate() == null) {
                        // Have not saved the transfer date yet.
                        purchaseSold = null;
                        existingPurchaseOwner = true;
                    } else { // report.getTransferDate() != null
                        if(report.isNewOwner()) {
                            existingPurchaseOwner = true;
                            purchaseSold = NtvReport.NEW_OWNER;
                        } else {
                            purchaseSold = NtvReport.OLD_OWNER;
                            // Since sold, this owner is not the curent owner
                            existingPurchaseOwner =  false;
                        }
                    }

                    ownerChangeIndicator =  report.getTransferDate() != null;
                    systemOwnerChangeIndicator = !pickListTransfers.isEmpty();

                    // Can Address Information be supplied
                    // Any newer reports (staging or internal schema)?
                    allowContactChange = getEmissionsReportService().onlyOlderReports(
                            facility, ntvReport);

                    // False if there is an existing transfer date that is newer than the one specified in the report.
                    if(allowContactChange && ntvReport.getPrimary().getTransferDate() != null) {
                        for(SelectItem si : pickListTransfers) {
                            if(ntvReport.getPrimary().getTransferDate().before((Timestamp)si.getValue())) {
                                allowContactChange = false;
                                break;
                            }
                        }
                    }
                    // END copied to BO
                    needNewOwnerInfo = ownerChangeIndicator && allowContactChange;
                    allowPrimaryAddress = allowContactChange && (!ownerChangeIndicator || NtvReport.NEW_OWNER.equals(purchaseSold));
                    if(needNewOwnerInfo) {
                        // If transfer date already known, then not new owner (one that can be updated)
                        for(SelectItem si : pickListTransfers) {
                            if(ntvReport.getPrimary().getTransferDate().equals((Timestamp)si.getValue())) {
                                needNewOwnerInfo = false;
                                break;
                            }
                        }
                    }

                    knownShutdownDate = facility.getShutdownDate();
                    shutdownIndicator = report.getShutdownDate() != null || knownShutdownDate != null;
                    
                    needApproveConfirm = report.getShutdownDate() != null;
                    ntvReport.determineReportable(facility);
                    contactRefDate = ntvReport.getContactRefDate();
                    soldReducedRequirement = ntvReport.isSoldReducedRequirement();
                    shutdownReducedRequirement = ntvReport.isShutdownReducedRequirement();
                    report1 = ntvReport.getReportV1();
                    report2 = ntvReport.getReportV2();
                }
                
                // Determine whether to reset emissions range for year not being reported
                if(report1 == null & ntvReport.getReport1() != null) {
                    ntvReport.getReport1().setFeeId(null);
                }
                if(report2 == null & ntvReport.getReport2() != null) {
                    ntvReport.getReport2().setFeeId(null);
                }
                
                if(report1 == null && report2 == null) {
                    if(shutdownReducedRequirement && soldReducedRequirement) {
                        displayMsg = ownerShutdownMsg;
                    } else if(shutdownReducedRequirement) {
                        displayMsg = shutdownMsg;
                    } else {
                        if(allowContactChange) {
                            displayMsg = ownerChangeMsg;
                        } else {
                            displayMsg = ownerChangeMsgOldRpt;
                        }
                    }
                }

                evenAdditionalEmissions = false;
                oddAdditionalEmissions = false;
                if(report1 != null ) {
                    locatedScReports = retrieveSCEmissionsReports(report1, facility.getFacilityId());
                    locatedScReports.displayMsgs();
                    if(locatedScReports.isFailed()) {
                        setErr(true);
                        return null;
                    }
                    //setIncludeES1(report1.isRptES());
                    fee1PickList = locatedScReports.getSc().getFeePickList(EmissionReportsRealDef.NTV, minValue, "; enumerate");
                    emissionsFer1 = report1.getFerPollutants();
                    fee1 = report1.getFeeId();
                    displayTotal1 = getReportTotal(report1);
                    emissionsES1Gray = report1.getESList(locatedScReports);
                    nonAttainment1 = !getEmissionsReportService().retrieveCompliance(report1.getReportYear(),
                            facility.getPhyAddr().getCountyCd());
                    // Should FER details be provided.
                    should1DetailsBeProvided();
                }

                if(report2 != null ) {
                    locatedScReports2 = retrieveSCEmissionsReports(report2, facility.getFacilityId());
                    locatedScReports2.displayMsgs();
                    if(locatedScReports2.isFailed()) {
                        setErr(true);
                        return null;
                    }
                    //setIncludeES2(report2.isRptES());
                    fee2PickList = locatedScReports2.getSc().getFeePickList(EmissionReportsRealDef.NTV, minValue, "; enumerate");
                    emissionsFer2 = report2.getFerPollutants();
                    fee2 = report2.getFeeId();
                    displayTotal2 = getReportTotal(report2);
                    emissionsES2Gray = report2.getESList(locatedScReports2);
                    nonAttainment2 = !getEmissionsReportService().retrieveCompliance(report2.getReportYear(),
                            facility.getPhyAddr().getCountyCd());
                    // Should FER details be provided
                    should2DetailsBeProvided();
                }
                
                // Get previous years information.
                prevRptEvenYear = new Integer(ntvReport.getOddYear() - 3);
                EmissionsReport rPrevEven = getEmissionsReportService().
                    retrieveLatestEmissionReport(prevRptEvenYear,facility.getFacilityId());
                if(rPrevEven == null) {
                    prevRptEvenYearString = NO_REPORT;
                    prevRptEvenYearTons = null;
                } else {
                    prevRptEvenYearTons = rPrevEven.getTotalEmissions();
                    if(prevRptEvenYearTons == null) {
                        prevRptEvenYearString = NO_EMISSIONS_REPORT;
                        if(rPrevEven.getFeeId() != null) {
                            Fee f = getInfrastructureService().retrieveFee(rPrevEven.getFeeId());
                            if(f != null) {
                                prevRptEvenYearString = f.getDescription(EmissionReportsRealDef.NTV, 0, "");
                            }
                        }
                    }
                }
                
                prevRptOddYear = new Integer(ntvReport.getOddYear() - 2);
                EmissionsReport rPrevOdd = getEmissionsReportService().
                    retrieveLatestEmissionReport(prevRptOddYear,facility.getFacilityId());
                if(rPrevOdd == null) {
                    prevRptOddYearString = NO_REPORT;
                    prevRptOddYearTons = null;
                } else {
                    prevRptOddYearTons = rPrevOdd.getTotalEmissions();
                    if(prevRptOddYearTons == null) {
                        prevRptOddYearString = NO_EMISSIONS_REPORT;
                        if(rPrevOdd.getFeeId() != null) {
                            Fee f = getInfrastructureService().retrieveFee(rPrevOdd.getFeeId());
                            if(f != null) {
                                prevRptOddYearString = f.getDescription(EmissionReportsRealDef.NTV, 0, "");
                            }
                        }
                    }
                }

                // Determine existing contacts to display
                activeBilling = facility.getActiveContact(ContactTypeDef.BILL, contactRefDate);                    
                //activePrimary = facility.getActiveContact(ContactTypeDef.PRIM, contactRefDate);   
                currentBillingAddress = new StringBuffer();
                billingDateRange = "";
                if(activeBilling != null) {
                    activeBilling.getContact().appendContactInfo(currentBillingAddress);
                    billingDateRange = activeBilling.getContactType().getDateRange();
                }
                currentPrimaryAddress = null;
                /*if(activePrimary != null) {
                    currentPrimaryAddress = new StringBuffer();
                    activePrimary.getContact().appendContactInfo(currentPrimaryAddress);
                    primaryDateRange = activePrimary.getContactType().getDateRange();
                }*/

                // Create pick list of all current contacts
                // including the new ones being added
                currentPrevOwnerPickList = new ArrayList<SelectItem>();
                currentNewOwnerPickList = new ArrayList<SelectItem>();
                currentNewBillingPickList = new ArrayList<SelectItem>();
                currentNewPrimaryPickList = new ArrayList<SelectItem>();
                if(report.getNewOwnerAddr() != null) {
                    currentPrevOwnerPickList.add(new SelectItem(
                            report.getNewOwnerAddr(), "(same as new owner info)"));
                    currentNewOwnerPickList.add(new SelectItem(
                            report.getNewOwnerAddr(), "(same as new owner info)"));
                    currentNewBillingPickList.add(new SelectItem(
                            report.getNewOwnerAddr(), "(same as new owner info)"));
                    currentNewPrimaryPickList.add(new SelectItem(
                            report.getNewOwnerAddr(), "(same as new owner info)"));
                }
                if(report.getBillingAddr() != null) {
                    currentPrevOwnerPickList.add(new SelectItem(
                            report.getBillingAddr(), "(same as new billing info)"));
                    currentNewOwnerPickList.add(new SelectItem(
                            report.getBillingAddr(), "(same as new billing info)"));
                    currentNewBillingPickList.add(new SelectItem(
                            report.getBillingAddr(), "(same as new billing info)"));
                    currentNewPrimaryPickList.add(new SelectItem(
                            report.getBillingAddr(), "(same as new billing info)"));
                }
                if(report.getPrimaryAddr() != null) {
                    currentPrevOwnerPickList.add(new SelectItem(
                            report.getPrimaryAddr(), "(same as new primary info)"));
                    currentNewOwnerPickList.add(new SelectItem(
                            report.getPrimaryAddr(), "(same as new primary info)"));
                    currentNewBillingPickList.add(new SelectItem(
                            report.getPrimaryAddr(), "(same as new primary info)"));
                    currentNewPrimaryPickList.add(new SelectItem(
                            report.getPrimaryAddr(), "(same as new primary info)"));
                }
                if(report.getPrevOwnerForwardingAddr() != null) {
                    currentPrevOwnerPickList.add(new SelectItem(
                            report.getPrevOwnerForwardingAddr(), "(same as previous owner forwarding)"));
                    currentNewOwnerPickList.add(new SelectItem(
                            report.getPrevOwnerForwardingAddr(), "(same as previous owner forwarding)"));
                    currentNewBillingPickList.add(new SelectItem(
                            report.getPrevOwnerForwardingAddr(), "(same as previous owner forwarding)"));
                    currentNewPrimaryPickList.add(new SelectItem(
                            report.getPrevOwnerForwardingAddr(), "(same as previous owner forwarding)"));
                }

                facility.currentContactsPickList(currentPrevOwnerPickList, null);
                facility.currentContactsPickList(currentNewBillingPickList, null);
                facility.currentContactsPickList(currentNewPrimaryPickList, null);
                
                if(report.getTransferDate() == null) {
                    //facility.currentContactsPickList(currentNewOwnerPickList, ContactTypeDef.OWNR);
                } else {
                    facility.currentContactsPickList(currentNewOwnerPickList, null);
                }

                prevOwnerAddress = null;
                if(report.getPrevOwnerForwardingAddr() != null) {
                    report.getPrevOwnerForwardingAddr().setFacilityId(facility.getFacilityId());
                    prevOwnerAddress = new StringBuffer();
                    report.getPrevOwnerForwardingAddr().appendContactInfo(prevOwnerAddress);
                }
                
                newOwnerAddress = null;
                if(report.getNewOwnerAddr() != null) {
                    report.getNewOwnerAddr().setFacilityId(facility.getFacilityId());
                    newOwnerAddress = new StringBuffer();
                    report.getNewOwnerAddr().appendContactInfo(newOwnerAddress);
                }
                
                newBillingAddress = null;
                if(report.getBillingAddr() != null) {
                    report.getBillingAddr().setFacilityId(facility.getFacilityId());
                    newBillingAddress = new StringBuffer();
                    report.getBillingAddr().appendContactInfo(newBillingAddress);
                    // How does it compare to existing
                    report.getBillingAddr().setFacilityId(facility.getFacilityId());
                    Contact pB = getFacilityService().retrieveFacilityContact(report.getBillingAddr());
                    boolean updatedBillingExists = false;
                    boolean updatedBillingSameName = false;
                    boolean updatedBillingSameInfo = false;
                    updatedBillingExists = pB != null;
                    if(updatedBillingExists) {
                        if(activeBilling != null) {
                            updatedBillingSameName =
                                pB.getContactId().equals(activeBilling.getContact().getContactId());
                        }
                        updatedBillingSameInfo = pB.equalsIgnoreCase(report.getBillingAddr());
                    }
                    billingCompare = contactCompare(updatedBillingExists,
                            updatedBillingSameName, updatedBillingSameInfo);
                 //   if(activeBilling != null) {
                 //       thisBillOnly = activeBilling.getContactType().getEndDate()==null?"":"For this report only";
                 //   }
                }
                
                newPrimaryAddress = null;
                if(report.getPrimaryAddr() != null) {
                    report.getPrimaryAddr().setFacilityId(facility.getFacilityId());
                    newPrimaryAddress = new StringBuffer();
                    report.getPrimaryAddr().appendContactInfo(newPrimaryAddress);
                    // How does it compare to existing
                    report.getPrimaryAddr().setFacilityId(facility.getFacilityId());
                    Contact pC = getFacilityService().retrieveFacilityContact(report.getPrimaryAddr());
                    boolean updatedPrimaryExists = false;
                    boolean updatedPrimarySameName = false;
                    boolean updatedPrimarySameInfo = false;
                    updatedPrimaryExists = pC != null;
                    if(updatedPrimaryExists) {
                        if(activePrimary != null) {
                            updatedPrimarySameName =
                                pC.getContactId().equals(activePrimary.getContact().getContactId());
                        }
                        updatedPrimarySameInfo = pC.equalsIgnoreCase(report.getPrimaryAddr());
                    }
                    primaryCompare = contactCompare(updatedPrimaryExists,
                            updatedPrimarySameName, updatedPrimarySameInfo);
                }

                rpb.processNotes(report);
                if(this.isInternalApp()) {
                    us.oh.state.epa.stars2.app.emissionsReport.ReportDetail rd = 
                        (us.oh.state.epa.stars2.app.emissionsReport.ReportDetail)FacesUtil
                    .getManagedBean("reportDetail");
                    rd.setReportId(reportId);  // needed to keep track of last report examined.
                } else {
                    us.oh.state.epa.stars2.portal.emissionsReport.ReportDetail rd = 
                        (us.oh.state.epa.stars2.portal.emissionsReport.ReportDetail)FacesUtil
                    .getManagedBean("reportDetail");
                    rd.setReportId(reportId);  // needed to keep track of last report examined.
                }
                
                // Determine if reporting category has changed.
                // TODO
            } catch (Exception e) {
                DisplayUtil.displayError("Failed to Read Report Information");
                handleException(report.getEmissionsRptId(), e);
                setErr(true);
            }
        }
        if(!isErr() && !isReloadRpt()) {
            setAdminEditable(true);
        }
        return report1;
    }
    
    void should1DetailsBeProvided() {
        // Should FER details be provided.
        evenAdditionalEmissions = false;
        if(report1.getFeeId() != null) {
            for(Fee f : locatedScReports.getSc().getFees()) {
                if(f.getFeeId().equals(report1.getFeeId())
                        && f.getLowRange() != null) {
                    if(f.getLowRange().intValue() >= minValue) {
                        evenAdditionalEmissions = true;
                    }
                    break;
                }
            }
        }
    }
    
    public boolean isGeneralUser() {
        boolean ret = false;

        if (isInternalApp()) {
            ret = InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("generalUser");
        }

        return ret;
    }
    
    void should2DetailsBeProvided() {
        // Should details be provided
        oddAdditionalEmissions = false;
        if(report2.getFeeId() != null) {
            for(Fee f : locatedScReports2.getSc().getFees()) {
                if(f.getFeeId().equals(report2.getFeeId())
                        && f.getLowRange() != null) {
                    if(f.getLowRange().intValue() >= minValue) {
                        oddAdditionalEmissions = true;
                    }
                    break;
                }
            }
        }
    }
    
    String getReportTotal(EmissionsReport rpt) {
          if(rpt.getTotalEmissions() == null) return " ";
          return EmissionRow.getTotalString(rpt.getTotalEmissions().doubleValue());
      }

    public final String adminCancel() {
        setAdminEditable(false);
        setReloadRpt(true);
        ntvReport = null;
        getReport1();  // read NTV report back in.
        return NTV_REPORT;
    }

    public final void adminEdit() {
        setAdminEditable(true);
        return;
    }
    
    public String adminSave() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return adminSaveInternal();
        } finally {
            clearButtonClicked();
        }
    }

    private final String adminSaveInternal() {
        if(pickedTransferDate != null) {
            report.setTransferDate(pickedTransferDate);
        }
        report.clearValidationMessages();
        List<ValidationMessage> validationMessages = report.verifyNTVOwnerShutdown(
                isInternalApp(), ownerChangeIndicator,
                purchaseSold, shutdownIndicator && knownShutdownDate == null, pickListTransfers);
        boolean hasError = displayValidationMessages(
                "detailPage:", validationMessages.toArray(new ValidationMessage[0]));
        if (hasError) return NTV_REPORT;
        setAdminEditable(false);
        setReloadRpt(true);
        passedValidation = null;
        boolean ok = true;
        try {
            ntvReport.determineReportable(facility); // Has an admin change changed the years reported?
            getEmissionsReportService().modifyEmissionsReport(facility, ntvReport, minValue, scReports, scReports2, openedForEdit);
        } catch (RemoteException re) {
            DisplayUtil.displayError("Failed to Save Report Changes");
            handleException(report.getEmissionsRptId(), re);
            ok = false;
            setErr(true);
        }
        if(ok) {
            ntvReport = null;
            getReport1();  // read NTV report back in.
        }
        return NTV_REPORT;
    }

    public final void edit() {
        setEditable(true);
        return;
    }

    public final String cancel() {
        setEditable(false);
        ntvReport = null;
        getReport1();  // read NTV report back in.
        return NTV_REPORT;
    }
    
    public String save() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return saveInternal();
        } finally {
            clearButtonClicked();
        }
    }

    private final String saveInternal() {
        boolean ok = true;
        setEditable(false);
        passedValidation = null;
        try {
            getEmissionsReportService().modifyEmissionsReport(facility, ntvReport, minValue, scReports, scReports2, openedForEdit);
        } catch (RemoteException re) {
            DisplayUtil.displayError("Failed to Save Report Changes");
            handleException(ntvReport.getPrimary().getEmissionsRptId(), re);
            ok = false;
            setErr(true);
        }
        if(ok) {
            ntvReport = null;
            getReport1();  // read NTV report back in. 
        }
        return NTV_REPORT;
    }

    public final String editForward() {
        if(report.getPrevOwnerForwardingAddr() == null) {
            contact = new Contact();
            contactModify = false;
        } else {
            contact = new Contact(report.getPrevOwnerForwardingAddr());
            contactModify = true;
        }
        contactType = NTVContactType.oldOwner;
        setEditable1(true);
        return "dialog:editContact";
    }

    public final String editPrimary() {
        if(report.getPrimaryAddr() == null) {
            contact = new Contact();
            contactModify = false;
        } else {
            contact = new Contact(report.getPrimaryAddr());
            contactModify = true;
        }
        contactType = NTVContactType.primary;
        setEditable1(true);
        return "dialog:editContact";
    }

    public final String editBilling() {
        if(report.getBillingAddr() == null) {
            contact = new Contact();
            contactModify = false;
        } else {
            contact = new Contact(report.getBillingAddr());
            contactModify = true;
        }
        contactType = NTVContactType.billing;
        setEditable1(true);
        return "dialog:editContact";
    }

    public final String editNewOwner() {
        if(report.getNewOwnerAddr() == null) {
            contact = new Contact();
            contactModify = false;
        } else {
            contact = new Contact(report.getNewOwnerAddr());
            contactModify = true;
        }
        contactType = NTVContactType.newOwner;
        setEditable1(true);
        return "dialog:editContact";
    }

    public final void cancelAddContact() {
        setEditable1(false);
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public void saveEditContact() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            saveEditContactInternal();
        } finally {
            clearButtonClicked();
        }
    }

    private final void saveEditContactInternal() {
        ValidationMessage[] validationMessages = contact.validate();
        boolean ok = true;
        boolean hasError = displayValidationMessages(
                "detailPage:", validationMessages);
        if(hasError) return;
        try {
            getEmissionsReportService().saveContact(report, contactType, contactModify, contact);
        } catch (RemoteException re) {
            DisplayUtil.displayError("Failed to Save Contact Information");
            handleException(report.getEmissionsRptId(), re);
            ok = false;
            setErr(true);
        }
        setEditable1(false);
        if(ok) {
            ntvReport = null;
            getReport1();  // read NTV report back in.
        }
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public String removeForward() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return removeForwardInternal();
        } finally {
            clearButtonClicked();
        }
    }

    private final String removeForwardInternal() {
        boolean ok = true;
        try {
            getEmissionsReportService().saveContact(report, NTVContactType.oldOwner,
                    false, null);
        } catch (RemoteException re) {
            DisplayUtil.displayError("Failed to Remove Forwarding Contact Information");
            handleException(report.getEmissionsRptId(), re);
            ok = false;
            setErr(true);
        }
        if(ok) {
            ntvReport = null;
            getReport1();  // read NTV report back in.
        }
        return null;
    }
    
    public String removeBilling() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return removeBillingInternal();
        } finally {
            clearButtonClicked();
        }
    }

    private final String removeBillingInternal() {
        boolean ok = true;
        try {
            getEmissionsReportService().saveContact(report, NTVContactType.billing,
                    false, null);
        } catch (RemoteException re) {
            DisplayUtil.displayError("Failed to Remove Billing Contact Information");
            handleException(report.getEmissionsRptId(), re);
            ok = false;
            setErr(true);
        }
        if(ok) {
            ntvReport = null;
            getReport1();  // read NTV report back in.
        }
        return null;
    }
    
    public String removePrimary() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return removePrimaryInternal();
        } finally {
            clearButtonClicked();
        }
    }

    private final String removePrimaryInternal() {
        boolean ok = true;
        try {
            getEmissionsReportService().saveContact(report, NTVContactType.primary,
                    false, null);
        } catch (RemoteException re) {
            DisplayUtil.displayError("Failed to Remove Primary Contact Information");
            handleException(report.getEmissionsRptId(), re);
            ok = false;
            setErr(true);
        }
        if(ok) {
            ntvReport = null;
            getReport1();  // read NTV report back in.
        }
        return null;
    }

    public String removeNewOwner() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return removeNewOwnerInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private final String removeNewOwnerInternal() {
        boolean ok = true;
        try {
            getEmissionsReportService().saveContact(report, NTVContactType.newOwner,
                    false, null);
        } catch (RemoteException re) {
            DisplayUtil.displayError("Failed to Remove New Owner Information");
            handleException(report.getEmissionsRptId(), re);
            ok = false;
            setErr(true);
        }
        if(ok) {
            ntvReport = null;
            getReport1();  // read NTV report back in.
        }
        return null;
    }

    public final String loadContactAddress() {
        contact.getAddress().copyAddress(facility.getPhyAddr());
        return null;
    }

    public final Facility getFacility() {
        getReport1(this);
        return facility;
    }

    public List<EmissionTotal> getEmissionsES1() {
        return emissionsES1;
    }

    public void setEmissionsES1(List<EmissionTotal> emissionsES1) {
        this.emissionsES1 = emissionsES1;
    }

    public List<EmissionTotal> getEmissionsES2() {
        return emissionsES2;
    }

    public void setEmissionsES2(List<EmissionTotal> emissionsES2) {
        this.emissionsES2 = emissionsES2;
    }

    public List<EmissionTotal> getEmissionsFer2() {
        return emissionsFer2;
    }

    public List<EmissionTotal> getEmissionsFer1() {
        return emissionsFer1;
    }

    public NtvReport getNtvReport() {
        return ntvReport;
    }

    public Integer getPurchaseSold() {
        return purchaseSold;
    }

    public void setPurchaseSold(Integer purchaseSold) {
        this.purchaseSold = purchaseSold;
        if(purchaseSold != null) {
            if(purchaseSold.equals(NtvReport.NEW_OWNER)) {
                report.setNewOwner(true);
            } else if(purchaseSold.equals(NtvReport.OLD_OWNER)) {
                report.setNewOwner(false);
            }
        }
    }

    public boolean isShutdownIndicator() {
        return shutdownIndicator;
    }

    public void setShutdownIndicator(boolean shutdownIndicator) {
        if(!shutdownIndicator) {
            report.setShutdownDate(null);
        }
        this.shutdownIndicator = shutdownIndicator;
    }

    public boolean isOwnerChangeIndicator() {
        return ownerChangeIndicator;
    }

    public void setOwnerChangeIndicator(boolean ownerChangeIndicator) {
        if(!ownerChangeIndicator) {
            report.setTransferDate(null);
            purchaseSold = null;
        }
        this.ownerChangeIndicator = ownerChangeIndicator;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public StringBuffer getPrevOwnerAddress() {
        return prevOwnerAddress;
    }

    public Contact getPrevOwnerCont() {
        return null;
    }

    public void setPrevOwnerCont(Contact prevOwnerCont) {
        if(prevOwnerCont == null) return;
        boolean ok = true;
        Contact con = new Contact(prevOwnerCont);
        try {
            if(report.getPrevOwnerForwardingAddr() != null) {
                keepExistingIdentity(con, report.getPrevOwnerForwardingAddr());
                getEmissionsReportService().saveContact(report, NTVContactType.oldOwner,
                        true, con);
            } else {
                getEmissionsReportService().saveContact(report, NTVContactType.oldOwner,
                        false, con);
            }
            report.getPrevOwnerForwardingAddr().setFacilityId(facility.getFacilityId());
        } catch (RemoteException re) {
            ok = false;
            handleException(report.getEmissionsRptId(), re);
        }
        if (!ok) {
            DisplayUtil.displayError("Failed to Change Previous Owner Information");
            setErr(true);
        } else {
            DisplayUtil.displayInfo("Previous Owner Information Changed");
            ntvReport = null;
            getReport1();  // read NTV report back in. 
        }
        return;
    }

    public String getDisplayMsg() {
        return displayMsg;
    }

    public Contact getNewBillingCont() {
        return null;
    }

    public void setNewBillingCont(Contact newBillingCont) {
        if(newBillingCont == null) return;
        boolean ok = true;
        Contact con = new Contact(newBillingCont);
        try {
            if(report.getBillingAddr() != null) {
                keepExistingIdentity(con, report.getBillingAddr());
                getEmissionsReportService().saveContact(report, NTVContactType.billing,
                        true, con);
            } else {
                getEmissionsReportService().saveContact(report, NTVContactType.billing,
                        false, con);
            }
            report.getBillingAddr().setFacilityId(facility.getFacilityId());
        } catch (RemoteException re) {
            ok = false;
            handleException(report.getEmissionsRptId(), re);
        }
        if (!ok) {
            DisplayUtil.displayError("Failed to Change Billing Information");
            setErr(true);
        } else {
            DisplayUtil.displayInfo("Billing Information Changed");
            ntvReport = null;
            getReport1();  // read NTV report back in. 
        }
        return;
    }
    
    private void keepExistingIdentity(Contact copyTo, Contact copyFrom) {
        copyTo.setContactId(copyFrom.getContactId());
        copyTo.setLastModified(copyFrom.getLastModified());
        copyTo.getAddress().setLastModified(
                copyFrom.getAddress().getLastModified());
        copyTo.setAddressId(copyFrom.getAddressId());
        copyTo.setContactTypes(new ArrayList<ContactType>());
        copyTo.getAddress().setAddressId(
                copyFrom.getAddressId());
    }

    public Contact getNewPrimaryCont() {
        return null;
    }

    public void setNewPrimaryCont(Contact newPrimaryCont) {
        if(newPrimaryCont == null) return;
        boolean ok = true;
        Contact con = new Contact(newPrimaryCont);
        try {
            if(report.getPrimaryAddr() != null) {
                keepExistingIdentity(con, report.getPrimaryAddr());
                getEmissionsReportService().saveContact(report, NTVContactType.primary,
                        true, con);
            } else {
                getEmissionsReportService().saveContact(report, NTVContactType.primary,
                        false, con);
            }
            report.getPrimaryAddr().setFacilityId(facility.getFacilityId());
        } catch (RemoteException re) {
            ok = false;
            handleException(report.getEmissionsRptId(), re);
        }
        if (!ok) {
            DisplayUtil.displayError("Failed to Change Primary Contact Information");
            setErr(true);
        } else {
            DisplayUtil.displayInfo("Primary Contact Information Changed");
            ntvReport = null;
            getReport1();  // read NTV report back in. 
        }
        return;
    }


    public Contact getNewOwnerCont() {
        return null;
    }

    public void setNewOwnerCont(Contact newOwnerCont) {
        if(newOwnerCont == null) return;
        boolean ok = true;
        Contact con = new Contact(newOwnerCont);
        try {
            if(report.getNewOwnerAddr() != null) {
                keepExistingIdentity(con, report.getNewOwnerAddr());
                getEmissionsReportService().saveContact(report, NTVContactType.newOwner,
                        true, con);
            } else {
                getEmissionsReportService().saveContact(report, NTVContactType.newOwner,
                        false, con);
            }
            report.getNewOwnerAddr().setFacilityId(facility.getFacilityId());
        } catch (RemoteException re) {
            ok = false;
            handleException(report.getEmissionsRptId(), re);
        }
        if (!ok) {
            DisplayUtil.displayError("Failed to Change New Owner Information");
            setErr(true);
        } else {
            DisplayUtil.displayInfo("New Owner Information Changed");
            ntvReport = null;
            getReport1();  // read NTV report back in.
        }
        return;
    }
    
    public final String ntvDeleteRptReq() {
        return "dialog:deleteNtvReportReq";
    }
    
    public boolean deleteNtvReportI() {
        boolean ok = true;
        try {
            getEmissionsReportService().deleteEmissionsReport(ntvReport);
        } catch (RemoteException re) {
            handleException(ntvReport.getPrimary().getEmissionsRptId(), re);
            ok = false;
        }
        return ok;
    }

    public StringBuffer getNewOwnerAddress() {
        return newOwnerAddress;
    }

    public StringBuffer getNewPrimaryAddress() {
        return newPrimaryAddress;
    }

    public StringBuffer getNewBillingAddress() {
        return newBillingAddress;
    }

    public List<EmissionTotal> getEmissionsES1Gray() {
        return emissionsES1Gray;
    }

    public List<EmissionTotal> getEmissionsES2Gray() {
        return emissionsES2Gray;
    }

    public StringBuffer getCurrentBillingAddress() {
        return currentBillingAddress;
    }

    public String getCurrentOwnersAddresses() {
        return currentOwnersAddresses.length()==0?null:currentOwnersAddresses.toString();
    }

    public StringBuffer getCurrentPrimaryAddress() {
        return currentPrimaryAddress;
    }
    
    public final String startCreateRevisedReport() {
        /*
         * A NTV report is being revised.  If the new report is NTV
         * then the emissions information is carried over.
         * 
         * Locate the newest submitted reports for the years
         * represented by this report.  If there is more than one year
         * and if they are not both NTV, then a decision is
         * needed as to which report year to revise. 
         */
        evenYearModInfo = null;
        oddYearModInfo = null;
        Integer yr = null;
        Integer yr2 = null;
        try {
            if(ntvReport.getReport1() != null) {
                yr = ntvReport.getReport1().getReportYear();
                evenYearModInfo = getEmissionsReportService().getEmissionsRptInfo(
                        ntvReport.getReport1().getReportYear(), facility.getFacilityId());
                if(evenYearModInfo != null) {
                    evenUsefull = evenYearModInfo.canReport();
                    if(evenUsefull == null) {
                        DisplayUtil.displayInfo("Revised report for " +
                                evenYearModInfo.getYear() +
                                "cannot be created because " +
                                evenYearModInfo.whyNotReport());
                    }
                }
            }
            if(ntvReport.getReport2() != null) {
                yr2 = ntvReport.getReport2().getReportYear();
                oddYearModInfo = getEmissionsReportService().getEmissionsRptInfo(
                        ntvReport.getReport2().getReportYear(), facility.getFacilityId());
                if(oddYearModInfo != null) {
                    oddUsefull = oddYearModInfo.canReport();
                    if(oddUsefull == null) {
                        DisplayUtil.displayInfo("Revised report for " +
                                oddYearModInfo.getYear() +
                                "cannot be created because " +
                                oddYearModInfo.whyNotReport());
                    }
                }
            }
            locateNewestRpts(yr, null, yr2, null, facility.getFacilityId());
            // Even if this report is for two years, there may be newer
            // reports for a single year.
            // If there are reports in progress, then cannot revise.
            if(evenUsefull != null && notFiledReport != -1) {
                evenUsefull = null;
                DisplayUtil.displayInfo("Revised report for " +
                        evenYearModInfo.getYear() +
                        " cannot be created because report " +
                        notFiledReport + " can still be updated");
            }
            if(oddUsefull != null && notFiledReport2 != -1) {
                oddUsefull = null;
                DisplayUtil.displayInfo("Revised report for " +
                        oddYearModInfo.getYear() +
                        " cannot be created because report " +
                        notFiledReport2 + " can still be updated");
            }
            // If the latest submitted report is actually two
            // reports, then must select one to modify.
            if(evenUsefull != null && oddUsefull != null) {
                decideModRpt = true;
                selectModRpt = null;
            } else {
                decideModRpt = false;
            }
        } catch (RemoteException re) {
            handleException(ntvReport.getPrimary().getEmissionsRptId(), re);
            setErr(true);
        }
        if(evenUsefull == null && oddUsefull == null) {
            return null;
        }
        if(evenUsefull == null) {
            evenYearModInfo = null;
        }
        if(oddUsefull == null) {
            oddYearModInfo = null;
        }
        return "dialog:reviseNtvReport";
    }
    
    public void startCreateReviseReportDone() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            startCreateReviseReportDoneInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private final void startCreateReviseReportDoneInternal() {
        EmissionsRptInfo rptToCreate = null;
        Boolean setToNTV = null;
        String navigateTo = null;
        if(decideModRpt) {
            // user had a choice of what to create
            if(selectModRpt.intValue() == 1) {
                rptToCreate = evenYearModInfo;
            } else if(selectModRpt.intValue() == 2) {
                rptToCreate = oddYearModInfo;
            } else {
                // User did not select a report.
                DisplayUtil.displayError("Please select a report or cancel");
                return;
            }
        } else {
            if(evenUsefull != null && oddUsefull != null) {
                // Need two year NTV report
                rptToCreate = null;  // indicates two year report
            } else {
                // Creating a one year report
                if(evenUsefull != null) {
                    rptToCreate = evenYearModInfo;
                } else if(oddUsefull != null) {
                    rptToCreate = oddYearModInfo;
                } else {
                    // Error condition should not occur
                    logger.error("Should have been a one year report");
                    DisplayUtil.displayError("Failed to create the report");
                    setErr(true);
                }
            }
        }
        if(!isErr()) {
            // Create the report
            if(rptToCreate == null) {
                // create two year NTV report
                // which copies the emissions from existing report
                // Does user  have permission?
                if(!isNtvIssuance()) {
                    setErr(true);
                    DisplayUtil.displayError("You do not have permission to create a NTV report.");
                }
                if(!isErr()) {
                    ReportTemplates rtA = null;
                    ReportTemplates rtB = null;
                    try {
                        rtA = getEmissionsReportService().retrieveSCEmissionsReports(
                                ntvReport.getEvenYear(), "AC", facility.getFacilityId());
                        rtB = getEmissionsReportService().retrieveSCEmissionsReports(
                                ntvReport.getOddYear(), "AC", facility.getFacilityId());
                    } catch(RemoteException re) {
                        DisplayUtil.displayError("Failed to retrieve report template information.");
                        setErr(true);
                    }
                    if(rtA.isFailed() || rtB.isFailed()) {
                        rtA.displayMsgs();
                        rtB.displayMsgs();
                        setErr(true);
                    } else {
                    NtvReport newRpt = NtvReport.cloneNTVReport(ntvReport, submittedReport);
                        Task rtn  = callNTV_create(newRpt, facility);
                        if(rtn != null) {
                            createRTask = rtn;
                            createRTask.setTaskType(Task.TaskType.R_ER);
                            setToNTV = new Boolean(true);
                            navigateTo = NTV_REPORT;
                            reportId = createRTask.getNtvReport().getPrimary().getEmissionsRptId();
                            ntvReport = null;  //  To load in this new report
                            inStaging = true;
                            setReloadRpt(false);
                            getReport1();
                        }

                    }
                }
            } else {
                    // Create empty TV or SMTV report.
                    // Does user  have permission?
                    if(!isModTV_SMTV()) {
                        setErr(true);
                        DisplayUtil.displayError("You do not have permission to create a TV or SMTV report.");

                    }
                    if(!isErr()) {
                        int rptMod;
                        if(rptToCreate.getYear().intValue()%2 == 0) rptMod = submittedReport;
                        else rptMod = submittedReport2;
                        EmissionsReport rpt = new EmissionsReport();
                        rpt.setReportModified(rptMod);
                        Task rtn = callTV_SMTV_create(facility,
                                rptToCreate.getYear(), rptToCreate.getContentTypeCd(), rpt,
                                rpt.getReportModified());
                        if(rtn != null) {
                            createRTask = rtn;
                            setToNTV = new Boolean(false);
                            navigateTo = NTV_REPORT;
                            ReportProfileBase rP = (ReportProfileBase) FacesUtil
                            .getManagedBean("reportProfile");
                            //  Needed because switching backing beans
                            rP.setReportId(reportId);
                            rP.setCreateRTask(createRTask);
                            rP.setInStaging(inStaging);
                            rP.setCreateFTask(createFTask);
                            rP.setReport(null);
                            rP.setInStaging(true);
                            rP.getReport();  // Read the report.
                        } else {
                            setErr(true);
                        }
                }
            }
        }
        ReportSearch reportSearch = (ReportSearch) FacesUtil
        .getManagedBean("reportSearch");
        if(setToNTV != null) {
            reportSearch.setNtvReport(setToNTV);
        }
        if(!isErr() && this.isPortalApp()) {
            MyTasks mt = (MyTasks) FacesUtil.getManagedBean("myTasks");
            mt.renderEmissionReportMenu(createRTask);
            mt.refreshTasks(facility.getFacilityId());
        }
        reportSearch.setPopupRedirectOutcome(navigateTo);
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    protected Boolean internalNtvValidate(boolean forApprove) {
        List<ValidationMessage> vml1 = new ArrayList<ValidationMessage>();
        boolean fOk = true;
        if(isInternalApp()) { // skip facility validation if from portal
            try {
                vml1 = FacilityValidation.validateNTVemissionReport(fpId, !forApprove);
            } catch (ValidationException ve) {
                fOk = false;
                handleException(ntvReport.getPrimary().getEmissionsRptId(), ve);
                DisplayUtil.displayError("Failed to read the facility--partial validation performed");
                logger.error("Failed to read the facility--partial validation performed.  FPId " +
                        report.getFpId() + ".  ", ve);
            }
        }
        List<ValidationMessage> validationMessages;

        report.clearValidationMessages();
        validationMessages = ntvReport.submitVerify(facility,
                isInternalApp(), needNewOwnerInfo, allowPrimaryAddress, 
                existingPurchaseOwner,
                minValue, scReports, scReports2,
                report1, report2, vml1.size() != 0);

        for (ValidationMessage vm : validationMessages) {
            vm.setReferenceID(ValidationBase.REPORT_TAG + ":" +
                    reportId + ":" + vm.getReferenceID() +
                    ":" + NTV_REPORT);
        }
        validationMessages.addAll(vml1);
        boolean ok = true;
        if(!forApprove) {
            ok = AppValidationMsg.validate(validationMessages, true);
        } else {
            // Don't want validation successful message
            boolean hasErrors = false;
            if (validationMessages != null && !validationMessages.isEmpty()) {
                for (ValidationMessage msg : validationMessages) {
                    if (msg.getSeverity().equals(ValidationMessage.Severity.ERROR)) {
                        hasErrors = true;
                    }
                }
            }
            if(hasErrors) {
                ok = AppValidationMsg.validate(validationMessages, true);
            }
        }
        return new Boolean(ok && fOk);
    }
    
    public final String validationDlgAction() {
        String rtn = super.validationDlgAction();
        if(null != rtn) return rtn;
        return "emissionReport"; // stay on same page
    }
    
    public String ntvValidate() {
        passedValidation = internalNtvValidate(report.isSubmitted());
        boolean haveTS1 = false;
        boolean haveTS2 = false;
        if(report1 != null) haveTS1 = report1.attachmentsContainTradeSecrets();
        if(report2 != null) haveTS2 = report2.attachmentsContainTradeSecrets();
        if (passedValidation && (haveTS1 || haveTS2)) {
        	return "validationTradeSecretNotification";
        } else {
        	return "emissionReportNoRedirect";
        }
    }
    
    // Only called from app side.
    protected String ntvSubmitInternal(String state) {
        boolean ok = true;
        try {
            boolean newWF = getEmissionsReportService().ntvSubmit(ntvReport,
                    facility, state, minValue, scReports, scReports2,
                    getUserID(), false);
            if(!newWF) {
                DisplayUtil.displayWarning("Workflow for this report was previously created. That workflow may BE IN THE WRONG STEP.");
            }
        } catch (RemoteException re) {
            handleException(ntvReport.getPrimary().getEmissionsRptId(), re);
            ok = false;
            setErr(true);
        }
        if (ok) {
            DisplayUtil.displayInfo("Submit Successful");
            //  Read report back in.
            reportId = ntvReport.getPrimary().getEmissionsRptId();
            ntvReport = null;
            getReport1(); // read report
        } else {
            DisplayUtil.displayError("Submit Failed");
        }
        return null;  // stay on same page
    }

    public boolean isNonAttainment1() {
        return nonAttainment1;
    }

    public boolean isNonAttainment2() {
        return nonAttainment2;
    }

    public String getMustEnumerateES() {
        return mustEnumerateES;
    }

    public void setNtvReport(NtvReport ntvReport) {
        this.ntvReport = ntvReport;
    }

    public boolean isEvenAdditionalEmissions() {
        return evenAdditionalEmissions;
    }

    public boolean isOddAdditionalEmissions() {
        return oddAdditionalEmissions;
    }

    public void setInclude1(boolean includeES1) {
        this.includeES1 = includeES1;
        //report1.setRptES(includeES1);
        reportsNeeded(scReports, locatedScReports, report1);
        report1.addNTVPollutants(scReports);
        emissionsES1 = report1.getEsPollutants();
    }

    public void setInclude2(boolean includeES2) {
        this.includeES2 = includeES2;
        //report2.setRptES(includeES2);
        reportsNeeded(scReports2, locatedScReports2, report2);
        report2.addNTVPollutants(scReports2);
        emissionsES2 = report2.getEsPollutants();
    }
    
    public String contactCompare(boolean exists,
            boolean sameName, boolean sameInfo) {
        String rtn;
        if(!exists) {
            rtn = "new name";
        } else if(sameName) {
            if(sameInfo) {
                rtn = "same name, same info";
            } else {
                rtn = "same name, different info";
            }
        } else {
            if(sameInfo) {
                rtn = "existing name, same info";
            } else {
                rtn = "existing name, different info";
            }
        }
        return rtn;
    }
    
    public String submitProfile() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return submitProfileInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private final String submitProfileInternal() {
        FacilityProfileBase fp = (FacilityProfileBase) FacesUtil
        .getManagedBean("facilityProfile");
        fp.initFacilityProfile(fpId, false);  // Do not view staging
        return "goToFacilityPage";
    }

    public String printEmissionReport() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
        	setIncludeAllAttachments(false);
            return printEmissionReportInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private final String printEmissionReportInternal()  {
        List<EmissionTotal> emissionsFer1Pdf = emissionsFer1;
        if(!evenAdditionalEmissions) {
            emissionsFer1Pdf = null;
        }
        List<EmissionTotal> emissionsFer2Pdf = emissionsFer2;
        if(!oddAdditionalEmissions) {
            emissionsFer2Pdf = null;
        }
    
        String ret = "dialog:ntvEmissionReport";
        String user = getUserName();
        
        try {
        	reportDoc = new TmpDocument();
            List<EmissionTotal> emissionsES1Gray1 = null;
            List<EmissionTotal> emissionsES2Gray1 = null;
            emissionsES1Gray1 = includeES1 ? emissionsES1 : emissionsES1Gray;
            emissionsES2Gray1 = includeES2 ? emissionsES2 : emissionsES2Gray;
            reportDocuments = getEmissionsReportService().getPrintableDocumentList(facility, ntvReport,
            		reportDoc, currentOwnersAddresses, prevOwnerAddress, newOwnerAddress,
                    currentBillingAddress, newBillingAddress,
                    currentPrimaryAddress, newPrimaryAddress,
                    prevRptEvenYear, prevRptEvenYearTons, prevRptEvenYearString,
                    fee1PickList, emissionsFer1Pdf,
                    emissionsES1Gray1, nonAttainment1, includeES1,
                    prevRptOddYear, prevRptOddYearTons, prevRptOddYearString,
                    fee2PickList, emissionsFer2Pdf,
                    emissionsES2Gray1, nonAttainment2, includeES2, user, inStaging, hideTradeSecret, includeAllAttachments);
        } catch (RemoteException re) {
            DisplayUtil.displayError("Unable to generate emissions inventory report documents");
            handleException(ntvReport.getPrimary().getEmissionsRptId(), re);
            ret = null;
        }
        
        try {
        	reportAttachments = getEmissionsReportService().getPrintableAttachmentList(null, reportDoc, null, ntvReport.getPrimary().getAttachments(), 
              hideTradeSecret, inStaging, ntvReport.getPrimary().getRptReceivedStatusDate(), includeAllAttachments);
        } catch(RemoteException re) {
            logger.error("getPrintableAttachmentList() failed for report " + report.getEmissionsRptId(), re);
            DisplayUtil.displayError("Unable to generate report attachments");
        }
        if(reportAttachments == null || reportAttachments.size() == 0) reportAttachments = null;
        
        return ret;
    }
    
    public boolean isAllowEdit() {
        boolean rtn = !report.isSubmitted();
        if(isInternalApp()) {
            if(!report.isApproved() || InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
                rtn = true;
            }
        }
        if(!isInternalApp() && !isInStaging()){
            rtn = false;
        }
        return rtn;
    }
    
    public void applyEditNote(ActionEvent actionEvent) {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            applyEditNoteInternal(actionEvent);
        } finally {
            clearButtonClicked();
        }
    }
    
    private final void applyEditNoteInternal(ActionEvent actionEvent) {
        boolean ok = true;
        setEditable(false);
        
        // make sure note is provided
        if (reportNote.getNoteTxt() == null || reportNote.getNoteTxt().trim().equals("")) {
        	DisplayUtil.displayError("Attribute Note is not set.");
        	return;
        }
        
        // TODO  the following validate() just creates empty array.  Do we need anything???
        ValidationMessage[] validationMessages = reportNote.validate();

        if (validationMessages.length > 0) {
            if (displayValidationMessages("", validationMessages)) {
                return;
            }
        }

        try {
            if (!noteModify) {
                report.addNote(reportNote);
            }
            getEmissionsReportService().modifyEmissionsReport(facility, null, report, null, null, openedForEdit);
        } catch (RemoteException re) {
            handleException(ntvReport.getPrimary().getEmissionsRptId(), re);
            ok = false;
            setErr(true);
        }
        if (ok) {
            DisplayUtil.displayInfo("Report note added/updated successfully");
            ntvReport = null;
            getReport1(); // read report
        } else {
            noteModify = false;
            setEditable(false);
            DisplayUtil.displayError("Adding/Updating report note failed");
        }
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public boolean isShowNtvSubmitButton() {
        return !isAnyEditable() && !report.isSubmitted() && (passedValidation!= null && passedValidation.booleanValue());
    }
    
    public boolean isIncludeES1() {
        return includeES1;
    }

    public boolean isIncludeES2() {
        return includeES2;
    }

    public static String getNoRptNeededMsg() {
        return noRptNeededMsg;
    }

    public Timestamp getKnownShutdownDate() {
        return knownShutdownDate;
    }

    public Integer getPrevRptEvenYear() {
        return prevRptEvenYear;
    }

    public Integer getPrevRptOddYear() {
        return prevRptOddYear;
    }

    public Timestamp getPickedTransferDate() {
        return pickedTransferDate;
    }

    public void setPickedTransferDate(Timestamp pickedTransferDate) {
        this.pickedTransferDate = pickedTransferDate;
    }
    
    public boolean isPickListTransfersEmpty() {
        return pickListTransfers.isEmpty();
    }

    public List<SelectItem> getPickListTransfers() {
        return pickListTransfers;
    }

    public List<SelectItem> getCurrentNewBillingPickList() {
        return currentNewBillingPickList;
    }

    public List<SelectItem> getCurrentNewOwnerPickList() {
        return currentNewOwnerPickList;
    }

    public List<SelectItem> getCurrentNewPrimaryPickList() {
        return currentNewPrimaryPickList;
    }

    public List<SelectItem> getCurrentPrevOwnerPickList() {
        return currentPrevOwnerPickList;
    }

    public Collection<SelectItem> getFee1PickList() {
        return fee1PickList;
    }

    public Collection<SelectItem> getFee2PickList() {
        return fee2PickList;
    }

    public boolean isDecideModRpt() {
        return decideModRpt;
    }

    public void setDecideModRpt(boolean decideModRpt) {
        this.decideModRpt = decideModRpt;
    }

    public EmissionsRptInfo getEvenYearModInfo() {
        return evenYearModInfo;
    }

    public EmissionsRptInfo getOddYearModInfo() {
        return oddYearModInfo;
    }

    public Integer getSelectModRpt() {
        return selectModRpt;
    }

    public void setSelectModRpt(Integer selectModRpt) {
        this.selectModRpt = selectModRpt;
    }

    public String getPrevRptEvenYearString() {
        return prevRptEvenYearString;
    }

    public Float getPrevRptEvenYearTons() {
        return prevRptEvenYearTons;
    }

    public String getPrevRptOddYearString() {
        return prevRptOddYearString;
    }

    public Float getPrevRptOddYearTons() {
        return prevRptOddYearTons;
    }

    public String getMustEnumerateFER() {
        return mustEnumerateFER;
    }

    public String getBillingCompare() {
        return billingCompare;
    }

    public String getPrimaryCompare() {
        return primaryCompare;
    }

//    public String getThisBillOnly() {
//        return thisBillOnly;
//    }

    public boolean isAllowContactChange() {
        return allowContactChange;
    }

    public boolean isSystemOwnerChangeIndicator() {
        return systemOwnerChangeIndicator;
    }

    public void setSystemOwnerChangeIndicator(boolean systemOwnerChangeIndicator) {
        this.systemOwnerChangeIndicator = systemOwnerChangeIndicator;
    }

    public String getDisplayTotal1() {
        return displayTotal1;
    }

    public String getDisplayTotal2() {
        return displayTotal2;
    }
    
    public boolean isDisallowClick() {
        return isAnyEditable();
        
    }
    
    public final boolean isAnyEditable() {
        return this.isEditable() || this.isAdminEditable();
    }

    public boolean isExistingPurchaseOwner() {
        return existingPurchaseOwner;
    }

    public void setExistingPurchaseOwner(boolean existingPurchaseOwner) {
        this.existingPurchaseOwner = existingPurchaseOwner;
    }

    public boolean isNeedNewOwnerInfo() {
        return needNewOwnerInfo;
    }

    public boolean isAllowPrimaryAddress() {
        return allowPrimaryAddress;
    }
    
    public boolean isRenderPrevOwnerAddr() {
        return !err && reloadRpt && (getPrevOwnerAddress() != null ||
                (allowContactChange && needNewOwnerInfo && ntvReport.isPrevOwnStillOwes()));
    }
    
    public boolean isRenderNewOwnerAddr() {
        return !err && reloadRpt &&
                ((allowContactChange && needNewOwnerInfo) ||
                 (allowContactChange && !needNewOwnerInfo && existingPurchaseOwner)||
                 getNewOwnerAddress() != null);
    }
    
    public boolean isRenderBillingPrimary() {
        return !err && reloadRpt && 
            (purchaseSold != null || !ownerChangeIndicator ||
            !getPrimaryReport().getRptReceivedStatusCd().equals(ReportReceivedStatusDef.NOT_FILED));
    }
    
    public boolean isRenderEmissionsInfo() {
        return !err && reloadRpt &&
            (getReport1() != null || getReport2() != null) && (purchaseSold != null || !ownerChangeIndicator || !getPrimaryReport().getRptReceivedStatusCd().equals(ReportReceivedStatusDef.NOT_FILED));
    }
    
    public boolean isRenderAdditionalInfo() {
        return !err && reloadRpt && 
            (evenAdditionalEmissions || oddAdditionalEmissions) &&
            (purchaseSold != null || !ownerChangeIndicator || !getPrimaryReport().getRptReceivedStatusCd().equals(ReportReceivedStatusDef.NOT_FILED));
    }
    
    public boolean isRenderNonAttain() {
        return !err && reloadRpt &&
            (getReport1() != null || getReport2() != null) && (purchaseSold != null || !ownerChangeIndicator || !getPrimaryReport().getRptReceivedStatusCd().equals(ReportReceivedStatusDef.NOT_FILED));
    }

    public Integer getFee1() {
        return fee1;
    }

    public void setFee1(Integer fee1) {
        this.fee1 = fee1;
        report1.setFeeId(fee1);
        should1DetailsBeProvided();
    }

    public Integer getFee2() {
        return fee2;
    }

    public void setFee2(Integer fee2) {
        this.fee2 = fee2;
        report2.setFeeId(fee2);
        should2DetailsBeProvided();
    }

    public String getBillingDateRange() {
        return billingDateRange;
    }

    public String getPrimaryDateRange() {
        return primaryDateRange;
    }

    public boolean isNeedApproveConfirm() {
        return needApproveConfirm;
    }

    public String getApproveConfirmText() {
        return approveConfirmText;
    }
    
    public final boolean isIncludeAllAttachments() {
		return includeAllAttachments;
	}

	public final void setIncludeAllAttachments(boolean includeAllAttachments) {
		this.includeAllAttachments = includeAllAttachments;
	}
}