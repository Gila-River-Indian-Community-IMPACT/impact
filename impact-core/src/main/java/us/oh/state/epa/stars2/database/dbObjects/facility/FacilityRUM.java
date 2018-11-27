package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.RUMDispositionDef;

public class FacilityRUM extends BaseDB {
    private Integer rumId;
    private String undeliverableAddress;
    private Integer userId;
    private String facilityId;
    private Timestamp originalMailDt;
    private Timestamp createdDt;
    private String correspondenceCategory;
    private String correspondenceCategoryCd;
    private String disposition;
    private String dapcNote;
    private String contactName;
    private String userName;
    private String reasonDesc;
    private String reasonCd;
    private boolean isNewRecord;

    public FacilityRUM() {
    }

    public final String getDispositionDesc() {
        return RUMDispositionDef.getData().getItems().getItemDesc(disposition);
    }

    public final String getContactName() {
        return contactName;
    }

    public final void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public final String getUserName() {
        return userName;
    }

    public final void setUserName(String userName) {
        this.userName = userName;
    }

    public final void populate(ResultSet rs) {
        try {
            setRumId(AbstractDAO.getInteger(rs, "rum_id"));
            setUndeliverableAddress(rs.getString("undeliverable_address"));
            setUserId(AbstractDAO.getInteger(rs, "user_id"));
            setReasonDesc(rs.getString("reason"));
            setReasonCd(rs.getString("reasonCd"));
            setDapcNote(rs.getString("dapc_note"));
            setFacilityId(rs.getString("facility_id"));
            setOriginalMailDt(rs.getTimestamp("orig_mail_dt"));
            setCreatedDt(rs.getTimestamp("created_dt"));
            setDisposition(rs.getString("disposition"));
            setCategoryCd(rs.getString("categoryCd"));
            setCorrespondenceCategory(rs.getString("category"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
            setDirty(false);
            isNewRecord = false;
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }

    public final String getCorrespondenceCategory() {
        return correspondenceCategory;
    }

    public final void setCorrespondenceCategory(String correspondenceCategory) {
        this.correspondenceCategory = correspondenceCategory;
    }

    public final String getDapcNote() {
        return dapcNote;
    }

    public final void setDapcNote(String dapcNote) {
        this.dapcNote = dapcNote;
    }

    public final String getDisposition() {
        return disposition;
    }

    public final void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public final String getFacilityId() {
        return facilityId;
    }

    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public final Timestamp getOriginalMailDt() {
        return originalMailDt;
    }

    public final void setOriginalMailDt(Timestamp originalMailDt) {
        this.originalMailDt = originalMailDt;
    }

    public final Integer getRumId() {
        return rumId;
    }

    public final void setRumId(Integer rumId) {
        this.rumId = rumId;
    }

    public final Integer getUserId() {
        return userId;
    }

    public final void setUserId(Integer userId) {
        this.userId = userId;
    }

    public final String getCategoryCd() {
        return correspondenceCategoryCd;
    }

    public final void setCategoryCd(String correspondenceCategoryCd) {
        this.correspondenceCategoryCd = correspondenceCategoryCd;
    }

    public final String getUndeliverableAddress() {
        return undeliverableAddress;
    }

    public final void setUndeliverableAddress(String undeliverableAddress) {
        this.undeliverableAddress = undeliverableAddress;
    }

    public final String getReasonDesc() {
        return reasonDesc;
    }

    public final void setReasonDesc(String reasonDesc) {
        this.reasonDesc = reasonDesc;
    }

    public final String getReasonCd() {
        return reasonCd;
    }

    public final void setReasonCd(String reasonCd) {
        this.reasonCd = reasonCd;
    }

    public final Timestamp getCreatedDt() {
        return createdDt;
    }

    public final void setCreatedDt(Timestamp createdDt) {
        this.createdDt = createdDt;
    }

    public boolean isNewRecord() {
        return isNewRecord;
    }

    public void setNewRecord(boolean isNewRecord) {
        this.isNewRecord = isNewRecord;
    }
}
