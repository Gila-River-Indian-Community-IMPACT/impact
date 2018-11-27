package org.gricdeq.scs;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.Source;

import org.gricdeq.scs.schema.sharedcromerr.signandcor._1.Authenticate;
import org.gricdeq.scs.schema.sharedcromerr.signandcor._1.AuthenticateResponse;
import org.gricdeq.scs.schema.sharedcromerr.signandcor._1.DocumentType;
import org.gricdeq.scs.schema.sharedcromerr.signandcor._1.ObjectFactory;
import org.gricdeq.scs.schema.sharedcromerr.signandcor._1.SharedCromerrFault;
import org.gricdeq.scs.schema.sharedcromerr.signandcor._1.SignAndStoreCor;
import org.gricdeq.scs.schema.sharedcromerr.signandcor._1.SignAndStoreCorResponse;
import org.gricdeq.scs.schema.sharedcromerr.signandcor._1.SignatureDataType;
import org.gricdeq.scs.schema.sharedcromerr.signandcor._1.UserType;
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

import net.exchangenetwork.wsdl.sharedcromerr.signandcor._1.SharedCromerrException;

@Component
@Scope("prototype")
public class ScsSignatureAndCorClient extends WebServiceGatewaySupport {

	private static final Logger log = LoggerFactory.getLogger(ScsSignatureAndCorClient.class);
	
	@Autowired public ScsConfigurationProvider config;

    @PostConstruct
    private void init() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		this.setMarshaller(marshaller);
		this.setUnmarshaller(marshaller);
    	this.setDefaultUri(getConfig().getSignatureAndCorServiceUri());
    	((Jaxb2Marshaller)this.getMarshaller()).setContextPath("org.gricdeq.scs.schema.sharedcromerr.signandcor._1");
    }
    
    @SuppressWarnings("unchecked")
	public AuthenticateResponse authenticate(String adminId, String adminPwd) throws SharedCromerrException {
    	ObjectFactory factory = new ObjectFactory();

		Authenticate authenticate = new Authenticate();
		authenticate.setAdminId(adminId);
		authenticate.setCredential(adminPwd);

		JAXBElement<Authenticate> request = factory.createAuthenticate(authenticate);
		JAXBElement<AuthenticateResponse> response = null;
		try {
			response = (JAXBElement<AuthenticateResponse>) getWebServiceTemplate()
					.marshalSendAndReceive(getConfig().getSignatureAndCorServiceUri(),
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
	public SignAndStoreCorResponse signAndStoreCor(String adminToken, String activityId, UserType user, DocumentType document,
			SignatureDataType signatureData) throws SharedCromerrException {
    	ObjectFactory factory = new ObjectFactory();

		SignAndStoreCor signAndStoreCor = new SignAndStoreCor();
		signAndStoreCor.setActivityId(activityId);
		signAndStoreCor.setDocument(document);
		signAndStoreCor.setSecurityToken(adminToken);
		signAndStoreCor.setSignatureData(signatureData);
		signAndStoreCor.setUser(user);

		JAXBElement<SignAndStoreCor> request = factory.createSignAndStoreCor(signAndStoreCor);
		JAXBElement<SignAndStoreCorResponse> response = null;
		try {
			response = (JAXBElement<SignAndStoreCorResponse>) getWebServiceTemplate()
					.marshalSendAndReceive(getConfig().getSignatureAndCorServiceUri(),
							request,
							new SoapActionCallback("SignAndStoreCOR"));
		} catch (SoapFaultClientException e) {
			log.error("Unable to e-sign and store copy of record: " + e,e); 
			handleSoapFaultClientException(e);
			return null;
		}
		return response.getValue();
	}
    
    

	@SuppressWarnings("unchecked")
	private void handleSoapFaultClientException(SoapFaultClientException e) throws SharedCromerrException {
		SoapFaultDetail soapFaultDetail = e.getSoapFault().getFaultDetail();
		if (null != soapFaultDetail) {
			SoapFaultDetailElement detailElementChild = 
					(SoapFaultDetailElement) soapFaultDetail.getDetailEntries().next();
			Source detailSource = detailElementChild.getSource();
	
			try {
				JAXBElement<SharedCromerrFault> detail = 
						(JAXBElement<SharedCromerrFault>) getUnmarshaller().unmarshal(detailSource);
				SharedCromerrFault fault = detail.getValue();
				
			    throw new SharedCromerrException(fault.getDescription(),fault);
			} catch (IOException e1) {
				String msg = "cannot unmarshal SOAP fault detail object: " + soapFaultDetail.getSource();
				log.error(msg,e);
			    throw new RuntimeException(msg);
			}
		} else {
			String msg = "cannot extract SOAP fault detail object.  possible fault reason: " + e.getFaultStringOrReason();
			log.error(msg,e);
		    throw new RuntimeException(msg);
		}
	}

	public ScsConfigurationProvider getConfig() {
		return config;
	}

	public void setConfig(ScsConfigurationProvider config) {
		this.config = config;
	}
	
}