package us.oh.state.epa.stars2.portal.delegation;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import oracle.adf.view.faces.context.AdfFacesContext;
import us.oh.state.epa.stars2.bo.DelegationRequestService;
import us.oh.state.epa.stars2.database.dbObjects.application.DelegationRequest;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.DelegationDispositionDef;
import us.oh.state.epa.stars2.def.GenericIssuanceTypeDef;
import us.oh.state.epa.stars2.portal.application.ApplicationDetail;
import us.oh.state.epa.stars2.portal.application.ApplicationSearch;
import us.oh.state.epa.stars2.portal.facility.FacilityProfile;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.GeneralIssuance;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.document.AttachmentEvent;
import us.oh.state.epa.stars2.webcommon.document.AttachmentException;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentListener;

@SuppressWarnings("serial")
public class Delegation extends ValidationBase implements IAttachmentListener {
    private DelegationRequest delegationRequest;
    private DelegationRequest[] delegationRequests;
    private boolean editable;
    private Facility facility;
    private boolean workflowEnabled=false;
    private String popupRedirectOutcome;
    private DelegationRequestService delegationRequestService;

    public DelegationRequestService getDelegationRequestService() {
		return delegationRequestService;
	}

	public void setDelegationRequestService(
			DelegationRequestService delegationRequestService) {
		this.delegationRequestService = delegationRequestService;
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
        // only show Issuance panel if its submitted
        getDelegationRequest().isTitleV();
        if (getDelegationRequest().getSubmittedDate() != null) {// &&
                                                                // getDelegationRequest().getEffectiveDate()
                                                                // !=null
                                                                // //Mantis
                                                                // #2175
            return true;
        }
        
        if (getDelegationRequest().getSubmittedDate() != null) {// &&
                                                                // getDelegationRequest().getEffectiveDate()
                                                                // ==null
                                                                // //Mantis
                                                                // #2175
            // 2174: DOR Reminder Messages
            // DisplayUtil.displayInfo("Reminder: Issuances may not be created
            // until an effective date is provided.");
        }
        return false;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    
        public String validate() {
            if (validateRequest()) {
                DisplayUtil.displayInfo("Delegation Request is valid and ready to submit.");
            }
            return "";
        }
        
        private boolean validateRequest() {
            //all text fields, except Address2 and effective date must be provided
            boolean response=true;
            if (delegationRequest.getReceivedDate()==null) {
                DisplayUtil.displayWarning("The date the request was received is required prior to saving.");
                response = false;
            } 
            if (delegationRequest.getAssigFirstName() == null 
            		|| delegationRequest.getAssigLastName() == null 
            		|| delegationRequest.getAssigTitle() == null 
            		|| delegationRequest.getAssigAddress1() == null 
            		|| delegationRequest.getAssigCity() == null 
            		|| delegationRequest.getAssigStateCd() == null 
            		|| delegationRequest.getAssigZip() == null) {
                DisplayUtil.displayWarning("The name and address of the person being delegated is required for submission.");
                response = false;
            }
            
            if (delegationRequest.getOrigFirstName() == null 
            		|| delegationRequest.getOrigLastName() == null 
            		|| delegationRequest.getOrigTitle() == null 
            		|| delegationRequest.getOrigAddress1() == null 
            		|| delegationRequest.getOrigCity() == null 
            		|| delegationRequest.getOrigStateCd() == null 
            		|| delegationRequest.getOrigZip() == null) {
                DisplayUtil.displayWarning("The name and address of the person requesting delegation is required for submission.");
                response = false;
            }

           /* if (delegationRequest.getEffectiveDate()== null && delegationRequest.getSubmittedDate() != null) {
                DisplayUtil.displayInfo("Reminder: Issuances may not be created until an effective date is provided.");
            }*/ //Mantis #2175
            
            return response;
        }
        
        public DelegationRequest getDelegationRequest() {
            return delegationRequest;
        }

        public void setDelegationRequest(DelegationRequest delegationRequest) {
            this.delegationRequest = delegationRequest;

            // bug 2179 shoule load gi and set allow to false.
            //logger.debug("trying to initalize gi bean");
            GeneralIssuance gi = (GeneralIssuance) FacesUtil
            .getManagedBean("generalIssuance");
            gi.setIssuanceTypes(GenericIssuanceTypeDef.getTypes(delegationRequest,null));
            gi.loadIssuances(delegationRequest);

            gi.setIssuanceAllow(false);
            if (isIncludeIssuance()) {  //is request submitted?
                if (gi != null) { 
                    /*
                     * The following depends on type of facility type and approval status
                     */
                    // if it is approved, don't show issuance button.
                    if (getDelegationRequest().getRequestTypeCd().equals(DelegationDispositionDef.DELEGATION_DISPOSITION_DENY)) {
                        //State #1 from Erica's diagram
                        //logger.debug("Denied!");
                        gi.setIssuanceAllow(true);
                    } else if (!getDelegationRequest().isTitleV()) {
                        //State #2
                        //logger.debug("Approved - NTV!");
                        gi.setIssuanceAllow(true);
                    } else if (getDelegationRequest().isTitleV() && !getDelegationRequest().isFac25MilOr250EmpQualified()) {
                        //State #3
                        //logger.debug("Approved - TV!");
                        gi.setIssuanceAllow(true);
                    } else if (getDelegationRequest().isTitleV() && getDelegationRequest().isFac25MilOr250EmpQualified()){
                        //logger.debug("Approved - Director!");
                        gi.setIssuanceAllow(true);
                    } else {
                        gi.setIssuanceAllow(false);
                    }
                } else {
                    logger.error("Unable to get reference to issuance bean");
                }
                gi.setBean(this);
            } else {
                logger.error("Doesn't think request submitted");
            }
            initializeAttachmentBean();
        }
        

        public void setDelegationRequestID(int id) {
            /*
             * retrieve delegation request with provided ID and set
             */
            try {
                setDelegationRequest(getDelegationRequestService().retrieveDelegationRequest(id));
                initializeAttachmentBean();
            } catch (RemoteException exception) {
                handleException(exception);
            }
        }
        
        public DefSelectItems getDispositionDef() {
            return DelegationDispositionDef.getData().getItems();

         }
        
        public final String startEditRequest(ActionEvent actionEvent) {
            setEditable(true);
            initializeAttachmentBean();
            return "dialog:editRelocation";
        }
        
        public final String startAddRequest() {
            setEditable(true);
           
            delegationRequest = new DelegationRequest();
            delegationRequest.setFacility(facility);
            //delegationRequest.setUserId(InfrastructureDefs.getCurrentUserId());
            delegationRequest.setNewRecord(true);
            delegationRequest.setApplicationTypeCD("");
            return "dialog:editDelegation";
        }
        
        /*
         * This shouldn't be needed for DOR since there's just one type of DOR
         */
        /*
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
        */
        
        public final List<SelectItem>  getActivePermits() {
            DefData data = new DefData();
            /*
             * TO-DO needed for DOR??????
             */
            /*
            try {
                Permit x[] = getDelegationRequestService().getPermits(getFacility().getFacilityId());
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
               try {
                save();
                ApplicationSearch asc = (ApplicationSearch) FacesUtil.getManagedBean("applicationSearch");
                asc.setPopupRedirectOutcome("applicationDetail");
                FacesUtil.returnFromDialogAndRefresh();
            } catch (RemoteException e) {
                handleException(e);
            }
        }
        
        public String createNewApplication() {
            String ret = null;
            try {
                ret = save();
            } catch (RemoteException e) {
                handleException(e);
            }
            return ret;
        }
        
        public String save() throws RemoteException{
                if (delegationRequest.isNewRecord()) {
                    //logger.debug("new record...");
                    delegationRequest=getDelegationRequestService().createDelegationRequest(delegationRequest);
                    delegationRequest.setNewRecord(false);
                } else {
                    //logger.debug("updating record");
                    delegationRequest=getDelegationRequestService().modifyDelegationRequest(delegationRequest);
                }

                //setEditMode(false);
                setEditable(false);
                initializeAttachmentBean();
                refresh();
            return null;    //return to the same page
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
            ApplicationSearch asc = (ApplicationSearch) FacesUtil
            .getManagedBean("applicationSearch");
            //logger.debug("Beginning search");
            asc.search();
            //logger.debug("search done");
            ApplicationDetail applicationDetail = (ApplicationDetail) FacesUtil
            .getManagedBean("applicationDetail");
            applicationDetail.setApplicationID(getDelegationRequest().getApplicationID());
            applicationDetail.reload();
            delegationRequest = (DelegationRequest)applicationDetail.getApplication();
            //delegationRequest = new DelegationRequest();
        }
        
        public final void submit(ActionEvent actionEvent) {
            setEditable(false);
            initializeAttachmentBean();
            //setEditMode(false);
            try {
                if (validateRequest()) {
                    //if valid update and submit to workflow

                    if (getDelegationRequestService().submit(delegationRequest)) {
                       // delegationRequest.setSubmittedDate(new Timestamp(System.currentTimeMillis()));
                       // save();
                        DisplayUtil.displayInfo("Record submitted");
                       // getDelegationRequests();  //refresh our list
                    } else {
                        DisplayUtil.displayInfo("Error submitting record. Unable to generate workflow Task.");
                    }

                } else {
                    DisplayUtil.displayError("Record is not valid.  Please provide all required fields and re-submit");
                }
                refresh();
             //   FacesUtil.returnFromDialogAndRefresh();
            } catch (RemoteException e) {
                handleException(e);
            }
        }
        
        public boolean isAdmin() {
           return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin(); 
        }
        
        public String deleteApplication() {
        try {
            // only delete if the record is not yet submitted
            setEditable(false);
            if (delegationRequest.getSubmittedDate() == null) {
                //logger.debug("Deleting application");
                getDelegationRequestService().deleteDelegationRequest(delegationRequest);
                DisplayUtil.displayInfo("Application deleted");
                // refresh();
                return "applicationSearch";
            }
            DisplayUtil.displayError("Request has already been submitted and may not be deleted");
            return "";
        } catch (RemoteException e) {
            // put error message up to user
            handleException(e);
            return "";
        }
    }

        
        /**
         * Cancel changes to application
         * 
         * @return
         */
        public final String undoApplication() {
            return null;
        }
    
        
        public final void cancelEditRequest() {
            //  facilityRUM = null;
            //  rumModify = false;
            //  logger.debug("Cancelling Edit");
              setEditable(false);
              initializeAttachmentBean();
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
          

          public void initializeAttachmentBean() {
              
              if (delegationRequest.getApplicationNumber() != null && delegationRequest.getApplicationNumber().length()>0) {
                  /* STEP 1
                   * Get a reference to the Attachment backing bean
                   */
                  
                  Attachments a = (Attachments) FacesUtil
                  .getManagedBean("attachments");
                  a.addAttachmentListener(this);
                  a.setStaging(false);    //this feature only supported internally (no portal version)
                  
                  //default evertyhing to uneditable
                  a.setNewPermitted(false);
                  a.setUpdatePermitted(false);
                  
                  if (getDelegationRequest().getSubmittedDate()==null) {   
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
                  a.setObjectId(Integer.toString(this.getDelegationRequest().getApplicationID()));
                  a.setFacilityId(getDelegationRequest().getFacilityId());
                 // a.setDocument(d);
    
                  /* STEP 3
                   * Set the picklist in the backing bean for the document types
                   */
                  a.setSubtitle(null);
                  a.setAttachmentTypesDef(null);            
                  a.setHasDocType(false);
                  a.setAttachmentList(this.delegationRequest.getAttachments());
              }
              
          }
          
        public final void initialize() {
            //logger.debug("Initialize facility in delegation");
            FacilityProfile fp = (FacilityProfile) FacesUtil
            .getManagedBean("facilityProfile");
            setFacility(fp.getFacility());  
        }
        
        public final void dialogDone() {
            return;
        }
        
        public DelegationRequest[] getDelegationRequests() {
            initialize();
            //logger.debug("return array of delegation requests for: "+getFacility().getFacilityId());
            /*
             * Assumes we already have a valid reference to the facility object
             */
          
            delegationRequests = new DelegationRequest[0];
            
            try {
                delegationRequests =  getDelegationRequestService().retrieveDelegationRequests(facility.getFacilityId());
            } catch (RemoteException re) {
                handleException(re);
                delegationRequests = new DelegationRequest[0];
            }
            return delegationRequests;
        }
        
        public AttachmentEvent createAttachment(Attachments attachment)
        throws AttachmentException {
            //go create our record in the database
            try {
                getDelegationRequestService().createDelegationAttachment(delegationRequest,attachment.getTempDoc(),attachment.getFileToUpload().getInputStream());
                delegationRequest = getDelegationRequestService().retrieveDelegationRequest(delegationRequest.getApplicationID());  
            } catch (RemoteException e) {
                handleException(e);
            } catch (IOException e) {
                handleException(new RemoteException(e.getMessage(), e));
            }
            //setEditMode(true);
            FacesUtil.returnFromDialogAndRefresh();
            return null;
        }

        public AttachmentEvent deleteAttachment(Attachments attachment) {
            try {
                getDelegationRequestService().deleteDelegationAttachment(delegationRequest, attachment.getTempDoc());
                delegationRequest = getDelegationRequestService().retrieveDelegationRequest(delegationRequest.getApplicationID());
            } catch (RemoteException e) {
                handleException(e);
            }
            //setEditMode(false);
            FacesUtil.returnFromDialogAndRefresh();
            return null;
        }
        
        public AttachmentEvent updateAttachment(Attachments attachment) {
            try {
                getDelegationRequestService().modifyDelegationAttachment(delegationRequest,attachment.getTempDoc());
                delegationRequest = getDelegationRequestService().retrieveDelegationRequest(delegationRequest.getApplicationID());
            } catch (RemoteException e) {
                handleException(e);
            }
            //setEditMode(false);
            FacesUtil.returnFromDialogAndRefresh();
            return null;
        }
        
        public void cancelAttachment() {
            popupRedirectOutcome=null;
            FacesUtil.returnFromDialogAndRefresh();
        }

}
