package us.oh.state.epa.stars2.framework.config;

import org.w3c.dom.Element;

/**
 * XMLNode.
 *
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @version 1.0
 * @author Andrew Wilcox
 */
public class XMLNode extends BasicNode {
    private Element element;

    /**
     * @param name
     * @param fullPath
     * @param element
     */
    public XMLNode(String name, String fullPath, Element element) {
        super(name, fullPath);
        this.element = element;
    }

    /**
     * @return
     */
    public final Element getElement() {
        return element;
    }
}
