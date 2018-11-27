package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * CM_POLLUTANT_DEF table
 */
public class EmissionCalcMethodDef extends SimpleDef implements Comparable<EmissionCalcMethodDef> {

    // SCCEmissionsFactor uses FIRE DB for factor/formula
    public static final String SCCEmissionsTimeBasedFactorCEM = "1";
    public static final String SCCEmissionsTimeBasedFactorStackTest = "2";
    public static final String SCCEmissionsTimeBasedFactorEstimated = "3";
    public static final String SCCEmissionsTimeBasedFactorAllowable = "4";
    public static final String SCCEmissionsFactor = "109";
    public static final String SCCEmissions = "201";
    public static final String AQDGenerated = "301";
    
    private String explanation;
    private Integer accuracy;
    private boolean userSelectable;
    
    private static final String defName = "EmissionCalcMethodDef";
    private static final String userSelectableDefName = "UserSelectableEmissionCalcMethodDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("EmissionsReportSQL.retrieveCalculationMethods", 
            		EmissionCalcMethodDef.class);
            
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public void populate(ResultSet rs) {

        super.populate(rs);

        try {
        	setExplanation(rs.getString("emission_calc_method_long_dsc"));
        	setAccuracy(rs.getInt("emission_calc_method_accuracy"));
        	setUserSelectable(AbstractDAO.translateIndicatorToBoolean(rs.getString("user_selectable")));
        } catch (SQLException sqle) {
            logger.warn("Optional field: " + sqle.getMessage());
        }

    }
    
    public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public static boolean isFactorMethod(String cd) {
    	return null != cd? Integer.parseInt(cd) < 200 : false;
    }
    
    public static boolean isTimeBasedFactorMethod(String cd) {
    	return (null != cd)? (SCCEmissionsTimeBasedFactorCEM.equals(cd) || 
    			SCCEmissionsTimeBasedFactorStackTest.equals(cd) || 
    			SCCEmissionsTimeBasedFactorEstimated.equals(cd) || 
    			SCCEmissionsTimeBasedFactorAllowable.equals(cd)) : false;
    }
	
	public Integer getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Integer accuracy) {
		this.accuracy = accuracy;
	}

	public static List<EmissionCalcMethodDef> retrieveMethods() {
		List<EmissionCalcMethodDef> ret = new ArrayList<EmissionCalcMethodDef>();
		
		DefData data = getData();
		
		if(data != null) {
			DefSelectItems items = data.getItems();
			for(BaseDef def : items.getCompleteItems().values()) {
				if(def instanceof EmissionCalcMethodDef && !def.isDeprecated()) {
					ret.add((EmissionCalcMethodDef) def);
				}
			}
			Collections.sort(ret, Collections.reverseOrder());
		}
		
		return ret;
	}

	@Override
	public int compareTo(EmissionCalcMethodDef o) {
		if (null != accuracy) {
			if (null != o.accuracy) {
				return accuracy.compareTo(o.accuracy);
			}
			return -1;
		}
		return -1;
	}

	public static DefData getUserSelectableData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(userSelectableDefName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("EmissionsReportSQL.retrieveUserSelectableCalculationMethods", EmissionCalcMethodDef.class);
			cfgMgr.addDef(userSelectableDefName, data);
		}

		return data;
	}

	public boolean isUserSelectable() {
		return userSelectable;
	}

	public void setUserSelectable(boolean userSelectable) {
		this.userSelectable = userSelectable;
	}
}

