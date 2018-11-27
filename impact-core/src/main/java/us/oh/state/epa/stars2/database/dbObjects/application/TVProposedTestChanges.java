package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class TVProposedTestChanges extends BaseDB {
    private Integer tvApplicableReqId;
    private Integer proposedTestChangesId;
    private String proposedTestChangesReq;
    private String proposedTestChanges;
    
    public TVProposedTestChanges() {
        super();
    }

    public TVProposedTestChanges(TVProposedTestChanges old) {
        super(old);
        setTvApplicableReqId(old.getTvApplicableReqId());
        setProposedTestChangesId(old.getProposedTestChangesId());
        setProposedTestChangesReq(old.getProposedTestChangesReq());
        setProposedTestChanges(old.getProposedTestChanges());
    }
    
    public void populate(ResultSet rs) throws SQLException {
        setTvApplicableReqId(AbstractDAO.getInteger(rs, "tv_applicable_req_id"));
        setProposedTestChangesId(AbstractDAO.getInteger(rs, "proposed_test_changes_id"));
        setProposedTestChangesReq(rs.getString("proposed_test_changes_req"));
        setProposedTestChanges(rs.getString("proposed_test_changes"));
    }

    public final String getProposedTestChanges() {
        return proposedTestChanges;
    }

    public final void setProposedTestChanges(String proposedTestChanges) {
        this.proposedTestChanges = proposedTestChanges;
    }

    public final String getProposedTestChangesReq() {
        return proposedTestChangesReq;
    }

    public final void setProposedTestChangesReq(String proposedTestChangesReq) {
        this.proposedTestChangesReq = proposedTestChangesReq;
    }

    public final Integer getProposedTestChangesId() {
        return proposedTestChangesId;
    }

    public final void setProposedTestChangesId(Integer proposedTestChangesId) {
        this.proposedTestChangesId = proposedTestChangesId;
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
        result = PRIME * result + ((proposedTestChanges == null) ? 0 : proposedTestChanges.hashCode());
        result = PRIME * result + ((proposedTestChangesReq == null) ? 0 : proposedTestChangesReq.hashCode());
        result = PRIME * result + ((proposedTestChangesId == null) ? 0 : proposedTestChangesId.hashCode());
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
        final TVProposedTestChanges other = (TVProposedTestChanges) obj;
        if (proposedTestChanges == null) {
            if (other.proposedTestChanges != null)
                return false;
        } else if (!proposedTestChanges.equals(other.proposedTestChanges))
            return false;
        if (proposedTestChangesReq == null) {
            if (other.proposedTestChangesReq != null)
                return false;
        } else if (!proposedTestChangesReq.equals(other.proposedTestChangesReq))
            return false;
        if (proposedTestChangesId == null) {
            if (other.proposedTestChangesId != null)
                return false;
        } else if (!proposedTestChangesId.equals(other.proposedTestChangesId))
            return false;
        if (tvApplicableReqId == null) {
            if (other.tvApplicableReqId != null)
                return false;
        } else if (!tvApplicableReqId.equals(other.tvApplicableReqId))
            return false;
        return true;
    }

}
