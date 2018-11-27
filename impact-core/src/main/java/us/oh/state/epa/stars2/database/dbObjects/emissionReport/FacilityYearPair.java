package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class FacilityYearPair extends BaseDB {
    private String facilityId;
    private String facilityOpState;
    private int year;
    private String category;
    private String state;

    public FacilityYearPair() {
    }

    public final void populate(ResultSet rs) {
        try {
            setFacilityId(rs.getString("facility_id"));
            setFacilityOpState(rs.getString("operating_status_cd"));
            setYear(AbstractDAO.getInteger(rs, "year"));
            setState(rs.getString("rpt_received_status_cd"));
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }
    
    

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((facilityId == null) ? 0 : facilityId.hashCode());
        result = PRIME * result + year;
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
        final FacilityYearPair other = (FacilityYearPair) obj;
        if (facilityId == null) {
            if (other.facilityId != null)
                return false;
        } else if (!facilityId.equals(other.facilityId))
            return false;
        if (year != other.year)
            return false;
        return true;
    }

    public final String getFacilityId() {
        return facilityId;
    }

    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFacilityOpState() {
        return facilityOpState;
    }

    public void setFacilityOpState(String facilityOpState) {
        this.facilityOpState = facilityOpState;
    }
}
