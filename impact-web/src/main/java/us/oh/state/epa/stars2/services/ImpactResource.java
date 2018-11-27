package us.oh.state.epa.stars2.services;

import java.rmi.RemoteException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;


@Path("/impactservice")
public class ImpactResource {
	private Logger logger = Logger.getLogger(this.getClass());

	@GET
	@Path("/getSequence/{sequence}")
	public String nextSequenceVal(@PathParam("sequence")String sequence) throws DAOException {
		 Integer ret = null;
	        InfrastructureService infraBO = null;

	        try {
	           infraBO = ServiceFactory.getInstance().getInfrastructureService();
	           ret = infraBO.nextSequenceVal(sequence);
	        } catch (ServiceFactoryException sfe) {
	            logger.error(sfe.getMessage(), sfe);
	        } catch (RemoteException re) {
	           logger.error(re.getMessage(), re);
	        }

	        return ret.toString();
	}
	
	
	@POST
	@Path("/processSubmittedTask")
	 public void processSubmittedTask(byte[] fromXmlTask) throws DAOException {
        logger.debug("Web Service Process Submitted Starting.");
        logger.debug("fromXmlTask = " + new String(fromXmlTask));

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

	        	logger.debug("Exiting  normally.");
    }
	
	@POST
	@Path("/registerDocument")
	public String registerDocument(String path) throws DAOException {
		logger.debug("---> registerDocument");
        InfrastructureService infraBO;
		try {
			infraBO = ServiceFactory.getInstance().getInfrastructureService();
		} catch (ServiceFactoryException e) {
            String error = "Caught a ServiceFactoryException: message = " + e.getMessage();
            logger.error(error, e);
            throw new DAOException(error, e);
		}
        return infraBO.registerDocument(path);
		
	}
	
	@POST
	@Path("/registerSignature")
	public String registerSignature(
			@QueryParam("documentId") String documentId, 
			@QueryParam("organizationId") String organizationId, 
			@QueryParam("username") String username,
			@QueryParam("redirectUrl") String redirectUrl) throws DAOException {
		logger.debug("---> registerSignature");
        InfrastructureService infraBO;
		try {
			infraBO = ServiceFactory.getInstance().getInfrastructureService();
		} catch (ServiceFactoryException e) {
            String error = "Caught a ServiceFactoryException: message = " + e.getMessage();
            logger.error(error, e);
            throw new DAOException(error, e);
		}
        return infraBO.registerSignature(documentId,organizationId,username,redirectUrl);
	}
	
	@POST
	@Path("/processNewFacilityRequest")
	 public String processNewFacilityRequest(byte[] fromXmlTask) throws DAOException {
		logger.debug("Web Service Process Submitted New Facility Request Starting.");
        logger.debug("fromXmlTask = " + new String(fromXmlTask));
        String requestId = null;

	        try {
	            InfrastructureService infraBO = ServiceFactory.getInstance().getInfrastructureService();
	            requestId = infraBO.processNewFacilityRequest(fromXmlTask);
	        }
	        catch (ServiceFactoryException sfe) {
	            String error = "processNewFacilityRequest: Caught a ServiceFactoryException: message = " + sfe.getMessage();
	            logger.error(error, sfe);
	            throw new DAOException(error, sfe);
	        }
	        catch (DAOException daoe) {
	            String error = "processNewFacilityRequest: Caught an DAOEException: message = " + daoe.getMessage();
	            logger.error(error, daoe);
	            throw daoe;
	        }
	        catch (RemoteException re) {
	            String error = "processNewFacilityRequest: Caught an RemoteException: message = " + re.getMessage();
	            logger.error(error, re);
	            throw new DAOException(error, re);
	        }
	        catch (Exception e) {
	            String error = "processNewFacilityRequest: Caught an Exception of type " + e.getClass().getName() 
	                + ": message = " + e.getMessage();
	            logger.error(error, e);
	            throw new DAOException(error, e);
	        }

	        	logger.debug("Exiting  normally.");
	        	return requestId;
    }

}
