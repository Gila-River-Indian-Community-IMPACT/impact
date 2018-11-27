package us.oh.state.epa.stars2.webcommon.tag;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;

/**
 * AreaMapTag
 * 
 * @author yehp
 * 
 */
public class AreaMapTag extends UIComponentTag implements java.io.Serializable {
    private String beanName;

    public final void setBeanName(String newValue) {
        beanName = newValue;
    }

    @Override
    public final void setProperties(UIComponent component) {
        super.setProperties(component);
        if (component != null) {
            Tags.setString(component, "beanName", beanName);
        }
    }

    @Override
    public final void release() {
        super.release();
        beanName = null;
    }

    @Override
    public final String getRendererType() {
        return null;
    }

    @Override
    public final String getComponentType() {
        return "us.oh.state.epa.stars2.webcommon.component.AreaMap";
    }
}
