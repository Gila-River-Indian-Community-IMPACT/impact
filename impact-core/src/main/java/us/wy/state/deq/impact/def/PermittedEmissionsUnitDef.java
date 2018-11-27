package us.wy.state.deq.impact.def;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class PermittedEmissionsUnitDef extends SimpleDef {

	// ****************** Variables *******************
	private static final String defName = "PermittedEmissionsUnitDef";
	private static final String factorName = "PermittedEmissionsUnitDefFactor";
	private static final String tableName = "FP_PERMITTED_EMISSIONS_UNIT_DEF";
	private static final String keyFieldName = "PERMITTED_EMISSIONS_UNIT_CD";
	private static final String valueFieldName = "PERMITTED_EMISSIONS_UNIT_DESC";
	private static final String factorFieldName = "PERMITTED_EMISSIONS_UNIT_FACTOR";
	private static final String deprecatedFieldName = "DEPRECATED";

	private String unitCd;
	private String unitDesc;
	private BigDecimal unitFactor;

	// ****************** Public Static Methods *******************
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB(tableName, keyFieldName, valueFieldName,
					deprecatedFieldName);

			cfgMgr.addDef(defName, data);
		}

		return data;
	}

	public static DefData getFactorData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData factorData = cfgMgr.getDef(factorName);

		if (factorData == null) {
			factorData = new DefData();
			factorData.loadFromDB(tableName, keyFieldName, factorFieldName,
					deprecatedFieldName);

			cfgMgr.addDef(factorName, factorData);
		}

		return factorData;
	}

	public void populate(ResultSet rs) {

		super.populate(rs);

		try {
			setUnitCd(rs.getString("permitted_emissions_unit_cd"));
			setUnitDesc(rs.getString("permitted_emissions_unit_desc"));
			setUnitFactor(rs.getBigDecimal("permitted_emissions_unit_factor"));
		} catch (SQLException sqle) {
			logger.warn("Optional field: " + sqle.getMessage());
		}

	}

	public String getUnitCd() {
		return unitCd;
	}

	public void setUnitCd(String unitCd) {
		this.unitCd = unitCd;
	}

	public String getUnitDesc() {
		return unitDesc;
	}

	public void setUnitDesc(String unitDesc) {
		this.unitDesc = unitDesc;
	}

	public BigDecimal getUnitFactor() {
		return unitFactor;
	}

	public void setUnitFactor(BigDecimal unitFactor) {
		this.unitFactor = unitFactor;
	}
}
