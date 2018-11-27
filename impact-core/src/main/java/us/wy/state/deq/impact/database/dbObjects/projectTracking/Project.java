package us.wy.state.deq.impact.database.dbObjects.projectTracking;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.webcommon.bean.Stars2Object;
import us.wy.state.deq.impact.util.ImpactUtils;

@SuppressWarnings("serial")
public class Project extends BaseDB {
	
	public static final String COMMON_ATTRIBUTES_PAGE_VIEW_ID = "commonAttributes:";

	private Integer projectId;
	private String projectNumber;
	private String projectName;
	private String projectDescription;
	private String projectTypeCd;
	private String projectStateCd;
	private Integer creatorId;
	private Date createdDate;
	private String projectSummary;
	private String extAgencyWebsiteUri;
	private List<String> projectDivisionCds = new ArrayList<String>();
	private List<Stars2Object> projectLeadUserIds = new ArrayList<Stars2Object>();
	private List<ProjectTrackingEvent> trackingEvents = new ArrayList<ProjectTrackingEvent>();
	private List<ProjectNote> notes = new ArrayList<ProjectNote>();
	private List<ProjectAttachment> attachments = new ArrayList<ProjectAttachment>();
	
		
	public Project() {
		super();
	}
	
	public Project(Project p) {
		super();
		if(null != p) {
			setProjectId(p.getProjectId());
			setProjectNumber(p.getProjectNumber());
			setProjectName(p.getProjectName());
			setProjectDescription(p.getProjectDescription());
			setProjectTypeCd(p.getProjectTypeCd());
			setProjectStateCd(p.getProjectStateCd());
			setCreatorId(p.getCreatorId());
			setCreatedDate(p.getCreatedDate());
			setProjectSummary(p.getProjectSummary());
			setExtAgencyWebsiteUri(p.getExtAgencyWebsiteUri());
			setProjectDivisionCds(p.getProjectDivisionCds());
			setProjectLeadUserIds(p.getProjectLeadUserIds());
			setTrackingEvents(p.getTrackingEvents());
			setNotes(p.getNotes());
			setAttachments(p.getAttachments());
		}
	}

	public boolean isHasAttachments() {
		return getAttachments().size() > 0;
	}

	
	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getProjectNumber() {
		return projectNumber;
	}

	public void setProjectNumber(String projectNumber) {
		this.projectNumber = projectNumber;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	public String getProjectTypeCd() {
		return projectTypeCd;
	}

	public void setProjectTypeCd(String projectTypeCd) {
		this.projectTypeCd = projectTypeCd;
	}

	public String getProjectStateCd() {
		return projectStateCd;
	}

	public void setProjectStateCd(String projectStateCd) {
		this.projectStateCd = projectStateCd;
	}
	
	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	public String getShortProjectName() {
		String shortProjectName = this.projectName;
		
		if(null != this.projectName) {
			shortProjectName = this.projectName.length() > 100 
					? this.projectName.substring(0, 100) + "..." : this.projectName;
		} 
		
		return shortProjectName;
	}
	
	public String getProjectSummary() {
		return projectSummary;
	}

	public void setProjectSummary(String projectSummary) {
		this.projectSummary = projectSummary;
	}

	public String getExtAgencyWebsiteUri() {
		return ImpactUtils.toUri(extAgencyWebsiteUri);
	}

	public void setExtAgencyWebsiteUri(String extAgencyWebsiteUri) {
		this.extAgencyWebsiteUri = extAgencyWebsiteUri;
	}
	
	public List<String> getProjectDivisionCds() {
		if(null == projectDivisionCds) {
			setProjectDivisionCds(new ArrayList<String>());
		}
		return projectDivisionCds;
	}

	public void setProjectDivisionCds(List<String> projectDivisionCds) {
		this.projectDivisionCds = projectDivisionCds;
	}
	
	public List<Stars2Object> getProjectLeadUserIds() {
		if(null == projectLeadUserIds) {
			setProjectLeadUserIds(new ArrayList<Stars2Object>());
		}
		return projectLeadUserIds;
	}

	public void setProjectLeadUserIds(List<Stars2Object> projectLeadUserIds) {
		this.projectLeadUserIds = projectLeadUserIds;
	}
	
	public List<ProjectTrackingEvent> getTrackingEvents() {
		if(null == trackingEvents) {
			setTrackingEvents(new ArrayList<ProjectTrackingEvent>());
		}
		return trackingEvents;
	}

	public void setTrackingEvents(List<ProjectTrackingEvent> trackingEvents) {
		this.trackingEvents = trackingEvents;
	}
	
	public List<ProjectNote> getNotes() {
		if(null == notes) {
			setNotes(new ArrayList<ProjectNote>());
		}
		return notes;
	}

	public void setNotes(List<ProjectNote> notes) {
		this.notes = notes;
	}
	
	public List<ProjectAttachment> getAttachments() {
		if(null == attachments) {
			setAttachments(new ArrayList<ProjectAttachment>());
		}
		return attachments;
	}

	public void setAttachments(List<ProjectAttachment> attachments) {
		this.attachments = attachments;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		if(null != rs) {
			setProjectId(AbstractDAO.getInteger(rs, "project_id"));
			setProjectNumber(rs.getString("project_nbr"));
			setProjectName(rs.getString("project_name"));
			setProjectDescription(rs.getString("project_dsc"));
			setProjectTypeCd(rs.getString("project_type_cd"));
			setProjectStateCd(rs.getString("project_state_cd"));
			setCreatorId(AbstractDAO.getInteger(rs, "creator_id"));
			setCreatedDate(rs.getDate("created_date"));
			setProjectSummary(rs.getString("project_summary"));
			setExtAgencyWebsiteUri(rs.getString("ext_agency_website_uri"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
		}
	}
	
	@Override
	public ValidationMessage[] validate() {
		validationMessages.clear();
		
		requiredFields();
		
		// check for blank and duplicate values for project lead(s)
		Set<Integer> ids = new HashSet<Integer>();
		for(Integer obj : Stars2Object.fromStar2IntObject(this.projectLeadUserIds)) {
			String fieldName = COMMON_ATTRIBUTES_PAGE_VIEW_ID + "projectLeadUserIds";
			if(null == obj || obj.intValue() == 0) {
				validationMessages
						.put(fieldName,
								new ValidationMessage(
										fieldName,
										"Empty value in a row of the Project Lead(s) table",
										ValidationMessage.Severity.ERROR,
										"projectLeadUserIds"));
			} else {
				if(ids.contains(obj)) {
					validationMessages
					.put(fieldName,
							new ValidationMessage(
									fieldName,
									"Duplicate value in Project Lead(s) table",
									ValidationMessage.Severity.ERROR,
									"projectLeadUserIds"));
				} else {
					ids.add(obj);
				}
			}
		}
		
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
	
	private void requiredFields() {
		// project name
		requiredField(this.projectName, COMMON_ATTRIBUTES_PAGE_VIEW_ID
				+ "projectName", "Project Name", "projectName");

		// project description
		requiredField(this.projectDescription, COMMON_ATTRIBUTES_PAGE_VIEW_ID
				+ "projectDescription", "Project Description",
				"projectDescription");

		// project type
		requiredField(this.projectTypeCd, COMMON_ATTRIBUTES_PAGE_VIEW_ID
				+ "projectTypeCd", "Project Type", "projectTypeCd");

		// project status
		requiredField(this.projectStateCd, COMMON_ATTRIBUTES_PAGE_VIEW_ID
				+ "projectStateCd", "Project Status", "projectStateCd");
	}
	
	public void addProjectLead() {
		if(null == projectLeadUserIds) {
			setProjectLeadUserIds(new ArrayList<Stars2Object>());
		}
		
		projectLeadUserIds.add(new Stars2Object());
	}
	
	public boolean isHasEvent() {
		return !this.trackingEvents.isEmpty();
	}
}
