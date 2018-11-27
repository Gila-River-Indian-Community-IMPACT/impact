package us.wy.state.deq.impact.def;

import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class FCEPreSnapshotTypeDef {
	// ****************** Variables *******************
		private static final String defName = "FCEPreSnapshotTypeDef";
		private static final String tableName = "CE_FCE_PRE_SNAPSHOT_TYPE_DEF";
		private static final String keyFieldName = "SNAPSHOT_TYPE_CD";
		private static final String valueFieldName = "SNAPSHOT_TYPE_DSC";
		private static final String deprecatedFieldName = "DEPRECATED";

		public static final String PA = "PA";
		public static final String PT = "PT";
		public static final String ST = "ST";
		
		public static final String CR = "CR";
		public static final String DC = "DC";
		public static final String EI = "EI";
		
		public static final String LI = "LI";
		public static final String MO = "MO";
		public static final String SV = "SV";
		

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



