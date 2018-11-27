package us.wy.state.deq.impact.bo;

public interface CoreService {

	public us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef[] retrieveBaseIdDB(
			java.lang.String sqlLoadString, java.lang.Class objectToRetrieve)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	

	public us.oh.state.epa.stars2.def.BaseDef[] retrieveBaseDB(
			java.lang.String sqlLoadString, java.lang.Class objectToRetrieve)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	
	

	/**
	 * Retrieves all values for a definition that has a code of type string -
	 * i.e., a SimpleIdDef
	 * 
	 * @return SimpleIdDef[] an array containing all the values
	 */
	public us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef[] retrieveSimpleIdDefs(
			java.lang.String dbTable, java.lang.String dbIdColumn,
			java.lang.String dbDescColumn, java.lang.String dbDeprecatedColumn,
			java.lang.String sortByColumn)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	
	
	

	/**
	 * Retrieves all values for a definition that has a code of type string -
	 * i.e., a SimpleDef
	 * 
	 * @return SimpleDef[] an array containing all the values
	 */
	public us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef[] retrieveSimpleDefs(
			java.lang.String dbTable, java.lang.String dbCodeColumn,
			java.lang.String dbDescColumn, java.lang.String dbDeprecatedColumn,
			java.lang.String sortByColumn)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Retrieves all values for a definition that has a code of type string -
	 * i.e., a SimpleDef which matches the where attribute value
	 * 
	 * @return SimpleDef[] an array containing all the values
	 */
	public us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef[] retrieveSimpleDefs(
			java.lang.String dbTable, java.lang.String dbCodeColumn,
			java.lang.String dbDescColumn, java.lang.String dbDeprecatedColumn,
			java.lang.String sortByColumn, java.lang.String whereColumn,
			java.lang.String whereValue)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
}
