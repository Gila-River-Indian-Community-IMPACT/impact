package us.oh.state.epa.stars2.framework.config;

import us.wy.state.deq.impact.App;

/**
 * This is the source for instances of <code>ConfigManager</code>. The
 * current implementation of this interface supports groups of XML files, rather
 * than requiring one large XML file. The <code>
 * ConfigManager</code> looks for
 * the VM parameter "config" which should point to a directory. For example:
 *
 * <pre><code>
 *   java -Dconfig=bin/configuration
 * </code></pre>
 *
 * where <code>bin/configuration</code> is the root directory holding the XML
 * configuration files. You can also specify more than one directory by comma
 * (,) delimiting the paths (do NOT use the OS's file delimiter). <br/> There
 * are a number of rules about how the files are used to built the configuration
 * tree:
 * <ul>
 * <li>File names aren't used as part of the path to a node in the hierarchy.</li>
 * <li>The name of the root element is not used as part of the path to a node.
 * By convention, &lt;root&gt; should be used as the name of the root element.</li>
 * <li>Directory names are used when building the path to a node</li>
 * <li>When building the tree structure, if two elements of the same name are
 * encountered in different files, two nodes with the same name will appear in
 * the tree unless each element has the attribute <code>create-new=no</code>
 * in which case the contents of both nodes will be merged into a single node.</li>
 * <li>The contents of an XML file in a given directory are read before
 * subdirectories are processed.</li>
 * </ul>
 * Note also that it appears as if the current implementation doesn't always
 * handle empty directoried well. If you have an empty directory that is in
 * between two directories that contain XML files, it's recommended that you
 * create an xml file that contains empty elements with the subdirectory names.
 * Each element must also have the <code>create-name=no</code> attribute.
 * <DL>
 * 
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version 1.1
 * @author Andrew Wilcox
 * 
 */
public class ConfigManagerFactory {
    public static final ConfigManager configManager() {
        return App.getApplicationContext().getBean(FSXMLConfigManager.class);
    }
}
