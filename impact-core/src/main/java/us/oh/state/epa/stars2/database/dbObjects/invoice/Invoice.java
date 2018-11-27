package us.oh.state.epa.stars2.database.dbObjects.invoice;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import us.oh.state.epa.revenues.domain.Revenue;
import us.oh.state.epa.revenues.domain.Adjustment;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsDocument;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitDocument;
import us.oh.state.epa.stars2.def.InvoiceState;
import us.oh.state.epa.stars2.def.RevenueState;

public class Invoice extends BaseDB {
    static final public String prefix = "Stars2(";
    static final public String RevenueNotAvail = "Revenues System currently unavailable, please try again later.";
    
    private Integer invoiceId;
    private String facilityId;
    private Integer permitId;
    private Integer emissionsRptId;
    private transient String emissionsRptCd;
    private String invoiceStateCd;
    private String revenueStateCd;
    private Integer revenueId;
    private String revenueTypeCd;
    private Double origAmount;
    private Timestamp creationDate;	
    private Timestamp dueDate;
    private String facilityName;
    private Integer fpId;
    private boolean multiInvoice;
    private boolean adjustedInvoice;
    private Revenue revenue;
    private Revenue revenueImmediate;
    private List<InvoiceNote> notes;
    /*  adjustmentNum == null means the feature is not used--for invoices started before
     *  this feature.
     *  When a receivable is created we do the following:
     *    Set state to Posted, set adjustmentNum = 0, note RevenueId is null
     *    Save Invoice to Stars2 database
     *    Perform createReceivable on Revenues System and
     *    Set RevenueId in Invoice and save in Stars2 database.
     *    IF WE FIND POSTED Invoice still in post workflow step and it has null Revenue ID,
     *    THEN we know that we had a failure and may have created a useless Revenue Id--with balance.
     *    Generate ERRROR message and don't complete workflow step.
     *    INSTEAD, the error message says to check Revenue System for that entry and cancel it.
     *    ALSO provide a button that in this case will set the Invoice back to ready to post.
     *  
     *  When we are retrieving an Invoice, and the adjustmentNum != null we do the following:
     *    If the state is "ready to invoice" then do nothing extra.
     *    If the state is "Posted" then check to see if the post "really happended" by:
     *    Examine each of the adjustment reasons and see if any of them have the text
     *    "Stars2(N)"  where N equals adjusmentNum.  If so, then the adjustment has been recorded,
     *    Otherwise, set the state back to "ready to invoice" because it has not been successful--but
     *    only do that in memory.
     *    
     *    For the aggregate Invoicing workflow step, if the invoice is already posted, then
     *    do not process further.
     */
    private Integer adjustmentNum;
	
    private Contact contact = new Contact();
    //private Address address = new Address();	
    private PermitDocument permitInvDocument = new PermitDocument();
    private EmissionsDocument reportInvDocument = new EmissionsDocument();
    private List<InvoiceDetail> invoiceDetails = new ArrayList<InvoiceDetail>(0);

    private transient Invoice prevInvoice; //holds temp in memory, previous invoice.
    private transient Invoice compInvoice; //holds temp in memory, diff between current and previous invoice.
    private transient String prevInvoiceFailureMsg = null;
    private transient boolean allowOperations;
    private transient boolean haveBillingAddress;
	
    public Invoice() {
        super();
        prevInvoice = null;
        compInvoice = null;
    }

    public Invoice(Invoice old) {
        super(old);

        if (old != null) {
            setInvoiceId(old.getInvoiceId());
            setFacilityId(old.getFacilityId());
            setPermitId(old.getPermitId());
            setEmissionsRptId(old.getEmissionsRptId());
            setInvoiceStateCd(old.getInvoiceStateCd());
            setRevenueId(old.getRevenueId());
            setOrigAmount(old.getOrigAmount());
            setCreationDate(old.getCreationDate());						
            setDueDate(old.getDueDate());
            setRevenueTypeCd(old.getRevenueTypeCd());
            setFacilityName(old.getFacilityName());
            setFpId(old.getFpId());
            setAdjustmentNum(old.getAdjustmentNum());
            setLastModified(old.getLastModified());
            setContact(old.getContact());
           // setAddress(old.getAddress());
            setNotes(old.getNotes());
            setPermitInvDocument(old.getPermitInvDocument());
            setReportInvDocument(old.getReportInvDocument());
			
            setInvoiceDetails(old.getInvoiceDetails());
        }
    }
    
    public static String lostRevenueMsg() {
        String s = us.oh.state.epa.stars2.app.invoice.InvoiceDetail.staticUnPostLabel();
        return "It appears that this invoice encountered a problem when posting resulting in either failing to record the Revenue ID created or failing to create a Revenue ID." +
        "  Confirm whether a Revenue Id was created and delete/inactivate it." +
        "  If you choose to cancel this invoice, you must first " + s +
        ".  This invoice can only be processed on the Invoice Detail page.";
    }

    public final Contact getContact() {
        return contact;
    }

    public final void setContact(Contact contact) {
        this.contact = contact;
    }

    public final Integer getAddressId() {
        Integer rtn = null;
        if(contact != null) {
            rtn = contact.getAddressId();
        }
        return rtn;
    }

    public final void setAddressId(Integer addressId) {
        if(this.contact == null) {
            this.contact = new Contact();
        }
        this.contact.getAddress().setAddressId(addressId);
    }

    public final Integer getContactId() {
        Integer rtn = null;
        if(contact != null) {
            rtn = contact.getContactId();
        }
        return rtn;
    }

    public final void setContactId(Integer contactId) {
        if(this.contact == null) {
            this.contact = new Contact();
        }
        this.contact.setContactId(contactId);
    }

    public boolean isMultiInvoice() {
        return multiInvoice;
    }

    public void setMultiInvoice(boolean multiInvoice) {
        this.multiInvoice = multiInvoice;
    }

    public boolean isAdjustedInvoice() {
        return adjustedInvoice;
    }

    public void setAdjustedInvoice(boolean adjustedInvoice) {
        this.adjustedInvoice = adjustedInvoice;
    }

    public final Timestamp getDueDate() {
        return dueDate;
    }

    public final void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public final Timestamp getCreationDate() {
        return creationDate;
    }

    public final void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }	

    public final Integer getEmissionsRptId() {
        return emissionsRptId;
    }

    public final void setEmissionsRptId(Integer emissionsRptId) {
        this.emissionsRptId = emissionsRptId;
    }

    public final String getFacilityId() {
        return facilityId;
    }

    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public final Integer getInvoiceId() {
        return invoiceId;
    }

    public final void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public final String getInvoiceStateCd() {
        return invoiceStateCd;
    }

    public final void setInvoiceStateCd(String invoiceStateCd) {
        this.invoiceStateCd = invoiceStateCd;
    }

    public final String getInvoiceStateDsc() {
        String ret = "";

        ret = 
            InvoiceState.getData().getItems().getItemDesc(getInvoiceStateCd());
            
		
        return ret;
    }
	
    public String getRevenueStateCd() {
        return revenueStateCd;
    }

    public void setRevenueStateCd(String revenueStateCd) {
        this.revenueStateCd = revenueStateCd;
    }

    public final String getRevenueStateDsc() {
        String ret = "";

        ret = 
            RevenueState.getData().getItems().getItemDesc(getRevenueStateCd());
		
        return ret;
    }
	
    public final Double getOrigAmount() {
        return origAmount;
    }

    public final void setOrigAmount(Double origAmount) {
        this.origAmount = origAmount;
    }

    public final Integer getPermitId() {
        return permitId;
    }

    public final void setPermitId(Integer permitId) {
        this.permitId = permitId;
    }

    public Revenue getRevenue() {
        return revenue;
    }

    public void setRevenue(Revenue revenue) {
        this.revenue = revenue;
    }

    public final Integer getRevenueId() {
        return revenueId;
    }

    public final void setRevenueId(Integer revenueId) {
        this.revenueId = revenueId;
    }

    public final void addInvoiceDetail(InvoiceDetail newInvDtl) {
        if (newInvDtl != null) {
            if (invoiceDetails == null) {
                invoiceDetails = new ArrayList<InvoiceDetail>();
            }

            invoiceDetails.add(newInvDtl);
        }
    }

    public final InvoiceDetail[] getInvoiceDetails() {
        return invoiceDetails.toArray(new InvoiceDetail[0]);
    }

    public final void setInvoiceDetails(InvoiceDetail[] invoiceDetails) {
        if ((invoiceDetails != null) && (invoiceDetails.length > 0)) {
            this.invoiceDetails = new ArrayList<InvoiceDetail>();
            for (InvoiceDetail detail : invoiceDetails) {
                this.invoiceDetails.add(detail);
            }
        }
    }
    
    public final boolean readyToCheckIN(){
        boolean ret = false;
        if (InvoiceState.hasBeenPosted(invoiceStateCd) ||
                invoiceStateCd.equalsIgnoreCase(InvoiceState.CANCELLED)) {
            ret = true;
        }
    return ret; 
    }
	
    public final boolean fullyPosted(){
        boolean ret = true;
        if (InvoiceState.hasBeenPosted(invoiceStateCd) &&
        		revenueId == null) {
            ret = false;
        }
	return ret;	
    }

    public final String getRevenueTypeCd() {
        return revenueTypeCd;
    }

    public final void setRevenueTypeCd(String revenueTypeCd) {
        this.revenueTypeCd = revenueTypeCd;
    }

    public final String getFacilityName() {
        return facilityName;
    }

    public final void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public final Integer getFpId() {
        return fpId;
    }

    public final void setFpId(Integer fpId) {
        this.fpId = fpId;
    }

    public final String getTitle(){
        String rtn = null;
        if(contact != null) {
            rtn = contact.getTitleCd();
        }
        return rtn;
    }
	
    public final String getName() {
        String rtn = null;
        if(contact != null) {
            rtn = contact.getName();
        }
        return rtn;
    }

    public final String getBillingAddressLine1() {
        String rtn = null;
        if(contact != null && contact.getAddress() != null) {
            rtn = contact.getAddress().getAddressLine1();
        }
        return rtn;
    }
	
    public final String getBillingAddressLine2() {
        String rtn = null;
        if(contact != null && contact.getAddress() != null) {
            rtn = contact.getAddress().getAddressLine2();
        }
        return rtn;
    }

    public final String getBillingCityState() {
        String ret = null;
        if(contact != null && contact.getAddress() != null) {
            ret = contact.getAddress().getCityName() 
            + "," + contact.getAddress().getState() 
            + " " + contact.getAddress().getZipCode();
        }
        return ret;
    }
	
    public List<InvoiceNote> getNotes() {
        if(notes == null){
            notes = new ArrayList<InvoiceNote>(0);
        }
        return notes;
    }

    public void setNotes(List<InvoiceNote> note) {
        if (notes == null) {
            notes = new ArrayList<InvoiceNote>(0);
        }
        notes = note;
		
    }
	
    public final void addNote(InvoiceNote note){
        if (notes == null) {
            notes = new ArrayList<InvoiceNote>(1);
        }
        if (note != null && (!notes.contains(note))) {
            notes.add(note);
        }
    }

    public void populate(ResultSet rs) throws SQLException {
        try {
            setInvoiceId(AbstractDAO.getInteger(rs, "invoice_id"));
            setEmissionsRptId(AbstractDAO.getInteger(rs, "emissions_rpt_id"));
            setPermitId(AbstractDAO.getInteger(rs, "permit_id"));
            setFacilityId(rs.getString("facility_id"));
            setRevenueId(AbstractDAO.getInteger(rs, "revenue_id"));
            setInvoiceStateCd(rs.getString("invoice_state_cd"));
            setCreationDate(rs.getTimestamp("creation_dt"));
            setDueDate(rs.getTimestamp("due_dt"));
            setRevenueTypeCd(rs.getString("revenue_type_cd"));
            setOrigAmount(AbstractDAO.getDouble(rs, "orig_amount"));
            setFacilityName(rs.getString("facility_nm"));
            setFpId(AbstractDAO.getInteger(rs, "fp_id"));
            setLastModified(AbstractDAO.getInteger(rs, "invoice_lm"));
            setDirty(false);
            setAllowOperations(true);
            setHaveBillingAddress(true);
			setAdjustmentNum(AbstractDAO.getInteger(rs, "adjustment_num"));
            try {
//                if (AbstractDAO.getInteger(rs, "address_id") != null) {
//                    address.populate(rs);
//                }
                if (AbstractDAO.getInteger(rs, "contact_id") != null) {
                    contact.populate(rs);
                }
                if (AbstractDAO.getInteger(rs, "document_id") != null
                    && AbstractDAO.getInteger(rs, "permit_id") != null) {
                    permitInvDocument.populate(rs);
                }
                if (AbstractDAO.getInteger(rs, "document_id") != null
                    && AbstractDAO.getInteger(rs, "emissions_rpt_id") != null) {
                    reportInvDocument.populate(rs);
                }	
                // There are some migrated invoices which do not have emissions inventories, 
                // This is necessary to populate the document for those invoices.
                if (AbstractDAO.getInteger(rs, "document_id") != null 
                   && AbstractDAO.getInteger(rs, "emissions_rpt_id") == null
                   && AbstractDAO.getInteger(rs, "permit_id") == null) {
                    reportInvDocument.populate(rs);
                }
				
                if (AbstractDAO.getInteger(rs, "invoice_detail_id") != null) {
                    invoiceDetails = new ArrayList<InvoiceDetail>();
                    do {
                        InvoiceDetail tempDetail = new InvoiceDetail();
                        tempDetail.populate(rs);
                        invoiceDetails.add(tempDetail);
                    } while (rs.next());
                }
            } catch (SQLException sqle) {
                logger.debug("Optional field error: " + sqle.getMessage());
            }
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result
            + ((creationDate == null) ? 0 : creationDate.hashCode());
        result = PRIME * result + ((dueDate == null) ? 0 : dueDate.hashCode());
        result = PRIME * result
            + ((emissionsRptId == null) ? 0 : emissionsRptId.hashCode());
        result = PRIME * result
            + ((facilityId == null) ? 0 : facilityId.hashCode());
        result = PRIME * result
            + ((facilityName == null) ? 0 : facilityName.hashCode());
        result = PRIME * result + ((fpId == null) ? 0 : fpId.hashCode());
        result = PRIME * result
            + ((invoiceId == null) ? 0 : invoiceId.hashCode());
        result = PRIME * result
            + ((invoiceStateCd == null) ? 0 : invoiceStateCd.hashCode());
        result = PRIME * result
            + ((origAmount == null) ? 0 : origAmount.hashCode());
        result = PRIME * result
            + ((permitId == null) ? 0 : permitId.hashCode());
        result = PRIME * result
            + ((revenueId == null) ? 0 : revenueId.hashCode());
        result = PRIME * result
            + ((revenueTypeCd == null) ? 0 : revenueTypeCd.hashCode());

        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Invoice other = (Invoice) obj;
        if (creationDate == null) {
            if (other.creationDate != null)
                return false;
        } else if (!creationDate.equals(other.creationDate))
            return false;
        if (dueDate == null) {
            if (other.dueDate != null)
                return false;
        } else if (!dueDate.equals(other.dueDate))
            return false;
        if (emissionsRptId == null) {
            if (other.emissionsRptId != null)
                return false;
        } else if (!emissionsRptId.equals(other.emissionsRptId))
            return false;
        if (facilityId == null) {
            if (other.facilityId != null)
                return false;
        } else if (!facilityId.equals(other.facilityId))
            return false;
        if (facilityName == null) {
            if (other.facilityName != null)
                return false;
        } else if (!facilityName.equals(other.facilityName))
            return false;
        if (fpId == null) {
            if (other.fpId != null)
                return false;
        } else if (!fpId.equals(other.fpId))
            return false;
        if (invoiceId == null) {
            if (other.invoiceId != null)
                return false;
        } else if (!invoiceId.equals(other.invoiceId))
            return false;
        if (invoiceStateCd == null) {
            if (other.invoiceStateCd != null)
                return false;
        } else if (!invoiceStateCd.equals(other.invoiceStateCd))
            return false;
        if (origAmount == null) {
            if (other.origAmount != null)
                return false;
        } else if (!origAmount.equals(other.origAmount))
            return false;
        if (permitId == null) {
            if (other.permitId != null)
                return false;
        } else if (!permitId.equals(other.permitId))
            return false;
        if (revenueId == null) {
            if (other.revenueId != null)
                return false;
        } else if (!revenueId.equals(other.revenueId))
            return false;
        if (revenueTypeCd == null) {
            if (other.revenueTypeCd != null)
                return false;
        } else if (!revenueTypeCd.equals(other.revenueTypeCd))
            return false;

        return true;
    }

    public static Timestamp calculateDueDate(Timestamp date, int days) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(date.getTime());
        gc.add(Calendar.DATE, days);
        return new Timestamp(gc.getTimeInMillis());

    }

//    public final Address getAddress() {
//        return address;
//    }

//    public final void setAddress(Address address) {
//        this.address = address;
//    }

    public Invoice getPrevInvoice() {
        return prevInvoice;
    }

    public void setPrevInvoice(Invoice prevInvoice) {
        this.prevInvoice = prevInvoice;
    }

    public Invoice getCompInvoice() {
        return compInvoice;
    }

    public void setCompInvoice(Invoice compInvoice) {
        this.compInvoice = compInvoice;
    }

    public PermitDocument getPermitInvDocument() {
        return permitInvDocument;
    }

    public void setPermitInvDocument(PermitDocument invoiceDocument) {
        this.permitInvDocument = invoiceDocument;
    }

    public EmissionsDocument getReportInvDocument() {
        return reportInvDocument;
    }

    public void setReportInvDocument(EmissionsDocument reportInvDocument) {
        this.reportInvDocument = reportInvDocument;
    }

    public String getPrevInvoiceFailureMsg() {
        return prevInvoiceFailureMsg;
    }

    public void setPrevInvoiceFailureMsg(String prevInvoiceFailureMsg) {
        this.prevInvoiceFailureMsg = prevInvoiceFailureMsg;
    }

    public boolean isAllowOperations() {
        return allowOperations;
    }

    public void setAllowOperations(boolean allowOperations) {
        this.allowOperations = allowOperations;
    }
    
    public void checkPostCompleted() {
        /*
         * This checks to see if we failed to perform the adjustment in Revenues.
         * See comments with attribure adjustmentNum.
         */
        if (invoiceStateCd.equalsIgnoreCase(InvoiceState.POSTED_TO_REVENUES)) {
            if(adjustmentNum != null) {
                if(!adjustmentPerformedAlready()) {
                    invoiceStateCd = InvoiceState.READY_TO_INVOICE;
                }
            }
        }
    }
    
    public boolean adjustmentPerformedAlready() {
        boolean rtn = false;
        if(adjustmentNum != null && revenue != null && revenue.getAdjustmentList() != null) {
            for(Adjustment a : revenue.getAdjustmentList()) {
                int v = getValue(a);
                if(v != -1 && adjustmentNum == v) {
                    rtn = true;
                }
            }
        }
        return rtn;
    }
    
    public int highestAdjustment() {
        int rtn = -1;
        if(revenue != null && revenue.getAdjustmentList() != null) {
            for(Adjustment a : revenue.getAdjustmentList()) {
                int v = getValue(a);
                if(v > rtn) {
                    rtn = v;
                }
            }
        }
        return rtn;
    }
    
    private static int getValue(Adjustment a) {
        int rtn = -1;
        if(a.getReason() != null && a.getReason().length() >= prefix.length() + 2) {
            if(a.getReason().startsWith(prefix)) {
                int end = a.getReason().indexOf(")");
                String strNum = a.getReason().substring(prefix.length(), end);
                if(strNum.matches("(0?1?2?3?4?5?6?7?8?9?)*")) {
                    rtn = Integer.parseInt(strNum);
                }
            }
        }
        return rtn;
    }

    public Integer getAdjustmentNum() {
        return adjustmentNum;
    }

    public void setAdjustmentNum(Integer adjustmentNum) {
        this.adjustmentNum = adjustmentNum;
    }

    public boolean isHaveBillingAddress() {
        return haveBillingAddress;
    }

    public void setHaveBillingAddress(boolean haveBillingAddress) {
        this.haveBillingAddress = haveBillingAddress;
    }

    public Revenue getRevenueImmediate() {
        return revenueImmediate;
    }

    public void setRevenueImmediate(Revenue revenueImmediate) {
        this.revenueImmediate = revenueImmediate;
    }
    
    public boolean isPosted() {
        if(InvoiceState.hasBeenPosted(invoiceStateCd)) {
            return true;
        }
        return false;
    }

    public String getEmissionsRptCd() {
        return emissionsRptCd;
    }

    public void setEmissionsRptCd(String emissionsRptCd) {
        this.emissionsRptCd = emissionsRptCd;
    }
    
}
