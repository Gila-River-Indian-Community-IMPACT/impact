package us.oh.state.epa.stars2.framework.config;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import us.oh.state.epa.stars2.framework.exception.SystemException;

@Component("configManager")
public class FSXMLConfigManager extends ConfigManager {
    private TreeManager treeManager;
    private static Logger logger = Logger.getLogger(FSXMLConfigManager.class);
    
    public FSXMLConfigManager() throws SAXException,
            ParserConfigurationException, IOException {
    	super();
    }

    @PostConstruct
    protected void init() throws SAXException, ParserConfigurationException,
                       IOException {
        // See if the configuration is in the classpath. If it is make it the 
        // default location and look for it as a command line argument.
        // Use the command line argument as the configuration location, if it's 
        // there.
        String environment = System.getProperty("epa.environment");
        String instance = System.getProperty("epa.instance");
        String configurationPath = "configuration/" ;
        if (environment != null && instance != null) {
            configurationPath = configurationPath + environment + "/" + instance;
            System.out.println("**************************************************");
            System.out.println("\tEnvironment = " + environment);
            System.out.println("\tInstance = " + instance);
            System.out.println("\tConfiguration Path = " + configurationPath);
            System.out.println("**************************************************");
        } else {
            System.out.println("WARNING: environment and/or instance is not set.");
        }
        URL log4jURL = this.getClass().getClassLoader().getResource(configurationPath + "log4j.properties");
        if (log4jURL != null) {
            System.out.println("Configuring log4j with "  + log4jURL);
          PropertyConfigurator.configure(log4jURL);
        } else {
            System.out.println("Using default log4j properties file.");
        }
//        String defaultLocation = null;
        Enumeration<URL> appURLs = this.getClass().getClassLoader().getResources(configurationPath);

        if (appURLs != null) {
//            defaultLocation = appURL.getPath();
        }
                
//        String prop = System.getProperty("config", defaultLocation);

//        StringTokenizer st = new StringTokenizer(prop, ",");
//        ArrayList<String> list = new ArrayList<String>();
//
//        while (st.hasMoreTokens()) {
//            String location = st.nextToken();
//            logger.debug("Using config location: " + location);
//            list.add(location);
//        }
//
//        ArrayList<File> files = new ArrayList<File>();
//
//        for (String fileName : list) {
//            files.add(new File(fileName));
//        }

//        treeManager = new TreeManager(files.toArray(new File[0]));
        treeManager = new TreeManager(appURLs);
        treeManager.init(this);
    }

    public final void reload() {
        try {
            logger.debug("Reloading configuration...");
            init();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e.getMessage());
        }
    }
    
    /**
     * @see ConfigManager#getRoot()
     */
    public final Node getRoot() {
        return treeManager.getRoot();
    }

    /**
     * @see ConfigManager#getNode(String path)
     */
    public final Node getNode(String path) {
        StringTokenizer tok = new StringTokenizer(path, DELIMITER);
        TreeNode currentNode = null;
        
        if (logger == null) {
            logger = Logger.getLogger(FSXMLConfigManager.class);
        }

        if (treeManager == null) {
            Throwable err = new Throwable();
            logger.error("Treemanager is null!!!", err.fillInStackTrace());
        } else {
            currentNode = treeManager.getRoot();
            String name = null;

            while (tok.hasMoreTokens()) {
                name = tok.nextToken();
                TreeNode nextNode = currentNode.getChild(name);

                if (nextNode == null) {
                    currentNode = null;
                    break;
                }

                currentNode = nextNode;
            }
        }

        logger.debug("currentNode = " + currentNode);
        return currentNode;
    }

    /**
     * @see ConfigManager#getChildOf(Node theNode, String name)
     */
    public final Node getChildOf(Node theNode, String name) {
        return ((TreeNode)theNode).getChild(name);
    }

    /**
     * @see ConfigManager#getChildrenOf(Node theNode, String name)
     */
    public final Node[] getChildrenOf(Node theNode, String name) {
        return ((TreeNode)theNode).getChildren(name);
    }

    /**
     * @see ConfigManager#getAllChildrenOf(Node theNode)
     */
    public final Node[] getAllChildrenOf(Node theNode) {
        return ((TreeNode)theNode).getAllChildren();
    }

    /**
     * @param listener
     */
    public final void registerConfigListener(ConfigListener listener) {
    }

    /**
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        return treeManager.toString();
    }
    
    public void shutdown() {
        treeManager.shutdown();
        treeManager = null;
        
        logger = null;
    }
}
