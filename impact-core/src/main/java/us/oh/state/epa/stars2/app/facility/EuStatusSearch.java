package us.oh.state.epa.stars2.app.facility;

import java.rmi.RemoteException;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityEmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;

@SuppressWarnings("serial")
public class EuStatusSearch extends AppBase {
    private String facilityName;
    private String facilityId;
    private String facilityOperatingStatusCd = null;
    private String euOperatingStatusCd = EuOperatingStatusDef.NI;
    private String doLaaCd;
    private String permitClassCd;
    private FacilityEmissionUnit[] eus;
    private boolean hasSearchResults;
	private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    public EuStatusSearch() {
        super();
        cacheViewIDs.add("/facilities/euStatusSearch.jsp");
    }
    
    public final String getFacilityId() {
        return facilityId;
    }

    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public final String getFacilityName() {
        return facilityName;
    }

    public final void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }


    public final String getDoLaaCd() {
        return doLaaCd;
    }

    public final void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    public final List<SelectItem> getDoLaas() {
        return DoLaaDef.getData().getItems().getAllSearchItems();
    }

    public final String getPermitClassCd() {
        return permitClassCd;
    }

    public final void setPermitClassCd(String permitClassCd) {
        this.permitClassCd = permitClassCd;
    }

    public String submitSearch() {
        eus = null;
        hasSearchResults = false;

        try {
            eus = getFacilityService().searchEuStatus(facilityName, facilityId,
                    facilityOperatingStatusCd, euOperatingStatusCd,
                    doLaaCd, permitClassCd, unlimitedResults());
            DisplayUtil.displayHitLimit(eus.length);
            if (eus.length == 0) {
                DisplayUtil
                        .displayInfo("There are no emissions units that match the search criteria");
            } else {
                hasSearchResults = true;
            }
        } catch (RemoteException re) {
        	handleException(re);
            DisplayUtil.displayError("Search failed");
            eus = new FacilityEmissionUnit[0];
        }

        return null;
    }

    public String reset() {
        facilityName = null;
        facilityId = null;
        facilityOperatingStatusCd = null;
        euOperatingStatusCd = EuOperatingStatusDef.NI;
        doLaaCd = null;
        permitClassCd = null;
        eus = null;
        hasSearchResults = false;
        return null;
    }

    public final boolean getHasSearchResults() {
        return hasSearchResults;
    }

    public FacilityEmissionUnit[] getEus() {
        return eus;
    }
    
    public void restoreCache() {
    }

    public void clearCache() {
        eus = null;
        hasSearchResults = false;
	}

    public final void setHasSearchResults(boolean hasSearchResults) {
        this.hasSearchResults = hasSearchResults;
    }

    public String getEuOperatingStatusCd() {
        return euOperatingStatusCd;
    }

    public void setEuOperatingStatusCd(String euOperatingStatusCd) {
        this.euOperatingStatusCd = euOperatingStatusCd;
    }

    public String getFacilityOperatingStatusCd() {
        return facilityOperatingStatusCd;
    }

    public void setFacilityOperatingStatusCd(String facilityOperatingStatusCd) {
        this.facilityOperatingStatusCd = facilityOperatingStatusCd;
    }

    public void setEus(FacilityEmissionUnit[] eus) {
        this.eus = eus;
    }
}
