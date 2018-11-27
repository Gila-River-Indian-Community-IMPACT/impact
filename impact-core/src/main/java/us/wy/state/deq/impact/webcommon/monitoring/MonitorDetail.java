package us.wy.state.deq.impact.webcommon.monitoring;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.bo.MonitoringService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.MonitorParamTypeDef;
import us.oh.state.epa.stars2.def.MonitorStatusDef;
import us.oh.state.epa.stars2.def.MonitorTypeDef;
import us.oh.state.epa.stars2.def.MonitoringCollFreqCodeDef;
import us.oh.state.epa.stars2.def.MonitoringDurationCodeDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.WxParamDef;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.database.dbObjects.monitoring.Monitor;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorNote;

public class MonitorDetail extends TaskBase {

	private Integer monitorId;
	
	private Monitor monitor;
	
	private TableSorter notesWrapper = new TableSorter();
	
	private boolean disabledUpdateButton = true;
	
	private boolean editable1;

	private boolean noteModify;
	
	private boolean editable = false;
	
	private String popupRedirectOutcome;
	
    public static final String DETAIL_OUTCOME = "monitoring.monitorDetail";

    private MonitorSiteDetail monitorSiteDetail;
    
    private MonitoringService monitoringService;
    
    private MonitorNote modifyNote;
    
    private MonitorNote monitorNote;

	public MonitoringService getMonitoringService() {
		return monitoringService;
	}

	public void setMonitoringService(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}

	
    public MonitorSiteDetail getMonitorSiteDetail() {
		return monitorSiteDetail;
	}

	public void setMonitorSiteDetail(MonitorSiteDetail monitorSiteDetail) {
		this.monitorSiteDetail = monitorSiteDetail;
	}
	
	public MonitorNote getModifyNote() {
		return modifyNote;
	}

	public void setModifyNote(MonitorNote modifyNote) {
		this.modifyNote = modifyNote;
	}
	
	public MonitorNote getMonitorNote() {
		return monitorNote;
	}

	public void setMonitorNote(MonitorNote monitorNote) {
		this.monitorNote = monitorNote;
	}

	public String editMonitor() {
        setEditable(true);
        
        
        return null;
    }
	
	public String getActiveStatusCd() {
		return MonitorStatusDef.getActiveCode();
	}
	
	public final void closeDialog() {
        FacesUtil.returnFromDialogAndRefresh();
    }

	public String getPopupRedirectOutcome() {
		return popupRedirectOutcome;
	}

	public void setPopupRedirectOutcome(String popupRedirectOutcome) {
		this.popupRedirectOutcome = popupRedirectOutcome;
	}

	public void deleteMonitor() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			boolean ok = deleteMonitorI();
			if (ok) {
				setPopupRedirectOutcome(MonitorSiteDetail.DETAIL_OUTCOME);
				getMonitorSiteDetail().refresh();
				DisplayUtil.displayInfo("Monitor successfully deleted");
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_monitorDetail"))
						.setDisabled(true);
			} else {
				DisplayUtil.displayError("Failed to delete Monitor");
			}
			FacesUtil.returnFromDialogAndRefresh();
		} finally {
			clearButtonClicked();
		}
	}

	public boolean deleteMonitorI() {
		boolean ok = true;
		try {
			getMonitoringService().deleteMonitor(monitor);
			monitorId = null;
			if (monitor.getSiteId().equals(getMonitorSiteDetail().getSiteId())) {
				getMonitorSiteDetail().refreshMonitors();
			}
		} catch (RemoteException re) {
			handleException(
					"exception in Monitoring " + monitor.getSiteId(), re);
			ok = false;
		}
		return ok;
	}

	public final String getPopupRedirect() {
        if (popupRedirectOutcome != null) {
            FacesUtil.setOutcome(null, popupRedirectOutcome);
            popupRedirectOutcome = null;
        }
        return null;
    }
	


    public String saveEditMonitor() {
        boolean operationOK = true;
        
    	// clean up orphan data
//    	cleanOrphanData();
        
        String errorClientIdPrefix = "monitorSite:";
        
        if(displayValidationMessages(errorClientIdPrefix,
        		monitor.validate())) {
        	operationOK = false;
        }
        
        if(operationOK) {
        	
        	if (monitor.getStatus().equals(getActiveStatusCd())) {
        		monitor.setEndDate(null);
        	}
        	
        	if (monitor.isTypeAmbient()) {
        		monitor.setParameterMet(null);
        	}
        	
        	if (monitor.isTypeMeteorological()) {
        		monitor.setParameter(null);
        	}
        	
            try {
            	getMonitoringService().modifyMonitor(monitor);

                submit(false);  //   Retrieve new object
            } 
            catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                operationOK = false;
            }
            setEditable(false);
            if (operationOK) {
                DisplayUtil.displayInfo("Monitor updated successfully");
            }
            else {
                cancelEdit();
                DisplayUtil.displayError("Monitor update failed");
            }
        }
        return SUCCESS;

    	
    	
//    	setEditable(false);
//    	return null;
    }

    public String cancelEdit() {
    	setEditable(false);
    	return submit(false); //TODO return val?
    }

	public String showAssociatedSite() {
		getMonitorSiteDetail().setSiteId(getMonitor().getSiteId());
		/*getMonitorSiteDetail().submitFromJsp();*/
    	getMonitorSiteDetail().submit(false);
		setFromTODOList(false);
		return SUCCESS;
	}
	


	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public Monitor getMonitor() {
		return monitor;
	}

	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}

	public Integer getMonitorId() {
		return monitorId;
	}

	public String refresh() {
		return DETAIL_OUTCOME;
	}

	public Timestamp getMaxDate() {
		return new Timestamp(System.currentTimeMillis());
	}
	

	public final DefSelectItems getCollFreqCdDefs() {
		return MonitoringCollFreqCodeDef.getData().getItems();
	}


	public final DefSelectItems getDurationCdDefs() {
		return MonitoringDurationCodeDef.getData().getItems();
	}

	public final DefSelectItems getStatusDefs() {
		return MonitorStatusDef.getData().getItems();
	}

	public final DefSelectItems getParamTypeDefs() {
		return MonitorParamTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getMetParamTypeDefs() {
		return WxParamDef.getData().getItems();
	}

	public final DefSelectItems getTypeDefs() {
		return MonitorTypeDef.getData().getItems();
	}

	public void setMonitorId(Integer monitorId) {
		this.monitorId = monitorId;
	}

    public final String submitFromJsp() { // from Search Datagrid
    	System.out.println("----> submitFromJsp(): " +monitorId);
    	String rtn = submit(false);
        createLinkedToObjs();
		if (isInternalApp()) {
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_monitorDetail")).setDisabled(false);
		}
		if (isPublicApp()) {
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_aqdMonitorDetail")).setDisabled(false);
		}
        return rtn;
    }

    // submit
    public final String submit(boolean readOnlyDB) { 
//        savedDesName = null;
//        associatedWithFacility = null;
//        allowedToChangeFacility = false;
		try {
			monitor = getMonitoringService().retrieveMonitor(monitorId);
			if(monitor == null) {
				DisplayUtil.displayError("No monitor found with id: "
						+ monitorId + ".");
				return FAIL;
			} else {
		        
			}
	    } catch (RemoteException re) {
	    	DisplayUtil.displayError("Retrieval of monitor failed");
	        logger.error(re.getMessage(), re);
	    }
//	 
//        useDescription = desName;
//        if(savedDesName != null) {
//        	useDescription = savedDesName; 
//        }
		
		setNotesInMonitor();

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

	public boolean isEditable1() {
		return editable1;
	}

	public void setEditable1(boolean editable1) {
		this.editable1 = editable1;
	}

	public boolean isNoteModify() {
		return noteModify;
	}

	public void setNoteModify(boolean noteModify) {
		this.noteModify = noteModify;
	}

	/* START CODE: monitor notes */

	public final String startViewNote() {
		setEditable1(false);
		noteModify = false;
		monitorNote = new MonitorNote(modifyNote);
		return "dialog:monitorNoteDetail";
	}

	public final String startEditNote(ActionEvent actionEvent) {
		setEditable1(true);
		noteModify = true;
		return "dialog:monitorNoteDetail";
	}

	public final String startAddNote() {
		setEditable1(true);
		noteModify = false;
		monitorNote = new MonitorNote();
		monitorNote.setMonitorId(monitorId);
		monitorNote.setUserId(InfrastructureDefs.getCurrentUserId());
		monitorNote.setNoteTypeCd(NoteType.DAPC);
		monitorNote.setDateEntered(new Timestamp(System.currentTimeMillis()));
		return "dialog:monitorNoteDetail";
	}

	public final boolean isDisableNoteButton() {
		boolean isAdmin = InfrastructureDefs.getCurrentUserAttrs()
				.isStars2Admin();

		if (isReadOnlyUser()) {
			return true;
		}

		if (isAdmin)
			return false;

		boolean isCreatedUser = monitorNote.getUserId().equals(
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
		} else if (!monitorNote.getUserId().equals(
				InfrastructureDefs.getCurrentUserId())) {
			return true;
		}
		return false;
	}

	public final void applyEditNote(ActionEvent actionEvent) {
		boolean operationOK = true;

		// make sure note is provided
		if (monitorNote.getNoteTxt() == null
				|| monitorNote.getNoteTxt().trim().equals("")) {
			DisplayUtil.displayError("Attribute Note is not set.");
			return;
		}

		ValidationMessage[] validationMessages = monitorNote.validate();

		if (validationMessages.length > 0) {
			if (displayValidationMessages("", validationMessages)) {
				return;
			}
		}

		try {
			if (noteModify == false) {
				getMonitoringService().createMonitorNote(monitorNote);
			} else {
				// edit
				getMonitoringService().modifyMonitorNote(monitorNote);
			}

			refreshNotes();

		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		if (operationOK) {
			DisplayUtil.displayInfo("Monitor notes updated successfully");
		} else {
			cancelEditNote();
			DisplayUtil.displayError("Updating Monitor notes failed");
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

		monitorNote = null;

		MonitorNote[] monitorNotes;
		try {
			monitorNotes = getMonitoringService().retrieveMonitorNotes(
					monitorId);
			this.monitor.setNotes(new ArrayList<MonitorNote>(Arrays
					.asList(monitorNotes)));
			setNotesInMonitor();
		} catch (RemoteException re) {
			handleException(re);
		}
	}

	protected final void setNotesInMonitor() {
		if (this.isInternalApp()) {
			notesWrapper.setWrappedData(monitor.getNotes());
		}
	}

	public final void dialogDone() {
		return;
	}
	
	public void monitorTypeChanged(ValueChangeEvent event) {
		getMonitor().setParameter(null);
		getMonitor().setParameterMet(null);
	}
	
    public final String submitFromAmbientJsp() { // from Search Datagrid in homeMonitorSite.jsp (Portal/Public)
    	System.out.println("----> submitFromJsp(): " +monitorId);
    	String rtn = submit(false);
        createLinkedToObjs();
        return rtn;
    }
}
