package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * @author Kbradley
 * 
 */
public class EgressPointType extends BaseDB {
    private String egressPointTypeDsc;
    private String egressPointTypeCd;

    public EgressPointType() {
    }

    /**
     * @return
     */
    public final String getEgressPointTypeCd() {
        return egressPointTypeCd;
    }

    /**
     * @param egressPointTypeCd
     */
    public final void setEgressPointTypeCd(String egressPointTypeCd) {
        this.egressPointTypeCd = egressPointTypeCd;
    }

    /**
     * @return
     */
    public final String getEgressPointTypeDsc() {
        return egressPointTypeDsc;
    }

    /**
     * @param egressPointTypeDsc
     */
    public final void setEgressPointTypeDsc(String egressPointTypeDsc) {
        this.egressPointTypeDsc = egressPointTypeDsc;
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            setEgressPointTypeCd(rs.getString("egress_point_type_cd"));
            setEgressPointTypeDsc(rs.getString("egress_point_type_dsc"));
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }
}
