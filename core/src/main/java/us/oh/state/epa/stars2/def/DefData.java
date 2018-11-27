package us.oh.state.epa.stars2.def;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.CoreService;

public class DefData {
	
	private static final long REFRESH_INTERVAL = 60 * 60 * 1000; // 1 hour
	private DefSelectItems selectItems;
	private boolean loadedFromDB;
	private long refreshTime;
	private long lastRefreshTime;
	private String sql;
	private Class<? extends BaseDB> dataClass;
	private String dbTable;
	private String dbCodeColumn;
	private String dbDescColumn;
	private String dbDeprecatedColumn;
	private String dbTemplateType;
	private String dbTemplatePath;
	private String sortByColumn;
	private boolean idDef;
	private List<Object> excludedKeys = new ArrayList<Object>();
	private transient Logger logger;
	private boolean refresh;

	ApplicationContext appContext = App.getApplicationContext();
	
	// decouple workflow
//	private InfrastructureService svc = 
//	(InfrastructureService)appContext.getBean(InfrastructureService.class);
	private CoreService svc = appContext.getBean(CoreService.class);

	public DefData() {
		logger = Logger.getLogger(DefData.class);
		this.setRefresh(false);
	}

	public DefData(boolean idDef) {
		logger = Logger.getLogger(DefData.class);
		this.idDef = idDef;
		this.setRefresh(false);
	}

	public void loadFromDB(String sql, Class<? extends BaseDB> dataClass) {
		if (sql != null) {
			this.sql = sql;
			this.dataClass = dataClass;
			refreshTime = 0; // to force refresh first time it's accessed
			loadedFromDB = true;
		}
	}

	public void loadFromDB(String inDbTable, String inDbCodeColumn,
			String inDbDescColumn, String inDbDeprecatedColumn, String inDbTemplateType, String inDbTemplatePath) {
		this.dbTable = inDbTable;
		this.dbCodeColumn = inDbCodeColumn;
		this.dbDescColumn = inDbDescColumn;
		this.dbDeprecatedColumn = inDbDeprecatedColumn;
		this.dbTemplateType = inDbTemplateType;
		this.dbTemplatePath = inDbTemplatePath;
		refreshTime = 0; // to force refresh first time it's accessed
		loadedFromDB = true;
	}

	public void loadFromDB(String inDbTable, String inDbCodeColumn,
			String inDbDescColumn, String inDbDeprecatedColumn,
			String inSortByColumn) {
		this.dbTable = inDbTable;
		this.dbCodeColumn = inDbCodeColumn;
		this.dbDescColumn = inDbDescColumn;
		this.dbDeprecatedColumn = inDbDeprecatedColumn;
		this.sortByColumn = inSortByColumn;
		refreshTime = 0; // to force refresh first time it's accessed
		loadedFromDB = true;
	}
	
	public void loadFromDB(String inDbTable, String inDbCodeColumn,
			String inDbDescColumn, String inDbDeprecatedColumn
			) {
		this.dbTable = inDbTable;
		this.dbCodeColumn = inDbCodeColumn;
		this.dbDescColumn = inDbDescColumn;
		this.dbDeprecatedColumn = inDbDeprecatedColumn;
		refreshTime = 0; // to force refresh first time it's accessed
		loadedFromDB = true;
	}

	public void addItem(String code, String desc) {
		if (selectItems == null) {
			selectItems = new DefSelectItems();
			selectItems.addExcludedKeys(excludedKeys);
		}
		selectItems.add(code, desc, false);
		loadedFromDB = false;
	}

	public void addItem(String code, String desc, boolean depricate) {
		if (selectItems == null) {
			selectItems = new DefSelectItems();
			selectItems.addExcludedKeys(excludedKeys);
		}
		selectItems.add(code, desc, depricate);
		loadedFromDB = false;
	}

	public DefSelectItems getItems() {
		refresh();
		return selectItems;
	}

	public BaseDef getItem(String code) {
		refresh();
		return selectItems.getItem(code);
	}

	public void addExcludedKey(Object excludedKey) {
		excludedKeys.add(excludedKey);
	}

	public void reload() {
		refreshTime = 0;
		this.refresh = true;
		refresh();
	}

	// decouple workflow
	private void refresh() {
		// re-query the database if refresh timeout occurs or if there
		// are currently no items in selectItems
		/*
		 */
		if (loadedFromDB
				&& (selectItems == null
						|| selectItems.getAllItems().size() == 0 || System
						.currentTimeMillis() > refreshTime)) {

			try {
				selectItems = new DefSelectItems();
				selectItems.addExcludedKeys(excludedKeys);

				if ((sql != null) && (dataClass != null)) {
					if (idDef) {
						SimpleIdDef[] defs = svc.retrieveBaseIdDB(sql,
								dataClass);
						selectItems.add(defs);
						logger.debug(dataClass.getName() + " updated with "
								+ defs.length + " rows of data");
					} else {
						BaseDef[] defs = svc.retrieveBaseDB(sql, dataClass);
						selectItems.add(defs);
						logger.debug(dataClass.getName() + " updated with "
								+ defs.length + " rows of data");
					}
				} else {
					if (idDef) {
						SimpleIdDef[] defs = svc.retrieveSimpleIdDefs(dbTable,
								dbCodeColumn, dbDescColumn, dbDeprecatedColumn,
								sortByColumn);
						selectItems.add(defs);
						logger.debug("Internal cache for " + dbTable
								+ " updated with " + defs.length
								+ " rows of data");
					} else {
						SimpleDef[] defs = svc.retrieveSimpleDefs(dbTable,
								dbCodeColumn, dbDescColumn, dbDeprecatedColumn,
								sortByColumn);
						selectItems.add(defs);
						logger.debug("Internal cache for " + dbTable
								+ " updated with " + defs.length
								+ " rows of data");
					}
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}
			lastRefreshTime = System.currentTimeMillis();
			refreshTime = System.currentTimeMillis() + REFRESH_INTERVAL;
		}
		 /*
		  *
		  */

	}

	public long getRefreshTime() {
		return refreshTime;
	}

	public final long getLastRefreshTime() {
		return lastRefreshTime;
	}

	public boolean isRefresh() {
		logger.debug("Internal cache out of sync: "
				+ (System.currentTimeMillis() > refreshTime));
		return refresh || (System.currentTimeMillis() > refreshTime);
	}

	public void setRefresh(boolean refresh) {
		lastRefreshTime = System.currentTimeMillis();
		refreshTime = System.currentTimeMillis() + REFRESH_INTERVAL;

		this.refresh = refresh;
	}
	
    public final static boolean isDapcAttachmentOnly(String attachCd) {
    	// These attachments are not to be zipped or copied upon cloning.
    	// It is OK to display them to the user.
    	// They cannot originate from eBiz.
    	if(attachCd != null && attachCd.length() > 0 &&
    			"x".equalsIgnoreCase(attachCd.substring(0, 1))) return true;
    	return false;
    }
}
