package us.oh.state.epa.stars2.workflow.framework.components;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * <p>
 * Title: PickList
 * </p>
 * 
 * <p>
 * Description: A "PickList" consists of two user selectable lists with a pair
 * of buttons in between them. Elements in the left list can be moved to the
 * right list and vice versa. The left list is referred to as the "source" list
 * and the right list is referred to as the "destination" list.
 * </p>
 * 
 * <p>
 * How this works: The user selects one or more elements in the list. Whenever
 * the user selects elements in an list, a button is enabled that, when
 * selected, moves the elements from their current list to the other list. The
 * button labeled ">>" moves elements from the left list to the right list and "<<"
 * moves elements from the right list to the left list. Elements may appear in
 * one list or the other, but not both.
 * <p>
 * 
 * <p>
 * At some point, the application will be interested in contents of each list.
 * Methods are provided to allow the application to retrieve each list (see
 * <code>getDestContents()</code> and <code>getSourceContents()</code> for
 * details).
 * </p>
 * 
 * <p>
 * Kavita Baireddy picked the name and Shweta Saxena approved it. Blame them.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 */
public class PickList extends JPanel {
    private MultiSelectionList sourceList;
    private MultiSelectionList pickedList;
    private JButton moveToPickedButton;
    private JButton moveToSourceButton;
    private boolean resetList;

    /**
     * Constructor. Creates a "Pick List" with "srcPicks" assigned to the source
     * (or left side) list and "destPicks" assigned to the destination (or right
     * side) list. If both lists are null, the two lists in will be rather large
     * because the lists have no basis to compute a proper size. If the
     * "srcPicks" is not null, and the "destPicks is null or empty, then
     * "srcPicks" is used to compute the size for both the source and
     * destination lists. However, the destination list will still be empty when
     * the PickList first becomes visible.
     * 
     * @param srcPicks
     *            String[] User selections to display in source list.
     * @param destPicks
     *            String[] User selections to display in the destination list.
     */
    public PickList(String[] srcPicks, String[] destPicks) {
        // This is your standard, pain-in-the-butt Swing layout technique.

        LayoutManager mainLayout = new GridBagLayout();
        setLayout(mainLayout);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 100;
        gbc.weighty = 100;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Build the source list.

        JPanel foo = new JPanel();
        // foo.setBorder (new LineBorder(Color.BLUE)) ;
        add(foo, gbc);
        sourceList = new MultiSelectionList(6);
        foo.add(sourceList);
        gbc.gridx = 1;

        // Throw in some horizontal space.

        add(Box.createHorizontalStrut(10), gbc);
        gbc.gridx = 2;

        // Build the two buttons we need.

        JPanel buttons = new JPanel();
        // buttons.setBorder (new LineBorder (Color.GREEN)) ;
        buttons.setLayout(new GridLayout(0, 1, 5, 5));
        add(buttons, gbc);
        gbc.gridx = 3;

        moveToPickedButton = new JButton(">>");
        moveToSourceButton = new JButton("<<");
        moveToPickedButton.setEnabled(false);
        moveToSourceButton.setEnabled(false);
        buttons.add(moveToPickedButton);
        buttons.add(moveToSourceButton);

        // Throw in a little more horizontal space.

        add(Box.createHorizontalStrut(10), gbc);
        gbc.gridx = 4;

        // Build the destination list.

        foo = new JPanel();
        // foo.setBorder (new LineBorder(Color.RED)) ;
        add(foo, gbc);
        pickedList = new MultiSelectionList(6);
        foo.add(pickedList);

        // Populate the source list with "srcPicks".

        updateSourceList(srcPicks);

        // Now, engage in a little trickery. If we don't have "destPicks"
        // but we do have "srcPicks", use "srcPicks" to compute the size of
        // the destination list. We set the "resetList" flag so we will
        // know later that we need to wipe out the contents of the destination
        // list.

        if (((destPicks == null) || (destPicks.length == 0))
                && (srcPicks != null)) {
            updateDestList(srcPicks);
            resetList = true;
        } else // We have destination picks or both lists are empty.
        {
            updateDestList(destPicks);
            resetList = false;
        }

        // Add in our various listener objects.

        sourceList.addSelectionListener(new ListListener(this));
        pickedList.addSelectionListener(new ListListener(this));

        moveToPickedButton.addActionListener(new ButtonListener(this));
        moveToSourceButton.addActionListener(new ButtonListener(this));
    }

    /**
     * Updates the source list with a new set of values. Does not alter the
     * destination list in any way.
     * 
     * @param srcPicks
     *            String[] New values to display in the source list.
     */
    public final void updateSourceList(String[] srcPicks) {
        sourceList.updateContents(srcPicks);
    }

    /**
     * Updates the destination list with a new set of values. Does not alter the
     * source list in any way.
     * 
     * @param destPicks
     *            String[] New values to display in the destination list.
     */
    public final void updateDestList(String[] destPicks) {
        pickedList.updateContents(destPicks);
    }

    /**
     * Returns the current contents of the source list. This includes both
     * selected and non-selected entries.
     * 
     * @return String[] The contents of the list.
     */
    public final String[] getSourceContents() {
        return sourceList.getAllValues();
    }

    /**
     * Returns the current contents of the destination list. This includes both
     * selected and non-selected entries.
     * 
     * @return String[] The contents of the list.
     */
    public final String[] getDestContents() {
        return pickedList.getAllValues();
    }

    /**
     * Swing framework method. Overloaded so we can see if we need to clear out
     * the destination list.
     * 
     * @param grf
     *            Graphics AWT Graphics object.
     */
    protected final void paintComponent(Graphics grf) {
        // If the "resetList" flag is set, the contents of "pickedList" were
        // used strictly for size computation and the list is actually
        // supposed to be empty. So empty it.

        if (resetList) {
            pickedList.updateContents(null); // Clear the list.
            resetList = false; // Don't want to do this again
        }

        // Let the base class handle it from here.

        super.paintComponent(grf);
    }

    /**
     * Handler for list selection. Basically, looks at the event to see which
     * list generated the event and whether this event is due to a list
     * selection or unselection. Adjusts the "enabled" state of the button that
     * moves elements from this list to the other list.
     * 
     * @param evt
     *            MultiListSelectionEvent List selection event.
     */
    protected final void listSelected(MultiListSelectionEvent evt) {
        Object selList = evt.getSource(); // Which list generated it?
        int selectCode = evt.getEventType(); // What type of event?

        // Pick the button that moves stuff from this list to the other one.
        // Enable or disable the button depending on the event type.

        JButton button = moveToPickedButton;

        if (selList == pickedList) {
            button = moveToSourceButton;
        }

        PickList.setButton(selectCode, button);
    }

    /**
     * Handler for button selection. The fact that the button has been selected
     * means that the associated list has one or more selections. The intent is
     * to move these selections from their current list to the other one.
     * 
     * @param evt
     *            ActionEvent Button event.
     */
    protected final void buttonEvent(ActionEvent evt) {
        Object src = evt.getSource();

        if (src == moveToPickedButton) {
            PickList.moveSelections(sourceList, pickedList);
        } else if (src == moveToSourceButton) {
            PickList.moveSelections(pickedList, sourceList);
        }
    }

    /**
     * Moves values selected in "source" to "dest".
     * 
     * @param source
     *            MultiSelectionList List with user selections.
     * @param dest
     *            MultiSelectionList Place to copy these selections to.
     */
    static private void moveSelections(MultiSelectionList source,
            MultiSelectionList dest) {
        String[] selected = source.getSelectedValues();
        source.removeContents(selected);
        dest.appendContents(selected);
    }

    /**
     * Enables or disables "button" based on the event selection code.
     * 
     * @param selectCode
     *            int SELECTED or UNSELECTED.
     * @param button
     *            JButton The button to enable or disable.
     */
    static private void setButton(int selectCode, JButton button) {
        if (selectCode == MultiListSelectionEvent.SELECTION) {
            button.setEnabled(true);
        } else {
            button.setEnabled(false);
        }
    }

    // This method is used for development only.
    // Runtime VM parameters:
    //
    // "-Dconfig=C:/Projects/OSSM/java/WEB-INF/configuration" +
    // "-DOSSMHOME=C:/Projects/OSSM" +
    // "-Djava.ext.dirs=C:/Projects/OSSM/java/WEB-INF/lib" +
    // "-DHOSTNAME=jec_laptop"
    /*
     * static public void main (String[] args) { String[] testCrap = { "First",
     * "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth",
     * "Ninth", "Tenth" } ;
     *  // String[] moreCrap = { "Yay", "Boo", "Whoop", "Dee", "Doo" } ;
     * 
     * JFrame window = new JFrame ("Some Dumb Test") ; JPanel jp = new JPanel() ;
     * window.getContentPane().add (jp) ;
     * 
     * PickList pickList = new PickList (testCrap, null) ; // PickList pickList =
     * new PickList (null, null) ; jp.add (pickList) ;
     * 
     * window.pack() ; window.setVisible (true) ; }
     */

    /* ************* */
    /* Inner Classes */
    /* ************* */
    // Listens to selections in the lists.
    static private class ListListener implements MultiListSelectionListener {
        PickList pickList;

        ListListener(PickList plist) {
            pickList = plist;
        }

        public void valueSelected(MultiListSelectionEvent event) {
            pickList.listSelected(event);
        }
    }

    // Listens to button selections.

    static private class ButtonListener implements ActionListener {
        PickList pickList;
        ButtonListener(PickList plist) {
            pickList = plist;
        }

        public void actionPerformed(ActionEvent evt) {
            pickList.buttonEvent(evt);
        }
    }
}
