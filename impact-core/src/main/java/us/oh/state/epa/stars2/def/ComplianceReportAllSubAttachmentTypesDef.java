package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * This is all the attachment type for all the compliance reports that used to
 * be type other (not TV CC and not PER) but has been expanded into
 * additional types of reports.  The table cr_other_attachment_type_def has
 * all the attachment types for each of these reports and an additional column
 * indicating what type of report it goes with.
 */
public class ComplianceReportAllSubAttachmentTypesDef extends SimpleDef {
    private static final String defName = "ComplianceReportAllSubAttachmentTypesDef";
    private static DefData excludeAQDTypeDefData;
    private boolean tradeSecretAllowed;
    
    // Use this function to check tradeSecretAllowed.
    public final boolean isTradeSecretAllowed() {
        return tradeSecretAllowed;
    }

    public final void setTradeSecretAllowed(boolean tradeSecretAllowed) {
        this.tradeSecretAllowed = tradeSecretAllowed;
    }
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("ComplianceReportSQL.retrieveAllAttachmentTypes", ComplianceReportAllSubAttachmentTypesDef.class);
            cfgMgr.addDef(defName, data);
            
            excludeAQDTypeDefData = new DefData();
            DefSelectItems attachItems = data.getItems();
            String attachCode;
            for (SelectItem selItem:  attachItems.getAllItems()) {
            	attachCode = (String)selItem.getValue();
            	if (!DefData.isDapcAttachmentOnly(attachCode)) {
            		excludeAQDTypeDefData.addItem(attachCode, selItem.getLabel(), selItem.isDisabled());
            	} else {
            		excludeAQDTypeDefData.addItem(attachCode, selItem.getLabel(), true);
            	}
            }
        }
        return data;
    }
    
    public void populate(ResultSet rs) {
        super.populate(rs);
        
        try {
            setTradeSecretAllowed(AbstractDAO.translateIndicatorToBoolean(rs.getString("trade_secret_allowed")));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }
    
    public static DefData getExcludeAQDTypeData() {
    	if (excludeAQDTypeDefData == null) {
    		getData();
    	}
    	return excludeAQDTypeDefData;
    }
}
