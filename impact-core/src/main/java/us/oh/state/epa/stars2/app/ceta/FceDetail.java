package us.oh.state.epa.stars2.app.ceta;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.aspose.words.Document;

import oracle.adf.view.faces.component.UIXTable;
import oracle.adf.view.faces.event.ReturnEvent;
import oracle.adf.view.faces.event.SelectionEvent;
import oracle.adf.view.faces.model.RowKeySet;
import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.app.tools.BulkOperationsCatalog;
import us.oh.state.epa.stars2.app.workflow.ActivityProfile;
import us.oh.state.epa.stars2.bo.EnforcementActionService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.bo.StackTestService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.CetaBaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ceta.Evaluator;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceAmbientMonitorLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceApplicationSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceAttachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceContinuousMonitorLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceEmissionsInventoryLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FcePermitCondition;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceStackTestSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.database.dbObjects.ceta.InspectionNote;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SiteVisit;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityCemComLimit;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.FceAttachmentTypeDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.TemplateDocTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentGenerationException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.util.TimestampUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.ConfirmWindow;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.document.AttachmentEvent;
import us.oh.state.epa.stars2.webcommon.document.AttachmentException;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentListener;
import us.oh.state.epa.stars2.webcommon.documentgeneration.DocumentGenerator;
import us.oh.state.epa.stars2.webcommon.documentgeneration.facility.FacilityDocumentGenerator;
import us.oh.state.epa.stars2.webcommon.documentgeneration.inspection.InspectionDocumentGenerator;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.app.continuousMonitoring.ContinuousMonitorDetail;
import us.wy.state.deq.impact.def.FCEInspectionReportStateDef;
import us.wy.state.deq.impact.def.FCEPreSnapshotTypeDef;

/*
 * Backing bean for a single Inspection to create or modify the Inspection.
 */
public class FceDetail extends TaskBase implements IAttachmentListener {
	
	private static final long serialVersionUID = 2717781104439368839L;

	// Inspection report sections used in Inspection Report generation.
	public static final String INSPECTION_CONCERNS = "Inspection Concerns";
	public static final String FILE_REVIEW_OR_RECORDS_REQUEST ="File Review/Records Request";
	public static final String REGULATORY_DISCUSSION = "Regulatory Discussion";
	public static final String PHYSICAL_INSPECTION_OF_PLANT = "Physical Inspection of Plant";
	public static final String AMBIENT_MONITORING = "Ambient Monitoring";
	public static final String OTHER_INFORMATION = "Other Information";
	public static final String PERMIT_APPLICATIONS = "Permit Applications";
	public static final String PERMIT_CONDITIONS_AND_STATUS = "Permit Conditions and Status";
	public static final String PERMITS ="Permits";
	public static final String STACK_TESTS = "Stack Tests";
	public static final String COMPLIANCE_REPORTS = "Compliance Reports";
	public static final String EMISSIONS_INVENTORIES = "Emissions Inventories";
	public static final String SITE_VISITS = "Site Visits";
	public static final String CEM_COM_CMS = "CEM/COM/CMS";
	public static final String AMBIENT_MONITORS = "Ambient Monitors";
	public static final String CONTROL_EQUIPMENT = "Control Equipment";
	public static final String RELEASE_POINTS = "Release Points";
	public static final String EMISSION_UNITS ="Emission Units";
	public static final String ATTACHMENTS = "Attachments";
	
	private Integer fceId;
	private Integer visitId;
    private String facilityId;
    private Facility facility;
    private FullComplianceEval fce;
    private SiteVisit siteVisit;
    private boolean editable;
    private boolean schedEditable;
    private boolean complEditable;
    private boolean haveSubmitted = false;

    private boolean newFceObject = false;
    private boolean existingFceObject = false;
    
    private boolean cannotDelete;  // used when trying to delete.
    private String cannotdeleteReason;
    private String afsWarning;
    private boolean blankOutPage = false;
    private static final int CONTENT_WIDTH = 845;
    private boolean newNote;
    private InspectionNote tempComment;
    private InspectionNote modifyComment;
    private boolean noteReadOnly;
    private static final String COMMENT_DIALOG_OUTCOME = "dialog:inspectionNoteDetail";
    private static final String PRE_INSPECTION_DATA_MENU = "ceta.fceDetail.fcePreData";
    private static final String PERMIT_CONDITIONS_MENU = "ceta.fceDetail.fcePermitConditions";
    private static final String OBSERVATIONS_MENU = "ceta.fceDetail.fceObservations";
    
    private TableSorter allFCEs = new TableSorter();
    
    private EnforcementActionService enforcementActionService;
	private FacilityService facilityService;
	private FullComplianceEvalService fullComplianceEvalService;
	private StackTestService stackTestService;
	
	private String infoStatement;

    private List<Integer> corrPermitIdsToAssociate = new ArrayList<Integer>();
    private List<Integer> corrPermitIdsToDisassociate = new ArrayList<Integer>();

	private boolean permitSelectionEditable;
	private boolean addPermitConditionsAutomatically = true;
	private boolean permitConditionsSelectionEditable;
	
	private boolean startDateEditMode = false;	

	private List<FceApplicationSearchLineItem> applicationList = new ArrayList<FceApplicationSearchLineItem>();
	private TableSorter applicationWrapper = new TableSorter();
	private boolean hasPreservedPA = false;
	private boolean dateRangeChangePA = false;
	
	private List<Permit> permitList = new ArrayList<Permit>();
	private TableSorter permitWrapper = new TableSorter();
	private boolean hasPreservedPT = false;
	private boolean dateRangeChangePT = false;	

	private List<FceStackTestSearchLineItem> stackTestList = new ArrayList<FceStackTestSearchLineItem>();
	private TableSorter stackTestWrapper = new TableSorter();
	private boolean hasPreservedST = false;
	private boolean dateRangeChangeST = false;

	private List<ComplianceReportList> complianceReportList = new ArrayList<ComplianceReportList>();
	private TableSorter complianceReportWrapper = new TableSorter();
	private boolean hasPreservedCR = false;
	private boolean dateRangeChangeCR = false;
	
	private List<Correspondence> correspondenceList = new ArrayList<Correspondence>();
	private TableSorter correspondenceWrapper = new TableSorter();
	private boolean hasPreservedDC = false;
	private boolean dateRangeChangeDC = false;
	
	//Emissions Inventory
	private List<FceEmissionsInventoryLineItem> emissionsInventoryList = new ArrayList<FceEmissionsInventoryLineItem>();
	private TableSorter emissionsInventoryWrapper = new TableSorter();
	private boolean hasPreservedEI = false;
	private boolean dateRangeChangeEI = false;	
	
	private List<SiteVisit> siteVisitList = new ArrayList<SiteVisit>();
	private TableSorter siteVisitsWrapper = new TableSorter();
	private boolean hasPreservedSiteVisit = false;
	private boolean dateRangeChangeSiteVisit = false;
	
	private List<FceAmbientMonitorLineItem> ambientMonitorList = new ArrayList<FceAmbientMonitorLineItem>();
	private TableSorter ambientMonitorsWrapper = new TableSorter();
	private boolean hasPreservedAmbientMonitor = false;
	private boolean dateRangeChangeAmbientMonitor = false;
	
	private List<FceContinuousMonitorLineItem> continuousMonitorList = new ArrayList<FceContinuousMonitorLineItem>();
	private TableSorter continuousMonitorsWrapper = new TableSorter();
	private boolean hasPreservedContinuousMonitor = false;
	private boolean dateRangeChangeContinuousMonitor = false;
	
	private TableSorter includedPermitConditions = new TableSorter();
	private TableSorter excludedPermitConditions = new TableSorter();
	
	private TableSorter inspectionReportSectionsWrapper;
	private List<String> inspectionReportSections;
	private List<String> selectedInspectionReportSections = new ArrayList<String>();

	/*
	 * indicates whether all the sections on the generate inspection report 
	 * popup should be selected or not
	 */
	private boolean selectAllInspReportSections = false; 
	
	private String currentInspRptPath = null;
	
	// Used to temporarily set the Start Date and End Date in the
	// pre-inspection snapshot(s) to read only. This is done in order
	// to refresh the values of these fields upon clicking on the
	// Clear Preserved List button. See TFS task 7712
	private boolean snaphotSearchDatesReadOnly;

	// Set from the fceAttachments.jsp upon clicking on the
	// generate button for a given attachment in the
	// attachments datagrid
	private String templateDocTypeCd;

	// Set from the fceAttachments.jsp upon clicking on the
	// upload button for a given attachment in the
	// attachments datagrid
	private String docTypeCd;

	// Path where the generated document is stored upon clicking
	// on the generate button in the attachments daatgrid
	private String generatedDocumentFilePath;
	
	private static final Map<String, String> sectionTagMap = new HashMap<String, String>() {

		private static final long serialVersionUID = -6391412644516861215L;

		{
			put(INSPECTION_CONCERNS, "inspectionConcerns");
			put(FILE_REVIEW_OR_RECORDS_REQUEST, "inspectionFileReview");
			put(REGULATORY_DISCUSSION, "inspectionRegDiscussion");
			put(PHYSICAL_INSPECTION_OF_PLANT, "inspectionPhysical");
			put(AMBIENT_MONITORING, "inspectionAmbient");
			put(OTHER_INFORMATION, "inspectionOther");
			put(PERMIT_APPLICATIONS, "inspectionPermitApplicationList");
			put(PERMIT_CONDITIONS_AND_STATUS, "inspectionPermitConditions");
			put(PERMITS, "inspectionPermitList");
			put(STACK_TESTS, "inspectionStackTestList");
			put(COMPLIANCE_REPORTS, "inspectionCompRptList");
			put(EMISSIONS_INVENTORIES, "inspectionEIList");
			put(SITE_VISITS, "inspectionSiteVisitList");
			put(CEM_COM_CMS, "inspectionCEMCOMCMSList");
			put(AMBIENT_MONITORS, "inspectionAmbientMonitorList");
			put(CONTROL_EQUIPMENT, "inspectionControlEquipmentList");
			put(RELEASE_POINTS, "inspectionReleasePointList");
			put(EMISSION_UNITS, "facilityEmissionUnitTable");
			put(ATTACHMENTS, "inspectionAttachments");
		}
	};
	
	
	private static final Map<String, String> sectionHeadingTagMap = new HashMap<String, String>() {

		private static final long serialVersionUID = -9068962798430531944L;

		{
			put(INSPECTION_CONCERNS, "inspectionConcernsHeading");
			put(FILE_REVIEW_OR_RECORDS_REQUEST, "inspectionFileReviewHeading");
			put(REGULATORY_DISCUSSION, "inspectionRegDiscussionHeading");
			put(PHYSICAL_INSPECTION_OF_PLANT, "inspectionPhysicalHeading");
			put(AMBIENT_MONITORING, "inspectionAmbientHeading");
			put(OTHER_INFORMATION, "inspectionOtherHeading");
			put(PERMIT_APPLICATIONS, "inspectionPermitApplicationListHeading");
			put(PERMIT_CONDITIONS_AND_STATUS, "inspectionPermitConditionsHeading");
			put(PERMITS, "inspectionPermitListHeading");
			put(STACK_TESTS, "inspectionStackTestListHeading");
			put(COMPLIANCE_REPORTS, "inspectionCompRptListHeading");
			put(EMISSIONS_INVENTORIES, "inspectionEIListHeading");
			put(SITE_VISITS, "inspectionSiteVisitListHeading");
			put(CEM_COM_CMS, "inspectionCEMCOMCMSListHeading");
			put(AMBIENT_MONITORS, "inspectionAmbientMonitorListHeading");
			put(CONTROL_EQUIPMENT, "inspectionControlEquipmentListHeading");
			put(RELEASE_POINTS, "inspectionReleasePointListHeading");
			put(EMISSION_UNITS, "facilityEmissionUnitTableHeading");
			put(ATTACHMENTS, "inspectionAttachmentsHeading");
		}
	};
	
	private final String PA_SNAPSHOT_DESC = "Applications";
	private final String PT_SNAPSHOT_DESC = "Permits";
	private final String ST_SNAPSHOT_DESC = "Stack Tests";
	private final String CR_SNAPSHOT_DESC = "Compliance Reports";
	private final String CORR_SNAPSHOT_DESC = "Correspondence";
	private final String EI_SNAPSHOT_DESC = "Emissions Inventories";
	private final String CEM_SNAPSHOT_DESC = "CEM/COM/CMS Limits";
	private final String AM_SNAPSHOT_DESC = "Ambient Monitors";
	private final String SV_SNAPSHOT_DESC = "Site Visits";
	
	public StackTestService getStackTestService() {
		return stackTestService;
	}

	public void setStackTestService(StackTestService stackTestService) {
		this.stackTestService = stackTestService;
	}

	public FullComplianceEvalService getFullComplianceEvalService() {
		return fullComplianceEvalService;
	}

	public void setFullComplianceEvalService(
			FullComplianceEvalService fullComplianceEvalService) {
		this.fullComplianceEvalService = fullComplianceEvalService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}
    
    public EnforcementActionService getEnforcementActionService() {
		return enforcementActionService;
	}
	public void setEnforcementActionService(EnforcementActionService enforcementActionService) {
		this.enforcementActionService = enforcementActionService;
	}

    public FceDetail() {
        super();
    }
    
    public String from2ndLevelMenu() {
//        submit();
//        facility = null;  // force re-reading the profile
//        fce = null;  // force re-reading the Inspection
        // if(fce != null) initializeAttachmentBean();
        
        blankOutPage = false;
        facility = null;  // force re-reading the profile
        fce = null;  // force re-reading the Inspection
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        
        try {
            submitInternal();
        } finally {
            clearButtonClicked();
        }        
        return "ceta.fceDetail";
    }
    
    public final String submit() {
        blankOutPage = false;
        facility = null;  // force re-reading the profile
        fce = null;  // force re-reading the Inspection
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        setFromTODOList(false);
//    	//reset referenceReviewStartDate
//    	setReferenceReviewStartDate(null);
        try {
            return submitInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    public final void submitSilently(String facilityId) {
        blankOutPage = false;
        // in case a visit has changed
        if(haveSubmitted && facilityId.equals(this.facilityId) && fceId != null) {
            try {
                fce = getFullComplianceEvalService().retrieveFce(facilityId, fceId);
            } catch (RemoteException e) {
                blankOutPage = true;
                handleException("Failed to retrieve Inspection " + fceId, e);
            }
            if(fce != null) {
                ((SimpleMenuItem)FacesUtil.getManagedBean("menuItem_fceDetail")).setDisabled(false);
            }
        }
    }
    
	private final String submitInternal() {

		blankOutPage = false;
		String ret = null;
		boolean ok = true;

		if (fceId != null) {

			newFceObject = false;
			haveSubmitted = true;

			try {

				fce = getFullComplianceEvalService().retrieveFce(facilityId, fceId);
				existingFceObject = true;

				if (fce != null) {
					facilityId = fce.getFacilityId();

					initializeAttachmentBean();
					ok = getAssociatedFacility();
					if (!ok) {
						blankOutPage = true;
						DisplayUtil.displayError("Facility " + facilityId + " for Inspection " + fceId + " not found.");
					}

				} else {
					ok = false;
					blankOutPage = true;
					DisplayUtil.displayError("Inspection is not found with that number.");
				}

			} catch (RemoteException e) {
				handleException("Failed to retrieve Inspection " + fceId, e);
				ok = false;
			}

			if (ok) {
				ret = "ceta.fceDetail";
				((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_fceDetail")).setDisabled(false);
				StackTestSearch stSearch = (StackTestSearch) FacesUtil.getManagedBean("stackTestSearch");
				stSearch.setFromFacility(true);
			} else
				return null;
		}

		return ret;
	}
//Aftre 10.0 release, this method is to retrieved current facility, and will not make any change on the inspection fp_id   
    private boolean getCurrentFacility() {
        // determine if current facility or historic version needed.
//        boolean replaceFpIdInFce = true;
//        if(fce.getFpId() != null && fce.isReportStateComplete()) {
//            replaceFpIdInFce = false;
//        }
        boolean ok = true;
        try {
//          get current, if needed
            if(facility == null || facility.getVersionId() != -1 || !facility.getFacilityId().equals(facilityId)) {
                // Replace facility with current
                facility = getFacilityService().retrieveFacility(facilityId);
                if(facility == null){
                    ok = false;
                    DisplayUtil.displayError("Facility " + facilityId + "  not located.");
                } else {
                    facilityId = facility.getFacilityId();
                }
            }
//            if(replaceFpIdInFce) {
//                if(ok) {
//                    fce.setFpId(facility.getFpId()); // The updated fpId will be stored if the record is actually updated.
//                }
//            }
        } catch(RemoteException re) {
            ok = false;
            DisplayUtil.displayError("Unable to retrieve the facility inventory");
            String s = "Error retrieving facility inventory for Inspection "+ fce.getId() + ", facility=" + facility + " and fpId=" + fce.getFpId();
            handleException(s, re);
        }
        return ok;
    }
    
    //this method is to retrieve the 'Facility' associated with the inspection report 
    private boolean getAssociatedFacility() {
    	boolean ok = false;
    	try{
    		if (fce != null && fce.getFpId() != null){
    			facility = getFacilityService().retrieveFacility(fce.getFpId());
    			if (facility == null){
    				DisplayUtil.displayError("Facility " + facilityId + "  not located.");
                } else {
                	ok = true;
                    facilityId = facility.getFacilityId();
                }
    		} 
    	} catch(RemoteException re) {
            ok = false;
            DisplayUtil.displayError("Unable to retrieve the facility inventory");
            String s = "Error retrieving facility inventory for Inspection "+ fce.getId() + ", facility=" + facility + " and fpId=" + fce.getFpId();
            handleException(s, re);
        }
        return ok;	
    } 
    

    public LinkedHashMap<String, Timestamp> getScheduleChoices() {
        LinkedHashMap<String, Timestamp> scheds = new LinkedHashMap<String, Timestamp>();
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH)/3 * 3;
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Timestamp ref = new Timestamp(cal.getTimeInMillis());
		ref.setNanos(0);
		if (fce.getScheduledTimestamp() != null
				&& fce.getScheduledTimestamp().before(ref))
			ref = fce.getScheduledTimestamp();
		cal.setTime(new Date(ref.getTime()));
		for (int i = 0; i < 40; i++) {
			Timestamp use = ref;
			// make sure timestamp used will match something in picklist.
			if (CetaBaseDB.getScheduled(ref).equals(
					CetaBaseDB.getScheduled(fce.getScheduledTimestamp()))) {
				use = fce.getScheduledTimestamp();
			}
			scheds.put(CetaBaseDB.getScheduled(ref), use);
			cal.add(Calendar.MONTH, 3);
			ref = new Timestamp(cal.getTimeInMillis());
		}
		return scheds;
    }
    
    public final void save() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            saveInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private final void saveInternal() {
  
        if(newFceObject) {
            ValidationMessage[] validationMessages = fce.validate();
            if (displayValidationMessages("body:",  validationMessages)) {
                return;
            }
            //creating new inspection, pull latest facility inventory
            boolean ok = getCurrentFacility();
            if(!ok) {
                blankOutPage = true;
                setEditable(false);
                return;
            }
            try {
                // If new Inspection, supply the fpId
                if(fce.getFpId() ==  null) fce.setFpId(facility.getFpId());
                fce.setFacilityId(facility.getFacilityId()); // set facility id for use in creating directory

                fce.setCreatedById(InfrastructureDefs.getCurrentUserId());
                fce = getFullComplianceEvalService().createFce(fce);
                fce = getFullComplianceEvalService().retrieveFce(facilityId, fce.getId());
                fceId = fce.getId();
                existingFceObject = true;
                newFceObject = false;
            } catch(RemoteException re) {
                DisplayUtil.displayError("Failed to create the Inspection.");
                blankOutPage = true;
                setEditable(false);
                handleException("Failed to create the Inspection for facility " + facilityId, re);
                return;
            }
            setEditable(false);
            DisplayUtil.displayInfo("Inspection created successfully.");
        } else if(existingFceObject){
        	ValidationMessage[] validationMessages = fce.validate();
            if (displayValidationMessages("body:",  validationMessages)) {
                return;
            } 
            if (fce.isLegacyInspection() || fce.isPre10Legacy()) {
            	fce.setComplianceStatusCd(null);
			} else {
				fce.setMemo(null);
			}
            try {
                getFullComplianceEvalService().modifyFce(fce);
                DisplayUtil.displayInfo("Inspection updated successfully.");
                setEditable(false);
            } catch (RemoteException e) {
            	handleException(e);
                return;
            } finally {
            	try {
					fce = getFullComplianceEvalService().retrieveFce(facilityId, fce.getId());
				} catch (DAOException e) {
					logger.error("Could not retrieve inspection : " + fce.getInspId());
					DisplayUtil.displayError("Failed to update inspection "
							+ fce.getInspId()
							+ ". The inspection may no longer exist.");
					handleBadDetailAndRedirect();
				} catch (RemoteException e) {
					logger.error("Could not retrieve inspection : " + fce.getInspId());
					DisplayUtil.displayError("Failed to update inspection "
							+ fce.getInspId()
							+ ". The inspection may no longer exist.");
					handleBadDetailAndRedirect();
				}
        		
            	
            }
        } else {
            DisplayUtil.displayError("should not get here");
            blankOutPage = true;
            setEditable(false);
        }
        
        ((SimpleMenuItem) FacesUtil
				.getManagedBean("menuItem_fceDetail"))
				.setDisabled(false);
    }
    
    public final String viewDetail() {
		this.setEditable(false);
		this.setEditMode(false);
		String ret = null;
		fce = null;

		logger.debug("viewDetail getFceId() = " + getFceId());
		loadInspection();

		if (fce != null) {
			logger.debug("viewDetail this.getFce().getId() = "
					+ this.getFce().getId());
			if (this.getFce().getId() != null) {
				ret = "ceta.fceDetail";
			} else {
				ret = null;
				DisplayUtil
						.displayError("Inspection is not found with that number.");
			}
		} else {
			DisplayUtil
					.displayError("Inspection is not found with that number.");
		}
		return ret;
	}

	private void loadInspection() {
		try {
			fce = getFullComplianceEvalService().retrieveFce(facilityId, fceId);
			if (fce != null) {
				setFacilityId(fce.getFacilityId());
                if(!getAssociatedFacility()) {
                    blankOutPage = true;
                    DisplayUtil.displayError("Facility " + facilityId + " for Inspection " + fceId + " not found.");
                }
			}
		} catch (RemoteException e) {
			handleException(e);
		}
	}

	public final void close() {
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public boolean isSchedAdminEditOnly() {
        boolean rtn = false;
        if(fce != null) {
            if(fce.getSchedAfsId() != null && isPrivledged()) rtn = true;
        }
        return rtn;
    }
    
    public boolean isPrivledged() {
        return isStars2Admin() || isFceScheduler();
    }
    
    public boolean isAllowSchedEditOperations() {
        boolean rtn = false;
        if(fce != null) {
            if(isCetaUpdate() && fce.getSchedAfsId() == null || isPrivledged()) rtn = true;
            rtn = rtn && !isReadOnlyUser();
        }
        return rtn;
    }
    
    public boolean isComplAdminEditOnly() {
        boolean rtn = false;
        if(fce != null) {
            if(isCetaUpdate() && fce.getEvalAfsId() == null || isPrivledged()) rtn = true;
            rtn = rtn && !isReadOnlyUser();
        }
        return rtn;
    }
    
    public boolean isAllowComplEditOperations() {
        boolean rtn = false;
        if (fce != null) {
            if(isCetaUpdate() && fce.getEvalAfsId() == null || isPrivledged()) rtn = true;
        }
        return rtn;
    }
    
    public boolean isAllowDelete() {
    	
    	boolean ret = false;
    	if (fce != null) {
    		if (fce.getInspectionReportStateCd() == null 
    				|| (fce.getInspectionReportStateCd() != null && fce.getInspectionReportStateCd().equals(FCEInspectionReportStateDef.INITIAL))) {
    			ret = true;
    		} else if (isPrivledged()) {
    			ret = true;
    		}
    	}
        ret = ret && !isReadOnlyUser();

    	return ret;
    }
    
    public void setAllowDelete(boolean allowDelete) {
    	
    }
    
    public final void editNewFCE(String facilityId) {
        complEditFce();  // for new object allow editing of both components
        schedEditFce();  // for new object allow editing of both components
        newFceObject = true;
        existingFceObject = false;
        fce = new FullComplianceEval();
        Calendar cal = Calendar.getInstance();
        Timestamp today = new Timestamp(cal.getTimeInMillis());
        fce.setCreatedDt(today);
//        fceId = null;
        this.facilityId = facilityId;
        fce.setFacilityId(facilityId);
        getCurrentFacility();
        initializeAttachmentBean();
        blankOutPage = false;
    }
    
    public final void editFce() {
        complEditFce();
        schedEditFce();
        setEditable(true);
    }
    
    // this is not currently used because we decided noo to split editing into two components
//  Removed from jsp
//    <af:commandButton text="Completion Edit"
//        action="#{fceDetail.complEditFce}"
//        disabled="#{!fceDetail.allowComplEditOperations}"
//        rendered="#{! fceDetail.editable && !fceDetail.readOnlyUser}" />
    public final void complEditFce() {
        setComplEditable(true);
        setEditable(true);
    }
    
    // this is not currently used because we decided noo to split editing into two components
// Removed from jsp
//    <af:commandButton text="Schedule Edit"
//        action="#{fceDetail.schedEditFce}"
//        disabled="#{!fceDetail.allowSchedEditOperations}"
//        rendered="#{! fceDetail.editable && !fceDetail.readOnlyUser}" />
    public final void schedEditFce() {
        setSchedEditable(true);
        setEditable(true);
    }
    
    public final String fceDelete() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return fceDeleteInternal();
        } finally {
            clearButtonClicked();
        }
    }

    private final String fceDeleteInternal() {
    	
        if (newFceObject) {
            DisplayUtil.displayError("This new FCE has not been saved, no delete needed.");
            return null;
        }
        
        cannotDelete = false;
        cannotdeleteReason = "Click <b>Delete</b> to remove the Inspection.";
        afsWarning = null;
        
        if (fce.isUsEpaCommitted() && !isFceScheduler()) {
            cannotDelete = true;
            cannotdeleteReason = "This Inspection cannot be deleted because it is marked committed to the US EPA and you do not have the Inspection Scheduler Role.";
        } else {
            if (fce.getAssocSiteVisits().length > 0) {
                cannotDelete = true;
                cannotdeleteReason = "You cannot delete this Inspection while it has associated Site Visits.";
            }
            if (fce.getAssocStackTests().length > 0) {
                if(cannotDelete) {
                    cannotdeleteReason = cannotdeleteReason + " and associated Stack Tests";
                } else {
                    cannotdeleteReason = "You cannot delete this Inspection while it has associated Stack Tests.";
                }
                cannotDelete = true;
            }
            
			// Adding logic to prevent deletion of a inspection when a compliance event is referencing it.
			if (fce.getAssocComplianceStatusEvents().size() > 0) {
				cannotDelete = true;
				cannotdeleteReason = "You cannot delete this Inspection while it has associated Compliance Status Event(s).";
			}
			
			// Adding logic to prevent deletion of a inspection when it is referred by another inspection as Last Inpection
			if (fce.getInspectionsReferencedIn().size() > 0){
				cannotDelete = true;
				cannotdeleteReason = "You cannot delete this Inspection while it is referred by other Inspection(s) as Last Completed Inspection.";
			}
			
        }
        
        if (!(fce.isLegacyInspection() || fce.isPre10Legacy() 
        		|| (fce.getInspectionReportStateCd() != null 
        			&& (fce.getInspectionReportStateCd().equals(FCEInspectionReportStateDef.DEAD_ENDED)
        					|| fce.getInspectionReportStateCd().equals(FCEInspectionReportStateDef.INITIAL))))) {
			cannotDelete = true;
			cannotdeleteReason = "You cannot delete this Inspection unless it is a legacy inspection, "
					+ " the Inspection is in the Initial state, or the Inspection has been dead-ended "
					+ "by canceling the associated workflow.";        	
        }
        
        if (fce.getSchedAfsId() != null) {
            afsWarning = "If you delete this Inspection, you will lose the fact that it was committed/promised to EPA.";
        }
        
        if (fce.getEvalAfsId() != null) {
            if (afsWarning == null) {
                afsWarning = "";
            } else {
                afsWarning = afsWarning + "<br>";
            }
            afsWarning = afsWarning  + "If you delete this Inspection, you will lose the fact that it is exported to AFS.";
        }

        return "dialog:confirmFceDelete";

    }

    public boolean getDisplayComplianceEventsTable() {
    	return isCannotDelete() && !getFce().getAssocComplianceStatusEvents().isEmpty();
    }
    
    public final void deleteFCE2() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            fceDelete2Internal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private void fceDelete2Internal() {
        try {
            getFullComplianceEvalService().removeFce(fce);
            fceId = null;
            facilityId = null;
            DisplayUtil.displayInfo("Inspection deleted successfully.");
            handleBadDetail();
        } catch(RemoteException re) {
            handleException("Failed to delete Inspection " + fce.getId(), re);
            return;
        }
        FacesUtil.returnFromDialogAndRefresh();
    }

    public final String cancelEdit() {
        setEditable(false);
        if(newFceObject) {
            DisplayUtil.displayInfo("Operation cancelled, Inspection not created.");
            blankOutPage = true;
            FceSiteVisits fceSiteVisit = (FceSiteVisits) FacesUtil.getManagedBean("fceSiteVisits");
            return fceSiteVisit.initFCEs();
        } else {
            DisplayUtil.displayInfo("Operation cancelled, Inspection not updated");
            try {
                fce = getFullComplianceEvalService().retrieveFce(facilityId, fce.getId());     
                boolean ok = true;
                if (fce != null) {
                	if (null == facilityId) {
                		facilityId = fce.getFacilityId();
                	}
                    initializeAttachmentBean();
                    if(!ok) {
                        blankOutPage = true;
                        DisplayUtil.displayError("Facility " + facilityId + " for Inspection " + fceId + " not found.");
                    }
                } else {
                    ok = false;
                    blankOutPage = true;
                    DisplayUtil.displayError("Inspection is not found with that number.");
                }
            } catch(RemoteException re) {
                blankOutPage = true;
                handleException("Failed to re-read Inspection " + fce.getId(), re);
            }
        }
        return "ceta.fceDetail";
    }
    
    public final String generateLetter() {
        BulkOperationsCatalog bOpCat = (BulkOperationsCatalog)FacesUtil.getManagedBean("bulkOperationsCatalog");
        // do same initialization that Tools-->DocumentGeneration would have done.
        String rtn = bOpCat.setBulkOpToDGEN();
        // populate only facilityId
        bOpCat.setFacilityId(facilityId);
        // navigate to jsp page for Tools-->DocumentGeneration
        return rtn;
    }

    public String pickReassignEt() {
        // reset the selection flags in case set previously and then cancelled.
        for(StackTest st : fce.getStackTests()) {
            st.setSelected(false);
        }
        return "dialog:cetaReassociateEtFce";
    }
    
    public final void saveReassignEt() {
        // (multiple) selected visits are reassigned to this Inspection.
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            saveReassignInternalEt();
        } finally {
            clearButtonClicked();
        }
    }
    
    private final void saveReassignInternalEt() {
        try {
            getStackTestService().saveReassign(fceId, fce.getStackTests());
            DisplayUtil.displayInfo("Reassociations completed sucessfully");
//  DENNIS  Do we need this?
//            // Update Search results in case results should change.
//            SiteVisitSearch visitSearchBean = (SiteVisitSearch) FacesUtil.getManagedBean("siteVisitSearch");
//            visitSearchBean.silentSearch();
            
            submitInternal();
        } catch (RemoteException e) {
            String s = "Failed to reassign visits for Inspection " + fceId;
            DisplayUtil.displayError(s);
            handleException(s, e);
        }
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public String pickReassign() {
        // reset the selection flags in case set previously and then cancelled.
        for(SiteVisit sv : fce.getSiteVisits()) {
            sv.setSelected(false);
        }
        return "dialog:cetaReassociateFce";
    }
    
    public final void saveReassign() {
        // (multiple) selected visits are reassigned to this Inspection.
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            saveReassignInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private final void saveReassignInternal() {
        try {
            getFullComplianceEvalService().saveReassign(fceId, fce.getSiteVisits());
            DisplayUtil.displayInfo("Reassociations completed sucessfully");
            
            // Update Search results in case results should change.
            SiteVisitSearch visitSearchBean = (SiteVisitSearch) FacesUtil.getManagedBean("siteVisitSearch");
            visitSearchBean.silentSearch();
            
            submitInternal();
        } catch (RemoteException e) {
            String s = "Failed to reassign visits for Inspection " + fceId;
            DisplayUtil.displayError(s);
            handleException(s, e);
        }
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public final void cancelReassign() {
        DisplayUtil.displayInfo("Reassociations cancelled");
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public void initializeAttachmentBean() {
        Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
        a.addAttachmentListener(this);
        a.setStaging(false);
        a.setNewPermitted(isCetaUpdate());
        a.setUpdatePermitted(isCetaUpdate()&&!isInspectionDetailLocked());
        a.setFacilityId(fce.getFacilityId());
        a.setSubPath("Inspection");
        if(fce.getId() != null) a.setObjectId(Integer.toString(fce.getId()));
        else a.setObjectId("");
        a.setSubtitle(null);
        a.setTradeSecretSupported(false);
        a.setHasDocType(true);
        a.setAttachmentTypesDef(FceAttachmentTypeDef.getData()
                .getItems());
        a.setAttachmentList(getAttachmentList().toArray(
                new Attachment[0]));
        a.setLocked(isInspectionDetailLocked());
        a.cleanup();
    }

    public void cancelAttachment() {
        FacesUtil.returnFromDialogAndRefresh();
    }

    public AttachmentEvent createAttachment(Attachments attachment)
            throws AttachmentException {
        boolean ok = true;
        if (attachment.getDocument() == null) {
            // should never happen
            logger.error("Attempt to process null attachment");
            ok = false;
        } else {
        	if (attachment.getPublicAttachmentInfo() == null
                    && attachment.getFileToUpload() != null) {
                attachment.setPublicAttachmentInfo(new UploadedFileInfo(
                        attachment.getFileToUpload()));
            }
        	
        	// make sure document description and type are provided
            if (attachment.getDocument().getDescription() == null || attachment.getDocument().getDescription().trim().equals("")) {
                DisplayUtil
                        .displayError("Please specify the description for this attachment");
                ok = false;
            }
            if (attachment.getDocument().getDocTypeCd() == null || attachment.getDocument().getDocTypeCd().trim().equals("")) {
                DisplayUtil
                        .displayError("Please specify an attachment type for this attachment");
                ok = false;
            }
            
            // make sure document file is provided
            if (attachment.getPublicAttachmentInfo() == null
					&& attachment.getTempDoc().getDocumentID() == null) {
				DisplayUtil
						.displayError("Please specify the file to upload for this attachment");
				ok = false;
			}
        }
        if (ok) {
            try {
            	Attachment ret;
                try {
                    ret = getFullComplianceEvalService().createFceAttachment(
                            fce, attachment.getTempDoc(),
                            attachment.getPublicAttachmentInfo().getInputStream());
                } catch (IOException e) {
                    throw new RemoteException(e.getMessage(), e);
                }

                fce.setAttachments(getFullComplianceEvalService().retrieveFceAttachments(fce.getId()));
                Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
                a.setAttachmentList(getAttachmentList().toArray(new Attachment[0]));
                
                if (ret == null){
                	attachment.cleanup();
                	DisplayUtil.displayError("Attachment of Inspection Report Type already exists. Cannot add more then one Inspection Report.");
                	FacesUtil.returnFromDialogAndRefresh();
                	return null;
                }
                attachment.cleanup();
                DisplayUtil.displayInfo("The attachment has been added to the inspection.");
                FacesUtil.returnFromDialogAndRefresh();
            } catch (RemoteException e) {
                blankOutPage = true;
                setEditable(false);
                // turn it into an exception we can handle.
                handleException(e);
            }
        }
        return null;
    }

    public AttachmentEvent deleteAttachment(Attachments attachment) {
        boolean ok = true;
        try {
            Attachment doc = attachment.getTempDoc();
            getFullComplianceEvalService().removeFceAttachment(new FceAttachment(doc));
        } catch (RemoteException e) {
            ok = false;
            handleException(e);
        }
        // reload attachments
        try {
            fce.setAttachments(getFullComplianceEvalService().retrieveFceAttachments(fce.getId()));
            Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
            a.setAttachmentList(getAttachmentList().toArray(new Attachment[0]));
            attachment.cleanup();
        } catch (RemoteException e) {
            blankOutPage = true;
            setEditable(false);
            ok = false;
            // turn it into an exception we can handle.
            handleException(e);
        }
        if(ok) {
        	DisplayUtil.displayInfo("The attachment has been removed.");
            FacesUtil.returnFromDialogAndRefresh();
        }
        return null;
    }

    public AttachmentEvent updateAttachment(Attachments attachment) {
        Attachment doc = attachment.getTempDoc();
        boolean ok = true;
        
        // make sure document description is provided
        if (doc.getDescription() == null
        		|| doc.getDescription().trim().equals("")) {
        	DisplayUtil
        	.displayError("Please specify the description for this attachment");
        	ok = false;
        }
     	
        if (ok) {
        	try {
        		getFullComplianceEvalService().modifyFceAttachment(new FceAttachment(doc));
        	} catch (RemoteException e) {
        		ok = false;
        		handleException(e);
        	}
        	// reload attachments
        	try {
        		fce.setAttachments(getFullComplianceEvalService().retrieveFceAttachments(fce.getId()));
        		Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
        		a.setAttachmentList(getAttachmentList().toArray(new Attachment[0]));
        		attachment.cleanup();
        	} catch (RemoteException e) {
        		blankOutPage = true;
        		setEditable(false);
        		// turn it into an exception we can handle.
        		ok = false;
        		handleException(e);
        	}
        	if(ok) {
	        	DisplayUtil.displayInfo("The attachment information has been updated.");
        		FacesUtil.returnFromDialogAndRefresh();
        	}
        }
        return null;
    }
    
    public Timestamp getDateEvaluated() {
        return fce.getDateEvaluated();
    }

    public Timestamp getDateReported() {
        return fce.getDateReported();
    }
    
    public void setDateEvaluated(Timestamp dateEvaluated) {
    	fce.setDateEvaluated(dateEvaluated);
    }

    public void setDateReported(Timestamp dateReported) {
    	fce.setDateReported(dateReported);
    }
    
    public boolean isUsEpaCommitted() {
        return fce.isUsEpaCommitted();
    }

    public void setUsEpaCommitted(boolean usEpaCommitted) {
        fce.setUsEpaCommitted(usEpaCommitted);
    }
    
    public String mergeSched() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        //    Get all Inspections for facility that are not completed
        try {
            FullComplianceEval[] all = getFullComplianceEvalService().retrieveFceWithNoSiteVisitsOrStackTests(facilityId);
            
            if(all.length == 0) {
                DisplayUtil.displayInfo("Facility " + facilityId + " has no scheduled and uncompleted inspections that do not have any site visit or stack test associations.");
                return null;
            }
            
            allFCEs.setWrappedData(all);
        } catch (RemoteException re) {
            handleException(re);
        } finally {
            clearButtonClicked();
        }
        return "dialog:mergeFceSched";
    }

    public void performSchedMerge() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        Iterator<?> it = allFCEs.getTable().getSelectionState().getKeySet().iterator();
        FullComplianceEval selectedRow = null;
        boolean ok = false;
        if (it.hasNext()) {
            Object obj = it.next();
            allFCEs.setRowKey(obj);
            selectedRow = (FullComplianceEval) allFCEs.getRowData();
            allFCEs.getTable().getSelectionState().clear();
            ok = true;
        }
        if(!ok) {
            DisplayUtil.displayError("Cancel or make a selection");
            return;
        }
        try {
        	try {
				selectedRow = getFullComplianceEvalService().retrieveFce(selectedRow.getFacilityId(), selectedRow.getId());
			} catch (RemoteException re) {
                handleException(re);
                return;
			}
            
            SiteVisit[] svs = selectedRow.getAssocSiteVisits();
            StackTest[] sts = selectedRow.getAssocStackTests();
            
            if(svs.length > 0) {
                cannotDelete = true;
                cannotdeleteReason = "Cannot merge this scheduled inspection while it has associated site visits";
            }
            if(sts.length > 0) {
                if(cannotDelete) {
                    cannotdeleteReason = cannotdeleteReason + " and associated stack tests";
                } else {
                    cannotDelete = true;
                    cannotdeleteReason = "Cannot merge this scheduled inspection while it has associated stack tests";
                }
            }
            //try {
            //    Integer[] enfIds = getEnforcementService().getEnforcementWithDiscovery(AFSActionDiscoveryTypeDef.FCE, selectedRow.getId());
            //    if(enfIds.length > 0) {
            //        StringBuffer sb = new StringBuffer();
            //        for(Integer i : enfIds) {
            //            sb.append(i.toString() + " ");
            //        }
            //        cannotDelete = true;
            //        String cannotdeleteReasonE = " because it is specified as the Discovery reason in the following Enforcemnt(s):  " + sb.toString();

            //        if(cannotDelete) {
            //            cannotdeleteReason = cannotdeleteReason + " and" + cannotdeleteReasonE;
            //        } else {
            //            cannotdeleteReason = "You cannot delete this scheduled inspection" + cannotdeleteReasonE;
            //        }
            //    }
            //} catch (RemoteException re) {
            //    handleException(re);
            //    return;
            //}
            
            if(cannotDelete) {
                DisplayUtil.displayError(cannotdeleteReason);
                return;
            }
            
            // check memo lengths and copy memo
            String schedMemo = "";
            if(selectedRow.getMemo() != null && selectedRow.getMemo().trim().length() > 0) schedMemo = "SCHEDULED INSPECTION COMPLIANCE STATUS: " + selectedRow.getMemo().trim() + "; ";
            String compMemo = "";
            if(schedMemo.length() == 0) {
                compMemo = (fce.getMemo() == null)?"":fce.getMemo();
            } else {
                if(fce.getMemo() != null && fce.getMemo().trim().length() > 0) compMemo = "\n\nCOMPLETED INSPECTION COMPLIANCE STATUS: " + fce.getMemo().trim();
            }
            String combinedMemo = schedMemo + compMemo;
            if(combinedMemo.length() > 4000) {
                DisplayUtil.displayError("The combined compliance status lengths of scheduled and completed inspections exceed 4000 characters.  First combine the compliance statuses yourself or shorten one of them.");
                return;
            }
            fce.setMemo(combinedMemo);
            try {
                fce.setAssignedStaff(selectedRow.getAssignedStaff());
                fce.setScheduledTimestamp(selectedRow.getScheduledTimestamp());
                fce.setUsEpaCommitted(selectedRow.isUsEpaCommitted());
                fce.setSchedAfsId(selectedRow.getSchedAfsId());
                fce.setSchedAfsDate(selectedRow.getSchedAfsDate());
                getFullComplianceEvalService().modifyFce(fce);
                fce = getFullComplianceEvalService().retrieveFce(facilityId, fce.getId());
                DisplayUtil.displayInfo("Inspection updated with schedule information.");
                setEditable(false);
            } catch(RemoteException re) {
                handleException("Failed to update inspection " + fce.getInspId(), re);
                return;
            }
            try {
                getFullComplianceEvalService().removeFce(selectedRow);
            } catch(RemoteException re) {
                handleException("Failed to delete scheduled inspection " + selectedRow.getInspId(), re);
                return;
            }
        } finally {
            clearButtonClicked();
        }
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public final void cancelMergeSched() {
        DisplayUtil.displayInfo("Merge of Schedule cancelled");
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public boolean isShowMergeSched() {
        return !isEditable() && isStars2Admin() && fce.getScheduledTimestamp() == null && fce.getAssignedStaff() == null;
    }

    public boolean isHasSchedAdminRole() {
        return isFceScheduler();
    }

	public final Integer getFceId() {
		return fceId;
	}

	public final void setFceId(Integer fceId) {
		this.fceId = fceId;
	}

	public final String getFacilityId() {
		return facilityId;
	}

	public final String getFacilityName() {
		String facilityName = "";
		if (facility != null) {
			facilityName = facility.getName();
		}
		return facilityName;
	}

	public final boolean isEditable() {
		return editable;
	}

	public final void setEditable(boolean editable) {
		this.editable = editable;
        if(!editable) {
            setSchedEditable(false);
            setComplEditable(false);
        }
        Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
        a.setNewPermitted(isCetaUpdate());
        a.setUpdatePermitted(isCetaUpdate());
        if(fce != null && fce.getId() != null) a.setObjectId(Integer.toString(fce.getId()));
        else a.setObjectId("");
	}
    
    public boolean isLegacy() {
        return TimestampUtil.isCetaLegacy(fce.getCreatedDt());
    }

	public final Facility getFacility() {
		return facility;
	}

	public final void setFacility(Facility facility) {
		this.facility = facility;
	}

    public FullComplianceEval getFce() {
        return fce;
    }

    public void setFce(FullComplianceEval fce) {
        this.fce = fce;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public Integer getVisitId() {
        return visitId;
    }

    public void setVisitId(Integer visitId) {
        this.visitId = visitId;
    }

    public SiteVisit getSiteVisit() {
        return siteVisit;
    }

    public void setSiteVisit(SiteVisit visit) {
        siteVisit = visit;
    }

    public boolean isBlankOutPage() {
        return blankOutPage;
    }

    public void setBlankOutPage(boolean blankOutPage) {
        this.blankOutPage = blankOutPage;
    }

    public boolean isExistingFceObject() {
        return existingFceObject;
    }

    public void setExistingFceObject(boolean existingFceObject) {
        this.existingFceObject = existingFceObject;
    }

    public boolean isNewFceObject() {
        return newFceObject;
    }

    public boolean isCannotDelete() {
        return cannotDelete;
    }

    public String getCannotdeleteReason() {
        return cannotdeleteReason;
    }

    public boolean isComplEditable() {
        return complEditable;
    }

    public void setComplEditable(boolean complEditable) {
        this.complEditable = complEditable;
    }

    public boolean isSchedEditable() {
        return schedEditable;
    }

    public void setSchedEditable(boolean schedEditable) {
        this.schedEditable = schedEditable;
    }

    public String goToSummaryPage() {
    	FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
    	fp.setFacilityId(facilityId);
    	fp.submitProfileById();
    	FceSiteVisits fceSiteVisit = (FceSiteVisits) FacesUtil.getManagedBean("fceSiteVisits");
    	return fceSiteVisit.initFCEs();
    }

    public TableSorter getAllFCEs() {
        return allFCEs;
    }

    public void setAllFCEs(TableSorter allFCEs) {
        this.allFCEs = allFCEs;
    }

    public String getAfsWarning() {
        return afsWarning;
    }
    
	// called from the workflow activity validation
    @Override
	public List<ValidationMessage> validate(Integer inActivityTemplateId) {
		
		submitInternal();
		
		return InspectionWorkFlowValidation.validate(this.fce, inActivityTemplateId);
		
	}

	@Override
	public String findOutcome(String url, String ret) {
		ActivityProfile ap = (ActivityProfile) FacesUtil
				.getManagedBean("activityProfile");
		WorkFlowProcess p = ap.getProcess();
		if(p != null) {
			facilityId = p.getFacilityIdString();
		}
		return submitInternal();
	}

	@Override
	public Integer getExternalId() {
		return fceId;
	}

	@Override
	public void setExternalId(Integer externalId) {
		fceId = externalId;

	}

	@Override
	public String toExternal() {
		// the facility Id will already have been set by entering the workflow.
		ActivityProfile ap = (ActivityProfile) FacesUtil.getManagedBean("activityProfile");
		WorkFlowProcess p = ap.getProcess();
		if(p != null) {
			facilityId = p.getFacilityIdString();
			return submitInternal();
		}
		return null;
	}

	@Override
	public String getExternalName(ProcessActivity activity) {
		return "Inspection";
	}

	public String getExternalNum() {
		String	tempInspId = null;
		if (!Utility.isNullOrZero(fceId)) {
			String format = "INSP" + "%06d";
			try {
				tempInspId = String.format(format, fceId);
			} catch (NumberFormatException nfe) {
			}
		}
		return tempInspId;
	}

	public String getValidationDlgReference() {
		return FCE_TAG;
	}

	public final String validationDlgAction() {
		String rtn = super.validationDlgAction();
		if (null != rtn)
			return rtn;
		if (newValidationDlgReference == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(newValidationDlgReference, ":");
		String subsystem = st.nextToken();
		if (!subsystem.equals(ValidationBase.ENFORCEMENT_TAG)) {
			logger.error("Enforcement reference is in error: "
					+ newValidationDlgReference);
			DisplayUtil.displayError("Error processing validation message");
			return null;
		}
		if (st.hasMoreTokens()) {
			String facilityId = st.nextToken();

			FacilityProfile fp = (FacilityProfile) FacesUtil
					.getManagedBean("facilityProfile");
			fp.setFacilityId(facilityId);
			fp.submitProfileById();
			EnforcementSearch es = (EnforcementSearch) FacesUtil
					.getManagedBean("enforcementSearch");
			return es.initEnforcementsInter(true);
		}
		return ENFORCEMENT_TAG;
	}
	
	public final String getContentTableWidth() {
        return (CONTENT_WIDTH - 50) + "";
    }
	
	private void loadNotes(int fceId) {
        try {
        	Note[] inspectionNotes = getFullComplianceEvalService().retrieveInspectionNotes(fceId);
            fce.setInspectionNotes(inspectionNotes);
        } catch (RemoteException ex) {
            DisplayUtil.displayError("cannot load permit comments");
            handleException(ex);
            return;
        }
    }
	
	 public final String startAddComment() {
	        tempComment = new InspectionNote();
	        tempComment.setUserId(getCurrentUserID());
	        tempComment.setFceId(fce.getId());
	        tempComment.setDateEntered(new Timestamp(System.currentTimeMillis()));
	        tempComment.setNoteTypeCd(NoteType.DAPC);
	        newNote = true;
	        noteReadOnly = false;

	        return COMMENT_DIALOG_OUTCOME;
	    }
	
	 public final String startEditComment() {
	        newNote = false;
	        tempComment = new InspectionNote(modifyComment);
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
	                getFullComplianceEvalService().createInspectionNote(tempComment);
	            } else {
	            	getFullComplianceEvalService().modifyInspectionNote(tempComment);
	            }

	        } catch (RemoteException e) {
	            DisplayUtil.displayError("cannot save comment");
	            handleException(e);
	            return;
	        }

	        tempComment = null;
	        reloadNotes();
	        FacesUtil.returnFromDialogAndRefresh();
		 }
	 }

	    public final void commentDialogDone(ReturnEvent returnEvent) {
	        tempComment = null;
	        reloadNotes();
	    }

	    public final InspectionNote getTempComment() {
	        return tempComment;
	    }

	    public final void setTempComment(InspectionNote tempComment) {
	        this.tempComment = tempComment;
	    }
	    
	    public Integer getCurrentUserID() {
	        return InfrastructureDefs.getCurrentUserId();
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
	    
	    public final String reloadNotes() {
	        loadNotes(fce.getId());
	        return FacesUtil.getCurrentPage(); // stay on same page
	    }
	    
	    public final void closeDialog(ActionEvent actionEvent) {
	    	tempComment = null;
	    	noteReadOnly = false;
	        FacesUtil.returnFromDialogAndRefresh();
	    }
	    
		public Timestamp getDateSentToEPA() {
			return fce.getDateSentToEPA();
		}

		public void setDateSentToEPA(Timestamp dateSentToEPA) {
			fce.setDateSentToEPA(dateSentToEPA);
		}
		
		private void handleBadDetail() {
            ((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_fceDetail"))
            .setDisabled(true);
            StackTests sts = (StackTests)FacesUtil.getManagedBean("stackTests");
            FceSearch inspectionSearch = (FceSearch)FacesUtil.getManagedBean("fceSearch");
            inspectionSearch.submitSearch();
            sts.setPopupRedirectOutcome("fce.search");
            blankOutPage = true;
            setEditable(false);
		}
		
		private void handleBadDetailAndRedirect() {
			handleBadDetail();
			StackTests sts = (StackTests)FacesUtil.getManagedBean("stackTests");
			sts.getPopupRedirect();
		}
		
		public String getAssociatedSiteVisitMsg()
		{
			String msg = "";
			if (fce != null && fce.getSiteVisitsSize() > 0)
			{
				msg = "Click to select visits you would like to associate with this Inspection";
			}
			else
			{
				msg = "There are no unassociated site visits for this facility";
			}
			return msg;
		}
		
		public String getAssociatedStackTestMsg()
		{
			String msg = "";
			if (fce != null && fce.getStackTestsSize() > 0)
			{
				msg = "Click to select stack tests you would like to associate with this Inspection";
			}
			else
			{
				msg = "There are no unassociated stack tests for this facility";
			}
			return msg;
		}
		
		public final InspectionNote getModifyComment() {
	        return modifyComment;
	    }

	    public final void setModifyComment(InspectionNote modifyComment) {
	        this.modifyComment = modifyComment;
	    }
	    
    public String getPreInspectionData() {
    	//navigate to fcePreData.jsp
    	//retrieve preserved list of each snapshot
		try{
			fce = getFullComplianceEvalService().retrieveFce(facilityId, fceId);
			
			setApplicationWrapperFromPreservedList();
			setPermitWrapperFromPreservedList();
			setStackTestWrapperFromPreservedList();
			setComplianceReportWrapperFromPreservedList();
			setCorrespondenceWrapperFromPreservedList();
			setEmissionsInventoryWrapperFromPreservedList();
			setSiteVisitWrapperFromPreservedList();
			setAmbientMonitorWrapperFromPreservedList();
			setContinuousMonitorWrapperFromPreservedList();
			
			if (fce.getFcePreData().getDateRangePA().isNewObject()){
				setHasPreservedPA(false);
				setDateRangeChangePA(true);
			} else {
				setHasPreservedPA(true);
				setDateRangeChangePA(false);
			} 
			
			if (fce.getFcePreData().getDateRangePT().isNewObject()){
				setHasPreservedPT(false);
				setDateRangeChangePT(true);
			} else {
				setHasPreservedPT(true);
				setDateRangeChangePT(false);
			} 
			
			if (fce.getFcePreData().getDateRangeST().isNewObject()){
				setHasPreservedST(false);
				setDateRangeChangeST(true);
			} else {
				setHasPreservedST(true);
				setDateRangeChangeST(false);
			} 
			
			if (fce.getFcePreData().getDateRangeCR().isNewObject()){
				setHasPreservedCR(false);
				setDateRangeChangeCR(true);
			} else {
				setHasPreservedCR(true);
				setDateRangeChangeCR(false);
			} 
			
			if (fce.getFcePreData().getDateRangeDC().isNewObject()){
				setHasPreservedDC(false);
				setDateRangeChangeDC(true);
			} else {
				setHasPreservedDC(true);
				setDateRangeChangeDC(false);
			} 

			if (fce.getFcePreData().getDateRangeEI().isNewObject()){
				setHasPreservedEI(false);
				setDateRangeChangeEI(true);
			} else {
				setHasPreservedEI(true);
				setDateRangeChangeEI(false);
			} 
			
			if (fce.getFcePreData().getDateRangeSiteVisits().isNewObject()){
				setHasPreservedSiteVisit(false);
				setDateRangeChangeSiteVisit(true);
			} else {
				setHasPreservedSiteVisit(true);
				setDateRangeChangeSiteVisit(false);
			}
			
			if (fce.getFcePreData().getDateRangeAmbientMonitors().isNewObject()){
				setHasPreservedAmbientMonitor(false);
				setDateRangeChangeAmbientMonitor(true);
			} else {
				setHasPreservedAmbientMonitor(true);
				setDateRangeChangeAmbientMonitor(false);
			}
			
			if (fce.getFcePreData().getDateRangeContinuousMonitors().isNewObject()){
				setHasPreservedContinuousMonitor(false);
				setDateRangeChangeContinuousMonitor(true);
			} else {
				setHasPreservedContinuousMonitor(true);
				setDateRangeChangeContinuousMonitor(false);
			}
			
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			
		} catch(RemoteException e) {
			logger.error("Failed to retrieve Pre-inspection Data for " + fce.getInspId());
			DisplayUtil.displayError("Failed to retrieve Pre-inspection Data for " + fce.getInspId());
			handleBadDetailAndRedirect();
			return null;
		}
        return PRE_INSPECTION_DATA_MENU;
    }

	public String getFcePermitConditions() {
		// navigate to fcePermitConditions.jsp
		resetPermitSelection();
		
		try {
			fce = getFullComplianceEvalService().retrieveFce(facilityId, fceId);
			updatePermitConditionSelection();
		} catch (RemoteException e) {
			handleException("Error retrieving associated permit conditions.", e);
		}
		return PERMIT_CONDITIONS_MENU;
	}
    
    public String getFceObservations() {
    	//navigate to fceObservations.jsp
    	try {
			fce = getFullComplianceEvalService().retrieveFce(facilityId, fceId);
		} catch (RemoteException e) {
			handleException("Error retrieving observations and concerns.", e);
		}
        return OBSERVATIONS_MENU;
    }

    public static LinkedHashMap<String, String> getArrivalDepartureTimeMap() {
        LinkedHashMap<String, String> scheds = new LinkedHashMap<String, String>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Timestamp ref = new Timestamp(cal.getTimeInMillis());
		ref.setNanos(0);
		cal.setTime(new Date(ref.getTime()));
		
		for (int i = 0; i < 96; i++) {
			Timestamp use = ref;
			scheds.put(new SimpleDateFormat("hh:mm a").format(use), new SimpleDateFormat("HHmm").format(use));
			cal.add(Calendar.MINUTE, 15);
			ref = new Timestamp(cal.getTimeInMillis());
		}
		return scheds;
    }
    
    public LinkedHashMap<String, String> getArrivalDepartureTimes() {
    	return getArrivalDepartureTimeMap();
    }

    
    public final String prepareInspRpt() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
        	infoStatement = "Click <b>Yes</b> to proceed and place this Inspection in the Preparing state. This will also initiate the Inspection Report workflow.";
        	return "dialog:confirmPrepareInspRpt";
        } finally {
            clearButtonClicked();
        }
    }
    

	public String getInfoStatement() {
		return infoStatement;
	}

	public void setInfoStatement(String infoStatement) {
		this.infoStatement = infoStatement;
	}
	
    public final void fcePrepare() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
    	try{
    		if(isInspectionValidToPrepare()){
    			getFullComplianceEvalService().prepareFce(fce);
        		DisplayUtil.displayInfo("Inspection Report prepared successfully.");
    		}
    	}
    	catch (Exception ex){
    		DisplayUtil.displayError("Failed to prepare Inspection Report");
    	} finally {
        	try {
				fce = getFullComplianceEvalService().retrieveFce(facilityId, fce.getId());
				getAssociatedFacility();
			} catch (DAOException e) {
				logger.error("Could not retrieve inspection : " + fce.getInspId());
				DisplayUtil.displayError("Failed to retrieve inspection "
						+ fce.getInspId()
						+ ". The inspection may no longer exist.");
				handleBadDetailAndRedirect();
			} catch (RemoteException e) {
				logger.error("Could not retrieve inspection : " + fce.getInspId());
				DisplayUtil.displayError("Failed to retrieve inspection "
						+ fce.getInspId()
						+ ". The inspection may no longer exist.");
				handleBadDetailAndRedirect();
			}
        }
    	FacesUtil.returnFromDialogAndRefresh();
    	clearButtonClicked();
    }
    
	private boolean isInspectionValidToPrepare() {
		boolean ret = true;
		if(fce.getInspectionType() == null) {
			DisplayUtil.displayError("Inspection Type has to be specified before Preparing Inspection Report.");
			ret = false;
		}
		return ret;
	}

	public DefSelectItems getStBasicUsersDef() {
		DefSelectItems allUsers = BasicUsersDef.getData().getItems();
		DefSelectItems unselectedUsers = new DefSelectItems();
		List<Integer> staffList = new ArrayList<Integer>();

		// create a list of already selected staff
		for (Evaluator staff : fce.getAdditionalAqdStaffPresent()) {
			if (staff.getEvaluator() != null)
				staffList.add(staff.getEvaluator());
		}

		// create a list of available staff i.e., allUsers minus already
		// selected staff
		for (SelectItem user : allUsers.getCurrentItems()) {
			if (!staffList.contains((Integer) user.getValue()))
				unselectedUsers.add(user.getValue(), user.getLabel(), false);
			else
				unselectedUsers.add(user.getValue(), user.getLabel(), true);
		}
		return unselectedUsers;
	}
	
	public final void addAdditionalAqdStaffPresent() {
		fce.addAdditionalAqdStaffPresent();
	}
	
	public final void deleteAdditionalAqdStaffPresent() {
		if (fce.getAdditionalAqdStaffPresent() == null)
			return;
		Iterator<Evaluator> l = fce.getAdditionalAqdStaffPresent().iterator();
		while (l.hasNext()) {
			Evaluator e = l.next();
			if (e == null)
				continue;
			if (e.isSelected())
				l.remove();
		}
	}
	
	public final String completeInspRpt(){
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
        	infoStatement = "Click <b>Yes</b> to proceed and mark this Inspection as Completed.";
        	return "dialog:confirmCompleteInspRpt";
        } finally {
            clearButtonClicked();
        }
	}
    public final void fceComplete() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        if (validateInspCompletionDate()){
	    	try{
	    		getFullComplianceEvalService().completeFce(fce);
	    		DisplayUtil.displayInfo("Inspection Report complete successfully.");
	    	}
	    	catch (Exception ex){
	    		DisplayUtil.displayError("Failed to complete Inspection Report");
	    	} finally {
	        	try {
					fce = getFullComplianceEvalService().retrieveFce(facilityId, fce.getId());
					getAssociatedFacility();
				} catch (DAOException e) {
					logger.error("Could not retrieve inspection : " + fce.getInspId());
					DisplayUtil.displayError("Failed to retrieve inspection "
							+ fce.getInspId()
							+ ". The inspection may no longer exist.");
					handleBadDetailAndRedirect();
				} catch (RemoteException e) {
					logger.error("Could not retrieve inspection : " + fce.getInspId());
					DisplayUtil.displayError("Failed to retrieve inspection "
							+ fce.getInspId()
							+ ". The inspection may no longer exist.");
					handleBadDetailAndRedirect();
				}
	        }
    	}
        FacesUtil.returnFromDialogAndRefresh();
        clearButtonClicked();
    }
    
    
    public boolean validateInspCompletionDate(){
    	boolean ret = false;
    	if (fce.getDateEvaluated()==null){
    		DisplayUtil.displayError("Inspection Report cannot be completed since there is no Inspection Date");
    		return ret;
    	}
    	Timestamp current = new Timestamp(Calendar.getInstance().getTimeInMillis());
    	if (fce.getDateEvaluated().after(current)){
    		DisplayUtil.displayError("Inspection Report cannot be completed since Inspection Date is a future date.");
    		return ret;
    	}
    	//check for both 1st day and 2nd day.
    	//Inspection Date cannot be null
    	//must not be a future date
    	return true;
    }
    
    public final String finalizeInspRpt(){
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
        	infoStatement = "Once you mark the Inspection Report as finalized, users will no longer be able to add or change information associated with this inspection."
        			+ "Click <b>Yes</b> to proceed or No to cancel";
        	return "dialog:confirmFinalizeInspRpt";
        } finally {
            clearButtonClicked();
        }
    }

    public final void fceFinalize() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
    		getFullComplianceEvalService().finalizeFce(fce);
    		DisplayUtil.displayInfo("Inspection Report finalized successfully.");
        } catch(Exception ex){
    		logger.error("Failed to finalize Inspection Report", ex);
    		DisplayUtil.displayError("Failed to finalize Inspection Report");
    	} finally {
        	try {
				fce = getFullComplianceEvalService().retrieveFce(facilityId, fce.getId());
				initializeAttachmentBean();
			} catch (DAOException e) {
				logger.error("Could not retrieve inspection : " + fce.getInspId());
				DisplayUtil.displayError("Failed to retrieve inspection "
						+ fce.getInspId()
						+ ". The inspection may no longer exist.");
				handleBadDetailAndRedirect();
			} catch (RemoteException e) {
				logger.error("Could not retrieve inspection : " + fce.getInspId());
				DisplayUtil.displayError("Failed to retrieve inspection "
						+ fce.getInspId()
						+ ". The inspection may no longer exist.");
				handleBadDetailAndRedirect();
			}
        }
        FacesUtil.returnFromDialogAndRefresh();
        clearButtonClicked();
    }
    
    public boolean isInspectionFinalizer(){
    	return InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("fce.detail.inspectionFinalizer");
    }
    
	public boolean isObservationsEditable() {
		boolean ret = false;
		
		if(isPrivledged()){
			ret = true;
		} else if (fce != null && StringUtils.equalsIgnoreCase(fce.getInspectionReportStateCd(),
				FCEInspectionReportStateDef.PREPARE)) {
			ret = true;
		}
		return ret;
	}
	
	public boolean isPermitConditionsEditable() {
		boolean ret = false;
		
		if(isPrivledged()){
			ret = true;
		} else if (fce != null && StringUtils.equalsIgnoreCase(fce.getInspectionReportStateCd(),
				FCEInspectionReportStateDef.PREPARE)) {
			ret = true;
		}
		return ret;
	}
	
	public final void saveObservationsAndConcerns() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}

		try {
			getFullComplianceEvalService().modifyFceObservationsAndConcerns(fce);
			DisplayUtil.displayInfo("Observations And Concerns updated successfully.");
			setEditable(false);
		} catch (RemoteException e) {
			handleException(e);
			return;
		} finally {
			try {
				fce = getFullComplianceEvalService().retrieveFce(facilityId, fce.getId());
			} catch (DAOException e) {
				logger.error("Could not retrieve inspection : " + fce.getInspId());
				DisplayUtil.displayError(
						"Failed to update inspection " + fce.getInspId() + ". The inspection may no longer exist.");
				handleBadDetailAndRedirect();
			} catch (RemoteException e) {
				logger.error("Could not retrieve inspection : " + fce.getInspId());
				DisplayUtil.displayError(
						"Failed to update inspection " + fce.getInspId() + ". The inspection may no longer exist.");
				handleBadDetailAndRedirect();
			}
			clearButtonClicked();
		}
	}
	
	public final String cancelObservationsAndConcernsEdit() {
		setEditable(false);
		DisplayUtil.displayInfo("Operation cancelled, Inspection not updated");
		try {
			fce = getFullComplianceEvalService().retrieveFce(facilityId, fce.getId());
		} catch (RemoteException re) {
			blankOutPage = true;
			handleException("Failed to re-read Inspection " + fce.getId(), re);
		}
		return OBSERVATIONS_MENU;
	}
	
	public List<SelectItem> getAvailablePermits() {
		List<SelectItem> availablePermits = new ArrayList<SelectItem>();
		
		try {
			availablePermits = getFullComplianceEvalService().getAllFinalPermits(fce);
		} catch (DAOException e) {
			blankOutPage = true;
			handleException("Failed to retrieve permits " + fce.getId(), e);
		}
		return availablePermits;
	}
	
	public List<Integer> getAssociatedPermits() {
		List<Integer> permits = new ArrayList<Integer>();

		permits.addAll(fce.getAssociatedPermits());

		if (!corrPermitIdsToAssociate.isEmpty()) {
			permits.addAll(corrPermitIdsToAssociate);
		}

		if (!corrPermitIdsToDisassociate.isEmpty()) {
			permits.removeAll(corrPermitIdsToDisassociate);

		}
		
		return permits;
	}
	
	public void setAssociatedPermits(List<Integer> associatedPermits) {

		corrPermitIdsToAssociate.clear();
		corrPermitIdsToDisassociate.clear();

		List<Integer> currentlyAssociatePermits = fce.getAssociatedPermits();

		// determine if any previously associated permits were now disassociated
		for (Integer permitId : currentlyAssociatePermits) {
			if (!associatedPermits.contains(permitId)) {
				corrPermitIdsToDisassociate.add(permitId);
			}
		}

		// determine any newly associated permits
		for (Integer permitId : associatedPermits) {
			if (!currentlyAssociatePermits.contains(permitId)) {
				corrPermitIdsToAssociate.add(permitId);
			}
		}
	}

	public void editFcePermitSelection() {
		setPermitSelectionEditable(true);
	}
	
	public void saveFcePermitSelection() {
		if (!firstButtonClick() || fce == null) { // protect from multiple clicks
			clearButtonClicked();
			return;
		}

		try {
			getFullComplianceEvalService().updateAssociatedPermits(fce, corrPermitIdsToAssociate,
					corrPermitIdsToDisassociate);
			// update Permit conditions
			updateAssociatedPermitConditions();

			DisplayUtil.displayInfo("Permits selection updated successfully.");
			setPermitSelectionEditable(false);
		} catch (RemoteException e) {
			handleException(e);
			return;
		} finally {
			try {
				fce = getFullComplianceEvalService().retrieveFce(facilityId, fce.getId());
				updatePermitConditionSelection();
				resetPermitSelection();
			} catch (DAOException e) {
				logger.error("Could not retrieve inspection : " + fce.getInspId());
				DisplayUtil.displayError(
						"Failed to update inspection " + fce.getInspId() + ". The inspection may no longer exist.");
				handleBadDetailAndRedirect();
			} catch (RemoteException e) {
				logger.error("Could not retrieve inspection : " + fce.getInspId());
				DisplayUtil.displayError(
						"Failed to update inspection " + fce.getInspId() + ". The inspection may no longer exist.");
				handleBadDetailAndRedirect();
			}
			clearButtonClicked();
		}
		FacesUtil.returnFromDialogAndRefresh();
	}
	
	private void updateAssociatedPermitConditions() {
		if (corrPermitIdsToDisassociate != null) {
			try {
				getFullComplianceEvalService()
						.deleteAssociatedPermitConditionIdRefByPermitIds(corrPermitIdsToDisassociate);
			} catch (DAOException e) {
				handleException("Error excluding Permit Condition(s)", e);
				return;
			}
		}
		if (corrPermitIdsToAssociate != null) {
			if (addPermitConditionsAutomatically) {
				// Add to Included list of PCs
				try {
					getFullComplianceEvalService().associatePermitConditionsByPermitIds(corrPermitIdsToAssociate,
							fce.getId());
				} catch (DAOException e) {
					handleException("Error including Permit Condition(s)", e);
					return;
				}
			}
		}
	}
	
	public String cancelPermitSelectionEditable() {
		setPermitSelectionEditable(false);
		DisplayUtil.displayInfo("Operation cancelled, Inspection not updated");
		try {
			fce = getFullComplianceEvalService().retrieveFce(facilityId, fce.getId());
			resetPermitSelection();
		} catch (RemoteException re) {
			blankOutPage = true;
			handleException("Failed to re-read Inspection " + fce.getId(), re);
		}

		return PERMIT_CONDITIONS_MENU;
	}
	
	private void resetPermitSelection() {
		corrPermitIdsToAssociate = new ArrayList<Integer>();
		corrPermitIdsToDisassociate = new ArrayList<Integer>();
		addPermitConditionsAutomatically = true;
	}
	
	private void updatePermitConditionSelection() throws DAOException {
		includedPermitConditions = new TableSorter();
		excludedPermitConditions = new TableSorter();
		
		includedPermitConditions.setWrappedData(fce.getFcePermitConditions());

		List<PermitConditionSearchLineItem> excludedList = getFullComplianceEvalService()
				.retrieveExcludedPermitConditionsByFceId(fce.getId());
		excludedPermitConditions.setWrappedData(excludedList);
	}
	
	public boolean isPermitSelectionEditable() {
		return permitSelectionEditable;
	}

	public void setPermitSelectionEditable(boolean permitSelectionEditable) {
		this.permitSelectionEditable = permitSelectionEditable;
	}

	public void searchPA() {
		if (fce.getFcePreData().getDateRangePA().getStartDt() == null) {
			DisplayUtil.displayError("Application: Start Date is empty.");
			return;
		}
		if (fce.getFcePreData().getDateRangePA().getEndDt() != null) {
			if (fce.getFcePreData().getDateRangePA().getStartDt()
					.after(fce.getFcePreData().getDateRangePA().getEndDt())) {
				DisplayUtil.displayError("Application: Start Date is after End Date.");
				return;
			}
		}
		try {
			applicationList = getFullComplianceEvalService().retrieveFceApplicationsBySearch(fce);
			applicationWrapper = new TableSorter();
			applicationWrapper.setWrappedData(applicationList);
			setDateRangeChangePA(false);
			String message = getSnapshotSearchSuccessMessage(PA_SNAPSHOT_DESC, false);
			if (applicationList.size() == 0) {
				message = getSnapshotSearchSuccessMessage(PA_SNAPSHOT_DESC, true);
			}
			DisplayUtil.displayInfo(message);
		} catch (DAOException e) {
			logger.error("Could not search applications for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotSearchErrorMessage(PA_SNAPSHOT_DESC));
			handleBadDetailAndRedirect();
		}
	}
	
	public String resetPA() {
		fce.setPreDataDefaultDateRange(FCEPreSnapshotTypeDef.PA);
		setDateRangeChangePA(true);
		// setHasSearchResultsPA(false);
		applicationList = new ArrayList<FceApplicationSearchLineItem>();
		applicationWrapper = new TableSorter();
		DisplayUtil.displayInfo(getSnapshotResetMessage(PA_SNAPSHOT_DESC));
		return PRE_INSPECTION_DATA_MENU;
	}
	
	public void setPreservedListPA() {
		try {
			HashSet<Integer> applicationIds = new HashSet<Integer>();
			for (FceApplicationSearchLineItem item : applicationList) {
				applicationIds.add(item.getApplicationId());
			}
			fce.getFcePreData().setApplicationList(new ArrayList<Integer>(applicationIds));

			if (fce.getFcePreData().getDateRangePA().getStartDt() == null) {
				DisplayUtil.displayError("Application: Start Date is empty.");
			}
			if (fce.getFcePreData().getDateRangePA().getEndDt() == null) {
				fce.getFcePreData().getDateRangePA()
						.setEndDt(((InfrastructureDefs) FacesUtil.getManagedBean("infraDefs")).getTodaysDate());
			}
			// set Preserved List
			getFullComplianceEvalService().updateFceApplicationsPreserved(fce);
			hasPreservedPA = true;
			setApplicationWrapperFromPreservedList();
			DisplayUtil.displayInfo(getSnapshotPreservedMessage(PA_SNAPSHOT_DESC, true));
		} catch (RemoteException e) {
			logger.error("Could not preserve Application list for " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotPreservedMessage(PA_SNAPSHOT_DESC, false));
			handleException(e);
		}
	}
	
	private void setApplicationWrapperFromPreservedList() throws DAOException {
		// retrieve preserve list from DB
		applicationList = getFullComplianceEvalService().retrieveFceApplicationsPreserved(fce);
		applicationWrapper = new TableSorter();
		applicationWrapper.setWrappedData(applicationList);
	}
	
	public void recallPreservedListPA(){		
		try{
			setApplicationWrapperFromPreservedList();
			setDateRangeChangePA(false);
			DisplayUtil.displayInfo(getSnapshotRecalledMessage(PA_SNAPSHOT_DESC, true));
		} catch(DAOException e) {
			logger.error("Could not recall preserved application list for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotRecalledMessage(PA_SNAPSHOT_DESC, false));
			handleBadDetailAndRedirect();
		}
	}
	
	public void clearPreservedListPA() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}

		ConfirmWindow cw = (ConfirmWindow) FacesUtil.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO) || fce == null) {
			clearButtonClicked();
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			return;
		}

		try {
			getFullComplianceEvalService().clearFceApplicationsPreserved(fce);
			hasPreservedPA = false;
			setApplicationWrapperFromPreservedList();

			fce.setPreDataDefaultDateRange(FCEPreSnapshotTypeDef.PA);
			setDateRangeChangePA(true);
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			DisplayUtil.displayInfo(getSnapshotClearedMessage(PA_SNAPSHOT_DESC, true));
		} catch (DAOException e) {
			logger.error("Could not clear applications for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotClearedMessage(PA_SNAPSHOT_DESC, false));
			handleException(e);
		} finally {
			clearButtonClicked();
		}
	}
	
	public List<FceApplicationSearchLineItem> getApplicationList() {
		return applicationList;
	}

	public void setApplicationList(List<FceApplicationSearchLineItem> applicationList) {
		this.applicationList = applicationList;
	}

	public TableSorter getApplicationWrapper() {
		return applicationWrapper;
	}

	public void setApplicationWrapper(TableSorter applicationWrapper) {
		this.applicationWrapper = applicationWrapper;
	}
	
	
	public boolean isDateRangeChangePA() {
		return dateRangeChangePA;
	}

	public void setDateRangeChangePA(boolean dateRangeChangePA) {
		this.dateRangeChangePA = dateRangeChangePA;
	}
	
	public boolean isHasPreservedPA() {
		return hasPreservedPA;
	}

	public void setHasPreservedPA(boolean hasPreservedPA) {
		this.hasPreservedPA = hasPreservedPA;
	}
	
	public void paDateRangeChanged(ValueChangeEvent event){
		setDateRangeChangePA(true);
	}

	public boolean isAddPermitConditionsAutomatically() {
		return addPermitConditionsAutomatically;
	}

	public void setAddPermitConditionsAutomatically(boolean addPermitConditionsAutomatically) {
		this.addPermitConditionsAutomatically = addPermitConditionsAutomatically;
	}

	public TableSorter getIncludedPermitConditions() {
		return includedPermitConditions;
	}

	public void setIncludedPermitConditions(TableSorter includedPermitConditions) {
		this.includedPermitConditions = includedPermitConditions;
	}

	public TableSorter getExcludedPermitConditions() {
		return excludedPermitConditions;
	}

	public void setExcludedPermitConditions(TableSorter excludedPermitConditions) {
		this.excludedPermitConditions = excludedPermitConditions;
	}
	
	private final void updateReferenceReviewStartDate() throws RemoteException {
		
			//update referenceReviewStartDate in DB
			//reset, search, preserve for each artifact
			getFullComplianceEvalService().modifyFceReferenceReviewStartDate(fce);
			setHasPreservedPA(true);
			setHasPreservedPT(true);
			setHasPreservedST(true);
			setHasPreservedCR(true);
			setHasPreservedDC(true);
			setHasPreservedEI(true);
			setHasPreservedContinuousMonitor(true);
			setHasPreservedAmbientMonitor(true);
			setHasPreservedSiteVisit(true);
			
			fce = getFullComplianceEvalService().retrieveFce(facilityId, fce.getId());
			setApplicationWrapperFromPreservedList();
			setDateRangeChangePA(false);

			setPermitWrapperFromPreservedList();
			setDateRangeChangePT(false);
			
			setStackTestWrapperFromPreservedList();
			setDateRangeChangeST(false);
			
			setComplianceReportWrapperFromPreservedList();
			setDateRangeChangeCR(false);
			
			setCorrespondenceWrapperFromPreservedList();
			setDateRangeChangeDC(false);
			
			setEmissionsInventoryWrapperFromPreservedList();
			setDateRangeChangeEI(false);

			setContinuousMonitorWrapperFromPreservedList();
			setDateRangeChangeContinuousMonitor(false);
			
			setAmbientMonitorWrapperFromPreservedList();
			setDateRangeChangeAmbientMonitor(false);
			
			setSiteVisitWrapperFromPreservedList();
			setDateRangeChangeSiteVisit(false);
	}
	
	
	
	public final void resetStartDateToLastInspDate() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		// referenceReviewStartDate should not be after today and should not be null

		try {
			updateReferenceReviewStartDate();
			DisplayUtil.displayInfo("Successfully created the preserved lists based on the Last Inspection Date");
		} catch (RemoteException e) {
			logger.error("Error in resetStartDateToLastInspDate()", e);
			handleException(e);
			DisplayUtil.displayError("Failed to set Start Date to Last Inspection Date.");
		} finally {
			clearButtonClicked();
		}
	}

	public boolean isStartDateEditMode() {
		return startDateEditMode;
	}

	public void setStartDateEditMode(boolean startDateEditMode) {
		this.startDateEditMode = startDateEditMode;
	}


	public final void resetStartDateByUser(){
		setStartDateEditMode(true);
	}
	
	public void saveStartDate() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			if (fce.getReferenceReviewStartDate() == null) {
				DisplayUtil.displayError("Start Date cannot be blank");
				return;
			}
			updateReferenceReviewStartDate();
			setStartDateEditMode(false);
			DisplayUtil.displayInfo("Successfully created the preserved lists based on the Start Date picked");
		} catch (RemoteException ex) {
			logger.error("Error in saveStartDate()", ex);
			handleException(ex);
			DisplayUtil.displayError("Failed to set Start Date to the date picked");
		} finally {
			clearButtonClicked();
		}

	}
	
	public String cancelSetStartDateByUser() {
		try {
			fce = getFullComplianceEvalService().retrieveFceOnly(fce.getId());
			setStartDateEditMode(false);
		} catch (RemoteException re) {
			blankOutPage = true;
			handleException("Failed to re-read Inspection " + fce.getId(), re);
		}
		return getPreInspectionData();
	}

	public List<Permit> getPermitList() {
		return permitList;
	}

	public void setPermitList(List<Permit> permitList) {
		this.permitList = permitList;
	}

	public TableSorter getPermitWrapper() {
		return permitWrapper;
	}

	public void setPermitWrapper(TableSorter permitWrapper) {
		this.permitWrapper = permitWrapper;
	}

	public boolean isHasPreservedPT() {
		return hasPreservedPT;
	}

	public void setHasPreservedPT(boolean hasPreservedPT) {
		this.hasPreservedPT = hasPreservedPT;
	}

	public boolean isDateRangeChangePT() {
		return dateRangeChangePT;
	}

	public void setDateRangeChangePT(boolean dateRangeChangePT) {
		this.dateRangeChangePT = dateRangeChangePT;
	}

	public void searchPT() {
		if (fce.getFcePreData().getDateRangePT().getStartDt() == null) {
			DisplayUtil.displayError("Permits: Start Date is empty.");
			return;
		}
		if (fce.getFcePreData().getDateRangePT().getEndDt() != null) {
			if (fce.getFcePreData().getDateRangePT().getStartDt()
					.after(fce.getFcePreData().getDateRangePT().getEndDt())) {
				DisplayUtil.displayError("Permits: Start Date is after End Date.");
				return;
			}
		}
		try {
			permitList = getFullComplianceEvalService().retrieveFcePermitsBySearch(fce);
			permitWrapper = new TableSorter();
			permitWrapper.setWrappedData(permitList);
			setDateRangeChangePT(false);
			String message = getSnapshotSearchSuccessMessage(PT_SNAPSHOT_DESC, false);
			if (permitList.size() == 0) {
				message = getSnapshotSearchSuccessMessage(PT_SNAPSHOT_DESC, true);
			}
			DisplayUtil.displayInfo(message);
		} catch (DAOException e) {
			logger.error("Could not search permits for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotSearchErrorMessage(PT_SNAPSHOT_DESC));
			handleBadDetailAndRedirect();
		}
	}

	public String resetPT() {
		fce.setPreDataDefaultDateRange(FCEPreSnapshotTypeDef.PT);
		setDateRangeChangePT(true);
		permitList = new ArrayList<Permit>();
		permitWrapper = new TableSorter();
		DisplayUtil.displayInfo(getSnapshotResetMessage(PT_SNAPSHOT_DESC));
		return PRE_INSPECTION_DATA_MENU;
	}

	public void setPreservedListPT() {
		try {
			HashSet<Integer> permitIds = new HashSet<Integer>();
			for (Permit item : permitList) {
				permitIds.add(item.getPermitID());
			}
			fce.getFcePreData().setPermitList(new ArrayList<Integer>(permitIds));

			if (fce.getFcePreData().getDateRangePT().getStartDt() == null) {
				DisplayUtil.displayError("Permits: Start Date is empty.");
			}
			if (fce.getFcePreData().getDateRangePT().getEndDt() == null) {
				fce.getFcePreData().getDateRangePT()
						.setEndDt(((InfrastructureDefs) FacesUtil.getManagedBean("infraDefs")).getTodaysDate());
			}
			// set Preserved List
			getFullComplianceEvalService().updateFcePermitsPreserved(fce);
			hasPreservedPT = true;

			setPermitWrapperFromPreservedList();
			DisplayUtil.displayInfo(getSnapshotPreservedMessage(PT_SNAPSHOT_DESC, true));
		} catch (RemoteException e) {
			logger.error("Could not preserve Permit list for " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotPreservedMessage(PT_SNAPSHOT_DESC, false));
			handleException(e);
		}
	}
	
	public void recallPreservedListPT() {
		try {
			setPermitWrapperFromPreservedList();
			setDateRangeChangePT(false);
			DisplayUtil.displayInfo(getSnapshotRecalledMessage(PT_SNAPSHOT_DESC, true));
		} catch (DAOException e) {
			logger.error("Could not recall preserved permit list for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotRecalledMessage(PT_SNAPSHOT_DESC, false));
			handleBadDetailAndRedirect();
		}
	}

	private void setPermitWrapperFromPreservedList() throws DAOException {
		permitList = getFullComplianceEvalService().retrieveFcePermitsPreserved(fce);
		permitWrapper = new TableSorter();
		permitWrapper.setWrappedData(permitList);
	}
	
	public void clearPreservedListPT() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		ConfirmWindow cw = (ConfirmWindow) FacesUtil.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO) || fce == null) {
			clearButtonClicked();
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			return;
		}

		try {
			// Date Range & List
			getFullComplianceEvalService().clearFcePermitsPreserved(fce);
			hasPreservedPT = false;
			resetPT();
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			DisplayUtil.displayInfo(getSnapshotClearedMessage(PT_SNAPSHOT_DESC, true));
		} catch (DAOException e) {
			logger.error("Could not clear permits for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotClearedMessage(PT_SNAPSHOT_DESC, false));
			handleException(e);
		} finally {
			clearButtonClicked();
		}
	}
	
	public void ptDateRangeChanged(ValueChangeEvent event){
		setDateRangeChangePT(true);
	}

	public boolean isPermitConditionsSelectionEditable() {
		return permitConditionsSelectionEditable;
	}

	public void setPermitConditionsSelectionEditable(boolean permitConditionsSelectionEditable) {
		this.permitConditionsSelectionEditable = permitConditionsSelectionEditable;
	}

	public void editFcePermitConditionSelection() {
		setPermitConditionsSelectionEditable(true);
	}
	
	public void saveFcePermitConditionSelection() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}

		try {
			getFullComplianceEvalService().modifyAssociatedPermitConditionIdRef(fce.getFcePermitConditions());
			includeExcludeSelectedPermitConditions();

			DisplayUtil.displayInfo("Updated successfully.");
			setPermitConditionsSelectionEditable(false);
		} catch (RemoteException e) {
			handleException("Failed to update inspection " + fce.getInspId(), e);
			return;
		} finally {
			try {
				fce = getFullComplianceEvalService().retrieveFce(facilityId, fce.getId());
				updatePermitConditionSelection();
			} catch (RemoteException e) {
				logger.error("Could not retrieve inspection : " + fce.getInspId());
				handleException("Failed to retrieve inspection " + fce.getInspId(), e);
			}
			clearButtonClicked();
		}
	}

	private void includeExcludeSelectedPermitConditions() throws RemoteException {
		List<Integer> permitConditionsToInclude = new ArrayList<Integer>();
		List<Integer> permitConditionsToExclude = new ArrayList<Integer>();

		if (includedPermitConditions != null && includedPermitConditions.getData() != null) {
			for (Object permitCond : includedPermitConditions.getData()) {
				FcePermitCondition permitCondition = (FcePermitCondition) permitCond;
				if (permitCondition.isSelected())
					permitConditionsToExclude.add(permitCondition.getPermitConditionId());
			}
		}

		if (excludedPermitConditions != null && excludedPermitConditions.getData() != null) {
			for (Object permitCond : excludedPermitConditions.getData()) {
				PermitConditionSearchLineItem permitCondition = (PermitConditionSearchLineItem) permitCond;
				if (permitCondition.isSelected())
					permitConditionsToInclude.add(permitCondition.getPermitConditionId());
			}
		}
		if (!permitConditionsToInclude.isEmpty() || !permitConditionsToExclude.isEmpty()) {
			getFullComplianceEvalService().updatePermitConditionSelections(fce, permitConditionsToInclude,
					permitConditionsToExclude);
		}
	}

	public void cancelFcePermitConditionSelection() {
		setPermitConditionsSelectionEditable(false);
	}

    //Stack Test Snapshot
	public List<FceStackTestSearchLineItem> getStackTestList() {
		return stackTestList;
	}

	public void setStackTestList(List<FceStackTestSearchLineItem> stackTestList) {
		this.stackTestList = stackTestList;
	}

	public TableSorter getStackTestWrapper() {
		return stackTestWrapper;
	}

	public void setStackTestWrapper(TableSorter stackTestWrapper) {
		this.stackTestWrapper = stackTestWrapper;
	}

	public boolean isHasPreservedST() {
		return hasPreservedST;
	}

	public void setHasPreservedST(boolean hasPreservedST) {
		this.hasPreservedST = hasPreservedST;
	}

	public boolean isDateRangeChangeST() {
		return dateRangeChangeST;
	}

	public void setDateRangeChangeST(boolean dateRangeChangeST) {
		this.dateRangeChangeST = dateRangeChangeST;
	}

	public void searchST() {
		if (fce.getFcePreData().getDateRangeST().getStartDt() == null) {
			DisplayUtil.displayError("Stack Test: Start Date is empty.");
			return;
		}
		if (fce.getFcePreData().getDateRangeST().getEndDt() != null) {
			if (fce.getFcePreData().getDateRangeST().getStartDt()
					.after(fce.getFcePreData().getDateRangeST().getEndDt())) {
				DisplayUtil.displayError("Stack Test: Start Date is after End Date.");
				return;
			}
		}
		try {
			stackTestList = getFullComplianceEvalService().retrieveFceStackTestsBySearch(fce);
			stackTestWrapper = new TableSorter();
			stackTestWrapper.setWrappedData(stackTestList);
			setDateRangeChangeST(false);
			String message = getSnapshotSearchSuccessMessage(ST_SNAPSHOT_DESC, false);
			if (stackTestList.size() == 0) {
				message = getSnapshotSearchSuccessMessage(ST_SNAPSHOT_DESC, true);
			}
			DisplayUtil.displayInfo(message);
		} catch (DAOException e) {
			logger.error("Could not search stack tests for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotSearchErrorMessage(ST_SNAPSHOT_DESC));
			handleBadDetailAndRedirect();
		}
	}

	public String resetST() {
		fce.setPreDataDefaultDateRange(FCEPreSnapshotTypeDef.ST);
		setDateRangeChangeST(true);
		stackTestList = new ArrayList<FceStackTestSearchLineItem>();
		stackTestWrapper = new TableSorter();
		DisplayUtil.displayInfo(getSnapshotResetMessage(ST_SNAPSHOT_DESC));
		return PRE_INSPECTION_DATA_MENU;
	}

	public void setPreservedListST() {
		try {
			HashSet<Integer> stackTestIds = new HashSet<Integer>();
			for (FceStackTestSearchLineItem item : stackTestList) {
				stackTestIds.add(item.getStackTestId());
			}
			fce.getFcePreData().setStackTestList(new ArrayList<Integer>(stackTestIds));

			if (fce.getFcePreData().getDateRangeST().getStartDt() == null) {
				DisplayUtil.displayError("Stack Test: Start Date is empty.");
			}
			if (fce.getFcePreData().getDateRangeST().getEndDt() == null) {
				fce.getFcePreData().getDateRangeST()
						.setEndDt(((InfrastructureDefs) FacesUtil.getManagedBean("infraDefs")).getTodaysDate());
			}
			// set Preserved List
			getFullComplianceEvalService().updateFceStackTestsPreserved(fce);
			hasPreservedST = true;
			setStackTestWrapperFromPreservedList();
			DisplayUtil.displayInfo(getSnapshotPreservedMessage(ST_SNAPSHOT_DESC, true));
		} catch (RemoteException e) {
			logger.error("Could not preserve Stack Test list for " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotPreservedMessage(ST_SNAPSHOT_DESC, false));
			handleException(e);
		}
	}
	
	public void recallPreservedListST(){		
		try{
			setStackTestWrapperFromPreservedList();
			setDateRangeChangeST(false);
			DisplayUtil.displayInfo(getSnapshotRecalledMessage(ST_SNAPSHOT_DESC, true));
		} catch(DAOException e) {
			logger.error("Could not recall preserved stack test list for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotRecalledMessage(ST_SNAPSHOT_DESC, false));
			handleBadDetailAndRedirect();
		}
	}
	
	private void setStackTestWrapperFromPreservedList() throws DAOException {
		stackTestList = getFullComplianceEvalService().retrieveFceStackTestsPreserved(fce);
		stackTestWrapper = new TableSorter();
		stackTestWrapper.setWrappedData(stackTestList);
	}
	
	public void clearPreservedListST(){
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		
		ConfirmWindow cw = (ConfirmWindow) FacesUtil.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO) || fce == null) {
			clearButtonClicked();
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			return;
		}
		
		try{
			getFullComplianceEvalService().clearFceStackTestsPreserved(fce); // Date Range & List
			hasPreservedST = false;
			resetST();
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			DisplayUtil.displayInfo(getSnapshotClearedMessage(ST_SNAPSHOT_DESC, true));
		} catch(DAOException e) {
			logger.error("Could not clear stack tests for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotClearedMessage(ST_SNAPSHOT_DESC, false));
			handleException(e);
		} finally{
	        clearButtonClicked();
		}
	}
	
	public void stDateRangeChanged(ValueChangeEvent event){
		setDateRangeChangeST(true);
	}
	
	public TableSorter getInspectionReportSectionsWrapper() {
		return inspectionReportSectionsWrapper;
	}

	public void setInspectionReportSectionsWrapper(TableSorter inspectionReportSectionsWrapper) {
		this.inspectionReportSectionsWrapper = inspectionReportSectionsWrapper;
	}

	public List<String> getInspectionReportSections() {
		return inspectionReportSections;
	}

	public void setInspectionReportSections(List<String> inspectionReportSections) {
		this.inspectionReportSections = inspectionReportSections;
	}

	public void selectedSections(SelectionEvent action) {
		
		this.selectedInspectionReportSections.clear();
		
		try {
			UIXTable table = this.inspectionReportSectionsWrapper.getTable();
			@SuppressWarnings("unchecked")
			Iterator<RowKeySet> selection = table.getSelectionState().getKeySet().iterator();
			while (selection.hasNext()) {
				table.setRowKey(selection.next());
				String row = (String) table.getRowData();
				this.getSelectedInspectionReportSections().add(new String(row));
			}
		} catch (Exception re) {
			logger.error("An error occured when selecting/unselecting sections for generating inspection report.", re);
			handleException(new RemoteException(re.getMessage(), re));
		}
	}

	public final String displayGenerateInspReportPopup() {
		
		initInspectionReportSectionsWrapper();

		return "dialog:generateInspectionReport";
	}

	public final void cancelInspectionReportGeneration() {
		
		this.inspectionReportSectionsWrapper = null;
		this.inspectionReportSections = null;
		
		FacesUtil.returnFromDialogAndRefresh();
	}

	public void initInspectionReportSectionsWrapper() {
	
		this.inspectionReportSections = new ArrayList<String>(Arrays.asList(
				INSPECTION_CONCERNS,
				FILE_REVIEW_OR_RECORDS_REQUEST,
				REGULATORY_DISCUSSION,
				PHYSICAL_INSPECTION_OF_PLANT,
				AMBIENT_MONITORING,
				OTHER_INFORMATION,
				PERMIT_APPLICATIONS,
				PERMIT_CONDITIONS_AND_STATUS,
				PERMITS,
				STACK_TESTS,
				COMPLIANCE_REPORTS,
				EMISSIONS_INVENTORIES,
				SITE_VISITS,
				CEM_COM_CMS,
				AMBIENT_MONITORS,
				CONTROL_EQUIPMENT,
				RELEASE_POINTS,
				EMISSION_UNITS,
				ATTACHMENTS
			)
		);
		
		this.selectedInspectionReportSections = new ArrayList<String>();
		
		this.inspectionReportSectionsWrapper = new TableSorter();
		this.inspectionReportSectionsWrapper.setWrappedData(this.inspectionReportSections);
		this.selectAllInspReportSections = true;
	}

	public List<String> getSelectedInspectionReportSections() {
		return selectedInspectionReportSections;
	}

	public void setSelectedInspectionReportSections(List<String> selectedInspectionReportSections) {
		this.selectedInspectionReportSections = selectedInspectionReportSections;
	}
	
	public String generateInspectionReportDocument() {

		String ret = null;
		Document template = TemplateDocTypeDef.getTemplateAsAsposeDocument(TemplateDocTypeDef.INSPECTION_REPORT);

		if (null != template) {

			try {
				// list for storing tags associated with sections that were not selected
				List<String> excludedTags = new ArrayList<String>();
				
				for (String section : this.inspectionReportSections) {
					if (!this.selectedInspectionReportSections.contains(section)) {
						excludedTags.add(sectionTagMap.get(section));
						excludedTags.add(sectionHeadingTagMap.get(section));
					}
				}
				
				// first generate the facility information
				DocumentGenerator docGenerator = new FacilityDocumentGenerator();
				Document facilityReport = docGenerator.generateDocument(this.fce.getFpId(), template, excludedTags);
				
				// next generate the inspection information based on the selected sections
				docGenerator = new InspectionDocumentGenerator();
				Document inspectionReport = docGenerator.generateDocument(this.fceId, facilityReport, excludedTags);
				
				// getting the tmp path where the document is stored.
				currentInspRptPath = getInspectionReportPath();
				boolean saved = DocumentUtil.saveDocument(inspectionReport, currentInspRptPath);
				
				if (!saved) {
					DisplayUtil.displayError(
							"ERROR: An error occured while saving the generated report. Please contact the system administrator.");
				} else {
					ret = "dialog:downloadInspectionReport";
				}
			} catch (DocumentGenerationException dge) {
				DisplayUtil.displayError("An error occured when generating the the Inspection Report.");
				logger.error(dge.getMessage(), dge);
			}
		} else {
			DisplayUtil.displayError(
					"An error occured when trying to read Inspection Report template. Check if the template exists.");
			FacesUtil.returnFromDialog();
		}
		
		return ret;
	}

	//Compliance Report
	public List<ComplianceReportList> getComplianceReportList() {
		return complianceReportList;
	}

	public void setComplianceReportList(List<ComplianceReportList> complianceReportList) {
		this.complianceReportList = complianceReportList;
	}

	public TableSorter getComplianceReportWrapper() {
		return complianceReportWrapper;
	}

	public void setComplianceReportWrapper(TableSorter complianceReportWrapper) {
		this.complianceReportWrapper = complianceReportWrapper;
	}

	public boolean isHasPreservedCR() {
		return hasPreservedCR;
	}

	public void setHasPreservedCR(boolean hasPreservedCR) {
		this.hasPreservedCR = hasPreservedCR;
	}

	public boolean isDateRangeChangeCR() {
		return dateRangeChangeCR;
	}

	public void setDateRangeChangeCR(boolean dateRangeChangeCR) {
		this.dateRangeChangeCR = dateRangeChangeCR;
	}
	
	public void crDateRangeChanged(ValueChangeEvent event){
		setDateRangeChangeCR(true);
	}
	

	public void searchCR() {
		if (fce.getFcePreData().getDateRangeCR().getStartDt() == null) {
			DisplayUtil.displayError("Compliance Report: Start Date is empty.");
			return;
		}
		if (fce.getFcePreData().getDateRangeCR().getEndDt() != null) {
			if (fce.getFcePreData().getDateRangeCR().getStartDt()
					.after(fce.getFcePreData().getDateRangeCR().getEndDt())) {
				DisplayUtil.displayError("Compliance Report: Start Date is after End Date.");
				return;
			}
		}
		try {
			complianceReportList = getFullComplianceEvalService().retrieveFceComplianceReportBySearch(fce);
			complianceReportWrapper = new TableSorter();
			complianceReportWrapper.setWrappedData(complianceReportList);
			setDateRangeChangeCR(false);
			String message = getSnapshotSearchSuccessMessage(CR_SNAPSHOT_DESC, false);
			if (complianceReportList.size() == 0) {
				message = getSnapshotSearchSuccessMessage(CR_SNAPSHOT_DESC, true);
			}
			DisplayUtil.displayInfo(message);
		} catch (DAOException e) {
			logger.error("Could not search compliance reports for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotSearchErrorMessage(CR_SNAPSHOT_DESC));
			handleBadDetailAndRedirect();
		}
	}

	public String resetCR() {
		fce.setPreDataDefaultDateRange(FCEPreSnapshotTypeDef.CR);
		setDateRangeChangeCR(true);
		complianceReportList = new ArrayList<ComplianceReportList>();
		complianceReportWrapper = new TableSorter();
		DisplayUtil.displayInfo(getSnapshotResetMessage(CR_SNAPSHOT_DESC));
		return PRE_INSPECTION_DATA_MENU;
	}

	public void setPreservedListCR() {
		try {
			HashSet<Integer> complianceReportIds = new HashSet<Integer>();
			for (ComplianceReportList item : complianceReportList) {
				complianceReportIds.add(item.getReportId());
			}
			fce.getFcePreData().setComplianceReportList(new ArrayList<Integer>(complianceReportIds));

			if (fce.getFcePreData().getDateRangeCR().getStartDt() == null) {
				DisplayUtil.displayError("Compliance Report: Start Date is empty.");
			}
			if (fce.getFcePreData().getDateRangeCR().getEndDt() == null) {
				fce.getFcePreData().getDateRangeCR()
						.setEndDt(((InfrastructureDefs) FacesUtil.getManagedBean("infraDefs")).getTodaysDate());
			}
			// set Preserved List
			getFullComplianceEvalService().updateFceComplianceReportsPreserved(fce);
			hasPreservedCR = true;
			setComplianceReportWrapperFromPreservedList();
			DisplayUtil.displayInfo(getSnapshotPreservedMessage(CR_SNAPSHOT_DESC, true));
		} catch (RemoteException e) {
			logger.error("Could not preserve Compliance Report list for " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotPreservedMessage(CR_SNAPSHOT_DESC, false));
			handleException(e);
		}
	}
	
	public void recallPreservedListCR() {
		try {
			setComplianceReportWrapperFromPreservedList();
			setDateRangeChangeCR(false);
			DisplayUtil.displayInfo(getSnapshotRecalledMessage(CR_SNAPSHOT_DESC, true));
		} catch (DAOException e) {
			logger.error("Could not recall preserved Compliance Report list for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotRecalledMessage(CR_SNAPSHOT_DESC, false));
			handleBadDetailAndRedirect();
		}
	}

	private void setComplianceReportWrapperFromPreservedList() throws DAOException {
		complianceReportList = getFullComplianceEvalService().retrieveFceComplianceReportsPreserved(fce);
		complianceReportWrapper = new TableSorter();
		complianceReportWrapper.setWrappedData(complianceReportList);
	}
	
	public void clearPreservedListCR() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}

		ConfirmWindow cw = (ConfirmWindow) FacesUtil.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO) || fce == null) {
			clearButtonClicked();
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			return;
		}

		try {
			getFullComplianceEvalService().clearFceComplianceReportsPreserved(fce);
			hasPreservedCR = false;
			resetCR();
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			DisplayUtil.displayInfo(getSnapshotClearedMessage(CR_SNAPSHOT_DESC, true));
		} catch (DAOException e) {
			logger.error("Could not clear Compliance Reports for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotClearedMessage(CR_SNAPSHOT_DESC, false));
			handleException(e);
		} finally {
			clearButtonClicked();
		}
	}
	
	public List<SiteVisit> getSiteVisitList() {
		return siteVisitList;
	}

	public void setSiteVisitList(List<SiteVisit> siteVisitList) {
		this.siteVisitList = siteVisitList;
	}

	public TableSorter getSiteVisitsWrapper() {
		return siteVisitsWrapper;
	}

	public void setSiteVisitsWrapper(TableSorter siteVisitsWrapper) {
		this.siteVisitsWrapper = siteVisitsWrapper;
	}
	
	public boolean isHasPreservedSiteVisit() {
		return hasPreservedSiteVisit;
	}

	public void setHasPreservedSiteVisit(boolean hasPreservedSiteVisit) {
		this.hasPreservedSiteVisit = hasPreservedSiteVisit;
	}

	public boolean isDateRangeChangeSiteVisit() {
		return dateRangeChangeSiteVisit;
	}

	public void setDateRangeChangeSiteVisit(boolean dateRangeChangeSiteVisit) {
		this.dateRangeChangeSiteVisit = dateRangeChangeSiteVisit;
	}
	
	public void siteVisitsDateRangeChanged(ValueChangeEvent event){
		setDateRangeChangeSiteVisit(true);
	}
	
	public void searchSiteVisit() {
		if (fce.getFcePreData().getDateRangeSiteVisits().getStartDt() == null) {
			DisplayUtil.displayError("Site Visits: Start Date is empty.");
			return;
		}
		if (fce.getFcePreData().getDateRangeSiteVisits().getEndDt() != null) {
			if (fce.getFcePreData().getDateRangeSiteVisits().getStartDt()
					.after(fce.getFcePreData().getDateRangeSiteVisits().getEndDt())) {
				DisplayUtil.displayError("Site Visits: Start Date is after End Date.");
				return;
			}
		}
		try {
			SiteVisit[] sv = getFullComplianceEvalService().searchSiteVisits(fce.getFacilityId(),
					fce.getFcePreData().getDateRangeSiteVisits().getStartDt(),
					fce.getFcePreData().getDateRangeSiteVisits().getEndDt());
			siteVisitList = Arrays.asList(sv);
			Collections.sort(siteVisitList, new Comparator<SiteVisit>() {
				@Override
				public int compare(SiteVisit sv1, SiteVisit sv2) {
					return sv1.getSiteId().compareTo(sv2.getSiteId());
				}
			});
			siteVisitsWrapper = new TableSorter();
			siteVisitsWrapper.setWrappedData(siteVisitList);
			setDateRangeChangeSiteVisit(false);
			String message = getSnapshotSearchSuccessMessage(SV_SNAPSHOT_DESC, false);
			if (siteVisitList.size() == 0) {
				message = getSnapshotSearchSuccessMessage(SV_SNAPSHOT_DESC, true);
			}
			DisplayUtil.displayInfo(message);
		} catch (RemoteException e) {
			logger.error("Could not search Site Visits for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotSearchErrorMessage(AM_SNAPSHOT_DESC));
			handleBadDetailAndRedirect();
		}
	}

	public String resetSiteVisit() {
		Timestamp referenceReviewStartDate = fce.getReferenceReviewStartDate();
		fce.getFcePreData().getDateRangeSiteVisits().setStartDt(referenceReviewStartDate);
		fce.getFcePreData().getDateRangeSiteVisits().setEndDt(null);
		setDateRangeChangeSiteVisit(true);
		siteVisitList = new ArrayList<SiteVisit>();
		siteVisitsWrapper = new TableSorter();
		DisplayUtil.displayInfo(getSnapshotResetMessage(SV_SNAPSHOT_DESC));
		return PRE_INSPECTION_DATA_MENU;
	}
	
	public void setPreservedListSiteVisit() {
		try {
			HashSet<Integer> siteVisitIds = new HashSet<Integer>();
			for (SiteVisit item : siteVisitList) {
				siteVisitIds.add(item.getId());
			}
			fce.getFcePreData().setSiteVisitList(new ArrayList<Integer>(siteVisitIds));

			if (fce.getFcePreData().getDateRangeSiteVisits().getStartDt() == null) {
				DisplayUtil.displayError("Site Visits: Start Date is empty.");
			}
			if (fce.getFcePreData().getDateRangeSiteVisits().getEndDt() == null) {
				fce.getFcePreData().getDateRangeSiteVisits()
						.setEndDt(((InfrastructureDefs) FacesUtil.getManagedBean("infraDefs")).getTodaysDate());
			}
			// set Preserved List
			getFullComplianceEvalService().updateFceSiteVisitsPreserved(fce);
			hasPreservedSiteVisit = true;
			setSiteVisitWrapperFromPreservedList();
			DisplayUtil.displayInfo(getSnapshotPreservedMessage(SV_SNAPSHOT_DESC, true));
		} catch (RemoteException e) {
			logger.error("Could not preserve Site Visit list for " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotPreservedMessage(SV_SNAPSHOT_DESC, false));
			handleException(e);
		}
	}

	public void recallPreservedListSiteVisit() {
		try {
			setSiteVisitWrapperFromPreservedList();
			setDateRangeChangeSiteVisit(false);
			DisplayUtil.displayInfo(getSnapshotRecalledMessage(SV_SNAPSHOT_DESC, true));
		} catch (DAOException e) {
			logger.error("Could not recall preserved Site Visit list for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotRecalledMessage(SV_SNAPSHOT_DESC, false));
			handleBadDetailAndRedirect();
		}
	}

	private void setSiteVisitWrapperFromPreservedList() throws DAOException {
		siteVisitList = getFullComplianceEvalService().retrieveFceSiteVisitsPreserved(fce);
		siteVisitsWrapper = new TableSorter();
		siteVisitsWrapper.setWrappedData(siteVisitList);
	}
	
	public void clearPreservedListSiteVisit() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		ConfirmWindow cw = (ConfirmWindow) FacesUtil.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO) || fce == null) {
			clearButtonClicked();
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			return;
		}

		try {
			getFullComplianceEvalService().clearFceSiteVisitsPreserved(fce);
			hasPreservedSiteVisit = false;
			Timestamp referenceReviewStartDate = fce.getReferenceReviewStartDate();
			fce.getFcePreData().getDateRangeSiteVisits().setStartDt(referenceReviewStartDate);
			fce.getFcePreData().getDateRangeSiteVisits().setEndDt(null);
			siteVisitList = new ArrayList<SiteVisit>();
			siteVisitsWrapper = new TableSorter();
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			DisplayUtil.displayInfo(getSnapshotClearedMessage(SV_SNAPSHOT_DESC, true));
		} catch (RemoteException e) {
			logger.error("Could not clear Site Visit for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotClearedMessage(SV_SNAPSHOT_DESC, false));
			handleException(e);
		} finally {
			clearButtonClicked();
		}
	}
	
	public List<FceAmbientMonitorLineItem> getAmbientMonitorList() {
		return ambientMonitorList;
	}

	public void setAmbientMonitorList(List<FceAmbientMonitorLineItem> ambientMonitorList) {
		this.ambientMonitorList = ambientMonitorList;
	}

	public TableSorter getAmbientMonitorsWrapper() {
		return ambientMonitorsWrapper;
	}

	public void setAmbientMonitorsWrapper(TableSorter ambientMonitorsWrapper) {
		this.ambientMonitorsWrapper = ambientMonitorsWrapper;
	}

	public boolean isHasPreservedAmbientMonitor() {
		return hasPreservedAmbientMonitor;
	}

	public void setHasPreservedAmbientMonitor(boolean hasPreservedAmbientMonitor) {
		this.hasPreservedAmbientMonitor = hasPreservedAmbientMonitor;
	}

	public boolean isDateRangeChangeAmbientMonitor() {
		return dateRangeChangeAmbientMonitor;
	}

	public void setDateRangeChangeAmbientMonitor(boolean dateRangeChangeAmbientMonitor) {
		this.dateRangeChangeAmbientMonitor = dateRangeChangeAmbientMonitor;
	}
	
	public void ambientMonitorsDateRangeChanged(ValueChangeEvent event){
		setDateRangeChangeAmbientMonitor(true);
	}

	public void searchAmbientMonitors() {
		if (fce.getFcePreData().getDateRangeAmbientMonitors().getStartDt() == null) {
			DisplayUtil.displayError("Ambient Monitors: Start Date is empty.");
			return;
		}
		if (fce.getFcePreData().getDateRangeAmbientMonitors().getEndDt() != null) {
			if (fce.getFcePreData().getDateRangeAmbientMonitors().getStartDt()
					.after(fce.getFcePreData().getDateRangeAmbientMonitors().getEndDt())) {
				DisplayUtil.displayError("Ambient Monitors: Start Date is after End Date.");
				return;
			}
		}

		try {
			ambientMonitorList = getFullComplianceEvalService().searchFacilityMonitorsByDate(fce);
			ambientMonitorsWrapper = new TableSorter();
			ambientMonitorsWrapper.setWrappedData(ambientMonitorList);
			setDateRangeChangeAmbientMonitor(false);
			String message = getSnapshotSearchSuccessMessage(AM_SNAPSHOT_DESC, false);
			if (ambientMonitorList.size() == 0) {
				message = getSnapshotSearchSuccessMessage(AM_SNAPSHOT_DESC, true);
			}
			DisplayUtil.displayInfo(message);
		} catch (RemoteException e) {
			logger.error("Could not search Ambient Monitors for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotSearchErrorMessage(AM_SNAPSHOT_DESC));
			handleBadDetailAndRedirect();
		}
	}

	public String resetAmbientMonitors() {
		Timestamp referenceReviewStartDate = fce.getReferenceReviewStartDate();
		fce.getFcePreData().getDateRangeAmbientMonitors().setStartDt(referenceReviewStartDate);
		fce.getFcePreData().getDateRangeAmbientMonitors().setEndDt(null);
		setDateRangeChangeAmbientMonitor(true);
		ambientMonitorList = new ArrayList<FceAmbientMonitorLineItem>();
		ambientMonitorsWrapper = new TableSorter();
		DisplayUtil.displayInfo(getSnapshotResetMessage(AM_SNAPSHOT_DESC));
		return PRE_INSPECTION_DATA_MENU;
	}

	public void setPreservedListAmbientMonitor() {
		try {
			HashSet<Integer> ambientMonitorSiteIds = new HashSet<Integer>();
			for (FceAmbientMonitorLineItem item : ambientMonitorList) {
				ambientMonitorSiteIds.add(item.getSiteId());
			}
			fce.getFcePreData().setAmbientMonitorList(new ArrayList<Integer>(ambientMonitorSiteIds));

			if (fce.getFcePreData().getDateRangeAmbientMonitors().getStartDt() == null) {
				DisplayUtil.displayError("Ambient Monitors: Start Date is empty.");
			}
			if (fce.getFcePreData().getDateRangeAmbientMonitors().getEndDt() == null) {
				fce.getFcePreData().getDateRangeAmbientMonitors()
						.setEndDt(((InfrastructureDefs) FacesUtil.getManagedBean("infraDefs")).getTodaysDate());
			}
			// set Preserved List
			getFullComplianceEvalService().updateFceAmbientMonitorsPreserved(fce);
			hasPreservedAmbientMonitor = true;
			setAmbientMonitorWrapperFromPreservedList();
			DisplayUtil.displayInfo(getSnapshotPreservedMessage(AM_SNAPSHOT_DESC, true));
		} catch (RemoteException e) {
			logger.error("Could not preserve Ambient Monitor list for " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotPreservedMessage(AM_SNAPSHOT_DESC, false));
			handleException(e);
		}
	}
	
	public void recallPreservedListAmbientMonitor() {
		try {
			setAmbientMonitorWrapperFromPreservedList();
			setDateRangeChangeAmbientMonitor(false);
			DisplayUtil.displayInfo(getSnapshotRecalledMessage(AM_SNAPSHOT_DESC, true));
		} catch (DAOException e) {
			logger.error("Could not recall preserved Ambient Monitor list for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotRecalledMessage(AM_SNAPSHOT_DESC, false));
			handleBadDetailAndRedirect();
		}
	}

	private void setAmbientMonitorWrapperFromPreservedList() throws DAOException {
		ambientMonitorList = getFullComplianceEvalService().retrieveFceAmbientMonitorsPreserved(fce);
		ambientMonitorsWrapper = new TableSorter();
		ambientMonitorsWrapper.setWrappedData(ambientMonitorList);
	}
	
	public void clearPreservedListAmbientMonitor() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		ConfirmWindow cw = (ConfirmWindow) FacesUtil.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO) || fce == null) {
			clearButtonClicked();
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			return;
		}

		try {
			getFullComplianceEvalService().clearFceAmbientMonitorsPreserved(fce);
			hasPreservedAmbientMonitor = false;
			Timestamp referenceReviewStartDate = fce.getReferenceReviewStartDate();
			fce.getFcePreData().getDateRangeAmbientMonitors().setStartDt(referenceReviewStartDate);
			fce.getFcePreData().getDateRangeAmbientMonitors().setEndDt(null);
			ambientMonitorList = new ArrayList<FceAmbientMonitorLineItem>();
			ambientMonitorsWrapper = new TableSorter();
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			DisplayUtil.displayInfo(getSnapshotClearedMessage(AM_SNAPSHOT_DESC, true));
		} catch (DAOException e) {
			logger.error("Could not clear Ambient Monitor for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotClearedMessage(AM_SNAPSHOT_DESC, false));
			handleException(e);
		} finally {
			clearButtonClicked();
		}
	}
	
	//Correspondence
	public List<Correspondence> getCorrespondenceList() {
		return correspondenceList;
	}

	public void setCorrespondenceList(List<Correspondence> correspondenceList) {
		this.correspondenceList = correspondenceList;
	}

	public TableSorter getCorrespondenceWrapper() {
		return correspondenceWrapper;
	}

	public void setCorrespondenceWrapper(TableSorter correspondenceWrapper) {
		this.correspondenceWrapper = correspondenceWrapper;
	}

	public boolean isHasPreservedDC() {
		return hasPreservedDC;
	}

	public void setHasPreservedDC(boolean hasPreservedDC) {
		this.hasPreservedDC = hasPreservedDC;
	}

	public boolean isDateRangeChangeDC() {
		return dateRangeChangeDC;
	}

	public void setDateRangeChangeDC(boolean dateRangeChangeDC) {
		this.dateRangeChangeDC = dateRangeChangeDC;
	}
	
	public void dcDateRangeChanged(ValueChangeEvent event){
		setDateRangeChangeDC(true);
	}
	

	public void searchDC() {
		if (fce.getFcePreData().getDateRangeDC().getStartDt() == null) {
			DisplayUtil.displayError("Correspondence: Start Date is empty.");
			return;
		}
		if (fce.getFcePreData().getDateRangeDC().getEndDt() != null) {
			if (fce.getFcePreData().getDateRangeDC().getStartDt()
					.after(fce.getFcePreData().getDateRangeDC().getEndDt())) {
				DisplayUtil.displayError("Correspondence: Start Date is after End Date.");
				return;
			}
		}
		try {
			correspondenceList = getFullComplianceEvalService().retrieveFceCorrespondenceBySearch(fce);
			correspondenceWrapper = new TableSorter();
			correspondenceWrapper.setWrappedData(correspondenceList);
			setDateRangeChangeDC(false);
			String message = getSnapshotSearchSuccessMessage(CORR_SNAPSHOT_DESC, false);
			if (correspondenceList.size() == 0) {
				message = "There is no Correspondence that match the search date range.";
			}
			DisplayUtil.displayInfo(message);
		} catch (DAOException e) {
			logger.error("Could not search correspondences for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotSearchErrorMessage(CORR_SNAPSHOT_DESC));
			handleBadDetailAndRedirect();
		}
	}

	public String resetDC() {
		Timestamp referenceReviewStartDate = fce.getReferenceReviewStartDate();
		fce.getFcePreData().getDateRangeDC().setStartDt(referenceReviewStartDate);
		fce.getFcePreData().getDateRangeDC().setEndDt(null);
		setDateRangeChangeDC(true);
		correspondenceList = new ArrayList<Correspondence>();
		correspondenceWrapper = new TableSorter();
		DisplayUtil.displayInfo(getSnapshotResetMessage(CORR_SNAPSHOT_DESC));
		return PRE_INSPECTION_DATA_MENU;
	}

	public void setPreservedListDC() {
		try {
			HashSet<Integer> correspondenceIds = new HashSet<Integer>();
			for (Correspondence item : correspondenceList) {
				correspondenceIds.add(item.getCorrespondenceID());
			}
			fce.getFcePreData().setCorrespondenceList(new ArrayList<Integer>(correspondenceIds));
			if (fce.getFcePreData().getDateRangeDC().getStartDt() == null) {
				DisplayUtil.displayError("Correspondence: Start Date is empty.");
			}
			if (fce.getFcePreData().getDateRangeDC().getEndDt() == null) {
				fce.getFcePreData().getDateRangeDC()
						.setEndDt(((InfrastructureDefs) FacesUtil.getManagedBean("infraDefs")).getTodaysDate());
			}
			// set Preserved List
			getFullComplianceEvalService().updateFceCorrespondencesPreserved(fce);
			hasPreservedDC = true;
			setCorrespondenceWrapperFromPreservedList();
			DisplayUtil.displayInfo(getSnapshotPreservedMessage(CORR_SNAPSHOT_DESC, true));
		} catch (RemoteException e) {
			logger.error("Could not preserve Correspondences list for " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotPreservedMessage(CORR_SNAPSHOT_DESC, false));
			handleException(e);
		}
	}
	
	public void recallPreservedListDC() {
		try {
			setCorrespondenceWrapperFromPreservedList();
			setDateRangeChangeCR(false);
			DisplayUtil.displayInfo(getSnapshotRecalledMessage(CORR_SNAPSHOT_DESC, true));
		} catch (DAOException e) {
			logger.error("Could not recall preserved Correspondence list for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotRecalledMessage(CORR_SNAPSHOT_DESC, false));
			handleBadDetailAndRedirect();
		}
	}

	private void setCorrespondenceWrapperFromPreservedList() throws DAOException {
		correspondenceList = getFullComplianceEvalService().retrieveFceCorrespondencesPreserved(fce);
		correspondenceWrapper = new TableSorter();
		correspondenceWrapper.setWrappedData(correspondenceList);
	}
	
	public void clearPreservedListDC(){
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		
		ConfirmWindow cw = (ConfirmWindow) FacesUtil.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO) || fce == null) {
			clearButtonClicked();
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			return;
		}
		
		try{
			getFullComplianceEvalService().clearFceCorrespondencesPreserved(fce); // Date Range & List
			hasPreservedDC = false;
			resetDC();
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			DisplayUtil.displayInfo(getSnapshotClearedMessage(CORR_SNAPSHOT_DESC, true));
		} catch(DAOException e) {
			logger.error("Could not clear Correspondence for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotClearedMessage(CORR_SNAPSHOT_DESC, false));
			handleException(e);
		} finally{
	        clearButtonClicked();
		}
	}
	
	// Generate Inspection Info Document
	public String generateInspectionInfoDocument() {

		String ret = null;

		Document template = TemplateDocTypeDef.getTemplateAsAsposeDocument(TemplateDocTypeDef.INSPECTION_INFO_DOCUMENT);

		if (null != template) {

			try {

				// first generate the facility information
				DocumentGenerator docGenerator = new FacilityDocumentGenerator();
				Document facilityInfoDocument = docGenerator.generateDocument(this.fce.getFpId(), template);

				// next generate the inspection information
				docGenerator = new InspectionDocumentGenerator();
				Document inspectionInfoDocument = docGenerator.generateDocument(this.fceId, facilityInfoDocument);

				// save the generated document under tmp path
				boolean saved = DocumentUtil.saveDocument(inspectionInfoDocument, getInspectionInfoDocumentPath());
				if (!saved) {
					DisplayUtil.displayError(
							"ERROR: An error occured while saving the generated document. Please contact the system administrator");
				} else {
					// navigate to the document download popup
					ret = "dialog:generateInspectionInfoDoc";
				}

			} catch (DocumentGenerationException dge) {
				DisplayUtil.displayError("An error occured when generating the inspection info document.");
				logger.error(dge.getMessage(), dge);
			}
		} else {
			DisplayUtil.displayError(
					"An error occured when trying to read Inspection Info template. Check if the template exists.");
		}

		return ret;
	}

	public String getInspectionInfoDocURL() {

		String docUrl = null;

		if (DocumentUtil.isFileExists(getInspectionInfoDocumentPath())) {

			docUrl = DocumentUtil.getFileStoreBaseURL() + getInspectionInfoDocumentPath();
		} else {
			logger.error("File not found " + getInspectionInfoDocumentPath());
			DisplayUtil.displayError("ERROR: Inspection Info document not found");
		}

		return docUrl;
	}

	public List<FceContinuousMonitorLineItem> getContinuousMonitorList() {
		return continuousMonitorList;
	}

	public void setContinuousMonitorList(List<FceContinuousMonitorLineItem> continuousMonitorList) {
		this.continuousMonitorList = continuousMonitorList;
	}

	public TableSorter getContinuousMonitorsWrapper() {
		return continuousMonitorsWrapper;
	}

	public void setContinuousMonitorsWrapper(TableSorter continuousMonitorsWrapper) {
		this.continuousMonitorsWrapper = continuousMonitorsWrapper;
	}

	public boolean isHasPreservedContinuousMonitor() {
		return hasPreservedContinuousMonitor;
	}

	public void setHasPreservedContinuousMonitor(boolean hasPreservedContinuousMonitor) {
		this.hasPreservedContinuousMonitor = hasPreservedContinuousMonitor;
	}

	public boolean isDateRangeChangeContinuousMonitor() {
		return dateRangeChangeContinuousMonitor;
	}

	public void setDateRangeChangeContinuousMonitor(boolean dateRangeChangeContinuousMonitor) {
		this.dateRangeChangeContinuousMonitor = dateRangeChangeContinuousMonitor;
	}

	public void continuousMonitorsDateRangeChanged(ValueChangeEvent event){
		setDateRangeChangeContinuousMonitor(true);
	}
	
	public void searchContinuousMonitorLimits() {
		if (fce.getFcePreData().getDateRangeContinuousMonitors().getStartDt() == null) {
			DisplayUtil.displayError("CEM/COM/CMS: Start Date is empty.");
			return;
		}
		if (fce.getFcePreData().getDateRangeContinuousMonitors().getEndDt() != null) {
			if (fce.getFcePreData().getDateRangeContinuousMonitors().getStartDt()
					.after(fce.getFcePreData().getDateRangeContinuousMonitors().getEndDt())) {
				DisplayUtil.displayError("CEM/COM/CMS: Start Date is after End Date.");
				return;
			}
		}

		try {
			continuousMonitorList = getFullComplianceEvalService().searchFacilityCemComLimitsByDate(fce);
			continuousMonitorsWrapper = new TableSorter();
			continuousMonitorsWrapper.setWrappedData(continuousMonitorList);
			setDateRangeChangeContinuousMonitor(false);
			String message = getSnapshotSearchSuccessMessage(CEM_SNAPSHOT_DESC, false);
			if (continuousMonitorList.size() == 0) {
				message = getSnapshotSearchSuccessMessage(CEM_SNAPSHOT_DESC, true);
			}
			DisplayUtil.displayInfo(message);
		} catch (DAOException e) {
			logger.error("Could not search CEM/COM/CMS for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotSearchErrorMessage(CEM_SNAPSHOT_DESC));
			handleBadDetailAndRedirect();
		}
	}

	public String resetContinuousMonitorLimits() {
		Timestamp referenceReviewStartDate = fce.getReferenceReviewStartDate();
		fce.getFcePreData().getDateRangeContinuousMonitors().setStartDt(referenceReviewStartDate);
		fce.getFcePreData().getDateRangeContinuousMonitors().setEndDt(null);
		setDateRangeChangeContinuousMonitor(true);
		continuousMonitorList = new ArrayList<FceContinuousMonitorLineItem>();
		continuousMonitorsWrapper = new TableSorter();
		DisplayUtil.displayInfo(getSnapshotResetMessage(CEM_SNAPSHOT_DESC));
		return PRE_INSPECTION_DATA_MENU;
	}

	public void setPreservedListContinuousMonitorLimits() {
		try {
			HashSet<Integer> limitIds = new HashSet<Integer>();
			for (FceContinuousMonitorLineItem item : continuousMonitorList) {
				limitIds.add(item.getLimitId());
			}
			fce.getFcePreData().setContinuousMonitorList(new ArrayList<Integer>(limitIds));

			if (fce.getFcePreData().getDateRangeContinuousMonitors().getStartDt() == null) {
				DisplayUtil.displayError("CEM/COM/CMS Monitors: Start Date is empty.");
			}
			if (fce.getFcePreData().getDateRangeContinuousMonitors().getEndDt() == null) {
				fce.getFcePreData().getDateRangeContinuousMonitors()
						.setEndDt(((InfrastructureDefs) FacesUtil.getManagedBean("infraDefs")).getTodaysDate());
			}
			// set Preserved List
			getFullComplianceEvalService().updateFceCemComLimitsPreserved(fce);
			hasPreservedContinuousMonitor = true;
			setContinuousMonitorWrapperFromPreservedList();
			DisplayUtil.displayInfo(getSnapshotPreservedMessage(CEM_SNAPSHOT_DESC, true));
		} catch (RemoteException e) {
			logger.error("Could not preserve CEM/COM/CMS list for " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotPreservedMessage(CEM_SNAPSHOT_DESC, false));
			handleException(e);
		}
	}

	public void recallPreservedListContinuousMonitorLimits() {
		try {
			setContinuousMonitorWrapperFromPreservedList();
			setDateRangeChangeContinuousMonitor(false);
			DisplayUtil.displayInfo(getSnapshotRecalledMessage(CEM_SNAPSHOT_DESC, true));
		} catch (DAOException e) {
			logger.error("Could not recall preserved CEM/COM/CMS Monitor list for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotRecalledMessage(CEM_SNAPSHOT_DESC, false));
			handleBadDetailAndRedirect();
		}
	}

	private void setContinuousMonitorWrapperFromPreservedList() throws DAOException {
		continuousMonitorList = getFullComplianceEvalService().retrieveFceCemComLimitsPreserved(fce);
		continuousMonitorsWrapper = new TableSorter();
		continuousMonitorsWrapper.setWrappedData(continuousMonitorList);
	}
	
	public void clearPreservedListContinuousMonitorLimits() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		ConfirmWindow cw = (ConfirmWindow) FacesUtil.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO) || fce == null) {
			clearButtonClicked();
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			return;
		}

		try {
			getFullComplianceEvalService().clearFceCemComLimitsPreserved(fce); // Date Range &
																					// List
			hasPreservedContinuousMonitor = false;
			Timestamp referenceReviewStartDate = fce.getReferenceReviewStartDate();
			fce.getFcePreData().getDateRangeContinuousMonitors().setStartDt(referenceReviewStartDate);
			fce.getFcePreData().getDateRangeContinuousMonitors().setEndDt(null);
			continuousMonitorList = new ArrayList<FceContinuousMonitorLineItem>();
			continuousMonitorsWrapper = new TableSorter();
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			DisplayUtil.displayInfo(getSnapshotClearedMessage(CEM_SNAPSHOT_DESC, true));
		} catch (DAOException e) {
			logger.error("Could not clear CEM/COM/CMS Monitor for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotClearedMessage(CEM_SNAPSHOT_DESC, false));
			handleException(e);
		} finally {
			clearButtonClicked();
		}
	}
	
	public final String viewFacilityCemComLimit() {

		int index = this.continuousMonitorsWrapper.getRowIndex();
		FceContinuousMonitorLineItem limit = (FceContinuousMonitorLineItem) this.continuousMonitorsWrapper
				.getRowData(index);
		FacilityCemComLimit facilityCemComLimit = new FacilityCemComLimit(limit);
		
		FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
    	fp.setFacilityId(facilityId);
    	fp.setFacility(this.facility);
    	fp.setFpId(fce.getFpId());
    	
		return fp.viewFacilityCemComLimit(facilityCemComLimit);
	}
	
	public void showAuditLimitTrendReport() {
		FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
    	fp.setFacilityId(facilityId);
    	fp.setFacility(this.facility);
    	fp.setFpId(fce.getFpId());
    	fp.showAuditLimitTrendReport();
	}
	
	public void showPeriodicLimitTrendReport() {
		FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
    	fp.setFacilityId(facilityId);
    	fp.setFacility(this.facility);
    	fp.setFpId(fce.getFpId());
    	fp.showPeriodicLimitTrendReport();
	}
	
	public String viewContinuousMonitor() {
		FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
		ContinuousMonitorDetail cm = (ContinuousMonitorDetail) FacesUtil.getManagedBean("continuousMonitorDetail"); 
    	fp.setFacilityId(facilityId);
    	fp.setFacility(this.facility);
    	fp.setFpId(fce.getFpId());
    	fp.submitProfile();
    	return cm.submit();
	}
	
	public List<FceEmissionsInventoryLineItem> getEmissionsInventoryList() {
		return emissionsInventoryList;
	}

	public void setEmissionsInventoryList(List<FceEmissionsInventoryLineItem> emissionsInventoryList) {
		this.emissionsInventoryList = emissionsInventoryList;
	}

	public TableSorter getEmissionsInventoryWrapper() {
		return emissionsInventoryWrapper;
	}

	public void setEmissionsInventoryWrapper(TableSorter emissionsInventoryWrapper) {
		this.emissionsInventoryWrapper = emissionsInventoryWrapper;
	}
	
	public boolean isHasPreservedEI() {
		return hasPreservedEI;
	}

	public void setHasPreservedEI(boolean hasPreservedEI) {
		this.hasPreservedEI = hasPreservedEI;
	}

	public boolean isDateRangeChangeEI() {
		return dateRangeChangeEI;
	}

	public void setDateRangeChangeEI(boolean dateRangeChangeEI) {
		this.dateRangeChangeEI = dateRangeChangeEI;
	}

	
	public void eiDateRangeChanged(ValueChangeEvent event){
		setDateRangeChangeEI(true);
	}
	

	public void searchEI() {
		if (fce.getFcePreData().getDateRangeEI().getStartDt() == null) {
			DisplayUtil.displayError("Emissions Inventory: Start Date is empty.");
			return;
		}
		if (fce.getFcePreData().getDateRangeEI().getEndDt() != null) {
			if (fce.getFcePreData().getDateRangeEI().getStartDt()
					.after(fce.getFcePreData().getDateRangeEI().getEndDt())) {
				DisplayUtil.displayError("Emissions Inventory: Start Date is after End Date.");
				return;
			}
		}
		try {
			emissionsInventoryList = getFullComplianceEvalService().retrieveFceEmissionsInventoryBySearch(fce);
			emissionsInventoryWrapper = new TableSorter();
			emissionsInventoryWrapper.setWrappedData(emissionsInventoryList);
			setDateRangeChangeEI(false);
			String message = getSnapshotSearchSuccessMessage(EI_SNAPSHOT_DESC, false);
			if (emissionsInventoryList.size() == 0) {
				message = getSnapshotSearchSuccessMessage(EI_SNAPSHOT_DESC, true);
			}
			DisplayUtil.displayInfo(message);
		} catch (DAOException e) {
			logger.error("Could not search emissions inventories for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotSearchErrorMessage(EI_SNAPSHOT_DESC));
			handleBadDetailAndRedirect();
		}
	}
	
	public String resetEI() {
		Timestamp referenceReviewStartDate = fce.getReferenceReviewStartDate();
		fce.getFcePreData().getDateRangeEI().setStartDt(referenceReviewStartDate);
		fce.getFcePreData().getDateRangeEI().setEndDt(null);
		setDateRangeChangeEI(true);
		emissionsInventoryList = new ArrayList<FceEmissionsInventoryLineItem>();
		emissionsInventoryWrapper = new TableSorter();
		DisplayUtil.displayInfo(getSnapshotResetMessage(EI_SNAPSHOT_DESC));
		return PRE_INSPECTION_DATA_MENU;
	}

	public void setPreservedListEI() {
		try {
			HashSet<Integer> emissionsRptIds = new HashSet<Integer>();
			for (FceEmissionsInventoryLineItem item : emissionsInventoryList) {
				emissionsRptIds.add(item.getEmissionsRptId());
			}
			fce.getFcePreData().setEmissionsInventoryList(new ArrayList<Integer>(emissionsRptIds));
			if (fce.getFcePreData().getDateRangeEI().getStartDt() == null) {
				DisplayUtil.displayError("Emissions Inventory: Start Date is empty.");
			}
			if (fce.getFcePreData().getDateRangeEI().getEndDt() == null) {
				fce.getFcePreData().getDateRangeEI()
						.setEndDt(((InfrastructureDefs) FacesUtil.getManagedBean("infraDefs")).getTodaysDate());
			}
			// set Preserved List
			getFullComplianceEvalService().updateFceEmissionsInventoriesPreserved(fce);
			hasPreservedEI = true;
			setEmissionsInventoryWrapperFromPreservedList();
			DisplayUtil.displayInfo(getSnapshotPreservedMessage(EI_SNAPSHOT_DESC, true));
		} catch (RemoteException e) {
			logger.error("Could not preserve Emissions Inventory list for " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotPreservedMessage(EI_SNAPSHOT_DESC, false));
			handleException(e);
		}
	}
	
	public void recallPreservedListEI() {
		try {
			setEmissionsInventoryWrapperFromPreservedList();
			setDateRangeChangeEI(false);
			DisplayUtil.displayInfo(getSnapshotRecalledMessage(EI_SNAPSHOT_DESC, true));
		} catch (DAOException e) {
			logger.error("Could not recall preserved Emissions Inventory list for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotRecalledMessage(EI_SNAPSHOT_DESC, false));
			handleBadDetailAndRedirect();
		}
	}

	private void setEmissionsInventoryWrapperFromPreservedList() throws DAOException {
		// Date Range & List
		emissionsInventoryList = getFullComplianceEvalService().retrieveFceEmissionsInventoriesPreserved(fce);
		emissionsInventoryWrapper = new TableSorter();
		emissionsInventoryWrapper.setWrappedData(emissionsInventoryList);
	}
	
	public void clearPreservedListEI(){
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		
		ConfirmWindow cw = (ConfirmWindow) FacesUtil.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO) || fce == null) {
			clearButtonClicked();
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			return;
		}
		
		try{
			getFullComplianceEvalService().clearFceEmissionsInventoriesPreserved(fce); // Date Range & List
			hasPreservedEI = false;
			resetEI();
			setSnaphotSearchDatesReadOnly(isInspectionReadOnly());
			DisplayUtil.displayInfo(getSnapshotClearedMessage(EI_SNAPSHOT_DESC, true));
		} catch(DAOException e) {
			logger.error("Could not clear Emissions Inventory for inspection: " + fce.getInspId());
			DisplayUtil.displayError(getSnapshotClearedMessage(EI_SNAPSHOT_DESC, false));
			handleException(e);
		} finally{
	        clearButtonClicked();
		}
	}

	private String getInspectionInfoDocumentPath() {
				
		return File.separator 
				+ "tmp" 
				+ File.separator 
				+ this.fce.getInspId() 
				+ "_Inspection_Info"
				+ ".docx";
	}
	

	private String getSnapshotPreservedMessage(String snapshotName, boolean isSuccess) {
		if (isSuccess)
			return "Successfully created the preserved list for " + snapshotName + " snapshot.";
		else
			return "Failed to preserve " + snapshotName + " list.";
	}

	private String getSnapshotClearedMessage(String snapshotName, boolean isSuccess) {
		if (isSuccess)
			return "Successfully cleared the preserved " + snapshotName + " list.";
		else
			return "Failed to clear the preserved " + snapshotName + " list.";
	}

	private String getSnapshotRecalledMessage(String snapshotName, boolean isSuccess) {
		if (isSuccess)
			return "Successfully recalled the preserved " + snapshotName + " list.";
		else
			return "Failed to recall the preserved " + snapshotName + " list.";
	}

	private String getSnapshotSearchSuccessMessage(String snapshotName, boolean isNoResults) {
		if (!isNoResults)
			return "Successfully retrieved " + snapshotName + " list.";
		else
			return "There are no " + snapshotName + " that match the search date range. ";
	}
	
	private String getSnapshotSearchErrorMessage(String snapshotName) {
		return "Failed to search " + snapshotName + " for Inspection " + fce.getInspId();
	}
	
	private String getSnapshotResetMessage(String snapshotName) {
		return "Successfully reset the " + snapshotName + " snapshot.";
	}
	
    public void cancellation() {
    	
        try {
        	submitInternal();
        	fce.setInspectionReportStateCd(FCEInspectionReportStateDef.DEAD_ENDED);
            getFullComplianceEvalService().modifyFce(fce);
            DisplayUtil.displayInfo("Inspection updated successfully.");
            setEditable(false);
        } catch (RemoteException e) {
        	handleException(e);
            return;
        } finally {
        	try {
				fce = getFullComplianceEvalService().retrieveFce(facilityId, fce.getId());
			} catch (DAOException e) {
				logger.error("Could not retrieve inspection : " + fce.getInspId());
				DisplayUtil.displayError("Failed to update inspection "
						+ fce.getInspId()
						+ ". The inspection may no longer exist.");
				handleBadDetailAndRedirect();
			} catch (RemoteException e) {
				logger.error("Could not retrieve inspection : " + fce.getInspId());
				DisplayUtil.displayError("Failed to update inspection "
						+ fce.getInspId()
						+ ". The inspection may no longer exist.");
				handleBadDetailAndRedirect();
			}
        }
        
    }


/*3 level menu buttons - disabled (readOnly)
 *                   AdminPriv      GeneralUser     ReadOnlyUser
 * Initial             true           true            true 
 * Preparing		   false 		  false			  true
 * Complete		       false 		  false			  true
 * Finalize			   false 		  true			  true
 * 
 */
	
	public boolean isInspectionReadOnly() {
		boolean readOnly = true;
		if (fce.isReportStatePrepare() || fce.isReportStateComplete()) {
			readOnly = false;
		}
		if (isPrivledged()) {
			readOnly = false;
		}
		if (fce.isReportStateInitial()) {
			readOnly = true;
		}
		if (isReadOnlyUser()) {
			readOnly = true;
		}
		
		if (fce.isReportStateDeadEnded() || fce.isLegacyInspection() || fce.isPre10Legacy()){
			readOnly = true;
		}
		return readOnly;
	}
	
	public String getInspectionReadOnlyStatement() {
		String s = "<b>** The Inspection must be in the &quot;Preparing Inspection Report&quot; or &quot;Inspection Report Completed&quot; state to edit the contents of this page **</b>";
		return s;
	}

	/*fceDetail: edit, add/delete attachment - disabled (readOnly)
	 * Note is editable
	 *                   AdminPriv      GeneralUser     ReadOnlyUser
	 * Initial             false          false           true 
	 * Preparing		   false 		  false			  true
	 * Complete		       false 		  false  		  true
	 * Finalize			   false 		  true			  true
	 * 
	 */	
	public boolean isInspectionDetailLocked() {
		boolean locked = true;
		
		
		if (fce != null) {
			if (fce.isReportStateInitial() || fce.isReportStatePrepare() || fce.isReportStateComplete()) {
				locked = false;
			}
		}

		if (isPrivledged()) {
			locked = false;
		}

		if (isReadOnlyUser()) {
			locked = true;
		}

		return locked;
	}
	
	public boolean isEnhancedNonLegacyInspection() {
		return !fce.isLegacyInspection() && !fce.isPre10Legacy();
	}
	
	public String currentDateTime() {
		
		DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyMMddHHmmss");
		LocalDateTime now = LocalDateTime.now();
		String currentDateTime = pattern.format(now);
		
		return currentDateTime;
	}
	
	private String getInspectionReportPath() {
		
		return File.separator 
				+ "tmp" 
				+ File.separator 
				+ this.fce.getInspId() 
				+ "-"
				+ this.currentDateTime()
				+ ".docx";
	}
	
	public String getInspectionReportURL() {
		
		String reportUrl = null;
		
		if (DocumentUtil.isFileExists(currentInspRptPath)) {
			reportUrl = DocumentUtil.getFileStoreBaseURL() + currentInspRptPath;
		} else {
			logger.error("Inspection Report not found " + currentInspRptPath);
			DisplayUtil.displayError("ERROR: Inspection Report not found.");
		}
		
		return reportUrl;
	}
	
	
	public boolean isSelectAllInspReportSections() {
		return selectAllInspReportSections;
	}

	public void setSelectAllInspReportSections(boolean selectAllInspReportSections) {
		this.selectAllInspReportSections = selectAllInspReportSections;
	}

	
	public String getSelectAllJs() {
		
		String js = "";
		
		if (isSelectAllInspReportSections()) {
			
			js = "_uixt_inspRpt_inspRptTable.multiSelect(true);";
			
			setSelectAllInspReportSections(false);;
		}
		
		return js;
	}
	
	public void closeInspectionReportGenPopup(ReturnEvent re) {
		cancelInspectionReportGeneration();
	}

	public boolean isSnaphotSearchDatesReadOnly() {
		return snaphotSearchDatesReadOnly;
	}

	public void setSnaphotSearchDatesReadOnly(boolean snaphotSearchDatesReadOnly) {
		this.snaphotSearchDatesReadOnly = snaphotSearchDatesReadOnly;
	}

	
	public void associateRptWithCurrentFacility() {

		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
			return;
		}

		try {
			//!!!let change the facilityId to a non-existing facilityId to see whether it will return null
			Facility currentFacility = getFacilityService().retrieveFacility(facilityId, false);
			if (null != currentFacility) {
				Integer fpId = currentFacility.getFpId();
				Integer lastModified = currentFacility.getLastModified();
				// update only if the report is not already associated with the
				// current version of the facility inventory
				if (!fce.getFpId().equals(fpId) || !facility.getLastModified().equals(lastModified)) {				
					logger.debug("Associating inspection report with current facility version (fpId = " + fpId + ")");

					getFullComplianceEvalService().associateRptWithCurrentFacility(fce, currentFacility, InfrastructureDefs.getCurrentUserId());					

					DisplayUtil.displayInfo("Inspection report is now associated with the current version of the facility inventory");
				} else {
					DisplayUtil.displayInfo("Inspection report is already associated with the current version of the facility inventory");
				}
			} else {
				// should not happen
				DisplayUtil.displayError("Failed to retrieve facility associated with this inspection report");
				throw new DAOException("Failed to retrieve facility " + facilityId);
			}
		} catch (RemoteException e) {
			DisplayUtil.displayError("Failed to assoicate the Inspection report with the current version of the facility inventory");
			handleException(e);
		} finally {
			try{
				// retrieve the update report
				fce = getFullComplianceEvalService().retrieveFce(facilityId, fce.getId());
				boolean ok = false;
				// update facility
				if (fce != null) {
					facilityId = fce.getFacilityId();

					initializeAttachmentBean();
					ok = getAssociatedFacility();
					if (!ok) {
						blankOutPage = true;
						DisplayUtil.displayError("Facility " + facilityId + " for Inspection " + fceId + " not found.");
					}

				} else {
					blankOutPage = true;
					DisplayUtil.displayError("Inspection is not found with that number.");
				}

			} catch (RemoteException e) {
				handleException("Failed to retrieve Inspection " + fceId, e);
			}
		}
	}
	
	public String getTemplateDocTypeCd() {
		return templateDocTypeCd;
	}

	public void setTemplateDocTypeCd(String templateDocTypeCd) {
		this.templateDocTypeCd = templateDocTypeCd;
	}

	public String getDocTypeCd() {
		return docTypeCd;
	}

	public void setDocTypeCd(String docTypeCd) {
		this.docTypeCd = docTypeCd;
	}
	
	public String getGeneratedDocumentFilePath() {
		return generatedDocumentFilePath;
	}

	public void setGeneratedDocumentFilePath(String generatedDocumentFilePath) {
		this.generatedDocumentFilePath = generatedDocumentFilePath;
	}

	public String generateDocumentFromTemplate() {
		String ret = null;

		Document template = TemplateDocTypeDef.getTemplateAsAsposeDocument(this.templateDocTypeCd);

		if (null != template) {

			try {

				// first generate the facility information
				DocumentGenerator docGenerator = new FacilityDocumentGenerator();
				Document facilityInfoDocument = docGenerator.generateDocument(this.fce.getFpId(), template);

				// next generate the inspection information
				docGenerator = new InspectionDocumentGenerator();
				Document inspectionInfoDocument = docGenerator.generateDocument(this.fceId, facilityInfoDocument);

				// save the generated document under tmp path
				this.generatedDocumentFilePath = File.separator 
									+ "tmp" 
									+ File.separator 
									+ this.fce.getInspId() 
									+ "_"
									+ TemplateDocTypeDef.getData().getItems().getItemDesc(this.templateDocTypeCd) 
									+ ".docx";
				
				boolean saved = DocumentUtil.saveDocument(inspectionInfoDocument, this.generatedDocumentFilePath);
				if (!saved) {
					this.generatedDocumentFilePath = null;
					DisplayUtil.displayError(
							"ERROR: An error occured while saving the generated document. Please contact the system administrator");
				} else {
					// navigate to the document download popup
					ret = "dialog:downloadGeneratedDocument";
				}

			} catch (DocumentGenerationException dge) {
				this.generatedDocumentFilePath = null;
				DisplayUtil.displayError("An error occured when generating the document from the template.");
				logger.error(dge.getMessage(), dge);
			}
		} else {
			DisplayUtil.displayError(
					"An error occured when trying to read the template. Check if the template exists and the template code is valid.");
		}

		return ret;
	}

	/*
	 * For each item in the inspection attachment type definition list, if the
	 * attachment type has document template associated with it, then check if 
	 * that attachment type is present in the inspection attachments table. 
	 * If that attachment type is not present, then insert a dummy row in the
	 * inspection attachments table with attachment type set to the missing
	 * attachment type. This will allow the system to always display a row in 
	 * the attachments table for those attachment types that allow document
	 * generation.
	 * 
	 * See TFS task 7707.
	 *  
	 */
	private List<FceAttachment> getAttachmentList() {

		List<FceAttachment> attachmentList = new ArrayList<FceAttachment>();

		List<FceAttachment> fceAttachments = this.fce.getAttachments();

		for (SelectItem si : FceAttachmentTypeDef.getDocumentTemplateData().getItems().getCurrentItems()) {

			String templateDocTypeCd = si.getLabel();

			if (!Utility.isNullOrEmpty(si.getLabel())) {

				String attachmentTypeCd = (String) si.getValue();

				boolean found = false;

				for (FceAttachment attachment : fceAttachments) {

					if (attachment.getDocTypeCd().equalsIgnoreCase(attachmentTypeCd)) {
						found = true;
						break;
					}
				}

				if (!found) {

					FceAttachment fceAttachment = new FceAttachment();
					fceAttachment.setTemplateDocTypeCd(templateDocTypeCd);
					fceAttachment.setDocTypeCd(attachmentTypeCd);
					attachmentList.add(fceAttachment);
				}
			}
		}

		attachmentList.addAll(fceAttachments);

		return attachmentList;

	}

	/*
	 * Wrapper for startNewAttachment method in the Attachments bean.
	 * This method is called from the fceAttachments.jsp upon clicking on
	 * the Upload button. When the upload button is clicked, the docTypeCd
	 * is set via jsp i.e., the attachment type for which the upload button
	 * has been clicked. This is done in order to pre-select the attachment
	 * type in the editDocAttachments.jsp
	 */
	public String startNewAttachment() {

		String ret = null;

		Attachments attachments = (Attachments) FacesUtil.getManagedBean("attachments");

		ret = attachments.startNewAttachment();

		attachments.getDocument().setDocTypeCd(docTypeCd);

		return ret;

	}

	public String getGeneratedDocumentURL() {
		
		String docUrl = null;

		if (DocumentUtil.isFileExists(this.generatedDocumentFilePath)) {

			docUrl = DocumentUtil.getFileStoreBaseURL() + this.generatedDocumentFilePath;
		} else {
			logger.error("File not found " + this.generatedDocumentFilePath);
			DisplayUtil.displayError("ERROR: Document not found");
		}

		return docUrl;
	}
	
	public boolean getAllowedToGenerateInspectionReport() {
		
		boolean allowed = true;
		
		allowed = !(isReadOnlyUser()
					|| fce.isReportStateInitial() 
					|| fce.isReportStateFinal() 
					|| fce.isReportStateDeadEnded() 
					|| fce.isLegacyInspection() 
					|| fce.isPre10Legacy());
		
		// Special case ... do not disable the Generate Inspection Report buttons for
		// Admin if the inspection has been finalized.  Override the previous setting.
		if (isStars2Admin() && fce.isReportStateFinal()) {
			allowed = true;
		}
		
		return allowed;	
	}

	public String confirmPermitSelection() {
		if (corrPermitIdsToAssociate.isEmpty() && corrPermitIdsToDisassociate.isEmpty()) {
			setPermitSelectionEditable(false);
			return null;
		}

		return "dialog:confirmPermitSelection";
	}
}
