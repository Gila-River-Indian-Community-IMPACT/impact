/**
 * 
 */
package us.oh.state.epa.stars2.webcommon.component;

import java.util.StringTokenizer;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.el.ValueBinding;

import oracle.adf.view.faces.component.core.input.CoreSelectOneChoice;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.FacesUtil;

/**
 * @author Pyeh
 * 
 */
public class SelectItems extends UISelectItems implements java.io.Serializable {
    @Override
    public final Object getValue() {
        UIComponent p = this.getParent();
        AppBase pr = getBean();

        pr.setParent((CoreSelectOneChoice) p);
        return super.getValue();
    }

    /**
     * @return
     * 
     */
    private AppBase getBean() {
        ValueBinding vb = getValueBinding("value");
        StringTokenizer st = new StringTokenizer(vb.getExpressionString(), ".");
        String beanName = st.nextToken().substring(2);

        AppBase pr = (AppBase) FacesUtil.getManagedBean(beanName);

        return pr;
    }

}
