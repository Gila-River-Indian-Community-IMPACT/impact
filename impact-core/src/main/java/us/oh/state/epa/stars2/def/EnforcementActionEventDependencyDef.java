package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EnforcementActionEventDependencyDef extends SimpleDef {
	private String predecessorEvent;

	private static final String defName = "EnforcementActionEventDependencyDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB(
					"EnforcementActionSQL.retrieveEnforcementActionAllEventDependencies",
					EnforcementActionEventDependencyDef.class);

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

	public void populate(ResultSet rs) {

		super.populate(rs);

		try {
			setPredecessorEvent((rs
					.getString("enforcement_action_event_pred_cd")));
		} catch (SQLException sqle) {
			logger.warn("Optional field: " + sqle.getMessage());
		}

	}

	public static String getPredecessor(String cd, Logger logger) {
		EnforcementActionEventDependencyDef bd = (EnforcementActionEventDependencyDef) EnforcementActionEventDependencyDef
				.getData().getItems().getItem(cd);
		if (bd == null) {
			logger.error("Predecessor Event: " + cd);
			return null;
		}
		return bd.getPredecessorEvent();
	}

	public String getPredecessorEvent() {
		return predecessorEvent;
	}

	public void setPredecessorEvent(String predecessorEvent) {
		this.predecessorEvent = predecessorEvent;
	}
	
	public static HashMap<String, List<String>> getPredecessorEventsMap(Logger logger) {
		HashMap<String, List<String>> dependentEventMap = new HashMap<String, List<String>>();
		List<SelectItem> dependentEventDefList = new ArrayList<SelectItem>();
		String eventCd = null;
		String predecessorEventCd = null;
		
		dependentEventDefList = EnforcementActionEventDependencyDef.getData().getItems().getCurrentItems();
		for(SelectItem event : dependentEventDefList) {
			eventCd = event.getLabel();
			predecessorEventCd = getPredecessor((String)event.getValue(), logger);
			if(null != dependentEventMap.get(eventCd)) {
				dependentEventMap.get(eventCd).add(predecessorEventCd);
			} else {
				List<String> predecessorEventCds = new ArrayList<String>();
				predecessorEventCds.add(predecessorEventCd);
				dependentEventMap.put(eventCd, predecessorEventCds);
			}
		}
		
		return dependentEventMap;
	}
}
