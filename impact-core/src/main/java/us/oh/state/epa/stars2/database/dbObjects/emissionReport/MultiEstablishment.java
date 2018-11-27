package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class MultiEstablishment extends BaseDB {
    private String facilityId;
    private String federalSCSCId;

    public MultiEstablishment() {
        super();
    }
    
    public MultiEstablishment(String facilityId, String federalSCCId) {
        super();
        setFacilityId(facilityId);
        setFederalSCSCId(federalSCSCId);
    }

    public final void populate(ResultSet rs) {
        try {
            setFacilityId(rs.getString("facility_id"));
            setFederalSCSCId(rs.getString("federal_scsc_id"));
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getFederalSCSCId() {
        return federalSCSCId;
    }

    public void setFederalSCSCId(String federalSCSCId) {
        this.federalSCSCId = federalSCSCId;
    }
}


