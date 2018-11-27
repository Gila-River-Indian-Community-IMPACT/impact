package us.oh.state.epa.stars2.app.tools;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.faces.event.ActionEvent;

import us.oh.state.epa.stars2.bo.CorrespondenceService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.wy.state.deq.impact.App;

@SuppressWarnings("serial")
public abstract class BulkOperation extends AppBase {

	protected BulkOperationsCatalog catalog;

	/** Default name of the button that accomplishes the bulk operation. */
	private String bulkOpButtonName = "Bulk Operation";

	/**
	 * String used to determine the page to be navigated to when the "Bulk
	 * Operation" button is clicked. The default value returns the user to the
	 * "Bulk Operations" jsp page.
	 */
	private String navigation = "tools.bulkOperationsCatalog";

	/**
	 * String used to provide current status of ongoing operation when the
	 * BulkOperation is being run in a background thread.
	 */
	private String _statusMsg = "";

	/** BulkOperation has started its initial search. */
	private boolean _searchStarted;

	/** BulkOperation has completed its initial search. */
	private boolean _searchComplete;

	/**
	 * Synchronous search operation completeted
	 */
	private boolean hasSearchResults;
	
	private FacilityService facilityService = 
			App.getApplicationContext().getBean(FacilityService.class); //TODO reconsider static call

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}
	
	private CorrespondenceService correspondenceService = App.getApplicationContext().getBean(CorrespondenceService.class);

	
	public CorrespondenceService getCorrespondenceService() {
		return correspondenceService;
	}
	
	public void setCorrespondenceService(CorrespondenceService correspondenceService) {
		this.correspondenceService = correspondenceService;
	}
	
	/**
	 * Initialize the BulkOperation. Typically, the BulkOperation may need to
	 * initialize default values in one or more of the BulkOperationsCatalog
	 * search fields. This method is called by the BulkOperationsCatalog after
	 * the BulkOperation is selected, but before the search screen fields are
	 * displayed.
	 */
	public void init(BulkOperationsCatalog catalog) {
		return;
	}

	/**
	 * Each bulk operation is responsible for providing a search operation based
	 * on the paramters gathered by the BulkOperationsCatalog bean. The search
	 * is run when the "Select" button on the Bulk Operations screen is clicked.
	 * The BulkOperationsCatalog will then display the results of the search.
	 * Below the results is a "Bulk Operation" button.
	 * 
	 * @param catalog
	 * @return
	 * @throws RemoteException
	 */
	// public FacilityList[] searchFacilities(BulkOperationsCatalog catalog)
	public void searchFacilities(BulkOperationsCatalog catalog)
			throws RemoteException {

		return;
	}
	
	/**
	 * Searches contacts and retrieves their contact types.
	 * @param catalog
	 * @throws RemoteException
	 */
	public void searchContactTypes(BulkOperationsCatalog catalog)
			throws RemoteException {

		return;
	}

	/**
	 * @param catalog
	 * @return
	 * @throws RemoteException
	 */
	public void searchPermits(BulkOperationsCatalog catalog)
			throws RemoteException {

		return;
	}

	/**
	 * @param catalog
	 * @return
	 * @throws RemoteException
	 */
	public void searchApplications(BulkOperationsCatalog catalog)
			throws RemoteException {

		return;
	}

	/**
	 * @param catalog
	 * @return
	 * @throws RemoteException
	 */
	public EmissionsReportSearch[] searchEmissionsReports(
			BulkOperationsCatalog catalog) throws RemoteException {

		return new EmissionsReportSearch[0];
	}

	/**
	 * @param catalog
	 * @return
	 * @throws RemoteException
	 */
	public ComplianceReportList[] searchComplianceReports(
			BulkOperationsCatalog catalog) throws RemoteException {

		return new ComplianceReportList[0];
	}

	/**
	 * @param catalog
	 * @return
	 * @throws RemoteException
	 */
	public void searchInvoices(BulkOperationsCatalog catalog)
			throws RemoteException {

		return;
	}

	/**
	 * When the "Bulk Operation" button is clicked, the BulkOperationsCatalog
	 * class will call this method and return the value of the getNavigation()
	 * method to the jsf controller. Any further requests from the page that is
	 * navigated to are handled by this (the derived) class in the normal way.
	 * The default version does no preliminary work.
	 * 
	 * @param catalog
	 */
	public abstract void performPreliminaryWork(BulkOperationsCatalog catalog)
			throws RemoteException, IOException;

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
	public abstract String performPostWork(BulkOperationsCatalog catalog)
			throws RemoteException;

	public void reset() {
	}

	public void cancel() {
	}

	public void correspondence(ActionEvent actionEvent) throws RemoteException {
	}

	/**
	 * Label of the Bulk Operation button displayed beneath the search results.
	 * 
	 * @return
	 */
	public final String getButtonName() {
		return bulkOpButtonName;
	}

	/**
	 * Label of the Bulk Operation button displayed beneath the search results.
	 * 
	 * @param buttonName
	 */
	public final void setButtonName(String buttonName) {
		if (buttonName != null && buttonName.length() > 0) {
			bulkOpButtonName = buttonName;
		}
	}

	/**
	 * String used to determine the page to be navigated to when the "Bulk
	 * Operation" button is clicked. The default value returns the user to the
	 * "Bulk Operations" jsp page.
	 * 
	 * @return
	 */
	public final String getNavigation() {
		return navigation;
	}

	/**
	 * String used to determine the page to be navigated to when the "Bulk
	 * Operation" button is clicked. The default value returns the user to the
	 * "Bulk Operations" jsp page.
	 * 
	 * @param navigation
	 */
	public final void setNavigation(String navigation) {
		if (navigation != null && navigation.length() > 0) {
			this.navigation = navigation;
		}
	}

	/**
	 * String used to provide current status of ongoing operation when the
	 * BulkOperation is being run in a background thread.
	 */
	public final synchronized String getStatusMessage() {
		return _statusMsg;
	}

	/**
	 * String used to provide current status of ongoing operation when the
	 * BulkOperation is being run in a background thread.
	 */
	public final synchronized void setStatusMessage(String statusMsg) {
		_statusMsg = statusMsg;
	}

	/** Start the thread that will do the search work. */
	public final synchronized void start(Thread thread) {
		setSearchCompleted(false);
		thread.start();
	}

	/** BulkOperation has started its initial search. */
	public final synchronized boolean isSearchStarted() {
		return _searchStarted;
	}

	/** BulkOperation has started its initial search. */
	public final synchronized void setSearchStarted(boolean started) {
		_searchStarted = started;
		if (_searchStarted && catalog != null && !catalog.isSyncSearchEnabled()){
			logger.debug("Progress bar should be shown");
			catalog.setShowProgressBar(true);
		}
	}

	/** BulkOperation has completed its initial search. */
	public final synchronized boolean isSearchCompleted() {
		return _searchComplete;
	}

	/** BulkOperation has completed its initial search. */
	public final synchronized void setSearchCompleted(boolean complete) {
		_searchComplete = complete;
		if (catalog != null && !catalog.isSyncSearchEnabled()) {
			if (_searchComplete) {
				logger.debug("Asynchronous search is completed");
				catalog.setInfoMessage("Search completed.");
				catalog.setShowProgressBar(false);
				catalog.setRefreshStr(" ");
			}
		}

	}

	public final boolean isHasSearchResults() {
		return hasSearchResults;
	}

	/** BulkOperation has completed its initial search. */
	public final void setHasSearchResults(boolean complete) {
		this.hasSearchResults = complete;
		if (catalog != null) {
			if (complete) {
				DisplayUtil.displayInfo("Search completed.");
			}
		}

	}
	
	/**
	 * When the "Bulk Operation" button is clicked, the BulkOperationsCatalog
	 * class will call this method and return the value of the getNavigation()
	 * method to the jsf controller. Any further requests from the page that is
	 * navigated to are handled by this (the derived) class in the normal way.
	 * The default version does no preliminary work.
	 * 
	 * @param catalog
	 */
	public void searchInspections(BulkOperationsCatalog catalog)
			throws RemoteException {
		return;
	}
	
	public void searchFacilityPurgeCandidates(BulkOperationsCatalog catalog) throws RemoteException {
		return;
	}
}
