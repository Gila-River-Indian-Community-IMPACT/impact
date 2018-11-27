package us.wy.state.deq.impact.def;

import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EventTypeDef {
	
	//****************** Variables ******************* 
	private String typeCd;
	private String typeDesc;
	private boolean deprecated;
	private int lastModifed;
	
    private static final String defName = "EventTypeDef";
    private static final String tableName = "CM_EVENT_TYPE_DEF";
    private static final String keyFieldName = "TYPE_CD";
    private static final String valueFieldName = "TYPE_DSC";
    private static final String deprecatedFieldName = "DEPRECATED";

	//****************** Properties *******************
	public String getTypeCd() {
		return typeCd;
	}

	public void setTypeCd(String typeCd) {
		this.typeCd = typeCd;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	public String getypeDesc() {
		return typeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}

	public int getLastModifed() {
		return lastModifed;
	}

	public void setLastModifed(int lastModifed) {
		this.lastModifed = lastModifed;
	}

	//****************** Public Static Methods *******************
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB(tableName,
                    keyFieldName, valueFieldName,
                    deprecatedFieldName);

            cfgMgr.addDef(defName, data);
        }
        return data;
    } 
}
