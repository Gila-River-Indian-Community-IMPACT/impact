package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

public class PermitNote extends Note {

    private Integer _permitId;

    public PermitNote() {
        this.requiredField(_permitId, "permitId");
    }

    public PermitNote(PermitNote note) {
        super(note);
        setPermitId(note.getPermitId());
        setDirty(note.isDirty());
    }

    public final Integer getPermitId() {
        return _permitId;
    }

    public final void setPermitId(Integer permitId) {
        _permitId = permitId;
        this.requiredField(_permitId, "permitId");
        setDirty(true);
    }

    public final void populate(ResultSet rs) {

        try {
            super.populate(rs);
            setPermitId(AbstractDAO.getInteger(rs, "PERMIT_ID"));
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
            + ((_permitId == null) ? 0 : _permitId.hashCode());
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

        final PermitNote other = (PermitNote) obj;

        // Either both null or equal values.
        if (((_permitId == null) && (other.getPermitId() != null))
            || ((_permitId != null) && (other.getPermitId() == null))
            || ((_permitId != null) && (other.getPermitId() != null) 
                && !(_permitId.equals(other.getPermitId())))) {
                                                                          
            return false;
        }
        return true;
    }
}
