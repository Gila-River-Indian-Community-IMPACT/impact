package us.oh.state.epa.stars2.bo;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceAmbientMonitorLineItem;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.database.dbObjects.monitoring.Monitor;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroup;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroupNote;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorNote;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorReport;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorSite;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorSiteNote;

public interface MonitoringService {

	List<Document> getPrintableDocumentList(MonitorReport monitorReport,
			String user, boolean readOnly) throws DAOException;

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
	
	Attachment createMonitorGroupAttachment(Integer groupId, Attachment attachment, InputStream fileStream) throws DAOException;
	
	Attachment[] retrieveMonitorGroupAttachments(Integer groupId) throws DAOException;
	
	Attachment updateMonitorGroupAttachment(Attachment attachment) throws DAOException;
	
	void removeMonitorGroupAttachment(Attachment attachment) throws DAOException;
	
	MonitorGroupNote[] retrieveMonitorGroupNotes(int monitorGroupId)
			throws DAOException;

	MonitorGroupNote createMonitorGroupNote(
			MonitorGroupNote monitorGroupNote) throws DAOException;

	boolean modifyMonitorGroupNote(MonitorGroupNote monitorGroupNote)
			throws DAOException;
			
	MonitorSiteNote[] retrieveMonitorSiteNotes(int monitorSiteId)
			throws DAOException;

	MonitorSiteNote createMonitorSiteNote(
			MonitorSiteNote monitorSiteNote) throws DAOException;

	boolean modifyMonitorSiteNote(MonitorSiteNote monitorSiteNote)
			throws DAOException;
	
	MonitorNote[] retrieveMonitorNotes(int monitorId)
			throws DAOException;

	MonitorNote createMonitorNote(
			MonitorNote monitorNote) throws DAOException;

	boolean modifyMonitorNote(MonitorNote monitorNote)
			throws DAOException;

	MonitorReport createMonitorReport(MonitorReport monitorReport) throws DAOException;
	
	MonitorReport[] retrieveMonitorReports(Integer monitorGroupId) throws DAOException;
	
	MonitorReport[] retrieveMonitorReports(Integer monitorGroupId, boolean includeStaging) throws DAOException;
	
	MonitorReport retrieveMonitorReport(Integer reportId) throws DAOException;
	
	MonitorReport retrieveMonitorReport(Integer reportId,boolean staging) throws DAOException;
	
	MonitorReport submitReport(MonitorReport monitorReport, Integer assignedUserId) throws DAOException;
	
	boolean modifyMonitorReport(MonitorReport monitorReport) throws DAOException;
	
	void deleteMonitorReport(MonitorReport monitorReport) throws DAOException;
	
	Attachment createMonitorReportAttachment(Integer reportId, Attachment attachment, InputStream fileStream) throws DAOException;
	
	Attachment[] retrieveMonitorReportAttachments(Integer reportId) throws DAOException;
	
	Attachment[] retrieveMonitorReportAttachments(Integer reportId, boolean includeStaging) throws DAOException;
	
	Attachment updateMonitorReportAttachment(Attachment attachment) throws DAOException;
	
	void removeMonitorReportAttachment(Attachment attachment) throws DAOException;

	boolean checkMonitorReportsValid(Integer groupId) throws DAOException;

	void createMonitorReportsFromGateway(List<MonitorReport> monitorReports) throws DAOException;
	
	public Attachment createMonitorReportAttachmentFromPortal(Integer reportId, Attachment attachment, boolean staging) throws DAOException;

	Collection<? extends Document> getPrintableAttachmentList(
			MonitorReport mrpt, boolean useReadonlyDB) throws DAOException;

	List<FceAmbientMonitorLineItem> searchFacilityMonitorsByDate(String facilityId, Timestamp startDate, Timestamp endDate)
			throws DAOException;

	void removeMonitorReportAttachment(Attachment attachment, boolean deleteAttachments) throws DAOException;

	void deleteMonitorReport(MonitorReport monitorReport, boolean deleteAttachmentFiles) throws DAOException;

}
