package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.FireVariableNamesDef;

@SuppressWarnings("serial")
public class EmissionsVariable extends BaseDB implements
        Comparable<EmissionsVariable>, Serializable {
    private Integer emissionPeriodId;

    private String variable;

    private String value; // amount of material.

    private String meaning; // meaning of the value.

    private Double valueV;

    private boolean tradeSecret; // trade Secret for throughput

    private String tradeSecretText;

    // Not in DATABASE
    private boolean belongs; // Should variable be kept

    private boolean fromComparisonRpt; // indicate if report or comparison
                                        // report.

    private boolean diffMark; // Is there difference?

    private Integer reportYear;
    
    public EmissionsVariable() {
        super();
    }
    
    public EmissionsVariable(EmissionsVariable e) {
        super();
        this.meaning = e.meaning;
        this.tradeSecret = e.tradeSecret;
        this.value = e.value;
        this.valueV = e.valueV;
        this.variable = e.variable;
        this.reportYear = e.reportYear;
    }
    
    public void copyNameOnly(EmissionsVariable e) {
        this.meaning = e.meaning;
        this.variable = e.variable;
    }

    public EmissionsVariable(String variable) {
        super();
        this.variable = variable;
        this.meaning = FireVariableNamesDef.getData().getItems().
            getItemDesc(getVariable());
        this.belongs = true;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public final int compareTo(EmissionsVariable compObj) {
        /*
         * Compares this object with the specified object for order. Returns a
         * negative integer, zero, or a positive integer as this object is less
         * than, equal to, or greater than the specified object.
         */
        if (0 != this.variable.compareTo(compObj.variable)) {
            return this.variable.compareTo(compObj.variable); // names are
                                                                // different
        }
        if (this.fromComparisonRpt) {
            return 1;
        }
        return -1;
    }

    public final void populate(ResultSet rs) {
        try {
            setEmissionPeriodId(AbstractDAO
                    .getInteger(rs, "emission_period_id"));
            setVariable(rs.getString("variable"));
            setMeaning(FireVariableNamesDef.getData().getItems().
                  getItemDesc(getVariable()));
            setValue(rs.getString("value"));
            valueV = EmissionsReport.convertStringToNum(getValue(), logger);
            // setTradeSecretText(rs.getString("value_ts_just"));
            // setTradeSecret(AbstractDAO.translateIndicatorToBoolean(rs
            // .getString("value_ts")));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
    }

    static public List<EmissionsVariable> variablesComparison(
            EmissionsReportPeriod orig, EmissionsReportPeriod comp,
            Integer percentDiff, Integer origYear, Integer compYear) {
        List<EmissionsVariable> varsOrig;
        List<EmissionsVariable> varsC;
        if (orig == null) {
            varsOrig = new ArrayList<EmissionsVariable>(0);
        } else {
            varsOrig = orig.getVars();
            for (EmissionsVariable v : varsOrig) {
                v.setTradeSecret(orig.isTradeSecretS());
                v.setReportYear(origYear);
            }
        }
        if (comp == null) {
            varsC = new ArrayList<EmissionsVariable>(0);
        } else {
            varsC = comp.getVars();
            for (EmissionsVariable v : varsC) {
                v.setTradeSecret(comp.isTradeSecretS());
                v.setReportYear(compYear);
            }
        }
        return order(compareEmissions(varsOrig, varsC, percentDiff, origYear, compYear));
    }

    static ArrayList<EmissionsVariable> compareEmissions(
            List<EmissionsVariable> varsOrig, List<EmissionsVariable> varsC,
            Integer percentDiff, Integer origYear, Integer compYear) {
        float diffPercent = percentDiff.intValue() / 100.0f;
        ArrayList<EmissionsVariable> varsComp = new ArrayList<EmissionsVariable>(
                varsC);
        ArrayList<EmissionsVariable> compList = new ArrayList<EmissionsVariable>();
        for (EmissionsVariable v : varsOrig) {
            EmissionsVariable rowC = getComparison(varsComp, compYear, v);
            v.markDiff(rowC, diffPercent);
            compList.add(v);
            compList.add(rowC);
        }
        // Go through remaining
        for (EmissionsVariable vC : varsComp) {
            vC.fromComparisonRpt = true;
            EmissionsVariable vO = new EmissionsVariable();
            vO.copyNameOnly(vC);
            vO.setReportYear(origYear);
            vO.markDiff(vC, diffPercent);
            compList.add(vO);
            compList.add(vC);
        }
        return compList;
    }

    private static ArrayList<EmissionsVariable> order(
            List<EmissionsVariable> list) {
        // put into order
        ArrayList<EmissionsVariable> alRows = new ArrayList<EmissionsVariable>();
        TreeSet<EmissionsVariable> tRows = new TreeSet<EmissionsVariable>();
        // Put into TreeSet to do the ordering
        for (EmissionsVariable r : list) {
            tRows.add(r);
        }
        // Convert to a list since TreeSort does not work in wrapper
        for (EmissionsVariable r : tRows) {
            alRows.add(r);
        }
        return alRows;
    }

    static EmissionsVariable getComparison(List<EmissionsVariable> lst, Integer lstYear,
            EmissionsVariable e) {
        Iterator<EmissionsVariable> i = lst.listIterator();
        EmissionsVariable v = null;
        while (i.hasNext()) {
            EmissionsVariable vv = i.next();
            if (vv.getVariable().equals(e.getVariable())) {
                v = vv;
                i.remove();
                break;
            }
        }
        if (v == null) {
            v = new EmissionsVariable();
            v.copyNameOnly(e);
            v.setReportYear(lstYear);
        }
        v.fromComparisonRpt = true;
        return v;
    }

    void markDiff(EmissionsVariable e, float diff) {
        if (e.value == null && this.value != null || e.value != null
                && this.value == null) {
            this.diffMark = true;
        } else if (e.value != null && this.value != null) {
            this.diffMark = flagDifference(e.valueV, this.valueV, diff);
        } else
            this.diffMark = false;
    }

    static boolean flagDifference(double cf, double of, double diffPercent) {
        double diff = cf * diffPercent; // find % difference
        if (of < cf - diff || of > cf + diff) {
            return true;
        }
        return false;
    }

    public Integer getEmissionPeriodId() {
        return emissionPeriodId;
    }

    public void setEmissionPeriodId(Integer emissionPeriodId) {
        this.emissionPeriodId = emissionPeriodId;
    }

    public boolean isTradeSecret() {
        return tradeSecret;
    }

    public void setTradeSecret(boolean tradeSecret) {
        this.tradeSecret = tradeSecret;
    }

    public String getTradeSecretText() {
        return tradeSecretText;
    }

    public void setTradeSecretText(String tradeSecretText) {
        this.tradeSecretText = tradeSecretText;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME
                * result
                + ((emissionPeriodId == null) ? 0 : emissionPeriodId.hashCode());
        result = PRIME * result
                + ((variable == null) ? 0 : variable.hashCode());
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
        final EmissionsVariable other = (EmissionsVariable) obj;
        if (emissionPeriodId == null) {
            if (other.emissionPeriodId != null)
                return false;
        } else if (!emissionPeriodId.equals(other.emissionPeriodId))
            return false;
        if (variable == null) {
            if (other.variable != null)
                return false;
        } else if (!variable.equals(other.variable))
            return false;
        if (reportYear == null) {
            if (other.reportYear != null)
                return false;
        } else if (!reportYear.equals(other.reportYear))
            return false;
        return true;
    }

    public boolean isBelongs() {
        return belongs;
    }

    public void setBelongs(boolean belongs) {
        this.belongs = belongs;
    }

    public boolean isFromComparisonRpt() {
        return fromComparisonRpt;
    }

    public boolean isDiffMark() {
        return diffMark;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = null;
        if (value != null && value.length() > 0) {
            this.value = value;
        }
        valueV = EmissionsReport.convertStringToNum(this.value, logger);
    }

    public Double getValueV() {
        return valueV;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
    
    public final Integer getReportYear() {
        return reportYear;
    }

    public final void setReportYear(Integer reportYear) {
        this.reportYear = reportYear;
    }
}