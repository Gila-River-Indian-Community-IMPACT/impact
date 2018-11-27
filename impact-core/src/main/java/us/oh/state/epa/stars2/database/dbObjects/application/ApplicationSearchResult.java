package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;

@SuppressWarnings("serial")
public class ApplicationSearchResult extends BaseDB {
    private Integer applicationId;
    private String applicationTypeCd;
    private String applicationNumber;
    private Timestamp receivedDate;
    private Timestamp submittedDate;
    private String previousApplicationNumber;
    private Integer fpId;
    private String facilityId;
    private String facilityName;
    private String doLaaCd;
    private String countyCd;
    private String permitNumbers;
    private String cmpId;
    private String companyName;
    private String generalPermit;
    

    public String getGeneralPermit() {
		return generalPermit;
	}

	public void setGeneralPermit(String generalPermit) {
		this.generalPermit = generalPermit;
	}

	public void populate(ResultSet rs) throws SQLException {
        setApplicationId(rs.getInt("application_id"));
        setApplicationTypeCd(rs.getString("application_type_cd"));
        setApplicationNumber(rs.getString("application_nbr"));
        setPreviousApplicationNumber(rs.getString("previous_application_nbr"));
        setReceivedDate(rs.getTimestamp("received_date"));
        setSubmittedDate(rs.getTimestamp("submitted_date"));
        setFpId(rs.getInt("fp_id"));
        setFacilityId(rs.getString("facility_id"));
        setFacilityName(rs.getString("facility_nm"));
        setDoLaaCd(rs.getString("do_laa_cd"));
        setCountyCd(rs.getString("county_cd"));
        setCompanyName(rs.getString("company_nm"));
        setCmpId(rs.getString("cmp_id"));
        setGeneralPermit(rs.getString("general_permit"));
        
    }

    public boolean isRelocationRequest() {
        /*
    	if (getApplicationTypeCd().equals(ApplicationTypeDef.INTENT_TO_RELOCATE) || getApplicationTypeCd().equals(ApplicationTypeDef.SITE_PRE_APPROVAL) || getApplicationTypeCd().equals(ApplicationTypeDef.RELOCATE_TO_PREAPPROVED_SITE)) {
            return true;
        }
        */
        return false;
    }

    public boolean isDelegationRequest() {
        if (getApplicationTypeCd().equals(ApplicationTypeDef.DELEGATION_OF_RESPONSIBILITY)) {
            return true;
        }
        return false;
    }
    
    public final Integer getApplicationId() {
        return applicationId;
    }

    public final void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public final String getApplicationNumber() {
        return applicationNumber;
    }

    public final void setApplicationNumber(String applicationNbr) {
        this.applicationNumber = applicationNbr;
    }

    public final String getApplicationTypeCd() {
        return applicationTypeCd;
    }

    public final void setApplicationTypeCd(String applicationTypeCd) {
        this.applicationTypeCd = applicationTypeCd;
    }

    public final String getCountyCd() {
        return countyCd;
    }

    public final void setCountyCd(String countyCd) {
        this.countyCd = countyCd;
    }

    public final String getDoLaaCd() {
        return doLaaCd;
    }

    public final void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    public final String getFacilityId() {
        return facilityId;
    }

    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public final String getFacilityName() {
        return facilityName;
    }

    public final void setFacilityName(String facilityNm) {
        this.facilityName = facilityNm;
    }

    public final Integer getFpId() {
        return fpId;
    }

    public final void setFpId(Integer fpId) {
        this.fpId = fpId;
    }

    public final String getPreviousApplicationNumber() {
        return previousApplicationNumber;
    }

    public final void setPreviousApplicationNumber(String previousApplicationNbr) {
        this.previousApplicationNumber = previousApplicationNbr;
    }


    public final String getCompanyName() {
        return companyName;
    }

    public final void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public final String getCmpId() {
        return cmpId;
    }

    public final void setCmpId(String cmpId) {
        this.cmpId = cmpId;
    }

    public final Timestamp getSubmittedDate() {
        return submittedDate;
    }

    public final void setSubmittedDate(Timestamp submittedDate) {
        this.submittedDate = submittedDate;
    }

    public final Timestamp getReceivedDate() {
        return receivedDate;
    }

    public final void setReceivedDate(Timestamp receivedDate) {
        this.receivedDate = receivedDate;
    }

    public final String getPermitNumbers() {
        return permitNumbers;
    }

    public final void setPermitNumbers(String permitNumbers) {
        this.permitNumbers = permitNumbers;
    }

}
