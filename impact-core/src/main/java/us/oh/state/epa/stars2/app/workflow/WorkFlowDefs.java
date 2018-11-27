package us.oh.state.epa.stars2.app.workflow;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.PerformerDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.TransitionDef;
import us.oh.state.epa.stars2.def.ActivityReferralTypeDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

@SuppressWarnings("serial")
public class WorkFlowDefs extends AppBase {
    private static final long RESET_TIME_MILLIS = 5 * 60 * 1000;
    private LinkedHashMap<String, String> transitionTypes;
    private LinkedHashMap<String, String> activityStatus;
    private LinkedHashMap<String, String> statesDef;
    private LinkedHashMap<String, String> statusDef;
    private LinkedHashMap<String, String> statusList;
    private LinkedHashMap<String, Integer> workflows;
    private SimpleDef[] processTypes;
    private ActivityTemplateDef[] activityTypes;
    private PerformerDef[] performerTypes;
    private Timestamp nextResetTime;
    
    private ReadWorkFlowService workFlowService;

    public ReadWorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public void setWorkFlowService(ReadWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

    public WorkFlowDefs() {
        super();
        reset();
    }

    public final List<SelectItem> getActivityReferralTypes() {
        return ActivityReferralTypeDef.getData().getItems().getItems(
                getParent().getValue());
    }
    
    /**
     * Check the nextResetTime which is seted base on RESET_TIME_MILLIS
     * everytime when this object is initialled and reseted. Here setting all
     * the defs to null. They will reload from databse when they are called from
     * jsp page.
     */
    private void reset() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (nextResetTime == null) {
            nextResetTime = new Timestamp(now.getTime() + RESET_TIME_MILLIS);
        }
        if (now.after(nextResetTime)) {
            transitionTypes = null;
            activityStatus = null;
            activityTypes = null;
            statesDef = null;
            statusDef = null;
            processTypes = null;
            performerTypes = null;
            nextResetTime = new Timestamp(now.getTime() + RESET_TIME_MILLIS);
        }
    }

	/**
     * @return LinkedHashMap<String, String>
     */
    public final LinkedHashMap<String, String> getStateDef() {
        reset();
        if (statesDef == null) {
            try {
                statesDef = getWorkFlowService().getStateDef();
            } catch (RemoteException re) {
                handleException(re);
            }
        }
        return statesDef;
    }

    public final LinkedHashMap<String, String> getStatusDef() {
        reset();
        if (statusDef == null) {

            try {
                statusDef = getWorkFlowService().getStatusDef();
                statusList = new LinkedHashMap<String, String>();
                for (String key : statusDef.keySet())
                    statusList.put(statusDef.get(key), key);
            } catch (RemoteException re) {
                logger.error(re.getMessage(), re);
                DisplayUtil.displayError("System error. Please contact system administrator");
            }
        }
        return statusDef;
    }

    public final LinkedHashMap<String, String> getTransitionsDef() {
        reset();
        if (transitionTypes == null) {

            try {
                TransitionDef[] tempArray = getWorkFlowService()
                        .retrieveTransitionDef();

                transitionTypes = new LinkedHashMap<String, String>();
                for (TransitionDef tempState : tempArray) {
                    transitionTypes.put(tempState.getDescription(), tempState
                            .getCode());
                }
            } catch (RemoteException re) {
                handleException(re);
            }
        }
        return transitionTypes;
    }

    public final LinkedHashMap<String, String> getActivityStatus() {
        reset();
        if (activityStatus == null) {

            try {
                SimpleDef[] tempArray = getWorkFlowService()
                        .retrieveActivityStatusDef();

                activityStatus = new LinkedHashMap<String, String>();
                for (SimpleDef temp : tempArray) {
                	if (!temp.isDeprecated())
                    activityStatus.put(temp.getDescription(), temp.getCode());
                }
            } catch (RemoteException re) {
                logger.error(re.getMessage(), re);
                DisplayUtil.displayError("System error. Please contact system administrator");
            }
        }
        return activityStatus;
    }

    public final LinkedHashMap<String, String> getProcessTypes() {
        getProcessTypesArray();

        LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();
        for (SimpleDef temp : processTypes) {
            ret.put(temp.getDescription(), temp.getCode());
        }

        return ret;
    }
    
    public final LinkedHashMap<String, String> getProcessTypeDefs() {
        getProcessTypesArray();

        LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();
        for (SimpleDef temp : processTypes) {
        	if (!temp.isDeprecated())
            ret.put(temp.getDescription(), temp.getCode());
        }

        return ret;
    }

    public final SimpleDef[] getProcessTypesArray() {
        reset();
        if (processTypes == null) {

            try {
                processTypes = getWorkFlowService().retrieveProcessDefs();
            } catch (RemoteException re) {
                handleException(re);
            }
        }

        return processTypes;
    }

    public final LinkedHashMap<String, String> getActivityTypes() {
        getActivityTypesArray();

        LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();
        String name = "ALL Info Workflow Tasks";
        ret.put(name, "Facility Changes/Miscellaneous");
        for (ActivityTemplateDef temp : activityTypes) {
            name = temp.getActivityTemplateNm(); 
            if (name.equalsIgnoreCase("Facility Changes/Miscellaneous"))
                continue;
            if (!AbstractDAO.translateIndicatorToBoolean(temp.getDeprecatedInd()))
            	ret.put(name, name);
        }

        return ret;
    }

    public final LinkedHashMap<String, String> getPermittingActivityTypes() {
    	
    	Map<Integer,String> permitActivityTypes = null;
        try {
        	permitActivityTypes = getWorkFlowService().retrievePermittingActivityTypes();
        } catch (RemoteException e) {
            logger.error(e.getMessage());
        }

        LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();
        
        for (String name : permitActivityTypes.values()) {
        	ret.put(name, name);
        }
        return ret;
    }

    public final ActivityTemplateDef[] getActivityTypesArray() {
        reset();
        if (activityTypes == null) {

            try {
                activityTypes = getWorkFlowService().retrieveActivityTypes();
            } catch (RemoteException e) {
                logger.error(e.getMessage());
            }
        }

        return activityTypes;
    }

     public final LinkedHashMap<String, String> getPerformerTypes() {
        getPerformerTypesArray();

        LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();
        for (PerformerDef temp : performerTypes) {
            ret.put(temp.getDesc(), temp.getCode());
        }

        return ret;
    }

    public final PerformerDef[] getPerformerTypesArray() {
        reset();
        if (performerTypes == null) {

            try {
                performerTypes = getWorkFlowService().retrievePerformerDefs();
            } catch (RemoteException re) {
                handleException(re);
            }
        }

        return performerTypes;
    }

    public final void setWorkflows(LinkedHashMap<String, Integer> workflows) {
        this.workflows = workflows;
    }

    public final LinkedHashMap<String, Integer> getWorkflows() {
        if (workflows == null) {
            try {
                workflows = getWorkFlowService().retrieveWorkflowTempIdAndNm();
            } catch (RemoteException re) {
                logger.error(re.getMessage(), re);
                DisplayUtil.displayError("System error. Please contact system administrator");
            }
        }
        return workflows;
    }

    /**
     * @return the statusList
     */
    public final LinkedHashMap<String, String> getStatusList() {
        return statusList;
    }

    /**
     * @param statusList the statusList to set
     */
    public final void setStatusList(LinkedHashMap<String, String> statusList) {
        this.statusList = statusList;
    }
}
