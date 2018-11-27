package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.PollutantDef;

@SuppressWarnings("serial")
public class TVApplicationEUEmissions extends BaseDB {
    private Integer applicationEuId;
    private Integer tvEuOperatingScenarioId;
    private String pollutantCd;
    private String euEmissionTableCd;
    private String pteTonsYr;
    private String co2Equivalent;
    private String pteDeterminationBasis;
    private boolean detBasisTradeSecret;
    private String reasonDetBasisTradeSecret;
    private String applicableReq;

    /**
     * Major status is calculated and set in BO and not stored in the database.
     */
    private String majorStatus;

    public TVApplicationEUEmissions() {
        super();
        pteTonsYr = null;
        co2Equivalent = "0";
    }
    
    public TVApplicationEUEmissions(Integer applicationEuId, 
            Integer tvEuOperatingScenarioId, String pollutantCd, 
            String euEmissionTableCd) {
        super();
        this.applicationEuId = applicationEuId;
        this.tvEuOperatingScenarioId = tvEuOperatingScenarioId;
        this.pollutantCd = pollutantCd;
        this.euEmissionTableCd = euEmissionTableCd;
        pteTonsYr = null;
        co2Equivalent = "0";
    }

    public TVApplicationEUEmissions(TVApplicationEUEmissions old) {
        super(old);
        this.applicationEuId = old.applicationEuId;
        this.tvEuOperatingScenarioId = old.tvEuOperatingScenarioId;
        this.pollutantCd = old.pollutantCd;
        this.euEmissionTableCd = old.euEmissionTableCd;
        this.pteTonsYr = old.pteTonsYr;
        this.co2Equivalent = old.co2Equivalent;
        this.pteDeterminationBasis = old.pteDeterminationBasis;
        this.detBasisTradeSecret = old.detBasisTradeSecret;
        this.reasonDetBasisTradeSecret = old.reasonDetBasisTradeSecret;
        this.applicableReq = old.applicableReq;
    }

    public void populate(ResultSet rs) throws SQLException {
        try {
            setApplicationEuId(AbstractDAO.getInteger(rs, "application_eu_id"));
            setTvEuOperatingScenarioId(AbstractDAO.getInteger(rs, "tv_eu_operating_scenario_id"));
            setPollutantCd(rs.getString("pollutant_cd"));
            setEuEmissionTableCd(rs.getString("eu_emission_table_cd"));
            setPteTonsYr(rs.getString("pte_tons_yr"));
            try {
            	setCo2Equivalent(rs.getString("co2_equivalent"));
            } catch (SQLException e) {
            	logger.warn("co2_equivalent field is missing");
            }
            setPteDeterminationBasis(rs.getString("pte_determination_basis"));
            setDetBasisTradeSecret(AbstractDAO.translateIndicatorToBoolean(rs.getString("det_basis_trade_secret")));
            setReasonDetBasisTradeSecret(rs.getString("reason_det_basis_trade_secret"));
            setApplicableReq(rs.getString("applicable_req"));
        } catch (SQLException e) {
            logger.error("Required field error");
        }
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

    public final boolean isDetBasisTradeSecret() {
        return detBasisTradeSecret;
    }

    public final void setDetBasisTradeSecret(boolean detBasisTradeSecret) {
        this.detBasisTradeSecret = detBasisTradeSecret;
    }

    public final String getPollutantCd() {
        return pollutantCd;
    }

    public final void setPollutantCd(String pollutantCd) {
        this.pollutantCd = pollutantCd;
    }

    public final String getPteDeterminationBasis() {
        return pteDeterminationBasis;
    }

    public final void setPteDeterminationBasis(String pteDeterminationBasis) {
        this.pteDeterminationBasis = pteDeterminationBasis;
    }

    public final String getPteTonsYr() {
        // don't allow nulls
        if (pteTonsYr == null || pteTonsYr.trim().length() == 0) {
            pteTonsYr = "0";
        }
        return pteTonsYr;
    }

    public final void setPteTonsYr(String pteTonsYr) {
//        // don't allow nulls
//        if (pteTonsYr == null || pteTonsYr.trim().length() == 0) {
//            pteTonsYr = "0";
//        }
    	if(pteTonsYr != null && pteTonsYr.length() == 0) pteTonsYr = null;
        this.pteTonsYr = pteTonsYr; 
    }

    public final String getCo2Equivalent() {
    	// don't allow nulls
    	if (co2Equivalent == null || co2Equivalent.trim().length() == 0) {
    		co2Equivalent = "0";
    	}
		return co2Equivalent;
	}

	public final void setCo2Equivalent(String co2Equivalent) {
    	// don't allow nulls
    	if (co2Equivalent == null || co2Equivalent.trim().length() == 0) {
    		co2Equivalent = "0";
    	}
		this.co2Equivalent = co2Equivalent;
	}

	public final String getReasonDetBasisTradeSecret() {
        return reasonDetBasisTradeSecret;
    }

    public final void setReasonDetBasisTradeSecret(String reasonDetBasisTradeSecret) {
        this.reasonDetBasisTradeSecret = reasonDetBasisTradeSecret;
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
        result = PRIME * result + ((applicableReq == null) ? 0 : applicableReq.hashCode());
        result = PRIME * result + ((applicationEuId == null) ? 0 : applicationEuId.hashCode());
        result = PRIME * result + (detBasisTradeSecret ? 1231 : 1237);
        result = PRIME * result + ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
        result = PRIME * result + ((pteDeterminationBasis == null) ? 0 : pteDeterminationBasis.hashCode());
        result = PRIME * result + ((pteTonsYr == null) ? 0 : pteTonsYr.hashCode());
        result = PRIME * result + ((co2Equivalent == null) ? 0 : co2Equivalent.hashCode());
        result = PRIME * result + ((reasonDetBasisTradeSecret == null) ? 0 : reasonDetBasisTradeSecret.hashCode());
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
        final TVApplicationEUEmissions other = (TVApplicationEUEmissions) obj;
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
        if (detBasisTradeSecret != other.detBasisTradeSecret)
            return false;
        if (pollutantCd == null) {
            if (other.pollutantCd != null)
                return false;
        } else if (!pollutantCd.equals(other.pollutantCd))
            return false;
        if (pteDeterminationBasis == null) {
            if (other.pteDeterminationBasis != null)
                return false;
        } else if (!pteDeterminationBasis.equals(other.pteDeterminationBasis))
            return false;
        if (pteTonsYr == null) {
            if (other.pteTonsYr != null)
                return false;
        } else if (!pteTonsYr.equals(other.pteTonsYr))
            return false;
        if (co2Equivalent == null) {
                if (other.co2Equivalent != null)
                    return false;
            } else if (!co2Equivalent.equals(other.co2Equivalent))
                return false;
        if (reasonDetBasisTradeSecret == null) {
            if (other.reasonDetBasisTradeSecret != null)
                return false;
        } else if (!reasonDetBasisTradeSecret.equals(other.reasonDetBasisTradeSecret))
            return false;
        if (tvEuOperatingScenarioId == null) {
            if (other.tvEuOperatingScenarioId != null)
                return false;
        } else if (!tvEuOperatingScenarioId.equals(other.tvEuOperatingScenarioId))
            return false;
        return true;
    }

    public final String getEuEmissionTableCd() {
        return euEmissionTableCd;
    }

    public final void setEuEmissionTableCd(String euEmissionTableCd) {
        this.euEmissionTableCd = euEmissionTableCd;
    }

    public final String getMajorStatus() {
        return majorStatus;
    }
    
    public final void setMajorStatus(String majorStatus) {
        this.majorStatus = majorStatus;
    }

    
    /**
     * test to see if PTE is HAP total or HAP max
     * @return
     */
    public boolean isHapStat() {
        return (PollutantDef.HTOT_CD.equals(pollutantCd) || 
                PollutantDef.HMAX_CD.equals(pollutantCd));
    }
    
    public static List<TVApplicationEUEmissions> cloneList(List<TVApplicationEUEmissions> eList) {
        List<TVApplicationEUEmissions> clonedList = new ArrayList<TVApplicationEUEmissions>(eList.size());
        for (TVApplicationEUEmissions em : eList) {
            clonedList.add(new TVApplicationEUEmissions(em));
        }
        return clonedList;
    }
    
}
