package us.oh.state.epa.stars2.app.ceta;
import java.rmi.RemoteException;

import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
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
    
    public final String submitStackTestPopup() {
        String rtn = null;
        // This is to display a datagrid of stack tests associated with facilityId and visitDate.
        try {
            if(facilityForStackTestPopup == null || 
                    !facilityForStackTestPopup.getFacilityId().equals(facilityIdForStackTestPopup)) {
                facilityForStackTestPopup = getFacilityService().retrieveFacility(facilityIdForStackTestPopup);
            }
        } catch (RemoteException re) {
            DisplayUtil.displayError("Failed to read facility inventory for facility " + facilityIdForStackTestPopup);
            handleException(re);
            return null;
        }
        try {
            stackTestList = getStackTestService().searchStackTests(facilityIdForStackTestPopup, visitDate);
            
            rtn = "dialog:stackTestsForVisit";
        } catch(RemoteException re) {
            handleException(re);
        }
        return rtn;
    }

    public String chgFceAssign() {
        // button is on the stackTest popup page of stack tests for the visit date
        // Get all Inspections for facility
        try {
            FullComplianceEval[] all = getFullComplianceEvalService().retrieveFceBySearch(facilityIdForStackTestPopup);
            if(all.length == 0) {
                DisplayUtil.displayInfo("Facility " + facilityIdForStackTestPopup + " has no Inspections");
                return null;
            }
            SiteVisitDetail siteVisitDetail = (SiteVisitDetail)FacesUtil.getManagedBean("siteVisitDetail");
            TableSorter allFCEs = new TableSorter();
            allFCEs.setWrappedData(all);
            siteVisitDetail.setAllFCEs(allFCEs);
        } catch (RemoteException re) {
            handleException(re);
        }
        return "dialog:fceReassign";
    }
}
