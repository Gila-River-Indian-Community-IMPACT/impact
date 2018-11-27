package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PermitClassDef;

@SuppressWarnings("serial")
public class FacilityEmissionUnit extends BaseDB {
    private Integer fpId;
    private String facilityId;
    private String facilityName;
    private String facilityOpStatusCd;
    private String doLaaCd;
    private String facilityPermitClassCd;
    private String epaEmuId;
    private String euOpStatusCd;
    
    private OperatingStatusDef operatingStatusDef;
    
    private PermitClassDef permitClassDef;

    public FacilityEmissionUnit(Integer fpId, String facilityId, String facilityName, String facilityOpStatusCd, String doLaaCd, String facilityPermitClassCd, String epaEmuId, String euOpStatusCd) {
        super();
        this.fpId = fpId;
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.facilityOpStatusCd = facilityOpStatusCd;
        this.doLaaCd = doLaaCd;
        this.facilityPermitClassCd = facilityPermitClassCd;
        this.epaEmuId = epaEmuId;
        this.euOpStatusCd = euOpStatusCd;
    }
    
    public FacilityEmissionUnit() {}

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            setFpId(AbstractDAO.getInteger(rs, "fp_id"));
            setFacilityId(rs.getString("facility_id"));
            setFacilityName(rs.getString("facility_name"));
            setFacilityOpStatus(rs.getString("facility_operating_status_cd"));
            setDoLaaCd(rs.getString("do_laa_cd"));
            setFacilityPermitClassCd(rs.getString("permit_classification_cd"));
            setEpaEmuId(rs.getString("epa_emu_id"));
            setEuOpStatusCd(rs.getString("eu_operating_status_cd"));
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }

    public String getDoLaaCd() {
        return doLaaCd;
    }

    public void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    public String getEpaEmuId() {
        return epaEmuId;
    }

    public void setEpaEmuId(String epaEmuId) {
        this.epaEmuId = epaEmuId;
    }

    public String getEuOpStatusCd() {
        return euOpStatusCd;
    }

    public void setEuOpStatusCd(String euOpStatusCd) {
        this.euOpStatusCd = euOpStatusCd;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityOpStatusCd() {
        return facilityOpStatusCd;
    }

    public void setFacilityOpStatus(String facilityOpStatusCd) {
        this.facilityOpStatusCd = facilityOpStatusCd;
    }

    public String getFacilityPermitClassCd() {
        return facilityPermitClassCd;
    }

    public void setFacilityPermitClassCd(String facilityPermitClassCd) {
        this.facilityPermitClassCd = facilityPermitClassCd;
    }

    public Integer getFpId() {
        return fpId;
    }

    public void setFpId(Integer fpId) {
        this.fpId = fpId;
    }
    public String getEuOpStatus() {
        String ret = "";
        ret = EuOperatingStatusDef.getData().getItems().getItemDesc(euOpStatusCd);
        return ret;
    }
    
    public String getFacilityOpStatus() {
        String ret = "";
        ret = operatingStatusDef.getData().getItems().getItemDesc(facilityOpStatusCd);
        return ret;
    }
    
    public String getFacilityPermitClass() {
        String ret = "";
        ret = permitClassDef.getData().getItems().getItemDesc(facilityPermitClassCd);
        return ret;
    }
}
