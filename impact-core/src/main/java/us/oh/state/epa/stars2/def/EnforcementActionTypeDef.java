package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EnforcementActionTypeDef extends SimpleDef {
	private boolean hpvCriterionReq;
	private boolean frvTypeReq;

	private static final String defName = "EnforcementActionTypeDef";

	public static final String LOV = "LOV";
	public static final String NOV_Minor = "NOVM";
	public static final String NOV_HPV = "NOV-HPV";
	public static final String NOV_TV_SM = "NOV-TVSM";
	public static final String NO_ACTION = "NA";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB(
					"EnforcementActionSQL.retrieveEnforcementActionAllTypes",
					EnforcementActionTypeDef.class);

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

	public void populate(ResultSet rs) {

		super.populate(rs);

		try {
			setHpvCriterionReq(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("hpv_criterion_req")));
			setFrvTypeReq(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("frv_type_req")));
		} catch (SQLException sqle) {
			logger.warn("Optional field: " + sqle.getMessage());
		}

	}

	public static boolean needHPVCriterion(String cd, Logger logger) {
		EnforcementActionTypeDef bd = (EnforcementActionTypeDef) EnforcementActionTypeDef
				.getData().getItems().getItem(cd);
		if (bd == null) {
			logger.error("HPV Criterion value not found for type cd " + cd);
			return false;
		}
		return bd.hpvCriterionReq;
	}
	
	public static boolean needFRVType(String cd, Logger logger) {
		EnforcementActionTypeDef bd = (EnforcementActionTypeDef) EnforcementActionTypeDef
				.getData().getItems().getItem(cd);
		if (bd == null) {
			logger.error("FRV Type value not found for type cd " + cd);
			return false;
		}
		return bd.frvTypeReq;
	}

	public boolean isHpvCriterionReq() {
		return hpvCriterionReq;
	}

	public void setHpvCriterionReq(boolean hpvCriterionReq) {
		this.hpvCriterionReq = hpvCriterionReq;
	}

	public boolean isFrvTypeReq() {
		return frvTypeReq;
	}

	public void setFrvTypeReq(boolean frvTypeReq) {
		this.frvTypeReq = frvTypeReq;
	}
}
