package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class MonitorTypeDef {
    private static final String defName = "MonitorTypeDef";
    
    public static final String AMBIENT_AIR = "01";
    public static final String METEOROLOGICAL = "02";
    
//  public static final String RO_SIGNATURE = "10";

  public static DefData getData() {
      ConfigManager cfgMgr = ConfigManagerFactory.configManager();
      DefData data = cfgMgr.getDef(defName);

      if (data == null) {
          data = new DefData();
          data.loadFromDB("MO_MONITOR_TYPE_DEF", "MO_MONITOR_TYPE_CD", "MO_MONITOR_TYPE_DSC", "deprecated");

          cfgMgr.addDef(defName, data);
      }
      return data;
  }
}
