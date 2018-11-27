package us.wy.state.deq.impact.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.database.dao.ApplicationDAO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.PermitDAO;
import us.oh.state.epa.stars2.database.dbObjects.permit.TimeSheetRow;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Component
public class DataMigration {

    protected transient Logger logger = Logger.getLogger(this.getClass());

    @Autowired private PermitDAO readOnlyPermitDAO;
    
    @Autowired private InfrastructureDAO readOnlyInfrastructureDAO;
    
    @Autowired private ApplicationDAO readOnlyApplicationDAO;
    
	@PostConstruct
	public void init() {
		try {
			if (isTimesheetEntryEnabled() && !isAqdsTimesheetDataMigrated()) {
				runAqdsTimesheetMigration();
			}
		} catch (Exception e) {
			throw new RuntimeException("AQDS timesheet migration failed: ",e);
		}
	}

	private boolean isTimesheetEntryEnabled() {
        return (boolean)Config.getEnvEntry("app/timesheetEntryEnabled",false);
	}

	private boolean isAqdsTimesheetDataMigrated() throws DAOException {
		// disabled until needed again.
		
		//		Integer count = 
		//				getReadOnlyInfrastructureDAO().countAqdsTimesheetEntries();
		//		logger.debug("countAqdsTimesheetEntriesInImpact = " + count);
		//		return count > 0;
		return true;
	}

	@Transactional(rollbackFor = Exception.class)
	private void runAqdsTimesheetMigration() throws Exception {
		logger.debug("[Migration] ** Running AQDS timesheet data migration ... ");

		logger.debug("[Migration]  ---> Deleting existing timesheet entries ... ");
		getReadOnlyInfrastructureDAO().deleteAllTimesheetEntries();
		
		logger.debug("[Migration]  ---> Retrieving AQDS timesheet entries ... ");
		TimeSheetRow[] timesheetEntries =
				getReadOnlyInfrastructureDAO().retrieveAqdsNsrTimesheetEntries();
		logger.debug("[Migration] timesheetEntriesInAqds = " + timesheetEntries);
		

		Map<String,Boolean> appExists = new HashMap<String,Boolean>();
		
		for (int i = 0; i < timesheetEntries.length; i++) {
			if (null == appExists.get(timesheetEntries[i])) {
				Boolean exists = 
						getReadOnlyApplicationDAO().applicationExists(timesheetEntries[i]);
				appExists.put(timesheetEntries[i].getNsrId(), exists);
				logger.debug("[Migration] app number = " + timesheetEntries[i].getNsrId());
				logger.debug("[Migration] app exists = " + exists);
			}
			if (appExists.get(timesheetEntries[i].getNsrId())) {
				TimeSheetRow entry = timesheetEntries[i];
				entry.setFunction("NSR");
				entry.setSection("NSR");
	
				getReadOnlyInfrastructureDAO().assignImpactUser(entry);
				if (null == entry.getUserId()) {
					logger.warn("[Migration] AQDS timesheet employee unknown to IMPACT: " + entry.getAqdsEmployeeId());
					logger.warn("[Migration] Skipping AQDS timesheet entry: " + entry.getHoursEntryId());
					timesheetEntries[i] = null;
				}
				
				//set invoiced property
				Map<Integer,Timestamp> lastInvoiceReferenceDate = 
						getReadOnlyInfrastructureDAO().retrieveLastInvoiceReferenceDate(entry.getNsrId());
				if (!lastInvoiceReferenceDate.isEmpty()) {
					for (int permitId : lastInvoiceReferenceDate.keySet()) {
						Timestamp date = lastInvoiceReferenceDate.get(permitId);
						if (null != date) {
							if (date.after(entry.getDate())) {
								entry.setInvoiced(true);
								logger.debug("[Migration] invoiced date > entry date, (invoiced = true): " + entry.getNsrId());
							} else {
								logger.debug("[Migration] invoiced date < entry date (invoiced = false): " + entry.getNsrId());							
							}
						} else {
							logger.debug("[Migration] No last invoiced reference date for (invoiced = false): " + entry.getNsrId());						
						}
						logger.debug("[Migration] permitId = " + permitId);						
					}
				} else {
					logger.debug("[Migration] Cannot locate permit for application (invoiced = false): " + entry.getNsrId());
				}
			} else {
				logger.warn("[Migration] AQDS timesheet app number unknown to IMPACT: " + timesheetEntries[i].getAppNumber());
				logger.warn("[Migration] Skipping AQDS timesheet entry: " + timesheetEntries[i].getHoursEntryId());
				timesheetEntries[i] = null;
			}
		}
		timesheetEntries = removeNullElements(timesheetEntries);
		
		logger.debug("[Migration]  ---> Creating timesheet entries ... ");
		getReadOnlyInfrastructureDAO().createTimesheetEntries(timesheetEntries);
	}

	public static TimeSheetRow[] removeNullElements(final TimeSheetRow[] v) {
	    List<TimeSheetRow> list = new ArrayList<TimeSheetRow>(Arrays.asList(v));
	    list.removeAll(Collections.singleton(null));
	    return list.toArray(new TimeSheetRow[list.size()]);
	}

	public ApplicationDAO getReadOnlyApplicationDAO() {
		return readOnlyApplicationDAO;
	}

	public void setReadOnlyApplicationDAO(ApplicationDAO readOnlyApplicationDAO) {
		this.readOnlyApplicationDAO = readOnlyApplicationDAO;
	}

	public PermitDAO getReadOnlyPermitDAO() {
		return readOnlyPermitDAO;
	}

	public void setReadOnlyPermitDAO(PermitDAO readOnlyPermitDAO) {
		this.readOnlyPermitDAO = readOnlyPermitDAO;
	}

	public InfrastructureDAO getReadOnlyInfrastructureDAO() {
		return readOnlyInfrastructureDAO;
	}

	public void setReadOnlyInfrastructureDAO(
			InfrastructureDAO readOnlyInfrastructureDAO) {
		this.readOnlyInfrastructureDAO = readOnlyInfrastructureDAO;
	}

	
	
}
