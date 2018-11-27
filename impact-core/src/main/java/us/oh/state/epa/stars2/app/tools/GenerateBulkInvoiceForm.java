package us.oh.state.epa.stars2.app.tools;

import java.io.File;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.StringTokenizer;

import javax.faces.event.ActionEvent;

import us.oh.state.epa.stars2.bo.CorrespondenceService;
import us.oh.state.epa.stars2.bo.DocumentService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.invoice.Invoice;
import us.oh.state.epa.stars2.def.CorrespondenceDirectionDef;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentGenerationException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

@SuppressWarnings("serial")
public class GenerateBulkInvoiceForm extends GenerateBulkInvoiceBase {

    public GenerateBulkInvoiceForm() {
        super();
    }
    
    public void correspondence(ActionEvent actionEvent) throws RemoteException {

        CorrespondenceService corrBO = null;
        InfrastructureService infoBO = null;
        DocumentService docBO;

        try {
            corrBO = getCorrespondenceService();
            infoBO = getInfrastructureService();
            docBO = getDocumentService();
        }
        catch (Exception e) {
            logger.error("Exception caught while fetching services, msg = "
                         + e.getMessage(), e);
            return;
        }

        if (_correspondenceDate == null) {
            DisplayUtil.displayError("Missing Correspondence Date");
            return;
        }

        StringTokenizer st = new StringTokenizer(_correspondenceDate, "/");
        if (st.countTokens() != 3) {
            DisplayUtil.displayError("Correspondence Date should be in the form MM/DD/YYYY");
            return;
        }
        String invoiceId = null;
        try {
            int month = Integer.parseInt(st.nextToken()) - 1;
            int day = Integer.parseInt(st.nextToken());
            int year = Integer.parseInt(st.nextToken());
            
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            
            for (Correspondence correspondence : _correspondence) {
                invoiceId = correspondence.getAdditionalInfo();
                Timestamp ts = new Timestamp(cal.getTimeInMillis());
                correspondence.setDateGenerated(ts);
                correspondence.setDirectionCd(CorrespondenceDirectionDef.OUTGOING);
                corrBO.createCorrespondence(correspondence);
                int id = Integer.parseInt(invoiceId);
                infoBO.setInvoicePaymentLateInfo(id);
                Document cDoc = correspondence.getDocument();
                if (cDoc != null && correspondence.getSavedDocReqd()) {
                    cDoc.setTemporary(false);
                    docBO.updateDocument(cDoc);
                }
            }
        } catch(NumberFormatException nfe) {
            
        } catch (Exception e) {
            String logStr = "Problem while storing correspondence for invoice " + invoiceId + ".  Exception type = "
                + e.getClass().getName() + ", Msg = "
                + e.getMessage();
            logger.error(logStr, e);
            DisplayUtil.displayError(logStr);
        }
        _correspondenceSent = true;
        return;
    }
}
