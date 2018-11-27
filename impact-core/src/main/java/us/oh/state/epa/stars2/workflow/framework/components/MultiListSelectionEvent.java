package us.oh.state.epa.stars2.workflow.framework.components;

import java.util.EventObject;

/**
 * <p>
 * Title: MultiListSelectionEvent
 * </p>
 * 
 * <p>
 * Description: Selection and unselection event associated with user selections
 * in <code>MultiSelectionList</code> objects.
 * </p>
 * 
 * <p>
 * Every time the user makes a selection in a <code>MultiSelectionList</code>
 * object, this event will be generated. It's event type will be "SELECTION" and
 * all currently selected values in the list are provided as "selections". This
 * event will also be generated as the user un-selects items in the list. The
 * event type will still be "SELECTION" and "selections" will contain all
 * current list selects. When the user unselects the final selection, this event
 * will be generated with an event type of "UNSELECTION" and the "selections"
 * list will be null.
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
public class MultiListSelectionEvent extends EventObject {
    /**
     * Event type when one or more entries in the list have been selected.
     */
    public static final int SELECTION = 1;
    /**
     * Event type when all selected entries in the list have been unselected.
     */
    public static final int UNSELECTION = 2;
    private int eventType;
    private String[] selections;
    /**
     * Constructor.
     * 
     * @param src
     *            Object The <code>MultiSelectionList</code> generating this
     *            event.
     * @param evtType
     *            int The event type, SELECTION or UNSELECTION.
     * @param selections
     *            String[] Currently selected entries in the list.
     */
    public MultiListSelectionEvent(Object src, int evtType, String[] selections) {
        super(src);
        eventType = evtType;
        this.selections = selections;
    }

    /**
     * Returns the event type for this event.
     * 
     * @return int Either SELECTION or UNSELECTION.
     */
    public final int getEventType() {
        return eventType;
    }

    /**
     * Returns all of the current selections from the list. If this is an
     * UNSELECTION event, this value will be "null".
     * 
     * @return String[] Current user selections.
     */
    public final String[] getSelections() {
        return selections;
    }
}
