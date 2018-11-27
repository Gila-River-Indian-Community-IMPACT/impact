package us.oh.state.epa.stars2.workflow.engine;

import java.rmi.RemoteException;

import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

/**
 * <p>
 * Title: ProcessFlowController
 * </p>
 * 
 * <p>
 * Description: This is the base class for ProcessFlow controllers. It provides
 * common services needed by ProcessFlow controllers.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 * @version 1.0
 */

abstract class ProcessFlowController extends Controller {
    ProcessFlow processFlow;

    /**
     * Constructor. Takes a reference to the ProcessFlow being controlled.
     * 
     * @param pf
     *            ProcessFlow being controlled.
     */
    protected ProcessFlowController(ProcessFlow pf) {
        this.processFlow = pf;
    }

    /**
     * Saves ProcessFlow information to the database. This only saves
     * process-specific state (not template state).
     * 
     * @throws DAOException
     *             Database update failure.
     */
    protected void saveToDatabase() throws DAOException {
        try {
//            WriteWorkFlowService workFlowBO = ServiceFactory.getInstance().getWriteWorkFlowService();
            WriteWorkFlowService workFlowBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
            this.processFlow = workFlowBO.updateProcessFlow(this.processFlow);
//        } catch (ServiceFactoryException sfe) {
//            Controller.logException(sfe);
        } catch (RemoteException re) {
            Controller.logException(re);
        }
    }
    
    protected void saveToDatabase(Transaction trans) throws DAOException {
        try {
        	if(trans == null){
        		trans = TransactionFactory.createTransaction();
        	}
        	
//            WriteWorkFlowService workFlowBO = ServiceFactory.getInstance().getWriteWorkFlowService();
            WriteWorkFlowService workFlowBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
            this.processFlow = workFlowBO.updateProcessFlow(this.processFlow, trans);
//        } catch (ServiceFactoryException sfe) {
//            Controller.logException(sfe);
        } catch (RemoteException re) {
            Controller.logException(re);
        }
    }
}
