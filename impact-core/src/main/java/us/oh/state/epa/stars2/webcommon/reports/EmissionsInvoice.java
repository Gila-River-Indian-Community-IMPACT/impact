package us.oh.state.epa.stars2.webcommon.reports;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.Emissions;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEU;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportPeriod;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsTable;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsTableDetail;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.EuEmission;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCNonChargePollutant;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCPollutant;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.ValidationBase;

public class EmissionsInvoice extends ValidationBase {

	private static final long serialVersionUID = -4654398742777875346L;

	private EmissionsReport report;
	private BigDecimal totalFee = BigDecimal.ZERO;
	public String totalFormattedFee = "";
	private String minFeeForEmissionInventory;
	private EmissionsReportService emissionsReportService;
	private FacilityService facilityService;
	private Facility facility;
	private String emissionsCalulationInfo = null;

	private String maxTotalEmissionsPerPollutant = null;
	private String allowablesNotConfiguredMessage;
	private String allowablesConfiguredZeroMessage;
	private boolean allowablesNotConfiguredInFacilityEu = false;
	private boolean allowablesConfiguredZeroInFacilityEu = false;
	private boolean maxPollutantEmissionsReached = false;
	private BigDecimal sumChargeableTons = BigDecimal.ZERO;
	private BigDecimal pollutantCap = new BigDecimal("4000.0000");
	
	private HashMap<String, EmissionsTable> mapEmissionsTable = new HashMap<String, EmissionsTable>();
	private List<EmissionsTable> emissTableList = new ArrayList<EmissionsTable>();
	private TableSorter emissTableWrapper = new TableSorter();

	private List<EmissionsTableDetail> emissTableDetail = new ArrayList<EmissionsTableDetail>();
	private TableSorter emissTableDetailWrapper = new TableSorter();
	private List<EmissionsTableDetail> etdList = new ArrayList<EmissionsTableDetail>();

	private HashMap<String, Double> billedOnAllowable = new HashMap<String, Double>();

	private HashMap<String, List<SCNonChargePollutant>> nonCharPollutantsFromSC = new HashMap<String, List<SCNonChargePollutant>>();
	private boolean pollutantNonChargeableDueToSets = false;

	private NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();


	public EmissionsInvoice() {
		super();
	}

	public final String getReportTotalEmissionsFee() {

		try {

			report.getEmissionTotals();
			BigDecimal feeForFirstHalf = BigDecimal.ZERO;
			BigDecimal feeForSecondHalf = BigDecimal.ZERO;
			minFeeForEmissionInventory = null;
			int maxAncestors = 0;

			ReportTemplates rt = emissionsReportService.retrieveSCEmissionsReports(report.getReportYear(),
					report.getContentTypeCd(), facility.getFacilityId());
			pollutantCap = new BigDecimal(rt.getSc().getPollutantCap());

			SCNonChargePollutant nonCharPollutants[] = rt.getSc().getNcPollutants();

			List<EmissionsReportPeriod> erp;

			Fee fee1 = rt.getSc().getFeeFirstHalf();
			feeForFirstHalf = new BigDecimal(fee1.getAmount());
			Fee fee2 = rt.getSc().getFeeSecondHalf();
			feeForSecondHalf = new BigDecimal(fee2.getAmount());

			int retVal = feeForFirstHalf.compareTo(feeForSecondHalf);
			if (retVal > 0 || retVal < 0) {
				emissionsCalulationInfo = "Per Ton First Half Year Fee: "
						+ defaultFormat.format(feeForFirstHalf.setScale(2, RoundingMode.HALF_UP))
						+ " Per Ton Second Half Year Fee: "
						+ defaultFormat.format(feeForSecondHalf.setScale(2, RoundingMode.HALF_UP));
			} else {
				emissionsCalulationInfo = "Per Ton Fee for the year: "
						+ defaultFormat.format(feeForFirstHalf.setScale(2, RoundingMode.HALF_UP));
			}

			List<EmissionUnit> facilityEuList = facility.getEmissionUnits();

			List<EmissionsReportEU> reportEuList = report.getEus();

			// Build a map of all SCNonChargePollutants that affect each
			// pollutant (if any).
			for (int j = 0; j < nonCharPollutants.length; j++) {
				List<SCNonChargePollutant> scncList = nonCharPollutantsFromSC
						.get(nonCharPollutants[j].getPollutantCd());
				if (scncList == null) {
					scncList = new ArrayList<SCNonChargePollutant>();
					nonCharPollutantsFromSC.put(nonCharPollutants[j].getPollutantCd(), scncList);
				}
				scncList.add(nonCharPollutants[j]);
			}

			maxPollutantEmissionsReached = false;

			for (EmissionUnit facilityEU : facilityEuList) {

				// Loop through the pollutants in the Service Catalog:
				// Build a map of pollutants that are billed based on
				// Allowable using Facility EU EmuId as the key.
				buildMapOfPollutantsBilledOnAllowable(rt, facilityEU);

				for (EmissionsReportEU emissionReportEu : reportEuList) {

					if ((emissionReportEu.getEmuId().equals(facilityEU.getEmuId()))
							&& emissionReportEu.isInvoicingNeeded()) {

						erp = emissionReportEu.getPeriods();
						Integer numPeriods = erp.size();

						for (EmissionsReportPeriod erps : erp) {

							int firstHalfPercent = erps.getFirstHalfHrsOfOperationPct();
							int secondHalfPercent = erps.getSecondHalfHrsOfOperationPct();

							Emissions[] emiss = emissionsReportService.retrieveEmissions(erps);

							// Build the list EmissionsTables where each et acts
							// as an accumulator of emissions across EU processes.
							for (int i = 0; i < emiss.length; i++) {

								emiss[i].setFirstHalfPercent(firstHalfPercent);
								emiss[i].setSecondHalfPercent(secondHalfPercent);
								emiss[i].setEmuID(emissionReportEu.getEmuId());
								emiss[i].setEpaEmuID(emissionReportEu.getEpaEmuId());
								emiss[i].setProcessID(erps.getTreeLabel());
								EmissionProcess ep = facilityEU.getEmissionProcess(erps.getTreeLabel());
								if (ep.getSccCode() != null && ep.getSccCode().getSccId() != null) {
									emiss[i].setSccId(ep.getSccCode().getSccId());
								}

								EmissionsTable et = mapEmissionsTable.get(emiss[i].getPollutantCd());
								if (et == null) {
									et = new EmissionsTable();
									et.setPollutantCd(emiss[i].getPollutantCd());
									et.setPollutantDesc(PollutantDef.getData().getItems()
											.getItem(emiss[i].getPollutantCd()).getDescription());
									et.setMySCNonChargePollutantList(
											nonCharPollutantsFromSC.get(emiss[i].getPollutantCd()));
									mapEmissionsTable.put(et.getPollutantCd(), et);
								}
								et.addEmission(emiss[i]);
								et.setReportable(true);
								
								BigDecimal totalAllowable = getTotalAllowable(emiss[i].getPollutantCd(), emissionReportEu.getEmuId(), numPeriods);
								if (totalAllowable != null && totalAllowable.compareTo(BigDecimal.ZERO) > 0) {
									emiss[i].setTotalAllowable(totalAllowable);
									et.setAllowableIsUsedToCalculate(true);
								}
								
							}

						}
					}
				}
			}

			// Now figure out if a pollutant belongs in a set so we can hand
			// non-chargeable emissions down from parent to children and/or up
			// from children to parents within the set.
			for (EmissionsTable etbl : mapEmissionsTable.values()) {

				etbl.setSeen(false);
				buildMapOfNonChargeableDueToSets(etbl);
				
				if (etbl.getNumGens() > maxAncestors) {
					maxAncestors = etbl.getNumGens();
				}

			}

			int crunchAncestors = 0;
			while (crunchAncestors <= maxAncestors) {
				for (EmissionsTable etbl : mapEmissionsTable.values()) {
					if (etbl.getNumGens() == crunchAncestors && !etbl.isSeen()) {
						InitializeEmissionsTables(etbl);
						removeUnbillableChildren(etbl);
					}
				}
				crunchAncestors++;
			}

			calculateFees(pollutantCap, feeForFirstHalf, feeForSecondHalf);
			setTotalTonsAndFee(pollutantCap);
			
			for (EmissionsTable etbl : mapEmissionsTable.values()) {
				cleanEmissionsTable(etbl);
			}
			sumChargeableTons = sumChargeableTons.setScale(5, BigDecimal.ROUND_HALF_UP);
			
			setMessages(reportEuList, rt, pollutantCap.doubleValue());

			setEmissTable(new ArrayList<EmissionsTable>(mapEmissionsTable.values()));
			Collections.sort(emissTableList);
			emissTableWrapper.setWrappedData(emissTableList);

			setEmissTableDetail(etdList);
			Collections.sort(etdList);
			emissTableDetailWrapper.setWrappedData(emissTableDetail);

		} catch (Exception e) {
			String errMsg = "Invoice Generation Error; Please Contact System Admin";
			DisplayUtil.displayError(errMsg);
			logger.error(errMsg, e);
			return null;
		}

		return "dialog:invoiceDoc";
	}
	
	private void InitializeEmissionsTables(EmissionsTable emTable) {
		
		BigDecimal nonChargeableStackAmountFromParent = emTable.getNonChargeableStackAmountFromParent();
		BigDecimal nonChargeableFugitiveAmountFromParent = emTable.getNonChargeableFugitiveAmountFromParent();
		
		BigDecimal nonChargeableFirstHalfFromParent = emTable.getNonChargeableFirstHalfFromParent();
		BigDecimal nonChargeableSecondHalfFromParent = emTable.getNonChargeableSecondHalfFromParent();

		for (Emissions emiss : emTable.getEmissions()) {

			int firstHalfPercent = emiss.getFirstHalfPercent();
			int secondHalfPercent = emiss.getSecondHalfPercent();

			String currentUnits = emiss.getEmissionsUnitNumerator();

			BigDecimal currentStackEmissions = BigDecimal.valueOf(parseDoubleOrNull(emiss.getStackEmissions()));
			currentStackEmissions = returnValueInTons(currentUnits, currentStackEmissions);
			emTable.setStackEmissions(emTable.getStackEmissions().add(currentStackEmissions));
			emTable.setFirstHalfStackEmissions(currentStackEmissions.multiply((BigDecimal
					.valueOf(((double) firstHalfPercent)).divide(BigDecimal.valueOf(100d), BigDecimal.ROUND_HALF_UP))));
			emTable.setSecondHalfStackEmissions(
					currentStackEmissions.multiply((BigDecimal.valueOf(((double) secondHalfPercent))
							.divide(BigDecimal.valueOf(100d), BigDecimal.ROUND_HALF_UP))));
			BigDecimal currentChargeableStackEmissions = currentStackEmissions;

			BigDecimal currentFugitiveEmissions = BigDecimal.valueOf(parseDoubleOrNull(emiss.getFugitiveEmissions()));
			currentFugitiveEmissions = returnValueInTons(currentUnits, currentFugitiveEmissions);
			emTable.setFugitiveEmissions(emTable.getFugitiveEmissions().add(currentFugitiveEmissions));
			emTable.setFirstHalfFugitiveEmissions(currentFugitiveEmissions.multiply((BigDecimal
					.valueOf(((double) firstHalfPercent)).divide(BigDecimal.valueOf(100d), BigDecimal.ROUND_HALF_UP))));
			emTable.setSecondHalfFugitiveEmissions(
					currentFugitiveEmissions.multiply((BigDecimal.valueOf(((double) secondHalfPercent))
							.divide(BigDecimal.valueOf(100d), BigDecimal.ROUND_HALF_UP))));
			BigDecimal currentChargeableFugitiveEmissions = currentFugitiveEmissions;

			BigDecimal currentActualTotalTons = currentStackEmissions.add(currentFugitiveEmissions);
			emTable.setActualTotalTons(emTable.getActualTotalTons().add(currentActualTotalTons));

			BigDecimal currentFirstHalfActualTotalTons = BigDecimal.ZERO;
			BigDecimal currentSecondHalfActualTotalTons = BigDecimal.ZERO;
			if (currentActualTotalTons.compareTo(BigDecimal.ZERO) > 0) {
				currentFirstHalfActualTotalTons = currentActualTotalTons
						.multiply((BigDecimal.valueOf(((double) firstHalfPercent)).divide(BigDecimal.valueOf(100d),
								BigDecimal.ROUND_HALF_UP)));
				currentSecondHalfActualTotalTons = currentActualTotalTons
						.multiply((BigDecimal.valueOf(((double) secondHalfPercent)).divide(BigDecimal.valueOf(100d),
								BigDecimal.ROUND_HALF_UP)));
			}

			BigDecimal currentFirstHalfAllowableTotalTons = BigDecimal.valueOf(0d);
			BigDecimal currentSecondHalfAllowableTotalTons = BigDecimal.valueOf(0d);

			BigDecimal currentFirstHalfChargeableTotalTons = currentFirstHalfActualTotalTons;
			BigDecimal currentSecondHalfChargeableTotalTons = currentSecondHalfActualTotalTons;

			BigDecimal totalAllowable = null;
			if (emiss.getPollutantCd().startsWith("PM") && billedOnAllowable.get("PM" + emiss.getEmuID()) != null) {
				totalAllowable = new BigDecimal(billedOnAllowable.get("PM" + emiss.getEmuID()));
			} else if (billedOnAllowable.get(emiss.getPollutantCd() + emiss.getEmuID()) != null) {
				totalAllowable = new BigDecimal(billedOnAllowable.get(emiss.getPollutantCd() + emiss.getEmuID()));
			}

			if (totalAllowable != null) {
				currentFirstHalfAllowableTotalTons = totalAllowable.multiply(BigDecimal.valueOf(((double) firstHalfPercent / 100d)));
				currentSecondHalfAllowableTotalTons = totalAllowable.multiply(BigDecimal.valueOf(((double) secondHalfPercent / 100d)));
				currentFirstHalfChargeableTotalTons = currentFirstHalfAllowableTotalTons;
				currentSecondHalfChargeableTotalTons = currentSecondHalfAllowableTotalTons;
				emTable.setFirstHalfAllowableTotalTons(
						emTable.getFirstHalfAllowableTotalTons().add(currentFirstHalfAllowableTotalTons));
				emTable.setSecondHalfAllowableTotalTons(
						emTable.getSecondHalfAllowableTotalTons().add(currentSecondHalfAllowableTotalTons));
				emTable.setAllowableTotalTons(emTable.getAllowableTotalTons().add(currentFirstHalfAllowableTotalTons)
						.add(currentSecondHalfAllowableTotalTons));				
				emTable.setAllowableIsUsedToCalculate(true);
				emiss.setTotalAllowable(totalAllowable);
			}
			
			emTable.setFirstHalfActualTotalTons(
					emTable.getFirstHalfActualTotalTons().add(currentFirstHalfActualTotalTons));
			emTable.setSecondHalfActualTotalTons(
					emTable.getSecondHalfActualTotalTons().add(currentSecondHalfActualTotalTons));
			
			// Apply parent non-chargeables first.
			List<SCNonChargePollutant> ncPollList = emTable.getParentSCNonChargePollutantList();
			if (ncPollList != null) {
				for (SCNonChargePollutant ncPoll : ncPollList) {
					if (ncPoll.isFugitiveOnly()) {
						// All fugitive emissions for this pollutant are
						// non-chargeable for any SCC.
						if (ncPoll.getSccCd() == null || ncPoll.getSccCd().getSccId() == null
								|| ncPoll.getSccCd().getSccId().equals(SccCode.DUMMY_SCC_ID)) {
							currentFirstHalfChargeableTotalTons = currentFirstHalfChargeableTotalTons.subtract(
									currentFugitiveEmissions.multiply(BigDecimal.valueOf(((double) firstHalfPercent))
											.divide(BigDecimal.valueOf(100d), BigDecimal.ROUND_HALF_UP)));
							currentSecondHalfChargeableTotalTons = currentSecondHalfChargeableTotalTons.subtract(
									currentFugitiveEmissions.multiply(BigDecimal.valueOf(((double) secondHalfPercent))
											.divide(BigDecimal.valueOf(100d), BigDecimal.ROUND_HALF_UP)));
							currentChargeableFugitiveEmissions = BigDecimal.ZERO;
						} else {
							// Else process must have an SCC that matches the
							// exclusion.
							if (emiss.getSccId() != null && ncPoll.getSccCd() != null
									&& emiss.getSccId().equals(ncPoll.getSccCd().getSccId())) {
								currentFirstHalfChargeableTotalTons = currentFirstHalfChargeableTotalTons
										.subtract(currentFugitiveEmissions
												.multiply(BigDecimal.valueOf(((double) firstHalfPercent))
														.divide(BigDecimal.valueOf(100d), BigDecimal.ROUND_HALF_UP)));
								currentSecondHalfChargeableTotalTons = currentSecondHalfChargeableTotalTons
										.subtract(currentFugitiveEmissions
												.multiply(BigDecimal.valueOf(((double) secondHalfPercent))
														.divide(BigDecimal.valueOf(100d), BigDecimal.ROUND_HALF_UP)));
								currentChargeableFugitiveEmissions = BigDecimal.ZERO;
							}
						}
					} else {
						// All emissions for this pollutant are non-chargeable
						// for any SCC.
						if (ncPoll.getSccCd() == null || ncPoll.getSccCd().getSccId() == null
								|| ncPoll.getSccCd().getSccId().equals(SccCode.DUMMY_SCC_ID)) {
							currentFirstHalfChargeableTotalTons = BigDecimal.ZERO;
							currentSecondHalfChargeableTotalTons = BigDecimal.ZERO;
							currentChargeableFugitiveEmissions = BigDecimal.ZERO;
							currentChargeableStackEmissions = BigDecimal.ZERO;
						} else {
							// Else process must have an SCC that matches the
							// exclusion.
							if (emiss.getSccId() != null && ncPoll.getSccCd() != null
									&& emiss.getSccId().equals(ncPoll.getSccCd().getSccId())) {
								currentFirstHalfChargeableTotalTons = BigDecimal.ZERO;
								currentSecondHalfChargeableTotalTons = BigDecimal.ZERO;
								currentChargeableFugitiveEmissions = BigDecimal.ZERO;
								currentChargeableStackEmissions = BigDecimal.ZERO;
							}
						}
					}
				}
				nonChargeableStackAmountFromParent = 
						nonChargeableStackAmountFromParent.add(currentStackEmissions.subtract(currentChargeableStackEmissions));
				nonChargeableFugitiveAmountFromParent = 
						nonChargeableFugitiveAmountFromParent.add(currentFugitiveEmissions.subtract(currentChargeableFugitiveEmissions));
				
				nonChargeableFirstHalfFromParent = nonChargeableFirstHalfFromParent.add(currentFirstHalfActualTotalTons.subtract(currentFirstHalfChargeableTotalTons));
				nonChargeableSecondHalfFromParent = nonChargeableSecondHalfFromParent.add(currentSecondHalfActualTotalTons.subtract(currentSecondHalfChargeableTotalTons));
				
			}

			ncPollList = emTable.getMySCNonChargePollutantList();
			if (ncPollList != null) {
				for (SCNonChargePollutant ncPoll : ncPollList) {
					if (ncPoll.isFugitiveOnly()) {
						// All fugitive emissions for this pollutant are
						// non-chargeable for any SCC.
						if (ncPoll.getSccCd() == null || ncPoll.getSccCd().getSccId() == null
								|| ncPoll.getSccCd().getSccId().equals(SccCode.DUMMY_SCC_ID)) {
							currentFirstHalfChargeableTotalTons = currentFirstHalfChargeableTotalTons.subtract(
									currentFugitiveEmissions.multiply(BigDecimal.valueOf(((double) firstHalfPercent))
											.divide(BigDecimal.valueOf(100d), BigDecimal.ROUND_HALF_UP)));
							currentSecondHalfChargeableTotalTons = currentSecondHalfChargeableTotalTons.subtract(
									currentFugitiveEmissions.multiply(BigDecimal.valueOf(((double) secondHalfPercent))
											.divide(BigDecimal.valueOf(100d), BigDecimal.ROUND_HALF_UP)));
							currentChargeableFugitiveEmissions = BigDecimal.ZERO;
							emTable.setPollutantNonChargeableInSC(true);
						} else {
							// Else process must have an SCC that matches the
							// exclusion.
							if (emiss.getSccId() != null && ncPoll.getSccCd() != null
									&& emiss.getSccId().equals(ncPoll.getSccCd().getSccId())) {
								currentFirstHalfChargeableTotalTons = currentFirstHalfChargeableTotalTons
										.subtract(currentFugitiveEmissions
												.multiply(BigDecimal.valueOf(((double) firstHalfPercent))
														.divide(BigDecimal.valueOf(100d), BigDecimal.ROUND_HALF_UP)));
								currentSecondHalfChargeableTotalTons = currentSecondHalfChargeableTotalTons
										.subtract(currentFugitiveEmissions
												.multiply(BigDecimal.valueOf(((double) secondHalfPercent))
														.divide(BigDecimal.valueOf(100d), BigDecimal.ROUND_HALF_UP)));
								currentChargeableFugitiveEmissions = BigDecimal.ZERO;
								emTable.setPollutantNonChargeableInSC(true);
							}
						}
					} else {
						// All emissions for this pollutant are non-chargeable
						// for any SCC.
						if (ncPoll.getSccCd() == null || ncPoll.getSccCd().getSccId() == null
								|| ncPoll.getSccCd().getSccId().equals(SccCode.DUMMY_SCC_ID)) {
							currentFirstHalfChargeableTotalTons = BigDecimal.ZERO;
							currentSecondHalfChargeableTotalTons = BigDecimal.ZERO;
							emTable.setPollutantNonChargeableInSC(true);
							currentChargeableFugitiveEmissions = BigDecimal.ZERO;
							currentChargeableStackEmissions = BigDecimal.ZERO;
						} else {
							// Else process must have an SCC that matches the
							// exclusion.
							if (emiss.getSccId() != null && ncPoll.getSccCd() != null
									&& emiss.getSccId().equals(ncPoll.getSccCd().getSccId())) {
								currentFirstHalfChargeableTotalTons = BigDecimal.ZERO;
								currentSecondHalfChargeableTotalTons = BigDecimal.ZERO;
								currentChargeableFugitiveEmissions = BigDecimal.ZERO;
								currentChargeableStackEmissions = BigDecimal.ZERO;
								emTable.setPollutantNonChargeableInSC(true);
							}
						}
					}
				}
			}

			emTable.setChargeableFugitiveEmissions(
					emTable.getChargeableFugitiveEmissions().add(currentChargeableFugitiveEmissions));
			emTable.setChargeableStackEmissions(
					emTable.getChargeableStackEmissions().add(currentChargeableStackEmissions));

			emTable.setFirstHalfChargeableTotalTons(
					emTable.getFirstHalfChargeableTotalTons().add(currentFirstHalfChargeableTotalTons));
			emTable.setSecondHalfChargeableTotalTons(
					emTable.getSecondHalfChargeableTotalTons().add(currentSecondHalfChargeableTotalTons));
			emTable.setChargeableTotalTons(emTable.getChargeableTotalTons().add(currentFirstHalfChargeableTotalTons)
					.add(currentSecondHalfChargeableTotalTons));

			emTable.setNonChargeableStackAmountFromParent(nonChargeableStackAmountFromParent);
			emTable.setNonChargeableFugitiveAmountFromParent(nonChargeableFugitiveAmountFromParent);
			emTable.setNonChargeableFirstHalfFromParent(nonChargeableFirstHalfFromParent);
			emTable.setNonChargeableSecondHalfFromParent(nonChargeableSecondHalfFromParent);

			populateEmissionsTableDetail(emiss, currentFirstHalfActualTotalTons.doubleValue(),
					currentSecondHalfActualTotalTons.doubleValue());
		}

		for (EmissionsTable child : emTable.getChildEmissions().values()) {

			child.addParentSCNonChargePollutantList(emTable.getMySCNonChargePollutantList());
			child.setNonChargeableStackAmountFromParent(nonChargeableStackAmountFromParent);
			child.setNonChargeableFugitiveAmountFromParent(nonChargeableFugitiveAmountFromParent);
			child.setNonChargeableFirstHalfFromParent(nonChargeableFirstHalfFromParent);
			child.setNonChargeableSecondHalfFromParent(nonChargeableSecondHalfFromParent);
			child.setPollutantNonChargeableDueToSets(true);
			child.setParentDesc(emTable.getPollutantDesc());
			InitializeEmissionsTables(child);
			child.setSeen(true);

		}

	}
	
	private void removeUnbillableChildren(EmissionsTable parent) {
		
		if (parent.getChildEmissions() != null && !parent.getChildEmissions().isEmpty()) {

			for (EmissionsTable child : parent.getChildEmissions().values()) {

				if (child.getChildEmissions() != null && !child.getChildEmissions().isEmpty()) {
					removeUnbillableChildren(child);
				}

				BigDecimal parentFirstHalfChargeableTotalTons = parent.getFirstHalfChargeableTotalTons();
				BigDecimal parentSecondHalfChargeableTotalTons = parent.getSecondHalfChargeableTotalTons();
				BigDecimal parentChargeableTotalTons = parent.getChargeableTotalTons();

				BigDecimal childFrstHalfActualTotalTons = child.getFirstHalfActualTotalTons();
				BigDecimal childSecondHalfActualTotalTons = child.getSecondHalfActualTotalTons();
				BigDecimal childActualTotalTons = child.getChargeableTotalTons();

				BigDecimal childFirstHalfChargeableTotalTons = child.getFirstHalfChargeableTotalTons();
				BigDecimal childSecondHalfChargeableTotalTons = child.getSecondHalfChargeableTotalTons();
				BigDecimal childChargeableTotalTons = child.getChargeableTotalTons();

				// Pass any non-chargeables caused by child's ScNonChargePollutantList that are not
				// already accounted for in the parent back up the graph.
				if (child.isPollutantNonChargeableInSC()) {
					
					BigDecimal childFirstHalfNonChargeableTotalTons = childFrstHalfActualTotalTons
							.subtract(childFirstHalfChargeableTotalTons);
					BigDecimal childSecondHalfNonChargeableTotalTons = childSecondHalfActualTotalTons
							.subtract(childSecondHalfChargeableTotalTons);
					BigDecimal childNonChargeableTotalTons = childActualTotalTons.subtract(childChargeableTotalTons);
					
					parentFirstHalfChargeableTotalTons = parentFirstHalfChargeableTotalTons
							.subtract(childFirstHalfNonChargeableTotalTons).add(child.getNonChargeableFirstHalfFromParent());
					if (parentFirstHalfChargeableTotalTons.compareTo(BigDecimal.ZERO) < 0) {
						parentFirstHalfChargeableTotalTons = BigDecimal.ZERO;
					}
					parentSecondHalfChargeableTotalTons = parentSecondHalfChargeableTotalTons
							.subtract(childSecondHalfNonChargeableTotalTons).add(child.getNonChargeableSecondHalfFromParent());
					if (parentSecondHalfChargeableTotalTons.compareTo(BigDecimal.ZERO) < 0) {
						parentSecondHalfChargeableTotalTons = BigDecimal.ZERO;
					}
					parentChargeableTotalTons = parentChargeableTotalTons.subtract(childNonChargeableTotalTons);
					if (parentChargeableTotalTons.compareTo(BigDecimal.ZERO) < 0) {
						parentChargeableTotalTons = BigDecimal.ZERO;
					}

					BigDecimal parentChargeableFugitiveEmissions = parent.getChargeableFugitiveEmissions();
					BigDecimal parentChargeableStackEmissions = parent.getChargeableStackEmissions();

					BigDecimal childNonChargeableFugitiveEmissions = child.getFugitiveEmissions()
							.subtract(child.getChargeableFugitiveEmissions());
					BigDecimal childNonChargeableStackEmisions = child.getStackEmissions()
							.subtract(child.getChargeableStackEmissions());

					parentChargeableFugitiveEmissions = parentChargeableFugitiveEmissions
							.subtract(childNonChargeableFugitiveEmissions)
							.add(child.getNonChargeableFugitiveAmountFromParent());
					if (parentChargeableFugitiveEmissions.compareTo(BigDecimal.ZERO) < 0) {
						parentChargeableFugitiveEmissions = BigDecimal.ZERO;
					}
					parentChargeableStackEmissions = parentChargeableStackEmissions
							.subtract(childNonChargeableStackEmisions)
							.add(child.getNonChargeableStackAmountFromParent());
					if (parentChargeableStackEmissions.compareTo(BigDecimal.ZERO) < 0) {
						parentChargeableStackEmissions = BigDecimal.ZERO;
					}
					parentChargeableTotalTons = parentChargeableFugitiveEmissions.add(parentChargeableStackEmissions);

					parent.setChargeableFugitiveEmissions(parentChargeableFugitiveEmissions);
					parent.setChargeableStackEmissions(parentChargeableStackEmissions);

					parent.setPollutantNonChargeableInSC(true);

				}
				
				parent.setFirstHalfChargeableTotalTons(parentFirstHalfChargeableTotalTons);
				parent.setSecondHalfChargeableTotalTons(parentSecondHalfChargeableTotalTons);
				parent.setChargeableTotalTons(parentChargeableTotalTons);

			}
		}
		
		return;
	}

	private void calculateFees(BigDecimal pollutantCap, BigDecimal feeForFirstHalf, BigDecimal feeForSecondHalf) {

		for (EmissionsTable table : mapEmissionsTable.values()) {

			BigDecimal chargeableTotalTons = BigDecimal.ZERO;
			BigDecimal origTotalFeePerPollutant = BigDecimal.ZERO;
			BigDecimal totalFeePerPollutant = BigDecimal.ZERO;
			BigDecimal firstHalfFee = BigDecimal.ZERO;
			BigDecimal secondHalfFee = BigDecimal.ZERO;

			origTotalFeePerPollutant = table.getTotalFeePerPollutant();

			BigDecimal totalFirstHalfChargeableTotalTons = table.getFirstHalfChargeableTotalTons();
			BigDecimal totalSecondHalfChargeableTotalTons = table.getSecondHalfChargeableTotalTons();

			BigDecimal totalFirstHalfFee = table.getFirstHalfFee();
			BigDecimal totalSecondHalfFee = table.getSecondHalfFee();

			if (table.isPollutantNonChargeableDueToSets()) {

				totalFeePerPollutant = BigDecimal.ZERO;
				origTotalFeePerPollutant = BigDecimal.ZERO;
				totalFeePerPollutant = BigDecimal.ZERO;
				chargeableTotalTons = BigDecimal.ZERO;

			} else {

				if (totalFirstHalfChargeableTotalTons.compareTo(pollutantCap) > 0) {

					maxPollutantEmissionsReached = true;
					firstHalfFee = pollutantCap.multiply(feeForFirstHalf);
					secondHalfFee = BigDecimal.ZERO;
					chargeableTotalTons = pollutantCap;
					totalFeePerPollutant = firstHalfFee.add(secondHalfFee);

				} else if (totalFirstHalfChargeableTotalTons.compareTo(pollutantCap) == 0) {

					maxPollutantEmissionsReached = true;
					firstHalfFee = pollutantCap.multiply(feeForFirstHalf);
					secondHalfFee = BigDecimal.ZERO;
					chargeableTotalTons = pollutantCap;
					totalFeePerPollutant = firstHalfFee.add(secondHalfFee);

				} else if (totalFirstHalfChargeableTotalTons.compareTo(pollutantCap) < 0) {

					firstHalfFee = totalFirstHalfChargeableTotalTons.multiply(feeForFirstHalf);

					if (totalFirstHalfChargeableTotalTons.add(totalSecondHalfChargeableTotalTons)
							.compareTo(pollutantCap) <= 0) {

						secondHalfFee = totalSecondHalfChargeableTotalTons.multiply(feeForSecondHalf);
						chargeableTotalTons = totalFirstHalfChargeableTotalTons.add(totalSecondHalfChargeableTotalTons);
						totalFeePerPollutant = firstHalfFee.add(secondHalfFee);

					} else if (totalFirstHalfChargeableTotalTons.add(totalSecondHalfChargeableTotalTons)
							.compareTo(pollutantCap) > 0) {

						secondHalfFee = ((pollutantCap.subtract(totalFirstHalfChargeableTotalTons))
								.multiply(feeForSecondHalf));
						chargeableTotalTons = pollutantCap;
						totalFeePerPollutant = firstHalfFee.add(secondHalfFee);
						maxPollutantEmissionsReached = true;

					}

				}

			}
			
			table.setFirstHalfFee(firstHalfFee.add(totalFirstHalfFee));
			table.setSecondHalfFee(secondHalfFee.add(totalSecondHalfFee));

			totalFeePerPollutant = origTotalFeePerPollutant.add(totalFeePerPollutant);
			table.setTotalFeePerPollutant(totalFeePerPollutant);
			table.setChargeableTotalTons(chargeableTotalTons);

			sumChargeableTons = sumChargeableTons.add(chargeableTotalTons);

		}

	}

	private void populateEmissionsTableDetail(Emissions emiss, Double firstHalfActualTotalTons,
			Double secondHalfActualTotalTons) {

		EmissionsTableDetail etd = new EmissionsTableDetail();
		etd.setPollutantCd(emiss.getPollutantCd());
		etd.setPollutantDesc(PollutantDef.getData().getItems().getItem(emiss.getPollutantCd()).getDescription());
		etd.setProcessID(emiss.getProcessID());
		etd.setEmuID(emiss.getEmuID());
		etd.setEpaEmuID(emiss.getEpaEmuID());
		etd.setFirstHalfActualTotalTons(customFormat(BigDecimal.valueOf(firstHalfActualTotalTons).doubleValue()));
		etd.setSecondHalfActualTotalTons(customFormat(BigDecimal.valueOf(secondHalfActualTotalTons).doubleValue()));
		etd.setActualTotalTons(
				customFormat(BigDecimal.valueOf(firstHalfActualTotalTons + secondHalfActualTotalTons).doubleValue()));

		String allowableTotalTons = "";
		if (emiss.getTotalAllowable().compareTo(BigDecimal.ZERO) > 0) {
			allowableTotalTons = (emiss.getTotalAllowable().setScale(5, BigDecimal.ROUND_HALF_UP)).toPlainString();
		}
		etd.setAllowableTotalTons(allowableTotalTons);

		etdList.add(etd);
	}

	private void setTotalTonsAndFee(BigDecimal pollutantCap2) {

		for (EmissionsTable etl : mapEmissionsTable.values()) {

			BigDecimal chargeableTotalTons = etl.getChargeableTotalTons();
			BigDecimal totalFeePerPollutant = etl.getTotalFeePerPollutant();

			String prefix = "";

			if (etl.getPollutantCd().startsWith("PM") && totalFeePerPollutant.compareTo(BigDecimal.ZERO) == 0) {
				etl.setPollutantNonChargeableDueToSets(true);
			}

			prefix = "";
			if (etl.isAllowableIsUsedToCalculate()) {
				prefix = "(1)";
			}
			if (etl.isPollutantNonChargeableInSC()) {
				if (prefix.length() > 0) {
					prefix = prefix + ", ";
				}
				prefix = prefix + "(2)";
			}
			if (etl.isPollutantNonChargeableDueToSets()) {
				if (prefix.length() > 0) {
					prefix = prefix + ", ";
				}
				prefix = prefix + "(3)";
			}
			if (chargeableTotalTons == pollutantCap2) {
				if (prefix.length() > 0) {
					prefix = prefix + ", ";
				}
				prefix = prefix + "(4)";
				maxPollutantEmissionsReached = true;
			}
			if (prefix.length() > 0) {
				prefix = prefix + " ";
			}

			if (etl.isAllowableIsUsedToCalculate() || etl.isPollutantNonChargeableInSC()
					|| etl.isPollutantNonChargeableDueToSets()) {
				etl.setChargeableTotalTonsStr(prefix + customFormat(chargeableTotalTons.setScale(5, RoundingMode.HALF_UP).doubleValue()));
			} else if (chargeableTotalTons != pollutantCap2) {
				etl.setChargeableTotalTonsStr(prefix + customFormat(chargeableTotalTons.setScale(5, RoundingMode.HALF_UP).doubleValue()));
			} else if (chargeableTotalTons == pollutantCap2) {
				etl.setChargeableTotalTonsStr(prefix + customFormat(pollutantCap2.setScale(5, RoundingMode.HALF_UP).doubleValue()));
			}

			etl.setTotalFeePerPollutant(totalFeePerPollutant.setScale(2, RoundingMode.HALF_UP));
		}

	}
	
	private void cleanEmissionsTable(EmissionsTable table) {
		
		BigDecimal firstHalfActualTotalTons = table.getFirstHalfActualTotalTons().setScale(5, BigDecimal.ROUND_HALF_UP);
		table.setFirstHalfActualTotalTons(firstHalfActualTotalTons);
		
		BigDecimal secondHalfActualTotalTons = table.getSecondHalfActualTotalTons().setScale(5, BigDecimal.ROUND_HALF_UP);
		table.setSecondHalfActualTotalTons(secondHalfActualTotalTons);
		
		BigDecimal actualTotalTons = table.getActualTotalTons().setScale(5, BigDecimal.ROUND_HALF_UP);
		table.setActualTotalTons(actualTotalTons);
		
		BigDecimal chargeableTotalTons = table.getChargeableTotalTons().setScale(5, BigDecimal.ROUND_HALF_UP);
		table.setChargeableTotalTons(chargeableTotalTons);
		
	}

	private void setMessages(List<EmissionsReportEU> reportEuList, ReportTemplates rt, double pollutantCap) {
		
		HashMap<String, String> pollutantEuWithNoAllowableList = new HashMap<String, String>();
		HashMap<String, String> pollutantEuWithZeroAllowableList = new HashMap<String, String>();
		this.allowablesNotConfiguredInFacilityEu = false;
		this.allowablesConfiguredZeroInFacilityEu = false;
		List<EmissionUnit> newEus = facility.getEmissionUnits();
		SCPollutant pollutants[] = rt.getSc().getPollutants();

		// Alert Message
		for (EmissionUnit emissionUnit : newEus) {
			for (EmissionsReportEU emissionReportEu : reportEuList) {
				if (emissionReportEu.getEmuId().equals(emissionUnit.getEmuId())
						&& emissionReportEu.isInvoicingNeeded()) {

					if (emissionUnit.getEuInitStartupDate() != null) {

						Calendar cal = Calendar.getInstance();
						cal.setTime(emissionUnit.getEuInitStartupDate());
						if (cal.get(Calendar.YEAR) <= report.getReportYear()) {
							List<EuEmission> permittedEmissions2 = emissionUnit.getEuEmissions();

							// Loop through pollutants in the ServiceCatalog:
							// If any pollutant is billed based on Allowable
							// but there is no entry for it in the Facility
							// Detail EU Information page, add the pollutant
							// to the list.
							if (permittedEmissions2.size() == 0) {
								for (int i = 0; i < pollutants.length; i++) {
									if (pollutants[i].isBilledBasedOnPermitted()) {

										pollutantEuWithNoAllowableList.put(
												pollutants[i].getPollutantCd() + "-" + emissionUnit.getEpaEmuId(),
												null);
										this.allowablesNotConfiguredInFacilityEu = true;
									}
								}

							} else {

								for (int i = 0; i < pollutants.length; i++) {
									if (pollutants[i].isBilledBasedOnPermitted()) {
										boolean isInPermittedEmissions = false;
										for (EuEmission euemi : permittedEmissions2) {

											if (pollutants[i].getPollutantCd()
													.equalsIgnoreCase(euemi.getPollutantCd())) {
												isInPermittedEmissions = true;
											}
										}
										if (!isInPermittedEmissions) {

											pollutantEuWithNoAllowableList.put(
													pollutants[i].getPollutantCd() + "-" + emissionUnit.getEpaEmuId(),
													null);
											this.allowablesNotConfiguredInFacilityEu = true;

										}
									}
								}

								Double totalAllowable = 0d;
								for (int i = 0; i < pollutants.length; i++) {
									if (pollutants[i].isBilledBasedOnPermitted()) {
										boolean isZeroInPermittedEmissions = false;
										for (EuEmission euemi : permittedEmissions2) {

											if (pollutants[i].getPollutantCd()
													.equalsIgnoreCase(euemi.getPollutantCd())) {
												if (null != euemi.getAllowableEmissionsTonsYear()) {
													totalAllowable = euemi.getAllowableEmissionsTonsYear()
															.doubleValue();
													if (totalAllowable <= 0d) {
														isZeroInPermittedEmissions = true;
													}
												}
											}
										}
										if (isZeroInPermittedEmissions) {

											pollutantEuWithZeroAllowableList.put(
													pollutants[i].getPollutantCd() + "-" + emissionUnit.getEpaEmuId(),
													null);
											this.allowablesConfiguredZeroInFacilityEu = true;

										}
									}
								}
							}
						}
					}
				}
			}
		}

		allowablesNotConfiguredMessage = "";
		if (this.allowablesNotConfiguredInFacilityEu) {
			if (pollutantEuWithNoAllowableList.size() != 0) {

				allowablesNotConfiguredMessage = "Pollutant - EmissionUnitId:"
						+ pollutantEuWithNoAllowableList.keySet().toString()
						+ " configured to be 'Billed Based on Allowable instead of Actual' but no Allowable amount is configured at the facility EU level.";
			} else {
				logger.debug("This should never happen.");
			}
		} else {
			allowablesNotConfiguredMessage = "";
		}

		allowablesConfiguredZeroMessage = "";
		if (this.allowablesConfiguredZeroInFacilityEu) {
			if (pollutantEuWithZeroAllowableList.size() != 0) {

				allowablesConfiguredZeroMessage = "Pollutant - EmissionUnitId:"
						+ pollutantEuWithZeroAllowableList.keySet().toString()
						+ " configured to be 'Billed Based on Allowable instead of Actual' but Allowable amount of zero is configured at the facility EU level.";
			} else {
				logger.debug("This should never happen.");
			}
		} else {
			allowablesConfiguredZeroMessage = "";
		}

		maxTotalEmissionsPerPollutant = customFormat(
				BigDecimal.valueOf(pollutantCap).setScale(5, RoundingMode.HALF_UP).doubleValue());
		

		for (EmissionsTable eTable : mapEmissionsTable.values()) {
			totalFee = totalFee.add(eTable.getTotalFeePerPollutant());
		}

		boolean minFeeEnabled = false;
		minFeeEnabled = false;
		BigDecimal minimumFee = rt.getSc().getEiMinimumFee();
		if (totalFee.compareTo(minimumFee) < 0) {
			totalFee = minimumFee;
			minFeeEnabled = true;
		}

		totalFormattedFee = defaultFormat.format(totalFee.setScale(2, RoundingMode.HALF_UP));

		if (minFeeEnabled) {
			minFeeForEmissionInventory = "Minimum charge for Emissions Inventory is "
					+ defaultFormat.format(minimumFee.setScale(2, RoundingMode.HALF_UP));
		}
	}

	private int buildMapOfNonChargeableDueToSets(EmissionsTable etbl) {

		PollutantDef parentPoll = PollutantPartOf.retrieveParent(etbl.getPollutantCd());
		if (parentPoll != null) {

			String pollCd = parentPoll.getCode();
			EmissionsTable parent = mapEmissionsTable.get(pollCd);

			// Parent may exist but not be configured as reportable in ServiceCatalog.
			if (parent != null) {
				int parentsGen = buildMapOfNonChargeableDueToSets(parent);
				if (parent.getChildEmissionTable(etbl.getPollutantCd()) == null) {
					parent.addChildEmissionsTable(etbl);
					etbl.setNumGens(parentsGen + 1);
				}
			}
		}
		
		return etbl.getNumGens();
		
	}

	private void buildMapOfPollutantsBilledOnAllowable(ReportTemplates rt,
			EmissionUnit facilityEU) {

		SCEmissionsReport sc = rt.getSc();
		
		if (billedOnAllowable == null) {
			billedOnAllowable = new HashMap<String, Double>();
		}
		
		Double largestPMAllowable = 0d;
		boolean pmAllowableConfigured = false;
		String largestPMAllowablePollutantCd = "";
		
		for (EuEmission emi : facilityEU.getEuEmissions()) {
			
			SCPollutant scPoll = sc.findPollutant(emi.getPollutantCd());
			if (scPoll == null || !scPoll.isBilledBasedOnPermitted()) {
				continue;
			}

			Double currentAllowable = 0d;
			if (null == emi.getAllowableEmissionsTonsYear()) {
				continue;
			} else {
				currentAllowable = emi.getAllowableEmissionsTonsYear().doubleValue();
			}

			if (emi.getPollutantCd().startsWith("PM")) {
				pmAllowableConfigured = true;
				if (largestPMAllowable < currentAllowable) {
					largestPMAllowable = currentAllowable;
					largestPMAllowablePollutantCd = emi.getPollutantCd();
				}
			} else {
				billedOnAllowable.put(emi.getPollutantCd() + facilityEU.getEmuId(), currentAllowable);
			}

		}
		
		if (pmAllowableConfigured) {
			billedOnAllowable.put("PM" + facilityEU.getEmuId(), largestPMAllowable);
			billedOnAllowable.put(largestPMAllowablePollutantCd + facilityEU.getEmuId(), largestPMAllowable);
		}

		return;

	}

	private BigDecimal returnValueInTons(String units, BigDecimal input) {

		BigDecimal value = BigDecimal.valueOf(0d);
		if (!Utility.isNullOrEmpty(units)) {
			if (units.equalsIgnoreCase("TON")) {
				value = input;
			} else if (units.equalsIgnoreCase("LB")) {
				value = input.divide(BigDecimal.valueOf(2000));
			}
		}
		return value;

	}

	public double parseDoubleOrNull(String str) {
		return str != null ? parseDoubleSafely(str) : 0;
	}

	public double parseDoubleSafely(String str) {
		double result = 0;
		try {
			result = Double.parseDouble(str.replaceAll(",", ""));
		} catch (NullPointerException npe) {
			logger.error("parseDoubleSafely NullPointerException ", npe);
		} catch (NumberFormatException nfe) {
			logger.error("parseDoubleSafely NumberFormatException ", nfe);
		}
		return result;
	}

	public String customFormat(double value) {
		String pattern = "###,##0.00000";
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		String output = myFormatter.format(value);
		return output;
	}

	private BigDecimal getTotalAllowable(String pollutantCd, Integer EuId, Integer numPeriods) {
		Double totalAllowable = null;
		if (pollutantCd.startsWith("PM") && billedOnAllowable.containsKey("PM" + EuId)) {
			totalAllowable = billedOnAllowable.get("PM" + EuId);
		} else if (billedOnAllowable.containsKey(pollutantCd + EuId)) {
			totalAllowable = billedOnAllowable.get(pollutantCd + EuId);
		}
		// Allocate the totalAllowable equally across all processes for the EU.
		if (totalAllowable != null) {
			return new BigDecimal(totalAllowable / numPeriods);
		} else {
			return null;
		}
	}

	public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}

	public void setEmissionsReportService(EmissionsReportService emissionsReportService) {
		this.emissionsReportService = emissionsReportService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public String getTotalFormattedFee() {
		return totalFormattedFee;
	}

	public void setTotalFormattedFee(String totalFormattedFee) {
		this.totalFormattedFee = totalFormattedFee;
	}

	public EmissionsReport getReport() {
		return report;
	}

	public void setReport(EmissionsReport report) {
		this.report = report;
	}

	public String getMinFeeForEmissionInventory() {
		return minFeeForEmissionInventory;
	}

	public void setMinFeeForEmissionInventory(String minFeeForEmissionInventory) {
		this.minFeeForEmissionInventory = minFeeForEmissionInventory;
	}

	public TableSorter getEmissTableWrapper() {
		return emissTableWrapper;
	}

	public TableSorter getEmissTableDetailWrapper() {
		return emissTableDetailWrapper;
	}

	public String getEmissionsCalulationInfo() {
		return emissionsCalulationInfo;
	}

	public void setEmissionsCalulationInfo(String emissionsCalulationInfo) {
		this.emissionsCalulationInfo = emissionsCalulationInfo;
	}

	public List<EmissionsTable> getEmissTable() {
		return emissTableList;
	}

	public void setEmissTable(List<EmissionsTable> emissTable) {
		this.emissTableList = emissTable;
	}

	public List<EmissionsTableDetail> getEmissTableDetail() {
		return emissTableDetail;
	}

	public void setEmissTableDetail(List<EmissionsTableDetail> emissTableDetail) {
		this.emissTableDetail = emissTableDetail;
	}

	public boolean isallowablesNotConfiguredInFacilityEu() {
		return allowablesNotConfiguredInFacilityEu;
	}

	public void setallowablesNotConfiguredInFacilityEu(boolean allowablesNotConfiguredInFacilityEu) {
		this.allowablesNotConfiguredInFacilityEu = allowablesNotConfiguredInFacilityEu;
	}

	public String getAllowablesNotConfiguredMessage() {
		return allowablesNotConfiguredMessage;
	}

	public void setAllowablesNotConfiguredMessage(String allowablesNotConfiguredMessage) {
		this.allowablesNotConfiguredMessage = allowablesNotConfiguredMessage;
	}

	public boolean isallowablesConfiguredZeroInFacilityEu() {
		return allowablesConfiguredZeroInFacilityEu;
	}

	public void setallowablesConfiguredZeroInFacilityEu(boolean allowablesConfiguredZeroInFacilityEu) {
		this.allowablesConfiguredZeroInFacilityEu = allowablesConfiguredZeroInFacilityEu;
	}

	public String getAllowablesConfiguredZeroMessage() {
		return allowablesConfiguredZeroMessage;
	}

	public void setAllowablesConfiguredZeroMessage(String allowablesConfiguredZeroMessage) {
		this.allowablesConfiguredZeroMessage = allowablesConfiguredZeroMessage;
	}

	public boolean isMaxPollutantEmissionsReached() {
		return maxPollutantEmissionsReached;
	}

	public void setMaxPollutantEmissionsReached(boolean maxPollutantEmissionsReached) {
		this.maxPollutantEmissionsReached = maxPollutantEmissionsReached;
	}

	public String getMaxTotalEmissionsPerPollutant() {
		return maxTotalEmissionsPerPollutant;
	}

	public void setMaxTotalEmissionsPerPollutant(String maxTotalEmissionsPerPollutant) {
		this.maxTotalEmissionsPerPollutant = maxTotalEmissionsPerPollutant;
	}

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	public final boolean isPollutantNonChargeableDueToSets() {
		return pollutantNonChargeableDueToSets;
	}

	public final void setPollutantNonChargeableDueToSets(boolean pollutantNonChargeableDueToSets) {
		this.pollutantNonChargeableDueToSets = pollutantNonChargeableDueToSets;
	}

	public final String getTotalChargeableTons() {
		return sumChargeableTons.toPlainString();
	}

	public final void setTotalChargeableTons(String totalChargeableTons) {
		this.sumChargeableTons = new BigDecimal(totalChargeableTons);
	}

}

