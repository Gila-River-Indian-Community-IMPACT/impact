package us.oh.state.epa.stars2.portal.workflow;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.portal.facility.FacilityProfile;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

public class EventLog extends AppBase {
    private Integer fpId;
    private String facilityName;
    private String facilityId;
    private Integer userId;
    private Timestamp date;
    private Timestamp dateTo;
    private String eventTypeDefCd;
    private String note;
    private String command;
    private LinkedHashMap<String, String> eventType;
    private us.oh.state.epa.stars2.database.dbObjects.facility.EventLog[] els;
    private boolean hasSearchResults;
	private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    public final String submit() {
        try {
            us.oh.state.epa.stars2.database.dbObjects.facility.EventLog el = new us.oh.state.epa.stars2.database.dbObjects.facility.EventLog();
            el.setFpId(fpId);
            el.setUserId(userId);
            el.setDate(date);
            el.setDateTo(dateTo);
            el.setEventTypeDefCd(eventTypeDefCd);
            el.setNote(note);
            el.setFacilityId(facilityId);
            el.setFacilityName(facilityName);

            els = getFacilityService().retrieveEventLogs(el);

            hasSearchResults = true;
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil.displayError("System error. Please contact system administrator");
        }
        return SUCCESS;
    }

    public final String createEvent() {
        try {
            us.oh.state.epa.stars2.database.dbObjects.facility.EventLog el = new us.oh.state.epa.stars2.database.dbObjects.facility.EventLog();
            el.setFpId(fpId);
            el.setUserId(userId);
            el.setDate(date);
            el.setEventTypeDefCd(eventTypeDefCd);
            el.setNote(note);
            el.setFacilityId(facilityId);

            getFacilityService().createEventLog(el);
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil.displayError("System error. Please contact system administrator");
        }
        return SUCCESS;
    }

    public final String reset() {
        els = null;
        fpId = null;
        date = null;
        dateTo = null;
        eventTypeDefCd = null;
        note = null;
        facilityId = null;
        facilityName = null;
        hasSearchResults = false;
        FacilityProfile facprofile = (FacilityProfile) FacesUtil
                .getManagedBean("facilityProfile");
        fpId = facprofile.getFpId();
        userId = InfrastructureDefs.getCurrentUserId();
        return SUCCESS;
    }

    public final us.oh.state.epa.stars2.database.dbObjects.facility.EventLog[] getEls() {
        return els;
    }

    public final void setEls(
            us.oh.state.epa.stars2.database.dbObjects.facility.EventLog[] els) {
        this.els = els;
    }

    public final String getCommand() {
        return command;
    }

    public final void setCommand(String command) {
        this.command = command;
        reset();
    }

    public final Timestamp getDate() {
        return date;
    }

    public final void setDate(Timestamp date) {
        this.date = date;
    }

    public final Timestamp getDateTo() {
        return dateTo;
    }

    public final void setDateTo(Timestamp dateTo) {
        this.dateTo = dateTo;
    }

    public final LinkedHashMap<String, String> getEventType() {
        if (eventType == null) {
            try {
                eventType = getFacilityService().retrieveEventTypeCdAndDesc();
            } catch (RemoteException re) {
                logger.error(re.getMessage(), re);
                DisplayUtil.displayError("System error. Please contact system administrator");
            }
        }
        return eventType;
    }

    public final void setEventType(LinkedHashMap<String, String> eventType) {
        this.eventType = eventType;
    }

    public final String getEventTypeDefCd() {
        return eventTypeDefCd;
    }

    public final void setEventTypeDefCd(String eventTypeDefCd) {
        this.eventTypeDefCd = eventTypeDefCd;
    }

    public final Integer getFpId() {
        return fpId;
    }

    public final void setFpId(Integer fpId) {
        this.fpId = fpId;
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

    public final String getNote() {
        return note;
    }

    public final void setNote(String note) {
        this.note = note;
    }

    public final Integer getUserId() {
        return userId;
    }

    public final void setUserId(Integer userId) {
        this.userId = userId;
    }

    public final boolean isHasSearchResults() {
        return hasSearchResults;
    }

    public final void setHasSearchResults(boolean hasSearchResults) {
        this.hasSearchResults = hasSearchResults;
    }
}
