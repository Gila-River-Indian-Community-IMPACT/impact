package us.oh.state.epa.stars2.portal.relocation;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.RelocateRequestService;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocationAddtlAddr;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.def.RelocationAttachmentTypeDef;
import us.oh.state.epa.stars2.portal.application.ApplicationDetail;
import us.oh.state.epa.stars2.portal.bean.SubmitTask;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.document.AttachmentEvent;
import us.oh.state.epa.stars2.webcommon.document.AttachmentException;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentListener;


@SuppressWarnings("serial")
public class Relocation extends ValidationBase implements IAttachmentListener {
	private RelocateRequest relocateRequest;
	private boolean editable;
	private boolean admin = false;
	private Facility facility;
	private boolean staging = false;
	private MyTasks myTasks;
	private Task task;
    private SimpleDef[] preApprovedAddressList = null;
    private String preApprovedAddressCd = null;
	
	private RelocateRequest portalRelReq;
	private RelocateRequest newPortalRelReq;
	private List<RelocationAddtlAddr> allAddresses;
	private RelocateRequestService relocateRequestService;

	public RelocateRequestService getRelocateRequestService() {
		return relocateRequestService;
	}

	public void setRelocateRequestService(
			RelocateRequestService relocateRequestService) {
		this.relocateRequestService = relocateRequestService;
	}
	
	public final Class<? extends RelocateRequest> getNewApplicationClass() {
        return portalRelReq == null ? null : portalRelReq.getClass();
    }
	
	private MyTasks getMyTasks() {
    	if (myTasks == null) {
    		myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
    	}
    	return myTasks;
    }
	
	public final void ResetNewPortalRelReq() {
		newPortalRelReq = new RelocateRequest();
		editable = false;
        preApprovedAddressList = null;
        preApprovedAddressCd = null;
	}
	
	public final void createNewITR(ActionEvent event) {
	    if(!firstButtonClick()) { // protect from multiple clicks
	        return;
	    }
	    try {
	        if (newPortalRelReq.getApplicationTypeCD().equals("")) {
	            DisplayUtil.displayWarning("Please select a request type");
	            return;
	        }

	        try {
	            MyTasks myTasks1 = getMyTasks();
	            RelocateRequest recReq1 = myTasks1.createNewITR(newPortalRelReq);
	            if (recReq1 != null) {
	                setPortalRelReq(recReq1);
	                myTasks1.setPageRedirect("relocateDetail");
	            }
	            FacesUtil.returnFromDialogAndRefresh();
	        } catch (RemoteException re) {
	            handleException(re);
	        }
	    } finally {
	        clearButtonClicked();
	    }
	}
	
	public final void cancelCreateNewITR(ActionEvent event) {
		ResetNewPortalRelReq();
		FacesUtil.returnFromDialogAndRefresh();
	}
	
	public void setPortalRelReq(Integer reqId) {
		try {
			setPortalRelReq(getRelocateRequestService().retrieveRelocateRequest(reqId, facility.getFacilityId()));
		} catch (RemoteException re) {
			DisplayUtil.displayError("Retrieve Relocate Request failed");
		}
		
	}

	public final String startEditRequest() {
		editable = true;
        initializeAttachmentBean(relocateRequest, staging);
		return "relocateDetail";
	}
	
	public final String submitRequest() {
		try {
			setPortalRelReq(getRelocateRequestService().retrieveRelocateRequest(portalRelReq.getRequestId(), facility.getFacilityId()));
			portalRelReq.setSubmittedDate(new Timestamp(System.currentTimeMillis()));
			task.setRelocateRequest(portalRelReq);
			task.setFacility(facility);
			SubmitTask submitTask = (SubmitTask) FacesUtil.getManagedBean("submitTask");
        	submitTask.setTitle("Submit Intent To Relocate Request");
        	submitTask.setDapcAttestationRequired(true);
            submitTask.setNonROSubmission(!myTasks.isHasSubmit());
        	return submitTask.confirm();
		} catch (RemoteException re) {
			handleException(re);
		}
		DisplayUtil.displayError("Submit intent to relocate request failed");
		return "relocateDetail";
	}
	
	public final String cancelEditRequest() {
		try {
			setPortalRelReq(getRelocateRequestService().retrieveRelocateRequest(portalRelReq.getRequestId(), facility.getFacilityId()));
		} catch (RemoteException re) {
			handleException(re);
		}
		editable = false;
        initializeAttachmentBean(relocateRequest, staging);
		return "relocateDetail";
	}
	
	public final String saveEditRequest() {
		try {
			Facility itrFac= new Facility();
			itrFac.setFacilityId(facility.getFacilityId());
			itrFac.setFpId(facility.getFpId());
			portalRelReq.setFacility(itrFac);
			if (allAddresses != null) {
				portalRelReq.setAllAddresses(allAddresses.toArray(new RelocationAddtlAddr[0]));
			} else {
				portalRelReq.setAllAddresses(null);
			}
			getRelocateRequestService().modifyRelocateRequest(portalRelReq);
			setPortalRelReq(getRelocateRequestService().retrieveRelocateRequest(portalRelReq.getRequestId(), facility.getFacilityId()));
		} catch (RemoteException re) {
			handleException(re);
		}
		editable = false;
        initializeAttachmentBean(relocateRequest, staging);
		return "relocateDetail";
	}
	
	public void initializeAttachmentBean(RelocateRequest relocateRequest, boolean stage) {        
		if (relocateRequest.getApplicationNumber() != null && relocateRequest.getApplicationNumber().length()>0) {
			/* STEP 1
			 * Get a reference to the Attachment backing bean
			 */
        
			Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
			a.addAttachmentListener(this);
			a.setStaging(stage);
			if (stage) {
				a.setNewPermitted(!editable);
				a.setUpdatePermitted(!editable);
			} else {
				a.setNewPermitted(false);
				a.setUpdatePermitted(false);
			}
        
			/* STEP 2
			 * Create a new, empty Document object, set its FacilityID and 
			 * give the attachment backing bean a reference to it. 
			 */
			
			a.setSubPath("Applications");
			a.setObjectId(Integer.toString(relocateRequest.getApplicationID()));
			a.setFacilityId(getMyTasks().getFacility().getFacilityId());

			/* STEP 3
			 * Set the picklist in the backing bean for the document types
			 */
			a.setSubtitle(null);
			a.setAttachmentTypesDef(RelocationAttachmentTypeDef.getData().getItems());            
			a.setHasDocType(true);
			
			Attachment attachments[] = relocateRequest.getAttachments();
			a.setAttachmentList(attachments);
        }       
    }
	
	public void cancelAttachment() {
        FacesUtil.returnFromDialogAndRefresh();
    }

    public AttachmentEvent createAttachment(Attachments attachment)
            throws AttachmentException {
        //go create our record in the database
        try {
        	getRelocateRequestService().createRelocationAttachment(relocateRequest,attachment.getTempDoc(),attachment.getFileToUpload().getInputStream());
        	setPortalRelReq(getRelocateRequestService().retrieveRelocateRequest(portalRelReq.getRequestId(), facility.getFacilityId()));  
        } catch (Exception e) {
        	DisplayUtil.displayError("Error create attachment record.");
        	logger.error(e.getMessage(), e);
        }
        FacesUtil.returnFromDialogAndRefresh();
        return null;
    }

    public AttachmentEvent deleteAttachment(Attachments attachment) {
        try {
        	getRelocateRequestService().deleteRelocationAttachment(relocateRequest, attachment.getTempDoc());
        	setPortalRelReq(getRelocateRequestService().retrieveRelocateRequest(portalRelReq.getRequestId(), facility.getFacilityId()));
        } catch (Exception e) {
            DisplayUtil.displayError("Error while deleting attachment.");
            logger.error(e.getMessage(), e);
        }
        setEditMode(false);
        FacesUtil.returnFromDialogAndRefresh();
        return null;
    }

    public AttachmentEvent updateAttachment(Attachments attachment) {
        try {
        	getRelocateRequestService().modifyRelocationAttachment(relocateRequest,attachment.getTempDoc());
        	setPortalRelReq(getRelocateRequestService().retrieveRelocateRequest(portalRelReq.getRequestId(), facility.getFacilityId()));
        } catch (Exception e) {
            DisplayUtil.displayError("Error while updating attachment.");
            logger.error(e.getMessage(), e);
        }
        setEditMode(false);
        FacesUtil.returnFromDialogAndRefresh();
        return null;
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
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public RelocateRequest getRelocateRequest() {
		return relocateRequest;
	}

	public void setRelocateRequest(RelocateRequest relocateRequest) {
        preApprovedAddressList = null;
        preApprovedAddressCd = null;
		this.relocateRequest = relocateRequest;
		initializeAttachmentBean(relocateRequest, staging);
	}

	public boolean isStaging() {
		return staging;
	}

	public void setStaging(boolean staging) {
		this.staging = staging;
	}

	public RelocateRequest getPortalRelReq() {
		return portalRelReq;
	}

	public void setPortalRelReq(RelocateRequest portalRelReq) {
        preApprovedAddressList = null;
        preApprovedAddressCd = null;
        allAddresses = null;
		this.portalRelReq = portalRelReq;
		if (portalRelReq.getApplicationID() != null) {
			ApplicationDetail appDetail = (ApplicationDetail)FacesUtil.getManagedBean("applicationDetail");
			portalRelReq.setFacility(facility);
			appDetail.loadApplication(portalRelReq);
		}
	}

	public RelocateRequest getNewPortalRelReq() {
		return newPortalRelReq;
	}

	public void setNewPortalRelReq(RelocateRequest newPortalRelReq) {
		this.newPortalRelReq = newPortalRelReq;
        preApprovedAddressList = null;
        preApprovedAddressCd = null;
	}
	
	public boolean isAttachmentUpdatePermitted(Attachments attachment) {
        return staging;
    }
	
	public boolean  isAttachmentDeletePermitted(Attachments attachment) {
		return staging;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public boolean isAdmin() {
		return admin;
	}
	
    public final boolean getSubmitAllowed() {
        return !isEditable();
    }
    
    public final List<SelectItem> getPreApprovedAddressList() {
        List<SelectItem> itemList = new ArrayList<SelectItem>();
        initPreApprovedAddressList();
        if (preApprovedAddressList != null) {
	        for (SimpleDef preApprovedAddress : preApprovedAddressList) {
	            itemList.add(new SelectItem(preApprovedAddress.getCode(), 
	                    preApprovedAddress.getDescription()));
	        }
        }
        
        return itemList;
    }
    
    private void initPreApprovedAddressList() {
        if (preApprovedAddressList == null && relocateRequest != null && relocateRequest.getFacilityId() != null) {
            try {
                preApprovedAddressList = getRelocateRequestService().retrievePreApprovedAddressesForFacility(relocateRequest.getFacilityId());
            } catch (RemoteException e) {
            	handleException(e);
            }
        }
    }

    public final String getPreApprovedAddressCd() {
    	initPreApprovedAddressList();
        if (preApprovedAddressCd == null && preApprovedAddressList != null ) {
            // make sure address id matches id for specified address
            for (SimpleDef preAppovedAddress : preApprovedAddressList) {
                if (preAppovedAddress.getDescription().equals(relocateRequest.getFutureAddress())) {
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
                portalRelReq.setFutureAddress(preAppovedAddress.getDescription());
                // reset lists of all addresses so new address will be picked up
                portalRelReq.setAllAddresses(null);
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
    public final boolean isReadOnly() {
        return !isEditable() || (relocateRequest != null && relocateRequest.getSubmittedDate()!=null);
    }
    
    public final String getAttestationDocURL() {
        String url = null;
        Attachment doc;
		try {
			relocateRequest.setFacility(facility);
			doc = getRelocateRequestService().createITRAttestationDocument(relocateRequest);
	        if (doc != null) {
	            url = doc.getDocURL();
	        } else {
	            // this should never happen
	            logger.error("Error creating attestation document for ITR: " + relocateRequest.getApplicationNumber());
	        }
		} catch (RemoteException e) {
			handleException(e);
		}
        return url; // stay on same page
    }
    
    public final List<RelocationAddtlAddr> getAllAddresses() {
    	if (allAddresses == null && portalRelReq != null) {
    		allAddresses = new ArrayList<RelocationAddtlAddr>();
    		for (RelocationAddtlAddr addr : portalRelReq.getAllAddresses()) {
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
    	if (portalRelReq != null) {
    		addr.setRequestId(portalRelReq.getRequestId());
    	}
    	return addr;
    }

}
