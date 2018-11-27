package us.oh.state.epa.stars2.framework.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

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
public class TreeNode extends BasicNode implements Serializable {
    private HashMap<String, ArrayList<TreeNode>> children = new HashMap<String, ArrayList<TreeNode>>();
    private ArrayList<TreeNode> allChildren = new ArrayList<TreeNode>();

    public TreeNode(String name, String fullPath) {
        super(name, fullPath);
    }

    /**
     * @param manager
     */
    public final void init(ConfigManager manager) {
        for (String name : getAttributeNames()) {
            String value = getAsString(name);

            if (value.length() > 0 && value.charAt(0) == '$') {
                String location = value.substring(1);
                Node node = manager.getNode(location);

                if (node != null) {
                    String replacementValue = node.getText();
                    set(name, replacementValue);
                }
            }
        }
    }

    /**
     * @param childNode
     */
    public final void addChild(TreeNode childNode) {
        String name = childNode.getName();
        ArrayList<TreeNode> list = children.get(name);

        if (list == null) {
            list = new ArrayList<TreeNode>();
            children.put(name, list);
        }

        list.add(childNode);
        allChildren.add(childNode);
    }

    /**
     * @param childName
     * @return
     */
    public final TreeNode getChild(String childName) {
        ArrayList<TreeNode> list = children.get(childName);
        TreeNode ret = null;
        
        if (list != null) {
            ret = list.get(0);
        }

        return ret;
    }

    /**
     * @param child
     */
    public final void removeChild(TreeNode child) {
        assert child != null;
        children.remove(child.getName());
    }

    /**
     * @param childName
     * @return
     */
    public final TreeNode[] getChildren(String childName) {
        return children.get(childName).toArray(new TreeNode[0]);
    }

    /**
     * @return
     */
    public final TreeNode[] getAllChildren() {
        return allChildren.toArray(new TreeNode[0]);
    }   
    
    /**
     * @param childName
     * @return
     */
    public final TreeNode[] setChildren(String childName) {
        return children.get(childName).toArray(new TreeNode[0]);
    }

    /**
     * @return
     */
    public final TreeNode[] setAllChildren() {
        return allChildren.toArray(new TreeNode[0]);
    }
}
