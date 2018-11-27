package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class GovtFacilityTypeDef {
    public static final String PRIVATE = "0";
    public static final String FEDERAL = "1";
    public static final String STATE = "2";
    public static final String COUNTY = "3";
    public static final String MUNICIPALITY = "4";
    public static final String DISTRICT = "5";
    public static final String TRIBAL_NATION = "6";
    private static final String defName = "GovtFacilityTypeDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("CE_GOVT_FACILITY_TYPE_DEF", "GOVT_FACILITY_TYPE_CD",
                    "GOVT_FACILITY_TYPE_DSC", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
