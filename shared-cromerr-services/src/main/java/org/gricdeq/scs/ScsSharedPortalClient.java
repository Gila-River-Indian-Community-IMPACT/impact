package org.gricdeq.scs;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.Source;

import org.gricdeq.scs.schema.sharedcromerr.portal._1.Authenticate;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.AuthenticateResponse;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.ObjectFactory;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.RetrieveRolesWithOrganizationsForDataflowAndPartner;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.RetrieveRolesWithOrganizationsForDataflowAndPartnerResponse;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.RetrieveUser;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.RetrieveUserResponse;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.SharedPortalFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import net.exchangenetwork.wsdl.sharedcromerr.portal._1.SharedPortalException;


@Component
@Scope("prototype")
public class ScsSharedPortalClient extends WebServiceGatewaySupport {

	private static final Logger log = LoggerFactory.getLogger(ScsSharedPortalClient.class);

	@Autowired public ScsConfigurationProvider config;
	
    @PostConstruct
    private void init() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		this.setMarshaller(marshaller);
		this.setUnmarshaller(marshaller);
    	this.setDefaultUri(getConfig().getSharedPortalServiceUri());
    	((Jaxb2Marshaller)this.getMarshaller()).setContextPath("org.gricdeq.scs.schema.sharedcromerr.portal._1");
    }
    
    @SuppressWarnings("unchecked")
	public AuthenticateResponse authenticate(String adminId, String adminPwd) throws SharedPortalException {
		ObjectFactory factory = new ObjectFactory();

		Authenticate authenticate = new Authenticate();
		authenticate.setUserId(adminId);
		authenticate.setCredential(adminPwd);

		JAXBElement<Authenticate> request = factory.createAuthenticate(authenticate);
		JAXBElement<AuthenticateResponse> response = null;
		try {
			response = (JAXBElement<AuthenticateResponse>) getWebServiceTemplate()
					.marshalSendAndReceive(getConfig().getSharedPortalServiceUri(),
							request,
							new SoapActionCallback("Authenticate"));
		} catch (SoapFaultClientException e) {
			log.error("SCS soap service authentication failed.  Please check the server configuration and ensure the SCS credentials are correct: " + e,e); 
			handleSoapFaultClientException(e);
			return null;
		}
		return response.getValue();
	}

    @SuppressWarnings("unchecked")
    public RetrieveRolesWithOrganizationsForDataflowAndPartnerResponse retrieveRolesWithOrganizationsForDataflowAndPartner(String securityToken, String user, 
    		String dataflow, String partner) throws SharedPortalException {
		ObjectFactory factory = new ObjectFactory();

		RetrieveRolesWithOrganizationsForDataflowAndPartner retrieveRolesWithOrganizationsForDataflowAndPartner = 
				new RetrieveRolesWithOrganizationsForDataflowAndPartner();
		retrieveRolesWithOrganizationsForDataflowAndPartner.setSecurityToken(securityToken);
		retrieveRolesWithOrganizationsForDataflowAndPartner.setUserId(user);
		retrieveRolesWithOrganizationsForDataflowAndPartner.setDataflow(dataflow);
		retrieveRolesWithOrganizationsForDataflowAndPartner.setPartnerId(partner);

		JAXBElement<RetrieveRolesWithOrganizationsForDataflowAndPartner> request = 
				factory.createRetrieveRolesWithOrganizationsForDataflowAndPartner(retrieveRolesWithOrganizationsForDataflowAndPartner);
		JAXBElement<RetrieveRolesWithOrganizationsForDataflowAndPartnerResponse> response = null;
		try {
			response = (JAXBElement<RetrieveRolesWithOrganizationsForDataflowAndPartnerResponse>) getWebServiceTemplate()
					.marshalSendAndReceive(getConfig().getSharedPortalServiceUri(),
							request,
							new SoapActionCallback(""));
		} catch (SoapFaultClientException e) {
			log.error("Unable to retrieve roles/organizations for dataflow/partner: " + e,e); 
			handleSoapFaultClientException(e);
			return null;
		}
		return response.getValue();
    }

    @SuppressWarnings("unchecked")
    public RetrieveUserResponse retrieveUser(String securityToken, String user) 
    		throws SharedPortalException {
		ObjectFactory factory = new ObjectFactory();

		RetrieveUser retrieveUser = new RetrieveUser();
		retrieveUser.setSecurityToken(securityToken);
		retrieveUser.setUserId(user);

		JAXBElement<RetrieveUser> request = 
				factory.createRetrieveUser(retrieveUser);
		JAXBElement<RetrieveUserResponse> response = null;
		try {
			response = (JAXBElement<RetrieveUserResponse>) getWebServiceTemplate()
					.marshalSendAndReceive(getConfig().getSharedPortalServiceUri(),
							request,
							new SoapActionCallback(""));
		} catch (SoapFaultClientException e) {
			log.error("Unable to retrieve portal user: " + e,e); 
			handleSoapFaultClientException(e);
			return null;
		}
		return response.getValue();
    }


	@SuppressWarnings("unchecked")
	private void handleSoapFaultClientException(SoapFaultClientException e) throws SharedPortalException {
		SoapFaultDetail soapFaultDetail = e.getSoapFault().getFaultDetail();
		SoapFaultDetailElement detailElementChild = 
				(SoapFaultDetailElement) soapFaultDetail.getDetailEntries().next();
		Source detailSource = detailElementChild.getSource();

		try {
			JAXBElement<SharedPortalFault> detail = 
					(JAXBElement<SharedPortalFault>) getUnmarshaller().unmarshal(detailSource);
			SharedPortalFault fault = detail.getValue();
			
		    throw new SharedPortalException(fault.getDescription(),fault);
		} catch (IOException e1) {
		    throw new RuntimeException("cannot unmarshal SOAP fault detail object: " + soapFaultDetail.getSource());
		}
	}

	public ScsConfigurationProvider getConfig() {
		return config;
	}

	public void setConfig(ScsConfigurationProvider config) {
		this.config = config;
	}
	

}