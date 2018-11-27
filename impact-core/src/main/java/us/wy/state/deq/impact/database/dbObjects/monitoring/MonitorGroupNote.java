package us.wy.state.deq.impact.database.dbObjects.monitoring;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

public class MonitorGroupNote extends Note {

	private Integer _monitorGroupId;

	public MonitorGroupNote() {
		this.requiredField(_monitorGroupId, "monitorGroupId");
	}

	public MonitorGroupNote(MonitorGroupNote note) {
		super(note);
		setMonitorGroupId(note.getMonitorGroupId());
		setDirty(note.isDirty());
	}

	public final Integer getMonitorGroupId() {
		return _monitorGroupId;
	}

	public final void setMonitorGroupId(Integer monitorGroupId) {
		_monitorGroupId = monitorGroupId;
		this.requiredField(_monitorGroupId, "monitorGroupId");
		setDirty(true);
	}

	public final void populate(ResultSet rs) {

		try {
			super.populate(rs);
			setMonitorGroupId(AbstractDAO.getInteger(rs, "MONITOR_GROUP_ID"));
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
				+ ((_monitorGroupId == null) ? 0 : _monitorGroupId.hashCode());
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

		final MonitorGroupNote other = (MonitorGroupNote) obj;

		// Either both null or equal values.
		if (((_monitorGroupId == null) && (other.getMonitorGroupId() != null))
				|| ((_monitorGroupId != null) && (other.getMonitorGroupId() == null))
				|| ((_monitorGroupId != null)
						&& (other.getMonitorGroupId() != null) && !(_monitorGroupId
							.equals(other.getMonitorGroupId())))) {

			return false;
		}
		return true;
	}
}
