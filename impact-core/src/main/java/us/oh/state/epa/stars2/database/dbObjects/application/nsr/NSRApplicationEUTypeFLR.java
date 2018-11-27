package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeFLR extends NSRApplicationEUType {

	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeFLR:";
	private String emergencyFlareOnlyCd;
	private String ignitionDeviceTypeCd;
	private Float btuContent;
	private String smokelessDesignCd;
	private String assitGasUtilizedCd;
	private Float assistGasUtilizedBtu;
	private Float fuelSulfurContent;
	private String fuelSulfurContentUnitsCd;
	private Float wasteGasVolume;
	private String wasteGasVolumeUnitsCd;
	private Timestamp installationDate;
	private String continuouslyMonitoredCd;
	private String continuousMonitoringDesc;

	public NSRApplicationEUTypeFLR() {
		super();
	}

	public NSRApplicationEUTypeFLR(NSRApplicationEUTypeFLR old) {
		super(old);
		if (old != null) {
			setEmergencyFlareOnlyCd(old.getEmergencyFlareOnlyCd());
			setIgnitionDeviceTypeCd(old.getIgnitionDeviceTypeCd());
			setBtuContent(old.getBtuContent());
			setSmokelessDesignCd(old.getSmokelessDesignCd());
			setAssitGasUtilizedCd(old.getAssitGasUtilizedCd());
			setAssistGasUtilizedBtu(old.getAssistGasUtilizedBtu());
			setFuelSulfurContent(old.getFuelSulfurContent());
			setFuelSulfurContentUnitsCd(old.getFuelSulfurContentUnitsCd());
			setWasteGasVolume(old.getWasteGasVolume());
			setWasteGasVolumeUnitsCd(old.getWasteGasVolumeUnitsCd());
			setInstallationDate(old.getInstallationDate());
			setContinuouslyMonitoredCd(old.getContinuouslyMonitoredCd());
			setContinuousMonitoringDesc(old.getContinuousMonitoringDesc());
		}
	}

	public void setEmergencyFlareOnlyCd(String emergencyFlareOnlyCd) {
		this.emergencyFlareOnlyCd = emergencyFlareOnlyCd;
	}

	public String getEmergencyFlareOnlyCd() {
		return emergencyFlareOnlyCd;
	}

	public void setIgnitionDeviceTypeCd(String ignitionDeviceTypeCd) {
		this.ignitionDeviceTypeCd = ignitionDeviceTypeCd;
	}

	public String getIgnitionDeviceTypeCd() {
		return ignitionDeviceTypeCd;
	}

	public void setBtuContent(Float btuContent) {
		this.btuContent = btuContent;
	}

	public Float getBtuContent() {
		return btuContent;
	}

	public void setSmokelessDesignCd(String smokelessDesignCd) {
		this.smokelessDesignCd = smokelessDesignCd;
	}

	public String getSmokelessDesignCd() {
		return smokelessDesignCd;
	}

	public void setAssitGasUtilizedCd(String assitGasUtilizedCd) {
		this.assitGasUtilizedCd = assitGasUtilizedCd;

		if (!isAssistGasUtilized()) {
			setAssistGasUtilizedBtu(null);
			setFuelSulfurContent(null);
			setFuelSulfurContentUnitsCd(null);
		}
	}

	public String getAssitGasUtilizedCd() {
		return assitGasUtilizedCd;
	}

	public void setAssistGasUtilizedBtu(Float assistGasUtilizedBtu) {
		this.assistGasUtilizedBtu = assistGasUtilizedBtu;
	}

	public Float getAssistGasUtilizedBtu() {
		return assistGasUtilizedBtu;
	}

	public void setFuelSulfurContent(Float fuelSulfurContent) {
		this.fuelSulfurContent = fuelSulfurContent;
	}

	public Float getFuelSulfurContent() {
		return fuelSulfurContent;
	}

	public void setFuelSulfurContentUnitsCd(String fuelSulfurContentUnitsCd) {
		this.fuelSulfurContentUnitsCd = fuelSulfurContentUnitsCd;
	}

	public String getFuelSulfurContentUnitsCd() {
		return fuelSulfurContentUnitsCd;
	}

	public void setWasteGasVolume(Float wasteGasVolume) {
		this.wasteGasVolume = wasteGasVolume;
	}

	public Float getWasteGasVolume() {
		return wasteGasVolume;
	}

	public void setWasteGasVolumeUnitsCd(String wasteGasVolumeUnitsCd) {
		this.wasteGasVolumeUnitsCd = wasteGasVolumeUnitsCd;
	}

	public String getWasteGasVolumeUnitsCd() {
		return wasteGasVolumeUnitsCd;
	}

	public void setInstallationDate(Timestamp installationDate) {
		this.installationDate = installationDate;
	}

	public Timestamp getInstallationDate() {
		return installationDate;
	}

	public void setContinuouslyMonitoredCd(String continuouslyMonitoredCd) {
		this.continuouslyMonitoredCd = continuouslyMonitoredCd;

		if (!isContinuouslyMonitored()) {
			setContinuousMonitoringDesc(null);
		}
	}

	public String getContinuouslyMonitoredCd() {
		return continuouslyMonitoredCd;
	}

	public void setContinuousMonitoringDesc(String continuousMonitoringDesc) {
		this.continuousMonitoringDesc = continuousMonitoringDesc;
	}

	public String getContinuousMonitoringDesc() {
		return continuousMonitoringDesc;
	}

	public boolean isAssistGasUtilized() {
		boolean ret;

		ret = !Utility.isNullOrEmpty(this.assitGasUtilizedCd)
				&& this.assitGasUtilizedCd.equals("Yes");
		return ret;
	}

	public boolean isContinuouslyMonitored() {
		boolean ret;

		ret = !Utility.isNullOrEmpty(this.continuouslyMonitoredCd)
				&& this.continuouslyMonitoredCd.equals("Yes");
		return ret;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setEmergencyFlareOnlyCd(rs.getString("emergency_flare_only_cd"));
		setIgnitionDeviceTypeCd(rs.getString("ignition_device_type_cd"));
		setBtuContent(AbstractDAO.getFloat(rs, "btu"));
		setSmokelessDesignCd(rs.getString("smokeless_design_cd"));
		setAssitGasUtilizedCd(rs.getString("assist_gas_utilized_cd"));
		setAssistGasUtilizedBtu(AbstractDAO.getFloat(rs,
				"assist_gas_utilized_btu"));
		setFuelSulfurContent(AbstractDAO.getFloat(rs, "fuel_sulfur"));
		setFuelSulfurContentUnitsCd(rs.getString("units_fuel_sulfur_cd"));
		setWasteGasVolume(AbstractDAO.getFloat(rs, "waste_gas_volume"));
		setWasteGasVolumeUnitsCd(rs.getString("units_waste_gas_volume_cd"));
		setInstallationDate(rs.getTimestamp("installation_date"));
		setContinuouslyMonitoredCd(rs.getString("continuously_monitored_cd"));
		setContinuousMonitoringDesc(rs
				.getString("describe_continuous_monitoring"));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((emergencyFlareOnlyCd == null) ? 0 : emergencyFlareOnlyCd
						.hashCode());
		result = prime
				* result
				+ ((ignitionDeviceTypeCd == null) ? 0 : ignitionDeviceTypeCd
						.hashCode());
		result = prime * result
				+ ((btuContent == null) ? 0 : btuContent.hashCode());
		result = prime
				* result
				+ ((smokelessDesignCd == null) ? 0 : smokelessDesignCd
						.hashCode());
		result = prime
				* result
				+ ((assitGasUtilizedCd == null) ? 0 : assitGasUtilizedCd
						.hashCode());
		result = prime
				* result
				+ ((assistGasUtilizedBtu == null) ? 0 : assistGasUtilizedBtu
						.hashCode());
		result = prime
				* result
				+ ((fuelSulfurContent == null) ? 0 : fuelSulfurContent
						.hashCode());
		result = prime
				* result
				+ ((fuelSulfurContentUnitsCd == null) ? 0
						: fuelSulfurContentUnitsCd.hashCode());
		result = prime * result
				+ ((wasteGasVolume == null) ? 0 : wasteGasVolume.hashCode());
		result = prime
				* result
				+ ((wasteGasVolumeUnitsCd == null) ? 0 : wasteGasVolumeUnitsCd
						.hashCode());
		result = prime
				* result
				+ ((installationDate == null) ? 0 : installationDate.hashCode());
		result = prime
				* result
				+ ((continuouslyMonitoredCd == null) ? 0
						: continuouslyMonitoredCd.hashCode());
		result = prime
				* result
				+ ((continuousMonitoringDesc == null) ? 0
						: continuousMonitoringDesc.hashCode());
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
		final NSRApplicationEUTypeFLR other = (NSRApplicationEUTypeFLR) obj;
		if (emergencyFlareOnlyCd == null) {
			if (other.emergencyFlareOnlyCd != null)
				return false;
		} else if (!emergencyFlareOnlyCd.equals(other.emergencyFlareOnlyCd))
			return false;
		if (ignitionDeviceTypeCd == null) {
			if (other.ignitionDeviceTypeCd != null)
				return false;
		} else if (!ignitionDeviceTypeCd.equals(other.ignitionDeviceTypeCd))
			return false;
		if (btuContent == null) {
			if (other.btuContent != null)
				return false;
		} else if (!btuContent.equals(other.btuContent))
			return false;
		if (smokelessDesignCd == null) {
			if (other.smokelessDesignCd != null)
				return false;
		} else if (!smokelessDesignCd.equals(other.smokelessDesignCd))
			return false;
		if (assitGasUtilizedCd == null) {
			if (other.assitGasUtilizedCd != null)
				return false;
		} else if (!assitGasUtilizedCd.equals(other.assitGasUtilizedCd))
			return false;
		if (assistGasUtilizedBtu == null) {
			if (other.assistGasUtilizedBtu != null)
				return false;
		} else if (!assistGasUtilizedBtu.equals(other.assistGasUtilizedBtu))
			return false;
		if (fuelSulfurContent == null) {
			if (other.fuelSulfurContent != null)
				return false;
		} else if (!fuelSulfurContent.equals(other.fuelSulfurContent))
			return false;
		if (fuelSulfurContentUnitsCd == null) {
			if (other.fuelSulfurContentUnitsCd != null)
				return false;
		} else if (!fuelSulfurContentUnitsCd
				.equals(other.fuelSulfurContentUnitsCd))
			return false;
		if (wasteGasVolume == null) {
			if (other.wasteGasVolume != null)
				return false;
		} else if (!wasteGasVolume.equals(other.wasteGasVolume))
			return false;
		if (wasteGasVolumeUnitsCd == null) {
			if (other.wasteGasVolumeUnitsCd != null)
				return false;
		} else if (!wasteGasVolumeUnitsCd.equals(other.wasteGasVolumeUnitsCd))
			return false;
		if (installationDate == null) {
			if (other.installationDate != null)
				return false;
		} else if (!installationDate.equals(other.installationDate))
			return false;
		if (continuouslyMonitoredCd == null) {
			if (other.continuouslyMonitoredCd != null)
				return false;
		} else if (!continuouslyMonitoredCd
				.equals(other.continuouslyMonitoredCd))
			return false;
		if (continuousMonitoringDesc == null) {
			if (other.continuousMonitoringDesc != null)
				return false;
		} else if (!continuousMonitoringDesc
				.equals(other.continuousMonitoringDesc))
			return false;
		return true;
	}

	public final ValidationMessage[] validate() {
		this.clearValidationMessages();
		requiredFields();
		validateRanges();

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public void requiredFields() {
		requiredField(this.emergencyFlareOnlyCd, PAGE_VIEW_ID_PREFIX
				+ "emergencyFlareOnlyCd", "Emergency Flare Only",
				"emergencyFlareOnlyCd");
		requiredField(this.ignitionDeviceTypeCd, PAGE_VIEW_ID_PREFIX
				+ "ignitionDeviceTypeCd", "Ignition Device Type",
				"ignitionDeviceTypeCd");
		requiredField(this.btuContent, PAGE_VIEW_ID_PREFIX + "btuContent",
				"Btu Content (Btu/scf)", "btuContent");
		requiredField(this.smokelessDesignCd, PAGE_VIEW_ID_PREFIX
				+ "smokelessDesignCd", "Smokeless Design", "smokelessDesignCd");
		requiredField(this.assitGasUtilizedCd, PAGE_VIEW_ID_PREFIX
				+ "assitGasUtilizedCd", "Assist Gas Utilized",
				"assitGasUtilizedCd");
		if (isAssistGasUtilized()) {
			requiredField(this.assistGasUtilizedBtu, PAGE_VIEW_ID_PREFIX
					+ "assistGasUtilizedBtu", "BTU Content (BTU/scf)",
					"assistGasUtilizedBtu");
			requiredField(this.fuelSulfurContent, PAGE_VIEW_ID_PREFIX
					+ "fuelSulfurContent", "Fuel Sulfur Content",
					"fuelSulfurContent");
			requiredField(this.fuelSulfurContentUnitsCd, PAGE_VIEW_ID_PREFIX
					+ "fuelSulfurContentUnitsCd", "Fule Sulfur Content Units",
					"fuelSulfurContentUnitsCd");
		}
		requiredField(this.wasteGasVolume, PAGE_VIEW_ID_PREFIX
				+ "wasteGasVolume", "Waste Gas Volume", "wasteGasVolume");
		requiredField(this.wasteGasVolumeUnitsCd, PAGE_VIEW_ID_PREFIX
				+ "wasteGasVolumeUnitsCd", "Waste Gas Volume Units",
				"wasteGasVolumeUnitsCd");
		requiredField(this.continuouslyMonitoredCd, PAGE_VIEW_ID_PREFIX
				+ "continuouslyMonitoredCd", "Continuously Monitored",
				"continuouslyMonitoredCd");
		if (isContinuouslyMonitored()) {
			requiredField(this.continuousMonitoringDesc, PAGE_VIEW_ID_PREFIX
					+ "continuousMonitoringDesc",
					"Describe Continuous Monitoring",
					"continuousMonitoringDesc");
		}
	}

	private void validateRanges() {
		checkRangeValues(this.btuContent, new Float(0.01), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "btuContent",
				"Btu Content (Btu/scf)");
		checkRangeValues(this.assistGasUtilizedBtu, new Float(0.01), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "assistGasUtilizedBtu",
				"BTU Content (BTU/scf)");
		checkRangeValues(this.fuelSulfurContent, new Float(0.00), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "fuelSulfurContent",
				"Fuel Sulfur Content");
		checkRangeValues(this.wasteGasVolume, new Float(0.01), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "wasteGasVolume",
				"Waste Gas Volume");
	}
}
