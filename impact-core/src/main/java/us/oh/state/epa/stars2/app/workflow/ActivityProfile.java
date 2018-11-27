package us.oh.state.epa.stars2.app.workflow;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import oracle.adf.view.faces.component.UIXTable;
import oracle.adf.view.faces.component.core.layout.CorePanelForm;
import oracle.adf.view.faces.context.AdfFacesContext;
import oracle.adf.view.faces.event.ReturnEvent;
import oracle.adf.view.faces.model.RowKeySet;

import org.joda.time.DateTime;
import org.quartz.TriggerKey;

import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.EventLog;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityReferralType;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataField;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessNote;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Duration;
import us.oh.state.epa.stars2.scheduler.Stars2Scheduler;
import us.oh.state.epa.stars2.webcommon.AppValidationMsg;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.ConfirmWindow;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.component.BuildComponent;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

@SuppressWarnings("serial")
public class ActivityProfile extends ValidationBase {
    private static final String NOTE_DIALOG_OUTCOME = "dialog:newNote";
    private static final String CHECK_IN = "Complete";
    private static final String ACTIVITY_PROFILE = "activityProfile";
    private static final Integer postNtvInvoiceActivity = new Integer(126);
    private static final Integer postTvInvoiceActivity = new Integer(131);
//    private static final Integer printNtvInvoiceActivity = new Integer(127);
//    private static final Integer printTvInvoiceActivity = new Integer(132);
    private Timestamp changeStartDt;
    private Timestamp changeJeopardyDt;
    private Timestamp changeDueDt;
    private Timestamp changeEndDt;
    private Integer activityTemplateId;
    private String aggregate;
    private Integer processId;
    private ProcessActivity activity;
    private WorkFlowProcess process;
    private Integer loopCnt;
    private ProcessActivity[] aggregates;
    private transient CorePanelForm data;
    private boolean hasData;
    private String checkInNM;
    private boolean needCheckIn;
    private boolean needCheckOut;
    private boolean canSkip;
    private boolean detailAble;
    private transient UIXTable table;
    private boolean canReload = true;
    private LinkedHashMap<String, Integer> activityTemplates;
    private Integer gotoActivityTemplateId;
    private ProcessNote[] processNotes;
    private String note;
    private Date referDate;
    private ProcessNote tempNote;
    private ProcessNote modifyNote;
    private LinkedHashMap<Integer, ActivityReferralType> activityReferralTypes;
    private List<ValidationMessage> allMessages;

    private boolean fromExternal;
    private String externalName;
    private String externalNum;
    private TaskBase externalBean;
    private boolean newNote;
    private boolean noteReadOnly;
    
    private transient boolean validationFailed = false;
    
    private ReadWorkFlowService readWorkFlowService;
    private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public ReadWorkFlowService getReadWorkFlowService() {
		return readWorkFlowService;
	}

	public void setReadWorkFlowService(ReadWorkFlowService readWorkFlowService) {
		this.readWorkFlowService = readWorkFlowService;
	}

	public ActivityProfile() {
        super();
    }

    public final void setReferralType(Integer typeId) {
        if (activityReferralTypes == null) {
            activityReferralTypes = new LinkedHashMap<Integer, ActivityReferralType>();
            ActivityReferralType[] arts;
            try {
                arts = getReadWorkFlowService().retrieveActivityReferralTypes();
                for (ActivityReferralType art : arts)
                    activityReferralTypes.put(art.getActivityReferralTypeId(),
                            art);
            } catch (RemoteException e) {
                handleException(e);
            }
        }
        ActivityReferralType art = activityReferralTypes.get(typeId);
        getActivity().setExtendProcessEndDate(art.getExtendProcessEndDate());
        getActivity().setActivityReferralTypeId(art.getActivityReferralTypeId());
        Duration d = new Duration();
        int referDays = art.getDefaultDays();
        if (referDays  == 0)
            referDays = 365;
        d.setDays(referDays);
        try {
            referDate = d.addToDate(new Timestamp(System.currentTimeMillis()));
        } catch (Exception e) {
            DisplayUtil.displayError("Days error.");
        }
    }

    public final Integer getReferralType() {
        return getActivity().getActivityReferralTypeId();
    }

    public final String toExternal(){
        String ret = null;
        if (externalBean != null)
            try {
                ret = externalBean.toExternal();
            } catch (Exception e) {
                TaskBase.makeErrorMsg(getProcess(), "Error on external bean toExternal", e, logger);
            }
        return ret;
    }
    
    public final boolean isDoSelectedButton() {
        boolean ret = false;
        if (!ActivityStatusDef.IN_PROCESS.equalsIgnoreCase(getActivity().getActivityStatusCd()))
            ret = false;
        else if (externalBean != null){
            try{
                ret  = externalBean.isDoSelectedButton(getActivity());
            } catch (Exception e){
                TaskBase.makeErrorMsg(getProcess(), "Error on external bean isDoSelectedButton", e, logger);
            }
        } else
            ret = false;
                
        return ret;
    }

    public final String getDoSelectedButtonText() {
        String ret = "No Text";
        if (externalBean != null)
            try{
                ret = externalBean.getDoSelectedButtonText();
            } catch (Exception e){
                TaskBase.makeErrorMsg(getProcess(), "Error on external bean doSelectedButtonText", e, logger);
            }
        return ret;
    }
    
    public final String getDoSelectedConfirmMsg() {
        String ret = null;
        if (externalBean != null)
            try{
                ret  = externalBean.getDoSelectedConfirmMsg();
            } catch (Exception e){
                TaskBase.makeErrorMsg(getProcess(), "Error on external bean getDoSelectedConfirmMsg", e, logger);
            }
        return ret;
    }

    public final String getDoSelectedConfirmType(){
        String ret = (new ConfirmWindow()).getYesNo();
        if (externalBean != null)
            try{
                ret  = externalBean.getDoSelectedConfirmType();
            } catch (Exception e){
                TaskBase.makeErrorMsg(getProcess(), "Error on external bean getDoSelectedConfirmType", e, logger);
            }
        return ret;
    }
   
    public final void externalBeanDoSelected(ReturnEvent returnEvent) {

        try {
            externalBean.doSelected(table);
        } catch (Exception e) {
            TaskBase.makeErrorMsg(getProcess(), "Error on external bean doSelected", e, logger);
        }

        reloadActivity(ACTIVITY_PROFILE);
        return;
    }

    public final void selfReassignSelected(ReturnEvent returnEvent) {
        Iterator<?> it = table.getSelectionState().getKeySet().iterator();
        Object oldKey = table.getRowKey();

        while (it.hasNext()) {
            Object obj = it.next();
            table.setRowKey(obj);
            ProcessActivity pa = (ProcessActivity) table.getRowData();
            this.setProcessId(pa.getProcessId());
            this.setActivityTemplateId(pa.getActivityTemplateId());
            this.selfReassign(null);
        }
        aggregates = null;
        table.setRowKey(oldKey);
        table.setSelectionState(new RowKeySet());
    }
    public final void selfReassign(ReturnEvent returnEvent) {
        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");
        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.YES)) {
        	// decouple workflow
        	// send all activities through the wf manager; why is template 0
        	// special here?  can't tell, but using wfm and calling wf svc do not
        	// differ that much (as far as what they do) and we cannot call 
        	// wf svc from here now that we are uncoupled.
            if (null == activityTemplateId) {
            	activityTemplateId = getActivity().getActivityTemplateId();
            }
                WorkFlowManager wfm = new WorkFlowManager();
                WorkFlowResponse resp = wfm.reAssign(processId,
                        activityTemplateId, getUserId());

                if (resp.hasError() || resp.hasFailed()) {
                    handleWorkFlowResponse(resp);
                    return;
                }
//            } else {
//                try {
//                    getActivity().setUserId(getUserId());
//                    getWriteWorkFlowService().updateProcessActivity(getActivity());
//                } catch (RemoteException e) {
//                    handleException(e);
//                    return;
//                }
//            }

            writeSelfReassignNote();
            DisplayUtil.displayInfo(SUCCESS);
            reload();
        }
        return;
    }
    private void handleWorkFlowResponse(WorkFlowResponse resp) {
        for (String s : resp.getErrorMessages()){
            DisplayUtil.displayError(s);
            logger.warn(s);
        }
        for (String s : resp.getRecommendationMessages()){
            DisplayUtil.displayInfo(s);
            logger.debug(s);
        }
    }

    private void writeSelfReassignNote() {
        StringBuffer sb = new StringBuffer();
        sb.append("User Self Assign '");
        sb.append(getActivity().getActivityTemplateNm());
        sb.append("' ");
        writeNote(sb.toString());
    }
    private void writeNote(String noteString) {
//        // decouple workflow ... reimplement via wf manager
//    	// decouple workflow
//    	// consider a "create process note" wf action
//    	throw new RuntimeException(" decouple workflow; not implemented yet");
//        ProcessNote pn = new ProcessNote();
//        pn.setProcessId(processId);
//        pn.setUserId(getUserId());
//        pn.setNote(noteString);
//        try {
//            getWriteWorkFlowService().createProcessNote(pn);
//        } catch (RemoteException re) {
//            handleException(re);
//        }
//        String ret = ERROR;
        WorkFlowManager wfm = new WorkFlowManager();
        WorkFlowResponse resp = wfm.saveNote(processId, getUserId(),
                noteString);
        if (!resp.hasError() && !resp.hasFailed()) {
            DisplayUtil.displayInfo("Create process note success.");
        } else {
            handleWorkFlowResponse(resp);
        }
//        return ret;
    }
    
    public final String detail() {
        aggregate = "N";
        detailAble = true;
        return ACTIVITY_PROFILE;
    }
    public final String showAggregate() {
        aggregate = "Y";
        detailAble = true;
        return ACTIVITY_PROFILE;
    }
    public final String changeStartDate() {
    	// get activity and process dates in the joda datatime for validation
    	DateTime newStartDate = new DateTime(changeStartDt.getTime()).withTimeAtStartOfDay();
    	
    	DateTime activityJeopardyDate = new DateTime(getActivity().getJeopardyDt().getTime()).withTimeAtStartOfDay();
    	DateTime activityDueDate = new DateTime(getActivity().getDueDt().getTime()).withTimeAtStartOfDay();
    	
    	DateTime processStartDate = new DateTime(getProcess().getStartDt().getTime()).withTimeAtStartOfDay();
    	DateTime processJeopardyDate = new DateTime(getProcess().getJeopardyDt().getTime()).withTimeAtStartOfDay();
    	DateTime processDueDate = new DateTime(getProcess().getDueDt().getTime()).withTimeAtStartOfDay();
    	
        if (!(newStartDate.isAfter(processStartDate.getMillis())
        		|| newStartDate.isEqual(processStartDate.getMillis()))){
            DisplayUtil.displayError(
            		"Start date is not on or after process start date: " + 
            				processStartDate.toString("MM/dd/yyyy"));
            setChangeStartDt(getActivity().getStartDt());
            return ERROR;
        }
        if (!newStartDate.isBefore(activityDueDate.getMillis())){
        	DisplayUtil.displayError(
        			"Start date is not before activity due date: " + 
        					activityDueDate.toString("MM/dd/yyyy"));
        	DisplayUtil.displayError(
        			"Please change the activity due date before changing the activity start date.");
        	setChangeStartDt(getActivity().getStartDt());
        	return ERROR;
        }
        if (!newStartDate.isBefore(activityJeopardyDate.getMillis())){
        	DisplayUtil.displayError(
        			"Start date is not before activity jeopardy date: " + 
        					activityJeopardyDate.toString("MM/dd/yyyy"));
        	DisplayUtil.displayError(
        			"Please change the activity jeopardy date before changing the activity start date.");
        	setChangeStartDt(getActivity().getStartDt());
        	return ERROR;
        }
        if (!newStartDate.isBefore(processDueDate.getMillis())){
        	DisplayUtil.displayError(
        			"Start date is not before process due date: " + 
        					processDueDate.toString("MM/dd/yyyy"));
        	DisplayUtil.displayError(
        			"Please change the process due date before changing the activity start date.");
        	setChangeStartDt(getActivity().getStartDt());
        	return ERROR;
        }
        if (!newStartDate.isBefore(processJeopardyDate.getMillis())){
        	DisplayUtil.displayWarning(
        			"Start date is not before process jeopardy date: " + 
        					processJeopardyDate.toString("MM/dd/yyyy"));
        }

        String ret = ERROR;
        WorkFlowManager wfm = new WorkFlowManager();
        WorkFlowResponse resp = wfm.changeStartDate(processId,
                activityTemplateId, changeStartDt);
        if (!resp.hasError() && !resp.hasFailed()) {
            writeChangeStartDateNote();
            getActivity().setStartDt(changeStartDt);
            DisplayUtil.displayInfo("Change start date Success.");
            ret = reload();
        } else {
            handleWorkFlowResponse(resp);
        }
        return ret;
    }
    private void writeChangeStartDateNote() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        StringBuffer sb = new StringBuffer();
        sb.append("Change ");
        sb.append(getActivity().getActivityTemplateNm());
        sb.append(" Start date from ");
        sb.append(df.format(getActivity().getStartDt()));
        sb.append(" to ");
        sb.append(df.format(changeStartDt));
        sb.append(". ");
        writeNote(sb.toString());
    }
    public final String changeDueDate() {
    	// get activity and process dates in the joda datatime for validation
    	DateTime newDueDate = new DateTime(changeDueDt.getTime()).withTimeAtStartOfDay();
    	DateTime newjeopardyDate = new DateTime(changeJeopardyDt.getTime()).withTimeAtStartOfDay();
    	
    	DateTime activityStartDate = new DateTime(getActivity().getStartDt().getTime()).withTimeAtStartOfDay();
    	
    	DateTime processStartDate = new DateTime(getProcess().getStartDt().getTime()).withTimeAtStartOfDay();
    	DateTime processJeopardyDate = new DateTime(getProcess().getJeopardyDt().getTime()).withTimeAtStartOfDay();
    	DateTime processDueDate = new DateTime(getProcess().getDueDt().getTime()).withTimeAtStartOfDay();
    	
        /**** due date ****/
    	if(!newDueDate.isAfter(processStartDate.getMillis())) {
    		DisplayUtil.displayError(
            		"Due date is not after process start date: " + 
            				processStartDate.toString("MM/dd/yyyy"));
            setChangeDueDt(getActivity().getDueDt());
            return ERROR;
    	}
    	if (!newDueDate.isAfter(activityStartDate.getMillis())){
            DisplayUtil.displayError(
            		"Due date is not after activity start date " + 
            				activityStartDate.toString("MM/dd/yyyy"));
            setChangeDueDt(getActivity().getDueDt());
            return ERROR;
        }
    	if (!(newDueDate.isAfter(newjeopardyDate.getMillis()) ||
    			newDueDate.isEqual(newjeopardyDate.getMillis()))){
            DisplayUtil.displayError(
            		"Due date is not after or equal to activity jeopardy date " + 
            				newjeopardyDate.toString("MM/dd/yyyy"));
            setChangeDueDt(getActivity().getDueDt());
            return ERROR;
        }
    	if (!(newDueDate.isBefore(processDueDate.getMillis()) ||
    			newDueDate.isEqual(processDueDate.getMillis()))){
            DisplayUtil.displayError(
            		"Due date is not before or equal to process due date " + 
            				processDueDate.toString("MM/dd/yyyy"));
            setChangeDueDt(getActivity().getDueDt());
            return ERROR;
        }
        
        /**** jeopardy date ****/
    	if (!newjeopardyDate.isAfter(activityStartDate.getMillis())){
            DisplayUtil.displayError(
            		"Jeopardy date is not after activity start date: " +
            				activityStartDate.toString("MM/dd/yyyy"));
            setChangeJeopardyDt(getActivity().getJeopardyDt());
            return ERROR;
        }
        if (!(newjeopardyDate.isBefore(newDueDate.getMillis())
        		|| newjeopardyDate.isEqual(newDueDate.getMillis()))){
            DisplayUtil.displayError(
            		"Jeopardy date is not before or equal to activity due date: " +
            				newDueDate.toString("MM/dd/yyyy"));
            setChangeJeopardyDt(getActivity().getJeopardyDt());
            return ERROR;
        }
        if (!newjeopardyDate.isAfter(processStartDate.getMillis())){
            DisplayUtil.displayError(
            		"Jeopardy date is not after process start date: " +
            				processStartDate.toString("MM/dd/yyyy"));
            setChangeJeopardyDt(getActivity().getJeopardyDt());
            return ERROR;
        }
        if (!(newjeopardyDate.isBefore(processJeopardyDate.getMillis())
        		|| newjeopardyDate.isEqual(processJeopardyDate.getMillis()))){
            DisplayUtil.displayError(
            		"Jeopardy date is not before or equal to process jeopardy date: " +
            				processJeopardyDate.toString("MM/dd/yyyy"));
            setChangeJeopardyDt(getActivity().getJeopardyDt());
            return ERROR;
        }
        if (!(newjeopardyDate.isBefore(processDueDate.getMillis())
        		|| newjeopardyDate.isEqual(processDueDate.getMillis()))){
            DisplayUtil.displayError(
            		"Jeopardy date is not before or equal to process due date: " +
            				processDueDate.toString("MM/dd/yyyy"));
            setChangeJeopardyDt(getActivity().getJeopardyDt());
            return ERROR;
        }
        
        String ret = ERROR;
        WorkFlowManager wfm = new WorkFlowManager();
        WorkFlowResponse resp = wfm.changeDueDate(processId,
                activityTemplateId, changeJeopardyDt, changeDueDt);

        if (!resp.hasError() && !resp.hasFailed()) {
            writeChangeDueDateNote();
            DisplayUtil.displayInfo("Change due date Success.");
            ret = reload();
        } else {
            handleWorkFlowResponse(resp);
        }
        return ret;
    }
    private void writeChangeDueDateNote() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        StringBuffer sb = new StringBuffer();
        sb.append("Change ");
        sb.append(getActivity().getActivityTemplateNm());
        sb.append(" Jeopardy date from ");
        sb.append(df.format(getActivity().getJeopardyDt()));
        sb.append(" to ");
        sb.append(df.format(changeJeopardyDt));
        sb.append(" and Due date from ");
        sb.append(df.format(getActivity().getDueDt()));
        sb.append(" to ");
        sb.append(df.format(changeDueDt));
        sb.append(". ");
        writeNote(sb.toString());
    }
    public final String referActivity() {
//        // decouple workflow ... reimplement via wf manager
//    	// decouple workflow
//    	throw new RuntimeException(" decouple workflow; not implemented yet");
        String ret = "";
        if (activity == null){
            // for some reason activity is null.  Should not happen.
            ret = reloadActivity(ret);
            // if the activity is still null something wrong.
            if (activity == null)
                return ret;
        }
        if (referDate == null)
            setReferralType(getReferralType());
        getActivity().setDueDt(new Timestamp(referDate.getTime()));
        WorkFlowManager wfm = new WorkFlowManager();
        WorkFlowResponse resp = wfm.refer(processId, activityTemplateId,
                getUserId(), getActivity().getDueDt(), 
                getActivity().getActivityReferralTypeId(), 
                getActivity().getExtendProcessEndDate());

        if (resp.hasError() || resp.hasFailed()) {
            handleWorkFlowResponse(resp);
            return ERROR;
        } else {
        	//decouple workflow ... 
        	// get the milestone indicator
        	try {
	        	ActivityTemplate at = getReadWorkFlowService().retrieveActivityTemplate(
	                    activity.getActivityTemplateId());
	        	if (null != at && at.getMilestoneInd().equalsIgnoreCase("Y")) {
	                  makeEventLog();
		          } else {
		        	  if (null == at)
		        		  throw new RuntimeException("can't find activity template");
		          }
        	} catch (RemoteException re) {
        		TaskBase.makeErrorMsg(getProcess(), "Error creating event log", re, logger);
        	}
        	DisplayUtil.displayInfo("Referral success.");
        }

        loopCnt=null;
        ret = reloadActivity(ACTIVITY_PROFILE);

        return ret;
    }
    
    public final void endReferral(ActionEvent actionEvent) {
        Timestamp today = new Timestamp(System.currentTimeMillis());
        if (changeEndDt.after(today)){
            DisplayUtil.displayError("End date cannot be after today.");
            return;
        }
        if (changeEndDt.before(getActivity().getStartDt())){
            DisplayUtil.displayError("End date cannot be before start date.");
            return;
        }
        
        String tn = Stars2Scheduler.buildTriggerName(processId,
                activityTemplateId, getActivity().getLoopCnt());
        WorkFlowManager wfm = new WorkFlowManager();
        WorkFlowResponse unReferResp = wfm.unRefer(processId, activityTemplateId,
                getActivity().getLoopCnt(), getUserId(), changeEndDt);

        if (!unReferResp.hasError() && !unReferResp.hasFailed()) {
        	try {
	        	ActivityTemplate at = getReadWorkFlowService().retrieveActivityTemplate(
	                    activity.getActivityTemplateId());
	        	if (null != at && at.getMilestoneInd().equalsIgnoreCase("Y")) {
	              try {
	                  makeEventLog();
	              } catch (RemoteException re) {
	                  TaskBase.makeErrorMsg(getProcess(), "Error creating event log", re, logger);
	              }
	          } else {
	        	  if (null == at)
	        		  throw new RuntimeException("can't find activity template");
	          }
            } catch (RemoteException re) {
                DisplayUtil.displayError("The system successfully ended the referral of this task but encountered an error when setting the end of referral date you selected. ");
                handleException(re);
            }
            try {
                Stars2Scheduler scheduler = (Stars2Scheduler) CompMgr
                        .newInstance("app.Scheduler");

                scheduler.getScheduler().unscheduleJob(TriggerKey.triggerKey(tn,
                        Stars2Scheduler.recurringJobName));
            } catch (Exception e) {
                logger.error("Stars2Scheduler Exception on " 
                        + Stars2Scheduler.recurringJobName 
                        + " trigger name is " + tn, e);
                DisplayUtil.displayError("A Scheduler error has occurred. Please contact System Administrator.");
            }
            
            reloadActivity(ACTIVITY_PROFILE);
            setActivityTemplateId(activity.getActivityTemplateId());
        	DisplayUtil.displayInfo("End referral success.");
        } else {
            handleWorkFlowResponse(unReferResp);
        }
        
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public final String gotoSubmit() {
        String ret = ERROR;
//        // decouple workflow ... reimplement via wf manager
//    	// decouple workflow
//    	throw new RuntimeException(" decouple workflow; not implemented yet");
        WorkFlowManager wfm = new WorkFlowManager();
        WorkFlowResponse redoResp = wfm.redo(processId, gotoActivityTemplateId);

        if (!redoResp.hasError() && !redoResp.hasFailed()) {
//            ProcessNote pn = new ProcessNote();
//            pn.setProcessId(processId);
//            pn.setUserId(getUserId());
//            pn.setNote("Activity looped back to " + getGotoActivityName() + ". "
//                    + note);
            // decouple workflow
//            try {
//                getWriteWorkFlowService().createProcessNote(pn);
//            } catch (RemoteException re) {
//                handleException(re);
//            }

        	WorkFlowResponse saveNoteResp = wfm.saveNote(processId, getUserId(),
            		"Activity looped back to " + getGotoActivityName() + ". "
                    + note);
            if (!saveNoteResp.hasError() && !saveNoteResp.hasFailed()) {
            	DisplayUtil.displayInfo("Activity loop back success.");
            } else {
            	handleWorkFlowResponse(saveNoteResp);            	
            }
            

            note = "";
            ret = reloadActivity(ACTIVITY_PROFILE);
        } else {
            handleWorkFlowResponse(redoResp);
        }
        return ret;
    }

    private String getGotoActivityName() {
        String ret = "Activity not found";
        if (activityTemplates != null){
            Set<String> keys = activityTemplates.keySet();
            for (String k : keys) {
                if (activityTemplates.get(k).equals(gotoActivityTemplateId)) {
                    ret = k;
                    break;
                }
            }
        }
        return ret;
    }
    
    public String checkIn() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return checkInInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private final String checkInInternal() {
    	boolean errorMsgAdded = false;
        String re;
        allMessages = new ArrayList<ValidationMessage>();
        if (checkInNM.equalsIgnoreCase(CHECK_IN)) {
            setLoopCnt(getActivity().getLoopCnt());
            
            Integer tpid = getProcessId();
            Integer taid = getActivityTemplateId();
            Integer loopc = getActivity().getLoopCnt();
            
            re = workFlowCheckIn(processId, getActivity().getActivityTemplateId(),
                    getUserId(), getActivity().getActivityTemplateNm());
            if (validationFailed) {
            	errorMsgAdded = true;
                setProcessId(tpid);
                setLoopCnt(loopc);
                setActivityTemplateId(taid);
            }
            

            re = reloadActivity(re);
        } else {
            re = taskDismiss(processId, getUserId(), getActivity()
                    .getActivityTemplateNm());

            aggregates = null;
            getAggregates();
            if (aggregates != null && aggregates.length > 0){
                ProcessActivity ta = aggregates[0];
                setProcessId(ta.getProcessId());
                setActivityTemplateId(ta.getActivityTemplateId());
                re = reloadActivity(re);
            }else{
                needCheckIn = false;
                canSkip = false;
                canReload = false;
                getActivity().setActivityStatusCd("CM");
            }
        }
        if (allMessages.size() != 0)
            AppValidationMsg.validate(allMessages, true, errorMsgAdded);
        return re;
    }

    private String reloadActivity(String ret) {
        setActivity(null);
        getActivity();
        if (activity == null)
            ret = goTodoSearch();
        return ret;
    }

    private String goTodoSearch() {
        DisplayUtil.displayError("Cannot retrieve task :" + processId + "-" + activityTemplateId + "-" + loopCnt);
        ToDoSearch ts = (ToDoSearch)FacesUtil.getManagedBean("toDoSearch");
        return ts.reset();
    }

    private String taskDismiss(Integer inProcessId, Integer inUserId,
            String name) {
        String ret = ERROR;
        // decouple workflow ... reimplement via wf manager
    	// decouple workflow
//    	throw new RuntimeException("decouple workflow; not implemented yet");
        try {
        	
//            getWriteWorkFlowService().removeProcessFlows(inProcessId, inUserId);
            
            WorkFlowManager wfm = new WorkFlowManager();
            WorkFlowResponse removeProcessFlowsResp = wfm.removeProcessFlows(inProcessId, inUserId);
            if (!removeProcessFlowsResp.hasError() && !removeProcessFlowsResp.hasFailed()) {
            	DisplayUtil.displayInfo("Success dismiss todo '" + name + "'");
            } else {
            	handleWorkFlowResponse(removeProcessFlowsResp);            	
            }
            
            getActivity().setActivityStatusCd("CM");
            ret = ACTIVITY_PROFILE;
        } catch (NullPointerException e) {
            e.printStackTrace();
            DisplayUtil.displayError("Todo '" + name + " not found.");
        }
        return ret;
    }
    private String workFlowCheckIn(Integer inProcessId,
            Integer inActivityTemplateId, Integer inUserId,
            String inActivityName) {
        String ret = ERROR;
        setProcessId(inProcessId);
        try {
            externalBean = TaskBase.setUp(getProcess(), inActivityTemplateId,
                    getProcess().getExternalId());
        } catch (Exception e) {
            TaskBase.makeErrorMsg(getProcess(), "Error on external bean setUp", e, logger);
        }
        if (validate(inActivityTemplateId)) {
            LinkedHashMap<String, String> tdata = BuildComponent
                    .getDataToHashMap(data);
            if (tdata.get("missingRequiredValue").equals("Y")){
                DisplayUtil.displayError("Required value is missing.  Check in terminated.");
                return ret;
            }
            
            WorkFlowManager wfm = new WorkFlowManager();
            WorkFlowResponse resp = wfm.checkIn(inProcessId,
                    inActivityTemplateId, inUserId, tdata);
            if (!resp.hasError() && !resp.hasFailed()) {
            	DisplayUtil.displayInfo(CHECK_IN + " task '" + inActivityName
            			+ "' Successful");
            	ret = "s";

            	//decouple workflow ... 
            	//TODO get the milestone indicator
            	try {
		        	ActivityTemplate at = getReadWorkFlowService().retrieveActivityTemplate(
		                    activity.getActivityTemplateId());
		        	if (null != at && at.getMilestoneInd().equalsIgnoreCase("Y")) {
		                  makeEventLog();
			          } else {
			        	  if (null == at)
			        		  throw new RuntimeException("can't find activity template");
			          }
	        	} catch (RemoteException re) {
	        		TaskBase.makeErrorMsg(getProcess(), "Error creating event log", re, logger);
	        	}
            } else {
                handleWorkFlowResponse(resp);
            }
        }

        return ret;
    }

	private void makeEventLog() throws DAOException, RemoteException {
		EventLog el = new EventLog();
		StringBuffer note = new StringBuffer();
		note.append("Task ");
		note.append(activity.getActivityTemplateNm());
		note.append(" in ");
		note.append(activity.getProcessTemplateNm());
		note.append(" ");
		note.append(activity.getProcessId());
		note.append(" complete.");

		FacilityService facilityBO = getFacilityService();
		el.setFpId(activity.getFpId());
		el.setUserId(activity.getUserId());
		el.setDate(new Timestamp(System.currentTimeMillis()));
		el.setEventTypeDefCd(activity.getProcessCd());
		el.setExternalId(activity.getExternalId());
		el.setNote(note.toString());

		facilityBO.createEventLog(el);
	}
    
    private boolean validate(Integer inActivityTemplateId) {
        boolean ret = false;
        boolean operationOk = true;
        List<ValidationMessage> messages = null;

        if (externalBean != null) {
            try {
                messages = externalBean.validate(inActivityTemplateId);
            }catch (Exception e){
            	operationOk = false;
                TaskBase.makeErrorMsg(getProcess(), "Error occurred during validation", e, logger);
            }
        }

		if (operationOk) {
			if (messages == null || (messages != null && messages.isEmpty())) {
				ret = true;
				Object close_validation_dialog = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
            	if (close_validation_dialog != null) {
            		FacesUtil.startAndCloseModelessDialog(AppValidationMsg.VALIDATION_RESULTS_URL);
            		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
            	}
			} else if (aggregate != null && aggregate.equalsIgnoreCase("Y")) {
				allMessages.addAll(messages);
				ret = AppValidationMsg.validate(messages, false);
			} else {
				ret = AppValidationMsg.validate(messages, true);
			}
		} else {
			ret = false;
		}
		
		validationFailed = (!ret) ? true : false;

        return ret;
    }

    @Override
    public final String validationDlgAction() {
        String ret = super.validationDlgAction();
        
        if (externalBean != null) {
            super.setValidationDlgReference(ret);
            try{
                ret = externalBean.validationDlgAction();
            } catch (Exception e) {
                TaskBase.makeErrorMsg(getProcess(), "Error on external bean validationDlgAction", e, logger);
            }
        }
        return ret;
    }

    public String checkInSelected() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return checkInSelectedInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private final String checkInSelectedInternal() {
        Iterator<?> it = table.getSelectionState().getKeySet().iterator();
        Object oldKey = table.getRowKey();
        String re = "";
        allMessages = new ArrayList<ValidationMessage>();
        boolean errorMsgAdded = false;
        
        if (checkInNM.equalsIgnoreCase(CHECK_IN)) {
            Integer tpid = getProcessId();
            Integer taid = getActivityTemplateId();
            Integer loopc = getActivity().getLoopCnt();
            while (it.hasNext()) {
                Object obj = it.next();
                table.setRowKey(obj);
                ProcessActivity pa = (ProcessActivity) table.getRowData();

                if (pa != null)
                    re = workFlowCheckIn(pa.getProcessId(), pa
                        .getActivityTemplateId(), getUserId(), pa
                        .getActivityTemplateNm());
                if (validationFailed) {
                	errorMsgAdded = true;
                }
            }
            setProcessId(tpid);
            setLoopCnt(loopc);
            setActivityTemplateId(taid);
            reload();
        } else {
            String st = getActivity().getActivityStatusCd();
            while (it.hasNext()) {
                Object obj = it.next();
                table.setRowKey(obj);
                ProcessActivity pa = (ProcessActivity) table.getRowData();

                if (pa != null) {
                    re = taskDismiss(pa.getProcessId(), getUserId(), pa
                            .getActivityTemplateNm());
                    if (pa.getProcessId().equals(processId)) {
                        st = "CM";
                    }
                }
            }
            aggregates = null;
            getAggregates();
            if (aggregates != null && aggregates.length > 0){
                ProcessActivity ta = aggregates[0];
                setProcessId(ta.getProcessId());
                setActivityTemplateId(ta.getActivityTemplateId());
                re = reloadActivity(re);
            }else{
                if (st.equals("CM")){
                    needCheckIn = false;
                    canSkip = false;
                    canReload = false;
                    getActivity().setActivityStatusCd(st);
                }
            }
        }

        if (allMessages.size() != 0)
            AppValidationMsg.validate(allMessages, true, errorMsgAdded);

        table.setRowKey(oldKey);
        table.setSelectionState(new RowKeySet());
        return re;
    }

    public final void setActivityTemplateId(Integer activityTemplateId) {
        aggregates = null;
        activityTemplates = null;
        data = null;
        this.activityTemplateId = activityTemplateId;
        SimpleMenuItem.setDisabled("menuItem_activityProfile", false);
        setActivity(null);
        validationFailed = false;
        //reloadActivity(SUCCESS);
    }

    public final ProcessActivity[] getAggregates() {
        if (aggregates == null) {
            aggregates = new ProcessActivity[0];
            try {
                if (getActivity() != null) {
                    ProcessActivity pa = new ProcessActivity();
                    pa.setUserId(getActivity().getUserId());
                    pa.setActivityTemplateId(getActivity().getActivityTemplateId());
                    pa.setActivityStatusCd(getActivity().getActivityStatusCd());
                    pa.setProcessTemplateNm(getActivity().getProcessTemplateNm());
                    pa.setAggregate(getActivity().getAggregate());
                    if (getActivity().getEndDt() != null)
                        pa.setEndDt(getActivity().getEndDt());
                    pa.setUnlimitedResults(unlimitedResults());

                    aggregates = getReadWorkFlowService().retrieveActivityList(pa);
                    DisplayUtil.displayHitLimit(aggregates.length);
                }
            } catch (RemoteException re) {
                handleException(re);
            }
        }
        return aggregates;
    }

    public final void setAggregates(ProcessActivity[] aggregates) {
        this.aggregates = aggregates;
    }

    public final Integer getProcessId() {
        return processId;
    }

    public final void setProcessId(Integer processId) {
        process = null;
        loopCnt = null;
        processNotes = null;
        this.processId = processId;
    }

    public final ProcessActivity getActivity() {
        if (activity == null) {
            try {
                externalBean = null;
                referDate = null;
                ProcessActivity pa = new ProcessActivity();
                pa.setProcessId(processId);
                if (loopCnt == null) {
                    pa.setCurrent("Y");
                } else {
                    pa.setLoopCnt(loopCnt);
                }
                pa.setActivityTemplateId(activityTemplateId);

                //
                // Once a user has looked at an activity marked this activity
                // as viewed.
                //
                activity = getReadWorkFlowService().retrieveActivity(pa);
                if (activity == null){
                    logger.error("User is loading activity with ids not in database : "
                            + pa.toIds() , new Exception());
                } else {
                    if (activity.getProcessEndDt() == null && 
                    		activity.getUserId().equals(getUserId())) {
                        WorkFlowManager wfm = new WorkFlowManager();
                        WorkFlowResponse resp = 
                        		wfm.updateActivityViewedState(activity.getProcessId(),
                        				activity.getActivityTemplateId());
                        if (resp.hasError() || resp.hasFailed()) {
                            handleWorkFlowResponse(resp);
                            return null;
                        }
                    }

                    if (activity.getActivityTemplateId() == null){
                        logger.error("Activity Template Id is null, this should not happen.", new Exception());
                    }
                    setUpButtons();
                    buildData();
                    changeStartDt = activity.getStartDt();
                    changeDueDt = activity.getDueDt();
                    changeJeopardyDt = activity.getJeopardyDt();
                    changeEndDt = new Timestamp(System.currentTimeMillis());
                    processNotes = null;
                    getAggregates();
                    if (activity.getAggregate().equals("N") || aggregates == null || aggregates.length == 0) {
                        detailAble = false;
                    } else {
                        detailAble = true;
                    }

                    if (activity.getActivityReferralTypeId() != null)
                        setReferralType(activity.getActivityReferralTypeId());
                }
                
            } catch (RemoteException re) {
                handleException(re);
            }
        }
        return activity;
    }

    @SuppressWarnings("unchecked")
    private void buildData() {
        BuildComponent.cleanUp(data);

        data = new CorePanelForm();
        List<Object> children = data.getChildren();
        children.clear();

        DataField[] tdata;
        try {
            tdata = getReadWorkFlowService().retrieveDataFieldsForProcess(processId);

            for (DataField d : tdata) {
                BuildComponent bc = new BuildComponent();
                boolean sameActivity = d.getActivityTemplateId() != null
                        && d.getActivityTemplateId().equals(activityTemplateId);
                boolean sameUser = getUserId().equals(activity.getUserId());
                boolean readOnly = !needCheckIn || !sameActivity || !sameUser;
                if (!readOnly && sameActivity && d.isRequired() && d.getDataTypeId().equals(5))
                    d.setDataValue(null);
                DataDetail dd = d.getDataDetail();
                dd.setDataDetailVal(d.getDataValue());
                bc.setDataDetail(dd);
                bc.setReadOnly(readOnly);
                if (validationFailed) {
                	bc.setRequired(false);
                } else {
                	bc.setRequired(sameActivity);
                }
                bc.setUnselectedLabel("UnSure");

                children.add(bc.byDataTypeId());
            }
        } catch (RemoteException re) {
            handleException(re);
        }
    }

    public final void noteDialogDone(ReturnEvent returnEvent) {
        tempNote = null;
        processNotes = null;
    }

    public final String startAddNote() {
        tempNote = new ProcessNote();
        tempNote.setUserId(getUserId());
        tempNote.setPostedDt(new Timestamp(System.currentTimeMillis()));
        newNote = true;

        return NOTE_DIALOG_OUTCOME;
    }

    public final String startViewNote() {
        newNote = false;
        tempNote = new ProcessNote(modifyNote);
        if (tempNote.getUserId().equals(getUserId()))
            noteReadOnly = false;
        else
            noteReadOnly = true;

        return NOTE_DIALOG_OUTCOME;
    }

    public final void saveComment(ActionEvent actionEvent) {
//        // decouple workflow ... reimplement via wf manager
//    	// decouple workflow
//    	throw new RuntimeException(" decouple workflow; not implemented yet");
    	
      List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
 		
   	  // make sure note is provided
      if (tempNote.getNote() == null || tempNote.getNote().trim().equals("")) {
         	  validationMessages.add(new ValidationMessage("noteTxt", "Attribute " + "Note" + " is not set.",
         			  ValidationMessage.Severity.ERROR, "noteTxt"));
   	  }
           
      if (validationMessages.size() > 0) {
   		displayValidationMessages("", validationMessages.toArray(new ValidationMessage[0]));
   	  } else {    	
        if (newNote) {
        	if(tempNote.getNote() == null) {
        		tempNote.setNote("");
        	}
            writeNote(tempNote.getNote());
        }
        else {
            modifyNote();
        }
        
        tempNote = null;
        processNotes = null;
        setProcessNotes(getProcessNotes());
        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
   	  }
    }
	
	private void modifyNote() {
//		try {
//		    getWriteWorkFlowService().modifyProcessNote(tempNote);
//		} catch (RemoteException re) {
//		    handleException(re);
//		}
        WorkFlowManager wfm = new WorkFlowManager();
        WorkFlowResponse resp = wfm.modifyNote(processId, getUserId(),
        		tempNote.getNoteId(),tempNote.getLastModified(),
        		tempNote.getNote());
        if (!resp.hasError() && !resp.hasFailed()) {
            DisplayUtil.displayInfo("Modify process note Success.");
        } else {
            handleWorkFlowResponse(resp);
        }
	}

    private void setUpButtons() {
        boolean disabled = false;
        if (activity.getActivityStatusCd().equalsIgnoreCase(ActivityStatusDef.IN_PROCESS)) {
            needCheckIn = true;
            if (getActivityTemplates().size() == 0)
                disabled = true;
            else
                disabled = false;
        } else {
            needCheckIn = false;
            disabled = true;
        }
        SimpleMenuItem.setDisabled("menuItem_gotoActivity", disabled);

        boolean reassignDisabled = false;
        disabled = false;
        if (activity.getActivityStatusCd().equalsIgnoreCase(ActivityStatusDef.CANCELLED)
                || activity.getActivityStatusCd().equalsIgnoreCase(ActivityStatusDef.COMPLETED)
                || activity.getActivityStatusCd().equalsIgnoreCase(ActivityStatusDef.UNREFERRED)) {
            disabled = true;
            reassignDisabled = true;
        }
        
        // disable change start/due date and referral buttons for not completed tasks
        if (activity.getActivityStatusCd().equalsIgnoreCase(ActivityStatusDef.NOT_COMPLETED)) {
        	disabled = true;
        }
        
        if (activity.getActivityTemplateId() == 0) {
            checkInNM = "Done";  // 1849
            disabled = true;
            canSkip = false;
            SimpleMenuItem.setDisabled("menuItem_workflowProfile", true);
        } else {
            SimpleMenuItem.setDisabled("menuItem_workflowProfile", false);
            checkInNM = CHECK_IN;
            if (activity.getActivityStatusCd().equalsIgnoreCase(ActivityStatusDef.IN_PROCESS)) {
                canSkip = true;
            } else {
                canSkip = false;
            }

            if (activity.getActivityStatusCd().equalsIgnoreCase(ActivityStatusDef.PENDING)) {
                needCheckOut = true;
            } else {
                needCheckOut = false;
            }
            
            // disable skip button for issue final, compliance reports and stack tests
            // finalize activity (inspection)
            if(activity.getActivityTemplateId() == 1199 
            		|| activity.getActivityTemplateId() == 6 
            		|| activity.getActivityTemplateId() == 7
            		|| activity.getActivityTemplateId() == 8
            		|| activity.getActivityTemplateId() == 9
            		|| activity.getActivityTemplateId() == 11
            		|| activity.getActivityTemplateId() == 12
            		|| activity.getActivityTemplateId() == 300
            		|| activity.getActivityTemplateId() == 1304
            		)
            	canSkip = false;

            activityTemplates = null;
            WorkFlow2DDraw wf = (WorkFlow2DDraw) FacesUtil.getManagedBean("workFlow2DDraw");
            if (wf != null) {
                wf.setImage(null);
            }

            try {
                externalBean = TaskBase.setUp(getProcess(), activityTemplateId,
                        getProcess().getExternalId());
            } catch (Exception e) {
                TaskBase.makeErrorMsg(getProcess(), "Error on external bean setUp", e, logger);
            }
            disabled = (disabled || !activity.getUserId().equals(getUserId()));
        }

        if (externalBean != null)
            try {
                externalName = externalBean.getExternalName(activity);
                externalNum = externalBean.getExternalNum();
            } catch (Exception e) {
                logger.warn("Error on external bean validate: " + e, e);
                externalName = null;
                externalNum = null;
            }
        else {
            externalName = null;
            externalNum = null;
        }
        
        SimpleMenuItem.setDisabled("menuItem_reassignActivity", reassignDisabled);
        SimpleMenuItem.setDisabled("menuItem_changeActivityStartDate", disabled);
        SimpleMenuItem.setDisabled("menuItem_changeActivityDueDate", disabled);
        SimpleMenuItem.setDisabled("menuItem_referActivity", disabled);
    }
    
    public final void setActivity(ProcessActivity activity) {
        this.activity = activity;
    }

    public final WorkFlowProcess getProcess() {
        if (process == null) {
            try {
                process = getReadWorkFlowService().retrieveProcess(processId);
            } catch (RemoteException re) {
                handleException(re);
            }
        }
        return process;
    }

    public final void setProcess(WorkFlowProcess process) {
        this.process = process;
    }
    
    public String submitProfile() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return submitProfileInternal();
        } finally {
            clearButtonClicked();
        }
    }

    private final String submitProfileInternal() {
        String ret = "activityProfile";
        ret = reloadActivity(ret);

        if (externalBean != null && !fromExternal)
            try {
                String url = activity.getActivityUrl();
                ret = externalBean.findOutcome(url, ret);
            } catch (Exception e) {
                TaskBase.makeErrorMsg(getProcess(), "Error on external bean findOutcome", e, logger);
            }
        
        return ret;
    }
    
    public final String submitActProfile() {
        
        return reloadActivity("activityProfile");
    }
    
    public final String reload() {
        aggregates = null;
        return reloadActivity(ACTIVITY_PROFILE);
    }
    
    public String checkOut() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return checkOutInternal();
        } finally {
            clearButtonClicked();
        }
    }

    private final String checkOutInternal() {
        WorkFlowManager wfm = new WorkFlowManager();
        WorkFlowResponse resp = wfm.checkOut(processId, getActivity()
                .getActivityTemplateId(), getUserId());

        if (resp.hasError() || resp.hasFailed()) {
            handleWorkFlowResponse(resp);
        } else {
            DisplayUtil.displayInfo(SUCCESS);
        }
        activity = null;
        return ACTIVITY_PROFILE;
    }

    public final void skip(ReturnEvent returnEvent) {
        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");
        if (!cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
        	if (validationFailed) {
        		validationFailed = false;
        		buildData();
        		DisplayUtil.displayWarning("Skip was not performed due to a validation error when you performed completion of this step. If you really want to skip, please try again.");
        		return;
        	}
            LinkedHashMap<String, String> tdata = BuildComponent
                    .getDataToHashMap(data);

            WorkFlowManager wfm = new WorkFlowManager();
            WorkFlowResponse resp = wfm.skip(processId, getActivity()
                    .getActivityTemplateId(), getUserId(), tdata);

            if (resp.hasError() || resp.hasFailed()) {
                handleWorkFlowResponse(resp);
            } else {
                DisplayUtil.displayInfo("Skip success.");
            }
            reloadActivity(ACTIVITY_PROFILE);
        } else if (validationFailed) {
        	validationFailed = false;
        	buildData();
        }
        return;
    }

    public final String getCheckInNM() {
        return checkInNM;
    }

    public final void setCheckInNM(String checkInNM) {
        this.checkInNM = checkInNM;
    }

    public final boolean isCanReload() {
        return canReload;
    }

    public final void setCanReload(boolean canReload) {
        this.canReload = canReload;
    }

    public final boolean isFromExternal() {
        return fromExternal;
    }

    public final void setFromExternal(boolean fromExternal) {
        this.fromExternal = fromExternal;
    }

    public final UIXTable getTable() {
        return table;
    }

    public final void setTable(UIXTable table) {
        this.table = table;
    }

    public final boolean isCanSkip() {
        return canSkip;
    }

    public final void setCanSkip(boolean canSkip) {
        this.canSkip = canSkip;
    }

    public final boolean isNeedCheckOut() {
        return needCheckOut;
    }

    public final void setNeedCheckOut(boolean needCheckOut) {
        this.needCheckOut = needCheckOut;
    }

    public final boolean isNeedCheckIn() {
        return needCheckIn;
    }
    
    public final boolean isNeedAggCheckIn() {
        // Used to not display the complete button for emissions inventory invoice posting since
        // this should be done on the more detailed view.
        if(postNtvInvoiceActivity.equals(activityTemplateId) || postTvInvoiceActivity.equals(activityTemplateId)) {
            return false;
        } else {
            return needCheckIn;
        }
    }

    public final void setNeedCheckIn(boolean needCheckIn) {
        this.needCheckIn = needCheckIn;
    }

    public final boolean isHasData() {
        return hasData;
    }

    public final void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

    public final CorePanelForm getData() {
        if (data == null)
            reload();
        return data;
    }

    public final void setData(CorePanelForm data) {
        this.data = data;
    }

    public final Integer getActivityTemplateId() {
        return activityTemplateId;
    }

    public final String getAggregate() {
        return aggregate;
    }

    public final void setAggregate(String aggregate) {
        aggregates = null;
        table = null;
        this.aggregate = aggregate;
    }

    public final LinkedHashMap<String, Integer> getActivityTemplates() {
        if (activityTemplates == null) {
            try {
                ProcessActivity pa = new ProcessActivity();
                pa.setProcessId(processId);
                pa.setPerformerTypeCd("M");
                pa.setCurrent("Y");
                pa.setUnlimitedResults(unlimitedResults());

                ProcessActivity[] activities = getReadWorkFlowService()
                        .retrieveActivityList(pa);

                DisplayUtil.displayHitLimit(activities.length);
                LinkedHashMap<String, Integer> ret = new LinkedHashMap<String, Integer>();
                for (ProcessActivity temp : activities) {
                    if (temp.getActivityStatusCd().equalsIgnoreCase(ActivityStatusDef.COMPLETED)) {
                        ret.put(temp.getActivityTemplateNm(), temp
                                .getActivityTemplateId());
                    }
                }
                activityTemplates = ret;
            } catch (RemoteException re) {
                handleException(re);
            }
        }
        return activityTemplates;
    }

    public final void setActivityTemplates(
            LinkedHashMap<String, Integer> activityTemplates) {
        this.activityTemplates = activityTemplates;
    }

    public final Integer getGotoActivityTemplateId() {
        return gotoActivityTemplateId;
    }

    public final void setGotoActivityTemplateId(Integer gotoActivityTemplateId) {
        this.gotoActivityTemplateId = gotoActivityTemplateId;
    }

    public final Integer getLoopCnt() {
        return loopCnt;
    }

    public final void setLoopCnt(Integer loopCnt) {
        this.loopCnt = loopCnt;
    }
    public final boolean isDetailAble() {
        return detailAble;
    }
    public final void setDetailAble(boolean detailAble) {
        this.detailAble = detailAble;
    }
    public final ProcessNote[] getProcessNotes() {
        if (processNotes == null){
            try {
                ProcessNote pn = new ProcessNote();
                pn.setProcessId(processId);
                processNotes = getReadWorkFlowService().retrieveProcessNotes(pn);
            } catch (RemoteException re) {
                handleException(re);
            }
        }
        return processNotes;
    }
    public final void setProcessNotes(ProcessNote[] processNotes) {
        this.processNotes = processNotes;
    }
    public final String getNote() {
        return note;
    }
    public final void setNote(String note) {
        this.note = note;
    }
    public final Integer getUserId() {
        return InfrastructureDefs.getCurrentUserId();
    }
    public final Timestamp getChangeDueDt() {
        return getActivity().getDueDt();
    }
    public final void setChangeDueDt(Timestamp changeDueDt) {
        this.changeDueDt = changeDueDt;
    }
    public final Timestamp getChangeJeopardyDt() {
        return getActivity().getJeopardyDt();
    }
    public final void setChangeJeopardyDt(Timestamp changeJeopardyDt) {
        this.changeJeopardyDt = changeJeopardyDt;
    }
    public final ProcessNote getTempNote() {
        return tempNote;
    }
    public final void setTempNote(ProcessNote tempNote) {
        this.tempNote = tempNote;
    }
    public final ProcessNote getModifyNote() {
        return modifyNote;
    }
    public final void setModifyNote(ProcessNote modifyNote) {
        this.modifyNote = modifyNote;
    }
    public final void closeDialog(ActionEvent actionEvent) {
    	tempNote = null;
    	noteReadOnly = false;
        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }
    public final String getExternalName() {
        return externalName;
    }
    public final void setExternalName(String externalName) {
        this.externalName = externalName;
    }
    
    public final Timestamp getChangeStartDt() {
        return changeStartDt;
    }

    public final void setChangeStartDt(Timestamp changeStartDt) {
        this.changeStartDt = changeStartDt;
    }

    public final String getExternalNum() {
        return externalNum;
    }

    public final void setExternalNum(String externalNum) {
        this.externalNum = externalNum;
    }

    public final Date getReferDate() {
        if (referDate == null)
            referDate = new Date();
        return referDate;
    }

    public final void setReferDate(Date referDate) {
        this.referDate = referDate;
    }

    public final boolean isNoteReadOnly() {
    	if(isReadOnlyUser()){
    		return true;
    	}
    	
        return noteReadOnly;
    }

    public final void setNoteReadOnly(boolean noteReadOnly) {
        this.noteReadOnly = noteReadOnly;
    }

    public final LinkedHashMap<Integer, ActivityReferralType> getActivityReferralTypes() {
        return activityReferralTypes;
    }

    public final void setActivityReferralTypes(
            LinkedHashMap<Integer, ActivityReferralType> activityReferralTypes) {
        this.activityReferralTypes = activityReferralTypes;
    }

    public final List<ValidationMessage> getAllMessages() {
        return allMessages;
    }

    public final void setAllMessages(List<ValidationMessage> allMessages) {
        this.allMessages = allMessages;
    }

    public final TaskBase getExternalBean() {
        return externalBean;
    }

    public final void setExternalBean(TaskBase externalBean) {
        this.externalBean = externalBean;
    }

    public final boolean isNewNote() {
        return newNote;
    }

    public final void setNewNote(boolean newNote) {
        this.newNote = newNote;
    }

    public final void setUserId(Integer userId) {
        
    }

    /**
     * @return the changeEndDt
     */
    public final Timestamp getChangeEndDt() {
        return changeEndDt;
    }

    /**
     * @param changeEndDt the changeEndDt to set
     */
    public final void setChangeEndDt(Timestamp changeEndDt) {
        this.changeEndDt = changeEndDt;
    }

	public final boolean isValidationFailed() {
		return validationFailed;
	}

	public final void setValidationFailed(boolean validationFailed) {
		this.validationFailed = validationFailed;
	}
}
