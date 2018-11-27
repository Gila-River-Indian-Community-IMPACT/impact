package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;

/**
 * @author Kbradley
 * 
 */
public class EuEmission extends FacilityEmission {
	
	private static final long serialVersionUID = 3541429644795212170L;

	private Integer emuId;
    private String comment;

    public EuEmission() {
    }

    public EuEmission(EuEmission other) {
    	super(other);
    	setEmuId(other.getEmuId());
    	setComment(other.getComment());
    }
    
    /**
     * @return
     */
    public final String getComment() {
        return comment;
    }

    /**
     * @param comment
     */
    public final void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return
     */
    public final Integer getEmuId() {
        return emuId;
    }

    /**
     * @param PollutantDsc
     */
    public final void setEmuId(Integer emuId) {
        this.emuId = emuId;
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            super.populate(rs);
            setEmuId(AbstractDAO.getInteger(rs, "emu_id"));
            setComment(rs.getString("comments"));
            setLastModified(AbstractDAO.getInteger(rs, "euEmissions_lm"));
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((comment == null) ? 0 : comment.hashCode());
        result = PRIME * result + ((emuId == null) ? 0 : emuId.hashCode());
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
        final EuEmission other = (EuEmission) obj;
        if (comment == null) {
            if (other.comment != null)
                return false;
        } else if (!comment.equals(other.comment))
            return false;
        if (emuId == null) {
            if (other.emuId != null)
                return false;
        } else if (!emuId.equals(other.emuId))
            return false;
        return true;
    }

}
