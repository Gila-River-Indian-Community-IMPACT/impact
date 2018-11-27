package us.oh.state.epa.stars2.bo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.database.dao.ApplicationDAO;
import us.oh.state.epa.stars2.database.dao.DelegationRequestDAO;
import us.oh.state.epa.stars2.database.dao.DocumentDAO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.application.DelegationRequest;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.ValidationException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

@Transactional(rollbackFor=Exception.class)
@Service
public class DelegationRequestBO extends BaseBO implements DelegationRequestService  {
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public DelegationRequest createDelegationRequest(DelegationRequest delegationRequest)
            throws DAOException, ValidationException {
        Transaction trans = TransactionFactory.createTransaction();
        DelegationRequest ret = null;
        
        try {
            ApplicationDAO appDAO = applicationDAO(trans);
            //first create the application record and get the application ID
            logger.debug("about to create application");
            delegationRequest.setApplicationNumber(appDAO.generateApplicationNumber(DelegationRequest.class));
            delegationRequest.setApplicationID(appDAO.createApplication(delegationRequest).getApplicationID());
            logger.debug("app created ("+delegationRequest.getApplicationID()+"). Ccreating delegation record");
            //second, create the delegation record
            ret = createDelegationRequest(delegationRequest, trans);
            logger.debug("delegation record created - committing");
            //commit
            trans.complete();
        } catch (RemoteException re) {
            cancelTransaction(trans, re);
        } finally { // Clean up our transaction stuff
            closeTransaction(trans);
        }
        return ret;
    } 

    
    /**
     * @param delegationRequest
     * @param trans
     * @return
     * @throws DAOException
     */
    private DelegationRequest createDelegationRequest(DelegationRequest delegationRequest,
            Transaction trans) throws DAOException {
        DelegationRequest ret = null;
        DelegationRequestDAO delegationDAO = delegationRequestDAO(trans);
        try {  
            DelegationRequest tempDelegationRequest = delegationDAO.createDelegationRequest(delegationRequest);
            String path = File.separator+"Facilities"+File.separator+ delegationRequest.getFacilityId() + File.separator+"Applications"+File.separator+delegationRequest.getApplicationID();
            DocumentUtil.mkDir(path);
            if (tempDelegationRequest != null) {
                ret = tempDelegationRequest;
            } else {
                logger.error("Failed to insert Relocation Request");
                throw new DAOException("Failed to create Relocation Request");
            }
            logger.debug("Done with create");
            if (tempDelegationRequest != null) {
                ret = tempDelegationRequest;
            } else {
                logger.error("Failed to insert Delegation Request");
                throw new DAOException("Failed to create delegation Request");
            }
            
        } catch (DAOException e) { // Throw it all away if we have an Exception
            logger.error("Unable to add row: " + e);
            throw e;
        } catch (IOException iox) {
            logger.error("Unable to create folder for Delegation attachments",iox);
            throw new DAOException("Unable to create folder for Delegation attachments", iox);
        }
        return ret;
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    
    public DelegationRequest modifyDelegationRequest(DelegationRequest delegationRequest)
            throws DAOException, ValidationException {
        Transaction trans = TransactionFactory.createTransaction();
        DelegationRequest ret = null;
        ApplicationDAO appDAO = applicationDAO(trans);
        try {
            logger.debug("attempting to save to Delegation: App LM:" + delegationRequest.getLastModified() + " RR LM:"  + delegationRequest.getDelegationLastModified());
            appDAO.modifyApplication(delegationRequest);
            ret = modifyDelegationRequest(delegationRequest, trans);
            trans.complete();
        } catch (RemoteException re) {
            cancelTransaction(trans, re);
        } finally { // Clean up our transaction stuff
            closeTransaction(trans);
        }
        return ret;
    } 
    
    /**
     * @param delegationRequest
     * @param trans
     * @return
     * @throws DAOException
     */
    private DelegationRequest modifyDelegationRequest(DelegationRequest delegationRequest,
            Transaction trans) throws DAOException {
        DelegationRequest ret = null;
        DelegationRequestDAO delegationDAO = delegationRequestDAO(trans);
        try {  
            DelegationRequest tempDelegationRequest = delegationDAO.modifyDelegationRequest(delegationRequest);
            if (tempDelegationRequest != null) {
                ret = tempDelegationRequest;
            } else {
                logger.error("NULL DAO response. Failed to modify Delegation Request");
                throw new DAOException("Failed to modify Delegation Request");
            }
        } catch (DAOException e) { // Throw it all away if we have an Exception
            logger.warn(e);
            throw e;
        }
        return ret;
    }
    
    /**
     * Locate all of the delegation requests for a given facility.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public DelegationRequest[] retrieveDelegationRequests(String facilityID)
            throws DAOException {
       // logger.debug("Delegate BO: retreiving requests for facility: " + facilityID);
       // return delegationRequestDAO().retrieveDelegationRequests(facilityID);
        return null;
    }
    
    /**
     * Locate delecation request by app id
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public DelegationRequest retrieveDelegationRequest(int applicationId)
            throws DAOException {
        DelegationRequest request = delegationRequestDAO().retrieveDelegationRequest(applicationId);
        //retrieve attachments and populate the managed bean
        request.setAttachments(delegationRequestDAO().retrieveAttachments(request.getFacilityId(), request.getApplicationID()));
        logger.debug("Del Object "+ applicationId + ": Just set to have " + request.getAttachments().length);
        try {
            Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
            a.setAttachmentList(request.getAttachments());
            logger.debug("AppID: "+ applicationId + ": Just set bean to have " + a.getAttachmentList().length);
        } catch (Exception e) {
           logger.error("Error setting attachments into manageed bean",e);
        }
         return request;
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public boolean deleteDelegationRequest(DelegationRequest delegationRequest)
            throws DAOException, ValidationException {
        Transaction trans = TransactionFactory.createTransaction();
        int appId = delegationRequest.getApplicationID();
        try {
            deleteDelegationAttachments(delegationRequest,trans);
            deleteDelegationRequest(delegationRequest, trans);
            ApplicationDAO appDAO = applicationDAO(trans);
            appDAO.removeApplication(appId);
            trans.complete();
            return true;
        } catch (DAOException e) {
            cancelTransaction(trans, e);
        } finally { // Clean up our transaction stuff
            closeTransaction(trans);
        }
        //it failed
        return false;
    } 

    
    /**
     * @param delegationRequest
     * @param trans
     * @return
     * @throws DAOException
     */
    private void deleteDelegationRequest(DelegationRequest delegationRequest,
            Transaction trans) throws DAOException {
        DelegationRequestDAO delegateDAO = delegationRequestDAO(trans);
        try {  
            logger.debug("deleting dr record... for "+delegationRequest.getApplicationID());
            DelegationRequest tempDelegationRequest = delegateDAO.deleteDelegationRequest(delegationRequest);
            if (tempDelegationRequest != null) {
                logger.error("Failed to delete Delegation Request");
                throw new DAOException("Failed to delete Delegation Request");
            }
        } catch (DAOException e) { // Throw it all away if we have an Exception
            logger.error("Error deleting RR record",e);
            throw e;
        }
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public boolean submit(DelegationRequest ret) {
        logger.debug("initiating workflow with Delegation ID #"+ret.getApplicationID());
        Integer externalId = ret.getApplicationID();
        Integer workflowId = null;
        int uid=InfrastructureDefs.getCurrentUserId();
        try {
            ReadWorkFlowService wfBO = ServiceFactory.getInstance().getReadWorkFlowService();
    
            String ptName = "DOR";
            workflowId = wfBO.retrieveWorkflowTempIdAndNm().get(ptName);
            Timestamp dueDt = null;
            String rush = "N";
    
            FacilityDAO fd = facilityDAO();
            Facility fp = fd.retrieveFacility(ret.getFacilityId());
    
            WorkFlowManager wfm = new WorkFlowManager();
            
            WorkFlowResponse resp = wfm.submitProcess(workflowId, ret
                    .getApplicationID(), fp.getFpId(), uid, rush,
                    ret.getSubmittedDate(), dueDt, null);
    
            if (resp.hasError() || resp.hasFailed()) {
                String[] errorMsgs = resp.getErrorMessages();
                String[] recomMsgs = resp.getRecommendationMessages();
                throw new DAOException(errorMsgs[0] + " " + recomMsgs[0]);
            }
            ret.setSubmittedDate(new Timestamp(System.currentTimeMillis()));
            modifyDelegationRequest(ret);
            return true;
        } catch (ServiceFactoryException sfe) {
            logger.error("Error creating Delegation WF task",sfe);
            return false;
        } catch (RemoteException re) {
            // cancel workflow
            if (workflowId != null) {
                try {
                    rollBackWorkflow(externalId, workflowId, uid);
                } catch (Exception e3) {
                    logger.error("FAILED attempt to roll back workflow.", e3);
                }
            }
            logger.error("Error creating Delegation WF task",re);
            return false;
        }
    }
    
    /**
     * Create a new row in the appropriate Compliance Attachment table.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Attachment createDelegationAttachment(DelegationRequest request,Attachment attachment, InputStream fileStream)
            throws DAOException, ValidationException {
        Transaction trans = TransactionFactory.createTransaction();

        DelegationRequestDAO delegateDAO = delegationRequestDAO(trans);
        
        try {
            attachment = (Attachment)documentDAO(trans).createDocument(attachment);
            delegateDAO.createDelegationAttachment(request,attachment);
            DocumentUtil.createDocument(attachment.getPath(), fileStream);
            trans.complete();
        } catch (DAOException e) {
            cancelTransaction(trans, e);
        } catch (IOException ioe) {
            try {
                DocumentUtil.removeDocument(attachment.getPath());
            } catch (IOException ioex) {
            }
            cancelTransaction(trans, new RemoteException(ioe.getMessage()));
        } finally { // Clean up our transaction stuff
            closeTransaction(trans);
        }

        return attachment;
    } 
    
    /**
     * @param attachment
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */

    public boolean modifyDelegationAttachment(DelegationRequest request,Attachment attachment)
            throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        boolean ret = true;

        DelegationRequestDAO delegateDAO = delegationRequestDAO(trans);
        DocumentDAO documentDAO = documentDAO(trans);
        try {
            if (attachment.getLastModifiedTS() == null) {
                attachment
                        .setLastModifiedTS(new Timestamp(System
                                .currentTimeMillis()));
            }
            attachment.setUploadDate(attachment.getLastModifiedTS());
            documentDAO.modifyDocument(attachment);
            delegateDAO.modifyDelegationAttachment(request,attachment);
            trans.complete();
        } catch (DAOException e) {
            ret=false;
            cancelTransaction(trans, e);
        } finally { // Clean up our transaction stuff
            closeTransaction(trans);
        }
        return ret;
    }
    
    /**
     * Delete row in the DC_CORRESPONDENCE table.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    
    public boolean deleteDelegationAttachment(DelegationRequest request, Attachment attachment) throws DAOException {

        Transaction trans = TransactionFactory.createTransaction();
        
        try {
            DelegationRequestDAO delegateDAO = delegationRequestDAO(trans);
            delegateDAO.deleteDelegationAttachment(request, attachment,true);
            trans.complete();
    } catch (DAOException de) {
        cancelTransaction(trans, de);
    } finally {
        closeTransaction(trans);
    }
    
    return true;
    } 
    
    public void deleteDelegationAttachments(DelegationRequest request,Transaction trans) throws DAOException {
        Attachment attachement[] = request.getAttachments();
        for (int i=0;i<attachement.length;i++) {
            DelegationRequestDAO delegationDAO = delegationRequestDAO(trans);
            delegationDAO.deleteDelegationAttachment(request, attachement[i],true);
        }
} 
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.bo.DelegationRequestService#validateDelegationRequest(
	 * us.oh.state.epa.stars2.database.dbObjects.application.DelegationRequest)
	 */
	@Override
	public ValidationMessage[] validateDelegationRequest(DelegationRequest delegationRequest)
			throws DAOException {
		if (delegationRequest == null) {
			DAOException e = new DAOException("invalid delegationRequest");
			throw e;
		}
		ValidationMessage[] delRequestValMsgs = delegationRequest.validateRecievedDt();
		return delRequestValMsgs;
	}
}
