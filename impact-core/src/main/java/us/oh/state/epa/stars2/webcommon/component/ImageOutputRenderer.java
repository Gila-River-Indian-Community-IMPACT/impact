package us.oh.state.epa.stars2.webcommon.component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.servlet.http.HttpServletResponse;

import us.oh.state.epa.stars2.webcommon.bean.Image;

/**
 * ImageOutputRenderer
 * 
 * @author yehp
 * 
 */
public class ImageOutputRenderer extends Renderer implements java.io.Serializable {
    @SuppressWarnings("unchecked")
    public final void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {
        if (component.isRendered()) {
            HttpServletResponse response = (HttpServletResponse) context
                    .getExternalContext().getResponse();
            response.setContentType("image/jpeg");
            OutputStream stream = response.getOutputStream();

            // Read tag attribute from componebt.
            Map<String, Object> attributes = component.getAttributes();
            String beanName = parseString(attributes, "beanName",
                    "workFlow2DDraw");

            // Read bean from FacesContext.
            FacesContext fc = FacesContext.getCurrentInstance();
            Image image = (Image) fc.getApplication().getVariableResolver()
                    .resolveVariable(context, beanName);

            // Output the BufferedImage.
            Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter writer = iter.next();
            writer.setOutput(ImageIO.createImageOutputStream(stream));
            writer.write(image.getImage());

            context.responseComplete();
        }
        return;
    }

    private static String parseString(Map<String, Object> attributes, String name,
            String defaultValue) {
        String value = (String)attributes.get(name);

        if (value != null) {
            if (value.contains(".")) {
                value = value.substring(0, value.indexOf("."));
            }
            return value;
        }

        return defaultValue;
    }
}
