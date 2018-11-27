package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ReportAcceptedStatusDef {
    private static final String defName = "ReportAcceptedStatusDef";
//  public static final String RO_SIGNATURE = "10";

  public static DefData getData() {
      ConfigManager cfgMgr = ConfigManagerFactory.configManager();
      DefData data = cfgMgr.getDef(defName);

      if (data == null) {
          data = new DefData();
          data.loadFromDB("MO_RPT_ACCEPTED_STATUS_DEF", "MO_RPT_ACCEPTED_STATUS_CD", "MO_RPT_ACCEPTED_STATUS_DSC", "deprecated");

          cfgMgr.addDef(defName, data);
      }
      return data;
  }
}
