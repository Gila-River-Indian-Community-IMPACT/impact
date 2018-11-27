package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.ActivityReferralTypeDef;
import us.oh.state.epa.stars2.def.ActivityStatusDef;

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
 * @version $Revision: 1.28 $
 * @author $Author: dleinbaugh $
 */
public class ProcessActivity extends BaseDB {
	
	private static final long serialVersionUID = -7551545058642610190L;

	private Integer activityTemplateId;
    private Integer processId;
    private Integer parentProcessId;
    private Integer userId;
    private Integer oldUserId;
    private Integer loopCnt;
    private boolean looped;
    private Integer ownerId;
    private String expedited;
    private boolean viewed;
    private String current;
    private String activityStatusCd;
    private Timestamp readyDt;
    private Timestamp startDt;
    private Timestamp endDt;
    private Timestamp dueDt;
    private Timestamp jeopardyDt;
    private String activityTemplateNm;
    private String orderTypeDsc;
    private String activityStatusDsc;
    private String performerTypeCd;
    private String activityTemplateDsc;
    private String userName;
    private String ownerName;
    private String firstName;
    private String lastName;
    private Integer numberOfRetries;
    private Integer retryInterval; // In minutes

    // Additional attributes for activity lists
    private Integer serviceId;
    private Integer externalId;
    private String facilityId;
    private String doLaaName;
    private String doLaaCd;
    private String permitType;
    private String issuanceType;
    private String activityUrl;
    private String facilityNm;
    private Timestamp processStartDt;
    private Timestamp processDueDt;
    private Timestamp processEndDt;
    private Timestamp processJeoDt;
    private String processCd;
    private Integer fpId;
    private String aggregate;
    private String status;
    private String statusColor;
    private TimeSpan duration;
    private TimeSpan processDuration;
    private String processTemplateNm;
    
    //
    // Permit Summary Attributes
    private int dolaaDuration;
    private int aqmpDuration;
    private int companyDuration;
    private int legalDuration;
    private int pmuDuration;
    private int aqdDuration;
    private Integer contactId;
    private String extendProcessEndDate = "N";
    private Integer activityReferralTypeId;
    private ArrayList<String> activityStatusCds = new ArrayList<String>();
    private boolean inStatus;
    private Integer processTemplateId;

    /**
     * 
     */
    public ProcessActivity() {
        super();
        looped = false;
        dolaaDuration = 0;
        aqmpDuration = 0;
        companyDuration = 0;
        legalDuration = 0;
        pmuDuration = 0;
    }

    /**
     * @param old
     */
    public ProcessActivity(ProcessActivity old) {
        super(old);
        if (old != null) {
            setActivityTemplateId(old.getActivityTemplateId());
            setActivityStatusCd(old.getActivityStatusCd());
            setActivityStatusCds(old.getActivityStatusCds());
            setActivityStatusDsc(old.getActivityTemplateDsc());
            setActivityTemplateDsc(old.getActivityTemplateDsc());
            setActivityTemplateNm(old.getActivityTemplateNm());
            setActivityUrl(old.getActivityUrl());
            setAggregate(old.getAggregate());
            setContactId(old.getContactId());
            setCurrent(old.getCurrent());
            setDueDt(old.getDueDt());
            setDuration(old.getDuration());
            setEndDt(old.getEndDt());
            setExpedited(old.getExpedited());
            setViewed(old.isViewed());
            setExtendProcessEndDate(old.getExtendProcessEndDate());
            setActivityReferralTypeId(old.getActivityReferralTypeId());
            setExternalId(old.getExternalId());
            setFacilityId(old.getFacilityId());
            setFacilityNm(old.getFacilityNm());
            setFirstName(old.getFirstName());
            setDoLaaCd(old.getDoLaaCd());
            setFpId(old.getFpId());
            setInStatus(old.isInStatus());
            setJeopardyDt(old.getJeopardyDt());
            setLastName(old.getLastName());
            setLoopCnt(old.getLoopCnt());
            setNumberOfRetries(old.getNumberOfRetries());
            setOldUserId(old.getOldUserId());
            setOrderTypeDsc(old.getOrderTypeDsc());
            setOwnerId(old.getOwnerId());
            setOwnerName(old.getOwnerName());
            setParentProcessId(old.getParentProcessId());
            setPerformerTypeCd(old.getPerformerTypeCd());
            setProcessCd(old.getProcessCd());
            setProcessDueDt(old.getProcessDueDt());
            setProcessDuration(old.getProcessDuration());
            setProcessEndDt(old.getProcessEndDt());
            setProcessId(old.getProcessId());
            setProcessJeoDt(old.getProcessJeoDt());
            setProcessStartDt(old.getProcessStartDt());
            setProcessTemplateNm(old.getProcessTemplateNm());
            setReadyDt(old.getReadyDt());
            setRetryInterval(old.getRetryInterval());
            setSelected(old.isSelected());
            setServiceId(old.getServiceId());
            setStartDt(old.getStartDt());
            setStatus(old.getStatus());
            setStatusColor(old.getStatusColor());
            setUserId(old.getUserId());
            setUserName(old.getUserName());

            setDolaaDuration(old.getDolaaDuration());
            setAqmpDuration(old.getAqmpDuration());
            setCompanyDuration(old.getCompanyDuration());
            setLegalDuration(old.getLegalDuration());
            setPmuDuration(old.getPmuDuration());
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
            setReadyDt(rs.getTimestamp("A_Ready_Dt"));
            setEndDt(rs.getTimestamp("A_END_DT"));
            setDueDt(rs.getTimestamp("A_Due_Dt"));
            setJeopardyDt(rs.getTimestamp("A_Jeopardy_Dt"));
            // activity status
            /*setStatus(WorkFlowProcess.checkStatus(getJeopardyDt(), getDueDt(),
                    getEndDt()));
            setStatusColor(WorkFlowProcess.checkStatusColor(getStatus()));*/
            setExternalId(AbstractDAO.getInteger(rs, "External_Id"));
            if (getStartDt() != null) {
                setDuration(new TimeSpan(getEndDt(), getStartDt()));
            }
            
            setProcessTemplateNm(rs.getString("PROCESS_TEMPLATE_NM"));
            StringBuffer name = new StringBuffer(rs
                    .getString("activity_template_nm"));
            String activityName = name.toString();

            if (getLoopCnt() != 1) {
                name.append(" - ");
                name.append(getLoopCnt());
            }
            
            setActivityReferralTypeId(AbstractDAO.getInteger(rs,
            "ACTIVITY_REFERRAL_TYPE_ID"));
            
            if (duration != null && activityName != null)
                setPermitDurations(activityName, duration);

            setContactId(AbstractDAO.getInteger(rs, "CONTACT_ID"));
            setExtendProcessEndDate(rs.getString("EXTEND_PROCESS_END_DATE"));
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
            setStatus(WorkFlowProcess.checkStatus(getJeopardyDt(), getDueDt(), getEndDt()));
            setStatusColor(WorkFlowProcess.checkStatusColor(getStatus()));

            if (getProcessStartDt() != null) {
                setProcessDuration(new TimeSpan(getProcessEndDt(),
                        getProcessStartDt()));
            }
            setCurrent(rs.getString("IS_CURRENT"));
            setActivityUrl(rs.getString("ACTIVITY_URL"));

            setPerformerTypeCd(rs.getString("performer_type_cd"));
            setActivityStatusDsc(rs.getString("activity_status_dsc"));
            setActivityTemplateDsc(rs.getString("activity_template_dsc"));
            setUserName(setUserName(rs));
            setNumberOfRetries(AbstractDAO.getInteger(rs, "number_of_retries"));
            setRetryInterval(AbstractDAO.getInteger(rs, "retry_interval"));
            
        } catch (SQLException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e.getMessage());
			}
        }
    }

	private String setUserName(ResultSet rs) {
		String lFirstName = null;
		try {
			lFirstName = rs.getString("first_nm");
		} catch (SQLException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e.getMessage());
			}
		}
		String lLastName = null;
		try {
			lLastName = rs.getString("last_nm");
		} catch (SQLException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e.getMessage());
			}
		}
		String lUserName = "Unassigned";

		if ((lFirstName != null) && (lFirstName.length() > 0)) {
		    StringBuffer sb = new StringBuffer();
		    sb.append(lFirstName);

		    if ((lLastName != null) && (lLastName.length() > 0)) {
		        sb.append(" ");
		        sb.append(lLastName);
		    }

		    lUserName = sb.toString();
		}
		return lUserName;
	}
    
    public void setPermitDurations(String name, TimeSpan duration) {
        int days = duration.getDays();

        boolean referred = (ActivityStatusDef.REFERRED.equals(activityStatusCd) 
                || ActivityStatusDef.UNREFERRED.equals(activityStatusCd))
                && (activityReferralTypeId != null);
        if (referred  && (activityReferralTypeId <= ActivityReferralTypeDef.FACILITY_MODELING)) {
            setCompanyDuration(days);
        } else if (referred && (activityReferralTypeId == ActivityReferralTypeDef.CO_LEGAL)) {
            setLegalDuration(days);
        } else if (name.contains("DO/LAA ")
                || name.contains("Technically Complete")
                || name.contains("Multi Media Form")) {
            setDolaaDuration(days);
        } else if (name.contains("CO ")) {
            setAqmpDuration(days);
        } else if (name.contains("Prepare ")
                || name.contains("Issue ")
                || "Enter Referral Date".equals(name)
                || "Denial Notice".equals(name)) {
            setPmuDuration(days);
        } else {
        	setAqdDuration(days);
        }
    }
    
    public final TimeSpan getProcessDuration() {
        return processDuration;
    }

    public final void setProcessDuration(TimeSpan processDuration) {
        this.processDuration = processDuration;
    }

    public final int getAqmpDuration() {
        return aqmpDuration;
    }

    public final void setAqmpDuration(int aqmpDuration) {
        this.aqmpDuration = aqmpDuration;
    }

    public final int getCompanyDuration() {
        return companyDuration;
    }

    public final void setCompanyDuration(int companyDuration) {
        this.companyDuration = companyDuration;
    }

    public final int getDolaaDuration() {
        return dolaaDuration;
    }

    public final void setDolaaDuration(int dolaaDuration) {
        this.dolaaDuration = dolaaDuration;
    }

    public final int getLegalDuration() {
        return legalDuration;
    }

    public final void setLegalDuration(int legalDuration) {
        this.legalDuration = legalDuration;
    }

    public final int getPmuDuration() {
        return pmuDuration;
    }

    public final void setPmuDuration(int pmuDuration) {
        this.pmuDuration = pmuDuration;
    }

    public final Integer getContactId() {
        return contactId;
    }

    public final void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    public final String getExtendProcessEndDate() {
        return extendProcessEndDate;
    }

    public final void setExtendProcessEndDate(String extendProcessEndDate) {
        this.extendProcessEndDate = extendProcessEndDate;
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

    public final TimeSpan getDuration() {
        return duration;
    }

    public final void setDuration(TimeSpan span) {
        this.duration = span;
    }

    public final String getStatus() {
        return status;
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
     * @return Returns the current.
     */
    public final String getCurrent() {
        return current;
    }

    /**
     * @param current
     *            The current to set.
     */
    public final void setCurrent(String current) {
        this.current = current;
    }

    /**
     * @return Returns the ownerId.
     */
    public final Integer getOwnerId() {
        return ownerId;
    }

    /**
     * @param id
     *            The ownerId to set.
     */
    public final void setOwnerId(Integer id) {
        ownerId = id;
    }

    /**
     * @return Returns the ownerName.
     */
    public final String getOwnerName() {
        return ownerName;
    }

    /**
     * @param name
     *            The ownerName to set.
     */
    public final void setOwnerName(String name) {
        ownerName = name;
    }

    public final Integer getNumberOfRetries() {
        return this.numberOfRetries;
    }

    public final void setNumberOfRetries(Integer retryCnt) {
        this.numberOfRetries = retryCnt;
    }

    public final Integer getRetryInterval() {
        return this.retryInterval;
    }

    public final void setRetryInterval(Integer newInterval) {
        this.retryInterval = newInterval;
    }

    // ------------------------------------------
    // activityTemplateDsc
    // ------------------------------------------

    public final String getActivityTemplateDsc() {
        return this.activityTemplateDsc;
    }

    public final void setActivityTemplateDsc(String newDsc) {
        this.activityTemplateDsc = newDsc;
    }

    // ------------------------------------------
    // activityUrl
    // ------------------------------------------

    public final String getActivityUrl() {
        return this.activityUrl;
    }

    public final void setActivityUrl(String url) {
        this.activityUrl = url;
    }

    // ------------------------------------------
    // User Name
    // ------------------------------------------

    public final String getUserName() {
        return this.userName;
    }

    public final void setUserName(String newName) {
        this.userName = newName;
    }

    // ------------------------------------------
    // User First Name
    // ------------------------------------------

    public final String getFirstName() {
        return this.firstName;
    }

    public final void setFirstName(String newName) {
        this.firstName = newName;
    }

    // ------------------------------------------
    // User Last Name
    // ------------------------------------------

    public final String getLastName() {
        return this.lastName;
    }

    public final void setLastName(String newName) {
        this.lastName = newName;
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
    // ProcessId
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
     * @param ProcessId
     */
    public final void setProcessId(Integer processId) {
        this.processId = processId;
    }

    // ------------------------------------------
    // ProcessId
    // ------------------------------------------

    /**
     * ProcessId
     * 
     * @return Integer
     */
    public final Integer getParentProcessId() {
        return parentProcessId;
    }

    /**
     * ProcessId
     * 
     * @param ProcessId
     */
    public final void setParentProcessId(Integer ProcessId) {
        parentProcessId = ProcessId;
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
            this.oldUserId = this.userId;
        }
        this.userId = userId;
    }

    public final Integer getOldUserId() {
        return oldUserId;
    }

    public final void setOldUserId(Integer oldUserId) {
        this.oldUserId = oldUserId;
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

    // ------------------------------------------
    // activityStatusCd
    // ------------------------------------------

    /**
     * ActivityStatusCd
     * 
     * @return String
     */
    public final String getActivityStatusCd() {
        return activityStatusCd;
    }

    /**
     * ActivityStatusCd
     * 
     * @param activityStatusCd
     */
    public final void setActivityStatusCd(String activityStatusCd) {
        this.activityStatusCd = activityStatusCd;
    }

    // ------------------------------------------
    // readyDt
    // ------------------------------------------

    /**
     * ReadyDt
     * 
     * @return Timestamp
     */
    public final Timestamp getReadyDt() {
        return readyDt;
    }

    /**
     * ReadyDt
     * 
     * @param readyDt
     */
    public final void setReadyDt(Timestamp readyDt) {
        this.readyDt = readyDt;
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
    // startDt
    // ------------------------------------------

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
    // dueDt
    // ------------------------------------------

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

    // ------------------------------------------
    // jeopardyDt
    // ------------------------------------------

    public final Timestamp getJeopardyDt() {
        return this.jeopardyDt;
    }

    public final void setJeopardyDt(Timestamp jeoDt) {
        this.jeopardyDt = jeoDt;
    }

    // ------------------------------------------
    // Process dueDt
    // ------------------------------------------

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

    // ------------------------------------------
    // Process endDt
    // ------------------------------------------

    /**
     * EndDt
     * 
     * @return Timestamp
     */
    public final Timestamp getProcessEndDt() {
        return processEndDt;
    }

    /**
     * EndDt
     * 
     * @param Rendt
     */
    public final void setProcessEndDt(Timestamp endDt) {
        processEndDt = endDt;
    }

    // ------------------------------------------
    // Process jeoDt
    // ------------------------------------------

    /**
     * JeoDt
     * 
     * @return Timestamp
     */
    public final Timestamp getProcessJeoDt() {
        return processJeoDt;
    }

    /**
     * JeoDt
     * 
     * @param Rendt
     */
    public final void setProcessJeoDt(Timestamp jeoDt) {
        processJeoDt = jeoDt;
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

    // -------------------------------------------
    // ActivityStatusDsc
    // -------------------------------------------

    public final String getActivityStatusDsc() {
        return this.activityStatusDsc;
    }

    public final void setActivityStatusDsc(String newDef) {
        this.activityStatusDsc = newDef;
    }

    // -------------------------------------------
    // ActivityStatusDsc
    // -------------------------------------------

    public final String getOrderTypeDsc() {
        return this.orderTypeDsc;
    }

    public final void setOrderTypeDsc(String newDef) {
        this.orderTypeDsc = newDef;
    }

    // ----------------------------------------------------------------------
    // performTypeCd. This is actually defined in the ActivityTemplateDef
    // object, but stored in the ProcessActivity object
    // because we may need to overwrite it in the event that
    // an automatic activity performer fails. In that case,
    // we change the activity from "automatic" to "manual"
    // and change the status to "blocked".
    // ----------------------------------------------------------------------

    public final String getPerformerTypeCd() {
        return this.performerTypeCd;
    }

    public final void setPerformerTypeCd(String newCd) {
        this.performerTypeCd = newCd;
    }

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

    // ------------------------------------------
    // facilityNm
    // ------------------------------------------

    public final String getFacilityNm() {
        return this.facilityNm;
    }

    public final void setFacilityNm(String newName) {
        this.facilityNm = newName;
    }

    public final String getDoLaaName() {
        return doLaaName;
    }

    public final void setDoLaaName(String doLaaName) {
        this.doLaaName = doLaaName;
    }

    public final String getIssuanceType() {
        return issuanceType;
    }

    public final void setIssuanceType(String issuanceType) {
        this.issuanceType = issuanceType;
    }

    public final String getPermitType() {
        return permitType;
    }

    public final void setPermitType(String permitType) {
        this.permitType = permitType;
    }

    public final String getProcessCd() {
        return processCd;
    }

    public final void setProcessCd(String processCd) {
        this.processCd = processCd;
    }

    public final ArrayList<String> getActivityStatusCds() {
        return activityStatusCds;
    }

    public final void setActivityStatusCds(ArrayList<String> activityStatusCds) {
        if (activityStatusCds == null)
            this.activityStatusCds = new ArrayList<String>();
        this.activityStatusCds = activityStatusCds;
    }

    public final boolean isInStatus() {
        return inStatus;
    }

    public final void setInStatus(boolean inStatus) {
        this.inStatus = inStatus;
    }

    public final int compareTo(Object o) {
        if (o instanceof ProcessActivity) {
            return this.activityTemplateNm.compareTo(((ProcessActivity) o)
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
                + ((activityStatusDsc == null) ? 0 : activityStatusDsc
                        .hashCode());
        result = PRIME
                * result
                + ((activityTemplateDsc == null) ? 0 : activityTemplateDsc
                        .hashCode());
        result = PRIME
                * result
                + ((activityTemplateId == null) ? 0 : activityTemplateId
                        .hashCode());
        result = PRIME
                * result
                + ((activityTemplateNm == null) ? 0 : activityTemplateNm
                        .hashCode());
        result = PRIME * result
                + ((activityUrl == null) ? 0 : activityUrl.hashCode());
        result = PRIME * result
                + ((aggregate == null) ? 0 : aggregate.hashCode());
        result = PRIME * result
                + ((contactId == null) ? 0 : contactId.hashCode());
        result = PRIME * result + ((current == null) ? 0 : current.hashCode());
        result = PRIME * result
                + ((doLaaName == null) ? 0 : doLaaName.hashCode());
        result = PRIME * result + ((dueDt == null) ? 0 : dueDt.hashCode());
        result = PRIME * result + ((endDt == null) ? 0 : endDt.hashCode());
        result = PRIME
                * result
                + ((extendProcessEndDate == null) ? 0 : extendProcessEndDate
                        .hashCode());
        result = PRIME * result
                + ((externalId == null) ? 0 : externalId.hashCode());
        result = PRIME * result
                + ((facilityId == null) ? 0 : facilityId.hashCode());
        result = PRIME * result
                + ((facilityNm == null) ? 0 : facilityNm.hashCode());
        result = PRIME * result
                + ((firstName == null) ? 0 : firstName.hashCode());
        result = PRIME * result + ((fpId == null) ? 0 : fpId.hashCode());
        result = PRIME * result + (inStatus ? 1231 : 1237);
        result = PRIME * result
                + ((issuanceType == null) ? 0 : issuanceType.hashCode());
        result = PRIME * result
                + ((jeopardyDt == null) ? 0 : jeopardyDt.hashCode());
        result = PRIME * result
                + ((lastName == null) ? 0 : lastName.hashCode());
        result = PRIME * result + ((loopCnt == null) ? 0 : loopCnt.hashCode());
        result = PRIME * result
                + ((numberOfRetries == null) ? 0 : numberOfRetries.hashCode());
        result = PRIME * result
                + ((oldUserId == null) ? 0 : oldUserId.hashCode());
        result = PRIME * result
                + ((orderTypeDsc == null) ? 0 : orderTypeDsc.hashCode());
        result = PRIME * result + ((ownerId == null) ? 0 : ownerId.hashCode());
        result = PRIME * result
                + ((ownerName == null) ? 0 : ownerName.hashCode());
        result = PRIME * result
                + ((parentProcessId == null) ? 0 : parentProcessId.hashCode());
        result = PRIME * result
                + ((performerTypeCd == null) ? 0 : performerTypeCd.hashCode());
        result = PRIME * result
                + ((permitType == null) ? 0 : permitType.hashCode());
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
        result = PRIME * result + ((readyDt == null) ? 0 : readyDt.hashCode());
        result = PRIME * result
                + ((retryInterval == null) ? 0 : retryInterval.hashCode());
        result = PRIME * result
                + ((serviceId == null) ? 0 : serviceId.hashCode());
        result = PRIME * result + ((startDt == null) ? 0 : startDt.hashCode());
        result = PRIME * result + ((status == null) ? 0 : status.hashCode());
        result = PRIME * result
                + ((statusColor == null) ? 0 : statusColor.hashCode());
        result = PRIME * result + ((userId == null) ? 0 : userId.hashCode());
        result = PRIME * result
                + ((userName == null) ? 0 : userName.hashCode());
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
        final ProcessActivity other = (ProcessActivity) obj;
        if (activityStatusCd == null) {
            if (other.activityStatusCd != null)
                return false;
        } else if (!activityStatusCd.equals(other.activityStatusCd))
            return false;
        if (activityStatusDsc == null) {
            if (other.activityStatusDsc != null)
                return false;
        } else if (!activityStatusDsc.equals(other.activityStatusDsc))
            return false;
        if (activityTemplateDsc == null) {
            if (other.activityTemplateDsc != null)
                return false;
        } else if (!activityTemplateDsc.equals(other.activityTemplateDsc))
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
        if (activityUrl == null) {
            if (other.activityUrl != null)
                return false;
        } else if (!activityUrl.equals(other.activityUrl))
            return false;
        if (aggregate == null) {
            if (other.aggregate != null)
                return false;
        } else if (!aggregate.equals(other.aggregate))
            return false;
        if (contactId == null) {
            if (other.contactId != null)
                return false;
        } else if (!contactId.equals(other.contactId))
            return false;
        if (current == null) {
            if (other.current != null)
                return false;
        } else if (!current.equals(other.current))
            return false;
        if (doLaaName == null) {
            if (other.doLaaName != null)
                return false;
        } else if (!doLaaName.equals(other.doLaaName))
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
        if (extendProcessEndDate == null) {
            if (other.extendProcessEndDate != null)
                return false;
        } else if (!extendProcessEndDate.equals(other.extendProcessEndDate))
            return false;
        if (externalId == null) {
            if (other.externalId != null)
                return false;
        } else if (!externalId.equals(other.externalId))
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
        if (firstName == null) {
            if (other.firstName != null)
                return false;
        } else if (!firstName.equals(other.firstName))
            return false;
        if (fpId == null) {
            if (other.fpId != null)
                return false;
        } else if (!fpId.equals(other.fpId))
            return false;
        if (inStatus != other.inStatus)
            return false;
        if (issuanceType == null) {
            if (other.issuanceType != null)
                return false;
        } else if (!issuanceType.equals(other.issuanceType))
            return false;
        if (jeopardyDt == null) {
            if (other.jeopardyDt != null)
                return false;
        } else if (!jeopardyDt.equals(other.jeopardyDt))
            return false;
        if (lastName == null) {
            if (other.lastName != null)
                return false;
        } else if (!lastName.equals(other.lastName))
            return false;
        if (loopCnt == null) {
            if (other.loopCnt != null)
                return false;
        } else if (!loopCnt.equals(other.loopCnt))
            return false;
        if (numberOfRetries == null) {
            if (other.numberOfRetries != null)
                return false;
        } else if (!numberOfRetries.equals(other.numberOfRetries))
            return false;
        if (oldUserId == null) {
            if (other.oldUserId != null)
                return false;
        } else if (!oldUserId.equals(other.oldUserId))
            return false;
        if (orderTypeDsc == null) {
            if (other.orderTypeDsc != null)
                return false;
        } else if (!orderTypeDsc.equals(other.orderTypeDsc))
            return false;
        if (ownerId == null) {
            if (other.ownerId != null)
                return false;
        } else if (!ownerId.equals(other.ownerId))
            return false;
        if (ownerName == null) {
            if (other.ownerName != null)
                return false;
        } else if (!ownerName.equals(other.ownerName))
            return false;
        if (parentProcessId == null) {
            if (other.parentProcessId != null)
                return false;
        } else if (!parentProcessId.equals(other.parentProcessId))
            return false;
        if (performerTypeCd == null) {
            if (other.performerTypeCd != null)
                return false;
        } else if (!performerTypeCd.equals(other.performerTypeCd))
            return false;
        if (permitType == null) {
            if (other.permitType != null)
                return false;
        } else if (!permitType.equals(other.permitType))
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
        if (readyDt == null) {
            if (other.readyDt != null)
                return false;
        } else if (!readyDt.equals(other.readyDt))
            return false;
        if (retryInterval == null) {
            if (other.retryInterval != null)
                return false;
        } else if (!retryInterval.equals(other.retryInterval))
            return false;
        if (serviceId == null) {
            if (other.serviceId != null)
                return false;
        } else if (!serviceId.equals(other.serviceId))
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
        if (userName == null) {
            if (other.userName != null)
                return false;
        } else if (!userName.equals(other.userName))
            return false;
        if (viewed != other.viewed)
            return false;
        return true;
    }

    public final Integer getActivityReferralTypeId() {
        return activityReferralTypeId;
    }

    public final void setActivityReferralTypeId(Integer activityReferralTypeId) {
        this.activityReferralTypeId = activityReferralTypeId;
    }

    public final void setProcessTemplateId(Integer processTemplateId) {
        this.processTemplateId = processTemplateId;
    }

    /**
     * @return the processTemplateId
     */
    public final Integer getProcessTemplateId() {
        return processTemplateId;
    }

    public String toIds() {
        String ret = " getFacilityId() " + getFacilityId() 
            + ", getFacilityNm " + getFacilityNm()
            + ", getDoLaaName() " + getDoLaaName()
            + ", getUserId() " + getUserId()
            + ", getActivityStatusCd() " + getActivityStatusCd()
            + ", getActivityTemplateNm() " + getActivityTemplateNm()
            + ", getProcessCd() " + getProcessCd()
            + ", getProcessId() " + getProcessId()
            + ", getProcessTemplateId() " + getProcessTemplateId()
            + ", getProcessTemplateNm() " + getProcessTemplateNm()
            + ", getActivityTemplateId() " + getActivityTemplateId()
            + ", getCurrent() " + getCurrent()
            + ", getAggregate() " + getAggregate()
            + ", getPerformerTypeCd() " + getPerformerTypeCd()
            + ", getExternalId() " + getExternalId()
            + ", getLoopCnt() " + getLoopCnt()
            + ", getActivityStatusCds() " + getActivityStatusCds()
            + ", getEndDt() " + getEndDt();
        return ret;
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

    public String getDoLaaCd() {
        return doLaaCd;
    }

    public void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

	public int getAqdDuration() {
		return aqdDuration;
	}

	public void setAqdDuration(int aqdDuration) {
		this.aqdDuration = aqdDuration;
	}
}
