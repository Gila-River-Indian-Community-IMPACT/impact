package us.oh.state.epa.stars2.util;

import java.io.PrintStream;

public class Afs9_3Pollutant extends Afs9_Base {
    String scscId;   // length 10
    String airProgramCd;  // length 1
    String pollutantCd;  // length 5
    // The pollutant code is forced to comply with below because those are the only
    //  pollutants in the pick lists.
//   Air Program Code  Potential Pollutants(s)
//          0 = SIP       FACIL
//          M = MACT      THAP
//          V = Title V   FACIL
//          F = FESOP     FACIL
                       
//          6 = PSD       SO2
//                        NO2
//                        CO
//                        PB
//                        PT
//                        VOC
    
//           7 = NSR      SO2
//                        NO2
//                        CO
//                        PB
//                        PT
//                        VOC
                        
//           8 = NESHAP   AS
//                        AB
//                        BE
//                        BZ
//                        CE
//                        RD
//                        VC
//                        HG
    
//           9 = NSPS     SO2
//                        NO2
//                        CO
//                        PB
//                        PT
//                        VOC
    String casNum;  // length 9
    String complianceStat;  // length 1
//                1 = In Violation
//                3 = In Compliance
//                5 = On Schedule
    String classCd;  // length 2
//                A = Title V
//                SM = Synthetic Minor
//                B = Non-Title V (True Minor)
    String attainment;  // length 1  The ozone attainment status is applied to the VOC and NOX (Mike V.).
//                A = Attainment
//                N = Non-Attainment
    
    public Afs9_3Pollutant(String scscId, String airProgramCd, String pollutantCd, 
            String compliance, String attainment, String ident) throws Exception {
        this.scscId = insertValueExact(blanks(10), scscId, ident);
        this.airProgramCd = airProgramCd;
        this.pollutantCd = blanks(5);
        this.casNum = blanks(9);
        
        boolean isCas = pollutantCd.matches("^[0-9]*$");
        if(isCas) {
            this.casNum = insertValueNoTruncate(this.casNum, pollutantCd, ident);
        } else {
            this.pollutantCd = insertValueNoTruncate(this.pollutantCd, pollutantCd, ident);
        }
        
        
        this.complianceStat = compliance;
        this.classCd = insertValue(blanks(2), classCd, ident);
        this.attainment = attainment;
        
    }

    String getRecord() {
        return scscId + airProgramCd + pollutantCd + casNum + complianceStat + classCd + attainment;
    }
    
    public void writeRecord(PrintStream file) {
        file.println(getRecord());
    }

    public String getAirProgramCd() {
        return airProgramCd;
    }

    public String getAttainment() {
        return attainment;
    }

    public String getClassCd() {
        return classCd;
    }

    public String getComplianceStat() {
        return complianceStat;
    }

    public String getPollutantCd() {
        return pollutantCd;
    }

    public String getScscId() {
        return scscId;
    }

    public String getCasNum() {
        return casNum;
    }
}
