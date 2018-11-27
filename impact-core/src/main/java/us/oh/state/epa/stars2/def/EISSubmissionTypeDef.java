package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EISSubmissionTypeDef {
	
	private static final String defName = "EISSubmissionTypeDef";
	    
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);
		
		if (data == null) {
		    data = new DefData();
		    data.loadFromDB("RP_EIS_SUBMISSION_TYPE_DEF", "EIS_SUBMISSION_TYPE_CD",
		        "EIS_SUBMISSION_TYPE_DSC", "DEPRECATED");
		
		    cfgMgr.addDef(defName, data);
		}
		return data;
	}
}