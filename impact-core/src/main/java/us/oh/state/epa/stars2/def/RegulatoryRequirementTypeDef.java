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

@SuppressWarnings("serial")
public class RegulatoryRequirementTypeDef extends SimpleDef {
	
	public static final String defName = "RegulatoryRequirementTypeDef";
	public static final String allowFileImportDefName = "AllowFileImportRegulatoryRequirementTypeDef";
	
	private boolean allowJumpStartDataEntry;
	private boolean reportToUSEPA;
	private Integer priority;
	private boolean allowFileImport;
	
	public boolean isAllowJumpStartDataEntry() {
		return allowJumpStartDataEntry;
	}
	
	public void setAllowJumpStartDataEntry(boolean allowJumpStartDataEntry) {
		this.allowJumpStartDataEntry = allowJumpStartDataEntry;
	}
	
	public boolean isReportToUSEPA() {
		return reportToUSEPA;
	}
	
	public void setReportToUSEPA(boolean reportToUSEPA) {
		this.reportToUSEPA = reportToUSEPA;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);
		
		if (data == null) {
			data = new DefData();
			data.loadFromDB(
					"EmissionsReportSQL.retrieveRegulatoryRequirementTypes",
					RegulatoryRequirementTypeDef.class);
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
	
	public static DefData getAllowFileImportData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(allowFileImportDefName);
		
		if (data == null) {
			data = new DefData();
			data.loadFromDB(
					"EmissionsReportSQL.retrieveAllowFileImportRegulatoryRequirementTypes",
					RegulatoryRequirementTypeDef.class);
			cfgMgr.addDef(allowFileImportDefName, data);
		}
		return data;
	}	
	
	public void populate(ResultSet rs) {
		if (null != rs) {
			try {
				super.populate(rs);
				setAllowJumpStartDataEntry(AbstractDAO
						.translateIndicatorToBoolean(rs
								.getString("allow_jumpstart_data_entry")));
				setReportToUSEPA(AbstractDAO.translateIndicatorToBoolean(rs
						.getString("report_to_usepa")));
				setPriority(AbstractDAO.getInteger(rs, "priority"));
				setAllowFileImport(AbstractDAO
						.translateIndicatorToBoolean(rs
								.getString("allow_file_import")));
			} catch (SQLException sqle) {
				logger.error("Required field error");
			}
		}
	}
	
	private static List<RegulatoryRequirementTypeDef> getDefListItems() {

		List<RegulatoryRequirementTypeDef> defListItems
						= new ArrayList<RegulatoryRequirementTypeDef>();
			
		Collection<BaseDef> baseDefCollection 
						= getData().getItems().getCompleteItems().values();
		
		for(BaseDef bd : baseDefCollection) {
			defListItems.add((RegulatoryRequirementTypeDef)bd);
		}
		
		return defListItems;
	}
	
	public static boolean allowedToJumpstartDataEntry(
			String regulatoryRequirementTypeCd) {
		boolean allowed = false;
		for (RegulatoryRequirementTypeDef def : getDefListItems()) {
			if (def.getCode().equals(regulatoryRequirementTypeCd)) {
				allowed = def.isAllowJumpStartDataEntry();
				break;
			}
		}
		return allowed;
	}
	
	public static boolean reportableToUSEPA(String regulatoryRequirementTypeCd) {
		boolean reportable = false;
		for (RegulatoryRequirementTypeDef def : getDefListItems()) {
			if (def.getCode().equals(regulatoryRequirementTypeCd)) {
				reportable = def.isReportToUSEPA();
				break;
			}
		}
		return reportable;
	}
	
	
	public static boolean isPriority(String regulatoryRequirementTypeCd) {
		Integer value = null;
		for (RegulatoryRequirementTypeDef def : getDefListItems()) {
			if (def.getCode().equals(regulatoryRequirementTypeCd)) {
				value = def.getPriority();
				break;
			}
		}
		
		if (null != value) {
			for (RegulatoryRequirementTypeDef def : getDefListItems()) {
				if (value < def.getPriority()) {
					return false;
				}
			}
		} else {
			return false;
		}
		
		return true;
	}
	
	public static boolean isPriority(String regulatoryRequirementTypeCd, 
			List<String> compareTo) {
		Integer value = null;
		for (RegulatoryRequirementTypeDef def : getDefListItems()) {
			if (def.getCode().equals(regulatoryRequirementTypeCd)) {
				value = def.getPriority();
				break;
			}
		}
		
		if (null != value) {
			Integer comparisonValue = null;
			for (String cd : compareTo) {
				for (RegulatoryRequirementTypeDef def : getDefListItems()) {
					if (def.getCode().equals(cd)) {
						comparisonValue = def.getPriority();
						if (null != comparisonValue && comparisonValue < def.getPriority()) {
							return false;
						}
						break;
					}
				}
			}
		} else {
			return false;
		}
		
		return true;
	}
	
	public static Integer getPriority(String regulatoryRequirementTypeCd) {
		Integer priority = 0;
		for (RegulatoryRequirementTypeDef def : getDefListItems()) {
			if (def.getCode().equals(regulatoryRequirementTypeCd)) {
				priority = def.getPriority();
				break;
			}
		}
		return priority;
	}

	public boolean isAllowFileImport() {
		return allowFileImport;
	}

	public void setAllowFileImport(boolean allowFileImport) {
		this.allowFileImport = allowFileImport;
	}
	
}
