package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class FacilityRoleActivity extends BaseDB {

	private static final long serialVersionUID = 8818195149624411875L;

	private String facilityRoleCd;
	private String facilityRoleDsc;
	private String processTemplateDsc;
	private String processDsc;
	private String activityTemplateNm;

	public FacilityRoleActivity() {
	}

	public final String getFacilityRoleCd() {
		return facilityRoleCd;
	}

	public final void setFacilityRoleCd(String facilityRoleCd) {
		this.facilityRoleCd = facilityRoleCd;
	}

	public final String getFacilityRoleDsc() {
		return facilityRoleDsc;
	}

	public final void setFacilityRoleDsc(String facilityRoleDsc) {
		this.facilityRoleDsc = facilityRoleDsc;
	}

	public String getProcessTemplateDsc() {
		return processTemplateDsc;
	}

	public void setProcessTemplateDsc(String processTemplateDsc) {
		this.processTemplateDsc = processTemplateDsc;
	}

	public String getProcessDsc() {
		return processDsc;
	}

	public void setProcessDsc(String processDsc) {
		this.processDsc = processDsc;
	}

	public String getActivityTemplateNm() {
		return activityTemplateNm;
	}

	public void setActivityTemplateNm(String activityTemplateNm) {
		this.activityTemplateNm = activityTemplateNm;
	}

	public final void populate(ResultSet rs) {
		try {
			setFacilityRoleCd(rs.getString("FACILITY_ROLE_CD"));
			setFacilityRoleDsc(rs.getString("FACILITY_ROLE_DSC"));
			setProcessTemplateDsc(rs.getString("PROCESS_TEMPLATE_DSC"));
			setProcessDsc(rs.getString("PROCESS_DSC"));
			setActivityTemplateNm(rs.getString("ACTIVITY_TEMPLATE_NM"));
		} catch (SQLException sqle) {
			logger.error("Required field error: " + sqle);
		}
	}

}
