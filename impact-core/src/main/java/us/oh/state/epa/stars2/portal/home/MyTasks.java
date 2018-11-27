package us.oh.state.epa.stars2.portal.home;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gricdeq.impact.ExternalRole;
import org.gricdeq.impact.UserPrincipal;

import oracle.adf.view.faces.component.UIXTable;
import oracle.adf.view.faces.event.ReturnEvent;
import us.oh.state.epa.stars2.app.facility.CloneFacility;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.bo.MonitoringService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.Task.TaskType;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.DelegationRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.PBRNotification;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.RPCRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.TIVApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplication;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReport;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactRole;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.PermitIssuanceTypeDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.portal.application.ApplicationDetail;
import us.oh.state.epa.stars2.portal.application.ApplicationSearch;
import us.oh.state.epa.stars2.portal.ceta.StackTestDetail;
import us.oh.state.epa.stars2.portal.ceta.StackTestSearch;
import us.oh.state.epa.stars2.portal.compliance.ComplianceReports;
import us.oh.state.epa.stars2.portal.emissionsReport.ReportDetail;
import us.oh.state.epa.stars2.portal.emissionsReport.ReportProfile;
import us.oh.state.epa.stars2.portal.facility.FacilityProfile;
import us.oh.state.epa.stars2.portal.relocation.Relocation;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.AppValidationMsg;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuTree;
import us.wy.state.deq.impact.app.contact.ExternalOrganization;
import us.wy.state.deq.impact.bo.CompanyService;
import us.wy.state.deq.impact.database.dbObjects.company.Company;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroup;
import us.wy.state.deq.impact.portal.company.CompanyProfile;
import us.wy.state.deq.impact.webcommon.monitoring.MonitorGroupDetail;

public class MyTasks extends AppBase {
	
	private static final long serialVersionUID = -439058561163433641L;

	public static final String GET_PIN_URL = "../util/pin.jsf";
	private static final String HOME = "home";
	private HashMap<String, Task> tasks = new HashMap<String, Task>();
	private List<CreateMenuItem> facilityMenuItems = new ArrayList<CreateMenuItem>();
	private List<CreateMenuItem> permitMenuItems = new ArrayList<CreateMenuItem>();
	private List<CreateMenuItem> emissionsMenuItems = new ArrayList<CreateMenuItem>();
	private List<CreateMenuItem> complianceMenuItems = new ArrayList<CreateMenuItem>();
	private List<CreateMenuItem> monitoringMenuItems = new ArrayList<CreateMenuItem>();
	private List<CreateMenuItem> rightMenuItems = new ArrayList<CreateMenuItem>();
	private transient UIXTable tasksTable;
	private String datasetId;
	private Task task;
	private String taskId;
	private String pageRedirect = null;
	private Facility facility;
	private Integer corePlaceId;
	String corePlaceIdStr;
	private String loginName;
	private String origRef;
	private boolean hasSubmit;
	private boolean isReconciled;
	private boolean frozenFacilityViewable;
	private boolean fromHomeContact;
	private boolean hasTVPTO;
	private Task selDelTask = null;
	private String deleteTaskMsg = null;
	private String epaPortal;

	private Company company;
	private String facilityId;
	public static String contactMsg = "Please contact AQD personnel at XXX-XXX-XXXX.";

	private Map<String,Integer> monitorGroupIds = new HashMap<String,Integer>();
	
	private boolean testMode = "true"
			.equals(Config.getEnvEntry("app/testMode"));

	private CompanyService companyService;
	private FacilityService facilityService;
	private InfrastructureService infrastructureService;
	private boolean impactFullEnabled;

	private MonitoringService monitoringService;
	
	private boolean stagingMonitorReportUploadFlag = false;
	
	private ContactRole currentRole;
	private List<ContactRole> contactRoles;
	private ContactRole initialRole;
	
	
	public MonitoringService getMonitoringService() {
		return monitoringService;
	}

	public void setMonitoringService(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(
			InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	
	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}
	
	
	
	public boolean isStagingMonitorReportUploadFlag() {
		return stagingMonitorReportUploadFlag;
	}

	public void setStagingMonitorReportUploadFlag(
			boolean stagingMonitorReportUploadFlag) {
		this.stagingMonitorReportUploadFlag = stagingMonitorReportUploadFlag;
	}

	public Integer getMonitorGroupId() {
		if (null != getFacilityId() && 
				!monitorGroupIds.containsKey(getFacilityId())) {
			refreshMonitorGroupId();
		}
		return monitorGroupIds.get(getFacilityId());
	}

	public void setMonitorGroupId(Integer monitorGroupId) {
		monitorGroupIds.put(getFacilityId(), monitorGroupId);
	}

	public MyTasks() {
		init();
	}

	public CompanyService getCompanyService() {
		return companyService;
	}

	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}

	private void init() {
		
		hasTVPTO = false;

		Node portalNode = Config.findNode("app.defaultEPAPortal");
		if (portalNode != null) {
			epaPortal = portalNode.getText();
		}

		ExternalContext context = FacesContext.getCurrentInstance()
				.getExternalContext();

		boolean logout;

		if (context != null) {
			HttpServletRequest request = (HttpServletRequest) context
					.getRequest();
			HttpServletResponse response = (HttpServletResponse) context
					.getResponse();

			if (request != null) {

				HttpSession tempSession = request.getSession(false);
				String referalStr = request.getHeader("referer");

				/* BEGIN TEST MODE PARM ASSIGNMENT */
				if (testMode) {
					referalStr = SystemPropertyDef.getSystemPropertyValue("referalStr", null);
				}
				/* END OF TEST MODE PARM ASSIGNMENT */

				// If we came from within the stars2 app, don't do anything.
				if (tempSession != null) {
					if (null == referalStr || !referalStr.contains("stars2")) {
						origRef = referalStr;
						Enumeration<?> attrEnum = request.getParameterNames();

						while (attrEnum.hasMoreElements()) {
							String parmName = (String) attrEnum.nextElement();

							logger.debug("Parameter " + parmName
									+ " has a value of "
									+ request.getParameter(parmName));
						}

						loginName = request.getParameter("LOGIN_NAME");
						corePlaceIdStr = request.getParameter("TYPE_ID");
						datasetId = request.getParameter("DATASET_ID");
						logout = AbstractDAO
								.translateIndicatorToBoolean(request
										.getParameter("LOGOUT"));
						isReconciled = AbstractDAO
								.translateIndicatorToBoolean(request
										.getParameter("RECON_STATUS"));
						hasSubmit = AbstractDAO
								.translateIndicatorToBoolean(request
										.getParameter("ALLOW_SUBMIT"));

						
						java.security.Principal p = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
						loginName =  p.getName();
						if (p instanceof UserPrincipal) {
							UserPrincipal userPrincipal = (UserPrincipal)p;
							this.contactRoles = userPrincipal.getContact().getContactRoles();
							this.initialRole = userPrincipal.getInitialRole();
						}
						
						


						/* BEGIN TEST MODE PARM ASSIGNMENT */
						if (testMode) {
							// loginName = getParameter("loginName");
							if (SystemPropertyDef.getSystemPropertyValueAsBoolean("hasSubmit", false)) {
								hasSubmit = true;
							} else {
								hasSubmit = false;
							}
						}
						/* END OF TEST MODE PARM ASSIGNMENT */

						if (logout) {
							logger.debug("logout requested. invalidating current session");
							tempSession.invalidate();

							try {
								String redirect = epaPortal;
								if (epaPortal == null) {
									redirect = origRef;
								}
								logger.debug("logout - redirecting to '"
										+ redirect + "'");
								response.sendRedirect(redirect);
							} catch (IOException ioe) {
								logger.error(ioe.getMessage(), ioe);
							}
						} else if (datasetId != null
								&& !datasetId.equalsIgnoreCase("null")) {
							try {
								task = getInfrastructureService().retrieveTask(
										datasetId);

								if (task != null) {
									this.corePlaceId = task.getCorePlaceId();
									refreshTasks(this.corePlaceId);
									facility = getFacilityService()
											.retrieveFacilityData(
													task.getFacilityId(), -1);
								} else {
									DisplayUtil
											.displayError("Task not found. If you submitted a task, Please continue with another task from eBusiness Center.");
									pageRedirect = "TaskSubmited";
									logger.error("Task not found; loginName="
											+ loginName + ", corePlaceIdStr="
											+ corePlaceIdStr + "datasetId="
											+ datasetId + ", logout=" + logout
											+ ", isReconsciled=" + isReconciled
											+ ", hasSubmit=" + hasSubmit);
									return;
									/*
									 * resetTabs(); for (SimpleMenuItem tempMenu
									 * : ((SimpleMenuItem)
									 * FacesUtil.getManagedBean
									 * ("menuItem_mytasks")).getChildren()) {
									 * tempMenu.setDisabled(true); }
									 */
								}
							} catch (RemoteException re) {
								handleException("init() failed for facility "
										+ task.getFacilityId(), re);
							}

							((SimpleMenuItem) FacesUtil
									.getManagedBean("menuItem_createFacility"))
									.setRendered(false);
							((SimpleMenuItem) FacesUtil
									.getManagedBean("menuItem_home"))
									.setRendered(true);

							FacilityProfile fp = (FacilityProfile) FacesUtil
									.getManagedBean("facilityProfile");
							fp.setMyTasks(this);

							pageRedirect = processTask();

							if (task != null) {
								switch (task.getTaskType()) {
								case FC:
								case FCH:
									facility = fp.getFacility();
									break;
								default:
									try {
										facility = getFacilityService()
												.retrieveFacilityData(
														task.getFacilityId(),
														-1);
									} catch (RemoteException re) {
										handleException(re);
										DisplayUtil
												.displayError("Accessing Facility for facility ID : ["
														+ corePlaceId
														+ "] failed");
										return;
									}
								}
							}

							// This try block is temporary; can be removed after
							// a while
							fixFacilityContact();

							return;
						} else if (loginName != null && !loginName.equals("")) {
							if (this.currentRole == null && null == this.initialRole) { // redirect to companySelector
								resetTabs();

								CompanyProfile cp = (CompanyProfile) FacesUtil
										.getManagedBean("companyProfile");
								cp.getActiveContactRolesByUsername(loginName);
								cp.setContactRolesWrapper();
								
								hideTabs();
								FacesUtil
										.renderSimpleMenuItem("menuItem_cmpSelector");
								FacesUtil
										.hideSimpleMenuItem("menuItem_facSelector");
								pageRedirect = "companySelector";
							}
						} else {
							logger.error("External Company ID not set!");
						}
					} else {
						logger.debug("referalStr contains stars2: " + referalStr);
					}
				} else {
					logger.debug("session is null");
				}
			} else {
				logger.error("Request is null");
			}
		} else {
			logger.error("Context is null");
		}
		
		ExternalOrganization externalOrganization = null;
		if (null != this.currentRole || null != this.initialRole) { // redirect to facilitySelector
			if (null == this.currentRole) {
				for (ContactRole contactRole : this.contactRoles) {
					if (contactRole.getExternalRole().getUserRoleId().equals(
							this.initialRole.getExternalRole().getUserRoleId())) {
						this.currentRole = contactRole;
						break;
					}
				}
			}
			externalOrganization = this.currentRole.getExternalRole().getOrganization();

			// refresh company
			CompanyProfile companyProfile = (CompanyProfile) FacesUtil.getManagedBean("companyProfile");
			try {
				company = companyProfile.getCompanyByExternalCompanyId(externalOrganization.getOrganizationId(), false);
				if (company == null) { //TODO this is curious! why would it look in the staging schema???
					company = getCompanyService()
							.retrieveCompanyByExternalCompanyId(externalOrganization.getOrganizationId(),
									true);
				}

				if (company == null) {
					DisplayUtil.displayInfo(
							"Company does not exist for External Company ID : ["
									+ externalOrganization.getOrganizationId() + "].", "facSelector");
				}
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil
						.displayError("Accessing Company for External Company ID : ["
								+ externalOrganization.getOrganizationId() + "] failed");
				return;
			}

			if (facility == null || facilityId == null) {
				if (company != null) {
					companyProfile.setCmpId(company.getCmpId());
					companyProfile.getCompany();
					companyProfile.goFacilitySelector(this);
				}

				logger.debug("No facility has been chosen to edit.");
				pageRedirect = "facilitySelector";
			}

		} else {
			hideTabs();
			FacesUtil.renderSimpleMenuItem("menuItem_cmpSelector");
			FacesUtil.hideSimpleMenuItem("menuItem_facSelector");
			pageRedirect = "companySelector";
		}

		if (facilityId != null) {
			// refresh facility
			FacilityProfile fp = (FacilityProfile) FacesUtil
					.getManagedBean("facilityProfile");
			try {
				facility = fp.getFacilityByFacilityId(facilityId, false);
				if (facility == null) {
					facility = fp.getFacilityByFacilityId(facilityId, true);
				} else {
					frozenFacilityViewable = true;

					// 2504 change facility id
					Facility stagingFacility = getFacilityService()
							.retrieveFacilityByFacilityId(facilityId, true);
					if (stagingFacility != null
							&& !facility.getFacilityId().equals(
									stagingFacility.getFacilityId())) {
						getFacilityService()
								.changeGateWayFacilityId(
										stagingFacility.getFacilityId(),
										facility.getFacilityId(),
										facility.getDoLaaCd());
					}
				}

				if (facility != null) {
					hasTVPTO = isHasIssuedTVPTO();
					facilityMenuItems.clear();
					permitMenuItems.clear();
					emissionsMenuItems.clear();
					complianceMenuItems.clear();
					monitoringMenuItems.clear();
					rightMenuItems.clear();
				}

			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil
						.displayError("Accessing Facility for CROMERR Company ID : ["
								+ externalOrganization.getOrganizationId() + "] failed");
				return;
			}

			getTaskFromTasksTable();

			// This try block is temporary; can be removed after a while
			fixFacilityContact();

			refreshTasks(this.facilityId);

			if (facility != null) {
				if (pageRedirect != null
						&& !pageRedirect.equals("facilityProfile")
						&& !pageRedirect.equals("applicationDetail")
						&& !pageRedirect.equals("emissionReport")
						&& !pageRedirect.equals("complianceDetail")
						&& !pageRedirect.equals("ceta.stackTestDetail")
						&& !pageRedirect.equals("monitoring.monitorGroupDetail")) {
					pageRedirect = null;
				}
			}
			fp.setEditStaging(true);
		}
	}


	public ContactRole getCurrentRole() {
		return currentRole;
	}

	public void setCurrentRole(ContactRole currentRole) {
		this.currentRole = currentRole;
	}

	public List<ContactRole> getContactRoles() {
		return contactRoles;
	}

	public void setContactRoles(List<ContactRole> contactRoles) {
		this.contactRoles = contactRoles;
	}

	// This try block is temporary; can be removed after a while
	private void fixFacilityContact() {
		if (facility == null)
			return; // Nothing to fix.
		try {
			boolean fix;
			fix = getInfrastructureService().fixFacilityContact(facility);
			if (fix) {
				this.refreshTasks(facility.getCorePlaceId());
			}
		} catch (RemoteException re) {
			logger.error(
					"Populating facility contact in staging failed; But continue",
					re);
			return;
		}
	}

	public String getUserName() {
		return loginName;
	}

	public void refreshTasks(Integer corePlaceId) {
		Task filterTask = new Task();
		filterTask.setCorePlaceId(corePlaceId);

		tasks.clear();

		if (facility != null && task != null
				&& !task.getCorePlaceId().equals(facility.getCorePlaceId())) {
			logger.debug("Facility has changed");
			resetTabs();
		}

		try {
			logger.debug("Retrieving tasks for facility with core place id = "
					+ filterTask.getCorePlaceId());
			Task[] tasks1 = getInfrastructureService()
					.retrieveTasks(filterTask);
			logger.debug(tasks1.length
					+ " tasks retrieved for facility with core place id = "
					+ filterTask.getCorePlaceId());
			for (Task task : tasks1) {
				tasks.put(task.getTaskId(), task);
			}
			for (Task t : tasks1) {
				String id = t.getDependentTaskId();
				if (null == id) {
					continue;
				}
				for (Task t2 : tasks1) {
					if (id.equals(t2.getTaskId())) {
						t.setDependentTaskDescription(t2.getTaskDescription());
						break;
					}
				}
			}

		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Accessing Facility Change tasks failed");
		}
	}

	/**
	 * Refresh tasks by facility id instead of core place id
	 * 
	 * @param facilityId
	 */
	public void refreshTasks(String facilityId) {

		Task filterTask = new Task();
		filterTask.setFacilityId(facilityId);

		tasks.clear();

		if (facility != null && task != null
				&& !task.getFacilityId().equals(facility.getFacilityId())) {
			logger.debug("Facility has changed");
			resetTabs();
		}

		try {
			logger.debug("Retrieving tasks for facility with facility id = "
					+ filterTask.getFacilityId());
			Task[] tasks1 = getInfrastructureService()
					.retrieveTasks(filterTask);
			logger.debug(tasks1.length
					+ " tasks retrieved for facility with facility id = "
					+ filterTask.getFacilityId());
			for (Task task : tasks1) {
				tasks.put(task.getTaskId(), task);
			}
			for (Task t : tasks1) {
				String id = t.getDependentTaskId();
				if (null == id) {
					continue;
				}
				for (Task t2 : tasks1) {
					if (id.equals(t2.getTaskId())) {
						t.setDependentTaskDescription(t2.getTaskDescription());
						break;
					}
				}
			}
			
			FacilityProfile facilityProfile = (FacilityProfile)FacesUtil.getManagedBean("facilityProfile");
			if(tasks.isEmpty()) {
				if(facilityProfile.getTask() != null)
					facilityProfile.setTask(null);
				if(facilityProfile.getContactTask() != null)
					facilityProfile.setContactTask(null);
				
			}
			else {
				for(Task task : tasks.values().toArray(new Task[0])) {
					if(task.getTaskType().equals(Task.TaskType.FC))
						facilityProfile.setTask(task);
					else if(task.getTaskType().equals(Task.TaskType.FCC))
						facilityProfile.setContactTask(task);
				}
			}

		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Accessing Facility Change tasks failed");
		}

		if (facility != null) {
			resetTabs();
		}
	}

	public final String getTaskId() {
		return taskId;
	}

	public final void setTaskId(final String taskId) {
		this.taskId = taskId;
		if (taskId != null) {
			try {
				logger.debug("retrieving task with taskId = " + taskId);
				// refresh current task object
				task = getInfrastructureService().retrieveTask(taskId);

			} catch (Exception e) {
				handleException(new RemoteException("Failed to set taskId", e));
			}
		}
	}

	public final Task getTask() {

		if ((task == null) || (!task.getTaskId().equals(taskId))) {
			task = tasks.get(taskId);
		}
		Task ret = task;

		return ret;
	}

	public final void setTask(final Task task) {
		this.task = task;
	}

	public final Task[] getTasks() {
		return tasks.values().toArray(new Task[0]);
	}

	public final void setTasks(final List<Task> tasks) {
		this.tasks = new HashMap<String, Task>();

		for (Task task : tasks) {
			this.tasks.put(task.getTaskId(), task);
		}
	}

	public final String submitTask() {
		return submitTask(false);
	}
	
	public final String submitTask(boolean fromValidationDlgAction) { // invoked from home.jsp page
		Task task = getTask();
		// make sure user name for submitted task is the user who is submitting
		task.setUserName(getUserName());
		return processTask(fromValidationDlgAction);
	}

	private String processTask() {
		return processTask(false);
	}
	
	private String processTask(boolean fromValidationDlgAction) {
		String ret = "TaskSubmited";
		resetTabs();
		if (task != null) {
			FacilityProfile fp = null;
			logger.debug("Task ID = " + task.getTaskId() + " Task type = "
					+ task.getTaskType() + " Task fpId = " + task.getFpId());
					
			switch (task.getTaskType()) {
			case FC:
			case FCH:
				if (!fromValidationDlgAction) {
					if (getFacility(task.getFpId(), task) == null) {
						DisplayUtil.displayError("Process " + task.getTaskDescription().toLowerCase() + " task failed.");
						return ret;
					}
				}
				ret = "facilityProfile";
				fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
				fp.setFacilityId(facility.getFacilityId());
				if (fromValidationDlgAction) {
					fp.setFpId(task.getFpId());
					fp.setTask(task);
				} else {
					if (fp.getFacility().isValidated()) {
						fp.updateFacilityValidity();
					}
				}
				DisplayUtil.clearQueuedMessages();
				((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_editFacilityProfile")).setDisabled(false);
				resetTabs();
				this.setStagingEditFlag(true);
				break;
			case FCC:
				fromHomeContact = false;
				fp = (FacilityProfile) FacesUtil
						.getManagedBean("facilityProfile");
				ret = fp.fromHomeContacts(true);
				fp.setContactTask(task);
				FacesUtil.renderSimpleMenuItem("menuItem_changeContact");
				break;
			case ER:
			case R_ER:
				ReportDetail rd = (ReportDetail) FacesUtil
						.getManagedBean("reportDetail");
				if (task.getTaskInternalId() != null) {
					rd.setReportId(task.getTaskInternalId());
					if (!fromValidationDlgAction) {
					  String rpRtn = rd.portalSubmit(task);
					  if (rpRtn != null) {
						ret = rpRtn;
					  } else {
						logger.error("Failed to read emissions inventory.  CorePlaceId = "
								+ this.corePlaceId
								+ ", Task ID = "
								+ task.getTaskId()
								+ ", Task type = "
								+ task.getTaskType()
								+ ", Task fpId = "
								+ task.getFpId());
					  }
					}
				}
				rd.setTask(task);
				resetTabs();
				this.setStagingEditFlag(true);
				break;
			case TVPA:
			case COPY_TVPA:
			case PTPA:
			case COPY_PTPA:
			case PBR:
			case RPC:
			case TIVPA:
			case DOR:
				ret = renderPermitApplicationMenu(task, false, fromValidationDlgAction);
				this.setStagingEditFlag(true);
				break;
			case CR_OTHR:
			case CR_TVCC:
			case CR_PER:
			case CR_TEST:
			case CR_CEMS:
			case CR_SMBR:
			case CR_ONE:
			case CR_GENERIC:	
				//ret = renderComplianceReportMenu(task,false);
				
				ComplianceReports complianceReports = (ComplianceReports) FacesUtil
						.getManagedBean("complianceReport");
				complianceReports.setEditable(true);
				task.setFacility(facility);
				complianceReports.setTask(task);
//				complianceReports.setEditMode(false);
				complianceReports.setReadOnlyReport(false);
				complianceReports.loadComplianceReport(task.getTaskInternalId(), false);
				complianceReports.getComplianceReport().setUserName(
						getUserName());
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_compReportDetail"))
						.setRendered(true);
				ret = "complianceDetail";
				ComplianceReport c = task.getComplianceReport();
				
				
				//if(!complianceReports.getComplianceReport().isValid()){
					//complianceReports.setPassedValidation(false);
				complianceReports.setPassedValidation(complianceReports.getComplianceReport().getValidated());
				//}
				
				// if(c != null) {
				// logger.error("INFO: value of receivedDate is " +
				// c.getReceivedDate() + " for compliance report " +
				// c.getReportId());
				// } else {
				// logger.error("INFO: value of c is null");
				// }
				this.setStagingEditFlag(true);
				break;
			case REL:
				Relocation relocation = (Relocation) FacesUtil
						.getManagedBean("relocation");
				relocation.setFacility(facility);
				relocation.setEditable(false);
				relocation.setPortalRelReq(task.getTaskInternalId());
				relocation.setTask(task);
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_applications"))
						.setRendered(true);
				ret = "relocateDetail";
				break;
			case ST:
				ret = renderStackTestMenu(task, false, fromValidationDlgAction);
				this.setStagingEditFlag(true);
				break;
			case MRPT:
				ret = renderUploadMonitorReportMenu(task, false, fromValidationDlgAction);
				this.setStagingMonitorReportUploadFlag(true);
				break;
			default:
				break;
			}
		}
		return ret;
	}

	private void setStagingEditFlag(boolean isEdit) {
		FacilityProfile fp = (FacilityProfile) FacesUtil
				.getManagedBean("facilityProfile");
		fp.setEditStaging(isEdit);
	}

	public void resetTabs() {
		this.setStagingEditFlag(false);
		this.setStagingMonitorReportUploadFlag(false);

		hideTabs();

		if (tasks != null) {
			// show tabs for current tasks
			for (Task currentTask : tasks.values()) {
				logger.debug("Task id: " + currentTask.getTaskId()
						+ " Task Type: " + currentTask.getTaskType());
				showTabs(currentTask);
			}

		}

		addTabs();

		FacesUtil.renderSimpleMenuItem("menuItem_cmpSelector");
		FacesUtil.renderSimpleMenuItem("menuItem_facSelector");
		FacesUtil.renderSimpleMenuItem("menuItem_home");

		return;
	}

	private void addTabs() {
		if (tasks != null) {
			String appMenuNamePrefix = "application_task_";
			String emissionsMenuNamePrefix = "emissions_task_";
			String facilityMenuNamePrefix = "facility_task_";
			String complianceMenuNamePrefix = "compliance_task_";
			String stackTestMenuNamePrefix = "stacktest_task_";
			String monitorReportMenuNamePrefix = "monitor_report_task_";
			
			SimpleMenuTree mainMenu = (SimpleMenuTree) FacesUtil
					.getManagedBean("menuModel");

			List<SimpleMenuItem> menuItems = mainMenu.getChildren();

			List<SimpleMenuItem> newItems = new ArrayList<SimpleMenuItem>();

			for (Task task : tasks.values()) {
				switch (task.getTaskType()) {
				case TVPA:
				case COPY_TVPA:
				case PTPA:
				case COPY_PTPA:
				case PBR:
				case RPC:
				case TIVPA:
				case DOR:
					String menuName = appMenuNamePrefix + task.getTaskId();
					boolean exists = false;

					for (SimpleMenuItem menuItem : menuItems) {
						exists = menuItem.getName().equals(menuName);
						if (exists) {
							menuItem.setRendered(true);
							if (taskId != null
									&& taskId.equals(task.getTaskId())) {
								if (FacesUtil.getCurrentPage() == null) {
									menuItem.setSelected(true);
								} else if (FacesUtil.getCurrentPage() != null
										&& !FacesUtil.getCurrentPage().equals(
												"homeApplicationDetail")) {
									menuItem.setSelected(true);

									if (menuItem.getChildren() != null
											&& menuItem.getChildren().size() > 0) {
										menuItem.getChildren().get(0)
												.setSelected(true);
									}
								} else {
									menuItem.setSelected(false);
								}

								menuItem.setOutcome("method.myTasks.goApplicationDetailChange");
							} else {
								menuItem.setSelected(false);
							}
							break;
						}

					}

					if (!exists) {
						SimpleMenuItem applicationItem = new SimpleMenuItem();
						applicationItem.setLabel("Task - "
								+ task.getTaskDescription());
						applicationItem.setName(menuName);
						applicationItem.setRendered(true);

						applicationItem
								.setOutcome("method.myTasks.goApplicationDetailChange");
						applicationItem.setTaskId(task.getTaskId());
						applicationItem
								.setBeanName(SimpleMenuItem.APP_RESERVED_BEAN_NAME
										+ "_" + task.getTaskId());

						SimpleMenuItem subApplicationItem = new SimpleMenuItem();
						subApplicationItem = (SimpleMenuItem) FacesUtil
								.getManagedBean("menuItem_appDetail");
						List<SimpleMenuItem> applicationItemSubMenu = new ArrayList<SimpleMenuItem>();
						applicationItemSubMenu.add(subApplicationItem);
						applicationItem.setChildren(applicationItemSubMenu);

						List<String> viewIds = new ArrayList<String>();
						viewIds.add("/applications/applicationDetail.jsp");
						applicationItem.setViewIDs(viewIds);
						newItems.add(applicationItem);
					}

					break;
				case FC:
				case FCH:
					String facilityMenuName = facilityMenuNamePrefix + task.getTaskId();
					boolean facilityExists = false;
					for (SimpleMenuItem menuItem : menuItems) {
						facilityExists = menuItem.getName().equals(facilityMenuName);
						if (facilityExists) {
							menuItem.setRendered(true);
							if (taskId != null && taskId.equals(task.getTaskId())) {
								if (FacesUtil.getCurrentPage() == null) {
									menuItem.setSelected(true);
								} else if (FacesUtil.getCurrentPage() != null /*&& !FacesUtil.getCurrentPage().equals("homeFacilityProfile")*/) {
									menuItem.setSelected(true);
									if (menuItem.getChildren() != null	&& menuItem.getChildren().size() > 0) {
										List<SimpleMenuItem> children_menuItems = menuItem.getChildren();
										for (SimpleMenuItem children_menuItem : children_menuItems) {
											if (children_menuItem.getName().equals("facilities.profile")) {
												children_menuItem.setSelected(true);
												break;
											}
										}
									}
									menuItem.setOutcome("method.myTasks.goFacilityDetailChange");
								} else {
									menuItem.setSelected(false);
								}
							} else {
								menuItem.setSelected(false);
							}
							break;
						}
					}					
					
					if (!facilityExists) {
						SimpleMenuItem facilityItem = new SimpleMenuItem();
						facilityItem.setLabel("Task - "	+ task.getTaskDescription());
						facilityItem.setName(facilityMenuName);
						facilityItem.setRendered(true);
						facilityItem.setOutcome("method.myTasks.goFacilityDetailChange");
						facilityItem.setTaskId(task.getTaskId());
						facilityItem.setBeanName(SimpleMenuItem.FACILITY_RESERVED_BEAN_NAME + "_" + task.getTaskId());

						SimpleMenuItem facilityMenu = new SimpleMenuItem();
						facilityMenu = (SimpleMenuItem) FacesUtil.getManagedBean("menuItem_editFacilityProfile");
						List<SimpleMenuItem> facilityMenuSubMenu = new ArrayList<SimpleMenuItem>();
						facilityMenuSubMenu.add(facilityMenu);
						facilityItem.setChildren(facilityMenuSubMenu);

						List<String> viewIds = new ArrayList<String>();
						viewIds.add("/facilities/facilityProfile.jsp");
						facilityItem.setViewIDs(viewIds);
						newItems.add(facilityItem);
					}
					
					break;					
				case ER:
				case R_ER:
					String reportMenuName = emissionsMenuNamePrefix + task.getTaskId();
					boolean reportExists = false;

					for (SimpleMenuItem menuItem : menuItems) {
						reportExists = menuItem.getName()
								.equals(reportMenuName);
						if (reportExists) {
							menuItem.setRendered(true);
							if (taskId != null
									&& taskId.equals(task.getTaskId())) {
								if (FacesUtil.getCurrentPage() == null) {
									menuItem.setSelected(true);
								} else if (FacesUtil.getCurrentPage() != null
										&& !FacesUtil.getCurrentPage().equals(
												"homeReportDetail")) {
									menuItem.setSelected(true);

									if (menuItem.getChildren() != null
											&& menuItem.getChildren().size() > 0) {
										menuItem.getChildren().get(0)
												.setSelected(true);
									}

									menuItem.setOutcome("method.myTasks.goReportDetailChange");
								} else {
									menuItem.setSelected(false);
								}
							} else {
								menuItem.setSelected(false);
							}
							break;
						}

					}					
					
					
					if (!reportExists) {
						SimpleMenuItem reportItem = new SimpleMenuItem();
						reportItem.setLabel("Task - "
								+ task.getTaskDescription());
						reportItem.setName(reportMenuName);
						reportItem.setRendered(true);

						reportItem
								.setOutcome("method.myTasks.goReportDetailChange");
						reportItem.setTaskId(task.getTaskId());
						reportItem
								.setBeanName(SimpleMenuItem.REPORT_RESERVED_BEAN_NAME
										+ "_" + task.getTaskId());

						SimpleMenuItem reportMenu = new SimpleMenuItem();
						reportMenu = (SimpleMenuItem) FacesUtil
								.getManagedBean("menuItem_TVDetail");
						List<SimpleMenuItem> reportMenuSubMenu = new ArrayList<SimpleMenuItem>();
						reportMenuSubMenu.add(reportMenu);
						reportItem.setChildren(reportMenuSubMenu);

						List<String> viewIds = new ArrayList<String>();
						viewIds.add("/reports/reportDetail.jsp");
						reportItem.setViewIDs(viewIds);
						newItems.add(reportItem);
					}
					
					break;
				case CR_OTHR:
				case CR_TVCC:
				case CR_PER:
				case CR_TEST:
				case CR_CEMS:
				case CR_SMBR:
				case CR_ONE:
				case CR_GENERIC:	
					String complianceMenuName = complianceMenuNamePrefix + task.getTaskId();
					boolean complianceExists = false;

					for (SimpleMenuItem menuItem : menuItems) {
						complianceExists = menuItem.getName().equals(complianceMenuName);
						if (complianceExists) {
							menuItem.setRendered(true);
							logger.debug("taskId = " + taskId);
							if (taskId != null
									&& taskId.equals(task.getTaskId())) {
								logger.debug("taskId != null && taskId.equals(task.getTaskId())");
								if (FacesUtil.getCurrentPage() == null) {
									logger.debug("getCurrentPage() == null");
									menuItem.setSelected(true);
								} else if (FacesUtil.getCurrentPage() != null
										&& !FacesUtil.getCurrentPage().equals(
												"homeComReportDetail")) {
									menuItem.setSelected(true);
									logger.debug("else getCurrentPage() == null");

									if (menuItem.getChildren() != null
											&& menuItem.getChildren().size() > 0) {
										logger.debug("getChildren");
										menuItem.getChildren().get(0)
												.setSelected(true);
									}
								} else {
									menuItem.setSelected(false);
								}

								menuItem.setOutcome("method.myTasks.goComplianceReportChange");
							} else {
								logger.debug("else taskId != null && taskId.equals(task.getTaskId())");
								menuItem.setSelected(false);
							}
							break;
						}

					}

					if (!complianceExists) {
						SimpleMenuItem complianceItem = new SimpleMenuItem();
						complianceItem.setLabel("Task - "
								+ task.getTaskDescription());
						complianceItem.setName(complianceMenuName);
						complianceItem.setRendered(true);

						complianceItem
								.setOutcome("method.myTasks.goComplianceReportChange");
						complianceItem.setTaskId(task.getTaskId());
						complianceItem
								.setBeanName(SimpleMenuItem.COMPLIANCE_RESERVED_BEAN_NAME
										+ "_" + task.getTaskId());
						SimpleMenuItem subComplianceItem = new SimpleMenuItem();
						subComplianceItem = (SimpleMenuItem) FacesUtil
								.getManagedBean("menuItem_compReportDetail");
						List<SimpleMenuItem> complianceItemSubMenu = new ArrayList<SimpleMenuItem>();
						complianceItemSubMenu.add(subComplianceItem);
						complianceItem.setChildren(complianceItemSubMenu);

						List<String> viewIds = new ArrayList<String>();
						viewIds.add("/compliance/compReportDetail.jsp");
						viewIds.add("/compliance/valTradSecNotif.jsp");
						complianceItem.setViewIDs(viewIds);
						newItems.add(complianceItem);
					}

					break;
					
				case ST:
					String stackTestMenuName = stackTestMenuNamePrefix + task.getTaskId();
					boolean stackTestExists = false;

					for (SimpleMenuItem menuItem : menuItems) {
						stackTestExists = menuItem.getName().equals(stackTestMenuName);
						if (stackTestExists) {
							menuItem.setRendered(true);
							if (taskId != null
									&& taskId.equals(task.getTaskId())) {
								if (FacesUtil.getCurrentPage() == null) {
									menuItem.setSelected(true);
								} else if (FacesUtil.getCurrentPage() != null
										&& !FacesUtil.getCurrentPage().equals(
												"homeStackTestDetail")) {
									menuItem.setSelected(true);

									if (menuItem.getChildren() != null
											&& menuItem.getChildren().size() > 0) {
										menuItem.getChildren().get(0)
												.setSelected(true);
									}
								} else {
									menuItem.setSelected(false);
								}

								menuItem.setOutcome("method.myTasks.goStackTestDetailChange");
							} else {
								menuItem.setSelected(false);
							}
							break;
						}

					}

					if (!stackTestExists) {
						SimpleMenuItem stackTestItem = new SimpleMenuItem();
						stackTestItem.setLabel("Task - "
								+ task.getTaskDescription());
						stackTestItem.setName(stackTestMenuName);
						stackTestItem.setRendered(true);

						stackTestItem
								.setOutcome("method.myTasks.goStackTestDetailChange");
						stackTestItem.setTaskId(task.getTaskId());
						stackTestItem
								.setBeanName(SimpleMenuItem.STACKTEST_RESERVED_BEAN_NAME
										+ "_" + task.getTaskId());

						SimpleMenuItem subStackTestItem = new SimpleMenuItem();
						subStackTestItem = (SimpleMenuItem) FacesUtil
								.getManagedBean("menuItem_stackTestDetail");
						List<SimpleMenuItem> stackTestItemSubMenu = new ArrayList<SimpleMenuItem>();
						stackTestItemSubMenu.add(subStackTestItem);
						stackTestItem.setChildren(stackTestItemSubMenu);

						List<String> viewIds = new ArrayList<String>();
						viewIds.add("/ceta/stackTestDetail.jsp");
						viewIds.add("/ceta/stackTestDetail2.jsp");
						stackTestItem.setViewIDs(viewIds);
						newItems.add(stackTestItem);
					}

					break;
				case MRPT:
					String monitorReportMenuName = monitorReportMenuNamePrefix + task.getTaskId();
					boolean monitorReportExists = false;
					for (SimpleMenuItem menuItem : menuItems) {
						monitorReportExists = menuItem.getName().equals(monitorReportMenuName);
						if (monitorReportExists) {
							menuItem.setRendered(true);
							if (taskId != null && taskId.equals(task.getTaskId())) {
								if (FacesUtil.getCurrentPage() == null) {
									menuItem.setSelected(true);
								} else if (FacesUtil.getCurrentPage() != null && !FacesUtil.getCurrentPage().equals("homeMonitorGroup")) {
									menuItem.setSelected(true);
									if (menuItem.getChildren() != null
											&& menuItem.getChildren().size() > 0) {
										menuItem.getChildren().get(0)
												.setSelected(true);
									}
								} else {
									menuItem.setSelected(false);
								}
								menuItem.setOutcome("method.myTasks.goUploadMonitorReport");
							} else {
								menuItem.setSelected(false);
							}
							break;
						}
					}					
					
					if (!monitorReportExists) {
						SimpleMenuItem monitorReportItem = new SimpleMenuItem();
						monitorReportItem.setLabel("Task - "	+ task.getTaskDescription());
						monitorReportItem.setName(monitorReportMenuName);
						monitorReportItem.setRendered(true);
						monitorReportItem.setOutcome("method.myTasks.goUploadMonitorReport");
						monitorReportItem.setTaskId(task.getTaskId());
						monitorReportItem.setBeanName(SimpleMenuItem.MONITOR_REPORT_RESERVED_BEAN_NAME + "_" + task.getTaskId());

						SimpleMenuItem monitorReportMenu = new SimpleMenuItem();
						monitorReportMenu = (SimpleMenuItem) FacesUtil.getManagedBean("menuItem_uploadMonitorReport");
						
						List<SimpleMenuItem> monitorReportMenuSubMenu = new ArrayList<SimpleMenuItem>();
						monitorReportMenuSubMenu.add(monitorReportMenu);
						monitorReportItem.setChildren(monitorReportMenuSubMenu);

						List<String> viewIds = new ArrayList<String>();
						viewIds.add("/monitoring/monitorGroup.jsp");
						viewIds.add("/monitoring/monitorSite.jsp");
						viewIds.add("/monitoring/monitor.jsp");
						monitorReportItem.setViewIDs(viewIds);
						newItems.add(monitorReportItem);
					}
					
					break;					
				default:

					break;
				}
			}

			menuItems.addAll(newItems);
			mainMenu.setChildren(menuItems);
		}
	}

	public void hideTabs() {
		SimpleMenuTree mainMenu = (SimpleMenuTree) FacesUtil
				.getManagedBean("menuModel");

		List<SimpleMenuItem> menuItems = mainMenu.getChildren();
		for (SimpleMenuItem menuItem : menuItems) {
			if (menuItem.getBeanName().equals("menuItem_cmpSelector")
					|| menuItem.getBeanName().equals("menuItem_facSelector")
					|| menuItem.getBeanName().equals("menuItem_cromerrHome")) {
				continue;
			} else {
				menuItem.setRendered(false);
			}
		}
	}

	private void showTabs(Task task) {
		if (task == null) {
			return;
		}

		switch (task.getTaskType()) {
		case FCC:
			FacesUtil.renderSimpleMenuItem("menuItem_changeContact");
			break;

		case FCH:
		case FC:
			//FacesUtil.renderSimpleMenuItem("menuItem_facility");
			FacesUtil.renderSimpleMenuItem("menuItem_changeContact");

		case CR_OTHR:
		case CR_TVCC:
		case CR_PER:
		case CR_ONE:
		case CR_CEMS:
		case CR_GENERIC:	
			//FacesUtil.renderSimpleMenuItem("menuItem_compliance");
			break;

		case PBR:
		case PTPA:
		case RPC:
		case TVPA:
		case COPY_PTPA:
		case COPY_TVPA:
		case TIVPA:
		case DOR:
			// FacesUtil.renderSimpleMenuItem("menuItem_applications");
			break;

		case ER:
		case R_ER:
			//FacesUtil.renderSimpleMenuItem("menuItem_emissionsReport");
			break;
		case ST:
			//FacesUtil.renderSimpleMenuItem("menuItem_stacktests");
			break;
		case MRPT:
			//FacesUtil.renderSimpleMenuItem("menuItem_stacktests");
			break;
		default:
			break;
		}
	}

	private void resetTabs(Task task) {
		if (task == null) {
			resetTabs();
			return;
		}

		switch (task.getTaskType()) {
		case FCC:
			FacesUtil.hideSimpleMenuItem("menuItem_changeContact");
			break;

		case FCH:
			FacesUtil.hideSimpleMenuItem("menuItem_facility");

		case CR_OTHR:
		case CR_TVCC:
		case CR_PER:
		case CR_TEST:
		case CR_CEMS:
		case CR_SMBR:
		case CR_ONE:
		case CR_GENERIC:	
			FacesUtil.hideSimpleMenuItem("menuItem_compliance");
			break;

		case PBR:
		case PTPA:
		case RPC:
		case TVPA:
		case COPY_PTPA:
		case COPY_TVPA:
		case TIVPA:
		case DOR:
			FacesUtil.hideSimpleMenuItem("menuItem_applications");
			break;

		case ER:
		case R_ER:
			FacesUtil.hideSimpleMenuItem("menuItem_emissionsReport");
			break;
		case ST:
			FacesUtil.hideSimpleMenuItem("menuItem_stacktests");
			break;
		case MRPT:
			FacesUtil.hideSimpleMenuItem("menuItem_monitorGroupDetail");
			break;
		default:
			break;
		}
	}

	public final CreateMenuItem[] getFacilityMenuItems() {
		getMenuItems();

		return facilityMenuItems.toArray(new CreateMenuItem[0]);
	}

	public final CreateMenuItem[] getPermitMenuItems() {
		getMenuItems();
		return permitMenuItems.toArray(new CreateMenuItem[0]);
	}

	public final CreateMenuItem[] getEmissionsMenuItems() {
		getMenuItems();

		return emissionsMenuItems.toArray(new CreateMenuItem[0]);
	}

	public final CreateMenuItem[] getComplianceMenuItems() {
		getMenuItems();

		return complianceMenuItems.toArray(new CreateMenuItem[0]);
	}
	
	public final CreateMenuItem[] getMonitoringMenuItems() {
		getMenuItems();

		return monitoringMenuItems.toArray(new CreateMenuItem[0]);
	}
	
	public final CreateMenuItem[] getRightMenuItems() {
		getMenuItems();

		return rightMenuItems.toArray(new CreateMenuItem[0]);
	}

	public final void getMenuItems() {
		// if the size is 0, there is either a problem or the list has been
		// loaded yet.
		// TODO add equivalent rightMenuItems.size() == 0 for reports
		if (facilityMenuItems.size() == 0 
				|| permitMenuItems.size() == 0
				|| emissionsMenuItems.size() == 0
				|| complianceMenuItems.size() == 0
				|| monitoringMenuItems.size() == 0) {
			for (Node node : Config.findNodes("app.portalCreateMenu.menuItem")) {
				if (node.getAsString("column").equalsIgnoreCase("facility")) {
					facilityMenuItems.add(new CreateMenuItem(node
							.getAsString("title"), node.getAsString("outcome"),
							node.getAsString("explainText"), node.getAsBoolean(
									"popup", false), node.getAsBoolean(
									"disabled", false), node
									.getAsString("width"), node
									.getAsString("height")));
				} else if (node.getAsString("column").equalsIgnoreCase("permit")) {
					if (isImpactFullEnabled() || (!isImpactFullEnabled() 
							&& "true".equalsIgnoreCase(node.getAsString("restrictedMode")))) {
						permitMenuItems.add(new CreateMenuItem(node
								.getAsString("title"), node.getAsString("outcome"),
								node.getAsString("explainText"), node.getAsBoolean(
										"popup", false), node.getAsBoolean(
												"disabled", false), node
												.getAsString("width"), node
												.getAsString("height")));

					}
					
				} else if (node.getAsString("column").equalsIgnoreCase(
						"emissions")) {
					emissionsMenuItems.add(new CreateMenuItem(node
							.getAsString("title"), node.getAsString("outcome"),
							node.getAsString("explainText"), node.getAsBoolean(
									"popup", false), node.getAsBoolean(
									"disabled", false), node
									.getAsString("width"), node
									.getAsString("height")));

				} else if (node.getAsString("column").equalsIgnoreCase(
						"compliance")) {
					complianceMenuItems.add(new CreateMenuItem(node
							.getAsString("title"), node.getAsString("outcome"),
							node.getAsString("explainText"), node.getAsBoolean(
									"popup", false), node.getAsBoolean(
									"disabled", false), node
									.getAsString("width"), node
									.getAsString("height")));

				} else if (node.getAsString("column").equalsIgnoreCase(
						"monitoring")) {
							monitoringMenuItems.add(new CreateMenuItem(node
							.getAsString("title"), node.getAsString("outcome"),
							node.getAsString("explainText"), node.getAsBoolean(
									"popup", false), node.getAsBoolean(
									"disabled", false), node
									.getAsString("width"), node
									.getAsString("height")));

				} else {

					boolean addit = false;

					// we only include the TV Compliance report option if the
					// facility has at least one issued Title V Permit
					if (!node.getAsString("outcome").equals(
							"createCompReportTVCC")) {
						addit = true;
						if (node.getAsString("outcome")
								.equals("createIntToRel")
								&& !facility.getPortable()) {
							addit = false;
						}
					} else if (node.getAsString("outcome").equals(
							"createCompReportTVCC")
							&& hasTVPTO) {
						if (hasTVPTO) {
							addit = true;
						}
					}
					if (addit) {
						rightMenuItems.add(new CreateMenuItem(node
								.getAsString("title"), node
								.getAsString("outcome"), node
								.getAsString("explainText"), node.getAsBoolean(
								"popup", false), node.getAsBoolean("disabled",
								false), node.getAsString("width"), node
								.getAsString("height")));

					}
				}
			}
		}
		
		
		return;
	}

	public final void deleteTask(ActionEvent actionEvent) {
		try {
			switch (selDelTask.getTaskType()) {
			case CR_OTHR:
			case CR_PER:
			case CR_TVCC:
			case CR_TEST:
			case CR_CEMS:
			case CR_SMBR:
			case CR_ONE:
			case REL:
			case CR_GENERIC:
			case ST:
			case R_ER:
			case ER:
			case COPY_PTPA:
			case PTPA:
			case COPY_TVPA:
			case TVPA:
			case MRPT:
				// special case since we need to delete any attachment files
				getInfrastructureService().deleteTask(selDelTask, true);

				break;
			default:
				getInfrastructureService().deleteTask(selDelTask, false);
				break;
			}
			removeTaskFromMyTasks(selDelTask);

		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Delete task failed.");
		}
		FacesUtil.returnFromDialogAndRefresh();
	}

	public boolean removeTaskFromMyTasks(Task task) throws RemoteException {
		boolean ret = true;
		Task facTask = null;

		if (task.getDependent()) {
			switch (task.getTaskType()) {
			case FC:
			case FCH:
				// in case of creating facility and remove task without
				// submiting it.
				facility = getFacilityService().retrieveFacility(
						task.getFacilityId());
				if (facility == null) {
					DisplayUtil
							.displayInfo("Facility does not exist for new tasks.");
					for (SimpleMenuItem tempMenu : ((SimpleMenuItem) FacesUtil
							.getManagedBean("menuItem_mytasks")).getChildren()) {
						tempMenu.setDisabled(true);
					}
					tasks.remove(task.getTaskId());
				}
				break;
			default:
				facTask = getInfrastructureService().retrieveTask(
						task.getDependentTaskId());
				if (facTask == null) {
					FacesUtil.hideSimpleMenuItem("menuItem_facility");
				}
			}
		} else {
			switch (task.getTaskType()) {
			case CR_OTHR:
			case CR_TVCC:
			case CR_PER:
			case CR_ONE:
			case CR_CEMS:	
			case CR_GENERIC:	
				FacesUtil.hideSimpleMenuItem("menuItem_compliance");
				break;

			case REL:
				FacesUtil.hideSimpleMenuItem("menuItem_applications");
				break;
			case ST:
				logger.debug("case ST");
				FacesUtil.hideSimpleMenuItem("menuItem_stacktests");
				break;
			case MRPT:
				logger.debug("case MRPT");
				FacesUtil.hideSimpleMenuItem("menuItem_monitorGroupDetail");
				break;

			default:
				break;
			}
		}

		resetTabs(task);
		// TODO remove reference to core place id
		// refreshTasks(this.corePlaceId);
		refreshTasks(this.facilityId);

		return ret;
	}

	/*
	 * from Task : 1 --> from Fac Change 2 --> from create facility 3 --> others
	 */
	public final Task changeFacilityProfileTask(int fromTask) {
		for (Task task : tasks.values().toArray(new Task[0])) {
			if (task.getTaskType().equals(Task.TaskType.FC)) {
				if (fromTask == 1) {
					DisplayUtil
							.displayError("Facility change  request failed. There is already a task in progress.");
					return null;
				}
				task.setFacility(facility); // so facility name will appear with
											// task description
				return task;
			}
		}

		Task newTask = createNewTask("Facility Inventory Change",
				Task.TaskType.FC, true, null, facility.getFacilityId(), 0,
				"current", this.corePlaceId, loginName);

		try {
			if (fromTask != 2) {
				facility = getFacilityService().retrieveFacility(
						facility.getFacilityId());
				if (facility == null) {
					DisplayUtil.displayError("Facility does not exist.");
					return null;
				}
			}
			newTask = changeFacilityProfileTask(fromTask, facility.getFpId(),
					newTask);
			if (newTask != null) {
				newTask.setFacility(facility); // so facility name will appear
												// with task description
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Create task failed");
			newTask = null;
		}
			
		// submitTask();
		return newTask;
	}

	public final Task changeHistFacilityProfileTask(Integer fpId,
			String taskDesc) {
		for (Task task : tasks.values().toArray(new Task[0])) {
			if (task.getTaskType().equals(Task.TaskType.FCH)
					&& task.getFpId().equals(fpId)) {
				task.setFacility(facility); // so facility name will appear with
											// task description
				return task;
			}
		}

		Task newTask = createNewTask(taskDesc, Task.TaskType.FCH, true, null,
				facility.getFacilityId(), 0, "current", this.corePlaceId,
				loginName);

		newTask = changeFacilityProfileTask(3, fpId, newTask);
		// newTask = changeFacilityProfileTask(1, fpId, newTask);

		return newTask;
	}

	private final Task changeFacilityProfileTask(int fromTask, Integer fpId,
			Task newTask) {
		newTask.setFpId(fpId);
		newTask.setTaskInternalId(fpId);
		try {
			Task contactTask = createContactTask(2);
			if (contactTask == null) {
				return null;
			}

			if (fromTask != 3) {
				newTask.setReferenceCount(0);
				newTask = getInfrastructureService().createTask(newTask,
						contactTask);
				if (getFacility(newTask.getFpId(), newTask) == null) {
					DisplayUtil
							.displayError("Process facility change task request failed.");
					return null;
				}
				tasks.put(newTask.getTaskId(), newTask);
			} else {
				newTask.setReferenceCount(0);
				newTask.setDependentTaskId(contactTask.getTaskId());
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Create task failed");
			newTask = null;
		}

		return newTask;
	}

	public final String goApplicationDetailChange() {
		logger.debug("Made it to goApplicationDetailChange");
		String ret = HOME;

		if (taskId != null) {
			resetTabs();
			for (Task task : tasks.values().toArray(new Task[0])) {
				if (task.getTaskId().equals(taskId)) {
					setTask(task);
					ret = submitTask();
				}
			}
		}

		return ret;
	}
	
	public final String goReportDetailChange() {
		logger.debug("Made it to goReportDetailChange");
		String ret = HOME;

		if (taskId != null) {
			resetTabs();
			for (Task task : tasks.values().toArray(new Task[0])) {
				if (task.getTaskId().equals(taskId)) {
					setTask(task);
					ret = submitTask();
				}
			}
		}

		return ret;
	}

	public final String goFacilityDetailChange() {
		logger.debug("Made it to goFacilityDetailChange");
		String ret = HOME;

		if (taskId != null) {
			resetTabs();
			for (Task task : tasks.values().toArray(new Task[0])) {
				if (task.getTaskId().equals(taskId)) {
					setTask(task);
					ret = submitTask();
				}
			}
		}

		return ret;
	}
	
	public final String goFacilityInventoryChange() {
		String ret = HOME;
		for (Task task : tasks.values().toArray(new Task[0])) {
			if (task.getTaskType().equals(Task.TaskType.FC)) {
				// return existing task
				if (getFacility(task.getFpId(), task) == null) {
					DisplayUtil
							.displayError("Process facility change task failed.");
					return null;
				}
				FacilityProfile fp = (FacilityProfile) FacesUtil
						.getManagedBean("facilityProfile");
				fp.setFacilityId(facility.getFacilityId());
				if (fp.getFacility().isValidated()) {
					fp.updateFacilityValidity();
				}
				DisplayUtil.clearQueuedMessages();

				this.task = task;
				FacesUtil.renderSimpleMenuItem("menuItem_facility");

				this.setStagingEditFlag(true);

				ret = "facilityProfile";
			}
		}

		return ret;
	}
	
	public final String goComplianceReportChange() {
		logger.debug("Made it to goComplianceReportChange");
		String ret = HOME;

		if (taskId != null) {
			resetTabs();
			for (Task task : tasks.values().toArray(new Task[0])) {
				if (task.getTaskId().equals(taskId)) {
					setTask(task);
					ret = submitTask();
				}
			}
		}

		return ret;
	}
	
	public final String goStackTestDetailChange() {
		logger.debug("Made it to goStackTestDetailChange");
		String ret = HOME;

		if (taskId != null) {
			resetTabs();
			for (Task task : tasks.values().toArray(new Task[0])) {
				if (task.getTaskId().equals(taskId)) {
					setTask(task);
					ret = submitTask();
				}
			}
		}

		return ret;
	}
	
	public final String goUploadMonitorReport() {
		logger.debug("Made it to goUploadMonitorReport");
		String ret = HOME;

		if (taskId != null) {
			resetTabs();
			for (Task task : tasks.values().toArray(new Task[0])) {
				if (task.getTaskId().equals(taskId)) {
					setTask(task);
					ret = submitTask();
				}
			}
		}
		
		this.setStagingMonitorReportUploadFlag(true);

		return ret;
	}

	public final String changeFacilityProfileWithCloning() {
		String ret = null;
		if (HOME.equals(changeFacilityProfile())) {
			ret = HOME;
		} else {
			CloneFacility cloneFacility = (CloneFacility) FacesUtil
					.getManagedBean("cloneFacility");
			cloneFacility.resetCloneFacility();
			ret = "dialog:cloneFacility";
		}
		return ret;
	}

	public final String submitChangeFacilityProfileWithCloning() {
        FacesUtil.returnFromDialogAndRefresh();
        return changeFacilityProfile();
	}
	
	public final String changeFacilityProfile() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}

		try {
			String ret = HOME;

			FacilityProfile fp = (FacilityProfile) FacesUtil
					.getManagedBean("facilityProfile");
			fp.setFacilityId(facility.getFacilityId());

			// create task

			resetTabs();

			Task newTask = changeFacilityProfileTask(1);
			if (newTask != null) {
				FacesUtil.renderSimpleMenuItem("menuItem_facility");
				ret = "facilityProfile";
			}

			// TODO core place id reference
			// refreshTasks(this.corePlaceId);
			refreshTasks(this.facilityId);
			this.setStagingEditFlag(true);
			if(newTask != null && newTask.getTaskId() != null) {
				setTaskId(newTask.getTaskId());
				fp.setTask(newTask);
				resetTabs();
				fp.setEditStaging(true);
			}
			
			return ret;
		} finally {
			clearButtonClicked();
		}
	}

	public static Task createNewTask(String taskDesc, Task.TaskType taskType,
			boolean taskDepend, String taskDependId, String facId,
			Integer fpId, String version, Integer corePlaceId, String loginName) {
		Task newTask = new Task();

		newTask.setTaskDescription(taskDesc);
		newTask.setTaskType(taskType);
		newTask.setDependent(taskDepend);
		newTask.setDependentTaskId(taskDependId);
		newTask.setVersion(version);
		newTask.setFacilityId(facId);
		newTask.setFpId(fpId);
		newTask.setCorePlaceId(corePlaceId);
		newTask.setUserName(loginName);

		return newTask;
	}

	public boolean facilityExistsInternally() {
		try {
			// TODO Remove core place id references
			// Facility fac =
			// getFacilityService().retrieveFacilityByCorePlaceId(corePlaceId,
			// false);
			Facility fac = getFacilityService().retrieveFacilityByFacilityId(
					facilityId, false);
			if (fac == null) {
				DisplayUtil.displayError("Facility " + facility.getFacilityId()
						+ "with facility ID " + facilityId + " does not exist;"
						+ contactMsg);
				logger.error("Facility " + facility.getFacilityId()
						+ "with facility ID " + facilityId
						+ " does not exist; " + contactMsg);
				return false;
			}
		} catch (Exception e) {
			DisplayUtil
					.displayError("System error accessing facility internally");
			return false;
		}
		return true;
	}

	public final String createPTI_PTIOApp() { // Since new, it will be in
												// staging
		return startNewApplication(PTIOApplication.class);
	}

	public final String createTVPTOApp() { // Since new, it will be in staging
		return startNewApplication(TVApplication.class);
	}

	public final String createTIVApp() { // Since new, it will be in staging
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			String ret = null;

			// create a TIV Application and call createApplication directly
			// since no popup window is needed for this type of application.
			TIVApplication app = new TIVApplication();
			app.setFacility(facility);
			try {
				ret = createApplication(app);
				setPageRedirect(ret);
			} catch (RemoteException e) {
				// handleException is called in createApplication if there is
				// an exception, so just log a littler more info here.
				logger.error("Exception creating TIV application from portal");
			}
			return ret;
		} finally {
			clearButtonClicked();
		}
	}

	public final String createPBR() { // Since new, it will be in staging
		return startNewApplication(PBRNotification.class);
	}
/*
	public final String createCompReportPER() { // Since new compliance report
												// and in MyTasks, it will be in
												// staging.
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			ComplianceReports compReport = null;
			String ret = "";
			try {
				logger.debug("Creating CR PER task");
				if (!facilityExistsInternally()) {
					DisplayUtil.displayError("Facility "
							+ facility.getFacilityId() + "with core ID "
							+ corePlaceId + " does not exist; " + contactMsg);
					return ret;
				}

				Task newTask = createNewTask(
						ComplianceReportTypeDef
								.getData()
								.getItems()
								.getItemDesc(
										ComplianceReportTypeDef.COMPLIANCE_TYPE_PER),
						Task.TaskType.CR_PER, false, null,
						facility.getFacilityId(), null, "current",
						this.corePlaceId, loginName);
				if (newTask != null) {
					((SimpleMenuItem) FacesUtil
							.getManagedBean("menuItem_compliance"))
							.setRendered(true);
				}

				compReport = (ComplianceReports) FacesUtil
						.getManagedBean("complianceReport");
				ComplianceReportSearch compReportSearch = (ComplianceReportSearch) FacesUtil
						.getManagedBean("complianceReportSearch");
				compReportSearch.setFacilityId(facility.getFacilityId());
				compReport.setFacility(facility);
				compReport.startNewReport(
						ComplianceReportTypeDef.COMPLIANCE_TYPE_PER, newTask);
				ret = "dialog:newComplianceReport";
			} catch (Exception e) {
				handleException(new RemoteException(
						"Failed to create Compliance Report of type PER", e));
				DisplayUtil
						.displayError("System error initializing new Permit Eval report");
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_compliance"))
						.setRendered(false);
			}
			return ret;
		} finally {
			clearButtonClicked();
		}
	}

	public final String createCompReportTVCC() { // Since new, it will be in
													// staging
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			ComplianceReports compReport = null;
			String ret = "";
			try {
				logger.debug("Creating CR TVCC task");
				if (!facilityExistsInternally()) {
					DisplayUtil.displayError("Facility "
							+ facility.getFacilityId() + "with core ID "
							+ corePlaceId + " does not exist; " + contactMsg);
					return ret;
				}

				Task newTask = createNewTask(
						ComplianceReportTypeDef
								.getData()
								.getItems()
								.getItemDesc(
										ComplianceReportTypeDef.COMPLIANCE_TYPE_TVCC),
						Task.TaskType.CR_TVCC, false, null,
						facility.getFacilityId(), null, "current",
						this.corePlaceId, loginName);
				if (newTask != null) {
					((SimpleMenuItem) FacesUtil
							.getManagedBean("menuItem_compliance"))
							.setRendered(true);
				}

				compReport = (ComplianceReports) FacesUtil
						.getManagedBean("complianceReport");
				ComplianceReportSearch compReportSearch = (ComplianceReportSearch) FacesUtil
						.getManagedBean("complianceReportSearch");
				compReportSearch.setFacilityId(facility.getFacilityId());
				compReport.setFacility(facility);
				compReport.startNewReport(
						ComplianceReportTypeDef.COMPLIANCE_TYPE_TVCC, newTask);
				ret = "dialog:newComplianceReport";
			} catch (Exception e) {
				handleException(new RemoteException(
						"Failed to create Compliance Report of type TVCC", e));
				DisplayUtil
						.displayError("System error initializing new Title V Compliance report");
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_compliance"))
						.setRendered(false);
			}
			return ret;
		} finally {
			clearButtonClicked();
		}
	}

	public final String createCompReportSmbr() { // Since new, it will be in
													// staging
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			ComplianceReports compReport = null;
			String ret = "";
			try {
				// logger.debug("Creating CR Other task");
				if (!facilityExistsInternally()) {
					DisplayUtil.displayError("Facility "
							+ facility.getFacilityId() + "with core ID "
							+ corePlaceId + " does not exist; " + contactMsg);
					return ret;
				}

				Task newTask = createNewTask(
						ComplianceReportTypeDef
								.getData()
								.getItems()
								.getItemDesc(
										ComplianceReportTypeDef.COMPLIANCE_TYPE_SMBR),
						Task.TaskType.CR_SMBR, false, null,
						facility.getFacilityId(), null, "current",
						this.corePlaceId, loginName);
				if (newTask != null) {
					((SimpleMenuItem) FacesUtil
							.getManagedBean("menuItem_compliance"))
							.setRendered(true);
				}

				compReport = (ComplianceReports) FacesUtil
						.getManagedBean("complianceReport");
				ComplianceReportSearch compReportSearch = (ComplianceReportSearch) FacesUtil
						.getManagedBean("complianceReportSearch");
				compReportSearch.setFacilityId(facility.getFacilityId());
				compReport.setFacility(facility);
				compReport.startNewReport(
						ComplianceReportTypeDef.COMPLIANCE_TYPE_SMBR, newTask);
				ret = "dialog:newComplianceReport";
			} catch (Exception e) {
				handleException(new RemoteException(
						"Failed to create Compliance Report of type SMBR", e));
				DisplayUtil
						.displayError("System error initializing new SMBR report");
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_compliance"))
						.setRendered(false);
			}
			return ret;
		} finally {
			clearButtonClicked();
		}
	}

	public final String createCompReportOther() { // Since new, it will be in
													// staging
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			ComplianceReports compReport = null;
			String ret = "";
			try {
				// logger.debug("Creating CR Other task");
				if (!facilityExistsInternally()) {
					DisplayUtil.displayError("Facility " + facility.getFacilityId()
							+ "with facility ID " + facilityId + " does not exist; "
							+ contactMsg);
					return ret;
				}

				Task newTask = createNewTask(
						ComplianceReportTypeDef
								.getData()
								.getItems()
								.getItemDesc(
										ComplianceReportTypeDef.COMPLIANCE_TYPE_OTHER),
						Task.TaskType.CR_OTHR, false, null,
						facility.getFacilityId(), null, "current",
						this.corePlaceId, loginName);
				if (newTask != null) {
					((SimpleMenuItem) FacesUtil
							.getManagedBean("menuItem_compliance"))
							.setRendered(true);
				}

				compReport = (ComplianceReports) FacesUtil
						.getManagedBean("complianceReport");
				ComplianceReportSearch compReportSearch = (ComplianceReportSearch) FacesUtil
						.getManagedBean("complianceReportSearch");
				compReportSearch.setFacilityId(facility.getFacilityId());
				compReport.setFacility(facility);
				compReport.startNewReport(
						ComplianceReportTypeDef.COMPLIANCE_TYPE_OTHER, newTask);
				ret = "dialog:newComplianceReport";
			} catch (Exception e) {
				handleException(new RemoteException(
						"Failed in Creating CR Other task", e));
				DisplayUtil
						.displayError("System error initializing new Other report");
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_compliance"))
						.setRendered(false);
			}
			return ret;
		} finally {
			clearButtonClicked();
		}
	}

	public final String createCompReportTesting() { // Since new, it will be in
													// staging
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			ComplianceReports compReport = null;
			String ret = "";
			try {
				if (!facilityExistsInternally()) {
					DisplayUtil.displayError("Facility "
							+ facility.getFacilityId() + "with core ID "
							+ corePlaceId + " does not exist; " + contactMsg);
					return ret;
				}

				Task newTask = createNewTask(
						ComplianceReportTypeDef
								.getData()
								.getItems()
								.getItemDesc(
										ComplianceReportTypeDef.COMPLIANCE_TYPE_TESTING),
						Task.TaskType.CR_TEST, false, null,
						facility.getFacilityId(), null, "current",
						this.corePlaceId, loginName);
				if (newTask != null) {
					((SimpleMenuItem) FacesUtil
							.getManagedBean("menuItem_compliance"))
							.setRendered(true);
				}

				compReport = (ComplianceReports) FacesUtil
						.getManagedBean("complianceReport");
				ComplianceReportSearch compReportSearch = (ComplianceReportSearch) FacesUtil
						.getManagedBean("complianceReportSearch");
				compReportSearch.setFacilityId(facility.getFacilityId());
				compReport.setFacility(facility);
				compReport.startNewReport(
						ComplianceReportTypeDef.COMPLIANCE_TYPE_TESTING,
						newTask);
				ret = "dialog:newComplianceReport";
			} catch (Exception e) {
				handleException(new RemoteException(
						"Failed in creating Compliance Report of type testing",
						e));
				DisplayUtil
						.displayError("System error initializing new Testing report");
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_compliance"))
						.setRendered(false);
			}
			return ret;
		} finally {
			clearButtonClicked();
		}
	}

	public final String createCompReportCems() { // Since new, it will be in
													// staging
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			ComplianceReports compReport = null;
			String ret = "";
			try {
				if (!facilityExistsInternally()) {
					DisplayUtil.displayError("Facility "
							+ facility.getFacilityId() + "with core ID "
							+ corePlaceId + " does not exist; " + contactMsg);
					return ret;
				}

				Task newTask = createNewTask(
						ComplianceReportTypeDef
								.getData()
								.getItems()
								.getItemDesc(
										ComplianceReportTypeDef.COMPLIANCE_TYPE_CEMS),
						Task.TaskType.CR_CEMS, false, null,
						facility.getFacilityId(), null, "current",
						this.corePlaceId, loginName);
				if (newTask != null) {
					((SimpleMenuItem) FacesUtil
							.getManagedBean("menuItem_compliance"))
							.setRendered(true);
				}

				compReport = (ComplianceReports) FacesUtil
						.getManagedBean("complianceReport");
				ComplianceReportSearch compReportSearch = (ComplianceReportSearch) FacesUtil
						.getManagedBean("complianceReportSearch");
				compReportSearch.setFacilityId(facility.getFacilityId());
				compReport.setFacility(facility);
				compReport.startNewReport(
						ComplianceReportTypeDef.COMPLIANCE_TYPE_CEMS, newTask);
				ret = "dialog:newComplianceReport";
			} catch (Exception e) {
				handleException(new RemoteException(
						"Failed to create Compliance Report of type CEMS", e));
				DisplayUtil
						.displayError("System error initializing new CEMS report");
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_compliance"))
						.setRendered(false);
			}
			return ret;
		} finally {
			clearButtonClicked();
		}
	}
	
	public final String createCompReportOneTime() { // Since new, it will be in
		// staging
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			ComplianceReports compReport = null;
			String ret = "";
			try {
				// logger.debug("Creating CR OneTime task");
				if (!facilityExistsInternally()) {
					DisplayUtil.displayError("Facility "
							+ facility.getFacilityId() + "with facility ID "
							+ facilityId + " does not exist; " + contactMsg);
					return ret;
				}

				Task newTask = createNewTask(
						ComplianceReportTypeDef
								.getData()
								.getItems()
								.getItemDesc(
										ComplianceReportTypeDef.COMPLIANCE_TYPE_ONE),
						Task.TaskType.CR_ONE, false, null,
						facility.getFacilityId(), null, "current",
						this.corePlaceId, loginName);
				if (newTask != null) {
					((SimpleMenuItem) FacesUtil
							.getManagedBean("menuItem_compliance"))
							.setRendered(true);
				}

				compReport = (ComplianceReports) FacesUtil
						.getManagedBean("complianceReport");
				ComplianceReportSearch compReportSearch = (ComplianceReportSearch) FacesUtil
						.getManagedBean("complianceReportSearch");
				compReportSearch.setFacilityId(facility.getFacilityId());
				compReport.setFacility(facility);
				compReport.startNewReport(
						ComplianceReportTypeDef.COMPLIANCE_TYPE_ONE, newTask);
				ret = "dialog:newComplianceReport";
			} catch (Exception e) {
				handleException(new RemoteException(
						"Failed in Creating CR OneTime task", e));
				DisplayUtil
						.displayError("System error initializing new Other report");
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_compliance"))
						.setRendered(false);
			}
			return ret;
		} finally {
			clearButtonClicked();
		}
	}
*/
	public final String startCreateCompReport() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		DisplayUtil.clearQueuedMessages();
		clearButtonClicked();
		ComplianceReports complianceReports = (ComplianceReports) FacesUtil
				.getManagedBean("complianceReport");
		complianceReports.setComplianceReport(new ComplianceReport());
		String ret = "dialog:newComplianceReport";
		return ret;
	}
	
	public final String uploadMonitorReport() throws RemoteException {
		String ret = HOME;
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		
		DisplayUtil.clearQueuedMessages();
		
		for (Task task : tasks.values().toArray(new Task[0])) {
			if (task.getTaskType().equals(Task.TaskType.MRPT)) {
				DisplayUtil
						.displayError("Upload monitor report request failed. There is already a task in progress");
				clearButtonClicked();
				return ret;
			}
		}

		task = createNewTask("Upload Monitor Report", TaskType.MRPT, 
				false, null, facility.getFacilityId(),
				null, "current", this.corePlaceId, loginName);
		
		if (task != null) {
			((SimpleMenuItem) FacesUtil
					.getManagedBean("menuItem_monitorGroupDetail"))
					.setRendered(true);
		}

		try {

			Facility f = getFacilityService().retrieveFacility(
					facility.getFacilityId(), true);
			if (testMode)
				logger.debug("Attempt access to staging facility inventory "
						+ facility.getFacilityId());
			if (f == null) {
				if (testMode)
					logger.error("staging profile not found");
				// otherwise use the current one in the read-only database.
				f = getFacilityService().retrieveFacility(
						facility.getFacilityId());
			}
			facility = f;
			MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
			task.setTaskInternalId(getMonitorGroupId());
			task = getInfrastructureService().createTask(task);
			task.setFacility(facility); // so task can display facility name
			task.setTaskDescription("Upload Monitor Report");

			ret = HOME;
			ret = myTasks.renderUploadMonitorReportMenu(task, false);

			myTasks.setPageRedirect(ret);
			myTasks.setTaskId(task.getTaskId());

		} catch (RemoteException re) {
			handleException(re);
			task = null;
			throw re;
		} finally {

			refreshTasks(this.facilityId);

			goUploadMonitorReport();

			clearButtonClicked();
		}
		
		return ret;
	}
	
	public final String startNewStackTestReport() {
		String ret = null;
		if (!firstButtonClick()) { // protect from multiple clicks
			return ret;
		}
		try {
			StackTestSearch stSearch = (StackTestSearch) FacesUtil
					.getManagedBean("stackTestSearch");
			stSearch.setNewStackTestFacilityID(facility.getFacilityId());
			logger.debug("in startNewStackTestReport ... NewStackTestFacilityID = " + stSearch.getNewStackTestFacilityID());
			stSearch.setFacilityId(facility.getFacilityId());
			// search for previous applications so copy capability will be
			// enabled
			//stSearch.search();
			ret = "dialog:newStackTest";
		} catch (Exception e) {
			handleException(new RemoteException(e.getMessage(), e));
		} finally {
			clearButtonClicked();
		}
		return ret;
	}
	
	public final String createRPC() { // Since new, it will be in staging
		return startNewApplication(RPCRequest.class);
	}

	public final String createDOR() {
		return startNewApplication(DelegationRequest.class);
	}

	private final String startNewApplication(
			Class<? extends Application> newApplicationClass) {
		String ret = null;
		if (!firstButtonClick()) { // protect from multiple clicks
			return ret;
		}
		try {
			ApplicationSearch appSearch = (ApplicationSearch) FacesUtil
					.getManagedBean("applicationSearch");
			appSearch.setNewApplicationClass(newApplicationClass);
			appSearch.setNewApplicationFacilityID(facility.getFacilityId());
			appSearch.setFacilityID(facility.getFacilityId());
			// search for previous applications so copy capability will be
			// enabled
			appSearch.search();
			ret = "dialog:newApplication";
		} catch (Exception e) {
			handleException(new RemoteException(e.getMessage(), e));
		} finally {
			clearButtonClicked();
		}
		return ret;
	}

	public final String createApplication(Application app)
			throws RemoteException { // Since new, it will be in staging
		String ret = HOME;

		// create facility task
		Task facTask = changeFacilityProfileTask(3);
		if (facTask == null) {
			return ret;
		}

		String appType = ApplicationTypeDef.getData().getItems()
				.getItemDesc(app.getApplicationTypeCD());
		Task newTask = createNewTask(appType,
				getApplicationTaskType(app.getApplicationTypeCD()), true,
				facTask.getTaskId(), facTask.getFacilityId(),
				facTask.getFpId(), "current", this.corePlaceId, loginName);

		try {
			newTask.setApplication(app);
			newTask.setFacility(facility); // so facility name will appear with
											// task description
			newTask = getInfrastructureService().createTask(newTask, facTask);
			setTaskId(newTask.getTaskId());
			ret = renderPermitApplicationMenu(newTask, false);
		} catch (RemoteException re) {
			handleException(re);
			if (app.getApplicationID() != null) {
				logger.error("APPLICATION ID = " + app.getApplicationID());
			}
			if (app.getApplicationNumber() != null) {
				logger.error("APPLICATION NUMBER = "
						+ app.getApplicationNumber());
			}
			newTask = null;
			throw re;
		}

		refreshTasks(this.facilityId);
		return ret;
	}
	
	public final String createStackTest(StackTest st)
			throws RemoteException { // Since new, it will be in staging
		String ret = HOME;

		// create facility task
		Task facTask = changeFacilityProfileTask(3);
		if (facTask == null) {
			return ret;
		}
		logger.debug("before createNewTask");
		Task newTask = createNewTask("Stack Test",
				Task.TaskType.ST, true,
				facTask.getTaskId(), facTask.getFacilityId(),
				facTask.getFpId(), "current", this.corePlaceId, loginName);
		
		logger.debug("after createNewTask");
		try {
			newTask.setStackTest(st);
			newTask.setFacility(facility); // so facility name will appear with
											// task description
			newTask = getInfrastructureService().createTask(newTask, facTask);
			setTaskId(newTask.getTaskId());
			ret = renderStackTestMenu(newTask, false);
		} catch (RemoteException re) {
			handleException(re);
			if (st.getId() != null) {
				logger.error("STACK TEST ID = " + st.getId());
			}
			if (st.getStckId() != null) {
				logger.error("STACK TEST NUMBER = "
						+ st.getStckId());
			}
			newTask = null;
			throw re;
		}

		refreshTasks(this.facilityId);
		return ret;
	}

	public final String copyApplication(Application oldApp)
			throws RemoteException { // Since new, it will be in staging
		String ret = HOME;

		// create facility task if needed
		Task facTask = changeFacilityProfileTask(3);
		if (facTask == null) {
			return ret;
		}
		Task newTask = null;

		String appType = ApplicationTypeDef.getData().getItems()
				.getItemDesc(oldApp.getApplicationTypeCD());
		Task.TaskType taskType = Task.TaskType.COPY_PTPA;
		if (oldApp.getClass().equals(TVApplication.class)) {
			taskType = Task.TaskType.COPY_TVPA;
		} else if (!oldApp.getClass().equals(PTIOApplication.class)) {
			throw new RemoteException("Internal Error: Classes of type "
					+ oldApp.getClass().getName()
					+ " are not eligible for the copy operation.");
		}
		String taskLabel = null;
		if (oldApp.isApplicationCorrected()) {
			taskLabel = "Correction to " + appType + " Number "
					+ oldApp.getApplicationNumber();
		} else {
			taskLabel = appType;
		}
		newTask = createNewTask(taskLabel, taskType, true, facTask.getTaskId(),
				facTask.getFacilityId(), facTask.getFpId(), "current",
				this.corePlaceId, loginName);
		newTask.setApplication(oldApp);
		newTask.setFacility(facility); // so facility name will appear with task
										// description
		newTask = getInfrastructureService().createTask(newTask, facTask);

		setTaskId(newTask.getTaskId());
		ret = renderPermitApplicationMenu(newTask, false);

		refreshTasks(this.facilityId);
		return ret;
	}

	private Task.TaskType getApplicationTaskType(String applicationTypeCd) {
		Task.TaskType taskType = null;
		if (ApplicationTypeDef.PTIO_APPLICATION.equals(applicationTypeCd)) {
			taskType = Task.TaskType.PTPA;
		} else if (ApplicationTypeDef.TITLE_V_APPLICATION
				.equals(applicationTypeCd)) {
			taskType = Task.TaskType.TVPA;
		/*}
		 * Remove PBR references in permit applications else if
		 * (ApplicationTypeDef.PBR_NOTIFICATION .equals(applicationTypeCd)) {
		 * taskType = Task.TaskType.PBR; }
		 *///else if (ApplicationTypeDef.RPC_REQUEST.equals(applicationTypeCd)) {
			//taskType = Task.TaskType.RPC;
		//} else if (ApplicationTypeDef.TITLE_IV_APPLICATION
		//		.equals(applicationTypeCd)) {
		//	taskType = Task.TaskType.TIVPA;
		} else if (ApplicationTypeDef.DELEGATION_OF_RESPONSIBILITY
				.equals(applicationTypeCd)) {
			taskType = Task.TaskType.DOR;
		}
		if (taskType == null) {
			logger.error("No Application Task Type found for application type code: "
					+ applicationTypeCd);
		}
		return taskType;
	}

	private String renderPermitApplicationMenu(Task task, boolean editable) {
		return renderPermitApplicationMenu(task, editable, false);
	}
	
	private String renderPermitApplicationMenu(Task task, boolean editable, boolean fromValidationDlgAction) {
		ApplicationDetail ad = (ApplicationDetail) FacesUtil
				.getManagedBean("applicationDetail");
		// pass "this" to ApplicationDetail to avoid a circular reference
		ad.setMyTasks(this);
		ad.setTask(task);
		if (!fromValidationDlgAction) {
			ad.setApplicationID(task.getTaskInternalId());
			ad.setEditMode(editable);
		} else {
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_appDetail")).setDisabled(false);
		}
		
		resetTabs();

		return "applicationDetail";
	}

	private String renderUploadMonitorReportMenu(Task task, boolean editable) {
		return renderUploadMonitorReportMenu(task, editable, false);
	}
	
	private String renderUploadMonitorReportMenu(Task task, boolean editable, boolean fromValidationDlgAction) {
		MonitorGroupDetail mgrp = (MonitorGroupDetail) FacesUtil
				.getManagedBean("monitorGroupDetail");
		mgrp.refreshPortal();
//		 pass "this" to ApplicationDetail to avoid a circular reference
//		mgrp.setMyTasks(this);
		mgrp.setReportsTask(task);
		if (!fromValidationDlgAction) {
//			ad.setApplicationID(task.getTaskInternalId());
			setStagingMonitorReportUploadFlag(editable);
		} else {
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_uploadMonitorReport")).setDisabled(false);
		}
		
		resetTabs();

		return "monitoring.monitorGroupDetail";
	}

	public final String createEmissionReport() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		
		DisplayUtil.clearQueuedMessages();
		
		try {
			String s = null;
			ReportProfile reportProfile = (ReportProfile) FacesUtil
					.getManagedBean("reportProfile");
			reportProfile.setInStaging(true);
			s = reportProfile.createNewEmissionReport(facility.getFacilityId());
			return s;
		} finally {
			clearButtonClicked();
		}
	}

	public void renderEmissionReportMenu(Task task) {
		tasks.put(task.getTaskId(), task);
		setTaskId(task.getTaskId());
		((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_emissionsReport"))
				.setRendered(true);
		
		ReportDetail rd = (ReportDetail) FacesUtil
				.getManagedBean("reportDetail");
		rd.setTask(this.task);
		
		// TODO core place id
		// refreshTasks(facility.getCorePlaceId());
		refreshTasks(facility.getFacilityId());
	}
	
	public String renderComplianceReportMenu(Task task, boolean editable) {
		ComplianceReports cr = (ComplianceReports) FacesUtil
				.getManagedBean("complianceReport");
		// pass "this" to ApplicationDetail to avoid a circular reference
		//cr.setMyTasks(this);
		cr.setTask(task);
		//cr.setApplicationID(task.getTaskInternalId());
		cr.setEditMode(editable);
		
		resetTabs();

		return "complianceDetail";
	}
	
	private String renderStackTestMenu(Task task, boolean editable) {
		return renderStackTestMenu(task, editable, false);
	}
	
	private String renderStackTestMenu(Task task, boolean editable, boolean fromValidationDlgAction) {
		StackTestDetail st = (StackTestDetail) FacesUtil
				.getManagedBean("stackTestDetail");
		// pass "this" to StackTestDetail to avoid a circular reference
		st.setMyTasks(this);
		st.setTask(task);
		st.setStackTestID(task.getTaskInternalId());
		if (!fromValidationDlgAction) {
			st.setEditMode(editable);
		} else {
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_stackTestDetail")).setDisabled(false);
		}
		
		resetTabs();

		return "ceta.stackTestDetail";
	}

	public final static Facility getFacility(Integer fpId, Task task) {
		FacilityProfile facilityProfile = (FacilityProfile) FacesUtil
				.getManagedBean("facilityProfile");
		facilityProfile.setFacility(null);
		facilityProfile.setStaging(true);
		facilityProfile.setFpId(fpId);
		facilityProfile.setTask(task);
		facilityProfile.setSaveStaging(true);
		facilityProfile.setSelEpaEmuIds(null);
		facilityProfile.setExpandOpt(0);
		Facility facility = facilityProfile.getFacility();
		return facility;
	}

	public UIXTable getTasksTable() {
		return tasksTable;
	}

	public void setTasksTable(UIXTable tasksTable) {
		this.tasksTable = tasksTable;
	}

	public void createNewFacilityTask(Facility newFac) {
		facility = newFac;
		Task newTask = changeFacilityProfileTask(2);
		if (newTask != null) {
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_facility"))
					.setRendered(true);
			((SimpleMenuItem) FacesUtil
					.getManagedBean("menuItem_createFacility"))
					.setRendered(false);
			pageRedirect = null;
			FacilityProfile fp = (FacilityProfile) FacesUtil
					.getManagedBean("facilityProfile");
			fp.setTask(newTask);
			fp.setSaveStaging(true);
		}

		// TODO core place id
		// refreshTasks(this.corePlaceId);
		refreshTasks(this.facilityId);
	}

	public String getPageRedirect() {
		if (!"facilityProfile".equals(pageRedirect)) {
			init();
		}
		if (pageRedirect != null) {
			FacesUtil.setOutcome(null, pageRedirect);
			pageRedirect = null;
		}
		return null;
	}

	public boolean isHomePage() {
		return (pageRedirect == null);
	}

	public Facility getFacility() {
		return facility;
	}

	public String goHome() {
		pageRedirect = null;
		resetTabs();
		((SimpleMenuItem)FacesUtil.getManagedBean("menuItem_homeAmbientMonitoring")).setDisabled(null == getMonitorGroupId());
		
		// Close the validation dialog, if one is open. This will prevent a
		// system error that would occur if the user clicked on a Facility error
		// in the validation popup. User will need to re-validate to continue
		// working on validations. Navigation from the validation popup doesn't
		// work anyway after the user navigates to the Home tab. Making it work
		// would be expensive, and since the user hasn't complained about the
		// system error or unresponsive validation links yet, it is not likely
		// that this scenario is used much.
		Object close_validation_dialog = FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
		if (close_validation_dialog != null) {
			FacesUtil.startAndCloseModelessDialog(AppValidationMsg.VALIDATION_RESULTS_URL);
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
					.remove(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
		}
				
		return "home";
	}

	private void refreshMonitorGroupId() {
		MonitorGroup searchObj = new MonitorGroup();
		searchObj.setFacilityId(getFacilityId());
		try {
			MonitorGroup[] monitorGroups = 
					getMonitoringService().searchMonitorGroups(searchObj);
			for (MonitorGroup monitorGroup : monitorGroups) {
				setMonitorGroupId(monitorGroup.getGroupId());
			}
		} catch (DAOException e) {
			handleException(e);
			DisplayUtil.displayError("Cannot access monitor groups.");
		}
	}
	
	public Task createContactTask(int fromTask) {
		if (!facilityExistsInternally()) {
			DisplayUtil.displayError("Facility " + facility.getFacilityId()
					+ "with facility ID " + facilityId + " does not exist; "
					+ contactMsg);
			return null;
		}

		for (Task task : tasks.values().toArray(new Task[0])) {
			if (task.getTaskType().equals(Task.TaskType.FCC)) {
				task.setFacility(facility); // so facility name will appear with
											// task description
				return task;
			}
		}
		Task contactTask = createNewTask("Facility Contact Change",
				Task.TaskType.FCC, false, null, facility.getFacilityId(), null,
				"current", facility.getCorePlaceId(), loginName);
		contactTask.setReferenceCount(0);
		contactTask.setFacility(facility); // so facility name will appear with
											// description
		if (fromTask == 2) {
			return contactTask;
		}

		try {
			contactTask.setReferenceCount(0);
			contactTask = getInfrastructureService().createTask(contactTask);
		} catch (RemoteException re) {
			handleException(re);
			contactTask = null;
		}
		return contactTask;
	}

	public void setPageRedirect(String pageRedirect) {
		this.pageRedirect = pageRedirect;
	}

	public Task findFacilityTask(Integer fpId) {
		for (Task task : tasks.values().toArray(new Task[0])) {
			if (task.getTaskType().equals(Task.TaskType.FC)
					|| task.getTaskType().equals(Task.TaskType.FCH)) {
				if (task.getTaskInternalId().equals(fpId)) {
					return task;
				}
			}
		}
		return null;
	}

	public Task findCurrentFacilityTask() {
		for (Task task : tasks.values().toArray(new Task[0])) {
			if (task.getTaskType().equals(Task.TaskType.FC)) {
				return task;
			}
		}
		return null;
	}

	public Task findApplicationTask(Integer appId) {
		for (Task task : tasks.values().toArray(new Task[0])) {
			if ((task.getTaskType().equals(Task.TaskType.TVPA) || 
				 task.getTaskType().equals(Task.TaskType.COPY_TVPA) ||
				 task.getTaskType().equals(Task.TaskType.PTPA) || 
				 task.getTaskType().equals(Task.TaskType.COPY_PTPA) || 
				 task.getTaskType().equals(Task.TaskType.PBR) || 
				 task.getTaskType().equals(Task.TaskType.RPC) || 
				 task.getTaskType().equals(Task.TaskType.TIVPA) || 
				 task.getTaskType().equals(Task.TaskType.DOR))
					&& task.getTaskInternalId().equals(appId)) {				
				return task;
			}
		}
		return null;
	}
	
	public Task findReportTask(Integer rptId) {
		for (Task task : tasks.values().toArray(new Task[0])) {
			if ((task.getTaskType().equals(Task.TaskType.ER) || task
					.getTaskType().equals(Task.TaskType.R_ER))
					&& task.getTaskInternalId().equals(rptId)) {
				return task;
			}
		}
		return null;
	}

	public Task findStackTestTask(Integer stackTestId) {
		for (Task task : tasks.values().toArray(new Task[0])) {
			if (task.getTaskType().equals(Task.TaskType.ST) && task.getTaskInternalId().equals(stackTestId)) {				
				return task;
			}
		}
		return null;
	}
	
	public Task findComplianceReportTask(Integer rptId) {
		for (Task task : tasks.values().toArray(new Task[0])) {
			if (((task.getTaskType().equals(Task.TaskType.CR_OTHR))
					|| (task.getTaskType().equals(Task.TaskType.CR_CEMS))
					|| (task.getTaskType().equals(Task.TaskType.CR_ONE))
					|| (task.getTaskType().equals(Task.TaskType.CR_GENERIC)))	
					&& task.getTaskInternalId().equals(rptId)) {
				return task;
			}
		}
		return null;
	}
	
	public final String getLoginName() {
		return loginName;
	}

	public final boolean isHasSubmit() {
		if (currentRole != null) {
			hasSubmit = ExternalRole.CERTIFIER_ROLE.equals(currentRole.getExternalRole().getRoleName()); //TODO need to go all the way to 
																											// the external role or should there 
																											// be a contact role name?
		}
		return hasSubmit;
	}

	public final boolean isReconciled() {
		return isReconciled;
	}

	public final boolean isFrozenFacilityViewable() {
		return frozenFacilityViewable;
	}

	public void setFrozenFacilityViewable(boolean frozenFacilityViewable) {
		this.frozenFacilityViewable = frozenFacilityViewable;
	}

	public final String goFacilityContactChange() {
		String ret = HOME;
		for (Task task : tasks.values().toArray(new Task[0])) {
			if (task.getTaskType().equals(Task.TaskType.FCC)) {
				this.task = task;
				fromHomeContact = false;
				FacilityProfile fp = (FacilityProfile) FacesUtil
						.getManagedBean("facilityProfile");
				ret = fp.fromHomeContacts(true);
				fp.setContactTask(task);
				FacesUtil.renderSimpleMenuItem("menuItem_changeContact");
			}
		}
		return ret;
	}

	public final String changeOwnerContact() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			String ret = HOME;
			for (Task task : tasks.values().toArray(new Task[0])) {
				if (task.getTaskType().equals(Task.TaskType.FCC)) {
					DisplayUtil
							.displayError("Facility contact change  request failed. There is already a task in progress");
					return ret;
				}
			}

			Task contactTask = createContactTask(1);
			if (contactTask == null) {
				return ret;
			}

			fromHomeContact = false;
			FacilityProfile fp = (FacilityProfile) FacesUtil
					.getManagedBean("facilityProfile");
			ret = fp.fromHomeContacts(true);
			fp.setContactTask(contactTask);
			FacesUtil.renderSimpleMenuItem("menuItem_changeContact");

			// TODO core place id
			// refreshTasks(this.corePlaceId);
			refreshTasks(this.facilityId);
			return ret;
		} finally {
			clearButtonClicked();
		}
	}

	public final String fromHomeOwners() {
		fromHomeContact = true;
		FacilityProfile fp = (FacilityProfile) FacesUtil
				.getManagedBean("facilityProfile");
		fp.getFacilityOwners();

		return "homeOwners";
	}

	public final String fromHomeCurrentOwner() {
		fromHomeContact = true;
		FacilityProfile fp = (FacilityProfile) FacesUtil
				.getManagedBean("facilityProfile");
		String cmpId = fp.getFacility().getOwner().getCompany().getCmpId();

		CompanyProfile cp = (CompanyProfile) FacesUtil
				.getManagedBean("companyProfile");
		cp.setCmpId(cmpId);

		return "companyProfile";
	}

	public final String fromHomeContacts() {
		fromHomeContact = true;
		FacilityProfile fp = (FacilityProfile) FacesUtil
				.getManagedBean("facilityProfile");
		fp.fromHomeContacts(false);

		return "homeContacts";
	}

	public final String fromHomeFacContacts() {
		fromHomeContact = true;
		FacilityProfile fp = (FacilityProfile) FacesUtil
				.getManagedBean("facilityProfile");
		fp.fromHomeContacts(false);

		return "homeFacContacts";
	}

	public final String setStagingFacilityEditFlag() {
		this.setStagingEditFlag(true);

		return "facilityProfile";
	}

	public final String fromHomeFacility() {
		this.setStagingEditFlag(false);
		FacilityProfile fp = (FacilityProfile) FacesUtil
				.getManagedBean("facilityProfile");
		fp.setFpId(facility.getFpId());
		fp.setStaging(false);
		fp.submitProfile();

		return "homeFacilityProfile";
	}

	public boolean isFromHomeContact() {
		return fromHomeContact;
	}

	public Integer getCorePlaceId() {
		return corePlaceId;
	}

	public boolean isHasIssuedTVPTO() {
		try {
			PermitService permitBO = ServiceFactory.getInstance()
					.getPermitService();
			List<Permit> permits = permitBO.search(null, null, null,
					PermitTypeDef.TV_PTO, null, null, null, null, getFacility()
							.getFacilityId(), null,
					PermitIssuanceTypeDef.Final, null, null, null, null, false, null);

			if (permits.size() > 0) {
				return true;
			}

			return false;
		} catch (ServiceFactoryException sfe) {
			logger.error(
					"Unable to identify whether facility has issued TV PTIO Permits",
					sfe);
			return false;
		} catch (DAOException dao) {
			logger.error(
					"Unable to identify whether facility has issued TV PTIO Permits",
					dao);
			return false;
		} catch (RemoteException re) {
			logger.error(
					"Unable to identify whether facility has issued TV PTIO Permits",
					re);
			return false;
		}
	}

	public void setFromHomeContact(boolean fromHomeContact) {
		this.fromHomeContact = fromHomeContact;
	}

	public final String createIntToRel() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}

		try {
			ApplicationSearch appSearch = (ApplicationSearch) FacesUtil
					.getManagedBean("applicationSearch");
			appSearch.setNewApplication(null);
			Relocation relocation = (Relocation) FacesUtil
					.getManagedBean("relocation");
			relocation.ResetNewPortalRelReq();
			relocation.setFacility(facility);
			return "dialog:createIntToRel";
		} finally {
			clearButtonClicked();
		}
	}

	public final RelocateRequest createNewITR(RelocateRequest relocReq)
			throws RemoteException { // Since new, it will be in staging
		RelocateRequest ret = null;
		if (!facilityExistsInternally()) {
			DisplayUtil.displayError("Facility with core ID " + corePlaceId
					+ " does not exist in IMPACT; " + contactMsg);
			return ret;
		}
		String relReqType = ApplicationTypeDef.getData().getItems()
				.getItemDesc(relocReq.getApplicationTypeCD());
		Task newTask = createNewTask(relReqType, Task.TaskType.REL, false,
				null, facility.getFacilityId(), null, "current",
				facility.getCorePlaceId(), loginName);

		try {
			newTask.setRelocateRequest(relocReq);
			newTask.setFacility(facility); // so facility name will appear with
											// task description
			newTask = getInfrastructureService().createTask(newTask);
			ret = newTask.getRelocateRequest();
		} catch (RemoteException re) {
			handleException(re);
			newTask = null;
			throw re;
		}

		// TODO core place id
		// refreshTasks(this.corePlaceId);
		refreshTasks(this.facilityId);
		pageRedirect = "relocateDetail";
		((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_applications"))
				.setRendered(true);
		Relocation relocation = (Relocation) FacesUtil
				.getManagedBean("relocation");
		relocation.setTask(newTask);
		relocation.setFacility(facility);
		return ret;
	}

	public final void confirmReturned(ReturnEvent event) {
	}

	public final String deletePopup() {
		String B1 = "Click yes to delete the following selected task:<BR><UL>";
		String E1 = "</UL>";
		String B2 = "Click yes to delete the following selected task and its dependent task:<BR><UL>";
		String E2 = "</UL><BR>"
				+ "If you have made changes in the dependent task and do not want to lose those changes, click no to return to the IMPACT home page.";
		String B3 = "Click yes to delete the following selected task and its dependent tasks:<BR><UL>";
		String E3 = "</UL><BR>"
				+ "If you have made changes in one of the dependent tasks and do not want to lose those changes, click no to return to the IMPACT home page.";

		selDelTask = null;
		if (task == null) {
			DisplayUtil.displayInfo("Please select a task to delete");
			return HOME;
		}

		Task contactTask = null;
		Task facTask = null;
		try {
			if (task.getDependent()) {
				switch (task.getTaskType()) {
				case FC:
				case FCH:
					task = getInfrastructureService().retrieveTask(
							task.getTaskId());
					if (task.getReferenceCount().intValue() >= 1) {
						DisplayUtil
								.displayError("Delete task failed; Other tasks depend on this task.");
						return HOME;
					}

					contactTask = getInfrastructureService().retrieveTask(
							task.getDependentTaskId());
					if (contactTask.getReferenceCount().intValue() > 1) {
						deleteTaskMsg = B1;
						deleteTaskMsg += "<LI>"
								+ task.getTaskDescription() + "</LI>";
						deleteTaskMsg += E1;
					} else {
						deleteTaskMsg = B2;
						deleteTaskMsg += "<LI>" + task.getTaskDescription()
								+ "</LI>" + "<LI>"
								+ contactTask.getTaskDescription() + "</LI>";
						deleteTaskMsg += E2;
					}
					break;
				default:
					task = getInfrastructureService().retrieveTask(
							task.getTaskId());
					facTask = getInfrastructureService().retrieveTask(
							task.getDependentTaskId());
					if (facTask.getReferenceCount().intValue() > 1) {
						deleteTaskMsg = B1;
						deleteTaskMsg += "<LI>" + task.getTaskDescription()
								+ "</LI>";
						deleteTaskMsg += E1;
					} else {
						contactTask = getInfrastructureService().retrieveTask(
								facTask.getDependentTaskId());
						if (contactTask.getReferenceCount().intValue() > 1) {
							deleteTaskMsg = B2;
							deleteTaskMsg += "<LI>" + task.getTaskDescription()
									+ "</LI>" + "<LI>"
									+ facTask.getTaskDescription() + "</LI>";
							deleteTaskMsg += E2;

						} else {
							deleteTaskMsg = B3;
							deleteTaskMsg += "<LI>" + task.getTaskDescription()
									+ "</LI>" + "<LI>"
									+ facTask.getTaskDescription() + "</LI>"
									+ "<LI>" + contactTask.getTaskDescription()
									+ "</LI>";
							deleteTaskMsg += E3;
						}
					}
				}
			} else {
				switch (task.getTaskType()) {
				case FCC:
					task = getInfrastructureService().retrieveTask(
							task.getTaskId());
					if (task.getReferenceCount().intValue() >= 1) {
						DisplayUtil
								.displayError("Delete task failed; Other tasks depend on this task.");
						return HOME;
					}

					// let it follows throug default section

				default:
					deleteTaskMsg = B1;
					deleteTaskMsg += "<LI>" + task.getTaskDescription()
							+ "</LI>";
					deleteTaskMsg += E1;
					break;
				}
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Delete task failed.");
			deleteTaskMsg = null;
			return HOME;
		}

		deleteTaskMsg += "<BR>";
		selDelTask = task;
		return "dialog:deleteInProgTask";
	}

	public final void canceldelInProgTask(ActionEvent actionEvent) {
		FacesUtil.returnFromDialogAndRefresh();
	}

	public String getDeleteTaskMsg() {
		return deleteTaskMsg;
	}

	public void setDeleteTaskMsg(String deleteTaskMsg) {
		this.deleteTaskMsg = deleteTaskMsg;
	}

	public String getInfo() {
		String dsId;
		if (datasetId != null)
			dsId = "datasetId=" + datasetId;
		else
			dsId = "datasetId not set";
		String cpIdStr;
		if (corePlaceIdStr != null)
			cpIdStr = "corePlaceIdStr=" + corePlaceIdStr;
		else
			cpIdStr = "corePlaceIdStr not set";
		String cpId;
		if (corePlaceId != null)
			cpId = "corePlaceId=" + corePlaceId.toString();
		else
			cpId = "corePlaceId not set";
		String lName;
		if (loginName != null)
			lName = "loginName=" + loginName;
		else
			lName = "loginName not set";
		String taskInfo;
		if (task != null)
			taskInfo = "task info (taskId=" + task.getTaskId()
					+ ", facilityId=" + task.getFacilityId() + ", corePlaceId="
					+ task.getCorePlaceId() + ")";
		else
			taskInfo = "task not set";
		String facilityInfo;
		if (facility != null)
			facilityInfo = "facility info (facilityId="
					+ facility.getFacilityId() + ", fpId=" + facility.getFpId()
					+ ", corePlaceId=" + facility.getCorePlaceId() + ")";
		else
			facilityInfo = "facility not set";
		return dsId + "; " + cpIdStr + "; " + cpId + "; " + lName + "; "
				+ taskInfo + "; " + facilityInfo;
	}

	public Object getExternalCompanyId() {
		return null == this.currentRole? null : 
			this.currentRole.getExternalRole().getOrganization().getOrganizationId();
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getFixedTabs() {
		if (FacesUtil.getCurrentPage().equals("companySelector")) {
			hideTabs();
			FacesUtil.renderSimpleMenuItem("menuItem_cmpSelector");
			FacesUtil.hideSimpleMenuItem("menuItem_facSelector");
		}

		if (FacesUtil.getCurrentPage().equals("facilitySelector")) {
			hideTabs();
			FacesUtil.renderSimpleMenuItem("menuItem_cmpSelector");
			FacesUtil.renderSimpleMenuItem("menuItem_facSelector");
		}

		return SUCCESS;
	}

	private void getTaskFromTasksTable() {
		task = null;
		if (tasksTable != null && tasksTable.getSelectionState() != null) {
			Iterator<?> it = tasksTable.getSelectionState().getKeySet()
					.iterator();
			if (it.hasNext()) {
				Object obj = it.next();
				tasksTable.setRowKey(obj);
				task = (Task) tasksTable.getRowData();
				tasksTable.getSelectionState().clear();
			}
		}
	}

	public boolean isImpactFullEnabled() {
		boolean impactFull = false;
		String enableImpactFullName = Config.findNode("app.enableImpactFull")
				.getAsString("jndiName");
		if (!Utility.isNullOrEmpty(enableImpactFullName)) {
			Object enableImpactObj = Config.getEnvEntry(enableImpactFullName);
			if (enableImpactObj != null) {
				String enableImpactFull = (String) Config
						.getEnvEntry(enableImpactFullName);

				if (!Utility.isNullOrEmpty(enableImpactFull)
						&& enableImpactFull.equals("true")) {
					impactFull = true;
				}
			}
		}
		setImpactFullEnabled(impactFull);
		return impactFullEnabled;
	}

	public void setImpactFullEnabled(boolean impactFullEnabled) {
		this.impactFullEnabled = impactFullEnabled;
	}

}
