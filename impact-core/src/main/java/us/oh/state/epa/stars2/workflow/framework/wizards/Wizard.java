package us.oh.state.epa.stars2.workflow.framework.wizards;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import us.oh.state.epa.stars2.framework.gui.ErrorListener;
import us.oh.state.epa.stars2.framework.gui.GuiInput;

/**
 * <p>
 * Title: Wizard
 * </p>
 * 
 * <p>
 * Description: This class provides the basic wizard framework. A wizard
 * consists of a dialog window with one or more (usually more) panels that are
 * used to perform some function. The wizard dialog automatically provides
 * "Back", "Next"/"Finish", and "Cancel" buttons. The "Back" button allows the
 * user to go back to the previous panel. The "Next" button selects the next
 * panel in the wizard. The "Finish" button allows the user to finish the
 * wizard. "Cancel" allows the user to terminate the wizard without doing an
 * update.
 * </p>
 * 
 * <p>
 * How to use this class: This class can be used as a component, but you may
 * find it easier to "extend" this class to your own wizard class. To build a
 * wizard, you need to focus on two tasks. First, build the individual panels of
 * the wizard. A typical wizard might include an introduction panel (or "splash"
 * panel) and one or more panels to gather user information. If the user
 * information is gathered in the form of an object, then the
 * <code>ObjectEditorPanel</code> can be especially helpful in constructing
 * wizard panels. These panels are instantiated as extensions of the
 * <code>WizardPanel</code> class.
 * </p>
 * 
 * <p>
 * The second task to focus on is linking the panels together to form a logical
 * sequence of steps. Many wizard panels may need to select the next step based
 * on the contents of the current panel. Other panels can be linked together
 * statically. Individual wizard panels do not need to worry about previous
 * steps; the <code>Wizard</code> knows the execution sequence and will
 * automatically handle the "Back" button. Each <code>WizardPanel</code> is
 * wrapped up in a <code>WizardPanelDescriptor</code>, which identifies the
 * panel, whether or not it is the initial panel, and whether or not it is a
 * final panel.
 * </p>
 * 
 * <p>
 * All wizards must have one (and only one) initial panel. Wizards can have any
 * number of "finish" panels. The wizard will always terminate and return to
 * caller whenever the user selects "Finish" (and any necessary validation has
 * completed successfully) or the user selects "Cancel".
 * </p>
 * 
 * <p>
 * This framework was adapted from the Wizard framework described in a "white
 * paper" on Sun's Java technology website. The original framework seemed
 * somewhat lacking in capability. However, this framework really isn't anything
 * to brag about.
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

public class Wizard extends WindowAdapter implements ErrorListener {
    /**
     * Return code that indicates that the wizard has not been executed yet.
     */
    public static final int NOT_EXECUTED = -1;

    /**
     * Return code that indicates that wizard has successfully finished.
     */
    public static final int FINISHED = 0;

    /**
     * Return code that indicates that the user cancelled the wizard.
     */
    public static final int CANCELLED = 1;

    /**
     * Return code that indicates that the wizard attempted to transition to an
     * unknown object Id.
     */
    public static final int ERROR_UNKNOWN_ID = 2;

    /**
     * Return code that indicates that the wizard attempted to transition to a
     * known object Id, but there was no panel associated with the object Id.
     */
    public static final int ERROR_NO_PANEL_FOR_ID = 3;

    /**
     * Return code that indicates that the wizard has not identified an initial
     * panel.
     */
    public static final int ERROR_INVALID_INIT_ID = 4;
    private int returnCode;
    private LinkedList<WizardPanelDescriptor> panelStack;
    private HashMap<Object, WizardPanelDescriptor> panelMap;
    private HashMap<WizardPanelDescriptor, Object> backPanelMap;
    private JDialog wizardDialog;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JButton backButton;
    private JButton nextButton;
    private JButton cancelButton;
    private JPanel statusPanel;
    private JTextArea statusMsg;

    // Return codes. These values are returned by the "getReturnCode()" and
    // "show()" methods.

    /**
     * Constructor. Use this constructor when the wizard has no parent window.
     * 
     * @param title
     *            String The title to display on the wizard title bar.
     */
    public Wizard(String title) {
        this(title, (Frame) null);
    }

    /**
     * Constructor. Use this constructor when the wizard has a dialog window for
     * a parent. The parent is "blocked" until this wizard completes.
     * 
     * @param title
     *            String The title to display on the wizard title bar.
     * @param parent
     *            Dialog Parent dialog window.
     */
    public Wizard(String title, Dialog parent) {
        wizardDialog = new JDialog(parent, title, true);
        initComponents();
    }

    /**
     * Constructor. Use this constructor when the wizard has a regular window
     * for a parent. The parent is "blocked" until this wizard completes.
     * 
     * @param title
     *            String The title to display on the wizard title bar.
     * @param parent
     *            Frame Parent window.
     */
    public Wizard(String title, Frame parent) {
        wizardDialog = new JDialog(parent, title, true);
        initComponents();
    }

    /**
     * Returns the current value of the return code. This will always be one of
     * the constants defined at the beginning of this class.
     * 
     * @return int
     */
    public final int getReturnCode() {
        return returnCode;
    }

    /**
     * Returns the JDialog that contains the wizard. This value should only be
     * used for parenting and not for direct manipulation of the dialog.
     * 
     * @return JDialog
     */
    public final JDialog getWizardDialog() {
        return wizardDialog;
    }

    /**
     * Registers a panel with the wizard. The panel is identified by its object
     * "id" (usually a String) so that other panels that wish to transition to
     * this panel can return this object from
     * <code>WizardPanel.getNextPanelDescriptor()</code>.
     * 
     * @param id
     *            Object Unique object identifier for this wizard panel.
     * @param panel
     *            WizardPanelDescriptor Identifies the wizard panel, whether it
     *            is the initial panel, and whether it is a final panel.
     */
    public final void registerPanel(Object id, WizardPanelDescriptor panel) {
        panelMap.put(id, panel); // Save for "Next" lookup
        backPanelMap.put(panel, id); // Save for "Back" lookup

        // Get the "panel" part and add it to the wizard dialog.

        WizardPanel wpanel = panel.getPanelComponent();
        Component editPanel = wpanel.getComponent();
        cardPanel.add(editPanel, id);
    }

    /**
     * Makes the Wizard visible as a modal dialog. The wizard remains visisble
     * until the user selects the "Finish" or "Cancel" button. The return code
     * indicates successful completion of the wizard or a possible error
     * condition (see return constants defined earlier).
     * 
     * @return int The result of the show operation.
     */
    public final int show() {
        // First, find the Id of the initial panel. If we can't find one,
        // something is wrong.

        Object initId = findInitPanelId();

        if (initId == null) {
            returnCode = Wizard.ERROR_INVALID_INIT_ID;
            return returnCode;
        }

        // Show the initial panel. Then display the wizard. Note that the
        // Dialog.show() method is marked "deprecated". No alternative
        // method has been identified in Java, so I am using it anyway.

        setCurrentPanel(initId);

        wizardDialog.pack();
        wizardDialog.setVisible(true);

        return returnCode;
    }

    /**
     * Sets the current panel of the wizard to the panel associated with "id".
     * This replaces any current panel with the new one.
     * 
     * @param id
     *            Object Id of the panel to display.
     */
    public final void setCurrentPanel(Object id) {
        // If the incoming "id" is null, caller is an idiot.

        if (id == null) {
            close(Wizard.ERROR_UNKNOWN_ID);
            return;
        }

        WizardPanelDescriptor nextDesc = panelMap.get(id);

        // If we can't find an matching panel descriptor, then caller probably
        // misspelled something (but at least caller isn't an idiot).

        if (nextDesc == null) {
            close(Wizard.ERROR_NO_PANEL_FOR_ID);
            return;
        }

        // If we are currently showing a panel, then tell that panel that it
        // is about to be hidden.

        if (panelStack.size() > 0) {
            WizardPanelDescriptor wpd = panelStack.getFirst();
            WizardPanel deadmeat = wpd.getPanelComponent();
            deadmeat.aboutToHidePanel();
        }

        // Put the new panel on top of the stack. Also, tell it that it is
        // about to be displayed, display it, and then tell it that it is
        // being displayed.

        panelStack.addFirst(nextDesc);

        WizardPanel freshmeat = nextDesc.getPanelComponent();
        freshmeat.aboutToDisplayPanel();
        cardLayout.show(cardPanel, id.toString());
        freshmeat.displayingPanel();

        // Now, figure out whether or not to enable the "Back" button. If
        // the newly displayed panel is the only one on the stack, then
        // disable the back button (we have no panel to go back to).

        boolean allowBackBtn = true;

        if (panelStack.size() <= 1) {
            allowBackBtn = false;
        }

        backButton.setEnabled(allowBackBtn);

        // If our panel is a "final" panel, change the "Next" button label to
        // "Finish". Otherwise, set the label to "Next".

        if (nextDesc.isFinalPanel()) {
            nextButton.setText("Finish");
        } else {
            nextButton.setText("Next >");
        }
    }

    /**
     * Allows the wizard to be closed from other points. The "returnCode" should
     * be one of the constants defined earlier in this class. Sets the return
     * code to "returnCode" and then removes the wizard from the screen.
     * 
     * @param inReturnCode
     *            int The return code to return to caller.
     */
    public final void close(int inReturnCode) {
        this.returnCode = inReturnCode;
        wizardDialog.dispose();
    }

    /**
     * Handles user selection of the "close window" icon (usually the big "X" on
     * the right side of the title bar). We do not have the ability to deny this
     * action, so all we can do is set the return code to "Cancelled".
     * 
     * @param evt
     *            WindowEvent The window event.
     */
    public final void windowClosing(WindowEvent evt) {
        returnCode = Wizard.CANCELLED;
    }

    /**
     * Clears all messages from the status message area.
     */
    public final void clearStatusMessage() {
        setMessage(" ");
    }

    /**
     * Sets a status message into the status message area. Status messages are
     * displayed in a different color from error messages.
     * 
     * @param msg
     *            The message to show.
     */
    public final void setStatusMessage(String msg) {
        statusMsg.setForeground(Color.BLACK);
        setMessage(msg);
    }

    /**
     * Clears any message, error or otherwise, currently displayed in the status
     * message area.
     */
    public final void clearErrorMessage() {
        clearStatusMessage();
    }

    /**
     * Sets an error message into the status message area. Error messages "beep"
     * the PC and are output in "red". The field label is extracted from
     * "object" and prefixed to the message to identify the field issuing the
     * error message.
     * 
     * @param object
     *            The GuiInput object originating the error condition.
     * @param msg
     *            The message to be displayed.
     */
    public final void setErrorMessage(GuiInput object, String msg) {
        Toolkit.getDefaultToolkit().beep();

        if (object != null) {
            String label = object.getLabel();
            msg = label + msg;
        }

        statusMsg.setForeground(Color.RED);
        setMessage(msg);
    }

    /**
     * Loads message "msg" into the status message area. Different types of
     * messages may require different visual attributes. The message setters can
     * set these attributes, for example, the color of the message, and then
     * call this message to finish formatting and displaying the message.
     * 
     * @param msg
     *            The message to be displayed in the status area.
     */
    protected final void setMessage(String msg) {
        // If the message is null or empty, give it a reasonable default.

        if ((msg == null) || (msg.length() == 0)) {
            statusMsg.setText(" ");
        } else // Else format it to fit the message area and display it.
        {
            String formattedMsg = formatMessage(msg, statusMsg);
            statusMsg.setText(formattedMsg);
        }

        wizardDialog.repaint();
    }

    /**
     * Formats a message for display in the status message area. The intent is
     * to break the message up into lines that will fit inside the status
     * message area. The line is broken up by inserting "new lines", e.g., "\n"
     * into the message at appropriate places.
     * 
     * @param msg
     *            The message to be displayed in the status message area.
     * @param displayArea
     *            The status message area.
     * 
     * @return The message formatted for display in "displayArea".
     */
    protected final String formatMessage(String msg, JTextArea displayArea) {
        // If I ask "displayArea" how wide it is, it will return zero (bug?).
        // So, ask the container how wide it is and use that value.

        Dimension dd = statusPanel.getSize();
        int dw = new Double(dd.getWidth()).intValue();

        // Get the font used in the display area so we can calculate how wide
        // the message is going to be via the "FontMetrics".

        // Font df = displayArea.getFont() ;
        FontMetrics fmet = displayArea.getGraphics().getFontMetrics();

        // Calculate the bounding rectangle around the message. Compare the
        // width of the bounding rectangle to the width of the status message
        // area. If the message will fit, then we are done.

        Rectangle2D textArea = fmet.getStringBounds(msg, displayArea
                .getGraphics());
        int rw = new Double(textArea.getWidth()).intValue();

        if (rw <= dw) {
            return msg;
        }

        // The message isn't going to fit, so we have to break it up. To do
        // this, break the message up into words and build a line of words
        // until we "fill up" the available width.

        StringBuffer sb = new StringBuffer(msg.length() + 20);
        StringTokenizer st = new StringTokenizer(msg, " ");
        int lineCnt = 0;
        int usedWidth = 0;

        // For (each word we have in the message) see how much width the word
        // plus one space needs. If it will fit in the current line, just
        // append it. If it won't fit, start a new line and append it.

        while (st.hasMoreTokens()) {
            String tmp = st.nextToken() + " ";
            textArea = fmet.getStringBounds(tmp, displayArea.getGraphics());
            rw = new Double(textArea.getWidth()).intValue();
            int newWidth = usedWidth + rw;

            if (newWidth > dw) {
                sb.append("\n");
                lineCnt++;
                usedWidth = 0;
            } else {
                usedWidth = newWidth;
            }

            sb.append(tmp);
        }

        // All done ...

        String result = sb.toString();
        return result;
    }

    /**
     * Searches the panels associated with this wizard to find the initial panel
     * for display. If no initial panel is defined, then returns "null". If more
     * than one initial panel is defined, then the first one found is returned.
     * 
     * @return Object The Id of the initial panel to display.
     */
    protected final Object findInitPanelId() {
        // Create an iterator over the keys of the panel map.

        Set<?> keySet = panelMap.keySet();
        Iterator<?> iter = keySet.iterator();

        // Iterate over the keys. For each key, see if the associated
        // descriptor indicates that this is the initial panel. If it is,
        // return the key.

        while (iter.hasNext()) {
            Object key = iter.next();
            WizardPanelDescriptor wpd = panelMap.get(key);

            if (wpd.isInitPanel()) {
                return key;
            }
        }

        return null; // We did not find an initial panel
    }

    /**
     * Initializes common elements of the Wizard.
     */
    private void initComponents() {
        // First, initialize the purely data (non-GUI) members.

        returnCode = Wizard.NOT_EXECUTED;
        panelMap = new HashMap<Object, WizardPanelDescriptor>();
        backPanelMap = new HashMap<WizardPanelDescriptor, Object>();

        panelStack = new LinkedList<WizardPanelDescriptor>();

        // Access the content panel of the dialog. We can only add our
        // components to this section.

        Container mainContainer = wizardDialog.getContentPane();
        mainContainer.setLayout(new BorderLayout());

        // Use a JPanel with a CardLayout to hold wizard panels.

        cardPanel = new JPanel();
        cardPanel.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        mainContainer.add(cardPanel, BorderLayout.NORTH);

        // Now, build a panel for the wizard's buttons.

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());

        JSeparator separator = new JSeparator();
        buttonPanel.add(separator, BorderLayout.NORTH);

        Box buttonBox = new Box(BoxLayout.X_AXIS);
        buttonBox.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));

        // Build the "Back" button, some space, and then the "Next" button.
        // Put a little bit more space between "Next" and the "Cancel" button.

        backButton = new JButton("< Back");
        backButton.addActionListener(new BackListener(this));
        buttonBox.add(backButton);
        buttonBox.add(Box.createHorizontalStrut(10));

        nextButton = new JButton("Next >");
        nextButton.addActionListener(new NextListener(this));
        buttonBox.add(nextButton);
        buttonBox.add(Box.createHorizontalStrut(30));

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new CancelListener(this));
        buttonBox.add(cancelButton);

        buttonPanel.add(buttonBox, BorderLayout.EAST);

        mainContainer.add(buttonPanel, BorderLayout.CENTER);

        // Now, build an area for status and error messages.

        statusPanel = new JPanel();
        mainContainer.add(statusPanel, BorderLayout.SOUTH);

        LayoutManager smgr = new BorderLayout();
        statusPanel.setLayout(smgr);
        statusPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));

        JLabel temp = new JLabel();
        Font font = temp.getFont();

        // Use a JTextArea for status messages so that we can pre-allocate
        // two lines of display. Apparently, JLabel doesn't resize when
        // the text is too big for the current label area.

        statusMsg = new JTextArea();
        statusMsg.setFont(font);
        statusMsg.setEditable(false);
        statusMsg.setRows(2);
        statusPanel.add(statusMsg, BorderLayout.WEST);
    }

    /**
     * Handler for the "Back" button action. Note that this action will only
     * occur if there is a panel to go back to.
     */
    protected final void back() {
        // Remove the current panel from the top of the stack. Tell it that
        // it is about to be hidden.

        WizardPanelDescriptor wpd = panelStack.removeFirst();
        WizardPanel currentPanel = wpd.getPanelComponent();
        currentPanel.aboutToHidePanel();

        // Pop the panel we want to go back to from the top of the stack.
        // Re-add this panel via "setCurrentPanel" so we get its button
        // state correct.

        WizardPanelDescriptor backDesc = panelStack.removeFirst();
        Object backId = backPanelMap.get(backDesc);
        setCurrentPanel(backId);
    }

    /**
     * Handler for the "Next" button action. Note that the "Next" button also
     * serves as the "Finish" button.
     */
    protected final void next() {
        // Get the current button label. If the button is currently a
        // "Finish" button, tell the current panel to finish the wizard and
        // shut down the wizard with the "FINISHED" return code.

        nextButton.setEnabled(false);
        String buttonLabel = nextButton.getText();

        WizardPanelDescriptor wpd = panelStack.getFirst();
        WizardPanel currentPanel = wpd.getPanelComponent();

        if (!currentPanel.validateContents()) {
            nextButton.setEnabled(true);
            return;
        }

        currentPanel.aboutToHidePanel();

        if (buttonLabel.equals("Finish")) {
            if (currentPanel.finish()) {
                close(Wizard.FINISHED);
            }

            nextButton.setEnabled(true);
            return;
        }

        // If we get here, we are going to transition to the next panel.
        // Ask the current panel for the Id of the next panel and then bring
        // that panel into view.

        Object nextId = currentPanel.getNextPanelDescriptor();
        setCurrentPanel(nextId);
        nextButton.setEnabled(true);
    }

    /**
     * Handles selection of the "Cancel" button. Basically, terminates the
     * wizard.
     */
    protected final void cancel() {
        int userOption = JOptionPane
                .showConfirmDialog(
                        wizardDialog,
                        "Exiting the wizard now will discard any changes you have made.  Continue?",
                        "Cancel Wizard", JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

        if (userOption == JOptionPane.YES_OPTION) {
            close(Wizard.CANCELLED);
        }
    }

    /* ************* */
    /* Inner Classes */
    /* ************* */
    // 'Back' button listener
    private static class BackListener implements ActionListener {
        private Wizard editor;

        BackListener(Wizard editor) {
            this.editor = editor;
        }

        public void actionPerformed(ActionEvent evt) {
            editor.back();
        }
    }

    // 'Next' button listener

    private static class NextListener implements ActionListener {
        private Wizard editor;

        NextListener(Wizard editor) {
            this.editor = editor;
        }

        public void actionPerformed(ActionEvent evt) {
            editor.next();
        }
    }

    // 'Cancel' button listener

    private static class CancelListener implements ActionListener {
        private Wizard editor;
        
        CancelListener(Wizard editor) {
            this.editor = editor;
        }

        public void actionPerformed(ActionEvent evt) {
            editor.cancel();
        }
    }
}
