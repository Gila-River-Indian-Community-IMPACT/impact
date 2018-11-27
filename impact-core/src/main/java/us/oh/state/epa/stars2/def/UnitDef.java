package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

@SuppressWarnings("serial")
public class UnitDef extends SimpleDef {
    private String usEpaUnitCd;
    private static final String defName = "UnitDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("EmissionsReportSQL.retrieveUnitDefs", 
                    UnitDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }

    public void populate(ResultSet rs) {
        super.populate(rs);
        try {
            setUsEpaUnitCd(rs.getString("us_epa_unit_cd"));
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
        }
    }
    
    public static String getUsEpaUnitCd(String cd) {
        // Using the unit cd convert to the code used by the US EPA
        String rtn = null;
        BaseDef bd = UnitDef.getData().getItem(cd);
        if(bd != null) {
            rtn = ((UnitDef)bd).getUsEpaUnitCd();
            if(rtn == null || rtn.length() == 0) {
                rtn = ((UnitDef)(UnitDef.getData().getItem(cd))).getCode();  // use standard code if no special code for US EPA
            }
        }
        return rtn;
    }

    public void setUsEpaUnitCd(String usEpaUnitCd) {
        this.usEpaUnitCd = usEpaUnitCd;
    }

    public String getUsEpaUnitCd() {
        return usEpaUnitCd;
    }
}
