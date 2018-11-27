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

public class CetaEnforceNote {
    protected static Scanner s;
    protected static Integer prevEnf_id = null;
    protected static boolean eof;
    
    /*

    Do this in eclipse,  then replace "|eEnNdD;"  with "|" 
     
    SELECT ' ',
    rtrim(enf_id), '|',
    rtrim(facility_id), '|',
    noteDate, '|',
    rtrim(note),  '|eEnNdD' from ceta.dbo.enforce_note order by facility_id, enf_id, noteDate;
    */
    Integer enf_id;
    String facility_id;
    Timestamp noteDate;
    String note;

    // Initialize to read the flat file.
    static boolean initialize(String fileName, Logger logger) throws IOException {
        s = null;
        prevEnf_id = null;
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
    static CetaEnforceNote next(Logger logger) throws IOException {
        int fieldNum = 0;
        boolean partialRow = false;;
        CetaEnforceNote cen = new CetaEnforceNote();
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
                cen.enf_id = getInteger(logger, str);
            }
            cen.facility_id = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            
            str = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            if(str != null && str.length() > 0) {
                cen.noteDate = convertDate(logger, str, "enf_id", cen.enf_id);
            }
            
            cen.note = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            partialRow = false;
            prevEnf_id = cen.enf_id;
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            eof = true;
            CetaPopulateEnforcements.outStream
            .write("(EnforcementNote) EOF or invalid data on fieldNum= " + fieldNum + "; enf_id=" + cen.enf_id
                    + " (previous enf_id=" + prevEnf_id
                    + ")\n");
            if (partialRow) {
                CetaPopulateEnforcements.outStream
                .write("ERROR: Failed to read fieldNum= " + fieldNum + "; enf_id=" + cen.enf_id
                    + " (previous enf_id=" + prevEnf_id
                    + ")\n");
                CetaPopulateEnforcements.outStream.flush();
                throw new RuntimeException(e);
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            CetaPopulateEnforcements.outStream
            .write("NumberFormatException on fieldNum= " + fieldNum + "; enf_id=" + cen.enf_id
                    + " (previous enf_id=" + prevEnf_id
                    + ")\n");
            CetaPopulateEnforcements.outStream.flush();
            throw new RuntimeException(e);
        }
        return cen;
    }
    
    static String removeMarkersAndNULL(String s) {
        String rtn = s;
        if(rtn != null && rtn.length() >= 2) {
            if(rtn.charAt(0) != ';' || rtn.charAt(rtn.length() - 1) != ';') {
                try {
                CetaPopulateEnforcements.outStream
                .write("Missing the bracketing semicolons.  Previous enf_id=" + prevEnf_id + ", field value="
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

    private static Timestamp convertDate(Logger logger, String dateStr, String label, Integer enf_id) {
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
                            + " (" + dateStr + "); enf_id=" + enf_id
                            + " (previous enf_id=" + prevEnf_id
                            + ")\n");
                } catch (IOException ex) {
                }
            }
        }
        return rtn;
    }

    public Integer getEnf_id() {
        return enf_id;
    }

    public void setEnf_id(Integer enf_id) {
        this.enf_id = enf_id;
    }

    public String getFacility_id() {
        return facility_id;
    }

    public void setFacility_id(String facility_id) {
        this.facility_id = facility_id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Timestamp getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(Timestamp noteDate) {
        this.noteDate = noteDate;
    }
}