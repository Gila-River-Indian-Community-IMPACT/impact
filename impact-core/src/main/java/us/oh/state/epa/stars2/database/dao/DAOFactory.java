package us.oh.state.epa.stars2.database.dao;

import java.util.Properties;

import us.oh.state.epa.stars2.framework.config.AbstractComponent;

/**
 * The DAOFactory builds a list of available DAO's from the config file Using
 * reflection it then builds a list of available DAO's
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version $Revision: 1.30 $
 * @author $Author: pmontoto $
 */

//@Component
@SuppressWarnings("serial")
@Deprecated
public class DAOFactory extends AbstractComponent {
    public static final String ApplicationDAO = "ApplicationDAO";
    public static final String ComplianceReportDAO = "ComplianceReportDAO";
    public static final String RelocateRequestDAO = "RelocateRequestDAO";
    public static final String DelegationRequestDAO = "DelegationRequestDAO";
    public static final String CorrespondenceDAO = "CorrespondenceDAO";
    public static final String DetailDataDAO = "DetailDataDAO";
    public static final String DocumentDAO = "DocumentDAO";
    public static final String EmissionsReportDAO = "EmissionsReportDAO";
    public static final String FullComplianceEvalDAO = "FullComplianceEvalDAO";
    public static final String StackTestDAO = "StackTestDAO";
    public static final String FacilityDAO = "FacilityDAO";
    public static final String CompanyDAO = "CompanyDAO";
    public static final String ContactDAO = "ContactDAO";
    public static final String GenericIssuanceDAO = "GenericIssuanceDAO";
    public static final String InfrastructureDAO = "InfrastructureDAO";
    public static final String InvoiceDAO = "InvoiceDAO";
    public static final String PermitDAO = "PermitDAO";
    public static final String ReportsDAO = "ReportsDAO";
    public static final String ServiceCatalogDAO = "ServiceCatalogDAO";
    public static final String WorkFlowDAO = "WorkFlowDAO";
    public static final String AdHocDAO = "AdHocDAO";
	public static final String GdfDAO = "GdfDAO";
	public static final String ComplaintDAO = "ComplaintDAO";
	public static final String CompEnfFacilityDAO = "CompEnfFacilityDAO";
	public static final String OffsitePceDAO = "OffsitePceDAO";
	public static final String EmissionUnitDAO = "EmissionUnitDAO";
	public static final String ApplicationEUTypeDAO = "ApplicationEUTypeDAO";
	
//    private static Logger logger = Logger.getLogger(DAOFactory.class);
//    
//    private HashMap<String, Class<?>> daos;
//    private HashMap<String, String> schemaQualifiers;
//    private String dbType;
//    private static boolean schemaSubstitution;

//  @Autowired private SQLLoader sqlLoader;
//    private SQLLoader sqlLoader;

//    public final String getSchemaQualifier(String key) {
//        String schema="";
//        if (schemaQualifiers.containsKey(key)) {
//             schema = schemaQualifiers.get(key)+".";
//        }
//        return schema;
//    }
    
    @Override
    public final boolean start(Properties initParameters, String instanceName) {
//        logger.debug("NewDAOFactory:start");
//        setInstanceName(instanceName);
//        daos = new HashMap<String, Class<?>>();
//        schemaQualifiers = new HashMap<String, String>();
//
//        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
//        Node root = cfgMgr.getNode("app.database");
//
//        dbType = root.getAsString("dbType");
//
//        if (dbType == null) {
//            logger.error("No Database Type set!");
//        }
//
//        try {
//            for (Node node : Config.findNodes("app.DAOClass")) {
//                try {
//                    daos.put(node.getAsString("name"), Class.forName(node
//                            .getAsString("class")));
//                } catch (ClassNotFoundException e) {
//                    // Log it, it isn't necessarily fatal at this point...
//                    logger.error(e.getMessage(), e);
//                }
//            }
//            sqlLoader = (SQLLoader) CompMgr.newInstance("app.SQLLoader");
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//
//        Node schemasNode = Config.findNode("app.Schemas");
//        
//        String alwaysUse = schemasNode.getAsString("alwaysUse");
//        
//        if (alwaysUse.compareToIgnoreCase("true") == 0) {
//            schemaSubstitution = true;
//        }
//
//        if (schemasNode != null) {
//            for (Node node : cfgMgr.getAllChildrenOf(schemasNode)) {
//                schemaQualifiers.put(node.getName(), node.getAsString("name"));
//            }
//        }
        return true;
    }

    @Override
    protected final void startInternal() {
    }

//    public final AbstractDAO retrieveDAO(String daoName) {
//        AbstractDAO retDAO = null;
//        Class<?> daoClass = daos.get(daoName);
//
//        if (daoClass != null) {
//            try {
//                retDAO = (AbstractDAO) daoClass.newInstance();
//            } catch (InstantiationException ie) {
//                logger.error(ie.getMessage(), ie);
//            } catch (IllegalAccessException iae) {
//                logger.error(iae.getMessage(), iae);
//            }
//        } else {
//            logger.error("No class found for DAO name = [" + daoName + "].");
//        }
//
//        return retDAO;
//    }

    public static final AbstractDAO getDAO(String daoName) {
    	AbstractDAO ret = null;
//        DAOFactory daoFactory = null;
//
//        try {
//            daoFactory = (DAOFactory) CompMgr.newInstance("app.DAOFactory");
//        } catch (UnableToStartException utse) {
//            logger.error(utse.getMessage(), utse);
//        }
//        
//        if (daoFactory != null) {
//            ret = daoFactory.retrieveDAO(daoName);
//
//            if (schemaSubstitution) {
//                String tempQualifier = daoFactory.schemaQualifiers.get(CommonConst.READONLY_SCHEMA);
//
//                if ((ret != null) && (tempQualifier != null)) {
//                    ret.setSchemaQualifer(tempQualifier + ".");
//                }
//            }
//        }
//
        return ret;
    }    
    
//    public static final AbstractDAO getDAO(String daoName, String schemaQualifer) {
//        DAOFactory daoFactory = null;
//        AbstractDAO ret = null;
//
//        try {
//            daoFactory = (DAOFactory) CompMgr.newInstance("app.DAOFactory");
//        } catch (UnableToStartException utse) {
//            logger.error(utse.getMessage(), utse);
//        }
//
//        if (daoFactory != null) {
//            ret = daoFactory.retrieveDAO(daoName);
//
//            String tempQualifier = daoFactory.schemaQualifiers
//                    .get(schemaQualifer);
//
//            if ((ret != null) && (tempQualifier != null)) {
//                ret.setSchemaQualifer(tempQualifier + ".");
//            }
//        }
//
//        return ret;
//    }

//    public final String retrieveSQL(String path) {
//        return sqlLoader.find(path, dbType);
//    }

//    public static final String loadSQL(String path) {
//        DAOFactory daoFactory = null;
//
//        try {
//            daoFactory = (DAOFactory) CompMgr.newInstance("app.DAOFactory");
//        } catch (UnableToStartException utse) {
//            logger.error(utse.getMessage(), utse);
//        }
//
//        return daoFactory.retrieveSQL(path);
//    }

//    public static final boolean isSchemaSubstitution() {
//        return schemaSubstitution;
//    }
}
