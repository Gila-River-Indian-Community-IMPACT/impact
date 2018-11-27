package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class OffsetTrackingNonAttainmentLevelDef {
	
	private static final String defName = "OffsetTrackingNonAttainmentLevelDef";
		
	public static final String MARGINAL = "NAL10";
	public static final String MODERATE = "NAL20";
	public static final String SERIOUS = "NAL30";
	public static final String SEVERE_15 = "NAL40";
	public static final String SEVERE_17 = "NAL50";
	public static final String EXTREME = "NAL60";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("ot_non_attainment_level_def", "non_attainment_level_cd",
                            "non_attainment_level_dsc", "deprecated");
            cfgMgr.addDef(defName, data);
        }
        return data;
    }

}
