package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.webcommon.reports.EmissionRow;

/*  Meaning of emissions:
 *  On database:
 *   fugitive\total         !null                    null
 *      !null         stack = total-fugitive       stack = null
 *       null         stack = total               stack = null
 *       
 *    On Web Page:
 *    if  stack=null   then total=fugitive
 *    if fugitive=null then total=stack
 */

public class Emissions extends BaseDB {
	
	private static final long serialVersionUID = -375698356964274331L;

	private Integer emissionPeriodId;
    private String pollutantCd;
    private String fireRef;
    private String fireRefFactor;
    private String emissionCalcMethodCd;
    private String fugitiveEmissions;
    private String stackEmissions;
    private String emissionsUnitNumerator;
    // annualAdjust is number of hours of uncontrolled
    // operation.  Divide by hours of operation for
    // impact.
    private String annualAdjust;
    private boolean factorNumericValueOverride;
    private String factorNumericValue;
    private String timeBasedFactorNumericValue;
    private String factorUnitNumerator;
    private String factorUnitDenominator;
    private boolean tradeSecretF;
    private String tradeSecretFText;
    private String explanation;
    private boolean tradeSecretE;
    private String tradeSecretEText;
    
    // Not in database
    private boolean visited; // If not visited, then it can be deleted.
    private boolean deletable; // Can user remove from report?
    private boolean removeRow; // Remove the row be because the fire row has expired and user hasn't provided a factor.
    private int firstHalfPercent;
    private int secondHalfPercent;
    private Integer emuID;
    private String epaEmuID;
    private String processID;
    private String SccId;
    private BigDecimal totalAllowable = BigDecimal.ZERO;

	public Emissions() {
        super();
        visited = false;
    }

    public Emissions(Emissions old) {
        super(old);

        if (old != null) {
            setEmissionPeriodId(old.getEmissionPeriodId());
            setPollutantCd(old.getPollutantCd());
            setFireRef(old.getFireRef());
            setFireRefFactor(old.getFireRefFactor());
            // setPrimaryDeviceCd(old.getPrimaryDeviceCd());
            // setSecondaryDeviceCd(old.getSecondaryDeviceCd());
            setEmissionCalcMethodCd(old.getEmissionCalcMethodCd());
            // setStartDate(old.getStartDate());
            // setEndDate(old.getEndDate());
            setFugitiveEmissions(old.getFugitiveEmissions());
            setStackEmissions(old.getStackEmissions());
            setEmissionsUnitNumerator(old.getEmissionsUnitNumerator());
            // setEmReliabilityInd(old.getEmReliabilityInd());
            setFactorNumericValueOverride(old.getFactorNumericValueOverride());
            setFactorNumericValue(old.getFactorNumericValue());
            setTimeBasedFactorNumericValue(old.getTimeBasedFactorNumericValue());
            setFactorUnitNumerator(old.getFactorUnitNumerator());
            setFactorUnitDenominator(old.getFactorUnitDenominator());
            // setEfReliabilityCd(old.getEfReliabilityCd());
            // setRuleEffectiveness(old.getRuleEffectiveness());
            // setControlStatus(old.getControlStatus());
            // setEmissionsDataLevel(old.getEmissionsDataLevel());
            // setEmissionsTypeCd(old.getEmissionsTypeCd());
            setAnnualAdjust(old.getAnnualAdjust());
            setExplanation(old.getExplanation());
            setTradeSecretE(old.isTradeSecretE());
            setTradeSecretEText(old.getTradeSecretEText());
            setTradeSecretF(old.isTradeSecretF());
            setTradeSecretFText(old.getTradeSecretFText());
        }
    }
    
    public Emissions(EmissionRow er, Integer periodId) {
    	
        setEmissionPeriodId(periodId);
        setPollutantCd(er.getPollutantCd());
        setFireRef(er.getFireRef());
        setFireRefFactor(er.getFireRefFactor());
        setEmissionCalcMethodCd(er.getEmissionCalcMethodCd());
        setFugitiveEmissions(er.getFugitiveEmissions());
        setStackEmissions(er.getStackEmissions());
        setEmissionsUnitNumerator(er.getEmissionsUnitNumerator());
        setFactorNumericValueOverride(er.getFactorNumericValueOverride());
        setFactorNumericValue(er.getFactorNumericValue());
        setTimeBasedFactorNumericValue(er.getTimeBasedFactorNumericValue());
        setFactorUnitNumerator(er.getFactorUnitNumerator());
        setFactorUnitDenominator(er.getFactorUnitDenominator());
        setAnnualAdjust(er.getAnnualAdjust());
        setExplanation(er.getExplanation());
        setTradeSecretE(er.isTradeSecretE());
        setTradeSecretEText(er.getTradeSecretEText());
        setTradeSecretF(er.isTradeSecretF());
        setTradeSecretFText(er.getTradeSecretFText());
    }

    public final String getEmissionCalcMethodCd() {
        return emissionCalcMethodCd;
    }

    public final void setEmissionCalcMethodCd(String emissionCalcMethodCd) {
        this.emissionCalcMethodCd = emissionCalcMethodCd;
    }

    public final Integer getEmissionPeriodId() {
        return emissionPeriodId;
    }

    public final void setEmissionPeriodId(Integer emissionPeriodId) {
        this.emissionPeriodId = emissionPeriodId;
    }

    public final String getEmissionsUnitNumerator() {
        return emissionsUnitNumerator;
    }

    public final void setEmissionsUnitNumerator(String emissionsUnitNumerator) {
        this.emissionsUnitNumerator = emissionsUnitNumerator;
    }

    public final String getFactorUnitDenominator() {
        return factorUnitDenominator;
    }

    public final void setFactorUnitDenominator(String factorUnitDenominator) {
        this.factorUnitDenominator = factorUnitDenominator;
    }

    public final String getFactorUnitNumerator() {
        return factorUnitNumerator;
    }

    public final void setFactorUnitNumerator(String factorUnitNumerator) {
        this.factorUnitNumerator = factorUnitNumerator;
    }

    public final String getPollutantCd() {
        return pollutantCd;
    }

    public final void setPollutantCd(String pollutantCd) {
        this.pollutantCd = pollutantCd;
    }

    public final String getFireRef() {
        return fireRef;
    }

    public final void setFireRef(String fireRef) {
        this.fireRef = fireRef;
    }

    public final String getFireRefFactor() {
        return fireRefFactor;
    }

    public final void setFireRefFactor(String fireRefFactor) {
        this.fireRefFactor = fireRefFactor;
    }

    public final boolean isVisited() {
        return visited;
    }

    public final void setVisited(boolean visited) {
        this.visited = visited;
    }

    public final int getFirstHalfPercent() {
		return firstHalfPercent;
	}

	public final void setFirstHalfPercent(int firstHalfPercent) {
		this.firstHalfPercent = firstHalfPercent;
	}

	public final int getSecondHalfPercent() {
		return secondHalfPercent;
	}

	public final void setSecondHalfPercent(int secondHalfPercent) {
		this.secondHalfPercent = secondHalfPercent;
	}

	public Integer getEmuID() {
		return emuID;
	}

	public void setEmuID(Integer emuID) {
		this.emuID = emuID;
	}

	public String getEpaEmuID() {
		return epaEmuID;
	}

	public void setEpaEmuID(String epaEmuID) {
		this.epaEmuID = epaEmuID;
	}

	public String getProcessID() {
		return processID;
	}

	public void setProcessID(String processID) {
		this.processID = processID;
	}

	public final String getSccId() {
		return SccId;
	}

	public final void setSccId(String sccId) {
		SccId = sccId;
	}

	/*
	public final Integer getFirstHalfPercentForAllowable() {
		return firstHalfPercentForAllowable;
	}

	public final void setFirstHalfPercentForAllowable(Integer firstHalfPercentForAllowable) {
		this.firstHalfPercentForAllowable = firstHalfPercentForAllowable;
	}

	public final Integer getSecondHalfPercentForAllowable() {
		return secondHalfPercentForAllowable;
	}

	public final void setSecondHalfPercentForAllowable(Integer secondHalfPercentForAllowable) {
		this.secondHalfPercentForAllowable = secondHalfPercentForAllowable;
	}
	*/
	
	public final BigDecimal getTotalAllowable() {
		return totalAllowable;
	}

	public final void setTotalAllowable(BigDecimal totalAllowable) {
		this.totalAllowable = totalAllowable;
	}

	public final void populate(ResultSet rs) {
    	
        try {
            setEmissionPeriodId(AbstractDAO
                    .getInteger(rs, "emission_period_id")); // emission_period_id renamed
            setPollutantCd(rs.getString("pollutant_cd"));
            setEmissionCalcMethodCd(rs.getString("emission_calc_method_cd"));
            setFugitiveEmissions(rs.getString("fugitive_emissions"));
            setStackEmissions(rs.getString("stack_emissions"));
            setEmissionsUnitNumerator(rs.getString("emission_unit_numerator"));
            setFactorNumericValueOverride(AbstractDAO.translateIndicatorToBoolean(rs.getString("factor_numeric_value_override")));
            setFactorNumericValue(rs.getString("factor_numeric_value"));
            setTimeBasedFactorNumericValue(rs.getString("time_based_factor_numeric_value"));
            setFactorUnitNumerator(rs.getString("factor_unit_numerator"));
            setFactorUnitDenominator(rs.getString("factor_unit_denominator"));
//            setTradeSecretF(AbstractDAO.translateIndicatorToBoolean(rs
//                    .getString("factor_ts")));
//            setTradeSecretFText(rs.getString("factor_ts_just"));
            setAnnualAdjust(rs.getString("annual_adjustment"));
            setFireRef(rs.getString("fire_id"));
            setFireRefFactor(rs.getString("factor"));
            setExplanation(rs.getString("explanation"));
//            setTradeSecretE(AbstractDAO.translateIndicatorToBoolean(rs
//                    .getString("explain_ts")));
//            setTradeSecretEText(rs.getString("explain_ts_just"));
            setLastModified(AbstractDAO.getInteger(rs, "emissions_lm"));
            // null is used by the .jsp to know there is no value
            if(tradeSecretEText != null && tradeSecretEText.length() == 0) tradeSecretEText = null;
            if(tradeSecretFText != null && tradeSecretFText.length() == 0) tradeSecretFText = null;
            if(explanation != null && explanation.length() == 0) explanation = null;
            if(emissionsUnitNumerator == null) {
                logger.error("EmissionsUnitNumerator is null for pollutant "
                        + pollutantCd + " in period " + emissionPeriodId
                        + ". This message is to help determine why it is null:  the only effect the user notices is that they may have to reset units even though it had earlier been set.");
            }
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
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
        final Emissions other = (Emissions) obj;
        if (pollutantCd == null) {
            if (other.pollutantCd != null)
                return false;
        } else if (!pollutantCd.equals(other.pollutantCd))
            return false;
        return true;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public boolean isTradeSecretE() {
        return tradeSecretE;
    }

    public void setTradeSecretE(boolean tradeSecretE) {
        this.tradeSecretE = tradeSecretE;
    }

    public String getTradeSecretEText() {
        return tradeSecretEText;
    }

    public void setTradeSecretEText(String tradeSecretEText) {
        this.tradeSecretEText = tradeSecretEText;
    }

    public boolean isTradeSecretF() {
        return tradeSecretF;
    }

    public void setTradeSecretF(boolean tradeSecretF) {
        this.tradeSecretF = tradeSecretF;
    }

    public String getTradeSecretFText() {
        return tradeSecretFText;
    }

    public void setTradeSecretFText(String tradeSecretFText) {
        this.tradeSecretFText = tradeSecretFText;
    }

    public String getAnnualAdjust() {
        return annualAdjust;
    }

    public void setAnnualAdjust(String annualAdjust) {
        this.annualAdjust = annualAdjust;
    }

    public boolean getFactorNumericValueOverride() {
		return factorNumericValueOverride;
	}

	public void setFactorNumericValueOverride(boolean factorNumericValueOverride) {
		this.factorNumericValueOverride = factorNumericValueOverride;
	}

	public String getFactorNumericValue() {
        return factorNumericValue;
    }

    public void setFactorNumericValue(String factorNumericValue) {
        this.factorNumericValue = factorNumericValue;
    }

    public String getTimeBasedFactorNumericValue() {
        return timeBasedFactorNumericValue;
    }

    public void setTimeBasedFactorNumericValue(String timeBasedFactorNumericValue) {
        this.timeBasedFactorNumericValue = timeBasedFactorNumericValue;
    }

    public String getStackEmissions() {
        return stackEmissions;
    }

    public String getFugitiveEmissions() {
        return fugitiveEmissions;
    }

    public void setFugitiveEmissions(String fugitiveEmissions) {
        this.fugitiveEmissions = fugitiveEmissions;
    }

    public void setStackEmissions(String stackEmissions) {
        this.stackEmissions = stackEmissions;
    }

	public boolean isRemoveRow() {
		return removeRow;
	}

	public void setRemoveRow(boolean removeRow) {
		this.removeRow = removeRow;
	}
	
}
