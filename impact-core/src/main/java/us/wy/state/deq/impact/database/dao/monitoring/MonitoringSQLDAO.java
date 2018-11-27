package us.wy.state.deq.impact.database.dao.monitoring;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceAmbientMonitorLineItem;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.wy.state.deq.impact.database.dao.MonitoringDAO;
import us.wy.state.deq.impact.database.dbObjects.monitoring.Monitor;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroup;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroupNote;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorNote;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorReport;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorSite;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorSiteNote;

@Repository
public class MonitoringSQLDAO extends AbstractDAO implements MonitoringDAO {

	@Override
	public MonitorSite[] searchMonitorSites(MonitorSite searchObj) throws DAOException {
		return searchMonitorSites(searchObj, false);
	}
	
	@Override
	public MonitorSite[] searchMonitorSites(MonitorSite searchObj,
			boolean excludeFacilityOwned) throws DAOException {

        StringBuffer statementSQL = new StringBuffer(loadSQL("MonitoringSQL.searchMonitorSites"));

        String mstId = searchObj.getMstId();
		if (!Utility.isNullOrEmpty(mstId)) {
			String format = "MST%06d";
			mstId = mstId.trim();
			int tempId;
			try {
				tempId = Integer.parseInt(mstId);
				mstId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}
		if (!Utility.isNullOrEmpty(mstId)) {
			statementSQL.append(" AND LOWER(mst.mst_id) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(mstId.replace("*", "%")));
			statementSQL.append("')");
		}

      if (searchObj.getSiteName() != null && searchObj.getSiteName().trim().length() > 0) {
	      statementSQL.append(" AND LOWER(mst.site_name) LIKE ");
	      statementSQL.append("LOWER('");
	      statementSQL.append(SQLizeString(searchObj.getSiteName().replace("*", "%")));
	      statementSQL.append("')");
	  }

      String mgrpId = searchObj.getMgrpId();
		if (!Utility.isNullOrEmpty(mgrpId)) {
			String format = "MGRP%06d";
			mgrpId = mgrpId.trim();
			int tempId;
			try {
				tempId = Integer.parseInt(mgrpId);
				mgrpId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}
		if (!Utility.isNullOrEmpty(mgrpId)) {
			statementSQL.append(" AND LOWER(mgrp.mgrp_id) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(mgrpId.replace("*", "%")));
			statementSQL.append("')");
		}

    if (searchObj.getGroupName() != null && searchObj.getGroupName().trim().length() > 0) {
	      statementSQL.append(" AND LOWER(mgrp.group_name) LIKE ");
	      statementSQL.append("LOWER('");
	      statementSQL.append(SQLizeString(searchObj.getGroupName().replace("*", "%")));
	      statementSQL.append("')");
	  }

		if (searchObj.getCounty() != null  && searchObj.getCounty().trim().length() > 0) {
			statementSQL.append(" AND mst.county_cd = '");
			statementSQL.append(SQLizeString(searchObj.getCounty()));
			statementSQL.append("'");
		}

      String facilityId = searchObj.getFacilityId();
		if (!Utility.isNullOrEmpty(facilityId)) {
			String format = "F%06d";
			facilityId = facilityId.trim();

			int tempId;
			try {
				tempId = Integer.parseInt(facilityId);
				facilityId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}
      
		if (!Utility.isNullOrEmpty(facilityId)) {
			statementSQL.append(" AND LOWER(f.facility_id) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(facilityId.replace("*", "%")));
			statementSQL.append("')");
        }
        if (searchObj.getFacilityName() != null
                && searchObj.getFacilityName().trim().length() > 0) {
            statementSQL.append(" AND LOWER(f.facility_nm) LIKE ");
            statementSQL.append("LOWER('");
            statementSQL.append(SQLizeString(searchObj.getFacilityName().replace(
                                                                               "*", "%")));
            statementSQL.append("')");
        }
        if (searchObj.getStatus() != null
                && searchObj.getStatus().trim().length() > 0) {
            statementSQL.append(" AND mst.status_cd = '");
            statementSQL.append(SQLizeString(searchObj.getStatus()));
            statementSQL.append("'");
        }
        if (searchObj.getCompanyId() != null) {
            statementSQL.append("AND c.cmp_id = '" + SQLizeString(searchObj.getCompanyId()) + "'");
        }
        if (searchObj.getGroupId() != null) {
            statementSQL.append("AND mgrp.group_id = " + searchObj.getGroupId());
        }
        
        if(excludeFacilityOwned) {
        	statementSQL.append("AND mgrp.aqd_owned = 'Y'");
        }

        statementSQL.append(" ORDER BY mst.site_id");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());

        ArrayList<MonitorSite> ret = connHandler.retrieveArray(MonitorSite.class);

        return ret.toArray(new MonitorSite[0]);
	}

	@Override
	public MonitorSite retrieveMonitorSite(Integer siteId) throws DAOException {

        StringBuffer statementSQL 
            = new StringBuffer(loadSQL("MonitoringSQL.searchMonitorSites"));
                                                     
        statementSQL.append(" AND mst.site_id = ?");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());

        connHandler.setInteger(1, siteId);

        return (MonitorSite) connHandler.retrieve(MonitorSite.class);
	}

	@Override
	public boolean modifyMonitorSite(MonitorSite monitorSite)
			throws DAOException {
        checkNull(monitorSite);

        ConnectionHandler connHandler 
            = new ConnectionHandler("MonitoringSQL.modifyMonitorSite", false);
                                                              
        int i = 1;

        connHandler.setString(i++, monitorSite.getSiteName());
        connHandler.setString(i++, monitorSite.getSiteObjective());
        connHandler.setString(i++, monitorSite.getAqsSiteId());
        connHandler.setString(i++, monitorSite.getCounty());
        connHandler.setInteger(i++, monitorSite.getGroupId());
        connHandler.setString(i++, 
        		AbstractDAO.translateBooleanToIndicator(monitorSite.isInAqs()));
        
        connHandler.setString(i++, monitorSite.getStatus());
        
        connHandler.setDate(i++, monitorSite.getStartDate() == null? 
        		null : new java.sql.Date(monitorSite.getStartDate().getTime()));
        connHandler.setDate(i++, monitorSite.getEndDate() == null? 
        		null : new java.sql.Date(monitorSite.getEndDate().getTime()));
        connHandler.setBigDecimal(i++, monitorSite.getLatitude());
        connHandler.setBigDecimal(i++, monitorSite.getLongitude());
        connHandler.setInteger(i++, monitorSite.getElevation());
        connHandler.setString(i++, monitorSite.getWyvisnet());
        connHandler.setInteger(i++, monitorSite.getLastModified() + 1);
        
        connHandler.setInteger(i++, monitorSite.getSiteId());
        connHandler.setInteger(i++, monitorSite.getLastModified());
        
        return connHandler.update();
	}

    public final void deleteMonitorSite(MonitorSite monitorSite)
            throws DAOException {

            ConnectionHandler connHandler 
                = new ConnectionHandler("MonitoringSQL.deleteMonitorSite", false);

            connHandler.setInteger(1, monitorSite.getSiteId());

            connHandler.remove();

            return;
        }

	@Override
	public MonitorGroup createMonitorGroup(MonitorGroup monitorGroup)
			throws DAOException {
        

		MonitorGroup ret = monitorGroup;
		ConnectionHandler connHandler
			= new ConnectionHandler("MonitoringSQL.createMonitorGroup", false);
		
		int i = 1;
		Integer groupId = nextSequenceVal("S_MONITOR_GROUP");
		connHandler.setInteger(i++, groupId);

		connHandler.setString(i++, monitorGroup.getGroupName());
		
		connHandler.setString(i++, 
				AbstractDAO.translateBooleanToIndicator(monitorGroup.isAqdOwned()));
		Integer companyId = 
				monitorGroup.getCmpId() == null? 
						null : Integer.valueOf(monitorGroup.getCmpId().split("CMP")[1]);
		connHandler.setInteger(i++, monitorGroup.getMonitorReviewerId());
		connHandler.setInteger(i++, companyId);
		connHandler.setString(i++, monitorGroup.getFacilityId());
		connHandler.setString(i++, monitorGroup.getDescription());
		connHandler.setString(i++, monitorGroup.getContractor());
		connHandler.update();
		
		ret.setGroupId(groupId);
		ret.setLastModified(1);
		
		return ret;
	}

	@Override
	public MonitorGroup retrieveMonitorGroup(Integer groupId)
			throws DAOException {

        StringBuffer statementSQL 
            = new StringBuffer(loadSQL("MonitoringSQL.searchMonitorGroups"));
                                                     
        statementSQL.append(" AND mgrp.group_id = ?");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());

        connHandler.setInteger(1, groupId);

        return (MonitorGroup) connHandler.retrieve(MonitorGroup.class);
	}

	@Override
	public boolean modifyMonitorGroup(MonitorGroup monitorGroup)
			throws DAOException {
        checkNull(monitorGroup);

        ConnectionHandler connHandler 
            = new ConnectionHandler("MonitoringSQL.modifyMonitorGroup", false);
                                                              
        int i = 1;

        connHandler.setString(i++, monitorGroup.getGroupName());
        connHandler.setString(i++, monitorGroup.getDescription());
		connHandler.setString(i++, 
				AbstractDAO.translateBooleanToIndicator(monitorGroup.isAqdOwned()));
		Integer companyId = 
				monitorGroup.getCmpId() == null? 
						null : Integer.valueOf(monitorGroup.getCmpId().split("CMP")[1]);
		connHandler.setInteger(i++, monitorGroup.getMonitorReviewerId());
		connHandler.setInteger(i++, companyId);
        connHandler.setString(i++, monitorGroup.getFacilityId());
        connHandler.setString(i++, monitorGroup.getContractor());
        connHandler.setInteger(i++, monitorGroup.getLastModified() + 1);
        
        connHandler.setInteger(i++, monitorGroup.getGroupId());
        connHandler.setInteger(i++, monitorGroup.getLastModified());
        
        return connHandler.update();
	}

	@Override
	public void deleteMonitorGroup(MonitorGroup monitorGroup)
			throws DAOException {
        ConnectionHandler connHandler 
            = new ConnectionHandler("MonitoringSQL.deleteMonitorGroup", false);

        connHandler.setInteger(1, monitorGroup.getGroupId());

        connHandler.remove();

        return;
	}

	@Override
	public MonitorSite createMonitorSite(MonitorSite monitorSite)
			throws DAOException {
        
		MonitorSite ret = monitorSite;
		ConnectionHandler connHandler
			= new ConnectionHandler("MonitoringSQL.createMonitorSite", false);
		
		int i = 1;
		Integer siteId = nextSequenceVal("S_MONITOR_SITE");
		connHandler.setInteger(i++, siteId);

		connHandler.setString(i++, monitorSite.getSiteName());
		connHandler.setString(i++, monitorSite.getSiteObjective());
		connHandler.setString(i++, monitorSite.getAqsSiteId());
		connHandler.setString(i++, monitorSite.getCounty());
		connHandler.setInteger(i++, monitorSite.getGroupId());
		connHandler.setString(i++, 
				AbstractDAO.translateBooleanToIndicator(monitorSite.isInAqs()));
		connHandler.setString(i++, monitorSite.getStatus());
		connHandler.setDate(i++, new java.sql.Date(monitorSite.getStartDate().getTime()));
		connHandler.setDate(i++, 
				monitorSite.getEndDate() == null? 
						null : new java.sql.Date(monitorSite.getEndDate().getTime()));
		connHandler.setBigDecimal(i++, monitorSite.getLatitude());
		connHandler.setBigDecimal(i++, monitorSite.getLongitude());
		connHandler.setInteger(i++, monitorSite.getElevation());
		connHandler.setString(i++, monitorSite.getWyvisnet());
		
		connHandler.update();
		
		ret.setSiteId(siteId);
		ret.setLastModified(1);
		
		return ret;
	}
	
	@Override
	public MonitorGroup[] searchMonitorGroups(MonitorGroup searchObj)
			throws DAOException {
		return searchMonitorGroups(searchObj, false);
	}

	@Override
	public MonitorGroup[] searchMonitorGroups(MonitorGroup searchObj, boolean excludeFacilityOwned)
			throws DAOException {
        StringBuffer statementSQL = new StringBuffer(loadSQL("MonitoringSQL.searchMonitorGroups"));

      String mgrpId = searchObj.getMgrpId();
		if (!Utility.isNullOrEmpty(mgrpId)) {
			String format = "MGRP%06d";
			mgrpId = mgrpId.trim();
			int tempId;
			try {
				tempId = Integer.parseInt(mgrpId);
				mgrpId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}
		if (!Utility.isNullOrEmpty(mgrpId)) {
			statementSQL.append(" AND LOWER(mgrp.mgrp_id) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(mgrpId.replace("*", "%")));
			statementSQL.append("')");
		}

    if (searchObj.getGroupName() != null && searchObj.getGroupName().trim().length() > 0) {
	      statementSQL.append(" AND LOWER(mgrp.group_name) LIKE ");
	      statementSQL.append("LOWER('");
	      statementSQL.append(SQLizeString(searchObj.getGroupName().replace("*", "%")));
	      statementSQL.append("')");
	  }

      String facilityId = searchObj.getFacilityId();
		if (!Utility.isNullOrEmpty(facilityId)) {
			String format = "F%06d";
			facilityId = facilityId.trim();
			int tempId;
			try {
				tempId = Integer.parseInt(facilityId);
				facilityId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}
      
		if (!Utility.isNullOrEmpty(facilityId)) {
			statementSQL.append(" AND LOWER(f.facility_id) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(facilityId.replace("*", "%")));
			statementSQL.append("')");
        }
        if (searchObj.getFacilityName() != null
            && searchObj.getFacilityName().trim().length() > 0) {
            statementSQL.append(" AND LOWER(f.facility_nm) LIKE ");
            statementSQL.append("LOWER('");
            statementSQL.append(SQLizeString(searchObj.getFacilityName().replace(
                                                                               "*", "%")));
            statementSQL.append("')");
        }
        if (searchObj.getCompanyId() != null) {
            statementSQL.append("AND c.cmp_id = '" + searchObj.getCompanyId() + "'");
        }
        if (searchObj.getGroupId() != null) {
            statementSQL.append("AND mgrp.group_id = '" + searchObj.getGroupId() + "'");
        }
        
        if (searchObj.getMonitorReviewerId() != null) {
            statementSQL.append("AND mgrp.monitor_reviewer = '" + searchObj.getMonitorReviewerId() + "'");
        }
        
        if(excludeFacilityOwned) {
        	statementSQL.append("AND mgrp.aqd_owned = 'Y'");
        }

        statementSQL.append(" ORDER BY mgrp.group_id");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());

        ArrayList<MonitorGroup> ret = connHandler.retrieveArray(MonitorGroup.class);

        return ret.toArray(new MonitorGroup[0]);
	}

	@Override
	public Monitor createMonitor(Monitor monitor) throws DAOException {
        
		Monitor ret = monitor;
		ConnectionHandler connHandler
			= new ConnectionHandler("MonitoringSQL.createMonitor", false);
		
		int i = 1;
		Integer monitorId = nextSequenceVal("S_MONITOR");
		connHandler.setInteger(i++, monitorId);
		connHandler.setInteger(i++, monitor.getSiteId());
		connHandler.setString(i++, monitor.getType());
		connHandler.setString(i++, monitor.getParameter());
		connHandler.setString(i++, monitor.getStatus());
		connHandler.setString(i++, monitor.getName());
		connHandler.setInteger(i++, monitor.getParameterCode());
		connHandler.setInteger(i++, monitor.getParameterOccurrenceCode());
		connHandler.setInteger(i++, monitor.getMethodCode());
		connHandler.setInteger(i++, monitor.getUnitCode());
		connHandler.setDate(i++, new java.sql.Date(monitor.getStartDate().getTime()));
		connHandler.setDate(i++, 
				monitor.getEndDate() == null? 
						null : new java.sql.Date(monitor.getEndDate().getTime()));
		connHandler.setString(i++, monitor.getDurationCode());
		connHandler.setString(i++, monitor.getFrequencyCode());
		connHandler.setString(i++, monitor.getComments());
		connHandler.setString(i++, monitor.getParameterMet());
		connHandler.update();
		
		ret.setMonitorId(monitorId);
		ret.setLastModified(1);
		
		return ret;
	}

	@Override
	public Monitor[] searchMonitors(Monitor searchObj) throws DAOException {

        StringBuffer statementSQL = new StringBuffer(loadSQL("MonitoringSQL.searchMonitors"));

	      if (searchObj.getMonitorId() != null) {
		      statementSQL.append("AND m.monitor_id = " + searchObj.getMonitorId());
		  }
	      if (searchObj.getSiteId() != null) {
		      statementSQL.append("AND m.site_id = " + searchObj.getSiteId());
		  }

        statementSQL.append(" ORDER BY m.monitor_id");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());

        ArrayList<Monitor> ret = connHandler.retrieveArray(Monitor.class);

        return ret.toArray(new Monitor[0]);
	}

	@Override
	public Monitor retrieveMonitor(Integer monitorId) throws DAOException {

        StringBuffer statementSQL 
            = new StringBuffer(loadSQL("MonitoringSQL.searchMonitors"));
                                                     
        statementSQL.append(" AND m.monitor_id = ?");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());

        connHandler.setInteger(1, monitorId);

        return (Monitor) connHandler.retrieve(Monitor.class);
	}

	@Override
	public boolean modifyMonitor(Monitor monitor) throws DAOException {
        checkNull(monitor);

        ConnectionHandler connHandler 
            = new ConnectionHandler("MonitoringSQL.modifyMonitor", false);
                                                              
        int i = 1;

		connHandler.setInteger(i++, monitor.getSiteId());
		connHandler.setString(i++, monitor.getType());
		connHandler.setString(i++, monitor.getParameter());
		connHandler.setString(i++, monitor.getStatus());
		connHandler.setString(i++, monitor.getName());
		connHandler.setInteger(i++, monitor.getParameterCode());
		connHandler.setInteger(i++, monitor.getParameterOccurrenceCode());
		connHandler.setInteger(i++, monitor.getMethodCode());
		connHandler.setInteger(i++, monitor.getUnitCode());
		connHandler.setDate(i++, new java.sql.Date(monitor.getStartDate().getTime()));
		connHandler.setDate(i++, 
				monitor.getEndDate() == null? 
						null : new java.sql.Date(monitor.getEndDate().getTime()));
		connHandler.setString(i++, monitor.getDurationCode());
		connHandler.setString(i++, monitor.getFrequencyCode());
		connHandler.setString(i++, monitor.getComments());
        connHandler.setInteger(i++, monitor.getLastModified() + 1);
        connHandler.setString(i++, monitor.getParameterMet());
        
		connHandler.setInteger(i++, monitor.getMonitorId());
        connHandler.setInteger(i++, monitor.getLastModified());

        return connHandler.update();
	}

	@Override
	public void deleteMonitor(Monitor monitor) throws DAOException {
        ConnectionHandler connHandler 
	        = new ConnectionHandler("MonitoringSQL.deleteMonitor", false);
	    connHandler.setInteger(1, monitor.getMonitorId());
	    connHandler.remove();
	
	    return;
	}

	@Override
	public Monitor[] searchMonitorsByAqsId(String aqsSiteId, Integer siteId)
			throws DAOException {
        ConnectionHandler connHandler 
	        = new ConnectionHandler("MonitoringSQL.searchMonitorsByAqsId", false);
	                                                          
	    int i = 1;
	
		connHandler.setString(i++, aqsSiteId);
		connHandler.setString(i++, siteId);
        ArrayList<Monitor> ret = connHandler.retrieveArray(Monitor.class);

        return ret.toArray(new Monitor[0]);
	}
	
	@Override
	public Attachment createMonitorGroupAttachment(Integer groupId, Attachment attachment)
			throws DAOException {
    	Attachment ret = attachment;

		ConnectionHandler connHandler = new ConnectionHandler(
				"MonitoringSQL.createMonitorGroupAttachment", false);

		int i = 1;
		connHandler.setInteger(i++, attachment.getDocumentID());
		connHandler.setInteger(i++, groupId);
		connHandler.setString(i++, attachment.getDocTypeCd());
		connHandler.setInteger(i++, 1);
		connHandler.update();

		ret.setLastModified(1);

		return ret;
    }

   @Override
	public Attachment[] retrieveMonitorGroupAttachments(Integer groupId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"MonitoringSQL.retrieveMonitorGroupAttachments", true);

		connHandler.setInteger(1, groupId);

		ArrayList<Attachment> ret = connHandler.retrieveArray(Attachment.class);

		return ret.toArray(new Attachment[0]);
	}
   
   @Override
	public Attachment updateMonitorGroupAttachment(Attachment attachment)
			throws DAOException {
		Attachment ret = attachment;

		ConnectionHandler connHandler = new ConnectionHandler(
				"MonitoringSQL.updateMonitorGroupAttachment", false);

		int i = 1;
		connHandler.setInteger(i++, attachment.getDocumentID());
		connHandler.setString(i++, attachment.getDocTypeCd());
		connHandler.setInteger(i++, attachment.getRefLastModified() + 1);
		connHandler.setInteger(i++, attachment.getDocumentID());
		connHandler.setInteger(i++, attachment.getRefLastModified());
		connHandler.update();

		return ret;
	}
   
   @Override
	public void removeMonitorGroupAttachment(Attachment attachment)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"MonitoringSQL.removeMonitorGroupAttachment", false);

		connHandler.setInteger(1, attachment.getDocumentID());
		connHandler.remove();
	}
	
	// Monitor Group Notes
	
	public void removeMonitorGroupNotes(int monitorGroupId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"MonitoringSQL.removeMonitorGroupNotes", false);
		connHandler.setInteger(1, monitorGroupId);
		connHandler.remove();
	}

	public final MonitorGroupNote[] retrieveMonitorGroupNotes(int monitorGroupID)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"MonitoringSQL.retrieveMonitorGroupNotes", true);
		connHandler.setInteger(1, monitorGroupID);
		ArrayList<MonitorGroupNote> ret = connHandler
				.retrieveArray(MonitorGroupNote.class);

		return ret.toArray(new MonitorGroupNote[0]);
	}

	public final void createMonitorGroupNote(Integer monitorGroupID, Integer noteId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"MonitoringSQL.createMonitorGroupNote", false);

		checkNull(monitorGroupID);
		checkNull(noteId);
		connHandler.setInteger(1, monitorGroupID);
		connHandler.setInteger(2, noteId);

		connHandler.update();
	}
	
	// Monitor Site Notes
	
	public void removeMonitorSiteNotes(int monitorSiteId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"MonitoringSQL.removeMonitorSiteNotes", false);
		connHandler.setInteger(1, monitorSiteId);
		connHandler.remove();
	}

	public final MonitorSiteNote[] retrieveMonitorSiteNotes(int monitorSiteID)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"MonitoringSQL.retrieveMonitorSiteNotes", true);
		connHandler.setInteger(1, monitorSiteID);
		ArrayList<MonitorSiteNote> ret = connHandler
				.retrieveArray(MonitorSiteNote.class);

		return ret.toArray(new MonitorSiteNote[0]);
	}

	public final void createMonitorSiteNote(Integer monitorSiteID, Integer noteId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"MonitoringSQL.createMonitorSiteNote", false);

		checkNull(monitorSiteID);
		checkNull(noteId);
		connHandler.setInteger(1, monitorSiteID);
		connHandler.setInteger(2, noteId);

		connHandler.update();
	}
	
	// Monitor Notes
	
	public void removeMonitorNotes(int monitorId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"MonitoringSQL.removeMonitorNotes", false);
		connHandler.setInteger(1, monitorId);
		connHandler.remove();
	}

	public final MonitorNote[] retrieveMonitorNotes(int monitorID)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"MonitoringSQL.retrieveMonitorNotes", true);
		connHandler.setInteger(1, monitorID);
		ArrayList<MonitorNote> ret = connHandler
				.retrieveArray(MonitorNote.class);

		return ret.toArray(new MonitorNote[0]);
	}

	public final void createMonitorNote(Integer monitorID, Integer noteId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"MonitoringSQL.createMonitorNote", false);

		checkNull(monitorID);
		checkNull(noteId);
		connHandler.setInteger(1, monitorID);
		connHandler.setInteger(2, noteId);

		connHandler.update();
	}
	
	// monitor reports
	
	@Override
	public MonitorReport createMonitorReport(MonitorReport monitorReport)
		throws DAOException {
		
		MonitorReport ret = monitorReport;
		ConnectionHandler connHandler
			= new ConnectionHandler("MonitoringSQL.createMonitorReport", false);
		
		int i = 1;
		Integer reportId = nextSequenceVal("S_MONITOR_RPT", monitorReport.getReportId());
		connHandler.setInteger(i++, reportId);
		connHandler.setInteger(i++, monitorReport.getMonitorGroupId());
		connHandler.setString(i++, monitorReport.getReportTypeCd());
		connHandler.setInteger(i++, monitorReport.getYear());
		connHandler.setInteger(i++, monitorReport.getQuarter());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(monitorReport.isLegacyReport()));
		connHandler.setString(i++, monitorReport.getDescription());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(monitorReport.isValidated()));
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(monitorReport.isSubmitted()));
		connHandler.setTimestamp(i++, monitorReport.getSubmittedDate());
		connHandler.setInteger(i++, monitorReport.getMonitoringReviewerId());
		connHandler.setTimestamp(i++, monitorReport.getMonitoringReviewDate());
		connHandler.setInteger(i++, monitorReport.getComplianceReviewerId());
		connHandler.setTimestamp(i++, monitorReport.getComplianceReviewDate());
		connHandler.setString(i++, monitorReport.getReviewStatusCd());
		connHandler.setString(i++, monitorReport.getPermitNumber());
		connHandler.setString(i++, monitorReport.getComplianceStatusCd());
		connHandler.setString(i++, monitorReport.getReportAcceptedCd());
		connHandler.setString(i++, monitorReport.getAqdComments());
		connHandler.setInteger(i++, monitorReport.getCreatedById());

		connHandler.update();

		ret.setStaging(CommonConst.STAGING_SCHEMA.equals(getSchema()));
		ret.setReportId(reportId);
		ret.setLastModified(1);
		
		return ret;
	}
	
	@Override
	public MonitorReport[] retrieveMonitorReports(Integer monitorGroupId) throws DAOException {

        StringBuffer statementSQL 
            = new StringBuffer(loadSQL("MonitoringSQL.retrieveMonitorReportsByGroupId"));
        
        StringBuffer whereClause = new StringBuffer();
    	// additional where clauses for IMPACT public
    	if(CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
    		// retrieve only if ambient monitor report is submitted 
    		whereClause.append(" AND mr.submitted = 'Y'");
    	}
    	if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP) && CommonConst.READONLY_SCHEMA.equals(getSchema())){
    		whereClause.append(" AND mr.submitted = 'Y'");
    	}
    	
    	statementSQL.append(whereClause);
    	
        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());

        connHandler.setInteger(1, monitorGroupId);
        
        ArrayList<MonitorReport> ret = connHandler.retrieveArray(MonitorReport.class);
        for (MonitorReport mrpt : ret) {
        	mrpt.setStaging(CommonConst.STAGING_SCHEMA.equals(getSchema()));
        }
        return ret.toArray(new MonitorReport[0]);
	}
	
	@Override
	public MonitorReport retrieveMonitorReport(Integer reportId) throws DAOException {

        StringBuffer statementSQL 
            = new StringBuffer(loadSQL("MonitoringSQL.retrieveMonitorReportById"));
                                                     
        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());

        connHandler.setInteger(1, reportId);
        
        MonitorReport ret = (MonitorReport)connHandler.retrieve(MonitorReport.class);
        if (null != ret) {
        	ret.setStaging(CommonConst.STAGING_SCHEMA.equals(getSchema()));
        }
        return ret;
	}
	
	
	@Override
	public boolean modifyMonitorReport(MonitorReport monitorReport)
		throws DAOException {
		
		checkNull(monitorReport);
		
		ConnectionHandler connHandler
			= new ConnectionHandler("MonitoringSQL.modifyMonitorReport", false);
		
		int i = 1;
		
		connHandler.setInteger(i++, monitorReport.getMonitorGroupId());
		connHandler.setString(i++, monitorReport.getReportTypeCd());
		connHandler.setInteger(i++, monitorReport.getYear());
		connHandler.setInteger(i++, monitorReport.getQuarter());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(monitorReport.isLegacyReport()));
		connHandler.setString(i++, monitorReport.getDescription());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(monitorReport.isValidated()));
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(monitorReport.isSubmitted()));
		connHandler.setTimestamp(i++, monitorReport.getSubmittedDate());
		connHandler.setInteger(i++, monitorReport.getMonitoringReviewerId());
		connHandler.setTimestamp(i++, monitorReport.getMonitoringReviewDate());
		connHandler.setInteger(i++, monitorReport.getComplianceReviewerId());
		connHandler.setTimestamp(i++, monitorReport.getComplianceReviewDate());
		connHandler.setString(i++, monitorReport.getReviewStatusCd());
		connHandler.setString(i++, monitorReport.getPermitNumber());
		connHandler.setString(i++, monitorReport.getComplianceStatusCd());
		connHandler.setString(i++, monitorReport.getReportAcceptedCd());
		connHandler.setString(i++, monitorReport.getAqdComments());
		connHandler.setInteger(i++, monitorReport.getLastModified() + 1);
		connHandler.setInteger(i++, monitorReport.getReportId());
		connHandler.setInteger(i++, monitorReport.getLastModified());
		
		return connHandler.update();
		
	}
	
	@Override
	public final void deleteMonitorReport(MonitorReport monitorReport)
            throws DAOException {

            ConnectionHandler connHandler 
                = new ConnectionHandler("MonitoringSQL.deleteMonitorReport", false);

            connHandler.setInteger(1, monitorReport.getReportId());

            connHandler.remove();

            return;
    }	
	
	// monitor report attachments
	@Override
	public Attachment createMonitorReportAttachment(Integer reportId, Attachment attachment)
			throws DAOException {
    	Attachment ret = attachment;

		ConnectionHandler connHandler = new ConnectionHandler(
				"MonitoringSQL.createMonitorReportAttachment", false);

		int i = 1;
		connHandler.setInteger(i++, attachment.getDocumentID());
		connHandler.setInteger(i++, reportId);
		connHandler.setString(i++, attachment.getDocTypeCd());
		connHandler.setInteger(i++, 1);
		connHandler.update();

		ret.setLastModified(1);

		return ret;
    }

   @Override
	public Attachment[] retrieveMonitorReportAttachments(Integer reportId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"MonitoringSQL.retrieveMonitorReportAttachments", true);

		connHandler.setInteger(1, reportId);

		ArrayList<Attachment> ret = connHandler.retrieveArray(Attachment.class);

		return ret.toArray(new Attachment[0]);
	}
   
   @Override
	public Attachment updateMonitorReportAttachment(Attachment attachment)
			throws DAOException {
		Attachment ret = attachment;

		ConnectionHandler connHandler = new ConnectionHandler(
				"MonitoringSQL.updateMonitorReportAttachment", false);

		int i = 1;
		connHandler.setInteger(i++, attachment.getDocumentID());
		connHandler.setString(i++, attachment.getDocTypeCd());
		connHandler.setInteger(i++, attachment.getRefLastModified() + 1);
		connHandler.setInteger(i++, attachment.getDocumentID());
		connHandler.setInteger(i++, attachment.getRefLastModified());
		connHandler.update();

		return ret;
	}
   
   @Override
	public void removeMonitorReportAttachment(Attachment attachment)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"MonitoringSQL.removeMonitorReportAttachment", false);

		connHandler.setInteger(1, attachment.getDocumentID());
		connHandler.remove();
	}

	@Override
	public boolean checkMonitorReportsValid(Integer groupId) throws DAOException {

        StringBuffer statementSQL 
            = new StringBuffer(loadSQL("MonitoringSQL.checkMonitorReportsValid"));
                                                     
        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());

        connHandler.setInteger(1, groupId);
        
        Integer invalidCount = (Integer)connHandler.retrieveJavaObject(Integer.class);
        return invalidCount > 0? false : true;
	}

	@Override
	public List<FceAmbientMonitorLineItem> searchFacilityMonitorsByDate(String facilityId, Timestamp startDate, Timestamp endDate)
			throws DAOException {
		checkNull(facilityId);
		checkNull(startDate);
		checkNull(endDate);

		StringBuffer statementSQL = new StringBuffer(loadSQL("MonitoringSQL.searchFacilityMonitorSitesByDate"));

	    StringBuffer sortBy = new StringBuffer(" ORDER BY mst.site_id, m.monitor_id");

        statementSQL.append(sortBy.toString());
		
		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(statementSQL.toString());
		int i = 1;
		
		connHandler.setTimestamp(i++, formatBeginOfDay(endDate));
		connHandler.setTimestamp(i++, formatBeginOfDay(startDate));
		connHandler.setString(i++, facilityId);
		connHandler.setTimestamp(i++, formatBeginOfDay(endDate));
		connHandler.setTimestamp(i++, formatBeginOfDay(startDate));
		
		ArrayList<FceAmbientMonitorLineItem> ret = connHandler.retrieveArray(FceAmbientMonitorLineItem.class);

		return ret;
	}
}
