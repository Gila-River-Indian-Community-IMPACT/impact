package us.oh.state.epa.stars2.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>
 * Title: FieldComparator
 * </p>
 * 
 * <p>
 * Description: FieldComparators work with data types to do value comparisons.
 * Data types are usually associated with custom or service detail data in the
 * database. The actual values are stored as strings. However, when we compare
 * these values, we want to convert them back to their "native" data type. The
 * FieldComparator does all of this.
 * </p>
 * 
 * <p>
 * The intended use of this class is to first call the factory method,
 * "create()" to create a concrete FieldComparator, specifying the type of
 * operation you wish to perform, for example "==" or "<=", etc. The string
 * values to be compared are then passed into the "compare()" method. It will do
 * the data conversion and then return "true" if the operation was satisfied.
 * </p>.
 * 
 * <p>
 * For example, suppose we have a data field name "foo" of type "Integer" and we
 * want to know if "foo" is less than or equal to "10". The following code
 * snippet would do that:
 * </p>
 * <p>
 * FieldComparator fc = fc.create (T_INTEGER, OP_LT_EQUALS) ; boolean result =
 * fc.compare (foo, "10") ;
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 * @version 1.0
 */

abstract public class FieldComparator {
    // NOTE: I have no idea how this next data type got into the system;
    // it does not correspond to any value in Data_Type_Def. However,
    // we got it and, fortunately, we know how to handle it.
    public static final String JL_STRING = "java.lang.String";
    // The following constants define the supported operations.
    public static final String OP_EQUALS = "=";
    public static final String OP_NOT_EQUALS = "!=";
    public static final String OP_LESS_THAN = "<";
    public static final String OP_LT_EQUALS = "<=";
    public static final String OP_GREATER_THAN = ">";
    public static final String OP_GT_EQUALS = ">=";
    // The following constants define the supported data types. These
    // correspond to values in the "data_type_dsc" field of the
    // Data_Type_Def table.
    public static final String T_STRING = "String";
    public static final String T_INTEGER = "Integer";
    public static final String T_FLOAT = "Float";
    public static final String T_DATE = "Date";
    public static final String T_ENUM = "Enum";
    private static HashMap<String, Integer> opMap = new HashMap<String, Integer>(); 
    private static HashMap<String, Integer> typeMap = new HashMap<String, Integer>();

    private String _operation; // The operation we are doing.

    /**
     * Constructor. The input operation should be equal to one of the "OP"
     * constants.
     * 
     * @param operation
     *            The operation to be performed.
     */
    protected FieldComparator(String operation) {
        this._operation = operation;
    }

    /**
     * Factory method to create a FieldComparator. The "type" identifies the
     * native data type for the comparison and should be equal to one of the
     * "T_" constants defined for this class. The "operation" identifies the
     * comparison operation to be performed and should be equal to one of the
     * "OP_" constants defined in this file. An exception is thrown if either
     * the "type" or the "operation" is unknown.
     * 
     * @param type
     *            Identifies the native data type.
     * @param operation
     *            Identifies the operation to be performed.
     * 
     * @return A FieldComparator object capable of performing the data
     *         comparison.
     * 
     * @throws java.lang.Exception
     *             Unknown "type" or "operation".
     */
    static public FieldComparator create(String type, String operation)
            throws Exception {
        FieldComparator ret = null;
        // Make sure the operation is valid. If not, it's an exception.

        if (!FieldComparator.validateOperation(operation)) {
            throw new Exception("The operation [" + operation
                    + "] is not valid.");
        }

        // Do a simple, "brute force" ripple test to create the right kind
        // of comparator.
        if (type.compareToIgnoreCase(T_STRING) == 0) {
            ret = new StringComparator(operation);
        } else if (type.compareToIgnoreCase(T_INTEGER) == 0) {
            ret = new IntegerComparator(operation);
        } else if (type.compareToIgnoreCase(T_FLOAT) == 0) {
            ret = new FloatComparator(operation);
        } else if (type.compareToIgnoreCase(T_DATE) == 0) {
            ret = new DateComparator(operation);
        } else if (type.compareToIgnoreCase(T_ENUM) == 0) {
            ret = new StringComparator(operation);
        } else if (type.compareToIgnoreCase(JL_STRING) == 0) {
            ret = new StringComparator(operation);
        } else { // Bad news....
            throw new Exception("Comparator type = [" + type + "] is not "
                    + "supported.");
        }
        
        return ret;
    }

    /**
     * Validates the <tt>String</tt> that represents the comparison operation.
     * Returns "true" if the "operation" is a recognized value. Otherwise,
     * returns "false". To be valid, the "operation" must be equal to one of the
     * "OP_" constants defined in this class.
     * 
     * @param operation
     *            Operation string.
     * 
     * @return boolean "true" if the operation is known.
     */
    static public boolean validateOperation(String operation) {
        boolean valid = false;

        // If we have this value in our operation map, then we recognize it.

        if (opMap.containsKey(operation)) {
            valid = true;
        }

        return valid;
    }

    /**
     * Compares "left" to "right" based on the "operation" passed in the
     * constructor. For example: "<left> <operation> <right>". The Strings
     * "left" and "right" are converted to an appropriate native data type and
     * then compared based on the operation given in the constructor. Returns
     * "true" if the operation was satisfied. Otherwise, returns "false". An
     * exception is thrown if either String could not be parsed to the
     * underlying data type, for example, we are performing an integer
     * comparison and one of the inputs strings is "foo".
     * 
     * @param left
     *            String representing the left value of the comparison.
     * @param right
     *            String representing the right value of the comparison.
     * 
     * @return boolean "true" if the comparison operation was satisfied.
     * 
     * @throws java.lang.Exception
     *             Could not parse input string.
     */
    @SuppressWarnings("unchecked")
    public boolean compare(String left, String right) throws Exception {
        // Convert the two strings to their native data types.

        Comparable cleft = this.convert(left);
        Comparable cright = this.convert(right);

        // Do the comparison. This will generate an integer value per the
        // standard Comparable interface.

        int cvalue = cleft.compareTo(cright);
        Integer opTag = FieldComparator.opMap.get(this._operation);

        // See what type of operation that was originally requested and
        // return that value to caller.

        switch (opTag.intValue()) {
        case 1:
            return (cvalue == 0);
        case 2:
            return (cvalue != 0);
        case 3:
            return (cvalue < 0);
        case 4:
            return (cvalue <= 0);
        case 5:
            return (cvalue > 0);
        case 6:
            return (cvalue >= 0);
        default:
            break;
        }

        return false; // Keep the compiler happy...
    }

    /**
     * Abstract method. Converts the String "s" into the native data type.
     * 
     * @param s
     *            String value to be converted.
     * 
     * @return Comparable A native datatype equivalent to "s".
     * 
     * @throws java.lang.Exception
     *             Could not parse "s".
     */
    protected abstract Comparable<? extends Object> convert(String s) throws Exception;

    static {
        // Map the operation strings to Integer values so we can use them
        // in the "switch" statement.

        opMap.put(OP_EQUALS, new Integer(1));
        opMap.put(OP_NOT_EQUALS, new Integer(2));
        opMap.put(OP_LESS_THAN, new Integer(3));
        opMap.put(OP_LT_EQUALS, new Integer(4));
        opMap.put(OP_GREATER_THAN, new Integer(5));
        opMap.put(OP_GT_EQUALS, new Integer(6));

        // Map the String type descriptor to an integer value. The string
        // descriptor is from the "data_type_dsc" column of the Data_Type_Def
        // table. The integer value is from the "data_type_id" of that
        // same table.

        typeMap.put(T_STRING, new Integer(1));
        typeMap.put(T_INTEGER, new Integer(2));
        typeMap.put(T_DATE, new Integer(3));
        typeMap.put(T_FLOAT, new Integer(4));
        typeMap.put(JL_STRING, new Integer(1));
    }
}

// Concrete Implementations of FieldComparators. I will bet that you can
// figure out what they do without any further sagacious commentary by me.

class IntegerComparator extends FieldComparator {
    IntegerComparator(String operation) {
        super(operation);
    }

    protected Comparable<Integer> convert(String s) throws Exception {
        return new Integer(s);
    }
}

class StringComparator extends FieldComparator {
    StringComparator(String operation) {
        super(operation);
    }

    protected Comparable<String> convert(String s) {
        return s;
    }
}

class FloatComparator extends FieldComparator {
    FloatComparator(String operation) {
        super(operation);
    }

    protected Comparable<Float> convert(String s) throws Exception {
        return new Float(s);
    }
}

class DateComparator extends FieldComparator {
    DateComparator(String operation) {
        super(operation);
    }

    protected Comparable<Date> convert(String s) throws Exception {
        DateFormat df = DateFormat.getInstance();
        Date d = df.parse(s);
        return d;
    }
}
