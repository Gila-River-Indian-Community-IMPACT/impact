package us.oh.state.epa.stars2.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.StackTestService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.Evaluator;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FacilityHistory;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestMethodPollutant;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestedPollutant;
import us.oh.state.epa.stars2.database.dbObjects.ceta.TestVisitDate;
import us.oh.state.epa.stars2.database.dbObjects.ceta.TestedEmissionsUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityHistList;
import us.oh.state.epa.stars2.def.CetaStackTestAuditsDef;
import us.oh.state.epa.stars2.def.CetaStackTestResultsDef;
import us.oh.state.epa.stars2.def.EmissionsTestStateDef;
import us.oh.state.epa.stars2.def.SccCodesDef;
import us.oh.state.epa.stars2.webcommon.ValidationBase;

/*
 * Populate Site Visits after Stack Tests to be able to set AFS info into the
 * visits created by the Stack Tests.
 */

@SuppressWarnings("serial")
public class CetaPopulateEmissionsTests extends ValidationBase implements Job {
    protected static FileWriter outStream;
    protected HashMap<String, StackTestMethodPollutant[]> methodPollutants = null;
    public static String directory = "C:/Projects";
	private FacilityService facilityService;
	private StackTestService stackTestService;

	public StackTestService getStackTestService() {
		return stackTestService;
	}

	public void setStackTestService(StackTestService stackTestService) {
		this.stackTestService = stackTestService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    public CetaPopulateEmissionsTests() {
        super();
    }
    
    public void process() {
        try {
            methodPollutants = getStackTestService().retrieveAllMethodPolls();
        } catch(RemoteException re) {
            logger.error("Got RemoteException: " + re.getMessage(), re);
            try {
                outStream.write("Failed to retrieveAllMethodPolls " + re.getMessage() + "\n");
            } catch(IOException ioe) {
                ;
            }
        }
        try {
            outStream = new FileWriter(new File(directory + File.separator + "migrationData" + File.separator, "stackTestMigrationLog.txt"));
            outStream.write("Starting migration of CETA Stack Tests\n");
            outStream.flush();
            CetaGetUserId.loadTable(directory);
            boolean rtn = CetaEmissionsTest.initialize("cetaStackTests.txt", logger);
            //outStream.write("Return from initialize() with return " + rtn + "\n");
            //outStream.flush();
            if(!rtn) return;
            CetaEmissionsTest cet = CetaEmissionsTest.next();
            // String s = "Returned from first next().  ";
            //if(cet == null) s = s + "cet is null";
            //outStream.write(s + "\n");
            //outStream.flush();
            while(cet != null && !CetaEmissionsTest.eof) {
                //outStream.write("Ry" + cet.getAppendixK_id() + "\n");
                //outStream.flush();
                String probToReport = "";
                String witnessedNot = "";
                StackTest et = new StackTest();
                et.setLegacyFlag(true);

                if(cet.getAppendixK_id() == null) {
                    // id is null.
                    outStream.write("appendixK_id is null, previous=" + CetaEmissionsTest.prevAppendixK_id + "\n");
                    outStream.flush();
                }
                et.setId(cet.getAppendixK_id());  // + 50000);  // DENNIS--TEMP

                String facilityId = cet.getFacility_id();
                Facility facility = null;
                Facility histFacility = null;
                Facility curFac = getFacilityService().retrieveFacility(facilityId);
                if(curFac == null) {
                    outStream.write("Did not find current facility, facilityId=" + facilityId + ", appendixK_id=" + et.getId() + "\n");
                    cet = CetaEmissionsTest.next();
                    continue;
                }
                et.setCreatedDt(cet.date_entered);
                List<Integer> usr = CetaGetUserId.getId(cet.entered_by, curFac.getDoLaaCd());
                for(Integer userId : usr) {
                    et.setCreatedById(userId);
                    break;  // use first one
                }
                et.setDateEvaluated(cet.getReviewDate());
                String emissionTestState;
                if(et.getDateEvaluated() == null) {
                    emissionTestState = EmissionsTestStateDef.DRAFT;
                    facility = curFac;
                } else {
                    Integer fpId = curFac.getFpId();
                    emissionTestState = EmissionsTestStateDef.SUBMITTED;
                    // If draft state then use current otherwise use most recent
                    // historic profile that is older than evaluated date.
                    FacilityHistList fhl = getFacilityService().searchFacilitiesHist(facilityId, et.getDateEvaluated());
                    if(fhl != null) {
                        fpId = fhl.getFpId();
                    } else {
                        outStream.write("Did not find historic facilities, facilityId=" + facilityId + ", appendixK_id=" + et.getId() + "\n");
                        cet = CetaEmissionsTest.next();
                        continue;
                    }
                    histFacility = getFacilityService().retrieveFacilityProfile(fpId);
                    if(histFacility == null) {
                        outStream.write("Did not find historic facility, fpId=" + fpId + ", appendixK_id=" + et.getId() + "\n");
                        cet = CetaEmissionsTest.next();
                        continue;
                    }
                    facility = histFacility;
                }
                et.setFpId(facility.getFpId());
                et.setEmissionTestState(emissionTestState);

                usr = CetaGetUserId.getId(cet.evaluator, facility.getDoLaaCd());
                ArrayList<Evaluator> eList = new ArrayList<Evaluator>();
                for(Integer userId : usr) {
                    Evaluator e = new Evaluator();
                    e.setEvaluator(userId);
                    eList.add(e);
                }
                Boolean w = getBoolean(cet.witness_yes, cet.witness_no);
                if(w == null) {
                    outStream.write("Errro, witness flag not set, appendixK_id=" + et.getId() + "\n");
                } else if(w) {  // marked as witnessed
                    if(eList.size() == 0) {
                        // was actually witnessed, make it marked as such
                        Evaluator e = new Evaluator();
                        e.setEvaluator(CommonConst.UNKNOWN_USER_ID);
                        eList.add(e);
                    }
                    et.setWitnesses(eList);
                } else { // not witnessed.
                    //  See if any witnesses specified anyway ignoring those that say no witness.
                    if(cet.evaluator != null && cet.evaluator.length() > 0) {
                        String le = cet.evaluator.toLowerCase();
                        if(     le.equals("no witness") ||
                                le.equals("n/a") ||
                                le.equals("not witnessed") ||
                                le.equals("na") ||
                                le.equals("nobody") ||
                                le.equals("none") ||
                                le.equals("unwitnessed") ||
                                le.equals("no one") ||
                                le.equals("ms. nobody") ||
                                le.equals("no witnesses") ||
                                le.equals("no") ||
                                le.equals("xx") ||
                                le.equals("unknown") ||
                                le.equals("memo") ||
                                le.equals("witness") ||
                                le.equals("unwitnessed plant test")) {
                            // ignore these names
                            ;
                        } else {
                            // preserve name in memo field
                            witnessedNot = "Witnessed=No, but it says Witnessed By " + cet.evaluator;
                        }
                    }
                }

                usr = CetaGetUserId.getId(cet.reviewer, facility.getDoLaaCd());
                et.setReviewer(null);
                if(usr != null && usr.size() > 0) {
                    et.setReviewer(usr.get(0));
                }
                if(cet.reviewDate != null) {
                    TestVisitDate tvd = new TestVisitDate();
                    tvd.setTestDate(cet.evaluationDate);
                    ArrayList<TestVisitDate> dates = new ArrayList<TestVisitDate>();
                    dates.add(tvd);
                    et.setVisitDates(dates);
                }

                String auditCd = null;
                Boolean a = getBoolean(cet.getAudits_yes(), cet.getAudits_no());
                if(a != null) {
                    Boolean pass = getBoolean(cet.getAuditsPass_yes());
                    Boolean fail = getBoolean(cet.getAuditsPass_no());
                    Boolean na = getBoolean(cet.getAuditPass_na());
                    if(a) { // audits is true
                        if(pass != null && fail != null && na != null) {
                            if(pass && !fail && !na) {
                                auditCd = CetaStackTestAuditsDef.YES;
                            } else if(fail && !pass && !na) {
                                auditCd = CetaStackTestAuditsDef.NO;
                            } else if(!fail && !pass && na) {
                                auditCd = CetaStackTestAuditsDef.NA;
                            }
                        }
                    } else { // no audits
                        if(pass != null && fail != null && na != null) {
                            if( na && !pass && !fail) { 
                                auditCd = CetaStackTestAuditsDef.NA;
                            } else if(!na && !pass && fail) { //  user should say audit n/a
                                auditCd = CetaStackTestAuditsDef.NA;
                            } else if(!na && pass && !fail) { // allow user to say audit passed
                                auditCd = CetaStackTestAuditsDef.YES;
                            } 
                        }
                    }
                }
                if(auditCd == null) {
                    // audit values are inconsistent
                    outStream.write("audit values are inconsistent: Audits_yes=" + cet.getAudits_yes() +
                            ", Audits_no=" + cet.getAudits_no() +
                            ",AuditsPass_yes=" +
                            cet.getAuditsPass_yes() + ", AuditsPass_no=" + cet.getAuditsPass_no() +
                            ", AuditPass_na=" + 
                            cet.getAuditPass_na() +
                            ", appendixK_id=" + et.getId() + "\n");
                }
                et.setAuditsCd(auditCd);
                et.setCompany(cet.getCompanyDidTest());
                // Not sure if we're using this, so will always output message, in case we are, so we can
                // then fix it.  Conformance boolean was replaced with conformedToTestMethod
                //et.setConformance(getBoolean(cet.getConformance_yes(), cet.getConformance_no()));
                //if(et.getConformance() == null) {
                    // conformance valuess are inconsistent
                    outStream.write("conformance values are inconsistent: Conformance_yes=" + cet.getConformance_yes() +
                            ", Conformance_no=" + cet.getConformance_no() +
                            ", appendixK_id=" + et.getId() + "\n");
                //}

                et.setControlEquipUsed(cet.getControl_equipment());
                et.setMonitoringEquip(cet.getMonitoring_equipment());
                et.setDateReceived(cet.getReceivedDate());
                et.setDateScheduled(cet.getDateScheduled());

                Boolean b = getBoolean(cet.getChkBif());
                if(b == null) {
                    // chkBif not set correctly.
                    outStream.write("chkBif not set correctly\n");
                } else {
                    et.setForBif(b.booleanValue());
                }

                b = getBoolean(cet.getChkMact());
                if(b == null) {
                    // chkMact not set.
                    outStream.write("chkMact not set correctly\n");
                } else {
                    et.setForMact(b.booleanValue());
                }

                b = getBoolean(cet.getChkNsps());
                if(b == null) {
                    // chkNsps not set.
                    outStream.write("chkNsps not set correctly\n");
                } else {
                    et.setForNsps(b.booleanValue());
                }

                b = getBoolean(cet.getChkOther());
                if(b == null) {
                    // chkOther not set.
                    outStream.write("chkOther not set correctly\n");
                } else {
                    et.setForOther(b.booleanValue());
                }

                b = getBoolean(cet.getChkPti());
                if(b == null) {
                    // chkPti not set.
                    outStream.write("chkPti not set correctly\n");
                } else {
                    et.setForPti(b.booleanValue());
                }

                // Note that forPtio is a new attribute (leave it not checked--false).

                b = getBoolean(cet.getChkPto());
                if(b == null) {
                    // chkPto not set.
                    outStream.write("chkPto not set correctly\n");
                } else {
                    et.setForPto(b.booleanValue());
                }

                b = getBoolean(cet.getChkIV());
                if(b == null) {
                    // chkIv not set.
                    outStream.write("chkIv not set correctly\n");
                } else {
                    et.setForTiv(b.booleanValue());
                }

                b = getBoolean(cet.getChkTv());
                if(b == null) {
                    // chkTv not set.
                    outStream.write("chkTv not set correctly\n");
                } else {
                    et.setForTv(b.booleanValue());
                }
                
                String sccValueToUse = null;
                if(cet.scc ==  null || cet.scc.length() == 0) {
                    sccValueToUse = "99999999";
                } else {
                    if(SccCodesDef.getSccCode(cet.scc) == null) {
                        // outStream.write("The SCC Code " + cet.scc + " is not a valid code for AppendixK_id=" + et.getId() + ", DO/LAA=" + facility.getDoLaaCd() + ", facility=" + facility.getFacilityId() + "\n");
                        String sep = "";
                        if(probToReport.length() > 0) {
                            sep = "; ";
                        }
                        probToReport = probToReport + sep + "SCC Code " + cet.scc + " is not valid [99999999 inserted at migration]";
                        sccValueToUse = "99999999";
                    } else {
                        sccValueToUse = cet.scc;
                    }
                }
                
                EmissionUnit eu = facility.getEmissionUnit(cet.euId);

                if(eu == null && et.getEmissionTestState().equals(EmissionsTestStateDef.SUBMITTED)) {
                    facility = curFac;
                    eu = facility.getEmissionUnit(cet.euId);
                    et.setFpId(facility.getFpId());
                }
                if(eu == null) {
                    // Could not find eu in currFac either
                    outStream.write("ERROR (EU/SCC and all pollutant test information put in Memo only) Did not find EU " + cet.euId + ", DO/LAA=" + facility.getDoLaaCd() + ", facility=" + facility.getFacilityId() + ", in current, fpId=" + facility.getFpId() + ",  state=" + et.getEmissionTestState() + ", appendixK_id=" + et.getId() + "\n");
                    String sep = "";
                    if(probToReport.length() > 0) {
                        sep = "; ";
                    }
                    probToReport = probToReport + sep + "Tested EU " + cet.euId + " [" + cet.euid_desc + "] not found in facility inventory";
                    
                }
                if(eu != null) {
                    TestedEmissionsUnit u = new TestedEmissionsUnit(eu);
                    et.addTestedEmissionsUnit(u);
                    u.setSccs(sccValueToUse);
                }

                if(cet.getAfs_sentDate() != null && 
                        (cet.getAfs_actionId() == null || cet.getAfs_actionId().length() == 0) ){
                    cet.setAfs_actionId("legacy");
                } 
                
                
                Boolean rPass = getBoolean(cet.results_pass, cet.results_fail);
                Boolean rRetest = getBoolean(cet.results_retest);
                Boolean rNotCompleted = getBoolean(cet.results_notCompleted);
                String resCode = null;
                String resStr = "?";
                if(rPass != null) {
                    if(rPass.booleanValue()) {
                        resCode = CetaStackTestResultsDef.PASS;
                        resStr = "Passed";
                    }
                    else {
                        resCode = CetaStackTestResultsDef.FAIL;
                        resStr = "Failed";
                    }
                } else {
                    if(rRetest) {
                        resCode = CetaStackTestResultsDef.RETEST_NEEDED;
                        resStr = "Invalid";
                    }
                    if(rNotCompleted) {
                        resCode = CetaStackTestResultsDef.NOT_COMPLETED;
                        resStr = "Incomplete";
                    }
                }

                String codeLabel = "Tested pollutants: ";
                StringBuffer codeList = new StringBuffer(codeLabel);
                if(cet.methodNo ==  null || cet.methodNo.length() == 0) {
                    outStream.write("MethodNo is missing.  AppendixK_id=" + et.getId() + "\n");
                } else {
                    et.setStackTestMethodCd(cet.methodNo);
                    StackTestMethodPollutant[] pollChoices = methodPollutants.get(cet.methodNo);
                    if(cet.pollutantsCode != null && cet.pollutantsCode.length() > 0) {
                        String ss = cet.pollutantsCode;
                        boolean more = true;
                        String code;
                        ArrayList<StackTestedPollutant> testedPolls = new ArrayList<StackTestedPollutant>();
                        ArrayList<StackTestedPollutant> justPolls = new ArrayList<StackTestedPollutant>();
                        while(more) {
                            int i = ss.indexOf(' ');
                            if(i == -1) {
                                more = false;
                                code = ss;
                            } else {
                                code = ss.substring(0, i);
                                ss = ss.substring(i + 1);
                            }
                            // convert pollutant code
                            code = convertPollutantCd(code);

                            // Does code exist
                            boolean found = false;
                            for(StackTestMethodPollutant sss : pollChoices) {
                                if(sss.getPollutantCd().equals(code)) {
                                    found = true;
                                    sss.setSelected(true);
                                    break;
                                }
                            }
                            if(!found) {
                                outStream.write("Pollutant Code " + code + " is not a valid code for Method=" + cet.methodNo + 
                                        " AppendixK_id=" + et.getId() + ", DO/LAA=" + facility.getDoLaaCd() + ", facility=" + facility.getFacilityId() + "\n");
                            } else {
                                if(codeList.length() > codeLabel.length()) {
                                    codeList.append(", ");
                                }
                                codeList.append(code);
                                StackTestedPollutant justP = new StackTestedPollutant(code, null, null);
                                justP.setSuperSelected(true);
                                justPolls.add(justP);
                                if(eu != null && sccValueToUse != null) {
                                    StackTestedPollutant stp = new StackTestedPollutant(code, eu, sccValueToUse);
                                    stp.setStackTestResultsCd(resCode);
                                    stp.setAfsSentDate(cet.getAfs_sentDate());
                                    stp.setAfsId(cet.getAfs_actionId());  
                                    stp.setSuperSelected(true);
                                    testedPolls.add(stp);
                                }
                            }
                        }
                        et.setAllMethodPollutants(justPolls.toArray(new StackTestedPollutant[0]));
                        et.setTestedPollutants(testedPolls.toArray(new StackTestedPollutant[0]));
                    }
                }
                if(et.getTestedPollutants() != null && et.getTestedPollutants().length == 1) {
                    // put the values in if only one pollutant
                    et.getTestedPollutants()[0].setTestAvgOperRate(cet.getAvg_operating_rate());
                    et.getTestedPollutants()[0].setTestRate(cet.getAvg_emmision_rate());
                    et.getTestedPollutants()[0].setPermitMaxRate(cet.getMax_rate());
                    et.getTestedPollutants()[0].setPermitAllowRate(cet.getAllowable_rate());
                } else {
                    // if zero or more than one complete row, put into memo field only
                    String sep = "";
                    if(probToReport.length() > 0) {
                        sep = "; ";
                    }
                    probToReport = probToReport + sep + codeList.toString() +
                    "; Permitted Allowable Emission Rate = " + cet.getAllowable_rate() +
                    "; Permitted Maximum Operating Rate = " + cet.getMax_rate() +
                    "; Average Tested Emission Rate = " + cet.getAvg_emmision_rate() +
                    "; Average Tested Operating Rate = " + cet.getAvg_operating_rate();
                    String afsDate = "";
                    if(cet.getAfs_sentDate() != null) afsDate = cet.getAfs_sentDate().toString();
                    String afsId = "";
                    if(cet.getAfs_actionId() != null) afsId = cet.getAfs_actionId();
                    probToReport = probToReport +
                    "; Results = " + resStr +
                    "; AFS Date = " + afsDate +
                    "; AFS ID = " + afsId;
                }

                if(rPass != null && (rRetest != null && rRetest.booleanValue() ||
                        rNotCompleted != null && rNotCompleted.booleanValue())) {
                    // results are pass/fail but also set to not completed or retest.
                    outStream.write("Result values are inconsistent (is pass or fail and also something else): results_pass=" + cet.results_pass +
                            ", results_fail=" + cet.results_fail + 
                            ", results_retest=" + cet.results_retest +
                            ", results_notCompleted=" + cet.results_notCompleted + 
                            ", appendixK_id=" + et.getId() + "\n");
                }
                if((rRetest != null && rRetest.booleanValue() &&
                        rNotCompleted != null && rNotCompleted.booleanValue()) ){
                    // both retest and not completed are set to true
                    outStream.write("Result values are inconsistent (not pass or fail but both not completed and retest): results_pass=" + cet.results_pass +
                            ", results_fail=" + cet.results_fail + 
                            ", results_retest=" + cet.results_retest +
                            ", results_notCompleted=" + cet.results_notCompleted + 
                            ", appendixK_id=" + et.getId() + "\n");
                }

                String witSep = "";
                if(witnessedNot.length() > 0 && probToReport.length() > 0) {
                    witSep = "; ";
                }

                if(probToReport.length() > 0 || witnessedNot.length() > 0) {
                    probToReport = "(Stars2 Migration Problems: " + probToReport + witSep + witnessedNot + ") ";
                }
                et.setMemo(probToReport + cet.memoAppenK.replaceAll("'", "''"));
                // create the stack test record
                StackTest st= null;
//              outStream.write("R " + et.getId() + "\n");
//              outStream.flush();

                FacilityHistory fh = new FacilityHistory();
//              fh.setAirProgramCompCd(ComplianceStatusDef.ON_SCHEDULE);
//              fh.setMactCompCd(ComplianceStatusDef.ON_SCHEDULE);
//              fh.setSipCompCd(ComplianceStatusDef.ON_SCHEDULE);
                Boolean aa = getBoolean(cet.sVchkTv);

                Boolean sm = getBoolean(cet.sVchkSm);
               
                fh.setStartDate(cet.evaluationDate);

                aa = getBoolean(cet.sVchkHPV);
                if(aa != null) {
                    ;
                }

                aa = getBoolean(cet.sVchkNeshap);
                if(aa != null) {
                    fh.setNeshaps(aa);
                }

                aa = getBoolean(cet.sVchkNsps);
                if(aa != null) {
                    fh.setNsps(aa);
                }

                aa = getBoolean(cet.sVchkMact);
                if(aa != null) {
                    fh.setMact(aa);
                }

                aa = getBoolean(cet.sVchkPsd);
                if(aa != null) {
                    fh.setPsd(aa);
                }

                aa = getBoolean(cet.sVchkAtt);
                if(aa != null) {
                    fh.setNsrNonAttainment(!aa);
                }

                try {
                    st = getStackTestService().createMigratedStackTest(facility, et, fh);
                    if(st == null) {
                        outStream.write("Failed to create Stack Test.  AppendixK_id=" + et.getId() + "\n");
                    }
                } catch(Exception e) {
                    logger.error("Got Exception: " + e.getMessage(), e);
                    outStream.write("Failed to create Stack Test with error " + e.getMessage() + ".  AppendixK_id=" + et.getId() + "\n");
                }
                outStream.flush();
                cet = CetaEmissionsTest.next();
                //s = "Rb"; // "Returned from next().  ";
                //if(cet == null) s = s + "cet is null\n";
                //else s = s + cet.appendixK_id + "\n";
                //outStream.write(s);
                //outStream.flush();
            }
            outStream.write("Finished processing stack tests.  Last one was " + CetaEmissionsTest.prevAppendixK_id + "\n");
            CetaGetUserId.dumpMissingNames(directory);
        } catch (IOException ioe) {
            logger.error("Got IOException: " + ioe.getMessage(), ioe);
            try {
                outStream.write("Got IOException: " + ioe.getMessage() + "\n");
                outStream.flush();
            } catch (IOException x) {
                ;
            }
        } catch (RuntimeException e) {
            logger.error("Got RuntimeException: " + e.getMessage(), e);
            CetaGetUserId.dumpMissingNames(directory);
            throw e;
        }
    }

    static protected Boolean getBoolean(String t, String f) {
        if("true".equals(t) && "false".equals(f)) return true;
        if("true".equals(f) && "false".equals(t)) return false;
        return null;
    }
    
    static protected Boolean getBoolean(String b) {
        if("true".equals(b)) return true;
        if("false".equals(b)) return false;
        return null;
    }
    
    static String convertPollutantCd(String code) {
//      conversions
        if("PT".equals(code))
            code = "PM-PRI";  // reached
        else if("HSO4P".equals(code))
            code = "7664939";  // reached
        else if("PB".equals(code)) 
            code = "7439921"; // reached
        else if("Pb".equals(code)) 
            code = "7439921"; // reached (air programs)
        else if("H2S".equals(code))
            code = "7783064";  // reached
        else if("COS".equals(code)) 
            code = "463581";  // reached
        else if("CADIS".equals(code)) 
            code = "75150";  // reached
        else if("HCL".equals(code))
            code = "7647010";  // reached
        else if("HF".equals(code)) 
            code = "7664393";  // reached
        else if("CL".equals(code)) 
            code = "7782505";  // reached
        else if("HG".equals(code)) // mercury
            code = "7439976";  // reached
        else if("BE".equals(code)) 
            code = "7440417";  // reached
        else if("VC".equals(code))
            code = "75014";  // reached
        else if("AS".equals(code)) // Arsenic
            code = "7440382";  // reached
        else if("PM10".equals(code))
            code = "PM10-PRI";  // reached
        else if("SB-PT".equals(code)) // Antimony
            code = "7440360";  // reached
        else if("BE".equals(code))  // Beryllium
            code = "7440417";
        else if("CD".equals(code))  // Cadmium
            code = "7440439";
        else if("CRC".equals(code))  // Chromium
            code = "7440473";  // reached
        else if("CU-PT".equals(code))  // Copper
            code = "7440508";  // reached
        else if("MN-PT".equals(code))  // Manganese
            code = "7439965";  // reached
        else if("BA-PT".equals(code))  // Barium
            code = "7440393";
        else if("MTNOL".equals(code)) // Methanol
            code = "67561";  // reached
        else if("7440280".equals(code)) // Thallium
            code = "TI";  // reached
        else if("FORM".equals(code)) // Formaldehyde
            code = "50000";  // reached
        else if("DBNZF".equals(code)) // DIBENZOFURAN
            code = "132649";  // reached
        else if("HCl".equals(code)) // Hydrogen Chloride
            code = "7647010";  // reached
        else if("Cl".equals(code)) // Chlorine
            code = "7782505";  // reached
        else if("NI-PT".equals(code)) // Nickel Powder
            code = "7440022";  // reached
        return code;
    }
    
    public void execute(JobExecutionContext context) throws JobExecutionException {        
        logger.error("INFO: CetaPopulateEmissionsTests is executing.");
        try {
            CetaPopulateEmissionsTests instance = new CetaPopulateEmissionsTests();
            directory = DocumentUtil.getFileStoreRootPath();
            instance.process();
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.error("INFO: CetaPopulateEmissionsTests has completed.");

    }
}
