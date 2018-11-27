package us.wy.state.deq.impact.def;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class TnkMaterialStoredTypeLiquidDef extends SimpleDef {

	private static final long serialVersionUID = -5278853492546429179L;
	
	public static final String OIL = "Oil";
	public static final String WATER = "Water";
	public static final String OTHER = "Other";

	// ****************** Variables *******************
	private static final String defName = "TnkMaterialStoredTypeLiquidDef";
	private static final String tableName = "CM_TNK_MATERIAL_STORED_TYPE_LIQUID_DEF";
	private static final String keyFieldName = "TYPE_CD";
	private static final String valueFieldName = "TYPE_DSC";
	private static final String deprecatedFieldName = "DEPRECATED";

	// ****************** Public Static Methods *******************
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB(tableName, keyFieldName, valueFieldName, deprecatedFieldName);

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
