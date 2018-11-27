package us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

@SuppressWarnings("serial")
public class EmissionUnitReplacement extends BaseDB {
	private Integer replacementId;
	private Integer emuId;
	private String serialNumber;
	private Timestamp serialNumberEffectiveDate;
	private String manufacturerName;
	private Timestamp installDate;
	private Timestamp operationStartupDate;
	private Timestamp orderDate;
	private Timestamp manufactureDate;
	private Timestamp shutdownDate;
	private Timestamp removalDate;
	
	public EmissionUnitReplacement() {
		
	}
	
	public EmissionUnitReplacement(EmissionUnitReplacement old) {
		this.emuId = old.emuId;
		this.serialNumber = old.serialNumber;
		this.serialNumberEffectiveDate = old.serialNumberEffectiveDate;
		this.manufacturerName = old.manufacturerName;
		this.installDate = old.installDate;
		this.operationStartupDate = old.operationStartupDate;
		this.orderDate = old.orderDate;
		this.manufactureDate = old.manufactureDate;
		this.shutdownDate = old.shutdownDate;
		this.removalDate = old.removalDate;
	}

	public Integer getReplacementId() {
		return replacementId;
	}

	public void setReplacementId(Integer replacementId) {
		this.replacementId = replacementId;
	}

	public Integer getEmuId() {
		return emuId;
	}

	public void setEmuId(Integer emuId) {
		this.emuId = emuId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Timestamp getSerialNumberEffectiveDate() {
		return serialNumberEffectiveDate;
	}

	public void setSerialNumberEffectiveDate(Timestamp serialNumberEffectiveDate) {
		this.serialNumberEffectiveDate = serialNumberEffectiveDate;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public Timestamp getInstallDate() {
		return installDate;
	}

	public void setInstallDate(Timestamp installDate) {
		this.installDate = installDate;
	}

	public Timestamp getOperationStartupDate() {
		return operationStartupDate;
	}

	public void setOperationStartupDate(Timestamp operationStartupDate) {
		this.operationStartupDate = operationStartupDate;
	}

	public Timestamp getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Timestamp orderDate) {
		this.orderDate = orderDate;
	}

	public Timestamp getManufactureDate() {
		return manufactureDate;
	}

	public void setManufactureDate(Timestamp manufactureDate) {
		this.manufactureDate = manufactureDate;
	}

	public Timestamp getShutdownDate() {
		return shutdownDate;
	}

	public void setShutdownDate(Timestamp shutdownDate) {
		if (this.shutdownDate != null
				&& (shutdownDate == null || shutdownDate.equals(""))) {
			requiredField(shutdownDate, "engineSerialTrackingPage:shutdownDate",
					"Shutdown Date");
		} else if(shutdownDate != null && !shutdownDate.equals("")) {
			requiredField(shutdownDate, "engineSerialTrackingPage:shutdownDate",
					"Shutdown Date");
		}
		
		this.shutdownDate = shutdownDate;
	}
	
	public Timestamp getRemovalDate() {
		return removalDate;
	}

	public void setRemovalDate(Timestamp removalDate) {
		if (this.removalDate != null
				&& (removalDate == null || removalDate.equals(""))) {
			requiredField(removalDate, "engineSerialTrackingPage:removalDate",
					"Removal Date");
		} else if(removalDate != null && !removalDate.equals("")) {
			requiredField(removalDate, "engineSerialTrackingPage:removalDate",
					"Removal Date");
		}
		this.removalDate = removalDate;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		try {
			setReplacementId(AbstractDAO.getInteger(rs, "replacement_id"));
			setEmuId(AbstractDAO.getInteger(rs, "emu_id"));
			setSerialNumber(rs.getString("serial_number"));
			setSerialNumberEffectiveDate(rs
					.getTimestamp("serial_number_effective_date"));
			setManufacturerName(rs.getString("manufacture_name"));
			setInstallDate(rs.getTimestamp("install_date"));
			setOperationStartupDate(rs.getTimestamp("operation_startup_date"));
			setOrderDate(rs.getTimestamp("order_date"));
			setManufactureDate(rs.getTimestamp("manufacture_date"));
			setShutdownDate(rs.getTimestamp("shutdown_date"));
			setRemovalDate(rs.getTimestamp("removal_date"));
		} catch (SQLException sqle) {
			logger.error("Required field error");
		} finally {
			newObject = false;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final EmissionUnitReplacement other = (EmissionUnitReplacement) obj;
		if (replacementId == null) {
			if (other.replacementId != null)
				return false;
		} else if (!replacementId.equals(other.replacementId))
			return false;

		if (emuId == null) {
			if (other.emuId != null)
				return false;
		} else if (!emuId.equals(other.emuId))
			return false;

		if (manufacturerName == null) {
			if (other.manufacturerName != null)
				return false;
		} else if (!manufacturerName.equals(other.manufacturerName))
			return false;

		if (serialNumber == null) {
			if (other.serialNumber != null)
				return false;
		} else if (!serialNumber.equals(other.serialNumber))
			return false;

		if (serialNumberEffectiveDate == null) {
			if (other.serialNumberEffectiveDate != null)
				return false;
		} else if (!serialNumberEffectiveDate
				.equals(other.serialNumberEffectiveDate))
			return false;

		if (installDate == null) {
			if (other.installDate != null)
				return false;
		} else if (!installDate
				.equals(other.installDate))
			return false;

		if (operationStartupDate == null) {
			if (other.operationStartupDate != null)
				return false;
		} else if (!operationStartupDate
				.equals(other.operationStartupDate))
			return false;

		if (orderDate == null) {
			if (other.orderDate != null)
				return false;
		} else if (!orderDate
				.equals(other.orderDate))
			return false;
		
		if (manufactureDate == null) {
			if (other.manufactureDate != null)
				return false;
		} else if (!manufactureDate
				.equals(other.manufactureDate))
			return false;
		
		if (shutdownDate == null) {
			if (other.shutdownDate != null)
				return false;
		} else if (!shutdownDate
				.equals(other.shutdownDate))
			return false;

		if (removalDate == null) {
			if (other.removalDate != null)
				return false;
		} else if (!removalDate
				.equals(other.removalDate))
			return false;
		return true;
	}

	public ValidationMessage[] validate(String emissionUnitTypeCd) {

		String pageViewId = "emissionUnitType" + emissionUnitTypeCd + ":";
		requiredField(serialNumber, pageViewId + "serialNumber",
				"Serial Number");
		requiredField(serialNumberEffectiveDate, pageViewId
				+ "serialNumberEffectiveDate", "Serial Number Effective Date");
		requiredField(manufacturerName, pageViewId + "manufacturerName",
				"Manufacturer Name");

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	
	public ValidationMessage[] validateEngine(List<EmissionUnitReplacement> serialNumbers) {
		List<ValidationMessage> ret = new ArrayList<ValidationMessage>();
		validateAnyEngine();
		ret.addAll(validationMessages.values());
		
		if(this.isNewObject()) {
			for(EmissionUnitReplacement serialNumber : serialNumbers) {
				ret.addAll(serialNumber.validateOldEngine());
			}
		}

		return new ArrayList<ValidationMessage>(ret).toArray(new ValidationMessage[0]);
	}
	
	private void validateAnyEngine() {
		String pageViewId = "engineSerialTrackingPage:";
		
		requiredField(serialNumber, pageViewId + "serialNumber",
				"Serial Number");
	}
	
	public List<ValidationMessage> validateOldEngine() {
		List<ValidationMessage> ret = new ArrayList<ValidationMessage>();
		
		String pageViewId = "engineSerialTrackingPage:";
		
		if (shutdownDate == null || shutdownDate.equals("")) {
			ret.add(new ValidationMessage(pageViewId + "shutdownDate",
					"Current engine [serial number: "+ this.serialNumber +"] must have a Shutdown Date.",
					ValidationMessage.Severity.ERROR, null));
		}
		
		if (removalDate == null || removalDate.equals("")) {
			ret.add(new ValidationMessage(pageViewId + "removalDate",
					"Current engine [serial number: "+ this.serialNumber +"] must have a Removal Date.",
					ValidationMessage.Severity.ERROR, null));
		}
		
		return ret;
	}
	
	public boolean isSerialNumberCurrent() {
		boolean ret = false;

		if ((shutdownDate == null || shutdownDate.equals(""))
				&& (removalDate == null || removalDate.equals(""))) {
			ret = true;
		}
		
		return ret;
	}
}
