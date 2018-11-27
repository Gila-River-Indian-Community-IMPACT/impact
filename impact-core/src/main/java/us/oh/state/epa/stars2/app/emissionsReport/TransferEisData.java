package us.oh.state.epa.stars2.app.emissionsReport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import javax.faces.event.ActionEvent;

import us.oh.state.epa.stars2.bo.VirtualExchangeServiceBO.TransferResult;
import us.oh.state.epa.stars2.bo.VirtualExchangeServiceService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.ves.TransferLogEntry;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.wy.state.deq.impact.app.Status;

@SuppressWarnings("serial")
public class TransferEisData extends ValidationBase {

    private Integer reportingYear;
    
    private String[] permitClassCds;

    private VirtualExchangeServiceService virtualExchangeServiceService;

    private TransferResult transferEisResult;
    
    private Future<TransferResult> asyncTransferEisResult;
    
	private TableSorter transferLogEntriesWrapper;

	private List<TransferLogEntry> transferLogEntries;

    private boolean transferInProgress;
    
    private String transferType;
    
	public List<TransferLogEntry> getTransferLogEntries() {
		return transferLogEntries;
	}

	public void setTransferLogEntries(List<TransferLogEntry> transferLogEntries) {
		this.transferLogEntries = transferLogEntries;
	}

	public String initTransferEisData() {
		if (!isTransferInProgress()) {
			setMaximum(100);
			setValue(0);
			reset();
		}
		loadLogEntries();
		return "transferEisDataToStagingTables";
	}

	private void loadLogEntries() {
		setTransferLogEntriesWrapper(new TableSorter());
		try {
			setTransferLogEntries(getVirtualExchangeServiceService().retrieveTransferLogEntries());
			getTransferLogEntriesWrapper().setWrappedData(getTransferLogEntries());
		} catch (DAOException e) {
			handleException("Error occurred while loading transfer log: " + e,e);
			setTransferInProgress(false);
		}
	}


	@Override
	public long getValue() {
		return null == getTransferEisResult() ? 0 : getTransferEisResult().getValue();
	}

	public void cancelPendingEisEiDataTransfer() {
		boolean pendingFound = false;
		try {
			List<TransferLogEntry> pendingTransfers = 
					getVirtualExchangeServiceService().retrievePendingTransferLogEntries();
			for (TransferLogEntry pendingTransfer : pendingTransfers) {
				if ("EIS".equals(pendingTransfer.getDomain())) {
					pendingFound = true;
					setTransferInProgress(false);
					if (null != getTransferEisResult()) {
						getTransferEisResult().setStatus(Status.CANCELED);
					}

					pendingTransfer.setStatus(Status.CANCELED.getValue());
					pendingTransfer.setMessage("Canceled by " + InfrastructureDefs.getCurrentUserAttrs().getUserName());
					pendingTransfer.setEndDate(new Timestamp(new Date().getTime()));
					pendingTransfer.setDuration(calculateDuration(pendingTransfer));
					try {
						getVirtualExchangeServiceService().updateTransferLogEntry(pendingTransfer);
						DisplayUtil.displayInfo("Transfer canceled.  ID: " + pendingTransfer.getId());
					} catch (DAOException e1) {
						handleException("Error occurred while canceling EIS-EI transfer: " + e1, e1);
					}
				}
			}
		} catch (DAOException e1) {
			handleException("Error occurred while canceling EIS-EI transfer: " + e1, e1);
		}
		if (!pendingFound) {
			DisplayUtil.displayInfo("No pending transfers were found.");
		}
	}

	public void transferEisData() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}

		if(displayValidationMessages("transferEisData:",
        		this.validate())) {
        	return;
        }


		if (checkForPendingEISTransfers()) {
			return;
		}
		
		String username = InfrastructureDefs.getCurrentUserAttrs().getUserName();
		setTransferEisEiResult(new TransferResult("EIS"));
		getTransferEisResult().setStatus(Status.PENDING);
		getTransferEisResult().setMaximum(getMaximum());

		TransferLogEntry logEntry = getTransferEisResult().getLogEntry();
		logEntry.setType(getTransferType()); //TODO: raw string
		logEntry.setStartDate(new Timestamp(new Date().getTime()));
		logEntry.setUsername(username);
		logEntry.setReportingYear(reportingYear);
		logEntry.setFacilityTypes(getPermitClassCdsString());

		try {
			getVirtualExchangeServiceService().createTransferLogEntry(getTransferEisResult());
			setTransferInProgress(true);
			initTransferEisData();

		} catch (DAOException e) {
			handleException("Error occurred while transferring EIS data: " + e, e);
			setTransferInProgress(false);
			getTransferEisResult().setStatus(Status.FAILURE);

			logEntry.setMessage(e.getMessage());
			logEntry.setEndDate(new Timestamp(new Date().getTime()));
			logEntry.setDuration(calculateDuration(logEntry));
			try {
				getVirtualExchangeServiceService().updateTransferLogEntry(getTransferEisResult());
			} catch (DAOException e1) {
				handleException("Error occurred while logging EIS transfer: " + e1, e1);
			}
		}

		if ("EIS-EI".equals(getTransferType())) {
			asyncTransferEisResult = getVirtualExchangeServiceService().transferEisEiData(getTransferEisResult(),
					username, getReportingYear(), getPermitClassCds());
		} else
		if ("EIS-Facility".equals(getTransferType())) {
			asyncTransferEisResult = getVirtualExchangeServiceService().transferEisFacilityData(getTransferEisResult(),
					username, getReportingYear(), getPermitClassCds());
		} else {
			throw new RuntimeException("Cannot determine transfer type.");
		}
	}

	private boolean checkForPendingEISTransfers() {
		try {
			List<TransferLogEntry> pendingTransfers = 
					getVirtualExchangeServiceService().retrievePendingTransferLogEntries();
			for (TransferLogEntry pendingTransfer : pendingTransfers) {
				if ("EIS".equals(pendingTransfer.getDomain())) {
					DisplayUtil.displayError("EIS transfer already pending.  ID: " + pendingTransfer.getId());
					return true;
				}
			}
		} catch (DAOException e1) {
			handleException("Error occurred while reading transfer log: " + e1, e1);
		}
		return false;
	}

	
	private long calculateDuration(TransferLogEntry transferLogEntry) {
		return (transferLogEntry.getEndDate().getTime()	- transferLogEntry.getStartDate().getTime());
	}

	public String refreshPage(ActionEvent e) {
		clearButtonClicked();
		loadLogEntries();
		return "transferEisDataToStagingTables";
	}

	
	private String getPermitClassCdsString() {
		StringBuffer codes = new StringBuffer();
		for (String cd : getPermitClassCds()) {
			codes.append(cd).append(' ');
		}
		return codes.toString().toUpperCase();
	}

	public boolean hasTransferLogEntries() {
		return getTransferLogEntries() == null ? false : getTransferLogEntries().size() > 0;
	}

	public String complete() {
		if (Status.FAILURE == getTransferEisResult().getStatus()) {	
			DisplayUtil.displayError(getTransferEisResult().getLogEntry().getMessage());
		} else {
			DisplayUtil.displayInfo(getTransferEisResult().getLogEntry().getMessage());
		}
		setTransferInProgress(false);
		return initTransferEisData();
	}

	public void reset() {
		setTransferType(null);
		setReportingYear(null);
		setPermitClassCds(null);
	}

	private HashMap<String, ValidationMessage> validationMessages = new HashMap<String, ValidationMessage>(1);
	
	public ValidationMessage[] validate() {
		this.validationMessages.clear();
		
		if(null == this.getReportingYear()) {
			ValidationMessage valMsg = new ValidationMessage("reportingYear", "Set a reporting year.", ValidationMessage.Severity.ERROR);
			this.validationMessages.put("reportingYear", valMsg);
		}
        
		if(Utility.isNullOrEmpty(this.getTransferType())) {
			ValidationMessage valMsg = new ValidationMessage("transferType", "Set a transfer type.", ValidationMessage.Severity.ERROR);
			this.validationMessages.put("transferType", valMsg);
		}
        
		if(null == this.getPermitClassCds() || this.getPermitClassCds().length == 0) {
			ValidationMessage valMsg = new ValidationMessage("permitClassCds", "Set a facility class.", ValidationMessage.Severity.ERROR);
			this.validationMessages.put("permitClassCds", valMsg);
		}
        
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
	



	public VirtualExchangeServiceService getVirtualExchangeServiceService() {
		return virtualExchangeServiceService;
	}

	public void setVirtualExchangeServiceService(VirtualExchangeServiceService virtualExchangeServiceService) {
		this.virtualExchangeServiceService = virtualExchangeServiceService;
	}

	public Integer getReportingYear() {
		return reportingYear;
	}

	public void setReportingYear(Integer reportingYear) {
		this.reportingYear = reportingYear;
	}

	public String[] getPermitClassCds() {
		return permitClassCds;
	}

	public void setPermitClassCds(String[] permitClassCds) {
		this.permitClassCds = permitClassCds;
	}
	
	public boolean isTransferComplete() {
		return getValue() == getMaximum();
	}

	public TransferResult getTransferEisResult() {
		return transferEisResult;
	}

	public void setTransferEisEiResult(TransferResult transferEisEiResult) {
		this.transferEisResult = transferEisEiResult;
	}

	public TableSorter getTransferLogEntriesWrapper() {
		return transferLogEntriesWrapper;
	}

	public void setTransferLogEntriesWrapper(TableSorter transferLogEntriesWrapper) {
		this.transferLogEntriesWrapper = transferLogEntriesWrapper;
	}

	public boolean isTransferInProgress() {
		return transferInProgress;
	}

	public void setTransferInProgress(boolean transferInProgress) {
		this.transferInProgress = transferInProgress;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	
}
