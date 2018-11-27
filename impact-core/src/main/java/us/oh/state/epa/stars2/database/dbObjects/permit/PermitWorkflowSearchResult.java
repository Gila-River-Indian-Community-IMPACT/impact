package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class PermitWorkflowSearchResult extends BaseDB {

	private String processId;
	private String companyNm;
	private String facilityId;
	private Integer fpId;
	private String facilityNm;
	private String activityId;
	private String activityNm;
	private String activityStatusCd;
	private String userNm;
	private String userFirstNm;
	private String userLastNm;
	private Date startDate;
	private Date endDate;
	private String permitTypeCd;
	private String permitNumber;
	private String applicationNbr;
	private Date applicationReceivedDate;
	private String activityTemplateId;
	private Boolean extendProcessEndDate;

	public Integer getFpId() {
		return fpId;
	}

	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}

	public int getAgencyDays() {
		return isCountedAsAgencyDays()? getTotalDays() : 0;
	}

	public int getNonAgencyDays() {
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
	
	public int getTotalDays() {
		int days = 0;
		if (null != startDate) {
			DateTime startDateTime = 
					new DateTime(Utility.formatBeginOfDay(this.startDate));
			DateTime endDateTime = null;
			if (null == endDate) {
				endDateTime = 
						new DateTime(Utility.formatBeginOfDay(new Date()));
			} else {
				endDateTime = 
						new DateTime(Utility.formatBeginOfDay(this.endDate));
			}
			Days d = Days.daysBetween(startDateTime, endDateTime);
			
			days = d.getDays();
		}
		return days;
	}

	

	public String getUserFirstNm() {
		return userFirstNm;
	}

	public void setUserFirstNm(String userFirstNm) {
		this.userFirstNm = userFirstNm;
	}

	public String getUserLastNm() {
		return userLastNm;
	}

	public void setUserLastNm(String userLastNm) {
		this.userLastNm = userLastNm;
	}

	public Boolean getExtendProcessEndDate() {
		return extendProcessEndDate;
	}

	public void setExtendProcessEndDate(Boolean extendProcessEndDate) {
		this.extendProcessEndDate = extendProcessEndDate;
	}

	public String getActivityTemplateId() {
		return activityTemplateId;
	}


	public void setActivityTemplateId(String activityTemplateId) {
		this.activityTemplateId = activityTemplateId;
	}


	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public Date getApplicationReceivedDate() {
		return applicationReceivedDate;
	}




	public void setApplicationReceivedDate(Date applicationReceivedDate) {
		this.applicationReceivedDate = applicationReceivedDate;
	}




	public String getCompanyNm() {
		return companyNm;
	}




	public void setCompanyNm(String companyNm) {
		this.companyNm = companyNm;
	}




	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getFacilityNm() {
		return facilityNm;
	}

	public void setFacilityNm(String facilityNm) {
		this.facilityNm = facilityNm;
	}

	public String getActivityNm() {
		return activityNm;
	}

	public void setActivityNm(String activityNm) {
		this.activityNm = activityNm;
	}

	public String getActivityStatusCd() {
		return activityStatusCd;
	}

	public void setActivityStatusCd(String activityStatusCd) {
		this.activityStatusCd = activityStatusCd;
	}

	public String getUserNm() {
		return userNm;
	}

	public void setUserNm(String userNm) {
		this.userNm = userNm;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getPermitTypeCd() {
		return permitTypeCd;
	}

	public void setPermitTypeCd(String permitTypeCd) {
		this.permitTypeCd = permitTypeCd;
	}

	public String getPermitNumber() {
		return permitNumber;
	}

	public void setPermitNumber(String permitNumber) {
		this.permitNumber = permitNumber;
	}

	public String getApplicationNbr() {
		return applicationNbr;
	}

	public void setApplicationNbr(String applicationNbr) {
		this.applicationNbr = applicationNbr;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	
}
