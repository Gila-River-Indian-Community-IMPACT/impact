package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class EventLog extends BaseDB {
    private Integer eventLogId;
    private Integer fpId;
    private Integer userId;
    private Timestamp date;
    private Timestamp dateTo;
    private String eventTypeDefCd;
    private String note;
    private String facilityName;
    private String facilityId;
    
    private Integer externalId;

    public final void populate(ResultSet rs) throws SQLException {
        try {
            setEventLogId(AbstractDAO.getInteger(rs, "EVENT_LOG_ID"));
            setFpId(AbstractDAO.getInteger(rs, "FP_ID"));
            setUserId(AbstractDAO.getInteger(rs, "USER_ID"));
            setDate(rs.getTimestamp("EVENT_DATE"));
            setEventTypeDefCd(rs.getString("EVENT_TYPE_CD"));
            setNote(rs.getString("EVENT_NOTE"));
            setLastModified(AbstractDAO.getInteger(rs, "LAST_MODIFIED"));
            setFacilityId(rs.getString("FACILITY_ID"));
            setFacilityName(rs.getString("FACILITY_NM"));
        } catch (SQLException sqle) {
            logger.debug(sqle.getMessage());
        }
    }

    public final Integer getEventLogId() {
        return eventLogId;
    }

    public final void setEventLogId(Integer eventLogId) {
        this.eventLogId = eventLogId;
    }

    public final Timestamp getDate() {
        return date;
    }

    public final void setDate(Timestamp date) {
        this.date = date;
    }

    public final Timestamp getDateTo() {
        return dateTo;
    }

    public final void setDateTo(Timestamp dateTo) {
        this.dateTo = dateTo;
    }

    public final String getEventTypeDefCd() {
        return eventTypeDefCd;
    }

    public final void setEventTypeDefCd(String eventTypeDefCd) {
        this.eventTypeDefCd = eventTypeDefCd;
    }

    public final Integer getFpId() {
        return fpId;
    }

    public final void setFpId(Integer fpId) {
        this.fpId = fpId;
    }

    public final String getNote() {
        return note;
    }

    public final void setNote(String note) {
        this.note = note;
    }

    public final Integer getUserId() {
        return userId;
    }

    public final void setUserId(Integer userId) {
        this.userId = userId;
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

    /**
     * @return the externalId
     */
    public final Integer getExternalId() {
        return externalId;
    }

    /**
     * @param externalId the externalId to set
     */
    public final void setExternalId(Integer externalId) {
        this.externalId = externalId;
    }
}
