package us.oh.state.epa.stars2.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.Evaluator;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FacilityHistory;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SiteVisit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityHistList;
import us.oh.state.epa.stars2.def.SiteVisitTypeDef;
import us.oh.state.epa.stars2.webcommon.ValidationBase;

/*
 * Populate Site Visits after Stack Tests to be able to set AFS info into the
 * visits created by the Stack Tests.
 */

@SuppressWarnings("serial")
public class CetaPopulateSiteVisits extends ValidationBase implements Job {
    protected static FileWriter outStream;
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
    
    public CetaPopulateSiteVisits() {
        super();
    }
    
    public void process() {
        try {
            outStream = new FileWriter(new File(directory + File.separator + "migrationData" + File.separator, "siteVisitMigrationLog.txt"));
            outStream.write("Starting migration of CETA Site Visits\n");
            outStream.flush();
            CetaGetUserId.loadTable(directory);
            boolean rtn = CetaSiteVisit.initialize("cetaSiteVisits.txt", logger); 
            if(!rtn) return;
            CetaSiteVisit csv = CetaSiteVisit.next(logger);
            while(csv != null) {
                String facilityId = csv.getFacility_id();
                SiteVisit sv = new SiteVisit();
                sv.setVisitDate(csv.getSiteVisitDate());
                sv.setFacilityId(facilityId);
                if("Permit Related Issues".equals(csv.getTypeOfVisit())) {
                    sv.setVisitType(SiteVisitTypeDef.PERMIT_RELATED_ISSUES);
                } else if("Complaint Investigation".equals(csv.getTypeOfVisit())) {
                    sv.setVisitType(SiteVisitTypeDef.COMPLIANT_INVESTIGATION);
                } else if("Compliance Evaluation".equals(csv.getTypeOfVisit())) {
                    sv.setVisitType(SiteVisitTypeDef.COMPLIANCE_EVALUATION);
                } else {
                    if(!"Stack Test".equals(csv.getTypeOfVisit())) {
                        outStream.write("Unknown visit type=" + csv.getTypeOfVisit() + ", siteVisitId=" + csv.siteVisitId + ".  Skipped\n");
                        csv = CetaSiteVisit.next(logger);  // go to next
                        continue;
                    }
                    sv.setVisitType(SiteVisitTypeDef.STACK_TEST);
                }
                if("Stack Test".equals(csv.getTypeOfVisit())) {
                    // Locate vist record already created for this stack test
                    // and if found insert AFS information.
                    // Otherwise give log error.
                    
                    SiteVisit[] foundVisit = getFullComplianceEvalService().retrieveVisitBySearch(null, null, facilityId, 
                            sv.getVisitDate(), sv.getVisitDate(), sv.getVisitType(),
                            null, null, null, null, null, null, null, null, null);
                    if(foundVisit == null || foundVisit.length == 0) {
                        String s = "At migration into Stars2, no witnessed Stack Tests found.  CETA Site Visit Id=" + csv.siteVisitId + ".";
                        outStream.write("No witnessed Stack Tests found for Site Visit, facilityId=" + facilityId + ", date= " + csv.siteVisitDate + ", visitType=" + csv.typeOfVisit + ", siteVisitId=" + csv.siteVisitId + "\n");
                        sv.setMemo(s);
                        sv.setId(csv.getSiteVisitId());  // + 50000);  // DENNIS--TEMP
                        sv.setAfsId(csv.getAfs_actionId());
                        sv.setAfsDate(csv.getAfs_sentDate());
                        Facility curFac = getFacilityService().retrieveFacility(facilityId);
                        if(curFac == null) {
                            outStream.write("Did not find current facility, facilityId=" + facilityId + ", siteVisitId=" + csv.siteVisitId + ".  Stack Test Site Visit Skipped.\n");
                            csv = CetaSiteVisit.next(logger);  // go to next
                            continue;
                        }
                        Integer fpId = curFac.getFpId();
                        
                        // Use most recent historic profile that is older than evaluated date.
                        FacilityHistList fhl = getFacilityService().searchFacilitiesHist(facilityId, sv.getVisitDate());
                        if(fhl != null) {
                            fpId = fhl.getFpId();
                        } else {
                            outStream.write("Did not find historic facilities for stack test Site Visit, facilityId=" + facilityId + ", siteVisitId=" + csv.siteVisitId + ".  Skipped\n");
                            csv = CetaSiteVisit.next(logger);  // go to next
                            continue;
                        }
                        Facility histFacility = null;
                        histFacility = getFacilityService().retrieveFacilityProfile(fpId);
                        if(histFacility == null) {
                            outStream.write("Did not find historic facility for stack test Site Visit, fpId=" + fpId + ", siteVisitId=" + csv.siteVisitId + ".  Skipped\n");
                            csv = CetaSiteVisit.next(logger);  // go to next
                            continue;
                        }
                        
                        sv.setCreatedDt(csv.date_entered);
                        List<Integer> usr = CetaGetUserId.getId(csv.entered_by, curFac.getDoLaaCd());
                        for(Integer userId : usr) {
                            sv.setCreatedById(userId);
                            break;  // use first one
                        }

                        sv.setFpId(fpId);
                        // create the site visit record for Stack Test Site Visit
                        SiteVisit st= null;
                        try {
                            st = getFullComplianceEvalService().createSiteVisit(sv);
                            if(st == null) {
                                outStream.write("Failed to create stack test Site Visit.  siteVisitId=" + csv.siteVisitId + "\n");
                            }
                        } catch(Exception e) {
                            String str = "Failed to create stack test Site Visit with error " + e.getMessage() + ".  siteVisitId=" + csv.siteVisitId + "\n";
                            logger.error(str, e);
                            outStream.write(str);
                        }
                    } else {
                        if(foundVisit.length > 1) {
                            outStream.write("Found " + foundVisit.length + " Stack Tests for Site Visit, facilityId=" + facilityId + ", siteVisitId=" + csv.siteVisitId + "\n");
                        }
                        // Update with AFS info
                        foundVisit[0].setAfsId(csv.getAfs_actionId());
                        foundVisit[0].setAfsDate(csv.getAfs_sentDate());
                        
                        sv.setCreatedDt(csv.date_entered);
                        List<Integer> usr = CetaGetUserId.getId(csv.entered_by, "skipped DO/LAA");
                        for(Integer userId : usr) {
                            sv.setCreatedById(userId);
                            break;  // use first one
                        }
                        try {
                            getFullComplianceEvalService().modifySiteVisit(foundVisit[0]);
                        } catch(Exception e) {
                            String s = "Failed to update Site Visit (" + foundVisit[0].getId() + ") with error " + e.getMessage() + ".  siteVisitId=" + csv.siteVisitId + ".  Update skipped\n";
                            logger.error(s, e);
                            outStream.write(s);
                        }
                        outStream.flush();
                    }
                    csv = CetaSiteVisit.next(logger);  // go to next
                    continue;
                }
                
                sv.setLegacyFlag(true);

                if(csv.getSiteVisitId() == null) {
                    // id is null.
                    outStream.write("siteVisitId is null, previous=" + CetaSiteVisit.prevSiteVisitId + "\n");
                }
                sv.setId(csv.getSiteVisitId());  // + 50000);  // DENNIS--TEMP
                
                Facility facility = null;
                Facility histFacility = null;
                Facility curFac = getFacilityService().retrieveFacility(facilityId);
                if(curFac == null) {
                    outStream.write("Did not find current facility, facilityId=" + facilityId + ", siteVisitId=" + csv.siteVisitId + ".  Skipped.\n");
                    csv = CetaSiteVisit.next(logger);  // go to next
                    continue;
                }
                sv.setCreatedDt(csv.date_entered);
                List<Integer> usr = CetaGetUserId.getId(csv.entered_by, curFac.getDoLaaCd());
                for(Integer userId : usr) {
                    sv.setCreatedById(userId);
                    break;  // use first one
                }
                
                String yesNo = "";
                String yesNoNull = csv.getAnnounced();
                if(yesNoNull == null){
                    outStream.write("Announced not set, siteVisitId=" + csv.siteVisitId + "\n");
                } else yesNo = yesNoNull;
                
                sv.setAnnounced(yesNo);
                sv.setAfsId(csv.getAfs_actionId());
                sv.setAfsDate(csv.getAfs_sentDate());
                if(sv.getVisitDate() == null) {
                    outStream.write("VisitDate is null, siteVisitId=" + csv.siteVisitId + "\n");
                    facility = curFac;
                } else { // pick profile to us
                    Integer fpId = curFac.getFpId();
                    // Use most recent historic profile that is older than evaluated date.
                    FacilityHistList fhl = getFacilityService().searchFacilitiesHist(facilityId, sv.getVisitDate());
                    if(fhl != null) {
                        fpId = fhl.getFpId();
                    } else {
                        outStream.write("Did not find historic facilities, facilityId=" + facilityId + ", siteVisitId=" + csv.siteVisitId + ".  Skipped\n");
                        csv = CetaSiteVisit.next(logger);  // go to next
                        continue;
                    }
                    histFacility = getFacilityService().retrieveFacilityProfile(fpId);
                    if(histFacility == null) {
                        outStream.write("Did not find historic facility, fpId=" + fpId + ", siteVisitId=" + csv.siteVisitId + ".  Skipped\n");
                        csv = CetaSiteVisit.next(logger);  // go to next
                        continue;
                    }
                    facility = histFacility;
                }
                if(facility == null) {
                    outStream.write("Did not find facility, facilityId=" + facilityId + ", siteVisitId=" + csv.siteVisitId + ".  Skipped\n");
                    csv = CetaSiteVisit.next(logger); // go to next
                    continue;
                }
                sv.setFpId(facility.getFpId());
                
                // process evaluator including multiple evaluators.
                usr =  CetaGetUserId.getId(csv.evaluator, facility.getDoLaaCd());
                if(usr != null && usr.size() > 0) {
                    ArrayList<Evaluator> eList = new ArrayList<Evaluator>();
                    for(Integer i : usr) {
                        Evaluator e = new Evaluator(i);
                        eList.add(e);
                    }
                    sv.setEvaluators(eList);
                }

                Boolean b = getBoolean(csv.getChkTv());
                boolean areNull = false;
                if(b == null) areNull = true;
                b = getNullIsFalse(b);
                
                b = getBoolean(csv.getChkSm());
                if(b == null) areNull = true;
                b = getNullIsFalse(b);
                
                b = getBoolean(csv.getChkHPV());
                if(b == null) areNull = true;
                b = getNullIsFalse(b);
                
                b = getBoolean(csv.getChkNsps());
                if(b == null) areNull = true;
                b = getNullIsFalse(b);
                
                b = getBoolean(csv.getChkNeshap());
                if(b == null) areNull = true;
                b = getNullIsFalse(b);
                
                b = getBoolean(csv.getChkMact());
                if(b == null) areNull = true;
                b = getNullIsFalse(b);
                
                b = getBoolean(csv.getChkPsd());
                if(b == null) areNull = true;
                b = getNullIsFalse(b);
                
                b = getBoolean(csv.getChkAtt());
                if(b == null) areNull = true;
                b = getNullIsFalse(b);
    
                FacilityHistory fh = new FacilityHistory();

                
                Boolean a = getBoolean(csv.chkTv);
                 fh.setStartDate(sv.getVisitDate());
                
                a = getBoolean(csv.chkHPV);
                if(a != null) {
                    ;
                }

                a = getBoolean(csv.chkNeshap);
                if(a != null) {
                    fh.setNeshaps(a);
                }

                a = getBoolean(csv.chkNsps);
                if(a != null) {
                    fh.setNsps(a);
                }

                a = getBoolean(csv.chkMact);
                if(a != null) {
                    fh.setMact(a);
                }

                a = getBoolean(csv.chkPsd);
                if(a != null) {
                    fh.setPsd(a);
                }

                a = getBoolean(csv.chkAtt);
                if(a != null) {
                    fh.setNsrNonAttainment(!a);
                }
                sv.setFacilityHistory(fh);

                sv.setMemo(csv.getMemo_siteVisit());
                
                // create the site visit record
                SiteVisit st= null;
                try {
                    st = getFullComplianceEvalService().createMigratedSiteVisit(sv);
                    if(st == null) {
                        outStream.write("Failed to create Site Visit.  siteVisitId=" + csv.siteVisitId + "\n");
                    }
                } catch(Exception e) {
                    String s = "Failed to create site Visit with error " + e.getMessage() + ".  siteVisitId=" + csv.siteVisitId + "\n";
                    logger.error(s, e);
                    outStream.write(s);
                }
                outStream.flush();
                csv = CetaSiteVisit.next(logger);
            }
            CetaGetUserId.dumpMissingNames(directory);
        } catch (IOException ioe) {
            try {
                logger.error(ioe.getMessage(), ioe);
                outStream.write("IOException: " + ioe.getMessage() + "\n");
                outStream.flush();
            } catch (IOException x) {
                ;
            }
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            CetaGetUserId.dumpMissingNames(directory);
            throw e;
        }
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
    
    static protected Boolean getBYesNo(String b) {
        if("Yes".equals(b)) return true;
        if("No".equals(b)) return false;
        return null;
    }
    
    public void execute(JobExecutionContext context) throws JobExecutionException {        
        logger.error("INFO: CetaPopulateSiteVisits is executing.");
        try {
            CetaPopulateSiteVisits instance = new CetaPopulateSiteVisits();
            directory = DocumentUtil.getFileStoreRootPath();
            instance.process();
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.error("INFO: CetaPopulateSiteVisits has completed.");
    }
}
