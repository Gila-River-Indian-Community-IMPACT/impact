package org.gricdeq.scs;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.Source;

import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.AnswerQuestion;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.AnswerQuestionResponse;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.AnswerType;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.Authenticate;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.AuthenticateResponse;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.CreateActivity;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.CreateActivityResponse;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.GetQuestion;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.GetQuestionResponse;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.ObjectFactory;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.PropertiesType;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.SharedCromerrFault;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.UserType;
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

import net.exchangenetwork.wsdl.sharedcromerr.secondfactorauth._1.SharedCromerrException;

@Component
@Scope("prototype")
public class ScsSecondFactorAuthenticationClient extends WebServiceGatewaySupport {

	private static final Logger log = LoggerFactory.getLogger(ScsSecondFactorAuthenticationClient.class);

	@Autowired public ScsConfigurationProvider config;
	
    @PostConstruct
    private void init() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		this.setMarshaller(marshaller);
		this.setUnmarshaller(marshaller);
    	this.setDefaultUri(getConfig().getSecondFactorAuthenticationServiceUri());
    	((Jaxb2Marshaller)this.getMarshaller()).setContextPath("org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1");
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
					.marshalSendAndReceive(getConfig().getSecondFactorAuthenticationServiceUri(),
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
	public GetQuestionResponse getQuestion(String securityToken, String activityId, UserType user) throws SharedCromerrException {
		ObjectFactory factory = new ObjectFactory();

		GetQuestion getQuestion = new GetQuestion();
		getQuestion.setSecurityToken(securityToken);
		getQuestion.setActivityId(activityId);
		getQuestion.setUser(user);

		JAXBElement<GetQuestion> request = factory.createGetQuestion(getQuestion);
		JAXBElement<GetQuestionResponse> response = null;
		try {
			response = (JAXBElement<GetQuestionResponse>) getWebServiceTemplate()
					.marshalSendAndReceive(getConfig().getSecondFactorAuthenticationServiceUri(),
							request,
							new SoapActionCallback("GetQuestion"));
		} catch (SoapFaultClientException e) {
			log.error("Unable to get security question: " + e,e);  //TODO this may be logging overkill
			handleSoapFaultClientException(e);
			return null;
		}
		return  response.getValue();
	}

	@SuppressWarnings("unchecked")
	public AnswerQuestionResponse answerQuestion(String securityToken, String activityId, 
			UserType user, AnswerType answer) throws SharedCromerrException {
		ObjectFactory factory = new ObjectFactory();

    	AnswerQuestion answerQuestion = new AnswerQuestion();
    	answerQuestion.setSecurityToken(securityToken);
    	answerQuestion.setActivityId(activityId);
		answerQuestion.setUser(user);
		answerQuestion.setAnswer(answer);

		JAXBElement<AnswerQuestion> request = factory.createAnswerQuestion(answerQuestion);
		JAXBElement<AnswerQuestionResponse> response = null;
		try {
			response = (JAXBElement<AnswerQuestionResponse>) getWebServiceTemplate()
					.marshalSendAndReceive(getConfig().getSecondFactorAuthenticationServiceUri(),
							request,
							new SoapActionCallback("AnswerQuestion"));
		} catch (SoapFaultClientException e) {
			log.error("Unable to answer security question: " + e,e);  //TODO this may be logging overkill
			handleSoapFaultClientException(e);
			return null;
		}
		return  response.getValue();
	}

	@SuppressWarnings("unchecked")
	public CreateActivityResponse createActivity(String securityToken, String dataflow, UserType user, 
			PropertiesType properties) throws SharedCromerrException {
		ObjectFactory factory = new ObjectFactory();

    	CreateActivity createActivity = new CreateActivity();
    	createActivity.setSecurityToken(securityToken);
    	createActivity.setUser(user);
    	createActivity.setDataflow(dataflow);
    	createActivity.setProperties(properties);

		JAXBElement<CreateActivity> request = factory.createCreateActivity(createActivity);
		JAXBElement<CreateActivityResponse> response = null;
		try {
			response = (JAXBElement<CreateActivityResponse>) getWebServiceTemplate()
					.marshalSendAndReceive(getConfig().getSecondFactorAuthenticationServiceUri(),
							request,
							new SoapActionCallback("CreateActivity"));
		} catch (SoapFaultClientException e) {
			log.error("Unable to create SCS soap service activity: " + e,e); 
			handleSoapFaultClientException(e);
			return null;
		}
		return  response.getValue();
	}
	
	public CreateActivityResponse createActivity(String adminToken, String scsDataflow,
			org.gricdeq.scs.schema.sharedcromerr.portal._1.UserType user, 
			PropertiesType properties) throws SharedCromerrException {
		UserType secondFactorAuthUser = transform(user);
		return createActivity(adminToken,scsDataflow,secondFactorAuthUser,properties);
	}

	private UserType transform(org.gricdeq.scs.schema.sharedcromerr.portal._1.UserType user) {
		UserType secondFactorAuthUser = new UserType();
		secondFactorAuthUser.setFirstName(user.getFirstName());
		secondFactorAuthUser.setLastName(user.getLastName());
		secondFactorAuthUser.setMiddleInitial(user.getMiddleInitial());
		secondFactorAuthUser.setUserId(user.getUserId());
		return secondFactorAuthUser;
	}

	@SuppressWarnings("unchecked")
	private void handleSoapFaultClientException(SoapFaultClientException e) throws SharedCromerrException {
		SoapFaultDetail soapFaultDetail = e.getSoapFault().getFaultDetail();
		SoapFaultDetailElement detailElementChild = 
				(SoapFaultDetailElement) soapFaultDetail.getDetailEntries().next();
		Source detailSource = detailElementChild.getSource();

		try {
			JAXBElement<SharedCromerrFault> detail = 
					(JAXBElement<SharedCromerrFault>) getUnmarshaller().unmarshal(detailSource);
			SharedCromerrFault fault = detail.getValue();
			
		    throw new SharedCromerrException(fault.getDescription(),fault);
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