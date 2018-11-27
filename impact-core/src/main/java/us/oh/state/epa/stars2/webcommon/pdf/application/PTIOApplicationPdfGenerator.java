package us.oh.state.epa.stars2.webcommon.pdf.application;

import java.io.IOException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.bo.ApplicationBO;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUEmissions;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUFugitiveLeaks;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUMaterialUsed;
import us.oh.state.epa.stars2.database.dbObjects.application.NSRApplicationBACTEmission;
import us.oh.state.epa.stars2.database.dbObjects.application.NSRApplicationLAEREmission;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeAMN;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeAPT;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeBOL;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeCCU;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeCKD;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeCMX;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeCSH;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeCTW;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeDHY;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeEGU;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeENG;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeFLR;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeFUG;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeHET;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeHMA;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeINC;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeLUD;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypePNE;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeSEB;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeSEP;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeSRU;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeTGT;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeTNK;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeVNT;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.def.AppEUAPTUnitTypeDef;
import us.oh.state.epa.stars2.def.AppEUAmineTypeDef;
import us.oh.state.epa.stars2.def.AppEUBOLServiceTypeDef;
import us.oh.state.epa.stars2.def.AppEUBoilerTypeDef;
import us.oh.state.epa.stars2.def.AppEUBtuUnitsDef;
import us.oh.state.epa.stars2.def.AppEUCCUEquipTypeDef;
import us.oh.state.epa.stars2.def.AppEUCirculationPumpTypeDef;
import us.oh.state.epa.stars2.def.AppEUCirculationRateDef;
import us.oh.state.epa.stars2.def.AppEUCoalUsageTypeDef;
import us.oh.state.epa.stars2.def.AppEUCrusherTypeDef;
import us.oh.state.epa.stars2.def.AppEUENGTierRatingDef;
import us.oh.state.epa.stars2.def.AppEUEquipServiceTypeDef;
import us.oh.state.epa.stars2.def.AppEUFUGEmissionTypeDef;
import us.oh.state.epa.stars2.def.AppEUFlowRateUnitsDef;
import us.oh.state.epa.stars2.def.AppEUFuelConsumptionDef;
import us.oh.state.epa.stars2.def.AppEUFuelGasHeatingDef;
import us.oh.state.epa.stars2.def.AppEUFuelSulfurDef;
import us.oh.state.epa.stars2.def.AppEUINCPriFuelTypeDef;
import us.oh.state.epa.stars2.def.AppEUIgnitionDeviceTypeDef;
import us.oh.state.epa.stars2.def.AppEULUDThroughputUnitsDef;
import us.oh.state.epa.stars2.def.AppEUMateProcessedTypeDef;
import us.oh.state.epa.stars2.def.AppEUMateUsageDef;
import us.oh.state.epa.stars2.def.AppEUMaterialTypeDef;
import us.oh.state.epa.stars2.def.AppEUMotiveForceDef;
import us.oh.state.epa.stars2.def.AppEUNEGServiceTypeDef;
import us.oh.state.epa.stars2.def.AppEUPowerSourceDef;
import us.oh.state.epa.stars2.def.AppEUProductionHourRateDef;
import us.oh.state.epa.stars2.def.AppEUProductionRateDef;
import us.oh.state.epa.stars2.def.AppEUScreenDef;
import us.oh.state.epa.stars2.def.AppEUStockpileSizeDef;
import us.oh.state.epa.stars2.def.AppEUStockpileTypeDef;
import us.oh.state.epa.stars2.def.AppEUStrippingGasSourceDef;
import us.oh.state.epa.stars2.def.AppEUTNKThroughputUnitsDef;
import us.oh.state.epa.stars2.def.AppEUTurbineCycleTypeDef;
import us.oh.state.epa.stars2.def.AppEUWasteGasVolUnitsDef;
import us.oh.state.epa.stars2.def.ApplicationType;
import us.oh.state.epa.stars2.def.FacilityTypeDef;
import us.oh.state.epa.stars2.def.MaterialUsedDef;
import us.oh.state.epa.stars2.def.NSREuFedRuleAppDef;
import us.oh.state.epa.stars2.def.PAEuPteDeterminationBasisDef;
import us.oh.state.epa.stars2.def.PTIOApplicationEUPurposeDef;
import us.oh.state.epa.stars2.def.PTIOApplicationFacilityTypeDef;
import us.oh.state.epa.stars2.def.PTIOApplicationPurposeDef;
import us.oh.state.epa.stars2.def.PTIOApplicationSageGrouseDef;
import us.oh.state.epa.stars2.def.PTIOFedRuleAppl1Def;
import us.oh.state.epa.stars2.def.PTIOFedRuleAppl2Def;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.webcommon.converter.PhoneNumberConverter;
import us.wy.state.deq.impact.def.CshUnitTypeDef;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;
import us.wy.state.deq.impact.def.PAEuPTEUnitsDef;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;

@SuppressWarnings("serial")
public class PTIOApplicationPdfGenerator extends ApplicationPdfGenerator {

	PTIOApplicationPdfGenerator() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.webcommon.pdf.application.ApplicationPdfGenerator
	 * #printApplication
	 * (us.oh.state.epa.stars2.database.dbObjects.application.Application)
	 */
	protected void printApplication(Application app) throws IOException,
			DocumentException {
		printNSRApplication((PTIOApplication) app);

		for (ApplicationEU eu : app.getIncludedEus()) {
			printNSRApplicationEU((PTIOApplication) app, (PTIOApplicationEU) eu);
		}
	}

	/*
	 * PTIO - Section I
	 */
	private void printNSRApplication(PTIOApplication ptioApp)
			throws IOException, DocumentException {
		// define footer - NOTE: footer must be added to document after
		// PdfWriter.getInstance and BEFORE document.open so footer will
		// appear on all pages (including first page)
		// HeaderFooter footer = new HeaderFooter(new
		// Phrase("Ohio EPA, Division of Air Pollution Control" +
		// "                          "
		// + " Page ", normalFont),
		// new Phrase( "                                      " +
		// "PTI/PTIO Application - Section I", normalFont));
		// footer.setAlignment(Element.ALIGN_CENTER);
		// footer.setBorder(0);
		// document.setHeader(createHeader(ptioApp));
		document.setFooter(createFooter(ptioApp));

		document.setPageCount(0);
		document.newPage();

		// add a title to the document
		String[] title = { "Air Quality Division",
				"Application for NSR Permit", };

		Table titleTable = createTitleTable(title[0], title[1]);
		document.add(titleTable);

		// add "items" or questions
		List sections = createItemList();
		sections.add(getNSRApplicationInfo(ptioApp));
		sections.add(getPurposeOfApplication(ptioApp));
		sections.add(getFederalRulesApplicability(ptioApp));
		
		if (!isPublic()) {
			sections.add(getTradeSecretInformation(ptioApp));
			sections.add(getPermitApplicationContact(ptioApp));
		}
		sections.add(getModelingSection(ptioApp));
		sections.add(getAttachments("Application Attachments",
				ptioApp.getDocuments(), ApplicationType.PTIO));

		if (isInternal()) {
			sections.add(generateNotes(ptioApp.getApplicationNotes()));
		}

		document.add(sections);
	}

	private ListItem getNSRApplicationInfo(PTIOApplication ptioApp)
			throws BadElementException {

		ListItem ret = new ListItem();

		ret.add(createItemTitle("NSR Application", ""));

		if (ptioApp.isApplicationCorrected() || isInternal()) {
			Table infoTable = generateQATableShortQ();
			if (ptioApp.isApplicationCorrected()
					&& !ptioApp.isApplicationAmended()) {
				generateQAColumns(infoTable,
						"Correction to application number :",
						ptioApp.getPreviousApplicationNumber());
				generateQAColumns(infoTable, "Reason for correction :",
						ptioApp.getApplicationCorrectedReason());
			}
			if (ptioApp.isApplicationAmended()) {
				generateQAColumns(infoTable,
						"Amendment to application number :",
						ptioApp.getPreviousApplicationNumber());
			}

			if (isInternal()) {
				generateQAColumns(infoTable, "Date application received :",
						toDateFormat1(ptioApp.getReceivedDate()));
				generateQAColumns(infoTable,
						"Is this a legacy NSR Application?",
						toYesNo(ptioApp.isLegacyStatePTOApp()));
				generateQAColumns(infoTable,
						"Is this a known incomplete NSR Application?",
						toYesNo(ptioApp.isKnownIncompleteNSRApp()));
			}
			ret.add(infoTable);
		}

		// add text explaining application document
		Table appExplTable = createTextTable(
				defaultLeading,
				"This information should be filled out for each New Source Review (NSR) application. "
						+ "An NSR permit is required for all air contaminant sources (emissions units) installed or "
						+ "modified after January 1, 1974. See the application instructions for additional information.",
				italicFont);
		// appExplTable.setPadding(5);
		appExplTable.setWidth(100);
		ret.add(appExplTable);

		if (isInternal()) {
			// add "Emission Unit application reason summary." table
			ret.add(getOepaUseTable(ptioApp));

			Table tempTable = generateQATableShortQ();
			generateQAColumns(tempTable, "Facility Type :",
					PTIOApplicationFacilityTypeDef.getData().getItems()
						.getItemDesc(ptioApp.getPotentialTitleVFlag()));
			generateQAColumns(tempTable, "Sage Grouse :",
					PTIOApplicationSageGrouseDef.getData().getItems()
							.getItemDesc(ptioApp.getSageGrouseCd()));
			if (isEqual(ptioApp.getSageGrouseCd(), "0")) {  
				generateQAColumns(tempTable, "Name of Agency/Department :",
						ptioApp.getSageGrouseAgencyName());
			}
			ret.add(tempTable);
		}
		return ret;

	}

	private Table getOepaUseTable(PTIOApplication app)
			throws BadElementException {
		Table oepaTable = generateTable(5);
		float[] widths = { 53, 4, 22, 4, 30 };
		oepaTable.setWidths(widths);
		oepaTable.setSpacing(3);

		Cell titleCell = createCell(
				"Emission Unit application reason summary :", 0);
		titleCell.setRowspan(3);

		oepaTable.addCell(titleCell);

		java.util.List<String> purposeCds = app.getApplicationPurposeCDs();

		Cell checkCell = createCheckCell(purposeCds
				.contains(PTIOApplicationPurposeDef.CONSTRUCTION));
		Cell textCell = createCell("Construction", 0);
		oepaTable.addCell(checkCell);
		oepaTable.addCell(textCell);

		checkCell = createCheckCell(purposeCds
				.contains(PTIOApplicationPurposeDef.SYNTHETIC_MINOR));
		textCell = createCell("Synthetic Minor", 0);
		oepaTable.addCell(checkCell);
		oepaTable.addCell(textCell);

		checkCell = createCheckCell(purposeCds
				.contains(PTIOApplicationPurposeDef.MODIFICATION));
		textCell = createCell("Modification", 0);
		oepaTable.addCell(checkCell);
		oepaTable.addCell(textCell);

		checkCell = createCheckCell(purposeCds
				.contains(PTIOApplicationPurposeDef.TEMPORARY_PERMIT));
		textCell = createCell("Temporary Permit", 0);
		oepaTable.addCell(checkCell);
		oepaTable.addCell(textCell);

		checkCell = createCheckCell(purposeCds
				.contains(PTIOApplicationPurposeDef.RECONSTRUCTION));
		textCell = createCell("Reconstruction", 0);
		oepaTable.addCell(checkCell);
		oepaTable.addCell(textCell);

		checkCell = createCheckCell(purposeCds
				.contains(PTIOApplicationPurposeDef.OTHER));
		textCell = createCell("Other", 0);
		oepaTable.addCell(checkCell);
		oepaTable.addCell(textCell);

		if (purposeCds.contains(PTIOApplicationPurposeDef.OTHER)) {

			textCell = createCell("Please explain :", 0);
			textCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			oepaTable.addCell(textCell);

			textCell = createCell(toString(app.getOtherPurposeDesc()), 0);
			textCell.setColspan(4);
			oepaTable.addCell(textCell);

		}

		return oepaTable;
	}

	private ListItem getPurposeOfApplication(PTIOApplication ptioApp)
			throws BadElementException {
		ListItem ret = new ListItem();

		ret.add(createItemTitle("Purpose of Application", ""));

		ret.add(createOneLine(
				"Please summarize the reason this permit is being applied for.",
				normalFont));
		ret.add(createTextTable(ptioApp.getApplicationDesc()));

		Table tempTable = generateQATableLongQ();
		generateQAColumns(
				tempTable,
				"Has the facility changed location or is it a new/greenfield facility?",
				toYesNo(ptioApp.getFacilityChangedLocationFlag()));

		if (isEqual(ptioApp.getFacilityChangedLocationFlag(), "Y")) {
			generateQAColumns(
					tempTable,
					"Has a Land Use Planning document been included in this application?",
					toYesNo(ptioApp.getLandUsePlanningFlag()));
		}

		generateQAColumns(tempTable,
				"Does production at this facility contain H2S?",
				toYesNo(ptioApp.getContainH2SFlag()));
		if (isEqual(ptioApp.getContainH2SFlag(), "Y")) {
			generateQAColumns(
					tempTable,
					"Has the Division been contacted regarding this application?",
					toYesNo(ptioApp.getDivisionContacedFlag()));
		}

		ret.add(tempTable);

		return ret;
	}

	private ListItem getFederalRulesApplicability(PTIOApplication ptioApp)
			throws BadElementException {
		ListItem ret = new ListItem();

		ret.add(createItemTitle("Federal Rules Applicability - Facility Level",
				""));

		Table fedRulesTable = generateQATable();
		// PSD
		fedRulesTable.addCell(createCellwihtDes(
				"Prevention of Significant Deterioration (PSD)",
				"These rules are found under WAQSR Chapter 6, Section 4."));
		fedRulesTable.addCell(createDataCell(PTIOFedRuleAppl2Def.getData()
				.getItems().getItemDesc(ptioApp.getPsdApplicableFlag()), 0));

		// NSR
		fedRulesTable.addCell(createCellwihtDes(
				"Non-Attainment New Source Review",
				"These rules are found under WAQSR Chapter 6, Section 13."));
		fedRulesTable.addCell(createDataCell(PTIOFedRuleAppl2Def.getData()
				.getItems().getItemDesc(ptioApp.getNsrApplicableFlag()), 0));

		ret.add(fedRulesTable);
		return ret;
	}

	private ListItem getTradeSecretInformation(PTIOApplication ptioApp)
			throws BadElementException {
		ListItem ret = new ListItem();

		ret.add(createItemTitle(
				"Trade Secret Information",
				" - One or more Emissions Units in this application contains trade secret information."));

		boolean hasTradeSecret = false;
		for (ApplicationDocumentRef docRef : ptioApp.getDocuments()) {
			if (docRef.getTradeSecretDocId() != null) {
				hasTradeSecret = true;
				containsTradeSecret = true;
				break;
			}
		}
		if (!hasTradeSecret) {
			APP_EU_LOOP: for (ApplicationEU appEU : ptioApp.getEus()) {
				for (ApplicationDocumentRef docRef : appEU.getEuDocuments()) {
					if (docRef.getTradeSecretDocId() != null) {
						hasTradeSecret = true;
						containsTradeSecret = true;
						break APP_EU_LOOP;
					}
				}

			}
		}
		ret.add(new Paragraph(defaultLeading, toYesNo(hasTradeSecret), dataFont));

		return ret;
	}

	private ListItem getPermitApplicationContact(PTIOApplication ptioApp)
			throws BadElementException {
		ListItem ret = new ListItem();

		ret.add(createItemTitle(
				"Permit Application Contact",
				" - Newly created contacts and application contact changes will be saved when the application is saved."));

		if (ptioApp.getContact() != null) {
			Contact contact = ptioApp.getContact();

			Table contactTable = generateTable(3);
			contactTable.setBorder(0);

			String nameStr = contact.getFirstNm() == null ? "" : contact
					.getFirstNm() + " ";
			nameStr += contact.getLastNm() == null ? "" : contact.getLastNm();
			Cell nameCell = createDataCell(nameStr, 0);
			nameCell.setBorderWidthBottom(1);
			contactTable.addCell(nameCell);

			Cell titleCell = createDataCell(contact.getCompanyTitle(), 0);
			titleCell.setBorderWidthBottom(1);
			contactTable.addCell(titleCell);

			Cell companyCell = createDataCell(contact.getCompanyName(), 0);
			companyCell.setBorderWidthBottom(1);
			contactTable.addCell(companyCell);

			Cell nameLabelCell = createCell("Name", 0);
			contactTable.addCell(nameLabelCell);

			Cell titleLabelCell = createCell("Title", 0);
			contactTable.addCell(titleLabelCell);

			Cell companyLabelCell = createCell("Company", 0);
			contactTable.addCell(companyLabelCell);

			String streetStr = contact.getAddress().getAddressLine1();
			if (contact.getAddress().getAddressLine2() != null) {
				streetStr += contact.getAddress().getAddressLine2();
			}
			Cell streetCell = createDataCell(streetStr);
			streetCell.setBorder(0);
			streetCell.setBorderWidthBottom(1);
			contactTable.addCell(streetCell);

			StringBuffer cityState = new StringBuffer();
			if (contact.getAddress().getCityName() != null) {
				cityState.append(contact.getAddress().getCityName());
			}
			if (contact.getAddress().getState() != null) {
				cityState.append(", " + contact.getAddress().getState());
			}
			Cell cityStateCell = createDataCell(cityState.toString(), 0);
			cityStateCell.setBorderWidthBottom(1);
			contactTable.addCell(cityStateCell);

			String zip = null;
			if (contact.getAddress().getZipCode5() != null) {
				zip = contact.getAddress().getZipCode5();
			}
			Cell zipCell = createDataCell(zip, 0);
			zipCell.setBorderWidthBottom(1);
			contactTable.addCell(zipCell);

			Cell streetLabelCell = createCell("Street Address", 0);
			contactTable.addCell(streetLabelCell);

			Cell cityLabelCell = createCell("City/Township, State", normalFont,
					0);
			contactTable.addCell(cityLabelCell);

			Cell zipLabelCell = createCell("Zip Code", 0);
			contactTable.addCell(zipLabelCell);

			PhoneNumberConverter phoneConvert = new PhoneNumberConverter();
			Cell phoneCell = createDataCell(
					phoneConvert.tryFormatPhoneNumber(contact.getPhoneNo()), 0);
			phoneCell.setBorderWidthBottom(1);
			contactTable.addCell(phoneCell);

			Cell faxCell = createDataCell(
					phoneConvert.tryFormatPhoneNumber(contact.getFaxNo()), 0);
			faxCell.setBorderWidthBottom(1);
			contactTable.addCell(faxCell);

			Cell emailCell = createDataCell(contact.getEmailAddressTxt(), 0);
			emailCell.setBorderWidthBottom(1);
			contactTable.addCell(emailCell);

			Cell phoneLabelCell = createCell("Phone", 0);
			contactTable.addCell(phoneLabelCell);

			Cell faxLabelCell = createCell("Fax", 0);
			contactTable.addCell(faxLabelCell);

			Cell emailLabelCell = createCell("E-mail", 0);
			contactTable.addCell(emailLabelCell);

			Cell emailCell2 = createDataCell(contact.getEmailAddressTxt2(), 0);
			emailCell2.setBorderWidthBottom(1);
			contactTable.addCell(emailCell2);

			// creating 2 empty cells for formatting purpose.
			// Can be replaced with new fields in future.
			Cell empty1 = createDataCell("", 0);
			contactTable.addCell(empty1);
			Cell empty2 = createDataCell("", 0);
			contactTable.addCell(empty2);

			Cell emailLabelCell2 = createCell("Secondary E-mail", 0);
			contactTable.addCell(emailLabelCell2);

			ret.add(contactTable);
		}

		return ret;
	}

	private ListItem getModelingSection(PTIOApplication ptioApp)
			throws BadElementException {
		ListItem ret = new ListItem();
		ret.add(createItemTitle("Modeling Section", ""));

		Table appExplTable = createTextTable(
				defaultLeading,
				"Ambient Air Quality Impact Analysis: WAQSR Chapter 6, Section 2(c)(ii) requires that "
						+ "permit applicants demonstrate that a proposed facility will not prevent the attainment "
						+ "or maintenance of any ambient air quality standard.",
				italicFont);
		ret.add(appExplTable);

		Table tempTable = generateQATableLongQ();
		generateQAColumns(
				tempTable,
				"Has the applicant contacted AQD to determine if modeling is required?",
				toYesNo(ptioApp.getModelingContactFlag()));
		generateQAColumns(tempTable,
				"Is a modeling analysis part of this application?",
				toYesNo(ptioApp.getModelingAnalysisFlag()));
		generateQAColumns(
				tempTable,
				"Is the proposed project subject to Prevention of Significant Deterioration (PSD) requirements?",
				toYesNo(ptioApp.getPreventionPsdFlag()));
		if (isEqual(ptioApp.getPreventionPsdFlag(), "Y")) {
			generateQAColumns(
					tempTable,
					"Has the Division been notified to schedule a pre-application meeting?",
					toYesNo(ptioApp.getPreAppMeetingFlag()));
			if (isEqual(ptioApp.getPreAppMeetingFlag(), "Y")) {
				generateQAColumns(
						tempTable,
						"Has a modeling protocol been submitted to and approved by the Division?",
						toYesNo(ptioApp.getModelingProtocolSubmitFlag()));
				generateQAColumns(
						tempTable,
						"Has the Division received a Q/D analysis to submit to the respective FLMs to determine the need for an AQRV analysis?",
						toYesNo(ptioApp.getAqrvAnalysisSubmitFlag()));
			}
		}
		ret.add(tempTable);

		return ret;
	}

	/*
	 * PTIO - Section II
	 */

	private void printNSRApplicationEU(PTIOApplication ptioApp,
			PTIOApplicationEU eu) throws IOException, DocumentException {
		// define footer - NOTE: footer must be added to document after
		// PdfWriter.getInstance and BEFORE document.open so footer will
		// appear on all pages (including first page)
		// HeaderFooter footer = new HeaderFooter(new
		// Phrase("Ohio EPA, Division of Air Pollution Control" +
		// "                          "
		// + " Page ", normalFont),
		// new Phrase( "                                      " +
		// "PTI/PTIO Application - Section II", normalFont));
		// footer.setAlignment(Element.ALIGN_CENTER);
		// footer.setBorder(0);
		document.setFooter(createFooter(ptioApp));

		// start each section II on a new page
		// document.setPageCount(0);
		document.newPage();

		// header (first page only)
		document.add(getNSREUHeader(eu));

		// // add text explaining application document
		// Table appExplTable = createTextTable(
		// defaultLeading,
		// "One copy of this section should be filled out for each air "
		// + "contaminant source (emissions unit) covered by this PTI/PTIO "
		// + "application identified in Section I, Question 5. See the "
		// + "application instructions for additional information.",
		// italicFont);
		// document.add(appExplTable);

		// add "items" or questions
		List sections = createItemList();
		sections.add(getSourceInstallationOrModiflcationSchedule(ptioApp, eu));
		sections.add(getEmissionUnitTypeSpecificInformation(ptioApp, eu));
		sections.add(getPotentialOperatingSchedule(ptioApp, eu));
		sections.add(getEmissionsInformation(ptioApp, eu));
		sections.add(getBACT(ptioApp, eu));
		sections.add(getLAER(ptioApp, eu));
		sections.add(getFederalandStateRuleApplicability(ptioApp, eu));
		sections.add(getAttachments("Emission Unit Attachments",
				eu.getEuDocuments(), ApplicationType.PTIO));

		document.add(sections);
	}

	private Table getNSREUHeader(PTIOApplicationEU eu)
			throws BadElementException {

		Table header = generateTable(3);
		header.setBorder(0);
		float[] widths = { 50, 30, 20 };
		header.setWidths(widths);

		Cell blank = new Cell();
		blank.setBorder(0);

		Cell titleCell = createCell(
				"Section II - Specific Air Contaminant Source Information",
				boldFont, 0);
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

		return header;
	}

	private ListItem getSourceInstallationOrModiflcationSchedule(
			PTIOApplication ptioApp, PTIOApplicationEU eu)
			throws BadElementException {
		ListItem item = new ListItem();

		item.add(createItemTitle(
				"Source Installation or Modification Schedule",
				" – Select reason(s) for this emissions unit being included in this application "
						+ "(must be completed regardless of date of installation or modification):"));

		Table schedTable = generateTable(1);
		schedTable.setBorder(0);

		String purposeCd = eu.getPtioEUPurposeCD();
		String facilityTypeCd = ptioApp.getFacility().getFacilityTypeCd();
		if (purposeCd == null) {
			purposeCd = "";
		}

		schedTable.addCell(createCell(PTIOApplicationEUPurposeDef.getData()
				.getItems().getItemDesc(purposeCd), 0));

		if (isEuPurposeCDsRequireAfterPermitFlag(purposeCd, facilityTypeCd)) {
			if (isEuPurposeCDsRequireWorkDate(purposeCd, facilityTypeCd)) {
				schedTable.addCell(createCell(
						getEuPurposeCDWorkStartLabel(purposeCd), boldFont, 0));
				if (!eu.isWorkStartAfterPermit()) {
					schedTable.addCell(createCell(
							toDateFormat1(eu.getWorkStartDate()), 0));
				}
				if (eu.getWorkStartDate() == null) {
					schedTable.addCell(createCell(
							"After permit has been issued :"
									+ toYesNo(eu.isWorkStartAfterPermit()),
							boldFont, 0));
				}
			}
		} else {
			if (isEuPurposeCDsRequireWorkDate(purposeCd, facilityTypeCd)) {
				schedTable.addCell(createCell(
						getEuPurposeCDWorkStartLabel(purposeCd), boldFont, 0));
				schedTable.addCell(createCell(
						toDateFormat1(eu.getWorkStartDate()), 0));
			}
		}

		if (isEuPurposeCDsContainReconstruction(eu.getPtioEUPurposeCDs())) {
			schedTable.addCell(createCell("Please explain :", boldFont, 0));
			schedTable.addCell(createCell(eu.getReconstructionDesc(), 0));
		}

		if (isEuPurposeCDsContainOther(eu.getPtioEUPurposeCDs())) {
			schedTable.addCell(createCell("Please explain :", boldFont, 0));
			schedTable.addCell(createCell(eu.getModificationDesc(), 0));
		}

		item.add(schedTable);

		return item;
	}

	private String getEuPurposeCDWorkStartLabel(String purposeCd) {
		String label = null;
		if (purposeCd.contains(PTIOApplicationEUPurposeDef.CONSTRUCTION)) {
			label = "Date production began: ";
		} else if (purposeCd.contains(PTIOApplicationEUPurposeDef.MODIFICATION)) {
			label = "When will you begin to modify the air contaminant source? ";
		}
		return label;
	}

	private boolean isEuPurposeCDsContainOther(
			java.util.List<String> ptioEUPurposeCDs) {
		return ptioEUPurposeCDs.contains(PTIOApplicationEUPurposeDef.OTHER);
	}

	private boolean isEuPurposeCDsContainReconstruction(
			java.util.List<String> ptioEUPurposeCDs) {
		return ptioEUPurposeCDs
				.contains(PTIOApplicationEUPurposeDef.RECONSTRUCTION);
	}

	private boolean isEuPurposeCDsRequireWorkDate(String purposeCd,
			String facilityTypeCd) {
		return (purposeCd.contains(PTIOApplicationEUPurposeDef.CONSTRUCTION) && FacilityTypeDef
				.isOilAndGas(application.getFacility().getFacilityTypeCd()))
				|| purposeCd.contains(PTIOApplicationEUPurposeDef.MODIFICATION);
	}

	private boolean isEuPurposeCDsRequireAfterPermitFlag(String purposeCd,
			String facilityTypeCd) {
		return purposeCd.contains(PTIOApplicationEUPurposeDef.CONSTRUCTION)
				&& FacilityTypeDef.isOilAndGas(facilityTypeCd);
	}

	/* Start - getEmissionUnitTypeSpecificInformation */
	private ListItem getEmissionUnitTypeSpecificInformation(
			PTIOApplication ptioApp, PTIOApplicationEU eu)
			throws BadElementException {
		ListItem ret = new ListItem();

		ret.add(createItemTitle("Emission Unit Type Specific Information", ""));
		String emissionUnitTypeCd = eu.getFpEU().getEmissionUnitTypeCd();

		Table table = generateTable(4);
		table.setBorder(0);

		String temp;
		String temp1;
		temp = EmissionUnitTypeDef.getData().getItems()
				.getItemDesc(emissionUnitTypeCd);
		generate2Columns(table, "Emission Unit Type :", temp, false, null,
				null, false);

		if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.ENG)) {
			NSRApplicationEUTypeENG euType = (NSRApplicationEUTypeENG) eu
					.getEuType();
			temp = toVALUE_FORMAT(euType.getBtuContent());
			temp1 = AppEUBtuUnitsDef.getData().getItems()
					.getItemDesc(euType.getEngBtuUnitsCd());
			generate2Columns(table, "Btu Content :", temp, false, "Units :",
					temp1, false);

			temp = toVALUE_FORMAT(euType.getFuelSulfurContent());
			temp1 = AppEUFuelSulfurDef.getData().getItems()
					.getItemDesc(euType.getEngSulfarUnitsCd());
			generate2Columns(table, "Fuel Sulfur Content :", temp, false,
					"Units :", temp1, false);

			temp = AppEUNEGServiceTypeDef.getData().getItems()
					.getItemDesc(euType.getServiceType());
			generate2Columns(table, "Type of Service :", temp, false, null,
					null, false);

			if ("Diesel".equals(euType.getFpEuPrimaryFuelType()) ||
					"Diesel".equals(euType.getFpEuSecondaryFuelType())) {
				temp = euType.isDieselEngineEpaTierCertified()? "Yes" : "No";
				generate2Columns(table, "Is diesel engine EPA Tier Certified?", temp, false, null,
						null, false);
	
				temp = euType.getTierRating();
				temp1 = AppEUENGTierRatingDef.getData().getItems().getItemDesc(euType.getTierRating());
				generate2Columns(table, "Tier Rating :", temp1, false, null,
						null, false);
			}
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.BOL)) {
			NSRApplicationEUTypeBOL euType = (NSRApplicationEUTypeBOL) eu
					.getEuType();
			temp = AppEUBoilerTypeDef.getData().getItems()
					.getItemDesc(euType.getBoilerTypeCd());
			generate2Columns(table, "Boiler Type :", temp, false, null, null,
					false);

			temp = toVALUE_FORMAT(euType.getBtuContent());
			temp1 = AppEUBtuUnitsDef.getData().getItems()
					.getItemDesc(euType.getBolBtuUnitsCd());
			generate2Columns(table, "Btu Content :", temp, false, "Units :",
					temp1, false);

			temp = toVALUE_FORMAT(euType.getFuelSulfurContent());
			temp1 = AppEUFuelSulfurDef.getData().getItems()
					.getItemDesc(euType.getBolSulfurUnitsCd());
			generate2Columns(table, "Fuel Sulfur Content :", temp, false,
					"Units :", temp1, false);

			temp = toString(euType.getFuelAshContent());
			temp1 = AppEUBOLServiceTypeDef.getData().getItems()
					.getItemDesc(euType.getServiceTypeCd());
			generate2Columns(table, "Fuel Ash Content (%) :", temp, false,
					"Type of Service :", temp1, false);

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.HMA)) {
			NSRApplicationEUTypeHMA euType = (NSRApplicationEUTypeHMA) eu
					.getEuType();

			temp = toString(euType.getManufactureName());
			temp1 = toString(euType.getModelNameAndNum());
			generate2Columns(table, "Manufacturer Name :", temp, false,
					"Model Name and Number :", temp1, false);

			temp = toVALUE_FORMAT(euType.getFuelSulfurContent());
			temp1 = AppEUFuelSulfurDef.getData().getItems()
					.getItemDesc(euType.getHmaSulfurUnitsCd());
			generate2Columns(table, "Fuel Sulfur Content :", temp, false,
					"Units :", temp1, false);

			temp = toVALUE_FORMAT(euType.getFuelConsumption());
			temp1 = AppEUFuelConsumptionDef.getData().getItems()
					.getItemDesc(euType.getHmafuelConsUnitsCd());
			generate2Columns(table, "Fuel Consumption :", temp, false,
					"Units :", temp1, false);

			temp = toVALUE_FORMAT(euType.getFuelGasHeatingVal());
			temp1 = AppEUFuelGasHeatingDef.getData().getItems()
					.getItemDesc(euType.getFuelGasHeatingUnitsCd());
			generate2Columns(table, "Fuel Gas Heating Value :", temp, false,
					"Units :", temp1, false);

			temp = toVALUE_FORMAT(euType.getStackVolumetricFlow());
			generate2Columns(table, "Stack Volumetric Flow (dscfm) :", temp,
					false, null, null, false);

			temp = toYesNo(euType.getPlantProcessRecycledAsphaltCd());
			if (euType.isPlantProcessRecycledAsphalt()) {
				temp1 = toString(euType.getMaxRapPercent());
				generate2Columns(table, "Plant Processes Recycled Asphalt :",
						temp, false, "Maximum Percent RAP (%) :", temp1, false);
			} else {
				generate2Columns(table, "Plant Processes Recycled Asphalt :",
						temp, false, null, null, false);
			}

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.CMX)) {
			NSRApplicationEUTypeCMX euType = (NSRApplicationEUTypeCMX) eu
					.getEuType();

			Cell tempCell = createCell("Material Used :", 0);
			tempCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(tempCell);

			// Start-- Material Used table
			table.addCell(createHeaderCell("Material Used"));
			table.addCell(createHeaderCell("Amount*"));
			table.addCell(createHeaderCell("Units*"));

			for (ApplicationEUMaterialUsed mu : eu.getMaterialUsed()) {
				table.addCell(createCell("", 0));
				table.addCell(createDataCell(MaterialUsedDef.getData()
						.getItems().getItemDesc(mu.getMaterialUsedCd())));
				table.addCell(createNumberCell(mu.getMaterialAmount()));
				table.addCell(createDataCell(AppEUProductionRateDef.getData()
						.getItems().getItemDesc(mu.getUnitCd())));
			}
			// End-- Material Used table

			temp = toString(euType.getMaxAnnualProductionRate());
			temp1 = AppEUProductionRateDef.getData().getItems()
					.getItemDesc(euType.getMaxAnnualProductionRateUnitsCd());
			generate2Columns(table, "Maximum Annual Production Rate :", temp,
					false, "Units :", temp1, false);

			temp = toString(euType.getAvgHourlyProductionRate());
			temp1 = AppEUProductionHourRateDef.getData().getItems()
					.getItemDesc(euType.getAvgHourlyProductionRateUnitsCd());
			generate2Columns(table, "Average Hourly Production Rate :", temp,
					false, "Units :", temp1, false);

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.FUG)) {
			NSRApplicationEUTypeFUG euType = (NSRApplicationEUTypeFUG) eu
					.getEuType();

			String fugitiveCd = euType.getFugitiveEmissionTypeCd();

			temp = AppEUFUGEmissionTypeDef.getData().getItems()
					.getItemDesc(fugitiveCd);
			generate2Columns(table, "Type of Fugitive Emission :", temp, false,
					null, null, false);

			if (fugitiveCd != null) {
				if (fugitiveCd.equals(AppEUFUGEmissionTypeDef.HAUL_ROAD)) {

					temp = toVALUE_FORMAT(euType.getMaxDistanceMateriaHauled());
					generate2Columns(
							table,
							"Maximum Distance Material will be Hauled (or until Reaching Pavement) (miles) :",
							temp, false, null, null, false);

					temp = toString(euType.getTruckType1());
					temp1 = toString(euType.getType1TrucksCount());
					generate2Columns(table, "Truck Type 1 :", temp, false,
							"Number of Type 1 Trucks :", temp1, false);

					temp = toString(euType.getType1TrucksCapacity());
					temp1 = toString(euType.getType1TrucksEmptyWeight());
					generate2Columns(table,
							"Capacity of Type 1 Trucks (tons) :", temp, false,
							"Empty Weight of Type 1 Trucks (lbs) :", temp1,
							false);

					temp = toString(euType.getTruckType2());
					temp1 = toString(euType.getType2TrucksCount());
					generate2Columns(table, "Truck Type 2 :", temp, false,
							"Number of Type 2 Trucks :", temp1, false);

					temp = toString(euType.getType2TrucksCapacity());
					temp1 = toString(euType.getType2TrucksEmptyWeight());
					generate2Columns(table,
							"Capacity of Type 2 Trucks (tons) :", temp, false,
							"Empty Weight of Type 2 Trucks (lbs) :", temp1,
							false);

					temp = toString(euType.getTruckType3());
					temp1 = toString(euType.getType3TrucksCount());
					generate2Columns(table, "Truck Type 3 :", temp, false,
							"Number of Type 3 Trucks :", temp1, false);

					temp = toString(euType.getType3TrucksCapacity());
					temp1 = toString(euType.getType3TrucksEmptyWeight());
					generate2Columns(table,
							"Capacity of Type 3 Trucks (tons) :", temp, false,
							"Empty Weight of Type 3 Trucks (lbs) :", temp1,
							false);

					temp = toString(euType.getTruckType4());
					temp1 = toString(euType.getType4TrucksCount());
					generate2Columns(table, "Truck Type 4 :", temp, false,
							"Number of Type 4 Trucks :", temp1, false);

					temp = toString(euType.getType4TrucksCapacity());
					temp1 = toString(euType.getType4TrucksEmptyWeight());
					generate2Columns(table,
							"Capacity of Type 4 Trucks (tons) :", temp, false,
							"Empty Weight of Type 4 Trucks (lbs) :", temp1,
							false);

				} else if (fugitiveCd
						.equals(AppEUFUGEmissionTypeDef.EXPOSED_ACREAGE)) {

					temp = toVALUE_FORMAT(euType
							.getAcreageSubjectToWindErosion());
					generate2Columns(table,
							"Acreage Subject to Wind Erosion (acres) :", temp,
							false, null, null, false);

				} else if (fugitiveCd.equals(AppEUFUGEmissionTypeDef.STOCKPILE)) {

					temp = AppEUStockpileTypeDef.getData().getItems()
							.getItemDesc(euType.getStockPileTypeCd());
					generate2Columns(table, "Type of Stockpile :", temp, false,
							null, null, false);

					temp = toVALUE_FORMAT(euType
							.getMaterialAddedRemovedFromPileDay());
					temp1 = toVALUE_FORMAT(euType
							.getMaterialAddedRemovedFromPileYr());
					generate2Columns(table,
							"Material Added/Removed from Pile (tons/day) :",
							temp, false,
							"Material Added/Removed from Pile (tons/yr) :",
							temp1, false);

					temp = toString(euType.getStockPilesCount());
					generate2Columns(table, "Number of Stockpiles :", temp,
							false, null, null, false);

					temp = toString(euType.getStockPileSize());
					temp1 = AppEUStockpileSizeDef.getData().getItems()
							.getItemDesc(euType.getStockPileUnitCd());
					generate2Columns(table, "Size of Stockpile :", temp, false,
							"Units :", temp1, false);

				} else if (fugitiveCd.equals(AppEUFUGEmissionTypeDef.BLASTING)) {

					temp = toString(euType.getBlastsPerYearNumber());
					temp1 = toString(euType.getBlastingAgentUsedType());
					generate2Columns(table, "Number of Blasts per year :",
							temp, false, "Type of Blasting Agent Used :",
							temp1, false);

					temp = toVALUE_FORMAT(euType.getBlastingAgentUsedAmount());
					temp1 = toVALUE_FORMAT(euType.getBlastHorizontalArea());
					generate2Columns(table,
							"Amount of Blasting Agent Used (tons/yr) :", temp,
							false, "Horizontal Area of Blast (cu. ft) :",
							temp1, false);

					temp = AppEUMaterialTypeDef.getData().getItems()
							.getItemDesc(euType.getMaterialBlastedTypeCd());
					generate2Columns(table, "Type of material blasted :", temp,
							false, null, null, false);

				} else if (fugitiveCd
						.equals(AppEUFUGEmissionTypeDef.FIGUTIVE_LEAK_AT_OG)) {

					table.addCell(createHeaderCell("Equipment and Service Type"));
					table.addCell(createHeaderCell("Number of New or Modified Equipment Types"));
					table.addCell(createHeaderCell("Leak Rate (ppm)"));
					table.addCell(createHeaderCell("Percent VOC"));

					for (ApplicationEUFugitiveLeaks leak : eu
							.getApplicationEUFugitiveLeaks()) {
						table.addCell(createDataCell(AppEUEquipServiceTypeDef
								.getData().getItems()
								.getItemDesc(leak.getEquipmentServiceTypeCd())));
						table.addCell(createNumberCell(toString(leak
								.getEquipmentTypeNumber())));
						table.addCell(createNumberCell(toVALUE_FORMAT(leak
								.getLeakRate())));
						table.addCell(createNumberCell(toString(leak
								.getPercentVoc())));
					}

				} else if (fugitiveCd.equals(AppEUFUGEmissionTypeDef.OTHER)) {

					temp = toString(euType.getFugitiveSourceDescription());
					generate2Columns(table,
							"Detailed Description of Fugitive Source :", temp,
							false, null, null, false);
				}
			}

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.LUD)) {
			NSRApplicationEUTypeLUD euType = (NSRApplicationEUTypeLUD) eu
					.getEuType();

			temp = toString(euType.getMaxHourlyThroughput());
			temp1 = AppEULUDThroughputUnitsDef.getData().getItems()
					.getItemDesc(euType.getMaxHourlyThroughputUnitsCd());
			generate2Columns(table, "Maximum Hourly Throughput :", temp, false,
					"Units :", temp1, false);

			temp = toString(euType.getDetailedDescription());
			generate2Columns(table,
					"Detailed Description of Loading/Unloading/Dump Source :",
					temp, false, null, null, false);

			table.addCell(createCell("", 0));
			Cell tempCell = new Cell();
			tempCell.setBorder(0);
			tempCell.setColspan(3);
			tempCell.add(new Paragraph(
					defaultLeading,
					"*Provide detailed calculations documenting the potential emissions and emission factors used to calculate emissions from this source.",
					italicFont));
			table.addCell(tempCell);

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.TNK)) {

			NSRApplicationEUTypeTNK euType = (NSRApplicationEUTypeTNK) eu
					.getEuType();

			temp = toString(euType.getMaxHourlyThroughput());
			temp1 = AppEUTNKThroughputUnitsDef.getData().getItems()
					.getItemDesc(euType.getMaxHourlyThroughputUnitsCd());
			generate2Columns(table, "Maximum Hourly Throughput :", temp, false,
					"Units :", temp1, false);

			temp = toString(euType.getIsTankHeated());
			generate2Columns(table, "Is Tank Heated :", temp, false, null,
					null, false);

			temp = toVALUE_FORMAT(euType.getOperatingPressure());
			temp1 = toVALUE_FORMAT(euType.getVaporPressureOfMaterialStored());
			generate2Columns(table, "Operating Pressure (psig) :", temp, false,
					"Vapor Pressure of Material Stored (psig) :", temp1, false);

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.CSH)) {

			NSRApplicationEUTypeCSH euType = (NSRApplicationEUTypeCSH) eu
					.getEuType();

			temp = CshUnitTypeDef.getData().getItems()
					.getItemDesc(euType.getFpEuUnitType());
			generate2Columns(table, "Unit Type :", temp, false, null, null,
					false);

			if (euType.isCrushing()) {

				temp = toString(euType.getCrushedMaterialType());
				generate2Columns(table, "Type of Material Crushed :", temp,
						false, null, null, false);

				temp = AppEUCrusherTypeDef.getData().getItems()
						.getItemDesc(euType.getCrusherTypeCd());
				generate2Columns(table, "Type of Crusher :", temp, false, null,
						null, false);

				temp = toDateFormat1(euType.getCrusherManufactureDate());
				generate2Columns(table, "Manufacture Date :", temp, false,
						null, null, false);

				temp = AppEUPowerSourceDef.getData().getItems()
						.getItemDesc(euType.getCrusherPowerSourceCd());
				generate2Columns(table, "Power Source :", temp, false, null,
						null, false);

				temp = toString(euType.getMaxCrusherCapacity());
				generate2Columns(table, "Max Crusher Capacity (tons/hr) :",
						temp, false, null, null, false);

			} else if (euType.isScreening()) {

				temp = AppEUScreenDef.getData().getItems()
						.getItemDesc(euType.getScreenCd());
				generate2Columns(table, "Screen :", temp, false, null, null,
						false);

				temp = toString(euType.getScreenType());
				generate2Columns(table, "Screen Type :", temp, false, null,
						null, false);

				temp = toString(euType.getMaterialScreenedType());
				generate2Columns(table, "Type of Material Screened :", temp,
						false, null, null, false);

				temp = toDateFormat1(euType.getScreenManufactureDate());
				generate2Columns(table, "Manufacture Date :", temp, false,
						null, null, false);

				temp = AppEUPowerSourceDef.getData().getItems()
						.getItemDesc(euType.getScreenPowerSourceCd());
				generate2Columns(table, "Power Source :", temp, false, null,
						null, false);

				temp = toString(euType.getOperatingInConjunctionCd());
				generate2Columns(table,
						"Operating in Conjunction with a Crusher :", temp,
						false, null, null, false);

				if (euType.isOperatingInConjunction()) {
					temp = toString(euType.getMaxScreeingCapacity());
					generate2Columns(table,
							"Max Screening Capacity (tons/hr) :", temp, false,
							null, null, false);
				}

			} else if (euType.isHandling()) {

				temp = toString(euType.getConveyorTransferDropPoints());
				temp1 = toString(euType.getMaterialTransferredType());
				generate2Columns(table,
						"Number of Conveyor transfer and drop points :", temp,
						false, "Type of Material being Transferred :", temp1,
						false);

			} else if (euType.isOther()) {

				temp = toString(euType.getDetailedDescription());
				generate2Columns(table, "Detailed Description of Unit :", temp,
						false, null, null, false);

				table.addCell(createCell("", 0));
				Cell tempCell = new Cell();
				tempCell.setBorder(0);
				tempCell.setColspan(3);
				tempCell.add(new Paragraph(
						defaultLeading,
						"*Provide detailed calculations documenting the potential emissions and emission factors used to calculate emissions from this source.",
						italicFont));
				table.addCell(tempCell);

			}

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.FLR)) {

			NSRApplicationEUTypeFLR euType = (NSRApplicationEUTypeFLR) eu
					.getEuType();

			temp = toString(euType.getEmergencyFlareOnlyCd());
			temp1 = AppEUIgnitionDeviceTypeDef.getData().getItems()
					.getItemDesc(euType.getIgnitionDeviceTypeCd());
			generate2Columns(table, "Emergency Flare Only :", temp, false,
					"Ignition Device Type :", temp1, false);

			temp = toVALUE_FORMAT(euType.getBtuContent());
			temp1 = toString(euType.getSmokelessDesignCd());
			generate2Columns(table, "Btu Content (Btu/scf) :", temp, false,
					"Smokeless Design :", temp1, false);

			temp = toString(euType.getAssitGasUtilizedCd());
			generate2Columns(table, "Assist Gas Utilized :", temp, false, null,
					null, false);

			if (euType.isAssistGasUtilized()) {
				temp = toVALUE_FORMAT(euType.getAssistGasUtilizedBtu());
				generate2Columns(table, "BTU Content (BTU/scf) :", temp, false,
						null, null, false);
				temp = toVALUE_FORMAT(euType.getFuelSulfurContent());
				temp1 = AppEUFuelSulfurDef.getData().getItems()
						.getItemDesc(euType.getFuelSulfurContentUnitsCd());
				generate2Columns(table, "Fuel Sulfur Content :", temp, false,
						"Units :", temp1, false);
			}

			temp = toVALUE_FORMAT(euType.getWasteGasVolume());
			temp1 = AppEUWasteGasVolUnitsDef.getData().getItems()
					.getItemDesc(euType.getWasteGasVolumeUnitsCd());
			generate2Columns(table, "Waste Gas Volume :", temp, false,
					"Units :", temp1, false);

			temp = toDateFormat1(euType.getInstallationDate());
			generate2Columns(table, "Installation Date :", temp, false, null,
					null, false);

			temp = toString(euType.getContinuouslyMonitoredCd());
			generate2Columns(table, "Continuously Monitored :", temp, false,
					null, null, false);

			if (euType.isContinuouslyMonitored()) {
				temp = toString(euType.getContinuousMonitoringDesc());
				generate2Columns(table, "Describe Continuous Monitoring :",
						temp, false, null, null, false);
			}

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.INC)) {

			NSRApplicationEUTypeINC euType = (NSRApplicationEUTypeINC) eu
					.getEuType();

			temp = AppEUINCPriFuelTypeDef.getData().getItems()
					.getItemDesc(euType.getPrimaryFuelType());
			generate2Columns(table, "Primary Fuel Type :", temp, false, null,
					null, false);

			temp = toVALUE_FORMAT(euType.getBtuContent());
			temp1 = AppEUBtuUnitsDef.getData().getItems()
					.getItemDesc(euType.getBtuContentUnitsCd());
			generate2Columns(table, "Btu Content :", temp, false, "Units :",
					temp1, false);

			temp = toVALUE_FORMAT(euType.getFuelSulfurContent());
			temp1 = AppEUFuelSulfurDef.getData().getItems()
					.getItemDesc(euType.getFuelSulfurContentUnitsCd());
			generate2Columns(table, "Fuel Sulfur Content :", temp, false,
					"Units :", temp1, false);

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.DHY)) {

			NSRApplicationEUTypeDHY euType = (NSRApplicationEUTypeDHY) eu
					.getEuType();

			temp = toString(euType.getTemperatureOfWetGas());
			temp1 = toVALUE_FORMAT(euType.getWaterContentOfDryGas());
			generate2Columns(table, "Temperature of Wet Gas (F):", temp, false,
					"Water Content of Dry Gas (lbs H2O/MMscf):", temp1, false);

			temp = toVALUE_FORMAT(euType.getPressureOfWetGas());
			temp1 = toString(euType.getManufactureNameOfGlycolCircPump());
			generate2Columns(table, "Pressure of Wet Gas (psig):", temp, false,
					"Manufacturer Name of Glycol Circulation Pump:", temp1,
					false);

			temp = toVALUE_FORMAT(euType.getWaterContentOfWetGas());
			temp1 = toString(euType.getModelNameAndNoOfGlycolCircPump());
			generate2Columns(table,
					"Water Content of Wet Gas (lbs H2O/MMscf):", temp, false,
					"Model Name and Number of Glycol Circulation Pump:", temp1,
					false);

			temp = toVALUE_FORMAT(euType.getFlowRateOfDryGas());
			generate2Columns(table, "Flow Rate of Dry Gas (MMscfd):", temp,
					false, null, null, false);

			temp = AppEUCirculationPumpTypeDef.getData().getItems()
					.getItemDesc(euType.getTypeOfGlycolCirculationPumpCd());
			generate2Columns(table, "Type of Glycol Circulation Pump:", temp,
					false, null, null, false);

			if (euType.isGasClycolCirculationPump()) {
				temp = toVALUE_FORMAT(euType.getPumpVolumeRatio());
				temp1 = toString(euType.getActualLeanGlycolCirculationRate());
				generate2Columns(
						table,
						"Pump Volume Ratio (acfm/gpm):",
						temp,
						false,
						"Actual LEAN Glycol Circulation Rate (gallons/minute):",
						temp1, false);

				temp = toString(euType.getMaxLeanGlycolCirculationRate());
				temp1 = toString(euType.getSourceOfMotiveGasForPump());
				generate2Columns(
						table,
						"Maximum LEAN Glycol Circulation Rate (gallons/minute):",
						temp, false, "Source of Motive Gas for Pump:", temp1,
						false);

				temp = toYesNo(euType.getAdditionalGasStrippingCd());
				generate2Columns(table, "Additional Gas Stripping:", temp,
						false, null, null, false);

				if (isEqual(euType.getAdditionalGasStrippingCd(), "Y")) {
					temp = toVALUE_FORMAT(euType.getStrippingGasRate());
					temp1 = AppEUStrippingGasSourceDef.getData().getItems()
							.getItemDesc(euType.getSourceOfStrippingGasCd());
					generate2Columns(table, "Stripping Gas Rate (scf/minute):",
							temp, false, "Source of Stripping Gas:", temp1,
							false);
				}

				temp = toYesNo(euType.getIncludeGlycolFlashTankSeparatorCd());
				generate2Columns(table, "Include Glycol Flash Tank/Separator:",
						temp, false, null, null, false);

				if (isEqual(euType.getIncludeGlycolFlashTankSeparatorCd(), "Y")) {

					temp = toVALUE_FORMAT(euType.getFlashTankOffGasStream());
					temp1 = toString(euType.getOperatingTemperature());
					generate2Columns(table,
							"Flash Tank Off Gas Stream (scf/hr):", temp, false,
							"Operating Temperature (F):", temp1, false);

					temp = toString(euType.getFlashVaporsRouted());
					temp1 = toVALUE_FORMAT(euType.getOperatingPressure());
					generate2Columns(table, "Where are Flash vapors Routed?",
							temp, false, "Operating Pressure (psig):", temp1,
							false);

					temp = toYesNo(euType.getIsVesselHeatedCd());
					generate2Columns(table, "Is Vessel Heated:", temp, false,
							null, null, false);
				}
			}

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.HET)) {

			NSRApplicationEUTypeHET euType = (NSRApplicationEUTypeHET) eu
					.getEuType();

			temp = toVALUE_FORMAT(euType.getFuelsulfur());
			temp1 = AppEUFuelSulfurDef.getData().getItems()
					.getItemDesc(euType.getUnitsFuelSulfurCd());
			generate2Columns(table, "Fuel Sulfur Content :", temp, false,
					"Units :", temp1, false);

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.PNE)) {

			NSRApplicationEUTypePNE euType = (NSRApplicationEUTypePNE) eu
					.getEuType();
			
			generate2Columns(table, "Count of New or Modified Equipment :", toString(euType.getNewOrModifiedEqpCnt()),
					false, null, null, false);

			temp = AppEUMotiveForceDef.getData().getItems()
					.getItemDesc(euType.getMotiveForceCd());
			generate2Columns(table, "Motive Force :", temp, false, null, null,
					false);

			temp = toString(euType.getVoc());
			temp1 = toString(euType.getHap());
			generate2Columns(table, "VOC Content (%) :", temp, false,
					"HAP Content (%) :", temp1, false);

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.BVC)) {
			// No Emission Unit Type Specific Information
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.CCU)) {

			NSRApplicationEUTypeCCU euType = (NSRApplicationEUTypeCCU) eu
					.getEuType();

			temp = toString(euType.getRequestedChargeRate());
			temp1 = toVALUE_FORMAT(euType.getQuenchGasVolums());
			generate2Columns(table, "Requested Charge Rate (Barrels/day) :",
					temp, false, "Quench Gas Volume (scf/hr) :", temp1, false);

			temp = toYesNo(euType.getOdorMaskingAgentUsedCd());
			temp1 = toString(euType.getSulfurContentOfQuenchGas());
			generate2Columns(table, "Odor Masking Agent Used ?", temp, false,
					"Sulfur Content of Quench Gas (%) :", temp1, false);

			temp = toString(euType.getBatchCycleTime());
			temp1 = AppEUCCUEquipTypeDef.getData().getItems()
					.getItemDesc(euType.getTypeOfEquipmentCd());
			generate2Columns(table, "Batch Cycle Time (hr) :", temp, false,
					"Type of Equipment :", temp1, false);

			temp = toString(euType.getQuenchCycleTime());
			generate2Columns(table, "Quench Cycle Time (hr) :", temp, false,
					null, null, false);

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.CTW)) {

			NSRApplicationEUTypeCTW euType = (NSRApplicationEUTypeCTW) eu
					.getEuType();

			temp = toString(euType.getCellFlowRate());
			temp1 = toString(euType.getCirculationRate());
			generate2Columns(table, "Cell Flow Rate (cu. ft/min) :", temp,
					false, "Circulation Rate (gallons/min) :", temp1, false);

			temp = toString(euType.getVoc());
			temp1 = toString(euType.getHap());
			generate2Columns(table, "VOC Content (%) :", temp, false,
					"HAP Content (%) :", temp1, false);

			temp = toString(euType.getCellNumber());
			generate2Columns(table, "Number of cells :", temp, false, null,
					null, false);

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.AMN)) {

			NSRApplicationEUTypeAMN euType = (NSRApplicationEUTypeAMN) eu
					.getEuType();

			temp = toString(euType.getInletCO2Conc());
			temp1 = toString(euType.getInletH2SConc());
			generate2Columns(table, "Inlet CO2 Concentration (%) :", temp,
					false, "Inlet H2S Concentration (%) :", temp1, false);

			temp = toString(euType.getAcidGasCO2Conc());
			temp1 = toString(euType.getAcidGasH2SConc());
			generate2Columns(table, "Acid Gas CO2 Concentration (%) :", temp,
					false, "Acid Gas H2S Concentration (%) :", temp1, false);

			temp = toString(euType.getAmineCircRate());
			temp1 = AppEUCirculationRateDef.getData().getItems()
					.getItemDesc(euType.getAmineCircUnitsCd());
			generate2Columns(table, "Amine Circulation Rate :", temp, false,
					"Units :", temp1, false);

			temp = AppEUAmineTypeDef.getData().getItems()
					.getItemDesc(euType.getAmineTypeCd());
			generate2Columns(table, "Type of Amine :", temp, false, null, null,
					false);

			temp = toString(euType.getInletGasTemp());
			temp1 = toString(euType.getInletGasPressure());
			generate2Columns(table, "Temperature of Inlet Gas (F) :", temp,
					false, "Pressure of Inlet Gas (psig) :", temp1, false);

			temp = toString(euType.getOutletGasFlowRate());
			temp1 = toString(euType.getAcidGasFlowRate());
			generate2Columns(table, "Flow Rate of Outlet Gas (MMscf/day) :",
					temp, false, "Flow Rate of Acid Gas (MMscf/day) :", temp1,
					false);

			temp = AppEUCirculationPumpTypeDef.getData().getItems()
					.getItemDesc(euType.getAmineCircPumpTypeCd());
			if (euType.isCirculationPumpTypeGas()) {
				temp1 = toString(euType.getPumpVolumeRatio());
				generate2Columns(table, "Type of Amine Circulation Pump :",
						temp, false, "Pump Volume Ratio (acfm/gpm) :", temp1,
						false);
			} else {
				generate2Columns(table, "Type of Amine Circulation Pump :",
						temp, false, null, null, false);
			}

			temp = toString(euType.getMaxLeanAmineCircRate());
			temp1 = toString(euType.getActualLeanAmineCircRate());
			generate2Columns(table,
					"Maximum LEAN Amine Circulation Rate (gallons/minute) :",
					temp, false,
					"Actual LEAN Amine Circulation Rate (gallons/minute) :",
					temp1, false);

			temp = toString(euType.getMotiveGasPumpSource());
			generate2Columns(table, "Source of Motive Gas for Pump :", temp,
					false, null, null, false);

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.SRU)) {

			NSRApplicationEUTypeSRU euType = (NSRApplicationEUTypeSRU) eu
					.getEuType();

			temp = toString(euType.getInletSulfurConc());
			temp1 = toString(euType.getOutletSulfurConc());
			generate2Columns(table, "Inlet Sulfur Concentration (%) :", temp,
					false, "Outlet Sulfur Concentration (%) :", temp1, false);

			temp = toString(euType.getDesignCapacity());
			generate2Columns(table, "Design Capacity (MMscf/day) :", temp,
					false, null, null, false);

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.TGT)) {

			NSRApplicationEUTypeTGT euType = (NSRApplicationEUTypeTGT) eu
					.getEuType();

			temp = toString(euType.getExhaustFlowRate());
			generate2Columns(table, "Exhaust Flow Rate (acfm/hr) :", temp,
					false, null, null, false);

			temp = toString(euType.getInletSulfurConc());
			temp1 = toString(euType.getOutletSulfurConc());
			generate2Columns(table, "Inlet Sulfur Concentration (%) :", temp,
					false, "Outlet Sulfur Concentration (%) :", temp1, false);

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.VNT)) {

			NSRApplicationEUTypeVNT euType = (NSRApplicationEUTypeVNT) eu
					.getEuType();

			temp = toString(euType.getFlowRateThroughput());
			temp1 = AppEUFlowRateUnitsDef.getData().getItems()
					.getItemDesc(euType.getFlowRateThroughputUnitsCd());
			generate2Columns(table, "Flow Rate or Throughput :", temp, false,
					"Units :", temp1, false);

			temp = toString(euType.getVocConc());
			temp1 = toString(euType.getHapsConc());
			generate2Columns(table, "VOC Concentration (%) :", temp, false,
					"HAPs Concentration (%) :", temp1, false);

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.CKD)) {

			NSRApplicationEUTypeCKD euType = (NSRApplicationEUTypeCKD) eu
					.getEuType();

			temp = euType.isFuelFiredSource()? "Yes" : "No";
			generate2Columns(table, "Is this a fuel-fired source?", temp,
					false, null, null, false);

			if (euType.isFuelFiredSource()) {
				temp = toVALUE_FORMAT(euType.getBtu());
				temp1 = AppEUBtuUnitsDef.getData().getItems()
						.getItemDesc(euType.getUnitsBtuCd());
				generate2Columns(table, "Btu Content :", temp, false, "Units :",
						temp1, false);
	
				temp = toVALUE_FORMAT(euType.getFuelSulfur());
				temp1 = AppEUFuelSulfurDef.getData().getItems()
						.getItemDesc(euType.getUnitsFuelSulfurCd());
				generate2Columns(table, "Fuel Sulfur Content :", temp, false,
						"Units :", temp1, false);
	
			}
			temp = AppEUMateProcessedTypeDef.getData().getItems()
					.getItemDesc(euType.getTypeOfMaterialProcessedCd());
			generate2Columns(table, "Type of Material Processed :", temp,
					false, null, null, false);
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.APT)) {

			NSRApplicationEUTypeAPT euType = (NSRApplicationEUTypeAPT) eu
					.getEuType();

			temp = AppEUAPTUnitTypeDef.getData().getItems()
					.getItemDesc(euType.getUnitTypeCd());
			if (euType.isPrillTower()) {
				temp1 = toVALUE_FORMAT(euType.getExhaustFlowRate());
				generate2Columns(table, "Unit Type :", temp, false,
						"Exhaust Flow Rate (acfm) :", temp1, false);
			} else {
				generate2Columns(table, "Unit Type :", temp, false, null, null,
						false);
			}

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.EGU)) {

			NSRApplicationEUTypeEGU euType = (NSRApplicationEUTypeEGU) eu
					.getEuType();

			temp = toVALUE_FORMAT(euType.getBtu());
			temp1 = AppEUBtuUnitsDef.getData().getItems()
					.getItemDesc(euType.getUnitsBtuCd());
			generate2Columns(table, "Btu Content :", temp, false, "Units :",
					temp1, false);

			temp = toVALUE_FORMAT(euType.getFuelSulfur());
			temp1 = AppEUFuelSulfurDef.getData().getItems()
					.getItemDesc(euType.getUnitsFuelSulfurCd());
			generate2Columns(table, "Fuel Sulfur Content :", temp, false,
					"Units :", temp1, false);

			temp = toString(euType.getNetElectricalOutput());
			temp1 = toString(euType.getGrossElectricalOutput());
			generate2Columns(table, "Net Electrical Output (MW) :", temp,
					false, "Gross Electrical Output (MW) :", temp1, false);

			if (euType.isTurbine()) {
				temp = AppEUTurbineCycleTypeDef.getData().getItems()
						.getItemDesc(euType.getTurbineCycleTypeCd());
				generate2Columns(table, "Turbine Cycle Type :", temp, false,
						null, null, false);
			}

			if (euType.isCoal()) {
				temp = AppEUCoalUsageTypeDef.getData().getItems()
						.getItemDesc(euType.getCoalUsageTypeCd());
				generate2Columns(table, "Coal Usage Type :", temp, false, null,
						null, false);
			}

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.SEB)) {

			NSRApplicationEUTypeSEB euType = (NSRApplicationEUTypeSEB) eu
					.getEuType();

			temp = toString(euType.getFpEuUnitType());

			generate2Columns(table, "Unit Type :", temp, false, null, null,
					false);

			temp = toVALUE_FORMAT(euType.getMaterialUsage());
			temp1 = AppEUMateUsageDef.getData().getItems()
					.getItemDesc(euType.getUnitMaterialUsageCd());
			generate2Columns(table, "Material Usage :", temp, false, "Units :",
					temp1, false);

			temp = toString(euType.getVoc());
			temp1 = toString(euType.getHaps());
			generate2Columns(table, "VOC Content (%) :", temp, false,
					"HAP Content (%) :", temp1, false);

		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.SEP)) {

			NSRApplicationEUTypeSEP euType = (NSRApplicationEUTypeSEP) eu
					.getEuType();

			temp = toString(euType.getOperatingTemperature());
			temp1 = toVALUE_FORMAT(euType.getOperatingPressure());
			generate2Columns(table, "Operating Temperature (F) :", temp, false,
					"Operating Pressure (psig) :", temp1, false);

		}

		ret.add(table);
		return ret;
	}

	/* End - getEmissionUnitTypeSpecificInformation */

	/* Start - getPotentialOperatingSchedule */
	private ListItem getPotentialOperatingSchedule(PTIOApplication ptioApp,
			PTIOApplicationEU eu) throws BadElementException {
		ListItem ret = new ListItem();

		ret.add(createItemTitle("Potential Operating Schedule",
				" – Provide the operating schedule for this emissions unit"));
		Table tempTable = generateQATableShortQ();
		generateQAColumns(tempTable, "Hours/day :",
				toString(eu.getOpSchedHrsDay()));
		generateQAColumns(tempTable, "Hours/year :",
				toString(eu.getOpSchedHrsYr()));

		ret.add(tempTable);

		return ret;
	}

	/* End - getPotentialOperatingSchedule */

	/* Start - getEmissionsInformation */
	private ListItem getEmissionsInformation(PTIOApplication ptioApp,
			PTIOApplicationEU eu) throws BadElementException {
		ListItem ret = new ListItem();

		ret.add(createItemTitle(
				"Emissions Information",
				" \"Potential to emit\" means the maximum capacity of a source to emit any air pollutant under "
						+ "its physical and operational design. Any physical or operational limitation on the capacity of "
						+ "a source to emit an air pollutant, including air pollution control equipment and restrictions on "
						+ "hours of operation or on the type or amount of material combusted, stored or processed, shall be treated"
						+ " as part of its design if the limitation is enforceable by the EPA and the Division. This term does not "
						+ "alter or affect the use of this term for any other purposes under the Act, or the term “capacity factor” "
						+ "as used in Title IV of the Act or the regulations promulgated thereunder."));

		ret.add(createOneLine("Basis for Determination Options:", normalFont));
		List ptionList = createItemList();
		ptionList.add(createItem("Manufacturer Data", italicFont));
		ptionList.add(createItem("Test results for this source", italicFont));
		ptionList.add(createItem("Similar source test results", italicFont));
		ptionList.add(createItem("GRICalc", italicFont));
		ptionList.add(createItem("Tanks Program", italicFont));
		ptionList.add(createItem("AP-42", italicFont));
		ptionList
				.add(createItem(
						"Other.  If this is selected, attach a document with a description of the method used.",
						italicFont));

		ret.add(ptionList);

		// order CAP emissions
		ArrayList<ApplicationEUEmissions> orderedCapEmissions = new ArrayList<ApplicationEUEmissions>();
		for (String pollutantCd : ApplicationBO
				.getPTIOCapPollutantCodesOrdered()) {
			for (ApplicationEUEmissions emissions : eu.getCapEmissions()) {
				if (emissions.getPollutantCd().equals(pollutantCd)) {
					orderedCapEmissions.add(emissions);
				}
			}
		}
		ret.add(createOneLine("Criteria Pollutants :", boldFont));
		ret.add(createCriteriaPollutantsTable(orderedCapEmissions));

		ret.add(createOneLine(
				"Hazardous Air Pollutants (HAPs) and Toxic Air Contaminants:",
				boldFont));
		ret.add(createPollutantsTable(eu.getHapTacEmissions(), true));

		ret.add(createOneLine("Greenhouse Gases (GHGs):", boldFont));
		ret.add(createPollutantsTable(eu.getGhgEmissions(), false));

		ret.add(createOneLine(
				"* Provide your calculations as an attachment and explain how all process "
						+ "variables and emissions factors were selected. Note the emission factor(s) "
						+ "employed and document origin. Example: AP-42, Table 4.4-3 (8/97); stack test, "
						+ "Method 5, 4/96; mass balance based on MSDS; etc.",
				italicFont));

		ret.add(createOneLine(
				"** AQD Calculated - See 'Help' for more information.",
				italicFont));

		return ret;
	}

	private Table createCriteriaPollutantsTable(
			java.util.List<ApplicationEUEmissions> emissions)
			throws BadElementException {
		Table table = generateTable(7);
		float[] widths = { 2, 1, 1, 1, 1, 1, 1 };
		table.setWidths(widths);

		table.addCell(createHeaderCellRowspan("Pollutant", 2));
		table.addCell(createHeaderCellRowspan(
				"Pre-Controlled Potential Emissions (tons/yr)", 2));
		table.addCell(createHeaderCellColspan("Efficiency Standards", 2));
		table.addCell(createHeaderCellRowspan(
				"Potential to Emit (PTE) (lbs/hr)*", 2));
		table.addCell(createHeaderCellRowspan(
				"Potential to Emit (PTE) (tons/yr)*", 2));
		table.addCell(createHeaderCellRowspan("Basis for Determination*", 2));
		table.addCell(createHeaderCell("Potential to Emit (PTE)*"));
		table.addCell(createHeaderCell("Units*"));

		for (ApplicationEUEmissions e : emissions) {
			String pollutant = null;

			boolean isHap = isEqual(e.getHapCapPollutant(), "hapPollutant");

			pollutant = ApplicationBO.getPTIOCapPollutantDefs().get(
					e.getPollutantCd());
			table.addCell(createDataCell(pollutant));
			table.addCell(createDataCell(toEMISSIONS_VALUE_FORMAT(
					e.getPreCtlPotentialEmissions(), isHap)));
			table.addCell(createDataCell(toEMISSIONS_VALUE_FORMAT(
					e.getPotentialToEmit(), isHap)));
			table.addCell(createDataCell(PAEuPTEUnitsDef.getData().getItems()
					.getItemDesc(e.getUnitCd())));
			table.addCell(createDataCell(toEMISSIONS_VALUE_FORMAT(
					e.getPotentialToEmitLbHr(), isHap)));
			table.addCell(createDataCell(toEMISSIONS_VALUE_FORMAT(
					e.getPotentialToEmitTonYr(), isHap)));
			table.addCell(createDataCell(PAEuPteDeterminationBasisDef.getData()
					.getItems().getItemDesc(e.getPteDeterminationBasisCd())));

		}
		return table;
	}

	private Table createPollutantsTable(
			java.util.List<ApplicationEUEmissions> emissions, boolean isHap)
			throws BadElementException {
		Table table = generateTable(8);
		float[] widths = { 2, 1, 1, 1, 1, 1, 1, 1 };
		table.setWidths(widths);

		table.addCell(createHeaderCellRowspan("Pollutant", 2));
		table.addCell(createHeaderCellRowspan("Pollutant Category", 2));
		table.addCell(createHeaderCellRowspan(
				"Pre-Controlled Potential Emissions (tons/yr)", 2));
		table.addCell(createHeaderCellColspan("Efficiency Standards", 2));
		table.addCell(createHeaderCellRowspan(
				"Potential to Emit (PTE) (lbs/hr)*", 2));
		table.addCell(createHeaderCellRowspan(
				"Potential to Emit (PTE) (tons/yr)*", 2));
		table.addCell(createHeaderCellRowspan("Basis for Determination*", 2));
		table.addCell(createHeaderCell("Potential to Emit (PTE)*"));
		table.addCell(createHeaderCell("Units*"));

		for (ApplicationEUEmissions e : emissions) {
			String pollutant = null;
			pollutant = PollutantDef.getData().getItems()
					.getItemDesc(e.getPollutantCd());
			table.addCell(createDataCell(pollutant));
			table.addCell(createDataCell(e.getPollutantCategory()));
			table.addCell(createDataCell(toEMISSIONS_VALUE_FORMAT(
					e.getPreCtlPotentialEmissions(), isHap)));
			table.addCell(createDataCell(toEMISSIONS_VALUE_FORMAT(
					e.getPotentialToEmit(), isHap)));
			table.addCell(createDataCell(PAEuPTEUnitsDef.getData().getItems()
					.getItemDesc(e.getUnitCd())));
			table.addCell(createDataCell(toEMISSIONS_VALUE_FORMAT(
					e.getPotentialToEmitLbHr(), isHap)));
			table.addCell(createDataCell(toEMISSIONS_VALUE_FORMAT(
					e.getPotentialToEmitTonYr(), isHap)));
			table.addCell(createDataCell(PAEuPteDeterminationBasisDef.getData()
					.getItems().getItemDesc(e.getPteDeterminationBasisCd())));

		}
		return table;
	}

	/* End - getEmissionsInformation */

	/* Start - getBACT */
	private ListItem getBACT(PTIOApplication ptioApp, PTIOApplicationEU eu)
			throws BadElementException {
		ListItem ret = new ListItem();
		ret.add(createItemTitle("Best Available Control Technology (BACT)", ""));

		Table tempTable = generateQATable();
		if (ptioApp.isPsdSubjectToReg()) {
			generateQAColumns(tempTable, "Is this unit subject to PSD BACT? ",
					toYesNo(eu.getPsdBACTFlag()));
		}
		generateQAColumns(tempTable,
				"Was a BACT Analysis completed for this unit? ",
				toYesNo(eu.getBactFlag()));

		ret.add(tempTable);

		if (eu.isBactAnalysisCompleted()) {
			ret.add(createBACTTable(eu.getBactEmissions()));
		}

		return ret;
	}

	private Table createBACTTable(
			java.util.List<NSRApplicationBACTEmission> bacts)
			throws BadElementException {
		Table table = generateTable(2);

		table.addCell(createHeaderCell("Pollutant"));
		table.addCell(createHeaderCell("Proposed BACT"));

		for (NSRApplicationBACTEmission bact : bacts) {
			String pollutant = null;
			pollutant = PollutantDef.getData().getItems()
					.getItemDesc(bact.getPollutantCd());
			table.addCell(createDataCell(pollutant));
			table.addCell(createDataCell(bact.getBact()));
		}

		return table;
	}

	/* End - getBACT */

	/* Start - getLAER */
	private ListItem getLAER(PTIOApplication ptioApp, PTIOApplicationEU eu)
			throws BadElementException {
		ListItem ret = new ListItem();
		ret.add(createItemTitle("Lowest Achievable Emission Rate (LAER)", ""));

		Table tempTable = generateQATable();
		if (ptioApp.isNsrSubjectToReg()) {
			generateQAColumns(tempTable, "Is this unit subject to LAER? ",
					toYesNo(eu.getNsrLAERFlag()));
		}
		generateQAColumns(tempTable,
				"Was a LAER Analysis completed for this unit? ",
				toYesNo(eu.getLaerFlag()));

		ret.add(tempTable);

		if (eu.isLaerAnalysisCompleted()) {
			ret.add(createLAERTable(eu.getLaerEmissions()));
		}

		return ret;
	}

	private Table createLAERTable(
			java.util.List<NSRApplicationLAEREmission> laers)
			throws BadElementException {
		Table table = generateTable(2);

		table.addCell(createHeaderCell("Pollutant"));
		table.addCell(createHeaderCell("Proposed LAER"));

		for (NSRApplicationLAEREmission laer : laers) {
			String pollutant = null;
			pollutant = PollutantDef.getData().getItems()
					.getItemDesc(laer.getPollutantCd());
			table.addCell(createDataCell(pollutant));
			table.addCell(createDataCell(laer.getLaer()));
		}

		return table;
	}

	/* End - getLAER */

	/* Start - getFederalandStateRuleApplicability */
	private ListItem getFederalandStateRuleApplicability(
			PTIOApplication ptioApp, PTIOApplicationEU eu)
			throws BadElementException {
		ListItem ret = new ListItem();
		ret.add(createItemTitle("Federal and State Rule Applicability", ""));

		Table tempTable = generateQATable();

		// NSPS
		tempTable
				.addCell(createCellwihtDes(
						"New Source Performance Standards (NSPS)",
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
						"National Emission Standards for Hazardous Air Pollutants (NESHAP Part 61)",
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
						"National Emission Standards for Hazardous Air Pollutants (NESHAP Part 63)",
						"National Emission Standards for Hazardous Air Pollutants (NESHAP Part 63) standards are listed under 40 CFR 63."));
		tempTable.addCell(createDataCell(PTIOFedRuleAppl1Def.getData()
				.getItems().getItemDesc(eu.getMactApplicableFlag()), 0));
		ret.add(tempTable);
		if (isShowSubparts(eu.getMactApplicableFlag())) {
			ret.add(createMACTTable(eu.getMactSubpartCodes()));
		}

		// PSD
		tempTable = generateQATable();
		tempTable.addCell(createCellwihtDes(
				"Prevention of Significant Deterioration (PSD)",
				"These rules are found under WAQSR Chapter 6, Section 4."));
		tempTable.addCell(createDataCell(NSREuFedRuleAppDef.getData()
				.getItems().getItemDesc(eu.getPsdApplicableFlag()), 0));
		ret.add(tempTable);

		// Non-Attainment
		tempTable = generateQATable();
		tempTable.addCell(createCellwihtDes("Non-Attainment New Source Review",
				"These rules are found under WAQSR Chapter 6, Section 13."));
		tempTable.addCell(createDataCell(NSREuFedRuleAppDef.getData()
				.getItems().getItemDesc(eu.getNsrApplicableFlag()), 0));
		ret.add(tempTable);

		if (eu.getFedRulesExemption()) {
			ret.add(createOneLine(
					"Please explain why you checked “Subject, but exempt” in this question for one or more federal "
							+ "rules. Identify each exemption and whether the entire facility and/or the specific air "
							+ "contaminant sources included in this permit application is exempted. Attach an additional page if necessary.",
					boldFont));
			ret.add(createTextTable(eu.getFederalRuleApplicabilityExplanation()));
		}
		return ret;
	}

	/* End - getFederalandStateRuleApplicability */
}
