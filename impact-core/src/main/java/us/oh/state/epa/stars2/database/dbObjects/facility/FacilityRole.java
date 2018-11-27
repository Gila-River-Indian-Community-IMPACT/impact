package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.FacilityRoleDef;

public class FacilityRole extends BaseDB {
	private static final long serialVersionUID = -4156450291212483898L;
	
	private String facilityId;
    private Integer userId;
    private String facilityRoleCd;
    private String facilityRoleDsc;
    private Integer activityCount;

    public FacilityRole() {
    }

    public FacilityRole(FacilityRoleDef facRoleDef, String facilityId) {
        if (facRoleDef != null) {
            setFacilityId(facilityId);
            setUserId(facRoleDef.getUserId());
            setFacilityRoleCd(facRoleDef.getFacilityRoleCd());
        }
    }

    public final String getFacilityRoleDsc() {
        return facilityRoleDsc;
    }

    public final void setFacilityRoleDsc(String facilityRoleDsc) {
        this.facilityRoleDsc = facilityRoleDsc;
    }

    public final String getFacilityRoleCd() {
        return facilityRoleCd;
    }

    public final void setFacilityRoleCd(String facilityRoleCd) {
        this.facilityRoleCd = facilityRoleCd;
    }

    public final Integer getUserId() {
        return userId;
    }

    public final void setUserId(Integer userId) {
        this.userId = userId;
    }

    public final String getFacilityId() {
        return facilityId;
    }

    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public final void populate(ResultSet rs) {
        try {
            setFacilityId(rs.getString("facility_id"));
            setUserId(AbstractDAO.getInteger(rs, "user_id"));
            setFacilityRoleCd(rs.getString("facility_role_cd"));
            setFacilityRoleDsc(rs.getString("facility_role_dsc"));
            setLastModified(AbstractDAO.getInteger(rs, "ffrx_lm"));
            setActivityCount(AbstractDAO.getInteger(rs, "ACTIVITY_COUNT"));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle);
        }
    }

	public Integer getActivityCount() {
		return activityCount;
	}

	public void setActivityCount(Integer activityCount) {
		this.activityCount = activityCount;
	}

}
