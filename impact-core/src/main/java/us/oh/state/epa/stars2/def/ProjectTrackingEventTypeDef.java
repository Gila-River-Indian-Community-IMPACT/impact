package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class ProjectTrackingEventTypeDef extends SimpleDef {
	
	public static final String defName = "ProjectTrackingEventTypeDef";
	
	public static final String NEPA = "PTYPE10";
	public static final String GRANTS = "PTYPE20";
	public static final String LETTERS = "PTYPE30";
	
	private String projectTypeCd;
	private boolean allowMultiple;
	private Integer sortOrder;
	
	public String getProjectTypeCd() {
		return projectTypeCd;
	}

	public void setProjectTypeCd(String projectTypeCd) {
		this.projectTypeCd = projectTypeCd;
	}
	
	public boolean isAllowMultiple() {
		return allowMultiple;
	}

	public void setAllowMultiple(boolean allowMultiple) {
		this.allowMultiple = allowMultiple;
	}
	
	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public void populate(ResultSet rs) {
		super.populate(rs);
		try {
			setProjectTypeCd(rs.getString("project_type_cd"));
			setAllowMultiple(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("allow_multiple")));
			setSortOrder(AbstractDAO.getInteger(rs, "sort_order"));
		}catch(SQLException sqle) {
			logger.error("Required field error");
		}
	}

	public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrieveProjectTrackingEventTypes", 
            		ProjectTrackingEventTypeDef.class);
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    /**
	 * Returns items in the project tracking event type definition list
	 */
	public static List<ProjectTrackingEventTypeDef> getDefListItems() {
		
		List<ProjectTrackingEventTypeDef> defListItems
						= new ArrayList<ProjectTrackingEventTypeDef>();
			
		Collection<BaseDef> baseDefCollection 
						= getData().getItems().getCompleteItems().values();
		
		for(BaseDef bd : baseDefCollection) {
			defListItems.add((ProjectTrackingEventTypeDef)bd);
		}
		
		Collections.sort(defListItems,
				new Comparator<ProjectTrackingEventTypeDef>() {
					public int compare(ProjectTrackingEventTypeDef e1,
							ProjectTrackingEventTypeDef e2) {
						return e1.getSortOrder().compareTo(e2.getSortOrder());
					}
				});
		
		return defListItems;
	}
	
	/**
	 * Returns type specific items in the project tracking event type definition
	 * list
	 */
	public static List<ProjectTrackingEventTypeDef> getProjectTypeDefListItems(
			String projectTypeCd) {

		List<ProjectTrackingEventTypeDef> defListItems 
						= new ArrayList<ProjectTrackingEventTypeDef>();

		if (!Utility.isNullOrEmpty(projectTypeCd)) {
			for (ProjectTrackingEventTypeDef def : getDefListItems()) {
				if (def.getProjectTypeCd().equals(projectTypeCd)) {
					defListItems.add(def);
				}
			}
		}

		return defListItems;
	}
	
	/**
	 * Returns a list of active items in the project tracking event type definition list
	 * for a specific project type
	 */
	public static List<SelectItem> getProjectTrackingEvents(String projectTypeCd) {
		List<SelectItem> trackingEvents = new ArrayList<SelectItem>();

		if(!Utility.isNullOrEmpty(projectTypeCd)) {
			Collection<BaseDef> baseDefCollection = getData().getItems()
					.getCompleteItems().values();
			for (BaseDef bd : baseDefCollection) {
				ProjectTrackingEventTypeDef def = (ProjectTrackingEventTypeDef) bd;
				if (!def.isDeprecated()
						&& def.getProjectTypeCd().equals(projectTypeCd)) {
					SelectItem si = new SelectItem(def.getCode(),
							def.getDescription(), null, def.isDeprecated());
					trackingEvents.add(si);
				}
			}
		}
		
		return trackingEvents;
	}
	
	/** 
	 * Returns def. list items for a specific project type
	 */
	public static DefSelectItems getProjectTypeTrackingEventDefs(
			String projectTypeCd) {
		DefSelectItems ret = new DefSelectItems();
		List<BaseDef> defList = new ArrayList<BaseDef>();

		if(!Utility.isNullOrEmpty(projectTypeCd)) {
			List<ProjectTrackingEventTypeDef> trackingEventDefList = 
						getProjectTypeDefListItems(projectTypeCd);
			
			for (ProjectTrackingEventTypeDef def : trackingEventDefList) {
				if (def.getProjectTypeCd().equals(projectTypeCd)) {
					// if the def. list item is deprecated, remove the word
					// "inactive" from the description so that it does not get
					// added again when the def. list item is added to
					// DefSelectItems
					if (def.isDeprecated()) {
						def.setDescription(def.getDescription().replace(
								DefSelectItems.INACTIVE, ""));
					}
					defList.add(def);
				}
			}
	
			ret.add(defList.toArray(new BaseDef[0]));
		}

		return ret;
	}
	
	public static boolean getAllowMultiple(String projectTrackingEventTypeCd) {
		boolean ret = false;
		for (ProjectTrackingEventTypeDef def : getDefListItems()) {
			if (def.getCode().equals(projectTrackingEventTypeCd)
					&& def.isAllowMultiple()) {
				ret = true;
				break;
			}
		}

		return ret;

	}
}
