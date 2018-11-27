package us.oh.state.epa.stars2.webcommon.menu;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import com.Ostermiller.util.StringTokenizer;

import oracle.adf.view.faces.model.BaseMenuModel;
import oracle.adf.view.faces.model.ChildPropertyTreeModel;
import oracle.adf.view.faces.model.MenuModel;
import oracle.adf.view.faces.model.TreeModel;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.userAuth.UserAttributes;
import us.oh.state.epa.stars2.webcommon.FacesUtil;

public class SimpleMenuTree extends BaseMenuModel implements
        java.io.Serializable {
    private transient Logger logger;
    private List<SimpleMenuItem> children;
    private transient Map<String, Object> rowKeyMap;

    public SimpleMenuTree() {
        logger = Logger.getLogger(SimpleMenuTree.class);
    }
    
    /**
     * @see oracle.adf.view.faces.model.MenuModel#getFocusRowKey()
     */
    public final Object getFocusRowKey() {
        String currentViewId = FacesContext.getCurrentInstance().getViewRoot()
                .getViewId();
        return rowKeyMap.get(currentViewId);
    }

    /**
     * @return The model for this tree.
     */
    public final MenuModel getModel() {
        if (rowKeyMap == null) {
            TreeModel tree = new ChildPropertyTreeModel(children, "children");
            setWrappedData(tree);
            populateMap(tree);
        }
        return this;
    }

    /**
     * @param children
     */
    public final void setChildren(List<SimpleMenuItem> children) {
        if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
            // Get the user's attributes from session and manipulate the
            // menu structure to fit the user's roles.
            UserAttributes userAttrs = (UserAttributes) FacesUtil.getManagedBean("userAttrs");

            if (this.children == null) {
                this.children = new ArrayList<SimpleMenuItem>();
            }

            for (SimpleMenuItem tempItem : children) {
                // Every user gets all "global" menus and the "home" tab,
                // regardless of what their usecases say.
                if ((tempItem.getType().compareTo("global") == 0) || (userAttrs.isCurrentUseCaseValid(tempItem.getName())) || (tempItem.getName().compareTo("home") == 0) || (userAttrs.getUserName().compareTo("Admin") == 0)) {
                    this.children.add(tempItem);

                    if (tempItem.getViewIDs() != null) {
                        for (String viewId : tempItem.getViewIDs()) {
                            userAttrs.addViewIdToUseCase(viewId, tempItem.getName());
                        }
                    }
                    
                    StringTokenizer st = new StringTokenizer(tempItem.getName(), ".");
                    String useCase = st.nextToken();

                    while (st.hasNext())
                        useCase = useCase + "_" + st.nextToken();

                    logger.debug(useCase);
                }
            }
            
        } else {
            this.children = children;

            UserAttributes userAttrs = (UserAttributes) FacesUtil.getManagedBean("userAttrs");

            for (SimpleMenuItem tempItem : children) {
                if (tempItem.getViewIDs() != null) {
                    for (String viewId : tempItem.getViewIDs()) {
                        userAttrs.addViewIdToUseCase(viewId, tempItem.getName());
                    }
                }
                
                StringTokenizer st = new StringTokenizer(tempItem.getName(), ".");
                String useCase = st.nextToken();

                while (st.hasNext())
                    useCase = useCase + "_" + st.nextToken();

                logger.debug(useCase);
            }
        }
    }

    /**
     * @return Children of this menu tree.
     */
    public final List<SimpleMenuItem> getChildren() {
        return children;
    }

    private void populateMap(TreeModel tree) {
        rowKeyMap = new HashMap<String, Object>();
        for (int idx = 0; idx < tree.getRowCount(); idx++) {
            tree.setRowIndex(idx);
            populateMapRecursive(tree);
        }
    }

    private void populateMapRecursive(TreeModel tree) {
        Object rowKey = tree.getRowKey();
        SimpleMenuItem node = (SimpleMenuItem)tree.getRowData();
        if (node.getViewIDs() != null) {
            for (String viewID : node.getViewIDs()) {
                rowKeyMap.put(viewID, rowKey);
            }
        }

        if (tree.isContainer()) {
            tree.enterContainer();
            for (int idx = 0; idx < tree.getRowCount(); idx++) {
                tree.setRowIndex(idx);
                populateMapRecursive(tree);
            }
            tree.exitContainer();
        }
    }
    
    protected void finalize() throws Throwable {
        logger = null;

        if (children != null) {
            Iterator<SimpleMenuItem> it = children.iterator();
            while(it.hasNext()) {
                it.next();
                it.remove();
            }
//            for (SimpleMenuItem item : children) {
//                children.remove(item);
//            }
            children = null;
        }

        if (rowKeyMap != null) {
            Iterator<String> it = rowKeyMap.keySet().iterator();
            while(it.hasNext()) {
                it.next();
                it.remove();
            }
//            for (String tempStr : rowKeyMap.keySet()) {
//                rowKeyMap.remove(tempStr);
//            }
            rowKeyMap = null;
        }

    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
    }
}
