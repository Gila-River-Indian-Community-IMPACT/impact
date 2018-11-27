package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;

/*
 * The codes declared in this class must match the content of the
 * PA_NSPS_SUBPART_DEF table
 */
public class PTIONSPSSubpartDef extends SimpleDef{
    // data items (besides code, desc, last modified and deprecated)
    private String nspsSubpartAfsCd;
    private static final String defName = "PTIONSPSSubpartDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrieveNspsSubparts", 
                    PTIONSPSSubpartDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public PTIONSPSSubpartDef() {
        super();
    }

    public void populate(ResultSet rs) {
        super.populate(rs);
        try {
            setNspsSubpartAfsCd(rs.getString("nsps_subpart_afs_cd"));
        } catch (SQLException sqle) {
            logger.error("Error populating ValidationControl: " + sqle.getMessage());
        }

    }
    
    public static PTIONSPSSubpartDef getSubpartDef(String cd)
    throws ApplicationException{
        PTIONSPSSubpartDef vc = (PTIONSPSSubpartDef)PTIONSPSSubpartDef.getData().getItems().getItem(cd);
        return vc;
    }

    public String getNspsSubpartAfsCd() {
        return nspsSubpartAfsCd;
    }

    public void setNspsSubpartAfsCd(String nspsSubpartAfsCd) {
        this.nspsSubpartAfsCd = nspsSubpartAfsCd;
    }
}
