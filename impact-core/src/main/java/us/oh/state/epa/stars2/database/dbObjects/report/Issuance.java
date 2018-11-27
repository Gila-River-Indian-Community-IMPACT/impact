package us.oh.state.epa.stars2.database.dbObjects.report;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class Issuance extends BaseDB {
    private String doLaaId;
    private String doLaaCd;
    private String doLaaName;
    private String permitType;
    private String issuanceType;

    public Issuance() {
    }

    /**
     * Getter for property doLaaCd.
     * 
     * @return Value of property doLaaCd.
     * 
     */
    public final String getDoLaaCd() {
        return doLaaCd;
    }

    /**
     * Setter for property doLaaCd.
     * 
     * @param doLaaCd
     *            New value of property doLaaCd.
     * 
     */
    public final void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    /**
     * Getter for property doLaaName.
     * 
     * @return Value of property minVal.
     * 
     */
    public final String getDoLaaName() {
        return doLaaName;
    }

    /**
     * Setter for property doLaaName.
     * 
     * @param minVal
     *            New value of property doLaaName.
     * 
     */
    public final void setDoLaaName(String doLaaName) {
        this.doLaaName = doLaaName;
    }

    /**
     * Getter for property issuanceType.
     * 
     * @return Value of property issuanceType.
     * 
     */
    public final String getIssuanceType() {
        return issuanceType;
    }

    /**
     * Setter for property issuanceType.
     * 
     * @param minVal
     *            New value of property issuanceType.
     * 
     */
    public final void setIssuanceType(String issuanceType) {
        this.issuanceType = issuanceType;
    }

    /**
     * Getter for property permitType.
     * 
     * @return Value of property permitType.
     * 
     */
    public final String getPermitType() {
        return permitType;
    }

    /**
     * Setter for property permitType.
     * 
     * @param minVal
     *            New value of property permitType.
     * 
     */
    public final void setPermitType(String permitType) {
        this.permitType = permitType;
    }

    public final String getDoLaaId() {
        return doLaaId;
    }

    public final void setDoLaaId(String doLaaId) {
        this.doLaaId = doLaaId;
    }

    public final void populate(ResultSet rs) {
        try {
            setDoLaaCd(rs.getString("do_laa_cd"));
            setDoLaaName(rs.getString("do_laa_dsc"));
            setPermitType(rs.getString("permit_type_cd"));
            setIssuanceType(rs.getString("issuance_type_cd"));
            setDoLaaId(rs.getString("do_laa_id"));

        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }

    @Override
    public final int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((doLaaCd == null) ? 0 : doLaaCd.hashCode());
        result = PRIME * result + ((doLaaId == null) ? 0 : doLaaId.hashCode());
        result = PRIME * result
                + ((doLaaName == null) ? 0 : doLaaName.hashCode());
        result = PRIME * result
                + ((issuanceType == null) ? 0 : issuanceType.hashCode());
        result = PRIME * result
                + ((permitType == null) ? 0 : permitType.hashCode());
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Issuance other = (Issuance) obj;
        if (doLaaCd == null) {
            if (other.doLaaCd != null)
                return false;
        } else if (!doLaaCd.equals(other.doLaaCd))
            return false;
        if (doLaaId == null) {
            if (other.doLaaId != null)
                return false;
        } else if (!doLaaId.equals(other.doLaaId))
            return false;
        if (doLaaName == null) {
            if (other.doLaaName != null)
                return false;
        } else if (!doLaaName.equals(other.doLaaName))
            return false;
        if (issuanceType == null) {
            if (other.issuanceType != null)
                return false;
        } else if (!issuanceType.equals(other.issuanceType))
            return false;
        if (permitType == null) {
            if (other.permitType != null)
                return false;
        } else if (!permitType.equals(other.permitType))
            return false;
        return true;
    }

    public final String getQueryType() {
        return permitType + "." + issuanceType + "." + doLaaName;
    }
}
