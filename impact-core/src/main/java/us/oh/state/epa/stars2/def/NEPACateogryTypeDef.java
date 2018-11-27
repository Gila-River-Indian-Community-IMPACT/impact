package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class NEPACateogryTypeDef {
	public static final String defName = "NEPACateogryTypeDef";
	
	public static final String OIL_AND_GAS = "OG";
	public static final String COAL = "CO";
	public static final String URANIUM = "UR";
	public static final String HARD_ROCK = "HR";
	public static final String PIPELINE = "PI";
	public static final String TRANSMISSION = "TR";
	public static final String FORESTRY = "FO";  
	public static final String VEGETATION = "VE";
	public static final String RMP = "RMP";
	public static final String FOREST_PLAN ="FP"; 
	public static final String SOLAR = "SO";
	public static final String WIND = "WI";
	public static final String OTHER = "OTH";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PO_NEPA_CATEGORY_DEF", "CATEGORY_CD",
					"CATEGORY_DSC", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
