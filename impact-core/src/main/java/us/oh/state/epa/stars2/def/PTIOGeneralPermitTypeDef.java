package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_GENERAL_PERMIT_TYPE_DEF table.
 */
public class PTIOGeneralPermitTypeDef {
    public static final String BOILERS = "0";
    public static final String DRYCLEANING_OPERATION = "1";
    public static final String MISC_METAL_PARTS_PAINTING_LINES = "2";
    public static final String READY_MIX_CONCRETE_BATCH_PLANTS = "3";
    public static final String UNPAVED_ROADWAYS_AND_PARKING_AREAS = "4";
    public static final String PAVED_ROADWAYS_AND_PARKING_AREAS = "5";
    public static final String STORAGE_PILES = "6";
    private static final String defName = "PTIOGeneralPermitTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("pa_general_permit_type_def",
                    "general_permit_type_cd", "general_permit_type_dsc",
                    "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
