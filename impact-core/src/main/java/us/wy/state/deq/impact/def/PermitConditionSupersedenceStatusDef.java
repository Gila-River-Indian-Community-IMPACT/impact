package us.wy.state.deq.impact.def;

import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class PermitConditionSupersedenceStatusDef {
	
	public enum PermitConditionSupersedenceStatus {
		COMPLETE, PARTIAL
	}
	
	public static String COMPLETE = "C";
	public static String PARTIAL = "P";

	// ****************** Variables *******************
	private static final String defName = "PermitConditionSupersedenceStatusDef";
	private static final String tableName = "PT_PERMIT_CONDITION_SUPERSEDENCE_STATUS_DEF";
	private static final String keyFieldName = "CODE";
	private static final String valueFieldName = "DESCRIPTION";
	private static final String deprecatedFieldName = "DEPRECATED";

	// ****************** Public Static Methods *******************
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB(tableName, keyFieldName, valueFieldName,
					deprecatedFieldName);

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
	
	public static String getStatus(PermitConditionSupersedenceStatus statusEnum) {
		
		if (statusEnum == null) {
			return "";
		}
		
		switch(statusEnum) {
		case COMPLETE:
			return COMPLETE;
		case PARTIAL:
			return PARTIAL;
		default:
			return "";
		}
		
	}
	
	public static PermitConditionSupersedenceStatus getStatusEnum(String status) {
		
		if (status == null) {
			return null;
		}
	
		if (status.equals(PermitConditionSupersedenceStatusDef.COMPLETE)) {
			return PermitConditionSupersedenceStatus.COMPLETE;
		} else if (status.equals(PermitConditionSupersedenceStatusDef.PARTIAL)) {
			return PermitConditionSupersedenceStatus.PARTIAL;
		}
		
		return null;

	}
	
}

