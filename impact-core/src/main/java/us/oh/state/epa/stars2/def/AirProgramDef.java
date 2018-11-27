package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class AirProgramDef {
	public static final String TITLE_V = "TV";
	public static final String SM_FESOP = "SM";
	private static final String defName = "AirProgramDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);
		
		if (data == null) {
			data = new DefData();
			data.loadFromDB("FP_AIR_PROGRAM_DEF", "air_program_cd",
					"air_program_dsc", "DEPRECATED");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

	public static String printableValue(String cd) {
		String rtn = "NO VALUE";
		if (cd != null)
			rtn = AirProgramDef.getData().getItems().getItemDesc(cd);
		if (rtn == null)
			rtn = "NO GOOD VALUE";
		return rtn;
	}
}
