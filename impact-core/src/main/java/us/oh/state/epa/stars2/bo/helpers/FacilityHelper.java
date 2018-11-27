package us.oh.state.epa.stars2.bo.helpers;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.alerts.Alert;
import us.oh.state.epa.portal.base.Constants;
import us.oh.state.epa.stars2.bo.BaseBO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.facility.EventLog;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.def.WorkflowProcessDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Transactional(rollbackFor=Exception.class)
@Service
public class FacilityHelper extends BaseBO {
	
    public EventLog createEventLog(EventLog el) throws DAOException {
    	
        EventLog ret = null;
        Boolean migration = SystemPropertyDef.getSystemPropertyValueAsBoolean("migration", false);
        
        if (migration != null && !migration){
            Transaction trans = TransactionFactory.createTransaction();
            FacilityDAO facDAO = facilityDAO(trans);

            try {
                // Add more info for event log base of the log type and external ID
                if (el.getExternalId() != null){
                    if (WorkflowProcessDef.PERMITTING.equalsIgnoreCase(el.getEventTypeDefCd())){
                        Permit tp = permitDAO().retrievePermit(el.getExternalId());
                        String ts = "";
                        if (tp != null){
                            ts = "( Permit Type : " + tp.getPermitType();
                            ts = ts + ", Permit Number : " + tp.getPermitNumber();
                            ts = ts + ", Permit Reason(s) : ";
                            for (String s : tp.getPermitReasonCDs()) {
                                s = PermitReasonsDef.getData().getItems().getItemDesc(s);
                                ts = ts + s + " ";
                            }
                            ts = ts + " )";
                        }
                        el.setNote(el.getNote() + ts);
                    }
                }
                
                ret = facDAO.createEventLog(el);
                createEventPortalAlert(el);
                trans.complete();
            } catch (DAOException e) {
                cancelTransaction(el.getNote(), trans, e);
            } finally { // Clean up our transaction stuff
                closeTransaction(trans);
            }
        }

        return ret;
    }
    
    public void createEventPortalAlert(EventLog el) {
 
    	String erMsg = "";

    	try {
            Boolean migration = SystemPropertyDef.getSystemPropertyValueAsBoolean("migration", false);
            if (migration != null && migration){
                return;
            }
            
            String facilityId = null;
            String corePlaceId = null;
            Facility facility = facilityDAO().retrieveFacilityData(el.getFpId());
            
            if (facility == null) {
                logger.error("Alert for event [" + el.getNote() + 
                        "] was not sent since facility not found for FP ID " + el.getFpId());
                return;
            }
            
            facilityId = facility.getFacilityId();
            if (facility.getCorePlaceId() == null) {
                logger.debug("Alert for event [" + el.getNote() + "] was not sent since Core Place ID is null.");
                return;
            }
            
            corePlaceId = facility.getCorePlaceId().toString();
            
            InfrastructureDAO infraDAO = infrastructureDAO();
            String userName = infraDAO.retrieveUserName(el.getUserId());
            
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = new GregorianCalendar();
            endDate.setTimeInMillis(startDate.getTimeInMillis());
            endDate.add(Calendar.DAY_OF_MONTH, 3);
            erMsg = "create event portal alert failed (Air Services Facility ID " + 
                facilityId + ", corePlaceId " + corePlaceId + "): ";
            Alert newAlert = new Alert(Constants.ALERT_TYPE_PLACE, // type
                    Constants.ALERT_PRIORITY_NORMAL, // priority
                    Constants.ALERT_STATUS_ACTIVE, // status
                    "Air Services Facility ID " + facilityId + ": " + el.getNote(), // message
                    Constants.SERVICE_DAPC,
                    startDate,
                    endDate,
                    corePlaceId // facility core ID
                    );
                                       
// MANTIS # 3203  This no longer works.
//            EventService eventService = ServiceFactory.getInstance().getEventService();
//            eventService.createAlert(userName, newAlert);
        } catch (RemoteException e) {
            logger.error(erMsg + e.getMessage(), e);
        } 
//        catch (ServiceFactoryException sfe) {
//            logger.error(erMsg + sfe.getMessage(), sfe);
//        }
        catch (Exception e) {
            logger.error(erMsg + e.getMessage(), e);
		}
 
//        logger.debug("createEventPortalAlert for event [" + el.getNote() + "] was successful.");
    }
}
