package us.oh.state.epa.stars2.app.home;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.app.permit.PermitSearch;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitActivitySearch;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.WorkflowProcessDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

/**
 * 
 * @author Pyeh
 *
 */
@SuppressWarnings("serial")
public class PermitToDoSearch extends AppBase {
    private PermitActivitySearch[] shortActivities;
    private PermitActivitySearch pa = new PermitActivitySearch();
    private boolean hasSearchResults;
    private boolean showFacility = true;
    private boolean fromExternal;
    
	private PermitService permitService;
	
    private ReadWorkFlowService workFlowService;

	public ReadWorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public void setWorkFlowService(ReadWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	public PermitService getPermitService() {
		return permitService;
	}

	public void setPermitService(PermitService permitService) {
		this.permitService = permitService;
	}

    private LinkedHashMap<String, String> workflows;
    
    public PermitToDoSearch() {
        super();
        init();
    }

    public final String submit() {

        try {
            pa.setUnlimitedResults(unlimitedResults());

            PermitActivitySearch[] activities = new PermitActivitySearch[0];
            activities = getPermitService().searchPermitActivity(pa);
            DisplayUtil.displayHitLimit(activities.length);
            if (activities.length == 0) {
                DisplayUtil.displayNoRecords();
            }

            hasSearchResults = true;

            ArrayList<PermitActivitySearch> ret = new ArrayList<PermitActivitySearch>();
            ArrayList<String> aggs = new ArrayList<String>();
            Map<String, PermitActivitySearch> am = new LinkedHashMap<String, PermitActivitySearch>();
            ArrayList<String> maggs = new ArrayList<String>();
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            for (PermitActivitySearch ta : activities) {
                if (ta.getAggregate().equalsIgnoreCase("Y")) {
                    String uid = null;
                    if (ta.getUserId() == null)
                        uid = "Not assigned";
                    else
                        uid = ta.getUserId().toString();
                    String key = uid + ta.getActivityTemplateId() 
                        + ta.getActivityStatusCd();
                    if (ta.getEndDt() != null)
                        key = key + df.format(ta.getEndDt());
                    
                    if (maggs.contains(key)) {
                        continue;
                    } else if (aggs.contains(key)) {
                        String name;
                        if (ta.getActivityTemplateId().equals(0)) {
                            name = ta.getProcessTemplateNm() + " *";
                        } else {
                            name = ta.getActivityTemplateNm() + " *";
                        }

                        ta = am.get(key);
                        ta.setActivityTemplateNm(name);
                        maggs.add(key);
                        continue;
                    } else {
                        aggs.add(key);
                        am.put(key, ta);
                    }
                }
                ret.add(ta);
            }
            shortActivities = ret.toArray(new PermitActivitySearch[0]);
        } catch (RemoteException re) {
            handleException(re);
        }
        return "permitToDo";
    }

    public final String reset() {
        init();
        submit();
        return "permitToDo";
    }
    
    private void init() {
        pa = new PermitActivitySearch();
        pa.setProcessCd(WorkflowProcessDef.PERMITTING);
        pa.setUserId(InfrastructureDefs.getCurrentUserId());
        pa.setActivityStatusCd(ActivityStatusDef.IN_PROCESS);
        hasSearchResults = false;
        fromExternal = false;
        shortActivities = null;
        showFacility = true;
        workflows = null;
    }

    public final LinkedHashMap<String, String> getActivityTypes() {

        LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();
        try {
            ActivityTemplateDef[] activityTypes = getWorkFlowService()
                    .retrieveActivityTemplateDefs(WorkflowProcessDef.PERMITTING);
            for (ActivityTemplateDef temp : activityTypes) {
                String name = temp.getActivityTemplateNm();
                ret.put(name, name);
            }
        } catch (RemoteException e) {
            logger.error(e.getMessage());
        }

        return ret;
    }
    
    public final LinkedHashMap<String, String> getWorkflows() {
        if (workflows == null){
            try {
                workflows = getWorkFlowService().retrieveProcessTemplatesByType(WorkflowProcessDef.PERMITTING);
            } catch (RemoteException e) {
                logger.error(e.getMessage(), e);
                DisplayUtil.displayError("System error. Please contact system administrator");
            }
        }
        return workflows;
    }
    
    public final boolean isFromExternal() {
        return fromExternal;
    }

    public final boolean isShowFacility() {
        return showFacility;
    }

    public final void setShowFacility(boolean showFacility) {
        this.showFacility = showFacility;
    }

    public final Integer getRows() {
        return getPageLimit();
    }

    public final boolean isHasSearchResults() {
        return hasSearchResults;
    }

    public final void setHasSearchResults(boolean hasSearchResults) {
        this.hasSearchResults = hasSearchResults;
    }

    public final PermitActivitySearch[] getShortActivities() {
        return shortActivities;
    }

    public final void setShortActivities(PermitActivitySearch[] shortActivities) {
        this.shortActivities = shortActivities;
    }

    /**
     * @return the pa
     */
    public final PermitActivitySearch getPa() {
        return pa;
    }

    /**
     * @param pa the pa to set
     */
    public final void setPa(PermitActivitySearch pa) {
        this.pa = pa;
    }
    
    public final List<SelectItem> getPermitReasons() {
        List<SelectItem> ret = PermitReasonsDef.getPermitReasons(pa.getPermitTypeCd(), null);
        for (SelectItem si : ret)
            si.setDisabled(false);
        return ret;
    }

    public final List<SelectItem> getPermitGlobalStatusDefs() {
        return PermitSearch.FindPermitGlobalStatusDefs(getParent().getValue(), pa.getPermitTypeCd());
    }
    
    /**
     * 
     * @return
     */
    public final LinkedHashMap<String, String> getPermitDateBy() {
        return PermitSearch.buildPermitDateBy(pa.getPermitTypeCd());
    }
    
}
