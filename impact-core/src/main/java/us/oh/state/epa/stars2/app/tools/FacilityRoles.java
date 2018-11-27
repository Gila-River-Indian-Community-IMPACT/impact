package us.oh.state.epa.stars2.app.tools;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRole;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

@SuppressWarnings("serial")
public class FacilityRoles extends BulkOperation {

    private FacilityList[] facilities;
    private Facility firstFacility;
    private boolean facilityRoleEditable;
    private Integer _userIdForRole;
    private boolean affectMultipleDolaas;

    public FacilityRoles() {
        super();
        setButtonName("Reassign Roles");
        setNavigation("dialog:facilityRoles");
    }

    /**
     * Each bulk operation is responsible for providing a search operation based
     * on the parameters gathered by the BulkOperationsCatalog bean. The search
     * is run when the "Select" button on the Bulk Operations screen is clicked.
     * The BulkOperationsCatalog will then display the results of the search.
     * Below the results is a "Bulk Operation" button.
     */
    public final void searchFacilities(BulkOperationsCatalog lcatalog)
        throws RemoteException {

        this.catalog = lcatalog;

		facilities = getFacilityService().searchFacilities(
				catalog.getFacilityNm(), catalog.getFacilityId(),
				catalog.getCompanyName(), null, catalog.getCounty(),
				catalog.getOperatingStatusCd(), catalog.getDoLaa(),
				catalog.getNaicsCd(), catalog.getPermitClassCd(),
				catalog.getTvPermitStatus(), catalog.getAddress1(), null, null,
				null, null, true, catalog.getFacilityTypeCd());

        facilityRoleEditable = true;
        ArrayList<FacilityList> tfs = new ArrayList<FacilityList>();
        setMaximum(facilities.length);
        setValue(0);
        
        int i = 0;
        setSearchStarted(true);
        
        for (FacilityList facility : facilities) {
        	if (catalog.getReportCategoryCd() != null) {
        		if (!catalog.getReportCategoryCd().equals(facility.getReportingTypeCd())) {
        			continue;
        		}
         	}
    		tfs.add(facility);
        	setValue(++i);
        }
        facilities = tfs.toArray(new FacilityList[0]);

        setValue(facilities.length);
        
        catalog.setFacilities(facilities);
        setSearchCompleted(true);

    }

    /**
     * When the "Bulk Operation" button is clicked, the BulkOperationsCatalog
     * class will call this method, ensure that the BulkOperation class is
     * placed into the jsf context, and return the value of the getNavigation()
     * method to the jsf controller. Any further requests from the page that is
     * navigated to are handled by this bean in the normal way. The default
     * version does no preliminary work.
     * @throws RemoteException 
     */
    public final void performPreliminaryWork(BulkOperationsCatalog lcatalog) throws RemoteException {

        FacilityList[] selectedFacilities = lcatalog.getSelectedFacilities();
        Integer fpid = selectedFacilities[0].getFpId();

        try {
            this.firstFacility = getFacilityService().retrieveFacilityProfile(fpid);
            
            affectMultipleDolaas = false;
            String s = "";
            for(FacilityList fList : selectedFacilities){            	
            	if(s.length() > 0 && !fList.getDoLaaCd().equals(s)){
            		affectMultipleDolaas = true;            		
            		break;
            	}            		
            	s = fList.getDoLaaCd();            	
            }            
        }
        catch (RemoteException re) {
            logger.error(re.getMessage(), re);
        	throw new RemoteException("Role update failed: System Error");
        }
        return;
    }

	public final String performPostWork(BulkOperationsCatalog lcatalog) {

		int currentUserId = InfrastructureDefs.getCurrentUserId();
		FacilityList[] selectedFacilities = lcatalog.getSelectedFacilities();
		String roleCd = catalog.getFacilityRole();

		try {
			getFacilityService().bulkUpdateFacilityRoles(roleCd, selectedFacilities, currentUserId, _userIdForRole);
		} catch (Exception e) {
			DisplayUtil.displayError("Bulk role update failed.");
			logger.error(e.getMessage(), e);
			return FAIL;
		}

		DisplayUtil.displayInfo("Operation completed successfully.");
		return SUCCESS;

    }

    public final boolean isFacilityRoleEditable() {
        return facilityRoleEditable;
    }

    public final void setFacilityRoleEditable(boolean facilityRoleEditable) {
        this.facilityRoleEditable = facilityRoleEditable;
    }

    public final FacilityRole[] getRoles() {
        return firstFacility.getFacilityRoles().values().toArray(new FacilityRole[0]);
                                                                 
    }

    public final FacilityRole getRole() {
    	FacilityRole ret = null;
    	if(firstFacility != null) {
    		HashMap<String, FacilityRole> frs = firstFacility.getFacilityRoles();
    		ret = frs.get(catalog.getFacilityRole());
    	}
        return ret;
    }

    public final Integer getUserIdForRole() {
        return _userIdForRole;
    }

    public final void setUserIdForRole(Integer userIdForRole) {
        _userIdForRole = userIdForRole;
    }

	public final boolean isAffectMultipleDolaas() {
		return affectMultipleDolaas;
	}

	public final void setAffectMultipleDolaas(boolean affectMultipleDolaas) {
		this.affectMultipleDolaas = affectMultipleDolaas;
	}    

}
