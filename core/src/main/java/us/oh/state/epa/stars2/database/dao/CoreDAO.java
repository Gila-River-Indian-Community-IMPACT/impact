package us.oh.state.epa.stars2.database.dao;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface CoreDAO extends DataAccessObject {


    <T extends BaseDB>SimpleIdDef[] retrieveBaseIdDB(String sqlLoadString, Class<T> objectToRetrieve) throws DAOException;
    
    /**
     * Retrieves an array of BaseDB's that are retrieved by the supplied
     * sqlLoadString and populated using reflection and the supplied class.
     * 
     * @param sqlLoadString
     * @param objectToRetrieve
     * @return
     * @throws DAOException
     */
    <T extends BaseDB>BaseDef[] retrieveBaseDB(String sqlLoadString, Class<T> objectToRetrieve)
            throws DAOException;
    

    /**
     * Retrieves an array of SimpleIdDefs for the supplied table name, id
     * column, description column, deprecated column and sort by criteria.
     * 
     * @param dbTable
     * @param dbIdColumn
     * @param dbDescColumn
     * @param dbDeprecatedColumn
     * @return
     * @throws DAOException
     */
    SimpleIdDef[] retrieveSimpleIdDefs(String dbTable, String dbIdColumn,
            String dbDescColumn, String dbDeprecatedColumn, String sortByColumn)
            throws DAOException;
    

    /**
     * Retrieves an array of SimpleDefs for the supplied table name, id column,
     * description column, deprecated column and sort by criteria.
     * 
     * @param dbTable
     * @param dbCodeColumn
     * @param dbDescColumn
     * @param dbDeprecatedColumn
     * @return
     * @throws DAOException
     */
    SimpleDef[] retrieveSimpleDefs(String dbTable, String dbCodeColumn,
            String dbDescColumn, String dbDeprecatedColumn, String sortByColumn)
            throws DAOException;

    /**
     * Retrieves an array of SimpleIdDefs for the supplied table name, id
     * column, description column, deprecated column and sort by criteria and a
     * customized where clause.
     * 
     * @param dbTable
     * @param dbCodeColumn
     * @param dbDescColumn
     * @param dbDeprecatedColumn
     * @param sortByColumn
     * @param whereColumn
     * @param whereVal
     * @return
     * @throws DAOException
     */
    SimpleDef[] retrieveSimpleDefs(String dbTable, String dbCodeColumn,
            String dbDescColumn, String dbDeprecatedColumn,
            String sortByColumn, String whereColumn, String whereValue)
            throws DAOException;
    
    
}
