package us.oh.state.epa.stars2.bo.event;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.MonitoringService;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroup;

@Component
public class FacilityOwnershipChangeEventListener 
	implements ImpactEventListener<FacilityOwnershipChangeEvent> {
	
	@Autowired FacilityService facilityService;
	
	@Autowired MonitoringService monitoringService;
	
	@PostConstruct
	public void init() {
		facilityService.getListeners().add(this);
	}

	@Override
	public void eventOccurred(FacilityOwnershipChangeEvent event) {
		MonitorGroup searchObj = new MonitorGroup();
		searchObj.setFacilityId(event.getCurrentFacilityOwner().getFacilityId());
		try {
			MonitorGroup[] groups = getMonitoringService().searchMonitorGroups(searchObj);
			for (MonitorGroup group : groups) {
				group.setCmpId(event.getNewFacilityOwner().getCompany().getCmpId());
				getMonitoringService().modifyMonitorGroup(group);
			}
		} catch (DAOException e) {
			throw new RuntimeException("Exception occurred updating monitor groups during facility ownership change.");
		}
	}

	protected FacilityService getFacilityService() {
		return facilityService;
	}

	protected void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	protected MonitoringService getMonitoringService() {
		return monitoringService;
	}

	protected void setMonitoringService(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}
}
