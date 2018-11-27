package us.wy.state.deq.impact.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.database.dao.CoreDAO;
import us.oh.state.epa.stars2.database.dao.DAOManager;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Transactional(rollbackFor=Exception.class)
@Service
public class CoreBO implements CoreService {

	@Autowired DAOManager daoManager;

    private CoreDAO coreDAO() throws DAOException {
        return (CoreDAO)daoManager.getDAO(null, CoreDAO.class, null);
    }


	/**
	 * @param sqlLoadString
	 * @param objectToRetrieve
	 * @return SimpleIdDef
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SimpleIdDef[] retrieveBaseIdDB(String sqlLoadString,
			Class objectToRetrieve) throws DAOException {
		return coreDAO().retrieveBaseIdDB(sqlLoadString,
				objectToRetrieve);
	}
	
	
	/**
	 * @param sqlLoadString
	 * @param objectToRetrieve
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BaseDef[] retrieveBaseDB(String sqlLoadString, Class objectToRetrieve)
			throws DAOException {
		return coreDAO().retrieveBaseDB(sqlLoadString,
				objectToRetrieve);
	}

	
	

	/**
	 * Retrieves all values for a definition that has a code of type string -
	 * i.e., a SimpleIdDef
	 * 
	 * @return SimpleIdDef[] an array containing all the values
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveSimpleIdDefs(String dbTable,
			String dbIdColumn, String dbDescColumn, String dbDeprecatedColumn,
			String sortByColumn) throws DAOException {

		return coreDAO().retrieveSimpleIdDefs(dbTable, dbIdColumn,
				dbDescColumn, dbDeprecatedColumn, sortByColumn);
	}
	
	
	

	/**
	 * Retrieves all values for a definition that has a code of type string -
	 * i.e., a SimpleDef
	 * 
	 * @return SimpleDef[] an array containing all the values
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleDef[] retrieveSimpleDefs(String dbTable, String dbCodeColumn,
			String dbDescColumn, String dbDeprecatedColumn, String sortByColumn)
			throws DAOException {

		return coreDAO().retrieveSimpleDefs(dbTable, dbCodeColumn,
				dbDescColumn, dbDeprecatedColumn, sortByColumn);
	}

	/**
	 * Retrieves all values for a definition that has a code of type string -
	 * i.e., a SimpleDef which matches the where attribute value
	 * 
	 * @return SimpleDef[] an array containing all the values
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleDef[] retrieveSimpleDefs(String dbTable, String dbCodeColumn,
			String dbDescColumn, String dbDeprecatedColumn,
			String sortByColumn, String whereColumn, String whereValue)
			throws DAOException {

		return coreDAO().retrieveSimpleDefs(dbTable, dbCodeColumn,
				dbDescColumn, dbDeprecatedColumn, sortByColumn, whereColumn,
				whereValue);
	}

}
