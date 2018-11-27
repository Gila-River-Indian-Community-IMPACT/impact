package us.oh.state.epa.stars2.webcommon.bean;

import java.rmi.RemoteException;
import javax.faces.component.html.HtmlOutputText;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.webcommon.AppBase;

@SuppressWarnings("serial")
public class FacilityName  extends AppBase {
    private HtmlOutputText outputText;

	private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}
    public final HtmlOutputText getOutputText() {
        return outputText;
    }

    public final void setOutputText(HtmlOutputText text) {
        this.outputText = text;
    }

    public final String getFacilityName() {
        String facilityName = "";
        String facilityId = (String) outputText.getAttributes().get("facilityId");
        if (facilityId != null) {
            try {
                Facility facility = getFacilityService().retrieveFacilityData(facilityId, -1);
                if (facility != null) {
                    facilityName = facility.getName();
                } else {
                    logger.error("No facility found with id " + facilityId);
                }
            } catch (RemoteException e) {
                logger.error("Exception retrieving facility data for facility id " + facilityId, e);
            }
        }
        return facilityName;
    }
}
