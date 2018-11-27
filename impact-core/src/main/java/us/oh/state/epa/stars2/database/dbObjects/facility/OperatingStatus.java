package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * @author Kbradley
 * 
 */
public class OperatingStatus extends BaseDB {
    private String operatingStatusDsc;
    private String operatingStatusCd;

    public OperatingStatus() {
    }

    /**
     * @return
     */
    public final String getOperatingStatusCd() {
        return operatingStatusCd;
    }

    /**
     * @param operatingStatusCd
     */
    public final void setOperatingStatusCd(String operatingStatusCd) {
        this.operatingStatusCd = operatingStatusCd;
    }

    /**
     * @return
     */
    public final String getOperatingStatusDsc() {
        return operatingStatusDsc;
    }

    /**
     * @param operatingStatusDsc
     */
    public final void setOperatingStatusDsc(String operatingStatusDsc) {
        this.operatingStatusDsc = operatingStatusDsc;
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            setOperatingStatusCd(rs.getString("operating_status_cd"));
            setOperatingStatusDsc(rs.getString("operating_status_dsc"));
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }
}
