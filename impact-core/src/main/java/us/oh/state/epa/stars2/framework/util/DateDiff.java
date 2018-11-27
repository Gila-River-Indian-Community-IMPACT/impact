package us.oh.state.epa.stars2.framework.util;

import java.util.Date;

/**
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2004 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version $Revision: 1.0
 * @author $Author: montotop
 * 
 */
public class DateDiff {
    private static final long MillisInSec = 1000;
    private static final long SecsInMin = 60;
    private static final long SecsInHr = 60 * SecsInMin;
    private static final long SecsInDay = 24 * SecsInHr;

    public static Duration getDiff(Date startDt, Date endDt) {
        Duration duration = null;
        if (startDt != null && endDt != null) {
            duration = new Duration();
            long diffSecs = (endDt.getTime() - startDt.getTime()) / MillisInSec;
            duration.setDays(new Long(diffSecs / SecsInDay).intValue());
            duration.setHours(new Long((diffSecs % SecsInDay) / SecsInHr)
                    .intValue());
            duration.setMinutes(new Long((diffSecs % SecsInHr) / SecsInMin)
                    .intValue());
            duration.setSeconds(new Long(diffSecs % SecsInMin).intValue());
        }
        return duration;
    }
}
