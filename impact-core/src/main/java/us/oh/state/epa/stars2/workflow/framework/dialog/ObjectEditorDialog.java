package us.oh.state.epa.stars2.workflow.framework.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import us.oh.state.epa.stars2.framework.gui.DropDownInput;
import us.oh.state.epa.stars2.framework.gui.DurationInput;
import us.oh.state.epa.stars2.framework.gui.ErrorListener;
import us.oh.state.epa.stars2.framework.gui.IntegerInput;
import us.oh.state.epa.stars2.framework.gui.StringInput;
import us.oh.state.epa.stars2.workflow.framework.components.ObjectEditorPanel;

/**
 * <p>
 * Title: ObjectEditorDialog
 * </p>
 * 
 * <p>
 * Description: This class is the base class for object editor dialogs. Object
 * editor dialogs allow an application to easily and quickly build a dialog
 * window to edit properties of an object.
 * </p>
 * 
 * <p>
 * This class provides the framework mechanisms needed to support this
 * operation. It is expected that a derived class will determine what fields are
 * actually edited and set up the dialog to do the editing.
 * <p>
 * 
 * <p>
 * The "propertyName" is used to associated an editor object with a field in
 * "dbObject". This class will automatically extract field values from
 * "dbObject" and update field values in "dbObject". How this works: Suppose the
 * "dbObject" has a Integer value named "integerValue". The property name for
 * this field is "integerValue". The ObjectEditor will automatically look for
 * "Integer getIntegerValue()" and "setIntegerValue(Integer)" or
 * "setIntegerValue(String)". If it finds these methods, they will be used for
 * automated behavior.
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
public class ObjectEditorDialog extends BaseDialog implements ErrorListener {
    protected Box buttonPanel; // Panel that holds the buttons
    protected JButton applyButton; // The "Apply" button
    protected JButton okButton; // The "Ok" button
    protected JButton cancelButton; // The "Cancel" button
    protected ObjectEditorPanel editPanel; // ObjectEditorPanel
    private JFrame parentFrame; // Frame Parent/owner window
    private JDialog parentDialog; // Dialog parent/owner window

    /**
     * Constructor. The "title" is the title displayed in the title bar of the
     * editor dialog. The "dbObject" is the object being edited by this dialog.
     * "Parent" is the parent window associated with this dialog.
     * 
     * @param title
     *            Dialog title.
     * @param dbObject
     *            Object being edited.
     * @param parent
     *            Parent window.
     */
    protected ObjectEditorDialog(String title, Object dbObject, JFrame parent) {
        super(parent, title, true);
        parentFrame = parent;
        initComponents(dbObject);
    }

    /**
     * Constructor. The "title" is the title displayed in the title bar of the
     * editor dialog. The "dbObject" is the object being edited by this dialog.
     * "Parent" is the parent window associated with this dialog.
     * 
     * @param title
     *            String Dialog title.
     * @param dbObject
     *            Object Object being edited.
     * @param parent
     *            JDialog Parent window.
     */
    protected ObjectEditorDialog(String title, Object dbObject, JDialog parent) {
        super(parent, title, true);
        parentDialog = parent;
        initComponents(dbObject);
    }

    /**
     * Private portion of the constructor. Creates all of the visual components
     * for the editor diaolog.
     * 
     * @param dbObject
     *            Object Object being edited.
     */
    private void initComponents(Object dbObject) {
        // Start building the "guts" of the dialog.

        Container contentPane = getContentPane();
        Box bb = Box.createVerticalBox();
        contentPane.add(bb);

        editPanel = new ObjectEditorPanel(dbObject, this);
        bb.add(editPanel.getComponent());

        // Buttons will go in the "button" panel.

        bb.add(Box.createVerticalStrut(5));

        buttonPanel = Box.createHorizontalBox();
        bb.add(buttonPanel);

        addButtons(buttonPanel);

        // Status and error messages will go in the "status" panel.

        bb.add(Box.createVerticalStrut(5));

        statusPanel = new JPanel();
        bb.add(statusPanel);
        LayoutManager smgr = new BorderLayout();
        statusPanel.setLayout(smgr);
        statusPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));

        JLabel temp = new JLabel();
        Font font = temp.getFont();

        // Use a JTextArea for status messages so that we can pre-allocated
        // three lines of display. Apparently, JLabel doesn't resize when
        // the text is too big for the current label area.

        statusMsg = new JTextArea();
        statusMsg.setFont(font);
        statusMsg.setEditable(false);
        statusMsg.setRows(3);
        statusPanel.add(statusMsg, BorderLayout.WEST);
    }

    /**
     * Returns the object edited by this dialog.
     * 
     * @return Object object
     */
    public final Object getObject() {
        return editPanel.getObject();
    }

    /**
     * Brings up the ObjectEdtior dialog for user editing. If one or more values
     * were changed by the user, returns "true". Otherwise, returns "false".
     * Note that the dialog is "modal", i.e., the rest of the application is
     * blocked until the dialog is dismissed.
     * 
     * @return boolean "True" if the user edited one or more field values.
     */
    public final boolean edit() {
        // Update GuiInput values from "dbObject".

        editPanel.prepForEdit();
        editPanel.setFieldsFromObject();
        int editCnt = editPanel.getEditCount();

        if (editCnt == 0) {
            applyButton.setEnabled(false);
            okButton.setEnabled(false);
        }

        // Position the editor dialog on top of the main application window.

        if (parentFrame != null) {
            Container parentPanel = parentFrame.getContentPane();
            setLocationRelativeTo(parentPanel);
        } else if (parentDialog != null) {
            Container parentPanel = parentDialog.getContentPane();
            setLocationRelativeTo(parentPanel);
        }

        // Make the dialog visible and usable by the user.

        pack();
        setVisible(true);

        // Tell caller if anything was updated.

        return editPanel.isUpdated();
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

        if (methods == null) {
            return mapping;
        }

        int i;
        Method m;
        String mName;

        // For (each Method), get the method name. Put the method name ->
        // Method in the HashMap.

        for (i = 0; i < methods.length; i++) {
            m = methods[i];
            mName = m.getName();
            mapping.put(mName, m);
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
        Date d = cal.getTime();

        return d;
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
        Timestamp ts = new Timestamp(millisecs);
        return ts;
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
    protected final DropDownInput addDropDownEditor(String propertyName,
            String propertyLabel, String initValue, String[] userSelections,
            String[] mappedSelections, boolean editable, boolean required) {
        return editPanel
                .addDropDownEditor(propertyName, propertyLabel, initValue,
                        userSelections, mappedSelections, editable, required);
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
    protected final DropDownInput addYesNoEditor(String propertyName,
            String propertyLabel, String initValue, boolean editable,
            boolean required) {
        return editPanel.addYesNoEditor(propertyName, propertyLabel, initValue,
                editable, required);
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
    protected final StringInput addTextEditor(String propertyName,
            String propertyLabel, String initValue, Integer maxCharCnt,
            boolean editable, boolean required) {
        return editPanel.addTextEditor(propertyName, propertyLabel, initValue,
                maxCharCnt, editable, required);
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
    protected final DurationInput addDurationEditor(String propertyName,
            String propertyLabel, Integer initValue, boolean editable,
            boolean required) {
        return editPanel.addDurationEditor(propertyName, propertyLabel,
                initValue, editable, required);
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
    protected final IntegerInput addIntegerEditor(String propertyName,
            String propertyLabel, Integer initValue, Integer minValue,
            Integer maxValue, boolean editable, boolean required) {
        return editPanel.addIntegerEditor(propertyName, propertyLabel,
                initValue, minValue, maxValue, editable, required);
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
    protected final boolean validateContents() {
        if (!editPanel.validateContents()) {
            return false;
        }
        // Let the derived class take a shot at validation.

        return derivedValidate();
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

    /**
     * Handles selection of the 'Apply' button. Updates values in "_object" with
     * the values the user entered in the GUI components.
     */
    protected final void apply() {
        setStatusMessage("Changes have been applied to the object.");

        editPanel.setObjectFromFields();

        if (parentFrame != null) {
            parentFrame.repaint();
        } else if (parentDialog != null) {
            parentDialog.repaint();
        }
    }

    /**
     * Adds our action buttons to "btnArea".
     * 
     * @param btnArea
     *            Container built to hold the buttons.
     */
    private void addButtons(Container btnArea) {
        applyButton = new JButton("Apply");
        applyButton.addActionListener(new ApplyListener(this));
        btnArea.add(applyButton);

        okButton = new JButton("Ok");
        okButton.addActionListener(new DoneListener(this));
        btnArea.add(okButton);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new CancelListener(this));
        btnArea.add(cancelButton);
    }

    /* ************* */
    /* Inner Classes */
    /* ************* */
    // 'Apply' button listener
    private static class ApplyListener implements ActionListener {
        private ObjectEditorDialog editor;

        ApplyListener(ObjectEditorDialog editor) {
            this.editor = editor;
        }

        public void actionPerformed(ActionEvent evt) {
            if (editor.validateContents()) {
                editor.apply();
            }
        }
    }

    // 'Ok' button listener

    private static class DoneListener implements ActionListener {
        private ObjectEditorDialog editor;

        DoneListener(ObjectEditorDialog editor) {
            this.editor = editor;
        }

        public void actionPerformed(ActionEvent evt) {
            if (editor.validateContents()) {
                editor.apply();
                editor.dispose();
            }
        }
    }

    // 'Cancel' button listener

    private static class CancelListener implements ActionListener {
        private ObjectEditorDialog editor;
        
        CancelListener(ObjectEditorDialog editor) {
            this.editor = editor;
        }

        public void actionPerformed(ActionEvent evt) {
            editor.dispose();
        }
    }
}
