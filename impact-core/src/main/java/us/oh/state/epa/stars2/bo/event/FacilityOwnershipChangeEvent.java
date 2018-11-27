package us.oh.state.epa.stars2.bo.event;

import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityOwner;


public class FacilityOwnershipChangeEvent implements ImpactEvent {

	private FacilityOwner currentFacilityOwner;
	
	private FacilityOwner newFacilityOwner;
	
	public FacilityOwnershipChangeEvent(FacilityOwner currentFacilityOwner,
			FacilityOwner newFacilityOwner) {
		
		this.currentFacilityOwner = currentFacilityOwner;
		this.newFacilityOwner = newFacilityOwner;
	}

	public FacilityOwner getCurrentFacilityOwner() {
		return currentFacilityOwner;
	}

	public void setCurrentFacilityOwner(FacilityOwner currentFacilityOwner) {
		this.currentFacilityOwner = currentFacilityOwner;
	}

	public FacilityOwner getNewFacilityOwner() {
		return newFacilityOwner;
	}

	public void setNewFacilityOwner(FacilityOwner newFacilityOwner) {
		this.newFacilityOwner = newFacilityOwner;
	}
}