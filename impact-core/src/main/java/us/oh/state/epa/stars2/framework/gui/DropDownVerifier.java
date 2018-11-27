package us.oh.state.epa.stars2.framework.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;

/**
 * <p>Title: DropDownVerifier</p>
 *
 * <p>Description: This is the verifier for selecting an entry in a drop-down
 *                 list (JComboBox) where the values in the list may not be
 *                 editted by the user.</p>
 *
 * <p>The fact that the user cannot actually edit the list poses a problem: we
 * don't get any keyboard focus events like we do when the user is leaving a
 * text field.  To mimic the behavior we want, we add an ActionListener to the
 * JComboBox and use that to detect a user selection.  We then treat that event
 * as if it were a focus event and propagate it to the listeners.</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: MentorGen, LLC</p>
 * @author J. E. Collier
 * @version 1.0
 */

public class DropDownVerifier extends AbstractVerifier implements
        ActionListener {
    /**
     * Framework method.  Used to verify that the current contents of the object
     * are valid and allow keyboard focus to move to the next object. Because
     * the list of elements is fixed, there are no invalid values, so all we
     * really do here is update the object and publish the change notification.
     *
     * @param input The JComboBox that originated the event.
     *
     * @return Always returns "true".
     */
    public final boolean verify(JComponent input) {
        //  Figure out what was selected in the combo box.  Update the string
        //  value, which will in turn update the object value.
        JComboBox cbox = (JComboBox) input;
        String selection = (String) (cbox.getSelectedItem());

        this.updateStringValue(selection);

        this.sendChangeNotification();

        return true;
    }

    public final boolean verify(String input) {
        return true;
    }

    /**
     * Overload this method.  We don't want the object value set via this
     * method.
     *
     * @param obj It doesn't matter; we ignore it.
     */
    protected final void setObjectValue(Object obj) {
    }

    /**
     * ActionListener framework method.  Receives the drop-down selection event
     * and simply maps it to "verify()".
     *
     * @param evt ActionEvent for the user selection.
     */
    public final void actionPerformed(ActionEvent evt) {
        Object obj = evt.getSource();
        JComboBox cbox = (JComboBox) obj;
        this.verify(cbox);
    }
}
