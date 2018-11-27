package us.oh.state.epa.stars2.portal.home;

import java.lang.reflect.Method;

import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;

@SuppressWarnings("serial")
public class CreateMenuItem extends AppBase {
    private String title;
    private String outcome;
    private String explainText;
    private boolean popup;
    private boolean disabled;
    private String width;
    private String height;

    public CreateMenuItem(final String title, final String outcome, 
            final String explainText, final boolean popup, final boolean disabled, final String width,
            final String height) {
        this.title = title;
        this.outcome = outcome;
        this.explainText = explainText;
        this.popup = popup;
        this.disabled = disabled;
        this.width = width;
        this.height = height;
    }
    
	public final String getExplainText() {
        return explainText;
    }

    public final void setExplainText(final String explainText) {
        this.explainText = explainText;
    }

    public final String getOutcome() {
        return outcome;
    }

    public final void setOutcome(final String outcome) {
        this.outcome = outcome;
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(final String title) {
        this.title = title;
    }
    
    public String outcome() {
    	String ret = null;
    	Method m;
    	MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
    	try {
            m = myTasks.getClass().getMethod(outcome,
                    new Class[0]);
            ret = (String) m.invoke(myTasks, new Object[0]);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            DisplayUtil.displayError("System error");
        }
    	return ret;
    }

    public final boolean isPopup() {
        return popup;
    }

    public final void setPopup(boolean popup) {
        this.popup = popup;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}
