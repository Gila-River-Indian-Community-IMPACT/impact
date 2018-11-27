package us.oh.state.epa.stars2.app.tools;

import java.io.File;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dbObjects.permit.PTIOPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitChargePayment;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitDocument;
import us.oh.state.epa.stars2.def.CorrespondenceAttachmentTypeDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.NSRBillingChargePaymentTypeDef;
import us.oh.state.epa.stars2.def.NSRBillingFeeTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.wy.state.deq.impact.App;

import com.aspose.words.ImportFormatMode;

@SuppressWarnings("serial")
public class GenerateBulkNSRBillingInvoice extends BulkOperation {
	
	public static String TMP_FILE_STORE_PATH = "tmp";
	public static String MERGED_INVOICE_FILE_NAME = "mergedinvoice";
	public static String MERGED_INVOICE_FILE_EXTN = ".docx";

	private Permit[] permits;

	private FormResultNSRInvoice[] results;

	private static boolean operationInProgress;

	private PermitService permitService = App.getApplicationContext().getBean(
			PermitService.class);
	
	private com.aspose.words.Document mergedInvoiceDocument;
	private String mergedInvoiceDocPath;
	
	public GenerateBulkNSRBillingInvoice() {
		super();
		setButtonName("Generate Invoices");
		setNavigation("dialog:bulkInvoices");
	}

	/**
	 * Each bulk operation is responsible for providing a search operation based
	 * on the parameters gathered by the BulkOperationsCatalog bean. The search
	 * is run when the "Select" button on the Bulk Operations screen is clicked.
	 * The BulkOperationsCatalog will then display the results of the search.
	 * Below the results is a "Bulk Operation" button.
	 */

	public final void searchPermits(BulkOperationsCatalog catalog)
			throws RemoteException {

		this.catalog = catalog;

		List<PTIOPermit> pList = getPermitService().searchPermitsForFinalInvoice(
				catalog.getCompanyName(), 
				catalog.getFacilityId(),
				catalog.getFacilityNm(),
				true);

		setMaximum(pList.size());
		setValue(pList.size());

		setSearchStarted(true);

		catalog.setPermits(pList.toArray(new PTIOPermit[0]));
		catalog.setGenerateNSRInvoiceFlag(true);  // used to disable button
		setSearchCompleted(true);

		return;

	}

	/**
	 * When the "Bulk Operation" button is clicked, the BulkOperationsCatalog
	 * class will call this method, ensure that the BulkOperation class is
	 * placed into the jsf context, and return the value of the getNavigation()
	 * method to the jsf controller. Any further requests from the page that is
	 * navigated to are handled by this bean in the normal way. The default
	 * version does no preliminary work.
	 * @throws RemoteException 
	 */
	public final void performPreliminaryWork(BulkOperationsCatalog lcatalog) throws RemoteException {

		this.catalog = lcatalog;

		permits = catalog.getSelectedPermits();
		results = new FormResultNSRInvoice[permits.length];

		SimpleDateFormat sdf = new SimpleDateFormat("MM_dd_yy_HH_mm_ss");
		String timeStamp = sdf.format(new Timestamp(System.currentTimeMillis()));

		mergedInvoiceDocPath = TMP_FILE_STORE_PATH + File.separator + MERGED_INVOICE_FILE_NAME + "_" + timeStamp
				+ MERGED_INVOICE_FILE_EXTN;

		if (permits.length != 0) {

			int successCount = 0;
			int failureCount = 0;

			catalog.setMaximum(permits.length);
			catalog.setValue(0);
			catalog.setShowProgressBarInvoiceGeneration(true);

			for (int i = 0; i < permits.length; i++) {

				int count = i + 1;
				catalog.setValue(count);

				try {

					results[i] = new FormResultNSRInvoice();
					results[i].setId(permits[i].getPermitNumber());
					results[i].setFacilityId(permits[i].getFacilityId());

					Permit tp = getPermitService().retrievePermit(permits[i].getPermitID()).getPermit();

					if (isFinalInvoiceGenerated(tp)) {
						
						String logStr = "Final Invoice already exists...may have been generated manually or popup from previous bulk operation may have been closed in unexpected manner.";
						results[i].setNotes(logStr);

					} else {

						Calendar cal = Calendar.getInstance();
						cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
						cal = Calendar.getInstance();
						Timestamp cd = new Timestamp(cal.getTimeInMillis());

						getPermitService().generateNSRPermitInvoice(tp, NSRBillingFeeTypeDef.FINAL_INVOICE, cd);
							tp = getPermitService().retrievePermit(permits[i].getPermitID()).getPermit();
						
						PermitDocument finalInvoice = tp.getFinalInvoiceDocument();

						results[i].setFormURL(finalInvoice.getDocURL());

						results[i].setFileName(finalInvoice.getDescription());

						results[i].setFinalInvoiceAmount(((PTIOPermit) tp).getFinalInvoiceAmount());

						// Merge the generated final invoice document(s).
						String finalInvoiceDocPath = DocumentUtil.getFileStoreRootPath() + finalInvoice.getPath();
						if (null == mergedInvoiceDocument) {
							mergedInvoiceDocument = new com.aspose.words.Document(finalInvoiceDocPath);
						} else {
							com.aspose.words.Document finalInvoiceDocument = new com.aspose.words.Document(
									finalInvoiceDocPath);
							mergedInvoiceDocument = DocumentUtil.mergeDocuments(mergedInvoiceDocument,
									finalInvoiceDocument, ImportFormatMode.USE_DESTINATION_STYLES);
						}
						
						// After the invoice is generated ensure that the
						// invoiced charges minus received payments
						// is equal to the current balance.
						if (!getPermitService().isCurrentBalanceValid(tp, cd)) {
							results[i].setNotes(
									"Warning: The sum of the invoiced charges minus the received payments does not equal "
											+ "the Current Balance. Please review the fee information for correctness.");
						} else {
							results[i].setNotes("Invoice generated successfully.");
						}
					}
					
					successCount++;

				} catch (DAOException daoe) {
					String logStr = daoe.getMessage();
					logger.error(logStr, daoe);
					results[i].setNotes(logStr);
					failureCount++;
				} catch (Exception e) {
					String logStr = e.getMessage();
					logger.error(logStr, e);
					results[i].setNotes(logStr);
					failureCount++;
				}
			}

			catalog.setShowProgressBarInvoiceGeneration(false);

			if (null != mergedInvoiceDocument) {
				// Save the merged invoice document.
				try {
					String filePath = DocumentUtil.getFileStoreRootPath() + File.separator + mergedInvoiceDocPath;
					mergedInvoiceDocument.save(filePath);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}

			if (successCount > 0 && failureCount == 0) {
				DisplayUtil.displayInfo("All invoice(s) generated successfully.");
			} else if (failureCount > 0) {
				DisplayUtil.displayError("Unable to generate final invoices for all " + permits.length
						+ " selected Permits. " + "Failure count was " + failureCount + ", success count was "
						+ (permits.length - failureCount) + ".");
			}
		}
		
		return;
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
	public final String performPostWork(BulkOperationsCatalog lcatalog) {
		
		this.catalog = lcatalog;

		if (isOperationInProgress()) {
			DisplayUtil
					.displayError("An another instance of bulk operation is already in progress."
							+ " Please wait for the operation to finish.");
			return ERROR;
		}

		boolean ok = true;

		setOperationInProgress(false);

		if (ok) {
			mergedInvoiceDocument = null;
			DisplayUtil.displayInfo("Operation completed successfully.");
		}

		return "tools.docGenerationCatalog";
	}

	public boolean isOperationInProgress() {
		return operationInProgress;
	}

	public void setOperationInProgress(boolean opInProgress) {
		operationInProgress = opInProgress;
	}

	public DefSelectItems getAttachmentTypesDef() {
		return CorrespondenceAttachmentTypeDef.getData().getItems();
	}

	public final FormResultNSRInvoice[] getResults() {
		return results;
	}

	public PermitService getPermitService() {
		return permitService;
	}

	public void setPermitService(PermitService permitService) {
		this.permitService = permitService;
	}

	public boolean isFinalInvoiceGenerated(Permit permit) {
		boolean ret = false;
		for (PermitChargePayment pcp : ((PTIOPermit) permit)
				.getPermitChargePaymentList()) {
			if (pcp.getTransactionType().equalsIgnoreCase(
					NSRBillingChargePaymentTypeDef.FINAL_INVOICE)) {
				ret = true;
				break;
			}
		}

		return ret;
	}

	public String getMergedInvoiceDocPath() {
		return mergedInvoiceDocPath;
	}

	public void setMergedInvoiceDocPath(String mergedInvoiceDocPath) {
		this.mergedInvoiceDocPath = mergedInvoiceDocPath;
	}
	
	public String getMergedInvoiceDocURL() {
		
		if(null != mergedInvoiceDocument && null != mergedInvoiceDocPath) {
			return DocumentUtil.getFileStoreBaseURL() + File.separator + mergedInvoiceDocPath;
		} else {
			return null;
		}
	}
	
	public boolean isMergedInvoiceDocEmpty() {
		return null == mergedInvoiceDocument;
	}
	
}
