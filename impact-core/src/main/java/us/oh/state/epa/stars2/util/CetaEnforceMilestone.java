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

public class CetaEnforceMilestone {
    protected static Scanner s;
    protected static Integer prevEnfId = null;
    protected static boolean eof;
    
    /*
  
    Do this in eclipse,  then replace "|eEnNdD;"  with "|" 
     
    SELECT ' ',
    rtrim(id), '|',
    rtrim(facility_id), '|',
    orderDate, '|',
    rtrim(settlementcase), '|',
    rtrim(milestone), '|',
    rtrim(payment_amt), '|',
    deadlineDate, '|',
    completeDate, '|',
    rtrim(memo),  '|eEnNdD' from ceta.dbo.enforce_milestones order by facility_id, orderDate;
    */
    Integer id;
    String facility_id;
    Timestamp orderDate;
    String settlementcase;
    String milestone;
    String payment_amt;
    Timestamp deadlineDate;
    Timestamp completionDate;
    String memo;

    // Initialize to read the flat file.
    static boolean initialize(String fileName, Logger logger) throws IOException {
        s = null;
        prevEnfId = null;
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
    static CetaEnforceMilestone next(Logger logger) throws IOException {
        int fieldNum = 0;
        boolean partialRow = false;;
        CetaEnforceMilestone cem = new CetaEnforceMilestone();
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
                cem.id = getInteger(str);
            }
            cem.facility_id = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                cem.orderDate = convertDate(str, "orderDate", cem.id);
            }
            
            cem.settlementcase = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cem.milestone = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            cem.payment_amt = removeMarkersAndNULL(s.next().trim());
            fieldNum++;

            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                cem.deadlineDate = convertDate(str, "deadlineDate", cem.id);
            }
            
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                cem.completionDate = convertDate(str, "completionDate", cem.id);
            }
            
            cem.memo = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            
            partialRow = false;
            prevEnfId = cem.id;
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            eof = true;
            CetaPopulateEnforcements.outStream
            .write("(EnforcementMilestine)EOF or invalid data on fieldNum= " + fieldNum + "; id=" + cem.id
                    + " (previous id=" + prevEnfId
                    + ")\n");
            if (partialRow) {
                CetaPopulateEnforcements.outStream
                .write("ERROR: Failed to read fieldNum= " + fieldNum + "; id=" + cem.id
                    + " (previous id=" + prevEnfId
                    + ")\n");
                CetaPopulateEnforcements.outStream.flush();
                throw new RuntimeException(e);
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            CetaPopulateEnforcements.outStream
            .write("NumberFormatException on fieldNum= " + fieldNum + "; id=" + cem.id
                    + " (previous id=" + prevEnfId
                    + ")\n");
            CetaPopulateEnforcements.outStream.flush();
            throw new RuntimeException(e);
        }
        return cem;
    }
    
    static String removeMarkersAndNULL(String s) {
        String rtn = s;
        if(rtn != null && rtn.length() >= 2) {
            if(rtn.charAt(0) != ';' || rtn.charAt(rtn.length() - 1) != ';') {
                try {
                CetaPopulateEnforcements.outStream
                .write("Missing the bracketing semicolons.  Previous id=" + prevEnfId + ", field value="
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

    private static Timestamp convertDate(String dateStr, String label, Integer id) {
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
                    CetaPopulateEnforcements.outStream
                    .write("ERRROR: Bad Date for " + label
                            + " (" + dateStr + "); id=" + id
                            + " (previous id=" + prevEnfId
                            + ")\n");
                } catch (IOException ex) {
                }
            }
        }
        return rtn;
    }

    public Timestamp getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Timestamp completionDate) {
        this.completionDate = completionDate;
    }

    public Timestamp getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(Timestamp deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public String getFacility_id() {
        return facility_id;
    }

    public void setFacility_id(String facility_id) {
        this.facility_id = facility_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMilestone() {
        return milestone;
    }

    public void setMilestone(String milestone) {
        this.milestone = milestone;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public String getPayment_amt() {
        return payment_amt;
    }

    public void setPayment_amt(String payment_amt) {
        this.payment_amt = payment_amt;
    }

    public String getSettlementcase() {
        return settlementcase;
    }

    public void setSettlementcase(String settlementcase) {
        this.settlementcase = settlementcase;
    }
}