package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class MonitoringContractorDef {
    private static final String defName = "MonitoringContractorDef";
//  public static final String RO_SIGNATURE = "10";

  public static DefData getData() {
      ConfigManager cfgMgr = ConfigManagerFactory.configManager();
      DefData data = cfgMgr.getDef(defName);

      if (data == null) {
          data = new DefData();
          data.loadFromDB("MO_CONTRACTOR_DEF", "MO_CONTRACTOR_CD", "MO_CONTRACTOR_DSC", "deprecated");

          cfgMgr.addDef(defName, data);
      }
      return data;
  }
}
