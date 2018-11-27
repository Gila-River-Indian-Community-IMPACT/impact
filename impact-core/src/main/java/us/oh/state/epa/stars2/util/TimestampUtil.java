package us.oh.state.epa.stars2.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
 * Provides a convenient wrapper around java.sql.Timestamp to create
 * SQL-friendly Timestamp objects without relying on deprecated methods.
 */
public class TimestampUtil {
    private static Timestamp cetaMigrationDay = null;

    /*
     * FOR TESTING ONLY
     */
    public static void main(String[] args) {
        try {
            System.out.println(TimestampUtil.convertToTimestamp("1231",null, "MMM dd"));
        } catch (TimestampUtilException sue) {
            System.out.println(sue);
        }
        try {
            System.out.println(TimestampUtil.convertToTimestamp("0430",null, "MMM dd, yyyy"));
        } catch (TimestampUtilException sue) {
            System.out.println("error");
        }
        try {
            System.out.println(TimestampUtil.convertToTimestamp("0430",new Integer(1968), "MMM dd, yyyy"));
        } catch (TimestampUtilException sue) {
             System.out.println("error");
        }
    }
    
    public static final Timestamp setToNoonofDate(Timestamp inputTS) {
        if (inputTS != null) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(inputTS.getTime());
            c.set(Calendar.HOUR_OF_DAY,12);
            c.set(Calendar.MINUTE,0);
            c.set(Calendar.SECOND,0);
            c.set(Calendar.MILLISECOND, 0);
            inputTS.setTime(c.getTimeInMillis());
        }
        return inputTS;
    }
    
    public static boolean isCetaLegacy(Timestamp createdDt) {
        if(createdDt == null) return true;
        if(cetaMigrationDay == null) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR,2013);
            c.set(Calendar.MONTH,Calendar.JANUARY);
            c.set(Calendar.DAY_OF_MONTH, 23);
            c.set(Calendar.HOUR_OF_DAY,12);
            cetaMigrationDay = new Timestamp(c.getTimeInMillis());
        }
        return createdDt.before(cetaMigrationDay);
    }
    
    public static final Timestamp setToStartofDate(Timestamp inputTS) {
        if (inputTS != null) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(inputTS.getTime());
            c.set(Calendar.HOUR_OF_DAY,0);
            c.set(Calendar.MINUTE,0);
            c.set(Calendar.SECOND,0);
            c.set(Calendar.MILLISECOND, 1);
            inputTS.setTime(c.getTimeInMillis());
        }
        return inputTS;
    }
    
    public static final Timestamp setToEndofDate(Timestamp inputTS) {
        if (inputTS != null) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(inputTS.getTime());
            c.set(Calendar.HOUR_OF_DAY,23);
            c.set(Calendar.MINUTE,59);
            c.set(Calendar.SECOND,59);
            c.set(Calendar.MILLISECOND, 999);
            inputTS.setTime(c.getTimeInMillis());
        }
        return inputTS;
    }
    
    public static final Timestamp setToStartofNextDate(Timestamp inputTS) {
        if (inputTS != null) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(inputTS.getTime());
            
            c.add(Calendar.DAY_OF_YEAR,1);
            c.set(Calendar.HOUR_OF_DAY,0);
            c.set(Calendar.MINUTE,0);
            c.set(Calendar.SECOND,1);
            c.set(Calendar.MILLISECOND, 0);
            inputTS.setTime(c.getTimeInMillis());
        }
        return inputTS;
    }
    
    public static final Timestamp setToEndofPreviousDate(Timestamp inputTS) {
        if (inputTS != null) {
          
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(inputTS.getTime());

            c.add(Calendar.DAY_OF_YEAR,-1);
            c.set(Calendar.HOUR_OF_DAY,23);
            c.set(Calendar.MINUTE,59);
            c.set(Calendar.SECOND,59);
            c.set(Calendar.MILLISECOND, 999);
            inputTS.setTime(c.getTimeInMillis());
        }
        return inputTS;
    }
    
    public static final Timestamp convertToTimestamp(String inputMMDD,Integer year) throws TimestampUtilException {
        try {
            int iMonth = Integer.parseInt(inputMMDD.substring(0, 2))-1;
            int iDay = Integer.parseInt(inputMMDD.substring(2));
            if (iMonth<1 || iMonth > 12) {
                throw new TimestampUtilException();
            }
            if (iDay < 1 || iDay > 31) {
                throw new TimestampUtilException();
            }
            int iYear=0;
            if (year==null) {
                Timestamp tmp = new Timestamp(System.currentTimeMillis());
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(tmp.getTime());
                iYear = c.get(Calendar.YEAR);
            } else {
                iYear = year.intValue();
            }
            Calendar cStartDt = Calendar.getInstance();
            cStartDt.set(iYear,iMonth,iDay,12,0,0);
            return new Timestamp(cStartDt.getTimeInMillis());     
        } catch (Exception e) {
            throw new TimestampUtilException();
        }
    }
    
    public static final String convertToTimestamp(String input,Integer year,String format) throws TimestampUtilException  {
       try {
            Timestamp ts = convertToTimestamp(input,year);
            SimpleDateFormat sdf = new SimpleDateFormat(format);  
            return sdf.format(ts);
       } catch (Exception e) {
           throw new TimestampUtilException();
       }
    }
}
