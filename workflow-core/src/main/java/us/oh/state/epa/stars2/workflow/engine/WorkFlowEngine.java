package us.oh.state.epa.stars2.workflow.engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.framework.daemon.ManagedComponent;
import us.oh.state.epa.stars2.framework.exception.UnableToStartException;
import us.oh.state.epa.stars2.workflow.Activity;
import us.oh.state.epa.stars2.workflow.util.WorkFlowUtils;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

/**
 * <p>
 * Title: WorkFlowEngine
 * </p>
 * 
 * <p>
 * Description: The WorkFlowEngine is a stand-alone application responsible for
 * maintaining the current status of provisioning and sales force workflows.
 * Status is maintained both within the WorkFlowEngine and persisted to the
 * database as needed.
 * </p>
 * 
 * <p>
 * The web application (servlet) communicates with the WorkFlowEngine via the
 * WorkFlowManager class. Using the WorkFlowManager, the application can check
 * out an Activity (on behalf of a user), check in an activity, etc. Order entry
 * and Sales Proposals create new workflows by submitting orders and proposals
 * via the WorkFlowManager.
 * </p>
 * 
 * <p>
 * Operationally, the WorkFlowEngine runs as a Daemon process. There is also a
 * test tool, the WorkFlowRunner, that can be used to run the engine as a
 * stand-alone, non-Daemon process (useful for development and debugging).
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author S. Wooster, J. E. Collier
 */
@SuppressWarnings("serial")
public class WorkFlowEngine extends ManagedComponent implements Runnable
// implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler
{
	
	
	WorkFlowProcessor processor = 
			App.getApplicationContext().getBean(WorkFlowProcessor.class);
	
	
	
//	//TODO should consolidate the workflow context and the webapp context as
//	//much as possible
//	private ApplicationContext context =
//	    new ClassPathXmlApplicationContext(new String[] {"workflow-core-beans.xml"});
	
//	private InfrastructureService infrastructureBO = 
//			App.getApplicationContext().getBean(InfrastructureService.class);
	
    public static final Integer WORKFLOW_SYSTEM = new Integer(2);

    // The id identifying the Integer data type in the
    // Custom_Detail_Type_Def table
    public static final Integer INTEGER_CUSTOM_DETAIL_ID = new Integer(12);

    // These are the action strings supported by the workflow engine. These
    // strings will tell the "processCmd()" method the action that is being
    // requested.

    /**
     * Used to identify a service on an order or proposal that has no associated
     * activity template defined in the database.
     */
    public static final String NO_ACTIVITY_TEMPLATE = "6";

    public static final Integer NO_ACT_TEMPL_DEF_ID = new Integer(
            NO_ACTIVITY_TEMPLATE);

    /**
     * Checks in an Activity assigned to a user. Marks the activity as
     * "Completed" and triggers the next Activity in the workflow.
     */
    public static final String ACTION_CHECK_IN = "CheckIn";

    /**
     * Marks an Activity as assigned to a user and unavailable for assignment to
     * any other user.
     */
    public static final String ACTION_CHECK_OUT = "CheckOut";

    /**
     * Releases a previously checked out Activity. Unassigns the Activity and
     * places it back in the "Ready" state. This only applies to "Manual"
     * Activities.
     */
    public static final String ACTION_RELEASE = "Release";

    /**
     * Saves the data associated with an Activity that is currently checked out,
     * but leaves the Activity in the checked out state.
     */
    public static final String ACTION_SAVE = "Save";

    /**
     * Indicates that an order or proposal has been submitted and needs to be
     * decomposed into individual workflows for the services on the order or
     * proposal.
     */
    public static final String ACTION_COMPOSITE = "Composite";

    /**
     * Splits a service workflow off from its composite order and assigns it to
     * a seperate workflow.
     */
    public static final String ACTION_SPLIT = "Split";

    /**
     * Initializes a new workflow (ProcessFlow). Creates the ProcessFlow from
     * the templates. If the ProcessFlow has sub-flows, these are also created.
     * After creating the ProcessFlow, saves it to the database.
     */
    public static final String ACTION_INIT = "Init";

    /**
     * An "are you there?" request. Mostly used during development to insure
     * that socket setup is working correctly.
     */
    public static final String ACTION_PING = "Ping";

    /**
     * Returns a previously completed Activity to the "ready" state.
     */
    public static final String ACTION_UNDO = "Undo";

    /**
     * Loops back to an already completed activity
     */
    public static final String ACTION_REDO = "Redo";

    /**
     * Marks all activities from Redo activity forwards as not done to prepare
     * for looping backwards.
     */
    public static final String ACTION_LOOP = "Loop";

    /**
     * Refer the given Activity. Referring a task results in the current
     * activity being completed and essentially creating a new instance of the
     * current activity with its state marked as Referred.
     */
    public static final String ACTION_REFER = "Refer";

    /**
     * UnRefer the given Activity. UnRefer is simliar to a CheckIn action but
     * not the same. A the current Referred task is mark as complated and a new
     * task of the same type is created and is marked as IN_PROGRESS
     */
    public static final String ACTION_UNREFER = "UnRefer";

    /**
     * Reassigns a checked out Activity to a new user.
     */
    public static final String ACTION_REASSIGN = "ReAssign";

    /**
     * Retries to execute an automatic Activity that is currently blocked due to
     * a failure.
     */
    public static final String ACTION_RETRY = "Retry";

    /**
     * Terminates a service from the order it is currently assigned to.
     * Provisioning for this service is terminated and the service is removed
     * from the order. If this is the only service on the order, the order is
     * canceled.
     */
    public static final String ACTION_TERM_SVC = "Terminate";

    /**
     * Rolls back a ProcessFlow from the "Terminate Service Order" Activity to
     * whatever Activity got us into that state to begin with. In addition,
     * undoes any "skip" controls sents down other output branchs of the
     * Activity.
     */
    public static final String ACTION_ROLLBACK_TERM_SVC = "RollbackTerm";

    /**
     * External interface designation to cancel an order that is in the
     * provisioning process. If necessary, a cancelation order is created to
     * "undo" whatever degree of processing has been completed.
     */
    public static final String ACTION_CANCEL_ORDER = "CancelOrder";

    // Internal action. When we do a "rollback" on one Activity, we usually
    // need to send an "Undo Rollback" to the previous Activity to put it
    // in a "Ready" state for a later retry.

    public static final String ACTION_UNDO_ROLLBACK = "UndoRollback";

    // This is an internal action. When new things get created or checked
    // in, we create a "Ready" Controller to ready any downstream
    // activities or processes.

    public static final String ACTION_READY = "Ready";

    // This is another, internal action. If an Activity has multiple
    // output Transitions and these Transitions are tied to a DataField,
    // we may only take one Transition and skip any others. In that case,
    // we use a "Skip" Controller to skip all of the Activities along the
    // Transitions that were not taken.

    public static final String ACTION_SKIP = "Skip";

    // Internal action. If we are doing a rollback or undo on an Activity,
    // we also need to "unskip" any downstream Activities that we may have
    // skipped earlier. In that case, we use an "Unskip" Controller to
    // unskip them all.

    public static final String ACTION_UNSKIP = "Unskip";

    // Internal action. When activities or workflows are completed, they
    // typically need to examine their outbound Transitions to see what to
    // do next. Since that can be kind of complicated, we use a special
    // controller to handle it.

    public static final String ACTION_COMPLETE = "Complete";

    public static final int DEFAULT_PORT = 12000;

    public static final int DEFAULT_RANGE = 1000;

    public static final String ACTION_CHANGE_DUE_DATE = "ChangeDueDate";

    public static final String ACTION_ACTIVITY_DUE_DATE = "ChangeActDueDate";

    public static final String ACTION_CHANGE_START_DATE = "ChangeStartDate";

    public static final String ACTION_ACTIVITY_START_DATE = "ChangeActStartDate";
    
    public static final String ACTION_CANCEL_PROCESS = "CancelProcess";

    public static final String ACTION_CANCEL = "CancelActivity";

    protected static Logger logger = Logger.getLogger(WorkFlowEngine.class);

    public static final String ACTION_CHECK_IN_TO = "CheckInTo";

	public static final String ACTION_UPDATE_ACTIVITY_VIEWED_STATE = "UpdateActivityViewed";

	public static final String ACTION_SAVE_NOTE = "SaveNote";

	public static final String ACTION_MODIFY_NOTE = "ModifyNote";

	public static final String ACTION_CHANGE_END_DATE = "ChangeEndDate";

	public static final String ACTION_REMOVE_PROCESS_FLOWS = "RemoveProcessFlows";

	public static final String ACTION_CREATE_TASK_PROCESS_FLOWS = "CreateTaskProcFlows";

	public static final String ACTION_CLONE_PROCESS = "CloneProcess";

	public static final String ROLE_DISCRIMINATOR = "RoleDiscriminator";

	public static final String ASSIGNED_USER = "AssignedUser";

	public static final String DO_NOT_SKIP = "DO_NOT_SKIP";
	
	private final String TASK_USERID = "Task UserId";

    private static WorkFlowEngine instance = null;

    private ServerSocket socket = null;

    // ============================================================================
    // Daemon Methods
    // ============================================================================

    public boolean start(Properties initParameters, String instanceName)
            throws UnableToStartException {
        boolean ret = super.start(initParameters, instanceName);
        if (ret)
            super.setName("WorkFlowEngine-" + instanceName);
        return ret;
    }

    /**
     * Initializes the WorkFlowEngine. This actually does two major operations.
     * First, it determines a port to use for socket connections and publishes
     * this port and hostname to the database so that WorkFlowManager objects
     * running in this environment can find this engine. Second, it initializes
     * its own internal data structures from the database. This initialization
     * consists of all workflows that have not been completed yet.
     * 
     * @throws java.lang.UnableToStartException
     *             Database access error.
     */
    public boolean init() throws UnableToStartException {
        boolean ret = false;
        try {
        	
        	// decouple workflow ... do not call wf manager from wf engine
        	/**
        	 * 
            // We need to test if an engine is already running. We currently
            // don't support more than one engine running, so if the one listed
            // in the DB is active, i.e. responds to a "ping" message, than
            // we will exit and not start.
            WorkFlowManager tempManager = new WorkFlowManager();
            WorkFlowRequest tempRequest = new WorkFlowRequest();
            tempRequest.setType(WorkFlowManager.START_UP);
            WorkFlowResponse wfResponse = tempManager
                    .sendWorkFlowRequest(tempRequest, false);

            if ((wfResponse != null)
                    && (!wfResponse.getStatusCode().equalsIgnoreCase(
                            WorkFlowResponse.OK))) {
        	 */

            	logger.debug("Initializing Workflow Engine.");
                WorkFlowEngine.instance = this;

                // First, look for a Java property that identified the Host
                // name.
                // Note that Java has deprecated standard access to environment
                // variables so this technique is used to identify the name of
                // the
                // host. If we don't have a host name, then we are hosed.

                // decouple workflow
//                String hostName = InetAddress.getLocalHost().getHostName();
//
//                if (hostName == null) {
//                    hostName = "localhost";
//                    String errMsg = "Could not retrieve hostname from system. Please check and restart";
//
//                    System.err.println(errMsg);
//                    logger.error(errMsg);
//                }

                // Find the "app.WorkFlow" node in the configuration tree.

                ConfigManager manager = ConfigManagerFactory.configManager();
                Node node = manager.getNode("app");
                Node[] children = manager.getChildrenOf(node, "WorkFlow");
                Node child;
                int pRange = 1000;
                boolean first = false;

                // Lookup any pre-existing Daemon info in the database. If we
                // don't
                // find any, then this is the first time this engine has been
                // run
                // in this environment.

                // decouple workflow
//                dInfo = WorkFlowEngine.lookupDaemonInfo(hostName);

//                if (dInfo == null) {
//                    logger.debug("Initializing DB with WF socket info.");
//                    dInfo = new DaemonInfo();
//                    dInfo.setDaemonCode("WF");
//                    first = true;
//                }

//                int port = DEFAULT_PORT;
                // Look in the configuration tree to see if we can find a port
                // number
                // and range to use for our socket connection. Provide defaults
                // for
                // both just in case we don't find any.
//                if (children.length > 0) {
//                    child = children[0];
//                    port = child.getAsInt(BASE_PORT, DEFAULT_PORT);
//                    pRange = child.getAsInt(PORT_RANGE, DEFAULT_RANGE);
//                    tableSize = child.getAsInt(TABLE_SIZE, 500);
//                }

                // We are generating a random port some where between the base
                // port
                // number and the base_port + port_range. We will register the
                // port
                // and host used by this daemon in the database. This strategy
                // avoids port conflicts and allows us to move a daemon to
                // another
                // machine.

//                Random generator = new Random();
//                int r = generator.nextInt();
//                port = port + (r % pRange);
//                dInfo.setHostName(hostName);
//                dInfo.setPortNumber(new Integer(port));

                // Update the database with host name & port number.

//                updateDaemonInfo(dInfo, first);

                
                // decouple workflow ... get host:port form environment entries
                // create a convenience/utility method to make getting these
                //env entries easier.
                String hostName = "";
                Integer port = null;

    			String hostJndiName = Config.findNode("app.workflowEngine.host")
    					.getAsString("jndiName");
    			String portJndiName = Config.findNode("app.workflowEngine.port")
    					.getAsString("jndiName");
    			try {
    				Context initContext = new InitialContext();
    				Context envContext = (Context) initContext.lookup("java:/comp/env");
    				hostName = (String) envContext.lookup(hostJndiName);
    				port = (Integer) envContext.lookup(portJndiName);
    			} catch (NamingException ne) {
    				throw new RuntimeException(ne);
    			}
                
                logger.debug("Workflow Engine assigned host name = " + hostName);
                logger.debug("Workflow Engine assigned port number = "
                        + Integer.toString(port));

                // Read in all of the active work flows (or ProcessFlow
                // objects).
                // Stick them in the "_workFlows" Hashtable for easy access.

                // databaseInit();

                // Open a "listener" socket to our port number.

                socket = new ServerSocket(port);
                ret = true;
                
            	// decouple workflow ... do not call wf manager from wf engine
                /*
                 * 
            } else {
                logger
                        .debug("A workflow engine already appears to be running, this instance will exit.");
                ret = false;
            }
                 */
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UnableToStartException(e);
        }
        return ret;
    }

	
    /**
     * Waits for service requests from other applications.
     */
    public void run() {
        // _done = false ; // Flag to allow external termination of Daemon.

        // If the socket is null, we assume this instance shouldn't be run.
        try {
            if (socket != null) {
                Socket client = null;
                logger.debug("WorkFlowEngine starting...");

                while (!initShutdown()) {
                    try {
                        // Wait for somebody to write something to our socket.
                        // When
                        // we get something, set up an output connection to the
                        // writer and process the command.
                        logger.debug("Engine waitting for socket.");
                        client = socket.accept();
                        logger.debug("Socket connection accepted.");

                        new Thread(new ProcessRequest(client, this)).start();

                        /**
                         * in = client.getInputStream(); ois = new
                         * ObjectInputStream(in); out =
                         * client.getOutputStream(); oos = new
                         * ObjectOutputStream(out);
                         *  // The WorkFlowRequest tells us what to do. Funnel
                         * it off to // "processCmd()", which will do the
                         * request and give us the // response.
                         * 
                         * WorkFlowRequest req = (WorkFlowRequest)
                         * (ois.readObject()); logger.debug("WorkFlowRequest
                         * received: " + req.toString() + ".");
                         * 
                         * WorkFlowResponse wfr = processCmd(req);
                         * logger.debug("Response generated: " +
                         * wfr.toString());
                         * 
                         * oos.writeObject(wfr); oos.flush();
                         * logger.debug("Response written to socket.");
                         */

                    } catch (IOException ioe) {
                         logger.error(ioe.getMessage(), ioe);
                    }
                }
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioe) {
                    logger.error(ioe.getMessage(), ioe);
                }
                
                logger.debug("Socket connection closed.");
            }
            instance = null;
            logger.debug("WorkFlowEngine shutting down...");
            shutdownComplete();
        }
    }

    public synchronized void shutdown() {
        try {
            initShutdown = true;

            if (socket != null) {
                socket.close();
            }

            notify();
        } catch (IOException ioe) {
            logger.error(ioe);
        } catch (IllegalMonitorStateException imse) {
            logger.error(imse);
        }
    }

    /**
     * Reads current Daemon info, i.e., host name and port number, from the
     * database. For the WorkFlowEngine, the daemon code is "WF".
     * 
     * @param daemon_cd
     *            String that identifies the Daemon we are looking for.
     * @return DaemonInfo object containing host name and port number (if
     *         found).
     * 
     * @throws java.lang.Exception
     *             Database access error.
     */
//    static public DaemonInfo lookupDaemonInfo()
//        throws Exception {
//
//        InfrastructureService infrastructureBO 
//            = ServiceFactory.getInstance().getInfrastructureService();
//        DaemonInfo dInfo = infrastructureBO.retrieveDaemonInfo("WF");
//
//        return dInfo;
//    }
    
    /**
     * Reads current Daemon info, i.e., host name and port number, from the
     * database. For the WorkFlowEngine, the daemon code is "WF".
     *  
     * @param hostname
     * @return
     * @throws Exception
     */
//    static public DaemonInfo lookupDaemonInfo(String hostname)
//            throws Exception {
//
////            InfrastructureService infrastructureBO 
////                = ServiceFactory.getInstance().getInfrastructureService();
//    	InfrastructureService infrastructureBO = 
//    			App.getApplicationContext().getBean(InfrastructureService.class);
//        DaemonInfo dInfo = infrastructureBO.retrieveDaemonInfo("WF", hostname);
//
//        return dInfo;
//    }

    /**
     * Updates Daemon info in the database with the current host name and port
     * number of this application. If <code>first</code> is set to "true",
     * then the Daemon record needs to be created in the database. If
     * <code>first</code> is set to "false", then the Daemon record exists,
     * but needs to be updated with new information.
     * 
     * @param dInfo
     *            Object that identifies the daemon, host name, and port number.
     * @param first
     *            Set to "true" if this is the first time this application has
     *            been run against this database.
     * @throws ServiceFactoryException 
     * @throws RemoteException 
     */
//    public void updateDaemonInfo(DaemonInfo dInfo, boolean first) 
//            throws ServiceFactoryException, RemoteException{
//
//        if (first) {
//            infrastructureBO.createDaemonInfo(dInfo);
//        } else {
//            infrastructureBO.modifyDaemonInfo(dInfo);
//        }
//    }

    /**
     * Initializes the internal "_workflows" table with all workflows (or
     * ProcessFlow objects) from the database that have not been completed yet,
     * i.e., their "end date" is null. This serves as a working cache for
     * processing action commands.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     */
    /*
     * private void databaseInit() throws Exception {
     * System.out.println("databaseInit: start");
     * WorkFlowEngine.logger.debug("databaseInit: start"); // Retrieve all of the
     * ProcessFlows as an array. This will include // Activities, DataFields,
     * and Transitions. ProcessFlow[] activePfs =
     * workFlowBO.retrieveActiveProcessFlows();
     * 
     * WorkFlowEngine._workFlows = buildProcessMap(activePfs);
     * 
     * logger.debug("databaseInit: completed.");
     * System.out.println("databaseInit: completed.");
     * 
     * spawnRestartThreads(activePfs); }
     */
    /**
     * Examines the list of ProcessFlows, "pfs" to see if any of these have any
     * components that need to be restarted. If so, these are restarted.
     * Typically, components are stored in the database with a "restart" state.
     * When the workflow engine is initialized, thread objects need to be
     * created to perform these restarts.
     * 
     * @param pfs
     *            ProcessFlow[] List of ProcessFlows.
     */
    protected void spawnRestartThreads(ProcessFlow[] pfs) {
        // If there are no process flows, we don't have to restart anything.

        if ((pfs != null) && (pfs.length > 0)) {
            // Get the list of Activities for each process. Create threads to
            // ready any processes that are waiting for a ready and to restart
            // any Activities that are waiting for automatic retry.
            for (ProcessFlow pf : pfs) {
                spawnProcessThread(pf);
                Activity[] pfActs = pf.getActivities();
                spawnActivityThreads(pfActs);
            }
        }
    }

    /**
     * Checks "pf" to see if it is currently waiting on a "ready" date to
     * expire. If so and the date is past, readies "pf" for provisioning.
     * 
     * @param pf
     *            ProcessFlow
     */
    protected void spawnProcessThread(ProcessFlow pf) {
        Timestamp rdyDt = pf.getReadyDt();
        Timestamp startDt = pf.getStartDt();
        Timestamp endDt = pf.getEndDt();

        // The state of the process is deduced from its dates. If this
        // process is waiting for ready, then the ready date will be non-null,
        // and the start and end dates will both be null.

        if ((rdyDt == null) || (startDt != null) || (endDt != null)) {
            return;
        }

        Controller.logInfoMessage("Process eligible for readying, name/Id = ["
                + pf.getProcess().getProcessTemplateNm() + ", "
                + pf.getProcessId().toString() + "].");

        // This ProcessFlow is waiting to be readied. The only issues is:
        // can it be readied now or do we still need to wait a bit? What
        // we will do here is create a ProcessReadyController for the process
        // and let it deal with the situation. It will either ready the
        // process or create an "auto ready thread" to ready the process at
        // the appropriate time.

        IController cc = Controller.createController(
                WorkFlowEngine.ACTION_READY, pf);

        WorkFlowResponse resp = new WorkFlowResponse();
        cc.execute(resp, new WorkFlowRequest());

        if (resp.hasError() || resp.hasFailed()) {
            logger.error("Auto Ready of waiting process failed.");
        }
    }

    /**
     * Examines the list of Activity objects to see if any of them are in the
     * "waiting for automatic retry" state. If so, create the object that
     * handles the retry attempt.
     * 
     * @param activities
     *            Activity[] A list of Activities.
     */
    protected void spawnActivityThreads(Activity[] activities) {
        // If there are no Activities, then we have nothing to do.

        if ((activities != null) && (activities.length > 0)) {
            // For (each Activity) see if the Activity is currently waiting for
            // an automatic retry. If so, create the "retry" object.
            for (Activity act : activities) {
                String actStatus = act.getStatusCd();

                if (actStatus.equals(Activity.WAIT_FOR_RETRY)) {
                    StringBuffer msg = new StringBuffer(300);

                    msg.append("Creating restart thread for Activity name = ");
                    msg.append(act.getActivityName());
                    msg.append(", Process ID = ");
                    msg.append(act.getProcessId());
                    msg.append(", # of Remaining Retries = ");
                    msg.append(act.getNumberOfRetries());
                    msg.append(", Retry Interval = ");
                    msg.append(act.getRetryInterval());
                    msg.append(" minutes.");

                    logger.debug(msg.toString());

                    AutoActivityRetryThread aart = new AutoActivityRetryThread(
                            act);
                    Thread tt = new Thread(aart, "WorkFlowEngine-aart");
                    tt.setDaemon(true);
                    tt.start();
                }
            }
        }
    }

    /**
     * Verifies that the order associated with "order Id" is in the proper state
     * for cancellation, i.e., "In Process". If the order is in an improper
     * state, an exception is thrown. If the order state is correct, no
     * exception is thrown.
     * 
     * @param orderId
     *            Integer The Id of the order to be cancelled.
     * 
     * @throws Exception
     *             Order state is illegal for cancellation.
     */
    protected void validateForCancellation(Integer orderId) throws Exception {
        // OrderDAO orderDao = (OrderDAO)(DAOFactory.getDAO("OrderDAO")) ;
        // OrderHeader header = orderDao.retrieveOrderHeader
        // (orderId.intValue()) ;

        // String orderStatus = header.getOrderStatusCode() ;

        // if (!orderStatus.equals ("IP"))
        // {
        // throw new Exception ("Order Id = " + orderId.toString() + " is not "
        // +
        // "in a state that can be cancelled. Current " +
        // "state = [" + orderStatus + "].") ;
        // }
    }

    /**
     * Changes the status of the order associated with "orderId" to "cancelled".
     * 
     * @param orderId
     *            Integer The Id of the Order to be cancelled.
     * 
     * @throws Exception
     *             Database access error.
     */
    protected void cancelOrder(Integer orderId) throws Exception {
        // OrderDAO orderDao = (OrderDAO)(DAOFactory.getDAO("OrderDAO")) ;
        // OrderHeader header = orderDao.retrieveOrderHeader
        // (orderId.intValue()) ;

        // header.setOrderStatusCode ("CNC") ;
        // orderDao.modifyOrderHeader (header) ;
    }

    /**
     * Searches the "in memory" ProcessFlows to find the primary process flow
     * that is associated with "orderId". With sub-flows, an order can have
     * several different process flows, but only one of these is the primary
     * ProcessFlow for the order. This method finds the primary one. If no
     * ProcessFlow is found, then "null" is returned.
     * 
     * @param orderId
     *            Integer The Order Id.
     * 
     * @return ProcessFlow Primary ProcessFlow for this order.
    protected ProcessFlow findProcessForOrder(Integer orderId) {
        ProcessFlow ret = null;
        Integer pfOrderId;
        Integer pfParentId;

        // What we have to do here is iterate over the contents of the
        // workflows HashTable to see if we have a ProcessFlow for this
        // orderId. We need to lock the mapping table while we do this
        // search.
        synchronized (WorkFlowEngine._workFlows) {
            Enumeration<ProcessFlow> enumPf = WorkFlowEngine._workFlows.elements();

            while (enumPf.hasMoreElements()) {
                ProcessFlow pf = enumPf.nextElement();

                pfOrderId = pf.getExternalId();
                pfParentId = pf.getParentProcessId();

                // If the external Id of this ProcessFlow matches the one we
                // are looking for AND this process flow does not have a parent
                // (meaning it is not a sub-flow), then this is the guy we are
                // looking for.

                if ((pfOrderId.equals(orderId)) && (pfParentId == null)) {
                    ret = pf;
                }
            }
        }

        return ret;
    }
     */

    /**
     * Returns the singleton reference to the workflow engine. This should only
     * be visible within the workflow engine package.
     * 
     * @return WorkFlowEngine Singleton reference.
     */
    static WorkFlowEngine getInstance() {
        return WorkFlowEngine.instance;
    }

    /**
     * Creates a "cancel" order for "deadChild" if needed. A cancel order is
     * needed only if one or more Activities in the process have been completed
     * and one or more Activities have special "undo" Activities, i.e., an
     * Activity that needs to be executed to undo a previously completed
     * Activity. If a cancellation order is created, the order Id of that order
     * is returned. If no cancellation order is created, then "null" is
     * returned. Child or sub-flows of this process flow are also checked to see
     * if they need cancellation orders.
     * 
     * @param deadProcess
     *            Process that is being cancelled.
     * @param userId
     *            The Id of the user initiating the cancel request.
     * @param resp
     *            Response object being returned to application.
     * 
     * @return ArrayList Integer order Ids of cancellation orders that were
     *         created.
     * 
     * @throws Exception
     *             Database access error.
     */
    protected ArrayList<Integer> createCancelOrder(ProcessFlow deadProcess,
            Integer userId, WorkFlowResponse resp) throws Exception {
        logger.debug("Checking to cancel order Id = "
                + deadProcess.getExternalId().toString() + ", process Id = "
                + deadProcess.getProcessId() + ".");

        // ArrayList<Integer> cancelOrderIds = new ArrayList<Integer>();

        // CancellationBO cbo = new CancellationBO(userId);
        // ProcessFlow[] cancelPfs = cbo.getCancelProcesses();

        // If we didn't get any cancellation processes back, then we are
        // done.

        // if ((cancelPfs == null) || (cancelPfs.length == 0)) {
        logger.debug("No cancellation processes were needed.");
        return null;
        // }
        /*
         * logger.debug("Cancellation process created, process Id = " +
         * cancelPfs[0].getProcessId().toString()); // If we got cancellation
         * processes, add them to the active workflows // table. Then, get the
         * order Id from the first process and return // / that to caller.
         * 
         * addProcessFlowsToMap(cancelPfs);
         * 
         * logger.debug("Sending ACTION_READY control to cancellation process.");
         * 
         * IController ic = WorkFlowEngine.createController(
         * WorkFlowEngine.ACTION_READY, cancelPfs[0]); ic.execute(resp, new
         * WorkFlowRequest());
         * 
         * int i; for (i = 0; i < cancelPfs.length; i++) { Integer cancOrderId =
         * cancelPfs[i].getExternalId();
         * 
         * if ((cancOrderId != null) && (!cancelOrderIds.contains(cancOrderId))) {
         * logger.debug("Adding Cancellation Order Id = " +
         * cancOrderId.toString() + " to output array.");
         * 
         * cancelOrderIds.add(cancOrderId); } }
         * 
         * return cancelOrderIds;
         */
    }

    /**
     * Searches "activities" to find completed Activities, i.e., Activities with
     * non-null end dates. Returns an array containing all completed Activities
     * order by end date where the most recently completed Activity will be the
     * first element in the array.
     * 
     * @param activities
     *            Array of activities for a ProcessFlow.
     * 
     * @return Activity[] Array of activities that are completed.
     */
    protected Activity[] findCompletedActivities(Activity[] activities) {
        // If we have no Activities, we have no completed Activities.
        ArrayList<Activity> completed = new ArrayList<Activity>();

        if ((activities != null) && (activities.length > 0)) {
            // We have activities. See if any of them are completed.

            ProcessActivity pa;
            // For (each Activity) see if it is completed. If so, add it to our
            // array of completed Activities.
            for (Activity a : activities) {
                pa = a.getProcessActivity();
                Timestamp endDt = pa.getEndDt();

                if (endDt != null) {
                    addToCompletedList(a, completed);
                }
            }
        }
        return completed.toArray(new Activity[0]);
    }

    /**
     * Adds completed Activity "a" to the list of completed Activities. The list
     * is sorted by Activity end date such that the Activity that was completed
     * first is at the end of the list and the Activity that was completed last
     * is at the beginning of the list.
     * 
     * @param a
     *            A completed Activity.
     * @param completed
     *            An ArrayList of other completed Activities.
     */
    static protected void addToCompletedList(Activity a,
            ArrayList<Activity> completed) {
        Timestamp endDt = a.getEndDt();
        int i = 0;

        // For (each previously completed Activity) compare the end date for
        // that activity to the end date for "a".
        for (Activity srch : completed) {
            // If "a" was completed after "srch", insert "a" in front of
            // "srch" and return.
            if (endDt.compareTo(srch.getEndDt()) > 0) {
                completed.add(i++, a);
                return;
            }
        }
        completed.add(a);
    }

    /**
     * Builds a ProcessFlow mapping. This consists of a <tt>Hashtable</tt>
     * that where the key is the "processId" (which is unique for each
     * ProcessFlow) and the value is the ProcessFlow. In addition, parent-child
     * (or container-subflow) relationships are extracted and linked where
     * needed.
     * 
     * @param processes
     *            An array of ProcessFlow objects.
     * 
     * @return Hashtable A mapping of Integer process Ids to ProcessFlows.
     */
    protected Hashtable<Integer, ProcessFlow> buildProcessMap(
            ProcessFlow[] processes) {
        Hashtable<Integer, ProcessFlow> processMap = new Hashtable<Integer, ProcessFlow>();

        // If we don't have any ProcessFlows, then the Hashtable is already
        // done.
        if ((processes != null) && (processes.length > 0)) {
            // The first step is to put all of the ProcessFlows in the
            // Hashtable.
            // This makes it easier to look them up to set the parent-child
            // links.
            for (ProcessFlow p : processes) {
                processMap.put(p.getProcessId(), p);
            }

            // Look through all the current workFlows for subFlows in order to
            // build links from Parent Activities to the SubFlows and from
            // the subFlows to the parent Activities.
            ProcessFlow parentProcess;
            Activity parentAct;

            for (ProcessFlow p : processMap.values()) {
                Integer parentProcessId = p.getParentProcessId();

                // If we have a non-null parent process Id, find the parent
                // process in the Hashtable.

                if (parentProcessId != null) {
                    // This looks odd, but is correct. The
                    // "activity_template_id"
                    // stored in the "Process" table is the
                    // "activity_template_id"
                    // of the parent Activity (the column should probably be
                    // renamed "parent_activity_template_id". The key on the
                    // "Process_Activity table is (process_id,
                    // activity_template_id).
                    // So, we need BOTH values to locate the parent Activity.
                    Integer parentActivityId = p.getParentActivityTemplateId();

                    if (parentActivityId == null) {
                        // This is probably an error and should throw an
                        // Exception.
                        continue;
                    }

                    // Find the parent ProcessFlow in the process map and then
                    // find
                    // the parent Activity in the parent process flow.
                    parentProcess = processMap.get(parentProcessId);
                    parentAct = parentProcess.getActivity(parentActivityId);

                    // Hook the current ProcessFlow to its parent Activity.
                    parentAct.setSubFlow(p);

                    // Hook the parent ProcessFlow and parent Activity to the
                    // current ProcessFlow.
                    p.setParentActivity(parentAct);
                    p.setParentProcess(parentProcess);
                }
            }
        } else {
            logger.debug("buildProcessMap: completed, no ProcessFlows found.");
        }

        return processMap;
    }

    /**
     * Adds each of the ProcessFlow objects in "flows" to the in-memory process
     * map. The in-memory process map represents workflow processes that have
     * not been completed yet. Note that no assumption is made about ProcessFlow
     * state, i.e., whether the process is active, completed, or even dead.
     * 
     * @param flows
     *            An array of ProcessFlow objects.
    protected void addProcessFlowsToMap(ProcessFlow[] flows) {
        for (ProcessFlow pf : flows) {
            addProcessFlowToMap(pf);
        }
    }
     */
    
//    private void addProcessFlowToMap(ProcessFlow pf) {
//        WorkFlowEngine._workFlows.put(pf.getProcessId(), pf);
//        // Keep the new process id in the list and if the size is over,
//        // remove the last process id from list and table
//        processIdList.add(pf.getProcessId());
//        if (processIdList.size() > tableSize) {
//            Integer tId = processIdList.removeLast();
//            WorkFlowEngine._workFlows.remove(tId);
//        }
//    }

    // ===========================================================================
    // COMMAND PROCESSING. All input commands from WorkFlowManager objects are
    // handled by the "processCmd()" method. This method
    // parses the input object to determine what command is being done and then
    // routes the command to the appropriate command handler.
    // ============================================================================

    /**
     * Handler for commands from external applications (via a "WorkFlowManager"
     * object). Conceptually, the "command" string consists of a set of
     * "name=value" pairs separated by commas. The first name-value pair is
     * expected to be an action identifier. The remaining name-value pairs
     * contain action-specific information. Typical command strings look like:
     * 
     * Action=CheckIn,Id=109000,ExtId=1000,ActId=1,Data=name:value, ...
     * 
     * where "..." represents more "Data=name:value" pairs.
     * 
     * Also:
     * 
     * Action=Composite,ExtId=213001,Service=0:1,Service=1:2, ...
     * 
     * where "Service=serviceId,activityTemplateDefId".
     * 
     * 
     * @param command
     *            The command to be executed.
     * 
     * @return WorkFlowResponse The result of the command execution.
     */
    public WorkFlowResponse processCmd(WorkFlowRequest command) {
    	
    	return processor.processCmd(command);
    	
//        WorkFlowResponse resp = new WorkFlowResponse();
//
//        String cmdStr = command.toString();
//        logger.debug(cmdStr);
//        String action = command.getAction();
//
//        // Get the "Action" string from the command. Figure out who wants
//        // the command next and give it to them.
//
//        try {
//            if (action.equals(WorkFlowEngine.ACTION_PING)) {
//                resp = new WorkFlowResponse();
//            } else if (action.equals(WorkFlowEngine.ACTION_COMPOSITE)) {
//                resp = createComposite(command);
//            } else if (action.equals(WorkFlowEngine.ACTION_INIT)) {
//                resp = createProcess(command);
//            } else if (action.equals(WorkFlowEngine.ACTION_CREATE_TASK_PROCESS_FLOWS)) {
//                resp = createTaskProcessFlows(command);
//            } else {
//
//                Integer processId = command.getProcessId();
//                validate(processId, "Process Id");
//                ProcessFlow process = findProcessFlow(processId);
//                long ct = Thread.currentThread().getId();
//
//                if (process.isLocked(ct)) {
//                    logger.debug("Process is locked. " + processId);
//                    resp.addError("Process is being worked by others.");
//                    return resp;
//                }
//
//                try {
//                    if (action.equals(WorkFlowEngine.ACTION_CHECK_OUT)) {
//                        resp = checkOut(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_RELEASE)) {
//                        resp = release(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_SAVE)) {
//                        resp = saveState(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_CHECK_IN)) {
//                        resp = checkIn(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_CHECK_IN_TO)) {
//                        resp = checkInTo(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_REASSIGN)) {
//                        resp = reassign(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_UNDO)) {
//                        resp = undo(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_REDO)) {
//                        resp = redo(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_REFER)) {
//                        resp = refer(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_UNREFER)) {
//                        resp = unRefer(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_RETRY)) {
//                        resp = retry(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_SPLIT)) {
//                        resp = splitProcess(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_ROLLBACK_TERM_SVC)) {
//                        resp = rollbackTerminateService(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_SKIP)) {
//                        resp = skip(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_CANCEL_PROCESS)) {
//                        resp = cancelProcess(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_UPDATE_ACTIVITY_VIEWED_STATE)) {
//                        resp = updateActivityViewedState(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_SAVE_NOTE)) {
//                        resp = saveNote(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_MODIFY_NOTE)) {
//                        resp = modifyNote(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_CHANGE_END_DATE)) {
//                        resp = changeEndDate(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_REMOVE_PROCESS_FLOWS)) {
//                        resp = removeProcessFlows(command, resp, process);
//                    } else if (action.equals(WorkFlowEngine.ACTION_CLONE_PROCESS)) {
//                        resp = cloneProcess(command, resp, process);
//                    } else {
//                        resp = executeCommand(command, resp, process);
//                    }
//
//                } finally {
//                    process.unLock(ct);
//                }
//            }
//        } catch (Exception e) {
//            logger.error(command.toString(), e);
//            resp.addError(e.getMessage());
//        }
//
//        return resp; // Return Workflow response
    }

//    private WorkFlowResponse cloneProcess(WorkFlowRequest rqst,
//			WorkFlowResponse resp, ProcessFlow process) throws Exception {
//        if (rqst != null) {
//            validate(rqst.getProcessId(), "Process Id");
//            validate(rqst.getUserId(), "User Id");
//
//            String cmd = rqst.getAction();
//            IController c = WorkFlowEngine.createController(cmd, process);
//
//            c.execute(resp, rqst);
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//        return resp;
//	}

//	private WorkFlowResponse createTaskProcessFlows(WorkFlowRequest command) throws Exception {
//		WorkFlowResponse ret = new WorkFlowResponse();
//        if (command != null) {
//            validate(command.getUserId(), "User Id");
//            validate(command.getWorkFlow(), "Workflow");
//            
//            //String cmd = rqst.getAction();
//            //IController c = WorkFlowEngine.createController(cmd, process);
//            String roleDiscrim = extractRoleDiscrim(command);
//            
//            HashMap<String,String> data = new HashMap<String,String>();
//            String taskUserId = null;
//            
//        	for (int i = 0; i < command.getDataCount(); i++) {
//        		String name = command.getDataName(i);
//        		if (TASK_USERID.equals(name)) {
//        			taskUserId = command.getDataValue(i);
//        		} else {
//        			data.put(name,command.getDataValue(i));
//        		}
//        	}
//        	
//        	WriteWorkFlowService wfBO = 
//        			App.getApplicationContext().getBean(WriteWorkFlowService.class);
//        	
//            try {
//            	// creating task process flows usually expects 0 for workflow id
////            	Integer workflowId = new Integer(0);
////            	if(command.getWorkFlow() != null) {
////            		workflowId = command.getWorkFlow();
////            	}
//				wfBO.createTaskProcessFlows(command.getWorkFlow(), command.getAccountId(), 
//						command.getOrderId(), command.getExpedite(), command.getStartDt(), 
//						null, command.getUserId(), data, Integer.parseInt(taskUserId), roleDiscrim);
//			} catch (Exception e) {
//                Controller.logger.error(e.getMessage(), e);
//                ret.addError(e.getMessage());
//			}
//            
//            //c.execute(resp, rqst);
//        } else {
//            logger.error("Request was null.");
//            ret.addError("Request was null.");
//        }
//        return ret;
//	}

//	private WorkFlowResponse removeProcessFlows(WorkFlowRequest rqst,
//			WorkFlowResponse resp, ProcessFlow process) throws Exception {
//        if (rqst != null) {
//            validate(rqst.getProcessId(), "Process Id");
//            validate(rqst.getUserId(), "User Id");
//
//            String cmd = rqst.getAction();
//            IController c = WorkFlowEngine.createController(cmd, process);
//
//            c.execute(resp, rqst);
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//        return resp;
//	}

//	private WorkFlowResponse changeEndDate(WorkFlowRequest rqst,
//			WorkFlowResponse resp, ProcessFlow process) throws Exception {
//        // Extract the input parameters we need for check out and validate
//        // them.
//        if (rqst != null) {
//            validate(rqst.getActivityId(), "Activity Id");
//            validate(rqst.getUserId(), "User Id");
//
//            // See if we can find the Activity in the active workflows. If not,
//            // then something is wrong and an Exception is thrown.
//
//            Activity a = process.getActivity(rqst.getActivityId());
//
//            a.setUserId(rqst.getUserId());
//            String cmd = rqst.getAction();
//
//            IController c = WorkFlowEngine.createController(cmd, a);
//
//            c.execute(resp, rqst);
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//
//        return resp;
//	}

//	private WorkFlowResponse modifyNote(WorkFlowRequest rqst,
//			WorkFlowResponse resp, ProcessFlow process) throws Exception  {
//        if (rqst != null) {
//            validate(rqst.getProcessId(), "Process Id");
//            validate(rqst.getUserId(), "User Id");
//
//            String cmd = rqst.getAction();
//            IController c = WorkFlowEngine.createController(cmd, process);
//
//            c.execute(resp, rqst);
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//        return resp;
//	}

//	private WorkFlowResponse saveNote(WorkFlowRequest rqst,
//			WorkFlowResponse resp, ProcessFlow process) throws Exception  {
//        if (rqst != null) {
//            validate(rqst.getProcessId(), "Process Id");
//            validate(rqst.getUserId(), "User Id");
//
//            String cmd = rqst.getAction();
//            IController c = WorkFlowEngine.createController(cmd, process);
//
//            c.execute(resp, rqst);
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//        return resp;
//	}

//	private WorkFlowResponse updateActivityViewedState(
//			WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {
//        if (rqst != null) {
//            validate(rqst.getProcessId(), "Process Id");
//            validate(rqst.getActivityId(), "Activity Id");
//            
//            Activity a = process.getActivity(rqst.getActivityId());
//
//            a.setUserId(rqst.getUserId());
//            String cmd = rqst.getAction();
//
//            IController c = WorkFlowEngine.createController(cmd, a);
//
//            c.execute(resp, rqst);
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//
//        return resp;
//	}

	/**
     * Find the command with controller and execute it.
     * Works for following actions: 
     * WorkFlowEngine.ACTION_CHANGE_DUE_DATE
     * WorkFlowEngine.ACTION_CHANGE_START_DATE
     * 
     * @param rqst
     * @param resp
     * @param process
     * @return
     * @throws Exception
     */
//    private WorkFlowResponse executeCommand(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {
//
//        if (rqst != null) {
//        	logger.debug(" Creating controller for: " + rqst.getAction());
//            IController c = WorkFlowEngine.createController(rqst.getAction(),
//                    process);
//
//            c.execute(resp, rqst);
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//
//        return resp;
//    }

    /**
     * Performs a check-out operation. Check-out assigns an Activity to a user.
     * The user is responsible for performing the Activity, entering any data
     * associated with the Activity, and then checking the Activity back in when
     * completed. While the user has the Activity checked out, no other user can
     * operate on the Activity. Exceptions are thrown for a variety of
     * conditions, such as invalid input parameters, could not find Activity,
     * invalid state for check-out, etc.
     * 
     * @param rqst
     *            WorkFlowRequest containing check-out parameters.
     * @param process 
     * @param resp 
     * 
     * @return WorkFlowResponse The results of the check out request.
     * 
     * @throws java.lang.Exception
     *             Something is seriously wrong.
     */
    public WorkFlowResponse checkOut(WorkFlowRequest rqst, WorkFlowResponse resp, 
            ProcessFlow process) throws Exception {
    	
    	return processor.checkOut(rqst,resp,process);
    	
//        // Extract the input parameters we need for check out and validate
//        // them.
//        if (rqst != null) {
//            validate(rqst.getActivityId(), "Activity Id");
//            validate(rqst.getUserId(), "User Id");
//
//            // See if we can find the Activity in the active workflows. If not,
//            // then something is wrong and an Exception is thrown.
//
//            Activity a = process.getActivity(rqst.getActivityId());
//
//            // See if the Activity is ready for check-out. If somebody else
//            // beat us to it, then just return a failure response. If the
//            // Activity state is completely wrong for check-out, an Exception
//            // will be thrown.
//            if (validForCheckOut(a)) {
//                // Create a controller object to handle the check-out. If there
//                // are "side effects" or down-stream consequences, the
//                // Controller
//                // will take care of propagating those for us.
//
//                a.setUserId(rqst.getUserId());
//                String cmd = rqst.getAction();
//
//                IController c = WorkFlowEngine.createController(cmd, a);
//
//                c.execute(resp, rqst);
//            } else {
//                logger.error("This Activity has already been checked out.");
//                resp.addError("This Activity has already been checked out.");
//            }
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//
//        return resp;
    }



    /**
     * Releases a checked-out Activity. The Activity must currently be checked
     * out to this user in order to be released. Exceptions are thrown for a
     * variety of conditions, such as invalid input parameters, could not find
     * Activity, invalid state for release, etc.
     * 
     * @param rqst
     *            WorkFlowRequest containing check-out parameters.
     * @param process 
     * @param resp 
     * 
     * @return WorkFlowResponse The results of the check out request.
     * 
     * @throws java.lang.Exception
     *             Something is seriously wrong.
     */
    public WorkFlowResponse release(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

    	return processor.release(rqst,resp,process);
    	
        // Extract the input parameters we need for check out and validate
        // them.
//        if (rqst != null) {
//            validate(rqst.getActivityId(), "Activity Id");
//            validate(rqst.getUserId(), "User Id");
//
//            // See if we can find the Activity in the active workflows. If not,
//            // then something is wrong and an Exception is thrown.
//
//            Activity a = process.getActivity(rqst.getActivityId());
//
//            // See if the Activity is checked-out to this user. If not, then
//            // the request is illegal so just return an error response.
//
//            if (validForRelease(a, rqst.getUserId())) {
//                // Set the Activity state and then create a controller object to
//                // handle the database update.
//                a.setStatusCd(Activity.PENDING);
//                a.setStartDt(null);
//                a.setUserId(null);
//                a.setDueDt(null);
//                a.setJeopardyDt(null);
//
//                String cmd = rqst.getAction();
//
//                IController c = WorkFlowEngine.createController(cmd, a);
//
//                c.execute(resp, rqst);
//            } else {
//                String errMsg = "Invalid Activity state for release, status = ["
//                        + a.getStatusCd()
//                        + "], Request User Id = ["
//                        + rqst.getUserId().toString() 
//                        + "], Assigned User Id = ["
//                        + a.getUserId() + "].";
//
//                logger.debug(errMsg);
//                resp.addError(errMsg);
//            }
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//
//        return resp;
    }

    /**
     * Changes an Activity from "checked out" back to the "ready" state. This is
     * similar to the "release" action, except "undo" is for administrators who
     * have permission to "undo" any check-out by any user.
     * 
     * @param rqst
     *            WorkFlowRequest containing undo parameters.
     * @param process 
     * @param resp 
     * 
     * @return WorkFlowResponse containing results of the "undo" request.
     * 
     * @throws java.lang.Exception
     *             State error or database access error.
     */
    public WorkFlowResponse undo(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

    	
    	return processor.undo(rqst, resp, process);
    	
//        // Extract the input parameters we need for check out and validate
//        // them.
//        if (rqst != null) {
//            validate(rqst.getActivityId(), "Activity Id");
//
//            // See if we can find the Activity in the active workflows. If not,
//            // then something is wrong and an Exception is thrown.
//            Activity a = process.getActivity(rqst.getActivityId());
//
//            // All we want to know is whether or not this Activity is checked
//            // out. If it is, we can undo it. If not, don't touch it.
//            if (isCheckedOut(a)) {
//                // Create a controller to handle the database update.
//                String cmd = rqst.getAction();
//                IController c = WorkFlowEngine.createController(cmd, a);
//                c.execute(resp, rqst);
//            } else {
//                StringBuffer msg = new StringBuffer(300);
//                msg.append("This Activity is not valid for undo, ");
//                msg.append("status = [");
//                msg.append(a.getStatusCd());
//                msg.append("].  Only can undo In process status");
//
//                String errMsg = msg.toString();
//
//                logger.debug(errMsg);
//                resp.addError(errMsg);
//            }
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//
//        return resp;
    }

    /**
     * Support for a generic operation to be able to repeat / redo previously
     * completed activites.
     * 
     * @param rqst
     *            WorkFlowRequest containing undo parameters.
     * @param ret 
     * @param process 
     * 
     * @return WorkFlowResponse containing results of the "undo" request.
     * 
     * @throws java.lang.Exception
     *             State error or database access error.
     */
//    private WorkFlowResponse redo(WorkFlowRequest rqst, WorkFlowResponse ret, 
//            ProcessFlow process) throws Exception {
//
//        // Extract the input parameters we need for check out and validate
//        // them.
//        if (rqst != null) {
//            validate(rqst.getActivityId(), "Activity Id");
//
//            Integer activityId = rqst.getActivityId();
//
//            Activity a = process.getActivity(activityId);
//
//            if (Activity.IN_PROCESS.equals(a.getStatusCd())) {
//                logger.debug("Process is being looped. " + process.getProcessId());
//                ret.addError("Process is being looped.");
//                return ret;
//            }
//            IController c = null;
//
//            String cmd = rqst.getAction();
//            c = WorkFlowEngine.createController(cmd, a);
//            c.execute(ret, rqst);
//
//        } else {
//            logger.error("Request was null.");
//            ret.addError("Request was null.");
//        }
//
//        return ret;
//    }

    /**
     * Support for a generic operation to be able to refer an IN_PROGRESS
     * Activity typically to an outside contact.
     * 
     * @param rqst
     *            WorkFlowRequest containing undo parameters.
     * @param process 
     * @param resp 
     * 
     * @return WorkFlowResponse containing results of the "refer" request.
     * 
     * @throws java.lang.Exception
     *             State error or database access error.
     */
    public WorkFlowResponse refer(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

    	
    	return processor.refer(rqst, resp, process);
    	
//        // Extract the input parameters we need for check out and validate
//        // them.
//        if (rqst != null) {
//            validate(rqst.getProcessId(), "Process Id");
//            validate(rqst.getActivityId(), "Activity Id");
//            validate(rqst.getUserId(), "User Id");
//
//            // See if we can find the Activity in the active workflows. If not,
//            // then something is wrong and an Exception is thrown.
//
//            Activity a = process.getActivity(rqst.getActivityId());
//
//            // See if the Activity is valid for check in. If the
//            // Activity state is completely wrong for saving, an Exception
//            // will be thrown.
//
//            if (validForCheckIn(a, rqst.getUserId())) {
//                HashMap<String, String> dataValues = extractDataValues(rqst);
//
//                // Create a controller object to handle the check in. If there
//                // are "side effects" or down-stream consequences (which there
//                // will
//                // not be for a "check in"), the Controller will take care of
//                // propagating those for us.
//                String cmd = rqst.getAction();
//
//                IController c = WorkFlowEngine.createController(cmd, a);
//
//                c.execute(resp, rqst, dataValues);
//            } else {
//                StringBuffer msg = new StringBuffer(500);
//                msg.append("This Activity is not valid for referring, status = [");
//                msg.append(a.getStatusCd());
//                msg.append("], Requesting user Id = [");
//                msg.append(rqst.getUserId().toString());
//                msg.append("], assigned user Id = [");
//
//                Integer assigned = a.getUserId();
//
//                if (assigned == null) {
//                    msg.append(" Not Assigned ");
//                } else {
//                    msg.append(assigned.toString());
//                }
//                msg.append("].");
//
//                String errMsg = msg.toString();
//                logger.debug(errMsg);
//                resp.addError(errMsg);
//            }
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//
//        return resp;
    }

    /**
     * Support for a generic operation to terminate an activity that is in the
     * referred state.
     * 
     * @param rqst
     *            WorkFlowRequest containing undo parameters.
     * @param process 
     * @param resp 
     * 
     * @return WorkFlowResponse containing results of the "unRefer" request.
     * 
     * @throws java.lang.Exception
     *             State error or database access error.
     */
    public WorkFlowResponse unRefer(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

    	
    	return processor.unRefer(rqst, resp, process);
    	
//        // Extract the input parameters we need for check out and validate
//        // them.
//        if (rqst != null) {
//            validate(rqst.getActivityId(), "Activity Id");
//            validate(rqst.getUserId(), "User Id");
//            validate(rqst.getLoopCnt(), "Loop Cnt");
//
//            // See if we can find the Activity in the active workflows. If not,
//            // then something is wrong and an Exception is thrown.
//            Activity a = process.getActivity(rqst.getActivityId());
//
//            HashMap<String, String> dataValues = extractDataValues(rqst);
//
//            // Create a controller object to handle the check in. If there
//            // are "side effects" or down-stream consequences (which there will
//            // not be for a "check in"), the Controller will take care of
//            // propagating those for us.
//            String cmd = rqst.getAction();
//
//            IController c = WorkFlowEngine.createController(cmd, a);
//
//            //
//            // There is a possibility that we already came out of the referred
//            // state manually. If this is the case there is nothing to do.
//            //
//            if (a.getLoopCnt().equals(rqst.getLoopCnt())) {
//                c.execute(resp, rqst, dataValues);
//            }
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//
//        return resp;
    }

    public WorkFlowResponse cancelProcess(WorkFlowRequest command, WorkFlowResponse resp, ProcessFlow process)
            throws Exception {
    	
    	return processor.cancelProcess(command, resp, process);
    	
//        logger.debug("Cancel Process request received: " + command);
//
//        WorkFlowResponse ret = new WorkFlowResponse();
//
//        // Extract the order Id and user Id and insure we have legitimate
//        // values.
//        if (command != null) {
//            validate(command.getUserId(), "User Id");
//
//            // Find the "top level" ProcessFlow for this order. If we don't find
//            // one, something is wrong.
//
//                IController c = WorkFlowEngine.createController(command
//                        .getAction(), process);
//
//                c.execute(ret, command);
//
//        } else {
//            logger.error("Request was null.");
//            ret.addError("Request was null.");
//        }
//
//        return ret;
    }

    /**
     * Creates a "composite" in response to submittal of an order or a proposal
     * to the workflow engine. A "composite" consists of a custom
     * ProcessTemplate created for the order/proposal. Each service on the
     * order/proposal becomes an ActivityTemplate in the ProcessTemplate.
     * Finally, ProcessFlow objects are created for the order/proposal, and each
     * service on the order/proposal. All processes are "readied" for
     * provisioning or sales flow processing.
     * 
     * @param command
     *            The command object from the workflow manager.
     * 
     * @return A response to be returned to the workflow manager.
     * 
     * @throws java.lang.Exception
     *             Validation or database access error.
     */
    public WorkFlowResponse createComposite(WorkFlowRequest command)
            throws Exception {
    	
    	return processor.createComposite(command);
    	
//        WorkFlowResponse ret = new WorkFlowResponse();
//
//        // Validate our input parameters before we do anything else.
//        if (command != null) {
//            validate(command.getOrderId(), "External Id");
//            validate(command.getUserId(), "User Id");
//            validate(command.getAccountId(), "accountId");
//            WorkFlowUtils.validateType(command.getType(), "WorkFlow Type");
//
//            ProcessTemplate pt = null;
//            ProcessFlow[] processes = null;
//            IController c = null;
//            
//            String roleDiscrim = extractRoleDiscrim(command);
//
//            // Extract the service mapping from the command object and translate
//            // it into something we can hand to the workflow business object.
//            ServiceTemplateMap[] svcMap = createServiceMap(command);
//
//            // Wrap the database operations up in a "try/catch" block. If these
//            // fail, we are seriously hosed.
//            try {
////              WriteWorkFlowService workFlowBO = ServiceFactory.getInstance().getWriteWorkFlowService();
//              WriteWorkFlowService workFlowBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
//                pt = workFlowBO.createCompositeTemplate(command.getType(),
//                        command.getOrderId(), svcMap, false);
//
//                // If we could not create the process template, something went
//                // wrong.
//
//                if (pt == null) {
//                    throw new Exception("Process Template creation failed; "
//                            + "Template type = [" + command.getType() + "], "
//                            + "External Id = ["
//                            + command.getOrderId().toString() + "].");
//                }
//
//                Integer ptid = pt.getProcessTemplateId();
//                processes = workFlowBO.createProcessFlows(ptid, command
//                        .getAccountId(), command.getOrderId(), command
//                        .getExpedite(), command.getStartDt(), command
//                        .getDueDt(), command.getUserId(), roleDiscrim);
//            } catch (Exception e) {
//                logger.error(this, e);
//                e.printStackTrace(System.out);
//
//                String errMsg = e.getMessage();
//
//                if ((errMsg == null) || (errMsg.length() == 0)) {
//                    errMsg = e.getClass().getName() + " has been detected.";
//                }
//
//                ret.addError(errMsg);
//                ret.setRollbackNeeded(true);
//            }
//
//            // If we get here, then we successfully created our template and
//            // our ProcessFlows. Add the ProcessFlows to the map of active
//            // ProcessFlows. Then, create "Ready" controller for the first
//            // ProcessFlow and have it start up the process.
//            if (!ret.getRollbackNeeded()) {
////                addProcessFlowsToMap(processes);
//                c = WorkFlowEngine.createController(
//                        WorkFlowEngine.ACTION_READY, processes[0]);
//                c.execute(ret, command);
//            }
//        } else {
//            logger.error("Request was null.");
//            ret.addError("Request was null.");
//        }
//
//        return ret;
    }

    /**
     * Creates a "composite" in response to submittal of an order or a proposal
     * to the workflow engine. A "composite" consists of a custom
     * ProcessTemplate created for the order/proposal. Each service on the
     * order/proposal becomes an ActivityTemplate in the ProcessTemplate.
     * Finally, ProcessFlow objects are created for the order/proposal, and each
     * service on the order/proposal. All processes are "readied" for
     * provisioning or sales flow processing.
     * 
     * @param command
     *            The command object from the workflow manager.
     * 
     * @return A response to be returned to the workflow manager.
     * 
     * @throws java.lang.Exception
     *             Validation or database access error.
     */
    public WorkFlowResponse createProcess(WorkFlowRequest command)
            throws Exception {
    	return processor.createProcess(command);
    	
    	
//        WorkFlowResponse ret = new WorkFlowResponse();
//
//        // Validate our input parameters before we do anything else.
//        if (command != null) {
//        	logger.debug(" Validate the process");
//            validate(command.getOrderId(), "External Id");
//            validate(command.getUserId(), "User Id");
//            validate(command.getAccountId(), "accountId");
//            
//            String roleDiscrim = extractRoleDiscrim(command);
//
//            ProcessFlow[] processes = null;
//            IController c = null;
//
//            try {
////              WorkFlowService workFlowBO = ServiceFactory.getInstance().getWorkFlowService();
//              WriteWorkFlowService workFlowBO = context.getBean(WriteWorkFlowService.class);
//                logger.debug(" Creating the process flows");
//                //DONE 2475
//                // pass in role discrim
//                processes = workFlowBO.createProcessFlows(
//                        command.getWorkFlow(), command.getAccountId(), command
//                                .getOrderId(), command.getExpedite(), command
//                                .getStartDt(), command.getDueDt(), command
//                                .getUserId(), roleDiscrim, command.getTransaction());
//            } catch (Exception e) {
//                logger.error(this, e);
//                e.printStackTrace(System.out);
//
//                String errMsg = e.getMessage();
//
//                if ((errMsg == null) || (errMsg.length() == 0)) {
//                    errMsg = e.getClass().getName() + " has been detected.";
//                }
//
//                ret.addError(errMsg);
//                ret.setRollbackNeeded(true);
//            }
//            
//            logger.debug(" Process flows successfully created");
//
//            // If we get here, then we successfully created our template and
//            // our ProcessFlows. Add the ProcessFlows to the map of active
//            // ProcessFlows. Then, create "Ready" controller for the first
//            // ProcessFlow and have it start up the process.
//            if (!ret.getRollbackNeeded()) {
////                addProcessFlowsToMap(processes);
//                c = WorkFlowEngine.createController(
//                        WorkFlowEngine.ACTION_READY, processes[0]);
//                HashMap<String, String> dataValues = extractDataValues(command);
//                c.execute(ret, command, dataValues);
//                
//                if ((dataValues != null) && (dataValues.size() > 0)) {
//                    // Create a controller object to handle the save. If there
//                    // are "side effects" or down-stream consequences (which
//                    // there
//                    // should
//                    // not be for a "save"), the Controller will take care of
//                    // propagating
//                    // those for us.
//                	logger.debug(" Doing a down-stream controller save");
//                    Activity a = processes[0].getFirstActivity();
//                    c = WorkFlowEngine.createController(WorkFlowEngine.ACTION_SAVE, a);
//                    c.execute(ret, command, dataValues);
//                }
//            }
//        } else {
//            logger.error("Request was null.");
//            ret.addError("Request was null.");
//        }
//
//        return ret;
    }

//	private String extractRoleDiscrim(WorkFlowRequest command) {
//		String roleDiscrim = null;
//		for (int i = 0; i < command.getDataCount(); i++) {
//			if (WorkFlowEngine.ROLE_DISCRIMINATOR.equals(command.getDataName(i))) {
//				roleDiscrim = command.getDataValue(i);
//				break;
//			}
//		}
//		return roleDiscrim;
//	}

    /**
     * Saves any data fields associated with the Activity identified in the
     * "rqst", but leaves the Activity checked out to its current owner.
     * Validation is performed to insure that the Activity is currently checked
     * out, and that the user requesting the "save" is the same user that has
     * the Activity checked out. If this condition is not true, then the save
     * will not be attempted.
     * 
     * @param rqst
     *            Request object containing command info and data to be saved.
     * @param process 
     * @param resp 
     * 
     * @return WorkFlowResponse Response object indicating results of request.
     * 
     * @throws java.lang.Exception
     *             Incorrect state or database access error.
     */
    public WorkFlowResponse saveState(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

    	
    	return processor.saveState(rqst, resp, process);
    	
//        // Extract the input parameters we need for check out and validate
//        // them.
//        if (rqst != null) {
//            validate(rqst.getActivityId(), "Activity Id");
//            validate(rqst.getUserId(), "User Id");
//
//            // See if we can find the Activity in the active workflows. If not,
//            // then something is wrong and an Exception is thrown.
//
//            Activity a = process.getActivity(rqst.getActivityId());
//
//            // See if the Activity is valid for saving the data. If not, record
//            // a tale of sorrow and woe to the response object.
//            if (validForCheckIn(a, rqst.getUserId())) {
//                // All we want to do is save service detail data. Extract it
//                // from
//                // the request object. If we don't have any, then we really have
//                // no
//                // reason to be here.
//                HashMap<String, String> dataValues = extractDataValues(rqst);
//
//                if ((dataValues != null) && (dataValues.size() > 0)) {
//                    // Create a controller object to handle the save. If there
//                    // are "side effects" or down-stream consequences (which
//                    // there
//                    // should
//                    // not be for a "save"), the Controller will take care of
//                    // propagating
//                    // those for us.
//                    String cmd = rqst.getAction();
//
//                    IController c = WorkFlowEngine.createController(cmd, a);
//
//                    c.execute(resp, rqst, dataValues);
//                }
//            } else {
//                StringBuffer msg = new StringBuffer(500);
//                msg
//                        .append("This Activity is not valid for saving data, status = [");
//                msg.append(a.getStatusCd());
//                msg.append("], Requesting user Id = [");
//                msg.append(rqst.getUserId().toString());
//                msg.append("], assigned user Id = [");
//
//                Integer assigned = a.getUserId();
//
//                if (assigned == null) {
//                    msg.append("null");
//                } else {
//                    msg.append(assigned.toString());
//                }
//                msg.append("].");
//
//                String errMsg = msg.toString();
//
//                logger.error(errMsg);
//                resp.addError(errMsg);
//            }
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//
//        return resp;
    }

    /**
     * Checks in an Activity current assigned to a user. The Activity must be in
     * the "In Process" state and assigned to the user making the check in
     * request. If successful, the "Activity" will be marked "Completed" in the
     * database and any service data associated with the activity will be saved.
     * Checking in one Activity usually will have the side effect of readying
     * other Activities or completing a ProcessFlow.
     * 
     * @param rqst
     *            Check in request. Includes possible service data.
     * @param process 
     * @param resp 
     * 
     * @return WorkFlowResponse The result of the check in request.
     * 
     * @throws java.lang.Exception
     *             Invalid state or database access error.
     */
    public WorkFlowResponse checkIn(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {
    	
    	return processor.checkIn(rqst, resp, process);
    	
//        // Extract the input parameters we need for check out and validate
//        // them.
//        if (rqst != null) {
//            validate(rqst.getActivityId(), "Activity Id");
//            validate(rqst.getUserId(), "User Id");
////            logger.error("DWL: CheckIn:  action=" + rqst.getAction() + ", type=" + rqst.getType() +
////                    ",activityId=" + rqst.getActivityId() + ", userId=" + rqst.getUserId(), 
////                    new Exception());  // #3206
//
//            // See if we can find the Activity in the active workflows. If not,
//            // then something is wrong and an Exception is thrown.
//            Activity a = process.getActivity(rqst.getActivityId());
//
//            // See if the Activity is valid for check in. If the
//            // Activity state is completely wrong for saving, an Exception
//            // will be thrown.
//            if (validForCheckIn(a, rqst.getUserId())) {
//                HashMap<String, String> dataValues = extractDataValues(rqst);
//
//                // Create a controller object to handle the check in. If there
//                // are "side effects" or down-stream consequences (which there
//                // will
//                // not be for a "check in"), the Controller will take care of
//                // propagating those for us.
//                String cmd = rqst.getAction();
//
//                IController c = WorkFlowEngine.createController(cmd, a);
//
//                c.execute(resp, rqst, dataValues);
//            } else {
//                StringBuffer msg = new StringBuffer(500);
//                msg.append("This Activity [");
//                msg.append(a.getActivityTemplateId());
//                msg.append("] is not valid for saving data, status = [");
//                msg.append(a.getStatusCd());
//                msg.append("], Requesting user Id = [");
//                msg.append(rqst.getUserId().toString());
//                msg.append("], assigned user Id = [");
//
//                Integer assigned = a.getUserId();
//
//                if (assigned == null) {
//                    msg.append(" Not Assigned");
//                } else {
//                    msg.append(assigned.toString());
//                }
//                msg.append("].");
//
//                String errMsg = msg.toString();
//                logger.error(errMsg);
//                resp.addError(errMsg);
//            }
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//
//        return resp;
    }
    
    public WorkFlowResponse checkInTo(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

    	
    	return processor.checkInTo(rqst,resp,process);
    	
//        // Extract the input parameters we need for check out and validate
//        // them.
//        if (rqst != null) {
//            validate(rqst.getActivityId(), "Activity Id");
//
//            logger.debug("CheckInTo : process id = " + rqst.getProcessId() + " to activity id = " + rqst.getActivityId() + " start.");
//            
//            Activity a = process.getFirstActivity();
//            ArrayList<Activity> as = new ArrayList<Activity>(); 
//            LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
//            as = findActivityPath(as, a, rqst.getActivityId(), data);
//
//            WorkFlowRequest c = new WorkFlowRequest(ACTION_CHECK_IN);
//            c.setProcessId(rqst.getProcessId());
//            if (data != null) {
//                for (String dataName : data.keySet()) {
//                    String dataValue = data.get(dataName);
//                    c.addDataPair(dataName, dataValue);
//                }
//            }
//            
//            for (int i = as.size(); i > 1; i--){
//                a = as.get(i-1);
//                c.setUserId(a.getUserId());
//                c.setActivityId(a.getActivityId());
//                logger.debug("CheckInTo : check in activity id = " + a.getActivityId());
//                resp = checkIn(c, resp, process);
//            }
//
//            logger.debug("CheckInTo : process id = " + rqst.getProcessId() + " to activity id = " + rqst.getActivityId() + " finished.");
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//
//        return resp;
    }

//    private ArrayList<Activity> findActivityPath(ArrayList<Activity> as, 
//            Activity a, Integer activityId, LinkedHashMap<String, String> data) {
//        
//        if (!a.getActivityId().equals(activityId)){
//            if (!a.getOutboundTransitionType().equals(Activity.LOOP)){
//                ProcessFlow pf = a.getContainer();
//                Transition[] outbounds = pf.getOutboundTransitions(a);
//                for (Transition t : outbounds){
//                    Activity nextAct = pf.getActivity(t.getToId());
//                    findActivityPath(as, nextAct, activityId, data);
//
//                    if (as.size() != 0){ 
//                        if(!ActivityStatusDef.COMPLETED.equalsIgnoreCase(a.getStatusCd())){
//                            as.add(a);
//                            data.put(t.getDataName(), t.getDataValue());
//                        }
//                        break;
//                    } 
//                }
//            }
//        }else{
//            as.add(a);
//        }
//            
//        return as;
//    }

    /**
     * Reassigns an Activity checked out to one user to another user. The
     * Activity must be in the "In Process" state, but the user who currently
     * has the Activity checked out is ignored. If successful, the "Activity"
     * will be "checked out", but assigned to a new user. The database will be
     * to reflect the change.
     * 
     * @param rqst
     *            Re-assignment request.
     * @param process 
     * @param resp 
     * 
     * @return WorkFlowResponse The result of the re-assignment request.
     * 
     * @throws java.lang.Exception
     *             Invalid state or database access error.
     */
    public WorkFlowResponse reassign(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

    	
    	return processor.reassign(rqst, resp, process);
    	
//        // Extract the input parameters we need for check out and validate
//        // them.
//        if (rqst != null) {
//            //validate(rqst.getActivityId(), "Activity Id");
//            validate(rqst.getUserId(), "User Id");
//            validate(rqst.getActivityId(), "Activity Id");
//
//            // See if we can find the Activity in the active workflows. If not,
//            // then something is wrong and an Exception is thrown.
//            Activity a = process.getActivity(rqst.getActivityId());
//
//            if (isCheckedOut(a) || 
//                    a.getStatusCd().equalsIgnoreCase(Activity.NOT_DONE) || 
//                    a.getStatusCd().equalsIgnoreCase(Activity.REFERRED)) {
//                // Assign the new user Id here. The controller will just save
//                // the
//                // Activity to the database.
//                a.setUserId(rqst.getUserId());
//                String cmd = rqst.getAction();
//                IController c = WorkFlowEngine.createController(cmd, a);
//
//                c.execute(resp, rqst);
//            } else {
//                StringBuffer msg = new StringBuffer(300);
//                msg.append("This Activity is not valid for re-assignment, ");
//                msg.append("status = [");
//                msg.append(a.getStatusCd());
//                msg.append("].  Only can re-assign In process activity.");
//
//                String errMsg = msg.toString();
//                logger.error(errMsg);
//                resp.addError(errMsg);
//            }
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//
//        return resp;
    }

    /**
     * Attempts to "retry" an automatic Activity after it has been blocked due
     * to a failure. The Activity is expected to be marked as "blocked". If that
     * is true, then the automatic class associated with this Activity will be
     * reattempted. If it succeeds, then standard downstream processing, e.g.,
     * any follow-on Activities are made "ready", will be performed. If the
     * automatic Activity fails, the Activity will remain in the "blocked"
     * state.
     * 
     * @param rqst
     *            Contains retry information such as activity Id and process Id
 * @param process 
 * @param resp .
     * 
     * @return WorkFlowResponse containing the results of the request.
     * 
     * @throws java.lang.Exception
     *             Automatic class exception.
     */
    public WorkFlowResponse retry(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

    	return processor.retry(rqst, resp, process);
    	
//        // Extract the input parameters we need for check out and validate
//        // them.
//        if (rqst != null) {
//            validate(rqst.getActivityId(), "Activity Id");
//            validate(rqst.getUserId(), "User Id");
//
//            // See if we can find the Activity in the active workflows. If not,
//            // then something is wrong and an Exception is thrown.
//            Activity a = process.getActivity(rqst.getActivityId());
//
//            if (validForRetry(a)) {
//                // Assign the new user Id here. The controller will retry the
//                // Activity. If that works, then downstream Activities will be
//                // readied for processing.
//                a.setUserId(rqst.getUserId());
//                String cmd = rqst.getAction();
//                IController c = WorkFlowEngine.createController(cmd, a);
//
//                c.execute(resp, rqst);
//            } else {
//                StringBuffer msg = new StringBuffer(300);
//                msg.append("This Activity is not valid for re-try, ");
//                msg.append("status = [");
//                msg.append(a.getStatusCd());
//                msg.append("].");
//
//                String errMsg = msg.toString();
//                logger.error(errMsg);
//                resp.addError(errMsg);
//            }
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//
//        return resp;
    }

    /**
     * Move a service that is currently on one order to another order. The new
     * order is assumed to have been created and the tables in the ordering
     * subsystem reflect the change. The Activity representing the service is
     * moved from its original ProcessFlow to the ProcessFlow for the new order.
     * 
     * @param rqst
     *            WorkFlowRequest object containing new parameters.
     * @param process 
     * @param resp 
     * 
     * @return WorkFlowReponse The results of this request.
     * 
     * @throws java.lang.Exception
     *             Any number of errors.
     */
    public WorkFlowResponse splitProcess(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

    	return processor.splitProcess(rqst, resp, process);
    	
//        // Extract the input parameters we need for check out and validate
//        // them.
//        if (rqst != null) {
//            validate(rqst.getProcessId(), "Process Id");
//            validate(rqst.getUserId(), "User Id");
//
//            // Get reference to the Process AS IT ORIGINALLY EXISTED in the
//            // the process map. We are going to need this in a minute. If we
//            // can't find it, then an exception will be thrown.
//
//            // Find the parent ProcessFlow and Activity for "split". Verify that
//            // we have the correct number of in-bound and out-bound Transitions.
//            // If we don't, it is an error.
//
//            ProcessFlow oldParent = process.getParentProcess();
//            Activity parentAct = process.getParentActivity();
//
//            Transition[] inbound = oldParent.getInboundTransitions(parentAct);
//
//            if ((inbound == null) || (inbound.length != 1)) {
//                String errMsg = "Incorrect number of inbound Transitions to "
//                        + "parent Activity.";
//
//                logger.error(errMsg);
//                throw new Exception(errMsg);
//            }
//
//            Transition[] outbound = oldParent.getOutboundTransitions(parentAct);
//
//            if ((outbound == null) || (outbound.length != 1)) {
//                String errMsg = "Incorrect number of outbound Transitions to "
//                        + "parent Activity.";
//
//                logger.error(errMsg);
//                throw new Exception(errMsg);
//            }
//
//            // Get a handle to the Activity that is downstream from our parent
//            // Activity. We need this now because we are going to throw away
//            // its inbound Transition and we will need to send it a "Ready"
//            // command after we have split the order.
//
//            Integer downstreamId = outbound[0].getToId();
//            Activity downstream = oldParent.getActivity(downstreamId);
//
//            // The WorkFlow business object is going to go through conniptions
//            // to create a proper ProcessFlow for the order split. In addition,
//            // it will also the "old parent" to fix it up in the database to
//            // remove the "parentAct" Activity from that process. We delegate
//            // this to the business object so that we can do everything that
//            // needs to be done in a single transaction (easier to clean up when
//            // we fail).
//
//            try {
//                WriteWorkFlowService workFlowBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
//                ProcessFlow newPf = workFlowBO.createSplitProcessFlow(rqst
//                        .getType(), rqst.getOrderId(), parentAct, oldParent,
//                        process);
//
////                addProcessFlowToMap(newPf);
//
//                // Now, send a "Ready" command to the downstream Activity in
//                // "oldParent". If all of the other processes on this order are
//                // done, this will complete that order. If it does, the
//                // Controllers will update the in-memory workflows to remove
//                // the process.
//
//                IController c = WorkFlowEngine.createController(
//                        WorkFlowEngine.ACTION_READY, downstream);
//                c.execute(resp, rqst);
//
//                // Ask the in-memory workflows table for the "oldParent". If
//                // that
//                // is now gone, then "oldParent" is completed.
//
////                Object o = WorkFlowEngine._workFlows.get(oldParent
////                        .getProcessId());
////
////                if (o == null) {
////                    String infoMsg = "Order #"
////                            + oldParent.getExternalId().toString()
////                            + " is completed.";
////
////                    logger.debug(infoMsg);
////                    resp.addInfoMsg(infoMsg);
////                }
//
//                // One final thing to do: Send the process Id for the new
//                // process
//                // back to the WorkFlowManager so it can pass it up the food
//                // chain
//                // to its caller. We will do this using the "built-in" data
//                // interface.
//
//                resp.addDataSpec("processId", INTEGER_CUSTOM_DETAIL_ID, newPf
//                        .getProcessId().toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//                logger.error("Exception: " + e.getMessage(), e);
//                resp.addError(e.getMessage());
//            }
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//
//        return resp;
    }

    /**
     * Handles a request to rollback a "Terminate Service Order" to the previous
     * step, i.e., the user does not want to terminate this service order and
     * would like to give it another try. The WorkFlowRequest must contain the
     * process Id and activity Id (activity template Id) of the "Terminate
     * Service Order" request. Also, it should contain the user Id of the user
     * making the request.
     * 
     * @param rqst
     *            WorkFlowRequest containing Activity key values.
     * @param process 
     * @param resp 
     * 
     * @return WorkFlowResponse The result of the request.
     * 
     * @throws java.lang.Exception
     *             Validation or database access error.
     */
    public WorkFlowResponse rollbackTerminateService(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process)
            throws Exception {

    	return processor.rollbackTerminateService(rqst, resp, process);
    	
//        // Extract the input parameters we need for rollback and validate
//        // them.
//        if (rqst != null) {
//            validate(rqst.getUserId(), "User Id");
//            validate(rqst.getActivityId(), "Activity Id");
//
//            // Find the "Terminate Service Order" Activity and its parent
//            // ProcessFlow. If we can't find either one, then throw an
//            // Exception.
//            // Make sure the Activity is in a state that valid for what we are
//            // about to do to it.
//            Activity deadmeat = process.getActivity(rqst.getActivityId());
//
//            if (!validForRollback(deadmeat)) {
//                // Create a Controller for the rollback and have it finish up
//                // what
//                // we need to do.
//                IController c = WorkFlowEngine.createController(
//                        WorkFlowEngine.ACTION_ROLLBACK_TERM_SVC, deadmeat);
//                c.execute(resp, rqst);
//            } else {
//                resp.addFailure("Invalid state for Activity rollback.");
//            }
//        } else {
//            logger.error("Request was null.");
//            resp.addError("Request was null.");
//        }
//
//        return resp;
    }

    // ===========================================================================
    // UTILITY Methods.
    // ===========================================================================

    /**
     * Creates an array of Service Id to Activity Template Def Id objects from
     * the input "cmd".
     * 
     * @param cmd
     *            WorkFlowRequest object containing service mapping info.
     * 
     * @return The service mapping info.
     */
    protected ServiceTemplateMap[] createServiceMap(WorkFlowRequest cmd) {
        ArrayList<ServiceTemplateMap> temp = new ArrayList<ServiceTemplateMap>();

        for (int i = 0; i < cmd.getServiceCount(); i++) {
            ServiceTemplateMap stm = new ServiceTemplateMap(
                    cmd.getServiceId(i), cmd.getActivityTemplateDefId(i));
            temp.add(stm);
        }

        return temp.toArray(new ServiceTemplateMap[0]);
    }

    /**
     * Extracts data name-value pairs from the "cmd" and returns them in a
     * HashMap. The HashMap will contain only <code>String</code> objects. The
     * "data name" will be the key and the "data value" will be the value. If
     * "cmd" contains no data, then the returned HashMap will have a "size()" of
     * zero.
     * 
     * @param cmd
     *            WorkFlowRequest object containing data name-value pairs.
     * 
     * @return HashMap containing String data name (key) and data value (value).
     */
    protected HashMap<String, String> extractDataValues(WorkFlowRequest cmd) {
        HashMap<String, String> dataValues = new HashMap<String, String>();

        for (int i = 0; i < cmd.getDataCount(); i++) {
            dataValues.put(cmd.getDataName(i), cmd.getDataValue(i));
        }

        return dataValues;
    }

    /**
     * Validates an Integer request parameter to verify that it exists and that
     * it has a value greater than zero. If either of these conditions is
     * untrue, then the parameter is invalid and an Exception is thrown. If this
     * method returns without an exception, then validation was successful.
     * 
     * @param parameter
     *            Integer input parameter to be validated.
     * @param name
     *            Name of the parameter (included in Exception message).
     * 
     * @throws java.lang.Exception
     *             Missing parameter or invalid value.
     */
//    protected void validate(Integer parameter, String name) throws Exception {
//        if (parameter == null) {
//            String errMsg = "Parameter [" + name + "] is missing from the "
//                    + "input request.";
//
//            logger.error(errMsg);
//            throw new Exception(errMsg);
//        }
//
//        if (parameter.intValue() <= -1 && parameter.intValue() != -100) {
//            String errMsg = "Invalid value for input parameter. Name = " + name
//                    + ", value = " + parameter.toString() + ".";
//
//            logger.error(errMsg);
//            throw new Exception(errMsg);
//        }
//    }

    /**
     * Finds an <tt>Activity</tt> object in the set of active ProcessFlow
     * objects given its process Id and activity Id. The combination of process
     * Id and activity Id uniquely identify an Activity. If no corresponding
     * Activity is found in the active workflows (it could still exist in the
     * database, but it would be in a "completed" state), then an Exception is
     * thrown.
     * 
     * @param processId
     *            Process Id.
     * @param activityId
     *            Activity Id.
     * 
     * @return Activity The corresponding Activity object.
     * 
     * @throws Exception
     *             Activity not found in active workflows.
     */
    protected Activity findActivity(Integer processId, Integer activityId)
            throws Exception {
    	
    	return processor.findActivity(processId, activityId);
    	
//        Activity a = null;
//
//        ProcessFlow p = findProcessFlow(processId);
//
//        if (p != null) {
//            a = p.getActivity(activityId);
//            // a.setCurrent("Y");
//        }
//
//        if (a == null) {
//            String errMsg = "Activity not found for process Id = "
//                    + processId.toString() + ", Activity Id = "
//                    + activityId.toString() + ".";
//
//            logger.error(errMsg);
//            throw new Exception(errMsg);
//        }
//
//        return a;
    }

    /**
     * 
    protected void updateProcessMap(Hashtable<Integer, ProcessFlow> processMap,
            ProcessFlow p) throws DAOException {
        processMap.put(p.getProcessId(), p);
        
        // Keep the new process id in the list and if the size is over, 
        // remove the last process id from list and table
        processIdList.add(p.getProcessId());
        if (processIdList.size() > tableSize){
            Integer tId = processIdList.removeLast();
            processMap.remove(tId);
        }
        
        // Look through all the current workFlows for subFlows in order to
        // build links from Parent Activities to the SubFlows and from
        // the subFlows to the parent Activities.
        ProcessFlow parentProcess;
        Activity parentAct;

        Integer parentProcessId = p.getParentProcessId();

        // If we have a non-null parent process Id, find the parent
        // process in the Hashtable.
        try {
            ReadWorkFlowService workFlowBO = App.getApplicationContext().getBean(ReadWorkFlowService.class);

            if (parentProcessId != null) {
                // This looks odd, but is correct. The "activity_template_id"
                // stored in the "Process" table is the "activity_template_id"
                // of the parent Activity (the column should probably be
                // renamed "parent_activity_template_id". The key on the
                // "Process_Activity table is (process_id,
                // activity_template_id).
                // So, we need BOTH values to locate the parent Activity.
                Integer parentActivityId = p.getParentActivityTemplateId();

                if (parentActivityId != null) {
                    // Find the parent ProcessFlow in the process map and then
                    // find
                    // the parent Activity in the parent process flow.
                    parentProcess = processMap.get(parentProcessId);
                    if (parentProcess == null) {
                        parentProcess = workFlowBO.retrieveActiveProcessFlow(parentProcessId);
                        if (parentProcess == null) {
                            return;
                        }
                        updateProcessMap(WorkFlowEngine._workFlows, parentProcess);
                    }
                    parentAct = parentProcess.getActivity(parentActivityId);

                    // Hook the current ProcessFlow to its parent Activity.
                    parentAct.setSubFlow(p);

                    // Hook the parent ProcessFlow and parent Activity to the
                    // current ProcessFlow.
                    p.setParentActivity(parentAct);
                    p.setParentProcess(parentProcess);
                }
            } else {
                // Find the parent process for this process from database and
                // add
                // it into map.
                ProcessFlow[] ps = workFlowBO.retrieveParentActiveProcessFlow(p.getProcessId());

                for (ProcessFlow pp : ps)
                    updateProcessMap(WorkFlowEngine._workFlows, pp);
            }
        } catch (RemoteException re) {
            logger.error(re);
            throw new DAOException(re.getMessage());
        }
        return;
    }
     */

    /**
     * Finds the ProcessFlow corresponding to "processId" in the workflows
     * table. If no ProcessFlow is found, throws an Exception.
     * 
     * @param processId
     *            Id of the ProcessFlow to be found.
     * 
     * @return ProcessFlow The corresponding ProcessFlow.
     * 
     * @throws java.lang.Exception
     *             ProcessFlow not found.
     */
    protected ProcessFlow findProcessFlow(Integer processId) throws Exception {
    	
    	return processor.findProcessFlow(processId);
    	
////        ProcessFlow p = WorkFlowEngine._workFlows.get(processId);
////
////        if (p == null) {
//            ReadWorkFlowService workFlowBO = App.getApplicationContext().getBean(ReadWorkFlowService.class);
//            ProcessFlow p = workFlowBO.retrieveActiveProcessFlow(processId);
//            if (p != null) {
////                updateProcessMap(WorkFlowEngine._workFlows, p);
//            } else {
//                String errMsg = "Active ProcessFlow not found for process Id = "
//                        + processId.toString() + "--if it exists the end date is null.";
//
//                logger.error(errMsg);
//                throw new Exception(errMsg);
//            }
////        }
//        
//        // move the processId to the begin of list
////        processIdList.remove(processId);
////        processIdList.addFirst(processId);
//
//        return p;
    }

    /**
     * Verifies that the Activity <code>a</code> is in a state suitable for
     * check-out. The Activity is in one of three states: valid for check-out,
     * invalid for check-out, or somebody else has it checked out. The way this
     * is represented is:
     * 
     * If the Activity is valid for check-out, return "true". If the Activity is
     * already checked out, return "false". If the Activity is in an invalid
     * state for check-out, e.g., it is already completed, then throw an
     * Exception.
     * 
     * @param act
     *            Activity to be validated.
     * 
     * @return boolean "true" if ready for check-out, "false" if already checked
     *         out by somebody else.
     * 
     * @throws java.lang.Exception
     *             Invalid Activity state for check-out.
     */
    protected boolean validForCheckOut(Activity act) throws Exception {
        String aStatus = null;
        boolean ret = false;

        synchronized (act) {
            aStatus = act.getStatusCd();
        }

        // If the Activity is in the PENDING state, it is ready for check-out.

        if (aStatus.compareTo(Activity.PENDING) == 0) {
            ret = true;
        } else if (aStatus.compareTo(Activity.IN_PROCESS) == 0) {
            // If the Activity is IN_PROCESS, it is already checked out.
            ret = false;
        } else {
            // If we get here, something is really wrong.
            String errMsg = "Invalid Activity status = " + aStatus
                    + " for check out.";
            logger.error(errMsg);
            throw new Exception(errMsg);
        }

        return ret;
    }

    /**
     * Verifies that the Activity <code>a</code> is in a state suitable for
     * by-pass. The Activity is in one of three states: valid for skipping,
     * invalid for skipping, or somebody else has already skipped it. The way
     * this is represented is:
     * 
     * If the Activity is valid for skipping, return "true". If the Activity is
     * already skipped, return "false". If the Activity is in an invalid state
     * for by-pass, e.g., it is already completed, then throw an Exception.
     * 
     * @param act
     *            Activity to be validated.
     * 
     * @return boolean "true" if ready for by-pass, "false" if already skipped.
     * 
     * @throws java.lang.Exception
     *             Invalid Activity state for check-out.
     */
    protected boolean validForSkipping(Activity act) throws Exception {
        String aStatus = null;
        boolean ret = false;

        synchronized (act) {
            aStatus = act.getStatusCd();
        }

        // If the Activity is in the PENDING state, it is ready for by-pass.

        if (aStatus.compareTo(Activity.PENDING) == 0
                || aStatus.equalsIgnoreCase(Activity.IN_PROCESS)) {
            ret = true;
        } else if (aStatus.compareTo(Activity.SKIPPED) == 0) {
            // If the Activity is SKIPPED, it is already by-passed.
            ret = false;
        } else {
            // If we get here, something is really wrong.
            String errMsg = "Invalid Activity status = " + aStatus
                    + " for by-pass.";
            logger.error(errMsg);
            throw new Exception(errMsg);
        }

        return ret;
    }

    /**
     * Verifies that the Activity <code>a</code> is in a state suitable for
     * rollback. This means that this Activity has been designated as a terminal
     * activity and has been reached in the workflow. Now, the user wants to
     * back up a step and see if another branch of the workflow can be taken. If
     * the Activity is either checked-out or ready, then it is in a valid state.
     * Otherwise, throw an Exception.
     * 
     * If the Activity is valid for rollback, return "true". If the Activity is
     * in an invalid state for rollback, e.g., it is already completed, then
     * throw an Exception.
     * 
     * @param act
     *            Activity to be validated.
     * 
     * @return boolean "true" if ready for rollback.
     * 
     * @throws java.lang.Exception
     *             Invalid Activity state for rollback.
     */
    protected boolean validForRollback(Activity act) throws Exception {
        String aStatus = null;
        boolean ret = false;

        synchronized (act) {
            aStatus = act.getStatusCd();
        }

        // If the Activity is in the PENDING or IN_PROCESS state, it is ready
        // for rollback.
        if (aStatus.compareTo(Activity.PENDING) == 0
                || aStatus.compareTo(Activity.IN_PROCESS) == 0) {
            ret = true;
        } else {
            // If we get here, something is really wrong.
            String errMsg = "Invalid Activity status = " + aStatus
                    + " for rollback.";
            logger.error(errMsg);
            throw new Exception(errMsg);
        }

        return ret;
    }

    /**
     * Verifies that the Activity "a" is valid for checking in. To be valid, "a"
     * must be "In Process", i.e., checked out, to user "userId". Returns "true"
     * if the condition is satisfied, otherwise returns "false".
     * 
     * @param act
     *            Activity we are validating.
     * @param userId
     *            The Id of the user making the save request.
     * 
     * @return "true" if "act" is currently checked out to "user Id".
     */
    protected boolean validForCheckIn(Activity act, Integer userId) {
        String aStatus = null;
        Integer actUserId = null;
        boolean ret = false;

        synchronized (act) {
            aStatus = act.getStatusCd();
            actUserId = act.getUserId();
        }

        if (aStatus.equals(Activity.IN_PROCESS) && (actUserId.equals(userId))) {
            ret = true;
        }

        return ret;
    }

    /**
     * Verifies that the Activity "a" is valid for re-assignment. To be valid,
     * "a" must be "In Process", i.e., checked out. Note that we don't care who
     * the Activity is currently assigned to. Returns "true" if the condition is
     * satisfied, otherwise returns "false".
     * 
     * @param a
     *            Activity we are validating.
     * 
     * @return "true" if "a" is currently checked out to "user Id".
     */
    protected boolean isCheckedOut(Activity a) {
        boolean ret = false;
        String aStatus = a.getStatusCd();

        if (aStatus.equals(Activity.IN_PROCESS)) {
            ret = true;
        }

        return ret;
    }

    /**
     * Verifies that Activity "a" is valid for release. To be valid, the
     * Activity must be in the checked-out state AND assigned to this user.
     * Returns "true" if the Activity is currently checked out to this user.
     * Otherwise, returns "false".
     * 
     * @param act
     *            Activity to be released.
     * @param userId
     *            The user who is releasing the Activity.
     * 
     * @return True if this user can release this Activity from check-out.
     */
    protected boolean validForRelease(Activity act, Integer userId) {
        String aStatus = null;
        Integer aUser = null;
        boolean ret = false;

        synchronized (act) {
            aStatus = act.getStatusCd();
            aUser = act.getUserId();
        }

        if (aStatus.equals(Activity.IN_PROCESS) && aUser.equals(userId)) {
            ret = true;
        }

        return ret;
    }

    /**
     * Returns "true" if Activity "a" is valid for retry. This means that "a"
     * must be in the "blocked" state.
     * 
     * @param act
     *            Activity to be re-attempted.
     * 
     * @return "true" if the "act" can be re-attempted.
     */
    protected boolean validForRetry(Activity act) {
        String aStatus = null;
        boolean ret = false;

        synchronized (act) {
            aStatus = act.getStatusCd();
        }

        if (aStatus.equals(Activity.BLOCKED)
                || aStatus.equals(Activity.WAIT_FOR_RETRY)) {
            ret = true;
        }

        return ret;
    }

    /**
     * Creates a controller for Activity "a" for command "cmd".
     * 
     * @param cmd
     *            The command to be executed on the Activity.
     * @param a
     *            The Activity to operate on.
     * 
     * @return Controller that will execute on "a".
     */
    static protected IController createController(String cmd, Activity a) {
        return Controller.createController(cmd, a);
    }

    /**
     * Creates a controller for ProcessFlow "p" for command "cmd".
     * 
     * @param cmd
     *            The command to be executed on the Activity.
     * @param p
     *            The ProcessFlow to operate on.
     * 
     * @return Controller that will execute on "p".
     */
    static protected IController createController(String cmd, ProcessFlow p) {
        return Controller.createController(cmd, p);
    }

    /**
     * @return the dInfo
     */
//    public final DaemonInfo getDInfo() {
//        return dInfo;
//    }
}

class ProcessRequest implements Runnable {
    private Socket client;
    private WorkFlowEngine engine;
    private Logger logger = Logger.getLogger(this.getClass());
        
    public ProcessRequest(Socket inClient, WorkFlowEngine inEngine) {
        this.client = inClient;
        this.engine = inEngine;
    }
    
    public void run() {
        try {
            InputStream in = client.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(in);
            OutputStream out = client.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);

            // The WorkFlowRequest tells us what to do. Funnel it off to
            // "processCmd()", which will do the request and give us the
            // response.

            WorkFlowRequest req = (WorkFlowRequest) (ois.readObject());
            logger.debug("WorkFlowRequest received: " + req.toString() + ".");

            if (req.getDueDt() != null && req.getDueDt().getTime() == 0) {
            	req.setDueDt(null);
            }
            if (req.getEndDt() != null && req.getEndDt().getTime() == 0) {
            	req.setEndDt(null);
            }
            if (req.getJeopardyDt() != null && req.getJeopardyDt().getTime() == 0) {
            	req.setJeopardyDt(null);
            }
            if (req.getStartDt() != null && req.getStartDt().getTime() == 0) {
            	req.setStartDt(null);
            }
            
            WorkFlowResponse wfr = engine.processCmd(req);
            logger.debug("Response generated: " + wfr.toString());

            oos.writeObject(wfr);
            oos.flush();
            logger.debug("Response written to socket.");

        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
        } catch (ClassNotFoundException cnfe) {
            logger.error(cnfe.getMessage(), cnfe);
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException ioe) {
                logger.error(ioe.getMessage(), ioe);
            }
         }
    }
}
