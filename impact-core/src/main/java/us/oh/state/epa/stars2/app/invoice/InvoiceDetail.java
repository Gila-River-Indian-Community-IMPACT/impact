package us.oh.state.epa.stars2.app.invoice;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import us.oh.state.epa.aport.admin.domain.User;
import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.invoice.Invoice;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceNote;
import us.oh.state.epa.stars2.database.dbObjects.invoice.MultiInvoiceList;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitDocument;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitInfo;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;
import us.oh.state.epa.stars2.def.AdjustmentType;
import us.oh.state.epa.stars2.def.EmissionReportsRealDef;
import us.oh.state.epa.stars2.def.InvoiceState;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.RevenueTypeDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.def.TemplateDocTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.util.DocumentGenerationException;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.ContactUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.bean.NameValue;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.oh.state.epa.stars2.webcommon.reports.NtvReport;

public class InvoiceDetail extends AppBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -629273319026670368L;

	protected static final String CONTACT_CLIENT_ID = "contact:";

	public static final String INDICATOR_MSG = "You have selected to 'Activate/Deactivate' the Revenue. "
			+ "Click Yes to proceed or No to cancel.";

	private boolean err = false;
    
    private Integer invoiceId;

	private Integer emissionsRptId;
    
    private boolean existingPurchaseOwner;
    
    private boolean autoGenRpt;
    
    private boolean allowUnPost = false;

	private Integer permitId;

	private Invoice invoice;
    
    private Facility facility;

	private Contact contact;
    private String contactComment;

	private User user;

	private MultiInvoiceList[] otherInvoices;

	private boolean editable;
	
	private boolean editable1;

	private boolean contactModify;

	private Timestamp invoiceDate;
    private Timestamp minInvoiceDate;
    private Timestamp maxInvoiceDate;

	private Double adjustmentAmount;

	private String adjustmentDocID; // Revenue Adjustment document Id

	private String reason;

	private String adjustmentType;

	private LinkedHashMap<String, Double> emissionRanges = new LinkedHashMap<String, Double>();// SMTV /NTV emission Range

	private String reportType;
	
	private String confirmMessage;
    private String confirmPostWithMultiple = null;
	
    private String confirmOperation;

	private TableSorter notesWrapper;

	private InvoiceNote invoiceNote;
	private InvoiceNote modifyInvoiceNote;

	private InvoiceNote invoiceNotes[];

	private boolean noteModify;

	private NtvReport ntvReport = null;
    private EmissionsReport report = null;

    private boolean clearRevenueIdFlag = false;
	
    private EmissionsReportService emissionsReportService;
	private FacilityService facilityService;
	private InfrastructureService infrastructureService;
	private PermitService permitService;

	public PermitService getPermitService() {
		return permitService;
	}

	public void setPermitService(PermitService permitService) {
		this.permitService = permitService;
	}

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

    public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}

	public void setEmissionsReportService(
			EmissionsReportService emissionsReportService) {
		this.emissionsReportService = emissionsReportService;
	}
	public InvoiceDetail() {
		super();
		notesWrapper = new TableSorter();
	}

	public final Invoice getInvoice() {
		return invoice;
	}

	public final void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public NtvReport getNtvReport() {
		return ntvReport;
	}

	public final Contact getContact() {
        if(invoice != null) {
            return invoice.getContact();
        }
        return null;
	}

	public final void setContact(Contact contact) {
		invoice.setContact(contact);
	}

	public String getAdjustmentDocID() {
		return adjustmentDocID;
	}

	public void setAdjustmentDocID(String adjustmentDocId) {
		this.adjustmentDocID = adjustmentDocId;
	}

	public final Timestamp getInvoiceDate() {
		return invoiceDate;
	}

	public final void setInvoiceDate(Timestamp invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public final void editContact() {
		
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        minInvoiceDate = new Timestamp(cal.getTimeInMillis());
        int lastDay = 30;
        Integer days = SystemPropertyDef.getSystemPropertyValueAsInteger("Max_Future_Invoice_Days", 30);
        if (days != null && days > 1) {
        	lastDay = days;
        }
        cal.add(Calendar.DAY_OF_MONTH, lastDay + 1);
        maxInvoiceDate = new Timestamp(cal.getTimeInMillis());
		setEditable(true);
		contactModify = true;
	}

	/*
	 * Clear values that may have been carried over from a previous invoice
	 */
	private final void reset() {
		invoice = null;
		otherInvoices = null;
		adjustmentType = null;
		reason = null;
		emissionRanges = new LinkedHashMap<String, Double>();
	}

	private final void resetNewAdjustment() {
		adjustmentType = null;
		adjustmentAmount = null;
		adjustmentDocID = null;
		reason = null;
	}

	public final void saveEditContact() {
		boolean operationOK = true;
		contact = getContact();
		ValidationMessage[] validationMessages;
		Integer contactId = getContact().getContactId();

		try {
			validationMessages = getInfrastructureService().validateContact(contact);
			if (displayValidationMessages(CONTACT_CLIENT_ID, validationMessages)) {
				return;
			}
		} catch (RemoteException re) {
			DisplayUtil.displayError("Accessing contact failed for validation");
			logger.error(re.getMessage(), re);
            err = true;
			return;
		}

		try {
			if (contactModify){
				getInfrastructureService().modifyContactData(contact);
			}	
			contact = getInfrastructureService().retrieveContact(contactId);
			setContact(contact);
		} catch (RemoteException re) {
			operationOK = false;
			handleException(re);
		}

		if (operationOK) {
			DisplayUtil.displayInfo("Billing contact updated successfully");
		} else {
			//cancelEditContact();
            err = true;
			DisplayUtil.displayError("Updating billing contact failed");
		}

		contactModify = false;
		setEditable(false);
		closeDialog();
	}

//	public final void cancelEditContact() {
//		contactModify = false;
//		getInvoice();
//		setEditable(false);
//		closeDialog();
//	}

	public final void saveEditData() {
		try {
			if (invoice.isAdjustedInvoice()) {
				invoice.setInvoiceDetails(invoice.getCompInvoice()
						.getInvoiceDetails());
			} else {
				invoice.setInvoiceDetails(invoice.getInvoiceDetails());
			}
			
			getInfrastructureService().modifyInvoice(invoice);
			DisplayUtil.displayInfo("Invoice data saved successfully");
			internalSubmit();
		} catch (RemoteException re) {
            err = true;
			DisplayUtil.displayError("Updating invoice data failed");
			handleException(re);
		}
		setEditable(false);
	}

	public final boolean isEditable() {
		return editable;
	}

	public final void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isEditable1() {
		return editable1;
	}

	public void setEditable1(boolean editable1) {
		this.editable1 = editable1;
	}

	public final void cancelEdit() {
		contactModify = false;
		getInvoice();
		setEditable(false);
	}

	public final void postAdjustment() {
		boolean operationOK = true;
		try {
            if (user == null) { 
                throw new DAOException("User is null"); 
            }			
			if (invoice.isAdjustedInvoice()) {
                if(invoiceDate == null){
                    DisplayUtil.displayError("Enter Invoice Date which will appear in the adjusted Invoice document.");
                    return;
                }
				adjustmentType = AdjustmentType.ADJUST_CURRENT_AMOUNT;
				adjustmentAmount = invoice.getCompInvoice().getOrigAmount();
                adjustmentDocID = "not null";
                reason = "not null";
			}

			if (adjustmentType == null) {
				DisplayUtil.displayError("Please select Adjustment Type");
				operationOK = false;
			}
			if (adjustmentAmount == null || adjustmentAmount == 0) {
				//To avoid *EXCEPTION* EPAIllegalValueException : No need to adjust a revenue by zero.
				DisplayUtil.displayError("Adjustment cannot be made if difference is zero.  If correct, Cancel the Invoice.");
				operationOK = false;
			}
			if (adjustmentDocID == null) {
				DisplayUtil.displayError("Please enter Document ID");
				operationOK = false;
			}
			if (reason == null) {
				DisplayUtil.displayError("Please enter Reason");
				operationOK = false;
			}

			if (invoice.getRevenueId() != null && operationOK) {
				// Revised Report- Adjusted Invoice will maintain same RevenueId
				// as its previous Invoice and the difference
				// between current Invoice and previous Invoice will be posted
				// as an Adjustment to Current Amount in Revenues.

				if (invoice.isAdjustedInvoice()) {
                    invoice.setInvoiceStateCd(InvoiceState.POSTED_TO_REVENUES);
                    invoice.setDueDate(invoiceDate);
                    adjustmentDocID = SystemPropertyDef.getSystemPropertyValue("Revenue_Adjustment_DocumentID", null);
                    int nextAdj = invoice.highestAdjustment() + 1;
                    invoice.setAdjustmentNum(nextAdj);
                    reason = Invoice.prefix + nextAdj + "): " + SystemPropertyDef.getSystemPropertyValue("RevisedRptAdjustment_Reason", null);
                    getInfrastructureService().modifyInvoice(invoice);
					getInfrastructureService().createAdjustment(user, adjustmentType,
							invoice, adjustmentAmount, adjustmentDocID, reason);
					resetNewAdjustment();// clear out adjustment fields
					DisplayUtil.displayInfo("Adjustment posted successful");

				} else {// new Adjustment
					getInfrastructureService().createAdjustment(user, adjustmentType,
							invoice, adjustmentAmount, adjustmentDocID, reason);
					resetNewAdjustment();
					DisplayUtil.displayInfo("Adjustment posted successful");
					closeDialog();
				}
				internalSubmit();
			}
		} catch (RemoteException re) {
			DisplayUtil.displayError("Adjustment failed");
            err = true;
			handleException(re);
		}
	}

	public final String editBillAddress() {
		return "dialog:editBillAddress";
	}

	public final String adjustmentDialog() {
		return "dialog:createAdjustment";
	}

	public final void closeDialog() {
		FacesUtil.returnFromDialogAndRefresh();
	}
    
    public final String submit() {
        clearRevenueIdFlag = false;
        invoiceDate = null;
        return internalSubmit();
    }

	public final String internalSubmit() {
		String ret = null;
        facility = null;
        err = false;
        contactModify = false;
        setEditable(false);
        setEditable1(false);
		try {
			reset();
			user = InfrastructureDefs.getPortalUser();						
			invoice = getInfrastructureService().retrieveInvoice(user, invoiceId, false);			
			notesWrapper.setWrappedData(invoice.getNotes());
            autoGenRpt = false;
            allowUnPost = false;
            try{
                facility = getFacilityService().retrieveFacility(invoice.getFacilityId());
                if(facility == null) {
                    String s = "Unable to retrieve facility " + 
                        invoice.getFacilityId() + " for this Invoice";
                    DisplayUtil.displayError(s);
                    logger.error("internalSubmit, " + s);
                    err = true;
                    return null;
                }
            } catch(RemoteException re){
                DisplayUtil.displayError("Unable to retrieve facility " + 
                            invoice.getFacilityId() + " for this Invoice");
                err = true;
                handleException(re);
                return null;
            }
            report = null;
			if (invoice.getEmissionsRptId() != null) {

				report = getEmissionsReportService()
						.retrieveEmissionsReport(invoice.getEmissionsRptId(),
								false);
                // existingPurchaseOwner flag indicates whether the
                // billing contact in the report is just for this
                // invoice.
                    existingPurchaseOwner = true;

                EmissionsReport tvSmtvRpt = null;
                ntvReport = null;
					setReportType(EmissionReportsRealDef.TV);
                    tvSmtvRpt = report;
                // Determine where we get billing contact
                invoice.setContact(null);  // the one in the invoice is not used permanently (from database)
                contactComment = "NO USABLE BILLING CONTACT IN PROFILE";
                if(!existingPurchaseOwner) {
                    invoice.setContact(report.getBillingAddr()); 
                    contactComment = "Billing Contact for this invoice only";
                } 
                if(invoice.getContact() == null) { // get correct contact from facility
                    ContactUtil activeConU = getInfrastructureService().locateInvoiceContact(invoice, tvSmtvRpt, ntvReport);
                    if(activeConU != null) {
                        String startDate = "";
                        if(activeConU.getContactType().getStartDate() != null) {
                            startDate = ContactUtil.datePrtFormat(activeConU.getContactType().getStartDate());
                        }
                        String endDate = "current";
                        if(activeConU.getContactType().getEndDate() != null) {
                            endDate = ContactUtil.datePrtFormat(activeConU.getContactType().getEndDate());
                        }
                        contactComment = "Facility Inventory Billing Contact (" + startDate + " - " + endDate + ")";
                    }
                }
                
                if(invoice.getContact() == null) {
                    invoice.setHaveBillingAddress(false); // no billing address-->no operations
                }
                
                if(InvoiceState.hasBeenPosted(invoice.getInvoiceStateCd())
                        && invoice.getRevenueId() == null) {
                    invoice.setAllowOperations(false); // lost Revenue ID-->no operations
                    invoice.setPrevInvoiceFailureMsg("Invoice Id " + invoice.getInvoiceId() + ":" + Invoice.lostRevenueMsg());
                    allowUnPost = true;
                }
                
				if(!clearRevenueIdFlag) {
				    otherInvoices = getInfrastructureService().retrieveOtherInvoices(
				            user, report, invoice);
				    if (otherInvoices != null) {
				        invoice.setMultiInvoice(true);
				    }
				} else {
				    invoice.setAdjustedInvoice(false);
                }
			} else {
			    // This is for a permit
                contactComment = "NO CURRENT BILLING CONTACT IN PROFILE";
                ContactUtil activeConU = getInfrastructureService().locateInvoiceContact(invoice, null, null);
                if(activeConU != null) {
                    String startDate = "";
                    if(activeConU.getContactType().getStartDate() != null) {
                        startDate = ContactUtil.datePrtFormat(activeConU.getContactType().getStartDate());
                    }
                    String endDate = "current";
                    if(activeConU.getContactType().getEndDate() != null) {
                        endDate = ContactUtil.datePrtFormat(activeConU.getContactType().getEndDate());
                    }
                    contactComment = "Facility Inventory Billing Contact (" + startDate + " - " + endDate + ")";
                }
            }
			ret = "invoiceDetail";

		} catch (RemoteException re) {
			DisplayUtil.displayError("Failed to read invoice");
            err = true;
			handleException(re);
		}

		return ret;
	}

	public final void postToRevenues() {
        if (invoice.isAdjustedInvoice()) {
            postAdjustment();
            return;
        }
	    try {
            if (user == null) { 
                throw new DAOException("User is null"); 
            }
	        if(invoiceDate == null){
	            DisplayUtil.displayError("Enter Invoice Date");
	            return;
	        }	

	        ValidationMessage pvm = 
	            getInfrastructureService().preparePostToRevenue(user, invoice, invoiceDate,
	                    false);
	        if(pvm == null) {
	            if(isIntraStateVoucherFlag()){
	                DisplayUtil.displayInfo("ISTV is required to be generated out of OAKS for this facility.");
	            }
	            DisplayUtil.displayInfo("Post to Revenues successful");
	        } else {
	            DisplayUtil.displayError(pvm.getMessage());
	        }
	        internalSubmit();
	    } catch (RemoteException re) {				
	        DisplayUtil.displayError("Post to Revenues failed");
	        err = true;
	        handleException(re);
	    }		
	}

	public final void cancelInvoice(){
		try{
			invoice.setInvoiceStateCd(InvoiceState.CANCELLED);
			getInfrastructureService().modifyInvoice(invoice);
			// getEmissionsReportService().cancelEmissionsReport(invoice.getEmissionsRptId());
			DisplayUtil.displayInfo("Invoice Cancelled.");
			internalSubmit();
		} catch(RemoteException re){
			DisplayUtil.displayError("Invoice Cancel failed");
            err = true;
			handleException(re);
		}
	}
    
	public final void unPost() {
	    try{
	        if(InvoiceState.hasBeenPosted(invoice.getInvoiceStateCd())) {
	            invoice.setInvoiceStateCd(InvoiceState.READY_TO_INVOICE);
                invoice.setRevenueId(null);
	            getInfrastructureService().modifyInvoice(invoice);
	            DisplayUtil.displayInfo("Invoice is now un-posted");
	            internalSubmit();
	        }
	    } catch(RemoteException re){
	        DisplayUtil.displayError("Invoice un-post failed");
            err = true;
	        handleException(re);
	    }
	}
	
	public final String confirmIndicatorAdjustment() {
		return "dialog:confirmIndicatorAdjustment";
	}

	public final void doIndicatorAdjustment() {
		boolean operationOK = true;

		try {
			if (invoice.getRevenueId() != null) {
				
				getInfrastructureService().performIndicatorAdjustment(user,
						invoice.getRevenueId(), adjustmentType);
			}
		} catch (RemoteException re) {
			operationOK = false;
			handleException(re);
		}
		if (operationOK) {
			DisplayUtil.displayInfo("Indicator adjustment successful");
            closeDialog();
            internalSubmit();
		} else {
            err = true;
			DisplayUtil.displayError("Indicator adjustment failed");
		}
	}

	public final boolean isIntraStateVoucherFlag(){
	    boolean ret = false;
	    if(facility != null && facility.isIntraStateVoucherFlag()){
	        ret = true;			
	    }
	    return ret;
	}
	
	public final void printInvoice() {
		PermitDocument permitDoc;
		TemplateDocument templateDoc;
		EmissionsReport report = null;
		String templateTypeCD = null;

		if (user != null) {
			try {
				if (invoice.getPermitId() != null) {
					PermitInfo info = getPermitService().retrievePermit(
							invoice.getPermitId());
					Permit permit = info.getPermit();
					templateTypeCD = TemplateDocTypeDef.PERMIT_INVOICE;

					templateDoc = TemplateDocTypeDef
							.getTemplate(templateTypeCD);

					if ((permitDoc = getInfrastructureService().generatePermitInvDocument(facility, 
									permit, invoice, "15", templateDoc)) != null) {
						invoice.setPermitInvDocument(permitDoc);
						getInfrastructureService().modifyInvoiceDocument(invoice);
					}
				}
				if (invoice.getEmissionsRptId() != null) {					
					report = getEmissionsReportService().retrieveEmissionsReport(
							invoice.getEmissionsRptId(), false);
					String corrTypeCD = null;
					String reportCategory = RevenueTypeDef
								.getReportCategory(invoice.getRevenueTypeCd());
				
					if (reportCategory.equals(EmissionReportsRealDef.TV)) {
						templateTypeCD = TemplateDocTypeDef.TV_FEE_INVOICE;
                        corrTypeCD = "20";

					} else if (reportCategory.equals(EmissionReportsRealDef.SMTV)) {
						templateTypeCD = TemplateDocTypeDef.SMTV_FEE_INVOICE;
                        corrTypeCD = "14";

					} else if (reportCategory.equals(EmissionReportsRealDef.NTV)) {
						templateTypeCD = TemplateDocTypeDef.NTV_FEE_INVOICE;
                        corrTypeCD = "11";
					}

					templateDoc = TemplateDocTypeDef.getTemplate(templateTypeCD);

					getInfrastructureService().generateReportInvDocument(facility, report,
							invoice, corrTypeCD, templateDoc);
							
					/*if ((reportDoc = getInfrastructureService().generateReportInvDocument(facility, report,
										invoice, templateDoc)) != null) {
						invoice.setReportInvDocument(reportDoc);
					}*/
				}
				//getInfrastructureService().modifyInvoiceDocument(invoice);

				DisplayUtil.displayInfo("Invoice Document generation successful.");
				internalSubmit();
			} catch(DocumentGenerationException dge) {
                if(invoice != null && invoice.getContact() == null) {
                    DisplayUtil.displayError("No active billing contact for invoice");
                    err = true;
                } else {
                    DisplayUtil.displayError("Invoice Document generation failed."
                            + dge.getMessage());
                    DisplayUtil.displayError("A system error has occurred. Please contact System Administrator.");
                    logger.error(dge.getMessage(), dge);
                    err = true;
                }
            } catch (RemoteException re) {
				DisplayUtil.displayError("Invoice Document generation failed."
						+ re.getMessage());
                err = true;
				handleException(re);
			}
		} else {
			DisplayUtil.displayError("Cannot generate Document, User is null.");
            err = true;
		}
	}
    
    public final boolean isNewRevenueButton() {
        return invoice.getRevenueId() != null && invoice.getEmissionsRptId() != null &&
        !editable && !invoice.getInvoiceStateCd().equals(InvoiceState.POSTED_TO_REVENUES) &&
                !invoice.getInvoiceStateCd().equals(InvoiceState.LATE_LETTER_INVOICE) &&
                !invoice.getInvoiceStateCd().equals(InvoiceState.CANCELLED);
}
    
    public final String clearRevenueId() {
        invoice.setRevenueId(null);
        invoice.setRevenue(null);
        invoice.setRevenueImmediate(null);
        clearRevenueIdFlag = true;
        try {
            getInfrastructureService().modifyInvoice(invoice);
            DisplayUtil.displayInfo("Invoice data saved successfully");
            internalSubmit();
        } catch (RemoteException re) {
            DisplayUtil.displayError("Updating invoice data failed");
            err = true;
            handleException(re);
        }
        closeDialog();
        return null;
    }

	public final String startAddNote() {
		setEditable1(true);
		noteModify = false;
		invoiceNote = new InvoiceNote();
		invoiceNote.setNoteTypeCd(NoteType.DAPC);
		invoiceNote.setInvoiceId(invoice.getInvoiceId());
		invoiceNote.setUserId(InfrastructureDefs.getCurrentUserId());
		invoiceNote.setDateEntered(new Timestamp(System.currentTimeMillis()));
		return "dialog:noteDetail";
	}

	public final String startEditNote(ActionEvent actionEvent) {
		setEditable1(true);
		noteModify = true;
		return "dialog:noteDetail";
	}

	public final String startViewNote() {
		noteModify = false;
		invoiceNote = new InvoiceNote(modifyInvoiceNote);
		return "dialog:noteDetail";
	}

	public final void applyEditNote(ActionEvent actionEvent) {
		boolean operationOK = true;
		setEditable1(false);
		
		// make sure note is provided
		if (invoiceNote.getNoteTxt() == null || invoiceNote.getNoteTxt().trim().equals("")) {
			DisplayUtil.displayError("Attribute Note is not set.");
			return;
		}

		ValidationMessage[] validationMessages = invoiceNote.validate();

		if (validationMessages.length > 0) {
			if (displayValidationMessages("", validationMessages)) {
				return;
			}
		}
		try {
			if (!noteModify) {
				getInfrastructureService().createInvoiceNote(invoiceNote);
			} else {
				getInfrastructureService().modifyInvoiceNote(invoiceNote);
			}

			invoiceNotes = getInfrastructureService().retrieveInvoiceNotes(invoiceId);
			notesWrapper.setWrappedData(invoiceNotes);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			operationOK = false;
		}

		if (operationOK) {
			DisplayUtil.displayInfo("Invoice note added/updated successfully");
		} else {
			//cancelEditNote();
            err = true;
			DisplayUtil.displayError("Adding/Updating invoice note failed");
		}
		closeDialog();
	}

	public final void cancelEditNote() {
		invoiceNote = null;
		noteModify = false;
		setEditable1(false);
		closeDialog();
	}

	public final Integer getUserID() {
		return InfrastructureDefs.getCurrentUserId();
	}

	public final boolean isPostToRevenuesVisible() {
		return InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid(
				"invoices.posttorevenues")  && invoice.isAllowOperations();
	}

//    public final boolean isDisplayCancelButton() {
//        return InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid(
//        "invoices.posttorevenues") && !editable  && invoice.isAllowOperations()
//        && invoice.getInvoiceStateCd() != null
//        &&  invoice.getInvoiceStateCd().equals(InvoiceState.READY_TO_INVOICE);
//    }

	public final boolean isStars2Admin() {
		return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin();
	}

	public final InvoiceNote getInvoiceNote() {
		return invoiceNote;
	}

	public final void setInvoiceNote(InvoiceNote invoiceNote) {
		this.invoiceNote = invoiceNote;
	}

	public final InvoiceNote getModifyInvoiceNote() {
        return modifyInvoiceNote;
    }

    public final void setModifyInvoiceNote(InvoiceNote modifyInvoiceNote) {
        this.modifyInvoiceNote = modifyInvoiceNote;
    }
    
	public final User getUser() {
		return user;
	}

	public final Integer getInvoiceId() {
		return invoiceId;
	}

	public final void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
		SimpleMenuItem.setDisabled("menuItem_InvDetail", false);
	}

	public Integer getEmissionsRptId() {
		return emissionsRptId;
	}

	public void setEmissionsRptId(Integer emissionsRptId) {
		this.emissionsRptId = emissionsRptId;
	}

	public Integer getPermitId() {
		return permitId;
	}

	public void setPermitId(Integer permitId) {
		this.permitId = permitId;
	}

	public final MultiInvoiceList[] getOtherInvoices() {
		return otherInvoices;
	}

	public final List<SelectItem> getAdjustmentTypes() {
		return AdjustmentType.getData().getItems().getItems(adjustmentType,
				true);
	}

	public final Double getAdjustmentAmount() {
		return adjustmentAmount;
	}

	public final void setAdjustmentAmount(Double adjustmentAmount) {
		this.adjustmentAmount = adjustmentAmount;
	}

	public final String getConfirmMsg() {
		return INDICATOR_MSG;
	}

	public final String getAdjustmentType() {
		return adjustmentType;
	}

	public final void setAdjustmentType(String adjustmentType) {
		this.adjustmentType = adjustmentType;
	}

	public final String getReason() {
		return reason;
	}

	public final void setReason(String reason) {
		this.reason = reason;
	}

	public String getConfirmMessage() {
	    if (confirmOperation.equals("Post to Revenues")) {
	        if(confirmPostWithMultiple != null && confirmPostWithMultiple.length() > 0) {
                confirmMessage = "You have selected to post this Invoice to Revenues.<br>However there are later reports/invoices: " +
                confirmPostWithMultiple + "<br><br>If you Post this invoice to Revenues there may be later revised invoices to Post.<br>Would you like to Post this invoice anyway? ";
	        } else {
	            confirmMessage = "You have selected to post this Invoice to Revenues. Would you like to continue? ";
	        }
	    } else if (confirmOperation.equals("Print Invoice")) {
	        confirmMessage = "Select 'Yes' to Generate Invoice Document or 'No' to cancel.";
	    } else if(confirmOperation.equals("Cancel")){
	        confirmMessage = "You have selected to dead-end the Invoice. Would you like to continue? ";
	    }
	    return confirmMessage;
	}

	public void setConfirmMessage(String confirmMessage) {
		this.confirmMessage = confirmMessage;
	}

	public String getConfirmOperation() {
		return confirmOperation;
	}

	public void setConfirmOperation(String confirmOperation) {
	    this.confirmOperation = confirmOperation;
	    confirmPostWithMultiple = null;
	    if (confirmOperation.equals("Post to Revenues")) {
	        try {
	            if(report != null) {
	                confirmPostWithMultiple = getInfrastructureService().newerAvailable(report.getEmissionsRptId());
	            }
	        } catch (RemoteException re) {
	            confirmPostWithMultiple = "Stars2 failed when looking for newer reports/invoices.  It is recommended to respond \"No\" until this problem is resolved.";
	            logger.error("Failed performing newerAvailable(" + emissionsRptId + "); " + re.getMessage(), re);
	        }
	    }
	}

	public final void performOperation() {
		if (confirmOperation.equals("Post to Revenues")) {
			postToRevenues();
		} else if (confirmOperation.equals("Print Invoice")) {
			printInvoice();
		} else if(confirmOperation.equals("Cancel")){
			cancelInvoice();
		}  else if(confirmOperation.equals("UnPost")){
           unPost();
        }

		confirmOperation = "";
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void cancelOperation() {
		confirmOperation = "";
		FacesUtil.returnFromDialogAndRefresh();
	}
    
	public NameValue[] getEmissionRanges() {
		ArrayList<NameValue> ret = new ArrayList<NameValue>();

        for (String key : emissionRanges.keySet()) {
            ret.add(new NameValue(key, emissionRanges.get(key)));
        }

        return ret.toArray(new NameValue[0]);	
	}

	public final String getReportType() {
		return reportType;
	}

	public final void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public final TableSorter getNotesWrapper() {
		return notesWrapper;
	}	

	public final String getRevenueState() {
		String ret = null;
		
		if(invoice != null && invoice.getRevenue() != null){
			if (invoice.getRevenue().getBalanceDue().getAsDouble() < .1d) {
				ret = "Paid in Full";
			} else if (invoice.getRevenue().getBalanceDue().getAsDouble() >= .1d) {
				ret = "Not Paid";
			}

			if (invoice.getRevenue().isCertified()) {
				ret = "Certified to AGO";
			}
		}

		return ret;
	}

    public boolean isAutoGenRpt() {
        return autoGenRpt;
    }

    public String getContactComment() {
        return contactComment;
    }

    public boolean isAllowUnPost() {
        return allowUnPost;
    }
    
    public String getUnPostLabel() {
        return staticUnPostLabel();
    }
    
    public static String staticUnPostLabel() {
        String rtn = "Move Invoice state back to \"Ready to Invoice\"";
        String s = null;
        s = InvoiceState.getData().getItems().getItemDesc(InvoiceState.READY_TO_INVOICE);
        if(s != null) {
            rtn = "Move Invoice state back to \"" + s + "\"";
        }
        return rtn;
    }

    public boolean isErr() {
        return err;
    }

    public String getConfirmPostWithMultiple() {
        return confirmPostWithMultiple;
    }

    public Timestamp getMaxInvoiceDate() {
        return maxInvoiceDate;
    }

    public Timestamp getMinInvoiceDate() {
        return minInvoiceDate;
    }
}
