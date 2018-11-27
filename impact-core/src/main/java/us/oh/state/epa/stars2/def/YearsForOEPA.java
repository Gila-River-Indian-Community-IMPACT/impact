package us.oh.state.epa.stars2.def;

import java.util.Calendar;
import java.util.GregorianCalendar;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class YearsForOEPA {
    private static int firstYearOfOperation = 2008;
    private static final String defName = "YearsForOEPA";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            // find out the current year
            Calendar now = Calendar.getInstance();
            int currentYear = now.get(Calendar.YEAR);
            
            // if it is not yet June 1st, set current year to last year
            // since users will likely still be working on reports for
            // the previous year (Mantis 2772)
//            GregorianCalendar june = new GregorianCalendar();
//            june.set(Calendar.MONTH, Calendar.JUNE);
//            june.set(Calendar.DAY_OF_MONTH, 1);
//            june.set(Calendar.YEAR, currentYear);
//            if (now.before(june)) {
//            	currentYear--;
//            }
            for (int k=currentYear;k>=firstYearOfOperation;k--) {
                data.addItem(Integer.toString(k),Integer.toString(k));
            }

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static String getDefaultYear() {
        // find out the current year
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        
        // if it is not yet June 1st, set current year to last year
        // since users will likely still be working on reports for
        // the previous year (Mantis 2772)
        GregorianCalendar june = new GregorianCalendar();
        june.set(Calendar.MONTH, Calendar.JUNE);
        june.set(Calendar.DAY_OF_MONTH, 1);
        june.set(Calendar.YEAR, currentYear);
        if (now.before(june)) {
        	currentYear--;
        }
        return Integer.toString(currentYear);
    	
    }
}
