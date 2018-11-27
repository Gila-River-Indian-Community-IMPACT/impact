package us.oh.state.epa.stars2.database.dbObjects.report;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class SimplePermitId extends BaseDB {
    private Integer permitId;

    public SimplePermitId() {
    }

    public final Integer getPermitId() {
        return permitId;
    }

    public final void setPermitId(Integer permitId) {
        this.permitId = permitId;
    }

    public final void populate(ResultSet rs) {
        try {
            setPermitId(AbstractDAO.getInteger(rs, "permit_id"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }
}
