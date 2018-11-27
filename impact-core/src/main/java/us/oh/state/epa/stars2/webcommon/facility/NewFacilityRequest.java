package us.oh.state.epa.stars2.webcommon.facility;

import java.rmi.RemoteException;
import java.sql.Timestamp;

import javax.faces.event.ActionEvent;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.facility.FacilityRequestSearch;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facilityRequest.FacilityRequest;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.State;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.DataStoreConcurrencyException;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.bo.ContactService;
import us.wy.state.deq.impact.portal.company.CompanyProfile;

@SuppressWarnings("serial")
public class NewFacilityRequest extends AppBase {

	public NewFacilityRequest(NewFacilityRequest newRequest) {
		this.editMode = false;
		this.setFacilityRequest(newRequest.getFacilityRequest());
	}

	private Address phyAddress = null;

	private FacilityRequest facilityRequest;

	protected Address address;

	private FacilityService facilityService;

	private ContactService contactService;

	private Integer requestId;

	private boolean editMode;

	private boolean requestDeleted;

	protected boolean error = false; // blank jsp page when an error occurs

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public ContactService getContactService() {
		return contactService;
	}

	public void setContactService(ContactService contactService) {
		this.contactService = contactService;
	}

	public FacilityRequest getFacilityRequest() {
		return facilityRequest;
	}

	public void setFacilityRequest(FacilityRequest facilityRequest) {
		this.facilityRequest = facilityRequest;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public NewFacilityRequest() {
		editMode = false;
		error = false;
		requestDeleted = false;
		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			cacheViewIDs.add("/facilities/newFacilityRequestDetail.jsp");
			cacheViewIDs.add("/facilities/facilityRequestSearch.jsp");
		} else if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			cacheViewIDs.add("/newFacilityRequest.jsp");
		}

		resetNewFacilityRequestInternal(); // here
	}

	public final String createNewFacilityRequest() {

		return "dialog:newFacilityRequest";
	}

	public final void setResetData(String resetData) {
		resetNewFacilityRequestInternal();
	}

	public final void resetNewFacilityRequest(ActionEvent event) {
		resetNewFacilityRequestInternal();
	}

	public final void resetNewFacilityRequestInternal() {
		editMode = false;
		requestDeleted = false;
		facilityRequest = new FacilityRequest();
		phyAddress = facilityRequest.getPhyAddr();
		phyAddress.setBeginDate(new Timestamp(System.currentTimeMillis()));
		phyAddress.setState(State.DEFAULT_STATE);
		phyAddress.setCountryCd("US");

		facilityRequest.setOperatingStatusCd(OperatingStatusDef.OP);

	}

	@SuppressWarnings("unchecked")
	public final String submitNewFacilityRequest(ActionEvent event) {

		if (!firstButtonClick()) { // protect from multiple clicks
			return "";
		}

		String id = null;
		boolean inputErrors = false;

		CompanyProfile cp = (CompanyProfile) FacesUtil
				.getManagedBean("companyProfile");

		ValidationMessage[] validationMessages, validationAddressMessages;
		Address phyAddr = facilityRequest.getPhyAddr();
		try {
			validationMessages = validateNewFacilityRequest(facilityRequest);
			if (displayValidationMessages("newFacilityRequest:",
					validationMessages)) {
				inputErrors = true;
			}

//			phyAddr.validateAddress();
//
//			phyAddr.getValidationMessages().remove("zipCode");
//			phyAddr.getValidationMessages().remove("cityName");
//			phyAddr.getValidationMessages().remove("addressLine1");
//
//			phyAddr.validZipCode();
//			phyAddr.validateLocationInfo();
			
			phyAddr.validateFacilityAddress();
			validationAddressMessages = phyAddr.validate();

			if (displayValidationMessages("newFacilityRequest:Address",
					validationAddressMessages)) {
				inputErrors = true;
			}

			facilityRequest.setCompanyName(cp.getCompany().getName());
			facilityRequest.setCompanyId(cp.getCompany().getCompanyId());

			MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
			if (myTasks.getLoginName() != null) {
				Contact externalContact = getContactService()
						.retrieveContactByExternalUsername(myTasks.getLoginName());
				if (externalContact != null) {
					facilityRequest.setCurrentUserId(externalContact
							.getContactId());
				} else {
					facilityRequest.setCurrentUserId(null);
				}
			}

			FacilityRequest request = new FacilityRequest(this.facilityRequest);
			if (!inputErrors) {
				id = getInfrastructureService().submitNewFacilityRequest(
						request);
			}

		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Create Facility Creation Request failed");
			return FAIL;
		} finally {
			if (!inputErrors) {
				resetNewFacilityRequestInternal();
				cp.goFacilitySelector();
				FacesUtil.returnFromDialogAndRefresh();
				DisplayUtil
						.displayInfo("Facility Creation Request "
								+ id
								+ " successfully created. The Air Quality Division will contact you regarding this request.");
			}
			clearButtonClicked();
		}

		return "";
	}

	public ValidationMessage[] validateNewFacilityRequest(
			FacilityRequest facilityRequest) {
		ValidationMessage[] validationMessages = null;

		if (facilityRequest == null) {
			logger.debug("invalid facilityRequest (null)");
			return null;
		}
		validationMessages = facilityRequest.validateForCreateFacilityRequest();
		return validationMessages;
	}

	@Override
	public void clearCache() {

	}

	public final void cancelNewFacilityRequest(ActionEvent event) {
		this.resetNewFacilityRequestInternal();
		FacesUtil.returnFromDialogAndRefresh();
	}

	// / Internal only

	public final String submitFacilityRequest() {

		error = false;
		editMode = false;
		requestDeleted = false;
		facilityRequest = null;
		getFacilityRequestDetail();

		if (facilityRequest == null) {
			error = true;
			logger.error("Unexpected NULL pointer return from accesing existing facility request from Database.");
			DisplayUtil
					.displayInfo("Facility Request is not found due to unexpected system problem; Please try again.");
		}

		return "newFacilityRequestDetail";

	}

	public String refreshSearchFacilityRequests() {
		this.setFacilityRequest(null);
		this.setEditMode(false);
		FacilityRequestSearch frs = (FacilityRequestSearch) FacesUtil
				.getManagedBean("facilityRequestSearch");

		frs.reset();
		frs.submitSearch();

		return "facilityRequestSearch";
	}

	public final FacilityRequest getFacilityRequestDetail() {

		if ((facilityRequest == null && requestId != null)
				|| (requestId != null && !requestId.equals(facilityRequest
						.getRequestId()))) {
			logger.debug("requestId = " + requestId);
			try {
				facilityRequest = getFacilityService().retrieveFacilityRequest(
						requestId);

				if (facilityRequest == null) {
					logger.error("No facility request found for requestId = "
							+ requestId);
				}
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayError("Accessing facility request failed");
			}
		} else {
			logger.debug("No need to re-query for facility request.");
		}
		if (facilityRequest == null) {
			logger.error("getFacilityRequestDetail is returning NULL!!!  requestId = "
					+ requestId);
		}

		return facilityRequest;
	}

	public final void edit() {
		setEditMode(true);
	}

	public final String saveFacilityRequest() {
		boolean operationOK = true;
		boolean remoteException = false;
		int currentUserId = InfrastructureDefs.getCurrentUserId();
		String reqId = facilityRequest.getReqId();

		try {
			if (!getFacilityService().modifyFacilityRequest(facilityRequest,
					currentUserId)) {
				DisplayUtil.displayInfo("The update failed for facility request " + reqId + ".");
				operationOK = false;
			}

		} catch (RemoteException re) {
			handleException(re, "Saving facility request " + reqId + " failed.");
			operationOK = false;
			remoteException = true;
		}

		if (operationOK) {
			submitFacilityRequest();
			DisplayUtil.displayInfo("Facility Request data saved successfully");
		} else {

			if (!remoteException) {
				DisplayUtil
						.displayError("Saving facility request " + reqId + " failed. Another user may have deleted the record you were trying to modify.");
			}
			return refreshSearchFacilityRequests();
		}
		this.editMode = false;
		return "newFacilityRequestDetail";
	}

	private void handleException(RemoteException re, String message) {
		if (re != null) {
			if (re instanceof DataStoreConcurrencyException) {
				DisplayUtil
						.displayError(message
								+ " - more than one user was attempting to edit and save facility request information at one time. Please re-enter your changes and submit again.");
				logger.warn(re.getMessage(), re);
			} else {
				DisplayUtil
						.displayError(message
								+ " -  system error has occurred. Please contact System Administrator.");
				logger.error(re.getMessage(), re);
			}
		}
	}

	public Integer getRequestId() {
		return requestId;
	}

	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}

	public final String refreshFacilityRequest() {

		if (requestId != null) {
			facilityRequest = null;
			setEditMode(false);
		}

		return "newFacilityRequestDetail";
	}

	public Address getPhyAddress() {
		return phyAddress;
	}

	public void setPhyAddress(Address phyAddress) {
		this.phyAddress = phyAddress;
	}

	public String from2ndLevelMenu() {
		return submitFacilityRequest();
	}

	public boolean isAdmin() {
		boolean ret = false;

		ret = isStars2Admin();

		return ret;
	}

	public final String requestDelete() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}

		clearButtonClicked();

		return "dialog:requestFacilityRequestDelete";
	}

	public final String deleteFacilityRequest() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		
		boolean ok = true;
		String reqId = facilityRequest.getReqId();

		if (ok) {
			try {
				getFacilityService().deleteFacilityRequest(facilityRequest);
				DisplayUtil
						.displayInfo("Facility Creation Request " + reqId + " successfully deleted.");
				handleBadDetail();
				error = true; // There is no error but this blanks out the
								// screen
				requestDeleted = true;
			} catch (DAOException e) {
				handleException(e);
				ok = false;
			}
		}
		if (!ok) {
			DisplayUtil.displayInfo("Failed to delete Facility Request " + reqId);
			error = true;
		}
	
		this.setFacilityRequest(null);
		this.setEditMode(false);
		
		clearButtonClicked();
		
		FacesUtil.returnFromDialogAndRefresh();
		
		return "Success";
	}

	public final String createFacility() {

		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}

		CreateFacility cf = (CreateFacility) FacesUtil
				.getManagedBean("createFacility");
		cf.getFacility().setCompanyName(facilityRequest.getCompanyIdString());
		cf.getFacility().setName(facilityRequest.getName());
		cf.getFacility().setDesc(facilityRequest.getDesc());
		cf.getFacility().setFacilityTypeCd(facilityRequest.getFacilityTypeCd());
		cf.getFacility().setOperatingStatusCd(
				facilityRequest.getOperatingStatusCd());
		cf.getFacility().setPhyAddr(facilityRequest.getPhyAddr());

		clearButtonClicked();

		return "facilities.createFacility";

	}

	public void handleBadDetail() {
		((SimpleMenuItem) FacesUtil
				.getManagedBean("menuItem_facilityCreationRequestDetail"))
				.setDisabled(true);

		NewFacilityRequest nfr = (NewFacilityRequest) FacesUtil
				.getManagedBean("newFacilityRequest");
		nfr.setFacilityRequest(null);

		FacilityRequestSearch frs = (FacilityRequestSearch) FacesUtil
				.getManagedBean("facilityRequestSearch");
		frs.reset();
		frs.submitSearch();
		frs.setPopupRedirectOutcome("facilities.newFacReq");
	}

	public final boolean isEditMode() {
		return editMode;
	}

	public final void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public final void closePopup() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final boolean isRequestDeleted() {
		return requestDeleted;
	}

	public final void setRequestDeleted(boolean requestDeleted) {
		this.requestDeleted = requestDeleted;
	}
}