package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ComplianceAttachmentTypeDef extends SimpleDef {
	
	public static final String ONE_ATTACHMENT_TYPE_MEMO = "5";
	public static final String CEMS_ATTACHMENT_TYPE_MEMO = "25";
	public static final String OTHER_ATTACHMENT_TYPE_MEMO = "45";
	

	private static Logger logger = Logger.getLogger(ComplianceAttachmentTypeDef.class);
	private static final String defName = "ComplianceAttachmentTypeDef";
    
    private String reportTypeCd;
    private boolean tradeSecretAllowed;

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("ComplianceReportSQL.retrieveComplianceReportTypeAttachments", 
            		ComplianceAttachmentTypeDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }

    public void populate(ResultSet rs) {
        super.populate(rs);
        try {
            setReportTypeCd(rs.getString("report_type_cd"));
            setTradeSecretAllowed(AbstractDAO.translateIndicatorToBoolean(rs.getString("trade_secret_allowed")));
        } catch (SQLException sqle) {
            logger.error("Problem with report_type_cd: " + sqle.getMessage());
        }
    }
    
    public int compareTo(Object o) {
        String dsc = ((ComplianceReportCategoriesTypeDef)o).getDescription();
        return dsc.compareTo(getDescription());
    }

    public String getReportTypeCd() {
        return reportTypeCd;
    }

    public void setReportTypeCd(String reportTypeCd) {
        this.reportTypeCd = reportTypeCd;
    }
    
    public boolean isTradeSecretAllowed() {
		return tradeSecretAllowed;
	}

	public void setTradeSecretAllowed(boolean tradeSecretAllowed) {
		this.tradeSecretAllowed = tradeSecretAllowed;
	}

	public static DefSelectItems getReportyTypeAttachments(String reportTypeCd) {
    	List<BaseDef> bd = new ArrayList<BaseDef>();
    	DefSelectItems reportTypeAttachments = new DefSelectItems();
    	DefSelectItems reportTypeAttachmentsDef = ComplianceAttachmentTypeDef.getData().getItems();
    	
    	if (reportTypeAttachmentsDef != null) {
    		List<BaseDef> categories = new ArrayList<BaseDef>(reportTypeAttachmentsDef.getCompleteItems().values());
    		for(BaseDef category : categories) {
    			if(null != category
    					&& (category instanceof ComplianceAttachmentTypeDef)
    					&& ((ComplianceAttachmentTypeDef)category).getReportTypeCd().equalsIgnoreCase(reportTypeCd)) {
    				bd.add(category);
    			}
    		}
    	}
    	
    	reportTypeAttachments.add(bd.toArray(new BaseDef[0]));
    	
    	return reportTypeAttachments;
    }
}
