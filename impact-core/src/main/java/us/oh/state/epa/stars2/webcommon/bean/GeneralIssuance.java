package us.oh.state.epa.stars2.webcommon.bean;

import java.rmi.RemoteException;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import oracle.adf.view.faces.context.AdfFacesContext;
import oracle.adf.view.faces.event.ReturnEvent;
import oracle.adf.view.faces.model.UploadedFile;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.GenericIssuanceService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.DelegationRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RPERequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RPRRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
import us.oh.state.epa.stars2.database.dbObjects.document.CorrespondenceDocument;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.genericIssuance.GenericIssuance;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitInfo;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.DraftPublicNoticeDef;
import us.oh.state.epa.stars2.def.FacilityRoleDef;
import us.oh.state.epa.stars2.def.GenericIssuanceTypeDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.AppValidationMsg;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

public class GeneralIssuance extends AppBase {

    private static final String ISSUANCE_DOC_TYPE = "id";

    private static final String ADDRESS_LABEL_DOC_TYPE = "al";

    private boolean issuanceAllow;

    private boolean editMode;

    private boolean newIssuance;

    private GenericIssuance issuance;

    private List<GenericIssuance> issuances;

    private List<SelectItem> issuanceTypes;

    private CorrespondenceDocument tempDoc;

    private Integer issuanceId;

    private Integer applicationId;

    private UploadedFile fileToUpload;

    private String facilityId;

    private AppBase bean;

    private String uploadType;

    private Integer permitId;

    private Integer fpId;

    private String doLaaCd;
    
    private Document invoiceDoc;
    
	private FacilityService facilityService;
	private GenericIssuanceService genericIssuanceService;
	private InfrastructureService infrastructureService;
	private PermitService permitService;

	private ReadWorkFlowService readWorkFlowService;

	public ReadWorkFlowService getReadWorkFlowService() {
		return readWorkFlowService;
	}

	public void setReadWorkFlowService(ReadWorkFlowService readWorkFlowService) {
		this.readWorkFlowService = readWorkFlowService;
	}

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

	public GenericIssuanceService getGenericIssuanceService() {
		return genericIssuanceService;
	}

	public void setGenericIssuanceService(
			GenericIssuanceService genericIssuanceService) {
		this.genericIssuanceService = genericIssuanceService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    /**
     * Load issuance object with issuanceId and pop up the general issuance
     * detail window.
     * 
     * @return
     */
    public String loadIssuance() {
        loadIssuance(issuanceId);
        return "dialog:issuancePop";
    }

    private void loadIssuance(Integer issId) {
        try {
            issuance = getGenericIssuanceService().retrieveGenericIssuance(issId);
            issuanceAllow = !issuance.isIssued();
            
            // set invoice doc. 
            // for some issuance, there are invoice object related and the doc
            // need to be showed on issuance page.
            setInvoiceDoc(null);
            if (GenericIssuanceTypeDef.TIME_EXTENSION_PTI.equals(issuance.getIssuanceTypeCd()) ||
                    GenericIssuanceTypeDef.TIME_EXTENSION_PTIO.equals(issuance.getIssuanceTypeCd())){
                PermitInfo tpi = getPermitService().retrievePermit(issuance.getPermitId());
                if (tpi != null){
                    Permit tp = tpi.getPermit();
                    setInvoiceDoc(tp.getInvoiceDoc());
                }
            }
        } catch (RemoteException e) {
            DisplayUtil.displayError("An error occurred while loading issuance data. Please try again.");
            logger.error(e.getMessage(), e);
        }
    }

    public void loadIssuances(BaseDB in) {

        applicationId = null;
        facilityId = null;
        fpId = null;
        doLaaCd = null;
        permitId = null;
        
        newIssuance = false;
        
        if (in instanceof RelocateRequest) {
            logger.warn("loading issuances");
            RelocateRequest rr = (RelocateRequest)in;
            
            applicationId = rr.getApplicationID();
            doLaaCd = rr.getFacility().getDoLaaCd();
            facilityId = rr.getFacility().getFacilityId();
            fpId = rr.getFacility().getFpId();
           
            /*
             * there is no permit requried for relocation request (per Erica, uses
             * free-form text field for user to provide this type of info)
             */

        } else if (in instanceof DelegationRequest) {
            DelegationRequest dr = (DelegationRequest)in;

            applicationId = dr.getApplicationID();
            doLaaCd = dr.getFacility().getDoLaaCd();
            facilityId = dr.getFacility().getFacilityId();
            fpId = dr.getFacility().getFpId();
            
            /*
             * there is no permit requried for delegation requests
             */

        } else if (in instanceof Application) {
            Application app = (Application) in;
            applicationId = app.getApplicationID();
            facilityId = app.getFacilityId();
            fpId = app.getFacility().getFpId();
            doLaaCd = app.getDoLaaCd();
            if (in instanceof RPERequest || in instanceof RPRRequest){
                Permit permit;
                try {
                    permit = getPermitService().retrievePermit(((Application) in).getApplicationNumber());
                    if (permit != null){
                        permit = getPermitService().retrievePermit(permit.getPermitID()).getPermit();
                        permitId = permit.getPermitID();
                    }
                } catch (RemoteException e) {
                    handleException(e);
                }
            }
        } else if (in instanceof Permit) {
            Permit p = (Permit)in;
            for (Application app : p.getApplications()){
                applicationId = app.getApplicationID();
                doLaaCd = app.getDoLaaCd();
                facilityId = app.getFacilityId();
                fpId = app.getFacility().getFpId();
            }
            if (facilityId == null){
                Facility f;
                try {
                    f = getFacilityService().retrieveFacility(p.getFacilityId());
                    doLaaCd = f.getDoLaaCd();
                    facilityId = f.getFacilityId();
                    fpId = f.getFpId();
                } catch (RemoteException e) {
                    handleException(e);
                }
            }
            permitId = p.getPermitID();
            
        } 
        
        reLoadIssuances();

    }

    private void reLoadIssuances() {
        // TODO add facilityId and permitId
        try {
            issuances = getGenericIssuanceService().searchGenericIssuances(null,
                    applicationId, permitId, null);
        } catch (RemoteException e) {
            DisplayUtil
                    .displayError("An error occurred while loading issuances data. Please try again.");
            logger.error(e.getMessage(), e);
        }
    }

    public final String beginEdit() {
        editMode = true;
        return null;
    }

    public final String createIssuance() {
        issuance = new GenericIssuance();
        issuance.setApplicationId(applicationId);
        issuance.setPermitId(permitId);
        issuance.setFacilityId(facilityId);
        
        if (issuanceTypes.size() == 1)
            setIssuanceTypeCd((String)issuanceTypes.get(0).getValue());
        newIssuance = true;
        beginEdit();
        return "dialog:issuancePop";
    }

    public final String save() {
        if (newIssuance) {
            try {
                getGenericIssuanceService().createGenericIssuance(issuance);
                issuances.add(issuance);
            } catch (RemoteException e) {
                DisplayUtil
                        .displayError("An error occurred while create issuances data. Please try again.");
                logger.error(e.getMessage(), e);
                return null;
            }
        } else {
            try {
                getGenericIssuanceService().modifyGenericIssuance(issuance);
            } catch (RemoteException e) {
                DisplayUtil
                        .displayError("An error occurred while modify issuances data. Please try again.");
                logger.error(e.getMessage(), e);
                return null;
            }
        }

        loadIssuance(issuance.getIssuanceId());
        editMode = false;
        newIssuance = false;
        return null;
    }

    public final void issue(ReturnEvent returnEvent) {

        ConfirmWindow cw 
            = (ConfirmWindow) FacesUtil.getManagedBean("confirmWindow");
                
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
            return;
        }
        
        List<ValidationMessage> messages = GeneralIssuanceValidation.issueValidation(issuance);
        if (AppValidationMsg.validate(messages, true)) {
            try {
                ValidationMessage[] vms 
                    = getGenericIssuanceService().issueGenericIssuance(issuance,
                                                               InfrastructureDefs.getCurrentUserId(),
                                                               InfrastructureDefs.getPortalUser());
                for (ValidationMessage vm : vms) {
                    if (!vm.getSeverity().equals(ValidationMessage.Severity.ERROR)) {
                        DisplayUtil.displayWarning(vm.getMessage());
                    }
                    else {
                        DisplayUtil.displayError(vm.getMessage());
                    }
                }

                loadIssuance(issuance.getIssuanceId());
            } 
            catch (RemoteException e) {
                DisplayUtil.displayError("An error occurred during issuance. Please try again.");
                logger.error("General issuance exception: " + e.getMessage(), e);
            }
        }
    }
    
    public final void delete(ReturnEvent returnEvent) {

        ConfirmWindow cw 
            = (ConfirmWindow) FacesUtil.getManagedBean("confirmWindow");
                
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
            return;
        }
        
        try{
            getGenericIssuanceService().deleteGenericIssuance(issuance,
                    InfrastructureDefs.getCurrentUserId());
        }catch (RemoteException ex) {
            DisplayUtil.displayError("Cannot delete : " + ex.getMessage());
            handleException(ex);
        }
        
        reLoadIssuances();

        // Also reload the main object.
        bean.reload();
        FacesUtil.returnFromDialogAndRefresh();
    }

    public final void cancel(ActionEvent actionEvent) {
        reLoadIssuances();

        // Also reload the main object.
        bean.reload();
        FacesUtil.returnFromDialogAndRefresh();
    }

    public final String generateDocs(){

        try {
            issuance.setPermitId(permitId);
            getGenericIssuanceService().generateDocs(issuance,
                                             InfrastructureDefs.getCurrentUserId());
            
            loadIssuance(issuance.getIssuanceId());
        } 
        catch (RemoteException ex) {
            DisplayUtil.displayError("Cannot prepare : " + ex.getMessage());
            handleException(ex);
        }
        
        return null;
    }
    
    public final String uploadDoc() {

        tempDoc = new CorrespondenceDocument();
        fileToUpload = null;
        String desc = null;

        if (ISSUANCE_DOC_TYPE.equalsIgnoreCase(uploadType)) {
            for (SelectItem s : issuanceTypes)
                if (s.getValue().equals(issuance.getIssuanceTypeCd())) {
                    desc = s.getLabel();
                    break;
                }
        }
        else if (ADDRESS_LABEL_DOC_TYPE.equalsIgnoreCase(uploadType)){
            desc = "Address Label";
        }
        
        tempDoc.setDescription(desc);
        return "dialog:issuanceDoc";
    }

    public final void applyEditDoc(ActionEvent actionEvent) {

        tempDoc.setFacilityID(facilityId);
        tempDoc.setLastModifiedBy(InfrastructureDefs.getCurrentUserId());
        if (fileToUpload == null) {
            DisplayUtil.displayWarning("Please select a file to upload");
            return;
        }

        try {
            tempDoc.setExtension(DocumentUtil.getFileExtension(fileToUpload
                    .getFilename()));
            tempDoc = getGenericIssuanceService().uploadTempDocument(tempDoc,
                    fileToUpload.getInputStream());
        } catch (Exception ex) {
            DisplayUtil.displayError("cannot upload document");
            logger.error(ex.getMessage(), ex);
            return;
        }

        fileToUpload = null;

        if (ISSUANCE_DOC_TYPE.equalsIgnoreCase(uploadType)) {
            issuance.setIssuanceDoc(tempDoc);
        }
        else if (ADDRESS_LABEL_DOC_TYPE.equalsIgnoreCase(uploadType)) {
            issuance.setAddrLabelDoc(tempDoc);
        }

        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }

    public final void cancelEditDoc(ActionEvent actionEvent) {
        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }

    public final void docDialogDone(ReturnEvent returnEvent) {
        tempDoc = null;
    }

    public final void setIssuanceAllow(boolean issuanceAllow) {
        this.issuanceAllow = issuanceAllow;
    }

    public final boolean isIssuanceAllow() {
        return !isEditMode() && issuanceAllow && 
            (InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("generalIssuance"));
    }
    
    public final boolean isEditAllow(){
        boolean ret = isIssuanceAllow();

        if (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
            ret = true;
        }

        if (InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid(
                "permits.detail.editable")) {
            ret = true;
        }

        return ret;
    }
    
    public final boolean isOneType(){
        return issuanceTypes.size() == 1;
    }
    
    public final boolean isEditMode() {
        return editMode;
    }

    public final void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public final GenericIssuance getIssuance() {
        return issuance;
    }

    public final void setIssuance(GenericIssuance issuance) {
        this.issuance = issuance;
        this.issuanceId = issuance.getIssuanceId();
    }

    public final List<SelectItem> getIssuanceTypes() {
        return issuanceTypes;
    }
    
    public final List<SelectItem> getIssuanceTypesAll() {
        /* 
         * this was added so the read-only table (issuancesTable.jsp) could list 
         * all the possible types while the pop-up editor can be setup to use
         * a subset of items available through the existing getIssaunceTypes()
         */
        return GenericIssuanceTypeDef.getData().getItems().getAllItems();
    }

    public final void setIssuanceTypes(List<SelectItem> issuanceTypes) {
        this.issuanceTypes = issuanceTypes;
    }

    public final List<GenericIssuance> getIssuances() {
        return issuances;
    }

    public final void setIssuances(List<GenericIssuance> issuances) {
        this.issuances = issuances;
    }

    public final Integer getIssuanceId() {
        return issuanceId;
    }

    public final void setIssuanceId(Integer issuanceId) {
        this.issuanceId = issuanceId;
    }

    public final String getIssuanceTypeCd() {
        return issuance.getIssuanceTypeCd();
    }

    public final void setIssuanceTypeCd(String issuanceTypeCd) {

        //retrieve the WRAPN_CD from IS_ISSUANCE_TYPE_DEF table
        String wrapn_cd = GenericIssuanceTypeDef.getWrapnData().getItems().getItemDesc(issuanceTypeCd);
        String def 
            = DraftPublicNoticeDef.getData().getItems().getItemDesc(wrapn_cd);
        
        if (issuance==null) {logger.error("No issuance object found");}
        issuance.setIssuanceTypeCd(issuanceTypeCd);
        
        if (def == null) {
            // Mantis 2681
            def = "Public Notice Text is not defined.";
          logger.error("Public Notice Text is not found for type " + issuanceTypeCd);
        }
        else {
            try {
            	String role = FacilityRoleDef.NSR_PERMIT_ENGINEER;
            	// To Do - assign correct facility role. In STARS2, this was set to the permit writer.
            	// For Impact, there are two Permit Engineer roles: one for NSR and one for Title V.
            	// Since permit type is not easily determined AND we are not sure that this method is
            	// used in IMPACT, we will set the role to NSR_PERMIT_ENGINEER, for now, until we determine that it
            	// needs to be set to something else.
            	
                Integer[] users 
                    = getReadWorkFlowService().retrieveUserIdsOfFacility(fpId,role);
                UserDef user = null;
                if (users != null && users.length > 0) {
                    user = getInfrastructureService().retrieveUserDef(users[0]);
                }
                def = DraftPublicNoticeDef.doSubstitution(user, doLaaCd, def);
                
            } 
            catch (RemoteException e) {
                DisplayUtil.displayError("cannot find user");
                handleException(e);
            }
        }
        issuance.setPublicNoticeText(def);
    }

    public final void setFileToUpload(UploadedFile fileToUpload) {
        this.fileToUpload = fileToUpload;
    }

    public final UploadedFile getFileToUpload() {
        return fileToUpload;
    }

    public final CorrespondenceDocument getTempDoc() {
        return tempDoc;
    }

    public final void setTempDoc(CorrespondenceDocument tempDoc) {
        this.tempDoc = tempDoc;
    }

    public void setBean(AppBase bean) {
        this.bean = bean;
    }

    public final String getUploadType() {
        return uploadType;
    }

    public final void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }

    /**
     * @return the invoiceDoc
     */
    public final Document getInvoiceDoc() {
        return invoiceDoc;
    }

    /**
     * @param invoiceDoc the invoiceDoc to set
     */
    public final void setInvoiceDoc(Document invoiceDoc) {
        this.invoiceDoc = invoiceDoc;
    }

}
