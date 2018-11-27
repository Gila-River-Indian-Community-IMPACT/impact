package us.oh.state.epa.stars2.util;

import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementMilestone;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementNote;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityHistList;
import us.oh.state.epa.stars2.webcommon.ValidationBase;

@SuppressWarnings("serial")
public class CetaPopulateEnforcementsOriginal extends ValidationBase implements Job {
    protected static FileWriter outStream;
    
    Facility facility = null;
    Facility curFac = null;
    public static String directory = "C:/Projects";
    
    //private EnforcementService enforcementService;
	private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    /*public EnforcementService getEnforcementService() {
		return enforcementService;
	}
	public void setEnforcementService(EnforcementService enforcementService) {
		this.enforcementService = enforcementService;
	}*/

    public CetaPopulateEnforcementsOriginal() {
        super();
    }
    //  select count(*) from stars2.ce_enforcement where enforcement_id > 50000
    /*   Delete the records.
     delete stars2.ce_facility_neshaps_xref where facility_hist_id in
     	(select facility_hist_id from stars2.ce_enforcement_action);

	 delete stars2.ce_facility_nsps_subparts where facility_hist_id in
     	(select facility_hist_id from stars2.ce_enforcement_action);

	 delete stars2.ce_facility_nsr_pollutants where facility_hist_id in
     	(select facility_hist_id from stars2.ce_enforcement_action);

	 delete stars2.ce_facility_psd_pollutants where facility_hist_id in
     	(select facility_hist_id from stars2.ce_enforcement_action);
     delete stars2.ce_facility_hist where facility_hist_id in
     	(select facility_hist_id from stars2.ce_enforcement_action);
     
     delete stars2.CE_ENFORCEMENT_EVALUATOR_XREF where action_id in 
         (select action_id from stars2.CE_ENFORCEMENT_ACTION);
     delete stars2.CE_ENFORCEMENT_VIOLATION_XREF;
     delete stars2.CM_NOTE where note_id in 
         (select note_id from stars2.CE_ENFORCEMENT_NOTE_XREF);
     delete stars2.CE_ENFORCEMENT_NOTE_XREF;
     delete stars2.CE_ENFORCEMENT_MILESTONE;
     delete stars2.CE_ENFORCEMENT_ATTACHMENT;
     delete stars2.CE_ENFORCEMENT_ACTION;
     delete stars2.CE_ENFORCEMENT;
     

     
    select * from stars2.cm_sequence_def where 
        sequence_nm = 'CE_enforcement_milestone_id'
        or sequence_nm = 'CE_enforcement_id'
        or sequence_nm = 'CE_enforcement_action_id';
        
        
    update stars2.cm_sequence_def set last_used_num = 1000 where sequence_nm = 'CE_enforcement_milestone_id';
    update stars2.cm_sequence_def set last_used_num = 10000 where sequence_nm = 'CE_enforcement_id';
    update stars2.cm_sequence_def set last_used_num = 1 where sequence_nm = 'CE_enforcement_action_id';
    
    // AFTER MIGRATION PERFORM THIS (or build it into migration).
    update stars2.ce_enforcement_action  set afs_sent_date = to_date('2000-01-01','yyyy-mm-dd') where  afs_id = 'legacy' and afs_sent_date is null

     
     
     
    */
/*    public void process() {
    	CetaEnforceAction cea = null;
        try {
            logger.error("CetaPopulateEnforcement directory is: \"" + directory + "\"");
            outStream = new FileWriter(new File(directory + File.separator + "migrationData" + File.separator, "enforcementMigrationLog.txt"));
            logger.error("CetaPopulateEnforcement 2");
            outStream.write("Starting migration of CETA Enforcements\n");
            logger.error("CetaPopulateEnforcement 3");
            outStream.flush();
            CetaGetUserId.loadTable(directory);
            boolean rtn0 = CetaEnforceNote.initialize("cetaEnforcementNotes.txt", logger);
            if(!rtn0) return;
            boolean rtn1 = CetaEnforceMilestone.initialize("cetaEnforcementMilestones.txt", logger);
            if(!rtn1) return;
            boolean rtn2 = CetaEnforceAction.initialize("cetaEnforcementActions.txt", logger);
            if(!rtn2) return;
            EnforcementNote en = null;
            EnforcementMilestone em = null;
            EnforcementAction ea = null;
            //Enforcement enforcement = null;
            ArrayList<EnforcementMilestone> milestones = null;
            
            CetaEnforceMilestone cem = null;
            CetaEnforceNote cen = null;
            Integer enfId = null;
            Integer nextEnfId = null;
            boolean enforcementErrors = false;
            while(true) {
                // Get actions
                while(!CetaEnforceAction.eof) {
                    if(cea == null) cea = CetaEnforceAction.next(logger);
                    if(CetaEnforceAction.eof) break;
                    nextEnfId = cea.enf_id;
                    if(cea.enf_id.equals(enfId)) {
                        if(enforcement == null) {
                            // create enforcement object to indicate there is at least one action
                            enforcement = new Enforcement();
                            curFac = getFacilityService().retrieveFacility(cea.facility_id);
                            if(curFac == null) {
                                outStream.write("Did not find current facility, facilityId=" + cea.facility_id + ", enf_id=" + cea.getEnf_id() + ", sequence_number=" + cea.getSequence_number() + ".  Skipped.\n");
                                outStream.flush();
                                enforcementErrors = true;
                                // read through remaining records for this enf_id
                                cea = skipEnforcement(cea);
                                nextEnfId = cea.enf_id;
                                break;
                            }
                            
                        }
                        ea = getEnforceAction(enforcement, cea, curFac);
                        enforcement.addEnforcementAction(ea);
                        // if first action then get historic fpId (actions ordered by violationDate)
                        if(enforcement.getEnforcementActions().size() == 1) {
                            Integer fId = locateFpid(cea);
                            enforcement.setFpId(fId);
                            if(fId == null) {
                                outStream.write("locateFpid failed, facilityId=" + cea.facility_id + ", enf_id=" + cea.getEnf_id() + ", sequence_number=" + cea.getSequence_number() + ".  Skipped.\n");
                                outStream.flush();
                                enforcementErrors = true;
                                // read through remaining records for this enf_id
                                cea = skipEnforcement(cea);
                                nextEnfId = cea.enf_id;
                                break;
                            }
                        }
                        processCaseInfo(cea, enforcement);
                        cea = null;  // this action has been used
                    } else {
                        // got to next facility; go on to milestones
                        break;
                    }
                }
                // get milestones
                milestones = new ArrayList<EnforcementMilestone>();
                while(!CetaEnforceMilestone.eof) {
                    if(cem == null) cem = CetaEnforceMilestone.next(logger);
                    if(CetaEnforceMilestone.eof) {
                        // no more milestones; go on to notes
                        break;
                    }

                    if(cem == null || cem.id == null) {
                        cem = null;  // for breakpoint
                    }
                    if(cem.id.equals(enfId)) {
                        em = getEnforcementMilestone(cem);
                        if(!enforcementErrors) {
                            // skip milestone
                            milestones.add(em);
                        }
                        cem = null;  // this milestone has been used
                    } else {
                        // no more milestones; go on to notes
                        break;
                    }
                }
                
                // get notes
                while(!CetaEnforceNote.eof) {
                    if(cen == null) cen = CetaEnforceNote.next(logger);
                    if(CetaEnforceNote.eof) break;
                    if(cen.enf_id.equals(enfId)) {
                        en = getEnforcementNote(cen);
                        if(!enforcementErrors) {  // skip note if errors
                            enforcement.addNote(en);
                        }
                        cen = null;  // this note has been used
                    } else {
                        // no more notes; create Stars2 Enforcement case
                        break;
                    }
                }
                enfId = nextEnfId;  // prepare for next case
                if(enforcement == null && !CetaEnforceAction.eof) {
                    // This is the first time through since the enf_id did not match
                    continue;
                }
                if(enforcement == null) {
                    // finished processing all records
                    break;
                }
                if(enforcementErrors) {
                    enforcementErrors = false;
                    enforcement = null;  // reset for next case
                    continue;  // lets continue with the next.
                }
                // create the entire Case/Actions/Milestones/Notes
                int offset = 0;
                enforcement.setEnforcementId(enforcement.getEnforcementId() + offset);
                for(EnforcementAction aElm : enforcement.getEnforcementActions()) {
                    aElm.setEnforcementId(aElm.getEnforcementId() + offset);
                }
                for(EnforcementNote nElm : enforcement.getNotes()) {
                    nElm.setEnforcementId(nElm.getEnforcementId() + offset);
                }
                Facility fac = null;
                try {
            		fac = getFacilityService().retrieveFacilityData(enforcement.getFpId());
            	} catch(Exception e) {
            		logger.error("Unable to get facility FP-ID : " +  enforcement.getFpId(), e);
            		fac = null;
            		try {
                        outStream.write("Exception: " + e.getMessage() + " continue processing;\n");
                        outStream.flush();
                        } catch (IOException x) {
                            ;
                        }
            	}
                for(EnforcementMilestone mElm : milestones) {
                    mElm.setEnforcementId(mElm.getEnforcementId() + offset);
                    if (fac != null) {
                    	checkMilestone(enforcement, mElm, fac);
                    }
                }
                
                try {
                    getEnforcementService().createEntireEnforcement(enforcement, milestones);
                } catch(Exception e) {
                    String s = "Failed to create enforcement with error " + e.getMessage() + ".  enf_Id=" + enforcement.getEnforcementId() + "\n";
                    outStream.write(s);
                    logger.error(s,e);
                }
                enforcement = null;  // reset for next case
                outStream.flush();
                if(CetaEnforceAction.eof) {
                    if(cem == null) cem = CetaEnforceMilestone.next(logger);
                    while(!CetaEnforceMilestone.eof) {
                        outStream.write("Unused milestone, id = " + cem.getId() + "\n");
                        cem = CetaEnforceMilestone.next(logger);
                    }
                    if(cen == null) cen = CetaEnforceNote.next(logger);
                    while(!CetaEnforceNote.eof) {
                        outStream.write("Unused notes, enf_id = " + cen.getEnf_id() + "\n");
                        cen = CetaEnforceNote.next(logger);
                    }
                    outStream.flush();
                    break;  // we are done
                }
            }
         CetaGetUserId.dumpMissingNames(directory);
        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
            try {
            outStream.write("IOException: " + ioe.getMessage() + "\n");
            outStream.flush();
            } catch (IOException x) {
                ;
            }
        } catch (RuntimeException e) {
            CetaGetUserId.dumpMissingNames(directory);
            try {
            	if (e.getMessage().contains("Unable to create directory")) {
            		String f = "/Facilities/" + cea.facility_id;
            		DocumentUtil.mkDir(f);
            	}
            } catch (Exception e1) {
            	logger.error("Unable to create facility directory; facility ID = " + cea.facility_id + e1);
            }
            throw e;
        }
    }*/
    
    /*private void checkMilestone(Enforcement enforcement, EnforcementMilestone mElm, Facility fac) {
    	try {
    		if (!mElm.getFacilityId().equals(fac.getFacilityId())) {
    			outStream.write("The facility ID : " + mElm.getFacilityId() + 
    							" in milestone is not matching the facility ID : " +
    							fac.getFacilityId() + " for enforcement ID : " + enforcement.getEnforcementId() + "\n");
    		}
    	} catch(Exception e) {
    		logger.error("Unable to verfiy Milestone facility ID; Milestone ID = " + mElm.getMilestoneId(), e);
    		try {
                outStream.write("Exception: " + e.getMessage() + " Continue processing\n");
                outStream.flush();
                } catch (IOException x) {
                    ;
                }
    	}
    }*/
    
    private CetaEnforceAction skipEnforcement(CetaEnforceAction skipCea)  {
        // Skip rest of case and return first good ceta record
        CetaEnforceAction rtn = null;
        try {
            while(!CetaEnforceAction.eof) {
                rtn = CetaEnforceAction.next(logger);
                if(!rtn.enf_id.equals(skipCea.enf_id)) {
                    break;
                }
            }
        }catch(IOException ioe) {

        }
        return rtn;
    }
    
    /*protected void processCaseInfo(CetaEnforceAction cea, Enforcement enforcement) throws IOException {
        enforcement.setFacilityId(cea.facility_id);
        enforcement.setEnforcementId(cea.getEnf_id());
 We set the case state later on when we create entrie enforcement in BO.
        // Use info in the CETA Action to set/update the Stars2 Case.
        // This includes at least setting the enforcement state.
        if(enforcement.getEnfCaseState() == null) {
            enforcement.setEnfCaseState(EnfCaseStateDef.init1);
        }
        if(AFSEnforcementTypeDef.EA.equals(cea.getActionTypeCd())) {
            enforcement.setEnfCaseState(EnfCaseStateDef.open2);
        }
       
        // handle violation codes
        Boolean hpv = get01(cea.getHpv());
        String codes = cea.getHpv_code();
        if((hpv == null || !hpv) && codes != null && codes.length() != 0) {
            outStream.write("Violation flag is incorrect for enf_id " + cea.getEnf_id() +
            		" in facility : " + cea.getFacility_id() +
                    " and sequence_number " + cea.sequence_number + ", hpvFlag is \"" +
                    cea.hpv + "\", codes are \"" +
                    cea.hpv_code + "\"\n");
        }

        if(hpv != null && hpv && (codes == null || codes.length() == 0)) {
            outStream.write("Violation flag is incorrect for enf_id " + cea.getEnf_id() +
                    " and sequence_number " + cea.sequence_number + ", hpvFlag is \"" +
                    cea.hpv + "\", codes are \"" +
                    cea.hpv_code + "\"\n");
        }

        // all codes are three charater codes
        int ndx = 0;
        ArrayList<String> violationTypeCodes = new ArrayList<String>();
        while(codes.length() > ndx) {
            if(codes.length() > ndx + 2) {
                String nxtCode = codes.substring(ndx, ndx + 3);
                violationTypeCodes.add(nxtCode);
                ndx = ndx + 4;  // skip over blank separator--if there is one
            } else {
                // bad remaining length
                outStream.write("Violation codes are incorrect for enf_id " + cea.getEnf_id() +
                		" in facility : " + cea.getFacility_id() +
                        " and sequence_number " + cea.sequence_number + ", codes are \"" +
                        cea.hpv_code + "\"\n");
                break;
            }
        }
        if (violationTypeCodes.size() > 0 || (hpv != null && hpv)) {
        	enforcement.setAddToHPVList(true);
        }
        
        if(enforcement.getViolationTypeCodes() == null || enforcement.getViolationTypeCodes().size() == 0) {
            enforcement.setViolationTypeCodes(violationTypeCodes); // put in with first action only
        }
        if(!enforcement.getViolationTypeCodes().containsAll(violationTypeCodes) ||
                !violationTypeCodes.containsAll(enforcement.getViolationTypeCodes())) {
            // One of the actions does not carry the same violation codes
            outStream.write("Violation codes are not the same in all actions of enf_id " + cea.getEnf_id() +
            		" in facility : " + cea.getFacility_id() +
            		" because sequence_number " + cea.sequence_number + " is not the same as the first one\n");
        }
        
        if (enforcement.getDiscoveredDate() == null) {
        	enforcement.setDiscoveredDate(cea.violationDate);
        } else if (!enforcement.getDiscoveredDate().equals(cea.violationDate)) {
        	outStream.write("ViolationDate is not the same in all actions of enf_id " + cea.getEnf_id() +
        			" in facility : " + cea.getFacility_id() +
        			" because sequence_number " + cea.sequence_number + " is not the same as the first one\n");
        	if (cea.violationDate == null) {
        		outStream.write("ViolationDate is not set of enf_id " + cea.getEnf_id() +
            			" in facility : " + cea.getFacility_id() +
            			" because sequence_number " + cea.sequence_number + "\n");
        	} else if (cea.violationDate.before(enforcement.getDiscoveredDate())) {
        		enforcement.setDiscoveredDate(cea.violationDate);
        	}
        }
        if (enforcement.getDayZeroDate() == null) {
        	enforcement.setDayZeroDate(cea.zeroDate);
        } else if (!enforcement.getDayZeroDate().equals(cea.zeroDate)) {
        	outStream.write("ZeroDate is not the same in all actions of enf_id " + cea.getEnf_id() +
        			" in facility : " + cea.getFacility_id() +
        			" because sequence_number " + cea.sequence_number + " is not the same as the first one\n");
        }
    }*/
    
    /*protected EnforcementAction getEnforceAction(Enforcement enforcement, CetaEnforceAction cea, Facility f) throws IOException {
        EnforcementAction ea = new EnforcementAction();
        ea.setEnforcement(enforcement);

        // IGNORE cea.getSequence_number()-- a new index will be created.
        ea.setEnforcementId(cea.getEnf_id());
        
        String actionCd = null;
        if(cea.getActionTypeCd().length() >= 2) {
            actionCd = cea.getActionTypeCd().substring(0, 2);
        }
        ea.setEnfActionTypeCd(actionCd);
        ea.setActionDate(cea.getActionDate());
        
        String typeViolation = cea.typeOfViolation;
        if(typeViolation != null) typeViolation = typeViolation.trim();
        if("Administrative Only".equals(typeViolation)) typeViolation = "A";
        if("Emission".equals(typeViolation)) typeViolation = "E";
        ea.setEnfViolationTypeCd(typeViolation);
        
//      "PCE / Other" "Stack Test"  "FCE"
//      "Title V Annual  Compliance Certifications";
        String disAction = cea.actionDiscovery;
        if (disAction != null) {
        	disAction = disAction.trim();
        	if (disAction.length() != 0) {
        		if ("PCE / Other".equals(disAction)) disAction = "O";
        		else if ("Stack Test".equals(disAction)) disAction = "S";
        		else if ("Title V Annual  Compliance Certifications".equals(disAction)) disAction = "T";
        		else if ("FCE".equals(disAction)) disAction = "F";
        		else if ("Site Visit".equals(disAction)) disAction = "P";
        		else {
        			outStream.write("unknown discovery action " + cea.actionDiscovery + "for enf_id " + cea.getEnf_id() +
        					" in facility : " + cea.getFacility_id() +
        					" and sequence_number " + cea.sequence_number +"\n");
        		}
        	}
        }
        ea.setEnfDiscoveryTypeCd(disAction);

        String cash = cea.getCashAmount();
        ea.setFinalCashAmount(cash);
        
        String sep = cea.getSepAmount();
        ea.setFinalSepAmount(sep);
        
        ea.setAfsMemo(cea.getMemoToAfs());
        ea.setAfsId(cea.getAfs_actionId());
        ea.setAfsSentDate(cea.getAfs_sentDate());
        
        Boolean form = get01(cea.getFormal());
        Boolean inForm = get01(cea.getInformal());
        if(form == null || inForm == null || form == inForm) {
            outStream.write("Formal/Informal mismatch, facilityId=" + enforcement.getFacilityId() + ", enf_id=" + cea.getEnf_id() + ", sequence_number=" + cea.getSequence_number() + ".  Skipped.\n");
        }
        
        String formCd = "I";
        if(form != null && form) formCd = "F";
        ea.setEnfFormOfActionCd(formCd);

        //  process evaluator including multiple evaluators.
        List<Integer> usr =  CetaGetUserId.getId(cea.evaluator, curFac.getDoLaaCd());
        if(usr != null && usr.size() > 0) {
            ea.setEvaluators(usr);
        }
       
        ea.setCreatedDt(cea.getDate_entered());
        List<Integer> enterByUsrs =  CetaGetUserId.getId(cea.entered_by, curFac.getDoLaaCd());
        if (enterByUsrs != null && enterByUsrs.size() > 0) {
        	ea.setCreatedById(enterByUsrs.get(0));
        } else {
        	ea.setCreatedById(null);
        }
       
        FacilityHistory fh = new FacilityHistory();
        
        Boolean a = getBoolean(cea.chkNeshap);
        if(a != null) {
            fh.setNeshaps(a);
        }

        a = getBoolean(cea.chkNsps);
        if(a != null) {
            fh.setNsps(a);
        }

        a = getBoolean(cea.chkMact);
        if(a != null) {
            fh.setMact(a);
        }

        a = getBoolean(cea.chkPsd);
        if(a != null) {
            fh.setPsd(a);
        }

        a = getBoolean(cea.chkNonAtt);
        if(a != null) {
            fh.setNsrNonAttainment(a);
        }
   
//        fh.setAirProgramCompCd(ComplianceStatusDef.ON_SCHEDULE);
//        fh.setMactCompCd(ComplianceStatusDef.ON_SCHEDULE);
//        fh.setSipCompCd(ComplianceStatusDef.ON_SCHEDULE);
        
        boolean bTV = getTrueFalse(cea.getChkTv());
        boolean bSM = getTrueFalse(cea.getChkSm());
        boolean bNonHPV = getTrueFalse(cea.chkNonHPV);
        int foundCnt = 0;
        if(bNonHPV) {
            foundCnt++;
            fh.setAirProgramCd(null);
        }
        if(bSM) {
            foundCnt++;
            fh.setAirProgramCd(AirProgramDef.SM_FESOP);
        }
        if(bTV) {
            foundCnt++;
            fh.setAirProgramCd(AirProgramDef.TITLE_V);
        }

        fh.setStartDate(ea.getActionDate());
        ea.setFacilityHistory(fh);
        
        return ea;
    }
    */
    Integer locateFpid(CetaEnforceAction cea) throws IOException {
        Facility histFacility = null;
        if(cea.getActionDate() == null) {
            outStream.write("ActionDate is null, facilityId=" + cea.facility_id + ", enf_id=" + cea.getEnf_id() + ", sequence_number=" + cea.getSequence_number() + ".  Skipped.\n");
            facility = curFac;
        } else { // pick profile to us
            Integer fpId = curFac.getFpId();
            // Use most recent historic profile that is older than evaluated date.
            try {
                FacilityHistList fhl = getFacilityService().searchFacilitiesHist(cea.facility_id, cea.actionDate);
                if(fhl != null) {
                    fpId = fhl.getFpId();
                } else {
                    outStream.write("Did not find historic facilities, facilityId=" + cea.facility_id + ", enf_id=" + cea.getEnf_id() + ", sequence_number=" + cea.getSequence_number() + ".  Skipped.\n");
                    return null;
                }
                histFacility = getFacilityService().retrieveFacilityProfile(fpId);
                if(histFacility == null) {
                    outStream.write("Did not find historic facility, fpId=" + fpId + ", facilityId=" + cea.facility_id + ", enf_id=" + cea.getEnf_id() + ", sequence_number=" + cea.getSequence_number() + ".  Skipped.\n");
                    return null;
                }
                facility = histFacility;
            } catch(RemoteException re) {
                String s = "Error looking for historic facility " + cea.facility_id;
                outStream.write(s);
                logger.error(s, re);
                return null;
            }
        }
        if(facility == null) {
            outStream.write("Did not find facility, facilityId=" + cea.facility_id + ", enf_id=" + cea.getEnf_id() + ", sequence_number=" + cea.getSequence_number() + ".  Skipped.\n");
            return null;
        }
         return facility.getFpId();
    }
    
    protected EnforcementMilestone getEnforcementMilestone(CetaEnforceMilestone cem) throws IOException {
        EnforcementMilestone m = new EnforcementMilestone();
        m.setFacilityId(cem.facility_id);
        m.setCompletionDate(cem.completionDate);
        m.setDeadlineDate(cem.deadlineDate);
        m.setEnforcementId(cem.id); // Note that the id field must be changed to be the enf_if it belongs to (not in CETA database)
        m.setMemo(cem.memo);
        m.setMilestoneOrRequirements(cem.milestone);
        m.setOrderDate(cem.orderDate);
        m.setPaymentAmount(cem.getPayment_amt());
        String settlementcase = cem.settlementcase;
        if (settlementcase != null) settlementcase = settlementcase.trim();
        if ("Findings & Orders(consensual)".equals(settlementcase)) settlementcase = "foc";
        else if ("Findings (Unilateral)".equals(settlementcase)) settlementcase = "fou";
        else if ("Consent Order".equals(settlementcase)) settlementcase = "csto";
        else if ("Court Order".equals(settlementcase)) settlementcase = "cto";
        else {
        	outStream.write("unknown milestone settlement case " + cem.settlementcase + "for enf_id " + cem.id +
        			" in facility : " + cem.getFacility_id() + "\n");
        }
        m.setCaseSettlementCd(settlementcase);
        return m;
    }
    
    protected EnforcementNote getEnforcementNote(CetaEnforceNote cen) {
        EnforcementNote n = new EnforcementNote();
        n.setEnforcementId(cen.enf_id);
        n.setNoteTxt(cen.note);
        n.setUserId(CommonConst.LEGACY_USER_ID);
        n.setDateEntered(cen.noteDate);
        return n;
    }
    
    static protected Boolean getBoolean(String t, String f) {
        if("true".equals(t) && "false".equals(f)) return true;
        if("true".equals(f) && "false".equals(t)) return false;
        return null;
    }
    
    static protected Boolean getNullIsFalse(Boolean b) {
        Boolean rtn = b;
        if(b == null) b = new Boolean(false);
        return rtn;
    }
    
    static protected Boolean getBoolean(String b) {
        if("true".equals(b)) return true;
        if("false".equals(b)) return false;
        return null;
    }
    
    static protected Boolean get01(String b) {
        if("1".equals(b)) return true;
        if("0".equals(b)) return false;
        return null;
    }
    
    static protected Boolean getBYesNo(String b) {
        if("Yes".equals(b)) return true;
        if("No".equals(b)) return false;
        return null;
    }
    
    static protected boolean getTrueFalse(String b) {
        if("true".equals(b)) return true;
        if("false".equals(b)) return false;
        return false;
    }
    
    public void execute(JobExecutionContext context) throws JobExecutionException {        
        logger.error("INFO: CetaPopulateEnforcements is executing.");
        try {
            // CetaPopulateEnforcements instance = new CetaPopulateEnforcements();
            directory = DocumentUtil.getFileStoreRootPath();
            ///this.process();
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.error("INFO: CetaPopulateEnforcements has completed.");

    }
}
