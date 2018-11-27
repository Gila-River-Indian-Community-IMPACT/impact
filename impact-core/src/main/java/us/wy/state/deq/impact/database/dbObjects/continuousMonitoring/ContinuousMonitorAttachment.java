package us.wy.state.deq.impact.database.dbObjects.continuousMonitoring;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;

@SuppressWarnings("serial")
public class ContinuousMonitorAttachment extends Attachment {
	private Integer monitorId;

	public ContinuousMonitorAttachment() {
		super();
	}

	public ContinuousMonitorAttachment(ContinuousMonitorAttachment old) {
		super(old);
		if (old != null) {
			setMonitorId(old.getMonitorId());
			if (monitorId != null) {
				setObjectId(monitorId.toString());
			}
		}
	}

	public ContinuousMonitorAttachment(Attachment doc) {
		super(doc);
		if (doc != null && doc.getObjectId() != null) {
			setMonitorId(Integer.decode(doc.getObjectId()));
		}
	}

	public final Integer getMonitorId() {
		return monitorId;
	}

	public final void setMonitorId(Integer monitorId) {
		this.monitorId = monitorId;
		if (monitorId != null) {
			setObjectId(monitorId.toString());
		} else {
			setObjectId(null);
		}
	}

	@Override
	public void populate(ResultSet rs) {
		super.populate(rs);
		try {
			setMonitorId(AbstractDAO.getInteger(rs, "MONITOR_ID"));
		} catch (SQLException e) {
			logger.error("Required field error");
		}
	}
}
