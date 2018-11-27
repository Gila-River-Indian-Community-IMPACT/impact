package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ContactTypeDef {
    // duplicate public static final String OPER="oper";
    //public static final String OWNR="ownr"; // To Do: obsolete for IMPACT. scrub code
    public static final String BILL="bill";
    public static final String RSOF="resp";
    public static final String ONST="oper";
    //public static final String PRIM="prim"; // To Do: obsolete for IMPACT. scrub code
    public static final String COMP="comp";
    public static final String EMIS="emis";
    //public static final String OTHR="other";
    public static final String ENVI="envr";
    public static final String PERM="perm";
    public static final String MONI="moni";
    public static final String NSRB = "nsrb";
    private static final String defNoOwnerName = "NoOwnerContactTypeDef";
    private static final String defName = "ContactTypeDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("cm_contact_type_def", "contact_type_cd",
                    "contact_type_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static DefData getNoOwnerData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defNoOwnerName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrieveNoOwnerContactTypes",
                    SimpleDef.class);
            
            cfgMgr.addDef(defNoOwnerName, data);
        }
        return data;
    }
}
