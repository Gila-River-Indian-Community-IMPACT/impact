package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.lang.WordUtils;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;

/**
 * Process.
 * 
 * <p>
 * <b>Note about the Ready Date:</b> The process ready date is used to handle
 * situations where a service order has been placed before it is allowed to be
 * provisioned, based on customer need date. For example, suppose the system
 * will not accept a new service order or move service order until 30 days
 * before the customer need date. Now, suppose a customer orders that service 60
 * days before they actually need it. In that case, the ready date would be set
 * to "need date - 30 days". The work flow engine will not "ready" that workflow
 * until the ready date is met.
 * </p>
 * 
 * <p>
 * The "ready date" is only used if needed. If the customer orders that same
 * service 29 days before needed, then the workflow engine would automatically
 * ready the workflow when the order was submitted and the ready date would
 * remain null. Whenever the ready date is used to activate a workflow, it
 * should be set to null in the database.
 * </p>
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version $Revision: 1.26 $
 * @author $Author: ryamini $
 */
@SuppressWarnings("serial")
public class WorkFlowProcess extends BaseDB {

    public static final String STATE_COMPLETED_CD = "CM";
    public static final String STATE_COMPLETED_DESC = "Completed";
    public static final String STATE_IN_PROCESS_DESC = "In Process";
    public static final String STATE_IN_PROCESS_CD = "IP";
    public static final String STATE_CANCELLED_CD = "CNC";
    public static final String STATE_CANCELLED_DESC = "Cancelled";
    public static final String STATUS_OK_DESC = "OK";
    public static final String STATUS_OK_CD = "OK";
    public static final String OK_COLOR_STRING = "00FF00";
    public static final String STATUS_JEOPARDY_DESC = "Jeopardy";
    public static final String STATUS_JEOPARDY_CD = "JP";
    public static final String STATUS_JEOP_COLOR_STRING = "FFFF80";
    public static final String STATUS_LATE_CD = "LA";
    public static final String STATUS_LATE_DESC = "Late";
    public static final String STATUS_LATE_COLOR_STRING = "FF5050";
    
    public static final String PERMIT_WORKFLOW_NAME = "Permitting";
    public static final String UNDELIVERED_MAIL = "Undelivered Mail";
    public static final String COMPLIANCE_REPORTS = "Compliance Reports";
    public static final String CR_CEMS_COMS_RATA = "CEMS/COMS/RATA Reporting";
    public static final String CR_ONE_TIME_REPORTS = "One Time Reports";
    public static final String CR_OTHER_COMP_REPORTS = "Other Compliance Reports";
    public static final String CR_GENERIC_COMPLIANCE_REPORT = "Generic Compliance Report";
    public static final String STACK_TESTS = "Stack Testing";
    public static final String INSPECTION_DUE_SOON = "Inspection Due Soon";
    public static final String INTENT_TO_RELOCATE = "ITR";
    public static final String DELEGATION = "DOR";
    public static final String BLUE_CARD_REVIEW = "Blue Card Review";
    public static final String SMTV_TV_REVIEW = "Emissions Inventory Review";
    public static final String CO_REVIEW = "CO Review";
    public static final String RPE = "Extension";
    public static final String PBR = "PBR";
    public static final String REVOCATION = "Rescission";
    public static final String CORRECTED_APPLICATION = "Corrected Application";
    public static final String TV_EI_REVIEW = "Emissions Inventory Review";
    public static final String FACILITY_CHANGES = "Facility Changes/Miscellaneous";
    public static final String AMBIENT_MONITORING_REPORT = "Ambient Monitoring";
    public static final String ENFORCEMENT_ACTIONS = "Enforcement Actions";
    public static final String INSPECTION_REPORT_WORKFLOW_NAME = "Inspection Report";
    
    
    private Integer processId; // Primary key
    private Integer userId; // Usually "null" in the database
    private Integer facilityId;
    private String facilityIdString;
    private Integer serviceId;
    private Integer processTemplateId;
    private Integer parentProcessId;
    private Integer activityTemplateId;
    private Integer externalId;
    private String reportId;
    private Timestamp startDt;
    private Timestamp endDt;
    private Timestamp dueDt;
    private Timestamp jeopardyDt;
    private Timestamp readyDt;
    private String processTemplateNm;
    private String activityTemplateNm;
    private String facilityNm;
    private String processCd;
    private String dynamicInd;
    private String expedite;
    private String orderType;
    private String expeditedInd;
    private String state;
    private String status;
    private String statusColor;
    private boolean current;
    
    private String permitNumber;
    private String permitTypeCd;
    private String permitReasonCd;
    private Timestamp oldDueDt;
    private Timestamp oldJeopardyDt;

    /**
     * 
     */
    public WorkFlowProcess() {
        super();
    }

    /**
     * @param old
     */
    public WorkFlowProcess(WorkFlowProcess old) {
        super(old);
        if (old != null) {
            setActivityTemplateId(old.getActivityTemplateId());
            setActivityTemplateNm(old.getActivityTemplateNm());
            setCurrent(old.isCurrent());
            setDueDt(old.getDueDt());
            setDynamicInd(old.getDynamicInd());
            setEndDt(old.getEndDt());
            setExpedite(old.getExpedite());
            setExpeditedInd(old.getExpeditedInd());
            setExternalId(old.getExternalId());
            setFacilityId(old.getFacilityId());
            setFacilityIdString(old.getFacilityIdString());
            setFacilityNm(old.getFacilityNm());
            setJeopardyDt(old.getJeopardyDt());
            setOrderType(old.getOrderType());
            setParentProcessId(old.getParentProcessId());
            setProcessCd(old.getProcessCd());
            setProcessId(old.getProcessId());
            setProcessTemplateId(old.getProcessTemplateId());
            setProcessTemplateNm(old.getProcessTemplateNm());
            setReadyDt(old.getReadyDt());
            setReportId(old.getReportId());
            setServiceId(old.getServiceId());
            setStartDt(old.getStartDt());
            setState(old.getState());
            setStatus(old.getStatus());
            setStatusColor(old.getStatusColor());
            setUserId(old.getUserId());
            setPermitNumber(old.getPermitNumber());
            setPermitTypeCd(old.getPermitTypeCd());
            setPermitReasonCd(old.getPermitReasonCd());
        }
    }

    
    public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public final String getStatusColor() {
        return statusColor;
    }

    public final void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }

    public final boolean isCurrent() {
        return current;
    }

    public final void setCurrent(boolean current) {
        this.current = current;
    }

    public final String getFacilityIdString() {
        return facilityIdString;
    }

    public final void setFacilityIdString(String facilityIdString) {
        this.facilityIdString = facilityIdString;
    }

    public final String getState() {
        return state;
    }

    public final void setState(String state) {
        this.state = state;
    }

    public final String getStatus() {
        return status;
    }

    public final void setStatus(String status) {
        this.status = status;
    }

    public final String toString() {
        StringBuffer sb = new StringBuffer();
        String nl = System.getProperty("line.separator", "\n");

        sb.append("Process Id: ");

        if (this.processId != null) {
            sb.append(this.processId.toString());
        } else {
            sb.append("NULL");
        }

        sb.append(nl);
        sb.append("Process Template Id: ");

        if (this.processTemplateId != null) {
            sb.append(this.processTemplateId.toString());
        } else {
            sb.append("NULL");
        }

        sb.append(nl);
        sb.append("Parent Process Id: ");

        if (this.parentProcessId != null) {
            sb.append(this.parentProcessId.toString());
        } else {
            sb.append("NULL");
        }

        sb.append(nl);
        sb.append("Activity Template Id: ");

        if (this.activityTemplateId != null) {
            sb.append(this.activityTemplateId.toString());
        } else {
            sb.append("NULL");
        }

        sb.append(nl);
        sb.append("External Id: ");

        if (this.externalId != null) {
            sb.append(this.externalId.toString());
        } else {
            sb.append("NULL");
        }

        sb.append(nl);
        sb.append("Service Id: ");

        if (this.serviceId != null) {
            sb.append(this.serviceId.toString());
        } else {
            sb.append("NULL");
        }

        sb.append(nl);
        sb.append("facility Id: ");

        if (this.facilityId != null) {
            sb.append(this.facilityId.toString());
        } else {
            sb.append("NULL");
        }

        sb.append(nl);
        sb.append("Start Date: ");

        if (this.startDt != null) {
            sb.append(this.startDt.toString());
        } else {
            sb.append("None");
        }

        sb.append(nl);
        sb.append("End Date: ");

        if (this.endDt != null) {
            sb.append(this.endDt.toString());
        } else {
            sb.append("None");
        }

        sb.append(nl);
        sb.append("Jeopardy Date: ");

        if (this.jeopardyDt != null) {
            sb.append(this.jeopardyDt.toString());
        } else {
            sb.append("None");
        }

        sb.append(nl);
        sb.append("Due Date: ");

        if (this.dueDt != null) {
            sb.append(this.dueDt.toString());
        } else {
            sb.append("None");
        }

        sb.append(nl);
        sb.append("Ready Date: ");

        if (this.readyDt != null) {
            sb.append(this.readyDt.toString());
        } else {
            sb.append("None");
        }

        sb.append(nl);
        return sb.toString();
    }

    // ------------------------------------------
    // Dynamic Ind
    // ------------------------------------------

    public final String getDynamicInd() {
        return this.dynamicInd;
    }

    public final void setDynamicInd(String ind) {
        this.dynamicInd = ind;
    }

    // ------------------------------------------
    // Expedite
    // ------------------------------------------

    public final String getExpedite() {
        return this.expedite;
    }

    public final void setExpedite(String expedite) {
        this.expedite = expedite;
    }

    // ------------------------------------------
    // processId
    // ------------------------------------------

    /**
     * ProcessId
     * 
     * @return Integer
     */
    public final Integer getProcessId() {
        return processId;
    }

    /**
     * ProcessId
     * 
     * @param processId
     */
    public final void setProcessId(Integer processId) {
        this.processId = processId;
    }

    // ------------------------------------------
    // externalId
    // ------------------------------------------

    /**
     * ExternalId
     * 
     * @return Integer
     */
    public final Integer getExternalId() {
        return externalId;
    }

    /**
     * ExternalId
     * 
     * @param externalId
     */
    public final void setExternalId(Integer externalId) {
        this.externalId = externalId;
    }

    // ------------------------------------------
    // serviceId
    // ------------------------------------------

    /**
     * ServiceId
     * 
     * @return Integer
     */
    public final Integer getServiceId() {
        return serviceId;
    }

    /**
     * ServiceId
     * 
     * @param serviceId
     */
    public final void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    // ------------------------------------------
    // facilityId
    // ------------------------------------------

    /**
     * facilityId
     * 
     * @return Integer
     */
    public final Integer getFacilityId() {
        return facilityId;
    }

    /**
     * facilityId
     * 
     * @param facilityId
     */
    public final void setFacilityId(Integer facilityId) {
        this.facilityId = facilityId;
    }

    // ------------------------------------------
    // processTemplateId
    // ------------------------------------------

    /**
     * ProcessTemplateId
     * 
     * @return Integer
     */
    public final Integer getProcessTemplateId() {
        return processTemplateId;
    }

    /**
     * ProcessTemplateId
     * 
     * @param processTemplateId
     */
    public final void setProcessTemplateId(Integer processTemplateId) {
        this.processTemplateId = processTemplateId;
    }

    // ------------------------------------------
    // parentProcessId
    // ------------------------------------------

    /**
     * ParentProcessId
     * 
     * @return Integer
     */
    public final Integer getParentProcessId() {
        return parentProcessId;
    }

    /**
     * ParentProcessId
     * 
     * @param parentProcessId
     */
    public final void setParentProcessId(Integer parentProcessId) {
        this.parentProcessId = parentProcessId;
    }

    // ------------------------------------------
    // activityTemplateId
    // ------------------------------------------

    /**
     * ActivityTemplateId
     * 
     * @return Integer
     */
    public final Integer getActivityTemplateId() {
        return activityTemplateId;
    }

    /**
     * ActivityTemplateId
     * 
     * @param activityTemplateId
     */
    public final void setActivityTemplateId(Integer activityTemplateId) {
        this.activityTemplateId = activityTemplateId;
    }

    // ------------------------------------------
    // startDt
    // ------------------------------------------

    /**
     * StartDt
     * 
     * @return Timestamp
     */
    public final Timestamp getStartDt() {
        return startDt;
    }

    /**
     * StartDt
     * 
     * @param startDt
     */
    public final void setStartDt(Timestamp startDt) {
        this.startDt = startDt;
    }

    // ------------------------------------------
    // endDt
    // ------------------------------------------

    /**
     * EndDt
     * 
     * @return Timestamp
     */
    public final Timestamp getEndDt() {
        return endDt;
    }

    /**
     * EndDt
     * 
     * @param endDt
     */
    public final void setEndDt(Timestamp endDt) {
        this.endDt = endDt;
    }

    // ------------------------------------------
    // jeopardyDt
    // ------------------------------------------

    /**
     * JeopardyDt
     * 
     * @return Timestamp
     */
    public final Timestamp getJeopardyDt() {
        return jeopardyDt;
    }

    /**
     * JeopardyDt
     * 
     * @param jeopardyDt
     */
    public final void setJeopardyDt(Timestamp jeopardyDt) {
        oldJeopardyDt = this.jeopardyDt;
        this.jeopardyDt = jeopardyDt;
    }

    // ------------------------------------------
    // dueDt
    // ------------------------------------------

    /**
     * @return the oldDueDt
     */
    public final Timestamp getOldDueDt() {
        return oldDueDt;
    }

    /**
     * @return the oldJeopardyDt
     */
    public final Timestamp getOldJeopardyDt() {
        return oldJeopardyDt;
    }

    /**
     * DueDt
     * 
     * @return Timestamp
     */
    public final Timestamp getDueDt() {
        return dueDt;
    }

    /**
     * DueDt
     * 
     * @param dueDt
     */
    public final void setDueDt(Timestamp dueDt) {
        oldDueDt = this.dueDt;
        this.dueDt = dueDt;
    }

    // ------------------------------------------
    // userId
    // ------------------------------------------

    public final Integer getUserId() {
        return this.userId;
    }

    public final void setUserId(Integer newId) {
        this.userId = newId;
    }

    // ------------------------------------------
    // processTemplateNm
    // ------------------------------------------

    public final String getProcessTemplateNm() {
        return this.processTemplateNm;
    }

    public final void setProcessTemplateNm(String newName) {
        this.processTemplateNm = newName;
    }

    // ------------------------------------------
    // activityTemplateNm
    // ------------------------------------------

    public final String getActivityTemplateNm() {
        return this.activityTemplateNm;
    }

    public final void setActivityTemplateNm(String newName) {
        this.activityTemplateNm = newName;
    }

    // ------------------------------------------
    // facilityNm
    // ------------------------------------------

    public final String getFacilityNm() {
        return this.facilityNm;
    }

    public final void setFacilityNm(String newName) {
        this.facilityNm = newName;
    }

    // ------------------------------------------
    // processCd
    // ------------------------------------------

    public final String getProcessCd() {
        return this.processCd;
    }

    public final void setProcessCd(String cd) {
        this.processCd = cd;
    }

    // ------------------------------------------
    // Order Type -- "New" or "cancellation"
    // ------------------------------------------

    public final String getOrderType() {
        return this.orderType;
    }

    public final void setOrderType(String type) {
        this.orderType = type;
    }

    // ------------------------------------------
    // Expedited -- "Y" or "N"
    // ------------------------------------------

    public final String getExpeditedInd() {
        return this.expeditedInd;
    }

    public final void setExpeditedInd(String ind) {
        this.expeditedInd = ind;
    }

    public final Timestamp getReadyDt() {
        return this.readyDt;
    }

    public final void setReadyDt(Timestamp newDt) {
        this.readyDt = newDt;
    }

    public final void populate(ResultSet rs) {

        try {
            setProcessId(AbstractDAO.getInteger(rs, "process_id"));
            setUserId(AbstractDAO.getInteger(rs, "user_id"));
            setFacilityId(AbstractDAO.getInteger(rs, "fp_id"));
            setFacilityIdString(rs.getString("facility_id"));
            setServiceId(AbstractDAO.getInteger(rs, "service_id"));
            setProcessTemplateId(AbstractDAO.getInteger(rs,"process_template_id"));
            setParentProcessId(AbstractDAO.getInteger(rs, "parent_process_id"));
            setActivityTemplateId(AbstractDAO.getInteger(rs,"activity_template_id"));
            setExternalId(AbstractDAO.getInteger(rs, "external_id"));
            setStartDt(rs.getTimestamp("start_dt"));
            setEndDt(rs.getTimestamp("end_dt"));
            setDueDt(rs.getTimestamp("due_dt"));
            setJeopardyDt(rs.getTimestamp("jeopardy_dt"));
            setReadyDt(rs.getTimestamp("ready_dt"));
            setProcessTemplateNm(rs.getString("process_template_nm"));
            setFacilityNm(rs.getString("facility_nm"));
            setActivityTemplateNm(rs.getString("activity_template_nm"));
            setProcessCd(rs.getString("process_cd"));
            setDynamicInd(rs.getString("dynamic_ind"));
            setExpedite(rs.getString("expedite"));
            setLastModified(AbstractDAO.getInteger(rs, "process_lm"));
        }
        catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }

        try {
            if (getEndDt() == null) {
                setState(STATE_IN_PROCESS_CD);
            }
            else {
                setState(rs.getString("ACTIVITY_STATUS_CD"));
            }
        }
        catch (SQLException sqle) {
            logger.debug("Optional field error: " + sqle.getMessage());
        }
        
        setStatus(checkStatus(getJeopardyDt(), getDueDt(), getEndDt()));
        setStatusColor(checkStatusColor(getStatus()));

        try {
            // Those are for workflow search table.
            setPermitNumber(rs.getString("PERMIT_NBR"));
            setPermitTypeCd(rs.getString("PERMIT_TYPE_CD"));
            setPermitReasonCd(rs.getString("PRIMARY_REASON_CD"));
        }
        catch (SQLException sqle) {
            logger.debug("Optional field error: on permit info " + sqle.getMessage());
        }
    }

    public final static String checkStatusColor(String s) {
        if (s.equalsIgnoreCase(STATUS_OK_CD)) {
            return "";
        } else if (s.equalsIgnoreCase(STATUS_JEOPARDY_CD)) {
            return STATUS_JEOP_COLOR_STRING;
        } else {
            return STATUS_LATE_COLOR_STRING;
        }
    }

    public final static String checkStatus(Timestamp jeopardyDt,
            Timestamp dueDt, Timestamp endDt) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (endDt == null) {
            endDt = now;
        }

        if (dueDt != null && endDt.after(dueDt)) {
            return STATUS_LATE_CD;
        } else if (jeopardyDt != null && endDt.after(jeopardyDt)) {
            return STATUS_JEOPARDY_CD;
        } else {
            return STATUS_OK_CD;
        }
    }

    /**
     * @return the permitNumber
     */
    public final String getPermitNumber() {
        return permitNumber;
    }

    /**
     * @param permitNumber the permitNumber to set
     */
    public final void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }

    /**
     * @return the permitTypeCd
     */
    public final String getPermitTypeCd() {
        return permitTypeCd;
    }

    /**
     * @param permitTypeCd the permitTypeCd to set
     */
    public final void setPermitTypeCd(String permitTypeCd) {
        this.permitTypeCd = permitTypeCd;
    }
    
    public final String getPermitReasonCd() {
		return permitReasonCd;
	}

	public final void setPermitReasonCd(String permitReasonCd) {
		this.permitReasonCd = permitReasonCd;
	}

	public final String getPermitTypeDesc() {
        String ret = "";
        if (getPermitTypeCd() != null)
            ret = PermitTypeDef.getData().getItems().getItemDesc(getPermitTypeCd());
        return ret;
    }
	
	 public final String getUserName() {
	        return WordUtils.capitalize(BasicUsersDef.getAllUserData().getItems().getDescFromAllItem(getUserId()));
	    }
	
}
