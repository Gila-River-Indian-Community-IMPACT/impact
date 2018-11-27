package us.oh.state.epa.stars2.app.admin;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.context.FacesContext;

import oracle.adf.view.faces.event.ReturnEvent;

import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;
import org.apache.myfaces.custom.tree2.TreeStateBase;

import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SecurityGroup;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.userAuth.UseCase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.TreeBase;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuTree;

/**
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2006 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @author Kbradley
 *
 */
/**
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2006 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @author Kbradley
 *
 */
@SuppressWarnings("serial")
public class RoleTree extends TreeBase {
    private UseCase useCase;
    private HashMap<Integer, UseCase> allUseCases;
    private SecurityGroup role;
    private boolean newRole;
    private boolean roleEditable;
    private boolean cloning;
    private HashMap<String, UseCase> useCaseMap;
    private HashMap<String, UseCase> orphans;
    
	private InfrastructureService infrastructureService;

    public RoleTree() {
        super();
        
        cacheViewIDs.add("/admin/roles.jsp");
    }

    public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	/**
     * @return
     */
    public final String confirm() {
        return "dialog:confirmRoleDelete";
    }

    /**
     * @param event
     */
    public final void confirmReturned(final ReturnEvent event) {
    }

    /**
     * @see TreeBase#setSelectedTreeNode(TreeNode)
     */
    public final void setSelectedTreeNode(final TreeNode selectedTreeNode) {
        reset();

        this.selectedTreeNode = selectedTreeNode;
    }

    /**
     * @return
     */
    public final UseCase getUseCase() {
        if (selectedTreeNode.getType().equals("useCase")) {
            if (useCase == null
                    || (!selectedTreeNode.getIdentifier().equals(
                            useCase.getUseCase()))) {

                try {
                    String useCaseCd = selectedTreeNode.getIdentifier();
                    useCase = infrastructureService.retrieveUseCase(useCaseCd);
                } catch (RemoteException re) {
                    logger.error(re.getMessage(), re);
                    DisplayUtil.displayError("System error. Please contact system administrator");
                }
            }
        } else {
            useCase = null;
        }

        return useCase;
    }

    /**
     * @return
     */
    public final SecurityGroup getRole() {
        if (selectedTreeNode.getType().equals("role") && !cloning) {
            setNewRole(false);

            if ((role == null)
                    || (role.getSecurityGroupId() == null)
                    || (!selectedTreeNode.getIdentifier().equals(
                            role.getSecurityGroupId().toString()))) {

                setRoleEditable(false);
                try {
                    Integer roleId = new Integer(selectedTreeNode
                            .getIdentifier());
                    role = infrastructureService.retrieveSecurityGroup(roleId);
                } catch (RemoteException re) {
                    logger.error(re.getMessage(), re);
                    DisplayUtil.displayError("System error. Please contact system administrator");
                }
            }
        }

        return role;
    }

    /**
     * @see TreeBase#getTreeData()
     */
    @SuppressWarnings("unchecked")
    public final TreeModelBase getTreeData() {
        if (treeData == null) {
            TreeNodeBase root = new TreeNodeBase("root", "Roles", "root", false);
            treeData = new TreeModelBase(root);
            ArrayList<String> treePath = new ArrayList<String>();
            treePath.add("0");

            try {
                SecurityGroup[] roles = infrastructureService
                        .retrieveSecurityGroups();

                for (SecurityGroup lRole : roles) {
                    TreeNodeBase roleNode = new TreeNodeBase("role", lRole
                            .getSecurityGroupName(), lRole.getSecurityGroupId()
                            .toString(), false);

                    UseCase[] useCases = infrastructureService.retrieveUseCases(lRole.getSecurityGroupId());

                    HashMap<Integer, TreeNodeBase> useCaseTree = new HashMap<Integer, TreeNodeBase>();

                    // We have to go through the useCases twice. The first
                    // time through we ensure that all nodes are in the Tree
                    // HashMap. The second loop builds the parent child
                    // relationships.
                    for (UseCase lUseCase : useCases) {
                        TreeNodeBase useCaseNode = new TreeNodeBase("useCase",
                                lUseCase.getUseCaseName(), lUseCase
                                        .getUseCase(), false);

                        useCaseTree.put(lUseCase.getSecurityId(), useCaseNode);
                    }

                    for (UseCase lUseCase : useCases) {
                        TreeNodeBase useCaseNode = useCaseTree.get(lUseCase
                                .getSecurityId());

                        // See if this node has a parent, if so add this
                        // node as a leaf to the parent. Otherwise add it as a 
                        // leaf to the role.
                        if (lUseCase.getParentId() != null) {
                            if (useCaseTree.get(lUseCase.getParentId()) != null) {
                            useCaseTree.get(lUseCase.getParentId())
                                    .getChildren().add(useCaseNode);
                            } else {
                                logger.error("Parent Id not found " + lUseCase.getParentId());
                            }
                        } else {
                            roleNode.getChildren().add(useCaseNode);
                        }
                    }
                    root.getChildren().add(roleNode);
                }
            } catch (RemoteException re) {
                logger.error(re.getMessage(), re);
                DisplayUtil.displayError("System error. Please contact system administrator");
            }
            TreeStateBase treeState = new TreeStateBase();

            treeState.expandPath(treePath.toArray(new String[0]));
            treeData.setTreeState(treeState);
            selectedTreeNode = root;
            current = "root";
        }
        return treeData;
    }

    /**
     * @return
     */
    public final boolean isNewRole() {
        return newRole;
    }

    /**
     * @param value
     */
    public final void setNewRole(final boolean value) {
        this.newRole = value;
    }

    /**
     * @return
     */
    public final String addNewRole() {
        setNewRole(true);

        role = new SecurityGroup();

        role.setAppTypeCd(CompMgr.getAppName());

        return "RoleAdded";
    }

    /**
     * @return
     */
    public final String reset() {
        setNewRole(false);
        setRoleEditable(false);

        allUseCases = null;
        cloning = false;

        role = null;

        return CANCELLED;
    }

    /**
     * @return
     */
    public final boolean isRoleEditable() {
        return roleEditable;
    }

    /**
     * @param value
     */
    public final void setRoleEditable(final boolean value) {
        this.roleEditable = value;
    }

    /**
     * @return
     */
    public final String editRole() {
        setRoleEditable(true);

        return "RoleEditable";
    }

    /**
     * @return
     */
    public final String cloneRole() {
        setRoleEditable(true);
        setNewRole(true);

        cloning = true;

        role.setSecurityGroupId(null);
        role.setSecurityGroupName(null);

        return "RoleCloned";
    }

    /**
     * @return
     */
    public final String saveRole() {
        try {
            if (newRole) {
                infrastructureService.createSecurityGroup(role);
            } else {
                infrastructureService.modifySecurityGroup(role);
                
                // refresh the users drop-down list so that the list is
	            // updated right away
                BasicUsersDef.getData().reload();
            }
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil.displayError("System error. Please contact system administrator");
        }

        reset();

        treeData = null;

        return "RoleSaved";
    }

    /**
     * @return
     */
    public final String deleteRole() {
        try {
            infrastructureService.removeSecurityGroup(role);
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil.displayError("System error. Please contact system administrator");
        }

        reset();

        treeData = null;

        return "RoleDeleted";
    }

    /**
     * @return
     */
    public final List<Integer> getCurrentRoleUseCases() {
        ArrayList<Integer> ret = new ArrayList<Integer>();

        if (role != null) {
            for (UseCase tempUseCase : role.getUseCases().values()) {
                ret.add(tempUseCase.getSecurityId());
            }
        }

        return ret;
    }

    /**
     * @param useCases
     */
    public final void setCurrentRoleUseCases(final List<Integer> useCases) {
        getAllUseCases();

        if (role != null) {
            // Make sure the role has a "clean" map of useCases.
            role.setUseCases(null);

            for (Integer tempId : useCases) {
                UseCase tempUseCase = allUseCases.get(tempId);
                role.addUseCase(tempUseCase);

                Integer parentId = tempUseCase.getParentId();

                // Ensure that this useCases parents are also added.
                while (parentId != null) {
                    tempUseCase = allUseCases.get(parentId);
                    role.addUseCase(tempUseCase);

                    parentId = tempUseCase.getParentId();
                }
            }
        }
        return;
    }

    /**
     * @return
     */
    public final HashMap<String, Integer> getAllUseCases() {
        if (allUseCases == null) {
            allUseCases = new LinkedHashMap<Integer, UseCase>();

            UseCase[] tempUseCases = null;

            try {
                tempUseCases = infrastructureService.retrieveAllUseCases();
            } catch (RemoteException re) {
                logger.error(re.getMessage(), re);
                DisplayUtil.displayError("System error. Please contact system administrator");
            }

            if (tempUseCases != null) {
                for (UseCase tempUseCase : tempUseCases) {
                    if (tempUseCase.getParentId() == null) {
                        allUseCases.put(tempUseCase.getSecurityId(),
                                tempUseCase);

                        if (tempUseCase.getChildren().size() > 0) {
                            buildAllUseCases(tempUseCase.getChildren());
                        }
                    }
                }
            }
        }

        HashMap<String, Integer> ret = new LinkedHashMap<String, Integer>();

        if (allUseCases.size() > 0) {
            for (UseCase tempUseCase : allUseCases.values()) {
                ret.put(tempUseCase.getUseCaseName(), tempUseCase
                        .getSecurityId());
            }
        }

        return ret;
    }

    /**
     * @param children
     */
    private void buildAllUseCases(final ArrayList<UseCase> children) {
        for (UseCase child : children) {
            allUseCases.put(child.getSecurityId(), child);

            if (child.getChildren().size() > 0) {
                buildAllUseCases(child.getChildren());
            }
        }
    }

    /**
     * @return
     */
    public final String loadUseCases() {
        SimpleMenuTree menuTree = (SimpleMenuTree) FacesContext
                .getCurrentInstance().getExternalContext().getSessionMap().get(
                        "menuModel");

        getAllUseCases();

        useCaseMap = new HashMap<String, UseCase>();

        for (UseCase tempUseCase : allUseCases.values()) {
            useCaseMap.put(tempUseCase.getUseCase(), tempUseCase);
        }

        orphans = new HashMap<String, UseCase>();

        try {
            if (menuTree != null) {
                processUseCaseChildren(menuTree.getChildren());
                // If there are any orphans left, add them with null parents.
                if (orphans.size() > 0) {
                    for (UseCase tempUseCase : orphans.values()) {
                        infrastructureService.createUseCase(tempUseCase);
                    }
                }
            }
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil.displayError("System error. Please contact system administrator");
        }

        reset();

        return "UseCasesLoaded";
    }

    /**
     * @param children
     */
    private void processUseCaseChildren(final List<SimpleMenuItem> children) {
        for (SimpleMenuItem item : children) {
            if ((item.getType().compareTo("global") != 0)
                    && (!useCaseMap.containsKey(item.getName()))) {
                UseCase tempUseCase = new UseCase();
                tempUseCase.setUseCaseName(item.getLabel());
                tempUseCase.setUseCase(item.getName());

                String useCaseStr = tempUseCase.getUseCase();
                String useCaseParent = null;

                // If the use case contains no '.', we'll assume this is a new
                // parent, and add it.
                if (useCaseStr.lastIndexOf('.') > 0) {
                    useCaseParent = useCaseStr.substring(0, useCaseStr
                            .lastIndexOf('.'));

                    // See if the parent is currently defined. If not
                    // save for later.
                    if (!useCaseMap.containsKey(useCaseParent)) {
                        orphans.put(useCaseStr, tempUseCase);
                    } else {
                        UseCase parentUseCase = useCaseMap.get(useCaseParent);

                        tempUseCase.setParentId(parentUseCase.getSecurityId());

                        try {
                            tempUseCase = infrastructureService.createUseCase(
                                    tempUseCase);
                        } catch (RemoteException re) {
                            logger.error(re.getMessage(), re);
                            DisplayUtil.displayError("System error. Please contact system administrator");
                        }

                        if (tempUseCase != null) {
                            useCaseMap.put(useCaseStr, tempUseCase);
                        }

                        // Now we need to go through the orphan list and
                        // see if this was a parent for any of the
                        // orphans.
                    }
                } else {
                    try {
                        tempUseCase = infrastructureService.createUseCase(
                                tempUseCase);
                    } catch (RemoteException re) {
                        logger.error(re.getMessage(), re);
                        DisplayUtil.displayError("System error. Please contact system administrator");
                    }
                    
                    if (tempUseCase != null) {
                        useCaseMap.put(useCaseStr, tempUseCase);
                    }
                }
            }

            if (item.getChildren() != null) {
                processUseCaseChildren(item.getChildren());
            }
        }
    }
    
    public void clearCache() {
        super.clearCache();
        
        if (allUseCases != null) {
            allUseCases.clear();
            allUseCases = null;
        }

        if (useCaseMap != null) {
            useCaseMap.clear();
            useCaseMap = null;
        }

        if (orphans != null) {
            orphans.clear();
            orphans = null;
        }
    }  
}
