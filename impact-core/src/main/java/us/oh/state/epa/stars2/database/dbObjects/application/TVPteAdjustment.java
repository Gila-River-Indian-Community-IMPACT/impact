package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.PollutantDef;

@SuppressWarnings("serial")
public class TVPteAdjustment extends BaseDB {
    private Integer applicationId;
    private String pollutantCd;
    private String euEmissionTableCd;
    /**
     * Major status is calculated and set in BO and not stored in the database.
     */
    private String majorStatus;
    /**
     * pteEUTotal is not stored in the database, but is recalculated
     * each time it is accessed (calculation occurs in TVApplication class).
     */
    private String pteEUTotal = "0";
    /**
     * co2Equivalent is not stored in the database, but is recalculated
     * each time it is accessed (calculation occurs in TVApplication class).
     */
    private String co2Equivalent = "0";
    /**
     * pteAdjusted is an alternate PTE value entered by the user and stored
     * in the database. NOTE: the name of the database column is pte_adjustment
     * since this value was previously an adjustment to the calculated value.
     * It is not likely the column name will be changed at this point.
     */
    private String pteAdjusted;
    
    public TVPteAdjustment() {
        super();
    }
    
    public TVPteAdjustment(TVPteAdjustment old) {
        super(old);
        if (old != null) {
            this.applicationId = old.applicationId;
            this.pollutantCd = old.pollutantCd;
            this.euEmissionTableCd = old.euEmissionTableCd;
            this.pteEUTotal = old.pteEUTotal;
            this.co2Equivalent = old.co2Equivalent;
            this.pteAdjusted = old.pteAdjusted;
        }
    }
    
    public TVPteAdjustment(Integer applicationId, String euEmissionTableCd, 
            String pollutantCd) {
        this.applicationId = applicationId;
        this.pollutantCd = pollutantCd;
        this.euEmissionTableCd = euEmissionTableCd;
        this.pteEUTotal = "0";
        this.co2Equivalent = "0";
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((applicationId == null) ? 0 : applicationId.hashCode());
        result = PRIME * result + ((euEmissionTableCd == null) ? 0 : euEmissionTableCd.hashCode());
        result = PRIME * result + ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
        result = PRIME * result + ((pteAdjusted == null) ? 0 : pteAdjusted.hashCode());
        result = PRIME * result + ((pteEUTotal == null) ? 0 : pteEUTotal.hashCode());
        result = PRIME * result + ((co2Equivalent == null) ? 0 : co2Equivalent.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TVPteAdjustment other = (TVPteAdjustment) obj;
        if (applicationId == null) {
            if (other.applicationId != null)
                return false;
        } else if (!applicationId.equals(other.applicationId))
            return false;
        if (euEmissionTableCd == null) {
            if (other.euEmissionTableCd != null)
                return false;
        } else if (!euEmissionTableCd.equals(other.euEmissionTableCd))
            return false;
        if (pollutantCd == null) {
            if (other.pollutantCd != null)
                return false;
        } else if (!pollutantCd.equals(other.pollutantCd))
            return false;
        if (pteAdjusted == null) {
            if (other.pteAdjusted != null)
                return false;
        } else if (!pteAdjusted.equals(other.pteAdjusted))
            return false;
        if (pteEUTotal == null) {
            if (other.pteEUTotal != null)
                return false;
        } else if (!pteEUTotal.equals(other.pteEUTotal))
            return false;
        if (co2Equivalent == null) {
            if (other.co2Equivalent != null)
                return false;
        } else if (!co2Equivalent.equals(other.co2Equivalent))
            return false;
        return true;
    }

    public void populate(ResultSet rs) throws SQLException {
        setApplicationId(AbstractDAO.getInteger(rs, "application_id"));
        setPollutantCd(rs.getString("pollutant_cd"));
        setEuEmissionTableCd(rs.getString("eu_emission_table_cd"));
        setPteAdjusted(rs.getString("pte_adjustment"));
        setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
    }
    public final Integer getApplicationId() {
        return applicationId;
    }
    public final void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }
    public final String getEuEmissionTableCd() {
        return euEmissionTableCd;
    }
    public final void setEuEmissionTableCd(String euEmissionTableCd) {
        this.euEmissionTableCd = euEmissionTableCd;
    }
    public final String getPollutantCd() {
        return pollutantCd;
    }
    public final void setPollutantCd(String pollutantCd) {
        this.pollutantCd = pollutantCd;
    }
    public final String getPteAdjusted() {
        return pteAdjusted;
    }
    public final void setPteAdjusted(String pteAdjustment) {
        this.pteAdjusted = pteAdjustment;
    }
    public final String getPteEUTotal() {
        return pteEUTotal;
    }
    public final void setPteEUTotal(String pteCalculated) {
        this.pteEUTotal = pteCalculated;
    }
    public final String getCo2Equivalent() {
        return co2Equivalent;
    }
    public final void setCo2Equivalent(String co2Equivalent) {
        this.co2Equivalent = co2Equivalent;
    }
    public final String getPteFinal() {
        String finalPte = pteEUTotal;
        if (pteAdjusted != null) {
            finalPte = pteAdjusted;
        }
        return finalPte;
    }

    public final String getMajorStatus() {
        return majorStatus;
    }

    public final void setMajorStatus(String majorStatus) {
        this.majorStatus = majorStatus;
    }
    
    /**
     * test to see if PTE is HAP total or HAP max
     * @return
     */
    public boolean isHapStat() {
        return (PollutantDef.HTOT_CD.equals(pollutantCd) || 
                PollutantDef.HMAX_CD.equals(pollutantCd));
    }
}
