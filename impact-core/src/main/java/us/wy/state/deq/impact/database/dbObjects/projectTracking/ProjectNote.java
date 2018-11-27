package us.wy.state.deq.impact.database.dbObjects.projectTracking;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

@SuppressWarnings("serial")
public class ProjectNote extends Note {
	
	private Integer projectId;
	
	public ProjectNote() {
		super();
	}
	
	public ProjectNote(ProjectNote note) {
		super(note);
		if(null != note) {
			setProjectId(note.getProjectId());
		}
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	
	@Override
	public void populate(ResultSet rs) {
		if(null != rs) {
			try {
				super.populate(rs);
				setProjectId(AbstractDAO.getInteger(rs, "project_id"));
				setNewObject(false);
				setDirty(false);
			} catch(SQLException se) {
				logger.error("Required field error");
			}

		}
	}
	
}
