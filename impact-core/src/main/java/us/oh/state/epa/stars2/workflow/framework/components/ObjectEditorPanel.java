package us.oh.state.epa.stars2.workflow.framework.components;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.framework.gui.DropDownInput;
import us.oh.state.epa.stars2.framework.gui.DurationInput;
import us.oh.state.epa.stars2.framework.gui.ErrorListener;
import us.oh.state.epa.stars2.framework.gui.FloatInput;
import us.oh.state.epa.stars2.framework.gui.GuiInput;
import us.oh.state.epa.stars2.framework.gui.IntegerInput;
import us.oh.state.epa.stars2.framework.gui.StringInput;

/**
 * <p>
 * Title: ObjectEditorPanel
 * </p>
 * 
 * <p>
 * Description: This object builds a GUI panel and allows an application to add
 * input components, for example, text fields, to accept user input. A variety
 * of pre-defined editor objects are available. In addition, the application can
 * also create its custom component and add that to the editor.
 * </p>
 * 
 * <p>
 * Objects constructed via the "addXXXEditor" methods will also automatically
 * update "dbObject" at an appropriate time. Custom objects will need to handle
 * object updates themselves.
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
 */
public class ObjectEditorPanel {
    private static Logger logger = Logger.getLogger(ObjectEditorPanel.class);
    protected Box mainPanel; // Holds the entire editor
    protected Box infoPanel; // Holds non-editable components
    protected Box editPanel; // Holds editable components
    private int infoCnt; // Number of Info objects added
    private int editCnt; // Number of edit object added
    private Object object; // The object being edited
    private HashMap<String, Object> propertyMap; // Property name to editor
                                                    // map
    private ArrayList<String> propertyNames; // List of property names
    private HashMap<String, Method> methodMap; // Java methods on object
    private boolean updated; // True => the object has changed
    private ErrorListener errorListener; // The thing that listens for errors

    /**
     * Constructor. "dbObject" is the object being edited. "listener" is a
     * listener object for error messages.
     * 
     * @param dbObject
     *            Object The object being edited.
     * @param listener
     *            ErrorListener Listener for error messages.
     */
    public ObjectEditorPanel(Object dbObject, ErrorListener listener) {
        errorListener = listener;
        object = dbObject;

        mainPanel = Box.createVerticalBox();
        // mainPanel.setBorder (new LineBorder (Color.GREEN)) ;

        // GuiInput objects that are not being edited go in the "Info" panel.

        infoPanel = Box.createVerticalBox();
        infoPanel.setBorder(new TitledBorder("Info Only"));
        mainPanel.add(infoPanel);

        // The GuiInput objects will go in the "edit" Panel.

        editPanel = Box.createVerticalBox();
        editPanel.setBorder(new TitledBorder("Edit"));
        mainPanel.add(editPanel);

        // We use these later for mapping properties to GuiInput objects.

        propertyMap = new HashMap<String, Object>();
        propertyNames = new ArrayList<String>();

        // Get an array of all the method names for "dbObject". Put them
        // in a HashMap for efficient searching.

        Class<? extends Object> objClass = dbObject.getClass();
        Method[] methods = objClass.getMethods();
        methodMap = ObjectEditorPanel.createMethodMap(methods);
    }

    /**
     * Returns the component that contains this editor panel. This is the
     * component that the application needs to add to its own editor window or
     * dialog.
     * 
     * @return Component Container for all objects in the editor.
     */
    public final Component getComponent() {
        return mainPanel;
    }

    /**
     * Returns "true" if the object passed in via the constructor has been
     * updated from user input values. Otherwise, returns "false".
     * 
     * @return boolean "True" if the object has been updated.
     */
    public final boolean isUpdated() {
        return updated;
    }

    /**
     * Returns the number of objects in the "Edit" section of the editor.
     * 
     * @return int The number of "Edit" objects.
     */
    public final int getEditCount() {
        return editCnt;
    }

    /**
     * This method should be called just prior to making the editor visible.
     * This method disables the "Info Only" and "Edit" sections if they don't
     * have any components. This avoids drawing an empty "box" in the editor.
     */
    public final void prepForEdit() {
        if (infoCnt == 0) {
            infoPanel.setVisible(false);
        }

        if (editCnt == 0) {
            editPanel.setVisible(false);
        }
    }

    /**
     * Returns the object edited by this dialog.
     * 
     * @return Object object
     */
    public final Object getObject() {
        return object;
    }

    /**
     * Creates a HashMap for fast lookup of <tt>Method</tt>. The HashMap uses
     * the method name as the key and the <tt>Method</tt> object as the value.
     * If "methods" is null or empty, then an empty HashMap is returned.
     * 
     * @param methods
     *            An array of Method objects.
     * 
     * @return HashMap where the key is the method name and the value is the
     *         <tt>Method</tt> object.
     */
    public static HashMap<String, Method> createMethodMap(Method[] methods) {
        // Create the HashMap. If the "methods" array is null, return the
        // empty HashMap.

        HashMap<String, Method> mapping = new HashMap<String, Method>();

        if (methods != null) {
            // For (each Method), get the method name. Put the method name ->
            // Method in the HashMap.
            for (Method m : methods) {
                mapping.put(m.getName(), m);
            }
        }

        return mapping;
    }

    /**
     * Makes a Date out of a Timestamp.
     * 
     * @param ts
     *            Timestamp to be converted to a Date.
     * 
     * @return Date.
     */
    public static Date makeDate(Timestamp ts) {
        Calendar cal = Calendar.getInstance();
        long millisecs = ts.getTime();
        cal.setTimeInMillis(millisecs);

        return cal.getTime();
    }

    /**
     * Makes a Timestamp out of a date.
     * 
     * @param d
     *            Date to be converted to a Timestamp.
     * 
     * @return Timestamp
     */
    public static Timestamp makeTimestamp(Date d) {
        long millisecs = d.getTime();
        return new Timestamp(millisecs);
    }

    /**
     * Sets field values in the editor from "dbObject". This is typically done
     * as part of creating and initializing the editor.
     */
    public final void setFieldsFromObject() {
        GuiInput t;
        String getMethodName;

        // For (Each field name in our DynamicObject), see if we can set this
        // value from "dbObject".
        for (String propertyName : propertyNames) {
            getMethodName = formatMethodName("get", propertyName);

            // Get the class name for the field's data value. See if we can
            // find a "get" method on "dbObject" that returns that type of
            // object, or a String.

            t = (GuiInput) (propertyMap.get(propertyName));
            String objType = t.getObjectType();

            if (objType.equals("java.util.Date")) {
                objType = "java.sql.Timestamp";
            }

            Method m = findGetMethod(getMethodName, objType);

            // If we found a get method, call it to return the value. Then
            // assign the value to the field.

            if (m != null) {
                Object getValue = callGetMethod(object, m);
                assignFieldValue(propertyName, getValue);
            }
        }
    }

    /**
     * Sets values in "dbObject" from field values in the editor. This should
     * typically be done whenever the user selects the "Apply" or "Ok" buttons.
     */
    public final void setObjectFromFields() {
        GuiInput t;
        String setMethodName;

        // For (Each field name in our DynamicObject), see if we can set this
        // value from "dbObject".

        for (String fieldName : propertyNames) {
            setMethodName = formatMethodName("set", fieldName);

            t = (GuiInput) (propertyMap.get(fieldName));

            if (!t.isEditable()) {// If "t" isn't editable, the user can't
                continue;
            }

            String objType = t.getObjectType();

            if (objType.equals("java.util.Date")) {
                objType = "java.sql.Timestamp";
            }

            Method m = findSetMethod(setMethodName, objType);

            // If we found a get method, call it to return the value. Then
            // assign the value to the field.

            if (m != null) {
                assignObjectValue(fieldName, m);
                updated = true;
            }
        }
    }

    /**
     * Copies a value from a GuiInput object to "dbObject". "fieldName" is used
     * to find the GuiInput object that has the value we want. It is assigned to
     * "dbObject" by calling Method "m" on that object. Returns "true" if the
     * assignment was successful, otherwise returns "false". Errors are added to
     * the log file.
     * 
     * @param fieldName
     *            The name of the field.
     * @param m
     *            The Method on "dbObject" to invoke.
     * 
     * @return boolean "True" if the assignment request succeeded.
     */
    protected final boolean assignObjectValue(String fieldName, Method m) {
        boolean result = false; // A pessimistic point of view...

        // Get the Type object for "fieldName" from our DynamicObject.
        // If something we need is not found, then skip this whole operation.

        GuiInput t = (GuiInput) (propertyMap.get(fieldName));

        if ((t == null) || (t.getValue() == null)) {
            ObjectEditorPanel.logger.error("No GuiInput object found for  "
                    + "field name = [" + fieldName + "].");
            return false;
        }

        // Get the object and its type for this field.

        Object fieldObj = t.getValue();
        String objType = t.getObjectType();

        // "Date" objects need to be converted to "Timestamp" objects.

        if (objType.equals("java.util.Date")) {
            Timestamp ts = ObjectEditorPanel.makeTimestamp((Date) fieldObj);
            fieldObj = ts;
        }

        if (fieldObj.equals("-1")) {
            fieldObj = new Integer(0);
        }

        // Construct an array to hold our input parameter.

        Object[] inputParams = new Object[1];
        inputParams[0] = fieldObj;

        // Invoke the "set" method on "object". Ignore any value it may
        // return.

        try {
            m.invoke(object, inputParams);
            result = true; // It worked!! (I am amazed...)
        } catch (IllegalAccessException iae) {
        	iae.printStackTrace();
            ObjectEditorPanel.logger.error(
                    "Exception while invoking method name = [" + m.getName()
                            + "].", iae);
        } catch (InvocationTargetException ie) {
        	ie.printStackTrace();
            ObjectEditorPanel.logger.error(
                    "Exception while invoking method name = [" + m.getName()
                            + "].", ie);
        }

        return result;
    }

    /**
     * Returns a "set" method on "dbObject" that has a single input parameter of
     * "paramType" and returns "void". For example, if we are looking for a
     * method named "setFoo(java.lang.Integer)", then returns that method or
     * "null" if no matching method is found.
     * 
     * @param methodName
     *            The name of the method we are looking for.
     * @param paramType
     *            The input parameter type we are looking for.
     * 
     * @return Method The method that matches name and input parameter type.
     */
    protected final Method findSetMethod(String methodName, String paramType) {
        // See if we can find this method name in the HashMap. If not, then
        // we are done.

        Method setMethod = methodMap.get(methodName);

        if (setMethod == null) {
            ObjectEditorPanel.logger.debug("No method named [" + methodName
                    + "] was found.");
            return null;
        }

        // See if this method takes "one" input parameter. If not, we don't
        // want to use it. Note that we don't care about the return type.

        Class<?>[] inputParams = setMethod.getParameterTypes();

        if (inputParams.length != 1) {
            ObjectEditorPanel.logger.debug("Method named [" + methodName
                    + "] was found, " + "but parameter count is not one.");
            return null;
        }

        // Check the name of the input parameter's class. If it matches the
        // type we are looking for, then this is our method.

        Class<?> cls = inputParams[0];
        String inParamType = cls.getName();

        if (inParamType.equals(paramType)) {
            return setMethod;
        }

        ObjectEditorPanel.logger
                .debug("Method named [" + methodName + "] was found, "
                        + "but input parameter was not correct type.");
        return null;
    }

    /**
     * Formats a field name into the name of a "get" or "set" method. The
     * "prefix" should either be "get" or "set". All of the naming conventions
     * of <code>DynamicObjectAdapter</code> are recognized by this method.
     * 
     * @param prefix
     *            Either "get" or "set".
     * @param fieldName
     *            The name of the field in "dbObject".
     * 
     * @return String formatted "get" or "set" method name.
     */
    protected final String formatMethodName(String prefix, String fieldName) {
        StringBuffer sb = new StringBuffer(100);
        sb.append(prefix);

        // Replace underbars, "", with dashes "-".

        fieldName = fieldName.replace('_', '-');

        // Tokenize the String, using "-" as the token separator. Start
        // each token with a capital letter and append it to StringBuffer.
        // For example, "accountId" becomes "AccountId" or "account-id"
        // becomes "AccountId".

        StringTokenizer token = new StringTokenizer(fieldName, "-");
        String segment;

        while (token.hasMoreTokens()) {
            segment = token.nextToken();
            String firstChar = segment.substring(0, 1);
            sb.append(firstChar.toUpperCase());
            sb.append(segment.substring(1));
        }

        // All done, ship it out.
        return sb.toString();
    }

    /**
     * Returns a "get" method on "dbObject" that matches "returnType" or
     * "String". For example, if we are looking for a method named "getFoo()"
     * that returns "java.lang.Integer", then matches on either "Integer
     * getFoo()" or "String getFoo()". Returns "null" if no matching method is
     * found.
     * 
     * @param methodName
     *            The name of the method we are looking for.
     * @param returnType
     *            The return type of the method we are looking for.
     * 
     * @return Method The method that matches name and return type.
     */
    protected final Method findGetMethod(String methodName, String returnType) {
        // See if we can find this method name in the HashMap. If not, then
        // we are done.

        Method getMethod = methodMap.get(methodName);

        if (getMethod == null) {
            ObjectEditorPanel.logger.debug("No method named [" + methodName
                    + "] was found.");
            return null;
        }

        // See if this method has "zero" input parameters. If it takes one
        // or more input parameters, we don't want to use it.

        Class<?>[] inputParams = getMethod.getParameterTypes();

        if (inputParams.length != 0) {
            ObjectEditorPanel.logger
                    .debug("Get method found, but had input parameters.  "
                            + "Method name = [" + methodName + "].");
            return null;
        }

        // Get the return type for the method. If it matches the type we
        // want or is a "String", then we can use it.

        String mRtnType = getMethod.getReturnType().getName();

        if ((mRtnType.equals(returnType))
                || (mRtnType.equals("java.lang.String"))) {
            return getMethod;
        }

        if ((returnType.equals("java.lang.String"))
                || (mRtnType.equals("java.lang.Integer"))) {
            return getMethod;
        }

        return null;
    }

    /**
     * Calls a "get" method identified by "m" on object "dbObject". Returns the
     * object returned by that "get" method. If this fails, an exception will be
     * logged and "null" will be returned.
     * 
     * @param dbObject
     *            Object with the value to be returned.
     * @param m
     *            Method that returns the value.
     * 
     * @return Object The value extracted from "dbObject".
     */
    protected final Object callGetMethod(Object dbObject, Method m) {
        Object dbValue = null;

        // Make the invocation call. If an exception is thrown, log it and
        // return "null".

        try {
            dbValue = m.invoke(dbObject, new Object[0]);
        } catch (IllegalAccessException iae) {
        	iae.printStackTrace();
            ObjectEditorPanel.logger.error(
                    "Exception while invoking method name = [" + m.getName()
                            + "].", iae);
        } catch (InvocationTargetException ie) {
        	ie.printStackTrace();
            ObjectEditorPanel.logger.error(
                    "Exception while invoking method name = [" + m.getName()
                            + "].", ie);
        }

        return dbValue;
    }

    /**
     * Assigns the object value "objValue" to its corresponding GuiInput object.
     * The "fieldName" identifies the the GuiInput that will be set to
     * "objValue".
     * 
     * @param fieldName
     *            The name of the field associated with the GuiInput object.
     * @param objValue
     *            The value to assign to the GuiInput object.
     */
    protected final void assignFieldValue(String fieldName, Object objValue) {
        // If the object value is null or empty, we have nothing to assign.

        if ((objValue == null) || (objValue.toString().length() == 0)) {
            ObjectEditorPanel.logger
                    .error("Attempt to assign NULL or emtpy value to "
                            + "field name = [" + fieldName + "].");
            return;
        }

        // Get the Type for "fieldName" from the DynamicObject. If this is
        // null, we have nothing to assign "objValue" to.

        GuiInput t = (GuiInput) (propertyMap.get(fieldName));

        if (t == null) {
            ObjectEditorPanel.logger.error("No GuiInput object found for  "
                    + "field name = [" + fieldName + "].");
            return;
        }

        // If we are looking for a "Date", convert the Timestamp from
        // "objValue" into a Date and assign it to the field. Otherwise,
        // use the String interface to set the value.

        String objType = t.getObjectType();

        if (objType.equals("java.util.Date")) {
            Date d = ObjectEditorPanel.makeDate((Timestamp) objValue);
            t.setValue(d);
        } else {
            String fieldValue = objValue.toString();
            t.setString(fieldValue);
        }
    }

    /**
     * Adds a drop-down editor to the dialog. This consists of a combo box
     * containing one or more "userSelections". The user may only select one of
     * the values. When a "userSelection" is selected, it automatically mapped
     * to a "mappedSelection" (if defined). Thus, a more descriptive user value
     * can automatically be mapped to a value more suitable for internal
     * persistence, i.e., a "code" or "id".
     * 
     * @param propertyName
     *            The name of this property in the associated "dbObject".
     * @param propertyLabel
     *            The label to display for this property.
     * @param initValue
     *            The initial value for this property.
     * @param userSelections
     *            The list of options the user may select.
     * @param mappedSelections
     *            What the user selections actually map to (can be null).
     * @param editable
     *            Set to "true" if the user can edit this field or "false" if
     *            the field is "display only".
     * @param required
     *            Set to "true" if a valid value is required for this field.
     * 
     * @return DropDownInput The object created by this method.
     */
    public final DropDownInput addDropDownEditor(String propertyName,
            String propertyLabel, String initValue, String[] userSelections,
            String[] mappedSelections, boolean editable, boolean required) {
        // Save the property name in our list of property names.

        propertyNames.add(propertyName);

        // Create the field and add our listener to it.

        DropDownInput ddInput = new DropDownInput(propertyLabel, initValue,
                userSelections, mappedSelections, editable, required);

        ddInput.addErrorListener(errorListener);

        JComponent jcomp = ddInput.getComponent();

        addComponent(jcomp, editable);

        // Save the field in our lookup map for later updates.

        propertyMap.put(propertyName, ddInput);
        return ddInput;
    }

    /**
     * Adds a "Yes/No" drop down editor to the dialog. This is a drop-down input
     * whose only selections are "Yes" and "No". "Yes" is mapped to the value
     * "Y" and "No" is mapped to the value "N".
     * 
     * @param propertyName
     *            The name of this property in the associated "dbObject".
     * @param propertyLabel
     *            The label to display for this property.
     * @param initValue
     *            The initial value for this property.
     * @param editable
     *            Set to "true" if the user can edit this field or "false" if
     *            the field is "display only".
     * @param required
     *            Set to "true" if a valid value is required for this field.
     * 
     * @return DropDownInput The object created by this method.
     */
    public final DropDownInput addYesNoEditor(String propertyName,
            String propertyLabel, String initValue, boolean editable,
            boolean required) {
        String[] userSelections = new String[2];
        userSelections[0] = "Yes";
        userSelections[1] = "No";

        String[] mappedSelections = new String[2];
        mappedSelections[0] = "Y";
        mappedSelections[1] = "N";

        return addDropDownEditor(propertyName, propertyLabel, initValue,
                userSelections, mappedSelections, editable, required);
    }

    /**
     * Creates a string input object in the ObjectEditor. Typically, these
     * objects just hold text. However, there is usually a limit to how much
     * characters are allowed (for example, in a database field), so that value
     * is specified as "maxCharCnt". If this criterion does not apply, set
     * "maxCharCnt" to null. The ObjectEditor maintains ownership over the
     * returned <code>StringInput</code> object. The reference is returned so
     * that the caller may attach listeners or adjust the object in response to
     * other user inputs.
     * 
     * @param propertyName
     *            The name of this property in the associated "dbObject".
     * @param propertyLabel
     *            The label to display for this property.
     * @param initValue
     *            The initial value for this property.
     * @param maxCharCnt
     *            Maximum number of characters to allow in this field.
     * @param editable
     *            Set to "true" if the user can edit this field or "false" if
     *            the field is "display only".
     * @param required
     *            Set to "true" if a valid value is required for this field.
     * 
     * @return StringInput The object created by this method.
     */
    public final StringInput addTextEditor(String propertyName, String propertyLabel,
            String initValue, Integer maxCharCnt, boolean editable,
            boolean required) {
        // Save the property name in our list of property names.

        propertyNames.add(propertyName);

        // Create the field and add our listener to it.

        StringInput strField = new StringInput(propertyLabel, initValue,
                maxCharCnt, editable, required);

        strField.addErrorListener(errorListener);

        JComponent jcomp = strField.getComponent();
        addComponent(jcomp, editable);

        // Save the field in our lookup map for later updates.

        propertyMap.put(propertyName, strField);
        return strField;
    }

    /**
     * Creates a DurationInput object in the ObjectEditor. Duration specifies
     * how long an Activity or Process is expected to take. This object is a bit
     * different in that visually it consists of two fields, a "Days" field and
     * an "hours" field. Internally, the data is stored as "seconds". Negative
     * numbers are not permitted in either field.
     * 
     * @param propertyName
     *            The name of this property in the associated "dbObject".
     * @param propertyLabel
     *            The label to display for this property.
     * @param initValue
     *            The initial value for this property, in seconds.
     * @param editable
     *            Set to "true" if the user can edit this field or "false" if
     *            the field is "display only".
     * @param required
     *            Set to "true" if a valid value is required for this field.
     * 
     * @return DurationInput The object created by this method.
     */
    public final DurationInput addDurationEditor(String propertyName,
            String propertyLabel, Integer initValue, boolean editable,
            boolean required) {
        // Save the property name in our list of property names.

        propertyNames.add(propertyName);

        // Create the field and add our listener to it.

        DurationInput durField = new DurationInput(propertyLabel, initValue,
                editable, required);

        durField.addErrorListener(errorListener);

        JComponent jcomp = durField.getComponent();
        addComponent(jcomp, editable);

        // Save the field in our lookup map for later updates.

        propertyMap.put(propertyName, durField);
        return durField;
    }

    /**
     * Adds an integer editor box to the dialog. This editor object allows the
     * user to enter an integer value. The input is verified to be a legal
     * integer value. Optional minimum and maximum values may be specified.
     * Range checking is always inclusive; for example, if the minimum value is
     * set to "1", then any integer value >= 1 will satisfy that criterion. If a
     * range value does not apply, set its value to "null". The ObjectEditor
     * maintains ownership over the returned <code>IntegerInput</code> object.
     * The reference is returned so that the caller may attach listeners or
     * adjust the object in response to other user inputs.
     * 
     * @param propertyName
     *            The name of this property in the associated "dbObject".
     * @param propertyLabel
     *            The label to display for this property.
     * @param initValue
     *            The initial value for this property.
     * @param minValue
     *            The minimum allowable value for this property.
     * @param maxValue
     *            The maximum allowable value for this property.
     * @param editable
     *            Set to "true" if the user can edit this field or "false" if
     *            the field is "display only".
     * @param required
     *            Set to "true" if a valid value is required for this field.
     * 
     * @return IntegerInput The object created by this method.
     */
    public final IntegerInput addIntegerEditor(String propertyName,
            String propertyLabel, Integer initValue, Integer minValue,
            Integer maxValue, boolean editable, boolean required) {
        // Save the property name in our list of property names.

        propertyNames.add(propertyName);

        // Create the field and add our listener to it.

        IntegerInput intField = new IntegerInput(propertyLabel, initValue,
                minValue, maxValue, editable, required);

        intField.addErrorListener(errorListener);

        JComponent jcomp = intField.getComponent();
        addComponent(jcomp, editable);

        // Save the field in our lookup map for later updates.

        propertyMap.put(propertyName, intField);
        return intField;
    }

    /**
     * Adds a floating point editor box to the dialog. This editor object allows
     * the user to enter a numeric value. The input is verified to be a legal
     * floating point value. Optional minimum and maximum values may be
     * specified. Range checking is always inclusive; for example, if the
     * minimum value is set to "1.3", then any value >= 1.3 will satisfy that
     * criterion. If a range value does not apply, set its value to "null". The
     * ObjectEditor maintains ownership over the returned
     * <code>FloatInput</code> object. The reference is returned so that the
     * caller may attach listeners or adjust the object in response to other
     * user inputs.
     * 
     * @param propertyName
     *            The name of this property in the associated "dbObject".
     * @param propertyLabel
     *            The label to display for this property.
     * @param initValue
     *            The initial value for this property.
     * @param minValue
     *            The minimum allowable value for this property.
     * @param maxValue
     *            The maximum allowable value for this property.
     * @param editable
     *            Set to "true" if the user can edit this field or "false" if
     *            the field is "display only".
     * @param required
     *            Set to "true" if a valid value is required for this field.
     * 
     * @return IntegerInput The object created by this method.
     */
    public final FloatInput addFloatEditor(String propertyName, String propertyLabel,
            Float initValue, Float minValue, Float maxValue, boolean editable,
            boolean required) {
        // Save the property name in our list of property names.

        propertyNames.add(propertyName);

        // Create the field and add our listener to it.

        FloatInput fltField = new FloatInput(propertyLabel, initValue,
                minValue, maxValue, editable, required);

        fltField.addErrorListener(errorListener);

        JComponent jcomp = fltField.getComponent();
        addComponent(jcomp, editable);

        // Save the field in our lookup map for later updates.

        propertyMap.put(propertyName, fltField);
        return fltField;
    }

    /**
     * This method should be used to add a component to add an editor component
     * to the dialog. The reason this method should be used is panel management.
     * There are two panels in the dialog: one for non-editable components,
     * i.e., "information only", and another for editable components. This
     * method makes sure the component is added to the right panel and keeps
     * track of the total number of components in each panel. If a panel is
     * empty, then it is not shown to the user.
     * 
     * @param component
     *            The component to be added.
     * @param editable
     *            "True" if the component is editable.
     */
    public final void addComponent(Component component, boolean editable) {
        if (editable) {
            editPanel.add(component);
            editCnt++;
        } else {
            infoPanel.add(component);
            infoCnt++;
        }
    }

    /**
     * Validates the contents of the dialog in response to an "Apply" button or
     * "Ok" button selection. Basically, we do three things: (1) Validate the
     * contents of whatever component currently has keyboard focus. (2) Make
     * sure that any field that requires a value has a value. (3) Give the
     * derived class the opportunity to perform its own validation of the data.
     * Returns "true" if all validations were successful, otherwise returns
     * "false".
     * 
     * @return boolean "True" if the contents of the dialog are valid.
     */
    public final boolean validateContents() {
        String name;
        int i;

        // For (each field in the dialog edit area) validate it.

        for (i = 0; i < propertyNames.size(); i++) {
            name = propertyNames.get(i);
            GuiInput gtype = (GuiInput) (propertyMap.get(name));

            // The object that currently has keyboard focus probably hasn't
            // had a chance to be validated. Do that now.

            if (gtype.isFocusOwner()) {
                if (!gtype.validateValue()) {
                    String errMsg = gtype.getErrorMessage();
                    setErrorMessage(gtype, errMsg);
                    return false;
                }
            }

            // If this field requires a value, make sure it has one.

            if (gtype.requiresValue() && !gtype.isAssigned()) {
                String msg = "Field [" + gtype.getLabel()
                        + "] requires a value.";
                setErrorMessage(gtype, msg);
                return false;
            }
        }

        // Let the derived class take a shot at validation.

        return derivedValidate();
    }

    private void setErrorMessage(GuiInput inpObject, String errMsg) {
        errorListener.setErrorMessage(inpObject, errMsg);
    }

    /**
     * Framework method. Allows the derived class to perform any cross-field
     * validations, etc., it would like to perform. This is done following final
     * field-level validation whenever the user selects the "Apply" or "Ok"
     * buttons. The derived class should return "true" if validation was
     * successful. If validation was not successful, the derived class should
     * place an error message in the status area and return "false". The
     * implementation provided by this class simply returns "true".
     * 
     * @return boolean "true" if validatation was successful.
     */
    private boolean derivedValidate() {
        return true;
    }
}
