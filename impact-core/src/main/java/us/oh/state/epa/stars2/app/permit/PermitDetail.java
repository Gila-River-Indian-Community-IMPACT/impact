package us.oh.state.epa.stars2.app.permit;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeStateBase;

import oracle.adf.view.faces.component.UIXTable;
import oracle.adf.view.faces.context.AdfFacesContext;
import oracle.adf.view.faces.event.ReturnEvent;
import oracle.adf.view.faces.model.UploadedFile;
import us.oh.state.epa.aport.admin.domain.User;
import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.app.application.ApplicationDetail;
import us.oh.state.epa.stars2.app.tools.ListPermitTempFiles;
import us.oh.state.epa.stars2.bo.ApplicationService;
import us.oh.state.epa.stars2.bo.DocumentService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.bo.PermitConditionService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.adhoc.DataGridCell;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.RPCRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplication;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.AreaEmissionsOffset;
import us.oh.state.epa.stars2.database.dbObjects.permit.EmissionsOffset;
import us.oh.state.epa.stars2.database.dbObjects.permit.NSRFixedCharge;
import us.oh.state.epa.stars2.database.dbObjects.permit.PTIOPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitCC;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitChargePayment;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitDocument;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEU;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitInfo;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitIssuance;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitNote;
import us.oh.state.epa.stars2.database.dbObjects.permit.TVPermit;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataField;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.DraftPublicNoticeDef;
import us.oh.state.epa.stars2.def.FacilityRoleDef;
import us.oh.state.epa.stars2.def.GenericIssuanceTypeDef;
import us.oh.state.epa.stars2.def.IssuanceStatusDef;
import us.oh.state.epa.stars2.def.NSRBillingChargePaymentTypeDef;
import us.oh.state.epa.stars2.def.NSRBillingFeeTypeDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.PTIOPERDueDateDef;
import us.oh.state.epa.stars2.def.PermitActionTypeDef;
import us.oh.state.epa.stars2.def.PermitDocIssuanceStageDef;
import us.oh.state.epa.stars2.def.PermitDocTypeDef;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitIssuanceTypeDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PermitStatusDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.PublicNoticeTypeDef;
import us.oh.state.epa.stars2.def.RPCTypeDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.def.TemplateDocTypeDef;
import us.oh.state.epa.stars2.def.USEPAOutcomeDef;
import us.oh.state.epa.stars2.def.WorkflowProcessDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.InvoiceGenerationException;
import us.oh.state.epa.stars2.framework.userAuth.UserAttributes;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentGenerationException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.AppValidationMsg;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.Stars2TreeNode;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.ConfirmWindow;
import us.oh.state.epa.stars2.webcommon.bean.GeneralIssuance;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.bean.Stars2Object;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

public class PermitDetail extends TaskBase {
	
	private static final long serialVersionUID = 8486901090819546189L;

	public static final String UPDATE_PROFILE_MSG = "Click 'Yes' to finalize the selected permits or 'No' to cancel the operation. If you choose 'Yes', the permits will be marked as published/issued, statuses of partially superseded/superseded permit conditions will be updated, and any required updates to the facility inventories will be made."; 
    private static final String COMMENT_DIALOG_OUTCOME = "dialog:dapcComment";
    private static final int COLLAPSED_INDEX = 0;
    private static final int PERMIT_WIDTH = 995;
    private static final int CONTENT_WIDTH = 845;
    private static final int TREE_WIDTH = PERMIT_WIDTH - CONTENT_WIDTH;
    static final String PERMIT_NODE_TYPE = "permit";
    static final String APP_NODE_TYPE = "app";
    static final String EU_GROUP_NODE_TYPE = "euGroup";
    static final String EU_NODE_TYPE = "eu";
    static final String EXCLUDE_EU_NODE_TYPE = "excludedEU";
    static final String MORE_EUS_NODE_TYPE = "moreEUs";
    static final String MORE_GROUPS_NODE_TYPE = "moreGroups";
    protected static final String DOC_MANAGED_BEAN = "doc";

    private transient UIXTable neshapsSubpartsTable;
    private transient UIXTable nspsSubpartsTable;
    private transient UIXTable mactSubpartsTable;
    private transient UIXTable part61NESHAPSubpartsTable;
    private transient UIXTable part63NESHAPSubpartsTable;
    private transient UIXTable subpartsTable;
    private List<Stars2Object> mactSubparts;
    private List<Stars2Object> neshapsSubparts;
    private List<Stars2Object> nspsSubparts;
    private List<Stars2Object> part61NESHAPSubparts;
    private List<Stars2Object> part63NESHAPSubparts;
    private List<Stars2Object> subparts;
    
    private TreeModelBase treeData;
    private Stars2TreeNode selectedTreeNode;
    private PermitEUGroup selectedEUGroup;
    private PermitEU selectedEU;
    private EmissionUnit selectedExcludedEU;
    
    private boolean editMode;
    private boolean editMode1;
    private boolean EditApplications;
    private Permit permit;
    private Facility currentFacility;
    private List<EmissionUnit> allEUs;
    private List<SelectItem> euGroupSelectItems;
    private List<SelectItem> rpeNumbers;
    private List<SelectItem> rprNumbers;
    private Map<String, Map<String, PermitDocument>> docsMap;
    private List<PermitDocument> attachments;
    private List<PermitDocument> tcs;
    private PermitDocument topTCDoc;
    private String newReferencedAppNumber;
    private String newSupersededPermitNumber;
    private PermitEUGroup newEUGroup;
    private PermitEU newEU;
    private PermitNote tempComment;
    private PermitNote modifyComment;
    private boolean newNote;
    private String switchButtonText;
    private boolean switchButton;
    private boolean isNewPermit;
    private PermitDocument tempDoc;
    private PermitDocument docBeingModified;
    private PermitDocument docBeingCloned;
    private boolean isDocUpload;
    private TemplateDocument newDocumentTemplate;
    private UploadedFile fileToUpload;
    private UploadedFile templateFileToUpload;
	private String currentIssuanceAction;
    private List<EmissionUnit> excludedEUs;
    private String groupHeader = "EU Group";
    private ArrayList<SelectItem> activePermits;
    private String docTypeCD;
    private String issuanceStageFlag;
    private PermitEUGroup individualEUGroup;
    private PermitDocument singleDoc;
    private PermitEU eu;

    // private static final String ATTACHMENT_MANAGED_BEAN = "attachments";
    // private Attachments attachmentManager;
    private boolean deniable;
    private boolean noteReadOnly;
    private Integer corrEpaEMUID;
    private String docInfo;
    private String zipUrl;
    private boolean modifyIndividualEUGroup;
    private Document recentAppDoc;
    private boolean removeGroup;
	private boolean euUpdateError = false;
	
	private boolean newPermitWithDocuments;
	private UploadedFileInfo draftFileInfo;
	private UploadedFileInfo finalFileInfo;
	private UploadedFileInfo sobFileInfo;
	
	private boolean dialogEdit;
	
	private ApplicationService applicationService;
    private DocumentService documentService;
	private FacilityService facilityService;
	private InfrastructureService infrastructureService;
	private PermitService permitService;
	private PermitConditionService permitConditionService;
    private ReadWorkFlowService readWorkFlowService;
    
    private List<PermitDocument> fixedPotentialAttachments;
    private List<PermitDocument> potentialAttachments;
    private List<PermitDocument> permitDetailAttachments;
    private List<PermitDocument> draftPublicationAttachments;
    private List<PermitDocument> ppPublicationAttachments;
    private List<PermitDocument> finalIssuanceAttachments;
    private List<PermitDocument> withdrawalIssuancceAttachments;
    private List<PermitDocument> feeSummaryAttachments;
    private String previousApplicationNumnber;
    private String currentPage;
    private DocumentGenerationBean documentGenerationBean; 
    private String defValue;  
    private DataGridCell record;
    private boolean semiEditMode;
    
	// NSR Billing
    private TableSorter NSRFixedChargeWrapper;
    protected NSRFixedCharge modifyNSRFixedCharge;
	private boolean newNSRFixedCharge = false;
	
	private TableSorter NSRTimeSheetRowWrapper;
	
	private TableSorter permitChargePaymentWrapper;
	protected PermitChargePayment modifyPermitChargePayment;
	private boolean newPermitChargePayment = false;
	
	private Timestamp invoiceRefDate;
	private String invoiceType;
	
	public String totalFormatedCurrentBalance = "";
	
	private List<AreaEmissionsOffset> areaEmissionsOffsetList;
	private HashMap<String, List<EmissionsOffset>> areaEmissionsOffsetMap = 
			new HashMap<String, List<EmissionsOffset>>();
	
	protected HashMap<String, ValidationMessage> validationMessages = new HashMap<String, ValidationMessage>(
			1);
	
	private String popupRedirectOutcome;
	
	// permit conditions
	private TableSorter permitConditionsWrapper;
	
	private Integer tempPermitId;

    public DataGridCell getRecord() {
		return record;
	}

	public void setRecord(DataGridCell record) {
		this.record = record;
	}

	public String getDefValue() {
		return defValue;
	}

	public void setDefValue(String defValue) {
		this.defValue = defValue;
	}

	public DocumentGenerationBean getDocumentGenerationBean() {
		return documentGenerationBean;
	}

	public void setDocumentGenerationBean(
			DocumentGenerationBean documentGenerationBean) {
		this.documentGenerationBean = documentGenerationBean;
	}

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

	public PermitConditionService getPermitConditionService() {
		return permitConditionService;
	}

	public void setPermitConditionService(PermitConditionService permitConditionService) {
		this.permitConditionService = permitConditionService;
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

    public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}


    public PermitDetail() {
    	
        super();

        this.setTag(ValidationBase.PERMIT_TAG);
        // for NSR Billing
        NSRFixedChargeWrapper = new TableSorter();
        NSRTimeSheetRowWrapper = new TableSorter();
        permitChargePaymentWrapper = new TableSorter(); 
        areaEmissionsOffsetList = new ArrayList<AreaEmissionsOffset>();
        
    }

    public ApplicationService getApplicationService() {
		return applicationService;
	}

	public void setApplicationService(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	@Override
    public final void cancellation() {
        deadEndPermit();
    }

    @Override
    public final String goToAllWorkflows() {
        setProcessTypeCd(WorkflowProcessDef.PERMITTING);
        super.goToAllWorkflows();
        return "allPermitWorkflows";
    }

    @Override
    public final Integer getExternalId() {
        return permit.getPermitID();
    }

    @Override
    public final void setExternalId(Integer externalId) {
        setPermitID(externalId);
    }

    @Override
    public final boolean isDoSelectedButton(ProcessActivity activity) {
        String name = activity.getActivityTemplateNm();
        boolean ret = false;

        if (name.contains("Complete Public Notice")
                && InfrastructureDefs.getCurrentUserAttrs()
                        .isCurrentUseCaseValid("permits.detail.draftIssuance")) {

            currentIssuanceAction = PermitGlobalStatusDef.ISSUED_DRAFT;
            ret = true;
        } else if (name.contains("Issue Final")
                && InfrastructureDefs.getCurrentUserAttrs()
                        .isCurrentUseCaseValid("permits.detail.finalIssuance")) {

            currentIssuanceAction = PermitGlobalStatusDef.ISSUED_FINAL;
            ret = true;	
        } else if (name.contains("Issue Inactive/Withdrawn")
        		  && InfrastructureDefs.getCurrentUserAttrs()
                  	.isCurrentUseCaseValid("permits.detail.denialIssuance")) {

        	currentIssuanceAction = PermitGlobalStatusDef.DENIAL_PENDING;
        	ret = true;
        } else {
	    	DataField[] dataFields;
			try {
				dataFields = getReadWorkFlowService().retrieveDataFieldsForProcess(activity.getProcessId());
		    	if (dataFields != null) {
		    		for (DataField df : dataFields) {
		    			if (df.getDataName().equals("Publication Type")) {
		    				
		    				if(df.getDataValue().equals("Prepare Public Notice Package") && InfrastructureDefs.getCurrentUserAttrs()
		                          .isCurrentUseCaseValid("permits.detail.draftIssuance")) {
		    					currentIssuanceAction = PermitGlobalStatusDef.ISSUED_DRAFT;
		    					ret = true;
		    				} else if (df.getDataValue().equals("Prepare Proposed Permit")&& InfrastructureDefs.getCurrentUserAttrs()
		                          .isCurrentUseCaseValid("permits.detail.PPIssuance")) {
		    		            currentIssuanceAction = PermitGlobalStatusDef.ISSUED_PP;
		    		            ret = true;
		    				}
		    			}
		    		}
		    	}  
			} catch (DAOException e) {
				logger.error("Couldn't get data for Permit activity: " + e.toString());
				DisplayUtil.displayError("Could not retrieve activity data.");
			} catch (RemoteException e) {
				logger.error("Couldn't get data for Permit activity: " + e.toString());
				DisplayUtil.displayError("Could not retrieve activity data.");
			}
        }
        
        
        return ret;
    }

    @Override
    public final String getDoSelectedButtonText() {
        return "Finalize Selected";
    }

    @Override
    public String getDoSelectedConfirmMsg() {
        return PermitDetail.UPDATE_PROFILE_MSG;
    }

    @Override
    public String getDoSelectedConfirmType() {
        return (new ConfirmWindow()).getYesNo();
    }

    @SuppressWarnings("unchecked")
    public final void doSelected(UIXTable table) {

        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");

        boolean updateProfile = true;
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.YES)) {

            Iterator<?> it = table.getSelectionState().getKeySet().iterator();
            Object oldKey = table.getRowKey();
            List<Integer> pIDs = new ArrayList<Integer>();
            List<List<Permit>> permitTables = new ArrayList<List<Permit>>();
            //List<Permit> ptiPermits = new ArrayList<Permit>();
            List<Permit> ptioPermits = new ArrayList<Permit>();
            List<Permit> ptoPermits = new ArrayList<Permit>();

            while (it.hasNext()) {
                Object obj = it.next();
                table.setRowKey(obj);
                ProcessActivity pa = (ProcessActivity) table.getRowData();
                pIDs.add(pa.getExternalId());
            }

            List<ValidationMessage> retVal = new ArrayList<ValidationMessage>();
            try {
                User user = InfrastructureDefs.getPortalUser();
                // retVal = getPermitService().finalizeIssuances(pIDs,
                // currentIssuanceAction,
                // updateProfile, getCurrentUserID(), user);

                List<Integer> failedPIDs = new ArrayList<Integer>();

                for (Integer pID : pIDs) {
                    try {

                        ValidationMessage vm = getPermitService().finalizeIssuance(pID,
                                currentIssuanceAction, updateProfile,
                                getCurrentUserID(), user);
                        if (vm != null) {
                            retVal.add(vm);
                        }
                    } catch (Exception e) {
                        retVal.add(new ValidationMessage("Permit",
                        		e.getMessage(),
        						ValidationMessage.Severity.ERROR, null));
                        failedPIDs.add(pID);
                        logger.error("Permit ID " + pID + " has not been Finalized. Failed to finalize permit ID " + pID + ".",
                                e);
                    }
                }
                for (Integer pID : failedPIDs) {
                    pIDs.remove(pID);
                }

                for (Integer pid : pIDs) {
                    PermitInfo pi = getPermitService().retrievePermit(pid);
                    Permit tp = pi.getPermit();
                    tp.setCurrentIssuanceDoc(currentIssuanceAction);
                    if (tp instanceof PTIOPermit) {
                        //if (((PTIOPermit) tp).isTv()) {
                        //    ptiPermits.add(tp);
                        //} else {
                            ptioPermits.add(tp);
                        //}
                    } else {
                        ptoPermits.add(tp);
                    }
                }

            } catch (RemoteException e) {
                String msg = "Exception caught while finalizing permit batch.";
                if (e.getMessage() != null) {
                    msg += " " + e.getMessage();
                }
                logger.error(msg, e);
                DisplayUtil.displayError(msg);
            }

            //if (ptiPermits.size() != 0)
            //    permitTables.add(ptiPermits);
            if (ptioPermits.size() != 0)
                permitTables.add(ptioPermits);
            if (ptoPermits.size() != 0)
                permitTables.add(ptoPermits);
            if (permitTables.size() > 0) {
            	FacesContext.getCurrentInstance().getExternalContext()
            	.getSessionMap().put("permitTables", permitTables);
            	FacesUtil.startModelessDialog("../permits/permitManifestFile.jsf",
            			800, 1000);
            }

            if (AppValidationMsg.validate(retVal, true)) {
            	Object close_validation_dialog = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
            	if (close_validation_dialog != null && (retVal != null && retVal.isEmpty())) {
            		FacesUtil.startAndCloseModelessDialog(AppValidationMsg.VALIDATION_RESULTS_URL);
            		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
            	}
            }
            
            table.setRowKey(oldKey);
        }
    }

    @Override
    public String getExternalName(ProcessActivity activity) {
        String ret = super.getExternalName(activity);
        String url = activity.getActivityUrl();
        if (url.contains("applications")) {
            ret = "Application";
        } else if (url.contains("permits")) {
            ret = "Permit";
        }
        return ret;
    }

    @Override
    public String getExternalNum() {
        return permit.getPermitNumber();
    }

    @Override
    public String toExternal() {
        return loadPermit();
    }

    @Override
    public final String findOutcome(String url, String ret) {
        if (url.contains("permits")) {
            if (url.contains("/enterReferralDate.jsf")) {
                //if (PermitGlobalStatusDef.ISSUED_PPP.equalsIgnoreCase(
                //        getPermit().getPermitGlobalStatusCD()))
                //    ret = PermitValidation.PPP_OUTCOME;
                //else
                    ret = PermitValidation.DRAFT_OUTCOME;
            } else if (url.contains("/draftIssuance.jsf")) {
                ret = PermitValidation.DRAFT_OUTCOME;
            } else if (url.contains("/finalIssuance.jsf")) {
                ret = PermitValidation.FINAL_OUTCOME;
            } else if (url.contains("/attachments.jsf")) {
                ret = "permits.detail.attachments";
            } else if (url.contains("/denialIssuance.jsf")) {
                ret = "permits.detail.denialIssuance";
            } else if (getPermit() instanceof PTIOPermit
                    && url.contains("/feeSummary.jsf")) {
                ret = "permits.detail.feeSummary";
            } else if (getPermit().getPermitType().equalsIgnoreCase(
                            PermitTypeDef.TV_PTO)
                    //|| getPermit().getPermitType().equalsIgnoreCase(
                    //        PermitTypeDef.TIV_PTO)
                            ) {
                //if (url.contains("/PPPIssuance.jsf")) {
                //    ret = PermitValidation.PPP_OUTCOME;
                // } else 
                	if (url.contains("/PPIssuance.jsf")) {
                    ret = PermitValidation.PP_OUTCOME;
                } else {
                    ret = PermitValidation.PERMIT_PROFILE_OUTCOME;
                }
            } else {
                ret = PermitValidation.PERMIT_PROFILE_OUTCOME;
            }
        } else if (url.contains("applications")) {
            ret = setUpApplication(ret);
        }
        return ret;
    }

    public final void syncEUsWithFacility(ReturnEvent returnEvent) {

        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");

        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
            return;
        }

        List<ValidationMessage> retVal = new ArrayList<ValidationMessage>();
        try {
            ValidationMessage[] tv = getPermitService().synchPermitEUs(permit);
            for (ValidationMessage v : tv) {
                retVal.add(v);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            DisplayUtil.displayError(e.toString());
        }
        AppValidationMsg.validate(retVal, true);
        reloadPermit();
    }

    private String setUpApplication(String ret) {
        ApplicationDetail applicationDetail = (ApplicationDetail) FacesUtil
                .getManagedBean("applicationDetail");
        List<Application> apps = getPermit().getApplications();
        if (!apps.isEmpty()) {
            applicationDetail.setApplicationID(apps.get(0).getApplicationID());
            applicationDetail.setWorkflowProcessId(getWorkflowProcessId());
            applicationDetail.setWorkflowActivityId(getWorkflowActivityId());
            applicationDetail.setFromTODOList(getFromTODOList());
            ret = "applicationDetail";
        }
        return ret;
    }

    @Override
    public final List<ValidationMessage> validate(Integer inActivityTemplateId) {
        reloadPermit();
        return PermitValidation.validate(getPermit(), inActivityTemplateId);
    }

    /**
     * ValidationBase#getValidationDlgReference() returns the current page
     * outcome. This method added other parts for the tree control.
     * 
     * The return string format is matching in validationDlgAction. The return
     * string format in this case is nodeType:ID:outcome Example return :
     * eu:EpaEmuId:outcome
     * 
     * @see us.oh.state.epa.stars2.webcommon.ValidationBase#getValidationDlgReference()
     */
    public final String getValidationDlgReference() {
        String page = super.getValidationDlgReference();
        String ts;
        if (selectedTreeNode == null) {
            ts = "";
        } else if (selectedTreeNode.getType() == null) {
            ts = "";
        } else if (selectedTreeNode.getType().equals(EU_NODE_TYPE)) {
            PermitEU uo = (PermitEU) selectedTreeNode.getUserObject();
            if (uo == null) // eu:000:outcome
                ts = PermitValidation.buildEUNodeRef("000");
            else
                // eu:EpaEmuId:outcome
                ts = PermitValidation.buildEUNodeRef(Integer.toString(uo
                        .getPermitEUID()));
        } else if (selectedTreeNode.getType().equals(EXCLUDE_EU_NODE_TYPE)) {
            EmissionUnit uo = (EmissionUnit) selectedTreeNode.getUserObject();
            if (uo == null) // excludedEU:000:outcome
                ts = PermitValidation.buildExcludeEUNodeRef("000");
            else
                // excludedEU:EpaEmuId:outcome
                ts = PermitValidation.buildExcludeEUNodeRef(uo.getEmuId()
                        .toString());
        } else if (selectedTreeNode.getType().equals(PERMIT_NODE_TYPE)) {
            ts = PERMIT_NODE_TYPE; // permit
        } else
            ts = "";

        return ts + PermitValidation.SEPARATOR + page;
    }

    /**
     * This method decoded the return string from getValidationDlgReference and
     * the referenceID in ValidationMessage to find out the correct page to
     * navigated.
     * 
     * the referenceID of ValudationMessage is in newValidationDlgReference.
     * 
     * @see us.oh.state.epa.stars2.webcommon.ValidationBase#validationDlgAction()
     */
    public final String validationDlgAction() {
        String rtn = super.validationDlgAction();
        if (rtn != null)
            return rtn;
        if (newValidationDlgReference == null
                || newValidationDlgReference
                        .equals(getValidationDlgReference())) {
            return FacesUtil.getCurrentPage(); // stay on same page
        }

        // tokenizer the string by SEPARATOR.
        if (!newValidationDlgReference.contains(PermitValidation.SEPARATOR)) {
            DisplayUtil.displayInfo("Cannot navigated to selected error.");
            return FacesUtil.getCurrentPage(); // stay on same page
        }
        StringTokenizer st = new StringTokenizer(newValidationDlgReference,
                PermitValidation.SEPARATOR);
        String nodeType = st.nextToken();

        if (nodeType.equals(APP_NODE_TYPE)) {
            return loadPermit();
        } else if (nodeType.equals(PERMIT_NODE_TYPE)) {
            selectedTreeNode = (Stars2TreeNode) treeData.getNodeById("0"); // root
        } else {
            String nodeID = st.nextToken();

            selectedEUGroup = null;
            selectedEU = null;
            selectedExcludedEU = null;
            createTree();
            Stars2TreeNode root = (Stars2TreeNode) treeData.getNodeById("0");
            root = root.findNode(nodeType, nodeID);

            if (root == null) {
                DisplayUtil.displayError("Cannot find node selected in popup");
                return FacesUtil.getCurrentPage(); // stay on same page
            }
            selectedTreeNode = root;
        }
        nodeClicked();

        // always return the outcome string.
        return st.nextToken();
    }

    public final List<SelectItem> getSupersedablePermits() {
        List<SelectItem> si = new ArrayList<SelectItem>();
        // Mantis 3030 - add "dummy" entry for pre-1974 EUs
        si.add(new SelectItem(1, "Not Applicable"));
        try {
	        if (PermitTypeDef.TV_PTO.equals(permit.getPermitType())) {
	        	SimpleIdDef[] defs = getPermitService().retrieveSupersedableTVPermits(permit.getFacilityId());
	            for (SimpleIdDef s : defs)
	                si.add(new SelectItem(s.getId(), s.getDescription()));
	        } else {
		        if (selectedEU != null)
		            corrEpaEMUID = selectedEU.getCorrEpaEMUID();
		
		        if (corrEpaEMUID != null) {
		                SimpleIdDef[] defs = getPermitService().retrieveSupersedablePermits(
		                        corrEpaEMUID, permit.getPermitType());
		                for (SimpleIdDef s : defs)
		                    si.add(new SelectItem(s.getId(), s.getDescription()));
		        }
	        }
        } catch (RemoteException e) {
            handleException(e);
            DisplayUtil.displayError(e.getMessage());
        }
        return si;
    }

    public final PermitDocument getSupersededDoc() {

        PermitDocument ret = null;
        List<String> prs = permit.getPermitReasonCDs();

        if (permit instanceof TVPermit && prs.size() == 1
            && prs.contains(PermitReasonsDef.APA)
            && ((TVPermit) permit).getSupersededPermitID() != null) {
            
            try {
                PermitInfo tpi = getPermitService().retrievePermit(((TVPermit) permit).getSupersededPermitID());
                
                if (tpi != null) {
                    Permit tp = tpi.getPermit();
                    if (tp != null) {
                        tp.setCurrentIssuanceDoc(PermitGlobalStatusDef.ISSUED_FINAL);
                        ret = tp.getCurrentIssuanceDoc();
                    }
                }
            }
            catch (RemoteException e) {
                handleException(e);
                DisplayUtil.displayError(e.getMessage());
            }
        }

        return ret;
    }

    public final List<SelectItem> getModelGeneralPermitDefs() {
        List<SelectItem> si = new ArrayList<SelectItem>();
        String typeCd = selectedEU.getGeneralPermitTypeCd();
        if (typeCd != null)
            try {
                SimpleDef[] defs = getPermitService().retrieveModelGeneralPermitDefs(
                        typeCd);
                for (SimpleDef s : defs)
                    si.add(new SelectItem(s.getCode(), s.getDescription()));

            } catch (RemoteException e) {
                handleException(e);
                DisplayUtil.displayError(e.getMessage());
            }
        return si;
    }

    public final List<SelectItem> getEuCategorieFees() {
        List<SelectItem> si = new ArrayList<SelectItem>();
        if (selectedEU.getEuFee().getFeeCategoryId() != null) {
            try {
                List<Fee> fs = getInfrastructureService().retrieveEUCategory(
                        selectedEU.getEuFee().getFeeCategoryId()).getFees();
                for (Fee fee : fs)
                    si.add(new SelectItem(fee.getFeeId(), fee.getFeeNm()));
                selectedEU.getEuFee().setFees(fs);
            } catch (RemoteException e) {
                handleException(e);
            }
        }
        return si;
    }

    public final boolean getOriginalPermitNeeded() {
        boolean ret = false;
        /*
         * List<String> prs = permit.getPermitReasonCDs(); if (prs != null &&
         * prs.contains(PermitReasonsDef.CHAPTER_31_MOD) ||
         * prs.contains(PermitReasonsDef.ADMIN_MOD) ||
         * prs.contains(PermitReasonsDef.SPM) ||
         * prs.contains(PermitReasonsDef.APA) ||
         * prs.contains(PermitReasonsDef.OFF_PERMIT_CHANGE) ||
         * prs.contains(PermitReasonsDef.MPM)) { ret = true; } else {
         * permit.setOriginalPermitNo(null); }
         */
        return ret;
    }

    //public final boolean getGeneralPermitAllowed() {
    //    boolean ret = false;
    //    List<String> prs = permit.getPermitReasonCDs();
    //    if (prs != null) {
    //        if (!prs.contains(PermitReasonsDef.ADMIN_MOD)) {
    //            ret = true;
    //        }
    //    }

    //    if (!ret && permit instanceof PTIOPermit) {
    //        ((PTIOPermit) permit).setGeneralPermit(false);
    //    }

    //    return ret;
    //}

    //public final boolean getExpressPermitAllowed() {
    //    List<String> prs = permit.getPermitReasonCDs();
    //    if (prs != null) {
    //        boolean re = true;
    //        if (prs.contains(PermitReasonsDef.CHAPTER_31_MOD))
    //            re = false;
    //        if (prs.contains(PermitReasonsDef.ADMIN_MOD))
    //            re = false;

    //        if (re)
    //            return re;
    //   }

    //    if (permit instanceof PTIOPermit)
    //        ((PTIOPermit) permit).setExpress(false);
    //    return false;
    //}

    public final boolean getFraAllowed() {
        return permit.isMact() || permit.isNeshaps() || permit.isNsps()
        		|| permit.isPart61NESHAP() || permit.isPart63NESHAP();
    }

    public final boolean getInstallation() {
        /*
         * List<String> prs = permit.getPermitReasonCDs(); if (prs != null &&
         * prs.contains(PermitReasonsDef.INITIAL_INSTALLATION)) return true;
         */
        return false;
    }

    public final int getPermitID() {
        return permit.getPermitID();
    }

    public final void setPermitID(int permitID) {
        loadPermit(permitID);

        SimpleMenuItem.setDisabled("menuItem_permitCurrentWorkflow",
                !getFromTODOList());
    }

    /**
     * 
     */
    private void reset() {
        rpeNumbers = null;
        rprNumbers = null;
        activePermits = null;
        individualEUGroup = null;
        zipUrl = null;
        recentAppDoc = null;
        removeGroup = false;
    }

    public final <T extends Permit> int newPermit(Class<T> permitClass, String legacyPermitNumber,
            String newPermitNumber, boolean titleV, boolean permitIssuedDraft, Facility facility,
            Timestamp PermitDraftIssueDate, Timestamp PermitFinalIssueDate, Timestamp PermitEffectiveDate, 
            Timestamp PermitExpirationDate, String PermitDescription, 
            UploadedFileInfo draftFileInfo, UploadedFileInfo finalFileInfo,
            UploadedFileInfo sobFileInfo) {
        String permitNumber;
        
        boolean ok = true;

        try {
            permit = permitClass.newInstance();
            if (newPermitNumber == null || newPermitNumber.trim().equals("")) {
                permitNumber = getPermitService().getNewPermitNumber();
            } else {
                permitNumber = newPermitNumber;
                if (getPermitService().isDuplicatePermitNumber(permitNumber)) {
					DisplayUtil.displayError("The permit number already exists in the system.");
					ok = false; // failure
				} 
            }
            if (StringUtils.isNotBlank(legacyPermitNumber)) {
            	if (getPermitService().isDuplicateLegacyPermitNumber(legacyPermitNumber,permitNumber)) {
            		DisplayUtil.displayError("The legacy permit number already exists in the system.");
            		ok = false; // failure
            	} 
            }
            
            if(Utility.isNullOrEmpty(PermitDescription)) {
            	DisplayUtil.displayError("Permit Description cannot be empty.");
            	ok = false;
            }
            
			if (titleV
					&& (PermitFinalIssueDate == null || PermitExpirationDate == null)) {
				DisplayUtil
						.displayError("Both Final Issuance Date and Expiration Date must must be provided.");
				ok = false;
			}
			
			if (titleV
					&& (finalFileInfo == null || sobFileInfo == null)) {
				DisplayUtil
						.displayError("Both Final Issuance Document and Final Statement of Basis Document must must be provided.");
				ok = false;
			}
			
			// for NSR permit final issuance date is needed
			if(!titleV && PermitFinalIssueDate == null) {
				DisplayUtil
				.displayError("Final Issuance Date must must be provided.");
				ok = false;
			}
			
			if (draftFileInfo != null && !DocumentUtil.isValidFileExtension(draftFileInfo.getFileName())){
				if (titleV)
					DisplayUtil.displayError(DocumentUtil.invalidFileExtensionMessage("Public Notice Document"));
				else
					DisplayUtil.displayError(DocumentUtil.invalidFileExtensionMessage("NSR Analysis Document"));
				ok = false;
				draftFileInfo.cleanup();
			}
			
			if (finalFileInfo != null && !DocumentUtil.isValidFileExtension(finalFileInfo.getFileName())){
				DisplayUtil.displayError(DocumentUtil.invalidFileExtensionMessage("Final Issuance Document"));
				ok = false;
				finalFileInfo.cleanup();
			}
			if (sobFileInfo != null && !DocumentUtil.isValidFileExtension(sobFileInfo.getFileName())){
				DisplayUtil.displayError(DocumentUtil.invalidFileExtensionMessage("Final Statement of Basis Document"));
				ok = false;
				sobFileInfo.cleanup();
			}

			if(!ok)
				return -1;
			
        } catch (Exception ex) {
            return -1; // failure
        }

        if (permitClass == PTIOPermit.class) {
            ((PTIOPermit) permit).setTv(titleV);
            ((PTIOPermit) permit).setBillable(false);
        }
        currentFacility = facility;
        permit.setFacilityId(facility.getFacilityId());
        permit.setLegacyPermit(true);
        permit.setLegacyPermitNumber(legacyPermitNumber);
        permit.setPermitNumber(permitNumber);
        permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.ISSUED_FINAL);
        permit.setFinalIssuanceStatusCd(IssuanceStatusDef.ISSUED);
    	permit.setFinalIssueDate(PermitFinalIssueDate);
    	
        if (permitIssuedDraft) {
        	permit.setDraftIssuanceStatusCd(IssuanceStatusDef.ISSUED);
        }
    	permit.setDraftIssueDate(PermitDraftIssueDate);
    	
    	permit.setEffectiveDate(PermitEffectiveDate);
    	permit.setExpirationDate(PermitExpirationDate);
    	
    	// set the permit primary reason to Not Assigned if it does not have a reason
    	if(null == permit.getPrimaryReasonCD())
    		permit.setPrimaryReasonCD(PermitReasonsDef.NOT_ASSIGNED);
    	
    	permit.setDescription(PermitDescription);

        allEUs = new ArrayList<EmissionUnit>();
        isNewPermit = true;
        this.draftFileInfo = draftFileInfo;
        this.finalFileInfo = finalFileInfo;
        this.sobFileInfo = sobFileInfo;
        newPermitWithDocuments = (this.draftFileInfo != null || this.finalFileInfo != null || this.sobFileInfo != null);
        setupScreen();
        //enterEditMode();
        
        return 0; // success
    }

    public final TreeModelBase getTreeData() {
        return treeData;
    }

    public final Stars2TreeNode getSelectedTreeNode() {
        return selectedTreeNode;
    }

    public final void setSelectedTreeNode(Stars2TreeNode selectedTreeNode) {
        this.selectedTreeNode = selectedTreeNode;
    }

    public final Permit getPermit() {
        return permit;
    }

    public final Facility getCurrentFacility() {
        return currentFacility;
    }

    public final boolean getEditMode() {
        return editMode;
    }

    public final boolean isAddEUsFromFacilityAllowed(){
        boolean ret = true;
        
    	if(InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid(
                "permits.detail.addEUsFromFacilityAllowed")) {
    		ret = true;
    	}
        
        if (isPermitInDoneStatus()) {
            ret = false;
        }

        if (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
            ret = true;
        }
        
        return ret;

    }
    
    public final boolean getEditAllowed() {
        boolean ret = true;

        if (InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid(
        		"permits.detail.editable")) {
        	ret = true;
        }
        
        if (isPermitInDoneStatus()) {
            ret = false;
        }

        if (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
            ret = true;
        }

        return ret;
    }
    
    public final boolean getSemiEditAllowed() {
    	return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin() 
    				|| InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("editFinalizedPermit");
    }

	public boolean isPermitInDoneStatus() {
		if (permit == null) {
			return false;
		} else {
			return permit.getPermitGlobalStatusCD().equalsIgnoreCase(
					PermitGlobalStatusDef.DEAD_ENDED)
					|| permit.getPermitGlobalStatusCD().equalsIgnoreCase(
							PermitGlobalStatusDef.ISSUED_DENIAL)
					|| permit.getPermitGlobalStatusCD().equalsIgnoreCase(
							PermitGlobalStatusDef.ISSUED_FINAL);
		}
	}
    
    public boolean isPermitSomethingFinal() {
        return permit.getPermitGlobalStatusCD().equalsIgnoreCase(
                PermitGlobalStatusDef.ISSUED_DRAFT)
                || permit.getPermitGlobalStatusCD().equalsIgnoreCase(
                        PermitGlobalStatusDef.ISSUED_PP)
                //|| permit.getPermitGlobalStatusCD().equalsIgnoreCase(
                //        PermitGlobalStatusDef.ISSUED_PPP)
                || isPermitInDoneStatus();
    }

    public final String getNewSupersededPermitNumber() {
        return newSupersededPermitNumber;
    }

    public final void setNewSupersededPermitNumber(
            String newSupersededPermitNumber) {
        this.newSupersededPermitNumber = newSupersededPermitNumber;
    }

    public final String getNewReferencedAppNumber() {
        return newReferencedAppNumber;
    }

    public final void setNewReferencedAppNumber(String newReferencedAppNumber) {
        this.newReferencedAppNumber = newReferencedAppNumber;
    }

    public final void setFileToUpload(UploadedFile fileToUpload) {
        this.fileToUpload = fileToUpload;
    }

    public final UploadedFile getFileToUpload() {
        return fileToUpload;
    }
    
    public final UploadedFile getTemplateFileToUpload() {
		return templateFileToUpload;
	}

	public final void setTemplateFileToUpload(UploadedFile templateFileToUpload) {
		this.templateFileToUpload = templateFileToUpload;
	}

    public final PermitEUGroup getSelectedEUGroup() {
        return selectedEUGroup;
    }

    public final PermitEU getSelectedEU() {
        return selectedEU;
    }

    public final EmissionUnit getSelectedExcludedEU() {
        return selectedExcludedEU;
    }

    public final TemplateDocument getNewDocumentTemplate() {
        return newDocumentTemplate;
    }

    public final void setNewDocumentTemplate(TemplateDocument doc) {
        this.newDocumentTemplate = doc;
    }

    public final String getTcTemplateDocTypeCD() {

        if (permit instanceof PTIOPermit) {
            return ((PTIOPermit) permit).isTv() ? TemplateDocTypeDef.PTI_T_AND_C
                    : TemplateDocTypeDef.PTIO_T_AND_C;
        }

        return TemplateDocTypeDef.TVPTO_T_AND_C;
    }

    public final String getIntroTemplateDocTypeCD() {
        if (permit instanceof PTIOPermit) {
            return ((PTIOPermit) permit).isTv() ? TemplateDocTypeDef.PTI_DRAFT_ISSUANCE_PKG
                    : TemplateDocTypeDef.PTIO_DRAFT_ISSUANCE_PKG;
        }

        return TemplateDocTypeDef.TVPTO_DRAFT_ISSUANCE_PKG;
    }

    public final Map<String, Map<String, PermitDocument>> getDocsMap() {
        return docsMap;
    }

    public final List<PermitDocument> getAttachments() {
        return attachments;
    }

    public final List<PermitDocument> getTcs() {
        return tcs;
    }

    public final PermitEUGroup getSelectedEUEUGroup() {
        return selectedEU.getEuGroup();
    }

    public final void setSelectedEUEUGroup(PermitEUGroup euGroup) {
        if (selectedEU == newEU) {
            selectedEU.setEuGroup(euGroup);
        } else if (euGroup != selectedEU.getEuGroup()) {
            if (selectedEU.getEuGroup() != null) {
                selectedEU.getEuGroup().getPermitEUs().remove(selectedEU);
            }
            selectedEU.setEuGroup(euGroup);
            if (euGroup != null) {
                euGroup.getPermitEUs().add(selectedEU);
            }
        }
    }

    public final List<SelectItem> getEuGroupSelectItems() {
        return euGroupSelectItems;
    }

    public final boolean getIsDocUpload() {
        return isDocUpload;
    }

    public final boolean getIsDocUpdate() {
        return docBeingModified != null;
    }

    public final PermitDocument getTempDoc() {
        return tempDoc;
    }

    /**
     * @return the docInfo
     */
    public final String getDocInfo() {
        return docInfo;
    }

    /*
     * Actions
     */
    public final String euFeeClicked() {
        selectedEUGroup = null;
        selectedEU = null;
        selectedExcludedEU = null;

        Stars2TreeNode euNode = new Stars2TreeNode("eu", eu.getFpEU()
                .getEpaEmuId(), Integer.toString(eu.getPermitEUID()), false, eu);

        selectedTreeNode = euNode;
        selectedEU = (PermitEU) selectedTreeNode.getUserObject();

        createTree();
        return SUCCESS; // to permitDetail.jsp see permits-config.xml
    }

    public final String nodeClicked() {
        selectedEUGroup = null;
        selectedEU = null;
        selectedExcludedEU = null;
        if (selectedTreeNode == null)
            return null;

        if (selectedTreeNode.getType().equals("euGroup")) {
            selectedEUGroup = (PermitEUGroup) selectedTreeNode.getUserObject();
            groupHeader = "EU Group";
        } else if (selectedTreeNode.getType().equals("eu")) {
            selectedEU = (PermitEU) selectedTreeNode.getUserObject();
        } else if (selectedTreeNode.getType().equals("excludedEU")) {
            selectedExcludedEU = (EmissionUnit) selectedTreeNode
                    .getUserObject();
        }
        createTree();
        return FacesUtil.getCurrentPage(); // stay on same page
    }

    public final String enterEditMode() {
    	// if entering the edit mode after the permit is issued final,
    	// allow only to edit certain fields necessary for finishing the workflow
    	if(isPermitInDoneStatus() && InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("editFinalizedPermit")) 
    		semiEditMode = true;
    	else
    		editMode = true;
        
        return FacesUtil.getCurrentPage(); // stay on same page
    }

    public final String reloadPermit() {
        loadPermit(permit.getPermitID());
       	leaveEditMode();
        return FacesUtil.getCurrentPage(); // stay on same page
    }
    
    public final String reloadComments() {
        loadComments(permit.getPermitID());
        return FacesUtil.getCurrentPage(); // stay on same page
    }
    
    public final String updatePermitDocuments() {
        if(modifyPermitDocuments(permit)) {
            DisplayUtil.displayInfo("Documents successfully saved.");
        }
        return FacesUtil.getCurrentPage(); // stay on same page
    }
    
    public final String createNewLegacyPermit() {
    	return updatePermitInternal(true);
    }
    
    public final String updatePermit() {
//  DENNIS  I commented this check out because it should never trigger.
//          You would not have been allowed to do something if you did not have the rights
//          only to be told at the end of it, that you cannot do it.
//          (This is not about validating attributes--just about whether you had permission)
//        if (!getEditAllowed()) {
//            DisplayUtil
//                    .displayError("You are not authorized to modify this permit");
//            return FacesUtil.getCurrentPage(); // stay on same page
//        }
        return updatePermitInternal(false);
    }
    
    private final String updatePermitInternal(boolean createNewLegacyPermit) {
    	if (StringUtils.isNotBlank(permit.getLegacyPermitNumber())) {
    		try {
				if (getPermitService().isDuplicateLegacyPermitNumber(permit.getLegacyPermitNumber(),permit.getPermitNumber())) {
					String msg = "The legacy permit number already exists in the system.";
					throw new DAOException(msg,msg);
				}
			} catch (RemoteException e) {
                handleException(e);
                return null;
			} 
    	}
    	
    	if (permit instanceof TVPermit 
    			&& ((TVPermit) permit).getRecissionDate() != null 
    			&& ((TVPermit) permit).getFinalIssueDate() != null 
    			&& ((TVPermit) permit).getRecissionDate().before(((TVPermit) permit).getFinalIssueDate())) {
    		DisplayUtil.displayError("Rescission Date must fall on or after the Final Issuance date.");
    		return null;
		} else if (permit instanceof PTIOPermit
				&& ((PTIOPermit) permit).getRecissionDate() != null
				&& ((PTIOPermit) permit).getFinalIssueDate() != null
				&& ((PTIOPermit) permit).getRecissionDate().before(
						((PTIOPermit) permit).getFinalIssueDate())) {
			DisplayUtil
					.displayError("Rescission Date must fall on or after the Final Issuance date.");
			return null;
		}
    	
    	PermitInfo updatedInfo;
        boolean ok = true;

        if (isNewPermit) {
            try {
                updatedInfo = getPermitService().createPermit(permit,
                        getCurrentUserID());
                
                if (newPermitWithDocuments) {
                	permit = updatedInfo.getPermit();
                	addNewPermitDocuments();
                	getPermitService().modifyPermit(permit, getCurrentUserID());
                	newPermitWithDocuments = false;
                }
            } catch (RemoteException ex) {
                DisplayUtil.displayError("Cannot create Permit");
                handleException(ex);
                return null;
            }

            handleEUGroupChange(null, null);
            allEUs = updatedInfo.getPermittableEUs();
            if (updatedInfo.getCurrentFacility() != null) {
                currentFacility = updatedInfo.getCurrentFacility();
            }

            isNewPermit = false;
            if (createNewLegacyPermit) {
            	loadPermit(permit.getPermitID());
            } else {
            	reloadPermit();
            }
        } else {
            // 2521
            /* General Permit not valid for WY 
             * if (!permit.isGeneralPermit()){
                for (PermitEU teu : permit.getEus()){
                    teu.setGeneralPermitTypeCd(null);
                    teu.setModelGeneralPermitCd(null);
                }
            }*/
            ok = modifyReloadPermit(permit);
        }
        if(ok) {
            DisplayUtil.displayInfo("Data successfully saved.");
            if (!createNewLegacyPermit) {
            	leaveEditMode();
            }
        }
        
        return FacesUtil.getCurrentPage(); // stay on same page
    }

    private void addNewPermitDocuments() {
    	if (draftFileInfo != null) {
    		if(permit.getPermitType().equals(PermitTypeDef.TV_PTO)) 
    			docTypeCD = PermitDocTypeDef.TV_PUBLIC_NOTICE_DOCUMENT;
    		else if(permit.getPermitType().equals(PermitTypeDef.NSR))
    			docTypeCD = PermitDocTypeDef.ANALYSIS_DOCUMENT;
    		issuanceStageFlag = PermitDocIssuanceStageDef.DRAFT;
    		setupTempDoc();
    		tempDoc.setFacilityID(permit.getFacilityId());
    		tempDoc.setPermitId(permit.getPermitID());
    		uploadNewDoc(draftFileInfo);
    	}
    	if (finalFileInfo != null) {
    		if(permit.getPermitType().equals(PermitTypeDef.TV_PTO))
    			docTypeCD = PermitDocTypeDef.FINAL_TV_PERMIT_DOCUMENT;
    		else if(permit.getPermitType().equals(PermitTypeDef.NSR))
    			docTypeCD = PermitDocTypeDef.NSR_FINAL_PERMIT_WAIVER_PACKAGE;
    		issuanceStageFlag = PermitDocIssuanceStageDef.FINAL;
    		setupTempDoc();
    		tempDoc.setFacilityID(permit.getFacilityId());
    		tempDoc.setPermitId(permit.getPermitID());
    		uploadNewDoc(finalFileInfo);
    	}
    	if (sobFileInfo != null) {
    		if(permit.getPermitType().equals(PermitTypeDef.TV_PTO))
    			docTypeCD = PermitDocTypeDef.FINAL_STATEMENT_BASIS;
    		//else if(permit.getPermitType().equals(PermitTypeDef.NSR))
    		//	docTypeCD = PermitDocTypeDef.PERMIT_DOCUMENT;
    		issuanceStageFlag = PermitDocIssuanceStageDef.FINAL;
    		setupTempDoc();
    		tempDoc.setFacilityID(permit.getFacilityId());
    		tempDoc.setPermitId(permit.getPermitID());
    		uploadNewDoc(sobFileInfo);
    	}
        handleDocumentsModified();
        organisePotentialPermitDocuments();
	}
    
    private void setupTempDoc() {
        tempDoc = new PermitDocument();
        tempDoc.setLastModifiedBy(getCurrentUserID());
        tempDoc.setPermitDocTypeCD(docTypeCD);
        if (PermitDocTypeDef.ISSUANCE_DOCUMENT.equals(docTypeCD)) {
            tempDoc.setDescription(PermitDocIssuanceStageDef
                    .findIssuanceDocDesc(issuanceStageFlag, permit
                            .getPermitType()));
        }
       if (issuanceStageFlag.equalsIgnoreCase("")) {
            if (PermitDocTypeDef.ISSUANCE_DOCUMENT.equals(docTypeCD)) {
                logger.error("Houston, we have a problem.");
            }
            tempDoc.setIssuanceStageFlag(null);
        } else
            tempDoc.setIssuanceStageFlag(issuanceStageFlag);
        isDocUpload = true;
        docInfo = null;
    }
    
    private void uploadNewDoc(UploadedFileInfo uploadedFile) {
        try {
            tempDoc.setExtension(DocumentUtil.getFileExtension(uploadedFile.getFileName()));
            tempDoc = getPermitService().uploadTempDocument(tempDoc,uploadedFile.getInputStream());
            if(permit.isLegacyPermit() && Utility.isNullOrEmpty(tempDoc.getDescription())) {
            	tempDoc.setDescription(
        				PermitDocTypeDef.getData().getItems().getItemDesc(tempDoc.getPermitDocTypeCD()));
            }
            permit.getDocuments().add(tempDoc);
        } catch (RemoteException ex) {
            handleException(ex);
        } catch (IOException e) {
            logger.error("cannot upload document", e);
            DisplayUtil.displayError("cannot get upload document");
        }
    }

	public final String undoPermit() {
        if (isNewPermit) {
            SimpleMenuItem.setDisabled("menuItem_permitDetail", true);
            leaveEditMode();
            return "permitSearch";
        }
        reloadPermit();
        return FacesUtil.getCurrentPage();
    }

    public final boolean isDeadEndDisabled() {
        return permit.getPermitGlobalStatusCD().equalsIgnoreCase(
                PermitGlobalStatusDef.ISSUED_FINAL);
    }
    
    public final void deadEndedPermit(ReturnEvent returnEvent) {
        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO))
            return;

        deadEndPermit();
    }
    
//    public final void unDeadEndedPermit(ReturnEvent returnEvent) {
//        ConfirmWindow cw = (ConfirmWindow) FacesUtil
//                .getManagedBean("confirmWindow");
//        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO))
//            return;
//
//        unDeadEndPermit();
//    }

    private void deadEndPermit() {
        if (!PermitGlobalStatusDef.ISSUED_FINAL.equalsIgnoreCase(permit
                .getPermitGlobalStatusCD())) {
            permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.DEAD_ENDED);
            modifyReloadPermit(permit);
        }else{
            DisplayUtil.displayError("Cannot dead-end an issued final permit.");
        }
    }

    public final String requesUnDeadEnd() {
        return "dialog:unDeadEndPermit";
    }

    public final String unDeadEndPermit() {
        unDeadEndPermitInternal();
        FacesUtil.returnFromDialogAndRefresh();
        return null; 
    }
    
    public final void cancelPopup() {
    	setEditMode1(false);
    	setEditApplications(false);
    	setPreviousApplicationNumnber(null);
    	FacesUtil.returnFromDialogAndRefresh();
    	return;
        
    }

    private void unDeadEndPermitInternal() {
        if (PermitGlobalStatusDef.DEAD_ENDED.equalsIgnoreCase(permit
                .getPermitGlobalStatusCD())  && InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
            try {
                getPermitService().unDeadendPermit(permit);
            } catch (RemoteException ex) {
                DisplayUtil.displayError("Cannot return permit from dead-end state ");
                handleException(ex);
            }
            loadPermit(permit.getPermitID());
            //reloadPermit();
        }else{
            DisplayUtil.displayError("Cannot un-dead-end permit without Admin rights.");
        }
        //FacesUtil.returnFromDialogAndRefresh();
        //AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }

    public final void denyPermit(ReturnEvent returnEvent) {
        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO))
            return;

        permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.DENIAL_PENDING);
        modifyReloadPermit(permit);
    }

    public final boolean isUnDeniable() {
        return permit.getPermitGlobalStatusCD().equalsIgnoreCase(
                PermitGlobalStatusDef.DENIAL_PENDING);
    }

    public final void unDenyPermit(ReturnEvent returnEvent) {
        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO))
            return;

        permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.NONE);
        modifyReloadPermit(permit);
    }

    public final String undoEU() {
        boolean isNewEU = (selectedEU == newEU);
        if (isNewEU) {
            leaveEditMode();
            return FacesUtil.getCurrentPage(); // stay on same page
        }

        PermitEU updatedEU;
        try {
            updatedEU = getPermitService().retrievePermitEU(selectedEU.getPermitEUID());
        } catch (RemoteException ex) {
            DisplayUtil.displayError("Cannot reload EU.");
            handleException(ex);
            return FacesUtil.getCurrentPage(); // stay on same page
        }
        updatedEU.setEuGroup(selectedEU.getEuGroup());

        createTree();
        leaveEditMode();
        return FacesUtil.getCurrentPage(); // stay on same page
    }

    public final String updateEU() {
        if (selectedEU.getEuGroup() == null) {
            // This part should be in BO.
            try {
                PermitEUGroup individualGroup = permit.getIndividualEuGroup();

                if (individualGroup == null) {
                    individualGroup = new PermitEUGroup();
                    individualGroup.setIndividualEUGroup();
                    individualGroup.setPermitID(permit.getPermitID());
                    individualGroup = getPermitService().createEUGroup(individualGroup);
                }

                selectedEU.setEuGroup(individualGroup);

            } catch (RemoteException ex) {
                DisplayUtil.displayError("Cannot create EU group.");
                handleException(ex);
                return null;
            }
        }

        if (!getEditAllowed()) {
            DisplayUtil
                    .displayError("You are not authorized to modify this permit");
            return FacesUtil.getCurrentPage(); // stay on same page
        }

        boolean isNewEU = (selectedEU == newEU);
        PermitEU updatedEU;
        if (isNewEU) {
            newEU.setPermitEUGroupID(newEU.getEuGroup().getPermitEUGroupID());
            try {
                updatedEU = getPermitService().createEU(newEU, permit.getPermitType());
            } catch (RemoteException ex) {
                DisplayUtil.displayError("Cannot update EU.");
                handleException(ex);
                return FacesUtil.getCurrentPage(); // stay on same page
            }

            DisplayUtil.displayInfo("EU included");
        } else {
            try {
                getPermitService().modifyEU(selectedEU, getCurrentUserID());
                DisplayUtil.displayInfo("EU updated");
            } catch (RemoteException ex) {
                DisplayUtil.displayError("Cannot update EU");
                handleException(ex);
            } finally {
                try {
                    updatedEU = getPermitService().retrievePermitEU(
                            selectedEU.getPermitEUID());
                } catch (RemoteException ex) {
                    DisplayUtil.displayError("Cannot load EU");
                    handleException(ex);
                    return null;
                }
            }
            /*
             * Replace selectedEU with updatedEU in the local profile
             */
        }
        
        loadPermit(permit.getPermitID());
        
        // update applications that are associated with this permit
        try {
            getApplicationService().updateApplicationAfterPermitUpdate(permit);
        } catch (RemoteException e) {
            // just log an error and continue - no need to hole permit up for this problem
            logger.error("Exception while updating application(s) related to permit.", e);
        }

        Stars2TreeNode root = (Stars2TreeNode) treeData.getNodeById("0");
        root = root
                .findNode(EU_NODE_TYPE, updatedEU.getPermitEUID().toString());
        selectedTreeNode = root;
        selectedEU = (PermitEU) root.getUserObject();
        createTree();
        leaveEditMode();
        return FacesUtil.getCurrentPage(); // stay on same page
    }

    public final String excludeEU() {
        if (getEditAllowed()) {
            try {
                getPermitService().removeEU(selectedEU);
            } catch (RemoteException ex) {
                DisplayUtil.displayError("Cannot exclude EU from permit");
                handleException(ex);
                return FacesUtil.getCurrentPage(); // stay on same page
            }

            loadPermit(permit.getPermitID());
            createTree();
            // excludeEU is not calling from EU group update
            if (selectedEUGroup == null)
                DisplayUtil.displayInfo("EU excluded from permit");
        } else {
            DisplayUtil
                    .displayError("You are not authorized to modify this permit");
        }

        return FacesUtil.getCurrentPage(); // stay on same page
    }

    public final String removeEUFromCurrentGroup() {
        if (getEditAllowed()) {
        	// Mantis 2781 Check to see if individual EU group is null
        	individualEUGroup = permit.getIndividualEuGroup();
        	if (individualEUGroup != null) {
                selectedEU.setEuGroup(permit.getIndividualEuGroup());
                updateEU();
        	} else {
        		String errorMsg = "Individual EU Group is null for permit " + permit.getPermitNumber();
        		logger.error(errorMsg);
                DisplayUtil.displayError("Unable to remove EU from EU group. Contact System Administrator.");
        	}
        } else {
            DisplayUtil.displayError("You are not authorized to modify this permit");
        }
        return null;
    }

    public final String createTempEU() {
        newEU = new PermitEU();
        newEU.setPermitStatusCd(PermitStatusDef.NONE);
        newEU.setFpEU(selectedExcludedEU);
        newEU.setDapcDescription(selectedExcludedEU.getEuDesc());
        newEU.setCompanyId(selectedExcludedEU.getCompanyId());
        enterEditMode();
        selectedTreeNode = new Stars2TreeNode("eu", null, null, false, null);
        selectedEU = newEU;
        return FacesUtil.getCurrentPage(); // stay on same page
    }

    public final String includeEUs() {
        selectedEUGroup = null;
        enterEditMode();
        for (PermitEUGroup euGroup : permit.getEuGroups()) {
            if (euGroup.isIndividualEUGroup()) {
                selectedEUGroup = euGroup;
                newEUGroup = null;
                break;
            }
        }
        if (selectedEUGroup == null) {
            newEUGroup = new PermitEUGroup();
            newEUGroup.setIndividualEUGroup();
            selectedEUGroup = newEUGroup;
        }

        groupHeader = "Emissions Units";
        selectedTreeNode = new Stars2TreeNode("euGroup", null, null, false,
                null);
        return FacesUtil.getCurrentPage(); // stay on same page
    }

    public final String includeFacilityEUs() {
        selectedEUGroup = null;
        enterEditMode();
        for (PermitEUGroup euGroup : permit.getEuGroups()) {
            if (euGroup.isIndividualEUGroup()) {
                selectedEUGroup = euGroup;
                newEUGroup = null;
                break;
            }
        }
        if (selectedEUGroup == null) {
            newEUGroup = new PermitEUGroup();
            newEUGroup.setIndividualEUGroup();
            selectedEUGroup = newEUGroup;
        }

        groupHeader = "Add EU to Permit from Facility Inventory";
        selectedTreeNode = new Stars2TreeNode("euGroup", null, null, false,
                null);

        try {
            EmissionUnit[] feus = getFacilityService().retrieveFacilityEmissionUnits(
                    this.permit.getFpId());
            excludedEUs = new ArrayList<EmissionUnit>();
            for (EmissionUnit eu : feus)
                excludedEUs.add(eu);

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return FacesUtil.getCurrentPage(); // stay on same page
    }

    public final String copyPermit() {
    
        permit.setPermitNumber(null);
        permit.setLegacyPermit(false); // Chris: If I'm cloning it in STARS2, then the newly created (cloned) permit is *not* a legacy permit.
        permit.setDocuments(null);
        permit.setEuGroups(null);

        try {
            PermitInfo tp = getPermitService().createPermit(permit, getCurrentUserID());
            permit = tp.getPermit();
        } catch (RemoteException e) {
            DisplayUtil.displayError("Copy permit error.  Please try again.");
            handleException(e);
        }
        
        return reloadPermit();
    }
    
    public final String createTempEUGroup() {
        enterEditMode();
        newEUGroup = new PermitEUGroup();
        groupHeader = "Create EU Group";
        selectedTreeNode = new Stars2TreeNode("euGroup", null, null, false,
                null);
        selectedEUGroup = newEUGroup;
        return FacesUtil.getCurrentPage(); // stay on same page
    }

    public final String updateEUGroup() {
    	if (euUpdateError) {
    		DisplayUtil.displayError("Unable to update EU Group. Contact System Administrator");
    		euUpdateError = false;
            return FacesUtil.getCurrentPage(); // stay on same page
    	}
        if (!getEditAllowed()) {
            DisplayUtil
                    .displayError("You are not authorized to modify this permit");
            return FacesUtil.getCurrentPage(); // stay on same page
        }

        boolean isNewEUGroup = (selectedEUGroup == newEUGroup);

        if (isNewEUGroup) {
            if (selectedEUGroup.getName() == null
                    || selectedEUGroup.getName().length() == 0) {
                DisplayUtil.displayError("Please enter an EU group name");
                return FacesUtil.getCurrentPage(); // stay on same page
            }
            newEUGroup.setPermitID(permit.getPermitID());
            try {
                getPermitService().createEUGroup(newEUGroup);
            } catch (RemoteException ex) {
                DisplayUtil.displayError("Cannot update EU group");
                handleException(ex);
                return null;
            }
            // handleEUGroupChange(null, updatedEUGroup);
            DisplayUtil.displayInfo("EU group created");
        } else {
            try {
                // bug 1573
                if (removeGroup)
                    selectedEUGroup.setName(null);
                
                if (selectedEUGroup.getName() == null
                        || selectedEUGroup.getName().length() == 0) {
                    selectedEUGroup.setName("remove");
                    for (PermitEU pe : selectedEUGroup.getPermitEUs()){
                    	// Mantis 2781 Check to see if individual EU group is null
                    	individualEUGroup = permit.getIndividualEuGroup();
                    	if (individualEUGroup != null) {
                    		individualEUGroup.addPermitEU(pe);
	                        modifyIndividualEUGroup = true;
                    	} else {
                    		String errorMsg = "Individual EU Group is null for permit " + permit.getPermitNumber();
                    		logger.error(errorMsg);
                    		throw new RemoteException(errorMsg);
                    	}
                    }
                    selectedEUGroup.setPermitEUs(null);
                }
                getPermitService().modifyEUGroup(selectedEUGroup);
                
                // Bug 1978
                if (modifyIndividualEUGroup)
                    getPermitService().modifyEUGroup(permit.getIndividualEuGroup());
                DisplayUtil.displayInfo("EU group updated");
                
                if (selectedEUGroup.getName().equalsIgnoreCase("remove"))
                    removeEUGroup();
            } catch (RemoteException ex) {
                DisplayUtil.displayError("Cannot update EU group");
                handleException(ex);
                return null;
            }
        }
        
        reloadPermit();
        
        // update applications that are associated with this permit
        try {
            getApplicationService().updateApplicationAfterPermitUpdate(permit);
        } catch (RemoteException e) {
            // just log an error and continue - no need to hole permit up for this problem
            logger.error("Exception while updating application(s) related to permit.", e);
        }
        
        return FacesUtil.getCurrentPage(); // stay on same page
    }
    
    public final void undoEUGroupC(ReturnEvent returnEvent) {
        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO))
            return;
        
        return;
    }
    
    public final String undoEUGroup() {
        boolean isNewEUGroup = (selectedEUGroup == newEUGroup);
        if (isNewEUGroup) {
            selectTreeRoot();
            leaveEditMode();
            return FacesUtil.getCurrentPage(); // stay on same page
        }

        PermitEUGroup updatedEUGroup;
        try {
            updatedEUGroup = getPermitService().retrieveEUGroup(
                    selectedEUGroup.getPermitEUGroupID());
        } catch (RemoteException ex) {
            DisplayUtil.displayError("Cannot reload EU group");
            handleException(ex);
            return FacesUtil.getCurrentPage(); // stay on same page
        }

        handleEUGroupChange(selectedEUGroup, updatedEUGroup);
        createTree();
        leaveEditMode();
        return FacesUtil.getCurrentPage(); // stay on same page
    }

    public final String removeEUGroup() {
        if (!getEditAllowed()) {
            DisplayUtil
                    .displayError("You are not authorized to modify this permit");
            return FacesUtil.getCurrentPage(); // stay on same page
        }

        try {
            getPermitService().removeEUGroup(selectedEUGroup);
        } catch (RemoteException ex) {
            DisplayUtil.displayError("Cannot remove EU group");
            handleException(ex);
            return FacesUtil.getCurrentPage(); // stay on same page
        }

        handleEUGroupChange(selectedEUGroup, null);
        createTree();
        leaveEditMode();
        DisplayUtil.displayInfo("EU group deleted");
        return FacesUtil.getCurrentPage(); // stay on same page
    }

    public final void addReferencedApp() {
    	boolean ok = true;
        try {
        	if (this.newReferencedAppNumber == null || this.newReferencedAppNumber.length() == 0)
        	{
        		ok = false;
                DisplayUtil.displayError("Please select a application number");
                return;
        	} else if (getPreviousApplicationNumnber() != null && getPreviousApplicationNumnber().length() != 0)
        	{
        		if (this.newReferencedAppNumber.equalsIgnoreCase(getPreviousApplicationNumnber()))
        		{
        			ok = false;
        			DisplayUtil.displayError("The new application "
                            + this.newReferencedAppNumber + " is same as previous application number. Please select different application number");
        			return;
        		}
        	}
        	
            Application referencedApp = getApplicationService().retrieveApplication(
            		this.newReferencedAppNumber);
            if (referencedApp == null) {
            	ok = false;
                DisplayUtil.displayWarning("Please select a application number");
            } else if (!referencedApp.getFacilityId().equals(permit.getFacilityId())){
            	ok = false;
                DisplayUtil.displayError("This application "
                        + this.newReferencedAppNumber + " has different facility id "
                        + referencedApp.getFacilityId());
            } else if (!isOkToAddApplication(referencedApp)) {
                // 2580
            	ok = false;
                DisplayUtil.displayError("This application "
                        + this.newReferencedAppNumber + " is a wrong type for this permit. ");
            } else {
                /*if (!canAddApplication(referencedApp))
                    DisplayUtil.displayError(referencedApp
                            .getApplicationNumber()
                            + " with purpose "
                            + referencedApp.getApplicationPurposeDesc()
                            + " not acceptable in current permit.");
                else*/
            	
                    permit.addNewApplication(referencedApp);
    if (isEditApplications() && getPreviousApplicationNumnber() != null)
                    {
                    	for (Application app : permit.getApplications())
                    	{
                    		if (app.getApplicationNumber().equalsIgnoreCase(getPreviousApplicationNumnber()))
                    		{
                    			permit.removeApplication(app);
                    			setPreviousApplicationNumnber(null);
                    			break;
                    		}
                    	}
                    }
            	
                    getPermitService().savePermitApplications(permit);
            }
        } catch (RemoteException ex) {
            DisplayUtil.displayError("Cannot load application: "
                    + ex.getMessage());
            handleException(ex);
        }
		newReferencedAppNumber = null;
		setPreviousApplicationNumnber(null);
        if (ok) {
        this.editMode = false;
        setEditApplications(false);
        FacesUtil.returnFromDialogAndRefresh();
        }
        
    }
    
    private boolean isOkToAddApplication(Application app) {
        boolean ok = false;
        if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO)) {
            if (app instanceof TVApplication) {
               ok = true; 
            } else if (app instanceof RPCRequest) {
                ok = RPCTypeDef.TV_OFF_PERMIT_CHANGE.equals(((RPCRequest)app).getRpcTypeCd()) ||
                    RPCTypeDef.TV_ADMIN_PERMIT_AMENDMENT.equals(((RPCRequest)app).getRpcTypeCd());
            }
        } else if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.NSR) 
                //|| permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TVPTI)
                ) {
            if (app instanceof PTIOApplication) {
                ok = true;
            } else if (app instanceof RPCRequest) {
                ok = RPCTypeDef.PTIO_ADMIN_MOD.equals(((RPCRequest)app).getRpcTypeCd());
            }
        //} else if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TIV_PTO)) {
        //    ok = app instanceof TIVApplication;
        }
        return ok;
    }

    /**
     * Check to determine if permit has both installation and renewal app
     * 
     * @param referencedApp
     * @return
     
    private boolean canAddApplication(Application referencedApp) {
        boolean renewal = referencedApp.getApplicationPurposeCDs().contains(
                PTIOApplicationPurposeDef.RENEWAL);
        boolean installation = referencedApp.getApplicationPurposeCDs()
                .contains(PTIOApplicationPurposeDef.INSTALLATION);
        boolean ret = true;
        
        for (Application a : permit.getApplications()) {
            if (renewal
                    && a.getApplicationPurposeCDs().contains(
                            PTIOApplicationPurposeDef.INSTALLATION))
                ret = false;
            if (installation
                    && a.getApplicationPurposeCDs().contains(
                            PTIOApplicationPurposeDef.RENEWAL))
                ret = false;
        }
        
        return ret;
    }
    */

    public final void deleteReferencedApp() {
    	if (!isAllowedToDeleteReferencedApp())	{
    		DisplayUtil.displayError("At least One Application should be associated to Permit");
    	}	else {
    		 try {
				Application referencedApp = getApplicationService().retrieveApplication(
				 		this.newReferencedAppNumber);
			
    		permit.removeApplication(referencedApp);
    		
    			getPermitService().removePermitApplication(referencedApp);
    		 } catch (DAOException e) {
    			DisplayUtil.displayError("Cannot REmove application: "
    					+ e.getMessage());
    			handleException(e);
    		} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
    	setEditMode(false);
    	FacesUtil.returnFromDialogAndRefresh();
    }

    /*
     * Actions - documents-related
     */
    public final String topTCDoc() {
        try {
            PermitDocument td = singleDoc;
            modifyReloadPermit(permit);
            singleDoc = td;
            getDocumentService().setDocumentModified(singleDoc.getDocumentID(),
                    getCurrentUserID());
            topTCDoc = singleDoc;
            singleDoc = null;
        } catch (RemoteException e) {
            handleException(e);
            DisplayUtil.displayError(e.getMessage());
        }

        return reloadPermit();
    }

    public final void deleteDoc(ReturnEvent returnEvent) {
        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
            singleDoc = null;
            return;
        }

        permit.getDocuments().remove(singleDoc);
        tcs.remove(singleDoc);
        singleDoc = null;
    }

    public final void deleteDocFromMap(ReturnEvent returnEvent) {
        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
            singleDoc = null;
            return;
        }

        permit.getDocuments().remove(singleDoc);
        docsMap.remove(docTypeCD).remove(issuanceStageFlag);
        singleDoc = null;
    }
    
    public final void deleteAttachment(ReturnEvent returnEvent) {
        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
            singleDoc = null;
            FacesUtil.returnFromDialogAndRefresh();
            return;
        }

        permit.getDocuments().remove(singleDoc);
        attachments.remove(singleDoc);
        // update db
        try {
        	getPermitService().removePermitDocument(singleDoc);
        }catch(DAOException de) {
        	DisplayUtil.displayError("Unknown error when deleting permit document");
        	logger.error(de.getMessage());
        }
        
        singleDoc = null;
        organisePotentialPermitDocuments();
        FacesUtil.returnFromDialogAndRefresh();
    }

    public final void docDialogDone(ReturnEvent returnEvent) {
        docBeingModified = null;
        docBeingCloned = null;
        tempDoc = null;
        singleDoc = null; // attachment detail page is using
    }
    
    public final void templateDialogDone(ReturnEvent returnEvent) {
        docBeingModified = null;
        docBeingCloned = null;
        tempDoc = null;
        singleDoc = null; // attachment detail page is using
    }
    
    public final String uploadIssuanceDoc() {
        uploadDoc();
        return "dialog:issuanceDocDetail";
    }
    
    public final void issuanceDocDone(ReturnEvent returnEvent) {
        docDialogDone(returnEvent);
        modifyReloadPermit(permit);
    }

    /**
     * TODO remove startEditDoc
     * 
     * Only in portalJsp now.. do we need this?
     * 
     * @return
     */
    public final String startEditDoc() {
        try {
            docBeingModified = (PermitDocument) FacesUtil.getManagedBean("doc");
            docBeingCloned = null;
            tempDoc = (PermitDocument) BeanUtils.cloneBean(docBeingModified);
            isDocUpload = false;
            docInfo = null;
            return "dialog:permitDoc";
        } catch (Exception ex) {
            DisplayUtil.displayError("cannot edit document");
            return FacesUtil.getCurrentPage(); // stay on same page
        }
    }

    /**
     * TODO remove startCloneTCDoc
     * 
     * Only in portalJsp now.. do we need this?
     * 
     * @return
     */
    public final String startCloneTCDoc() {
        docBeingModified = null;
        docBeingCloned = (PermitDocument) FacesUtil.getManagedBean("doc");
        tempDoc = new PermitDocument();
        tempDoc.setLastModifiedBy(getCurrentUserID());
        tempDoc.setPermitDocTypeCD(PermitDocTypeDef.TERMS_CONDITIONS);
        tempDoc.setIssuanceStageFlag(null);
        isDocUpload = false;
        docInfo = null;
        return "dialog:permitDoc";
    }

    public final String uploadDoc() {
    	dialogEdit = true;
        setupTempDoc();
        return "dialog:permitDoc";
    }
    
    public final String startUploadTemplate() {
    	record = (DataGridCell) FacesUtil
    			.getManagedBean(defValue);
    	return "dialog:permitTemplateDoc";
    }

    public final boolean isDocDescReadOnly() {
        return !dialogEdit
                || PermitDocTypeDef.ISSUANCE_DOCUMENT.equals(docTypeCD);
    }
    
    public final boolean isAttachDocDelAble() {
    	return true;
    }

    public final String startUploadTCDoc() {
    	dialogEdit = true;
        docBeingModified = null;
        docBeingCloned = null;
        tempDoc = new PermitDocument();
        tempDoc.setLastModifiedBy(getCurrentUserID());
        tempDoc.setPermitDocTypeCD(PermitDocTypeDef.TERMS_CONDITIONS);
        docTypeCD = PermitDocTypeDef.TERMS_CONDITIONS;
        tempDoc.setIssuanceStageFlag(null);
        isDocUpload = true;
        docInfo = null;
        return "dialog:permitDoc";
    }

    public final String startGenerateTCDoc() {
    	dialogEdit = true;
        docBeingModified = null;
        docBeingCloned = null;
        tempDoc = new PermitDocument();
        tempDoc.setLastModifiedBy(getCurrentUserID());
        tempDoc.setPermitDocTypeCD(PermitDocTypeDef.TERMS_CONDITIONS);
        docTypeCD = PermitDocTypeDef.TERMS_CONDITIONS;
        tempDoc.setIssuanceStageFlag(null);
        isDocUpload = false;
        docInfo = "Note: in addition to generating a Terms and Conditions document, the system will synchronize the four character AQD EU ids listed in the permit with the values found in the current facility inventory.";
        return "dialog:permitDoc";
    }
    
    public final String startGeneratePDIDoc() {
    	dialogEdit = true;
        docBeingModified = null;
        docBeingCloned = null;
        tempDoc = new PermitDocument();
        tempDoc.setLastModifiedBy(getCurrentUserID());
        tempDoc.setPermitDocTypeCD(PermitDocTypeDef.PERMIT_DOCUMENT_INFO);
        docTypeCD = PermitDocTypeDef.PERMIT_DOCUMENT_INFO;
        tempDoc.setIssuanceStageFlag(null);
        isDocUpload = false;
        docInfo = "Note: in addition to generating a Permit information document, the system will synchronize the four character AQD EU ids listed in the permit with the values found in the current facility inventory.";
        return "dialog:permitDoc";
    }
    
    public final String startUploadAttachDoc() {
    	dialogEdit = true;
        docBeingModified = null;
        docBeingCloned = null;
        tempDoc = new PermitDocument();
        tempDoc.setIssuanceStageFlag(null);
        isDocUpload = true;
        docInfo = null;
        return "dialog:permitDoc";
    }
    
    
    
    public final String startUploadPotentialAttachDoc() {
    	dialogEdit = true;
        docBeingModified = null;
        docBeingCloned = null;
        tempDoc = (PermitDocument) FacesUtil.getManagedBean(DOC_MANAGED_BEAN);
        tempDoc.setIssuanceStageFlag(null);
        tempDoc.setRequiredDoc(true);
        isDocUpload = true;
        docInfo = null;
        return "dialog:permitDoc";
    }
    
    public final void applyEditAttachment(ActionEvent actionEvent) {
    	if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
        	applyEditAttachmentInternal(actionEvent);
        } finally {
            clearButtonClicked();
        }
    }
    
    private final void applyEditAttachmentInternal(ActionEvent actionEvent) {
        updatePermitDocuments();
        organisePotentialPermitDocuments();
    	AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }
    
    public final String generatePermitDoc() {
    	tempDoc = new PermitDocument();
    	tempDoc.setPermitDocTypeCD(PermitDocTypeDef.PERMIT_DOCUMENT_INFO);            
    	tempDoc.setPermitId(permit.getPermitID());
    	tempDoc.setFacilityID(permit.getFacilityId());
    	
    	String templateTypeCD="";
    	String templateType = tempDoc.getPermitDocTypeCD();
    	
    	if(templateType.equalsIgnoreCase("PDO")){
        	templateTypeCD = TemplateDocTypeDef.PTI_PID; 
        	tempDoc.setPermitDocTypeDsc("Permit Document Information");
        }
    	
    	tempDoc = generateTempDoc(tempDoc, templateTypeCD);   
    	
    	return "dialog:openGenDoc";
    }
    public final String generateLetterDoc() {
    	tempDoc = (PermitDocument) FacesUtil.getManagedBean(DOC_MANAGED_BEAN);
    	if(tempDoc==null){
    		tempDoc = new PermitDocument();
    		tempDoc.setPermitDocTypeCD(PermitDocTypeDef.PERMIT_DOCUMENT_INFO);            
    	}
    		
    	tempDoc.setPermitId(permit.getPermitID());
    	tempDoc.setFacilityID(permit.getFacilityId());
        
        String templateTypeCD="";

        String templateType = tempDoc.getPermitDocTypeCD();
        String templateName = tempDoc.getPermitDocTypeDsc();	
        String permitAttachmentId = tempDoc.getPermitDocumentID();
        TemplateDocument templateDoc = TemplateDocTypeDef.getTemplate(permitAttachmentId);
        String documentPath = templateDoc.getTemplateDocPath();
     
        
        if(templateType.equalsIgnoreCase("CL")){
        	templateTypeCD = TemplateDocTypeDef.PTI_CL;
        	tempDoc.setPermitDocTypeDsc("Completeness Letter");
        }  else if(templateType.equalsIgnoreCase("NRL")){
        	templateTypeCD = TemplateDocTypeDef.PTI_RL;  
        	tempDoc.setPermitDocTypeDsc("Receipt Letter");
        }	else if(templateType.equalsIgnoreCase("CNL")){
        	templateTypeCD = TemplateDocTypeDef.PTI_CNL;  
        	tempDoc.setPermitDocTypeDsc("Company Notice Letter");
        }	else if(templateType.equalsIgnoreCase("PND")){
        	templateTypeCD = TemplateDocTypeDef.PTI_PND; 
        	tempDoc.setPermitDocTypeDsc("Public Notice Document");
        }	else if(templateType.equalsIgnoreCase("PAA")){
        	templateTypeCD = TemplateDocTypeDef.PTI_PAA; 
        	tempDoc.setPermitDocTypeDsc("Permit Analysis Document");
        }	else if(templateType.equalsIgnoreCase("PDO")){
        	templateTypeCD = TemplateDocTypeDef.PTI_PID; 
        	tempDoc.setPermitDocTypeDsc("Permit Document Information");
        }

       
     	tempDoc = generateTempDoc(tempDoc, templateDoc.getTemplateDocTypeCD());   
     	
     	//System.out.println("tempDoc: "+tempDoc.getGeneratedDocumentPath());
     	
     	return "dialog:openGenDoc";
    }
    
    public final void applyEditDoc(ActionEvent actionEvent) {
    	if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
        	applyEditDocInternal(actionEvent);
        } finally {
            clearButtonClicked();
        }
    }
    
    private final void applyEditDocInternal(ActionEvent actionEvent) {
        if (!isPermitAttachments()) {
            DisplayUtil
                    .displayError("You are not authorized to modify this permit");
            return;
        }
         

        tempDoc.setPermitId(permit.getPermitID());
        tempDoc.setFacilityID(permit.getFacilityId());
        tempDoc.setLastModifiedBy(getCurrentUserID());
        if (docBeingModified != null) {
            // update
            permit.getDocuments().remove(docBeingModified);
            permit.getDocuments().add(tempDoc);
        } else if (docBeingCloned != null) {
            // clone
            try {
                tempDoc = getPermitService().cloneTempDocument(docBeingCloned, tempDoc);
            } catch (RemoteException ex) {
                DisplayUtil.displayError("cannot clone document");
                handleException(ex);
                return;
            }

            permit.getDocuments().add(tempDoc);
        } else if (isDocUpload) {
            // upload
            if (fileToUpload == null) {
                DisplayUtil.displayWarning("Please select a file to upload");
                return;
            }
			if (fileToUpload != null && !DocumentUtil.isValidFileExtension(fileToUpload)){
				DocumentUtil.invalidFileExtensionMessage(null);
				return;
			}

            try {
                tempDoc.setExtension(DocumentUtil.getFileExtension(fileToUpload
                        .getFilename()));
                tempDoc = getPermitService().uploadTempDocument(tempDoc,
                        fileToUpload.getInputStream());
            } catch (RemoteException ex) {
                DisplayUtil.displayError("cannot upload document");
                handleException(ex);
                return;
            } catch (IOException e) {
                DisplayUtil.displayError("cannot get upload document");
                logger.error("cannot get upload document: " + e, e);
                return;
            }

            fileToUpload = null;
//            Bug fix for TFS task - 4621
//            if (singleDoc != null) {
//                permit.getDocuments().remove(singleDoc);
//                singleDoc = null;
//            }
            permit.getDocuments().add(tempDoc);
        } else {
            String templateTypeCD;

            if (permit instanceof PTIOPermit) {
                if (((PTIOPermit) permit).isTv()) {
                    templateTypeCD = TemplateDocTypeDef.PTI_T_AND_C;
                } else {
                    templateTypeCD = TemplateDocTypeDef.PTIO_T_AND_C;
                }
            //} else if (PermitTypeDef.TIV_PTO.endsWith(permit.getPermitType())){
            //   templateTypeCD = TemplateDocTypeDef.TIVPTO_T_AND_C;
            } else {
                templateTypeCD = TemplateDocTypeDef.TVPTO_T_AND_C;
            }

            if ((tempDoc = generateTempDoc(tempDoc, templateTypeCD)) != null) {
                permit.getDocuments().add(tempDoc);
                if (topTCDoc != null)
                    topTCDoc.setLastModifiedTS(new Timestamp(System
                            .currentTimeMillis()));
            }
        }

        handleDocumentsModified();
        updatePermitDocuments();
        organisePotentialPermitDocuments();
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    //public final String uploadTemplate(ActionEvent actionEvent){
    	
    public final void uploadTemplate(ValueChangeEvent ve){
    	//String templatePath=null;
    	 if (ve.getPhaseId() != PhaseId.INVOKE_APPLICATION) {

             ve.setPhaseId(PhaseId.INVOKE_APPLICATION);

             ve.queue();

             return;

    	 }

    	// upload
        if (templateFileToUpload == null) {
            DisplayUtil.displayWarning("Please select a file to upload");
            return;
        }

        try {
           	DocumentUtil.createTemplate(templateFileToUpload.getFilename(),templateFileToUpload.getInputStream());
        } catch (RemoteException ex) {
            DisplayUtil.displayError("cannot upload document");
            handleException(ex);
            return;
        } catch (IOException e) {
            DisplayUtil.displayError("cannot get upload document");
            logger.error("cannot get upload document: " + e, e);
            return;
        }

        templateFileToUpload = null;        
        
        return;
        
    }
    

    public final void cancelEditDoc(ActionEvent actionEvent) {
    	dialogEdit = false;
        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }

    public final void removeEditDoc(ActionEvent actionEvent) {
        permit.getDocuments().remove(docBeingModified);
        handleDocumentsModified();
        organisePotentialPermitDocuments();
        dialogEdit = false;
        updatePermit();
        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }

    public final void closeViewDoc(ActionEvent actionEvent) {
    	dialogEdit = false;
        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }

    public final String startViewDoc() {
        tempDoc = (PermitDocument) FacesUtil.getManagedBean("doc");
        return "dialog:permitDoc";
    }

    private List<ValidationMessage> validatePermitIssuance() throws RemoteException {
    	List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
    	
    	
		try {
			reloadPermit();

			PermitIssuance iss = new PermitIssuance();
			if (PermitIssuanceTypeDef.isValid(currentIssuanceAction)) {
				if (currentIssuanceAction
						.equalsIgnoreCase(PermitIssuanceTypeDef.Draft)) {
					// messages =
					// PermitValidation.validateDraftIssuance(permit);
					messages = PermitValidation
							.validatePublicNoticePackage(permit);
					iss = permit.getDraftIssuance();
				} else if (currentIssuanceAction
						.equalsIgnoreCase(PermitIssuanceTypeDef.Final)) {
					messages = PermitValidation.validateFinalIssuance(permit);
					iss = permit.getFinalIssuance();
				} else if (currentIssuanceAction
						.equalsIgnoreCase(PermitIssuanceTypeDef.PP)) {
					// messages = PermitValidation.validatePPIssuance(permit);
					messages = PermitValidation
							.validatePreparePPPackage(permit);
					iss = ((TVPermit) permit).getPpIssuance();
				} // else if (currentIssuanceAction
					// .equalsIgnoreCase(PermitIssuanceTypeDef.PPP)) {
					// messages = PermitValidation.validatePPPIssuance(permit);
					// iss = ((TVPermit) permit).getPppIssuance();
					// }
			} else {
				messages.add(new ValidationMessage("Permit", "Issuance type "
						+ currentIssuanceAction + " is incorrect type.",
						ValidationMessage.Severity.ERROR));
			}

			if (!AppValidationMsg.validate(messages, true)) {
				iss.setIssuanceStatusCd(IssuanceStatusDef.NOT_READY);
				DisplayUtil.displayInfo("Status changed to 'Not Ready'.");
				updatePermit();
			} else {
				Object close_validation_dialog = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
            	if (close_validation_dialog != null && (messages != null && messages.isEmpty())) {
            		FacesUtil.startAndCloseModelessDialog(AppValidationMsg.VALIDATION_RESULTS_URL);
            		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
            	}
			}
		} catch (Exception ex) {
			throw new RemoteException(
					"an error occurred during Permit validation.");
		}

		return messages;
    }
    
    /*
     * Actions - issuance
     */
    public final String validateIssuance() {
		boolean operationOk = true;
		try {
			validatePermitIssuance();
		} catch (Exception e) {
			operationOk = false;
		}

		if (!operationOk) {
			DisplayUtil
			.displayError("An error occurred when validating issuance/publication.");
		} 

        return null;
    }

    public final String skipIssuance() {
        PermitIssuance pi = null;
        // from JSP page: rendered="#{permitDetail.permit.draftIssuanceStatusCd != permitReference.issuanceStatusReady && permitDetail.permit.draftIssuanceStatusCd != permitReference.issuanceStatusSkipped}"
        // from JSP page: rendered="#{permitDetail.permit.finalIssuanceStatusCd != permitReference.issuanceStatusReady && permitDetail.permit.finalIssuanceStatusCd != permitReference.issuanceStatusSkipped}"
        // from JSP page: rendered="#{permitDetail.permit.pppIssuanceStatusCd != permitReference.issuanceStatusReady && permitDetail.permit.pppIssuanceStatusCd != permitReference.issuanceStatusSkipped}"
        // from JSP page: rendered="#{permitDetail.permit.ppIssuanceStatusCd != permitReference.issuanceStatusReady && permitDetail.permit.ppIssuanceStatusCd != permitReference.issuanceStatusSkipped}"
        if (currentIssuanceAction.equalsIgnoreCase(PermitIssuanceTypeDef.Draft)) {
            // permit.setDraftIssuanceStatusCd(IssuanceStatusDef.SKIPPED);
            pi = permit.getDraftIssuance();
        } else if (currentIssuanceAction
                .equalsIgnoreCase(PermitIssuanceTypeDef.PP)) {
            TVPermit tp = (TVPermit) permit;
            // tp.setPpIssuanceStatusCd(IssuanceStatusDef.SKIPPED);
            pi = tp.getPpIssuance();
        //} else if (currentIssuanceAction
        //        .equalsIgnoreCase(PermitIssuanceTypeDef.PPP)) {
        //    TVPermit tp = (TVPermit) permit;
            // tp.setPppIssuanceStatusCd(IssuanceStatusDef.SKIPPED);
        //    pi = tp.getPppIssuance();
        } else if (currentIssuanceAction
                .equalsIgnoreCase(PermitIssuanceTypeDef.Final)) {
            // permit.setFinalIssuanceStatusCd(IssuanceStatusDef.SKIPPED);
            pi = permit.getFinalIssuance();
        }
        if (pi != null)
            resetIssuance(pi);

        permit.setCurrentIssuanceDoc(currentIssuanceAction);
        PermitDocument doc = permit.getCurrentIssuanceDoc();
        if (doc != null)
            permit.getDocuments().remove(doc);

        updatePermit();
        return null;
    }

    /**
     * Used to prepare documents for issuance
     * @deprecated No longer used within IMPACT
     * @return
     */
    @Deprecated
    public final String prepareIssuance() {
        try {
            getPermitService().prepareIssuance(permit.getPermitID(),
                    getCurrentUserID(), currentIssuanceAction);

            loadPermit(permit.getPermitID());

        } catch (RemoteException ex) {
            DisplayUtil.displayError("Cannot prepare : " + ex.getMessage());
        }

        return FacesUtil.getCurrentPage(); // stay on same page
    }

    public final String markReadyIssuance() {
		boolean operationOk = true;
		try {
			getPermitService().markReadyIssuance(permit.getPermitID(),
					currentIssuanceAction, getCurrentUserID());
			loadPermit(permit.getPermitID());
			validatePermitIssuance();
		} catch (RemoteException ex) {
			operationOk = false;
			DisplayUtil.displayError("Cannot mark ready; " + ex.getMessage());
			handleException(ex);
		}

		if (!operationOk) {
			// validation may have failed
			unprepareIssuance();
		}

		return null; // stay on same page
	}

    public final String unprepareIssuance() {
        try {
            getPermitService().unprepareDraftIssuance(permit.getPermitID(),
                    currentIssuanceAction, getCurrentUserID());

            loadPermit(permit.getPermitID());
        } catch (RemoteException ex) {
            DisplayUtil.displayError("Cannot unmark (unprepare) issuance "
                    + ex.getMessage());
            handleException(ex);
        }

        return FacesUtil.getCurrentPage(); // stay on same page
    }

    public final String finalizeIssuance() {
        try {
        	validatePermitIssuance();
            User user = InfrastructureDefs.getPortalUser();
            ValidationMessage vm = getPermitService().finalizeIssuance(
                    permit.getPermitID(), currentIssuanceAction, true,
                    getCurrentUserID(), user);

            if (vm != null) {
                DisplayUtil.displayWarning(vm.getMessage());
            }

            DisplayUtil
                    .displayWarning("Please check-in the task from task profile.");

            loadPermit(permit.getPermitID());
        } catch (RemoteException ex) {
            DisplayUtil.displayError("Cannot finalize issuance "
                    + ex.getMessage());
            handleException(ex);
        }

        return null; // stay on same page
    }

    public final void finalizeIssuance(ReturnEvent returnEvent) {

        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");

        boolean updateProfile = true;
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.YES)) {
            try {
            	validatePermitIssuance();
                User user = InfrastructureDefs.getPortalUser();
                getPermitService().finalizeIssuance(permit.getPermitID(),
                        currentIssuanceAction, updateProfile,
                        getCurrentUserID(), user);

                DisplayUtil
                        .displayWarning("Please check-in the task from task profile.");

                loadPermit(permit.getPermitID());
            } catch (RemoteException ex) {
                DisplayUtil.displayError("Cannot finalize issuance "
                        + ex.getMessage());
                handleException(ex);
            }
        }
    }
    
    /**
     * 
     * @return
     */
    public final void deleteIssuance(ReturnEvent returnEvent) {
        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO))
            return;

        PermitIssuance pi = null;
        if (currentIssuanceAction.equalsIgnoreCase(PermitIssuanceTypeDef.Draft)) {
            pi = permit.getDraftIssuance();
        } else if (currentIssuanceAction
                .equalsIgnoreCase(PermitIssuanceTypeDef.PP)) {
            TVPermit tp = (TVPermit) permit;
            pi = tp.getPpIssuance();
        //} else if (currentIssuanceAction
        //        .equalsIgnoreCase(PermitIssuanceTypeDef.PPP)) {
        //    TVPermit tp = (TVPermit) permit;
        //    pi = tp.getPppIssuance();
        } else if (currentIssuanceAction
                .equalsIgnoreCase(PermitIssuanceTypeDef.Final)) {
            pi = permit.getFinalIssuance();
            permit.setEffectiveDate(null);
            permit.setExpirationDate(null);
        }
        if (pi != null) {
            resetIssuance(pi);
            permit.setCurrentIssuanceDoc(currentIssuanceAction);
            PermitDocument doc = permit.getCurrentIssuanceDoc();
            if (doc != null)
                permit.getDocuments().remove(doc);

            modifyReloadPermit(permit);
        }

    }

    /**
     * To reissue a draft issuance.
     * 
     * @return
     */
    public final void reissueDraft(ReturnEvent returnEvent) {
        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO))
            return;

        permit.setCurrentIssuanceDoc(PermitIssuanceTypeDef.Draft);
        PermitIssuance pi = permit.getDraftIssuance();
        pi.setIssuanceTypeCd(PermitIssuanceTypeDef.Draft);
        resetIssuance(pi);
        PermitDocument doc = permit.getCurrentIssuanceDoc();
        if (doc != null)
            doc.setPermitDocTypeCD(PermitDocTypeDef.PREVIOUSLY_ISSUED);
        
        permit.setPublicNoticeNewspaperCd(null);
        
        //reset permit global status to none
        permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.NONE);
        modifyReloadPermit(permit);

    }
    
    /**
     * To reissue a PP issuance.
     * 
     * @return
     */
    public final void reissuePp(ReturnEvent returnEvent) {
        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO))
            return;

        permit.setCurrentIssuanceDoc(PermitIssuanceTypeDef.PP);
        PermitIssuance pi = ((TVPermit)permit).getPpIssuance();
        pi.setIssuanceTypeCd(PermitIssuanceTypeDef.PP);
        resetIssuance(pi);
        PermitDocument doc = permit.getCurrentIssuanceDoc();
        if (doc != null)
            doc.setPermitDocTypeCD(PermitDocTypeDef.PREVIOUSLY_ISSUED);
        ((TVPermit)permit).setUsepaCompleteDate(null);
        ((TVPermit)permit).setUsepaExpedited(false);
        ((TVPermit)permit).setUsepaOutcomeCd(USEPAOutcomeDef.NO_RESPONSE);
        ((TVPermit)permit).setUsepaReceivedPermitDate(null);
        ((TVPermit)permit).setUsepaPermitSentDate(null);
    
        //reset permit global status to issued draft
        permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.ISSUED_DRAFT);
        modifyReloadPermit(permit);

    }
    
    /**
     * To reissue a draft issuance.
     * 
     * @return
     */
    /*
    public final void reissuePpp(ReturnEvent returnEvent) {
        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO))
            return;

        permit.setCurrentIssuanceDoc(PermitIssuanceTypeDef.PPP);
        PermitIssuance pi = ((TVPermit)permit).getPppIssuance();
        pi.setIssuanceTypeCd(PermitIssuanceTypeDef.PPP);
        resetIssuance(pi);
        PermitDocument doc = permit.getCurrentIssuanceDoc();
        if (doc != null)
            doc.setPermitDocTypeCD(PermitDocTypeDef.PREVIOUSLY_ISSUED);
        ((TVPermit)permit).setPppReviewWaived(false);

        modifyReloadPermit(permit);

    }
    */
    private void resetIssuance(PermitIssuance pi) {
        pi.setIssuanceDate(null);
        pi.setIssuanceTypeCd(currentIssuanceAction);
        pi.setIssuanceStatusCd(IssuanceStatusDef.NOT_READY);
        pi.setPublicNoticeRequestDate(null);
        pi.setPublicNoticePublishDate(null);
        pi.setPublicCommentEndDate(null);
        pi.setHearingRequested(false);
        pi.setHearingRequestedDate(null);
        pi.setHearingNoticeRequestDate(null);
        pi.setHearingNoticePublishDate(null);
        pi.setHearingDate(null);
        pi.setNewspaperAffidavitDate(null);
        removeIssuanceDoc(currentIssuanceAction);
    }

    private void removeIssuanceDoc(String currentIssuanceType) {
        List<PermitDocument> docs = permit.getDocuments();
        List<PermitDocument> newDocs = new ArrayList<PermitDocument>();
        String cStage = PermitDocIssuanceStageDef.getStage(currentIssuanceType);
        for (PermitDocument doc : docs) {
            if (doc.getIssuanceStageFlag() == null)
                newDocs.add(doc);
            else if (doc.getPermitDocTypeCD().equalsIgnoreCase(
                    PermitDocTypeDef.ISSUANCE_DOCUMENT))
                newDocs.add(doc);
            // 2565
            else if (doc.getPermitDocTypeCD().equalsIgnoreCase(
                    PermitDocTypeDef.PUBLIC_NOTICE))
                newDocs.add(doc);
            else if (!doc.getIssuanceStageFlag().equalsIgnoreCase(cStage)) {
                newDocs.add(doc);
            }
        }
        permit.setDocuments(newDocs);
    }

    /*
     * Private functions
     */
    @SuppressWarnings("unchecked")
    private void createTree() {
        ArrayList<String> treePath = new ArrayList<String>();
        String pathSeparator = ":";

        Stars2TreeNode facNode = new Stars2TreeNode(PERMIT_NODE_TYPE, permit
                .getPermitNumber(), "", false, permit);
        treePath.add("0");
        selectedTreeNode = facNode;

        int euGroupNodeNum = 1;
        PermitEUGroup curEUGroup = null;
        if (selectedEUGroup != null) {
            curEUGroup = selectedEUGroup;
        }
        if (selectedEU != null) {
            curEUGroup = selectedEU.getEuGroup();
        }
        boolean passEUGroup = false;
        if (curEUGroup != null
                && permit.getEuGroups().indexOf(curEUGroup) > COLLAPSED_INDEX) {
            treePath.add("0:" + Integer.toString(euGroupNodeNum));
            Stars2TreeNode dummyNode = new Stars2TreeNode(
                    MORE_GROUPS_NODE_TYPE, " ", "More EU Groups", true, " ");
            facNode.getChildren().add(dummyNode);
            passEUGroup = true;
        }

        excludedEUs = new ArrayList<EmissionUnit>();
        excludedEUs.addAll(allEUs);
        for (PermitEUGroup euGroup : permit.getEuGroups()) {
            if (passEUGroup && !euGroup.equals(curEUGroup)) {
                for (PermitEU eu : euGroup.getPermitEUs())
                    removeFpEU(eu.getFpEU());
                continue;
            }
            passEUGroup = false;

            Stars2TreeNode euGroupNode = null;
            if (euGroup.isIndividualEUGroup()) {
                euGroupNode = facNode;
                individualEUGroup = euGroup;
            } else
                euGroupNode = new Stars2TreeNode(EU_GROUP_NODE_TYPE, euGroup
                        .getName(), Integer.toString(euGroup
                        .getPermitEUGroupID()), false, euGroup);

            if (euGroup.equals(selectedEUGroup))
                selectedTreeNode = euGroupNode;

            int euNodeNum = 0;
            boolean passEU = false;
            if (selectedEU != null
                    && euGroup.getPermitEUs().indexOf(selectedEU) > COLLAPSED_INDEX) {
                treePath.add("0:" + Integer.toString(euGroupNodeNum)
                        + pathSeparator + Integer.toString(euNodeNum++));
                Stars2TreeNode dummyNode = new Stars2TreeNode(
                        MORE_EUS_NODE_TYPE, " ", "More EUs", true, " ");
                euGroupNode.getChildren().add(dummyNode);
                passEU = true;
            }
            for (PermitEU eu : euGroup.getPermitEUs()) {
                eu.setEuGroup(euGroup);
                removeFpEU(eu.getFpEU());

                if (passEU && !eu.equals(selectedEU))
                    continue;
                passEU = false;

                Stars2TreeNode euNode = new Stars2TreeNode(EU_NODE_TYPE, eu
                        .getFpEU().getEpaEmuId(), Integer.toString(eu
                        .getPermitEUID()), false, eu);
                if (eu.equals(selectedEU))
                    selectedTreeNode = euNode;

                if (!euGroup.isIndividualEUGroup()) {
                    treePath.add("0:" + Integer.toString(euGroupNodeNum)
                            + pathSeparator + Integer.toString(euNodeNum++));
                } else {
                    treePath.add("0:" + Integer.toString(euGroupNodeNum++));
                }

                euGroupNode.getChildren().add(euNode);
            }

            if (!euGroup.isIndividualEUGroup()) {
                treePath.add("0:" + Integer.toString(euGroupNodeNum++));
                facNode.getChildren().add(euGroupNode);
            }
        }

        int fpEUNodeNum = euGroupNodeNum;
        for (EmissionUnit fpEU : excludedEUs) {
            if (fpEU == null)
                continue;
            Stars2TreeNode excludedEUNode = new Stars2TreeNode(
                    EXCLUDE_EU_NODE_TYPE, fpEU.getEpaEmuId(), Integer
                            .toString(fpEU.getEmuId()), true, fpEU);
            treePath.add("0:" + Integer.toString(fpEUNodeNum++));
            if (fpEU.equals(selectedExcludedEU))
                selectedTreeNode = excludedEUNode;
            facNode.getChildren().add(excludedEUNode);
        }

        TreeStateBase treeState = new TreeStateBase();
        treeState.expandPath(treePath.toArray(new String[0]));
        treeData = new TreeModelBase(facNode);
        treeData.setTreeState(treeState);
    }

    private void removeFpEU(EmissionUnit fpEU) {
        ArrayList<EmissionUnit> tEUs = new ArrayList<EmissionUnit>();
        for (EmissionUnit teu : excludedEUs)
            if (!fpEU.getCorrEpaEmuId().equals(teu.getCorrEpaEmuId()))
                tEUs.add(teu);
        excludedEUs = tEUs;
    }

    private void selectTreeRoot() {
        selectedTreeNode = (Stars2TreeNode) treeData.getNodeById("0"); // root
        selectedEU = null;
        selectedEUGroup = null;
        selectedExcludedEU = null;
    }
/*
    public final boolean isConvertButton() {
        return permit.getPermitType().equalsIgnoreCase(PermitTypeDef.NSR)
                && permit.getPermitGlobalStatusCD().equalsIgnoreCase(
                        PermitGlobalStatusDef.ISSUED_FINAL)
                && !((PTIOPermit) permit).isConvertedToPTI()
                && InfrastructureDefs.getCurrentUserAttrs()
                        .isCurrentUseCaseValid("permits.detail.convertToPTI");
    }
*/
/*
    public final void convertToPTI(ReturnEvent returnEvent) {
        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO))
            return;
        
        try {
            getPermitService().convertToPTI(permit, getCurrentUserID());
        } catch (RemoteException ex) {
            DisplayUtil.displayError("cannot modify permit ");
            handleException(ex);
        }
        reloadPermit();
    }
*/
/*
    public final void switchTo(ReturnEvent returnEvent) {
        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO))
            return;

        PTIOPermit tp = (PTIOPermit) permit;
        if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.NSR)) {
            tp.setTv(true);
            tp.setFeptio(false);
            tp.setFePtioTvAvoid(false);
        //} else if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TVPTI)) {
        //    tp.setTv(false);
        //    tp.setMajorNonAttainment(false);
        }
        if (PublicNoticeTypeDef.STANDARD_WORDING.equalsIgnoreCase(permit
                .getPublicNoticeType()) || PublicNoticeTypeDef.PSD_STANDARD_WORDING.equalsIgnoreCase(permit
                        .getPublicNoticeType())){
        	setPublicNoticeText(permit.getPublicNoticeType());
        }

        modifyReloadPermit(permit);
    }
*/
    /**
     * @param inPermit
     * @return
     */
    private boolean modifyPermitDocuments(Permit inPermit) {
    	boolean ok = true;
        try {
            getPermitService().modifyPermitDocuments(inPermit);
        } catch (RemoteException ex) {
            ok = false;
            DisplayUtil.displayError("cannot modify permit documents");
            handleException(ex);
        }
        return ok;
    }
    
    /**
     * @param inPermit
     * @return
     */
    private boolean modifyReloadPermit(Permit inPermit) {
        boolean ok = true;
        try {
        	if (StringUtils.isNotBlank(permit.getLegacyPermitNumber())) {
				if (getPermitService().isDuplicateLegacyPermitNumber(permit.getLegacyPermitNumber(),permit.getPermitNumber())) {
					String msg = "The legacy permit number already exists in the system.";
					throw new DAOException(msg,msg);
				}
        	}
            setSubpartCDs(inPermit);
            ok = validateSubparts(inPermit);
            
            if(ok) {
            getPermitService().modifyPermit(inPermit, getCurrentUserID());
            // looks like modifyPermit already modify EUs
            /*
             * if (inPermit instanceof TVPermit) for (PermitEUGroup eug :
             * inPermit.getEuGroups()) for (PermitEU eu : eug.getPermitEUs())
             * getPermitService().modifyEU(eu, getUserID());
             */
            }

        } catch (RemoteException ex) {
            ok = false;
            DisplayUtil.displayError("cannot modify permit ");
            handleException(ex);
        }
        if(ok){
        	reloadPermit();
        }
        return ok;
    }
    
    private boolean validateSubparts(Permit permit) {
    	boolean ret = true;
    	List<String> nspsSubparts = permit.getNspsSubpartCDs();
    	List<String> neshapPart61Subparts = permit.getPart61NESHAPSubpartCDs();
    	List<String> neshapPart63Subparts = permit.getPart63NESHAPSubpartCDs();
    	
    	boolean nspsHasDuplicate = false;
    	boolean neshapPart61HasDuplicate = false;
    	boolean neshapPart63HasDuplicate = false;
    	
    	if(nspsSubparts != null) {
    		nspsHasDuplicate = Utility.hasDuplicate(nspsSubparts);
    		if(nspsHasDuplicate) {
    			ret = false;
    			DisplayUtil.displayError("You cannot add duplicate NSPS subparts.");
    		}
    	}
    	
    	if(neshapPart61Subparts != null) {
    		neshapPart61HasDuplicate = Utility.hasDuplicate(neshapPart61Subparts);
    		if(neshapPart61HasDuplicate) {
    			ret = false;
    			DisplayUtil.displayError("You cannot add duplicate Part 61 NESHAP subparts.");
    		}
    	}
    	
    	if(neshapPart63Subparts != null) {
    		neshapPart63HasDuplicate = Utility.hasDuplicate(neshapPart63Subparts);
    		if(neshapPart63HasDuplicate) {
    			ret = false;
    			DisplayUtil.displayError("You cannot add duplicate Part 63 NESHAP subparts.");
    		}
    	}
    	
    	
    	return ret;
    }

    private void setSubpartCDs(Permit inPermit) {
        List<String> sub = inPermit.getMactSubpartCDs();
        sub.clear();
        for (Stars2Object s : mactSubparts) {
        	if (s.getValue() != null) {
        		sub.add(s.getValue().toString());
        	}
        }

        sub = inPermit.getNeshapsSubpartCDs();
        sub.clear();
        for (Stars2Object s : neshapsSubparts) {
        	if (s.getValue() != null) {
        		sub.add(s.getValue().toString());
        	}
        }

        sub = inPermit.getNspsSubpartCDs();
        sub.clear();
        for (Stars2Object s : nspsSubparts) {
        	if (s.getValue() != null) {
        		sub.add(s.getValue().toString());
        	}
        }
        
        sub = inPermit.getPart61NESHAPSubpartCDs();
        sub.clear();
        for (Stars2Object s : part61NESHAPSubparts) {
        	if (s.getValue() != null) {
        		sub.add(s.getValue().toString());
        	}
        }
        
        sub = inPermit.getPart63NESHAPSubpartCDs();
        sub.clear();
        for (Stars2Object s : part63NESHAPSubparts) {
        	if (s.getValue() != null) {
        		sub.add(s.getValue().toString());
        	}
        }
    }

    public final String loadPermit() {
        if (permit != null) {
            if (
            		//permit.getPermitType().equalsIgnoreCase(
                    //        PermitTypeDef.RPE)
                    //|| 
                    permit.getPermitType().equalsIgnoreCase(
                            PermitTypeDef.RPR)) {
                return setUpApplication("");
            }

            List<String> ss = new ArrayList<String>();
            for (SelectItem si : getPermitReasons()) {
                ss.add((String) si.getValue());
            }
            for (String s : permit.getPermitReasonCDs()) {
                if (!ss.contains(s)) {
                    DisplayUtil.displayError("Permit reason is not correct.");
                    return ERROR;
                }
            }

            return PermitValidation.PERMIT_PROFILE_OUTCOME;
        }
        DisplayUtil.displayError("Permit is not loaded.");
        return ERROR;
    }
    
    public final String loadPermitFromPermitConditionSearch(){

        loadPermit(tempPermitId);
        SimpleMenuItem.setDisabled("menuItem_permitCurrentWorkflow",
                !getFromTODOList());
    	tempPermitId = null;
    	return loadPermit();
    }

    private void loadPermit(int permitID) {
        PermitInfo info;
        reset();
        try {
            info = getPermitService().retrievePermit(permitID);
            permit = info.getPermit();
			if (permit instanceof PTIOPermit) {
				this.NSRFixedChargeWrapper = new TableSorter();
				this.NSRFixedChargeWrapper
						.setWrappedData(((PTIOPermit) this.permit)
								.getNSRFixedChargeList());
				
				this.NSRTimeSheetRowWrapper = new TableSorter();
				this.NSRTimeSheetRowWrapper
						.setWrappedData(((PTIOPermit) this.permit)
								.getNSRTimeSheetRowList());
								
				this.permitChargePaymentWrapper = new TableSorter();
				this.permitChargePaymentWrapper
						.setWrappedData(((PTIOPermit) this.permit)
								.getPermitChargePaymentList());
				
				refreshAreaEmissionsOffsetList();
				
				
			}
			
			// Permit Conditions
			this.permitConditionsWrapper = new TableSorter();
			this.permitConditionsWrapper
					.setWrappedData((this.permit)
							.getPermitConditionList());
			
			
            SimpleMenuItem.setDisabled("menuItem_permitDetail", false);
            /*
            if (permit instanceof TIVPermit){
                Application recentApp = null;
                for (Application ta : permit.getApplications()){
                    if (!ApplicationTypeDef.TITLE_IV_APPLICATION.equals(ta.getApplicationTypeCD()))
                        continue;
                    if (recentApp == null)
                        recentApp = ta;
                    else if (recentApp.getSubmittedDate().before(ta.getSubmittedDate()))
                        recentApp = ta;
                }
                if (recentApp != null){
                    ApplicationDocumentRef[] appDocs = getApplicationService().retrieveApplicationDocuments(recentApp.getApplicationID());
                    for (ApplicationDocumentRef tdoc : appDocs) {
                        if (ApplicationDocumentTypeDef.TITLE_IV_ACID_RAIN_APP.equals(tdoc.getApplicationDocumentTypeCD())) {
                            if (tdoc.getDocumentId() != null) {
                            	// mantis 2826 - set recentAppDoc to most recently uploaded document
                            	if (recentAppDoc == null) {
                            		recentAppDoc = getDocumentService().getDocumentByID(tdoc.getDocumentId(), false);
                            	} else {
                            		Document tmpDoc = getDocumentService().getDocumentByID(tdoc.getDocumentId(), false);
                            		if (tmpDoc.getLastModifiedTS().after(recentAppDoc.getLastModifiedTS())) {
                            			recentAppDoc = tmpDoc;
                            		}
                            	}
                            }
                        }
                    }
                }
            }
            */
        } catch (RemoteException ex) {
            permit = null;
            SimpleMenuItem.setDisabled("menuItem_permitDetail", true);
            DisplayUtil.displayError("cannot load permit");
            handleException(ex);
            return;
        }

        currentFacility = info.getCurrentFacility();
        allEUs = info.getPermittableEUs();
        
        setupScreen();

    }
    
    public void loadPermit(ActionEvent e) {
    	AppValidationMsg msg = (AppValidationMsg) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get( "msg");  

    	PermitInfo info;
    	reset();
    	if (msg.getPermitId() != null) {
    		try {
    			info = getPermitService().retrievePermit(msg.getPermitId());
    			permit = info.getPermit();
				if (permit instanceof PTIOPermit) {
					this.NSRFixedChargeWrapper = new TableSorter();
					this.NSRFixedChargeWrapper
							.setWrappedData(((PTIOPermit) this.permit)
									.getNSRFixedChargeList());
					
					this.NSRTimeSheetRowWrapper = new TableSorter();
					this.NSRTimeSheetRowWrapper
							.setWrappedData(((PTIOPermit) this.permit)
									.getNSRTimeSheetRowList());
									
					this.permitChargePaymentWrapper = new TableSorter();
					this.permitChargePaymentWrapper
							.setWrappedData(((PTIOPermit) this.permit)
									.getPermitChargePaymentList());
					
					refreshAreaEmissionsOffsetList();

					
				}
				
				// Permit Conditions
				this.permitConditionsWrapper = new TableSorter();
				this.permitConditionsWrapper
						.setWrappedData((this.permit)
								.getPermitConditionList());
				
    			SimpleMenuItem.setDisabled("menuItem_permitDetail", false);
    		} catch (RemoteException ex) {
    			permit = null;
    			SimpleMenuItem.setDisabled("menuItem_permitDetail", true);
    			DisplayUtil.displayError("cannot load permit");
    			handleException(ex);
    			return;
    		}

    		currentFacility = info.getCurrentFacility();
    		allEUs = info.getPermittableEUs();

    		setupScreen();
    		FacesUtil.refreshMainWindow();
    	}

    }

    private void loadComments(int permitID) {
        try {
        	Note[] dapcComments = getPermitService().retrievePermitComments(permitID);
            permit.setDapcComments(dapcComments);
        } catch (RemoteException ex) {
            DisplayUtil.displayError("cannot load permit comments");
            handleException(ex);
            return;
        }
    }
    
    private void setupScreen() {
        mactSubparts = Stars2Object.toStar2Object(permit.getMactSubpartCDs());
        neshapsSubparts = Stars2Object.toStar2Object(permit
                .getNeshapsSubpartCDs());
        nspsSubparts = Stars2Object.toStar2Object(permit.getNspsSubpartCDs());
        part61NESHAPSubparts = Stars2Object.toStar2Object(permit
                .getPart61NESHAPSubpartCDs());
        part63NESHAPSubparts = Stars2Object.toStar2Object(permit
                .getPart63NESHAPSubpartCDs());
        
        handleEUGroupChange(null, null);

        if (getEditAllowed()) {
            corrEpaEMUID = null;
            List<PermitEU> eus = permit.getEus();
            for (PermitEU pe : eus)
                if (pe != null)
                    corrEpaEMUID = pe.getCorrEpaEMUID();

            /*
             * if (corrEpaEMUID == null){ String msg = "There are no EUs
             * currently selected to be in this permit"; if (eus == null || (eus !=
             * null && eus.size() == 0)) DisplayUtil.displayInfo(msg); else
             * DisplayUtil.displayError(msg); }
             */
        }

        handleDocumentsModified();
        organisePotentialPermitDocuments();
        createTree();
        selectTreeRoot();
        boolean disabled;
        if (PermitTypeDef.TV_PTO.equalsIgnoreCase(permit.getPermitType()) 
        		//||
                //PermitTypeDef.TIV_PTO.equalsIgnoreCase(permit.getPermitType())
                )
            disabled = false;
        else
            disabled = true;

        /*
         * if
         * (permit.getPermitReasonCDs().contains(PermitReasonsDef.OFF_PERMIT_CHANGE)){
         * SimpleMenuItem.setDisabled("menuItem_permitDraftIssuance", true);
         * }else{ SimpleMenuItem.setDisabled("menuItem_permitDraftIssuance",
         * false); }
         */
        SimpleMenuItem.setDisabled("menuItem_permitPPPIssuance", disabled);
        SimpleMenuItem.setDisabled("menuItem_permitPPIssuance", disabled);
        SimpleMenuItem.setDisabled("menuItem_permitBATSummary", !disabled);

        String gs = permit.getPermitGlobalStatusCD();
        if (PermitGlobalStatusDef.DENIAL_PENDING.equalsIgnoreCase(gs)
                || PermitGlobalStatusDef.ISSUED_DENIAL.equalsIgnoreCase(gs)
                || PermitGlobalStatusDef.ISSUED_FINAL.equalsIgnoreCase(gs)
                || PermitGlobalStatusDef.DEAD_ENDED.equalsIgnoreCase(gs))
            deniable = false;
        else
            deniable = true;

        if (PermitGlobalStatusDef.DENIAL_PENDING.equalsIgnoreCase(gs)
                || PermitGlobalStatusDef.ISSUED_DENIAL.equalsIgnoreCase(gs)) {

            disabled = false;

            GeneralIssuance gi = (GeneralIssuance) FacesUtil
                    .getManagedBean("generalIssuance");
            boolean ret = false;
            if (PermitGlobalStatusDef.DENIAL_PENDING.equalsIgnoreCase(gs))
                ret = true;

            gi.loadIssuances(permit);
            gi.setIssuanceTypes(GenericIssuanceTypeDef.getTypes(permit,
                            permit));
            gi.setIssuanceAllow(ret);
            gi.setBean(this);
        } else
            disabled = true;
        SimpleMenuItem.setDisabled("menuItem_permitDenialIssuance", disabled);

        if (permit instanceof PTIOPermit)
            disabled = false;
        else
            disabled = true;
        SimpleMenuItem.setDisabled("menuItem_permitFeeSummary", disabled);
        SimpleMenuItem.setDisabled("menuItem_permitOffsetTracking", disabled);

        //if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.NSR)) {
        //    switchButtonText = "Change to PTI";
        //} else if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TVPTI))
        //    switchButtonText = "Change to PTIO";
       // else
            switchButtonText = null;
    }

    private void handleDocumentsModified() {
        // attachmentManager = (Attachments) FacesUtil
        // .getManagedBean(ATTACHMENT_MANAGED_BEAN);
        // List<Integer> ds = new ArrayList<Integer>();

        docsMap = new HashMap<String, Map<String, PermitDocument>>();
        attachments = new ArrayList<PermitDocument>();
        tcs = new ArrayList<PermitDocument>();
        topTCDoc = null;
        for (PermitDocument doc : permit.getDocuments()) {
        	if (doc.getPermitDocTypeCD().equals(PermitDocTypeDef.ATTACHMENT)
                    || doc.getPermitDocTypeCD().equals(
                            PermitDocTypeDef.MULTIMEDIA_LETTER)) {
                attachments.add(doc);
                // ds.add(doc.getDocumentID());
            } else 
            	if (doc.getPermitDocTypeCD().equals(
                    PermitDocTypeDef.TERMS_CONDITIONS)) {
                tcs.add(doc);
                if (topTCDoc == null)
                    topTCDoc = doc;
            } else if (doc.getPermitDocTypeCD().equals(
                    PermitDocTypeDef.PREVIOUSLY_ISSUED)) {
                tcs.add(doc);
                if (topTCDoc == null)
                    topTCDoc = doc;
            } else {
                Map<String, PermitDocument> byType = docsMap.get(doc
                        .getPermitDocTypeCD());
                if (byType == null) {
                    byType = new HashMap<String, PermitDocument>();
                    docsMap.put(doc.getPermitDocTypeCD(), byType);
                }
                byType.put(doc.getIssuanceStageFlag() == null ? "" : doc
                        .getIssuanceStageFlag(), doc);
                
                attachments.add(doc);

                // Also add issuance document into T&C table.
                // Bug fix of 2052
                /*if (doc.getPermitDocTypeCD().equalsIgnoreCase(
                        PermitDocTypeDef.ISSUANCE_DOCUMENT))
                    tcs.add(doc);*/
            }
        }

        // int[] docID = new int[ds.size()];
        // for (int i = 0; i < ds.size(); i++)
        // docID[i] = ds.get(i);
        // attachmentManager.setDocumentIDs(docID);

    }

    public Integer getCurrentUserID() {
        return InfrastructureDefs.getCurrentUserId();
    }

    private PermitDocument generateTempDoc(PermitDocument doc,
            String templateTypeCD) {

        TemplateDocument templateDoc = TemplateDocTypeDef
                .getTemplate(templateTypeCD);

        try {
            doc = getPermitService().generateTempDocument(permit, templateDoc, doc);
            //DisplayUtil.displayInfo("Document generated.");
            return doc;
        } catch (RemoteException ex) {
            if (ex instanceof DAOException && ex.getCause() == null) {
                DisplayUtil.displayError(ex.getMessage());
                return null;
            }
            DisplayUtil.displayError(ex.getMessage() + " "
                    + templateDoc.getPath());
            handleException(ex);
            return null;
        }
    }

    private void leaveEditMode() {
        editMode = false;
        newEUGroup = null;
        newEU = null;
        semiEditMode = false;
    }

    private void handleEUGroupChange(PermitEUGroup euGroupToRemove,
            PermitEUGroup euGroupToAdd) {
        if (euGroupToRemove != null && euGroupToAdd != null) {
            List<PermitEU> eus = euGroupToRemove.getPermitEUs();
            euGroupToAdd.setPermitEUs(eus);
            for (PermitEU eu : eus) {
                eu.setEuGroup(euGroupToAdd);
            }
        }

        if (euGroupToRemove != null) {
            for (PermitEUGroup tpeg : permit.getEuGroups())
                if (tpeg.getPermitEUGroupID().equals(
                        euGroupToRemove.getPermitEUGroupID())) {
                    permit.getEuGroups().remove(tpeg);
                    break;
                }
        }
        if (euGroupToAdd != null) {
            permit.getEuGroups().add(euGroupToAdd);
        }

        euGroupSelectItems = new ArrayList<SelectItem>();
        for (PermitEUGroup euGroup : permit.getEuGroups()) {
            if (!euGroup.isIndividualEUGroup())
                euGroupSelectItems.add(new SelectItem(euGroup, euGroup
                        .getName()));
        }
    }

    /**
     * @return
     */
    public final List<SelectItem> getPermitReasons() {
        List<String> pr = permit.getPermitReasonCDs();
       // if (permit != null && permit.getPermitType() != null
       //         && permit.getPermitType().equals(PermitTypeDef.TVPTI)) {
       //     pr.remove(PermitReasonsDef.RENEWAL);
       // }
        if (pr.contains(PermitReasonsDef.NOT_ASSIGNED) && pr.size() > 1) {
            pr.remove(PermitReasonsDef.NOT_ASSIGNED);
        }else if (pr.size() == 0)
            pr.add(PermitReasonsDef.NOT_ASSIGNED);

        return PermitReasonsDef.getPermitReasons(permit.getPermitType(), pr);
    }

    /**
     * @return
     * 
     */
    /*public final List<SelectItem> getOtherActivePermitsForFacility() {

    	activePermits = new ArrayList<SelectItem>();

    	try {
    		
    		String dateBy = null;
    		if (permit.getFinalIssueDate() != null) {
        		dateBy = "pif.issuance_date";    			
    		}
    		// PermitLevelStatusDef.ACTIVE
    		List<Permit> ps = getPermitService().search(null, null, null, permit.getPermitType(), null, null, null, null,
                    permit.getFacilityId(), null,
                    PermitGlobalStatusDef.ISSUED_FINAL,
                    dateBy,
                    null, permit.getFinalIssueDate(), null,
                    unlimitedResults(), null);
    		
            DisplayUtil.displayHitLimit(ps.size());

            for (Permit p : ps) {
            	if (!permit.getPermitID().equals(p.getPermitID())) {
            		activePermits.add(new SelectItem(p.getPermitID(), p
            				.getPermitNumber()));
            	}
            }
 
    	} catch (RemoteException e) {
            handleException(e);
        }

    	return activePermits;
    }*/

    /**
     * @return
     * 
     */
    /*
    public final List<SelectItem> getRpeNumbers() {
        rpeNumbers = new ArrayList<SelectItem>();
        try {
            ApplicationSearchResult[] apps = getApplicationService()
                    .retrieveApplicationSearchResults(null,null,
                            permit.getFacilityId(), null, null, null, "RPE",
                            null, false, null, null, null, true);

            for (ApplicationSearchResult a : apps) {
                rpeNumbers.add(new SelectItem(a.getApplicationId(), a
                        .getApplicationNumber()));
            }
        } catch (RemoteException e) {
            handleException(e);
        }
        return rpeNumbers;
    }
    */

    /**
     * @return
     * 
     */
    public final List<SelectItem> getRprNumbers() {
        rprNumbers = new ArrayList<SelectItem>();
        try {
            ApplicationSearchResult[] apps = getApplicationService()
                    .retrieveApplicationSearchResults(null,null,
                            permit.getFacilityId(), null, null, null,
                            ApplicationTypeDef.RPR_REQUEST, null, false, null,
                            null, null, true);

            for (ApplicationSearchResult a : apps) {
                rprNumbers.add(new SelectItem(a.getApplicationId(), a
                        .getApplicationNumber()));
            }
        } catch (RemoteException e) {
            handleException(e);
        }
        return rprNumbers;
    }

    public final String addCC() {
        PermitCC cc = new PermitCC();
        cc.setPermitId(permit.getPermitID());
        permit.addPermitCC(cc);
        return null;
    }

    public final void deleteCC(ActionEvent actionEvent) {
        UIXTable table = getCoreTable(actionEvent);
        Iterator<?> it = table.getSelectionState().getKeySet().iterator();
        List<Object> rccList = new ArrayList<Object>();

        Object oldKey = table.getRowKey();
        while (it.hasNext()) {
            Object obj = it.next();
            table.setRowKey(obj);
            // if remove from the list now the list in table will change.
            // so add the object into a list and remove later.
            rccList.add(table.getRowData());
        }
        for (Object o : rccList)
            permit.getPermitCCList().remove(o);

        table.setRowKey(oldKey);
        table.getSelectionState().clear();

    }

    private UIXTable getCoreTable(ActionEvent actionEvent) {
        UIComponent comp = actionEvent.getComponent();
        while (!(comp.getParent() instanceof UIXTable))
            comp = comp.getParent();
        return (UIXTable) comp.getParent();
    }

    public final String startAddComment() {
        tempComment = new PermitNote();
        tempComment.setUserId(getCurrentUserID());
        tempComment.setPermitId(permit.getPermitID());
        tempComment.setDateEntered(new Timestamp(System.currentTimeMillis()));
        tempComment.setNoteTypeCd(NoteType.DAPC);
        newNote = true;
        noteReadOnly = false;

        return COMMENT_DIALOG_OUTCOME;
    }

    public final String startEditComment() {
        newNote = false;
        tempComment = new PermitNote(modifyComment);
        if (tempComment.getUserId().equals(getCurrentUserID()))
            noteReadOnly = false;
        else
            noteReadOnly = true;

        return COMMENT_DIALOG_OUTCOME;
    }

    public final void saveComment(ActionEvent actionEvent) {
      List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
		
	  // make sure note is provided
      if (tempComment.getNoteTxt() == null || tempComment.getNoteTxt().trim().equals("")) {
      	  validationMessages.add(new ValidationMessage("noteTxt", "Attribute " + "Note" + " is not set.",
      			  ValidationMessage.Severity.ERROR, "noteTxt"));
	  }
        
      if (validationMessages.size() > 0) {
		displayValidationMessages("", validationMessages.toArray(new ValidationMessage[0]));
	  } else {
        try {
            if (newNote) {
                getPermitService().createPermitNote(tempComment);
            } else {
                getPermitService().modifyPermitNote(tempComment);
            }

        } catch (RemoteException e) {
            DisplayUtil.displayError("cannot save comment");
            handleException(e);
            return;
        }

        tempComment = null;
        reloadComments();
        FacesUtil.returnFromDialogAndRefresh();
	  }
    }

    public final void commentDialogDone(ReturnEvent returnEvent) {
        tempComment = null;
        reloadPermit();
    }

    public final PermitNote getTempComment() {
        return tempComment;
    }

    public final void setTempComment(PermitNote tempComment) {
        this.tempComment = tempComment;
    }
    
    public final PermitNote getModifyComment() {
        return modifyComment;
    }

    public final void setModifyComment(PermitNote modifyComment) {
        this.modifyComment = modifyComment;
    }

    public final String getCurrentIssuanceAction() {
        return currentIssuanceAction;
    }

    public final void setCurrentIssuanceAction(String currentIssuanceAction) {
        this.currentIssuanceAction = currentIssuanceAction;
    }

    public final String getSwitchButtonText() {
        return switchButtonText;
    }

    public final boolean isSwitchButton() {
        if (switchButtonText == null)
            switchButton = false;
        else
            switchButton = true;
        return switchButton && !isPermitInDoneStatus();
    }

    public final List<SelectItem> getExcludedEUs() {
        List<SelectItem> eus = new ArrayList<SelectItem>();
        List<Integer> euIds = new ArrayList<Integer>();
        if (selectedEUGroup != null)
            for (PermitEU pe : selectedEUGroup.getPermitEUs()) {
                eus.add(new SelectItem(pe.getFpEU().getCorrEpaEmuId(), pe
                        .getFpEU().getEpaEmuId()));
                euIds.add(pe.getFpEU().getCorrEpaEmuId());
            }
        if (individualEUGroup != null && !selectedEUGroup.isIndividualEUGroup())
            for (PermitEU pe : individualEUGroup.getPermitEUs()) {
                eus.add(new SelectItem(pe.getFpEU().getCorrEpaEmuId(), pe
                        .getFpEU().getEpaEmuId()));
                euIds.add(pe.getFpEU().getCorrEpaEmuId());
            }
        List<EmissionUnit> tmpExEus = new ArrayList<EmissionUnit>();
        for (EmissionUnit e : excludedEUs) {
            if (!euIds.contains(e.getCorrEpaEmuId())) {
                eus.add(new SelectItem(e.getCorrEpaEmuId(), e.getEpaEmuId()));
                tmpExEus.add(e);
            }
        }
        excludedEUs = tmpExEus;

        return eus;
    }

    public final List<Integer> getCurrentEUs() {
        ArrayList<Integer> ret = new ArrayList<Integer>();

        for (PermitEU pe : selectedEUGroup.getPermitEUs()) {
            ret.add(pe.getFpEU().getCorrEpaEmuId());
        }

        return ret;
    }

    public final void setCurrentEUs(List<Integer> eus) {
        List<PermitEU> permitEUs = new ArrayList<PermitEU>();
        modifyIndividualEUGroup = false;
        if (selectedEUGroup != null)
            for (PermitEU pe : selectedEUGroup.getPermitEUs()) {
                // mark the eu toBeDeleted and put into the eu list for DAO
                // to delete it from the group.
                /*if (!eus.contains(pe.getFpEU().getCorrEpaEmuId()))
                    pe.setToBeDeleted(true);
                permitEUs.add(pe);*/
                
                // Bug 1978
                if (selectedEUGroup.isIndividualEUGroup()){
                    if (!eus.contains(pe.getFpEU().getCorrEpaEmuId()))
                        pe.setToBeDeleted(true);
                    permitEUs.add(pe);
                }else{
                    if (!eus.contains(pe.getFpEU().getCorrEpaEmuId())){
                    	individualEUGroup = permit.getIndividualEuGroup();
                    	if (individualEUGroup != null) {
                    		individualEUGroup.getPermitEUs().add(pe);
	                        modifyIndividualEUGroup = true;
                    	} else {
                    		String errorMsg = "Individual EU Group is null for permit " + permit.getPermitNumber();
                    		logger.error(errorMsg);
                            permitEUs.add(pe);
                            euUpdateError = true;
                    	}
                    }else{
                        permitEUs.add(pe);
                    }
                }
            }

        if (individualEUGroup != null && !selectedEUGroup.isIndividualEUGroup()) {
            for (PermitEU pe : individualEUGroup.getPermitEUs())
                if (eus.contains(pe.getFpEU().getCorrEpaEmuId())) {
                    permitEUs.add(pe);
                }
        }

        for (EmissionUnit e : excludedEUs) {
            if (eus.contains(e.getCorrEpaEmuId())) {
                PermitEU te = new PermitEU();
                te.setPermitStatusCd(PermitStatusDef.NONE);
                te.setFpEU(e);
                permitEUs.add(te);
            }
        }

        selectedEUGroup.setPermitEUs(permitEUs);
    }

    public final String getGroupHeader() {
        return groupHeader;
    }

    public final void setGroupHeader(String groupHeader) {
        this.groupHeader = groupHeader;
    }

    public final List<Stars2Object> getMactSubparts() {
        return mactSubparts;
    }

    public final void setMactSubparts(List<Stars2Object> mactSubparts) {
        this.mactSubparts = mactSubparts;
    }

    public final UIXTable getMactSubpartsTable() {
        return mactSubpartsTable;
    }

    public final void setMactSubpartsTable(UIXTable mactSubpartsTable) {
        this.mactSubpartsTable = mactSubpartsTable;
    }

    public final List<Stars2Object> getNeshapsSubparts() {
        return neshapsSubparts;
    }

    public final void setNeshapsSubparts(List<Stars2Object> neshapsSubparts) {
        this.neshapsSubparts = neshapsSubparts;
    }

    public final UIXTable getNeshapsSubpartsTable() {
        return neshapsSubpartsTable;
    }

    public final void setNeshapsSubpartsTable(UIXTable neshapsSubpartsTable) {
        this.neshapsSubpartsTable = neshapsSubpartsTable;
    }

    public final List<Stars2Object> getNspsSubparts() {
        return nspsSubparts;
    }

    public final void setNspsSubparts(List<Stars2Object> nspsSubparts) {
        this.nspsSubparts = nspsSubparts;
    }

    public final UIXTable getNspsSubpartsTable() {
        return nspsSubpartsTable;
    }

    public final void setNspsSubpartsTable(UIXTable nspsSubpartsTable) {
        this.nspsSubpartsTable = nspsSubpartsTable;
    }

    public final void addSubpart(ActionEvent ae) {
        subparts.add(new Stars2Object(null));
    }

    public final void deleteSubparts(ActionEvent ae) {
        subparts = Stars2Object.deleteStars2Objects(subpartsTable, subparts);
    }

    public final List<Stars2Object> getSubparts() {
        return subparts;
    }

    public final void setSubparts(List<Stars2Object> subparts) {
        this.subparts = subparts;
    }

    public final UIXTable getSubpartsTable() {
        return subpartsTable;
    }

    public final void setSubpartsTable(UIXTable subpartsTable) {
        this.subpartsTable = subpartsTable;
    }

    public final String getDocTypeCD() {
        return docTypeCD;
    }

    public final void setDocTypeCD(String docTypeCD) {
        this.docTypeCD = docTypeCD;
    }

    public final String getIssuanceStageFlag() {
        return issuanceStageFlag;
    }

    public final void setIssuanceStageFlag(String issuanceStageFlag) {
        this.issuanceStageFlag = issuanceStageFlag;
    }

    public final PermitEU getEu() {
        return eu;
    }

    public final void setEu(PermitEU eu) {
        this.eu = eu;
    }

    public final PermitDocument getSingleDoc() {
        return singleDoc;
    }

    public final void setSingleDoc(PermitDocument singleDoc) {
        this.singleDoc = singleDoc;
    }

    public final String getHeader() {
        String h = "Permit Information";
       // if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.REG))
       //     h = "Registration Status";
        return h;
    }

    public final String getPermitWidth() {
        return PERMIT_WIDTH + "";
    }

    public final String getPermitTableWidth() {
        return (PERMIT_WIDTH - 5) + "";
    }

    public final String getContentWidth() {
        return CONTENT_WIDTH + "";
    }

    public final String getContentTableWidth() {
        return (CONTENT_WIDTH - 50) + "";
    }

    public final String getTreeWidth() {
        return TREE_WIDTH + "";
    }

    public final boolean isDeniable() {
        return deniable;
    }

    public final void setDeniable(boolean deniable) {
        this.deniable = deniable;
    }

    public final String getPublicNoticeType() {
        if ((permit.getPublicNoticeText() == null || permit
                .getPublicNoticeText().length() == 0)
                && (PublicNoticeTypeDef.STANDARD_WORDING.equalsIgnoreCase(permit
                        .getPublicNoticeType()) || PublicNoticeTypeDef.PSD_STANDARD_WORDING.equalsIgnoreCase(permit
                                .getPublicNoticeType())))
            setPublicNoticeText(permit.getPublicNoticeType());
        return permit.getPublicNoticeType();
    }

    public final void setPublicNoticeType(String publicNoticeType) {
        if (PublicNoticeTypeDef.STANDARD_WORDING
                .equalsIgnoreCase(publicNoticeType)
                || PublicNoticeTypeDef.CUSTOMIZED_WORDING
                        .equalsIgnoreCase(publicNoticeType) || PublicNoticeTypeDef.PSD_STANDARD_WORDING
                        .equalsIgnoreCase(publicNoticeType)) {
        	if(PublicNoticeTypeDef.CUSTOMIZED_WORDING
                        .equalsIgnoreCase(publicNoticeType)){
        		setPublicNoticeText(permit.getPublicNoticeType());
        	} else {
	            setPublicNoticeText(publicNoticeType);
        	}
        } else {
            permit.setPublicNoticeText("");
        }
        permit.setPublicNoticeType(publicNoticeType);
    }

    private void setPublicNoticeText(String publicNoticeType) {
        // Once upon a time we thought we needed draft public notice text for
        // each permit type, and reason combination. We have determined that we
        // need just one for each permit type.
        // String code = permit.getPermitType() + permit.getPrimaryReasonCD();
        String code = permit.getPermitType();
        String def = DraftPublicNoticeDef.getData(publicNoticeType).getItems()
                .getItemDesc(code);
        String role = FacilityRoleDef.UNDELIVERED_MAIL_ADMIN;
        if (def == null) {
            // Mantis 2681
            def = "Public Notice Text is not defined.";
            logger.error("Public Notice Text is not found for type " + code);
        } else {
            try {
            	if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.NSR)) {
            		role = FacilityRoleDef.NSR_PERMIT_ENGINEER;
            	} else if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO)) {
            		role = FacilityRoleDef.TV_PERMIT_ENGINEER;
            	}
                Integer[] users = getReadWorkFlowService().retrieveUserIdsOfFacility(
                        permit.getFpId(), role);
                UserDef user = null;
                if (users != null && users.length > 0)
                    user = getInfrastructureService().retrieveUserDef(users[0]);

                String doLaaCd = currentFacility.getDoLaaCd();
                def = DraftPublicNoticeDef.doSubstitution(user, doLaaCd, def);

            } catch (RemoteException e) {
                DisplayUtil.displayError("cannot find user");
                handleException(e);
            }
        }

        // Text in permit object only can be changed when type is CW.
        String type = permit.getPublicNoticeType();
        permit.setPublicNoticeType(PublicNoticeTypeDef.CUSTOMIZED_WORDING);
        permit.setPublicNoticeText(def);
        permit.setPublicNoticeType(type);
    }

    public final boolean getShowNoticeText() {
        return !PublicNoticeTypeDef.CUSTOMIZED_WORDING_IN_SUPPLIED_FILE
                .equalsIgnoreCase(permit.getPublicNoticeType());
    }

    public final PermitDocument getTopTCDoc() {
        return topTCDoc;
    }

    /**
     * 
     * @return
     */
    public final boolean isExpirationDateNeeded() {
        boolean ret = true;
        if (permit instanceof PTIOPermit && ((PTIOPermit) permit).isTv())
            ret = false;
        return ret;
    }

    public final void setFinalIssueDate(Timestamp date) {
        date = convertYear(date);
        permit.setFinalIssueDate(date);
        if (date != null) {
            // fix of bug 1818
            if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO) 
                    && (permit.getPermitReasonCDs().contains(PermitReasonsDef.INITIAL)
                    || permit.getPermitReasonCDs().contains(PermitReasonsDef.RENEWAL))) {
                Calendar td = new GregorianCalendar();
                td.setTimeInMillis(date.getTime());
                //td.add(Calendar.DAY_OF_MONTH, 21);
                setEffectiveDate(new Timestamp(td.getTimeInMillis()));
            } else {
                setEffectiveDate(date);
            }
        }
    }

    public final void setPpIssueDate(Timestamp date) {
        date = convertYear(date);
        if (permit instanceof TVPermit) {
            TVPermit tp = (TVPermit) permit;
            tp.setPpIssueDate(date);
            // For IMPACT, setting of UsepaCompleteDate is triggered by setting Date USEPA Received Permit.
            //if (date != null) {
            //    Calendar td = new GregorianCalendar();
            //    td.setTimeInMillis(date.getTime());
            //    td.add(Calendar.DAY_OF_YEAR, 45);
            //    tp.setUsepaCompleteDate(new Timestamp(td.getTimeInMillis()));
            //}
        }
    }
    
    public final Timestamp getPpIssueDate() {
        return permit.getPpIssueDate();
    }

    private Timestamp convertYear(Timestamp date) {
        if (date != null) {
            Calendar td = new GregorianCalendar();
            td.setTimeInMillis(date.getTime());
            int year = td.get(Calendar.YEAR);
            if (year >= 0 && year <= 39)
                td.set(Calendar.YEAR, year + 2000);
            else if (year <= 99)
                td.set(Calendar.YEAR, year + 1900);

            date = new Timestamp(td.getTimeInMillis());
        }
        return date;
    }

    public final Timestamp getFinalIssueDate() {
        return permit.getFinalIssueDate();
    }

    public final void setEffectiveDate(Timestamp date) {
    	date = convertYear(date);
    	if(!permit.getPermitType().equalsIgnoreCase(PermitTypeDef.NSR)) {
    		permit.setEffectiveDate(date);
		
    	if (date != null) {
    		Timestamp xDate = null;
    		try {
    			xDate = getPermitService().findExpirationDate(permit);
    			permit.setExpirationDate(xDate);
    			if (!permit.getPermitType().equalsIgnoreCase(PermitTypeDef.NSR) 
    					//&& !permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TVPTI) 
    					&& xDate == null) {
    				DisplayUtil.displayError("This permit cannot be processed as a modification.  There is no initial/renewal that is being modified.  This permit should be returned to the Permitting section.  DO NOT ISSUE.");
    				permit.setEffectiveDate(null);
    				permit.setExpirationDate(null);
    				permit.setFinalIssueDate(null);
    			} else if(permit.isEarlyRenewalFlag()) {
    				if(permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO) 
    						//|| 
    						//permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TIV_PTO)
    						) {
    					Calendar e = Calendar.getInstance();
    					e.setTimeInMillis(date.getTime());
    					e.set(Calendar.HOUR_OF_DAY, 0);
    					e.set(Calendar.MINUTE, 0);
    					e.add(Calendar.DAY_OF_YEAR, 540);
    					Calendar x = Calendar.getInstance();
    					x.setTimeInMillis(xDate.getTime());
    					x.set(Calendar.HOUR_OF_DAY, 0);
    					x.set(Calendar.MINUTE, 1);
    					boolean tooWide = e.before(x);
    					Calendar d = Calendar.getInstance();
    					d.setTimeInMillis(date.getTime());
    					d.add(Calendar.YEAR, 5);
    					permit.setExpirationDate(new Timestamp(d.getTimeInMillis()));
    					if(tooWide) {
    						Calendar xC = Calendar.getInstance();
    						xC.setTimeInMillis(xDate.getTime());
    						xC.add(Calendar.DAY_OF_YEAR, -540);
    						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    						String dateStr = sdf.format(new Timestamp(xC.getTimeInMillis()));
    						DisplayUtil.displayError("This permit cannot be issued as an early renewal until " + dateStr + ".  Please refer this task until this date.");
    						permit.setEffectiveDate(null);
    						permit.setExpirationDate(null);
    						permit.setFinalIssueDate(null);
    					}
    				} else if(permit.getPermitType().equalsIgnoreCase(PermitTypeDef.NSR)) {
    					// no effective date in NSR permits
    					permit.setEffectiveDate(null);
    				}
    			} else {
    				if(permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO) 
    						//|| 
    						//permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TIV_PTO)
    						) {
    					
    					if (!PermitReasonsDef.isModReason(permit.getPrimaryReasonCD()) && !PermitReasonsDef.RENEWAL.equals(permit.getPrimaryReasonCD())) {
    						; // + 5 years returned.  Us it.
    					} else {
    						if(xDate != null) {
    							/* For now this check is not needed since IMPACT system does not have the
    	    					   history of old permits.
    							Calendar xC = Calendar.getInstance();
        						xC.setTimeInMillis(xDate.getTime());
        						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        						String dateStr = sdf.format(new Timestamp(xC.getTimeInMillis()));
    							// still an active permit; do not finalize this permit
    							DisplayUtil.displayError("A permit is still active until " + dateStr + ".  Do not issue this permit unless you check \"Issue Early Renewal\".");
    							permit.setEffectiveDate(null);
    							permit.setExpirationDate(null);
    							permit.setFinalIssueDate(null); */
    							;
    						}
    					}
    				}
    			}
    		} catch (RemoteException e) {
    			DisplayUtil.displayError("Cannot find expiration date.");
    			handleException(e);
    		}
    	}}
    }

    public final Timestamp getEffectiveDate() {
        return permit.getEffectiveDate();
    }

    @Override
    public String reload() {
        return reloadPermit();
    }

    public final boolean isShowPerDueDate() {

        boolean ret = false;
        if (permit instanceof PTIOPermit
                && ((PTIOPermit) permit).getPerDueDateCD() != null 
                && ((PTIOPermit) permit).getPerDueDateCD().equals(
                        PTIOPERDueDateDef.NOT_APPLICABLE)) {
            ret = true;
        }
        return ret;
    }

    public final boolean isNoteReadOnly() {
    	if(isReadOnlyUser()){
    		return true;
    	}
    	
        return noteReadOnly;
    }

    public final void setNoteReadOnly(boolean noteReadOnly) {
        this.noteReadOnly = noteReadOnly;
    }

    public final String getPermitDocsZipFile() {

        try {

            ArrayList<PermitDocument> pds = new ArrayList<PermitDocument>();

            for (PermitDocument pd : permit.getDocuments()) {

                if (pd.getPermitDocTypeCD().equals(
                        PermitDocTypeDef.TERMS_CONDITIONS)) {
                    if (getTopTCDoc() != null && getTopTCDoc().equals(pd)) {
                        pds.add(pd);
                    }
                    continue;
                }

                if (pd.getIssuanceStageFlag() == null
                        || pd.getIssuanceStageFlag().equals(
                                PermitDocIssuanceStageDef.ALL)) {
                    pds.add(pd);
                } else if (pd.getIssuanceStageFlag() != null
                        && getCurrentIssuanceAction().equals(
                                PermitGlobalStatusDef.ISSUED_DRAFT)
                        && pd.getIssuanceStageFlag().equals(
                                PermitDocIssuanceStageDef.DRAFT)) {
                    pds.add(pd);
                //} else if (pd.getIssuanceStageFlag() != null
                //        && getCurrentIssuanceAction().equals(
                //                PermitGlobalStatusDef.ISSUED_PPP)
                //        && pd.getIssuanceStageFlag().equals(
                //                PermitDocIssuanceStageDef.PRELIMINARY_PROPOSED)) {
                //    pds.add(pd);
                } else if (pd.getIssuanceStageFlag() != null
                        && getCurrentIssuanceAction().equals(
                                PermitGlobalStatusDef.ISSUED_PP)
                        && pd.getIssuanceStageFlag().equals(
                                PermitDocIssuanceStageDef.PROPOSED)) {
                    pds.add(pd);
                } else if (pd.getIssuanceStageFlag() != null
                        && getCurrentIssuanceAction().equals(
                                PermitGlobalStatusDef.ISSUED_FINAL)
                        && pd.getIssuanceStageFlag().equals(
                                PermitDocIssuanceStageDef.FINAL)) {
                    pds.add(pd);
                }
            }
            
            if (pds.isEmpty()) {
                DisplayUtil.displayError("This permit has no documents.");
                return "FAILURE";
            }

            String stage = "unknown";
            if (getCurrentIssuanceAction() != null
                    && getCurrentIssuanceAction().equals(
                            PermitGlobalStatusDef.ISSUED_DRAFT)) {
                stage = "Draft";
            } else if (getCurrentIssuanceAction() != null
                    && getCurrentIssuanceAction().equals(
                            PermitGlobalStatusDef.ISSUED_FINAL)) {
                stage = "Final";
            } else if (getCurrentIssuanceAction() != null) {
                stage = getCurrentIssuanceAction();
            }

            zipUrl = getPermitService().zipPermitDocs(
                    pds.toArray(new PermitDocument[0]),
                    permit.getPermitNumber(), stage, getCurrentUserID(), recentAppDoc);

            FacesUtil.startModelessDialog("../permits/downloadZip.jsf", 500,
                    500);

            return "SUCCESS";
        } catch (RemoteException re) {
            logger.error("System error: " + re.toString(), re);
            DisplayUtil
                    .displayError("System error: please contact the system administrator. "
                            + re.toString());
        }

        return "FAILURE";

    }

    public final String getUrl() {
        logger.debug("URL = " + DocumentUtil.getFileStoreBaseURL() + zipUrl);
        return DocumentUtil.getFileStoreBaseURL() + zipUrl;
    }

    /**
     * @return the recentAppDoc
     */
    public final Document getRecentAppDoc() {
        return recentAppDoc;
    }

    /**
     * @param recentAppDoc the recentAppDoc to set
     */
    public final void setRecentAppDoc(Document recentAppDoc) {
        this.recentAppDoc = recentAppDoc;
    }

    /**
     * @return the removeGroup
     */
    public final boolean isRemoveGroup() {
        return removeGroup;
    }

    /**
     * @param removeGroup the removeGroup to set
     */
    public final void setRemoveGroup(boolean removeGroup) {
        this.removeGroup = removeGroup;
    }

    public final String generateStatementOfBasisDoc() {
    	return generateTCDocument("131");
    }
    
    public final String generatePermitStrategyDoc() {
    	return generateTCDocument("132");
    }
    
    public final String generateResponseToCommentsDoc() {
    	return generateTCDocument("134");
    }
    
    private String generateTCDocument(String docTemplateId) {
    	String ret = null;
    	if (permit != null && permit.getFacilityId() != null && permit.getPermitNumber() != null) {
            try {
            	Facility facility = getFacilityService().retrieveFacility(permit.getFacilityId());
                //DocumentGenerationBean dataBean = new DocumentGenerationBean();
            	documentGenerationBean.setCorrespondenceDate(null);
            	documentGenerationBean.setPermit(permit);
            	documentGenerationBean.setFacility(facility);
                TemplateDocument templateDoc = getDocumentService().retrieveTemplateDocument(docTemplateId);
                String fPath = templateDoc.getTemplateDocPath();
                int lastSlash = fPath.lastIndexOf('/');
                if (lastSlash < 0) {
                    lastSlash = fPath.lastIndexOf('\\');
                }
                int lastDot = fPath.lastIndexOf('.');
                String fileBaseName = fPath.substring(lastSlash + 1, lastDot);
                UserAttributes ua = (UserAttributes) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userAttrs");
                String tmpDirName = getDocumentService().createTmpDir(ua.getUserName());
                /*DocumentUtil.generateDocument(templateDoc.getPath(), documentGenerationBean, tmpDirName
                                              + File.separator + permit.getPermitNumber()
                                              + "-" + fileBaseName + ".docx");*/
                DocumentUtil.generateAsposeDocument(templateDoc.getTemplateDocPath(), documentGenerationBean, tmpDirName
                        + File.separator + permit.getPermitNumber() 
                        + "-" + fileBaseName + ".docx");

                // set up temp files list to only list files for this permit
                ListPermitTempFiles permitTempFiles = (ListPermitTempFiles) FacesUtil
                        .getManagedBean("listPermitTempFiles");
                permitTempFiles.setPermitNbr(permit.getPermitNumber());
                
                ret = "dialog:listPermitTempFilesPopup";
            }
            catch (RemoteException e) {
                handleException(e);
            } catch (DocumentGenerationException e) {
				handleException(new RemoteException(e.getMessage(), e));
			} catch (IOException e) {
				handleException(new RemoteException(e.getMessage(), e));
			}
    	}
    	return ret;
    }

	public boolean isDialogEdit() {
		return dialogEdit;
	}

	public void setDialogEdit(boolean dialogEdit) {
		this.dialogEdit = dialogEdit;
	}
	
	public boolean isActionTypePermit(){
		boolean isActionTypePermit = false;
		if (permit instanceof PTIOPermit)
		{
			String actionType = ((PTIOPermit) permit).getPermitActionType();
			isActionTypePermit =  (!Utility.isNullOrEmpty(actionType)
					&& (actionType.equals(PermitActionTypeDef.PERMIT)));
		}
		return isActionTypePermit;
	}
	
    public List<SelectItem> getPublicNoticeTypes() {
        return PublicNoticeTypeDef.getData(permit.getPermitType()).getItems().getCurrentItems();
    }
    
    public final String startToaddReferencedApp() {
		setEditMode1(true);
		setEditApplications(true);
		return "dialog:addApplicationDetail";
	}
    
    public final void editReferencedApp() {
    	setEditMode1(true);
    	setEditApplications(true);
	}
    
      public final String startToEditReferencedApp() {
    	setEditMode1(false);
    	setPreviousApplicationNumnber(this.newReferencedAppNumber);
    	setEditApplications(true);
//    	this.permitApplication.setNewObject(false);
		
		return "dialog:addApplicationDetail";
	}
            
   public final TreeSet<String> getAllAssociatedSubmittedApplicationNumbers() {
    	TreeSet<String> appNumbersSet = new TreeSet<String>();
    	ApplicationSearchResult[] applicationsForThisFacility;
    	try {
    		applicationsForThisFacility 
    		= getApplicationService().retrieveApplicationSearchResults(null, null,getPermit().getFacilityId(), null, null,
    				null, null, null, false, null, null, null, true);

    		for (ApplicationSearchResult app : applicationsForThisFacility) {
    			// only submitted NSR or TV applications can show up in drop down
    			// and application type must match selected type
    			if ((ApplicationTypeDef.PTIO_APPLICATION.equals(app.getApplicationTypeCd()) ||
    					ApplicationTypeDef.TITLE_V_APPLICATION.equals(app.getApplicationTypeCd())) &&
    					app.getApplicationTypeCd().equals(getPermit().getApplicationPermitType())) {
    				if (app.getSubmittedDate() == null) {
    					continue;   // only submitted applications may be corrected/amended
    				}
    				appNumbersSet.add(app.getApplicationNumber());
    			}
    		}
    	} catch (Exception e) {
    		logger.error(e.getMessage(), e);
    	}
    	return appNumbersSet;
    }
	
	   public final List<SelectItem> getAssociatedSubmittedApplicationNumbers() {
	        List<SelectItem> appNumbers = new ArrayList<SelectItem>();
	        TreeSet<String> appNumbersSet = new TreeSet<String>();
	        try {
	        	appNumbersSet = getAllAssociatedSubmittedApplicationNumbers();
	                for (Application apps : getPermit().getApplications()) {
		                	if (appNumbersSet != null && appNumbersSet.contains(apps.getApplicationNumber())) {
		                		appNumbersSet.remove(apps.getApplicationNumber());
		                	}
	               	}
	                if (isEditApplications() && getPreviousApplicationNumnber() != null)
	                	appNumbersSet.add(this.newReferencedAppNumber);
	                Iterator<String> it = appNumbersSet.iterator();
	                while (it.hasNext()) {
	                    String appNumber = it.next();
	                    appNumbers.add(new SelectItem(appNumber, appNumber));
	                }
	                
	            
	        }catch (Exception e) {
	                logger.error(e.getMessage(), e);
	            }
	        
	        return appNumbers;
	    }
	   
	   public final List<SelectItem> getApplicationNumbersToAssocaite() {

	        List<SelectItem> appNumbers = new ArrayList<SelectItem>();
	        TreeSet<String> appNumbersSet = new TreeSet<String>();
	        try {
	        	appNumbersSet = getAllAssociatedSubmittedApplicationNumbers();
	                for (Application apps : getPermit().getApplications())
               	{
	                	if (appNumbersSet != null && appNumbersSet.contains(apps.getApplicationNumber()))
	                	{
	                		appNumbersSet.remove(apps.getApplicationNumber());
	                	}
               	}
	                Iterator<String> it = appNumbersSet.iterator();
	                while (it.hasNext()) {
	                    String appNumber = it.next();
	                    appNumbers.add(new SelectItem(appNumber, appNumber));
	                }
	                
	            
	        }catch (Exception e) {
	                logger.error(e.getMessage(), e);
	            }
	        
	        return appNumbers;
	    
	   
	   }
	   
	   public final int getApplicationNumbersToAssocaiteCount()
	   {
		   boolean currentEditMode = isEditMode1();
		   setEditMode1(true);
		   int currentPermitApplications = getApplicationNumbersToAssocaite().size();
		   setEditMode1(currentEditMode);
		   return currentPermitApplications;
	   }
	   
	   public final List<Stars2Object> getpart61NESHAPSubparts() {
	        return part61NESHAPSubparts;
	    }

	    public final void setPart61NESHAPSubparts(List<Stars2Object> part61NESHAPSubparts) {
	        this.part61NESHAPSubparts = part61NESHAPSubparts;
	    }

	    public final UIXTable getPart61NESHAPSubpartsTable() {
	        return part61NESHAPSubpartsTable;
	    }

	    public final void setPart61NESHAPSubpartsTable(UIXTable part61NESHAPSubpartsTable) {
	        this.part61NESHAPSubpartsTable = part61NESHAPSubpartsTable;
	    }

	    public final List<Stars2Object> getpart63NESHAPSubparts() {
	        return part63NESHAPSubparts;
	    }

	    public final void setPart63NESHAPSubparts(List<Stars2Object> part63NESHAPSubparts) {
	        this.part63NESHAPSubparts = part63NESHAPSubparts;
	    }

	    public final UIXTable getPart63NESHAPSubpartsTable() {
	        return part63NESHAPSubpartsTable;
	    }

	    public final void setPart63NESHAPSubpartsTable(UIXTable part63NESHAPSubpartsTable) {
	        this.part63NESHAPSubpartsTable = part63NESHAPSubpartsTable;
	    }
	    public final List<PermitDocument> getFixedPotentialAttachments() {
	    	if (FacesUtil.getCurrentPage().equals("permitProfile")) {
	    		setFixedPotentialAttachments(getPermitDetailAttachments());
	    		setCurrentPage("permitProfile");
	    	} else if (FacesUtil.getCurrentPage().equals("permits.detail.draftIssuance")) {
	    		setFixedPotentialAttachments(getDraftPublicationAttachments());
	    		setCurrentPage("permits.detail.draftIssuance");
	    	} else if (FacesUtil.getCurrentPage().equals("permits.detail.PPIssuance")) {
	    		setFixedPotentialAttachments(getPPPublicationAttachments());
	    		setCurrentPage("permits.detail.PPIssuance");
	    	} else if (FacesUtil.getCurrentPage().equals("permits.detail.finalIssuance")) {
	    		setFixedPotentialAttachments(getFinalIssuanceAttachments());
	    		setCurrentPage("permits.detail.finalIssuance");
	    	} else if (FacesUtil.getCurrentPage().equals("permits.detail.denialIssuance")) {
	    		setFixedPotentialAttachments(getwithdrawalIssuancceAttachments());
	    		setCurrentPage("permits.detail.denialIssuance");
	    	} else if (FacesUtil.getCurrentPage().equals("permits.detail.feeSummary")) {
	    		setFixedPotentialAttachments(getFeeSummaryAttachments());
	    		setCurrentPage("permits.detail.feeSummary");
	    	}
	        return fixedPotentialAttachments;
	    }
	    public final List<PermitDocument> getPermitDetailAttachments() {
	        return permitDetailAttachments;
	    }
	    public final List<PermitDocument> getDraftPublicationAttachments() {
	        return draftPublicationAttachments;
	    }
	    public final List<PermitDocument> getPPPublicationAttachments() {
	        return ppPublicationAttachments;
	    }
	    public final List<PermitDocument> getFinalIssuanceAttachments() {
	        return finalIssuanceAttachments;
	    }
	    public final List<PermitDocument> getwithdrawalIssuancceAttachments() {
	        return withdrawalIssuancceAttachments;
	    }
	    
	    public final List<PermitDocument> getFeeSummaryAttachments() {
	        return feeSummaryAttachments;
	    }
	    
	    public final List<PermitDocument> getPotentialAttachments() {
	    	List<String> attachedDocTypes = new ArrayList<String>(0);
	    	for (PermitDocument permitDoc: permit.getDocuments())
	    	{
	    		attachedDocTypes.add(permitDoc.getPermitDocTypeCD());
	    	}
	    	potentialAttachments = new ArrayList<PermitDocument>(0);
	    	DefSelectItems permitDocTypeDefs = getPermitDocTypeDefs();
	    	for (SelectItem item : permitDocTypeDefs.getCurrentItems()) {
	    		PermitDocTypeDef permitDocTypeDef = (PermitDocTypeDef) permitDocTypeDefs
	    				.getItem(item.getValue().toString());
	    		if (permitDocTypeDef.isFixed() && (!attachedDocTypes.contains(permitDocTypeDef.getCode())))
	    			{
	    				PermitDocument document = new PermitDocument();
	    				document.setPermitId(permit.getPermitID());
	    				document.setFixed(permitDocTypeDef.isFixed());
	    				document.setPermitDocTypeCD(permitDocTypeDef.getCode());
	    				document.setNsr(permitDocTypeDef.isNsr());
	    				document.setTitleV(permitDocTypeDef.isTitleV());
	    				document.setPermitDetail(permitDocTypeDef.isPermitDetail());
	    				document.setDraftPublication(permitDocTypeDef.isDraftPublication());
	    				document.setPpPublication(permitDocTypeDef.isPpPublication());
	    				document.setFinalIssuance(permitDocTypeDef.isFinalIssuance());
	    				document.setWithdrawalIssuance(permitDocTypeDef.isWithdrawalIssuance());
	    				document.setFeeSummary(permitDocTypeDef.isFeeSummary());
	    				document.setPermitDocumentID(permitDocTypeDef.getPermitDocumentid());
	    				//document.setTemplatePath(permitDocTypeDef.getTemplatePath());
	    				document.setPermitDocTypeDsc(permitDocTypeDef.getPermitDocTypeDsc());
	    				document.setRequiredDoc(true);
	    				
	    				potentialAttachments.add(document);
	    			}
	    			else
	    			{
	    				for (PermitDocument document: permit.getDocuments())
	    				{
	    					if (permitDocTypeDef.getCode().equals(document.getPermitDocTypeCD()))
	    					{
	    						document.setFixed(permitDocTypeDef.isFixed());
	    						document.setPermitDocTypeCD(permitDocTypeDef.getCode());
	    						document.setNsr(permitDocTypeDef.isNsr());
	    						document.setTitleV(permitDocTypeDef.isTitleV());
	    						document.setPermitDetail(permitDocTypeDef.isPermitDetail());
	    						document.setDraftPublication(permitDocTypeDef.isDraftPublication());
	    						document.setPpPublication(permitDocTypeDef.isPpPublication());
	    						document.setFinalIssuance(permitDocTypeDef.isFinalIssuance());
	    						document.setWithdrawalIssuance(permitDocTypeDef.isWithdrawalIssuance());
	    						document.setFeeSummary(permitDocTypeDef.isFeeSummary());
	    						document.setPermitDocumentID(permitDocTypeDef.getPermitDocumentid());
	    						//document.setTemplatePath(permitDocTypeDef.getTemplatePath());
	    						document.setPermitDocTypeDsc(permitDocTypeDef.getPermitDocTypeDsc());
	    						potentialAttachments.add(document);
	    					}
	    				}
	    			}
	    	}
	    	return potentialAttachments;
	    }
	    
	    private void organisePotentialPermitDocuments() {
	    	permitDetailAttachments = new ArrayList<PermitDocument>();
	    	draftPublicationAttachments = new ArrayList<PermitDocument>();
	    	ppPublicationAttachments = new ArrayList<PermitDocument>();
	    	finalIssuanceAttachments = new ArrayList<PermitDocument>();
	    	withdrawalIssuancceAttachments = new ArrayList<PermitDocument>();
	    	feeSummaryAttachments = new ArrayList<PermitDocument>();
	    	topTCDoc = null;
	    	for (PermitDocument doc : getPotentialAttachments()) {
	    		if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.NSR))
	    		{
	    			if (doc.isNsr())
	    			{
	    				if (doc.isPermitDetail())
	    					permitDetailAttachments.add(doc);
	    				if (doc.isDraftPublication())
	    					draftPublicationAttachments.add(doc);
	    				if (doc.isPpPublication())
	    					ppPublicationAttachments.add(doc);
	    				if (doc.isFinalIssuance())
	    					finalIssuanceAttachments.add(doc);
	    				if (doc.isWithdrawalIssuance())
	    					withdrawalIssuancceAttachments.add(doc);
	    				if (doc.isFeeSummary())
	    					feeSummaryAttachments.add(doc);
	    			}
	    		}
	    		else
	    		{
	    			if (doc.isTitleV())
	    			{
	    				if (doc.isPermitDetail())
	    					permitDetailAttachments.add(doc);
	    				if (doc.isDraftPublication())
	    					draftPublicationAttachments.add(doc);
	    				if (doc.isPpPublication())
	    					ppPublicationAttachments.add(doc);
	    				if (doc.isFinalIssuance())
	    					finalIssuanceAttachments.add(doc);
	    				if (doc.isWithdrawalIssuance())
	    					withdrawalIssuancceAttachments.add(doc);
	    			}
	    		}
	    	}
	    }
	    
	    public final DefSelectItems getPermitDocTypeDefs() {
	    	return PermitDocTypeDef.getData().getItems();
	    }
		public void setFixedPotentialAttachments(List<PermitDocument> fixedPotentialAttachments) {
			this.fixedPotentialAttachments = fixedPotentialAttachments;
		}

		public List<SelectItem> getPermitFixedDocTypeInitialDefs() {
			return PermitDocTypeDef.getFixedData();
		}
		public List<SelectItem> getPermitDynaimDocTypeInitialDefs() {
			return PermitDocTypeDef.getDynamicData(permit.getPermitType(), getCurrentPage(), singleDoc);
		}
		
		public final void closeDialog() {
			FacesUtil.returnFromDialogAndRefresh();
		}
		
		public final void closeModelessDialog() {
			FacesUtil.returnFromDialog();
		}
		
		public boolean isEditMode1() {
			return editMode1;
		}

		public void setEditMode1(boolean editMode1) {
			this.editMode1 = editMode1;
		}
		
		public String getAddButtonText()
		{
			String text = "Add";
			if (getApplicationNumbersToAssocaiteCount() == 0)
				text = "There are no other submitted permit applications for this facility that can be associated with this permit";
			return text;
		}
		
		public String getEditButtonText()
		{
			String text = "Edit";
			if (getApplicationNumbersToAssocaiteCount() == 0)
				text = "There are no other submitted permit applications for this facility that can be associated with this permit";
			return text;
		}
		
		  public final int getPermitApplicationCount()
		   {
			   return permit.getApplications().size();
		   }
		
		public String getDeleteButtonText()
		{
			String text = "Delete";
			if (!permit.isLegacyPermit() && permit.getApplications().size() == 1)
				text = "At least one application should be associated to this permit";
			return text;
		}


		public String getPreviousApplicationNumnber() {
			return previousApplicationNumnber;
		}

		public void setPreviousApplicationNumnber(String previousApplicationNumnber) {
			this.previousApplicationNumnber = previousApplicationNumnber;
		}

		public boolean isEditApplications() {
			return EditApplications;
		}

		public void setEditApplications(boolean editApplications) {
			EditApplications = editApplications;
		}

		public String getCurrentPage() {
			return currentPage;
		}

		public void setCurrentPage(String currentPage) {
			this.currentPage = currentPage;
		}

		public boolean isSemiEditMode() {
			return semiEditMode;
		}

		public void setSemiEditMode(boolean semiEditMode) {
			this.semiEditMode = semiEditMode;
		}
		
		public boolean isPermitIssuedFinal() {
			return permit.getPermitGlobalStatusCD().equalsIgnoreCase(PermitGlobalStatusDef.ISSUED_FINAL);
		}
		
		public boolean isNSRAdmin() {
			return InfrastructureDefs.getCurrentUserAttrs().isNSRAdminUser();
		}
		
		public boolean isStars2Admin() {
			return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin();
		}	
		
	public final TableSorter getNSRTimeSheetRowWrapper() {
		return NSRTimeSheetRowWrapper;
	}

	public final void setNSRTimeSheetRowWrapper(
			TableSorter NSRTimeSheetRowWrapper) {
		this.NSRTimeSheetRowWrapper = NSRTimeSheetRowWrapper;
	}
	
	public List<AreaEmissionsOffset> getAreaEmissionsOffsetList() {
		return this.areaEmissionsOffsetList;
	}
	
	public void setAreaEmissionsOffsetList(
			List<AreaEmissionsOffset> areaEmissionsOffsetList) {
		this.areaEmissionsOffsetList = areaEmissionsOffsetList;
	}
	
	/* START CODE: NSR FIXED CHARGE */


	private final void initializeNSRFixedCharges() {
		this.NSRFixedChargeWrapper = new TableSorter();
		try {
			((PTIOPermit) this.permit).setNSRFixedChargeList(getPermitService()
					.retrieveNSRFixedChargeList(this.permit.getPermitID()));
			this.NSRFixedChargeWrapper
					.setWrappedData(((PTIOPermit) this.permit)
							.getNSRFixedChargeList());

		} catch (RemoteException re) {
			logger.error("failed to get FIXED CHARGE LIST", re);
		} finally {
			this.editMode1 = false;
		}
	}

	public final String startToAddNSRFixedCharge() {
		this.editMode1 = true;
		this.modifyNSRFixedCharge = new NSRFixedCharge();
		this.modifyNSRFixedCharge.setPermitId(permit.getPermitID());
		this.newNSRFixedCharge = true;

		return "dialog:nsrFixedChargeDetail";
	}

	public final String startToEditNSRFixedCharge() {
		int index = this.NSRFixedChargeWrapper.getRowIndex();
		this.modifyNSRFixedCharge = getSelectedNSRFixedCharge(index);

		this.newNSRFixedCharge = false;
		this.editMode1 = false;

		return "dialog:nsrFixedChargeDetail";
	}

	public NSRFixedCharge getModifyNSRFixedCharge() {

		return this.modifyNSRFixedCharge;
	}

	public void setModifyNSRFixedCharge(NSRFixedCharge modifyNSRFixedCharge) {
		this.modifyNSRFixedCharge = modifyNSRFixedCharge;
	}

	private final NSRFixedCharge getSelectedNSRFixedCharge(int index) {
		NSRFixedCharge fc = (NSRFixedCharge) this.NSRFixedChargeWrapper
				.getRowData(index);

		return fc;
	}

	public boolean isNewNSRFixedCharge() {
		return newNSRFixedCharge;
	}

	public void setNewNSRFixedCharge(boolean newNSRFixedCharge) {
		this.newNSRFixedCharge = newNSRFixedCharge;
	}

	public final void editNSRFixedCharge() {
		this.editMode1 = true;
		this.newNSRFixedCharge = false;
	}

	public final void saveNSRFixedCharge() {

		DisplayUtil.clearQueuedMessages();

		if (fixedChargeValidated()) {
			if (this.newNSRFixedCharge) {
				createNSRFixedCharge();
			} else {
				updateNSRFixedCharge();
			}
		}

	}

	public void closeNSRFixedChargeDialog() {
		this.refreshNSRFixedCharges();
		closeDialog();
	}

	protected final void refreshNSRFixedCharges() {
		this.editMode1 = false;
		this.newNSRFixedCharge = false;
		this.initializeNSRFixedCharges();
	}

	public void createNSRFixedCharge() {

		try {

			getPermitService().createNSRFixedCharge(
					this.getModifyNSRFixedCharge());
			DisplayUtil.displayInfo("Create NSR Permit Fixed Charge Success");

		} catch (DAOException e) {
			logger.error("Create NSR Permit Fixed Charge Failed", e);
			DisplayUtil.displayError("Create NSR Permit Fixed Charge Failed");

		} catch (Exception ex) {
			logger.error("Create NSR Permit Fixed Charge Failed", ex);
			DisplayUtil.displayError("Create NSR Permit Fixed Charge Failed");

		} finally {
			this.refreshNSRFixedCharges();
			FacesUtil.returnFromDialogAndRefresh();
		}
	}

	public void updateNSRFixedCharge() {

		try {
			// Modify FIXED CHARGE

			getPermitService().modifyNSRFixedCharge(
					this.getModifyNSRFixedCharge());
	
			// display warning if the invoiced flag is flipped
			if(null != this.getModifyNSRFixedCharge().getInvoicedValueChangeEvent()) {
				DisplayUtil.displayWarning("You have changed the invoiced status of an NSR Permit Fixed/Other Charge. Please re-issue the invoice for the NSR permit/waiver if necessary.");
				this.getModifyNSRFixedCharge().setInvoicedValueChangeEvent(null);
			}
			// display warning if the amount is changed after the fixed/other charge was invoiced
			if(null != this.getModifyNSRFixedCharge().getAmountValueChangeEvent()
					&& this.getModifyNSRFixedCharge().isInvoiced()) {
				DisplayUtil.displayWarning("You have changed the amount of an NSR Permit Fixed/Other Charge after it was invoiced. Please re-issue the invoice for the NSR permit/waiver if necessary.");
				this.getModifyNSRFixedCharge().setAmountValueChangeEvent(null);
			}
			
			DisplayUtil.displayInfo("Modify NSR Permit Fixed Charge Success");
		} catch (DAOException e) {
			logger.error("Create NSR Permit Fixed Charge Failed", e);
			DisplayUtil.displayError("Create NSR Permit Fixed Charge Failed");

		} catch (RemoteException ex) {
			logger.error("Create NSR Permit Fixed Charge Failed", ex);
			DisplayUtil.displayError("Create NSR Permit Fixed Charge Failed");

		} catch (Exception ex) {
			logger.error("Create NSR Permit Fixed Charge Failed", ex);
			DisplayUtil.displayError("Create NSR Permit Fixed Charge Failed");

		} finally {
			this.refreshNSRFixedCharges();
			closeDialog();
		}
	}

	public void deleteNSRFixedCharge() {

		DisplayUtil.clearQueuedMessages();

		try {
			boolean isFixedChargeInvoiced = this.getModifyNSRFixedCharge().isInvoiced();
			getPermitService().removeNSRFixedCharge(this.modifyNSRFixedCharge);
			 // display warning if the deleted fixed/other charge was already invoiced
			if(isFixedChargeInvoiced) {
				DisplayUtil.displayWarning("You have deleted an NSR Permit Fixed/Other Charge that was already invoiced. Please re-issue the invoice for the NSR permit/waiver if necessary.");
			}
			DisplayUtil.displayInfo("Delete NSR Permit Fixed Charge Success");

		} catch (DAOException e) {
			logger.error("Remove NSR Permit Fixed Charge failed", e);
			DisplayUtil.displayError("Remove NSR Permit Fixed Charge failed");

		} finally {

			refreshNSRFixedCharges();
			closeDialog();
		}
	}

	public void cancelNSRFixedCharge() {
		this.refreshNSRFixedCharges();
		closeDialog();
	}

	public final TableSorter getNSRFixedChargeWrapper() {
		return this.NSRFixedChargeWrapper;
	}

	public final void setNSRFixedChargeWrapper(TableSorter NSRFixedChargeWrapper) {
		this.NSRFixedChargeWrapper = NSRFixedChargeWrapper;
	}

	public boolean fixedChargeValidated() {

		if (this.getModifyNSRFixedCharge().getCreatedDate() == null
				|| Utility.isNullOrEmpty(this.getModifyNSRFixedCharge()
						.getCreatedDate().toString())) {
			DisplayUtil.displayError("ERROR: Attribute Date is not set.");
			return false;
		}
		
		if (this.getModifyNSRFixedCharge().getDescription() == null
				|| Utility.isNullOrEmpty(this.getModifyNSRFixedCharge()
						.getDescription())) {
			DisplayUtil.displayError("ERROR: Attribute Description is not set.");
			return false;
		}
		
		if (this.getModifyNSRFixedCharge().getAmount() == null
				|| Utility.isNullOrEmpty(this.getModifyNSRFixedCharge()
						.getAmount().toString())) {
			DisplayUtil.displayError("ERROR: Attribute Amount is not set.");
			return false;
		}
		return true;
	}

	/* END CODE: NSR FIXED CHARGE */
		
		/* START CODE: PERMIT CHARGE PAYMENT */

	private final void initializePermitChargePayments() {
		this.permitChargePaymentWrapper = new TableSorter();
		try {
			((PTIOPermit) this.permit)
					.setPermitChargePaymentList(getPermitService()
							.retrievePermitChargePaymentList(
									this.permit.getPermitID()));
			this.permitChargePaymentWrapper
					.setWrappedData(((PTIOPermit) this.permit)
							.getPermitChargePaymentList());

		} catch (RemoteException re) {
			logger.error("failed to get PERMIT CHARGE PAYMENTS", re);
		} finally {
			this.editMode1 = false;
		}
	}

	private final void retrieveCurrentTotal() {
		try {
			((PTIOPermit) this.permit)
					.setCurrentTotal(getPermitService()
							.retrievePermitChargePaymentTotal(
									this.permit.getPermitID()));

		} catch (RemoteException re) {
			logger.error("failed to get Current Total", re);
		} finally {
			this.editMode1 = false;
		}
	}

	private final void retrieveCurrentBalanceTotal() {
		this.retrieveCurrentTotal();
		
	}

	public final String startToAddPermitChargePayment() {
		this.editMode1 = true;
		this.modifyPermitChargePayment = new PermitChargePayment();
		this.modifyPermitChargePayment.setPermitId(permit.getPermitID());
		this.newPermitChargePayment = true;

		return "dialog:permitChargePaymentDetail";
	}

	public final String startToEditPermitChargePayment() {
		int index = this.permitChargePaymentWrapper.getRowIndex();
		this.modifyPermitChargePayment = getSelectedPermitChargePayment(index);
		
		this.newPermitChargePayment = false;
		this.editMode1 = false;

		return "dialog:permitChargePaymentDetail";
	}

	public PermitChargePayment getModifyPermitChargePayment() {

		return this.modifyPermitChargePayment;
	}

	public void setModifyPermitChargePayment(
			PermitChargePayment modifyPermitChargePayment) {
		this.modifyPermitChargePayment = modifyPermitChargePayment;
	}

	private final PermitChargePayment getSelectedPermitChargePayment(int index) {
		PermitChargePayment cp = (PermitChargePayment) this.permitChargePaymentWrapper
				.getRowData(index);

		return cp;
	}

	public boolean isNewPermitChargePayment() {
		return newPermitChargePayment;
	}

	public void setNewPermitChargePayment(boolean newPermitChargePayment) {
		this.newPermitChargePayment = newPermitChargePayment;
	}

	public final void editPermitChargePayment() {
		this.editMode1 = true;
		this.newPermitChargePayment = false;
	}

	public final void savePermitChargePayment() {

		DisplayUtil.clearQueuedMessages();
		
		if (!(this.getModifyPermitChargePayment().isPayment()
				|| this.getModifyPermitChargePayment().isOtherCredit())) {
			this.getModifyPermitChargePayment().setTransmittalNumber(null);
			this.getModifyPermitChargePayment().setCheckNumber(null);
		}

		if (chargePaymentValidated()) {
			if (this.newPermitChargePayment) {
				createPermitChargePayment();
			} else {
				updatePermitChargePayment();
			}
		}
	}

	public void closePermitChargePaymentDialog() {
		this.refreshPermitChargePayments();
		closeDialog();
	}

	protected final void refreshPermitChargePayments() {
		this.editMode1 = false;
		this.newPermitChargePayment = false;
		this.initializePermitChargePayments();
		
		this.retrieveCurrentBalanceTotal();
	}

	public void createPermitChargePayment() {

		try {

			getPermitService().createPermitChargePayment(
					this.getModifyPermitChargePayment());
			DisplayUtil.displayInfo("Create Permit Charge Payment Success");

		} catch (DAOException e) {
			logger.error("Create Permit Charge Payment Failed", e);
			DisplayUtil.displayError("Create Permit Charge Payment Failed");

		} catch (Exception ex) {
			logger.error("Create Permit Charge Payment Failed", ex);
			DisplayUtil.displayError("Create Permit Charge Payment Failed");

		} finally {
			this.refreshPermitChargePayments();
			FacesUtil.returnFromDialogAndRefresh();
		}
	}

	public void updatePermitChargePayment() {

		try {
			// Modify PERMIT CHARGE PAYMENT codes

			getPermitService().modifyPermitChargePayment(
					this.getModifyPermitChargePayment());
			DisplayUtil.displayInfo("Create Permit Charge Payment Success");

		} catch (DAOException e) {
			logger.error("Create Permit Charge Payment Failed", e);
			DisplayUtil.displayError("Create Permit Charge Payment Failed");

		} catch (RemoteException ex) {
			logger.error("Create Permit Charge Payment Failed", ex);
			DisplayUtil.displayError("Create Permit Charge Payment Failed");

		} catch (Exception ex) {
			logger.error("Create Permit Charge Payment Failed", ex);
			DisplayUtil.displayError("Create Permit Charge Payment Failed");

		} finally {
			this.refreshPermitChargePayments();
			closeDialog();
		}
	}

	public void deletePermitChargePayment() {

		DisplayUtil.clearQueuedMessages();

		try {
			getPermitService().removePermitChargePayment(
					this.modifyPermitChargePayment);
			DisplayUtil.displayInfo("Delete Permit Charge Payment Success");

		} catch (DAOException e) {
			logger.error("Remove Permit Charge Payment failed", e);
			DisplayUtil.displayError("Remove Permit Charge Payment failed");

		} finally {

			refreshPermitChargePayments();
			closeDialog();
		}
	}

	public void cancelPermitChargePayment() {
		this.refreshPermitChargePayments();
		closeDialog();
	}
	
	public final TableSorter getPermitChargePaymentWrapper() {
		return this.permitChargePaymentWrapper;
	}

	public final void setPermitChargePaymentWrapper(
			TableSorter permitChargePaymentWrapper) {
		this.permitChargePaymentWrapper = permitChargePaymentWrapper;
	}
	
	public boolean chargePaymentValidated() {

		if (this.getModifyPermitChargePayment()
				.getTransactionDate() == null || Utility.isNullOrEmpty(this.getModifyPermitChargePayment()
				.getTransactionDate().toString())) {
			DisplayUtil.displayError("ERROR: Attribute Date is not set.");
			return false;
		}

		if (Utility.isNullOrEmpty(this.getModifyPermitChargePayment()
				.getTransactionType())) {
			DisplayUtil
					.displayError("ERROR: Attribute Charge/Payment Type is not set.");
			return false;
		}

		if (this.getModifyPermitChargePayment()
				.getTransactionAmount() == null || Utility.isNullOrEmpty(this.getModifyPermitChargePayment()
				.getTransactionAmount().toString())) {
			DisplayUtil.displayError("ERROR: Attribute Amount is not set.");
			return false;
		}
		
		Double d1 = this.getModifyPermitChargePayment().getTransactionAmount();
		Double d2 = 999999.99;
		int i1 = Double.compare(d1, d2);
		if (i1 > 0) {
			DisplayUtil
					.displayError("ERROR: Attribute Amount cannot be greater than $999,999.99.");
			return false;
		}

		Double d3 = 0d;
		Double d4 = this.getModifyPermitChargePayment().getTransactionAmount();
		int i2 = Double.compare(d3, d4);
		if (i2 > 0) {
			DisplayUtil
					.displayError("ERROR: Attribute Amount cannot have a negative value.");
			return false;
		}
		
		if (this.getModifyPermitChargePayment().isPayment()) {
			if (this.getModifyPermitChargePayment()
					.getCheckNumber() == null || Utility.isNullOrEmpty(this.getModifyPermitChargePayment()
					.getCheckNumber().toString())) {
				DisplayUtil.displayError("ERROR: Check Number is not set.");
				return false;
			}
			
			if (this.getModifyPermitChargePayment().getCheckNumber().length() < 2) {
				DisplayUtil
						.displayError("ERROR: Check Number must be at least two characters.");
				return false;
			}

			if (this.getModifyPermitChargePayment().getCheckNumber().length() > 12) {
				DisplayUtil
						.displayError("ERROR: Check Number cannot be longer than twelve characters.");
				return false;
			}
		}
		
		if (this.getModifyPermitChargePayment().isOtherCredit()) {
			
			if(!Utility.isNullOrEmpty(this.getModifyPermitChargePayment().getCheckNumber())) {
				if (this.getModifyPermitChargePayment().getCheckNumber().length() < 2) {
					DisplayUtil
							.displayError("ERROR: Check Number must be at least two characters.");
					return false;
				}
	
				if (this.getModifyPermitChargePayment().getCheckNumber().length() > 12) {
					DisplayUtil
							.displayError("ERROR: Check Number cannot be longer than twelve characters.");
					return false;
				}
			}	
		}

		return true;
	}
	
	/* END CODE: PERMIT CHARGE PAYMENT */
	
	public final boolean isDisabledUpdateButton() {
		if (isReadOnlyUser()) {
			return true;
		}

		if (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
			return false;
		}

		return true;
	}
	
	public final void dialogDone() {
		return;
	}
	
	public final boolean getNSRFeeSummaryEditAllowed() {
        return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
        		|| InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("editNSRFeeSummary");
    }
	
	public final String enterFeeSummaryEditMode() {
    	editMode = true;
        return FacesUtil.getCurrentPage(); // stay on same page
    }
	
	public String startGenerateInvoice() {
		setEditMode1(true);
		invoiceType = null;
		invoiceRefDate = null;
		return "dialog:generateNSRPermitInvoice";
	}
	
	public void cancelGenerateInvoice() {
		setEditMode1(false);
		FacesUtil.returnFromDialogAndRefresh();
	}
	
	public void generateNSRPermitInvoice() {
		
		// validate invoiceType and invoiceRefDate fields
		if(Utility.isNullOrEmpty(invoiceType)) {
			DisplayUtil.displayError("You must select a value for Invoice Type", invoiceType);
			return;
		}
		if(null == invoiceRefDate) {
			DisplayUtil.displayError("You must provide a value for Invoice Reference Date");
			return;
		}
		
		setEditMode1(false);
		
		try {
			if(getPermitService().generateNSRPermitInvoice(permit, invoiceType, invoiceRefDate)) {
				refreshPermitChargePayments();
				reloadPermit();
				// after the invoice is generated ensure that the invoiced charges minus received payments
				// is equal to the current balance
				if(!getPermitService().isCurrentBalanceValid(permit, invoiceRefDate)) {
					DisplayUtil.displayWarning("The sum of the invoiced charges minus the received payments does not equal" 
												+ " the Current Balance. Please review the fee information for correctness.");
				}
				DisplayUtil.displayInfo("Invoice generated successfully");
			} else {
				reloadPermit();
				DisplayUtil.displayError("Failed to generate invoice");
			}
		} catch(DAOException daoe) {	
			DisplayUtil.displayError("Failed to generate invoice");
			handleException(daoe);
		} catch(InvoiceGenerationException ige) {	
			DisplayUtil.displayError("Failed to generate invoice: " + ige.getMessage());
		}
		
		FacesUtil.returnFromDialogAndRefresh();
	}
	
	public boolean isInitialInvoiceGenerated() {
		boolean ret = false;
		for(PermitChargePayment pcp : ((PTIOPermit)this.permit).getPermitChargePaymentList()) {
			if(pcp.getTransactionType().equalsIgnoreCase(NSRBillingChargePaymentTypeDef.INITIAL_INVOICE)) {
				ret = true;
				break;
			}
		}
		
		return ret;
	}
	
	public boolean isFinalInvoiceGenerated() {
		boolean ret = false;
		for(PermitChargePayment pcp : ((PTIOPermit)this.permit).getPermitChargePaymentList()) {
			if(pcp.getTransactionType().equalsIgnoreCase(NSRBillingChargePaymentTypeDef.FINAL_INVOICE)) {
				ret = true;
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * Returns the list of invoice types that can be generated for this permit object.
	 * <br><br>
	 * Initial invoice can be generated only for permits i.e., initial invoice cannot be
	 * generated for waivers.
	 * <br><br>
	 * Initial invoice cannot be generated again if it has already been generated once.
	 * <br><br>
	 * Final invoice cannot be generated again if it has already been generated once.
	 * <br><br>
	 * @return
	 */
	
	public final List<SelectItem> getNSRBillingFeeTypeDefs() {
    	List<SelectItem> invoiceTypes = new ArrayList<SelectItem>();
    	
    	for(SelectItem si : NSRBillingFeeTypeDef.getData().getItems().getAllItems()) {
    		String invoiceType = (String)si.getValue();
    		
    		if(invoiceType.equalsIgnoreCase(NSRBillingChargePaymentTypeDef.INITIAL_INVOICE)
    			&& (isInitialInvoiceGenerated()
    					|| isFinalInvoiceGenerated()
    					|| !((PTIOPermit)permit).isActionTypePermit())) {
    				continue;
    			}
    		
    		if(invoiceType.equalsIgnoreCase(NSRBillingChargePaymentTypeDef.FINAL_INVOICE)
        			&& isFinalInvoiceGenerated()) {
        				continue;
        			}
    		
    		invoiceTypes.add(si);
    		
    	}
    	
		return invoiceTypes;
    }
	
	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}
	
	/**
	 * The default invoice reference date for initial invoice is 14 days prior to
	 * the date on which the invoice is being generated i.e., current date - 14-days.
	 * For final invoice, the default invoice date is the current date and for other
	 * invoice types, the invoice reference date is picked by the user.
	 *  
	 * @return
	 */
	public void computeInvoiceReferenceDate(ValueChangeEvent vce) {
		Calendar cal = new GregorianCalendar();
		if(((String)vce.getNewValue()).equalsIgnoreCase(NSRBillingFeeTypeDef.INITIAL_INVOICE)) {
			// subtract 14-days from current date
			cal.add(Calendar.DATE, -SystemPropertyDef.getSystemPropertyValueAsInteger("InitialInvoiceCutOffDays", null));
			setInvoiceRefDate(new Timestamp(cal.getTimeInMillis()));
		} else if(((String)vce.getNewValue()).equalsIgnoreCase(NSRBillingFeeTypeDef.FINAL_INVOICE)) {
			setInvoiceRefDate(new Timestamp(cal.getTimeInMillis()));
		} else {
			setInvoiceRefDate(null);
		}
	}
	
	public Timestamp getInvoiceRefDate() {
		return invoiceRefDate;
	}

	/** 
	 * set the invoice reference date with time portion set to 11:59:59 PM
	 */
	public void setInvoiceRefDate(Timestamp invoiceRefDate) {
		if(null != invoiceRefDate) {
			// set the time to 11:59:59 PM
			Calendar cal = new GregorianCalendar();
			cal.setTimeInMillis(invoiceRefDate.getTime());
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			this.invoiceRefDate = new Timestamp(cal.getTimeInMillis());
		} else {
			this.invoiceRefDate = invoiceRefDate;
		}
	}
	
	public Timestamp getMinimumInvoiceRefDate() {
		if(getPermitService().isTimesheetEntryEnabled()) {
			// if the IMPACT timesheet entry feature is enabled then there is no minimum date
			return null;
		} else {
			// system is still using AQDS to retrieve timesheet data
			Timestamp lastInvoiceRefDate = ((PTIOPermit)permit).getLastInvoiceRefDate();		
			if(null != lastInvoiceRefDate) {
				// the time portion of the lastInvoiceRefDate will be set to 23:59:59.999
				// so, add a millisecond to lastInvoiceRefDate to set the minimum date for 
				// invoice ref. date to the next day
				return new Timestamp(lastInvoiceRefDate.getTime() + 1);
			} else {
				return null;
			}
		}
	}
	
	/**
	 * returns todays date with time set to 23:59:59 
	 */
	public Timestamp getTodaysDate() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);

		return new Timestamp(cal.getTimeInMillis());
	}
	
	/**
	 * Returns the charge/payment types that can be generated for this permit object.
	 * <br><br>
	 * Initial invoice can be generated only for permits i.e., initial invoice cannot be
	 * generated for waivers.
	 * <br><br>
	 * Initial invoice cannot be generated again if it has already been generated once.
	 * <br><br>
	 * Final invoice cannot be generated again if it has already been generated once.
	 * <br><br>
	 * @return
	 */
	public final List<SelectItem> getNSRBillingChargePaymentTypeDefs() {
    	List<SelectItem> invoiceTypes = new ArrayList<SelectItem>();
    	    	
		for(SelectItem si : NSRBillingChargePaymentTypeDef.getData().getItems().getItems(getParent().getValue())) {
    		String invoiceType = (String)si.getValue();
    		
    		// preserve the transaction type of the row currently selected charge/payment row
    		if(null != modifyPermitChargePayment
    				&& invoiceType.equalsIgnoreCase(modifyPermitChargePayment.getTransactionType())) {
        		invoiceTypes.add(si);
        		continue;
        	}
    		
    		if(invoiceType.equalsIgnoreCase(NSRBillingChargePaymentTypeDef.INITIAL_INVOICE)
    			&& (isInitialInvoiceGenerated()
    					|| isFinalInvoiceGenerated()
    					|| !((PTIOPermit)permit).isActionTypePermit())) {
    				continue;
    			}
    		
    		if(invoiceType.equalsIgnoreCase(NSRBillingChargePaymentTypeDef.FINAL_INVOICE)
        			&& isFinalInvoiceGenerated()) {
        				continue;
        			}
    		
    		invoiceTypes.add(si);
    		
    	}

		return invoiceTypes;
    }

	public String getTotalFormatedCurrentBalance() {

		Locale locale = new Locale("en", "US");
		NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
		String totalAmountDueStr;
		
		BigDecimal totalCurrentBalance = (((PTIOPermit) this.permit)
				.getCurrentTotal());

		totalAmountDueStr = nf.format(totalCurrentBalance);

		return totalAmountDueStr;
	}

	public void setTotalFormatedCurrentBalance(String totalFormatedCurrentBalance) {
		this.totalFormatedCurrentBalance = totalFormatedCurrentBalance;
	}
	
	public final boolean isNSRFixedChargeEditOrDeleteAllowed() {
		return  InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
				|| (InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("editNSRFeeSummary")
						&& !getModifyNSRFixedCharge().isInvoiced());
	}
	
	/** 
	 * Builds a hashmap of non-attainment area (key) and the associated emissions offset (values)
	 * for a given permit
	 */
	public void buildAreaEmissionsOffsetMap() {
		if(permit instanceof PTIOPermit) {
			areaEmissionsOffsetMap.clear();
			
			for(EmissionsOffset eo : ((PTIOPermit)permit).getEmissionsOffsetList()) {
				String areaCd = eo.getNonAttainmentAreaCd();
				List<EmissionsOffset> emissionsOffsetList = areaEmissionsOffsetMap.get(areaCd);
				
				if(null == emissionsOffsetList) {
					emissionsOffsetList = new ArrayList<EmissionsOffset>();
				}
				
				emissionsOffsetList.add(eo);
				areaEmissionsOffsetMap.put(areaCd, emissionsOffsetList);
			}
		}
	}
	
	private void refreshAreaEmissionsOffsetList() {
		// build a map of non-attainment area and the associated emissions offsets for the 
		// given permit
		buildAreaEmissionsOffsetMap();
		
		Iterator<String> iter = areaEmissionsOffsetMap.keySet().iterator();
		areaEmissionsOffsetList.clear();
		
		while(iter.hasNext()) {
			String areaCd = iter.next();
			List<EmissionsOffset> emissionsOffsets = areaEmissionsOffsetMap.get(areaCd);
			AreaEmissionsOffset areaEmissionsOffset = new AreaEmissionsOffset();
			
			if(null != emissionsOffsets && emissionsOffsets.size() > 0) {
				// get the non-attainment area and attainment standard from the first item in the list
				// note - the value of non-attainment area and attainment standard will be same for all the
				// items in the list for a given non-attainment area
				EmissionsOffset eo = emissionsOffsets.get(0);
				
				areaEmissionsOffset.setNonAttainmentAreaCd(eo.getNonAttainmentAreaCd());
				areaEmissionsOffset.setAttainmentStandardCd(eo.getAttainmentStandardCd());
				areaEmissionsOffset.setEmissionsOffsets(emissionsOffsets);
				
				areaEmissionsOffsetList.add(areaEmissionsOffset);
			}
		}
	}
	
	public final String updatePermitEmissionsOffsets() {
		boolean validationPassed = true;
		List<EmissionsOffset> emissionsOffsets = ((PTIOPermit)permit).getEmissionsOffsetList();
		
		for(EmissionsOffset eo: emissionsOffsets) {
			ValidationMessage[] valMsgs = eo.validate();
			if(null != valMsgs && valMsgs.length > 0) {
				validationPassed = false;
				for(ValidationMessage msg: valMsgs) {
					DisplayUtil.displayError(msg.getMessage(), msg.getProperty());
				}
			}
		}
		
		if(validationPassed) {
			// proceed with the update
			try{
				for(EmissionsOffset eo: emissionsOffsets) {
					getPermitService().modifyPermitEmissionsOffset(eo);
				}
				return updatePermit();
			}catch(RemoteException re) {
				handleException(re);
			}
		}
		
		return null;
	}
	
	public boolean isOffsetTrackingInfoAvailable() {
		return areaEmissionsOffsetList.size() > 0 ? true : false;
	}
	
	public String refreshOffsetTrackingInformation() {
		return "dialog:refreshOffsetTrackingInformation";
	}
	
	public void regenerateOffsetTrackingEntries() {
		try {
			getPermitService().regenerateOffsetTrackingEntries((PTIOPermit)permit);
			loadPermit(permit.getPermitID());
			DisplayUtil.displayInfo("Offset tracking information refreshed successfully");
		}catch(RemoteException e) {
			DisplayUtil.displayError("Failed to refresh offset tracking information");
			handleException(e);
		}finally {
			closeDialog();
		}
	}
	
	public boolean isAllowedToDeletePermit() {
		return isStars2Admin()
				&& permit.isLegacyPermit();
	}
	
	/**
	 * Deletes a permit that is marked legacy and has non-empty value for permit
	 * number.
	 */
	public final void deleteLegacyPermit() {
		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO))
			return;

		try {
			if (getPermitService().deleteLegacyPermit(permit)) {
				// after deleting the permit, navigate to the second level
				// permit search menu and perform search with filters already
				// selected (if any)
				PermitSearch permitSearch = (PermitSearch) FacesUtil
						.getManagedBean("permitSearch");
				if (null != permitSearch) {
					permitSearch.search();
				}
				
				// enable permit search menu
				SimpleMenuItem permitSearchMenu = (SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_permitSearch");
				if (null != permitSearchMenu) {
					permitSearchMenu.setDisabled(false);
				}

				// disable permit detail menu
				SimpleMenuItem permitDetailMenu = (SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_permitDetail");
				if (null != permitDetailMenu) {
					permitDetailMenu.setDisabled(true);
				}

				setPopupRedirectOutcome("permitSearch");
				
				DisplayUtil.displayInfo("Permit " + permit.getPermitNumber()
						+ " has been deleted.");
			}
		} catch (DAOException e) {
			DisplayUtil.displayError("Could not delete permit "
					+ permit.getPermitNumber() + ".");
			handleException(e);
		}
	}

	public String getPopupRedirectOutcome() {
		if (null != popupRedirectOutcome) {
			FacesUtil.setOutcome(null, popupRedirectOutcome);
			popupRedirectOutcome = null;
		}
		return popupRedirectOutcome;
	}

	public void setPopupRedirectOutcome(String popupRedirectOutcome) {
		this.popupRedirectOutcome = popupRedirectOutcome;
	}	
	
	public boolean isAllowedToDeleteReferencedApp() {
		boolean ret = false;
		if(permit.isLegacyPermit()) {
			// always allowed to delete if it is a legacy permit
			ret = true; 
		} else {
			// allow to delete only if there are more than one referenced applications
			ret = getPermitApplicationCount() > 1; 
		}
		
		return ret;
	}
	
	public Date getOldestApplicationReceivedDate() {
		Timestamp oldestAppReceivedDt = null;
		
		for(Application app : permit.getApplications()) {
			Timestamp receivedDt = app.getReceivedDate();
			if(null == oldestAppReceivedDt) {
				oldestAppReceivedDt = new Timestamp(receivedDt.getTime());
			} else if(null != receivedDt
					&& receivedDt.before(oldestAppReceivedDt)) {
				oldestAppReceivedDt = new Timestamp(receivedDt.getTime());
			}
		}
		
		// get rid of the time portion in the date
		return null != oldestAppReceivedDt ? DateUtils.truncate(
				oldestAppReceivedDt, Calendar.DATE) : null;
	}
	
	public Date getDefaultMinimumDate() {
		Calendar epoch = new GregorianCalendar(1970, 0, 1);
		return new Date(epoch.getTimeInMillis());
	}
	

	public TableSorter getPermitConditionsWrapper() {
		return permitConditionsWrapper;
	}

	public void setPermitConditionsWrapper(TableSorter permitConditionsWrapper) {
		this.permitConditionsWrapper = permitConditionsWrapper;
	}
	
	
	public final void refreshPermitConditionList() {

		this.permitConditionsWrapper = new TableSorter();

		try {
			this.permit.setPermitConditionList(
					getPermitConditionService().retrievePermitConditionList(this.permit.getPermitID()));
			this.permitConditionsWrapper.setWrappedData(this.permit.getPermitConditionList());
		} catch (RemoteException e) {
			DisplayUtil.displayError("Failed to retrieve permit condition list");
			logger.error("failed to get PERMIT CONDITION LIST", e);
		}
	}
	
	public final String closePopuploadPermit() {
		
		
		return loadPermit();
	}

	public Integer getTempPermitId() {
		return tempPermitId;
	}

	public void setTempPermitId(Integer tempPermitId) {
		this.tempPermitId = tempPermitId;
	}

}
