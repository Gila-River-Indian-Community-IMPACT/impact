package us.oh.state.epa.stars2.bo;

import java.io.InputStream;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.Budget;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.Project;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectAttachment;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectNote;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectSearchResult;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectTrackingEvent;
import us.wy.state.deq.impact.webcommon.projectTracking.ProjectTrackingSearchCriteria;

public interface ProjectTrackingService {
	
	List<Document> getPrintableDocumentList(Project project, String user) 
			throws DAOException;

	Project createProject(Project project) throws DAOException;

	Project retrieveProject(Integer projectId) throws DAOException;

	boolean updateProject(Project project) throws DAOException;
	
	void deleteProject(Project project) throws DAOException;
	
	ProjectTrackingEvent createProjectTrackingEvent(ProjectTrackingEvent event)
			throws DAOException;
	
	ProjectTrackingEvent retrieveProjectTrackingEvent(Integer eventId)
			throws DAOException;

	List<ProjectTrackingEvent> retrieveProjectTrackingEventsByProjectId(
			Integer projectId) throws DAOException;

	boolean updateProjectTrackingEvent(ProjectTrackingEvent event)
			throws DAOException;

	void deleteProjectTrackingEvent(Integer eventId) throws DAOException;

	void deleteProjectTrackingEventsByProjectId(Integer projectId)
			throws DAOException;

	ProjectSearchResult[] searchProjects(ProjectTrackingSearchCriteria criteria)
			throws DAOException;
	
	ProjectNote createProjectNote(ProjectNote note) throws DAOException;
	
	List<ProjectNote> retrieveProjectNotesByProjectId(Integer projectId)
			throws DAOException;
	
	boolean modifyProjectNote(ProjectNote note) throws DAOException;
	
	ProjectAttachment createProjectTrackingAttachment(
			ProjectAttachment attachment, InputStream fileStream)
			throws DAOException;
	
	List<ProjectAttachment> retrieveProjectTrackingAttachments(Integer projectId)
			throws DAOException;
	
	boolean updateProjectAttachment(ProjectAttachment projectAttachment)
			throws DAOException;
	
	void deleteProjectAttachment(ProjectAttachment projectAttachment)
			throws DAOException;
	
	Budget createBudget(Budget budget) throws DAOException;
	
	boolean updateBudget(Budget budget) throws DAOException;
	
	Budget retrieveBudget(Integer budgetId) throws DAOException;
	
	List<Budget> retrieveBudgetByProjectId(Integer projectId) throws DAOException;
	
	void deleteBudget(Integer budgetId) throws DAOException;
	
}
