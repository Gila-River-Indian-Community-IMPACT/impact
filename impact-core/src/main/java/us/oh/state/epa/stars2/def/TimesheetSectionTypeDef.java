package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.util.Utility;

public class TimesheetSectionTypeDef {
	
// TODO - add static constants for function codes
	
	public static final String AD1 = "AD1";
	public static final String AD2 = "AD2";
	public static final String AD3 = "AD3";
	public static final String AM1 = "AM1";
	public static final String AM2 = "AM2";
	public static final String AM3 = "AM3";
	public static final String ASB = "ASB";
	public static final String CM1 = "CM1";
	public static final String CM2 = "CM2";
	public static final String CP1 = "CP1";
	public static final String CP2 = "CP2";
	public static final String DI1 = "DI1";
	public static final String DI2 = "DI2";
	public static final String EI1 = "EI1";
	public static final String EI2 = "EI2";
	public static final String LOC = "LOC";
	public static final String NSR = "NSR";
	public static final String OZ1 = "OZ1";
	public static final String OZ2 = "OZ2";
	public static final String PAR = "PAR";
	public static final String PER = "PER";
	public static final String RE1 = "RE1";
	public static final String RE2 = "RE2";
	public static final String RE3 = "RE3";
	public static final String SP1 = "SP1";
	public static final String SP2 = "SP2";
		
    private static final String defName = "TimesheetSectionTypeDef";
    private static final String defFunction = "TimesheetSectionTypeFunctionDef";
    private static final String defDefaultFunction = "TimesheetSectionTypeDefaultFunctionDef";
    private static final String defNSRValueRequired = "TimesheetSectionTypeNSRValueRequiredDef";
    private static final String defTvValueRequired = "TimesheetSectionTypeTvValueRequiredDef";
    private static final String defReportSeparately = "TimesheetSectionTypeReportSeparateDef";
    private static final String defDisplayCode = "TimesheetSectionTypeDisplayCodeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("ts_section_type_def", "section_cd",
                            "section_desc", "deprecated");
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static DefData getFunctionData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defDefaultFunction);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("ts_section_type_def", "section_cd",
                            "section_function_cd", "deprecated");
            cfgMgr.addDef(defDefaultFunction, data);
        }
        return data;
    }
    
    public static DefData getDefaultFunctionData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defFunction);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("ts_section_type_def", "section_cd",
                            "section_default_for_function", "deprecated");
            cfgMgr.addDef(defFunction, data);
        }
        return data;
    }
    
    public static DefData getNSRValueRequiredData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defNSRValueRequired);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("ts_section_type_def", "section_cd",
                            "section_requires_nsr_value", "deprecated");
            cfgMgr.addDef(defNSRValueRequired, data);
        }
        return data;
    }
    
    public static DefData getTvValueRequiredData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defTvValueRequired);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("ts_section_type_def", "section_cd",
                            "section_requires_tv_value", "deprecated");
            cfgMgr.addDef(defTvValueRequired, data);
        }
        return data;
    }
    
    public static DefData getReportSeparateData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defReportSeparately);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("ts_section_type_def", "section_cd",
                            "report_separately", "deprecated");
            cfgMgr.addDef(defReportSeparately, data);
        }
        return data;
    }
    
    public static String getFunctionCd(String sectionCd) {
		return getFunctionData().getItems().getDescFromAllItem(sectionCd)
				.replace(DefSelectItems.INACTIVE, "");
    }
    
    public static boolean isReportSeparately(String sectionCd) {
		return AbstractDAO.translateIndicatorToBoolean(getReportSeparateData()
				.getItems().getDescFromAllItem(sectionCd));
				
    }
    
    public static DefData getDisplayCodeData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defDisplayCode);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("ts_section_type_def", "section_cd",
                            "display_code", "deprecated");
            cfgMgr.addDef(defDisplayCode, data);
        }
        return data;
    }
    
	public static String getDisplayCode(String sectionCd) {
		String displayCode = null;
		displayCode = getDisplayCodeData().getItems().getDescFromAllItem(
				sectionCd);
		
		if (null != displayCode) {
			displayCode = displayCode.replace(DefSelectItems.INACTIVE, "");
		}
		
		if (Utility.isNullOrEmpty(displayCode)) {
			displayCode = sectionCd;
		}

		return displayCode;
	}
    
}
