package us.oh.state.epa.stars2.app.relocation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import oracle.adf.view.faces.context.AdfFacesContext;
import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.app.application.ApplicationDetail;
import us.oh.state.epa.stars2.app.application.ApplicationSearch;
import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.bo.GenericIssuanceService;
import us.oh.state.epa.stars2.bo.RelocateRequestService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocationAddtlAddr;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.genericIssuance.GenericIssuance;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.GenericIssuanceTypeDef;
import us.oh.state.epa.stars2.def.RelocationAttachmentTypeDef;
import us.oh.state.epa.stars2.def.RelocationDispositionDef;
import us.oh.state.epa.stars2.def.RelocationJFODef;
import us.oh.state.epa.stars2.def.RelocationTypeDef;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.application.ApplicationDetailCommon;
import us.oh.state.epa.stars2.webcommon.bean.GeneralIssuance;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.document.AttachmentEvent;
import us.oh.state.epa.stars2.webcommon.document.AttachmentException;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentListener;

@SuppressWarnings("serial")
public class Relocation extends TaskBase implements IAttachmentListener {
    private RelocateRequest relocateRequest;
    private RelocateRequest[] relocateRequests;
    private boolean editable;
    private Facility facility;
    private boolean workflowEnabled=false;
    private boolean staging = false;
    private String popupRedirectOutcome;
    private static final Integer ITR_ISSUANCE = 115;
    private SimpleDef[] preApprovedAddressList;
    private String preApprovedAddressCd;

	private List<RelocationAddtlAddr> allAddresses;
	
	private GenericIssuanceService genericIssuanceService;
	private RelocateRequestService relocateRequestService;

	public RelocateRequestService getRelocateRequestService() {
		return relocateRequestService;
	}

	public void setRelocateRequestService(
			RelocateRequestService relocateRequestService) {
		this.relocateRequestService = relocateRequestService;
	}

	public GenericIssuanceService getGenericIssuanceService() {
		return genericIssuanceService;
	}

	public void setGenericIssuanceService(
			GenericIssuanceService genericIssuanceService) {
		this.genericIssuanceService = genericIssuanceService;
	}

    public String getPopupRedirectOutcome() {
        return popupRedirectOutcome;
    }

    public void setPopupRedirectOutcome(String popupRedirectOutcome) {
        this.popupRedirectOutcome = popupRedirectOutcome;
    }

    public boolean isWorkflowEnabled() {
        return workflowEnabled;
    }

    public void setWorkflowEnabled(boolean workflowEnabled) {
        this.workflowEnabled = workflowEnabled;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isIncludeIssuance() {
        boolean ret = false;
        //only show Issuance panel if issuance is ITR or SPA && its submitted and it has a JFOReco
        if (getRelocateRequest().getApplicationTypeCD().equals(RelocationTypeDef.INTENT_TO_RELOCATE) 
                || getRelocateRequest().getApplicationTypeCD().equals(RelocationTypeDef.SITE_PRE_APPROVAL)) {
            if (getRelocateRequest().getSubmittedDate() !=null && getRelocateRequest().getJfoRecommendation()!=null) {
                ret = true;
            }
        }
        return ret;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public String findOutcome(String url, String ret) {
        ApplicationDetail applicationDetail = (ApplicationDetail) FacesUtil
        .getManagedBean("applicationDetail");

        applicationDetail.setApplicationID(relocateRequest.getApplicationID());

        return "applicationDetail";
    }

    @Override
    public String getExternalNum() {
        return getRelocateRequest().getApplicationNumber();
    }

    @Override
    public Integer getExternalId() {
        return getRelocateRequest().getApplicationID();
    }

    @Override
    public void setExternalId(Integer externalId) {
        setRelocateRequestID(externalId);
        this.setWorkflowEnabled(true);
    }

    @Override
    public List<ValidationMessage> validate(Integer inActivityTemplateId) {
        ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();
        if (ITR_ISSUANCE.equals(inActivityTemplateId)) {
            if (relocateRequest != null) {
                //only validate issuance if request is ITR or SPA
                if (RelocationTypeDef.INTENT_TO_RELOCATE.equals(relocateRequest.getApplicationTypeCD()) ||
                        RelocationTypeDef.SITE_PRE_APPROVAL.equals(relocateRequest.getApplicationTypeCD())) {
                    try {
                        List<GenericIssuance> issuances = getGenericIssuanceService().searchGenericIssuances(null,
                                relocateRequest.getApplicationID(), null, null);
                        int issuedCount = 0;
                        if (issuances != null && issuances.size() > 0) {
                            for (GenericIssuance gi : issuances) {
                                if (gi.isIssued()) {
                                    issuedCount++;
                                }
                            }
                        } 
                        if (issuedCount > 1) {
                            messages.add(new ValidationMessage("Application",
                                    "There are multiple issued issuances for this application. " +
                                    "Only one issuance is allowed.",
                                    ValidationMessage.Severity.ERROR,
                                    ValidationBase.APPLICATION_TAG));
                        } else if (issuedCount == 0){
                            messages.add(new ValidationMessage("Application",
                                    "There are no issued issuances for this application.",
                                    ValidationMessage.Severity.ERROR,
                                    ValidationBase.APPLICATION_TAG));
                        }
                    } catch (RemoteException e) {
                        handleException(e);
                    }
                }
            } else {
                messages.add(new ValidationMessage("Application",
                        "A system error has occurred. Please contact System Administrator.",
                        ValidationMessage.Severity.ERROR));
                logger.error("relocateRequest is null");
            }
        }
        return messages;
    }

    public String validate() {
        if (validateRequest()) {
            DisplayUtil.displayInfo("Relocation Request is valid and ready to submit.");
        }
        return "";
    }

    private boolean validateRequest() {
        boolean response=true;
        if (relocateRequest.getReceivedDate()==null) {
            DisplayUtil.displayWarning("The date the request was received is required prior to saving.");
            response = false;
        } 
        return response;
    }

    public RelocateRequest getRelocateRequest() {
        return relocateRequest;
    }

    public void setRelocateRequest(RelocateRequest relocateRequest) {
        preApprovedAddressList = null;
        preApprovedAddressCd = null;
        allAddresses = null;
        this.relocateRequest = relocateRequest;
        //only handle issuances if its an ITR or RPS

        if (isIncludeIssuance()) {
            try {
                GeneralIssuance gi = (GeneralIssuance) FacesUtil
                .getManagedBean("generalIssuance");
                gi.setIssuanceTypes(GenericIssuanceTypeDef.getTypes(relocateRequest,null));
                gi.loadIssuances(relocateRequest);

                /*
                 * The following depends on the type of ITR and its disposition state
                 */

                gi.setIssuanceAllow(false);
                if (gi != null) {
                    if (relocateRequest.getApplicationTypeCD().equals(RelocationTypeDef.INTENT_TO_RELOCATE)) {
                        if (relocateRequest.getJfoRecommendation().equals(RelocationJFODef.APPROVE)) {
                            //  gi.setIssuanceTypeCd(GenericIssuanceTypeDef.INTENT_TO_RELOCATE_APPROVED);
                            gi.setIssuanceAllow(true);
                        } else if (relocateRequest.getJfoRecommendation().equals(RelocationJFODef.APPROVE_WITH_CONDITIONS)) {
                            //   gi.setIssuanceTypeCd(GenericIssuanceTypeDef.INTENT_TO_RELOCATE_APPROVED_COND);
                            gi.setIssuanceAllow(true);
                        } else if (relocateRequest.getJfoRecommendation().equals(RelocationDispositionDef.DENIED)) {
                            //   gi.setIssuanceTypeCd(GenericIssuanceTypeDef.INTENT_TO_RELOCATE_DENIED);  
                            gi.setIssuanceAllow(true);
                        } 
                    } else if (relocateRequest.getApplicationTypeCD().equals(RelocationTypeDef.SITE_PRE_APPROVAL)) {
                        if (relocateRequest.getJfoRecommendation().equals(RelocationJFODef.APPROVE)) {
                            //   gi.setIssuanceTypeCd(GenericIssuanceTypeDef.SITE_PRE_APPROVAL_APPROVED);
                            gi.setIssuanceAllow(true);
                        } else if (relocateRequest.getJfoRecommendation().equals(RelocationJFODef.APPROVE_WITH_CONDITIONS)) {
                            //   gi.setIssuanceTypeCd(GenericIssuanceTypeDef.SITE_PRE_APPROVAL_APPROVED_COND);   
                            gi.setIssuanceAllow(true);
                        } else if (relocateRequest.getJfoRecommendation().equals(RelocationDispositionDef.DENIED)) {
                            //   gi.setIssuanceTypeCd(GenericIssuanceTypeDef.SITE_PRE_APPROVAL_DENIED);   
                            gi.setIssuanceAllow(true);
                        } 
                    } 
                } else {
                    logger.error("Unable to get reference to issuance bean");
                }
                gi.setBean(this);
            } catch (Exception e) {
                logger.error("Error setting up General Issuance backing bean",e);
            }
        }

        initializeAttachmentBean();

    }

    public void setRelocateRequestID(int id) {
        /*
         * retrieve relocate request with provided ID and set
         */
        try {
            setRelocateRequest(getRelocateRequestService().retrieveRelocateRequest(id, false));
            initializeAttachmentBean();
        } catch (Exception exception) {
            logger.error("Unable to retrieve Relocate Request",exception);
        }
    }

    public final String startEditRequest(ActionEvent actionEvent) {
        setEditable(true);
        initializeAttachmentBean();
        return "dialog:editRelocation";
    }

    public final String startAddRequest() {
        setEditable(true);

        relocateRequest = new RelocateRequest();
        relocateRequest.setFacility(facility);
        relocateRequest.setUserId(InfrastructureDefs.getCurrentUserId());
        relocateRequest.setNewRecord(true);
        relocateRequest.setApplicationTypeCD("");
        return "dialog:editRelocation";
    }

    public DefData getTypeDef() {
        DefData dd = ApplicationTypeDef.getData();
        dd.addExcludedKey("PBR");
        dd.addExcludedKey("PTIO");
        dd.addExcludedKey("TV");
        dd.addExcludedKey("RPC");
        dd.addExcludedKey("RPE");
        dd.addExcludedKey("RPR");
        return dd;
    }
    public final List<SelectItem>  getActivePermits() {
        DefData data = new DefData();
        /*
         * No longer needed per Erica 4/24/2008
         */
        /*
        try {
            Permit x[] = getRelocateRequestService().getPermits(getFacility().getFacilityId());
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            for (int i=0;i<x.length;i++) {
                String permitId = x[i].getPermitID().toString();
                if (x[i].getFinalIssueDate() != null) {
                    data.addItem(permitId,x[i].getPermitNumber() + " (Issued: "+df.format(x[i].getFinalIssueDate())+")");
                } else {
                    data.addItem(permitId,x[i].getPermitNumber() + " (Not issued)");
                }
            }
        } catch (Exception e) {
            DisplayUtil.displayWarning("System error retrieving active permits.  Please contact support.");
            logger.error(e.getMessage(), e);
        } 
         */
        return data.getItems().getItems(getParent().getValue());
    }


    public final void save(ActionEvent actionEvent) {
    	if(save().equals(SUCCESS)){
    		save();
    		ApplicationSearch asc = (ApplicationSearch) FacesUtil
    				.getManagedBean("applicationSearch");
    		asc.setPopupRedirectOutcome("applicationDetail");
    		FacesUtil.returnFromDialogAndRefresh();
    	}
    }


    public String save() {
        try {
        	ValidationMessage[] validationMessages;
        	if (allAddresses != null) {
        		relocateRequest.setAllAddresses(allAddresses.toArray(new RelocationAddtlAddr[0]));
        	} else {
        		relocateRequest.setAllAddresses(null);
        	}
            if (relocateRequest.isNewRecord()) {
            	validationMessages = getRelocateRequestService().validateRelocateRequest(relocateRequest);
        		if (displayValidationMessages("newApplcation:", validationMessages)) {
        			return FAIL;
        		}
                relocateRequest=getRelocateRequestService().createRelocateRequest(relocateRequest);
                //logger.debug("initializaing attachment bean");
                initializeAttachmentBean();
                relocateRequest.setNewRecord(false);
                setFromTODOList(false);
            } else {
                relocateRequest=getRelocateRequestService().modifyRelocateRequest(relocateRequest);
            }
            //setEditMode(false);
            setEditable(false);
            initializeAttachmentBean();
            refresh();
        } catch (Exception e) {
            logger.error("Unable to create/update Relocation Request", e);
            DisplayUtil.displayError("Error encountered. Unable to save");
        }
        return SUCCESS;
    }

    /*
    private void handleDocumentsModified() {
        // attachmentManager = (Attachments) FacesUtil
        // .getManagedBean(ATTACHMENT_MANAGED_BEAN);
        //List<Integer> ds = new ArrayList<Integer>();

        docsMap = new HashMap<String, Map<String, PermitDocument>>();
        attachments = new ArrayList<PermitDocument>();
        tcs = new ArrayList<PermitDocument>();
        topTCDoc = null;
        for (PermitDocument doc : permit.getDocuments()) {
            if (doc.getPermitDocTypeCD().equals(PermitDocTypeDef.ATTACHMENT)
                || doc.getPermitDocTypeCD().equals(PermitDocTypeDef.MULTIMEDIA_LETTER)) {
                attachments.add(doc);
                //ds.add(doc.getDocumentID());
            } else if (doc.getPermitDocTypeCD().equals(
                    PermitDocTypeDef.TERMS_CONDITIONS)) {
                tcs.add(doc);
                if (topTCDoc == null)
                    topTCDoc = doc;
            } else if (doc.getPermitDocTypeCD().equals(PermitDocTypeDef.PREVIOUSLY_ISSUED)) {
                tcs.add(doc);
                if (topTCDoc == null)
                    topTCDoc = doc;
            }
            else {
                Map<String, PermitDocument> byType = docsMap.get(doc
                        .getPermitDocTypeCD());
                if (byType == null) {
                    byType = new HashMap<String, PermitDocument>();
                    docsMap.put(doc.getPermitDocTypeCD(), byType);
                }
                byType.put(doc.getIssuanceStageFlag() == null ? "" : doc
                        .getIssuanceStageFlag(), doc);

                // Also add issuance document into T&C table.
                if (doc.getPermitDocTypeCD().equalsIgnoreCase(
                        PermitDocTypeDef.ISSUANCE_DOCUMENT))
                    tcs.add(doc);
            }
        }

    }
     */

    private void refresh() {
        /*
         * refreshes the application search bean
         */
        preApprovedAddressList = null;
        preApprovedAddressCd = null;
        ApplicationSearch asc = (ApplicationSearch) FacesUtil
        .getManagedBean("applicationSearch");
        asc.search();
        ApplicationDetail applicationDetail = (ApplicationDetail) FacesUtil
        .getManagedBean("applicationDetail");
        applicationDetail.setApplicationID(getRelocateRequest().getApplicationID());
        applicationDetail.reload();
        relocateRequest = (RelocateRequest)applicationDetail.getApplication();
    }

    public final void submit(ActionEvent actionEvent) {
        setEditable(false);
        initializeAttachmentBean();
        //setEditMode(false);
        try {
            if (validateRequest()) {
                //if valid update and submit to workflow
                if (getRelocateRequestService().submit(relocateRequest, true)) {
                    //  relocateRequest.setDateSubmitted(new Timestamp(System.currentTimeMillis()));
                    //  save();
                    DisplayUtil.displayInfo("Record submitted");
                    // getRelocateRequests();  //refresh our list
                } else {
                    DisplayUtil.displayInfo("Error submitting record. Unable to generate workflow Task.");
                }
            } else {
                DisplayUtil.displayError("Record is not valid.  Please provide all required fields and re-submit");
            }
            refresh();
            //   FacesUtil.returnFromDialogAndRefresh();
        } catch (Exception e) {
            //put error message up to user
            logger.error("Unable to submit request", e);
            DisplayUtil.displayError("Error encountered. Unable to save");
        }
    }

    public boolean isAdmin() {
        return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin(); 
    }

    public String deleteRequest() {
        try {
            setEditable(false);
            // only delete if the record is not yet submitted
            if (relocateRequest.getSubmittedDate() == null) {
                //logger.debug("Deleting record");
                getRelocateRequestService().deleteRelocateRequest(relocateRequest, true);
                DisplayUtil.displayInfo("Application deleted");
                return "applicationSearch";
            }
            DisplayUtil.displayError("Request has already been submitted and may not be deleted");
            return "";

        } catch (Exception e) {
            // put error message up to user
            logger.error("Attempging to delete record " + e.getMessage(), e);
            DisplayUtil.displayError("Error encountered. Unable to delete");
            return "";
        }
    }

    public final String cancelEditRequest() {
        //  facilityRUM = null;
        //  rumModify = false;
        ApplicationDetailCommon applicationDetail = 
            (ApplicationDetailCommon)FacesUtil.getManagedBean("applicationDetail");
        applicationDetail.reload();
        relocateRequest = (RelocateRequest)applicationDetail.getApplication();
        setEditable(false);
        initializeAttachmentBean();
        return "applicationDetail";
    }

    private void resetNewApplication() {
        ApplicationSearch asc = (ApplicationSearch) FacesUtil
        .getManagedBean("applicationSearch");
        asc.resetNewApplication();
    }

    public final void cancelNewApplication(ActionEvent actionEvent) {
        resetNewApplication();
        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }

    public final void initialize() {
        //logger.debug("Initialize facility in relocation");
        FacilityProfile fp = (FacilityProfile) FacesUtil
        .getManagedBean("facilityProfile");
        setFacility(fp.getFacility());  
    }

    public final void dialogDone() {
        return;
    }


    public void initializeAttachmentBean() {

        if (relocateRequest.getApplicationNumber() != null && relocateRequest.getApplicationNumber().length()>0) {
            /* STEP 1
             * Get a reference to the Attachment backing bean
             */

            Attachments a = (Attachments) FacesUtil
            .getManagedBean("attachments");
            a.addAttachmentListener(this);
            a.setStaging(false);    //this feature only supported internally (no portal version)

            //default evertyhing to uneditable
            //     a.setNewPermitted(false);
            a.setUpdatePermitted(false);

            if (getRelocateRequest().getSubmittedDate()==null) {   
                //it hasn't been submitted yet, so allow them to add/edit if we're in edit mode
                a.setNewPermitted(!editable);
                a.setUpdatePermitted(!editable);
            } else {
                //if we're admin then we can still update
                if (isAdmin())  {
                    a.setNewPermitted(!editable);
                    a.setUpdatePermitted(!editable);
                }
            }

            /* STEP 2
             * Create a new, empty Document object, set its FacilityID and 
             * give the attachment backing bean a reference to it. 
             */
            //Attachment d = new Attachment();
            a.setSubPath("Applications");
            a.setObjectId(Integer.toString(this.getRelocateRequest().getApplicationID()));
            a.setFacilityId(getRelocateRequest().getFacilityId());
            // a.setDocument(d);

            /* STEP 3
             * Set the picklist in the backing bean for the document types
             */
            a.setSubtitle(null);
            a.setAttachmentTypesDef(RelocationAttachmentTypeDef.getData().getItems());            
            a.setHasDocType(true);

            a.setAttachmentList(this.relocateRequest.getAttachments());
        }

    }

    public RelocateRequest[] getRelocateRequests() {
        initialize();
        //logger.debug("return array of relocation requests for: "+getFacility().getFacilityId());
        /*
         * Assumes we already have a valid reference to the facility object
         */

        relocateRequests = new RelocateRequest[0];

        try {
            relocateRequests =  getRelocateRequestService().retrieveRelocateRequests(facility.getFacilityId());
        } catch (Exception re) {
            DisplayUtil.displayError("Retrieval of Relocation records failed");
            relocateRequests = new RelocateRequest[0];
            logger.error("Error retrieving relocate requests", re);
        }
        return relocateRequests;
    }

    public void setRelocateRequests(RelocateRequest[] relocateRequests) {
        this.relocateRequests = relocateRequests;
    }

    public void cancelAttachment() {
        popupRedirectOutcome=null;
        FacesUtil.returnFromDialogAndRefresh();
    }

    public AttachmentEvent createAttachment(Attachments attachment)
    throws AttachmentException {
        //go create our record in the database
        try {
            getRelocateRequestService().createRelocationAttachment(relocateRequest,attachment.getTempDoc(),attachment.getFileToUpload().getInputStream());
            relocateRequest = getRelocateRequestService().retrieveRelocateRequest(relocateRequest.getRequestId(), false);  
        } catch (Exception e) {
            DisplayUtil.displayError("Error create attachment record.");
            logger.error(e.getMessage(), e);
        }
        //setEditMode(true);
        FacesUtil.returnFromDialogAndRefresh();
        return null;
    }

    public AttachmentEvent deleteAttachment(Attachments attachment) {
        try {
            getRelocateRequestService().deleteRelocationAttachment(relocateRequest, attachment.getTempDoc());
            relocateRequest = getRelocateRequestService().retrieveRelocateRequest(relocateRequest.getRequestId(), false);
        } catch (Exception e) {
            DisplayUtil.displayError("Error while deleting attachment.");
            logger.error(e.getMessage(), e);
        }
        //setEditMode(false);
        FacesUtil.returnFromDialogAndRefresh();
        return null;
    }

    public AttachmentEvent updateAttachment(Attachments attachment) {
        try {
            getRelocateRequestService().modifyRelocationAttachment(relocateRequest,attachment.getTempDoc());
            relocateRequest = getRelocateRequestService().retrieveRelocateRequest(relocateRequest.getRequestId(), false);
        } catch (Exception e) {
            DisplayUtil.displayError("Error while updating attachment.");
            logger.error(e.getMessage(), e);
        }
        //setEditMode(false);
        FacesUtil.returnFromDialogAndRefresh();
        return null;
    }

    @Override
    public String getExternalName(ProcessActivity activity) {
        return "Application";
    }

    @Override
    public String toExternal() {
        String ret = null;
        if (getExternalNum() != null) {
            ApplicationDetail applicationDetail = (ApplicationDetail)FacesUtil.getManagedBean("applicationDetail");
            applicationDetail.setApplicationNumber(getExternalNum());
            ret = "applicationDetail";
        }
        return ret;
    }

    @Override
    public String validationDlgAction() {
        String ret = null;
        if (getExternalNum() != null) {
            ApplicationDetail applicationDetail = (ApplicationDetail)FacesUtil.getManagedBean("applicationDetail");
            applicationDetail.setApplicationNumber(getExternalNum());
            ret = applicationDetail.validationDlgAction();
        }
        return ret;
    }

    public boolean isStaging() {
        return staging;
    }

    public void setStaging(boolean staging) {
        this.staging = staging;
    }

    /**
     * @return the readOnly
     */
    public final boolean isReadOnly() {
        return !isEditable() || (relocateRequest != null && relocateRequest.getSubmittedDate()!=null);
    }
    
    public final List<SelectItem> getPreApprovedAddressList() {
        List<SelectItem> itemList = new ArrayList<SelectItem>();
        if (preApprovedAddressList == null && relocateRequest != null && relocateRequest.getFacilityId() != null) {
            try {
                preApprovedAddressList = getRelocateRequestService().retrievePreApprovedAddressesForFacility(relocateRequest.getFacilityId());
            } catch (RemoteException e) {
                logger.error("Exception retrieving pre-approved relocation addresses.", e);
            }
        }
        for (SimpleDef preApprovedAddress : preApprovedAddressList) {
            if (preApprovedAddress.getCode() != null && preApprovedAddress.getDescription() != null) {
                itemList.add(new SelectItem(preApprovedAddress.getCode(), 
                        preApprovedAddress.getDescription()));
            }
        }
        
        return itemList;
    }

    public final String getPreApprovedAddressCd() {
        if (preApprovedAddressCd == null) {
            // make sure address id matches id for specifed address
            for (SimpleDef preAppovedAddress : preApprovedAddressList) {
                if (preAppovedAddress.getDescription() != null && 
                        preAppovedAddress.getDescription().equals(relocateRequest.getFutureAddress())) {
                	preApprovedAddressCd = preAppovedAddress.getCode();
                    break;
                }
            }
        }
        return preApprovedAddressCd;
    }

    public final void setPreApprovedAddressCd(String preApprovedAddressCd) {
        this.preApprovedAddressCd = preApprovedAddressCd;
        // set future address to match address selected from drop down list
        for (SimpleDef preAppovedAddress : preApprovedAddressList) {
            if (preAppovedAddress.getCode().equals(preApprovedAddressCd)) {
                relocateRequest.setFutureAddress(preAppovedAddress.getDescription());
                // reset lists of all addresses so new address will be picked up
                relocateRequest.setAllAddresses(null);
                setAllAddresses(null);
                break;
            }
        }
    }
    
    public final boolean isNonRPS() {
        return (relocateRequest != null && relocateRequest.getApplicationTypeCD() != null
                && relocateRequest.getApplicationTypeCD().trim().length() > 0 &&
                !relocateRequest.getApplicationTypeCD().equals("RPS"));
    }
    
    public final boolean isRelocateToPreApproved() {
        return (relocateRequest != null && relocateRequest.getApplicationTypeCD() != null
                && relocateRequest.getApplicationTypeCD().equals("RPS"));
    }
    
    public final List<RelocationAddtlAddr> getAllAddresses() {
    	if (allAddresses == null && relocateRequest != null) {
    		allAddresses = new ArrayList<RelocationAddtlAddr>();
    		for (RelocationAddtlAddr addr : relocateRequest.getAllAddresses()) {
    			allAddresses.add(addr);
    		}
    	}
    	return allAddresses;
    }
    
    public final void setAllAddresses(List<RelocationAddtlAddr> allAddresses) {
    	this.allAddresses = allAddresses;
    }
    
    public final RelocationAddtlAddr getNewRelocationAddr() {
    	RelocationAddtlAddr addr = new RelocationAddtlAddr();
    	if (relocateRequest != null) {
    		addr.setRequestId(relocateRequest.getRequestId());
    	}
    	return addr;
    }

}
