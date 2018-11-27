package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

// RY: This file is not needed anymore; can be deleted

/**
 * @author Kbradley
 * 
 */
/* TODO: RY: This file will be deleted later */
@SuppressWarnings("serial")
public class EmissionUnitList extends FacilityNode {
    private String dapcDescription;
    private Timestamp euInstallDate;
    private Timestamp euStartupDate;
    private OperatingStatus operatingStatus;
    private String operatingStatusDsc;
    private String ptiStatusDsc;
    private String opPermStatusDsc;
    private String tvClassDsc;
    private String exemptStatusDsc;

    public EmissionUnitList() {
    }

    /**
     * @return
     */
    public final Timestamp getEuInstallDate() {
        return euInstallDate;
    }

    /**
     * @param euInstallDate
     */
    public final void setEuInstallDate(Timestamp euInstallDate) {
        this.euInstallDate = euInstallDate;
    }

    /**
     * @return
     */
    public final Timestamp getEuStartupDate() {
        return euStartupDate;
    }

    /**
     * @param euStartupDate
     */
    public final void setEuStartupDate(Timestamp euStartupDate) {
        this.euStartupDate = euStartupDate;
    }

    /**
     * @param operatingStatus
     */
    public final void setOperatingStatus(OperatingStatus operatingStatus) {
        this.operatingStatus = operatingStatus;
    }

    /**
     * @return
     */
    public final OperatingStatus getOperatingStatus() {
        return operatingStatus;
    }

    public final String getDapcDescription() {
        return dapcDescription;
    }

    public final void setDapcDescription(String dapcDescription) {
        this.dapcDescription = dapcDescription;
    }

    /**
     * @return
     */
    public final String getOperatingStatusDsc() {
        return operatingStatusDsc;
    }

    /**
     * @param operatingStatusDsc
     */
    public final void setOperatingStatusDsc(String operatingStatusDsc) {
        this.operatingStatusDsc = operatingStatusDsc;
    }

    /**
     * @return
     */
    public final String getPtiStatusDsc() {
        return ptiStatusDsc;
    }

    /**
     * @param operatingStatusDsc
     */
    public final void setPtiStatusDsc(String ptiStatusDsc) {
        this.ptiStatusDsc = ptiStatusDsc;
    }

    /**
     * @return
     */
    public final String getOpPermStatusDsc() {
        return opPermStatusDsc;
    }

    /**
     * @param operatingStatusDsc
     */
    public final void setOpPermStatusDsc(String opPermStatusDsc) {
        this.opPermStatusDsc = opPermStatusDsc;
    }

    /**
     * @return
     */
    public final String getTvClassDsc() {
        return tvClassDsc;
    }

    /**
     * @param operatingStatusDsc
     */
    public final void setTvClassDsc(String tvClassDsc) {
        this.tvClassDsc = tvClassDsc;
    }

    /**
     * @return
     */
    public final String getExemptStatusDsc() {
        return exemptStatusDsc;
    }

    /**
     * @param operatingStatusDsc
     */
    public final void setExemptStatusDsc(String exemptStatusDsc) {
        this.exemptStatusDsc = exemptStatusDsc;
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            super.populate(rs);
            setEuStartupDate(rs.getTimestamp("startup_dt"));
            setEuInstallDate(rs.getTimestamp("install_dt"));
            // setDapcDescription(rs.getString("dapc_dsc"));

            OperatingStatus tempOperatingStatus = new OperatingStatus();
            tempOperatingStatus.populate(rs);
            setOperatingStatus(tempOperatingStatus);
            setOperatingStatusDsc(operatingStatus.getOperatingStatusDsc());

        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }
}
