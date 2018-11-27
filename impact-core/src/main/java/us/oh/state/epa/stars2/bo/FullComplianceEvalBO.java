package us.oh.state.epa.stars2.bo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;

import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.CompEnfFacilityDAO;
import us.oh.state.epa.stars2.database.dao.DocumentDAO;
import us.oh.state.epa.stars2.database.dao.FullComplianceEvalDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.PermitDAO;
import us.oh.state.epa.stars2.database.dao.StackTestDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dao.WorkFlowDAO;
import us.oh.state.epa.stars2.database.dao.document.CorrespondenceSQLDAO;
import us.oh.state.epa.stars2.database.dao.permit.PermitSQLDAO;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.AirProgramCompliance;
import us.oh.state.epa.stars2.database.dbObjects.ceta.AmbientConditions;
import us.oh.state.epa.stars2.database.dbObjects.ceta.Evaluator;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FacilityHistory;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceAmbientMonitorLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceApplicationSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceAttachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceContinuousMonitorLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceEmissionsInventoryLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FcePermitCondition;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceScheduleRow;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceStackTestSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.database.dbObjects.ceta.InspectionNote;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SearchDateRange;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SiteVisit;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SiteVisitNote;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestedPollutant;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SvAttachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.TestVisitDate;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReport;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportNote;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.permit.ComplianceStatusEvent;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.util.DbInteger;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.CetaStackTestResultsDef;
import us.oh.state.epa.stars2.def.ComplianceReportSearchDateByDef;
import us.oh.state.epa.stars2.def.ComplianceReportStatusDef;
import us.oh.state.epa.stars2.def.CorrespondenceDirectionDef;
import us.oh.state.epa.stars2.def.FacilityTypeDef;
import us.oh.state.epa.stars2.def.FceAttachmentTypeDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.RegulatoryRequirementTypeDef;
import us.oh.state.epa.stars2.def.SiteVisitTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.ValidationException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;
import us.wy.state.deq.impact.def.ComplianceTrackingEventTypeDef;
import us.wy.state.deq.impact.def.FCEInspectionReportStateDef;
import us.wy.state.deq.impact.def.FCEPreSnapshotTypeDef;
import us.wy.state.deq.impact.def.PermitConditionComplianceStatusDef;

/** Business object. */
@Transactional(rollbackFor=Exception.class)
@Service
public class FullComplianceEvalBO extends BaseBO implements FullComplianceEvalService {

	@Autowired
	private PermitService permitService;
	
	@Autowired
	private PermitConditionService permitConditionService;
	
	@Autowired
	private MonitoringService monitoringService;
	
	@Autowired
	private ReadWorkFlowService readWorkFlowService;
	
	@Autowired
	private FacilityHistoryService facilityHistoryService;
	
	@Autowired
	private StackTestService stackTestService;
	
	@Autowired
	private FacilityService facilityService;
	
	public PermitService getPermitService() {
		return permitService;
	}

	public void setPermitService(PermitService permitService) {
		this.permitService = permitService;
	}

	public PermitConditionService getPermitConditionService() {
		return permitConditionService;
	}

	public void setPermitConditionService(PermitConditionService permitConditionService) {
		this.permitConditionService = permitConditionService;
	}
	
	public MonitoringService getMonitoringService() {
		return monitoringService;
	}

	public void setMonitoringService(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}
	
	public ReadWorkFlowService getReadWorkFlowService() {
		return readWorkFlowService;
	}

	public void setReadWorkFlowService(ReadWorkFlowService readWorkFlowService) {
		this.readWorkFlowService = readWorkFlowService;
	}

	public FacilityHistoryService getFacilityHistoryService() {
		return facilityHistoryService;
	}

	public void setFacilityHistoryService(FacilityHistoryService facilityHistoryService) {
		this.facilityHistoryService = facilityHistoryService;
	}

	public StackTestService getStackTestService() {
		return stackTestService;
	}

	public void setStackTestService(StackTestService stackTestService) {
		this.stackTestService = stackTestService;
	}
	
    public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}	
	

	/**
     * @param facilityId
     * @param fceId
     * @return FullComplianceEval
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     * 
     */
    public FullComplianceEval retrieveFce(String facilityId, Integer fceId)
    throws DAOException {
    	
        FullComplianceEval fce = null;

        try {

        	fce = fullComplianceEvalDAO().retrieveFce(fceId);

        	if (fce != null) {
                SiteVisit[] visits = retrieveSiteVisitsByFce(fce.getFacilityId(), fce.getId());
                fce.setAssocSiteVisits(visits);
                visits = fullComplianceEvalDAO().retrieveVisitsUnassigned(fce.getFacilityId());
                fce.setSiteVisits(visits);
                StackTestService stackTestService = getStackTestService();
                StackTest[] tests = stackTestService.retrieveStacktestByFce(fceId);
                fce.setAssocStackTests(tests);
                tests = stackTestService.retrieveStacktestsUnassigned(fce.getFacilityId());
                fce.setStackTests(tests);
                List<FceAttachment> attachments = fullComplianceEvalDAO().retrieveFceAttachments(fceId);
                fce.setAttachments(attachments);
                fce.setInspectionNotes(fullComplianceEvalDAO().retrieveInspectionNotes(fce.getId()));
                List<ComplianceStatusEvent> cse = permitConditionDAO().retrieveComplianceStatusEventListByReferencedInspection(fce.getId());
                fce.setAssocComplianceStatusEvents(cse);
                List<Evaluator> additionalAqdStaff = fullComplianceEvalDAO().retrieveAdditionalAQDStaffByFceId(fce.getId());
                fce.setAdditionalAqdStaffPresent(additionalAqdStaff);
                retrieveAmbientConditionsByFce(fce);
                List<Integer> associatedPermits = fullComplianceEvalDAO().retrieveAssociatedPermitIdsByFceId(fce.getId());
                fce.setAssociatedPermits(associatedPermits);
                List<FcePermitCondition> fcePermitConditions = fullComplianceEvalDAO()
        				.retrieveAssociatedPermitConditionsByFceId(fce.getId());
        		fce.setFcePermitConditions(fcePermitConditions);
        		List<String> inspections = fullComplianceEvalDAO().retrieveInspectionIdsForLastFceId(fceId);
        		fce.setInspectionsReferencedIn(inspections);
            }
            else {
                String s = "Inspection not found, Inspection Id=" + fceId;
                logger.error(s);              
                return null;
                //throw new DAOException(s, new Exception());
            }

        	FacilityHistoryService facilityHistoryBO = getFacilityHistoryService();
            FacilityHistory fh = facilityHistoryBO.retrieveFacilityHistory(fce.getFacilityHistId());
            fce.setFacilityHistory(fh);

        } catch (RemoteException de) {
            logger.error("fceId=" + fceId + " " + de.getMessage(), de);
            throw new DAOException(de.getMessage(), de);
        }

        return fce;
    }
    
    /**
     * @param fceId
     * @return FullComplianceEval
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     * 
     */
    public FullComplianceEval retrieveFceOnly(Integer fceId)
    throws DAOException{
        FullComplianceEval fce = null;
        try {
            fce = fullComplianceEvalDAO().retrieveFce(fceId);
        } catch(RemoteException de) {
            logger.error("fceId=" + fceId + " " + de.getMessage(), de);
            throw new DAOException(de.getMessage(), de);
        }
        return fce;
    }
    
    /**
     * @param inspId
     * @return FullComplianceEval
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     * 
     */
    public FullComplianceEval retrieveFceOnly(String inspId)
    throws DAOException{
        FullComplianceEval fce = null;
        try {
            fce = fullComplianceEvalDAO().retrieveFce(inspId);
        } catch(RemoteException de) {
            logger.error("inspId=" + inspId + " " + de.getMessage(), de);
            throw new DAOException(de.getMessage(), de);
        }
        return fce;
    }
    
    /**
     * @param fceId
     * @return List<FceAttachment>
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     * 
     */
    public List<FceAttachment> retrieveFceAttachments(Integer fceId)
    throws DAOException{
        List<FceAttachment> attachments = null;
        checkNull(fceId);
        try {
            attachments = fullComplianceEvalDAO().retrieveFceAttachments(fceId);
        } catch(RemoteException de) {
            logger.error("fceId=" + fceId + " " + de.getMessage(), de);
            throw new DAOException(de.getMessage(), de);
        }
        return attachments;
    }
    
    /**
     * @param visitId
     * @return List<FceAttachment>
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     * 
     */
    public List<SvAttachment> retrieveSvAttachments(Integer visitId)
    throws DAOException{
        List<SvAttachment> attachments = null;
        checkNull(visitId);
        try {
            attachments = fullComplianceEvalDAO().retrieveSvAttachments(visitId);
        } catch(RemoteException de) {
            logger.error("fceId=" + visitId + " " + de.getMessage(), de);
            throw new DAOException(de.getMessage(), de);
        }
        return attachments;
    }
    
    /**
     * @param siteVisit
     * @return boolean
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     * 
     */
    public boolean isVisitDup(SiteVisit siteVisit)
    throws DAOException {
        /*
         * It would be a duplicate if there is a visit in the database with the same facility id,
         * visit date, and visit type.  Be sure to exclude comparing with a site visit having the
         * same site visit id.  Also, check for a range of visit dates to cover the entire day to match
         * any timestamp that is anywhere within the day.
         * 
         * If it is a new visit, the site visit id will be null (and not match any id in the database).
         */
        boolean rtn = false;
        try {
            rtn = fullComplianceEvalDAO().isVisitDup(siteVisit);
        } catch(DAOException de) {
            logger.error("siteVisitId=" + siteVisit.getId() + " " + de.getMessage(), de);
            throw de;
        }
        return rtn;
    }

    /**
     * @param visitId
     * @return SiteVisit
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     * 
     */
    public SiteVisit retrieveSiteVisit(Integer visitId)
    throws RemoteException {

    	SiteVisit visit = null;
        try {
            visit = fullComplianceEvalDAO().retrieveSiteVisit(visitId);
            if(visit != null) {
                List<SvAttachment> attachments = fullComplianceEvalDAO().retrieveSvAttachments(visit.getId());
                visit.setAttachments(attachments);
                visit.setSiteVisitNotes(fullComplianceEvalDAO().retrieveSiteVisitNotes(visit.getId()));
                FacilityHistoryService facilityHistoryBO = getFacilityHistoryService();
                FacilityHistory fh = facilityHistoryBO.retrieveFacilityHistory(visit.getFacilityHistId());
                visit.setFacilityHistory(fh);
                visit.setInspectionsReferencedIn(fullComplianceEvalDAO().retrieveInspectionIdsForSiteVisitId(visitId));
            } else {
                String s = "Site Visit not found, siteVisitId=" + visitId;
                logger.error(s);
                return null;
                //throw new DAOException(s, new Exception());
            }
        } catch(RemoteException de) {
            logger.error("siteVisitId=" + visitId + " " + de.getMessage(), de);
            throw de;
        }

        return visit;
    }
    
    /**
     * @param fceId
     * @return SiteVisit[]
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     * 
     */
    public SiteVisit[] retrieveSiteVisitsByFce(String facilityId, Integer fceId)
    throws DAOException {
        SiteVisit[] visits = null;
        try {
            visits = fullComplianceEvalDAO().retrieveSiteVisitsByFce(fceId);
//          fill in Witnesses when type is Stack Test
            FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
            for(SiteVisit sv : visits) {
                if(SiteVisitTypeDef.STACK_TEST.equals(sv.getVisitType())) {
                    sv.setEvaluators(fceDAO.retrieveEmissionsTestWitnesses(facilityId, sv.getVisitDate()));
                }
            }
        } catch(DAOException de) {
            logger.error("fceId=" + fceId + " " + de.getMessage(), de);
            throw de;
        }
        return visits;
    }
    
    /**
     * @param fceId
     * @param SiteVisit[]
     * @returns void
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     * 
     */
    public void saveReassign(Integer fceId, SiteVisit[] visits)
    throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        SiteVisit svCopy = null;
        try {
            for(SiteVisit sv : visits) {
                svCopy = sv;
                if(sv.isSelected()) {
                    sv.setFceId(fceId);
                    modifySiteVisit(sv, trans);
                }
            }
        } catch(DAOException de) {
            logger.error("fceId=" + fceId + "; " + "siteVisitId=" + svCopy.getId() + " " + de.getMessage(), de);
            throw de;
        }
    }
    
    /**
     * @param newFce
     * @return FullComplianceEval
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */  
    public FullComplianceEval createFce(FullComplianceEval newFce) 
    throws DAOException, ValidationException {
        checkNull(newFce);
        checkNull(newFce.getFpId());
        Transaction trans = TransactionFactory.createTransaction();
        FullComplianceEval ret = null;
        try {
            ret = createFce(newFce, trans);       
            trans.complete();
        } catch (DAOException e) {
            logger.error("fceId=" + newFce.getId() + " " + e.getMessage(), e);
            cancelTransaction(trans, e);
        } finally {
            closeTransaction(trans);
        }
        return ret;
    }
    
    /**
     * @param newFCE
     * @param trans
     * @return FullComplianceEval
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */  
    public FullComplianceEval createFce(FullComplianceEval newFCE, Transaction trans) throws DAOException {
    	
        FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO(trans);
        FullComplianceEval rtn = null;
        // create/reference history record
        if (newFCE.getDateEvaluated() != null) {
            // Create history record at the time the inspection completion entered
            try {
                FacilityHistoryService fsBO = getFacilityHistoryService();
                FacilityHistory hist = fsBO.createFacilityHistory(newFCE
                        .getFpId(), trans);
                newFCE.setFacilityHistId(hist.getFacilityHistId());
                newFCE.setFacilityHistory(hist);
            } catch (RemoteException re) {
                String s = "createFce failed for fpId=" + newFCE.getFpId();
                logger.error(s, re);
            }
        }
        
        //Adding as part of 10.0 enhancement. Creating Legacy Inspections in Finalized state.
        if (newFCE.isLegacyInspection()) {
        	newFCE.setInspectionReportStateCd(FCEInspectionReportStateDef.FINAL);
        }
        updateInspectionDate(newFCE);
        rtn = fceDAO.createFce(newFCE);
        // create directory for fce documents
        String path = null;
        try {
            path = getFceDir(rtn);
            File dir = new File(path);
            File parentDir = dir.getParentFile();
            if (!parentDir.exists()) {
                DocumentUtil.mkDir(parentDir.getPath());
            }
            DocumentUtil.mkDir(path);
        } catch (IOException e) {
            logger.error("Exception creating file store directory " + path, e);
            throw new DAOException("Exception creating file store directory", e);
        }
		for (Evaluator aqdStaff : rtn.getAdditionalAqdStaffPresent()) {
			if (aqdStaff.getEvaluator() == null)
				continue;
			createAdditionalAQDStaff(aqdStaff.getEvaluator(), rtn.getId());
		}

		createFceAmbientConditions(rtn, rtn.getId());

        return rtn;
    }

    /**
     * @param newFce
     * @return FullComplianceEval
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */  
    public FullComplianceEval createMigratedFce(FullComplianceEval newFce)
        throws DAOException, ValidationException {
    	
        checkNull(newFce);
        checkNull(newFce.getFpId());
        checkNull(newFce.getFacilityHistory());
        Transaction trans = TransactionFactory.createTransaction();

        FullComplianceEval ret = null;

        try {
            ret = createMigratedFce(newFce, trans);       
            trans.complete();
        } catch (DAOException e) {
            logger.error("fceId=" + newFce.getId() + " " + e.getMessage(), e);
            cancelTransaction(trans, e);
        } finally {
            closeTransaction(trans);
        }
        
        return ret;
    }
    
    /**
     * @param newFCE
     * @param trans
     * @return FullComplianceEval
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */  
    public FullComplianceEval createMigratedFce(FullComplianceEval newFce, Transaction trans) throws DAOException {
        FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO(trans);
        FullComplianceEval rtn = null;
        // create history record
        CompEnfFacilityDAO compEnfFacilityDAO = compEnfFacilityDAO(trans);
        FacilityHistory fhR = compEnfFacilityDAO.createFacilityHistory(newFce.getFacilityHistory());
        Integer hId = fhR.getFacilityHistId();
        newFce.setFacilityHistId(hId);
        rtn = fceDAO.createFce(newFce);
        // create directory for fce documents
        String path = null;
        try {
            path = getFceDir(rtn);
            File dir = new File(path);
            File parentDir = dir.getParentFile();
            if (!parentDir.exists()) {
                DocumentUtil.mkDir(parentDir.getPath());
            }
            DocumentUtil.mkDir(path);
        } catch (IOException e) {
            logger.error("Exception creating file store directory " + path, e);
            throw new DAOException("Exception creating file store directory", e);
        }
        return rtn;
    }
    
    private String getFceDir(FullComplianceEval fce) {
        return "/Facilities/" + fce.getFacilityId()
        + "/Inspection/" + fce.getId();
    }
    
    private String getFceParentDir(FullComplianceEval fce) {
        return "/Facilities/" + fce.getFacilityId()
        + "/Inspection";
    }
    
    /**
     * @param newFCE
     * @param trans
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */  
    public void createFceGetFpId(String facilityId, FullComplianceEval newFCE)
    		throws DAOException {
    	
        FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();  
        FullComplianceEval rtn = fceDAO.createFceGetFpId(facilityId, newFCE);
        
        // Create directory for fce documents.
        String path = null;
        try {
        	
            path = getFceDir(newFCE);
            File dir = new File(path);
            File parentDir = dir.getParentFile();
            if (!parentDir.exists()) {
                DocumentUtil.mkDir(parentDir.getPath());
            }
            DocumentUtil.mkDir(path);
            
        } catch (IOException e) {
            logger.error("Exception creating file store directory " + path, e);
            throw new DAOException("Exception creating file store directory", e);
        }

		createFceAmbientConditions(rtn, rtn.getId());

    }
    
    /**
     * @param newFCE
     * @param trans
     * @return FullComplianceEval
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */  
    public void addMissingFceDir(FullComplianceEval fce) throws DAOException {
    	String path = File.separator + "Facilities" + File.separator + fce.getFacilityId()
    			+ File.separator + "Inspection";
    	try {
    		// create directory for fce documents
    		path = null;
    		path = getFceDir(fce);
    		File dir = new File(DocumentUtil.getFileStoreRootPath() + path.replaceAll(Matcher.quoteReplacement("\\"),  "/"));
    		File parentDir = dir.getParentFile();
    		if (!parentDir.exists()) {
    			DocumentUtil.mkDir(getFceParentDir(fce));
    			logger.error("Created Inspection directory " + path);
    		}
    		dir = new File(DocumentUtil.getFileStoreRootPath() + path);
    		if (!dir.exists()) {
    			DocumentUtil.mkDir(path);
    			logger.error("Created directory for Inspection " + fce.getId() + " at " + path);
    		}
    	} catch (IOException e) {
    		logger.error("Exception creating file store directory " + path, e);
    		throw new DAOException("Exception creating file store directory", e);
    	}
    }
    
    /**
     * @param newVisit
     * @return SiteVisit
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */  
    public SiteVisit createSiteVisit(SiteVisit newVisit) 
        throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        SiteVisit ret = null;
        checkNull(newVisit);
        checkNull(newVisit.getFpId());
        try {
            ret = createSiteVisit(newVisit, trans);       
            trans.complete();
        } catch (DAOException e) {
            logger.error("siteVisitId=" + newVisit.getId() + " " + e.getMessage(), e); 
            cancelTransaction(trans, e);
        } finally {
            closeTransaction(trans);
        }
        return ret;
    }
    /**
     * @param newVisit
     * @param trans
     * @throws DAOException
     * 
     * @return FullComplianceEval
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */  
    public SiteVisit createSiteVisit(SiteVisit newVisit, Transaction trans) throws DAOException {

    	// create/reference history record
        try {
            FacilityHistoryService fsBO = getFacilityHistoryService();
            FacilityHistory hist = fsBO.createFacilityHistory(newVisit.getFpId(), trans);
            newVisit.setFacilityHistId(hist.getFacilityHistId());
            newVisit.setFacilityHistory(hist);
        } catch(RemoteException re) {
            logger.error("Exception creating facility history: " + re.getMessage(), re);
            throw new DAOException("Exception creating facility history: " + re.getMessage(), re);            
        }
        FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO(trans);
        SiteVisit sv = fceDAO.createSiteVisit(newVisit);
        for(Evaluator e : sv.getEvaluators()) {
            fceDAO.createVisitEvaluator(e, newVisit.getId());
        }
        // create directory for site visit documents
        String path = null;
        try {
            path = getSiteVisitDir(sv);
            File dir = new File(path);
            File parentDir = dir.getParentFile();
            if (!parentDir.exists()) {
                DocumentUtil.mkDir(parentDir.getPath());
            }
            DocumentUtil.mkDir(path);
        } catch (IOException e) {
            logger.error("Exception creating file store directory " + path, e);
            throw new DAOException("Exception creating file store directory", e);
        }
        return sv;
    }

    /**
     * @param newVisit
     * @return SiteVisit
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */  
    public SiteVisit createMigratedSiteVisit(SiteVisit newVisit) 
        throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        SiteVisit ret = null;
        checkNull(newVisit);
        checkNull(newVisit.getFpId());
        try {
            ret = createMigratedSiteVisit(newVisit, trans);       
            trans.complete();
        } catch (DAOException e) {
            logger.error("siteVisitId=" + newVisit.getId() + " " + e.getMessage(), e); 
            cancelTransaction(trans, e);
        } finally {
            closeTransaction(trans);
        }
        return ret;
    }
    
    /**  FOR MIGRATION
     * @param newVisit
     * @param trans
     * @throws DAOException
     * 
     * @return FullComplianceEval
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */  
    public SiteVisit createMigratedSiteVisit(SiteVisit newVisit, Transaction trans) throws DAOException {
        // create/reference history record
        CompEnfFacilityDAO compEnfFacilityDAO = compEnfFacilityDAO(trans);
        FacilityHistory fhSv = compEnfFacilityDAO.createFacilityHistory(newVisit.getFacilityHistory());
        Integer hId = fhSv.getFacilityHistId();
        newVisit.setFacilityHistId(hId);

        FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO(trans);
        SiteVisit sv = fceDAO.createSiteVisit(newVisit);
        for(Evaluator e : sv.getEvaluators()) {
            fceDAO.createVisitEvaluator(e, newVisit.getId());
        }
        // create directory for site visit documents
        String path = null;
        try {
            path = getSiteVisitDir(sv);
            File dir = new File(path);
            File parentDir = dir.getParentFile();
            if (!parentDir.exists()) {
                DocumentUtil.mkDir(parentDir.getPath());
            }
            DocumentUtil.mkDir(path);
        } catch (IOException e) {
            logger.error("Exception creating file store directory " + path, e);
            throw new DAOException("Exception creating file store directory", e);
        }
        return sv;
    }
    
    private String getSiteVisitDir(SiteVisit sv) {
        return "/Facilities/" + sv.getFacilityId()
        + "/SiteVisit/" + sv.getId();
    }

    /**
     * @param fce
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void modifyFce(FullComplianceEval fce) throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();

        try {
        	modifyFce(fce, trans);
            trans.complete();
        } catch (DAOException e) {
            cancelTransaction(trans, e);
            throw e;
        } finally {
            closeTransaction(trans);
        }        
    }

    /**
     * @param fce
     * @param trans
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void modifyFce(FullComplianceEval fce, Transaction trans) throws DAOException {
        FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO(trans);
        if (fce.getDateEvaluated() != null &&
                fce.getFacilityHistory() == null) {
            // Create history record at the time the inspection completion entered
            try {
                FacilityHistoryService fsBO = getFacilityHistoryService();
                FacilityHistory hist = fsBO.createFacilityHistory(fce.getFpId(), trans);
                fce.setFacilityHistId(hist.getFacilityHistId());
                fce.setFacilityHistory(hist);
            } catch (RemoteException re) {
                String s = "modifyFce failed for fpId=" + fce.getFpId();
                logger.error(s, re);
            }
        }
        updateInspectionDate(fce);
        fceDAO.modifyFce(fce);
        deleteAdditionalAQDStaffByFceId(fce.getId());
        for (Evaluator aqdStaff: fce.getAdditionalAqdStaffPresent()) {
        	if (aqdStaff.getEvaluator() == null)
				continue;
        	createAdditionalAQDStaff(aqdStaff.getEvaluator(), fce.getId());
        }
        modifyFceAmbientConditions(fce);
    }
    
    
	/**
	 * @param fce
	 *            Set the DateEvaluated field to the Inspection date from either Day one ambient
	 *            conditions or Day two ambient conditions (whichever is the later date)
	 */
	private void updateInspectionDate(FullComplianceEval fce) {
		if (!fce.isLegacyInspection() && !fce.isPre10Legacy()) {
			if (fce.getDayTwoAmbientConditions() != null
					&& fce.getDayTwoAmbientConditions().getInspectionDate() != null) {
				fce.setDateEvaluated(fce.getDayTwoAmbientConditions().getInspectionDate());
			} else if (fce.getDayOneAmbientConditions() != null
					&& fce.getDayOneAmbientConditions().getInspectionDate() != null) {
				fce.setDateEvaluated(fce.getDayOneAmbientConditions().getInspectionDate());
			} else {
				fce.setDateEvaluated(null);
			}
			
		}
	}
    
    /**
     * @param fce
     * @param trans
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void modifyFce(FceScheduleRow fceR) throws DAOException {
        FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
        fceDAO.modifyFce(fceR);
    } 

    /**
     * @param visit
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void modifySiteVisit(SiteVisit visit) throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();

        try {
            modifySiteVisit(visit, trans);
            trans.complete();
        } catch (DAOException e) {
            logger.error("siteVisitId=" + visit.getId() + " " + e.getMessage(), e);
            cancelTransaction(trans, e);
        } finally {
            closeTransaction(trans);
        }
    }
    /**
     * @param visit
     * @param trans
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void modifySiteVisit(SiteVisit visit, Transaction trans) throws DAOException {
        FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO(trans);
        fceDAO.modifySiteVisit(visit);
        fceDAO.removeVisitEvaluators(visit.getId());
        if(!SiteVisitTypeDef.STACK_TEST.equals(visit.getVisitType())) {
            for(Evaluator e : visit.getEvaluators()) {
                fceDAO.createVisitEvaluator(e, visit.getId());
            }
        }
    }

	/**
	 * @param fce
	 * @throws DAOException
	 * 
	 */
	public void removeFce(FullComplianceEval fce) throws DAOException {

		checkNull(fce);

		Transaction trans = TransactionFactory.createTransaction();
		try {
			removeFce(fce, trans);
		} catch (DAOException e) {
			logger.error("fceId=" + fce.getId() + " " + e.getMessage(), e);
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
	}

    /**
     * @param fce
     * @param trans
     * @return void
     * @throws DAOException
     * 
     */
    public void removeFce(FullComplianceEval fce, Transaction trans) throws DAOException {

    	FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO(trans);
        InfrastructureDAO infraDAO = infrastructureDAO(trans); 

        for(FceAttachment fa : fce.getAttachments()) {
            removeFceAttachment(fa, trans);
        }
        fceDAO.removeFceNotes(fce.getId());
        
		if (fce.getInspectionNotes() != null) {
			for (Note fceNote : fce.getInspectionNotes()) {
				infraDAO.removeNote(fceNote.getNoteId());
			}
		}

		fceDAO.deleteAdditionalAQDStaffByFceId(fce.getId());
		fceDAO.deleteFceAmbientConditionsByFceId(fce.getId());
		fceDAO.deleteAssociatedPermitConditionIdRefsByFceId(fce.getId());
		fceDAO.deleteAssociatedPermitIdRefsByFceId(fce.getId());
		deletePreservedSnapshotRefs(fce.getId());
        fceDAO.removeFce(fce.getId());

    }

    // Add/remove any snapshots here
    @Override
    public void deletePreservedSnapshotRefs(Integer fceId) throws DAOException {
    	deleteFceApplicationsPreserved(fceId);
    	deleteFcePermitsPreserved(fceId);
    	deleteFceStackTestsPreserved(fceId);
    	deleteFceComplianceReportsPreserved(fceId);
    	deleteFceCorrespondencesPreserved(fceId);
    	deleteFceEmissionsInventoriesPreserved(fceId);
    	deleteFceCemComLimitsPreserved(fceId);
    	deleteFceAmbientMonitorsPreserved(fceId);
    	deleteFceSiteVisitsPreserved(fceId);
	}

	/**
     * @param visitId
     * @return boolean
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */ 

    public void removeSiteVisit(SiteVisit siteVisit)  throws DAOException {
        checkNull(siteVisit);
        Transaction trans = TransactionFactory.createTransaction();
        try {
            removeSiteVisit(siteVisit, trans);
        } catch (DAOException e) {
            logger.error("siteVisitId=" + siteVisit.getId() + " " + e.getMessage(), e);
            cancelTransaction(trans, e);
        } finally {
            closeTransaction(trans);
        }
    }

    /**
     * @param visitId
     * @param trans
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void removeSiteVisit(SiteVisit siteVisit, Transaction trans) throws DAOException {
        FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO(trans);
        InfrastructureDAO infraDAO = infrastructureDAO(trans);
        for(SvAttachment fa : siteVisit.getAttachments()) {
            removeSvAttachment(fa, trans);
        }
        fceDAO.removeVisitEvaluators(siteVisit.getId());
        fceDAO.removeSiteVisitNotes(siteVisit.getId());
        for(Note svNote : siteVisit.getSiteVisitNotes())
        	infraDAO.removeNote(svNote.getNoteId());
        
        fceDAO.removeSiteVisit(siteVisit.getId());
    }


    /**
     * @param fceId
     * @param facilityId
     * @param beginDate
     * @param endDate
     * @param assignedStaff
     * @param evaluator
     * @param schedLocked
     * @param completed
     * @return FullComplianceEval[]
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public  FullComplianceEval[] retrieveFceBySearch(String inspId, String facilityId,
            String facilityName, String countyCd, String doLaaCd, String permitClassCd, String facilityTypeCd,
            Timestamp beginSched, Timestamp endSched,
            Timestamp beginDate, Timestamp endDate, Integer assignedStaff, 
            Integer evaluator, String usEpaCommitted, List<String> inspectionReportStateCds, String portable, String cmpId) throws DAOException {
        FullComplianceEval[] rtn = null;
        try {
            FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO();
            rtn =  fullComplianceEvalDAO.retrieveFceBySearch(inspId, facilityId,
                    facilityName, countyCd, doLaaCd, permitClassCd, facilityTypeCd,
                    beginSched, endSched,
                    beginDate, endDate, assignedStaff, 
                    evaluator, usEpaCommitted, inspectionReportStateCds, portable, cmpId);
        } catch (DAOException de) {
            logger.error(de.getMessage(), de);
            throw de;
        }
        return rtn;
    }

    /**
     * @param facilityId
     * @return FullComplianceEval[]
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public FullComplianceEval[] retrieveFceBySearch(String facilityId) throws DAOException {
    FullComplianceEval[] rtn = null;
    try {
        FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO();
        rtn =  fullComplianceEvalDAO.retrieveFceBySearch(null, facilityId,
                null, null, null, null, null, null, null,
                null, null, null, null, null, null, null,null);
    } catch(DAOException de) {
        logger.error("facilityId=" + facilityId + " " + de.getMessage(), de);
        throw de;
    }
    return rtn;
    }

    /**
     * @param facilityId
     * @return SiteVisit[]
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public SiteVisit[] retrieveVisitBySearch(String facilityId) throws DAOException {
        SiteVisit[] rtn = null;
        try {
            FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO();
            rtn = fullComplianceEvalDAO.retrieveVisitBySearch(null, null, facilityId,
                    null, null, null, null, null, null, null, null, null, null, null, null);
            for(SiteVisit sv : rtn) {
                if(SiteVisitTypeDef.STACK_TEST.equals(sv.getVisitType())) {
                    sv.setEvaluators(fullComplianceEvalDAO.retrieveEmissionsTestWitnesses(facilityId, sv.getVisitDate()));
                }
            };
        } catch(DAOException de) {
            logger.error("facilityId=" + facilityId + " " + de.getMessage(), de);
            throw de;
        }
        return rtn;
    }

    /**
     * @param fceId
     * @param facilityId
     * @param beginDate
     * @param endDate
     * @param visitType
     * @param announced
     * @param evaluator
     * @return SiteVisit[]
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public SiteVisit[] retrieveVisitBySearch(String siteId, String inspId, String facilityId, 
            Timestamp beginDate, Timestamp endDate, String visitType,
            String announced, Integer evaluator, String facilityName,
            String doLaaCd, String countyCd, String permitClassCd, String facilityTypeCd, String cmpId,  String complianceIssued) throws DAOException {
        FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO();
        SiteVisit[] rtn = null;
        try {
        rtn = fullComplianceEvalDAO.retrieveVisitBySearch(siteId, inspId,
                facilityId, beginDate, endDate, visitType, announced, evaluator,
                facilityName, doLaaCd, countyCd, permitClassCd, facilityTypeCd, cmpId, complianceIssued) ;
        // fill in Witnesses when type is Stack Test
        for(SiteVisit sv : rtn) {
            if(SiteVisitTypeDef.STACK_TEST.equals(sv.getVisitType())) {
                sv.setEvaluators(fullComplianceEvalDAO.retrieveEmissionsTestWitnesses(sv.getFacilityId(), sv.getVisitDate()));
            }
        }
        } catch(DAOException de) {
            logger.error(de.getMessage(), de);
            throw de;
        }
        return rtn;
    }

    /**
     * @param facilityId
     * @param beginDate
     * @param endDate
     * @return SiteVisit[]
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public SiteVisit[] searchSiteVisits(String facilityId,
            Timestamp beginDate, Timestamp endDate) throws DAOException {
        FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO();
        SiteVisit[] rtn = null;
        try {
            rtn = fullComplianceEvalDAO.retrieveVisitBySearch(null, null,
                facilityId, beginDate, endDate, null, null, null, null, null, null, null, null, null, null);
//          fill in Witnesses when type is Stack Test
            for(SiteVisit sv : rtn) {
                if(SiteVisitTypeDef.STACK_TEST.equals(sv.getVisitType())) {
                    sv.setEvaluators(fullComplianceEvalDAO.retrieveEmissionsTestWitnesses(facilityId, sv.getVisitDate()));
                }
            }
        } catch(DAOException de) {
            logger.error("facilityId=" + facilityId + " " + de.getMessage(), de);
            throw de;
        }
        return rtn;
    }
    
    /**
     * @param eList
     * @return List<Evaluator>
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public List<Evaluator> orderEvaluators(List<Evaluator> eList) throws DAOException {
        List<Evaluator> rtn;
        if(eList == null || eList.size() == 0) {
            return eList;
        }
        FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO();
        try {
            
            rtn = fullComplianceEvalDAO.orderEvaluators(eList);
        } catch(DAOException de) {
            logger.error(de.getMessage(), de);
            throw de;
        }
        return rtn;
    }
    
    /**
     * @param  facilityId
     * @param  facilityName
     * @param  doLaa
     * @param  countyCd
     * @param  inspectClassCd
     * @param  schedOrNot
     * @param  showThruFfy
     * @param  nonCommitted
     * @return List<FceScheduleRow>
     * @throws DAOException 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ArrayList<FceScheduleRow> needToSchedFce(String facilityId,
            String facilityNm, String doLaaCd, String countyCd, String permitClassCd, String facilityTypeCd, 
            boolean schedOrNot, Integer showThruFfy, boolean nonCommitted, String cmpId) throws DAOException {
    	int inspectionFrequency;
        FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO();
        ArrayList<FceScheduleRow> rtn = null;
        try {
            rtn = fullComplianceEvalDAO.needToSchedFirstFce(facilityId, facilityNm,
                    doLaaCd, countyCd, permitClassCd, facilityTypeCd, cmpId);
            ArrayList<FceScheduleRow> rtn2 = fullComplianceEvalDAO.needToSchedFce(facilityId, facilityNm,
                    doLaaCd, countyCd, permitClassCd, facilityTypeCd, cmpId);
            rtn.addAll(rtn2);
        } catch(DAOException de) {
            logger.error(de.getMessage(), de);
            throw de;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
        Iterator<FceScheduleRow> it1 = rtn.iterator();
        while(it1.hasNext()) {
            FceScheduleRow r = it1.next();
            if(r.getCompletedFceId() == null && r.getScheduledFceId() == null) {
                // Never has had an FCE.
            } else {
                if(r.getScheduledFceId() != null) {
                    // Do not schedule another one if this one not completed but does exist.
//                    if(!schedOrNot) {
//                        it1.remove();
//                        continue;
//                    }
//                    if(r.getNextScheduled() == null) {
//                        // protect against no schedule set
//                        ;  // leave it blank (null)
//                    } else {
//                        // show the FFY needed in when we have one scheduled and not completed
//                        r.setInFfy(getFfy(r.getNextScheduled()));
//                    }
                } else {
                    // Has been completed, create next one based upon completion date.
                    r.setInFfy(getFfy(r.getLastCompleted()));
                    if(PermitClassDef.TV.equals(r.getPermitClassCd())) {
                    	inspectionFrequency = Integer.parseInt(FacilityTypeDef.getTvInspectionFrequency(r.getFacilityTypeCd()));
                        int nextNeededFfy = getFfy(r.getLastCompleted()) + inspectionFrequency;
                        r.setNeededBy(nextNeededFfy);
                    } else if(PermitClassDef.SMTV.equals(r.getPermitClassCd())) {
                    	inspectionFrequency = Integer.parseInt(FacilityTypeDef.getSmtvInspectionFrequency(r.getFacilityTypeCd()));
                        int nextNeededFfy = getFfy(r.getLastCompleted()) + inspectionFrequency;
                        r.setNeededBy(nextNeededFfy);
                    } else if(PermitClassDef.NTV.equals(r.getPermitClassCd())) {
                    	inspectionFrequency = Integer.parseInt(FacilityTypeDef.getSmtvInspectionFrequency(r.getFacilityTypeCd()));
                        int nextNeededFfy = getFfy(r.getLastCompleted()) + inspectionFrequency;
                        r.setNeededBy(nextNeededFfy);
                    }
                }
            }
        }
        
        Collections.sort(rtn);
        // Combine completed with next scheduled -- if they match up
        // Also don't allow to add one on completed row if other scheduled rows (completed row did not match a scheduled one)
        FceScheduleRow completed = null;
        boolean allowCombine = true;   // and completed is not null
        ListIterator<FceScheduleRow> it = rtn.listIterator();
        String prevFacilityId = "";
        while(it.hasNext()) {
            FceScheduleRow row = it.next();
            if(!row.getFacilityId().equals(prevFacilityId)) {
                allowCombine = true;  // reset for next facility
                prevFacilityId = row.getFacilityId();
            }
            if(row.getLastCompleted() != null) {
                completed = row;
            } else if(completed != null) {
                Timestamp dateOfCompleted = completed.getCompletedScheduledTimestamp();;
                if(dateOfCompleted == null){
                    // In case the Inspection is incomplete.  Has completion date but was not scheduled.
                    dateOfCompleted = completed.getLastCompleted();
                }
                if(allowCombine && row.getFacilityId().equals(completed.getFacilityId()) &&
                		completed.getNeededBy() != null && row.getNeededBy() != null && 0 <= completed.getNeededBy().compareTo(row.getNeededBy())
                    && 0 > new Integer(getFfy(dateOfCompleted)).compareTo(row.getNeededBy())) {
                    // The scheduled one must be after the completed one and not after when needed.
                    // Combine rows (last completed and next scheduled)
                    if(completed.getAssignedStaff() == null && completed.getNextScheduled() == null) {
                        // Only combine one row--in case two have the same FFY
                        completed.setAssignedStaff(row.getAssignedStaff());
                        completed.setUnchangedAssignedStaff(row.getAssignedStaff());
                        completed.setNextScheduled(row.getNextScheduled());
                        completed.setUnchangedNextScheduled(row.getNextScheduled());
                        completed.setScheduledFceId(row.getScheduledFceId());
                        completed.setScheduledUsEpaCommitted(row.isScheduledUsEpaCommitted());
                        completed.setUnchangedScheduledUsEpaCommitted(row.isUnchangedScheduledUsEpaCommitted());
                        completed.setScheduledAfsSchedLocked(row.isScheduledAfsSchedLocked());
                        completed.setScheduledLastModified(row.getScheduledLastModified());
                        it.remove();
                    }
                } else if(row.getNeededBy() != null && row.getFacilityId().equals(completed.getFacilityId())) {
                    // Do have some scheduled ones so don't allow to add one on completed row
                    completed.setLocked(true);
                    
                }
                // If the first one was or was not a match, do not combine any others.
                allowCombine = false;
            }
        }
        
        // Indicate which rows can be cloned
        FceScheduleRow prevRow = null;
        FceScheduleRow r = null;
        it = rtn.listIterator();
        while(it.hasNext()) {
            prevRow = r;
            r = it.next();
            // find last row for facility
            if(prevRow != null && !r.getFacilityId().equals(prevRow.getFacilityId())) {
                // Starting new facility;
                if(prevRow.getNextScheduled() != null) {
                    prevRow.setAllowAddAnother(true);
                }
            }
        }
        // process last row
        if(r != null && r.getNextScheduled() != null) {
            r.setAllowAddAnother(true);
        }
        
        // Set order
        String facil = "";
        Integer highestYear = null;
        ArrayList<FceScheduleRow> facilRows = new ArrayList<FceScheduleRow>();
        it = rtn.listIterator();
        while(it.hasNext()) {
            r = it.next();
            if(r.getFacilityId().equals(facil)) {
                facilRows.add(r);
                Timestamp d = r.getCompletedScheduledTimestamp();
                if(r.getNextScheduled() != null) d = r.getNextScheduled();
                int y = d != null?getFfy(d):0;
                if(highestYear == null || highestYear < y) {
                    highestYear = y;
                }
            } else {
                for(FceScheduleRow rr : facilRows) {
                   if(highestYear == null) rr.setOrder(0);
                   else rr.setOrder(highestYear);
                }
                highestYear = null;
                facil = r.getFacilityId();
                facilRows = new ArrayList<FceScheduleRow>();
                facilRows.add(r);
                highestYear = r.getNeededBy();
            }
        }
        for(FceScheduleRow rr : facilRows) {
            if(highestYear == null) rr.setOrder(0);
            else rr.setOrder(highestYear);
         }
        // resort according to the order established.
        Collections.sort(rtn);
        
        // Remove rows too far in future
        it = rtn.listIterator();
        while(it.hasNext()) {
            r = it.next();
            if(r.getNeededBy() != null && r.getNeededBy() > showThruFfy) {
                it.remove();  // don't want those out further.
            }
        }
        
        // Remove sheduled ones that are scheduled prior to thd last completion date since they are too old to work with
        it = rtn.listIterator();
        // look for completed one.  Then look at following ones and if scheudled date earler remove it
        String facilId =  "";
        Timestamp lastCompleted = null;
        while(it.hasNext()) {
            r = it.next();
            if(!facilId.equals(r.getFacilityId())) {
                lastCompleted = r.getLastCompleted();
                facilId = r.getFacilityId();
                continue;
            }
            if(lastCompleted == null) {
                lastCompleted = r.getLastCompleted();
                continue;
            } else {
                if(r.getNextScheduled() != null && lastCompleted.after(r.getNextScheduled())) {
                    it.remove(); 
                }
            }
        }
        
        // Remove scheduled one if flag indicates that.
        if(!schedOrNot) {
            facil = "";
            boolean deleteIfSame = false;
            it = rtn.listIterator();
            while(it.hasNext()) {
                r = it.next();
                if(deleteIfSame) {
                    if(r.getFacilityId().equals(facil)) {
                        it.remove();
                        continue;
                    } else {
                        deleteIfSame = false;
                    }
                }

                if(r.getScheduledFceId() != null) {
                    // delete entire facility
                	facil = r.getFacilityId();
                	it.remove();
                	deleteIfSame = true;
                	// look for previous ones.
                	if(it.hasPrevious()) {
                		r = it.previous();
                		if(r.getFacilityId().equals(facil)) {
                			it.remove();
                		} else {
                			it.next();
                		}
                	}
                    
                }
            }
        }
        
        return rtn;
    }
    
    /**
     * @param  fceSched
     * @return void
     * @throws DAOException 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
	public int updateFceSched(List<FceScheduleRow> fceSched) throws DAOException {

		int rtn = 0;
		try {
			
			for (FceScheduleRow r : fceSched) {
				
				if (r.isLocked()) {
					continue;
				}
				
				if (r.isChanged()) {
					
					// Update existing record.
					modifyFce(r);
					rtn++;

				} else if (r.getScheduledFceId() == null && r.getNextScheduled() != null) {

					// New one.
					FullComplianceEval newFCE = new FullComplianceEval(null, r.getAssignedStaff(), r.getFacilityId(),
							r.getNextScheduled(), null, true, null, null, null, null, false);

					AmbientConditions ac1 = new AmbientConditions();
					ac1.setInspectionDay(1);
					newFCE.setDayOneAmbientConditions(ac1);

					AmbientConditions ac2 = new AmbientConditions();
					ac2.setInspectionDay(2);
					newFCE.setDayTwoAmbientConditions(ac2);

					createFceGetFpId(r.getFacilityId(), newFCE);
					rtn++;
				}
			}
			
		} catch (DAOException de) {
			logger.error(de.getMessage(), de);
			throw de;
		}
		
		return rtn;
	}
    
    // Return Federal Fiscal Year.  It is the year that includes September
    int getFfy(Timestamp ts) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts.getTime());
        if(cal.get(Calendar.MONTH) < Calendar.OCTOBER) {
            return cal.get(Calendar.YEAR);
        } else {
            return cal.get(Calendar.YEAR) + 1;
        }
    }
    
    /**
     * @param name
     * @return Integer
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Integer getUserId(String name) throws DAOException {
    Integer rtn = null;
    try {
        FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO();
        DbInteger dbI =  fullComplianceEvalDAO.getUserId(name);
        if(dbI != null) {
            rtn = dbI.getCnt();
        }
    } catch(DAOException de) {
        logger.error("name=" + name + " " + de.getMessage(), de);
        throw de;
    }
    return rtn;
    }
    
    /**
     * @param doc
     * @return void
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void removeFceAttachment(FceAttachment doc)
            throws DAOException {
        Transaction trans = null;

        try {
            trans = TransactionFactory.createTransaction();
            removeFceAttachment(doc, trans);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }
    }
    
    /**
     * @param doc
     * @return void
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void removeSvAttachment(SvAttachment doc)
            throws DAOException {
        Transaction trans = null;

        try {
            trans = TransactionFactory.createTransaction();
            removeSvAttachment(doc, trans);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }
    }
    
    private void removeFceAttachment(FceAttachment doc, Transaction trans)
    throws DAOException {
        FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO(trans);
        DocumentDAO docDAO = documentDAO(trans);
        doc.setTemporary(true);
        docDAO.modifyDocument(doc);
        fullComplianceEvalDAO.deleteFceAttachment(doc);
    }
    
    private void removeSvAttachment(SvAttachment doc, Transaction trans)
    throws DAOException {
        FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO(trans);
        DocumentDAO docDAO = documentDAO(trans);
        doc.setTemporary(true);
        docDAO.modifyDocument(doc);
        fullComplianceEvalDAO.deleteSvAttachment(doc);
    }
    
    /**
     * @param doc
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void modifyFceAttachment(FceAttachment doc)
            throws DAOException {
        Transaction trans = null;
        try {
            trans = TransactionFactory.createTransaction();
            FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO(trans);
            DocumentDAO documentDAO = documentDAO(trans);
            fullComplianceEvalDAO.modifyFceAttachment(doc);
            documentDAO.modifyDocument(doc);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }

        return;
    }
    
    /**
     * @param doc
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void modifySvAttachment(SvAttachment doc)
            throws DAOException {
        Transaction trans = null;
        try {
            trans = TransactionFactory.createTransaction();
            FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO(trans);
            DocumentDAO documentDAO = documentDAO(trans);
            fullComplianceEvalDAO.modifySvAttachment(doc);
            documentDAO.modifyDocument(doc);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }

        return;
    }

    /**
     * Create a new row in the Attachment table.
     * @param fce
     * @param attachment
     * @param fileStream
     * @return Attachment 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Attachment createFceAttachment(FullComplianceEval fce,
                Attachment attachment, InputStream fileStream)
        throws DAOException, ValidationException {
            Transaction trans = TransactionFactory.createTransaction();
            FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO(trans);
            
            if (attachment.getDocTypeCd().equals(FceAttachmentTypeDef.INSPECTION_REPORT)){
            	Integer cntIR = fullComplianceEvalDAO.retireveFceAttachmentCountByType(fce.getId(), FceAttachmentTypeDef.INSPECTION_REPORT);
            	if (cntIR > 0 ){
            		return null;
            	}
            }
            
            try {
                // add document info to database
                // NOTE: This needs to be done before file is created in file store
                // since document id obtained from createDocument method is used as
                // the file name for the file store file
                attachment = (Attachment)documentDAO(trans).createDocument(attachment);
                DocumentUtil.createDocument(attachment.getPath(), fileStream);
	            fullComplianceEvalDAO.createFceAttachment(new FceAttachment(attachment));
	            if (attachment != null) {
	                // need to set enforcementId here because it defaults to 0
	//                attachment.setObjectId(enf.getEnforcementId().toString());
	//                createEnforcementTradeSecretAttachment(attachment, tsAttachment, tsInputStream, trans);
	            }
	            trans.complete();
	
	        } catch (DAOException e) {
	            try {
	                // need to delete public document if adding trade secret document fails
	                DocumentUtil.removeDocument(attachment.getPath());
	            } catch (IOException ioex) {
	                logger.error("Exception while attempting to delete document: " + attachment.getPath(), ioex);
	            }
	            cancelTransaction(trans, e);
	        } catch (IOException e) {
	            cancelTransaction(trans, new RemoteException(e.getMessage(), e));
	        } finally { // Clean up our transaction stuff
	            closeTransaction(trans);
	        }

        return attachment;
    }
    
    /**
     * Create a new row in the Attachment table.
     * @param siteVisit
     * @param attachment
     * @param fileStream
     * @return Attachment 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Attachment createSvAttachment(SiteVisit siteVisit,
                Attachment attachment, InputStream fileStream)
        throws DAOException, ValidationException {
            Transaction trans = TransactionFactory.createTransaction();
            FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO(trans);

            try {
                // add document info to database
                // NOTE: This needs to be done before file is created in file store
                // since document id obtained from createDocument method is used as
                // the file name for the file store file
                attachment = (Attachment)documentDAO(trans).createDocument(attachment);
                DocumentUtil.createDocument(attachment.getPath(), fileStream);
            fullComplianceEvalDAO.createSvAttachment(new SvAttachment(attachment));
//            if (attachment != null) {
//                // need to set enforcementId here because it defaults to 0
////                attachment.setObjectId(enf.getEnforcementId().toString());
////                createEnforcementTradeSecretAttachment(attachment, tsAttachment, tsInputStream, trans);
//            }
            trans.complete();

        } catch (DAOException e) {
            try {
                DocumentUtil.removeDocument(attachment.getPath());
            } catch (IOException ioex) {
                logger.error("Exception while attempting to delete document: " + attachment.getPath(), ioex);
            }
            cancelTransaction(trans, e);
        } catch (IOException e) {
            cancelTransaction(trans, new RemoteException(e.getMessage(), e));
        } finally { // Clean up our transaction stuff
            closeTransaction(trans);
        }

        return attachment;
    }
    
    /**
     * @return List<SiteVisit>
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public List<SiteVisit> newAfsSiteVisits() throws DAOException {
        FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO();
        List<SiteVisit> rtn = null;
        try {
            rtn = fullComplianceEvalDAO.newAfsSiteVisits();
        } catch(DAOException de) {
            logger.error(de.getMessage(), de);
            throw de;
        }
        return rtn;
    }
    
    /**
     * @return List<FullComplianceEval>
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public List<FullComplianceEval> newAfsScheduledFCEs(Timestamp beginSched, Timestamp endSched) throws DAOException {
        FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO();
        List<FullComplianceEval> rtn = null;
        try {
            rtn = fullComplianceEvalDAO.newAfsScheduledFCEs(beginSched, endSched);
        } catch(DAOException de) {
            logger.error(de.getMessage(), de);
            throw de;
        }
        return rtn;
    }
        
        /**
         * @return List<FullComplianceEval>
         * @throws DAOException
         * 
         * @ejb.interface-method view-type="remote"
         * @ejb.transaction type="NotSupported"
         */
        public List<FullComplianceEval> newAfsFCEs() throws DAOException {
            FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO();
            List<FullComplianceEval> rtn = null;
            try {
                rtn = fullComplianceEvalDAO.newAfsFCEs();
            } catch(DAOException de) {
                logger.error(de.getMessage(), de);
                throw de;
            }
            return rtn;
        }
        
        /**
         * 
         * @param facilityId
         * @param fce
         * @return Integer
         * @throws DAOException
         * 
         * @ejb.interface-method view-type="remote"
         * @ejb.transaction type="Required"
         */
        public Integer afsLockFceComp(String scscId, FullComplianceEval fce) throws DAOException {
            Transaction trans = null;
            Integer id = null;
            try {
                trans = TransactionFactory.createTransaction();     
                FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO(trans);
                StackTestDAO stackTestDAO = stackTestDAO(trans);
                id = stackTestDAO.getAfsId(scscId);
                stackTestDAO.updateAfsId( scscId,  id);
                fullComplianceEvalDAO.afsLockFceComp(fce, id);
            } catch (DAOException e) {
                cancelTransaction(trans, e);
            } finally {
                closeTransaction(trans);
            }
            return id;
        }
        
        /**
         * 
         * @param facilityId
         * @param fce
         * @throws DAOException
         * 
         * @ejb.interface-method view-type="remote"
         * @ejb.transaction type="Required"
         */
        public Integer afsLockFceSched(String scscId, FullComplianceEval fce) throws DAOException {
            Transaction trans = null;
            try {
                trans = TransactionFactory.createTransaction();     
                FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO(trans);
                fullComplianceEvalDAO.afsLockFceSched(fce);
            } catch (DAOException e) {
                cancelTransaction(trans, e);
            } finally {
                closeTransaction(trans);
            }
            return -1;
        }
        
        /**
         * @param facilityId
         * @param sv
         * @return Integer
         * @throws DAOException
         * 
         * @ejb.interface-method view-type="remote"
         * @ejb.transaction type="Required"
         */
        public Integer afsLockSiteVisit(String scscId, SiteVisit sv) throws DAOException {
            Transaction trans = null;
            Integer id = null;
            try {
                trans = TransactionFactory.createTransaction();     
                FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO(trans);
                StackTestDAO stackTestDAO = stackTestDAO(trans);
                id = stackTestDAO.getAfsId(scscId);
                stackTestDAO.updateAfsId( scscId,  id);
                fullComplianceEvalDAO.afsLockSiteVisit(sv, id);
            } catch (DAOException e) {
                cancelTransaction(trans, e);
            } finally {
                closeTransaction(trans);
            }
            return id;
        }
        
        /** 
         * @param sv
         * @return boolean
         * @throws DAOException
         * 
         * @ejb.interface-method view-type="remote"
         * @ejb.transaction type="Required"
         */
        public boolean afsSetDateSiteVisit(SiteVisit sv) throws DAOException {
            Transaction trans = null;
            boolean rtn = false;
            try {
                trans = TransactionFactory.createTransaction();     
                FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO(trans);
                rtn = fullComplianceEvalDAO.afsSetDateSiteVisit(sv);
            } catch (DAOException e) {
                cancelTransaction(trans, e);
            } finally {
                closeTransaction(trans);
            }
            return rtn;
        }
        
        /**
         * @param fce
         * @return boolean
         * @throws DAOException
         * 
         * @ejb.interface-method view-type="remote"
         * @ejb.transaction type="Required"
         */
        public boolean afsSetDateFceSched(FullComplianceEval fce) throws DAOException {
            Transaction trans = null;
            boolean rtn = false;
            try {
                trans = TransactionFactory.createTransaction();     
                FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO(trans);
                rtn = fullComplianceEvalDAO.afsSetDateFceSched(fce);
            } catch (DAOException e) {
                cancelTransaction(trans, e);
            } finally {
                closeTransaction(trans);
            }
            return rtn;
        }
        
        /**
         * @param fce
         * @return boolean
         * @throws DAOException
         * 
         * @ejb.interface-method view-type="remote"
         * @ejb.transaction type="Required"
         */
        public boolean afsSetDateFceComp(FullComplianceEval fce) throws DAOException {
            Transaction trans = null;
            boolean rtn = false;
            try {
                trans = TransactionFactory.createTransaction();     
                FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO(trans);
                rtn = fullComplianceEvalDAO.afsSetDateFceComp(fce);
            } catch (DAOException e) {
                cancelTransaction(trans, e);
            } finally {
                closeTransaction(trans);
            }
            return rtn;
        }
        
        /**
         * 
         * @param scscId
         * @param afsId
         * @return List<FullComplianceEval>
         * @throws DAOException
         * 
         * @ejb.interface-method view-type="remote"
         * @ejb.transaction type="Required"
         */
        public List<FullComplianceEval> retrieveSchedFce(String scscId, String afsId) throws DAOException {
            Transaction trans = null;
            List<FullComplianceEval> rtn = null;
            checkNull(scscId);
            checkNull(afsId);
            try {
                rtn = fullComplianceEvalDAO().retrieveSchedFceByAfsId(scscId, afsId);
            } catch (DAOException e) {
                cancelTransaction(trans, e);
            } finally {
                closeTransaction(trans);
            }
            return rtn;
        }
        
        /**
         * 
         * @param scscId
         * @param afsId
         * @return List<FullComplianceEval>
         * @throws DAOException
         * 
         * @ejb.interface-method view-type="remote"
         * @ejb.transaction type="Required"
         */
        public List<FullComplianceEval> retrieveEvalFce(String scscId, String afsId) throws DAOException {
            Transaction trans = null;
            List<FullComplianceEval> rtn = null;
            checkNull(scscId);
            checkNull(afsId);
            try {
                rtn = fullComplianceEvalDAO().retrieveEvalFceByAfsId(scscId, afsId);
            } catch (DAOException e) {
                cancelTransaction(trans, e);
            } finally {
                closeTransaction(trans);
            }
            return rtn;
        }
        
        /**
         * 
         * @param scscId
         * @param afsId
         * @return List<SiteVisit>
         * @throws DAOException
         * 
         * @ejb.interface-method view-type="remote"
         * @ejb.transaction type="Required"
         */
        public List<SiteVisit> retrieveSiteVisitByAfsId(String scscId, String afsId) throws DAOException {
            Transaction trans = null;
            List<SiteVisit> rtn = null;
            checkNull(scscId);
            checkNull(afsId);
            try {
                rtn = fullComplianceEvalDAO().retrieveSiteVisitByAfsId(scscId, afsId);
            } catch (DAOException e) {
                cancelTransaction(trans, e);
            } finally {
                closeTransaction(trans);
            }
            return rtn;
        }
        
        /**
         * @param facilityId
         * @return FullComplianceEval
         * @throws DAOException
         * 
         * @ejb.interface-method view-type="remote"
         * @ejb.transaction type="NotSupported"
         */
        public FullComplianceEval retrieveLastCompletedFce(String facilityId) throws DAOException {
        FullComplianceEval rtn = null;
        try {
            rtn =  fullComplianceEvalDAO().retrieveLastCompletedFce(facilityId);
        } catch(DAOException de) {
            logger.error("facilityId=" + facilityId + " " + de.getMessage(), de);
            throw de;
        }
        return rtn;
        }
        
        /**
         * @param facilityId
         * @return FullComplianceEval
         * @throws DAOException
         * 
         * @ejb.interface-method view-type="remote"
         * @ejb.transaction type="NotSupported"
         */
        public FullComplianceEval retrieveLastScheduledFce(String facilityId) throws DAOException {
        FullComplianceEval rtn = null;
        try {
            FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO();
            rtn =  fullComplianceEvalDAO.retrieveLastScheduledFce(facilityId);
        } catch(DAOException de) {
            logger.error("facilityId=" + facilityId + " " + de.getMessage(), de);
            throw de;
        }
        return rtn;
        }
        
        /**
         * @param facilityId
         * @return SiteVisit
         * @throws DAOException
         * 
         * @ejb.interface-method view-type="remote"
         * @ejb.transaction type="NotSupported"
         */
        public SiteVisit retrieveLastSiteVisit(String facilityId) throws DAOException {
        SiteVisit rtn = null;
        try {
            FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO();
            rtn =  fullComplianceEvalDAO.retrieveLastSiteVisit(facilityId);
        } catch(DAOException de) {
            logger.error("facilityId=" + facilityId + " " + de.getMessage(), de);
            throw de;
        }
        return rtn;
        }
        
        /**
         * @param  facilityId
         * @param  facilityName
         * @param  doLaa
         * @param  countyCd
         * @param  List<InspectionClassifications>
         * @param  List<SipCompliances>
         * @param  List<MactCompliances>
         * @param  List<TvCompliances>
         * @param  List<FesopCompliances>
         * @param  List<NeshapsCompliances>
         * @param  List<NspsCompliances>
         * @param  List<PsdCompliances>
         * @param  List<NsrCompliances>
         * @return List<AirProgramCompliance>
         * @throws DAOException 
         * @ejb.interface-method view-type="remote"
         * @ejb.transaction type="NotSupported"
         */
        public List<AirProgramCompliance> complianceSearch(String facilityId, String facilityName,
              String operatingStatusCd, String doLaaCd, String countyCd, List<String> selectedC,
              List<String> selectedSipList, List<String> selectedMactList, 
              List<String> selectedTvList, List<String> selectedSmList,
              List<String> selectedNeshapsList, List<String> selectedNspsList,
              List<String> selectedPsdList, List<String> selectedNsrList) throws DAOException {
            
            FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO();
            ArrayList<Integer> rtn = new ArrayList<Integer>();;
            // determine how many air program tables need to be searched
            int apCnt = 0;
            if(selectedNeshapsList!=null) apCnt++;
            if(selectedNspsList!=null) apCnt++;
            if(selectedPsdList!=null) apCnt++;
            if(selectedNsrList!=null) apCnt++;
            if(apCnt <= 1) {
                try {
                    rtn = fullComplianceEvalDAO.complianceSearch(facilityId, facilityName,
                            operatingStatusCd, doLaaCd, countyCd, selectedC,
                            selectedSipList, selectedMactList, 
                            selectedTvList, selectedSmList,
                            selectedNeshapsList, selectedNspsList,
                            selectedPsdList, selectedNsrList);
                } catch(DAOException de) {
                    logger.error(de.getMessage(), de);
                    throw de;
                }
            } else {
                ArrayList<Integer> rtn2 = null;
                // Do at most two air program tables at once
                if(selectedNeshapsList!=null) {
                    try {
                        rtn2 = fullComplianceEvalDAO.complianceSearch(facilityId, facilityName,
                                operatingStatusCd, doLaaCd, countyCd, selectedC,
                                selectedSipList, selectedMactList, 
                                selectedTvList, selectedSmList,
                                selectedNeshapsList, null,
                                null, null);
                    } catch(DAOException de) {
                        logger.error(de.getMessage(), de);
                        throw de;
                    }
                    rtn.addAll(rtn2);
                }
                if(selectedNspsList!=null) {
                    try {
                        rtn2 = fullComplianceEvalDAO.complianceSearch(facilityId, facilityName,
                                operatingStatusCd, doLaaCd, countyCd, selectedC,
                                selectedSipList, selectedMactList, 
                                selectedTvList, selectedSmList,
                                null, selectedNspsList,
                                null, null);
                    } catch(DAOException de) {
                        logger.error(de.getMessage(), de);
                        throw de;
                    }
                    rtn.addAll(rtn2);
                }
                if(selectedPsdList!=null) {
                    try {
                        rtn2 = fullComplianceEvalDAO.complianceSearch(facilityId, facilityName,
                                operatingStatusCd, doLaaCd, countyCd, selectedC,
                                selectedSipList, selectedMactList, 
                                selectedTvList, selectedSmList,
                                null, null,
                                selectedPsdList, null);
                    } catch(DAOException de) {
                        logger.error(de.getMessage(), de);
                        throw de;
                    }
                    rtn.addAll(rtn2);
                }
                if(selectedNsrList!=null) {
                    try {
                        rtn2 = fullComplianceEvalDAO.complianceSearch(facilityId, facilityName,
                                operatingStatusCd, doLaaCd, countyCd, selectedC,
                                selectedSipList, selectedMactList, 
                                selectedTvList, selectedSmList,
                                null, null,
                                null, selectedNsrList);
                    } catch(DAOException de) {
                        logger.error(de.getMessage(), de);
                        throw de;
                    }
                    rtn.addAll(rtn2);
                }
                HashSet<Integer> hash = new HashSet<Integer>(rtn.size());
                hash.addAll(rtn);  // get rid of duplicates
                rtn = new ArrayList<Integer>(hash.size());
                rtn.addAll(hash);
                Collections.sort(rtn);
            }
            ArrayList<AirProgramCompliance> rtnAirProgC = null;
            try {
                rtnAirProgC = fullComplianceEvalDAO.complianceSearch(rtn);
            } catch(DAOException de) {
                logger.error(de.getMessage(), de);
                throw de;
            }
            return rtnAirProgC;
        }
        
	/**
	 * @return void
	 * @throws DAOException
	 */
	public void fceNeedReminders() throws DAOException, RemoteException {

		List<FullComplianceEval> fceList = null;
		ReadWorkFlowService wfBO = null;
		try {
			FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO();
			fceList = fullComplianceEvalDAO.fceNeedReminders();
			wfBO = getReadWorkFlowService();
		} catch (DAOException de) {
			logger.error("fceNeedReminders() failed with " + de.getMessage(), de);
			throw de;
		}

		// Create workflows

		String ptName = WorkFlowProcess.INSPECTION_DUE_SOON;
		Integer workflowId = wfBO.retrieveWorkflowTempIdAndNm().get(ptName);
		if (workflowId == null) {
			String s = "Failed to find workflow for \"" + ptName;
			logger.error(s);
			throw new DAOException(s);
		}

		String rush = "N";
		WorkFlowManager wfm = new WorkFlowManager();
		int cnt = 400;
		for (FullComplianceEval fce : fceList) {
			cnt--;
			if (cnt < 0)
				break; // limit number of workflows created at a time.
			Timestamp startDt = fce.getScheduledTimestamp();

			Calendar cal = Calendar.getInstance();
			Date date = new Date(startDt.getTime());
			cal.setTime(date);
			cal.add(Calendar.MONTH, 3);
			cal.add(Calendar.DAY_OF_YEAR, -1);
			Timestamp dueDt = new Timestamp(cal.getTimeInMillis());
			WorkFlowResponse resp = wfm.submitProcess(workflowId, fce.getId(), fce.getFpId(), CommonConst.ADMIN_USER_ID,
					rush, startDt, dueDt, null);

			if (resp.hasError() || resp.hasFailed()) {
				StringBuffer errMsg = new StringBuffer();
				StringBuffer recomMsg = new StringBuffer();
				String[] errorMsgs = resp.getErrorMessages();
				String[] recomMsgs = resp.getRecommendationMessages();
				for (String msg : errorMsgs) {
					errMsg.append(msg + " ");
				}
				for (String msg : recomMsgs) {
					recomMsg.append(msg + " ");
				}
				String s = "Error encountered trying to create workflow for Inspection Due Soon " + fce.getId() + ": "
						+ errMsg.toString();
				logger.error(s);
				throw new DAOException(s, new Exception());
			}
			// find process activity and set user ID
			WorkFlowProcess[] processes;
			try {
				final Integer templateId = workflowId;
				WorkFlowProcess wfProcess = new WorkFlowProcess();
				wfProcess.setProcessTemplateId(templateId);
				wfProcess.setExternalId(fce.getId());
				wfProcess.setCurrent(true);
				wfProcess.setUnlimitedResults(true);
				// Find the workflow process for this report
				processes = wfBO.retrieveProcessList(wfProcess);
			} catch (RemoteException e) {
				String s = "Caught RemoteException checking for exisiting workflow for Inspection " + fce.getId();
				logger.error(s, e);
				throw new DAOException(s, new Exception());
			}
			if (processes.length == 0) {
				// no workflow found
				String s = "No workflow for Inspection " + fce.getId();
				logger.error(s);
				throw new DAOException(s, new Exception());
			}
			if (processes.length > 1) {
				// more than one workflow found
				String s = "More than one workflow for Inspection " + fce.getId();
				logger.error(s);
				throw new DAOException(s, new Exception());
			}
			WorkFlowDAO workFlowDAO = workFlowDAO();
			ProcessActivity[] pa = workFlowDAO.retrieveProcessActivities(processes[0].getProcessId());
			if (pa.length == 0) {
				// no stop found
				String s = "No step for Inspection " + fce.getId() + " and process " + processes[0].getProcessId();
				logger.error(s);
				throw new DAOException(s, new Exception());
			}
			if (pa.length > 1) {
				// more than one step found
				String s = "More than one step found for Inspection " + fce.getId() + " and process "
						+ processes[0].getProcessId();
				logger.error(s);
				throw new DAOException(s, new Exception());
			}
			Integer user = fce.getAssignedStaff();
			if (fce.getEvaluator() != null)
				user = fce.getEvaluator();
			pa[0].setUserId(user);
			workFlowDAO.modifyProcessActivity(pa[0]);
		}
	}

        /**
    	 * Returns all of the Inspection comments by FCE ID.
    	 * 
    	 * @param int The FCE ID
    	 * 
    	 * @return Note[] All comments of this Inspection.
    	 * 
    	 * @throws DAOException
    	 *             Database access error.
    	 * 
    	 * @ejb.interface-method view-type="remote"
    	 * @ejb.transaction type="NotSupported"
    	 */
    	public Note[] retrieveInspectionNotes(int fceID) throws DAOException {
    		return fullComplianceEvalDAO().retrieveInspectionNotes(fceID);
    	}
    	
    	public InspectionNote createInspectionNote(InspectionNote inspectionNote)
    			throws DAOException {
    		Transaction trans = TransactionFactory.createTransaction();
    		InspectionNote ret = null;

    		try {
    			ret = createInspectionNote(inspectionNote, trans);

    			if (ret != null) {
    				trans.complete();
    			} else {
    				trans.cancel();
    				logger.error("Failed to insert Inspection Note");
    			}
    		} catch (DAOException de) {
    			cancelTransaction(trans, de);
    		} finally {
    			closeTransaction(trans);
    		}

    		return ret;
    	}
    	
    	/**
    	 * @param inspectionNote
    	 * @param trans
    	 * @throws DAOException
    	 * 
    	 * @ejb.interface-method view-type="remote"
    	 * @ejb.transaction type="Required"
    	 */
    	public InspectionNote createInspectionNote(InspectionNote inspectionNote, Transaction trans)
    			throws DAOException {
    		InfrastructureDAO infraDAO = infrastructureDAO(trans);
    		FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO(trans);

    		Note tempNote = infraDAO.createNote(inspectionNote);

    		if (tempNote != null) {
    			inspectionNote.setNoteId(tempNote.getNoteId());

    			fullComplianceEvalDAO.createInspectionNote(inspectionNote.getFceId(),
    					inspectionNote.getNoteId());
    		} else
    			inspectionNote = null;
    		return inspectionNote;
    	}
    	
    	/**
    	 * 
    	 * @return boolean <tt>true</tt> if a record was updated.
    	 * 
    	 * @throws DAOException
    	 *             Database access error.
    	 * 
    	 * @ejb.interface-method view-type="remote"
    	 * @ejb.transaction type="Required"
    	 */
    	public boolean modifyInspectionNote(InspectionNote inspectionNote) throws DAOException {
    		return infrastructureDAO().modifyNote(inspectionNote);
    	}
    	
    	 /**
    	 * Returns all of the Site Visit comments by VISIT ID.
    	 * 
    	 * @param int The VISIT ID
    	 * 
    	 * @return Note[] All comments of this Site Visit.
    	 * 
    	 * @throws DAOException
    	 *             Database access error.
    	 * 
    	 * @ejb.interface-method view-type="remote"
    	 * @ejb.transaction type="NotSupported"
    	 */
    	public Note[] retrieveSiteVisitNotes(int visitId) throws DAOException {
    		return fullComplianceEvalDAO().retrieveSiteVisitNotes(visitId);
    	}
    	
    	public SiteVisitNote createSiteVisitNote(SiteVisitNote siteVisitNote)
    			throws DAOException {
    		Transaction trans = TransactionFactory.createTransaction();
    		SiteVisitNote ret = null;

    		try {
    			ret = createSiteVisitNote(siteVisitNote, trans);

    			if (ret != null) {
    				trans.complete();
    			} else {
    				trans.cancel();
    				logger.error("Failed to insert Site Visit Note");
    			}
    		} catch (DAOException de) {
    			cancelTransaction(trans, de);
    		} finally {
    			closeTransaction(trans);
    		}

    		return ret;
    	}
    	
    	/**
    	 * @param siteVisitNote
    	 * @param trans
    	 * @throws DAOException
    	 * 
    	 * @ejb.interface-method view-type="remote"
    	 * @ejb.transaction type="Required"
    	 */
    	public SiteVisitNote createSiteVisitNote(SiteVisitNote siteVisitNote, Transaction trans)
    			throws DAOException {
    		InfrastructureDAO infraDAO = infrastructureDAO(trans);
    		FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO(trans);

    		Note tempNote = infraDAO.createNote(siteVisitNote);

    		if (tempNote != null) {
    			siteVisitNote.setNoteId(tempNote.getNoteId());

    			fullComplianceEvalDAO.createSiteVisitNote(siteVisitNote.getVisitId(),
    					siteVisitNote.getNoteId());
    		} else
    			siteVisitNote = null;
    		return siteVisitNote;
    	}
    	
    	/**
    	 * 
    	 * @return boolean <tt>true</tt> if a record was updated.
    	 * 
    	 * @throws DAOException
    	 *             Database access error.
    	 * 
    	 * @ejb.interface-method view-type="remote"
    	 * @ejb.transaction type="Required"
    	 */
    	public boolean modifySiteVisitNote(SiteVisitNote siteVisitNote) throws DAOException {
    		return infrastructureDAO().modifyNote(siteVisitNote);
    	}

	/**
	 * Returns inspections that are not associated with any site visit or stack
	 * test and are uncompleted.
	 */
	@Override
	public FullComplianceEval[] retrieveFceWithNoSiteVisitsOrStackTests(
			String facilityId) throws DAOException {
		FullComplianceEval[] rtn = null;
		try {
			FullComplianceEvalDAO fullComplianceEvalDAO = fullComplianceEvalDAO();
			rtn = fullComplianceEvalDAO
					.retrieveFcesWithoutSiteVisitsOrStackTests(facilityId);
		} catch (DAOException de) {
			logger.error(de.getMessage(), de);
			throw de;
		}
		return rtn;
	}

	
	@Override
	public void prepareFce(FullComplianceEval fce) throws DAOException {
		
		try{
			FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
			Integer lastFceId = null; 
			FullComplianceEval last = retrieveLastPriorCompletedFce(fce.getFacilityId(), fce.getId());
			if (last != null) {
				lastFceId = last.getId();
			}
			fce.setLastFceId(lastFceId);
			Facility facility = getFacilityService().retrieveFacility(fce.getFacilityId());
			fce.setFpId(facility.getFpId());
			fce.setInspectionReportStateCd(FCEInspectionReportStateDef.PREPARE);
			fceDAO.modifyFcePrepare(fce);
			
		} catch (RemoteException re){
			logger.error(re.getMessage(), re);
			throw new DAOException(re.getLocalizedMessage(), re);
		}

		// create the inspection report workflow process
		try {
			createInspectionReportWorkflow(fce);
		} catch (RemoteException re) {
			logger.error("Unable to create workflow for inspection report " + fce.getInspId(), re);
			throw new DAOException(re.getLocalizedMessage(), re);
		} catch (ServiceFactoryException sfe) {
			logger.error("Unable to create workflow for inspection report " + fce.getInspId(), sfe);
			throw new DAOException(sfe.getLocalizedMessage(), sfe);
		}
		
	}
	
    public FullComplianceEval retrieveLastPriorCompletedFce(String facilityId, Integer fceId) throws DAOException {
	    FullComplianceEval rtn = null;
	    try {
	        rtn =  fullComplianceEvalDAO().retrieveLastPriorCompletedFce(facilityId, fceId);
	    } catch(DAOException de) {
	        logger.error("facilityId=" + facilityId + " " + de.getMessage(), de);
	        throw de;
	    }
	    return rtn;
    }

	
/*	public void modifyFceLastInsp(Integer fceId, String lastInspId, Timestamp lastInspDate) throws DAOException {
        FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
        fceDAO.modifyFceLastInsp(fceId, lastInspId, lastInspDate);
	}*/
	
	
	@Override
    public void deleteAdditionalAQDStaffByFceId(Integer fceId) throws DAOException {
    	try {
    		fullComplianceEvalDAO().deleteAdditionalAQDStaffByFceId(fceId);
		} catch (DAOException de) {
        	DisplayUtil.displayError("Failed to delete Additional AQD Staff");
    	    logger.error(de.getMessage(), de);
    	    throw de;
        }
    }
    
	@Override
    public void createAdditionalAQDStaff(Integer aqdStaffId, Integer fceId) throws DAOException {
    	try {
    		fullComplianceEvalDAO().createAdditionalAQDStaff(aqdStaffId, fceId);
		} catch (DAOException de) {
        	DisplayUtil.displayError("Failed to create Additional AQD Staff");
    	    logger.error(de.getMessage(), de);
    	    throw de;
        }
    }
    
	@Override
    public List<Evaluator> retrieveAdditionalAQDStaffByFceId(Integer fceId)
			throws DAOException {
    	List<Evaluator> aqDStaffIds = null;
    	try {
    		aqDStaffIds= fullComplianceEvalDAO().retrieveAdditionalAQDStaffByFceId(fceId);
		} catch (DAOException de) {
        	DisplayUtil.displayError("Failed to retrieve Additional AQD Staff");
    	    logger.error(de.getMessage(), de);
    	    throw de;
        }
    	return aqDStaffIds;
    }
	@Override
	public void completeFce(FullComplianceEval fce) throws DAOException, RemoteException {
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
    		fce.setInspectionReportStateCd(FCEInspectionReportStateDef.COMPLETE);
    		Calendar cal = Calendar.getInstance();
    	    cal.set(Calendar.HOUR_OF_DAY, 0);
    		cal.set(Calendar.MINUTE, 0);
    		cal.set(Calendar.SECOND, 0);
    		cal.set(Calendar.MILLISECOND, 0);
    		Timestamp dateReported = new Timestamp(cal.getTimeInMillis());
    		fce.setDateReported(dateReported);
    		fce.setEvaluator(InfrastructureDefs.getCurrentUserId());
    		Facility facility = getFacilityService().retrieveFacility(fce.getFacilityId());
    		fce.setFpId(facility.getFpId());
    		fceDAO.modifyFceComplete(fce); // update several columns in ce_fce_table
    		getFacilityService().markProfileHistory(fce.getFpId());
		} catch(DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@Override
	public void finalizeFce (FullComplianceEval fce) throws DAOException {
		
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			List<ComplianceStatusEvent> complianceStatusEventList = fce.getAssocComplianceStatusEvents();
			for (FcePermitCondition fpc : fce.getFcePermitConditions()) {
				ComplianceStatusEvent cse = new ComplianceStatusEvent();
				cse.setInspectionReference(fce.getId());
				cse.setPermitConditionId(fpc.getPermitConditionId());
				cse.setEventDate(fce.getDateEvaluated());
				cse.setEventTypeCd(ComplianceTrackingEventTypeDef.INSPECTION);
				cse.setStatus(PermitConditionComplianceStatusDef.getData().getItems().getItemDesc(fpc.getComplianceStatusCd()));
				cse.setLastUpdatedById(fce.getEvaluator());
				cse.setLastUpdatedDate(fce.getDateEvaluated());
				permitConditionService.createComplianceStatusEvent(cse);
			}
    		fce.setInspectionReportStateCd(FCEInspectionReportStateDef.FINAL);
			fceDAO.modifyFceReportState(fce);
		} catch (DAOException de) {
			DisplayUtil.displayError("Failed to finalize Inspection Report");
			logger.error(de.getMessage(), de);
			throw de;
		}
	}	
	
	private void createFceAmbientConditions(FullComplianceEval fce, Integer fceId) throws DAOException {
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			AmbientConditions dayOneAmbientConditions = fce.getDayOneAmbientConditions();
			AmbientConditions dayTwoAmbientConditions = fce.getDayTwoAmbientConditions();
			dayOneAmbientConditions.setFceId(fceId);
			dayTwoAmbientConditions.setFceId(fceId);
			fceDAO.createFceAmbientConditions(dayOneAmbientConditions);
			fceDAO.createFceAmbientConditions(dayTwoAmbientConditions);
		} catch (DAOException de) {
			DisplayUtil.displayError("Failed to create Inspection Report Ambient Condition");
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	private void modifyFceAmbientConditions(FullComplianceEval fce) throws DAOException {
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			AmbientConditions dayOneAmbientConditions = fce.getDayOneAmbientConditions();
			if(dayOneAmbientConditions!=null) {
				dayOneAmbientConditions.setFceId(fce.getId());
			}
			
			AmbientConditions dayTwoAmbientConditions = fce.getDayTwoAmbientConditions();
			if(dayTwoAmbientConditions!=null) {
				dayTwoAmbientConditions.setFceId(fce.getId());
			}
			
			ArrayList<AmbientConditions> ambientConditions = retrieveFceAmbientConditions(fce.getId());
			if(!ambientConditions.isEmpty()) {
				fceDAO.modifyFceAmbientConditions(dayOneAmbientConditions);
				fceDAO.modifyFceAmbientConditions(dayTwoAmbientConditions);
			}
		} catch (DAOException de) {
			DisplayUtil.displayError("Failed to modify Inspection Report Ambient Condition");
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
    private void retrieveAmbientConditionsByFce(FullComplianceEval fce) throws DAOException {
    	ArrayList<AmbientConditions> ambientConditions = retrieveFceAmbientConditions(fce.getId());
		for(AmbientConditions ambientCondition: ambientConditions) {
			if (ambientCondition.getInspectionDay().equals(AmbientConditions.AMBIENT_CONDITIONS_DAY_ONE)) {
				fce.setDayOneAmbientConditions(ambientCondition);
			} else if (ambientCondition.getInspectionDay().equals(AmbientConditions.AMBIENT_CONDITIONS_DAY_TWO)){
				fce.setDayTwoAmbientConditions(ambientCondition);
			}
		}
	}
    
	private ArrayList<AmbientConditions> retrieveFceAmbientConditions(Integer fceId) throws DAOException {
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		ArrayList<AmbientConditions> ambientConditions = null;
		try {
			ambientConditions = fceDAO.retrieveFceAmbientConditions(fceId);
		} catch (DAOException de) {
			DisplayUtil.displayError("Failed to retrieve Inspection Report Ambient Condition");
			logger.error(de.getMessage(), de);
			throw de;
		}
		return ambientConditions;
	}

	@Override
	public void modifyFceObservationsAndConcerns(FullComplianceEval fce) throws DAOException {
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			fceDAO.updateFceObservationsAndConcerns(fce);
		} catch (DAOException de) {
			DisplayUtil.displayError("Failed to update Inspection Report Observations And Concerns");
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	private Integer createInspectionReportWorkflow(FullComplianceEval fce) 
			throws DAOException, ServiceFactoryException, RemoteException {
		
		logger.debug("Creating inspection report workflow");
		
		ReadWorkFlowService wfBO = getReadWorkFlowService();
		
		String ptName = WorkFlowProcess.INSPECTION_REPORT_WORKFLOW_NAME;

		Integer	workflowId = wfBO.retrieveWorkflowTempIdAndNm().get(ptName);
		
		if (workflowId == null) {

			String s = "Failed to find workflow for \"" + ptName
					+ "\" for inspection report " + fce.getInspId();
			logger.error(s);
			throw new DAOException(s);
		}

		String rush = "N";
		Integer fpId = fce.getFpId();
		Integer userId = InfrastructureDefs.getCurrentUserId();
		Timestamp wfStartDt = new Timestamp(System.currentTimeMillis());
		Timestamp wfDueDt = null;

		logger.debug("creating inspection report workflow with workflowId: " + workflowId
				+ ", ReportID "	+ fce.getId()
				+ ", FPid: " + fpId
				+ ", UID: "	+ userId 
				+ ", Due Date: " + wfDueDt);
		
		WorkFlowManager wfm = new WorkFlowManager();
		
		WorkFlowResponse resp = wfm.submitProcess(workflowId, fce.getId(),
				fpId, userId, rush, wfStartDt, wfDueDt, null);

		if (resp.hasError() || resp.hasFailed()) {
			StringBuffer errMsg = new StringBuffer();
			StringBuffer recomMsg = new StringBuffer();
			String[] errorMsgs = resp.getErrorMessages();
			String[] recomMsgs = resp.getRecommendationMessages();
			
			for (String msg : errorMsgs) {
				errMsg.append(msg + " ");
			}
			
			for (String msg : recomMsgs) {
				recomMsg.append(msg + " ");
			}
			
			String s = "Error encountered trying to create workflow for inspection report "
					+ fce.getInspId() + ": " + errMsg.toString();
			
			logger.error(s);
			
			throw new DAOException(s + " " + recomMsg);
		}
		
		return workflowId;
	}
	
	@Override
	public List<SelectItem> getAllFinalPermits(FullComplianceEval fce) throws DAOException {
		List<SelectItem> availablePermits = new ArrayList<SelectItem>();

		String facilityID = fce.getFacilityId();
		String permitStatusCd = PermitGlobalStatusDef.ISSUED_FINAL;

		try {
			List<Permit> permitList = getPermitService().search(null, null, null, null, null, null, null,
					null, facilityID, null, permitStatusCd, null, null, null, null, true, null);

			if (null != permitList) {
				Collections.sort(permitList, new Comparator<Permit>() {
					@Override
					public int compare(Permit p1, Permit p2) {
						if (p2.getFinalIssueDate() == null)
							return -1;
						if (p1.getFinalIssueDate() == null)
							return 1;
						return p2.getFinalIssueDate().compareTo(p1.getFinalIssueDate());
					};
				});

				for (Permit permit : permitList) {
					if (permit.getFinalIssueDate() != null) {
						String finalIssueDate = new SimpleDateFormat("MM/dd/yyyy").format(permit.getFinalIssueDate());
						String permitType = (String) PermitTypeDef.getPermitTypeDesc(permit.getPermitType());
						
						String permitDisplay = finalIssueDate + " - " + permit.getPermitNumber() + " - "
								+ permitType;
						
						availablePermits.add(new SelectItem(permit.getPermitID(), permitDisplay, permit.getDescription()));
					}
				}
			}

		} catch (RemoteException e) {
			DisplayUtil.displayError("Failed to retieve Permits for Selection");
			logger.error(e.getMessage(), e);
			throw new DAOException("Failed to retieve Permits for Selection", e);
		}
		return availablePermits;
	}
	
	@Override
	public void updateAssociatedPermits(FullComplianceEval fce, List<Integer> permitsToAssociate,
			List<Integer> permitsToDisassoicate) throws DAOException {

		Integer fceId = fce.getId();

		// Add rows for the newly associated EUs.
		for (Integer permitId : permitsToAssociate) {
			createAssociatedPermitIdRef(fceId, permitId);
		}

		// Delete rows for the disassociated EUs.
		for (Integer permitId : permitsToDisassoicate) {
			deleteAssociatedPermitIdRef(fceId, permitId);
		}

		// Calling modify fce here to handle concurrent modification of associated permits
		fullComplianceEvalDAO().modifyFce(fce);
	}

	private void createAssociatedPermitIdRef(Integer fceId, Integer permitId) throws DAOException {
		fullComplianceEvalDAO().createAssociatedPermitIdRef(fceId, permitId);
	}
	
	private void deleteAssociatedPermitIdRef(Integer fceId, Integer permitId) throws DAOException {
		fullComplianceEvalDAO().deleteAssociatedPermitIdRef(fceId, permitId);
	}
	
	@Override
	public List<Integer> retrieveAssociatedPermitIdsByFceId(Integer fceId) throws DAOException {
		List<Integer> associatedPermitIds = new ArrayList<Integer>();
		associatedPermitIds = fullComplianceEvalDAO().retrieveAssociatedPermitIdsByFceId(fceId);
		
		return associatedPermitIds;
	}
	
	@Override
	public List<FceApplicationSearchLineItem> retrieveFceApplicationsBySearch(FullComplianceEval fce) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		List<FceApplicationSearchLineItem> applicationList = null;
		Timestamp startDt = null;
		Timestamp endDt = null;
		if (fce.getFcePreData().getDateRangePA().getStartDt()!=null)
			startDt = new Timestamp(fce.getFcePreData().getDateRangePA().getStartDt().getTime());
		if (fce.getFcePreData().getDateRangePA().getEndDt()!=null){
			endDt =  new Timestamp(fce.getFcePreData().getDateRangePA().getEndDt().getTime());
			Calendar cal = Calendar.getInstance();
			cal.setTime(endDt);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			endDt.setTime(cal.getTime().getTime());
		}
		try {
			applicationList = fceDAO.retrieveFceApplicationsBySearch(fce.getFacilityId(), startDt, endDt);
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
		return applicationList;
	}
	

	@Override
	public List<FceApplicationSearchLineItem> retrieveFceApplicationsPreserved(FullComplianceEval fce) throws DAOException{
		List <FceApplicationSearchLineItem> ret;
		//retrieve preserved search date range + retrieve preserved list
		try{
			ret = retrieveFceApplicationListPreservedByFceId(fce.getId());
			SearchDateRange dateRangePA = retrieveFcePreservedSearchDateRange(fce.getId(), FCEPreSnapshotTypeDef.PA);
			if (dateRangePA == null){
				dateRangePA = new SearchDateRange();
			}
			fce.getFcePreData().setDateRangePA(dateRangePA);			
			HashSet<Integer> applicationIds = new HashSet<Integer>();
			for (FceApplicationSearchLineItem item: ret){
				applicationIds.add(item.getApplicationId());
			}
			fce.getFcePreData().setApplicationList(new ArrayList<Integer>(applicationIds));
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
		return ret;
	}

	
	public List<FceApplicationSearchLineItem> retrieveFceApplicationListPreservedByFceId(Integer fceId) throws DAOException{
		return fullComplianceEvalDAO().retrieveFceApplicationListPreservedByFceId(fceId);
	}
	
	@Override
	public void updateFceApplicationsPreserved(FullComplianceEval fce) throws DAOException{
		setFceApplicationsPreserved(fce);
		modifyFce(fce);
	}
	
	private void setFceApplicationsPreserved(FullComplianceEval fce) throws DAOException{
		Timestamp startDt = new Timestamp(fce.getFcePreData().getDateRangePA().getStartDt().getTime());
		Timestamp endDt = new Timestamp(fce.getFcePreData().getDateRangePA().getEndDt().getTime());
		// delete & add    1. preserved date range    2. preserved application ids
		deleteFceApplicationsPreserved(fce.getId());
		addFceApplicationsSnapshot(fce.getId(), startDt, endDt, fce.getFcePreData().getApplicationList());
	}
	
	@Override
	public void clearFceApplicationsPreserved(FullComplianceEval fce) throws DAOException{
		deleteFceApplicationsPreserved(fce.getId());
		modifyFce(fce);
	}
	
	private void deleteFceApplicationsPreserved(Integer fceId) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			fceDAO.deleteFcePreservedSearchDateRange(fceId, FCEPreSnapshotTypeDef.PA); // delete date range preserved
			fceDAO.deleteFceApplicationPreservedList(fceId); // delete application ids preserved
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	@Override
	public void addFceApplicationsSnapshot(Integer fceId, Timestamp startDt, Timestamp endDt, List<Integer> ids) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			fceDAO.addFceSnapshotSearchDateRange(fceId, FCEPreSnapshotTypeDef.PA, startDt, endDt); //add preserved date range 	
			for (Integer id : ids){
				fceDAO.addFceApplicationSnapshotList(fceId, id); //add preserved List
			}
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	@Override
	public SearchDateRange retrieveFcePreservedSearchDateRange(Integer fceId, String snapshotTypeCd) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		return fceDAO.retrieveFcePreservedSearchDateRange(fceId, snapshotTypeCd);
	}
	
	@Override
	public void deleteAssociatedPermitConditionIdRefByPermitIds(List<Integer> permitIds) throws DAOException {
		if (permitIds != null && permitIds.size() > 0) {
			fullComplianceEvalDAO().deleteAssociatedPermitConditionIdRefByPermitIds(permitIds);
		}
	}

	@Override
	public void associatePermitConditionsByPermitIds(List<Integer> permitIds, Integer fceId) throws DAOException {
		if (permitIds != null && permitIds.size() > 0) {
			try {
				if (getPermitConditionService().retrievePermitConditionsCountByPermitIds(permitIds) > 0) {
					fullComplianceEvalDAO().associatePermitConditionsByPermitIds(permitIds, fceId);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
		}
	}
	
	@Override
	public List<FcePermitCondition> retrieveAssociatedPermitConditionsByFceId(Integer fceId) throws DAOException {
		List<FcePermitCondition> associatedPermitConditions = fullComplianceEvalDAO()
				.retrieveAssociatedPermitConditionsByFceId(fceId);
		
		return associatedPermitConditions;
	}
	
	@Override
	public List<PermitConditionSearchLineItem> retrieveExcludedPermitConditionsByFceId(Integer fceId) throws DAOException {
		List<PermitConditionSearchLineItem> associatedPermitConditions = fullComplianceEvalDAO()
				.retrieveExcludedPermitConditionsByFceId(fceId);
		
		return associatedPermitConditions;
	}
	
	
	//************Permit****************/
	@Override
	public List<Permit> retrieveFcePermitsBySearch(FullComplianceEval fce) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		PermitDAO permitDAO= permitDAO();
		List<Permit> permitList = null;
		Timestamp startDt = null;
		Timestamp endDt = null;
		if (fce.getFcePreData().getDateRangePT().getStartDt() != null)
			startDt = new Timestamp(fce.getFcePreData().getDateRangePT().getStartDt().getTime());
		if (fce.getFcePreData().getDateRangePT().getEndDt() != null){
			endDt =  new Timestamp(fce.getFcePreData().getDateRangePT().getEndDt().getTime());
		}
		String dateField = PermitSQLDAO.FINAL_ISSUANCE_DATE;
		try {
			permitList = permitDAO.searchPermits(null, null, null,
					null, null, null, null, null, fce.getFacilityId(),
					null, null, dateField, startDt, endDt,null, true, null);
			
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
		return permitList;
	}
	
	@Override
	public void updateFcePermitsPreserved(FullComplianceEval fce) throws DAOException {
		setFcePermitsPreserved(fce);
		modifyFce(fce);
	}
	
	private void setFcePermitsPreserved(FullComplianceEval fce) throws DAOException {
		Timestamp startDt = new Timestamp(fce.getFcePreData().getDateRangePT().getStartDt().getTime());
		Timestamp endDt = new Timestamp(fce.getFcePreData().getDateRangePT().getEndDt().getTime());
		// delete & add 1. preserved date range 2. preserved application ids
		deleteFcePermitsPreserved(fce.getId());
		addFcePermitsSnapshot(fce.getId(), startDt, endDt, fce.getFcePreData().getPermitList());
	}
	
	@Override
	public void clearFcePermitsPreserved(FullComplianceEval fce) throws DAOException {
		deleteFcePermitsPreserved(fce.getId());
		modifyFce(fce);
	}

	private void deleteFcePermitsPreserved(Integer fceId) throws DAOException {
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			// delete date range preserved
			fceDAO.deleteFcePreservedSearchDateRange(fceId, FCEPreSnapshotTypeDef.PT);
			// delete application ids preserved
			fceDAO.deleteFcePermitPreservedList(fceId);
		} catch (DAOException de) {
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	@Override
	public void addFcePermitsSnapshot(Integer fceId, Timestamp startDt, Timestamp endDt, List<Integer> ids) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			fceDAO.addFceSnapshotSearchDateRange(fceId, FCEPreSnapshotTypeDef.PT, startDt, endDt); //add preserved date range 	
			for (Integer id : ids){
				fceDAO.addFcePermitSnapshotList(fceId, id); //add preserved List
			}
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	@Override
	public List<Permit> retrieveFcePermitsPreserved(FullComplianceEval fce) throws DAOException{
		List <Permit> ret;
		//retrieve preserved search date range + retrieve preserved list
		try{
			ret = retrieveFcePermitListPreservedByFceId(fce.getId());
			SearchDateRange dateRangePT = retrieveFcePreservedSearchDateRange(fce.getId(), FCEPreSnapshotTypeDef.PT);
			if (dateRangePT == null){
				dateRangePT = new SearchDateRange();
			}
			fce.getFcePreData().setDateRangePT(dateRangePT);	
			HashSet<Integer> permitIds = new HashSet<Integer>();
			for (Permit item: ret){
				permitIds.add(item.getPermitID());
			}
			fce.getFcePreData().setPermitList(new ArrayList<Integer>(permitIds));
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
		return ret;
	}

	
	public List<Permit> retrieveFcePermitListPreservedByFceId(Integer fceId) throws DAOException{
		return fullComplianceEvalDAO().retrieveFcePermitListPreservedByFceId(fceId);
	}
	
	@Override
	public void modifyAssociatedPermitConditionIdRef(List<FcePermitCondition> pcs) throws DAOException {
		try {
			for (FcePermitCondition pc : pcs) {
				fullComplianceEvalDAO().modifyAssociatedPermitConditionIdRef(pc);
			}
		} catch (DAOException de) {
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	@Override
	public void deleteAssociatedPermitConditionIdRefsByFceId(Integer fceId) throws DAOException {
		try {
			fullComplianceEvalDAO().deleteAssociatedPermitConditionIdRefsByFceId(fceId);
		} catch (DAOException de) {
			logger.error(de.getMessage(), de);
			throw de;
		}
	}

	@Override
	public void updatePermitConditionSelections(FullComplianceEval fce, List<Integer> permitConditionsToInclude,
			List<Integer> permitConditionsToExclude) throws DAOException {
		Integer fceId = fce.getId();
		deleteAssociatedPermitConditionIds(fceId, permitConditionsToExclude);
		createAssociatedPermitConditionIds(fceId, permitConditionsToInclude);
	}
	
	@Override
	public void deleteAssociatedPermitConditionIds(Integer fceId, List<Integer> permitConditions)
			throws DAOException {
		try {
			for (Integer condId : permitConditions) {
				fullComplianceEvalDAO().deleteAssociatedPermitConditionIdRef(fceId, condId);
			}
		} catch (DAOException de) {
			logger.error(de.getMessage(), de);
			throw de;
		}
	}

	@Override
	public void createAssociatedPermitConditionIds(Integer fceId, List<Integer> permitConditions)
			throws DAOException {
		try {
			for (Integer condId : permitConditions) {
				fullComplianceEvalDAO().createAssociatedPermitConditionIdRef(fceId, condId);
			}
		} catch (DAOException de) {
			logger.error(de.getMessage(), de);
			throw de;
		}
	}


	//****************Stack Test
	@Override
	public List<FceStackTestSearchLineItem> retrieveFceStackTestsBySearch(FullComplianceEval fce) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		List<FceStackTestSearchLineItem> ret = null;
		Timestamp startDt = null;
		Timestamp endDt = null;
		if (fce.getFcePreData().getDateRangeST().getStartDt() != null)
			startDt = new Timestamp(fce.getFcePreData().getDateRangeST().getStartDt().getTime());
		if (fce.getFcePreData().getDateRangeST().getEndDt() != null){
			endDt =  new Timestamp(fce.getFcePreData().getDateRangeST().getEndDt().getTime());
		}
		
		ret = fceDAO.retrieveFceStackTestsBySearch(fce.getFacilityId(), startDt, endDt);
			
		for (FceStackTestSearchLineItem stackTest:ret){
			retrieveStackTestPollutantsAndEus(stackTest);
			retrieveStackTestDatesById(stackTest);
		}
		return ret;
	}
	
	@Override
	public void updateFceStackTestsPreserved(FullComplianceEval fce) throws DAOException{
		setFceStackTestsPreserved(fce);
		modifyFce(fce);
	}
	
	private void setFceStackTestsPreserved(FullComplianceEval fce) throws DAOException{
		Timestamp startDt = new Timestamp(fce.getFcePreData().getDateRangeST().getStartDt().getTime());
		Timestamp endDt = new Timestamp(fce.getFcePreData().getDateRangeST().getEndDt().getTime());
		// delete & add    1. preserved date range    2. preserved application ids
		deleteFceStackTestsPreserved(fce.getId());
		addFceStackTestsSnapshot(fce.getId(), startDt, endDt, fce.getFcePreData().getStackTestList());
	}
	
	@Override
	public void clearFceStackTestsPreserved(FullComplianceEval fce) throws DAOException {
		deleteFceStackTestsPreserved(fce.getId());
		modifyFce(fce);
	}

	private void deleteFceStackTestsPreserved(Integer fceId) throws DAOException {
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			// delete date range preserved
			fceDAO.deleteFcePreservedSearchDateRange(fceId, FCEPreSnapshotTypeDef.ST);
			// delete application ids preserved
			fceDAO.deleteFceStackTestPreservedList(fceId);
		} catch (DAOException de) {
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	@Override
	public void addFceStackTestsSnapshot(Integer fceId, Timestamp startDt, Timestamp endDt, List<Integer> ids) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			fceDAO.addFceSnapshotSearchDateRange(fceId, FCEPreSnapshotTypeDef.ST, startDt, endDt); //add preserved date range 	
			for (Integer id : ids){
				fceDAO.addFceStackTestSnapshotList(fceId, id); //add preserved List
			}
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	@Override
	public List<FceStackTestSearchLineItem> retrieveFceStackTestsPreserved(FullComplianceEval fce) throws DAOException{
		List <FceStackTestSearchLineItem> ret;
		//retrieve preserved search date range + retrieve preserved list
		try{
			ret = retrieveFceStackTestListPreservedByFceId(fce.getId());

			SearchDateRange dateRangeST = retrieveFcePreservedSearchDateRange(fce.getId(), FCEPreSnapshotTypeDef.ST);
			if (dateRangeST == null){
				dateRangeST = new SearchDateRange();
			}
			fce.getFcePreData().setDateRangeST(dateRangeST);
			
			HashSet<Integer> stackTestIds = new HashSet<Integer>();
			for (FceStackTestSearchLineItem item: ret){
				stackTestIds.add(item.getStackTestId());
			}
			fce.getFcePreData().setStackTestList(new ArrayList<Integer>(stackTestIds));
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
		return ret;
	}

	//retreive list of stack tests & populate pollutants, eus, failed pollutants and test dates
	public List<FceStackTestSearchLineItem> retrieveFceStackTestListPreservedByFceId(Integer fceId) throws DAOException{
		List <FceStackTestSearchLineItem> ret = fullComplianceEvalDAO().retrieveFceStackTestListPreservedByFceId(fceId);		
		for (FceStackTestSearchLineItem stackTest:ret){
			retrieveStackTestPollutantsAndEus(stackTest);
			retrieveStackTestDatesById(stackTest);
		}
		return ret;
	}
	
	private void retrieveStackTestPollutantsAndEus(FceStackTestSearchLineItem stackTest) throws DAOException{
		StackTestDAO stackTestDAO = stackTestDAO();
		List<StackTestedPollutant> testPolls = stackTestDAO().retrieveTestPollutantsAndEus(stackTest.getStackTestId());
		TreeSet<String> pollTested = new TreeSet<String>();
		TreeSet<String> pollFailed = new TreeSet<String>();
		TreeSet<String> testEus = new TreeSet<String>();
		
		for (StackTestedPollutant stp:testPolls){
			if (stp.getPollutantCd() != null) {
				pollTested.add(stp.getPollutantDsc());
				if (CetaStackTestResultsDef.FAIL.equals(stp.getStackTestResultsCd())){
					pollFailed.add(stp.getPollutantDsc());
				}
			}
			if (stp.getTestedEu() != null){
				if (stp.getEpaEmuId() == null){
					logger.error("Failed to retrieve EPA_EMU_ID for stack test" + stackTest.getStckId());
				}
				testEus.add(stp.getEpaEmuId());
			}
		}
		
		StringBuffer pollutantsTested = new StringBuffer();
		StringBuffer pollutantsFailed = new StringBuffer();
		StringBuffer eus = new StringBuffer();
		
		for (String st:pollTested){
			if (pollutantsTested.length() > 0){
				pollutantsTested.append(", ");
			}
			pollutantsTested.append(st);
		}
		for (String st:pollFailed){
			if (pollutantsFailed.length() > 0){
				pollutantsFailed.append(", ");
			}
			pollutantsFailed.append(st);
		}
		for (String st:testEus){
			if (eus.length() > 0){
				eus.append(", ");
			}
			eus.append(st);
		}
		
		stackTest.setPollutantsTested(pollutantsTested.toString());
		stackTest.setPollutantsFailed(pollutantsFailed.toString());
		stackTest.setEus(eus.toString());
	}
	
	private void retrieveStackTestDatesById(FceStackTestSearchLineItem stackTest) throws DAOException{
		List<TestVisitDate> visitDates = stackTestDAO().retrieveStackTestDatesById(stackTest.getStackTestId());
		stackTest.setTestDatesString(stackTest.getDatesStrings(visitDates));
		if (visitDates.size() > 0 && visitDates.get(0).getTestDate() != null) {
			stackTest.setFirstVisitDate(visitDates.get(0).getTestDate());
		}
	}
	
	
	//Compliance Report
	@Override
	public List<ComplianceReportList> retrieveFceComplianceReportBySearch(FullComplianceEval fce) throws DAOException{
		List<ComplianceReportList> ret = null;
		Timestamp startDt = null;
		Timestamp endDt = null;
		if (fce.getFcePreData().getDateRangeCR().getStartDt() != null)
			startDt = new Timestamp(fce.getFcePreData().getDateRangeCR().getStartDt().getTime());
		if (fce.getFcePreData().getDateRangeCR().getEndDt() != null){
			endDt =  new Timestamp(fce.getFcePreData().getDateRangeCR().getEndDt().getTime());
			Calendar cal = Calendar.getInstance();
			cal.setTime(endDt);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			endDt.setTime(cal.getTime().getTime());
		}
		String dateBy = ComplianceReportSearchDateByDef.COMPLIANCE_REPORT_RECEIVED_DT;
		String reportStatus = ComplianceReportStatusDef.COMPLIANCE_STATUS_SUBMITTED;
		
		ret = fullComplianceEvalDAO().retrieveFceComplianceReportBySearch(fce.getFacilityId(), startDt, endDt);
		return ret;
	}
	
	@Override
	public void updateFceComplianceReportsPreserved(FullComplianceEval fce) throws DAOException{
		setFceComplianceReportsPreserved(fce);
		modifyFce(fce);
	}
	
	private void setFceComplianceReportsPreserved(FullComplianceEval fce) throws DAOException{
		Timestamp startDt = new Timestamp(fce.getFcePreData().getDateRangeCR().getStartDt().getTime());
		Timestamp endDt = new Timestamp(fce.getFcePreData().getDateRangeCR().getEndDt().getTime());
		// delete & add    1. preserved date range    2. preserved application ids
		deleteFceComplianceReportsPreserved(fce.getId());
		addFceComplianceReportsSnapshot(fce.getId(), startDt, endDt, fce.getFcePreData().getComplianceReportList());
	}
	
	@Override
	public void clearFceComplianceReportsPreserved(FullComplianceEval fce) throws DAOException {
		deleteFceComplianceReportsPreserved(fce.getId());
		modifyFce(fce);
	}

	public void deleteFceComplianceReportsPreserved(Integer fceId) throws DAOException {
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			// delete date range preserved
			fceDAO.deleteFcePreservedSearchDateRange(fceId, FCEPreSnapshotTypeDef.CR);
			// delete compliance report ids preserved
			fceDAO.deleteFceComplianceReportPreservedList(fceId);
		} catch (DAOException de) {
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	@Override
	public void addFceComplianceReportsSnapshot(Integer fceId, Timestamp startDt, Timestamp endDt, List<Integer> ids) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			fceDAO.addFceSnapshotSearchDateRange(fceId, FCEPreSnapshotTypeDef.CR, startDt, endDt); //add preserved date range 	
			for (Integer id : ids){
				fceDAO.addFceComplianceReportSnapshotList(fceId, id); //add preserved List
			}
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	
	@Override
	public List<ComplianceReportList> retrieveFceComplianceReportsPreserved(FullComplianceEval fce) throws DAOException{
		List <ComplianceReportList> ret;
		//retrieve preserved search date range + retrieve preserved list
		try{
			ret = retrieveFceComplianceReportListPreservedByFceId(fce.getId());
			SearchDateRange dateRangeCR = retrieveFcePreservedSearchDateRange(fce.getId(), FCEPreSnapshotTypeDef.CR);
			if (dateRangeCR == null){
				dateRangeCR = new SearchDateRange();
			}
			fce.getFcePreData().setDateRangeCR(dateRangeCR);
			
			HashSet<Integer> complianceReportIds = new HashSet<Integer>();
			for (ComplianceReportList item: ret){
				complianceReportIds.add(item.getReportId());
			}
			fce.getFcePreData().setComplianceReportList(new ArrayList<Integer>(complianceReportIds));
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
		return ret;
	}

	
	public List<ComplianceReportList> retrieveFceComplianceReportListPreservedByFceId(Integer fceId) throws DAOException{
		return fullComplianceEvalDAO().retrieveFceComplianceReportListPreservedByFceId(fceId);		
	}
	
	//Site Visit
	@Override
	public void updateFceSiteVisitsPreserved(FullComplianceEval fce) throws DAOException{
		setFceSiteVisitsPreserved(fce);
		modifyFce(fce);
	}
	
	private void setFceSiteVisitsPreserved(FullComplianceEval fce) throws DAOException{
		Timestamp startDt = new Timestamp(fce.getFcePreData().getDateRangeSiteVisits().getStartDt().getTime());
		Timestamp endDt = new Timestamp(fce.getFcePreData().getDateRangeSiteVisits().getEndDt().getTime());
		// delete & add    1. preserved date range    2. preserved site visit ids
		deleteFceSiteVisitsPreserved(fce.getId());
		addFceSiteVisitsSnapshot(fce.getId(), startDt, endDt, fce.getFcePreData().getSiteVisitList());
	}
	
	@Override
	public void clearFceSiteVisitsPreserved(FullComplianceEval fce) throws DAOException{
		deleteFceSiteVisitsPreserved(fce.getId());
		modifyFce(fce);
	}
	
	private void deleteFceSiteVisitsPreserved(Integer fceId) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			fceDAO.deleteFcePreservedSearchDateRange(fceId, FCEPreSnapshotTypeDef.SV); // delete date range preserved
			fceDAO.deleteFceSiteVisitPreservedList(fceId); // delete  site visit ids preserved
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	@Override
	public void addFceSiteVisitsSnapshot(Integer fceId, Timestamp startDt, Timestamp endDt, List<Integer> ids) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			fceDAO.addFceSnapshotSearchDateRange(fceId, FCEPreSnapshotTypeDef.SV, startDt, endDt); //add preserved date range 	
			for (Integer id : ids){
				fceDAO.addFceSiteVisitSnapshotList(fceId, id); //add preserved List
			}
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	@Override
	public List<SiteVisit> retrieveFceSiteVisitsPreserved(FullComplianceEval fce) throws DAOException{
		List<SiteVisit> ret;
		//retrieve preserved search date range + retrieve preserved list
		try{
			ret = fullComplianceEvalDAO().retrieveFceSiteVisitListPreservedByFceId(fce.getId());
			SearchDateRange dateRangeSiteVisit = retrieveFcePreservedSearchDateRange(fce.getId(), FCEPreSnapshotTypeDef.SV);
			if (dateRangeSiteVisit == null){
				dateRangeSiteVisit = new SearchDateRange();
			}
			fce.getFcePreData().setDateRangeSiteVisits(dateRangeSiteVisit);	
			HashSet<Integer> svIds = new HashSet<Integer>();
			for (SiteVisit item: ret){
				svIds.add(item.getId());
			}
			fce.getFcePreData().setSiteVisitList(new ArrayList<Integer>(svIds));
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
		return ret;
	}
	
	@Override
	public List<FceAmbientMonitorLineItem> searchFacilityMonitorsByDate(FullComplianceEval fce) throws DAOException {
		Timestamp startDt = null;
		Timestamp endDt = null;
		if (fce.getFcePreData().getDateRangeAmbientMonitors().getStartDt() != null)
			startDt = fce.getFcePreData().getDateRangeAmbientMonitors().getStartDt();
		if (fce.getFcePreData().getDateRangeAmbientMonitors().getEndDt() != null){
			endDt = fce.getFcePreData().getDateRangeAmbientMonitors().getEndDt();
		} else {
			endDt = ((InfrastructureDefs) FacesUtil.getManagedBean("infraDefs")).getTodaysDate();
		}
		
		List<FceAmbientMonitorLineItem> ret = getMonitoringService().searchFacilityMonitorsByDate(fce.getFacilityId(), startDt, endDt);
		return ret;
	}

	@Override
	public void clearFceAmbientMonitorsPreserved(FullComplianceEval fce) throws DAOException {
		deleteFceAmbientMonitorsPreserved(fce.getId());
		modifyFce(fce);
	}
	
	private void deleteFceAmbientMonitorsPreserved(Integer fceId) throws DAOException {
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			fceDAO.deleteFcePreservedSearchDateRange(fceId, FCEPreSnapshotTypeDef.MO); // delete date range preserved
			fceDAO.deleteFceAmbientMonitorPreservedList(fceId); // delete  Ambient Monitor ids preserved
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
	}

	@Override
	public List<FceAmbientMonitorLineItem> retrieveFceAmbientMonitorsPreserved(FullComplianceEval fce)
			throws DAOException {
		List<FceAmbientMonitorLineItem> ret;
		//retrieve preserved search date range + retrieve preserved list
		try{
			ret = fullComplianceEvalDAO().retrieveFceAmbientMonitorListPreservedByFceId(fce.getId());
			SearchDateRange dateRangeAmbientMonitor = retrieveFcePreservedSearchDateRange(fce.getId(), FCEPreSnapshotTypeDef.MO);
			if (dateRangeAmbientMonitor == null){
				dateRangeAmbientMonitor = new SearchDateRange();
			}
			fce.getFcePreData().setDateRangeAmbientMonitors(dateRangeAmbientMonitor);	
			HashSet<Integer> amIds = new HashSet<Integer>();
			for (FceAmbientMonitorLineItem item: ret){
				amIds.add(item.getMonitorId());
			}
			fce.getFcePreData().setAmbientMonitorList(new ArrayList<Integer>(amIds));
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
		return ret;
	}

	@Override
	public void updateFceAmbientMonitorsPreserved(FullComplianceEval fce) throws DAOException {
		setFceAmbientMonitorsPreserved(fce);
		modifyFce(fce);
	}
	
	private void setFceAmbientMonitorsPreserved(FullComplianceEval fce) throws DAOException {
		Timestamp startDt = new Timestamp(fce.getFcePreData().getDateRangeAmbientMonitors().getStartDt().getTime());
		Timestamp endDt = new Timestamp(fce.getFcePreData().getDateRangeAmbientMonitors().getEndDt().getTime());
		// delete & add    1. preserved date range    2. preserved site visit ids
		deleteFceAmbientMonitorsPreserved(fce.getId());
		addFceAmbientMonitorsSnapshot(fce.getId(), startDt, endDt, fce.getFcePreData().getAmbientMonitorList());
	}
	
	@Override
	public void addFceAmbientMonitorsSnapshot(Integer fceId, Timestamp startDt, Timestamp endDt, List<Integer> ids) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			fceDAO.addFceSnapshotSearchDateRange(fceId, FCEPreSnapshotTypeDef.MO, startDt, endDt); //add preserved date range 	
			for (Integer id : ids){
				fceDAO.addFceAmbientMonitorSnapshotList(fceId, id); //add preserved List
			}
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
	}


//Correspondence
	@Override
	public List<Correspondence> retrieveFceCorrespondenceBySearch(FullComplianceEval fce) throws DAOException{
		List<Correspondence> ret = new ArrayList<Correspondence>();
		
		Timestamp startDt = null;
		Timestamp endDt = null;
		if (fce.getFcePreData().getDateRangeDC().getStartDt() != null)
			startDt = new Timestamp(fce.getFcePreData().getDateRangeDC().getStartDt().getTime());
		if (fce.getFcePreData().getDateRangeDC().getEndDt() != null){
			Calendar calEndDate = Calendar.getInstance();
	    	calEndDate.setTimeInMillis(fce.getFcePreData().getDateRangeDC().getEndDt().getTime());
	    	calEndDate.set(Calendar.HOUR_OF_DAY, 23);
	    	calEndDate.set(Calendar.MINUTE, 59);
	    	calEndDate.set(Calendar.SECOND, 59);
	    	calEndDate.set(Calendar.MILLISECOND, 999);
			endDt =  new Timestamp(calEndDate.getTimeInMillis());
		}		
		
		Correspondence searchObj =  new Correspondence();
		searchObj.setFacilityID(fce.getFacilityId());
		searchObj.setDirectionCd(CorrespondenceDirectionDef.INCOMING);
		Correspondence[] in = correspondenceDAO().searchCorrespondenceByDate(searchObj, CorrespondenceSQLDAO.RECEIPT_DATE, startDt, endDt);
		
		searchObj.setDirectionCd(CorrespondenceDirectionDef.OUTGOING);
		Correspondence[] out = correspondenceDAO().searchCorrespondenceByDate(searchObj, CorrespondenceSQLDAO.DATE_GENERATED, startDt, endDt);
		
		ret.addAll(Arrays.asList(in));
		ret.addAll(Arrays.asList(out));

		for (Correspondence item : ret){
			correspondenceDAO().retrieveCorrespondenceAttachmentCount(item);
		}
		
		Collections.sort(ret, new Comparator<Correspondence>() {
			@Override
			public int compare(Correspondence c1, Correspondence c2) {
				return c1.getCorId().compareTo(c2.getCorId());
			}
		}
		);
		
		return ret;
	}
	
	@Override
	public void updateFceCorrespondencesPreserved(FullComplianceEval fce) throws DAOException{
		setFceCorrespondencesPreserved(fce);
		modifyFce(fce);
	}
	
	private void setFceCorrespondencesPreserved(FullComplianceEval fce) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		Timestamp startDt = new Timestamp(fce.getFcePreData().getDateRangeDC().getStartDt().getTime());
		Timestamp endDt = new Timestamp(fce.getFcePreData().getDateRangeDC().getEndDt().getTime());
		// delete & add    1. preserved date range    2. preserved application ids
		deleteFceCorrespondencesPreserved(fce.getId());
		addFceCorrespondencesSnapshot(fce.getId(), startDt, endDt, fce.getFcePreData().getCorrespondenceList());
	}
	
	@Override
	public void clearFceCorrespondencesPreserved(FullComplianceEval fce) throws DAOException{
		deleteFceCorrespondencesPreserved(fce.getId());
		modifyFce(fce);
	}
	
	private void deleteFceCorrespondencesPreserved(Integer fceId) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			fceDAO.deleteFcePreservedSearchDateRange(fceId, FCEPreSnapshotTypeDef.DC); // delete date range preserved
			fceDAO.deleteFceCorrespondencePreservedList(fceId); // delete application ids preserved
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	@Override
	public void addFceCorrespondencesSnapshot(Integer fceId, Timestamp startDt, Timestamp endDt, List<Integer> ids) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			fceDAO.addFceSnapshotSearchDateRange(fceId, FCEPreSnapshotTypeDef.DC, startDt, endDt); //add preserved date range 	
			for (Integer id : ids){
				fceDAO.addFceCorrespondenceSnapshotList(fceId, id); //add preserved List
			}
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	@Override
	public List<Correspondence> retrieveFceCorrespondencesPreserved(FullComplianceEval fce) throws DAOException{
		List <Correspondence> ret = new ArrayList<Correspondence>();
		//retrieve preserved search date range + retrieve preserved list
		try{
			ret = retrieveFceCorrespondenceListPreservedByFceId(fce.getId());
			
			SearchDateRange dateRangeDC = retrieveFcePreservedSearchDateRange(fce.getId(), FCEPreSnapshotTypeDef.DC);
			if (dateRangeDC == null){
				dateRangeDC = new SearchDateRange();
			}
			fce.getFcePreData().setDateRangeDC(dateRangeDC);
			
			HashSet<Integer> correspondenceIds = new HashSet<Integer>();
			for (Correspondence item: ret){
				correspondenceIds.add(item.getCorrespondenceID());
			}
			fce.getFcePreData().setCorrespondenceList(new ArrayList<Integer>(correspondenceIds));
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
		return ret;
	}

	public List<Correspondence> retrieveFceCorrespondenceListPreservedByFceId(Integer fceId) throws DAOException{
		List <Correspondence> ret = fullComplianceEvalDAO().retrieveFceCorrespondenceListPreservedByFceId(fceId);		
		for (Correspondence item : ret){
			correspondenceDAO().retrieveCorrespondenceAttachmentCount(item);
		}
		return ret;
	}
	
	//Emissions Inventory
	
	@Override
	public List<FceEmissionsInventoryLineItem> retrieveFceEmissionsInventoryBySearch(FullComplianceEval fce) throws DAOException{
		List<FceEmissionsInventoryLineItem> ret = new ArrayList<FceEmissionsInventoryLineItem>();
		Timestamp startDt = null;
		Timestamp endDt = ((InfrastructureDefs)FacesUtil.getManagedBean("infraDefs")).getTodaysDate();
		if (fce.getFcePreData().getDateRangeEI().getStartDt() != null)
			startDt = new Timestamp(fce.getFcePreData().getDateRangeEI().getStartDt().getTime());
		if (fce.getFcePreData().getDateRangeEI().getEndDt() != null){
			endDt =  new Timestamp(fce.getFcePreData().getDateRangeEI().getEndDt().getTime());
		} 
		Calendar cal = Calendar.getInstance();
		cal.setTime(endDt);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		endDt.setTime(cal.getTime().getTime());

		ret = fullComplianceEvalDAO().retrieveFceEmissionsInventoryBySearch(fce.getFacilityId(), startDt, endDt);	

        for (FceEmissionsInventoryLineItem r : ret) {
        	List<SCEmissionsReport> scs = emissionsReportDAO().retrieveAssociatedSCEmissionsReports(r.getEmissionsRptId());
        	for (SCEmissionsReport sc : scs) {
        		r.getRegulatoryRequirementCds().add(sc.getRegulatoryRequirementCd());
        		if (r.getContentTypeCd() == null) {
        			r.setContentTypeCd(sc.getContentTypeCd());
        		}
        	}
        	buildRegulatoryRequirementCdsString(r);
        	HashMap<String, Float> totalEmissions = fullComplianceEvalDAO().retrieveEmissionsInventoryPollutantTotalEmissions(r.getEmissionsRptId());
        	r.setPollutantEmissionsValue(totalEmissions);   
        }
		return ret;
	}
	
	@Override
	public void updateFceEmissionsInventoriesPreserved(FullComplianceEval fce) throws DAOException{
		setFceEmissionsInventoriesPreserved(fce);
		modifyFce(fce);
	}
	
	private void setFceEmissionsInventoriesPreserved(FullComplianceEval fce) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		Timestamp startDt = new Timestamp(fce.getFcePreData().getDateRangeEI().getStartDt().getTime());
		Timestamp endDt = new Timestamp(fce.getFcePreData().getDateRangeEI().getEndDt().getTime());
		// delete & add    1. preserved date range    2. preserved application ids
		deleteFceEmissionsInventoriesPreserved(fce.getId());
		addFceEmissionsInventoriesSnapshot(fce.getId(), startDt, endDt, fce.getFcePreData().getEmissionsInventoryList());
	}
	
	@Override
	public void clearFceEmissionsInventoriesPreserved(FullComplianceEval fce) throws DAOException{
		deleteFceEmissionsInventoriesPreserved(fce.getId());
		modifyFce(fce);
	}
	
	private void deleteFceEmissionsInventoriesPreserved(Integer fceId) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			fceDAO.deleteFcePreservedSearchDateRange(fceId, FCEPreSnapshotTypeDef.EI); // delete date range preserved
			fceDAO.deleteFceEmissionsInventoryPreservedList(fceId); // delete application ids preserved
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	@Override
	public void addFceEmissionsInventoriesSnapshot(Integer fceId, Timestamp startDt, Timestamp endDt, List<Integer> ids) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			fceDAO.addFceSnapshotSearchDateRange(fceId, FCEPreSnapshotTypeDef.EI, startDt, endDt); //add preserved date range 	
			for (Integer id : ids){
				fceDAO.addFceEmissionsInventorySnapshotList(fceId, id); //add preserved List
			}
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	@Override
	public List<FceEmissionsInventoryLineItem> retrieveFceEmissionsInventoriesPreserved(FullComplianceEval fce) throws DAOException{
		List <FceEmissionsInventoryLineItem> ret = new ArrayList<FceEmissionsInventoryLineItem>();
		//retrieve preserved search date range + retrieve preserved list
		try{
			ret = retrieveFceEmissionsInventoryListPreservedByFceId(fce.getId());
			
			SearchDateRange dateRangeEI = retrieveFcePreservedSearchDateRange(fce.getId(), FCEPreSnapshotTypeDef.EI);
			if (dateRangeEI == null){
				dateRangeEI = new SearchDateRange();
			}
			fce.getFcePreData().setDateRangeEI(dateRangeEI);
			
			HashSet<Integer> emissionsRptIds = new HashSet<Integer>();
			for (FceEmissionsInventoryLineItem item: ret){
				emissionsRptIds.add(item.getEmissionsRptId());
			}
			fce.getFcePreData().setEmissionsInventoryList(new ArrayList<Integer>(emissionsRptIds));
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
		return ret;
	}

	private void buildRegulatoryRequirementCdsString(FceEmissionsInventoryLineItem ei){
     	StringBuffer sb = new StringBuffer();
		Set<String> cds = ei.getRegulatoryRequirementCds();
       	Iterator<String> iter = cds.iterator();
    	int i = 0;
    	while (iter.hasNext()) {
    		String cd = iter.next();
    		sb.append(RegulatoryRequirementTypeDef.getData().getItems().getItemDesc(cd));
    		if (++i < cds.size()) {
    			sb.append("; ");
    		}
    	}
    	ei.setRegulatoryRequirementCdsString(sb.toString());		
	}
	
	
	public List<FceEmissionsInventoryLineItem> retrieveFceEmissionsInventoryListPreservedByFceId(Integer fceId) throws DAOException{
		List <FceEmissionsInventoryLineItem> ret = fullComplianceEvalDAO().retrieveFceEmissionsInventoryListPreservedByFceId(fceId);		
        for (FceEmissionsInventoryLineItem r : ret) {
        	List<SCEmissionsReport> scs = emissionsReportDAO().retrieveAssociatedSCEmissionsReports(r.getEmissionsRptId());
        	for (SCEmissionsReport sc : scs) {
        		r.getRegulatoryRequirementCds().add(sc.getRegulatoryRequirementCd());
        		if (r.getContentTypeCd() == null) {
        			r.setContentTypeCd(sc.getContentTypeCd());
        		}
        	}
        	buildRegulatoryRequirementCdsString(r);
        	HashMap<String, Float> totalEmissions = fullComplianceEvalDAO().retrieveEmissionsInventoryPollutantTotalEmissions(r.getEmissionsRptId());
        	r.setPollutantEmissionsValue(totalEmissions);       	
        }
		return ret;
	}
	
	
	@Override
	public List<FceContinuousMonitorLineItem> searchFacilityCemComLimitsByDate(FullComplianceEval fce)
			throws DAOException {
		Timestamp startDt = null;
		Timestamp endDt = null;
		
		if (fce.getFcePreData().getDateRangeContinuousMonitors().getStartDt() != null)
			startDt = fce.getFcePreData().getDateRangeContinuousMonitors().getStartDt();
		if (fce.getFcePreData().getDateRangeContinuousMonitors().getEndDt() != null) {
			endDt = fce.getFcePreData().getDateRangeContinuousMonitors().getEndDt();
		} else {
			endDt = ((InfrastructureDefs) FacesUtil.getManagedBean("infraDefs")).getTodaysDate();
		}
		
		return facilityDAO().searchFacilityCemComLimitsByDate(fce.getFacilityId(), startDt, endDt);
	}
	
	@Override
	public void updateFceCemComLimitsPreserved(FullComplianceEval fce) throws DAOException{
		setFceCemComLimitsPreserved(fce);
		modifyFce(fce);
	}
	
	private void setFceCemComLimitsPreserved(FullComplianceEval fce) throws DAOException{
		Timestamp startDt = new Timestamp(fce.getFcePreData().getDateRangeContinuousMonitors().getStartDt().getTime());
		Timestamp endDt = new Timestamp(fce.getFcePreData().getDateRangeContinuousMonitors().getEndDt().getTime());
		// delete & add    1. preserved date range    2. preserved application ids
		deleteFceCemComLimitsPreserved(fce.getId());
		addFceCemComLimitsSnapshot(fce.getId(), startDt, endDt, fce.getFcePreData().getContinuousMonitorList());
	}
	
	@Override
	public void clearFceCemComLimitsPreserved(FullComplianceEval fce) throws DAOException {
		deleteFceCemComLimitsPreserved(fce.getId());
		modifyFce(fce);
	}

	private void deleteFceCemComLimitsPreserved(Integer fceId) throws DAOException {
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			// delete date range preserved
			fceDAO.deleteFcePreservedSearchDateRange(fceId, FCEPreSnapshotTypeDef.LI);
			// delete application ids preserved
			fceDAO.deleteFceContinuousMonitorLimitPreservedList(fceId);
		} catch (DAOException de) {
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	@Override
	public void addFceCemComLimitsSnapshot(Integer fceId, Timestamp startDt, Timestamp endDt, List<Integer> ids) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			fceDAO.addFceSnapshotSearchDateRange(fceId, FCEPreSnapshotTypeDef.LI, startDt, endDt); //add preserved date range 	
			for (Integer id : ids){
				fceDAO.addFceContinuousMonitorLimitSnapshotList(fceId, id); //add preserved List
			}
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	@Override
	public List<FceContinuousMonitorLineItem> retrieveFceCemComLimitsPreserved(FullComplianceEval fce)
			throws DAOException {
		List<FceContinuousMonitorLineItem> ret;
		//retrieve preserved search date range + retrieve preserved list
		try{
			ret = fullComplianceEvalDAO().retrieveFceContinuousMonitorListPreservedByFceId(fce.getId());
			SearchDateRange dateRangeContMonitor = retrieveFcePreservedSearchDateRange(fce.getId(), FCEPreSnapshotTypeDef.LI);
			if (dateRangeContMonitor == null){
				dateRangeContMonitor = new SearchDateRange();
			}
			fce.getFcePreData().setDateRangeContinuousMonitors(dateRangeContMonitor);	
			HashSet<Integer> cmIds = new HashSet<Integer>();
			for (FceContinuousMonitorLineItem item: ret){
				cmIds.add(item.getLimitId());
			}
			fce.getFcePreData().setContinuousMonitorList(new ArrayList<Integer>(cmIds));
		} catch (DAOException de){
			logger.error(de.getMessage(), de);
			throw de;
		}
		return ret;
	}
	

	
	//Application: FceApplicationSearchLineItem
	//Permit: Permit
	//Stack Test:  FceStackTestSearchLineItem
	//Compliacne Report:: ComplianceReportList
	//Correspondence: Correspondence
	//Emissions Inventory: FceEmissionsInventoryLineItem
	//CEM/COM: FceContinuousMonitorLineItem
	//Ambient Monitors: FceAmbientMonitorLineItem
	//Site Visit: SiteVisit
	

	
	@Override
	public void modifyFceReferenceReviewStartDate(FullComplianceEval fce) throws DAOException{
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		try {
			fceDAO.updateFceReferenceReviewStartDate(fce);
			setSnapshotPreservedListByStartDate(fce, FCEPreSnapshotTypeDef.PA);
			setSnapshotPreservedListByStartDate(fce, FCEPreSnapshotTypeDef.PT);
			setSnapshotPreservedListByStartDate(fce, FCEPreSnapshotTypeDef.ST);
			setSnapshotPreservedListByStartDate(fce, FCEPreSnapshotTypeDef.CR);
			setSnapshotPreservedListByStartDate(fce, FCEPreSnapshotTypeDef.DC);
			setSnapshotPreservedListByStartDate(fce, FCEPreSnapshotTypeDef.EI);
			setSnapshotPreservedListByStartDate(fce, FCEPreSnapshotTypeDef.LI);
			setSnapshotPreservedListByStartDate(fce, FCEPreSnapshotTypeDef.MO);
			setSnapshotPreservedListByStartDate(fce, FCEPreSnapshotTypeDef.SV);
			modifyFce(fce);
		} catch (DAOException de) {
			logger.error(de.getMessage(), de);
			throw de;
		}
	}
	
	private void setSnapshotPreservedListByStartDate(FullComplianceEval fce, String snapshot) throws DAOException{
		//set search date range
		//setup each artifact: 1.Search. 2.Preserve Ids of searchResultList
		//set Preserved List for each snapshot
		fce.setPreDataDefaultDateRange(snapshot);
		Timestamp todaysDate = ((InfrastructureDefs)FacesUtil.getManagedBean("infraDefs")).getTodaysDate();
        if (FCEPreSnapshotTypeDef.PA.equals(snapshot)){
        	List<FceApplicationSearchLineItem> searchResultList = retrieveFceApplicationsBySearch(fce);  
        	fce.getFcePreData().setSnapshotIdList(searchResultList, FceApplicationSearchLineItem.class);
			fce.getFcePreData().getDateRangePA().setEndDt(todaysDate);
			setFceApplicationsPreserved(fce);
        
        } else if (FCEPreSnapshotTypeDef.PT.equals(snapshot)){
        	List<Permit> searchResultList = retrieveFcePermitsBySearch(fce);  
        	fce.getFcePreData().setSnapshotIdList(searchResultList, Permit.class);
        	fce.getFcePreData().getDateRangePT().setEndDt(todaysDate);
        	setFcePermitsPreserved(fce);
        
        } else if (FCEPreSnapshotTypeDef.ST.equals(snapshot)){
        	List<FceStackTestSearchLineItem> searchResultList = retrieveFceStackTestsBySearch(fce);  
        	fce.getFcePreData().setSnapshotIdList(searchResultList, FceStackTestSearchLineItem.class);
        	fce.getFcePreData().getDateRangeST().setEndDt(todaysDate);
        	setFceStackTestsPreserved(fce);
        	
        } else if (FCEPreSnapshotTypeDef.CR.equals(snapshot)){
        	List<ComplianceReportList> searchResultList = retrieveFceComplianceReportBySearch(fce);  
        	fce.getFcePreData().setSnapshotIdList(searchResultList, ComplianceReportList.class);
        	fce.getFcePreData().getDateRangeCR().setEndDt(todaysDate);
        	setFceComplianceReportsPreserved(fce);

        } else if (FCEPreSnapshotTypeDef.DC.equals(snapshot)){
        	List<Correspondence> searchResultList = retrieveFceCorrespondenceBySearch(fce);  
        	fce.getFcePreData().setSnapshotIdList(searchResultList, Correspondence.class);
        	fce.getFcePreData().getDateRangeDC().setEndDt(todaysDate);
        	setFceCorrespondencesPreserved(fce);

        } else if (FCEPreSnapshotTypeDef.EI.equals(snapshot)){
           	List<FceEmissionsInventoryLineItem> searchResultList = retrieveFceEmissionsInventoryBySearch(fce);  
        	fce.getFcePreData().setSnapshotIdList(searchResultList, FceEmissionsInventoryLineItem.class);
        	fce.getFcePreData().getDateRangeEI().setEndDt(todaysDate);
        	setFceEmissionsInventoriesPreserved(fce);

        } else if (FCEPreSnapshotTypeDef.LI.equals(snapshot)){
           	List<FceContinuousMonitorLineItem> searchResultList = searchFacilityCemComLimitsByDate(fce);  
        	fce.getFcePreData().setSnapshotIdList(searchResultList, FceContinuousMonitorLineItem.class);
        	fce.getFcePreData().getDateRangeContinuousMonitors().setEndDt(todaysDate);
        	setFceCemComLimitsPreserved(fce);

        } else if (FCEPreSnapshotTypeDef.MO.equals(snapshot)){
        	List<FceAmbientMonitorLineItem> searchResultList = searchFacilityMonitorsByDate(fce);  
        	fce.getFcePreData().setSnapshotIdList(searchResultList, FceAmbientMonitorLineItem.class);
        	fce.getFcePreData().getDateRangeAmbientMonitors().setEndDt(todaysDate);
        	setFceAmbientMonitorsPreserved(fce);
        	
        } else if (FCEPreSnapshotTypeDef.SV.equals(snapshot)){
        	List<SiteVisit> searchResultList = searchFacilitySiteVisitsByDate(fce);
        	fce.getFcePreData().setSnapshotIdList(searchResultList, SiteVisit.class);
        	fce.getFcePreData().getDateRangeSiteVisits().setEndDt(todaysDate);
        	setFceSiteVisitsPreserved(fce);
        }
		
	}
	
	public List<SiteVisit> searchFacilitySiteVisitsByDate(FullComplianceEval fce) throws DAOException{
		List<SiteVisit> ret;
		SiteVisit[] sv = searchSiteVisits(fce.getFacilityId(), fce.getFcePreData().getDateRangeSiteVisits().getStartDt(), fce.getFcePreData().getDateRangeSiteVisits().getEndDt());
		ret = Arrays.asList(sv);
		return ret;
	}

	@Override
	public void associateRptWithCurrentFacility(FullComplianceEval fce, Facility currentfacility, Integer userId) throws DAOException {
		// set the fpId in the inspection report to the current facility inventory
		fce.setFpId(currentfacility.getFpId());
		fullComplianceEvalDAO().modifyFce(fce);
		
	}


    /**
     * @param fce
     * @param trans
     * @return void
     * @throws DAOException
     * 
     */
//    public void deleteFce(FullComplianceEval fce) throws DAOException {
//
//    	Transaction trans = TransactionFactory.createTransaction();
//		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO(trans);
//        InfrastructureDAO infraDAO = infrastructureDAO(trans); 
//
//        for(FceAttachment fa : fce.getAttachments()) {
//            removeFceAttachment(fa, trans);
//        }
//        fceDAO.removeFceNotes(fce.getId());
//        
//		if (fce.getInspectionNotes() != null) {
//			for (Note fceNote : fce.getInspectionNotes()) {
//				infraDAO.removeNote(fceNote.getNoteId());
//			}
//		}
//
//		fceDAO.deleteAdditionalAQDStaffByFceId(fce.getId());
//		fceDAO.deleteFceAmbientConditionsByFceId(fce.getId());
//		fceDAO.deleteAssociatedPermitConditionIdRefsByFceId(fce.getId());
//		fceDAO.deleteAssociatedPermitIdRefsByFceId(fce.getId());
//		deletePreservedSnapshotRefs(fce.getId());
//		//TODO: SK
//		
//		// facilityDAO.deleteFacilityContactRelationship(facility.getFacilityId());
//		facilityDAO().deleteSiteVisitNoteXrefByFpId(fce.getFpId());
//		facilityDAO().deleteSiteVisitsXrefByFpid(fce.getFpId());
//		facilityDAO().deleteSiteVisitsByFpid(fce.getFpId());
//		//
//        fceDAO.removeFce(fce.getId());
//    }
    
    @Override
    public void deleteFcePermitRefs(Integer fceId) throws DAOException{
    	fullComplianceEvalDAO().deleteAssociatedPermitConditionIdRefsByFceId(fceId);
    	fullComplianceEvalDAO().deleteAssociatedPermitIdRefsByFceId(fceId);
    }
    
    @Override
    public void clearLastInspIdByFacility(String facilityId) throws DAOException{
    	fullComplianceEvalDAO().clearLastInspIdByFacility(facilityId);
    }

}