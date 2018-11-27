package us.oh.state.epa.stars2.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class CetaFce {
    protected static Scanner s;
    protected static Integer prevFceId = null;
    protected static boolean eof;
    
    /*
 
     us.oh.state.epa.stars2.util.CetaPopulateFCEs
    

    
    select * from stars2.cm_sequence_def where sequence_nm = 'S_Fce_Id';
    update stars2.cm_sequence_def set last_used_num = 20000 where sequence_nm = 'S_Fce_Id';
    
    FOLLOWING NOT NEEDED WHEN COMPLETE MIGRATION DONE
    update stars2.ce_site_visit set fce_id = null where fce_id is not null
    update stars2.CE_ENFORCEMENT set enf_discovery_id = null where enf_discovery_type_cd = 'F'
    
    
    ------------------------
    
    Do this in eclipse,  then replace "|eEnNdD;"  with "|" 
     
    SELECT ' ',
    rtrim(fceId), '|',
    rtrim(facility_id), '|',
    fceDate, '|',
    rtrim(evaluator), '|',
    rtrim(afs_actionId), '|',
    afs_sentDate, '|',
    date_entered, '|',
    rtrim(entered_by), '|',
    rtrim(chkTV), '|',
    rtrim(chkSm), '|',
    rtrim(chkNonHPV), '|',
    rtrim(chkNeshap), '|',
    rtrim(chkNsps), '|',
    rtrim(chkMact), '|',
    rtrim(chkPsd), '|',
    rtrim(chkNonAtt), '|',
    rtrim(memo_fce), '|eEnNdD' from ceta.dbo.fce order by facility_id, fceId;
    */
    Integer fceId;
    String facility_id;
    Timestamp fceDate;
    String evaluator;
    String afs_actionId;
    Timestamp afs_sentDate;
    Timestamp date_entered;
    String entered_by;
    String chkTV;
    String chkSM;
    String chkNonHPV;
    String chkNeshap;
    String chkNsps;
    String chkMact;
    String chkPsd;
    String chkNonAtt;
    String memo_fce;

    // Initialize to read the flat file.
    static boolean initialize(String fileName, Logger logger) throws IOException {
        s = null;
        prevFceId = null;
        eof = false;
        File file = new File(CetaPopulateFCEs.directory + File.separator + "migrationData" + File.separator, fileName);
        File cleanFile = new File(CetaPopulateFCEs.directory + File.separator + "migrationData" + File.separator, "clean_" + fileName);
        FilterCharacters.filter(CetaPopulateFCEs.outStream, logger, file, cleanFile);
        try {
            s = new Scanner(cleanFile).useDelimiter(Pattern.compile("[|]"));
        } catch (FileNotFoundException e) {
            eof = true;
            String s = "Failure to find file "
                + CetaPopulateFCEs.directory + File.separator + "migrationData" + File.separator + fileName + "\n";
            logger.error(s, e);
            CetaPopulateFCEs.outStream.write(s);
            CetaPopulateFCEs.outStream.close();
            return false;
        }
        return true;
    }

    // Return true if requested record read. False if record not present or at
    // eof.
    static CetaFce next() throws IOException {
        int fieldNum = 0;
        boolean partialRow = false;;
        CetaFce fce = new CetaFce();
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
                fce.fceId = getInteger(str);
            }
            fce.facility_id = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                fce.fceDate = convertDate(str, "fceDate", fce.fceId);
            }
            fce.evaluator = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                fce.afs_actionId = str;
            }
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                fce.afs_sentDate = convertDate(str, "afs_sentDate", fce.fceId);
            }
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                fce.date_entered = convertDate(str, "date_entered", fce.fceId);
            }
            fce.entered_by = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            fce.chkTV = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            fce.chkSM = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            fce.chkNonHPV = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            fce.chkNeshap = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            fce.chkNsps = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            fce.chkMact = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            fce.chkPsd = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            fce.chkNonAtt = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            fce.memo_fce = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            partialRow = false;
            prevFceId = fce.fceId;
        } catch (NoSuchElementException e) {
            eof = true;
            String msg = "; fce is null";
            if(fce != null) msg = "; fceId=" + fce.fceId;
            CetaPopulateFCEs.outStream
            .write("EOF or invalid data on fieldNum= " + fieldNum + msg
                    + " (previous fceId=" + prevFceId
                    + ")\n");
            if (partialRow) {
                CetaPopulateFCEs.outStream
                .write("ERROR: Failed to read fieldNum= " + fieldNum + msg
                    + " (previous fceId=" + prevFceId
                    + ")\n");
                CetaPopulateFCEs.outStream.flush();
                //throw new RuntimeException(e);
                return null;
            }
        } catch (NumberFormatException e) {
            CetaPopulateFCEs.outStream
            .write("NumberFormatException on fieldNum= " + fieldNum + "; fceId=" + fce.fceId
                    + " (previous fceId=" + prevFceId
                    + ")\n");
            CetaPopulateFCEs.outStream.flush();
            //throw new RuntimeException(e);
            return null;
        }
        return fce;
    }
    
    static String removeMarkersAndNULL(String s) {
        String rtn = s;
        if(rtn != null && rtn.length() >= 2) {
            if(rtn.charAt(0) != ';' || rtn.charAt(rtn.length() - 1) != ';') {
                try {
                CetaPopulateFCEs.outStream
                .write("Missing the bracketing semicolons.  Previous appendixK_id=" + prevFceId + ", field value="
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
    
    static Integer getInteger(String value) throws NumberFormatException {
        Integer rtn = null;
        if (value != null && value.length() > 0) {
            try {
                rtn = Integer.parseInt(value);
            } catch(NumberFormatException nfe) {

            }
        }
        return rtn;
    }

    private static Timestamp convertDate(String dateStr, String label, Integer appendixK_id) {
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
                    CetaPopulateFCEs.outStream
                    .write("ERRROR: Bad Date for " + label
                            + " (" + dateStr + "); appendixK_id=" + appendixK_id
                            + " (previous appendixK_id=" + prevFceId
                            + ")\n");
                } catch (IOException ex) {
                }
            }
        }
        return rtn;
    }

    public Timestamp getAfs_sentDate() {
        return afs_sentDate;
    }

    public void setAfs_sentDate(Timestamp afs_sentDate) {
        this.afs_sentDate = afs_sentDate;
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

    public String getChkSM() {
        return chkSM;
    }

    public void setChkSM(String chkSM) {
        this.chkSM = chkSM;
    }

    public String getChkTV() {
        return chkTV;
    }

    public void setChkTV(String chkTV) {
        this.chkTV = chkTV;
    }

    public Timestamp getDate_entered() {
        return date_entered;
    }

    public void setDate_entered(Timestamp date_entered) {
        this.date_entered = date_entered;
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

    public Timestamp getFceDate() {
        return fceDate;
    }

    public void setFceDate(Timestamp fceDate) {
        this.fceDate = fceDate;
    }

    public Integer getFceId() {
        return fceId;
    }

    public void setFceId(Integer fceId) {
        this.fceId = fceId;
    }

    public String getMemo_fce() {
        return memo_fce;
    }

    public void setMemo_fce(String memo_fce) {
        this.memo_fce = memo_fce;
    }

    public String getAfs_actionId() {
        return afs_actionId;
    }

    public void setAfs_actionId(String afs_actionId) {
        this.afs_actionId = afs_actionId;
    }
}