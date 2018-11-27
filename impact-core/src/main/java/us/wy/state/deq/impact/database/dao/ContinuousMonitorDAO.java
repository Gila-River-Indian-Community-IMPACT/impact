package us.wy.state.deq.impact.database.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import us.oh.state.epa.stars2.database.dao.TransactableDAO;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitorEqt;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitorNote;

public interface ContinuousMonitorDAO extends TransactableDAO {

	/**
	 * Create a new ContinuousMonitor.
	 * 
	 * @param continuousMonitor
	 * @return
	 * @throws DAOException
	 */
	ContinuousMonitor createContinuousMonitor(
			ContinuousMonitor continuousMonitor) throws DAOException;

	/**
	 * Delete ContinuousMonitor.
	 * 
	 * @param continuousMonitor
	 * @return
	 * @throws DAOException
	 */
	void deleteContinuousMonitor(ContinuousMonitor continuousMonitor)
			throws DAOException;

	/**
	 * Update ContinuousMonitor.
	 * 
	 * @param continuousMonitor
	 * @return
	 * @throws DAOException
	 */
	boolean modifyContinuousMonitor(ContinuousMonitor continuousMonitor)
			throws DAOException;

	/**
	 * Retrieve ContinuousMonitor.
	 * 
	 * @param continuousMonitorid
	 * @return
	 * @throws DAOException
	 */
	public ContinuousMonitor retrieveContinuousMonitor(
			Integer continuousMonitorId) throws DAOException;

	/**
	 * Retrieve all ContinuousMonitor objects for the specified search
	 * parameters. All ContinuousMonitorEqts related to the ContinuousMonitor
	 * will be retrieved as well.
	 * 
	 * @param facilityId
	 * @param facilityName
	 * @param doLaaCd
	 * @param countyCd
	 * @param addDt
	 * @param endDt
	 * @param mondId
	 * @param cmpId
	 * @return
	 * @throws DAOException
	 */
	public List<ContinuousMonitor> searchContinuousMonitors(Integer fpId, String facilityId,
			String facilityName, String doLaaCd, String countyCd,
			Timestamp addDt, Timestamp endDt, String monId, String cmpId)
			throws DAOException;

	/**
	 * Retrieve all attachments for the monitor identified by monitorId.
	 * 
	 * @param monitorId
	 * @return
	 * @throws DAOException
	 */
	// List<ContinuousMonitorAttachment>
	// retrieveContinuousMonitorAttachments(int monitorId) throws DAOException;

	/**
	 * Retrieve all notes for the monitor identified by monitorId.
	 * 
	 * @param monitorId
	 * @return
	 * @throws DAOException
	 */
	List<ContinuousMonitorNote> retrieveContinuousMonitorNotes(int corrMonitorId)
			throws DAOException;

	/**
	 * Add a new monitor note.
	 * 
	 * @param monitorId
	 * @param noteId
	 * @throws DAOException
	 */
	void addContinuousMonitorNote(int noteId, int corrMonitorId)
			throws DAOException;

	/**
	 * Delete all notes for the monitor identified by monitorId.
	 * 
	 * @param monitorId
	 * @throws DAOException
	 */
	void deleteContinuousMonitorNotes(int corrMonitorId) throws DAOException;

	/**
	 * Delete attachment for the monitor attachment identified by att.
	 * 
	 * @param att
	 * @return
	 * @throws DAOException
	 */
	// void deleteContinuousMonitorAttachment(ContinuousMonitorAttachment att)
	// throws DAOException;

	/**
	 * Create attachment for the monitor attachment identified by fa.
	 * 
	 * @param fa
	 * @return
	 * @throws DAOException
	 */
	// ContinuousMonitorAttachment
	// createContinuousMonitorAttachment(ContinuousMonitorAttachment fa) throws
	// DAOException;

	/**
	 * Modify attachment for the monitor attachment identified by doc.
	 * 
	 * @param doc
	 * @return
	 * @throws DAOException
	 */
	// boolean modifyContinuousMonitorAttachment(ContinuousMonitorAttachment
	// doc) throws DAOException;

	/**
	 * @param continuousMonitorId
	 * @return
	 * @throws DAOException
	 */
	List<ContinuousMonitorEqt> retrieveContinuousMonitorEqtList(
			int continuousMonitorId) throws DAOException;
	
	/**
	 * @param continuousMonitorId
	 * @return
	 * @throws DAOException
	 */
	List<ContinuousMonitorEqt> retrieveContinuousMonitorEqtListNewestFirst(
			int continuousMonitorId) throws DAOException;
	
	/**
	 * @param continuousMonitorId
	 * @return
	 * @throws DAOException
	 */
	List<ContinuousMonitorEqt> retrieveActiveContinuousMonitorEqtList(
			int continuousMonitorId) throws DAOException;

	/**
	 * @param continuousMonitorId
	 * @throws DAOException
	 */
	void removeContinuousMonitorEqtList(int continuousMonitorId)
			throws DAOException;

	/**
	 * @param cme
	 * @return
	 * @throws DAOException
	 */
	ContinuousMonitorEqt createContinuousMonitorEqt(ContinuousMonitorEqt cme)
			throws DAOException;

	/**
	 * @param cme
	 * @throws DAOException
	 */
	void modifyContinuousMonitorEqt(ContinuousMonitorEqt cme)
			throws DAOException;

	/**
	 * @param cme
	 * @throws DAOException
	 */
	void removeContinuousMonitorEqt(ContinuousMonitorEqt cme)
			throws DAOException;

	ContinuousMonitor[] retrieveContinuousMonitorByFpId(Integer fpId)
			throws DAOException;
	
	boolean createAssociatedFpEuIdRef(Integer monitorId, Integer emuId) throws DAOException;
	
	List<Integer> retrieveAssociatedFpEuIdsByMonitorId(Integer monitorId) throws DAOException;
	
	void deleteAssociatedFpEuIdRef(Integer monitorId, Integer emuId) throws DAOException;
	
	void deleteAssociatedFpEuIdsByMonitorId(Integer monitorId) throws DAOException;
	
	boolean createAssociatedFpEgressPointIdRef(Integer monitorId, Integer fpNodeId) throws DAOException;
	
	List<Integer> retrieveAssociatedFpEgressPointIdsByMonitorId(Integer monitorId) throws DAOException;
	
	void deleteAssociatedFpEgressPointIdRef(Integer monitorId, Integer fpNodeId) throws DAOException;
	
	void deleteAssociatedFpEgressPointIdsByMonitorId(Integer monitorId) throws DAOException;
	
	List<String> retrieveAssociatedFpEuEpaEmuIdsByMonitorId(Integer monitorId) throws DAOException;
	
	List<String> retrieveAssociatedFpReleasePointIdsByMonitorId(Integer monitorId) throws DAOException;
	
	ContinuousMonitorEqt createContinuousMonitorEqtWithCreatedDt(ContinuousMonitorEqt cme) throws DAOException;
	
	String retrieveNextMonId(Integer fpId) throws DAOException;
	
	ContinuousMonitor retrieveContinuousMonitorByFpIdAndCorrId(Integer fpId, 
			Integer corrMonitorId) throws DAOException;
	
	Map<Integer, String> facilitiesWithMatchingContinuousMonitorEqt(String manufacturerName,
			String modelNumber, String serialNumber) throws DAOException;
	
	List<Integer> retrieveCorrelatedMonitorIds(Integer corrMonitorId, Integer monitorId)
		throws DAOException;
	
}