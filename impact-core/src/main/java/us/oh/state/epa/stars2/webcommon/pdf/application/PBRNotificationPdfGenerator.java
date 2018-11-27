package us.oh.state.epa.stars2.webcommon.pdf.application;

import java.io.IOException;
import java.util.LinkedHashMap;

import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;

import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.PBRNotification;
import us.oh.state.epa.stars2.database.dbObjects.application.PBRNotificationDocument;
import us.oh.state.epa.stars2.def.PBRNotifDocTypeDef;
import us.oh.state.epa.stars2.def.PBRReasonDef;
import us.oh.state.epa.stars2.def.PBRTypeDef;

@SuppressWarnings("serial")
public class PBRNotificationPdfGenerator extends ApplicationPdfGenerator {
    
    PBRNotificationPdfGenerator() {
        super();
    }
    
    /* (non-Javadoc)
     * @see us.oh.state.epa.stars2.webcommon.pdf.application.ApplicationPdfGenerator#printApplication(us.oh.state.epa.stars2.database.dbObjects.application.Application)
     */
    protected void printApplication(Application app) throws IOException, DocumentException {
        printPBRNotification((PBRNotification)app);
    }

    /*
     * 
     *          PTIO - Section I
     * 
     */
    private void printPBRNotification(PBRNotification pbr) throws IOException, DocumentException {
        // define footer - NOTE: footer must be added to document after
        // PdfWriter.getInstance and BEFORE document.open so footer will
        // appear on all pages (including first page)
//        HeaderFooter footer = new HeaderFooter(new Phrase("Ohio EPA, Division of Air Pollution Control" +
//                "                          " 
//                + " Page ", normalFont), 
//                new Phrase( "                                      " + 
//                        "Permit-by-rule Notification", normalFont));
//        footer.setAlignment(Element.ALIGN_CENTER);
//        footer.setBorder(0);
        document.setFooter(createFooter(pbr));
        
        document.setPageCount(0);
        document.newPage();
        
        // add a title to the document
        String[] title = {
                "Division of Air Pollution Control",
                "Permit-by-rule Notification",
        };

        Table titleTable = createTitleTable(title[0], title[1]);
        document.add(titleTable);

        List sections = new List(true, 20);

        ListItem item = new ListItem();
        item.setAlignment(Element.ALIGN_LEFT);
        item.add(createItemTitle("General Information", ""));
        
        item.add(new Paragraph(defaultLeading,
                "By requesting to install and/or operate under this " +
                "PBR you are authorizing Ohio EPA to revoke any applicable permits " +
                "associated with the air contaminant source(s) in this PBR notification.",
                normalFont));
        
        LinkedHashMap<String, String> pbrDataMap = new LinkedHashMap<String, String>();
        pbrDataMap.put("PBR Type: ", PBRTypeDef.getData().getItems().getItemDesc(pbr.getPbrTypeCd()));
        
        String disposition = "Not yet assigned";
        if (pbr.getDispositionFlag() != null) {
            if (pbr.getDispositionFlag().equals("r")) {
                disposition = "Notification Received";
            } else if (pbr.getDispositionFlag().equals("a")) {
                disposition = "Accepted";
            } else if (pbr.getDispositionFlag().equals("d")) {
                disposition = "Denied";
            } else if (pbr.getDispositionFlag().equals("s")) {
                disposition = "Superseded";
            }
        }
        pbrDataMap.put("PBR Reason: ", PBRReasonDef.getData().getItems().getItemDesc(pbr.getPbrReasonCd()));
        pbrDataMap.put("Disposition: ", disposition);
        Table pbrDataTable = createNameValueTable(pbrDataMap, 1);
        pbrDataTable.setWidth(98);
        float[] widths = {1,6};
        pbrDataTable.setWidths(widths);
        item.add(pbrDataTable);
        sections.add(item);
        
        if (pbr.getIncludedEus().size() > 0) {
            item = new ListItem();
            item.add(createItemTitle("Emission Units", ""));
            item.add(createEUTable(pbr));
            sections.add(item);
        }
        
        if (pbr.getPbrDocuments().size() > 0) {
            item = new ListItem();
            item.add(createItemTitle("Attachments", ""));
            item.add(createPBRDocumentTable(pbr.getPbrDocuments()));
            sections.add(item);
        }

        document.add(sections);
    }

    protected Table createPBRDocumentTable(java.util.List<PBRNotificationDocument> documents) throws BadElementException {
        Table table = new Table(3);
        table.setWidth(98);
        table.setPadding(2);
        float[] widths = {2,2,1};
        table.setWidths(widths);
        
        table.addCell(createHeaderCell("Description"));
        table.addCell(createHeaderCell("Type"));
        table.addCell(createHeaderCell("Document Id"));
        
        for (PBRNotificationDocument doc : documents) {
            // skip document if no public version is available
            // and trade secret information is hidden
            table.addCell(createDataCell(doc.getDescription()));
            table.addCell(createDataCell(
                    PBRNotifDocTypeDef.getData().getItems().getItemDesc(
                            doc.getDocTypeCd())));
            table.addCell(createDataCell(doc.getDocumentID().toString()));
        }
        
        return table;
    }
    
}
