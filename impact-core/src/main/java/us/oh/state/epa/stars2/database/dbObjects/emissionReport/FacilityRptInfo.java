package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class FacilityRptInfo extends BaseDB {
    private String facilityId;
    private Timestamp shutdownDate;
    private String operatingStatusCd;
    //private Integer fpId;

    public FacilityRptInfo() {
    }

    public final void populate(ResultSet rs) {
        try {
            setFacilityId(rs.getString("facility_id"));
            setShutdownDate(rs.getTimestamp("last_shutdown_date"));
            setOperatingStatusCd(rs.getString("operating_status_cd"));
            //(AbstractDAO.getInteger(rs, "fp_id"));
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }

    public final String getFacilityId() {
        return facilityId;
    }

    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
    
    public final String getOperatingStatusCd() {
        return operatingStatusCd;
    }

    public final void setOperatingStatusCd(String operatingStatusCd) {
        this.operatingStatusCd = operatingStatusCd;
    }
    /*
    public Integer getFpId() {
		return fpId;
	}

	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}
*/
	@Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((facilityId == null) ? 0 : facilityId.hashCode());
        result = PRIME * result + ((operatingStatusCd == null) ? 0 : operatingStatusCd.hashCode());
        result = PRIME * result + ((shutdownDate == null) ? 0 : shutdownDate.hashCode());
        //result = PRIME * result + ((fpId == null) ? 0 : fpId.hashCode());
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
        final FacilityRptInfo other = (FacilityRptInfo) obj;
        if (facilityId == null) {
            if (other.facilityId != null)
                return false;
        } else if (!facilityId.equals(other.facilityId))
            return false;
        if (operatingStatusCd == null) {
            if (other.operatingStatusCd != null)
                return false;
        } else if (!operatingStatusCd.equals(other.operatingStatusCd))
            return false;
        if (shutdownDate == null) {
            if (other.shutdownDate != null)
                return false;
        } else if (!shutdownDate.equals(other.shutdownDate))
            return false;
        //if (fpId == null) {
        // (other.fpId != null)
        //        return false;
        //} else if (!fpId.equals(other.fpId))
        // false;
        return true;
    }

    public Timestamp getShutdownDate() {
        return shutdownDate;
    }

    public void setShutdownDate(Timestamp shutdownDate) {
        this.shutdownDate = shutdownDate;
    }
}
