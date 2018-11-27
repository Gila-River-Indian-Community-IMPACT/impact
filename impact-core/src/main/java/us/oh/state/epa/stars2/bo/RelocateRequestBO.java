package us.oh.state.epa.stars2.bo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.ApplicationDAO;
import us.oh.state.epa.stars2.database.dao.DocumentDAO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.PermitDAO;
import us.oh.state.epa.stars2.database.dao.RelocateRequestDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocationAddtlAddr;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TmpDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitInfo;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.RelocationAttachmentTypeDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.ValidationException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.pdf.application.ApplicationPdfGenerator;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

import com.lowagie.text.DocumentException;

@Transactional(rollbackFor=Exception.class)
@Service
public class RelocateRequestBO extends BaseBO implements RelocateRequestService {

    private PermitService permitBO = null;

    private RelocateRequestDAO getRelocateRequestDAO(boolean staging) throws DAOException {
    	String schema = null;
        if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
        	if (staging) {
        		schema = "Staging";
        	} else {
        		schema = "ReadOnly";
        	}
        }

        return relocateRequestDAO(schema);
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public RelocateRequest createRelocateRequest(RelocateRequest relocateRequest)
            throws DAOException, ValidationException {
        Transaction trans = TransactionFactory.createTransaction();
        RelocateRequest ret = null;
        
        try {
            ApplicationDAO appDAO = applicationDAO(trans);
            //first create the application record and get the application ID
            logger.error("about to create application");
            relocateRequest.setApplicationNumber(appDAO.generateApplicationNumber(RelocateRequest.class));
            relocateRequest.setApplicationID(appDAO.createApplication(relocateRequest).getApplicationID());
            logger.error("app created ("+relocateRequest.getApplicationID()+"). Ccreating relocate record");
            //second, create the relocate record
            ret = createRelocateRequest(relocateRequest, trans);
            logger.error("relocate record created - committing");
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
     * Create a new row in the appropriate Compliance Attachment table.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Attachment createRelocationAttachment(RelocateRequest request,Attachment attachment, InputStream fileStream)
            throws DAOException, ValidationException {
        Transaction trans = TransactionFactory.createTransaction();
        RelocateRequestDAO relocateDAO = relocateRequestDAO(trans);
        
        try {
            attachment = (Attachment)documentDAO(trans).createDocument(attachment);
            relocateDAO.createRelocationAttachment(request,attachment);
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
     * Delete row in the DC_CORRESPONDENCE table.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    
    public boolean deleteRelocationAttachment(RelocateRequest request, Attachment attachment) throws DAOException {

        Transaction trans = TransactionFactory.createTransaction();
        try {
            RelocateRequestDAO relocateDAO = relocateRequestDAO(trans);
            relocateDAO.deleteRelocationAttachment(request, attachment,true);
            trans.complete();
    } catch (DAOException de) {
        cancelTransaction(trans, de);
    } finally {
        closeTransaction(trans);
    }
    return true;
    } 
    
    public void deleteRelocationAttachments(RelocateRequest request,Transaction trans, boolean deleteAttachments) throws DAOException {
            Attachment attachment[] = request.getAttachments();
            try {
            for (int i=0;i<attachment.length;i++) {
                logger.error("trying to delete " + attachment[i].getDescription() );
                RelocateRequestDAO relocateDAO = relocateRequestDAO(trans);
                relocateDAO.deleteRelocationAttachment(request, attachment[i],deleteAttachments);
            }
            } catch (Exception e) {
                logger.error("Error deleting attachment",e);
            }
    } 
    
    /**
     * @param attachment
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */

    public boolean modifyRelocationAttachment(RelocateRequest request,Attachment attachment)
            throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        boolean ret = true;
        RelocateRequestDAO relocateDAO = relocateRequestDAO(trans);
        DocumentDAO documentDAO = documentDAO(trans);
        try {
            if (attachment.getLastModifiedTS() == null) {
                attachment
                        .setLastModifiedTS(new Timestamp(System
                                .currentTimeMillis()));
            }
            attachment.setUploadDate(attachment.getLastModifiedTS());
            documentDAO.modifyDocument(attachment);
            relocateDAO.modifyRelocationAttachment(request,attachment);
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
     * @param relocateRequest
     * @param trans
     * @return
     * @throws DAOException
     */
    private RelocateRequest createRelocateRequest(RelocateRequest relocateRequest,
            Transaction trans) throws DAOException {
        RelocateRequest ret = null;
        RelocateRequestDAO relocateDAO = relocateRequestDAO(trans);
        try {  
            RelocateRequest tempRelocateRequest = relocateDAO.createRelocateRequest(relocateRequest);
            String path = File.separator + "Facilities" + File.separator + relocateRequest.getFacilityId() 
            	+ File.separator + "Applications" + File.separator + relocateRequest.getApplicationID();	
            DocumentUtil.mkDir(path);
            if (tempRelocateRequest != null) {
                ret = tempRelocateRequest;
            } else {
                logger.error("Failed to insert Relocation Request");
                throw new DAOException("Failed to create Relocation Request");
            }
            
        } catch (DAOException e) { // Throw it all away if we have an Exception
            logger.error("Unable to add row: " + e,e);
            throw e;
        } catch (IOException iox) {
            logger.error("Unable to create folder for Relocation attachments",iox);
        }
        return ret;
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public RelocateRequest modifyRelocateRequest(RelocateRequest relocateRequest)
            throws DAOException, ValidationException {
        Transaction trans = TransactionFactory.createTransaction();
        RelocateRequest ret = null;
        try {
            ret = modifyRelocateRequest(relocateRequest, trans);
            trans.complete();
        } catch (RemoteException re) {
            cancelTransaction(trans, re);
        } finally { // Clean up our transaction stuff
            closeTransaction(trans);
        }
        return ret;
    } 

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public boolean submit(RelocateRequest ret, boolean internalSubmit) {
        logger.error("initiating workflow with Relocate ID #"+ret.getRequestId());
        Integer workflowId = null;
        Integer externalId = ret.getRequestId();
        try {
            ReadWorkFlowService wfBO = ServiceFactory.getInstance().getReadWorkFlowService();
    
            String ptName = "ITR";
            workflowId = wfBO.retrieveWorkflowTempIdAndNm().get(ptName);
            Timestamp dueDt = null;
            String rush = "N";
    
            FacilityDAO fd = facilityDAO();
            Facility fp = fd.retrieveFacility(ret.getFacilityId());
    
            WorkFlowManager wfm = new WorkFlowManager();
            WorkFlowResponse resp = wfm.submitProcess(workflowId, ret
                    .getRequestId(), fp.getFpId(), ret.getUserId(), rush,
                    ret.getSubmittedDate(), dueDt, null);
    
            if (resp.hasError() || resp.hasFailed()) {
                String[] errorMsgs = resp.getErrorMessages();
                String[] recomMsgs = resp.getRecommendationMessages();
                throw new DAOException(errorMsgs[0] + " " + recomMsgs[0]);
            }
            if (internalSubmit) {
            	logger.error("setting submitted date/time");
            	ret.setSubmittedDate(new Timestamp(System.currentTimeMillis()));
            	logger.error(ret.getSubmittedDate());
            	modifyRelocateRequest(ret);
            }
            return true;
        } catch (ServiceFactoryException sfe) {
            logger.error("Error creating Relocate WF task",sfe);
            return false;
        } catch (RemoteException re) {
            // cancel workflow
            if (workflowId != null) {
                try {
                    rollBackWorkflow(externalId, workflowId, ret.getUserId());
                } catch (Exception e3) {
                    logger.error("FAILED attempt to roll back workflow.", e3);
                }
            }
            logger.error("Error creating Relocate WF task",re);
            return false;
        }
    }
    
    /**
     * @param relocateRequest
     * @param trans
     * @return
     * @throws DAOException
     */
    private RelocateRequest modifyRelocateRequest(RelocateRequest relocateRequest,
            Transaction trans) throws DAOException {
        RelocateRequest ret = null;
        RelocateRequestDAO relocateDAO = relocateRequestDAO(trans);
        ApplicationDAO appDAO = applicationDAO(trans);
        try {  
        	relocateDAO.deleteRelocationAddtlAddrs(relocateRequest.getRequestId());
        	for (RelocationAddtlAddr addr : relocateRequest.getAdditionalAddresses()) {
        		addr.setAddtlAddrId(relocateDAO.getMaxAddtlAddrId(relocateRequest.getRequestId()));
        		relocateDAO.createRelocationAddtlAddr(addr);
        	}
            RelocateRequest tempRelocateRequest = relocateDAO.modifyRelocateRequest(relocateRequest);
            appDAO.modifyApplication(relocateRequest);
            if (tempRelocateRequest != null) {
                ret = tempRelocateRequest;
            } else {
                logger.error("NULL DAO response. Failed to modify Relocation Request");
                throw new DAOException("Failed to modify Relocation Request");
            }
            
        } catch (DAOException e) { // Throw it all away if we have an Exception
            logger.warn(e);
            throw e;
        }
        return ret;
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public boolean deleteRelocateRequest(RelocateRequest relocateRequest, boolean deleteAttachments)
            throws DAOException, ValidationException {
        Transaction trans = TransactionFactory.createTransaction();
        int appId = relocateRequest.getApplicationID();
        try {
            deleteRelocateRequest(relocateRequest, trans, deleteAttachments);
            ApplicationDAO appDAO = applicationDAO(trans);
            if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
            	appDAO.removeApplicationNotes(appId);
            }
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
     * @param relocateRequest
     * @param trans
     * @return
     * @throws DAOException
     */
    private void deleteRelocateRequest(RelocateRequest relocateRequest,
            Transaction trans, boolean deleteAttachments) throws DAOException {
        RelocateRequestDAO relocateDAO = relocateRequestDAO(trans);
        try {  
            logger.debug("deleting rr record... for "+relocateRequest.getRequestId());
            relocateDAO.deleteRelocationAddtlAddrs(relocateRequest.getRequestId());
            deleteRelocationAttachments(relocateRequest,trans, deleteAttachments);
            RelocateRequest tempRelocateRequest = relocateDAO.deleteRelocateRequest(relocateRequest);
            if (tempRelocateRequest != null) {
                logger.error("Failed to delete Relocation Request");
                throw new DAOException("Failed to delete Relocation Request");
            }
        } catch (DAOException e) { // Throw it all away if we have an Exception
            logger.error("Error deleting RR record",e);
            throw e;
        }
    }
    
    /**
     * Locate all of the relocation requests for a given facility.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public RelocateRequest[] retrieveRelocateRequests(String facilityID)
            throws DAOException {
       // logger.debug("Relocate BO: retreiving requests for facility: " + facilityID);
        return relocateRequestDAO().retrieveRelocateRequests(facilityID);
    }
    
    /**
     * Locate relocation request by id
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public RelocateRequest retrieveRelocateRequest(int requestId, boolean staging)
            throws DAOException {
    	RelocateRequestDAO relocateDAO = getRelocateRequestDAO(staging);
        RelocateRequest request = relocateDAO.retrieveRelocateRequest(requestId);
        if(request != null){
        	// retrieve additional addresses (if any)
        	for (RelocationAddtlAddr addr : relocateDAO.retrieveRelocationAddtlAddrs(requestId)) {
        		request.addAdditionalAddress(addr);
        	}
        	// retrieve attachments and populate the managed bean
             request.setAttachments(relocateDAO.retrieveAttachments(request.getFacilityId(), request.getApplicationID()));
       
        	try {
            	Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
    	        a.setAttachmentList(request.getAttachments());  
        	} catch (Exception e) {
           	logger.error("Error setting attachments into manageed bean",e);
        	}
        }
         return request;
    }
    
    /**
     * Locate relocation request by id
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public RelocateRequest retrieveRelocateRequest(int requestId, String facilityId)
            throws DAOException {
    	Facility facility= new Facility();
    	facility.setFacilityId(facilityId);
    	RelocateRequestDAO relocateDAO = getRelocateRequestDAO(true);
        RelocateRequest request = null;
        if (isInternalApp()) {
        	request = relocateDAO.retrieveRelocateRequest(requestId);
        } else {
        	request = relocateDAO.retrievePortalRelocateRequest(requestId);
        	request.setFacility(facility);
        }
        if(request != null){
        	// retrieve additional addresses (if any)
        	for (RelocationAddtlAddr addr : relocateDAO.retrieveRelocationAddtlAddrs(requestId)) {
        		request.addAdditionalAddress(addr);
        	}
        	// retrieve attachments and populate the managed bean
             request.setAttachments(relocateDAO.retrieveAttachments(facility.getFacilityId(), request.getApplicationID()));
        }
        
        request.setFacility(facility);

        return request;
    }
    
    /**
     * Locate relocation request by id
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public RelocateRequest retrieveRelocateRequestByAppId(int applicationId)
            throws DAOException {
        RelocateRequest request = relocateRequestDAO().retrieveRelocateRequestByAppId(applicationId);
        if(request != null){
//        	retrieve attachments and populate the managed bean
            request.setAttachments(relocateRequestDAO().retrieveAttachments(request.getFacilityId(), request.getApplicationID()));
                

        	try {
         	   Attachments a = (Attachments) FacesUtil
       	     .getManagedBean("attachments");
       	     a.setAttachmentList(request.getAttachments());  
      	 	} catch (Exception e) {
           		logger.error("Error setting attachments into manageed bean",e);
        	}
        }
         return request;
    }
    
    /**
     * Locate relocation request by id
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Permit[] getPermits(String facilityId) throws DAOException {
        PermitDAO permitDAO = permitDAO();
        ArrayList<Permit> activePermits = permitDAO.searchActivePermitsForFacility(facilityId);
        ArrayList<Permit> assocPermits = new ArrayList<Permit>();
        if (activePermits != null && !activePermits.isEmpty()) {
            for (Permit permit : activePermits) {
                if ((permit.getPermitType().equals(PermitTypeDef.NSR)) 
                		//|| (permit.getPermitType().equals(PermitTypeDef.TVPTI))
                		) {
                    //we have a valid permit. Return permit # as one of the permits.
                    try {
                        permitBO = ServiceFactory.getInstance().getPermitService();
                        PermitInfo info = permitBO.retrievePermit(permit.getPermitID());
                        permit.setFinalIssueDate(info.getPermit().getFinalIssueDate());
                        assocPermits.add(permit);
                    } catch (Exception e) {}
                }
            }
        }
        logger.debug("returning " + assocPermits.size());
        return assocPermits.toArray(new Permit[0]); 
    }
    
    /**
     * Create a zip file containing application data and all its related
     * attachments and download its contents.
     * @param app
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Document generateTempITRAttachmentZipFile(RelocateRequest relocateRequest) throws FileNotFoundException, IOException {

        TmpDocument appDoc = new TmpDocument();
        // Set the path elements of the temp doc.
        appDoc.setDescription("ITR attachments zip file");
        appDoc.setFacilityID(relocateRequest.getFacility().getFacilityId());
        appDoc.setTemporary(true);
        appDoc.setTmpFileName(relocateRequest.getApplicationNumber() + "Attachments.zip");

        // make sure temporary directory exists
        DocumentUtil.mkDirs(appDoc.getDirName());
        OutputStream os = DocumentUtil.createDocumentStream(appDoc.getPath());
        ZipOutputStream zos = new ZipOutputStream(os); 
        zipITRAttachments(relocateRequest, zos);
        zos.close(); 
        os.close();

        return appDoc;
    }
    
    private String getNameForAttachment(Attachment doc) {                    
        StringBuffer docName = new StringBuffer();
        //add description here?
        docName.append(doc.getDocumentID());
        if (doc.getExtension() != null && doc.getExtension().length() > 0) {
            docName.append("." + doc.getExtension());
        }
        return docName.toString();
    }
    
    private void zipITRAttachments(RelocateRequest relocateRequest, ZipOutputStream zos)
    		throws FileNotFoundException, IOException {
        DocumentService docBO = null;
        try {
            docBO = ServiceFactory.getInstance().getDocumentService();
        } catch (ServiceFactoryException e) {
            logger.error("Exception accessing DocumentService", e);
            throw new IOException("Exception accessing DocumentService");
        }
        
//      add attachments to zip file
        HashSet<Integer> docIdSet = new HashSet<Integer>();
        Attachment[] a = relocateRequest.getAttachments();
        for (int i=0;i<a.length;i++) {
            if (a[i].getDocumentID() != null && !docIdSet.contains(a[i].getDocumentID())) {
                docIdSet.add(a[i].getDocumentID());
                Document doc = docBO.getDocumentByID(a[i].getDocumentID(), false);
                if (doc != null && !doc.isTemporary()) {
                    InputStream docIS = DocumentUtil.getDocumentAsStream(doc.getPath());
                    addEntryToZip(getNameForAttachment(a[i]), docIS, zos);
                    docIS.close();
                } else if (doc == null){
                    logger.error("No document found with id " +  a[i].getDocumentID());
                }
            }
        }
    }
    
    /**
     * Create a new relocation request submitted from gateway.
     * 
     * @param Facility
     *            Facility inventory to create from gateway.
     *  @param newFpId
     *  		  FP iD to be used when creating a new Facility          
     *  @parm trans
     *  		  transaction
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void  createRelocationRequestFromPortal(RelocateRequest portalRelReq, Transaction trans)
    		throws DAOException {
    	String path = null;
    	try {
    		Attachment attachment;
    		RelocateRequestDAO relocateDAO = relocateRequestDAO(trans);
    		ApplicationDAO appDAO = applicationDAO(trans);
    		FacilityDAO facDAO = facilityDAO(trans);
    		Facility fac = facDAO.retrieveFacility(portalRelReq.getFacility().getFacilityId());
    		portalRelReq.getFacility().setFpId(fac.getFpId());
    		portalRelReq.setReceivedDate(new Timestamp(System.currentTimeMillis()));
    		portalRelReq.setUserId(CommonConst.GATEWAY_USER_ID);
    		appDAO.createApplication(portalRelReq);
    		relocateDAO.createRelocateRequest(portalRelReq);
    		for (Attachment reqAttach : portalRelReq.getAttachments()) {
    			attachment = (Attachment)documentDAO(trans).createDocument(reqAttach);
    			relocateDAO.createRelocationAttachment(portalRelReq, attachment);
    		}
    		for (RelocationAddtlAddr addr : portalRelReq.getAdditionalAddresses()) {
    			addr.setAddtlAddrId(relocateDAO.getMaxAddtlAddrId(portalRelReq.getRequestId()));
    			relocateDAO.createRelocationAddtlAddr(addr);
    		}
    	
    		if (!submit(portalRelReq, false)) {
    			throw new DAOException("Failed to create a workflow for relocate request : " 
    					+ portalRelReq.getRequestId());
    		}
    	} catch (DAOException e) {
        	String error = "create relocate request from gateWay failed: " + e.getMessage();
        	logger.error(e);
        	if (path != null) {
        		try {
        			DocumentUtil.rmDir(path);
        		} catch (IOException ioe) {
        			logger.error("Cannor remove directory path", ioe);
        		}
        	}
        	
        	throw new DAOException(error, e);
        } catch (Exception e) {
        	String error = "create relocate request from gateWay failed: " + e.getMessage();
        	logger.error(error);
        	if (path != null) {
        		try {
        			DocumentUtil.rmDir(path);
        		} catch (IOException ioe) {
        			logger.error("Cannor remove directory path", ioe);
        		}
        	}
        	throw new DAOException(error, e);
    	}
    }
    
    /**
     * Remove ITR attachments directories.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public void  RemoveRelocationRequestDirs(RelocateRequest relocateRequest, String facId, boolean deleteAttach) {
    	String path = null;
//    	try {
    		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
    			/*
    			if (relocateRequest.getApplicationTypeCD().equals(ApplicationTypeDef.INTENT_TO_RELOCATE)) {
    				if (deleteAttach) {
    					path = File.separator+"Facilities" + File.separator + facId + File.separator+"Applications"+File.separator+relocateRequest.getApplicationID();
    				}
    			} 
    			*/
//    			else {
//////    				if (deleteAttach) {
//////    					Facility itrFac = facilityDAO("Staging").retrieveFacility(CommonConst.GATEWAY_ITR_DUMMY_FPID);
//////    					path = File.separator+"Facilities" + File.separator + itrFac.getFacilityId() + File.separator+"Applications"+File.separator+relocateRequest.getApplicationID();
//////    			
//////    				}
//    			}
    			if (path != null) {
    				try {
    					DocumentUtil.rmDir(path);
    				} catch(IOException ioe) {
    					logger.error("Cannot remove directory : " + path, ioe);
    				}
    			}
    		}
//    	} catch (DAOException de) {
//    		logger.error("Cannot remove attachments directory for Relocate request : " + relocateRequest.getRequestId(), de);
//    	}
    }

    
    /**
     * Retrieve preapproved addresses for facility
     * @param facilityId
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public SimpleDef[] retrievePreApprovedAddressesForFacility (String facilityId)
    throws DAOException {
        RelocateRequestDAO relocateDAO = relocateRequestDAO();
        SimpleDef[] addressList = relocateDAO.retrievePreApprovedAddressesForFacility(facilityId);
        return addressList;
    }
    
    /**
     * Add attestation document to application 
     * @param app
     * @param attestationDoc
     * @param trans
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void addAttestationDocument(RelocateRequest relocation, Attachment attestationDoc, Transaction trans) throws DAOException {
        RelocateRequestDAO relocateDAO = relocateRequestDAO(trans);
		relocateDAO.createRelocationAttachment(relocation, attestationDoc);
		Attachment[] attachments = new Attachment[relocation.getAttachments().length + 1];
		int i = 0;
		for (i=0; i<relocation.getAttachments().length; i++) {
			attachments[i] = relocation.getAttachments()[i];
		}
		attachments[i] = attestationDoc;
		relocation.setAttachments(attachments);
		System.out.println(relocation.getAttachments().length);
    }
    
    /**
     * 
     * @param relocation
     * @param trans
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void removeAttestationDocument(RelocateRequest relocation, Transaction trans) throws DAOException {
    	RelocateRequestDAO relocateDAO = relocateRequestDAO(trans);
    	for (Attachment doc : relocation.getAttachments()) {
    		if (RelocationAttachmentTypeDef.RO_SIGNATURE.equals(doc.getDocTypeCd())) {
    			relocateDAO.deleteRelocationAttachment(relocation, doc);
    		}
    	}
    }
    
    /**
     * 
     * @param task
     * @throws DAOException
     * @throws IOException
     * @throws FileNotFoundException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void addSubmissionAttachments(Task task) throws DAOException, IOException, FileNotFoundException {
    	RelocateRequest relocateRequest = task.getRelocateRequest();
		Document zipDoc = null;
		
		// add zip file with application attachments to task for submission
		if (relocateRequest.getAttachments().length > 0) {
			logger.debug("zipping ITR attachments...");
			zipDoc = generateTempITRAttachmentZipFile(relocateRequest);
			logger.debug("Done zipping ITR attachments.");
		}
        
        if (zipDoc != null) {
        	us.oh.state.epa.portal.datasubmit.Attachment submitAttachment;
    		submitAttachment = new us.oh.state.epa.portal.datasubmit.Attachment(relocateRequest.getRequestId().toString(),
    				"text/zip", DocumentUtil.getFileStoreRootPath() + zipDoc.getPath(), null, null);
            submitAttachment.setSystemFilename(DocumentUtil.getFileStoreRootPath() + zipDoc.getPath());
    		task.getAttachments().add(submitAttachment);
        }
        
    }

    
    /**
     * Return pdf file RO can sign as attestation document.
     * @param relocation
     * @return
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Attachment createITRAttestationDocument(RelocateRequest relocation) throws DAOException {
    	Attachment doc = null;
    	try {
    		doc = getITRAttestationDocument(relocation);
    		OutputStream os = DocumentUtil.createDocumentStream(doc.getPath());     
    		ApplicationPdfGenerator generator = ApplicationPdfGenerator.getGeneratorForClass(PTIOApplication.class);
    		generator.setAttestationOnly(true);
    		generator.generatePdf(relocation, os, true, false, true);
    		os.close();
    	} catch (IOException e) {
    		throw new DAOException("Exception getting attestation document as stream", e);
    	} catch (DocumentException e) {
    		throw new DAOException("Exception getting attestation document as stream", e);
		}
    	return doc;
    }
    
    private Attachment getITRAttestationDocument(RelocateRequest relocation) {
    	Attachment doc = new Attachment();
    	doc.setFacilityID(relocation.getFacilityId());
        doc.setObjectId(relocation.getApplicationID().toString());
    	doc.setSubPath("Applications");
    	doc.setExtension("pdf");
        doc.setDocumentID(relocation.getRequestId());	// just a temporary id until the file is uploaded
        doc.setTemporary(true);
        doc.setDescription("Attestation document for " + relocation.getApplicationNumber());
        return doc;
    }
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.bo.DelegationRequestService#validateDelegationRequest(
	 * us.oh.state.epa.stars2.database.dbObjects.application.DelegationRequest)
	 */
	@Override
	public ValidationMessage[] validateRelocateRequest(RelocateRequest relocateRequest)
			throws DAOException {
		if (relocateRequest == null) {
			DAOException e = new DAOException("invalid relocationRequest");
			throw e;
		}
		ValidationMessage[] delRequestValMsgs = relocateRequest.validateRecievedDt();
		return delRequestValMsgs;
	}
}
