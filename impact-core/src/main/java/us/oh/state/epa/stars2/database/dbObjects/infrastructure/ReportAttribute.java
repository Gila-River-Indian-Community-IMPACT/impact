package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportAttribute extends SimpleDef {

    private String type;
    private Object value;
    private Object defaultValue;

    public final Object getValue() {
        if (value == null 
            || (value instanceof String && ((String) value).length() == 0)) {
            return defaultValue;
        }
        return value;
    }

    public final void setValue(Object value) {
        this.value = value;
    }

    private final void setDefaultValue(Object value) {
        this.defaultValue = value;
        this.value = value;
    }

    public final String getType() {
        return type;
    }

    public final void setType(String type) {
        this.type = type;
    } 
    
    public void populate(ResultSet rs) {
        super.populate(rs);
        
        try {
            setType(rs.getString("attribute_type"));
            setDefaultValue(rs.getString("default_value"));
        } 
        catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final ReportAttribute other = (ReportAttribute) obj;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
}
