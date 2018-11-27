package us.oh.state.epa.stars2.framework.config;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * @author yehp
 *
 */
public class XMLConfigManager4Jar extends XMLConfigManager {
    /**
     * @param inputStream
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public XMLConfigManager4Jar(InputStream inputStream) throws IOException,
            SAXException, ParserConfigurationException {
        super(inputStream);
    }

    /**
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public XMLConfigManager4Jar() throws IOException, SAXException,
            ParserConfigurationException {
        super();
    }

    /**
     * @param fileName
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public XMLConfigManager4Jar(String fileName) throws IOException,
            SAXException, ParserConfigurationException {
        super(fileName);
    }

    /**
     * TODO For only one app.xml Jar file it won't read with out root. May need
     * find out why and fix this problem.
     *
     */
    /**
     * @see us.oh.state.epa.stars2.framework.config.XMLConfigManager#getNode(java.lang.String)
     */
    public final Node getNode(String path) {
        StringBuffer n = new StringBuffer("root.");
        n.append(path);
        path = n.toString();

        return super.getNode(path);
    }
}
