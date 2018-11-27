package us.oh.state.epa.stars2.webcommon.pdf.application;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.TreeMap;

import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.FacilityWideRequirement;
import us.oh.state.epa.stars2.database.dbObjects.application.TIVApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplicationEUEmissions;
import us.oh.state.epa.stars2.database.dbObjects.application.TVEUOperatingScenario;
import us.oh.state.epa.stars2.database.dbObjects.application.TVEUOperationalRestriction;
import us.oh.state.epa.stars2.database.dbObjects.application.TVEUPollutantLimit;
import us.oh.state.epa.stars2.database.dbObjects.application.TVPteAdjustment;
import us.oh.state.epa.stars2.def.ApplicationType;
import us.oh.state.epa.stars2.def.PATvEuPteDeterminationBasisDef;
import us.oh.state.epa.stars2.def.PTIOFedRuleAppl1Def;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.TVAppComplianceCertIntervalDef;
import us.oh.state.epa.stars2.def.TVApplicationPurposeDef;
import us.oh.state.epa.stars2.def.TVClassification;
import us.oh.state.epa.stars2.def.TVFacWideReqOptionDef;
import us.oh.state.epa.stars2.def.TVFedRuleAppDef;
import us.oh.state.epa.stars2.def.TVIeuReasonDef;
import us.oh.state.epa.stars2.def.TvReqBasisDef;
import us.oh.state.epa.stars2.def.TvRestrictionTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Table;

@SuppressWarnings("serial")
public class TVApplicationPdfGenerator extends ApplicationPdfGenerator {

	private static String HAP = "HAP";
	private static String GHG = "GHG";
	private static DecimalFormat POTENTIAL_EMISSIONS_FORMAT = new DecimalFormat(
			"#,###,###,###.###");

	protected TVApplicationPdfGenerator() {
		super();
	}

	public void printApplication(Application app) throws IOException,
			DocumentException {

		TVApplication tvApp = (TVApplication) app;
		printTVApplication(tvApp);

		// use tree map to sort EUs by EPA EmuId
		TreeMap<String, ApplicationEU> euMap = new TreeMap<String, ApplicationEU>();
		for (ApplicationEU eu : app.getIncludedEus()) {
			euMap.put(eu.getFpEU().getEpaEmuId(), eu);
		}
		for (String epaEmuId : euMap.keySet()) {
			ApplicationEU eu = euMap.get(epaEmuId);
			printTVApplicationEU(tvApp, (TVApplicationEU) eu);
		}

	}

	private void printTVApplication(TVApplication tvApp) throws IOException,
			DocumentException {
		// TODO -- remove un-useful mark
		// HeaderFooter footer = new HeaderFooter(new
		// Phrase("Ohio EPA, Division of Air Pollution Control" +
		// "                          "
		// + " Page ", normalFont),
		// new Phrase( "                                      " +
		// "Title V Permit Application - Section I", normalFont));
		// footer.setAlignment(Element.ALIGN_CENTER);
		// footer.setBorder(0);
		document.setFooter(createFooter(tvApp));

		// Mantis 3268 document.setPageCount(0);
		document.setPageCount(0);
		document.newPage();

		// add a title to the document
		String[] title = { "Air Quality Division",
				"Application for Title V Permit-to-Operate", };

		Table titleTable = createTitleTable(title[0], title[1]);
		document.add(titleTable);

		// add "items" or questions
		List sections = createItemList();
		sections.add(getTVApplicationInfo(tvApp));
		sections.add(getReasonForApplication(tvApp));
		
		if (isPteSelectable(tvApp.getTvApplicationPurposeCd())) {
			sections.add(getPTE(tvApp));
		}
		if (tvApp.getTvApplicationPurposeCd() == null || isPteSelectable(tvApp.getTvApplicationPurposeCd()) || isPermitReasonSPMSelectable()) {
			sections.add(getOperationsDescription(tvApp));
			sections.add(getCleanAirActProvisions(tvApp));
			sections.add(getAmbientMonitoring(tvApp));
		}
		
		sections.add(getAirContaminantSources(tvApp));
	
		if (tvApp.getTvApplicationPurposeCd() == null || isPteSelectable(tvApp.getTvApplicationPurposeCd()) || isPermitReasonReopeningSelectable() || isPermitReasonSPMSelectable()) {
			sections.add(getFacilityWideRequirements(tvApp));
		}
		
		if (tvApp.getTvApplicationPurposeCd() == null || isPteSelectable(tvApp.getTvApplicationPurposeCd())) {
			sections.add(getInsignificantActivities());
		}
		sections.add(getAttachments("Application Attachments",
				tvApp.getDocuments(), ApplicationType.TITLE_V));

		if (isInternal()) {
			sections.add(generateNotes(tvApp.getApplicationNotes()));
		}

		document.add(sections);
	}

	private ListItem getTVApplicationInfo(TVApplication tvApp)
			throws BadElementException {

		ListItem ret = new ListItem();

		ret.add(createItemTitle("Title V Permit Application", ""));

		Table infoTable = generateQATableShortQ();
		if (tvApp.isApplicationCorrected() && !tvApp.isApplicationAmended()) {
			generateQAColumns(infoTable, "Correction to application number :",
					tvApp.getPreviousApplicationNumber());
			generateQAColumns(infoTable, "Reason for correction :",
					tvApp.getApplicationCorrectedReason());
		}

		if (isInternal()) {
			generateQAColumns(infoTable, "Date application received :",
					toDateFormat1(tvApp.getReceivedDate()));
			generateQAColumns(infoTable, "AQD Title V Application Number :",
					tvApp.getAqdTvApplicationNumber());
			generateQAColumns(infoTable,
					"Is this a legacy TitleV Application ?",
					toYesNo(tvApp.isLegacyStateTVApp()));
		}
		ret.add(infoTable);

		return ret;
	}

	private ListItem getReasonForApplication(TVApplication tvApp)
			throws BadElementException {
		ListItem ret = new ListItem();

		ret.add(createItemTitle("Reason for Application", ""));
		
		if (isPermitReasonSelectable()) {
			ret.add(createOneLine(
					"Explanation of Revision/Modification/Reopening",
					normalFont));

			List tempList = createItemList();
			tempList.add(createItem(
					"Administrative Permit Amendment - Administrative amendments are for minor changes, such "
							+ "as correction of typographical errors, changes to the responsible official(s), address changes, "
							+ "or a change in ownership.", italicFont));
			tempList.add(createItem(
					"502(b)(10) Change - A source may change operations without a Title V permit "
							+ "revision provided that the change meets the requirements of WAQSR Ch 6 Sec 3(d)(iii).",
					italicFont));
			tempList.add(createItem(
					"Minor Permit Modification - Minor permit modifications must meet the requirements of WAQSR Ch 6 Sec 3(d)(vi).",
					italicFont));
			tempList.add(createItem(
					"Significant Permit Modification – Any permit modification that does not qualify "
							+ "as a minor permit modification or administrative amendment.",
					italicFont));
			tempList.add(createItem(
					"Reopening – Additional requirements (typically NSPS or NESHAP) "
							+ "become applicable to the facility and the current Title V permit has three or more years "
							+ "remaining in its term.", italicFont));
			tempList.add(createItem(
					"Rescission Request – Through the removal of equipment or obtaining federally "
							+ "enforceable limits, the facility has become a minor source",
					italicFont));

			ret.add(tempList);
		}

		Table table = generateQATable();
		table.addCell(createCell(
				"Please Identify the reason for this application :", 0));

		if (tvApp.getTvApplicationPurposeCd() != null) {
			table.addCell(createDataCell(TVApplicationPurposeDef.getData()
					.getItems().getItemDesc(tvApp.getTvApplicationPurposeCd()),
					0));
			if (tvApp.getTvApplicationPurposeCd().equals(
					TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING)) {
				if (!Utility.isNullOrEmpty(tvApp.getPermitReasonCd())) {
						generateQAColumns(table, "", " - "
								+ PermitReasonsDef.getData().getItems()
										.getItemDesc(tvApp.getPermitReasonCd()));
				}
			}
		}
		ret.add(table);

		ret.add(createOneLine(
				"Please summarize the reason this permit is being applied for.",
				normalFont));
		ret.add(createTextTable(tvApp.getApplicationDesc()));

		return ret;
	}

	private ListItem getPTE(TVApplication tvApp) throws BadElementException {
		ListItem ret = new ListItem();

		ret.add(createItemTitle(
				"Facility-wide Potential to Emit (PTE)",
				" - The following table is generated by the system and is the sum of "
						+ "the PTE’s for all emission units in this application.  "
						+ "If the facility-wide PTE located in the facility inventory for this facility "
						+ "is different from that represented below, "
						+ "include an attachment describing the circumstances that change the PTE."));

		ret.add(createOneLine("Criteria Pollutants :", boldFont));
		ret.add(createPteTable(tvApp.getCapPteTotals()));

		ret.add(createOneLine("Hazardous Air Pollutants (HAPs) :", boldFont));
		ret.add(createHAPsTable(tvApp.getHapPteTotals()));
		ret.add(createOneLine(
				"* Based on the sum of all pollutants provided in this table.",
				italicFont));

		ret.add(createOneLine("Greenhouse Gas Pollutants :", boldFont));
		ret.add(createGHGTable(tvApp.getGhgPteTotals()));
		//ret.add(createOneLine(
		//		"* Based on the sum of all pollutants provided in this table.",
		//		italicFont));

		ret.add(createOneLine("Other Regulated Pollutants :", boldFont));
		ret.add(createPteTable(tvApp.getOthPteTotals()));

		return ret;
	}

	private Table createPteTable(java.util.List<TVPteAdjustment> pteTotals)
			throws BadElementException {
		Table ret = generateTable(2);
		float[] widths = { 2, 1 };
		ret.setWidths(widths);

		ret.addCell(createHeaderCell("Pollutant"));
		ret.addCell(createHeaderCell("PTE (tons/year)"));

		for (TVPteAdjustment poll : pteTotals) {
			ret.addCell(createDataCell(PollutantDef.getData().getItems()
					.getItemDesc(poll.getPollutantCd())));
			ret.addCell(createNumberCell(toEMISSIONS_VALUE_FORMAT(
					poll.getPteEUTotal(), false)));
		}
		return ret;
	}

	private Table createHAPsTable(java.util.List<TVPteAdjustment> hapPteTotals)
			throws BadElementException {
		Table ret = generateTable(3);
		float[] widths = { 2, 1, 1 };
		ret.setWidths(widths);
		ret.addCell(createHeaderCell("Pollutant"));
		ret.addCell(createHeaderCell("PTE (tons/year)"));
		ret.addCell(createHeaderCell("Major Status"));

		for (TVPteAdjustment poll : hapPteTotals) {
			ret.addCell(createDataCell(PollutantDef.getData().getItems()
					.getItemDesc(poll.getPollutantCd())));
			if (!Utility.isNullOrEmpty(poll.getPteEUTotal())) {
				ret.addCell(createNumberCell(toEMISSIONS_VALUE_FORMAT(
						poll.getPteEUTotal(), true)));
			} else {
				ret.addCell(createNumberCell(toEMISSIONS_VALUE_FORMAT(
						poll.getPteAdjusted(), true)));
			}
			ret.addCell(createDataCell(poll.getMajorStatus()));
		}

		return ret;
	}

	private Table createGHGTable(java.util.List<TVPteAdjustment> ghgPteTotals)
			throws BadElementException {
		//Table ret = generateTable(3);
		//float[] widths = { 2, 1, 1 };
		
		Table ret = generateTable(2);
		float[] widths = { 2, 1 };
		
		ret.setWidths(widths);

		ret.addCell(createHeaderCell("Pollutant"));
		ret.addCell(createHeaderCell("Facility PTE (tons/year)"));
		//ret.addCell(createHeaderCell("CO2 Equivalent (tons/year)"));

		for (TVPteAdjustment poll : ghgPteTotals) {
			ret.addCell(createDataCell(PollutantDef.getData().getItems()
					.getItemDesc(poll.getPollutantCd())));
			if (!Utility.isNullOrEmpty(poll.getPteEUTotal())) {
				ret.addCell(createNumberCell(toEMISSIONS_VALUE_FORMAT(
						poll.getPteEUTotal(), false)));
			} else {
				ret.addCell(createNumberCell(toEMISSIONS_VALUE_FORMAT(
						poll.getPteAdjusted(), false)));
			}

			//ret.addCell(createNumberCell(toEMISSIONS_VALUE_FORMAT(
			//		poll.getCo2Equivalent(), false)));
		}

		return ret;
	}

	private ListItem getOperationsDescription(TVApplication tvApp)
			throws BadElementException {
		ListItem ret = new ListItem();

		ret.add(createItemTitle("Operations Description", ""));

		ret.add(createOneLine(
				"Please provide a detailed description of the operations performed at this facility.",
				normalFont));
		ret.add(createTextTable(tvApp.getOperationsDsc()));

		ret.add(createOneLine(
				"Are there any Alternate Operating Scenarios (AOS) authorized for multiple emission units at your facility ?    "
						+ toYesNo(tvApp.getAlternateOperatingScenarios()),
				normalFont));
		ret.add(createOneLine(
				"* The attachment must include a list of each emissions unit affected by the scenario, the SIC code(s) "
						+ "for processes and products associated with the AOS, as well as the requirements that apply during the AOS.",
				italicFont));
		return ret;
	}

	private ListItem getCleanAirActProvisions(TVApplication tvApp)
			throws BadElementException {
		ListItem ret = new ListItem();

		ret.add(createItemTitle("Clean Air Act Provisions", ""));

		Table table = generateQATable();

		generateQAColumns(table,
				"Is this facility subject to 112(r) of the Clean Air Act ?",
				toYesNo(tvApp.getSubjectTo112R()));
		if (isEqual(tvApp.getSubjectTo112R(), "Y")) {
			generateQAColumns(table,
					"Has a plan been submitted under 112(r) ?",
					toYesNo(tvApp.getPlanSubmittedUnder112R()));

			if (isEqual(tvApp.getPlanSubmittedUnder112R(), "Y")) {
				generateQAColumns(table,
						"Date the Risk Management Plan was submitted ?",
						toDateFormat1(tvApp.getRiskManagementPlanSubmitDate()));
			}
		}

		generateQAColumns(table,
				"Is this facility subject to Title IV of the Clean Air Act ?",
				toYesNo(tvApp.getSubjectToTIV()));

		generateQAColumns(
				table,
				"Frequency of the submission of compliance certifications during the term of the permit ?",
				TVAppComplianceCertIntervalDef.getData().getItems()
						.getItemDesc(tvApp.getComplianceCertSubmitFrequency()));

		generateQAColumns(
				table,
				"Are the air contaminant sources identified in this application in compliance with applicable "
						+ "enhanced monitoring (Compliance Assurance Monitoring) and compliance certification requirements "
						+ "of the Act ?",
				toYesNo(tvApp.getComplianceWithApplicableEnhancedMonitoring()));

		if (isEqual(tvApp.getComplianceWithApplicableEnhancedMonitoring(), "N")) {
			generateQAColumns(table,
					"Describe which requirements are not being met :",
					toString(tvApp.getComplianceRequirementsNotMet()));
		}

		generateQAColumns(
				table,
				"Is the facility subject to engine configuration restrictions ?",
				toYesNo(tvApp.getSubjectToEngineConfigRestrictions()));

		if (isEqual(tvApp.getSubjectToEngineConfigRestrictions(), "Y")) {
			generateQAColumns(table, "NSR Permit Number ?",
					toString(tvApp.getNsrPermitNumber()));
		}

		generateQAColumns(
				table,
				"Is the facility subject to WAQSR Chapter 14, Section 3 (Actual Emissions of SO2 in any calendar "
						+ "year 2000 or later > 100 tons per year) ?",
				toYesNo(tvApp.getSubjectToWAQSR()));

		ret.add(table);

		return ret;
	}

	private ListItem getAmbientMonitoring(TVApplication tvApp)
			throws BadElementException {
		ListItem ret = new ListItem();

		ret.add(createItemTitle("Ambient Monitoring", ""));
		ret.add(createOneLine(
				"Is the facility required to conduct ambient monitoring ?    "
						+ toYesNo(tvApp.getAmbientMonitoring()), normalFont));

		ret.add(createOneLine(
				"If your facility is required to conduct ambient monitoring, attach "
						+ "a summary of the requirements including the following information :",
				italicFont));

		List tempList = createItemList();
		tempList.add(createItem("The basis for this requirement", italicFont));
		tempList.add(createItem("What pollutants are monitored", italicFont));
		tempList.add(createItem("What meteorological parameters are monitored",
				italicFont));
		tempList.add(createItem(
				"The date of your most recent Quality Assurance Plan or plan revision",
				italicFont));
		ret.add(tempList);

		return ret;
	}

	private ListItem getAirContaminantSources(TVApplication tvApp)
			throws BadElementException {

		ListItem ret = new ListItem();
		ret.add(createItemTitle("Air Contaminant Sources in this Application",
				""));

		Table table = generateTable(3);
		table.addCell(createHeaderCell("Emissions Unit ID"));
		table.addCell(createHeaderCell("Company Equipment ID"));
		table.addCell(createHeaderCell("Equipment Description"));
		for (ApplicationEU eu : tvApp.getIncludedEus()) {
			table.addCell(createDataCell(eu.getFpEU().getEpaEmuId()));
			table.addCell(createDataCell(eu.getFpEU().getCompanyId()));
			table.addCell(createDataCell(eu.getFpEU().getRegulatedUserDsc()));
		}
		ret.add(table);

		return ret;
	}

	private ListItem getFacilityWideRequirements(TVApplication tvApp)
			throws BadElementException {

		ListItem ret = new ListItem();
		ret.add(createItemTitle("Facility-Wide Requirements", ""));

		Table tempTable = generateQATable();

		// NSPS
		tempTable
				.addCell(createCellwihtDes(
						"New Source Performance Standards (NSPS) :",
						"New Source Performance Standards are listed under 40 CFR "
								+ "60 - Standards of Performance for New Stationary Sources."));
		tempTable.addCell(createDataCell(PTIOFedRuleAppl1Def.getData()
				.getItems().getItemDesc(tvApp.getNspsApplicableFlag()), 0));
		ret.add(tempTable);
		if (isShowSubparts(tvApp.getNspsApplicableFlag())) {
			ret.add(createNSPSTable(tvApp.getNspsSubpartCodes()));
		}

		// NESHAP
		tempTable = generateQATable();
		tempTable
				.addCell(createCellwihtDes(
						"National Emission Standards for Hazardous Air Pollutants (NESHAP Part 61) :",
						"National Emissions Standards for Hazardous Air Pollutants are listed under 40 "
								+ "CFR 61. (These include asbestos, benzene, beryllium, mercury, and vinyl chloride)."));
		tempTable.addCell(createDataCell(PTIOFedRuleAppl1Def.getData()
				.getItems().getItemDesc(tvApp.getNeshapApplicableFlag()), 0));
		ret.add(tempTable);
		if (isShowSubparts(tvApp.getNeshapApplicableFlag())) {
			ret.add(createNESHAPTable(tvApp.getNeshapSubpartCodes()));
		}

		// MACT
		tempTable = generateQATable();
		tempTable
				.addCell(createCellwihtDes(
						"National Emission Standards for Hazardous Air Pollutants (NESHAP Part 63) :",
						"National Emission Standards for Hazardous Air Pollutants (NESHAP Part 63) standards are listed under 40 CFR 63."));
		tempTable.addCell(createDataCell(PTIOFedRuleAppl1Def.getData()
				.getItems().getItemDesc(tvApp.getMactApplicableFlag()), 0));
		ret.add(tempTable);
		if (isShowSubparts(tvApp.getMactApplicableFlag())) {
			ret.add(createMACTTable(tvApp.getMactSubpartCodes()));
		}

		// PSD
		tempTable = generateQATable();
		tempTable.addCell(createCellwihtDes(
				"Prevention of Significant Deterioration (PSD) :",
				"These rules are found under WAQSR Chapter 6, Section 4."));
		tempTable.addCell(createDataCell(TVFedRuleAppDef.getData().getItems()
				.getItemDesc(tvApp.getPsdApplicableFlag()), 0));
		ret.add(tempTable);

		// Non-Attainment

		if (tvApp.getTvApplicationPurposeCd() == null
				|| isPteSelectable(tvApp.getTvApplicationPurposeCd())
				|| isPermitReasonSPMSelectable()) {
			tempTable = generateQATable();
			tempTable
					.addCell(createCellwihtDes(
							"Non-Attainment New Source Review :",
							"These rules are found under WAQSR Chapter 6, Section 13."));
			tempTable.addCell(createDataCell(TVFedRuleAppDef.getData()
					.getItems().getItemDesc(tvApp.getNsrApplicableFlag()), 0));
			ret.add(tempTable);
		}

		// Other
		tempTable = generateQATable();
		tempTable.addCell(createCellwihtDes(
				"Other Facility-Wide Requirements :", ""));
		tempTable
				.addCell(createDataCell(
						TVFacWideReqOptionDef
								.getData()
								.getItems()
								.getItemDesc(
										tvApp.getFacilityWideRequirementFlag()),
						0));
		ret.add(tempTable);
		if (TVFacWideReqOptionDef.SUBJECT.equals(tvApp
				.getFacilityWideRequirementFlag())) {
			ret.add(createFacilityWideRequirementTable(tvApp
					.getFacilityWideRequirements()));
		}

		ret.add(createOneLine(
				"Are there any proposed exemptions from otherwise applicable requirements for the facility?	",
				boldFont));
		ret.add(createTextTable(tvApp.getProposedExemptions()));

		return ret;
	}

	private Table createFacilityWideRequirementTable(
			java.util.List<FacilityWideRequirement> facilityWideRequirements)
			throws BadElementException {
		Table ret = generateTable(2);
		ret.addCell(createHeaderCell("Facility-Wide Requirement"));
		ret.addCell(createHeaderCell("Proposed Method to Demonstrate Compliance"));
		for (FacilityWideRequirement req : facilityWideRequirements) {
			ret.addCell(createDataCell(req.getDescription()));
			ret.addCell(createDataCell(req.getProposedMethod()));
		}

		return ret;
	}

	private ListItem getInsignificantActivities() {

		ListItem ret = new ListItem();

		ret.add(createItemTitle("Insignificant Activities", ""));

		ret.add(createOneLine(
				"Attach a list of activities incidental to the primary business of the facility and which result "
						+ "in emissions of less than one ton per year of a regulated pollutant or emissions less "
						+ "than 1000 pounds per year of a hazardous air pollutant. By listing these sources, the "
						+ "applicant is certifying emissions are less than the above quantities, and that the activity "
						+ "has no applicable requirements. Flares, incinerators, and fuel burning equipment (no matter "
						+ "how small) have applicable requirements and may not be listed here. Include in the list for each activity:",
				normalFont));

		List tempList = createItemList();
		tempList.add(createItem("Activity Description", italicFont));
		tempList.add(createItem("Pollutant(s)", italicFont));
		tempList.add(createItem("Estimated Emissions (by pollutant)",
				italicFont));
		ret.add(tempList);

		ret.add(createOneLine(
				"WAQSR Chapter 6, Section 3(c)(ii)(A)(III)(1.) indicates a source is not required to provide detailed "
						+ "information on insignificant activities which are incidental to the primary business activity. "
						+ "The insignificant activities must result in less than one ton of emissions of a regulated "
						+ "pollutant per year, or less than 1000 pounds of emissions from a hazardous air pollutant per year, "
						+ "and have no other applicable regulatory requirements. The emissions level of 1 ton per year for each "
						+ "regulated pollutant or 1000 pounds per year for each hazardous pollutant should be applied to the same "
						+ "activity collectively. For example, emissions from all maintenance-type painting operations at the "
						+ "facility should be totaled and listed collectively. List these activities, their respective "
						+ "pollutant(s), and an emission estimate for each pollutant. By listing the sources in this attachment, "
						+ "you are certifying that all activities and emission levels meet the aforementioned criteria.",
				normalFont));

		return ret;
	}

	private Table getTVPteTable(TVEUOperatingScenario scenario,
			java.util.List<TVApplicationEUEmissions> emissions, String type)
			throws BadElementException {

		Table ret = generateTable(3);
		float[] widths = { 8, 2, 3 };
		ret.setWidths(widths);

		ret.addCell(createHeaderCell("Pollutant"));
		ret.addCell(createHeaderCell("Potential to Emit (PTE) (tons/year)"));
		ret.addCell(createHeaderCell("Basis for Determination"));

		for (TVApplicationEUEmissions e : emissions) {
			ret.addCell(createDataCell(PollutantDef.getData().getItems()
					.getItemDesc(e.getPollutantCd())));

			ret.addCell(createNumberCell(toEMISSIONS_VALUE_FORMAT(
					e.getPteTonsYr(), HAP.equals(type))));

			ret.addCell(createDataCell(PATvEuPteDeterminationBasisDef.getData()
					.getItems().getItemDesc(e.getPteDeterminationBasis())));
		}

		// TODO --
		if (HAP.equals(type)) {
			ret.addCell(createDataCell("Total HAPs*  "
					+ toEMISSIONS_VALUE_FORMAT(
							toString(scenario.getHapsTotal()), true)
					+ " (tons/year)"));
		} else if (GHG.equals(type)) {
			// AQD asked us to remove the GHG total
			//ret.addCell(createDataCell("Total GHGs*  "
			//		+ toEMISSIONS_VALUE_FORMAT(
			//				toString(scenario.getGhgsTotal()), false)
			//		+ " (tons/year)"));
		}

		return ret;

	}

	private void printTVApplicationEU(TVApplication tvApp, TVApplicationEU eu)
			throws IOException, DocumentException {
		if (isIEU(eu)) {
			printInsignificantEu(tvApp, eu);
		} else {
			printNonInsignificantEU(tvApp, eu);
		}
	}

	private void printNonInsignificantEU(TVApplication tvApp, TVApplicationEU eu)
			throws IOException, DocumentException {
		// HeaderFooter footer = new HeaderFooter(new
		// Phrase("Ohio EPA, Division of Air Pollution Control" +
		// "                          "
		// + " Page ", normalFont),
		// new Phrase( "                                      " +
		// "Title V Permit Application - Section II", normalFont));
		// footer.setAlignment(Element.ALIGN_CENTER);
		// footer.setBorder(0);
		// document.setFooter(createFooter(tvApp));
		//
		// // start section II on a new page
		// document.setPageCount(0);
		document.newPage();
		document.add(getTVEUHeader(eu));

		// add "items" or questions
		List sections = createItemList();
		sections.add(getOperatingSchedule(eu.getNormalOperatingScenario()));
		sections.add(getTVEuPTE(eu, eu.getNormalOperatingScenario()));

		sections.add(getSpecificRequirements(eu));

		sections.add(getAttachments("Emissions Unit Attachments",
				eu.getEuDocuments(), ApplicationType.TITLE_V));

		document.add(sections);
	}

	private void printInsignificantEu(TVApplication tvApp, TVApplicationEU eu)
			throws IOException, DocumentException {
		// HeaderFooter footer = new HeaderFooter(new
		// Phrase("Ohio EPA, Division of Air Pollution Control" +
		// "                          "
		// + " Page ", normalFont),
		// new Phrase( "                                      " +
		// "Title V Permit Application - Section II", normalFont));
		// footer.setAlignment(Element.ALIGN_CENTER);
		// footer.setBorder(0);
		// document.setFooter(createFooter(tvApp));
		//
		// // start section II on a new page
		// document.setPageCount(0);
		document.newPage();
		document.add(getTVEUHeader(eu));

		// add "items" or questions
		List sections = createItemList();
		sections.add(getTVEuPTE(eu, eu.getNormalOperatingScenario()));

		sections.add(getAttachments("Emissions Unit Attachments",
				eu.getEuDocuments(), ApplicationType.TITLE_V));

		document.add(sections);
	}

	private Table getTVEUHeader(TVApplicationEU eu) throws BadElementException {

		Table header = generateTable(3);
		header.setBorder(0);
		float[] widths = { 50, 30, 20 };
		header.setWidths(widths);

		String headerTitle = null;
		boolean insignificant = false;

		Cell blank = new Cell();
		blank.setBorder(0);

		String classCd = eu.getFpEU().getTvClassCd();
		if (classCd != null && classCd.equals(TVClassification.INSIG)) {
			headerTitle = "Insignificant Emissions Unit";
			insignificant = true;
		} else {
			headerTitle = "Non-insignificant Emissions Unit";
		}

		Cell titleCell = createCell(headerTitle, boldFont, 0);
		titleCell.setBorderWidthBottom(1); // for underline
		titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);

		generateHeaderCell(header, titleCell, "AQD EU ID: ", eu.getFpEU()
				.getEpaEmuId());
		generateHeaderCell(header, blank, "AQD EU description: ", eu.getFpEU()
				.getEuDesc());
		generateHeaderCell(header, blank, "Company EU ID: ", eu.getFpEU()
				.getCompanyId());
		generateHeaderCell(header, blank, "Company EU Description: ", eu
				.getFpEU().getRegulatedUserDsc());

		if (insignificant) {
			generateHeaderCell(
					header,
					blank,
					"Basis for IEU status: ",
					TVIeuReasonDef.getData().getItems()
							.getItemDesc(eu.getTvIeuReasonCd()));
		}

		return header;
	}

	private ListItem getOperatingSchedule(TVEUOperatingScenario scenario)
			throws BadElementException {
		ListItem ret = new ListItem();

		ret.add(createItemTitle("Maximum Allowable Operating Schedule", ""));

		ret.add(createOneLine(
				"Provide the maximum allowable operating schedule for this emissions unit",
				normalFont));

		Table tempTable = generateQATableShortQ();
		generateQAColumns(tempTable, "Hours/day :",
				toString(scenario.getOpSchedHrsDay()));
		generateQAColumns(tempTable, "Hours/year :",
				toString(scenario.getOpSchedHrsYr()));
		ret.add(tempTable);

		float[] widths = { 4, 1 };
		tempTable = generateQATable(widths);
		generateQAColumns(
				tempTable,
				"Is there an Alternate Operating Scenario (AOS) authorized for this emission unit that is not included "
						+ "in an AOS for multiple emission units, already attached to this application ?",
				toYesNo(scenario.getOpAosAutherized()));
		ret.add(tempTable);

		ret.add(createOneLine(
				"* The attachment must include a list of each emissions unit affected by the scenario, "
						+ "the SIC code(s) for processes and products associated with the AOS, as well as the requirements "
						+ "that apply during the AOS.", italicFont));

		return ret;
	}

	private ListItem getTVEuPTE(TVApplicationEU eu,
			TVEUOperatingScenario scenario) throws BadElementException {
		ListItem ret = new ListItem();

		ret.add(createItemTitle(
				"Emission Unit Potential to Emit (PTE)",
				" \"Potential to emit\" means the maximum capacity of a stationary source to emit any air pollutant "
						+ "under its physical and operational design. Any physical or operational limitation on the capacity of a "
						+ "source to emit an air pollutant, including air pollution control equipment and restrictions on hours of "
						+ "operation or on the type or amount of material combusted, stored or processed, shall be treated as part "
						+ "of its design if the limitation is enforceable by the EPA and the Division.."));

		ret.add(createOneLine("Criteria Pollutants :", boldFont));
		ret.add(getTVPteTable(scenario, scenario.getCapEmissions(), ""));

		ret.add(createOneLine("Hazardous Air Pollutants (HAPs) :", boldFont));
		ret.add(getTVPteTable(scenario, scenario.getHapEmissions(), HAP));
		ret.add(createOneLine(
				"* Based on the sum of all pollutants provided in this table.",
				italicFont));

		ret.add(createOneLine("Greenhouse Gas Pollutants :", boldFont));
		ret.add(getTVPteTable(scenario, scenario.getGhgEmissions(), GHG));
		//ret.add(createOneLine(
		//		"* Based on the sum of all pollutants provided in this table.",
		//		italicFont));

		ret.add(createOneLine("Other Regulated Pollutants :", boldFont));
		ret.add(getTVPteTable(scenario, scenario.getOthEmissions(), ""));

		ret.add(createOneLine(
				"*Attach a description and calculations, as appropriate, documenting the basis for the PTE for each "
						+ "pollutant. If permit limits are used, no additional information is needed. For other basis methods:",
				italicFont));
		// AQD asked us to remove this.
		//ret.add(createOneLine(
		//		"**PTE for purposes of MACT applicability may include controlled emissions.",
		//		italicFont));

		List tempList = createItemList();
		tempList.add(createItem(
				"Manufacturer’s Data – Attach a copy of the information from the manufacturer",
				italicFont));
		tempList.add(createItem(
				"Test Results – Indicate test data and results", italicFont));
		tempList.add(createItem(
				"Similar Source Test Results – Attach a test result summary with a description of how this is a similar source",
				italicFont));
		tempList.add(createItem("GRICalc – Attach a printout of results",
				italicFont));
		tempList.add(createItem("Tanks Program – Attach a printout of results",
				italicFont));
		tempList.add(createItem(
				"AP-42 – Indicate AP-42 factor and publication date",
				italicFont));
		tempList.add(createItem(
				"Other – Attach a description of the method used", italicFont));
		ret.add(tempList);

		if (EmissionUnitTypeDef.ENG
				.equals(eu.getFpEU().getEmissionUnitTypeCd())) {
			ret.add(createOneLine("Engine Only Requirements", normalFont));

			Table tempTable = generateQATableShortQ();
			generateQAColumns(tempTable, "Engine Order Date :",
					toDateFormat1(scenario.getEngOrderDate()));
			generateQAColumns(tempTable, "Engine Manufactured Date :",
					toDateFormat1(scenario.getEngManufactureDate()));
			ret.add(tempTable);
		}

		return ret;
	}

	private ListItem getSpecificRequirements(TVApplicationEU eu)
			throws BadElementException {
		ListItem ret = new ListItem();

		ret.add(createItemTitle("Emissions Unit - Specific Requirements", ""));

		Table tempTable = generateQATable();

		// NSPS
		tempTable
				.addCell(createCellwihtDes(
						"New Source Performance Standards (NSPS) :",
						"New Source Performance Standards are listed under 40 CFR "
								+ "60 - Standards of Performance for New Stationary Sources."));
		tempTable.addCell(createDataCell(PTIOFedRuleAppl1Def.getData()
				.getItems().getItemDesc(eu.getNspsApplicableFlag()), 0));
		ret.add(tempTable);
		if (isShowSubparts(eu.getNspsApplicableFlag())) {
			ret.add(createNSPSTable(eu.getNspsSubpartCodes()));
		}

		// NESHAP
		tempTable = generateQATable();
		tempTable
				.addCell(createCellwihtDes(
						"National Emission Standards for Hazardous Air Pollutants (NESHAP Part 61) :",
						"National Emissions Standards for Hazardous Air Pollutants (NESHAP Part 61) are listed under 40 "
								+ "CFR 61. (These include asbestos, benzene, beryllium, mercury, and vinyl chloride)."));
		tempTable.addCell(createDataCell(PTIOFedRuleAppl1Def.getData()
				.getItems().getItemDesc(eu.getNeshapApplicableFlag()), 0));
		ret.add(tempTable);
		if (isShowSubparts(eu.getNeshapApplicableFlag())) {
			ret.add(createNESHAPTable(eu.getNeshapSubpartCodes()));
		}

		// MACT
		tempTable = generateQATable();
		tempTable
				.addCell(createCellwihtDes(
						"National Emission Standards for Hazardous Air Pollutants (NESHAP Part 63) :",
						"National Emission Standards for Hazardous Air Pollutants (NESHAP Part 63) standards are listed under 40 CFR 63."));
		tempTable.addCell(createDataCell(PTIOFedRuleAppl1Def.getData()
				.getItems().getItemDesc(eu.getMactApplicableFlag()), 0));
		ret.add(tempTable);
		if (isShowSubparts(eu.getMactApplicableFlag())) {
			ret.add(createMACTTable(eu.getMactSubpartCodes()));
		}

		if (eu.getFedRulesExemption()) {
			ret.add(createOneLine(
					"Please explain why you checked “Subject, but exempt” in this question for one or more federal "
							+ "rules. Identify each exemption and whether the entire facility and/or the specific air "
							+ "contaminant sources included in this permit application is exempted. Attach an additional page if necessary.",
					boldFont));
			ret.add(createTextTable(eu.getFederalRuleApplicabilityExplanation()));
		}

		ret.add(createOneLine("Pollutant Limits :", boldFont));
		ret.add(getPollutantLimits(eu.getPollutantLimits()));
		ret.add(createOneLine(
				"*If the pre-control potential emissions for any pollutant are above the major source threshold "
						+ "for that pollutant, and this application is for the renewal or modification of a "
						+ "Title V permit, then the application must include a CAM (Compliance Assurance Monitoring) "
						+ "Plan for that control device.  The major source threshold for criteria pollutants is 100 "
						+ "tons; for an individual HAP is 10 tons; and for GHGs is 100,000 tons CO2e.",
				italicFont));

		ret.add(createOneLine("Operational Restrictions :", boldFont));
		ret.add(getOperationalRestrictions(eu.getOperationalRestrictions()));

		return ret;
	}

	private Table getPollutantLimits(
			java.util.List<TVEUPollutantLimit> pollutantLimits)
			throws BadElementException {
		Table ret = generateTable(10);
		float[] widths = { 1, 1, 1, 2, 1, 1, 1, 1, 1, 1 };
		ret.setWidths(widths);

		ret.addCell(createHeaderCell("Requirement Basis"));
		ret.addCell(createHeaderCell("Permit/Waiver/State Rule/Federal Standard Cite"));
		ret.addCell(createHeaderCell("Numeric Limit and Unit"));
		ret.addCell(createHeaderCell("Pollutant"));
		ret.addCell(createHeaderCell("Averaging Period"));
		ret.addCell(createHeaderCell("In Compliance?"));
		ret.addCell(createHeaderCell("Pre-Controlled Potential Emissions (tons/yr)"));
		ret.addCell(createHeaderCell("Basis for Determination"));
		ret.addCell(createHeaderCell("Subject to CAM*"));
		ret.addCell(createHeaderCell("Method to Determine Compliance"));

		for (TVEUPollutantLimit pollutantLimit : pollutantLimits) {
			ret.addCell(createDataCell(TvReqBasisDef.getData().getItems()
					.getItemDesc(pollutantLimit.getReqBasisCd())));
			ret.addCell(createDataCell(toString(pollutantLimit.getRuleCite())));
			ret.addCell(createDataCell(toString(pollutantLimit
					.getNumLimitUnit())));
			ret.addCell(createDataCell(PollutantDef.getData().getItems()
					.getItemDesc(pollutantLimit.getPollutantCd())));
			ret.addCell(createDataCell(toString(pollutantLimit.getAvgPeriod())));
			ret.addCell(createDataCell(toYesNo(pollutantLimit
					.getComplianceFlag())));

			// TODO -- format may need to change
			ret.addCell(createDataCell(toPOTENTIAL_EMISSIONS_FORMAT(pollutantLimit
					.getPotentialEmissions())));
			ret.addCell(createDataCell(PATvEuPteDeterminationBasisDef.getData()
					.getItems()
					.getItemDesc(pollutantLimit.getDeterminationBasisCd())));
			ret.addCell(createDataCell(toYesNo(pollutantLimit.getCamFlag())));
			ret.addCell(createDataCell(toString(pollutantLimit
					.getComplianceMethod())));

		}

		return ret;
	}

	private Table getOperationalRestrictions(
			java.util.List<TVEUOperationalRestriction> operationalRestrictions)
			throws BadElementException {
		Table ret = generateTable(6);
		float[] widths = { 1, 1, 1, 1, 1, 1 };
		ret.setWidths(widths);

		ret.addCell(createHeaderCell("Requirement Basis"));
		ret.addCell(createHeaderCell("Permit/Waiver/State Rule/Federal Standard Cite"));
		ret.addCell(createHeaderCell("Restriction Type"));
		ret.addCell(createHeaderCell("Description of Restriction"));
		ret.addCell(createHeaderCell("In Compliance?"));
		ret.addCell(createHeaderCell("Method to Determine Compliance"));

		for (TVEUOperationalRestriction operationalRestriction : operationalRestrictions) {
			ret.addCell(createDataCell(TvReqBasisDef.getData().getItems()
					.getItemDesc(operationalRestriction.getReqBasisCd())));
			ret.addCell(createDataCell(toString(operationalRestriction
					.getRuleCite())));
			ret.addCell(createDataCell(TvRestrictionTypeDef.getData()
					.getItems()
					.getItemDesc(operationalRestriction.getRestrictionTypeCd())));
			ret.addCell(createDataCell(toString(operationalRestriction
					.getRestrictionDesc())));
			ret.addCell(createDataCell(toYesNo(operationalRestriction
					.getComplianceFlag())));
			ret.addCell(createDataCell(toString(operationalRestriction
					.getComplianceMethod())));
		}

		return ret;
	}

	private boolean isIEU(ApplicationEU eu) {
		String classCd = eu.getFpEU().getTvClassCd();
		return (classCd != null && classCd.equals(TVClassification.INSIG));
	}

	private boolean isPteSelectable(String tvApplicationPurposeCd) {
		return TVApplicationPurposeDef.INITIAL_APPLICATION
				.equals(tvApplicationPurposeCd)
				|| TVApplicationPurposeDef.RENEWAL
						.equals(tvApplicationPurposeCd);
	}
	
	private boolean isPermitReasonSPMSelectable() {
		return isPermitReasonSelectable() && PermitReasonsDef.SPM.equals(((TVApplication) application).getPermitReasonCd());
	}
	
	private boolean isPermitReasonSelectable() {
		return ((application instanceof TVApplication && TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING
				.equals(((TVApplication) application)
						.getTvApplicationPurposeCd())) || (application instanceof TIVApplication && TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING
				.equals(((TIVApplication) application).getAppPurposeCd())));
	}
	
	private boolean isPermitReasonReopeningSelectable() {
		return isPermitReasonSelectable() && PermitReasonsDef.REOPENING.equals(((TVApplication) application).getPermitReasonCd());
	}

	private String toPOTENTIAL_EMISSIONS_FORMAT(Double potentialEmissions) {
		return potentialEmissions == null ? null : POTENTIAL_EMISSIONS_FORMAT
				.format(potentialEmissions);
	}
}
