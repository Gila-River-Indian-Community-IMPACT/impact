package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class EmissionsMaterialActionUnits extends BaseDB {
    private Integer emissionPeriodId;
    private String material; // reference into NEI material table
                                // (rp_material_def)
    private String action; // reference into FIRE action table (rp_actions)
    private String measure; // reference into NEI unit table (rp_unit_def)
    private String throughput; // amount of material.
    private boolean tradeSecretM; // trade Secret for material
    private String tradeSecretMText;
    private boolean tradeSecretT; // trade Secret for throughput
    private String tradeSecretTText;

    // In-memory data elements
    private transient boolean materialDiff;
    private Double throughputV;  
    private boolean throughputDiff;
    private boolean belongs; // true if material is included in this period.
    private boolean notActive;
    private Integer reportYear;

    public EmissionsMaterialActionUnits() {
        super();
        belongs = false;
    }

    public EmissionsMaterialActionUnits(String material, String action,
            String measure, boolean notActive) {
        super();
        this.material = material;
        this.action = action;
        this.measure = measure;
        this.belongs = false;
        this.notActive = notActive;
    }

    public EmissionsMaterialActionUnits(EmissionsMaterialActionUnits e) {
        super();
        if(e != null) {
            this.material = e.material;
            this.action = e.action;
            this.measure = e.measure;
            this.belongs = e.belongs;
            this.notActive = e.notActive;
            this.reportYear = e.reportYear;
        }
    }

    public final void populateFields(EmissionsMaterialActionUnits e) {
        if(e != null) {
            this.belongs = true;
            this.material = e.material;
            this.action = e.action;
            this.throughput = e.throughput;
            this.throughputV = e.throughputV;
            this.measure = e.measure;
            this.notActive = e.notActive;
            this.emissionPeriodId = e.emissionPeriodId;
            this.tradeSecretM = e.tradeSecretM;
            this.tradeSecretMText = e.tradeSecretMText;
            this.tradeSecretT = e.tradeSecretT;
            this.tradeSecretTText = e.tradeSecretTText;
            this.reportYear = e.reportYear;
        }
    }

    // Get throughput:  There is at most one.
    public final static Double getThroughputV(
            List<EmissionsMaterialActionUnits> maus) {
        Double t = null;
        for(EmissionsMaterialActionUnits mau : maus) {
            if(mau.getThroughput() != null &&
                    mau.getThroughput().length() > 0) {
                t = mau.getThroughputV();
            }
            break;
        }
        return t;
    }
    
    public final Integer getEmissionPeriodId() {
        return emissionPeriodId;
    }

    public final void setEmissionPeriodId(Integer emissionPeriodId) {
        this.emissionPeriodId = emissionPeriodId;
    }

    public final String getAction() {
        return action;
    }

    public final void setAction(String action) {
        this.action = action;
    }

    public final String getMaterial() {
        return material;
    }

    public final void setMaterial(String material) {
        this.material = material;
    }

    public final String getMeasure() {
        return measure;
    }

    public final void setMeasure(String measure) {
        this.measure = measure;
    }

    public final void populate(ResultSet rs) {
        try {
            setEmissionPeriodId(AbstractDAO
                    .getInteger(rs, "emission_period_id"));
            setMaterial(rs.getString("material_cd"));
//            setTradeSecretMText(rs.getString("material_ts_just"));
//            setTradeSecretM(AbstractDAO.translateIndicatorToBoolean(rs
//                    .getString("material_ts")));
            setAction(rs.getString("action_cd"));
            setMeasure(rs.getString("material_unit_cd"));
            setThroughput((rs.getString("throughput")));
            throughputV = EmissionsReport.convertStringToNum(getThroughput(), logger);
            setTradeSecretTText(rs.getString("throughput_ts_just"));
            setTradeSecretT(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("throughput_ts")));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
    }
    
    public boolean materialActionSame(EmissionsMaterialActionUnits o) {
        // must be non null or return false;
        if(o.getMaterial() == null || o.getAction() == null) return false;
        if(o.material.equals(material) && o.action.equals(action)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((action == null) ? 0 : action.hashCode());
        result = PRIME
                * result
                + ((emissionPeriodId == null) ? 0 : emissionPeriodId.hashCode());
        result = PRIME * result
                + ((material == null) ? 0 : material.hashCode());
        result = PRIME * result + ((measure == null) ? 0 : measure.hashCode());
        result = PRIME * result
                + ((throughput == null) ? 0 : throughput.hashCode());
        result = PRIME * result
                + ((reportYear == null) ? 0 : reportYear.hashCode());
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
        final EmissionsMaterialActionUnits other = (EmissionsMaterialActionUnits) obj;
        if (action == null) {
            if (other.action != null)
                return false;
        } else if (!action.equals(other.action))
            return false;
        if (emissionPeriodId == null) {
            if (other.emissionPeriodId != null)
                return false;
        } else if (!emissionPeriodId.equals(other.emissionPeriodId))
            return false;
        if (material == null) {
            if (other.material != null)
                return false;
        } else if (!material.equals(other.material))
            return false;
        if (measure == null) {
            if (other.measure != null)
                return false;
        } else if (!measure.equals(other.measure))
            return false;
        if (throughput == null) {
            if (other.throughput != null)
                return false;
        } else if (!throughput.equals(other.throughput))
            return false;
        if (reportYear == null) {
            if (other.reportYear != null)
                return false;
        } else if (!reportYear.equals(other.reportYear))
            return false;
        return true;
    }

    public final boolean isBelongs() {
        return belongs;
    }

    public final void setBelongs(boolean belongs) {
        this.belongs = belongs;
    }

    public final boolean isTradeSecretM() {
        return tradeSecretM;
    }

    public final void setTradeSecretM(boolean tradeSecretM) {
        this.tradeSecretM = tradeSecretM;
    }

    public final String getTradeSecretMText() {
        return tradeSecretMText;
    }

    public final void setTradeSecretMText(String tradeSecretMText) {
        this.tradeSecretMText = tradeSecretMText;
    }

    public final boolean isTradeSecretT() {
        return tradeSecretT;
    }

    public final void setTradeSecretT(boolean tradeSecretT) {
        this.tradeSecretT = tradeSecretT;
    }

    public final String getTradeSecretTText() {
        return tradeSecretTText;
    }

    public final void setTradeSecretTText(String tradeSecretTText) {
        this.tradeSecretTText = tradeSecretTText;
    }

    public boolean isMaterialDiff() {
        return materialDiff;
    }

    public void setMaterialDiff(boolean materialDiff) {
        this.materialDiff = materialDiff;
    }

    public boolean isThroughputDiff() {
        return throughputDiff;
    }

    public void setThroughputDiff(boolean throughputDiff) {
        this.throughputDiff = throughputDiff;
    }

    public String getThroughput() {
        return throughput;
    }

    public void setThroughput(String throughput) {
        this.throughput = throughput;
        throughputV = EmissionsReport.convertStringToNum(this.throughput, logger);
    }

    public Double getThroughputV() {
        return throughputV;
    }

    public boolean isNotActive() {
        return notActive;
    }

    public void setNotActive(boolean notActive) {
        this.notActive = notActive;
    }
    
    public boolean isBelongsNotActvie() {
        return belongs && notActive;
    }
    
    public final Integer getReportYear() {
        return reportYear;
    }

    public final void setReportYear(Integer reportYear) {
        this.reportYear = reportYear;
    }
}
