package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import oracle.adf.view.faces.component.core.input.CoreInputText;
import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCPollutant;
import us.oh.state.epa.stars2.def.EmissionCalcMethodDef;
import us.oh.state.epa.stars2.def.EmissionUnitReportingDef;
import us.oh.state.epa.stars2.def.FireVariableNamesDef;
import us.oh.state.epa.stars2.def.MaterialDef;
import us.oh.state.epa.stars2.def.NonToxicPollutantDef;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.reports.ControlInfoRow;
import us.oh.state.epa.stars2.webcommon.reports.EmissionRow;
import us.oh.state.epa.stars2.webcommon.reports.ExpressionEval;
import us.oh.state.epa.stars2.webcommon.reports.PollutantPartOf;
import us.oh.state.epa.stars2.webcommon.reports.ReportProfileBase;

public class EmissionsReportPeriod extends BaseDB {
	
    public static final Double MIN_OPERATING_HOURS_VAL = 0d;
    public static final Double MAX_OPERATING_HOURS_VAL = 8760d;
    
	private static Integer zeroInteger = new Integer(0);

    private static Integer oneHundredInteger = new Integer(100);

    private Integer emissionsRptId;
    private Integer emissionPeriodId;
    private SccCode sccCode;
    private Integer winterThroughputPct;
    private Integer springThroughputPct;
    private Integer fallThroughputPct;
    private Integer summerThroughputPct;
    private Integer daysPerWeek;
    private Integer weeksPerYear;
    private Integer hoursPerDay;
    private Double hoursPerYear;
    private Integer firstHalfHrsOfOperationPct;
    private Integer secondHalfHrsOfOperationPct;
    private boolean hpyDiff = false;
    private boolean tradeSecretS;
    private String tradeSecretSText;
    private transient int tsRows = 13;

    private List<EmissionsMaterialActionUnits> maus = new ArrayList<EmissionsMaterialActionUnits>(0);
    private List<EmissionsVariable> vars = new ArrayList<EmissionsVariable>(0);
    private String reqVars = null;

    // private List<Emissions> emissionsSerializeable = null; // here so
    // getEmissionsSerializable & setEmissionsSerializable will be called.
    private HashMap<String, Emissions> emissions = new HashMap<String, Emissions>(0);

    // FOLLOWING VARIABLES NOT IN DATABASE
    private String treeLabel; // label used in report tree. Process name where

    // possible.
    private String treeLabelDesc; // description for process

    // Used to indicate if the process/period is not in Facility.
    private boolean notInFacility;

    // Used to tie together a reportPeriod (orig) and the reportPeriod (comp) it
    // is being compared to.
    // These are for temporary in-memory use and not on disk.
    private EmissionsReportPeriod orig; // points to the original reportPeriod

    // object or null
    private EmissionsReportPeriod comp; // points to the comparison reportPeriod

    // object or null.
    private boolean caution; // indicate if caution should be displayed on

    // tree.
    private boolean autoPopulated;

    private List<String> errors;

    private String processId; // Migration only: Hold process id

    // used only in EmissionsRow
    private transient ArrayList<EmissionRow> capHapList;
    private transient FireRow[] fireRows;
    private transient String staticMaterial;
    private transient Integer staticYear;
    private transient String adjS = null;
    private transient double adjV = 0;
    private  transient int cntAnnualAdjSame = 0;

    public static String convertSCC(EmissionsReportPeriod p) {
        String sccCodeStr = null;
        if (p != null) {
            sccCodeStr = p.convertSCC();
        }
        if (null == sccCodeStr) {
            return "---none---";
        }
        return sccCodeStr;
    }

    public EmissionsReportPeriod() {
        super();
        orig = null;
        comp = null;
        caution = false;
        sccCode = new SccCode();
        notInFacility = false; // default for normal display
        // default assign 25% throughput for each season
        winterThroughputPct = 25;
        springThroughputPct = 25;
        fallThroughputPct = 25;
        summerThroughputPct = 25;
        // default split hours of operation 50-50 between first and second half
        firstHalfHrsOfOperationPct = 50;
        secondHalfHrsOfOperationPct = 50;
        // default hours/day, days/week and weeks/year
        hoursPerDay = 24;
        daysPerWeek = 7;
        weeksPerYear = 52;
    }

    public EmissionsReportPeriod(EmissionsReportPeriod old) {
        super(old);

        if (old != null) {
            tsRows = old.tsRows;
            // Bypass all validity checking.
            emissionsRptId = old.emissionsRptId;
            // value of null indicates this is a new object
            emissionPeriodId = null;
            sccCode = old.sccCode;
            updateUserFields(old);
            notInFacility = old.isNotInFacility();
            if (old.emissions != null) {
                emissions = new HashMap<String, Emissions>(old.emissions);
            } else {
                emissions = new HashMap<String, Emissions>(0);
            }
            if (old.maus != null) {
                maus = new ArrayList<EmissionsMaterialActionUnits>(old.maus);
            } else {
                maus = new ArrayList<EmissionsMaterialActionUnits>(0);
            }
            if (old.vars != null) {
                vars = new ArrayList<EmissionsVariable>(old.vars.size());
                for (EmissionsVariable ev : old.vars) {
                    // Make deep copy of variables
                    EmissionsVariable newEv = new EmissionsVariable(ev);
                    vars.add(newEv);
                }
            } else {
                vars = new ArrayList<EmissionsVariable>(0);
            }
            orig = old.orig;
            comp = old.comp;
        }
    }

    public EmissionsReportPeriod(EmissionsReportPeriod old, boolean newPeriod) {
        super(old);

        if (old != null) {
            // Bypass all validity checking.
            emissionsRptId = old.emissionsRptId;
            // value of null indicates this is a new object
            emissionPeriodId = null;
            sccCode = old.sccCode;
            updateUserFields(old);
            setTradeSecretS(false);
            setTradeSecretSText(null);
            notInFacility = old.isNotInFacility();
            emissions = new HashMap<String, Emissions>(0);
            maus = new ArrayList<EmissionsMaterialActionUnits>(0);
            vars = new ArrayList<EmissionsVariable>(0);
            orig = old.orig;
            comp = old.comp;
        }
    }

    public static EmissionsReportPeriod shallowCopy(EmissionsReportPeriod old) {
        EmissionsReportPeriod newP = new EmissionsReportPeriod();
        newP.sccCode = old.sccCode;
        newP.updateUserFields(old);
        newP.emissions = old.emissions;
        newP.maus = old.maus;
        return newP;
    }

    public void updateUserFields(EmissionsReportPeriod p) {
        winterThroughputPct = p.winterThroughputPct;
        springThroughputPct = p.springThroughputPct;
        fallThroughputPct = p.fallThroughputPct;
        summerThroughputPct = p.summerThroughputPct;
        daysPerWeek = p.daysPerWeek;
        weeksPerYear = p.weeksPerYear;
        hoursPerDay = p.hoursPerDay;
        hoursPerYear = p.hoursPerYear;
        tradeSecretS = p.tradeSecretS;
        setTradeSecretSText(p.tradeSecretSText); // use set to control
                                                    // tradeSecretS.
        hpyDiff = p.hpyDiff;
        vars = p.vars;
        firstHalfHrsOfOperationPct = p.firstHalfHrsOfOperationPct;
        secondHalfHrsOfOperationPct = p.secondHalfHrsOfOperationPct;
    }

    public void resetSchedSeason() {
        winterThroughputPct = 25;
        springThroughputPct = 25;
        fallThroughputPct = 25;
        summerThroughputPct = 25;
        daysPerWeek = 7;
        weeksPerYear = 52;
        hoursPerDay = 24;
        hoursPerYear = null;
        firstHalfHrsOfOperationPct = 50;
        secondHalfHrsOfOperationPct = 50;
    }

    public void updateEmissions(Facility facility, EmissionsReport report,
            List<EmissionRow> periodEmissions, FireRow[] periodFireRows)
            throws ApplicationException {
        // Check for migration not setting this attribute
        if (periodEmissions == null) {
            logger
                    .error("updateEmissions function with periodEmissions null for emissionPeriodId = "
                            + emissionPeriodId
                            + " in report "
                            + report.getEmissionsRptId());
        }

        if (zeroHours()) {
            // Get rid of emissions.
            periodEmissions = new ArrayList<EmissionRow>(0);
        }
        // Recompute emissions
        // First evaluate any Fire formulas
        determineVars(report, periodEmissions, periodFireRows);
        // Locate a representative EU and the facility process
        Integer euId = null;
        EmissionsReportEU repEu = report.findEU(this);
        if (null == repEu) {
            EmissionsReportEUGroup euG = report.findEuG(this);
            if (euG != null && !euG.getEus().isEmpty()) {
                euId = euG.getEus().get(0);
            }
        } else {
            euId = repEu.getCorrEpaEmuId();
        }
        EmissionProcess p = null;
        EmissionUnit eu = null;
        if (null != euId) {
            eu = facility.getMatchingEmissionUnit(euId);
            if (null != eu) {
                p = eu.findProcess(this.getSccId());
            }
        }
        // Go through all the pollutants to compute emissions
        Double t = EmissionsMaterialActionUnits.getThroughputV(maus);
        for (EmissionRow er : periodEmissions) {
            // New rows need pollutand description set
            if (null == er.getPollutant()) {
                er.setPollutant(NonToxicPollutantDef.getData().getItems()
                        .getItemDesc(er.getPollutantCd()));
            }
            // do emissions calculations
            	
	            if (null != er.getFactorNumericValue()) {
					if (er.isResetMethodType()) {
						// need to reset method type because factor reset
						er.setEmissionCalcMethodCd(null);
						er.setAnnualAdjust(null);
						er.setTimeBasedFactorNumericValue(null);
                		er.setFugitiveEmissions(null);
                		er.setStackEmissions(null);
                		er.setTotalEmissions(null);
                		er.setResetMethodType(false);
					} else {
		                if (null != er.getAnnualAdjust() && hoursPerYear != null
		                        && p != null && null != t) {
						if (EmissionCalcMethodDef.isTimeBasedFactorMethod(er
								.getEmissionCalcMethodCd())
								&& er.getTimeBasedFactorNumericValue() == null) {
	                		er.setFugitiveEmissions(null);
	                		er.setStackEmissions(null);
	                		er.setTotalEmissions(null);         
						} else {
		                    Double d = new Double(t.doubleValue());
		                    List<ControlInfoRow> calc = ControlInfoRow
		                            .generateControlMatrix(facility, p, this, er, false, false);
		                    if(ControlInfoRow.isProblems()) {
		                        String s2 = ControlInfoRow.getProblems().toString() + " for report " +
		                        report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc "
		                        + this.getSccId() + ", pollutantCd " + er.getPollutantCd();
		                        DisplayUtil.displayError(s2);
		                    }

		                    double fugitiveTotalPercentage = ControlInfoRow.fugitiveTotal(calc);
		                    double fug = 0d;
		                    // TFS task - 4160: 
		                    // when calculating fugitive emissions, if uncontrolled emisisons factor is 0, then
		                    // use the time-based factor value instead for computing the fugitive emissions
		                    if((EmissionCalcMethodDef.isTimeBasedFactorMethod(er.getEmissionCalcMethodCd())
		                    		&& er.getFactorNumericValueV() != null && er.getFactorNumericValueV() == 0)) {
		                    	fug = hoursPerYear * er.getTimeBasedFactorNumericValueV() * fugitiveTotalPercentage;
		                    } else {
		                    	fug = d.doubleValue() * er.getFactorNumericValueV() * fugitiveTotalPercentage;
		                    }
		                    
		                    logger.debug("---> fugitiveTotalPercentage = " + fugitiveTotalPercentage);
		                    double stk = 0d;
		                    if (EmissionCalcMethodDef.isTimeBasedFactorMethod(er.getEmissionCalcMethodCd())) {
		                    		double stackTotalPercentage = 1.d - fugitiveTotalPercentage;

			                    	double controlledHours = hoursPerYear - er.getAnnualAdjustV();
			                    	double controlledStackOutput = 
			                    			controlledHours * er.getTimeBasedFactorNumericValueV() * stackTotalPercentage;
			                    	double uncontrolledHourPercentage = er.getAnnualAdjustV() / hoursPerYear;
			                    	double uncontrolledStackOutput = 
			    		                	d.doubleValue() * er.getFactorNumericValueV() * uncontrolledHourPercentage * stackTotalPercentage;
			                    	stk = controlledStackOutput + uncontrolledStackOutput;

			                } else {
			                	double stackTotalPercentage = ControlInfoRow.stackTotal(calc);
			                	stk = d.doubleValue() * er.getFactorNumericValueV()
			                			* stackTotalPercentage;
			                    logger.debug("---> stackTotalPercentage = " + stackTotalPercentage);
			                }
		                    if (er.getEmissionsUnitNumerator().equals(
		                            EmissionUnitReportingDef.TONS)) {
		                        stk /= 2000d;
		                        fug /= 2000d;
		                    }
		                    er.setFugitiveEmissionsV(fug);
		                    er.setStackEmissionsV(stk);
		                    er.setTotalEmissionsV(fug + stk);
		                    er.setFugitiveEmissions(EmissionsReport.numberToString(fug));
		                    er.setStackEmissions(EmissionsReport.numberToString(stk));
		                    er.setTotalEmissions(EmissionsReport.numberToString(er.getTotalEmissionsV()));
		                } }else {
		                	if (er.getEmissionCalcMethodCd() != null &&
		                			EmissionsReport.convertStringToNum(
		                					er.getEmissionCalcMethodCd()) < 200) {
		                		er.setFugitiveEmissions(null);
		                		er.setStackEmissions(null);
		                		er.setTotalEmissions(null);
		                	}
		                }
					}
                } else {
                	if ((null == er.getAnnualAdjust() || null == hoursPerYear
	                        || null == t || (er.getEmissionCalcMethodCd() != null 
	    							&& EmissionCalcMethodDef.isFactorMethod(er.getEmissionCalcMethodCd()))) 
	    							&& (er.getEmissionCalcMethodCd() != null &&
                			EmissionsReport.convertStringToNum(
                					er.getEmissionCalcMethodCd()) < 200)) {
                		er.setFugitiveEmissions(null);
                		er.setStackEmissions(null);
						er.setTotalEmissions(null);
                	}
                }

            // Handle dependent changes
//            if (null != er.getFireRef()
//                    && (er.getEmissionCalcMethodCd() != null && !er
//                            .getEmissionCalcMethodCd().equals(
//                                    EmissionCalcMethodDef.SCCEmissionsFactor))) {
//                er.setFireRef(null);
//                er.setFactorNumericValue(null);
//                er.setStackEmissions(null);
//                er.setFugitiveEmissions(null);
//                er.setTotalEmissions(null);
//                er.setFormula(null);
//            }
            // IMPACT - If the pollutant is one of the four PMs that are
            // excluded from being automatically added if a row exists in
            // the fire table AND it WAS manually added to the HAP table,
            // set the fire_id to null so that we treat this row just
            // like any other manually added row.
            if (er.isDeletable() && ("PM-FIL".equals(er.getPollutantCd()) ||
            		"PM10-FIL".equals(er.getPollutantCd()) ||
            		"PM25-FIL".equals(er.getPollutantCd()) ||
            		"PM-CON".equals(er.getPollutantCd()))) {
            	er.setFireRef(null);
            }
            Emissions e = emissions.get(er.getPollutantCd());
            if (null == e) {
          
            	
            	// IMPACT - If the pollutant is one of the four PMs that are
            	// excluded from being automatically added if a row exists in
            	// the fire table AND it wasn't manually added to the HAP table, skip it.
            	
            	// exclude FIL and CON PMs
				// First clause covers PMs with one active fire row.
				// Second clause covers PMs with more than one active fire rows.
				if ((er.getFireRef() != null || (!er.isDeletable() && er.getNumFireRows() > 1))
						&& ("PM-FIL".equals(er.getPollutantCd()) || "PM10-FIL".equals(er.getPollutantCd())
								|| "PM25-FIL".equals(er.getPollutantCd()) || "PM-CON".equals(er.getPollutantCd()))) {
					continue;
				}
            	         	
                // New row. Add it to period emissions
                Emissions newE = new Emissions();
                copy(newE, er);
                addEmission(newE);
            } else {
                copy(e, er);
            }
        }

        // Delete rows not visited
        Iterator<Emissions> i = emissions.values().iterator();
        while (i.hasNext()) {
            if (!i.next().isVisited()) {
                i.remove();
            }
        }
    }

    public void determineVars(EmissionsReport report,
            List<EmissionRow> periodEmissions, FireRow[] fireRows)
            throws ApplicationException {
        HashMap<String, EmissionsVariable> varMap = new HashMap<String, EmissionsVariable>();
        // Preload map
        for (EmissionsVariable v : vars) {
            v.setBelongs(false);
            varMap.put(ExpressionEval.lowerToUpper(v.getVariable()), v);
        }

        // Locate all variables and mark those still used.
        // Evaluate expression if all variables are set.
        for (EmissionRow er : periodEmissions) {
            if (
//            		er.getEmissionCalcMethodCd() == null
//                    || !er.getEmissionCalcMethodCd().equals(
//                            EmissionCalcMethodDef.SCCEmissionsFactor)
//                    || 
                    er.getFormula() == null)
                continue;
            ExpressionEval eE = ExpressionEval.createFromFactory();
            eE.setExpression(er.getFormula());
            String[] lVars = eE.getVariables();
            if (eE.getErr() != null) {
                String s = "Emission Calculator problem on pollutant "
                        + er.getPollutant() + " using Fire Factor "
                        + er.getFireRef() + ": " + eE.getErr();
                addError(s);
                logger.error(s + "; in EmissionsReportPeriod "
                        + emissionPeriodId + " in report "
                        + report.getEmissionsRptId());
            } else {
                EmissionsVariable v;
                boolean allSet = true;
                for (String s : lVars) {
                    if (varMap.containsKey(s)) {
                        v = varMap.get(s);
                        v.setBelongs(true);
                        if (v.getValue() == null)
                            allSet = false;
                        else {
                            eE.setVariable(s, v.getValueV());
                            if (eE.getErr() != null) {
                                allSet = false;
                                s = "Emission Calculator problem on pollutant "
                                        + er.getPollutant() + ": "
                                        + eE.getErr();
                                addError(s);
                                logger.error(s + "; in EmissionsReportPeriod "
                                        + emissionPeriodId + " in report "
                                        + report.getEmissionsRptId());
                            }
                        }
                    } else {
                        v = new EmissionsVariable(ExpressionEval
                                .upperToLower(s));
                        varMap.put(s, v);
                        allSet = false;
                    }
                }
                if (allSet) {
                    double dv = eE.getValue();
                    if (eE.getErr() != null) {
                        String s = "Emission Calculator problem on pollutant "
                                + er.getPollutant() + ": " + eE.getErr();
                        addError(s);
                        logger.error(s + "; in EmissionsReportPeriod "
                                + emissionPeriodId + " in report "
                                + report.getEmissionsRptId());
                        if(!er.getFactorNumericValueOverride()) {
	                        er.setFactorNumericValue(null);
	//                        er.setStackEmissions(null);
	//                        er.setFugitiveEmissions(null);
	                        er.setTotalEmissions(null);
                        }
                    } else {
                    	if (!er.getFactorNumericValueOverride()) {
	                        er.setFactorNumericValue(EmissionsReport
	                                .numberToString(dv));
                    	}
                        er.setFireRefFactor(EmissionsReport
                        		.numberToString(dv));
                    }
                } else {
                    if(!er.getFactorNumericValueOverride()) {
	                	er.setFactorNumericValue(null);
	//                    er.setStackEmissions(null);
	//                    er.setFugitiveEmissions(null);
	                    er.setTotalEmissions(null);
                    }
                }
            }
        }

        // Locate variables that must be included simply because there is a
        // formula that could be used
        String materialCd = getCurrentMaterial();
        TreeSet<String> reqiredVars = new TreeSet<String>();
        for (FireRow r : fireRows) {
            if (r.getFormula() != null
                    && report.getReportYear().intValue() > 2008
                    && null != r.getPollutantCd() && r.getMaterial() != null
                    && materialCd != null && materialCd.equals(r.getMaterial())
                    && r.isActive(report.getReportYear())) {
                // These are all the active fire rows with formulas
                ExpressionEval eE = ExpressionEval.createFromFactory();
                eE.setExpression(r.getFormula());
                String[] lVars = eE.getVariables();
                if (eE.getErr() != null) {
                    String s = "Emission Calculator problem on pollutant "
                            + r.getPollutant() + " using Fire Factor "
                            + r.getFactor() + ": " + eE.getErr();
                    logger
                            .error(s
                                    + "; determining required variables in EmissionsReportPeriod "
                                    + emissionPeriodId + " in report "
                                    + report.getEmissionsRptId());
                } else {
                    EmissionsVariable v;
                    for (String s : lVars) {
                        String lS = ExpressionEval.upperToLower(s);
                        FireVariableNamesDef fvnd = (FireVariableNamesDef) FireVariableNamesDef
                                .getData().getItem(lS);
                        if (fvnd != null && fvnd.isRequired()) {
                            reqiredVars.add(lS);
                            if (varMap.containsKey(s)) {
                                v = varMap.get(s);
                                v.setBelongs(true);
                            } else {
                                v = new EmissionsVariable(lS);
                                varMap.put(s, v);
                            }
                        }
                    }
                }
            }
        }
        reqVars = null;
        if (reqiredVars.size() != 0) {
            String next = null;
            String prevNext = null;
            for (String s : reqiredVars) {
                prevNext = next;
                next = s;
                if (prevNext == null) {
                    continue;
                } else {
                    if (reqVars == null) {
                        reqVars = prevNext;
                    } else {
                        reqVars = reqVars + ", " + prevNext;
                    }
                }
            }
            if (reqVars == null) {
                reqVars = "the variable " + next;
            } else {
                reqVars = "the variables " + reqVars + " and " + next;
            }
        }

        // Replace Variables in Period with updated ones.
        vars = new ArrayList<EmissionsVariable>();
        for (EmissionsVariable v : varMap.values()) {
            if (v.isBelongs()) {
                vars.add(v);
            }
        }
    }

    private void copy(Emissions e, EmissionRow er) {
        e.setVisited(true);
        e.setEmissionCalcMethodCd(er.getEmissionCalcMethodCd());
        e.setEmissionsUnitNumerator(er.getEmissionsUnitNumerator());
        e.setFactorNumericValueOverride(er.getFactorNumericValueOverride());
        e.setFactorNumericValue(er.getFactorNumericValue());
        e.setTimeBasedFactorNumericValue(er.getTimeBasedFactorNumericValue());
        e.setFactorUnitDenominator(er.getFactorUnitDenominator());
        e.setFactorUnitNumerator(er.getFactorUnitNumerator());
        e.setAnnualAdjust(er.getAnnualAdjust());
        e.setFireRef(er.getFireRef());
        e.setFugitiveEmissions(er.getFugitiveEmissions());
        e.setStackEmissions(er.getStackEmissions());
        e.setPollutantCd(er.getPollutantCd());
        e.setDeletable(er.isDeletable());
        e.setExplanation(er.getExplanation());
        e.setTradeSecretE(er.isTradeSecretE());
        e.setTradeSecretEText(er.getTradeSecretEText());
        e.setTradeSecretF(er.isTradeSecretF());
        e.setTradeSecretFText(er.getTradeSecretFText());
    }

    public boolean anyValuesSet() {
        boolean rtn = false;
        if (winterThroughputPct != null || springThroughputPct != null
                || fallThroughputPct != null || summerThroughputPct != null
                || daysPerWeek != null || weeksPerYear != null
                || hoursPerDay != null || hoursPerYear != null && hoursPerYear != 0) {
            rtn = true;
        } else {
            EmissionsMaterialActionUnits emau = getCurrentMaus();
            if (emau != null && emau.getThroughput() != null) {
                Double d = EmissionsReport.convertStringToNum(emau
                        .getThroughput(), logger);
                if (!d.equals(0d)) {
                    rtn = true;
                }
            }
        }
        if(!rtn) {
            if(vars != null) {
                for(EmissionsVariable v : vars) {
                    if(v.getValueV() != null) {
                        rtn = true;
                        break;
                    }
                }
            }
        }
        return rtn;
    }

    public boolean allValuesSet() {
        boolean rtn = isAllNonVariablesSet();
        if (rtn) {
            rtn = isAllVariablesSet();
        }
        return rtn;
    }

    public void autoPopulate(EmissionsReportPeriod savedP) {
        winterThroughputPct = savedP.getWinterThroughputPct();
        springThroughputPct = savedP.getSpringThroughputPct();
        fallThroughputPct = savedP.getFallThroughputPct();
        summerThroughputPct = savedP.getSummerThroughputPct();
        daysPerWeek = savedP.getDaysPerWeek();
        weeksPerYear = savedP.getWeeksPerYear();
        hoursPerDay = savedP.getHoursPerDay();
        hoursPerYear = savedP.getHoursPerYear();
        firstHalfHrsOfOperationPct = savedP.firstHalfHrsOfOperationPct;
        secondHalfHrsOfOperationPct = savedP.secondHalfHrsOfOperationPct;
        autoPopulated = true;
    }

    public boolean isAllSchedSeasonNull() {
        boolean rtn = false;
        if (winterThroughputPct == null && springThroughputPct == null
                && fallThroughputPct == null && summerThroughputPct == null
                && daysPerWeek == null && weeksPerYear == null
                && hoursPerDay == null && hoursPerYear == null) {
            rtn = true;
        }
        return rtn;
    }

    public boolean isAllNonVariablesSet() {
        boolean rtn = true;
        if (winterThroughputPct == null || springThroughputPct == null
                || fallThroughputPct == null || summerThroughputPct == null
                || daysPerWeek == null || weeksPerYear == null
                || hoursPerDay == null || hoursPerYear == null) {
            rtn = false;
        } else {
            EmissionsMaterialActionUnits emau = getCurrentMaus();
            if (emau == null || emau.getThroughput() == null) {
                rtn = false;
            }
        }
        return rtn;
    }

    public boolean isAllVariablesSet() {
        boolean rtn = true;
        for (EmissionsVariable v : vars) {
            if (v.isBelongs() && v.getValue() == null) {
                rtn = false;
                break;
            }
        }
        return rtn;
    }

    // Do the non-Set function validation tests. The Set function validations
    // are already done and will be returned with these.
    public final ValidationMessage[] checkPageValues(
            List<EmissionsMaterialActionUnits> periodMaterialList, EmissionsReport emissionsReport) {
    	if(null == hoursPerYear) {
            validationMessages
                    .put(   "hoursPerYear",
                            new ValidationMessage(
                                    "hoursPerYear",
                                    "Required attribute Actual Hours is not set",
                                    ValidationMessage.Severity.ERROR, null));
    	}
    	if(null == winterThroughputPct) {
            validationMessages
                    .put(   "winterThroughputPct",
                            new ValidationMessage(
                                    "winterThroughputPct",
                                    "Required attribute Winter (Jan-Feb, Dec)% is not set",
                                    ValidationMessage.Severity.ERROR, null));
    	}
    	if(null == springThroughputPct) {
            validationMessages
                    .put(   "springThroughputPct",
                            new ValidationMessage(
                                    "springThroughputPct",
                                    "Required attribute Spring (Mar-May)% is not set",
                                    ValidationMessage.Severity.ERROR, null));
    	}
    	if(null == summerThroughputPct) {
            validationMessages
                    .put(   "summerThroughputPct",
                            new ValidationMessage(
                                    "summerThroughputPct",
                                    "Required attribute Summer (Jun-Aug)% is not set",
                                    ValidationMessage.Severity.ERROR, null));
    	}
    	if(null == fallThroughputPct) {
            validationMessages
                    .put(   "fallThroughputPct",
                            new ValidationMessage(
                                    "fallThroughputPct",
                                    "Required attribute Fall (Sep-Nov)% is not set",
                                    ValidationMessage.Severity.ERROR, null));
    	}
    	if(null == firstHalfHrsOfOperationPct) {
            validationMessages
                    .put(   "firstHalfHrsOfOperationPct",
                            new ValidationMessage(
                                    "firstHalfHrsOfOperationPct",
                                    "Required attribute Hours of Operation (First Half Year)% is not set",
                                    ValidationMessage.Severity.ERROR, null));
    	}
    	if(null == secondHalfHrsOfOperationPct) {
            validationMessages
                    .put(   "secondHalfHrsOfOperationPct",
                            new ValidationMessage(
                                    "secondHalfHrsOfOperationPct",
                                    "Required attribute Hours of Operation (Second Half Year)% is not set",
                                    ValidationMessage.Severity.ERROR, null));
    	}
    	
        Boolean isZeroHours = null;
        if (hoursPerYear != null) {
            if (hoursPerYear.equals(0d)) {
                isZeroHours = true;
            } else {
                isZeroHours = false;
            }

        }
        Boolean isZeroThroughput = null;
        EmissionsMaterialActionUnits e = null;
        // Get throughput from tempory list.
        for (EmissionsMaterialActionUnits em : periodMaterialList)
            if (em.isBelongs()) {
                e = em;
                break;
            }
        if (e != null && e.getThroughput() != null
                && e.getThroughput().length() != 0) {
            Double d = EmissionsReport.convertStringToNum(e.getThroughput(),
                    logger);
            if (d.equals(0d)) {
                isZeroThroughput = true;
            } else {
                isZeroThroughput = false;
            }
        }

        if (this.tradeSecretS) {
            if (tradeSecretSText == null
                    || tradeSecretSText.trim().length() == 0) {
                validationMessages.put("noTS", new ValidationMessage("tsRadio",
                        "No Trade Secret justification given",
                        ValidationMessage.Severity.ERROR, null));
            }
        }

        if (isZeroHours != null
                && isZeroThroughput != null
                && (isZeroHours.booleanValue() ^ isZeroThroughput
                        .booleanValue())) {
            if (isZeroHours) {
                validationMessages
                        .put(
                                "zeroConflict",
                                new ValidationMessage(
                                        "hoursPerYear2",
                                        "Conflict with Actual Hours zero but Throughput not zero",
                                        ValidationMessage.Severity.ERROR, null));
            } else {
                validationMessages
                        .put(
                                "zeroConflict",
                                new ValidationMessage(
                                        "hoursPerYear2",
                                        "Conflict with Throughput zero but Actual Hours not zero",
                                        ValidationMessage.Severity.ERROR, null));
            }
        }

        if (hoursPerYear != null) {
            String errMsg = null;
            double largestAdjust = 0.0d;
            for (Emissions em : emissions.values()) {
                if (em.getAnnualAdjust() != null
                        && em.getAnnualAdjust().length() > 0
                        && EmissionsReport.convertStringToNum(em
                                .getAnnualAdjust(), logger) > hoursPerYear) {
                    NonToxicPollutantDef baseDef;
                    String pollName;
                    try {
                        baseDef = NonToxicPollutantDef.getPollutantBaseDef(em
                                .getPollutantCd());
                        pollName = "the pollutant " + baseDef.getDescription()
                                + ".";
                    } catch (ApplicationException ae) {
                        pollName = "any of the pollutants.";
                    }
                    if (EmissionsReport.convertStringToNum(
                            em.getAnnualAdjust(), logger) > largestAdjust) {
                        largestAdjust = EmissionsReport.convertStringToNum(em
                                .getAnnualAdjust(), logger);
                        errMsg = "Actual Hours cannot be less than the hours of uncontrolled operation for "
                                + pollName;
                    }
                }
            }
            if (errMsg != null) {
                validationMessages.put("hoursPerYearToo",
                        new ValidationMessage("hoursPerYear2", errMsg,
                                ValidationMessage.Severity.ERROR, null));
            }
        }

        checkRangeValues(winterThroughputPct, new Integer(0), new Integer(100),
                "winter", "Winter Throughput Percent");
        checkRangeValues(springThroughputPct, new Integer(0), new Integer(100),
                "spring", "Spring Throughput Percent");
        checkRangeValues(summerThroughputPct, new Integer(0), new Integer(100),
                "summer", "Summer Throughput Percent");
        checkRangeValues(fallThroughputPct, new Integer(0), new Integer(100),
                "fall", "Fall Throughput Percent");
        int total = 0;
        int cnt = 0;
        if (null != fallThroughputPct) {
            cnt++;
            total = fallThroughputPct.intValue();
        }
        if (null != winterThroughputPct) {
            cnt++;
            total += winterThroughputPct.intValue();
        }
        if (null != springThroughputPct) {
            cnt++;
            total += springThroughputPct.intValue();
        }
        if (null != summerThroughputPct) {
            cnt++;
            total += summerThroughputPct.intValue();
        }
        if (total != 100) {
            validationMessages
                    .put(
                            "seasonTotal",
                            new ValidationMessage(
                                    "fall",
                                    "Winter, Spring, Summer & Fall percent total must equal and not exceed 100",
                                    ValidationMessage.Severity.ERROR, null));
        }

        // validate the schedules
        checkRangeValues(hoursPerDay, new Integer(0), new Integer(24),
                "hoursPerDay2", "Hours Per Day");
        checkRangeValues(daysPerWeek, new Integer(0), new Integer(7),
                "daysPerWeek2", "Days Per Week");
        checkRangeValues(weeksPerYear, new Integer(0), new Integer(52),
                "weeksPerYear2", "Weeks Per Year");
        checkRangeValues(hoursPerYear, new Double(0), getMaxOperatingHoursValue(emissionsReport),
                "hoursPerYear2", "Actual Hours");
        checkRangeValues(firstHalfHrsOfOperationPct, new Integer(0), new Integer(100),
                "firstHalfHrsOfOperationPct", "Percent of Hours of Operation (First Half year)");
        checkRangeValues(secondHalfHrsOfOperationPct, new Integer(0), new Integer(100),
                "secondHalfHrsOfOperationPct", "Percent of Hours of Operation (Second Half year)");
        

        // validate the fire variables are in correct range.
        for (EmissionsVariable ev : vars) {
            if (ev.getValue() != null) {
                String vs = ev.getVariable();
                FireVariableNamesDef fvnd = (FireVariableNamesDef) FireVariableNamesDef
                        .getData().getItem(vs);
                if (fvnd != null) {
                    // Check min value
                    if (fvnd.getMinVal() != null && fvnd.getMinVal().length() > 0) {
                        double d = EmissionsReport.convertStringToNum(fvnd
                                .getMinVal());
                        if (ev.getValueV().doubleValue() < d) {
                            validationMessages
                                    .put(
                                            fvnd.getCode(),
                                            new ValidationMessage(
                                                    "minVariable"
                                                            + fvnd.getCode(),
                                                    "Value of variable "
                                                            + fvnd.getCode()
                                                            + " is less than the minimum of "
                                                            + fvnd.getMinVal(),
                                                    ValidationMessage.Severity.ERROR,
                                                    null));
                        }
                    }
                    // Check max value
                    if (fvnd.getMaxVal() != null && fvnd.getMaxVal().length() > 0) {
                        double d = EmissionsReport.convertStringToNum(fvnd
                                .getMaxVal());
                        if (ev.getValueV().doubleValue() > d) {
                            validationMessages
                                    .put(
                                            fvnd.getCode(),
                                            new ValidationMessage(
                                                    "maxVariable"
                                                            + fvnd.getCode(),
                                                    "Value of variable "
                                                            + fvnd.getCode()
                                                            + " is greater than the maximum of "
                                                            + fvnd.getMaxVal(),
                                                    ValidationMessage.Severity.ERROR,
                                                    null));
                        }
                    }
                }
            }
        }

        return new ArrayList<ValidationMessage>(validationMessages.values())
                .toArray(new ValidationMessage[0]);
    }

	// This routine used both to check at edit and at validation time.
    public boolean checkEmissionsInfo(List<EmissionRow> periodEmissions,
            FireRow[] fRows, EmissionsReport rpt) {
        boolean ok = true;
        outer: for (int i = 0; i < periodEmissions.size(); i++) {
            EmissionRow erI = periodEmissions.get(i);
            removeAll("dup");
            // Check for no pollutant entered -- new row where pollutant not
            // selected
            if (null == erI.getPollutantCd()) {
                DisplayUtil.displayError("Emissions: row " + (i + 1)
                        + " has no pollutant name entered\")."
                        + "  Select a pollutant or delete the row.");
                ok = false;
                continue outer; // skip other tests for this row.
            }
            NonToxicPollutantDef baseDef;
            try {
                baseDef = NonToxicPollutantDef.getPollutantBaseDef(erI
                        .getPollutantCd());
            } catch (ApplicationException e) {
                DisplayUtil
                        .displayError("Error retrieving pollutant information for row "
                                + (i + 1)
                                + ".  If problem continues, ask for help.");
                ok = false;
                continue outer;
            }
            if (null != erI.getPollutantCd()) {
                // make sure units has a value;
                if (erI.getEmissionsUnitNumerator() == null) {
                    DisplayUtil.displayError("Emissions: row " + (i + 1)
                            + " (pollutant \"" + baseDef.getDescription()
                            + "\")." + "  Units is not set.");
                    ok = false;
                    logger
                            .error("EmissionsUnitNumerator is null for pollutant "
                                    + erI.getPollutantCd()
                                    + " in some period of report "
                                    + rpt.getEmissionsRptId()
                                    + ".  This message is to help determine why it is null:  the only effect the user notices is that they may have to reset units even though it had earlier been set.");
                }
            }
            // check for duplicate
            for (int j = i - 1; j >= 0; j--) {
                EmissionRow erJ = periodEmissions.get(j);
                if (erI.getPollutantCd().equals(erJ.getPollutantCd())) {
                    DisplayUtil
                            .displayError("Emissions: row "
                                    + (i + 1)
                                    + " is a duplicate of row "
                                    + (j + 1)
                                    + " (pollutant \""
                                    + baseDef.getDescription()
                                    + "\")."
                                    + "  Select a different pollutant, delete one of the rows or cancel the update.");
                    ok = false;
                    continue outer; // skip other tests for this row.
                }
            }

            // Check for pollutant being deprecated
            if (baseDef.isDeprecatedReally()) {
                DisplayUtil
                        .displayError("Emissions: row "
                                + (i + 1)
                                + ", Pollutant "
                                + baseDef.getDescription()
                                + ", is no longer available for use (is inactive).  The Emissions Inventory cannot be saved until the System Administrator makes a configuration change.");
                erI.setDeletable(true); // Make sure it can be deleted.
                ok = false;
                continue outer; // No point to check any further
            }

            if (erI.getAnnualAdjust() != null && hoursPerYear != null
                    && erI.getAnnualAdjustV() > hoursPerYear) {
                ok = false;
                DisplayUtil
                        .displayError("Emissions: row "
                                + (i + 1)
                                + ", Pollutant "
                                + baseDef.getDescription()
                                + ", hours of uncontrolled operation can not exceed the hours of operation per year.");
            }

            if (null != erI.getEmissionCalcMethodCd()) {
                if (!erI.getEmissionCalcMethodCd().equals(
                        EmissionCalcMethodDef.SCCEmissionsFactor)) {
                    //erI.setFireRef(null); // remove fire reference if not
                                            // autocalculate method
                }
                if (EmissionCalcMethodDef.isFactorMethod(erI
                        .getEmissionCalcMethodCd())) {
//                    if (null != erI.getFugitiveEmissions()
//                            || null != erI.getStackEmissions()) {
//                        DisplayUtil
//                                .displayError("Emissions: row "
//                                        + (i + 1)
//                                        + ", Pollutant "
//                                        + baseDef.getDescription()
//                                        + ": Because a factor method is selected,  delete the fugitive/stack amounts reported.");
//                        ok = false;
//                    }
                } else if (null != erI.getAnnualAdjust()) {
                    DisplayUtil
                            .displayError("Emissions: row "
                                    + (i + 1)
                                    + ", Pollutant "
                                    + baseDef.getDescription()
                                    + ", cannot specify Hours Uncontrolled or Uncontrolled Emissions Factor with an emissions method.  Delete Hours Uncontrolled and Uncontrolled Emissions Factor or cancel the update.");
                    ok = false;
                }
            } else if (null != erI.getFugitiveEmissions()
                    || null != erI.getStackEmissions()
//                    || null != erI.getFactorNumericValue()
                    || null != erI.getAnnualAdjust()) {
                DisplayUtil
                        .displayError("Emissions: row "
                                + (i + 1)
                                + ", Pollutant "
                                + baseDef.getDescription()
                                + ", emissions Method Used not specified.  Select a calculation method, delete other information for this emission or cancel the update.");
                ok = false;
            }
            // Check for deprecated FIRE reference
            if (null != erI.getFireRef()) {
                boolean rowFound = false;
                for (int k = 0; k < fRows.length; k++) {
                    if (erI.getFireRef().equals(fRows[k].getFactorId())) {
                        rowFound = true;
                        if (!fRows[k].isActive(rpt.getReportYear())) {
                            if (!rpt.isSubmitted()) { // report not yet
                                                        // submitted
                                DisplayUtil
                                        .displayError("Emissions: row "
                                                + (i + 1)
                                                + " (pollutant \""
                                                + baseDef.getDescription()
                                                + "\").   FIRE row selected is no longer active for year "
                                                + rpt.getReportYear()
                                                + ".  Choose a different fire row or different calculation method.");
                                ok = false;
                            }
                        }
                        break;
                    }
                }
                if (!rowFound) {
                    DisplayUtil
                            .displayError("Emissions: row "
                                    + (i + 1)
                                    + " (pollutant \""
                                    + baseDef.getDescription()
                                    + "\").   FIRE row no longer in the table. Choose a different fire row or different calculation method.");
                    ok = false;
                }
            }
        }
        return ok;
    }

    public void updateFireFactors(List<EmissionRow> periodEmissions,
            FireRow[] fRows, EmissionsReport rpt) {
        for (int i = 0; i < periodEmissions.size(); i++) {
            // Add new FIRE Factor values
            EmissionRow erI = periodEmissions.get(i);
            ArrayList<FireRow> choices = null;
            if (erI.getEmissionCalcMethodCd() == null || EmissionCalcMethodDef.isFactorMethod(erI.getEmissionCalcMethodCd())
            		) {
                // Locate fire reference and factor -- if possible
                choices = FireRow.computeFireChoices(erI, getCurrentMaterial(),
                        fRows, rpt.getReportYear());
                boolean locatedRef = false;
                if (null != erI.getFireRef()) {
                    // locate the factor
                    for (FireRow fr : choices) {
                        // Note, if the factor is not found (because it is
                        // not valid for this time period, then no factor
                        // is supplied.
                        if (fr.getFactorId().equals(erI.getFireRef())) {
                            erI.setFormula(fr.getFormula());
                            if (!erI.getFactorNumericValueOverride()) {
                            	erI.setFactorNumericValue(fr.getFactorFormula());
                            }
                            erI.setFireRefFactor(fr.getFactorFormula());
                            locatedRef = true;
                            break;
                        }
                    }
                }
                if (!locatedRef) {
                    erI.setFireRef(null);
                }
                if (null == erI.getFireRef()) {
//                    erI.setFactorNumericValue(null); // reset
                    erI.setFormula(null); // reset
                    if (choices.size() == 1) {
                        FireRow fr = choices.get(0);
                        // exactly one choice, use it.
                        erI.setFormula(fr.getFormula());
                        if (!erI.getFactorNumericValueOverride()) {
                        	erI.setFactorNumericValue(fr.getFactorFormula());
                        }
                        erI.setFireRef(FireRow.getFireId(fr));
                        erI.setFireRefFactor(fr.getFactorFormula());
                    }
                }
            }
        }
    }

    public String convertSCC() {
        String ret;
        String scc = getSccId();

        if (null != scc && scc.length() == 8) {
            ret = scc.substring(0, 1) + "-" + scc.substring(1, 3) + "-"
                    + scc.substring(3, 6) + "-" + scc.substring(6);
        } else {
            ret = scc;
        }
        return ret;
    }

    public String getSccId() {
        String ret = null;

        if (sccCode != null && sccCode.getSccId() != null
                && sccCode.getSccId().length() > 7) {
            ret = sccCode.getSccId();
        }

        return ret;
    }

    public final HashMap<String, Emissions> getEmissions() {
        if (emissions == null) {
            emissions = new HashMap<String, Emissions>(0);
        }
        return emissions;
    }
    
    public final Emissions getEmission(String cd) {
        return emissions.get(cd);
    }

    public final void setEmissions(HashMap<String, Emissions> emissions) {
        if (emissions == null) {
            this.emissions = new HashMap<String, Emissions>(0);
        } else {
            this.emissions = emissions;
        }
    }

    public final void clearEmissions() {
        emissions = new HashMap<String, Emissions>(0);
    }

    public final void addEmission(Emissions newEmission) {

        if (newEmission != null) {
            emissions.put(newEmission.getPollutantCd(), newEmission);
        }
    }

    public final void addMau(EmissionsMaterialActionUnits mau) {
        if (maus != null) {
            maus.add(mau);
        }
    }

    public final void addVar(EmissionsVariable v) {
        if (vars != null) {
            vars.add(v);
        }
    }

    public final EmissionsMaterialActionUnits findRow(EmissionsMaterialActionUnits emau) {
        EmissionsMaterialActionUnits ret = null;

        for (EmissionsMaterialActionUnits e : maus) {
            if (e.getMaterial().equals(emau.getMaterial()) &&
                    e.getAction().equals(emau.getAction())) {
                ret = e;
            }
        }
        return ret;
    }

//    public final EmissionsMaterialActionUnits findActiveMaterial(String material) {
//        EmissionsMaterialActionUnits mau = findMaterial(material);
//
//        if ((null != mau) && !mau.isBelongs()) {
//            mau = null;
//        }
//
//        return mau;
//    }

    // public boolean notOperated() {
    // boolean notOperatedFlag = false;
    // EmissionsMaterialActionUnits emau = getCurrentMaus();
    // if(emau != null && emau.getThroughput() != null && emau.getThroughputV()
    // == 0d) {
    // notOperatedFlag = true;
    // }
    // if(hoursPerYear != null && hoursPerYear == 0) {
    // notOperatedFlag = true;
    // }
    // return notOperatedFlag;
    // }

    public List<ValidationMessage> submitVerify(EmissionsReportService eRBO, String name, EmissionsReport emissionsReport)
    throws ApplicationException {
        // Validate that SCC code exists and is not deprecated and
        // corresponds to an emission unit process.
    	checkPageValues(maus, emissionsReport);   // verify period values in the validation process
        if(notInFacility || caution) {
            validationMessages.put("EU " + emissionPeriodId,
                    new ValidationMessage("edit",
                            "Report Period: \"" +
                            treeLabel + "\" is currently not valid",
                            ValidationMessage.Severity.ERROR, "period:"
                            + ReportProfileBase.treeNodeId(this), name));
            return new ArrayList<ValidationMessage>(validationMessages.values());
        }
        if(zeroHours()) {
            return new ArrayList<ValidationMessage>(validationMessages.values());
        }

        // Validate that schedule and season have values
        requiredField(hoursPerYear, "hoursPerYear",
                "P:" + this.treeLabel + ":Schedule: Actual Hours", "period:" +
                ReportProfileBase.treeNodeId(this), name);

        requiredField(winterThroughputPct, "winterThroughputPct",
                "P:" + this.treeLabel + ":Throughput: Winter Percent", "period:" +
                ReportProfileBase.treeNodeId(this), name);
        requiredField(springThroughputPct, "springThroughputPct",
                "P:" + this.treeLabel + ":Throughput: Spring Percent", "period:" +
                ReportProfileBase.treeNodeId(this), name);
        requiredField(summerThroughputPct, "summerThroughputPct",
                "P:" + this.treeLabel + ":Throughput: Summer Percent", "period:" +
                ReportProfileBase.treeNodeId(this), name);
        requiredField(fallThroughputPct, "fallThroughputPct",
                "P:" + this.treeLabel + ":Throughput: Fall Percent", "period:" +
                ReportProfileBase.treeNodeId(this), name);
        
        int totalHrsOfOperationPct = 0;
        if(null != firstHalfHrsOfOperationPct)
        		totalHrsOfOperationPct += firstHalfHrsOfOperationPct.intValue();
        if(null != secondHalfHrsOfOperationPct)
    		totalHrsOfOperationPct += secondHalfHrsOfOperationPct.intValue();
        if(100 != totalHrsOfOperationPct) {
        	validationMessages.put("Percent of Hours of Operation" +
                    emissionPeriodId,
                    new ValidationMessage("edit1", "P:" + this.treeLabel + 
                            ":Total of Percent of Hours of Operation should be 100",
                            ValidationMessage.Severity.ERROR, "period:"
                            + ReportProfileBase.treeNodeId(this), name));
        }
       
        FireRow[] periodFireRows = new FireRow[0];
        try {
            periodFireRows =  eRBO.retrieveFireRows(emissionsReport.getReportYear(), this);
        } catch (DAOException ee){
            validationMessages.put("FailToReadFire " +
                    emissionPeriodId,
                    new ValidationMessage("edit2", "P:" + this.treeLabel + 
                            ":Failed to read FIRE data for this period",
                            ValidationMessage.Severity.ERROR, "period:"
                            + ReportProfileBase.treeNodeId(this), name));
        } catch (RemoteException ee){
            validationMessages.put("FailToReadFire " +
                    emissionPeriodId,
                    new ValidationMessage("edit2", "P:" + this.treeLabel + 
                            ":Failed to read FIRE data for this period",
                            ValidationMessage.Severity.ERROR, "period:"
                            + ReportProfileBase.treeNodeId(this), name));
        }
        
        // Validate that material is selected and has throughput
        EmissionsMaterialActionUnits  emau = getCurrentMaus();
        if(emau == null) { // Is material selected
            validationMessages.put("NoMaterial " +
                    emissionPeriodId,
                    new ValidationMessage("edit1", "P:" + this.treeLabel +
                            ":No Material Selected",
                            ValidationMessage.Severity.ERROR, "period:"
                            + ReportProfileBase.treeNodeId(this), name));
        } else { // if material is deprecated
            String currentMaterial = emau.getMaterial();
            boolean notActive = true;
            for (FireRow row : periodFireRows) {
                if(!row.isActive(emissionsReport.getReportYear()) && (currentMaterial == null || !currentMaterial.equals(row.getMaterial()))) {
                    continue;
                }
                if(!row.isActive(emissionsReport.getReportYear())) {
                    continue; // row not active
                }
                if(!currentMaterial.equals(row.getMaterial())) {
                    continue; // row not correct material
                }
                notActive = false;
            }
            if(notActive) {
                validationMessages.put("NoActiveMaterial " +
                        emissionPeriodId,
                        new ValidationMessage("edit1", "P:" +
                                this.treeLabel + ":Material not available for this report year",
                                ValidationMessage.Severity.ERROR, "period:"
                                + ReportProfileBase.treeNodeId(this), name));
            }  else { // if material, continue checking
                if(emau.getThroughput() == null) {
                    validationMessages.put("NoThroughput " +
                            emissionPeriodId,
                            new ValidationMessage("edit1", "P:" +
                                    this.treeLabel + ":Material throughput not specified",
                                    ValidationMessage.Severity.ERROR, "period:"
                                    + ReportProfileBase.treeNodeId(this), name));
                }
            }
        }

        // Validate that any variables are set
        for(EmissionsVariable ev : vars) {
            if(ev.getValue() == null || ev.getValue().length() == 0) {
                validationMessages.put("variableMissing " +
                        emissionPeriodId + " " + ev.getVariable(),
                        new ValidationMessage("edit2", "P:" + this.treeLabel + 
                                ":The variable " + ev.getVariable() + " is not set",
                                ValidationMessage.Severity.ERROR, "period:"
                                + ReportProfileBase.treeNodeId(this), name));
            }
        }
        
        PollutantPartOf partOf = new PollutantPartOf();
        
        for(Emissions e : emissions.values()) {
            // Validate that no deprecated or toxic pollutants are used.
            if(NonToxicPollutantDef.getPollutantBaseDef(e.getPollutantCd()).isDeprecatedReally()){
                validationMessages.put("deprecated " +
                        emissionPeriodId + e.getPollutantCd(),
                        new ValidationMessage("edit2", "P:" + this.treeLabel + ":" +
                                NonToxicPollutantDef.getTheDescription(e.getPollutantCd()) +
                                " is no longer available for use (deprecated)",
                                ValidationMessage.Severity.ERROR, "period:"
                                + ReportProfileBase.treeNodeId(this), name));
                continue;  // Do not continue validating this pollutant.
            }
/*
            if(NonToxicPollutantDef.getPollutantBaseDef(e.getPollutantCd()).isDeprecated()){
                validationMessages.put("deprecated " +
                        emissionPeriodId + e.getPollutantCd(),
                        new ValidationMessage("edit2", "P:" + this.treeLabel + ":" +
                                NonToxicPollutantDef.getTheDescription(e.getPollutantCd()) +
                                " is no longer available for use (is  a Toxic)",
                                ValidationMessage.Severity.ERROR, "period:"
                                + ReportProfileBase.treeNodeId(this), name));
                continue;  // Do not continue validating this pollutant.
            }
*/
            // Validate that each pollutant row filled out completely
            if(null == e.getFugitiveEmissions() || null == e.getStackEmissions()) {
                validationMessages.put("MissingEmissions " +
                        emissionPeriodId + e.getPollutantCd(),
                        new ValidationMessage("edit2", "P:" + this.treeLabel + 
                                ":Emissions values missing for " +
                                NonToxicPollutantDef.getTheDescription(e.getPollutantCd()),
                                ValidationMessage.Severity.ERROR, "period:"
                                + ReportProfileBase.treeNodeId(this), name));
            } else {
                // validate that fugitive emissions are not really high and
                // should
                // probably be stack emissions instead.
                double fValue = EmissionsReport.convertStringToNum(e.getFugitiveEmissions());
                double v = EmissionUnitReportingDef.convert(
                        e.getEmissionsUnitNumerator(), fValue,
                        EmissionUnitReportingDef.TONS);
                if(v > 100) { // over 100 tons
                    validationMessages.put("fugitiveTooHigh " +
                            emissionPeriodId + e.getPollutantCd(),
                            new ValidationMessage("edit2", "P:" + this.treeLabel + 
                                    ":Fugitive Emissions are very high for " +
                                    NonToxicPollutantDef.getTheDescription(e.getPollutantCd())
                                    + ", please check to ensure this is correct. Should they be stack emissions instead?",
                                    ValidationMessage.Severity.WARNING, "period:"
                                    + ReportProfileBase.treeNodeId(this), name));
                }
            }

            // Validate that no deprecated FIRE rows are used
            FireRow found = null;
            if(null != e.getFireRef()) {
                for(FireRow r : periodFireRows) {
                    if(e.getFireRef().equals(r.getFactorId())){
                        found = r;
                        break;
                    }
                }

                if(found == null) {
                    String s1 = "FireFactor " +
                    emissionPeriodId + e.getPollutantCd();
                    String s2 = "P:" + this.treeLabel + ":FIRE factor for " +
                    NonToxicPollutantDef.getTheDescription(e.getPollutantCd()) +
                    " could not be located.";
                    validationMessages.put(s1,
                            new ValidationMessage("edit2", s2,
                                    ValidationMessage.Severity.ERROR, "period:"
                                    + ReportProfileBase.treeNodeId(this), name));
                    logger.error(s1 + " (" + name + "); " + s2);  // do not
                                                                    // expect
                                                                    // this
                } else if(!found.isActive(emissionsReport.getReportYear())) {
                    String s1 = "FireFactor " +
                    emissionPeriodId + e.getPollutantCd();
                    String s2 = "P:" + this.treeLabel + ":FIRE factor for " +
                    NonToxicPollutantDef.getTheDescription(e.getPollutantCd()) +
                    " is expired.";
                    validationMessages.put(s1,
                            new ValidationMessage("edit2", s2,
                                    ValidationMessage.Severity.ERROR, "period:"
                                    + ReportProfileBase.treeNodeId(this), name));
                }
            }

            // Validate pollutant parts do not exceed whole
            String errStr = partOf.verifySubpartTotals(e, emissions);
            if(null != errStr) {
                String s = "P:" + this.treeLabel + ":Emissions of (" +
                NonToxicPollutantDef.getTheDescription(e.getPollutantCd()) +
                ") " + errStr;
                validationMessages.put("PartsLargerThanWhole " +
                        emissionPeriodId + e.getPollutantCd(),
                        new ValidationMessage("edit2", s,
                                ValidationMessage.Severity.ERROR, "period:"
                                + ReportProfileBase.treeNodeId(this), name));
            }
        }

        // Validate that if there are PM condensible then there are PM
        // filterable
        String filt = "PM-FIL";
        String cond = "PM-CON";
        Emissions condE = emissions.get(cond);
        Emissions fillE = emissions.get(filt);
        boolean hasCond = false;
        boolean hasFilt = false;
        String condName = NonToxicPollutantDef.getTheDescription(cond);
        String filtName = NonToxicPollutantDef.getTheDescription(filt);
        if(condE != null) {
            if(condE.getFugitiveEmissions() != null
                    && condE.getFugitiveEmissions().length() > 0) {
                double condV = EmissionsReport.convertStringToNum(condE.getFugitiveEmissions());
                if(condV > 0d) {
                    hasCond = true; // must have filterable
                }
                if(hasCond) {
                	if (fillE != null) {
                		if(fillE.getFugitiveEmissions() != null) {
                			if( fillE.getFugitiveEmissions().length() == 0) {
                				hasFilt = true; // not yet filled in--no error yet.
                			} else {
                				double filtV = EmissionsReport.convertStringToNum(fillE.getFugitiveEmissions());
                				if(filtV > 0d) {
                					hasFilt = true;
                				}
                			}
                		}
                	}
                    if(!hasFilt) {  // validation error should also have
                                    // fugitive PM-FIL
                        validationMessages.put("condFiltFug " +
                                emissionPeriodId + "CondFiltPMFugitive",
                                new ValidationMessage("edit2",
                                        "Since there are fugitive " + condName + " there must also be fugitive "
                                        + filtName, ValidationMessage.Severity.ERROR, "period:"
                                        + ReportProfileBase.treeNodeId(this), name));
                    }
                }
                hasCond = false;
                hasFilt = false;
                if(condE.getStackEmissions() != null
                        && condE.getStackEmissions().length() > 0) {
                    condV = EmissionsReport.convertStringToNum(condE.getStackEmissions());
                    if(condV > 0d) {
                        hasCond = true; // must have filterable
                    }
                    if(hasCond) {
                    	if (fillE != null) {
                    		if(fillE.getStackEmissions() != null) {
                    			if( fillE.getStackEmissions().length() == 0) {
                    				hasFilt = true; // not yet filled in--no error
                    				// yet.
                    			} else {
                    				double filtV = EmissionsReport.convertStringToNum(fillE.getStackEmissions());
                    				if(filtV > 0d) {
                    					hasFilt = true;
                    				}
                    			}
                    		}
                    	}
                        if(!hasFilt) {  // validation error should also have
                                        // stack PM-FIL
                            validationMessages.put("condFiltStk " +
                                    emissionPeriodId + "CondFiltPMStack",
                                    new ValidationMessage("edit2",
                                            "Since there are stack " + condName + " there must also be stack "
                                            + filtName, ValidationMessage.Severity.ERROR, "period:"
                                            + ReportProfileBase.treeNodeId(this), name));
                        }
                    }
                }
            }

        }

        return new ArrayList<ValidationMessage>(validationMessages.values());
    }

//    public void removeEmissionLevels() {
//        for (Emissions e : emissions.values()) {
//            e.setFugitiveEmissions(null);
//            e.setStackEmissions(null);
//        }
//    }

    public float percentControlledAverage() {
        int cnt = 0;
        float sum = 0f;
        for (Emissions e : emissions.values()) {
            if (e.getFactorNumericValue() != null) {
                // Have a factor
                float adj = 0;
                if (e.getAnnualAdjust() != null) {
                    adj = Float.parseFloat(e.getAnnualAdjust());
                    sum = sum + adj;
                    cnt++;
                }
            }
        }
        if (cnt > 0 && hoursPerYear != null) {
            // totalHours - (average uncontrolled hours)]*100/totalHours.
            return (float) ((hoursPerYear - sum / cnt) * 100f / hoursPerYear);
        } else { // no usable factors
            return 100f;
        }
    }

    public final Integer getEmissionPeriodId() {
        return emissionPeriodId;
    }

    public final void setEmissionPeriodId(Integer emissionPeriodId) {
        this.emissionPeriodId = emissionPeriodId;
    }

    public final Integer getEmissionsRptId() {
        return emissionsRptId;
    }

    public final void setEmissionsRptId(Integer emissionsRptId) {
        this.emissionsRptId = emissionsRptId;
    }

    public final SccCode getSccCode() {
        return sccCode;
    }

    public final void setSccCode(SccCode sccCode) {
        this.sccCode = sccCode;
    }

    public final Integer getDaysPerWeek() {
        return daysPerWeek;
    }

    public final void setDaysPerWeek(Integer daysPerWeek) {
        this.daysPerWeek = daysPerWeek;
    }

    public final Integer getFallThroughputPct() {
        return fallThroughputPct;
    }

    public final void setFallThroughputPct(Integer fallThroughputPct) {
        checkRangeValues(fallThroughputPct, zeroInteger, oneHundredInteger,
                "fall", "Fall Throughput Percent");
        this.fallThroughputPct = fallThroughputPct;
    }

    public final Integer getHoursPerDay() {
        return hoursPerDay;
    }

    public final void setHoursPerDay(Integer hoursPerDay) {
        this.hoursPerDay = hoursPerDay;
    }

    public final Double getHoursPerYear() {
        return hoursPerYear;
    }

    public final void setHoursPerYear(Double hoursPerYear) {
        this.hoursPerYear = hoursPerYear;
        if (zeroHours()) {
            emissions = new HashMap<String, Emissions>(0);
        }
    }

    public final Integer getSpringThroughputPct() {
        return springThroughputPct;
    }

    public final void setSpringThroughputPct(Integer springThroughputPct) {
        checkRangeValues(springThroughputPct, zeroInteger, oneHundredInteger,
                "spring", "Spring Throughput Percent");
        this.springThroughputPct = springThroughputPct;
    }

    public final Integer getSummerThroughputPct() {
        return summerThroughputPct;
    }

    public final void setSummerThroughputPct(Integer summerThroughputPct) {
        checkRangeValues(summerThroughputPct, zeroInteger, oneHundredInteger,
                "summer", "Summer Throughput Percent");
        this.summerThroughputPct = summerThroughputPct;
    }

    public final Integer getWeeksPerYear() {
        return weeksPerYear;
    }

    public final void setWeeksPerYear(Integer weeksPerYear) {
        this.weeksPerYear = weeksPerYear;
    }

    public final Integer getWinterThroughputPct() {
        return winterThroughputPct;
    }

    public final void setWinterThroughputPct(Integer winterThroughputPct) {
        checkRangeValues(winterThroughputPct, zeroInteger, oneHundredInteger,
                "winter", "Winter Throughput Percent");
        this.winterThroughputPct = winterThroughputPct;
    }

    public final void populate(ResultSet rs) {
        boolean foundAll = false;
        try {
            setEmissionPeriodId(AbstractDAO
                    .getInteger(rs, "emission_period_id"));
            sccCode.setSccId(rs.getString("scc_id"));
            setWinterThroughputPct(AbstractDAO.getInteger(rs,
                    "winter_throughput_pct"));
            setSpringThroughputPct(AbstractDAO.getInteger(rs,
                    "spring_throughput_pct"));
            setSummerThroughputPct(AbstractDAO.getInteger(rs,
                    "summer_throughput_pct"));
            setFallThroughputPct(AbstractDAO.getInteger(rs,
                    "fall_throughput_pct"));
            setDaysPerWeek(AbstractDAO.getInteger(rs, "days_per_week"));
            setWeeksPerYear(AbstractDAO.getInteger(rs, "weeks_per_year"));
            setHoursPerDay(AbstractDAO.getInteger(rs, "hours_per_day"));
            setHoursPerYear(AbstractDAO.getDouble(rs, "hours_per_year"));
            setFirstHalfHrsOfOperationPct(AbstractDAO.getInteger(rs, "first_half_hours_of_operation_pct"));
            setSecondHalfHrsOfOperationPct(AbstractDAO.getInteger(rs, "second_half_hours_of_operation_pct"));
            setLastModified(AbstractDAO.getInteger(rs, "emissionPeriod_lm"));
            setTradeSecretSText(rs.getString("sched_ts_just"));
            setTradeSecretS(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("sched_ts")));
            if (tradeSecretSText != null
                    && tradeSecretSText.trim().length() > 0) {
                tsRows = tradeSecretSText.length() / 160;
                tsRows++;
            }
            foundAll = true;
            if (rs.getString("pollutant_cd") != null) {
                do {
                    Emissions emission = new Emissions();

                    emission.populate(rs);

                    emissions.put(emission.getPollutantCd(), emission);
                } while (rs.next());
            }

        } catch (SQLException sqle) {
            if (!foundAll) {
                logger.warn(sqle.getMessage());
            }
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result
                + ((daysPerWeek == null) ? 0 : daysPerWeek.hashCode());
        result = PRIME
                * result
                + ((emissionPeriodId == null) ? 0 : emissionPeriodId.hashCode());
        result = PRIME * result
                + ((emissionsRptId == null) ? 0 : emissionsRptId.hashCode());
        result = PRIME
                * result
                + ((fallThroughputPct == null) ? 0 : fallThroughputPct
                        .hashCode());
        result = PRIME * result
                + ((hoursPerDay == null) ? 0 : hoursPerDay.hashCode());
        result = PRIME * result
                + ((hoursPerYear == null) ? 0 : hoursPerYear.hashCode());
        result = PRIME * result + (notInFacility ? 1231 : 1237);
        result = PRIME
                * result
                + ((springThroughputPct == null) ? 0 : springThroughputPct
                        .hashCode());
        result = PRIME
                * result
                + ((summerThroughputPct == null) ? 0 : summerThroughputPct
                        .hashCode());
        result = PRIME * result
                + ((treeLabel == null) ? 0 : treeLabel.hashCode());
        result = PRIME * result
                + ((weeksPerYear == null) ? 0 : weeksPerYear.hashCode());
        result = PRIME
                * result
                + ((winterThroughputPct == null) ? 0 : winterThroughputPct
                        .hashCode());
        result = PRIME * result
                + ((firstHalfHrsOfOperationPct == null) ? 0 : firstHalfHrsOfOperationPct.hashCode());
        result = PRIME * result
                + ((secondHalfHrsOfOperationPct == null) ? 0 : secondHalfHrsOfOperationPct.hashCode());
        
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
        final EmissionsReportPeriod other = (EmissionsReportPeriod) obj;
        if (daysPerWeek == null) {
            if (other.daysPerWeek != null)
                return false;
        } else if (!daysPerWeek.equals(other.daysPerWeek))
            return false;
        if (emissionPeriodId == null) {
            if (other.emissionPeriodId != null)
                return false;
        } else if (!emissionPeriodId.equals(other.emissionPeriodId))
            return false;
        if (emissionsRptId == null) {
            if (other.emissionsRptId != null)
                return false;
        } else if (!emissionsRptId.equals(other.emissionsRptId))
            return false;
        if (fallThroughputPct == null) {
            if (other.fallThroughputPct != null)
                return false;
        } else if (!fallThroughputPct.equals(other.fallThroughputPct))
            return false;
        if (hoursPerDay == null) {
            if (other.hoursPerDay != null)
                return false;
        } else if (!hoursPerDay.equals(other.hoursPerDay))
            return false;
        if (hoursPerYear == null) {
            if (other.hoursPerYear != null)
                return false;
        } else if (!hoursPerYear.equals(other.hoursPerYear))
            return false;
        if (notInFacility != other.notInFacility)
            return false;
        if (springThroughputPct == null) {
            if (other.springThroughputPct != null)
                return false;
        } else if (!springThroughputPct.equals(other.springThroughputPct))
            return false;
        if (summerThroughputPct == null) {
            if (other.summerThroughputPct != null)
                return false;
        } else if (!summerThroughputPct.equals(other.summerThroughputPct))
            return false;
        if (treeLabel == null) {
            if (other.treeLabel != null)
                return false;
        } else if (!treeLabel.equals(other.treeLabel))
            return false;
        if (weeksPerYear == null) {
            if (other.weeksPerYear != null)
                return false;
        } else if (!weeksPerYear.equals(other.weeksPerYear))
            return false;
        if (winterThroughputPct == null) {
            if (other.winterThroughputPct != null)
                return false;
        } else if (!winterThroughputPct.equals(other.winterThroughputPct))
            return false;
        if (firstHalfHrsOfOperationPct == null) {
            if (other.firstHalfHrsOfOperationPct != null)
                return false;
        } else if (!firstHalfHrsOfOperationPct.equals(other.firstHalfHrsOfOperationPct))
            return false;
        if (secondHalfHrsOfOperationPct == null) {
            if (other.secondHalfHrsOfOperationPct != null)
                return false;
        } else if (!secondHalfHrsOfOperationPct.equals(other.secondHalfHrsOfOperationPct))
            return false;
        return true;
    }

    public void clearErrors() {
        errors = null;
    }

    void addError(String error) {
        if (errors == null) {
            errors = new ArrayList<String>();
        }
        errors.add(error);
    }

    public final EmissionsReportPeriod getComp() {
        return comp;
    }

    public final void setComp(EmissionsReportPeriod comp) {
        this.comp = comp;
    }

    public final EmissionsReportPeriod getOrig() {
        return orig;
    }

    public final void setOrig(EmissionsReportPeriod orig) {
        this.orig = orig;
    }

    public final boolean isCaution() {
        return caution;
    }

    public final void setCaution(boolean caution) {
        this.caution = caution;
    }

    public final boolean isNotInFacility() {
        return notInFacility;
    }

    public final void setNotInFacility(boolean notInFacility) {
        this.notInFacility = notInFacility;
    }

    public final List<EmissionsMaterialActionUnits> getMaus() {
        return maus;
    }

    public final String getCurrentMaterialName() {
        String id = getCurrentMaterial();
        if (id != null) {
            return MaterialDef.getData().getItems().getItemDesc(id);
        }
        return "Material";
    }

    public final String getCurrentMaterial() {
        String currentMaterial = null;
        EmissionsMaterialActionUnits e = getCurrentMaus();
        if (e != null) {
            currentMaterial = e.getMaterial();
        }
        return currentMaterial;
    }

    public final EmissionsMaterialActionUnits getCurrentMaus() {
        EmissionsMaterialActionUnits e = null;
        for (EmissionsMaterialActionUnits em : this.maus) {
            if (em.isBelongs()) {
                e = em;
                break;
            }
        }
        return e;
    }

    public final void clearMaus() {
        maus = new ArrayList<EmissionsMaterialActionUnits>(0);
    }

    // used only for fromXML Stream conversiion on submit
    public final void setMaus(List<EmissionsMaterialActionUnits> maus) {
        this.maus = maus;
    }

    public final void setMaus(List<EmissionRow> periodEmissions,
            EmissionsMaterialActionUnits newMaterialRow,
            SCEmissionsReport scR) {
        // Change materials.
        // If an actual change, then adjust emissions.
        // Assume one material.

        EmissionsMaterialActionUnits emau = getCurrentMaus();
        
        List<SCPollutant> scPollutants = Arrays.asList(scR.getPollutants());

        if (emau != null && null != emau.getMaterial() && emau.getAction() != null) {
           if(!emau.getMaterial().equals(newMaterialRow.getMaterial()) ||
                   !emau.getAction().equals(newMaterialRow.getAction())) {
                // Remove all factors
                HashMap<String, Emissions> newEmissions = new HashMap<String, Emissions>(
                        0);
				for (EmissionRow er : periodEmissions) {
					// If pollutant is in Service Catalog for this EI, add it.
					if (scPollutants.contains(er.getPollutant())) {
						Emissions e = new Emissions(er, this.emissionPeriodId);
						if (e.getFactorNumericValue() != null) {
							e.setFactorNumericValue(null);
							// e.setStackEmissions(null);
							// e.setFugitiveEmissions(null);

						}
						if (e.getFactorNumericValueOverride())
							e.setFactorNumericValueOverride(false);
						if (e.getFireRef() != null) {
							e.setFireRef(null);
							// e.setFactorNumericValue(null);
							// e.setStackEmissions(null);
							// e.setFugitiveEmissions(null);
						}
						newEmissions.put(e.getPollutantCd(), e);
					}
				}
                // Replace emissions
                emissions = newEmissions;
            }
        }
        // Mark new material
        maus = new ArrayList<EmissionsMaterialActionUnits>(0);
        EmissionsMaterialActionUnits row = new EmissionsMaterialActionUnits();
        row.populateFields(newMaterialRow);
        row.setBelongs(true);
        maus.add(row);
    }

    // Name needed this way for jsp
    public boolean isZeroHours() {
        return zeroHours();
    }

    /*
     * returns true if hours per year are zero or material throughput is zero or
     * all emissions specified are zero
     */
    public boolean zeroHours() {
        boolean isZeroHours = false;
        if (hoursPerYear != null && hoursPerYear == 0) {
            isZeroHours = true;
        }
        if (!isZeroHours) {
            EmissionsMaterialActionUnits e = getCurrentMaus();
            if (e != null && e.getThroughput() != null
                    && e.getThroughput().length() != 0) {
                double d = EmissionsReport.convertStringToNum(
                        e.getThroughput(), logger);
                if (d == 0d) {
                    isZeroHours = true;
                }
            }
        }
        return isZeroHours;
    }

    /*
     * returns true if hours per year are zero or material throughput is zero or
     * all emissions specified are zero
     */
    public boolean zeroEmissions() {
        boolean isZeroEmissions = zeroHours();
        if (!isZeroEmissions) {
            isZeroEmissions = true;
            if (emissions.size() != 0) {
                // since there is a period and since the hours or material
                // throughput
                // is not zero, then we must have some emissions.
                // If none, then they are not yet filled out and leave
                // isZeroHours false;
                for (Emissions e : emissions.values()) {
                    if (e.getStackEmissions() == null
                            || e.getStackEmissions().length() == 0) {
                    } else {
                        double d = EmissionsReport.convertStringToNum(e
                                .getStackEmissions(), logger);
                        if (d != 0d) {
                            isZeroEmissions = false;
                            break;
                        }
                    }
                    if (e.getFugitiveEmissions() == null
                            || e.getFugitiveEmissions().length() == 0) {
                    } else {
                        double d = EmissionsReport.convertStringToNum(e
                                .getFugitiveEmissions(), logger);
                        if (d != 0d) {
                            isZeroEmissions = false;
                            break;
                        }
                    }
                }
            }
        }
        return isZeroEmissions;
    }

    public final String getTreeLabel() {
        return treeLabel;
    }

    public final void setTreeLabel(String treeLabel) {
        this.treeLabel = treeLabel;
    }

    public String getTradeSecretSText() {
        return tradeSecretSText;
    }

    public void setTradeSecretSText(String tradeSecretSText) {
        this.tradeSecretSText = tradeSecretSText;
    }

    public boolean isTradeSecretS() {
        return tradeSecretS;
    }

    public void setTradeSecretS(boolean tradeSecretS) {
        this.tradeSecretS = tradeSecretS;
        if (!tradeSecretS) {
            tradeSecretSText = null;
        }
    }

    public List<EmissionsVariable> getVars() {
        return vars;
    }

    // used only for fromXML Stream conversiion on submit
    public void setVars(List<EmissionsVariable> vars) {
        this.vars = vars;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public boolean isHpyDiff() {
        return hpyDiff;
    }

    public void setHpyDiff(boolean hpyDiff) {
        this.hpyDiff = hpyDiff;
    }

    public String getTreeLabelDesc() {
        return treeLabelDesc;
    }

    public void setTreeLabelDesc(String treeLabelDesc) {
        this.treeLabelDesc = treeLabelDesc;
    }

    public String getReqVars() {
        return reqVars;
    }

    public boolean isAutoPopulated() {
        return autoPopulated;
    }

    public int getTsRows() {
        return tsRows;
    }

    public String getAdjS() {
        return adjS;
    }

    public void setAdjS(String adjS) {
        this.adjS = adjS;
    }

    public double getAdjV() {
        return adjV;
    }

    public void setAdjV(double adjV) {
        this.adjV = adjV;
    }

    public int getCntAnnualAdjSame() {
        return cntAnnualAdjSame;
    }

    public void setCntAnnualAdjSame(int cntAnnualAdjSame) {
        this.cntAnnualAdjSame = cntAnnualAdjSame;
    }

    public ArrayList<EmissionRow> getCapHapList() {
        return capHapList;
    }

    public void setCapHapList(ArrayList<EmissionRow> capHapList) {
        this.capHapList = capHapList;
    }

    public FireRow[] getFireRows() {
        return fireRows;
    }

    public void setFireRows(FireRow[] fireRows) {
        this.fireRows = fireRows;
    }

    public String getStaticMaterial() {
        return staticMaterial;
    }

    public void setStaticMaterial(String staticMaterial) {
        this.staticMaterial = staticMaterial;
    }

    public Integer getStaticYear() {
        return staticYear;
    }

    public void setStaticYear(Integer staticYear) {
        this.staticYear = staticYear;
    }
    
    public Integer getFirstHalfHrsOfOperationPct() {
		return firstHalfHrsOfOperationPct;
	}

	public void setFirstHalfHrsOfOperationPct(Integer firstHalfHrsOfOperationPct) {
		this.firstHalfHrsOfOperationPct = firstHalfHrsOfOperationPct;
	}

	public Integer getSecondHalfHrsOfOperationPct() {
		return secondHalfHrsOfOperationPct;
	}

	public void setSecondHalfHrsOfOperationPct(Integer secondHalfHrsOfOperationPct) {
		this.secondHalfHrsOfOperationPct = secondHalfHrsOfOperationPct;
	}
	
	public void updateFirstHalfHrsOfOperationPct(ValueChangeEvent ve) {
		if(null == ve.getNewValue())
			setFirstHalfHrsOfOperationPct(new Integer(100));	
		else
		setFirstHalfHrsOfOperationPct(new Integer(100 - Integer.parseInt(ve.getNewValue().toString())));
	}
	
	public void updateSecondHalfHrsOfOperationPct(ValueChangeEvent ve) {
		if(null == ve.getNewValue())
			setSecondHalfHrsOfOperationPct(new Integer(100));
		else
			setSecondHalfHrsOfOperationPct(new Integer(100 - Integer.parseInt(ve.getNewValue().toString())));
	}	
	
	public void getTotalEmissions(){
		
	}
	
	public boolean isHasVariables() {
		return (null != this.vars && !this.vars.isEmpty()) ? true : false;
	}
	
	public boolean isHasMultipleMaterials() {
		return this.maus.size() > 1 ? true : false;
	}
	
	// called from dataEntryWizard.jsp
	public void validateOperatingHours(FacesContext context, UIComponent comp,
			Object value) {
		Double operatingHours = (Double) value;
		if (MIN_OPERATING_HOURS_VAL > operatingHours
				|| getMaxOperatingHoursValue() < operatingHours) {
			((CoreInputText) comp).setValid(false);
			FacesMessage message = new FacesMessage(
					"Operating Hours should be between "
							+ MIN_OPERATING_HOURS_VAL + " and "
							+ getMaxOperatingHoursValue());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(comp.getClientId(context), message);
		}
	}
	
	public double getMaxOperatingHoursValue() {
		ReportProfileBase reportProfile = (ReportProfileBase) FacesUtil
				.getManagedBean("reportProfile");
		return null != reportProfile ? reportProfile
				.getMaxHoursInReportingPeriod() : MAX_OPERATING_HOURS_VAL;
	}
	
    private double getMaxOperatingHoursValue(EmissionsReport emissionsReport) {
    	if (emissionsReport == null){
    		return MAX_OPERATING_HOURS_VAL;
    	}
    	
    	double maxHrs = emissionsReport.getMaxHoursInReportingPeriod();
		return maxHrs;
	}
//	 public final void checkPageValues(
//	            List<EmissionsMaterialActionUnits> periodMaterialList,  String name){
//    	 //EU emission_period_id      treeLabel = PRC001      eportProfileBase.treeNodeId(this) = P12371   name -> euId = ENG001
//		 checkPageValues(periodMaterialList);
//	    	//debug purpose
//		 
//		 
//    	 validationMessages.put("EU " + emissionPeriodId,
//                 new ValidationMessage("edit",
//                         "Report Period: \"" +
//                         treeLabel + "\" is currently not valid",
//                         ValidationMessage.Severity.ERROR, "period:"
//                         + ReportProfileBase.treeNodeId(this), name));
//    	 
//
//		 return;
//	 }
	    	
	
}