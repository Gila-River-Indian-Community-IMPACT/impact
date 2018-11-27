package us.oh.state.epa.stars2.util;

import java.io.PrintStream;

import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;

public class Afs9_1PlantGeneral extends Afs9_Base {
    String scscId;   // length 10
    String facilityName;  // length 40
    String address1;  // length 30
    //     city code: generate 5 blanks
    String city; // length 30
    String zip;  // length 9
    //     sicCd  generate 4 blanks
    String govFacCd;  // length 1
    String naicsCd;  // length 6
    String  facilityId; // length 10
    //     generate 21 blanks
    
    public Afs9_1PlantGeneral(Facility f) throws Exception {
        String ident = "facility " + f.getFacilityId();
        scscId = insertValue(blanks(10), f.getFederalSCSCId(), ident);

        facilityName = insertValue(blanks(40), f.getName(), ident);
        if(f.getPhyAddr() != null) {
            address1 = insertValue(blanks(30), f.getPhyAddr().getAddressLine1(), ident);
            city = insertValue(blanks(30), f.getPhyAddr().getCityName(), ident);
            zip = insertValueNoTruncate(blanks(9),f.getPhyAddr().getZipCode9(), ident);
        } else logger.error("No physical address for " + ident);
        String g = "";
        if(f.getGovtFacilityTypeCd() != null) g = f.getGovtFacilityTypeCd();
        govFacCd = insertValueExact(blanks(1), g, ident);
        facilityId = insertValueExact(blanks(10),f.getFacilityId(), ident);
        String n = "";
        if(f.getNaicsCds() != null && f.getNaicsCds().size() > 0) {
            n = f.getNaicsCds().get(0);
        }
        naicsCd = insertValueNoTruncate(blanks(6), n, ident);
    }
    
    String getRecord() {
        return scscId + facilityName + address1 + blanks(5) + city + zip + blanks(4) + govFacCd + naicsCd +
          facilityId + blanks(21);
    }
    
    public void writeRecord(PrintStream file) {
        file.println(getRecord());
    }

    public String getAddress1() {
        return address1;
    }

    public String getCity() {
        return city;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getGovFacCd() {
        return govFacCd;
    }

    public String getNaicsCd() {
        return naicsCd;
    }

    public String getScscId() {
        return scscId;
    }

    public String getZip() {
        return zip;
    }
}
