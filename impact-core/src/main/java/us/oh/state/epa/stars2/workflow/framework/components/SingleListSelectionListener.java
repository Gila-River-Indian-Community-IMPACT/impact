package us.oh.state.epa.stars2.workflow.framework.components;

import java.util.EventListener;

/**
 * <p>
 * Title: SingleListSelectionListener
 * </p>
 * 
 * <p>
 * Description: Interface that defines the selection callback for a
 * SingleSelectionList.
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
public interface SingleListSelectionListener extends EventListener {
    /**
     * Method that gets called whenever the user has made a selection from a
     * list.
     * 
     * @param event
     *            list selection event.
     */
    void valueSelected(SingleListSelectionEvent event);
}
