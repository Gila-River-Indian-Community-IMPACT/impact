package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class FacilityComplianceStatus  extends BaseDB {
	private Integer facilityHistoryId;
	private String pollutantCd;
	private String complianceCd;
    private transient boolean delete;
	
	public final Integer getFacilityHistoryId() {
		return facilityHistoryId;
	}
	public final void setFacilityHistoryId(Integer facilityHistoryId) {
		this.facilityHistoryId = facilityHistoryId;
	}
	public final String getPollutantCd() {
		return pollutantCd;
	}
	public final void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}
	public final String getComplianceCd() {
		return complianceCd;
	}
	public final void setComplianceCd(String complianceCd) {
		this.complianceCd = complianceCd;
	}
	
	public void populate(ResultSet rs) throws SQLException {
		try {
			setFacilityHistoryId(AbstractDAO.getInteger(rs, "facility_hist_id"));
			setPollutantCd(rs.getString("pollutant_cd"));
			setComplianceCd(rs.getString("comp_cd"));
	        setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
	}
    public boolean isDelete() {
        return delete;
    }
    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final FacilityComplianceStatus other = (FacilityComplianceStatus) obj;
        if (pollutantCd == null) {
            if (other.pollutantCd != null)
                return false;
        } else if (!pollutantCd.equals(other.pollutantCd))
            return false;
        return true;
    }
	
	
}
