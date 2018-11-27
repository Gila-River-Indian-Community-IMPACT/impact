package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class AFSViolationTypeCodesDef {
    public static final String DIS = "DIS";
    public static final String GC1 = "GC1";
    public static final String GC2 = "GC2";
    public static final String GC3 = "GC3";
    public static final String GC4 = "GC4";
    public static final String GC5 = "GC5";
    public static final String GC6 = "GC6";
    public static final String GC7 = "GC7";
    public static final String GC8 = "GC8";
    public static final String GC9 = "GC9";
    public static final String GC10 = "GC10";
    public static final String M1A = "M1A";
    public static final String M1B = "M1B";
    public static final String M1C = "M1C";
    public static final String M2A = "M2A";
    public static final String M2B = "M2B";
    public static final String M2C = "M2C";
    public static final String M3A = "M3A";
    public static final String M3B = "M3B";
    public static final String M3C = "M3C";
    public static final String M3D = "M3D";
    public static final String M3E = "M3E";
    public static final String M3F = "M3F";
    public static final String M4A = "M4A";
    public static final String M4B = "M4B";
    public static final String M4C = "M4C";
    public static final String M4D = "M4D";
    public static final String M4E = "M4E";
    public static final String M4F = "M4F";
 

    private static final String defName = "AFSViolationTypeCodesDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("CE_ENF_VIOLATION_DEF", "ENF_VIOLATION_CD",
                    "ENF_VIOLATION_DSC", "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
