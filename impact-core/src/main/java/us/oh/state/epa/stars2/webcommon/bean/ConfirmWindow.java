package us.oh.state.epa.stars2.webcommon.bean;

import javax.faces.event.ActionEvent;

import oracle.adf.view.faces.context.AdfFacesContext;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.FacesUtil;

public class ConfirmWindow extends AppBase {
    public static final String YES = "Yes";
    public static final String NO = "No";
    public static final String CANCEL = "Cancel";
    private static final String YES_NO = "YN";
    private static final String YES_NO_CANCEL = "YNC";
    private static final int WIDTH = 600;
    private static final int HEIGHT = 200;
    private static final String OUTCOME = "dialog:confirm";
    private String title = "Confirmation";
    private boolean prettyTitle;
    private String message;
    private String type;
    private String selection;
    private String button1Text;
    private String button2Text;
    private String button3Text;
    private String method;

    // For setType to use
    public final String getYesNo() {
        return YES_NO;
    }

    public final String getYesNoCancel() {
        return YES_NO_CANCEL;
    }

    public final void setType(String type) {
        this.type = type;
        if (type.equalsIgnoreCase(YES_NO)) {
            setButton1Text(YES);
            setButton2Text(NO);
        } else if (type.equalsIgnoreCase(YES_NO_CANCEL)) {
            setButton1Text(YES);
            setButton2Text(NO);
            setButton3Text(CANCEL);
        } else {

        }
    }

    public final String getMessage() {
        if (message == null) {
            StringBuffer st = new StringBuffer();
            if (type.equalsIgnoreCase(YES_NO)) {
                st
                        .append("Please click 'Yes' to confirm the action or 'No' to reject.");
            } else if (type.equalsIgnoreCase(YES_NO_CANCEL)) {
                st
                        .append("Please click 'Yes' for yes, 'No' for no, and 'Cancel' to cancel.");
            } else {
                st
                        .append("Please report this error.  Confirmation type is needed!!");
            }
            message = st.toString();
        }
        return message;
    }

    public void selected(ActionEvent actionEvent) {
        if (method != null){
            FacesUtil.invokeMethod(method, null, null);
            method = null;
            FacesUtil.returnFromDialogAndRefresh();
        }else
            AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }

    // Button control
    private void resetButtons() {
        button1Text = null;
        button2Text = null;
        button3Text = null;
        message = null;
    }

    public final void setButton1Text(String text) {
        resetButtons();
        button1Text = text;
    }

    public final void setButton2Text(String text) {
        button2Text = text;
    }

    public final void setButton3Text(String text) {
        button3Text = text;
    }

    public final String getButton3Text() {
        return button3Text;
    }

    public final String getButton1Text() {
        return button1Text;
    }

    public final String getButton2Text() {
        return button2Text;
    }

    // normal getter/setter
    public final void setMessage(String message) {
        this.message = message;
    }

    public final String getSelection() {
        return selection;
    }

    public final void setSelection(String selection) {
        this.selection = selection;
    }

    public final int getHeight() {
        return HEIGHT;
    }

    public final int getWidth() {
        return WIDTH;
    }

    public String confirm() {
        return OUTCOME;
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    public final boolean isPrettyTitle() {
        return prettyTitle;
    }

    public final void setPrettyTitle(boolean prettyTitle) {
        this.prettyTitle = prettyTitle;
    }

    /**
     * @return the method
     */
    public final String getMethod() {
        return method;
    }

    /**
     * @param method the method to set
     */
    public final void setMethod(String method) {
        this.method = method;
    }
}
