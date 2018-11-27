package us.wy.state.deq.impact.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

@SuppressWarnings("serial")
public class FugitiveComponentDef extends SimpleDef {
	private Short sortOrder;
	
	//****************** Variables ******************* 
	public static final String CNT = "CNT";
	public static final String FLG = "FLG";
	public static final String OEL = "OEL";
	public static final String OTH = "OTH";
	public static final String PPS = "PPS";
	public static final String VLV = "VLV";	
	
    private static final String defName = "FugitiveComponentDef";
    private static final String tableName = "CM_FUG_COMPONENT_DEF";
    private static final String keyFieldName = "COMPONENT_CD";
    private static final String valueFieldName = "COMPONENT_DSC";
    private static final String deprecatedFieldName = "DEPRECATED";

	//****************** Public Static Methods *******************
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrieveFugComponents",FugitiveComponentDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    } 
    
    public void populate(ResultSet rs) {
        super.populate(rs);
        try {
            setSortOrder(AbstractDAO.getShort(rs, "sort_order"));
        } catch (SQLException sqle) {
            logger.warn("Optional field: " + sqle.getMessage());
        }

    }
    
	public Short getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Short sortOrder) {
		this.sortOrder = sortOrder;
	}

	public final static List<FugitiveComponentDef> getFugComponentDefs() {
		List<FugitiveComponentDef> fugComponentDefs = new ArrayList<FugitiveComponentDef>();
		DefSelectItems fugComponentDefItems = FugitiveComponentDef.getData().getItems();
		for (SelectItem item : fugComponentDefItems.getCurrentItems()) {
			FugitiveComponentDef fugComponentDef = (FugitiveComponentDef) fugComponentDefItems.getItem(item.getValue().toString());
			fugComponentDefs.add(fugComponentDef);
		}
		Collections.sort(fugComponentDefs, new Comparator<FugitiveComponentDef>() {
			@Override
			public int compare(FugitiveComponentDef c1, FugitiveComponentDef c2) {
				return c1.getSortOrder().compareTo(c2.getSortOrder());
			}
		}
		);
		return fugComponentDefs;
	}
}

