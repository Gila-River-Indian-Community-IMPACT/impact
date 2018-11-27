package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class TVAltScenarioPteReq extends BaseDB {
    private Integer tvAltScenarioPteReqId;
    private Integer applicationEuId;
    private Integer tvEuOperatingScenarioId;
    private String pollutantCd;
    private Float allowable;
    private String emissionUnitsCd;
    private String applicableReq;
    
    public TVAltScenarioPteReq() {
        super();
    }
    
    public TVAltScenarioPteReq(TVAltScenarioPteReq old) {
        super(old);
        if (old != null) {
            this.tvAltScenarioPteReqId = old.tvAltScenarioPteReqId;
            this.applicationEuId = old.applicationEuId;
            this.tvEuOperatingScenarioId = old.tvEuOperatingScenarioId;
            this.pollutantCd = old.pollutantCd;
            this.allowable = old.allowable;
            this.emissionUnitsCd = old.emissionUnitsCd;
            this.applicableReq = old.applicableReq;
        }
    }
    
    public void populate(ResultSet rs) throws SQLException {
        setTvAltScenarioPteReqId(AbstractDAO.getInteger(rs, "tv_alt_scenario_pte_req_id"));
        setApplicationEuId(AbstractDAO.getInteger(rs, "application_eu_id"));
        setTvEuOperatingScenarioId(AbstractDAO.getInteger(rs, "tv_eu_operating_scenario_id"));
        setPollutantCd(rs.getString("pollutant_cd"));
        setAllowable(AbstractDAO.getFloat(rs, "allowable"));
        setEmissionUnitsCd(rs.getString("emission_units_cd"));
        setApplicableReq(rs.getString("applicable_req"));
        setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
    }

    public final Integer getTvAltScenarioPteReqId() {
        return tvAltScenarioPteReqId;
    }

    public final void setTvAltScenarioPteReqId(Integer tvAltScenarioPteReqId) {
        this.tvAltScenarioPteReqId = tvAltScenarioPteReqId;
    }

    public final Float getAllowable() {
        return allowable;
    }

    public final void setAllowable(Float allowable) {
        this.allowable = allowable;
    }

    public final String getApplicableReq() {
        return applicableReq;
    }

    public final void setApplicableReq(String applicableReq) {
        this.applicableReq = applicableReq;
    }

    public final Integer getApplicationEuId() {
        return applicationEuId;
    }

    public final void setApplicationEuId(Integer applicationEuId) {
        this.applicationEuId = applicationEuId;
    }

    public final String getEmissionUnitsCd() {
        return emissionUnitsCd;
    }

    public final void setEmissionUnitsCd(String emissionUnitsCd) {
        this.emissionUnitsCd = emissionUnitsCd;
    }

    public final String getPollutantCd() {
        return pollutantCd;
    }

    public final void setPollutantCd(String pollutantDef) {
        this.pollutantCd = pollutantDef;
    }

    public final Integer getTvEuOperatingScenarioId() {
        return tvEuOperatingScenarioId;
    }

    public final void setTvEuOperatingScenarioId(Integer tvEuOperatingScenarioId) {
        this.tvEuOperatingScenarioId = tvEuOperatingScenarioId;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((allowable == null) ? 0 : allowable.hashCode());
        result = PRIME * result + ((applicableReq == null) ? 0 : applicableReq.hashCode());
        result = PRIME * result + ((applicationEuId == null) ? 0 : applicationEuId.hashCode());
        result = PRIME * result + ((emissionUnitsCd == null) ? 0 : emissionUnitsCd.hashCode());
        result = PRIME * result + ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
        result = PRIME * result + ((tvAltScenarioPteReqId == null) ? 0 : tvAltScenarioPteReqId.hashCode());
        result = PRIME * result + ((tvEuOperatingScenarioId == null) ? 0 : tvEuOperatingScenarioId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TVAltScenarioPteReq other = (TVAltScenarioPteReq) obj;
        if (allowable == null) {
            if (other.allowable != null)
                return false;
        } else if (!allowable.equals(other.allowable))
            return false;
        if (applicableReq == null) {
            if (other.applicableReq != null)
                return false;
        } else if (!applicableReq.equals(other.applicableReq))
            return false;
        if (applicationEuId == null) {
            if (other.applicationEuId != null)
                return false;
        } else if (!applicationEuId.equals(other.applicationEuId))
            return false;
        if (emissionUnitsCd == null) {
            if (other.emissionUnitsCd != null)
                return false;
        } else if (!emissionUnitsCd.equals(other.emissionUnitsCd))
            return false;
        if (pollutantCd == null) {
            if (other.pollutantCd != null)
                return false;
        } else if (!pollutantCd.equals(other.pollutantCd))
            return false;
        if (tvAltScenarioPteReqId == null) {
            if (other.tvAltScenarioPteReqId != null)
                return false;
        } else if (!tvAltScenarioPteReqId.equals(other.tvAltScenarioPteReqId))
            return false;
        if (tvEuOperatingScenarioId == null) {
            if (other.tvEuOperatingScenarioId != null)
                return false;
        } else if (!tvEuOperatingScenarioId.equals(other.tvEuOperatingScenarioId))
            return false;
        return true;
    }
    
    public static List<TVAltScenarioPteReq> cloneList(List<TVAltScenarioPteReq> tList) {
        List<TVAltScenarioPteReq> clonedList = new ArrayList<TVAltScenarioPteReq>(tList.size());
        for (TVAltScenarioPteReq tr : tList) {
            clonedList.add(new TVAltScenarioPteReq(tr));
        }
        return clonedList;
    }

}
