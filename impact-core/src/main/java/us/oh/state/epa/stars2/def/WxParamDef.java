package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class WxParamDef {
    private static final String defName = "WxParamDef";
//  public static final String RO_SIGNATURE = "10";

  public static DefData getData() {
      ConfigManager cfgMgr = ConfigManagerFactory.configManager();
      DefData data = cfgMgr.getDef(defName);

      if (data == null) {
          data = new DefData();
          data.loadFromDB("MO_WX_PARAM_DEF", "MO_WX_PARAM_CD", "MO_WX_PARAM_DSC", "deprecated");

          cfgMgr.addDef(defName, data);
      }
      return data;
  }

}
