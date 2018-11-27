package us.oh.state.epa.stars2.app.workflow;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

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
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivityLight;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.StopWatch;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroup;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorReport;

public class ToDoSearch extends AppBase {
	
	private static final long serialVersionUID = 4715153961370234877L;
	
	private String facilityId;
    private String facilityNm;
    private Integer userId;
    private String activityStatusCd;
    private boolean looped;
    private String activityNm;
    private String doLaaName;
    private String doLaaCd;
    private String ProcessTypeCd;
    private Integer externalId;
    private ProcessActivityLight[] shortActivities;
    private ProcessActivity pa = new ProcessActivity();
    private boolean hasSearchResults;
    private ProcessActivityLight[] activities;
    private boolean showFacility = true;
    private boolean fromExternal;
    
    private ReadWorkFlowService workFlowService;
    private ComplianceReportService complianceReportService;
    private StackTestService stackTestService;
    private EmissionsReportService emissionsReportService;
    private MonitoringService monitoringService;
    private EnforcementActionService enforcementActionService;
    private FullComplianceEvalService fullComplianceEvalService;

    
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

	public LinkedHashMap<String, String> getActivityNames() {
    	 LinkedHashMap<String, String> activityNames = new LinkedHashMap<String, String>();
    	try {
    		ActivityTemplate[] templates =
    				getWorkFlowService().retrieveActivityTemplates();
            String name = "ALL Info Workflow Tasks";
            activityNames.put(name, "Facility Changes/Miscellaneous");
            for (ActivityTemplate template : templates) {
                name = template.getActivityTemplateNm(); 
                if ("Facility Changes/Miscellaneous".equalsIgnoreCase(name))
                    continue;
                activityNames.put(name, name);
            }
		} catch (RemoteException e) {
            handleException(e);
		}
    	return activityNames;
    }

	
    private LinkedHashMap<String, String> workflows;
    private String processTemplateNm;
    
    public ToDoSearch() {
        super();
        init();
        // explicitly set the spring beans here, since they do not seem to get 
        // injected completely before the page renders
        setComplianceReportService(App.getApplicationContext().getBean(
        		ComplianceReportService.class));
        setStackTestService(App.getApplicationContext().getBean(
        		StackTestService.class));
        setEmissionsReportService(App.getApplicationContext().getBean(
        		EmissionsReportService.class));
        setMonitoringService(App.getApplicationContext().getBean(
        		MonitoringService.class));
        setEnforcementActionService(App.getApplicationContext().getBean(
        		EnforcementActionService.class));
        setFullComplianceEvalService(App.getApplicationContext().getBean(
        		FullComplianceEvalService.class));
        submit();
    }

    public final ProcessActivityLight[] getShortActivities() {
        return shortActivities;
    }

    public final void setShortActivities(ProcessActivityLight[] shortActivities) {
        this.shortActivities = shortActivities;
    }

    public final String getActivityStatusCd() {
        return activityStatusCd;
    }

    public final void setActivityStatusCd(String activityStatusCd) {
        if ("All".equalsIgnoreCase(activityStatusCd)) {
            //
            // Special case for workload mgmt report. Reseting for the
            // next retrieval. "All" is set in workloadReport.jsp.
            //
            reset();
            fromExternal = true;
            this.activityStatusCd = null;
        } else {
            this.activityStatusCd = activityStatusCd;
        }
    }

    public final String getActivityNm() {
        return activityNm;
    }

    public final void setActivityNm(String activityNm) {
        this.activityNm = activityNm;
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
    
    public final Integer getUserId() {
        return userId;
    }

    public final void setUserId(Integer userId) {
        this.userId = userId;
    }

    public final String getDoLaaName() {
        return doLaaName;
    }

    public final void setDoLaaName(String doLaaName) {
        this.doLaaName = doLaaName;
    }

    public final boolean isHasSearchResults() {
        return hasSearchResults;
    }

    public final void setHasSearchResults(boolean hasSearchResults) {
        this.hasSearchResults = hasSearchResults;
    }

    public final ProcessActivityLight[] getActivities() {
        return activities;
    }

    public final void setActivities(ProcessActivityLight[] activities) {
        this.activities = activities;
    }

    public final String submit() {
    	//init();
        StopWatch timer = new StopWatch();
        timer.start();
        try {
            pa.setFacilityId(facilityId);
            pa.setFacilityNm(facilityNm);
            pa.setUserId(userId);
            pa.setActivityStatusCd(activityStatusCd);
            pa.setActivityTemplateNm(activityNm);
            pa.setProcessCd(ProcessTypeCd);
            pa.setPerformerTypeCd("M");
            pa.setLooped(looped);
            pa.setExternalId(externalId);
            pa.setDoLaaName(doLaaName);
            pa.setDoLaaCd(doLaaCd);
            pa.setUnlimitedResults(unlimitedResults());
            pa.setProcessTemplateNm(processTemplateNm);

            activities = new ProcessActivityLight[0];
            logger.debug("Calling WorkFlowBO");
            activities = App.getApplicationContext().getBean(ReadWorkFlowService.class).retrieveActivityListLight(pa);
            DisplayUtil.displayHitLimit(activities.length);
            timer.stop();
            logger.debug("WorkFlowBO back " + timer.toString());
            timer.start();
            if (activities.length == 0) {
                DisplayUtil.displayNoRecords();
            }

            hasSearchResults = true;

            LinkedList<ProcessActivityLight> ret = new LinkedList<ProcessActivityLight>();
            ArrayList<ProcessActivityLight> headOfList = new ArrayList<ProcessActivityLight>();
            Map<String, ProcessActivityLight> am = new LinkedHashMap<String, ProcessActivityLight>();
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        	if (activities.length > 500) {
        		DisplayUtil.displayInfo("For system performance reasons, Report IDs are not retrieved when there are more than 500 To Do search results.");
        	}
            for (ProcessActivityLight ta : activities) {
            	if (activities.length <= 500) {
            		setReportId(ta);
            	}
            	// display aggregate activities in a single row
                if (ta.getAggregate().equalsIgnoreCase("Y")) {
                    String uid = null;
                    if (ta.getUserId() == null)
                        uid = "Not assigned";
                    else
                        uid = ta.getUserId().toString();
                    String key = uid + ta.getActivityTemplateId() 
                        + ta.getActivityStatusCd();
                    if (ta.getEndDt() != null)
                        key = key + df.format(ta.getEndDt());
                    
                    ProcessActivityLight amTa = am.get(key);
                    if (amTa == null) {
                    	// create new record for this key
                    	am.put(key, ta);
                    } else {
                    	// change template name displayed if more than one activity is in the
                    	// aggregate - just add an asterisk if all activities have the same
                    	// template name or change it to the process template name if different
                    	// types of activity are aggregated.
                    	if (ta.getActivityTemplateId().equals(0) && 
                    			!ta.getActivityTemplateNm().equals(amTa.getActivityTemplateNm().replaceAll(" \\*", ""))) {
                    		amTa.setActivityTemplateNm(ta.getProcessTemplateNm() + " *");
                    	} else {
                    		amTa.setActivityTemplateNm(ta.getActivityTemplateNm() + " *");
                    	}
                    	continue;
                    }
                }
                if ("Scheduled Maint. Bypass Request".equals(ta.getProcessTemplateNm())) {
                	headOfList.add(ta);
                } else {
                	ret.add(ta);
                }
            }
            // add Scheduled Maint Bypass Request items to beginning of list.
            if (headOfList.size() > 0) {
            	for (ProcessActivityLight pal : headOfList) {
            		ret.addFirst(pal);
            	}
            }
            shortActivities = ret.toArray(new ProcessActivityLight[0]);
        } catch (RemoteException re) {
            handleException(re);
        }

        timer.stop();
        logger.debug("Submit done " + timer.toString());
        return "todo";
    }
    
    private void setReportId(ProcessActivityLight ta) throws DAOException, RemoteException {
		if (ta.getExternalId() != null) {
			if (ta.getProcessTemplateNm().equals(WorkFlowProcess.CR_CEMS_COMS_RATA) ||
					ta.getProcessTemplateNm().equals(WorkFlowProcess.CR_ONE_TIME_REPORTS) ||
					ta.getProcessTemplateNm().equals(WorkFlowProcess.CR_OTHER_COMP_REPORTS) ||
					ta.getProcessTemplateNm().equals(WorkFlowProcess.CR_GENERIC_COMPLIANCE_REPORT)
					) {
				ComplianceReport report =
						getComplianceReportService().retrieveComplianceReportOnly(
								ta.getExternalId(), true);
				if (null != report) {
					ta.setReportId(report.getReportCRPTId());
				}
			} 
			else
			if (ta.getProcessTemplateNm().equals(WorkFlowProcess.STACK_TESTS)) {
				StackTest stackTest =
						getStackTestService().retrieveStackTestRowOnly(
								ta.getExternalId());
				if (null != stackTest) {
					ta.setReportId(stackTest.getStckId());
				}
			} 
			else
			if (ta.getProcessTemplateNm().equals(WorkFlowProcess.TV_EI_REVIEW)) {
				EmissionsReport ei = 
						getEmissionsReportService().retrieveEmissionsReportRow(
								ta.getExternalId(), true);
				if (null != ei) {
					ta.setReportId(ei.getEmissionsInventoryId());
				}
			} else
			if (ta.getProcessTemplateNm().equals(WorkFlowProcess.AMBIENT_MONITORING_REPORT)) {
				MonitorReport  monitorReport = 
						getMonitoringService().retrieveMonitorReport(ta.getExternalId());
				if (null != monitorReport) {
					ta.setReportId(monitorReport.getMrptId());
					// since the facility information is not available in the search results
					// we will set it here by retrieving that information from the associated monitoring group
					MonitorGroup monitorGroup = getMonitoringService().retrieveMonitorGroup(monitorReport.getMonitorGroupId());
					if(null != monitorGroup) {
						ta.setFacilityId(monitorGroup.getFacilityId());
						ta.setFacilityNm(monitorGroup.getFacilityName());
						ta.setFpId(monitorGroup.getFpId());
					}
				}
			}
			else
				if (ta.getProcessTemplateNm().equals(WorkFlowProcess.ENFORCEMENT_ACTIONS)) {
					EnforcementAction enforcementAction =
							getEnforcementActionService().retrieveEnforcementAction(
									ta.getExternalId());
					if (null != enforcementAction) {
						ta.setReportId(enforcementAction.getEnfId());
					}
				} 
			else if (ta.getProcessTemplateNm().equals(WorkFlowProcess.INSPECTION_REPORT_WORKFLOW_NAME)
					|| ta.getProcessTemplateNm().equals(WorkFlowProcess.INSPECTION_DUE_SOON)) {
				
				FullComplianceEval fce = getFullComplianceEvalService().retrieveFceOnly(ta.getExternalId());
				
				if (null != fce) {
					
					ta.setReportId(fce.getInspId());
				}
			} 
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("No report ID for this external object type:");
					logger.debug("---> External ID: " + ta.getExternalId());
					logger.debug("---> Process Template Name: " + ta.getProcessTemplateNm());
				}
			}
		}
	}

	public final String submitHome() {
    	init();
        return submit();
    }

    public final String reset() {
        init();
        submit();
        return "todo";
    }
    
    private void init() {
        pa = new ProcessActivity();
        activities = null;
        facilityId = null;
        facilityNm = null;
        userId = InfrastructureDefs.getCurrentUserId();
        activityStatusCd = "IP";
        activityNm = null;
        ProcessTypeCd = null;
        externalId = null;
        hasSearchResults = false;
        fromExternal = false;
        shortActivities = null;
        doLaaName = null;
        doLaaCd = null;
        showFacility = true;
        workflows = null;
        processTemplateNm = null;

    }

    public final Integer getExternalId() {
        return externalId;
    }

    public final void setExternalId(Integer externalId) {
        reset();
        this.externalId = externalId;
        activityStatusCd = null;
        userId = null;
        // Mantis 1619
        //fromExternal = true;
    }

    public final boolean isFromExternal() {
        return fromExternal;
    }

    public final boolean isShowFacility() {
        return showFacility;
    }

    public final void setShowFacility(boolean showFacility) {
        this.showFacility = showFacility;
    }

    public final Integer getRows() {
        return getPageLimit();
    }

    /**
     * @return the looped
     */
    public final boolean isLooped() {
        return looped;
    }

    /**
     * @param looped the looped to set
     */
    public final void setLooped(boolean looped) {
        this.looped = looped;
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

    public String getDoLaaCd() {
        return doLaaCd;
    }

    public void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }
}
