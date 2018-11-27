package us.oh.state.epa.stars2.util;

import java.io.PrintStream;

import us.oh.state.epa.stars2.database.dbObjects.ceta.CetaBaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;

public class AfsFceSched extends Afs9_Base {
    Integer fceId;
    String scscId;
    String facilityId;
    String facilityName;
    String year;
    
    
    public AfsFceSched(Facility f, FullComplianceEval fce) throws Exception {
        fceId = fce.getId();
        facilityId = f.getFacilityId();
        scscId = f.getFederalSCSCId();
        facilityName = "\"" + f.getName().replaceAll("\"", "\"\"") + "\"";
        year = CetaBaseDB.getScheduledFFY(fce.getScheduledTimestamp());
    }
    
    String getRecord() {
        return scscId + "," + facilityId + "," + fceId + "," +  year + "," + facilityName;
    }
    
    public void writeRecord(PrintStream file) {
        file.println(getRecord());
    }

    public String getFacilityId() {
        return facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getScscId() {
        return scscId;
    }

    public String getYear() {
        return year;
    }

    public Integer getFceId() {
        return fceId;
    }

}
