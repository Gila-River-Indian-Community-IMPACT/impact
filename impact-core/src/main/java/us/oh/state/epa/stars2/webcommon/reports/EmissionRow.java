package us.oh.state.epa.stars2.webcommon.reports;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dbObjects.emissionReport.Emissions;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsMaterialActionUnits;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEU;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportPeriod;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FireRow;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCDataImportPollutant;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCPollutant;
import us.oh.state.epa.stars2.def.EmissionCalcMethodDef;
import us.oh.state.epa.stars2.def.EmissionUnitReportingDef;
import us.oh.state.epa.stars2.def.NonToxicPollutantDef;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.framework.util.Utility;

/*  DESIGN
 * How Emissions Reporting handles deprecation of FIRE rows, SCC codes, Pollutants
 * and materials:
 * 
 * The deprecation flag in the Pollutant table is used.  If a pollutant is marked
 * deprecated, then it is not valid in an emissions inventory.  Care must be taken that
 * a deprecated pollutant is not in any report definition because if a revised
 * report is created with that report definition, then that pollutant cannnot be
 * removed from the report (because it is in the definition) and it cannot be
 * submitted because it fails validation (because deprecated).   Consequently,
 * a report definition for the year YYYY must be revised to not contain that
 * pollutant in order to successfully file revised reports.  Note that the report
 * for year YYYY with the most recent start date is the one used.  Also,
 * a deprecated pollutant will not be available in the pollutant pick list
 * when adding an emission row if it is deprecated.
 * 
 * The deprecation flag in the Material table is not used. The material
 * is considered active if it is in an active FIRE Factor table entry.
 * 
 * The SCC table does contain create/deprecate attributes which specify year.  The
 * SCC code is valid for the date range [create, deprecate).  So [1997, 2001) indicates
 * that the row is valid for the years 1997 up to but not including 2001.  The report
 * validates that the SCC codes used for report processes exist and not deprecated
 * for the year of the report.
 * 
 * The FIRE Factor table also contains create/deprecate attributes which specify year.
 * A FIRE Factor table entry can only be used if it is valid for the year of the
 * report.  If the FIRE Factor entry is valid, the SCC code is not checked.  Since
 * no emissions can be reported for a preocess with an SCC code that is not active,
 * the FIRE Factor table is not even used for that process.
 * 
 * If a FIRE Factor entry is active, the material specified is also
 * considered active.  Therefore, if the material is not valid for a
 * certain year, then if there is a FIRE Factor entry containing that material, the
 * FIRE Factor entry must have create/deprecate set so that the FIRE Factor
 * entry is not active for that year.
 * 
 * If a FIRE Factor entry is active, the pollutant specified can be used.
 * Therefore, if the pollutant is deprecated then all the FIRE rows containing that
 * pollutant should be marked not active for all possible report years.  Alternatively
 * the code could be changed to not use the FIRE row if the pollutant deprecate
 * flag is set to true;
 */

public class EmissionRow implements Comparable<EmissionRow>, Serializable {

	private static final long serialVersionUID = 447466161421620447L;

	private String pollutantCd;
	private Integer order;
	private String fireRef;
	private String fireRefFactor;
	private int numFireRows;
	private int activeFireRows;
	private String emissionCalcMethodCd;
	private boolean emissionCalcMethodDiff;
	private String fugitiveEmissions;
	private double fugitiveEmissionsV;
	private boolean fugitiveEmissionsDiff;
	private String totalEmissions;
	private double totalEmissionsV;
	private boolean totalEmissionsDiff;
	private String stackEmissions;
	private double stackEmissionsV;
	private boolean stackEmissionsDiff;
	private String emissionsUnitNumerator;
	private boolean expiredFire;
	private String factorNumericValue;
	private String timeBasedFactorNumericValue;
	private Double factorNumericValueV;
	private Double timeBasedFactorNumericValueV;
	private String formula; // Used to compute factor
	private boolean factorNumericValueDiff;
	private boolean timeBasedFactorNumericValueDiff;
	private String factorUnitNumerator;
	private String factorUnitDenominator;
	private boolean tradeSecretF;
	private String tradeSecretFText;
	private String annualAdjust;
	private double annualAdjustV;
	private boolean chgAllAnnualAdj;
	private boolean chgAllAnnualAdjRender;
	private String explanation;
	private String truncatedExp;
	private boolean tradeSecretE;
	private String tradeSecretEText;
	private String pollutant;
	private String category;
	private boolean fromComparisonRpt; // indicate if report or comparison
										// report.
	// true if the row added by user and not from FIRE or Report Template
	private boolean deletable;
	private boolean delete; // true if user asked to delete row
	private boolean newLine; // if just added to allow picking pollutantCd
	private boolean chargeable; // Is this pollutant charged $
	private boolean exceedThresholdQA;
	private boolean euLevel;
	private Float thresholdQA;
	private String thresholdQAStr;
	private boolean fugitivesPossible = true;
	private boolean stackEmissionsPossible = true;
	private boolean fromServiceCatalog = false;
	private Integer reportYear;
	private transient Logger logger;
	private boolean resetMethodType;
	private EmissionsReportPeriod p;

	public static EmissionRow locateRow(String pollutantCd, List<EmissionRow> l) {

		for (EmissionRow e : l) {
			if (pollutantCd.equals(e.getPollutantCd()))
				return e;
		}
		return null;
	}

	public EmissionRow() {
		emissionsUnitNumerator = EmissionUnitReportingDef.TONS;
		logger = Logger.getLogger(this.getClass());
		fromComparisonRpt = false;
		numFireRows = 0;
	}

	EmissionRow(String pollutantCd, Integer order, Float thresholdQa) throws ApplicationException {

		logger = Logger.getLogger(this.getClass());
		// this.modified = false;
		this.order = order;
		this.thresholdQA = thresholdQa;
		exceedThresholdQA = false;
		this.pollutantCd = pollutantCd;
		pollutant = NonToxicPollutantDef.getTheDescription(pollutantCd);
		category = NonToxicPollutantDef.getTheCategory(pollutantCd);
		fireRef = null;
		// fireRefFactor = null;
		numFireRows = 0;
		emissionCalcMethodCd = null;
		fugitiveEmissions = null;
		fugitiveEmissionsDiff = false;
		stackEmissions = null;
		stackEmissionsDiff = false;
		totalEmissions = null;
		totalEmissionsDiff = false;
		emissionsUnitNumerator = EmissionUnitReportingDef.TONS;
		setFactorNumericValue(null);
		timeBasedFactorNumericValue = null;
		factorUnitNumerator = null;
		factorUnitDenominator = null;
		annualAdjust = null;
		fromComparisonRpt = false;
		factorNumericValueDiff = false;
		timeBasedFactorNumericValueDiff = false;
		emissionCalcMethodDiff = false;
		reportYear = null;
	}

	EmissionRow(Emissions e) throws ApplicationException {

		logger = Logger.getLogger(this.getClass());
		pollutantCd = e.getPollutantCd();
		pollutant = NonToxicPollutantDef.getTheDescription(pollutantCd);
		fireRef = e.getFireRef();
		fireRefFactor = e.getFireRefFactor();
		numFireRows = 0;
		if (fireRef != null) {
			numFireRows = 1;
		}
		emissionCalcMethodCd = e.getEmissionCalcMethodCd();
		fugitiveEmissions = e.getFugitiveEmissions();
		stackEmissions = e.getStackEmissions();
		fugitiveEmissionsV = EmissionsReport.convertStringToNum(fugitiveEmissions, logger);
		stackEmissionsV = EmissionsReport.convertStringToNum(stackEmissions, logger);
		emissionsUnitNumerator = e.getEmissionsUnitNumerator();
		calcTotalEmissions(emissionsUnitNumerator);
		setFactorNumericValueOverride(e.getFactorNumericValueOverride());
		setFactorNumericValue(e.getFactorNumericValue());
		setTimeBasedFactorNumericValue(e.getTimeBasedFactorNumericValue());
		factorUnitNumerator = e.getFactorUnitNumerator();
		factorUnitDenominator = e.getFactorUnitDenominator();
		annualAdjust = e.getAnnualAdjust();
		annualAdjustV = EmissionsReport.convertStringToNum(annualAdjust, logger);
		category = NonToxicPollutantDef.getTheCategory(pollutantCd);
		setExplanation(e.getExplanation()); // need set to poopulate truncated
											// Exp
		tradeSecretEText = e.getTradeSecretEText();
		tradeSecretE = e.isTradeSecretE();
		tradeSecretFText = e.getTradeSecretFText();
		tradeSecretF = e.isTradeSecretF();
		reportYear = null;
		// Assume chargeable until proven otherwise.
		chargeable = true;
	}

	private void calcTotalEmissions(String units) {

		totalEmissions = null;
		exceedThresholdQA = false;
		if (stackEmissions != null && fugitiveEmissions != null) {

			totalEmissionsV = stackEmissionsV + fugitiveEmissionsV;
			totalEmissions = EmissionsReport.numberToString(totalEmissionsV);

			if (thresholdQA != null) {

				double v = EmissionUnitReportingDef.convert(units, totalEmissionsV, EmissionUnitReportingDef.TONS);

				if (v >= thresholdQA.floatValue()) {
					exceedThresholdQA = true;
					Double d = new Double(thresholdQA);
					thresholdQAStr = EmissionsReport.numberToString(d);
				}
			}
		}
	}

	private void setQaFlag(String units) {

		if (thresholdQA != null && totalEmissions != null) {
			double v = EmissionUnitReportingDef.convert(units, totalEmissionsV, EmissionUnitReportingDef.TONS);
			if (v >= thresholdQA.floatValue()) {
				exceedThresholdQA = true;
				Double d = new Double(thresholdQA);
				thresholdQAStr = EmissionsReport.numberToString(d);
			}
		}
	}

	public static Integer secondTab1stRow(ArrayList<EmissionRow> rows) {

		Integer rtn = null;
		if (rows != null) {

			int rowNum = 0;
			boolean found = false;

			for (EmissionRow er : rows) {
				rtn = rowNum;
				if (er.order == null) {
					found = true;
					break;
				}
				rowNum++;
			}

			if (!found) {
				// Since we need an index one higher than the row found in,
				// we have a special case when no HAPs.
				rtn = rows.size();
			}
		}

		return rtn;
	}

	/**
	 * Compares this object with the specified object for order. Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public final int compareTo(EmissionRow compObj) {

		if (null != order) {
			if (null != compObj.order) {
				if (0 != order.compareTo(compObj.order)) {
					return order.compareTo(compObj.order); // orders are
															// different
				}
				if (0 != pollutant.compareTo(compObj.pollutant)) {
					return pollutant.compareTo(compObj.pollutant); // pollutants
																	// are
																	// different
				}
				if (fromComparisonRpt) {
					return 1;
				}
			}
			return -1; // Items without an order go after those with an order
						// number.
		}

		// order is null
		if (null != compObj.order) {
			// both have order null
			return 1;
		}

		boolean thisCap = false;
		if (null != category) {
			thisCap = category.toLowerCase().contains("cap");
		}
		boolean bCap = false;
		if (null != compObj.category) {
			bCap = compObj.category.toLowerCase().contains("cap");
		}
		if (thisCap && !bCap) {
			return -1;
		}
		if (!thisCap && bCap) {
			// Both are CAPs or both not CAPs.
			return 1;
		}
		if (thisCap) {
			if (0 != pollutant.compareTo(compObj.pollutant)) {
				// pollutants are different
				return pollutant.compareTo(compObj.pollutant);
			}
			if (fromComparisonRpt) {
				return 1;
			}
		}

		boolean thisGhg = false;
		if (null != category) {
			thisGhg = category.toLowerCase().contains("ghg");
		}
		boolean bGhg = false;
		if (null != compObj.category) {
			bGhg = compObj.category.toLowerCase().contains("ghg");
		}

		if (thisGhg && !bGhg) {
			return -1;
		}
		if (!thisGhg && bGhg) {
			return 1;
		}

		// Both are GHGs or both not
		if (0 != pollutant.compareTo(compObj.pollutant)) {
			// pollutants are different
			return pollutant.compareTo(compObj.pollutant);
		}
		if (fromComparisonRpt) {
			return 1;
		}

		return -1;
	}

	static private void initializeList(HashMap<String, EmissionRow> list, SCEmissionsReport scR)
			throws ApplicationException {

		if (null == scR) {
			return;
		}

		for (SCPollutant p : scR.getPollutants()) {

			EmissionRow e = new EmissionRow();
			e.pollutantCd = p.getPollutantCd();
			e.pollutant = NonToxicPollutantDef.getTheDescription(p.getPollutantCd());
			e.category = NonToxicPollutantDef.getTheCategory(p.getPollutantCd());
			e.order = p.getDisplayOrder();
			e.thresholdQA = p.getThresholdQa();
			// Init all to Tons.
			e.emissionsUnitNumerator = EmissionUnitReportingDef.TONS;
			e.chargeable = p.isChargeable();
			e.setFromServiceCatalog(true);
			list.put(e.getPollutantCd(), e);
		}

		return;
	}

	private static void initializeFromFire(HashMap<String, EmissionRow> list, int year, EmissionsReportPeriod period)
			throws ApplicationException {

		if (year < 2009) {
			// Don't augment from FIRE unless reporting year 2009 or later.
			return;
		}

		// Add in pollutants.
		// Determine hours uncontrolled (annualAdjust).
		// Should either be null -- if not all the same otherwise.
		// Should be the same as everything else.
		EmissionRow erow = new EmissionRow();
		erow.setP(period);
		erow.order = null;

		if (!erow.allAnnualAjustTheSame(erow) || period.getCntAnnualAdjSame() == 0) {
			period.setAdjS(null);
			period.setAdjV(0d);
		}

		for (EmissionsMaterialActionUnits mau : period.getMaus()) {

			for (FireRow row : period.getFireRows()) {

				if (!mau.getMaterial().equals(row.getMaterial()) || !row.isActive(year)) {
					continue;
				}

				String key = row.getPollutantCd();
				if (null == key) {
					// row was just to define material
					continue;
				}

				// Add row if pollutant not already in list
				if (list.containsKey(key)) {
					continue;
				}

				// Only include HAPs
				String catg = NonToxicPollutantDef.getTheCategory(key);
				if (!catg.toLowerCase().contains("hap") && !catg.toLowerCase().contains("cap")
						&& !catg.toLowerCase().contains("ghg")) {
					continue;
				}

				NonToxicPollutantDef baseDef;
				baseDef = NonToxicPollutantDef.getPollutantBaseDef(row.getPollutantCd());
				if (baseDef.isDeprecated()) {
					continue;
				}

				EmissionRow e = new EmissionRow();
				if (period.getCntAnnualAdjSame() <= 1 && period.getAdjS() == null) {
					e.annualAdjust = "0";
					e.annualAdjustV = 0;
				} else {
					e.annualAdjust = period.getAdjS();
					e.annualAdjustV = period.getAdjV();
				}

				e.pollutantCd = row.getPollutantCd();
				e.pollutant = NonToxicPollutantDef.getTheDescription(row.getPollutantCd());
				e.category = NonToxicPollutantDef.getTheCategory(e.pollutantCd);

				if (row.isFactorFormula()) {
					// If only one fire row and a HAP then use fire
					// factor/formula.
					int cnt = 0;
					for (FireRow r : period.getFireRows()) {

						if (!mau.getMaterial().equals(r.getMaterial()) || !r.isActive(year)) {
							continue;
						}

						// Only look at rows that define pollutant and a factor
						// or formula.
						if (null == r.getPollutantCd() || !r.isFactorFormula()) {
							continue;
						}

						if (e.pollutantCd.equals(r.getPollutantCd())) {
							cnt++;
						}
					}

					e.numFireRows = cnt;
					if (1 <= cnt) {
						try {
							e.emissionCalcMethodCd = EmissionCalcMethodDef.SCCEmissionsFactor;
							e.deletable = false;

							if (1 == cnt) {
								// If exactly one, then get factor.
								e.fireRef = FireRow.getFireId(row);
								e.fireRefFactor = row.getFactorFormula();
								e.setFactorNumericValue(row.getFactorFormula());
								e.setFormula(row.getFormula());
							}

						} catch (NumberFormatException ne) {
							// TODO do nothing
						}
					}
				}

				list.put(e.getPollutantCd(), e);
			}
		}
	}

	static public ArrayList<EmissionRow> getEmissions(EmissionsReport report, boolean doRptCompare, boolean submitted,
			Integer percentDiff, ReportTemplates rptT, Logger logger, boolean excludePhase1Boiler)
			throws ApplicationException {

		LinkedHashMap<String, EmissionRow> emissionsTotals = new LinkedHashMap<String, EmissionRow>();
		HashMap<String, EmissionRow> compareEmissionsTotals = null;
		addEmissions(doRptCompare ? report.getOrig() : report, emissionsTotals, logger, excludePhase1Boiler);

		if (!doRptCompare) {
			ArrayList<EmissionRow> l = new ArrayList<EmissionRow>();
			l = order(emissionsTotals.values(), rptT);
			return l;
		}

		float diffPercent = 20;
		if (percentDiff != null) {
			diffPercent = percentDiff.intValue() / 100.0f;
		}

		EmissionsReport compareReport = report.getComp();
		compareEmissionsTotals = new HashMap<String, EmissionRow>();
		addEmissions(compareReport, compareEmissionsTotals, logger, excludePhase1Boiler);

		return order(compareEmissions(emissionsTotals, compareEmissionsTotals, diffPercent,
				(doRptCompare ? report.getOrig().getReportYear() : report.getReportYear()),
				(doRptCompare ? compareReport.getReportYear() : report.getReportYear())), rptT);

	}

	static ArrayList<EmissionRow> compareEmissions(HashMap<String, EmissionRow> emissionsTotals,
			HashMap<String, EmissionRow> compareEmissionsTotals, double diffPercent, int origYear, int compYear)
			throws ApplicationException {

		ArrayList<EmissionRow> finalList = new ArrayList<EmissionRow>();

		for (EmissionRow r : emissionsTotals.values()) {

			r.fromComparisonRpt = false;
			finalList.add(r);
			boolean rFugitiveIsZero = true;
			boolean rStackIsZero = true;
			boolean rTotalIsZero = true;
			boolean rFactorNumericValueIsZero = true;
			boolean rTimeBasedFactorNumericValueIsZero = true;

			if (r.getFugitiveEmissions() != null && r.getFugitiveEmissionsV() != 0d) {
				// TODO is this comparison to zero
				rFugitiveIsZero = false;
			}
			if (r.getStackEmissions() != null && r.getStackEmissionsV() != 0d) {
				// TODO is this comparison to zero
				rStackIsZero = false;
			}
			if (r.getTotalEmissions() != null && r.getTotalEmissionsV() != 0d) {
				// TODO is this comparison to zero
				rTotalIsZero = false;
			}
			if (r.getFactorNumericValue() != null && r.getFactorNumericValueV() != 0d) {
				// TODO is this comparison to zero
				rFactorNumericValueIsZero = false;
			}
			if (r.getTimeBasedFactorNumericValue() != null && r.getTimeBasedFactorNumericValueV() != 0d) {
				// TODO is this comparison to zero
				rTimeBasedFactorNumericValueIsZero = false;
			}

			EmissionRow cRow = compareEmissionsTotals.remove(r.getPollutantCd());
			if (cRow == null) {

				// Pollutant not in comparison report, put in an empty row.
				EmissionRow row = new EmissionRow(r.getPollutantCd(), r.getOrder(), r.getThresholdQA());
				row.fromComparisonRpt = true;
				row.setReportYear(compYear);
				finalList.add(row);

				if (!rFugitiveIsZero) {
					r.setFugitiveEmissionsDiff(true);
				}
				if (!rStackIsZero) {
					r.setStackEmissionsDiff(true);
				}
				if (!rTotalIsZero) {
					r.setTotalEmissionsDiff(true);
				}
				if (r.getEmissionCalcMethodCd() != null && r.getEmissionCalcMethodCd().length() > 0) {
					r.setEmissionCalcMethodDiff(true);
				}
				if (!rFactorNumericValueIsZero) {
					r.setFactorNumericValueDiff(true);
				}
				if (!rTimeBasedFactorNumericValueIsZero) {
					r.setTimeBasedFactorNumericValueDiff(true);
				}

			} else {
				cRow.fromComparisonRpt = true;
				finalList.add(cRow);

				if (r.getEmissionCalcMethodCd() != null && r.getEmissionCalcMethodCd().length() > 0) {
					// Note value considered zero if null or units is null
					boolean cFugitiveIsZero = true;
					boolean cStackIsZero = true;
					boolean cTotalIsZero = true;

					if (cRow.getEmissionsUnitNumerator() != null && r.getEmissionsUnitNumerator() != null) {

						if (cRow.getFugitiveEmissions() != null && cRow.getFugitiveEmissionsV() != 0d) {
							// TODO is this comparison to zero
							cFugitiveIsZero = false;
						}
						if (cRow.getStackEmissions() != null && cRow.getStackEmissionsV() != 0d) {
							// TODO is this comparison to zero
							cStackIsZero = false;
						}
						if (cRow.getTotalEmissions() != null && cRow.getTotalEmissionsV() != 0d) {
							// TODO is this comparison to zero
							cTotalIsZero = false;
						}
					}

					// See how values compare
					if (cFugitiveIsZero != rFugitiveIsZero) {
						r.fugitiveEmissionsDiff = true;

					} else if (!cFugitiveIsZero) {
						double f = EmissionUnitReportingDef.convert(r.getEmissionsUnitNumerator(), r.fugitiveEmissionsV,
								cRow.emissionsUnitNumerator);
						r.fugitiveEmissionsDiff = flagDifference(cRow.fugitiveEmissionsV, f, diffPercent);
					}

					if (cStackIsZero != rStackIsZero) {
						r.setStackEmissionsDiff(true);

					} else if (!cStackIsZero) {
						double s = EmissionUnitReportingDef.convert(r.getEmissionsUnitNumerator(), r.stackEmissionsV,
								cRow.emissionsUnitNumerator);
						r.stackEmissionsDiff = flagDifference(cRow.stackEmissionsV, s, diffPercent);
					}

					if (cTotalIsZero != rTotalIsZero) {
						r.setTotalEmissionsDiff(true);

					} else if (!cTotalIsZero) {
						double t = EmissionUnitReportingDef.convert(r.getEmissionsUnitNumerator(), r.totalEmissionsV,
								cRow.emissionsUnitNumerator);
						r.totalEmissionsDiff = flagDifference(cRow.totalEmissionsV, t, diffPercent);
					}

					if (!r.emissionCalcMethodCd.equals(cRow.emissionCalcMethodCd)) {
						r.setEmissionCalcMethodDiff(true);
					}

					if (null != r.factorNumericValue ^ null != cRow.factorNumericValue) {
						r.factorNumericValueDiff = true;

					} else if (null != r.factorNumericValue && null != cRow.factorNumericValue) {

						if (EmissionCalcMethodDef.isFactorMethod(r.emissionCalcMethodCd)
								|| EmissionCalcMethodDef.isFactorMethod(cRow.emissionCalcMethodCd)) {
							r.factorNumericValueDiff = flagDifference(cRow.factorNumericValueV, r.factorNumericValueV,
									diffPercent);
						}
					}

					if (null != r.timeBasedFactorNumericValue ^ null != cRow.timeBasedFactorNumericValue) {
						r.timeBasedFactorNumericValueDiff = true;

					} else if (null != r.timeBasedFactorNumericValue && null != cRow.timeBasedFactorNumericValue) {

						if (EmissionCalcMethodDef.isTimeBasedFactorMethod(r.emissionCalcMethodCd)
								|| EmissionCalcMethodDef.isTimeBasedFactorMethod(cRow.emissionCalcMethodCd)) {
							r.timeBasedFactorNumericValueDiff = flagDifference(cRow.timeBasedFactorNumericValueV,
									r.timeBasedFactorNumericValueV, diffPercent);
						}
					}
				}
			}
		}

		// Go through any remaining pollutants.
		for (EmissionRow c : compareEmissionsTotals.values()) {

			EmissionRow r = new EmissionRow(c.getPollutantCd(), c.getOrder(), c.getThresholdQA());
			c.fromComparisonRpt = true;
			r.fromComparisonRpt = false;
			r.setReportYear(origYear);
			finalList.add(r);
			finalList.add(c);
		}

		return finalList;
	}

	public static boolean flagDifference(double cf, double of, double diffPercent) {

		double diff = cf * diffPercent; // find % difference
		if (of < cf - diff || of > cf + diff) {
			return true;
		}

		return false;
	}

	static void addEmissions(EmissionsReport report, HashMap<String, EmissionRow> emissionsTotals, Logger logger,
			boolean excludePhase1Boiler) throws ApplicationException {

		for (EmissionsReportEUGroup grpEmissionUnit : report.getEuGroups()) {
			addEmissions(grpEmissionUnit, emissionsTotals, logger, excludePhase1Boiler, report.getReportYear());
		}
		for (EmissionsReportEU e : report.getEus()) {
			addEmissions(e, emissionsTotals, logger, excludePhase1Boiler, report.getReportYear());
		}
	}

	static public ArrayList<EmissionRow> getEmissions(EmissionsReportEUGroup grpEmissionUnit, boolean doRptCompare,
			Integer percentDiff, ReportTemplates rptT, Logger logger) throws ApplicationException {

		return getEmissions(grpEmissionUnit, doRptCompare, percentDiff, rptT, logger, null, null);
	}

	static public ArrayList<EmissionRow> getEmissions(EmissionsReportEUGroup grpEmissionUnit, boolean doRptCompare,
			Integer percentDiff, ReportTemplates rptT, Logger logger, Integer origReportYear, Integer compReportYear)
			throws ApplicationException {

		HashMap<String, EmissionRow> emissionsTotals = new HashMap<String, EmissionRow>();
		if (!doRptCompare) {
			addEmissions(grpEmissionUnit, emissionsTotals, logger, false, origReportYear);
			return order(emissionsTotals.values(), rptT);
		}

		float diffPercent = percentDiff.intValue() / 100.0f;
		EmissionsReportEUGroup orig = grpEmissionUnit.getOrig();
		EmissionsReportEUGroup comp = grpEmissionUnit.getComp();

		if (null != orig) {
			addEmissions(orig, emissionsTotals, logger, false, origReportYear);
		}

		HashMap<String, EmissionRow> compareEmissionsTotals = new HashMap<String, EmissionRow>();
		if (null != comp) {
			addEmissions(comp, compareEmissionsTotals, logger, false, compReportYear);
		}

		return order(
				compareEmissions(emissionsTotals, compareEmissionsTotals, diffPercent, origReportYear, compReportYear),
				rptT);
	}

	static void addEmissions(EmissionsReportEUGroup grpEmissionUnit, HashMap<String, EmissionRow> emissionsTotals,
			Logger logger, boolean excludePhase1Boiler, Integer reportYear) throws ApplicationException {

		addEmissions(grpEmissionUnit.getPeriod(), emissionsTotals, logger, reportYear);
	}

	static public ArrayList<EmissionRow> getEmissions(EmissionsReportEU emissionUnit, boolean doRptCompare,
			Integer percentDiff, ReportTemplates rptT, Logger logger) throws ApplicationException {

		return getEmissions(emissionUnit, doRptCompare, percentDiff, rptT, logger, null, null);
	}

	static public ArrayList<EmissionRow> getEmissions(EmissionsReportEU emissionUnit, boolean doRptCompare,
			Integer percentDiff, ReportTemplates rptT, Logger logger, Integer origReportYear, Integer compReportYear)
			throws ApplicationException {

		HashMap<String, EmissionRow> emissionsTotals = new HashMap<String, EmissionRow>(); // createList();
		ArrayList<EmissionRow> alRows;

		if (!doRptCompare) {
			addEmissions(emissionUnit, emissionsTotals, logger, false, origReportYear);
			alRows = order(emissionsTotals.values(), rptT);

		} else {

			float diffPercent = percentDiff.intValue() / 100.0f;
			EmissionsReportEU orig = emissionUnit.getOrig();
			EmissionsReportEU comp = emissionUnit.getComp();

			if (null != orig) {
				addEmissions(orig, emissionsTotals, logger, false, origReportYear);
			}

			HashMap<String, EmissionRow> compareEmissionsTotals = new HashMap<String, EmissionRow>();
			if (null != comp) {
				addEmissions(comp, compareEmissionsTotals, logger, false, compReportYear);
			}

			alRows = order(compareEmissions(emissionsTotals, compareEmissionsTotals, diffPercent, origReportYear,
					compReportYear), rptT);
		}

		return alRows;
	}

	private static ArrayList<EmissionRow> order(Collection<EmissionRow> list, ReportTemplates rptT) {

		// Put into order.
		ArrayList<EmissionRow> alRows = new ArrayList<EmissionRow>();
		TreeSet<EmissionRow> tRows = new TreeSet<EmissionRow>();

		// Put into TreeSet to do the ordering.
		for (EmissionRow r : list) {

			if (rptT != null) {

				SCPollutant p;

				if (null != rptT.getSc()) {
					p = rptT.getSc().findPollutant(r.pollutantCd);
					if (null != p) {
						r.order = p.getDisplayOrder();
						r.thresholdQA = p.getThresholdQa();
						r.setQaFlag(r.getEmissionsUnitNumerator());
						r.chargeable = p.isChargeable();
						r.fromServiceCatalog = true;
					}
				}
			}

			tRows.add(r);
		}

		// Convert to a list since TreeSort does not work in wrapper.
		for (EmissionRow r : tRows) {
			alRows.add(r);
		}
		return alRows;
	}

	static void addEmissions(EmissionsReportEU emissionUnit, HashMap<String, EmissionRow> emissionsTotals,
			Logger logger, boolean excludePhase1Boiler, Integer reportYear) throws ApplicationException {

		if (excludePhase1Boiler && emissionUnit.isPhaseOneBoiler()) {
			return;
		}

		for (EmissionsReportPeriod erp : emissionUnit.getPeriods()) {
			addEmissions(erp, emissionsTotals, logger, reportYear);
		}
	}

	static public ArrayList<EmissionRow> getEmissions(int year, EmissionsReportPeriod emissionsPeriod,
			ReportTemplates rptT, boolean doRptCompare, boolean submitted, Integer percentDiff, Logger logger)
			throws ApplicationException {

		return getEmissions(year, -1, emissionsPeriod, rptT, doRptCompare, submitted, percentDiff, logger);
	}

	static public ArrayList<EmissionRow> getEmissions(int origYear, int compYear, EmissionsReportPeriod emissionsPeriod,
			ReportTemplates rptT, boolean doRptCompare, boolean submitted, Integer percentDiff, Logger logger)
			throws ApplicationException {

		return getEmissions(origYear, compYear, emissionsPeriod, rptT, doRptCompare, submitted, percentDiff, logger,
				false);
	}

	static public ArrayList<EmissionRow> getEmissions(int origYear, int compYear, EmissionsReportPeriod emissionsPeriod,
			ReportTemplates rptT, boolean doRptCompare, boolean submitted, Integer percentDiff, Logger logger,
			boolean autoGenerated) throws ApplicationException {

		HashMap<String, EmissionRow> emissionsTotals = new HashMap<String, EmissionRow>();

		if (!doRptCompare && !submitted && !emissionsPeriod.zeroHours()) {
			initializeList(emissionsTotals, rptT.getSc());
			// Only add in FIRE pollutants if EIS included. (STARS2)
			// IMPACT - always add in FIRE pollutants, for now.
			if (!autoGenerated) {
				initializeFromFire(emissionsTotals, origYear, emissionsPeriod);
			}
		}

		ArrayList<EmissionRow> alRows = new ArrayList<EmissionRow>();
		if (!doRptCompare) {
			initEmissions(emissionsPeriod, origYear, submitted, emissionsTotals, rptT, logger);
			defaultEmissionUnits(emissionsTotals);
			alRows = order(emissionsTotals.values(), rptT);

		} else {

			// Since compare treat like submitted to avoid changing anything.
			double diffPercent = percentDiff.intValue() / 100.0f;
			EmissionsReportPeriod orig = emissionsPeriod.getOrig();
			EmissionsReportPeriod comp = emissionsPeriod.getComp();
			if (null != orig) {
				initEmissions(orig, origYear, true, emissionsTotals, rptT, logger);
			}

			HashMap<String, EmissionRow> compareEmissionsTotals = new HashMap<String, EmissionRow>();
			if (null != comp) {
				initEmissions(comp, compYear, true, compareEmissionsTotals, rptT, logger);
			}

			alRows = order(compareEmissions(emissionsTotals, compareEmissionsTotals, diffPercent, origYear, compYear),
					rptT);
		}

		return alRows;
	}

	// If not already submitted, merge in the emission data already in report
	// with
	// what was prepopulated from the report definitions and picked up because
	// of FIRE.
	// Otherwise just add in the ordering and chargable from the reports.
	static void initEmissions(EmissionsReportPeriod emissionsPeriod, int year, boolean submitted,
			HashMap<String, EmissionRow> emissionsTotals, ReportTemplates rptT, Logger logger)
			throws ApplicationException {

		for (Emissions e : emissionsPeriod.getEmissions().values()) {

			e.setTradeSecretF(emissionsPeriod.isTradeSecretS());
			e.setTradeSecretE(emissionsPeriod.isTradeSecretS());

			if (!submitted) {

				String pm = e.getPollutantCd();
				
				
				NonToxicPollutantDef pollutantDef;
				pollutantDef = NonToxicPollutantDef.getPollutantBaseDef(pm);
				
				EmissionRow eRow = emissionsTotals.get(pm);
				if (eRow == null) {
					// Pollutant in the report but not required or included
					// because of FIRE. Only include it if it is not
					// deprecated and there is no factor (if throughput-based
					// factor selected).
					if (!pollutantDef.isDeprecated()) {
						EmissionRow eR = new EmissionRow(e);
						eR.setDeletable(true);
						eR.setP(emissionsPeriod);
						eR.init(e, emissionsPeriod, year, logger, rptT);
						if (!e.isRemoveRow()) {
							emissionsTotals.put(eR.getPollutantCd(), eR);
						}
					}

				} else {

					// Pollutant in the report and required or included because
					// of FIRE.
					// If it is deprecated, remove it.
					// If it is one of the four excluded PMs, it was added
					// manually,
					// so we should allow the user to delete it...set deletable
					// to true.
					if (pollutantDef.isDeprecated()) {
						emissionsTotals.remove(pm);
					} else if ("PM-FIL".equals(eRow.getPollutantCd()) || "PM10-FIL".equals(eRow.getPollutantCd())
							|| "PM25-FIL".equals(eRow.getPollutantCd()) || "PM-CON".equals(eRow.getPollutantCd())) {

						emissionsTotals.remove(pm);
						EmissionRow eR = new EmissionRow(e);
						eR.setDeletable(true);
						eR.setP(emissionsPeriod);
						eR.init(e, emissionsPeriod, year, logger, rptT);
						if (!e.isRemoveRow()) {
							emissionsTotals.put(eR.getPollutantCd(), eR);
						}

					} else {
						eRow.setP(emissionsPeriod);
						eRow.init(e, emissionsPeriod, year, logger, rptT);
						if (e.isRemoveRow()) {
							emissionsTotals.remove(pm);
						}
					}
				}

			} else {
				// Report already submitted.
				EmissionRow eR = new EmissionRow(e);

				if (rptT.getSc() != null) {
					SCPollutant scP = rptT.getSc().findPollutant(eR.getPollutantCd());
					if (scP != null) {
						eR.order = scP.getDisplayOrder();
						eR.thresholdQA = scP.getThresholdQa();
						eR.chargeable = scP.isChargeable();
						eR.fromServiceCatalog = true;
					} else {
						SCDataImportPollutant scD = rptT.getSc().findDIPollutant(eR.getPollutantCd());
						if (scD != null) {
							eR.fromServiceCatalog = true;
							eR.chargeable = true;
						}
					}
				}

				if (year != -1) {
					eR.setReportYear(new Integer(year));
				}

				emissionsTotals.put(eR.getPollutantCd(), eR);
			}
		}
	}

	// Set in default units if not already set.
	// Should only be error case where units of null pre-exists in database.
	static void defaultEmissionUnits(HashMap<String, EmissionRow> emissionsTotals) throws ApplicationException {

		for (EmissionRow r : emissionsTotals.values()) {
			if (r.getEmissionsUnitNumerator() == null) {
				r.setEmissionsUnitNumerator(EmissionUnitReportingDef.TONS);
			}
		}
	}

	public static void addEmissions(EmissionsReportPeriod emissionsPeriod, HashMap<String, EmissionRow> emissionsTotals,
			Logger logger, Integer reportYear) throws ApplicationException {

		for (Emissions e : emissionsPeriod.getEmissions().values()) { // Account
																		// for
																		// groups.
			String pm = e.getPollutantCd();
			EmissionRow eRow = emissionsTotals.get(pm);
			if (eRow == null) {
				// First time pollutant seen.
				EmissionRow eR = new EmissionRow(e);
				eR.setReportYear(reportYear);
				emissionsTotals.put(pm, eR);
			} else {
				eRow.setReportYear(reportYear);
				eRow.add(e);
			}
		}
	}

	// Only called if report not yet submittted
	private void init(Emissions e, EmissionsReportPeriod emissionsPeriod, int year, Logger logger, ReportTemplates rptT)
			throws ApplicationException {
		
		e.setRemoveRow(false);

		fugitiveEmissions = e.getFugitiveEmissions();
		stackEmissions = e.getStackEmissions();
		fugitiveEmissionsV = EmissionsReport.convertStringToNum(fugitiveEmissions, logger);
		stackEmissionsV = EmissionsReport.convertStringToNum(stackEmissions, logger);
		emissionsUnitNumerator = e.getEmissionsUnitNumerator();
		calcTotalEmissions(emissionsUnitNumerator);
		emissionCalcMethodCd = e.getEmissionCalcMethodCd();
		setFactorNumericValueOverride(e.getFactorNumericValueOverride());
		setFactorNumericValue(e.getFactorNumericValue());
		setTimeBasedFactorNumericValue(e.getTimeBasedFactorNumericValue());
		factorUnitNumerator = e.getFactorUnitNumerator();
		factorUnitDenominator = e.getFactorUnitDenominator();
		if (year != -1) {
			reportYear = new Integer(year);
		}
		String material;
		fireRefFactor = e.getFireRefFactor();

		// Must be autocalc to work with FIRE.
		fireRef = e.getFireRef();
		numFireRows = 0;
		activeFireRows = 0;
		material = emissionsPeriod.getCurrentMaterial();
		boolean found = false;
		FireRow alternateFireRow = null;

		this.expiredFire = false;

		for (FireRow fr : emissionsPeriod.getFireRows()) {

			if (fr.getFactorId().equals(fireRef)) {

				formula = fr.getFormula();
				String factorString = fr.getFactorFormula();
				if (factorString != null && !factorNumericValueOverride) {
					// Only replace with factor when it is a factor
					// otherwise leave any factor (previously computed from
					// formula) in place.
					setFactorNumericValue(fr.getFactorFormula());
				}

				found = true;
				numFireRows++;
				this.expiredFire = !fr.isActive(year);
				if (!this.expiredFire) {
					activeFireRows++;
				}

			} else {

				if (!fr.isActive(year) || fr.getMaterial() == null || fr.getPollutantCd() == null) {
					continue;
				}
				if (!fr.getMaterial().equals(material) || !fr.isFactorFormula()) {
					continue;
				}
				if (!fr.getPollutantCd().equals(e.getPollutantCd())) {
					continue;
				}
				found = true;
				alternateFireRow = fr;
				numFireRows++;
				activeFireRows++;
			}
		}

		if (found && this.expiredFire) {

			if (numFireRows == 2) {
				// If the fire factor expired and there is now only one active
				// fire factor,
				// replace the expired factor (unless the factor value was
				// user-supplied).
				// In either case, associate the current fire factor with the
				// emission row.
				String factorString = alternateFireRow.getFactorFormula();
				if (factorString != null && !getFactorNumericValueOverride()) {
					setFactorNumericValue(alternateFireRow.getFactorFormula());
				}

				fireRef = alternateFireRow.getFactorId();
				fireRefFactor = alternateFireRow.getFactorFormula();
				formula = alternateFireRow.getFormula();
				expiredFire = false;
				numFireRows--;

			} else if (numFireRows > 2) {
				// If the fire factor expired and there is more than one active
				// fire factor,
				// do not automatically pick one. If there is a user-supplied
				// factor, keep
				// it.
				if (!getFactorNumericValueOverride()) {
					setFactorNumericValue(null);
				}
				fireRef = null;
				fireRefFactor = null;
				formula = null;
				stackEmissions = null;
				fugitiveEmissions = null;
				totalEmissions = null;
				expiredFire = false;
				numFireRows--;
			} else if (numFireRows == 1) {
				
				// Is this pollutant in the Service Catalog?
				boolean isSCPollutant = false;
				if (rptT.getSc() != null) {
					SCPollutant scP = rptT.getSc().findPollutant(e.getPollutantCd());
					if (scP != null) {
						isSCPollutant = true;
					}
				}
				
				// If the only fire factor expired and there is no user-supplied
				// value,
				// remove the emissions row. Also, remove any fire row
				// association.
				if (!getFactorNumericValueOverride()) {
					// The fire factor has expired.
					// If there is not a user-supplied factor and not CAP pollutant and Throughput-based factor method,
					// remove the emissions row.
					if (!isSCPollutant && EmissionCalcMethodDef.SCCEmissionsFactor.equals(emissionCalcMethodCd)) {
						e.setRemoveRow(true);
					}
					// If this is a CAP row and fire row has expired and Throughput-based factor method,
					// refresh the fire variables.
					if (isSCPollutant && EmissionCalcMethodDef.SCCEmissionsFactor.equals(emissionCalcMethodCd)) {
							emissionCalcMethodCd = null;
							stackEmissions = null;
							fugitiveEmissions = null;
							totalEmissions = null;
					}
					// Reset factor numeric value.
					factorNumericValue = null;
					rememberedFactorNumericValue = null;
				}
					// If there is a user-supplied factor, continue to use it, but reset the fire variables.
				fireRef = null;
				fireRefFactor = null;
				formula = null;
				expiredFire = false;
			}
		}

		if (!found) {
			fireRef = null;
			fireRefFactor = null;
			formula = null;
			expiredFire = false;
			if (EmissionCalcMethodDef.isFactorMethod(emissionCalcMethodCd)) {
				stackEmissions = null;
				fugitiveEmissions = null;
				totalEmissions = null;
			}
			logger.error("Could not find FIRE row " + fireRef);
		}

		annualAdjust = e.getAnnualAdjust();
		annualAdjustV = EmissionsReport.convertStringToNum(annualAdjust, logger);
		pollutant = NonToxicPollutantDef.getTheDescription(pollutantCd);
		category = NonToxicPollutantDef.getTheCategory(pollutantCd);
		setExplanation(e.getExplanation()); // need set to poopulate truncated
											// Exp
		tradeSecretE = e.isTradeSecretE();
		tradeSecretEText = e.getTradeSecretEText();
		tradeSecretF = e.isTradeSecretF();
		tradeSecretFText = e.getTradeSecretFText();

		// Null is used by the .jsp to know there is no value.
		if (tradeSecretEText != null && tradeSecretEText.length() == 0) {
			tradeSecretEText = null;
		}
		if (explanation != null && explanation.length() == 0) {
			explanation = null;
		}

	}

	private void add(Emissions e) {

		double fE = EmissionsReport.convertStringToNum(e.getFugitiveEmissions(), logger);

		if (e.getFugitiveEmissions() != null && emissionsUnitNumerator != null
				&& e.getEmissionsUnitNumerator() != null) {
			// Add them in.
			double f = EmissionUnitReportingDef.convert(e.getEmissionsUnitNumerator(), fE, emissionsUnitNumerator);
			if (fugitiveEmissions == null) {
				// First non-null value
				fugitiveEmissionsV = f;
				fugitiveEmissions = EmissionsReport.numberToString(fugitiveEmissionsV);
			} else {
				fugitiveEmissionsV = fugitiveEmissionsV + f;
				fugitiveEmissions = EmissionsReport.numberToString(fugitiveEmissionsV);
			}
		}

		double sE = EmissionsReport.convertStringToNum(e.getStackEmissions(), logger);
		if (e.getStackEmissions() != null && emissionsUnitNumerator != null && e.getEmissionsUnitNumerator() != null) {
			// Add them in.
			double s = EmissionUnitReportingDef.convert(e.getEmissionsUnitNumerator(), sE, emissionsUnitNumerator);
			if (stackEmissions == null) {
				// First non-null value.
				stackEmissionsV = s;
				stackEmissions = EmissionsReport.numberToString(stackEmissionsV);
			} else {
				stackEmissionsV = stackEmissionsV + s;
				stackEmissions = EmissionsReport.numberToString(stackEmissionsV);
			}
		}

		calcTotalEmissions(emissionsUnitNumerator);
	}

	public static Double getTotalEmissions(EmissionsReportEU eu, Integer reportYear, ReportTemplates scReports,
			Logger logger) {
		double sum = 0d;
		for (EmissionsReportPeriod p : eu.getPeriods()) {
			Double val = getTotalEmissions(p, reportYear, scReports, logger);
			if (val != null) {
				sum = sum + val;
			} else {
				return null;
			}
		}
		return sum;
	}

	public static Double getTotalEmissions(EmissionsReportPeriod p, Integer reportYear, ReportTemplates scReports,
			Logger logger) {

		double sum = 0d;
		ArrayList<EmissionRow> emissionRows = null;
		FireRow[] fr = new FireRow[0];
		p.setFireRows(fr);

		try {
			emissionRows = EmissionRow.getEmissions(reportYear, p, scReports, false, true, 0, logger);
		} catch (ApplicationException e) {
			return null;
		}

		for (EmissionRow er : emissionRows) {

			if (er.isChargeable() && er.getEmissionsUnitNumerator() != null) {
				// Note that if stack or fugitive not set then value is zero.
				double v = EmissionUnitReportingDef.convert(er.getEmissionsUnitNumerator(), er.getStackEmissionsV(),
						EmissionUnitReportingDef.TONS);
				sum = sum + v;
				v = EmissionUnitReportingDef.convert(er.getEmissionsUnitNumerator(), er.getFugitiveEmissionsV(),
						EmissionUnitReportingDef.TONS);
				sum = sum + v;
			}
		}

		return sum;
	}

	public static Double getTotalEmissions(EmissionsReportEUGroup grp, Integer reportYear, ReportTemplates scReports,
			Logger logger) {

		double sum = 0d;
		ArrayList<EmissionRow> emissionRows = null;
		FireRow[] fr = new FireRow[0];
		grp.getPeriod().setFireRows(fr);

		try {
			emissionRows = EmissionRow.getEmissions(reportYear, grp.getPeriod(), scReports, false, true, 0, logger);
		} catch (ApplicationException e) {
			return null;
		}

		for (EmissionRow er : emissionRows) {
			if (er.isChargeable() && er.getEmissionsUnitNumerator() != null) {
				double v = EmissionUnitReportingDef.convert(er.getEmissionsUnitNumerator(), er.getStackEmissionsV(),
						EmissionUnitReportingDef.TONS);
				sum = sum + v;
				v = EmissionUnitReportingDef.convert(er.getEmissionsUnitNumerator(), er.getFugitiveEmissionsV(),
						EmissionUnitReportingDef.TONS);
				sum = sum + v;
			}
		}

		return sum;
	}

	public static String getTotalStringHyphon(Double sum) {
		String rtn = null;
		if (sum != null) {
			rtn = " - " + EmissionsReport.numberToString(sum) + "Ton";
		} else {
			rtn = "????";
		}
		return rtn;
	}

	public static String getTotalString(Double sum) {
		String rtn = null;
		if (sum != null) {
			rtn = EmissionsReport.numberToString(sum) + " Ton";
		} else {
			rtn = "????";
		}
		return rtn;
	}

	public static String getTotalSringHyphon(Double reportTotalTons) {
		return " - " + EmissionsReport.numberToString(reportTotalTons) + "Ton";
	}

	public static String getTotalSring(Double reportTotalTons) {
		return EmissionsReport.numberToString(reportTotalTons);
	}

	private boolean factorNumericValueOverride = false;

	public void setFactorNumericValueOverride(boolean factorNumericValueOverride) {
		this.factorNumericValueOverride = factorNumericValueOverride;
	}

	public void factorNumericValueChangeListener(ValueChangeEvent event) {

		if (event.getOldValue() == null && event.getNewValue() == null) {
			setFactorNumericValueOverride(false);
			setResetMethodType(false);

		} else {

			if (event.getOldValue() != null && event.getNewValue() != null
					&& !Utility.isNullOrEmpty((String) event.getOldValue())
					&& Utility.isNullOrEmpty((String) event.getNewValue())) {
				setFactorNumericValueOverride(false);
				setResetMethodType(true);
			} else {
				setFactorNumericValueOverride(true);
				setResetMethodType(false);

				// Reset factorNumericValueOverride if value entered
				// equals factor from the associated fire row.
				String newCode = null;
				if (event.getNewValue() != null) {
					newCode = (String) event.getNewValue();
				}

				Double newV = null;
				Double fireRefFactorV = null;
				if (newCode != null && !Utility.isNullOrEmpty((String) newCode)) {
					String s = ((String) newCode).replaceAll(",", "");
					newV = Double.valueOf(s);
				}
				if (getFireRefFactor() != null && !Utility.isNullOrEmpty(getFireRefFactor())) {
					String s = (getFireRefFactor()).replaceAll(",", "");
					fireRefFactorV = Double.valueOf(s);
				}

				if (fireRefFactorV != null && newV != null && newV.equals(fireRefFactorV)) {
					setFactorNumericValueOverride(false);
				}
			}
		}
	}

	public boolean getFactorNumericValueOverride() {
		return this.factorNumericValueOverride;
	}

	// reset row values
	public void emissionCalcMethodChangeListener(ValueChangeEvent event) {

		setAnnualAdjust(null);

		Integer code = null;
		if (null != event.getNewValue()) {
			code = Integer.parseInt((String) event.getNewValue());
		}
		if (null == code || code > 200) {
			if (null == getFactorNumericValue() && null != this.rememberedFactorNumericValue) {
				setFactorNumericValue(this.rememberedFactorNumericValue);
			}
		}

		// Reset default uncontrolled emissions factor for blanked factor
		// methods
		String oldCode = null;
		if (event.getOldValue() != null) {
			oldCode = (String) event.getOldValue();
		}
		if (!Utility.isNullOrEmpty(oldCode)) {
			String newCode = null;
			if (event.getNewValue() != null) {
				newCode = (String) event.getNewValue();
			}
			if (Utility.isNullOrEmpty(newCode)) {
				setFactorNumericValueOverride(false);
				if (!Utility.isNullOrEmpty(getFireRefFactor())) {
					setFactorNumericValue(getFireRefFactor());
				}
			}
		}

		setTimeBasedFactorNumericValue(null);
		setFugitiveEmissions(null);
		setStackEmissions(null);
		this.chgAllAnnualAdjRender = false;
	}

	private String rememberedFactorNumericValue = null;

	public String getEmissionCalcMethodCd() {
		return emissionCalcMethodCd;
	}

	public void setEmissionCalcMethodCd(String emissionCalcMethodCd) {

		// should we change hours uncontrolled?
		if (convertEmptyToNull(emissionCalcMethodCd) == null
				|| !EmissionCalcMethodDef.isFactorMethod(emissionCalcMethodCd)) {
			// No longer (if ever) a factor method.
			if (this.annualAdjust != null && allAnnualAjustTheSame(this) && p.getCntAnnualAdjSame() > 1) {
				this.annualAdjust = null;
				this.annualAdjustV = 0;
			}
			if (convertEmptyToNull(emissionCalcMethodCd) != null) {
				// Prepopulate emissions with zero if possible since emissions
				// method.
				if (!fugitivesPossible && fugitiveEmissions == null) {
					fugitiveEmissions = null;
					fugitiveEmissionsV = 0d;
				}
				if (!stackEmissionsPossible && stackEmissions == null) {
					stackEmissions = null;
					stackEmissionsV = 0d;
				}
			}

		} else {

			if (allAnnualAjustTheSame(this)) {
				this.annualAdjust = null;
				this.annualAdjustV = 0;
			}

			// Set emissions to null if emissions not possible and currently 0
			if (!fugitivesPossible && fugitiveEmissions != null) {
				if (EmissionsReport.convertStringToNum(fugitiveEmissions, logger) == 0d) {
					fugitiveEmissions = null;
					fugitiveEmissionsV = 0d;
				}
			}
			if (!stackEmissionsPossible && stackEmissions != null) {
				if (EmissionsReport.convertStringToNum(stackEmissions, logger) == 0d) {
					stackEmissions = null;
					stackEmissionsV = 0d;
				}
			}
			if (EmissionCalcMethodDef.SCCEmissionsFactor.equals(emissionCalcMethodCd)) {
				// ?
			}

			if (p.getStaticMaterial() != null && p.getStaticYear() != null) {
				int cnt = 0;
				for (FireRow r : p.getFireRows()) {
					if (!p.getStaticMaterial().equals(r.getMaterial())) {
						continue;
					}

					// Only look at rows that define pollutant and
					// a factor or formula.
					if (null == r.getPollutantCd() || !r.isFactorFormula()) {
						continue;
					}
					if (!this.getPollutantCd().equals(r.getPollutantCd())) {
						continue;
					}
					if (!r.isActive(p.getStaticYear())) {
						// Don't count it if not already selected and not active
						continue;
					}
					cnt++;
				}

				numFireRows = cnt;
			}
		}

		this.emissionCalcMethodCd = emissionCalcMethodCd;
	}

	public String getEmissionsUnitNumerator() {
		return emissionsUnitNumerator;
	}

	public void setEmissionsUnitNumerator(String emissionsUnitNumerator) {
		this.emissionsUnitNumerator = emissionsUnitNumerator;
	}

	public String getFactorUnitDenominator() {
		return factorUnitDenominator;
	}

	public void setFactorUnitDenominator(String factorUnitDenominator) {
		this.factorUnitDenominator = factorUnitDenominator;
	}

	public String getFactorUnitNumerator() {
		return factorUnitNumerator;
	}

	public void setFactorUnitNumerator(String factorUnitNumerator) {
		this.factorUnitNumerator = factorUnitNumerator;
	}

	public String getPollutantCd() {
		return pollutantCd;
	}

	public void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}

	public boolean isFugitiveEmissionsDiff() {
		return fugitiveEmissionsDiff;
	}

	public boolean isStackEmissionsDiff() {
		return stackEmissionsDiff;
	}

	public void setFugitiveEmissionsDiff(boolean fugitiveEmissionsDiff) {
		this.fugitiveEmissionsDiff = fugitiveEmissionsDiff;
	}

	public void setStackEmissionsDiff(boolean stackEmissionsDiff) {
		this.stackEmissionsDiff = stackEmissionsDiff;
	}

	public boolean isTotalEmissionsDiff() {
		return totalEmissionsDiff;
	}

	public void setTotalEmissionsDiff(boolean totalEmissionsDiff) {
		this.totalEmissionsDiff = totalEmissionsDiff;
	}

	public String getFireRef() {
		return fireRef;
	}

	public void setFireRef(String fireRef) {
		this.fireRef = fireRef;
	}

	public String getFireRefFactor() {
		return fireRefFactor;
	}

	public void setFireRefFactor(String fireRefFactor) {
		this.fireRefFactor = fireRefFactor;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public boolean isNewLine() {
		return newLine;
	}

	public void setNewLine(boolean newLine) {
		this.newLine = newLine;
	}

	public String getPollutant() {
		return pollutant;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {

		if (explanation != null && explanation.length() == 0) {
			this.explanation = null;
		} else {
			this.explanation = explanation;
			if (explanation != null && explanation.length() > 25) {
				truncatedExp = explanation.substring(0, 25) + "...";
			} else
				truncatedExp = explanation;
		}
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

	public String getTruncatedExp() {
		return truncatedExp;
	}

	public void setTruncatedExp(String truncatedExp) {
		this.truncatedExp = truncatedExp;
	}

	public boolean isEmissionCalcMethodDiff() {
		return emissionCalcMethodDiff;
	}

	public void setEmissionCalcMethodDiff(boolean emissionCalcMethodDiff) {
		this.emissionCalcMethodDiff = emissionCalcMethodDiff;
	}

	public boolean isFactorNumericValueDiff() {
		return factorNumericValueDiff;
	}

	public void setFactorNumericValueDiff(boolean factorNumericValueDiff) {
		this.factorNumericValueDiff = factorNumericValueDiff;
	}

	public boolean isTimeBasedFactorNumericValueDiff() {
		return timeBasedFactorNumericValueDiff;
	}

	public void setTimeBasedFactorNumericValueDiff(boolean timeBasedFactorNumericValueDiff) {
		this.timeBasedFactorNumericValueDiff = timeBasedFactorNumericValueDiff;
	}

	public boolean isFromComparisonRpt() {
		return fromComparisonRpt;
	}

	public boolean isChargeable() {
		return chargeable;
	}

	public void setPollutant(String pollutant) {
		this.pollutant = pollutant;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((pollutant == null) ? 0 : pollutant.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final EmissionRow other = (EmissionRow) obj;
		if (pollutant != other.pollutant)
			return false;

		return true;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getAnnualAdjust() {
		return annualAdjust;
	}

	public void setAnnualAdjust(String aAdjust) {

		p.setCntAnnualAdjSame(0);
		String annAdjust = convertEmptyToNull(aAdjust);
		// Do we change others? Check before any values changed.
		boolean allSame = true;

		// Don't attempt change all if this row is not factor method.
		if (EmissionCalcMethodDef.isFactorMethod(this.getEmissionCalcMethodCd()) && p.getCapHapList() != null) {

			for (EmissionRow er : p.getCapHapList()) {

				if (!sameType(this, er)) {
					continue;
				}
				er.chgAllAnnualAdjRender = false;
				er.chgAllAnnualAdj = false;
				if (er == this) {
					p.setCntAnnualAdjSame(p.getCntAnnualAdjSame() + 1);
					continue;
				}
				if (er.getEmissionCalcMethodCd() != null
						&& EmissionCalcMethodDef.isFactorMethod(er.getEmissionCalcMethodCd())) {
					if (!valuesEqual(er.annualAdjust, er.annualAdjustV, this.annualAdjust, this.annualAdjustV)) {
						allSame = false;
					} else {
						p.setCntAnnualAdjSame(p.getCntAnnualAdjSame() + 1);
					}
				}

			}
		}

		if (allSame && p.getCntAnnualAdjSame() > 1) {
			this.chgAllAnnualAdjRender = true;
		}

		this.annualAdjust = convertEmptyToNull(annAdjust);
		annualAdjustV = EmissionsReport.convertStringToNum(annAdjust, logger);

	}

	private static boolean sameType(EmissionRow r1, EmissionRow r2) {
		if (r1.order == null && r2.order == null)
			return true;
		if (r1.order != null && r2.order != null)
			return true;
		return false;
	}

	private boolean allAnnualAjustTheSame(EmissionRow selected) {

		boolean allSame = true;
		boolean found1st = false;
		p.setAdjS(null);
		p.setAdjV(0d);
		p.setCntAnnualAdjSame(0);

		if (p.getCapHapList() != null) {

			for (EmissionRow er : p.getCapHapList()) {
				if (!sameType(selected, er)) {
					continue;
				}
				if (er.getEmissionCalcMethodCd() != null
						&& EmissionCalcMethodDef.isFactorMethod(er.getEmissionCalcMethodCd())) {
					if (!found1st) {
						found1st = true;
						p.setAdjS(er.annualAdjust);
						p.setAdjV(er.annualAdjustV);
						p.setCntAnnualAdjSame(1);
						continue;
					}
					if (!valuesEqual(er.annualAdjust, er.annualAdjustV, p.getAdjS(), p.getAdjV())) {
						allSame = false;
						break;
					}
					p.setCntAnnualAdjSame(p.getCntAnnualAdjSame() + 1);
				}

			}
		}

		return allSame;
	}

	public boolean isChgAllAnnualAdj() {
		return chgAllAnnualAdj;
	}

	public void setChgAllAnnualAdj(boolean chgAllAnnualAdj) {

		this.chgAllAnnualAdj = chgAllAnnualAdj;
		if (chgAllAnnualAdj) {
			if (p.getCapHapList() != null) {
				for (EmissionRow er : p.getCapHapList()) {
					if (!sameType(this, er))
						continue;
					if (er.getEmissionCalcMethodCd() != null
							&& EmissionCalcMethodDef.isFactorMethod(er.getEmissionCalcMethodCd())) {
						er.annualAdjust = this.annualAdjust;
						er.annualAdjustV = this.annualAdjustV;
						er.chgAllAnnualAdjRender = false;
						er.chgAllAnnualAdj = false;
					}
				}
			}
		}
	}

	public static boolean isFactorFormulaUsed(ArrayList<EmissionRow> emissionsList) {

		// Have any values been filled into the cap table?
		boolean rtn = false;
		if (emissionsList != null) {
			for (EmissionRow er : emissionsList) {
				if (convertEmptyToNull(er.emissionCalcMethodCd) == null
						|| !er.emissionCalcMethodCd.equals(EmissionCalcMethodDef.SCCEmissionsFactor)) {
					continue;
				}
				if (convertEmptyToNull(er.formula) != null) {
					rtn = true;
				}
			}
		}

		return rtn;
	}

	static boolean valuesEqual(String s1, double d1, String s2, double d2) {

		if (s1 == null && s2 != null || s1 != null && s2 == null) {
			return false;
		}
		if (s1 == null && s2 == null) {
			return true;
		}
		return d1 == d2;
	}

	public double getAnnualAdjustV() {
		return annualAdjustV;
	}

	public void setAnnualAdjustV(double annualAdjustV) {
		this.annualAdjustV = annualAdjustV;
	}

	public String getFactorNumericValue() {
		return factorNumericValue;
	}

	public String getTimeBasedFactorNumericValue() {
		return timeBasedFactorNumericValue;
	}

	public void setFactorNumericValue(String factorNumericValue) {

		logger.debug("---> setFactorNumericValue() for EmissionCalcMethodCd: " + getEmissionCalcMethodCd());
		logger.debug("-----> current = " + this.factorNumericValue);
		logger.debug("-----> new = " + factorNumericValue);
		if (null != getFactorNumericValue()) {
			this.rememberedFactorNumericValue = getFactorNumericValue();
		}
		this.factorNumericValue = convertEmptyToNull(factorNumericValue);
		factorNumericValueV = EmissionsReport.convertStringToNum(factorNumericValue, logger);
		if (this.factorNumericValue != null) {
			this.factorNumericValue = EmissionsReport.numberToString(factorNumericValueV);
		}
	}

	public void setTimeBasedFactorNumericValue(String timeBasedFactorNumericValue) {
		this.timeBasedFactorNumericValue = convertEmptyToNull(timeBasedFactorNumericValue);
		timeBasedFactorNumericValueV = EmissionsReport.convertStringToNum(timeBasedFactorNumericValue, logger);
		if (this.timeBasedFactorNumericValue != null) {
			this.timeBasedFactorNumericValue = EmissionsReport.numberToString(timeBasedFactorNumericValueV);
		}
	}

	public Double getFactorNumericValueV() {
		return factorNumericValueV;
	}

	public Double getTimeBasedFactorNumericValueV() {
		return timeBasedFactorNumericValueV;
	}

	public double getFugitiveEmissionsV() {
		return fugitiveEmissionsV;
	}

	public double getStackEmissionsV() {
		return stackEmissionsV;
	}

	public String getFugitiveEmissions() {
		return fugitiveEmissions;
	}

	public void setFugitiveEmissions(String fugitiveEmissions) {
		this.fugitiveEmissions = convertEmptyToNull(fugitiveEmissions);
	}

	public String getStackEmissions() {
		return stackEmissions;
	}

	public void setStackEmissions(String stackEmissions) {
		this.stackEmissions = convertEmptyToNull(stackEmissions);
	}

	public String getTotalEmissions() {
		return totalEmissions;
	}

	public void setTotalEmissions(String totalEmissions) {
		this.totalEmissions = convertEmptyToNull(totalEmissions);
	}

	public double getTotalEmissionsV() {
		return totalEmissionsV;
	}

	public void setFugitiveEmissionsV(double fugitiveEmissionsV) {
		this.fugitiveEmissionsV = fugitiveEmissionsV;
	}

	public void setStackEmissionsV(double stackEmissionsV) {
		this.stackEmissionsV = stackEmissionsV;
	}

	public void setTotalEmissionsV(double totalEmissionsV) {
		this.totalEmissionsV = totalEmissionsV;
	}

	private static String convertEmptyToNull(String s) {
		String ret = null;
		if (s != null && s.length() > 0) {
			ret = s;
		}
		return ret;
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

		in.defaultReadObject();
		logger = Logger.getLogger(this.getClass());
	}

	public boolean isExpiredFire() {
		return expiredFire;
	}

	public void setExpiredFire(boolean expiredFire) {
		this.expiredFire = expiredFire;
	}

	public boolean isExceedThresholdQA() {
		return exceedThresholdQA;
	}

	public void setExceedThresholdQA(boolean exceedThresholdQA) {
		this.exceedThresholdQA = exceedThresholdQA;
	}

	public Float getThresholdQA() {
		return thresholdQA;
	}

	public void setThresholdQA(Float thresholdQA) {
		this.thresholdQA = thresholdQA;
	}

	public String getThresholdQAStr() {
		return thresholdQAStr;
	}

	public void setThresholdQAStr(String thresholdQAStr) {
		this.thresholdQAStr = thresholdQAStr;
	}

	public boolean isEuLevel() {
		return euLevel;
	}

	public void setEuLevel(boolean euLevel) {
		this.euLevel = euLevel;
	}

	public boolean isChgAllAnnualAdjRender() {
		return chgAllAnnualAdjRender;
	}

	public boolean isAdjustExists() {
		return annualAdjust != null && annualAdjust.length() > 0;
	}

	public int getNumFireRows() {
		return numFireRows;
	}

	public void setNumFireRows(int numFireRows) {
		this.numFireRows = numFireRows;
	}

	public boolean isFugitivesPossible() {
		return fugitivesPossible;
	}

	public void setFugitivesPossible(boolean fugitivesPossible) {
		this.fugitivesPossible = fugitivesPossible;
	}

	public boolean isStackEmissionsPossible() {
		return stackEmissionsPossible;
	}

	public void setStackEmissionsPossible(boolean stackEmissionsPossible) {
		this.stackEmissionsPossible = stackEmissionsPossible;
	}

	public EmissionsReportPeriod getP() {
		return p;
	}

	public void setP(EmissionsReportPeriod p) {
		this.p = p;
	}

	public boolean isFromServiceCatalog() {
		return fromServiceCatalog;
	}

	public void setFromServiceCatalog(boolean fromServiceCatalog) {
		this.fromServiceCatalog = fromServiceCatalog;
	}

	public boolean isThroughputBasedFactor() {
		boolean ret = false;
		if (this.emissionCalcMethodCd != null) {
			ret = EmissionCalcMethodDef.SCCEmissionsFactor.equalsIgnoreCase(this.emissionCalcMethodCd);
		}
		return ret;
	}

	public final Integer getReportYear() {
		return reportYear;
	}

	public final void setReportYear(Integer reportYear) {
		this.reportYear = reportYear;
	}

	public boolean isResetMethodType() {
		return resetMethodType;
	}

	public void setResetMethodType(boolean resetMethodType) {
		this.resetMethodType = resetMethodType;
	}

	public boolean isEmissionsMethod() {
		boolean ret = false;
		if (this.emissionCalcMethodCd != null) {
			ret = EmissionCalcMethodDef.SCCEmissions.equalsIgnoreCase(this.emissionCalcMethodCd);
		}
		return ret;
	}

	public int getActiveFireRows() {
		return activeFireRows;
	}

	public void setActiveFireRows(int activeFireRows) {
		this.activeFireRows = activeFireRows;
	}

	public String getRememberedFactorNumericValue() {
		return rememberedFactorNumericValue;
	}

	public void setRememberedFactorNumericValue(String rememberedFactorNumericValue) {
		this.rememberedFactorNumericValue = rememberedFactorNumericValue;
	}
}
