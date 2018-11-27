package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * @author Kbradley
 * 
 */
public class ControlEquipmentData extends BaseDB {
    private Integer fpNodeId;
    private Integer dataDetailId;
    private String dataValue;

    public ControlEquipmentData() {
    }

    /**
     * @return
     */
    public final String getDataValue() {
        return dataValue;
    }

    /**
     * @param value
     */
    public final void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    /**
     * @return
     */
    public final Integer getDataDetailId() {
        return dataDetailId;
    }

    /**
     * @param dataId
     */
    public final void setDataDetailId(Integer dataDetailId) {
        this.dataDetailId = dataDetailId;
    }

    /**
     * @return
     */
    public final Integer getFpNodeId() {
        return fpNodeId;
    }

    /**
     * @param fpNodeId
     */
    public final void setFpNodeId(Integer fpNodeId) {
        this.fpNodeId = fpNodeId;
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            setFpNodeId(AbstractDAO.getInteger(rs, "fpnode_id"));
            setDataDetailId(AbstractDAO.getInteger(rs, "data_detail_id"));
            setDataValue(rs.getString("data_value"));
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }
}
