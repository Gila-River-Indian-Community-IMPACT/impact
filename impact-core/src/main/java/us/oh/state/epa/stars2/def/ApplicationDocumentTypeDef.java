package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_APPLICATION_DOC_TYPE_DEF table
 */
public class ApplicationDocumentTypeDef extends SimpleDef {

	// ********** Variables **********
	private String typeExp;
	private boolean tradeSecretAllowed;

	public static final String BACT_ANALYSIS = "02";
	public static final String LAER_ANALYSIS = "03";
	public static final String FACILITY_MAP = "04";
	public static final String PROCESS_FLOW_DIAGRAM = "05";
	public static final String MODELING_ANALYSIS = "06";
	public static final String LAND_USE_PLANING = "07";
	public static final String BASIS_FOR_DETERMINATION = "08";
	public static final String EMISSIONS_CALCULATIONS = "09";
	public static final String OTHER_TYPE_OF_DEMONSTRATION = "10";
	public static final String LEGACY_NSR_APPLICATION = "11";
	public static final String HARD_COPY_APPLICATION = "12";
	public static final String COVER_LETTER_PROJECT_DESCRIPTION = "13";
	public static final String EQUIPMENT_LIST = "14";

	private static final String defName = "ApplicationDocumentTypeDef";
	private static final long serialVersionUID = -3225869285824265938L;

	// ****************** Properties *******************
	public String getTypeExp() {
		return this.typeExp;
	}

	public void setTypeExp(String typeExp) {
		this.typeExp = typeExp;
	}

	public boolean isTradeSecretAllowed() {
		return tradeSecretAllowed;
	}

	public void setTradeSecretAllowed(boolean tradeSecretAllowed) {
		this.tradeSecretAllowed = tradeSecretAllowed;
	}

	// ********** Static Methods **********
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("ApplicationSQL.retrieveNSRApplicationDocTypeDef", ApplicationDocumentTypeDef.class);
			cfgMgr.addDef(defName, data);
		}
		return data;
	}

	// ****************** Implement Abstract class - SimpleDef *******************
	@Override
	public void populate(ResultSet rs) {
		super.populate(rs);
		
        try {
    		setTypeExp(rs.getString("application_doc_type_exp"));
    		setTradeSecretAllowed(AbstractDAO.translateIndicatorToBoolean(rs.getString("trade_secret_allowed")));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
	}
}
