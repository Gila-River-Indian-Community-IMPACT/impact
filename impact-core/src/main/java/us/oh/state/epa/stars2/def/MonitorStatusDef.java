package us.oh.state.epa.stars2.def;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class MonitorStatusDef {
    private static final String defName = "MonitorStatusDef";
//  public static final String RO_SIGNATURE = "10";

  public static DefData getData() {
      ConfigManager cfgMgr = ConfigManagerFactory.configManager();
      DefData data = cfgMgr.getDef(defName);

      if (data == null) {
          data = new DefData();
          data.loadFromDB("MO_MONITOR_STATUS_DEF", "MO_MONITOR_STATUS_CD", "MO_MONITOR_STATUS_DSC", "deprecated");

          cfgMgr.addDef(defName, data);
      }
      return data;
  }

  public static String getInactiveCode() {
	  for (SelectItem item : getData().getItems().getAllItems()) {
		  if ("Inactive".equals(item.getLabel())) {
			  return (String)item.getValue();
		  }
	  }
	  return null;
  }

  public static String getActiveCode() {
	  for (SelectItem item : getData().getItems().getAllItems()) {
		  if ("Active".equals(item.getLabel())) {
			  return (String)item.getValue();
		  }
	  }
	  return null;
  }
}
