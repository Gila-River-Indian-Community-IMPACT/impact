package us.oh.state.epa.stars2.app.tools;

import java.rmi.RemoteException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dbObjects.facility.EventLog;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

@SuppressWarnings("serial")
public class FacilitiesNewEvent extends BulkOperation {

    private FacilityList[] facilities;
    private EventLog eventLog;

    public FacilitiesNewEvent() {
        super();
        setButtonName("Facilities New Event");
        setNavigation("dialog:facilitiesNewEvent");
    }

    /**
     * Each bulk operation is responsible for providing a search operation based
     * on the paramters gathered by the BulkOperationsCatalog bean. The search
     * is run when the "Select" button on the Bulk Operations screen is clicked.
     * The BulkOperationsCatalog will then display the results of the search.
     * Below the results is a "Bulk Operation" button.
     */
    public final void searchFacilities(BulkOperationsCatalog lcatalog)
        throws RemoteException {

        this.catalog = lcatalog;
        eventLog = new EventLog();
        eventLog.setDate(new Timestamp(System.currentTimeMillis()));
        eventLog.setEventTypeDefCd("dpcn");

        facilities = getFacilityService().searchFacilities(null, catalog.getFacilityId(),null, null,
                                            catalog.getCounty(),
                                            catalog.getOperatingStatus(),
                                            catalog.getDoLaa(),
                                            catalog.getNaicsCd(),
                                            catalog.getPermittingClassCd(),
                                            catalog.getTvPermitStatus(), null,
                                            catalog.getCity(),
                                            catalog.getZipCode(), catalog.isPortable()? "Y" : "N", 
                                            null, true, null);

        setMaximum(facilities.length);
        setValue(facilities.length);

        setSearchStarted(true);
        catalog.setFacilities(facilities);
        setSearchCompleted(true);

    }

    /**
     * When the "Bulk Operation" button is clicked, the BulkOperationsCatalog
     * class will call this method, ensure that the BulkOperation class is
     * placed into the jsf context, and return the value of the getNavigation()
     * method to the jsf controller. Any further requests from the page that is
     * navigated to are handled by this bean in the normal way. The default
     * version does no preliminary work.
     */
    public final void performPreliminaryWork(BulkOperationsCatalog lcatalog) {

        return;
    }

    public final String performPostWork(BulkOperationsCatalog lcatalog) {
    	boolean ok = true;
        FacilityList[] selectedFacilities = lcatalog.getSelectedFacilities();
       
        for (FacilityList facilityL : selectedFacilities) {
            try {
                eventLog.setUserId(InfrastructureDefs.getCurrentUserId());
                eventLog.setFpId(facilityL.getFpId());
                eventLog.setFacilityId(facilityL.getFacilityId());

                getFacilityService().createEventLog(eventLog);
            }
            catch (RemoteException re) {
            	ok = false;
                DisplayUtil.displayError("Create new event failed for facility " + facilityL.getFacilityId());
                logger.error(re.getMessage(), re);
            }

        }
        if (ok) {
        	DisplayUtil.displayInfo("Operation completed successfully.");
        }

        return SUCCESS;
    }

    /**
     * @return the eventLog
     */
    public final EventLog getEventLog() {
        return eventLog;
    }

    /**
     * @param eventLog the eventLog to set
     */
    public final void setEventLog(EventLog eventLog) {
        this.eventLog = eventLog;
    }    

}
