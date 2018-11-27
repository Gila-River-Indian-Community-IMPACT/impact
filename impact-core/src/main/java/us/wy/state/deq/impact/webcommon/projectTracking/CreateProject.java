package us.wy.state.deq.impact.webcommon.projectTracking;

import java.rmi.RemoteException;
import java.util.Date;

import us.oh.state.epa.stars2.bo.ProjectTrackingService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.ProjectTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.Contract;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.GrantProject;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.LetterProject;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.NEPAProject;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.Project;

@SuppressWarnings("serial")
public class CreateProject extends AppBase {
	
	public static String PAGE_VIEW_ID = "createProject:";
	public static String CREATE_OUTCOME = "createProjectTracking";
	
	private String projectName;
	private String projectDescription;
	private String projectTypeCd;
	private String projectStateCd;
	
	private ProjectTrackingService projectTrackingService;
	
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

	public ProjectTrackingService getProjectTrackingService() {
		return projectTrackingService;
	}
	
	public void setProjectTrackingService(
			ProjectTrackingService projectTrackingService) {
		this.projectTrackingService = projectTrackingService;
	}
	
	public String create() {
		String ret = null;
		
		if(!firstButtonClick()) {
			return null; // protect from multiple clicks
		}
		
		try {
			Project project = getProjectTypeClass(projectTypeCd).newInstance();
			project.setProjectDescription(getProjectDescription());
			project.setProjectName(getProjectName());
			project.setProjectTypeCd(getProjectTypeCd());
			project.setProjectStateCd(getProjectStateCd());
			
			ValidationMessage[] valMsgs = project.validate();
			if (project.isValid()) {
				// set creator id and created date
				project.setCreatorId(InfrastructureDefs.getCurrentUserId());
				project.setCreatedDate(new Date(System.currentTimeMillis()));
	
				logger.debug("Creating project " + project.getProjectName());
				Project p = getProjectTrackingService().createProject(project);
				logger.debug("Successfully created a new project with id: " + p.getProjectId());
				
				ProjectTrackingDetail projectTrackingDetail = (ProjectTrackingDetail) FacesUtil
						.getManagedBean("projectTrackingDetail");
				projectTrackingDetail.setProjectId(p.getProjectId());
				projectTrackingDetail.setProjectTypeCd(p.getProjectTypeCd());
				ret = projectTrackingDetail.refresh();
			} else {
				displayValidationMessages(PAGE_VIEW_ID, valMsgs);
			}
		} catch (RemoteException re) {
			DisplayUtil
					.displayError("An error occured while creating the project");
			handleException(re);
		} catch(InstantiationException ie) {
			DisplayUtil
			.displayError("A system error occured while creating the project");
			logger.error("Instantiation error", ie);
		} catch(IllegalAccessException iae) {
			DisplayUtil
			.displayError("A system error occured while creating the project");
			logger.error("Illegal access error", iae);
		}finally {
			clearButtonClicked();
		}
		
		return ret;
	}
	
	public String reset() {
		this.projectName = null;
		this.projectDescription = null;
		this.projectTypeCd = null;
		this.projectStateCd = null;
				
		return null; // stay on the same page
	}
	
	public String refresh() {
		reset();
		return CREATE_OUTCOME;
	}
	
	public Class<? extends Project> getProjectTypeClass(String projectTypeCd) {
		Class<? extends Project> projectTypeClass = Project.class; // default project class
		if(!Utility.isNullOrEmpty(projectTypeCd)) {
			if (projectTypeCd.equals(ProjectTypeDef.NEPA)) {
				projectTypeClass = NEPAProject.class;
			} else if (projectTypeCd.equals(ProjectTypeDef.GRANTS)) {
				projectTypeClass = GrantProject.class;
			} else if (projectTypeCd.equals(ProjectTypeDef.LETTERS)) {
				projectTypeClass = LetterProject.class;
			} else if (projectTypeCd.equals(ProjectTypeDef.CONTRACTS)) {
				projectTypeClass = Contract.class;
			}
		}

		return projectTypeClass;
	}
}
