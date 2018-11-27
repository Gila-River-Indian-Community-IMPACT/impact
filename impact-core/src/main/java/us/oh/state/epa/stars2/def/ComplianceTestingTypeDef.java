package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ComplianceTestingTypeDef  extends SimpleDef { 
    private static final String defName = "ComplianceTestingTypeDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("ComplianceReportSQL.retrieveTestingReportSubTypes", 
                    ComplianceOtherTypeDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }

    public void populate(ResultSet rs) {
        super.populate(rs);
    }
}
