package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class FieldAuditLog extends BaseDB {
	private Integer fieldAuditLogId;
	private String facilityId;
	private String cmpId;
	private String facilityName;
	private String companyName;
	private String categoryCd;
	private String attributeCd;
	private String uniqueId;
	private String originalValue;
	private String newValue;
	private Integer corrEmuId;
	private boolean isFacSearch = false;
	private Timestamp dateEntered;
	@SuppressWarnings("unused")
	private Long dateEnteredLong;
	private Integer userId;
	private Timestamp beginDt;
	private Timestamp endDt;

	public FieldAuditLog() {
		super();
	}

	public FieldAuditLog(String attributeCd, String uniqueId,
			String originalValue, String newValue) {
		super();
		setAttributeCd(attributeCd);
		setUniqueId(uniqueId);
		setOriginalValue(originalValue);
		setNewValue(newValue);
	}
	
	public FieldAuditLog(String attributeCd, String uniqueId,
			String originalValue, String newValue, Integer corrEmuId) {
		super();
		setAttributeCd(attributeCd);
		setUniqueId(uniqueId);
		setOriginalValue(originalValue);
		setNewValue(newValue);
		setCorrEmuId(corrEmuId);
	}	

	public FieldAuditLog(FieldAuditLog old) {
		super(old);

		if (old != null) {
			setFieldAuditLogId(old.getFieldAuditLogId());
			setFacilityId(old.getFacilityId());
			setFacilityName(old.getFacilityName());
			setCmpId(old.getCmpId());
			setCompanyName(old.getCompanyName());
			setCategoryCd(old.getCategoryCd());
			setAttributeCd(old.getAttributeCd());
			setUniqueId(old.getUniqueId());
			setOriginalValue(old.getOriginalValue());
			setNewValue(old.getNewValue());
			setDateEntered(old.getDateEntered());
			setUserId(old.getUserId());
			setCorrEmuId(old.getCorrEmuId());
		}
	}

	public final Integer getFieldAuditLogId() {
		return fieldAuditLogId;
	}

	public final void setFieldAuditLogId(Integer fieldAuditLogId) {
		this.fieldAuditLogId = fieldAuditLogId;
	}

	public final String getAttributeCd() {
		return attributeCd;
	}

	public final void setAttributeCd(String attributeCd) {
		this.attributeCd = attributeCd;
	}

	public final String getCategoryCd() {
		return categoryCd;
	}

	public final void setCategoryCd(String categoryCd) {
		this.categoryCd = categoryCd;
	}

	public final Timestamp getDateEntered() {
		return dateEntered;
	}

	private void setDateEntered(Timestamp dateEntered) {
		this.dateEntered = dateEntered;
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public final String getCmpId() {
		return cmpId;
	}

	public final void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}

	public boolean isFacSearch() {
		return isFacSearch;
	}

	public void setFacSearch(boolean isFacSearch) {
		this.isFacSearch = isFacSearch;
	}

	public final void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public final String getNewValue() {
		return newValue;
	}

	public final void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public final String getOriginalValue() {
		return originalValue;
	}

	public final void setOriginalValue(String originalValue) {
		this.originalValue = originalValue;
	}

	public final String getUniqueId() {
		return uniqueId;
	}

	public final void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public final Integer getUserId() {
		return userId;
	}

	public final void setUserId(Integer userId) {
		this.userId = userId;
	}

	public final void populate(ResultSet rs) throws SQLException {
		try {
			setFieldAuditLogId(AbstractDAO.getInteger(rs, "field_audit_log_id"));
			setFacilityId(rs.getString("facility_id"));
			setFacilityName(rs.getString("facility_nm"));
			setCmpId(rs.getString("cmp_id"));
			setCompanyName(rs.getString("company_nm"));
			setCategoryCd(rs.getString("category_cd"));
			setAttributeCd(rs.getString("attribute_cd"));
			setUniqueId(rs.getString("unique_id"));
			setOriginalValue(rs.getString("old_value"));
			setNewValue(rs.getString("new_value"));
			setDateEntered(rs.getTimestamp("date_entered"));
			setUserId(AbstractDAO.getInteger(rs, "user_id"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
			setCorrEmuId(AbstractDAO.getInteger(rs, "corrEmuId"));
		} catch (SQLException sqle) {
			logger.warn(sqle.getMessage());
		}
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result
				+ ((attributeCd == null) ? 0 : attributeCd.hashCode());
		result = PRIME * result
				+ ((categoryCd == null) ? 0 : categoryCd.hashCode());
		result = PRIME * result
				+ ((dateEntered == null) ? 0 : dateEntered.hashCode());
		result = PRIME * result
				+ ((facilityId == null) ? 0 : facilityId.hashCode());
		result = PRIME * result
				+ ((facilityName == null) ? 0 : facilityName.hashCode());
		result = PRIME * result + ((cmpId == null) ? 0 : cmpId.hashCode());
		result = PRIME * result
				+ ((companyName == null) ? 0 : companyName.hashCode());
		result = PRIME * result
				+ ((fieldAuditLogId == null) ? 0 : fieldAuditLogId.hashCode());
		result = PRIME * result
				+ ((newValue == null) ? 0 : newValue.hashCode());
		result = PRIME * result
				+ ((originalValue == null) ? 0 : originalValue.hashCode());
		result = PRIME * result
				+ ((uniqueId == null) ? 0 : uniqueId.hashCode());
		result = PRIME * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FieldAuditLog other = (FieldAuditLog) obj;
		if (attributeCd == null) {
			if (other.attributeCd != null)
				return false;
		} else if (!attributeCd.equals(other.attributeCd))
			return false;
		if (categoryCd == null) {
			if (other.categoryCd != null)
				return false;
		} else if (!categoryCd.equals(other.categoryCd))
			return false;
		if (dateEntered == null) {
			if (other.dateEntered != null)
				return false;
		} else if (!dateEntered.equals(other.dateEntered))
			return false;
		if (facilityId == null) {
			if (other.facilityId != null)
				return false;
		} else if (!facilityId.equals(other.facilityId))
			return false;
		if (cmpId == null) {
			if (other.cmpId != null)
				return false;
		} else if (!cmpId.equals(other.cmpId))
			return false;
		if (companyName == null) {
			if (other.companyName != null)
				return false;
		} else if (!companyName.equals(other.companyName))
			return false;
		if (facilityName == null) {
			if (other.facilityName != null)
				return false;
		} else if (!facilityName.equals(other.facilityName))
			return false;
		if (fieldAuditLogId == null) {
			if (other.fieldAuditLogId != null)
				return false;
		} else if (!fieldAuditLogId.equals(other.fieldAuditLogId))
			return false;
		if (newValue == null) {
			if (other.newValue != null)
				return false;
		} else if (!newValue.equals(other.newValue))
			return false;
		if (originalValue == null) {
			if (other.originalValue != null)
				return false;
		} else if (!originalValue.equals(other.originalValue))
			return false;
		if (uniqueId == null) {
			if (other.uniqueId != null)
				return false;
		} else if (!uniqueId.equals(other.uniqueId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	public final Long getDateEnteredLong() {
		return dateEntered.getTime();
	}

	public final void setDateEnteredLong(Long dateEnteredLong) {
		this.dateEntered = new Timestamp(dateEnteredLong);
	}

	public Integer getCorrEmuId() {
		return corrEmuId;
	}

	public void setCorrEmuId(Integer corrEmuId) {
		this.corrEmuId = corrEmuId;
	}

	public Timestamp getBeginDt() {
		return beginDt;
	}

	public void setBeginDt(Timestamp beginDt) {
		if (beginDt != null){
			Calendar cal = new GregorianCalendar();
			cal.setTimeInMillis(beginDt.getTime());
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			beginDt = new Timestamp(cal.getTimeInMillis());		
		}
		this.beginDt = beginDt;
	}
	
	public Timestamp getEndDt() {
		return endDt;
	}

	public void setEndDt(Timestamp endDt) {
		if (endDt != null){
			Calendar cal = new GregorianCalendar();
			cal.setTimeInMillis(endDt.getTime());
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			endDt = new Timestamp(cal.getTimeInMillis() + TimeUnit.DAYS.toMillis(1));
		}
		this.endDt = endDt;
	}
}
