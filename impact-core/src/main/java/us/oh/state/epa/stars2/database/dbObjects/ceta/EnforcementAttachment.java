package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;

@SuppressWarnings("serial")
public class EnforcementAttachment extends Attachment {
	private Integer enforcementId;
	
	public EnforcementAttachment() {
		super();
	}

	public EnforcementAttachment(EnforcementAttachment old) {
		super(old);
		if (old != null) {
			setEnforcementId(old.getEnforcementId());
			if (enforcementId != null) {
				setObjectId(enforcementId.toString());
			}
		}
	}
	
	public EnforcementAttachment(Attachment doc) {
		super(doc);
		if (doc != null && doc.getObjectId() != null) {
			setEnforcementId(Integer.decode(doc.getObjectId()));
		}
	}

	public final Integer getEnforcementId() {
		return enforcementId;
	}

	public final void setEnforcementId(Integer enforcementId) {
		this.enforcementId = enforcementId;
		if (enforcementId != null) {
			setObjectId(enforcementId.toString());
		} else {
			setObjectId(null);
		}
	}

	@Override
	public void populate(ResultSet rs) {
		super.populate(rs);
		try {
			setEnforcementId(AbstractDAO.getInteger(rs, "ENFORCEMENT_ACTION_ID"));
		} catch (SQLException e) {
            logger.error("Required field error");
        }
	}
}
