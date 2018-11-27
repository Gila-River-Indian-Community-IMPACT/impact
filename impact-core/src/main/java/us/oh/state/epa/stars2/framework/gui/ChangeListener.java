package us.oh.state.epa.stars2.framework.gui;

/**
 * <p>
 * Title: ChangeListener
 * </p>
 *
 * <p>
 * Description: This interface allows an application to listen for valid changes
 * to a <tt>GuiInput</tt> object. This interface is only invoked when a
 * <tt>GuiInput</tt> has changed value and that change has been validated. The
 * notification may be viewed as occurring while input focus is moving from the
 * recently validated <tt>GuiInput</tt> to the next object.
 * </p>
 *
 * <p>
 * This interface supports situations where a successful change to a value in
 * one field changes the set of available selections in another field. In these
 * situations, adding a <code>ChangeListener</code> to the first field would
 * allow the application to filter the set of options displayed in a second
 * field.
 * </p>
 *
 * <p>
 * NOTE: If the process of altering the contents of one or more dependent fields
 * involves a database retrieval or file retrieval, use a
 * <code>SwingWorker</code> to do that operation so that the GUI will not be
 * blocked.
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

public interface ChangeListener {
    /**
     * Identifies the <tt>GuiInput</tt> that has changed and successfully
     * validated. Input focus is about to leave "gt" and move to another object.
     * The listener should retrieve either the <code>String</code> version of
     * the value, or the <tt>Object</tt> version of the value, but should
     * otherwise NOT alter "gt".
     *
     * @param changed
     *            GuiInput that has been changed and validated.
     */
    void valueChanged(GuiInput changed);
}
