package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class NSRBillingChargePaymentTypeDef {
	public static final String PAYMENT = "PT";
	public static final String INITIAL_INVOICE = "II";
	public static final String FINAL_INVOICE = "FI";
	public static final String SPECIAL_INVOICE = "SI";
	public static final String OTHER_CREDIT = "OC";
	
	private static final String defName = "NSRBillingChargePaymentTypeDef";
	private static final String defNameChargePaymentType = "NSRBillingChargePaymentTransactionTypeDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("NSR_BILLING_CHARGE_PAYMENT_TYPE_DEF", "CODE", "DESCRIPTION", "DEPRECATED", "DESCRIPTION");
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
	
	public static DefData getChargePaymentTypeData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defNameChargePaymentType);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("NSR_BILLING_CHARGE_PAYMENT_TYPE_DEF", "CODE", "TYPE", "DEPRECATED", "TYPE");
			cfgMgr.addDef(defNameChargePaymentType, data);
		}
		return data;
	}

}
