package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ComplianceStatusDef {
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String ON_SCHEDULE = "O";
    private static final String defName = "ComplianceStatusDef";

    // TODO associate this with table CM_GOVT_FACILITY_TYPE_DEF once it is defined.
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("ce_air_programs_def", "air_programs_cd", "air_programs_dsc", "deprecated");
            
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static String printableValue(String cd) {
        String rtn = "NO VALUE";
        if(cd != null) rtn = ComplianceStatusDef.getData().getItems().getItemDesc(cd);
        if(rtn == null) rtn = "NO GOOD VALUE";
        return rtn;
    }
}
