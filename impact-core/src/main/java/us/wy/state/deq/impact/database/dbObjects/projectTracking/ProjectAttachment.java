package us.wy.state.deq.impact.database.dbObjects.projectTracking;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;

@SuppressWarnings("serial")
public class ProjectAttachment extends BaseDB {
	
	private Integer trackingEventId;
	private Integer projectId;
	private Attachment attachment;
	
	public ProjectAttachment() {
		super();
		attachment = new Attachment();
	}
	
	public ProjectAttachment(ProjectAttachment projectAttachment) {
		super(projectAttachment);
		if(null != projectAttachment) {
			setTrackingEventId(projectAttachment.getTrackingEventId());
			setProjectId(projectAttachment.getProjectId());
			setAttachment(projectAttachment.getAttachment());
		}
	}

	public Integer getTrackingEventId() {
		return trackingEventId;
	}

	public void setTrackingEventId(Integer trackingEventId) {
		this.trackingEventId = trackingEventId;
	}
	
	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	
	public Attachment getAttachment() {
		return attachment;
	}

	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		if(null != rs) {
			setTrackingEventId(AbstractDAO.getInteger(rs, "event_id"));
			setProjectId(AbstractDAO.getInteger(rs, "project_id"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
			attachment.populate(rs);
		}
	}
	
	// for sorting to work on the jsp, need these getters
	public Integer getDocumentID() {
		return this.attachment.getDocumentID();
	}
	
	public String getDocTypeCd() {
		return this.attachment.getDocTypeCd();
	}
	
	public String getDescription() {
		return this.attachment.getDescription();
	}
	
	public Integer getLastModifiedBy() {
		return this.attachment.getLastModifiedBy();
	}
	
	public Timestamp getUploadDate() {
		return this.attachment.getUploadDate();
	}
	
}
