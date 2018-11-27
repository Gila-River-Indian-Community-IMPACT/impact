package us.oh.state.epa.stars2.app.admin;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.app.tools.BulkOperation;
import us.oh.state.epa.stars2.app.tools.BulkOperationsCatalog;
import us.oh.state.epa.stars2.bo.FacilityPurgeBO;
import us.oh.state.epa.stars2.bo.FacilityPurgeService;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityPurgeLog;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityPurgeSearchLineItem;

import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.wy.state.deq.impact.App;

@SuppressWarnings("serial")
public class FacilityPurgingUtil extends BulkOperation{

	private FacilityPurgeBO facilityPurgeService = 
			App.getApplicationContext().getBean(FacilityPurgeBO.class); //TODO reconsider static call

	public FacilityPurgeService getFacilityPurgeService() {
		return facilityPurgeService;
	}

	public void setFacilityPurgeService(FacilityPurgeBO facilityService) {
		this.facilityPurgeService = facilityService;
	}


	private List<FacilityPurgeSearchLineItem> purgeCandidates;

	public FacilityPurgingUtil() {
        super();
        setButtonName("Purge Facilities");
        setNavigation("dialog:confirmPurgeFacility");
    }

	public List<FacilityPurgeSearchLineItem> getPurgeCandidates() {
		return purgeCandidates;
	}

	public void setPurdgeCandidates(List<FacilityPurgeSearchLineItem> purdgeCandidates) {
		this.purgeCandidates = purdgeCandidates;
	}
	
	/***************************************************************************/
	//retrieve facility candidates for purging
	@Override
	public final void searchFacilityPurgeCandidates(BulkOperationsCatalog lcatalog)
	        throws RemoteException {
		this.catalog= lcatalog;
		//retrieve Facility List that can be purged
		Integer months = SystemPropertyDef.getSystemPropertyValueAsInteger("app_retention_policy_months", null);
		
		if (months == null || months.compareTo(0) <= 0 || months.compareTo(2400) > 0){
			purgeCandidates = new ArrayList<FacilityPurgeSearchLineItem>();
			catalog.setPurdgeCandidates(purgeCandidates);
			DisplayUtil.displayInfo("Retention Policy Active Record months is not set or it is not set properly.");
			return;
		} 
		
		try {
			purgeCandidates = getFacilityService().retrieveFacilityListForPurging(months);
			catalog.setPurdgeCandidates(purgeCandidates);
			if (purgeCandidates == null || purgeCandidates.isEmpty()){
				DisplayUtil.displayError("There is no facility satisfying purging criteria.");
			}
		} catch (DAOException e){
			logger.error("Could not retrieve Facility List for Purdging.");
			DisplayUtil.displayError("Retrieve Facility List for Purging failed.");
		}
	}
	
	@Override
	public void performPreliminaryWork(BulkOperationsCatalog catalog) throws RemoteException, IOException {
		// TODO Auto-generated method stub
//		please refer to GenerateBulkNSRBillingInvoice.performPreliminaryWork()
	}

	@Override
	public String performPostWork(BulkOperationsCatalog catalog) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public final void applyFinalAction(){
		String failedPurge = "";
		String successfulPurge = "";
		String failedPurgeDueToPortalMOReport = "";
		for (FacilityPurgeSearchLineItem i : catalog.getSelectedPurgeCandidates()){
			boolean success = false;
			try{
				success = purgeFacility(i);
			} catch(DAOException e){
				if (e != null && e.getMessage() != null && e.getMessage().contains("staging.MO_MONITOR_RPT")){
					failedPurgeDueToPortalMOReport = failedPurgeDueToPortalMOReport + " " + i.getFacilityId();
				}
				logger.error(e.getMessage(), e);
			} 
			if (success){
				successfulPurge = successfulPurge + " " + i.getFacilityId();
			} else {
				failedPurge = failedPurge + " " + i.getFacilityId();
			}
    	}
		if (successfulPurge.length() > 0)
			DisplayUtil.displayInfo("Successfully purged facility: " + successfulPurge + ". Facility purge log is created for each purged facility.");
		if (failedPurge.length() > 0)
			DisplayUtil.displayError("Failed to purge facility: " + failedPurge + ". Please contact system administrator for help.");
		if (failedPurgeDueToPortalMOReport.length() >0){
			DisplayUtil.displayError("For Facility" + failedPurgeDueToPortalMOReport + ", there is incomplete Upload Monitor Report Task from portal. Please complete/delete it before purging the facility.");
		}
		this.catalog.reset();
		this.catalog.submitFacilitPurgeSearch();
		FacesUtil.returnFromDialogAndRefresh();		
	}
	

	/**
 * 
 * @param facility
 * @return
 * @throws DAOException
 */
	public boolean purgeFacility(FacilityPurgeSearchLineItem facility) throws DAOException {

		boolean success = false;

		if (null != facility && null != facility.getFacilityId()) {

			success = getFacilityPurgeService().purgeFacility(facility.getFacilityId());

			createPurgeLogs(facility);

		}
		return success;

	}

	/**
	 * 
	 * @param facility
	 * @throws DAOException
	 */
	private void createPurgeLogs(FacilityPurgeSearchLineItem facility) throws DAOException {
		FacilityPurgeLog facilityPurgeLog = new FacilityPurgeLog();
		facilityPurgeLog.setFacilityId(facility.getFacilityId());
		facilityPurgeLog.setFacilityName(facility.getFacilityName());
		facilityPurgeLog.setCompanyId(facility.getCmpId());
		facilityPurgeLog.setCompanyName(facility.getCompanyName());
		facilityPurgeLog.setShutdownDate(facility.getShutdownDate());
		facilityPurgeLog.setPurgedDate(new Timestamp(System.currentTimeMillis()));
		facilityPurgeLog.setUserId(InfrastructureDefs.getCurrentUserId());
		getFacilityService().createFacilityPurgeLog(facilityPurgeLog);

		
	}

}
