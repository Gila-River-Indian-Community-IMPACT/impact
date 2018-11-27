package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class EnforcementActionEvent extends BaseDB {

	private Integer eventId;
	private Integer enforcementActionId;
	private String eventCd;
	private String eventDesc;
	private Timestamp eventDate;
	private Integer addedBy;

	public EnforcementActionEvent() {

		this.requiredField(eventId, "event_id");
		this.requiredField(eventCd, "event_cd");
		this.requiredField(eventDate, "event_dt");
		this.requiredField(addedBy, "added_by");

		setDirty(false);

	}

	public EnforcementActionEvent(EnforcementActionEvent old) {

		super(old);

		setEventId(old.getEventId());
		setEnforcementActionId(old.getEnforcementActionId());
		setEventCd(old.getEventCd());
		setEventDesc(old.getEventDesc());
		setEventDate(old.getEventDate());
		setAddedBy(old.getAddedBy());

		setLastModified(old.getLastModified());
		setDirty(old.isDirty());
	}

	public void populate(ResultSet rs) {

		try {
			setEventId(AbstractDAO.getInteger(rs, "event_id"));
			setEnforcementActionId(AbstractDAO.getInteger(rs, "enforcement_action_id"));
			setEventCd(rs.getString("enforcement_action_event_cd"));
			setEventDesc(rs.getString("enforcement_action_event_dsc"));
			setEventDate(rs.getTimestamp("event_dt"));
			setAddedBy(AbstractDAO.getInteger(rs, "added_by"));
			

			setLastModified(AbstractDAO.getInteger(rs, "eae_lm"));
			setDirty(false);
		} catch (SQLException sqle) {
			logger.error("Required field error");
		}

	}

	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
		this.requiredField(eventId, "event_id");
		setDirty(true);
	}

	public Integer getEnforcementActionId() {
		return enforcementActionId;
	}

	public void setEnforcementActionId(Integer enforcementActionId) {
		this.enforcementActionId = enforcementActionId;
	}

	public String getEventCd() {
		return eventCd;
	}

	public void setEventCd(String eventCd) {
		this.eventCd = eventCd;
		this.requiredField(eventCd, "event_cd");
		setDirty(true);
	}
	
	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	public Timestamp getEventDate() {
		return eventDate;
	}

	public void setEventDate(Timestamp eventDate) {
		this.eventDate = eventDate;
		this.requiredField(eventDate, "event_dt");
		setDirty(true);
	}

	public Integer getAddedBy() {
		return addedBy;
	}

	public void setAddedBy(Integer addedBy) {
		this.addedBy = addedBy;
		this.requiredField(addedBy, "added_by");
		setDirty(true);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((addedBy == null) ? 0 : addedBy.hashCode());
		result = prime * result + ((eventCd == null) ? 0 : eventCd.hashCode());
		result = prime * result
				+ ((eventDate == null) ? 0 : eventDate.hashCode());
		result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
		result = prime
				* result
				+ ((enforcementActionId == null) ? 0 : enforcementActionId
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof EnforcementActionEvent)) {
			return false;
		}
		EnforcementActionEvent other = (EnforcementActionEvent) obj;
		if (addedBy == null) {
			if (other.addedBy != null) {
				return false;
			}
		} else if (!addedBy.equals(other.addedBy)) {
			return false;
		}
		if (eventCd == null) {
			if (other.eventCd != null) {
				return false;
			}
		} else if (!eventCd.equals(other.eventCd)) {
			return false;
		}
		if (eventDate == null) {
			if (other.eventDate != null) {
				return false;
			}
		} else if (!eventDate.equals(other.eventDate)) {
			return false;
		}
		if (eventId == null) {
			if (other.eventId != null) {
				return false;
			}
		} else if (!eventId.equals(other.eventId)) {
			return false;
		}
		if (enforcementActionId == null) {
			if (other.enforcementActionId != null) {
				return false;
			}
		} else if (!enforcementActionId.equals(other.enforcementActionId)) {
			return false;
		}
		return true;
	}
}
