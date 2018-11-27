package us.oh.state.epa.stars2.database.dbObjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import us.oh.state.epa.portal.datasubmit.Attachment;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.webcommon.reports.NtvReport;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorReport;

@SuppressWarnings("serial")
public class Task extends BaseDB {
    private String taskName = "0";
    private TaskType taskType;
//    private String taskTypeString;
    private String taskDescription;
    private Integer taskInternalId;
    private String taskId;
    private String dependentTaskId;
    private boolean dependent;
    private transient Timestamp createDate;
    private long longCreateDate;
    private String userName;
    private String version;
    private String facilityId;
    private Integer fpId;
    private Integer referenceCount;
    private Integer corePlaceId;
    private String submissionId;
    private String gatewaySubmiterUserNm;
    private boolean nonROSubmission = false;
    private Integer documentId;
    
	// Remaining attributes not in database
    private Integer newFpId; // used to pass new FpId from portal to internal during submit of Historical ER
    private Facility facility;
    private List<Contact> facilityContacts;
    private us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment attestationDoc; // for use with contact change requests
    private Application application;
    private ComplianceReport complianceReport;
    private StackTest stackTest;
    /* Operations are:
     * blank - not yet set to an operation.
     * n - create new empty report from current profile.
     * r - create revised report, given year and fpId.
     */
    private transient String emissionsReportOperation = " ";
    private transient String dependentTaskDescription;
    private EmissionsReport report;
    private NtvReport ntvReport;
    private RelocateRequest relocateRequest;
    private static HashMap<TaskType, String> taskTypeDescs;
    
    private List<Attachment> attachments = new ArrayList<Attachment>(0);

    private List<MonitorReport> monitorReports = new ArrayList<MonitorReport>(0);
    
    public static enum TaskType {
        COPY_PTPA, COPY_TVPA, CR_CEMS, CR_GENERIC, CR_OTHR, CR_PER, CR_SMBR, 
        CR_TEST, CR_TVCC, CR_ONE, DOR, ER, FC, FCC, FCH, PBR, PTPA, R_ER, REL, 
        RPC, ST, TIVPA, TVPA, MRPT
    }

    public Task() {
    	super();
    	if (taskTypeDescs == null) {
    		taskTypeDescs = new HashMap<TaskType, String>(); 
    		taskTypeDescs.put(TaskType.FC, "Facility Detail Change");
    		taskTypeDescs.put(TaskType.FCC, "Facility Contact Change");
    		taskTypeDescs.put(TaskType.FCH, "Facility Historical Change");
    		taskTypeDescs.put(TaskType.ER, "Emissions Inventory");  // we don't need to tell the difference between new/revised here.  This used to specify NEW
            taskTypeDescs.put(TaskType.R_ER, "Emissions Inventory"); // we don't need to tell the difference between new/revised here.  This used to specify REVISED
    		taskTypeDescs.put(TaskType.TVPA, "Title V Permit Application");
    		//taskTypeDescs.put(TaskType.TIVPA, "Title IV Acid Rain Application");
            taskTypeDescs.put(TaskType.COPY_TVPA, "Copy/Correct Title V Permit Application");
    		taskTypeDescs.put(TaskType.PTPA, "NSR Permit Application");
            taskTypeDescs.put(TaskType.COPY_PTPA, "Copy/Correct NSR Permit Application");
    		//taskTypeDescs.put(TaskType.PBR, "Permit-by-rule Notification");
            //taskTypeDescs.put(TaskType.RPC, "Request Administrative Permit Amendment");
            //taskTypeDescs.put(TaskType.DOR, "Delegation of Responsibility");
            taskTypeDescs.put(TaskType.CR_OTHR, "Compliance: Other Compliance Report");
//            taskTypeDescs.put(TaskType.CR_PER, "Compliance: Permit Evaluation Report");
//            taskTypeDescs.put(TaskType.CR_TVCC, "Compliance: TV Annual Certification");
//            taskTypeDescs.put(TaskType.CR_TEST, "Compliance: Emissions/Stack Testing");
            taskTypeDescs.put(TaskType.CR_CEMS, "Compliance: CEMS/COMS/RATA");
//            taskTypeDescs.put(TaskType.CR_SMBR, "Compliance: Scheduled Maintenence Bypass");
            taskTypeDescs.put(TaskType.CR_ONE, "Compliance: One Time Compliance Report");
//            taskTypeDescs.put(TaskType.CR_GENERIC, "Compliance: Generic Compliance Report");
            //taskTypeDescs.put(TaskType.REL, "Intent To Relocate");
            taskTypeDescs.put(TaskType.ST, "Stack Test");
            taskTypeDescs.put(TaskType.MRPT, "Monitor Reports");
    	}
    }
    public final Integer getTaskInternalId() {
        return taskInternalId;
    }

    public final void setTaskInternalId(final Integer taskInternalId) {
        this.taskInternalId = taskInternalId;
    }

    public final String getTaskDescription() {
        return taskDescription;
    }

    public final void setTaskDescription(final String taskDescription) {
        this.taskDescription = taskDescription;
    }
    
	public List<MonitorReport> getMonitorReports() {
		return monitorReports;
	}
	public void setMonitorReports(List<MonitorReport> monitorReports) {
		this.monitorReports = monitorReports;
	}
	public final String getTaskName() {
        return taskName;
    }

    public final void setTaskName(final String taskName) {
        this.taskName = taskName;
    }

    public final TaskType getTaskType() {
        return taskType;
    }

    public final void setTaskType(final TaskType taskType) {
    	this.taskType = taskType;
    }

    public final String getTaskTypeDescription() {
    	return taskTypeDescs.get(taskType);
    }
    
    public final Timestamp getCreateDate() {
        return createDate;
    }

    public final void setCreateDate(final Timestamp createDate) {
        this.createDate = createDate;
        if (this.createDate != null) {
            this.longCreateDate = this.createDate.getTime();
        } else {
            this.longCreateDate = 0;
        }
    }

    public HashMap<TaskType, String> getTaskTypeDescs() {
		return taskTypeDescs;
	}

	public final long getLongCreateDate() {
        long ret = 0;
        
        if (createDate != null) {
            ret = createDate.getTime();
        }
        
        return ret;
    }
    public final void setLongCreateDate(long longCreateDate) {
        createDate = null;
        
        if (longCreateDate > 0) {
            createDate = new Timestamp(longCreateDate);
        }
    }
    public final boolean getDependent() {
        return dependent;
    }

    public final void setDependent(final boolean dependent) {
        this.dependent = dependent;
    }

    public final String getTaskId() {
        return taskId;
    }

    public final void setTaskId(final String taskId) {
        this.taskId = taskId;
    }

    public final String getUserName() {
        return userName;
    }

    public final void setUserName(final String userName) {
        this.userName = userName;
    }

    public final String getVersion() {
        return version;
    }

    public final void setVersion(final String version) {
        this.version = version;
    }

    public final Facility getFacility() {
        return facility;
    }

    public final void setFacility(Facility facility) {
        this.facility = facility;
    }

    public final String getFacilityId() {
        return facilityId;
    }

    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public final Integer getFpId() {
        return fpId;
    }

    public final void setFpId(Integer fpId) {
        this.fpId = fpId;
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
    	try {
            setTaskId(rs.getString("task_id"));
            setFpId(AbstractDAO.getInteger(rs, "FP_ID"));
            setDependentTaskId(rs.getString("dependent_task_id"));
            setTaskType(TaskType.valueOf(rs.getString("task_type")));
            setTaskName(rs.getString("task_name"));
            setTaskDescription(rs.getString("task_description"));
            setDependent(AbstractDAO.translateIndicatorToBoolean(rs.getString("dependent")));
            setCreateDate(rs.getTimestamp("create_date"));
            setUserName(rs.getString("user_name"));
            setVersion(rs.getString("version"));
            setFacilityId(rs.getString("facility_id"));
            setTaskInternalId(AbstractDAO.getInteger(rs, "internal_id"));
            setReferenceCount(AbstractDAO.getInteger(rs, "reference_count"));
            setCorePlaceId(AbstractDAO.getInteger(rs, "core_place_id"));
            setNonROSubmission(AbstractDAO.translateIndicatorToBoolean(rs.getString("non_ro_submission")));
            setDocumentId(AbstractDAO.getInteger(rs, "document_id"));
            setLastModified(AbstractDAO.getInteger(rs, "task_lm"));
    	} catch (SQLException sqle) {
            logger.debug(sqle.getMessage());
        } finally {
            newObject = false;
        }
    }

    public final List<Contact> getFacilityContacts() {
        return facilityContacts;
    }

    public final void setFacilityContacts(List<Contact> facilityContacts) {
        this.facilityContacts = facilityContacts;
    }

    public final Integer getReferenceCount() {
        return referenceCount;
    }

    public final void setReferenceCount(Integer referenceCount) {
        this.referenceCount = referenceCount;
    }

    public final String getDependentTaskId() {
        return dependentTaskId;
    }

    public final void setDependentTaskId(String dependentTaskId) {
        this.dependentTaskId = dependentTaskId;
    }

	public String getTaskTypeString() {
		if (taskType == null) {
			return null;
		}
		return taskType.toString();
	}

	public void setTaskTypeString(String taskTypeString) {
		if (taskTypeString != null) {
			taskType = Task.TaskType.valueOf(taskTypeString);
		}
	}
    public final Application getApplication() {
        return application;
    }
    public final void setApplication(Application application) {
        this.application = application;
    }
    public EmissionsReport getReport() {
        return report;
    }
    public void setReport(EmissionsReport report) {
        this.report = report;
    }
    public String getEmissionsReportOperation() {
        return emissionsReportOperation;
    }
    public char getEmissionsReportOperationAsChar() {
        char op = ' ';
        if (emissionsReportOperation != null && emissionsReportOperation.length() == 1) {
            op = emissionsReportOperation.charAt(0);
        }
        return op;
    }
    public void setEmissionsReportOperation(String emissionsReportOperation) {
        this.emissionsReportOperation = emissionsReportOperation;
    }
    public String getDependentTaskDescription() {
        return dependentTaskDescription;
    }
    public void setDependentTaskDescription(String dependentTaskDescription) {
        this.dependentTaskDescription = dependentTaskDescription;
    }
	public Integer getCorePlaceId() {
		return corePlaceId;
	}
	public void setCorePlaceId(Integer corePlaceId) {
		this.corePlaceId = corePlaceId;
	}
    public NtvReport getNtvReport() {
        return ntvReport;
    }
    public void setNtvReport(NtvReport ntvReport) {
        this.ntvReport = ntvReport;
    }
    public final List<Attachment> getAttachments() {
        return attachments;
    }
    public final void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
    public ComplianceReport getComplianceReport() {
        return complianceReport;
    }
    public void setComplianceReport(ComplianceReport complianceReport) {
        this.complianceReport = complianceReport;
    }
    public final StackTest getStackTest() {
        return stackTest;
    }
    public final void setStackTest(StackTest stackTest) {
        this.stackTest = stackTest;
    }
	public Integer getNewFpId() {
		return newFpId;
	}
	public void setNewFpId(Integer newFpId) {
		this.newFpId = newFpId;
	}
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // manually set transient date values since this does not appear to
        // work properly with persistence
        setLongCreateDate(this.longCreateDate);
    }
    public final String getSubmissionId() {
        return submissionId;
    }
    public final void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }
    public final String getGatewaySubmiterUserNm() {
        return gatewaySubmiterUserNm;
    }
    public final void setGatewaySubmiterUserNm(String gatewaySubmiterUserNm) {
        this.gatewaySubmiterUserNm = gatewaySubmiterUserNm;
    }
	public RelocateRequest getRelocateRequest() {
		return relocateRequest;
	}
	public void setRelocateRequest(RelocateRequest relocateRequest) {
		this.relocateRequest = relocateRequest;
	}
	public final boolean isNonROSubmission() {
		return nonROSubmission;
	}
	public final void setNonROSubmission(boolean nonROSubmission) {
		this.nonROSubmission = nonROSubmission;
	}
	public final Integer getDocumentId() {
		return documentId;
	}
	public final void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}
    
    public final us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment getAttestationDoc() {
		return attestationDoc;
	}
	public final void setAttestationDoc(us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment attestationDoc) {
		this.attestationDoc = attestationDoc;
	}
}
