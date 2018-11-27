package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.WordUtils;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;

/**
 * ProcessActivity.
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version $Revision: 1.12 $
 * @author $Author: dleinbaugh $
 */
public class ProcessActivityLight extends BaseDB {
	
	private static final long serialVersionUID = 458262750499656943L;

	private Integer processId;
    private Integer activityTemplateId;
    private Integer loopCnt;
    private String aggregate;
    private String facilityId;
    private Integer fpId;
    private String facilityNm;
    private boolean viewed;
    private Integer userId;
    private String activityStatusCd;
    private Timestamp startDt;
    private Integer duration;
    private Timestamp dueDt;
    private Timestamp jeopardyDt;
    private Timestamp endDt;
    private String processTemplateNm;
    private Integer externalId;
    private String reportId;

	private String status;
    private String statusColor;
    private Timestamp processStartDt;
    private Integer processDuration;
    private Timestamp processDueDt;
    private String expedited;
    private String activityTemplateNm;
    
    private Integer permitId;
	private String permitNumber;
    private String permitTypeCd;
    private String permitReasonCd;
    private List<String> permitReasonCDs;
    
    private String cmpId;
    private String cmpName;

    /**
     * 
     */
    public ProcessActivityLight() {
        super();
    }

    /**
     * @param old
     */
    public ProcessActivityLight(ProcessActivityLight old) {
        super(old);
        if (old != null) {
            setActivityTemplateId(old.getActivityTemplateId());
            setActivityStatusCd(old.getActivityStatusCd());
            setActivityTemplateId(old.getActivityTemplateId());
            setActivityTemplateNm(old.getActivityTemplateNm());
            setAggregate(old.getAggregate());
            setDueDt(old.getDueDt());
            setEndDt(old.getEndDt());
            setDuration(old.getDuration());
            setExpedited(old.getExpedited());
            setExternalId(old.getExternalId());
            setViewed(old.isViewed());
            setFacilityId(old.getFacilityId());
            setFacilityNm(old.getFacilityNm());
            setFpId(old.getFpId());
            setLoopCnt(old.getLoopCnt());
            setProcessDueDt(old.getProcessDueDt());
            setProcessDuration(old.getProcessDuration());
            setProcessId(old.getProcessId());
            setProcessStartDt(old.getProcessStartDt());
            setProcessTemplateNm(old.getProcessTemplateNm());
            setSelected(old.isSelected());
            setStartDt(old.getStartDt());
            setStatus(old.getStatus());
            setStatusColor(old.getStatusColor());
            setUserId(old.getUserId());
            setPermitId(old.getPermitId());
            setPermitNumber(old.getPermitNumber());
            setPermitTypeCd(old.getPermitTypeCd());
            setPermitReasonCd(old.getPermitReasonCd());
            setPermitReasonCDs(old.getPermitReasonCDs());
            setCmpId(old.getCmpId());
            setCmpName(old.getCmpName());
        }
    }

    public Integer getExternalId() {
		return externalId;
	}

	public void setExternalId(Integer externalId) {
		this.externalId = externalId;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public final String getDurationColor(){
        String ret = "";
        if (getStatus().equals(WorkFlowProcess.STATUS_OK_CD)){
            ret = " background-color:#" + WorkFlowProcess.checkStatusColor(
                        WorkFlowProcess.checkStatus(getJeopardyDt(), getDueDt(),
                                getEndDt())) + ";";
        }else
            ret = " background-color:#" + getStatusColor() + ";";
        return ret;
    }
    
    public final String getStatusColor() {
        return statusColor;
    }

    public final void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }

    public final String getProcessTemplateNm() {
        return processTemplateNm;
    }

    public final void setProcessTemplateNm(String processTemplateNm) {
        this.processTemplateNm = processTemplateNm;
    }

    public final String getStatus() {
        return status;
    }

    // this should be moved out; we don't want dbobjects calling services/etc.
    // but, is it even being used anywhere??
    public final String getStatusDesc() {
    	//TODO decouple workflow
    	throw new RuntimeException("decouple workflow; not implemented yet");
//        WorkFlowDefs wfDef = (WorkFlowDefs) FacesUtil.getManagedBean("workFlowDefs");
//        return wfDef.getStatusList().get(getStatus());
//    	return null;
    }
    
    public final void setStatus(String status) {
        this.status = status;
    }

    public final String getAggregate() {
        return aggregate;
    }

    public final void setAggregate(String aggregate) {
        this.aggregate = aggregate;
    }

    public final Integer getFpId() {
        return fpId;
    }

    public final void setFpId(Integer fpId) {
        this.fpId = fpId;
    }

    /**
     * @return Returns the expedited.
     */
    public final String getExpedited() {
        return expedited;
    }

    /**
     * @param expedited
     *            The expedited to set.
     */
    public final void setExpedited(String expedited) {
        this.expedited = expedited;
    }

    public final boolean isViewed() {
        return viewed;
    }

    public final void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

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
     * @param ProcessId
     */
    public final void setProcessId(Integer processId) {
        this.processId = processId;
    }

    /**
     * UserId
     * 
     * @return Integer
     */
    public final Integer getUserId() {
        return userId;
    }
    
    public final String getUserName() {
        return WordUtils.capitalize(BasicUsersDef.getAllUserData().getItems().getDescFromAllItem(getUserId()));
    }

    /**
     * UserId
     * 
     * @param userId
     */
    public final void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * loopCnt
     * 
     * @return Integer
     */
    public final Integer getLoopCnt() {
        return loopCnt;
    }

    /**
     * LoopCnt
     * 
     * @param loopCnt
     */
    public final void setLoopCnt(Integer loopCnt) {
        this.loopCnt = loopCnt;
    }

    /**
     * ActivityStatusCd
     * 
     * @return String
     */
    public final String getActivityStatusCd() {
        return activityStatusCd;
    }
    
    public final String getActivityStatusDesc() {
        return ActivityStatusDef.getData().getItems().getItemDesc(getActivityStatusCd());
    }

    /**
     * ActivityStatusCd
     * 
     * @param activityStatusCd
     */
    public final void setActivityStatusCd(String activityStatusCd) {
        this.activityStatusCd = activityStatusCd;
    }

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

    /**
     * StartDt
     * 
     * @return Timestamp
     */
    public final Timestamp getProcessStartDt() {
        return processStartDt;
    }

    /**
     * StartDt
     * 
     * @param startDt
     */
    public final void setProcessStartDt(Timestamp startDt) {
        processStartDt = startDt;
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
        this.dueDt = dueDt;
    }

    /**
     * DueDt
     * 
     * @return Timestamp
     */
    public final Timestamp getProcessDueDt() {
        return processDueDt;
    }

    /**
     * DueDt
     * 
     * @param dueDt
     */
    public final void setProcessDueDt(Timestamp dueDt) {
        processDueDt = dueDt;
    }

    /**
     * facilityId
     * 
     * @return Integer
     */
    public final String getFacilityId() {
        return facilityId;
    }

    /**
     * facilityId
     * 
     * @param facilityId
     */
    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public final String getFacilityNm() {
        return this.facilityNm;
    }

    public final void setFacilityNm(String newName) {
        this.facilityNm = newName;
    }

    public final void populate(ResultSet rs) {
        try {
            setActivityTemplateId(AbstractDAO.getInteger(rs,
                    "activity_template_id"));
            setProcessId(AbstractDAO.getInteger(rs, "process_id"));
            setUserId(AbstractDAO.getInteger(rs, "user_id"));
            setLoopCnt(AbstractDAO.getInteger(rs, "loop_cnt"));
            setActivityStatusCd(rs.getString("activity_status_cd"));
            setStartDt(rs.getTimestamp("A_start_dt"));
            setEndDt(rs.getTimestamp("A_END_DT"));
            setDueDt(rs.getTimestamp("A_Due_Dt"));
            setJeopardyDt(rs.getTimestamp("A_Jeopardy_Dt"));
            if (getStartDt() != null) {
                setDuration(new TimeSpan(getEndDt(), getStartDt()).getDays());
            }

            setProcessTemplateNm(rs.getString("PROCESS_TEMPLATE_NM"));
            StringBuffer name = new StringBuffer(rs
                    .getString("activity_template_nm"));

            if (getLoopCnt() != 1) {
                name.append(" - ");
                name.append(getLoopCnt());
            }
            setActivityTemplateNm(name.toString());
            if (getActivityTemplateId().equals(0))
                setActivityTemplateNm(rs.getString("DATA_VAL"));
            setViewed(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("VIEWED")));
            setAggregate(rs.getString("AGGREGATE"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
            setFacilityId(rs.getString("FACILITY_ID"));
            setFacilityNm(rs.getString("facility_Nm"));
            setFpId(AbstractDAO.getInteger(rs, "fp_Id"));
            setProcessStartDt(rs.getTimestamp("P_Start_Dt"));
            setProcessDueDt(rs.getTimestamp("P_Due_Dt"));
            Timestamp processJeoDt = rs.getTimestamp("P_Jeopardy_Dt");
            Timestamp processEndDt = rs.getTimestamp("P_End_Dt");
            setStatus(WorkFlowProcess.checkStatus(processJeoDt, getProcessDueDt(),
                    processEndDt));
            setStatusColor(WorkFlowProcess.checkStatusColor(getStatus()));

            if (getProcessStartDt() != null) {
                setProcessDuration(new TimeSpan(processEndDt,
                        getProcessStartDt()).getDays());
            }
            setPermitId(AbstractDAO.getInteger(rs, "PERMIT_ID"));
            setPermitNumber(rs.getString("PERMIT_NBR"));
            setPermitTypeCd(rs.getString("PERMIT_TYPE_CD"));
            setPermitReasonCd(rs.getString("PRIMARY_REASON_CD"));
            String reasons = rs.getString("all_reasons");
            ArrayList<String> allReasons = new ArrayList<String>();
            if (reasons != null && reasons.length() > 0) {
                StringTokenizer str = new StringTokenizer(reasons);
                while (str.hasMoreTokens()) {
                    allReasons.add(str.nextToken());
                }
                setPermitReasonCDs(allReasons);
            }
            setExternalId(rs.getInt("external_id"));
            setCmpId(rs.getString("cmp_id"));
            setCmpName(rs.getString("cmp_name"));
        } catch (SQLException sqle) {
            logger.debug(sqle.getMessage());
        }
    }

    public final int compareTo(Object o) {
        if (o instanceof ProcessActivityLight) {
            return this.activityTemplateId.compareTo(((ProcessActivityLight) o)
                    .getActivityTemplateId());
        }
        return -1;
    }

    @Override
    public final int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME
                * result
                + ((activityStatusCd == null) ? 0 : activityStatusCd.hashCode());
        result = PRIME
                * result
                + ((activityTemplateId == null) ? 0 : activityTemplateId
                        .hashCode());
        result = PRIME * result
                + ((aggregate == null) ? 0 : aggregate.hashCode());
        result = PRIME * result + ((dueDt == null) ? 0 : dueDt.hashCode());
        result = PRIME * result
                + ((facilityId == null) ? 0 : facilityId.hashCode());
        result = PRIME * result
                + ((facilityNm == null) ? 0 : facilityNm.hashCode());
        result = PRIME * result + ((fpId == null) ? 0 : fpId.hashCode());
        result = PRIME * result + ((loopCnt == null) ? 0 : loopCnt.hashCode());
        result = PRIME * result
                + ((processDueDt == null) ? 0 : processDueDt.hashCode());
        result = PRIME * result
                + ((processId == null) ? 0 : processId.hashCode());
        result = PRIME * result
                + ((processStartDt == null) ? 0 : processStartDt.hashCode());
        result = PRIME
                * result
                + ((processTemplateNm == null) ? 0 : processTemplateNm
                        .hashCode());
        result = PRIME * result + ((startDt == null) ? 0 : startDt.hashCode());
        result = PRIME * result + ((status == null) ? 0 : status.hashCode());
        result = PRIME * result
                + ((statusColor == null) ? 0 : statusColor.hashCode());
        result = PRIME * result + ((userId == null) ? 0 : userId.hashCode());
        result = PRIME * result + (viewed ? 1231 : 1237);
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ProcessActivityLight other = (ProcessActivityLight) obj;
        
        if (processId != other.processId) 
            return false;
        
        return true;
    }

    /**
     * @return the duration
     */
    public final Integer getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public final void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * @return the processDuration
     */
    public final Integer getProcessDuration() {
        return processDuration;
    }

    /**
     * @param processDuration the processDuration to set
     */
    public final void setProcessDuration(Integer processDuration) {
        this.processDuration = processDuration;
    }

    /**
     * @return the activityTemplateNm
     */
    public final String getActivityTemplateNm() {
        return activityTemplateNm;
    }

    /**
     * @param activityTemplateNm the activityTemplateNm to set
     */
    public final void setActivityTemplateNm(String activityTemplateNm) {
        this.activityTemplateNm = activityTemplateNm;
    }

    /**
     * @return the endDt
     */
    public final Timestamp getEndDt() {
        return endDt;
    }

    /**
     * @param endDt the endDt to set
     */
    public final void setEndDt(Timestamp endDt) {
        this.endDt = endDt;
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
    
    public final String getPermitTypeDesc() {
        String ret = "";
        if (getPermitTypeCd() != null)
            ret = PermitTypeDef.getData().getItems().getItemDesc(getPermitTypeCd());
        return ret;
    }

    /**
     * @return the permitReasonCDs
     */
    public final List<String> getPermitReasonCDs() {
        return permitReasonCDs;
    }

    /**
     * @param permitReasonCDs the permitReasonCDs to set
     */
    public final void setPermitReasonCDs(List<String> permitReasonCDs) {
        this.permitReasonCDs = permitReasonCDs;
    }

    /**
     * @return the permitReasonCd
     */
    public final String getPermitReasonCd() {
        return permitReasonCd;
    }

    /**
     * @param permitReasonCd the permitReasonCd to set
     */
    public final void setPermitReasonCd(String permitReasonCd) {
        this.permitReasonCd = permitReasonCd;
    }

    /**
     * @return the jeopardyDt
     */
    public final Timestamp getJeopardyDt() {
        return jeopardyDt;
    }

    /**
     * @param jeopardyDt the jeopardyDt to set
     */
    public final void setJeopardyDt(Timestamp jeopardyDt) {
        this.jeopardyDt = jeopardyDt;
    }
    


    public final String getSchedMaintColor(){
        String ret = "";
        if ("Scheduled Maint. Bypass Request".equals(processTemplateNm)){
            ret = " background-color:#FF5050;";
        } else {
            ret = " background-color:#FFFFFF;";
        }
        return ret;
    }

	public String getCmpId() {
		return cmpId;
	}

	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}

	public String getCmpName() {
		return cmpName;
	}

	public void setCmpName(String cmpName) {
		this.cmpName = cmpName;
	}
	
    public Integer getPermitId() {
		return permitId;
	}

	public void setPermitId(Integer permitId) {
		this.permitId = permitId;
	}
    
}
