package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class TVApplicationDocumentTypeDef extends SimpleDef {

	// ********** Variables **********
	private boolean tradeSecretAllowed;
	private String typeExp;

	public static final String PROCESS_FLOW_DIAGRAM = "01";
	public static final String SITE_PLOT_PLAN = "02";
	public static final String SPECIAL_CIRUMSTANCES = "03";
	public static final String CAM_PLAN = "04";
	public static final String ALTERNATE_OPERATING_SCENARIO = "05";
	public static final String COMPLIANCE_PLAN_FORM = "06";
	public static final String DEPRESSURIZATION_EMISSIONS = "07";
	public static final String TIV_ACID_RAIN_APP = "08";
	public static final String AMBIENT_MONITORING_FORM = "09";
	public static final String INSIGNIFICANT_ACTIVITIES_FORM = "10";
	public static final String EU_ALTERNATE_OPERATING_SCENARIO = "11";
	public static final String PTE_CALCULATIONS = "12";
	public static final String PRE_CONTROL_PTE_CALCULATIONS = "13";
	public static final String BASIS_FOR_DETERMINATION = "14";
	public static final String LEGACY_Title_V_APPLICATION = "15";

	private static final long serialVersionUID = -813869494109237527L;
	private static final String defName = "TVApplicationDocumentTypeDef";

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
			data.loadFromDB("ApplicationSQL.retrieveTitleVApplicationDocTypeDef", TVApplicationDocumentTypeDef.class);
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
