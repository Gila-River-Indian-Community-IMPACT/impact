package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import org.gricdeq.impact.ExternalRole;

import us.wy.state.deq.impact.database.dbObjects.company.Company;

public class ContactRole {

	private ExternalRole externalRole;
	
	private Company company;

	private boolean active;

	private int totalExcludedFacilities = 0;

	
	public ContactRole() {
		super();
	}
	
	public ContactRole(ExternalRole externalRole) {
		super();
		this.externalRole = externalRole;
	}

	public ExternalRole getExternalRole() {
		return externalRole;
	}

	public void setExternalRole(ExternalRole externalRole) {
		this.externalRole = externalRole;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public int getTotalExcludedFacilities() {
		return totalExcludedFacilities;
	}

	public void setTotalExcludedFacilities(int totalExcludedFacilities) {
		this.totalExcludedFacilities = totalExcludedFacilities;
	}
	
	
}
