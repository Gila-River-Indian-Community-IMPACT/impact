package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.BaseDef;

public class SimpleDef extends BaseDB implements BaseDef {
	
	private static final long serialVersionUID = 1L;
	
	private String code;
    private String description;
    private boolean deprecated;
    private int CodeColumnSize;
    private int DescriptionColumnSize;
    private String table;
    private String codeColumn;
    private String descriptionColumn;

    public SimpleDef() {
    }

    public SimpleDef(SimpleDef old) {
        super(old);

        if (old != null) {
            setCode(old.getCode());
            setDescription(old.getDescription());
            setDeprecated(old.isDeprecated());
        }
    }

    public final String getCode() {
        return code;
    }

    public final void setCode(String code) {
        this.code = code;
    }

    public final String getDescription() {
        if (description == null) {
            description = "";
        }
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    // Cannot be final since derived classes overwrite this.
    public boolean isDeprecated() {
        return deprecated;
    }
    
    // Use this function to check deprecated.
    public final boolean isDeprecatedReally() {
        return deprecated;
    }

    public final void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public void populate(ResultSet rs) {

        try {
            setCode(rs.getString("code"));
            setDescription(rs.getString("description"));
            setDeprecated(AbstractDAO.translateIndicatorToBoolean(rs.getString("deprecated")));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        }
        catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
                                                                  
    }

    public int getCodeColumnSize() {
        return CodeColumnSize;
    }

    public void setCodeColumnSize(int codeColumnSize) {
        CodeColumnSize = codeColumnSize;
    }

    public int getDescriptionColumnSize() {
        return DescriptionColumnSize;
    }

    public void setDescriptionColumnSize(int descriptionColumnSize) {
        DescriptionColumnSize = descriptionColumnSize;
    }

    public String getCodeColumn() {
        return codeColumn;
    }

    public void setCodeColumn(String codeColumn) {
        this.codeColumn = codeColumn;
    }

    public String getDescriptionColumn() {
        return descriptionColumn;
    }

    public void setDescriptionColumn(String descriptionColumn) {
        this.descriptionColumn = descriptionColumn;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
