package us.wy.state.deq.impact.database.dbObjects.continuousMonitoring;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

@SuppressWarnings("serial")
public class ContinuousMonitorNote extends Note {
	private Integer corrMonitorId;

	public ContinuousMonitorNote() {
		super();
	}

	public ContinuousMonitorNote(ContinuousMonitorNote old) {
		super(old);
		if (old != null) {
			setCorrMonitorId(old.getCorrMonitorId());
			setDirty(old.isDirty());
		}
	}

	public Integer getCorrMonitorId() {
		return corrMonitorId;
	}

	public void setCorrMonitorId(Integer corrMonitorId) {
		this.corrMonitorId = corrMonitorId;
	}

	@Override
	public void populate(ResultSet rs) {
		try {
			setCorrMonitorId(AbstractDAO.getInteger(rs, "corr_monitor_id"));
		} catch (SQLException sqle) {
			logger.error("Required field error");
		}
		super.populate(rs);
	}
	
}
