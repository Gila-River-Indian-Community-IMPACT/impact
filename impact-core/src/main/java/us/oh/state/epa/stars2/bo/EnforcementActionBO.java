package us.oh.state.epa.stars2.bo;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.database.dao.DocumentDAO;
import us.oh.state.epa.stars2.database.dao.EnforcementActionDAO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementActionEvent;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAttachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementNote;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.EnforcementActionEventDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.ValidationException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

@Transactional(rollbackFor=Exception.class)
@Service
public class EnforcementActionBO extends BaseBO implements EnforcementActionService {
	
	/**
	 * 
	 * @param enforcementAction
	 * @throws DAOException
	 * 
	 */
	public EnforcementAction createEnforcementAction(
			EnforcementAction enforcementAction) throws DAOException,
			RemoteException, ServiceFactoryException {

		EnforcementActionDAO enforcementActionDAO = enforcementActionDAO();

		try {
			enforcementAction = enforcementActionDAO
					.createEnforcementAction(enforcementAction);

			for (String referralTypeCd : enforcementAction.getReferralTypes()) {
				createReferalType(referralTypeCd, enforcementAction.getEnforcementActionId());
			}	
		
			if (!enforcementAction.isLegacyFlag()) {
				Integer assignedUserId = null;

				Facility fp = null;

				FacilityDAO fd = facilityDAO();
				fp = fd.retrieveFacility(enforcementAction.getFacilityId());

				//Create workflows and todos for two facility roles:
				if (fp != null) {
					Integer roleUserId = null;
					
					// get the associated District reviewer id so that the
					// workflow task can be assigned to the correct internal user
					roleUserId = fp
							.getFacilityRoles()
							.get(us.oh.state.epa.stars2.def.FacilityRoleDef.ENFORCEMENT_REVIEWER_DISTRICT)
							.getUserId();
					assignedUserId = roleUserId;
					// create workflow for enforcement action - District
					// Reviewer
					createWorkflow(enforcementAction, null, assignedUserId,
							enforcementAction.getCreatorId(), new Timestamp(
									enforcementAction.getCreatedDate()
											.getTime()));
					
					// get the associated Cheyenne reviewer id so that the
					// workflow task can be assigned to the correct internal user
					roleUserId = fp
							.getFacilityRoles()
							.get(us.oh.state.epa.stars2.def.FacilityRoleDef.ENFORCEMENT_REVIEWER_CHEYENNE)
							.getUserId();
					assignedUserId = roleUserId;
					// create workflow for enforcement action - Cheyenne
					// Reviewer
					createWorkflow(enforcementAction, null, assignedUserId,
							enforcementAction.getCreatorId(), new Timestamp(
									enforcementAction.getCreatedDate()
											.getTime()));
				}

			}

		} catch (DAOException de) {
			DisplayUtil.displayError("Failed to create enforcement action");
			logger.error(de.getMessage(), de);
			throw de;
		} catch (RemoteException re) {
			DisplayUtil.displayError("Failed to create enforcement action");
			logger.error(re.getMessage(), re);
			throw re;
		} catch (ServiceFactoryException se) {
			DisplayUtil.displayError("Failed to create enforcement action");
			logger.error(se.getMessage(), se);
			throw se;
		}

		return enforcementAction;
	}
	
	/**
	 * 
	 * @param action
	 * @throws DAOException
	 * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
	 */
	public void deleteEnforcementAction(EnforcementAction action)
			throws DAOException {
		try {
			InfrastructureDAO infraDAO = infrastructureDAO();

			EnforcementActionDAO enforcementActionDAO = enforcementActionDAO();
			
			deleteReferralTypeById(action.getEnforcementActionId());
			
			enforcementActionDAO.removeEnforcementActionEventList(action.getEnforcementActionId());

			for (EnforcementAttachment ea : action.getAttachments()) {
				removeEnforcementAttachment(ea);
			}
			enforcementActionDAO.deleteEnforcementNotes(action
					.getEnforcementActionId());
			for (Note eaNote : action.getNotes()) {
				infraDAO.removeNote(eaNote.getNoteId());
			}

			enforcementActionDAO.deleteEnforcementAction(action);

		} catch (DAOException e) {
			DisplayUtil.displayError("Failed to delete enforcement action");
			logger.error(e.getMessage(), e);
			throw e;

		} catch (RemoteException e) {
			DisplayUtil.displayError("Failed to delete enforcement action");
			logger.error(e.getMessage(), e);
			throw e;
			
		} finally {

		}
	}

	/**
	 * 
	 * @param enforcementAction
	 * @throws DAOException
	 * 
	 */
	public boolean modifyEnforcementAction(EnforcementAction enforcementAction)
			throws DAOException {

		boolean ret = false;
		EnforcementActionDAO enforcementActionDAO = enforcementActionDAO();

		try {
			ret = enforcementActionDAO
					.modifyEnforcementAction(enforcementAction);
			
			if (ret){
				deleteReferralTypeById(enforcementAction.getEnforcementActionId());
				for (String referralTypeCd : enforcementAction.getReferralTypes()) {
					createReferalType(referralTypeCd, enforcementAction.getEnforcementActionId());
				}
			}
			
		} catch (DAOException de) {
			DisplayUtil.displayError("Failed to update enforcement action");
			logger.error(de.getMessage(), de);
			throw de;
		}
		return ret;
	}

	
	/**
	 * 
	 * @param enforcementActionId
	 * @throws DAOException
	 * 
	 */
	public EnforcementAction retrieveEnforcementAction(Integer enforcementActionId)
			throws DAOException {
		
		EnforcementAction ret = null;
		EnforcementActionDAO enforcementActionDAO = enforcementActionDAO();
        
        try {
        	ret = enforcementActionDAO.retrieveEnforcementAction(enforcementActionId);
        	populateEnforcementAction(ret);
        } catch (DAOException de) {
        	DisplayUtil.displayError("Failed to retrieve enforcement action");
    	    logger.error(de.getMessage(), de);
    	    throw de;
        }	
        return ret;
	}
	
	/**
	 * 
	 * @param referralTypeCd
	 * @param enforcementActionId
	 * @throws DAOException
	 * 
	 */
	public void createReferalType(String referralTypeCd, Integer enforcementActionId)
			throws DAOException {
		
		EnforcementActionDAO enforcementActionDAO = enforcementActionDAO();
		
		try {
			enforcementActionDAO.createReferalType(referralTypeCd, enforcementActionId);
		} catch (DAOException de) {
        	DisplayUtil.displayError("Failed to create enforcement action referral type");
    	    logger.error(de.getMessage(), de);
    	    throw de;
    	    
        }	
	}
	
	/**
	 * 
	 * @param enforcementActionId
	 * @throws DAOException
	 * 
	 */
	public void deleteReferralTypeById(Integer enforcementActionId)
			throws DAOException {
		
		EnforcementActionDAO enforcementActionDAO = enforcementActionDAO();
		
		try {
			enforcementActionDAO.deleteReferralTypeById(enforcementActionId);
		} catch (DAOException de) {
        	DisplayUtil.displayError("Failed to delete enforcement action referral type");
    	    logger.error(de.getMessage(), de);
    	    throw de;
        }	
	}
	
	/**
	 * 
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
	 */
	public List<EnforcementAction> searchEnforcementActions(String facilityId, String facilityName, String doLaaCd,
			String countyCd, String addToHPVList,
			String eventCd,
			Timestamp beginActionDt, Timestamp endActionDt,
			Integer evaluator, String enforcementId, List<String> enfStateCases, String enfCaseNum, 
			String actionTypeCd, String cmpId,
			String docketNumber) throws DAOException {
		List<EnforcementAction> result = new ArrayList<EnforcementAction>();
		result = enforcementActionDAO().searchEnforcementActions(facilityId, facilityName, doLaaCd, countyCd, 
					addToHPVList, eventCd, beginActionDt, endActionDt,
					evaluator, enforcementId, enfStateCases, enfCaseNum, actionTypeCd, cmpId,
					docketNumber);
		for (EnforcementAction enf : result) {
			populateEnforcementAction(enf);
		}
		return result;
	}
	
	private void populateEnforcementAction(EnforcementAction enf) throws DAOException {
		if (enf == null ) {
			return;
		}
		enf.setEnfFormOfActionCd("I");
		
		List<EnforcementActionEvent> events = enforcementActionDAO()
				.retrieveEnforcementActionEventList(
						enf.getEnforcementActionId());

		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

		for (EnforcementActionEvent ea : events) {
			String eventCdStr = ea.getEventCd();
			
			if (eventCdStr != null && ea.getEventDate() != null) {
				formatter = new SimpleDateFormat("MM/dd/yyyy");
				String s = null;
				s = formatter.format(ea.getEventDate());
				
				if (eventCdStr.equals(EnforcementActionEventDef.NOV_ISSUED)) { // NOV issued
					if (!Utility.isNullOrEmpty(enf.getDateNOVIssuedString())) {
						enf.setDateNOVIssuedString(enf.getDateNOVIssuedString()
								+ " " + s);
					} else {
						enf.setDateNOVIssuedString(s);
					}
				} else if (eventCdStr.equals(EnforcementActionEventDef.DAY_ZERO)) { // Day Zero
					if (!Utility.isNullOrEmpty(enf.getDateDayZeroString())) {
						enf.setDateDayZeroString(enf.getDateDayZeroString()
								+ " " + s);
					} else {
						enf.setDateDayZeroString(s);
					}
				} else if (eventCdStr.equals(EnforcementActionEventDef.REFERRED_TO_AG)) { // Referred to AG
					if (!Utility.isNullOrEmpty(enf.getDateReferredToAGString())) {
						enf.setDateReferredToAGString(enf
								.getDateReferredToAGString() + " " + s);
					} else {
						enf.setDateReferredToAGString(s);
					}
				} else if (eventCdStr.equals(EnforcementActionEventDef.FINAL_SETTLEMENT_AGREEMENT)) { // Final Settlement Agreement
					if (!Utility.isNullOrEmpty(enf.getDateFinalSettlementAgreementString())) {
						enf.setDateFinalSettlementAgreementString(enf
								.getDateFinalSettlementAgreementString() + " " + s);
					} else {
						enf.setDateFinalSettlementAgreementString(s);
					}
				} else if (eventCdStr.equals(EnforcementActionEventDef.CHECK_RECEIVED)) { // Check Received
					if (!Utility
							.isNullOrEmpty(enf.getDateCheckReceivedString())) {
						enf.setDateCheckReceivedString(enf
								.getDateCheckReceivedString() + " " + s);
					} else {
						enf.setDateCheckReceivedString(s);
					}
				} else if (eventCdStr.equals(EnforcementActionEventDef.NOV_CLOSED)) { // NOV Closed
					if (!Utility.isNullOrEmpty(enf.getDateNOVClosedString())) {
						enf.setDateNOVClosedString(enf.getDateNOVClosedString()
								+ " " + s);
					} else {
						enf.setDateNOVClosedString(s);
					}
				}
			}
		}
		List<EnforcementNote> notes = enforcementActionDAO().retrieveEnforcementNotes(enf.getEnforcementActionId());
		enf.setNotes(notes);
		
		List<EnforcementAttachment> attachments = enforcementActionDAO().retrieveEnforcementAttachments(enf.getEnforcementActionId());
		enf.setAttachments(attachments);
	}
	
	/**
	 * 
	 * @param note
	 * @throws DAOException
	 * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
	 */
	public EnforcementNote createEnforcementNote(EnforcementNote note) throws DAOException {
		EnforcementNote ret = null;
	    try {
	    		
	    	EnforcementActionDAO enforcementActionDAO = enforcementActionDAO();
	    	InfrastructureDAO infrastructureDAO = infrastructureDAO();
			Note tempNote = infrastructureDAO.createNote(note);
			if (tempNote != null) {
				ret = note;
				ret.setNoteId(tempNote.getNoteId());
				enforcementActionDAO.addEnforcementNote(note.getEnforcementId(), note.getNoteId());
			} else {
	            logger.error("Failed to insert Facility Note");
	            throw new DAOException("Failed to insert Facility Note");
	        }
        } catch (DAOException e) {
        	
        } finally {
        	
        }
		return ret; 
	}

	/**
	 * 
	 * @param note
	 * @throws DAOException
	 * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
	 */
	public void modifyEnforcementNote(EnforcementNote note) throws DAOException {
		infrastructureDAO().modifyNote(note);
	}
	
	/**
	 * Returns all of the Compliance Report notes by Report ID.
	 * 
	 * @param int The Report ID
	 * 
	 * @return Note[] All notes of this Compliance Report.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<EnforcementNote> retrieveEnforcementNotes(int enforcementActionId) throws DAOException {
		return enforcementActionDAO().retrieveEnforcementNotes(enforcementActionId);
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
	public Attachment createEnforcementAttachment(EnforcementAction enforcementAction,
			Attachment attachment, InputStream fileStream) throws DAOException,
			ValidationException {

		EnforcementActionDAO enforcementActionDAO = enforcementActionDAO();

		try {
			// add document info to database
			// NOTE: This needs to be done before file is created in file store
			// since document id obtained from createDocument method is used as
			// the file name for the file store file
			attachment = (Attachment) documentDAO().createDocument(
					attachment);
			DocumentUtil.createDocument(attachment.getPath(), fileStream);
			enforcementActionDAO.createEnforcementAttachment(new EnforcementAttachment(
					attachment));
			// if (attachment != null) {
			// // need to set enforcementId here because it defaults to 0
			// // attachment.setObjectId(enf.getEnforcementId().toString());
			// // createEnforcementTradeSecretAttachment(attachment,
			// tsAttachment, tsInputStream, trans);
			// }

		} catch (DAOException e) {
			try {
				DocumentUtil.removeDocument(attachment.getPath());
			} catch (IOException ioex) {
				logger.error("Exception while attempting to delete document: "
						+ attachment.getPath(), ioex);
			}
		} catch (IOException e) {
			new RemoteException(e.getMessage(), e);
		} finally {
		}

		return attachment;
	}
	
	/**
     * 
     * @param enforcementId
     * @return List<EnforcementAttachment>
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    public List<EnforcementAttachment> retrieveEnforcementAttachments(int enforcementId) throws DAOException {
        List<EnforcementAttachment> attachments = null;
        attachments = enforcementActionDAO().retrieveEnforcementAttachments(enforcementId);
        return attachments;
    }
    
    /**
     * @param doc
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void modifyEnforcementAttachment(EnforcementAttachment doc)
            throws DAOException {
        try {
        	EnforcementActionDAO enforcementActionDAO = enforcementActionDAO();
            DocumentDAO documentDAO = documentDAO();
            enforcementActionDAO.modifyEnforcementAttachment(doc);
            documentDAO.modifyDocument(doc);
        } catch (DAOException de) {
            
        } finally {
            
        }

        return;
    }
    
    /**
     * @param doc
     * @return void
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void removeEnforcementAttachment(EnforcementAttachment doc)
            throws DAOException {

        try {
            removeEnforcmentAttachment(doc);
        } catch (DAOException de) {
            
        } finally {
            
        }
    }
    
    private void removeEnforcmentAttachment(EnforcementAttachment doc)
    	    throws DAOException {
    	        EnforcementActionDAO enforcementActionDAO = enforcementActionDAO();
    	        DocumentDAO docDAO = documentDAO();
    	        doc.setTemporary(true);
    	        docDAO.modifyDocument(doc);
    	        enforcementActionDAO.deleteEnforcementAttachment(doc);
    	    }
    
	public List<EnforcementActionEvent> retrieveEnforcementActionEventList(
			Integer enforcementActionId) throws DAOException {

		List<EnforcementActionEvent> eaeList = null;

		try {

			eaeList = enforcementActionDAO()
					.retrieveEnforcementActionEventList(enforcementActionId);
		} catch (DAOException e) {
			DisplayUtil
					.displayError("Error while retrieving Enforcement Action Events");
			logger.error(e.getMessage());
		} catch (Exception e) {
			DisplayUtil
					.displayError("Error while retrieving Enforcement Action Events");
			logger.error(e.getMessage());
		} finally {

		}

		return eaeList;

	}

	public void createEnforcementActionEvent(EnforcementActionEvent eae)
			throws DAOException {
		try {

			enforcementActionDAO().createEnforcementActionEvent(eae);

		} catch (DAOException e) {
			DisplayUtil
					.displayError("Error while creating Enforcement Action Event");
			logger.error(e.getMessage());
		} catch (Exception e) {
			DisplayUtil
					.displayError("Error while creating Enforcement Action Events");
			logger.error(e.getMessage());
		} finally {

		}

	}

	public void modifyEnforcementActionEvent(EnforcementActionEvent eae)
			throws DAOException {
		try {

			enforcementActionDAO().modifyEnforcementActionEvent(eae);

		} catch (DAOException e) {
			DisplayUtil
					.displayError("Error while modifying Enforcement Action Event");
			logger.error(e.getMessage());
		} catch (Exception e) {
			DisplayUtil
					.displayError("Error while modifying Enforcement Action Events");
			logger.error(e.getMessage());
		} finally {

		}

	}

	public final void removeEnforcementActionEvent(EnforcementActionEvent eae)
			throws DAOException {
		try {

			enforcementActionDAO().removeEnforcementActionEvent(eae);
		} catch (DAOException e) {
			DisplayUtil
					.displayError("Error while removing Enforcement Action Events");
			logger.error(e.getMessage());
		} catch (Exception e) {
			DisplayUtil
					.displayError("Error while removing Enforcement Action Events");
			logger.error(e.getMessage());
		} finally {

		}
	}
	
	public final EnforcementAction[] retrieveEnforcementActionByFacilityId(String facilityId)
			throws DAOException {
		return enforcementActionDAO().retrieveEnforcementActionByFacilityId(facilityId);
	}
	
	private void createWorkflow(EnforcementAction enforcementAction, Integer fpId, Integer assignedUserId,
			Integer userId, Timestamp startDt) throws DAOException, ServiceFactoryException,
			RemoteException {
		// create workflow
		ReadWorkFlowService wfBO = ServiceFactory.getInstance()
				.getReadWorkFlowService();
		Integer workflowId = null;
		Timestamp dueDt = null;

		logger.debug("Creating enforcement action workflow");
		String ptName = WorkFlowProcess.ENFORCEMENT_ACTIONS;

		workflowId = wfBO.retrieveWorkflowTempIdAndNm().get(ptName);
		if (workflowId == null) {
			String s = "Failed to find workflow for \"" + ptName
					+ "\" for Enforcement Action " + enforcementAction.getEnfId();
			logger.error(s);
			throw new DAOException(s);
		}

		String rush = "N";

		if (fpId == null) {
			FacilityDAO fd = facilityDAO();
			Facility fp = fd.retrieveFacility(enforcementAction.getFacilityId());
			fpId = fp.getFpId();
		}
		
		HashMap<String,String> data = new HashMap<String,String>();
		data.put(WorkFlowManager.ASSIGNED_USER, String.valueOf(assignedUserId));

		logger.debug(" creating Enforcement Action workflow with workflowId: "
				+ workflowId + ", Enforcement Action ID " + enforcementAction.getEnfId()
				+ ", facility Id: " + enforcementAction.getFacilityId() + ", UID: "
				+ userId + ", Due Date: " + dueDt);
		WorkFlowManager wfm = new WorkFlowManager();
		WorkFlowResponse resp = wfm.submitProcess(workflowId,
				enforcementAction.getEnforcementActionId(), fpId, userId, rush, startDt, dueDt, data, null);

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
			String s = "Error encountered trying to create workflow for enforcement action "
					+ enforcementAction.getEnfId() + ": " + errMsg.toString();
			logger.error(s);
			throw new DAOException(s + " " + recomMsg);
		}
	}
}
