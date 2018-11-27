package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class ProjectTypeDef extends SimpleDef {

	private static final String defName = "ProjectTypeDef";
	
	public static final String NEPA = "PTYPE10";
	public static final String GRANTS = "PTYPE20";
	public static final String LETTERS = "PTYPE30";
	public static final String CONTRACTS = "PTYPE40";
	
	private Integer  securityGroupId;
	
	public Integer getSecurityGroupId() {
		return securityGroupId;
	}

	public void setSecurityGroupId(Integer securityGroupId) {
		this.securityGroupId = securityGroupId;
	}
	
	public void populate(ResultSet rs) {
		super.populate(rs);
		try {
			if (null != rs) {
				setSecurityGroupId(AbstractDAO.getInteger(rs,
						"security_group_id"));
			}
		} catch (SQLException sqle) {
			logger.error("Required field error", sqle);
		}
	}

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("InfrastructureSQL.retrieveProjectTypes", 
					ProjectTypeDef.class);

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
	
	/**
	 * Returns items in the project tracking event type definition list
	 */
	public static List<ProjectTypeDef> getDefListItems() {

		List<ProjectTypeDef> defListItems = new ArrayList<ProjectTypeDef>();

		Collection<BaseDef> baseDefCollection = getData().getItems()
				.getCompleteItems().values();

		for (BaseDef bd : baseDefCollection) {
			defListItems.add((ProjectTypeDef) bd);
		}

		return defListItems;
	}
	
	/**
	 * Returns the id of the user group (role) associated with the given project type
	 * 
	 */
	public static Integer getProjectTypeSecurityGroupId(String projectTypeCd) {
		Integer ret = null;
		if (!Utility.isNullOrEmpty(projectTypeCd)) {
			for (ProjectTypeDef def : getDefListItems()) {
				if (def.getCode().equals(projectTypeCd)) {
					ret = def.getSecurityGroupId();
					break;
				}
			}
		}

		return ret;
	}
	
	public static String getProjectTypeDescription(String projectTypeCd) {
		return getData().getItems().getDescFromAllItem(projectTypeCd);
	}
}
