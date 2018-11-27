package us.oh.state.epa.stars2.database.dbObjects.report;

import java.io.Serializable;
import java.sql.Timestamp;

@SuppressWarnings("serial")
public class PEROverdueDetails implements Serializable {
	private Timestamp perDueDate;
	private String facilityName;
	private String facilityId;
	private String epaEmuId;
	private String euDescription;
	
	public final Timestamp getPerDueDate() {
		return perDueDate;
	}
	public final void setPerDueDate(Timestamp perDueDate) {
		this.perDueDate = perDueDate;
	}
	public final String getFacilityName() {
		return facilityName;
	}
	public final void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}
	public final String getFacilityId() {
		return facilityId;
	}
	public final void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}
	public final String getEpaEmuId() {
		return epaEmuId;
	}
	public final void setEpaEmuId(String epaEmuId) {
		this.epaEmuId = epaEmuId;
	}
	public final String getEuDescription() {
		return euDescription;
	}
	public final void setEuDescription(String euDescription) {
		this.euDescription = euDescription;
	}
}
