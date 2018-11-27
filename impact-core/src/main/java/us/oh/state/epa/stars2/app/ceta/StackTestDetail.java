package us.oh.state.epa.stars2.app.ceta;

import java.rmi.RemoteException;
import java.sql.Timestamp;

import javax.faces.event.ActionEvent;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.def.EmissionsTestStateDef;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.ceta.StackTestDetailCommon;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;

/*
 * Scheme to handle multiple EUs and one Stack Test Method.
 * Not yet decided about SCC--whether the same for all EUs or different.
 * 
 * The database will contain only StackTestedPollutant rows which will repeat the EU and SCC ID 
 * for each pollutant row.
 * Internally in this bean, we maintain that table as well as the 
 * table of EUs (StackTest.testedEmissionsUnits) and table of
 * tested pollutants StackTest.testedPollutants.
 * 
 * Upon reading the stack test, both testedEmissionsUnits and testedPollutants (including
 * test results) are
 * created by processing the StackTestedPollutant rows.
 * 
 * Within the web interface, the user changes which EUs/SCC and which pollutants from the
 * displayed tables testedEmissionsUnits & testedPollutants and these changes make the
 * appropriate changes to the displayed pollutant table where the columns for EU/SCC and pollutant
 * are not updatable, deletable or addable.  The only things that can be done is enter/change the
 * permitted & tested rates and the AFS information.
 */

@SuppressWarnings("serial")
public class StackTestDetail extends StackTestDetailCommon {

	public StackTestDetail() {
		super();
	}
	
	// The following methods exist in both the internal app and portal version.
	
	public void returnToDetail() {
		StackTests stackTests = (StackTests) FacesUtil
				.getManagedBean("stackTests");
		stackTests.setPopupRedirectOutcome("ceta.stackTestDetail");
		FacesUtil.returnFromDialogAndRefresh();
	}
	
	public String initStackTestsForCancelEdit() {
		StackTests stackTests = (StackTests) FacesUtil
				.getManagedBean("stackTests");
		return stackTests.initStackTestsForCancelEdit();
	}
	
	public void handleBadDetail() {
		((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_stackTestDetail"))
				.setDisabled(true);
		StackTests sts = (StackTests) FacesUtil.getManagedBean("stackTests");
		StackTestSearch stackTestSearch = (StackTestSearch) FacesUtil
				.getManagedBean("stackTestSearch");
		stackTestSearch.search();
		sts.setPopupRedirectOutcome("stackTest.search");
	}

	public void handleBadDetailAndRedirect() {
		handleBadDetail();
		StackTests sts = (StackTests) FacesUtil.getManagedBean("stackTests");
		sts.getPopupRedirect();
	}
	
	// The following methods exist only in the internal app version
	
	public final void submitStackTestFromPopup() {
		// Invoked from list of stack tests associated with a visit date in a 
		// popup page.
		String rtn = null;
		if (!firstButtonClick()) { // protect from multiple clicks
			FacesUtil.returnFromDialogAndRefresh();
		}
		try {
			setFromTODOList(false);
			rtn = submitStackTestInternal(schemaFlag());
		} finally {
			clearButtonClicked();
		}

		StackTests stackTests = (StackTests) FacesUtil
				.getManagedBean("stackTests");
		stackTests.setPopupRedirectOutcome(rtn);
		FacesUtil.returnFromDialogAndRefresh();
	}
	
	public final void finishSubmitStateAndCloneRem() {
		if (reminderDate == null) {
			DisplayUtil
					.displayError("A Reminder Date must be supplied if creating a reminder.");
			return;
		}
		stackTest.setEmissionTestState(EmissionsTestStateDef.SUBMITTED);
		stackTest.setTestBeingSubmitted(true);
		stackTest.setSubmittedDate(new Timestamp(System.currentTimeMillis()));
		internalModifyStackTest(true, false);
		DisplayUtil.displayInfo("Submit sucessful");
		// clear fields that do not belong in new reminder
		stackTest.clearForNewReminder();
		stackTest.setReminderDate(reminderDate);
		cloneStackTestInternal("Reminder", EmissionsTestStateDef.REMINDER);
		StackTests stackTests = (StackTests) FacesUtil
				.getManagedBean("stackTests");
		stackTests.setPopupRedirectOutcome("ceta.stackTestDetail");
		FacesUtil.returnFromDialogAndRefresh();
	}
	
	public String goToSummaryPage() {
		FacilityProfile fp = (FacilityProfile) FacesUtil
				.getManagedBean("facilityProfile");
		fp.setFacilityId(facility.getFacilityId());
		fp.submitProfileById();
		StackTests stackTests = (StackTests) FacesUtil
				.getManagedBean("stackTests");
		return stackTests.initStackTests();
	}
	
	public final void updateFacilityProfile(ActionEvent actionEvent) {
		if (stackTest != null) {
			String infoMsg = "The Stack Test is now associated with "
					+ "the current version of the Facility Inventory.";
			try {
				if (!getStackTestService()
						.synchStackTestWithCurrentFacilityProfile(stackTest)) {
					infoMsg = "The Stack Test was already associated with "
							+ "the current version of the Facility Inventory. No change was made.";
				} else {
					this.validatedSuccessfully = false;
				}

				internalModifyStackTest(true, false);
				String ret = submitStackTest();
				StackTests stackTests = (StackTests) FacesUtil
						.getManagedBean("stackTests");
				stackTests.setPopupRedirectOutcome(ret);
				FacesUtil.returnFromDialogAndRefresh();

				DisplayUtil.displayInfo(infoMsg);
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayInfo("Failed to update Facility Inventory.");
				error = true;
				setEditable(false);
				memoEditable = false;
			}
		}
	}
	
	public boolean isSubmittedFromPortal() {
		if (this.stackTest.getCreatedById().equals(CommonConst.GATEWAY_USER_ID)) {
			return true;
		} else {
			return false;
		}
	}
	
	public final void assignStackTest() {
		Integer userId = InfrastructureDefs.getCurrentUserId();
		
		try {
			getStackTestService().assignStackTest(stackTest, userId);
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayInfo("Failed to Assign Stack Test.");
			error = true;
			setEditable(false);
			memoEditable = false;
		}

		DisplayUtil.displayInfo("Stack Test assigned successfully.");
	}
	
	public final String viewDetail() {
		this.setEditable(false);
		this.setEditMode(false);
		String ret = null;
		stackTest = null;

		logger.debug("viewDetail getReportId() = " + getId());
		loadStackTest(getId(), schemaFlag());

		if (stackTest != null) {
			logger.debug("viewDetail this.getComplianceReport().getStckId() = "
					+ this.getStackTest().getStckId());
			if (this.getStackTest().getStckId() != null) {
				ret = STACK_TEST;
			} else {
				ret = null;
				DisplayUtil
						.displayError("Stack Test is not found with that number.");
			}
		} else {
			DisplayUtil
					.displayError("Stack Test is not found with that number.");
		}
		return ret;
	}
	
	@Override
	public void loadStackTest(int stackTestId, boolean schemaFlag) {
		super.loadStackTest(stackTestId, schemaFlag);
	}
	
}
