package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/* Release Point Operating Status */

public class NSRInvoicePaidDef {
    private static final String defName = "NSRInvoicePaidDef";
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String WAIVER = "W";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.addItem(NSRInvoicePaidDef.YES, "Yes");
            data.addItem(NSRInvoicePaidDef.NO, "No");
            data.addItem(NSRInvoicePaidDef.WAIVER, "Waiver");
            
            
//            data.loadFromDB("PT_PERMIT_ACTION_TYPE_DEF", "ACTION_TYPE_CD",
//                            "ACTION_TYPE_DSC", "deprecated");

            cfgMgr.addDef(defName, data);
        }

        return data;
    }
     
}
