package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class FireVariableNamesDef extends SimpleDef{
    private static final String defName = "FireVariableNamesDef";
    private String minVal;
    private String maxVal;
    private boolean required;
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        if (data == null) {
            data = new DefData();
            data.loadFromDB("EmissionsReportSQL.retrieveFireVariables", 
                    FireVariableNamesDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public void populate(ResultSet rs) {

        super.populate(rs);

        try {
            setMinVal(rs.getString("min_val"));
            setMaxVal(rs.getString("max_val"));
            setRequired(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("required")));
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
        }
    }

    public String getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(String maxVal) {
        this.maxVal = maxVal;
    }

    public String getMinVal() {
        return minVal;
    }

    public void setMinVal(String minVal) {
        this.minVal = minVal;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
    
	public static Object getFireVariableName(String itemDesc) {
		Object ret = null;
		if(!Utility.isNullOrEmpty(itemDesc)) {
			ret = getData().getItems().getItemDesc(itemDesc);
		}
		
		return ret;
	}
}
