package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class ContactType extends BaseDB {
	private Integer contactId;
	private String contactTypeCd;
	private String facilityId;
	private Timestamp startDate;
	private Timestamp endDate;
	@SuppressWarnings("unused")
	private Long startDateLong; // XML encoding/decoding use for Timestamp
	@SuppressWarnings("unused")
	private Long endDateLong; // XML encoding/decoding use for Timestamp
	private static final SimpleDateFormat dateFormat = new java.text.SimpleDateFormat(
			"M/d/yyyy");
	private String facilityName;
	private String fpId;
	private String companyName;
	private String cmpId;

	// TODO replace separate facility attributes with facility object

	public ContactType() {
		requiredFields();
	}

	public ContactType(Integer contactId, String contactTypeCd,
			Timestamp startDate, Timestamp endDate) {
		this.contactId = contactId;
		this.contactTypeCd = contactTypeCd;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public ContactType(ContactType old) {
		super(old);

		if (old != null) {
			setContactId(old.getContactId());
			setContactTypeCd(old.getContactTypeCd());
			setStartDate(old.getStartDate());
			setEndDate(old.getEndDate());

			if (old.getFacilityId() != null) {
				setFacilityId(old.getFacilityId());
			}
		}
		requiredFields();
	}

	private void requiredFields() {
		requiredField(contactId, "contactId", "Contact");
		requiredField(contactTypeCd, "contactType", "Type");
		requiredField(startDate, "startDate", "Start Date");
	}

	public String getFpId() {
		return fpId;
	}

	public void setFpId(String fpId) {
		this.fpId = fpId;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public final Integer getContactId() {
		return contactId;
	}

	public final void setContactId(Integer contactId) {
		requiredField(contactId, "contactId", "Contact");
		this.contactId = contactId;
	}

	public final String getContactTypeCd() {
		return contactTypeCd;
	}

	public final void setContactTypeCd(String contactTypeCd) {
		requiredField(contactTypeCd, "contactType", "Type");
		this.contactTypeCd = contactTypeCd;
	}

	public final Timestamp getEndDate() {
		return endDate;
	}

	public final void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public final Timestamp getStartDate() {
		return startDate;
	}

	public final void setStartDate(Timestamp startDate) {
		requiredField(startDate, "startDate", "Start Date");
		this.startDate = startDate;
	}

	public final String getDateRange() {
		StringBuffer sb;
		if (startDate != null) {
			sb = new StringBuffer(dateFormat.format(startDate));
			sb.append(" - ");
		} else {
			sb = new StringBuffer("past - ");
		}
		if (endDate != null) {
			sb.append(dateFormat.format(endDate));
		} else {
			sb.append("current");
		}
		return sb.toString();
	}

	public final void populate(ResultSet rs) {
		try {
			setContactId(AbstractDAO.getInteger(rs, "contact_id"));
			setContactTypeCd(rs.getString("contact_type_cd"));
			setStartDate(rs.getTimestamp("ct_start_date"));
			setEndDate(rs.getTimestamp("ct_end_date"));
			setLastModified(AbstractDAO.getInteger(rs, "contactType_lm"));
			setFacilityId(rs.getString("facility_id"));
			try {
				setFacilityName(rs.getString("facility_nm"));
			} catch (SQLException sqle) {
				logger.debug(sqle.getMessage());
			}
			setFpId(rs.getString("fp_id"));

			try {
				setCompanyName(rs.getString("company_name"));
			} catch (SQLException sqle) {
				logger.debug(sqle.getMessage());
			}

			try {
				setCmpId(rs.getString("cmp_id"));
			} catch (SQLException sqle) {
				logger.debug(sqle.getMessage());
			}

		} catch (SQLException sqle) {
			logger.debug(sqle.getMessage());
		}
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result
				+ ((contactId == null) ? 0 : contactId.hashCode());
		result = PRIME * result
				+ ((contactTypeCd == null) ? 0 : contactTypeCd.hashCode());
		result = PRIME * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = PRIME * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
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
		final ContactType other = (ContactType) obj;
		if (contactId == null) {
			if (other.contactId != null)
				return false;
		} else if (!contactId.equals(other.contactId))
			return false;
		if (contactTypeCd == null) {
			if (other.contactTypeCd != null)
				return false;
		} else if (!contactTypeCd.equals(other.contactTypeCd))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

	public final Long getEndDateLong() {
		if (endDate == null) {
			return null;
		}
		return endDate.getTime();
	}

	public final void setEndDateLong(Long endDateLong) {
		if (endDateLong != null) {
			endDate = new Timestamp(endDateLong);
		}
	}

	public final Long getStartDateLong() {
		if (startDate == null) {
			return null;
		}
		return startDate.getTime();
	}

	public final void setStartDateLong(Long startDateLong) {
		if (startDateLong != null) {
			startDate = new Timestamp(startDateLong);
		}
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCmpId() {
		return cmpId;
	}

	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}
}
