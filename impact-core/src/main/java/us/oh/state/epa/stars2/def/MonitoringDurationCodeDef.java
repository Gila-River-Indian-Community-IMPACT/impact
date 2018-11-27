package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class MonitoringDurationCodeDef {
    private static final String defName = "MonitoringDurationCodeDef";
//  public static final String RO_SIGNATURE = "10";

  public static DefData getData() {
      ConfigManager cfgMgr = ConfigManagerFactory.configManager();
      DefData data = cfgMgr.getDef(defName);

      if (data == null) {
          data = new DefData();
          data.loadFromDB("MO_DURATION_CODE_DEF", "MO_DURATION_CODE_CD", "MO_DURATION_CODE_DSC", "deprecated");

          cfgMgr.addDef(defName, data);
      }
      return data;
  }
}
