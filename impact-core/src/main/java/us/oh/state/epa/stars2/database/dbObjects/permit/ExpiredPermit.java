package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class ExpiredPermit extends BaseDB {

    private Integer permitId;
    private String permitNm;
    private Integer userId;
    private Timestamp expirationDate;
    private Timestamp issuanceDate;
    private String facilityId;
    private Integer corrEpaEmuId;
    private boolean feptio;

    public ExpiredPermit() {
        super();
        setDirty(false);
    }

    public ExpiredPermit(ExpiredPermit old) {

        super(old);
        if (old != null) {
            setCorrEpaEmuId(old.getCorrEpaEmuId());
            setExpirationDate(old.getExpirationDate());
            setFacilityId(old.getFacilityId());
            setIssuanceDate(old.getIssuanceDate());
            setPermitNm(old.getPermitNm());
            setPermitId(old.getPermitId());
            setUserId(old.getUserId());
            setFeptio(old.isFeptio());

            setLastModified(old.getLastModified());
            setDirty(old.isDirty());
        }

    }
    
    public final void populate(ResultSet rs) {

        try {
            setPermitId(AbstractDAO.getInteger(rs, "permit_id"));
            setPermitNm(rs.getString("PERMIT_NBR"));
            setUserId(AbstractDAO.getInteger(rs, "user_id"));
            setExpirationDate(rs.getTimestamp("expiration_date"));
            setIssuanceDate(rs.getTimestamp("issuance_date"));
            setFacilityId(rs.getString("facility_id"));
            setCorrEpaEmuId(AbstractDAO.getInteger(rs, "CORR_EPA_EMU_ID"));
            setFeptio(AbstractDAO.getBoolean(rs, "FE_PTIO_FLAG"));

            setDirty(false);
        }
        catch (SQLException sqle) {
            logger.error("Required field error");
        }
        finally {
            newObject = false;
        }
    }

    public final Timestamp getExpirationDate() {
        return expirationDate;
    }

    public final void setExpirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
        setDirty(true);
    }

    public final String getFacilityId() {
        return facilityId;
    }

    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
        setDirty(true);
    }

    public final Timestamp getIssuanceDate() {
        return issuanceDate;
    }

    public final void setIssuanceDate(Timestamp issuanceDate) {
        this.issuanceDate = issuanceDate;
        setDirty(true);
    }

    public final Integer getPermitId() {
        return permitId;
    }

    public final void setPermitId(Integer permitId) {
        this.permitId = permitId;
        setDirty(true);
    }

    public final Integer getUserId() {
        return userId;
    }

    public final void setUserId(Integer userId) {
        this.userId = userId;
        setDirty(true);
    }

    public final String getPermitNm() {
        return permitNm;
    }

    public final void setPermitNm(String permitNm) {
        this.permitNm = permitNm;
    }

    public final boolean isFeptio() {
        return feptio;
    }

    public final void setFeptio(boolean feptio) {
        this.feptio = feptio;
    }

    public final Integer getCorrEpaEmuId() {
        return corrEpaEmuId;
    }

    public final void setCorrEpaEmuId(Integer corrEpaEmuId) {
        this.corrEpaEmuId = corrEpaEmuId;
        setDirty(true);
    }

}
