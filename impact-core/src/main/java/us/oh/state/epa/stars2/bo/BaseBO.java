package us.oh.state.epa.stars2.bo;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.event.ImpactEvent;
import us.oh.state.epa.stars2.bo.event.ImpactEventListener;
import us.oh.state.epa.stars2.database.dao.AdHocDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAO;
import us.oh.state.epa.stars2.database.dao.CompEnfFacilityDAO;
import us.oh.state.epa.stars2.database.dao.ComplaintDAO;
import us.oh.state.epa.stars2.database.dao.ComplianceReportDAO;
import us.oh.state.epa.stars2.database.dao.CorrespondenceDAO;
import us.oh.state.epa.stars2.database.dao.DAOManager;
import us.oh.state.epa.stars2.database.dao.DataAccessObject;
import us.oh.state.epa.stars2.database.dao.DelegationRequestDAO;
import us.oh.state.epa.stars2.database.dao.DetailDataDAO;
import us.oh.state.epa.stars2.database.dao.DocumentDAO;
import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dao.EmissionsReportDAO;
import us.oh.state.epa.stars2.database.dao.EnforcementActionDAO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.FullComplianceEvalDAO;
import us.oh.state.epa.stars2.database.dao.GdfDAO;
import us.oh.state.epa.stars2.database.dao.GenericIssuanceDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.InvoiceDAO;
import us.oh.state.epa.stars2.database.dao.OffsitePceDAO;
import us.oh.state.epa.stars2.database.dao.PermitConditionDAO;
import us.oh.state.epa.stars2.database.dao.PermitDAO;
import us.oh.state.epa.stars2.database.dao.RelocateRequestDAO;
import us.oh.state.epa.stars2.database.dao.ReportsDAO;
import us.oh.state.epa.stars2.database.dao.ServiceCatalogDAO;
import us.oh.state.epa.stars2.database.dao.StackTestDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.VirtualExchangeServiceDAO;
import us.oh.state.epa.stars2.database.dao.WorkFlowDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.OffsetTrackingNonAttainmentAreaDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.workflow.engine.ProcessFlow;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;
import us.wy.state.deq.impact.database.dao.CompanyDAO;
import us.wy.state.deq.impact.database.dao.ContactDAO;
import us.wy.state.deq.impact.database.dao.ContinuousMonitorDAO;
import us.wy.state.deq.impact.database.dao.MonitoringDAO;
import us.wy.state.deq.impact.database.dao.ProjectTrackingDAO;

/**
 * @author Kbradley
 * 
 */
//TODO do not like how this class has dependencies to every functional subsystem.
//     this prevents a modular architecture and disallows system configurations composed of 
//     subsets of functionality
public class BaseBO {
	
    protected transient Logger logger = Logger.getLogger(this.getClass());
    
    private boolean testMode = "true".equals(Config.getEnvEntry("app/testMode"));

    @Autowired DAOManager daoManager;
    
    private final Set<ImpactEventListener<? extends ImpactEvent>> listeners = 
    		new HashSet<ImpactEventListener<? extends ImpactEvent>>();

    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected final void notifyListeners(ImpactEvent event) {
		for (ImpactEventListener listener : getListeners()) {
			listener.eventOccurred(event);
		}
	}
	
    public final Set<ImpactEventListener<? extends ImpactEvent>> getListeners() {
		return listeners;
	}

    
	protected boolean isFacilityInNonattainmentZone(Integer fpId)
		throws DAOException {
		return getFacilityNonattainmentAreas(fpId).size() > 0;
	}

	protected List<OffsetTrackingNonAttainmentAreaDef> getFacilityNonattainmentAreas(Integer fpId)
			throws DAOException {
		List<OffsetTrackingNonAttainmentAreaDef> nonattainmentAreas =
				new ArrayList<OffsetTrackingNonAttainmentAreaDef>();
		Address facilityAddress = null;
		List<Address> addresses = 
				facilityDAO(getSchema(CommonConst.STAGING_SCHEMA)).retrieveFacilityAddresses(fpId);
		for (Address address : addresses) {
			if (null == address.getEndDate()) {
				facilityAddress = address;
			}
		}
		if (null != facilityAddress) {
			Integer addressId = facilityAddress.getAddressId();
			CollectionUtils.addAll(nonattainmentAreas, 
					infrastructureDAO(getSchema(CommonConst.STAGING_SCHEMA)).findNonattainmentAreasByAddress(addressId));
		} else {
			throw new DAOException("Unable to determine the facility's current location, while checking if facility is in a non-attainment zone.");
		}
		return nonattainmentAreas;
	}

    
    
	public boolean isTestMode() {
    	return testMode;
    }


	protected DataAccessObject getStagingDAO(Class<? extends DataAccessObject> type) {
		return daoManager.getStagingDAO(type);
	}
	
	protected DataAccessObject getReadOnlyDAO(Class<? extends DataAccessObject> type) {
		return daoManager.getReadOnlyDAO(type);
	}
	
	protected DataAccessObject getDAO(Class<? extends DataAccessObject> type, Transaction trans) {
		return daoManager.getDAO(type,trans);
	}
	
	protected DataAccessObject getDAO(Class<? extends DataAccessObject> type) {
		return daoManager.getDAO(type);
	}

	
	protected DataAccessObject getDAO(Class<? extends DataAccessObject> type, 
			boolean staging) throws DAOException {
		return daoManager.getDAO(type,staging);
	}

	
	
	//======================================================================//

    


	protected FacilityDAO getFacilityDAO() throws DAOException {
		String schema = null;
		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			schema = "Staging";
		}

		return facilityDAO(schema);
	}
	
	protected FacilityDAO getFacilityDAO(boolean staging) throws DAOException {
		String schema = null;
		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			if (staging) {
				schema = "Staging";
			} else {
				schema = "ReadOnly";
			}
		}

		return facilityDAO(schema);
	}
	
	protected ContactDAO getContactDAO(boolean staging) throws DAOException {
		String schema = null;
		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			if (staging) {
				schema = "Staging";
			} else {
				schema = "ReadOnly";
			}
		}

		return contactDAO(schema);
	}
	

	protected ApplicationDAO getApplicationDAO() throws DAOException {
		String schema = null;
		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			schema = "Staging";
		}

		return applicationDAO(schema);
	}

	protected ApplicationEUTypeDAO getApplicationEUTypeDAO(String typeCd,
			Transaction trans, boolean staging) {
		ApplicationEUTypeDAO euTypeDAO = null;
		Class<DataAccessObject> c;
		String type = "us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAO";
		
		try {
			c = (Class<DataAccessObject>) Class.forName(type + typeCd);
		} catch (ClassNotFoundException e) {
			logger.debug("ApplicationEUTypeDAO not found, using default. " + e);
			try {
				c = (Class<DataAccessObject>) Class.forName(type + "Default");
			} catch (ClassNotFoundException e1) {
				logger.error("default ApplicationEUTypeDAO not found.");
				throw new RuntimeException(e1);
			}
		}
		try {
			euTypeDAO = (ApplicationEUTypeDAO) getDAO(c, staging);
		} catch (Exception e) {
			throw new RuntimeException("Unable to find DAO type: " + type);
		}
		return euTypeDAO;
	}


	protected EmissionUnitTypeDAO getEUTypeDAO(String typeCd, Transaction trans,
			boolean staging) {
		String schema;
		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			if (staging) {
				schema = "staging";
			} else {
				schema = "readOnly";
			}
		} else {
			schema = "readOnly";
		}

		String daoBeanName = schema + "EmissionUnitType" + typeCd + "DAO";
		logger.debug("EmissionUnitTypeDAO bean name: " + daoBeanName);
		
		EmissionUnitTypeDAO euTypeDAO = null;
		if (App.getApplicationContext().containsBeanDefinition(daoBeanName)) {
			euTypeDAO = App.getApplicationContext().getBean(daoBeanName,
					EmissionUnitTypeDAO.class);
		} else {
			// no extended attributes for this eu type; return null
		}
		return euTypeDAO;
	}

	protected ContinuousMonitorDAO getContinuousMonitorDAO() throws DAOException {
		String schema = null;
		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			schema = "Staging";
		}

		return continuousMonitorDAO(schema);
	}
	
	protected ContinuousMonitorDAO getContinuousMonitorDAO(boolean staging) throws DAOException {
		String schema = null;
		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			if (staging) {
				schema = "Staging";
			} else {
				schema = "ReadOnly";
			}
		}

		return continuousMonitorDAO(schema);
	}
	
	
	//======================================================================//
    
    
    protected void rollBackWorkflow(Integer externalId, Integer processTemplateId, Integer userId) throws RemoteException {
        ProcessFlow processFlow = retrieveProcessFlow(externalId, processTemplateId);
        if (processFlow != null) {
            Integer processId = processFlow.getProcessId();
            WorkFlowManager wfm = new WorkFlowManager();
            wfm.cancelProcess(processId, userId);
        } else {
            throw new DAOException("rollBackWorkflow FAILED: No process flow found for externalId=" + 
                    externalId +  ", processTemplateId = " + processTemplateId);
        }
    }
    
    private ProcessFlow retrieveProcessFlow(Integer externalId, Integer processTemplateId) throws RemoteException {

    	// retrieve the workflow service from the app context.  since workflow service is itself a BaseBO,
    	// we are unable to autowire and must look up manually here.
        ReadWorkFlowService workFlowService = App.getApplicationContext().getBean(ReadWorkFlowService.class);
        
        ProcessFlow permitProcessFlow = null;
        WorkFlowProcess wfProcess = new WorkFlowProcess();
        wfProcess.setProcessTemplateId(processTemplateId);
        wfProcess.setExternalId(externalId);
        wfProcess.setCurrent(true);
        int activeProcessFlowCount = 0;
        WorkFlowProcess[] processes = workFlowService.retrieveProcessList(wfProcess);
        for (WorkFlowProcess process : processes) {
            ProcessFlow tmpProcessFlow = workFlowService.retrieveActiveProcessFlow(process.getProcessId());
            if (tmpProcessFlow == null || tmpProcessFlow.getEndDt() != null) {
                continue;
            }
            permitProcessFlow = tmpProcessFlow;
            activeProcessFlowCount++;
        }
        if (activeProcessFlowCount != 1) {
            logger.error("Unexpected result querying work flow processes for external id " + externalId
                   + " " + processes.length + " work flow processes found.");
            permitProcessFlow = null;
        }
        return permitProcessFlow;
    }
    
    /**
     * Get the appropriate schema based on whether this is an external app.
     * @return
     */
    protected String getSchema(String portalSchema) {
        String schema = null;
        if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
            schema = portalSchema;
        }
        return schema;
    }

    private DataAccessObject getDAO(Transaction trans, Class<? extends DataAccessObject> type, String schemaQualifer) throws DAOException {
    	return daoManager.getDAO(trans,type,schemaQualifer);
    }
    
    protected ApplicationDAO applicationDAO(String schema) throws DAOException {
        return (ApplicationDAO)getDAO(null, ApplicationDAO.class, schema);
    }

    protected ApplicationDAO applicationDAO() throws DAOException {
        return (ApplicationDAO)getDAO(null, ApplicationDAO.class, null);
    }
    
    protected ApplicationDAO applicationDAO(String schema, Transaction trans) throws DAOException {
        return (ApplicationDAO)getDAO(trans, ApplicationDAO.class, schema);
    }
    
    protected AdHocDAO adHocDAO() throws DAOException {
        return (AdHocDAO)getDAO(null, AdHocDAO.class, null);
    }

    protected ApplicationDAO applicationDAO(Transaction trans) throws DAOException {
        return (ApplicationDAO)getDAO(trans, ApplicationDAO.class, null);
    }

    protected ComplianceReportDAO complianceReportDAO(String schema) throws DAOException {
        return (ComplianceReportDAO)getDAO(null, ComplianceReportDAO.class, schema);
    }

    protected ComplianceReportDAO complianceReportDAO() throws DAOException {
        return (ComplianceReportDAO)getDAO(null, ComplianceReportDAO.class, null);
    }

    protected ComplianceReportDAO complianceReportDAO(Transaction trans) throws DAOException {
        return (ComplianceReportDAO)getDAO(trans, ComplianceReportDAO.class, null);
    }
    
    protected RelocateRequestDAO relocateRequestDAO(String schema) throws DAOException {
        return (RelocateRequestDAO)getDAO(null, RelocateRequestDAO.class, schema);
    }

    protected RelocateRequestDAO relocateRequestDAO() throws DAOException {
        return (RelocateRequestDAO)getDAO(null, RelocateRequestDAO.class, null);
    }

    protected RelocateRequestDAO relocateRequestDAO(Transaction trans) throws DAOException {
        return (RelocateRequestDAO)getDAO(trans, RelocateRequestDAO.class, null);
    }
    
    protected DelegationRequestDAO delegationRequestDAO() throws DAOException {
        return (DelegationRequestDAO)getDAO(null, DelegationRequestDAO.class, null);
    }

    protected DelegationRequestDAO delegationRequestDAO(Transaction trans) throws DAOException {
        return (DelegationRequestDAO)getDAO(trans, DelegationRequestDAO.class, null);
    }
    

    
    protected CompEnfFacilityDAO compEnfFacilityDAO(String schema) throws DAOException {
        return (CompEnfFacilityDAO)getDAO(null, CompEnfFacilityDAO.class, schema);
    }

    protected CompEnfFacilityDAO compEnfFacilityDAO() throws DAOException {
        return (CompEnfFacilityDAO)getDAO(null, CompEnfFacilityDAO.class, null);
    }

    protected CompEnfFacilityDAO compEnfFacilityDAO(Transaction trans) throws DAOException {
        return (CompEnfFacilityDAO)getDAO(trans, CompEnfFacilityDAO.class, null);
    }
    
    protected DelegationRequestDAO delegationRequestDAO(String schema) throws DAOException {
        return (DelegationRequestDAO)getDAO(null, DelegationRequestDAO.class, schema);
    }
    
    protected CorrespondenceDAO correspondenceDAO() throws DAOException {
        return (CorrespondenceDAO)getDAO(null, CorrespondenceDAO.class, null);
    }

    protected MonitoringDAO monitoringDAO() throws DAOException {
        return (MonitoringDAO)getDAO(null, MonitoringDAO.class, null);
    }

    protected MonitoringDAO monitoringDAO(String schema) throws DAOException {
        return (MonitoringDAO)getDAO(null, MonitoringDAO.class, schema);
    }

    protected CorrespondenceDAO correspondenceDAO(Transaction trans) throws DAOException {
        return (CorrespondenceDAO)getDAO(trans, CorrespondenceDAO.class, null);
    }

    protected DetailDataDAO detailDataDAO(String schema) throws DAOException {
        return (DetailDataDAO)getDAO(null, DetailDataDAO.class, schema);
    }

    protected DetailDataDAO detailDataDAO() throws DAOException {
        return (DetailDataDAO)getDAO(null, DetailDataDAO.class, null);
    }

    protected DetailDataDAO detailDataDAO(Transaction trans) throws DAOException {
        return (DetailDataDAO)getDAO(trans, DetailDataDAO.class, null);
    }

    protected DocumentDAO documentDAO(String schema) throws DAOException {
        return (DocumentDAO)getDAO(null, DocumentDAO.class, schema);
    }

    protected DocumentDAO documentDAO() throws DAOException {
        return (DocumentDAO)getDAO(null, DocumentDAO.class, null);
    }

    protected DocumentDAO documentDAO(Transaction trans) throws DAOException {
        return (DocumentDAO)getDAO(trans, DocumentDAO.class, null);
    }
    
    protected FullComplianceEvalDAO fullComplianceEvalDAO(String schema) throws DAOException {
        return (FullComplianceEvalDAO)getDAO(null, FullComplianceEvalDAO.class, schema);
    }

    protected FullComplianceEvalDAO fullComplianceEvalDAO() throws DAOException {
        return (FullComplianceEvalDAO)getDAO(null, FullComplianceEvalDAO.class, null);
    }

    protected FullComplianceEvalDAO fullComplianceEvalDAO(Transaction trans) throws DAOException {
        return (FullComplianceEvalDAO)getDAO(trans, FullComplianceEvalDAO.class, null);
    }
    
    protected StackTestDAO stackTestDAO(String schema) throws DAOException {
        return (StackTestDAO)getDAO(null, StackTestDAO.class, schema);
    }

    protected StackTestDAO stackTestDAO() throws DAOException {
        return (StackTestDAO)getDAO(null, StackTestDAO.class, null);
    }

    protected StackTestDAO stackTestDAO(Transaction trans) throws DAOException {
        return (StackTestDAO)getDAO(trans, StackTestDAO.class, null);
    }

    protected EmissionsReportDAO emissionsReportDAO(String schema) throws DAOException {
        return (EmissionsReportDAO)getDAO(null, EmissionsReportDAO.class, schema);
    }

    protected EmissionsReportDAO emissionsReportDAO() throws DAOException {
        return (EmissionsReportDAO)getDAO(null, EmissionsReportDAO.class, null);
    }

    protected EmissionsReportDAO emissionsReportDAO(Transaction trans) throws DAOException {
        return (EmissionsReportDAO)getDAO(trans, EmissionsReportDAO.class, null);
    }
    
    protected GdfDAO gdfDAO(String schema) throws DAOException {
        return (GdfDAO)getDAO(null, GdfDAO.class, schema);
    }

    protected GdfDAO gdfDAO() throws DAOException {
        return (GdfDAO)getDAO(null, GdfDAO.class, null);
    }

    protected GdfDAO gdfDAO(Transaction trans) throws DAOException {
        return (GdfDAO)getDAO(trans, GdfDAO.class, null);
    }
    
    protected ComplaintDAO complaintDAO(String schema) throws DAOException {
        return (ComplaintDAO)getDAO(null, ComplaintDAO.class, schema);
    }

    protected ComplaintDAO complaintDAO() throws DAOException {
        return (ComplaintDAO)getDAO(null, ComplaintDAO.class, null);
    }

    protected ComplaintDAO complaintDAO(Transaction trans) throws DAOException {
        return (ComplaintDAO)getDAO(trans, ComplaintDAO.class, null);
    }
    
    protected FacilityDAO facilityDAO(String schema) throws DAOException {
        return (FacilityDAO)getDAO(null, FacilityDAO.class, schema);
    }

    protected FacilityDAO facilityDAO() throws DAOException {
        return (FacilityDAO)getDAO(null, FacilityDAO.class, null);
    }

    protected FacilityDAO facilityDAO(Transaction trans) throws DAOException {
        return (FacilityDAO)getDAO(trans, FacilityDAO.class, null);
    }
    
    protected CompanyDAO companyDAO(String schema) throws DAOException {
        return (CompanyDAO)getDAO(null, CompanyDAO.class, schema);
    }

    protected CompanyDAO companyDAO() throws DAOException {
        return (CompanyDAO)getDAO(null, CompanyDAO.class, null);
    }

    protected CompanyDAO companyDAO(Transaction trans) throws DAOException {
        return (CompanyDAO)getDAO(trans, CompanyDAO.class, null);
    }

    protected ContactDAO contactDAO(String schema) throws DAOException {
        return (ContactDAO)getDAO(null, ContactDAO.class, schema);
    }
    
    protected ContactDAO contactDAO() throws DAOException {
        return (ContactDAO)getDAO(null, ContactDAO.class, null);
    }

    protected ContactDAO contactDAO(Transaction trans) throws DAOException {
        return (ContactDAO)getDAO(trans, ContactDAO.class, null);
    }

    protected InfrastructureDAO infrastructureDAO(String schema) throws DAOException {
        return (InfrastructureDAO)getDAO(null, InfrastructureDAO.class, schema);
    }

    protected InfrastructureDAO infrastructureDAO() throws DAOException {
        return (InfrastructureDAO)getDAO(null, InfrastructureDAO.class, null);
    }

    protected InfrastructureDAO infrastructureDAO(Transaction trans) throws DAOException {
        return (InfrastructureDAO)getDAO(trans, InfrastructureDAO.class, null);
    }

    protected InvoiceDAO invoiceDAO() throws DAOException {
        return (InvoiceDAO)getDAO(null, InvoiceDAO.class, null);
    }

    protected InvoiceDAO invoiceDAO(Transaction trans) throws DAOException {
        return (InvoiceDAO)getDAO(trans, InvoiceDAO.class, null);
    }

    protected PermitDAO permitDAO(String schema) throws DAOException {
        return (PermitDAO)getDAO(null, PermitDAO.class, schema);
    }

    protected PermitDAO permitDAO() throws DAOException {
        return (PermitDAO)getDAO(null, PermitDAO.class, null);
    }

    protected PermitConditionDAO permitConditionDAO() throws DAOException {
        return (PermitConditionDAO)getDAO(null, PermitConditionDAO.class, null);
    }

    protected PermitDAO permitDAO(Transaction trans) throws DAOException {
        return (PermitDAO)getDAO(trans, PermitDAO.class, null);
    }

    protected ReportsDAO reportsDAO(String schema) throws DAOException {
        return (ReportsDAO)getDAO(null, ReportsDAO.class, schema);
    }

    protected ReportsDAO reportsDAO() throws DAOException {
        return (ReportsDAO)getDAO(null, ReportsDAO.class, null);
    }

    protected ReportsDAO reportsDAO(Transaction trans) throws DAOException {
        return (ReportsDAO)getDAO(trans, ReportsDAO.class, null);
    }

    protected GenericIssuanceDAO genericIssuanceDAO() throws DAOException {
        return (GenericIssuanceDAO)getDAO(null, GenericIssuanceDAO.class, null);
    }

    protected GenericIssuanceDAO genericIssuanceDAO(Transaction trans) throws DAOException {
        return (GenericIssuanceDAO)getDAO(trans, GenericIssuanceDAO.class, null);
    }

    protected ServiceCatalogDAO serviceCatalogDAO() throws DAOException {
        return (ServiceCatalogDAO)getDAO(null, ServiceCatalogDAO.class, null);
    }
    
    protected ServiceCatalogDAO serviceCatalogDAO(Transaction trans) throws DAOException {
        return (ServiceCatalogDAO)getDAO(trans, ServiceCatalogDAO.class, null);
    }

    protected ServiceCatalogDAO serviceCatalogDAO(String schema) throws DAOException {
        return (ServiceCatalogDAO)getDAO(null, ServiceCatalogDAO.class, schema);
    }

    protected WorkFlowDAO workFlowDAO(String schema) throws DAOException {
        return (WorkFlowDAO)getDAO(null, WorkFlowDAO.class, schema);
    }

    protected WorkFlowDAO workFlowDAO() throws DAOException {
        return (WorkFlowDAO)getDAO(null, WorkFlowDAO.class, null);
    }

    protected WorkFlowDAO workFlowDAO(Transaction trans) throws DAOException {
        return (WorkFlowDAO)getDAO(trans, WorkFlowDAO.class, null);
    }

    protected OffsitePceDAO offsitePceDAO(String schema) throws DAOException {
        return (OffsitePceDAO)getDAO(null, OffsitePceDAO.class, schema);
    }

    protected OffsitePceDAO offsitePceDAO() throws DAOException {
        return (OffsitePceDAO)getDAO(null, OffsitePceDAO.class, null);
    }

    protected OffsitePceDAO offsitePceDAO(Transaction trans) throws DAOException {
        return (OffsitePceDAO)getDAO(trans, OffsitePceDAO.class, null);
    }
    
    protected EnforcementActionDAO enforcementActionDAO() throws DAOException {
        return (EnforcementActionDAO)getDAO(null, EnforcementActionDAO.class, null);
    }
    
    protected ContinuousMonitorDAO continuousMonitorDAO() throws DAOException {
        return (ContinuousMonitorDAO)getDAO(null, ContinuousMonitorDAO.class, null);
    }
    
    protected ContinuousMonitorDAO continuousMonitorDAO(String schema) throws DAOException {
        return (ContinuousMonitorDAO)getDAO(null, ContinuousMonitorDAO.class, schema);
    }
    
    protected ContinuousMonitorDAO continuousMonitorDAO(Transaction trans) throws DAOException {
        return (ContinuousMonitorDAO)getDAO(trans, ContinuousMonitorDAO.class, null);
    }
    
    protected ProjectTrackingDAO projectTrackingDAO() throws DAOException {
        return (ProjectTrackingDAO)getDAO(null, ProjectTrackingDAO.class, null);
    }

    protected ProjectTrackingDAO projectTrackingDAO(String schema) throws DAOException {
        return (ProjectTrackingDAO)getDAO(null, ProjectTrackingDAO.class, schema);
    }

    protected VirtualExchangeServiceDAO virtualExchangeServiceDAO() throws DAOException {
        return (VirtualExchangeServiceDAO)getDAO(null, VirtualExchangeServiceDAO.class, null);
    }


    protected void cancelTransaction(Transaction trans, RemoteException re) throws DAOException {
        logger.error(re.getMessage(), re);
        throw new DAOException(re.getMessage());
    }
    
    @Deprecated
    protected void cancelTransaction(Transaction trans, DAOException de) throws DAOException {
        logger.error(de.getMessage(), de);
        throw de;
    }
    
    protected void cancelTransaction(String msg, Transaction trans, DAOException de) throws DAOException {
        logger.error(de.getMessage() + " with " + msg, de);
        throw de;
    }
    
    @Deprecated
    protected void closeTransaction(Transaction trans) throws DAOException {
    }
    
    public void checkNull(Object inParm) throws DAOException {
        if (inParm == null) {
            throw new DAOException("Input parameter to DAO method null!");
        }
    }

    public boolean isInternalApp() {
    	return CompMgr.getAppName().equals(CommonConst.INTERNAL_APP);
    }
    
	public boolean isPortalApp() {
		return CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP);
	}

	public boolean isPublicApp() {
		return CompMgr.getAppName().equals(CommonConst.PUBLIC_APP);
	}
    
    protected void addEntryToZip(String entryName, InputStream is, ZipOutputStream zos) throws IOException {
        byte[] readBuffer = new byte[2156]; 
        int bytesIn = 0;
        
        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);
        //now write the content of the file to the ZipOutputStream 
        while((bytesIn = is.read(readBuffer)) != -1) 
        { 
            zos.write(readBuffer, 0, bytesIn); 
        } 
        //close the Stream 
        is.close(); 
    }
    
    protected boolean isDifferent(String s1, String s2) {
        if(s1 == null && s2 != null) return true;
        if(s1 == null && s2 == null) return false;
        return !s1.equals(s2);
    }
    
    protected boolean isDifferent(boolean b1, boolean b2) {
        if(b1 && b2) return false;
        if(!b1 && !b2) return false;
        return true;
    }
    
    protected String printableValue(boolean b) {
        if(b) return "Yes";
        else return "No";
    }
    
    protected boolean tooSmall(String a, String b) { // b should not be smaller
        boolean rtn = false;
        if(!(a == null || b == null || a.trim().length() == 0 || b.trim().length() == 0)) {
            float af = Float.parseFloat(a.replace(",", ""));
            float bf = Float.parseFloat(b.replace(",", ""));
            if(bf < af) rtn = true;
        }
        return rtn;
    }
    
	protected void removeProcessFlows(Integer externalId,
			Integer processTemplateId, Integer userId) throws RemoteException {
		
		ProcessFlow processFlow = retrieveProcessFlow(externalId, processTemplateId);
        if (processFlow != null) {
            Integer processId = processFlow.getProcessId();
            WorkFlowManager wfm = new WorkFlowManager();
    		WorkFlowResponse removeProcessFlowsResp = wfm.removeProcessFlows(
    				processId, userId);
    		if (removeProcessFlowsResp.hasError()
    				|| removeProcessFlowsResp.hasFailed()) {
    			
    			for (String s : removeProcessFlowsResp.getErrorMessages()){
    	            DisplayUtil.displayError(s);
    	            logger.warn(s);
    	        }
    	        for (String s : removeProcessFlowsResp.getRecommendationMessages()){
    	            DisplayUtil.displayInfo(s);
    	            logger.debug(s);
    	        }
    			
    			throw new DAOException(
    					"removeProcessFlows FAILED: An error occured when trying to remove workflow process "
    							+ processId);
    		}
        } else {
            throw new DAOException("removeProcessFlows FAILED: No process flow found for externalId=" + 
                    externalId +  ", processTemplateId = " + processTemplateId);
        }
	}
}
