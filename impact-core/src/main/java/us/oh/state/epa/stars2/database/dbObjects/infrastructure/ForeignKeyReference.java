package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;


public class ForeignKeyReference extends BaseDB{
	
	private String fkTableName;
	private String fkColumnName;
	private String fkObjectName;
	
	public ForeignKeyReference() {
	}
	
	public ForeignKeyReference(String fkTableName, String fkColumnName, String fkObjectName) {
		
		super();
		setFkTableName(fkTableName);
		setFkColumnName(fkColumnName);
		setFkObjectName(fkObjectName);
	}
	
	public ForeignKeyReference(ForeignKeyReference old) {
		super(old);
		
		if(null != old) {
			setFkTableName(old.getFkTableName());
			setFkColumnName(old.getFkColumnName());
			setFkObjectName(old.getFkObjectName());
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ForeignKeyReference other = (ForeignKeyReference) obj;
		if (fkTableName == null) {
			if (other.fkTableName != null)
				return false;
		} else if (!fkTableName.equals(other.fkTableName))
			return false;
		if (fkColumnName == null) {
			if (other.fkColumnName != null)
				return false;
		} else if (!fkColumnName.equals(other.fkColumnName))
			return false;
		if (fkObjectName == null) {
			if (other.fkObjectName != null)
				return false;
		} else if (!fkObjectName.equals(other.fkObjectName))
			return false;
		return true;
	}
	
	public final String getFkTableName() {
		return fkTableName;
	}
	
	public final void setFkTableName(String fkTableName) {
		this.fkTableName = fkTableName;
	}
	
	public final String getFkColumnName() {
		return fkColumnName;
	}
	
	public final void setFkColumnName(String fkColumnName) {
		this.fkColumnName = fkColumnName;
	}
	
	public final String getFkObjectName() {
		return fkObjectName;
	}

	public final void setFkObjectName(String fkObjectName) {
		this.fkObjectName = fkObjectName;
	}

	public final void populate(ResultSet rs) {
        try {
            setFkTableName(rs.getString("fk_table_name"));
            setFkColumnName(rs.getString("fk_column_name"));
            setFkObjectName(null); // this will be set later in the BO
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }
}
