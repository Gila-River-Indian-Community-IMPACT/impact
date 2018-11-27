package us.oh.state.epa.stars2.app.workflow;

import java.rmi.RemoteException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

@SuppressWarnings("serial")
public class ReassignActivities extends AppBase {
    private Integer processId;
    private ProcessActivity[] activities;
    private Integer activityTemplateId;
    private String command;
//    private WriteWorkFlowService writeWorkFlowService;
    private ReadWorkFlowService readWorkFlowService;

//	public WriteWorkFlowService getWriteWorkFlowService() {
//		return writeWorkFlowService;
//	}
//
//	public void setWriteWorkFlowService(WriteWorkFlowService writeWorkFlowService) {
//		this.writeWorkFlowService = writeWorkFlowService;
//	}

	public ReadWorkFlowService getReadWorkFlowService() {
		return readWorkFlowService;
	}

	public void setReadWorkFlowService(ReadWorkFlowService readWorkFlowService) {
		this.readWorkFlowService = readWorkFlowService;
	}

	public ReassignActivities() {
        super();
    }
    
    public final String apply() {
        WorkFlowManager wfm = new WorkFlowManager();
        Integer currentUserId = InfrastructureDefs.getCurrentUserId();
        
        for (ProcessActivity tpa : activities) {
            if (tpa.isSelected()) {
//                if (tpa.getActivityTemplateId() != 0) {
                    WorkFlowResponse resp = wfm.reAssign(tpa.getProcessId(), 
                            tpa.getActivityTemplateId(), tpa.getUserId());

                    if (resp.hasError() || resp.hasFailed()) {
                        for (String s : resp.getErrorMessages()){
                            DisplayUtil.displayError(s);
                            logger.warn(s);
                        }
                        for (String s : resp.getRecommendationMessages()){
                            DisplayUtil.displayInfo(s);
                            logger.debug(s);
                        }
                        return ERROR;
                    }
//                } else {
    	// decouple workflow
//        throw new RuntimeException("decouple workflow; method not implemented");
//                    try {
//                        getWriteWorkFlowService().updateProcessActivity(tpa);
//                    } catch (RemoteException re) {
//                        handleException(re);
//                        return ERROR;
//                    }
//                }

                writeNote(tpa, currentUserId);
            }
        }

        DisplayUtil.displayInfo("Reassign task success");

        ActivityProfile ap = (ActivityProfile) FacesUtil
                .getManagedBean("activityProfile");
        ap.reload();
        return null;
    }
    

    public final String applyWithoutRefresh() {
        WorkFlowManager wfm = new WorkFlowManager();
        Integer currentUserId = InfrastructureDefs.getCurrentUserId();
        
        for (ProcessActivity tpa : activities) {
            if (tpa.isSelected()) {
//                if (tpa.getActivityTemplateId() != 0) {
                    WorkFlowResponse resp = wfm.reAssign(tpa.getProcessId(), 
                            tpa.getActivityTemplateId(), tpa.getUserId());

                    if (resp.hasError() || resp.hasFailed()) {
                        for (String s : resp.getErrorMessages()){
                            DisplayUtil.displayError(s);
                            logger.warn(s);
                        }
                        for (String s : resp.getRecommendationMessages()){
                            DisplayUtil.displayInfo(s);
                            logger.debug(s);
                        }
                        return ERROR;
                    }
//                } else {
//                	// decouple workflow
//                    throw new RuntimeException("decouple workflow; method not implemented");
//                    try {
//                    	getWriteWorkFlowService().updateProcessActivity(tpa);
//                    } catch (RemoteException re) {
//                        handleException(re);
//                        return ERROR;
//                    }
//                }

                writeNote(tpa, currentUserId);
            }
        }
        return SUCCESS;
    }

    private void writeNote(ProcessActivity tpa, Integer currentUserId) {
//    	// decouple workflow
//        throw new RuntimeException("decouple workflow; method not implemented");
        String oldUserName = BasicUsersDef.getUserNm(tpa.getOldUserId());
        String newUserName = BasicUsersDef.getUserNm(tpa.getUserId());
        StringBuffer sb = new StringBuffer();
        sb.append("Reassign '");
        sb.append(tpa.getActivityTemplateNm());
        sb.append("' from user ");
        sb.append(oldUserName);
        sb.append(" to ");
        sb.append(newUserName);

//        ProcessNote pn = new ProcessNote();
//        pn.setProcessId(tpa.getProcessId());
//        pn.setUserId(currentUserId);
//        pn.setNote(sb.toString());
        
        WorkFlowManager wfm = new WorkFlowManager();
        wfm.saveNote(tpa.getProcessId(), currentUserId, sb.toString());
//        try {
//        	getWriteWorkFlowService().createProcessNote(pn);
//        } catch (RemoteException re) {
//            handleException(re);
//            DisplayUtil.displayError("Cannot create process note.");
//        }
    }

    public final ProcessActivity[] getActivities() {
        ProcessActivity[] ret = activities;
        if (activities == null) {
            if (processId == null) {
                ret = null;
            } else {
                try {
                    ProcessActivity pa = new ProcessActivity();
                    pa.setProcessId(processId);
                    ArrayList<String> statusCds = new ArrayList<String>();
                    statusCds.add("IP");
                    statusCds.add("PD");
                    statusCds.add("ND");
                    pa.setActivityStatusCds(statusCds);
                    pa.setInStatus(true);
                    pa.setPerformerTypeCd("M");
                    pa.setUnlimitedResults(unlimitedResults());

                    activities = getReadWorkFlowService().retrieveActivityList(pa);
                    DisplayUtil.displayHitLimit(activities.length);
                    ret = activities;
                } catch (RemoteException re) {
                    handleException(re);
                    DisplayUtil.displayError("Cannot retrive tasks list.");
                }
            }
        }
        return ret;
    }

    public final void setActivities(ProcessActivity[] activities) {
        this.activities = activities;
    }

    public final Integer getProcessId() {
        return processId;
    }

    public final void setProcessId(Integer processId) {
        activities = null;
        this.processId = processId;
    }

    public final Integer getActivityTemplateId() {
        return activityTemplateId;
    }

    public final void setActivityTemplateId(Integer activityTemplateId,
            Integer loopCnt) {
        this.activityTemplateId = activityTemplateId;

        try {
            ProcessActivity pa = new ProcessActivity();
            pa.setProcessId(processId);
            pa.setActivityTemplateId(activityTemplateId);
            pa.setLoopCnt(loopCnt);
            pa.setPerformerTypeCd("M");
            pa.setUnlimitedResults(unlimitedResults());

            activities = getReadWorkFlowService().retrieveActivityList(pa);
            DisplayUtil.displayHitLimit(activities.length);
        } catch (RemoteException re) {
            handleException(re);
            DisplayUtil.displayError("Cannot retrieve task list.");
        }
    }

    public final String getCommand() {
        return command;
    }

    public final void setCommand(String command) {
        this.command = command;
        if (command.equalsIgnoreCase("setActivityTemplateId")) {
            ActivityProfile ap = (ActivityProfile) FacesUtil
                    .getManagedBean("activityProfile");
            setProcessId(ap.getProcessId());
            setActivityTemplateId(ap.getActivityTemplateId(), ap.getActivity()
                    .getLoopCnt());
        } else if (command.equalsIgnoreCase("setProcessId")) {
            WorkFlow2DDraw wf = (WorkFlow2DDraw) FacesUtil
                    .getManagedBean("workFlow2DDraw");
            setProcessId(wf.getProcessId());
        }
    }
}
