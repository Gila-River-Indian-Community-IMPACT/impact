package us.oh.state.epa.stars2.webcommon.correspondence;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.CorrespondenceService;
import us.oh.state.epa.stars2.bo.DocumentService;
import us.oh.state.epa.stars2.bo.EnforcementActionService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.def.CorrespondenceDirectionDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

public class CreateCorrespondence extends AppBase {
	
	private static final long serialVersionUID = 4826679902669627214L;

	private Correspondence correspondence;
	private String pageRedirect;
	private List<EnforcementAction> linkedToObjs;
	private String linkedToObj;
	private CorrespondenceService correspondenceService;
	private DocumentService documentService;
	private EnforcementActionService enforcementActionService;
	private FacilityService facilityService;
	private String facilityId;
	private Integer linkedToId; 
	public static String CREATE_OUTCOME = "correspondence.create";
	
    private String associatedWithFacility;
    private boolean allowedToChangeFacility;
    
	public EnforcementActionService getEnforcementActionService() {
		return enforcementActionService;
	}

	public void setEnforcementActionService(EnforcementActionService enforcementActionService) {
		this.enforcementActionService = enforcementActionService;
	}

	public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public CorrespondenceService getCorrespondenceService() {
		return correspondenceService;
	}

	public void setCorrespondenceService(
			CorrespondenceService correspondenceService) {
		this.correspondenceService = correspondenceService;
	}

	public CreateCorrespondence() {
		super();
	}

	public Correspondence getCorrespondence() {
		return correspondence;
	}

	public void setCorrespondence(Correspondence correspondence) {
		this.correspondence = correspondence;
	}


	public Integer getEnforcementActionId() {
		if (linkedToObj == null || linkedToObj.equals("")) {
			return 0;
		} else {
			return new Integer(linkedToObj);
		}
	}

	public void setEnforcementActionId(Integer enforcementId) {
		if (enforcementId == null || enforcementId.equals(0)) {
			linkedToObj = "";
		} else {
			linkedToObj = enforcementId.toString();
		}
		correspondence.setLinkedToId(enforcementId);
	}

	public String getLinkedToObj() {
		return linkedToObj;
	}

	public void setLinkedToObj(String linkedToObj) {
		this.linkedToObj = linkedToObj;
	}

	public List<EnforcementAction> getLinkedToObjs() {
		return linkedToObjs;
	}

	public void setLinkedToObjs(List<EnforcementAction> linkedToObjs) {
		this.linkedToObjs = linkedToObjs;
	}

	public String getCorrespondenceTypeCode() {
		if (correspondence != null) {
			return correspondence.getCorrespondenceTypeCode();
		} else {
			return "";
		}
	}

	public void setCorrespondenceTypeCode(String correspondenceTypeCode) {
		if (correspondence == null) {
			return;
		}
		correspondence.setCorrespondenceTypeCode(correspondenceTypeCode);
	}
	
	public Integer getLinkedToId() {
		return linkedToId;
	}

	public void setLinkedToId(Integer linkedToId) {
		this.linkedToId = linkedToId;
	}

	public final boolean isShowLinkedToObj() {
		boolean ret = false;
		if (correspondence != null
				&& "enf".equals(correspondence.getLinkedToTypeCd())) {
			ret = true;
		}
		return ret;
	}

	public final String createCorrespondence() {
		
		String ret = null;
		boolean ok = true;
		
		 // Protect from multiple clicks.
		if (!firstButtonClick()) {
            return null;
        }
        
		try {
			cleanOrphanData();
			
			String errorClientIdPrefix = "createCorrespondence:";
			ValidationMessage[] validationMessages = correspondence.validate();
			ArrayList<ValidationMessage> msgArr = new ArrayList<ValidationMessage>(new ArrayList<>(Arrays.asList(validationMessages)));
			if (Utility.isNullOrEmpty(associatedWithFacility)) {
				ValidationMessage valMsg = new ValidationMessage("associatedWithFacility", "Associate a facility.", ValidationMessage.Severity.ERROR);
				msgArr.add(valMsg);
			} else if (associatedWithFacility == "Y" && correspondence.getFacilityID() == null) {
				ValidationMessage valMsg = new ValidationMessage("associatedWithFacility", "Associate a facility.", ValidationMessage.Severity.ERROR);
				msgArr.add(valMsg);				
			}
			
			if (msgArr != null && !msgArr.isEmpty()) {
				validationMessages = msgArr.toArray(new ValidationMessage[msgArr.size()]);
				displayValidationMessages(errorClientIdPrefix, validationMessages);
				ok = false;
			}
			
			if (ok) {
				// if correspondence is associated with a facility then update the district information
				if (associatedWithFacility == "Y" && correspondence.getFacilityID() != null) {
					correspondence.setDistrict(facilityService.retrieveFacility(correspondence.getFacilityID()).getDoLaaCd());
				}
				correspondence = getCorrespondenceService()
						.createCorrespondence(correspondence);

				if (correspondence.getCorrespondenceID() == null) {
					DisplayUtil.displayError("Failed to create Correspondence");
				} else {
					DisplayUtil.displayInfo("Correspondence created");
				}
				
			}
		} catch (Exception ex) {
			DisplayUtil.displayError("Failed to create Correspondence ");
			logger.error(ex.getMessage(), ex);
			ok = false;
		} finally {
			clearButtonClicked();
		}

		if (ok) {
			CorrespondenceDetail corrDetail = (CorrespondenceDetail) FacesUtil
					.getManagedBean("correspondenceDetail");
			corrDetail
					.setCorrespondenceID(correspondence.getCorrespondenceID());
			ret = corrDetail.refreshCorrespondenceDetail();
		}

		return ret;
	}

	public String refreshCreateCorrespondence() {
		
		this.correspondence = new Correspondence();
        associatedWithFacility = null;
        allowedToChangeFacility = false;
        
        if (facilityId != null) {
        	associatedWithFacility = "Y";
        	allowedToChangeFacility = true;
        }
        
		// may be created from facility (facilityId != null)
		this.correspondence.setFacilityID(facilityId);
		facilityId = null;

		return CREATE_OUTCOME;
	}

	public String getPageRedirect() {
		if (pageRedirect != null) {
			FacesUtil.setOutcome(null, pageRedirect);
			pageRedirect = null;
		}
		return null;
	}

	public void setPageRedirect(String pageRedirect) {
		this.pageRedirect = pageRedirect;
	}

	public boolean isAllowFileChange() {
		boolean rtn = true;
		return rtn;
	}

	public Timestamp getMaxDate() {
		return new Timestamp(System.currentTimeMillis());
	}

	public Timestamp getMinFollowUpDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Timestamp tomorrow = new Timestamp(cal.getTimeInMillis());
		return tomorrow;
	}

	private int getUserID() {
		return InfrastructureDefs.getCurrentUserId();
	}

	private void cleanOrphanData() {

		if (!correspondence.isFollowUpAction()) {
			correspondence.setFollowUpActionDate(null);
			correspondence.setFollowUpActionDescription(null);
		}

		if (associatedWithFacility == "Y") {
			correspondence.setCountyCd(null);
			correspondence.setCityCd(null);
		} else if (associatedWithFacility == "N") {
			correspondence.setFacilityID(null);
		}

		if (!correspondence.isLinkedtoEnfAction()) {
			correspondence.setLinkedToId(null);
			// correspondence.setHideAttachments(false);
			// DO NOT reset, in case correspondence is ever re-linked to an
			// enforcement action. Require EA admin to manually set it to false.
			// This is the more conservative approach.
		}

	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String reset() {
		if (correspondence != null) {
			setFacilityId(correspondence.getFacilityID());
		}

        associatedWithFacility = null;
        allowedToChangeFacility = false;
		correspondence = new Correspondence();

		return null;
	}
	
	public String getAssociatedWithFacility() {
		
		if (!Utility.isNullOrEmpty(correspondence.getFacilityID()) && !isAllowedToChangeFacilityId()) {
			associatedWithFacility = "Y";
		} else if (!Utility.isNullOrEmpty(correspondence.getDistrict()) && !isAllowedToChangeFacilityId()) {
			associatedWithFacility = "N";
		}
		return associatedWithFacility;
	}

	public void setAssociatedWithFacility(String associatedWithFacility) {
		
		if (AbstractDAO.translateIndicatorToBoolean(associatedWithFacility)) {
			setAllowedToChangeFacility(true);
		} else {
			correspondence.setFacilityID(null);
			setAllowedToChangeFacility(false);
		}
		
		this.associatedWithFacility = associatedWithFacility;
	}
	
	public boolean isAssociated() {
		boolean ret = false;
		
		ret = AbstractDAO.translateIndicatorToBoolean(getAssociatedWithFacility());
		
		return ret;
	}
	
	public boolean isAllowedToChangeFacilityId() {
		return allowedToChangeFacility;
	}
	
	public void setAllowedToChangeFacility(boolean allowedToChangeFacility) {
		this.allowedToChangeFacility = allowedToChangeFacility;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}
	
	public List<SelectItem> getFacilityEnforcementAction() {
		
		List<SelectItem> si = new ArrayList<SelectItem>();
		String idTemplate = "F000000";
		// Facility id as entered by the user on the UI.
		String inputFacilityId = getCorrespondence().getFacilityID(); 
		if (null != inputFacilityId && inputFacilityId.length() <= idTemplate.length()) {
			// Sometimes the user may specify just the numeric part of the facility id, so
			// convert the user entered facility id to FXXXXXX format before doing the search.
			String facilityId = idTemplate.substring(0, idTemplate.length() - inputFacilityId.length()) + inputFacilityId;
			if (null != facilityId) {
				try {
					EnforcementAction[] eaList = enforcementActionService.retrieveEnforcementActionByFacilityId(facilityId);
					for (EnforcementAction ea : eaList) {
						si.add(new SelectItem(ea.getEnforcementActionId(), ea.getEnfId()));
					}
				} catch(DAOException daoe) {
					handleException(daoe);
				}
			}
		}
		return si;
	}
	
	public String createCorrespondenceFromEA() {
		this.correspondence = new Correspondence();
        associatedWithFacility = null;
        allowedToChangeFacility = false;
        
        if (facilityId != null) {
        	associatedWithFacility="Y";
        	allowedToChangeFacility = true;
        	this.correspondence.setFacilityID(facilityId);
        }
		
		if (null != linkedToId) {
			this.correspondence.setLinkedtoEnfAction(true);
			this.correspondence.setLinkedToId(linkedToId);
		}
		
		// set correspondence direction to outgoing
		this.correspondence.setDirectionCd(CorrespondenceDirectionDef.OUTGOING);
		
		// set facility id to null so that clicking on the create correspondence menu the next time
		// the facility information is not automatically pre-populated with old data - TFS Task 5236
		setFacilityId(null);
		
		return CREATE_OUTCOME;
	}
	
	public final void setDefaultDistrict(ValueChangeEvent ve){
	    if (ve.getNewValue()!= null && ve.getNewValue().equals("N") && SystemPropertyDef.getSystemPropertyValueAsBoolean("hideDistrict", false)) {
	    	correspondence.setDistrict(SystemPropertyDef.getSystemPropertyValue("districtCd",null));
	    }
	}
	
}
