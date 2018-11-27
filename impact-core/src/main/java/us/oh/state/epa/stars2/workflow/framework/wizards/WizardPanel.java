package us.oh.state.epa.stars2.workflow.framework.wizards;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.workflow.framework.components.ObjectEditorPanel;

/**
 * <p>
 * Title: WizardPanel
 * </p>
 * 
 * <p>
 * Description: This is an abstract base class for all wizard panels. The wizard
 * contains one or more WizardPanels. The user selects the "Next" and "Previous"
 * buttons to navigate among the panels.
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

abstract public class WizardPanel {
    protected JPanel bufferPanel;
    protected JPanel instructionsPanel;
    protected ObjectEditorPanel editorPanel;
    private boolean newObject;
    private Wizard wizard;
    private Object nextPanelDescriptor;

    /**
     * Constructor. The "wizard" is the container for this panel. The
     * "newObject" should be set to "true" if creating a new object or "false"
     * if editing an existing object. The "nextPanelDescriptor" identifies the
     * next panel in the wizard (for static panel linkage). If the derived class
     * needs to determine the next panel Id from it's contents, it should set
     * this value to "null" here and overload the "getNextPanelDescriptor()"
     * method.
     * 
     * @param object
     *            Object The object being constructed by this wizard.
     * @param wizard
     *            Wizard The wizard container for his panel.
     * @param newObject
     *            boolean "true" if creating a new object.
     * @param nextPanelDescriptor
     *            Object Usually, this is a String.
     */
    protected WizardPanel(Object object, Wizard wizard, String instrLocation,
            boolean newObject, Object nextPanelDescriptor) {
        this.wizard = wizard;
        this.newObject = newObject;
        this.nextPanelDescriptor = nextPanelDescriptor;

        bufferPanel = new JPanel();
        LayoutManager mainLayout = new GridBagLayout();
        bufferPanel.setLayout(mainLayout);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 100;
        gbc.weighty = 100;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        instructionsPanel = new JPanel();
        instructionsPanel.setLayout(new BoxLayout(instructionsPanel,
                BoxLayout.Y_AXIS));
        bufferPanel.add(instructionsPanel, gbc);
        gbc.gridx = 1;

        bufferPanel.add(Box.createHorizontalStrut(20), gbc);
        gbc.gridx = 2;

        editorPanel = new ObjectEditorPanel(object, wizard);
        bufferPanel.add(editorPanel.getComponent(), gbc);

        // Load the instructions.

        if (instrLocation != null) {
            Node[] tempNodes = Config.findNodes(instrLocation);

            for (Node tempNode : tempNodes) {
                String tempStr = tempNode.getText();
                addInstructions(tempStr);
            }
        }
    }

    /**
     * Returns the actual visible component of the wizard. This will consist of
     * a two-column panel with instructions in the left column and editor
     * objects in the right column.
     * 
     * @return Component The unique part of this panel.
     */
    public final Component getComponent() {
        return bufferPanel;
    }

    /**
     * Framework method. Called whenever the panel is about to be displayed. In
     * this case, adjusts the editor panel to hide any unused components and
     * copies values from the object being edited to the fields in the edit
     * panel. Derived classes that overload this method should execute this
     * method from within their own version.
     */
    public void aboutToDisplayPanel() {
        editorPanel.prepForEdit();
        editorPanel.setFieldsFromObject();
    }

    /**
     * The "displayPanel()" method is called whenever the wizard panel is being
     * displayed. This method is called after "aboutToDisplayPanel()". The
     * default implementation does nothing.
     */
    public void displayingPanel() {
    }

    /**
     * Allows the derived object to validate its contents before transitioning
     * to either the next panel or finishing the wizard. If the derived object
     * determines that its current contents are not valid, it should display an
     * error message in the wizard status message window and return "false". The
     * default implementation verifies that all required fields have values.
     * 
     * @return boolean "true" if the contents of this panel are valid.
     */
    public boolean validateContents() {
        if (!editorPanel.validateContents()) {
            return false;
        }

        editorPanel.setObjectFromFields();
        return true;
    }

    /**
     * The "aboutToHidePanel()" is called whenever the wizard panel is about to
     * be replaced, either by a "Back" or "Next" action, or the user has
     * selected the "Finish" button. The contents of the panel are not validated
     * for a "Back" action, but are validated for "Next" or "Finish" prior to
     * calling this method.
     */
    public void aboutToHidePanel() {
    }

    /**
     * This method is called whenever the user has selected the "Finish" button.
     * The current panel's "validateContents()" method is called, and then it's
     * "aboutToHidePanel()" method is called. Finally, this method is called to
     * complete the wizard. Typically, a wizard would store its contents in the
     * database or file system, etc. If an error occurs, the derived class
     * should output an error message to the wizard status message area and
     * return "false" to caller so the wizard will not terminate. Returning
     * "true" indicates that the finish action was completed successfully. The
     * default implemenation returns "true".
     * 
     * @return boolean Results of the "finish" operation.
     */
    public boolean finish() {
        return editorPanel.validateContents();
    }

    /**
     * Returns the same next panel descriptor set via the constructor. This is
     * used primarily for static panel linkage, i.e., panel "A" always goes to
     * panel "B" next. For dynamic linkage, i.e., the next panel is chosen based
     * upon some user input value, this method should be overloaded by the
     * derived class.
     * 
     * @return Object The Id of the next object.
     */
    public Object getNextPanelDescriptor() {
        return nextPanelDescriptor;
    }

    /**
     * Adds "instructions" text to the "Instructions" panel.
     * 
     * @param instructionLine
     *            String
     */
    public void addInstructions(String instructionLine) {
        JLabel foo = new JLabel(instructionLine);
        instructionsPanel.add(foo);
    }

    /**
     * Returns "true" if the wizard is creating a new object or "false" if the
     * wizard is editing an existing object.
     * 
     * @return boolean "true" if creating a new object.
     */
    protected boolean isNewObject() {
        return newObject;
    }

    /**
     * Returns the Wizard that contains this panel.
     * 
     * @return Wizard
     */
    protected Wizard getWizard() {
        return wizard;
    }

    protected String[] getUnitCodes(DefData data) {
        List<SelectItem> units = data.getItems().getAllItems();
        List<String> unitCodes = new ArrayList<String>();

        for (SelectItem s : units)
            unitCodes.add((String) s.getValue());

        return unitCodes.toArray(new String[0]);
    }

    protected String[] getUnitDefs(DefData data) {
        List<SelectItem> units = data.getItems().getAllItems();
        List<String> unitDefs = new ArrayList<String>();

        for (SelectItem s : units)
            unitDefs.add(s.getLabel());

        return unitDefs.toArray(new String[0]);
    }
}
