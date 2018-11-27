package us.oh.state.epa.stars2.scheduler;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimestampCalculator {

    public static Timestamp get(int days, Timestamp calculateOn) {
        GregorianCalendar begin = new GregorianCalendar();
        begin.setTime(calculateOn);

        // calculate end date based on begin and duration
        begin.add(Calendar.DAY_OF_MONTH, days);
        return new Timestamp(begin.getTimeInMillis());
    }

}
