package us.oh.state.epa.stars2.webcommon.component;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.webcommon.bean.AreaBean;
import us.oh.state.epa.stars2.webcommon.bean.Image;

/**
 * AreaMapUIComp
 * 
 * @author yehp
 * 
 */
public class AreaMapUIComp extends UIComponentBase implements java.io.Serializable {
    protected transient Logger logger;

    public AreaMapUIComp() {
        logger = Logger.getLogger(this.getClass());   
    }
    
    public final void encodeBegin(FacesContext context) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        // Get bean from FacesContext.
        String beanName = (String) getAttributes().get("beanName");
        FacesContext fc = FacesContext.getCurrentInstance();
        Image image = (Image) fc.getApplication().getVariableResolver()
                .resolveVariable(context, beanName);

        validate(image, beanName);
        AreaBean[] areas = image.getAreas();
        if (areas != null) {
            // <map name="t">
            writer.startElement("map", this);
            writer.writeAttribute("name", image.getMapName(), null);

            // <area shape="RECT" coords="334,30,466,10"
            // href="/ticket/ticket-view.html" />
            for (AreaBean ab : areas) {
                writer.startElement("area", this);
                writer.writeAttribute("shape", ab.getShape(), null);
                writer.writeAttribute("coords", ab.getCoords(), null);
                StringBuffer sb = new StringBuffer(context.getExternalContext()
                        .getRequestContextPath());
                sb.append(ab.getHref());
                writer.writeAttribute("href", sb.toString(), null);
                writer.writeAttribute("title", ab.getTitle(), null);
                writer.endElement("area");
            }

            // </map>
            writer.endElement("map");
        }
    }

    /**
     * @param image
     * @param beanName
     */
    private void validate(Image image, String beanName) {
        // don't know how to do error page..
        if (image != null) {
            if (image.getMapName() == null) {
                logger.error(beanName + "map name is not defined.");
            }
        } else {
            logger.error(beanName + " is not defined in managed-bean.");
        }
    }

    /**
     * getFamily
     * 
     * @return
     */

    public final String getFamily() {
        return "Family in AreaMapUIComp?";
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
    }
}
