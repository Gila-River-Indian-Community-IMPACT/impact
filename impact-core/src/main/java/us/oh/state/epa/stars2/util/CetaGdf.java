package us.oh.state.epa.stars2.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CetaGdf {
    protected static Scanner s;
    protected static int prevGdfId = -1;
    protected static boolean eof;
    
    /*
    
    Since this is all numbers and date we can use the default ";" separator. 
     
    SELECT gdfID, officeId, gdfsMonth, gdfsYear,
     stageI, stageI_II, nonstageI_II 
     FROM ceta.dbo.gdfs order by officeId, gdfId;
     
    Note that we do not read queryDate because we can regenerate it from gdfsMonth and gdfsYear.
    */
    int gdfId;
    int officeId;
    int gdfsMonth;
    int gdfsYear;
    int stageI;
    int stageI_II;
    int nonStageI_II;


    // Initialize to read the flat file.
    static boolean initialize(String fileName) throws IOException {
        s = null;
        prevGdfId = -1;
        eof = false;
        File file = new File(CetaPopulateGDFs.directory + File.separator + "migrationData" + File.separator, fileName);
        try {
            s = new Scanner(file).useDelimiter(Pattern.compile("[;]"));
        } catch (FileNotFoundException e) {
            eof = true;
            CetaPopulateGDFs.outStream.write("Failure to find file "
                    + CetaPopulateGDFs.directory + File.separator + "migrationData" + File.separator + fileName + "\n");
            CetaPopulateGDFs.outStream.close();
            return false;
        }
        return true;
    }

    // Return true if requested record read. False if record not present or at
    // eof.
    static CetaGdf next() throws IOException {
        int fieldNum = 0;
        boolean partialRow = false;;
        CetaGdf gdf = new CetaGdf();
        if (eof)
            return null;
        try {
            // Read the attributes
            partialRow = false;
            fieldNum = 0;

//          Read the attributes
            fieldNum = 0;
            gdf.gdfId = getInt(s.next().trim());
            fieldNum++;
            
            partialRow = true;
            gdf.officeId = getInt(s.next().trim());
            fieldNum++;
            
            gdf.gdfsMonth = getInt(s.next().trim());
            fieldNum++;
            
            gdf.gdfsYear = getInt(s.next().trim());
            fieldNum++;
            
            gdf.stageI = getInt(s.next().trim());
            fieldNum++;
            
            gdf.stageI_II = getInt(s.next().trim());
            fieldNum++;
            
            gdf.nonStageI_II = getInt(s.next().trim());
            fieldNum++;

            partialRow = false;
            prevGdfId = gdf.gdfId;
        } catch (NoSuchElementException e) {
            eof = true;
            String msg = "; gdf is null";
            if(gdf != null) msg = "; gdfId=" + gdf.gdfId;
            CetaPopulateGDFs.outStream
            .write("EOF or invalid data on fieldNum= " + fieldNum + msg
                    + " (previous gdfId=" + prevGdfId
                    + ")\n");
            if (partialRow) {
                CetaPopulateGDFs.outStream
                .write("ERROR: Failed to read fieldNum= " + fieldNum + msg
                    + " (previous gdfId=" + prevGdfId
                    + ")\n");
                CetaPopulateGDFs.outStream.flush();
                //throw new RuntimeException(e);
                return null;
            }
        } catch (NumberFormatException e) {
            CetaPopulateGDFs.outStream
            .write("NumberFormatException on fieldNum= " + fieldNum + "; gdfId=" + gdf.gdfId
                    + " (previous gdfId=" + prevGdfId
                    + ")\n");
            CetaPopulateGDFs.outStream.flush();
            //throw new RuntimeException(e);
            return null;
        }
        return gdf;
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