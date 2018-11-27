package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class MonitoringComplianceStatusDef {
    private static final String defName = "MonitoringComplianceStatusDef";
//  public static final String RO_SIGNATURE = "10";

  public static DefData getData() {
      ConfigManager cfgMgr = ConfigManagerFactory.configManager();
      DefData data = cfgMgr.getDef(defName);

      if (data == null) {
          data = new DefData();
          data.loadFromDB("MO_COMPLIANCE_STATUS_DEF", "MO_COMPLIANCE_STATUS_CD", "MO_COMPLIANCE_STATUS_DSC", "deprecated");

          cfgMgr.addDef(defName, data);
      }
      return data;
  }
}
