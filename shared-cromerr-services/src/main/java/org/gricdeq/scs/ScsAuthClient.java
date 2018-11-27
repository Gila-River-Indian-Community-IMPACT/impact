package org.gricdeq.scs;

import javax.annotation.PostConstruct;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;

import org.gricdeq.scs.schema.auth._3.DomainTypeCode;
import org.gricdeq.scs.schema.auth._3.ObjectFactory;
import org.gricdeq.scs.schema.auth._3.Validate;
import org.gricdeq.scs.schema.auth._3.ValidateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

@Component
@Scope("prototype")
public class ScsAuthClient extends WebServiceGatewaySupport {

	private static final Logger log = LoggerFactory.getLogger(ScsAuthClient.class);
	
	@Autowired public ScsConfigurationProvider config;

	@PostConstruct
    private void init() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		this.setMarshaller(marshaller);
		this.setUnmarshaller(marshaller);
    }
	
    @Bean
    public WebServiceMessageFactory webServiceMessageFactory() throws SOAPException {
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        WebServiceMessageFactory webMessageFactory = new SaajSoapMessageFactory(messageFactory);
        return webMessageFactory;
    }
    
	public ValidateResponse validate(String adminId, String adminPassword, DomainTypeCode domainTypeCode, String securityToken,
			String clientIp, String resourceUri) {
		this.setDefaultUri(getConfig().getAuthServiceUri());
		((Jaxb2Marshaller)this.getMarshaller()).setContextPath("org.gricdeq.scs.schema.auth._3");
		ObjectFactory factory = new ObjectFactory();

		Validate validate = factory.createValidate();
		validate.setClientIp(clientIp);
		validate.setCredential(adminPassword);
		validate.setDomain(domainTypeCode);
		validate.setResourceURI(resourceUri);
		validate.setSecurityToken(securityToken);
		validate.setUserId(adminId);
		ValidateResponse validateResponse = null;
		
		try {
			getWebServiceTemplate().setMessageFactory(webServiceMessageFactory());
			validateResponse = (ValidateResponse)getWebServiceTemplate()
					.marshalSendAndReceive(getConfig().getAuthServiceUri(),
							validate,
							new SoapActionCallback("Validate"));
		} catch (SoapFaultClientException | SOAPException e) {
			log.error("Unable to validate security token: " + e,e); 
			throw new RuntimeException("soap fault: " + e);
		}
		return validateResponse;
	}


	public ScsConfigurationProvider getConfig() {
		return config;
	}

	public void setConfig(ScsConfigurationProvider config) {
		this.config = config;
	}
	
}