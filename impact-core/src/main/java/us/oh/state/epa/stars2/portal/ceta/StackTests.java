package us.oh.state.epa.stars2.portal.ceta;
import java.rmi.RemoteException;

import us.oh.state.epa.stars2.portal.facility.FacilityProfile;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ceta.StackTestsCommon;

@SuppressWarnings("serial")
public class StackTests extends StackTestsCommon {

    public StackTests() {
        super();
    }
    
    public String initStackTests() {
        reset();
        setFromFacility(true);
        StackTestDetail stackTestDetail = (StackTestDetail)FacesUtil.getManagedBean("stackTestDetail");
        stackTestDetail.setFacility(null);  // init in case the first operation is for new Stack test.
        FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
        try {
            facility = getFacilityService().retrieveFacility(fp.getFpId());
        } catch (RemoteException re) {
            DisplayUtil.displayError("Failed to read facility inventory for fpId " + fp.getFpId());
            handleException(re);
            return null;
        }
        StackTestSearch stackTestSearch = (StackTestSearch)FacesUtil.getManagedBean("stackTestSearch");
        stackTestSearch.setFromFacility(true);
        if (facility != null) {
            try {
                stackTestList = getStackTestService().searchStackTests(facility.getFacilityId());
                if (stackTestList.length == 0)
                	DisplayUtil.displayInfo("There are no Stack Tests available for this facility.");
            } catch (RemoteException re) {
                handleException(re);
            }
        }
        return "facilities.profile.stackTest";
    }
    
    public String initStackTestsForCancelEdit() {
        reset();
        StackTestDetail stackTestDetail = (StackTestDetail)FacesUtil.getManagedBean("stackTestDetail");
        stackTestDetail.setFacility(null);  // init in case the first operation is for new Stack test.
        FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
        try {
            facility = getFacilityService().retrieveFacility(fp.getFpId());
        } catch (RemoteException re) {
            DisplayUtil.displayError("Failed to read facility inventory for fpId " + fp.getFpId());
            handleException(re);
            return null;
        }
        StackTestSearch stackTestSearch = (StackTestSearch)FacesUtil.getManagedBean("stackTestSearch");
        stackTestSearch.setFromFacility(true);
        if (facility != null) {
            try {
                stackTestList = getStackTestService().searchStackTests(facility.getFacilityId());
            } catch (RemoteException re) {
                handleException(re);
            }
        }
        if (isFromFacility())
        	return "facilities.profile.stackTest";
        else
        	return "ceta.fceDetail";
    }
    
    public final String newStackTest() {
    	StackTestDetail stackTestDetail = (StackTestDetail)FacesUtil.getManagedBean("stackTestDetail");
        return stackTestDetail.newStackTest(facility);
    }

	public final String getHomeStackTestReports() {
		initStackTests();
		FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
        fp.setEditStaging(false);
        fp.setStaging(false);
		return "home.stackTestReports";
	}
}