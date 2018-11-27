package us.oh.state.epa.stars2.def;

import java.util.Calendar;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class YearsForFCEs {
	public static int maxYearsOut = 6;
    private static int firstYearOfOperation = 2000;
    private static final String defName = "YearsForFCEs";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        int savedCurrentYear = 0;
        // find out the current year
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR) + maxYearsOut;
        
/*
        if(Calendar.MONTH >= Calendar.OCTOBER) {
            // We want next FFY
            currentYear++;
        }
        currentYear++;
*/
        
        if (data == null || savedCurrentYear != currentYear) {
            savedCurrentYear = currentYear;
            data = new DefData();
            for (int k=currentYear;k>=firstYearOfOperation;k--) {
                data.addItem(Integer.toString(k),Integer.toString(k));
            }

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
