package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EnfCaseStateDef {	
	public static final String open2 = "2ope";
	public static final String open2a = "2opa";
	public static final String init1 = "1ini";
	public static final String close5 = "5clo";
	public static final String resolve4 = "4res"; // pending milestone
	public static final String reff3 = "3ref";
	
	private static final String defName = "EnfCaseStateDef";
	public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("CE_ENF_CASE_STATE_DEF", "ENF_CASE_STATE_CD",
                    "ENF_CASE_STATE_DSC", "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
	
	public final DefSelectItems getEnfCaseStateDef() {
        return EnfCaseStateDef.getData().getItems();
    }
	
	public static boolean isOpenState(String state) {
		return (state.equals(open2) || state.equals(open2a)) ? true : false;
	}
	
	public static boolean isCloseState(String state) {
		return (state.equals(close5)) ? true : false;
	}
	
	public static boolean isResolveState(String state) {
		return (state.equals(resolve4)) ? true : false;
	}

	public static boolean isInitState(String state) {
		return (state.equals(init1)) ? true : false;
	}
	public static boolean isRefferedState(String state) {
		return (state.equals(reff3)) ? true : false;
	}
}
