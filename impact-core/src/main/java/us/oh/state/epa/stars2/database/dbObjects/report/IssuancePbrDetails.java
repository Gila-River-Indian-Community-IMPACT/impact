package us.oh.state.epa.stars2.database.dbObjects.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class IssuancePbrDetails extends BaseDB {

    private Integer pbrId;
    private String permitNumber;
    private String euId;
    private String euDesc;
    private String facilityId;
    private String facilityName;
    private String doLaaId;
    private String doLaaName;
    private String pbrType;
    private String pbrReason;
    private Timestamp submittedDt;
    private String dispositionCd;

    public IssuancePbrDetails() {
    }

    public final void populate(ResultSet rs) {

        try {
            setPbrId(AbstractDAO.getInteger(rs, "permit_id"));
            setPermitNumber(rs.getString("permit_nbr"));
            setEuId(rs.getString("epa_emu_id"));
            setEuDesc(rs.getString("eu_dapc_dsc"));
            setFacilityId(rs.getString("facility_id"));
            setFacilityName(rs.getString("facility_nm"));
            setDoLaaId(rs.getString("do_laa_id"));
            setDoLaaName(rs.getString("do_laa_dsc"));
            setPbrType(rs.getString("pbr_type_dsc"));
            setPbrReason(rs.getString("pbr_reason_dsc"));
            setSubmittedDt(rs.getTimestamp("received_date"));
            setDispositionCd(rs.getString("disposition_flag"));
            
        } 
        catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }
    
    public final Integer getPbrId() {
        return pbrId;
    }

    public final void setPbrId(Integer pbrId) {
        this.pbrId = pbrId;
    }

    public final String getEuId() {
        return euId;
    }

    public final void setEuId(String euId) {
        this.euId = euId;
    }

    public final String getEuDesc() {
        return euDesc;
    }

    public final void setEuDesc(String euDesc) {
        this.euDesc = euDesc;
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

    public final void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public final String getDoLaaName() {
        return doLaaName;
    }

    public final void setDoLaaName(String doLaaName) {
        this.doLaaName = doLaaName;
    }

    public final String getDispositionCd() {
        return dispositionCd;
    }

    public final void setDispositionCd(String dispositionCd) {
        this.dispositionCd = dispositionCd;
    }

    public final String getPbrReason() {
        return pbrReason;
    }

    public final void setPbrReason(String pbrReason) {
        this.pbrReason = pbrReason;
    }

    public final String getPbrType() {
        return pbrType;
    }

    public final void setPbrType(String pbrType) {
        this.pbrType = pbrType;
    }

    public final Timestamp getSubmittedDt() {
        return submittedDt;
    }

    public final void setSubmittedDt(Timestamp submittedDt) {
        this.submittedDt = submittedDt;
    }
    
	public final String getDoLaaId() {
		return doLaaId;
	}

	public final void setDoLaaId(String doLaaId) {
		this.doLaaId = doLaaId;
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

}
