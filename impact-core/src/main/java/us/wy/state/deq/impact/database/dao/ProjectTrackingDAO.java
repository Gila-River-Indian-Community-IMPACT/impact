package us.wy.state.deq.impact.database.dao;

import java.util.List;

import us.oh.state.epa.stars2.database.dao.TransactableDAO;
import us.oh.state.epa.stars2.framework.exception.DAOException;
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

public interface ProjectTrackingDAO extends TransactableDAO{
	
	Project createProject(Project project) throws DAOException;

	Project retrieveProject(Integer projectId) throws DAOException;

	List<String> createProjectDivisions(Integer projectId,
			List<String> projectDivisionCds) throws DAOException;

	List<String> retrieveProjectDivisionCds(Integer projectId)
			throws DAOException;

	boolean updateProject(Project project) throws DAOException;
	
	void deleteProject(Integer projectId) throws DAOException;
	
	void deleteProjectDivisions(Integer projectId) throws DAOException;
	
	NEPAProject createNEPAProject(NEPAProject nepaProject) throws DAOException;

	boolean updateNEPAProject(NEPAProject nepaProject) throws DAOException;
	
	void deleteNEPAProject(Integer projectId) throws DAOException;
	
	List<String> createNEPAProjectLevelXrefs(Integer projectId,
			List<String> levelCds) throws DAOException;

	List<String> retrieveNEPAProjectLevelCds(Integer projectId)
			throws DAOException;
	
	void deleteNEPAProjectLevelXrefs(Integer projectId) throws DAOException;
	
	List<String> createNEPAProjectLeadAgencyXrefs(Integer projectId,
			List<String> leadAgenctCds) throws DAOException;

	List<String> retrieveNEPAProjectLeadAgencyCds(Integer projectId)
			throws DAOException;
	
	void deleteNEPAProjectLeadAgencyXrefs(Integer projectId) throws DAOException;

	List<String> createNEPAProjectBLMFieldOfficeXrefs(Integer projectId,
			List<String> BLMFieldOfficeCds) throws DAOException;

	List<String> retrieveNEPAProjectBLMFieldOfficeCds(Integer projectId)
			throws DAOException;
	
	void deleteNEPAProjectBLMFieldOfficeXrefs(Integer projectId) throws DAOException;
	
	List<String> createNEPAProjectNationalForestXrefs(Integer projectId,
			List<String> nationalForestCds) throws DAOException;

	List<String> retrieveNEPAProjectNationalForestCds(Integer projectId)
			throws DAOException;
	
	void deleteNEPAProjectNationalForestXrefs(Integer projectId) throws DAOException;
	
		
	List<String> createNEPAProjectNationalParkXrefs(Integer projectId,
			List<String> nationalParkCds) throws DAOException;

	List<String> retrieveNEPAProjectNationalParkCds(Integer projectId)
			throws DAOException;
	
	void deleteNEPAProjectNationalParkXrefs(Integer projectId) throws DAOException;
	
	GrantProject createGrantProject(GrantProject grantProject) throws DAOException;

	boolean updateGrantProject(GrantProject grantProject) throws DAOException;
	
	void deleteGrantProject(Integer projectId) throws DAOException;
	
	LetterProject createLetterProject(LetterProject letterProject) throws DAOException;

	boolean updateLetterProject(LetterProject letterProject) throws DAOException;
	
	void deleteLetterProject(Integer projectId) throws DAOException;
	
	List<Integer> createProjectLeadXrefs(Integer projectId,
			List<Integer> userIds) throws DAOException;

	List<Integer> retrieveProjectLeadUserIds(Integer projectId)
			throws DAOException;
	
	void deleteProjectLeadXrefs(Integer projectId) throws DAOException;
	
	List<Integer> createGrantProjectAccountantXrefs(Integer projectId,
			List<Integer> userIds) throws DAOException;

	List<Integer> retrieveGrantProjectAccountantUserIds(Integer projectId)
			throws DAOException;

	void deleteGrantProjectAccountantXrefs(Integer projectId)
			throws DAOException;
	
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
	
	ProjectNote createProjectNote(ProjectNote note) throws DAOException;

	ProjectSearchResult[] searchProjects(ProjectTrackingSearchCriteria criteria)
			throws DAOException;

	List<ProjectNote> retrieveProjectNotesByProjectId(Integer projectId)
			throws DAOException;

	void deleteProjectNotesByProjectId(Integer projectId) throws DAOException;
	
	ProjectAttachment createProjectTrackingAttachment(
			ProjectAttachment attachment) throws DAOException;

	List<ProjectAttachment> retrieveProjectTrackingAttachments(Integer projectId)
			throws DAOException;

	boolean updateProjectTrackingAttachment(ProjectAttachment attachment)
			throws DAOException;

	void deleteProjectTrackingAttachment(Integer documentId)
			throws DAOException;

	void deleteProjectTrackingAttachments(Integer projectId)
			throws DAOException;
	
	List<ProjectAttachmentInfo> retrieveProjectTrackingEventAttachmentInfo(
			Integer trackingEventId) throws DAOException;
	
	Contract createContract(Contract contract) throws DAOException;

	boolean updateContract(Contract contract) throws DAOException;
	
	void deleteContract(Integer projectId) throws DAOException;
	
	List<Integer> createContractAccountantXrefs(Integer projectId,
			List<Integer> userIds) throws DAOException;

	List<Integer> retrieveContractAccountantUserIds(Integer projectId)
			throws DAOException;

	void deleteContractAccountantXrefs(Integer projectId)
			throws DAOException;
	
	Budget createBudget(Budget budget) throws DAOException;
	
	boolean updateBudget(Budget budget) throws DAOException;
	
	Budget retrieveBudget(Integer budgetId) throws DAOException;
	
	List<Budget> retrieveBudgetByProjectId(Integer projectId) throws DAOException;
	
	void deleteBudget(Integer budgetId) throws DAOException;
	
	void deleteBudgetByProjectId(Integer projectId) throws DAOException;
	
}
 