package us.oh.state.epa.stars2.app;

import java.util.List;

import org.apache.log4j.Logger;

import oracle.adf.view.faces.component.UIXTable;
import us.oh.state.epa.stars2.app.ceta.EnforcementActionDetail;
import us.oh.state.epa.stars2.app.ceta.FceDetail;
import us.oh.state.epa.stars2.app.ceta.StackTestDetail;
import us.oh.state.epa.stars2.app.compliance.ComplianceReports;
import us.oh.state.epa.stars2.app.delegation.Delegation;
import us.oh.state.epa.stars2.app.emissionsReport.ReportDetail;
import us.oh.state.epa.stars2.app.facility.UndeliveredMail;
import us.oh.state.epa.stars2.app.permit.PermitDetail;
import us.oh.state.epa.stars2.app.relocation.Relocation;
import us.oh.state.epa.stars2.app.workflow.ActivityProfile;
import us.oh.state.epa.stars2.app.workflow.ToDoSearch;
import us.oh.state.epa.stars2.app.workflow.WorkFlow2DDraw;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.WorkflowProcessDef;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.ConfirmWindow;
import us.oh.state.epa.stars2.webcommon.correspondence.CorrespondenceDetail;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.oh.state.epa.stars2.webcommon.reports.ReportSearch;
import us.wy.state.deq.impact.webcommon.monitoring.MonitorReportDetail;

/**
 * The workflow system hold a external id for external subsystem.  By using
 * this external id, user can navigate from workflow system to external 
 * subsystem.  Therefore, subsystem develop has to extends this class to the
 * external subsystem back end bean to make navigation and doSelection work.
 * 
 * @author Poshan Yeh
 */
public abstract class TaskBase extends ValidationBase {

	private static final long serialVersionUID = 3618823681973414206L;

	/**
     * This is the first method workflow subsystem will call to setUp the
     * external back end bean. All other external back end bean has to define
     * itself here. If there is other back end bean related to the external back
     * end bean, the external back end bean has to define by itself in external
     * back end bean.
     * 
     * Subsystem developer has to create his "else if" statement to work.
     * 
     * @param process
     * @param activityTemplateId
     * @param externalId
     * @return
     */
    public static TaskBase setUp(WorkFlowProcess process,
            Integer activityTemplateId, int externalId) {
        TaskBase ret = null;

        if (WorkflowProcessDef.PERMITTING.equalsIgnoreCase(process
                .getProcessCd())) {
            PermitDetail permitDetail = (PermitDetail) FacesUtil
                    .getManagedBean("permitDetail");
            ret = permitDetail;
        } else if (process.getProcessTemplateNm().equals(
                WorkFlowProcess.UNDELIVERED_MAIL)) {
            // RUM-related backend bean added by Steve L.
            UndeliveredMail undeliveredMail = (UndeliveredMail) FacesUtil
                    .getManagedBean("undeliveredMail");
            ret = undeliveredMail;
        } else if (process.getProcessTemplateNm().equals(
                WorkFlowProcess.COMPLIANCE_REPORTS) || 
                process.getProcessTemplateNm().equals(WorkFlowProcess.CR_CEMS_COMS_RATA) ||
                process.getProcessTemplateNm().equals(WorkFlowProcess.CR_ONE_TIME_REPORTS) ||
                process.getProcessTemplateNm().equals(WorkFlowProcess.CR_OTHER_COMP_REPORTS)) {
            ComplianceReports cr = (ComplianceReports) FacesUtil
                    .getManagedBean("complianceReport");
            ret = cr;
        } else if (process.getProcessTemplateNm().equals("Stack Testing")) {
            StackTestDetail sd = (StackTestDetail) FacesUtil
                    .getManagedBean("stackTestDetail");
            ret = sd;
        } else if (process.getProcessTemplateNm().equals(WorkFlowProcess.INSPECTION_DUE_SOON)) {
        	FceDetail fce = (FceDetail) FacesUtil.getManagedBean("fceDetail");
            ret = fce;            
        } else if (process.getProcessTemplateNm().equals(
                    WorkFlowProcess.INTENT_TO_RELOCATE)) {
                Relocation rr = (Relocation) FacesUtil
                        .getManagedBean("relocation");
                ret = rr;
        } else if (process.getProcessTemplateNm().equals(
                WorkFlowProcess.DELEGATION)) {
            Delegation rr = (Delegation) FacesUtil
                    .getManagedBean("delegation");
            ret = rr;
        } else if (process.getProcessTemplateNm().equals(
                WorkFlowProcess.SMTV_TV_REVIEW)) {
            ReportSearch reportSearch = (ReportSearch) FacesUtil
            .getManagedBean("reportSearch");
            reportSearch.setNtvReport(false);
            ReportDetail rd = (ReportDetail)FacesUtil
            .getManagedBean("reportDetail");
            ret = rd.getReportTask();
        } else if (process.getProcessTemplateNm().equals(
                WorkFlowProcess.BLUE_CARD_REVIEW)
                || process.getProcessTemplateNm().equals(
                        WorkFlowProcess.CO_REVIEW)) {
            ReportSearch reportSearch = (ReportSearch) FacesUtil
            .getManagedBean("reportSearch");
            reportSearch.setNtvReport(true);
            ReportDetail rd = (ReportDetail)FacesUtil
            .getManagedBean("reportDetail");
            ret = rd.getReportTask();
        } else if (WorkflowProcessDef.CORRESPONDENCE.equalsIgnoreCase(process
                .getProcessCd())) {
            CorrespondenceDetail correspondenceDetail = (CorrespondenceDetail) FacesUtil
                    .getManagedBean("correspondenceDetail");
            ret = correspondenceDetail;
        }

        // Add your "else if" statement here. You should not change anything
        // before this line.
        else if(process.getProcessTemplateNm().equals(WorkFlowProcess.CR_GENERIC_COMPLIANCE_REPORT)) {
        	ComplianceReports cr = (ComplianceReports) FacesUtil
                    .getManagedBean("complianceReport");
            ret = cr;
        } else if(process.getProcessTemplateNm().equals(WorkFlowProcess.AMBIENT_MONITORING_REPORT)) {
        	MonitorReportDetail reportDetail = (MonitorReportDetail) FacesUtil
                    .getManagedBean("monitorReportDetail");
            ret = reportDetail;
        } else if(process.getProcessTemplateNm().equals(WorkFlowProcess.ENFORCEMENT_ACTIONS)) {
        	EnforcementActionDetail enforcementActionDetail = (EnforcementActionDetail) FacesUtil
                    .getManagedBean("enforcementActionDetail");
            ret = enforcementActionDetail;
        } else if (process.getProcessTemplateNm().equals(WorkFlowProcess.INSPECTION_REPORT_WORKFLOW_NAME)) {
        	FceDetail fceDetail = (FceDetail) FacesUtil
                    .getManagedBean("fceDetail");
            ret = fceDetail;
        }
        
        // you should not change anything after this line.
        if (ret != null) {
            ret.setWorkflowProcessId(process.getProcessId());
            ret.setWorkflowActivityId(activityTemplateId);
            ret.setFromTODOList(true);
            ret.setExternalId(externalId);
        }
        return ret;
    }

    /**
     * The validate method will be call everytime user try to checkin a task.
     * Talk to Poshan Yeh if you need to know the activityTemplate Id or you can
     * move your mouse onto the task at workflow profile to see the URL ids.
     * 
     * @param inActivityTemplateId
     * @return
     */
    public abstract List<ValidationMessage> validate(
            Integer inActivityTemplateId);

    /**
     * This method is used in ToDo list to find out which page to navigated to.
     * the outcome has to match in your own config.xml. Change script of
     * WF_ACTIVITY_TEMPLATE_DEF to modify the URL of your task.
     * 
     * @param url
     * @param ret
     * @return
     */
    public abstract String findOutcome(String url, String ret);

    /**
     * This method is used to find out the button name in task profile to link
     * to external subsystem base on the output of findOutcome method.
     * 
     * @param activity
     * @return
     */
    public String getExternalName(ProcessActivity activity) {
        return null;
    }

    /**
     * This is the number shows on task profile workflow information part. This
     * will also link to external subsystem base the output of on toExternal.
     * 
     * @return
     */
    public String getExternalNum() {
        return null;
    }

    /**
     * This method is used to do anything on external subsystem to load the
     * profile page and return the outcome. Usually is the load method in
     * external back end bean.
     * 
     * @return
     */
    public String toExternal() {
        return null;
    }

    /**
     * The external id in workflow system usually is the key id of subsystem.
     * However, subsystem developer can add any method in the external back end
     * bean to do anything and set the key id for load subsystem object.
     * 
     * @return
     */
    public abstract Integer getExternalId();

    public abstract void setExternalId(Integer externalId);

    /**
     * implement this method if you want to do somthing when the workflow is
     * canceled.
     */
    public void cancellation() {
    }

    /**
     * DoSelected button of aggregate table.
     * 
     * This is the method call for aggregate table to show button or not in this
     * activity.
     * 
     * @param activity
     * @return
     */
    public boolean isDoSelectedButton(ProcessActivity activity) {
        return false;
    }

    /**
     * The confirm window message for user.
     * 
     * @return
     */
    public String getDoSelectedConfirmMsg() {
        return null;
    }

    /**
     * The confirm window button type. Check ConfirmWindow to know what can use.
     * 
     * @return
     */
    public String getDoSelectedConfirmType() {
        return (new ConfirmWindow()).getYesNo();
    }

    /**
     * Display of button name for the aggregate table
     * 
     * @return
     */
    public String getDoSelectedButtonText() {
        return null;
    }

    /**
     * What to do for the selected tasks. From here is a list of tasks user
     * selected. Subsystem developer needs to retrieve each task from passin
     * object table to work on something.
     * 
     * @param table
     */
    public void doSelected(UIXTable table) {
        return;
    }

    /**
     * After this line is using for the "To Workflow Task" button and "Workflow
     * List" 3rd level menu. Subsystem developer should not change anything
     * below. If additional implementation is needed, please override from
     * external back end bean.
     * 
     * @return
     */
    public String goToCurrentWorkflow() {
        WorkFlow2DDraw workFlowProfile = (WorkFlow2DDraw) FacesUtil
                .getManagedBean("workFlow2DDraw");
        workFlowProfile.setProcessId(workflowProcessId);
        SimpleMenuItem.setDisabled("menuItem_workflowProfile", false);

        ActivityProfile activityProfile = (ActivityProfile) FacesUtil
                .getManagedBean("activityProfile");
        activityProfile.setActivityTemplateId(workflowActivityId);
        activityProfile.setFromExternal(true);
        activityProfile.submitProfile();
        return "currentWorkflow";
    }

    /**
     * Set your own outcome for this return in your back bean See
     * PermitDetail.java
     * 
     * @return
     */
    //TODO what in the wild world of sports is going on here??
    public String goToAllWorkflows() {
        ToDoSearch toDoSearch = (ToDoSearch) FacesUtil
                .getManagedBean("toDoSearch");
//        ToDoSearch toDoSearch = new ToDoSearch();
        toDoSearch.setExternalId(getExternalId());
        toDoSearch.setShowFacility(false);
        toDoSearch.setProcessTypeCd(processTypeCd);
        toDoSearch.submit();
        toDoSearch.setHasSearchResults(true);
//        ToDoSearch tBean = (ToDoSearch) FacesUtil
//            .getManagedBean("toDoSearch");
//        tBean.setShortActivities(toDoSearch.getShortActivities());
        return "allWorkflows";
    }
    
    private String processTypeCd;
        
    private Integer workflowProcessId;

    private Integer workflowActivityId;

    private boolean fromTODOList;
    
    public Integer getWorkflowProcessId() {
        return workflowProcessId;
    }

    public void setWorkflowProcessId(Integer workflowProcessId) {
        this.workflowProcessId = workflowProcessId;
    }

    public Integer getWorkflowActivityId() {
        return workflowActivityId;
    }

    public void setWorkflowActivityId(Integer workflowActivityId) {
        this.workflowActivityId = workflowActivityId;
    }

    public boolean getFromTODOList() {
        return fromTODOList;
    }

    public void setFromTODOList(boolean fromTODOList) {
        this.fromTODOList = fromTODOList;
    }

    /**
     * @return the processTypeCd
     */
    public final String getProcessTypeCd() {
        return processTypeCd;
    }

    /**
     * @param processTypeCd the processTypeCd to set
     */
    public final void setProcessTypeCd(String processTypeCd) {
        this.processTypeCd = processTypeCd;
    }
    
    public static String makeErrorMsg(WorkFlowProcess process, String string, Exception e, Logger logger) {
        String s = WorkflowProcessDef.getData().getItems().getItemDesc(process.getProcessCd());
        if(s == null) {
            s = process.getProcessCd();
        }
        String msg = string + " : " + s + " | "
                + process.getProcessTemplateNm() + " | " 
                + process.getExternalId();
        DisplayUtil.displayError(msg);
        logger.warn(msg, e);
        return msg;
    }
}
