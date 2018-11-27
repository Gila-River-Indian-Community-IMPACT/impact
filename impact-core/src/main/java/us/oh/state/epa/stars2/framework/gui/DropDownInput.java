package us.oh.state.epa.stars2.framework.gui;

import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.apache.log4j.Logger;

/**
 * <p>
 * Title: DropDownInput
 * </p>
 * 
 * <p>
 * Description: This input object presents a fixed set of options that allows
 * the user to select one option. The user does not have the ability to enter
 * new options or edit existing options.
 * </p>
 * 
 * <p>
 * This class supports mappings between what the user selects versus what needs
 * to stored in a persistent object. For example, database fields often map
 * between a "code" or "Id" that is stored in an object and a more user-friendly
 * "description". In this case, we might want to display the "description" to
 * the user, but have the "code" returned as the value selected. To do this, the
 * "userSelections" would be the array of descriptions and the
 * "mappedSelections" would be the array of "code" or "Id" values. If these are
 * one and the same, i.e., you simply want to return the "userSelection", then
 * enter "null" for the "mappedSelection".
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

public class DropDownInput extends AbstractInput {
    private static Logger logger = Logger.getLogger("DropDownInput");
    private JComboBox comboBox;
    private String initValue;
    private String[] userSelections;
    private HashMap<String, String> selectionMap; // Maps user selection to
    private HashMap<String, String> keyValueMap; // Maps "mapped" to user
    /**
     * Constructor. The "propertyLabel" is the prefix label applied to the
     * drop-down field. The "initValue" should be a value in the list of
     * "userSelections", but this is not enforced. The "userSelections" is the
     * set of values displayed to the user. "mappedSelections" is an optional
     * set of values that the user selection maps to. Thus, if the second item
     * in user selections is selected, then the second item in
     * "mappedSelections" is returned. The "mappedSelections" should either be
     * <code>null</code>, if the mapping is not needed (then the user
     * selection is returned), or have the same number of elements as
     * "userSelections (a one-to-one mapping). If the one-to-one mapping
     * constraint is not adhered to, then the mapping is ignored (and an error
     * message is added to the log file) and the user selection is returned
     * directly to the caller.
     * 
     * @param propertyLabel
     *            Property label.
     * @param initValue
     *            Initial value.
     * @param userSelections
     *            List of selections displayed to the user.
     * @param mappedSelections
     *            Optional list of mapping selections.
     * @param editable
     * 
     * @param required
     *            "True" if this field is required.
     */
    public DropDownInput(String propertyLabel, String initValue,
            String[] userSelections, String[] mappedSelections,
            boolean editable, boolean required) {
        super(propertyLabel, editable, required);
        replaceValues(initValue, userSelections, mappedSelections);
    }

    /**
     * Replaces the current contents of the drop-down object with new values.
     * Often, the contents of a drop-down input object depend on other values.
     * This method allows the application to alter the contents of the drop-down
     * as needed.
     * 
     * @param initValue
     *            String Initial value.
     * @param userSelections
     *            String[] List of selections displayed to the user.
     * @param mappedSelections
     *            String[] Optional list of mapping selections.
     */
    public final void replaceValues(String inInitValue,
            String[] inUserSelections, String[] mappedSelections) {
        // First, save the new values in the corresponding data members.

        this.initValue = inInitValue;
        this.userSelections = inUserSelections;
        selectionMap = createSelectionMap(userSelections, mappedSelections);

        keyValueMap = createSelectionMap(mappedSelections, userSelections);

        // If we have already created the Swing component, we need to update
        // it. Set the "frame validation" flag to "true" so that event
        // notifications are disabled.

        if (comboBox != null) {
            frameValidation = true;

            comboBox.removeAllItems();

            if ((userSelections != null) && (userSelections.length > 0)) {
                int i;
                for (i = 0; i < userSelections.length; i++) {
                    comboBox.addItem(userSelections[i]);
                }

                // Re-enable event notifications.

                frameValidation = false;
            }
        }
    }

    /**
     * Framework method.
     * 
     * @see GuiInput.getObjectType().
     * 
     * @return String the class name of the object returned by this object.
     */
    public final String getObjectType() {
        return String.class.getName();
    }

    /**
     * Sets the string value of this object to "str". In addition, looks up the
     * "mapped" selection for this value and sets the object value to the mapped
     * value. If "str" does not map to any mapped value, both the string and
     * object values will be set to "null" and an error will be logged.
     * 
     * @param str
     *            String A user selection.
     */
    public final void setStringValue(String str) {
        String mappedValue = selectionMap.get(str);

        // If "str" didn't map to anything, something is screwy...

        if (mappedValue == null) {
            DropDownInput.logger.error("User selection = [" + str
                    + "] was not found.");
            super.setStringValue(null);
            super.setObjectValue(null);
            return;
        }

        super.setStringValue(str);
        super.setObjectValue(mappedValue);
    }

    /**
     * Sets the object value of this object to "obj". In addition, looks up the
     * "user" selection for this value and sets the string value to the user
     * value. If "obj" does not map to any known value, both the string and
     * object values will be set to "null" and an error will be logged.
     * 
     * @param obj
     *            The String "code" or "Id" value.
     */
    public final void setObjectValue(Object obj) {
        if (obj instanceof String) {
            String key = (String) obj;
            String userValue = keyValueMap.get(key);

            // If "key" didn't map to anything, something is screwy...
            if (userValue == null) {
                DropDownInput.logger.error("Key selection = [" + key
                        + "] was not found.");
                super.setStringValue(null);
                super.setObjectValue(null);
            } else {
                super.setStringValue(userValue);
                super.setObjectValue(obj);
            }
        }
    }

    /**
     * Framework method.
     * 
     * @see AbstractInput.getVisual().
     * 
     * @return JComponent The text field used to accept string input.
     */
    protected final JComponent getVisual() {
        comboBox = new JComboBox();

        if ((userSelections != null) && (userSelections.length > 0)) {
            comboBox = new JComboBox(userSelections);
            comboBox.setEditable(isEditable());

            String initStr = initValue;

            if ((initStr == null) || (initStr.length() == 0)) {
                initStr = userSelections[0];
                comboBox.setSelectedItem(initStr);
                setStringValue(initStr);
            } else {
                String userSelection = keyValueMap.get(initStr);
                comboBox.setSelectedItem(userSelection);
                setObjectValue(initStr);
            }
        }

        return comboBox;
    }

    /**
     * Framework method.
     * 
     * @see AbstractInput.createVerifier(JComponent).
     * 
     * @param component
     *            Basically the same component we gave to the framework.
     * 
     * @return AbstractVerifier The object used to verify the user's input.
     */
    protected final AbstractVerifier createVerifier(JComponent component) {
        DropDownVerifier ddv = new DropDownVerifier();
        comboBox.addActionListener(ddv);
        return ddv;
    }

    /**
     * Creates a HashMap that maps user selections to their mapped values.
     * 
     * @param keys
     *            The user selections.
     * @param values
     *            The mapped values.
     * 
     * @return HashMap The map.
     */
    protected final HashMap<String, String> createSelectionMap(String[] keys,
            String[] values) {
        HashMap<String, String> map = new HashMap<String, String>();
        String[] vv = values;

        // If the caller is stoopid, log an error and return an empty HashMap.
        if ((keys != null) && (keys.length > 0)) {

            // If there are no mapped values (which is okay), then simply map
            // the user selections to themselves.

            if (values == null) {
                DropDownInput.logger
                        .debug("No mappedSelections were specified; using keys.");
                vv = keys;
            }

            // Else if (the user selections and mapped selections don't have the
            // same number of elements, i.e., the caller is an idiot, log an
            // error message and ignore the mappedSelections.

            else if (values.length != keys.length) {
                DropDownInput.logger
                        .error("userSelections array length does not match "
                                + "mappedSelections array length; using keys");
                vv = keys;
            }

            int i;
            String sk;
            String sv;

            // Whatever we decided to map, do it now.

            for (i = 0; i < keys.length; i++) {
                sk = keys[i];
                sv = vv[i];
                map.put(sk, sv);
            }
        } else {
            DropDownInput.logger.error("Null or empty userSelections array.");
        }

        return map;
    }

    protected final boolean setVisualValue() {
        boolean ret = false;

        if (super.setVisualValue()) {
            String userSelection = keyValueMap.get(getValue());
            JComboBox jcb = (JComboBox) (this.visual);
            jcb.setSelectedItem(userSelection);
            ret = true;
        }

        return ret;
    }
}
