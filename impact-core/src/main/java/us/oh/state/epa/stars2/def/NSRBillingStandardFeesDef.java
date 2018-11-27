package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

@SuppressWarnings("serial")
public class NSRBillingStandardFeesDef extends SimpleDef {
	
	private static final String defName = "NSRBillingStandardFeesDef";
	
	private String invoiceTypeCd;
	private Float fee;
	
	public NSRBillingStandardFeesDef() {
		super();
	}
	
	public String getInvoiceTypeCd() {
		return invoiceTypeCd;
	}

	public void setInvoiceTypeCd(String invoiceTypeCd) {
		this.invoiceTypeCd = invoiceTypeCd;
	}

	public Float getFee() {
		return fee;
	}

	public void setFee(Float fee) {
		this.fee = fee;
	}

	public static DefData getData() {
		
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);
 
		if (data == null) {
			data = new DefData();
			data.loadFromDB("NSRBillingSQL.retrieveStandardFeeDef", NSRBillingStandardFeesDef.class);
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
	
	public void populate(ResultSet rs) {
		super.populate(rs);
		
		try {
			setCode(rs.getString("code"));
			setDescription(rs.getString("description"));
			setInvoiceTypeCd(rs.getString("invoice_type_cd"));
			setFee(AbstractDAO.getFloat(rs, "fee"));
			setDeprecated(AbstractDAO.translateIndicatorToBoolean(rs.getString("deprecated")));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
		}catch(SQLException sqle) {
			logger.error(sqle.getMessage(), sqle);
		}
	}

}
