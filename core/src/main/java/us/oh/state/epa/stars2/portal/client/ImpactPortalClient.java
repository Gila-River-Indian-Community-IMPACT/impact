package us.oh.state.epa.stars2.portal.client;

import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.handlers.BasicAuthSecurityHandler;

import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public class ImpactPortalClient {
	
	String restURI;
	
	private static final String REST_API_USER =  (String) Config.getEnvEntry("app/internalRestServiceUser", "INTERNAL_REST_SERVICE_USER_NOT_FOUND");
	private static final String REST_API_PWD =  (String) Config.getEnvEntry("app/internalRestServicePwd", "INTERNAL_REST_SERVICE_PWD_NOT_FOUND");
	
	private Logger logger = Logger.getLogger(this.getClass());

	public ImpactPortalClient() {
		
		String restURIJndiName = Config.findNode("app.internalRestServiceURI")
				.getAsString("jndiName");
		
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			restURI = (String) envContext.lookup(restURIJndiName);
		} catch (NamingException ne) {
			throw new RuntimeException(ne);
		}
	}

	public Integer getSequence(String sequence) throws DAOException {
		
		logger.debug("calling Rest service to get next sequence.");

		ClientConfig clientConfig = new ClientConfig();
		Integer cTimeout = SystemPropertyDef.getSystemPropertyValueAsInteger("restConnectionTimeout", 120000);
		clientConfig = clientConfig.connectTimeout(cTimeout);
		Integer rTimeout = SystemPropertyDef.getSystemPropertyValueAsInteger("restReadTimeout", 120000);
		clientConfig = clientConfig.readTimeout(rTimeout);
		provideCredentials(clientConfig);
		
		RestClient client = new RestClient(clientConfig);
		

		Resource resource = client.resource(restURI
				+ "impactservice/getSequence/" + sequence);
		String nextSequence;
		try {
			nextSequence = resource.accept(MediaType.TEXT_PLAIN).get(
					String.class);
		} catch (Exception e) {
			throw new DAOException(
					"REST client threw exception while calling service: " + e);
		}
		return Integer.parseInt(nextSequence);

	}

	private void provideCredentials(ClientConfig clientConfig) {
		BasicAuthSecurityHandler basicAuthSecHandler = new BasicAuthSecurityHandler(); 
		basicAuthSecHandler.setUserName(REST_API_USER); 
		basicAuthSecHandler.setPassword(REST_API_PWD); 
		clientConfig.handlers(basicAuthSecHandler);
	}

	public void processSubmittedTask(byte[] fromXmlTask) throws DAOException {
		
		logger.debug("calling Rest service to process submitted task.");

		ClientConfig clientConfig = new ClientConfig();
		Integer cTimeout = SystemPropertyDef.getSystemPropertyValueAsInteger("restConnectionTimeout", 120000);
		clientConfig = clientConfig.connectTimeout(cTimeout);
		Integer rTimeout = SystemPropertyDef.getSystemPropertyValueAsInteger("restReadTimeout", 120000);
		clientConfig = clientConfig.readTimeout(rTimeout);
		provideCredentials(clientConfig);
		
		RestClient client = new RestClient(clientConfig);
		Resource resource = client.resource(restURI
				+ "impactservice/processSubmittedTask");
		ClientResponse response = resource.accept(MediaType.TEXT_PLAIN).post(
				fromXmlTask);
		logger.debug("processSubmittedTask response status code = "
				+ response.getStatusCode());
		if (response.getStatusType().getFamily() != SUCCESSFUL) {
			throw new DAOException(
					"REST service call returned status code: "
							+ response.getStatusCode() + " "
							+ response.getStatusType().getReasonPhrase());
		}
	}

	public String registerDocument(String path) throws DAOException {
		
		logger.debug("---> registerDocument");
		String documentId = null;
		
		logger.debug("calling Rest service to register document.");

		ClientConfig clientConfig = new ClientConfig();
		Integer cTimeout = SystemPropertyDef.getSystemPropertyValueAsInteger("restConnectionTimeout", 120000);
		clientConfig = clientConfig.connectTimeout(cTimeout);
		Integer rTimeout = SystemPropertyDef.getSystemPropertyValueAsInteger("restReadTimeout", 120000);
		clientConfig = clientConfig.readTimeout(rTimeout);
		provideCredentials(clientConfig);
		
		RestClient client = new RestClient(clientConfig);
		Resource resource = client.resource(restURI
				+ "impactservice/registerDocument");
		ClientResponse response = resource.accept(MediaType.TEXT_PLAIN).post(
				path);
		logger.debug("registerDocument response status code = "
				+ response.getStatusCode());
		
		if (response.getStatusType().getFamily() != SUCCESSFUL) {
			throw new DAOException(
					"REST service call returned status code: "
							+ response.getStatusCode() + " "
							+ response.getStatusType().getReasonPhrase());
		}
		
		documentId = response.getEntity(String.class);
		return documentId;
	}

	public String registerSignature(String documentId, String organizationId, 
			String username, String redirectUrl) throws DAOException {
		
		logger.debug("---> registerSignature");
		String signatureId = null;
		
		logger.debug("calling Rest service to register signature.");

		ClientConfig clientConfig = new ClientConfig();
		Integer cTimeout = SystemPropertyDef.getSystemPropertyValueAsInteger("restConnectionTimeout", 120000);
		clientConfig = clientConfig.connectTimeout(cTimeout);
		Integer rTimeout = SystemPropertyDef.getSystemPropertyValueAsInteger("restReadTimeout", 120000);
		clientConfig = clientConfig.readTimeout(rTimeout);
		provideCredentials(clientConfig);
		
		RestClient client = new RestClient(clientConfig);
		Resource resource = client.resource(restURI + 
				"impactservice/registerSignature?documentId=" + documentId +
				"&organizationId=" + organizationId +
				"&username=" + username +
				"&redirectUrl=" + redirectUrl);
		ClientResponse response = resource.accept(MediaType.TEXT_PLAIN).post(null);
		logger.debug("registerSignature response status code = "
				+ response.getStatusCode());
		if (response.getStatusType().getFamily() != SUCCESSFUL) {
			throw new DAOException(
					"REST service call returned status code: "
							+ response.getStatusCode() + " "
							+ response.getStatusType().getReasonPhrase());
		}
		
		signatureId = response.getEntity(String.class);
		
		return signatureId;
		
		
	}
	
	public String processNewFacilityRequest(byte[] fromXmlTask) throws DAOException {
		
		String requestId = null;
		logger.debug("calling Rest service to process submitted new facility request.");

		ClientConfig clientConfig = new ClientConfig();
		Integer cTimeout = SystemPropertyDef.getSystemPropertyValueAsInteger("restConnectionTimeout", 120000);
		clientConfig = clientConfig.connectTimeout(cTimeout);
		Integer rTimeout = SystemPropertyDef.getSystemPropertyValueAsInteger("restReadTimeout", 120000);
		clientConfig = clientConfig.readTimeout(rTimeout);
		provideCredentials(clientConfig);
		
		RestClient client = new RestClient(clientConfig);
		Resource resource = client.resource(restURI
				+ "impactservice/processNewFacilityRequest");
		ClientResponse response = resource.accept(MediaType.TEXT_PLAIN).post(
				fromXmlTask);
		logger.debug("processNewFacilityRequest response status code = "
				+ response.getStatusCode());
		if (response.getStatusType().getFamily() != SUCCESSFUL) {
			throw new DAOException(
					"REST service call returned status code: "
							+ response.getStatusCode() + " "
							+ response.getStatusType().getReasonPhrase());
		}
		
		requestId = response.getEntity(String.class);
		return requestId;
	}

}
