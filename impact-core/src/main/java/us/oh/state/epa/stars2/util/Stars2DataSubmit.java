package us.oh.state.epa.stars2.util;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.log4j.Logger;

import us.oh.state.epa.portal.base.Constants;
import us.oh.state.epa.portal.datasubmit.DataSubmit;
import us.oh.state.epa.portal.datasubmit.IDataIntegration;
import us.oh.state.epa.portal.exception.EPAGatewayException;

public class Stars2DataSubmit implements IDataIntegration {
	protected transient Logger logger;
	
	public Stars2DataSubmit() {
	    logger = Logger.getLogger(this.getClass());
	}
	
	/**
     * Returns the name of the data integration implementation.  This
     * should be something an end user would understand.
     */
    public String getName() {
    	return "Stars2 Data Submit Process.";
    }

    /**
     * Processes the data submission XML data.  This method is passed
     * the original DataSubmit object that contains the XML data to
     * process.
     * @param dataSubmit The original DataSubmit object.
     * @throws EPAGatewayException
     */
    public void process(DataSubmit dataSubmit) throws Exception {
    	logger.error("method not implemented");
//    	logger.debug("processing Stars2 data submit begins...");
//    	PortalClientService portalBO;
//    	byte[] data = dataSubmit.getData();
//    	if (data != null) {
//    		try {
//                portalBO = getPortalClientService();
//                portalBO.processSubmittedTask(data);
//    		} catch (RemoteException re) {
//    			logger.error(re.getMessage(), re);
//    			throw new Exception(re.getMessage());
//    		}
//    	}
//    	logger.debug("processing Stars2 data submit completed successfully");
    }

    /**
     * Looks for the service and command this implementation processes.
     * If the implementation does not process this combination of service
     * and command then 'false' must be returned.  If 'true' is returned
     * then the data integration framework will call the process() method.
     * @param service The service name to compare
     * @param command The command to compare
     * @return boolean
     */
    public boolean isValidService(String service, String command) {
      if ( (service != null && service.equals(Constants.SERVICE_DAPC)) &&
          (command != null && command.equalsIgnoreCase(Constants.DATASUBMIT_FP_COMMAND_SUBMIT)) ) {
    	  logger.debug("Validation of Stars2 data submit returns true");
        return true;
      }
      logger.debug("Validation of Stars2 data submit returns false");
      return false;
    }
	
//    private PortalClientService getPortalClientService() {
//        PortalClientService portalClientService = null;
//
//        Hashtable<String, String> prop = new Hashtable<String, String>();
//        prop.put(Context.INITIAL_CONTEXT_FACTORY,
//                "weblogic.jndi.WLInitialContextFactory");
//
//        try {
//            InitialContext ctx = new InitialContext(prop);
//
//            Object objref = ctx.lookup(PortalClientRemoteHome.JNDI_NAME);
//
//            EJBHome homeObj = (EJBHome) PortableRemoteObject.narrow(objref,
//                    PortalClientRemoteHome.class);
//
//            // call the standard create() method to retrieve the remote
//            // interface
//            Method createMeth = ((Object) homeObj).getClass().getMethod(
//                    "create", (Class[]) null);
//
//            portalClientService = (PortalClientService) createMeth.invoke(
//                    homeObj, (Object[]) null);
//        } catch (NamingException ne) {
//            logger.error(ne.getMessage(), ne);
//        } catch (NoSuchMethodException nsme) {
//            logger.error(nsme.getMessage(), nsme);
//        } catch (InvocationTargetException ite) {
//            logger.error(ite.getMessage(), ite);
//        } catch (IllegalAccessException iae) {
//            logger.error(iae.getMessage(), iae);
//        }
//
//        return portalClientService;
//    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
    }
}
