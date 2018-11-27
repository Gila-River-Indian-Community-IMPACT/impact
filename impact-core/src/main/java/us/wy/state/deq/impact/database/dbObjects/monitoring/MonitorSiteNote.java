package us.wy.state.deq.impact.database.dbObjects.monitoring;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

public class MonitorSiteNote extends Note {

	private Integer _monitorSiteId;

	public MonitorSiteNote() {
		this.requiredField(_monitorSiteId, "monitorSiteId");
	}

	public MonitorSiteNote(MonitorSiteNote note) {
		super(note);
		setMonitorSiteId(note.getMonitorSiteId());
		setDirty(note.isDirty());
	}

	public final Integer getMonitorSiteId() {
		return _monitorSiteId;
	}

	public final void setMonitorSiteId(Integer monitorSiteId) {
		_monitorSiteId = monitorSiteId;
		this.requiredField(_monitorSiteId, "monitorSiteId");
		setDirty(true);
	}

	public final void populate(ResultSet rs) {

		try {
			super.populate(rs);
			setMonitorSiteId(AbstractDAO.getInteger(rs, "MONITOR_SITE_ID"));
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
				+ ((_monitorSiteId == null) ? 0 : _monitorSiteId.hashCode());
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

		final MonitorSiteNote other = (MonitorSiteNote) obj;

		// Either both null or equal values.
		if (((_monitorSiteId == null) && (other.getMonitorSiteId() != null))
				|| ((_monitorSiteId != null) && (other.getMonitorSiteId() == null))
				|| ((_monitorSiteId != null)
						&& (other.getMonitorSiteId() != null) && !(_monitorSiteId
							.equals(other.getMonitorSiteId())))) {

			return false;
		}
		return true;
	}
}
