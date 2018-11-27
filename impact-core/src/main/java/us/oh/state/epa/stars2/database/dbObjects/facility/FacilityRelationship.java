package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * @author Kbradley
 * 
 */
public class FacilityRelationship extends BaseDB {
    private Integer fromNodeId;
    private Integer toNodeId;
    private Float flowFactor;

    public FacilityRelationship() {
    }
    
    public FacilityRelationship(Integer fromNodeId, Integer toNodeId,  Float flowFactor) {
        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
        this.flowFactor = flowFactor;
    }

    /**
     * @return
     */
    public final Integer getFromNodeId() {
        return fromNodeId;
    }

    /**
     * @param fromNodeId
     */
    public final void setFromNodeId(Integer fromNodeId) {
        this.fromNodeId = fromNodeId;
    }

    /**
     * @return
     */
    public final Integer getToNodeId() {
        return toNodeId;
    }

    /**
     * @param toNodeId
     */
    public final void setToNodeId(Integer toNodeId) {
        this.toNodeId = toNodeId;
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            setFromNodeId(AbstractDAO.getInteger(rs, "from_fpnode_id"));
            setToNodeId(AbstractDAO.getInteger(rs, "to_fpnode_id"));
            setFlowFactor(AbstractDAO.getFloat(rs, "flow_factor"));
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }

	public Float getFlowFactor() {
		return flowFactor;
	}

	public void setFlowFactor(Float flowFactor) {
		this.flowFactor = flowFactor;
	}
}
