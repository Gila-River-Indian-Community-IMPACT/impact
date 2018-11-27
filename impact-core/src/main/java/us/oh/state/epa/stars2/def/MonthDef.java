package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class MonthDef {
    public static final String JAN = "01";
    public static final String FEB = "02";
    public static final String MAR = "03";
    public static final String APR = "04";
    public static final String MAY = "05";
    public static final String JUN = "06";
    public static final String JUL = "07";
    public static final String AUG = "08";
    public static final String SEP = "09";
    public static final String OCT = "10";
    public static final String NOV = "11";
    public static final String DEC = "12";
    private static final String defName = "MonthDef";
    

    // TODO associate this with table CM_MONTH_Def once it is defined.
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.addItem(JAN, "January");
            data.addItem(FEB, "February");
            data.addItem(MAR, "March");
            data.addItem(APR, "April");
            data.addItem(MAY, "May");
            data.addItem(JUN, "June");
            data.addItem(JUL, "July");
            data.addItem(AUG, "August");
            data.addItem(SEP, "September");
            data.addItem(OCT, "October");
            data.addItem(NOV, "November");
            data.addItem(DEC, "December");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
