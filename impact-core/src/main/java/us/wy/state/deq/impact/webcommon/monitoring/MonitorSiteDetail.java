package us.wy.state.deq.impact.webcommon.monitoring;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.event.ActionEvent;

import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.bo.MonitoringService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.MonitorStatusDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.database.dbObjects.monitoring.Monitor;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorSite;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorSiteNote;

@SuppressWarnings("serial")
public class MonitorSiteDetail extends TaskBase {

    public static final String DETAIL_OUTCOME = "monitoring.monitorSiteDetail";

    private Integer siteId;
    
    private MonitorSite monitorSite;
    
	private TableSorter notesWrapper = new TableSorter();

	private boolean disabledUpdateButton = true;
	
	private boolean editable1;

	private boolean noteModify;

	private MonitorSiteNote modifyNote;

	private boolean editable = false;

	private TableSorter siteMonitorsWrapper = new TableSorter();

	private TableSorter otherMonitorsWrapper = new TableSorter();
	
    private String pageRedirect;
    
    private MonitorGroupDetail monitorGroupDetail;

    private MonitoringService monitoringService;
    
    private MonitorSiteNote monitorSiteNote;

    private boolean cannotDelete;  // used when trying to delete.
    private String cannotDeleteReason;
    
	public MonitorGroupDetail getMonitorGroupDetail() {
		return monitorGroupDetail;
	}

	public void setMonitorGroupDetail(MonitorGroupDetail monitorGroupDetail) {
		this.monitorGroupDetail = monitorGroupDetail;
	}

	public MonitoringService getMonitoringService() {
		return monitoringService;
	}

	public void setMonitoringService(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}
	
	public String getPageRedirect() {
		return pageRedirect;
	}

	public void setPageRedirect(String pageRedirect) {
		this.pageRedirect = pageRedirect;
	}

	public TableSorter getSiteMonitorsWrapper() {
		return siteMonitorsWrapper;
	}

	public void setSiteMonitorsWrapper(TableSorter siteMonitorsWrapper) {
		this.siteMonitorsWrapper = siteMonitorsWrapper;
	}

	public TableSorter getOtherMonitorsWrapper() {
		return otherMonitorsWrapper;
	}

	public void setOtherMonitorsWrapper(TableSorter otherMonitorsWrapper) {
		this.otherMonitorsWrapper = otherMonitorsWrapper;
	}

	public MonitorSiteNote getModifyNote() {
		return modifyNote;
	}

	public void setModifyNote(MonitorSiteNote modifyNote) {
		this.modifyNote = modifyNote;
	}
	
	public MonitorSiteNote getMonitorSiteNote() {
		return monitorSiteNote;
	}

	public void setMonitorSiteNote(MonitorSiteNote monitorSiteNote) {
		this.monitorSiteNote = monitorSiteNote;
	}

	public boolean isNoteModify() {
		return noteModify;
	}

	public void setNoteModify(boolean noteModify) {
		this.noteModify = noteModify;
	}

	public boolean isEditable1() {
		return editable1;
	}
	
	public final void dialogDone() {
		return;
	}

	public final void addMonitorDialogDone() {
		refreshMonitors();
	}
    
    public boolean isAssociatedWithMonitors() {
    	return getSiteMonitorsWrapper().getData() != null?
    			!getSiteMonitorsWrapper().getData().isEmpty() : false;
    }
    
	public void refreshMonitors() {
		Monitor searchObj = new Monitor();
		searchObj.setSiteId(siteId);
		Monitor[] monitors;
		try {
			monitors = getMonitoringService().searchMonitors(searchObj);
			getSiteMonitorsWrapper().setWrappedData(monitors);
		} catch (DAOException e) {
			DisplayUtil.displayError("Error occurred while refreshing monitors.");
		}
	}

	public void refreshOtherMonitors() {
		Monitor[] monitors;
		try {
			monitors = 
					getMonitoringService()
						.searchMonitorsByAqsId(monitorSite.getAqsSiteId(), 
							monitorSite.getSiteId());
			getOtherMonitorsWrapper().setWrappedData(monitors);
		} catch (DAOException e) {
			DisplayUtil.displayError("Error occurred while refreshing monitors.");
		}
	}

	public final String startAddMonitor() {
		CreateMonitor createMonitor = (CreateMonitor) FacesUtil
				.getManagedBean("createMonitor");
		createMonitor.setMonitor(new Monitor());
		setEditable1(true);
		return "dialog:createMonitor";
	}
	
	public String showAssociatedGroup() {
		getMonitorGroupDetail().setGroupId(getMonitorSite().getGroupId());
/*		getMonitorGroupDetail().submitFromJsp();*/
		getMonitorGroupDetail().submit(false);	
		setFromTODOList(false);
		return SUCCESS;
	}
	
	public void deleteMonitorSite() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			boolean ok = deleteMonitorSiteI();
			if (ok) {
				MonitorSiteSearch monitorSiteSearch = (MonitorSiteSearch) FacesUtil
						.getManagedBean("monitorSiteSearch");
				monitorSiteSearch
						.setPopupRedirectOutcome(MonitorSiteSearch.SEARCH_OUTCOME);
				monitorSiteSearch.submitSearch();
				DisplayUtil.displayInfo("Monitor Site successfully deleted");
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_monitorSiteDetail"))
						.setDisabled(true);
			} else {
				DisplayUtil.displayError("Failed to delete Monitor Site");
			}
			FacesUtil.returnFromDialogAndRefresh();
		} finally {
			clearButtonClicked();
		}
	}

	public boolean deleteMonitorSiteI() {
		boolean ok = true;
		try {
			getMonitoringService().deleteMonitorSite(monitorSite);
			siteId = null;
			if (monitorSite.getGroupId().equals(getMonitorGroupDetail().getGroupId())) {
				getMonitorGroupDetail().refreshAssociatedSites();
			}
		} catch (RemoteException re) {
			handleException(
					"exception in Monitoring " + monitorSite.getSiteId(), re);
			ok = false;
		}
		return ok;
	}
	
	public final void setEditable1(boolean editable1) {
		this.editable1 = editable1;
	}


	
	public TableSorter getNotesWrapper() {
		return notesWrapper;
	}

	public void setNotesWrapper(TableSorter notesWrapper) {
		this.notesWrapper = notesWrapper;
	}

	
	public boolean isDisabledUpdateButton() {
		return disabledUpdateButton;
	}

	public void setDisabledUpdateButton(boolean disabledUpdateButton) {
		this.disabledUpdateButton = disabledUpdateButton;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public Timestamp getMaxDate() {
		return new Timestamp(System.currentTimeMillis());
	}
	

	
	public String refresh() {
		if(siteId != null) {
			if (FAIL == submit(false)) {
				return null;
			}
		}
		return DETAIL_OUTCOME;
	}

    public final String submitFromJsp() { // from Search Datagrid
    	String rtn = submit(false);
        createLinkedToObjs();
		setFromTODOList(false);
		if (isInternalApp()) {
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_monitorSiteDetail")).setDisabled(false);
		}
		if (isPublicApp()){
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_aqdMonitorSiteDetail")).setDisabled(false);
		}
        return rtn;
    }

    // submit
    public final String submit(boolean readOnlyDB) { 
//        savedDesName = null;
//        associatedWithFacility = null;
//        allowedToChangeFacility = false;
		try {
			monitorSite = getMonitoringService().retrieveMonitorSite(siteId);
			if(monitorSite == null) {
				DisplayUtil.displayError("No monitor site found with id: "
						+ siteId + ".");
				return FAIL;
			} else {
				refreshMonitors();
				refreshOtherMonitors();
			}
	    } catch (RemoteException re) {
	    	DisplayUtil.displayError("Retrieval of monitor site failed");
	        logger.error(re.getMessage(), re);
	    }
//	 
//        useDescription = desName;
//        if(savedDesName != null) {
//        	useDescription = savedDesName; 
//        }
		
		setNotesInMonitorSite();
        return SUCCESS;
    }
    
    private void createLinkedToObjs() {
//    	if (correspondence == null) {
//    		linkedToObj = "";
//    	} else if (correspondence.getLinkedToTypeCd() == null || correspondence.getLinkedToId() == null || correspondence.getLinkedToId().equals(0)) {
//    		linkedToObj = "";
//    	} else {
//    		linkedToObj = correspondence.getLinkedToId().toString();
//    	}
    }

    public String editSite() {
        setEditable(true);
        
        
        return null;
    }
    
    public String cancelEdit() {
    	setEditable(false);
    	return submit(false); //TODO return val?
    }

    public String saveEditSite() {
        boolean operationOK = true;
        
    	// clean up orphan data
//    	cleanOrphanData();
        
        String errorClientIdPrefix = "monitorSite:";
        
        if(displayValidationMessages(errorClientIdPrefix,
        		monitorSite.validate())) {
        	operationOK = false;
        }
        
        if(operationOK) {
        	if (monitorSite.getStatus().equals(getActiveStatusCd())) {
        		monitorSite.setEndDate(null);
        	}
            try {
            	getMonitoringService().modifyMonitorSite(monitorSite);

                submit(false);  //   Retrieve new object
            } 
            catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                operationOK = false;
            }
            setEditable(false);
            if (operationOK) {
                DisplayUtil.displayInfo("Monitor site updated successfully");
            }
            else {
                cancelEdit();
                DisplayUtil.displayError("Monitor site update failed");
            }
        }
        return SUCCESS;

    	
    	
//    	setEditable(false);
//    	return null;
    }

    public boolean isEditable() {
    	return editable;
    }
    
	@Override
	public List<ValidationMessage> validate(Integer inActivityTemplateId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findOutcome(String url, String ret) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getExternalId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExternalId(Integer externalId) {
		// TODO Auto-generated method stub
		
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public MonitorSite getMonitorSite() {
		return monitorSite;
	}

	public void setMonitorSite(MonitorSite monitorSite) {
		this.monitorSite = monitorSite;
	}
	
	public final void closeDialog() {
        FacesUtil.returnFromDialogAndRefresh();
    }
	
	/* START CODE: monitor site notes */

	public final String startViewNote() {
		setEditable1(false);
		noteModify = false;
		monitorSiteNote = new MonitorSiteNote(modifyNote);
		return "dialog:monitorSiteNoteDetail";
	}

	public final String startEditNote(ActionEvent actionEvent) {
		setEditable1(true);
		noteModify = true;
		return "dialog:monitorSiteNoteDetail";
	}

	public final String startAddNote() {
		setEditable1(true);
		noteModify = false;
		monitorSiteNote = new MonitorSiteNote();
		monitorSiteNote.setMonitorSiteId(siteId);
		monitorSiteNote.setUserId(InfrastructureDefs.getCurrentUserId());
		monitorSiteNote.setNoteTypeCd(NoteType.DAPC);
		monitorSiteNote
				.setDateEntered(new Timestamp(System.currentTimeMillis()));
		return "dialog:monitorSiteNoteDetail";
	}

	public final boolean isDisableNoteButton() {
		boolean isAdmin = InfrastructureDefs.getCurrentUserAttrs()
				.isStars2Admin();

		if (isReadOnlyUser()) {
			return true;
		}

		if (isAdmin)
			return false;

		boolean isCreatedUser = monitorSiteNote.getUserId().equals(
				InfrastructureDefs.getCurrentUserId());
		if (isCreatedUser)
			return false;

		return true;
	}

	public final boolean isReadOnlyNote() {
		if (!editable1) {
			return true;
		} else if (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
			return false;
		} else if (!monitorSiteNote.getUserId().equals(
				InfrastructureDefs.getCurrentUserId())) {
			return true;
		}
		return false;
	}

	public final void applyEditNote(ActionEvent actionEvent) {
		boolean operationOK = true;

		// make sure note is provided
		if (monitorSiteNote.getNoteTxt() == null
				|| monitorSiteNote.getNoteTxt().trim().equals("")) {
			DisplayUtil.displayError("Attribute Note is not set.");
			return;
		}

		ValidationMessage[] validationMessages = monitorSiteNote.validate();

		if (validationMessages.length > 0) {
			if (displayValidationMessages("", validationMessages)) {
				return;
			}
		}

		try {
			if (noteModify == false) {
				getMonitoringService().createMonitorSiteNote(monitorSiteNote);
			} else {
				// edit
				getMonitoringService().modifyMonitorSiteNote(monitorSiteNote);
			}

			refreshNotes();

		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		if (operationOK) {
			DisplayUtil.displayInfo("Monitor Site notes updated successfully");
		} else {
			cancelEditNote();
			DisplayUtil.displayError("Updating Monitor Site notes failed");
		}

		noteModify = false;
		setEditable1(false);
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void cancelEditNote() {
		noteModify = false;
		setEditable1(false);
		FacesUtil.returnFromDialogAndRefresh();
	}

	private void refreshNotes() {

		monitorSiteNote = null;

		MonitorSiteNote[] monitorSiteNotes;
		try {
			monitorSiteNotes = getMonitoringService().retrieveMonitorSiteNotes(
					siteId);
			this.monitorSite.setNotes(new ArrayList<MonitorSiteNote>(Arrays
					.asList(monitorSiteNotes)));
			setNotesInMonitorSite();
		} catch (RemoteException re) {
			handleException(re);
		}
	}

	protected final void setNotesInMonitorSite() {
		if (this.isInternalApp()) {
			notesWrapper.setWrappedData(monitorSite.getNotes());
		}
	}
	
	public String getActiveStatusCd() {
		return MonitorStatusDef.getActiveCode();
	}
	//used for navigation from homeMonitorGroup.jsp (for Portal/Public)
	public String submitFromAmbientJsp(){
    	String rtn = submit(false);
        createLinkedToObjs();
		setFromTODOList(false);
        return rtn;
	}
	
	public boolean isCannotDelete() {
		return cannotDelete;
	}

	public void setCannotDelete(boolean cannotDelete) {
		this.cannotDelete = cannotDelete;
	}

	public String getCannotDeleteReason() {
		return cannotDeleteReason;
	}

	public void setCannotDeleteReason(String cannotdeleteReason) {
		this.cannotDeleteReason = cannotdeleteReason;
	}
	
	public boolean getDisplayReferencedInspectionsTable() {
		return isCannotDelete() && !getMonitorSite().getInspectionsReferencedIn().isEmpty();
	}

	public String startDelete() {
		if (getMonitorSite().getInspectionsReferencedIn().size() > 0 ){
			setCannotDelete(true);
			setCannotDeleteReason("You cannot delete this Monitor Site while it is being referenced in other places.");
		}
		return "dialog:deleteMonitorSite";
	}
}
