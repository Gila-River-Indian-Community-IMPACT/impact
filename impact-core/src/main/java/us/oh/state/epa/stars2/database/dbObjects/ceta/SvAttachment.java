package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;

@SuppressWarnings("serial")
public class SvAttachment extends Attachment {
	private Integer visitId;
	
	public SvAttachment() {
		super();
	}

	public SvAttachment(SvAttachment old) {
		super(old);
		if (old != null) {
			setVisitId(old.getVisitId());
			if (visitId != null) {
				setObjectId(visitId.toString());
			}
		}
	}
	
	public SvAttachment(Attachment doc) {
		super(doc);
		if (doc != null && doc.getObjectId() != null && doc.getObjectId().length() != 0) {
			setVisitId(Integer.decode(doc.getObjectId()));
		}
	}

	public final Integer getVisitId() {
		return visitId;
	}

	public final void setVisitId(Integer visitId) {
		this.visitId = visitId;
		if (visitId != null) {
			setObjectId(visitId.toString());
		} else {
			setObjectId(null);
		}
	}

	@Override
	public void populate(ResultSet rs) {
		super.populate(rs);
		try {
			setVisitId(AbstractDAO.getInteger(rs, "visit_id"));
		} catch (SQLException e) {
            logger.error("Required field error");
        }
	}
}
