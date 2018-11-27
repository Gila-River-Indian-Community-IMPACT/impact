package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_PBR_TYPE_DEF table
 */
public class PBRTypeDef {
    public static String EMERGENCY_ELECTRICAL_GENERATORS_WATER_PUMPS_OR_AIR_COMPRESSORS = "0";
    public static String EQUIPMENT_USED_FOR_INJECTION_AND_COMPRESSION_MOLDING_OF_RESINS = "1";
    public static String NONMETALLIC_MINERAL_PROCESSING_PLANTS = "2";
    public static String SOIL_VAPOR_EXTRACTION_REMEDIATION_ACTIVITIES = "3";
    public static String SOIL_LIQUID_EXTRACTION_REMEDIATION_ACTIVITIES = "4";
    public static String AUTO_BODY_REFINISHING_FACILITY = "5";
    public static String GASOLINE_DISPENSING_FACILITY_WITH_STAGE_I_CONTROLS = "6";
    public static String GASOLINE_DISPENSING_FACILITY_WITH_STAGE_I_AND_STAGE_II_CONTROLS = "7";
    public static String BOILER_AND_HEATER = "8";
    public static String SMALL_PRINTING_FACILITY = "9";
    public static String MID_SIZE_PRINTING_FACILITY = "10";
    private static final String defName = "PBRTypeDef";
    private static final String defLongName = "PBRTypeLongDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("pa_pbr_type_def", "pbr_type_cd",
                            "pbr_type_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }

    public static DefData getLongDscData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defLongName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("pa_pbr_type_def", "pbr_type_cd",
                               "long_dsc", "deprecated");

            cfgMgr.addDef(defLongName, data);
        }
        return data;
    }
    
    public static boolean isValid(String pbrTypeCd) {
        if (getData().getItems() != null 
            && getData().getItems().getItemDesc(pbrTypeCd) != null) {
            return true;
        }
        return false;
    }

}
