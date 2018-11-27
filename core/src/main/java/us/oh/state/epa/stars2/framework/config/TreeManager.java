package us.oh.state.epa.stars2.framework.config;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import us.oh.state.epa.stars2.framework.exception.SystemException;

/**
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
public class TreeManager {
    private XMLFilenameFilter xmlFilenameFilter = new XMLFilenameFilter();
    private DirectoryFilter dirFilter = new DirectoryFilter();
    private List<TreeNode> nodeList = new LinkedList<TreeNode>();
    private TreeNode root;
    Logger logger = Logger.getLogger(this.getClass());

    public TreeManager(File[] rootDir) throws SAXException,
            ParserConfigurationException, IOException {
        root = new TreeNode("", "");
        nodeList.add(root);

        for (File file : rootDir) {
            louFilter(file);
//            loadFiles(file, "", root);
        }
    }

    public TreeManager(Enumeration<URL> urls) throws SAXException, 
    	ParserConfigurationException, IOException {
        root = new TreeNode("", "");
        nodeList.add(root);
        while (urls.hasMoreElements()) {
        	loadFiles(urls.nextElement(), root);
        }
	}

	/**
     * @return
     */
    public final TreeNode getRoot() {
        return root;
    }

    /**
     * @param manager
     */
    public final void init(ConfigManager manager) {
        for (TreeNode treeNode : nodeList.toArray(new TreeNode[0])) {
            treeNode.init(manager);
        }
    }

    /**
     * It is not uncommon when people have to merge two app.xml files to rename
     * one of them to something like <code>app_lou.xml</code>. At this point
     * there are two app.xml files. The way the Config Manager works is that it
     * will read all of the xml files under a given directory. This is not how
     * people to expect it to work. They expect that the config manager will
     * specifically look for only app.xml in the root directory. The case
     * mentioned above can cause problems since both xml files are read. This
     * method will try to detect this situation and emit a warning if it finds
     * more than one <code>app.xml</code> in the root directory.
     * 
     * @param rootDir
     * @throws IOException
     */
    protected final void louFilter(File rootDir) {
        File[] file = rootDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.indexOf("app") != -1 && name.endsWith(".xml"));
            }
        });

        if (file != null && file.length > 1) {
            System.err
                    .println("+-----------------------------------------------+");
            System.err
                    .println("|                                               |");
            System.err
                    .println("| IMPORTANT:                                    |");
            System.err
                    .println("| Detected more than one *app*xml file in the   |");
            System.err
                    .println("| root config directory. This could             |");
            System.err
                    .println("| potentially cause problems. Please review.    |");
            System.err
                    .println("|                                               |");
            System.err
                    .println("+-----------------------------------------------+");
        }
    }

    protected final void loadFiles(URL thisDir, TreeNode currentNode)
            throws SAXException, ParserConfigurationException, IOException {

    	String subDirPattern = thisDir.getPath() + "*/";
    	if (subDirPattern.contains(".jar!")) {
    		subDirPattern = "jar:" + subDirPattern;
    	} else {
    		subDirPattern = "file:" + thisDir.getPath() + "\\*";    		
    	}
    	PathMatchingResourcePatternResolver pathMatcher = new PathMatchingResourcePatternResolver();
    	Resource[] dir = pathMatcher.getResources(subDirPattern);

        if (dir == null) {
            StringBuffer b = new StringBuffer(System
                    .getProperty("line.separator"));
            b.append(System.getProperty("line.separator"));
            b
                    .append("Couldn't find the directory that contains the configuration files.");
            b.append(System.getProperty("line.separator"));
            b
                    .append("Use the VM parameter 'config' to point to the directory that ");
            b.append(System.getProperty("line.separator"));
            b.append("contains the root configuration files.");
            b.append(System.getProperty("line.separator"));
            b.append(System.getProperty("line.separator"));
            b.append("Example: java -Dconfig=bin/com/mentorgen/config ...");
            b.append(System.getProperty("line.separator"));
            throw new SystemException(b.toString());
        }

        for (Resource file : dir) {
            String name = file.getFilename();
            
            TreeNode child = currentNode.getChild(name);

            if (child == null) {
                child = new TreeNode(name, file.getURL().getPath());
                nodeList.add(child);
            }

            currentNode.addChild(child);

            loadFiles(file.getURL(), child);
        }

        // load all of the XML files in thisDir
        //
    	String xmlPattern = thisDir.getPath() + "*.xml";
    	if (xmlPattern.contains(".jar!")) {
    		xmlPattern = "jar:" + xmlPattern;
    	} else {
    		xmlPattern = "file:" + xmlPattern;    		
    	}
        for (Resource ff : pathMatcher.getResources(xmlPattern)) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder builder = dbf.newDocumentBuilder();
                Document doc = builder.parse(ff.getInputStream());
                Element lRoot = doc.getDocumentElement();
                NodeList children = lRoot.getChildNodes();

                for (int a = 0; a < children.getLength(); a++) {
                    Node child = children.item(a);

                    if (child instanceof Element) {
                        loadElement((Element) child, thisDir.getPath(), currentNode);
                    } else if (child instanceof Text) {
                        currentNode.setText(child.getNodeValue());
                    }
                }
            } catch (SAXException e) {
                logger.error("SAXException while loading XML document.");
                logger.error("Resource path = [" + ff.getURL().getPath() + "].");
                logger.error("Exception message: " + e.getMessage());

                StringBuffer b = new StringBuffer(e.getMessage());
                b.append(System.getProperty("line.separator"));
                b.append("Resource path = [");
                b.append(ff.getURL().getPath());
                b.append("].");
                throw new SAXException(b.toString(), e);
            }
        }
    }

    /**
     * Outputs the values of a node and all of its children. The output for the
     * full Tree consists of about 160,000 lines of information. Therefore, it
     * is strongly recommended that the input <tt>PrintStream</tt> be
     * something other than <tt>System.out</tt>, e.g., a disk file.
     * 
     * @param n
     *            BasicNode whose attributes are output to <tt>ps</tt>.
     * @param ps
     *            PrintStream The place to output the information to.
     */
    public final void showNode(TreeNode n, PrintStream ps) {
        // Output a bunch of basic header stuff common to all TreeNodes.

        ps.println("\n===> TreeNode Output, Name = " + n.getName() + "\n");

        String txt = n.getText();

        if (txt == null) {
            txt = "<none>";
        }

        txt.trim();
        if (txt.length() == 0) {
            txt = "<none>";
        }

        ps.println("   Text = " + txt);
        ps.println("   FullPath = " + n.getFullPath());

        // Each TreeNode has a "Properties" section. Output the
        // Property name-values held by this node.

        String[] attrNames = n.getAttributeNames();

        if ((attrNames != null) && (attrNames.length > 0)) {
            int alen = attrNames.length;
            int i;
            String attr;
            String attrValue;
            ps.println("\n   Attributes = Values");
            ps.println("   -------------------");

            for (i = 0; i < alen; i++) {
                attr = attrNames[i];
                attrValue = n.getAsString(attr);
                ps.println("   " + attr + " = " + attrValue);
            }
        }

        // TreeNodes can also have children. Get a list of the children
        // held by "n". If there are children, go ahead and output them
        // too.

        TreeNode[] children = n.getAllChildren();

        if ((children != null) && (children.length > 0)) {
            TreeNode t;
            int i;
            for (i = 0; i < children.length; i++) {
                t = children[i];
                this.showNode(t, ps);
            }
        }

        ps.println("\n====> END: Node name = " + n.getName());
    }

    protected final void loadElement(Element element, String parentPath,
            TreeNode parent) {
        String name = element.getTagName();
        StringBuffer fullPath = new StringBuffer(parentPath);

        if (parentPath.length() > 0) {
            fullPath.append(ConfigManager.DELIMITER);
        }
        fullPath.append(name);

        TreeNode thisNode = null;

        // Handle override :

        String override = element.getAttribute("override");

        if (override != null && override.equals("yes")) {
            thisNode = parent.getChild(name);
            parent.removeChild(thisNode);
            thisNode = null;
        }

        // Handle createNew :

        String createNew = element.getAttribute("createNew");

        if (createNew != null && createNew.compareTo("no") == 0) {
            thisNode = parent.getChild(name);
        }

        // Process this node :

        if (thisNode == null) {
            thisNode = new TreeNode(name, fullPath.toString());
            parent.addChild(thisNode);
            nodeList.add(thisNode);
        }

        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attr = attributes.item(i);
            String attrName = attr.getNodeName();
            String attrValue = attr.getNodeValue();
            thisNode.set(attrName, attrValue);
        }

        // process child elements
        //
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if (child instanceof Element) {
                loadElement((Element) child, fullPath.toString(), thisNode);
            } else if (child instanceof Text) {
                thisNode.setText(child.getNodeValue());
            }
        }

    }

    /**
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        return toStringNodes(root);
    }

    protected final String toStringNodes(TreeNode node) {
        StringBuffer b = new StringBuffer(node.getFullPath());
        b.append(System.getProperty("line.separator"));

        TreeNode[] children = node.getAllChildren();

        if (children.length > 0) {
            b.append("------------------------------------");
            b.append(System.getProperty("line.separator"));
        }

        for (int i = 0; i < children.length; i++) {
            b.append(toStringNodes(children[i]));
        }

        return b.toString();
    }

    // ----------------------------------------------------------- inner classes

    final class XMLFilenameFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.endsWith(".xml");
        }
    }

    final class DirectoryFilter implements FileFilter {
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    }
    
    public void shutdown() {
        xmlFilenameFilter = null;
        dirFilter = null;
        nodeList = null;
        root = null;
    }
}
