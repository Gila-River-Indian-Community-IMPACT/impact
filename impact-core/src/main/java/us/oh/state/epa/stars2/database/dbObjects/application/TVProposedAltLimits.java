package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class TVProposedAltLimits extends BaseDB {
    private Integer tvApplicableReqId;
    private Integer proposedAltLimitsId;
    private String proposedAltLimitsReq;
    private String proposedAltLimits;
    
    public TVProposedAltLimits() {
        super();
    }

    public TVProposedAltLimits(TVProposedAltLimits old) {
        super(old);
        setTvApplicableReqId(old.getTvApplicableReqId());
        setProposedAltLimitsId(old.getProposedAltLimitsId());
        setProposedAltLimitsReq(old.getProposedAltLimitsReq());
        setProposedAltLimits(old.getProposedAltLimits());
    }
    
    public void populate(ResultSet rs) throws SQLException {
        setTvApplicableReqId(AbstractDAO.getInteger(rs, "tv_applicable_req_id"));
        setProposedAltLimitsId(AbstractDAO.getInteger(rs, "proposed_alt_limits_id"));
        setProposedAltLimitsReq(rs.getString("proposed_alt_limits_req"));
        setProposedAltLimits(rs.getString("proposed_alt_limits"));
    }

    public final String getProposedAltLimits() {
        return proposedAltLimits;
    }

    public final void setProposedAltLimits(String proposedAltLimits) {
        this.proposedAltLimits = proposedAltLimits;
    }

    public final String getProposedAltLimitsReq() {
        return proposedAltLimitsReq;
    }

    public final void setProposedAltLimitsReq(String proposedAltLimitsReq) {
        this.proposedAltLimitsReq = proposedAltLimitsReq;
    }

    public final Integer getProposedAltLimitsId() {
        return proposedAltLimitsId;
    }

    public final void setProposedAltLimitsId(Integer proposedAltLimitsId) {
        this.proposedAltLimitsId = proposedAltLimitsId;
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
        result = PRIME * result + ((proposedAltLimits == null) ? 0 : proposedAltLimits.hashCode());
        result = PRIME * result + ((proposedAltLimitsReq == null) ? 0 : proposedAltLimitsReq.hashCode());
        result = PRIME * result + ((proposedAltLimitsId == null) ? 0 : proposedAltLimitsId.hashCode());
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
        final TVProposedAltLimits other = (TVProposedAltLimits) obj;
        if (proposedAltLimits == null) {
            if (other.proposedAltLimits != null)
                return false;
        } else if (!proposedAltLimits.equals(other.proposedAltLimits))
            return false;
        if (proposedAltLimitsReq == null) {
            if (other.proposedAltLimitsReq != null)
                return false;
        } else if (!proposedAltLimitsReq.equals(other.proposedAltLimitsReq))
            return false;
        if (proposedAltLimitsId == null) {
            if (other.proposedAltLimitsId != null)
                return false;
        } else if (!proposedAltLimitsId.equals(other.proposedAltLimitsId))
            return false;
        if (tvApplicableReqId == null) {
            if (other.tvApplicableReqId != null)
                return false;
        } else if (!tvApplicableReqId.equals(other.tvApplicableReqId))
            return false;
        return true;
    }

}
