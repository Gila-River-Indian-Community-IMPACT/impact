/*
 * Created on Dec 15, 2004 
 * <DL>
 * <DT><B>Copyright:</B></DT><DD>Copyright 2004 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT><DD>Mentorgen, LLC</DD>
 * </DL>
 * @version $Revision: 1.0
 * @author $Author: montotop
 * 
 */
package us.oh.state.epa.stars2.framework.util;

import java.util.ArrayList;
import java.util.Date;

/*
 * This class is used to initialize a duration objects so it has the holidays it
 * needs in order to properly perform calculations for business days.
 */
public class DurationHelper {
    private static ArrayList<Date> holidayDts = new ArrayList<Date>();

    private DurationHelper() {
    }

    /**
     * Update list of holidays.
     * 
     * @throws DAOException
     */
    /***************************************************************************
     * public void refresh () throws DAOException { InfrastructureDAO
     * infrastructureDAO =
     * (InfrastructureDAO)DAOFactory.getDAO("InfrastructureDAO"); HolidayDef[]
     * holidays = infrastructureDAO.retrieveHolidayDefs(); holidayDts = new
     * ArrayList<Date>(); for (int i=0; i<holidays.length; i++) {
     * holidayDts.add(holidays[i].getHolidayDt()); } }
     * 
     **************************************************************************/

    /**
     * Initialize a duration so business days can be calculated properly.
     * 
     * @param duration
     * @return
     */
    public final Duration init(Duration duration) {
        if (duration != null) {
            if (duration.isBusinessDays()) {
                duration.setHolidays(holidayDts.toArray(new Date[0]));
            }
        }
        return duration;
    }
}
