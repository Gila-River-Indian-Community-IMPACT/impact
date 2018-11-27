package us.wy.state.deq.impact.database.dbObjects.continuousMonitoring;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class ContinuousMonitorEqt extends BaseDB {

	private Integer monitorEqtId;
	private Integer continuousMonitorId;
	private String manufacturerName;
	private String modelNumber;
	private String serialNumber;
	private Timestamp QAQCSubmittedDate;
	private Timestamp QAQCAcceptedDate;
	private Timestamp installDate;
	private Timestamp removalDate;
	private String addlInfo;
	private Integer addedBy;
	private Timestamp createdOn;

	public ContinuousMonitorEqt() {
		this.requiredField(manufacturerName, "Manufacturer");
		this.requiredField(modelNumber, "Model Number");
		this.requiredField(serialNumber, "Serial Number");

		setDirty(false);

	}

	public ContinuousMonitorEqt(ContinuousMonitorEqt old) {

		super(old);

		setMonitorEqtId(old.getMonitorEqtId());
		setContinuousMonitorId(old.getContinuousMonitorId());
		setManufacturerName(old.getManufacturerName());
		setModelNumber(old.getModelNumber());
		setSerialNumber(old.getSerialNumber());
		setQAQCSubmittedDate(old.getQAQCSubmittedDate());
		setQAQCAcceptedDate(old.getQAQCAcceptedDate());
		setInstallDate(old.getInstallDate());
		setRemovalDate(old.getRemovalDate());
		setAddlInfo(old.getAddlInfo());
		setAddedBy(old.getAddedBy());

		setLastModified(old.getLastModified());
		setDirty(old.isDirty());
	}

	public void populate(ResultSet rs) {

		try {
			setMonitorEqtId(AbstractDAO.getInteger(rs, "monitor_eqt_id"));
			setContinuousMonitorId(AbstractDAO.getInteger(rs, "monitor_id"));
			setManufacturerName(rs.getString("manufacturer_name"));
			setModelNumber(rs.getString("model_number"));
			setSerialNumber(rs.getString("serial_number"));
			setQAQCSubmittedDate(rs.getTimestamp("qaqc_submitted_date"));
			setQAQCAcceptedDate(rs.getTimestamp("qaqc_accepted_date"));
			setInstallDate(rs.getTimestamp("install_date"));
			setRemovalDate(rs.getTimestamp("removal_date"));
			setAddlInfo(rs.getString("addl_info"));
			setAddedBy(AbstractDAO.getInteger(rs, "added_by"));
			setCreatedOn(rs.getTimestamp("created_on"));
			setLastModified(AbstractDAO.getInteger(rs, "cme_lm"));
			
			setDirty(false);
		} catch (SQLException sqle) {
			logger.error("Required field error");
		}

	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public Integer getMonitorEqtId() {
		return monitorEqtId;
	}

	public void setMonitorEqtId(Integer monitorEqtId) {
		this.monitorEqtId = monitorEqtId;
	}

	public Integer getContinuousMonitorId() {
		return continuousMonitorId;
	}

	public void setContinuousMonitorId(Integer continuousMonitorId) {
		this.continuousMonitorId = continuousMonitorId;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Timestamp getQAQCSubmittedDate() {
		return QAQCSubmittedDate;
	}

	public void setQAQCSubmittedDate(Timestamp qAQCSubmittedDate) {
		QAQCSubmittedDate = qAQCSubmittedDate;
	}

	public Timestamp getQAQCAcceptedDate() {
		return QAQCAcceptedDate;
	}

	public void setQAQCAcceptedDate(Timestamp qAQCAcceptedDate) {
		QAQCAcceptedDate = qAQCAcceptedDate;
	}

	public Timestamp getInstallDate() {
		return installDate;
	}

	public void setInstallDate(Timestamp installDate) {
		this.installDate = installDate;
	}

	public Timestamp getRemovalDate() {
		return removalDate;
	}

	public void setRemovalDate(Timestamp removalDate) {
		this.removalDate = removalDate;
	}

	public String getAddlInfo() {
		return addlInfo;
	}

	public void setAddlInfo(String addlInfo) {
		this.addlInfo = addlInfo;
	}

	public Integer getAddedBy() {
		return addedBy;
	}

	public void setAddedBy(Integer addedBy) {
		this.addedBy = addedBy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((QAQCAcceptedDate == null) ? 0 : QAQCAcceptedDate.hashCode());
		result = prime
				* result
				+ ((QAQCSubmittedDate == null) ? 0 : QAQCSubmittedDate
						.hashCode());
		result = prime * result + ((addedBy == null) ? 0 : addedBy.hashCode());
		result = prime * result
				+ ((addlInfo == null) ? 0 : addlInfo.hashCode());
		result = prime
				* result
				+ ((continuousMonitorId == null) ? 0 : continuousMonitorId
						.hashCode());
		result = prime * result
				+ ((installDate == null) ? 0 : installDate.hashCode());
		result = prime
				* result
				+ ((manufacturerName == null) ? 0 : manufacturerName.hashCode());
		result = prime * result
				+ ((modelNumber == null) ? 0 : modelNumber.hashCode());
		result = prime * result
				+ ((monitorEqtId == null) ? 0 : monitorEqtId.hashCode());
		result = prime * result
				+ ((removalDate == null) ? 0 : removalDate.hashCode());
		result = prime * result
				+ ((serialNumber == null) ? 0 : serialNumber.hashCode());
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
		if (!(obj instanceof ContinuousMonitorEqt)) {
			return false;
		}
		ContinuousMonitorEqt other = (ContinuousMonitorEqt) obj;
		if (QAQCAcceptedDate == null) {
			if (other.QAQCAcceptedDate != null) {
				return false;
			}
		} else if (!QAQCAcceptedDate.equals(other.QAQCAcceptedDate)) {
			return false;
		}
		if (QAQCSubmittedDate == null) {
			if (other.QAQCSubmittedDate != null) {
				return false;
			}
		} else if (!QAQCSubmittedDate.equals(other.QAQCSubmittedDate)) {
			return false;
		}
		if (addedBy == null) {
			if (other.addedBy != null) {
				return false;
			}
		} else if (!addedBy.equals(other.addedBy)) {
			return false;
		}
		if (addlInfo == null) {
			if (other.addlInfo != null) {
				return false;
			}
		} else if (!addlInfo.equals(other.addlInfo)) {
			return false;
		}
		if (continuousMonitorId == null) {
			if (other.continuousMonitorId != null) {
				return false;
			}
		} else if (!continuousMonitorId.equals(other.continuousMonitorId)) {
			return false;
		}
		if (installDate == null) {
			if (other.installDate != null) {
				return false;
			}
		} else if (!installDate.equals(other.installDate)) {
			return false;
		}
		if (manufacturerName == null) {
			if (other.manufacturerName != null) {
				return false;
			}
		} else if (!manufacturerName.equals(other.manufacturerName)) {
			return false;
		}
		if (modelNumber == null) {
			if (other.modelNumber != null) {
				return false;
			}
		} else if (!modelNumber.equals(other.modelNumber)) {
			return false;
		}
		if (monitorEqtId == null) {
			if (other.monitorEqtId != null) {
				return false;
			}
		} else if (!monitorEqtId.equals(other.monitorEqtId)) {
			return false;
		}
		if (removalDate == null) {
			if (other.removalDate != null) {
				return false;
			}
		} else if (!removalDate.equals(other.removalDate)) {
			return false;
		}
		if (serialNumber == null) {
			if (other.serialNumber != null) {
				return false;
			}
		} else if (!serialNumber.equals(other.serialNumber)) {
			return false;
		}
		return true;
	}
}
