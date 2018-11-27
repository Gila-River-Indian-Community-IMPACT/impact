package us.oh.state.epa.stars2.workflow.util;

import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.WorkFlowDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.PerformerDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.TransitionDef;
import us.oh.state.epa.stars2.def.FacilityRoleDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.App;

//TODO what type of sterotype and scope??
public class WorkFlowDefs {
    private LinkedHashMap<String, String> transitionTypes;
    private LinkedHashMap<String, String> activityStatus;
    private SimpleDef[] processTypes;
    private PerformerDef[] performerTypes;
    private FacilityRoleDef[] facilityRoles;
    private WorkFlowDAO wfDao;
    private FacilityDAO facDao;
    private static Logger logger = Logger.getLogger(WorkFlowDefs.class);

    public WorkFlowDefs() {
        init();
    }

    private void init() {
        if (wfDao == null) {
//          wfDao = (WorkFlowDAO) (DAOFactory.getDAO("WorkFlowDAO"));
          wfDao = App.getApplicationContext().getBean("readOnlyWorkFlowDAO",WorkFlowDAO.class);
        }

        if (facDao == null) {
//            facDao = (FacilityDAO) (DAOFactory.getDAO("FacilityDAO"));
        	facDao = App.getApplicationContext().getBean("readOnlyFacilityDAO",FacilityDAO.class);
        }
    }

    /**
     * @return A LinkedHashMap of all defined transitionDefs.
     */
    public final LinkedHashMap<String, String> getTransitionsDef() {
        if (transitionTypes == null) {
            init();

            try {
                TransitionDef[] tempArray = wfDao.retrieveTransitionDef();

                transitionTypes = new LinkedHashMap<String, String>();
                for (TransitionDef tempState : tempArray) {
                    transitionTypes.put(tempState.getCode(), tempState
                            .getDescription());
                }
            } catch (DAOException de) {
                logger.error(de.getMessage());
            }
        }
        return transitionTypes;
    }

    /**
     * @return A LinkedHashMap of all defined ActivityStatusDefs.
     */
    public final LinkedHashMap<String, String> getActivityStatusDef() {
        if (activityStatus == null) {
            init();

            try {
                SimpleDef[] tempArray = wfDao.retrieveActivityStatusDef();

                activityStatus = new LinkedHashMap<String, String>();
                for (SimpleDef temp : tempArray) {
                    activityStatus.put(temp.getCode(), temp.getDescription());
                }
            } catch (DAOException de) {
                logger.error(de.getMessage());
            }
        }
        return activityStatus;
    }

    /**
     * @return A LinkedHashMap of all defined ProcessTypes.
     */
    public final LinkedHashMap<String, String> getProcessTypes() {
        getProcessTypesArray();

        LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();
        for (SimpleDef temp : processTypes) {
            ret.put(temp.getCode(), temp.getDescription());
        }

        return ret;
    }

    /**
     * @return An array of all defined ProcessTypes.
     */
    public final SimpleDef[] getProcessTypesArray() {
        if (processTypes == null) {
            init();

            try {
                processTypes = wfDao.retrieveProcessDefs();
            } catch (DAOException de) {
                logger.error(de.getMessage());
            }
        }

        return processTypes;
    }

    /**
     * @return An array of all defined FacilityRoles.
     */
    public final FacilityRoleDef[] getFacilityRoleArray() {
        if (facilityRoles == null) {
            init();

            try {
                facilityRoles = wfDao.retrieveFacilityRoleDefs();
            } catch (DAOException de) {
                logger.error(de.getMessage());
            }
        }

        return facilityRoles;
    }

    /**
     * @return A LinkedHashMap of all defined PerformerTypes.
     */
    public final LinkedHashMap<String, PerformerDef> getPerformerTypes() {
        getPerformerTypesArray();

        LinkedHashMap<String, PerformerDef> ret = new LinkedHashMap<String, PerformerDef>();
        for (PerformerDef temp : performerTypes) {
            ret.put(temp.getCode(), temp);
        }

        return ret;
    }

    /**
     * @return An array of all defined PerformerTypes.
     */
    public final PerformerDef[] getPerformerTypesArray() {
        if (performerTypes == null) {
            init();

            try {
                performerTypes = wfDao.retrievePerformerDefs();
            } catch (DAOException de) {
                logger.error(de.getMessage());
            }
        }

        return performerTypes;
    }
}
