package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * @author Kbradley
 * 
 */
public class FacilityIdRef extends BaseDB {
    private String countyCd;
    private String dolaCd;
    private String cityCd;
    private String cityName;
    private String dolaId;

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            setCountyCd(rs.getString("county_cd"));
            setDolaCd(rs.getString("do_laa_cd"));
            setCityCd(rs.getString("city_cd"));
            setCityName(rs.getString("city_nm"));
            setDolaId(rs.getString("do_laa_id"));
            String deprecated = rs.getString("deprecated");
            if("Y".equals(deprecated)) {
                setCityName(cityName + "(inactive)");
            }
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }

    public final String getCityCd() {
        return cityCd;
    }

    public final void setCityCd(String cityCd) {
        this.cityCd = cityCd;
    }

    public final String getCityName() {
        return cityName;
    }

    public final void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public final String getCountyCd() {
        return countyCd;
    }

    public final void setCountyCd(String countyCd) {
        this.countyCd = countyCd;
    }

    public final String getDolaCd() {
        return dolaCd;
    }

    public final void setDolaCd(String dolaCd) {
        this.dolaCd = dolaCd;
    }

    public final String getDolaId() {
        return dolaId;
    }

    public final void setDolaId(String dolaId) {
        this.dolaId = dolaId;
    }
}
