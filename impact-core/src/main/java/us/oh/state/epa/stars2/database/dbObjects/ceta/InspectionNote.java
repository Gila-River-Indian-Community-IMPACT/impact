package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

public class InspectionNote extends Note {

    private Integer _fceId;

    public InspectionNote() {
        this.requiredField(_fceId, "fceId");
    }

    public InspectionNote(InspectionNote note) {
        super(note);
        setFceId(note.getFceId());
        setDirty(note.isDirty());
    }

   

    public final void populate(ResultSet rs) {

        try {
            super.populate(rs);
            setFceId(AbstractDAO.getInteger(rs, "FCE_ID"));
            setDirty(false);
        } 
        catch (SQLException sqle) {
            logger.error("Required field error");
        }

    }

    @Override
        public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result
            + ((_fceId == null) ? 0 : _fceId.hashCode());
        return result;
    }

    @Override
        public boolean equals(Object obj) {

        if ((obj == null) || !(super.equals(obj))
            || (getClass() != obj.getClass())) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        final InspectionNote other = (InspectionNote) obj;

        // Either both null or equal values.
        if (((_fceId == null) && (other.getFceId() != null))
            || ((_fceId != null) && (other.getFceId() == null))
            || ((_fceId != null) && (other.getFceId() != null) 
                && !(_fceId.equals(other.getFceId())))) {
                                                                          
            return false;
        }
        return true;
    }

	public Integer getFceId() {
		return _fceId;
	}

	public void setFceId(Integer fceId) {
		this._fceId = fceId;
	     this.requiredField(_fceId, "fceId");
	        setDirty(true);
	   
	}
}
