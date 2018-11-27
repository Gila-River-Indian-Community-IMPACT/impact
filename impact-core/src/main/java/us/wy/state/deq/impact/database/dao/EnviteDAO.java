package us.wy.state.deq.impact.database.dao;

import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.app.contact.ExternalOrganization;

public interface EnviteDAO {

	ExternalOrganization[] retrieveEnviteOrgs(String enviteUsername) throws DAOException;
	
	String registerDocument(String documentUrl, 
			String applicationDocumentId) throws DAOException;	
	
	String registerSignature(String documentId, 
			String organizationId, String username, 
			String redirectUrl) throws DAOException;
}
