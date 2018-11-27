package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.BaseDef;

public class SimpleIdDef extends BaseDB implements BaseDef {

    private Integer id;
    private String description;
    private boolean deprecated;

    public SimpleIdDef(Integer id, String description) {
        super();
        this.id = id;
        this.description = description;
    }

    public SimpleIdDef() {
    }

    public SimpleIdDef(SimpleIdDef old) {
        super(old);

        if (old != null) {
            setId(old.getId());
            setDescription(old.getDescription());
            setDeprecated(old.isDeprecated());
        }
    }

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final boolean isDeprecated() {
        return deprecated;
    }

    public final void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public final void populate(ResultSet rs) {
        try {
            setId(AbstractDAO.getInteger(rs, "id"));
            setDescription(rs.getString("description"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
            setDeprecated(AbstractDAO.translateIndicatorToBoolean(rs.getString("deprecated")));
        }
        catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }

    public String getCode() {
        return id.toString();
    }

    public void setCode(String code) {
        // TODO Auto-generated method stub
        
    }
}
