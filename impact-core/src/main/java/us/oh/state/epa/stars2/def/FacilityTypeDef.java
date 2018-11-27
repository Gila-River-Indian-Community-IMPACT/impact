package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.util.Utility;

public class FacilityTypeDef {
	public static final String PROD_SITE = "13";
	public static final String PRODUCTION_SITE = "8";
	public static final String DEHYDRATION = "13";
	
	private static final String defName = "FacilityTypeDef";
	private static final String defTextName = "FacilityTypeTextDef";
	private static final String defGroupName = "FacilityTypeGroupDef";
	private static final String hasAPIs = "HasAPIs";
	private static final String defHasApisGroupName = "FacilityTypeHasApisGroupDef";
	private static final String oilAndGasGroupName = "FacilityTypeOilAndGasGroupDef";
	private static final String tvInspectionFrequencyGroupName = "FacilityTypeTvInspectionFrequencyGroupDef";
	private static final String smtvInspectionFrequencyGroupName = "FacilityTypeSmtvInspectionFrequencyGroupDef";
	private static final String ntvInspectionFrequencyGroupName = "FacilityTypeNtvInspectionFrequencyGroupDef";
	private static final String defRequiresApisGroupName = "FacilityTypeRequiresApisGroupDef";
	private static final String defHasHCAnalysis = "FacilityTypeHasHCAnalysisDef";
	
	public static DefData getTextData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defTextName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("fp_facility_type_def", "facility_type_cd",
					"facility_type_dsc", "deprecated");

			cfgMgr.addDef(defTextName, data);
		}
		return data;
	}

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("fp_facility_type_def", "facility_type_cd",
					"[facility_type_group] + '_' + [facility_type_dsc]",
					"deprecated", "facility_type_group, facility_type_dsc");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

	public static DefData getIsPortable() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defGroupName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("fp_facility_type_def", "facility_type_cd",
					"[is_portable]", "deprecated");

			cfgMgr.addDef(defGroupName, data);
		}
		return data;
	}

	public static DefData getHasApis() {
		return getHasApisData();
	}
	
	public static DefData getHasApisData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defHasApisGroupName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("fp_facility_type_def", "facility_type_cd",
					"has_apis", "deprecated");

			cfgMgr.addDef(defHasApisGroupName, data);
		}
		return data;
	}
	
	public static DefData getRequiresApisData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defRequiresApisGroupName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("fp_facility_type_def", "facility_type_cd",
					"requires_apis", "deprecated");

			cfgMgr.addDef(defRequiresApisGroupName, data);
		}
		return data;
	}
	
	public static DefData getOilAndGasGroupTypes() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(oilAndGasGroupName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("fp_facility_type_def", "facility_type_cd",
					"facility_type_group", "deprecated");

			cfgMgr.addDef(oilAndGasGroupName, data);
		}
		return data;
	}

	public static String isPortable(String facilityTypeCd) {
		DefData data = getIsPortable();
		String item = data.getItems().getItemDesc(facilityTypeCd);
		return item;
	}

	public static DefData getIsApiEnabled() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(hasAPIs);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("fp_facility_type_def", "facility_type_cd",
					"[has_apis]", "deprecated");

			cfgMgr.addDef(hasAPIs, data);
		}
		return data;
	}

	public static String hasAPisEnabled(String facilityTypeCd) {
		DefData data = getIsApiEnabled();
		String item = data.getItems().getItemDesc(facilityTypeCd);
		return item;
	}

	public static boolean hasApis(String facilityTypeCd) {
		DefData data = getHasApis();
		String item = data.getItems().getItemDesc(facilityTypeCd);
		return AbstractDAO.translateIndicatorToBoolean(item);
	}

	public static boolean requiresApis(String facilityTypeCd) {
		DefData data = getRequiresApisData();
		String item = data.getItems().getItemDesc(facilityTypeCd);
		return AbstractDAO.translateIndicatorToBoolean(item);
	}
	
	public static boolean isOilAndGas(String facilityTypeCd) {
		DefData data = getOilAndGasGroupTypes();
		String groupName = data.getItems().getItemDesc(facilityTypeCd);
		
		return !Utility.isNullOrEmpty(groupName) && groupName.equalsIgnoreCase("O&G");
	}
	public static String getTvInspectionFrequency(String facilityTypeCd) {
		DefData data = getTvInspectionFrequency();
		String item = data.getItems().getItemDesc(facilityTypeCd);
		return item;
	}
	public static String getSmtvInspectionFrequency(String facilityTypeCd) {
		DefData data = getSmtvInspectionFrequency();
		String item = data.getItems().getItemDesc(facilityTypeCd);
		return item;
	}
	public static String getNtvInspectionFrequency(String facilityTypeCd) {
		DefData data = getNtvInspectionFrequency();
		String item = data.getItems().getItemDesc(facilityTypeCd);
		return item;
	}
	
	public static DefData getTvInspectionFrequency() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(tvInspectionFrequencyGroupName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("fp_facility_type_def", "facility_type_cd",
					"tv_inspection_frequency", "deprecated");

			cfgMgr.addDef(tvInspectionFrequencyGroupName, data);
		}
		return data;
	}
	public static DefData getSmtvInspectionFrequency() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(smtvInspectionFrequencyGroupName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("fp_facility_type_def", "facility_type_cd",
					"smtv_inspection_frequency", "deprecated");

			cfgMgr.addDef(smtvInspectionFrequencyGroupName, data);
		}
		return data;
	}
	public static DefData getNtvInspectionFrequency() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(ntvInspectionFrequencyGroupName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("fp_facility_type_def", "facility_type_cd",
					"ntv_inspection_frequency", "deprecated");

			cfgMgr.addDef(ntvInspectionFrequencyGroupName, data);
		}
		return data;
	}
	
	public static DefData getHasHCAnalysisData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defHasHCAnalysis);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("fp_facility_type_def", "facility_type_cd",
					"has_hc_analysis", "deprecated");

			cfgMgr.addDef(defHasHCAnalysis, data);
		}
		return data;
	}
	
	/**
	 * Returns true if HAS_HC_ANALYSIS is set to yes for the given facilityTypeCd. If
	 * HAS_HC_ANALYSIS is set to no, or if the facilityTypeCd is null, returns false.
	 * @param facilityTypeCd
	 * @returns hasHCAnalysis
	 */
	public static boolean hasHCAnalysis(final String facilityTypeCd) {
		boolean hasHCAnalysis = false;

		if (!Utility.isNullOrEmpty(facilityTypeCd)) {

			String hasHCAnalysisStr = getHasHCAnalysisData().getItems().getItemDesc(facilityTypeCd);

			if (!Utility.isNullOrEmpty(hasHCAnalysisStr)) {
				hasHCAnalysis = AbstractDAO.translateIndicatorToBoolean(hasHCAnalysisStr);
			}
		}

		return hasHCAnalysis;
	}
}
