package us.oh.state.epa.stars2.workflow.framework.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.util.StringTokenizer;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import us.oh.state.epa.stars2.framework.gui.GuiInput;

public class BaseDialog extends JDialog {
    JTextArea statusMsg; // Status message

    JPanel statusPanel; // Panel that holds status messages

    /**
     * @param dialog
     * @param title
     * @param modal
     */
    public BaseDialog(JDialog dialog, String title, boolean modal) {
        super(dialog, title, modal);
    }

    /**
     * @param frame
     * @param title
     * @param modal
     */
    public BaseDialog(JFrame frame, String title, boolean modal) {
        super(frame, title, modal);
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

        repaint();
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
     * error message. Note that "object" may be null if the error message is not
     * associated with any specific component in the dialog.
     * 
     * @param object
     *            The GuiInput object originating the error condition.
     * @param msg
     *            The message to be displayed.
     */
    public final void setErrorMessage(GuiInput object, String msg) {
        Toolkit.getDefaultToolkit().beep();
        String label = null;

        if (object != null) {
            label = object.getLabel();
        } else {
            label = "";
        }

        statusMsg.setForeground(Color.RED);

        setMessage(label + msg);
    }
}
