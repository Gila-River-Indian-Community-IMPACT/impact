package us.oh.state.epa.stars2.database.dbObjects.ceta;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

@SuppressWarnings("serial")
public class Complaint extends BaseDB implements Comparable<Complaint> {
	private Integer complaintId;
	private String doLaaCd;
	private String year;
	private String month;
	private Integer highPriority;
	private Integer nonHighPriority;
	private Integer other;
	private Integer openBurning;
	private Integer antiTamperingInspections;
	private Integer asbestos;
	private Integer asbestosNonNotifier;
	
	public void populate(ResultSet rs) throws SQLException {
		try {
			setComplaintId(AbstractDAO.getInteger(rs, "complaint_id"));
			setDoLaaCd(rs.getString("do_laa_cd"));
			setYear(rs.getString("year"));
			setMonth(rs.getString("month"));
			setHighPriority(AbstractDAO.getInteger(rs, "highPriority"));
			setNonHighPriority(AbstractDAO.getInteger(rs, "nonHighPriority"));
			setOther(AbstractDAO.getInteger(rs, "other"));
			setOpenBurning(AbstractDAO.getInteger(rs, "openBurning"));
			setAntiTamperingInspections(AbstractDAO.getInteger(rs, "antiTamperingInspections"));
			setAsbestos(AbstractDAO.getInteger(rs, "asbestos"));
			setAsbestosNonNotifier(AbstractDAO.getInteger(rs, "asbestosNonNotifier"));
			setLastModified(AbstractDAO.getInteger(rs, "complaint_lm"));
		 } catch (SQLException sqle) {
	            logger.error(sqle.getMessage());
	        }

	}

	public final Integer getComplaintId() {
		return complaintId;
	}

	public final void setComplaintId(Integer complaintId) {
		this.complaintId = complaintId;
	}

	public final String getDoLaaCd() {
		return doLaaCd;
	}

	public final void setDoLaaCd(String doLaaCd) {
		this.doLaaCd = doLaaCd;
	}

	public final String getYear() {
		return year;
	}

	public final void setYear(String year) {
		this.year = year;
	}

	public final String getMonth() {
		return month;
	}

	public final void setMonth(String month) {
		this.month = month;
	}

	public final Integer getHighPriority() {
		return highPriority;
	}

	public final void setHighPriority(Integer highPriority) {
		this.highPriority = highPriority;
	}

	public final Integer getNonHighPriority() {
		return nonHighPriority;
	}

	public final void setNonHighPriority(Integer nonHighPriority) {
		this.nonHighPriority = nonHighPriority;
	}

	public final Integer getOther() {
		return other;
	}
	
	public final Integer getTotal() {
		int total = 0;
		if (highPriority != null) {
			total = highPriority;
		}
		if (nonHighPriority != null) {
			total += nonHighPriority;
		}
		if (other != null) {
			total += other;
		}
		return total;
	}

	public final void setOther(Integer other) {
		this.other = other;
	}

	public final Integer getOpenBurning() {
		return openBurning;
	}

	public final void setOpenBurning(Integer openBurning) {
		this.openBurning = openBurning;
	}

	public final Integer getAntiTamperingInspections() {
		return antiTamperingInspections;
	}

	public final void setAntiTamperingInspections(Integer antiTamperingInspections) {
		this.antiTamperingInspections = antiTamperingInspections;
	}
	
	public Integer getAsbestos() {
		return asbestos;
	}

	public void setAsbestos(Integer asbestos) {
		this.asbestos = asbestos;
	}

	public Integer getAsbestosNonNotifier() {
		return asbestosNonNotifier;
	}

	public void setAsbestosNonNotifier(Integer asbestosNonNotifier) {
		this.asbestosNonNotifier = asbestosNonNotifier;
	}
	
	public void copy(Complaint c) {
		this.complaintId = c.complaintId;
		this.doLaaCd = c.doLaaCd;
		this.year = c.year;
		this.month = c.month;
		this.highPriority = c.highPriority;
		this.nonHighPriority = c.nonHighPriority;
		this.other = c.other;
		this.openBurning = c.openBurning;
		this.antiTamperingInspections = c.antiTamperingInspections;
		this.asbestos = c.asbestos;
		this.asbestosNonNotifier = c.asbestosNonNotifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((antiTamperingInspections == null) ? 0
						: antiTamperingInspections.hashCode());
		result = prime * result
				+ ((complaintId == null) ? 0 : complaintId.hashCode());
		result = prime * result + ((doLaaCd == null) ? 0 : doLaaCd.hashCode());
		result = prime * result
				+ ((highPriority == null) ? 0 : highPriority.hashCode());
		result = prime * result + ((month == null) ? 0 : month.hashCode());
		result = prime * result
				+ ((nonHighPriority == null) ? 0 : nonHighPriority.hashCode());
		result = prime * result
				+ ((openBurning == null) ? 0 : openBurning.hashCode());
		result = prime * result + ((other == null) ? 0 : other.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
		result = prime * result + ((asbestos == null) ? 0 : asbestos.hashCode());
		result = prime * result + ((asbestosNonNotifier == null) ? 0 : asbestosNonNotifier.hashCode());
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
		Complaint other = (Complaint) obj;
		if (antiTamperingInspections == null) {
			if (other.antiTamperingInspections != null)
				return false;
		} else if (!antiTamperingInspections
				.equals(other.antiTamperingInspections))
			return false;
		if (complaintId == null) {
			if (other.complaintId != null)
				return false;
		} else if (!complaintId.equals(other.complaintId))
			return false;
		if (doLaaCd == null) {
			if (other.doLaaCd != null)
				return false;
		} else if (!doLaaCd.equals(other.doLaaCd))
			return false;
		if (highPriority == null) {
			if (other.highPriority != null)
				return false;
		} else if (!highPriority.equals(other.highPriority))
			return false;
		if (month == null) {
			if (other.month != null)
				return false;
		} else if (!month.equals(other.month))
			return false;
		if (nonHighPriority == null) {
			if (other.nonHighPriority != null)
				return false;
		} else if (!nonHighPriority.equals(other.nonHighPriority))
			return false;
		if (openBurning == null) {
			if (other.openBurning != null)
				return false;
		} else if (!openBurning.equals(other.openBurning))
			return false;
		if (this.other == null) {
			if (other.other != null)
				return false;
		} else if (!this.other.equals(other.other))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		if (asbestos == null) {
			if (other.asbestos != null)
				return false;
		} else if (!asbestos.equals(other.asbestos))
			return false;
		if (asbestosNonNotifier == null) {
			if (other.asbestosNonNotifier != null)
				return false;
		} else if (!asbestosNonNotifier.equals(other.asbestosNonNotifier))
			return false;
		return true;
	}

    public final ValidationMessage[] validate() {
    	validationMessages.clear();
    	requiredField(year, "yearChoice", "Year");
    	requiredField(month, "monthChoice", "Month");
    	requiredField(doLaaCd, "doLaaChoice", "DO/LAA Code");
        requiredField(highPriority, "highPriorityText", "High Priority Complaints");
        requiredField(nonHighPriority, "nonHighPriorityText", "Non-High Priority Complaints");
        requiredField(other, "otherText", "Other Complaints");
        requiredField(openBurning, "openBurningText", "Open Burning Complaints");
        requiredField(antiTamperingInspections, "antiTamperingInspectionsText", "Anti-Tampering Inspections");
        requiredField(asbestos, "asbestos", "Asbestos");
        requiredField(asbestosNonNotifier, "asbestosNonNotifier", "Asbestos Non-Notifier Inspections");
        return new ArrayList<ValidationMessage>(validationMessages.values())
                .toArray(new ValidationMessage[0]);
    }

	public int compareTo(Complaint o) {
		int ret = year.compareTo(o.year);
		if (ret == 0) {
			ret = month.compareTo(o.month);
		}
		return ret;
	}



}
