package us.oh.state.epa.stars2.app.workflow;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;

import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.permit.PTIOPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitDocument;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitInfo;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitNote;
import us.oh.state.epa.stars2.database.dbObjects.permit.TVPermit;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.TimeSpan;
import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EmissionReportsDef;
import us.oh.state.epa.stars2.def.IssuanceStatusDef;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.WorkflowProcessDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.AreaBean;
import us.oh.state.epa.stars2.webcommon.bean.ImageBase;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.oh.state.epa.stars2.webcommon.output.Process2DImage;
import us.oh.state.epa.stars2.workflow.Activity;
import us.oh.state.epa.stars2.workflow.engine.ProcessFlow;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;


public class WorkFlow2DDraw extends ImageBase {
	
	private static final long serialVersionUID = 7272526461142925392L;

	public static final String ORDER_STATUS = "OS";
    public static final String ORDER_TYPE = "OT";
    private static final int W = 90;
    private static final int H = 60;
    private static final String WORKFLOW_PROFILE = "workflowProfile";
    private String command;
    private String type;
    private String objectName;
    private String subSystem;
    private Integer processId;
    private boolean showList;
    private String url;
    private String listType;
    private ProcessActivity[] activities;
    private WorkFlowProcess process;
    private String note;
    private String subFlowUrl;
    private String externalType;

    private EmissionsReportService emissionsReportService;
	private PermitService permitService;
	private FullComplianceEvalService fullComplianceEvalService;
	
    private ReadWorkFlowService readWorkFlowService;

	public ReadWorkFlowService getReadWorkFlowService() {
		return readWorkFlowService;
	}

	public void setReadWorkFlowService(ReadWorkFlowService readWorkFlowService) {
		this.readWorkFlowService = readWorkFlowService;
	}

	public PermitService getPermitService() {
		return permitService;
	}

	public void setPermitService(PermitService permitService) {
		this.permitService = permitService;
	}
    
    public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}

	public void setEmissionsReportService(
			EmissionsReportService emissionsReportService) {
		this.emissionsReportService = emissionsReportService;
	}
	
    public FullComplianceEvalService getFullComplianceEvalService() {
		return fullComplianceEvalService;
	}

	public void setFullComplianceEvalService(FullComplianceEvalService fullComplianceEvalService) {
		this.fullComplianceEvalService = fullComplianceEvalService;
	}

	public WorkFlow2DDraw() {
        super();
    }
    
    public final String submitClone() {
        String ret = ERROR;
//        // decouple workflow ... reimplement via wf manager
//        throw new RuntimeException("decouple workflow; method not implemented");
        
        
        try {
	        if (WorkFlowProcess.PERMIT_WORKFLOW_NAME.equalsIgnoreCase(process.getProcessTemplateNm())) {
		      PermitService pBO = App.getApplicationContext().getBean(PermitService.class);
		      Permit permit 
		          = pBO.retrievePermit(process.getExternalId()).getPermit();
		      // save the original permit number and description
		      String origPermitNumber = permit.getPermitNumber();
		      String origPermitDescription = permit.getDescription();
		      String[] origPermitDocuments = new String[permit.getDocuments().size()];
		      permit.setPermitNumber(null);
		      permit.setLegacyPermit(false); // Chris: If I'm cloning it in STARS2, then the newly created (cloned) permit is *not* a legacy permit.
		      permit.setEuGroups(null);
		      // Save the document paths of the original permit
		      int i = 0;
		      for(PermitDocument doc : permit.getDocuments()) {
		    	  origPermitDocuments[i++] = doc.getBasePath();
		      }
		      
		      permit = pBO.createPermit(permit, InfrastructureDefs.getCurrentUserId(), null);
		      
		      // remove the field audit log that would have been created for changing the permit number
		      permit.checkDirty("pnum", origPermitNumber, null, origPermitNumber);
     
		      // set permit document ids to NULL so that a new id can be generated and assigned to the documents
		      for (PermitDocument doc : permit.getDocuments())
		    	  	doc.setDocumentID(null);
   		      pBO.modifyPermitDocuments(permit);

		      // clone remaining permit information however don't clone the final issuance information
   		      // if the cloning permit has already issued final
   		      if(null != permit.getFinalIssuanceStatusCd()
   		    		  && permit.getFinalIssuanceStatusCd().equals(IssuanceStatusDef.ISSUED)) {
   		    	  permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.NONE);
   		    	  permit.setFinalIssuanceStatusCd(IssuanceStatusDef.NOT_READY);
   		    	  permit.setFinalIssueDate(null);
   		    	  permit.setExpirationDate(null);
   		    	  if(permit instanceof PTIOPermit) 
   		    		  permit.setPermitSentOutDate(null);
   		    	  if(permit instanceof TVPermit)
   		    		  permit.setEffectiveDate(null);
   		      }

   		      // if the original permit had a description use it for the cloned permit as well since
   		      // createPermit would have set the permit description to the facility description
   		      if(null != origPermitDescription)
   		    	  permit.setDescription(origPermitDescription);
   		      
   		      pBO.modifyPermit(permit, InfrastructureDefs.getCurrentUserId(), null);
		      
		      // clone the documents in the original permit
   		      String origDoc = null;
		      String copyDoc = null;
		      i=0;
		      try {
		    	  	for(PermitDocument doc : permit.getDocuments()) {
		    	  		origDoc = File.separator + "Facilities" + File.separator + permit.getFacilityId() + origPermitDocuments[i++];
		    	  		copyDoc = File.separator + "Facilities" + File.separator + permit.getFacilityId() + doc.getBasePath();
		    	  
		    	  		DocumentUtil.copyDocument(origDoc, copyDoc);
		    	  	}
		      } catch(IOException ioe) {
		    		  DisplayUtil.displayError("A system error has occurred while cloninng permit document. " +
		    		  		"Please contact System Administrator.");
		              logger.error(ioe.getMessage(), ioe);
		      }
		      
		      // clone the notes from the original permit
		      for(Note note : permit.getDapcComments()) {
		    	  PermitNote pnote = (PermitNote)note;
		    	  pnote.setPermitId(permit.getPermitID());
		    	  pBO.createPermitNote(pnote);
		      }
		    
		      process.setExternalId(permit.getPermitID());
		      
	        }
         
		      WorkFlowManager wfm = new WorkFlowManager();
		      WorkFlowResponse resp = wfm.cloneProcess(process);
              if (resp.hasError() || resp.hasFailed()) {
            	  String errMsg = "";
            	  for (String err : resp.getErrorMessages()) {
            		  errMsg += err + "; ";
            	  }
              	throw new RuntimeException(
              			"error(s) while cloning process: " + errMsg);
              } else {
            	  DisplayUtil.displayInfo("Workflow was successfully cloned.");
            	  ret = SUCCESS;
              }
        } catch (RemoteException e) {
        	handleException(e);
        }
	      
	      
	      
//        try {
//            WorkFlowProcess tp = getWriteWorkFlowService().cloneProcess(processId,
//                    InfrastructureDefs.getCurrentUserId());
//            setProcessId(tp.getProcessId());
//            DisplayUtil.displayInfo(SUCCESS);
//            ret = SUCCESS;
//        } catch (RemoteException re) {
//            handleException(re);
//        }
        return ret;
    }

	public final String submitCancellation() {

		String ret = ERROR;

		int userId = InfrastructureDefs.getCurrentUserId();
		WorkFlowManager wfm = new WorkFlowManager();
		WorkFlowResponse cancelProcessResp = wfm.cancelProcess(processId, userId, note);

		if (!cancelProcessResp.hasError() && !cancelProcessResp.hasFailed()) {

			try {

				int processCnt = 0;
				WorkFlowProcess wfp = new WorkFlowProcess();
				wfp.setProcessTemplateId(process.getProcessTemplateId());
				wfp.setExternalId(process.getExternalId());
				wfp.setState(WorkFlowProcess.STATE_IN_PROCESS_CD);
				WorkFlowProcess[] wfpArr = getReadWorkFlowService().retrieveProcessList(wfp);
				if (wfpArr != null) {
					for (WorkFlowProcess wp : wfpArr) {
						if (wp.getState() != null && wp.getState().equals(WorkFlowProcess.STATE_IN_PROCESS_CD)) {
							processCnt++;
						}
					}
				}

				if (processCnt == 0) {
					TaskBase externalBean = TaskBase.setUp(getProcess(), null, getProcess().getExternalId());
					if (externalBean != null) {
						externalBean.cancellation();
					}
				} else {
		            DisplayUtil.displayInfo("Multiple workflow processes are associated with this process's external object. "
		            		+ "The object will not be cancelled, deleted, or have its status changed until all associated "
		            		+ "workflows are cancelled.");					
				}

			} catch (Exception e) {
	            DisplayUtil.displayError("Error while cancelling external object associated with this process.");
				logger.error("Error while cancelling external object associated with process " + process.getProcessId() 
					+ ": " + e.getMessage(), e);				
			}

			process = null;

			ActivityProfile ap = (ActivityProfile) FacesUtil.getManagedBean("activityProfile");
			ap.setProcessNotes(null);
			note = "";
			ret = WORKFLOW_PROFILE;

		} else {
			handleWorkFlowResponse(cancelProcessResp);
		}
		
		return ret;
	}

    private void handleWorkFlowResponse(WorkFlowResponse resp) {
        for (String s : resp.getErrorMessages()){
            DisplayUtil.displayError(s);
            logger.warn(s);
        }
        for (String s : resp.getRecommendationMessages()){
            DisplayUtil.displayInfo(s);
            logger.debug(s);
        }
    }

    public final String stopCancellation() {
        note = null;
        return SUCCESS;
    }

    public final String reload() {
        image = null;
        areas = null;
        activities = null;
        process = null;
        
        if(processId != null) {
			try {
				WorkFlowProcess p = getReadWorkFlowService().retrieveProcess(
						processId);
				if (p != null) {
					if (WorkFlowProcess.PERMIT_WORKFLOW_NAME.equalsIgnoreCase(p.getProcessTemplateNm())
							|| WorkFlowProcess.COMPLIANCE_REPORTS.equalsIgnoreCase(p.getProcessTemplateNm())
							|| WorkFlowProcess.CR_CEMS_COMS_RATA.equalsIgnoreCase(p.getProcessTemplateNm())
							|| WorkFlowProcess.CR_ONE_TIME_REPORTS.equalsIgnoreCase(p.getProcessTemplateNm())
							|| WorkFlowProcess.CR_OTHER_COMP_REPORTS.equalsIgnoreCase(p.getProcessTemplateNm())
							|| WorkFlowProcess.STACK_TESTS.equalsIgnoreCase(p.getProcessTemplateNm())
							|| WorkFlowProcess.CR_GENERIC_COMPLIANCE_REPORT.equalsIgnoreCase(p.getProcessTemplateNm())
							|| WorkFlowProcess.FACILITY_CHANGES.equalsIgnoreCase(p.getProcessTemplateNm())
							|| WorkFlowProcess.ENFORCEMENT_ACTIONS.equalsIgnoreCase(p.getProcessTemplateNm())
							|| WorkFlowProcess.INSPECTION_REPORT_WORKFLOW_NAME.equalsIgnoreCase(p.getProcessTemplateNm())) {
						ProcessFlow activeProcessFlow;

						activeProcessFlow = getReadWorkFlowService().retrieveActiveProcessFlow(processId);
						if (isInternalApp()) {
							if (activeProcessFlow != null) {
								SimpleMenuItem.setDisabled("menuItem_cloneProcess", false);
								SimpleMenuItem.setDisabled("menuItem_changeProcessDueDate", false);
								SimpleMenuItem.setDisabled("menuItem_cancelProcess", false);
								SimpleMenuItem.setDisabled("menuItem_reassign", false);
							} else {
								SimpleMenuItem.setDisabled("menuItem_cloneProcess", true);
								SimpleMenuItem.setDisabled("menuItem_changeProcessDueDate", true);
								SimpleMenuItem.setDisabled("menuItem_cancelProcess", true);
								SimpleMenuItem.setDisabled("menuItem_reassign", true);
							}

							if (!WorkFlowProcess.PERMIT_WORKFLOW_NAME.equalsIgnoreCase(p.getProcessTemplateNm())) {
								SimpleMenuItem.setDisabled("menuItem_cloneProcess", true);
							}
						}
					} else {
						if (isInternalApp()) {
							SimpleMenuItem.setDisabled("menuItem_cloneProcess", true);
						}
					}
				}
			} catch (DAOException e) {
				logger.error("Error retrieving process data: " + e.getMessage());
			} catch (RemoteException e) {
				logger.error("Error retrieving process data: " + e.getMessage());
			}
		}
        
        return "workflowProfile";
    }

    public final String changeDueDate() {
    	// get the process dates in the joda datetime for validation
    	DateTime processStartDate = new DateTime(getProcess().getStartDt().getTime()).withTimeAtStartOfDay();
    	DateTime processJeopardyDate = new DateTime(getProcess().getJeopardyDt().getTime()).withTimeAtStartOfDay();
    	DateTime processDueDate = new DateTime(getProcess().getDueDt().getTime()).withTimeAtStartOfDay();
    	
    	/**** jeopardy date ****/
        if (!processJeopardyDate.isAfter(processStartDate.getMillis())){
            DisplayUtil.displayError(
            		"Jeopardy date is not after process start date: " + 
            				processStartDate.toString("MM/dd/yyyy"));
            return ERROR;
        }
        if (!processJeopardyDate.isBefore(processDueDate.getMillis())){
            DisplayUtil.displayError(
            		"Jeopardy date is not before process due date: " + 
            				processDueDate.toString("MM/dd/yyyy"));
            return ERROR;
        }

        /**** due date ****/
        if (!processDueDate.isAfter(processStartDate.getMillis())){
            DisplayUtil.displayError(
            		"Due date is not after process start date: " + 
            				processStartDate.toString("MM/dd/yyyy"));
            return ERROR;
        }
        if (!processDueDate.isAfter(processJeopardyDate.getMillis())){
            DisplayUtil.displayError(
            		"Due date is not after process jeopardy date: " + 
            				processJeopardyDate.toString("MM/dd/yyyy"));
            return ERROR;
        }

        String ret = ERROR;
        WorkFlowManager wfm = new WorkFlowManager();
        WorkFlowResponse resp = wfm.changeDueDate(processId, null, process
                .getJeopardyDt(), process.getDueDt());

        if (!resp.hasError() && !resp.hasFailed()) {
        	
        	
//            ProcessNote pn = new ProcessNote();
//            pn.setProcessId(processId);
//            pn.setUserId(InfrastructureDefs.getCurrentUserId());
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            
            String txt = "Process "; 
            if (process.getOldDueDt() != null)
                txt += "due date changed from " + df.format(process.getOldDueDt());
            if (process.getOldDueDt() != null && process.getOldJeopardyDt() != null)
                txt += " and ";
            if (process.getOldJeopardyDt() != null)
                txt += "jeopardy date changed from " + df.format(process.getOldJeopardyDt());
//            pn.setNote(txt);

            WorkFlowResponse saveNoteResp = 
            		wfm.saveNote(processId, InfrastructureDefs.getCurrentUserId(), txt);
            
            if (!saveNoteResp.hasError() && !saveNoteResp.hasFailed()) {
            	DisplayUtil.displayInfo("Date change success.");
            } else {
            	handleWorkFlowResponse(saveNoteResp);
            }
            
//            try {
//            	getWriteWorkFlowService().createProcessNote(pn);
//                
            
            //TODO decouple workflow ... compare this with the pre-decoupled version
                ActivityProfile ap = (ActivityProfile) FacesUtil
                    .getManagedBean("activityProfile");
                ap.setProcessNotes(null);
                note = "";
                ret = WORKFLOW_PROFILE;
           // ^^^    
                
//            } catch (RemoteException re) {
//                logger.error(re.getMessage(), re);
//                DisplayUtil.displayError("System error. Please contact system administrator");
//            }
            process = null;
            DisplayUtil.displayInfo(SUCCESS);
            ret = SUCCESS;
        } else {
        	handleWorkFlowResponse(resp);
        }
        return ret;
    }

    public final String submitProfile() {
        reload();

        return SUCCESS;
    }

    public final String reset() {
        type = null;
        objectName = null;
        subSystem = null;

        return SUCCESS;
    }

    public final WorkFlowProcess getProcess() {
        if (process == null) {
            try {
                process = getReadWorkFlowService().retrieveProcess(processId);
            } catch (RemoteException re) {
                handleException(re);
            }
        }
        return process;
    }

    public final void setProcess(WorkFlowProcess process) {
        this.process = process;
    }

    public final ProcessActivity[] getActivities() {
        if (activities == null) {
            try {
                ProcessActivity pa = new ProcessActivity();
                pa.setProcessId(processId);
                ArrayList<String> statusCds = new ArrayList<String>();
                statusCds.add(ActivityStatusDef.NOT_COMPLETED);
                pa.setPerformerTypeCd("M");
                pa.setActivityStatusCds(statusCds);
                pa.setInStatus(false);
                pa.setUnlimitedResults(true);

                // New Stuff - Sam
                ProcessActivity total = new ProcessActivity();
                total.setActivityTemplateNm("Total Number of Days");
                total.setLoopCnt(null);

                activities = getReadWorkFlowService().retrieveActivityList(pa);
                ArrayList<ProcessActivity> acts = new ArrayList<ProcessActivity>();
                int totalNum = 0;
                for (ProcessActivity a : activities) {
                    if (a.getStartDt() == null)
                        continue;
                    
                    TimeSpan d = a.getDuration();
                    if (d != null) {
                        totalNum += d.getDays();
                    }
                    acts.add(a);
                    if (a.getActivityStatusCd().equalsIgnoreCase(ActivityStatusDef.COMPLETED)
                    		|| a.getActivityStatusCd().equalsIgnoreCase(ActivityStatusDef.UNREFERRED)
                    		|| a.getActivityStatusCd().equalsIgnoreCase(ActivityStatusDef.REFERRED)
                            || a.getActivityStatusCd().equalsIgnoreCase(ActivityStatusDef.IN_PROCESS)
                            || a.getActivityStatusCd().equalsIgnoreCase(ActivityStatusDef.ABANDONED)) {
                        total.setDolaaDuration(total.getDolaaDuration()
                                + a.getDolaaDuration());
                        total.setCompanyDuration(total.getCompanyDuration()
                                + a.getCompanyDuration());
                        total.setLegalDuration(total.getLegalDuration()
                                + a.getLegalDuration());
                        total.setAqmpDuration(total.getAqmpDuration()
                                + a.getAqmpDuration());
                        total.setPmuDuration(total.getPmuDuration()
                                + a.getPmuDuration());
                        total.setAqdDuration(total.getAqdDuration()
                                + a.getAqdDuration());
                        
                    }
                }
                
                Timestamp now = new Timestamp(System.currentTimeMillis());
                TimeSpan d = new TimeSpan(now, now);
                d.setDays(totalNum);
                total.setDuration(d);
                acts.add(total);
                activities = acts.toArray(new ProcessActivity[0]);
                // End of New Stuff
            } catch (RemoteException re) {
                logger.error(re.getMessage(), re);
                DisplayUtil.displayError("System error. Please contact system administrator");
            }
        }
        return activities;
    }

    public final void setActivities(ProcessActivity[] activities) {
        this.activities = activities;
    }

    public final String getListType() {
        return listType;
    }

    public final void setListType(String listType) {
        this.listType = listType;
    }

    public final String getObjectName() {
        return objectName;
    }

    public final void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public final Integer getProcessId() {
        return processId;
    }

    public final void setProcessId(Integer processId) {
        this.processId = processId;
        reload();
        ActivityProfile ap = (ActivityProfile) FacesUtil
                .getManagedBean("activityProfile");

        ap.setProcessId(processId);
    }

    public final String getSubSystem() {
        return subSystem;
    }

    public final void setSubSystem(String subSystem) {
        this.subSystem = subSystem;
    }

    public final String getType() {
        return type;
    }

    public final void setType(String type) {
        showList = true;
        image = null;
        areas = null;
        this.type = type;
    }

    public final boolean isShowList() {
        return showList;
    }

    public final void setShowList(boolean showList) {
        this.showList = showList;
    }

    public final String getUrl() {
        return url;
    }

    public final void setUrl(String url) {
        this.url = url;
    }

    public final String getNote() {
        return note;
    }

    public final void setNote(String note) {
        this.note = note;
    }

    public final String getCommand() {
        return command;
    }

    public final void setCommand(String command) {
        if (command.equalsIgnoreCase("reload")) {
            reload();
        }
        this.command = command;
    }

    /**
     * getAreas
     * 
     * @return
     */
    public final AreaBean[] getAreas() {
        if (areas == null) {
            try {
                draw(getWidth(), getHeight());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return areas;
    }

    /**
     * getImage
     * 
     * @return
     */
    public final BufferedImage getImage() {
        if (image == null) {
            try {
                process = null;
                activities = null;
                draw(getWidth(), getHeight());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return image;
    }

    /**
     * draw
     * 
     * @param width
     * @param height
     * @throws Exception
     */
    public final void draw(int width, int height) throws Exception {
        if (width == 0) {
            setWidth(1050);
        } else {
            setWidth(width);
        }

        WorkFlowProcess p = getReadWorkFlowService().retrieveProcess(processId);
        if (p == null) {
           logger.error("No Process found for Process ID = " + processId + ". Session may be timeout.");
           return;
           // throw new Exception("No Process found for Process ID = "
           //         + processId + ".");
        }
        
        String sla = selectSlaString(p);

        Process2DImage pi = new Process2DImage(width, height);
        pi.drawActLegend();
        if (isInternalApp()) {
        	pi.drawSlaLegend(sla);
        }

        Integer serviceId = p.getServiceId();
        if (serviceId == null) {
            serviceId = p.getProcessId();
        }
        Integer orderId = p.getExternalId();
        String serviceName = retrieveServiceName(serviceId, orderId);

        ArrayList<String> lines = new ArrayList<String>();
        try {
            if (WorkflowProcessDef.EMISSION_REPORTING.equals(p.getProcessCd())) {
                externalType = "Report : ";
                EmissionsReport er = getEmissionsReportService().retrieveEmissionsReport(p.getExternalId(), false);
                if (er != null){
                    lines.add("Emissions Inventory ID : " + er.getEmissionsInventoryId());
                    String years = er.getReportYear() + "";
                    if (er.getCompanionReport() != null){
                        EmissionsReport er2 = getEmissionsReportService().retrieveEmissionsReport(er.getCompanionReport(), false);
                        years = years + " & " + er2.getReportYear();
                    }
                    lines.add("Reporting Year : " + years);
                    ArrayList<String> category = new ArrayList<String>(1);
                    
                    lines.add("Report Type : " + getDescs(category,
                            EmissionReportsDef.getData().getItems())
                            //+ getDescs(er.getReportTypes(),
                            //ReportGroupTypes.getData().getItems())
                            );
                     
                }
                
            } else if (WorkflowProcessDef.PERMITTING.equals(p.getProcessCd())) {
                externalType = "Permit : ";
                PermitInfo permit = getPermitService()
                        .retrievePermit(p.getExternalId());
                lines.add("Permit Number : "
                        + permit.getPermit().getPermitNumber());
                lines.add("Permit Type : "
                        + permit.getPermit().getPermitTypeDsc());
                
                List<String> cds = new ArrayList<String>();
                cds = permit.getPermit().getPermitReasonCDs();
                if(cds.size() > 3) {
                	List<String> line1Cds = new ArrayList<String>();
                	List<String> line2Cds = new ArrayList<String>();
                	int i = 0;
                	for(String cd : cds) {
                		if(i < 3){
                			line1Cds.add(cd);
                		} else {
                			line2Cds.add(cd);
                		}
                		i++;
                	}
                	lines.add("Permit Reason(s) : " + 
                            getDescs(line1Cds, 
                                    PermitReasonsDef.getData().getItems()) + ",");
                	lines.add("                                 " + 
                            getDescs(line2Cds, 
                                    PermitReasonsDef.getData().getItems()));
                } else {
					lines.add("Permit Reason(s) : "
							+ getDescs(cds, PermitReasonsDef.getData()
									.getItems()));
                }
            } if (WorkflowProcessDef.INSPECTION.equals(p.getProcessCd())) {
                externalType = "Inspection : ";
                FullComplianceEval fce = getFullComplianceEvalService().retrieveFceOnly(p.getExternalId());
                if (fce != null){
                    lines.add("Inspection ID : " + fce.getInspId());
                }
            } else {
                externalType = "Other : ";
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            DisplayUtil.displayError("System error. Error reading external object.");
        }

        if (p.getParentProcessId() == null) {
            pi.drawTitleLegend(externalType, p.getProcessId().toString(), p
                    .getProcessTemplateNm(), sla, p.getStartDt(), p
                    .getJeopardyDt(), p.getDueDt(), p.getEndDt(), p
                    .getFacilityNm(), p.getFacilityIdString(), p
                    .getExternalId(), serviceName, lines, isInternalApp());
        } else {
            pi.drawTitleLegend(externalType, p.getProcessId().toString(), p
                    .getActivityTemplateNm(), sla, p.getStartDt(), p
                    .getJeopardyDt(), p.getDueDt(), p.getEndDt(), p
                    .getFacilityNm(), p.getFacilityIdString(), p
                    .getExternalId(), serviceName, lines, isInternalApp());
        }

        buildActivitys(pi, sla, p);

        image = pi.getImage();
    }

    private String getDescs(List<String> cds, DefSelectItems items) {
        String ts = "";
        int i = 1;
        for (String s : cds) {
            s = items.getItemDesc(s);
            
            
            if(i < cds.size()) {
            	ts = ts + s + ", ";
            } else {
            	ts = ts + s;
            }
            
            i++;
        }
        return ts;
    }

    static private class ActivityLocation {
        private Integer xLoc;

        private Integer yLoc;

        private boolean loop;

        ActivityLocation(Integer xloc, Integer yloc, boolean loop) {
            this.xLoc = xloc;
            this.yLoc = yloc;
            this.loop = loop;
        }

        Integer getXloc() {
            return this.xLoc;
        }

        Integer getYloc() {
            return this.yLoc;
        }

        boolean isLoop() {
            return loop;
        }
    }

    /**
     * @param p
     * @param sla
     * @param pi
     * @param processId2
     * @return
     */
    private void buildActivitys(Process2DImage pi, String sla, WorkFlowProcess p)
            throws Exception {
        HashMap<Integer, ActivityLocation> locationMap = new HashMap<Integer, ActivityLocation>();
        int minX = 1000;
        int minY = 0;
        int maxX = 0;
        int maxY = 1000;
        ArrayList<AreaBean> aab = new ArrayList<AreaBean>();

        ProcessActivity[] procActs = getReadWorkFlowService().retrieveProcessActivities(
                processId);
        for (ProcessActivity pa : procActs) {
            Integer activityId = pa.getActivityTemplateId();
            ActivityTemplate at = getReadWorkFlowService().retrieveActivityTemplate(
                    activityId);
            Integer xLoc = at.getXloc();
            Integer yLoc = at.getYloc();
            boolean loopAct = at.getOutTransitionDefCd().equals(Activity.LOOP);
            ActivityLocation al = new ActivityLocation(xLoc, yLoc, loopAct);
            locationMap.put(activityId, al);
            if (xLoc.intValue() < minX) {
                minX = xLoc.intValue();
                minY = yLoc.intValue();
            }

            if (xLoc.intValue() > maxX) {
                maxX = xLoc.intValue();
                maxY = yLoc.intValue();
            }
            Integer subFlowId = findSubflowId(pa);
            String activityNm = at.getActivityTemplateNm();
            String statusCd = pa.getActivityStatusCd();
            String performerCd = pa.getPerformerTypeCd();
            String outTran = at.getOutTransitionDefCd();
            String terminalInd = getTerminalInd(at);
            String aggregate = pa.getAggregate();
            Integer externalId = p.getExternalId();
            String slaStatus = pa.getStatus();

            int loop = pa.getLoopCnt();
            // Only show colored status at top left corner of Activity box if isInternalApp().
            if (!isInternalApp()) {
            	slaStatus = WorkFlowProcess.STATUS_OK_CD;
            }
            pi.drawActivity(subFlowId.intValue(), xLoc.intValue(), yLoc
                    .intValue(), activityNm, statusCd, performerCd, slaStatus,
                    outTran, terminalInd, loop, loopAct);

			// Disable navigation when clicking on Activity box if not internalApp.
			if (isInternalApp()) {
				String activityUrl = createActivityUrl(subFlowId, at);
				buildAreaMap(aab, xLoc, yLoc, subFlowId, activityUrl, activityId, aggregate, externalId);
			}
        }

        ActivityLocation fromAct;
        ActivityLocation toAct;
        if ("Y".equalsIgnoreCase(p.getDynamicInd()) && (procActs.length > 7)) {
            pi.drawBrackets(minX, minY, maxX, maxY);
        } else {
            Transition[] trans = getReadWorkFlowService().retrieveTransitions(
                    p.getProcessTemplateId(), true);
            for (Transition t : trans) {
                Integer fromId = t.getFromId();
                Integer toId = t.getToId();
                fromAct = locationMap.get(fromId);
                toAct = locationMap.get(toId);

                Integer x1 = fromAct.getXloc();
                Integer y1 = fromAct.getYloc();
                Integer x2 = toAct.getXloc();
                Integer y2 = toAct.getYloc();
                pi.drawTransition(x1.intValue(), y1.intValue(), x2.intValue(),
                        y2.intValue(), fromAct.isLoop());
            }
        }
        areas = aab.toArray(new AreaBean[0]);
    }

    /**
     * @param aab
     * @param yLoc
     * @param xLoc
     * @param subFlowId
     * @param activityUrl
     * @param activityId
     * @param aggregate
     * 
     */
    private void buildAreaMap(ArrayList<AreaBean> aab, Integer xLoc,
            Integer yLoc, Integer subFlowId, String activityUrl,
            Integer activityId, String aggregate, Integer externalId) {
        int x1 = (xLoc.intValue() * getWidth()) / 1000;
        int y1 = (yLoc.intValue() * getHeight()) / 1000;
        int x2 = x1 + W;
        int y2 = y1 + H;

        // If this Activity has a sub-flow, then we need a different
        // URL for the "Dtls" button (Details) than the rest of the
        // box.

        if (subFlowId.intValue() != -1) {
            // Set the URL for the part of the Activity box immediately
            // to the left of the "Dtls" button.
            aab.add(new AreaBean("RECT", x1 + "," + y1 + "," + (x1 + 60) + ","
                    + y2, activityUrl + "?processId=" + subFlowId));

            // Set the URL for the part of the Activity box immediately
            // below the "Dtls" button.

            aab.add(new AreaBean("RECT", (x1 + 60) + "," + (y1 + 11) + "," + x2
                    + "," + y2, activityUrl + "?processId=" + subFlowId));

            // Set the URL for the "Dtls" button.
            String dtlsUrl = "../prov/subflow-status.html";
            aab.add(new AreaBean("RECT", (x1 + 60) + "," + y1 + "," + x2 + ","
                    + (y1 + 11), dtlsUrl + "?processId=" + subFlowId));
        } else {
            String param = "&externalId=";
            if (activityUrl.contains("permitDetail")) {
                param = "&permitID=";
            }
            // This Activity does not have a sub-flow. Use the same
            // URL for the entire box.
            activityUrl = url;
            aab.add(new AreaBean("RECT", x1 + "," + y1 + "," + x2 + "," + y2,
                    activityUrl + "?processId=" + processId
                            + "&activityTemplateId=" + activityId
                            + "&aggregate=" + aggregate + param + externalId));
        }
    }

    /**
     * @param subFlowId
     * @param at
     * @return
     */
    private String createActivityUrl(Integer subFlowId, ActivityTemplate at)
            throws Exception {
        String ret = subFlowUrl;

        if ((subFlowId == null) || (subFlowId.intValue() == 0)) {
            ActivityTemplateDef atd = getReadWorkFlowService().retrieveActivityTemplateDef(
                    at.getActivityTemplateDefId());
            ret = atd.getActivityUrl();
        }

        return ret;
    }

    /**
     * @param at
     * @return
     */
    private String getTerminalInd(ActivityTemplate at) throws Exception {
        String terminalInd = "N";
        Integer atdId = at.getActivityTemplateDefId();

        ActivityTemplateDef atd = getReadWorkFlowService().retrieveActivityTemplateDef(
                atdId);

        if (atd != null) {
            String foo = atd.getTerminalInd();

            if ((foo != null) && (foo.length() > 0)) {
                terminalInd = foo;
            }
        }

        return terminalInd;
    }

    /**
     * @param pa
     * @return
     */
    private Integer findSubflowId(ProcessActivity pa) throws Exception {
        Integer sfId = new Integer(-1);

        Integer lProcessId = pa.getProcessId();
        Integer actTemplId = pa.getActivityTemplateId();

        WorkFlowProcess subflow = getReadWorkFlowService().retrieveSubFlowProcess(
                lProcessId, actTemplId);

        if (subflow != null) {
            sfId = subflow.getProcessId();
        }

        return sfId;
    }

    /**
     * @param serviceId
     * @param orderId
     * @return
     */
    private String retrieveServiceName(Integer serviceId, Integer orderId)
            throws Exception {
        return "Unknown";
    }

    /**
     * @param process
     * @return
     */
    private String selectSlaString(WorkFlowProcess inProcess) {
        Timestamp cdate = new Timestamp(java.lang.System.currentTimeMillis());
        String sla = "OK";

        Timestamp dueDt = inProcess.getDueDt();
        Timestamp jeopardyDt = inProcess.getJeopardyDt();

        if (dueDt != null) {
            if (cdate.after(dueDt)) {
                sla = "FA";
            } else if (cdate.after(jeopardyDt)) {
                sla = "JP";
            }
        }
        return sla;
    }

    public final String getSubFlowUrl() {
        return subFlowUrl;
    }

    public final void setSubFlowUrl(String subFlowUrl) {
        this.subFlowUrl = subFlowUrl;
    }

    public final String getExternalType() {
        return externalType;
    }

    public final void setExternalType(String externalType) {
        this.externalType = externalType;
    }
    
    // The get image is already doing re-draw for image.
    // This method created a NullPointerException in  
    // us.oh.state.epa.stars2.webcommon.FacesUtil.getManagedBean(FacesUtil.java:251)
/*    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();

        try {
            draw(getHeight(), getWidth());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }*/
}
