package us.oh.state.epa.stars2.webcommon.pdf.application;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;

import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.RPCRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RPCRequestDocument;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitInfo;
import us.oh.state.epa.stars2.def.RPCRequestDocTypeDef;
import us.oh.state.epa.stars2.def.RPCTypeDef;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

@SuppressWarnings("serial")
public class RPCRequestPdfGenerator extends ApplicationPdfGenerator {
    private static Logger logger = Logger.getLogger(RPCRequestPdfGenerator.class);
    
    RPCRequestPdfGenerator() {
        super();
    }
    
    /* (non-Javadoc)
     * @see us.oh.state.epa.stars2.webcommon.pdf.application.ApplicationPdfGenerator#printApplication(us.oh.state.epa.stars2.database.dbObjects.application.Application)
     */
    protected void printApplication(Application app) throws IOException, DocumentException {
        printRPCRequest((RPCRequest)app);
    }

    /*
     * 
     *          PTIO - Section I
     * 
     */
    private void printRPCRequest(RPCRequest rpc) throws IOException, DocumentException {
        // define footer - NOTE: footer must be added to document after
        // PdfWriter.getInstance and BEFORE document.open so footer will
        // appear on all pages (including first page)
//        HeaderFooter footer = new HeaderFooter(new Phrase("Ohio EPA, Division of Air Pollution Control" +
//                "                          " 
//                + " Page ", normalFont), 
//                new Phrase( "                                      " + 
//                        "Request Administrative Permit Modification", normalFont));
//        footer.setAlignment(Element.ALIGN_CENTER);
//        footer.setBorder(0);
        document.setFooter(createFooter(rpc));
        
        document.setPageCount(0);
        document.newPage();
        
        // add a title to the document
        String[] title = {
                "Division of Air Pollution Control",
                "Request Administrative Permit Amendment",
        };

        Table titleTable = createTitleTable(title[0], title[1]);
        document.add(titleTable);

        List sections = new List(true, 20);

        ListItem item = new ListItem();
        item.setAlignment(Element.ALIGN_LEFT);
        item.add(createItemTitle("Amendment Request Information", ""));
        
        String permitNumber = null;
        
        try {
            PermitService permitBO = ServiceFactory.getInstance().getPermitService();
            PermitInfo permitInfo = permitBO.retrievePermit(rpc.getPermitId());
            permitNumber = permitInfo.getPermit().getPermitNumber();
        } catch (ServiceFactoryException e) {
            logger.error(e.getMessage(), e);
        }
        
        LinkedHashMap<String, String> rpcDataMap = new LinkedHashMap<String, String>();
        rpcDataMap.put("Modification Type: ", RPCTypeDef.getData().getItems().getItemDesc(rpc.getRpcTypeCd()));
        rpcDataMap.put("Permit to be modified: ", permitNumber);
        Table rpcDataTable = createNameValueTable(rpcDataMap, 1);
        rpcDataTable.setWidth(98);
        float[] widths = {2,6};
        rpcDataTable.setWidths(widths);
        item.add(rpcDataTable);
        
        Phrase reasonTitle = new Phrase(defaultLeading, 
                "Please summarize the reason this permit is being modified.", normalFont);
        Table reasonText = createTextTable(defaultLeading, rpc.getApplicationDesc(), dataFont);
        item.add(reasonTitle);
        item.add(reasonText);
        
        sections.add(item);
        
        if (rpc.getContact() != null) {
            item = new ListItem();
            item.setAlignment(Element.ALIGN_LEFT);
            item.add(createItemTitle("Modification Request Contact", ""));
            Table contactTable = createRPCContactTable(rpc.getContact());
            item.add(contactTable);
            sections.add(item);
        }
        
        if (rpc.getIncludedEus().size() > 0) {
            item = new ListItem();
            item.add(createItemTitle("Emission Units", ""));
            item.add(createEUTable(rpc));
            sections.add(item);
        }
        
        if (rpc.getRpcDocuments().size() > 0) {
            item = new ListItem();
            item.add(createItemTitle("Attachments", ""));
            item.add(createRPCDocumentTable(rpc.getRpcDocuments()));
            sections.add(item);
        }

        document.add(sections);
    }

    protected Table createRPCDocumentTable(java.util.List<RPCRequestDocument> documents) throws BadElementException {
        Table table = new Table(3);
        table.setWidth(98);
        table.setPadding(2);
        float[] widths = {2,2,1};
        table.setWidths(widths);
        
        table.addCell(createHeaderCell("Description"));
        table.addCell(createHeaderCell("Type"));
        table.addCell(createHeaderCell("Document Id"));
        
        for (RPCRequestDocument doc : documents) {
            // skip document if no public version is available
            // and trade secret information is hidden
            table.addCell(createDataCell(doc.getDescription()));
            table.addCell(createDataCell(
                    RPCRequestDocTypeDef.getData().getItems().getItemDesc(
                            doc.getDocTypeCd())));
            table.addCell(createDataCell(doc.getDocumentID().toString()));
        }
        
        return table;
    }
    
    protected Table createRPCContactTable(Contact contact) throws DocumentException {
        Table contactTable = null;
        if (contact != null) {
            String address1 = "";
            String city = "";
            String state = "";
            String zip = "";

            if (contact.getAddress() != null) {
                address1 = contact.getAddress().getAddressLine1();
                city = contact.getAddress().getCityName();
                state = contact.getAddress().getState();
                zip = contact.getAddress().getZipCode5();
            }
            LinkedHashMap<String, String> contactMap = new LinkedHashMap<String, String>();
            contactMap.put("First Name: ", contact.getFirstNm());
            contactMap.put("Address: ", address1);
            contactMap.put("Zip Code: ", zip);
            contactMap.put("Last Name: ", contact.getLastNm());
            contactMap.put("City/Township: ", city);
            contactMap.put("Phone Number: ", contact.getPhoneNo());
            contactMap.put("Company Title: ", contact.getCompanyTitle());
            contactMap.put("State: ", state);
            contactMap.put("Email: ", contact.getEmailAddressTxt());
            contactMap.put("Secondary Email: ", contact.getEmailAddressTxt2());

            contactTable = createNameValueTable(contactMap, 3);
            contactTable.setWidth(98);
            float[] widths = {2, 2, 2, 2, 3, 4};
            contactTable.setWidths(widths);
        }
        return contactTable;
    }
    
}
