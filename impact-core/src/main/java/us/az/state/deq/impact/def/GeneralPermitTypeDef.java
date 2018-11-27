package us.az.state.deq.impact.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class GeneralPermitTypeDef extends SimpleDef {

	private static final long serialVersionUID = 2669352693642595758L;
	private static final String defName = "GeneralPermitTypeDef";

	private Timestamp issuanceDate;
	private Timestamp expirationDate;
	private String templatePath;

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PermitSQL.retrieveGeneralPermitTypeDef", GeneralPermitTypeDef.class);
			cfgMgr.addDef(defName, data);
		}
		return data;
	}

	@Override
	public void populate(ResultSet rs) {
		super.populate(rs);
		try {
			setIssuanceDate(rs.getTimestamp("ISSUANCE_RENEWAL_DATE"));
			setExpirationDate(rs.getTimestamp("EXPIRATION_DATE"));
			setTemplatePath(rs.getString("TEMPLATE_PATH"));
		} catch (SQLException sqle) {
			logger.error(sqle.getMessage(), sqle);
		}
	}

	public Timestamp getIssuanceDate() {
		return issuanceDate;
	}

	public void setIssuanceDate(Timestamp issuanceDate) {
		this.issuanceDate = issuanceDate;
	}

	public Timestamp getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Timestamp expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

}
