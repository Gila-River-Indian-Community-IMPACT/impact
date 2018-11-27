package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class TVComplianceObligations extends BaseDB {
    private Integer tvApplicableReqId;
    private Integer complianceObligationsId;
    private String complianceObligationsReq;
    private String complianceObligationsLimit;
    private String complianceObligationsBasis;
    
    public TVComplianceObligations() {
        super();
    }
    
    public TVComplianceObligations(TVComplianceObligations old) {
        super(old);
        setTvApplicableReqId(old.getTvApplicableReqId());
        setComplianceObligationsId(old.getComplianceObligationsId());
        setComplianceObligationsReq(old.getComplianceObligationsReq());
        setComplianceObligationsLimit(old.getComplianceObligationsLimit());
        setComplianceObligationsBasis(old.getComplianceObligationsBasis());
    }

    public void populate(ResultSet rs) throws SQLException {
        setTvApplicableReqId(AbstractDAO.getInteger(rs, "tv_applicable_req_id"));
        setComplianceObligationsId(AbstractDAO.getInteger(rs, "compliance_obligations_id"));
        setComplianceObligationsReq(rs.getString("compliance_obligations_req"));
        setComplianceObligationsLimit(rs.getString("compliance_obligations_limit"));
        setComplianceObligationsBasis(rs.getString("compliance_obligations_basis"));
    }

    public final String getComplianceObligationsLimit() {
        return complianceObligationsLimit;
    }

    public final void setComplianceObligationsLimit(String complianceObligations) {
        this.complianceObligationsLimit = complianceObligations;
    }

    public final String getComplianceObligationsBasis() {
        return complianceObligationsBasis;
    }

    public final void setComplianceObligationsBasis(
            String complianceObligationsBasis) {
        this.complianceObligationsBasis = complianceObligationsBasis;
    }

    public final String getComplianceObligationsReq() {
        return complianceObligationsReq;
    }

    public final void setComplianceObligationsReq(String complianceObligationsReq) {
        this.complianceObligationsReq = complianceObligationsReq;
    }

    public final Integer getComplianceObligationsId() {
        return complianceObligationsId;
    }

    public final void setComplianceObligationsId(Integer complianceId) {
        this.complianceObligationsId = complianceId;
    }

    public final Integer getTvApplicableReqId() {
        return tvApplicableReqId;
    }

    public final void setTvApplicableReqId(Integer tvApplicableReqId) {
        this.tvApplicableReqId = tvApplicableReqId;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((complianceObligationsBasis == null) ? 0 : complianceObligationsBasis.hashCode());
        result = PRIME * result + ((complianceObligationsId == null) ? 0 : complianceObligationsId.hashCode());
        result = PRIME * result + ((complianceObligationsLimit == null) ? 0 : complianceObligationsLimit.hashCode());
        result = PRIME * result + ((complianceObligationsReq == null) ? 0 : complianceObligationsReq.hashCode());
        result = PRIME * result + ((tvApplicableReqId == null) ? 0 : tvApplicableReqId.hashCode());
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
        final TVComplianceObligations other = (TVComplianceObligations) obj;
        if (complianceObligationsBasis == null) {
            if (other.complianceObligationsBasis != null)
                return false;
        } else if (!complianceObligationsBasis.equals(other.complianceObligationsBasis))
            return false;
        if (complianceObligationsId == null) {
            if (other.complianceObligationsId != null)
                return false;
        } else if (!complianceObligationsId.equals(other.complianceObligationsId))
            return false;
        if (complianceObligationsLimit == null) {
            if (other.complianceObligationsLimit != null)
                return false;
        } else if (!complianceObligationsLimit.equals(other.complianceObligationsLimit))
            return false;
        if (complianceObligationsReq == null) {
            if (other.complianceObligationsReq != null)
                return false;
        } else if (!complianceObligationsReq.equals(other.complianceObligationsReq))
            return false;
        if (tvApplicableReqId == null) {
            if (other.tvApplicableReqId != null)
                return false;
        } else if (!tvApplicableReqId.equals(other.tvApplicableReqId))
            return false;
        return true;
    }

}
