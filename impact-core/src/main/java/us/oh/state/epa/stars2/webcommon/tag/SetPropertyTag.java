package us.oh.state.epa.stars2.webcommon.tag;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.ValueBinding;

import org.apache.myfaces.shared_impl.taglib.UIComponentTagBase;

/**
 * SetPropertyTag
 * 
 * @author yehp
 * 
 */
public class SetPropertyTag extends UIComponentTagBase implements java.io.Serializable {
    private String property;
    private String value;
    private String converter;

    public final void setProperty(String newValue) {
        property = newValue;
    }

    public final void setValue(String newValue) {
        value = newValue;
    }

    public final void setConverter(String newValue) {
        converter = newValue;
    }

    @Override
    public final void setProperties(UIComponent component) {
        FacesContext facesContext = getFacesContext();
        Application application = facesContext.getApplication();

        if (isValueReference(property)) {
            ValueBinding propertyvb = application.createValueBinding(property);

            ValueBinding valuevb = null;
            if (isValueReference(value)) {
                valuevb = application.createValueBinding(value);
                Object v = valuevb.getValue(facesContext);
                if (v != null) {
                    value = v.toString();
                } else {
                    return;
                }
            }

            Class<?> type = propertyvb.getType(facesContext);
            if (type != null && !type.equals(String.class) && !type.equals(Object.class)) {
                Converter c = null;
                if (converter != null) {
                    c = application.createConverter(converter);
                } else {
                    try {
                        c = application.createConverter(type);
                    } catch (Exception e) {
                        throw new FacesException(
                                "No Converter registered with SetProperty and no appropriate standard converter found. Needed to convert String to "
                                        + type.getName(), e);
                    }
                }
                Object v = c.getAsObject(facesContext, facesContext
                        .getViewRoot(), value);
                propertyvb.setValue(facesContext, v);
            } else {
                propertyvb.setValue(facesContext, value);
            }
        }
    }

    @Override
    public final void release() {
        super.release();
        property = null;
        value = null;
    }

    @Override
    public final String getRendererType() {
        return null;
    }

    @Override
    public final String getComponentType() {
        return "us.oh.state.epa.stars2.webcommon.component.SetProperty";
    }
}
