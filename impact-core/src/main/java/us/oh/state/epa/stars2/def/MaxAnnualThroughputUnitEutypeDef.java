package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

/*
 * The codes declared in this class must match the content of the
 * CM_MAX_ANNUAL_THROUGHPUT_UNIT_DEF table
 */

public class MaxAnnualThroughputUnitEutypeDef extends SimpleDef { 

	private static final long serialVersionUID = 3245783405200128504L;
	
	private static Logger logger = Logger.getLogger(MaxAnnualThroughputUnitEutypeDef.class);
	
	public static final String DIS_CD = "DIS";
	public static final String TAR_CD= 	"TAR";
	public static final String PAM_CD = "PAM";
	public static final String MAT_CD = "MAT";
	public static final String MET_CD = "MET";
	public static final String MIX_CD = "MIX";
	public static final String MLD_CD = "MLD";
	public static final String PEL_CD = "PEL";
	public static final String PRN_CD = "PRN";
	public static final String SVU_CD = "SVU";
	public static final String OEP_CD = "OEP";
	
	public static final String INACTIVE = "(inactive)";
	
	private static final String defName = "MaxAnnualThroughputUnitEutypeDef";
	
	private String euTypeCd;

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("InfrastructureSQL.retrieveMaxAnnualThroughputUnitsEutype", MaxAnnualThroughputUnitEutypeDef.class);
			cfgMgr.addDef(defName, data);
		}
		return data;
	}	
    
    
	public final void populate(ResultSet rs) {
		super.populate(rs);
		try {
			setEuTypeCd(rs.getString("eu_type_cd"));
			setDeprecated(AbstractDAO.translateIndicatorToBoolean(rs.getString("deprecated")));
		} catch (SQLException sqle) {
			logger.warn("Unable to retrieve from DB: " + sqle.getMessage(), sqle);
		}
	}

	public static DefSelectItems getMaxAnnualThroughputUnit(String euTypeCd) {

		if (Utility.isNullOrEmpty(euTypeCd) || euTypeCd.equalsIgnoreCase("ALL")) {
			return MaxAnnualThroughputUnitDef.getData().getItems();
		}

		DefSelectItems maxAnnualThroughputUnitEuTypeDef = MaxAnnualThroughputUnitEutypeDef.getData().getItems();

		DefSelectItems euTypeMaxThroughputUnitdef = new DefSelectItems();

		if (maxAnnualThroughputUnitEuTypeDef != null) {

			List<BaseDef> units = new ArrayList<BaseDef>(maxAnnualThroughputUnitEuTypeDef.getCompleteItems().values());

			for (BaseDef unit : units) {

				if (null != unit && (unit instanceof MaxAnnualThroughputUnitEutypeDef)
						&& ((MaxAnnualThroughputUnitEutypeDef) unit).getEuTypeCd().equalsIgnoreCase(euTypeCd)) {
					
					// description in the max. annual throughput units eu type def. list 
					// maps to a code in the max. annual throughput unit type def. list.
					
					boolean deprecated = false;
					
					String code = null;
					if (unit.isDeprecated()) {
						// remove the word inactive from the description in order to map it
						// to a code in the max. annual throughput unit type def. list
						code = unit.getDescription().replace(INACTIVE, "");
						deprecated = true;
					} else {
						code = unit.getDescription();
					}
					
					String desc = MaxAnnualThroughputUnitDef.getData().getItem(code).getDescription();
					
					// if the def. list item in max. annual throughput unit type def. is
					// not deprecated but the mapped def. list item in the max. annual throughput
					// eu type def. list is deprecated, then append the inactive string to
					// the description
					if (MaxAnnualThroughputUnitDef.getData().getItem(code).isDeprecated()) {
						deprecated = true;
					} else {
						if (deprecated) {
							desc = desc + INACTIVE;
						}
					}
					
					euTypeMaxThroughputUnitdef.add(code, desc, deprecated);
				}
			}
		}

		return euTypeMaxThroughputUnitdef;
	}

	public String getEuTypeCd() {
		return euTypeCd;
	}


	public void setEuTypeCd(String euTypeCd) {
		this.euTypeCd = euTypeCd;
	}

}
