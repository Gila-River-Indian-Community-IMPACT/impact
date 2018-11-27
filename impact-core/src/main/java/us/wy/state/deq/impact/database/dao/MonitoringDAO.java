package us.wy.state.deq.impact.database.dao;

import java.sql.Timestamp;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.TransactableDAO;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceAmbientMonitorLineItem;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.database.dbObjects.monitoring.Monitor;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroup;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroupNote;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorNote;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorReport;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorSite;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorSiteNote;

public interface MonitoringDAO extends TransactableDAO {

	MonitorSite[] searchMonitorSites(MonitorSite searchObj) throws DAOException;
	
	MonitorSite[] searchMonitorSites(MonitorSite searchObj,
			boolean excludeFacilityOwned) throws DAOException;

	MonitorSite retrieveMonitorSite(Integer siteId) throws DAOException;

	boolean modifyMonitorSite(MonitorSite monitorSite) throws DAOException;

	void deleteMonitorSite(MonitorSite monitorSite) throws DAOException;

	MonitorGroup createMonitorGroup(MonitorGroup monitorGroup) throws DAOException;

	MonitorGroup retrieveMonitorGroup(Integer groupId) throws DAOException;

	boolean modifyMonitorGroup(MonitorGroup monitorGroup) throws DAOException;

	void deleteMonitorGroup(MonitorGroup monitorGroup) throws DAOException;

	MonitorSite createMonitorSite(MonitorSite monitorSite) throws DAOException;

	MonitorGroup[] searchMonitorGroups(MonitorGroup searchObj) throws DAOException;
	
	MonitorGroup[] searchMonitorGroups(MonitorGroup searchObj,
			boolean excludeFacilityOwned) throws DAOException;

	Monitor createMonitor(Monitor monitor) throws DAOException;

	Monitor[] searchMonitors(Monitor searchObj) throws DAOException;

	Monitor retrieveMonitor(Integer monitorId) throws DAOException;

	boolean modifyMonitor(Monitor monitor) throws DAOException;

	void deleteMonitor(Monitor monitor) throws DAOException;

	Monitor[] searchMonitorsByAqsId(String aqsSiteId, Integer siteId) throws DAOException;
	
	// monitor group attachments
	
	public Attachment createMonitorGroupAttachment(Integer groupId, Attachment attachment) throws DAOException;
	
	public Attachment[] retrieveMonitorGroupAttachments(Integer groupId) throws DAOException;
	
	public Attachment updateMonitorGroupAttachment(Attachment attachment) throws DAOException;
	
	public void removeMonitorGroupAttachment(Attachment attachment) throws DAOException;
	
	// Monitor Group Notes
	
	void removeMonitorGroupNotes(int monitorGroupId) throws DAOException;

	MonitorGroupNote[] retrieveMonitorGroupNotes(int monitorGroupId)
			throws DAOException;

	void createMonitorGroupNote(Integer monitorGroupId, Integer noteId)
			throws DAOException;
	
	// Monitor Site Notes
			
	void removeMonitorSiteNotes(int monitorSiteId) throws DAOException;

	MonitorSiteNote[] retrieveMonitorSiteNotes(int monitorSiteId)
			throws DAOException;

	void createMonitorSiteNote(Integer monitorSiteId, Integer noteId)
			throws DAOException;
	
	// Monitor Notes
			
	void removeMonitorNotes(int monitorId) throws DAOException;

	MonitorNote[] retrieveMonitorNotes(int monitorId)
			throws DAOException;

	void createMonitorNote(Integer monitorId, Integer noteId)
			throws DAOException;
	
	// monitor report
	public MonitorReport createMonitorReport(MonitorReport monitorReport) throws DAOException;
	
	public MonitorReport[] retrieveMonitorReports(Integer monitorGroupId) throws DAOException;
	
	public MonitorReport retrieveMonitorReport(Integer reportId) throws DAOException;
	
	public boolean modifyMonitorReport(MonitorReport monitorReport) throws DAOException;
	
	public void deleteMonitorReport(MonitorReport monitorReport) throws DAOException;
	
	// monitor report attachments
	
	public Attachment createMonitorReportAttachment(Integer reportId, Attachment attachment) throws DAOException;
	
	public Attachment[] retrieveMonitorReportAttachments(Integer reportId) throws DAOException;
	
	public Attachment updateMonitorReportAttachment(Attachment attachment) throws DAOException;
	
	public void removeMonitorReportAttachment(Attachment attachment) throws DAOException;

	boolean checkMonitorReportsValid(Integer groupId) throws DAOException;

	List<FceAmbientMonitorLineItem> searchFacilityMonitorsByDate(String facilityId, Timestamp startDate, Timestamp endDate)
			throws DAOException;
}
