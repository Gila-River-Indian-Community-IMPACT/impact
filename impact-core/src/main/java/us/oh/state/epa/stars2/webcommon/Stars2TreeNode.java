package us.oh.state.epa.stars2.webcommon;

import org.apache.myfaces.custom.tree2.TreeNodeBase;

/*
 * This class extends TreeNodeBase, to store a reference to a user
 * object. This makes it easier to retrieve the DB object associated with the
 * tree node clicked by the user.
 */
public class Stars2TreeNode extends TreeNodeBase {
    private Object userObject;
    private Integer parentFpNodeId;
    private String parentNodeId;

    public Stars2TreeNode(String type, String description, String identifier,
            boolean leaf, Object userObject) {
        super(type, description, identifier, leaf);
        this.userObject = userObject;
    }

    public final Object getUserObject() {
        return userObject;
    }

    public final void setUserObject(Object userObject) {
        this.userObject = userObject;
    }

    public final Integer getParentFpNodeId() {
        return parentFpNodeId;
    }

    public final void setParentFpNodeId(Integer parentFpNodeId) {
        this.parentFpNodeId = parentFpNodeId;
    }

    public final String getParentNodeId() {
        return parentNodeId;
    }

    public final void setParentNodeId(String parentNodeId) {
        this.parentNodeId = parentNodeId;
    }

    public final Stars2TreeNode findNode(String nodeType, String nodeID) {
        if (getType().equals(nodeType) && getIdentifier().equals(nodeID)) {
            return this;
        }
        for (Object nodeObj : getChildren()) {
            Stars2TreeNode node = (Stars2TreeNode) nodeObj;
            Stars2TreeNode tNode = node.findNode(nodeType, nodeID);
            if (tNode != null)
                return tNode;
        }
        return null;
    }
}
