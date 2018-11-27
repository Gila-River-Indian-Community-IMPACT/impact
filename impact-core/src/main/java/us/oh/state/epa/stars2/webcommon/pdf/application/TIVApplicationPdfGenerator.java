package us.oh.state.epa.stars2.webcommon.pdf.application;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;

import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.TIVApplication;
import us.oh.state.epa.stars2.def.ApplicationType;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.TVApplicationPurposeDef;

@SuppressWarnings("serial")
public class TIVApplicationPdfGenerator extends ApplicationPdfGenerator {

	protected TIVApplicationPdfGenerator() {
		super();
	}

	public void printApplication(Application app) throws IOException,
			DocumentException {
		printTIVSectionI((TIVApplication) app);

		// use tree map to sort EUs by EPA EmuId
		TreeMap<String, ApplicationEU> euMap = new TreeMap<String, ApplicationEU>();
		for (ApplicationEU eu : app.getIncludedEus()) {
			euMap.put(eu.getFpEU().getEpaEmuId(), eu);
		}
		for (String epaEmuId : euMap.keySet()) {
			ApplicationEU eu = euMap.get(epaEmuId);
			printTIVSectionII((TIVApplication) app, eu);
		}
	}

	private void printTIVSectionI(TIVApplication tivApp) throws IOException,
			DocumentException {
		// HeaderFooter footer = new HeaderFooter(new
		// Phrase("Ohio EPA, Division of Air Pollution Control" +
		// "                          "
		// + " Page ", normalFont),
		// new Phrase( "                                      " +
		// "Title IV Acid Rain Application", normalFont));
		// footer.setAlignment(Element.ALIGN_CENTER);
		// footer.setBorder(0);
		document.setFooter(createFooter(tivApp));

		document.setPageCount(0);
		document.newPage();

		// add a title to the document
		String[] title = { "Division of Air Pollution Control",
				"Title IV Acid Rain Application", };

		Table titleTable = createTitleTable(title[0], title[1]);
		document.add(titleTable);

		// add "items" or questions
		List sections = new List(false, 20);
		sections.add(getReasonForApplication(tivApp));

		sections.add(getAttachments("Application Attachments",
				tivApp.getDocuments(), ""));
		// if need please create 'ApplicationType.TIV'

		document.add(sections);
	}

	private ListItem getReasonForApplication(TIVApplication tivApp)
			throws BadElementException {
		ListItem reasonItem = new ListItem();

		Paragraph reasonItemTitle = new Paragraph(defaultLeading,
				"Reason for Application", boldFont);
		reasonItem.add(reasonItemTitle);

		Paragraph purpose = new Paragraph(defaultLeading,
				"Please Identify the " + "reason for this application: ",
				normalFont);

		if (tivApp.getAppPurposeCd() != null) {
			purpose.add(new Phrase(defaultLeading,
					TVApplicationPurposeDef.getData().getItems()
							.getItemDesc(tivApp.getAppPurposeCd()), dataFont));
			if (tivApp.getAppPurposeCd().equals(
					TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING)) {
				if (tivApp.getReasonCd() != null) {
					purpose.add(new Phrase(defaultLeading, " - "
							+ PermitReasonsDef.getData().getItems()
									.getItemDesc(tivApp.getReasonCd()),
							dataFont));
				}
			}
			reasonItem.add(purpose);
		}

		Paragraph reasonText = new Paragraph(
				defaultLeading,
				"Please summarize the "
						+ "reason this permit is being applied for. This text will be in the "
						+ "public notice that will appear in the newspaper of the county where "
						+ "the facility is located ", normalFont);

		reasonItem.add(reasonText);
		reasonItem.add(createTextTable(tivApp.getApplicationDesc()));

		return reasonItem;
	}

	private void printTIVSectionII(TIVApplication tivApp, ApplicationEU eu)
			throws IOException, DocumentException {
		List sections = new List(false, 20);
		sections.add(getEUHeader(eu));
		document.add(sections);

	}

	private ListItem getEUHeader(ApplicationEU eu) throws BadElementException {
		ListItem item = new ListItem();
		String headerTitle = "Emissions Unit";

		Paragraph itemTitle = new Paragraph(defaultLeading, headerTitle,
				boldFont);
		item.add(itemTitle);

		LinkedHashMap<String, String> nameValueMap = new LinkedHashMap<String, String>();
		nameValueMap.put("Ohio EPA EU ID : ", eu.getFpEU().getEpaEmuId());
		nameValueMap
				.put("Ohio EPA EU description : ", eu.getFpEU().getEuDesc());
		nameValueMap.put("Company EU ID : ", eu.getFpEU().getCompanyId());
		nameValueMap.put("Company EU description : ", eu.getFpEU()
				.getRegulatedUserDsc());
		nameValueMap.put("ORIS Boiler ID : ", eu.getFpEU().getOrisBoilerId());

		Table headerTable = createNameValueTable(nameValueMap, 1);
		headerTable.setWidth(100);
		float[] widths = { 2, 6 };
		headerTable.setWidths(widths);
		item.add(headerTable);

		return item;
	}
}
