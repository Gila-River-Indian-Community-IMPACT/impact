package us.oh.state.epa.stars2.workflow.types;

import java.text.DecimalFormat;

import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;
import us.oh.state.epa.stars2.framework.util.Duration;

/**
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @author Sam Wooster
 */
public class DurationType extends AbstractType {
    public static final int HOURS_IN_DAY = 24;
    protected final DecimalFormat minuteFormat = new DecimalFormat("00");

    public final void init(DataDetail dd) {
        super.init(dd);
        valid(true);
    }

    public final void fromString(String value) {
        stringValue = value;

        if (value == null || value.trim().length() == 0) {
            super.fromString(value);
        } else {
            Duration duration = new Duration();
            valid(duration.fromString(stringValue));
            setErrorMessage(duration.errorMessage());
            objectValue = duration;
        }
    }

    public final void value(Object object) {
        if (object == null) {
            super.value(object);
        } else if (object instanceof String) {
            this.fromString((String) object);
        } else if (object instanceof Duration) {
            objectValue = object;
            stringValue = objectValue.toString();
        } else {
            generateCannotCastMessage(object);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mentorgen.framework.framework.dynamicObject.Type#getObjectType()
     */
    public final String getObjectType() {
        return "com.mentorgen.framework.lib.util.Duration";
    }
}
