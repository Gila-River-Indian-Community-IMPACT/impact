package us.wy.state.deq.impact.webcommon.monitoring;

import javax.faces.event.ValueChangeEvent;

import us.oh.state.epa.stars2.bo.MonitoringService;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroup;

@SuppressWarnings("serial")
public class CreateMonitorGroup extends AppBase {

    private String pageRedirect;

    public static final String CREATE_OUTCOME = "monitoring.createMonitorGroup";
    
    private MonitorGroup monitorGroup = new MonitorGroup();

    private MonitoringService monitoringService;
    
    private String selectedContractor;

	public MonitoringService getMonitoringService() {
		return monitoringService;
	}

	public void setMonitoringService(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}
	
	public MonitorGroup getMonitorGroup() {
		return monitorGroup;
	}

	public void setMonitorGroup(MonitorGroup monitorGroup) {
		this.monitorGroup = monitorGroup;
	}
	
	public String getSelectedContractor() {
		return selectedContractor;
	}

	public void setSelectedContractor(String selectedContractor) {
		this.selectedContractor = selectedContractor;
	}

	public String refresh() {
		this.monitorGroup = new MonitorGroup();
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
	
	public void aqdOwnedSelected(ValueChangeEvent event) {
			getMonitorGroup().setCmpId(null);
			getMonitorGroup().setCompanyId(null);
			getMonitorGroup().setCompanyName(null);
			getMonitorGroup().setFacilityClass(null);
			getMonitorGroup().setFacilityId(null);
			getMonitorGroup().setFacilityName(null);
			getMonitorGroup().setFacilityType(null);
		
			getMonitorGroup().setContractor(null);

	}
	
	public String getPageRedirect() {
		return pageRedirect;
	}

	public void setPageRedirect(String pageRedirect) {
		this.pageRedirect = pageRedirect;
	}

	public boolean isFacilityAssociatedWithOtherGroup() throws DAOException {
		boolean ret = false;
		if (null != getMonitorGroup().getFacilityId()) {
			MonitorGroup searchObj = new MonitorGroup();
			searchObj.setFacilityId(getMonitorGroup().getFacilityId());
			ret = getMonitoringService().searchMonitorGroups(searchObj).length > 0;
		}
		return ret;
	}


	public final String create() {
		String ret = null;
		boolean ok = true;
		
//		if(!firstButtonClick()) { // protect from multiple clicks
//            return null;
//        }
        
		try {
//			cleanOrphanData();
			
	    	getMonitorGroup().setFacilityAssociatedWithOtherGroup(isFacilityAssociatedWithOtherGroup());
			String errorClientIdPrefix = "createCorrespondence:";
			if (displayValidationMessages(errorClientIdPrefix,
					monitorGroup.validate())) {
				ok = false;
			}


			if (ok) {
				monitorGroup = getMonitoringService()
						.createMonitorGroup(monitorGroup);

				if (monitorGroup.getGroupId() == null) {
					DisplayUtil.displayError("Failed to create monitor group");
				} else {
					DisplayUtil.displayInfo("Monitor group created");
				}
				
			}
		} catch (Exception ex) {
			DisplayUtil.displayError("Failed to create monitor group ");
//			logger.error(ex.getMessage(), ex);
			ok = false;
		} finally {
//			clearButtonClicked();
		}

		if (ok) {
			MonitorGroupDetail monitorGroupDetail = (MonitorGroupDetail) FacesUtil
					.getManagedBean("monitorGroupDetail");
			monitorGroupDetail.setGroupId(monitorGroup.getGroupId());
			ret = monitorGroupDetail.refresh();
		}

		return ret;
	}



	public String reset() {

		monitorGroup = new MonitorGroup();

		return null;
	}

}
