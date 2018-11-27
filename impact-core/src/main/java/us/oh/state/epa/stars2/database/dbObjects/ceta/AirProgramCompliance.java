package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.webcommon.reports.RowContainer;

@SuppressWarnings("serial")
public class AirProgramCompliance extends CetaBaseDB implements Comparable {
   
    private String facilityId;
    private String facilityName;
    private String operatingStatusCd;
    private Timestamp shutdownDate;
    private String doLaaCd;
    private String countyCd;
    private String sipComp;
    private String mactComp;
    private String tvComp;
    private String smComp;
    private String neshapsComp;
    private String nspsComp;
    private String psdComp;
    private String nsrComp;
    

    public AirProgramCompliance() {
        super();
    }
    
    public final int compareTo(Object b) {
        AirProgramCompliance rowB = (AirProgramCompliance)b;
            return facilityId.compareTo(rowB.getFacilityId());
    }
    
    /** Populate this instance from a database ResultSet. */
    public final void populate(java.sql.ResultSet rs) throws SQLException {
        boolean errorFlag = true;
        try{
            setFacilityId(rs.getString("facility_id"));
            setFacilityName(rs.getString("facility_nm"));
            this.setCountyCd(rs.getString("county_cd"));
            setDoLaaCd(rs.getString("do_laa_cd"));
            setOperatingStatusCd(rs.getString("operating_status_cd"));

        } catch(SQLException e) {
            if(errorFlag) {
                logger.error(e.getMessage(), e);
                throw e;
            }
        }
    }

    public String getCountyCd() {
        return countyCd;
    }

    public void setCountyCd(String countyCd) {
        this.countyCd = countyCd;
    }

    public String getDoLaaCd() {
        return doLaaCd;
    }

    public void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getOperatingStatusCd() {
        return operatingStatusCd;
    }

    public void setOperatingStatusCd(String operatingStatusCd) {
        this.operatingStatusCd = operatingStatusCd;
    }

    public String getMactComp() {
        return mactComp;
    }

    public void setMactComp(String mactComp) {
        this.mactComp = mactComp;
    }

    public String getNeshapsComp() {
        return neshapsComp;
    }

    public void setNeshapsComp(String neshapsComp) {
        this.neshapsComp = neshapsComp;
    }

    public String getNspsComp() {
        return nspsComp;
    }

    public void setNspsComp(String nspsComp) {
        this.nspsComp = nspsComp;
    }

    public String getNsrComp() {
        return nsrComp;
    }

    public void setNsrComp(String nsrComp) {
        this.nsrComp = nsrComp;
    }

    public String getPsdComp() {
        return psdComp;
    }

    public void setPsdComp(String psdComp) {
        this.psdComp = psdComp;
    }

    public String getSipComp() {
        return sipComp;
    }

    public void setSipComp(String sipComp) {
        this.sipComp = sipComp;
    }

    public String getSmComp() {
        return smComp;
    }

    public void setSmComp(String smComp) {
        this.smComp = smComp;
    }

    public String getTvComp() {
        return tvComp;
    }

    public void setTvComp(String tvComp) {
        this.tvComp = tvComp;
    }

    public Timestamp getShutdownDate() {
        return shutdownDate;
    }

    public void setShutdownDate(Timestamp shutdownDate) {
        this.shutdownDate = shutdownDate;
    }
}
