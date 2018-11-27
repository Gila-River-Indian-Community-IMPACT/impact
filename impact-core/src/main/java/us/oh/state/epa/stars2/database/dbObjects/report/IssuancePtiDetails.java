package us.oh.state.epa.stars2.database.dbObjects.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class IssuancePtiDetails extends BaseDB {

    private Integer permitId;
    private String permitNumber;
    private String facilityId;
    private String facilityName;
    private String doLaaName;
    private String reasonDsc;
    private String issuanceTypeCd;
    private String issuanceTypeDsc;
    private String generalPermitFlag;
    private Timestamp effectiveDt;
    private Timestamp expirationDt;
    private Timestamp issuanceDt;
    private Timestamp draftIssuanceDt;
    private Timestamp finalIssuanceDt;
    private Timestamp publicationDt;
    private Timestamp endOfCommentDt;
    private Float invoiceAmount;
    private Integer euCount;

    public IssuancePtiDetails() {
        invoiceAmount = new Float(0.0);
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
            setGeneralPermitFlag(rs.getString("general_permit_flag"));
            setIssuanceDt(rs.getTimestamp("issuance_date"));
            setEffectiveDt(rs.getTimestamp("effective_date"));
            setExpirationDt(rs.getTimestamp("expiration_date"));
            setPublicationDt(rs.getTimestamp("public_notice_publish_date"));
            setEndOfCommentDt(rs.getTimestamp("public_comment_end_date"));
            
            if (getExpirationDt() == null && getEffectiveDt() != null){
                Calendar td = new GregorianCalendar();
                td.setTimeInMillis(getEffectiveDt().getTime());
                td.add(Calendar.MONTH, 18);
                setExpirationDt(new Timestamp(td.getTimeInMillis()));
            }
        }
        catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
        
        try {
            setEuCount(AbstractDAO.getInteger(rs, "eu_count"));
            setDraftIssuanceDt(rs.getTimestamp("draft_date"));
            setFinalIssuanceDt(rs.getTimestamp("final_date"));
        } catch (SQLException e) {
        }
    }
    
    public final Timestamp getDraftIssuanceDt() {
        return draftIssuanceDt;
    }

    public final Timestamp getFinalIssuanceDt() {
        return finalIssuanceDt;
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

    public final String getGeneralPermitFlag() {
        return generalPermitFlag;
    }

    public final void setGeneralPermitFlag(String generalPermitFlag) {
        this.generalPermitFlag = generalPermitFlag;
    }

    public final Float getInvoiceAmount() {
        return invoiceAmount;
    }

    public final void setInvoiceAmount(Float invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
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

    public final String getIssuanceTypeDsc() {
        return issuanceTypeDsc;
    }

    public final void setIssuanceTypeDsc(String issuanceTypeDsc) {
        this.issuanceTypeDsc = issuanceTypeDsc;
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
     * @return the expirationDt
     */
    public final Timestamp getExpirationDt() {
        return expirationDt;
    }

    /**
     * @param expirationDt the expirationDt to set
     */
    public final void setExpirationDt(Timestamp expirationDt) {
        this.expirationDt = expirationDt;
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
     * @param draftIssuanceDt the draftIssuanceDt to set
     */
    public final void setDraftIssuanceDt(Timestamp draftIssuanceDt) {
        this.draftIssuanceDt = draftIssuanceDt;
    }

    /**
     * @param finalIssuanceDt the finalIssuanceDt to set
     */
    public final void setFinalIssuanceDt(Timestamp finalIssuanceDt) {
        this.finalIssuanceDt = finalIssuanceDt;
    }
}
