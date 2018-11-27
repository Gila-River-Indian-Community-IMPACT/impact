package us.oh.state.epa.stars2.database.dbObjects.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.workflow.TimeSpan;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

@SuppressWarnings("serial")
public class PermitSOPData extends BaseDB {

    public static final Integer OK = 0;
    public static final Integer WARNING = 1;
    public static final Integer DANGER = 2;

    private String facilityId;
    private String facilityNm;
    private String permitClassCd;
    private String naics;
    private String operatingStatusCd;

    private Integer permitId;
    private Integer processId;
    private Long days;
    private Long referredDays;
    private Long pcrRdays;
    private String activityName;
    private boolean current;
    private String permitGlobalStatusCd;
    private String permitType;
    private Timestamp draftIssueDate;
    private Timestamp draftCommentEndDate;
    private String reason;
    private String general;
    private String issueDraft;
    private String express;
    private String rush;
    private String activityStatusCd;
    private String doLaaName;
    private String doLaaCd;
    private String doLaaShortDsc;
    private String doLaaId;
    private Timestamp startDt;
    private Timestamp dueDt;
    private Timestamp taskStartDt;
    private Timestamp taskEndDt;
    private String extendProcessEndDate;
    private Integer status;
    private Integer fpId;
    private String statusStr;
    private String permitNumber;
    private String metPCReviewSLA;
    private Integer activityReferralTypeId;
    
    private Integer userId;
    private Integer reviewerId;
    private Integer validPermitEuCount; // EUs not shutdown or invalid
    private Integer totalPermitEuCount; // All EUs associated with permit (regardless of operating status)
    private Integer permitEuCount;      // EU count displayed in table
    private String notes;
    
    private Timestamp applicationReceivedDt;
    private Timestamp renewalReceivedDt;
    private transient boolean exclude = false;

    public PermitSOPData() {
        pcrRdays = new Long(0);
        days = new Long(0);
        referredDays = new Long(0);
    }
    
    public final void populate(ResultSet rs) {
        try {
            setPermitId(rs.getInt("permit_id"));
            setProcessId(rs.getInt("process_id"));
            setUserId(rs.getInt("user_id"));
            setActivityName(rs.getString("activity_template_nm"));
            setPermitGlobalStatusCd(rs.getString("permit_global_status_cd"));
            setPermitType(rs.getString("permit_type_cd"));
            setReason(rs.getString("reason_dsc"));
            setGeneral(rs.getString("general_permit_flag"));
            setIssueDraft(rs.getString("issue_draft_flag"));
            setExpress(rs.getString("express_flag"));
            setActivityStatusCd(rs.getString("activity_status_cd"));
            setDoLaaName(rs.getString("do_laa_dsc"));
            setDoLaaShortDsc(rs.getString("do_laa_short_dsc"));
            setDoLaaCd(rs.getString("do_laa_cd"));
            setDoLaaId(rs.getString("do_laa_id"));
            setStartDt(rs.getTimestamp("start_dt"));
            setFacilityId(rs.getString("facility_id"));
            setPermitNumber(rs.getString("permit_nbr"));
            setFacilityNm(rs.getString("facility_nm"));
            setTaskStartDt(rs.getTimestamp("task_start_dt"));
            setTaskEndDt(rs.getTimestamp("task_end_dt"));
            setExtendProcessEndDate(rs.getString("extend_process_end_date"));
            setFpId(rs.getInt("fp_id"));
            setDraftIssueDate(rs.getTimestamp("draft_issuance_date"));
            setDraftCommentEndDate(rs.getTimestamp("draft_comment_end_date"));
            setCurrent(AbstractDAO.translateIndicatorToBoolean(rs.getString("IS_CURRENT")));
            setValidPermitEuCount(AbstractDAO.getInteger(rs, "valid_permit_eu_count"));
            setTotalPermitEuCount(AbstractDAO.getInteger(rs, "total_permit_eu_count"));
            setPermitClassCd(rs.getString("permit_classification_cd"));
            setNaics(rs.getString("naics"));
            setOperatingStatusCd(rs.getString("operating_status_cd"));
            setApplicationReceivedDt(rs.getTimestamp("application_received_date"));
            setRenewalReceivedDt(rs.getTimestamp("renewal_received_date"));
            setActivityReferralTypeId(AbstractDAO.getInteger(rs,
                    "ACTIVITY_REFERRAL_TYPE_ID"));
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
    }


    public final Integer getPermitId() {
        return permitId;
    }

    public final void setPermitId(Integer permitId) {
        this.permitId = permitId;
    }

    public final String getActivityName() {
        return activityName;
    }

    public final void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public final String getPermitGlobalStatusCd() {
        String ret = "N";
        if (permitGlobalStatusCd != null)
            ret = permitGlobalStatusCd;

        return ret;
    }

    public final void setPermitGlobalStatusCd(String permitGlobalStatusCd) {
        this.permitGlobalStatusCd = permitGlobalStatusCd;
    }

    public final String getGeneral() {
        String ret = "N";
        if (general != null)
            ret = general;

        return ret;
    }

    public final void setGeneral(String general) {
        this.general = general;
    }

    public final String getIssueDraft() {
        String ret = "N";
        if (issueDraft != null)
            ret = issueDraft;

        return ret;
    }

    public final void setIssueDraft(String issueDraft) {
        this.issueDraft = issueDraft;
    }

    public final String getReason() {
        String ret = "NA";
        if (reason != null) {
            ret = reason;
        }

        return ret;
    }

    public final void setReason(String reason) {
        this.reason = reason;
    }

    public final String getPermitType() {
        return permitType;
    }

    public final void setPermitType(String permitType) {
        this.permitType = permitType;
    }

    public final String getActivityStatusCd() {
        return activityStatusCd;
    }

    public final void setActivityStatusCd(String activityStatusCd) {
        this.activityStatusCd = activityStatusCd;
    }

    public final String getDoLaaName() {
        return doLaaName;
    }

    public final void setDoLaaName(String doLaaName) {
        this.doLaaName = doLaaName;
    }

    public final String getDoLaaCd() {
        return doLaaCd;
    }

    public final void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    public final String getDoLaaId() {
        return doLaaId;
    }

    public final void setDoLaaId(String doLaaId) {
        this.doLaaId = doLaaId;
    }

    public final Timestamp getStartDt() {
        return startDt;
    }

    public final void setStartDt(Timestamp startDt) {
        this.startDt = startDt;
    }

    public final String getExpress() {
        String ret = "N";
        if (express != null) {
            ret = express;
        }

        return ret;
    }

    public final void setExpress(String express) {
        this.express = express;
    }

    public final String getRush() {
        String ret = "N";
        if (rush != null) {
            ret = rush;
        }

        return ret;
    }

    public final void setRush(String rush) {
        this.rush = rush;
    }

    public final Integer getStatus() {
        return status;
    }

    public final void setStatus(Integer status) {
        this.status = status;
        if (status == OK) {
            statusStr = "OK";
        } else if (status == WARNING) {
            statusStr = "Warning";
        } else if (status == DANGER) {
            statusStr = "Danger";
        }
    }

    public final String getStatusStr() {
        return statusStr;
    }

    public final void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public final Integer getProcessId() {
        return processId;
    }

    public final void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public final String getFacilityId() {
        return facilityId;
    }

    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public final String getFacilityNm() {
        return facilityNm;
    }

    public final void setFacilityNm(String facilityNm) {
        this.facilityNm = facilityNm;
    }

    public final Integer getFpId() {
        return fpId;
    }

    public final void setFpId(Integer fpId) {
        this.fpId = fpId;
    }

    public final String getPermitNumber() {
        return permitNumber;
    }

    public final void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }

    public final Long getDays() {
        return days;
    }

    public final void setDays(Long days) {
        this.days = days;
    }

    public final Long getReferredDays() {
        return referredDays;
    }

    public final void setReferredDays(Long referredDays) {
        this.referredDays = referredDays;
    }

    public final Timestamp getDueDt() {
        return dueDt;
    }

    public final void setDueDt(Timestamp dueDt) {
        this.dueDt = dueDt;
    }

    public final String getExtendProcessEndDate() {
        return extendProcessEndDate;
    }

    public final void setExtendProcessEndDate(String extendProcessEndDate) {
        this.extendProcessEndDate = extendProcessEndDate;
    }

    public final Timestamp getTaskEndDt() {
        return taskEndDt;
    }

    public final void setTaskEndDt(Timestamp taskEndDt) {
        this.taskEndDt = taskEndDt;
    }

    public final Timestamp getTaskStartDt() {
        return taskStartDt;
    }

    public final void setTaskStartDt(Timestamp taskStartDt) {
        this.taskStartDt = taskStartDt;
    }

    public final String getMetPCReviewSLA() {
        return metPCReviewSLA;
    }

    public final void setMetPCReviewSLA(String metPCReviewSLA) {
        this.metPCReviewSLA = metPCReviewSLA;
    }

    public final Long getPcrRdays() {
        return pcrRdays;
    }

    public final void setPcrRdays(Long pcrRdays) {
        this.pcrRdays = pcrRdays;
    }

    /**
     * @return the epaDays
     */
    public final Long getEpaDays() {
        return days-referredDays;
    }

    /**
     * @return the activityDuration
     */
    public final Integer getActivityDuration() {
        return new TimeSpan(getTaskEndDt(), getTaskStartDt()).getDays();
    }

    /**
     * @return the reviewerId
     */
    public final Integer getReviewerId() {
        return reviewerId;
    }

    /**
     * @param reviewerId the reviewerId to set
     */
    public final void setReviewerId(Integer reviewerId) {
        this.reviewerId = reviewerId;
    }

    /**
     * @return the userId
     */
    public final Integer getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public final void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public final String getUserName() {
        return BasicUsersDef.getUserNm(getUserId());
    }
    
    public final String getReviewerName() {
        return BasicUsersDef.getUserNm(getReviewerId());
    }
    
    public final String getActivityStatusDesc() {
        return ActivityStatusDef.getData().getItems().getItemDesc(getActivityStatusCd());
    }

    /**
     * @return the current
     */
    public final boolean isCurrent() {
        return current;
    }

    /**
     * @param current the current to set
     */
    public final void setCurrent(boolean current) {
        this.current = current;
    }

    /**
     * @return the draftIssueDate
     */
    public final Timestamp getDraftIssueDate() {
        return draftIssueDate;
    }

    /**
     * @param draftIssueDate the draftIssueDate to set
     */
    public final void setDraftIssueDate(Timestamp draftIssueDate) {
        this.draftIssueDate = draftIssueDate;
    }

	public final Integer getValidPermitEuCount() {
		return validPermitEuCount;
	}

	public final void setValidPermitEuCount(Integer validPermitEuCount) {
		this.validPermitEuCount = validPermitEuCount;
	}

	public final Integer getTotalPermitEuCount() {
		return totalPermitEuCount;
	}

	public final void setTotalPermitEuCount(Integer totalPermitEuCount) {
		this.totalPermitEuCount = totalPermitEuCount;
	}

	public final Integer getPermitEuCount() {
		return permitEuCount;
	}

	public final void setPermitEuCount(Integer permitEuCount) {
		this.permitEuCount = permitEuCount;
	}
    

    /**
     * @return the notes
     */
    public final String getNotes() {
        return notes;
    }

    /**
     * @param notes the notes to set
     */
    public final void setNotes(String notes) {
        this.notes = notes;
    }

    public void addNote(String note) {
        if (notes == null)
            notes = new String();
        notes = notes + note;
    }

	public final String getPermitClassCd() {
		return permitClassCd;
	}

	public final void setPermitClassCd(String permitClassCd) {
		this.permitClassCd = permitClassCd;
	}

	public final String getNaics() {
		return naics;
	}

	public final void setNaics(String naics) {
		this.naics = naics;
	}

	public final String getOperatingStatusCd() {
		return operatingStatusCd;
	}

	public final void setOperatingStatusCd(String operatingStatusCd) {
		this.operatingStatusCd = operatingStatusCd;
	}

	public final Timestamp getApplicationReceivedDt() {
		return applicationReceivedDt;
	}

	public final void setApplicationReceivedDt(Timestamp applicationReceivedDt) {
		this.applicationReceivedDt = applicationReceivedDt;
	}

	public final Timestamp getRenewalReceivedDt() {
		return renewalReceivedDt;
	}

	public final void setRenewalReceivedDt(Timestamp renewalReceivedDt) {
		this.renewalReceivedDt = renewalReceivedDt;
	}

    public Timestamp getDraftCommentEndDate() {
        return draftCommentEndDate;
    }

    public void setDraftCommentEndDate(Timestamp draftCommentEndDate) {
        this.draftCommentEndDate = draftCommentEndDate;
    }

    public String getDoLaaShortDsc() {
        return doLaaShortDsc;
    }

    public void setDoLaaShortDsc(String doLaaShortDsc) {
        this.doLaaShortDsc = doLaaShortDsc;
    }

	public Integer getActivityReferralTypeId() {
		return activityReferralTypeId;
	}

	public void setActivityReferralTypeId(Integer activityReferralTypeId) {
		this.activityReferralTypeId = activityReferralTypeId;
	}
	
	public boolean isExclude() {
		return exclude;
	}

	public void setExclude(boolean exclude) {
		this.exclude = exclude;
	}
}
