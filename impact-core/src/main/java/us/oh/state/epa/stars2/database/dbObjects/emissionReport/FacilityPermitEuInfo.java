package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.sql.Timestamp;


public class FacilityPermitEuInfo {
    private Integer euCorrId;
    private Timestamp revocationDt;
    private Timestamp terminatedDt;
    private Timestamp  supersededDt;


    public FacilityPermitEuInfo() {
        super();
    }

    public Integer getEuCorrId() {
        return euCorrId;
    }

    public void setEuCorrId(Integer euCorrId) {
        this.euCorrId = euCorrId;
    }

    public Timestamp getRevocationDt() {
        return revocationDt;
    }

    public void setRevocationDt(Timestamp revocationDt) {
        this.revocationDt = revocationDt;
    }

    public Timestamp getSupersededDt() {
        return supersededDt;
    }

    public void setSupersededDt(Timestamp supersededDt) {
        this.supersededDt = supersededDt;
    }

    public Timestamp getTerminatedDt() {
        return terminatedDt;
    }

    public void setTerminatedDt(Timestamp terminatedDt) {
        this.terminatedDt = terminatedDt;
    }
}


