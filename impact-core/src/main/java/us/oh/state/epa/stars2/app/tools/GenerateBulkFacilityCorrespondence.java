package us.oh.state.epa.stars2.app.tools;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import oracle.adf.view.faces.model.UploadedFile;

import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage.Severity;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRole;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.document.AttachmentEvent;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentListener;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.def.CorrespondenceAttachmentTypeDef;
import us.oh.state.epa.stars2.def.CorrespondenceDirectionDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.framework.util.Utility;

public class GenerateBulkFacilityCorrespondence extends BulkOperation  {
	private FacilityList[] facilities;
	private Correspondence correspondence;
	private UploadedFile fileToUpload;
	private UploadedFileInfo uploadedFileInfo;
	private String attachmentDescription;
	private String attachmentTypeCd;
	
	private static boolean operationInProgress;

    public GenerateBulkFacilityCorrespondence() {
        super();
        setButtonName("Generate Correspondence");
        setNavigation("dialog:bulkCorrespondence");
    }

    /**
     * Each bulk operation is responsible for providing a search operation based
     * on the paramters gathered by the BulkOperationsCatalog bean. The search
     * is run when the "Select" button on the Bulk Operations screen is clicked.
     * The BulkOperationsCatalog will then display the results of the search.
     * Below the results is a "Bulk Operation" button.
     */
    public final void searchFacilities(BulkOperationsCatalog lcatalog)
        throws RemoteException {
    	
    	if(isOperationInProgress()) {
    		DisplayUtil.displayError("An another instance of bulk operation is already in progress." +
    				" Please wait for the operation to finish.");
    		return;
    	}

        correspondence = new Correspondence();
        
    	this.catalog = lcatalog;

		facilities = getFacilityService().searchFacilities(
				catalog.getFacilityNm(), catalog.getFacilityId(),
				catalog.getCompanyName(), null, catalog.getCounty(),
				catalog.getOperatingStatusCd(), catalog.getDoLaa(),
				catalog.getNaicsCd(), catalog.getPermitClassCd(),
				catalog.getTvPermitStatus(), null, null, null,
				null, null, true, catalog.getFacilityTypeCd());

        ArrayList<FacilityList> tfs = new ArrayList<FacilityList>();
        setMaximum(facilities.length);
        setValue(0);
        
        int i = 0;
        setSearchStarted(true);
        
        for (FacilityList facility : facilities) {
        	if (catalog.getReportCategoryCd() != null) {
        		if (!catalog.getReportCategoryCd().equals(facility.getReportingTypeCd())) {
        			continue;
        		}
         	}
    		tfs.add(facility);
        	setValue(++i);
        }
        facilities = tfs.toArray(new FacilityList[0]);

        setValue(facilities.length);

        
        catalog.setFacilities(facilities);
        setSearchCompleted(true);
    }

    /**
     * When the "Bulk Operation" button is clicked, the BulkOperationsCatalog
     * class will call this method, ensure that the BulkOperation class is
     * placed into the jsf context, and return the value of the getNavigation()
     * method to the jsf controller. Any further requests from the page that is
     * navigated to are handled by this bean in the normal way. The default
     * version does no preliminary work.
     * @throws RemoteException 
     */
    public final void performPreliminaryWork(BulkOperationsCatalog lcatalog) throws RemoteException {
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
    public final String performPostWork(BulkOperationsCatalog lcatalog) {
    	
    	if(isOperationInProgress()) {
    		DisplayUtil.displayError("An another instance of bulk operation is already in progress." +
    				" Please wait for the operation to finish.");
    		return ERROR;
    	}
    	
    	boolean ok = true;
    	FacilityList[] selectedFacilities = lcatalog.getSelectedFacilities();
    	Attachment attachment = null;

    	List<ValidationMessage> valMessages = null;

    	valMessages = validateCorrespondence(correspondence);
    	if(valMessages.size() > 0) {
    		displayValidationMessages(valMessages);
    		return ERROR;
    	}

    	setOperationInProgress(true);
    	
    	if(null != uploadedFileInfo) {
    		// create an attachment
    		attachment = new Attachment();
    		attachment.setObjectId(null);
    		attachment.setTemporary(false);
    		attachment.setSubPath("Correspondence");
    		attachment.setLastModifiedBy(InfrastructureDefs.getCurrentUserId());
    		attachment.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
    		attachment.setUploadDate(attachment.getLastModifiedTS());
    		attachment.setExtension(DocumentUtil.getFileExtension(uploadedFileInfo.getFileName()));
    		attachment.setDocTypeCd(attachmentTypeCd);
    		attachment.setDescription(attachmentDescription);
    		
    		if (!DocumentUtil.isValidFileExtension(uploadedFileInfo.getFileName())){
        		DisplayUtil.displayError(DocumentUtil.invalidFileExtensionMessage(null));
        		return ERROR;
        	}

    	}

    	for (FacilityList facility : selectedFacilities) {
    		try {
    			// set the facility id and district to the current facility in the list
    			correspondence.setFacilityID(facility.getFacilityId());
    			correspondence.setDistrict(facility.getDoLaaCd());

    			if(null != uploadedFileInfo) {
    				attachment.setFacilityID(facility.getFacilityId());
    				attachment.setDocumentID(null);
    				getCorrespondenceService().createCorrespondenceWithAttachment(correspondence, attachment, 
    						uploadedFileInfo.getInputStream());
    			}else {
    				getCorrespondenceService().createCorrespondence(correspondence);
    			}
    		}
    		catch(RemoteException re) {
    			ok = false;
    			DisplayUtil.displayError("Create new correspondence failed for facility " + facility.getFacilityId());
    			logger.error(re.getMessage(), re);
    		}catch(IOException ioe) {
    			ok = false;
    			DisplayUtil.displayError("An I/O error occured during creation of correspondence for facility " + 
    										facility.getFacilityId());
    			logger.error(ioe.getMessage(), ioe);
    		}
    	}

    	setOperationInProgress(false);
    	deleteUploadedFile();
    	
    	if (ok) {
    		DisplayUtil.displayInfo("Operation completed successfully.");
    	}

    	return SUCCESS;
    }

	public Correspondence getCorrespondence() {
		return correspondence;
	}

	public void setCorrespondence(Correspondence correspondence) {
		this.correspondence = correspondence;
	}

	public UploadedFile getFileToUpload() {
		return fileToUpload;
	}

	public void setFileToUpload(UploadedFile fileToUpload) {
		this.fileToUpload = fileToUpload;
		// save the uploaded file info so it is not lost
    	if(null == uploadedFileInfo && null != fileToUpload) {
    		uploadedFileInfo = new UploadedFileInfo(fileToUpload);
    	}
	}
	
	public UploadedFileInfo getUploadedFileInfo() {
		return uploadedFileInfo;
	}

	public void setUploadedFileInfo(UploadedFileInfo uploadedFileInfo) {
		this.uploadedFileInfo = uploadedFileInfo;
	}

	public String getAttachmentDescription() {
		return attachmentDescription;
	}

	public void setAttachmentDescription(String attachmentDescription) {
		this.attachmentDescription = attachmentDescription;
	}

	public String getAttachmentTypeCd() {
		return attachmentTypeCd;
	}

	public void setAttachmentTypeCd(String attachmentTypeCd) {
		this.attachmentTypeCd = attachmentTypeCd;
	}
	
	public boolean isOperationInProgress() {
		return operationInProgress;
	}

	public void setOperationInProgress(boolean opInProgress) {
		operationInProgress = opInProgress;
	}
	
	public DefSelectItems getAttachmentTypesDef() {
		return CorrespondenceAttachmentTypeDef.getData().getItems();
	}
	
	public List<ValidationMessage> validateCorrespondence(Correspondence correspondence) {

		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		
		if(Utility.isNullOrEmpty(correspondence.getDirectionCd())) {
			valMessages.add(new ValidationMessage("directionCd", "Direction must be set",
													ValidationMessage.Severity.ERROR, "correspondenceDirection"));
		}else if(correspondence.getDirectionCd().equalsIgnoreCase(CorrespondenceDirectionDef.INCOMING)) {
			if(null == correspondence.getReceiptDate()) {
				valMessages.add(new ValidationMessage("receiptDate", "Receipt Date must be set",
						ValidationMessage.Severity.ERROR, "correspondenceReceiptDate"));
			}
			if(Utility.isNullOrEmpty(correspondence.getCorrespondenceCategoryCd())) {
				valMessages.add(new ValidationMessage("correspondenceCategoryCd", "Category must be set",
						ValidationMessage.Severity.ERROR, "correspondenceCategory"));
			}
		}else if(correspondence.getDirectionCd().equalsIgnoreCase(CorrespondenceDirectionDef.OUTGOING)) {
			if(null == correspondence.getDateGenerated()) {
				valMessages.add(new ValidationMessage("dateGenerated", "Date Generated must be set",
						ValidationMessage.Severity.ERROR, "correspondenceGeneratedDate"));
			}
		}
		
		if(Utility.isNullOrEmpty(correspondence.getCorrespondenceTypeCode())) {
			valMessages.add(new ValidationMessage("correspondenceTypeCode", "Type must be set",
					ValidationMessage.Severity.ERROR, "correspondenceType"));
		}
			
		if(null != fileToUpload) {
			if(Utility.isNullOrEmpty(attachmentDescription)) {
				valMessages.add(new ValidationMessage("attachmentDescription", "Description must be set",
						ValidationMessage.Severity.ERROR, "attachmentDescription"));
			}
			if(Utility.isNullOrEmpty(attachmentTypeCd)) {
				valMessages.add(new ValidationMessage("attachmentTypeCd", "Attachment Type must be set",
						ValidationMessage.Severity.ERROR, "attachmentTypeCd"));
			}
		}
		
		if(!Utility.isNullOrEmpty(attachmentDescription)) {
			if(null == fileToUpload) {
				valMessages.add(new ValidationMessage("fileToUpload", "File must must be specified for upload",
						ValidationMessage.Severity.ERROR, "attachment"));
			}
			if(Utility.isNullOrEmpty(attachmentTypeCd)) {
				valMessages.add(new ValidationMessage("attachmentTypeCd", "Attachment Type must be set",
						ValidationMessage.Severity.ERROR, "attachmentTypeCd"));
			}
		}
		
		if(!Utility.isNullOrEmpty(attachmentTypeCd)) {
			if(null == fileToUpload) {
				valMessages.add(new ValidationMessage("fileToUpload", "File must must be specified for upload",
						ValidationMessage.Severity.ERROR, "attachment"));
			}
			if(Utility.isNullOrEmpty(attachmentDescription)) {
				valMessages.add(new ValidationMessage("attachmentDescription", "Description must be set",
						ValidationMessage.Severity.ERROR, "attachmentDescription"));
			}
		}
		
		if(correspondence.isFollowUpAction()) {
			if(Utility.isNullOrEmpty(correspondence.getFollowUpActionDescription())) {
				valMessages.add(new ValidationMessage("followUpActionDescription", "Follow-up Action Description must be set",
						ValidationMessage.Severity.ERROR, "followUpActionDescription"));
			}
			if(null == correspondence.getFollowUpActionDate()) {
				valMessages.add(new ValidationMessage("followUpActionDate", "Follow-up Action Date must be set",
						ValidationMessage.Severity.ERROR, "followUpActionDate"));
				
			}
		}
		
		return valMessages;
	}
	
	public void displayValidationMessages(List<ValidationMessage> valMessages) {
		for(ValidationMessage valMsg : valMessages) {
			if(valMsg.getSeverity() == ValidationMessage.Severity.ERROR) {
				DisplayUtil.displayError(valMsg.getMessage(), valMsg.getReferenceID());
			}
			if(valMsg.getSeverity() == ValidationMessage.Severity.WARNING) {
				DisplayUtil.displayWarning(valMsg.getMessage(), valMsg.getReferenceID());
			}
			if(valMsg.getSeverity() == ValidationMessage.Severity.INFO) {
				DisplayUtil.displayInfo(valMsg.getMessage(), valMsg.getReferenceID());
			}
		}
	}
	
	public void deleteUploadedFile() {
		// don't cleanup if the bulk operation is running
		// i.e., user clicked cancel after starting the bulk operation
		if(!isOperationInProgress()) {
			fileToUpload = null;
			if(null != uploadedFileInfo) {
				uploadedFileInfo.cleanup();
				uploadedFileInfo = null;
			}
		} else {
			DisplayUtil.displayInfo("Please note that the bulk operation is still in progress.");
		}
	}
}
