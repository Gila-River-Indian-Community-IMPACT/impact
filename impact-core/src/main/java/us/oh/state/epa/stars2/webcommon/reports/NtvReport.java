package us.oh.state.epa.stars2.webcommon.reports;

import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsRptInfo;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.def.ContactTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.ValidationBase;


public class NtvReport extends BaseDB {
    static public Integer NEW_OWNER = new Integer(1);
    static public Integer OLD_OWNER = new Integer(2);
    private EmissionsReport report1;
    private EmissionsReport report2;
    // Report for the even year is the preferred primary report.
    private transient EmissionsReport primary;
    
    private transient Integer providedRptId;  // The Id used to read report
    private transient String providedEmInventoryId;  // The Id used to read report
    private transient Timestamp midpoint;
    private transient Timestamp rightPoint;
    private transient Timestamp leftPoint;
    private transient Timestamp anchor; // Dec 31st of latest year in report
    
    // If new owner leaves some years to be paid by previous owner
    // Only set, if owner change and report being filed by new owner.
    private transient boolean prevOwnStillOwes;
    
    // First year not to be paid by previous owner.
    private transient Timestamp firstNotPaid;
    private transient int oddYear;  // used to tie to revenue type code
    private transient int evenYear;
    
    // contacttRefDate is either anchor or if owners, then the last day
    // of ownership of owner at time of anchor.  If that owner is still
    // current owner, then it is null;
    private transient Timestamp contactRefDate;
    private transient boolean soldReducedRequirement;
    private transient boolean shutdownReducedRequirement;
    private transient EmissionsReport reportV1; // year to report
    private transient EmissionsReport reportV2; // year to report
    
    // shutdownToDoData is null or empty if no ToDo item needs
    // to be generated.
    private transient HashMap<String, String> shutdownToDoData = null;
    
    public NtvReport() {
        super();
    }

    public NtvReport(EmissionsReport reportA, EmissionsReport reportB) {       
        if(reportA == null) {
            if(reportB.getReportYear().intValue() % 2 == 0) {
                report2 = reportA;
                report1 = reportB;
            } else {
                report1 = reportA;
                report2 = reportB;
            }
        } else {
            if(reportA.getReportYear().intValue() % 2 == 0) {
                report1 = reportA;
                report2 = reportB;

            } else {
                report2 = reportA;
                report1 = reportB;
            }
        }
        if(report1 != null) primary = report1;
        else primary = report2;
        
        Calendar cal = Calendar.getInstance();
        evenYear = primary.getReportYear();
        evenYear = (evenYear%2==0)?evenYear:evenYear - 1;
        oddYear = evenYear + 1;
        cal.set(evenYear, Calendar.DECEMBER, 31, 23, 59, 59);
        midpoint = new Timestamp(cal.getTimeInMillis());
        midpoint.setNanos(0);
        cal.set(oddYear, Calendar.DECEMBER, 31, 23, 59, 59);
        rightPoint = new Timestamp(cal.getTimeInMillis());
        rightPoint.setNanos(0);
        cal.set(evenYear, Calendar.JANUARY, 1, 0, 0, 0);
        leftPoint = new Timestamp(cal.getTimeInMillis());
        leftPoint.setNanos(0);
    }
    
    public static NtvReport cloneNTVReport(NtvReport rpt, int rptId) {
        EmissionsReport ra = EmissionsReport.cloneNTVReport(rpt.report1);
        EmissionsReport rb = EmissionsReport.cloneNTVReport(rpt.report2);
        if(ra != null) {
            ra.setReportModified(rptId);
        }
        if(rb != null) {
            rb.setReportModified(rptId);
        }
        NtvReport newRpt = new NtvReport(ra, rb);
        return newRpt;
    }
    
    public static NtvReport cloneNTVReport(NtvReport rpt, EmissionsRptInfo eri,
            int rpt1Id, int rpt2Id) {
        EmissionsReport ra = null;
        EmissionsReport rb = null;
        if(eri.getYear()%2 == 0) { // even year
            ra = EmissionsReport.cloneNTVReport(rpt.report1);
            ra.setReportModified(rpt1Id);
        } else { // odd year
            rb = EmissionsReport.cloneNTVReport(rpt.report2);
            rb.setReportModified(rpt2Id);
        }
        NtvReport newRpt = new NtvReport(ra, rb);
        return newRpt;
    }
    
    public String getYears() {
        // Return the year or years covered
        // by this NTV report
        String rtn = "";
        String spacer = "";
        if(report1 != null) {
            rtn = report1.getReportYear().toString();
            spacer = "-";
        }
        if(report2 != null) {
            rtn = rtn + spacer + report2.getReportYear(); 
        }
        return rtn;
    }
    
    public static Integer otherYear(Integer year) {
        Integer ret = new Integer(year.intValue() - 1);
        
        if(year.intValue() % 2 == 0) {
            ret = new Integer(year.intValue() + 1);
        }
            
        return ret;
    }
    
    public void determineReportable(Facility facility)
        throws DAOException {
        reportV1 = report1;
        reportV2 = report2;
        prevOwnStillOwes = false;
        Timestamp sdDate = primary.getShutdownDate();
        if(sdDate == null) {
            sdDate = facility.getShutdownDate();
        }
        if(primary.getTransferDate() != null) {
            if(primary.isNewOwner()) {  // is new owner

                // What does previous owner owe:
                Boolean prevOwe1 = new Boolean(true);
                if(report1 == null) {
                    // If report year not included then skip 
                    prevOwe1 = null;
                }
                Boolean prevOwe2 = new Boolean(true);
                if(report2 == null) {
                    // If report year not included then skip 
                    prevOwe2 = null;
                }

                // Check second year
                if(!primary.getTransferDate().after(rightPoint)) {
                     prevOwe2 = null;  // Second year not owed.
                }
                // What does previous owner owe:
                // Check first year
                if(!primary.getTransferDate().after(midpoint)) {
                     prevOwe1 = null;  // First year not owed.
                }
                // Further determine based upon shutdown date.
                if(sdDate != null) {
                    if(!sdDate.after(rightPoint)) {
                        prevOwe2 = null;  // Second year owed.
                    }
                    // Check first year
                    if(!sdDate.after(midpoint)) {
                        prevOwe1 = null;  // First year owed.
                    }
                }
                if(primary.isProvideBothYears()) {
                    prevOwe1 = null;
                    prevOwe2 = null;
                }
                
                try {
                // See if reports received for these years.
                EmissionsReportService erBO = ServiceFactory.getInstance().getEmissionsReportService();
                if(prevOwe1 != null) {
                    prevOwe1 = erBO.missingReport(facility.getFacilityId(), evenYear, report1.getEmissionsRptId());
                }
                if(prevOwe2 != null) {
                    prevOwe2 = erBO.missingReport(facility.getFacilityId(), oddYear, report2.getEmissionsRptId());
                }
                } catch(ServiceFactoryException sre) {
                    throw new DAOException(sre.getMessage(), sre);
                } catch (RemoteException re) {
                    throw new DAOException(re.getMessage(), re);
                }
                prevOwnStillOwes = prevOwe1 != null || prevOwe2 != null;
                firstNotPaid = null;
                if(prevOwe2 != null) {
                    firstNotPaid = rightPoint;
                }
                if(prevOwe1 != null) {
                    firstNotPaid = midpoint;
                }
                
                // Check second year for new owner
                if(!primary.isProvideBothYears() && primary.getTransferDate().after(rightPoint)) {
                    if(reportV2 != null) {
                        reportV2 = null;  // Second year not owed.
                        soldReducedRequirement = true;
                    }
                }
                // Check first year for new owner
                if(!primary.isProvideBothYears() && primary.getTransferDate().after(midpoint)) {
                    if(reportV1 != null) {
                        reportV1 = null;  // First year not owed.
                        soldReducedRequirement = true;
                    }
                }
            } else {  // is previous owner
                if(!primary.isProvideBothYears()) {
                    // Check second year
                    if(!primary.getTransferDate().after(rightPoint)) {
                        if(reportV2 != null) {
                            reportV2 = null;  // Second year not owed.
                            soldReducedRequirement = true;
                        }
                    }
                    // Check first year
                    if(!primary.getTransferDate().after(midpoint)) {
                        if(reportV1 != null) {
                            reportV1 = null;  // First year not owed.
                            soldReducedRequirement = true;
                        }
                    }
                }
                // Further determine based upon shutdown date.
                if(sdDate != null) {
                    if(!sdDate.after(rightPoint)) {
                        reportV2 = null;  // Second year not owed.
                    }
                    // Check first year
                    if(!sdDate.after(midpoint)) {
                        reportV1 = null;  // First year not owed.
                    }
                }
            }
        }
        
        // Determine reference date to use for finding
        // appropriate contact.
        anchor = null;
        if(reportV2 != null) {
            anchor = rightPoint;
        } else if(reportV1 != null) {
            anchor = midpoint;
        } else anchor = null;
        if(primary.getTransferDate() != null) {
            if(primary.isNewOwner()) {  // is new owner
                anchor = primary.getTransferDate();
            } else {
                // need one day earlier
                Calendar cal = Calendar.getInstance();
                Date date = new Date(primary.getTransferDate().getTime());
                cal.setTime(date);
                cal.add(Calendar.HOUR_OF_DAY, -24);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                anchor = new Timestamp(cal.getTimeInMillis());
            }
        }

        contactRefDate = facility.latestOwnerRefDate(anchor);
        
        if(anchor != null) {
            if(primary.isProvideBothYears()) {
                // Above code was needed to set contacttRefDate;
                reportV1 = report1;
                reportV2 = report2;
            }
        }
        
        // Further determine based upon shutdown date.
        if(sdDate != null) {
            if(!sdDate.after(rightPoint)) {
                if(reportV2 != null) {
                    reportV2 = null;  // Second year owed.
                    shutdownReducedRequirement = true;
                }
            }
            // Check first year
            if(!sdDate.after(midpoint)) {
                if(reportV1 != null) {
                    reportV1 = null;  // First year owed.
                    shutdownReducedRequirement = true;
                }
            }
        }
    }
    
    public List<ValidationMessage> submitVerify(
            Facility facility, boolean internal, boolean needNewOwnerInfo,
            boolean allowPrimaryAddress,
            boolean existingPurchaseOwner,
            int minValue, ReportTemplates scReports, ReportTemplates scReports2,
            EmissionsReport rpt1, EmissionsReport rpt2,
            boolean vErrors) {
        List<ValidationMessage> validMsgs = new ArrayList<ValidationMessage>();
        List<ValidationMessage> vMsgs;
        
        List<SelectItem> pickListTransfers = facility.
                ownershipChange(leftPoint);
        boolean ownerChangeIndicator = !pickListTransfers.isEmpty() ||
            primary.getTransferDate() != null;
        
        boolean shutdownIndicator = primary.getShutdownDate() != null;
    //    boolean dtl1 = report1 != null && report1.getTotalEmissions() != null;
    //    boolean dtl2 = report2 != null && report2.getTotalEmissions() != null;
        Integer purchaseSold = null;
        if(primary.getTransferDate() == null) {
            // Have not saved the transfer date yet.
            purchaseSold = null;
        } else {
            if(primary.getTransferDate() != null) {
                if(primary.isNewOwner()) {
                    purchaseSold = new Integer(NtvReport.NEW_OWNER);
                } else {
                    purchaseSold = new Integer(NtvReport.OLD_OWNER);
                }
            }
        }

        addReturnLink(validMsgs, vErrors);
        
        // TODO  Delete the following test because already performed at save time.
        vMsgs = getPrimary().verifyNTVOwnerShutdown(internal, ownerChangeIndicator,
                purchaseSold, shutdownIndicator, pickListTransfers);
        addReturnLink(validMsgs, vMsgs.size() != 0);
        validMsgs.addAll(vMsgs);
        if(rpt1 != null) {
            rpt1.clearValidationMessages();
            vMsgs = rpt1.validateNtvRpt(minValue, scReports, "Even");
            addReturnLink(validMsgs, vMsgs.size() != 0);
            validMsgs.addAll(vMsgs);
        }
        if(rpt2 != null) {
            rpt2.clearValidationMessages();
            vMsgs = rpt2.validateNtvRpt(minValue, scReports2, "Odd");
            addReturnLink(validMsgs, vMsgs.size() != 0);
            validMsgs.addAll(vMsgs);
        }
        vMsgs = validatePrimaryNtvRpt(facility,
                needNewOwnerInfo, allowPrimaryAddress, 
                existingPurchaseOwner, purchaseSold);
        addReturnLink(validMsgs, vMsgs.size() != 0);
        validMsgs.addAll(vMsgs);
        return validMsgs;
    }
    
    void addReturnLink(List<ValidationMessage> validationMessages, boolean vErrors) {
        if(vErrors && validationMessages.size() == 0) {
            validationMessages.add(new ValidationMessage("rtn_to_rpt", "Return to Report",
                    ValidationMessage.Severity.INFO, ValidationBase.REPORT_TAG));
        }
    }
    
    public List<ValidationMessage> validatePrimaryNtvRpt(Facility facility,
            boolean needNewOwnerInfo,
            boolean allowPrimaryAddress,
            boolean existingPurchaseOwner, Integer purchaseSold) {
        HashMap<String, ValidationMessage> validationMessages = 
            new HashMap<String, ValidationMessage>();

            /*  THE FOLLOWING TEST IS PERFORMED IN function EmissionReport.verifyNTVOwnerShutdown()
            if(!dateFound && !isNewest) {
                validationMessages.put("validTransferDT", new ValidationMessage(
                        "validTransferDT", "The specified Transfer Date does not match the existing start date of an owner and there is already an owner with a more recent start date.  You can only provide this ownership information by direct communication with DAPC.",
                        ValidationMessage.Severity.ERROR, ValidationBase.REPORT_TAG));
            } */
            
            /*  If this is a new Transfer Date, then New Owner must be specified.
             */
            if(needNewOwnerInfo && primary.getNewOwnerAddr() == null) {
                validationMessages.put("requireNewOwner", new ValidationMessage(
                        "requireNewOwner", "The New Owner must be specified.",
                        ValidationMessage.Severity.ERROR, ValidationBase.REPORT_TAG));
            }
        
        /* (Prior to Approval)
         * Verify that if not most recent report then
         * no ownership information can be provided.
         *    This is:
         *       Old Owner Forwarding,
         *       Updated new/current owner and
         *       New transfer date (not already known),
         *       but Transfer Date  and whether buyer or
         *       seller can be given.
         *       Updated current billing & primary address can be given
         *       if current owner. 
         */
//        if(!allowOwnerInfo && !(!dateFound && !isNewest)) {
        if(!needNewOwnerInfo) {
            if(primary.getPrevOwnerForwardingAddr() != null) {
                validationMessages.put("newerNoForward", new ValidationMessage(
                        "newerNoForward", "You cannot specify Previous Owner Forwarding address through this report since there are already other reports for this or newer years.",
                        ValidationMessage.Severity.ERROR, ValidationBase.REPORT_TAG));
            }
        }
        if(!needNewOwnerInfo && !existingPurchaseOwner) {
            if(primary.getNewOwnerAddr() != null) {
                validationMessages.put("newerNoNewOwner", new ValidationMessage(
                        "newerNoNewOwner", "You cannot specify new/updated Owner information through this report since this is not the newest owner or there are already other reports for this or newer years.",
                        ValidationMessage.Severity.ERROR, ValidationBase.REPORT_TAG));
            }
        }
        
        // (Prior to Approval)  Verify that updated (same) owner information
        // must be one of the existing owners--must have same name.
        if(primary.getNewOwnerAddr() != null && !needNewOwnerInfo) {// primary.getTransferDate() == null) {
            boolean ownerFound = false;
            for(Contact c : facility.getAllContactsList()) {
                outer:
                    for(ContactType t : c.getContactTypes()) {
                        /*if(!t.getContactTypeCd().equals(ContactTypeDef.OWNR) ||
                                t.getEndDate() != null) {
                            continue;
                        }*/
                        if(c.sameContact(primary.getNewOwnerAddr())){
                            ownerFound = true;
                            break outer;
                        }
                    }
            }
            if(!ownerFound) {
                validationMessages.put("sameOwnerNotFnd", new ValidationMessage(
                        "sameOwnerNotFnd", "The updated Owner information must be for an existing owner with the same name.",
                        ValidationMessage.Severity.ERROR, ValidationBase.REPORT_TAG));
            }
        }

        /* Verify that if old owner submitting, then updated Primary Contact cannot
         * be given.  Also, Old Owner Forwarding Address cannot be given.
         */
        if(needNewOwnerInfo && !primary.isNewOwner()) {
            if(purchaseSold == null) {
                validationMessages.put("chgNoEntry", new ValidationMessage(
                        "chgNoEntry", "Records indicate owner change, but report does not indicate whether report is being submitted by previous owner or new owner.",
                        ValidationMessage.Severity.ERROR, ValidationBase.REPORT_TAG));
            }else if(purchaseSold.intValue() == NtvReport.OLD_OWNER) {
                if(primary.getPrimaryAddr() != null) {
                    validationMessages.put("oldNoPrimary", new ValidationMessage(
                            "oldNoPrimary", "The Previous Owner cannot provide the New Owner Primary Address through this report.",
                            ValidationMessage.Severity.ERROR, ValidationBase.REPORT_TAG));
                }
                if(primary.getPrevOwnerForwardingAddr() != null) {
                    validationMessages.put("oldNoForwarding", new ValidationMessage(
                            "oldNoForwarding", "The Previous Owner cannot provide their Forwarding Address through this report.",
                            ValidationMessage.Severity.ERROR, ValidationBase.REPORT_TAG));
                }
            }
        }
        
        if(!allowPrimaryAddress && primary.getPrimaryAddr() != null) {
            validationMessages.put("doNotAllowPrimary", new ValidationMessage(
                    "doNotAllowPrimary", "Cannot provide Primary Contact since newer reports or newer owners or I sold the facility.",
                    ValidationMessage.Severity.ERROR, ValidationBase.REPORT_TAG));
        }
        
        /* Verify that if the new owner is submitting and a report remains to
         * be paid, the Old Owner Forwarding address and new owner Primary Address
         * must be provided.
         */
        if(needNewOwnerInfo && prevOwnStillOwes) {
            if(primary.getPrevOwnerForwardingAddr() == null) {
                validationMessages.put("oldForwardingNeeded", new ValidationMessage(
                    "oldForwardingNeeded", "Since the Previous Owner owes report(s), the Previous Owner Forwarding (primary) address must be provided.",
                    ValidationMessage.Severity.ERROR, ValidationBase.REPORT_TAG));
            }
        }
        if(primary.getPrevOwnerForwardingAddr() != null && primary.getPrimaryAddr() == null) {
            validationMessages.put("newPrimaryNeeded", new ValidationMessage(
                    "newPrimaryNeeded", "Since the Previous Owner Forwarding (primary) info will end the current Primary info, Updated Primary information must be provided.",
                    ValidationMessage.Severity.ERROR, ValidationBase.REPORT_TAG));
        }
        return new ArrayList<ValidationMessage>(validationMessages.values());
    }


    public EmissionsReport getReport1() {
        return report1;
    }

    public void setReport1(EmissionsReport report1) {
        this.report1 = report1;
    }

    public EmissionsReport getReport2() {
        return report2;
    }

    public void setReport2(EmissionsReport report2) {
        this.report2 = report2;
    }

    public EmissionsReport getPrimary() {
        return primary;
    }

    public Timestamp getMidpoint() {
        return midpoint;
    }

    public Timestamp getRightPoint() {
        return rightPoint;
    }

    public Timestamp getLeftPoint() {
        return leftPoint;
    }

    public Integer getProvidedRptId() {
        return providedRptId;
    }

    public void setProvidedRptId(Integer providedRptId) {
        this.providedRptId = providedRptId;
    }
    

    public String getProvidedEmInventoryId() {
		return providedEmInventoryId;
	}

	public void setProvidedEmInventoryId(String providedEmInventoryId) {
		this.providedEmInventoryId = providedEmInventoryId;
	}

	public int getOddYear() {
        return oddYear;
    }

    public Timestamp getContactRefDate() {
        return contactRefDate;
    }

    public EmissionsReport getReportV1() {
        return reportV1;
    }

    public EmissionsReport getReportV2() {
        return reportV2;
    }

    public boolean isShutdownReducedRequirement() {
        return shutdownReducedRequirement;
    }

    public boolean isSoldReducedRequirement() {
        return soldReducedRequirement;
    }

    public Timestamp getAnchor() {
        return anchor;
    }

    public Timestamp getFirstNotPaid() {
        return firstNotPaid;
    }

    public boolean isPrevOwnStillOwes() {
        return prevOwnStillOwes;
    }
    
    public void populate(ResultSet rs) throws SQLException {
        logger.error("NtvReport.populate should never be called");
    }

    public int getEvenYear() {
        return evenYear;
    }

    public HashMap<String, String> getShutdownToDoData() {
        return shutdownToDoData;
    }

    public void setShutdownToDoData(HashMap<String, String> shutdownToDoData) {
        this.shutdownToDoData = shutdownToDoData;
    }
}