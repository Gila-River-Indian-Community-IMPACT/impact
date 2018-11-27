package us.oh.state.epa.stars2.framework.config;

import java.util.HashMap;
import java.util.Hashtable;

import javax.annotation.PostConstruct;

import us.oh.state.epa.stars2.def.DefData;


/**
 * By convention, the configuration information for reusable components is
 * stored under <code>app.COMPONENT-CLASS.INSTANCE-NAME<code>. Where
 * <code>COMPONENT-CLASS</code> is this component's type and
 * <code>INSTANCE-NAME</code> is the name for the instance of that component.
 * <br>
 * <b>Note that, while the ConfigManager support runtime reloading, it is
 * not required to.</b>
 *
 * <DL>
 * <DT><B>Copyright:</B></DT><DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT><DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @version 1.1
 * @author Andrew Wilcox
 */
public abstract class ConfigManager {
    protected static final String DELIMITER = ".";
    protected HashMap<String, DefData> defs = new HashMap<String, DefData>();
    private Hashtable<String, HashMap<String, String>> allIDMap = new Hashtable<String, HashMap<String, String>>();

    public ConfigManager() {
    	super();
    }
    
    @PostConstruct
    protected abstract void init() throws Exception; 
    
    public void addDef(String defName, DefData newDef) {
        if ((defs != null) && (defName != null) && (newDef != null)) {
           defs.put(defName, newDef); 
        }
    }
    
    public DefData getDef(String defName) {
        DefData ret = null;
        
        if (defs != null) {
            ret = defs.get(defName);
        }
        
        return ret;
    }
    /**
     * Retrieves the "root" of the configuration
     * 
     * @return Node "root" node of the configuration
     */
    public abstract Node getRoot();

    /**
     * Retrieves the node referenced by the path
     * 
     * @param path
     *            to node desired
     * @return Node requested Node, or null if path can't be found.
     */
    public abstract Node getNode(String path);

    /**
     * Retrieves a specifc child of the given node with the given name
     * 
     * @param theNode
     *            The parent node to search for the name
     * @param name
     *            The name of the child to look for
     * @return Node the child node or null if child can not be found
     */
    public abstract Node getChildOf(Node theNode, String name);

    /**
     * Retrieves specfic children nodes of the given node with the given name
     * 
     * @param theNode
     *            The parent node to search for the name
     * @param name
     *            The name of the children to look for
     * @return Node[] An Array of the childten nodes or null if the node has no
     *         children
     */
    public abstract Node[] getChildrenOf(Node theNode, String name);

    /**
     * Retrieves all children of the given node.
     * 
     * @param theNode
     *            The node to return the children of
     * @return Node[] An array of the children nodes or null if the node has no
     *         children
     */
    public abstract Node[] getAllChildrenOf(Node theNode);
    
    /**
     * Reloads the entire configuration tree. This can be dangerous, if someone
     * changes DB's, because currentlt the DAO subsystem does not look for changes
     * and DAO/BO's could be using multiple DB's. This is the most egregious 
     * example, but currently no area of the system checks for configuration 
     * changes, so other unexpected side effects could happen.
     */
    public abstract void reload();
    
    public abstract void shutdown();

    /**
     * @return the allIDMap
     */
    public final Hashtable<String, HashMap<String, String>> getAllIDMap() {
        return allIDMap;
    }

    /**
     * @param allIDMap the allIDMap to set
     */
    public final void setAllIDMap(
            Hashtable<String, HashMap<String, String>> allIDMap) {
        this.allIDMap = allIDMap;
    }
}
