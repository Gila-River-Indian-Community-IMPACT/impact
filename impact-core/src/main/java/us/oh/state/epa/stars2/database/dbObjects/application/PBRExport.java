package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class PBRExport extends BaseDB {
    private String facilityId;
    private String facilityName;
    private Timestamp receivedDate;
    private String euId;
    private String euIdDescription;
    private String pbrType;
    private String status;
    private Timestamp statusDate;

    public void populate(ResultSet rs) throws SQLException {
        setFacilityId(rs.getString("facility_id"));
        setFacilityName(rs.getString("facility_nm"));
        setReceivedDate(rs.getTimestamp("received_date"));
        setEuId(rs.getString("epa_emu_id"));
        setEuIdDescription(rs.getString("requlated_user_dsc"));
        setPbrType(rs.getString("pbr_type_dsc"));
        setStatus(rs.getString("status"));
        setStatusDate(rs.getTimestamp("effective_date"));
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

    public final Timestamp getReceivedDate() {
        return receivedDate;
    }

    public final void setReceivedDate(Timestamp receivedDate) {
        this.receivedDate = receivedDate;
    }

    public final String getEuId() {
        return euId;
    }

    public final void setEuId(String euId) {
        this.euId = euId;
    }

    public final String getEuIdDescription() {
        return euIdDescription;
    }

    public final void setEuIdDescription(String euIdDescription) {
        this.euIdDescription = euIdDescription;
    }

    public final String getPbrType() {
        return pbrType;
    }

    public final void setPbrType(String pbrType) {
        this.pbrType = pbrType;
    }

    public final String getStatus() {
        return status;
    }

    public final void setStatus(String status) {
        this.status = status;
    }

    public final Timestamp getStatusDate() {
        return statusDate;
    }

    public final void setStatusDate(Timestamp statusDate) {
        this.statusDate = statusDate;
    }

}
