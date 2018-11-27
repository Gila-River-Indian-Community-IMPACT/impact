package us.oh.state.epa.stars2.portal.ceta;

import javax.faces.event.ActionEvent;

import oracle.adf.view.faces.context.AdfFacesContext;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ceta.StackTestSearchCommon;

@SuppressWarnings("serial")
public class StackTestSearch extends StackTestSearchCommon {
	protected MyTasks myTasks;
    
    protected String newStackTestFacilityID;

    public StackTestSearch() {
        super();
    }

	protected MyTasks getMyTasks() {
    	if (myTasks == null) {
    		myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
    	}
    	return myTasks;
    }
    
    public final void createNewStackTest(ActionEvent event) {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            createNewStackTestInternal(event);
        } finally {
            clearButtonClicked();
        }
    }
    
	protected final void createNewStackTestInternal(ActionEvent event) {
		if (newStackTestFacilityID != null
				&& newStackTestFacilityID.length() > 0) {

			MyTasks myTasks = getMyTasks();
			logger.debug("in createNewStackTestInternal");

			// create new stack test
			Facility facility;

			try {
				facility = ServiceFactory.getInstance().getFacilityService()
						.retrieveFacility(newStackTestFacilityID);
				newStackTest = new StackTest();
				String redirectTo = myTasks.createStackTest(newStackTest);
				myTasks.setPageRedirect(redirectTo);
				FacesUtil.returnFromDialogAndRefresh();
				resetNewStackTest();

			} catch (Exception ex) {
				// UI error is displayed by myTasks.createApplication()
				// DisplayUtil.displayError("Failed creating request");
				logger.error(ex.getMessage(), ex);
			}

		} else {
			DisplayUtil.displayWarning("Please enter a facility ID");
		}
	}
    
    public final void cancelNewStackTest(ActionEvent actionEvent) {
        resetNewStackTest();
        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
        //fromNewAppPopup = false;
    }
    
    public final String getNewStackTestFacilityID() {
        return newStackTestFacilityID;
    }

    public final void setNewStackTestFacilityID(String newStackTestFacilityID) {
        this.newStackTestFacilityID = newStackTestFacilityID;
        setFacilityId(newStackTestFacilityID);
    }
}