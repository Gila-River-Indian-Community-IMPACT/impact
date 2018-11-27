package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_APPLICATION_TYPE_DEF table
 */
public class ApplicationTypeDef {
//  public static final String PBR_NOTIFICATION = "PBR";
    public static final String PTIO_APPLICATION = "PTIO";
    public static final String TITLE_IV_APPLICATION = "TIV";
    public static final String TITLE_V_APPLICATION = "TV";
    public static final String RPC_REQUEST = "RPC";
    public static final String RPE_REQUEST = "RPE";
    public static final String RPR_REQUEST = "RPR";
    /*
     * the following three types are for relocation
     */
    //public static final String RELOCATE_TO_PREAPPROVED_SITE = "RPS";
    //public static final String SITE_PRE_APPROVAL = "SPA";
    //public static final String INTENT_TO_RELOCATE = "ITR";
    
    /*
     * DOR (Delegation of Responsibility)
     */
    public static final String DELEGATION_OF_RESPONSIBILITY = "DOR";
    /*
     * end relocation type defs
     */
    private static final String defName = "ApplicationTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_APPLICATION_TYPE_DEF", "APPLICATION_TYPE_CD",
                    "APPLICATION_TYPE_DSC", "DEPRECATED", "APPLICATION_TYPE_DSC");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static boolean isRelocation(String cd) {
        /*
    	if(RELOCATE_TO_PREAPPROVED_SITE.equals(cd) ||
                SITE_PRE_APPROVAL.equals(cd) ||
                INTENT_TO_RELOCATE.equals(cd)) {
            return true;
        }
        */
        return false;
    }
}
