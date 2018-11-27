package us.oh.state.epa.stars2.webcommon.tag;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.webapp.UIComponentTag;

/**
 * Tags
 * 
 * Source code examples from 'Core JavaServer Faces'
 * http://www.horstmann.com/corejsf/
 */
public class Tags implements java.io.Serializable {
    @SuppressWarnings("unchecked")
    public static void setString(UIComponent component, String attributeName,
            String attributeValue) {
        if (attributeValue != null) {
            if (UIComponentTag.isValueReference(attributeValue)) {
                setValueBinding(component, attributeName, attributeValue);
            } else {
                component.getAttributes().put(attributeName, attributeValue);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void setInteger(UIComponent component, String attributeName,
            String attributeValue) {
        if (attributeValue != null) {
            if (UIComponentTag.isValueReference(attributeValue)) {
                setValueBinding(component, attributeName, attributeValue);
            } else {
                component.getAttributes().put(attributeName,
                        new Integer(attributeValue));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void setDouble(UIComponent component, String attributeName,
            String attributeValue) {
        if (attributeValue != null) {
            if (UIComponentTag.isValueReference(attributeValue)) {
                setValueBinding(component, attributeName, attributeValue);
            } else {
                component.getAttributes().put(attributeName,
                        new Double(attributeValue));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void setBoolean(UIComponent component, String attributeName,
            String attributeValue) {
        if (attributeValue == null) {
            if (UIComponentTag.isValueReference(attributeValue)) {
                setValueBinding(component, attributeName, attributeValue);
            } else {
                component.getAttributes().put(attributeName,
                        new Boolean(attributeValue));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void setStrings(UIComponent component, Map map) {
        Iterator<?> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            setString(component, (String) entry.getKey(), (String) entry
                    .getValue());
        }
    }

    public static void setValueBinding(UIComponent component,
            String attributeName, String attributeValue) {
        FacesContext context = FacesContext.getCurrentInstance();
        Application app = context.getApplication();
        ValueBinding vb = app.createValueBinding(attributeValue);
        component.setValueBinding(attributeName, vb);
    }

    public static void setActionListener(UIComponent component,
            String attributeValue) {
        setMethodBinding(component, "actionListener", attributeValue,
                new Class[] { ActionEvent.class });
    }

    public static void setValueChangeListener(UIComponent component,
            String attributeValue) {
        setMethodBinding(component, "valueChangeListener", attributeValue,
                new Class[] { ValueChangeEvent.class });
    }

    public static void setValidator(UIComponent component, String attributeValue) {
        setMethodBinding(component, "validator", attributeValue, new Class[] {
                FacesContext.class, UIComponent.class, Object.class });
    }

    @SuppressWarnings("unchecked")
    public static void setAction(UIComponent component, String attributeValue) {
        if (attributeValue != null) {
            if (UIComponentTag.isValueReference(attributeValue)) {
                setMethodBinding(component, "action", attributeValue,
                        new Class[] {});
            } else {
                MethodBinding mb = new ActionMethodBinding(attributeValue);
                component.getAttributes().put("action", mb);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void setMethodBinding(UIComponent component,
            String attributeName, String attributeValue, Class[] paramTypes) {
        if (attributeValue != null) {
            if (UIComponentTag.isValueReference(attributeValue)) {
                FacesContext context = FacesContext.getCurrentInstance();
                Application app = context.getApplication();
                MethodBinding mb = app.createMethodBinding(attributeValue,
                        paramTypes);
                component.getAttributes().put(attributeName, mb);
            }
        }
    }

    public static String eval(String expression) {
        String ret = null;

        if (expression != null) {
            if (UIComponentTag.isValueReference(expression)) {
                FacesContext context = FacesContext.getCurrentInstance();
                Application app = context.getApplication();
                ret = "" + app.createValueBinding(expression).getValue(context);
            } else {
                ret = expression;
            }
        }
        return ret;
    }

    public static Integer evalInteger(String expression) {
        Integer ret = null;

        if (expression != null) {
            if (UIComponentTag.isValueReference(expression)) {
                FacesContext context = FacesContext.getCurrentInstance();
                Application app = context.getApplication();
                Object r = app.createValueBinding(expression).getValue(context);
                if (r != null) {
                    if (r instanceof Integer) {
                        ret = (Integer) r;
                    } else {
                        ret = new Integer(r.toString());
                    }
                } else {
                    ret = new Integer(expression);
                }
            }
        }
        return ret;
    }

    public static Double evalDouble(String expression) {
        Double ret = null;

        if (expression != null) {
            if (UIComponentTag.isValueReference(expression)) {
                FacesContext context = FacesContext.getCurrentInstance();
                Application app = context.getApplication();
                Object r = app.createValueBinding(expression).getValue(context);
                if (r != null) {
                    if (r instanceof Double) {
                        ret = (Double) r;
                    } else {
                        ret = new Double(r.toString());
                    }
                }
            } else {
                ret = new Double(expression);
            }
        }
        return ret;
    }

    public static Boolean evalBoolean(String expression) {
        Boolean ret = null;

        if (expression != null) {
            if (UIComponentTag.isValueReference(expression)) {
                FacesContext context = FacesContext.getCurrentInstance();
                Application app = context.getApplication();
                Object r = app.createValueBinding(expression).getValue(context);
                if (r != null) {
                    if (r instanceof Boolean) {
                        ret = (Boolean) r;
                    } else {
                        ret = new Boolean(r.toString());
                    }
                }
            } else {
                ret = new Boolean(expression);
            }
        }
        return ret;
    }

    private static class ActionMethodBinding extends MethodBinding implements
            Serializable {
        private String result;

        public ActionMethodBinding(String result) {
            this.result = result;
        }

        @Override
        public Object invoke(FacesContext context, Object params[]) {
            return result;
        }

        @Override
        public String getExpressionString() {
            return result;
        }

        @Override
        public Class<? extends Object> getType(FacesContext context) {
            return String.class;
        }
    }
}
