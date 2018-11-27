package us.oh.state.epa.stars2.database.dbObjects;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Base interface for all database objects, all objects used to transfer data to
 * the database must implement this interface.
 * 
 * @author Kbradley
 * 
 */
public interface BaseDBObject {
    /**
     * Used to populate a BaseDBObject with values from a resultSet. Populate
     * methods help accomplish the abstraction of the exception handling and
     * correct Connection/Statement handling/closing at the DAO layer.
     * 
     * @param rs
     * @throws Exception
     */
    void populate(ResultSet rs) throws SQLException;

    void populate(ResultSet rs, int option) throws SQLException;

    byte[] toXMLStream();
}
