package us.oh.state.epa.stars2.app.tools;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRole;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;


public class UserFacilityRoles extends BulkOperation {
	
	private static final long serialVersionUID = 638356631078196895L;

	private FacilityList[] facilities;
    private Facility firstFacility;
    private boolean facilityRoleEditable;
    private Integer userIdForRole;
    private boolean affectMultipleDolaas;

    public boolean isAffectMultipleDolaas() {
		return affectMultipleDolaas;
	}

	public void setAffectMultipleDolaas(boolean affectMultipleDolaas) {
		this.affectMultipleDolaas = affectMultipleDolaas;
	}

	public FacilityList[] getFacilities() {
		return facilities;
	}

	public void setFacilities(FacilityList[] facilities) {
		this.facilities = facilities;
	}

	public Facility getFirstFacility() {
		return firstFacility;
	}

	public void setFirstFacility(Facility firstFacility) {
		this.firstFacility = firstFacility;
	}

	public boolean isFacilityRoleEditable() {
		return facilityRoleEditable;
	}

	public void setFacilityRoleEditable(boolean facilityRoleEditable) {
		this.facilityRoleEditable = facilityRoleEditable;
	}

    public final Integer getUserIdForRole() {
		return userIdForRole;
	}

	public final void setUserIdForRole(Integer userIdForRole) {
		this.userIdForRole = userIdForRole;
	}

	public UserFacilityRoles() {
        super();
        setButtonName("Reassign Facility Roles");
        setNavigation("dialog:userFacilityRoles");
    }

	/**
	 * Each bulk operation is responsible for providing a search operation based
	 * on the paramters gathered by the BulkOperationsCatalog bean. The search
	 * is run when the "Select" button on the Bulk Operations screen is clicked.
	 * The BulkOperationsCatalog will then display the results of the search.
	 * Below the results is a "Bulk Operation" button.
	 */
	public final void searchFacilities(BulkOperationsCatalog lcatalog) throws RemoteException {

		this.catalog = lcatalog;
		catalog.setRoleOperation(true);

		facilities = getFacilityService().searchFacilities(catalog.getFacilityNm(), catalog.getFacilityId(),
				catalog.getCompanyName(), null, catalog.getCounty(), catalog.getOperatingStatusCd(), catalog.getDoLaa(),
				catalog.getNaicsCd(), catalog.getPermitClassCd(), catalog.getTvPermitStatus(), catalog.getAddress1(),
				null, null, null, null, true, catalog.getFacilityTypeCd());

		facilityRoleEditable = true;

		setMaximum(facilities.length);
		setValue(0);

		int i = 0;
		setSearchStarted(true);
		ArrayList<FacilityList> tfs = new ArrayList<FacilityList>();

		FacilityRole[] roles = null;

		try {
			roles = getFacilityService().retrieveFacilityRoles(facilities, catalog.getStaffId());
		} catch (RemoteException e) {
			logger.error("", e);
		}

		HashMap<String, FacilityList> facMap = new HashMap<String, FacilityList>();
		for (FacilityList facility : facilities) {
			facMap.put(facility.getFacilityId(), facility);
		}
		for (FacilityRole role : roles) {
			if (catalog.getSearchThread().isInterrupted()) {
				// user clicked on reset stop loop and jump out.
				break;
			}
			FacilityList facility = facMap.get(role.getFacilityId());
			if (facility != null) {
				FacilityList newFacilityL = new FacilityList(facility);
				newFacilityL.setUserId(role.getUserId());
				newFacilityL.setRoleCd(role.getFacilityRoleCd());
				if (tfs != null) {
					tfs.add(newFacilityL);
				}
			}
			// add role code and user Id information to facility list
			// prepareRoles(facility, tfs);
			setValue(i++);
		}

		facilities = tfs.toArray(new FacilityList[0]);
		catalog.setFacilities(facilities);
		setSearchCompleted(true);

	}

    public final String performPostWork(BulkOperationsCatalog lcatalog) {
    	
        FacilityList[] selectedFacilities = lcatalog.getSelectedFacilities();
        
        HashMap<String, List<FacilityList>> roleToFacilities = new HashMap<String, List<FacilityList>>();
        
        for (FacilityList facilityL : selectedFacilities) {
        	String role = facilityL.getRoleCd();
        	List<FacilityList> facilitiesPerRole = roleToFacilities.get(role);
        	if (facilitiesPerRole == null) {
        		facilitiesPerRole = new ArrayList<FacilityList>();
        		roleToFacilities.put(role, facilitiesPerRole);
        	}
        	facilitiesPerRole.add(facilityL);
        }
        
        for (String roleCd : roleToFacilities.keySet()) {
        	try {
        		List<FacilityList> currFacs = roleToFacilities.get(roleCd);
        		if (currFacs != null && currFacs.size() > 0) {
        			getFacilityService().bulkUpdateFacilityRoles(roleCd, currFacs.toArray(new FacilityList[0]), catalog.getStaffId(), getUserIdForRole());
        		}
        	} catch (DAOException daoe) {
        		DisplayUtil.displayError("Role update failed for role " + roleCd + ".");
        		logger.error(daoe.getMessage(), daoe);
        	}
        }
       
        //for (FacilityList facilityL : selectedFacilities) {
        //    try {
        //        Facility facility = getFacilityService().retrieveFacility(facilityL.getFacilityId());
        //        HashMap<String, FacilityRole> frs = facility.getFacilityRoles();
        //        for (FacilityRole role : frs.values()) {
        //        	if (role.getUserId().equals(catalog.getStaffId()) 
        //        			&& role.getFacilityRoleCd().equals(facilityL.getRoleCd())) {
        //        		role.setUserId(userIdForRole);
        //        	}
        //        }
        //        facility.setFacilityRoles(frs);
        //        getFacilityService().updateFacilityRoles(frs.values().toArray(new FacilityRole[0]), 
        //        									facility, currentUserId);
        //    }
        //    catch (RemoteException re) {
        //        DisplayUtil.displayError("Role update failed for facility " + facilityL.getFacilityId());
        //        logger.error(re.getMessage(), re);
        //    }

        //}

        return SUCCESS;
    }

	@Override
	public void performPreliminaryWork(BulkOperationsCatalog catalog)
			throws RemoteException {
		// No preliminary work is needed
		
	}   

}
