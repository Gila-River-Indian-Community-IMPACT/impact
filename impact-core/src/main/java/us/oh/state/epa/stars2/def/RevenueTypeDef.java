package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

@SuppressWarnings("serial")
public class RevenueTypeDef extends SimpleDef {
    public static final String PTI_FEE = "PTIDA";
//  data items (besides code, desc and deprecated)
    private String emissionsRptCd;
    private Integer year;
    private static final String defName = "RevenueType";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InvoiceSQL.retrieveRevenueTypes", 
                    RevenueTypeDef.class);

            cfgMgr.addDef(defName, data);
            
        }
        return data;
    }

    public void populate(ResultSet rs) {

        super.populate(rs);

        try {
            setEmissionsRptCd(rs.getString("emissions_rpt_cd"));
            setYear(AbstractDAO.getInteger(rs, "year"));
        } catch (SQLException sqle) {
            logger.warn("Optional field: " + sqle.getMessage());
        }

    }

    public static String getReportCategory(String revenueTypeCode){
    	RevenueTypeDef emCode = (RevenueTypeDef)RevenueTypeDef.getData().getItems().getItem(revenueTypeCode);
    	
    	
    	return emCode.getEmissionsRptCd();    	
    }
    public String getEmissionsRptCd() {
        return emissionsRptCd;
    }

    public void setEmissionsRptCd(String emissionsRptCd) {
        this.emissionsRptCd = emissionsRptCd;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
