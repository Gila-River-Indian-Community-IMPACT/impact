package us.oh.state.epa.stars2.webcommon;

import org.apache.log4j.Logger;

import us.oh.state.epa.portal.service.accountservice.AccountService;
import us.oh.state.epa.portal.service.dapcservice.DAPCService;
import us.oh.state.epa.portal.service.datasetservice.DatasetService;
import us.oh.state.epa.portal.service.datasubmitservice.DataSubmitService;
import us.oh.state.epa.portal.service.eventservice.EventService;
import us.oh.state.epa.portal.service.placeservice.PlaceService;
import us.oh.state.epa.stars2.bo.AnalysisService;
import us.oh.state.epa.stars2.bo.ApplicationService;
import us.oh.state.epa.stars2.bo.ComplaintsService;
import us.oh.state.epa.stars2.bo.ComplianceReportService;
import us.oh.state.epa.stars2.bo.CorrespondenceService;
import us.oh.state.epa.stars2.bo.DelegationRequestService;
import us.oh.state.epa.stars2.bo.DocumentService;
import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.FacilityHistoryService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.bo.GDFService;
import us.oh.state.epa.stars2.bo.GenericIssuanceService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.bo.MonitoringService;
import us.oh.state.epa.stars2.bo.NaicsService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.bo.RelocateRequestService;
import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.bo.StackTestService;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.CompanyService;
import us.wy.state.deq.impact.bo.ContactService;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

/**
 * This class is a factory that abstracts the process required for obtaining
 * references to services provided by the middle-tier. The factory is
 * responsible for performing the necessary lookups and returning the requested
 * service objects to the caller.
 * 
 * @author Ken Bradley
 * @version $Revision: 1.43 $
 */
@Deprecated
public final class ServiceFactory {
	static String stars2 = "Stars2";
	static String itsInternalPortal = "ITSInternalPortal";
	static String itsExternalPortal = "ITSExternalPortal";
	static private ServiceFactory service = new ServiceFactory();
	private static Logger logger = Logger.getLogger(ServiceFactory.class);
//	private String baseGatewayLocation;

	private ServiceFactory() {
//		logger.debug("ServiceFactory Start");
//		Node baseGatewayLocationNode = Config.findNode("app.GatewayLocation");
//
//		if (baseGatewayLocationNode != null) {
//			baseGatewayLocation = baseGatewayLocationNode.getText();
//		} else {
//			throw new RuntimeException("No ITS gateway set!");
//		}
	}

	/**
	 * Returns a reference to the ServiceFactory object.
	 * 
	 * @return an object reference
	 */
	public static ServiceFactory getInstance() {
		return service;
	}

	public static void shutdown() {
		service = null;
	}

	/**
	 * Returns a reference to the Facility Remote 'Stateless' Session Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public EmissionsReportService getEmissionsReportService()
			throws ServiceFactoryException {
//		EmissionsReportService emissionsReportService = null;
//		emissionsReportService = new EmissionsReportBO();
//
//		return emissionsReportService;
		
		//TODO temporary until service factory is removed
		return App.getApplicationContext().getBean(EmissionsReportService.class);
	}

	/**
	 * Returns a reference to the Facility Remote 'Stateless' Session Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public FacilityService getFacilityService() throws ServiceFactoryException {
//		FacilityService facilityService = null;
//		facilityService = new FacilityBO();
//
//		return facilityService;
		//TODO temporary until service factory is removed
		return App.getApplicationContext().getBean(FacilityService.class);
	}
	
	public NaicsService getNaicsService() throws ServiceFactoryException {
//		NaicsService service = null;
//		service = new NaicsBO();
//
//		return service;
		//TODO temporary until service factory is removed
		return App.getApplicationContext().getBean(NaicsService.class);
	}

	/**
	 * Returns a reference to the Company Remote 'Stateless' Session Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public CompanyService getCompanyService() throws ServiceFactoryException {
//		CompanyService companyService = null;
//
//		companyService = new CompanyBO();
//
//		return companyService;
		return App.getApplicationContext().getBean(CompanyService.class);
	}
	
	public ContactService getContactService() throws ServiceFactoryException {
//		ContactService contactService = null;
//
//		contactService = new ContactBO();
//
//		return contactService;
		return App.getApplicationContext().getBean(ContactService.class);
	}

	/**
	 * Returns a reference to the GenericIssuance Remote 'Stateless' Session
	 * Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public GenericIssuanceService getGenericIssuanceService()
			throws ServiceFactoryException {
//		GenericIssuanceService genericIssuanceService = null;
//		genericIssuanceService = new GenericIssuanceBO();
//
//		return genericIssuanceService;
		return App.getApplicationContext().getBean(GenericIssuanceService.class);
	}

	/**
	 * Returns a reference to the Permit Remote 'Stateless' Session Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public PermitService getPermitService() throws ServiceFactoryException {
//		PermitService permitService = null;
//		permitService = new PermitBO();
//
//		return permitService;
		return App.getApplicationContext().getBean(PermitService.class);
	}

	/**
	 * Returns a reference to the Infrastructure Remote 'Stateless' Session
	 * Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public InfrastructureService getInfrastructureService()
			throws ServiceFactoryException {
//		InfrastructureService infrastructureService = null;
//		infrastructureService = new InfrastructureBO();
//		return infrastructureService;
		return App.getApplicationContext().getBean(InfrastructureService.class);
	}

//	/**
//	 * Returns a reference to the WorkFlow Remote 'Stateless' Session Bean.
//	 * 
//	 * @return a reference to the remote session bean stub
//	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
//	 *             an error has occurred when trying to retrieve the bean
//	 */
//	public WorkFlowService getWorkFlowService() throws ServiceFactoryException {
//		WorkFlowService workFlowService = null;
//		workFlowService = new WorkFlowBO();
//		return workFlowService;
//		return App.getApplicationContext().getBean(WorkFlowService.class);
//	}

	public ReadWorkFlowService getReadWorkFlowService() throws ServiceFactoryException {
		return App.getApplicationContext().getBean(ReadWorkFlowService.class);
	}

//	public WriteWorkFlowService getWriteWorkFlowService() throws ServiceFactoryException {
//		return App.getApplicationContext().getBean(WriteWorkFlowService.class);
//	}

	/**
	 * Returns a reference to the Document Remote 'Stateless' Session Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public CorrespondenceService getCorrespondenceService()
			throws ServiceFactoryException {
//		CorrespondenceService correspondenceService = null;
//		correspondenceService = new CorrespondenceBO();
//
//		return correspondenceService;
		return App.getApplicationContext().getBean(CorrespondenceService.class);
	}

	/**
	 * Returns a reference to the ComplianceReport Remote 'Stateless' Session
	 * Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public ComplianceReportService getComplianceReportService()
			throws ServiceFactoryException {
//		ComplianceReportService complianceReportService = null;
//		complianceReportService = new ComplianceReportBO();
//
//		return complianceReportService;
		return App.getApplicationContext().getBean(ComplianceReportService.class);
	}

	/**
	 * Returns a reference to the Document Remote 'Stateless' Session Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public DocumentService getDocumentService() throws ServiceFactoryException {
//		DocumentService documentService = null;
//		documentService = new DocumentBO();
//
//		return documentService;
		return App.getApplicationContext().getBean(DocumentService.class);
	}

	/**
	 * Returns a reference to the Application Remote 'Stateless' Session Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public ApplicationService getApplicationService()
			throws ServiceFactoryException {
//		ApplicationService applicationService = null;
//		applicationService = new ApplicationBO();
//
//		return applicationService;
		return App.getApplicationContext().getBean(ApplicationService.class);
	}

	/**
	 * Returns a reference to the Report Remote 'Stateless' Session Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public ReportService getReportService() throws ServiceFactoryException {
//		ReportService reportService = null;
//		reportService = new ReportsBO();
//
//		return reportService;
		return App.getApplicationContext().getBean(ReportService.class);
	}

	/**
	 * Returns a reference to the RelocateRequest Remote 'Stateless' Session
	 * Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public RelocateRequestService getRelocateRequestService()
			throws ServiceFactoryException {
//		RelocateRequestService relocateRequestService = null;
//		relocateRequestService = new RelocateRequestBO();
//
//		return relocateRequestService;
		return App.getApplicationContext().getBean(RelocateRequestService.class);
	}

	/**
	 * Returns a reference to the RelocateRequest Remote 'Stateless' Session
	 * Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */

	public DelegationRequestService getDelegationRequestService()
			throws ServiceFactoryException {
//		DelegationRequestService delegationRequestService = null;
//
//		delegationRequestService = new DelegationRequestBO();
//
//		return delegationRequestService;
		return App.getApplicationContext().getBean(DelegationRequestService.class);
	}

	/**
	 * Returns a reference to the Analysis Remote 'Stateless' Session Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public AnalysisService getAnalysisService() throws ServiceFactoryException {
//		AnalysisService analysisService = null;
//
//		analysisService = new AnalysisBO();
//
//		return analysisService;
		return App.getApplicationContext().getBean(AnalysisService.class);
	}

	/**
	 * Returns a reference to the PortalService Remote 'Stateless' Session Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	// public PortalService getPortalServiceService()
	// throws ServiceFactoryException {
	// PortalService portalService = null;
	// portalService = (PortalService) getStatelessBeanRemote(
	// "ejb/PortalService", PortalServiceHome.class);
	//
	// return portalService;
	// }

	/**
	 * Returns a reference to the NoticesService Remote 'Stateless' Session
	 * Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	// public NoticesService getNoticesService() throws ServiceFactoryException
	// {
	// NoticesService noticesService = null;
	// try {
	// Class<NoticesServiceHome> nshc = NoticesServiceHome.class;
	// if (nshc == null) {
	// throw new ServiceFactoryException(
	// "Unable to retrieve the NoticesService, "
	// + "NoticesServiceHome.class is null.");
	// }
	//
	// noticesService = (NoticesService) getStatelessBeanRemote(
	// "ejb/NoticesService", NoticesServiceHome.class);
	// // noticesService = NoticesClient.getService("appsman1,appsman2",
	// // 7025);
	// // noticesService = NoticesClient.getService("javadev0", 7002);
	// } catch (Exception epae) {
	// throw new ServiceFactoryException(
	// "Unable to retrieve the NoticesService. ", epae);
	// }
	// return noticesService;
	// }

	public AccountService getAccountService() throws ServiceFactoryException {
		AccountService accountService = null;
//		try {
//			AccountServiceServiceLocator psl = new AccountServiceServiceLocator();
//			if (psl != null) {
//				accountService = psl.getAccountService(new URL(new String(
//						baseGatewayLocation + "AccountService")));
//				if (accountService == null) {
//					logger.error("Unable to access accountService at "
//							+ baseGatewayLocation + "AccountService");
//				}
//			}
//		} catch (ServiceException se) {
//			throw new ServiceFactoryException(
//					"Unable to retrieve the AccountService. ", se);
//		} catch (MalformedURLException murle) {
//			throw new ServiceFactoryException(
//					"Unable to retrieve the AccountService. ", murle);
//		}

		return accountService;
	}

	public EventService getEventService() throws ServiceFactoryException {
		EventService eventService = null;
		/*JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(EventService.class);
		factory.setAddress(baseGatewayLocation);
		eventService = (EventService) factory.create();

		// Set the timeout to infinite. The default is 60 secs.
		Client client = ClientProxy.getClient(eventService);
		HTTPConduit conduit = (HTTPConduit) client.getConduit();
		HTTPClientPolicy policy = conduit.getClient();
		policy.setReceiveTimeout(0); // will wait indefinitely
*/
		return eventService;
	}

	public DatasetService getDatasetService() throws ServiceFactoryException {
		DatasetService datasetService = null;
//		try {
//			DatasetServiceServiceLocator dsl = new DatasetServiceServiceLocator();
//			if (dsl != null) {
//				datasetService = dsl.getDatasetService(new URL(new String(
//						baseGatewayLocation + "DatasetService")));
//				if (datasetService == null) {
//					logger.error("Unable to access datasetService at "
//							+ baseGatewayLocation + "DatasetService");
//				}
//			}
//		} catch (ServiceException se) {
//			throw new ServiceFactoryException(
//					"Unable to retrieve the DatasetService. ", se);
//		} catch (MalformedURLException murle) {
//			throw new ServiceFactoryException(
//					"Unable to retrieve the DatasetService. ", murle);
//		}
//
		return datasetService;
	}

	public DataSubmitService getDataSubmitService()
			throws ServiceFactoryException {
		DataSubmitService dataSubmitService = null;
//		try {
//			DataSubmitServiceServiceLocator dsl = new DataSubmitServiceServiceLocator();
//			if (dsl != null) {
//				dataSubmitService = dsl.getDataSubmitService(new URL(
//						new String(baseGatewayLocation + "DataSubmitService")));
//				if (dataSubmitService == null) {
//					logger.error("Unable to access datasetService at "
//							+ baseGatewayLocation + "DataSubmitService");
//				}
//			}
//		} catch (ServiceException se) {
//			throw new ServiceFactoryException(
//					"Unable to retrieve the DataSubmitService. ", se);
//		} catch (MalformedURLException murle) {
//			throw new ServiceFactoryException(
//					"Unable to retrieve the DataSubmitService. ", murle);
//		}

		return dataSubmitService;
	}

	public DAPCService getDAPCService() throws ServiceFactoryException {
		DAPCService dapcService = null;
//		try {
//			DAPCServiceServiceLocator dsl = new DAPCServiceServiceLocator();
//			if (dsl != null) {
//				dapcService = dsl.getDAPCService(new URL(new String(
//						baseGatewayLocation + "DAPCService")));
//				if (dapcService == null) {
//					logger.error("Unable to access DAPCService at "
//							+ baseGatewayLocation + "DACPService");
//				}
//			}
//		} catch (ServiceException se) {
//			throw new ServiceFactoryException(
//					"Unable to retrieve the DAPCService. ", se);
//		} catch (MalformedURLException murle) {
//			throw new ServiceFactoryException(
//					"Unable to retrieve the DAPCService. ", murle);
//		}
//
		return dapcService;
	}

	public PlaceService getPlaceService() throws ServiceFactoryException {
		PlaceService placeService = null;
//		try {
//			PlaceServiceServiceLocator psl = new PlaceServiceServiceLocator();
//			if (psl != null) {
//				placeService = psl.getPlaceService(new URL(new String(
//						baseGatewayLocation + "PlaceService")));
//				if (placeService == null) {
//					logger.error("Unable to access datasetService at "
//							+ baseGatewayLocation + "PlaceService");
//				}
//			}
//		} catch (ServiceException se) {
//			throw new ServiceFactoryException(
//					"Unable to retrieve the PlaceService. ", se);
//		} catch (MalformedURLException murle) {
//			throw new ServiceFactoryException(
//					"Unable to retrieve the PlaceService. ", murle);
//		}
//
		return placeService;
	}

	/**
	 * Retrieves an instance of the stateless EJB Object specified in the
	 * parameters list.
	 * 
	 * @param jndi
	 *            the jndi String name to use for the lookup
	 * @param home
	 *            the home interface class object
	 * @param remote
	 *            the remote interface class object
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	// private <T extends javax.ejb.EJBHome> EJBObject
	// getStatelessBeanRemote(String jndi, Class<T> home)
	// throws ServiceFactoryException {
	//
	// EJBObject beanObj = null;
	//
	// try {
	// // retrieve the home interface using the supplied class and JNDI
	// EJBHome homeObj = ServiceLocator.getInstance().getRemoteHome(jndi,
	// home);
	//
	// // call the standard create() method to retrieve the remote
	// // interface
	// Method createMeth = ((Object) homeObj).getClass().getMethod(
	// "create", (Class[]) null);
	// beanObj = (EJBObject) createMeth.invoke(homeObj, (Object[]) null);
	// } catch (ServiceLocatorException sle) {
	// logger.warn(
	// "Error trying to retrieve '" + home.getClass().getName()
	// + "' from the locator service with the '" + jndi
	// + "' JNDI", sle);
	//
	// throw new ServiceFactoryException("Unable to retrieve the '"
	// + home.getClass().getName()
	// + "' from its remote location. ", sle);
	// } catch (Exception e) {
	// logger.warn(
	// "An unknown error has occurred when trying to create and "
	// + "and returning '" + home.getClass().getName()
	// + "'", e);
	//
	// throw new ServiceFactoryException(
	// "An unknown error has occurred when trying to retrieve '"
	// + home.getClass().getName() + "' EJB");
	// }
	//
	// return beanObj;
	// }

	/**
	 * Returns a reference to the RevenuesService Remote 'Stateless' Session
	 * Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 */
	// public RevenuesService getRevenuesService() throws
	// ServiceFactoryException {
	// RevenuesService revenuesService = null;
	// try {
	// Class<RevenuesServiceHome> rshc = RevenuesServiceHome.class;
	// if (rshc == null) {
	// throw new ServiceFactoryException(
	// "Unable to retrieve the RevenuesService, "
	// + "RevenuesServiceHome.class is null.");
	// }
	// revenuesService = (RevenuesService) getStatelessBeanRemote(
	// "ejb/RevenuesService", RevenuesServiceHome.class);
	//
	// // revenuesService = RevenuesClient.getService("appsman1,appsman2",
	// 7025);
	// } catch (Exception epaex) {
	// throw new ServiceFactoryException(
	// "Unable to retrieve the RevenuesService. ", epaex);
	// }
	//
	// return revenuesService;
	// }

	/**
     * Returns a reference to the PlacesService Remote 'Stateless' Session Bean.
     * 
     * @return a reference to the remote session bean stub
     * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
     */
//    public PlacesService getPlacesService1() throws ServiceFactoryException {
//    	PlacesService placesService = null;
//        try {
//        	placesService = PlacesClient.getService(null);
//        } catch (Exception epaex) {
//            throw new ServiceFactoryException(
//                    "REZA1: Unable to retrieve the PlacesService. " + epaex.getMessage(), epaex);
//        }
//
//        return placesService;
//    }
    
    /**
     * Returns a reference to the PlacesService Remote 'Stateless' Session Bean.
     * 
     * @return a reference to the remote session bean stub
     * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
     */
//    public PlacesService getPlacesService() throws ServiceFactoryException {
//    	PlacesService placesService = null;
//        try {
//            Class<PlacesServiceHome> pshc = PlacesServiceHome.class;
//            if (pshc == null) {
//                throw new ServiceFactoryException(
//                        "REZA2: Unable to retrieve the PlacesService, "
//                                + "PlacesServiceHome.class is null.");
//            }
//            placesService = (PlacesService) getStatelessBeanRemote(
//                    "ejb/PlacesService", PlacesServiceHome.class);
//        } catch (Exception epaex) {
//            throw new ServiceFactoryException(
//                    "REZA2: Unable to retrieve the PlacesService. " + epaex.getMessage(), epaex);
//        }
//
//        return placesService;
//    }
    
    /**
     * Returns a reference to the PlacesService Remote 'Stateless' Session Bean.
     * 
     * @return a reference to the remote session bean stub
     * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
     */
//    public PlacesService getPlacesService1() throws ServiceFactoryException {
//    	PlacesService placesService = null;
//        try {
//        	placesService = PlacesClient.getService(null);
//        } catch (Exception epaex) {
//            throw new ServiceFactoryException(
//                    "REZA1: Unable to retrieve the PlacesService. " + epaex.getMessage(), epaex);
//        }
//
//        return placesService;
//    }
    
    /**
     * Returns a reference to the PlacesService Remote 'Stateless' Session Bean.
     * 
     * @return a reference to the remote session bean stub
     * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
     */
//    public PlacesService getPlacesService() throws ServiceFactoryException {
//    	PlacesService placesService = null;
//        try {
//            Class<PlacesServiceHome> pshc = PlacesServiceHome.class;
//            if (pshc == null) {
//                throw new ServiceFactoryException(
//                        "REZA2: Unable to retrieve the PlacesService, "
//                                + "PlacesServiceHome.class is null.");
//            }
//            placesService = (PlacesService) getStatelessBeanRemote(
//                    "ejb/PlacesService", PlacesServiceHome.class);
//        } catch (Exception epaex) {
//            throw new ServiceFactoryException(
//                    "REZA2: Unable to retrieve the PlacesService. " + epaex.getMessage(), epaex);
//        }
//
//        return placesService;
//    }
    
    /**
	 * *************************************************************************
	 * ***** CETA Services
	 * ******************************************************
	 * ************************
	 */

	/**
	 * Returns a reference to the WorkFlow Remote 'Stateless' Session Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public FullComplianceEvalService getFullComplianceEvalService()
			throws ServiceFactoryException {
//		FullComplianceEvalService fceService = null;
//		fceService = new FullComplianceEvalBO();
//
//		return fceService;
		return App.getApplicationContext().getBean(FullComplianceEvalService.class);
	}

	/**
	 * Returns a reference to the WorkFlow Remote 'Stateless' Session Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public StackTestService getStackTestService()
			throws ServiceFactoryException {
//		StackTestService stService = null;
//		stService = new StackTestBO();
//
//		return stService;
		return App.getApplicationContext().getBean(StackTestService.class);
	}

	/**
	 * Returns a reference to the WorkFlow Remote 'Stateless' Session Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public FacilityHistoryService getFacilityHistoryService()
			throws ServiceFactoryException {
		return App.getApplicationContext().getBean(FacilityHistoryService.class);
	}

	/**
	 * Returns a reference to the WorkFlow Remote 'Stateless' Session Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public ComplaintsService getComplaintsService()
			throws ServiceFactoryException {

		return App.getApplicationContext().getBean(ComplaintsService.class);
	}

	/**
	 * Returns a reference to the WorkFlow Remote 'Stateless' Session Bean.
	 * 
	 * @return a reference to the remote session bean stub
	 * @throws us.oh.state.epa.stars2.webcommon.ServiceFactoryException
	 *             an error has occurred when trying to retrieve the bean
	 */
	public GDFService getGDFService() throws ServiceFactoryException {
		return App.getApplicationContext().getBean(GDFService.class);
	}
	
	public MonitoringService getMonitoringService()
			throws ServiceFactoryException {
		return App.getApplicationContext().getBean(MonitoringService.class);
	}
}
