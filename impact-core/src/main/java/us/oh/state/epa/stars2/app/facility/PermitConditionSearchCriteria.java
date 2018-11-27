package us.oh.state.epa.stars2.app.facility;

import java.sql.Timestamp;

public class PermitConditionSearchCriteria {
	
	private String facilityId;
	private String permitNumber;
	private String legacyPermitNumber;
	private String permitConditionNumber;
	private String conditionTextPlain;
	private String euId;
	private String permitLevelStatusCd;
	private String permitConditionStatusCd;
	private String permitConditionCategoryCd;
	private String permitTypeCd;
	private String dateBy;
	private boolean showOnlyIssuedFinal = true;
	private Timestamp beginDt;
	private Timestamp endDt;
	
	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getPermitNumber() {
		return permitNumber;
	}
	
	public void setPermitNumber(String permitNumber) {
		this.permitNumber = permitNumber;
	}
	
	public String getLegacyPermitNumber() {
		return legacyPermitNumber;
	}
	
	public void setLegacyPermitNumber(String legacyPermitNumber) {
		this.legacyPermitNumber = legacyPermitNumber;
	}
	
	public String getPermitConditionNumber() {
		return permitConditionNumber;
	}
	
	public void setPermitConditionNumber(String permitConditionNumber) {
		this.permitConditionNumber = permitConditionNumber;
	}
	
	public String getConditionTextPlain() {
		return conditionTextPlain;
	}
	
	public void setConditionTextPlain(String conditionTextPlain) {
		this.conditionTextPlain = conditionTextPlain;
	}
	
	public String getEuId() {
		return euId;
	}
	
	public void setEuId(String euId) {
		this.euId = euId;
	}
	
	public String getPermitLevelStatusCd() {
		return permitLevelStatusCd;
	}
	
	public void setPermitLevelStatusCd(String permitLevelStatusCd) {
		this.permitLevelStatusCd = permitLevelStatusCd;
	}
	
	public String getPermitConditionStatusCd() {
		return permitConditionStatusCd;
	}
	
	public void setPermitConditionStatusCd(String permitConditionStatusCd) {
		this.permitConditionStatusCd = permitConditionStatusCd;
	}
	
	public String getPermitConditionCategoryCd() {
		return permitConditionCategoryCd;
	}
	
	public void setPermitConditionCategoryCd(String permitConditionCategoryCd) {
		this.permitConditionCategoryCd = permitConditionCategoryCd;
	}
	
	public String getPermitTypeCd() {
		return permitTypeCd;
	}
	
	public void setPermitTypeCd(String permitTypeCd) {
		this.permitTypeCd = permitTypeCd;
	}
	
	public String getDateBy() {
		return dateBy;
	}
	
	public void setDateBy(String dateBy) {
		this.dateBy = dateBy;
	}
	
	public Timestamp getBeginDt() {
		return beginDt;
	}
	
	public void setBeginDt(Timestamp beginDt) {
		this.beginDt = beginDt;
	}
	
	public Timestamp getEndDt() {
		return endDt;
	}
	
	public void setEndDt(Timestamp endDt) {
		this.endDt = endDt;
	}

	public boolean isShowOnlyIssuedFinal() {
		return showOnlyIssuedFinal;
	}

	public void setShowOnlyIssuedFinal(boolean showOnlyIssuedFinal) {
		this.showOnlyIssuedFinal = showOnlyIssuedFinal;
	}
	

}
