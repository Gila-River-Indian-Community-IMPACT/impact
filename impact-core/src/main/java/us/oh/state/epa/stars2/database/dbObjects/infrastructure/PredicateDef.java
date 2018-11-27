package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class PredicateDef extends BaseDB {

	public static final Object GREATER_THAN = "GT";
	public static final Object LESS_THAN = "LT";
	public static final Object EQUALS = "EQ";
	public static final Object GREATER_THAN_OR_EQUALS = "GE";
	public static final Object LESS_THAN_OR_EQUALS = "LE";
	private String predicateCd;
	private String predicateDsc;
	
	    public String getPredicateCd() {
		return predicateCd;
	}
	public void setPredicateCd(String predicateCd) {
		this.predicateCd = predicateCd;
	}
	public String getPredicateDsc() {
		return predicateDsc;
	}
	public void setPredicateDsc(String predicateDsc) {
		this.predicateDsc = predicateDsc;
	}
	public final void populate(ResultSet rs) {
        try {
        	setPredicateCd(rs.getString("predicate_cd"));
            setPredicateDsc(rs.getString("predicate_dsc"));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }
	@Override
	public String toString() {
		return "PredicateDef [predicateCd=" + predicateCd + ", predicateDsc="
				+ predicateDsc + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((predicateCd == null) ? 0 : predicateCd.hashCode());
		result = prime * result
				+ ((predicateDsc == null) ? 0 : predicateDsc.hashCode());
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
		PredicateDef other = (PredicateDef) obj;
		if (predicateCd == null) {
			if (other.predicateCd != null)
				return false;
		} else if (!predicateCd.equals(other.predicateCd))
			return false;
		if (predicateDsc == null) {
			if (other.predicateDsc != null)
				return false;
		} else if (!predicateDsc.equals(other.predicateDsc))
			return false;
		return true;
	}
	
	

}
