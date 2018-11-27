package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.util.Utility;

/*
 * The codes declared in this class must match the content of the
 * PT_PERMIT_TYPE_DEF table.
 */
public class PermitTypeDef {
//    public static final String TVPTI  = "TVPTI";
    public static final String NSR   = "NSR";
    public static final String TV_PTO = "TVPTO";
//    public static final String TIV_PTO = "TIVPTO";
//    public static final String PBR    = "PBR";
    public static final String RPR    = "RPR";
//    public static final String RPE    = "RPE";
//    public static final String REG    = "REG";
    // SPTO only setup from migration program and database.
//    public static final String SPTO   = "SPTO";
    private static final String defName = "PermitTypeDef";
    private static final String defLongName = "PermitTypeLongDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("pt_permit_type_def", "permit_type_cd",
                            "permit_type_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;

    }

    public static DefData getLongData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defLongName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("pt_permit_type_def", "permit_type_cd",
                            "long_dsc", "deprecated");

            cfgMgr.addDef(defLongName, data);
        }
        return data;

    }

    public static boolean isValid(String permitType) {

        if (getData().getItems() != null 
            && getData().getItems().getItemDesc(permitType) != null) {
            return true;
        }
        return false;
    }
    
    public static Object getPermitTypeDesc(String itemCd) {
		Object ret = null;
		if(!Utility.isNullOrEmpty(itemCd)) {
			ret = getData().getItems().getItemDesc(itemCd);
		}
		
		return ret;
	}

}
