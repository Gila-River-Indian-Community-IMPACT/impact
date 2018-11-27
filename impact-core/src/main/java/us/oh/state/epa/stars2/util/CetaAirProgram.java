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

public class CetaAirProgram {
    protected static Scanner s;
    protected static String prevFacilityId = "noValue";
    protected static boolean eof;
    
    /*
    
    Since this is all flags or facility id we can use the default ";" separator. 
     
    */
    String facility_id;
    String chkTV;
    String chkTV_N;
    String chkTV_Y;
    String chkTV_OS;
    String chksm;
    String chksm_Y;
    String chksm_N;
    String chksm_OS;
    String fChkHPV;
    String fChkTV;
    String fChksm;
    String fChkMega;
    String fChknonHPV;
    String fGov_code;
    String fMemo;
    String chksip;
    String chksip_Y;
    String chksip_N;
    String chksip_OS;
    String chkmact;
    String chkmact_Y;
    String chkmact_N;
    String chkmact_OS;
    String chkneshap;
    String chkneshap_As;  // As -> N
    String chkneshap_As_Y;
    String chkneshap_As_N;
    String chkneshap_As_OS;
    String chkneshap_AB; // AB -> M
    String chkneshap_AB_Y;
    String chkneshap_AB_N;
    String chkneshap_AB_OS;
    String chkneshap_Be;  // Be -> C
    String chkneshap_Be_Y;
    String chkneshap_Be_N;
    String chkneshap_Be_OS;
    String chkneshap_BZ;  // BZ -> FF
    String chkneshap_BZ_Y;
    String chkneshap_BZ_N;
    String chkneshap_BZ_OS;
    String chkneshap_CE;  // CE -> L
    String chkneshap_CE_Y;
    String chkneshap_CE_N;
    String chkneshap_CE_OS;
    String chkneshap_Hg;  // Hg -> E
    String chkneshap_Hg_Y;
    String chkneshap_Hg_N;
    String chkneshap_Hg_OS;
    String chkneshap_RD; //RD ->H
    String chkneshap_RD_Y;
    String chkneshap_RD_N;
    String chkneshap_RD_OS;
    String chkneshap_VC;  // VC -> F
    String chkneshap_VC_Y;
    String chkneshap_VC_N;
    String chkneshap_VC_OS;
    String chkNsps;   // these pollutants map
    String chkNsps_SO2;
    String chkNsps_SO2_Y;
    String chkNsps_SO2_N;
    String chkNsps_SO2_OS;
    String chkNsps_NOx;
    String chkNsps_NOx_Y;
    String chkNsps_NOx_N;
    String chkNsps_NOx_OS;
    String chkNsps_CO;
    String chkNsps_CO_Y;
    String chkNsps_CO_N;
    String chkNsps_CO_OS;
    String chkNsps_Pb;
    String chkNsps_Pb_Y;
    String chkNsps_Pb_N;
    String chkNsps_Pb_OS;
    String chkNsps_Pt;
    String chkNsps_Pt_Y;
    String chkNsps_Pt_N;
    String chkNsps_Pt_OS;
    String chkNsps_VOC;
    String chkNsps_VOC_Y;
    String chkNsps_VOC_N;
    String chkNsps_VOC_OS;
    String chkPsd;  // these pollutants map
    String chkPsd_SO2;
    String chkPsd_SO2_Y;
    String chkPsd_SO2_N;
    String chkPsd_SO2_OS;
    String chkPsd_NOx;
    String chkPsd_NOx_Y;
    String chkPsd_NOx_N;
    String chkPsd_NOx_OS;
    String chkPsd_CO;
    String chkPsd_CO_Y;
    String chkPsd_CO_N;
    String chkPsd_CO_OS;
    String chkPsd_Pb;
    String chkPsd_Pb_Y;
    String chkPsd_Pb_N;
    String chkPsd_Pb_OS;
    String chkPsd_Pt;
    String chkPsd_Pt_Y;
    String chkPsd_Pt_N;
    String chkPsd_Pt_OS;
    String chkPsd_VOC;
    String chkPsd_VOC_Y;
    String chkPsd_VOC_N;
    String chkPsd_VOC_OS;
    String chkNonatt;  // these pollutants map
    String chkNonatt_SO2;
    String chkNonatt_SO2_Y;
    String chkNonatt_SO2_N;
    String chkNonatt_SO2_OS;
    String chkNonatt_NOx;
    String chkNonatt_NOx_Y;
    String chkNonatt_NOx_N;
    String chkNonatt_NOx_OS;
    String chkNonatt_CO;
    String chkNonatt_CO_Y;
    String chkNonatt_CO_N;
    String chkNonatt_CO_OS;
    String chkNonatt_Pb;
    String chkNonatt_Pb_Y;
    String chkNonatt_Pb_N;
    String chkNonatt_Pb_OS;
    String chkNonatt_Pt;
    String chkNonatt_Pt_Y;
    String chkNonatt_Pt_N;
    String chkNonatt_Pt_OS;
    String chkNonatt_VOC;
    String chkNonatt_VOC_Y;
    String chkNonatt_VOC_N;
    String chkNonatt_VOC_OS;
    String mact_subpart1;
    String mact_subpart2;
    String mact_subpart3;
    String mact_subpart4;
    String mact_subpart5;
    String mact_subpart6;
    String mact_subpart7;
    String mact_subpart8;
    String mact_subpart9;
    String nsps_subpart1;
    String nsps_subpart2;
    String nsps_subpart3;
    String nsps_subpart4;
    String nsps_subpart5;
    String nsps_subpart6;
    String nsps_subpart7;
    String nsps_subpart8;
    String nsps_subpart9;
    String chkNsps_subpart1;
    String chkNsps_subpart2;
    String chkNsps_subpart3;
    String chkNsps_subpart4;
    String chkNsps_subpart5;
    String chkNsps_subpart6;
    String chkNsps_subpart7;
    String chkNsps_subpart8;
    String chkNsps_subpart9;
    String chkMact_subpart1;
    String chkMact_subpart2;
    String chkMact_subpart3;
    String chkMact_subpart4;
    String chkMact_subpart5;
    String chkMact_subpart6;
    String chkMact_subpart7;
    String chkMact_subpart8;
    String chkMact_subpart9;
    
    /*

    Do this in eclipse,  then replace "|eEnNdD;"  with "|" 

    select ' ',
     rtrim(a.facility_id),  '|',
     rtrim(a.chkTV),  '|',
     rtrim(a.chkTV_N),  '|',
     rtrim(a.chkTV_Y),  '|',
     rtrim(a.chkTV_OS), '|',  
     rtrim(a.chksm), '|',  
     rtrim(a.chksm_Y), '|',  
     rtrim(a.chksm_N), '|',  
     rtrim(a.chksm_OS), '|',
     rtrim(fi.chkHPV), '|',
     rtrim(fi.chkTV), '|',
     rtrim(fi.chksm), '|',
     rtrim(fi.chkMega), '|',
     rtrim(fi.chknonHPV), '|',
     rtrim(fi.gov_code), '|',
     rtrim(fi.memo), '|',
     rtrim(a.chksip), '|',  
     rtrim(a.chksip_Y), '|',  
     rtrim(a.chksip_N), '|',  
     rtrim(a.chksip_OS), '|',  
     rtrim(a.chkmact), '|',  
     rtrim(a.chkmact_Y), '|',  
     rtrim(a.chkmact_N), '|',  
     rtrim(a.chkmact_OS), '|',  
     rtrim(a.chkneshap), '|',  
     rtrim(a.chkneshap_As), '|',  
     rtrim(a.chkneshap_As_Y), '|',  
     rtrim(a.chkneshap_As_N), '|',  
     rtrim(a.chkneshap_As_OS), '|',  
     rtrim(a.chkneshap_AB), '|',  
     rtrim(a.chkneshap_AB_Y), '|',  
     rtrim(a.chkneshap_AB_N), '|',  
     rtrim(a.chkneshap_AB_OS), '|',  
     rtrim(a.chkneshap_Be), '|', 
     rtrim(a.chkneshap_Be_Y), '|',  
     rtrim(a.chkneshap_Be_N), '|',  
     rtrim(a.chkneshap_Be_OS), '|',  
     rtrim(a.chkneshap_BZ), '|',  
     rtrim(a.chkneshap_BZ_Y), '|',  
     rtrim(a.chkneshap_BZ_N), '|',  
     rtrim(a.chkneshap_BZ_OS), '|',  
     rtrim(a.chkneshap_CE), '|',  
     rtrim(a.chkneshap_CE_Y), '|',  
     rtrim(a.chkneshap_CE_N), '|',  
     rtrim(a.chkneshap_CE_OS), '|',  
     rtrim(a.chkneshap_Hg), '|',  
     rtrim(a.chkneshap_Hg_Y), '|',  
     rtrim(a.chkneshap_Hg_N), '|',  
     rtrim(a.chkneshap_Hg_OS), '|',  
     rtrim(a.chkneshap_RD), '|',  
     rtrim(a.chkneshap_RD_Y), '|',  
     rtrim(a.chkneshap_RD_N), '|',  
     rtrim(a.chkneshap_RD_OS), '|',  
     rtrim(a.chkneshap_VC), '|',  
     rtrim(a.chkneshap_VC_Y), '|',  
     rtrim(a.chkneshap_VC_N), '|',  
     rtrim(a.chkneshap_VC_OS), '|',  
     rtrim(a.chkNsps), '|',  
     rtrim(a.chkNsps_SO2), '|',  
     rtrim(a.chkNsps_SO2_Y), '|',  
     rtrim(a.chkNsps_SO2_N), '|',  
     rtrim(a.chkNsps_SO2_OS), '|',  
     rtrim(a.chkNsps_NOx), '|',  
     rtrim(a.chkNsps_NOx_Y), '|',  
     rtrim(a.chkNsps_NOx_N), '|',  
     rtrim(a.chkNsps_NOx_OS), '|',  
     rtrim(a.chkNsps_CO), '|',  
     rtrim(a.chkNsps_CO_Y), '|',  
     rtrim(a.chkNsps_CO_N), '|',  
     rtrim(a.chkNsps_CO_OS), '|',  
     rtrim(a.chkNsps_Pb), '|',  
     rtrim(a.chkNsps_Pb_Y), '|',  
     rtrim(a.chkNsps_Pb_N), '|',  
     rtrim(a.chkNsps_Pb_OS), '|',  
     rtrim(a.chkNsps_Pt), '|',  
     rtrim(a.chkNsps_Pt_Y), '|',  
     rtrim(a.chkNsps_Pt_N), '|',  
     rtrim(a.chkNsps_Pt_OS), '|',  
     rtrim(a.chkNsps_VOC), '|',  
     rtrim(a.chkNsps_VOC_Y), '|',  
     rtrim(a.chkNsps_VOC_N), '|',  
     rtrim(a.chkNsps_VOC_OS), '|',  
     rtrim(a.chkPsd), '|',  
     rtrim(a.chkPsd_SO2), '|',  
     rtrim(a.chkPsd_SO2_Y), '|',  
     rtrim(a.chkPsd_SO2_N), '|',  
     rtrim(a.chkPsd_SO2_OS), '|',  
     rtrim(a.chkPsd_NOx), '|',  
     rtrim(a.chkPsd_NOx_Y), '|',  
     rtrim(a.chkPsd_NOx_N), '|',  
     rtrim(a.chkPsd_NOx_OS), '|',  
     rtrim(a.chkPsd_CO), '|',  
     rtrim(a.chkPsd_CO_Y), '|',  
     rtrim(a.chkPsd_CO_N), '|',  
     rtrim(a.chkPsd_CO_OS), '|',  
     rtrim(a.chkPsd_Pb), '|',  
     rtrim(a.chkPsd_Pb_Y), '|',  
     rtrim(a.chkPsd_Pb_N), '|',  
     rtrim(a.chkPsd_Pb_OS), '|',  
     rtrim(a.chkPsd_Pt), '|',  
     rtrim(a.chkPsd_Pt_Y), '|',  
     rtrim(a.chkPsd_Pt_N), '|',  
     rtrim(a.chkPsd_Pt_OS), '|',  
     rtrim(a.chkPsd_VOC), '|',  
     rtrim(a.chkPsd_VOC_Y), '|',  
     rtrim(a.chkPsd_VOC_N), '|',  
     rtrim(a.chkPsd_VOC_OS), '|',  
     rtrim(a.chkNonatt), '|', 
     rtrim(a.chkNonattain_SO2), '|',  
     rtrim(a.chkNonattain_SO2_Y), '|',  
     rtrim(a.chkNonattain_SO2_N), '|',  
     rtrim(a.chkNonattain_SO2_OS), '|',  
     rtrim(a.chkNonattain_NOx), '|',  
     rtrim(a.chkNonattain_NOx_Y), '|',  
     rtrim(a.chkNonattain_NOx_N), '|',  
     rtrim(a.chkNonattain_NOx_OS), '|',  
     rtrim(a.chkNonattain_CO), '|',  
     rtrim(a.chkNonattain_CO_Y), '|',  
     rtrim(a.chkNonattain_CO_N), '|',  
     rtrim(a.chkNonattain_CO_OS), '|',  
     rtrim(a.chkNonattain_Pb), '|',  
     rtrim(a.chkNonattain_Pb_Y), '|',  
     rtrim(a.chkNonattain_Pb_N), '|',  
     rtrim(a.chkNonattain_Pb_OS), '|',  
     rtrim(a.chkNonattain_Pt), '|',  
     rtrim(a.chkNonattain_Pt_Y), '|',  
     rtrim(a.chkNonattain_Pt_N), '|',  
     rtrim(a.chkNonattain_Pt_OS), '|',  
     rtrim(a.chkNonattain_VOC), '|',  
     rtrim(a.chkNonattain_VOC_Y), '|',  
     rtrim(a.chkNonattain_VOC_N), '|',  
     rtrim(a.chkNonattain_VOC_OS), '|',  
     rtrim(a.mact_subpart1), '|',  
     rtrim(a.mact_subpart2), '|',  
     rtrim(a.mact_subpart3), '|',  
     rtrim(a.mact_subpart4), '|',  
     rtrim(a.mact_subpart5), '|',  
     rtrim(a.mact_subpart6), '|',  
     rtrim(a.mact_subpart7), '|',  
     rtrim(a.mact_subpart8), '|',  
     rtrim(a.mact_subpart9), '|',  
     rtrim(a.nsps_subpart1), '|',  
     rtrim(a.nsps_subpart2), '|',  
     rtrim(a.nsps_subpart3), '|',  
     rtrim(a.nsps_subpart4), '|',  
     rtrim(a.nsps_subpart5), '|',  
     rtrim(a.nsps_subpart6), '|',  
     rtrim(a.nsps_subpart7), '|',  
     rtrim(a.nsps_subpart8), '|',  
     rtrim(a.nsps_subpart9), '|',  
     rtrim(a.chkNsps_subpart1), '|',  
     rtrim(a.chkNsps_subpart2), '|',  
     rtrim(a.chkNsps_subpart3), '|',  
     rtrim(a.chkNsps_subpart4), '|',  
     rtrim(a.chkNsps_subpart5), '|',  
     rtrim(a.chkNsps_subpart6), '|',  
     rtrim(a.chkNsps_subpart7), '|',  
     rtrim(a.chkNsps_subpart8), '|',  
     rtrim(a.chkNsps_subpart9), '|',  
     rtrim(a.chkMact_subpart1), '|',  
     rtrim(a.chkMact_subpart2), '|',  
     rtrim(a.chkMact_subpart3), '|',  
     rtrim(a.chkMact_subpart4), '|',  
     rtrim(a.chkMact_subpart5), '|',  
     rtrim(a.chkMact_subpart6), '|',  
     rtrim(a.chkMact_subpart7), '|',  
     rtrim(a.chkMact_subpart8), '|',  
     rtrim(a.chkMact_subpart9), '|eEnNdD' from ceta.dbo.airprogram a, ceta.dbo.facility_info fi where a.facility_id = fi.facility_id 
     AND fi.facility_id < '0819020236'    >= '0819020236'
     order by a.facility_id;
     
     */


    // Initialize to read the flat file.
    static boolean initialize(String fileName, Logger logger) throws IOException {
        s = null;
        prevFacilityId = "noValue";
        eof = false;
        File file = new File(CetaPopulateAirProgram.directory + File.separator + "migrationData" + File.separator, fileName);
        File cleanFile = new File(CetaPopulateAirProgram.directory + File.separator + "migrationData" + File.separator, "clean_" + fileName);
        FilterCharacters.filter(CetaPopulateAirProgram.outStream, logger, file, cleanFile);
        try {
            s = new Scanner(cleanFile).useDelimiter(Pattern.compile("[|]"));
        } catch (FileNotFoundException e) {
            eof = true;
            CetaPopulateAirProgram.outStream.write("Failure to find file "
                    + CetaPopulateAirProgram.directory + File.separator + "migrationData" + File.separator + fileName + "\n");
            CetaPopulateAirProgram.outStream.close();
            return false;
        }
        return true;
    }

    // Return true if requested record read. False if record not present or at
    // eof.
    static CetaAirProgram next() throws IOException {
        int fieldNum = 0;
        boolean partialRow = false;;
        CetaAirProgram airP = new CetaAirProgram();
        if (eof)
            return null;
        try {
            // Read the attributes
            partialRow = false;
            fieldNum = 0;

//          Read the attributes
            fieldNum = 0;
            airP.facility_id = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            
            partialRow = true;
            airP.chkTV = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            airP.chkTV_N = removeMarkersAndNULL(s.next().trim());
            fieldNum++;
            airP.chkTV_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkTV_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chksm = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chksm_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chksm_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chksm_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            // no need to verify that fChkHPV is set only when TV,sm,mega set because a check of database shows that is never the case.
            airP.fChkHPV = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.fChkTV = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.fChksm = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.fChkMega = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.fChknonHPV = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.fGov_code = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.fMemo = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chksip = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chksip_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chksip_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chksip_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkmact = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkmact_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkmact_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkmact_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_As = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_As_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_As_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_As_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_AB = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_AB_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_AB_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_AB_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_Be = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_Be_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_Be_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_Be_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_BZ = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_BZ_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_BZ_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_BZ_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_CE = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_CE_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_CE_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_CE_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_Hg = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_Hg_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_Hg_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_Hg_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_RD = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_RD_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_RD_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_RD_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_VC = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_VC_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_VC_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkneshap_VC_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_SO2 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_SO2_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_SO2_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_SO2_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_NOx = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_NOx_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_NOx_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_NOx_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_CO = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_CO_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_CO_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_CO_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_Pb = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_Pb_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_Pb_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_Pb_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_Pt = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_Pt_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_Pt_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_Pt_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_VOC = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_VOC_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_VOC_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_VOC_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_SO2 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_SO2_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_SO2_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_SO2_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_NOx = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_NOx_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_NOx_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_NOx_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_CO = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_CO_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_CO_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_CO_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_Pb = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_Pb_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_Pb_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_Pb_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_Pt = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_Pt_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_Pt_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_Pt_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_VOC = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_VOC_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_VOC_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkPsd_VOC_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_SO2 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_SO2_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_SO2_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_SO2_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_NOx = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_NOx_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_NOx_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_NOx_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_CO = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_CO_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_CO_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_CO_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_Pb = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_Pb_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_Pb_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_Pb_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_Pt = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_Pt_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_Pt_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_Pt_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_VOC = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_VOC_Y = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_VOC_N = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNonatt_VOC_OS = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.mact_subpart1 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.mact_subpart2 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.mact_subpart3 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.mact_subpart4 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.mact_subpart5 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.mact_subpart6 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.mact_subpart7 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.mact_subpart8 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.mact_subpart9 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.nsps_subpart1 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.nsps_subpart2 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.nsps_subpart3 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.nsps_subpart4 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.nsps_subpart5 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.nsps_subpart6 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.nsps_subpart7 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.nsps_subpart8 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.nsps_subpart9 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_subpart1 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_subpart2 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_subpart3 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_subpart4 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_subpart5 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_subpart6 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_subpart7 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_subpart8 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkNsps_subpart9 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkMact_subpart1 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkMact_subpart2 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkMact_subpart3 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkMact_subpart4 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkMact_subpart5 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkMact_subpart6 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkMact_subpart7 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkMact_subpart8 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            airP.chkMact_subpart9 = removeMarkersAndNULL(s.next().trim()); fieldNum++;
            partialRow = false;
            prevFacilityId = airP.facility_id;
        } catch (NoSuchElementException e) {
            eof = true;
            String msg = "; airProgram is null";
            if(airP != null) msg = "; FacilityId=" + airP.facility_id;
            CetaPopulateAirProgram.outStream
            .write("EOF or invalid data on fieldNum= " + fieldNum + msg
                    + " (previous facilityId=" + prevFacilityId
                    + ")\n");
            if (partialRow) {
                CetaPopulateAirProgram.outStream
                .write("ERROR: Failed to read fieldNum= " + fieldNum + msg
                    + " (previous facilityId=" + prevFacilityId
                    + ")\n");
                CetaPopulateAirProgram.outStream.flush();
                //throw new RuntimeException(e);
                return null;
            }
        } catch (NumberFormatException e) {
            CetaPopulateAirProgram.outStream
            .write("NumberFormatException on fieldNum= " + fieldNum + "; facilityId=" + airP.facility_id
                    + " (previous facilityId=" + prevFacilityId
                    + ")\n");
            CetaPopulateAirProgram.outStream.flush();
            //throw new RuntimeException(e);
            return null;
        }
        return airP;
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
    
    static String removeMarkersAndNULL(String s) {
        String rtn = s;
        if(rtn != null && rtn.length() >= 2) {
            if(rtn.charAt(0) != ';' || rtn.charAt(rtn.length() - 1) != ';') {
                try {
                    CetaPopulateAirProgram.outStream
                .write("Missing the bracketing semicolons.  Previous facilityId=" + prevFacilityId + ", field value="
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
}