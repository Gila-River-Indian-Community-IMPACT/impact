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

public class CetaSiteVisit {
    protected static Scanner s;
    protected static Integer prevSiteVisitId = null;
    protected static boolean eof;
    
    /*
    
    select * from stars2.ce_site_visit;
    select count(*) from stars2.ce_site_visit;

    delete stars2.ce_facility_neshaps_xref where facility_hist_id in
     (select facility_hist_id from stars2.ce_site_visit);
    delete stars2.ce_facility_nsps_subparts where facility_hist_id in
     (select facility_hist_id from stars2.ce_site_visit);
     delete stars2.ce_facility_nsr_pollutants where facility_hist_id in
     (select facility_hist_id from stars2.ce_site_visit);
     delete stars2.ce_facility_psd_pollutants where facility_hist_id in
     (select facility_hist_id from stars2.ce_site_visit);
    delete stars2.ce_facility_hist where facility_hist_id in
     (select facility_hist_id from stars2.ce_site_visit);

     delete stars2.CE_SV_ATTACHMENT;
    delete stars2.ce_visit_eval_xref;
    delete stars2.ce_site_visit;
    
    select * from stars2.cm_sequence_def where sequence_nm = 'S_Visit_Id';
    update stars2.cm_sequence_def set last_used_num = 20000 where sequence_nm = 'S_Visit_Id';
    
    
    Do this in eclipse,  then replace "|eEnNdD;"  with "|" 
     
    SELECT ' ',
    rtrim(siteVisitId), '|',
    rtrim(facility_id), '|',
    rtrim(typeOfVisit), '|',
    siteVisitDate, '|',
    rtrim(evaluator), '|',
    rtrim(announced), '|',
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
    chknonAtt, '|',
    rtrim(memo_siteVisit),  '|eEnNdD' from ceta.dbo.siteVisit order by siteVisitId;
    */
    Integer siteVisitId;
    String facility_id;
    String typeOfVisit;
    Timestamp siteVisitDate;
    String evaluator;
    String announced;
    String afs_actionId;
    Timestamp afs_sentDate;
    Timestamp date_entered;
    String entered_by;
    String chkTv;
    String chkSm;
    String chkHPV;
    String chkNsps;
    String chkNeshap;
    String chkMact;
    String chkPsd;
    String chkAtt;
    String memo_siteVisit;

    // Initialize to read the flat file.
    static boolean initialize(String fileName, Logger logger) throws IOException {
        s = null;
        prevSiteVisitId = null;
        eof = false;
        File file = new File(CetaPopulateSiteVisits.directory + File.separator + "migrationData" + File.separator, fileName);
        File cleanFile = new File(CetaPopulateSiteVisits.directory + File.separator + "migrationData" + File.separator, "clean_" + fileName);
        FilterCharacters.filter(CetaPopulateSiteVisits.outStream, logger, file, cleanFile);
        try {
            s = new Scanner(cleanFile).useDelimiter(Pattern.compile("[|]"));
        } catch (FileNotFoundException e) {
            eof = true;
            String s = "Failure to find file "
                + CetaPopulateSiteVisits.directory + File.separator + "migrationData" + File.separator + fileName + "\n";
            logger.error(s, e);
            CetaPopulateSiteVisits.outStream.write(s);
            CetaPopulateSiteVisits.outStream.close();
            return false;
        }
        return true;
    }

    // Return true if requested record read. False if record not present or at
    // eof.
    static CetaSiteVisit next(Logger logger) throws IOException {
        int fieldNum = 0;
        boolean partialRow = false;;
        CetaSiteVisit csv = new CetaSiteVisit();
        if (eof)
            return null;
        try {
            // Read the attributes
            partialRow = false;
            fieldNum = 0;

            String str;
//          Read the attributes
            fieldNum = 0;
            str = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            partialRow = true;
            if(str != null && str.length() > 0) {
                csv.siteVisitId = getInteger(logger, str);
            }
            csv.facility_id = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            csv.typeOfVisit = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            
            str = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                csv.siteVisitDate = convertDate(logger, str, "siteVisitDate", csv.siteVisitId);
            }
            csv.evaluator = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            csv.announced = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            
            str = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                csv.afs_actionId = str;
            }
            str = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                csv.afs_sentDate = convertDate(logger, str, "afs_sentDate", csv.siteVisitId);
            }
            
            str = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                csv.date_entered = convertDate(logger, str, "date_entered", csv.siteVisitId);
            }
            csv.entered_by = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            
            csv.chkTv = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            csv.chkSm = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            csv.chkHPV = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            csv.chkNsps = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            csv.chkNeshap = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            csv.chkMact = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            csv.chkPsd = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            csv.chkAtt = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            csv.memo_siteVisit = removeMarkersAndNULL(logger, s.next().trim());
            fieldNum++;
            partialRow = false;
            prevSiteVisitId = csv.siteVisitId;
        } catch (NoSuchElementException e) {
            eof = true;
            String s = "EOF or invalid data on fieldNum= " + fieldNum + "; siteVisitId=" + csv.siteVisitId
            + " (previous siteVisitId=" + prevSiteVisitId
            + ")\n";
            logger.error(s, e);
            CetaPopulateSiteVisits.outStream.write(s);
            if (partialRow) {
                CetaPopulateSiteVisits.outStream.write("ERROR: Failed to read fieldNum= " + fieldNum + "; siteVisitId=" + csv.siteVisitId
                    + " (previous siteVisitId=" + prevSiteVisitId
                    + ")\n");
                CetaPopulateSiteVisits.outStream.flush();
                throw new RuntimeException(e);
            }
        } catch (NumberFormatException e) {
            String s = "NumberFormatException on fieldNum= " + fieldNum + "; siteVisitId=" + csv.siteVisitId
            + " (previous siteVisitId=" + prevSiteVisitId
            + ")\n";
            logger.error(s, e);
            CetaPopulateSiteVisits.outStream.write(s);
            CetaPopulateSiteVisits.outStream.flush();
            throw new RuntimeException(e);
        }
        return csv;
    }
    
    static String removeMarkersAndNULL(Logger logger, String s) {
        String rtn = s;
        if(rtn != null && rtn.length() >= 2) {
            if(rtn.charAt(0) != ';' || rtn.charAt(rtn.length() - 1) != ';') {
                try {
                CetaPopulateSiteVisits.outStream
                .write("Missing the bracketing semicolons.  Previous siteVisitId=" + prevSiteVisitId + ", field value="
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
                String s = "getInteger failed on \"" + value +  "\"\n";
                logger.error("getInteger failed on \"" + value +  "\"", nfe);
                try {
                    CetaPopulateSiteVisits.outStream.write(s);
                } catch (IOException ioe) {
                    ;
                }
            }
        }
        return rtn;
    }

    private static Timestamp convertDate(Logger logger, String dateStr, String label, Integer siteVisitId) {
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
                String s = "ERRROR: Bad Date for " + label
                + " (" + dateStr + "); siteVisitId=" + siteVisitId
                + " (previous siteVisitId=" + prevSiteVisitId  + ")\n";
                logger.error(s, e);
                try {
                    CetaPopulateSiteVisits.outStream.write(s);
                } catch (IOException ex) {
                    ;
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

    public String getAnnounced() {
        return announced;
    }

    public void setAnnounced(String announced) {
        this.announced = announced;
    }

    public String getChkAtt() {
        return chkAtt;
    }

    public void setChkAtt(String chkAtt) {
        this.chkAtt = chkAtt;
    }

    public String getChkHPV() {
        return chkHPV;
    }

    public void setChkHPV(String chkHPV) {
        this.chkHPV = chkHPV;
    }

    public String getChkMact() {
        return chkMact;
    }

    public void setChkMact(String chkMact) {
        this.chkMact = chkMact;
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

    public String getMemo_siteVisit() {
        return memo_siteVisit;
    }

    public void setMemo_siteVisit(String memo_siteVisit) {
        this.memo_siteVisit = memo_siteVisit;
    }

    public Timestamp getSiteVisitDate() {
        return siteVisitDate;
    }

    public void setSiteVisitDate(Timestamp siteVisitDate) {
        this.siteVisitDate = siteVisitDate;
    }

    public Integer getSiteVisitId() {
        return siteVisitId;
    }

    public void setSiteVisitId(Integer siteVisitId) {
        this.siteVisitId = siteVisitId;
    }

    public String getTypeOfVisit() {
        return typeOfVisit;
    }

    public void setTypeOfVisit(String typeOfVisit) {
        this.typeOfVisit = typeOfVisit;
    }

    public String getChkNeshap() {
        return chkNeshap;
    }

    public void setChkNeshap(String chkNeshap) {
        this.chkNeshap = chkNeshap;
    }

    public String getAfs_actionId() {
        return afs_actionId;
    }

    public void setAfs_actionId(String afs_actionId) {
        this.afs_actionId = afs_actionId;
    }
}