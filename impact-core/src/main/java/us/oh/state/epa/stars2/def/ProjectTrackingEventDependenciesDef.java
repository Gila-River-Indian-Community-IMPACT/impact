package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class ProjectTrackingEventDependenciesDef extends SimpleDef {

	public static final String defName = "ProjectTrackingEventDependenciesDef";

	private String eventTypeCd;
	private String predecessorEventTypeCd;
	
	public String getEventTypeCd() {
		return eventTypeCd;
	}

	public void setEventTypeCd(String eventTypeCd) {
		this.eventTypeCd = eventTypeCd;
	}

	public String getPredecessorEventTypeCd() {
		return predecessorEventTypeCd;
	}

	public void setPredecessorEventTypeCd(String predecessorEventTypeCd) {
		this.predecessorEventTypeCd = predecessorEventTypeCd;
	}
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);
		
		if (data == null) {
			data = new DefData();
			data.loadFromDB(
					"InfrastructureSQL.retrieveProjectTrackingEventDependencies",
					ProjectTrackingEventDependenciesDef.class);
			
			cfgMgr.addDef(defName, data);
		}
		return data;
	}

	public void populate(ResultSet rs) {
		super.populate(rs);
		try {
			setEventTypeCd(rs.getString("event_type_cd"));
			setPredecessorEventTypeCd(rs
					.getString("predecessor_event_type_cd"));
		} catch (SQLException sqle) {
			logger.error("Required field: " + sqle.getMessage());
		}
	}

	
	/**
	 * Returns a list of active items in the tracking event dependencies definition list
	 */
	public static List<ProjectTrackingEventDependenciesDef> getDefListItems() {
		List<ProjectTrackingEventDependenciesDef> defListItems
						= new ArrayList<ProjectTrackingEventDependenciesDef>();

		Collection<BaseDef> baseDefCollection 
						= getData().getItems().getCompleteItems().values();
		
		for(BaseDef bd : baseDefCollection) {
			if(!bd.isDeprecated()) {
				defListItems.add((ProjectTrackingEventDependenciesDef)bd);
			}
		}
		
		return defListItems;
	}
	
	/**
	 * Returns a map of event code and the list of the corresponding predecessor events
	 * 
	 */
	public static HashMap<String, List<String>> getPredecessorEventsMap() {
		HashMap<String, List<String>> predecessorEventsMap = new HashMap<String, List<String>>();
		
		List<ProjectTrackingEventDependenciesDef> defList = getDefListItems();
		
		for(ProjectTrackingEventDependenciesDef i: defList) {
			if(!i.isDeprecated()) {
				List<String> predecessorEventCdList = new ArrayList<String>();
				for(ProjectTrackingEventDependenciesDef j: defList) {
					if(i.getEventTypeCd().equals(j.getEventTypeCd())
							&& !j.isDeprecated()) {
						predecessorEventCdList.add(j.getPredecessorEventTypeCd());
					}
				}
				predecessorEventsMap.put(i.getEventTypeCd(), predecessorEventCdList);
			}
		}
		
		return predecessorEventsMap;
	}
	
	/**
	 * Returns a list of the predecessor events for a given event type
	 * 
	 */
	public static List<String> getPredecessorEvents(String eventTypeCd) {
		List<String> predecessorEventCdList = new ArrayList<String>();
		if(!Utility.isNullOrEmpty(eventTypeCd)) {
			HashMap<String, List<String>> predecessorEventsMap = getPredecessorEventsMap();
			predecessorEventCdList = predecessorEventsMap.get(eventTypeCd);
		}
		return predecessorEventCdList;
	}
	
	/**
	 * Returns a list of dependent events for a given event type 
	 * 	 
	*/
	public static List<String> getDependentEvents(String eventTypeCd) {

		List<String> dependentEventCds = new ArrayList<String>();
		HashMap<String, List<String>> predecessorEventsMap = getPredecessorEventsMap();
		if (!predecessorEventsMap.isEmpty()) {
		
			// create a list of events that depend on this event
			// i.e., events for which this event is a predecessor event

			Iterator<String>iter = predecessorEventsMap.keySet().iterator();
			while (iter.hasNext()) {
				String event = iter.next();
				List<String> predecessorEvents = predecessorEventsMap
						.get(event);
				if (null != predecessorEvents
						&& predecessorEvents.contains(eventTypeCd)) {
					dependentEventCds.add(event);
				}
			}
		}
		return dependentEventCds;
	}
	
}
