package us.oh.state.epa.stars2.framework.config;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Config.
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
public class Config {

	private static final Logger log = LoggerFactory.getLogger(Config.class);

	public static final Object getEnvEntry(String jndiName) {
		return getEnvEntry(jndiName, null);
	}
	
	public static final Object getEnvEntry(String jndiName, Object defaultValue) {
		Object value = null;
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			value = envContext.lookup(jndiName);
		} catch (NamingException ne) {
			// use default value
			log.error("Environment entry not found: " + jndiName + " (using default value: " + defaultValue + ")");
		}
		return null == value? defaultValue : value;
	}
	
    /**
     * @param where
     * @return
     */
    public static final Node findNode(String where) {
        return ConfigManagerFactory.configManager().getNode(where);
    }

    /**
     * @param parent
     * @param childName
     * @return
     */
    public static final Node findNode(Node parent, String childName) {
        return ConfigManagerFactory.configManager().getChildOf(parent, childName);
    }

    /**
     * @param where
     * @return
     */
    public static final Node[] findNodes(String where) {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        int index = where.lastIndexOf(ConfigManager.DELIMITER);
        Node[] ret = new Node[0];

        if (index == -1) {
            Node root = cfgMgr.getRoot();

            if (root != null) {
                ret = cfgMgr.getChildrenOf(root, where);
            }
        } else {
            String rootPart = where.substring(0, index);
            String endPart = where.substring(index + 1);
            Node rootNode = cfgMgr.getNode(rootPart);

            if (rootNode != null) {
                ret = cfgMgr.getChildrenOf(rootNode, endPart);
            }
        }

        return ret;
    }

    /**
     * @param parent
     * @param childName
     * @return
     */
    public static final Node[] findNodes(Node parent, String childName) {
        return ConfigManagerFactory.configManager().getChildrenOf(parent, childName);
    }

    /**
     * @param parent
     * @return
     */
    public static final Node[] findNodes(Node parent) {
        return ConfigManagerFactory.configManager().getAllChildrenOf(parent);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        return ConfigManagerFactory.configManager().toString();
    }
}
