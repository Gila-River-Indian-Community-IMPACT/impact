package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

@SuppressWarnings("serial")
public class EnforcementAttachmentTypeDef extends SimpleDef {
    private static final String defName = "EnforcementAttachmentTypeDef";
    
    private boolean enfAttachAcpFlag;

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            // data.loadFromDB("CE_ENF_ATTACHMENT_TYPE_DEF", "enf_attachment_type_cd", 
            		//"enf_attachment_type_dsc", "deprecated");
            data.loadFromDB("EnforcementActionSQL.retrieveEnfAttachmentTypes", EnforcementAttachmentTypeDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public void populate(ResultSet rs) {

        super.populate(rs);
        
        try {
        	setEnfAttachAcpFlag(AbstractDAO.translateIndicatorToBoolean(rs.getString("ENF_ATTACHMENT_ACP_FLAG")));
        } catch (SQLException sqle) {
            logger.warn("Optional field: " + sqle.getMessage());
        }
    }
    
    public final boolean isEnfAttachAcpFlag() {
		return enfAttachAcpFlag;
	}

	public final void setEnfAttachAcpFlag(boolean enfAttachAcpFlag) {
		this.enfAttachAcpFlag = enfAttachAcpFlag;
	}

	// Attorney Clinet Privileged
    public static boolean isACPType(String cd) {
    	EnforcementAttachmentTypeDef type = (EnforcementAttachmentTypeDef)EnforcementAttachmentTypeDef.getData().getItems().getItem(cd);
    	return type.isEnfAttachAcpFlag();
    }
}
