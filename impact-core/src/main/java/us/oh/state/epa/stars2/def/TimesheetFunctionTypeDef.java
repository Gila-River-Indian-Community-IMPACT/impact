package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class TimesheetFunctionTypeDef {
	
	// TODO - add static constants for function codes
	public static final String OneZeroThree = "103";
	public static final String OneZeroFive = "105";
	public static final String ADM = "ADM";
	public static final String BEV = "BEV";
	public static final String FML = "FML";
	public static final String HOL = "HOL";
	public static final String JIO = "JIO";
	public static final String JRY = "JRY";
	public static final String LEV = "LEV";
	public static final String LWP = "LWP";
	public static final String MIL = "MIL";
	public static final String NSR = "NSR";
	public static final String OPP = "OPP";
	public static final String PAO = "PAO";
	public static final String SLT = "SLT";
	public static final String SN = "SN";
	public static final String VOT = "VOT";
	
    private static final String defName = "TimesheetFunctionTypeDef";
    private static final String defLongDesc = "TimesheetFunctionTypeLongDef";
    private static final String defSectionRequired = "TimesheetFunctionTypeSectionRequiredDef";
    private static final String defSortOrder = "TimesheetFunctionTypeSortOrderDef";
    private static final String defFixed = "TimesheetFunctionTypeFixedDef";
    private static final String defGrouped = "TimesheetFunctionTypeGroupedDef";
    
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("ts_function_type_def", "function_cd",
                            "function_desc", "deprecated");
            cfgMgr.addDef(defName, data);
        }
        return data;
    }

    public static DefData getLongDescData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defLongDesc);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("ts_function_type_def", "function_cd",
                            "function_long_desc", "deprecated");
            cfgMgr.addDef(defLongDesc, data);
        }
        return data;
    }
    
    public static DefData getSectionRequiredData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defSectionRequired);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("ts_function_type_def", "function_cd",
                            "function_requires_section_value", "deprecated");
            cfgMgr.addDef(defSectionRequired, data);
        }
        return data;
    }
    
    public static DefData getSortOrderData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defSortOrder);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("ts_function_type_def", "function_cd",
                            "sort_order", "deprecated","sort_order" );
            cfgMgr.addDef(defSortOrder, data);
        }
        return data;
    }
    
    public static DefData getFixedData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defFixed);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("ts_function_type_def", "function_cd",
                            "fixed", "deprecated", "sort_order");
            cfgMgr.addDef(defFixed, data);
        }
        return data;
    }
    
    public static boolean isFixed(String functionCd) {
    	return AbstractDAO.translateIndicatorToBoolean(getFixedData().getItems().getDescFromAllItem(functionCd));
    }
    
    
    public static DefData getGroupedData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defGrouped);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("ts_function_type_def", "function_cd",
                            "grouped", "deprecated", "sort_order");
            cfgMgr.addDef(defGrouped, data);
        }
        return data;
    }
    
	public static boolean isGrouped(String functionCd) {
		return AbstractDAO
				.translateIndicatorToBoolean(getGroupedData()
						.getItems().getDescFromAllItem(functionCd));
	}

}

