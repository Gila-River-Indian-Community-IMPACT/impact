package us.wy.state.deq.impact.def;

import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class CshUnitTypeDef {

	public static final String CRUSHING = "crushing";
	public static final String SCREENING = "screening";
	public static final String HANDLING = "materialhandling";
	public static final String OTHER = "other";
	
	// ****************** Variables *******************
	private static final String defName = "CshUnitTypeDef";
	private static final String tableName = "CM_CSH_UNIT_TYPE_DEF";
	private static final String keyFieldName = "TYPE_CD";
	private static final String valueFieldName = "TYPE_DSC";
	private static final String deprecatedFieldName = "DEPRECATED";


	// ****************** Public Static Methods *******************
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			
			data.loadFromDB(tableName, keyFieldName, valueFieldName,
			deprecatedFieldName);
		
			cfgMgr.addDef(defName, data);
		}

		return data;
	}
}
