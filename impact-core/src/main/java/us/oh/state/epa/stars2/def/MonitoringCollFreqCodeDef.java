package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class MonitoringCollFreqCodeDef {
    private static final String defName = "MonitoringCollFreqCodeDef";
//  public static final String RO_SIGNATURE = "10";

  public static DefData getData() {
      ConfigManager cfgMgr = ConfigManagerFactory.configManager();
      DefData data = cfgMgr.getDef(defName);

      if (data == null) {
          data = new DefData();
          data.loadFromDB("MO_COLL_FREQ_DEF", "MO_COLL_FREQ_CD", "MO_COLL_FREQ_DSC", "deprecated");

          cfgMgr.addDef(defName, data);
      }
      return data;
  }
}
