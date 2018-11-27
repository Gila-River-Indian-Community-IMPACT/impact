package us.oh.state.epa.stars2.framework.util;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on Dec 15, 2004
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
public class Duration {
    public static final int HOURS_IN_DAY = 24;
    public static final int MINUTES_IN_HOUR = 60;
    public static final int SECONDS_IN_MINUTE = 60;
    private int days;
    private int hours;
    private int minutes;
    private int seconds;
    private boolean isBusinessDays;
    private HashMap<Date, Date> holidays;
    private DecimalFormat minuteFormat = new DecimalFormat("00");
    private String message;

    public String toString() {
        return days + "d " + hours + ":" + minuteFormat.format(minutes);
    }

    /**
     * Parse input string str use it to initialize this duration object.
     * 
     * @param str
     * @return true if str parses, false otherwise
     */
    public final boolean fromString(String str) {
        boolean valid = false;
        // reset attributes
        reset();
        // try to match full format
        Pattern pattern = Pattern.compile("(\\d+)(bd|d)\\s+(\\d+):(\\d\\d)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        if (valid = matcher.matches()) {
            days = Integer.parseInt(matcher.group(1));
            String dayType = matcher.group(2);
            hours = Integer.parseInt(matcher.group(3));
            minutes = Integer.parseInt(matcher.group(4));
            isBusinessDays = dayType.equalsIgnoreCase("bd");
        } else {
            // try to match just hours and minutes
            pattern = Pattern.compile("(\\d+):(\\d\\d)");
            matcher = pattern.matcher(str);
            if (valid = matcher.matches()) {
                days = 0;
                hours = Integer.parseInt(matcher.group(1));
                minutes = Integer.parseInt(matcher.group(2));
                isBusinessDays = false;
            } else {
                // assume integer is days
                pattern = Pattern.compile("(\\d+)(bd|d)");
                matcher = pattern.matcher(str);
                if (valid = matcher.matches()) {
                    days = Integer.parseInt(matcher.group(1));
                    String dayType = matcher.group(2);
                    hours = 0;
                    minutes = 0;
                    isBusinessDays = dayType.equalsIgnoreCase("bd");
                } else {
                    valid = false;
                    message = "Format (bd = business day): 7d(bd) 5:20";
                }
            }
        }
        if (hours > HOURS_IN_DAY) {
            days = hours / HOURS_IN_DAY;
            hours = hours % HOURS_IN_DAY;
        }
        if (minutes > MINUTES_IN_HOUR) {
            valid = false;
            message = "Minutes over 60.";
        }

        return valid;
    }

    /**
     * Add the duration value represented by this class to beginDt. The
     * setHolidays method must be called before addToDate in order to set the
     * appropriate holidays if this duration is in business days.
     * 
     * @param beginDt
     * @return beginDt plus the time interval represented by this class
     */
    public final Date addToDate(Date beginDt) throws Exception {
        GregorianCalendar begin = new GregorianCalendar();
        begin.setTime(beginDt);
        GregorianCalendar end = (GregorianCalendar) begin.clone();

        // calculate end date based on begin and duration
        end.add(Calendar.DAY_OF_MONTH, days);
        end.add(Calendar.HOUR_OF_DAY, hours);
        end.add(Calendar.MINUTE, minutes);

        if (isBusinessDays) {
            // add extra days to account for weekends and holidays

            // step through days in duration interval, adding extra
            // days to account for holidays and weekends
            GregorianCalendar thisDay = (GregorianCalendar) begin.clone();

            // add one day to the end date for each weekend day or
            // holiday within the timespan from begin to end.
            while (thisDay.before(end)) {
                if (isWeekend(thisDay) || isHoliday(thisDay)) {
                    end.add(Calendar.DAY_OF_MONTH, 1);
                }
                thisDay.add(Calendar.DAY_OF_MONTH, 1);
            }

            // add more days if the end date is a weekend or holiday
            while (isWeekend(end) || isHoliday(end)) {
                end.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        return end.getTime();
    }

    /**
     * Subtract the duration value represented by this class from beginDt. The
     * setHolidays method must be called before subtractFromDate in order to set
     * the appropriate holidays if this duration is in business days.
     * 
     * @param beginDt
     * @return beginDt minus the time interval represented by this class
     */
    public final Date subtractFromDate(Date beginDt) throws Exception {
        GregorianCalendar begin = new GregorianCalendar();
        begin.setTime(beginDt);
        GregorianCalendar end = (GregorianCalendar) begin.clone();

        // calculate end date based on begin and duration
        end.add(Calendar.DAY_OF_MONTH, -days);
        end.add(Calendar.HOUR_OF_DAY, -hours);
        end.add(Calendar.MINUTE, -minutes);

        GregorianCalendar thisDay = (GregorianCalendar) begin.clone();
        if (isBusinessDays) {
            // subtract extra days to account for weekends and holidays

            // step through days in duration interval, subtracting extra
            // days to account for holidays and weekends

            // subtract one day from the end date for each weekend day or
            // holiday within the timespan from begin to end.
            while (thisDay.after(end)) {
                if (isWeekend(thisDay) || isHoliday(thisDay)) {
                    end.add(Calendar.DAY_OF_MONTH, -1);
                }
                thisDay.add(Calendar.DAY_OF_MONTH, -1);
            }
            // subtract more days if the end date is a weekend or holiday
            while (isWeekend(end) || isHoliday(end)) {
                end.add(Calendar.DAY_OF_MONTH, -1);
            }
        }

        return end.getTime();
    }
    
    /**
     * Compute the difference between beginDt and endDt in days. This method will
     * clear out this Duration object (by setting all values to 0) compute the
     * number of days between startDt and endDt, and return the difference as an
     * integer as well as setting the class's day field to the result. If beginDt
     * is not before endDt, this method will return 0 (i.e. it will never return a
     * negative value). The hours, minutes, and seconds values will be zero after
     * calling this method.
     * Note: the result of this method disregards the hours, minutes, seconds, etc.
     * associated with beginDt and endDt, basing the computation solely on the calendar
     * date.
     * @param beginDt
     * @param endDt
     * @return
     * @throws Exception
     */
    public final int computeDurationInDays(Date beginDt, Date endDt) throws Exception {
        days = 0;
        hours = 0;
        minutes = 0;
        seconds = 0;
        GregorianCalendar begin = new GregorianCalendar();
        begin.setTime(beginDt);
        begin.clear(Calendar.HOUR);
        begin.clear(Calendar.HOUR_OF_DAY);
        begin.clear(Calendar.MINUTE);
        begin.clear(Calendar.SECOND);
        begin.clear(Calendar.MILLISECOND);
        GregorianCalendar end = new GregorianCalendar();
        end.setTime(endDt);
        end.clear(Calendar.HOUR);
        end.clear(Calendar.HOUR_OF_DAY);
        end.clear(Calendar.MINUTE);
        end.clear(Calendar.SECOND);
        end.clear(Calendar.MILLISECOND);
        
        while (begin.before(end)) {
            if (isBusinessDays && (isWeekend(begin) || isHoliday(begin))) {
                continue;
            }
            days++;
            begin.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        return days;
    }

    /**
     * Define holidays to be considered in calculating new date from a Date and
     * a Duration that is in business days.
     * 
     * @param holidays
     */
    public final void setHolidays(Date[] holidays) {
        this.holidays = new HashMap<Date, Date>();
        for (int i = 0; i < holidays.length; i++) {
            this.holidays.put(holidays[i], holidays[i]);
        }
    }

    /**
     * @return Returns the days.
     */
    public final int getDays() {
        return days;
    }

    /**
     * @param days
     *            The days to set.
     */
    public final void setDays(int days) {
        this.days = days;
    }

    /**
     * @return Returns the hours.
     */
    public final int getHours() {
        return hours;
    }

    /**
     * @param hours
     *            The hours to set.
     */
    public final void setHours(int hours) {
        this.hours = hours;
    }

    /**
     * @return Returns the minutes.
     */
    public final int getMinutes() {
        return minutes;
    }

    /**
     * @param minutes
     *            The minutes to set.
     */
    public final void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    /**
     * @return Returns the seconds.
     */
    public final int getSeconds() {
        return seconds;
    }

    /**
     * @param seconds
     *            The seconds to set.
     */
    public final void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    /**
     * @return Returns the isBusinessDays.
     */
    public final boolean isBusinessDays() {
        return isBusinessDays;
    }

    /**
     * @param isBusinessDays
     *            The isBusinessDays to set.
     */
    public final void setBusinessDays(boolean isBusinessDays) {
        this.isBusinessDays = isBusinessDays;
    }

    /**
     * Test to see if date represented by day is a weekend day
     * 
     * @param day
     * @return true if day is a Saturday or Sunday, false otherwise
     */
    private boolean isWeekend(GregorianCalendar day) {
        return (day.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || day
                .get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
    }

    /**
     * Test to see if date represented by day is a holiday
     * 
     * @param day
     * @return true if day is a holiday, false otherwise
     * @throws Exception
     */
    private boolean isHoliday(GregorianCalendar day) throws Exception {
        if (holidays == null) {
            return false;
        }
        // wipe out time since holiday does not contain time info
        GregorianCalendar dayClone = (GregorianCalendar) day.clone();
        dayClone.clear(Calendar.HOUR);
        dayClone.clear(Calendar.HOUR_OF_DAY);
        dayClone.clear(Calendar.MINUTE);
        dayClone.clear(Calendar.SECOND);
        dayClone.clear(Calendar.MILLISECOND);
        Timestamp dayTs = new Timestamp(dayClone.getTimeInMillis());
        return (holidays.get(dayTs) != null);
    }

    private void reset() {
        days = 0;
        hours = 0;
        minutes = 0;
        seconds = 0;
        isBusinessDays = false;
    }

    public final String errorMessage() {
        return message;
    }
}
