package us.oh.state.epa.stars2.workflow.engine;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import us.oh.state.epa.stars2.database.dbObjects.workflow.ControllerConfig;
import us.oh.state.epa.stars2.workflow.Activity;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

/**
 * <p>
 * Title: ControllerFactory
 * </p>
 * 
 * <p>
 * Description: A Factory for creating workflow controller objects. The factory
 * is constructed from the Controller_Factory_Cfg table in the database.
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
 * @version 1.0
 */
public class ControllerFactory {
    // ProcessFlow
    private static ControllerFactory instance;

    /**
     * Associates a controller with a workflow activity.
     */
    private static final String ACT_CTL_TYPE = "Activity";

    /**
     * Associates a controller with a workflow process flow.
     */
    private static final String PROC_CTL_TYPE = "ProcessFlow";
    private HashMap<String, String> activityMap; // Control type to class map
    // for Activity
    private HashMap<String, String> processMap; // Control type to class map for

    /**
     * Constructor. This is a singleton class.
     */
    private ControllerFactory() {
        ReadWorkFlowService wfBO = App.getApplicationContext().getBean(ReadWorkFlowService.class);
        
//        try {
//            wfBO = ServiceFactory.getInstance().getReadWorkFlowService();
//        } catch (ServiceFactoryException sfe) {
//        }

        ControllerConfig[] controls = null;

        activityMap = new HashMap<String, String>();
        processMap = new HashMap<String, String>();

        try {
            controls = wfBO.retrieveAllControllerConfigs();
        } catch (Exception e) {
            Controller.logger.error(e.getMessage(), e);
            return;
        }

        // If we had no configuration information in the database, we are
        // seriously hosed.

        if ((controls == null) || (controls.length == 0)) {
            Controller.logger.error("No controller configuration information "
                            + "found.");
        }

        String objectType;

        if (controls != null) {
            // Iterate over each configuration record. Assign the control to
            // the appropriate HashMap.
            for (ControllerConfig tempConfig : controls) {
                objectType = tempConfig.getControlObject();

                if (objectType.equals(ControllerFactory.ACT_CTL_TYPE)) {
                    activityMap.put(tempConfig.getControlType(), tempConfig
                            .getControllerClassName());
                } else if (objectType.equals(ControllerFactory.PROC_CTL_TYPE)) {
                    processMap.put(tempConfig.getControlType(), tempConfig
                            .getControllerClassName());
                } else {
                    Controller.logger.error("Unknown object type = ["
                            + objectType + "].");
                }
            }
        }
    }

    /**
     * Creates a Controller object for a workflow Activity. If the control type
     * is unknown, returns an <code>UnknownActionController</code>.
     * 
     * @param controlType
     *            String The type of control to control.
     * @param act
     *            Activity The Activity to be operated on.
     * 
     * @return IController The Controller that does that operation.
     */
    protected IController createControl(String controlType, Activity act) {
        return ControllerFactory.createControl(controlType, act, activityMap,
                "Activity");
    }

    /**
     * Creates a Controller object for a workflow ProcessFlow. If the control
     * type is unknown, returns an <code>UnknownActionController</code>.
     * 
     * @param controlType
     *            String The type of control to control.
     * @param process
     *            ProcessFlow The ProcessFlow to be operated on.
     * 
     * @return IController The Controller that does that operation.
     */
    protected IController createControl(String controlType, ProcessFlow process) {
        return ControllerFactory.createControl(controlType, process,
                processMap, "Process");
    }

    /**
     * Creates an instance of an appropriate controlled for "controlled". The
     * "controlMap" links control types to the name of the corresponding control
     * class. "objectName" is the name of the "controlled" type in the database.
     * If the control type is unknown, returns an
     * <code>UnknownActionController</code>.
     * 
     * @param controlType
     *            String Control type.
     * @param controlled
     *            Object Object to be controlled.
     * @param controlMap
     *            HashMap Control type to control class name map.
     * @param objectName
     *            String Database name for the controlled object.
     * 
     * @return IController The control for this object.
     */
    static private IController createControl(String controlType,
            Object controlled, HashMap<String, String> controlMap, String objectName) {
        IController ctl = null;

        // First, see if we can find a control class name for this control
        // type in the "controlMap". If not, log an error message and return
        // an UnknownActionController.

        String ctlClassName = controlMap.get(controlType);
        
        Controller.logger.debug(" Controller " + ctlClassName + " is being initialized");
        
//        Controller.logger.error("DWL:  In createControl, controlType=" + controlType +
//                ", ctlClassName=" + ctlClassName, new Exception());  // #3206
        
        if (ctlClassName == null) {
            Controller.logger.error("No control class name found for "
                    + "control type = [" + controlType + "], object name = ["
                    + objectName + "].");

            return new UnknownActionController(controlType);
        }

        // We have a controller class name. Now, we need to create the
        // controller object. To do this, we need a constructor that takes
        // an object of type "controlled".

        try {
            Class<?> ctlClass = Class.forName(ctlClassName);
            Class<?>[] paramClass = new Class[1];
            paramClass[0] = controlled.getClass();
            Constructor<?> ctlConstructor = ctlClass.getConstructor(paramClass);

            // If we don't have the right kind of constructor, we're dead.

            if (ctlConstructor == null) {
                Controller.logger.error("No Constructor found for '"
                        + ctlClassName + "' taking a parameter of type '"
                        + controlled.getClass() + "'.");

                return new UnknownActionController(controlType);
            }

            // Put the "controlled" object in a parameter array and invoke the
            // constructor we just found.

            Object[] params = new Object[1];
            params[0] = controlled;

            ctl = (Controller) (ctlConstructor.newInstance(params));
            // Controller.logger.error("DWL: Controller class is " + ctl.getClass().getCanonicalName(), new Exception()); // #3206
        } catch (Exception e) {
            Controller.logger.error(e.getMessage(), e);
            ctl = new UnknownActionController(controlType);
        }

        return ctl;
    }

    /**
     * Returns an instance of the controller factory. The factory is a singleton
     * object.
     * 
     * @return ControllerFactory
     */
    static public ControllerFactory getInstance() {
        if (ControllerFactory.instance == null) {
            ControllerFactory.instance = new ControllerFactory();
        }

        return ControllerFactory.instance;
    }
}
