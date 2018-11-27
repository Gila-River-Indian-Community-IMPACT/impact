package us.oh.state.epa.stars2.database.dbObjects.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class IssuanceTvDetails extends BaseDB {

    private Integer permitId;
    private String permitNumber;
    private String facilityId;
    private String facilityName;
    private String doLaaName;
    private String reasonDsc;
    private String issuanceTypeCd;
    private String issuanceTypeDsc;
    private String issuanceStatusCd;
    private Timestamp effectiveDt;
    private Timestamp expirationDt;
    private Timestamp issuanceDt;
    private Timestamp draftIssuanceDt;
    private Timestamp ppIssuanceDt;
    private Timestamp pppIssuanceDt;
    private Timestamp finalIssuanceDt;
    private Timestamp publicationDt;
    private Timestamp endOfCommentDt;
    private Integer euCount;
    
    // new field added for Mantis 2818
    private String permitClassCd;

    public IssuanceTvDetails() {
    }

    public final void populate(ResultSet rs) {

        try {
            setPermitId(AbstractDAO.getInteger(rs, "permit_id"));
            setPermitNumber(rs.getString("permit_nbr"));
            setFacilityId(rs.getString("facility_id"));
            setFacilityName(rs.getString("facility_nm"));
            setDoLaaName(rs.getString("do_laa_dsc"));
            setReasonDsc(rs.getString("reason_dsc"));
            setIssuanceTypeCd(rs.getString("issuance_type_cd"));
            setIssuanceTypeDsc(rs.getString("issuance_type_dsc"));
            setIssuanceStatusCd(rs.getString("issuance_status_cd"));
            setIssuanceDt(rs.getTimestamp("issuance_date"));
            setEffectiveDt(rs.getTimestamp("effective_date"));
            setPublicationDt(rs.getTimestamp("public_notice_publish_date"));
            setEndOfCommentDt(rs.getTimestamp("public_comment_end_date"));
            setExpirationDt(rs.getTimestamp("expiration_date"));
            setPermitClassCd(rs.getString("permit_classification_cd"));
        }
        catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
        
        try {
            setEuCount(AbstractDAO.getInteger(rs, "eu_count"));
            setDraftIssuanceDt(rs.getTimestamp("draft_date"));
            setPpIssuanceDt(rs.getTimestamp("pp_date"));
            setPppIssuanceDt(rs.getTimestamp("ppp_date"));
            setFinalIssuanceDt(rs.getTimestamp("final_date"));
        }
        catch (SQLException sqle) {
        }
    }
    
    public final String getIssuanceTypeCd() {
        return issuanceTypeCd;
    }

    public final void setIssuanceTypeCd(String issuanceTypeCd) {
        this.issuanceTypeCd = issuanceTypeCd;
    }

    public final Timestamp getIssuanceDt() {
        return issuanceDt;
    }

    public final void setIssuanceDt(Timestamp issuanceDt) {
        this.issuanceDt = issuanceDt;
    }

    public final Timestamp getEffectiveDt() {
        return effectiveDt;
    }

    public final void setEffectiveDt(Timestamp effectiveDt) {
        this.effectiveDt = effectiveDt;
    }

    public final Timestamp getEndOfCommentDt() {
        return endOfCommentDt;
    }

    public final void setEndOfCommentDt(Timestamp endOfCommentDt) {
        this.endOfCommentDt = endOfCommentDt;
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

    public final Timestamp getExpirationDt() {
        return expirationDt;
    }

    public final void setExpirationDt(Timestamp expirationDt) {
        this.expirationDt = expirationDt;
    }

    public final Integer getPermitId() {
        return permitId;
    }

    public final void setPermitId(Integer permitId) {
        this.permitId = permitId;
    }

    public final String getPermitNumber() {
        return permitNumber;
    }

    public final void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }

    public final String getIssuanceStatusCd() {
        return issuanceStatusCd;
    }

    public final void setIssuanceStatusCd(String issuanceStatusCd) {
        this.issuanceStatusCd = issuanceStatusCd;
    }

    public final Timestamp getPublicationDt() {
        return publicationDt;
    }

    public final void setPublicationDt(Timestamp publicationDt) {
        this.publicationDt = publicationDt;
    }

    public final String getReasonDsc() {
        return reasonDsc;
    }

    public final void setReasonDsc(String reasonDsc) {
        this.reasonDsc = reasonDsc;
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

    /**
     * @return the draftIssuanceDt
     */
    public final Timestamp getDraftIssuanceDt() {
        return draftIssuanceDt;
    }

    /**
     * @param draftIssuanceDt the draftIssuanceDt to set
     */
    public final void setDraftIssuanceDt(Timestamp draftIssuanceDt) {
        this.draftIssuanceDt = draftIssuanceDt;
    }

    /**
     * @return the finalIssuanceDt
     */
    public final Timestamp getFinalIssuanceDt() {
        return finalIssuanceDt;
    }

    /**
     * @param finalIssuanceDt the finalIssuanceDt to set
     */
    public final void setFinalIssuanceDt(Timestamp finalIssuanceDt) {
        this.finalIssuanceDt = finalIssuanceDt;
    }

    /**
     * @return the ppIssuanceDt
     */
    public final Timestamp getPpIssuanceDt() {
        return ppIssuanceDt;
    }

    /**
     * @param ppIssuanceDt the ppIssuanceDt to set
     */
    public final void setPpIssuanceDt(Timestamp ppIssuanceDt) {
        this.ppIssuanceDt = ppIssuanceDt;
    }

    /**
     * @return the pppIssuanceDt
     */
    public final Timestamp getPppIssuanceDt() {
        return pppIssuanceDt;
    }

    /**
     * @param pppIssuanceDt the pppIssuanceDt to set
     */
    public final void setPppIssuanceDt(Timestamp pppIssuanceDt) {
        this.pppIssuanceDt = pppIssuanceDt;
    }

    /**
     * @return the issuanceTypeDsc
     */
    public final String getIssuanceTypeDsc() {
        return issuanceTypeDsc;
    }

    /**
     * @param issuanceTypeDsc the issuanceTypeDsc to set
     */
    public final void setIssuanceTypeDsc(String issuanceTypeDsc) {
        this.issuanceTypeDsc = issuanceTypeDsc;
    }

	public final String getPermitClassCd() {
		return permitClassCd;
	}

	public final void setPermitClassCd(String permitClassCd) {
		this.permitClassCd = permitClassCd;
	}

}
