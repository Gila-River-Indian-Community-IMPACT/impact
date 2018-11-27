package us.oh.state.epa.stars2.webcommon.component;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

/**
 * SetPropertyComp
 * 
 * This is a place holder now becasue the SetPropertyTag done all the works.
 * 
 * @author yehp
 * 
 */
public class SetPropertyComp extends UIComponentBase implements java.io.Serializable {
    public final void encodeBegin(FacesContext context) throws IOException {
    }

    /**
     * getFamily
     * 
     * @return
     */

    public final String getFamily() {
        return "Family in SetPropertyComp?";
    }
}
