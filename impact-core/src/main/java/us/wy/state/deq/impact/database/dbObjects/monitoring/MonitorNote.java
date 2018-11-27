package us.wy.state.deq.impact.database.dbObjects.monitoring;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

public class MonitorNote extends Note {

	private Integer _monitorId;

	public MonitorNote() {
		this.requiredField(_monitorId, "monitorId");
	}

	public MonitorNote(MonitorNote note) {
		super(note);
		setMonitorId(note.getMonitorId());
		setDirty(note.isDirty());
	}

	public final Integer getMonitorId() {
		return _monitorId;
	}

	public final void setMonitorId(Integer monitorId) {
		_monitorId = monitorId;
		this.requiredField(_monitorId, "monitorId");
		setDirty(true);
	}

	public final void populate(ResultSet rs) {

		try {
			super.populate(rs);
			setMonitorId(AbstractDAO.getInteger(rs, "MONITOR_ID"));
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
				+ ((_monitorId == null) ? 0 : _monitorId.hashCode());
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

		final MonitorNote other = (MonitorNote) obj;

		// Either both null or equal values.
		if (((_monitorId == null) && (other.getMonitorId() != null))
				|| ((_monitorId != null) && (other.getMonitorId() == null))
				|| ((_monitorId != null) && (other.getMonitorId() != null) && !(_monitorId
						.equals(other.getMonitorId())))) {

			return false;
		}
		return true;
	}
}
