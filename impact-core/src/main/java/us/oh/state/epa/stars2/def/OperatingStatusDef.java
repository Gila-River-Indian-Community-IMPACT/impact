package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/* Facility Inventory Operating Status */

public class OperatingStatusDef {
	public static final String NI = "ni"; // not installed
	public static final String IA = "ia"; // inactive
	public static final String OP = "op"; // operating
	public static final String SD = "sd"; // shutdown
	private static final String defName = "OperatingStatusDef";
	private static final String defDemName = "OperatingStatusDemDef";
	private static DefData data;
	private static DefData data1;
	private static DefData demData;
	private static long refreshTime;

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data1 = cfgMgr.getDef(defName);

		if (data1 == null) {
			data1 = new DefData();
			data1.loadFromDB("fp_operating_status_def", "operating_status_cd",
					"operating_status_dsc", "deprecated");
			cfgMgr.addDef(defName, data1);
			refreshTime = -1;
		}

		DefSelectItems selItems = data1.getItems();

		if (data1.getRefreshTime() != refreshTime) {
			data = new DefData();

			data.addItem(NI, selItems.getItemDesc(NI),
					selItems.isItemDepricated(NI));
			data.addItem(IA, selItems.getItemDesc(IA),
					selItems.isItemDepricated(IA));
			data.addItem(OP, selItems.getItemDesc(OP),
					selItems.isItemDepricated(OP));
			data.addItem(SD, selItems.getItemDesc(SD),
					selItems.isItemDepricated(SD));
			refreshTime = data1.getRefreshTime();
		}

		return data;
	}

	public static DefData getDemData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data1 = cfgMgr.getDef(defDemName);

		if (data1 == null) {
			data1 = new DefData();
			data1.loadFromDB("fp_operating_status_def", "operating_status_cd",
					"operating_status_dsc", "deprecated");
			cfgMgr.addDef(defDemName, data1);
			refreshTime = -1;
		}

		DefSelectItems selItems = data1.getItems();
		if (data1.getRefreshTime() != refreshTime) {
			demData = new DefData();
			demData.addItem(NI, selItems.getItemDesc(NI),
					selItems.isItemDepricated(NI));
			demData.addItem(IA, selItems.getItemDesc(IA),
					selItems.isItemDepricated(IA));
			demData.addItem(OP, selItems.getItemDesc(OP),
					selItems.isItemDepricated(OP));
			refreshTime = data1.getRefreshTime();
		}

		return demData;
	}
}
