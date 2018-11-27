package us.oh.state.epa.stars2.framework.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetHelper {
	
	
	/**
	 * Checking a column is whether exists in the ResultSet or not 
	 * @param rs
	 * @param data column name
	 * @return The result specifics the column whether exists in the ResultSet
	 */
	static public final boolean hasDataColumn(ResultSet rs, String columnName) {
		boolean hasColumn = false;
		ResultSetMetaData rsmd = null;

		try {
			rsmd = rs.getMetaData();
		} catch (SQLException sqle) {
			return false;
		}

		int columns = 0;

		try {
			columns = rsmd.getColumnCount();
		} catch (SQLException sqle) {
			return false;
		}

		for (int x = 1; x <= columns; x++) {
			try {
				if (columnName.equals(rsmd.getColumnName(x))) {
					hasColumn = true;
					break;
				}
			} catch (SQLException e) {
				return false;
			}
		}

		return hasColumn;
	}
	
}
