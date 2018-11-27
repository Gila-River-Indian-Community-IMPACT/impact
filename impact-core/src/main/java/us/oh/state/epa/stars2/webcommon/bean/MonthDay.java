package us.oh.state.epa.stars2.webcommon.bean;

import java.util.Calendar;
import java.util.LinkedHashMap;

public class MonthDay implements java.io.Serializable {
    LinkedHashMap<String, Integer> months;
    LinkedHashMap<Integer, Integer> days;
    Integer currentMonth;

    public MonthDay() {
        months = new LinkedHashMap<String, Integer>();

        months.put("January", Calendar.JANUARY);
        months.put("February", Calendar.FEBRUARY);
        months.put("March", Calendar.MARCH);
        months.put("April", Calendar.APRIL);
        months.put("May", Calendar.MAY);
        months.put("June", Calendar.JUNE);
        months.put("July", Calendar.JULY);
        months.put("August", Calendar.AUGUST);
        months.put("September", Calendar.SEPTEMBER);
        months.put("October", Calendar.OCTOBER);
        months.put("November", Calendar.NOVEMBER);
        months.put("December", Calendar.DECEMBER);
    }

    public final LinkedHashMap<Integer, Integer> getDays() {
        days = new LinkedHashMap<Integer, Integer>();

        int numOfDays = 31;

        if (currentMonth != null) {
            if (currentMonth == Calendar.FEBRUARY) {
                numOfDays = 28;
            } else if ((currentMonth == Calendar.APRIL)
                    || (currentMonth == Calendar.JUNE)
                    || (currentMonth == Calendar.SEPTEMBER)
                    || (currentMonth == Calendar.NOVEMBER)) {
                numOfDays = 30;
            }
        }

        for (int i = 1; i <= numOfDays; i++) {
            days.put(new Integer(i), new Integer(i));
        }

        return days;
    }

    public final LinkedHashMap<String, Integer> getMonths() {
        return months;
    }

    public final Integer getCurrentMonth() {
        return currentMonth;
    }

    public final void setCurrentMonth(Integer currentMonth) {
        this.currentMonth = currentMonth;
    }
}
