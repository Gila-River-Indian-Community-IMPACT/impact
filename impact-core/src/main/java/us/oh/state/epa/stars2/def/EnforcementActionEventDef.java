package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EnforcementActionEventDef extends SimpleDef {
	private boolean allowMultiple;
	
	public static final String NOV_ISSUED = "003"; // NOV Issued
	public static final String DAY_ZERO = "004"; // Day Zero
	public static final String REFERRED_TO_AG = "027"; // Referred to AG
	public static final String FINAL_SETTLEMENT_AGREEMENT = "020"; // Final Settlement Agreement
	public static final String CHECK_RECEIVED = "023"; // Check Received
	public static final String NOV_CLOSED = "025"; // NOV Closed
	

	private static final String defName = "EnforcementActionEventDef";


	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB(
					"EnforcementActionSQL.retrieveEnforcementActionAllEvents",
					EnforcementActionEventDef.class);

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

	public void populate(ResultSet rs) {

		super.populate(rs);

		try {
			setAllowMultiple(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("allow_multiple")));
		} catch (SQLException sqle) {
			logger.warn("Optional field: " + sqle.getMessage());
		}

	}

	public static boolean multiplesAllowed(String cd, Logger logger) {
		EnforcementActionEventDef bd = (EnforcementActionEventDef) EnforcementActionEventDef
				.getData().getItems().getItem(cd);
		if (bd == null) {
			logger.error("Allow multiple: " + cd);
			return false;
		}
		return bd.allowMultiple;
	}

	public boolean isAllowMultiple() {
		return allowMultiple;
	}

	public void setAllowMultiple(boolean allowMultiple) {
		this.allowMultiple = allowMultiple;
	}
	
	public static boolean isDeprecated(String cd, Logger logger) {
		EnforcementActionEventDef bd = (EnforcementActionEventDef) EnforcementActionEventDef
				.getData().getItems().getItem(cd);
		if (bd == null) {
			logger.error("Deprecated: " + cd);
			return false;
		}
		return bd.isDeprecated();
	}
}
