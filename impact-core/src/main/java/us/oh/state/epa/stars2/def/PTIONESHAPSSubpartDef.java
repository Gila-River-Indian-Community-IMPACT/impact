package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;

/*
 * The codes declared in this class must match the content of the
 * PA_NESHAPS_SUBPART_DEF table
 */
public class PTIONESHAPSSubpartDef extends SimpleDef {
    private static final String defName = "PTIONESHAPSSubpartDef";
    private String neshapsSubpartAfsCd;
    private String pollutantCd;
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrieveNehapSubparts", 
                    PTIONESHAPSSubpartDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public void populate(ResultSet rs) {

        super.populate(rs);

        try {
            setNeshapsSubpartAfsCd(rs.getString("neshaps_subpart_afs_cd"));
            setPollutantCd(rs.getString("pollutant_cd"));
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
        }
    }
    
    public static PTIONESHAPSSubpartDef getSubpartDef(String cd)
    throws ApplicationException{
        PTIONESHAPSSubpartDef vc = (PTIONESHAPSSubpartDef)PTIONESHAPSSubpartDef.getData().getItems().getItem(cd);
        return vc;
    }

    public String getNeshapsSubpartAfsCd() {
        return neshapsSubpartAfsCd;
    }

    public void setNeshapsSubpartAfsCd(String neshapsSubpartAfsCd) {
        this.neshapsSubpartAfsCd = neshapsSubpartAfsCd;
    }

    public String getPollutantCd() {
        return pollutantCd;
    }

    public void setPollutantCd(String pollutantCd) {
        this.pollutantCd = pollutantCd;
    }
}
