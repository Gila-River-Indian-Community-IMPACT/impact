package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeENG extends NSRApplicationEUType {
	
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeENG:";
	private Float btuContent;
	private String engBtuUnitsCd;
	private Float fuelSulfurContent;
	private String engSulfarUnitsCd;
	private String serviceType;
	private String dieselEngineEpaTierCertifiedFlag;
	private String tierRating;
	private Boolean diesel = false;
	
	// fpEU data
	private String fpEuPrimaryFuelType;
	private String fpEuSecondaryFuelType;

	public NSRApplicationEUTypeENG() {
		super();
	}

	public NSRApplicationEUTypeENG(NSRApplicationEUTypeENG old) {
		super(old);
		if (old != null) {
			setBtuContent(old.getBtuContent());
			setEngBtuUnitsCd(old.getEngBtuUnitsCd());
			setFuelSulfurContent(old.getFuelSulfurContent());
			setEngSulfarUnitsCd(old.getEngSulfarUnitsCd());
			setServiceType(old.getServiceType());
			setDieselEngineEpaTierCertifiedFlag(old.getDieselEngineEpaTierCertifiedFlag());
			setTierRating(old.getTierRating());
			setDiesel(old.getDiesel());
			
			setFpEuPrimaryFuelType(old.getFpEuPrimaryFuelType());
			setFpEuSecondaryFuelType(old.getFpEuSecondaryFuelType());
		}
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setBtuContent(AbstractDAO.getFloat(rs, "btu"));
		setEngBtuUnitsCd(rs.getString("units_btu_cd"));
		setFuelSulfurContent(AbstractDAO.getFloat(rs, "fuel_sulfur"));
		setEngSulfarUnitsCd(rs.getString("units_fuel_sulfur_cd"));
		setServiceType(rs.getString("service_type_cd"));
		setDieselEngineEpaTierCertifiedFlag(rs.getString("diesel_engine_epa_tier_certified_flag"));
		setTierRating(rs.getString("tier_rating"));
		
		setDiesel(false);
		
		setFpEuPrimaryFuelType(null);
		setFpEuSecondaryFuelType(null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((btuContent == null) ? 0 : btuContent.hashCode());
		result = prime * result
				+ ((engBtuUnitsCd == null) ? 0 : engBtuUnitsCd.hashCode());
		result = prime
				* result
				+ ((fuelSulfurContent == null) ? 0 : fuelSulfurContent
						.hashCode());
		result = prime
				* result
				+ ((engSulfarUnitsCd == null) ? 0 : engSulfarUnitsCd.hashCode());
		result = prime * result
				+ ((serviceType == null) ? 0 : serviceType.hashCode());
		result = prime * result
				+ ((dieselEngineEpaTierCertifiedFlag == null) ? 0 : dieselEngineEpaTierCertifiedFlag.hashCode());
		result = prime * result
				+ ((tierRating == null) ? 0 : tierRating.hashCode());
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
		final NSRApplicationEUTypeENG other = (NSRApplicationEUTypeENG) obj;
		if (btuContent == null) {
			if (other.btuContent != null)
				return false;
		} else if (!btuContent.equals(other.btuContent))
			return false;
		if (engBtuUnitsCd == null) {
			if (other.engBtuUnitsCd != null)
				return false;
		} else if (!engBtuUnitsCd.equals(other.engBtuUnitsCd))
			return false;
		if (fuelSulfurContent == null) {
			if (other.fuelSulfurContent != null)
				return false;
		} else if (!fuelSulfurContent.equals(other.fuelSulfurContent))
			return false;
		if (engSulfarUnitsCd == null) {
			if (other.engSulfarUnitsCd != null)
				return false;
		} else if (!engSulfarUnitsCd.equals(other.engSulfarUnitsCd))
			return false;
		if (serviceType == null) {
			if (other.serviceType != null)
				return false;
		} else if (!serviceType.equals(other.serviceType))
			return false;
		if (dieselEngineEpaTierCertifiedFlag == null) {
			if (other.dieselEngineEpaTierCertifiedFlag != null)
				return false;
		} else if (!dieselEngineEpaTierCertifiedFlag.equals(other.dieselEngineEpaTierCertifiedFlag))
			return false;
		if (tierRating == null) {
			if (other.tierRating != null)
				return false;
		} else if (!tierRating.equals(other.tierRating))
			return false;
		return true;
	}

	public Boolean getDiesel() {
		return diesel;
	}

	public void setDiesel(Boolean diesel) {
		this.diesel = diesel;
	}

	public String getTierRating() {
		return tierRating;
	}

	public void setTierRating(String tierRating) {
		this.tierRating = tierRating;
	}

	public String getDieselEngineEpaTierCertifiedFlag() {
		return dieselEngineEpaTierCertifiedFlag;
	}

	public void setDieselEngineEpaTierCertifiedFlag(
			String dieselEngineEpaTierCertifiedFlag) {
		this.dieselEngineEpaTierCertifiedFlag = dieselEngineEpaTierCertifiedFlag;
	}

	public Float getBtuContent() {
		return btuContent;
	}

	public void setBtuContent(Float btuContent) {
		this.btuContent = btuContent;
	}

	public String getEngBtuUnitsCd() {
		return engBtuUnitsCd;
	}

	public void setEngBtuUnitsCd(String engBtuUnitsCd) {
		this.engBtuUnitsCd = engBtuUnitsCd;
	}

	public Float getFuelSulfurContent() {
		return fuelSulfurContent;
	}

	public void setFuelSulfurContent(Float fuelSulfurContent) {
		this.fuelSulfurContent = fuelSulfurContent;
	}

	public String getEngSulfarUnitsCd() {
		return engSulfarUnitsCd;
	}

	public void setEngSulfarUnitsCd(String engSulfarUnitsCd) {
		this.engSulfarUnitsCd = engSulfarUnitsCd;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public boolean isDieselEngineEpaTierCertified() {
		return AbstractDAO.translateIndicatorToBoolean(dieselEngineEpaTierCertifiedFlag);
	}

	@Override
	public final ValidationMessage[] validate() {
		this.clearValidationMessages();
		requiredFields();
		validateRanges();

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public void requiredFields() {

		if (this.diesel) {
			
			requiredField(this.dieselEngineEpaTierCertifiedFlag, PAGE_VIEW_ID_PREFIX + "dieselEngineEpaTierCertified",
					"Diesel Engine EPA Tier Certified", "dieselEngineEpaTierCertified");

			if ("Y".equals(this.dieselEngineEpaTierCertifiedFlag)) {
				requiredField(this.tierRating, PAGE_VIEW_ID_PREFIX + "tierRating",
						"Tier Rating", "tierRating");
			}
		}
		
		requiredField(this.btuContent, PAGE_VIEW_ID_PREFIX + "btuContent",
				"Btu Content", "btuContent");

		requiredField(this.engBtuUnitsCd,
				PAGE_VIEW_ID_PREFIX + "engBtuUnitsCd", "Btu Content Units",
				"engBtuUnitsCd");

		requiredField(this.fuelSulfurContent, PAGE_VIEW_ID_PREFIX
				+ "fuelSulfurContent", "Fuel Sulfur Content",
				"fuelSulfurContent");

		requiredField(this.engSulfarUnitsCd, PAGE_VIEW_ID_PREFIX
				+ "engSulfarUnitsCd", "Fuel Sulfur Content Units",
				"engSulfarUnitsCd");

		requiredField(this.serviceType, PAGE_VIEW_ID_PREFIX + "serviceType",
				"Type of Service", "serviceType");

	}

	public void validateRanges() {
		checkRangeValues(fuelSulfurContent, new Float(0.00), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "fuelSulfurContent",
				"Fuel Sulfur Content");
		checkRangeValues(btuContent, new Float(0.01),
				new Float(Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "btuContent",
				"Btu Content");
	}
	
	public void loadFpEuTypeData(EmissionUnitType fpEuType) {
		if (fpEuType != null) {
			this.setFpEuPrimaryFuelType(fpEuType.getPrimaryFuelType());
			this.setFpEuSecondaryFuelType(fpEuType.getSecondaryFuelType());
		}
	}
	
	public String getFpEuPrimaryFuelType() {
		return fpEuPrimaryFuelType;
	}

	public void setFpEuPrimaryFuelType(String fpEuPrimaryFuelType) {
		this.fpEuPrimaryFuelType = fpEuPrimaryFuelType;
		//if (!getDiesel()) {
		//	setDieselEngineEpaTierCertifiedFlag(null);
		//	setTierRating(null);
		//}
	}
	
	public String getFpEuSecondaryFuelType() {
		return fpEuSecondaryFuelType;
	}

	public void setFpEuSecondaryFuelType(String fpEuSecondaryFuelType) {
		this.fpEuSecondaryFuelType = fpEuSecondaryFuelType;
		//if (!getDiesel()) {
		//	setDieselEngineEpaTierCertifiedFlag(null);
		//	setTierRating(null);
		//}
	}
}
