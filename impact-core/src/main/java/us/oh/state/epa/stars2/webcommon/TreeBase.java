package us.oh.state.epa.stars2.webcommon;

import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.myfaces.custom.tree2.HtmlTree;
import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;

public class TreeBase extends ValidationBase {
	
	private static final long serialVersionUID = -5377002211915362055L;

	protected TreeModelBase treeData;
    protected String nodeId;
    protected TreeNode selectedTreeNode;
    protected String current;

    public final void processAction(ActionEvent event)
            throws AbortProcessingException {
        UIComponent component = (UIComponent) event.getSource();
        while (!(component != null && component instanceof HtmlTree)) {
            component = component.getParent();
        }

        HtmlTree tree = (HtmlTree) component;
        nodeId = tree.getNodeId();
    }

    public TreeNode getSelectedTreeNode() {
        return selectedTreeNode;
    }

    public void setSelectedTreeNode(TreeNode selectedTreeNode) {
        this.selectedTreeNode = selectedTreeNode;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public TreeModelBase getTreeData() {
        return treeData;
    }

    public void setTreeData(TreeModelBase treeData) {
        this.treeData = treeData;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
    
    public void clearCache() {
        treeData = null;
    }
}
