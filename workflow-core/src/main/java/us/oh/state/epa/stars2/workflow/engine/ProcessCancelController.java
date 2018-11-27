package us.oh.state.epa.stars2.workflow.engine;

import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: ProcessCancelController
 * </p>
 * 
 * <p>
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author Poshan
 * @version 1.0
 */

class ProcessCancelController extends ProcessFlowController {
    /**
     * Constructor. Takes a reference to the ProcessFlow to be readied.
     * 
     * @param pf
     *            ProcessFlow to be readied.
     */
    public ProcessCancelController(ProcessFlow pf) {
        super(pf);
    }

    /**
     * Framework method. Sets the start date for the ProcessFlow and readies all
     * initial Activities (there is probably only one).
     * 
     * @param resp
     *            Used to add failure or error messages.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        // Get an array of all of the Activity objects associated with this
        // Procss Flow. If it has no Activities, then we are done.
        Activity[] activities = processFlow.getActivities();

        if ((activities != null) && (activities.length > 0)) {
            for (Activity a : activities) {
                if (a.getEndDt() != null) {
                    continue;
                }
                IController c = Controller.createController(
                        WorkFlowEngine.ACTION_CANCEL, a);
                c.execute(resp, rqst);
            }
        }
        IController completeController = Controller.createController(
                WorkFlowEngine.ACTION_COMPLETE, processFlow);
        completeController.execute(resp, rqst);
        
        IController saveNoteController = Controller.createController(
                WorkFlowEngine.ACTION_SAVE_NOTE, processFlow);
        String note = "Process cancelled.";
        for (int i = 0; i < rqst.getDataCount(); i++) {
        	if(rqst.getDataName(i).equalsIgnoreCase(WorkFlowRequest.CANCEL_CUSTOM_NOTE)) {
        		note = note + " " + rqst.getDataValue(0);
        		break;
        	}
        }
    	rqst.addDataPair(WorkFlowRequest.SAVE_NOTE_VALUE, note);
        saveNoteController.execute(resp, rqst);
    }
}
