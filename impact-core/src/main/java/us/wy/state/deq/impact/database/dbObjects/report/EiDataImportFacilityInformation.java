package us.wy.state.deq.impact.database.dbObjects.report;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class EiDataImportFacilityInformation extends BaseDB {

	private static final long serialVersionUID = -4129654573415523751L;

	private String facilityId;
	private String euId;
	private String processId;

	private Integer fpId;
	private String facilityName;
	private String sccId;
	private Integer corrEpaEmuId;

	@Override
	public void populate(ResultSet rs) throws SQLException {
		try {
			setFacilityId(rs.getString("facility_id"));
			setEuId(rs.getString("epa_emu_id"));
			setProcessId(rs.getString("process_id"));
			setFpId(AbstractDAO.getInteger(rs, "fp_id"));
			setFacilityName(rs.getString("facility_nm"));
			setCorrEpaEmuId(AbstractDAO.getInteger(rs, "corr_epa_emu_id"));
			setSccId(rs.getString("scc_id"));
		} catch (SQLException sqle) {
			logger.warn(sqle.getMessage());
		}
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getEuId() {
		return euId;
	}

	public void setEuId(String euId) {
		this.euId = euId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Integer getFpId() {
		return fpId;
	}

	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getSccId() {
		return sccId;
	}

	public void setSccId(String sccId) {
		this.sccId = sccId;
	}

	public Integer getCorrEpaEmuId() {
		return corrEpaEmuId;
	}

	public void setCorrEpaEmuId(Integer corrEpaEmuId) {
		this.corrEpaEmuId = corrEpaEmuId;
	}

}
