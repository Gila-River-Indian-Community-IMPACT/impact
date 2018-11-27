package us.wy.state.deq.impact.bo;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitorEqt;

/**
 * Service interface for Continuous Monitor.
 */
public interface ContinuousMonitorService {

	public us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor createContinuousMonitor(
			us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor continuousMonitor)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException,
			us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

	public Integer deleteContinuousMonitor(
			us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor continuousMonitor)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public Facility modifyContinuousMonitor(
			us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor continuousMonitor,
			List<Integer> fpEuIdsToAssociate,
			List<Integer> fpEuIdsToDisassociate,
			List<Integer> fpEgressPointIdsToAssociate,
			List<Integer> fpEgressPointIdsToDisassociate)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor retrieveContinuousMonitor(
			java.lang.Integer continuousMonitorId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	
	public us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor retrieveContinuousMonitor(
			java.lang.Integer continuousMonitorId,
			boolean staging)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public java.util.List<us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor> searchContinuousMonitors(
			java.lang.Integer fpId,
			java.lang.String facilityId, java.lang.String facilityName,
			java.lang.String doLaaCd, java.lang.String countyCd,
			java.sql.Timestamp addDt, java.sql.Timestamp endDt,
			java.lang.String monitorId, java.lang.String cmpId,
			boolean staging)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/*
	 * public
	 * java.util.List<us.wy.state.deq.impact.database.dbObjects.continuousMonitoring
	 * .ContinuousMonitorAttachment> retrieveContinuousMonitorAttachments( int
	 * monitorId) throws
	 * us.oh.state.epa.stars2.framework.exception.DAOException,
	 * java.rmi.RemoteException;
	 */
	public us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitorNote createContinuousMonitorNote(
			us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitorNote note)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public void modifyContinuousMonitorNote(
			us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitorNote note)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public java.util.List<us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitorNote> retrieveContinuousMonitorNotes(
			int continuousMonitorId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Create a new row in the Attachment table.
	 */
	/*
	 * public us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment
	 * createContinuousMonitorAttachment(
	 * us.wy.state.deq.impact.database.dbObjects
	 * .continuousMonitoring.ContinuousMonitor mon,
	 * us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment
	 * attachment, java.io.InputStream fileStream) throws
	 * us.oh.state.epa.stars2.framework.exception.DAOException,
	 * us.oh.state.epa.stars2.framework.exception.ValidationException,
	 * java.rmi.RemoteException;
	 * 
	 * public void removeContinuousMonitorAttachment(
	 * us.wy.state.deq.impact.database
	 * .dbObjects.continuousMonitoring.ContinuousMonitorAttachment doc) throws
	 * us.oh.state.epa.stars2.framework.exception.DAOException,
	 * java.rmi.RemoteException;
	 * 
	 * public void modifyContinuousMonitorAttachment(
	 * us.wy.state.deq.impact.database
	 * .dbObjects.continuousMonitoring.ContinuousMonitorAttachment doc) throws
	 * us.oh.state.epa.stars2.framework.exception.DAOException,
	 * java.rmi.RemoteException;
	 */
	public List<ContinuousMonitorEqt> retrieveActiveContinuousMonitorEqtList(
			Integer continuousMonitorId) throws DAOException;
	
	public List<ContinuousMonitorEqt> retrieveContinuousMonitorEqtListNewestFirst(
			Integer continuousMonitorId) throws DAOException;
	
	public List<ContinuousMonitorEqt> retrieveContinuousMonitorEqtList(
			Integer continuousMonitorId, boolean staging) throws DAOException;

	public Integer createContinuousMonitorEqt(ContinuousMonitorEqt eae)
			throws DAOException, RemoteException;

	public Integer modifyContinuousMonitorEqt(ContinuousMonitorEqt eae)
			throws DAOException, RemoteException;

	public Integer removeContinuousMonitorEqt(ContinuousMonitorEqt eae)
			throws DAOException, RemoteException;

	public ContinuousMonitor[] retrieveContinuousMonitorByFpId(
			Integer fpId) throws DAOException;
	
	boolean createAssociatedFpEuIdRef(Integer monitorId, Integer emuId) throws DAOException;
	
	List<Integer> retrieveAssociatedFpEuIdsByMonitorId(Integer monitorId) throws DAOException;
	
	void updateAssociatedFpEuIds(Integer monitorId, List<Integer> fpEuIdsToAssociate,
			List<Integer> fpEuIdsToDisassoicate) throws DAOException;
	
	void deleteAssociatedFpEuIdRef(Integer monitorId, Integer emuId) throws DAOException;
	
	void deleteAssociatedFpEuIdsByMonitorId(Integer monitorId) throws DAOException;
	
	boolean createAssociatedFpEgressPointIdRef(Integer monitorId, Integer fpNodeId) throws DAOException;
	
	List<Integer> retrieveAssociatedFpEgressPointIdsByMonitorId(Integer monitorId) throws DAOException;
	
	void updateAssociatedFpEgressPointIds(Integer monitorId, List<Integer> fpEgressPointIdsToAssociate,
			List<Integer> fpEgressPointIdsToDisassociate) throws DAOException;
	
	void deleteAssociatedFpEgressPointIdRef(Integer monitorId, Integer fpNodeId) throws DAOException;
	
	void deleteAssociatedFpEgressPointIdsByMonitorId(Integer monitorId) throws DAOException;
	
	List<String> retrieveAssociatedFpEuEpaEmuIdsByMonitorId(Integer monitorId) throws DAOException;
	
	List<String> retrieveAssociatedFpReleasePointIdsByMonitorId(Integer monitorId) throws DAOException;
	
	ContinuousMonitor retrieveContinuousMonitorByFpIdAndCorrId(Integer fpId, 
			Integer corrMonitorId) throws DAOException;
	
	Map<Integer, String> facilitiesWithMatchingContinuousMonitorEqt(String manufacturerName,
			String modelNumber, String serialNumber) throws DAOException;

}