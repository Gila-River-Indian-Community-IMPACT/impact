package us.oh.state.epa.stars2.workflow.types;

import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;

/**
 * The following keys are optional:
 * <ul>
 * <li>min-length</li>
 * <li>max-length</li>
 * </ul>
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @author Sam Wooster
 */
public class StringType extends AbstractType {
    private int minLength = -1;
    private int maxLength = -1;

    /**
     * @see AbstractType#init(DataDetail)
     */
    public final void init(DataDetail dd) {
        super.init(dd);

        String min = dd.getMinVal();
        String max = dd.getMaxVal();

        if (min != null) {
            minLength = new Integer(min).intValue();
        }

        if (max != null) {
            maxLength = new Integer(max).intValue();
        }

        stringValue = dd.getDataDetailVal();
        valid(true);
    }

    /**
     * @see AbstractType#fromString(String)
     */
    public final void fromString(String value) {
        stringValue = value;

        if (value == null || value.length() == 0) {
            super.fromString(value);
        } else if (minLength != -1 && stringValue.length() < minLength) {
            StringBuffer b = new StringBuffer(" must be at least ");
            b.append(minLength);
            b.append(" characters long.");
            setErrorMessage(b.toString());
            valid(false);
        } else if (maxLength != -1 && stringValue.length() > maxLength) {
            StringBuffer b = new StringBuffer(" can be no more than ");
            b.append(maxLength);
            b.append(" characters long.");
            setErrorMessage(b.toString());
            valid(false);
        } else {
            objectValue = stringValue;
            valid(true);
        }
    }

    /**
     * @see AbstractType#value(Object)
     */
    public final void value(Object object) {
        if (object == null) {
            super.value(object);
        } else if (object instanceof String) {
            String value = (String) object;
            fromString(value);
        } else {
            generateCannotCastMessage(object);
        }
    }

    /**
     * @see Type#getObjectType()
     */
    public final String getObjectType() {
        return String.class.getName();
    }
}
