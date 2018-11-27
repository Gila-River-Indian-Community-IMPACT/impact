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

/**
 * <p>
 * Title: SingleSelectionList
 * </p>
 * 
 * <p>
 * Description: This class provides a scrollable selection list that allows only
 * a single list entry to be selected at any given time. The class also provides
 * listener support for immediate to a user list selection.
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
public class SingleSelectionList extends JPanel {
    protected JList list; // The List object
    private DefaultListModel listModel; // Underlying List Model
    private ArrayList<SingleListSelectionListener> listeners;
    private String[] contents;

    /**
     * Constructor.
     */
    public SingleSelectionList() {
        listeners = new ArrayList<SingleListSelectionListener>();

        // Use a BoxLayout for overall layout of this object.

        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        // Build the list. We only allow single selection.

        list = new JList();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelector(this));

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
    public final void addSelectionListener(SingleListSelectionListener listener) {
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
    public final boolean removeSelectionListener(SingleListSelectionListener listener) {
        return listeners.remove(listener);
    }

    /**
     * Removes all values from the list. After calling this method, the list
     * will no user-selectable values.
     */
    public final void resetContents() {
        updateContents(null);
    }

    /**
     * Replaces the current contents of the list with "newList". Each string in
     * "newList" becomes a single, selectable entry in the list.
     * 
     * @param newList
     *            Array of user-selectable strings.
     */
    public final void updateContents(String[] newList) {
        contents = newList; // Save for later refresh

        // Reset the list model. This "blanks" it out.

        listModel = new DefaultListModel();
        list.setModel(listModel);

        // If the incoming list of stuff is empty, then we are done.

        if ((newList != null) && (newList.length > 0)) {
            // For (each entry in the array) add it to the list model (and hence
            // to the list).
            int i;
            for (i = 0; i < newList.length; i++) {
                listModel.addElement(newList[i]);
            }
        }
    }

    /**
     * Sets the list to select value "selection". Note that this will trigger
     * listening events, so caller must be aware of this if the caller has
     * registered a selection listener with this object. If "selection" is not
     * found in the list, then nothing will be selected.
     * 
     * @param selection
     *            Value to be selected in the list.
     */
    public final void select(String selection) {
        list.setSelectedValue(selection, true);
    }

    /**
     * Selects a list entry by its index in the list.
     * 
     * @param idx
     *            int The index of the entry in the list.
     */
    public final void select(int idx) {
        list.setSelectedIndex(idx);
    }

    /**
     * Unselects any value currently selected in the list.
     */
    public final void unselect() {
        // list.clearSelection() ;
        updateContents(contents);
    }

    /**
     * Internal event handler for list selection. Builds the selection event
     * object and ships it out to all of the listeners.
     * 
     * @param selectedValue
     *            The value selected by the user.
     * @param selIdx
     *            The index of the value selected by the user.
     */
    protected final void notifySelection(String selectedValue, int selIdx) {
        // Create a selection event object.

        SingleListSelectionEvent evt = new SingleListSelectionEvent(this,
                selectedValue, selIdx);

        // For (each selection listener) send the event to the listener.
        for (SingleListSelectionListener s : listeners) {
            s.valueSelected(evt);
        }
    }

    /* ************* */
    /* Inner Classes */
    /* ************* */
    // This class handles list selection for the workflows list. When an
    // item is selected, this class notifies the WorkFlowPane of the
    // the selection.
    private static class ListSelector implements ListSelectionListener {
        private SingleSelectionList slist;

        ListSelector(SingleSelectionList slist) {
            this.slist = slist;
        }

        public void valueChanged(ListSelectionEvent evt) {
            // Ignore any selection events until the selection is complete.

            if (!evt.getValueIsAdjusting()) {
                // Get the selection string, which corresponds to the name and
                // index of selected object. Tell the workflow pane about it.

                Object sel = slist.list.getSelectedValue();
                int selIdx = slist.list.getSelectedIndex();

                if (sel != null) {
                    String selection = sel.toString();
                    slist.notifySelection(selection, selIdx);
                }
            }
        }
    }
}
