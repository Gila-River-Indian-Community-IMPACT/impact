package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

@SuppressWarnings("serial")
public class ContentTypeDef extends SimpleDef {
	
	public static final String ANNUAL = "AC";
	public static final String WINTER = "WC";

	public static final String defName = "ContentTypeDef";
	
	private int startDay;
	private int startMonth;
	private int endDay;
	private int endMonth;
	
	public int getStartDay() {
		return startDay;
	}
	
	public void setStartDay(int startDay) {
		this.startDay = startDay;
	}
	
	public int getStartMonth() {
		return startMonth;
	}
	
	public void setStartMonth(int startMonth) {
		this.startMonth = startMonth;
	}
	
	public int getEndDay() {
		return endDay;
	}
	
	public void setEndDay(int endDay) {
		this.endDay = endDay;
	}
	
	public int getEndMonth() {
		return endMonth;
	}
	
	public void setEndMonth(int endMonth) {
		this.endMonth = endMonth;
	}
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB(
					"EmissionsReportSQL.retrieveContentTypes",
					ContentTypeDef.class);
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
	
	public void populate(ResultSet rs) {
		if (null != rs) {
			try {
				super.populate(rs);
				setStartDay(rs.getInt("start_day"));
				setStartMonth(rs.getInt("start_month"));
				setEndDay(rs.getInt("end_day"));
				setEndMonth(rs.getInt("end_month"));
			} catch (SQLException sqle) {
				logger.error("Required field error");
			}
		}
	}
	
	/**
	 * Returns a list of active items in the content type definition list
	 */
	public static List<ContentTypeDef> getDefListItems() {

		List<ContentTypeDef> defListItems = new ArrayList<ContentTypeDef>();

		Collection<BaseDef> baseDefCollection = getData().getItems()
				.getCompleteItems().values();

		for (BaseDef bd : baseDefCollection) {
			if (!bd.isDeprecated()) {
				defListItems.add((ContentTypeDef) bd);
			}
		}

		return defListItems;
	}
	
	/**
	 * Returns a list of active items in the content type definition list
	 */
	public static ContentTypeDef getContentTypeDef(String contentTypeCd) {

		Collection<BaseDef> baseDefCollection = getData().getItems()
				.getCompleteItems().values();

		for (BaseDef bd : baseDefCollection) {
			if (bd.getCode().equals(contentTypeCd)) {
				return ((ContentTypeDef) bd);
			}
		}
		return null;
	}
}
