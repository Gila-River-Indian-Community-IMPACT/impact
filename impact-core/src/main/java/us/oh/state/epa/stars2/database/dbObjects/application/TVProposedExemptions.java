package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class TVProposedExemptions extends BaseDB {
    private Integer tvApplicableReqId;
    private Integer proposedExemptionsId;
    private String proposedExemptionsReq;
    private String proposedExemptions;

    public TVProposedExemptions() {
        super();
    }
    
    public TVProposedExemptions(TVProposedExemptions old) {
        super(old);
        setTvApplicableReqId(old.getTvApplicableReqId());
        setProposedExemptionsId(old.getProposedExemptionsId());
        setProposedExemptionsReq(old.getProposedExemptionsReq());
        setProposedExemptions(old.getProposedExemptions());
        
    }
    public void populate(ResultSet rs) throws SQLException {
        setTvApplicableReqId(AbstractDAO.getInteger(rs, "tv_applicable_req_id"));
        setProposedExemptionsId(AbstractDAO.getInteger(rs, "proposed_exemptions_id"));
        setProposedExemptionsReq(rs.getString("proposed_exemptions_req"));
        setProposedExemptions(rs.getString("proposed_exemptions"));
    }

    public final String getProposedExemptions() {
        return proposedExemptions;
    }

    public final void setProposedExemptions(String proposedExemptions) {
        this.proposedExemptions = proposedExemptions;
    }

    public final String getProposedExemptionsReq() {
        return proposedExemptionsReq;
    }

    public final void setProposedExemptionsReq(String proposedExemptionsReq) {
        this.proposedExemptionsReq = proposedExemptionsReq;
    }

    public final Integer getProposedExemptionsId() {
        return proposedExemptionsId;
    }

    public final void setProposedExemptionsId(Integer proposedExemptionsId) {
        this.proposedExemptionsId = proposedExemptionsId;
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
        result = PRIME * result + ((proposedExemptions == null) ? 0 : proposedExemptions.hashCode());
        result = PRIME * result + ((proposedExemptionsReq == null) ? 0 : proposedExemptionsReq.hashCode());
        result = PRIME * result + ((proposedExemptionsId == null) ? 0 : proposedExemptionsId.hashCode());
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
        final TVProposedExemptions other = (TVProposedExemptions) obj;
        if (proposedExemptions == null) {
            if (other.proposedExemptions != null)
                return false;
        } else if (!proposedExemptions.equals(other.proposedExemptions))
            return false;
        if (proposedExemptionsReq == null) {
            if (other.proposedExemptionsReq != null)
                return false;
        } else if (!proposedExemptionsReq.equals(other.proposedExemptionsReq))
            return false;
        if (proposedExemptionsId == null) {
            if (other.proposedExemptionsId != null)
                return false;
        } else if (!proposedExemptionsId.equals(other.proposedExemptionsId))
            return false;
        if (tvApplicableReqId == null) {
            if (other.tvApplicableReqId != null)
                return false;
        } else if (!tvApplicableReqId.equals(other.tvApplicableReqId))
            return false;
        return true;
    }

}
