package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * These are the choices for reporting units in
 * an emission report.
 * CAPS will default to TONS and
 * HAPS will default to POUNDS.
 */
public class EmissionUnitReportingDef {
    public static final String TONS = "TON";
    public static final String POUNDS = "LB";
    private static final String defName = "EmissionUnitReportingDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            // Note that even if TONS or POUNDS is depreciated in
            // UnitDef, the description will be returned.
            data.addItem(TONS, UnitDef.getData().
                    getItems().getItemDesc(TONS));
            data.addItem(POUNDS, UnitDef.getData().
                    getItems().getItemDesc(POUNDS));

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static double convert(String fromUnitCd, double value, String toUnitCd) {
        if(fromUnitCd.equals(TONS)) {
            if(toUnitCd.equals(TONS)) return value;
            if(toUnitCd.equals(POUNDS)) return value*2000d;
        }
        if(fromUnitCd.equals(POUNDS)) {
            if(toUnitCd.equals(POUNDS)) return value;
            if(toUnitCd.equals(TONS)) return value/2000d;
        }
        return value;
    }
}
