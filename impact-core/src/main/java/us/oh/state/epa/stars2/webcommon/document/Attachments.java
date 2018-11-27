package us.oh.state.epa.stars2.webcommon.document;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;

import javax.faces.event.ActionEvent;

import oracle.adf.view.faces.context.AdfFacesContext;
import oracle.adf.view.faces.model.UploadedFile;
import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.DocumentService;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReport;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.def.ComplianceAttachmentTypeDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EmissionsAttachmentTypeDef;
import us.oh.state.epa.stars2.def.StAttachmentTypeDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.OpenDocumentUtil;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

/**
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2006 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @author cmeier
 *
 */

public class Attachments extends ValidationBase {
	
	private static final long serialVersionUID = 4634933739326396715L;

	private Attachment tempDoc;
    private UploadedFile fileToUpload;
    private UploadedFile tsFileToUpload;
    private boolean newPermitted=true;
    private boolean newAttachment=false;
    private boolean updatePermitted=true;
    private boolean deletePermitted=true;
    private boolean tradeSecretSupported;
    private Attachment[] attachments;
    protected Attachment downloadDoc;
    private String facilityId;
    private Facility facility;
    private ComplianceReport complianceReport;
    private DefSelectItems attachmentTypesDef;
    private IAttachmentListener listener;
    private IAttachmentUpdateListener updateListener;
    private String subPath="";
    private String objectId="";
    private boolean hasDocType = true;
    private boolean staging=false;
    private boolean locked = false;
    private String subtitle="";
    private UploadedFileInfo publicAttachmentInfo;
    private UploadedFileInfo tradeSecretAttachmentInfo;
    private String warningMsg;  // displayed in red on attachment popup window when not null
    protected static final String COMPLIANCE_TS_DOC_DOWNLOAD_DIALOG = "dialog:editComplianceConfirmTSDownload";
    protected static final String COMPLIANCE_DOC_MANAGED_BEAN = "report";

    private DocumentService documentService;
    
    public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getSubPath() {
        return subPath;
    }

    public void setSubPath(String subPath) {
        this.subPath = subPath;
    }

    public void addAttachmentListener(IAttachmentListener ilisten) {
        listener = ilisten;
    }
    
    public void removeAttachmentListener(IAttachmentListener ilisten) {
        listener = null;
    }
    
    public void addAttachmentUpdateListener(IAttachmentUpdateListener updateListener) {
        this.updateListener = updateListener;
    }
    
    public void removeAttachmentUpdateListener() {
        updateListener = null;
    }
    
    public final void createAttachment(ActionEvent event) {
        if (listener != null) {
            if(!firstButtonClick()) { // protect from multiple clicks
                return;
            }
            try {
                // putFile(tempDoc);
                tempDoc.setTemporary(false);
                if (tempDoc.getLastModifiedTS() == null) {
                    tempDoc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
                }
                tempDoc.setUploadDate(tempDoc.getLastModifiedTS());
                
    			if (getPublicAttachmentInfo() == null && getFileToUpload() != null) {
    				setPublicAttachmentInfo(new UploadedFileInfo(getFileToUpload()));
    			}
                               
    			if (isTradeSecretAllowed() && getTradeSecretAttachmentInfo() == null && getTsFileToUpload() != null) {
    				setTradeSecretAttachmentInfo(new UploadedFileInfo(getTsFileToUpload()));
    			}
    			boolean validExtension = true;
                if (fileToUpload != null) {
                	tempDoc.setExtension(DocumentUtil.getFileExtension(fileToUpload.getFilename()));
                	if (!DocumentUtil.isValidFileExtension(fileToUpload.getFilename())){
                		DisplayUtil.displayError(DocumentUtil.invalidFileExtensionMessage(null));
                		validExtension = false;
                		publicAttachmentInfo = null;
                	}
                }
                if (this.isTradeSecretAllowed() && tsFileToUpload != null){
                	if (!DocumentUtil.isValidFileExtension(tsFileToUpload.getFilename())){
                		DisplayUtil.displayError(DocumentUtil.invalidFileExtensionMessage("trade secret"));
                		validExtension = false;
                		tradeSecretAttachmentInfo = null;
                	}
                }
                if (!validExtension){
                	return;
                }
                listener.createAttachment(this);
            } catch (AttachmentException ae) {
                /*
                 * listener failed for some reason. So clean up and remove the 
                 * Doc record and the attachment from the file system.
                 */
                logger.error("Unable to create attachment (attempting to put to "+tempDoc.getPath(),ae);
                try {
                    DocumentUtil.removeDocument(tempDoc.getPath());
                } catch (IOException ioe) {
                    logger.error("Unable to undo create attachment (attempting to put to "+tempDoc.getPath(),ioe);
                }
            } finally {
                clearButtonClicked();
            }
        }
    }
    
    public final void deleteAttachment(ActionEvent event) {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            if (listener != null) {
                listener.deleteAttachment(this);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            clearButtonClicked();
        }
    }
    public final void updateAttachment(ActionEvent event) {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
           // getDocumentService().updateDocument(tempDoc);
            if (listener != null) {
                listener.updateAttachment(this);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            clearButtonClicked();
        }
    }
    
    public final void cleanup() {
        // clean up temporary variables
        if (publicAttachmentInfo != null) {
            publicAttachmentInfo.cleanup();
            publicAttachmentInfo = null;
        }
        if (tradeSecretAttachmentInfo != null) {
            tradeSecretAttachmentInfo.cleanup();
            tradeSecretAttachmentInfo = null;
        }
    }
    
    public final void cancelAttachment(ActionEvent event) {
         if (listener != null) {
             listener.cancelAttachment();
         }
         // clean up temporary variables
         cleanup();
         AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }
    
    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public boolean isHasDocType() {
        return hasDocType;
    }

    public Attachments() {
        super();
        warningMsg = null;
    }
    
    public final boolean isUpdatePermitted() {
        if (updateListener != null) {
            updatePermitted = updateListener.isAttachmentUpdatePermitted(this);
        }
        return updatePermitted;
    }

    public final void setUpdatePermitted(boolean editPermitted) {
        this.updatePermitted = editPermitted;
    }

    public final boolean isNewPermitted() {
        return newPermitted;
    }

    public final void setNewPermitted(boolean newPermitted) {
        this.newPermitted = newPermitted;
    }

    public final String startViewDoc() {
       // tempDoc = (Document) FacesUtil.getManagedBean("document");
        return "";
    }

    public final void cancelEditDoc(ActionEvent actionEvent) {
        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }

    public final String startEditAttachment() {
        String ret = null;
        newAttachment=false;
        ret= "dialog:editComplianceAttachment";
        return ret;
    }

    public final String downloadDoc() {
     //   try {
         //   ApplicationDocument doc = (ApplicationDocument) FacesUtil
          //          .getManagedBean(DOC_MANAGED_BEAN);
          //  OpenDocumentUtil.downloadDocument(doc.getPath());
      //  } catch (IOException ioe) {
      //      DisplayUtil.displayError("cannot download document");
       //     logger.error("cannot download document", ioe);
     //   }
        return null; // stay on same page
    }

    public final void closeViewDoc(ActionEvent actionEvent) {
        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }

    public final void removeEditDoc(ActionEvent actionEvent) {
        /*
         * if (selectedTreeNode.getType().equals(APPLICATION_NODE_TYPE)) {
         * application.removeDocument(docBeingModified); } else {
         * selectedEU.getEuDocuments().remove(docBeingModified); }
         */
        FacesUtil.returnFromDialogAndRefresh();
    }

    public final Attachment getTempDoc() {
        return this.tempDoc;
    }


    public final void putFile(Document tempDoc) { // Quick check on 10/3/2018 -- this method is not used 
        /*
         * Call this method *before* adding the appropriate record(s) to 
         * the database.  If the insertion of the records fails then deleteFile 
         * should be called to clean up (roll back).
         */
        if (fileToUpload != null) {
            try {
                tempDoc.setExtension(DocumentUtil.getFileExtension(fileToUpload
                        .getFilename()));
                tempDoc = getDocumentService().uploadDocument(tempDoc,
                        fileToUpload.getInputStream(), staging);
            } catch (IOException ioe) {
                DisplayUtil.displayError("Error: Cannot upload document");
                logger.error(ioe.getMessage(), ioe);
            }
            //reset the variable pointing to the file
            fileToUpload = null;
        } else {
            DisplayUtil.displayWarning("Please select a file to upload");
        }
    }
    
    public final void getFile(Document doc) {
        /*
         * This retrieves the file and provides it to the browser as a binary.
         */
    }

    public Attachment getDocument() {
        return tempDoc;
    }
    
    public void setDocument(Attachment doc) {
        tempDoc = doc;
    }
    
    public final Attachment[] getAttachmentList() {
        return attachments;
    }
    
    public final void setAttachmentList(Attachment attachments[]) {
        this.attachments = attachments;
    }

    public ComplianceReport getComplianceReport() {
        return complianceReport;
    }

    public void setComplianceReport(ComplianceReport complianceReport) {
        this.complianceReport = complianceReport;
    }
    
    public DefSelectItems getAttachmentTypesDef() {
    	return attachmentTypesDef;
    }

    public void setAttachmentTypesDef(DefSelectItems attachmentTypesDef) {
        this.attachmentTypesDef = attachmentTypesDef;
    }

    public UploadedFile getFileToUpload() {
        return fileToUpload;
    }

    public void setFileToUpload(UploadedFile fileToUpload) {
        this.fileToUpload = fileToUpload;
    }
    
    public final String startNewAttachment() {
        tempDoc = new Attachment();
        tempDoc.setSubPath(getSubPath());
        tempDoc.setObjectId(getObjectId());
        tempDoc.setFacilityID(getFacilityId());
        if(attachmentTypesDef != null && attachmentTypesDef.getCurrentItems().size() == 1) {
            tempDoc.setDocTypeCd((String)attachmentTypesDef.getCurrentItems().get(0).getValue());
        }
        if (this.isStaging()) {
            tempDoc.setLastModifiedBy(CommonConst.GATEWAY_USER_ID);
        } else {  
            tempDoc.setLastModifiedBy(InfrastructureDefs.getCurrentUserId());
        }
        newAttachment=true;
        cleanup();
        return "dialog:editComplianceAttachment";
       
    }

    public boolean isDeletePermitted() {
        if (updateListener != null) {
            deletePermitted = updateListener.isAttachmentDeletePermitted(this);
        }
        return deletePermitted;
    }

    public void setDeletePermitted(boolean deletePermitted) {
        this.deletePermitted = deletePermitted;
    }

    public boolean isNewAttachment() {
        return newAttachment;
    }

    public void setNewAttachment(boolean newAttachment) {
        this.newAttachment = newAttachment;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public void setHasDocType(boolean hasDocType) {
        this.hasDocType = hasDocType;
    }

    public boolean isStaging() {
        return staging;
    }

    public void setStaging(boolean staging) {
        this.staging = staging;
    }

    public final boolean isTradeSecretSupported() {
        return tradeSecretSupported;
    }

    public final void setTradeSecretSupported(boolean tradeSecretSupported) {
        this.tradeSecretSupported = tradeSecretSupported;
    }

    public final UploadedFile getTsFileToUpload() {
        return tsFileToUpload;
    }

    public final void setTsFileToUpload(UploadedFile tsFileToUpload) {
        this.tsFileToUpload = tsFileToUpload;
    }

    public final UploadedFileInfo getPublicAttachmentInfo() {
        return publicAttachmentInfo;
    }

    public final void setPublicAttachmentInfo(UploadedFileInfo publicAttachmentInfo) {
        this.publicAttachmentInfo = publicAttachmentInfo;
    }

    public final UploadedFileInfo getTradeSecretAttachmentInfo() {
        return tradeSecretAttachmentInfo;
    }

    public final void setTradeSecretAttachmentInfo(
            UploadedFileInfo tradeSecretAttachmentInfo) {
        this.tradeSecretAttachmentInfo = tradeSecretAttachmentInfo;
    }

    public String getWarningMsg() {
        return warningMsg;
    }

    public void setWarningMsg(String warningMsg) {
        this.warningMsg = warningMsg;
    }

    public boolean isTradeSecretAllowed() {

    	boolean ret = false;

    	if (attachmentTypesDef != null && tempDoc != null) {
    		BaseDef attachmentDef = attachmentTypesDef.getItem(tempDoc.getDocTypeCd());
    		if (attachmentDef != null) {
//    			if (attachmentDef instanceof ComplianceAttachmentCemsTypeDef) {
//    				ComplianceAttachmentCemsTypeDef attachmentCemsTypeDef = (ComplianceAttachmentCemsTypeDef)attachmentDef;
//    				ret = attachmentCemsTypeDef.isTradeSecretAllowed();
//    			} else if (attachmentDef instanceof ComplianceAttachmentOneTypeDef) {
//    				ComplianceAttachmentOneTypeDef attachmentOneTypeDef = (ComplianceAttachmentOneTypeDef)attachmentDef;
//    				ret = attachmentOneTypeDef.isTradeSecretAllowed();
//    			} else if (attachmentDef instanceof ComplianceAttachmentOtherTypeDef) {
//    				ComplianceAttachmentOtherTypeDef attachmentOtherTypeDef = (ComplianceAttachmentOtherTypeDef)attachmentDef;
//    				ret = attachmentOtherTypeDef.isTradeSecretAllowed();
    			if(attachmentDef instanceof ComplianceAttachmentTypeDef) {
    				ComplianceAttachmentTypeDef complianceAttachmentTypeDef = (ComplianceAttachmentTypeDef)attachmentDef;
    				ret = complianceAttachmentTypeDef.isTradeSecretAllowed();
    			} else if (attachmentDef instanceof StAttachmentTypeDef) {
    				StAttachmentTypeDef stAttachmentTypeDef = (StAttachmentTypeDef)attachmentDef;
    				ret = stAttachmentTypeDef.isTradeSecretAllowed();    				
    			} else if (attachmentDef instanceof EmissionsAttachmentTypeDef){
    				EmissionsAttachmentTypeDef emissionsAttachmentTypeDef = (EmissionsAttachmentTypeDef)attachmentDef;
    				ret = emissionsAttachmentTypeDef.isTradeSecretAllowed();
    			}
    			
    		}
    	}

		return ret;
	}
	
    public String getTradeSecretDocURL() {
		String url = null;
		
		if (isPublicApp()) {
			return url;
		}
		// downloadDoc is set in the startDownloadTSDoc method for internal apps
		// but this method is called directly by the portal (bypassing
		// startDownloadTSDoc)
		// so we need to retrieve the value if it is not already set
		if (downloadDoc == null || !isInternalApp()) {
			downloadDoc = (Attachment) FacesUtil
					.getManagedBean(COMPLIANCE_DOC_MANAGED_BEAN);
		}
		if (downloadDoc == null || downloadDoc.getTradeSecretDocId() == null) {
			DisplayUtil.displayError("No Trade Secret document is available.");
		} else {
			url = downloadDoc.getTradeSecretDocURL();			
		}
		return url; // stay on same page
	}
    
    public String getTsConfirmMessage() {
    	return SystemPropertyDef.getSystemPropertyValue("TsConfirmMessage", null);
	}
    
    public String startDownloadTSDoc() {
		downloadDoc = (Attachment) FacesUtil.getManagedBean(COMPLIANCE_DOC_MANAGED_BEAN);
		return COMPLIANCE_TS_DOC_DOWNLOAD_DIALOG;
	}
    
    public String downloadTSDoc() {
		// downloadDoc is set in the startDownloadTSDoc method for internal apps
		// but this method is called directly by the portal (bypassing
		// startDownloadTSDoc)
		// so we need to retrieve the value if it is not already set
    	if (isPublicApp()) {
    		return null;
    	}
    	
		if (downloadDoc == null || !isInternalApp()) {
			downloadDoc = (Attachment) FacesUtil
					.getManagedBean(COMPLIANCE_DOC_MANAGED_BEAN);
		}
		if (downloadDoc == null || downloadDoc.getTradeSecretDocId() == null) {
			DisplayUtil.displayError("No Trade Secret document is available.");
		} else {
			try {
				OpenDocumentUtil.downloadDocument(downloadDoc.getTradeSecretDocURL());
			} catch (RemoteException e) {
				handleException(e);
			} catch (IOException e) {
				DisplayUtil.displayError("A system error has occurred. Please contact System Administrator.");
				logger.error(e.getMessage(), e);
			}
		}

		return null;
	}

	public void cancelDownloadTSDoc(ActionEvent event) {
		downloadDoc = null;
		AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}
