package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.def.RPCTypeDef;

@SuppressWarnings("serial")
public class RPCRequest extends Application {
    
    private String rpcTypeCd;
    private Integer permitId;

    private List<RPCRequestDocument> rpcDocuments;

    public RPCRequest() {
        super();
        setApplicationTypeCD("RPC");
    }

    public RPCRequest(RPCRequest old) {
        super(old);
        if (old != null) {
            setRpcTypeCd(old.getRpcTypeCd());
            setPermitId(old.getPermitId());
        }
    }

    @Override
    public final void populate(java.sql.ResultSet rs) {
        try {
            super.populate(rs);
            setRpcTypeCd(rs.getString("rpc_type_cd"));
            setPermitId(AbstractDAO.getInteger(rs, "prr_permit_id"));
            setLastModified(AbstractDAO.getInteger(rs, "prr_lm"));
        }
        catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }

    public final String getRpcTypeCd() {
        return rpcTypeCd;
    }

    public final void setRpcTypeCd(String typeFlag) {
        this.rpcTypeCd = typeFlag;
        this.requiredField(rpcTypeCd, "rpcTypeFlag");
    }

    public final Integer getPermitId() {
        return permitId;
    }

    public final void setPermitId(Integer permitId) {
        this.permitId = permitId;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((permitId == null) ? 0 : permitId.hashCode());
        result = PRIME * result + ((rpcTypeCd == null) ? 0 : rpcTypeCd.hashCode());
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
        final RPCRequest other = (RPCRequest) obj;
        if (permitId == null) {
            if (other.permitId != null)
                return false;
        } else if (!permitId.equals(other.permitId))
            return false;
        if (rpcTypeCd == null) {
            if (other.rpcTypeCd != null)
                return false;
        } else if (!rpcTypeCd.equals(other.rpcTypeCd))
            return false;
        return true;
    }

    public final List<RPCRequestDocument> getRpcDocuments() {
        if (rpcDocuments == null) {
            rpcDocuments = new ArrayList<RPCRequestDocument>();
        }
        return rpcDocuments;
    }

    public final void setRpcDocuments(List<RPCRequestDocument> rpcDocuments) {
        this.rpcDocuments = new ArrayList<RPCRequestDocument>();
        if (rpcDocuments != null) {
            this.rpcDocuments.addAll(rpcDocuments);
        }
    }
    
    public boolean hasAttachments() {
        boolean hasAttachments = false;
        if (getRpcDocuments().size() > 0) {
            hasAttachments = true;
        }
        return hasAttachments;
    }

    @Override
    public String getApplicationPurposeDesc() {
        StringBuffer sb = new StringBuffer();
        if (rpcTypeCd != null) {
            sb.append(RPCTypeDef.getData().getItems().getItemDesc(rpcTypeCd));
        }
        return sb.toString();
    }
    
}
