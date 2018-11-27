package us.oh.state.epa.stars2.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class CetaEnforceAction {
    protected static Scanner s;
    protected static Integer prevSequenceNumber = null;
    protected static boolean eof;
    
    /*
    
    select * from stars2.ce_enforcement;
    select count(*) from stars2.ce_enforcement;
    
    delete stars2.ce_facility_neshaps_xref where facility_hist_id in
     	(select facility_hist_id from stars2.ce_enforcement_action);
    delete stars2.ce_facility_nsps_subparts where facility_hist_id in
     	(select facility_hist_id from stars2.ce_enforcement_action);
     delete stars2.ce_facility_nsr_pollutants where facility_hist_id in
     	(select facility_hist_id from stars2.ce_enforcement_action);
     delete stars2.ce_facility_psd_pollutants where facility_hist_id in
     	(select facility_hist_id from stars2.ce_enforcement_action);
	delete stars2.CE_ENFORCEMENT_VIOLATION_XREF;
	delete STARS2.CE_ENFORCEMENT_EVALUATOR_XREF;
	delete stars2.ce_offsite_pce;

    delete stars2.ce_enforcement_attachment;
    delete stars2.ce_enforcement_note_xref;
    delete stars2.ce_enforcement_milestone;
    delete stars2.ce_enforcement_action;

    delete stars2.ce_facility_hist where facility_hist_id in
     	(select facility_hist_id from stars2.ce_enforcement_action);
         
    delete stars2.ce_enforcement;
    
    delete "STARS2"."WF_PROCESS_DATA" where process_id in (select process_id from "STARS2"."WF_PROCESS" where PROCESS_TEMPLATE_ID=217);
	delete "STARS2"."WF_PROCESS_ACTIVITY" where process_id in (select process_id from "STARS2"."WF_PROCESS" where PROCESS_TEMPLATE_ID=217);
	delete "STARS2"."WF_PROCESS_NOTES"  where process_id in (select process_id from "STARS2"."WF_PROCESS" where PROCESS_TEMPLATE_ID=217);
	delete "STARS2"."WF_PROCESS" where PROCESS_TEMPLATE_ID=217
    
    Need more for worflow
    
    Do this in eclipse,  then replace "|eEnNdD;"  with "|" 
     
    SELECT ' ',
    rtrim(sequence_number), '|',
    rtrim(enf_Id), '|',
    rtrim(facility_id), '|',
    actionDate, '|',
    rtrim(evaluator), '|',
    rtrim(actionTypeId), '|',
    rtrim(formal), '|',
    rtrim(informal), '|',
    rtrim(typeOfViolation), '|',
    rtrim(actionDiscovery), '|',
    violationDate, '|',
    zeroDate, '|',
    rtrim(sepAmount), '|',
    rtrim(cashAmount), '|',
    rtrim(afs_actionId), '|',
    afs_sentDate, '|',
    date_entered, '|',
    rtrim(entered_by), '|',
    chkTV, '|',
    chkSm, '|',
    chknonHPV, '|',
    chkNsps, '|',
    chkNeshap, '|',
    chkMact, '|',
    chkPsd, '|',
    chkNonAtt, '|',
    rtrim(hpv), '|',
    rtrim(hpv_code), '|',
    rtrim(memoToAfs),  '|eEnNdD' from ceta.dbo.enforce order by facility_id, enf_id, sequence_number;
    */
    Integer sequence_number;
    Integer enf_id;
    String facility_id;
    Timestamp actionDate;
    String evaluator;
    String actionTypeCd;
    String formal;
    String informal;
    String typeOfViolation;
    String actionDiscovery;
    Timestamp violationDate;
    Timestamp zeroDate;
    String sepAmount;
    String cashAmount;
    String afs_actionId;
    Timestamp afs_sentDate;
    Timestamp date_entered;
    String entered_by;
    String chkTv;
    String chkSm;
    String chkNonHPV;
    String chkNsps;
    String chkNeshap;
    String chkMact;
    String chkPsd;
    String chkNonAtt;
    String hpv;
    String hpv_code;
    String memoToAfs;

    // Initialize to read the flat file.
    static boolean initialize(String fileName,  Logger logger) throws IOException {
        s = null;
        prevSequenceNumber = null;
        eof = false;
        File file = new File(CetaPopulateEnforcements.directory + File.separator + "migrationData" + File.separator, fileName);
        File cleanFile = new File(CetaPopulateEnforcements.directory + File.separator + "migrationData" + File.separator, "clean_" + fileName);
        FilterCharacters.filter(CetaPopulateEnforcements.outStream, logger, file, cleanFile);
        try {
            s = new Scanner(cleanFile).useDelimiter(Pattern.compile("[|]"));
        } catch (FileNotFoundException e) {
            eof = true;
            CetaPopulateEnforcements.outStream.write("Failure to find file "
                    + CetaPopulateEnforcements.directory + File.separator + "migrationData" + File.separator + fileName + "\n");
            CetaPopulateEnforcements.outStream.close();
            return false;
        }
        return true;
    }

    // Return true if requested record read. False if record not present or at
    // eof.
    static CetaEnforceAction next(Logger logger) throws IOException {
        int fieldNum = 0;
        boolean partialRow = false;;
        CetaEnforceAction cea = new CetaEnforceAction();
        if (eof)
            return null;
        try {
            // Read the attributes
            partialRow = false;
            fieldNum = 0;

            String str;
//          Read the attributes
            fieldNum = 0;
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            partialRow = true;
            if(str != null && str.length() > 0) {
                cea.sequence_number = getInteger(logger, str);
            }
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                cea.enf_id = getInteger(logger, str);
            }
            cea.facility_id = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                cea.actionDate = convertDate(logger, str, "actionDate", cea.sequence_number);
            }
            
            cea.evaluator = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cea.actionTypeCd = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cea.formal = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cea.informal = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cea.typeOfViolation = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cea.actionDiscovery = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                cea.violationDate = convertDate(logger, str, "violationDate", cea.sequence_number);
            }
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                cea.zeroDate = convertDate(logger, str, "zeroDate", cea.sequence_number);
            }
            
            cea.sepAmount = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cea.cashAmount = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            
            cea.afs_actionId = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                cea.afs_sentDate = convertDate(logger, str, "afs_sentDate", cea.sequence_number);
            }
            
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                cea.date_entered = convertDate(logger, str, "date_entered", cea.sequence_number);
            }
            
            cea.entered_by = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cea.chkTv = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cea.chkSm = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cea.chkNonHPV = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cea.chkNsps = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cea.chkNeshap = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cea.chkMact = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cea.chkPsd = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cea.chkNonAtt = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cea.hpv = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cea.hpv_code = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cea.memoToAfs = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            // set the AFS ID for very old Actions
            if(cea.afs_sentDate == null && "1".equals(cea.formal) && (cea.afs_actionId == null ||
                    cea.afs_actionId.trim().length() == 0)) {
                // Only formal actions are sent to AFS. If this is old, then it was
                // sent by an earlier application and CETA does not have the AFS information.
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, 2002);
                Timestamp ts = new Timestamp(cal.getTimeInMillis());
                if(cea.actionDate.before(ts)) {
                    cea.afs_actionId = "legacy";
                }
            }
            partialRow = false;
            prevSequenceNumber = cea.sequence_number;
        } catch (NoSuchElementException e) {
            eof = true;
            CetaPopulateEnforcements.outStream
            .write("(EnforcementAction) EOF or invalid data on fieldNum= " + fieldNum + "; sequence_number=" + cea.sequence_number
                    + " (previous sequence_number=" + prevSequenceNumber
                    + ")\n");
            if (partialRow) {
                CetaPopulateEnforcements.outStream
                .write("ERROR: Failed to read fieldNum= " + fieldNum + "; sequence_number=" + cea.sequence_number
                    + " (previous sequence_number=" + prevSequenceNumber
                    + ")\n");
                CetaPopulateEnforcements.outStream.flush();
                throw new RuntimeException(e);
            }
        } catch (NumberFormatException e) {
            CetaPopulateEnforcements.outStream
            .write("NumberFormatException on fieldNum= " + fieldNum + "; sequence_number=" + cea.sequence_number
                    + " (previous sequence_number=" + prevSequenceNumber
                    + ")\n");
            CetaPopulateEnforcements.outStream.flush();
            throw new RuntimeException(e);
        }
        return cea;
    }
    
    static String removeMarkersAndNULL(String s) {
        String rtn = s;
        if(rtn != null && rtn.length() >= 2) {
            if(rtn.charAt(0) != ';' || rtn.charAt(rtn.length() - 1) != ';') {
                try {
                CetaPopulateEnforcements.outStream
                .write("Missing the bracketing semicolons.  Previous sequence_number=" + prevSequenceNumber + ", field value="
                        + s + "\n");
                } catch(IOException ioe) {
                    ;
                }
            } else {
                rtn = rtn.substring(1, rtn.length() - 1);
            }
        }
        if(rtn != null && rtn.equals("<NULL>")) rtn = "";
        return rtn;
    }
    
    static Integer getInteger(Logger logger, String value) throws NumberFormatException {
        Integer rtn = null;
        if (value != null && value.length() > 0) {
            try {
                rtn = Integer.parseInt(value);
            } catch(NumberFormatException nfe) {

            }
        }
        return rtn;
    }

    private static Timestamp convertDate(Logger logger, String dateStr, String label, Integer sequence_number) {
        Timestamp rtn = null;
        if(!"0.00".equals(dateStr)) {
            SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date;
            try {
                if (dateStr.length() != 0) {
                    if(dateStr.length() >= 3) {
                        if(dateStr.endsWith(".0")) {  // remove part we cannot handle
                            dateStr = dateStr.substring(0, dateStr.length() - 3);
                        }
                    }
                    date = dateFormat.parse(dateStr);
                    rtn = new Timestamp(date.getTime());
                }
            } catch (ParseException e) {
                try {
                    logger.error(e.getMessage(), e);
                    CetaPopulateEnforcements.outStream
                    .write("ERRROR: Bad Date for " + label
                            + " (" + dateStr + "); sequence_number=" + sequence_number
                            + " (previous sequence_number=" + prevSequenceNumber
                            + ")\n");
                } catch (IOException ex) {
                }
            }
        }
        return rtn;
    }

    public Timestamp getDate_entered() {
        return date_entered;
    }

    public void setDate_entered(Timestamp date_entered) {
        this.date_entered = date_entered;
    }

    public Integer getEnf_id() {
        return enf_id;
    }

    public void setEnf_id(Integer enf_id) {
        this.enf_id = enf_id;
    }

    public String getEntered_by() {
        return entered_by;
    }

    public void setEntered_by(String entered_by) {
        this.entered_by = entered_by;
    }

    public String getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(String evaluator) {
        this.evaluator = evaluator;
    }

    public String getFacility_id() {
        return facility_id;
    }

    public void setFacility_id(String facility_id) {
        this.facility_id = facility_id;
    }

    public String getFormal() {
        return formal;
    }

    public void setFormal(String formal) {
        this.formal = formal;
    }

    public String getHpv() {
        return hpv;
    }

    public void setHpv(String hpv) {
        this.hpv = hpv;
    }

    public String getHpv_code() {
        return hpv_code;
    }

    public void setHpv_code(String hpv_code) {
        this.hpv_code = hpv_code;
    }

    public String getInformal() {
        return informal;
    }

    public void setInformal(String informal) {
        this.informal = informal;
    }

    public String getMemoToAfs() {
        return memoToAfs;
    }

    public void setMemoToAfs(String memoToAfs) {
        this.memoToAfs = memoToAfs;
    }

    public String getSepAmount() {
        return sepAmount;
    }

    public void setSepAmount(String sepAmount) {
        this.sepAmount = sepAmount;
    }

    public Integer getSequence_number() {
        return sequence_number;
    }

    public void setSequence_number(Integer sequence_number) {
        this.sequence_number = sequence_number;
    }

    public String getTypeOfViolation() {
        return typeOfViolation;
    }

    public void setTypeOfViolation(String typeOfViolation) {
        this.typeOfViolation = typeOfViolation;
    }

    public Timestamp getViolationDate() {
        return violationDate;
    }

    public void setViolationDate(Timestamp violationDate) {
        this.violationDate = violationDate;
    }

    public Timestamp getZeroDate() {
        return zeroDate;
    }

    public void setZeroDate(Timestamp zeroDate) {
        this.zeroDate = zeroDate;
    }

    public String getChkNonAtt() {
        return chkNonAtt;
    }

    public void setChkNonAtt(String chkNonAtt) {
        this.chkNonAtt = chkNonAtt;
    }

    public String getChkNonHPV() {
        return chkNonHPV;
    }

    public void setChkNonHPV(String chkNonHPV) {
        this.chkNonHPV = chkNonHPV;
    }

    public Timestamp getActionDate() {
        return actionDate;
    }

    public void setActionDate(Timestamp actionDate) {
        this.actionDate = actionDate;
    }

    public String getActionDiscovery() {
        return actionDiscovery;
    }

    public void setActionDiscovery(String actionDiscovery) {
        this.actionDiscovery = actionDiscovery;
    }

    public String getActionTypeCd() {
        return actionTypeCd;
    }

    public void setActionTypeCd(String actionTypeCd) {
        this.actionTypeCd = actionTypeCd;
    }

    public String getAfs_actionId() {
        return afs_actionId;
    }

    public void setAfs_actionId(String afs_actionId) {
        this.afs_actionId = afs_actionId;
    }

    public Timestamp getAfs_sentDate() {
        return afs_sentDate;
    }

    public void setAfs_sentDate(Timestamp afs_sentDate) {
        this.afs_sentDate = afs_sentDate;
    }

    public String getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(String cashAmount) {
        this.cashAmount = cashAmount;
    }

    public String getChkMact() {
        return chkMact;
    }

    public void setChkMact(String chkMact) {
        this.chkMact = chkMact;
    }

    public String getChkNeshap() {
        return chkNeshap;
    }

    public void setChkNeshap(String chkNeshap) {
        this.chkNeshap = chkNeshap;
    }

    public String getChkNsps() {
        return chkNsps;
    }

    public void setChkNsps(String chkNsps) {
        this.chkNsps = chkNsps;
    }

    public String getChkPsd() {
        return chkPsd;
    }

    public void setChkPsd(String chkPsd) {
        this.chkPsd = chkPsd;
    }

    public String getChkSm() {
        return chkSm;
    }

    public void setChkSm(String chkSm) {
        this.chkSm = chkSm;
    }

    public String getChkTv() {
        return chkTv;
    }

    public void setChkTv(String chkTv) {
        this.chkTv = chkTv;
    }
}