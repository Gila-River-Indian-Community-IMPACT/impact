package us.wy.state.deq.impact.app.continuousMonitoring;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.bo.FacilityHistoryService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.wy.state.deq.impact.bo.ContinuousMonitorService;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor;

@SuppressWarnings("serial")
public class ContinuousMonitorSearch extends AppBase {
	private String facilityId;
	private String facilityName;
	private String cmpId;
	private String doLaaCd;
	private String countyCd;
	private Timestamp addDt;
	private Timestamp endDt;
	private Integer fpId; // to access the associated fpId for monitor

	private Integer continuousMonitorId;
	private String searchMonId;

	private List<ContinuousMonitor> continuousMonitors;
	private boolean hasSearchResults;
	private boolean fromFacility;
	private ContinuousMonitorSearch backup;
	private Facility facility;

	private ContinuousMonitorService continuousMonitorService;
	private FacilityHistoryService facilityHistoryService;
	private FacilityService facilityService;

	private String popupRedirectOutcome;

	public static final String SEARCH_OUTCOME = "continuousMonitors.search";

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public FacilityHistoryService getFacilityHistoryService() {
		return facilityHistoryService;
	}

	public void setFacilityHistoryService(
			FacilityHistoryService facilityHistoryService) {
		this.facilityHistoryService = facilityHistoryService;
	}

	public ContinuousMonitorService getContinuousMonitorService() {
		return continuousMonitorService;
	}

	public void setContinuousMonitorService(
			ContinuousMonitorService continuousMonitorService) {
		this.continuousMonitorService = continuousMonitorService;
	}

	public final String reset() {
		facilityId = null;
		facilityName = null;
		cmpId = null;
		doLaaCd = null;
		countyCd = null;
		hasSearchResults = false;
		addDt = null;
		endDt = null;
		searchMonId = null;
		return SUCCESS;
	}

	/**
	 * Invoked from Monitor search screen.
	 * 
	 * @return
	 */
	public final String search() {
		return search(true);  // If on portal, use staging schema.
	}
	public final String search(boolean staging) {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}

		if ((this.getAddDt() != null && this.getEndDt() != null)
				&& (!Utility.isNullOrEmpty(this.getAddDt().toString()) && !Utility
						.isNullOrEmpty(this.getEndDt().toString()))
				&& this.getAddDt().after(this.getEndDt())) {
			DisplayUtil.displayError("End Date must not be before Add Date");
			return null;
		}

		try {
			hasSearchResults = false;
			continuousMonitors = new ArrayList<ContinuousMonitor>();

			if (continuousMonitorId == null) {
				continuousMonitors = getContinuousMonitorService()
						.searchContinuousMonitors(fpId, facilityId, facilityName,
								doLaaCd, countyCd, addDt, endDt, searchMonId,
								cmpId, staging);

			} else {
				fromFacility = false;
				continuousMonitors = new ArrayList<ContinuousMonitor>();
				ContinuousMonitor cm = getContinuousMonitorService()
						.retrieveContinuousMonitor(continuousMonitorId, staging);
				if (cm != null) {
					continuousMonitors.add(cm);
				}
			}
			DisplayUtil.displayHitLimit(continuousMonitors.size());
			if (continuousMonitors.size() == 0) {
				if (fromFacility) {
					facility = getFacilityService()
							.retrieveFacility(facilityId, staging);
				} else {
					logger.debug("There are no monitors that match the search criteria.");
				}
			} else {
				if (fromFacility) {
					facility = getFacilityService()
							.retrieveFacility(facilityId, staging);
				}
				hasSearchResults = true;
			}
		} catch (RemoteException e) {
			handleException(e);
		} finally {
			clearButtonClicked();
		}
		return null;
	}

	public final void copy(ContinuousMonitorSearch es) {
		continuousMonitorId = es.continuousMonitorId;
		facilityId = es.facilityId;
		facilityName = es.facilityName;
		doLaaCd = es.doLaaCd;
		countyCd = es.countyCd;
		addDt = es.addDt;
		endDt = es.endDt;
		searchMonId = es.searchMonId;
	}

	/**
	 * This is the method called by the third-level menu of the facility
	 * inventory to display the monitor table.
	 * 
	 * @return
	 */

	public final String getFacilityId() {
		return facilityId;
	}

	public final void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public final String getFacilityName() {
		return facilityName;
	}

	public final void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public final String getDoLaaCd() {
		return doLaaCd;
	}

	public final void setDoLaaCd(String doLaaCd) {
		this.doLaaCd = doLaaCd;
	}

	public final String getCountyCd() {
		return countyCd;
	}

	public final void setCountyCd(String countyCd) {
		this.countyCd = countyCd;
	}

	public final Timestamp getAddDt() {
		return addDt;
	}

	public final void setAddDt(Timestamp addDt) {
		this.addDt = addDt;
	}

	public final Timestamp getEndDt() {
		return endDt;
	}

	public final void setEndDt(Timestamp endDt) {
		this.endDt = endDt;
	}

	public final Integer getContinuousMonitorId() {
		return continuousMonitorId;
	}

	public final void setContinuousMonitorId(Integer continuousMonitorId) {
		this.continuousMonitorId = continuousMonitorId;
	}

	public final String getSearchMonId() {
		return searchMonId;
	}

	public final void setSearchMonId(String searchMonId) {
		this.searchMonId = searchMonId;
	}

	public final List<ContinuousMonitor> getContinuousMonitors() {
		return continuousMonitors;
	}

	public final boolean isHasSearchResults() {
		return hasSearchResults;
	}

	public final boolean isFromFacility() {
		return fromFacility;
	}

	public final void setFromFacility(boolean fromFacility) {
		this.fromFacility = fromFacility;
	}

	public String getPopupRedirect() {
		if (popupRedirectOutcome != null) {
			FacesUtil.setOutcome(null, popupRedirectOutcome);
			popupRedirectOutcome = null;
		}
		return null;
	}

	public final void setPopupRedirectOutcome(String popupRedirectOutcome) {
		this.popupRedirectOutcome = popupRedirectOutcome;
	}

	public final Facility getFacility() {
		return facility;
	}

	public final void setFacility(Facility facility) {
		this.facility = facility;
	}

	public final void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}

	public final String getCmpId() {
		return cmpId;
	}

	public Integer getFpId() {
		return fpId;
	}

	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}
	
}
