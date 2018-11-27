package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class CorrespondenceAttachmentTypeDef {
	
public static final String CA = "CA"; // Correspondence Attachment
	
private static final String defName = "CorrespondenceAttachmentTypeDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);
	
		if (data == null) {
			data = new DefData();
			data.loadFromDB("DC_CORRESPONDENCE_ATTACHMENT_TYPE_DEF", "correspondence_attachment_type_cd", 
					"correspondence_attachment_type_dsc", "deprecated");
	
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
