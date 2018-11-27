package us.wy.state.deq.impact.database.dbObjects.company;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;

@SuppressWarnings("serial")
public class OffsetSummaryLineItem extends CompanyEmissionsOffsetRow {
	private String companyName;
	private String cmpId;
	
	// emissions offset adjustment information
	private Integer emissionsOffsetAdjustmentId;
	private Timestamp emissionsOffsetAdjustmentDate;
	private boolean includeInTotal;
	
	public OffsetSummaryLineItem() {
		super();
	}
	
	public OffsetSummaryLineItem(OffsetSummaryLineItem old) {
		super(old);
		if(null != old) {
			setCompanyName(old.getCompanyName());
			setCmpId(old.getCmpId());
			setEmissionsOffsetAdjustmentId(old.getEmissionsOffsetAdjustmentId());
			setEmissionsOffsetAdjustmentDate(old.getEmissionsOffsetAdjustmentDate());
			setIncludeInTotal(old.isIncludeInTotal());
		}
	}

	public String getCompanyName() {
		return companyName;
	}
	
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public String getCmpId() {
		return cmpId;
	}

	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}

	public Integer getEmissionsOffsetAdjustmentId() {
		return emissionsOffsetAdjustmentId;
	}
	
	public void setEmissionsOffsetAdjustmentId(Integer emissionsOffsetAdjustmentId) {
		this.emissionsOffsetAdjustmentId = emissionsOffsetAdjustmentId;
	}
	
	public Timestamp getEmissionsOffsetAdjustmentDate() {
		return emissionsOffsetAdjustmentDate;
	}
	
	public void setEmissionsOffsetAdjustmentDate(
			Timestamp emissionsOffsetAdjustmentDate) {
		this.emissionsOffsetAdjustmentDate = emissionsOffsetAdjustmentDate;
	}
	
	public boolean isIncludeInTotal() {
		return includeInTotal;
	}
	
	public void setIncludeInTotal(boolean includeInTotal) {
		this.includeInTotal = includeInTotal;
	}
	
	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		
		if(null != rs) {
			setCompanyName(rs.getString("company_nm"));
			setCmpId(rs.getString("cmp_id"));
			setEmissionsOffsetAdjustmentId(AbstractDAO.getInteger(rs, "emissions_offset_adjustment_id"));
			setEmissionsOffsetAdjustmentDate(rs.getTimestamp("emissions_offset_adjustment_date"));
			setIncludeInTotal(AbstractDAO.translateIndicatorToBoolean(rs.getString("include_in_total")));
		}
	}	
}
