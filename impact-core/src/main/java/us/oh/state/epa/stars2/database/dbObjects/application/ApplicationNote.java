package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

@SuppressWarnings("serial")
public class ApplicationNote extends Note {
    private Integer applicationId;

    /**
     * Copy Constructor
     * 
     * @param applicationNote
     *            a <code>ApplicationNote</code> object
     */
    public ApplicationNote(ApplicationNote applicationNote) {
        super(applicationNote);

        if (applicationNote != null) {
            this.applicationId = applicationNote.applicationId;
        }
    }

    public ApplicationNote() {

    }

    public final Integer getApplicationId() {
        return applicationId;
    }

    public final void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public final void populate(ResultSet rs) {
        try {
            setApplicationId(AbstractDAO.getInteger(rs, "application_id"));

            super.populate(rs);
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }
}
