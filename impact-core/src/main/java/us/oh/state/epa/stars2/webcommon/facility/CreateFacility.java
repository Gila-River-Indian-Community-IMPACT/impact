package us.oh.state.epa.stars2.webcommon.facility;

import java.rmi.RemoteException;
import java.sql.Timestamp;

import javax.faces.context.FacesContext;

import us.oh.state.epa.place.facility.FacilitySummary;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.def.CerrClassDef;
import us.oh.state.epa.stars2.def.GovtFacilityTypeDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.State;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;

public class CreateFacility extends AppBase {
	private static final long serialVersionUID = -1290893729607678920L;
	private Facility facility;
	private Address phyAddress = null;

	protected Address address;
	
	private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	private String corePlaceId;

	public CreateFacility() {
		cacheViewIDs.add("/facilities/createFacility.jsp");
		resetCreateFacility();
	}

	public final Facility getFacility() {
		return facility;
	}

	public final void setFacility(Facility facility) {
		this.facility = facility;
	}

	public final void setResetData(String resetData) {
		resetCreateFacility();
	}

	public final void resetCreateFacility() {
		
		facility = new Facility();
		facility.setGovtFacilityTypeCd(GovtFacilityTypeDef.PRIVATE);
		phyAddress = facility.getPhyAddr();

		phyAddress.setBeginDate(new Timestamp(System.currentTimeMillis()));
		phyAddress.setState(State.DEFAULT_STATE);
		phyAddress.setCountryCd("US");

		facility.setOperatingStatusCd(OperatingStatusDef.OP);

		if (isPortalApp()) {
			try {
				if (corePlaceId != null) {
					FacilitySummary facSummary = getFacilityService()
							.retrieveFacilitySummaryFromCoreDB(corePlaceId);
					if (facSummary != null) {
						us.oh.state.epa.portal.base.Address coreAddr = facSummary
								.getAddress();
						phyAddress = new Address(coreAddr);
						facility.setName(facSummary.getName());
					}
				}
			} catch (RemoteException re) {
				logger.debug(re);
			}
		} else {
			// Need to get core place ID
		}

		if (corePlaceId != null) {
			facility.setCorePlaceId(new Integer(corePlaceId));
		}

		clearButtonClicked();
	}

	@SuppressWarnings("unchecked")
	public final String submitCreateFacility() {
		
		 // Protect from multiple clicks.
		if (!firstButtonClick()) {
			return null;
		}
		
		ValidationMessage[] validationMessages, validationAddressMessages;
		Address phyAddr = facility.getPhyAddr();
		try {
			validationMessages = getFacilityService().validateCreateFacility(facility);
			if (displayValidationMessages("createFacility:", validationMessages)) {
				return FAIL;
			}

//			phyAddr.validateAddress();
//
//			phyAddr.getValidationMessages().remove("zipCode");
//			phyAddr.getValidationMessages().remove("cityName");
//			phyAddr.getValidationMessages().remove("addressLine1");
//			phyAddr.getValidationMessages().remove("districtCd");
			facility.setDoLaaCd(SystemPropertyDef.getSystemPropertyValue("districtCd", null));
			phyAddr.setDistrictCd(SystemPropertyDef.getSystemPropertyValue("districtCd", null)); 
//
//			phyAddr.validZipCode();
//			phyAddr.validateLocationInfo();
			phyAddr.validateFacilityAddress();
			validationAddressMessages = phyAddr.validate();

			if (displayValidationMessages("createFacility:Address",
					validationAddressMessages)) {
				return FAIL;
			}

			facility.setCerrClassCd(autoFillCERR(facility.getPermitClassCd()));

			facility = getFacilityService().createFacility(facility);
			if (facility == null) {
				DisplayUtil.displayError("Create Facility failed.");
				return FAIL;
			}

			FacilityProfileBase fp = (FacilityProfileBase) FacesUtil
					.getManagedBean("facilityProfile");

			if (isPortalApp()) {
				// Do we need this?
				FacesContext.getCurrentInstance().getExternalContext()
						.getSessionMap()
						.put("FACILITYID", facility.getFacilityId());
				MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
				myTasks.createNewFacilityTask(facility);
				((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_home"))
						.setRendered(true);
				fp.setStaging(true);
			} else {
				fp.setStaging(false);
			}

			fp.setAllContacts(null);
			fp.setFpId(facility.getFpId());

		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Create Facility failed");
			clearButtonClicked();
			return FAIL;
		} finally{
			clearButtonClicked();
		}
		
		resetCreateFacility();
		DisplayUtil.displayInfo("facility created successfully");

		((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_facProfile"))
				.setDisabled(false);
		

		return "facilityProfile";
	}
	
	private String autoFillCERR(String permitClassCd) {
		switch (permitClassCd) {
		case PermitClassDef.TV:
			return CerrClassDef.CAP;
		case PermitClassDef.NTV:
			return CerrClassDef.NON;
		case PermitClassDef.SMTV:
			return CerrClassDef.SYN;
		}
		return null;
	}

	public String getCorePlaceId() {
		return corePlaceId;
	}

	public void setCorePlaceId(String corePlaceId) {
		this.corePlaceId = corePlaceId;
	}

	@Override
	public void clearCache() {
		resetCreateFacility();
	}
}
