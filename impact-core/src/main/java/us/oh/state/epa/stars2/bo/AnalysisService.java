/*
 * Generated by XDoclet - Do not edit!
 */
package us.oh.state.epa.stars2.bo;

/**
 * Service interface for AnalysisEJB.
 */
public interface AnalysisService {
	
   /**
    * This method alerts AQD user belonging to the PER Admin system wide role that PER reminder letters should be generated. It is expected that this method will be called everyday via the scheduler (via a stub scheduler routine). This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-1: PER reminder - STARS2 shall create a to-do list information message for each AQD user belonging to the 'PER admin' system wide role group to remind them that PER reminder letters and forms should be generated. STARS2 shall generate this message NN days prior to each of the four PER due dates established by DAPC. The value of NN shall be configurable as a system parameter with a default value of 45.
    * @return void
    */
   public void PERReminderNotice(  )
      throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException;

   /**
    * This method alerts AQD users that Title V Permits are about to expire for their facilities. It is expected that this method will be called everyday via the scheduler (via a stub scheduler routine). This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-15: Title V and SMTV FER Notice of Violation (NOV)/ Late Letter- STARS2 shall provide autonomous processing to alert AQD users of cases were a Title V FER or SMTV FER is required but no FER has been received. STARS2 shall search the set of Emissions Inventories objects for the most recent reporting year for those objects with a status of 'Emissions Inventory/Reminder Sent '. If there are any objects that meet this criteria, STARS2 shall create a to-do list information message for each AQD user belonging to the 'Emissions Reporting' system wide role group. The information message shall indicate whether there were Title V facilities with missing FERs, SMTV facilities with missing FERs, or both. The analysis shall exclude facilities whose facility operational status is marked as 'Shutdown' and whose shutdown date is on or before 12/31 of the reporting year.
    * @return void
    */
   public void TVSMTVFERNoticeViolation(  )
      throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException;

   /**
    * This method alerts AQD users that PER reports are overdue for their facilities. It is expected that this method will be called everyday via the scheduler (via a stub scheduler routine). This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-2: PER overdue - STARS2 shall provide autonomous processing to alert AQD users of overdue PER reports. STARS2 shall create a to-do list information message for a designated AQD user (specified by a role in the facility inventory) when STARS2 detects that exactly NN days or exactly (NN + 30) days have elapsed since that facility's PER due date and no corresponding PER report (based on the reporting period for the PER report) has been received. The value of NN shall be configurable as a system parameter with a default value of 15. STARS2 shall exclude from this processing those facilities with a shutdown date that is greater than one year before the PER Due date. For example, if the PER is due 06/30/08 and the facility was shutdown 03/15/07 then that facility wouldn't be included in the PER reminders anymore. But if it was shutdown during the year the reporting is for, it should be included.
    * @return void
    */
   //public void PEROverdueNotice(  )
   //   throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException;

   /**
    * This method alerts AQD users that TV Certs reports are overdue for their facilities. It is expected that this method will be called everyday via the scheduler (via a stub scheduler routine). This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-3: TV cert overdue - STARS2 shall provide autonomous processing to alert AQD users of overdue TV cert reports. STARS2 shall create a to-do list information message for a designated AQD user (specified by a role in the facility inventory) when STARS2 detects that exactly NN days or exactly (NN + 30) days have elapsed since that facility's TV cert due date and no corresponding TV cert report (based on the reporting period for the TV cert report) has been received. The value of NN shall be configurable as a system parameter with a default value of 15. STARS2 shall exclude from this processing those Facilities with a shutdown date that is greater than one year before the end of the reporting period (12/31 of the reporting year). For example, if the Title V compliance certification is due 04/30/08 for 2007 compliance reporting, and the facility was shutdown 03/15/07 then that facility would be included in the Title V compliance certification reminders. But if it was shutdown during 2006, it should not be included.
    * @return void
    */
   public void TVCertOverdueNotice(  )
      throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException;

   /**
    * This method alerts AQD users that PTIO permits are expired for their facilities. It is expected that this method will be called everyday via the scheduler (via a stub scheduler routine). This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-4: PTIO expiration - STARS2 shall provide autonomous processing to update the EU level permit status field of PTIO permits. For each PTIO/EU permit with a permit status field of 'Active' and whose permit expiration date is past STARS2 shall set the status field to either 'Extended' or 'Expired'. If a renewal application for that EU has been received with an application date later than the final issuance date and prior to the expiration date of the PTIO permit, then STARS2 shall set the EU level permit status field to 'Extended', else STARS2 shall set it to 'Expired'. If STARS2 changes the permit status field to 'Expired', then it shall create a to-do list information message for a designated AQD user (specified by a role in the facility inventory) informing them of the change in status This method fullfils LSRD requirement U-22-Autonomous_Analyses-1-9: "PTIO/FEPTIO expiration in Facility Inventory - STARS2 shall provide autonomous processing to update the 'Current PTO/PTIO/Title V regulatory status' fields in the EU object in the facility inventory. For each PTIO/EU combination with an EU level permit status field of 'Active' and whose permit expiration date is past, STARS2 shall set the facility inventory field as follows: If a PTIO/FEPTIO renewal application with an application date that falls between the PTIO/FEPTIO's final issuance and expiration dates exists and the facility operating status is not 'shutdown', then set the EU's 'Current PTO/PTIO/Title V regulatory status' field to 'Extended PTIO' or 'Extended FEPTIO' and set the EU level permit status field to 'Extended' (overwrites previous value of 'Active'). Else set the 'Current PTO/PTIO/Title V regulatory status' field to 'Expired PTIO' or 'Expired FEPTIO' and set the EU level permit status field to 'Expired' (overwrites previous value of 'Active'). If STARS2 changes the status field to 'Expired PTIO' or 'Expired FEPTIO', then it shall create a to-do list information message for a designated AQD user (specified by a role in the facility inventory) informing them of the change.
    * @return void
    */
   public void PTIOExpirationNotice(  )
      throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException;

   /**
    * This method fullfils LSRD requirement U-22-Autonomous_Analyses-1-9: "PTIO/FEPTIO expiration in Facility Inventory - STARS2 shall provide autonomous processing to update the 'Current PTO/PTIO/Title V regulatory status' fields in the EU object in the facility inventory. For each PTIO/EU combination with an EU level permit status field of 'Active' and whose permit expiration date is past, STARS2 shall set the facility inventory field as follows: If a PTIO/FEPTIO renewal application with an application date that falls between the PTIO/FEPTIO's final issuance and expiration dates exists and the facility operating status is not 'shutdown', then set the EU's 'Current PTO/PTIO/Title V regulatory status' field to 'Extended PTIO' or 'Extended FEPTIO' and set the EU level permit status field to 'Extended' (overwrites previous value of 'Active'). Else set the 'Current PTO/PTIO/Title V regulatory status' field to 'Expired PTIO' or 'Expired FEPTIO' and set the EU level permit status field to 'Expired' (overwrites previous value of 'Active'). If STARS2 changes the status field to 'Expired PTIO' or 'Expired FEPTIO', then it shall create a to-do list information message for a designated AQD user (specified by a role in the facility inventory) informing them of the change."
    * @return void
    */
   public void PTIOExpirationFacilityProfile(  )
      throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException;

   /**
    * This method alerts AQD users that state PTO permits are expired for their facilities. It is expected that this method will be called everyday via the scheduler (via a stub scheduler routine). This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-5: State PTO expiration - STARS2 shall provide autonomous processing to update the EU level permit status field of state PTO permits. For each state PTO/EU permit with a permit status field of 'Active' and whose permit expiration date is past STARS2 shall set the status field to either 'Extended' or 'Expired'. If a renewal application for that EU has been received with an application date later than the final issuance date and prior to the expiration date of the PTIO permit, then STARS2 shall set the EU level permit status field to 'Extended', else STARS2 shall set it to 'Expired'. If STARS2 changes the permit status field to 'Expired', then it shall create a to-do list information message for a designated AQD user (specified by a role in the facility inventory) informing them of the change in status This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-10: State PTO/FESOP expiration in Facility Inventory - STARS2 shall provide autonomous processing to update the 'Current PTO/PTIO/Title V regulatory status' fields in the EU object in the facility inventory. For each State PTO(including FESOPs)/EU combination with an EU level permit status field of 'Active' and whose permit expiration date is past, STARS2 shall set the facility inventory field as follows: If a PTIO/FEPTIO renewal application with an application date that falls between the State PTO/FESOP's final issuance and expiration dates exists and the facility operating status is not 'shutdown', then set the EU's 'Current PTO/PTIO/Title V regulatory status' field to 'Extended PTO' or 'Extended FESOP' and set the EU level permit status field to 'Extended' (overwrites previous value of 'Active'). Else set the 'Current PTO/PTIO/Title V regulatory status' field to 'Expired PTO' or 'Expired FESOP' and set the EU level permit status field to 'Expired' (overwrites previous value of 'Active'). If STARS2 changes the status field to 'Expired PTO' or 'Expired FESOP', then it shall create a to-do list information message for a designated AQD user (specified by a role in the facility inventory) informing them of the change.
    * @return void
    */
   //public void StatePTOExpirationNotice(  )
   //   throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException;

   /**
    * This method alerts AQD users that Title V Permits are about to expire for their facilities. It is expected that this method will be called everyday via the scheduler (via a stub scheduler routine). This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-10: State PTO/FESOP expiration in Facility Inventory - STARS2 shall provide autonomous processing to update the 'Current PTO/PTIO/Title V regulatory status' fields in the EU object in the facility inventory. For each State PTO(including FESOPs)/EU combination with an EU level permit status field of 'Active' and whose permit expiration date is past, STARS2 shall set the facility inventory field as follows: If a PTIO/FEPTIO renewal application with an application date that falls between the State PTO/FESOP's final issuance and expiration dates exists and the facility operating status is not 'shutdown', then set the EU's 'Current PTO/PTIO/Title V regulatory status' field to 'Extended PTO' or 'Extended FESOP' and set the EU level permit status field to 'Extended' (overwrites previous value of 'Active'). Else set the 'Current PTO/PTIO/Title V regulatory status' field to 'Expired PTO' or 'Expired FESOP' and set the EU level permit status field to 'Expired' (overwrites previous value of 'Active'). If STARS2 changes the status field to 'Expired PTO' or 'Expired FESOP', then it shall create a to-do list information message for a designated AQD user (specified by a role in the facility inventory) informing them of the change.
    * @return void
    */
   public void StatePTOExpirationFacilityProfile(  )
      throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException;

   /**
    * This method alerts AQD users that Title V Permits are about to expire for their facilities. It is expected that this method will be called everyday via the scheduler (via a stub scheduler routine). This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-6: Title V Permit expiration - STARS2 shall provide autonomous processing to update the 'permit status' field of Title V Permits. For each Title V Permit with a permit status field of 'Active' and whose permit expiration date is past, STARS2 shall set the permit status field as follows: If a TV renewal application exists with an application date of at least 180 days but less than 18 months prior to the expiration date of the Active Title V Permit, then set the permit status field to 'Extended'. Else set the permit status field to 'Expired'. If STARS2 changes the permit status field to 'Expired', then it shall create a to-do list information message for a designated AQD user (specified by a role in the facility inventory) informing them of the change in status. This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-8: Title V Permit expiration in Facility Inventory - STARS2 shall provide autonomous processing to update the 'Title V Facility permit status' and 'Current PTO/PTIO/Title V regulatory status' fields in the facility inventory. For each Title V Permit with a permit status field of 'Active' and whose permit expiration date is past, STARS2 shall set the facility inventory field as follows: If a TV renewal application with an application date of at least 180 days but less than 18 months prior to the expiration date of the Active Title V Permit exists and the facility operating status is not 'shutdown', then set the 'Title V Facility permit status' field to 'Extended'. Else set the field to 'Expired'. If a TV renewal application with an application date of at least 180 days but less than 18 months prior to the expiration date of the Active Title V Permit exists, then set the 'Current PTO/PTIO/Title V regulatory status' field to 'Extended Title V Permit' where the status was previously 'Active Title V Permit' and the emissions unit does not have an operating status of 'shutdown' or 'invalid' (the field should not be set if the value was 'None' or 'PBR'). Else set the field to 'Expired' with all the same qualifications except no renewal received. If STARS2 changes the permit status field to 'Expired' for the facility status, then it shall create a to-do list information message for a designated AQD user (specified by a role in the facility inventory DO/LAA Permit Writer) informing them of the change.
    * @return void
    */
   public void TVPTOExpirations(  )
      throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException;

   /**
    * This method alerts AQD users that Title V Permits are about to expire for their facilities. It is expected that this method will be called everyday via the scheduler (via a stub scheduler routine). This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-7: Title V Permit expiration warning - STARS2 shall provide autonomous processing to alert AQD users of expiring Title V Permits. For each facility with an active Title V Permit, STARS2 shall create a to-do list information message for a designated AQD user (specified by a role in the facility inventory) exactly NNN days or exactly (NNN - 80) days prior to the permit expiration date if no Title V Permit renewal application has been received from that facility. The renewal application must have an application date of at least 180 days but less than 18 months prior to the expiration date of the Active Title V Permit. The value of NNN shall be configurable as a system parameter with a default value of 180. STARS2 shall exclude from this processing those facilities with a facility operating status of 'Shutdown'
    * @return void
    */
   public void TVPTOExpirationWarning(  )
      throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException;

   /**
    * This method alerts AQD users that Title V Permits are about to expire for their facilities. It is expected that this method will be called everyday via the scheduler (via a stub scheduler routine). This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-11: Title V Permit late application or no application (NOV) letter - STARS2 shall provide autonomous processing to alert AQD users to send Title V Permit late application or no application (NOV) letters. For each Title V Permit with a permit status field of 'Active', STARS2 shall search for facilities where all of the following conditions are true: 1. The facility has a Title V Permit with a permit status field of 'Active' and an expiration date that falls within the next 6 months 2. No Title V Permit renewal permit application with an application date that falls in the interval of 18 months to 180 days prior to the permit's expiration date exists 3. The STARS2 correspondence history contains no record of a 'late application or no application letter' with a 'date sent' field within the last 6 months 4. The facility's operational status is not 'Shutdown' If so, STARS2 shall create a to-do list information message for a designated AQD user (specified by a role in the facility inventory).
    * @return void
    */
   public void TVPTOLateApplication(  )
      throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException;

   /**
    * U-22-Autonomous_Analyses-1-12 Draft issue date but no draft public notification date - STARS2 shall provide autonomous processing to alert AQD users of missing Draft public notification dates. STARS2 shall detect occurrences of PTI, PTIO, and Title V Permit objects where all of the following conditions are true: 1. The permit status is 'Issued draft' 2. The draft issuance date is set (not null) 3. The current date is at least NN days after the draft issuance date where NN is a configurable system parameter with a default value of 5 4. The draft public notification date is not set (is null) 5. The corresponding facility's operational status is not 'Shutdown' If so, STARS2 shall create a to-do list information message for a designated AQD user (specified by a role in the facility inventory CO Issuance Coordinator).
    * @return void
    */
   public void DraftIssueNoPublicNotice(  )
      throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException;

   /**
    * U-22-Autonomous_Analyses-1-13 Draft public hearing newspaper request date but no public hearing notice date - STARS2 shall provide autonomous processing to alert AQD users of missing Draft public hearing newspaper request dates. STARS2 shall detect occurrences of PTI, PTIO, and Title V Permit objects where all of the following conditions are true: 1. The permit status is 'Issued draft' 2. The public hearing newspaper request date is set (not null) 3. The current date is at least NN days after the public hearing newspaper request date where NN is a configurable system parameter with a default value of 5 4. The public hearing notice date is not set (is null) 5. The corresponding facility's operational status is not 'Shutdown' If so, STARS2 shall create a to-do list information message for a designated AQD user (specified by a role in the facility inventory CO Issuance Coordinator).
    * @return void
    */
   public void DraftNewspaperRequestNoHearingDate(  )
      throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException;

   /**
    * This method alerts AQD users that Title V Permits are about to expire for their facilities. It is expected that this method will be called everyday via the scheduler (via a stub scheduler routine). This method fullfils LSRD requirement U-22-Autonomous_Analysis-1-14: PPP issue date but no certified mail receipt date - STARS2 shall provide autonomous processing to alert AQD users of missing PPP certified mail receipt dates. STARS2 shall detect occurrences of Title V Permit objects where all of the following conditions are true: 1. The correspondence type is 'PPP Title V Permit To Operate (PTO)' 2. The current date is at least NN days after the 'Date Generated' attribute where NN is a configurable system parameter with a default value of 15 3. The 'Certified Mail Tracking ID' is set (not null) 4. The 'Certified Mail Receipt Date' is not set If so, STARS2 shall create a to-do list information message for a designated AQD user (specified by a role in the facility inventory CO Notice Generator/Batch Processor).
    * @return void
    */
   //public void PPPIssueDateNoCertMailReceipt(  )
   //   throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException;

   public void addAppEUsToPermits(  )
      throws java.rmi.RemoteException;

}