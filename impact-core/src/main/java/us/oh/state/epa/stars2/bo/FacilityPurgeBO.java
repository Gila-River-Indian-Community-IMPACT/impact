package us.oh.state.epa.stars2.bo;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.app.facility.PermitConditionSearchCriteria;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SiteVisit;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReport;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportNote;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityPurgeSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityVersion;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitCondition;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;
import us.wy.state.deq.impact.database.dbObjects.monitoring.Monitor;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroup;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorReport;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorSite;

@Transactional(rollbackFor=Exception.class)
@Service
public class FacilityPurgeBO extends BaseBO implements FacilityPurgeService {
	
	@Autowired
	private PermitService permitService;
	
	@Autowired
	private PermitConditionService permitConditionService;
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private EnforcementActionService enforcementActionService;
	
	@Autowired
	private ComplianceReportService complianceReportService;
	
	@Autowired
	private EmissionsReportService emissionsReportService;
	
	@Autowired
	private FullComplianceEvalService fullComplianceEvalService;
	
	@Autowired
	private StackTestService stackTestService;	
	
	@Autowired		
	private CorrespondenceService correspondenceService;
	
	@Autowired		
	private FacilityService  facilityService;

	@Autowired
	private ReadWorkFlowService workFlowService;

	@Autowired
	private MonitoringService monitoringService;

	private FullComplianceEval[] fullComplianceEvalList;
	
	public PermitService getPermitService() {
		return permitService;
	}

	public void setPermitService(PermitService permitService) {
		this.permitService = permitService;
	}
	
	public ApplicationService getApplicationService() {
		return applicationService;
	}
	
	public PermitConditionService getPermitConditionService() {
		return permitConditionService;
	}

	public void setPermitConditionService(PermitConditionService permitConditionService) {
		this.permitConditionService = permitConditionService;
	}

	public void setApplicationService(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	public EnforcementActionService getEnforcementActionService() {
		return enforcementActionService;
	}

	public void setEnforcementActionService(EnforcementActionService enforcementActionService) {
		this.enforcementActionService = enforcementActionService;
	}

	public ComplianceReportService getComplianceReportService() {
		return complianceReportService;
	}

	public void setComplianceReportService(ComplianceReportService complianceReportService) {
		this.complianceReportService = complianceReportService;
	}

	public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}

	public void setEmissionsReportService(EmissionsReportService emissionsReportService) {
		this.emissionsReportService = emissionsReportService;
	}

	public FullComplianceEvalService getFullComplianceEvalService() {
		return fullComplianceEvalService;
	}

	public void setFullComplianceEvalService(FullComplianceEvalService fullComplianceEvalService) {
		this.fullComplianceEvalService = fullComplianceEvalService;
	}

	public StackTestService getStackTestService() {
		return stackTestService;
	}

	public void setStackTestService(StackTestService stackTestService) {
		this.stackTestService = stackTestService;
	}

	public CorrespondenceService getCorrespondenceService() {
		return correspondenceService;
	}

	public void setCorrespondenceService(CorrespondenceService correspondenceService) {
		this.correspondenceService = correspondenceService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public ReadWorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public void setWorkFlowService(ReadWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}
	
	public MonitoringService getMonitoringService() {
		return monitoringService;
	}

	public void setMonitoringService(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}

	/**
	 * 
	 */
	@Override
	public boolean purgeFacility(final String facilityId) throws DAOException {
		boolean success=false;
		try {
			
			deletePreservedSnapshotAndPermitRefs(facilityId);
			
			deleteSiteVisits(facilityId);

			deleteStackTests(facilityId);
			
			deleteInspections(facilityId);
			
			deleteEnforcementActions(facilityId);
			
//			deleteCorrespondence(facilityId);
			
			deleteMonitorGroup(facilityId);
			
			deleteFacilityEventLogs(facilityId);
						
			deleteFacilityPermits(facilityId);
			
			deleteFacilityApplications(facilityId);
			
			deleteEmissionInventories(facilityId);
			
			deleteComplianceReports(facilityId);
			
			deleteFacilityAttachments(facilityId);

			deleteFacility(facilityId);
			
			deleteWorkflows(facilityId);
			
			success=true;
			
		} catch (RemoteException re) {
			success=false;
			throw new DAOException(re.getMessage(), re);
			
		}
		return success;		

	}

	/**
	 * 
	 */
	private void deleteFacilityPermits(final String facilityId) throws DAOException, RemoteException {
		boolean unlimitedResults = true;

		// first delete all the permit conditions for the given facility
		PermitConditionSearchCriteria pcs = new PermitConditionSearchCriteria();
		pcs.setFacilityId(facilityId);
		pcs.setShowOnlyIssuedFinal(false);
		
		PermitConditionSearchLineItem[] permitConditionSearchResults = getPermitConditionService()
				.searchPermitConditions(pcs);

		// remove the supersession relationships
		for (PermitConditionSearchLineItem permitCondition : permitConditionSearchResults) {
			
			permitConditionDAO().deletePermitConditionSupersession(permitCondition.getPermitConditionId());
		}
		
		// remove the permit condition itself
		for (PermitConditionSearchLineItem permitCondition : permitConditionSearchResults) {
			
			PermitCondition pc = this.permitConditionService.retrievePermitConditionById(permitCondition.getPermitConditionId());
			
			if (null != pc) {
				
				this.permitConditionService.removePermitCondition(pc);
			}
		}
		
		// next delete all the permits
		List<Permit> permits = getPermitService().search(null, null, null, null, null, null, null, null, facilityId,
				null, null, null, null, null, null, unlimitedResults, null);

		if (null != permits) {

			for (Permit permit : permits) {

				getPermitService().deletePermit(permit.getPermitID());
			}
		}
	}


	

	/**
	 * 
	 */
	private void deleteFacilityApplications(final String facilityId) throws DAOException, RemoteException {
		boolean legacyStatePTOFlag = false;// ??
		// TODO:Check if there is sequencing needed between facility version,
		// fp_id, application number, facilityId
		ApplicationSearchResult[] results = getApplicationService().retrieveApplicationSearchResults(null, null,
				facilityId, null, null, null, null, null, legacyStatePTOFlag, null, null, null, true);

		if (null != results) {

			for (ApplicationSearchResult result : results) {

				deleteApplicationsByFacilityVersionId(result.getApplicationNumber());

			}

		}

	}




	/**
	 * 
	 */
	private void deleteApplicationsByFacilityVersionId(String applicationNumber) throws DAOException, RemoteException {

		Application tempApplication = getApplicationService().retrieveApplication(applicationNumber);
		
		boolean deleteAttachmentFiles = true;

		if (null != tempApplication) {

			getApplicationService().removeApplicationDocuments(tempApplication, null, deleteAttachmentFiles);

			getApplicationService().removeApplication(tempApplication);
			// applicationBO.deleteApplicationDir(application);

		}

	}

	/**
	 * 
	 */
	private void deleteEmissionInventories(final String facilityId) throws DAOException, RemoteException {
	FacilityVersion[] facilityVersionlist = getFacilityService().retrieveAllFacilityVersions(facilityId);
	
	getEmissionsReportService().deleteReferences(facilityId);

	if (null != facilityVersionlist) {

		for (FacilityVersion fv : facilityVersionlist) {

			EmissionsReport[] emissionsReportList = getEmissionsReportService()
					.retrieveEmissionsReports(fv.getFpId());
			
	
			for (EmissionsReport emissionsReport : emissionsReportList) {

				// // I shouldn't be doing this. There should be a method
				// that retrieves all notes as well.
				EmissionsReportNote[] notes = getEmissionsReportService()
						.retrieveReportNotes(emissionsReport.getEmissionsRptId());
				ArrayList<EmissionsReportNote> noteList = new ArrayList<EmissionsReportNote>(notes.length);
				for (EmissionsReportNote note : notes) {
					noteList.add(note);

				}

				emissionsReport.setNotes(noteList);

				boolean removeFiles = true;
				// Need to delete invoice before rpt
				//Check if this has parent reports
					getEmissionsReportService().deleteAssociatedInvoice(emissionsReport);
				// Delete Referencing rows first
//				if(emissionsReport.getReportModified()!=null){
//
//					getEmissionsReportService().deleteReferences(emissionsReport);
//
//				}
	
				getEmissionsReportService().deleteEmissionsReport(emissionsReport, null, removeFiles);
			}

		}

	}

}

//	/**
//	 * 
//	 */
//	private void deleteEmissionInventories(final String facilityId) throws DAOException, RemoteException {
//		
//		ArrayList <Integer> sortedResult=getSortedFpIdsByFacilityVersion(facilityId);
//
//		if(null != sortedResult){
//
//			for (Integer fpId:sortedResult){
//
//				EmissionsReport[] emissionsReportList =getEmissionsReportService().retrieveEmissionsReports(fpId);
//
//				for (EmissionsReport emissionsReport:emissionsReportList){
//					
////							// I shouldn't be doing this. There should be a method that retrieves all notes as well.
//							EmissionsReportNote[] notes = getEmissionsReportService().retrieveReportNotes(emissionsReport.getEmissionsRptId());
//							ArrayList<EmissionsReportNote> noteList=new ArrayList<EmissionsReportNote>(notes.length);
//							for (EmissionsReportNote note: notes){
//								noteList.add(note);
//								
//							}
//							
//							emissionsReport.setNotes(noteList);
//
//					boolean removeFiles=true;
//
//					getEmissionsReportService().deleteEmissionsReport(emissionsReport, null, removeFiles);
//				}
//
//			}
//
//
//		}
//
//	}
	
	/**
	 * 
	 */
//	private ArrayList<Integer> getSortedFpIdsByFacilityVersion(final String facilityId) throws DAOException, RemoteException {
//
//		FacilityVersion[] facilityVersionlist = getFacilityService().retrieveAllFacilityVersions(facilityId);
//
//		ArrayList<Integer> sortedResult = new ArrayList<Integer>(facilityVersionlist.length);
//
//		if (null != facilityVersionlist) {
//
//			if (facilityVersionlist != null && facilityVersionlist.length > 0) {
//
//				for (FacilityVersion FacilityVersion : facilityVersionlist) {
//					sortedResult.add(FacilityVersion.getFpId());
//
//				}
//				Collections.sort(sortedResult);
//			}
//
//		}
//		return sortedResult;
//
//	}



	/**
	 * 
	 */
	private void deleteEnforcementActions(final String facilityId) throws DAOException, RemoteException {
		EnforcementAction[] enforcementActions = getEnforcementActionService().retrieveEnforcementActionByFacilityId(facilityId); 
		
		if (null != enforcementActions) {

			for (EnforcementAction enforcementAction : enforcementActions) {

				getEnforcementActionService().retrieveEnforcementAction(enforcementAction.getEnforcementActionId());
				getEnforcementActionService().deleteEnforcementAction(enforcementAction);

			}

		}
	}



	/**
	 * 
	 */
	//readonly-schema should be true
	private void deleteStackTests(final String facilityId) throws DAOException, RemoteException {
		StackTest[] stackTests = getStackTestService().searchStackTests(facilityId);

		Facility facility = getFacilityService().retrieveFacility(facilityId);

		boolean deleteAttachmentFiles = true;

		boolean readOnly = true; //false means staging schema

		if (null != stackTests) {

			for (StackTest stackTest : stackTests) {

				StackTest stackTestComplete = getStackTestService().retrieveStackTest(stackTest.getId(), readOnly);

				getStackTestService().deleteStackTest(facility, stackTestComplete, readOnly, deleteAttachmentFiles);
			}

		}
	}

	/**
	 * 
	 */

//	private void deleteInspections(final String facilityId) throws DAOException, RemoteException {
//
//		FullComplianceEval[] fullComplianceEvalList = getFullComplianceEvalService().retrieveFceBySearch(facilityId);
//
//		ArrayList<Integer> fedIdList = new ArrayList<Integer>(fullComplianceEvalList.length);
//
//		if (null != fullComplianceEvalList) {
//
//			for (FullComplianceEval fullComplianceEval : fullComplianceEvalList) {
//
//				int fceIdTemp = fullComplianceEval.getId();
//
//				fedIdList.add(fceIdTemp);
//			}
//
//			Collections.sort(fedIdList);
//
//			for (int fceId : fedIdList) {
//
//				FullComplianceEval fce = getFullComplianceEvalService().retrieveFce(facilityId, fceId);
//
//				getFullComplianceEvalService().deleteFce(fce);
//
//			}
//
//		}
//
//	}



	/**
	 * 
	 */
//	private void deleteComplianceReports(final String facilityId) throws DAOException, RemoteException {
//		
//		ComplianceReportList[] complianceReportList = getComplianceReportService()
//				.searchComplianceReportByFacility(facilityId);
//
//		if (null != complianceReportList) {
//
//			for (ComplianceReportList complianceReport : complianceReportList) {
//
//				boolean removeFiles = true;
//
//				ComplianceReport rpt = getComplianceReportService().retrieveComplianceReport(complianceReport.getReportId());
//				
//				getComplianceReportService().deleteComplianceReport(rpt, null, removeFiles);
//
//			}
//
//		}
//
//	}




	/**
	 * 
	 */
//	private void deleteSiteVisits(final String facilityId) throws DAOException, RemoteException {
//
//		FullComplianceEval[] complianceList = getFullComplianceEvalService().retrieveFceBySearch(facilityId);
//
//		if (null != complianceList) {
//
//			for (FullComplianceEval fullComplianceEval : complianceList) {
//
//				SiteVisit[] siteVisits = fullComplianceEval.getAssocSiteVisits();
//
//				for (SiteVisit siteVisit : siteVisits) {
//
//					SiteVisit siteVisitDtl = getFullComplianceEvalService().retrieveSiteVisit(siteVisit.getId());
//
//					getFullComplianceEvalService().removeSiteVisit(siteVisitDtl, null);
//				}
//			}
//
//		}
//	}




	/**
	 * 
	 */
	private void deleteFacilityEventLogs(final String facilityId) throws DAOException, RemoteException {
		
		FacilityVersion[] facilityVersionList = getFacilityService().retrieveAllFacilityVersions(facilityId);

		ArrayList<Integer> sortedFpIdist = new ArrayList<Integer>(facilityVersionList.length);
		
		if( null != facilityVersionList){
			
			for (FacilityVersion facilityVersion : facilityVersionList) {
			
				sortedFpIdist.add(facilityVersion.getFpId()); // Or versionId??
			}
		
			Collections.sort(sortedFpIdist);
			
			for (Integer fpId : sortedFpIdist) {

				getFacilityService().deleteFacilityEventLogs(fpId);
			}

			
		}
	}



	/**
	 * 
	 */
	private void deleteFacilityInventory(final String facilityId) throws DAOException, RemoteException {

		FacilityVersion[] facilityVersionList = getFacilityService().retrieveAllFacilityVersions(facilityId);

		ArrayList<Integer> sortedFpIdist = new ArrayList<Integer>(facilityVersionList.length);

		if (null != facilityVersionList) {

//			for (FacilityVersion facilityVersion : facilityVersionList) {
//
//				sortedFpIdist.add(facilityVersion.getFpId());
//			}
//
//			Collections.sort(sortedFpIdist);
//
//			for (Integer fpId : sortedFpIdist) {
//
//				getFacilityService().deleteFacilityInv(fpId);
			for (FacilityVersion facilityVersion : facilityVersionList) {
				
				getFacilityService().deleteFacilityInventory(facilityVersion.getFpId()); 
				
			}

//			}
//
		}

	}



	/**
	 * 
	 */
	private void deleteFacilityAttachments(final String facilityId) throws DAOException, RemoteException {

		Attachment[] attachments = getFacilityService().retrieveFacilityAttachments(facilityId);

		if (null != attachments) {

			for (Attachment attachment : attachments) {

				getFacilityService().deleteFacilityAttachments(attachment);

			}

		}

	}



	/**
	 * 
	 */
	private void deleteFacility(final String facilityId) throws DAOException, RemoteException {
		getFacilityService().deleteFacility(facilityId);
		deleteFacilityInventory(facilityId);
//
//			Collections.sort(sortedFpIdist);
//
//			for (Integer fpId : sortedFpIdist) {
//
////				getFacilityService().deleteFacility(fpId);
//			}
//		}
	}



	/**
	 * 
	 */
	public void deleteWorkflows(final String facilityId) throws DAOException, RemoteException{
		// TODO Auto-generated method stub
		
	}




	/**
	 * 
	 */
	public void deleteCorrespondence(final String facilityId) throws DAOException, RemoteException {

		Correspondence[] correspondenceList = getCorrespondenceService().searchCorrespondenceByFacility(facilityId);

		if (null != correspondenceList) {

			for (Correspondence correspondence : correspondenceList) {
//				correspondence does not have notes
				getCorrespondenceService().deleteCorrespondence(correspondence);  //1. //  delete existing document  2. //  delete existing document attachments
			}
		}
	}


	public boolean deleteWorkflows(FacilityPurgeSearchLineItem facility) throws DAOException, RemoteException{
		WorkFlowManager wfm = new WorkFlowManager();
    	WorkFlowProcess wp = new WorkFlowProcess();
    	wp.setFacilityIdString(facility.getFacilityId());
		WorkFlowProcess[] processes = getWorkFlowService().retrieveProcessList(wp);  
		System.out.println("process lis size=" + processes.length + ". processes.ids are ");
		for (WorkFlowProcess p : processes){
			System.out.print(p.getProcessId() + ", ");
			WorkFlowResponse removeProcessFlowsResp = wfm.removeProcessFlows(p.getProcessId(), p.getUserId());
			if (removeProcessFlowsResp == null || removeProcessFlowsResp.hasError() || removeProcessFlowsResp.hasFailed()){
				return false;
			}
		}
    	return true;
	}

	@Override
	public boolean purgeFacility(FacilityPurgeSearchLineItem facility) throws DAOException{
		try{
//			if (!deleteWorkflows(facility)){
//				return false;
//			}
			
//			deleteXrefs(facility);
//			Application, Permit, Permit Condition, Stack Test, Compliance Report, Correspondence, Emission Inventories, 
			//CEM/COM Limits/Monitor, Ambient Monitors, Site Visit
		
			
			//deleteApplication
			//deletPermit --- compliance status events
			//deleteEmissionsInventories
			
			//deleteSiteVisit(checked)
			deleteSiteVisits(facility.getFacilityId());
			//deleteStackTest(checked) -- pre-request: delete compliance Status Events, fce_pre_stack_Test_xref 
			deleteStackTests(facility.getFacilityId());
			//deleteInspections(checked)
			deleteInspections(facility.getFacilityId());
			//enforcementAction
			deleteEnforcementActions(facility.getFacilityId());
			//deleteCorrespondence
			this.deleteCorrespondence(facility.getFacilityId());
			//deleteMonitorGroup
			this.deleteMonitorGroup(facility.getFacilityId());
			//Event log
			this.deleteFacilityEventLogs(facility.getFacilityId());
			
			deleteFacilityEventLogs(facility.getFacilityId());			
			
			deleteFacilityPermits(facility.getFacilityId());
			
			deleteFacilityApplications(facility.getFacilityId());
			
			deleteEmissionInventories(facility.getFacilityId());
			
			deleteFacilityInventory(facility.getFacilityId());
			
			deleteFacility(facility.getFacilityId());
			
			
			
			//Field Audit Log			
			//FacilityAttachment

		} catch (DAOException e){
			logger.error(e.getMessage(), e);
			return false;
		} catch(RemoteException e){
			logger.error(e.getMessage(), e);
			return false;
		} 
		return true;
	}
	//xrefs that referred to main object datas.
//	public void deleteXrefs(FacilityPurgeSearchLineItem facility) throws DAOException, RemoteException{
//		//for each fceId refered to a facility:
//		
//		fullComplianceEvalList = getFullComplianceEvalService().retrieveFceBySearch(facility.getFacilityId());
//		for (FullComplianceEval i : fullComplianceEvalList) {
//			getFullComplianceEvalService().deletePreservedSnapshotRefs(i.getId());
//			getFullComplianceEvalService().deleteFcePermitRefs(i.getId());
//		}
//		
//		
//	}
//		
//	public void deleteInspections(final String facilityId) throws DAOException, RemoteException {
//		getFullComplianceEvalService().clearLastInspIdByFacility(facilityId);
//		for (FullComplianceEval i : fullComplianceEvalList) {
//			FullComplianceEval fce = getFullComplianceEvalService().retrieveFce(facilityId, i.getId());
//			getFullComplianceEvalService().removeFce(fce);
//		}
//	}
//
//
//
//
//	public void deleteComplianceReports(final String facilityId) throws DAOException, RemoteException {
//		ComplianceReportList[] complianceReportList = getComplianceReportService().searchComplianceReportByFacility(facilityId);
//		if (null != complianceReportList) {
//			for (ComplianceReportList complianceReport : complianceReportList) {
//				ComplianceReport rpt = getComplianceReportService().retrieveComplianceReport(complianceReport.getReportId());
//				getComplianceReportService().deleteComplianceReport(rpt);
//			}
//		}
//	}
//	
//	//delete monitors, delete monitor sites, delete monitor group
//	public void deleteMonitorGroup(String facilityId) throws DAOException{
//		MonitorSite searchObjSite = new MonitorSite();
//		searchObjSite.setFacilityId(facilityId);
//		MonitorSite[] monitorSites = getMonitoringService().searchMonitorSites(searchObjSite, false);
//		for (MonitorSite ms : monitorSites){
//			ms = getMonitoringService().retrieveMonitorSite(ms.getSiteId());
//			Monitor searchObjMonitor = new Monitor();
//			searchObjMonitor.setSiteId(ms.getSiteId());
//			Monitor[] monitors = getMonitoringService().searchMonitors(searchObjMonitor);
//			for (Monitor m: monitors){
//				m = getMonitoringService().retrieveMonitor(m.getMonitorId());
//				getMonitoringService().deleteMonitor(m);
//			}
//			getMonitoringService().deleteMonitorSite(ms);
//		}
//		//delete Monitor report and monitor Group
//		MonitorGroup searchObjGroup = new MonitorGroup();
//		searchObjGroup.setFacilityId(facilityId);
//		MonitorGroup[] monitorGroups = getMonitoringService().searchMonitorGroups(searchObjGroup, false);
//		for (MonitorGroup monitorGroup : monitorGroups){
//			monitorGroup = getMonitoringService().retrieveMonitorGroup(monitorGroup.getGroupId());
//			MonitorReport[] monitorReports = getMonitoringService().retrieveMonitorReports(monitorGroup.getGroupId(),false);
//			for (MonitorReport monitorReport : monitorReports){
//				getMonitoringService().deleteMonitorReport(monitorReport, true);
//			}
//			getMonitoringService().deleteMonitorGroup(monitorGroup);
//		}
//		
//	}
//
//	private void deleteSiteVisits(final String facilityId) throws DAOException, RemoteException {
//		SiteVisit[] siteVisitList = getFullComplianceEvalService().retrieveVisitBySearch(facilityId);
//		if (null != siteVisitList) {
//			for (SiteVisit sv : siteVisitList) {
//				SiteVisit siteVisit = getFullComplianceEvalService().retrieveSiteVisit(sv.getId());
//				getFullComplianceEvalService().removeSiteVisit(siteVisit);
//			}
//		}
//	}


//	public boolean deleteWorkflows(FacilityPurgeSearchLineItem facility) throws DAOException, RemoteException{
//		WorkFlowManager wfm = new WorkFlowManager();
//    	WorkFlowProcess wp = new WorkFlowProcess();
//    	wp.setFacilityIdString(facility.getFacilityId());
//		WorkFlowProcess[] processes = getWorkFlowService().retrieveProcessList(wp);  
//		System.out.println("process lis size=" + processes.length + ". processes.ids are ");
//		for (WorkFlowProcess p : processes){
//			System.out.print(p.getProcessId() + ", ");
//			WorkFlowResponse removeProcessFlowsResp = wfm.removeProcessFlows(p.getProcessId(), p.getUserId());
//			if (removeProcessFlowsResp == null || removeProcessFlowsResp.hasError() || removeProcessFlowsResp.hasFailed()){
//				return false;
//			}
//		}
//    	return true;
//	}

//	@Override
//	public boolean purgeFacility(FacilityPurgeSearchLineItem facility) throws DAOException{
//		try{
//			if (!deleteWorkflows(facility)){
//				return false;
//			}
//			
////			deleteXrefs(facility);
////			Application, Permit, Permit Condition, Stack Test, Compliance Report, Correspondence, Emission Inventories, 
//			//CEM/COM Limits/Monitor, Ambient Monitors, Site Visit
//			fullComplianceEvalList = getFullComplianceEvalService().retrieveFceBySearch(facility.getFacilityId());
//			for (FullComplianceEval i : fullComplianceEvalList) {
//				getFullComplianceEvalService().deletePreservedSnapshotRefs(i.getId());
//				getFullComplianceEvalService().deleteFcePermitRefs(i.getId());
//			}
//			deleteComplianceReports(facility.getFacilityId());
//			
//			//deleteApplication
//			//deletPermit --- compliance status events
//			//deleteEmissionsInventories
//			
//			//deleteSiteVisit(checked)
//			deleteSiteVisits(facility.getFacilityId());
//			//deleteStackTest(checked) -- pre-request: delete compliance Status Events, fce_pre_stack_Test_xref 
//			deleteStackTests(facility.getFacilityId());
//			//deleteInspections(checked)
//			deleteInspections(facility.getFacilityId());
//			//enforcementAction
//			deleteEnforcementActions(facility.getFacilityId());
//			//deleteCorrespondence
//			this.deleteCorrespondence(facility.getFacilityId());
//			//deleteMonitorGroup
//			this.deleteMonitorGroup(facility.getFacilityId());
//			//Event log
//			this.deleteFacilityEventLogs(facility.getFacilityId());
//			//FacilityAttachment
//			deleteFacilityAttachments(facility.getFacilityId());
//			//Facility
//			deleteFacility(facility.getFacilityId());	
//			//Field Audit Log - included in deleteFacility	
////			getFacilityService().deleteFacilityFieldAuditLog(facility.getFacilityId());
//		} catch (DAOException e){
//			logger.error(e.getMessage(), e);
//			return false;
//		} catch(RemoteException e){
//			logger.error(e.getMessage(), e);
//			return false;
//		} 
//		FacilityPurgeLog facilityPurgeLog = new FacilityPurgeLog();
//		facilityPurgeLog.setFacilityId(facility.getFacilityId());
//		facilityPurgeLog.setFacilityName(facility.getFacilityName());
//		facilityPurgeLog.setCompanyId(facility.getCmpId());
//		facilityPurgeLog.setCompanyName(facility.getCompanyName());
//		facilityPurgeLog.setShutdownDate(facility.getShutdownDate());
//		facilityPurgeLog.setPurgedDate(new Timestamp(System.currentTimeMillis()));
//		facilityPurgeLog.setUserId(InfrastructureDefs.getCurrentUserId());
//		getFacilityService().createFacilityPurgeLog(facilityPurgeLog);
//		return true;
//	}

	//xrefs that referred to main object datas.
	/**
	 * 
	 * @param facilityId
	 * @throws DAOException
	 * @throws RemoteException
	 */
	public void deletePreservedSnapshotAndPermitRefs(String facilityId) throws DAOException, RemoteException{
		
		fullComplianceEvalList = getFullComplianceEvalService().retrieveFceBySearch(facilityId);
		
		for (FullComplianceEval i : fullComplianceEvalList) {
			
			getFullComplianceEvalService().deletePreservedSnapshotRefs(i.getId());
			
			getFullComplianceEvalService().deleteFcePermitRefs(i.getId());
		}
		
		
	}

	/**
	 * 
	 * @param facilityId
	 * @throws DAOException
	 * @throws RemoteException
	 */
	public void deleteInspections(final String facilityId) throws DAOException, RemoteException {

		getFullComplianceEvalService().clearLastInspIdByFacility(facilityId);

		for (FullComplianceEval i : fullComplianceEvalList) {

			FullComplianceEval fce = getFullComplianceEvalService().retrieveFce(facilityId, i.getId());

			getFullComplianceEvalService().removeFce(fce);
		}
	}

	

/**
 * 
 * @param facilityId
 * @throws DAOException
 * @throws RemoteException
 */
	public void deleteComplianceReports(final String facilityId) throws DAOException, RemoteException {

		ComplianceReportList[] complianceReportList = getComplianceReportService()
				.searchComplianceReportByFacility(facilityId);

		if (null != complianceReportList) {

			for (ComplianceReportList complianceReport : complianceReportList) {

				ComplianceReport rpt = getComplianceReportService()
						.retrieveComplianceReport(complianceReport.getReportId());

				getComplianceReportService().deleteComplianceReport(rpt);
			}
		}
	}

	
	//delete monitors, delete monitor sites, delete monitor group
	/**
	 * 
	 * @param facilityId
	 * @throws DAOException
	 */
	public void deleteMonitorGroup(String facilityId) throws DAOException {
		MonitorSite searchObjSite = new MonitorSite();

		searchObjSite.setFacilityId(facilityId);

		MonitorSite[] monitorSites = getMonitoringService().searchMonitorSites(searchObjSite, false);

		for (MonitorSite ms : monitorSites) {

			ms = getMonitoringService().retrieveMonitorSite(ms.getSiteId());

			Monitor searchObjMonitor = new Monitor();

			searchObjMonitor.setSiteId(ms.getSiteId());

			Monitor[] monitors = getMonitoringService().searchMonitors(searchObjMonitor);

			for (Monitor m : monitors) {

				m = getMonitoringService().retrieveMonitor(m.getMonitorId());

				getMonitoringService().deleteMonitor(m);
			}

			getMonitoringService().deleteMonitorSite(ms);
		}
		// delete Monitor report and monitor Group
		MonitorGroup searchObjGroup = new MonitorGroup();

		searchObjGroup.setFacilityId(facilityId);

		MonitorGroup[] monitorGroups = getMonitoringService().searchMonitorGroups(searchObjGroup, false);

		for (MonitorGroup monitorGroup : monitorGroups) {

			monitorGroup = getMonitoringService().retrieveMonitorGroup(monitorGroup.getGroupId());

			MonitorReport[] monitorReports = getMonitoringService().retrieveMonitorReports(monitorGroup.getGroupId(),
					false);

			for (MonitorReport monitorReport : monitorReports) {

				getMonitoringService().deleteMonitorReport(monitorReport, true);
			}

			getMonitoringService().deleteMonitorGroup(monitorGroup);
		}

	}

/**
 * 
 * @param facilityId
 * @throws DAOException
 * @throws RemoteException
 */

	private void deleteSiteVisits(final String facilityId) throws DAOException, RemoteException {
		
		SiteVisit[] siteVisitList = getFullComplianceEvalService().retrieveVisitBySearch(facilityId);
		
		if (null != siteVisitList) {
		
			for (SiteVisit sv : siteVisitList) {
			
				SiteVisit siteVisit = getFullComplianceEvalService().retrieveSiteVisit(sv.getId());
			
				getFullComplianceEvalService().removeSiteVisit(siteVisit);
			}
		}
	}

}
