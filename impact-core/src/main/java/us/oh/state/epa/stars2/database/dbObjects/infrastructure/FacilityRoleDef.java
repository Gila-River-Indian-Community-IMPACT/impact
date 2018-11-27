package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class FacilityRoleDef extends BaseDB {
    private String countyCd;
    private String facilityRoleCd;
    private Integer userId;

    public FacilityRoleDef() {
    }

    public final String getCountyCd() {
        return countyCd;
    }

    public final void setCountyCd(String countyCd) {
        this.countyCd = countyCd;
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

    public final void populate(ResultSet rs) {
        try {
            setCountyCd(rs.getString("county_cd"));
            setFacilityRoleCd(rs.getString("facility_role_cd"));
            setUserId(AbstractDAO.getInteger(rs, "user_id"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }
}
