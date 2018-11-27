package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

@SuppressWarnings("serial")
public class EnforcementNote extends Note {
	private Integer enforcementId;
	
	public EnforcementNote() {
		super();
	}
	
	public EnforcementNote(EnforcementNote old) {
		super(old);
		if (old != null) {
			setEnforcementId(old.getEnforcementId());
			setDirty(old.isDirty());
		}
	}

	public final Integer getEnforcementId() {
		return enforcementId;
	}

	public final void setEnforcementId(Integer enforcementId) {
		this.enforcementId = enforcementId;
	}

	@Override
	public void populate(ResultSet rs) {
		try {
			setEnforcementId(AbstractDAO.getInteger(rs, "enforcement_action_id"));
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
		super.populate(rs);
	}

}
