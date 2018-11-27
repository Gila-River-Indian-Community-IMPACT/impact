package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.webcommon.reports.ReportBaseCommon;

/*
 * The codes declared in this class must match the content of the
 * CM_POLLUTANT_DEF table
 */
@SuppressWarnings("serial")
public class MaterialUsedDef extends SimpleDef {
    // data items (besides code, desc and deprecated)
    public static final String SAND_CD = "SAND";
    public static final String SAND_DSC = "Sand";
    public static final String POCE_CD = "POCE";
    public static final String POCE_DSC = "Portland Cement";  
    public static final String AGGR_CD = "AGGR";
    public static final String AGGR_DSC = "Aggregate";
    public static final String FLYA_CD = "FLYA";
    public static final String FLYA_DSC = "Fly Ash";
    public static final String OTHE_CD = "OTHE";
    public static final String OTHE_DSC = "Other";  
    public static final String LIME_CD = "LIME";
    public static final String LIME_DSC = "Lime";
    private static final String defName = "MaterialUsedDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("CM_MATERIAL_USED_DEF", "TYPE_CD", "TYPE_DSC",
					"deprecated");
           cfgMgr.addDef(defName, data);
        }
        return data;
    }

    
   }
