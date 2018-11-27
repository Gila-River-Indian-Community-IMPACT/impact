package us.wy.state.deq.impact.database.dbObjects.projectTracking;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.ProjectTrackingEventTypeDef;

@SuppressWarnings("serial")
public class ProjectTrackingEvent extends BaseDB {
	public final static String PAGE_VIEW_ID = "projectTrackingEventDetail:";
	
	private Integer eventId;
	private Integer projectId;
	private String eventTypeCd;
	private Timestamp eventDate;
	private String eventDescription;
	private String eventStatus;
	private String issuesToAddress;
	private Timestamp responseDueDate;
	private Integer addedByUserId;
	private String eventNbr;
	
	// info of the associated attachments
	private List<ProjectAttachmentInfo> associatedAttachmentsInfo = new ArrayList<ProjectAttachmentInfo>();
	
	public ProjectTrackingEvent() {
		super();
	}
	
	public ProjectTrackingEvent(ProjectTrackingEvent event) {
		super(event);
		if(null != event) {
			setEventId(event.getEventId());
			setProjectId(event.getProjectId());
			setEventTypeCd(event.getEventTypeCd());
			setEventDate(event.getEventDate());
			setEventDescription(event.getEventDescription());
			setEventStatus(event.getEventStatus());
			setIssuesToAddress(event.getIssuesToAddress());
			setResponseDueDate(event.getResponseDueDate());
			setAddedByUserId(event.getAddedByUserId());
			setEventNbr(event.getEventNbr());
			setAssociatedAttachmentsInfo(event.getAssociatedAttachmentsInfo());
		}
	}
	
	public Integer getEventId() {
		return eventId;
	}
	
	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}
	
	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getEventTypeCd() {
		return eventTypeCd;
	}

	public void setEventTypeCd(String eventTypeCd) {
		this.eventTypeCd = eventTypeCd;
	}

	public Timestamp getEventDate() {
		return eventDate;
	}

	public void setEventDate(Timestamp eventDate) {
		this.eventDate = eventDate;
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	public String getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(String eventStatus) {
		this.eventStatus = eventStatus;
	}
	
	public String getIssuesToAddress() {
		return issuesToAddress;
	}

	public void setIssuesToAddress(String issuesToAddress) {
		this.issuesToAddress = issuesToAddress;
	}

	public Timestamp getResponseDueDate() {
		return responseDueDate;
	}

	public void setResponseDueDate(Timestamp responseDueDate) {
		this.responseDueDate = responseDueDate;
	}
	
	public Integer getAddedByUserId() {
		return addedByUserId;
	}

	public void setAddedByUserId(Integer addedByUserId) {
		this.addedByUserId = addedByUserId;
	}
	
	public String getEventNbr() {
		return eventNbr;
	}

	public void setEventNbr(String eventNbr) {
		this.eventNbr = eventNbr;
	}
	
	public List<ProjectAttachmentInfo> getAssociatedAttachmentsInfo() {
		if (null == associatedAttachmentsInfo) {
			setAssociatedAttachmentsInfo(new ArrayList<ProjectAttachmentInfo>());
		}
		return associatedAttachmentsInfo;
	}

	public void setAssociatedAttachmentsInfo(
			List<ProjectAttachmentInfo> associatedAttachmentsInfo) {
		this.associatedAttachmentsInfo = associatedAttachmentsInfo;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		if(null != rs) {
			setEventId(AbstractDAO.getInteger(rs, "event_id"));
			setProjectId(AbstractDAO.getInteger(rs, "project_id"));
			setEventTypeCd(rs.getString("event_type_cd"));
			setEventDate(rs.getTimestamp("event_date"));
			setEventDescription(rs.getString("event_description"));
			setEventStatus(rs.getString("event_status"));
			setIssuesToAddress(rs.getString("issues_to_address"));
			setResponseDueDate(rs.getTimestamp("response_due_date"));
			setAddedByUserId(AbstractDAO.getInteger(rs, "added_by"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
			setEventNbr(rs.getString("event_nbr"));
			setNewObject(false);
		}
	}
	
	@Override
	public ValidationMessage[] validate() {
		validationMessages.clear();
		
		requiredFields();
		
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
	
	private void requiredFields() {
		requiredField(this.eventDescription, PAGE_VIEW_ID
				+ "eventDescription", "Event Description", "eventDescription");

		requiredField(this.eventDate, PAGE_VIEW_ID
				+ "eventDate", "Event Date",
				"eventDate");

		requiredField(this.eventTypeCd, PAGE_VIEW_ID
				+ "eventTypeCd", "Event Type", "eventTypeCd");

	}
	
	public String getEventTypeDesc() {
		return ProjectTrackingEventTypeDef.getData().getItems()
				.getDescFromAllItem(this.eventTypeCd);
	}
}
