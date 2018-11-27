package us.oh.state.epa.stars2.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.CetaBaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FacilityHistory;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityHistList;
import us.oh.state.epa.stars2.webcommon.ValidationBase;

@SuppressWarnings("serial")
public class CetaPopulateFCEs extends ValidationBase implements Job {
    protected static FileWriter outStream;
    private static int idOffset = 0;  //dennis
    // private static int idOffset2 =  60000;  //dennis
    static int NUM_TO_PROCESS = 1000000;
    public static String directory = "C:/Projects";
	private FacilityService facilityService;
	private FullComplianceEvalService fullComplianceEvalService;

	public FullComplianceEvalService getFullComplianceEvalService() {
		return fullComplianceEvalService;
	}

	public void setFullComplianceEvalService(
			FullComplianceEvalService fullComplianceEvalService) {
		this.fullComplianceEvalService = fullComplianceEvalService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    public CetaPopulateFCEs() {
        super();
    }

    /*

    select count(*) from stars2.ce_fce;
    
    C A R E F U L L    W I T H    T H E S E   B E L O W :
    select * from stars2.cm_sequence_def where sequence_nm = 'CE_facility_hist_id';   
    update stars2.cm_sequence_def set last_used_num = 1000 where sequence_nm = 'CE_facility_hist_id';
    
    delete stars2.ce_facility_neshaps_xref;
    delete stars2.ce_facility_nsps_subparts;
    delete stars2.ce_facility_nsr_pollutants;
    delete stars2.ce_facility_psd_pollutants;
    delete stars2.ce_facility_hist;
     C A R E F U L L    W I T H    T H E S E   A B O V E :
    
    delete stars2.ce_facility_neshaps_xref where facility_hist_id in
     (select facility_hist_id from stars2.ce_fce);
    delete stars2.ce_facility_nsps_subparts where facility_hist_id in
     (select facility_hist_id from stars2.ce_fce);
     delete stars2.ce_facility_nsr_pollutants where facility_hist_id in
     (select facility_hist_id from stars2.ce_fce);
     delete stars2.ce_facility_psd_pollutants where facility_hist_id in
     (select facility_hist_id from stars2.ce_fce);
    delete stars2.ce_facility_hist where facility_hist_id in
     (select facility_hist_id from stars2.ce_fce);

    update stars2.ce_site_visit set fce_id = null where fce_id is not null;
    update stars2.ce_stack_test  set fce_id = null where fce_id is not null;

    delete stars2.CE_FCE_ATTACHMENT;
    delete stars2.ce_fce;
    
    select * from stars2.cm_sequence_def where sequence_nm = 'S_Fce_Id';
    update stars2.cm_sequence_def set last_used_num = 20000 where sequence_nm = 'S_Fce_Id';

     * Design 0f CETA Migration of FCE and FCE_Scheuduled records:
     *   First populate Stars2 FullComplianceEval objects from CETA table FCE.
     *   Then go through each of the FCE_Scheduled records.
     *   If there is only one FullComplianceEval record for the FFY this is scheduled
     *     then add the scheduling information into the record.  It is an error if that
     *     record already has scheduling information--in which case we log the CETA fceId value
     *     to handle it manually.
     *   If there are more than one FullComplianceEval record for that FFY scheduled
     *     then do not apply any of them and log that CETA scheduled_id value
     *     (and the Stars2 FCE_ID values--same as CETA FCE_IDs) to handle them manually.
     *     
     *   If we find a lot of exceptions then we will need to replan.  Otherwise, STARS2 can
     *   be updated manually with these exceptions.
     */
    public void process() {
        try {
            try {
                outStream = new FileWriter(new File(directory + File.separator + "migrationData" + File.separator, "fceMigrationLog.txt"));
                outStream.write("Starting migration of CETA Inspections\n");
                outStream.flush();
            } catch (IOException ioe) {
                logger.error(ioe.getMessage(), ioe);
            }
            CetaGetUserId.loadTable(directory);
            boolean rtn = CetaFce.initialize("cetaFCEs.txt", logger); 
            if(!rtn) return;
            CetaFce cFce = new CetaFce();
            int tempCnt = NUM_TO_PROCESS;
            while(true) {
                //if(true) break;  // temp  DENNIS FOR TESTING Already have Inspections, just need schedules
                tempCnt--;
                if(tempCnt < 0) break;
                cFce = CetaFce.next();
                if(cFce == null) break;
                FullComplianceEval fce = new FullComplianceEval();
                fce.setLegacyInspection(true);

                if(cFce.fceId == null) {
                    // id is null.
                    outStream.write("fceId is null, previousFceId=" + CetaFce.prevFceId + "\n");
                }
                fce.setId(cFce.fceId);

                String facilityId = cFce.facility_id;
                if(facilityId == null) {
                    outStream.write("facilityId is null, fceId=" + fce.getId() + "\n");
                }
                Facility facility = null;
                Facility histFacility = null;
                Facility curFac = getFacilityService().retrieveFacility(facilityId);
                if(curFac == null) {
                    outStream.write("Did not find current facility, facilityId=" + facilityId + ", fceId=" + fce.getId() + "\n");
                    continue;
                }
                fce.setDateEvaluated(cFce.getFceDate());
                if(fce.getDateEvaluated() == null) {
                    facility = curFac;
                } else {
                    Integer fpId = curFac.getFpId();
                    // If not evaluated then use current otherwise use most recent
                    // historic profile that is older than evaluated date.
                    FacilityHistList fhl = getFacilityService().searchFacilitiesHist(facilityId, fce.getDateEvaluated());
                    if(fhl != null) {
                        fpId = fhl.getFpId();
                    } else {
                        outStream.write("Did not find historic facilities, facilityId=" + facilityId + ", fceId=" + fce.getId() + "\n");
                        continue;
                    }
                    histFacility = getFacilityService().retrieveFacilityProfile(fpId);
                    if(histFacility == null) {
                        outStream.write("Did not find historic facility, fpId=" + fpId + ", fceId=" + fce.getId() + "\n");
                        continue;
                    }
                    facility = histFacility;
                }
                fce.setFpId(facility.getFpId());
                fce.setFacilityId(facility.getFacilityId());
                fce.setCreatedDt(cFce.date_entered);
                List<Integer> usr = CetaGetUserId.getId(cFce.entered_by, facility.getDoLaaCd());
                for(Integer userId : usr) {
                    fce.setCreatedById(userId);
                    break;  // use first one
                }
                fce.setDateEvaluated(cFce.fceDate);
                usr = CetaGetUserId.getId(cFce.evaluator, facility.getDoLaaCd());
                if(usr != null && usr.size() > 0) {
                    fce.setEvaluator(usr.get(0));
                }
                fce.setEvalAfsId(cFce.getAfs_actionId());
                fce.setEvalAfsDate(cFce.getAfs_sentDate());
                
                FacilityHistory fh = new FacilityHistory();
                // fh.setAirProgramCompCd(ComplianceStatusDef.ON_SCHEDULE);
//                fh.setMactCompCd(ComplianceStatusDef.ON_SCHEDULE);
//                fh.setSipCompCd(ComplianceStatusDef.ON_SCHEDULE);
                
                boolean bTV = getTrueFalse(cFce.chkTV);
                boolean bSM = getTrueFalse(cFce.chkSM);
                boolean bNonHPV = getTrueFalse(cFce.chkNonHPV);
                fh.setStartDate(fce.getDateEvaluated());

                Boolean a = getBoolean(cFce.chkNeshap);
                if(a != null) {
                    fh.setNeshaps(a);
                }

                a = getBoolean(cFce.chkNsps);
                if(a != null) {
                    fh.setNsps(a);
                }

                a = getBoolean(cFce.chkMact);
                if(a != null) {
                    fh.setMact(a);
                }

                a = getBoolean(cFce.chkPsd);
                if(a != null) {
                    fh.setPsd(a);
                }

                a = getBoolean(cFce.chkNonAtt);
                if(a != null) {
                    fh.setNsrNonAttainment(a);
                }
                fce.setFacilityHistory(fh);

                fce.setMemo(cFce.memo_fce.replaceAll("'", "''"));

                //  create the Inspection record
                FullComplianceEval createdFce = null;
                try {
                    // DENNIS OFFSET records to not disturn existing records.
                    fce.setId(fce.getId() + idOffset);
                    createdFce = getFullComplianceEvalService().createMigratedFce(fce);
                    if(createdFce == null) {
                        outStream.write("Failed to create FCE.  fceId=" + fce.getId() + "\n");
                    }
                } catch(Exception e) {
                    String s = "Failed to create Inspection with error " + e.getMessage() + ".  fceId=" + fce.getId() + ", facility=" + fce.getFacilityId() ;
                    logger.error(s, e);
                    outStream.write(s  + "\n");
                }
                outStream.flush();
            }
        } catch (IOException ioe) {
            try {
                logger.error(ioe.getMessage(), ioe);
                outStream.write("IOException: " + ioe.getMessage() + "\n");
                outStream.flush();
            } catch (IOException x) {
                logger.error(x.getMessage(), x);
                ;
            }
        }

        //============================ phase 2  add in schedule info =====================

        try {
            outStream.write("Starting migration of CETA Inspection Schedules\n");
            outStream.flush();
            boolean rtn = CetaFceScheduled.initialize("cetaFceSchedules.txt", logger); 
            if(!rtn) return;
            CetaFceScheduled cFceS = new CetaFceScheduled();
            int tempCnt = NUM_TO_PROCESS;
            while(true) {
                tempCnt--;
                if(tempCnt < 0) break;
                cFceS = CetaFceScheduled.next();
                if(cFceS == null) break;
                FullComplianceEval fceS = new FullComplianceEval();
                fceS.setLegacyInspection(true);

                if(cFceS.getScheduled_id() == null) {
                    // id is null.
                    outStream.write("scheduled_id is null, previous=" + CetaFceScheduled.prevScheduled_id + "\n");
                }
                // cannot set the id because CETA uses the same domain for ids in Inspections and Scheduled Inspections.
                // If we need to create a new record, assign from Stars2 next sequence number.
                // fceS.setId(cFceS.getScheduled_id());

                String facilityId = cFceS.getFacility_id();
                fceS.setFacilityId(facilityId);

                Timestamp lastDate = cFceS.getLastDate_each_quarter();
                Facility facility = null;
                Facility histFacility = null;
                Facility curFac = getFacilityService().retrieveFacility(facilityId);
                if(curFac == null) {
                    outStream.write("Did not find current facility, facilityId=" + facilityId + ", old scheduledId=" + cFceS.getScheduled_id() + "\n");
                    continue;
                }
                if(lastDate == null) {
                    facility = curFac;
                } else {
                    Integer fpId = curFac.getFpId();
                    // If not evaluated then use current otherwise use most recent
                    // historic profile that is older than evaluated date.
                    FacilityHistList fhl = getFacilityService().searchFacilitiesHist(facilityId, lastDate);
                    if(fhl != null) {
                        fpId = fhl.getFpId();
                    } else {
                        outStream.write("Did not find historic facilities, facilityId=" + facilityId + ", old scheduledId=" + cFceS.getScheduled_id() + "\n");
                        continue;
                    }
                    histFacility = getFacilityService().retrieveFacilityProfile(fpId);
                    if(histFacility == null) {
                        outStream.write("Did not find historic facility, fpId=" + fpId + ", old scheduledId=" + cFceS.getScheduled_id() + ", facility=" + fceS.getFacilityId()  + "\n");
                        continue;
                    }
                    facility = histFacility;
                }
                fceS.setFpId(facility.getFpId());

                String goal = cFceS.getFce_goal();
                if(goal == null) {
                    outStream.write("fce_goal is null, previousScheduled_id=" + CetaFceScheduled.prevScheduled_id + "\n");
                } else {
                    // convert to Timestamp
                    if(goal.length() < 7) {
                        outStream.write("goal has fewer characters than 7, old scheduledId=" + cFceS.getScheduled_id() + "\n");
                    } else {
                        String month = goal.substring(0, 3).toUpperCase();
                        String year = goal.substring(goal.length() - 4);
                        int calMonth = -1;
                        if(month.equals("JAN")) calMonth = Calendar.JANUARY;
                        else if(month.equals("APR")) calMonth = Calendar.APRIL;
                        else if(month.equals("JUL")) calMonth = Calendar.JULY;
                        else if(month.equals("OCT")) calMonth = Calendar.OCTOBER;
                        else {
                            outStream.write("Goal does not specify beginning month, old scheduledId=" + cFceS.getScheduled_id() + ", facility=" + fceS.getFacilityId()  + "\n");
                        }
                        
                        if(calMonth != -1) {
                            Timestamp qStart = null;
                            try {
                                int calYear = Integer.parseInt(year);
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.YEAR, calYear);
                                cal.set(Calendar.MONTH, calMonth);
                                Timestamp t = new Timestamp(cal.getTimeInMillis());
                                qStart = CetaBaseDB.quarterStart(t);
                                fceS.setScheduledTimestamp(qStart);
                                Timestamp qStart2 = CetaBaseDB.quarterStart(lastDate);
                                if(!qStart.equals(qStart2)) {
                                    outStream.write("goal and lastDate_each_quarter do not correspond (used lastDate_each_quarter), old scheduledId=" + cFceS.getScheduled_id() + ", facility=" + fceS.getFacilityId()  + "\n");
                                }
                            }catch(NumberFormatException e) {
                                logger.error(e.getMessage(), e);
                                outStream.write("year portion of goal is incorrect\"" + year + "\", old scheduledId=" + cFceS.getScheduled_id() + ", facility=" + fceS.getFacilityId()  + "\n");
                            }
                            
                            
                            Boolean sent = getBoolean(cFceS.getSenttoUSEPA());
                            if(sent != null && sent) {
                                // From Arunee: The sent date for Inspection schedule is October 1 of each fiscal year and data will be locked.
                                // DENNIS need to change and leave blank.  I should check for promised ones.
                                fceS.setUsEpaCommitted(true);
                                // Determine sent date.
                                fceS.setSchedAfsDate(CetaBaseDB.beginningFFY(qStart));
                                fceS.setSchedAfsId("legacy");  // needed to indicate exported (even thou not sent to AFS)
                            }
                      /*      Boolean a = getBoolean(cFceS.getTV());
                            if(a != null && a) {
                                fceS.setFrozenInspectionClassCd(InspectionClassDef.TV);
                            }

                            Boolean b = getBoolean(cFceS.getSM());
                            if(b != null && b) {
                                fceS.setFrozenInspectionClassCd(InspectionClassDef.FEPTIO);
                            }
                            if(a != null && b != null && a && b) {
                                outStream.write("Both TV and SM are set; old scheduledId=" + cFceS.getScheduled_id() + ", facility=" + fceS.getFacilityId()  + "\n");
                            }*/


                        } else {
                            outStream.write("month calculation is incorrect old scheduledId=" + cFceS.getScheduled_id() + ", facility=" + fceS.getFacilityId()  + "\n");
                        }
                    }
                }

                List<Integer> usr = CetaGetUserId.getId(cFceS.getFceScheduledStaff(), facility.getDoLaaCd()); 
                Integer evalId = null;
                if(usr != null && usr.size() > 0) {
                    for(Integer i : usr) {
                        if(evalId == null) evalId = i;
                        if(evalId.equals(CommonConst.UNKNOWN_USER_ID) && i != null) evalId = i;
                        if(evalId != null && !evalId.equals(CommonConst.UNKNOWN_USER_ID)) break;
                    }
                    fceS.setAssignedStaff(evalId);
                }
                if(usr.size() > 1) {
                    // more than one person scheduled.
                    outStream.write("More than one scheduled evaluator, used the first one (" + cFceS.getFceScheduledStaff() + "), fceId=" + fceS.getId() + ", facility=" + fceS.getFacilityId()  + "\n");
                }

                fceS.setMemo(cFceS.getMemo_fce_schedulted().replaceAll("'", "''"));

                // Locate corresponding Inspection records


                //  merge the Inspection Schedule record
                if(fceS.getScheduledTimestamp() != null) {
                    Calendar cal = Calendar.getInstance(); 
                    Timestamp beginning = CetaBaseDB.beginningFFY(fceS.getScheduledTimestamp());
                    cal.setTime(new Date(beginning.getTime()));
                    // add one year and subtract one second to get end of FFY.
                    cal.add(Calendar.YEAR, 1);
                    cal.add(Calendar.SECOND, -1);
                    Timestamp ending = new Timestamp(cal.getTimeInMillis());
                    FullComplianceEval fceAll[] =
                         getFullComplianceEvalService().retrieveFceBySearch(null, facilityId, null, null, null, null, null,
                                 null, null, beginning, ending, null, null, null, null, null, null);
                    ArrayList<FullComplianceEval> fceMatch = new ArrayList<FullComplianceEval>();
                    StringBuffer fceIdList = new StringBuffer();
                    for(FullComplianceEval f : fceAll) {
                        if(f.getId() >= idOffset) {
                            fceMatch.add(f);
                            fceIdList.append(f.getId() + "  ");
                        }
                    }
                    // See if any are exactly same quarter if more than one
                    if(fceMatch.size() > 1) {
                        for(FullComplianceEval fce :fceMatch) {
                            Timestamp ts = CetaBaseDB.quarterStart(fce.getDateEvaluated());
                            if(ts.equals(fceS.getScheduledTimestamp())) {
                                fceMatch = new ArrayList<FullComplianceEval>();
                                fceMatch.add(fce); // now it has exactly one
                                break;
                            }
                        }
                    }
                    if(fceMatch.size() == 0) {
                        // Look at the next two quarters to see if you find a match
                        cal.setTime(new Date(fceS.getScheduledTimestamp().getTime()));
                        cal.add(Calendar.MONTH, -3);  // Include one quarter before scheduled quarter
                        beginning = new Timestamp(cal.getTimeInMillis());
                        cal.setTime(new Date(fceS.getScheduledTimestamp().getTime()));
                        // add six months and subtract one second
                        cal.add(Calendar.MONTH, 9);  // Include two quarters beyond current scheduled quartet=r
                        cal.add(Calendar.SECOND, -1);
                        ending = new Timestamp(cal.getTimeInMillis());
                        fceAll = getFullComplianceEvalService().retrieveFceBySearch(null, facilityId, null, null, null, null, null,
                                    null, null, beginning, ending, null, null, null, null, null, null);
                        if(fceAll.length > 0) {
                            fceMatch.add(fceAll[0]);
                        }
                    }
                    if(fceMatch.size() == 0) {
                        // Apparently Inspection never completed
                        // Create just the Inspection schedule.
                        outStream.write("Have Schedule for " + fceS.getScheduledTimestamp() + " but no Inspection completion--creating the Inspection schedule.  old scheduledId=" + cFceS.getScheduled_id() + ", facility=" + fceS.getFacilityId() + "\n");
                        FullComplianceEval createdFce = null;
                        try {
                            // DENNIS OFFSET records to not disturb existing records.
                            // leave id null so it is assigned from available
                            createdFce = getFullComplianceEvalService().createFce(fceS);
                            if(createdFce == null) {
                                outStream.write("Failed to create Inspection Schedule.  old scheduledId=" + cFceS.getScheduled_id() + "\n");
                            }
                        } catch(Exception e) {
                            String s = "Failed to create Inspection Schedule with error " + e.getMessage() + ".  old scheduledId=" + cFceS.getScheduled_id() + ", facility=" + fceS.getFacilityId();
                            logger.error(s, e);
                            outStream.write(s  + "\n"); 
                        }
                    } else if(fceMatch.size() == 1) {
                        // We found the match, merge in
                        FullComplianceEval merged = fceMatch.get(0);

                        // See if already scheduled
                        if(merged.getAssignedStaff() != null || merged.getScheduledTimestamp() != null) {
                            outStream.write("Already found a schedule for fceId=" + merged.getId() + ", additional schedule (created separately) is old scheduledId=" + cFceS.getScheduled_id() + ", facility=" + fceS.getFacilityId()  + "\n");
                            try {
                                FullComplianceEval createdFce = null;
                                createdFce = getFullComplianceEvalService().createFce(fceS);
                                if(createdFce == null) {
                                    outStream.write("Failed to create additional Inspection Schedule.  old scheduledId=" + cFceS.getScheduled_id() + "\n");
                                }
                            } catch(Exception e) {
                                String s = "Failed to create additional Inspection Schedule with error " + e.getMessage() + ".  old scheduledId=" + cFceS.getScheduled_id() + ", facility=" + fceS.getFacilityId();
                                logger.error(s, e);
                                outStream.write(s  + "\n");
                            }
                        } else {
                            // confirm inspection classification in schedule and completion are the same
                     /*       if(merged.getFrozenInspectionClassCd() != null) {
                                if(merged.getFrozenInspectionClassCd().equals(InspectionClassDef.NON_HPF) &&
                                        fceS.getFrozenInspectionClassCd() != null) {
                                    // Note that we only set Inspection Classification into Scheduled one if TV or SM.
                                    String s = "Mismatch: Inspection Schedule Inspection Classification set but Completion is Non-HPF (value in Completion left unchanged).  Inspection Id = " + merged.getId() + ", old scheduledId=" + cFceS.getScheduled_id() + ", facility=" + fceS.getFacilityId();
                                    outStream.write(s  + "\n");
                                } else if(fceS.getFrozenInspectionClassCd() != null &&
                                        !fceS.getFrozenInspectionClassCd().equals(merged.getFrozenInspectionClassCd())) {
                                    String s = "Mismatch: Inspection Completion/Schedule Inspection Classifications not the same (value in Completion left unchanged).  Inspection Id = " + merged.getId() + ", old scheduledId=" + cFceS.getScheduled_id() + ", facility=" + fceS.getFacilityId();
                                    outStream.write(s  + "\n");
                                }
                            }*/
                            
                            merged.setUsEpaCommitted(fceS.isUsEpaCommitted());
                            merged.setSchedAfsId(fceS.getSchedAfsId());
                            //merged.setSchedAfsDate(fceS.getSchedAfsDate());
                            merged.setScheduledTimestamp(fceS.getScheduledTimestamp());
                            merged.setAssignedStaff(fceS.getAssignedStaff());
                            merged.setSchedAfsDate(CetaBaseDB.beginningFFY(fceS.getScheduledTimestamp()));
                            if(fceS.getMemo() != null && fceS.getMemo().length() > 0 &&
                                    merged.getMemo() != null && merged.getMemo().length() > 0)   
                                merged.setMemo(fceS.getMemo() + "  " + fceS.getMemo());
                            try {
                                getFullComplianceEvalService().modifyFce(merged);
                            } catch(Exception e) {
                                String s = "Failed to merge Inspection Schedule with error " + e.getMessage() + ".  FceId=" + merged.getId() + ", old scheduledId=" + cFceS.getScheduled_id() + ", facility=" + fceS.getFacilityId();
                                logger.error(s, e);
                                outStream.write(s  + "\n");
                            }
                        }
                    } else {
                        // More than one matching records.
                        outStream.write("More than one Inspection matched Inspection Schedule.  Separate Inspection Schedule created.  Inspections=" + fceIdList + " Old scheduledId=" + cFceS.getScheduled_id() + ", facility=" + fceS.getFacilityId()  + "\n");
                        try {
                            FullComplianceEval createdFce = null;
                            createdFce = getFullComplianceEvalService().createFce(fceS);
                            if(createdFce == null) {
                                outStream.write("Failed to create Inspection Schedule.  fceId=" + fceS.getId() + "\n");
                            }
                        } catch(Exception e) {
                            String s = "Failed to create Inspection Schedule with error " + e.getMessage() + ".  old scheduledId=" + cFceS.getScheduled_id() + ", facility=" + fceS.getFacilityId();
                            logger.error(s, e);
                            outStream.write(s  + "\n");
                        }
                    }
                }
                outStream.flush();
            }
        } catch (IOException ioe) {
            try {
                logger.error(ioe.getMessage(), ioe);
                outStream.write("IOException: " + ioe.getMessage() + "\n");
                outStream.flush();
            } catch (IOException x) {
                logger.error(x.getMessage(), x);
                ;
            }
        } catch (Exception e) {
            logger.error("Got Exception " + e.getMessage(), e);
            try {
                outStream.write("Got Exception " + e.getMessage());
                outStream.flush();
            } catch(Exception ee) {
                logger.error(ee.getMessage(), ee);
                ;
            }
        }

        CetaGetUserId.dumpMissingNames(directory);
    }

    static protected Boolean getBoolean(String t, String f) {
        if("1".equals(t) && "0".equals(f)) return true;
        if("1".equals(f) && "0".equals(t)) return false;
        return null;
    }

    static protected Boolean getBoolean(String b) {
        if("1".equals(b)) return true;
        if("0".equals(b)) return false;
        return null;
    }
    
    static protected boolean getTrueFalse(String b) {
        if("1".equals(b)) return true;
        if("0".equals(b)) return false;
        return false;
    }
    
    public void execute(JobExecutionContext context) throws JobExecutionException {        
        logger.error("INFO: CetaPopulateFCEs is executing.");
        
        // create migrationData directory
        // WARNING.  The system must create this directory in order to have the correct
        // permissions to add files.  The first time this is run to
        // create the directory, the migration will fail because we will not have
        // been able to add the input data file under that directory.
        // The solution is to add the file after the migrationData directory is
        // created and run the scheduled job again.
        directory = DocumentUtil.getFileStoreRootPath();
        try {
            DocumentUtil.mkDir(File.separator + "migrationData");
        } catch (IOException e) {
            logger.error("Exception creating migrationData directory \"" + directory + "\"", e);
        }
        
        try {
            CetaPopulateFCEs instance = new CetaPopulateFCEs();
            instance.process();
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.error("INFO: CetaPopulateFCEs has completed.");
    }
}
