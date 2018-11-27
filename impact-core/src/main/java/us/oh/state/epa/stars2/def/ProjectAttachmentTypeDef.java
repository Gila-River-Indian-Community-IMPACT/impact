package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class ProjectAttachmentTypeDef extends SimpleDef {
private static final String defName = "ProjectAttachmentTypeDef";
	
	private String projectTypeCd;
	
	public String getProjectTypeCd() {
		return projectTypeCd;
	}

	public void setProjectTypeCd(String projectTypeCd) {
		this.projectTypeCd = projectTypeCd;
	}

	public void populate(ResultSet rs) {
		super.populate(rs);
		try {
			setProjectTypeCd(rs.getString("project_type_cd"));
		}catch(SQLException sqle) {
			logger.error("Required field error");
		}
	}

	public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrieveProjectAttachementTypes", 
            		ProjectAttachmentTypeDef.class);
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    /**
	 * Returns a list of project type specific items in the project attachment type definition list
	 */
	private static List<ProjectAttachmentTypeDef> getDefListItems(String projectTypeCd) {
		
		List<ProjectAttachmentTypeDef> defListItems
						= new ArrayList<ProjectAttachmentTypeDef>();
			
		Collection<BaseDef> baseDefCollection 
						= getData().getItems().getCompleteItems().values();
		
		for(BaseDef bd : baseDefCollection) {
			if (((ProjectAttachmentTypeDef) bd).getProjectTypeCd().equals(
					projectTypeCd)) {
				defListItems.add((ProjectAttachmentTypeDef)bd);
			}
		}
		
		Collections.sort(defListItems, 
				new Comparator<ProjectAttachmentTypeDef>() {
			@Override
			public int compare(ProjectAttachmentTypeDef p1, ProjectAttachmentTypeDef p2) {
				return p1.getDescription().compareToIgnoreCase(p2.getDescription());
			}
		});

		return defListItems;
	}
	
	/**
	 * Returns def. list items for a specific project type
	 */
	public static DefSelectItems getProjectTypeAttachmentDefs(
			String projectTypeCd) {
		DefSelectItems ret = new DefSelectItems();
		List<BaseDef> defList = new ArrayList<BaseDef>();

		if (!Utility.isNullOrEmpty(projectTypeCd)) {
			for (ProjectAttachmentTypeDef def : getDefListItems(projectTypeCd)) {
				defList.add(def);
			}
			
			ret.add(defList.toArray(new BaseDef[0]));
		}

		return ret;
	}
}
