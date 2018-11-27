package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class CityDef extends SimpleDef {
	private String countyCd;
	private static final String defName = "CityDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("InfrastructureSQL.retrieveCities", CityDef.class);
			cfgMgr.addDef(defName, data);
		}
		return data;
	}

	public final void populate(ResultSet rs) {
		super.populate(rs);
		try {
			setCountyCd(rs.getString("county_cd"));
		} catch (SQLException sqle) {
			logger.warn("Required field error: " + sqle.getMessage(), sqle);
		}
	}

	public String getCountyCd() {
		return countyCd;
	}

	public void setCountyCd(String countyCd) {
		this.countyCd = countyCd;
	}
	
	public static List<SelectItem> getCountyCities(String countyCd) {

		List<SelectItem> countyCities = new ArrayList<SelectItem>();
		
		if(Utility.isNullOrEmpty(countyCd) || countyCd.equalsIgnoreCase("ALL")) {
			return CityDef.getData().getItems().getCurrentItems();
		}
		
		DefSelectItems citiesDef = CityDef.getData().getItems();
    	if (citiesDef != null) {
    		
    		List<BaseDef> cities = new ArrayList<BaseDef>(citiesDef.getCompleteItems().values());
    		    		
    		for(BaseDef city : cities) {
    			if(null != city
    					&& (city instanceof CityDef)
    					&& ((CityDef)city).getCountyCd().equalsIgnoreCase(countyCd)) {
    					countyCities.add(new SelectItem(city.getCode(), city.getDescription()));
    			}
    		}
    	}
    	
    	Collections.sort(countyCities, getSelectItemComparator());
		return countyCities;

	}
	
	public static Comparator<SelectItem> getSelectItemComparator() {
		return new Comparator<SelectItem>() {
			public int compare(SelectItem s1, SelectItem s2) {
				return s1.getLabel().compareToIgnoreCase(s2.getLabel());
			}
		};
	}
}
