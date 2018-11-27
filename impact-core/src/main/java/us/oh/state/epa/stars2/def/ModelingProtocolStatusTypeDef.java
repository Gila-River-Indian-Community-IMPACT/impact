package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ModelingProtocolStatusTypeDef {
	
	public static final String defName = "ModelingProtocolStatusTypeDef";
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PO_MODELING_PROTOCOL_STATUS_DEF", "STATUS_CD",
					"STATUS_DSC", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

}
