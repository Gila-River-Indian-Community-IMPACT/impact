package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

public class Note extends BaseDB {

	private static final long serialVersionUID = 3921383937363347823L;

	private static String[][] _messages = { { "userId", "UserId is missing." },
        { "dateEntered", "DateEntered is missing." } };
    private Integer _noteId;
    private String _noteTypeCd;
    private Integer _userId;
    private String _noteTxt;
    private Timestamp _dateEntered;

    public Note() {
        super();
        for (int i = 0; i < _messages.length; i++) {
            ValidationMessage msg = new ValidationMessage(_messages[i][0],
                    _messages[i][1]);
            validationMessages.put(_messages[i][0], msg);
        }
    }

    public Note(Note old) {
        super(old);

        for (int i = 0; i < _messages.length; i++) {
            ValidationMessage msg = new ValidationMessage(_messages[i][0],
                    _messages[i][1]);
            validationMessages.put(_messages[i][0], msg);
        }

        if (old != null) {
            setNoteId(old.getNoteId());
            setNoteTypeCd(old.getNoteTypeCd());
            setUserId(old.getUserId());
            setNoteTxt(old.getNoteTxt());
            setDateEntered(old.getDateEntered());
            setDirty(old.isDirty());
        }
    }

    public final Integer getNoteTxtLength() {
        Integer ret = 0;
        if (_noteTxt != null) {
            ret = _noteTxt.length();
        }

        return ret;
    }


    public final Timestamp getDateEntered() {
        return _dateEntered;
    }

    public final void setDateEntered(Timestamp dateEntered) {
        if (dateEntered != null) {
            validationMessages.remove("dateEntered");
        } else {
            ValidationMessage msg = new ValidationMessage(_messages[1][0],
                    _messages[1][1]);
            validationMessages.put(_messages[1][0], msg);
        }
        this._dateEntered = dateEntered;
        setDirty(true);
    }

    public final Integer getNoteId() {
        return _noteId;
    }

    public final void setNoteId(Integer noteId) {
        this._noteId = noteId;
    }

    public final String getNoteTxt() {
        return _noteTxt;
    }
    
    public final String getShortNoteTxt() {
        if(_noteTxt == null || _noteTxt.length() <= 92) return _noteTxt;
        else return _noteTxt.substring(0, 89) + "...";
    }

    public final void setNoteTxt(String noteTxt) {

        this._noteTxt = noteTxt;
        if (noteTxt != null && noteTxt.length() > 4000) {
            String id = "noteId = null";
            if (_noteId != null) {
                id = "noteId = " + _noteId.toString();
            }
            validationMessages.put("noteTxt", new ValidationMessage("noteTxt",
                    "Note is too long (> 4000)",
                    ValidationMessage.Severity.ERROR, id));
        } else {
            validationMessages.remove("noteTxt");
        }
        this._noteTxt = noteTxt;
        setDirty(true);
    }

    public final String getNoteTypeCd() {
        return _noteTypeCd;
    }

    public final void setNoteTypeCd(String noteTypeCd) {
        this._noteTypeCd = noteTypeCd;
        setDirty(true);
    }

    public final Integer getUserId() {
        return _userId;
    }

    public final void setUserId(Integer userId) {
        if (userId != null) {
            validationMessages.remove("userId");
        } else {
            ValidationMessage msg = new ValidationMessage(_messages[0][0],
                    _messages[0][1]);
            validationMessages.put(_messages[0][0], msg);
        }
        this._userId = userId;
        setDirty(true);
    }

    public void populate(ResultSet rs) {
        try {
            setNoteId(AbstractDAO.getInteger(rs, "note_id"));
            setNoteTypeCd(rs.getString("note_type_cd"));
            setNoteTxt(rs.getString("note_txt"));
            setUserId(AbstractDAO.getInteger(rs, "user_id"));
            setDateEntered(rs.getTimestamp("date_entered"));
            setLastModified(AbstractDAO.getInteger(rs, "cn_lm"));

            setDirty(false);
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result
                + ((_dateEntered == null) ? 0 : _dateEntered.hashCode());
        result = PRIME * result + ((_noteId == null) ? 0 : _noteId.hashCode());
        result = PRIME * result
                + ((_noteTxt == null) ? 0 : _noteTxt.hashCode());
        result = PRIME * result
                + ((_noteTypeCd == null) ? 0 : _noteTypeCd.hashCode());
        result = PRIME * result + ((_userId == null) ? 0 : _userId.hashCode());
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

        final Note other = (Note) obj;

        // Either both null or equal values.
        if (((_noteId == null) && (other.getNoteId() != null))
                || ((_noteId != null) && (other.getNoteId() == null))
                || ((_noteId != null) && (other.getNoteId() != null) && !(_noteId
                        .equals(other.getNoteId())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_dateEntered == null) && (other.getDateEntered() != null))
                || ((_dateEntered != null) && (other.getDateEntered() == null))
                || ((_dateEntered != null) && (other.getDateEntered() != null) && !(_dateEntered
                        .equals(other.getDateEntered())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_noteTxt == null) && (other.getNoteTxt() != null))
                || ((_noteTxt != null) && (other.getNoteTxt() == null))
                || ((_noteTxt != null) && (other.getNoteTxt() != null) && !(_noteTxt
                        .equals(other.getNoteTxt())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_noteTypeCd == null) && (other.getNoteTypeCd() != null))
                || ((_noteTypeCd != null) && (other.getNoteTypeCd() == null))
                || ((_noteTypeCd != null) && (other.getNoteTypeCd() != null) && !(_noteTypeCd
                        .equals(other.getNoteTypeCd())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_userId == null) && (other.getUserId() != null))
                || ((_userId != null) && (other.getUserId() == null))
                || ((_userId != null) && (other.getUserId() != null) && !(_userId
                        .equals(other.getUserId())))) {
            return false;
        }

        return true;
    }
}
