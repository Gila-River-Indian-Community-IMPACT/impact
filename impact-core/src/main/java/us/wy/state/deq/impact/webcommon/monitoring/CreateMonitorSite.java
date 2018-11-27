package us.wy.state.deq.impact.webcommon.monitoring;

import us.oh.state.epa.stars2.bo.MonitoringService;
import us.oh.state.epa.stars2.def.MonitorStatusDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorSite;

@SuppressWarnings("serial")
public class CreateMonitorSite extends AppBase {

    private String pageRedirect;

    public static final String CREATE_OUTCOME = "monitoring.createMonitorSite";
    
    private MonitorSite monitorSite = new MonitorSite();
    
    private MonitorSiteDetail monitorSiteDetail;
    
    private MonitorGroupDetail monitorGroupDetail;

    private MonitoringService monitoringService;

	public MonitorSiteDetail getMonitorSiteDetail() {
		return monitorSiteDetail;
	}

	public void setMonitorSiteDetail(MonitorSiteDetail monitorSiteDetail) {
		this.monitorSiteDetail = monitorSiteDetail;
	}

	public MonitorGroupDetail getMonitorGroupDetail() {
		return monitorGroupDetail;
	}

	public void setMonitorGroupDetail(MonitorGroupDetail monitorGroupDetail) {
		this.monitorGroupDetail = monitorGroupDetail;
	}

	public MonitoringService getMonitoringService() {
		return monitoringService;
	}

	public void setMonitoringService(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}

	
	public MonitorSite getMonitorSite() {
		return monitorSite;
	}

	public void setMonitorSite(MonitorSite monitorSite) {
		this.monitorSite = monitorSite;
	}

	public String refresh() {
		this.monitorSite = new MonitorSite();
//        associatedWithFacility = null;
//        allowedToChangeFacility = false;
//        
//        if(facilityId != null) {
//        	associatedWithFacility="Y";
//        	allowedToChangeFacility = true;
//        }
        
		// may be created from facility (facilityId != null)
//		this.correspondence.setFacilityID(facilityId);
//		facilityId = null;

		return CREATE_OUTCOME;
	}

	public String getPageRedirect() {
		return pageRedirect;
	}

	public void setPageRedirect(String pageRedirect) {
		this.pageRedirect = pageRedirect;
	}

	public final String create() {
		boolean ok = true;
		
//		if(!firstButtonClick()) { // protect from multiple clicks
//            return null;
//        }
        
		try {
//			cleanOrphanData();
		
			monitorSite.setGroupId(getMonitorGroupDetail().getGroupId());
			String errorClientIdPrefix = "createCorrespondence:";
			if (displayValidationMessages(errorClientIdPrefix,
					monitorSite.validate())) {
				ok = false;
			}


			if (ok) {
				if (monitorSite.getStatus().equals(getActiveStatusCd())) {
	        		monitorSite.setEndDate(null);
	        	}
				monitorSite = getMonitoringService()
						.createMonitorSite(monitorSite);

				if (monitorSite.getSiteId() == null) {
					DisplayUtil.displayError("Failed to add monitor site");
				} else {
					DisplayUtil.displayInfo("Monitor site added");
				}

			}
		} catch (Exception ex) {
			DisplayUtil.displayError("Failed to create monitor site ");
//			logger.error(ex.getMessage(), ex);
			ok = false;
		} finally {
//			clearButtonClicked();
		}

		if (ok) {
			getMonitorSiteDetail().setSiteId(monitorSite.getSiteId());
			if (null == getMonitorSiteDetail().refresh()) {
				DisplayUtil.displayError("Error occurred while refreshing monitor site detail.");
			} else {
				FacesUtil.returnFromDialogAndRefresh();
				getMonitorGroupDetail().associatedSitesDialogDone(); //manually call the return listener. TODO: revisit
				reset();
			}
		}

		return null;
	}



	public String reset() {

		monitorSite = new MonitorSite();

		return null;
	}


	public String cancel() {
		reset();
		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}
	
	public String getActiveStatusCd() {
		return MonitorStatusDef.getActiveCode();
	}

}
