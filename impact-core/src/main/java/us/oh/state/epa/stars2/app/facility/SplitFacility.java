package us.oh.state.epa.stars2.app.facility;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

public class SplitFacility extends AppBase {

	private static final long serialVersionUID = 3203056400784568297L;
	private String facilityId;
	private String facilityName;
	private Integer ownerCompanyId;
	private Facility facility;
	private String popupRedirectOutcome;
	
	private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public SplitFacility() {
		resetSplitFacility();
	}

	public final void resetSplitFacility() {
		facilityId = null;
		facilityName = null;
		ownerCompanyId = null;
		facility = new Facility();
	}

	public final void submitSplitFacility(ActionEvent actionEvent) {
		
		try {
			int currentUserId = InfrastructureDefs.getCurrentUserId();

			Facility splitFacility = getFacilityService().splitFacility(
					facility.getFpId(), facilityName, ownerCompanyId,
					currentUserId);

			// in case facilityProfile bean pointing at fpId with old facility
			// ID or other facility or none
			FacilityProfile fp = (FacilityProfile) FacesUtil
					.getManagedBean("facilityProfile");
			fp.setFpId(splitFacility.getFpId());
			fp.submitProfile();

			popupRedirectOutcome = "facilityProfile";
			
			resetSplitFacility();
			DisplayUtil.displayInfo("Facility split successfully.");
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Split Facility failed.");
			clearButtonClicked();
		}

		clearButtonClicked();
		FacesUtil.returnFromDialogAndRefresh();
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String confirm() {
		
		 // Protect from multiple clicks.
		if (!firstButtonClick()) {
			return null;
		}
		
		List<ValidationMessage> valMsgs = new ArrayList<ValidationMessage>();

		if (facilityId == null || facilityId.equals("")) {
			valMsgs.add(new ValidationMessage("facId",
					"Facility ID is required.",
					ValidationMessage.Severity.ERROR, null));
		} else if (facilityId.length() > 7) {
			valMsgs.add(new ValidationMessage(
					"facId",
					"Invalid Facility ID; The length must be less than 7 letter.",
					ValidationMessage.Severity.ERROR, null));
		}

		if (facilityName == null || facilityName.equals("")) {
			valMsgs.add(new ValidationMessage("facName",
					"New facility Name is required.",
					ValidationMessage.Severity.ERROR, null));
		}

		if (ownerCompanyId == null || ownerCompanyId.equals("")) {
			valMsgs.add(new ValidationMessage("ownerCompanyCd",
					"New Facility Company Name is required.",
					ValidationMessage.Severity.ERROR, null));
		}

		if (valMsgs.size() > 0) {
			if (displayValidationMessages("splitFacility:",
					valMsgs.toArray(new ValidationMessage[0]))) {
				clearButtonClicked();
				return null;
			}
		}

		if (!Utility.isNullOrEmpty(facilityId)) {
			if (!facilityId.startsWith("F")) {
				String format = "F%06d";
				int tempId;
				try {
					tempId = Integer.parseInt(facilityId);
					setFacilityId(String.format(format, tempId));
				} catch (NumberFormatException nfe) {
					DisplayUtil
							.displayError("Split Facility ID failed; Facility: [" + facilityId + "] does not exist.");
					clearButtonClicked();
					return null;
				}
			}
		}
		
		try {
			facility = getFacilityService().retrieveFacility(facilityId);
			if (facility == null) {
				DisplayUtil
						.displayError("Split Facility ID failed; Facility: ["
								+ facilityId + "] does not exist.");
				clearButtonClicked();
				return null;
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Split Facility failed");
			clearButtonClicked();
			return null;
		}

		return "dialog:splitFacility";
	}

	public void noConfirm(ActionEvent actionEvent) {
		popupRedirectOutcome = null;
		clearButtonClicked();
		FacesUtil.returnFromDialogAndRefresh();
	}

	public String getPopupRedirect() {
		if (popupRedirectOutcome != null) {
			FacesUtil.setOutcome(null, popupRedirectOutcome);
			popupRedirectOutcome = null;
		}
		return null;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public Integer getOwnerCompanyId() {
		return this.ownerCompanyId;
	}

	public void setOwnerCompanyId(Integer ownerCompanyId) {
		this.ownerCompanyId = ownerCompanyId;
	}

	@Override
	public void clearCache(){
		this.resetSplitFacility();
	}
}
