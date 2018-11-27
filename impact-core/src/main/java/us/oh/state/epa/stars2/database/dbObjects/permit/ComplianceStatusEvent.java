package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class ComplianceStatusEvent extends BaseDB {
	
	private Integer complianceStatusId;
	private String cStatusId;
	private Integer permitConditionId;
	private String pCondId;
	private Timestamp eventDate;
	private String eventTypeCd;
	private String status;
	private Integer inspectionReference;
	private Integer stackTestReference;
	private Integer complianceReportReference;
	
	private Integer lastUpdatedById;
	private Timestamp lastUpdatedDate;
	
	public ComplianceStatusEvent() {

		this.requiredField(permitConditionId, "permit_condition_id");
		this.requiredField(eventDate, "event_dt");
		this.requiredField(eventTypeCd, "event_type_cd");
		this.requiredField(lastUpdatedDate, "last_updated_dt");
		setDirty(false);
	}

	public ComplianceStatusEvent(ComplianceStatusEvent old) {
		super(old);
		setComplianceStatusId(old.getComplianceStatusId());
		setcStatusId(old.getcStatusId());
		setPermitConditionId(old.getPermitConditionId());
		setEventDate(old.getEventDate());
		setEventTypeCd(old.getEventTypeCd());
		setStatus(old.getStatus());
		setInspectionReference(old.getInspectionReference());
		setComplianceReportReference(old.getComplianceReportReference());
		setStackTestReference(old.getStackTestReference());
		setLastUpdatedById(old.getLastUpdatedById());
		setpCondId(old.getpCondId());
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		setComplianceStatusId(AbstractDAO.getInteger(rs, "compliance_status_id"));
		setcStatusId(rs.getString("cstatus_id"));
		setPermitConditionId(AbstractDAO.getInteger(rs, "permit_condition_id"));
		setEventDate(rs.getTimestamp("event_dt"));
		setEventTypeCd(rs.getString("event_type_cd"));
		setStatus(rs.getString("status"));
		setInspectionReference(AbstractDAO.getInteger(rs, "inspection_reference"));
		setComplianceReportReference(AbstractDAO.getInteger(rs, "compliance_report_reference"));
		setStackTestReference(AbstractDAO.getInteger(rs, "stack_test_reference"));
		setLastUpdatedById(AbstractDAO.getInteger(rs, "last_updated_by_id"));
		setLastUpdatedDate(rs.getTimestamp("last_updated_dt"));
		setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
		
		setDirty(false);
		
		try {
			setpCondId(rs.getString("p_cond_id"));
		} catch (SQLException sqle) {
			logger.debug("Optional field error: " + sqle.getMessage());
		}
	}

	public String getcStatusId() {
		return cStatusId;
	}

	public void setcStatusId(String cStatusId) {
		this.cStatusId = cStatusId;
	}

	public Integer getPermitConditionId() {
		return permitConditionId;
	}

	public void setPermitConditionId(Integer permitConditionId) {
		this.permitConditionId = permitConditionId;
	}

	public Integer getComplianceStatusId() {
		return complianceStatusId;
	}

	public void setComplianceStatusId(Integer complianceStatusId) {
		this.complianceStatusId = complianceStatusId;
	}

	public Timestamp getEventDate() {
		return eventDate;
	}

	public void setEventDate(Timestamp eventDate) {
		this.eventDate = eventDate;
	}

	public String getEventTypeCd() {
		return eventTypeCd;
	}

	public void setEventTypeCd(String eventTypeCd) {
		this.eventTypeCd = eventTypeCd;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getInspectionReference() {
		return inspectionReference;
	}

	public void setInspectionReference(Integer inspectionReference) {
		this.inspectionReference = inspectionReference;
	}

	public Integer getStackTestReference() {
		return stackTestReference;
	}

	public void setStackTestReference(Integer stackTestReference) {
		this.stackTestReference = stackTestReference;
	}

	public Integer getComplianceReportReference() {
		return complianceReportReference;
	}

	public void setComplianceReportReference(Integer complianceReportReference) {
		this.complianceReportReference = complianceReportReference;
	}

	public Integer getLastUpdatedById() {
		return lastUpdatedById;
	}

	public void setLastUpdatedById(Integer lastUpdatedById) {
		this.lastUpdatedById = lastUpdatedById;
	}

	public Timestamp getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}
	
	// needed for jsp only so that the last updated names in the
	// datagrid can be sorted by name instead of id
	public final String getLastUpdatedByName() {
		String ret = null;
		if(null != this.lastUpdatedById) {
			ret = BasicUsersDef.getUserNm(this.lastUpdatedById);
		}
		return ret;
	}

	public String getpCondId() {
		return pCondId;
	}

	public void setpCondId(String pCondId) {
		this.pCondId = pCondId;
	}

}
