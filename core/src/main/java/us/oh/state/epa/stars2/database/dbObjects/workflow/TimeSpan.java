package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.Timestamp;

public class TimeSpan implements Comparable<TimeSpan>, java.io.Serializable {
    private static final long MIL_SECOND_PER_DAY = 24 * 60 * 60 * 1000;
    private int days;

    public TimeSpan(Timestamp endDt, Timestamp startDt) {
        if (endDt == null) {
            endDt = new Timestamp(System.currentTimeMillis());
        }

        // long sDays = startDt.getTime() / MIL_SECOND_PER_DAY;
        // long eDays = endDt.getTime() / MIL_SECOND_PER_DAY;

        // days = new Long(eDays - sDays).intValue();
        days = Math.round(endDt.getTime() / MIL_SECOND_PER_DAY
                - startDt.getTime() / MIL_SECOND_PER_DAY);

    }

    public final int getDays() {
        return days;
    }

    public final void setDays(int days) {
        this.days = days;
    }

    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(days);
        /*
         * if ( days <= 1 ) sb.append(" Day"); else sb.append(" Days");
         */

        return sb.toString();
    }

    public final int compareTo(TimeSpan o) {
        return this.days - o.getDays();
    }
}
