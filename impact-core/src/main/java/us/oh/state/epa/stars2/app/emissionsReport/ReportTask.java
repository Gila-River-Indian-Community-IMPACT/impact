package us.oh.state.epa.stars2.app.emissionsReport;

import java.io.File;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;

import oracle.adf.view.faces.component.UIXTable;
import us.oh.state.epa.aport.admin.domain.User;
import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.app.tools.FormResult;
import us.oh.state.epa.stars2.app.workflow.ActivityProfile;
import us.oh.state.epa.stars2.bo.DocumentService;
import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureBO;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.invoice.Invoice;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceList;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.def.EmissionReportsRealDef;
import us.oh.state.epa.stars2.def.InvoiceState;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.AppValidationMsg;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.ConfirmWindow;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.reports.NtvReport;

/*
 * This class needed so the abstract class TaskBase can be extended.
 * Then, ReportTask can have an instance included in the app version
 * of ReportProfile to provide workflow functionality.
 */
@SuppressWarnings("serial")
public abstract class ReportTask extends TaskBase {
	public static final String INVOICE_DT_MSG = "Entered Date will be applied to all the invoices selected in the Aggregate list";

	public static String POST_CONFIRM_MSG = " Click 'Yes' to Review the selected Invoice or 'No' to cancel the operation.";

	public static String PRINT_CONFIRM_MSG = "Select 'Yes' to Generate Invoice Document or 'No' to cancel.";
    
    static private boolean trace = false;  //true;  // set to false if not doing debugging.

	public Timestamp invoiceDate;

	protected boolean postState;

	protected String templateName;

	private String BUTTON_TEXT;

	private String message;

	transient UIXTable uTable;

	transient UIXTable aggrTable;

	private String tmpDirName;

	private FormResult docsZipFile;

	private User user;

	private InvoiceList[] reviewInvoiceList;

	private List<Integer> processIDs = new ArrayList<Integer>(0);
    private boolean enablePostAdjustButton = false;
    private boolean enableCompletionButton = false;

	private String currentUser = InfrastructureDefs.getCurrentUserAttrs()
			.getUserName();

    private DocumentService documentService;
    
    private EmissionsReportService emissionsReportService;
	private FacilityService facilityService;
	private InfrastructureService infrastructureService;

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}

	public void setEmissionsReportService(
			EmissionsReportService emissionsReportService) {
		this.emissionsReportService = emissionsReportService;
	}
    public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public ReportTask() {
	}

	public abstract Integer getExternalId();

	public abstract void setExternalId(Integer externalId);

	@Override
	public final boolean isDoSelectedButton(ProcessActivity activity) {
		String name = activity.getActivityTemplateNm();
		boolean ret = false;
		if (name.contains("Post to Revenues")
				&& InfrastructureDefs.getCurrentUserAttrs()
						.isCurrentUseCaseValid("invoices.posttorevenues")) {

			BUTTON_TEXT = "Review Selected";
			message = POST_CONFIRM_MSG;
			ret = true;
		}
		// TODO Is there any need to check for privileges for a user to print
		// the document.
		else if (name.contains("Print Invoice")) {
			BUTTON_TEXT = "Print Selected";
			message = PRINT_CONFIRM_MSG;
			ret = true;
		}

		return ret;
	}

	@Override
	public final String getDoSelectedButtonText() {
		return BUTTON_TEXT;
	}

	@Override
	public String getDoSelectedConfirmMsg() {
		return message;
	}

	@Override
	public String getDoSelectedConfirmType() {
		return (new ConfirmWindow()).getYesNo();
	}

	@SuppressWarnings("unchecked")
	public final void doPost() throws DAOException {

		Object oldKey = uTable.getRowKey();
		List<List<Invoice>> invoiceTables = new ArrayList<List<Invoice>>();
		List<Invoice> invoice = new ArrayList<Invoice>();
		List<Invoice> istvInvoice = new ArrayList<Invoice>();
		List<ValidationMessage> retVal = new ArrayList<ValidationMessage>();

		if (user == null && !InfrastructureBO.testWithoutRevenues) {
            String s = "Cannot post to Revenues, User is null";
			DisplayUtil.displayError(s);
            throw new DAOException(s, s);
		}

		try { // do posting and/or adjustments
			retVal = getInfrastructureService().preparePostToRevenues(user, processIDs,
					invoiceDate);
		} catch (Exception ex) {
            logger.error("preparePostToRevenues() failed", ex);
			DisplayUtil.displayError(ex.toString());
            throw new DAOException("preparePostToRevenues failed, User=" + user.toString(), ex);
		}

        Integer pIDcopy = null;
        Integer invId = null;
        String facilityId = null;
		try {
			for (Integer pID : processIDs) {
                pIDcopy = pID;
				Invoice inv = getInfrastructureService().retrieveInvoiceByReportID(
						user, pID);

				if (inv != null) {
                    invId = inv.getInvoiceId();
					invoice.add(inv);
                    if(trace) {
                        logger.error("Debug Info:  Invoice " + inv.getInvoiceId() + " posted");
                    }

					for (int i = 0; i < reviewInvoiceList.length; i++) {
						if (reviewInvoiceList[i].getInvoiceId() != null &&
							  reviewInvoiceList[i].getInvoiceId().equals(inv.getInvoiceId())) {
							
							reviewInvoiceList[i].setRevenueId(inv.getRevenueId());
						}
					}
                    facilityId = inv.getFacilityId();
					Facility facility = getFacilityService().retrieveFacility(
							inv.getFacilityId());
					if (facility.isIntraStateVoucherFlag()) {
						istvInvoice.add(inv);
					}
				}
			}
			// After posting the reviewInvoiceList page should refresh with
			// revenue id, new list is populated to replace old one.
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
					.put("reviewInvoiceList", reviewInvoiceList);

		} catch (RemoteException re) {
			DisplayUtil.displayError(re.toString());
            String s = "Failed.  EmissionRptId= " + pIDcopy +
            ", invoiceId =" + invId +  ", FacilityId=" + facilityId;
            logger.error(s, re);
            throw new DAOException(s, re);
		}

		if (invoice.size() != 0) {
			invoiceTables.add(invoice);
		}
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.remove("docsZipFile");// Removed the map coz all documents
										// link shows up in posted results page.

		FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.put("invoiceTables", invoiceTables);

		if (istvInvoice.size() != 0) {
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().put("istvInvoiceTables", istvInvoice);

			FacesUtil.startModelessDialog("../inv/istvInvoiceList.jsf", 600,
					800);
		}

		AppValidationMsg.validate(retVal, true);
		uTable.setRowKey(oldKey);
		// postState = false;
        enablePostAdjustButton = false;  // don't allow button to be used a second time
        enableCompletionButton = true;  // enable button to complete workflow step
	}

	@SuppressWarnings("unchecked")
	public final void doPrint() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
        .put("partialResultMsg", null);  // clear out problem indications
		Object oldKey = uTable.getRowKey();
		List<List<Invoice>> invoiceTables = new ArrayList<List<Invoice>>();
		List<Invoice> invoice = new ArrayList<Invoice>();

		List<ValidationMessage> retVal = new ArrayList<ValidationMessage>();
		User user = null;
		try {
			user = InfrastructureDefs.getPortalUser();
			if (user == null && !InfrastructureBO.testWithoutRevenues) {
                String msg = "Cannot Print Document, User is null";
				DisplayUtil.displayError(msg);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .put("partialResultMsg", msg);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                        .put("invoiceTables", invoiceTables);
                AppValidationMsg.validate(retVal, true);
                uTable.setRowKey(oldKey);
			return;
			}

			retVal = getInfrastructureService().prepareReportInvDocument(user,
					processIDs);
		} catch (RemoteException re) {
			logger.error("Failed to generate any documents", re);
            String msg = "Failed to generate any documents.  Do not complete. Please start over.";
			DisplayUtil.displayError(msg);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
            .put("partialResultMsg", msg);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                    .put("invoiceTables", invoiceTables);
            AppValidationMsg.validate(retVal, true);
            uTable.setRowKey(oldKey);
            return;
		}

		try {
			tmpDirName = getDocumentService().createTmpDir(getCurrentUser());
		} catch (Exception e) {
			logger.error("Could not create tmp directory.", e);
            String msg = "ERROR: Could not create tmp directory.  Documents created under individual reports but not made easily available to you. Do not complete. Please start over.";
            DisplayUtil.displayError(msg);
            if (invoice.size() != 0) {
                invoiceTables.add(invoice);
            }
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
            .put("partialResultMsg", msg);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                    .put("invoiceTables", invoiceTables);
            AppValidationMsg.validate(retVal, true);
            uTable.setRowKey(oldKey);
            return;
		}

        int numFilesToZip = 0;
		try {
			for (Integer pID : processIDs) {
				Invoice inv = null;
				inv = getInfrastructureService().retrieveInvoiceByReportID(user, pID);

				if (inv != null && inv.getReportInvDocument() != null
                        && inv.getReportInvDocument().getDocumentID() != null) {
					addDocumentToTempDir(inv);
                    numFilesToZip++;
					invoice.add(inv);
				}
			}
		} catch (RemoteException re) {
			logger.error("Partial list of documents generated.", re);
            String msg = "ERROR: Partial list of documents generated. No zip file generated. Documents created under individual reports but not made easily available to you. Do not complete. Please start over.";
			DisplayUtil.displayError("msg");
            if (invoice.size() != 0) {
                invoiceTables.add(invoice);
            }
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
            .put("partialResultMsg", msg);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                    .put("invoiceTables", invoiceTables);
            AppValidationMsg.validate(retVal, true);
            uTable.setRowKey(oldKey);
            return;
        } catch(Exception e) {
            logger.error("Partial list of documents generated.", e);
            String msg = "ERROR: Partial list of documents generated. No zip file generated. Documents created under individual reports but not made easily available to you. Do not complete. Please start over.";
            DisplayUtil.displayError(msg);
            if (invoice.size() != 0) {
                invoiceTables.add(invoice);
            }
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
            .put("partialResultMsg", msg);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                    .put("invoiceTables", invoiceTables);
            AppValidationMsg.validate(retVal, true);
            uTable.setRowKey(oldKey);
            return;
        }
		try {// creates a zip file of all the documents generated.
		    if(numFilesToZip > 0) {
		        String fileBaseName = "Invoice Documents";
		        docsZipFile = new FormResult();
		        docsZipFile.setId("Zip File");
		        docsZipFile.setNotes("All Documents -");
		        Calendar cal = Calendar.getInstance();
		        String date = Integer.toString(cal.get(Calendar.YEAR)) + "-"
		        + Integer.toString((cal.get(Calendar.MONTH) + 1)) + "-"
		        + Integer.toString(cal.get(Calendar.DAY_OF_MONTH)) + "-"
		        + Integer.toString(cal.get(Calendar.HOUR_OF_DAY)) + "-"
		        + Integer.toString(cal.get(Calendar.MINUTE)) + "-"
		        + Integer.toString(cal.get(Calendar.SECOND));
		        String zipFileName = fileBaseName + "-" + date + ".zip";

		        DocumentUtil.createZipFile(tmpDirName, File.separator + "tmp"
		                + File.separator + getCurrentUser() + File.separator
		                + zipFileName, fileBaseName);

		        docsZipFile.setFormURL(DocumentUtil.getFileStoreBaseURL() + "/tmp/"
		                + getCurrentUser() + "/" + zipFileName);
		        docsZipFile.setFileName(fileBaseName + ".zip");
		    } else {
                String msg = "INFO: No documents generated so no zip file created.";
                DisplayUtil.displayError(msg);
                if (invoice.size() != 0) {
                    invoiceTables.add(invoice);
                }
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .put("partialResultMsg", msg);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                        .put("invoiceTables", invoiceTables);
                AppValidationMsg.validate(retVal, true);
                uTable.setRowKey(oldKey);
                return;
            }
		} catch (Exception ee) {
            logger.error("Failed to create zip file.", ee);
            String msg = "ERROR: Failed to create zip file. Documents created under individual reports but not made easily available to you. Do not complete. Please start over.";
            DisplayUtil.displayError(msg);
            if (invoice.size() != 0) {
                invoiceTables.add(invoice);
            }
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
            .put("partialResultMsg", msg);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                    .put("invoiceTables", invoiceTables);
            AppValidationMsg.validate(retVal, true);
            uTable.setRowKey(oldKey);
            return;
		}

		try {// Since we already have documents in zip, no use in keeping
		// this dir otherwise, Temporary files from
		// Tools menu will also list documents under this tmp dir.

			DocumentUtil.rmDir(tmpDirName);
		} catch (Exception re) {
			logger.debug("Unable to remove tmp dir " + re.getMessage(), re);
		}
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.put("docsZipFile", docsZipFile);
		if (invoice.size() != 0) {
			invoiceTables.add(invoice);
		}
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.put("invoiceTables", invoiceTables);
		AppValidationMsg.validate(retVal, true);
		uTable.setRowKey(oldKey);
		message = "";
	}

	public final String doSelectedReview(UIXTable table) {
		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		String ret = null;
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.YES)) {

			Iterator<?> it = table.getSelectionState().getKeySet().iterator();
			uTable = table;
			List<Integer> pIDs = new ArrayList<Integer>();

			while (it.hasNext()) {
				Object obj = it.next();
				table.setRowKey(obj);

				if (postState) {
					InvoiceList inv = (InvoiceList) table.getRowData();
					if (inv != null) {
						pIDs.add(inv.getEmissionsRptId());
					}
				} else {
					ProcessActivity pa = (ProcessActivity) table.getRowData();
					pIDs.add(pa.getExternalId());
				}
			}

			processIDs = pIDs;

			if (postState) {// get Invoice Date, only in Post to Revenues Step.
				if (this.templateName.equals("SMTV/TV Emission Review")) {
					ret = "dialog:tvInvoiceDate";
				} else if (this.templateName.equals("Blue Card Review")) {
					ret = "dialog:ntvInvoiceDate";
				}
			} else {
				doPrint();
				FacesUtil.startModelessDialog("../inv/invoiceRevenueList.jsf",
						600, 800);
			}
		}
		return ret;
	}

	/*
	 * This method is called from the review invoicelist popup which is based on
	 * invoicelist, but a task is completed with ProcessActivity. Common field
	 * between workflow aggregate list and invoicelist is processId, so based on
	 * the tasks selected in popup it is matched with the aggrTable and removed
	 * the tasks which are not selected.
	 */
	public final String checkInSelected(UIXTable reviewTable) {
		ActivityProfile activityProfile = (ActivityProfile) FacesUtil
				.getManagedBean("activityProfile");

		Iterator<?> it = reviewTable.getSelectionState().getKeySet().iterator();// uTable
		Iterator<?> cKeySet = aggrTable.getSelectionState().getKeySet()
				.iterator();

		List<Integer> pId = new ArrayList<Integer>();
        if(trace) {
            StringBuffer sb = new StringBuffer();
            for(Integer ii: processIDs) {
                sb.append(ii.toString() + " ");
            }
            logger.error("Debug Info:  At checkInSelected(), processIDs contains: " + sb.toString());
        }

		while (it.hasNext()) {
			Object obj = it.next();
			reviewTable.setRowKey(obj);
			InvoiceList invoice = (InvoiceList) reviewTable.getRowData();// uTable
			if (invoice != null) {
				pId.add(invoice.getProcessId());
			}
		}
        
        if(trace) {
            StringBuffer sb = new StringBuffer();
            for(Integer ii: pId) {
                sb.append(ii.toString() + " ");
            }
            logger.error("Debug Info:  At checkInSelected(), pId contains: " + sb.toString());
        }

		while (cKeySet.hasNext()) {
			Object obj = cKeySet.next();
			aggrTable.setRowKey(obj);
			ProcessActivity pa = (ProcessActivity) aggrTable.getRowData();
			boolean selected = false;

			for (Integer i : pId) {
				if (pa.getProcessId() == i) {
                    // should we keep it--remove it if it failed validation
                    if(processIDs.contains(pa.getExternalId())) {
                        selected = true;
                        if(trace) {
                            logger.error("Debug Info:  At checkInSelected(), selected set to true for pa.getExternalId()");
                        }
                    }
					break;
				}
			}

			if (!selected) {
				cKeySet.remove();
                if(trace) {
                    logger.error("Debug Info:  At checkInSelected(), selected not true for pa.getExternalId()");
                }
			}
		}

		activityProfile.setTable(aggrTable);

		return activityProfile.checkInSelected();
	}

	@SuppressWarnings("unchecked")
    public final void doSelected(UIXTable table) {
		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");

		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.YES)) {
			Iterator<?> it = table.getSelectionState().getKeySet().iterator();

			aggrTable = table;
			HashMap<Integer, Integer> pIDs = new HashMap<Integer, Integer>();
			List<EmissionsReport> reportList = new ArrayList<EmissionsReport>();
			postState = false;
			boolean rowSelected = it.hasNext();

			try {
				user = InfrastructureDefs.getPortalUser();
				if (user == null && !InfrastructureBO.testWithoutRevenues) {
					DisplayUtil.displayError("User is null");
					return;
				}

				while (it.hasNext()) {
					Object obj = it.next();
					table.setRowKey(obj);
					ProcessActivity pa = (ProcessActivity) table.getRowData();
					pIDs.put(pa.getExternalId(), pa.getProcessId());

					if (pa.getActivityTemplateNm().contains("Post to Revenues")) {
						postState = true;					
						EmissionsReport report = getEmissionsReportService()
								.retrieveEmissionsReport(pa.getExternalId(),
										false);
						if (report != null) {							
							reportList.add(report);
						}
					}
				}

				if (postState) {
				    InvoiceList row;
				    List<InvoiceList> mRow = new ArrayList<InvoiceList>();

				    for (EmissionsReport rpt : reportList) {
                        row = new InvoiceList();
				        Invoice inv = getInfrastructureService().retrieveInvoiceByReportID(user,
				                rpt.getEmissionsRptId());
                        if(inv != null){
                            if(inv.getPrevInvoiceFailureMsg() != null) {
                                // skip this invoice because there is earlier work to do.
                                // Also, retrieveInvoiceByReportID() only sets prevInvoiceFailureMsg
                                // if there is a previous uncompleted invoice.
                                continue;
                            }
                            row.setInvoiceId(inv.getInvoiceId());
                            row.setRevenueId(inv.getRevenueId());
                            row.setRevenueTypeCd(inv.getRevenueTypeCd());
                            row.setOrigAmount(inv.getOrigAmount());
                            if (inv.getCompInvoice() != null) {
                                row.setInvoiceDifference(inv
                                        .getCompInvoice().getOrigAmount());
                            }
                        } else{
                            logger.debug("Invoice not found for ER (probably because amount due is zero):"+ row.getEmissionsRptId());
                        }
				        Facility facility = getFacilityService().retrieveFacility(rpt.getFpId());
                        if(facility != null){
                            row.setFacilityId(facility.getFacilityId());
                            row.setFacilityName(facility.getName());
                        }
				        row.setProcessId(pIDs.get(rpt.getEmissionsRptId()));								
				        row.setEmissionsRptId(rpt.getEmissionsRptId());
				        row.setReportYear(rpt.getYears());
				        row.setPrevReport(rpt.getReportModified());
				        row.setTotalTons(rpt.getTotalEmissions());
				        mRow.add(row);
				    }

				    reviewInvoiceList = mRow.toArray(new InvoiceList[0]);
				    FacesContext.getCurrentInstance().getExternalContext()
				    .getSessionMap().put("reviewInvoiceList", reviewInvoiceList);
				    FacesUtil.startModelessDialog(
				            "../inv/reviewInvoiceList.jsf", 600, 950);
				} 
				else if(!postState && rowSelected){
				    doSelectedReview(table);
				}

			} catch (RemoteException re) {
				re.printStackTrace();
				logger.error("Exception in review List :" + re.getMessage(),
								re);
			}
		}
        enablePostAdjustButton = true;
        enableCompletionButton = false;

	}

    protected Invoice setUpInvoice(Integer externalId) throws DAOException {
        Invoice inv = null;
        try {
            User user = InfrastructureDefs.getPortalUser();
            inv = getInfrastructureService()
                    .retrieveInvoiceByReportID(user, externalId);

        } catch (RemoteException re) {
            String s = "Cannot retrieve invoice for report " + externalId;
            logger.error(s, re);
            throw new DAOException(s, re);
        }
        return inv;
    }

	/*
	 * Adds the invoice document to tmp directory to zip, Since invoice
	 * documents are stored in facilityId/EmissionsReport/ReportId.
	 * 
	 */
	private void addDocumentToTempDir(Invoice invoice) {

		String docPath = null;
		String newName = null;
		if (invoice.getReportInvDocument() != null) {
			docPath = invoice.getReportInvDocument().getPath();
			String name = invoice.getFacilityId() + "-"
					+ invoice.getEmissionsRptId() + "-"
					+ invoice.getInvoiceId() + ".docx";
			newName = docPath.replace(docPath, name);
		}

		if (docPath != null && tmpDirName != null) {
			try {
				DocumentUtil.copyDocument(docPath, tmpDirName + File.separator
						+ newName);

			} catch (Exception e) {
				logger.debug("Cannot copy document :" + e.getMessage(), e);
			}
		}
	}

	public final String doAggregatePrint() {
		String ret;
		doPrint();
		ret = "dialog:invPostList";

		return ret;
	}

	public final void cancellation(Integer externalId) {
		try{
			getEmissionsReportService().cancelEmissionsReport(externalId);
			
		} catch (RemoteException re) {
			re.printStackTrace();
			logger.error("Exception in cancellation :" + re.getMessage(),
							re);
		}	   
	}
	 
	public Timestamp getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Timestamp invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public final String getInvoiceMsg() {
		return INVOICE_DT_MSG;
	}

	public boolean isPostState() {
		return postState;
	}

	public String getCurrentUser() {
		return currentUser;
	}

    public boolean isEnableCompletionButton() {
        return enableCompletionButton;
    }

    public boolean isEnablePostAdjustButton() {
        return enablePostAdjustButton;
    }

    public void setEnableCompletionButton(boolean enableCompletionButton) {
        this.enableCompletionButton = enableCompletionButton;
    }

    public void setEnablePostAdjustButton(boolean enablePostAdjustButton) {
        this.enablePostAdjustButton = enablePostAdjustButton;
    }

	// Uses getValidationDlgReference() defined below

	// Uses validationDlgAction() defined below
}
