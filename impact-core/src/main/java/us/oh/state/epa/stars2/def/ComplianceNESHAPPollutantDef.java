package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ComplianceNESHAPPollutantDef {
    public static final String AS = "as";
    public static final String AB = "ab";
    public static final String BE = "be";
    public static final String BZ = "bz";
    public static final String CE = "ce";
    public static final String RD = "rd";
    public static final String VC = "vc";
    public static final String HG = "hg";
    private static final String defName = "ComplianceNESHAPPollutantDef";

    // TODO associate this with table CM_COMPLIANCE_NESHAP_POLLUTANT_DEF once it is defined.
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.addItem(AS, "As");
            data.addItem(AB, "AB");
            data.addItem(BE, "Be");
            data.addItem(BZ, "BZ");
            data.addItem(CE, "CE");
            data.addItem(RD, "RD");
            data.addItem(VC, "VC");
            data.addItem(HG, "Hg");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
