package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class PTIORegulatoryStatus {
    public static String ACTIVE_PTIO = "acpt";
    public static String EXTENDED_PTIO = "epio";
    public static String EXPIRED_PTIO = "xpio";

    public static String ACTIVE_FEPTIO   = "acfe";
    public static String EXTENDED_FEPTIO = "efio";
    public static String EXPIRED_FEPTIO  = "xfio";

    public static String ACTIVE_FESOP   = "acfs";
    public static String EXPIRED_FESOP  = "exfs";
    public static String EXTENDED_FESOP = "etfs";
    
    public static String ACTIVE_TV_PTO = "atvp";
    public static String EXTENDED_TV_PTO = "etvp";
    public static String EXPIRED_TV_PTO = "xtvp";

    public static String EXTENDED_PTO = "epto";
    public static String EXPIRED_PTO = "xpto";
    
    public static String PBR = "pbyr";
    public static String NONE = "none";
    private static final String defName = "PTIORegulatoryStatus";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("cm_ptio_regulatory_status",
                            "ptio_regulatory_status_cd", "ptio_regulatory_status_dsc",
                            "deprecated");

            cfgMgr.addDef(defName, data);
        }

        return data;
    }
}
