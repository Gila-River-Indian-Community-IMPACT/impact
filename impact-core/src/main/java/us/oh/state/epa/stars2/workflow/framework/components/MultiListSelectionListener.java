package us.oh.state.epa.stars2.workflow.framework.components;

import java.util.EventListener;

/**
 * <p>
 * Title: MultiListSelectionListener
 * </p>
 * 
 * <p>
 * Description: Interface that defines the selection callback for a
 * MultiSelectionList.
 * </p>
 * 
 * <p>
 * Keep in mind that the <code>MultiListSelectionEvent</code> will be
 * generated every time the user selects or un-selects an entry in the
 * associated list. For example, if the user selects three list entries and then
 * un-selects one of those entries, this will generate a total of four selection
 * events. An unselection event will be generated when the user un-selects the
 * only selected entry in the list.
 * </p>
 * 
 * <p>
 * Probably the best way to use this event is to view the event as "something
 * has been selected in the list" or "everything is now un-selected in the
 * list". Rather than relying on the current selection list (because there is no
 * way to really tell if the current list is the final user selection event),
 * use the fact that something is selected to enable a control, such as a
 * button, and let the user select this button to return the current list
 * selection and operate on it. When the un-select event occurs, use this event
 * to disable the control.
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
public interface MultiListSelectionListener extends EventListener {
    /**
     * Method that gets called whenever the user has made a selection from a
     * list.
     * 
     * @param event
     *            list selection event.
     */
    void valueSelected(MultiListSelectionEvent event);
}
