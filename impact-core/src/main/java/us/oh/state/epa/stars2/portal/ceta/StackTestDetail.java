package us.oh.state.epa.stars2.portal.ceta;

import java.rmi.RemoteException;

import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.ceta.TestedEmissionsUnit;
import us.oh.state.epa.stars2.portal.bean.SubmitTask;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
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
	
	private TestedEmissionsUnit selectedTestedEu; // the one located by selectedEpaEmuId
	String selectedScc; // To support EUs with no process
	
	private Task task;
    private MyTasks myTasks;

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
	
	// The following methods exist only in the portal version
	
	public final String submitStackTestRO() {
		String rtn = null;
		if (!firstButtonClick()) { // protect from multiple clicks
			return rtn;
		}
		try {
			setFromTODOList(false);
			rtn = submitStackTestInternal(true);
			setViewOnly(true);
		} finally {
			clearButtonClicked();
		}
		return rtn;
	}
	
		@Override
		public String reload() {		
			return reloadStackTest();
		}
		
		/**
		 * Reload data for current application and exit edit mode.
		 * 
		 * @return
		 */
		
		public String reloadStackTest() {
			//if (!readOnly) {
				this.id = stackTest.getId();
				setFromTODOList(false);
				submitStackTestInternal(schemaFlag());
			//} else {
				// load read-only version if edit is not allowed
			//	loadApplication(stackTest.getApplicationNumber());
			//}
			return null;
		}


		// method called by "Stack Test Detail" tab to refresh data
	    public final String refresh() {
	        reload();
	        return "stacktests";
	    }

	    public final Task getTask() {
	        return task;
	    }

	    public final void setTask(Task task) {
	        this.task = task;
	    }
	    
	    public final MyTasks getMyTasks() {
	    	if (myTasks == null) {
				myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
			}
	        return myTasks;
	    }

	    public final void setMyTasks(MyTasks myTasks) {
	        this.myTasks = myTasks;
	    }  
	    
	    public final String applySubmitFromPortal() {
	        String ret = null;
	        try {
	            // load document data into application
	            // this needs to be done so Document data can be sent
	            // to the internal application
	            // // TO DO: Stack Test portal attachments is this needed for Stack Test???? getStackTestService().loadAllDocuments(stackTest, false);

	            // don't want duplicate attachments
	            task.getAttachments().clear();
	            task.setStackTest(stackTest);

	            // need to retrieve current facility information because
	            // changes to the facility made while correcting validation errors
	            // may not be reflected in the copy of the facility stored in the
	            // application
	            getStackTestService().synchStackTestWithCurrentFacilityProfile(stackTest);
	            
	            // need to retrieve facility by fp_id to get all data needed
	            task.setFacility(getFacilityService().retrieveFacilityProfile(stackTest.getAssociatedFacility().getFpId(), true));
	            
	            MyTasks myTasks = (MyTasks)FacesUtil.getManagedBean("myTasks");
	            SubmitTask submitTask = (SubmitTask) FacesUtil.getManagedBean("submitTask");
	            submitTask.setDapcAttestationRequired(true);
	            submitTask.setRoSubmissionRequired(true);
	            submitTask.setNonROSubmission(!myTasks.isHasSubmit());
	            submitTask.setTitle("Submit Stack Test");
	            
	            printEmissionsTest();
	            submitTask.setDocuments(testDocuments);
	            ret = submitTask.confirm();

	        } catch (RemoteException re) {
	            handleException(re);
	        }
	        return ret;
	    }
   
	    /**
		 * Set the Id of the stack test to be displayed and load it on the screen.
		 * Within the portal application, this method will always retrieve
		 * stack test data from the STAGING schema. 
		 * 
		 * TBD If there is a need to view data
		 * from the READONLY schema, setApplicationNumber should be used instead.
		 * 
		 * @param applicationID
		 */

		public void setStackTestID(int stackTestID) {
			reset();
			stackTest = null; // clear old stack test
			
			id = stackTestID;
			setFromTODOList(false);
			submitStackTestInternal(schemaFlag());
			setViewOnly(false);
		}
		
		/*
		 * Clear out values that may have been carried over from a previous
		 * stack test displayed in UI
		 */
	
		protected void reset() {
			// TBD
		}
		
		public String validationDlgAction() {
			String ret = super.validationDlgAction();
			if (ret != null) {
				if (ret.equals(STACK_TEST)) {
					if (isPortalApp() && getMyTasks() != null) {
						Task tmpTask = getMyTasks().findStackTestTask(this.stackTest.getId());
						if (tmpTask != null) {
							getMyTasks().setTaskId(tmpTask.getTaskId());
							getMyTasks().submitTask(true);
						}
					}
				}
			}
			return ret;
		}
}