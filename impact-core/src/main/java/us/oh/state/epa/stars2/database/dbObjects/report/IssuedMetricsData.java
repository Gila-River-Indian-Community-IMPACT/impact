package us.oh.state.epa.stars2.database.dbObjects.report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.TimeSpan;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.PermitActionTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;

public class IssuedMetricsData extends BaseDB {

    private String facilityId;
    private String facilityNm;
    private String permitNumber;
    private String permitType;
    private String permitGlobalStatus;
    private String reason;
    private String general;
    private String rush;
    private Timestamp draftIssueDate;
    private Timestamp finalIssueDate;
    private Long preliminaryDays;
    private Long workflowDays;
    private Long dolaaDays;
    private Long coDays;
    private Long companyDays;
    private Long totalAgencyDays;
    private Long totalNonAgencyDays;
    private Long totalAgencyDaysForAnalysis;
    private Long totalNonAgencyDaysForAnalysis;
    
    private Timestamp startDt;
    private Timestamp endDt;

    private Integer permitId;
    private Integer processId;
    private Integer loopCnt;
    private String activityName;
    private String activityStatusCd;
    private String doLaaName;
    private String doLaaCd;
    private String doLaaShortDsc;
    private String doLaaId;
    private Timestamp taskStartDt;
    private Timestamp taskEndDt;
    private Integer fpId;
    private Integer activityReferralTypeId;
    
    private boolean current;
    private Integer userId;
    private Integer reviewerIdDOLAA;
    private Integer reviewerIdCO;
    private String notes;
    private Integer euCount;
    
    private String permitActionType;
	private String subjectToPsd;
	private String subjectToNansr;
	
	private boolean actionTypePermit;
	
	private String countyName;
	private Boolean extendProcessEndDate;
	
	private Timestamp completenessReviewStartDt;
    private Timestamp completenessReviewEndDt;
    private Timestamp publicNoticePublishDate;
    private Timestamp techReviewStartDt;
    private Timestamp managerReviewStartDt;
    
    private Integer permitCount;
    private Integer excessiveCount;
    private Integer excessivePreliminaryCount;
    private Double averageAqdDays;
    private Double averageIssuanceDays;
    
    private String descLabel;
    private Integer benchmarkDays;
    private Integer prelimBenchmarkDays;

    public IssuedMetricsData() {
        preliminaryDays = new Long(0);
        workflowDays = new Long(0);
        dolaaDays = new Long(0);
        coDays = new Long(0);
        companyDays = new Long(0);
        totalAgencyDays = new Long(0);
        totalNonAgencyDays = new Long(0);
        totalAgencyDaysForAnalysis = new Long(0);
        totalNonAgencyDaysForAnalysis = new Long(0);
        permitCount = new Integer(0);
        excessiveCount = new Integer(0);
        excessivePreliminaryCount = new Integer(0);
        averageAqdDays = new Double(0);
        averageIssuanceDays = new Double(0);
        benchmarkDays = new Integer(0);
        prelimBenchmarkDays = new Integer(0);
        
    }
    
    public final void populate(ResultSet rs) {
        try {
            setProcessId(AbstractDAO.getInteger(rs, "process_id"));
            setStartDt(rs.getTimestamp("start_dt"));
            setEndDt(rs.getTimestamp("END_DT"));
            setActivityName(rs.getString("activity_template_nm"));
            setTaskStartDt(rs.getTimestamp("task_start_dt"));
            setTaskEndDt(rs.getTimestamp("task_end_dt"));
            setActivityStatusCd(rs.getString("activity_status_cd"));
            setPermitId(AbstractDAO.getInteger(rs, "permit_id"));
            setPermitNumber(rs.getString("permit_nbr"));
            setPermitType(rs.getString("permit_type_cd"));
            setPermitActionType(rs.getString("action_type"));
            setSubjectToPsd(rs.getString("subject_to_psd_flag"));
            setSubjectToNansr(rs.getString("subject_to_nansr_flag"));
            setGeneral(rs.getString("general_permit_flag"));
            setReason(rs.getString("reason_dsc"));
            setFacilityId(rs.getString("facility_id"));
            setFacilityNm(rs.getString("facility_nm"));
            setFpId(AbstractDAO.getInteger(rs, "fp_id"));
            setActivityReferralTypeId(AbstractDAO.getInteger(rs, "ACTIVITY_REFERRAL_TYPE_ID"));
            setDoLaaName(rs.getString("do_laa_dsc"));
            setDoLaaCd(rs.getString("do_laa_cd"));
            setDoLaaShortDsc(rs.getString("do_laa_short_dsc"));
            setDoLaaId(rs.getString("do_laa_id"));
            setDraftIssueDate(rs.getTimestamp("draft_issuance_date"));
            setFinalIssueDate(rs.getTimestamp("final_issuance_date"));
            setPublicNoticePublishDate(rs.getTimestamp("public_notice_publish_date"));
            setWorkflowDays(new Long(new TimeSpan(getEndDt(), getStartDt()).getDays()));
            setCurrent(AbstractDAO.translateIndicatorToBoolean(rs.getString("IS_CURRENT")));
            setUserId(rs.getInt("user_id"));
            setLoopCnt(AbstractDAO.getInteger(rs, "loop_cnt"));
            setPermitGlobalStatus(rs.getString("permit_global_status_cd"));
            
            ProcessActivity pa = new ProcessActivity();
            pa.setActivityStatusCd(getActivityStatusCd());
            pa.setActivityReferralTypeId(getActivityReferralTypeId());
            pa.setLoopCnt(getLoopCnt());
            setEuCount(AbstractDAO.getInteger(rs, "eu_count"));
            setCountyName(rs.getString("county_nm"));
            setExtendProcessEndDate(
					AbstractDAO.translateIndicatorToBoolean(
							rs.getString("extend_process_end_date")));
            

        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
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

    public final String getGeneral() {
        String ret = "N";
        if (general != null)
            ret = general;

        return ret;
    }

    public final void setGeneral(String general) {
        this.general = general;
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

    /**
     * @return the activityReferralTypeId
     */
    public final Integer getActivityReferralTypeId() {
        return activityReferralTypeId;
    }

    /**
     * @param activityReferralTypeId the activityReferralTypeId to set
     */
    public final void setActivityReferralTypeId(Integer activityReferralTypeId) {
        this.activityReferralTypeId = activityReferralTypeId;
    }

    /**
     * @return the companyDays
     */
    public final Long getCompanyDays() {
        return companyDays;
    }

    /**
     * @param companyDays the companyDays to set
     */
    public final void setCompanyDays(Long companyDays) {
        this.companyDays = companyDays;
    }

    /**
     * @return the dolaaDays
     */
    public final Long getDolaaDays() {
        return dolaaDays;
    }

    /**
     * @param dolaaDays the dolaaDays to set
     */
    public final void setDolaaDays(Long dolaaDays) {
        this.dolaaDays = dolaaDays;
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

    /**
     * @return the finalIssueDate
     */
    public final Timestamp getFinalIssueDate() {
        return finalIssueDate;
    }

    /**
     * @param finalIssueDate the finalIssueDate to set
     */
    public final void setFinalIssueDate(Timestamp finalIssueDate) {
        this.finalIssueDate = finalIssueDate;
    }
    
    /**
     * @return the publicNoticePublishDate
     */
    public final Timestamp getPublicNoticePublishDate() {
        return publicNoticePublishDate;
    }

    /**
     * @param publicNoticePublishDate the publicNoticePublishDate to set
     */
    public final void setPublicNoticePublishDate(Timestamp publicNoticePublishDate) {
        this.publicNoticePublishDate = publicNoticePublishDate;
    }

    /**
     * @return the preliminaryDays
     */
    public final Long getPreliminaryDays() {
        return preliminaryDays;
    }

    /**
     * @param preliminaryDays the preliminaryDays to set
     */
    public final void setPreliminaryDays(Long preliminaryDays) {
        this.preliminaryDays = preliminaryDays;
    }

    /**
     * @return the workflowDays
     */
    public final Long getWorkflowDays() {
        return workflowDays;
    }

    /**
     * @param workflowDays the workflowDays to set
     */
    public final void setWorkflowDays(Long workflowDays) {
        this.workflowDays = workflowDays;
    }

    /**
     * @return the activityDuration
     */
    public final Integer getActivityDuration() {
        return new TimeSpan(getTaskEndDt(), getTaskStartDt()).getDays();
    }

    public final String getActivityStatusDesc() {
        return ActivityStatusDef.getData().getItems().getItemDesc(getActivityStatusCd());
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
     * @return the coDays
     */
    public final Long getCoDays() {
        return coDays;
    }

    /**
     * @param coDays the coDays to set
     */
    public final void setCoDays(Long coDays) {
        this.coDays = coDays;
    }
    
    /**
     * @return the coDays
     */
    public final Long getEpaDays() {
        return getCoDays()+getDolaaDays();
    }

    /**
     * @return the reviewerIdCO
     */
    public final Integer getReviewerIdCO() {
        return reviewerIdCO;
    }

    /**
     * @param reviewerIdCO the reviewerIdCO to set
     */
    public final void setReviewerIdCO(Integer reviewerIdCO) {
        this.reviewerIdCO = reviewerIdCO;
    }

    /**
     * @return the reviewerIdDOLAA
     */
    public final Integer getReviewerIdDOLAA() {
        return reviewerIdDOLAA;
    }

    /**
     * @param reviewerIdDOLAA the reviewerIdDOLAA to set
     */
    public final void setReviewerIdDOLAA(Integer reviewerIdDOLAA) {
        this.reviewerIdDOLAA = reviewerIdDOLAA;
    }
    
    public final String getReviewerNameCO() {
        return BasicUsersDef.getUserNm(getReviewerIdCO());
    }
    
    public final String getReviewerNameDOLAA() {
        return BasicUsersDef.getUserNm(getReviewerIdDOLAA());
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

    /**
     * @return the euCount
     */
    public final Integer getEuCount() {
        return euCount;
    }

    /**
     * @param euCount the euCount to set
     */
    public final void setEuCount(Integer euCount) {
        this.euCount = euCount;
    }

    public String getDoLaaShortDsc() {
        return doLaaShortDsc;
    }

    public void setDoLaaShortDsc(String doLaaShortDsc) {
        this.doLaaShortDsc = doLaaShortDsc;
    }
    
    public String getPermitActionType() {
		return permitActionType;
	}
    
	public String getPermitActionTypeDesc() {
		String desc = "";
		if (getPermitActionType() != null) {
			desc = PermitActionTypeDef.getData().getItems()
					.getItemDesc(getPermitActionType());
		}
		return desc;
	}

	public void setPermitActionType(String permitActionType) {
		this.permitActionType = permitActionType;
		if (this.permitActionType != null
				&& this.permitActionType.equals(PermitActionTypeDef.PERMIT)) {
			setActionTypePermit(true);
		} else if (this.permitActionType != null
				&& this.permitActionType.equals(PermitActionTypeDef.WAIVER)) {
			setActionTypePermit(false);
		}
	}

	public String getSubjectToPsd() {
		return subjectToPsd;
	}

	public void setSubjectToPsd(String subjectToPsd) {
		this.subjectToPsd = subjectToPsd;
	}

	public String getSubjectToNansr() {
		return subjectToNansr;
	}

	public void setSubjectToNansr(String subjectToNansr) {
		this.subjectToNansr = subjectToNansr;
	}
	
	public boolean isActionTypePermit() {
		return actionTypePermit;
	}

	public void setActionTypePermit(boolean actionTypePermit) {
		this.actionTypePermit = actionTypePermit;
	}
	 
	public boolean isWaiver() {
		return !isActionTypePermit();
	}
	
	public final String getCountyName() {
        return countyName;
    }

    public final void setCountyName(String countyName) {
        this.countyName = countyName;
    }
    
    public Boolean getExtendProcessEndDate() {
		return extendProcessEndDate;
	}

	public void setExtendProcessEndDate(Boolean extendProcessEndDate) {
		this.extendProcessEndDate = extendProcessEndDate;
	}
	
	public Long getAgencyDays() {
		return isCountedAsAgencyDays()? getTotalDays() : 0;
	}

	public Long getNonAgencyDays() {
		return isCountedAsAgencyDays()? 0 : getTotalDays();
	}

	public boolean isCountedAsAgencyDays() {
		boolean agencyDays = true;
		if (ActivityStatusDef.UNREFERRED.equals(activityStatusCd) ||
				ActivityStatusDef.REFERRED.equals(activityStatusCd)) {
			 agencyDays = null == extendProcessEndDate?  true : !extendProcessEndDate;
		}
		return agencyDays;
	}
	
	public Long getTotalDays() {
		Long days = 0L;
		if (null != taskStartDt) {
			DateTime startDateTime = 
					new DateTime(Utility.formatBeginOfDay(this.taskStartDt));
			DateTime endDateTime = null;
			if (null == taskEndDt) {
				endDateTime = 
						new DateTime(Utility.formatBeginOfDay(new Date()));
			} else {
				endDateTime = 
						new DateTime(Utility.formatBeginOfDay(this.taskEndDt));
			}
			Days d = Days.daysBetween(startDateTime, endDateTime);
			
			days =  (long) d.getDays();
		}
		return days;
	}
	
	public final Timestamp getCompletenessReviewStartDt() {
        return completenessReviewStartDt;
    }

    public final void setCompletenessReviewStartDt(Timestamp startDt) {
        this.completenessReviewStartDt = startDt;
    }
    
    public final Timestamp getCompletenessReviewEndDt() {
        return completenessReviewEndDt;
    }

    public final void setCompletenessReviewEndDt(Timestamp endDt) {
        this.completenessReviewEndDt = endDt;
    }
    
    public final Timestamp getTechReviewStartDt() {
        return techReviewStartDt;
    }

    public final void setTechReviewStartDt(Timestamp startDt) {
        this.techReviewStartDt = startDt;
    }
    
    public final Timestamp getManagerReviewStartDt() {
        return managerReviewStartDt;
    }

    public final void setManagerReviewStartDt(Timestamp startDt) {
        this.managerReviewStartDt = startDt;
    }
    
    public Long getTotalAgencyDays() {
		return totalAgencyDays;
	}
	
    public final void setTotalAgencyDays(Long totalAgencyDays) {
        this.totalAgencyDays = totalAgencyDays;
    }

	public Long getTotalNonAgencyDays() {
		return totalNonAgencyDays;
	}
	
	public final void setTotalNonAgencyDays(Long totalNonAgencyDays) {
        this.totalNonAgencyDays = totalNonAgencyDays;
    }
	
	public Long getTotalAgencyDaysForAnalysis() {
		return totalAgencyDaysForAnalysis;
	}
	
    public final void setTotalAgencyDaysForAnalysis(Long totalAgencyDays) {
        this.totalAgencyDaysForAnalysis = totalAgencyDays;
    }

	public Long getTotalNonAgencyDaysForAnalysis() {
		return totalNonAgencyDaysForAnalysis;
	}
	
	public final void setTotalNonAgencyDaysForAnalysis(Long totalNonAgencyDays) {
        this.totalNonAgencyDaysForAnalysis = totalNonAgencyDays;
    }

    public final Integer getPermitCount() {
        return permitCount;
    }
    
    public final void setPermitCount(Integer count) {
        this.permitCount = count;
    }
    
    public final void setExcessiveCount(Integer count) {
        this.excessiveCount = count;
    }
    
    public final Integer getExcessiveCount() {
        return excessiveCount;
    }
    
    public final void setExcessivePreliminaryCount(Integer count) {
        this.excessivePreliminaryCount = count;
    }
    
    public final Integer getExcessivePreliminaryCount() {
        return excessivePreliminaryCount;
    }
   
    public Double getAverageAqdDays() {
		return averageAqdDays;
	}
	
    public final void setAverageAqdDays(Double averageDays) {
        this.averageAqdDays = averageDays;
    }
    
    public Double getAverageIssuanceDays() {
		return averageIssuanceDays;
	}
	
    public final void setAverageIssuanceDays(Double averageDays) {
        this.averageIssuanceDays = averageDays;
    }
    
    public Double getTotalIssuanceDays() {
		return new Double(totalAgencyDays + totalNonAgencyDays);
	}
    
    public String customFormat(double value) {
		String pattern = "###,##0.00";
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		String output = myFormatter.format(value);
		return output;
	}
    
    public String getAverageAqdDaysString() {
    	String days = new String (
    	days = customFormat(BigDecimal
    			.valueOf(averageAqdDays) 
    			.setScale(2, RoundingMode.HALF_UP)
    			.doubleValue()));
    	return days;
    }
    
    public String getAverageIssuanceDaysString() {
    	String days = new String (
    	days = customFormat(BigDecimal
    			.valueOf(averageIssuanceDays) 
    			.setScale(2, RoundingMode.HALF_UP)
    			.doubleValue()));
    	return days;
    }
    
    public String getDescLabel() {
		return descLabel;
	}

	public void setDescLabel(String descLabel) {
		this.descLabel = descLabel;
	}
	
    public final Integer getLoopCnt() {
        return loopCnt;
    }

    public final void setLoopCnt(Integer loopCnt) {
        this.loopCnt = loopCnt;
    }
    
    public Integer getBenchmarkDays() {
		return benchmarkDays;
	}

	public void setBenchmarkDays(Integer days) {
		this.benchmarkDays = days;
	}
	
	public final String getPermitGlobalStatus() {
        return permitGlobalStatus;
    }

    public final void setPermitGlobalStatus(String permitGlobalStatus) {
        this.permitGlobalStatus = permitGlobalStatus;
    }
    
    public Integer getPrelimBenchmarkDays() {
		return prelimBenchmarkDays;
	}

	public void setPrelimBenchmarkDays(Integer days) {
		this.prelimBenchmarkDays = days;
	}
}
