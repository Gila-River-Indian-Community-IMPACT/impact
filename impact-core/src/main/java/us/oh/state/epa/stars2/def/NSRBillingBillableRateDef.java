package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

@SuppressWarnings("serial")
public class NSRBillingBillableRateDef extends SimpleDef{

	private static final String defName = "NSRBillingBillableRateDef";
	
	private Timestamp effectiveDate;
	private Float hourlyRate;
	
	public NSRBillingBillableRateDef() {
		super();
	}

	public Timestamp getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Timestamp effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Float getHourlyRate() {
		return hourlyRate;
	}

	public void setHourlyRate(Float hourlyRate) {
		this.hourlyRate = hourlyRate;
	}

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("NSRBillingSQL.retrieveBillableRateDef", NSRBillingBillableRateDef.class);
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
	
	public void populate(ResultSet rs) {
		super.populate(rs);
		try{
			setCode(rs.getString("code"));
			setDescription(rs.getString("description"));
			setEffectiveDate(rs.getTimestamp("effective_date"));
			setHourlyRate(AbstractDAO.getFloat(rs, "hourly_rate"));
			setDeprecated(AbstractDAO.translateIndicatorToBoolean(rs.getString("deprecated")));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
		}catch(SQLException sqle) {
			logger.error(sqle.getMessage(), sqle);
		}
		
	}
}
