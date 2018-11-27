package us.oh.state.epa.stars2.app.tools;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import us.oh.state.epa.stars2.app.workflow.ReassignActivities;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRole;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

/**
 * 
 * Following script is needed in the database.
 * 
INSERT INTO cm_bulk_def
        (bulk_id, bulk_nm, bulk_group_nm, bulk_menu, bulk_dsc, bulk_class, 
        bulk_search_type)
    VALUES (502, 'User Task Reassignments', 'Workflow Operations', 'bops',
        'User Task assignment Bulk Update', 
        'us.oh.state.epa.stars2.app.tools.UserTaskReassignments',
        'facility');

INSERT INTO cm_bulk_attributes_xref (bulk_id, attributes_cd)
    VALUES (502, 'sfid');

INSERT INTO cm_bulk_attributes_xref (bulk_id, attributes_cd)
    VALUES (502, 'fcid');
    
INSERT INTO cm_bulk_attributes_xref (bulk_id, attributes_cd)
    VALUES (502, 'dola');

 * 
 * @author Pyeh
 *
 */
@SuppressWarnings("serial")
public class UserTaskReassignments extends BulkOperation {

	private FacilityList[] facilities;
    private LinkedHashMap<String, ArrayList<ProcessActivity>> fas;
    private FacilityRole role;
    private ArrayList<ProcessActivity> activities;
    private boolean affectMultipleDolaas;
    private ReadWorkFlowService workFlowService = App.getApplicationContext()
			.getBean(ReadWorkFlowService.class);
	private boolean taskReassignment;

    public ReadWorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public void setWorkFlowService(ReadWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

    public UserTaskReassignments() {
        super();
        setButtonName("Reassign Tasks");
        setNavigation("dialog:taskReassignments");
    }

    /**
     * Each bulk operation is responsible for providing a search operation based
     * on the paramters gathered by the BulkOperationsCatalog bean. The search
     * is run when the "Select" button on the Bulk Operations screen is clicked.
     * The BulkOperationsCatalog will then display the results of the search.
     * Below the results is a "Bulk Operation" button.
     */
    public final void searchFacilities(BulkOperationsCatalog lcatalog)
        throws RemoteException {
    	
        // Set the indicator that this is a task-related operation.
        // This will allow task-related fields to be rendered on the 
        // bulk facility search table.
        lcatalog.setTaskOperation(true);

        this.catalog = lcatalog;

		facilities = getFacilityService().searchFacilities(
				catalog.getFacilityNm(), catalog.getFacilityId(),
				catalog.getCompanyName(), null, catalog.getCounty(),
				catalog.getOperatingStatusCd(), catalog.getDoLaa(),
				catalog.getNaicsCd(), catalog.getPermitClassCd(),
				catalog.getTvPermitStatus(), catalog.getAddress1(), null, null,
				null, null, true, catalog.getFacilityTypeCd());

        setMaximum(facilities.length);
        setValue(0);
        
        int i = 0;        
        setSearchStarted(true);
        fas = new LinkedHashMap<String, ArrayList<ProcessActivity>>();
        ArrayList<FacilityList> tfs = new ArrayList<FacilityList>();

		prepareTasks(facilities, tfs);
		setValue(i++);

		facilities = tfs.toArray(new FacilityList[0]);

        catalog.setFacilities(facilities);
        setSearchCompleted(true);

    }

    private void prepareTasks(FacilityList[] facilities, ArrayList<FacilityList> tfs) {
 
    	String errorMsg = "";
        Integer userId = catalog.getStaffId();

        errorMsg = "User Task Reassignments search failed, staff: " + catalog.getStaffId();
        
        ArrayList<ProcessActivity> tfa = new ArrayList<ProcessActivity>();

        ArrayList<String> ss = new ArrayList<String>();
        ss.add(ActivityStatusDef.IN_PROCESS);
        ss.add(ActivityStatusDef.NOT_COMPLETED);
        ss.add(ActivityStatusDef.REFERRED);
        
        ProcessActivity pa = new ProcessActivity();
        pa.setUserId(userId);
        pa.setActivityStatusCds(ss);
        pa.setInStatus(true);
        pa.setPerformerTypeCd("M");
        pa.setUnlimitedResults(true);

		HashMap<String, FacilityList> facHash = new HashMap<String, FacilityList>();
		String[] facilityIds = new String[facilities.length];
		for (int i = 0; i < facilities.length; i++) {
			facHash.put(facilities[i].getFacilityId(), facilities[i]);
			facilityIds[i] = facilities[i].getFacilityId();
		}

        try {
			ProcessActivity[] ta = getWorkFlowService().retrieveActivityListForFacilities(pa, facilityIds,
					userId);

            for (ProcessActivity tpa : ta) {
            	if (tpa.getProcessEndDt() != null) {
            		logger.error("Process " + tpa.getProcessId() + " has an end date, but not all activities are ended");
            		continue;
            	}
                if (userId.equals(tpa.getUserId())) {
                    tfa.add(tpa);
                    FacilityList newFacility = new FacilityList(facHash.get(tpa.getFacilityId()));
                    newFacility.setUserId(tpa.getUserId());
                    newFacility.setProcessId(tpa.getProcessId());
                    newFacility.setActivityTemplateId(tpa.getActivityTemplateId());
                    newFacility.setLoopCnt(tpa.getLoopCnt());
                    newFacility.setActivityTemplateNm(tpa.getActivityTemplateNm());
                    tfs.add(newFacility);
                    fas.put(newFacility.getFacilityId(), tfa);
                }
            }
        } catch (RemoteException re) {
            DisplayUtil.displayError(errorMsg);
            logger.error(errorMsg, re);
        }
        
        return;
    }

    /**
     * When the "Bulk Operation" button is clicked, the BulkOperationsCatalog
     * class will call this method, ensure that the BulkOperation class is
     * placed into the jsf context, and return the value of the getNavigation()
     * method to the jsf controller. Any further requests from the page that is
     * navigated to are handled by this bean in the normal way. The default
     * version does no preliminary work.
     */
    public final void performPreliminaryWork(BulkOperationsCatalog lcatalog) {
    	
		affectMultipleDolaas = false;
		String s = "";
		activities = new ArrayList<ProcessActivity>();
		FacilityList[] selectedFacilities = lcatalog.getSelectedFacilities();
		for (FacilityList fList : selectedFacilities) {
			if (!affectMultipleDolaas && s.length() > 0 && !fList.getDoLaaCd().equals(s)) {
				affectMultipleDolaas = true;
			}
			s = fList.getDoLaaCd();
        	for (ProcessActivity pa : fas.get(fList.getFacilityId())) {
        		if (pa.getProcessId().equals(fList.getProcessId()) &&
        				pa.getActivityTemplateId().equals(fList.getActivityTemplateId())) {
                    activities.add(pa);
        		}
        	}
		}
		
        setTaskReassignment(false);
        
        return;
    }

    public final String performPostWork(BulkOperationsCatalog lcatalog) {

        // Set user id for all activities.
        for (ProcessActivity pa : activities)
            pa.setUserId(role.getUserId());
        
        ReassignActivities ra = (ReassignActivities) FacesUtil.getManagedBean("reassignActivities");
        ra.setActivities(activities.toArray(new ProcessActivity[0]));
        ra.applyWithoutRefresh();
        DisplayUtil.displayInfo("Operation completed successfully.");
        return SUCCESS;
    }

    public final FacilityRole getRole() {
        role = new FacilityRole();
        return role;
    }

	public boolean isAffectMultipleDolaas() {
		return affectMultipleDolaas;
	}

	public void setAffectMultipleDolaas(boolean affectMultipleDolaas) {
		this.affectMultipleDolaas = affectMultipleDolaas;
	}

	public boolean isTaskReassignment() {
		return taskReassignment;
	}

	public void setTaskReassignment(boolean taskReassignment) {
		this.taskReassignment = taskReassignment;
	}    
}
