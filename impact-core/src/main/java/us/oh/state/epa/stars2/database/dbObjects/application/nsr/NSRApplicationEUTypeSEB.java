package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeSEB extends NSRApplicationEUType {
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeSEB:";

	private Float materialUsage;
	private String unitMaterialUsageCd;
	private BigDecimal voc;
	private BigDecimal haps;
	
	// fpEU data for validation
	private String fpEuUnitType;

	public NSRApplicationEUTypeSEB() {
		super();
	}

	public NSRApplicationEUTypeSEB(NSRApplicationEUTypeSEB old) {
		super(old);
		if (old != null) {
			setMaterialUsage(old.getMaterialUsage());
			setUnitMaterialUsageCd(old.getUnitMaterialUsageCd());
			setVoc(old.getVoc());
			setHaps(old.getHaps());
			
			setFpEuUnitType(old.getFpEuUnitType());
		}
	}

	public Float getMaterialUsage() {
		return materialUsage;
	}

	public void setMaterialUsage(Float materialUsage) {
		this.materialUsage = materialUsage;
	}

	public String getUnitMaterialUsageCd() {
		return unitMaterialUsageCd;
	}

	public void setUnitMaterialUsageCd(String unitMaterialUsageCd) {
		this.unitMaterialUsageCd = unitMaterialUsageCd;
	}

	public BigDecimal getVoc() {
		return voc;
	}

	public void setVoc(BigDecimal voc) {
		this.voc = voc;
	}

	public BigDecimal getHaps() {
		return haps;
	}

	public void setHaps(BigDecimal haps) {
		this.haps = haps;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setMaterialUsage(AbstractDAO.getFloat(rs, "material_usage"));
		setUnitMaterialUsageCd(rs.getString("unit_material_usage_cd"));
		setVoc(rs.getBigDecimal("voc"));
		setHaps(rs.getBigDecimal("haps"));
		
		setFpEuUnitType(null);

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((materialUsage == null) ? 0 : materialUsage.hashCode());
		result = prime
				* result
				+ ((unitMaterialUsageCd == null) ? 0 : unitMaterialUsageCd
						.hashCode());
		result = prime * result + ((voc == null) ? 0 : voc.hashCode());
		result = prime * result + ((haps == null) ? 0 : haps.hashCode());

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
		final NSRApplicationEUTypeSEB other = (NSRApplicationEUTypeSEB) obj;
		if (materialUsage == null) {
			if (other.materialUsage != null)
				return false;
		} else if (!materialUsage.equals(other.materialUsage))
			return false;
		if (unitMaterialUsageCd == null) {
			if (other.unitMaterialUsageCd != null)
				return false;
		} else if (!unitMaterialUsageCd.equals(other.unitMaterialUsageCd))
			return false;
		if (voc == null) {
			if (other.voc != null)
				return false;
		} else if (!voc.equals(other.voc))
			return false;
		if (haps == null) {
			if (other.haps != null)
				return false;
		} else if (!haps.equals(other.haps))
			return false;

		return true;
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
		requiredField(this.materialUsage,
				PAGE_VIEW_ID_PREFIX + "materialUsage", "Material Usage",
				"materialUsage");
		requiredField(this.unitMaterialUsageCd, PAGE_VIEW_ID_PREFIX
				+ "unitMaterialUsageCd", "Unit ", "unitMaterialUsageCd");
		requiredField(this.voc, PAGE_VIEW_ID_PREFIX + "voc", "VOC Content (%)",
				"voc");
		requiredField(this.haps, PAGE_VIEW_ID_PREFIX + "haps",
				"HAPs Content (%)", "haps");

	}

	public void validateRanges() {
		checkRangeValues(this.materialUsage, new Float(0.01), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "materialUsage",
				"Material Usage");
		checkRangeValues(this.voc, new BigDecimal(0), new BigDecimal(100),
				PAGE_VIEW_ID_PREFIX + "voc", "VOC Content (%)");
		checkRangeValues(this.haps, new BigDecimal(0), new BigDecimal(100),
				PAGE_VIEW_ID_PREFIX + "haps", "HAPs Content (%)");
	}
	
	public String getFpEuUnitType() {
		return fpEuUnitType;
	}

	public void setFpEuUnitType(String fpEuUnitType) {
		this.fpEuUnitType = fpEuUnitType;
	}
	
	public void loadFpEuTypeData(EmissionUnitType fpEuType) {
		if (fpEuType != null) {
			this.setFpEuUnitType(fpEuType.getUnitType());
		}
	}

}
