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

@SuppressWarnings("serial")
public class ComplianceReportCategoriesTypeDef extends SimpleDef {

	private static Logger logger = Logger.getLogger(ComplianceReportCategoriesTypeDef.class);
	private static final String defName = "ComplianceReportCategoriesTypeDef";
    
    private String reportTypeCd;
    private Boolean enableBulkSubmission;

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("ComplianceReportSQL.retrieveComplianceReportTypeCategories", 
            		ComplianceReportCategoriesTypeDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }

    public void populate(ResultSet rs) {
        super.populate(rs);
        try {
            this.setReportTypeCd(rs.getString("report_type_cd"));
            this.setEnableBulkSubmission(AbstractDAO.translateIndicatorToBoolean(rs.getString("bulk_enabled")));
        } catch (SQLException sqle) {
            logger.error("Problem with report_type_cd: " + sqle.getMessage());
        }
    }
    
    public int compareTo(Object o) {
        String dsc = ((ComplianceReportCategoriesTypeDef)o).getDescription();
        return dsc.compareTo(getDescription());
    }

    public Boolean getEnableBulkSubmission() {
		return enableBulkSubmission;
	}

	public void setEnableBulkSubmission(Boolean enableBulkSubmission) {
		this.enableBulkSubmission = enableBulkSubmission;
	}

	public String getReportTypeCd() {
        return reportTypeCd;
    }

    public void setReportTypeCd(String reportTypeCd) {
        this.reportTypeCd = reportTypeCd;
    }
    
    public static List<SelectItem>getReportyTypeCategories(String reportTypeCd) {
    	List<SelectItem> reportTypeCatrgories = new ArrayList<SelectItem>();
    	
    	DefSelectItems reportCategoryDef = ComplianceReportCategoriesTypeDef.getData().getItems();
    	if (reportCategoryDef != null) {
    		
    		List<BaseDef> categories = new ArrayList<BaseDef>(reportCategoryDef.getCompleteItems().values());
    		    		
    		for(BaseDef category : categories) {
    			if(null != category
    					&& (category instanceof ComplianceReportCategoriesTypeDef)
    					&& !category.isDeprecated()
    					&& ((ComplianceReportCategoriesTypeDef)category).getReportTypeCd().equalsIgnoreCase(reportTypeCd)) {
    				reportTypeCatrgories.add(new SelectItem(category.getCode(), category.getDescription()));
    			}
    		}
    	}
    	
    	return reportTypeCatrgories;
    }
    
	
	public static boolean isBulkEnabled(String otherTypeCd) {
		DefSelectItems items = ComplianceReportCategoriesTypeDef.getData().getItems();
		ComplianceReportCategoriesTypeDef def = (ComplianceReportCategoriesTypeDef)items.getItem(otherTypeCd);
		if (null != def && def.getEnableBulkSubmission()) {
			return true;
		}
		return false;
	}

}
