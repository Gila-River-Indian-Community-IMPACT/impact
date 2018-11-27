package us.oh.state.epa.stars2.workflow.types;

import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;

/**
 * Requires the following keys:<br>
 * <ul>
 * <li>table-name</li>
 * <li>desc-col</li>
 * <li>code-col</li>
 * </ul>
 * If you'd like this enumeration to default to no item selected, add the key
 * 'init-blank' with the value of 'true';
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
public class DBNumericEnumerationType extends AbstractType implements
        Enumeration {
    public static final String TABLE_NAME_KEY = "table-name";
    public static final String DESC_COL_KEY = "desc-col";
    public static final String CODE_COL_KEY = "code-col";
    public static final String INIT_BLANK = "init-blank";
    public static final String WHERE = "where";
    // private NumericLookup lookup;
    private String desc;
    private Integer code;
    private boolean initBlank;

    public final void init(DataDetail dd) {
        super.init(dd);
        /*
         * if (where == null) { lookup = new NumericLookup(table, desc, code); }
         * else { lookup = new NumericLookup(table, desc, code, where); }
         * 
         * if (blank != null && blank.compareToIgnoreCase("yes") == 0) {
         * initBlank = true; }
         */
    }

    public final String toString() {
        return desc;
    }

    public final void fromString(String value) {
        desc = value;

        if ((value == null || value.length() == 0) && initBlank) {
            code = null;
            valid(true);
            return;
        }

        /*
         * Integer code = lookup.getCodeForDescription(value);
         * 
         * if (code == null) { StringBuffer b = new StringBuffer(" must have a
         * value"); b.append(value + "."); setErrorMessage(b.toString());
         * valid(false); } else { this.code = code; valid(true); }
         */
    }

    public final Object value() {
        return code;
    }

    /**
     * The parameter must be a java.lang.Integer. If it is not, the state of
     * this instance will not change, but it will become invalid. If this
     * happens, you can make the object valid by calling validate;
     */
    public final void value(Object object) {
        if (object == null) {
            if (initBlank) {
                desc = null;
                code = null;
                valid(true);
            } else {
                setErrorMessage(" cannot be set to empty.");
                valid(false);
            }
        } /*
             * else if (object instanceof Integer) { Integer code = (Integer)
             * object; String desc = lookup.getDescriptionForCode(code);
             * 
             * if (desc == null) { StringBuffer b = new StringBuffer("The code
             * "); b.append(code); b.append(" is not part of ");
             * setErrorMessage(b.toString()); valid(false); } else { this.desc =
             * desc; this.code = code; valid(true); } }
             */else {
            generateCannotCastMessage(object);
        }
    }

    public final String[] getValues() {
        /*
         * String[] descriptions = lookup.getDescriptions(); String[] values;
         * int index = 0;
         * 
         * if (initBlank) { values = new String[descriptions.length + 1];
         * values[0] = null; index = 1; } else { values = new
         * String[descriptions.length]; }
         * 
         * for (int i = 0; i < descriptions.length; i++) { values[index++] =
         * descriptions[i]; }
         * 
         * return values;
         */
        return new String[0];
    }

    public final boolean validate() {
        if (code == null) {
            valid(true);
            return true;
        }

        /*
         * String desc = lookup.getDescriptionForCode(code);
         * valid(desc.compareTo(desc) == 0);
         */
        return valid();
    }

    /**
     * Returns the name of the type of object used to store data.
     * 
     * @return String object class name.
     */
    public final String getObjectType() {
        return Integer.class.getName();
    }
}
