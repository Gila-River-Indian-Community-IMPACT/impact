package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/* Facility Inventory Operating Status */

public class FacilityRequestStatusDef {
	public static final String PENDING = "PND"; // pending
	public static final String CLOSED_FAC_CREATED = "CFC"; // closed - facility created
	public static final String CLOSED_NO_ACTION = "CNA"; // closed - no action
	private static final String defName = "FacilityRequestStatusDef";
	private static DefData data;
	private static long refreshTime;

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data1 = cfgMgr.getDef(defName);

		if (data1 == null) {
			data1 = new DefData();
			data1.loadFromDB("fp_request_status_def", "request_status_cd",
					"request_status_dsc", "deprecated");
			cfgMgr.addDef(defName, data1);
			refreshTime = -1;
		}

		DefSelectItems selItems = data1.getItems();

		if (data1.getRefreshTime() != refreshTime) {
			data = new DefData();

			data.addItem(PENDING, selItems.getItemDesc(PENDING),
					selItems.isItemDepricated(PENDING));
			data.addItem(CLOSED_FAC_CREATED,
					selItems.getItemDesc(CLOSED_FAC_CREATED),
					selItems.isItemDepricated(CLOSED_FAC_CREATED));
			data.addItem(CLOSED_NO_ACTION,
					selItems.getItemDesc(CLOSED_NO_ACTION),
					selItems.isItemDepricated(CLOSED_NO_ACTION));

			refreshTime = data1.getRefreshTime();
		}

		return data;
	}
}