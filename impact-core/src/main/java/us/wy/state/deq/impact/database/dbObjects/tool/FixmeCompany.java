package us.wy.state.deq.impact.database.dbObjects.tool;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;

@SuppressWarnings("serial")
public class FixmeCompany extends BaseDB {

	private String facilityId;
	private Integer fpId;
	private Integer oldCompanyId;
	private String oldCompanyName;
	private Timestamp oldStartDate;
	private Timestamp oldEndDate;
	private Integer newCompanyId;
	private String newCompanyName;
	private Timestamp newStartDate;
	private Timestamp newEndDate;

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public Integer getFpId() {
		return fpId;
	}

	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}

	public Integer getOldCompanyId() {
		return oldCompanyId;
	}

	public void setOldCompanyId(Integer oldCompanyId) {
		this.oldCompanyId = oldCompanyId;
	}

	public String getOldCompanyName() {
		return oldCompanyName;
	}

	public void setOldCompanyName(String oldCompanyName) {
		this.oldCompanyName = oldCompanyName;
	}

	public Timestamp getOldStartDate() {
		return oldStartDate;
	}

	public void setOldStartDate(Timestamp oldStartDate) {
		this.oldStartDate = oldStartDate;
	}

	public Timestamp getOldEndDate() {
		return oldEndDate;
	}

	public void setOldEndDate(Timestamp oldEndDate) {
		this.oldEndDate = oldEndDate;
	}

	public Integer getNewCompanyId() {
		return newCompanyId;
	}

	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
	}

	public String getNewCompanyName() {
		return newCompanyName;
	}

	public void setNewCompanyName(String newCompanyName) {
		this.newCompanyName = newCompanyName;
	}

	public Timestamp getNewStartDate() {
		return newStartDate;
	}

	public void setNewStartDate(Timestamp newStartDate) {
		this.newStartDate = newStartDate;
	}

	public Timestamp getNewEndDate() {
		return newEndDate;
	}

	public void setNewEndDate(Timestamp newEndDate) {
		this.newEndDate = newEndDate;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		setFacilityId(rs.getString("facility_id"));
		setFpId(AbstractDAO.getInteger(rs, "fp_id"));
		setOldCompanyId(AbstractDAO.getInteger(rs, "old_company"));
		setOldCompanyName(rs.getString("old_company_name"));
		setOldStartDate(rs.getTimestamp("old_startdate"));
		setOldEndDate(rs.getTimestamp("old_enddate"));
		setNewCompanyId(AbstractDAO.getInteger(rs, "new_company"));
		setNewCompanyName(rs.getString("new_company_name"));
		setNewStartDate(rs.getTimestamp("new_startdate"));
		setNewEndDate(rs.getTimestamp("new_neddate"));
	}

}
