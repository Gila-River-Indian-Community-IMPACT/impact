package us.oh.state.epa.stars2.app.tools;

import java.io.File;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.StringTokenizer;

import javax.faces.event.ActionEvent;

import us.oh.state.epa.aport.admin.domain.User;
import us.oh.state.epa.common.biz.Money;
import us.oh.state.epa.common.data.OID;
import us.oh.state.epa.common.err.EPATypeMismatchException;
import us.oh.state.epa.common.util.Range;
import us.oh.state.epa.revenues.domain.CertificationType;
import us.oh.state.epa.revenues.domain.RevenueSearchObject;
import us.oh.state.epa.revenues.domain.RevenueSortObject;
import us.oh.state.epa.revenues.domain.RevenueSummary;
import us.oh.state.epa.revenues.domain.RevenueType;
import us.oh.state.epa.revenues.service.RevenuesService;
import us.oh.state.epa.stars2.bo.CorrespondenceService;
import us.oh.state.epa.stars2.bo.DocumentService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.BulkDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.invoice.Invoice;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceList;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.CorrespondenceDirectionDef;
import us.oh.state.epa.stars2.def.EmissionReportsRealDef;
import us.oh.state.epa.stars2.def.InvoiceState;
import us.oh.state.epa.stars2.def.RevenueState;
import us.oh.state.epa.stars2.def.RevenueTypeDef;
import us.oh.state.epa.stars2.def.TemplateDocTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentGenerationException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

public abstract class GenerateBulkInvoiceBase extends BulkCorrespondence{
	    protected String tmpLetterDirName;
        protected String tmpInvoiceDirName;
	    protected String urlLetterDirName;
        protected String urlInvoiceDirName;
	    protected InvoiceList[] invoices;	    
	    protected FormResult[] results;
	    protected String currentFile;	    
	    protected ArrayList<FormResult> resultsList;
	    protected ArrayList<InvoiceList> filteredInvoices;
	    protected User user;
        
        private static boolean testMode = false;  // TODO DENNIS for testing
        
    	private InfrastructureService infrastructureService;

    	public InfrastructureService getInfrastructureService() {
    		return infrastructureService;
    	}

    	public void setInfrastructureService(InfrastructureService infrastructureService) {
    		this.infrastructureService = infrastructureService;
    	}
       // protected BulkOperationsCatalog _catalog;
	    
	    public GenerateBulkInvoiceBase() {
	        super();
	        setButtonName("Generate Document(s)");
	        setNavigation("dialog:generateBulkForm");
	    }

        /**
         * Initialize the BulkOperation. Typically, the BulkOperation may need to
         * initialize default values in one or more of the BulkOperationsCatalog
         * search fields. This method is called by the BulkOperationsCatalog after
         * the BulkOperation is selected, but before the search screen fields are
         * displayed.
         */
        public final void init(BulkOperationsCatalog catalog) {
           // _catalog = catalog;
            return;
        }

	    /**
	     * Each bulk operation is responsible for providing a search operation based
	     * on the paramters gathered by the BulkOperationsCatalog bean. The search
	     * is run when the "Select" button on the Bulk Operations screen is clicked.
	     * The BulkOperationsCatalog will then display the results of the search.
	     * Below the results is a "Bulk Operation" button.
	     */
	    public final void searchInvoices(BulkOperationsCatalog catalog)
	        throws DAOException {
	    	
	        this.catalog = catalog;
	        user = catalog.getUser();
	    	setCorrespondenceDate(catalog.getCorrespondenceDate());

	        BulkDef bulkOpDef = catalog.getBulkDef();

	        setCorrespondenceTypeCode(bulkOpDef.getCorrespondenceTypeCd());

	        InvoiceList searchObj = new InvoiceList();
	        searchObj.setFacilityId(catalog.getFacilityId());
            searchObj.setDoLaaCd(catalog.getDoLaa());
	        searchObj.setFacilityName(catalog.getFacilityNm());
	        searchObj.setRevenueId(catalog.getRevenueId());
	        searchObj.setRevenueTypeCd(catalog.getRevenueTypeCd());
	        if(catalog.getStartDate() != null){
	        	searchObj.setBeginDt(new Date(catalog.getStartDate().getTime()));
	        }
	        if(catalog.getEndDate() != null){
	        	searchObj.setEndDt(new Date(catalog.getEndDate().getTime()));
	        }
            if(catalog.getBulkDef().getBulkId().equals(270)) {
                // late letter & invoice
                searchObj.setInvoiceStateCd(InvoiceState.POSTED_TO_REVENUES);
                searchObj.setRevenueStateCd(RevenueState.NP);
            } else {
                // invoice
                searchObj.setInvoiceStateCd(InvoiceState.LATE_LETTER_INVOICE);
                searchObj.setRevenueStateCd(RevenueState.NP);
            }
	       	        
	        boolean revenuesFirst = false;
	        if(searchObj.getRevenueStateCd() != null){        	
        		revenuesFirst = true;        	
        	}
            setMaximum(100);
            setValue(0);
	        try {
                setSearchStarted(true);
                if(user == null) {
                    catalog.setInfoMessage("User is null.  Request ignored because Revenues System can not be acccessed.");
                    catalog.setHasInvoiceSearchResults(false);
                    setSearchCompleted(true);
                    return;
                }
                invoices = getInfrastructureService().searchInvoices(user, searchObj, revenuesFirst);
            } catch(RemoteException re) {
                logger.error("searchInvoices failed: " + re.getMessage(), re);
                catalog.setInvoices(null);
                setSearchCompleted(true);
                throw new DAOException("searchInvoices failed: " + re.getMessage(), re);
            }

	        if (invoices.length == 0 || invoices.length > 20000) {
	            catalog.setInvoices(null);
	            setSearchCompleted(true);
	            return;
	        }
            
	        filteredInvoices = filterInvoices(invoices, catalog);	       
	        catalog.setInvoices(filteredInvoices.toArray(new InvoiceList[0]));
	        setSearchCompleted(true);

	        return;

	    }

	    protected ArrayList<InvoiceList> filterInvoices(InvoiceList[] invoiceBaseList,
	            BulkOperationsCatalog catalog) throws DAOException {
	        ArrayList<InvoiceList> filteredInvoices1 = new ArrayList<InvoiceList>();
	        HashSet<Integer> unqRevenueIds = new HashSet<Integer>();
            int handledCnt = 0;
            setMaximum(invoiceBaseList.length);
            setValue(handledCnt);
            
            if(testMode) {
                StringBuffer s = new StringBuffer(invoiceBaseList.length * 10);
                for(InvoiceList i : invoiceBaseList) {
                    s.append(i.getInvoiceId() + ", ");
                }
                logger.error("DEBUG: invoiceBaseList[" + invoiceBaseList.length + "]: " + s.toString());
            }
	        for(int i = 0; i < invoiceBaseList.length; i++){
	            boolean keepInvoice = true;
	            if(catalog.getBulkDef().getBulkId().equals(270) && !invoiceBaseList[i].getInvoiceStateCd().equals(InvoiceState.POSTED_TO_REVENUES)){	    			
	                keepInvoice = false; 
	            }
	            if(keepInvoice && invoiceBaseList[i].getRevenueId() != null) { // don't keep if no revenue Id
	                filteredInvoices1.add(invoiceBaseList[i]);
                    try {
                        unqRevenueIds.add(invoiceBaseList[i].getRevenueId());
                    } catch(Exception e) {
                        logger.error("new OID failed: " + e.getMessage(), e);
                        throw new DAOException("new OID failed: " + e.getMessage(), e);
                    }
	            } else {
	                setValue(++handledCnt);  // we have finished this one--Not the ones put into unqRevenueIds.
                }
	        }
            if(testMode) {
                StringBuffer s = new StringBuffer(filteredInvoices1.size() * 10);
                for(InvoiceList il : filteredInvoices1) {
                    s.append(il.getInvoiceId() + ", ");
                }
                logger.error("DEBUG: filteredInvoices1[" + filteredInvoices1.size() + "]: " + s.toString());
            }
	        OID[] revenueIdList = new OID[unqRevenueIds.size()];
            int ndx = 0;
            for(Integer oid : unqRevenueIds) {
                try {
                revenueIdList[ndx++] = new OID(oid);
                } catch(Exception e) {
                    throw new DAOException("new OID failed with " + e.getClass().toString(), e);
                }
            }
            RevenuesService revenueService = null;
            try {
                revenueService = getInfrastructureService().getRevenueService();
            } catch(RemoteException re) {
                logger.error("getRevenueService failed: " + re.getMessage(), re);
                throw new DAOException("getRevenueService failed: " + re.getMessage(), re);
            }
            RevenueSummary[] revenueList;
	        if(revenueService == null) {
	            // only true for testing
                try{
	            ArrayList<RevenueSummary> revenueArrayList = new ArrayList<RevenueSummary>();
	            // Create a dummy array of RevenueSummary objects with correct Revenue ID values.
	            for(InvoiceList inv : filteredInvoices1) {
	                Money x = new Money("10.00");
	                OID y = new OID(inv.getRevenueId());
	                RevenueType z = new RevenueType("TVE06");
	                Calendar c = Calendar.getInstance();
	                //RevenueSummary rs = new RevenueSummary(z, y, y, y, y, y, c, c, c, x, x, x, c, c, c, true, true, true);
                    RevenueSummary rs = new RevenueSummary(z, y, y, y, y, y, c, c, c, x, x, x, c, c, c, true, CertificationType.POTENTIAL, true);
	                rs.setOID(y);
	                if(rs.getOID() == null) {
	                    // some of the Stars2 invoices may not have a Revenue Id.
	                    rs.setOID(y);
	                }
	                revenueArrayList.add(rs);
	            }
	            revenueList = revenueArrayList.toArray(new RevenueSummary[0]);
                }catch(Exception e) {
                    logger.error("Test Code failed" + e.getMessage(), e);
                    throw new RuntimeException(e);
                }
	        } else {
                Range<Money> balanceDue = new Range<Money>();
                balanceDue.setStart(new Money(0.1));
                RevenueSortObject revenueSortObj = new RevenueSortObject();
                revenueSortObj.setByID();
                revenueSortObj.setByType();
                CertificationType ct = null;
                if(catalog.getBulkDef().getBulkId().equals(270)) {
                    ct = null;  // don't care about certified
                } else {
                    ct = CertificationType.POTENTIAL;
                }
                RevenueSearchObject revenueSearchObj = new RevenueSearchObject(
                        revenueIdList, null, null, null, null, null, null,
                        null, balanceDue, true, ct, true);
	            //this search will return revenues which are active, interest charged and not yet certified to AGO.
                if(testMode) {
                    StringBuffer s = new StringBuffer(revenueIdList.length * 10);
                    for(OID oid : revenueIdList) {
                        s.append(oid.toString() + ", ");
                    }
                    logger.error("DEBUG: revenueIdList[" + revenueIdList.length + "]: " + s.toString());
                }
                try {
	            revenueList = revenueService.retrieveDAPCRevenueList(user,
	                    revenueSearchObj, revenueSortObj);
                } catch(Exception re) {
                    String s = "retrieveDAPCRevenueList failed (number of oids=" + revenueIdList.length + "): " + re.getMessage();
                    logger.error(s, re);
                    throw new DAOException(s, re);
                }
	        }
            double remaining = invoiceBaseList.length - handledCnt;
            double fractionAtTime = ((double)remaining) / (double)filteredInvoices1.size();
            double cnt = handledCnt;
	        ArrayList<InvoiceList> filteredInvoices2 = new ArrayList<InvoiceList>();
	        if(revenueList != null) {
                if(testMode) {
                    StringBuffer s = new StringBuffer(revenueList.length * 10);
                    for(RevenueSummary rs : revenueList) {
                        s.append(rs.getOID().toString() + ", ");
                    }
                    logger.error("DEBUG: revenueList[" + revenueList.length + "]: " + s.toString());
                }
	            for (InvoiceList inv : filteredInvoices1) {   
	                for (int k = 0; k < revenueList.length; k++) {           
	                    if (inv.getRevenueId() == (Integer
	                            .parseInt(revenueList[k].getOID().toString()))) {
	                        boolean include = true;
	                        Double amt = revenueList[k].getBalanceDue().getAsDouble();
	                        if(amt <= 0) {
	                            include = false;
	                        }
	                        if(include) {
	                            filteredInvoices2.add(inv);
	                        }
	                        break;
	                    }
	                }
                    cnt = cnt + fractionAtTime;
	                setValue((int) cnt);
	            }
	        }
            if(testMode) {
                StringBuffer s = new StringBuffer(filteredInvoices2.size() * 10);
                for(InvoiceList il : filteredInvoices2) {
                    s.append(il.getInvoiceId().toString() + ", ");
                }
                logger.error("DEBUG: filteredInvoices2[" + filteredInvoices2.size() + "]: " + s.toString());
            }
            // Remove duplicates.
            boolean more = filteredInvoices2.size() > 1;
            int startPoint = 0;
            while(more) {
                Integer rId = filteredInvoices2.get(startPoint).getRevenueId();
                Integer iId = filteredInvoices2.get(startPoint).getInvoiceId();
                int theEnd = filteredInvoices2.size();
                for(int i = startPoint + 1; i < theEnd; i++) {
                    if(rId.equals(filteredInvoices2.get(i).getRevenueId())) {
                        // two invoices with the same revenue Id
                        if(iId.compareTo(filteredInvoices2.get(i).getInvoiceId()) < 0) {
                            iId = filteredInvoices2.get(i).getRevenueId();
                            // remove the one at startPoint
                            filteredInvoices2.remove(startPoint);
                            startPoint = i - 1;
                        } else {
                            // remove the one at i
                            filteredInvoices2.remove(i);
                            
                        }
                        i--; // since for look advances, need to back up one.
                        theEnd--; // One less in the list
                    }
                }
                startPoint++;
                if(startPoint >= filteredInvoices2.size()) {
                    more = false;
                }
            }
            if(testMode) {
                StringBuffer s = new StringBuffer(filteredInvoices2.size() * 10);
                for(InvoiceList il : filteredInvoices2) {
                    s.append(il.getInvoiceId().toString() + ", ");
                }
                logger.error("DEBUG: no duplicates:filteredInvoices2[" + filteredInvoices2.size() + "]: " + s.toString());
            }
	        return filteredInvoices2;
	    }

        /**
         * When the "Bulk Operation" button is clicked, the BulkOperationsCatalog
         * class will call this method, ensure that the BulkOperation class is
         * placed into the jsf context, and return the value of the getNavigation()
         * method to the jsf controller. Any further requests from the page that is
         * navigated to are handled by this bean in the normal way. The default
         * version does no preliminary work.
         */
        public final void performPreliminaryWork(BulkOperationsCatalog catalog){
            FormResult form = null;
            invoices = catalog.getSelectedInvoices();     
            
            try {
                tmpInvoiceDirName = getDocumentService().createTmpDir(getCurrentUser());
                urlInvoiceDirName = tmpInvoiceDirName.replace('\\', '/');            
            }
            catch (Exception e) {
                String logStr = "Could not create tmp directory. Exception " + e.getClass().getName() + ", Msg = "
                + e.getMessage();
                logger.error(logStr, e);
                form = new FormResult();
                form.setNotes(logStr);
                resultsList.add(0, form);
                results = resultsList.toArray(new FormResult[0]);
                return;
            }
            String fileLetterBaseName = null;
            TemplateDocument templateLetterDoc = null;
            TemplateDocument templateTV_FEE_Doc;
            String fileTV_Fee_BaseName;
            TemplateDocument templateSMTV_FEE_Doc;
            String fileSMTV_Fee_BaseName;
            TemplateDocument templateNTV_FEE_Doc;
            String fileNTV_Fee_BaseName;
            TemplateDocument templatePERMIT_Doc;
            String filePERMIT_BaseName;
            String documentID;
            try {
                if(catalog.getBulkDef().getBulkId() == 270) {
                    try {
                        tmpLetterDirName = getDocumentService().createTmpDir(getCurrentUser());
                        urlLetterDirName = tmpLetterDirName.replace('\\', '/');            
                    }
                    catch (Exception e) {
                        String logStr = "Could not create tmp directory. Exception " + e.getClass().getName() + ", Msg = "
                        + e.getMessage();
                        logger.error(logStr, e);
                        form = new FormResult();
                        form.setNotes(logStr);
                        resultsList.add(0, form);
                        results = resultsList.toArray(new FormResult[0]);
                        return;
                    }
                    documentID = catalog.getBulkDef().getTemplateDocId();
                    templateLetterDoc = getDocumentService().retrieveTemplateDocument(documentID);
                    fileLetterBaseName = getFileBaseName(templateLetterDoc);
                }

                //documentID = Integer.parseInt(TemplateDocTypeDef.TV_FEE_INVOICE);
                documentID =TemplateDocTypeDef.TV_FEE_INVOICE;
                templateTV_FEE_Doc = getDocumentService().retrieveTemplateDocument(documentID);
                fileTV_Fee_BaseName = getFileBaseName(templateTV_FEE_Doc);

                //documentID = Integer.parseInt(TemplateDocTypeDef.SMTV_FEE_INVOICE);
                documentID = TemplateDocTypeDef.SMTV_FEE_INVOICE;
                templateSMTV_FEE_Doc = getDocumentService().retrieveTemplateDocument(documentID);
                fileSMTV_Fee_BaseName = getFileBaseName(templateSMTV_FEE_Doc);


                //documentID = Integer.parseInt(TemplateDocTypeDef.NTV_FEE_INVOICE);
                documentID = TemplateDocTypeDef.NTV_FEE_INVOICE;
                templateNTV_FEE_Doc = getDocumentService().retrieveTemplateDocument(documentID);
                fileNTV_Fee_BaseName = getFileBaseName(templateNTV_FEE_Doc);

                //documentID = Integer.parseInt(TemplateDocTypeDef.PERMIT_INVOICE);
                documentID = TemplateDocTypeDef.PERMIT_INVOICE;
                templatePERMIT_Doc = getDocumentService().retrieveTemplateDocument(documentID);
                filePERMIT_BaseName = getFileBaseName(templatePERMIT_Doc);
            }catch(RemoteException daoe) {
                String logStr = "Could not rerieve document templates. Exception " + daoe.getClass().getName() + ", Msg = "
                + daoe.getMessage();
                logger.error(logStr, daoe);
                form = new FormResult();
                form.setNotes(logStr);
                resultsList.add(0, form);
                results = resultsList.toArray(new FormResult[0]);
                return;
            }
            String fileInvoiceBaseName = null;
            TemplateDocument templateInvoiceDoc = null;
            try {
                if (invoices.length != 0) {
                    resultsList = new ArrayList<FormResult>();
                    int fileLetterCnt = 0;
                    int fileInvoiceCnt = 0;
                    for (int i = 0; i < invoices.length; i++) {
                        try {
                            int invoiceId = invoices[i].getInvoiceId();
                            Invoice invoice = getInfrastructureService().retrieveInvoice(user, invoiceId, false);
                            Facility facility = getFacilityService().retrieveFacility(invoices[i].getFacilityId());
                            // Do late letter if requested
                            if(catalog.getBulkDef().getBulkId().equals(270)) {
                                form = new FormResult();
                                resultsList.add(form);
                                form.setId(facility.getName());
                                form.setFormURL(DocumentUtil.getFileStoreBaseURL()+ urlLetterDirName + "/" 
                                        + facility.getFacilityId() + "-"
                                        + invoice.getInvoiceId() + "-" + fileLetterBaseName + ".docx");
                                form.setFileName(facility.getFacilityId() + "-"
                                        + invoice.getInvoiceId() + "-" + fileLetterBaseName + ".docx");                       
                                String errors = generateLetterForm(templateLetterDoc, fileLetterBaseName, facility, invoice);
                                if(errors.length() > 0) {
                                    errors = errors + "--document not generated";
                                    form.setNotes(errors);
                                } else {
                                    fileLetterCnt ++;
                                }
                                if (errors.length() == 0) {
                                    addCorrespondence(invoice.getFacilityId(), invoice.getInvoiceId().toString(), tmpLetterDirName 
                                            + File.separator + facility.getFacilityId() + "-"
                                            + invoice.getInvoiceId() + "-" + fileLetterBaseName + ".docx");
                                }
                            }
                            
                            // Always do invoice
                            String reportCategory = RevenueTypeDef
                            .getReportCategory(invoice.getRevenueTypeCd());
                            if(reportCategory.equals(EmissionReportsRealDef.TV)){
                                fileInvoiceBaseName = fileTV_Fee_BaseName;   
                                templateInvoiceDoc = templateTV_FEE_Doc;
                            }
                            else if(reportCategory.equals(EmissionReportsRealDef.SMTV)){
                                fileInvoiceBaseName = fileSMTV_Fee_BaseName;
                                templateInvoiceDoc = templateSMTV_FEE_Doc;
                            }
                            else if(reportCategory.equals(EmissionReportsRealDef.NTV)){
                                fileInvoiceBaseName = fileNTV_Fee_BaseName;
                                templateInvoiceDoc = templateNTV_FEE_Doc;
                            }
                            else if(reportCategory.equals(EmissionReportsRealDef.NONE)){
                                fileInvoiceBaseName = filePERMIT_BaseName;
                                templateInvoiceDoc = templatePERMIT_Doc;
                            } else {
                                form = new FormResult();
                                resultsList.add(form);
                                form.setId(facility.getName());
                                form.setNotes("Revenue type code did not map to tv, smtv, ntv or none in reference table iv_revenue_type_def for invoice " + invoice.getInvoiceId());
                                continue;
                            }
                            form = new FormResult();
                            resultsList.add(form);
                            form.setId(facility.getName());
                            form.setFormURL(DocumentUtil.getFileStoreBaseURL()+ urlInvoiceDirName + "/" 
                                    + facility.getFacilityId() + "-"
                                    + invoice.getInvoiceId() + "-" + fileInvoiceBaseName + ".docx");
                            form.setFileName(facility.getFacilityId() + "-"
                                    + invoice.getInvoiceId() + "-" + fileInvoiceBaseName + ".docx");                       
                            String errors = generateInvoiceForm(templateInvoiceDoc, fileInvoiceBaseName, facility, invoice);
                            if(errors.length() > 0) {
                                errors = errors + "--document not generated";
                                form.setNotes(errors);
                            } else {
                                fileInvoiceCnt ++;
                            }
                            if (errors.length() == 0 && getCorrespondenceTypeCode() != null) {
                                addCorrespondence(invoice.getFacilityId(), invoice.getInvoiceId().toString(), tmpInvoiceDirName 
                                        + File.separator + facility.getFacilityId() + "-"
                                        + invoice.getInvoiceId() + "-" + fileInvoiceBaseName + ".docx");
                            }                                          

                        } catch (Exception e) {
                            String logStr = "Exception " + e.getClass().getName()
                            + ", Msg = " + e.getMessage();
                            logger.error(logStr, e);
                            form.setNotes(logStr);
                        }
                    }

                    if(fileInvoiceCnt > 0) {
                        try {
                            fileInvoiceBaseName = "Interest_Invoice";
                            form = new FormResult();
                            form.setId("Zip File");
                            form.setNotes("All Invoice Forms");
                            resultsList.add(0, form);
                            Calendar cal = Calendar.getInstance();
                            String date = Integer.toString(cal.get(Calendar.YEAR)) + "-" 
                            + Integer.toString((cal.get(Calendar.MONTH) + 1)) + "-"
                            + Integer.toString(cal.get(Calendar.DAY_OF_MONTH)) + "-"
                            + Integer.toString(cal.get(Calendar.HOUR_OF_DAY)) + "-"
                            + Integer.toString(cal.get(Calendar.MINUTE)) + "-"
                            + Integer.toString(cal.get(Calendar.SECOND));
                            String zipFileName = fileInvoiceBaseName + "-" + date + ".zip";
                            DocumentUtil.createZipFile(tmpInvoiceDirName, 
                                    File.separator + "tmp" + File.separator 
                                    + getCurrentUser() + File.separator + zipFileName,
                                    fileInvoiceBaseName);
                            form.setFormURL(DocumentUtil.getFileStoreBaseURL() + "/tmp/" + getCurrentUser()
                                    + "/" + zipFileName);
                            form.setFileName(fileInvoiceBaseName + ".zip"); 
                        }
                        catch (Exception e) {
                            String logStr = "Exception " + e.getClass().getName() + ", Msg = "
                            + e.getMessage();
                            logger.error(logStr, e);
                            form.setNotes(logStr);
                        }
                    } else {
                        form = new FormResult();
                        form.setId("Zip File");
                        form.setNotes("No Documents Generated; No Zip File");
                        resultsList.add(0, form);
                    }
                    if(catalog.getBulkDef().getBulkId() == 270) {
                        if(fileLetterCnt > 0) {
                            try {
                                //fileLetterBaseName = "Interest_Invoice";aa;
                                form = new FormResult();
                                form.setId("Zip File");
                                form.setNotes("All Letter Forms");
                                resultsList.add(0, form);
                                Calendar cal = Calendar.getInstance();
                                String date = Integer.toString(cal.get(Calendar.YEAR)) + "-" 
                                + Integer.toString((cal.get(Calendar.MONTH) + 1)) + "-"
                                + Integer.toString(cal.get(Calendar.DAY_OF_MONTH)) + "-"
                                + Integer.toString(cal.get(Calendar.HOUR_OF_DAY)) + "-"
                                + Integer.toString(cal.get(Calendar.MINUTE)) + "-"
                                + Integer.toString(cal.get(Calendar.SECOND));
                                String zipFileName = fileLetterBaseName + "-" + date + ".zip";
                                DocumentUtil.createZipFile(tmpLetterDirName, 
                                        File.separator + "tmp" + File.separator 
                                        + getCurrentUser() + File.separator + zipFileName,
                                        fileLetterBaseName);
                                form.setFormURL(DocumentUtil.getFileStoreBaseURL() + "/tmp/" + getCurrentUser()
                                        + "/" + zipFileName);
                                form.setFileName(fileLetterBaseName + ".zip"); 
                            }
                            catch (Exception e) {
                                String logStr = "Exception " + e.getClass().getName() + ", Msg = "
                                + e.getMessage();
                                logger.error(logStr, e);
                                form.setNotes(logStr);
                            }
                        } else {
                            form = new FormResult();
                            form.setId("Zip File");
                            form.setNotes("No Documents Generated; No Zip File");
                            resultsList.add(0, form);
                        }
                    }
                }
            } catch (Exception e) {
                String logStr = "Exception " + e.getClass().getName() + ", Msg = "
                + e.getMessage();
                logger.error(logStr, e);
                form = new FormResult();
                form.setNotes(logStr);
                resultsList.add(0, form);
            }
            results = resultsList.toArray(new FormResult[0]);
            return;
        }
        
        protected String getFileBaseName(TemplateDocument templateDoc) {
            String fileBaseName;
            String fPath = templateDoc.getPath();
            int lastSlash = fPath.lastIndexOf('/');
            if (lastSlash < 0) {
                lastSlash = fPath.lastIndexOf('\\');
            }
            int lastDot = fPath.lastIndexOf('.');
            fileBaseName = fPath.substring(lastSlash + 1, lastDot);
            return fileBaseName;
        }
        
        protected String generateInvoiceForm(TemplateDocument templateInvoiceDoc, String fileInvoiceBaseName, Facility facility, Invoice invoice) {

            StringBuffer errors = new StringBuffer();

            try {
                DocumentGenerationBean dataBean = new DocumentGenerationBean();
                if (getCorrespondenceDate() != null && getCorrespondenceDate().length() > 0) {
                    dataBean.setCorrespondenceDate(getCorrespondenceDate());
                }
                
                dataBean.setFacility(facility);
                dataBean.setInvoice(facility, invoice);
                
                //Generated invoice document should also be updated in invoice, apart from placing in tmp dir.
                Permit permit = null;
                EmissionsReport emissionsReport = null;
                if(invoice.getPermitId() != null){
                    permit = getPermitService().retrievePermit(invoice.getPermitId()).getPermit();
                    dataBean.setPermit(permit);
                    if(invoice != null) {
                        invoice.setContact(new Contact()); // so we know if DocumentGenerationException was because invoice had no contact
                    }
                    invoice.setPermitInvDocument(getInfrastructureService().generatePermitInvDocument(facility, permit, invoice, null, templateInvoiceDoc));
                    getInfrastructureService().modifyInvoiceDocument(invoice);
                }
                else if(invoice.getEmissionsRptId() != null){
                    emissionsReport = getEmissionsReportService().retrieveEmissionsReport(invoice.getEmissionsRptId(), false);
                    dataBean.setEmissionsReport(emissionsReport);
                    
                    getInfrastructureService().generateReportInvDocument(facility, emissionsReport, invoice, null, templateInvoiceDoc);
                    //updating document Id for emissions inventory invoice is handled by the above method,
                    //but for permits it has to be done explicitly. 
                } else {
                    errors.append("Invoice " + invoice.getInvoiceId() + " does not specify a report or permit, Facility- " + facility.getName()
                            + " #" + facility.getFacilityId());
                    return errors.toString();
                }
                           
                /*DocumentUtil.generateDocument(templateInvoiceDoc.getPath(), dataBean, tmpInvoiceDirName
                                              + File.separator + facility.getFacilityId() 
                                              + "-" + invoice.getInvoiceId() + "-" + fileInvoiceBaseName + ".docx");*/
                DocumentUtil.generateAsposeDocument(templateInvoiceDoc.getTemplateDocPath(), dataBean, tmpInvoiceDirName
                        + File.separator + facility.getFacilityId() 
                        + "-" + invoice.getInvoiceId() + "-" + fileInvoiceBaseName + ".docx");
            } catch(DocumentGenerationException dge) {
                String logStr = "DocumentGenerationException "
                    + ", Msg = " + dge.getMessage();
                if(invoice != null && invoice.getContact() == null) {
                    logStr = "No active billing contact for invoice";
                } else {
                    logger.error(logStr, dge);
                }
                errors.append(logStr);
            } catch (Exception e) {
                String logStr = "Exception " + e.getClass().getName()
                + ", Msg = " + e.getMessage();
                errors.append(logStr);
                logger.error(logStr, e);
            }

            return errors.toString();
        }
        
        protected String generateLetterForm(TemplateDocument templateLetterDoc, String fileLetterBaseName, Facility facility, Invoice invoice) {

            StringBuffer errors = new StringBuffer();

            try {
                DocumentGenerationBean dataBean = new DocumentGenerationBean();
                if (getCorrespondenceDate() != null && getCorrespondenceDate().length() > 0) {
                    dataBean.setCorrespondenceDate(getCorrespondenceDate());
                }
                dataBean.setFacility(facility);
                dataBean.setInvoice(facility, invoice);
                //DocumentUtil.generateDocument(templateLetterDoc.getPath(), dataBean, tmpLetterDirName                                          + File.separator + facility.getFacilityId() 
                //                              + "-" + invoice.getInvoiceId() + "-" + fileLetterBaseName + ".docx");
                DocumentUtil.generateAsposeDocument(templateLetterDoc.getTemplateDocPath(), dataBean, tmpLetterDirName
                		+ "-" + invoice.getInvoiceId() + "-" + fileLetterBaseName + ".docx");
                        
            } catch(DocumentGenerationException dge) {
                String logStr = "DocumentGenerationException "
                    + ", Msg = " + dge.getMessage();
                if(invoice != null && invoice.getContact() == null) {
                    logStr = "No active billing contact for invoice";
                } else {
                    logger.error(logStr, dge);
                }
                errors.append(logStr);
            } catch (Exception e) {
                String logStr = "Exception " + e.getClass().getName()
                    + ", Msg = " + e.getMessage();
                errors.append(logStr);
                logger.error(logStr, e);
            }

            return errors.toString();
        }
        
        protected  String generateForm(Facility facility, Invoice invoice) {
            return null;
        }
        
	    /**
	     * When the "Apply Operation" button is clicked, the BulkOperationsCatalog
	     * class will call this method and return the value of the getNavigation()
	     * method to the jsf controller. Any further requests from the page that is
	     * navigated to are handled by this (the derived) class in the normal way.
	     * The default version does no preliminary work.
	     * 
	     * @param catalog
	     * @return
	     */
	    public String performPostWork(BulkOperationsCatalog catalog) {
	        try {
	            DocumentUtil.rmDir(tmpLetterDirName);
	        }
	        catch (Exception e) {
	            logger.error("Unable to remove temporary directory " + tmpLetterDirName, e);
	        }
            try {
                DocumentUtil.rmDir(tmpInvoiceDirName);
            }
            catch (Exception e) {
                logger.error("Unable to remove temporary directory " + tmpInvoiceDirName, e);
            }
	        return SUCCESS;
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

	    public final FormResult[] getResults() {
	        return results;
	    }

	    public final void setFile(String fileName) {
	        currentFile = fileName;
	    }

	    public final String getFile() {
	        return currentFile;
	    }

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}	    
	
}
