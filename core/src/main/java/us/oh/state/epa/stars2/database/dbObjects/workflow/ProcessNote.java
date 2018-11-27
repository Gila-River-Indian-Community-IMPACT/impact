package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * ProcessNote
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version $Revision: 1.7 $
 * @author $Author: cmeier $
 */
public class ProcessNote extends BaseDB {
    private Integer noteId;
    private Integer processId;
    private Integer userId;
    private Timestamp postedDt;
    private String note;

    /**
     * 
     */
    public ProcessNote() {
        super();
    }
    
    public final Integer getNoteTxtLength() {
        Integer ret = 0;
        if (note != null) {
            ret = note.length();
        }

        return ret;
    }

    /**
     * @param old
     */
    public ProcessNote(ProcessNote old) {
        super(old);
        if (old != null) {
            setNoteId(old.getNoteId());
            setProcessId(old.getProcessId());
            setUserId(old.getUserId());
            setPostedDt(old.getPostedDt());
            setNote(old.getNote());
        }
    }

    public final String getNote() {
        return note;
    }

    public final void setNote(String note) {
        this.note = note;
    }

    public final Integer getNoteId() {
        return noteId;
    }

    public final void setNoteId(Integer noteId) {
        this.noteId = noteId;
    }

    public final Timestamp getPostedDt() {
        return postedDt;
    }

    public final void setPostedDt(Timestamp postedDt) {
        this.postedDt = postedDt;
    }

    public final Integer getUserId() {
        return userId;
    }

    public final void setUserId(Integer userId) {
        this.userId = userId;
    }

    public final Integer getProcessId() {
        return processId;
    }

    public final void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public final void populate(ResultSet rs) {
        try {
            setNoteId(AbstractDAO.getInteger(rs, "NOTE_ID"));
            setProcessId(AbstractDAO.getInteger(rs, "process_id"));
            setUserId(AbstractDAO.getInteger(rs, "USER_ID"));
            setPostedDt(rs.getTimestamp("POSTED_DT"));
            setNote(rs.getString("NOTE"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }
}
