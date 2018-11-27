package us.oh.state.epa.stars2.database.dao.core;

import java.util.ArrayList;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.CoreDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class CoreSQLDAO extends AbstractDAO implements CoreDAO {

	//TODO decouple workflow ... remove infrastructure service duplication
	/**
	 * @see InfrastructureDAO#retrieveBaseDB(String, Class)
	 */
	public final <T extends BaseDB> BaseDef[] retrieveBaseDB(
			String sqlLoadString, Class<T> objectToRetrieve)
			throws DAOException {

		checkNull(sqlLoadString);
		checkNull(objectToRetrieve);
		ArrayList<T> ret = new ArrayList<T>();

		if (BaseDef.class.isAssignableFrom(objectToRetrieve)) {
			ConnectionHandler connHandler = new ConnectionHandler(
					sqlLoadString, true);
			ret = connHandler.retrieveArray(objectToRetrieve);
		}

		return ret.toArray(new BaseDef[0]);
	}

	//TODO decouple workflow ... remove infrastructure service duplication
	/**
	 * @see InfrastructureDAO#retrieveBaseIdDB(String, Class) Added by Dennis to
	 *      allow using SQL to read in def table with Integer for code.
	 */
	public final <T extends BaseDB> SimpleIdDef[] retrieveBaseIdDB(
			String sqlLoadString, Class<T> objectToRetrieve)
			throws DAOException {

		checkNull(sqlLoadString);
		checkNull(objectToRetrieve);
		ArrayList<T> ret = new ArrayList<T>();

		if (BaseDef.class.isAssignableFrom(objectToRetrieve)) {
			ConnectionHandler connHandler = new ConnectionHandler(
					sqlLoadString, true);
			ret = connHandler.retrieveArray(objectToRetrieve);
		}

		return ret.toArray(new SimpleIdDef[0]);
	}

	//TODO decouple workflow ... remove infrastructure service duplication
	/**
	 * @see InfrastructureDAO#retrieveSimpleIdDefs(String, String, String,
	 *      String)
	 */
	public final SimpleIdDef[] retrieveSimpleIdDefs(String dbTable,
			String dbIdColumn, String dbDescColumn, String dbDeprecatedColumn,
			String sortByColumn) throws DAOException {

		checkNull(dbTable);
		checkNull(dbIdColumn);
		checkNull(dbDescColumn);

		String sql = "SELECT " + dbIdColumn + " AS id, " + dbDescColumn
				+ " AS description, ";

		if (dbDeprecatedColumn != null) {
			sql += dbDeprecatedColumn + " AS deprecated, ";
		} else {
			sql += "'N' AS deprecated, ";
		}

		sql += " last_modified" + " FROM " + addSchemaToTable(dbTable)
				+ " ORDER BY ";

		if (sortByColumn != null) {
			sql += sortByColumn;
		} else {
			sql += dbDescColumn;
		}

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(sql);

		ArrayList<SimpleIdDef> ret = connHandler
				.retrieveArray(SimpleIdDef.class);

		return ret.toArray(new SimpleIdDef[0]);
	}

	//TODO decouple workflow ... remove infrastructure service duplication
	/**
	 * @see InfrastructureDAO#retrieveSimpleDefs(String, String, String, String)
	 */
	public final SimpleDef[] retrieveSimpleDefs(String dbTable,
			String dbCodeColumn, String dbDescColumn,
			String dbDeprecatedColumn, String sortByColumn) throws DAOException {

		checkNull(dbTable);
		checkNull(dbCodeColumn);
		checkNull(dbDescColumn);

		String sql = "SELECT " + dbCodeColumn + " AS code, " + dbDescColumn
				+ " AS description, ";

		if (dbDeprecatedColumn != null) {
			sql += dbDeprecatedColumn + " AS deprecated, ";
		} else {
			sql += "'N' AS deprecated, ";
		}

		sql += " last_modified" + " FROM " + addSchemaToTable(dbTable)
				+ " ORDER BY ";

		if (sortByColumn != null) {
			sql += sortByColumn;
		} else {
			sql += dbDescColumn;
		}

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(sql);

		ArrayList<SimpleDef> ret = connHandler.retrieveArray(SimpleDef.class);

		return ret.toArray(new SimpleDef[0]);
	}

	//TODO decouple workflow ... remove infrastructure service duplication
	/**
	 * @see InfrastructureDAO#retrieveSimpleDefs(String, String, String, String)
	 */
	public final SimpleDef[] retrieveSimpleDefs(String dbTable,
			String dbCodeColumn, String dbDescColumn,
			String dbDeprecatedColumn, String sortByColumn, String whereColumn,
			String whereValue) throws DAOException {

		checkNull(dbTable);
		checkNull(dbCodeColumn);
		checkNull(dbDescColumn);

		String sql = "SELECT " + dbCodeColumn + " AS code, " + dbDescColumn
				+ " AS description, " + whereColumn + ", ";

		if (dbDeprecatedColumn != null) {
			sql += dbDeprecatedColumn + " AS deprecated, ";
		} else {
			sql += "'N' AS deprecated, ";
		}

		sql += " last_modified" + " FROM " + addSchemaToTable(dbTable);

		sql += " WHERE " + whereColumn + " = '" + whereValue + "'"
				+ " ORDER BY ";

		if (sortByColumn != null) {
			sql += sortByColumn;
		} else {
			sql += dbDescColumn;
		}

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(sql);

		ArrayList<SimpleDef> ret = connHandler.retrieveArray(SimpleDef.class);

		return ret.toArray(new SimpleDef[0]);
	}

}
