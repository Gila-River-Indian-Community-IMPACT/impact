package us.oh.state.epa.stars2.app.admin;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;
import org.apache.myfaces.custom.tree2.TreeStateBase;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRole;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SecurityGroup;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.TreeBase;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

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
public class UserTree extends TreeBase {
    private UserDef user;
    private boolean editable;
    private HashMap<Integer, SecurityGroup> roles;
    
	private InfrastructureService infrastructureService;
	private FacilityService facilityService;
    private ReadWorkFlowService workFlowService;

    public ReadWorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public void setWorkFlowService(ReadWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    public UserTree() {
        cacheViewIDs.add("/admin/users.jsp");
    }

    public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	/**
     * @see TreeBase#setSelectedTreeNode(TreeNode)
     */
    public final void setSelectedTreeNode(final TreeNode selectedTreeNode) {
        setEditable(false);
        this.selectedTreeNode = selectedTreeNode;
    }

    @SuppressWarnings("unchecked")
    public TreeModelBase getTreeData() {
        if (treeData == null) {
            TreeNodeBase root = new TreeNodeBase("root", "Users", "root", false);
            treeData = new TreeModelBase(root);
            ArrayList<String> treePath = new ArrayList<String>();
            treePath.add("0");
            
            TreeNodeBase activeUsersRoot = new TreeNodeBase("active", "Active Users", "active", false);
            root.getChildren().add(activeUsersRoot);
            treePath.add("0:0");
            
            TreeNodeBase inactiveUsersRoot = new TreeNodeBase("inactive", "Inactive Users", "inactive", false);
            root.getChildren().add(inactiveUsersRoot);
            treePath.add("0:1");
        
            try {
                SimpleIdDef[] users = infrastructureService.retrieveUserList(false);
        
                int activeCount = 0;
                int inactiveCount = 0;
                for (SimpleIdDef inUser : users) {
                    if (inUser.isDeprecated()) {
                        TreeNodeBase userNode = new TreeNodeBase("activeUser", inUser
                                .getDescription(), inUser.getId().toString(), false);
                    	activeUsersRoot.getChildren().add(userNode);
                        treePath.add("0:0:" + activeCount++);
                    } else {
                        TreeNodeBase userNode = new TreeNodeBase("inactiveUser", inUser
                                .getDescription(), inUser.getId().toString(), false);
                    	inactiveUsersRoot.getChildren().add(userNode);
                        treePath.add("0:1:" + inactiveCount++);
                    }
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
    public final UserDef getUser() {
        if ("activeUser".equals(selectedTreeNode.getType())
        		|| "inactiveUser".equals(selectedTreeNode.getType())) {
            if (user == null
                    || (!selectedTreeNode.getIdentifier().equals(
                            user.getUserId().toString()))) {

                setEditable(false);

                try {
                    Integer userId = new Integer(selectedTreeNode
                            .getIdentifier());
                    // Not needed but do it anyway
                    user = infrastructureService.retrieveUserDef(userId);
                } catch (RemoteException re) {
                    logger.error(re.getMessage(), re);
                    DisplayUtil.displayError("System error. Please contact system administrator");
                }
            }
        } else {
            user = null;
        }

        return user;
    }

    /**
     * @return
     */
    public final List<Integer> getCurrentUserRoles() {
        ArrayList<Integer> ret = new ArrayList<Integer>();

        if (user != null) {
            for (SecurityGroup tempGroup : user.getSecurityGroups()) {
                ret.add(tempGroup.getSecurityGroupId());
            }
        }

        return ret;
    }

    /**
     * @param currentRoles
     */
    public final void setCurrentUserRoles(final List<Integer> currentRoles) {
        if (user != null) {
            ArrayList<SecurityGroup> tempRoles = new ArrayList<SecurityGroup>();

            for (Integer tempId : currentRoles) {
                tempRoles.add(roles.get(tempId));
            }

            user.setSecurityGroups(tempRoles);
        }
    }

    /**
     * @return
     */
    public final HashMap<String, Integer> getAllRoles() {
        if (roles == null) {
            roles = new HashMap<Integer, SecurityGroup>();
            SecurityGroup[] tempRoles = null;

            try {
                tempRoles = infrastructureService.retrieveSecurityGroups();
            } catch (RemoteException re) {
                logger.error(re.getMessage(), re);
                DisplayUtil.displayError("System error. Please contact system administrator");
            }

            if (tempRoles != null) {
                for (SecurityGroup tempRole : tempRoles) {
                    roles.put(tempRole.getSecurityGroupId(), tempRole);
                }
            }
        }

        HashMap<String, Integer> ret = new HashMap<String, Integer>();

        if (roles.size() > 0) {
            for (SecurityGroup tempRole : roles.values()) {
                ret.put(tempRole.getSecurityGroupName(), tempRole
                        .getSecurityGroupId());
            }
        }

        return ret;
    }

    /**
     * @return
     */
    public final boolean isEditable() {
        return editable;
    }

    /**
     * @param editable
     */
    public final void setEditable(final boolean editable) {
        this.editable = editable;
        this.positionNumberChangeOccurred = false;
    }

    /**
     * @return
     */
    public final String editUser() {
        setEditable(true);

        return EDITABLE;
    }

    /**
     * @return
     */
    public final String deactivateUser() {
    	String ret = SUCCESS;
    	boolean okToDeactivate = true;
        ArrayList<String> ss = new ArrayList<String>();
        ss.add(ActivityStatusDef.IN_PROCESS);
        ss.add(ActivityStatusDef.NOT_COMPLETED);
        ss.add(ActivityStatusDef.REFERRED);
        
        ProcessActivity pa = new ProcessActivity();
        pa.setUserId(user.getUserId());
        pa.setActivityStatusCds(ss);
        pa.setInStatus(true);
        pa.setPerformerTypeCd("M");
        pa.setUnlimitedResults(true);
        try {
            ProcessActivity[] ta = getWorkFlowService().retrieveActivityList(pa);
            if (ta.length > 0) {
            	okToDeactivate = false;
            	DisplayUtil.displayError("User " + user.getUserLastNm() + ", " + user.getUserFirstNm() + 
            			" has been assigned one or more workflow tasks. You must reassign these tasks " +
            			"using the Tools -> Bulk Operations -> Workflow Operations -> User Task Reassignments " +
            			"window before deactivating this user.");
            }
        } catch (RemoteException re) {
        	handleException(re);
        }
        
        try {
        	FacilityRole[] roles = getFacilityService().retrieveFacilityRolesByUserId(user.getUserId());
        	if (roles.length > 0) {
            	okToDeactivate = false;
            	DisplayUtil.displayError("User " + user.getUserLastNm() + ", " + user.getUserFirstNm() + 
            			" has been assigned one or more facility roles. You must reassign these roles " +
            			"using the Tools -> Bulk Operations -> Facility Operations -> User Facility Roles " +
            			"window before deactivating this user.");
        	}
        } catch (RemoteException re) {
        	handleException(re);
        }
        
        if (okToDeactivate) {
	        user.setActiveInd("N");
	    	String userName = user.getUserLastNm() + ", " + user.getUserFirstNm();
	        ret = saveUser();
        	DisplayUtil.displayInfo("User " + userName + 
        			" has deactivated. This user may still appear in user drop down lists for an " +
        			"hour or more, but will eventually be removed.");
        }
        return ret;
    }

    /**
     * @return
     */
    public final String reactivateUser() {
    	String ret = SUCCESS;
    	String userName = user.getUserLastNm() + ", " + user.getUserFirstNm();
        user.setActiveInd("Y");
        ret = saveUser();
    	DisplayUtil.displayInfo("User " + userName + 
    			" has been reactivated. This user may not appear in user drop down lists for an " +
    			"hour or more, but will eventually be restored.");
    	return ret;
    }

    private boolean positionNumberChangeOccurred = false;
    
    public void positionNumberChanged(ValueChangeEvent event) {
    	positionNumberChangeOccurred = true;
    }
    
    /**
     * @return
     */
    public final String saveUser() {
    	String otherUser = null;
    	try {
	    	if (positionNumberChangeOccurred) {
				otherUser = infrastructureService.retrieveUserWithPositionNumber(
						user.getPositionNumber());
	    	}
	    	if (null == otherUser) {
	    		
	            if (!infrastructureService.modifyUser(user)){
	            	return FAIL;
	            }	            
	            // refresh the users drop-down list so that the list is
	            // updated right away
	            BasicUsersDef.getData().reload();
	    	} else {
	    		DisplayUtil.displayError("Position number is already assigned to a user: " + otherUser);
	    	}
    	} catch (RemoteException re) {
    		logger.error(re.getMessage(), re);
    		DisplayUtil.displayError("System error. Please contact system administrator");
    	}
        user = null;

        setEditable(false);
        return SUCCESS;
    }

    /**
     * @return
     */
    public final String cancelEdit() {
        user = null;

        setEditable(false);

        return CANCELLED;
    }
    
    public void clearCache() {
        super.clearCache();
        
        if (roles != null) {
            roles.clear();
            roles = null;
        }
    }  
}
