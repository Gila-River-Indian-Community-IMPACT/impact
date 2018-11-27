package us.oh.state.epa.stars2.def;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountyDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.util.Utility;

public class County {
    private static final String defName = "County";
    private static final String defZipName = "CountyZip";
    private static final String defDoLaaName = "CountyDoLaa";
    private static SoftReference<HashMap<String, CountyDef[]>> srDistrictCounties;
    
    public static final String OUT_OF_STATE_COUNTY = "89";
    
    private static Logger logger = Logger.getLogger(County.class);
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("cm_county_def", "county_cd", "county_nm", null);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    private static DefData getZipData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defZipName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("cm_county_def", "county_cd", "zip5", null);

            cfgMgr.addDef(defZipName, data);
        }
        return data;
    }
    
    private static DefData getDoLaaData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defDoLaaName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("cm_county_def", "county_cd", "do_laa_cd", null);

            cfgMgr.addDef(defDoLaaName, data);
        }
        return data;
    }

    
    public static String getMappingZip(String countyCd) {
    	
    	return getZipData().getItems().getItemDesc(countyCd);
    }
    
	public static List<SelectItem> getDistrictCounties(String districtCd) {

		List<SelectItem> districtCounties = new ArrayList<SelectItem>();
		
		if(Utility.isNullOrEmpty(districtCd) || districtCd.equalsIgnoreCase("ALL")) {
			districtCounties = getData().getItems().getCurrentItems();
		} else {
			List<SelectItem> counties = new ArrayList<SelectItem>();
			List<SelectItem> dolaaCounties = new ArrayList<SelectItem>();
			
			counties = getData().getItems().getCurrentItems();
			dolaaCounties = getDoLaaData().getItems().getCurrentItems();
			// refresh dolaa sub-list if the county list has been updated since the dolaa sub-list
			// is not automatically updated when the county list is updated 
			if(counties.size() != dolaaCounties.size()) {
				getDoLaaData().reload();
				dolaaCounties = getDoLaaData().getItems().getCurrentItems();
			}
			
			for(SelectItem si : dolaaCounties) {
				if(si.getLabel().equalsIgnoreCase(districtCd)) {
					districtCounties.add(new SelectItem(si.getValue(), getData().getItems().getItemDesc(si.getValue())));
				}
			}
		}	
		
		Collections.sort(districtCounties, getSelectItemComparator());
		
		return districtCounties;
	}
	
	public static Comparator<SelectItem> getSelectItemComparator() {
		return new Comparator<SelectItem>() {
			public int compare(SelectItem s1, SelectItem s2) {
				return s1.getLabel().compareToIgnoreCase(s2.getLabel());
			}
		};
	}
}
