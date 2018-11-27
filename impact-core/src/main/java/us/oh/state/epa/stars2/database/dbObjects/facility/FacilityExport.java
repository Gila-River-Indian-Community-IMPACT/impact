package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class FacilityExport extends BaseDB {
    String facilityId;
    String facilityName;
    String addressLine1;
    String addressLine2;
    String city;
    String zip5;
    String zip4;
    String countyName;
    String firstName;
    String lastName;
    String phoneNumber;
    String operatingStatus;
    String permitClassification;
    String naics;
    String emuID;
    String euDescription;
    String euOperatingStatus;
    Timestamp initialInstallDate;
    String processId;
    String sccId;
    
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

    public final String getAddressLine1() {
        return addressLine1;
    }

    public final void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public final String getAddressLine2() {
        return addressLine2;
    }

    public final void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public final String getCity() {
        return city;
    }

    public final void setCity(String city) {
        this.city = city;
    }

    public final String getZip5() {
        return zip5;
    }

    public final void setZip5(String zip5) {
        this.zip5 = zip5;
    }

    public final String getZip4() {
        return zip4;
    }

    public final void setZip4(String zip4) {
        this.zip4 = zip4;
    }

    public final String getCountyName() {
        return countyName;
    }

    public final void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public final String getFirstName() {
        return firstName;
    }

    public final void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public final String getLastName() {
        return lastName;
    }

    public final void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public final String getPhoneNumber() {
        return phoneNumber;
    }

    public final void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public final String getOperatingStatus() {
        return operatingStatus;
    }

    public final void setOperatingStatus(String operatingStatus) {
        this.operatingStatus = operatingStatus;
    }

    public final String getPermitClassification() {
        return permitClassification;
    }

    public final void setPermitClassification(String permitClassification) {
        this.permitClassification = permitClassification;
    }

    public final String getNaics() {
        return naics;
    }

    public final void setNaics(String naics) {
        this.naics = naics;
    }

    public final String getEmuID() {
        return emuID;
    }

    public final void setEmuID(String emuID) {
        this.emuID = emuID;
    }

    public final String getEuDescription() {
        return euDescription;
    }

    public final void setEuDescription(String euDescription) {
        this.euDescription = euDescription;
    }

    public final String getEuOperatingStatus() {
        return euOperatingStatus;
    }

    public final void setEuOperatingStatus(String euOperatingStatus) {
        this.euOperatingStatus = euOperatingStatus;
    }

    public final Timestamp getInitialInstallDate() {
        return initialInstallDate;
    }

    public final void setInitialInstallDate(Timestamp initialInstallDate) {
        this.initialInstallDate = initialInstallDate;
    }

    public final String getProcessId() {
        return processId;
    }

    public final void setProcessId(String processId) {
        this.processId = processId;
    }

    public final String getSccId() {
        return sccId;
    }

    public final void setSccId(String sccId) {
        this.sccId = sccId;
    }
    
    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public void populate(ResultSet rs) {
        try {
            setFacilityId(rs.getString("facility_id"));
            setFacilityName(rs.getString("facility_nm"));
            setAddressLine1(rs.getString("address1"));
            setAddressLine2(rs.getString("address2"));
            setCity(rs.getString("city"));
            setZip5(rs.getString("zip5"));
            setZip4(rs.getString("zip4"));
            setCountyName(rs.getString("county_nm"));
            setFirstName(rs.getString("first_nm"));
            setLastName(rs.getString("last_nm"));
            setPhoneNumber(rs.getString("phone_no"));
            setOperatingStatus(rs.getString("operating_status_dsc"));
            setPermitClassification(rs.getString("permit_classification_dsc"));
            setNaics(rs.getString("naics_dsc"));
            setEmuID(rs.getString("epa_emu_id"));
            setEuDescription(rs.getString("eu_desc"));
            setEuOperatingStatus(rs.getString("eu_operating_status"));
            setInitialInstallDate(rs.getTimestamp("initial_installation_dt"));
            setProcessId(rs.getString("process_id"));
            setSccId(rs.getString("scc_id"));
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
        }
    }

}
