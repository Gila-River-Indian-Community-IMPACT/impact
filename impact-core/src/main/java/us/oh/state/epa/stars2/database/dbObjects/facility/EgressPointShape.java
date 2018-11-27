package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * @author Kbradley
 * 
 */
public class EgressPointShape extends BaseDB {
    private String egressPointShapeDsc;
    private String egressPointShapeCd;

    public EgressPointShape() {
    }

    /**
     * @return
     */
    public final String getEgressPointShapeCd() {
        return egressPointShapeCd;
    }

    /**
     * @param egressPointShapeCd
     */
    public final void setEgressPointShapeCd(String egressPointShapeCd) {
        this.egressPointShapeCd = egressPointShapeCd;
    }

    /**
     * @return
     */
    public final String getEgressPointShapeDsc() {
        return egressPointShapeDsc;
    }

    /**
     * @param egressPointShapeDsc
     */
    public final void setEgressPointShapeDsc(String egressPointShapeDsc) {
        this.egressPointShapeDsc = egressPointShapeDsc;
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            setEgressPointShapeCd(rs.getString("egress_point_shape_cd"));
            setEgressPointShapeDsc(rs.getString("egress_point_shape_dsc"));
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }
}
