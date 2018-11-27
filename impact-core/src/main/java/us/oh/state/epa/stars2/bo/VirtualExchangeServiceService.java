package us.oh.state.epa.stars2.bo;

import java.util.List;
import java.util.concurrent.Future;

import us.oh.state.epa.stars2.bo.VirtualExchangeServiceBO.TransferResult;
import us.oh.state.epa.stars2.database.dbObjects.ves.TransferLogEntry;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface VirtualExchangeServiceService {

	List<TransferLogEntry> retrieveTransferLogEntries() throws DAOException;

	TransferLogEntry createTransferLogEntry(TransferResult transferResult) throws DAOException;

	List<TransferLogEntry> retrievePendingTransferLogEntries() throws DAOException;

	void updateTransferLogEntry(TransferLogEntry transferLogEntry) throws DAOException;

	void updateTransferLogEntry(TransferResult transferResult) throws DAOException;

	Future<TransferResult> transferEisEiData(TransferResult transferResult, 
			String username, Integer reportingYear, String[] permitClassCds);
	
	Future<TransferResult> transferEisFacilityData(TransferResult transferResult, String username,
			Integer reportingYear, String[] permitClassCds);

	Future<TransferResult> transferFacIdData(TransferResult transferResult, String username);
}
