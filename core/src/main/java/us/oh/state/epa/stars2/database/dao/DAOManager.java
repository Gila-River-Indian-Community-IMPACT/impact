package us.oh.state.epa.stars2.database.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Component
public class DAOManager {
	
    protected transient Logger logger = Logger.getLogger(this.getClass());

    @Autowired private Collection<DataAccessObject> daos;
    
    private Map<Class<? extends DataAccessObject>,DataAccessObject> readOnlyDaos = 
    		new HashMap<Class<? extends DataAccessObject>,DataAccessObject>();
    
    private Map<Class<? extends DataAccessObject>,DataAccessObject> stagingDaos = 
    		new HashMap<Class<? extends DataAccessObject>,DataAccessObject>();

    @PostConstruct
    private void init() {
    	for (DataAccessObject dao : daos) {
    		if (CommonConst.READONLY_SCHEMA.equals(dao.getSchema())) {
    			logger.debug("found read-only dao: " + dao.getClass());
    			readOnlyDaos.put((Class<? extends DataAccessObject>)dao.getClass().getInterfaces()[0],dao);
    		} else
    		if (CommonConst.STAGING_SCHEMA.equals(dao.getSchema())) {
    			logger.debug("found staging dao: " + dao.getClass());
    			stagingDaos.put((Class<? extends DataAccessObject>)dao.getClass().getInterfaces()[0],dao);    			
    		} else {
    			throw new RuntimeException("Value of DAO 'schema' property is not supported: " + dao.getSchema());
    		}
    	}
    }

	public DataAccessObject getStagingDAO(Class<? extends DataAccessObject> type) {
		return stagingDaos.get(type);
	}
	
	public DataAccessObject getReadOnlyDAO(Class<? extends DataAccessObject> type) {
		return readOnlyDaos.get(type);
	}
	
	public DataAccessObject getDAO(Class<? extends DataAccessObject> type, Transaction trans) {
		DataAccessObject dao;
		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP) && trans != null) {
			dao = getStagingDAO(type);
		} else {
			dao = getReadOnlyDAO(type);
		}
		return dao;
	}
	
	public DataAccessObject getDAO(Class<? extends DataAccessObject> type) {
		return getDAO(type,null);
	}

	
	public DataAccessObject getDAO(Class<? extends DataAccessObject> type, 
			boolean staging) throws DAOException {
		DataAccessObject dao;
		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			if (staging) {
				dao = getStagingDAO(type);
			} else {
				dao = getReadOnlyDAO(type);
			}
		} else {
			dao = getReadOnlyDAO(type);
		}
		return dao;
	}
	
    public DataAccessObject getDAO(Transaction trans, Class<? extends DataAccessObject> type, String schemaQualifer) throws DAOException {
        DataAccessObject dao = null;
        
        // If the application is set to always use schema substitution and we
        // were sent a nullgetSchema(CommonConst.STAGING_SCHEMA)getSchema(CommonConst.STAGING_SCHEMA)getSchema(CommonConst.STAGING_SCHEMA)getSchema(CommonConst.STAGING_SCHEMA)getSchema(CommonConst.STAGING_SCHEMA)getSchema(CommonConst.STAGING_SCHEMA)getSchema(CommonConst.STAGING_SCHEMA) qualifier, we are going to assume this is for the
        // read only database.
//        if ((DAOFactory.isSchemaSubstitution()) && (schemaQualifer == null)) {
        if (schemaQualifer == null) {
            schemaQualifer = CommonConst.READONLY_SCHEMA;
        }
        
        if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
        	dao = getReadOnlyDAO(type);
        } else if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP) && trans != null) {
//            dao = DAOFactory.getDAO(type, CommonConst.STAGING_SCHEMA);
        	dao = getStagingDAO(type);
        } else if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP) && trans == null && schemaQualifer == null) {
//            dao = DAOFactory.getDAO(type, CommonConst.READONLY_SCHEMA);                            
        	dao = getReadOnlyDAO(type);
        } else if (schemaQualifer != null) {
//            dao = DAOFactory.getDAO(type, schemaQualifer);                            
        	dao = getDAO(type,CommonConst.STAGING_SCHEMA.equals(schemaQualifer));
        } else {
//            dao = DAOFactory.getDAO(type);                            
        	dao = getReadOnlyDAO(type);
        }

//        checkDAONull(dao, type);
        
//        if (trans != null) {
//            dao.setTransaction(trans);
//        }
        

        return dao;        
    }


}
