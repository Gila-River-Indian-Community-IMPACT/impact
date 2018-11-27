package us.oh.state.epa.stars2.database.dao;

import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.adhoc.DataGridRow;
import us.oh.state.epa.stars2.database.adhoc.DataSet;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface AdHocDAO extends TransactableDAO {
    /**
     * @param complianceReport
     * @throws DAOException
     */
    boolean update(DataGridRow dgr,String table,DataGridRow dgrDefinition)
            throws DAOException;
    
    boolean insert(DataGridRow dgr,String table,DataGridRow dgrDefinition) 
            throws DAOException;
    
    List<SelectItem> retrievePickList(DataGridRow datasetDefinition,String field) 
            throws DAOException;
    
    DataSet retrieve(DataSet ds) 
            throws DAOException;
}






