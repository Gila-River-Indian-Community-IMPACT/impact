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
 * 'init-blank' with the value of 'yes';
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
public class DBStringEnumerationType extends AbstractType implements
        Enumeration {
    public static final String TABLE_NAME_KEY = "table-name";
    public static final String DESC_COL_KEY = "desc-col";
    public static final String CODE_COL_KEY = "code-col";
    public static final String INIT_BLANK = "init-blank";
    public static final String ORDER_BY = "order-by";
    public static final String WHERE = "where";
    // private StringLookup _lookup;
    private String desc;
    private String code;
    private boolean initBlank;

    // private String _order = null;

    public final void init(DataDetail dd) {
        super.init(dd);
        /*
         * if (where != null && where.compareTo("") != 0) { _lookup = new
         * StringLookup(table, desc, code, order, where); } else { _lookup = new
         * StringLookup(table, desc, code, order); }
         * 
         * String blank = getMeta(INIT_BLANK);
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
            this.code = null;
            valid(true);
            return;
        }
        /*
         * String code = _lookup.getCodeForDescription(value);
         * 
         * if (code == null) { StringBuffer b = new StringBuffer(" must have a
         * value"); b.append(value + "."); setErrorMessage(b.toString());
         * valid(false); } else { valid(true); this.code = code; }
         */
    }

    public final Object value() {
        return code;
    }

    /**
     * The parameter must be a java.lang.String. If it is not, the state of this
     * instance will not change, but it will become invalid. If this happens,
     * you can make the object valid by calling validate;
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
             * else if (object instanceof String) { String code = (String)
             * object; String desc = _lookup.getDescriptionForCode(code);
             * 
             * if (desc == null) { StringBuffer b = new StringBuffer("The code
             * "); b.append(code); b.append(" is not part of this enumeration");
             * setErrorMessage(b.toString()); valid(false); } else { desc =
             * desc; code = code; valid(true); } }
             */else {
            generateCannotCastMessage(object);
        }
    }

    public final String[] getValues() {
        /*
         * String[] descriptions = _lookup.getDescriptions(); String[] values;
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
         * String desc = _lookup.getDescriptionForCode(code);
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
        return String.class.getName();
    }
}
