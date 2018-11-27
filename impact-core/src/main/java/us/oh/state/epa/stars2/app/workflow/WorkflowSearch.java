package us.oh.state.epa.stars2.app.workflow;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.bo.ComplianceReportService;
import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.EnforcementActionService;
import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.bo.MonitoringService;
import us.oh.state.epa.stars2.bo.StackTestService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroup;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorReport;
import us.wy.state.deq.impact.database.dbObjects.workflow.ImpactWorkFlowProcess;

/**
 * @author Pyeh
 * 
 */
@SuppressWarnings("serial")
public class WorkflowSearch extends AppBase {
    private String facilityId;
    private String facilityNm;
    private String permitNumber;
    private Integer userId;
    private String ProcessTypeCd;
    private String processTemplateNm;
    private Timestamp startDateFrom;
    private Timestamp startDateTo;
    private String rush;
    private String section;
    private String by;
    private WorkFlowProcess wp = new WorkFlowProcess();
    private boolean hasSearchResults;
    private ImpactWorkFlowProcess[] processes;
    private WorkFlowProcess process;
    private Integer externalId;
    
    private LinkedHashMap<String, String> workflows;
    private ReadWorkFlowService workFlowService;
    private List<String> workFlowStates = new ArrayList<String>();
    private ComplianceReportService complianceReportService;
    private StackTestService stackTestService;
    private EmissionsReportService emissionsReportService;
    private MonitoringService monitoringService;
    private EnforcementActionService enforcementActionService;
    private FullComplianceEvalService fullComplianceEvalService;

    
    public Integer getExternalId() {
		return externalId;
	}

	public void setExternalId(Integer externalId) {
		this.externalId = externalId;
	}

	public WorkFlowProcess getProcess() {
		return process;
	}

	public void setProcess(WorkFlowProcess process) {
		this.process = process;
	}

	public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}

	public void setEmissionsReportService(
			EmissionsReportService emissionsReportService) {
		this.emissionsReportService = emissionsReportService;
	}


    
    public StackTestService getStackTestService() {
		return stackTestService;
	}

	public void setStackTestService(StackTestService stackTestService) {
		this.stackTestService = stackTestService;
	}

	public ComplianceReportService getComplianceReportService() {
		return complianceReportService;
	}

	public void setComplianceReportService(
			ComplianceReportService complianceReportService) {
		this.complianceReportService = complianceReportService;
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
	
	public EnforcementActionService getEnforcementActionService() {
		return enforcementActionService;
	}

	public void setEnforcementActionService(EnforcementActionService enforcementActionService) {
		this.enforcementActionService = enforcementActionService;
	}
	
	public FullComplianceEvalService getFullComplianceEvalService() {
		return fullComplianceEvalService;
	}

	public void setFullComplianceEvalService(FullComplianceEvalService fullComplianceEvalService) {
		this.fullComplianceEvalService = fullComplianceEvalService;
	}

	public WorkflowSearch() {
        super();
        cacheViewIDs.add("/workflows/workflows.jsp");
        workFlowStates.add(WorkFlowProcess.STATE_IN_PROCESS_CD);
    }
    
    /**
     * This method is use together with section in some JSP page. The useBean
     * will setup the value by and section from URL.
     * 
     * @return
     */
    public final String getBy() {
        return by;
    }

    public final void setBy(String by) {
        this.by = by;
    }

    public final String getSection() {
        return section;
    }

    public final void setSection(String section) {
        this.section = section;
        wp = new WorkFlowProcess();
        reset();
        // if the by is a status, the section value is a status value.
        if (by.equalsIgnoreCase("status")) {
            wp.setStatus(section);
        } else {
            wp.setProcessTemplateId(new Integer(section));
        }

        // This is a special search function base on the by and section.
        try {
            wp.setState(WorkFlowProcess.STATE_IN_PROCESS_CD);
            wp.setCurrent(true);
            wp.setUnlimitedResults(unlimitedResults());
            processes = convertToImpact(getWorkFlowService().retrieveProcessList(wp));
            DisplayUtil.displayHitLimit(processes.length);
            hasSearchResults = true;
        } catch (RemoteException re) {
            handleException(re);
        }
    }

    public final WorkFlowProcess[] getProcesses() {
        return processes;
    }

    public final void setProcesses(WorkFlowProcess[] processes) {
		this.processes = convertToImpact(processes);
    }

    public final WorkFlowProcess getWp() {
        return wp;
    }

    public final void setWp(WorkFlowProcess wp) {
        this.wp = wp;
    }

    public final String getFacilityId() {
        return facilityId;
    }

    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public final String getFacilityNm() {
        return facilityNm;
    }

    public final void setFacilityNm(String facilityNm) {
        this.facilityNm = facilityNm;
    }

    public final boolean isHasSearchResults() {
        return hasSearchResults;
    }

    public final void setHasSearchResults(boolean hasSearchResults) {
        this.hasSearchResults = hasSearchResults;
    }

    public final String getProcessTypeCd() {
        return ProcessTypeCd;
    }

    public final void setProcessTypeCd(String processTypeCd) {
        ProcessTypeCd = processTypeCd;
        if (processTypeCd == null)
            workflows = null;
        else{
            try {
                workflows = getWorkFlowService().retrieveProcessTemplatesByType(processTypeCd);
            } catch (RemoteException e) {
                logger.error(e.getMessage(), e);
                DisplayUtil.displayError("System error. Please contact system administrator");
            }
        }
    }

    public final LinkedHashMap<String, String> getWorkflows() {
        if (workflows == null){
            LinkedHashMap<String, Integer> tws = ((WorkFlowDefs)FacesUtil.getManagedBean("workFlowDefs")).getWorkflows();
            workflows = new LinkedHashMap<String, String>();
            for (String t : tws.keySet())
                workflows.put(t, t);
        }
        return workflows;
    }

    public final String getRush() {
        return rush;
    }

    public final void setRush(String rush) {
        this.rush = rush;
    }

    public final Timestamp getStartDateFrom() {
        return startDateFrom;
    }

    public final void setStartDateFrom(Timestamp startDateFrom) {
        this.startDateFrom = startDateFrom;
    }

    public final Timestamp getStartDateTo() {
        return startDateTo;
    }

    public final void setStartDateTo(Timestamp startDateTo) {
        this.startDateTo = startDateTo;
    }

    public final Integer getUserId() {
        return userId;
    }

    public final void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String navigate() {
    	String ret = null;
    	if (null != getProcess()) {
	    	// set up external bean
	    	TaskBase externalBean = TaskBase.setUp(getProcess(), null, 
	    			getProcess().getExternalId());
	    	
	    	// return outcome, trigger nav-rule
	    	if (null != externalBean) {
	    		ret = externalBean.findOutcome(null,null); //TODO does this work with all bean types?
	    	}
    	}
    	return ret;
    }
    
    public final String submit() {
        try {
            wp.setFacilityIdString(facilityId);
            wp.setFacilityNm(facilityNm);
            wp.setPermitNumber(permitNumber);
            wp.setUserId(userId);
            wp.setProcessCd(ProcessTypeCd);
            wp.setProcessTemplateNm(processTemplateNm);
            wp.setExpedite(rush);
            wp.setStartDt(startDateFrom);
            wp.setEndDt(startDateTo);
            wp.setCurrent(true);
            //wp.setState(WorkFlowProcess.STATE_IN_PROCESS_CD);
            wp.setState(null);
            wp.setUnlimitedResults(unlimitedResults());

            processes = convertToImpact(getWorkFlowService().retrieveProcessList(wp));
            // filter the workflows based on what is selected
            ArrayList<ImpactWorkFlowProcess> processesList = new ArrayList<ImpactWorkFlowProcess>();
            for(ImpactWorkFlowProcess proc : processes) {
            	String processState = proc.getState();
            	// this is hack to determine if the permit workflow is completed.
            	// long term solution would be to fix the retrieveProcessListBySearch query
            	// so that it retrieves the correct activity/process id representing the
            	// last step of the permit workflow
            	if(processState.equalsIgnoreCase("SK") && proc.getEndDt() != null)
            		processState=WorkFlowProcess.STATE_COMPLETED_CD;
            	// update process state
            	proc.setState(processState);
            	if(workFlowStates!=null){
            		if(workFlowStates.contains(processState))
            				processesList.add(proc);
            		}
            	}
            
            processes = processesList.toArray(new ImpactWorkFlowProcess[0]);

            if (processes.length > 500) {
        		DisplayUtil.displayInfo("For performance reasons, Report IDs are not retrieved when there are more than 500 Workflow search results.");
        	}
        	for (int i = 0; i < processes.length && processes.length <= 500; i++) {
           		setReportId(processes[i]);
        	}

            DisplayUtil.displayHitLimit(processes.length);

            if (processes.length == 0) {
                DisplayUtil.displayNoRecords();
            }

            hasSearchResults = true;
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil.displayError("System error. Please contact system administrator");
        }

        return SUCCESS;
    }

    private ImpactWorkFlowProcess[] convertToImpact(
			WorkFlowProcess[] procs) {
    	ImpactWorkFlowProcess[] impactProcs = 
    			new ImpactWorkFlowProcess[procs.length];
    	for (int i = 0; i < procs.length; i++) {
    		impactProcs[i] = new ImpactWorkFlowProcess(procs[i]);
    	}
    	return impactProcs;
	}

	private void setReportId(ImpactWorkFlowProcess p) 
		throws DAOException, RemoteException {
		if (p.getExternalId() != null) {
			if (p.getProcessTemplateNm().equals(WorkFlowProcess.CR_CEMS_COMS_RATA) ||
					p.getProcessTemplateNm().equals(WorkFlowProcess.CR_ONE_TIME_REPORTS) ||
					p.getProcessTemplateNm().equals(WorkFlowProcess.CR_OTHER_COMP_REPORTS) ||
					p.getProcessTemplateNm().equals(WorkFlowProcess.CR_GENERIC_COMPLIANCE_REPORT)
					) {
				ComplianceReport report =
						getComplianceReportService().retrieveComplianceReportOnly(
								p.getExternalId(), true);
				if (null != report) {
					p.setReportId(report.getReportCRPTId());
				}
			} 
			else
			if (p.getProcessTemplateNm().equals(WorkFlowProcess.STACK_TESTS)) {
				// TFS Task 5347
				// The getStckId method in the StackTest class is merely prefixing the id with "STCK"
				// and returning the prefixed value instead of retrieving the value from the database. 
				// So, instead of the retrieving the stack test record from the database in order to get the
				// the (UI)stack test id i.e., stckId, we will instead just create a dummy StackTest object with 
				// id = externalId of the workflow process and then call the getStckId method on it to get the
				// (UI) stack test id i.e., stckId
//				StackTest stackTest =
//						getStackTestService().retrieveStackTest(
//								p.getExternalId(),true);
//				if (null != stackTest) {
//					p.setReportId(stackTest.getStckId());
//				}
				StackTest stackTest = new StackTest();
				stackTest.setId(p.getExternalId());
				p.setReportId(stackTest.getStckId());
			} 
			else
			if (p.getProcessTemplateNm().equals(WorkFlowProcess.TV_EI_REVIEW)) {
				EmissionsReport ei = 
						getEmissionsReportService().retrieveEmissionsReportRow(
								p.getExternalId(), true);
				if (null != ei) {
					p.setReportId(ei.getEmissionsInventoryId());
				}
			} 
			else
			if(p.getProcessTemplateNm().equals(WorkFlowProcess.AMBIENT_MONITORING_REPORT)) {
				MonitorReport monitorReport = 
						getMonitoringService().retrieveMonitorReport(
								p.getExternalId());
				if (null != monitorReport) {
					p.setReportId(monitorReport.getMrptId());
					// since the facility information is not available in the search results
					// we will set it here by retrieving that information from the associated monitoring group
					MonitorGroup monitorGroup = getMonitoringService().retrieveMonitorGroup(monitorReport.getMonitorGroupId());
					if(null != monitorGroup) {
						p.setFacilityId(monitorGroup.getFpId());
						p.setFacilityNm(monitorGroup.getFacilityName());
						p.setFacilityIdString(monitorGroup.getFacilityId());
					}
				}
			} else
				if (p.getProcessTemplateNm().equals(WorkFlowProcess.ENFORCEMENT_ACTIONS)) {
					EnforcementAction enforcementAction =
							getEnforcementActionService().retrieveEnforcementAction(
									p.getExternalId());
					if (null != enforcementAction) {
						p.setReportId(enforcementAction.getEnfId());
					}
				} 
			else if (p.getProcessTemplateNm().equals(WorkFlowProcess.INSPECTION_REPORT_WORKFLOW_NAME)
					|| p.getProcessTemplateNm().equals(WorkFlowProcess.INSPECTION_DUE_SOON)) {
				
				FullComplianceEval fce = getFullComplianceEvalService().retrieveFceOnly(p.getExternalId());
				
				if (null != fce) {
					
					p.setReportId(fce.getInspId());
				}
			} 
			else {
				logger.error("Error determine external object type.");
				logger.error("---> External ID: " + p.getExternalId());
				logger.error("---> Process Template Name: " + p.getProcessTemplateNm());
			}
		}
	}

	public final String reset() {
        wp = new WorkFlowProcess();
        processes = null;
        facilityId = null;
        facilityNm = null;
        userId = null;
        ProcessTypeCd = null;
        startDateFrom = null;
        startDateTo = null;
        rush = null;
        processTemplateNm = null;
        workflows = null;
        hasSearchResults = false;
        permitNumber = null;
        if(workFlowStates!=null){
        	workFlowStates.clear();        
        	workFlowStates.add(WorkFlowProcess.STATE_IN_PROCESS_CD);
        }else{
        	workFlowStates = new ArrayList<String>();
        	workFlowStates.add(WorkFlowProcess.STATE_IN_PROCESS_CD);
        }
        
        return SUCCESS;
    }
    
    public void clearCache() {
        //processes = null;
        //hasSearchResults = false;
    }

    /**
     * @return the processTemplateNm
     */
    public final String getProcessTemplateNm() {
        return processTemplateNm;
    }

    /**
     * @param processTemplateNm the processTemplateNm to set
     */
    public final void setProcessTemplateNm(String processTemplateNm) {
        this.processTemplateNm = processTemplateNm;
    }

    /**
     * @return the permitNumber
     */
    public final String getPermitNumber() {
        return permitNumber;
    }

    /**
     * @param permitNumber the permitNumber to set
     */
    public final void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }

	public final List<String> getWorkFlowStates() {
		return workFlowStates;
	}

	public final void setWorkFlowStates(List<String> workFlowStates) {
		this.workFlowStates = workFlowStates;
	}

}
