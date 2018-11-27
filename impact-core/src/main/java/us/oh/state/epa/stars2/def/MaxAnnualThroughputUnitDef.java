package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.util.Utility;

/*
 * The codes declared in this class must match the content of the
 * CM_MAX_ANNUAL_THROUGHPUT_UNIT_DEF table
 */

public class MaxAnnualThroughputUnitDef extends SimpleDef { 

	private static final long serialVersionUID = 6587849735087360960L;
	private static Logger logger = Logger.getLogger(MaxAnnualThroughputUnitDef.class);
	
	private static final String defName = "MaxAnnualThroughputUnitDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("InfrastructureSQL.retrieveMaxAnnualThroughputUnits", MaxAnnualThroughputUnitDef.class);
			cfgMgr.addDef(defName, data);
		}
		return data;
	}	
    
    
	public final void populate(ResultSet rs) {
		super.populate(rs);
	}

}
