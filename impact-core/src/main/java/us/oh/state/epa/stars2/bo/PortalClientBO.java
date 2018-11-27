package us.oh.state.epa.stars2.bo;

import java.rmi.RemoteException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.util.FacilityIdReference;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

@Transactional(rollbackFor=Exception.class)
@Service
public class PortalClientBO extends BaseBO implements PortalClientService {
    /**
     * @return 
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public void processSubmittedTask(byte[] fromXmlTask) throws DAOException {

        logger.debug("Starting.");

        try {
            InfrastructureService infraBO = ServiceFactory.getInstance().getInfrastructureService();
            infraBO.processSubmittedTask(fromXmlTask);
        }
        catch (ServiceFactoryException sfe) {
            String error = "Caught a ServiceFactoryException: message = " + sfe.getMessage();
            logger.error(error, sfe);
            throw new DAOException(error, sfe);
        }
        catch (DAOException daoe) {
            String error = "Caught an DAOEException: message = " + daoe.getMessage();
            logger.error(error, daoe);
            throw daoe;
        }
        catch (RemoteException re) {
            String error = "Caught an RemoteException: message = " + re.getMessage();
            logger.error(error, re);
            throw new DAOException(error, re);
        }
        catch (Exception e) {
            String error = "Caught an Exception of type " + e.getClass().getName() 
                + ": message = " + e.getMessage();
            logger.error(error, e);
            throw new DAOException(error, e);
        }

        logger.debug("Exiting normally.");
    }
    
    /**
     * 
     * @ejb.interface-method view-type="remote" 
     * @ejb.transaction type="Required"
     */
       public String getFacilityId(FacilityIdReference facilityIdRef) throws DAOException {
        FacilityService facilityBO = null;
        String ret = null;
/*        FacilityIdRef tempRef = new FacilityIdRef();
        
        tempRef.setCityCd(facilityIdRef.getCityCd());
        tempRef.setCityName(facilityIdRef.getCityName());
        tempRef.setCountyCd(facilityIdRef.getCountyCd());
        tempRef.setDolaCd(facilityIdRef.getDolaCd());
        tempRef.setDolaId(facilityIdRef.getDolaId());*/

        try {
            facilityBO = ServiceFactory.getInstance().getFacilityService();
            ret = facilityBO.getNewFacilityId();
        } catch (ServiceFactoryException sfe) {
            logger.error(sfe.getMessage(), sfe);
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
        }

        return ret;
    }
       
       /**
         * 
         * @ejb.interface-method view-type="remote"
         * @ejb.transaction type="Required"
         */
    public String getFacilityId(String cityCd, String cityName, String countyCd, String doLaaCd, String doLaaId)
            throws DAOException {
        FacilityService facilityBO = null;
        String ret = null;
/*        FacilityIdRef tempRef = new FacilityIdRef();

        tempRef.setCityCd(cityCd);
        tempRef.setCityName(cityName);
        tempRef.setCountyCd(countyCd);
        tempRef.setDolaCd(doLaaCd);
        tempRef.setDolaId(doLaaId);
*/
        try {
            facilityBO = ServiceFactory.getInstance().getFacilityService();
            ret = facilityBO.getNewFacilityId();
        } catch (ServiceFactoryException sfe) {
            logger.error(sfe.getMessage(), sfe);
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
        }

        return ret;
    }
          
    /**
     * @param sequenceId
     * @return
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Integer nextSequenceVal(String sequenceId) throws DAOException {
        Integer ret = null;
        InfrastructureService infraBO = null;

        try {
            infraBO = ServiceFactory.getInstance().getInfrastructureService();
            ret = infraBO.nextSequenceVal(sequenceId);
        } catch (ServiceFactoryException sfe) {
            logger.error(sfe.getMessage(), sfe);
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
        }

        return ret;
    }
    
    /**
     * @return 
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public String processNewFacilityRequest(byte[] fromXmlTask) throws DAOException {
    	String requestId = null;

        logger.debug("Starting processNewFacilityRequest.");

        try {
            InfrastructureService infraBO = ServiceFactory.getInstance().getInfrastructureService();
            requestId = infraBO.processNewFacilityRequest(fromXmlTask);
        }
        catch (ServiceFactoryException sfe) {
            String error = "Caught a ServiceFactoryException: message = " + sfe.getMessage();
            logger.error(error, sfe);
            throw new DAOException(error, sfe);
        }
        catch (DAOException daoe) {
            String error = "Caught an DAOEException: message = " + daoe.getMessage();
            logger.error(error, daoe);
            throw daoe;
        }
        catch (RemoteException re) {
            String error = "Caught an RemoteException: message = " + re.getMessage();
            logger.error(error, re);
            throw new DAOException(error, re);
        }
        catch (Exception e) {
            String error = "Caught an Exception of type " + e.getClass().getName() 
                + ": message = " + e.getMessage();
            logger.error(error, e);
            throw new DAOException(error, e);
        }

        logger.debug("Exiting normally - processNewFacilityRequest.");
        return requestId;
    }
}
