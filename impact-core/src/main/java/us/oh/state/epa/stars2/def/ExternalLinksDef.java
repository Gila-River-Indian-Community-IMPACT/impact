package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.util.Utility;

public class ExternalLinksDef extends SimpleDef{
	
	private static final long serialVersionUID = 1L;

	private String extLinkSectionName;	
    private static final String defName = "ExternalLinksDef";
    
    private static final String defDefaultSection = "ExternalLinksDefDefaultSectionDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrieveExternalLinks", ExternalLinksDef.class);
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
	public final void populate(ResultSet rs) {
		super.populate(rs);
		try {
			setExtLinkSectionName(rs.getString("ext_link_section_cd"));
		} catch (SQLException sqle) {
			logger.warn("Unable to retrieve from DB: " + sqle.getMessage(), sqle);
		}
	}
   
	
	
	
	public static  String getExtLinkSectionName(String sectionCd) {    
		String sectionName = null;
		int count=0;
		DefSelectItems extLinksDef = ExternalLinksDef.getData().getItems();
		if (extLinksDef != null) {
			List<BaseDef> extLinks =  new ArrayList<BaseDef>( extLinksDef.getCompleteItems().values());
			if(sectionName==null && count <extLinks.size()){
				for(BaseDef extLink:extLinks){
					if(null != extLink & (extLink instanceof ExternalLinksDef)
							&& ((ExternalLinksDef)extLink).getCode().equalsIgnoreCase(sectionCd)){
						sectionName= ((ExternalLinksDef) extLink).getExtLinkSectionName();
					}
				}
			}

		}
		return sectionName;

	}
	
	
	public static List<SelectItem> getSectionLinks(String sectionCd) {

		List<SelectItem> sectionLinks = new ArrayList<SelectItem>();
		
		if(Utility.isNullOrEmpty(sectionCd) || sectionCd.equalsIgnoreCase("ALL")) {
			return ExternalLinksDef.getData().getItems().getCurrentItems();
		}
		
		DefSelectItems extLinksDef = ExternalLinksDef.getData().getItems();
    	if (extLinksDef != null) {
    		
    		List<BaseDef> extLinks = new ArrayList<BaseDef>(extLinksDef.getCompleteItems().values());
    		    		
    		for(BaseDef extLink : extLinks) {
    			if(null != extLink
    					&& (extLink instanceof ExternalLinksDef)
    					&& ((ExternalLinksDef)extLink).getExtLinkSectionName().equalsIgnoreCase(sectionCd)) {
    				sectionLinks.add(new SelectItem(extLink.getCode(), extLink.getDescription()));
    			}
    		}
    	}
    	
    	Collections.sort(sectionLinks, getSelectItemComparator());
		return sectionLinks;

	}
	
	public static Comparator<SelectItem> getSelectItemComparator() {
		return new Comparator<SelectItem>() {
			public int compare(SelectItem s1, SelectItem s2) {
				return s1.getLabel().compareToIgnoreCase(s2.getLabel());
			}
		};
	}
	
	
	public String getExtLinkSectionName() {
		return extLinkSectionName;
	}

	public void setExtLinkSectionName(String extLinkSectionName) {
		this.extLinkSectionName = extLinkSectionName;
	}
    
    
}
