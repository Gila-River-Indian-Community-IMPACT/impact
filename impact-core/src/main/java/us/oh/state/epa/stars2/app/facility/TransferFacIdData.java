package us.oh.state.epa.stars2.app.facility;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.faces.event.ActionEvent;

import us.oh.state.epa.stars2.bo.VirtualExchangeServiceBO.TransferResult;
import us.oh.state.epa.stars2.bo.VirtualExchangeServiceService;
import us.oh.state.epa.stars2.database.dbObjects.ves.TransferLogEntry;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.wy.state.deq.impact.app.Status;

@SuppressWarnings("serial")
public class TransferFacIdData extends ValidationBase {

    private VirtualExchangeServiceService virtualExchangeServiceService;

    private TransferResult transferResult;
    
    private Future<TransferResult> asyncTransferResult;
    
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

	public String initTransferData() {
		if (!isTransferInProgress()) {
			setMaximum(100);
			setValue(0);
		}
		loadLogEntries();
		return "transferFacIdDataToStagingTables";
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
		return null == getTransferResult() ? 0 : getTransferResult().getValue();
	}

	public void cancelPendingDataTransfer() {
		boolean pendingFound = false;
		try {
			List<TransferLogEntry> pendingTransfers = 
					getVirtualExchangeServiceService().retrievePendingTransferLogEntries();
			for (TransferLogEntry pendingTransfer : pendingTransfers) {
				if ("FACID".equals(pendingTransfer.getDomain())) {
					pendingFound = true;
					setTransferInProgress(false);
					if (null != getTransferResult()) {
						getTransferResult().setStatus(Status.CANCELED);
					}

					pendingTransfer.setStatus(Status.CANCELED.getValue());
					pendingTransfer.setMessage("Canceled by " + InfrastructureDefs.getCurrentUserAttrs().getUserName());
					pendingTransfer.setEndDate(new Timestamp(new Date().getTime()));
					pendingTransfer.setDuration(calculateDuration(pendingTransfer));
					try {
						getVirtualExchangeServiceService().updateTransferLogEntry(pendingTransfer);
						DisplayUtil.displayInfo("Transfer canceled.  ID: " + pendingTransfer.getId());
					} catch (DAOException e1) {
						handleException("Error occurred while canceling FACID transfer: " + e1, e1);
					}
				}
			}
		} catch (DAOException e1) {
			handleException("Error occurred while canceling FACID transfer: " + e1, e1);
		}
		if (!pendingFound) {
			DisplayUtil.displayInfo("No pending transfers were found.");
		}
	}

	public void transferData() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}

		if (checkForPendingTransfers()) {
			return;
		}

		setTransferType("FACID");
		String username = InfrastructureDefs.getCurrentUserAttrs().getUserName();
		setTransferResult(new TransferResult("FACID"));
		getTransferResult().setStatus(Status.PENDING);
		getTransferResult().setMaximum(getMaximum());

		TransferLogEntry logEntry = getTransferResult().getLogEntry();
		logEntry.setType(getTransferType()); //TODO: raw string
		logEntry.setStartDate(new Timestamp(new Date().getTime()));
		logEntry.setUsername(username);

		try {
			getVirtualExchangeServiceService().createTransferLogEntry(getTransferResult());
			setTransferInProgress(true);
			initTransferData();

		} catch (DAOException e) {
			handleException("Error occurred while transferring FACID data: " + e, e);
			setTransferInProgress(false);
			getTransferResult().setStatus(Status.FAILURE);

			logEntry.setMessage(e.getMessage());
			logEntry.setEndDate(new Timestamp(new Date().getTime()));
			logEntry.setDuration(calculateDuration(logEntry));
			try {
				getVirtualExchangeServiceService().updateTransferLogEntry(getTransferResult());
			} catch (DAOException e1) {
				handleException("Error occurred while logging FACID transfer: " + e1, e1);
			}
		}

		asyncTransferResult = getVirtualExchangeServiceService().transferFacIdData(getTransferResult(),
				username);
	}

	private boolean checkForPendingTransfers() {
		try {
			List<TransferLogEntry> pendingTransfers = 
					getVirtualExchangeServiceService().retrievePendingTransferLogEntries();
			for (TransferLogEntry pendingTransfer : pendingTransfers) {
				if ("FACID".equals(pendingTransfer.getDomain())) {
					DisplayUtil.displayError("FACID transfer already pending.  ID: " + pendingTransfer.getId());
					return true;
				}
			}
		} catch (DAOException e1) {
			handleException("Error occurred while reading transfer log: " + e1, e1);
		}
		return false;
	}

	public void reset() {
		setTransferType(null);
	}

	
	private long calculateDuration(TransferLogEntry transferLogEntry) {
		return (transferLogEntry.getEndDate().getTime()	- transferLogEntry.getStartDate().getTime());
	}

	public String refreshPage(ActionEvent e) {
		clearButtonClicked();
		loadLogEntries();
		return "transferFacIdDataToStagingTables";
	}

	
	public boolean hasTransferLogEntries() {
		return getTransferLogEntries() == null ? false : getTransferLogEntries().size() > 0;
	}

	public String complete() {
		if (Status.FAILURE == getTransferResult().getStatus()) {	
			DisplayUtil.displayError(getTransferResult().getLogEntry().getMessage());
		} else {
			DisplayUtil.displayInfo(getTransferResult().getLogEntry().getMessage());
		}
		setTransferInProgress(false);
		return initTransferData();
	}

	public VirtualExchangeServiceService getVirtualExchangeServiceService() {
		return virtualExchangeServiceService;
	}

	public void setVirtualExchangeServiceService(VirtualExchangeServiceService virtualExchangeServiceService) {
		this.virtualExchangeServiceService = virtualExchangeServiceService;
	}
	
	public boolean isTransferComplete() {
		return getValue() == getMaximum();
	}

	public TransferResult getTransferResult() {
		return transferResult;
	}

	public void setTransferResult(TransferResult transferResult) {
		this.transferResult = transferResult;
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
