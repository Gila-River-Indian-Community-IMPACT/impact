package us.wy.state.deq.impact.webcommon.monitoring;

import javax.faces.event.ValueChangeEvent;

import us.oh.state.epa.stars2.bo.MonitoringService;
import us.oh.state.epa.stars2.def.MonitorStatusDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.wy.state.deq.impact.database.dbObjects.monitoring.Monitor;

@SuppressWarnings("serial")
public class CreateMonitor extends AppBase {

    private String pageRedirect;

    public static final String CREATE_OUTCOME = "monitoring.createMonitor";
    
    private Monitor monitor = new Monitor();

    private MonitorDetail monitorDetail;
    
    private MonitorSiteDetail monitorSiteDetail;
    
    private MonitoringService monitoringService;

	public MonitoringService getMonitoringService() {
		return monitoringService;
	}

	public MonitorSiteDetail getMonitorSiteDetail() {
		return monitorSiteDetail;
	}

	public void setMonitorSiteDetail(MonitorSiteDetail monitorSiteDetail) {
		this.monitorSiteDetail = monitorSiteDetail;
	}

	public void setMonitoringService(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}
	

	public MonitorDetail getMonitorDetail() {
		return monitorDetail;
	}

	public void setMonitorDetail(MonitorDetail monitorDetail) {
		this.monitorDetail = monitorDetail;
	}

	public String refresh() {
		this.monitor = new Monitor();
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
		String ret = null;
		boolean ok = true;
		
		if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        
		try {
//			cleanOrphanData();
			
			monitor.setSiteId(getMonitorSiteDetail().getSiteId());
			String errorClientIdPrefix = "createMonitor:";
			if (displayValidationMessages(errorClientIdPrefix,
					monitor.validate())) {
				ok = false;
			}


			if (ok) {
				if (monitor.getStatus().equals(getActiveStatusCd())) {
	        		monitor.setEndDate(null);
	        	}
				
				if (monitor.isTypeAmbient()) {
	        		monitor.setParameterMet(null);
	        	}
	        	
	        	if (monitor.isTypeMeteorological()) {
	        		monitor.setParameter(null);
	        	}
	        	
				monitor = getMonitoringService()
						.createMonitor(monitor);

				if (monitor.getMonitorId() == null) {
					DisplayUtil.displayError("Failed to create monitor");
				} else {
					DisplayUtil.displayInfo("Monitor created");
				}
				
			}
		} catch (Exception ex) {
			DisplayUtil.displayError("Failed to create monitor");
//			logger.error(ex.getMessage(), ex);
			ok = false;
		} finally {
			clearButtonClicked();
		}

		if (ok) {
			getMonitorDetail().setMonitorId(monitor.getMonitorId());
			if (null == getMonitorDetail().refresh()) {
				DisplayUtil.displayError("Error occurred while refreshing monitor detail.");
			} else {
				reset();
				FacesUtil.returnFromDialogAndRefresh();
				getMonitorSiteDetail().addMonitorDialogDone(); //manually call the return listener. TODO: revisit
			}
		}

		return ret;
	}



	public String reset() {

		monitor = new Monitor();

		return null;
	}

	public Monitor getMonitor() {
		return monitor;
	}

	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}
	
	public String cancel() {
		reset();
		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}
	
	public void monitorTypeChanged(ValueChangeEvent event) {
		getMonitor().setParameter(null);
		getMonitor().setParameterMet(null);
	}
	
	public String getActiveStatusCd() {
		return MonitorStatusDef.getActiveCode();
	}

	

}
