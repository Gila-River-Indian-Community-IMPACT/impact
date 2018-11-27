package us.wy.state.deq.impact.webcommon.tools;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.wy.state.deq.impact.database.dbObjects.tool.FixmeCompany;

public class BulkFixmeOwnership extends AppBase {

	private static final long serialVersionUID = -2197525775053036222L;
	private List<FixmeCompany> fixmeCompanies = new ArrayList<FixmeCompany>(0);;
	
	private FacilityService facilityBO;
	

	public BulkFixmeOwnership() {
	}

	public FacilityService getFacilityService() {
		return facilityBO;
	}
	
	public void setFacilityService(FacilityService facilityService) {
		// this method gets called when JSF injects the service object during
		// object initialization.  to ensure that loadFixmeCompanies() also gets
		// called while the object is initialized, but after the service object
		// has been injected, include the call here, instead of in the 
		// constructor.
		this.facilityBO = facilityService;
	}
	
	public  void setFixmeCompanies(List<FixmeCompany> fixmeCompanies) {
		this.fixmeCompanies = fixmeCompanies;
	}

	public  List<FixmeCompany> getFixmeCompanies() {
		return this.fixmeCompanies;
	}

	public  boolean isNeedFix() {
		loadFixmeCompanies(); // when load the page re-load the data
		return !fixmeCompanies.isEmpty();
	}

	public  void removeFixmeCompanies() {

		try {
			boolean ret = getFacilityService().removeFixmeCompanies();

			if (ret) {
				DisplayUtil.displayInfo("Bulk FIXME ownership change executed successfully");
				loadFixmeCompanies();
			} else {
				DisplayUtil.displayError("Bulk FIXME ownership failed");
			}

		} catch (RemoteException ex) {
			DisplayUtil.displayError(ex.getMessage());
			return;
		}
	}

	private void loadFixmeCompanies() {
		fixmeCompanies = null;

		try {
			fixmeCompanies = getFacilityService().retrieveFixmeCompanies();

		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("failed");
		}
	}

}
