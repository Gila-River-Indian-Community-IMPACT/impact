package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class MonitorGroupAttachmentTypeDef {
    private static final String defName = "MonitorGroupAttachmentTypeDef";
//  public static final String RO_SIGNATURE = "10";

  public static DefData getData() {
      ConfigManager cfgMgr = ConfigManagerFactory.configManager();
      DefData data = cfgMgr.getDef(defName);

      if (data == null) {
          data = new DefData();
          data.loadFromDB("MO_MONITOR_GROUP_ATTACHMNT_TYPE_DEF", "MO_MONITOR_GROUP_ATTACHMNT_TYPE_CD", "MO_MONITOR_GROUP_ATTACHMNT_TYPE_DSC", "deprecated");

          cfgMgr.addDef(defName, data);
      }
      return data;
  }}
