package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

public class SiteVisitNote extends Note {

    private Integer _visitId;

    public SiteVisitNote() {
        this.requiredField(_visitId, "visitId");
    }

    public SiteVisitNote(SiteVisitNote note) {
        super(note);
        setVisitId(note.getVisitId());
        setDirty(note.isDirty());
    }

   

    public final void populate(ResultSet rs) {

        try {
            super.populate(rs);
            setVisitId(AbstractDAO.getInteger(rs, "VISIT_ID"));
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
            + ((_visitId == null) ? 0 : _visitId.hashCode());
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

        final SiteVisitNote other = (SiteVisitNote) obj;

        // Either both null or equal values.
        if (((_visitId == null) && (other.getVisitId() != null))
            || ((_visitId != null) && (other.getVisitId() == null))
            || ((_visitId != null) && (other.getVisitId() != null) 
                && !(_visitId.equals(other.getVisitId())))) {
                                                                          
            return false;
        }
        return true;
    }

	public Integer getVisitId() {
		return _visitId;
	}

	public void setVisitId(Integer visitId) {
		this._visitId = visitId;
	     this.requiredField(_visitId, "visitId");
	        setDirty(true);
	   
	}
}
