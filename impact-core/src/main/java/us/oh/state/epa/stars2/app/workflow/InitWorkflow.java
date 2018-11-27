package us.oh.state.epa.stars2.app.workflow;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import org.joda.time.LocalDate;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

@SuppressWarnings("serial")
public class InitWorkflow extends AppBase {
    
	private static final String INIT_WORKFLOW_PAGE_VIEW_ID = "initTask";
	
	private String facilityId;
    private Integer userId;
    private Integer workflowId;
    private String rush;
    private Timestamp dueDate;
    private String comment;
    private String subject;
    private int cUserId;
    private Integer externalId = 1; // permit Id & report Id
    private Integer fpId;
    
	private FacilityService facilityService;
    private ReadWorkFlowService readWorkFlowService;
//    private WriteWorkFlowService writeWorkFlowService;

	public ReadWorkFlowService getReadWorkFlowService() {
		return readWorkFlowService;
	}

	public void setReadWorkFlowService(ReadWorkFlowService readWorkFlowService) {
		this.readWorkFlowService = readWorkFlowService;
	}

//	public WriteWorkFlowService getWriteWorkFlowService() {
//		return writeWorkFlowService;
//	}
//
//	public void setWriteWorkFlowService(WriteWorkFlowService writeWorkFlowService) {
//		this.writeWorkFlowService = writeWorkFlowService;
//	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    public InitWorkflow() {
        super();
        reset();
    }

    public final String submit() {
        String ret = ERROR;
        boolean ok = true;
        
        if (Utility.isNullOrEmpty(facilityId)) {
        	DisplayUtil.displayError("Facility ID is required.");
        	ok = false;
        }
        
        if (null == userId) {
        	DisplayUtil.displayError("User is required.");
        	ok = false;
        }
        
        if (null == dueDate) {
        	DisplayUtil.displayError("Due Date is required.");
        	ok = false;
        }
        
        if (subject == null || (subject != null && subject.length() == 0)){
        	DisplayUtil.displayError("Task Name is required.");
        	ok = false;
        }
        
        if (comment == null || (comment != null && comment.length() == 0)){
            DisplayUtil.displayError("Comment is required.");
            ok = false;
        }
  
		if (ok) {
			HashMap<String, String> data = null;
			if (subject != null) {
				data = new HashMap<String, String>();
				data.put("Notes", comment);
				data.put("Task Name", subject);
			}

			try {
				// if (!Utility.isNullOrEmpty(facilityId)) {
				String format = "F%06d";

				int tempId;
				try {
					tempId = Integer.parseInt(facilityId);
					facilityId = String.format(format, tempId);
				} catch (NumberFormatException nfe) {
				}
				// }

				Facility f = getFacilityService().retrieveFacility(facilityId);
				if (f != null)
					fpId = f.getFpId();
				else {
					DisplayUtil.displayError("Cannot find facility.");
					return ret;
				}
			} catch (RemoteException e) {
				handleException(e);
				return ret;
			}

			if (workflowId != 0) {
				WorkFlowManager wfm = new WorkFlowManager();
				WorkFlowResponse resp = wfm.submitProcess(workflowId, externalId, fpId, userId, rush, null, dueDate,
						data);

				if (!resp.hasError() && !resp.hasFailed()) {
					DisplayUtil.displayInfo(SUCCESS);
					ret = SUCCESS;
				} else {
					for (String s : resp.getErrorMessages()) {
						DisplayUtil.displayError(s);
						logger.warn(s);
					}
					for (String s : resp.getRecommendationMessages()) {
						DisplayUtil.displayInfo(s);
						logger.debug(s);
					}
				}
			} else {
				ret = initTask(data);
			}
			if (ret == SUCCESS)
				ret = ((ToDoSearch) FacesUtil.getManagedBean("toDoSearch")).reset();
        }
        return ret;
    }

    private String initTask(HashMap<String, String> data) {
        String ret = ERROR;
//        // decouple workflow
//        throw new RuntimeException("decouple workflow; method not implemented");
        try {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (fpId == null) {
                fpId = 9;
            }
            Integer[] userIds = null;
            if (userId == null) {
                userIds = getReadWorkFlowService().retrieveUserIdsOfFacility(fpId, null);
            } else {
                userIds = new Integer[1];
                userIds[0] = userId;
            }

            WorkFlowManager wfm = new WorkFlowManager();
            for (Integer uId : userIds) {
//                getWriteWorkFlowService().createTaskProcessFlows(workflowId, fpId,
//                        0, rush, now, dueDate, cUserId, data, uId);
                
            	data.put("Task UserId", String.valueOf(uId));
            	
                WorkFlowResponse resp = 
                		wfm.createTaskProcessFlows(workflowId, fpId, 0, rush, now, dueDate, cUserId, data);

                if (resp.hasError() || resp.hasFailed()) {
                	throw new RuntimeException(
                			"error(s) while initializing task: " + 
                	resp.getErrorMessages());
                }
            }
            DisplayUtil.displayInfo(SUCCESS);
            ret = SUCCESS;
        } catch (RemoteException re) {
            handleException(re);
        }
        return ret;
    }

    public final String reset() {

        facilityId = null;
        cUserId = InfrastructureDefs.getCurrentUserId();
        userId = cUserId;
        comment = null;
        dueDate = null;
        rush = "N";
        subject = null;

        return INIT_WORKFLOW_PAGE_VIEW_ID;
    }

    public final Integer getExternalId() {
        return externalId;
    }

    public final void setExternalId(Integer externalId) {
        this.externalId = externalId;
    }

    /**
     * @return the facilityId
     */
    public final String getFacilityId() {
        return facilityId;
    }

    /**
     * @param facilityId the facilityId to set
     */
    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
    
    public final String getSubject() {
        return subject;
    }

    public final void setSubject(String subject) {
        this.subject = subject;
    }

    public final String getComment() {
        return comment;
    }

    public final void setComment(String comment) {
        this.comment = comment;
    }

    public final Timestamp getDueDate() {
        return dueDate;
    }

    public final void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public final String getRush() {
        return rush;
    }

    public final void setRush(String rush) {
        this.rush = rush;
    }

    public final Integer getWorkflowId() {
        return workflowId;
    }

    public final void setWorkflowId(Integer workflowId) {
        this.workflowId = workflowId;
    }

    public final Integer getUserId() {
        return userId;
    }

    public final void setUserId(Integer userId) {
        this.userId = userId;
    }
    
	public Date getMinimumDueDt() {
		// the minimum due date is tomorrow
		return LocalDate.now().plusDays(1).toDate();
	}
}
