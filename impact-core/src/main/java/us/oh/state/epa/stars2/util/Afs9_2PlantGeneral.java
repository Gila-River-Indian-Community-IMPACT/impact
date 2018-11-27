package us.oh.state.epa.stars2.util;

import java.io.PrintStream;

public class Afs9_2PlantGeneral extends Afs9_Base{
    String scscId;   // length 10
    String airProgramCd;  // length 1
//                0 = SIP (assumed to apply to all facilities)
//                6 = PSD
//                7 = NSR
//                8 = NESHAP
//                9 = NSPS
//                F = FESOP
//                M = MACT
//                V = Title V
    String airProgramOpStat;  // length 1:  "O"=operating; "X"=permanantly Closed
    String[] cfrSubparts1 = new String[12];  // Array length 12; String length 5
    
    public Afs9_2PlantGeneral(String scscId, String airProgramCd, String opCode, String[] subparts, String ident) 
     throws Exception {
        this.scscId = insertValueExact(blanks(10), scscId, ident);
        int i = 0;
        this.airProgramCd = airProgramCd;
        airProgramOpStat = insertValueExact(" ", opCode, ident);
        for(String s : subparts) {
            this.cfrSubparts1[i] = s;
            i++;
            if(i >= 12) break;  // only first 12 subparts.
        }

        for(; i < 12; i++) {
            cfrSubparts1[i] = Afs9_Base.blanks(5);
        }
    }
    
    String getRecord() {
        return scscId + airProgramCd + airProgramOpStat + cfrSubparts1[0] + cfrSubparts1[1] + cfrSubparts1[2] + 
        cfrSubparts1[3] + cfrSubparts1[4] + cfrSubparts1[5] + cfrSubparts1[6] + cfrSubparts1[7] + 
        cfrSubparts1[8] + cfrSubparts1[9] + cfrSubparts1[10] + cfrSubparts1[11];
    }
    
    public void writeRecord(PrintStream file) {
        file.println(getRecord());
    }

    public String getAirProgramCd() {
        return airProgramCd;
    }

    public String getAirProgramOpStat() {
        return airProgramOpStat;
    }

    public String[] getCfrSubparts1() {
        return cfrSubparts1;
    }
    
    public String getAllCfrSubparts1() {
        StringBuffer parts = new StringBuffer("");
        if(cfrSubparts1 != null) {
            for(String s : cfrSubparts1) {
                parts.append(s);
            }
        }
        return parts.toString().replace(' ', '.');
    }

    public String getScscId() {
        return scscId;
    }
}
