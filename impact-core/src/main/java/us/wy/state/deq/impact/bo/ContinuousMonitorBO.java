package us.wy.state.deq.impact.bo;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.BaseBO;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dao.EmissionsReportDAO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityCemComLimit;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.wy.state.deq.impact.database.dao.ContinuousMonitorDAO;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitorEqt;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitorNote;

@Transactional(rollbackFor = Exception.class)
@Service
public class ContinuousMonitorBO extends BaseBO implements
		ContinuousMonitorService {

	@Autowired FacilityService facilityService;
	
	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	/**
	 * 
	 * @param continuousMonitor
	 * @throws DAOException
	 * 
	 */
	@Override
	public ContinuousMonitor createContinuousMonitor(
			ContinuousMonitor continuousMonitor) throws DAOException,
			RemoteException, ServiceFactoryException {

		ContinuousMonitorDAO continuousMonitorDAO = continuousMonitorDAO();
		
		// create a new version of the facility if the current version is preserved
		Facility newFacility = copyFacilityProfile(continuousMonitor.getFpId());
		if (!newFacility.getFpId().equals(continuousMonitor.getFpId())) {
			// if a new facility version was created, then create the CEMS/COMS/CMS monitor
			//in the new current version of the facility
			continuousMonitor.setFpId(newFacility.getFpId());
		}

		continuousMonitor = continuousMonitorDAO
				.createContinuousMonitor(continuousMonitor);
		
		return continuousMonitor;
	}

	/**
	 * 
	 * @param action
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	
	@Override
	public Integer deleteContinuousMonitor(ContinuousMonitor monitor)
			throws DAOException, RemoteException {

		InfrastructureDAO infraDAO = infrastructureDAO();

		ContinuousMonitorDAO continuousMonitorDAO = getContinuousMonitorDAO();

		FacilityDAO facilityDAO = getFacilityDAO();

		Integer fpId = null;

		try {

			if (isInternalApp()) {
				// create a new version of the facility if the current version
				// is
				// preserved
				
				// TFS: 5829 - preserve the facility version so that the deletion
				// shall trigger the creation of new version of the facility inventory
				getFacilityService().markProfileHistory(monitor.getFpId());
				
				Facility newFacility = copyFacilityProfile(monitor.getFpId());
				if (!newFacility.getFpId().equals(monitor.getFpId())) {
					// if a new facility version was created, then delete the
					// CEMS/COMS/CMS monitor
					// from the new current version of the facility
					monitor = retrieveContinuousMonitorByFpIdAndCorrId(
							newFacility.getFpId(), monitor.getCorrMonitorId());
				}
			}

			// for returning the fpId associated with the CEM/COM/CMS monitor
			// being
			// deleted.
			// this will help the caller to determine whether the deletion
			// triggered
			// creation of a new facility inventory or not.
			fpId = monitor.getFpId();

			continuousMonitorDAO.removeContinuousMonitorEqtList(monitor
					.getContinuousMonitorId());

			facilityDAO.removeFacilityCemComLimitList(monitor
					.getContinuousMonitorId());
			/*
			 * for (ContinuousMonitorAttachment ea : monitor.getAttachments()) {
			 * removeContinuousMonitorAttachment(ea); }
			 */
			// delete the notes only if there are no correlated monitors

			if (isInternalApp()) {
				List<Integer> corrMonitorIdList = continuousMonitorDAO()
						.retrieveCorrelatedMonitorIds(
								monitor.getCorrMonitorId(),
								monitor.getContinuousMonitorId());
				if (corrMonitorIdList.isEmpty()) {
					continuousMonitorDAO.deleteContinuousMonitorNotes(monitor
							.getCorrMonitorId());
					for (Note eaNote : monitor.getNotes()) {
						infraDAO.removeNote(eaNote.getNoteId());
					}
				}
			}

			// delete references to associated eus
			continuousMonitorDAO.deleteAssociatedFpEuIdsByMonitorId(monitor
					.getContinuousMonitorId());

			// delete references to associated release points
			continuousMonitorDAO
					.deleteAssociatedFpEgressPointIdsByMonitorId(monitor
							.getContinuousMonitorId());

			continuousMonitorDAO.deleteContinuousMonitor(monitor);

		} catch (DAOException de) {
			DisplayUtil.displayError("Failed to delete monitor");
			logger.error(de.getMessage(), de);
			throw de;
		}

		return fpId;
	}

	/**
	 * 
	 * @param continuousMonitor
	 * @throws DAOException
	 * 
	 */
	@Override
	public Facility modifyContinuousMonitor(
			ContinuousMonitor continuousMonitor,
			List<Integer> fpEuIdsToAssociate,
			List<Integer> fpEuIdsToDisassociate,
			List<Integer> fpEgressPointIdsToAssociate,
			List<Integer> fpEgressPointIdsToDisassociate) 
					throws DAOException, RemoteException {

		ContinuousMonitorDAO continuousMonitorDAO = continuousMonitorDAO();
		
		List<Integer> updEuIdsToAssociate = new ArrayList<Integer>();
		List<Integer> updEuIdsToDisassociate = new ArrayList<Integer>();
		List<Integer> updEgressPointIdsToAssociate = new ArrayList<Integer>();
		List<Integer> updEgressPointIdsToDiaAssociate = new ArrayList<Integer>();
		
		boolean copyOnChange = false;
		
		// create a new version of the facility if the current version is preserved
		Facility newFacility = copyFacilityProfile(continuousMonitor.getFpId());
		if (!newFacility.getFpId().equals(continuousMonitor.getFpId())) {
			// if a new facility version was created, then modify the CEMS/COMS/CMS monitor
			// in the new current version of the facility
			ContinuousMonitor cm = retrieveContinuousMonitorByFpIdAndCorrId(
					newFacility.getFpId(), continuousMonitor.getCorrMonitorId()); 
					
			continuousMonitor.setContinuousMonitorId(cm.getContinuousMonitorId());
			continuousMonitor.setFpId(cm.getFpId());
			continuousMonitor.setLastModified(cm.getLastModified());
			
			updEuIdsToAssociate = updateIds(fpEuIdsToAssociate,
					newFacility.getCopyOnChangeEuIds());
			updEuIdsToDisassociate = updateIds(fpEuIdsToDisassociate,
					newFacility.getCopyOnChangeEuIds());
			updEgressPointIdsToAssociate = updateIds(
					fpEgressPointIdsToAssociate,
					newFacility.getCopyOnChangeFpNodeIds());
			updEgressPointIdsToDiaAssociate = updateIds(
					fpEgressPointIdsToDisassociate,
					newFacility.getCopyOnChangeFpNodeIds());
			
			copyOnChange = true;
		}

		continuousMonitorDAO.modifyContinuousMonitor(continuousMonitor);
		
		// update the associated objects
		if (copyOnChange) {
			// use the updated ids
			updateAssociatedFpEuIds(continuousMonitor.getContinuousMonitorId(),
					updEuIdsToAssociate, updEuIdsToDisassociate);

			updateAssociatedFpEgressPointIds(
					continuousMonitor.getContinuousMonitorId(),
					updEgressPointIdsToAssociate,
					updEgressPointIdsToDiaAssociate);
		} else {
			updateAssociatedFpEuIds(continuousMonitor.getContinuousMonitorId(),
					fpEuIdsToAssociate, fpEuIdsToDisassociate);

			updateAssociatedFpEgressPointIds(
					continuousMonitor.getContinuousMonitorId(),
					fpEgressPointIdsToAssociate, fpEgressPointIdsToDisassociate);
		}
		
		// return the facility so that the associated object ids can be
		// properly updated in case a new version of the facility was created
		return newFacility;
	}

	/**
	 * 
	 * @param continuousMonitorId
	 * @throws DAOException
	 * 
	 */
	public ContinuousMonitor retrieveContinuousMonitor(
			Integer continuousMonitorId) throws DAOException {

		ContinuousMonitor ret = null;
		ContinuousMonitorDAO continuousMonitorDAO = getContinuousMonitorDAO();

		try {
			ret = continuousMonitorDAO
					.retrieveContinuousMonitor(continuousMonitorId);
			populateContinuousMonitor(ret, true);
		} catch (DAOException de) {
			DisplayUtil.displayError("Failed to retrieve monitor");
			logger.error(de.getMessage(), de);
			throw de;
		}
		return ret;
	}
	
	public ContinuousMonitor retrieveContinuousMonitor(
			Integer continuousMonitorId, boolean staging) throws DAOException {

		ContinuousMonitor ret = null;
		ContinuousMonitorDAO continuousMonitorDAO = getContinuousMonitorDAO(staging);

		try {
			ret = continuousMonitorDAO
					.retrieveContinuousMonitor(continuousMonitorId);
			populateContinuousMonitor(ret,staging);
		} catch (DAOException de) {
			DisplayUtil.displayError("Failed to retrieve monitor");
			logger.error(de.getMessage(), de);
			throw de;
		}
		return ret;
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
	public List<ContinuousMonitor> searchContinuousMonitors(Integer fpId, String facilityId,
			String facilityName, String doLaaCd, String countyCd,
			Timestamp addDt, Timestamp endDt, String monId, String cmpId, boolean staging)
			throws DAOException {
		List<ContinuousMonitor> result = new ArrayList<ContinuousMonitor>();
		result = getContinuousMonitorDAO(staging).searchContinuousMonitors(fpId, facilityId,
				facilityName, doLaaCd, countyCd, addDt, endDt, monId, cmpId);
		for (ContinuousMonitor cm : result) {
			populateContinuousMonitor(cm, staging);
		}
		return result;
	}

	public void populateContinuousMonitor(ContinuousMonitor cm, boolean staging)
			throws DAOException {
		if (cm == null) {
			return;
		}
		List<ContinuousMonitorEqt> eqts = getContinuousMonitorDAO(staging)
				.retrieveContinuousMonitorEqtList(cm.getContinuousMonitorId());
		cm.setContinuousMonitorEqtList(eqts);
		
		List<FacilityCemComLimit> limits = getFacilityDAO(staging).retrieveFacilityCemComLimitListByMonitorId(cm.getContinuousMonitorId());
		cm.setFacilityCemComLimitList(limits);

		//SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

		if (isInternalApp()) {
			List<ContinuousMonitorNote> notes = getContinuousMonitorDAO()
					.retrieveContinuousMonitorNotes(cm.getCorrMonitorId());
			cm.setNotes(notes);
		}

		// List<ContinuousMonitorAttachment> attachments =
		// continuousMonitorDAO().retrieveContinuousMonitorAttachments(cm.getContinuousMonitorId());
		// cm.setAttachments(attachments);
		
		cm.setAssociatedFpEuIds(getContinuousMonitorDAO(staging).
				retrieveAssociatedFpEuIdsByMonitorId(cm.getContinuousMonitorId()));
		
		cm.setAssociatedFpEgressPointIds(getContinuousMonitorDAO(staging).
				retrieveAssociatedFpEgressPointIdsByMonitorId(cm.getContinuousMonitorId()));
		
		cm.setAssociatedFpEuEpaEmuIds(getContinuousMonitorDAO(staging).
				retrieveAssociatedFpEuEpaEmuIdsByMonitorId(cm.getContinuousMonitorId()));
		
		cm.setAssociatedFpEgressPointRPIds(getContinuousMonitorDAO(staging)
				.retrieveAssociatedFpReleasePointIdsByMonitorId(cm.getContinuousMonitorId()));
	}

	/**
	 * 
	 * @param note
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ContinuousMonitorNote createContinuousMonitorNote(
			ContinuousMonitorNote note) throws DAOException {
		ContinuousMonitorNote ret = null;
		
		ContinuousMonitorDAO continuousMonitorDAO = continuousMonitorDAO();
		InfrastructureDAO infrastructureDAO = infrastructureDAO();
		Note tempNote = infrastructureDAO.createNote(note);

		ret = note;
		ret.setNoteId(tempNote.getNoteId());
		continuousMonitorDAO.addContinuousMonitorNote(
				note.getNoteId(),
				note.getCorrMonitorId());
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
	public void modifyContinuousMonitorNote(ContinuousMonitorNote note)
			throws DAOException {
		infrastructureDAO().modifyNote(note);
	}

	/**
	 * Returns all of the Monitor notes by Monitor ID.
	 * 
	 * @param int The Report ID
	 * 
	 * @return Note[] All notes of this Monitor.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<ContinuousMonitorNote> retrieveContinuousMonitorNotes(
			int corrMonitorId) throws DAOException {
		return continuousMonitorDAO().retrieveContinuousMonitorNotes(
				corrMonitorId);
	}

	/**
	 * Create a new row in the Attachment table.
	 * 
	 * @param siteVisit
	 * @param attachment
	 * @param fileStream
	 * @return Attachment
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	/*
	 * public Attachment createContinuousMonitorAttachment(ContinuousMonitor
	 * continuousMonitor, Attachment attachment, InputStream fileStream) throws
	 * DAOException, ValidationException {
	 * 
	 * ContinuousMonitorDAO continuousMonitorDAO = continuousMonitorDAO();
	 * 
	 * try { // add document info to database // NOTE: This needs to be done
	 * before file is created in file store // since document id obtained from
	 * createDocument method is used as // the file name for the file store file
	 * attachment = (Attachment) documentDAO().createDocument( attachment);
	 * DocumentUtil.createDocument(attachment.getPath(), fileStream);
	 * continuousMonitorDAO.createContinuousMonitorAttachment(new
	 * ContinuousMonitorAttachment( attachment)); // if (attachment != null) {
	 * // // need to set enforcementId here because it defaults to 0 // //
	 * attachment.setObjectId(enf.getEnforcementId().toString()); // //
	 * createEnforcementTradeSecretAttachment(attachment, // tsAttachment,
	 * tsInputStream, trans); // }
	 * 
	 * } catch (DAOException e) { try {
	 * DocumentUtil.removeDocument(attachment.getPath()); } catch (IOException
	 * ioex) { logger.error("Exception while attempting to delete document: " +
	 * attachment.getPath(), ioex); } } catch (IOException e) { new
	 * RemoteException(e.getMessage(), e); } finally { }
	 * 
	 * return attachment; }
	 * 
	 * public List<ContinuousMonitorAttachment>
	 * retrieveContinuousMonitorAttachments(int enforcementId) throws
	 * DAOException { List<ContinuousMonitorAttachment> attachments = null;
	 * attachments =
	 * continuousMonitorDAO().retrieveContinuousMonitorAttachments(
	 * enforcementId); return attachments; }
	 * 
	 * public void modifyContinuousMonitorAttachment(ContinuousMonitorAttachment
	 * doc) throws DAOException { try { ContinuousMonitorDAO
	 * continuousMonitorDAO = continuousMonitorDAO(); DocumentDAO documentDAO =
	 * documentDAO();
	 * continuousMonitorDAO.modifyContinuousMonitorAttachment(doc);
	 * documentDAO.modifyDocument(doc); } catch (DAOException de) {
	 * 
	 * } finally {
	 * 
	 * }
	 * 
	 * return; }
	 * 
	 * public void removeContinuousMonitorAttachment(ContinuousMonitorAttachment
	 * doc) throws DAOException {
	 * 
	 * try { removeEnforcmentAttachment(doc); } catch (DAOException de) {
	 * 
	 * } finally {
	 * 
	 * } }
	 * 
	 * private void removeEnforcmentAttachment(ContinuousMonitorAttachment doc)
	 * throws DAOException { ContinuousMonitorDAO continuousMonitorDAO =
	 * continuousMonitorDAO(); DocumentDAO docDAO = documentDAO();
	 * doc.setTemporary(true); docDAO.modifyDocument(doc);
	 * continuousMonitorDAO.deleteContinuousMonitorAttachment(doc); }
	 */
	public List<ContinuousMonitorEqt> retrieveContinuousMonitorEqtList(
			Integer continuousMonitorId, boolean staging) throws DAOException {

		List<ContinuousMonitorEqt> cmeList = null;

		cmeList = getContinuousMonitorDAO(staging).retrieveContinuousMonitorEqtList(
				continuousMonitorId);

		return cmeList;

	}
	
	public List<ContinuousMonitorEqt> retrieveContinuousMonitorEqtListNewestFirst(
			Integer continuousMonitorId) throws DAOException {

		List<ContinuousMonitorEqt> cmeList = null;

		cmeList = continuousMonitorDAO()
				.retrieveContinuousMonitorEqtListNewestFirst(continuousMonitorId);

		return cmeList;

	}
	
	public List<ContinuousMonitorEqt> retrieveActiveContinuousMonitorEqtList(
			Integer continuousMonitorId) throws DAOException {

		List<ContinuousMonitorEqt> cmeList = null;

		cmeList = continuousMonitorDAO()
				.retrieveActiveContinuousMonitorEqtList(continuousMonitorId);

		return cmeList;

	}

	@Override
	public Integer createContinuousMonitorEqt(ContinuousMonitorEqt newEqt)
			throws DAOException, RemoteException {
		
		// get the CEM/COM/CMS monitor with which this equipment is associated
		ContinuousMonitor continuousMonitor = retrieveContinuousMonitor(newEqt
				.getContinuousMonitorId());

		// create a new version of the facility if the current version is
		// preserved
		Facility newFacility = copyFacilityProfile(continuousMonitor.getFpId());
		if (!newFacility.getFpId().equals(continuousMonitor.getFpId())) {
			// if a new facility version was created, then create the equipment
			// in the new current version of the facility
			ContinuousMonitor cm = retrieveContinuousMonitorByFpIdAndCorrId(
					newFacility.getFpId(), continuousMonitor.getCorrMonitorId()); 
			newEqt.setContinuousMonitorId(cm.getContinuousMonitorId());
		}

		// If there is only one active physical monitor for this logical
		// monitor, automatically set the Removal Date with
		// the value of the install date of the new physical monitor (if
		// provided).
		List<ContinuousMonitorEqt> activeEqtList = retrieveActiveContinuousMonitorEqtList(newEqt
				.getContinuousMonitorId());
		if (activeEqtList.size() == 1) {
			ContinuousMonitorEqt cme = activeEqtList.get(0);
			if (newEqt.getInstallDate() != null
					&& !Utility.isNullOrEmpty(newEqt.getInstallDate()
							.toString())) {
				// if the install date of the new equipment being added is prior to the
				// install date of the current active equipment, then do not set the
				// removal date on the current active equipment.
				if(null != cme.getInstallDate()
						&& newEqt.getInstallDate().before(cme.getInstallDate())) {
					DisplayUtil
							.displayWarning("Install date is prior to the install date of the current active monitor");
				} else {
					cme.setRemovalDate(newEqt.getInstallDate());
					modifyContinuousMonitorEqt(cme);
				}
			}
		}

		continuousMonitorDAO().createContinuousMonitorEqt(newEqt);

		// return the fpId associated with the facility version with which the
		// CEM/COM/CMS monitor containing this equipment is associated with. 
		// this will help the caller to determine whether the creation of
		// the equipment triggered creation of a new facility inventory or not.
		return newFacility.getFpId();
	}

	public Integer modifyContinuousMonitorEqt(ContinuousMonitorEqt cme)
			throws DAOException, RemoteException {

		// get the CEM/COM/CMS monitor with which this equipment is associated
		ContinuousMonitor continuousMonitor = retrieveContinuousMonitor(cme
				.getContinuousMonitorId());

		// create a new version of the facility if the current version is
		// preserved
		Facility newFacility = copyFacilityProfile(continuousMonitor.getFpId());
		if (!newFacility.getFpId().equals(continuousMonitor.getFpId())) {
			// if a new facility version was created because the current version was
			// marked as preserved, then update the equipment in the corresponding
			// monitor in the new current version of the facility
			
			// since the equipment object does not have a correlated id, we will use
			// a delete and add scheme to update the equipment in the new 
			// current facility version
			
			// Outline of the steps:
			// 1. Get the list of equipments associated with the logical monitor in
			//    the previous current facility version.
			// 2. Replace the equipment being updated in this list.
			// 3. Get the list of equipments associated with the logical monitor in
			//    the new current facility version.
			// 4. Remove all the equipments in the list from the database.
			// 5. Update the monitor id for the equipments in the list (step #1), to
			//    the logical monitor id in the current facility version.
			// 6. Add the equipment in the list (step #1) to the database.
			List<ContinuousMonitorEqt> removeList = new ArrayList<ContinuousMonitorEqt>();

			// Step #1
			List<ContinuousMonitorEqt> addList = continuousMonitor
					.getContinuousMonitorEqtList();

			// Step #2
			int index = -1;
			for (ContinuousMonitorEqt eqp : addList) {
				if (eqp.getMonitorEqtId().equals(cme.getMonitorEqtId())) {
					index = addList.indexOf(eqp);
					break;
				}
			}
			if (index != -1) {
				addList.set(index, cme);
			}

			// Step #3
			ContinuousMonitor cm = retrieveContinuousMonitorByFpIdAndCorrId(
					newFacility.getFpId(), continuousMonitor.getCorrMonitorId());
			Integer monitorId = cm.getContinuousMonitorId();
			removeList.addAll(retrieveContinuousMonitor(monitorId)
					.getContinuousMonitorEqtList());
					
			// Step #4
			for (ContinuousMonitorEqt eqp : removeList) {
				continuousMonitorDAO().removeContinuousMonitorEqt(eqp);
			}

			// # Steps #5 and #6
			for (ContinuousMonitorEqt eqp : addList) {
				eqp.setContinuousMonitorId(monitorId);
				eqp.setMonitorEqtId(null);
				continuousMonitorDAO().createContinuousMonitorEqtWithCreatedDt(
						eqp);
			}
		} else {
			continuousMonitorDAO().modifyContinuousMonitorEqt(cme);
		}

		// return the fpId associated with the facility version with which the
		// CEM/COM/CMS monitor containing this equipment is associated with. 
		// this will help the caller to determine whether the updating of
		// the equipment triggered creation of a new facility inventory or not.
		return newFacility.getFpId();

	}

	@Override
	public final Integer removeContinuousMonitorEqt(ContinuousMonitorEqt cme)
			throws DAOException, RemoteException {

		// get the CEM/COM/CMS monitor with which this equipment is associated
		ContinuousMonitor continuousMonitor = retrieveContinuousMonitor(cme
				.getContinuousMonitorId());

		// create a new version of the facility if the current version is
		// preserved
		Facility newFacility = copyFacilityProfile(continuousMonitor.getFpId());
		if (!newFacility.getFpId().equals(continuousMonitor.getFpId())) {
			// if a new facility version was created, then delete the equipment
			// from the corresponding logical monitor in the new current
			// version of the facility
			
			// since the equipment object does not have a correlated id, we will use
			// a delete and add scheme to remove the equipment from it's logical
			// monitor in the new current facility version
			
			// Outline of the steps:
			// 1. Get the list of equipments associated with the logical monitor in
			//    the previous current facility version.
			// 2. Remove the equipment being deleted from this list.
			// 3. Get the list of equipments associated with the logical monitor in
			//    the new current facility version.
			// 4. Remove all the equipments in the list from the database.
			// 5. Update the monitor id for the equipments in the list (step #1), to
			//    the logical monitor id in the current facility version.
			// 6. Add the equipment in the list (step #1) to the database.
			List<ContinuousMonitorEqt> removeList = new ArrayList<ContinuousMonitorEqt>();

			// Step #1
			List<ContinuousMonitorEqt> addList = continuousMonitor
					.getContinuousMonitorEqtList();

			// Step #2
			addList.remove(cme);

			// Step #3
			ContinuousMonitor cm = retrieveContinuousMonitorByFpIdAndCorrId(
					newFacility.getFpId(), continuousMonitor.getCorrMonitorId());
			Integer monitorId = cm.getContinuousMonitorId();
			removeList.addAll(retrieveContinuousMonitor(monitorId)
					.getContinuousMonitorEqtList());
			

			// Step #4
			for (ContinuousMonitorEqt eqp : removeList) {
				continuousMonitorDAO().removeContinuousMonitorEqt(eqp);
			}

			// # Steps #5 and #6
			for (ContinuousMonitorEqt eqp : addList) {
				eqp.setContinuousMonitorId(monitorId);
				eqp.setMonitorEqtId(null);
				continuousMonitorDAO().createContinuousMonitorEqtWithCreatedDt(
						eqp);
			}
		} else {
			continuousMonitorDAO().removeContinuousMonitorEqt(cme);
		}

		// return the fpId associated with the facility version with which the
		// CEM/COM/CMS monitor containing this equipment is associated with. 
		// this will help the caller to determine whether the deletion of
		// the equipment triggered creation of a new facility inventory or not.
		return newFacility.getFpId();

	}

	public final ContinuousMonitor[] retrieveContinuousMonitorByFpId(
			Integer fpId) throws DAOException {
		return continuousMonitorDAO().retrieveContinuousMonitorByFpId(
				fpId);
	}
	
	
	@Override
	public boolean createAssociatedFpEuIdRef(Integer monitorId, Integer emuId)
			throws DAOException {
		return continuousMonitorDAO().createAssociatedFpEuIdRef(monitorId, emuId);
	}
	
	@Override
	public List<Integer> retrieveAssociatedFpEuIdsByMonitorId(Integer monitorId)
			throws DAOException {
		return continuousMonitorDAO().retrieveAssociatedFpEuIdsByMonitorId(monitorId);
	}
	
	@Override
	public void updateAssociatedFpEuIds(Integer monitorId,
			List<Integer> fpEuIdsToAssociate,
			List<Integer> fpEuIdsToDisassoicate) throws DAOException {
		
		// add rows for the newly associated release points
		for(Integer emuId : fpEuIdsToAssociate) {
			createAssociatedFpEuIdRef(monitorId, emuId);
		}
		
		// delete rows for the disassociated release points
		for(Integer emuId : fpEuIdsToDisassoicate) {
			deleteAssociatedFpEuIdRef(monitorId, emuId);
		}
	}

	@Override
	public void deleteAssociatedFpEuIdRef(Integer monitorId, Integer emuId)
			throws DAOException {
		continuousMonitorDAO().deleteAssociatedFpEuIdRef(monitorId, emuId);
	}

	@Override
	public void deleteAssociatedFpEuIdsByMonitorId(Integer monitorId)
			throws DAOException {
		continuousMonitorDAO().deleteAssociatedFpEuIdsByMonitorId(monitorId);
	}

	@Override
	public boolean createAssociatedFpEgressPointIdRef(Integer monitorId,
			Integer fpNodeId) throws DAOException {
		return continuousMonitorDAO().createAssociatedFpEgressPointIdRef(monitorId, fpNodeId);
	}

	@Override
	public List<Integer> retrieveAssociatedFpEgressPointIdsByMonitorId(
			Integer monitorId) throws DAOException {
		return continuousMonitorDAO().retrieveAssociatedFpEgressPointIdsByMonitorId(monitorId);
	}
	
	@Override
	public void updateAssociatedFpEgressPointIds(Integer monitorId,
			List<Integer> fpEgressPointIdsToAssociate,
			List<Integer> fpEgressPointIdsToDisassociate)
			throws DAOException {
		// add rows for the newly associated release points
		for(Integer fpNodeId : fpEgressPointIdsToAssociate) {
			createAssociatedFpEgressPointIdRef(monitorId, fpNodeId);
		}
		
		// delete rows for the disassociated release points
		for(Integer fpNodeId : fpEgressPointIdsToDisassociate) {
			deleteAssociatedFpEgressPointIdRef(monitorId, fpNodeId);
		}
	}

	@Override
	public void deleteAssociatedFpEgressPointIdRef(Integer monitorId,
			Integer fpNodeId) throws DAOException {
		continuousMonitorDAO().deleteAssociatedFpEgressPointIdRef(monitorId, fpNodeId);
		
	}

	@Override
	public void deleteAssociatedFpEgressPointIdsByMonitorId(Integer monitorId)
			throws DAOException {
		continuousMonitorDAO().deleteAssociatedFpEgressPointIdsByMonitorId(monitorId);
		
	}
	
	@Override
	public List<String> retrieveAssociatedFpEuEpaEmuIdsByMonitorId(Integer monitorId)
			throws DAOException {
		return continuousMonitorDAO().retrieveAssociatedFpEuEpaEmuIdsByMonitorId(monitorId);
	}

	@Override
	public List<String> retrieveAssociatedFpReleasePointIdsByMonitorId(
			Integer monitorId) throws DAOException {
		return continuousMonitorDAO().retrieveAssociatedFpReleasePointIdsByMonitorId(monitorId);
	}
	
	// for creating new version of the facility if the current
	// version is marked as preserved
	private Facility copyFacilityProfile(Integer fpId) throws RemoteException {
		Facility newFacility = getFacilityService().copyFacilityProfile(fpId,
				new Timestamp(System.currentTimeMillis()),
				InfrastructureDefs.getCurrentUserId());
		if (newFacility == null) {
			throw new DAOException("Cannot access facility");
		}
		
		newFacility.setValidated(false);
		newFacility.setSubmitted(false);
		
		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			newFacility.setLastSubmissionType("I");
		} else if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			newFacility.setLastSubmissionType("E");
			newFacility.setLastSubmissionVersion(newFacility
					.getLastSubmissionVersion() + 1);
		}
		
		facilityDAO().modifyFacility(newFacility);
		
		applicationDAO().setActiveApplicationsValidatedFlag(
				newFacility.getFpId(), false);
		
		emissionsReportDAO().setActiveEmissionsReportsValidatedFlag(newFacility.getFpId(), false);
		
		// invalidate any in-progress compliance reports associated with this
		// facility
		complianceReportDAO().setActiveComplianceReportsValidatedFlag(
				newFacility.getFpId(), false);
		
		return newFacility;
	}
	
	@Override
	public 	ContinuousMonitor retrieveContinuousMonitorByFpIdAndCorrId(Integer fpId, 
			Integer corrMonitorId) throws DAOException {
		return continuousMonitorDAO().retrieveContinuousMonitorByFpIdAndCorrId(fpId, corrMonitorId);
	}

	@Override
	public Map<Integer, String> facilitiesWithMatchingContinuousMonitorEqt(String manufacturerName,
			String modelNumber, String serialNumber) throws DAOException {
		return continuousMonitorDAO().facilitiesWithMatchingContinuousMonitorEqt(
				manufacturerName, modelNumber, serialNumber);
	}
	
	private List<Integer> updateIds(List<Integer> ids,
			HashMap<Integer, Integer> copyOnChangeIds) {
		Integer newId;
		List<Integer> updatedIds = new ArrayList<Integer>();
		for (Integer id : ids) {
			newId = copyOnChangeIds.get(id);
			if (null != newId) {
				updatedIds.add(newId);
			}
		}

		return updatedIds;
	}
	
	

}
