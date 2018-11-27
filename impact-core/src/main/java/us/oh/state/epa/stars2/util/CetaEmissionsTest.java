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

public class CetaEmissionsTest {
    protected static Scanner s;
    protected static Integer prevAppendixK_id = null;
    protected static boolean eof;
    
    /*

The reviewer Bridget Byrne in stack test 3818 (in Test CETA) has a special character in it.  When viewed in notepad (see attached) it causes a new line.  In my migration program, it causes it to consider it two fields.
I cannot figure out what the character is.  It does not appear to be a tab, line-feed, soft-return, page-break or carrage-return.
Any idea how to find what the character is?
Thanks,
Dennis

select replace(reviewer, char(9), 'X') from ceta.dbo.appendixK where appendixK_ID = 3818;
select replace(reviewer, char(10), 'X') from ceta.dbo.appendixK where appendixK_ID = 3818;
select replace(reviewer, char(11), 'X') from ceta.dbo.appendixK where appendixK_ID = 3818;
select replace(reviewer, char(12), 'X') from ceta.dbo.appendixK where appendixK_ID = 3818;
select replace(reviewer, char(13), 'X') from ceta.dbo.appendixK where appendixK_ID = 3818;

Chr(9) Tab 
Chr(10) Line-feed 
Chr(11) Soft return (Shift+Enter) 
Chr(12) Page break 
Chr(13) Carriage return 

     */
    
    /*
    
    select * from stars2.ce_stack_test;
    select count(*) from stars2.ce_stack_test;
    select count(*) from ceta.dbo.appendixK;

    delete stars2.ce_stack_test_pollutant_xref;
    delete stars2.ce_stack_test_witness_xref;
    delete stars2.ce_stack_test_visit_date_xref;
       delete stars2.ce_st_attachment;
    delete stars2.ce_stack_test;
    
    select * from stars2.cm_sequence_def where sequence_nm = 'S_Stack_Test_Id';
    update stars2.cm_sequence_def set last_used_num = 20000 where sequence_nm = 'S_Stack_Test_Id';
    
    Do this in eclipse,  then replace "|eEnNdD;"  with "|" 
     
    SELECT ' ',
    rtrim(appendixK_id), '|',
    rtrim(siteVisit_id), '|',
    rtrim(ak.facility_id), '|',
    rtrim(euId), '|',
    rtrim(euid_desc), '|',
    rtrim(copyToSiteVisit), '|',
    rtrim(ak.afs_actionId), '|',
    ak.afs_sentDate, '|',
    evaluationDate, '|',
    dateScheduled, '|',
    receivedDate, '|',
    reviewDate, '|',
    shutdownDate, '|',
    rtrim(companyDidTest), '|',
    rtrim(methodNo), '|',
    rtrim(scc), '|',
    rtrim(control_equipment), '|',
    rtrim(avg_emmision_rate), '|',
    rtrim(avg_operating_rate), '|',
    rtrim(monitoring_equipment), '|',
    rtrim(allowable_rate), '|',
    rtrim(max_rate), '|',
    rtrim(ak.evaluator), '|',
    rtrim(reviewer), '|',
    results_pass, '|',
    results_fail, '|',
    results_retest, '|',
    results_notCompleted, '|',
    conformance_yes, '|',
    conformance_no, '|',
    audits_yes, '|',
    audits_no, '|',
    auditPass_yes, '|',
    auditPass_no, '|',
    auditPass_na, '|',
    witness_yes, '|',
    witness_no, '|',
    chkPti, '|',
    chkPto, '|',
    ak.chkTv, '|',
    ak.chkNsps, '|',
    ak.chkMact, '|',
    chkBif, '|',
    chkIV, '|',
    chkOther, '|',
    ak.date_entered, '|',
    rtrim(ak.entered_by), '|',
    rtrim(pollutantsCode), '|',
    rtrim(pollutants), '|',
    rtrim(memoAppenK), '|',
    sv.siteVisitId, '|',
    sv.chkTV, '|',
    sv.chkSm, '|',
    sv.chknonHPV, '|',
    sv.chkNsps, '|',
    sv.chkNeshap, '|',
    sv.chkMact, '|',
    sv.chkPsd, '|',
    sv.chknonAtt, '|eEnNdD' from ceta.dbo.appendixK ak
    LEFT OUTER JOIN ceta.dbo.siteVisit sv ON (ak.siteVisit_id = sv.siteVisitId)
    where appendixK_id < 6000 ORDER BY appendixK_id;   OR  where appendixK_id >= 6000 ORDER BY appendixK_id;  // D O   I N  T W O   P A R T S   B E C A U S E  T O O   B I G
    */
    Integer appendixK_id;
    Integer siteVisit_id;  // not used
    String facility_id;
    String euId;
    String euid_desc;  // not used
    String copyToSiteVisit; // not used
    String afs_actionId;
    Timestamp afs_sentDate;
    Timestamp evaluationDate;  //  this is XXXXXXXXXX
    Timestamp dateScheduled;
    Timestamp receivedDate;
    Timestamp reviewDate;    //  this is XXXXXXXX
    Timestamp shutdownDate;  // not used--always null
    String companyDidTest;
    String methodNo;
    String scc;
    String control_equipment;
    String avg_emmision_rate;
    String avg_operating_rate;
    String monitoring_equipment;
    String allowable_rate;
    String max_rate;
    String evaluator;
    String reviewer;
    String results_pass;
    String results_fail;
    String results_retest;
    String results_notCompleted;
    String conformance_yes;
    String conformance_no;
    String audits_yes;
    String audits_no;
    String auditsPass_yes;
    String auditsPass_no;
    String auditPass_na;
    String witness_yes;
    String witness_no;
    String chkPti;
    String chkPto;
    String chkTv;
    String chkNsps;
    String chkMact;
    String chkBif;
    String chkIV;
    String chkOther;
    Timestamp date_entered;
    String entered_by;
    String pollutantsCode;
    String pollutants;  // not used
    String memoAppenK;
    Integer sVsiteVisitId; // the rest (with names starting "sV") come from an outer join with the corresponding site visit table (if there is one)
    String sVchkTv;
    String sVchkSm;
    String sVchkHPV;
    String sVchkNsps;
    String sVchkNeshap;
    String sVchkMact;
    String sVchkPsd;
    String sVchkAtt;

    // Initialize to read the flat file.
    static boolean initialize(String fileName, Logger logger) throws IOException {
        s = null;
       prevAppendixK_id = null;
        eof = false;
        File file = new File(CetaPopulateEmissionsTests.directory + File.separator + "migrationData" + File.separator, fileName);
        File cleanFile = new File(CetaPopulateEmissionsTests.directory + File.separator + "migrationData" + File.separator, "clean_" + fileName);
        FilterCharacters.filter(CetaPopulateEmissionsTests.outStream, logger, file, cleanFile);
        try {
            s = new Scanner(cleanFile).useDelimiter(Pattern.compile("[|]"));
        } catch (FileNotFoundException e) {
            eof = true;
            String s = "Failure to find file "
                + CetaPopulateEmissionsTests.directory + File.separator + "migrationData" + File.separator + fileName + "\n";
            logger.error(s, e);
            CetaPopulateEmissionsTests.outStream.write(s);
            CetaPopulateEmissionsTests.outStream.flush();
            CetaPopulateEmissionsTests.outStream.close();
            return false;
        }
        return true;
    }

    // Return true if requested record read. False if record not present or at
    // eof.
    static CetaEmissionsTest next() throws IOException {
//        CetaPopulateEmissionsTests.outStream.write("P" + CetaEmissionsTest.prevAppendixK_id +
//                "\n");
//        CetaPopulateEmissionsTests.outStream.flush();
        int fieldNum = 0;
        boolean partialRow = false;;
        CetaEmissionsTest cet = new CetaEmissionsTest();
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
                cet.appendixK_id = getInteger(str);
            }
            removeMarkersAndNULL(s.next()); //siteVisit_id;  not used
            fieldNum++;
            cet.facility_id = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.euId = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.euid_desc = removeMarkersAndNULL(s.next()); //euid_desc; 
            fieldNum++;
            removeMarkersAndNULL(s.next()); //copyToSiteVisit; // not used
            fieldNum++;
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                cet.afs_actionId = str;
            }
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                cet.afs_sentDate = convertDate(str, "afs_sentDate", cet.appendixK_id);
            }
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                cet.evaluationDate = convertDate(str, "evaluationDate", cet.appendixK_id);
            }
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                cet.dateScheduled = convertDate(str, "dateScheduled", cet.appendixK_id);
            }
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                cet.receivedDate = convertDate(str, "receivedDate", cet.appendixK_id);
            }
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                cet.reviewDate = convertDate(str, "reviewDate", cet.appendixK_id);
            }
            removeMarkersAndNULL(s.next()); //shutdownDate;  // not used--always null
            fieldNum++;
            cet.companyDidTest = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.methodNo = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.scc = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.control_equipment = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.avg_emmision_rate = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.avg_operating_rate = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.monitoring_equipment = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.allowable_rate = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.max_rate = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.evaluator = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.reviewer = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.results_pass = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.results_fail = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.results_retest = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.results_notCompleted = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.conformance_yes = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.conformance_no = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.audits_yes = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.audits_no = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.auditsPass_yes = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.auditsPass_no = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.auditPass_na = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.witness_yes = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.witness_no = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.chkPti = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.chkPto = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.chkTv = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.chkNsps = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.chkMact = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.chkBif = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.chkIV = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.chkOther = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                cet.date_entered = convertDate(str, "date_entered", cet.appendixK_id);
            }
            cet.entered_by = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.pollutantsCode = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            removeMarkersAndNULL(s.next()); //pollutants;  // not used
            fieldNum++;
            cet.memoAppenK = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            partialRow = true;
            if(str != null && str.length() > 0) {
                cet.sVsiteVisitId = getInteger(str);
            }
            cet.sVchkTv = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.sVchkSm = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.sVchkHPV = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.sVchkNsps = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.sVchkNeshap = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.sVchkMact = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.sVchkPsd = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cet.sVchkAtt = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            partialRow = false;
            prevAppendixK_id = cet.appendixK_id;
        } catch (NoSuchElementException e) {
            eof = true;
            CetaPopulateEmissionsTests.outStream
            .write("EOF or invalid data on fieldNum= " + fieldNum + "; appendixK_id=" + cet.appendixK_id
                    + " (previous appendixK_id=" + prevAppendixK_id
                    + ")\n");
            if (partialRow) {
                CetaPopulateEmissionsTests.outStream
                .write("ERROR: Failed to read fieldNum= " + fieldNum + "; appendixK_id=" + cet.appendixK_id
                    + " (previous appendixK_id=" + prevAppendixK_id
                    + ")\n");
                CetaPopulateEmissionsTests.outStream.flush();
                throw new RuntimeException(e);
            }
        } catch (NumberFormatException e) {
            CetaPopulateEmissionsTests.outStream
            .write("NumberFormatException on fieldNum= " + fieldNum + "; appendixK_id=" + cet.appendixK_id
                    + " (previous appendixK_id=" + prevAppendixK_id
                    + ")\n");
            CetaPopulateEmissionsTests.outStream.flush();
            throw new RuntimeException(e);
        }
//        String s = "Ra";  // "Returning from next().  ";
//        if(cet == null) s = s + "cet is null\n";
//        else s = s + cet.appendixK_id + "\n";
//        CetaPopulateEmissionsTests.outStream.write(s);
//        CetaPopulateEmissionsTests.outStream.flush();
        return cet;
    }
    
    static String removeMarkersAndNULL(String s) {
        String rtn = s;
        if(rtn != null && rtn.length() >= 2) {
            if(rtn.charAt(0) != ';' || rtn.charAt(rtn.length() - 1) != ';') {
                try {
                CetaPopulateEmissionsTests.outStream
                .write("Missing the bracketing semicolons.  Previous appendixK_id=" + prevAppendixK_id + ", field value="
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
                    CetaPopulateEmissionsTests.outStream
                    .write("ERRROR: Bad Date for " + label
                            + " (" + dateStr + "); appendixK_id=" + appendixK_id
                            + " (previous appendixK_id=" + prevAppendixK_id
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

    public String getAllowable_rate() {
        return allowable_rate;
    }

    public Integer getAppendixK_id() {
        return appendixK_id;
    }

    public String getAuditPass_na() {
        return auditPass_na;
    }

    public String getAudits_no() {
        return audits_no;
    }

    public String getAudits_yes() {
        return audits_yes;
    }

    public String getAuditsPass_no() {
        return auditsPass_no;
    }

    public String getAuditsPass_yes() {
        return auditsPass_yes;
    }

    public String getAvg_emmision_rate() {
        return avg_emmision_rate;
    }

    public String getAvg_operating_rate() {
        return avg_operating_rate;
    }

    public String getChkBif() {
        return chkBif;
    }

    public String getChkIV() {
        return chkIV;
    }

    public String getChkMact() {
        return chkMact;
    }

    public String getChkNsps() {
        return chkNsps;
    }

    public String getChkOther() {
        return chkOther;
    }

    public String getChkPti() {
        return chkPti;
    }

    public String getChkPto() {
        return chkPto;
    }

    public String getChkTv() {
        return chkTv;
    }

    public String getCompanyDidTest() {
        return companyDidTest;
    }

    public String getConformance_no() {
        return conformance_no;
    }

    public String getConformance_yes() {
        return conformance_yes;
    }

    public String getControl_equipment() {
        return control_equipment;
    }

    public Timestamp getDate_entered() {
        return date_entered;
    }

    public Timestamp getDateScheduled() {
        return dateScheduled;
    }

    public String getEntered_by() {
        return entered_by;
    }

    public String getEuId() {
        return euId;
    }

    public String getEuid_desc() {
        return euid_desc;
    }

    public Timestamp getEvaluationDate() {
        return evaluationDate;
    }

    public String getEvaluator() {
        return evaluator;
    }

    public String getFacility_id() {
        return facility_id;
    }

    public String getMax_rate() {
        return max_rate;
    }

    public String getMemoAppenK() {
        return memoAppenK;
    }

    public String getMethodNo() {
        return methodNo;
    }

    public String getMonitoring_equipment() {
        return monitoring_equipment;
    }

    public String getPollutantsCode() {
        return pollutantsCode;
    }

    public Timestamp getReceivedDate() {
        return receivedDate;
    }

    public String getResults_fail() {
        return results_fail;
    }

    public String getResults_notCompleted() {
        return results_notCompleted;
    }

    public String getResults_pass() {
        return results_pass;
    }

    public String getResults_retest() {
        return results_retest;
    }

    public Timestamp getReviewDate() {
        return reviewDate;
    }

    public String getReviewer() {
        return reviewer;
    }

    public String getScc() {
        return scc;
    }

    public String getWitness_no() {
        return witness_no;
    }

    public String getWitness_yes() {
        return witness_yes;
    }

    public String getAfs_actionId() {
        return afs_actionId;
    }

    public void setAfs_actionId(String afs_actionId) {
        this.afs_actionId = afs_actionId;
    }

    public String getSVchkAtt() {
        return sVchkAtt;
    }

    public void setSVchkAtt(String vchkAtt) {
        sVchkAtt = vchkAtt;
    }

    public String getSVchkHPV() {
        return sVchkHPV;
    }

    public void setSVchkHPV(String vchkHPV) {
        sVchkHPV = vchkHPV;
    }

    public String getSVchkMact() {
        return sVchkMact;
    }

    public void setSVchkMact(String vchkMact) {
        sVchkMact = vchkMact;
    }

    public String getSVchkNeshap() {
        return sVchkNeshap;
    }

    public void setSVchkNeshap(String vchkNeshap) {
        sVchkNeshap = vchkNeshap;
    }

    public String getSVchkNsps() {
        return sVchkNsps;
    }

    public void setSVchkNsps(String vchkNsps) {
        sVchkNsps = vchkNsps;
    }

    public String getSVchkPsd() {
        return sVchkPsd;
    }

    public void setSVchkPsd(String vchkPsd) {
        sVchkPsd = vchkPsd;
    }

    public String getSVchkSm() {
        return sVchkSm;
    }

    public void setSVchkSm(String vchkSm) {
        sVchkSm = vchkSm;
    }

    public String getSVchkTv() {
        return sVchkTv;
    }

    public void setSVchkTv(String vchkTv) {
        sVchkTv = vchkTv;
    }

    public Integer getSVsiteVisitId() {
        return sVsiteVisitId;
    }

    public void setSVsiteVisitId(Integer vsiteVisitId) {
        sVsiteVisitId = vsiteVisitId;
    }
}