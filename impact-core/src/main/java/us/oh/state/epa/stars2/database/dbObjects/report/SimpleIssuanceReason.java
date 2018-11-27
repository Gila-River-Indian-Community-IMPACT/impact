package us.oh.state.epa.stars2.database.dbObjects.report;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class SimpleIssuanceReason extends BaseDB {
    private Integer permitId;
    private String reasonDsc;

    public SimpleIssuanceReason() {
    }

    public final Integer getPermitId() {
        return permitId;
    }

    public final void setPermitId(Integer permitId) {
        this.permitId = permitId;
    }

    public final String getReasonDsc() {
        return reasonDsc;
    }

    public final void setReasonDsc(String reasonDsc) {
        this.reasonDsc = reasonDsc;
    }

    public final void populate(ResultSet rs) {
        try {
            setPermitId(AbstractDAO.getInteger(rs, "permit_id"));
            setReasonDsc(rs.getString("reason_dsc"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }
}
