package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

public abstract class CetaBaseDB extends BaseDB {
	
    public boolean equals(Object obj) {
        return true;
    }
    
    public static String getScheduled(Timestamp scheduled) {
        String quarter = "";
        if(scheduled != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(scheduled.getTime());
            int yr = cal.get(Calendar.YEAR);
            int mon = cal.get(Calendar.MONTH);
            quarter = "Oct-Dec " + yr;
            if(mon < 9) quarter = "Jul-Sep " + yr;
            if(mon < 6) quarter = "Apr-Jun " + yr;
            if(mon < 3) quarter = "Jan-Mar " + yr;  
        }
        return quarter;
    }
    
    public static String getScheduledFFY(Timestamp scheduled) {
        String year = "????";
        if(scheduled == null) return year;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(scheduled.getTime());
        int yr = cal.get(Calendar.YEAR);
        int mon = cal.get(Calendar.MONTH);

        if(mon >= 9) yr++;  
        return Integer.toString(yr);
    }
    
    // Get timestamp representing the start of the FFY
    public static Timestamp beginningFFY(Timestamp d) {
        if(d == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(d.getTime()));
        if(cal.get(Calendar.MONTH) < Calendar.OCTOBER) {
            cal.add(Calendar.YEAR, -1);
            cal.set(Calendar.MONTH, Calendar.OCTOBER);
        }
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return quarterStart(new Timestamp(cal.getTimeInMillis()));
    }
    
    // Get timestamp representing the start of the scheduled quarter
    public static Timestamp quarterStart(Timestamp ts) {
        if(ts == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(ts.getTime()));
        int month = cal.get(Calendar.MONTH)/3 * 3;
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Timestamp ref = new Timestamp(cal.getTimeInMillis());
        ref.setNanos(0);
        return ref;
    }
    
    public String getUserNames(List<Evaluator> evaluators) {
        if(evaluators == null || evaluators.size() == 0) return null;
        StringBuffer sb = new StringBuffer();
        for(int i=0; i < evaluators.size(); i++) {
            Integer n = evaluators.get(i).getEvaluator();
            String s = null;
            if(n == null) continue;
            else {
                s = BasicUsersDef.getData().getItems().getDescFromAllItem(n);  
            }
            if(s == null) s = "UserId#" + evaluators.get(i).getEvaluator();
            sb.append(s);
            if(i < evaluators.size() - 1) sb.append("; ");
        }
        return sb.toString();
    }
    
    public String getDatesStrings(List<TestVisitDate> visitDates) {
        if(visitDates == null || visitDates.size() == 0) return null;
        StringBuffer sb = new StringBuffer();
        for(int i=0; i < visitDates.size(); i++) {
            Timestamp n = visitDates.get(i).getTestDate();
            sb.append(getDateStr(n));
            if(i < visitDates.size() - 1) sb.append(" ");
        }
        return sb.toString();
    }
    
    public static String getDateStr(Timestamp ts) {
        String dateStr = "";
        if(ts != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(ts.getTime());
            dateStr = (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.DAY_OF_MONTH) +
            "/" + cal.get(Calendar.YEAR);
        }
        return dateStr;
    }
    
    public static String trimString(String s) {
        if(s == null) return s;
        if(s.trim().length() == 0) return null;
        return s.trim();
    }
}
