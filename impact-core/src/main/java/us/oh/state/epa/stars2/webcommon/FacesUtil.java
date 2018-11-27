package us.oh.state.epa.stars2.webcommon;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.faces.application.Application;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.component.UISelectBoolean;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.component.UISelectMany;
import javax.faces.component.UISelectOne;
import javax.faces.component.ValueHolder;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import oracle.adf.view.faces.component.UIXInput;
import oracle.adf.view.faces.component.UIXOutput;
import oracle.adf.view.faces.component.UIXSelectBoolean;
import oracle.adf.view.faces.component.UIXSelectInput;
import oracle.adf.view.faces.component.UIXSelectMany;
import oracle.adf.view.faces.component.UIXSelectOne;
import oracle.adf.view.faces.component.UIXSwitcher;
import oracle.adf.view.faces.component.core.nav.CoreCommandButton;
import oracle.adf.view.faces.component.core.nav.CoreCommandLink;
import oracle.adf.view.faces.component.core.nav.CoreGoButton;
import oracle.adf.view.faces.component.core.nav.CoreGoLink;
import oracle.adf.view.faces.context.AdfFacesContext;

import org.apache.myfaces.config.RuntimeConfig;
import org.apache.myfaces.config.element.NavigationCase;
import org.apache.myfaces.config.element.NavigationRule;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;

@SuppressWarnings("serial")
public class FacesUtil implements java.io.Serializable {
    private static Map<String, String> navigationCases = null;
    private static String MULTI_VALUE_SEPARATOR = "|";
    private static final DateFormat dateFormatTS = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private static final DateFormat dateFormatNTS = new SimpleDateFormat("MM/dd/yyyy");
    private static DateFormat dateFormat;

    /*
     * Returns a <value, label> map of selection items for a select-type
     * component
     */
    @SuppressWarnings("unchecked")
    private static Map<Object, String> getSelectItems(ValueHolder component) {
        Map<Object, String> retVal = new HashMap<Object, String>();
        Iterator it = ((UIComponent) component).getChildren().iterator();
        while (it.hasNext()) {
            UIComponent child = (UIComponent) it.next();

            if (child instanceof UISelectItem) {
                UISelectItem uiSelectItem = (UISelectItem) child;
                retVal.put(uiSelectItem.getItemValue(), uiSelectItem
                        .getItemLabel());
            } else if (child instanceof UISelectItems) {
                UISelectItems selectItems = (UISelectItems) child;
                Object value = selectItems.getValue();

                if (value instanceof SelectItem) {
                    SelectItem selectItem = (SelectItem) value;
                    retVal.put(selectItem.getValue(), selectItem.getLabel());
                } else if (value instanceof Collection) {
                    Collection<SelectItem> coll = (Collection<SelectItem>) value;
                    for (SelectItem selectItem : coll) {
                        retVal
                                .put(selectItem.getValue(), selectItem
                                        .getLabel());
                    }
                } else if (value instanceof Map) {
                    Set<Map.Entry> valueSet = ((Map) value).entrySet();
                    for (Map.Entry entry : valueSet) {
                        retVal.put(entry.getValue(), (String) entry.getKey());
                    }
                } else if (value instanceof SelectItem[]) {
                    SelectItem[] arr = (SelectItem[]) value;
                    for (SelectItem selectItem : arr) {
                        retVal
                                .put(selectItem.getValue(), selectItem
                                        .getLabel());
                    }
                }
            }
        }
        return retVal;

    }

    /*
     * Returns the label corresponding to a value, given a <value, label> map of
     * selection items
     */
    private static String getSelectItemLabel(Map<Object, String> map, Object key) {
        String val = map.get(key);
        if (val == null) {
            val = map.get(key.toString());
        }
        return val;
    }

    /*
     * Returns the value of an object as text
     */
    private static String getObjectTextValue(Object obj) {
        if (obj instanceof Date) {
            return dateFormat.format(obj);
        }

        return obj.toString();
    }

    /*
     * Returns the exportable value (a String) of a component that implement
     * ValueHolder
     */
    private static String getComponentValue(ValueHolder component, boolean timeStampFlag) {
    	if (timeStampFlag) {
    		dateFormat = dateFormatTS;
    	} else {
    		dateFormat = dateFormatNTS;
    	}
    	
        Object objValue = component.getValue();

        if (objValue == null) {
            return "";
        }

        if (component instanceof UIXOutput || component instanceof UIXInput
                || component instanceof UIOutput
                || component instanceof UIInput
                || component instanceof UIXSelectInput) {
            return getObjectTextValue(objValue);
        } else if (component instanceof UIXSelectBoolean
                || component instanceof UISelectBoolean) {
            return ((Boolean) objValue).booleanValue() ? "yes" : "no";
        } else if (component instanceof UIXSelectMany
                || component instanceof UISelectMany) {
            Map<Object, String> selectItems = getSelectItems(component);
            if (objValue instanceof Collection<?>) {
                StringBuffer retVal = new StringBuffer();
                boolean isFirstValue = true;
                for (Object obj : (Collection<?>) objValue) {
                    if (isFirstValue) {
                        isFirstValue = false;
                    } else {
                        retVal.append(MULTI_VALUE_SEPARATOR);
                    }
                    retVal.append(getSelectItemLabel(selectItems, obj));
                }
                return retVal.toString();
            }

            return getObjectTextValue(objValue);
        } else if (component instanceof UIXSelectOne
                || component instanceof UISelectOne) {
            Map<Object, String> selectItems = getSelectItems(component);
            return getSelectItemLabel(selectItems, objValue);
        } else {
            return getObjectTextValue(objValue); // let's give it our best
                                                    // shot ...
        }
    }

    /*
     * Return the text content (a String) of a component
     */
    @SuppressWarnings("unchecked")
    public static String getComponentText(UIComponent comp, boolean timeStampFlag) {
        if (!comp.isRendered()) {
            return "";
        }

        if (comp instanceof ValueHolder && !(comp instanceof HtmlOutputLink)) {
            return getComponentValue((ValueHolder) comp, timeStampFlag);
        } else if (comp instanceof UIXSwitcher) {
            UIXSwitcher sw = (UIXSwitcher) comp;
            UIComponent selFacet;

            selFacet = (UIComponent) sw.getFacets().get(sw.getFacetName());
            if (selFacet == null) {
                selFacet = (UIComponent) sw.getFacets().get(
                        sw.getDefaultFacet());
            }
            if (selFacet == null) {
                return "";
            }

            return getComponentText(selFacet, timeStampFlag);
        }

        if (comp instanceof CoreCommandLink) {
            CoreCommandLink cmd = (CoreCommandLink) comp;
            String text = cmd.getText();
            if (text != null) {
                return text;
            }
        } else if (comp instanceof CoreCommandButton) {
            CoreCommandButton cmd = (CoreCommandButton) comp;
            String text = cmd.getText();
            if (text != null) {
                return text;
            }
        } else if (comp instanceof CoreGoLink) {
            CoreGoLink cmd = (CoreGoLink) comp;
            String text = cmd.getText();
            if (text != null) {
                return text;
            }
        } else if (comp instanceof CoreGoButton) {
            CoreGoButton cmd = (CoreGoButton) comp;
            String text = cmd.getText();
            if (text != null) {
                return text;
            }
        } else if (comp instanceof UICommand) {
            UICommand cmd = (UICommand) comp;
            Object value = cmd.getValue();
            if (value != null) {
                return getObjectTextValue(value);
            }
        }

        Iterator<UIComponent> it = comp.getChildren().iterator();
        StringBuffer retVal = new StringBuffer();
        while (it.hasNext()) {
            UIComponent child = it.next();
            retVal.append(getComponentText(child, timeStampFlag));
        }
        return retVal.toString();
    }
    
    private static SimpleMenuItem getSimpleMenuItem(String menuName) {
    	return (SimpleMenuItem)FacesUtil.getManagedBean(menuName);
    }
    
    public static void renderSimpleMenuItem(String menuName) {
    	getSimpleMenuItem(menuName).setRendered(true);
    }
    
    public static void hideSimpleMenuItem(String menuName) {
    	getSimpleMenuItem(menuName).setRendered(false);
    }
    
    /*
     * Returns a reference to a managed bean, given the name specified in the
     * JSF configuration file
     */
    public static Object getManagedBean(String managedBeanName) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        //TODO this will be used post-JSF1.1
//        Object obj = facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(),null,managedBeanName);
//        return obj;
        return facesContext.getApplication().getVariableResolver()
                .resolveVariable(facesContext, managedBeanName);
    }

    /*
     * Invokes a method on a managed bean via JSF
     */
    public static Object invokeMethod(String methodName, Class<? extends Object>[] argClasses,
            Object[] args) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        MethodBinding methodBinding = facesContext.getApplication()
                .createMethodBinding("#{" + methodName + "}", argClasses);
        return methodBinding.invoke(facesContext, args);
    }

    /*
     * Sets the outcome for navigation rules
     */
    public static void setOutcome(String fromAction, String outcome) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.getApplication().getNavigationHandler().handleNavigation(
                facesContext, fromAction, outcome);
    }

    /*
     * This function causes an ADF dialog to be dismissed and the main window -
     * i.e., the window that spawned the dialog - to refresh.
     * AdfFacesContext.returnFromDialog() dismisses the dialog but only performs
     * a partial refresh of the main window. A full refresh is needed in the
     * following two cases: - After dismissing the dialog, the main window
     * should navigate to a different JSF page - The dialog deletes the last
     * record of a collection that is displayed via an <af:table ...> in the
     * main window. This is because of an issue with ADF popups: if the backing
     * bean removes the last record in a collection displayed via an <af:table
     * ...> as part of a dialog, when the dialog is dismissed the record still
     * shows up in the table.
     */
    public static void returnFromDialogAndRefresh() {
    	returnFromDialog();
        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }
    
    public static void returnFromDialog() {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response 
            = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            
        PrintWriter pw = null;

        try {
            /*
             * ADF dialog windows contain frames. To get a reference to the main
             * window we must use top.opener. To refresh the content, we call
             * submit() on the form - window.location.reload() causes the
             * browser to request user confirmation, since the page is POST-ed.
             */
            pw = response.getWriter();
            pw.println("<script>top.opener.document.forms[0].submit();top.close();</script>");

            /*
             * By opening a HTML comment, we prevent the return-from-dialog
             * script that ADF inserts into the HTTP response from running. This
             * is necessary to avoid race conditions.
             */
            pw.println("<!-- ");
        } 
        catch (IOException e) {
            // Should never happen
        }
        finally {
            if (pw != null) {
                pw.flush();
                pw.close();
            }
        }
        facesContext.responseComplete();
    }
    
    public static void refreshMainWindow() {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response 
            = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            
        PrintWriter pw = null;

        try {
            /*
             * ADF dialog windows contain frames. To get a reference to the main
             * window we must use top.opener. To refresh the content, we call
             * submit() on the form - window.location.reload() causes the
             * browser to request user confirmation, since the page is POST-ed.
             */
            pw = response.getWriter();
            pw.println("<script>top.opener.document.forms[0].submit();this.location.href = this.location.href;</script>");

            /*
             * By opening a HTML comment, we prevent the return-from-dialog
             * script that ADF inserts into the HTTP response from running. This
             * is necessary to avoid race conditions.
             */
            pw.println("<!-- ");
        } 
        catch (IOException e) {
            // Should never happen
        }
        finally {
            if (pw != null) {
                pw.flush();
                pw.close();
            }
        }
        facesContext.responseComplete();
    }
    
    /*
     * Create Javascript for starting a modeless dialog.
     */
    public static void callModelessDialog(String jsCode) {
        if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)){
            HttpServletResponse httpResponse = (HttpServletResponse) FacesContext
                    .getCurrentInstance().getExternalContext().getResponse();

            try {
                PrintWriter responseWriter = httpResponse.getWriter();
                responseWriter.println("<script>");
                responseWriter.println(jsCode);
                responseWriter.println("</script>");
            } catch (IOException e) {
                // TODO IOException
            }
        }else{
            InfrastructureDefs ifDef = (InfrastructureDefs)getManagedBean("infraDefs");
            ifDef.setJs(jsCode);
        }
    }

    /*
     * Starts a modeless dialog. We use window.open() - as opposed to
     * window.showModelessDialog() - so that the dialog sticks around even if
     * the main window refreshes or navigates to another page.
     * 
     * 694 Make the popup window shows on top again.
     * For focus function to work on Firefox, user need to do following..
     * Firefox has an advanced Javascript option called “Raise or lower windows”
     * which is deactivated by default. You may find this option under:
     * - Tools 
     * - Options… 
     * - Content tab Advanced button next to “Enable JavaScript”
     * - check the box named "Raise or Lower Windows"
     */
    public static void startModelessDialog(String pageURL, int height, int width) {

        String js = "var nw=window.open('" + pageURL + "','"
                + pageURL.replace(".", "_").replace("/", "_") 
                + "','height=" + height + ",width=" + width 
                + ",resizable=yes,scrollbars=yes');nw.focus();";
        callModelessDialog(js);
    }
    
    /*
     * Starts a modeless dialog and closes it as soon as it open.
     */
    public static void startAndCloseModelessDialog(String pageURL) {

    	String js = "var nw=window.open('','"
                + pageURL.replace(".", "_").replace("/", "_") 
                + "','height=100,width=100');nw.close();";
        callModelessDialog(js);
    }
    
    public static void startModelessDialogWithMenubar(String pageURL, int height, int width) {

        String js = "var nw=window.open('" + pageURL + "','"
                + pageURL.replace(".", "_").replace("/", "_") 
                + "','height=" + height + ",width=" + width 
                + ",menubar=yes,resizable=yes,scrollbars=yes');nw.focus();";
        callModelessDialog(js);
    }

    public static String getCurrentPage() {
        getNavigationCases();
        String page = FacesContext.getCurrentInstance().getViewRoot()
                .getViewId();
        return navigationCases.get(page.replace(".jsp", ""));
    }

    @SuppressWarnings("unchecked")
    private static void getNavigationCases() {
        if (FacesUtil.navigationCases != null) {
            return;
        }

        ExternalContext externalContext = FacesContext.getCurrentInstance()
                .getExternalContext();
        RuntimeConfig runtimeConfig = RuntimeConfig
                .getCurrentInstance(externalContext);

        Collection rules = runtimeConfig.getNavigationRules();
        Map cases = new HashMap();
        Map outComes = new HashMap();

        for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
            NavigationRule rule = (NavigationRule) iterator.next();
            if (rule.getFromViewId() == null) {
                Object[] ncs = rule.getNavigationCases().toArray();
                for (int i = 0; i < ncs.length; i++) {
                    NavigationCase nc = (NavigationCase) ncs[i];
                    if (nc.getFromAction() == null) {
                        cases.put(nc.getToViewId().replace(".jsp", ""), nc
                                .getFromOutcome());
                        outComes.put(nc.getFromOutcome(), nc.getToViewId());
                    }
                }
            }
        }
        FacesUtil.navigationCases = cases;
        return;
    }

    public static void setValueToBean(String beanName, String valueName, Object value) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application application = facesContext.getApplication();
        String property = "#{" + beanName + "." + valueName + "}";
        ValueBinding propertyvb = application.createValueBinding(property);
        
        propertyvb.setValue(facesContext, value);
    }
    
    public static Timestamp convertYear(Timestamp date) {
        if (date != null) {
            Calendar td = new GregorianCalendar();
            td.setTimeInMillis(date.getTime());
            int year = td.get(Calendar.YEAR);
            if (year >= 0 && year <= 39)
                td.set(Calendar.YEAR, year + 2000);
            else if (year <= 99)
                td.set(Calendar.YEAR, year + 1900);

            date = new Timestamp(td.getTimeInMillis());
        }
        return date;
    }
}
