package us.oh.state.epa.stars2.portal.permit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.app.workflow.WorkFlow2DDraw;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dao.permit.PermitSQLDAO;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.util.Pair;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;

/**
 * @author Pyeh
 * 
 */
@SuppressWarnings("serial")
public class PermitSearch extends AppBase {
    public static int maxNumberOfHits = 1000; // TODO maxNumberOfHits
    private String legacyPermitNumber;
    private String permitNumber;
    private String facilityID;
    private String facilityName;
    private String permitType;
    private String permitReason;
    private String permitGlobalStatusCd;
    private String dateBy;
    private Timestamp beginDt;
    private Timestamp endDt;
    private String permitEUStatusCd;
    private String fromFacility = "false";
    private boolean hasSearchResults;
    private String applicationNumber;
    private Pair<Class<? extends Permit>, Boolean> newPermitType;
    private LinkedHashMap<String, String> permitStatus;
    private LinkedHashMap<String, String> permitDateBy;
    private List<Permit> permitsNSR;
    private List<Permit> permitsTV;

    private boolean fromEUPermitHistory = false;
	private PermitService permitService;
	
	private String selectedPermitNumber;

	public PermitService getPermitService() {
		return permitService;
	}

	public void setPermitService(PermitService permitService) {
		this.permitService = permitService;
	}

    public final String getFromFacility() {
        return fromFacility;
    }

    public final void setFromFacility(String fromFacility) {
        this.fromFacility = fromFacility;
    }

    /**
     * @return
     */
    public final String getLegacyPermitNumber() {
        return legacyPermitNumber;
    }

    /**
     * @param permitNumber
     */
    public final void setLegacyPermitNumber(String legacyPermitNumber) {
        if (legacyPermitNumber != null && legacyPermitNumber.equals("")) {
        	legacyPermitNumber = null;
        }
        this.legacyPermitNumber = legacyPermitNumber;
    }

    /**
     * @return
     */
    public final String getPermitNumber() {
        return permitNumber;
    }

    /**
     * @param permitNumber
     */
    public final void setPermitNumber(String permitNumber) {
        if (permitNumber != null && permitNumber.equals("")) {
            permitNumber = null;
        }
        this.permitNumber = permitNumber;
    }

    /**
     * @return
     */
    public final String getFacilityID() {
        return facilityID;
    }

    /**
     * @param facilityID
     */
    public final void setFacilityID(String facilityID) {
        if (facilityID != null && facilityID.equals("")) {
            facilityID = null;
        }
        this.facilityID = facilityID;
    }

    /**
     * @return
     */
    public final String getFacilityName() {
        return facilityName;
    }

    /**
     * @param facilityName
     */
    public final void setFacilityName(String facilityName) {
        if (facilityName != null && facilityName.equals("")) {
            facilityName = null;
        }
        this.facilityName = facilityName;
    }

    /**
     * @return
     */
    public final boolean getHasSearchResults() {
        return hasSearchResults;
    }

    /**
     * @return
     */
    public final Pair<Class<? extends Permit>, Boolean> getNewPermitType() {
        return newPermitType;
    }

    /**
     * @param newPermitType
     */
    public final void setNewPermitType(Pair<Class<? extends Permit>, Boolean> newPermitType) {
        this.newPermitType = newPermitType;
    }

    /**
     * @param applicationNumber
     */
    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    /**
     * @return
     */
    public final String getApplicationNumber() {
        return applicationNumber;
    }

    /**
     * @return
     */
    public final String getPermitType() {
        return permitType;
    }

    /**
     * @param permitType
     */
    public final void setPermitType(String permitType) {
        this.permitType = permitType;
    }

    /**
     * @return
     */
    public final String getPermitGlobalStatusCd() {
        return permitGlobalStatusCd;
    }

    /**
     * @param permitStatusCd
     */
    public final void setPermitGlobalStatusCd(String permitGlobalStatusCd) {
        this.permitGlobalStatusCd = permitGlobalStatusCd;
    }

    /**
     * @return
     */
    public final LinkedHashMap<String, String> getPermitStatus() {
        return permitStatus;
    }

    /**
     * @param permitStatus
     */
    public final void setPermitStatus(LinkedHashMap<String, String> permitStatus) {
        this.permitStatus = permitStatus;
    }

    /**
     * @return
     */
    public final String getPermitReason() {
        return permitReason;
    }

    /**
     * @param permitReason
     */
    public final void setPermitReason(String permitReason) {
        this.permitReason = permitReason;
    }

    /*
     * Actions -------------------------------------------------------
     */
    /**
     * @return
     */
	public final String search() {
		Integer pId = -1;
		try {

			List<Permit> permitsNSRlocal;
			List<Permit> permitsTVlocal;
			permitsNSRlocal = getPermitService().search(applicationNumber, null, null, PermitTypeDef.NSR, permitReason,
					null, legacyPermitNumber, permitNumber, facilityID, facilityName, permitGlobalStatusCd, dateBy,
					beginDt, endDt, permitEUStatusCd, unlimitedResults(), null);
			DisplayUtil.displayHitLimit(permitsNSRlocal.size());
			hasSearchResults = true;
			permitsNSR = new ArrayList<Permit>();
			// retrieve complete permit for all issuance documents.
			int i = 0;
			for (Permit p : permitsNSRlocal) {
				pId = p.getPermitID();
				i++;
				if (pId == null) {
					logger.error("Search (" + errMsg() + ") returned null while working on list item " + i);
				} else {
					Permit pp = getPermitService().retrievePermitLight(pId);
					if (pp == null) {
						logger.error("Search (" + errMsg() + ") failed on retrievePermit() for pId " + pId);
					} else {
						permitsNSR.add(pp);
					}
				}
			}

			permitsTVlocal = getPermitService().search(applicationNumber, null, null, PermitTypeDef.TV_PTO,
					permitReason, null, legacyPermitNumber, permitNumber, facilityID, facilityName,
					permitGlobalStatusCd, dateBy, beginDt, endDt, permitEUStatusCd, unlimitedResults(), null);
			DisplayUtil.displayHitLimit(permitsTVlocal.size());
			hasSearchResults = true;
			permitsTV = new ArrayList<Permit>();
			// retrieve complete permit for all issuance documents.
			int j = 0;
			for (Permit p : permitsTVlocal) {
				pId = p.getPermitID();
				j++;
				if (pId == null) {
					logger.error("Search (" + errMsg() + ") returned null while working on list item " + j);
				} else {
					Permit pp = getPermitService().retrievePermitLight(pId);
					if (pp == null) {
						logger.error("Search (" + errMsg() + ") failed on retrievePermit() for pId " + pId);
					} else {
						permitsTV.add(pp);
					}
				}
			}

			fromEUPermitHistory = false;
			return "permitSearch";
		} catch (Exception ex) {
			DisplayUtil.displayError("search failed");
			logger.error("Search (" + errMsg() + ") failed on pId " + pId + "; " + ex.getMessage(), ex);
			return "failure";
		}
	}

    private String errMsg() {
        return "applicationNumber=" + applicationNumber + ", permitType=" + permitType + ", permitReason=" +
          permitReason + ", legacyPermitNumber=" + legacyPermitNumber + ", permitNumber=" + permitNumber + ", facilityID=" + facilityID +
          ", facilityName=" + facilityName + ", permitGlobalStatusCd=" + permitGlobalStatusCd +
          ", dateBy=" + dateBy + ", beginDt=" + beginDt + ", endDt=" + endDt +
          ", permitEUStatusCd=" + permitEUStatusCd;
    }

    /**
     * @return
     */
    public final String reset() {
        legacyPermitNumber = null;
        permitNumber = null;
        facilityID = null;
        facilityName = null;
        permitsNSR = null;
        permitsTV = null;
        permitType = null;
        permitReason = null;
        permitGlobalStatusCd = null;
        applicationNumber = null;
        dateBy = null;
        beginDt = null;
        endDt = null;

        hasSearchResults = false;
        return SUCCESS;
    }

    /**
     * @return
     */
    public final String createNewPermit() {
        if (newPermitType == null) {
            DisplayUtil.displayError("Please select a permit type");
            return null; // stay on same page
        }

        return "permitDetail";
    }

    /**
     * @return
     */
    public final String cancelNewPermit() {
        return "permitSearch";
    }

    /*
     * To minimize session size, to be used in the future...
     */
    /**
     * 
     * private void shrink() { if (hasSearchResults) { permits = null;
     * resultsWrapper.setWrappedData(permits); } }
     * 
     * private void restore() { if (hasSearchResults) { search(); } }
     */

    /**
     * @return
     */
    public final List<SelectItem> getPermitReasons() {
        return PermitReasonsDef.getPermitReasons(permitType, null);
    }

    /**
     * @return
     */
    public final List<SelectItem> getAllPermitReasons() {
        return PermitReasonsDef.getAllPermitReasons();
    }

    public final List<SelectItem> getPermitTypes() {
        return PermitTypeDef.getData().getItems().getItems(permitType, true);
    }

    public final LinkedHashMap<String, String> getPermitDateBy() {
        if (permitDateBy == null) {
            permitDateBy = new LinkedHashMap<String, String>();
            permitDateBy.put("Effective Date", PermitSQLDAO.EFFECTIVE_DATE);
            permitDateBy.put("Expiration Date", PermitSQLDAO.EXPIRATION_DATE);
            permitDateBy.put("Mod Effective Date",
                    PermitSQLDAO.MOD_EFFECTIVE_DATE);
        }
        return permitDateBy;
    }

    public final String getDateBy() {
        return dateBy;
    }

    public final void setDateBy(String dateBy) {
        this.dateBy = dateBy;
    }

    public final Timestamp getBeginDt() {
        return beginDt;
    }

    public final void setBeginDt(Timestamp beginDt) {
        this.beginDt = beginDt;
    }

    public final Timestamp getEndDt() {
        return endDt;
    }

    public final void setEndDt(Timestamp endDt) {
        this.endDt = endDt;
    }

    public final boolean isFromEUPermitHistory() {
        return fromEUPermitHistory;
    }

    public final void setFromEUPermitHistory(boolean fromEUPermitHistory) {
        this.fromEUPermitHistory = fromEUPermitHistory;
    }

	public final String getPermitEUStatusCd() {
		return permitEUStatusCd;
	}

	public final void setPermitEUStatusCd(String permitEUStatusCd) {
		this.permitEUStatusCd = permitEUStatusCd;
	}
	
	public String getSelectedPermitNumber() {
		return selectedPermitNumber;
	}

	public void setSelectedPermitNumber(String selectedPermitNumber) {
		this.selectedPermitNumber = selectedPermitNumber;
	}

	public final String loadWorkflowDiagram() {
		Integer processId = null;
		try {
			processId = getPermitService().retrievePermitWorkflowProcessId(getSelectedPermitNumber());

			if (processId != null) {
				WorkFlow2DDraw workFlowProfile = (WorkFlow2DDraw) FacesUtil.getManagedBean("workFlow2DDraw");
				workFlowProfile.setProcessId(processId);
			} else {
				DisplayUtil.displayInfo("No Workflow Diagram exists for permit " + selectedPermitNumber + ".");
				return "failure";
			}

		} catch (Exception ex) {
			DisplayUtil.displayError("search failed");
			logger.error("retrievePermitWorkflowProcessId (" + errMsg() + ") failed on getSelectedPermitNumber() "
					+ getSelectedPermitNumber() + "; " + ex.getMessage(), ex);
			return "failure";
		}
		return "Success";
	}

	public final List<Permit> getPermitsNSR() {
		return permitsNSR;
	}

	public void setPermitsNSR(List<Permit> permitsNSR) {
		this.permitsNSR = permitsNSR;
	}

	public final List<Permit> getPermitsTV() {
		return permitsTV;
	}

	public void setPermitsTV(List<Permit> permitsTV) {
		this.permitsTV = permitsTV;
	}
}
