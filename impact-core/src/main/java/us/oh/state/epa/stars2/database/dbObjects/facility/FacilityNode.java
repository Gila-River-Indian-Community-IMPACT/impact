package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * @author Kbradley
 * 
 */
public class FacilityNode extends BaseDB {
	
	private static final long serialVersionUID = 6064760101401953268L;

	private Integer fpId;
    private Integer fpNodeId;
    private String facilityId;
    private Integer versionId;
    private Integer correlationId;
    private HashMap<Integer, FacilityRelationship> relationships = new HashMap<Integer, FacilityRelationship>(0);
    
    // flag to indicate whether node is linked only to EUs that are shutdown
    private boolean active;

    public FacilityNode() {
        super();
    }

    public FacilityNode(FacilityNode old) {
        super(old);

        if (old != null) {
            setFpId(old.getFpId());
            setFacilityId(old.getFacilityId());
            setFpNodeId(old.getFpNodeId());
            setVersionId(old.getVersionId());
            setRelationships(old.getRelationships());
            setCorrelationId(old.getCorrelationId());
        }
    }

    public final void setRelationships(
            HashMap<Integer, FacilityRelationship> relationships) {
        this.relationships = relationships;
    }

    /**
     * @return
     */
    public final FacilityRelationship[] getRelationships() {
        return relationships.values().toArray(new FacilityRelationship[0]);
    }

    /**
     * @param relationship
     */
    public final void addRelationship(FacilityRelationship relationship) {
        if (relationship != null) {
            this.relationships.put(relationship.getToNodeId(), relationship);
        }
    }
    
    
    public final FacilityRelationship findRelationship(Integer toNodeId) {
        return relationships.get(toNodeId);
    }


    public final void setRelationships(FacilityRelationship[] relationships) {
        this.relationships = new HashMap<Integer, FacilityRelationship>(relationships.length);

        for (FacilityRelationship temp : relationships) {
            addRelationship(temp);
        }
    }

    /**
     * @return
     */
    public final String getFacilityId() {
        return facilityId;
    }

    /**
     * @param facilityId
     */
    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    /**
     * @return
     */
    public final Integer getFpId() {
        return fpId;
    }

    /**
     * @param fpId
     */
    public final void setFpId(Integer fpId) {
        this.fpId = fpId;
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
     * @return
     */
    public final Integer getVersionId() {
        return versionId;
    }

    /**
     * @param versionId
     */
    public final void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public void populate(ResultSet rs) {
        try {
            setFpId(AbstractDAO.getInteger(rs, "fp_id"));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
        
        try {
            setCorrelationId(AbstractDAO.getInteger(rs, "corr_id"));
        } catch (SQLException sqle) {
            logger.debug("Optional field error: " + sqle.getMessage());
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result
                + ((facilityId == null) ? 0 : facilityId.hashCode());
        result = PRIME * result + ((fpId == null) ? 0 : fpId.hashCode());
        result = PRIME * result
                + ((fpNodeId == null) ? 0 : fpNodeId.hashCode());
        result = PRIME * result
                + ((versionId == null) ? 0 : versionId.hashCode());
        result = PRIME * result
        		+ ((correlationId == null) ? 0 : correlationId.hashCode());
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
        final FacilityNode other = (FacilityNode) obj;
        if (fpNodeId != other.fpNodeId)
           return false;
        
        return true;
    }

    public static FacilityNode findFpNode(List<FacilityNode> fpNodes,
            Integer FpNodeId) {
        for (FacilityNode temp : fpNodes) {
            if (temp.getFpNodeId() != null && temp.getFpNodeId().equals(FpNodeId)) {
                return temp;
            }
        }
        return null;
    }

    public final Integer getCorrelationId() {
        return correlationId;
    }

    public final void setCorrelationId(Integer correlationId) {
        this.correlationId = correlationId;
    }

    public final boolean isActive() {
        return active;
    }

    public final void setActive(boolean shutdown) {
        this.active = shutdown;
    }
}
