package us.oh.state.epa.stars2.workflow.engine;

import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: ActivitySaveController
 * </p>
 * 
 * <p>
 * Description: Updates service detail data to the appropriate tables in the
 * database. By the time we get to this object, this request is assumed to be
 * valid.
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

public class ActivitySaveController extends ActivityController {
    /**
     * Constructor.
     * 
     * @param a
     *            Activity whose data values are being saved.
     */
    public ActivitySaveController(Activity a) {
        super(a);
    }

    /**
     * Framework method. Saves any service detail data we have to the database.
     * If there is an error with the request, an error message will be added to
     * "resp".
     * 
     * @param resp
     *            Response object.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        synchronized (activity) {
            try {
                saveServiceData(rqst.getTransaction());
            } catch (DAOException de) {
                Controller.logger.error(de.getMessage(), de);
                resp.addFailure(de.getMessage());
            }
        }
    }
}
