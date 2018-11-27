package us.oh.state.epa.stars2.bo;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.database.dao.ApplicationDAO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.PermitDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dao.permit.PermitSQLDAO;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsRptInfo;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.PTIOPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEU;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitInfo;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.FacilityRoleDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PTIORegulatoryStatus;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PermitStatusDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;

/**
 * <p>
 * Title: AnalysisBO
 * </p>
 * 
 * <p>
 * Description: This is the Business Object for Management Reports.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author Ken Bradley
 */
@Transactional(rollbackFor=Exception.class)
@Service
public class AnalysisBO extends BaseBO implements AnalysisService {
	
    /**
     * This method alerts AQD user belonging to the PER Admin system wide role
     * that PER reminder letters should be generated. It is expected that this
     * method will be called everyday via the scheduler (via a stub scheduler
     * routine).
     * 
     * This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-1:
     * 
     * PER reminder - STARS2 shall create a to-do list information message for 
     * each AQD user belonging to the 'PER admin' system wide role group to 
     * remind them that PER reminder letters and forms should be generated. 
     * STARS2 shall generate this message NN days prior to each of the four 
     * PER due dates established by DAPC. The value of NN shall be configurable 
     * as a system parameter with a default value of 45.
     * 
     * @return void
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="RequiresNew"
     */
    public void PERReminderNotice() throws DAOException {

        Integer interval = SystemPropertyDef.getSystemPropertyValueAsInteger("PERReminderInterval", null);
        
        Integer perReminderFpId = SystemPropertyDef.getSystemPropertyValueAsInteger("PERReminderFacility", null);
        
        String perAdminRole = SystemPropertyDef.getSystemPropertyValue("PERAdminUseCase", "analysisPerAdmin");
        
        InfrastructureDAO infraDao = infrastructureDAO();
        
        SimpleDef[] dueDates = infraDao.retrieveSimpleDefs("PA_PER_DUE_DATE_DEF", 
                                                           "PER_DUE_DATE_CD", 
                                                           "PER_DUE_DATE_DT",
                                                           null, null);

        String notes = "PER reminder letters and forms should be generated";
        String taskName = "PER Reminder";

        for (SimpleDef item : dueDates) {
            String key = item.getDescription();
            
            if (key == null)
                continue;
            if (key.equalsIgnoreCase("N/A") || key.length() == 0)
                continue;
            
            //String param = key;
            //int perMonth = getPERMonth(param);
            //int perDay = getPERDay(param);
            int perMonth = new Integer(key.substring(0, 2));
            int perDay = new Integer(key.substring(2, 4));
            
            /*
             * STARS2 shall generate this message NN days prior to each of the
             * four PER due dates established by DAPC. The value of NN shall be
             * configurable as a system parameter with a default value of 45.
             */
            Calendar now = new GregorianCalendar();
            Timestamp tNow = new Timestamp(now.getTimeInMillis());
            now.set(Calendar.MONTH, perMonth - 1);
            now.set(Calendar.DAY_OF_MONTH, perDay);
            now.set(Calendar.HOUR_OF_DAY, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);
            now.add(Calendar.DAY_OF_YEAR, interval * -1);
            Timestamp sd = new Timestamp(now.getTimeInMillis());
            now.add(Calendar.DAY_OF_YEAR, 1);
            Timestamp ed = new Timestamp(now.getTimeInMillis());
            
            if (tNow.after(sd) && tNow.before(ed)) {

                // Retrieve all users that are associated with the PER
                // Admin role and put an info message on their "To Do" list
                Integer[] users 
                    = infraDao.retrieveUsersWithUseCase(perAdminRole);

                if (users.length == 0)
                    break;
                
                try {
                    for (Integer ud : users)
                        createToDoInfoMessage(notes, taskName, perReminderFpId,
                                null, ud, null);

                } catch (RemoteException re) {
                    logger.error(re.getMessage(), re);
                    new DAOException(re.getMessage());
                }

                break;
            }
        }
        return;
    }

    /**
     * This method alerts AQD users that Title V Permits are about to expire for
     * their facilities. It is expected that this method will be called everyday
     * via the scheduler (via a stub scheduler routine).
     * 
     * This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-15:
     * 
     * Title V and SMTV FER Notice of Violation (NOV)/ Late Letter- STARS2 shall 
     * provide autonomous processing to alert AQD users of cases were a 
     * Title V FER or SMTV FER is required but no FER has been received.  
     * STARS2 shall search the set of Emissions Inventories objects for the most 
     * recent reporting year for those objects with a status of 
     * 'Emissions Inventory/Reminder Sent '. If there are any objects that meet 
     * this criteria, STARS2 shall create a to-do list information message for 
     * each AQD user belonging to the 'Emissions Reporting' system wide role 
     * group. The information message shall indicate whether there were 
     * Title V facilities with missing FERs, SMTV facilities with missing FERs, 
     * or both.
     * 
     * The analysis shall exclude facilities whose facility operational status 
     * is marked as 'Shutdown' and whose shutdown date is on or before 12/31 
     * of the reporting year. 
     * 
     * @return void
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="RequiresNew"
     */
    public void TVSMTVFERNoticeViolation() throws DAOException {

    	String erRole = SystemPropertyDef.getSystemPropertyValue("EmissionsReportingUseCase", "analysisEmissionsReporting");
        
        /*
         * Title V FER or SMTV FER is required but no FER has been received.
         * STARS2 shall search the set of Emissions Inventories objects for the most
         * recent reporting year for those objects with a status of 'Emissions
         * Report/Reminder Sent '. If there are any objects that meet this
         * criteria.
         * 
         * The analysis shall exclude facilities whose facility operational
         * status is marked as 'Shutdown' and whose shutdown date is on or
         * before 12/31 of the reporting year.
         */
        Calendar now = new GregorianCalendar();
        now.setTimeInMillis(System.currentTimeMillis());
        Integer year = now.get(Calendar.YEAR)-1;
        
        EmissionsRptInfo[] ers = facilityDAO().retrieveFERNoticeViolation(year);

        if (ers.length != 0) {
            StringBuffer sb = new StringBuffer("Following facility(s)"
                    + " has Emissions Inventory/Reminder Sent"
                    + " but no FER has been received for year :" + year
                    + ". Total number yearly reports found :" + ers.length);
            String taskName = "TVSMTVFERNoticeViolation";
            Integer epaFpId = null;
            for (EmissionsRptInfo er : ers) {
                if (epaFpId == null)
                    epaFpId = facilityDAO().retrieveFacility(er.getFacilityId()).getFpId();
                sb.append("\n");
                sb.append(er.getFacilityId());
                sb.append("  ");
            }
        
            String notes = sb.toString();
            // Retrieve all users that are associated with the Emissions
            // Reporting role and put an info message on their "To Do" list
            Integer[] users = infrastructureDAO().retrieveUsersWithUseCase(erRole);

            for (Integer ud : users)
                createToDoInfoMessage(notes, taskName, epaFpId,
                        null, ud, null);
        }
        return;
    }
    
    // Not currently used by AQD
    /**
     * This method alerts AQD users that PER reports are overdue for their
     * facilities. It is expected that this method will be called everyday via
     * the scheduler (via a stub scheduler routine).
     * 
     * This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-2:
     * 
     * PER overdue - STARS2 shall provide autonomous processing to alert AQD 
     * users of overdue PER reports. STARS2 shall create a to-do list information 
     * message for a designated AQD user (specified by a role in the facility 
     * profile) when STARS2 detects that exactly NN days or exactly (NN + 30) days 
     * have elapsed since that facility's PER due date and no corresponding PER 
     * report (based on the reporting period for the PER report) has been received.  
     * The value of NN shall be configurable as a system parameter with a 
     * default value of 15.
     * 
     * STARS2 shall exclude from this processing those facilities with a shutdown 
     * date that is greater than one year before the PER Due date.  For example, 
     * if the PER is due 06/30/08 and the facility was shutdown 03/15/07 then 
     * that facility wouldn't be included in the PER reminders anymore.  But if 
     * it was shutdown during the year the reporting is for, it should be included.
     * 
     * @return void
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="RequiresNew"
     */
    //public void PEROverdueNotice() throws DAOException {
        
    //    Integer days = getParameterValue("PEROverdueNoticeDay", 15);
    //    String facilityRoleCd = getParameterValue("PEROverdueNoticeFacilityRoleCd", "curr");
        
        /*
         * STARS2 detects that exactly NN days or exactly (NN + 30) days have
         * elapsed since that facility's PER due date and no corresponding PER
         * report (based on the reporting period for the PER report) has been
         * received. The value of NN shall be configurable as a system parameter
         * with a default value of 15.
         * 
         */
    //   SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
    //    Calendar now = new GregorianCalendar();
    //    now.setTimeInMillis(System.currentTimeMillis());
    //    Integer year = now.get(Calendar.YEAR)-1;
        /*
         * exactly NN days have 'elapsed' that
         * facility's PER due date
         */
    //    Calendar cDate = new GregorianCalendar(now.get(Calendar.YEAR), now
    //            .get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
    //    cDate.add(Calendar.DAY_OF_MONTH, (days * -1));
    //    String dueDateString = sdf.format(new Timestamp(cDate.getTimeInMillis()));
    //    String perDueDateCD = null;
    //    for (SelectItem s : PTIOPERDueDateDef.getDueDateMonthDay().getItems().getAllItems()){
    //        if (dueDateString.equalsIgnoreCase(s.getLabel())){
    //            perDueDateCD  = (String) s.getValue();
    //            break;
    //        }
    //    }
        
        /*
         * exactly exactly (NN + 30) days have 'elapsed' that
         * facility's PER due date
         */
    //   cDate.add(Calendar.DAY_OF_MONTH, (30 * -1));
    //    dueDateString = sdf.format(new Timestamp(cDate.getTimeInMillis()));
    //    perDueDateCD = null;
    //    for (SelectItem s : PTIOPERDueDateDef.getDueDateMonthDay().getItems().getAllItems()){
    //        if (dueDateString.equalsIgnoreCase(s.getLabel())){
    //            perDueDateCD  = (String) s.getValue();
    //            break;
    //        }
    //    }
    //    logger.debug("running PER overdue analysis for year: " + year + ", due date cd = " + perDueDateCD);
    //    workOnPerOverDue(dueDateString, perDueDateCD, year, facilityRoleCd);
    //    return;
    //}
    
    /**
     * STARS2 shall exclude from this processing those facilities with a
     * shutdown date that is greater than one year before the Reporting period
     * end date. For example, if the PER is due 06/30/08 and the facility was
     * shutdown 03/15/07 then that facility wouldn't be included in the PER
     * reminders anymore. But if it was shutdown during the year the reporting
     * is for, it should be included.
     * 
     * @param dueDateString
     * @param perDueDateCD
     * @param year
     * @param facilityRoleCd
     * @param wfBO
     * @throws DAOException
     */
    
    //private void workOnPerOverDue(String dueDateString, String perDueDateCD, 
    //        Integer year, String facilityRoleCd) 
    //        throws DAOException {
        
    //    if (perDueDateCD != null){
    //        Calendar now = new GregorianCalendar();
    //        int currentYear = now.get(Calendar.YEAR);
            
    //        String datePeriod = PTIOPERDueDateDef.getStartOfPeriod().getItems().getItemDesc(perDueDateCD);
    //        Integer month = new Integer(datePeriod.substring(0, 2));
    //        Integer day = new Integer(datePeriod.substring(2, 4));
    //        Calendar reportBeginDate = new GregorianCalendar(currentYear-1, month-1, day, 0, 0, 0);
            
    //        datePeriod = PTIOPERDueDateDef.getEndOfPeriod().getItems().getItemDesc(perDueDateCD);
    //        month = new Integer(datePeriod.substring(0, 2));
    //        day = new Integer(datePeriod.substring(2, 4));
    //        Calendar reportEndDate = new GregorianCalendar(currentYear, month-1, day, 23, 59, 59);
    //        Calendar perReportEndDate = new GregorianCalendar(currentYear, month-1, day, 0, 0, 0);
            
            // if end of period is in the future, move period back one year
     //       if (reportEndDate.after(now)) {
     //       	reportBeginDate.add(Calendar.YEAR, -1);
     //       	reportEndDate.add(Calendar.YEAR, -1);
     //       	perReportEndDate.add(Calendar.YEAR, -1);
     //       }
     //   }
     //}
    

    /**
     * This method alerts AQD users that TV Certs reports are overdue for their
     * facilities. It is expected that this method will be called everyday via
     * the scheduler (via a stub scheduler routine).
     * 
     * This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-3:
     * 
     * TV cert overdue - STARS2 shall provide autonomous processing to alert
     * AQD users of overdue TV cert reports. STARS2 shall create a to-do list
     * information message for a designated AQD user (specified by a role in
     * the facility inventory) when STARS2 detects that exactly NN days or exactly
     * (NN + 30) days have elapsed since that facility's TV cert due date and no
     * corresponding TV cert report (based on the reporting period for the TV
     * cert report) has been received. The value of NN shall be configurable as
     * a system parameter with a default value of 15.
     * 
     * STARS2 shall exclude from this processing those Facilities with a
     * shutdown date that is greater than one year before the end of the
     * reporting period (12/31 of the reporting year). For example, if the Title
     * V compliance certification is due 04/30/08 for 2007 compliance reporting,
     * and the facility was shutdown 03/15/07 then that facility would be
     * included in the Title V compliance certification reminders. But if it was
     * shutdown during 2006, it should not be included.
     * 
     * @return void
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="RequiresNew"
     */
    public void TVCertOverdueNotice() throws DAOException {
        Integer days = SystemPropertyDef.getSystemPropertyValueAsInteger("TVCertOverdueDay", 15);
        String facilityRoleCd = SystemPropertyDef.getSystemPropertyValue("TVCertOverdueFacilityRoleCd", FacilityRoleDef.TV_PERMIT_SUPERVISOR);
        
        /*
         * STARS2 detects that exactly NN days or exactly (NN + 30) days have
         * elapsed since that facility's TV cert due date and no corresponding
         * TV cert report (based on the reporting period for the TV cert report)
         * has been received.
         */
        SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
        Calendar now = new GregorianCalendar();
        now.setTimeInMillis(System.currentTimeMillis());
        Integer year = now.get(Calendar.YEAR)-1;
        /*
         * exactly NN days or exactly (NN + 30) days have 'elapsed' that
         * facility's TV cert due date
         */
        Calendar cDate = new GregorianCalendar(now.get(Calendar.YEAR), now
                .get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        cDate.add(Calendar.DAY_OF_MONTH, (days * -1));
        String dueDateString1 = sdf.format(new Timestamp(cDate.getTimeInMillis()));
        cDate.add(Calendar.DAY_OF_MONTH, (30 * -1));
        String dueDateString2 = sdf.format(new Timestamp(cDate.getTimeInMillis()));
        cDate = new GregorianCalendar(year, 0, 1, 0, 0, 0);
        Timestamp shutDownDateFrom = new Timestamp(cDate.getTimeInMillis());
        cDate = new GregorianCalendar(year, 11, 31, 23,
                59, 59);
        Timestamp shutDownDateTo = new Timestamp(cDate.getTimeInMillis());
        Facility[] fs = facilityDAO().retrieveTvCertOverdue(year,
                dueDateString1, dueDateString2, shutDownDateFrom,
                shutDownDateTo);
        for (Facility f : fs) {
            Integer[] users = workFlowDAO().retrieveUserIdsOfFacility(
                    f.getFpId(), facilityRoleCd);
            Integer taskUserId = 0;
            if (users != null && users.length != 0) {
                taskUserId = users[0];
            } else {
            	logger.error("No user roles found for fpId: " + f.getFpId() 
            			+ ", facilityId: " + f.getFacilityId() + ", roleCd: " + facilityRoleCd);
            }
            

            String dueDateString = f.getTvCertRepdueDate();
            String formattedDueDateStr = "N/A";
            if (dueDateString != null && dueDateString.length() == 4) {
                formattedDueDateStr = dueDateString.substring(0, 2) + "/" +
                    dueDateString.substring(2) + "/" + now.get(Calendar.YEAR);
            }
            String notes = "Facility " + f.getFacilityId() 
                    + " had a Title V compliance certification (TVCC) due on "
                    + formattedDueDateStr
                    + " but no TVCC was submitted for " + year;
            String taskName = "TV Cert Overdue Notice";
            createToDoInfoMessage(notes, taskName, f.getFpId(), null, taskUserId, null);
        }
        return;
    }

  /**
     * This method alerts AQD users that PTIO permits are expired for their
     * facilities. It is expected that this method will be called everyday via
     * the scheduler (via a stub scheduler routine).
     * 
     * This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-4:
     * 
     * PTIO expiration - STARS2 shall provide autonomous processing to update
     * the EU level permit status field of PTIO permits. For each PTIO/EU permit
     * with a permit status field of 'Active' and whose permit expiration date
     * is past STARS2 shall set the status field to either 'Extended' or
     * 'Expired'. If a renewal application for that EU has been received with an
     * application date later than the final issuance date and prior to the
     * expiration date of the PTIO permit, then STARS2 shall set the EU level
     * permit status field to 'Extended', else STARS2 shall set it to 'Expired'.
     * 
     * If STARS2 changes the permit status field to 'Expired', then it shall
     * create a to-do list information message for a designated AQD user
     * (specified by a role in the facility inventory) informing them of the
     * change in status
     * 
     * This method fullfils LSRD requirement U-22-Autonomous_Analyses-1-9:
     * 
     * "PTIO/FEPTIO expiration in Facility Inventory - STARS2 shall provide
     * autonomous processing to update the 'Current PTO/PTIO/Title V regulatory
     * status' fields in the EU object in the facility inventory. For each PTIO/EU
     * combination with an EU level permit status field of 'Active' and whose
     * permit expiration date is past, STARS2 shall set the facility inventory
     * field as follows:
     * 
     * If a PTIO/FEPTIO renewal application with an application date that falls
     * between the PTIO/FEPTIO's final issuance and expiration dates exists and
     * the facility operating status is not 'shutdown', then set the EU's
     * 'Current PTO/PTIO/Title V regulatory status' field to 'Extended PTIO' or
     * 'Extended FEPTIO' and set the EU level permit status field to 'Extended'
     * (overwrites previous value of 'Active'). Else set the 'Current
     * PTO/PTIO/Title V regulatory status' field to 'Expired PTIO' or 'Expired
     * FEPTIO' and set the EU level permit status field to 'Expired' (overwrites
     * previous value of 'Active').
     * 
     * If STARS2 changes the status field to 'Expired PTIO' or 'Expired FEPTIO',
     * then it shall create a to-do list information message for a designated
     * AQD user (specified by a role in the facility inventory) informing them of
     * the change.
     * 
     * @return void
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="RequiresNew"
     */
    public void PTIOExpirationNotice() throws DAOException {
        PermitDAO permitDAO = permitDAO();
        ApplicationDAO appDAO = applicationDAO();
        FacilityDAO facDAO = facilityDAO();

        String facilityRoleCd = SystemPropertyDef.getSystemPropertyValue("NSRExpirationNoticeFacilityRoleCd", FacilityRoleDef.NSR_PERMIT_ENGINEER);
        String applicationNumber = null;
        String permitType = PermitTypeDef.NSR;
        String permitReason = null;
        String legacyPermitNumber = null;
        String permitNumber = null;
        String facilityID = null;
        String facilityName = null;
        String permitStatusCd = PermitGlobalStatusDef.ISSUED_FINAL;
        String dateBy = PermitSQLDAO.EXPIRATION_DATE;
        Timestamp endDate = new Timestamp(System.currentTimeMillis());
        Timestamp beginDate = beginDateFromDate(endDate);
        logger.debug("Searching PTIO permits with expiration date on or after: " + beginDate);
        
        List<Permit> tempPermits = permitDAO.searchPermits(applicationNumber, null, null,
                permitType, permitReason, null, legacyPermitNumber, permitNumber, facilityID,
                facilityName, permitStatusCd, dateBy, beginDate, endDate, null, true, null);

        logger.debug("" + tempPermits.size() + " permits retrieved");
        if (tempPermits.size() > 0) {

            Transaction trans = TransactionFactory.createTransaction();
            permitDAO.setTransaction(trans);
            appDAO.setTransaction(trans);
            facDAO.setTransaction(trans);

            try {
                
                for (Permit tp : tempPermits) {
                    /*
                     * U-22-Autonomous_Analyses-1-9 If a PTIO/FEPTIO renewal
                     * application with an application date that falls between
                     * the PTIO/FEPTIO's final issuance and expiration dates
                     * exists set the EU's 'Current PTO/PTIO/Title V regulatory
                     * status' field to 'Extended PTIO' or 'Extended FEPTIO' and
                     * set the EU level permit status field to 'Extended'
                     * (overwrites previous value of 'Active'). Else set the
                     * 'Current PTO/PTIO/Title V regulatory status' field to
                     * 'Expired PTIO' or 'Expired FEPTIO' and set the EU level
                     * permit status field to 'Expired' (overwrites previous
                     * value of 'Active').
                     * 
                     * U-22-Autonomous_Analysis-1-4 If a renewal application for
                     * that EU has been received with an application date later
                     * than the final issuance date and prior to the expiration
                     * date of the PTIO permit, then STARS2 shall set the EU
                     * level permit status field to 'Extended', else STARS2
                     * shall set it to 'Expired'.
                     */
                    Permit permit = permitDAO.retrievePermit(tp.getPermitID());
                    /*
                     * U-22-Autonomous_Analysis-1-9: If a PTIO/FEPTIO
                     * renewal application exists and the facility operating
                     * status is not 'shutdown'
                     */
                    Facility f = facDAO.retrieveFacility(
                            permit.getFacilityId());
                    if (f.getOperatingStatusCd().equalsIgnoreCase(OperatingStatusDef.SD)) {
                        logger.debug("Facility " + permit.getFacilityId() + " is shut down. " +
                        		"Not processing permit " + permit.getPermitNumber());
                        continue;
                    }
                	logger.debug("Processing permit " + permit.getPermitNumber()) ;
                    
                    boolean info = false;
                    for (PermitEU pe : permit.getEus()) {
                        if (!pe.getPermitStatusCd().equalsIgnoreCase(
                                PermitStatusDef.ACTIVE))
                            continue;

                        EmissionUnit eu = f.getMatchingEmissionUnit(pe
                                .getCorrEpaEMUID());
                        if (eu == null) {
                            throw new NullPointerException(
                                    "Could not locate a current Emission Unit for "
                                            + " Constant EU ID = "
                                            + pe.getCorrEpaEMUID());
                        }

                        Integer renewalApp = appDAO
                                .retrieveRenewalApplicationByEU(pe
                                        .getCorrEpaEMUID(), permit
                                        .getFinalIssueDate(), permit
                                        .getExpirationDate());

                        if (renewalApp != null) {
                        	logger.debug("Marking EU " + eu.getEpaEmuId() + " as extended. " +
                        			"Renwal application id = " + renewalApp) ;
                            pe.setPermitStatusCd(PermitStatusDef.EXTENDED);

                            if (((PTIOPermit) permit).isFeptio()) {
                                eu.setPtioStatusCd(PTIORegulatoryStatus.EXTENDED_FEPTIO);
                            } else {
                                eu.setPtioStatusCd(PTIORegulatoryStatus.EXTENDED_PTIO);
                            }
                        } else {
                            /*
                             * U-22-Autonomous_Analysis-1-4 If a renewal
                             * application for If STARS2 changes the permit
                             * status field to 'Expired'
                             */
                        	logger.debug("Marking EU " + eu.getEpaEmuId() + " as expired") ;
                            pe.setPermitStatusCd(PermitStatusDef.EXPIRED);

                            /*
                             * U-22-Autonomous_Analyses-1-9 If a PTIO/FEPTIO
                             * renewal If STARS2 changes the status field to
                             * 'Expired PTIO' or 'Expired FEPTIO'
                             */
                            if (((PTIOPermit) permit).isFeptio()) {
                                eu.setPtioStatusCd(PTIORegulatoryStatus.EXPIRED_FEPTIO);
                            } else {
                                eu.setPtioStatusCd(PTIORegulatoryStatus.EXPIRED_PTIO);
                            }
                            /*
                             * it shall create a to-do list information message
                             * for a designated AQD user (specified by a role
                             * in the facility inventory) informing them of the
                             * change in status
                             */
                            info = true;
                        }
                        permitDAO.modifyPermitEU(pe);
                        facDAO.modifyEmissionUnit(eu);
                    }
                    if (info) {
                        String notes = "Permit " + permit.getPermitNumber() +
                            " has expired and no renewal application was submitted. " +
                            "They may be operating the equipment without a permit.";
                        String taskName = "PTIO Expiration Notice";
                        Integer taskUserId = f.getFacilityRoles().get(
                                facilityRoleCd).getUserId();
                        createToDoInfoMessage(notes, taskName, f.getFpId(),
                                permit.getPermitID(), taskUserId, trans);
                        info = false;
                    }
                                        
                }
                trans.complete();
            } catch (DAOException de) {
                cancelTransaction(trans, de);
            } finally {
                closeTransaction(trans);
            }
        }

        return;
    }

    /**
     * 
     * This method fullfils LSRD requirement U-22-Autonomous_Analyses-1-9:
     * 
     * "PTIO/FEPTIO expiration in Facility Inventory - STARS2 shall provide
     * autonomous processing to update the 'Current PTO/PTIO/Title V regulatory
     * status' fields in the EU object in the facility inventory. For each PTIO/EU
     * combination with an EU level permit status field of 'Active' and whose
     * permit expiration date is past, STARS2 shall set the facility inventory
     * field as follows:
     * 
     * If a PTIO/FEPTIO renewal application with an application date that falls
     * between the PTIO/FEPTIO's final issuance and expiration dates exists and
     * the facility operating status is not 'shutdown', then set the EU's
     * 'Current PTO/PTIO/Title V regulatory status' field to 'Extended PTIO' or
     * 'Extended FEPTIO' and set the EU level permit status field to 'Extended'
     * (overwrites previous value of 'Active'). Else set the 'Current
     * PTO/PTIO/Title V regulatory status' field to 'Expired PTIO' or 'Expired
     * FEPTIO' and set the EU level permit status field to 'Expired' (overwrites
     * previous value of 'Active').
     * 
     * If STARS2 changes the status field to 'Expired PTIO' or 'Expired FEPTIO',
     * then it shall create a to-do list information message for a designated
     * AQD user (specified by a role in the facility inventory) informing them of
     * the change."
     * 
     * 
     * @return void
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="RequiresNew"
     */
    public void PTIOExpirationFacilityProfile() throws DAOException {
        /*
         * SELECT UNIQUE pp.permit_id, ffrx.user_id, pp.expiration_date,
         * ppi.issuance_date, pe.facility_eu_id FROM pt_ptio_permit ppp,
         * pt_permit pp, pt_eu pe, pt_eu_group peg, pt_permit_issuance ppi,
         * fp_facility_role_xref ffrx WHERE ppp.permit_id = pp.permit_id AND
         * pp.permit_id = peg.permit_id AND pp.permit_id = ppi.permit_id AND
         * ppi.issuance_type_cd = 'F' AND ppi.issuance_status_cd = 'I' AND
         * peg.permit_eu_group_id = pe.permit_eu_group_id AND
         * pe.permit_status_cd = 'A' AND pp.facility_id = ffrx.facility_id AND
         * ffrx.facility_role_cd = 'perw' AND pp.expiration_date < '2007-10-04'
         * 
         * SELECT pa.application_id FROM pa_application pa, pa_eu pe WHERE
         * pe.emu_id = ? AND pe.application_id = pa.application_id AND
         * pa.submitted_date > ? 'issuance_date' AND pa.submitted_date < ?
         * 'expiration_date'
         */
        return;
    }
    
    /**
     * This method alerts AQD users that state PTO permits are expired for
     * their facilities. It is expected that this method will be called everyday
     * via the scheduler (via a stub scheduler routine).
     * 
     * This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-5:
     * 
     * State PTO expiration - STARS2 shall provide autonomous processing to update 
     * the EU level permit status field of state PTO permits. For each state 
     * PTO/EU permit with a permit status field of 'Active' and whose permit 
     * expiration date is past STARS2 shall set the status field to either 
     * 'Extended' or 'Expired'. If a renewal application for that EU has been 
     * received with an application date later than the final issuance date 
     * and prior to the expiration date of the PTIO permit, then STARS2 shall 
     * set the EU level permit status field to 'Extended', else STARS2 shall 
     * set it to 'Expired'.
     * 
     * If STARS2 changes the permit status field to 'Expired', then it shall 
     * create a to-do list information message for a designated AQD user 
     * (specified by a role in the facility inventory) informing them of the 
     * change in status
     * 
     * This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-10:
     * 
     * State PTO/FESOP expiration in Facility Inventory - STARS2 shall provide 
     * autonomous processing to update the 'Current PTO/PTIO/Title V regulatory 
     * status' fields in the EU object in the facility inventory. For each State 
     * PTO(including FESOPs)/EU combination with an EU level permit status field 
     * of 'Active' and whose permit expiration date is past, STARS2 shall set 
     * the facility inventory field as follows:
     * 
     * If a PTIO/FEPTIO renewal application with an application date that falls 
     * between the State PTO/FESOP's final issuance and expiration dates exists 
     * and the facility operating status is not 'shutdown', then set the EU's  
     * 'Current PTO/PTIO/Title V regulatory status' field to 'Extended PTO' or 
     * 'Extended FESOP' and set the EU level permit status field to 'Extended' 
     * (overwrites previous value of  'Active').  Else set the 'Current 
     * PTO/PTIO/Title V regulatory status' field to 'Expired PTO' or 
     * 'Expired FESOP' and set the EU level permit status field to 'Expired' 
     * (overwrites previous value of  'Active').
     * 
     * If STARS2 changes the status field to 'Expired PTO' or 'Expired FESOP', 
     * then it shall create a to-do list information message for a designated 
     * AQD user (specified by a role in the facility inventory) informing them 
     * of the change.
     * 
     * @return void
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    /*
    public void StatePTOExpirationNotice() throws DAOException {
        PermitDAO permitDAO = permitDAO();
        ApplicationDAO appDAO = applicationDAO();
        FacilityDAO facDAO = facilityDAO();
*/
        /*
         * This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-5:
         * 
         * State PTO expiration - STARS2 shall provide autonomous processing to
         * update the EU level permit status field of state PTO permits. For
         * each state PTO/EU permit with a permit status field of 'Active' and
         * whose permit expiration date is past
         */
/*
        String applicationNumber = null;
        String permitType = PermitTypeDef.SPTO;
        String permitReason = null;
        String permitNumber = null;
        String facilityID = null;
        String facilityName = null;
        String permitStatusCd = PermitGlobalStatusDef.ISSUED_FINAL;
        String dateBy = PermitSQLDAO.EXPIRATION_DATE;
        Timestamp endDate = new Timestamp(System.currentTimeMillis());
        Timestamp beginDate = beginDateFromDate(endDate);
        logger.debug("Searching SPTO permits with expiration date on or after: " + beginDate);

        List<Permit> tempPermits = permitDAO.searchPermits(applicationNumber,null, null,
                permitType, permitReason, permitNumber, facilityID,
                facilityName, permitStatusCd, dateBy, beginDate, endDate, null, true);

        logger.debug("" + tempPermits.size() + " permits retrieved");
        if (tempPermits.size() > 0) {
            for (Permit tp : tempPermits) {
                try {

                    Permit permit = permitDAO.retrievePermit(tp.getPermitID());
                    Facility f = facDAO.retrieveFacility(
                            permit.getFacilityId());
*/
                    /*
                     * U-22-Autonomous_Analysis-1-10: If a PTIO/FEPTIO
                     * renewal application exists and the facility operating
                     * status is not 'shutdown'
                     */
/*
                    if (f.getOperatingStatusCd().equalsIgnoreCase(OperatingStatusDef.SD)) {
                        logger.debug("Facility " + permit.getFacilityId() + " is shut down. " +
                        		"Not processing permit " + permit.getPermitNumber());
                        continue;
                    }
                	logger.debug("Processing permit " + permit.getPermitNumber()) ;

                    boolean info = false;
                    List<PermitEU> eusToModify = new ArrayList<PermitEU>();
                    for (PermitEU pe : permit.getEus()) {
*/
                        /*
                         * U-22-Autonomous_Analysis-1-5:
                         * For each state PTO/EU permit with a permit status
                         * field of 'Active' 
                         */
/*
                        if (!pe.getPermitStatusCd().equalsIgnoreCase(
                                PermitStatusDef.ACTIVE))
                            continue;

                        EmissionUnit eu = f.getMatchingEmissionUnit(pe
                                .getCorrEpaEMUID());
                        if (eu == null) {
                            throw new NullPointerException(
                                    "Could not locate a current Emission Unit for "
                                    + " Constant EU ID = "
                                    + pe.getCorrEpaEMUID());
                        }
*/

                        /*
                         * U-22-Autonomous_Analysis-1-5:
                         * If a renewal application for that EU has been
                         * received with an application date later than the
                         * final issuance date and prior to the expiration date
                         * of the PTIO permit
                         * 
                         * U-22-Autonomous_Analysis-1-10:
                         * If a PTIO/FEPTIO renewal application with an
                         * application date that falls between the State
                         * PTO/FESOP's final issuance and expiration dates
                         */
    /*
                        Integer renewalApp = appDAO
                        .retrieveRenewalApplicationByEU(pe
                                .getCorrEpaEMUID(), permit
                                .getFinalIssueDate(), permit
                                .getExpirationDate());
     */

                        /*
                         * U-22-Autonomous_Analysis-1-5: STARS2 shall set the
                         * status field to either 'Extended' or 'Expired'. If a
                         * renewal application for that EU has been received,
                         * then STARS2 shall set the EU level permit status
                         * field to 'Extended', else STARS2 shall set it to
                         * 'Expired'.
                         * 
                         * U-22-Autonomous_Analysis-1-10: If a PTIO/FEPTIO
                         * renewal application exists, then set the EU's 'Current
                         * PTO/PTIO/Title V regulatory status' field to
                         * 'Extended PTO' or 'Extended FESOP' and set the EU
                         * level permit status field to 'Extended' (overwrites
                         * previous value of 'Active'). Else set the 'Current
                         * PTO/PTIO/Title V regulatory status' field to 'Expired
                         * PTO' or 'Expired FESOP' and set the EU level permit
                         * status field to 'Expired' (overwrites previous value
                         * of 'Active').
                         */
    /*
                        if (renewalApp != null) {
                        	logger.debug("Marking EU " + eu.getEpaEmuId() + " as extended. " +
                        			"Renwal application id = " + renewalApp) ;
                            pe.setPermitStatusCd(PermitStatusDef.EXTENDED);

                            if (((PTIOPermit) permit).isFeptio()) {
                                eu.setPtioStatusCd(PTIORegulatoryStatus.EXTENDED_FESOP);
                            } else {
                                eu.setPtioStatusCd(PTIORegulatoryStatus.EXTENDED_PTO);
                            }
                        } else {
                        	logger.debug("Marking EU " + eu.getEpaEmuId() + " as expired") ;
                            pe.setPermitStatusCd(PermitStatusDef.EXPIRED);
*/
                            /*
                             * U-22-Autonomous_Analysis-1-5:
                             * If STARS2 changes the permit status field to
                             * 'Expired', then it shall create a to-do list
                             * information message for a designated AQD user
                             * (specified by a role in the facility inventory)
                             * informing them of the change in status
                             */
                            // Mantis 2979 - don't create info task if EU
                            // has a permit status equal to PBR
    /*
                            info = !PTIORegulatoryStatus.PBR.equals(eu.getPtioStatusCd());
                        }
                        eusToModify.add(pe);
                    }

                    if (eusToModify.size() > 0) {
                    	logger.debug("Updating status for permit " + permit.getPermitNumber());
                    	updateSPTOEUInformation(permit, f, eusToModify, info);
                    }
                } catch (DAOException de) {
                	logger.error("Exception updating SPTO Expirations", de);
                } 

            }
        }
        return;
    }
    */
    
    /**
    * @ejb.transaction type="RequiresNew"
    */
    /*
    private void updateSPTOEUInformation(Permit permit, Facility f, List<PermitEU> permitEUs, boolean info) throws DAOException {
        String facilityRoleCd = getParameterValue("StatePTOExpirationNoticeFacilityRoleCd", "perw");
        PermitDAO permitDAO = permitDAO();
        FacilityDAO facDAO = facilityDAO();
        Transaction trans = TransactionFactory.createTransaction();
        permitDAO.setTransaction(trans);
        facDAO.setTransaction(trans);
        try {
        	for (PermitEU pe : permitEUs) {
        		EmissionUnit eu = pe.getFpEU();
                permitDAO.modifyPermitEU(pe);
                facDAO.modifyEmissionUnit(eu);
        	}

            if (info) {
                String notes = "Permit " + permit.getPermitNumber()
                + " is expired, and no renewal application was received on " 
                + "time.  The facility may be operating without a permit.";
                String taskName = "State PTO Expiration Notice";
                Integer taskUserId = f.getFacilityRoles().get(
                        facilityRoleCd).getUserId();
                createToDoInfoMessage(notes, taskName, f.getFpId(),
                        permit.getPermitID(), taskUserId, trans);
                info = false;
            }
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }
    }
    */

    /**
     * This method alerts AQD users that Title V Permits are about to expire for
     * their facilities. It is expected that this method will be called everyday
     * via the scheduler (via a stub scheduler routine).
     * 
     * This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-10:
     * 
     * State PTO/FESOP expiration in Facility Inventory - STARS2 shall provide 
     * autonomous processing to update the 'Current PTO/PTIO/Title V regulatory 
     * status' fields in the EU object in the facility inventory. For each State 
     * PTO(including FESOPs)/EU combination with an EU level permit status field 
     * of 'Active' and whose permit expiration date is past, STARS2 shall set 
     * the facility inventory field as follows:
     * 
     * If a PTIO/FEPTIO renewal application with an application date that falls 
     * between the State PTO/FESOP's final issuance and expiration dates exists 
     * and the facility operating status is not 'shutdown', then set the EU's  
     * 'Current PTO/PTIO/Title V regulatory status' field to 'Extended PTO' or 
     * 'Extended FESOP' and set the EU level permit status field to 'Extended' 
     * (overwrites previous value of  'Active').  Else set the 'Current 
     * PTO/PTIO/Title V regulatory status' field to 'Expired PTO' or 
     * 'Expired FESOP' and set the EU level permit status field to 'Expired' 
     * (overwrites previous value of  'Active').
     * 
     * If STARS2 changes the status field to 'Expired PTO' or 'Expired FESOP', 
     * then it shall create a to-do list information message for a designated 
     * AQD user (specified by a role in the facility inventory) informing them 
     * of the change.
     * 
     * @return void
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="RequiresNew"
     */
    public void StatePTOExpirationFacilityProfile() throws DAOException {
        /*
         * SELECT UNIQUE pp.permit_id, ffrx.user_id, pp.expiration_date,
         * ppi.issuance_date, pe.facility_eu_id FROM pt_ptio_permit ppp,
         * pt_permit pp, pt_eu pe, pt_eu_group peg, pt_permit_issuance ppi,
         * fp_facility_role_xref ffrx WHERE ppp.tv_flag = 'N' AND ppp.permit_id =
         * pp.permit_id AND pp.permit_id = peg.permit_id AND pp.permit_id =
         * ppi.permit_id AND ppi.issuance_type_cd = 'F' AND
         * ppi.issuance_status_cd = 'I' AND peg.permit_eu_group_id =
         * pe.permit_eu_group_id AND pe.permit_status_cd = 'A' AND
         * pp.facility_id = ffrx.facility_id AND ffrx.facility_role_cd = 'perw'
         * AND pp.expiration_date < '2007-10-04'
         * 
         * SELECT pa.application_id FROM pa_application pa, pa_eu pe WHERE
         * pe.emu_id = ? AND pe.application_id = pa.application_id AND
         * pa.submitted_date > ? 'issuance_date' AND pa.submitted_date < ?
         * 'expiration_date'
         */
        return;
    }
    
    /**
     * This method alerts AQD users that Title V Permits are about to expire for
     * their facilities. It is expected that this method will be called everyday
     * via the scheduler (via a stub scheduler routine).
     * 
     * This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-6:
     * 
     * Title V Permit expiration - STARS2 shall provide autonomous processing to update
     * the 'permit status' field of Title V Permits. For each Title V Permit with
     * a permit status field of 'Active' and whose permit expiration date is
     * past, STARS2 shall set the permit status field as follows:
     * 
     * If a TV renewal application exists with an application date of at least
     * 180 days but less than 18 months prior to the expiration date of the
     * Active Title V Permit, then set the permit status field to 'Extended'.
     * Else set the permit status field to 'Expired'.
     * 
     * If STARS2 changes the permit status field to 'Expired', then it shall
     * create a to-do list information message for a designated AQD user
     * (specified by a role in the facility inventory) informing them of the
     * change in status.
     * 
     * 
     * This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-8: 
     * 
     * Title V Permit expiration in Facility Inventory - STARS2 shall provide autonomous
     * processing to update the 'Title V Facility permit status' and 'Current
     * PTO/PTIO/Title V regulatory status' fields in the facility inventory. For
     * each Title V Permit with a permit status field of 'Active' and whose
     * permit expiration date is past, STARS2 shall set the facility inventory
     * field as follows:
     * 
     * If a TV renewal application with an application date of at least 180 days
     * but less than 18 months prior to the expiration date of the Active Title V Permit
     * permit exists and the facility operating status is not 'shutdown', then
     * set the 'Title V Facility permit status' field to 'Extended'. Else set
     * the field to 'Expired'.
     * 
     * If a TV renewal application with an application date of at least 180 days
     * but less than 18 months prior to the expiration date of the Active Title V Permit
     * permit exists, then set the 'Current PTO/PTIO/Title V regulatory status'
     * field to 'Extended Title V Permit' where the status was previously 'Active
     * Title V Permit' and the emissions unit does not have an operating status of
     * 'shutdown' or 'invalid' (the field should not be set if the value was
     * 'None' or 'PBR'). Else set the field to 'Expired' with all the same
     * qualifications except no renewal received.
     * 
     * If STARS2 changes the permit status field to 'Expired' for the facility
     * status, then it shall create a to-do list information message for a
     * designated AQD user (specified by a role in the facility inventory DO/LAA
     * Permit Writer) informing them of the change.
     * 
     * 
     * @return void
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="RequiresNew"
     */
    public void TVPTOExpirations() throws DAOException {
        PermitDAO permitDAO = permitDAO();

        String facilityRoleCd = SystemPropertyDef.getSystemPropertyValue("TVPTOExpirationsFacilityRoleCd", FacilityRoleDef.TV_PERMIT_ENGINEER);
        // First retrieve all permits that are expired as of today...
        String applicationNumber = null;
        String permitType = PermitTypeDef.TV_PTO;
        String permitReason = null;
        String legacyPermitNumber = null;
        String permitNumber = null;
        String facilityID = null;
        String facilityName = null;
        String permitStatusCd = PermitGlobalStatusDef.ISSUED_FINAL;
        String dateBy = PermitSQLDAO.EXPIRATION_DATE;
        Timestamp endDate = new Timestamp(System.currentTimeMillis());
        Timestamp beginDate = beginDateFromDate(endDate);
        logger.debug("Searching TVPTO permits with expiration date on or after: " + beginDate);

        List<Permit> tempPermits = permitDAO.searchPermits(applicationNumber,null, null,
                permitType, permitReason, null, legacyPermitNumber, permitNumber, facilityID,
                facilityName, permitStatusCd, dateBy, beginDate, endDate, null, true, null);

        logger.debug("" + tempPermits.size() + " permits retrieved");
        if (tempPermits.size() > 0) {
            for (Permit tp : tempPermits) {
                Transaction trans = TransactionFactory.createTransaction();
                permitDAO.setTransaction(trans);
                ApplicationDAO appDAO = applicationDAO(trans);
                FacilityDAO facDAO = facilityDAO(trans);
                try {
                    Permit permit = permitDAO.retrievePermit(tp.getPermitID());

                    Facility f = facDAO.retrieveFacility(permit.getFacilityId());

                    if (f.getOperatingStatusCd().equalsIgnoreCase(OperatingStatusDef.SD)) {
                        logger.debug("Facility " + permit.getFacilityId() + " is shut down. " +
                        		"Not processing permit " + permit.getPermitNumber());
                        continue;
                    }
                    
                	logger.debug("Processing permit " + permit.getPermitNumber()) ;

                    if (permit.getEuGroups() == null || permit.getEus() == null) {
                    	logger.debug("No EUs in permit " + permit.getPermitNumber()) ;
                        continue;
                    }
                    if (permit.getEus().size() == 0) {
                    	logger.debug("No EUs in permit " + permit.getPermitNumber()) ;
                        continue;
                    }
                    boolean hasActiveEU = false;
                    for (PermitEU peu : permit.getEus()) {
                    	if (PermitStatusDef.ACTIVE.equalsIgnoreCase(peu.getPermitStatusCd())) {
                    		hasActiveEU = true;
                    		break;
                    	}
                    }
                    if (!hasActiveEU) {
                    	logger.debug("Only active EUs in permit " + permit.getPermitNumber()) ;
                        continue;
                    }

                    // Active Title V Permit, then set the permit status field to 'Extended'.  
                    // Else set the permit status field to 'Expired'.
                    Integer renewalApp = retrieveRenewalTVApps(permit
                            .getFacilityId(), appDAO, permit.getExpirationDate());

                    String ptioStatusCd;
                    String facilityStatusCd;
                    if (renewalApp != null) {
                        /*
                         * If a TV renewal application with an application date
                         * of at least 180 days but less than 18 months prior to
                         * the expiration date of the Active Title V Permit
                         * exists, then set the 'Current PTO/PTIO/Title V
                         * regulatory status' field to 'Extended Title V Permit'
                         * where the status was previously 'Active Title V Permit'
                         * and the emissions unit does not have an operating
                         * status of 'shutdown' or 'invalid' (the field should
                         * not be set if the value was 'None' or 'PBR'). Else
                         * set the field to 'Expired' with all the same
                         * qualifications except no renewal received.
                         */
                    	logger.debug("Marking Permit " + permit.getPermitNumber() + " as extended. " +
                    			"Renwal application id = " + renewalApp) ;
                        permitDAO.updatePermitEUsStatus(permit.getPermitID(),
                                PermitStatusDef.EXTENDED);
                        ptioStatusCd = PTIORegulatoryStatus.EXTENDED_TV_PTO;
                        facilityStatusCd = PermitStatusDef.EXTENDED;
                    } else {
                    	logger.debug("Marking Permit " + permit.getPermitNumber() + " as expired") ;
                        permitDAO.updatePermitEUsStatus(permit.getPermitID(),
                                PermitStatusDef.EXPIRED);
                        ptioStatusCd = PTIORegulatoryStatus.EXPIRED_TV_PTO;
                        facilityStatusCd = PermitStatusDef.EXPIRED;

                        String taskName = "Title V Permit Expired.";
                        String notes = "Permit " + permit.getPermitNumber()
                        + " is expired.  No Title V renewal " +
                        "application was received between 18 months and 180 days of " +
                        "expiration. The company will be operating without a permit until " +
                        "the renewal is issued. If this message is incorrect, please " +
                        "contact the System Administrator.";
                        Integer taskUserId = f.getFacilityRoles().get(facilityRoleCd).getUserId();
                        createToDoInfoMessage(notes, taskName, f.getFpId(),
                                permit.getPermitID(), taskUserId, trans);
                    }
                    for (PermitEU pe : permit.getEus()) {
                        EmissionUnit eu = f.getMatchingEmissionUnit(pe
                                .getCorrEpaEMUID());
                        if (eu == null) {
                            throw new NullPointerException(
                                    "Could not locate a current Emission Unit for "
                                    + " Constant EU ID = "
                                    + pe.getCorrEpaEMUID());
                        }

                        if (EuOperatingStatusDef.SD.equalsIgnoreCase(eu.getOperatingStatusCd()) ||
                                EuOperatingStatusDef.IV.equalsIgnoreCase(eu.getOperatingStatusCd()) ||
                                eu.getPtioStatusCd() == null || 
                                PTIORegulatoryStatus.PBR.equalsIgnoreCase(eu.getPtioStatusCd()))
                            continue;

                        eu.setPtioStatusCd(ptioStatusCd);
                        facDAO.modifyEmissionUnit(eu);
                    }
                    f = facDAO.retrieveFacility(
                            permit.getFacilityId());
                    f.setPermitStatusCd(facilityStatusCd);
                    facDAO.modifyFacility(f);                
                    trans.complete();
                } catch (DAOException de) {
                    cancelTransaction(trans, de);
                } finally {
                    closeTransaction(trans);
                }
            }
        }
        return;
    }

    /**
     * This method alerts AQD users that Title V Permits are about to expire for
     * their facilities. It is expected that this method will be called everyday
     * via the scheduler (via a stub scheduler routine).
     * 
     * This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-7:
     * 
     * Title V Permit expiration warning - STARS2 shall provide autonomous processing to 
     * alert AQD users of expiring Title V Permits. For each facility with an 
     * active Title V Permit, STARS2 shall create a to-do list information message 
     * for a designated AQD user (specified by a role in the facility inventory) 
     * exactly NNN days or exactly (NNN - 80) days prior to the permit expiration 
     * date if no Title V Permit renewal application has been received from that facility. 
     * The renewal application must have an application date of at least 180 days 
     * but less than 18 months prior to the expiration date of the Active Title V Permit 
     * permit.
     * 
     * The value of NNN shall be configurable as a system parameter with a default 
     * value of 180.
     * 
     * STARS2 shall exclude from this processing those facilities with a facility 
     * operating status of 'Shutdown'
     * 
     * @return void
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="RequiresNew"
     */
    public void TVPTOExpirationWarning() throws DAOException {
        PermitDAO permitDAO = permitDAO();
        ApplicationDAO appDAO = applicationDAO();

        Integer days = SystemPropertyDef.getSystemPropertyValueAsInteger("TVPTOExpirationWarningDay", 180);
        
        // First retrieve all permits that are expired as of exactly NNN days 
        String facilityRoleCd = SystemPropertyDef.getSystemPropertyValue("TVPTOExpirationWarningFacilityRoleCd", FacilityRoleDef.TV_PERMIT_ENGINEER);
        String applicationNumber = null;
        String permitType = PermitTypeDef.TV_PTO;
        String permitReason = null;
        String legacyPermitNumber = null;
        String permitNumber = null;
        String facilityID = null;
        String facilityName = null;
        String permitStatusCd = PermitGlobalStatusDef.ISSUED_FINAL;
        String dateBy = PermitSQLDAO.EXPIRATION_DATE;
        Timestamp endDate = addDaysTo(new Timestamp(System.currentTimeMillis()), days);
        Timestamp beginDate = endDate;
        
        List<Permit> tempPermits = permitDAO.searchPermits(applicationNumber,null, null,
                permitType, permitReason, null, legacyPermitNumber, permitNumber, facilityID,
                facilityName, permitStatusCd, dateBy, beginDate, endDate, null, true, null);

        // or exactly (NNN - 80) days prior to the permit expiration date
        endDate = addDaysTo(new Timestamp(System.currentTimeMillis()), days - 80);
        beginDate = endDate;
        tempPermits.addAll(permitDAO.searchPermits(applicationNumber,null, null,
                permitType, permitReason, null, legacyPermitNumber, permitNumber, facilityID,
                facilityName, permitStatusCd, dateBy, beginDate, endDate, null, true, null));

        if (tempPermits.size() > 0) {
            for (Permit tp : tempPermits) {
                Permit permit = permitDAO.retrievePermit(tp.getPermitID());
                if (permit.getEuGroups() == null || permit.getEus() == null)
                    continue;
                if (permit.getEus().size() == 0)
                    continue;
                if (!permit.getEus().get(0).getPermitStatusCd().equalsIgnoreCase(
                                PermitStatusDef.ACTIVE))
                    continue;
                
                Integer renewalApp = retrieveRenewalTVApps(permit
                        .getFacilityId(), appDAO, permit.getExpirationDate());

                if (renewalApp == null) {
                    Facility f = facilityDAO().retrieveFacility(
                            permit.getFacilityId());

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    String notes = "Title V Permit " + permit.getPermitNumber()
                    	+ " will expire on " + dateFormat.format(permit.getExpirationDate())
                    	+ ". No timely renewal application was received. " 
                    	+ "Upon expiration of the permit the company may be operating without a permit.";
                    String taskName = "TVPTO Expiration Warning";
                    Integer taskUserId = f.getFacilityRoles().get(facilityRoleCd).getUserId();
                    createToDoInfoMessage(notes, taskName, f.getFpId(),
                            permit.getPermitID(), taskUserId, null);
                }
            }

        }
        return;
    }

    /**
     * This method alerts AQD users that Title V Permits are about to expire for
     * their facilities. It is expected that this method will be called everyday
     * via the scheduler (via a stub scheduler routine).
     * 
     * This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-11:
     * 
     * Title V Permit late application or no application (NOV) letter - STARS2 shall 
     * provide autonomous processing to alert AQD users to send Title V Permit late 
     * application or no application (NOV) letters.  For each Title V Permit with 
     * a permit status field of 'Active', STARS2 shall search for facilities 
     * where all of the following conditions are true:
     * 
     *  1. The facility has a Title V Permit with a permit status field of 'Active' and 
     *     an expiration date that falls within the next 6 months
     *  2. No Title V Permit renewal permit application with an application date that 
     *     falls in the interval of 18 months to 180 days prior to the permit's 
     *     expiration date exists
     *  3. The STARS2 correspondence history contains no record of a 
     *     'late application or no application letter' with a 'date sent'
     *     field within the last 6 months
     *  4. The facility's operational status is not 'Shutdown'
     *  
     *  If so, STARS2 shall create a to-do list information message for a 
     *  designated AQD user (specified by a role in the facility inventory). 
     * 
     * @return void
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="RequiresNew"
     */
    public void TVPTOLateApplication() throws DAOException {
        PermitDAO permitDAO = permitDAO();
        ApplicationDAO appDAO = applicationDAO();

        String corrTypeCD1 = SystemPropertyDef.getSystemPropertyValue("TVPTOLateApplicationTVRenewalSubmittedLateCd", "41"); //"NOV - Title V Renewal Submitted Late"
        String corrTypeCD2 = SystemPropertyDef.getSystemPropertyValue("TVPTOLateApplicationTVRenewalNotSubmittedCd", "42"); //"NOV - Title V Renewal Not Submitted"
        String facilityRoleCd = SystemPropertyDef.getSystemPropertyValue("TVPTOLateApplicationFacilityRoleCd", FacilityRoleDef.TV_PERMIT_SUPERVISOR);
        
        String applicationNumber = null;
        String permitType = PermitTypeDef.TV_PTO;
        String permitReason = null;
        String legacyPermitNumber = null;
        String permitNumber = null;
        String facilityID = null;
        String facilityName = null;
        String permitStatusCd = PermitGlobalStatusDef.ISSUED_FINAL;
        String dateBy = PermitSQLDAO.EXPIRATION_DATE;
        
        Calendar now = new GregorianCalendar();
        now.setTimeInMillis(System.currentTimeMillis());
        Calendar cDate = new GregorianCalendar(now
                .get(Calendar.YEAR), now.get(Calendar.MONTH), now
                .get(Calendar.DAY_OF_MONTH));
        cDate.add(Calendar.MONTH, 6);
        Timestamp endDate = new Timestamp(cDate.getTimeInMillis());
        Timestamp beginDate = new Timestamp(System.currentTimeMillis());
        
        /*
         * 1. The facility has a Title V Permit with a permit status field of 'Active'
         * and an expiration date that falls within the next 6 months
         */
        List<Permit> tempPermits = permitDAO.searchPermits(applicationNumber,null, null,
                permitType, permitReason, null, legacyPermitNumber, permitNumber, facilityID,
                facilityName, permitStatusCd, dateBy, beginDate, endDate, null, true, null);
        
        if (tempPermits.size() > 0) {
            for (Permit tp : tempPermits) {
                Permit permit = permitDAO.retrievePermit(tp.getPermitID());
                if (permit.getEuGroups() == null || permit.getEus() == null)
                    continue;
                if (permit.getEus().size() == 0)
                    continue;
                if (!permit.getEus().get(0).getPermitStatusCd().equalsIgnoreCase(
                                PermitStatusDef.ACTIVE))
                    continue;
                
                Facility f = facilityDAO().retrieveFacility(
                        permit.getFacilityId());
                /*
                 * 4. The facility's operational status is not 'Shutdown'
                 */
                if (f.getOperatingStatusCd().equalsIgnoreCase(OperatingStatusDef.SD))
                    continue;
                
                /*
                 * 2. No Title V Permit renewal permit application with an application
                 * date that falls in the interval of 18 months to 180 days
                 * prior to the permit's expiration date exists
                 */
                Integer renewalApp = retrieveRenewalTVApps(permit
                        .getFacilityId(), appDAO, permit.getExpirationDate());

                if (renewalApp == null) {

                    /*
                     * 3. The STARS2 correspondence history contains no record
                     * of a 'late application or no application letter' with a
                     * 'date sent' field within the last 6 months 
                     */
                    now = new GregorianCalendar();
                    now.setTimeInMillis(System.currentTimeMillis());
                    cDate = new GregorianCalendar(now.get(Calendar.YEAR), 
                            now.get(Calendar.MONTH), 
                            now.get(Calendar.DAY_OF_MONTH));
                    cDate.add(Calendar.MONTH, -6);
                    Correspondence correspondence = new Correspondence();
                    correspondence.setFacilityID(f.getFacilityId());
                    correspondence.setCorrespondenceTypeCode(corrTypeCD1);
                    Correspondence[] cs = correspondenceDAO().searchCorrespondence(correspondence);
                    boolean found = false;
                    for (Correspondence c : cs){
                        if (c.getDateGenerated().before(cDate.getTime()))
                            found = true;
                    }

                    if (!found){
                        correspondence.setCorrespondenceTypeCode(corrTypeCD2);
                        cs = correspondenceDAO().searchCorrespondence(correspondence);
                        for (Correspondence c : cs){
                            if (c.getDateGenerated().before(cDate.getTime()))
                                found = true;
                        }
                    }
                            
                    if (!found){
                        String taskName = "Title V Permit late application or no application (NOV) letter";
                        String notes = "A Title V renewal application was received late or not at all. " +
                        		"Therefore, an NOV must be sent to them so AQD can issue orders to them to " +
                        		"continue operating under the previously issued permit. Please generate the " +
                        		"NOV from Tools => Document Generation under the Applications folder. Please " +
                        		"choose the appropriate letter: NOV - Title V Renewal Not Submitted; OR, NOV - " +
                        		"Title V Renewal Submitted Late.";
                        Integer taskUserId = f.getFacilityRoles().get(facilityRoleCd).getUserId();
                        createToDoInfoMessage(notes, taskName, f.getFpId(),
                                permit.getPermitID(), taskUserId, null);
                    }
                }
            }

        }
        return;
    }

    /**
     * U-22-Autonomous_Analyses-1-12
     * 
     * Draft issue date but no draft public notification date - STARS2 shall
     * provide autonomous processing to alert AQD users of missing Draft public
     * notification dates. STARS2 shall detect occurrences of PTI, PTIO, and TV
     * PTO permit objects where all of the following conditions are true:
     * 
     * 1. The permit status is 'Issued draft' 
     * 2. The draft issuance date is set (not null) 
     * 3. The current date is at least NN days after the draft issuance date 
     *    where NN is a configurable system parameter with a default value of 5 
     * 4. The draft public notification date is not set (is null) 
     * 5. The corresponding facility's operational status is not 'Shutdown'
     * 
     * If so, STARS2 shall create a to-do list information message for a
     * designated AQD user (specified by a role in the facility inventory CO Issuance Coordinator).
     * 
     * 
     * @return void
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="RequiresNew"
     */
    public void DraftIssueNoPublicNotice() throws DAOException {
    	
        String facilityRoleCd = SystemPropertyDef.getSystemPropertyValue("DraftIssueNoPublicNoticeFacilityRoleCd", FacilityRoleDef.UNDELIVERED_MAIL_ADMIN);
        Integer days = SystemPropertyDef.getSystemPropertyValueAsInteger("DraftIssueNoPublicNoticeDay", 5);
        
        List<Permit> ps = permitDAO().searchPermits(null, null, null, null, null, null, null, null,
                null, null, PermitGlobalStatusDef.ISSUED_DRAFT, null, null, 
                null, null, true, null);
        
        for (Permit tp : ps) {
            Permit p = permitDAO().retrievePermit(tp.getPermitID());
            /*
             * 4. The draft public notification date is not set (is null) 
             * 3. The current date is at least NN days after the draft issuance
             * date where NN is a configurable system parameter with a default
             * value of 5
             */
            if (p.getDraftIssuance().getPublicNoticePublishDate() == null
                    && isModDays(days, p.getDraftIssueDate())) {
                /*
                 * 5. The corresponding facility's operational status is not
                 * 'Shutdown'
                 */
                Facility f = facilityDAO().retrieveFacility(tp.getFacilityId());
                if (!f.getOperatingStatusCd().equalsIgnoreCase(
                                OperatingStatusDef.SD)) {
                	
                	if (p.getPermitType().equalsIgnoreCase(PermitTypeDef.NSR)) {
                		facilityRoleCd = FacilityRoleDef.NSR_PERMIT_ENGINEER;
                	} else if (p.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO)) {
                		facilityRoleCd = FacilityRoleDef.TV_PERMIT_ENGINEER;
                	}

                    String notes = "Permit " + p.getPermitNumber()
                            + " does not have Public Notice date.";
                    String taskName = "No Public Notice date";
                    Integer taskUserId = f.getFacilityRoles().get(
                            facilityRoleCd).getUserId();
                    createToDoInfoMessage(notes, taskName, f.getFpId(), p
                            .getPermitID(), taskUserId, null);
                }
            }
        }
        return;
    }

    /**
     * U-22-Autonomous_Analyses-1-13
     * 
     * Draft public hearing newspaper request date but no public hearing notice
     * date - STARS2 shall provide autonomous processing to alert AQD users of
     * missing Draft public hearing newspaper request dates. STARS2 shall detect
     * occurrences of PTI, PTIO, and Title V Permit objects where all of the
     * following conditions are true:
     * 
     * 1. The permit status is 'Issued draft' 
     * 2. The public hearing newspaper request date is set (not null) 
     * 3. The current date is at least NN days after the public hearing 
     * newspaper request date where NN is a configurable system parameter with 
     * a default value of 5 
     * 4. The public hearing notice date is not set (is null) 
     * 5. The corresponding facility's operational status is not 'Shutdown'
     * 
     * If so, STARS2 shall create a to-do list information message for a
     * designated AQD user (specified by a role in the facility inventory CO Issuance Coordinator).
     * 
     * @return void
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="RequiresNew"
     */
    public void DraftNewspaperRequestNoHearingDate() throws DAOException {
    	
        String facilityRoleCd = SystemPropertyDef.getSystemPropertyValue("DraftNewspaperRequestNoHearingDateFacilityRoleCd", FacilityRoleDef.UNDELIVERED_MAIL_ADMIN);
        Integer days = SystemPropertyDef.getSystemPropertyValueAsInteger("DraftNewspaperRequestNoHearingDateDay", 5);
        
        List<Permit> ps = permitDAO().searchPermits(null, null, null, null, null, null, null, null,
                null, null, PermitGlobalStatusDef.ISSUED_DRAFT, null, null, 
                null, null, true, null);
        
        for (Permit tp : ps) {
            Permit p = permitDAO().retrievePermit(tp.getPermitID());
            /*
             * 2. The public hearing newspaper request date is set (not null) 
             * 4. The public hearing notice date is not set (is null)
             * 3. The current date is at least NN days after the draft issuance
             * date where NN is a configurable system parameter with a default
             * value of 5
             */
            if (p.getDraftIssuance().getHearingNoticePublishDate() == null
                    && p.getDraftIssuance().getHearingRequestedDate() != null 
                    && isAfterDays(days, p.getDraftIssuance().getHearingRequestedDate())) {
                Facility f = facilityDAO().retrieveFacility(tp.getFacilityId());
                /*
                 * 5. The corresponding facility's operational status is not
                 * 'Shutdown'
                 */
                if (!f.getOperatingStatusCd().equalsIgnoreCase(
                                OperatingStatusDef.SD)) {
                	
                	if (p.getPermitType().equalsIgnoreCase(PermitTypeDef.NSR)) {
                		facilityRoleCd = FacilityRoleDef.NSR_PERMIT_ENGINEER;
                	} else if (p.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO)) {
                		facilityRoleCd = FacilityRoleDef.TV_PERMIT_ENGINEER;
                	}
                	
                    String notes = "Permit " + p.getPermitNumber()
                            + " does not have public hearing notice date.";
                    String taskName = "No public hearing notice date";
                    Integer taskUserId = f.getFacilityRoles().get(
                            facilityRoleCd).getUserId();
                    createToDoInfoMessage(notes, taskName, f.getFpId(), p
                            .getPermitID(), taskUserId, null);
                }
            }
        }
        
        return;
    }

    /**
     * This method alerts AQD users that Title V Permits are about to expire for
     * their facilities. It is expected that this method will be called everyday
     * via the scheduler (via a stub scheduler routine).
     * 
     * This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-14:
     * 
     * PPP issue date but no certified mail receipt date - STARS2 shall provide
     * autonomous processing to alert AQD users of missing PPP certified mail
     * receipt dates. STARS2 shall detect occurrences of Title V Permit objects
     * where all of the following conditions are true:
     * 
     * 1. The correspondence type is 'PPP Title V Permit To Operate (PTO)' 
     * 2. The current date is at least NN days after the 'Date Generated' attribute
     * where NN is a configurable system parameter with a default value of 15 
     * 3. The 'Certified Mail Tracking ID' is set (not null) 
     * 4. The 'Certified Mail Receipt Date' is not set
     * 
     * If so, STARS2 shall create a to-do list information message for a
     * designated AQD user (specified by a role in the facility inventory CO Notice Generator/Batch Processor).
     * 
     * @return void
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="RequiresNew"
     */
    /*
    public void PPPIssueDateNoCertMailReceipt() throws DAOException {
        String facilityRoleCd = getParameterValue("PPPIssueDateNoCertMailReceiptFacilityRoleCd", "pubn");
        Integer days = getParameterValue("PPPIssueDateNoCertMailReceiptDay", 15);
        String corrTypeCD = getParameterValue("PPPIssueDateNoCertMailReceiptCorrespondenceTypeCode", "75"); // PPP CorrespondenceTypeCode
        
        // 1. The correspondence type is 'PPP Title V Permit To Operate (PTO)' 
        List<Permit> ps = permitDAO().searchPermits(null, null, null, PermitTypeDef.TV_PTO, 
                null, null, null, null, PermitGlobalStatusDef.ISSUED_PPP, null, 
                null, null, null, true);
        
        for (Permit p : ps){
            Facility f = facilityDAO().retrieveFacility(p.getFacilityId());
            TVPermit tp = (TVPermit)permitDAO().retrievePermit(p.getPermitID());
            Correspondence correspondence = new Correspondence();
            correspondence.setFacilityID(f.getFacilityId());
            correspondence.setCorrespondenceTypeCode(corrTypeCD);
            correspondence.setAdditionalInfo(tp.getPermitNumber());
            Correspondence[] pppcs = correspondenceDAO().searchCorrespondence(correspondence);
            for (Correspondence cr : pppcs){
                //  4. The PPP certified mail receipt date is not set (is null)
                //  NEW 3. The 'Certified Mail Tracking ID' is set (not null)
                //  2. The current date is at least NN days after the 'Date Generated' attribute
                //     where NN is a configurable system parameter with a default value of 15
                if (cr.getCertifiedMailRcptDate() == null
                        && cr.getCertifiedMailTrackId() != null
                        && isAfterDays(days, cr.getDateGenerated())) {
                    String notes = "Permit "
                            + p.getPermitNumber()
                            + " does not have certified mail receipt date for PPP issuance document.";
                    String taskName = "No certified mail receipt date";
                    Integer taskUserId = f.getFacilityRoles().get(
                            facilityRoleCd).getUserId();
                    createToDoInfoMessage(notes, taskName, f.getFpId(), p
                            .getPermitID(), taskUserId, null);
                }
            }
        }
    }

    */
    

    private boolean isAfterDays(Integer days, Timestamp date) {
        if (date == null)
            return false;
        Calendar now = new GregorianCalendar();
        now.setTimeInMillis(System.currentTimeMillis());
        now.add(Calendar.DAY_OF_MONTH, (days * -1));
        Calendar did = new GregorianCalendar();
        did.setTimeInMillis(date.getTime());
        return did.before(now);
    }
    
    /**
     *  If a TV renewal application exists with an application date
     *  of at least 180 days but less than 18 months prior to the expiration 
     *  date of the Active Title V Permit.
     *  
     * @param facilityId
     * @param appDAO
     * @param expirationDate
     * @return
     * @throws DAOException 
     */
    private Integer retrieveRenewalTVApps(String facilityId,
            ApplicationDAO appDAO, Timestamp expirationDate)
            throws DAOException {

        Calendar now = new GregorianCalendar();
        now.setTimeInMillis(expirationDate.getTime());
        Calendar upperDate = new GregorianCalendar(now
                .get(Calendar.YEAR), now.get(Calendar.MONTH), now
                .get(Calendar.DAY_OF_MONTH));
        Calendar lowerDate = new GregorianCalendar(now
                .get(Calendar.YEAR), now.get(Calendar.MONTH), now
                .get(Calendar.DAY_OF_MONTH));

        upperDate.add(Calendar.MONTH, -18);
        lowerDate.add(Calendar.DAY_OF_MONTH, -180);

        return appDAO.retrieveRenewalApplications(facilityId, 
                new Timestamp(upperDate.getTimeInMillis()), 
                new Timestamp(lowerDate.getTimeInMillis()));
    }

    private Timestamp addDaysTo(Timestamp timestamp, Integer days) {
        Calendar now = new GregorianCalendar();
        now.setTimeInMillis(timestamp.getTime());
        Calendar cDate = new GregorianCalendar(now 
                .get(Calendar.YEAR), now.get(Calendar.MONTH), now
                .get(Calendar.DAY_OF_MONTH));
        cDate.add(Calendar.DAY_OF_MONTH, days);
        return new Timestamp(cDate.getTimeInMillis());
    }

    /**
     * create a to-do list information message for a designated AQD user 
     * trans can be null
     * 
     * @param notes
     * @param taskName
     * @param fpId
     * @param facilityRoleCd
     * @param wfBO
     * @param externalId
     * @param taskUserId 
     * @param trans 
     * @throws DAOException
     */
    private void createToDoInfoMessage(String notes, String taskName,
            Integer fpId, Integer externalId,
            Integer taskUserId, Transaction trans) throws DAOException {

//    	// decouple workflow
//    	throw new RuntimeException("decouple workflow; method not implemented");
        Integer processTemplateId = new Integer(0);
        Timestamp today = new Timestamp(System.currentTimeMillis());
        Integer createBy = new Integer(1); // admin user id
        
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("Notes", notes );
        data.put("Task Name", taskName);
        data.put("Task UserId", String.valueOf(taskUserId));
        
//        try {
//        	WriteWorkFlowService wfBO = ServiceFactory.getInstance().getWriteWorkFlowService();
//            if (trans == null)
//                wfBO.createTaskProcessFlows(processTemplateId, fpId, externalId, "N", today, null, createBy, data, taskUserId);
//            else
//                wfBO.createTaskProcessFlows(processTemplateId, fpId, externalId, "N", today, null, createBy, data, taskUserId, trans);
//        } catch (ServiceFactoryException sfe) {
//            logger.error(sfe.getMessage(), sfe);
//            throw new DAOException(sfe.getMessage(), sfe);
//        } catch (RemoteException ex) {
//            logger.error("Error createing workflow task for : " + notes, ex);
//            throw new DAOException(ex.getMessage(), ex);
//        }
        
        WorkFlowManager wfm = new WorkFlowManager();
        WorkFlowResponse resp = 
        		wfm.createTaskProcessFlows(processTemplateId, fpId, externalId, "N", today, null, createBy, data);

        if (resp.hasError() || resp.hasFailed()) {
        	throw new RuntimeException(
        			"error(s) while creating process note: " + 
        	resp.getErrorMessages());
        }
    }

    /**
     * Because it should not search for everything, the begin date set to one
     * month
     * 
     * @param date
     * @return
     */
    private Timestamp beginDateFromDate(Timestamp date) {
        Calendar now = new GregorianCalendar();
        now.setTimeInMillis(date.getTime());
        // check for all permits in the last year if it's the first Saturday of the month
        if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY &&
        		now.get(Calendar.DAY_OF_MONTH ) <= 7) {
	        now.add(Calendar.YEAR, -3);
        } else {
        	// otherwise, check permits issued in the last month
	        now.add(Calendar.MONTH, -2);
        }
        return new Timestamp(now.getTimeInMillis());
    }
    
    private boolean isModDays(Integer days, Timestamp date) {
        if (date == null)
            return false;
        
        return (((System.currentTimeMillis() - date.getTime()) / (24*60*60*1000)) % days) == 0;
    }
    /**
     * 
     * @ejb.transaction type="Supports"
     * @ejb.interface-method view-type="remote"
     */
    public void addAppEUsToPermits() {
    	PermitService permitBO = null;
    	try {
			permitBO = ServiceFactory.getInstance().getPermitService();
	    	List<Permit> ptios = permitBO.search(null, null, null, PermitTypeDef.NSR, PermitReasonsDef.RENEWAL,
	    			null, null, null, null, null, null, null, null, null, null, true, null);
	    	for (Permit permit : ptios) {
	    		if (PermitGlobalStatusDef.DEAD_ENDED.equals(permit.getPermitGlobalStatusCD())) {
	    			logger.debug(" Skipping dead-ended PTIO: " + permit.getPermitNumber());
	    			continue;
	    		}
	    		try {
	    			addAppEUsToPermit(permit, permitBO);
	    		} catch (RemoteException e) {
	    			logger.error("Failed processing permit: " + permit.getPermitNumber(), e);
	    		} 
	    	}
	    	
	    	List<Permit> tvptos = permitBO.search(null, null, null, PermitTypeDef.TV_PTO, 
	    			null, null, null, null, null, null, PermitGlobalStatusDef.NONE, null, null, null, null, true, null);	    	
	    	for (Permit permit : tvptos) {
	    		try {
		    	addAppEUsToPermit(permit, permitBO);
	    		} catch (RemoteException e) {
	    			logger.error("Failed processing permit: " + permit.getPermitNumber(), e);
	    		}
	    	}
		} catch (ServiceFactoryException e) {
			logger.error("Failed getting PermitService", e);
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} 
    }

    private void addAppEUsToPermit(Permit permit, PermitService permitBO) throws RemoteException {
    	PermitInfo pi = permitBO.retrievePermit(permit.getPermitID());
		if (!pi.getPermit().isLegacyPermit()) {
			// only processing legacy PTIOs
			logger.debug(" Skipping non-legacy " + pi.getPermit().getPermitTypeDsc() + " : " 
					+ permit.getPermitNumber());
			 
			return;
		}
		if (pi.getPermit().getEus().size() == 0) {
			List<Application> permitApps = pi.getPermit().getApplications();
			List<PermitEUGroup> euGroups = pi.getPermit().getEuGroups();
			HashSet<Integer> addedEUs = new HashSet<Integer>();
			
			// there should be one eu group for this permit. If not, create it
			PermitEUGroup individualGroup = null;
			if (euGroups.size() == 0) {
				individualGroup = new PermitEUGroup();
                individualGroup.setIndividualEUGroup();
                individualGroup.setPermitID(permit.getPermitID());
			} else if (euGroups.size() == 1) {
				individualGroup = euGroups.get(0);
			} else {
				logger.error("Multiple EU groups, but no EUs for permit: " + permit.getPermitNumber());
				return;
			}
			
			// iterate over applications associated with permit to add their EUs
			for (Application app : permitApps) {
				List<ApplicationEU> appEUs = app.getIncludedEus();
				
				for (ApplicationEU appEU : appEUs) {
					for (EmissionUnit candidateEU : pi.getPermittableEUs()) {
						if (appEU.getFpEU().getCorrEpaEmuId().equals(candidateEU.getCorrEpaEmuId()) 
								&& !addedEUs.contains(appEU.getFpEU().getCorrEpaEmuId())) {
							PermitEU permitEU = new PermitEU();
							permitEU.setFpEU(candidateEU);
							permitEU.setPermitStatusCd("A");
							individualGroup.addPermitEU(permitEU);
							addedEUs.add(appEU.getFpEU().getCorrEpaEmuId());
						}
					}
				}
			}
			// this is just to log the permits that have been changed.
			permitBO.modifyPermit(pi.getPermit(), 1);
			if (individualGroup.getPermitEUs().size() > 0) {
				StringBuilder euStr = new StringBuilder();
				for (PermitEU peu : individualGroup.getPermitEUs()) {
					if (euStr.length() > 0) {
						euStr.append(", ");
					}
					euStr.append(peu.getFpEU().getEpaEmuId());
				}
				logger.debug(" Updated Permit: " + permit.getFacilityId() + ":" + permit.getPermitNumber() + ":" + euStr.toString());
			}
		}
    }
}
