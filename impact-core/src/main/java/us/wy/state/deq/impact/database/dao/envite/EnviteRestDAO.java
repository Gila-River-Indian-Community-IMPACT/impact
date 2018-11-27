package us.wy.state.deq.impact.database.dao.envite;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.codehaus.jackson.map.ObjectMapper;

import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.app.contact.ExternalOrganization;
import us.wy.state.deq.impact.database.dao.EnviteDAO;
import us.wy.state.deq.impact.security.FederationUtil;

public class EnviteRestDAO implements EnviteDAO {

	// disable hostname verification on ssl connections, so we can run in the
	// ETS test/staging environment (where certs do not match hostnames)
	static {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				//TODO add external configuration switch to toggle this on/off
				return true;
			}
		});
	}

	@Override
	public ExternalOrganization[] retrieveEnviteOrgs(String enviteUsername) 
			throws DAOException {
			List<ExternalOrganization> enviteOrgs = new ArrayList<ExternalOrganization>();
			if (null != enviteUsername) {
				EnviteClient enviteClient = new EnviteClient();
				enviteOrgs = Arrays.asList(enviteClient.getOrganizations(enviteUsername)); //TODO inefficient array->list->array
			}
			return enviteOrgs.toArray(new ExternalOrganization[0]);
	}

	@Override
	public String registerDocument(String documentUrl, 
			String applicationDocumentId) throws DAOException {
		EnviteClient enviteClient = new EnviteClient();
		return enviteClient.registerDocument(documentUrl, applicationDocumentId);
	}
	
	@Override
	public String registerSignature(String documentId, 
			String organizationId, String username, 
			String redirectUrl) throws DAOException {
		EnviteClient enviteClient = new EnviteClient();
		return enviteClient.registerSignature(documentId, organizationId, username, redirectUrl);
	}
	
	
	public static class EnviteClient {
		private String getClaimsUri;
		private String getRequestedClaimsUri;
		private String registerDocumentUri;
		private String registerSignatureUri;
		private String applicationKey;
		private Logger logger;
		private RestClient restClient;
		
		public EnviteClient() {
			logger = Logger.getLogger(this.getClass());
			String restUriJndiName = Config.findNode("app.enviteRestServiceURI")
					.getAsString("jndiName");
			String applicationKeyJndiName = Config.findNode("app.enviteApplicationKey")
					.getAsString("jndiName");
			try {
				Context initContext = new InitialContext();
				Context envContext = (Context) initContext.lookup("java:/comp/env");
				String restUri = (String) envContext.lookup(restUriJndiName);
				getClaimsUri = restUri + "/External/GetClaims";
				getRequestedClaimsUri = restUri + "/External/GetRequestedClaims";
				registerDocumentUri = restUri + "/External/RegisterDocument";
				registerSignatureUri = restUri + "/External/RegisterSignature";
				applicationKey = (String) envContext.lookup(applicationKeyJndiName);
			} catch (NamingException ne) {
				throw new RuntimeException(ne);
			}
			restClient = new RestClient();
		}

		public ExternalOrganization[] getOrganizations(String username) throws DAOException {
			logger.debug("calling Rest service to get envite orgs for: " + username);
			List<ExternalOrganization> enviteOrgs = new ArrayList<ExternalOrganization>();
			try {
				ObjectMapper mapper = new ObjectMapper();
				Resource getClaims = restClient.resource(getClaimsUri);
				getClaims.accept(MediaType.APPLICATION_JSON,"text/json");
				String response = getClaims.post(String.class,"applicationKey=" + applicationKey + "&username=" + username);
				logger.debug("----> ENVITE REST post response = " + response);
				EnviteClaim[] claims = mapper.readValue(response, EnviteClaim[].class);
				logger.debug("----> ENVITE REST post response claims (decoded) = " + claims);
				enviteOrgs.addAll(transform(claims, true));

				Resource getRequestedClaims = restClient.resource(getRequestedClaimsUri);
				getRequestedClaims.accept(MediaType.APPLICATION_JSON,"text/json");
				String response2 = getRequestedClaims.post(String.class,"applicationKey=" + applicationKey + "&username=" + username);
				logger.debug("----> ENVITE REST post response (2) = " + response2);
				EnviteClaim[] requestedClaims = mapper.readValue(response2, EnviteClaim[].class);
				logger.debug("----> ENVITE REST post response claims -- decoded (2) = " + requestedClaims);
				enviteOrgs.addAll(transform(requestedClaims, false));
			} catch (Exception e) {
				throw new DAOException(
						"REST client threw exception while calling service: " + e.getMessage(),e);
			}
			return enviteOrgs.toArray(new ExternalOrganization[0]);
		}
		
		public String registerDocument(String documentUrl, 
				String applicationDocumentId) throws DAOException {
			EnviteRegistration reg = null;
			String reqEntity = "applicationKey=" + applicationKey + 
				"&documentUrl=" + documentUrl + 
				"&applicationDocumentId=" + applicationDocumentId;
			logger.debug("----> ENVITE REST request entity = " + reqEntity);
			try {
				ObjectMapper mapper = new ObjectMapper();
				Resource registerDocument = restClient.resource(registerDocumentUri);
				registerDocument.accept(MediaType.APPLICATION_JSON,"text/json");
				String response = registerDocument.post(String.class, reqEntity);
				logger.debug("----> ENVITE REST post response = " + response);
				reg = mapper.readValue(response, EnviteRegistration.class);
				logger.debug("----> ENVITE REST post response (decoded) = " + reg);
			} catch (Exception e) {
				throw new DAOException(
						"REST client threw exception while calling service: " + e.getMessage(),e);
			}
			if (null != reg) {
				if (reg.errorList.length > 0) {
					throw new DAOException("ENVITE registerDocument call had errors: " + Arrays.toString(reg.errorList));
				}
				if (!"true".equals(reg.isValid)) {
					throw new DAOException("ENVITE registerSignature call failed; isValid = " + reg.isValid);					
				}
			}
			return reg.newId;
		}
		
		public String registerSignature(String documentId, 
				String organizationId, String username, 
				String redirectUrl) throws DAOException {
			EnviteRegistration reg = null;
			String reqEntity;
			try {
				reqEntity = "applicationKey=" + applicationKey + 
						"&documentId=" + documentId + 
						"&organizationId=" + organizationId +
						"&username=" + username +
						"&redirectUrl=" + URLEncoder.encode(redirectUrl,"utf-8");
			} catch (UnsupportedEncodingException e1) {
				reqEntity = null;
			}
			logger.debug("----> ENVITE REST request entity = " + reqEntity);
			try {
				ObjectMapper mapper = new ObjectMapper();
				Resource registerSignature = restClient.resource(registerSignatureUri);
				registerSignature.accept(MediaType.APPLICATION_JSON,"text/json");
				String response = registerSignature.post(String.class, reqEntity);
				logger.debug("----> ENVITE REST post response = " + response);
				reg = mapper.readValue(response, EnviteRegistration.class);
				logger.debug("----> ENVITE REST post response (decoded) = " + reg);
			} catch (Exception e) {
				throw new DAOException(
						"REST client threw exception while calling service: " + e.getMessage(),e);
			}
			if (null != reg) {
				if (reg.errorList.length > 0) {
					throw new DAOException("ENVITE registerSignature call had errors: " + Arrays.toString(reg.errorList));
				}
				if (!"true".equals(reg.isValid)) {
					throw new DAOException("ENVITE registerSignature call failed; isValid = " + reg.isValid);					
				}
			}
			return reg.newId;
		}
		

		private List<ExternalOrganization> transform(EnviteClaim[] claims, 
				boolean claimRequestApproved) {
			List<ExternalOrganization> enviteOrgs = new ArrayList<ExternalOrganization>();
			String firstName = null;
			String lastName = null;
			String email = null;
			String username = null;
			for (EnviteClaim claim : claims) {
				logger.debug("------> claim = " + claim);
				if (FederationUtil.ENVITE_CLAIM_TYPE_ENVITE_ROLES.equals(claim.type)) {
					Collections.addAll(enviteOrgs, FederationUtil.getExternalOrganizations(claim.value,claimRequestApproved));
				} else if (FederationUtil.ENVITE_CLAIM_TYPE_FIRST_NAME.equals(claim.type)) {
					firstName = claim.value;
				} else if (FederationUtil.ENVITE_CLAIM_TYPE_LAST_NAME.equals(claim.type)) {
					lastName = claim.value;
				} else if (FederationUtil.ENVITE_CLAIM_TYPE_EMAIL_ADDRESS.equals(claim.type)) {
					email = claim.value;
				} else if (FederationUtil.ENVITE_CLAIM_TYPE_USERNAME.equals(claim.type)) {
					username = claim.value;
				}
			}
			for (ExternalOrganization enviteOrg : enviteOrgs) {
				
				//TODO redo this
				
//				enviteOrg.setContactFirstName(firstName);
//				enviteOrg.setContactLastName(lastName);
//				enviteOrg.setContactEmail(email);
//				enviteOrg.setContactUsername(username);
			}
			return enviteOrgs;
		}
	}
}
