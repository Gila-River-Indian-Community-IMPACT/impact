package us.oh.state.epa.stars2.workflow.engine;

/**
 * <p>
 * Title: WorkFlowCmdData
 * </p>
 * 
 * <p>
 * Description: This is a data object that presents information from the
 * workflow engine to an <code>AbstractWorkFlowCommand</code>. This allows
 * the command object to access orders, services, and even the workflow engine
 * itself.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 */
public class WorkFlowCmdData {
    private Integer externalId;
    private Integer serviceId;
    private Integer accountId;
    private Integer processId;
    private Integer activityId;
    private String processCd;
    private WorkFlowEngine engine;

    /**
     * Constructor. Once constructed, the object is immutable.
     * 
     * @param externalId
     *            Integer The order Id.
     * @param serviceId
     *            Integer The service Id.
     * @param accountId
     *            Integer The account Id.
     * @param processId
     *            Integer The process Id.
     * @param activityId
     *            Integer The activity Id.
     * @param processCd
     *            String The process code.
     * @param wfe
     *            WorkFlowEngine The workflow engine.
     */
    public WorkFlowCmdData(Integer externalId, Integer serviceId,
            Integer accountId, Integer processId, Integer activityId,
            String processCd, WorkFlowEngine wfe) {
        this.externalId = externalId;
        this.serviceId = serviceId;
        this.accountId = accountId;
        this.processId = processId;
        this.activityId = activityId;
        this.processCd = processCd;
        this.engine = wfe;
    }

    /**
     * Returns the Id for the customer order that is being worked on. Customer
     * order Ids are "external" to the workflow engine.
     * 
     * @return Integer Customer order Id.
     */
    public final Integer getExternalId() {
        return externalId;
    }

    /**
     * Returns the Id of the service being provisioned.
     * 
     * @return Integer Service Id.
     */
    public final Integer getServiceId() {
        return serviceId;
    }

    /**
     * Returns the Id of the account that the order is being provisioned for.
     * 
     * @return Integer Account Id.
     */
    public final Integer getAccountId() {
        return accountId;
    }

    /**
     * Returns the Id of the workflow process that is being executed. In the
     * workflow engine, this is the <code>ProcessFlow</code> Id.
     * 
     * @return Integer Process Id.
     */
    public final Integer getProcessId() {
        return processId;
    }

    /**
     * Returns the Id of the workflow Activity that is being executed. In the
     * workflow engine, this is the "ActivityTemplateId". To located the
     * specific Activity, use the process Id to find the ProcessFlow, and then
     * use this value to find the Activity within the process.
     * 
     * @return Integer Activity Id.
     */
    public final Integer getActivityId() {
        return activityId;
    }

    /**
     * Indicates the type of process being executed. This will correspond to a
     * value in the Process_Def table.
     * 
     * @return String Process code.
     */
    public final String getProcessCode() {
        return processCd;
    }

    /**
     * Returns a reference to the workflow engine itself. This allows the
     * <code>AbstractWorkFlowCommand</code> to directly call public methods on
     * the workflow engine.
     * 
     * @return WorkFlowEngine
     */
    public final WorkFlowEngine getEngine() {
        return engine;
    }
}
