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

public class CetaFceScheduled {
    protected static Scanner s;
    protected static Integer prevScheduled_id = null;
    protected static boolean eof;
    
    /* 
    Do this in eclipse,  then replace "|eEnNdD;"  with "|" 
     
    SELECT ' ',
    rtrim(scheduled_id), '|',
    rtrim(facility_id), '|',
    rtrim(fce_goal), '|',
    rtrim(fceScheduledStaff), '|',
    date_entered, '|',
    rtrim(entered_by), '|',
    lastDate_each_quarter, '|',
    rtrim(TV), '|',
    rtrim(SM), '|',
    rtrim(senttoUSEPA), '|',
    rtrim(memo_fce_scheduled), '|eEnNdD' from ceta.dbo.fce_scheduled order by facility_id, scheduled_id;
    */
    private Integer scheduled_id;
    private String facility_id;
    private String fce_goal;
    private String fceScheduledStaff;
    private Timestamp date_entered;
    private String entered_by;
    private Timestamp lastDate_each_quarter;
    private String TV;  // This determines Inspection Classification
    private String SM;  // This determines Inspection Classification
    private String senttoUSEPA;
    private String memo_fce_schedulted;

    // Initialize to read the flat file.
    static boolean initialize(String fileName, Logger logger) throws IOException {
        s = null;
        prevScheduled_id = null;
        eof = false;
        File file = new File(CetaPopulateFCEs.directory + File.separator + "migrationData" + File.separator, fileName);
        File cleanFile = new File(CetaPopulateFCEs.directory + File.separator + "migrationData" + File.separator, "clean_" + fileName);
        FilterCharacters.filter(CetaPopulateFCEs.outStream, logger, file, cleanFile);
        try {
            s = new Scanner(cleanFile).useDelimiter(Pattern.compile("[|]"));
        } catch (FileNotFoundException e) {
            eof = true;
            CetaPopulateFCEs.outStream.write("Failure to find file "
                    + CetaPopulateFCEs.directory + File.separator + "migrationData" + File.separator + fileName + "\n");
            CetaPopulateFCEs.outStream.close();
            return false;
        }
        return true;
    }

    // Return true if requested record read. False if record not present or at
    // eof.
    static CetaFceScheduled next() throws IOException {
        int fieldNum = 0;
        boolean partialRow = false;;
        CetaFceScheduled fceSched = new CetaFceScheduled();
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
                fceSched.scheduled_id = getInteger(str);
            }
            fceSched.facility_id = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            fceSched.fce_goal = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            fceSched.fceScheduledStaff = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                fceSched.date_entered = convertDate(str, "date_entered", fceSched.scheduled_id);
            }
            fieldNum++;
            fceSched.entered_by = removeMarkersAndNULL(s.next().trim());
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                fceSched.lastDate_each_quarter = convertDate(str, "lastDate_each_quarter", fceSched.scheduled_id);
            }
            fceSched.TV = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            fceSched.SM = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            fceSched.senttoUSEPA = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            fceSched.memo_fce_schedulted = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            partialRow = false;
            prevScheduled_id = fceSched.scheduled_id;
        } catch (NoSuchElementException e) {
            eof = true;
            String msg = "; fceSched is null";
            if(fceSched != null) msg = "; scheduled_id=" + fceSched.scheduled_id;
            CetaPopulateFCEs.outStream
            .write("EOF or invalid data on fieldNum= " + fieldNum + msg
                    + " (previous scheduled_id=" + prevScheduled_id
                    + ")\n");
            if (partialRow) {
                CetaPopulateFCEs.outStream
                .write("ERROR: Failed to read fieldNum= " + fieldNum + msg
                    + " (previous scheduled_id=" + prevScheduled_id
                    + ")\n");
                CetaPopulateFCEs.outStream.flush();
                //throw new RuntimeException(e);
                return null;
            }
        } catch (NumberFormatException e) {
            CetaPopulateFCEs.outStream
            .write("NumberFormatException on fieldNum= " + fieldNum + "; scheduled_id=" + fceSched.scheduled_id
                    + " (previous scheduled_id=" + prevScheduled_id
                    + ")\n");
            CetaPopulateFCEs.outStream.flush();
           //throw new RuntimeException(e);
            return null;
        }
        return fceSched;
    }
    
    static String removeMarkersAndNULL(String s) {
        String rtn = s;
        if(rtn != null && rtn.length() >= 2) {
            if(rtn.charAt(0) != ';' || rtn.charAt(rtn.length() - 1) != ';') {
                try {
                CetaPopulateFCEs.outStream
                .write("Missing the bracketing semicolons.  Previous scheduled_id=" + prevScheduled_id + ", field value="
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

    private static Timestamp convertDate(String dateStr, String label, Integer scheduled_id) {
        Timestamp rtn = null;
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
                        + " (" + dateStr + "); scheduled_id=" + scheduled_id
                            + " (previous scheduled_id=" + prevScheduled_id
                            + ")\n");
            } catch (IOException ex) {
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

    public String getFacility_id() {
        return facility_id;
    }

    public void setFacility_id(String facility_id) {
        this.facility_id = facility_id;
    }

    public String getFce_goal() {
        return fce_goal;
    }

    public void setFce_goal(String fce_goal) {
        this.fce_goal = fce_goal;
    }

    public String getFceScheduledStaff() {
        return fceScheduledStaff;
    }

    public void setFceScheduledStaff(String fceScheduledStaff) {
        this.fceScheduledStaff = fceScheduledStaff;
    }

    public Timestamp getLastDate_each_quarter() {
        return lastDate_each_quarter;
    }

    public void setLastDate_each_quarter(Timestamp lastDate_each_quarter) {
        this.lastDate_each_quarter = lastDate_each_quarter;
    }

    public String getMemo_fce_schedulted() {
        return memo_fce_schedulted;
    }

    public void setMemo_fce_schedulted(String memo_fce_schedulted) {
        this.memo_fce_schedulted = memo_fce_schedulted;
    }

    public Integer getScheduled_id() {
        return scheduled_id;
    }

    public void setScheduled_id(Integer scheduled_id) {
        this.scheduled_id = scheduled_id;
    }

    public String getSenttoUSEPA() {
        return senttoUSEPA;
    }

    public void setSenttoUSEPA(String senttoUSEPA) {
        this.senttoUSEPA = senttoUSEPA;
    }

    public String getSM() {
        return SM;
    }

    public void setSM(String sm) {
        SM = sm;
    }

    public String getTV() {
        return TV;
    }

    public void setTV(String tv) {
        TV = tv;
    }

    public String getEntered_by() {
        return entered_by;
    }

    public void setEntered_by(String entered_by) {
        this.entered_by = entered_by;
    }
}