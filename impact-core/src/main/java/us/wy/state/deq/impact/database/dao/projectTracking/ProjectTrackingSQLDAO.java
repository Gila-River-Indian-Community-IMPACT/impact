package us.wy.state.deq.impact.database.dao.projectTracking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.def.ProjectTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.wy.state.deq.impact.database.dao.ProjectTrackingDAO;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.Budget;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.Contract;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.GrantProject;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.LetterProject;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.NEPAProject;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.Project;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectAttachment;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectAttachmentInfo;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectNote;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectSearchResult;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectTrackingEvent;
import us.wy.state.deq.impact.webcommon.projectTracking.ProjectTrackingSearchCriteria;

@Repository
public class ProjectTrackingSQLDAO extends AbstractDAO implements ProjectTrackingDAO {
	
	@Override
	public Project createProject(Project project) throws DAOException {
		checkNull(project);

		Project ret = project;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.createProject", true);

		Integer id = nextSequenceVal("S_Project_Id", project.getProjectId());

		int i = 1;
		connHandler.setInteger(i++, id);
		connHandler.setString(i++, project.getProjectName());
		connHandler.setString(i++, project.getProjectDescription());
		connHandler.setString(i++, project.getProjectTypeCd());
		connHandler.setString(i++, project.getProjectStateCd());
		connHandler.setInteger(i++, project.getCreatorId());
		connHandler.setDate(i++, new java.sql.Date(project.getCreatedDate()
				.getTime()));

		connHandler.update();

		ret.setProjectId(id);
		ret.setLastModified(1);

		return ret;
	}

	@Override
	public Project retrieveProject(Integer projectId) throws DAOException {
		checkNull(projectId);
		
		Project project = null;
		
		Connection conn = getReadOnlyConnection();
		PreparedStatement pStmt = null;
		Class<? extends Project> projectClass = Project.class; // default
																// project class
																// type
		try {
			pStmt = conn
					.prepareStatement(loadSQL("ProjectTrackingSQL.retrieveProject"));
			pStmt.setInt(1, projectId.intValue());

			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) {
				String projectTypeCd = rs.getString("project_type_cd");
				if (!Utility.isNullOrEmpty(projectTypeCd)) {
					if (projectTypeCd.equals(ProjectTypeDef.NEPA)) {
						projectClass = NEPAProject.class;
					} else if (projectTypeCd.equals(ProjectTypeDef.GRANTS)) {
						projectClass = GrantProject.class;
					} else if (projectTypeCd.equals(ProjectTypeDef.LETTERS)) {
						projectClass = LetterProject.class;
					} else if (projectTypeCd.equals(ProjectTypeDef.CONTRACTS)) {
						projectClass = Contract.class;
					}
					project = projectClass.newInstance();
					project.populate(rs);
				} else {
					throw new DAOException(
							"Unknown or invalid value for project type (projectTypeCd)");
				}
			}
		}catch(Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}
		
		return project;
	}

	@Override
	public List<String> createProjectDivisions(Integer projectId,
			List<String> projectDivisionCds) throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.createProjectDivision", true);
		
		connHandler.setInteger(1, projectId);
		try{
			for(String divisionCode: projectDivisionCds) {
				connHandler.setString(2, divisionCode);
				connHandler.updateNoClose();
			}
		}finally {
			connHandler.close();
		}
		
		return projectDivisionCds;
	}

	@Override
	public List<String> retrieveProjectDivisionCds(Integer projectId)
			throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.retrieveProjectDivisionCds", true);
		
		connHandler.setInteger(1, projectId);
		
		return connHandler.retrieveJavaObjectArray(String.class);
	}

	@Override
	public boolean updateProject(Project project) throws DAOException {
		checkNull(project);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.updateProject", true);

		int i = 1;
		connHandler.setString(i++, project.getProjectName());
		connHandler.setString(i++, project.getProjectDescription());
		connHandler.setString(i++, project.getProjectTypeCd());
		connHandler.setString(i++, project.getProjectStateCd());
		connHandler.setString(i++, project.getProjectSummary());
		connHandler.setString(i++, project.getExtAgencyWebsiteUri());
		connHandler.setInteger(i++, project.getLastModified() + 1);
		
		connHandler.setInteger(i++, project.getProjectId());
		connHandler.setInteger(i++, project.getLastModified());
		
		return connHandler.update();
	}

	@Override
	public void deleteProject(Integer projectId) throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteProject", true);
		
		connHandler.setInteger(1, projectId);
		
		connHandler.remove();
	}

	@Override
	public void deleteProjectDivisions(Integer projectId) throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteProjectDivisions", true);
		
		connHandler.setInteger(1, projectId);
		
		connHandler.remove();
	}

	@Override
	public NEPAProject createNEPAProject(NEPAProject nepaProject)
			throws DAOException {
		checkNull(nepaProject);
		checkNull(nepaProject.getProjectId());
		
		NEPAProject ret = nepaProject;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.createNEPAProject", true);

		int i = 1;
		connHandler.setInteger(i++, nepaProject.getProjectId());
		connHandler.setString(i++, nepaProject.getCategoryCd());
		connHandler.setString(i++, nepaProject.getProjectStageCd());
		connHandler.setString(i++, nepaProject.getExtAgencyContact());
		connHandler.setString(i++, nepaProject.getExtAgencyContactPhone());
		connHandler.setString(i++, nepaProject.getModelingRequired());
		connHandler.setInteger(i++, nepaProject.getShapeId());
		connHandler.update();

		ret.setLastModified(1);

		return ret;
	}

	@Override
	public boolean updateNEPAProject(NEPAProject nepaProject) throws DAOException {
		checkNull(nepaProject);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.updateNEPAProject", true);

		int i = 1;
		connHandler.setString(i++, nepaProject.getCategoryCd());
		connHandler.setString(i++, nepaProject.getProjectStageCd());
		connHandler.setString(i++, nepaProject.getExtAgencyContact());
		connHandler.setString(i++, nepaProject.getExtAgencyContactPhone());
		connHandler.setString(i++, nepaProject.getModelingRequired());
		connHandler.setInteger(i++, nepaProject.getShapeId());
		connHandler.setInteger(i++, nepaProject.getLastModified() + 1);

		connHandler.setInteger(i++, nepaProject.getProjectId());
		connHandler.setInteger(i++, nepaProject.getLastModified());

		return connHandler.update();
	}
	
	@Override
	public void deleteNEPAProject(Integer projectId) throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteNEPAProject", true);
		
		connHandler.setInteger(1, projectId);
		
		connHandler.remove();
	}
	
	@Override
	public List<String> createNEPAProjectLevelXrefs(Integer projectId,
			List<String> levelCds) throws DAOException {
		checkNull(projectId);
		checkNull(levelCds);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.createNEPAProjectLevelXref", true);
		
		connHandler.setInteger(1, projectId);
		try{
			for(String code: levelCds) {
				connHandler.setString(2, code);
				connHandler.updateNoClose();
			}
		}finally {
			connHandler.close();
		}
		
		return levelCds;
	}
	
	@Override
	public List<String> retrieveNEPAProjectLevelCds(Integer projectId)
			throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
 				"ProjectTrackingSQL.retrieveNEPAProjectLevelCds", true);
		
		connHandler.setInteger(1, projectId);
		
		return connHandler.retrieveJavaObjectArray(String.class);
	}
	
	@Override
	public void deleteNEPAProjectLevelXrefs(Integer projectId)
			throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteNEPAProjectLevelXrefs", true);
		
		connHandler.setInteger(1, projectId);
		
		connHandler.remove();
	}

	@Override
	public List<String> createNEPAProjectLeadAgencyXrefs(Integer projectId,
			List<String> leadAgencyCds) throws DAOException {
		checkNull(projectId);
		checkNull(leadAgencyCds);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.createNEPAProjectLeadAgencyXref", true);
		
		connHandler.setInteger(1, projectId);
		try{
			for(String code: leadAgencyCds) {
				connHandler.setString(2, code);
				connHandler.updateNoClose();
			}
		}finally {
			connHandler.close();
		}
		
		return leadAgencyCds;
	}

	@Override
	public List<String> retrieveNEPAProjectLeadAgencyCds(Integer projectId)
			throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.retrieveNEPAProjectLeadAgencyCds", true);
		
		connHandler.setInteger(1, projectId);
		
		return connHandler.retrieveJavaObjectArray(String.class);
	}

	@Override
	public void deleteNEPAProjectLeadAgencyXrefs(Integer projectId)
			throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteNEPAProjectLeadAgencyXrefs", true);
		
		connHandler.setInteger(1, projectId);
		
		connHandler.remove();
	}

	@Override
	public List<String> createNEPAProjectBLMFieldOfficeXrefs(Integer projectId,
			List<String> BLMFieldOfficeCds) throws DAOException {
		checkNull(projectId);
		checkNull(BLMFieldOfficeCds);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.createNEPAProjectBLMFieldOfficeXref", true);
		
		connHandler.setInteger(1, projectId);
		try{
			for(String code: BLMFieldOfficeCds) {
				connHandler.setString(2, code);
				connHandler.updateNoClose();
			}
		}finally {
			connHandler.close();
		}
		
		return BLMFieldOfficeCds;
	}

	@Override
	public List<String> retrieveNEPAProjectBLMFieldOfficeCds(Integer projectId)
			throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.retrieveNEPAProjectBLMFieldOfficeCds", true);
		
		connHandler.setInteger(1, projectId);
		
		return connHandler.retrieveJavaObjectArray(String.class);
	}

	@Override
	public void deleteNEPAProjectBLMFieldOfficeXrefs(Integer projectId)
			throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteNEPAProjectBLMFieldOfficeXrefs", true);
		
		connHandler.setInteger(1, projectId);
		
		connHandler.remove();
	}

	@Override
	public List<String> createNEPAProjectNationalForestXrefs(Integer projectId,
			List<String> nationalForestCds) throws DAOException {
		checkNull(projectId);
		checkNull(nationalForestCds);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.createNEPAProjectNationalForestXref", true);
		
		connHandler.setInteger(1, projectId);
		try{
			for(String code: nationalForestCds) {
				connHandler.setString(2, code);
				connHandler.updateNoClose();
			}
		}finally {
			connHandler.close();
		}
		
		return nationalForestCds;
	}

	@Override
	public List<String> retrieveNEPAProjectNationalForestCds(Integer projectId)
			throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.retrieveNEPAProjectNationalForestCds", true);
		
		connHandler.setInteger(1, projectId);
		
		return connHandler.retrieveJavaObjectArray(String.class);
	}

	@Override
	public void deleteNEPAProjectNationalForestXrefs(Integer projectId)
			throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteNEPAProjectNationalForestXrefs", true);
		
		connHandler.setInteger(1, projectId);
		
		connHandler.remove();
	}

	@Override
	public List<String> createNEPAProjectNationalParkXrefs(Integer projectId,
			List<String> nationalParkCds) throws DAOException {
		checkNull(projectId);
		checkNull(nationalParkCds);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.createNEPAProjectNationalParkXref", true);
		
		connHandler.setInteger(1, projectId);
		try{
			for(String code: nationalParkCds) {
				connHandler.setString(2, code);
				connHandler.updateNoClose();
			} 
		}finally {
			connHandler.close();
		}
		
		return nationalParkCds;
	}

	@Override
	public List<String> retrieveNEPAProjectNationalParkCds(Integer projectId)
			throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.retrieveNEPAProjectNationalParkCds", true);
		
		connHandler.setInteger(1, projectId);
		
		return connHandler.retrieveJavaObjectArray(String.class);
	}

	@Override
	public void deleteNEPAProjectNationalParkXrefs(Integer projectId)
			throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteNEPAProjectNationalParkXrefs", true);
		
		connHandler.setInteger(1, projectId);
		
		connHandler.remove();
	}

	@Override
	public GrantProject createGrantProject(GrantProject grantProject)
			throws DAOException {
		checkNull(grantProject);
		checkNull(grantProject.getProjectId());

		GrantProject ret = grantProject;
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.createGrantProject", true);

		int i = 1;
		connHandler.setInteger(i++, grantProject.getProjectId());
		connHandler.setString(i++, grantProject.getOutreachCategoryCd());
		connHandler.setString(i++, grantProject.getGrantStatusCd());
		connHandler.setBigDecimal(i++, grantProject.getTotalAmount());

		connHandler.update();

		ret.setLastModified(1);

		return ret;
	}

	@Override
	public boolean updateGrantProject(GrantProject grantProject)
			throws DAOException {
		checkNull(grantProject);
		checkNull(grantProject.getProjectId());

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.updateGrantProject", true);

		int i = 1;
		connHandler.setString(i++, grantProject.getOutreachCategoryCd());
		connHandler.setString(i++, grantProject.getGrantStatusCd());
		connHandler.setBigDecimal(i++, grantProject.getTotalAmount());
		connHandler.setInteger(i++, grantProject.getLastModified() + 1);

		connHandler.setInteger(i++, grantProject.getProjectId());
		connHandler.setInteger(i++, grantProject.getLastModified());

		return connHandler.update();
	}

	@Override
	public void deleteGrantProject(Integer projectId) throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteGrantProject", true);

		connHandler.setInteger(1, projectId);
		
		connHandler.remove();
	}

	@Override
	public LetterProject createLetterProject(LetterProject letterProject)
			throws DAOException {
		checkNull(letterProject);
		checkNull(letterProject.getProjectId());

		LetterProject ret = letterProject;
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.createLetterProject", true);

		int i = 1;
		connHandler.setInteger(i++, letterProject.getProjectId());
		connHandler.setString(i++, letterProject.getLetterTypeCd());

		connHandler.update();

		ret.setLastModified(1);

		return ret;
	}

	@Override
	public boolean updateLetterProject(LetterProject letterProject)
			throws DAOException {
		checkNull(letterProject);
		checkNull(letterProject.getProjectId());

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.updateLetterProject", true);

		int i = 1;
		connHandler.setString(i++, letterProject.getLetterTypeCd());
		connHandler.setInteger(i++, letterProject.getLastModified() + 1);

		connHandler.setInteger(i++, letterProject.getProjectId());
		connHandler.setInteger(i++, letterProject.getLastModified());

		return connHandler.update();
	}

	@Override
	public void deleteLetterProject(Integer projectId) throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteLetterProject", true);

		connHandler.setInteger(1, projectId);
		
		connHandler.remove();
	}

	@Override
	public List<Integer> createProjectLeadXrefs(Integer projectId,
			List<Integer> userIds) throws DAOException {
		checkNull(projectId);
		checkNull(userIds);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.createProjectLeadXref", true);
		
		connHandler.setInteger(1, projectId);
		try{
			for(Integer id: userIds) {
				connHandler.setInteger(2, id);
				connHandler.updateNoClose();
			}
		}finally {
			connHandler.close();
		}
		
		return userIds;
	}

	@Override
	public List<Integer> retrieveProjectLeadUserIds(Integer projectId)
			throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.retrieveProjectLeadUserIds", true);
		
		connHandler.setInteger(1, projectId);
		
		return connHandler.retrieveJavaObjectArray(Integer.class);
	}

	@Override
	public void deleteProjectLeadXrefs(Integer projectId) throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteProjectLeadXrefs", true);
		
		connHandler.setInteger(1, projectId);
		
		connHandler.remove();
	}

	@Override
	public List<Integer> createGrantProjectAccountantXrefs(Integer projectId,
			List<Integer> userIds) throws DAOException {
		checkNull(projectId);
		checkNull(userIds);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.createGrantProjectAccountantXref", true);

		connHandler.setInteger(1, projectId);
		try {
			for (Integer id : userIds) {
				connHandler.setInteger(2, id);
				connHandler.updateNoClose();
			}
		} finally {
			connHandler.close();
		}

		return userIds;
	}

	@Override
	public List<Integer> retrieveGrantProjectAccountantUserIds(Integer projectId)
			throws DAOException {
		checkNull(projectId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.retrieveGrantProjectAccountantUserIds",
				true);

		connHandler.setInteger(1, projectId);

		return connHandler.retrieveJavaObjectArray(Integer.class);
	}

	@Override
	public void deleteGrantProjectAccountantXrefs(Integer projectId)
			throws DAOException {
		checkNull(projectId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteGrantProjectAccountantXrefs", true);

		connHandler.setInteger(1, projectId);

		connHandler.remove();
	}

	@Override
	public ProjectTrackingEvent createProjectTrackingEvent(
			ProjectTrackingEvent event) throws DAOException {
		checkNull(event);

		ProjectTrackingEvent ret = event;
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.createProjectTrackingEvent", true);
		Integer id = nextSequenceVal("S_project_tracking_event_id",
				event.getEventId());

		int i = 1;
		connHandler.setInteger(i++, id);
		connHandler.setInteger(i++, event.getProjectId());
		connHandler.setString(i++, event.getEventTypeCd());
		connHandler.setTimestamp(i++, event.getEventDate());
		connHandler.setString(i++, event.getEventDescription());
		connHandler.setString(i++, event.getEventStatus());
		connHandler.setString(i++, event.getIssuesToAddress());
		connHandler.setTimestamp(i++, event.getResponseDueDate());
		connHandler.setInteger(i++, event.getAddedByUserId());

		connHandler.update();

		ret.setEventId(id);
		ret.setLastModified(1);

		return ret;
	}

	@Override
	public ProjectTrackingEvent retrieveProjectTrackingEvent(Integer eventId)
			throws DAOException {
		checkNull(eventId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.retrieveProjectTrackingEvent",
				true);
		connHandler.setInteger(1, eventId);
		
		return (ProjectTrackingEvent) connHandler
				.retrieve(ProjectTrackingEvent.class);
	}
	
	@Override
	public List<ProjectTrackingEvent> retrieveProjectTrackingEventsByProjectId(
			Integer projectId) throws DAOException {
		checkNull(projectId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.retrieveProjectTrackingEventsByProjectId",
				true);
		connHandler.setInteger(1, projectId);
		return connHandler.retrieveArray(ProjectTrackingEvent.class);
	}

	@Override
	public boolean updateProjectTrackingEvent(ProjectTrackingEvent event)
			throws DAOException {
		checkNull(event);
		checkNull(event.getEventId());
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.updateProjectTrackingEvent", true);

		int i = 1;
		connHandler.setInteger(i++, event.getProjectId());
		connHandler.setString(i++, event.getEventTypeCd());
		connHandler.setTimestamp(i++, event.getEventDate());
		connHandler.setString(i++, event.getEventDescription());
		connHandler.setString(i++, event.getEventStatus());
		connHandler.setString(i++, event.getIssuesToAddress());
		connHandler.setTimestamp(i++, event.getResponseDueDate());
		connHandler.setInteger(i++, event.getAddedByUserId());
		connHandler.setInteger(i++, event.getLastModified() + 1);

		connHandler.setInteger(i++, event.getEventId());
		connHandler.setInteger(i++, event.getLastModified());

		return connHandler.update();
	}

	@Override
	public void deleteProjectTrackingEvent(Integer eventId) throws DAOException {
		checkNull(eventId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteProjectTrackingEvent",
				true);
		connHandler.setInteger(1, eventId);
		connHandler.remove();
		
	}
	
	@Override
	public void deleteProjectTrackingEventsByProjectId(Integer projectId)
			throws DAOException {
		checkNull(projectId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteProjectTrackingEventsByProjectId",
				true);
		connHandler.setInteger(1, projectId);
		connHandler.remove();
	}

	@Override
	public ProjectSearchResult[] searchProjects(
			ProjectTrackingSearchCriteria criteria) throws DAOException {

		String projectId = null;
		if (!Utility.isNullOrEmpty(criteria.getProjectId())) {
			projectId = criteria.getProjectId().trim();
			int tempId;
			try {
				tempId = Integer.parseInt(projectId);
				projectId = String.format("P%06d", tempId);
			} catch (NumberFormatException nfe) {
			}
		}

		String statementSQL;

		setDefaultSearchLimit(-1);

		statementSQL = loadSQL("ProjectTrackingSQL.searchProjects");

		StringBuffer whereClause = new StringBuffer("");

		if (criteria.getProjectName() != null && 
				criteria.getProjectName().trim().length() > 0) {
			whereClause.append(" AND LOWER(po.project_name) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(
					criteria.getProjectName().trim().replace("*","%")));
			whereClause.append("')");
		}
		if (projectId != null && projectId.trim().length() > 0) {
			whereClause.append(" AND LOWER(po.project_nbr) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(projectId.trim().replace("*", "%")));
			whereClause.append("')");
		}
		if (criteria.getProjectTypeCd() != null) {
			whereClause.append(" AND po.project_type_cd = '");
			whereClause.append(SQLizeString(criteria.getProjectTypeCd()));
			whereClause.append("'");
		}
		if (criteria.getProjectStateCd() != null) {
			whereClause.append(" AND po.project_state_cd = '");
			whereClause.append(SQLizeString(criteria.getProjectStateCd()));
			whereClause.append("'");
		}
		if (criteria.getProjectNEPALevelCd() != null) {
			whereClause.append(" AND lv.level_cd = '");
			whereClause.append(SQLizeString(criteria.getProjectNEPALevelCd()));
			whereClause.append("'");
		}
		if (criteria.getProjectStageCd() != null) {
			whereClause.append(" AND ne.project_stage_cd = '");
			whereClause.append(SQLizeString(criteria.getProjectStageCd()));
			whereClause.append("'");
		}
		if (criteria.getProjectOutreachCategoryCd() != null) {
			whereClause.append(" AND gr.outreach_category_cd = '");
			whereClause.append(SQLizeString(criteria.getProjectOutreachCategoryCd()));
			whereClause.append("'");
		}
		if (criteria.getProjectGrantStatusCd() != null) {
			whereClause.append(" AND gr.grant_status_cd = '");
			whereClause.append(SQLizeString(criteria.getProjectGrantStatusCd()));
			whereClause.append("'");
		}
		if (criteria.getProjectLetterTypeCd() != null) {
			whereClause.append(" AND le.letter_type_cd = '");
			whereClause.append(SQLizeString(criteria.getProjectLetterTypeCd()));
			whereClause.append("'");
		}
		if (criteria.getProjectContractTypeCd() != null) {
			whereClause.append(" AND co.contract_type_cd = '");
			whereClause.append(SQLizeString(criteria.getProjectContractTypeCd()));
			whereClause.append("'");
		}
		if (criteria.getProjectDescription() != null && 
				criteria.getProjectDescription().trim().length() > 0) {
			whereClause.append(" AND LOWER(po.project_dsc) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(
					criteria.getProjectDescription().trim().replace("*", "%")));
			whereClause.append("')");
		}
		if (criteria.getProjectSummary() != null && 
				criteria.getProjectSummary().trim().length() > 0) {
			whereClause.append(" AND LOWER(po.project_summary) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(
					criteria.getProjectSummary().trim().replace("*", "%")));
			whereClause.append("')");
		}
		if (criteria.getEventTypeCd() != null) {
			whereClause.append(" AND e.event_type_cd = '");
			whereClause.append(SQLizeString(criteria.getEventTypeCd()));
			whereClause.append("'");
		}
		if (criteria.getProjectLeadId() != null) {
			whereClause.append(" AND ld.user_id = '");
			whereClause.append(SQLizeString(criteria.getProjectLeadId()));
			whereClause.append("'");
		}
		
		Timestamp beginDate = 
				null == criteria.getEventDateFrom()? 
						null : new Timestamp(criteria.getEventDateFrom().getTime());
		Timestamp endDate = 
				null == criteria.getEventDateTo()?
						null : new Timestamp(criteria.getEventDateTo().getTime());
				
		if (beginDate != null || endDate != null) {
			whereClause.append("AND (");
			if (beginDate != null) {
				whereClause.append("e.event_date >= ? ");
				if (endDate != null) {
					whereClause.append(" AND ");
				}
			}
			if (endDate != null) {
				whereClause.append("e.event_date <= ? ");
			}
			whereClause.append(" ) ");
		}

		StringBuffer sortBy = new StringBuffer(" ORDER BY po.project_id desc");

		statementSQL += whereClause.toString() + " " + sortBy.toString();


		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(statementSQL);

		int index = 1;
		if (beginDate != null) {
			connHandler.setTimestamp(index++, beginDate);
		}
		if (endDate != null) {
			connHandler.setTimestamp(index++, formatEndOfDay(endDate));
		}

		return connHandler.retrieveArray(ProjectSearchResult.class, 
				defaultSearchLimit).toArray(new ProjectSearchResult[0]);
	}

	@Override
	public ProjectNote createProjectNote(ProjectNote note) throws DAOException {
		checkNull(note);
		checkNull(note.getNoteId());
		checkNull(note.getProjectId());

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.createProjectNote", true);

		ProjectNote ret = note;

		int i = 1;
		connHandler.setInteger(i++, note.getNoteId());
		connHandler.setInteger(i++, note.getProjectId());

		connHandler.update();

		ret.setLastModified(1);

		return ret;
	}

	@Override
	public List<ProjectNote> retrieveProjectNotesByProjectId(Integer projectId)
			throws DAOException {
		checkNull(projectId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.retrieveProjectNotesByProjectId", true);

		connHandler.setInteger(1, projectId);

		return connHandler.retrieveArray(ProjectNote.class);
	}

	@Override
	public void deleteProjectNotesByProjectId(Integer projectId)
			throws DAOException {
		checkNull(projectId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteProjectNotesByProjectId", true);

		connHandler.setInteger(1, projectId);

		connHandler.remove();
	}

	@Override
	public ProjectAttachment createProjectTrackingAttachment(
			ProjectAttachment pa) throws DAOException {
		checkNull(pa);
		checkNull(pa.getProjectId());
		checkNull(pa.getAttachment());
		checkNull(pa.getAttachment().getDocumentID());
		checkNull(pa.getAttachment().getDocTypeCd());
		
		ProjectAttachment ret = pa;
		
		ConnectionHandler connHandler = new ConnectionHandler("ProjectTrackingSQL.createProjectTrackingAttachment", true);
		
		int i = 1;
		connHandler.setInteger(i++, pa.getAttachment().getDocumentID());
		connHandler.setInteger(i++, pa.getProjectId());
		connHandler.setInteger(i++, pa.getTrackingEventId());
		connHandler.setString(i++, pa.getAttachment().getDocTypeCd());
		
		connHandler.update();
		
		ret.setLastModified(1);
		
		return ret;
	}

	@Override
	public List<ProjectAttachment> retrieveProjectTrackingAttachments(
			Integer projectId) throws DAOException {
		checkNull(projectId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.retrieveProjectTrackingAttachments", true);

		connHandler.setInteger(1, projectId);

		return connHandler.retrieveArray(ProjectAttachment.class);
	}

	@Override
	public boolean updateProjectTrackingAttachment(ProjectAttachment pa)
			throws DAOException {
		checkNull(pa);
		checkNull(pa.getAttachment());
		checkNull(pa.getAttachment().getDocumentID());
		checkNull(pa.getAttachment().getDocTypeCd());

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.updateProjectTrackingAttachment", true);

		int i = 1;
		connHandler.setInteger(i++, pa.getTrackingEventId());
		connHandler.setString(i++, pa.getAttachment().getDocTypeCd());
		connHandler.setInteger(i++, pa.getLastModified() + 1);

		connHandler.setInteger(i++, pa.getAttachment().getDocumentID());
		connHandler.setInteger(i++, pa.getLastModified());

		return connHandler.update();
	}

	@Override
	public void deleteProjectTrackingAttachment(Integer documentId)
			throws DAOException {
		checkNull(documentId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteProjectTrackingAttachment", true);

		connHandler.setInteger(1, documentId);

		connHandler.remove();
	}

	@Override
	public void deleteProjectTrackingAttachments(Integer projectId)
			throws DAOException {
		checkNull(projectId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteProjectTrackingAttachments", true);

		connHandler.setInteger(1, projectId);

		connHandler.remove();
	}

	@Override
	public List<ProjectAttachmentInfo> retrieveProjectTrackingEventAttachmentInfo(
			Integer trackingEventId) throws DAOException {
		checkNull(trackingEventId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.retrieveProjectTrackingEventAttachmentInfo",
				true);
		connHandler.setInteger(1, trackingEventId);

		return connHandler.retrieveArray(ProjectAttachmentInfo.class);
	}

	@Override
	public Contract createContract(Contract contract) throws DAOException {
		checkNull(contract);
		checkNull(contract.getProjectId());

		Contract ret = contract;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.createContract", true);

		int i = 1;
		connHandler.setInteger(i++, contract.getProjectId());
		connHandler.setString(i++, contract.getContractTypeCd());
		connHandler.setString(i++, contract.getContractStatusCd());
		connHandler.setString(i++, contract.getContractNumber());
		connHandler.setTimestamp(i++, contract.getContractExpirationDate());
		connHandler.setString(i++, contract.getVendorName());
		connHandler.setString(i++, contract.getVendorNumber());
		connHandler
				.setTimestamp(i++, contract.getMonitoringOperationsEndDate());
		connHandler.setString(i++, contract.getMSANumber());
		connHandler.setLong(i++, contract.getAGHeatTicketNumber());
		connHandler.setString(i++, contract.getOCIOApproval());
		connHandler.setBigDecimal(i++, contract.getTotalAward());

		connHandler.update();

		ret.setLastModified(1);

		return ret;
	}

	@Override
	public boolean updateContract(Contract contract) throws DAOException {
		checkNull(contract);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.updateContract", true);

		int i = 1;
		connHandler.setString(i++, contract.getContractTypeCd());
		connHandler.setString(i++, contract.getContractStatusCd());
		connHandler.setString(i++, contract.getContractNumber());
		connHandler.setTimestamp(i++, contract.getContractExpirationDate());
		connHandler.setString(i++, contract.getVendorName());
		connHandler.setString(i++, contract.getVendorNumber());
		connHandler
				.setTimestamp(i++, contract.getMonitoringOperationsEndDate());
		connHandler.setString(i++, contract.getMSANumber());
		connHandler.setLong(i++, contract.getAGHeatTicketNumber());
		connHandler.setString(i++, contract.getOCIOApproval());
		connHandler.setBigDecimal(i++, contract.getTotalAward());
		connHandler.setInteger(i++, contract.getLastModified() + 1);
		
		connHandler.setInteger(i++, contract.getProjectId());
		connHandler.setInteger(i++, contract.getLastModified());

		return connHandler.update();
	}

	@Override
	public void deleteContract(Integer projectId) throws DAOException {
		checkNull(projectId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteContract", true);
		
		connHandler.setInteger(1, projectId);
		
		connHandler.remove();
	}

	@Override
	public List<Integer> createContractAccountantXrefs(Integer projectId,
			List<Integer> userIds) throws DAOException {
		checkNull(projectId);
		checkNull(userIds);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.createContractAccountantXref", true);

		connHandler.setInteger(1, projectId);
		try {
			for (Integer id : userIds) {
				connHandler.setInteger(2, id);
				connHandler.updateNoClose();
			}
		} finally {
			connHandler.close();
		}

		return userIds;
	}

	@Override
	public List<Integer> retrieveContractAccountantUserIds(Integer projectId)
			throws DAOException {
		checkNull(projectId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.retrieveContractAccountantUserIds",
				true);

		connHandler.setInteger(1, projectId);

		return connHandler.retrieveJavaObjectArray(Integer.class);
	}

	@Override
	public void deleteContractAccountantXrefs(Integer projectId)
			throws DAOException {
		checkNull(projectId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteContractAccountantXrefs", true);

		connHandler.setInteger(1, projectId);

		connHandler.remove();
	}

	@Override
	public Budget createBudget(Budget budget) throws DAOException {
		checkNull(budget);
		checkNull(budget.getProjectId());
		checkNull(budget.getBFY());
		checkNull(budget.getBudgetFunctionCd());
		checkNull(budget.getAmount());

		Budget ret = budget;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.CreateBudget", true);

		Integer id = nextSequenceVal("S_budget_id", budget.getBudgetId());

		int i = 1;
		connHandler.setInteger(i++, budget.getProjectId());
		connHandler.setInteger(i++, id);
		connHandler.setInteger(i++, budget.getBFY());
		connHandler.setString(i++, budget.getBudgetFunctionCd());
		connHandler.setBigDecimal(i++, budget.getAmount());

		connHandler.update();

		ret.setBudgetId(id);
		ret.setLastModified(1);

		return ret;
	}

	@Override
	public boolean updateBudget(Budget budget) throws DAOException {
		checkNull(budget);
		checkNull(budget.getBudgetId());
		checkNull(budget.getBFY());
		checkNull(budget.getBudgetFunctionCd());
		checkNull(budget.getAmount());

		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.updateBudget", true);

		int i = 1;
		connHandler.setInteger(i++, budget.getBFY());
		connHandler.setString(i++, budget.getBudgetFunctionCd());
		connHandler.setBigDecimal(i++, budget.getAmount());
		connHandler.setInteger(i++, budget.getLastModified() + 1);

		connHandler.setInteger(i++, budget.getBudgetId());
		connHandler.setInteger(i++, budget.getLastModified());

		return connHandler.update();
	}

	@Override
	public Budget retrieveBudget(Integer budgetId) throws DAOException {
		checkNull(budgetId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.retrieveBudget", true);
		connHandler.setInteger(1, budgetId);
		return (Budget) connHandler.retrieve(Budget.class);
	}

	@Override
	public List<Budget> retrieveBudgetByProjectId(Integer projectId)
			throws DAOException {
		checkNull(projectId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.retrieveBudgetByProjectId", true);
		connHandler.setInteger(1, projectId);
		return connHandler.retrieveArray(Budget.class);
	}

	@Override
	public void deleteBudget(Integer budgetId) throws DAOException {
		checkNull(budgetId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteBudget", true);
		connHandler.setInteger(1, budgetId);
		connHandler.remove();
	}

	@Override
	public void deleteBudgetByProjectId(Integer projectId) throws DAOException {
		checkNull(projectId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ProjectTrackingSQL.deleteBudgetByProjectId", true);
		connHandler.setInteger(1, projectId);
		connHandler.remove();
	}
}