package us.oh.state.epa.stars2.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CetaComplaint {
    protected static Scanner s;
    protected static int prevComplaintsId = -1;
    protected static boolean eof;
    
    /*
    
    Since this is all numbers and date we can use the default ";" separator. 
     
    SELECT complaintsId, officeId, complaintsMonth, complaintsYear,
    openBurning, hpv, nonHpv, other, antiTampering, total1
     FROM ceta.dbo.complaints order by officeId, complaintsId;
     
    Note that we do not read queryDate because we can regenerate it from complaintsMonth and complaintsYear.
    */
    int complaintsId;
    int officeId;
    int complaintsMonth;
    int complaintsYear;
    int openBurning;
    int hpv;
    int nonHpv;
    int other;
    int antiTampering;
    int total1;


    // Initialize to read the flat file.
    static boolean initialize(String fileName) throws IOException {
        s = null;
        prevComplaintsId = -1;
        eof = false;
        File file = new File(CetaPopulateComplaints.directory + File.separator + "migrationData" + File.separator, fileName);
        try {
            s = new Scanner(file).useDelimiter(Pattern.compile("[;]"));
        } catch (FileNotFoundException e) {
            eof = true;
            CetaPopulateComplaints.outStream.write("Failure to find file "
                    + CetaPopulateComplaints.directory + File.separator + "migrationData" + File.separator + fileName + "\n");
            CetaPopulateComplaints.outStream.close();
            return false;
        }
        return true;
    }

    // Return true if requested record read. False if record not present or at
    // eof.
    static CetaComplaint next() throws IOException {
        int fieldNum = 0;
        boolean partialRow = false;;
        CetaComplaint complaint = new CetaComplaint();
        if (eof)
            return null;
        try {
            // Read the attributes
            partialRow = false;
            fieldNum = 0;

//          Read the attributes
            fieldNum = 0;
            complaint.complaintsId = getInt(s.next().trim());
            fieldNum++;
            
            partialRow = true;
            complaint.officeId = getInt(s.next().trim());
            fieldNum++;
            
            complaint.complaintsMonth = getInt(s.next().trim());
            fieldNum++;

            complaint.complaintsYear = getInt(s.next().trim());
            fieldNum++;
            
            complaint.openBurning = getInt(s.next().trim());
            fieldNum++;
            
            complaint.hpv = getInt(s.next().trim());
            fieldNum++;
            
            complaint.nonHpv = getInt(s.next().trim());
            fieldNum++;
            
            complaint.other = getInt(s.next().trim());
            fieldNum++;
            
            complaint.antiTampering = getInt(s.next().trim());
            fieldNum++;
            
            complaint.total1 = getInt(s.next().trim());
            fieldNum++;

            partialRow = false;
            prevComplaintsId = complaint.complaintsId;
        } catch (NoSuchElementException e) {
            eof = true;
            String msg = "; complaint is null";
            if(complaint != null) msg = "; complaintsId=" + complaint.complaintsId;
            CetaPopulateComplaints.outStream
            .write("EOF or invalid data on fieldNum= " + fieldNum + msg
                    + " (previous complaintsId=" + prevComplaintsId
                    + ")\n");
            if (partialRow) {
                CetaPopulateComplaints.outStream
                .write("ERROR: Failed to read fieldNum= " + fieldNum + msg
                    + " (previous complaintsId=" + prevComplaintsId
                    + ")\n");
                CetaPopulateComplaints.outStream.flush();
                //throw new RuntimeException(e);
                return null;
            }
        } catch (NumberFormatException e) {
            CetaPopulateComplaints.outStream
            .write("NumberFormatException on fieldNum= " + fieldNum + "; complaintsId=" + complaint.complaintsId
                    + " (previous complaintsId=" + prevComplaintsId
                    + ")\n");
            CetaPopulateComplaints.outStream.flush();
            //throw new RuntimeException(e);
            return null;
        }
        return complaint;
    }
    
    static int getInt(String value) throws NumberFormatException {
        int rtn = -1;
        if (value != null && value.length() > 0) {
            try {
                rtn = Integer.parseInt(value);
            } catch(NumberFormatException nfe) {

            }
        }
        if(rtn == -1) throw new NumberFormatException();
        return rtn;
    }
}