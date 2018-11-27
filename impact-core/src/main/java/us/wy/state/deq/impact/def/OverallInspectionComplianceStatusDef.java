package us.wy.state.deq.impact.def;

import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class OverallInspectionComplianceStatusDef {

	// ****************** Variables *******************
	private static final String defName = "OverallInspectionComplianceStatusDef";
	private static final String defLongName = "OverallInspectionComplianceStatusLongDef";
	private static final String tableName = "CE_FCE_OVERALL_INSPECTION_COMPLIANCE_STATUS_DEF";
	private static final String keyFieldName = "INSPECTION_COMPLIANCE_STATUS_CD";
	private static final String valueFieldName = "INSPECTION_COMPLIANCE_STATUS_DSC";
	private static final String longValueFieldName = "INSPECTION_COMPLIANCE_STATUS_LONG_DSC";
	private static final String deprecatedFieldName = "DEPRECATED";

	// ****************** Public Static Methods *******************
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB(tableName, keyFieldName, valueFieldName, deprecatedFieldName);

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
	
	public static DefData getLongDscData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defLongName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB(tableName, keyFieldName, longValueFieldName, deprecatedFieldName);

            cfgMgr.addDef(defLongName, data);
        }
        return data;
    }
	
	public static boolean isValid(String inspectionComplianceStatusCd) {
        if (getData().getItems() != null 
            && getData().getItems().getItemDesc(inspectionComplianceStatusCd) != null) {
            return true;
        }
        return false;
    }
}
