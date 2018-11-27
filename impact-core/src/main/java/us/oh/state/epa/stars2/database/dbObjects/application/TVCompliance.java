package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class TVCompliance extends BaseDB {
    private Integer tvApplicableReqId;
    private Integer complianceId;
    private String complianceApproachReq;
    private String complianceApproach;
    
    public TVCompliance() {
        super();
    }
    
    public TVCompliance(TVCompliance old) {
        super(old);
        setTvApplicableReqId(old.getTvApplicableReqId());
        setComplianceId(old.getComplianceId());
        setComplianceApproachReq(old.getComplianceApproachReq());
        setComplianceApproach(old.getComplianceApproach());
    }

    public void populate(ResultSet rs) throws SQLException {
        setTvApplicableReqId(AbstractDAO.getInteger(rs, "tv_applicable_req_id"));
        setComplianceId(AbstractDAO.getInteger(rs, "compliance_id"));
        setComplianceApproachReq(rs.getString("compliance_approach_req"));
        setComplianceApproach(rs.getString("compliance_approach"));
    }
    public final String getComplianceApproach() {
        return complianceApproach;
    }

    public final void setComplianceApproach(String complianceApproach) {
        this.complianceApproach = complianceApproach;
    }

    public final String getComplianceApproachReq() {
        return complianceApproachReq;
    }

    public final void setComplianceApproachReq(String complianceApproachReq) {
        this.complianceApproachReq = complianceApproachReq;
    }

    public final Integer getComplianceId() {
        return complianceId;
    }

    public final void setComplianceId(Integer complianceId) {
        this.complianceId = complianceId;
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
        result = PRIME * result + ((complianceApproach == null) ? 0 : complianceApproach.hashCode());
        result = PRIME * result + ((complianceApproachReq == null) ? 0 : complianceApproachReq.hashCode());
        result = PRIME * result + ((complianceId == null) ? 0 : complianceId.hashCode());
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
        final TVCompliance other = (TVCompliance) obj;
        if (complianceApproach == null) {
            if (other.complianceApproach != null)
                return false;
        } else if (!complianceApproach.equals(other.complianceApproach))
            return false;
        if (complianceApproachReq == null) {
            if (other.complianceApproachReq != null)
                return false;
        } else if (!complianceApproachReq.equals(other.complianceApproachReq))
            return false;
        if (complianceId == null) {
            if (other.complianceId != null)
                return false;
        } else if (!complianceId.equals(other.complianceId))
            return false;
        if (tvApplicableReqId == null) {
            if (other.tvApplicableReqId != null)
                return false;
        } else if (!tvApplicableReqId.equals(other.tvApplicableReqId))
            return false;
        return true;
    }

}
