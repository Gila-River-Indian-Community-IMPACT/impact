package us.oh.state.epa.stars2.portal.bean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.gricdeq.impact.ScsExceptionHandler;
import org.gricdeq.scs.ScsSecondFactorAuthenticationClient;
import org.gricdeq.scs.ScsSharedPortalClient;
import org.gricdeq.scs.ScsSignatureAndCorClient;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.RetrieveUserResponse;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.SharedPortalErrorCode;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.SharedPortalFault;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.UserType;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.AnswerType;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.AuthenticateResponse;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.CreateActivityResponse;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.GetQuestionResponse;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.QuestionType;
import org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.SharedCromerrFault;
import org.gricdeq.scs.schema.sharedcromerr.signandcor._1.DocumentFormatType;
import org.gricdeq.scs.schema.sharedcromerr.signandcor._1.DocumentType;
import org.gricdeq.scs.schema.sharedcromerr.signandcor._1.SignAndStoreCorResponse;
import org.gricdeq.scs.schema.sharedcromerr.signandcor._1.SignatureDataType;
import org.jfree.util.Log;

import net.exchangenetwork.wsdl.sharedcromerr.portal._1.SharedPortalException;
import net.exchangenetwork.wsdl.sharedcromerr.secondfactorauth._1.SharedCromerrException;
import oracle.adf.view.faces.context.AdfFacesContext;
import oracle.adf.view.faces.event.ReturnEvent;
import oracle.adf.view.faces.model.UploadedFile;
import us.oh.state.epa.portal.base.Constants;
import us.oh.state.epa.portal.datasubmit.Attachment;
import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.ApplicationService;
import us.oh.state.epa.stars2.bo.ComplianceReportService;
import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.bo.RelocateRequestService;
import us.oh.state.epa.stars2.bo.StackTestService;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.Task.TaskType;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TmpDocument;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.portal.application.ApplicationDetail;
import us.oh.state.epa.stars2.portal.compliance.ComplianceReports;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;
import us.oh.state.epa.stars2.webcommon.bean.ConfirmWindow;
import us.oh.state.epa.stars2.webcommon.reports.ReportSearch;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.webcommon.monitoring.MonitorGroupDetail;

@SuppressWarnings("serial")
public class SubmitTask extends ConfirmWindow {
	private static final String CONFIRM_RO_STATUS = "dialog:confirmROStatus";
	private static final String ATTEST_OUTCOME = "dialog:confirmAttestation";
	private static final String PIN_OUTCOME = "dialog:confirmSubmitTaskPin";
	private static final String NON_RO_SUBMISSION_OUTCOME = "dialog:confirmNonROSubmission";
	private static final int ATTACHMENT_SIZE_THRESHOLD = 50000000;

	private Task task;
	private String pin;
	private boolean logUserOff = false;
	private boolean logUserOffFlag = false;
	private boolean pinNeeded;
	private String popupRedirect;
	private String submitedTaskId;
	private boolean completed;
	private String completeMessage;
	private QuestionType securityQuestion = null;
	private String securityQuestionAnswer;
	private String userProvidedSecurityAnswer;
	private boolean nonROSubmission = false;
	private UploadedFile uploadFile;
	private UploadedFileInfo uploadFileInfo;
	private Document roAttestationDoc;
	private boolean dapcAttestationRequired = true;
	private boolean roSubmissionRequired = true;
	private int popupCount = 0;
	private int submissionAttachmentSize;
	private boolean passedSecurityAnswer = false;
	private String alertMessage;
	private List<Document> documents;
	private String password;

	private ApplicationService applicationService;
    private ComplianceReportService complianceReportService;
    private EmissionsReportService emissionsReportService;
	private FacilityService facilityService;
	private InfrastructureService infrastructureService;
	private RelocateRequestService relocateRequestService;
	private ApplicationDetail applicationDetail;
	private StackTestService stackTestService;
	
	private ScsSecondFactorAuthenticationClient scsSecondFactorAuthenticationClient;
	private ScsSharedPortalClient scsSharedPortalClient;
	private ScsSignatureAndCorClient scsSignatureAndCorClient;

	//TODO move these to a common location
	private static final String SCS_ADMIN_ID = (String) Config.getEnvEntry("app/scsAdminId", "SCS_ADMIN_ID_NOT_FOUND");
	private static final String SCS_ADMIN_PWD = (String) Config.getEnvEntry("app/scsAdminPassword",
			"SCS_ADMIN_PWD_NOT_FOUND");
	private static final String SCS_DATAFLOW = (String) Config.getEnvEntry("app/scsDataflow", "SCS_DATAFLOW_NOT_FOUND");

	public SubmitTask() {
		super();
		this.scsSecondFactorAuthenticationClient = App.getApplicationContext().getBean(ScsSecondFactorAuthenticationClient.class);
		this.scsSharedPortalClient = App.getApplicationContext().getBean(ScsSharedPortalClient.class);
		this.scsSignatureAndCorClient = App.getApplicationContext().getBean(ScsSignatureAndCorClient.class);
	}
	
	public ScsSignatureAndCorClient getScsSignatureAndCorClient() {
		return scsSignatureAndCorClient;
	}

	public void setScsSignatureAndCorClient(ScsSignatureAndCorClient scsSignatureAndCorClient) {
		this.scsSignatureAndCorClient = scsSignatureAndCorClient;
	}

	public ScsSharedPortalClient getScsSharedPortalClient() {
		return scsSharedPortalClient;
	}

	public void setScsSharedPortalClient(ScsSharedPortalClient scsSharedPortalClient) {
		this.scsSharedPortalClient = scsSharedPortalClient;
	}

	public ScsSecondFactorAuthenticationClient getScsSecondFactorAuthenticationClient() {
		return scsSecondFactorAuthenticationClient;
	}

	public void setScsSecondFactorAuthenticationClient(
			ScsSecondFactorAuthenticationClient scsSecondFactorAuthenticationClient) {
		this.scsSecondFactorAuthenticationClient = scsSecondFactorAuthenticationClient;
	}

	public String getUserProvidedSecurityAnswer() {
		return userProvidedSecurityAnswer;
	}

	public void setUserProvidedSecurityAnswer(String userProvidedSecurityAnswer) {
		this.userProvidedSecurityAnswer = userProvidedSecurityAnswer;
	}

	public ApplicationDetail getApplicationDetail() {
		return applicationDetail;
	}

	public void setApplicationDetail(ApplicationDetail applicationDetail) {
		this.applicationDetail = applicationDetail;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public RelocateRequestService getRelocateRequestService() {
		return relocateRequestService;
	}

	public void setRelocateRequestService(
			RelocateRequestService relocateRequestService) {
		this.relocateRequestService = relocateRequestService;
	}

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}

	public void setEmissionsReportService(
			EmissionsReportService emissionsReportService) {
		this.emissionsReportService = emissionsReportService;
	}

	public ComplianceReportService getComplianceReportService() {
		return complianceReportService;
	}

	public void setComplianceReportService(
			ComplianceReportService complianceReportService) {
		this.complianceReportService = complianceReportService;
	}
	
	public StackTestService getStackTestService() {
		return stackTestService;
	}

	public void setStackTestService(
			StackTestService stackTestService) {
		this.stackTestService = stackTestService;
	}
	public String confirm() {
		logger.debug("Checking submission status");
		String ret = CONFIRM_RO_STATUS;
		if (!roSubmissionRequired) {
			logger.debug("RO Submission is not required.");
			dapcAttestationRequired = false;
			ret = ATTEST_OUTCOME;
		} else if (nonROSubmission) {
			logger.debug("Non RO submission where RO submission is required.");
			pin = null;
			securityQuestionAnswer = null;
			uploadFileInfo = null;
			ret = NON_RO_SUBMISSION_OUTCOME;
		}
		setAttestationMessage();
		logger.debug("Navigating to submission popup: " + ret);
		return ret;
	}

	public void returnSelected(ReturnEvent actionEvent) {
		if (roSubmissionRequired) {
			AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
		} else {
			FacesUtil.returnFromDialogAndRefresh();
		}
	}
	
	public void returnFromDialog(ReturnEvent actionEvent) {
		FacesUtil.setOutcome(null, "facilityProfile");	
	}

	public void returnFirstSelected(ReturnEvent actionEvent) {
		if(task.getTaskType().equals(Task.TaskType.MRPT)) {
			AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
		} else {
			FacesUtil.returnFromDialogAndRefresh();
		}
	}

	public ApplicationService getApplicationService() {
		return applicationService;
	}

	public void setApplicationService(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public final String processConfirmAttest() {
		if (getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
			return null;
		}
		pin = null;
		pinNeeded = true;
		completed = false;
		popupRedirect = null;
		completeMessage = null;
		alertMessage = null;
		passedSecurityAnswer = false;
		popupCount++;

		if (null != documents) {
			setSecurityQuestion(fetchSecurityQuestion());
			if (getSecurityQuestion() == null) {
				return null;
			}
		}

		return PIN_OUTCOME;
	}

	private QuestionType fetchSecurityQuestion() {
		String adminToken = scsAuth();
		String userId = getContextUserId();
		UserType user = fetchUser(adminToken,userId);
		String activityId = createScsActivity(adminToken, user);

		GetQuestionResponse getQuestionResponse = null;
		try {
			getQuestionResponse = 
					getScsSecondFactorAuthenticationClient().getQuestion(
							adminToken,activityId,transformToSecondFactorAuthType(user));
		} catch (SharedCromerrException e) {
			ScsExceptionHandler.handleException(e);
		}
		QuestionType question = null;
		if (null != getQuestionResponse) {
			question = getQuestionResponse.getQuestion();
		}
		return question;
	}

	private org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.UserType transformToSecondFactorAuthType(UserType user) {
		org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.UserType secondFactorAuthUser = 
				new org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.UserType();
		secondFactorAuthUser.setFirstName(user.getFirstName());
		secondFactorAuthUser.setLastName(user.getLastName());
		secondFactorAuthUser.setMiddleInitial(user.getMiddleInitial());
		secondFactorAuthUser.setUserId(user.getUserId());
		return secondFactorAuthUser;
	}

	private org.gricdeq.scs.schema.sharedcromerr.signandcor._1.UserType transformToSignAndCorType(UserType user) {
		org.gricdeq.scs.schema.sharedcromerr.signandcor._1.UserType signAndCorUser = 
				new org.gricdeq.scs.schema.sharedcromerr.signandcor._1.UserType();
		signAndCorUser.setFirstName(user.getFirstName());
		signAndCorUser.setLastName(user.getLastName());
		signAndCorUser.setMiddleInitial(user.getMiddleInitial());
		signAndCorUser.setUserId(user.getUserId());
		return signAndCorUser;
	}
	
	private String getContextUserId() {
		String userId = null;
		ExternalContext context = FacesContext.getCurrentInstance()
				.getExternalContext();
		if (context != null) {
			HttpServletRequest request = (HttpServletRequest) context.getRequest();
			Principal p = request.getUserPrincipal();
			userId = p.getName();
		}
		return userId;
	}

	public String getUserId() {
		return getContextUserId();
	}
	
	private String createScsActivity(String adminToken, UserType user) {
		CreateActivityResponse response = null;
		try {
			response = getScsSecondFactorAuthenticationClient().createActivity(adminToken,SCS_DATAFLOW,user,null);
		} catch (SharedCromerrException e) {
			ScsExceptionHandler.handleException(e);
		}
		String activityId = null;
		if (null != response) {
			activityId = response.getActivityId();
		}
		return activityId;
	}

	private String scsAuth() {
		AuthenticateResponse authenticateResponse = null;
		try {
			authenticateResponse = getScsSecondFactorAuthenticationClient().authenticate(SCS_ADMIN_ID, SCS_ADMIN_PWD);
		} catch (SharedCromerrException e) {
			ScsExceptionHandler.handleException(e);
		}
		String adminToken = null;
		if (null != authenticateResponse) {
			adminToken = authenticateResponse.getSecurityToken();
		}
		return adminToken;
	}

	private UserType fetchUser(String adminToken, String userId) {
		UserType user = null;
		RetrieveUserResponse response = null;
		try {
			response = getScsSharedPortalClient().retrieveUser(adminToken, userId);
		} catch (SharedPortalException e) {
			ScsExceptionHandler.handleException(e);
		}
		if (null != response) {
			user = response.getUser();
		}
		return user;
	}



	public final void checkSecurityAnswer() {
		String status = "";
		try {
			// AccountService accountService =
			// ServiceFactory.getInstance().getAccountService();
			MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
			// status = accountService.validateSecurityQuestionAnswer("STARS2",
			// myTasks.getLoginName(), securityQuestion,
			// securityQuestionAnswer);
			status = "VALID";
		} /*
		 * catch(ServiceFactoryException sfe) {
		 * logger.error("validateSecurityQuestionAnswer failed for task with id: "
		 * + task.getTaskId() + ", facility " + task.getFacilityId(), sfe);
		 * completeMessage = "An error has occurred. " +
		 * "If failures continue to occur, please contact System Support";
		 * return; }
		 */catch (Exception re) {
			logger.error(
					"validateSecurityQuestionAnswer failed for task with id: "
							+ task.getTaskId() + ", facility "
							+ task.getFacilityId(), re);
			completeMessage = "An error has occurred. "
					+ "If failures continue to occur, please contact System Support";
			return;
		}
		if (status.equals(Constants.SECURITY_QUESTION_STATUS_VALID)) {
			// Validation passed.
			// submitSecurityAnswer();
			passedSecurityAnswer = true;
		} else if (status.equals(Constants.SECURITY_QUESTION_STATUS_INVALID)) {
			// Alert the user that the answer was incorrect.
			securityQuestionAnswer = null;
			completeMessage = "Your security answer is incorrect.  Please try again with this question.  After 5 attempts you will need to Logout and login back in again to retry.";
		} else if (status.equals(Constants.SECURITY_QUESTION_STATUS_LOCKED)) {
			// WHAT WE NEED DENNIS
			// set a flag in SubmitTask and delete the attestation document.
			// change second popup to give a message about logging them out.
			// including a button for them to click to go away.
			// then in each of the possible pages we would be on (facility,
			// application,
			// report, etc. have a hidden attribute (like used for redirect)
			// that
			// will check the submitask flag and if set do this session
			// invalidate
			// seen below (isLogUserOff()).
			// Invalidate the user HTTP session.
			completeMessage = "You have exceeded the number of retries.  You will need to Logout and log back in again to retry";
			logUserOffFlag = true; // when checked by af:inputHidden it
									// invalidates session
			if (task != null && roAttestationDoc != null) {
				try {
					// remove attestation document if there was a failure
					getInfrastructureService().removeAttestationDocumentFromTask(task);
				} catch (RemoteException e) {
					logger.error(
							"Exception while removing attestation document for task with id: "
									+ task.getTaskId() + ", facility "
									+ task.getFacilityId(), e);
					completeMessage = "An error has occurred. "
							+ "If failures continue to occur, please contact System Support";
				}
			}
			// clear attachments if there was a failure
			task.getAttachments().clear();
		}
	}
	
	private String eSignNonce = null;
	
	public void submitTask() {
		
		// check security answer
		if (null != documents) {
			if (answerQuestion(getSecurityQuestion(), getUserProvidedSecurityAnswer())) {
				// if correct, submit e-signature and copy of record
				String documentId = null;
				try {
					documentId = signAndStoreCor();
				} catch (DAOException e) {
					DisplayUtil.displayError("System error: " + e.getMessage());
					Log.error("Problem e-signing and storing document: " + e.getMessage(),e);
				}
				// submit task
				if (null != documentId) {
					submit();
					this.completed = true;
				} else {
					this.completed = false;
				}
			} else {
				this.completed = false;
			}
		} else {
			submit();
			this.completed = true;
		}
		setPassword(null);
		setUserProvidedSecurityAnswer(null);
	}
	
	private boolean answerQuestion(QuestionType question, String userProvidedSecurityAnswer) {
		boolean result = false;
		if (null != question && null != userProvidedSecurityAnswer) {
			String adminToken = scsAuth();
			String userId = getContextUserId();
			UserType user = fetchUser(adminToken,userId);
			String activityId = createScsActivity(adminToken, user);
	
			if (null != activityId) {
				AnswerType answer = new AnswerType();
				answer.setAnswerText(userProvidedSecurityAnswer);
				answer.setQuestionId(question.getQuestionId());
				
				try {
					getScsSecondFactorAuthenticationClient().answerQuestion(
							adminToken,activityId,transformToSecondFactorAuthType(user),answer);
					result = true;
				} catch (SharedCromerrException e) {
					ScsExceptionHandler.handleException(e);
				}
			}
		}
		return result;
	}

	public final void submitSecurityAnswer() {
		logger.debug("---> submitSecurityAnswer");
		String documentId = null;
		try {			
				documentId = registerDocument();			
		} catch (DAOException re) {
			logger.error(
					"Exception while registering document for task with id: "
							+ task.getTaskId() + ", facility "
							+ task.getFacilityId(), re);
			completeMessage = "An error has occurred. "
					+ "If failures continue to occur, please contact System Support";
			return;
		} catch (Exception e) {
			logger.error(
					"Exception while registering document for task with id: "
							+ task.getTaskId() + ", facility "
							+ task.getFacilityId(), e);
			alertMessage = "Caution, further attention may be required.";
			completeMessage = "If the task you were attempting to submit no longer appears in the task table, "
					+ "then your submission was successful and no further action on your part is required. "
					+ "If the task remains in the task table, please log out and log back in. "
					+ "If the task still appears in the task table, you will need to submit it. "
					+ "If you continue to receive errors when you submit, please contact system support";
			return;			
		}
		
		String signatureId = null;
		if (null != documentId) {
			MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
			Object organizationId = myTasks.getExternalCompanyId();
			String username = myTasks.getLoginName(); 
			if (testMode) username = "mcolbert";
			eSignNonce = new Date().getTime() + "";
			String redirectUrl;
			try {
				redirectUrl = URLEncoder.encode(
						FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap().get("Referer") + 
						"&__esignid=" + eSignNonce, "utf-8");
			} catch (UnsupportedEncodingException e) {
				redirectUrl = null;
			}
			logger.debug("---> redirectUrl: " + redirectUrl);
			logger.debug("---> external org id: " + organizationId);
			logger.debug("---> external username: " + username);
			if (null != organizationId && null != username) {
				signatureId = registerSignature(documentId,organizationId.toString(),username,redirectUrl);
			} else {
				throw new RuntimeException("Unable to register signature; null input(s).");
			}
			if (!testMode) {
				if (null != signatureId) {
					String eSignUri = (String)Config.getEnvEntry("app/eSignURI");
					eSignUri = eSignUri + "?signatureId=" + signatureId;
					try {
						FacesContext.getCurrentInstance().getExternalContext().redirect(eSignUri);
					} catch (IOException e) {
						logger.error("I/O error occurred while attempting redirect: ", e);
						throw new RuntimeException(e);
					}
					
				}
			} else {
				submit();
			}
		} else {
			submit();
		}
	}
	
	private String registerDocument() throws DAOException {
		logger.debug("---> registerDocument");
		String documentId = null;
		
		logger.debug("--> documents = " + documents);
		
		if (null != documents) {
			Document appZip = null;
			for (Document doc : documents) {
				logger.debug("---> doc.path: " + doc.getPath());
				if (Document.CONTENT_TYPE_ZIP.equals(doc.getContentType())) {
					appZip = doc;
				}
			}
			logger.debug("---> appZip.path: " + appZip.getPath());
			if (null != appZip) {
				documentId = getInfrastructureService().registerDocument(appZip);
			}
		} else {
			logger.debug("---> no documents to register ");			
		}
		return documentId;
	}
	
	private String signAndStoreCor() throws DAOException {
		String documentId = null;
		if (null != documents) {
			TmpDocument submissionDocument = grabSubmissionDocument();
			if (null != submissionDocument) {
				String adminToken = scsAuth();
				String userId = getContextUserId();
				UserType user = fetchUser(adminToken,userId);
				String activityId = createScsActivity(adminToken, user);
				
				DocumentType document = new DocumentType();
				document.setName(submissionDocument.getTmpFileName());
				document.setFormat(DocumentFormatType.BIN);
				DataHandler dh = new DataHandler(new FileDataSource(DocumentUtil.getFileStoreRootPath() + submissionDocument.getPath()));
				document.setContent(dh);
				//TODO more document data ... ?
				
				SignatureDataType signatureData = new SignatureDataType();
				signatureData.setAnswerSHA256Hash(DigestUtils.sha256Hex(getUserProvidedSecurityAnswer()));
				signatureData.setPasswordSHA256Hash(DigestUtils.sha256Hex(getPassword()));
				signatureData.setQuestionId(getSecurityQuestion().getQuestionId());
				
				SignAndStoreCorResponse signAndStoreCorResponse = null;
				try {
					signAndStoreCorResponse = 
							getScsSignatureAndCorClient().signAndStoreCor(
									adminToken, activityId, transformToSignAndCorType(user), document, signatureData);
				} catch (net.exchangenetwork.wsdl.sharedcromerr.signandcor._1.SharedCromerrException e) {
					ScsExceptionHandler.handleException(e);
				}
				if (null != signAndStoreCorResponse) {
					documentId = signAndStoreCorResponse.getDocumentId();
				}
			} else {
				throw new DAOException("Documents unavailable");				
			}
		} else {
			throw new DAOException("Documents unavailable");
		}
		return documentId;
	}

	private TmpDocument grabSubmissionDocument() {
		TmpDocument submissionDocument = null;
		for (Document doc : documents) {
			logger.debug("---> doc.path: " + doc.getPath());
			if (Task.TaskType.FC.equals(task.getTaskType()) ||
					Task.TaskType.FCH.equals(task.getTaskType()) ||
					Task.TaskType.FCC.equals(task.getTaskType())) {
				// assume there is only one document
				if (doc instanceof TmpDocument) {
					submissionDocument = (TmpDocument)doc;
				}
			} else {
				// may be multiple documents, but only get the zip
				if (Document.CONTENT_TYPE_ZIP.equals(doc.getContentType())) {
					if (doc instanceof TmpDocument) {
						submissionDocument = (TmpDocument)doc;
					}
				}
			}
		}
		return submissionDocument;
	}
	
	private String registerSignature(String documentId, String organizationId, 
			String username, String redirectUrl) {
		logger.debug("---> registerSignature");
		String signatureId = null;
		try {
			signatureId = 
					getInfrastructureService().registerSignature(documentId,
							organizationId,username,redirectUrl);
		} catch (DAOException e) {
			logger.error(
					"Exception while registering signature: " + e.getMessage(), 
					e);
			completeMessage = "An error has occurred. "
					+ "If failures continue to occur, please contact System Support";
		}
		return signatureId;
	}
	
	private boolean testMode = "true".equals(Config.getEnvEntry("app/testMode"));

	/**
	 * user sent here upon redirect from e-signature
	 */
	public final void submit() {
		
		logger.debug("---> submit");
		completeMessage = "Submission of task is in progress;\nPlease wait for "
				+ "completeion of task and \ncheck the status by clicking on Check Status Button";
		boolean submitOk = false;
		MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
		
		try {

			logger.debug("Processing attachments for task: " + task.getTaskId()
					+ ", facility " + task.getFacilityId());
			addSubmissionAttachments();
			if (submissionAttachmentSize > ATTACHMENT_SIZE_THRESHOLD) {
				completeMessage = "Submission of task is in progress;\n"
						+ "Due to the size of the attachments associated with this submission, processing "
						+ "may take several minutes. Please wait for 30 minutes to an hour before checking submission status. "
						+ "If submission does not appear to have completed, please contact System Support.";
			}
			
			logger.debug("Submitting task with id: " + task.getTaskId()
					+ ", facility " + task.getFacilityId());
			submitedTaskId = getInfrastructureService().submitTask(task,
					myTasks.getLoginName(), pin, securityQuestionAnswer);
			submitOk = true;
			logger.debug("Completed submission of task with id: "
					+ task.getTaskId() + ", facility " + task.getFacilityId());
			cleanup();
			removeTask(myTasks);

		} catch (FileNotFoundException nfe) {

			completeMessage = "Submit task failed; Please try again.";
			DisplayUtil
					.displayError("Attachment file not found: "
							+ nfe.getMessage()
							+ ". The attachment was corrupt or contained a virus and therefore was not uploaded. "
							+ "Please delete the corresponding entry from the attachments table and upload it again.");

		} catch (Exception re) {

			completed = true;
			String errMsg = re.getMessage();
			logger.error("Exception caught in submitSecurityAnswer: " + errMsg,
					re);
			boolean updateTask = true;
			if (errMsg.contains(CommonConst.PREVIOUS_SUBMIT_IN_PROCESS_MSG)) {
				logger.debug("Previous submitted task is still in progress for task with id: "
						+ task.getTaskId()
						+ ", facility "
						+ task.getFacilityId());
				completeMessage = "Submission is still in progress. "
						+ "Please log out of the system and check the In Progress Tasks table at a later time. "
						+ "If your submission has not completed after an hour, please contact System Support.";
				// Mantis 2793: removed code setting updateTask to false
			} else if (errMsg.contains(CommonConst.PREVIOUS_SUBMIT_FAILED_MSG)) {
				logger.error("Previous submitted task failed for task with id: "
						+ task.getTaskId()
						+ ", facility "
						+ task.getFacilityId());
				completeMessage = "Task submission has failed. Please contact System Support.";
			} else if (errMsg.contains(CommonConst.INVALID_SUBMIT_PIN_MSG)) {
				logger.debug("Submit task failed; Invalid PIN for task with id: "
						+ task.getTaskId()
						+ ", facility "
						+ task.getFacilityId());
				pin = null;
				pinNeeded = true;
				completed = false;
				passedSecurityAnswer = false;
				completeMessage = "An Invalid PIN was entered. Please try again.";
			} else {
				logger.error("Submission failed for task with id: "
						+ task.getTaskId() + ", facility "
						+ task.getFacilityId());
				alertMessage = "Caution, further attention may be required.";
				completeMessage = "If the task you were attempting to submit no longer appears in the task table, "
						+ "then your submission was successful and no further action on your part is required. "
						+ "If the task remains in the task table, please log out and log back in. "
						+ "If the task still appears in the task table, you will need to submit it. "
						+ "If you continue to receive errors when you submit, please contact system support";
			}

			if (updateTask) {
				// need to update task so next submission will have a different
				// submission id
				try {
					logger.debug("Incrementing task counter for task with id: "
							+ task.getTaskId());
					getInfrastructureService().incrementTaskCounter(task);
				} catch (RemoteException re2) {
					logger.debug("Exception incrementing counter for task: "
							+ task.getTaskId());
				}
			}

			logger.error(
					"Exception submitting task with id: " + task.getTaskId()
							+ ", facility " + task.getFacilityId() + " - "
							+ re.getMessage(), re);
		} finally {
			if (!submitOk) {
				if (task != null && roAttestationDoc != null) {
					try {
						// remove attestation document if there was a failure
						getInfrastructureService().removeAttestationDocumentFromTask(
								task);
					} catch (RemoteException e) {
						logger.error(
								"Exception while removing attestation document for task with id: "
										+ task.getTaskId() + ", facility "
										+ task.getFacilityId(), e);
					}
				}
				// clear attachments if there was a failure
				task.getAttachments().clear();
			}
		}
	}

	private void cleanup() {
		// TODO Auto-generated method stub
	 setDocuments(null);	 
	}

	private void removeTask(MyTasks myTasks) {
		boolean removeOk = false;
		try {
			logger.debug("Removing task with id: " + task.getTaskId());
			myTasks.removeTaskFromMyTasks(task);
			roAttestationDoc = null;
			logger.debug("Completed removal of task with id: "
					+ task.getTaskId());
			removeOk = true;
		} catch (RemoteException e) {
			completeMessage = "Submit task completed successfully. "
					+ "However, task could not be deleted from In Progress Tasks table. "
					+ "Please contact system administrator.";
			logger.error("Exception deleting task: " + task.getTaskId());
		}
		if (removeOk) {
			switch (task.getTaskType()) {
			case FC:
			case FCH:
				myTasks.setFrozenFacilityViewable(true);
				break;
			case ER:
			case R_ER:
				ReportSearch rs = (ReportSearch) FacesUtil
						.getManagedBean("reportSearch");
				rs.setPopupRedirectOutcome("home");
				break;
			case CR_OTHR:
			case CR_TEST:
			case CR_CEMS:
			case CR_SMBR:
			case CR_PER:
			case CR_TVCC:
			case CR_ONE:
			case CR_GENERIC:	
				ComplianceReports cr = (ComplianceReports) FacesUtil
						.getManagedBean("complianceReport");
				cr.setPopupRedirectOutcome("home");
				//myTasks.setPageRedirect(null);// set to null to navigate to home
												// page after submit,
				// since setPageRedirect is set to 'compliancedetail' while
				// creating compliance report.
				break;
			//case ST:                                                 // TO DO: Confirm if this case should be removed
			//	StackTests st = (StackTests) FacesUtil                 // Stack Tests have dependencies on FC and FCC
			//			.getManagedBean("stackTests");                 // so should be treated like Applications and 
			//	st.setPopupRedirectOutcome("home");                    // Emissions Inventories.
			//	break;
			case MRPT:
				MonitorGroupDetail mg = (MonitorGroupDetail) FacesUtil
						.getManagedBean("monitorGroupDetail");
				mg.setPopupRedirectOutcome("home");
				myTasks.setPageRedirect(null);// set to null to navigate to home
												// page after submit
				break;
			default:
				if (task.getDependent()) {
					myTasks.setFrozenFacilityViewable(true);
				}
			}

			popupRedirect = "home";
			completeMessage = "Submit task completed successfully.";
			completed = true;
		}
	}

	private void addSubmissionAttachments() throws RemoteException,
			FileNotFoundException {
		try {
			if (roAttestationDoc != null && uploadFileInfo != null) {
				getInfrastructureService().addAttestationDocumentToTask(task,
						roAttestationDoc, uploadFileInfo.getFileName(),
						uploadFileInfo.getInputStream());
			}
			if (task.getApplication() != null) {
				getApplicationService().addSubmissionAttachments(task);
			}
			if (task.getComplianceReport() != null) {
				getComplianceReportService().addSubmissionAttachments(task);
			}
			if (task.getRelocateRequest() != null) {
				getRelocateRequestService().addSubmissionAttachments(task);
			}
			if (task.getReport() != null || task.getNtvReport() != null) {
				getEmissionsReportService().addSubmissionAttachments(task);
			}
			
			if (task.getStackTest() != null) {
				getStackTestService().addSubmissionAttachments(task);
			}

			if (TaskType.FC.equals(task.getTaskType())
					|| TaskType.FCH.equals(task.getTaskType())) {
				getFacilityService().addSubmissionAttachments(task);
			} else if (TaskType.FCC.equals(task.getTaskType())) {
				// need to store attestation document in task for facility
				// change request
				if (task.getFacility() == null) {
					logger.error("Task facility object is null for FCC task: "
							+ task.getTaskId());
				} else if (task.getFacility().getAttestationDocument() == null) {
					logger.debug(" No attestation document set for FCC task: "
							+ task.getTaskId());
				} else {
					logger.debug(" adding facility contact change attestation document "
							+ task.getFacility().getAttestationDocument()
									.getDocumentID()
							+ " to task "
							+ task.getTaskId()
							+ " for facility "
							+ task.getFacilityId());
					task.setAttestationDoc(task.getFacility()
							.getAttestationDocument());
				}
			}

			submissionAttachmentSize = 0;
			if (task.getAttachments().size() > 0) {
				for (Attachment a : task.getAttachments()) {
					logger.debug("attachment name = " + a.getSystemFilename());
					File att = new File(a.getSystemFilename());
					if (att != null) {
						logger.debug("attachment size = " + att.length());
						submissionAttachmentSize += att.length();
					} else {
						logger.error("Unable to create file for attachment: "
								+ a.getSystemFilename());
					}
				}
			}
			logger.debug("Total attachment size is: " + submissionAttachmentSize);
		} catch (FileNotFoundException nfe) {
			logger.error(nfe.getMessage(), nfe);
			throw nfe;
		} catch (IOException e) {
			throw new RemoteException(
					"Exception adding submission attachments", e);
		}
	}

	public final void submitPin() {
		securityQuestionAnswer = null;
		completeMessage = null;
		alertMessage = null;
		if (pin != null && pin.trim().length() > 0) {
			pinNeeded = false;
		}
		return;
	}

	public QuestionType getSecurityQuestion() {
		return this.securityQuestion;
	}

	public void setSecurityQuestion(QuestionType question) {
		this.securityQuestion = question;
	}

	public final void cancelGetPin() {
		pin = null;
		AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
	}

	public final void cancelGetQuestion() {
		securityQuestion = null;
		passedSecurityAnswer = false;
		AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
	}

	public final void cancelSaveAttestationDoc() {
		roAttestationDoc = null;
		uploadFile = null;
		uploadFileInfo = null;
		AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
	}

	public final void closeInProgSubmitTask() {
		closeSubmitTask();
	}

	public final void closeSubmitTask() {
		AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
	}

	public final int getAttestHeight() {
		return 400;
	}

	public final int getAttestWidth() {
		return 800;
	}

	public String getPopupRedirect() {
		if (popupRedirect != null) {
			FacesUtil.setOutcome(null, popupRedirect);
			popupRedirect = null;
		}
		return null;
	}

	public boolean getDoSubmit() {
		boolean complete = isCompleted();
		if (!complete) {
			String eSignIdParam = 
					(String)FacesContext.getCurrentInstance().
						getExternalContext().getRequestParameterMap().
							get("__esignid");
			if (null != eSignIdParam) {
				if (eSignIdParam.equals(this.eSignNonce)) {
					submit();
				}
			}
		}
		return isCompleted();
	}
	
	public boolean isCompleted() {
		return completed;
	}

	public String getSubmitedTaskId() {
		return submitedTaskId;
	}

	public String getCompleteMessage() {
		return completeMessage;
	}

	public String getSecurityQuestionAnswer() {
		return this.securityQuestionAnswer;
	}

	public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
		this.securityQuestionAnswer = securityQuestionAnswer;
	}

	public boolean isPassedSecurityAnswer() {
		return passedSecurityAnswer;
	}

	public final boolean isPinNeeded() {
		return pinNeeded;
	}

	public final void setPinNeeded(boolean needPin) {
		this.pinNeeded = needPin;
	}

	public final boolean isNonROSubmission() {
		return nonROSubmission;
	}

	public final void setNonROSubmission(boolean nonROSubmission) {
		this.nonROSubmission = nonROSubmission;
	}

	public final UploadedFile getUploadFile() {
		return uploadFile;
	}

	public final void setUploadFile(UploadedFile uploadFile) {
		this.uploadFile = uploadFile;
	}

	public final UploadedFileInfo getUploadFileInfo() {
		return uploadFileInfo;
	}

	public final void setUploadFileInfo(UploadedFileInfo uploadFileInfo) {
		this.uploadFileInfo = uploadFileInfo;
	}

	public final void saveAttestationDoc() {
		if (uploadFileInfo == null && uploadFile == null) {
			DisplayUtil.displayError("The Attestation File is required.");
			return;
		}
		// copy file upload information so it isn't lost
		if (uploadFileInfo == null && uploadFile != null) {
			uploadFileInfo = new UploadedFileInfo(uploadFile);
		}
		// new document
		// upload document and create database entry pointing to it
		roAttestationDoc = new Document();
		roAttestationDoc.setFacilityID(task.getFacilityId());
		roAttestationDoc.setLastModifiedBy(CommonConst.GATEWAY_USER_ID);
		roAttestationDoc.setLastCheckoutBy(CommonConst.GATEWAY_USER_ID);
		roAttestationDoc.setDescription("Attestation Document for submission "
				+ "PS:" + task.getFacilityId() + ":" + task.getTaskId() + ":"
				+ task.getTaskName());
	}

	public final String processConfirmAttestationDoc() {
		pin = null;
		pinNeeded = true;
		completed = false;
		popupRedirect = null;
		completeMessage = null;
		setAttestationMessage();
		popupCount++;
		return ATTEST_OUTCOME;
	}

	public final String processConfirmRO() {
		pin = null;
		pinNeeded = true;
		completed = false;
		popupRedirect = null;
		completeMessage = null;
		popupCount++;

		// remove attestation doc if it was previously added.
		if (task != null && roAttestationDoc != null) {
			try {
				getInfrastructureService().removeAttestationDocumentFromTask(task);
				// retrieve task from DB to get updated lastModified value, but
				// don't totally replace it since some needed data may not be in
				// the database.
				Task tempTask = getInfrastructureService().retrieveTask(
						task.getTaskId());
				task.setLastModified(tempTask.getLastModified());
				roAttestationDoc = null;
			} catch (RemoteException e) {
				logger.error(
						"Exception while modifying task: "
								+ task.getTaskInternalId(), e);
			}
		}
		setAttestationMessage();
		return ATTEST_OUTCOME;
	}

	public final String processConfirmNonRO() {
		pin = null;
		pinNeeded = true;
		completed = false;
		popupRedirect = null;
		completeMessage = null;
		alertMessage = null;
		uploadFile = null;
		uploadFileInfo = null;
		roAttestationDoc = null;
		nonROSubmission = true;
		popupCount++;
		return NON_RO_SUBMISSION_OUTCOME;
	}

	public final void reset() {
		pin = null;
		pinNeeded = false;
		completed = false;
		completeMessage = null;
		alertMessage = null;
		securityQuestionAnswer = null;
		passedSecurityAnswer = false;
		uploadFile = null;
		uploadFileInfo = null;
		roAttestationDoc = null;
	}

	public final String getCROMERRAttestationMessage() {
		String message = "";
		InfrastructureService infraBO;
		try {
			MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
			infraBO = ServiceFactory.getInstance().getInfrastructureService();
			message = infraBO.getCROMERRAttestationMessage(myTasks
					.getLoginName());
		} catch (ServiceFactoryException e) {
			logger.error("Exception accessing infrastructure service", e);
		} catch (RemoteException e) {
			logger.error("Exception retrieving CROMERR attestation language", e);
		}
		return message;
	}

	public final String getDAPCAttestationMessage() {
		String message = "";
		if (task.getFacility() != null) {
			try {
				if (task.getReport() != null) {
					EmissionsReportService emissionsReportBO = ServiceFactory
							.getInstance().getEmissionsReportService();
					message = emissionsReportBO
							.getDAPCAttestationMessage(task.getFacility()
									.getPermitClassCd(), task.getReport());
				} else if (task.getComplianceReport() != null || task.getStackTest() != null) {
					ComplianceReportService complianceReportBO = ServiceFactory
							.getInstance().getComplianceReportService();
					message = "<br><br>"
							+ complianceReportBO.getDAPCAttestationMessage(task
									.getComplianceReport(), task.getFacility()
									.getPermitClassCd());
				} else if (task.getApplication() != null) {
					String applicationTypeCd = task.getApplication().getApplicationTypeCD();
					if(applicationTypeCd.equals(ApplicationTypeDef.TITLE_V_APPLICATION)) 						
						message = SystemPropertyDef.getSystemPropertyValue("TVApplicationAttestationMessage", null);
					else if(applicationTypeCd.equals(ApplicationTypeDef.PTIO_APPLICATION))
						message = SystemPropertyDef.getSystemPropertyValue("NonTVApplicationAttestationMessage", null);
				} else {
					InfrastructureService infrastructureBO = ServiceFactory
							.getInstance().getInfrastructureService();
					message = infrastructureBO.getDAPCAttestationMessage(task
							.getFacility().getPermitClassCd());
				}
			} catch (ServiceFactoryException e) {
				handleException(new RemoteException("ServiceFactoryException",
						e));
			} catch (RemoteException e) {
				handleException(e);
			}
		} else {
			logger.error("Unable to get AQD attestation message. Task facility is null.");
		}
		return message;
	}

	public final void setAttestationMessage() {
		String attestationMsg = getCROMERRAttestationMessage();
		if (attestationMsg.length() > 0) {
			attestationMsg = attestationMsg.replaceAll("\n", "<br>\n");
		}
		if (!nonROSubmission && dapcAttestationRequired) {
			attestationMsg += "\n<br>\n" + getDAPCAttestationMessage();
		}
		setMessage(attestationMsg);
	}

	public final boolean isDapcAttestationRequired() {
		return dapcAttestationRequired;
	}

	public final void setDapcAttestationRequired(boolean dapcAttestationRequired) {
		this.dapcAttestationRequired = dapcAttestationRequired;
	}

	public final boolean isRoSubmissionRequired() {
		return roSubmissionRequired;
	}

	public final void setRoSubmissionRequired(boolean roSubmissionRequired) {
		this.roSubmissionRequired = roSubmissionRequired;
	}

	public boolean isLogUserOff() {
		if (logUserOffFlag) {
			// DisplayUtil.displayError("Too many attempts to answer the security question.  You have been logged off.  Log in again to try over.");
			task.getAttachments().clear();
			ExternalContext context = FacesContext.getCurrentInstance()
					.getExternalContext();
			HttpServletRequest request = (HttpServletRequest) context
					.getRequest();
			if (request != null) {
				HttpSession tempSession = request.getSession(false);
				tempSession.invalidate();
				String epaPortal = null;
				// This redirection below does not seem to do anything.
				// also did not help when we commented out the
				// invalidate()--still did not redirect.
				Node portalNode = Config.findNode("app.defaultEPAPortal");
				if (portalNode != null) {
					epaPortal = portalNode.getText();
				}
				String redirect = epaPortal;
				logger.debug(" logout - redirecting to '" + redirect
						+ "'");
				FacesUtil.setOutcome(null, epaPortal);
			}
		}
		return true;
	}

	public boolean isLogUserOffFlag() {
		return logUserOffFlag;
	}

	public String getAlertMessage() {
		return alertMessage;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	public List<Document> getDocuments() {
		return documents;
	}
}
