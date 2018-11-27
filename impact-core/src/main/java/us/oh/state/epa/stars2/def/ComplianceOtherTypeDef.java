package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ComplianceOtherTypeDef  extends SimpleDef { //implements Comparable{
    private static final String defName = "ComplianceOtherTypeDef";
    
    //private String reportTypeCd;

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("ComplianceReportSQL.retrieveOtherReportSubTypes", 
                    ComplianceOtherTypeDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }

    public void populate(ResultSet rs) {
        super.populate(rs);
//        try {
//            this.setReportTypeCd(rs.getString("report_type_cd"));
//        } catch (SQLException sqle) {
//            logger.error("Problem with report_type_cd: " + sqle.getMessage());
//        }
    }
    
//    public int compareTo(Object o) {
//        String dsc = ((ComplianceOtherTypeDef)o).getDescription();
//        return dsc.compareTo(getDescription());
//    }
//
//    public String getReportTypeCd() {
//        return reportTypeCd;
//    }
//
//    public void setReportTypeCd(String reportTypeCd) {
//        this.reportTypeCd = reportTypeCd;
//    }
}
