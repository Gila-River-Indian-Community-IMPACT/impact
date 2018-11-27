package us.wy.state.deq.impact.webcommon.projectTracking;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import oracle.adf.view.faces.event.ReturnEvent;

import org.apache.commons.lang.StringUtils;

import us.oh.state.epa.stars2.app.tools.SpatialData;
import us.oh.state.epa.stars2.app.tools.SpatialData.SpatialDataLineItem;
import us.oh.state.epa.stars2.bo.ProjectTrackingService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.def.AgencyDef;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.ProjectAttachmentTypeDef;
import us.oh.state.epa.stars2.def.ProjectTrackingEventDependenciesDef;
import us.oh.state.epa.stars2.def.ProjectTrackingEventTypeDef;
import us.oh.state.epa.stars2.def.ProjectTypeDef;
import us.oh.state.epa.stars2.def.ShapeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.userAuth.UseCase;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.bean.ConfirmWindow;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.bean.Stars2Object;
import us.oh.state.epa.stars2.webcommon.document.AttachmentEvent;
import us.oh.state.epa.stars2.webcommon.document.AttachmentException;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentListener;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.Budget;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.Contract;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.GrantProject;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.NEPAProject;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.Project;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectAttachment;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectNote;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectTrackingEvent;

@SuppressWarnings("serial")
public class ProjectTrackingDetail extends AppBase implements
		IAttachmentListener {

	public static final String PAGE_VIEW_ID = "projectTrackingDetail:";
	public static final String PROJECT_TRACKING_DETAIL_OUTCOME = "projectTrackingDetail";
	public static final String SPATIAL_DATA_OUTCOME = "tools.spatialData";
	public static final String PROJECT_TRACKING_EVENT_DETAIL_OUTCOME = "dialog:projectTrackingEventDetail";
	public static final String PROJECT_NOTE_DETAIL_OUTCOME = "dialog:projectNoteDetail";
	public static final String PROJECT_ATTACHMENT_DETAIL_OUTCOME = "dialog:projectAttachmentDetail";
	public static final String BUDGET_DETAIL_OUTCOME = "dialog:budgetDetail";
	

	private Integer projectId;
	private Project project;
	private String projectTypeCd;
	private ProjectTrackingService projectTrackingService;

	private String popupRedirectOutcome;

	private boolean editMode;

	private TableSorter projectLeadsWrapper = new TableSorter();
	private TableSorter grantAccountantsWrapper = new TableSorter();
	private TableSorter contractAccountantsWrapper = new TableSorter();

	private boolean trackingEventEditMode;
	private ProjectTrackingEvent trackingEvent;
	private Integer trackingEventId;
	
	private ProjectNote projectNote;
	private boolean noteEditMode;

	private ProjectAttachment projectAttachment;
	
	private Budget budget;
	private boolean budgetEditMode;
	
	protected List<Document> documents; // used for print/download


	
	public String printProjectTrackingDetail() {
		documents = prepareDocuments();
		return "dialog:prtProjectTrackingDetail";
	}

	private List<Document> prepareDocuments() {
		String user = InfrastructureDefs.getCurrentUserAttrs().getUserName();
		documents = new ArrayList<Document>();

		try {
			documents = getProjectTrackingService().getPrintableDocumentList(
					project, user);
		} catch (RemoteException re) {
			DisplayUtil
					.displayError("Unable to generate emissions inventory report documents");
			handleException("Stack Test " + project.getProjectId(), re);
		}
		return documents;
	}


	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	public String getProjectTypeCd() {
		return projectTypeCd;
	}

	public void setProjectTypeCd(String projectTypeCd) {
		this.projectTypeCd = projectTypeCd;
	}

	public ProjectTrackingService getProjectTrackingService() {
		return projectTrackingService;
	}

	public void setProjectTrackingService(
			ProjectTrackingService projectTrackingService) {
		this.projectTrackingService = projectTrackingService;
	}
	
	public TableSorter getProjectLeadsWrapper() {
		return projectLeadsWrapper;
	}

	public void setProjectLeadsWrapper(TableSorter projectLeadsWrapper) {
		this.projectLeadsWrapper = projectLeadsWrapper;
	}

	public TableSorter getGrantAccountantsWrapper() {
		return grantAccountantsWrapper;
	}

	public void setGrantAccountantsWrapper(
			TableSorter grantAccountantsWrapper) {
		this.grantAccountantsWrapper = grantAccountantsWrapper;
	}

	public boolean isTrackingEventEditMode() {
		return trackingEventEditMode;
	}

	public void setTrackingEventEditMode(boolean trackingEventEditMode) {
		this.trackingEventEditMode = trackingEventEditMode;
	}

	public ProjectTrackingEvent getTrackingEvent() {
		return trackingEvent;
	}

	public void setTrackingEvent(ProjectTrackingEvent trackingEvent) {
		this.trackingEvent = trackingEvent;
	}
	
	public ProjectNote getProjectNote() {
		return projectNote;
	}

	public void setProjectNote(ProjectNote projectNote) {
		this.projectNote = projectNote;
	}
	
	public boolean isNoteEditMode() {
		return noteEditMode;
	}

	public void setNoteEditMode(boolean noteEditMode) {
		this.noteEditMode = noteEditMode;
	}
	
	public ProjectAttachment getProjectAttachment() {
		return projectAttachment;
	}

	public void setProjectAttachment(ProjectAttachment projectAttachment) {
		this.projectAttachment = projectAttachment;
	}
	
	public Integer getTrackingEventId() {
		return trackingEventId;
	}

	public void setTrackingEventId(Integer trackingEventId) {
		this.trackingEventId = trackingEventId;
	}
	
	public TableSorter getContractAccountantsWrapper() {
		return contractAccountantsWrapper;
	}

	public void setContractAccountantsWrapper(
			TableSorter contractAccountantsWrapper) {
		this.contractAccountantsWrapper = contractAccountantsWrapper;
	}
	
	public Budget getBudget() {
		return budget;
	}

	public void setBudget(Budget budget) {
		this.budget = budget;
	}
	
	public boolean isBudgetEditMode() {
		return budgetEditMode;
	}

	public void setBudgetEditMode(boolean budgetEditMode) {
		this.budgetEditMode = budgetEditMode;
	}

	public String refresh() {
		String ret = null;
		
		if (!Utility.isNullOrEmpty(this.projectTypeCd)
				&& (null != ProjectTypeDef.getData().getItem(this.projectTypeCd))) {
			
			// check if the user has privileges to access the given project type
			if (!checkProjectAccessPrivileges()) {
				DisplayUtil
						.displayError("You do not have sufficient privileges to access "
								+ ProjectTypeDef
										.getProjectTypeDescription(this.projectTypeCd)
								+ " type project. Please contact system administrator.");
			} else if (SUCCESS == submit()) {
				ret = PROJECT_TRACKING_DETAIL_OUTCOME;
			}
		} else {
			DisplayUtil.displayError("Unknown or invalid project type");
		}

		return ret;
	}

	private String submit() {
		String ret = FAIL;
		try {
			if (null != projectId) {
				logger.debug("Retrieving project with id: " + projectId);
				project = getProjectTrackingService()
						.retrieveProject(projectId);
				if (null != project) {
					setupTableWrappers();

					SimpleMenuItem menuItem_projectTrackingDetail = (SimpleMenuItem) FacesUtil
							.getManagedBean("menuItem_projectTrackingDetail");
					if (null != menuItem_projectTrackingDetail) {
						menuItem_projectTrackingDetail.setDisabled(false);
						ret = SUCCESS;
					}
					initializeAttachmentBean();
				}
			}
		} catch (RemoteException re) {
			String msg = "An error occured when trying to retrieve the project with id "
					+ projectId;
			logger.error(msg, re);
			DisplayUtil.displayError(msg);
			handleException(re);
		}

		return ret;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public void enterEditMode() {
		this.editMode = true;
	}

	public void leaveEditMode() {
		this.editMode = false;
	}

	public String cancel() {
		leaveEditMode();
		if (this.projectLeadsWrapper.getTable().getSelectionState() != null){
			this.projectLeadsWrapper.getTable().getSelectionState().clear();
		}
		if (this.grantAccountantsWrapper.getTable().getSelectionState() != null){
			this.grantAccountantsWrapper.getTable().getSelectionState().clear();
		}
		if (this.contractAccountantsWrapper.getTable().getSelectionState() != null){
			this.contractAccountantsWrapper.getTable().getSelectionState().clear();
		}
		return submit();
	}

	public void save() {
		ValidationMessage[] valMsgs = project.validate();
		if (project.isValid()) {
			try {
				getProjectTrackingService().updateProject(project);

				// get the updated project
				submit();
				logger.debug("Project information updated successfully for project "
						+ project.getProjectNumber());
				DisplayUtil
						.displayInfo("Project information updated successfully");
			} catch (RemoteException re) {
				String msg = "Failed to update project information for project "
						+ project.getProjectNumber();
				logger.error(msg, re);
				DisplayUtil.displayError(msg);
				handleException(re);
			} finally {
				leaveEditMode();
				if (this.projectLeadsWrapper.getTable().getSelectionState() != null){
					this.projectLeadsWrapper.getTable().getSelectionState().clear();
				}
				if (this.grantAccountantsWrapper.getTable().getSelectionState() != null){
					this.grantAccountantsWrapper.getTable().getSelectionState().clear();
				}
				if (this.contractAccountantsWrapper.getTable().getSelectionState() != null){
					this.contractAccountantsWrapper.getTable().getSelectionState().clear();
				}
			}
		} else {
			displayValidationMessages(PAGE_VIEW_ID, valMsgs);
		}
	}

	public String delete() {
		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
			return null;
		}

		String ret = null;
		try {
			getProjectTrackingService().deleteProject(project);

			// disable project tracking detail menu
			SimpleMenuItem menuItem_projectTrackingDetail = (SimpleMenuItem) FacesUtil
					.getManagedBean("menuItem_projectTrackingDetail");
			if (null != menuItem_projectTrackingDetail) {
				menuItem_projectTrackingDetail.setDisabled(true);
			}

			String msg ="Project " + project.getProjectNumber()	+ " deleted successfully";
			logger.debug(msg);
			DisplayUtil.displayInfo(msg);
			
			ProjectTrackingSearch searchBean = (ProjectTrackingSearch) FacesUtil
					.getManagedBean("projectTrackingSearch");
			searchBean.submitSearch();
			
			ret = "projectTrackingSearch";
			setPopupRedirectOutcome(ret);
		} catch (RemoteException re) {
			String msg = "Failed to delete project " + project.getProjectNumber();
			logger.error(msg, re);
			DisplayUtil.displayError(msg);
			handleException(re);
		}

		return ret;
	}

	public final String getPopupRedirect() {
		if (popupRedirectOutcome != null) {
			FacesUtil.setOutcome(null, popupRedirectOutcome);
			popupRedirectOutcome = null;
		}
		return null;
	}

	public void setPopupRedirectOutcome(String popupRedirectOutcome) {
		this.popupRedirectOutcome = popupRedirectOutcome;
	}

	public final String getProjectType() {
		String ret = "basic";
		if (null != project) {
			if (project.getProjectTypeCd().equals(ProjectTypeDef.NEPA)) {
				ret = "nepa";
			} else if (project.getProjectTypeCd().equals(ProjectTypeDef.GRANTS)) {
				ret = "grants";
			} else if (project.getProjectTypeCd()
					.equals(ProjectTypeDef.LETTERS)) {
				ret = "letters";
			} else if (project.getProjectTypeCd()
					.equals(ProjectTypeDef.CONTRACTS)) {
				ret = "contract";
			}
		}

		return ret;
	}

	public final boolean getDisplayBLMFieldOfficeList() {
		return isNEPAProject()
				&& ((NEPAProject) project).getLeadAgencyCds().contains(
						AgencyDef.BLM);
	}

	public final boolean getDisplayNationalParkList() {
		return isNEPAProject()
				&& ((NEPAProject) project).getLeadAgencyCds().contains(
						AgencyDef.NATIONAL_PARK_SERVICE);
	}

	public final boolean getDisplayNationalForestList() {
		return isNEPAProject()
				&& ((NEPAProject) project).getLeadAgencyCds().contains(
						AgencyDef.US_FOREST_SERVICE);
	}

	public final boolean isNEPAProject() {
		return project.getProjectTypeCd().equals(ProjectTypeDef.NEPA);
	}

	public final boolean isGrantsProject() {
		return project.getProjectTypeCd().equals(ProjectTypeDef.GRANTS);
	}

	public final boolean isLettersProject() {
		return project.getProjectTypeCd().equals(ProjectTypeDef.LETTERS);
	}
	
	public final boolean isContract() {
		return project.getProjectTypeCd().equals(ProjectTypeDef.CONTRACTS);
	}

	private void setupTableWrappers() {
		// set up the wrappers
		projectLeadsWrapper
				.setWrappedData(this.project.getProjectLeadUserIds());

		if (isGrantsProject()) {
			grantAccountantsWrapper
					.setWrappedData(((GrantProject) this.project)
							.getAccountantUserIds());
		}
		
		if (isContract()) {
				contractAccountantsWrapper
					.setWrappedData(((Contract) this.project)
							.getAccountantUserIds());
		}
	}

	public void deleteProjectLeads() {
		List<Stars2Object> ret = Stars2Object.deleteStars2Objects(
				getProjectLeadsWrapper().getTable(),
				this.project.getProjectLeadUserIds());

		this.project.setProjectLeadUserIds(ret);
	}

	public DefSelectItems getProjectLeadUserDefs() {
		return getFiltertedUserDefs(Stars2Object
				.fromStar2IntObject(this.project.getProjectLeadUserIds()));
	}

	public void deleteGrantAccountants() {
		List<Stars2Object> ret = Stars2Object.deleteStars2Objects(
				getGrantAccountantsWrapper().getTable(),
				((GrantProject) this.project).getAccountantUserIds());

		((GrantProject) this.project).setAccountantUserIds(ret);
	}

	public DefSelectItems getGrantAccountantUserDefs() {
		return getFiltertedUserDefs(Stars2Object
				.fromStar2IntObject(((GrantProject) this.project)
						.getAccountantUserIds()));
	}

	private DefSelectItems getFiltertedUserDefs(List<Integer> userIdsToFilter) {
		DefSelectItems filteredUserDefs = new DefSelectItems();
		for (SelectItem si : BasicUsersDef.getAllActiveUserData().getItems()
				.getCurrentItems()) {
			if (!userIdsToFilter.contains((Integer) si.getValue())) {
				filteredUserDefs.add(si.getValue(), si.getLabel(),
						si.isDisabled());
			}
		}

		return filteredUserDefs;
	}

	public final String getShapeLabel() {
		return ShapeDef.getData().getItems()
				.getItemDesc(((NEPAProject) this.project).getStrShapeId());
	}

	public String displayProjectAreaOnMap() {
		SpatialData spatialDataBean = (SpatialData) FacesUtil
				.getManagedBean("spatialData");

		if (null != spatialDataBean && isNEPAProject()) {
			spatialDataBean.refresh();
			Integer shapeId = ((NEPAProject) this.project).getShapeId();
			for (SpatialDataLineItem item : spatialDataBean.getImportedShapes()) {
				item.setSelected(item.getShapeId().equals(shapeId));
			}
		}

		return SPATIAL_DATA_OUTCOME;
	}

	public void enterTrackingEventEditMode() {
		setTrackingEventEditMode(true);
	}
	
	public void leaveTrackingEventEditMode() {
		setTrackingEventEditMode(false);
	}
	
	public String startToViewTrackingEvent() {
		return PROJECT_TRACKING_EVENT_DETAIL_OUTCOME;
	}
	
	public String startToAddTrackingEvent() {
		this.trackingEvent = new ProjectTrackingEvent();
		this.trackingEvent.setProjectId(this.projectId);
		this.trackingEvent.setAddedByUserId(InfrastructureDefs
				.getCurrentUserId());
		enterTrackingEventEditMode();
		return PROJECT_TRACKING_EVENT_DETAIL_OUTCOME;
	}
	
	public void cancelTrackingEventEdit() {
		try {
			this.trackingEvent = getProjectTrackingService()
					.retrieveProjectTrackingEvent(
							this.trackingEvent.getEventId());
		} catch (DAOException de) {
			String msg = "An error occured when trying to retrieve the tracking event";
			logger.error(msg, de);
			DisplayUtil.displayError(msg);
			handleException(de);
		} finally {
			leaveTrackingEventEditMode();
		}
	}
	
	public void saveProjectTrackingEvent() {
		ValidationMessage[] valMsgs = this.trackingEvent.validate();
		if (null != this.trackingEvent) {
			if(this.trackingEvent.isValid()) {
				// check for duplicate event
				if (isDuplicateTrackingEvent()) {
					DisplayUtil
							.displayError(
									"ERROR: Duplicate tracking event type. Only one instance of this tracking event type can be added.",
									ProjectTrackingEvent.PAGE_VIEW_ID);
					return;
				}
				
				// check for missing predecessor event if any
				List<String> missingPredecessorEventsList = getMissingPredecessorEventsList();
				if (!missingPredecessorEventsList.isEmpty()) {
					List<String>temp = new ArrayList<String>();
					for(String s: missingPredecessorEventsList) {
						temp.add(ProjectTrackingEventTypeDef
								.getData().getItems().getDescFromAllItem(s));
					}
					String events = StringUtils
							.join(temp.toArray(new String[0]), ",");
					DisplayUtil
							.displayError("ERROR: Following predecessor event(s) - "
									+ events + " must be added before adding this event");
					return;
				}
				
				try {
					if(this.trackingEvent.isNewObject()) {
						getProjectTrackingService().createProjectTrackingEvent(
								this.trackingEvent);
						DisplayUtil.displayInfo("Project tracking event created successfully");
					} else {
						getProjectTrackingService().updateProjectTrackingEvent(
								this.trackingEvent);
						DisplayUtil.displayInfo("Project tracking event updated successfully");
					}
					closeTrackingEventDialog();
				} catch (RemoteException de) {
					String msg = "Failed to create or update the project tracking event";
					logger.error(msg, de);
					DisplayUtil.displayError(msg);
					handleException(de);
				}
			} else {
				displayValidationMessages("", valMsgs);
			}
		}
	}
	
	public void deleteProjectTrackingEvent(ReturnEvent re) {
		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
			return;
		}
		
		// first check if there are any dependent events
		List<String> dependentEventsList = getDependentEventsList();
		if(!dependentEventsList.isEmpty()) {
			List<String>temp = new ArrayList<String>();
			for(String s: dependentEventsList) {
				temp.add(ProjectTrackingEventTypeDef
						.getData().getItems().getDescFromAllItem(s));
			}
			String events = StringUtils
					.join(temp.toArray(new String[0]), ",");
			DisplayUtil
					.displayError("ERROR: Following dependent event(s) - "
							+ events + " must be deleted before deleting this event");
			return;
		}

		try {
			getProjectTrackingService().deleteProjectTrackingEvent(
					this.trackingEvent.getEventId());
			DisplayUtil
					.displayInfo("Successfully deleted project tracking event");
			closeTrackingEventDialog();
		} catch (DAOException de) {
			String msg = "Failed to delete project tracking event";
			logger.error(msg, de);
			DisplayUtil.displayError(msg);
			handleException(de);
		}
	}
	
	private void refreshProjectTrackingEvents() {
		try {
			List<ProjectTrackingEvent> events = getProjectTrackingService()
					.retrieveProjectTrackingEventsByProjectId(projectId);
			project.setTrackingEvents(events);
		} catch (DAOException de) {
			String msg = "Failed to retrieve tracking events";
			logger.error(msg, de);
			DisplayUtil.displayError(msg);
			handleException(de);
		}
	}
	
	public void closeTrackingEventDialog() {
		setTrackingEventEditMode(false);
		setTrackingEvent(null);
		setTrackingEventId(null);
		refreshProjectTrackingEvents();
		closeDialog();
	}

	public void closeDialog() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	
	public final DefSelectItems getProjectTrackingEventTypeDefs() {
		return ProjectTrackingEventTypeDef
				.getProjectTypeTrackingEventDefs(this.project
						.getProjectTypeCd());
	}
	
	private boolean isDuplicateTrackingEvent() {
		boolean isDuplicate = false;
		boolean isAllowMultiple = ProjectTrackingEventTypeDef
				.getAllowMultiple(this.trackingEvent.getEventTypeCd());

		if (!isAllowMultiple) {
			for (ProjectTrackingEvent event : this.project.getTrackingEvents()) {
				if (!event.getEventId().equals(this.trackingEvent.getEventId())
						&& event.getEventTypeCd().equals(
								this.trackingEvent.getEventTypeCd())) {
					isDuplicate = true;
					break;
				}
			}
		}

		return isDuplicate;
	}
	
	private List<String> getMissingPredecessorEventsList() {
		List<String> missingPredecessorEventsList = new ArrayList<String>();

		List<String> predecessorEventList = ProjectTrackingEventDependenciesDef
				.getPredecessorEvents(this.trackingEvent.getEventTypeCd());

		if(null != predecessorEventList) {
			for (String predecessorEventCd : predecessorEventList) {
				boolean found = false;
				for (ProjectTrackingEvent event : this.project.getTrackingEvents()) {
					if (predecessorEventCd.equals(event.getEventTypeCd())) {
						found = true;
						break;
					}
				}
				if (!found) {
					missingPredecessorEventsList.add(predecessorEventCd);
				}
			}
		}

		return missingPredecessorEventsList;
	}
	
	private List<String> getDependentEventsList() {
		List<String> dependentEventsList = ProjectTrackingEventDependenciesDef
				.getDependentEvents(this.trackingEvent.getEventTypeCd());
		
		List<String> ret = new ArrayList<String>();
		
		for(ProjectTrackingEvent event: this.project.getTrackingEvents()) {
			if(dependentEventsList.contains(event.getEventTypeCd())) {
				ret.add(event.getEventTypeCd());
			}
		}
		
		return ret;
	}
	
	public boolean getDisplayProjectTrackingEvents() {
		return !getProjectTrackingEventTypeDefs().getAllItems().isEmpty();
	}
	
	public void enterNoteEditMode() {
		setNoteEditMode(true);
	}
	
	public void leaveNoteEditMode() {
		setNoteEditMode(false);
	}
	
	public String startToAddNote() {
		this.projectNote = new ProjectNote();
		this.projectNote.setProjectId(this.projectId);
		this.projectNote.setNoteTypeCd(NoteType.USER);
		this.projectNote.setDateEntered(new Timestamp(System
				.currentTimeMillis()));
		this.projectNote.setUserId(InfrastructureDefs
				.getCurrentUserId());
		enterNoteEditMode();
		return PROJECT_NOTE_DETAIL_OUTCOME;
	}
	
	public String startToViewNote() {
		return PROJECT_NOTE_DETAIL_OUTCOME;
	}
	
	public void closeNoteDialog() {
		setNoteEditMode(false);
		setProjectNote(null);
		refreshProjectNotes();
		closeDialog();
	}
	
	private void refreshProjectNotes() {
		try {
			List<ProjectNote> notes = getProjectTrackingService()
					.retrieveProjectNotesByProjectId(projectId);

			project.setNotes(notes);
		} catch (DAOException de) {
			String msg = "Failed to retrieve notes";
			logger.error(msg, de);
			DisplayUtil.displayError(msg);
			handleException(de);
		}
	}
	
	public void saveNote() {
		if(Utility.isNullOrEmpty(this.projectNote.getNoteTxt())) {
			DisplayUtil.displayError("ERROR: Note is empty");
			return;
		}
		
		ValidationMessage[] valMsgs = this.projectNote.validate();
		if (this.projectNote.isValid()) {
			try {
				if (this.projectNote.isNewObject()) {
					getProjectTrackingService().createProjectNote(
							this.projectNote);
					DisplayUtil
							.displayInfo("Project tracking note created successfully");
				} else {
					getProjectTrackingService().modifyProjectNote(this.projectNote);
					DisplayUtil
							.displayInfo("Project tracking note updated successfully");
				}
				closeNoteDialog();
			} catch (RemoteException de) {
				String msg = "Failed to create or update the project tracking note";
				logger.error(msg, de);
				DisplayUtil.displayError(msg);
				handleException(de);
			}
		} else {
			displayValidationMessages("", valMsgs);
		}
	}
	
	public final DefSelectItems getProjectAttachmentTypeDefs() {
		return ProjectAttachmentTypeDef
				.getProjectTypeAttachmentDefs(this.project.getProjectTypeCd());

	}

	public final List<SelectItem> getProjectTrackingEventDefs() {
		List<SelectItem> si = new ArrayList<SelectItem>();
		for (ProjectTrackingEvent event : this.project.getTrackingEvents()) {
			si.add(new SelectItem(event.getEventId(), event.getEventNbr()));
		}

		return si;
	}

	public void initializeAttachmentBean() {
		Attachments attachments = (Attachments) FacesUtil
				.getManagedBean("attachments");
		attachments.addAttachmentListener(this);

		attachments.setSubPath("ProjectTracking" + File.separator
				+ this.projectId);

		attachments.setAttachmentTypesDef(getProjectAttachmentTypeDefs());

		attachments.setNewPermitted(true);
		attachments.setUpdatePermitted(true);
		attachments.setDeletePermitted(true);
		attachments.setHasDocType(true);
	}

	public final String startToAddAttachment() {
		String ret = null;
		Attachments attachments = (Attachments) FacesUtil
				.getManagedBean("attachments");
		if (null != attachments) {
			attachments.startNewAttachment();
			this.projectAttachment = new ProjectAttachment();
			this.projectAttachment.setProjectId(this.projectId);
			this.projectAttachment.setAttachment(attachments.getTempDoc());
			this.projectAttachment.getAttachment().setObjectId(null);
			ret = PROJECT_ATTACHMENT_DETAIL_OUTCOME;
		} else {
			String msg = "Unexpected error. Bean not found: attachments.";
			logger.error(msg, new RuntimeException(msg));
		}

		return ret;
	}

	public final String startToEditAttachment() {
		Attachments attachments = (Attachments) FacesUtil
				.getManagedBean("attachments");
		attachments.startEditAttachment();
		return PROJECT_ATTACHMENT_DETAIL_OUTCOME;
	}

	@Override
	public AttachmentEvent createAttachment(Attachments attachment)
			throws AttachmentException {
		boolean ok = true;

		if (null == this.projectAttachment.getAttachment()) {
			// should never happen
			logger.error("Attempt to process null attachment");
			ok = false;
		} else {
			// make sure document description and type are provided
			if (Utility
					.isNullOrEmpty(this.projectAttachment.getAttachment().getDescription())) {
				DisplayUtil
						.displayError("Please specify the description for this attachment");
				ok = false;
			}
			if (Utility.isNullOrEmpty(this.projectAttachment.getAttachment().getDocTypeCd())) {
				DisplayUtil
						.displayError("Please specify an attachment type for this attachment");
				ok = false;
			}

			if (attachment.getFileToUpload() == null) {
				DisplayUtil
						.displayError("Please specify the file to upload for this attachment");
				ok = false;
			}
		}

		if (ok) {
			try {
				getProjectTrackingService().createProjectTrackingAttachment(
						this.projectAttachment,
						attachment.getFileToUpload().getInputStream());
				this.project.setAttachments(getProjectTrackingService()
						.retrieveProjectTrackingAttachments(this.projectId));
				refreshProjectTrackingEvents(); // to show associated attachment
												// ids
				this.projectAttachment = null; // clean up
				DisplayUtil.displayInfo("Successfully added the attachment");
			} catch (DAOException daoe) {
				DisplayUtil
						.displayError("Create project tracking attachment failed");
				handleException(daoe);
			} catch (IOException ioe) {
				DisplayUtil
						.displayError("Create project tracking attachment failed");
				handleException(new DAOException(ioe.getMessage()));
			}

			closeDialog();
		}
		return null;
	}

	@Override
	public AttachmentEvent updateAttachment(Attachments attachment) {
		boolean ok = true;

		if (null == this.projectAttachment.getAttachment()) {
			// should never happen
			logger.error("Attempt to process null attachment");
			ok = false;
		} else {
			// make sure document description and type are provided
			if (Utility
					.isNullOrEmpty(this.projectAttachment.getAttachment().getDescription())) {
				DisplayUtil
						.displayError("Please specify the description for this attachment");
				ok = false;
			}
			if (Utility.isNullOrEmpty(this.projectAttachment.getAttachment().getDocTypeCd())) {
				DisplayUtil
						.displayError("Please specify an attachment type for this attachment");
				ok = false;
			}
		}

		if (ok) {
			try {
				getProjectTrackingService().updateProjectAttachment(
						this.projectAttachment);
				this.project.setAttachments(getProjectTrackingService()
						.retrieveProjectTrackingAttachments(this.projectId));
				refreshProjectTrackingEvents(); // to show associated attachment
												// ids
				this.projectAttachment = null; // clean up
				DisplayUtil
						.displayInfo("Attachment information was successfully updated");
			} catch (DAOException daoe) {
				DisplayUtil
						.displayError("Failed to update project tracking attachment");
				handleException(daoe);
			}
			closeDialog();
		}
		return null;
	}

	@Override
	public void cancelAttachment() {
		try {
			this.project.setAttachments(getProjectTrackingService()
					.retrieveProjectTrackingAttachments(this.projectId));
			this.projectAttachment = null;
		} catch (DAOException daoe) {
			DisplayUtil
					.displayError("Failed to retrieve project tracking attachments");
			handleException(daoe);
		}
		closeDialog();
	}

	@Override
	public AttachmentEvent deleteAttachment(Attachments attachment) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteAttachment(ReturnEvent re) {
		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
			return;
		}

		try {
			getProjectTrackingService().deleteProjectAttachment(
					this.projectAttachment);
			this.project.setAttachments(getProjectTrackingService()
					.retrieveProjectTrackingAttachments(this.projectId));
			refreshProjectTrackingEvents(); // to show associated attachment ids
			this.projectAttachment = null;
			DisplayUtil.displayInfo("Successfully deleted the attachment");
		} catch (DAOException daoe) {
			DisplayUtil.displayError("Failed to delete the attachment");
			handleException(daoe);
		}
		closeDialog();
	}

	// only allow to delete a tracking event if it is not associated with any attachment(s)
	public boolean getAllowedToDeleteTrackingEvent() {
		boolean ret = true;
		for (ProjectAttachment pa : this.project.getAttachments()) {
			if (null != pa.getTrackingEventId()
					&& pa.getTrackingEventId().equals(
							this.trackingEvent.getEventId())) {
				ret = false;
				break;
			}
		}

		return ret;
	}

	public String loadTrackingEvent() {
		String ret = null;
		if (null != this.trackingEventId) {
			try {
				this.trackingEvent = getProjectTrackingService()
						.retrieveProjectTrackingEvent(this.trackingEventId);
				ret = startToViewTrackingEvent();
			} catch (DAOException daoe) {
				DisplayUtil
						.displayError("Failed to retrieve project tracking event");
				handleException(daoe);
			}
		}
		return ret;
	}
	
	/**
	 * Checks if the current user has privileges to access a project of a given type
	 * 
	 * @return Returns true if the user has access privileges, otherwise false
	 */
	private boolean checkProjectAccessPrivileges() {
		boolean ret = true;
		
		if (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
			return ret;
		}

		Integer securityGroupId = ProjectTypeDef
				.getProjectTypeSecurityGroupId(this.projectTypeCd);

		// if the project type does not have an associated security group
		// then everyone can access
		if (null != securityGroupId) {
			try {
				UseCase[] useCases = getInfrastructureService()
						.retrieveUseCases(securityGroupId);
				for (UseCase uc : useCases) {
					if (!InfrastructureDefs.getCurrentUserAttrs()
							.isCurrentUseCaseValid(uc.getUseCase())) {
						ret = false;
						break;
					}
				}
			} catch (RemoteException re) {
				DisplayUtil
						.displayError("An error occured when trying to retrieve use cases");
				handleException(re);
			}
		}

		return ret;
	}
	
	/** Returns a list of project types accessible by the current user 
	 * 
	 */
	public List<SelectItem> getProjectTypesByAppUsr() {
		List<SelectItem> ret = new ArrayList<SelectItem>();
		
		if (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
			// admin user has access to all project types
			ret = ProjectTypeDef.getData().getItems().getCurrentItems();
		} else {
			try {
				for(SimpleDef def: getInfrastructureService().retrieveProjectTypesByAppUsr(
						InfrastructureDefs.getCurrentUserId())) {
					if(!def.isDeprecated()) {
						ret.add(new SelectItem(def.getCode(), def.getDescription()));
					}
				}
			} catch (DAOException daoe) {
				DisplayUtil
						.displayError("An error occured when trying to retrieve project types");
				handleException(daoe);
			}

		}
		
		return ret;
	}
	
	public void deleteContractAccountants() {
		List<Stars2Object> ret = Stars2Object.deleteStars2Objects(
				getContractAccountantsWrapper().getTable(),
				((Contract) this.project).getAccountantUserIds());

		((Contract) this.project).setAccountantUserIds(ret);
	}
	
	public DefSelectItems getContractAccountantUserDefs() {
		return getFiltertedUserDefs(Stars2Object
				.fromStar2IntObject(((Contract) this.project)
						.getAccountantUserIds()));
	}
	
	public void enterBudgetEditMode() {
		setBudgetEditMode(true);
	}
	
	public void leaveBudgetEditMode() {
		setBudgetEditMode(false);
	}
	
	public String startToAddBudget() {
		this.budget = new Budget();
		this.budget.setProjectId(this.projectId);
		enterBudgetEditMode();
		return BUDGET_DETAIL_OUTCOME;
	}
	
	public String startToViewBudget() {
		return BUDGET_DETAIL_OUTCOME;
	}
	
	private void refreshBudgetList() {
		try {
			List<Budget> budgetList = getProjectTrackingService()
					.retrieveBudgetByProjectId(this.projectId);
			((Contract)this.project).setBudgetList(budgetList);
		} catch (DAOException de) {
			String msg = "Failed to retrieve budget list";
			logger.error(msg, de);
			DisplayUtil.displayError(msg);
			handleException(de);
		}
	}
	
	
	public void saveBudget() {
		ValidationMessage[] valMsgs = this.budget.validate();
		if (null != this.budget) {
			// check duplicate budget function
			for(Budget b: ((Contract)this.project).getBudgetList()) {
				if(!b.getBudgetId().equals(this.budget.getBudgetId())
						&& b.getBudgetFunctionCd().equals(this.budget.getBudgetFunctionCd())) {
					DisplayUtil
							.displayError("ERROR: Duplicate function. Contract already has a budget with this function.");
					return;
				}
			}
			
			if (this.budget.isValid()) {
				try {
					if (this.budget.isNewObject()) {
						getProjectTrackingService().createBudget(this.budget);
						DisplayUtil.displayInfo("Budget created successfully");
					} else {
						getProjectTrackingService().updateBudget(this.budget);
						DisplayUtil.displayInfo("Budget updated successfully");
					}
					closeBudgetDialog();
				} catch (RemoteException de) {
					String msg = "Failed to create or update the budget";
					logger.error(msg, de);
					DisplayUtil.displayError(msg);
					handleException(de);
				}
			} else {
				displayValidationMessages("", valMsgs);
			}
		}
	}
	
	public void deleteBudget(ReturnEvent re) {
		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
			return;
		}
		
		try {
			getProjectTrackingService().deleteBudget(this.budget.getBudgetId());
			DisplayUtil
					.displayInfo("Successfully deleted the budget");
			closeBudgetDialog();
		} catch (DAOException de) {
			String msg = "Failed to delete the budget";
			logger.error(msg, de);
			DisplayUtil.displayError(msg);
			handleException(de);
		}
	}
	
	public void closeBudgetDialog() {
		leaveBudgetEditMode();
		this.budget = null;
		refreshBudgetList();
		closeDialog();
	}
	
	public int getVendorNameRows() {
		int rows = 1; // default
		String vendorName = ((Contract)this.project).getVendorName();
		if(!Utility.isNullOrEmpty(vendorName)
				&& vendorName.length() > 40) {
			rows = 5;
		}
		return rows;
	}

}