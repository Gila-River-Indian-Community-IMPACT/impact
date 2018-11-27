package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ExternalLinksSectionTypDef extends SimpleDef{
	
		private static final long serialVersionUID = 1L;

	private static final String defName = "ExternalLinksSectionTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("cm_ext_link_section_def", "ext_link_section_cd",
                            "ext_link_section_desc", "deprecated");
            cfgMgr.addDef(defName, data);
        }
        return data;
    }

}

