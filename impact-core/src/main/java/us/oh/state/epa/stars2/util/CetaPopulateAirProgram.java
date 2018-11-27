package us.oh.state.epa.stars2.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.FacilityHistoryService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityNote;
import us.oh.state.epa.stars2.database.dbObjects.facility.PollutantCompCode;
import us.oh.state.epa.stars2.def.AirProgramDef;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.def.ComplianceStatusDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.PTIOMACTSubpartDef;
import us.oh.state.epa.stars2.def.PTIONESHAPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIONSPSSubpartDef;
import us.oh.state.epa.stars2.webcommon.ValidationBase;

@SuppressWarnings("serial")
public class CetaPopulateAirProgram extends ValidationBase implements Job {
    protected static FileWriter outStream;
    public static String directory = "C:/Projects";
    CetaAirProgram airP;
    
    /*
 
    us.oh.state.epa.stars2.util.CetaPopulateAirProgram
    
    
    These are not separate objects but rows in facility tables.
  
     */

    //private EnforcementService enforcementService;
    
    private FacilityHistoryService facilityHistoryService;
	private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    
    public FacilityHistoryService getFacilityHistoryService() {
		return facilityHistoryService;
	}
	public void setFacilityHistoryService(
			FacilityHistoryService facilityHistoryService) {
		this.facilityHistoryService = facilityHistoryService;
	}
	/*public EnforcementService getEnforcementService() {
		return enforcementService;
	}
	public void setEnforcementService(EnforcementService enforcementService) {
		this.enforcementService = enforcementService;
	}*/
    public CetaPopulateAirProgram() {
        super();
    }

    public void process() {
        try {
            outStream = new FileWriter(new File(directory + File.separator + "migrationData" + File.separator, "airProgMigrationLog.txt"));
            outStream.write("Starting migration of CETA airProgram\n");
            outStream.flush();
            boolean rtn = CetaAirProgram.initialize("cetaAirPrograms.txt", logger); 
            if(!rtn) return;
            airP = new CetaAirProgram();
            while(true) {
                airP = CetaAirProgram.next();
                if(airP == null) break;
                if(airP.facility_id == null) {
                    outStream.write("ERROR:  Facility Id is null, skipping.  Previous Facility Id is " +
                            CetaAirProgram.prevFacilityId + "\n");
                    outStream.flush();
                    continue;
                }
                Facility fac = null;  // NEED TO GET CURRENT FACILITY INVENTORY
                fac = getFacilityService().retrieveFacility(airP.facility_id);
                if(fac == null) {
                    outStream.write("ERROR:  Did not find current facility, facilityId=" + airP.facility_id + "\n");
                    continue;
                }
                
                // Get Government attribute
                if(airP.fGov_code != null && airP.fGov_code.length() > 0) {
                    fac.setGovtFacilityTypeCd(airP.fGov_code);
                }

                
                // Get Air Program for TV or SM or neither
                fac.setAirProgramCd(null);
                fac.setAirProgramCompCd(null);

                if(getBoolean(airP.chkTV)) {
                    fac.setAirProgramCd(AirProgramDef.TITLE_V);
                    String c = getComp(airP.chkTV_Y, airP.chkTV_N, airP.chkTV_OS);
                    if(c.length() != 1) {
                        outStream.write("ERROR:  Not exactly one TV comp. code (" + c + ") for  facility, facilityId=" + airP.facility_id + "\n");
                    } else {
                        fac.setAirProgramCompCd(c);
                    }
                } else if(getBoolean(airP.chksm)) {
                    fac.setAirProgramCd(AirProgramDef.SM_FESOP);
                    String c = getComp(airP.chksm_Y, airP.chksm_N, airP.chksm_OS);
                    if(c.length() != 1) {
                        outStream.write("ERROR:  Not exactly one SM comp. code (" + c + ") for  facility, facilityId=" + airP.facility_id + "\n");
                    } else fac.setAirProgramCompCd(c);
                }

                if(getBoolean(airP.chksip)) {
                    String c = getComp(airP.chksip_Y, airP.chksip_N, airP.chksip_OS);
                    if(c.length() != 1) {
                        outStream.write("ERROR:  Not exactly one SIP comp. code (" + c + ") for  facility, facilityId=" + airP.facility_id + "\n");
                    } else fac.setSipCompCd(c);
                }
                
   
                if(getBoolean(airP.chkmact)) {
                    String c = getComp(airP.chkmact_Y, airP.chkmact_N, airP.chkmact_OS);
                    if(c.length() != 1) {
                        outStream.write("ERROR:  Not exactly one MACT comp. code (" + c + ") for  facility, facilityId=" + airP.facility_id + "\n");
                    } else fac.setMactCompCd(c);
                }
   
                StringBuffer sbFinal = new StringBuffer("");
                List<PollutantCompCode> list = fac.getNeshapsSubpartsCompCds();
                StringBuffer sb = new StringBuffer("NESHAPS Subparts ");
                for(PollutantCompCode pcc : list) {
                    sb.append("'" + pcc.getPollutantCd() + "/" + pcc.getPollutantCompCd() + "' ");
                }
                processCompliance(list, false, "N", airP.chkneshap,  airP.chkneshap_As, airP.chkneshap_As_Y, airP.chkneshap_As_N, airP.chkneshap_As_OS);
                processCompliance(list, false, "M", airP.chkneshap,  airP.chkneshap_AB, airP.chkneshap_AB_Y, airP.chkneshap_AB_N, airP.chkneshap_AB_OS);
                processCompliance(list, false, "C", airP.chkneshap,  airP.chkneshap_Be, airP.chkneshap_Be_Y, airP.chkneshap_Be_N, airP.chkneshap_Be_OS);
                processCompliance(list, false, "FF", airP.chkneshap,  airP.chkneshap_BZ, airP.chkneshap_BZ_Y, airP.chkneshap_BZ_N, airP.chkneshap_BZ_OS);
                processCompliance(list, false, "L", airP.chkneshap,  airP.chkneshap_CE, airP.chkneshap_CE_Y, airP.chkneshap_CE_N, airP.chkneshap_CE_OS);
                processCompliance(list, false, "E", airP.chkneshap,  airP.chkneshap_Hg, airP.chkneshap_Hg_Y, airP.chkneshap_Hg_N, airP.chkneshap_Hg_OS);
                processCompliance(list, false, "H", airP.chkneshap,  airP.chkneshap_RD, airP.chkneshap_RD_Y, airP.chkneshap_RD_N, airP.chkneshap_RD_OS);
                processCompliance(list, false, "F", airP.chkneshap,  airP.chkneshap_VC, airP.chkneshap_VC_Y, airP.chkneshap_VC_N, airP.chkneshap_VC_OS);
                if(list.size() == 0) fac.setNeshaps(false);
                else fac.setNeshaps(true);
                sb.append("Final: ");
                for(PollutantCompCode pcc : list) {
                    // confirm code belongs to NESHAPS
                    String desc = PTIONESHAPSSubpartDef.getData().getItems().getItemDesc(pcc.getPollutantCd());
                    if(desc == null) {
                        outStream.write("ERROR:  Not a valid NESHAPS code (" + pcc.getPollutantCd() + ") for  facility, facilityId=" + airP.facility_id + "\n");
                    }
                    sb.append("'" + pcc.getPollutantCd() + "/" + pcc.getPollutantCompCd() + "' ");
                }
                if(fac.isNeshaps()) sbFinal.append(sb);
                
                boolean ok;
                List<String> subpartsList = fac.getMactSubparts();
                sb = new StringBuffer("; MACT Subparts ");
                for(String s : subpartsList) {
                    sb.append("'" + s + "' ");
                }
                ok = updateSubpart(fac, subpartsList, airP.chkMact_subpart1, airP.mact_subpart1);
                if(!ok) outStream.write(" Mact_subpart1\n");
                ok = updateSubpart(fac, subpartsList, airP.chkMact_subpart2, airP.mact_subpart2);
                if(!ok) outStream.write(" Mact_subpart2\n");
                ok = updateSubpart(fac, subpartsList, airP.chkMact_subpart3, airP.mact_subpart3);
                if(!ok) outStream.write(" Mact_subpart3\n");
                ok = updateSubpart(fac, subpartsList, airP.chkMact_subpart4, airP.mact_subpart4);
                if(!ok) outStream.write(" Mact_subpart4\n");
                ok = updateSubpart(fac, subpartsList, airP.chkMact_subpart5, airP.mact_subpart5);
                if(!ok) outStream.write(" Mact_subpart5\n");
                ok = updateSubpart(fac, subpartsList, airP.chkMact_subpart6, airP.mact_subpart6);
                if(!ok) outStream.write(" Mact_subpart6\n");
                ok = updateSubpart(fac, subpartsList, airP.chkMact_subpart7, airP.mact_subpart7);
                if(!ok) outStream.write(" Mact_subpart7\n");
                ok = updateSubpart(fac, subpartsList, airP.chkMact_subpart8, airP.mact_subpart8);
                if(!ok) outStream.write(" Mact_subpart8\n");
                ok = updateSubpart(fac, subpartsList, airP.chkMact_subpart9, airP.mact_subpart9);
                if(!ok) outStream.write(" Mact_subpart9\n");
                if(subpartsList.size() == 0) fac.setMact(false);
                else {
                    fac.setMact(true);
                }
                sb.append("Final: ");
                for(String s : subpartsList) {
                    // confirm code belongs to MACT
                    String desc = PTIOMACTSubpartDef.getData().getItems().getItemDesc(s);
                    if(desc == null) {
                        outStream.write("ERROR:  Not a valid MACT code (" + s + ") for  facility, facilityId=" + airP.facility_id + "\n");
                    }
                    sb.append("'" + s + "' ");
                }
                if(fac.isMact()) sbFinal.append(sb);
                
                subpartsList = fac.getNspsSubparts();
                sb = new StringBuffer("; NSPS Subparts ");
                for(String s : subpartsList) {
                    sb.append("'" + s + "' ");
                }
                ok = updateSubpart(fac, subpartsList, airP.chkNsps_subpart1, nspsSubpartConversion(fac, airP.chkNsps_subpart1, airP.nsps_subpart1));
                if(!ok) outStream.write(" Nsps_subpart1\n");
                ok = updateSubpart(fac, subpartsList, airP.chkNsps_subpart2, nspsSubpartConversion(fac, airP.chkNsps_subpart2, airP.nsps_subpart2));
                if(!ok) outStream.write(" Nsps_subpart2\n");
                ok = updateSubpart(fac, subpartsList, airP.chkNsps_subpart3, nspsSubpartConversion(fac, airP.chkNsps_subpart3, airP.nsps_subpart3));
                if(!ok) outStream.write(" Nsps_subpart3\n");
                ok = updateSubpart(fac, subpartsList, airP.chkNsps_subpart4, nspsSubpartConversion(fac, airP.chkNsps_subpart4, airP.nsps_subpart4));
                if(!ok) outStream.write(" Nsps_subpart4\n");
                ok = updateSubpart(fac, subpartsList, airP.chkNsps_subpart5, nspsSubpartConversion(fac, airP.chkNsps_subpart5, airP.nsps_subpart5));
                if(!ok) outStream.write(" Nsps_subpart5\n");
                ok = updateSubpart(fac, subpartsList, airP.chkNsps_subpart6, nspsSubpartConversion(fac, airP.chkNsps_subpart6, airP.nsps_subpart6));
                if(!ok) outStream.write(" Nsps_subpart6\n");
                ok = updateSubpart(fac, subpartsList, airP.chkNsps_subpart7, nspsSubpartConversion(fac, airP.chkNsps_subpart7, airP.nsps_subpart7));
                if(!ok) outStream.write(" Nsps_subpart7\n");
                ok = updateSubpart(fac, subpartsList, airP.chkNsps_subpart8, nspsSubpartConversion(fac, airP.chkNsps_subpart8, airP.nsps_subpart8));
                if(!ok) outStream.write(" Nsps_subpart8\n");
                ok = updateSubpart(fac, subpartsList, airP.chkNsps_subpart9, nspsSubpartConversion(fac, airP.chkNsps_subpart9, airP.nsps_subpart9));
                if(!ok) outStream.write(" Nsps_subpart9\n");
                if(subpartsList.size() == 0) fac.setNsps(false);
                else fac.setNsps(true);
                sb.append("Final: ");
                for(String s : subpartsList) {
//                  confirm code belongs to NSPS subparts
                    String desc = PTIONSPSSubpartDef.getData().getItems().getItemDesc(s);
                    if(desc == null) {
                        outStream.write("ERROR:  Not a valid NSPS Subparts code (" + s + ") for  facility, facilityId=" + airP.facility_id + "\n");
                    }
                    sb.append("'" + s + "' ");
                }
                if(fac.isNsps())  sbFinal.append(sb);
  
                //  create the air program record
                if(sbFinal.length() > 0) {
                    outStream.write("FacilityId=" + airP.facility_id + " " + sbFinal.toString() + "\n");
                }
                try {
                    getFacilityHistoryService().migrateFacilityHistory(fac, CommonConst.LEGACY_USER_ID);
                } catch(Exception e) {
                    outStream.write("ERROR:  Failed to create air programs with error " + e.getMessage() + ".  facilityId=" + airP.facility_id  + 
                            " NSP Subparts: " + sb.toString() + "\n");
                }
                
                try {
                    if(airP.fMemo != null && airP.fMemo.length() > 0) {
                        FacilityNote note = new FacilityNote();
                        note.setNoteTxt("MIGRATED CETA MEMO: " + airP.fMemo);
                        note.setDateEntered(new Timestamp(System.currentTimeMillis()));
                        note.setUserId(CommonConst.LEGACY_USER_ID); 
                        note.setNoteTypeCd(NoteType.DAPC);
                        note.setFpId(fac.getFpId());
                        note.setFacilityId(fac.getFacilityId());
                        getFacilityService().createFacilityNote(note);
                    }
                } catch(Exception e) {
                    outStream.write("ERROR:  Failed to create facility note with error " + e.getMessage() + ".  facilityId=" + airP.facility_id  + 
                            " NSP Subparts: " + sb.toString() + "\n");
                }

                outStream.flush();
            }
        } catch (IOException ioe) {
            try {
                outStream.write("ERROR:  IOException: " + ioe.getMessage() + "\n");
                outStream.flush();
            } catch (IOException x) {
                ;
            } catch (Exception e) {
                try {
                    outStream.write("ERROR:  Exception: " + e.getMessage() + "\n");
                    outStream.flush();
                } catch (IOException x) {
                    ;
                }
            }
        }
    }
    
    void processCompliance(List<PollutantCompCode> list, boolean isPollutant, String rowCode, String presentGeneral, 
            String present, String y, String n, String os)  throws IOException {
        if(isPollutant) {
            rowCode = CetaPopulateEmissionsTests.convertPollutantCd(rowCode);
        }
        String cd = null;
        if(getBoolean(present)) {
            cd = getComp(y, n, os);
            if(cd.length() == 0) {
                // should be one true.  Write error
                outStream.write("ERROR:  No compliance code for " + rowCode + ", facilityId=" + airP.facility_id  + "\n");
                return;
            } else if(cd.length() > 1) {
                outStream.write("ERROR:  Multiple compliance codes (" + cd + ") for " + rowCode + ", facilityId=" + airP.facility_id  + "\n");
                return;
            }
        } else {
            if(getBoolean(y) || getBoolean(n) || getBoolean(os)) {
                // none should have been true.  Write error
                outStream.write("ERROR:  Should have been no compliance codes since not present (" + cd + ") for " + rowCode + ", facilityId=" + airP.facility_id  + "\n");
                return;
            }
        }
        if(getBoolean(presentGeneral)) {
            // process the row
            if(getBoolean(present)) {
                // See if row exists.  Add if necessary.  Update with compliance code.
                boolean found = false;
                PollutantCompCode foundRow = null;
                for(PollutantCompCode pcc : list) {
                    if(pcc.getPollutantCd().equals(rowCode)) {
                        found = true;
                        foundRow = pcc;
                        break;
                    }
                }
                if(!found) {
                    foundRow = new PollutantCompCode();
                    foundRow.setPollutantCd(rowCode);
                    list.add(foundRow);
                }
                foundRow.setPollutantCompCd(cd);
                
            }
        } else if(getBoolean(present)){
            //  Should not have been true. since category not enabled.  Write error
            outStream.write("ERROR:  Should have been no compliance codes (" + cd + ") since general false for " + rowCode + ", facilityId=" + airP.facility_id  + "\n");
            return;
            
        }
    }
    
    String getComp(String y, String n, String os) {
        StringBuffer rtn = new StringBuffer("");
        if(getBoolean(y)) {
            rtn.append(ComplianceStatusDef.YES);
        }
        if(getBoolean(n)) {
            if(rtn.length() > 0) rtn.append(";");
            rtn.append(ComplianceStatusDef.NO);
        }
        if(getBoolean(os)) {
            if(rtn.length() > 0) rtn.append(";");
            rtn.append(ComplianceStatusDef.ON_SCHEDULE);
        }
        return rtn.toString();
    }
    
    String nspsSubpartConversion(Facility f, String exists, String value) throws IOException {
        String stars2Val = null;
        if(getBoolean(exists)) {
            //      pick the corresponding value from reference table
            HashMap<String, BaseDef> items = PTIONSPSSubpartDef.getData().getItems().getCompleteItems();
            for(BaseDef bd : items.values()) {
                PTIONSPSSubpartDef subPartDef = (PTIONSPSSubpartDef)bd;
                if(subPartDef.getNspsSubpartAfsCd().equals(value)) {
                    // Use Stars2 code
                    stars2Val = (String)subPartDef.getCode();
                    break;
                }
            }
            if(stars2Val == null) {
                outStream.write("ERROR:  Failed to find NSPS Subpart " + value + " for facility " + f.getFacilityId() + "\n");
            }
        }
        return stars2Val;
    }
    
    boolean updateSubpart(Facility f, List<String> subpartsList, String exists, String value) throws IOException {
        if(getBoolean(exists)) {
            // Convert from CETA code to Stars2 code
            // 6X   -->   XXXXXX
            if(value == null || value.length() == 0) {
                outStream.write("ERROR:  Subpart is null for facility " + f.getFacilityId());
                return false;
            }
            // if only one character long, do not change it
            if(value.length() > 1) {
                // check first character
                if(value.substring(0, 1).equals("6")) {
                    String s = value.substring(1);
                    value = s + s + s + s + s + s;
                } else if(value.substring(0, 1).equals("7")) {
                    String s = value.substring(1);
                    value = s + s + s + s + s + s + s;
                }
            }
            boolean found = false;
            for(String subP : subpartsList) {
                if(subP.equals(value)) {
                        found = true;
                    break;
                }
            }
            if(!found) {
                // was not already in the list; add the proper code

                subpartsList.add(value);
            }
        } else if(value != null && value.length() != 0) {
            outStream.write("ERROR:  Subpart has value (" + value + ") for facility " + f.getFacilityId() + " but should not.");
            return false;
        }
        return true;
    }
    
    static protected boolean getBoolean(String b) {
        if("1".equals(b)) return true;
        if("0".equals(b)) return false;
        return false;
    }
    
    public void execute(JobExecutionContext context) throws JobExecutionException {        
        logger.error("INFO: CetaPopulateAirProgram is executing.");
        try {
            directory = DocumentUtil.getFileStoreRootPath();
            this.process();
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.error("INFO: CetaPopulateAirProgram has completed.");
    }
}
