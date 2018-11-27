package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_MODEL_GENERAL_PERMIT_DEF table.
 */
public class PTIOModelGeneralPermitDef {
    private static final String defName = "PTIOModelGeneralPermitDef";
    private static final String defGeneralName = "PTIOModelGeneralDef";
    private static final String defGeneralPermitName = "PTIOModelGeneralPermitPermitDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("pa_model_general_permit_def",
                    "model_general_permit_cd", "model_general_permit_dsc",
                    "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;

    }

    /** Get the matching code for the parent of a model general permit code. */
    public static DefData getGeneralPermitData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defGeneralName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("pa_general_permit_type_def",
                    "general_permit_type_cd", "general_permit_type_dsc",
                    "deprecated");
            
            cfgMgr.addDef(defGeneralName, data);
        }
        return data;

    }

    /** Get the matching code for the parent of a model general permit code. */
    public static DefData getGeneralPermitDataForModel() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defGeneralPermitName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("pa_model_general_permit_def",
                                        "model_general_permit_cd",
                                        "general_permit_type_cd",
                                        "deprecated");

            cfgMgr.addDef(defGeneralPermitName, data);
        }
        return data;

    }
}
