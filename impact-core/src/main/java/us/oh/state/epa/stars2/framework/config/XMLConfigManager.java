package us.oh.state.epa.stars2.framework.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import us.oh.state.epa.stars2.framework.exception.SystemException;

/**
 * XMLConfigManager.
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
public class XMLConfigManager extends ConfigManager {
    protected static final String XPATH_DELIMITER = "/";
    private Logger logger = Logger.getLogger(XMLConfigManager.class);
    private String fileName;
    private Element rootElement;
    private CachedXPathAPI xpathAPI = new CachedXPathAPI();

    /**
     * @param fileName
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public XMLConfigManager(String fileName) throws IOException, SAXException,
            ParserConfigurationException {
        this.fileName = fileName;
        //init();
    }

    /**
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public XMLConfigManager() throws IOException, SAXException,
            ParserConfigurationException {
        fileName = System.getProperty("config", "app.xml");
        //init();
    }

    /**
     * @param inputStream
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public XMLConfigManager(InputStream inputStream) throws IOException,
            SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(inputStream);

        synchronized (this) {
            rootElement = doc.getDocumentElement();
        }
    }

    protected void init() throws IOException, SAXException,
                       ParserConfigurationException {
        loadFromFile();
    }

    /**
     * @see us.oh.state.epa.stars2.framework.config.ConfigManager#reload()
     */
    public final void reload() {
        try {
            logger.debug("Reloading configuration...");
            init();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e.getMessage());
        }
    }

    protected final void loadFromFile() throws IOException, SAXException,
            ParserConfigurationException {
        logger.debug("Configuration being loaded from " + fileName);
        File file = new File(fileName);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc;

        if (file.exists()) {
            doc = builder.parse(file);
        } else {
            InputStream stream = new BufferedInputStream(Thread.currentThread()
                    .getContextClassLoader().getResourceAsStream(fileName));
            doc = builder.parse(stream);
        }

        synchronized (this) {
            rootElement = doc.getDocumentElement();
        }
    }

    protected final boolean isLoadedFromFile() {
        return (fileName != null);
    }

    /**
     * <b>for testing only<b> May not work in production code!!!
     */
    public final void setRootElement(Element root) {
        rootElement = root;
    }

    /**
     * @see ConfigManager#getRoot()
     */
    public final Node getRoot() {
        return buildNodeFromElement(rootElement, "");
    }

    /**
     * @see ConfigManager#getNode(String path)
     */
    public Node getNode(String path) {
        StringBuffer xpath = new StringBuffer();
        StringTokenizer tok = new StringTokenizer(path, DELIMITER);
        Node ret = null;

        while (tok.hasMoreTokens()) {
            String name = tok.nextToken();
            xpath.append(XPATH_DELIMITER);
            xpath.append(name);
        }

        org.w3c.dom.Node node = null;
        node = selectSingleNode(rootElement, xpath.toString());

        if (node instanceof Element) {
            ret = buildNodeFromElement((Element) node, path);
        }

        return ret;
    }

    /**
     * @see ConfigManager#getChildOf(Node parent, String name)
     */
    public final Node getChildOf(Node parent, String name) {
        Element element = ((XMLNode) parent).getElement();
        Node ret = null;

        if (element != null) {
            StringBuffer xpath = new StringBuffer(name);
            xpath.append("[1]");
            org.w3c.dom.Node child = null;

            child = selectSingleNode(element, xpath.toString());

            if (child instanceof Element) {
                String newPath = buildChildPath(parent.getFullPath(), name);
                ret = buildNodeFromElement((Element) child, newPath);
            }
        }

        return ret;
    }

    /**
     * @see ConfigManager#getChildrenOf(Node theNode, String childrenName)
     */
    public final Node[] getChildrenOf(Node theNode, String childrenName) {
        Element element = ((XMLNode) theNode).getElement();
        Node[] ret = new Node[0];

        if (element != null) {
            NodeList list = null;
            try {
                synchronized (xpathAPI) {
                    list = xpathAPI.selectNodeList(element, childrenName);
                }
            } catch (TransformerException te) {
                transformerException(te);
            }

            ArrayList<Node> choosen = new ArrayList<Node>();

            if (list != null) {
                for (int i = 0; i < list.getLength(); i++) {
                    if (list.item(i) instanceof Element) {
                        choosen.add((Node) list.item(i));
                    }
                }
            }

            ret = new Node[choosen.size()];
            String newPath = buildChildPath(theNode.getFullPath(), childrenName);

            for (int i = 0; i < ret.length; i++) {
                Element child = (Element) choosen.get(i);
                ret[i] = buildNodeFromElement(child, newPath);
            }
        }
        return ret;
    }

    /**
     * @see ConfigManager#getAllChildrenOf(Node theNode)
     */
    public final Node[] getAllChildrenOf(Node theNode) {
        Element element = ((XMLNode) theNode).getElement();
        NodeList list = element.getChildNodes();

        ArrayList<Node> choosen = new ArrayList<Node>();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i) instanceof Element) {
                choosen.add((Node) list.item(i));
            }
        }

        Node[] children = new Node[choosen.size()];

        for (int i = 0; i < children.length; i++) {
            Element child = (Element) choosen.get(i);
            String newPath = buildChildPath(theNode.getFullPath(), child
                    .getNodeName());
            children[i] = buildNodeFromElement(child, newPath);
        }

        return children;
    }

    protected final Node buildNodeFromElement(Element element, String path) {
        String name = element.getNodeName();
        XMLNode node = new XMLNode(name, path, element);
        org.w3c.dom.Node text = null;

        text = selectSingleNode(element, "text()");

        if (text != null) {
            node.setText(text.getNodeValue());
        }

        NamedNodeMap attributes = element.getAttributes();

        for (int i = 0; i < attributes.getLength(); i++) {
            org.w3c.dom.Node attr = attributes.item(i);
            String attrName = attr.getNodeName();
            String attrValue = attr.getNodeValue();
            node.set(attrName, attrValue);
        }

        return node;
    }

    protected final String buildChildPath(String parentPath, String childName) {
        StringBuffer path = new StringBuffer(parentPath);
        path.append(DELIMITER);
        path.append(childName);
        return path.toString();
    }

    protected final void transformerException(TransformerException e) {
        logger.debug("XML Config Manager: Error loading xpath expression:", e);
        throw new SystemException(e.getMessage());
    }

    /**
     * <code>XMLConfigManager</code> does not support dynamic reloading.
     * 
     * @see DynamicXMLConfigManager
     */
    public void registerConfigListener(ConfigListener listener) {
    }

    protected final org.w3c.dom.Node selectSingleNode(Element node, String path) {
        org.w3c.dom.Node ret = null;
        
        try {
            synchronized (xpathAPI) {
                ret = xpathAPI.selectSingleNode(node, path);
            }
        } catch (TransformerException e) {
            transformerException(e);
        }
        return ret;
    }
    
    public void shutdown() {
        logger = null;
    }
}
