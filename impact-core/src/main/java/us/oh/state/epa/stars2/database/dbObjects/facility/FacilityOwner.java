package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.wy.state.deq.impact.database.dbObjects.company.Company;

@SuppressWarnings("serial")
public class FacilityOwner extends BaseDB {
	private Company company;
	private String facilityId;

	private transient Timestamp startDate; // Used in memory not in database
	private transient Timestamp endDate; // Used in memory not in database

	public FacilityOwner() {

	}
	
	public FacilityOwner(Timestamp startDate, Company company, String facilityId, Timestamp endDate) {
		this.startDate = startDate;
		this.company = company;
		this.facilityId = facilityId;
		this.endDate = endDate;
	}
	
	public FacilityOwner(FacilityOwner oldOwner){
		super(oldOwner);
		
		this.startDate = oldOwner.getStartDate();
		this.company = oldOwner.getCompany();
		this.facilityId = oldOwner.getFacilityId();
		this.endDate = oldOwner.getEndDate();
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		setStartDate(rs.getTimestamp("start_date"));
		setEndDate(rs.getTimestamp("end_date"));
		setFacilityId(rs.getString("facility_id"));
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}
}
