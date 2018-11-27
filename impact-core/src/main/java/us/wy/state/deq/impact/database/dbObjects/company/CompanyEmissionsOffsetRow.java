package us.wy.state.deq.impact.database.dbObjects.company;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.permit.EmissionsOffset;

public class CompanyEmissionsOffsetRow extends EmissionsOffset {
	
	private static final long serialVersionUID = -4653142684759292593L;

	private Integer companyId;
	private String facilityId;
	private Integer fpId;
	private String facilityName;
	private String permitNumber;
	private Timestamp permitFinalIssuanceDate;
	
	public CompanyEmissionsOffsetRow() {
		super();
	}
	
	public CompanyEmissionsOffsetRow(CompanyEmissionsOffsetRow old) {
		super(old);
		if(null != old) {
			setCompanyId(old.getCompanyId());
			setFacilityId(old.getFacilityId());
			setFpId(old.getFpId());
			setFacilityName(old.getFacilityName());
			setPermitNumber(old.getPermitNumber());
			setPermitFinalIssuanceDate(old.getPermitFinalIssuanceDate());
		}
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	
	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getPermitNumber() {
		return permitNumber;
	}

	public void setPermitNumber(String permitNumber) {
		this.permitNumber = permitNumber;
	}

	public Timestamp getPermitFinalIssuanceDate() {
		return permitFinalIssuanceDate;
	}

	public void setPermitFinalIssuanceDate(Timestamp permitFinalIssuanceDate) {
		this.permitFinalIssuanceDate = permitFinalIssuanceDate;
	}
	
	public Integer getFpId() {
		return fpId;
	}

	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((companyId == null) ? 0 : companyId.hashCode());
		result = prime * result
				+ ((facilityId == null) ? 0 : facilityId.hashCode());
		result = prime * result
				+ ((facilityName == null) ? 0 : facilityName.hashCode());
		result = prime * result + ((fpId == null) ? 0 : fpId.hashCode());
		result = prime
				* result
				+ ((permitFinalIssuanceDate == null) ? 0
						: permitFinalIssuanceDate.hashCode());
		result = prime * result
				+ ((permitNumber == null) ? 0 : permitNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompanyEmissionsOffsetRow other = (CompanyEmissionsOffsetRow) obj;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		if (facilityId == null) {
			if (other.facilityId != null)
				return false;
		} else if (!facilityId.equals(other.facilityId))
			return false;
		if (facilityName == null) {
			if (other.facilityName != null)
				return false;
		} else if (!facilityName.equals(other.facilityName))
			return false;
		if (fpId == null) {
			if (other.fpId != null)
				return false;
		} else if (!fpId.equals(other.fpId))
			return false;
		if (permitFinalIssuanceDate == null) {
			if (other.permitFinalIssuanceDate != null)
				return false;
		} else if (!permitFinalIssuanceDate
				.equals(other.permitFinalIssuanceDate))
			return false;
		if (permitNumber == null) {
			if (other.permitNumber != null)
				return false;
		} else if (!permitNumber.equals(other.permitNumber))
			return false;
		return true;
	}

	public void populate(ResultSet rs)
			throws SQLException {
		if(null != rs) {
			super.populate(rs);
			setCompanyId(AbstractDAO.getInteger(rs, "company_id"));
			setFacilityId(rs.getString("facility_id"));
			setFpId(AbstractDAO.getInteger(rs, "fp_id"));
			setFacilityName(rs.getString("facility_nm"));
			setPermitNumber(rs.getString("permit_nbr"));
			setPermitFinalIssuanceDate(rs.getTimestamp("final_issuance_date"));
		}
	}

}
