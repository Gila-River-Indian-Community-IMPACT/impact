package us.oh.state.epa.stars2.app.tools;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import us.oh.state.epa.stars2.app.workflow.ReassignActivities;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRole;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataField;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.def.FacilityRoleDef;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

/**
 * 
 * Following script is needed in the database.
 * 
 * INSERT INTO cm_bulk_def
        (bulk_id, bulk_nm, bulk_group_nm, bulk_menu, bulk_dsc, bulk_class, bulk_search_type)
    VALUES (501, 'Task Reassignments', 'Workflow Operations', 'bops',
        'Task assignment Bulk Update', 'us.oh.state.epa.stars2.app.tools.TaskReassignments',
        'facility');
        
    INSERT INTO cm_attributes_def (attributes_cd, attributes_dsc)
    VALUES ('sfid', 'Staff ID');

    INSERT INTO cm_bulk_attributes_xref (bulk_id, attributes_cd)
    VALUES (501, 'cnty');

    INSERT INTO cm_bulk_attributes_xref (bulk_id, attributes_cd)
    VALUES (501, 'dola');

    INSERT INTO cm_bulk_attributes_xref (bulk_id, attributes_cd)
    VALUES (501, 'fcid');
    
    INSERT INTO cm_bulk_attributes_xref (bulk_id, attributes_cd)
    VALUES (501, 'fcnm');
    
    INSERT INTO cm_bulk_attributes_xref (bulk_id, attributes_cd)
    VALUES (501, 'opst');
    
    INSERT INTO cm_bulk_attributes_xref (bulk_id, attributes_cd)
    VALUES (501, 'ptcl');
    
    INSERT INTO cm_bulk_attributes_xref (bulk_id, attributes_cd)
    VALUES (501, 'naic');
    
    INSERT INTO cm_bulk_attributes_xref (bulk_id, attributes_cd)
    VALUES (501, 'port');
    
    INSERT INTO cm_bulk_attributes_xref (bulk_id, attributes_cd)
    VALUES (501, 'zipc');
    
    INSERT INTO cm_bulk_attributes_xref (bulk_id, attributes_cd)
    VALUES (501, 'fcro'); // search on facility role

 * 
 * @author Pyeh
 *
 */
@SuppressWarnings("serial")
public class RoleReassignments extends BulkOperation {
    private FacilityList[] facilities;
    private LinkedHashMap<String, ArrayList<ProcessActivity>> fas;
    private FacilityRole role;
    private ArrayList<ProcessActivity> activities;
    private boolean affectMultipleDolaas;
    private ReadWorkFlowService workFlowService;

    public ReadWorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public void setWorkFlowService(ReadWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

    public RoleReassignments() {
        super();
        setButtonName("Role Reassignments");
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
        // set indicator that this is a task-related operation
        // this will allow task-related fields to be rendered on the 
        // bulk facility search table.
        lcatalog.setTaskOperation(true);

        this.catalog = lcatalog;

        facilities 
            = getFacilityService().searchFacilities(catalog.getFacilityNm(),
                                            catalog.getFacilityId(),
                                            null,
                                            null,
                                            catalog.getCounty(),
                                            catalog.getOperatingStatus(),
                                            catalog.getDoLaa(),
                                            catalog.getNaicsCd(),
                                            catalog.getPermittingClassCd(),
                                            catalog.getTvPermitStatus(),
                                            null,
                                            catalog.getCity(),
                                            catalog.getZipCode(), 
                                            catalog.isPortable()? "Y" : "N", 
                                            null, true, null);
        int i = 0;
        setMaximum(facilities.length);
        setValue(i);
        setSearchStarted(true);
        fas = new LinkedHashMap<String, ArrayList<ProcessActivity>>();
        ArrayList<FacilityList> tfs = new ArrayList<FacilityList>();

        for (FacilityList facility : facilities) {
            if (catalog.getSearchThread().isInterrupted()) {
                // user clicked on reset stop loop and jump out.
                break;
            }
            prepareTasks(facility, tfs);
            setValue(i++);
        }
        facilities = tfs.toArray(new FacilityList[0]);

        catalog.setFacilities(facilities);
        setSearchCompleted(true);

    }

    private void prepareTasks(FacilityList facility, ArrayList<FacilityList> tfs) {
        String errorMsg = "Role reassignment search failed for facility id "
            + facility.getFacilityId() + ", role cd " + catalog.getFacilityRole();
        ArrayList<ProcessActivity> tfa = new ArrayList<ProcessActivity>();

        ArrayList<String> ss = new ArrayList<String>();
        ss.add(ActivityStatusDef.IN_PROCESS);
        ss.add(ActivityStatusDef.NOT_COMPLETED);
        ss.add(ActivityStatusDef.REFERRED);
        
        ProcessActivity pa = new ProcessActivity();
        pa.setFacilityId(facility.getFacilityId());
        pa.setActivityStatusCds(ss);
        pa.setInStatus(true);
        pa.setPerformerTypeCd("M");
        pa.setUnlimitedResults(true);
        try {
            ProcessActivity[] ta = getWorkFlowService().retrieveActivityList(pa);
            for (ProcessActivity tpa : ta) {
            	if (tpa.getProcessEndDt() != null) {
            		logger.error("Process " + tpa.getProcessId() + " has an end date, but not all activities are ended");
            		continue;
            	}
            	boolean addActivity = false;
                ActivityTemplate at = getWorkFlowService().retrieveActivityTemplate(tpa.getActivityTemplateId());
                if (tpa.getActivityTemplateId() == 0) {
                	DataField[] dataFields = getWorkFlowService().retrieveDataFieldsForProcess(tpa.getProcessId());
                	if (dataFields != null) {
                		for (DataField df : dataFields) {
                			if ("Task Name".equals(df.getDataName()) && "Facility Shutdown".equals(df.getDataValue())) {
                				if (FacilityRoleDef.FACILITY_PROFILE_ADMIN.equals(catalog.getFacilityRole()) 
                						//||
                						//FacilityRoleDef.EMISSION_BANKING_NOTIFICATION.equals(catalog.getFacilityRole())
                						) {
                					addActivity = true;
                				}
                			}
                		}
                	}
                } else if (catalog.getFacilityRole().equals(at.getRoleCd())) {
                	addActivity = true;
                }
                if (addActivity) {
                    tfa.add(tpa);
                    FacilityList newFacility = new FacilityList(facility);
                    newFacility.setUserId(tpa.getUserId());
                    newFacility.setProcessId(tpa.getProcessId());
                    newFacility.setActivityTemplateId(tpa.getActivityTemplateId());
                    newFacility.setLoopCnt(tpa.getLoopCnt());
                    newFacility.setActivityTemplateNm(tpa.getActivityTemplateNm());
                    tfs.add(newFacility);
                    fas.put(newFacility.getFacilityId(), tfa);
                    logger.debug("activity template name: " + tpa.getActivityTemplateNm() +
                            ", facility = " + facility.getFacilityId() + ", user = " + tpa.getUserId());
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
        activities = new ArrayList<ProcessActivity>(); 
        FacilityList[] selectedFacilities = lcatalog.getSelectedFacilities();
        for (FacilityList facility : selectedFacilities) {
            if (facility.isSelected()){
            	for (ProcessActivity pa : fas.get(facility.getFacilityId())) {
            		if (pa.getProcessId().equals(facility.getProcessId()) &&
            				pa.getActivityTemplateId().equals(facility.getActivityTemplateId())) {
                        activities.add(pa);
            		}
            	}
            }
        }
        
        affectMultipleDolaas = false;
        String s = "";
        for(FacilityList fList : selectedFacilities){            	
        	if(s.length() > 0 && !fList.getDoLaaCd().equals(s)){
        		affectMultipleDolaas = true;            		
        		break;
        	}            		
        	s = fList.getDoLaaCd();            	
        }   
        
        return;
    }

    public final String performPostWork(BulkOperationsCatalog lcatalog) {

        // set user id to all activities
        for (ProcessActivity pa : activities)
            pa.setUserId(role.getUserId());
        
        ReassignActivities ra = (ReassignActivities)FacesUtil.getManagedBean("reassignActivities");
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

    
}
