package us.oh.state.epa.stars2.bo;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.database.dao.CorrespondenceDAO;
import us.oh.state.epa.stars2.database.dao.DocumentDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dao.WorkFlowDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.ValidationException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

/** Business object for store and load of document correspondence. */
@Transactional(rollbackFor=Exception.class)
@Service
public class CorrespondenceBO extends BaseBO implements CorrespondenceService {

    /**
     * Locate all of the correspondence for a given facility.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Correspondence[] searchCorrespondenceByFacility(String facilityID)
        throws DAOException {
        return correspondenceDAO().searchCorrespondenceByFacility(facilityID);

    }
    
    /**
     * Locate all of the correspondence for a given facility.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Correspondence[] searchCorrespondenceByLinkedToId(Integer linkedToId)
        throws DAOException {
        return correspondenceDAO().searchCorrespondenceByLinkedToId(linkedToId);

    }

    /**
     * Create a new row in the DC_CORRESPONDENCE table.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Correspondence createCorrespondence(Correspondence correspondence)
        throws DAOException, ValidationException {

        Correspondence ret = null;
        Transaction trans = TransactionFactory.createTransaction();

        try {
            ret = createCorrespondence(correspondence, trans);
            trans.complete();
        }
        catch (ValidationException ve) {
            cancelTransaction(trans, new DAOException(ve.getMessage(), ve));
        }
        catch (DAOException de) {
            cancelTransaction(trans, de);
        }
        finally {
            closeTransaction(trans);
        }

        return ret;
    }

    /**
     * Create a new row in the DC_CORRESPONDENCE table.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Correspondence createCorrespondence(Correspondence correspondence, Transaction trans)
        throws DAOException, ValidationException {

        // Check internal validation of all of the pieces and parts.
        ArrayList<ValidationMessage> validMsgs = new ArrayList<ValidationMessage>();
        boolean failsBORules = false;
        if (!correspondence.isValid()) {
            validMsgs.addAll(correspondence.getValidationMessages().values());
            failsBORules = true;
        }

        for (Note note : correspondence.getNotes()) {
            if (!note.isValid()) {
                validMsgs.addAll(note.getValidationMessages().values());
                failsBORules = true;
            }
        }

        if (failsBORules) {
            for (ValidationMessage vm : validMsgs) {
                logger.error("Correspondence error: " + vm.getMessage());
            }
            throw new ValidationException("Cannot create new correspondence. Correspondence is not valid",
                                          null, validMsgs.toArray(new ValidationMessage[0]));
        }

        // Objects are valid so go ahead and try to create in db.
        CorrespondenceDAO cDao = correspondenceDAO(trans);
        InfrastructureDAO iDao = infrastructureDAO(trans);
        Correspondence ret = null;

        ret = cDao.createCorrespondence(correspondence);
        ret.setDirty(false);
        for (Note note : correspondence.getNotes()) {
            note = iDao.createNote(note);
            cDao.createNoteXref(ret.getCorrespondenceID(),
                                note.getNoteId());
        }

        return ret;

    } // END: public Correspondence createCorrespondence(Correspondence correspondence)

    /**
     * Update a row in the DC_CORRESPONDENCE table.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void updateCorrespondence(Correspondence correspondence)
        throws DAOException, ValidationException {

        // Check internal validation of all of the pieces and parts.
        ArrayList<ValidationMessage> validMsgs = new ArrayList<ValidationMessage>();
        boolean failsBORules = false;

        if (correspondence.getCorrespondenceID() == null) {
            ValidationMessage vm = new ValidationMessage("correspondenceID",
                                                         "CorrespondenceID must be set for update.");
            validMsgs.add(vm);
            failsBORules = true;
        }
        if (!correspondence.isValid()) {
            validMsgs.addAll(correspondence.getValidationMessages().values());
            failsBORules = true;
        }

        for (Note note : correspondence.getNotes()) {
            if (!note.isValid()) {
                validMsgs.addAll(note.getValidationMessages().values());
                failsBORules = true;
            }
        }

        if (failsBORules) {
            throw new ValidationException("Cannot update correspondence. Correspondence is not valid",
                                          null, validMsgs.toArray(new ValidationMessage[0]));
        }

        // Objects are valid so go ahead and try to update in db.
        Transaction trans = TransactionFactory.createTransaction();
        CorrespondenceDAO cDao = correspondenceDAO(trans);
        InfrastructureDAO iDao = infrastructureDAO(trans);

        try {
            cDao.updateCorrespondence(correspondence);
            correspondence.setDirty(false);
            for (Note note : correspondence.getNotes()) {
                if (note.getNoteId() != null) {
                    iDao.modifyNote(note);
                }
                else {
                    note = iDao.createNote(note);
                    cDao.createNoteXref(correspondence.getCorrespondenceID(),
                                        note.getNoteId());
                }
            }
            trans.complete();
        }
        catch (DAOException de) {
            cancelTransaction(trans, de);
        }
        finally {
            closeTransaction(trans);
        }

        return;

    } // END: public void updateCorrespondence(Correspondence correspondence)

    /**
     * Delete row in the DC_CORRESPONDENCE table.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void deleteCorrespondence(Correspondence correspondence)
        throws DAOException {

        Transaction trans = TransactionFactory.createTransaction();
        CorrespondenceDAO cDao = correspondenceDAO(trans);
        InfrastructureDAO iDao = infrastructureDAO(trans);

        try {
        	if(correspondence.getDocument() != null) {
                //  delete existing document
                try {
                    DocumentDAO documentDAO = documentDAO(trans);
                    correspondence.getDocument().setTemporary(true);
                    documentDAO.modifyDocument(correspondence.getDocument());
                } catch(IOException e){
                    logger.error("Exception while deleting document " +
                            " for correspondence " + correspondence.getCorrespondenceID());
                }
            }
            for (Note note : correspondence.getNotes()) {
                cDao.deleteNoteXref(correspondence.getCorrespondenceID(),
                                    note.getNoteId());
                iDao.removeNote(note.getNoteId());
            }
            // delete correspondence attachments
            for(Attachment attachment : retrieveCorrespondenceAttachments(correspondence.getCorrespondenceID())) {
            	cDao.removeCorrespondenceAttachment(attachment);
            }
            cDao.deleteCorrespondence(correspondence);
            trans.complete();
        }
        catch (DAOException de) {
            cancelTransaction(trans, de);
        }
        finally {
            closeTransaction(trans);
        }

        return;

    } // END: public void deleteCorrespondence(Correspondence correspondence)

    /**
     * @param correspondence
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Correspondence[] searchCorrespondence(Correspondence correspondence, String dateField,
            Timestamp startDate, Timestamp endDate)
        throws DAOException {
    	Timestamp incEndDate = null;
    	
    	if(endDate != null) {
	    	Calendar calEndDate = Calendar.getInstance();
	    	calEndDate.setTimeInMillis(endDate.getTime());
	    	calEndDate.set(Calendar.HOUR_OF_DAY, 23);
	    	calEndDate.set(Calendar.MINUTE, 59);
	    	calEndDate.set(Calendar.SECOND, 59);
	    	calEndDate.set(Calendar.MILLISECOND, 999);
	    	incEndDate = new Timestamp(calEndDate.getTimeInMillis());
    	}
    	
        return correspondenceDAO().searchCorrespondenceByDate(correspondence, dateField, startDate, incEndDate);
    }

    /**
     * @param correspondence
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
 
    public boolean modifyCorrespondence(Correspondence correspondence)
        throws DAOException { 
        Transaction trans = null;
        boolean rtn = false;
        try{
            trans = TransactionFactory.createTransaction();

            rtn = correspondenceDAO(trans).modifyCorrespondence(correspondence);
            
            trans.complete();
        } catch(DAOException de){
            cancelTransaction(trans, de);
        } finally{          
            closeTransaction(trans);
        }
        return rtn;
    }

    /**
     * @param correspondenceId
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Correspondence retrieveCorrespondence(int correspondenceId)
        throws DAOException {
    	Correspondence ret = correspondenceDAO().retrieveCorrespondence(correspondenceId);
        List<String> inspections = fullComplianceEvalDAO().retrieveInspectionIdsForCorrespondenceId(correspondenceId);
        ret.setInspectionsReferencedIn(inspections);
        
        return ret;
    }

    /**
     * @param
     * 
     * @return
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
	@Override
	public void createFollowUpActionNotices() throws DAOException,
			RemoteException {
		List<Correspondence> corrList = null;
		ReadWorkFlowService wfBO = null;
		try {
			CorrespondenceDAO corrDAO = correspondenceDAO();
			corrList = corrDAO.retrievePotentialFollowUpCorrespondence();
			wfBO = ServiceFactory.getInstance().getReadWorkFlowService();
		} catch (DAOException de) {
			logger.error(
					"retrievePotentialFollowUpCorrespondence() failed with "
							+ de.getMessage(), de);
			throw de;
		} catch (ServiceFactoryException sfe) {
			logger.error(
					"retrievePotentialFollowUpCorrespondence() failed with "
							+ sfe.getMessage(), sfe);
			throw new DAOException(
					"retrievePotentialFollowUpCorrespondence() failed with "
							+ sfe.getMessage(), sfe);
		}

		// Create workflows
		String ptName = "Correspondence Follow Up";
		Integer workflowId = wfBO.retrieveWorkflowTempIdAndNm().get(ptName);
		if (workflowId == null) {
			String s = "Failed to find workflow for \"" + ptName;
			logger.error(s);
			throw new DAOException(s);
		}

		String rush = "N";
		WorkFlowManager wfm = new WorkFlowManager();
		int cnt = 400;
		for (Correspondence corr : corrList) {
			cnt--;
			if (cnt < 0) {
				break; // limit number of workflows created at a time.
			}

			// determine if workflow already exists for correspondence
			WorkFlowProcess[] processes;
			try {
				final Integer templateId = workflowId;
				WorkFlowProcess wfProcess = new WorkFlowProcess();
				wfProcess.setProcessTemplateId(templateId);
				wfProcess.setExternalId(corr.getCorrespondenceID());
				wfProcess.setCurrent(true);
				wfProcess.setUnlimitedResults(true);
				processes = wfBO.retrieveProcessList(wfProcess);
			} catch (RemoteException e) {
				String s = "Caught RemoteException checking for exisiting workflow for Correspondence "
						+ corr.getCorrespondenceID();
				logger.error(s, e);
				throw new DAOException(s, new Exception());
			}

			if (processes.length > 0) {
				// workflow already exists for correspondence
				continue;
			}

			Integer userId = corr.getReviewerId();

			Timestamp startDt = corr.getFollowUpActionDate();

			Calendar cal = Calendar.getInstance();
			Date date = new Date(startDt.getTime());
			cal.setTime(date);
			cal.add(Calendar.MONTH, 3);
			cal.add(Calendar.DAY_OF_YEAR, -1);
			Timestamp dueDt = new Timestamp(cal.getTimeInMillis());
			WorkFlowResponse resp = wfm.submitProcess(workflowId,
					corr.getCorrespondenceID(), corr.getFpId(), userId, rush,
					startDt, dueDt, null);

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
				String s = "Error encountered trying to create workflow for Correspondence Follow Up "
						+ corr.getCorrespondenceID() + ": " + errMsg.toString();
				logger.error(s);
				throw new DAOException(s, new Exception());
			}

			// find process activity and set user ID
			WorkFlowProcess[] corrProcesses;
			try {
				final Integer templateId = workflowId;
				WorkFlowProcess wfProcess = new WorkFlowProcess();
				wfProcess.setProcessTemplateId(templateId);
				wfProcess.setExternalId(corr.getCorrespondenceID());
				wfProcess.setCurrent(true);
				wfProcess.setUnlimitedResults(true);
				// Find the workflow process for this report
				corrProcesses = wfBO.retrieveProcessList(wfProcess);
			} catch (RemoteException e) {
				String s = "Caught RemoteException checking for exisiting workflow for Correspondence "
						+ corr.getCorrespondenceID();
				logger.error(s, e);
				throw new DAOException(s, new Exception());
			}

			if (corrProcesses.length == 0) {
				// no workflow found
				String s = "No workflow for Correspondence "
						+ corr.getCorrespondenceID();
				logger.error(s);
				throw new DAOException(s, new Exception());
			}
			if (corrProcesses.length > 1) {
				// more than one workflow found
				String s = "More than one workflow for Correspondence "
						+ corr.getCorrespondenceID();
				logger.error(s);
				throw new DAOException(s, new Exception());
			}

			WorkFlowDAO workFlowDAO = workFlowDAO();
			ProcessActivity[] pa = workFlowDAO
					.retrieveProcessActivities(corrProcesses[0].getProcessId());
			if (pa.length == 0) {
				// no stop found
				String s = "No step for Correspondence "
						+ corr.getCorrespondenceID() + " and process "
						+ corrProcesses[0].getProcessId();
				logger.error(s);
				throw new DAOException(s, new Exception());
			}
			if (pa.length > 1) {
				// more than one step found
				String s = "More than one step found for Correspondence "
						+ corr.getCorrespondenceID() + " and process "
						+ corrProcesses[0].getProcessId();
				logger.error(s);
				throw new DAOException(s, new Exception());
			}
			Integer user = corr.getReviewerId();
			pa[0].setUserId(user);
			workFlowDAO.modifyProcessActivity(pa[0]);
		}
	}
    
	@Override
	public boolean doWorkflowsExist(Integer corrId) throws RemoteException {
		boolean ret = false;
		
		ReadWorkFlowService wfBO = null;
		try {
			wfBO = ServiceFactory.getInstance().getReadWorkFlowService();
		} catch (ServiceFactoryException sfe) {
			logger.error(
					"retrievePotentialFollowUpCorrespondence() failed with "
							+ sfe.getMessage(), sfe);
			throw new DAOException(
					"retrievePotentialFollowUpCorrespondence() failed with "
							+ sfe.getMessage(), sfe);
		}

		// Create workflows
		String ptName = "Correspondence Follow Up";
		Integer workflowId = wfBO.retrieveWorkflowTempIdAndNm().get(ptName);
		if (workflowId == null) {
			String s = "Failed to find workflow for \"" + ptName;
			logger.error(s);
			throw new DAOException(s);
		}
		
		WorkFlowProcess[] processes;
		try {
			final Integer templateId = workflowId;
			WorkFlowProcess wfProcess = new WorkFlowProcess();
			wfProcess.setProcessTemplateId(templateId);
			wfProcess.setExternalId(corrId);
			wfProcess.setCurrent(true);
			wfProcess.setUnlimitedResults(true);
			processes = wfBO.retrieveProcessList(wfProcess);
		} catch (RemoteException e) {
			String s = "Caught RemoteException checking for exisiting workflow for Correspondence "
					+ corrId;
			logger.error(s, e);
			throw new DAOException(s, new Exception());
		}

		if (processes.length > 0) {
			ret = true;
		}
		
		return ret;
	}
	
	public Attachment createCorrespondenceAttachment(Integer correspondenceId, Attachment correspondenceAttachment)
			throws DAOException, RemoteException {
			CorrespondenceDAO correspondenceDAO = correspondenceDAO();

			try{
				
				correspondenceAttachment = correspondenceDAO
						.createCorrespondenceAttachment(correspondenceId, correspondenceAttachment);
			} catch (DAOException e) {
				
			} catch (IOException ioe) {
				try {
					DocumentUtil.removeDocument(correspondenceAttachment.getPath());
				} catch (IOException ioex) {
				}
				
			} finally { // Clean up our transaction stuff
				
			}

			return correspondenceAttachment;
	}
	
	public Attachment createCorrespondenceAttachment(Integer correspondenceId, Attachment correspondenceAttachment, InputStream fileStream)
			throws DAOException, RemoteException {
			Transaction trans = TransactionFactory.createTransaction();
			CorrespondenceDAO correspondenceDAO = correspondenceDAO(trans);

			try{
				correspondenceAttachment = (Attachment) documentDAO().createDocument(
						correspondenceAttachment);
				correspondenceAttachment = correspondenceDAO
						.createCorrespondenceAttachment(correspondenceId, correspondenceAttachment);
				DocumentUtil.createDocument(correspondenceAttachment.getPath(),
						fileStream);
				trans.complete();
			} catch (DAOException e) {
				cancelTransaction(trans, e);
			} catch (IOException ioe) {
				try {
					DocumentUtil.removeDocument(correspondenceAttachment.getPath());
				} catch (IOException ioex) {
				}
				cancelTransaction(trans, new RemoteException(ioe.getMessage(), ioe));
			} finally { // Clean up our transaction stuff
				closeTransaction(trans);
			}

			return correspondenceAttachment;
	}
	
	public Attachment[] retrieveCorrespondenceAttachments(Integer correspondenceId) throws DAOException {
		Attachment[] ret = null;

		try {
			ret = correspondenceDAO().retrieveCorrespondenceAttachments(correspondenceId);
		} catch (DAOException de) {
			logger.error("retrieve correspondence attachments failed for "
					+ correspondenceId + ". " + de.getMessage(), de);
		}

		return ret;
	}
	
	public Attachment updateCorrespondenceAttachment(Attachment correspondenceAttachment) throws DAOException {
		Attachment ret = null;
		Transaction trans = TransactionFactory.createTransaction();
		CorrespondenceDAO correspondenceDAO = correspondenceDAO(trans);
		DocumentDAO documentDAO = documentDAO(trans);
		try {
			correspondenceAttachment.setLastModifiedTS(new Timestamp(System
						.currentTimeMillis()));
			if(correspondenceAttachment.getUploadDate() == null) {
				correspondenceAttachment.setUploadDate(correspondenceAttachment
					.getLastModifiedTS());
			}
			documentDAO.modifyDocument(correspondenceAttachment);
			ret = correspondenceDAO.updateCorrespondenceAttachment(correspondenceAttachment);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}
	
	public void removeCorrespondenceAttachment(Attachment correspondenceAttachment) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		CorrespondenceDAO correspondenceDAO = correspondenceDAO(trans);

		try {
			correspondenceDAO.removeCorrespondenceAttachment(correspondenceAttachment);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}
	
	/**
	 * Creates a correspondence along with an attachment
	 * 
	 * @param Correspondence - correspondence
	 * @param Attachment - correspondence attachment
	 * @param InputStream - inputstream for the correspondence attachment
	 * 
	 * @returns none
	 * @throws DAOException
	 * @throws IOException
	 */
	
	public void createCorrespondenceWithAttachment(Correspondence correspondence, Attachment attachment, InputStream is)
					throws DAOException {
		Attachment correspondenceAttachment = null;
		Correspondence corr = null;
		CorrespondenceDAO correspondenceDAO = correspondenceDAO();
		DocumentDAO documentDAO = documentDAO();
		try {
			corr = correspondenceDAO.createCorrespondence(correspondence);
			correspondenceAttachment = (Attachment)documentDAO.createDocument(attachment);
			correspondenceAttachment = correspondenceDAO.createCorrespondenceAttachment(corr.getCorrespondenceID(), correspondenceAttachment);
			DocumentUtil.createDocument(correspondenceAttachment.getPath(), is);
		}catch(IOException ioe) {
			logger.error("An exception occured while creating the document in the filestore " + ioe.getMessage());
			throw new DAOException(ioe.getMessage(), ioe);
		}
	}
}


