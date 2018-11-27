package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.WordUtils;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.workflow.TimeSpan;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.IssuanceStatusDef;
import us.oh.state.epa.stars2.def.PermitIssuanceTypeDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;

/**
 * PermitActivitySearch.
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version $Revision: 1.8 $
 * @author $Author: dleinbaugh $
 */
@SuppressWarnings("serial")
public class PermitActivitySearch extends BaseDB {
    // Search Criteria
    private String facilityId;
    private String facilityNm;
    private String doLaaCd;
    private Integer userId;
    private String processTemplateNm;
    private String activityTemplateNm;
    private String activityStatusCd;
    private boolean looped;
    private Integer permitID;
    private String permitNumber;
    private String permitTypeCd;
    private String permitReason;
    private String description;
    private String permitGlobalStatusCd;
    private String dateBy;
    private Timestamp beginDt;
    private Timestamp endDt;
    private String applicationNumber;
    private String processCd;
    
    // table
    private Integer fpId;
    private Integer activityTemplateId;
    private Integer processId;
    private Integer loopCnt;
    private boolean viewed;
    private String aggregate;
    private TimeSpan duration;
    private Timestamp startDt;
    private Timestamp dueDt;
    private String status;
    private String statusColor;
    private Timestamp processStartDt;
    private Timestamp processEndDt;
    private Timestamp processJeoDt;
    private TimeSpan processDuration;
    private Timestamp processDueDt;
    private List<String> permitReasonCDs;
    private String expedited;
    private String naicsCd;
    
    private Timestamp effectiveDate;
    private Timestamp expirationDate;
    private LinkedHashMap<String, PermitIssuance> permitIssuances = new LinkedHashMap<String, PermitIssuance>(4);
    private String reasons;
    private Timestamp permitSentOutDate; // for NSR permits only
    
    private String cmpId;
    private String cmpName;
    
    
    /**
     * 
     */
    public PermitActivitySearch() {
        super();
        looped = false;
    }

    /**
     * @param old
     */
    public PermitActivitySearch(PermitActivitySearch old) {
        super(old);
        if (old != null) {
            setActivityTemplateId(old.getActivityTemplateId());
            setActivityStatusCd(old.getActivityStatusCd());
            setActivityTemplateNm(old.getActivityTemplateNm());
            setAggregate(old.getAggregate());
            setDueDt(old.getDueDt());
            setDuration(old.getDuration());
            setEndDt(old.getEndDt());
            setExpedited(old.getExpedited());
            setViewed(old.isViewed());
            setFacilityId(old.getFacilityId());
            setFacilityNm(old.getFacilityNm());
            setDoLaaCd(old.getDoLaaCd());
            setFpId(old.getFpId());
            setLoopCnt(old.getLoopCnt());
            setProcessDueDt(old.getProcessDueDt());
            setProcessDuration(old.getProcessDuration());
            setProcessId(old.getProcessId());
            setProcessStartDt(old.getProcessStartDt());
            setProcessTemplateNm(old.getProcessTemplateNm());
            setSelected(old.isSelected());
            setStatus(old.getStatus());
            setStatusColor(old.getStatusColor());
            setUserId(old.getUserId());
            setPermitID(old.getPermitID());
            setPermitGlobalStatusCd(old.getPermitGlobalStatusCd());
            setEffectiveDate(old.getEffectiveDate());
            setExpirationDate(old.getExpirationDate());
            setPermitIssuances(old.getPermitIssuances());
            setPermitSentOutDate(old.getPermitSentOutDate());
            setCmpId(old.getCmpId());
            setCmpName(old.getCmpName());
        }
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
            if (getStartDt() != null) {
                setDuration(new TimeSpan(getEndDt(), getStartDt()));
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
            setProcessCd(rs.getString("PROCESS_CD"));
            setProcessStartDt(rs.getTimestamp("P_Start_Dt"));
            setProcessDueDt(rs.getTimestamp("P_Due_Dt"));
            setProcessJeoDt(rs.getTimestamp("P_Jeopardy_Dt"));
            setProcessEndDt(rs.getTimestamp("P_End_Dt"));
            setStatus(WorkFlowProcess.checkStatus(getProcessJeoDt(), getProcessDueDt(),
                    getProcessEndDt()));
            setStatusColor(WorkFlowProcess.checkStatusColor(getStatus()));

            if (getProcessStartDt() != null) {
                setProcessDuration(new TimeSpan(getProcessEndDt(),
                        getProcessStartDt()));
            }
          /*  String pr = rs.getString("RUSH_FLAG");
            if (pr != null)
                setExpedited((pr.equalsIgnoreCase("Y") ? "Yes" : "No"));*/
            setPermitID(AbstractDAO.getInteger(rs, "permit_id"));
            setPermitNumber(rs.getString("PERMIT_NBR"));
            setPermitTypeCd(rs.getString("PERMIT_TYPE_CD"));
            setReasons(rs.getString("all_reasons"));
            setDescription(rs.getString("description"));
            ArrayList<String> allReasons = new ArrayList<String>();
            if (reasons != null && reasons.length() > 0) {
                StringTokenizer str = new StringTokenizer(reasons);
                while (str.hasMoreTokens()) {
                    allReasons.add(str.nextToken());
                }
                setPermitReasonCDs(allReasons);
            }
            setApplicationNumber(rs.getString("all_apps"));
            setPermitGlobalStatusCd(rs.getString("permit_global_status_cd"));
            setEffectiveDate(rs.getTimestamp("effective_date"));
            setExpirationDate(rs.getTimestamp("expiration_date"));
            setDraftIssueDate(rs.getTimestamp("Draft_Issuance_date"));
            setFinalIssueDate(rs.getTimestamp("Final_Issuance_date"));
            setPpIssueDate(rs.getTimestamp("pp_issuance_date"));
            //setPppIssueDate(rs.getTimestamp("ppp_issuance_date"));
            setPublicNoticePublishDate(rs.getTimestamp("public_notice_publish_date"));
            setPermitSentOutDate(rs.getTimestamp("permit_sent_out_date"));
            setNaicsCd(rs.getString("naics_cd"));
            setCmpId(rs.getString("cmp_id"));
            setCmpName(rs.getString("cmp_name"));
        } catch (SQLException sqle) {
            logger.debug(sqle.getMessage());
        }
    }
    
    // ------------------------------------------
    // userId
    // ------------------------------------------

    /**
     * UserId
     * 
     * @return Integer
     */
    public final Integer getUserId() {
        return userId;
    }

    /**
     * UserId
     * 
     * @param userId
     */
    public final void setUserId(Integer userId) {
        if (this.userId != null && !this.userId.equals(userId)) {
            setSelected(true);
        }
        this.userId = userId;
    }

    public final int compareTo(Object o) {
        if (o instanceof PermitActivitySearch) {
            return this.activityTemplateNm.compareTo(((PermitActivitySearch) o)
                    .getActivityTemplateNm());
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
        result = PRIME
                * result
                + ((activityTemplateNm == null) ? 0 : activityTemplateNm
                        .hashCode());
        result = PRIME * result
                + ((aggregate == null) ? 0 : aggregate.hashCode());
        result = PRIME * result + ((dueDt == null) ? 0 : dueDt.hashCode());
        result = PRIME * result + ((endDt == null) ? 0 : endDt.hashCode());
        result = PRIME * result
                + ((facilityId == null) ? 0 : facilityId.hashCode());
        result = PRIME * result
                + ((facilityNm == null) ? 0 : facilityNm.hashCode());
        result = PRIME * result + ((loopCnt == null) ? 0 : loopCnt.hashCode());
        result = PRIME * result
                + ((processCd == null) ? 0 : processCd.hashCode());
        result = PRIME * result
                + ((processDueDt == null) ? 0 : processDueDt.hashCode());
        result = PRIME * result
                + ((processEndDt == null) ? 0 : processEndDt.hashCode());
        result = PRIME * result
                + ((processId == null) ? 0 : processId.hashCode());
        result = PRIME * result
                + ((processJeoDt == null) ? 0 : processJeoDt.hashCode());
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
        result = PRIME * result + ((permitSentOutDate == null) ? 0 : permitSentOutDate.hashCode());
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
        final PermitActivitySearch other = (PermitActivitySearch) obj;
        if (activityStatusCd == null) {
            if (other.activityStatusCd != null)
                return false;
        } else if (!activityStatusCd.equals(other.activityStatusCd))
            return false;
        if (activityTemplateId == null) {
            if (other.activityTemplateId != null)
                return false;
        } else if (!activityTemplateId.equals(other.activityTemplateId))
            return false;
        if (activityTemplateNm == null) {
            if (other.activityTemplateNm != null)
                return false;
        } else if (!activityTemplateNm.equals(other.activityTemplateNm))
            return false;
        if (aggregate == null) {
            if (other.aggregate != null)
                return false;
        } else if (!aggregate.equals(other.aggregate))
            return false;
        if (dueDt == null) {
            if (other.dueDt != null)
                return false;
        } else if (!dueDt.equals(other.dueDt))
            return false;
        if (endDt == null) {
            if (other.endDt != null)
                return false;
        } else if (!endDt.equals(other.endDt))
            return false;
        if (expedited != other.expedited)
            return false;
        if (facilityId == null) {
            if (other.facilityId != null)
                return false;
        } else if (!facilityId.equals(other.facilityId))
            return false;
        if (facilityNm == null) {
            if (other.facilityNm != null)
                return false;
        } else if (!facilityNm.equals(other.facilityNm))
            return false;
        if (loopCnt == null) {
            if (other.loopCnt != null)
                return false;
        } else if (!loopCnt.equals(other.loopCnt))
            return false;
        if (processCd == null) {
            if (other.processCd != null)
                return false;
        } else if (!processCd.equals(other.processCd))
            return false;
        if (processDueDt == null) {
            if (other.processDueDt != null)
                return false;
        } else if (!processDueDt.equals(other.processDueDt))
            return false;
        if (processEndDt == null) {
            if (other.processEndDt != null)
                return false;
        } else if (!processEndDt.equals(other.processEndDt))
            return false;
        if (processId == null) {
            if (other.processId != null)
                return false;
        } else if (!processId.equals(other.processId))
            return false;
        if (processJeoDt == null) {
            if (other.processJeoDt != null)
                return false;
        } else if (!processJeoDt.equals(other.processJeoDt))
            return false;
        if (processStartDt == null) {
            if (other.processStartDt != null)
                return false;
        } else if (!processStartDt.equals(other.processStartDt))
            return false;
        if (processTemplateNm == null) {
            if (other.processTemplateNm != null)
                return false;
        } else if (!processTemplateNm.equals(other.processTemplateNm))
            return false;
        if (startDt == null) {
            if (other.startDt != null)
                return false;
        } else if (!startDt.equals(other.startDt))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (statusColor == null) {
            if (other.statusColor != null)
                return false;
        } else if (!statusColor.equals(other.statusColor))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        if (viewed != other.viewed)
            return false;
        if(permitSentOutDate == null) {
        	if(other.permitSentOutDate != null)
        		return false;
        } else if(!permitSentOutDate.equals(other.permitSentOutDate))
        	return false;
        
        return true;
    }

    public String toIds() {
        String ret = " getFacilityId() " + getFacilityId() 
            + " getFacilityNm " + getFacilityNm()
            + " getUserId() " + getUserId()
            + " getActivityStatusCd() " + getActivityStatusCd()
            + " getActivityTemplateNm() " + getActivityTemplateNm()
            + " getProcessCd() " + getProcessCd()
            + " getProcessId() " + getProcessId()
            + " getProcessTemplateNm() " + getProcessTemplateNm()
            + " getActivityTemplateId() " + getActivityTemplateId()
            + " getAggregate() " + getAggregate()
            + " getLoopCnt() " + getLoopCnt()
            + " getEndDt() " + getEndDt();
        return ret;
    }
    
    public final void setDraftIssueDate(Timestamp issuanceDate) {
        setIssuanceDate(issuanceDate, PermitIssuanceTypeDef.Draft);
        setDirty(true);
    }

    public final Timestamp getDraftIssueDate() {
        return getIssueDate(PermitIssuanceTypeDef.Draft);
    }
    
    public final void setFinalIssueDate(Timestamp issuanceDate) {
        setIssuanceDate(issuanceDate, PermitIssuanceTypeDef.Final);
        setDirty(true);
    }
    
    public final Timestamp getFinalIssueDate() {
        return getIssueDate(PermitIssuanceTypeDef.Final);
    }
    
    public final void setPpIssueDate(Timestamp issuanceDate) {
        setIssuanceDate(issuanceDate, PermitIssuanceTypeDef.PP);
        setDirty(true);
    }

    public final Timestamp getPpIssueDate() {
        return getIssueDate(PermitIssuanceTypeDef.PP);
    }
/*
    public final void setPppIssueDate(Timestamp issuanceDate) {
        setIssuanceDate(issuanceDate, PermitIssuanceTypeDef.PPP);
        setDirty(true);
    }

    public final Timestamp getPppIssueDate() {
        return getIssueDate(PermitIssuanceTypeDef.PPP);
    }
*/
    protected void setIssuanceDate(Timestamp issuanceDate, String type) {
        PermitIssuance pi = permitIssuances.get(type);
        if (pi != null) {
            pi.setIssuanceDate(issuanceDate);
        }
        else {
            pi = new PermitIssuance(getPermitID(), getPermitNumber(), type,
                                    IssuanceStatusDef.NOT_READY);
            pi.setIssuanceDate(issuanceDate);
            permitIssuances.put(type, pi);
        }
        setDirty(true);
    }
    
    protected Timestamp getIssueDate(String type) {
        PermitIssuance pi = permitIssuances.get(type);
        if (pi != null) {
            return pi.getIssuanceDate();
        }
        return null;
    }
    
    public final String getActivityStatusDesc() {
        return ActivityStatusDef.getData().getItems().getItemDesc(getActivityStatusCd());
    }

    public final String getDurationColor(){
        String ret = "";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (getDueDt() != null && getDueDt().before(now))
            ret = " background-color:#" + WorkFlowProcess.STATUS_LATE_COLOR_STRING + ";";
        return ret;
    }
    
    public final String getPermitTypeDesc() {
        String ret = "";
        if (getPermitTypeCd() != null)
            ret = PermitTypeDef.getData().getItems().getItemDesc(getPermitTypeCd());
        return ret;
    }

    public final String getUserName() {
        return WordUtils.capitalize(BasicUsersDef.getUserNm(getUserId()));
    }
    
    /**
     * @return the activityStatusCd
     */
    public final String getActivityStatusCd() {
        return activityStatusCd;
    }

    /**
     * @param activityStatusCd the activityStatusCd to set
     */
    public final void setActivityStatusCd(String activityStatusCd) {
        this.activityStatusCd = activityStatusCd;
    }

    /**
     * @return the activityTemplateId
     */
    public final Integer getActivityTemplateId() {
        return activityTemplateId;
    }

    /**
     * @param activityTemplateId the activityTemplateId to set
     */
    public final void setActivityTemplateId(Integer activityTemplateId) {
        this.activityTemplateId = activityTemplateId;
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
     * @return the aggregate
     */
    public final String getAggregate() {
        return aggregate;
    }

    /**
     * @param aggregate the aggregate to set
     */
    public final void setAggregate(String aggregate) {
        this.aggregate = aggregate;
    }

    /**
     * @return the applicationNumber
     */
    public final String getApplicationNumber() {
        return applicationNumber;
    }

    /**
     * @param applicationNumber the applicationNumber to set
     */
    public final void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    /**
     * @return the beginDt
     */
    public final Timestamp getBeginDt() {
        return beginDt;
    }

    /**
     * @param beginDt the beginDt to set
     */
    public final void setBeginDt(Timestamp beginDt) {
        this.beginDt = beginDt;
    }

    /**
     * @return the dateBy
     */
    public final String getDateBy() {
        return dateBy;
    }

    /**
     * @param dateBy the dateBy to set
     */
    public final void setDateBy(String dateBy) {
        this.dateBy = dateBy;
    }

    /**
     * @return the duration
     */
    public final TimeSpan getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public final void setDuration(TimeSpan duration) {
        this.duration = duration;
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
     * @return the expedited
     */
    public final String getExpedited() {
        return expedited;
    }

    /**
     * @param expedited the expedited to set
     */
    public final void setExpedited(String expedited) {
        this.expedited = expedited;
    }

    /**
     * @return the facilityId
     */
    public final String getFacilityId() {
        return facilityId;
    }

    /**
     * @param facilityId the facilityId to set
     */
    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    /**
     * @return the facilityNm
     */
    public final String getFacilityNm() {
        return facilityNm;
    }

    /**
     * @param facilityNm the facilityNm to set
     */
    public final void setFacilityNm(String facilityNm) {
        this.facilityNm = facilityNm;
    }

    /**
     * @return the loopCnt
     */
    public final Integer getLoopCnt() {
        return loopCnt;
    }

    /**
     * @param loopCnt the loopCnt to set
     */
    public final void setLoopCnt(Integer loopCnt) {
        this.loopCnt = loopCnt;
    }

    /**
     * @return the looped
     */
    public final boolean isLooped() {
        return looped;
    }

    /**
     * @param looped the looped to set
     */
    public final void setLooped(boolean looped) {
        this.looped = looped;
    }

    /**
     * @return the permitGlobalStatusCd
     */
    public final String getPermitGlobalStatusCd() {
        return permitGlobalStatusCd;
    }

    /**
     * @param permitGlobalStatusCd the permitGlobalStatusCd to set
     */
    public final void setPermitGlobalStatusCd(String permitGlobalStatusCd) {
        this.permitGlobalStatusCd = permitGlobalStatusCd;
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
     * @return the permitReason
     */
    public final String getPermitReason() {
        return permitReason;
    }

    /**
     * @param permitReason the permitReason to set
     */
    public final void setPermitReason(String permitReason) {
        this.permitReason = permitReason;
    }

    public final String getDescription() {
		return description;
	}

	public final void setDescription(String description) {
		this.description = description;
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

    /**
     * @return the processDueDt
     */
    public final Timestamp getProcessDueDt() {
        return processDueDt;
    }

    /**
     * @param processDueDt the processDueDt to set
     */
    public final void setProcessDueDt(Timestamp processDueDt) {
        this.processDueDt = processDueDt;
    }

    /**
     * @return the processDuration
     */
    public final TimeSpan getProcessDuration() {
        return processDuration;
    }

    /**
     * @param processDuration the processDuration to set
     */
    public final void setProcessDuration(TimeSpan processDuration) {
        this.processDuration = processDuration;
    }

    /**
     * @return the processId
     */
    public final Integer getProcessId() {
        return processId;
    }

    /**
     * @param processId the processId to set
     */
    public final void setProcessId(Integer processId) {
        this.processId = processId;
    }

    /**
     * @return the processStartDt
     */
    public final Timestamp getProcessStartDt() {
        return processStartDt;
    }

    /**
     * @param processStartDt the processStartDt to set
     */
    public final void setProcessStartDt(Timestamp processStartDt) {
        this.processStartDt = processStartDt;
    }

    /**
     * @return the processTemplateNm
     */
    public final String getProcessTemplateNm() {
        return processTemplateNm;
    }

    /**
     * @param processTemplateNm the processTemplateNm to set
     */
    public final void setProcessTemplateNm(String processTemplateNm) {
        this.processTemplateNm = processTemplateNm;
    }

    /**
     * @return the status
     */
    public final String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public final void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the statusColor
     */
    public final String getStatusColor() {
        return statusColor;
    }

    /**
     * @param statusColor the statusColor to set
     */
    public final void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }

    /**
     * @return the viewed
     */
    public final boolean isViewed() {
        return viewed;
    }

    /**
     * @param viewed the viewed to set
     */
    public final void setViewed(boolean viewed) {
        this.viewed = viewed;
    }
    

    /**
     * @return the dueDt
     */
    public final Timestamp getDueDt() {
        return dueDt;
    }

    /**
     * @param dueDt the dueDt to set
     */
    public final void setDueDt(Timestamp dueDt) {
        this.dueDt = dueDt;
    }

    /**
     * @return the startDt
     */
    public final Timestamp getStartDt() {
        return startDt;
    }

    /**
     * @param startDt the startDt to set
     */
    public final void setStartDt(Timestamp startDt) {
        this.startDt = startDt;
    }

    /**
     * @return the processCd
     */
    public final String getProcessCd() {
        return processCd;
    }

    /**
     * @param processCd the processCd to set
     */
    public final void setProcessCd(String processCd) {
        this.processCd = processCd;
    }

    public final String getNaicsCd() {
		return naicsCd;
	}

	public final void setNaicsCd(String naicsCd) {
		this.naicsCd = naicsCd;
	}

	/**
     * @return the processEndDt
     */
    public final Timestamp getProcessEndDt() {
        return processEndDt;
    }

    /**
     * @param processEndDt the processEndDt to set
     */
    public final void setProcessEndDt(Timestamp processEndDt) {
        this.processEndDt = processEndDt;
    }

    /**
     * @return the processJeoDt
     */
    public final Timestamp getProcessJeoDt() {
        return processJeoDt;
    }

    /**
     * @param processJeoDt the processJeoDt to set
     */
    public final void setProcessJeoDt(Timestamp processJeoDt) {
        this.processJeoDt = processJeoDt;
    }

    /**
     * @return the effectiveDate
     */
    public final Timestamp getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * @param effectiveDate the effectiveDate to set
     */
    public final void setEffectiveDate(Timestamp effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    /**
     * @return the expirationDate
     */
    public final Timestamp getExpirationDate() {
        return expirationDate;
    }

    /**
     * @param expirationDate the expirationDate to set
     */
    public final void setExpirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * @return the permitID
     */
    public final Integer getPermitID() {
        return permitID;
    }

    /**
     * @param permitID the permitID to set
     */
    public final void setPermitID(Integer permitID) {
        this.permitID = permitID;
    }

    /**
     * @return the permitIssuances
     */
    public final LinkedHashMap<String, PermitIssuance> getPermitIssuances() {
        return permitIssuances;
    }

    /**
     * @param permitIssuances the permitIssuances to set
     */
    public final void setPermitIssuances(
            LinkedHashMap<String, PermitIssuance> permitIssuances) {
        this.permitIssuances = permitIssuances;
    }

    /**
     * @return the reasons
     */
    public final String getReasons() {
        return reasons;
    }

    /**
     * @param reasons the reasons to set
     */
    public final void setReasons(String reasons) {
        this.reasons = reasons;
    }

    /**
     * @return the fpId
     */
    public final Integer getFpId() {
        return fpId;
    }

    /**
     * @param fpId the fpId to set
     */
    public final void setFpId(Integer fpId) {
        this.fpId = fpId;
    }

    public String getDoLaaCd() {
        return doLaaCd;
    }

    public void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }
    
	public final Timestamp getPublicNoticePublishDate() {
		return getPublicNoticePublishDate(PermitIssuanceTypeDef.Draft);
	}

	protected Timestamp getPublicNoticePublishDate(String type) {
		PermitIssuance pi = permitIssuances.get(type);
		if (pi != null) {
			return pi.getPublicNoticePublishDate();
		}
		return null;
	}
	
	public final void setPublicNoticePublishDate(Timestamp publishDate) {
		setPublicNoticeDate(publishDate, PermitIssuanceTypeDef.Draft);
		setDirty(true);
	}

	protected void setPublicNoticeDate(Timestamp publishDate, String type) {
		PermitIssuance pi = permitIssuances.get(type);
		if (pi != null) {
			pi.setPublicNoticePublishDate(publishDate);
		} else {
			pi = new PermitIssuance(getPermitID(), getPermitNumber(), type,
					IssuanceStatusDef.NOT_READY);
			pi.setPublicNoticePublishDate(publishDate);
			permitIssuances.put(type, pi);
		}
		setDirty(true);
	}
	
	public Timestamp getPermitSentOutDate() {
		return permitSentOutDate;
	}

	public void setPermitSentOutDate(Timestamp permitSentOutDate) {
		this.permitSentOutDate = permitSentOutDate;
		setDirty(true);
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
	
	public final List<String> getApplicationNumbers() {
		String[] applications = new String[1];

		List<String> appList = new ArrayList<String>();

		if (applicationNumber != null && applicationNumber.length() > 0) {

			if (applicationNumber.contains(" ")) {
				applications = applicationNumber.split(" ");
			} else {
				applications[0] = applicationNumber;
			}
			appList = new ArrayList<String>(Arrays.asList(applications));
		}

		return appList;
	}
}
