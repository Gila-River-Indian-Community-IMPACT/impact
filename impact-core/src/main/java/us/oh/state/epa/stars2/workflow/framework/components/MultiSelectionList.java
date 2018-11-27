package us.oh.state.epa.stars2.workflow.framework.components;

import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import us.oh.state.epa.stars2.framework.util.Utility;

/**
 * <p>
 * Title: MultiSelectionList
 * </p>
 * 
 * <p>
 * Description:
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
public class MultiSelectionList extends JPanel {
    /**
     * Minimum number of rows that will be displayed in the list.
     */
    public static final int MIN_ROW_COUNT = 4;
    protected JList list; // The List object
    private DefaultListModel listModel; // Underlying List Model
    private ArrayList<MultiListSelectionListener> listeners; // Event listeners
    private ArrayList<String> contents; // Internal view of list contents
    
    /**
     * Constructor. The minimum acceptable visible row count is "MIN_ROW_COUNT".
     * Any value less than this will be overridden.
     * 
     * @param visibleRowCount
     *            int The number of rows that should be visible.
     */
    public MultiSelectionList(int visibleRowCount) {
        listeners = new ArrayList<MultiListSelectionListener>();

        if (visibleRowCount < MIN_ROW_COUNT) {
            visibleRowCount = MIN_ROW_COUNT;
        }

        // Use a BoxLayout for overall layout of this object.

        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        // Build the list. We only allow single selection.

        list = new JList();
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.addListSelectionListener(new ListSelector(this));
        list.setVisibleRowCount(visibleRowCount);

        JScrollPane listScrollPane = new JScrollPane(list);
        add(listScrollPane);
    }

    /**
     * Adds a selection listener to this object. This is the primary means by an
     * application can immediately detect a user selection.
     * 
     * @param listener
     *            Listener for a list selection.
     */
    public final void addSelectionListener(MultiListSelectionListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener from the list of listeners. Returns "true" if the the
     * listener was found and removed. Otherwise, returns "false".
     * 
     * @param listener
     *            List selection listener to be removed.
     * 
     * @return boolean "true" if the listener was found and removed.
     */
    public final boolean removeSelectionListener(MultiListSelectionListener listener) {
        return listeners.remove(listener);
    }

    /**
     * Replaces the current contents of the list with "newList". Each string in
     * "newList" becomes a single, selectable entry in the list.
     * 
     * @param newList
     *            Array of user-selectable strings.
     */
    public final void updateContents(String[] newList) {
        contents = Utility.createArrayList(newList);
        updateContents();
    }

    /**
     * Appends all of the values in "appending" to the current list.
     * 
     * @param appending
     *            String[] Values to be added to list.
     */
    public final void appendContents(String[] appending) {
        ArrayList<String> foo = Utility.createArrayList(appending);
        contents.addAll(foo);
        updateContents();
    }

    /**
     * Removes all of the values in "removing" from the list. If a value is not
     * found in the list, it is simply ignored.
     * 
     * @param removing
     *            String[] Values to be removed from the list.
     */
    public final void removeContents(String[] removing) {
        if ((removing != null) && (removing.length > 0)) {
            for (String deadMeat : removing) {
                contents.remove(deadMeat);
            }

            updateContents();
        }
    }

    /**
     * Returns all values currently selected by the user in the list. If no
     * values are selected, returns "null" or an empty array.
     * 
     * @return String[] Current user selections.
     */
    public final String[] getSelectedValues() {
        Object[] stuff = list.getSelectedValues();
        ArrayList<Object> foo = Utility.createArrayList(stuff);

        return foo.toArray(new String[0]);
    }

    /**
     * Returns all of the values currently contained in the list. This would
     * include both selected and non-selected values.
     * 
     * @return String[] All values currently in this list.
     */
    public final String[] getAllValues() {
        return contents.toArray(new String[0]);
    }

    /**
     * Updates the visual representation of the list with whatever we have in
     * our "contents" ArrayList.
     */
    private void updateContents() {
        // Reset the list model. This "blanks" it out.

        listModel = new DefaultListModel();
        list.setModel(listModel);

        // For (each entry in the array) add it to the list model (and hence
        // to the list).
        for (String tempStr : contents) {
            listModel.addElement(tempStr);
        }
    }

    /**
     * Internal event handler for list selection. Builds the selection event
     * object and ships it out to all of the listeners.
     * 
     * @param selectedValue
     *            The value selected by the user.
     */
    protected final void notifySelection(String selectedValue) {
        String[] selections = getSelectedValues();

        if ((selections != null) && (selections.length > 0)) {
            // Create a selection event object.

            MultiListSelectionEvent evt = new MultiListSelectionEvent(this,
                    MultiListSelectionEvent.SELECTION, selections);

            sendEvent(evt);
        }
    }

    /**
     * Sends the unselection event to all listeners.
     */
    protected final void notifyUnselection() {
        // Create a selection event object.

        MultiListSelectionEvent evt = new MultiListSelectionEvent(this,
                MultiListSelectionEvent.UNSELECTION, null);

        sendEvent(evt);
    }

    /**
     * Sends event "evt" to all event listeners.
     * 
     * @param evt
     *            MultiListSelectionEvent List selection event.
     */
    private void sendEvent(MultiListSelectionEvent evt) {
        // For (each selection listener) send the event to the listener.
        for (MultiListSelectionListener mlsListener : listeners) {
            mlsListener.valueSelected(evt);
        }
    }

    /* ************* */
    /* Inner Classes */
    /* ************* */
    // This class handles list selection for the workflows list. When an
    // item is selected, this class notifies the WorkFlowPane of the
    // the selection.
    private static class ListSelector implements ListSelectionListener {
        private MultiSelectionList mlist;
        
        ListSelector(MultiSelectionList mlist) {
            this.mlist = mlist;
        }

        public void valueChanged(ListSelectionEvent evt) {
            // Ignore any selection events until the selection is complete.

            if (evt.getValueIsAdjusting()) {
                return;
            }

            // Get the selection string, which corresponds to the name of
            // selected String. Tell the MultiSelectionList about it.
            // If the selected value is "null", then this is an unselection.

            Object sel = mlist.list.getSelectedValue();

            if (sel != null) {
                String selection = sel.toString();
                mlist.notifySelection(selection);
            } else {
                mlist.notifyUnselection();
            }
        }
    }
}
