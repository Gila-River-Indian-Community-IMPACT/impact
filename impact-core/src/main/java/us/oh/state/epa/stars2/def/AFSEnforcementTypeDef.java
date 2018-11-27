package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

@SuppressWarnings("serial")
public class AFSEnforcementTypeDef extends SimpleDef{
// public class AFSEnforcementTypeDef {
    public static final String L1 = "L1";
    public static final String D3 = "D3";
    public static final String D5 = "D5";
    public static final String EA = "EA";
    public static final String EAA = "EAA";
    public static final String D4 = "D4";
    public static final String _66 = "66"; //STATE_FINDINGS_AND_ORDERS_66
    public static final String _66A = "66A";  // UNILATERAL_FINDINGS_AND_ORDERS_66A
    public static final String LOCAL_FINDINGS_AND_ORDERS_68 = "68";
    public static final String FINAL_COMPLIANCE_AFTER_ENFORCEMNT_05 = "05";
    public static final String FINAL_COMPLIANCE_WITHOUT_ENFORCEMNT_05 = "05A";
    public static final String AFS_FINAL_COMPLIANCE_05 = "05";  // This is what is sent to AFS for "05" or "05A"
    public static final String WITHDRAWL_OF_ENFORCEMENT_ACTION_EW = "EW";
    public static final String REFER_TO_AGO_X3 = "X3";
    public static final String STATE_COURT_ORDER_FILED_X2 = "X2";
    public static final String DY = "DY";
    public static final String G4 = "G4";
    public static final String CASE_CLOSED_BANKRUPTCY_OR_LIMITATIONS_K9 = "K9";
    
    // Not in reference table but used by AFS Export
    public static final String STATE_DAY_ZERO_38 = "38";
    public static final String DATE_DAY_ZERO_ADDED_TO_AFS_VL = "VL";
    public static final String DATE_REFER_TO_AGO_ADDED_TO_AFS_OT = "OT";
    public static final String DATE_FINDINGS_OR_FINAL_COMPLIANCE_WAS_DECIDED_A4 = "A4";
    public static final String WHAT_IS_THE_DESCRIPTION_44 = "44";
    public static final String DATE_FINDINGS_OR_FINAL_COMPLIANCE_ADDED_TO_AFS_RT = "RT";
    
    private static final String defName = "AFSEnforcementTypeDef";
    
    private boolean penaltySepAmount;
    private boolean openningAction;
    private boolean closingAction;
    private boolean resolvingAction;
    private boolean refferingAction;
    private String sortOrder;
    private String formOfActionType;
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("EnforcementSQL.retrieveEnfActionTypes", AFSEnforcementTypeDef.class);
            //data.loadFromDB("CE_ENF_ACTION_TYPE_DEF", "ENF_ACTION_TYPE_CD",
                    //"ENF_ACTION_TYPE_DSC", "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public void populate(ResultSet rs) {

        super.populate(rs);
        
        try {
			setPenaltySepAmount(AbstractDAO.translateIndicatorToBoolean(rs.getString("PENALTY_SEP_AMOUNT")));
            setOpenningAction(AbstractDAO.translateIndicatorToBoolean(rs.getString("OPENNING_ACTION")));
            setClosingAction(AbstractDAO.translateIndicatorToBoolean(rs.getString("CLOSING_ACTION")));
            setResolvingAction(AbstractDAO.translateIndicatorToBoolean(rs.getString("RESOLVING_ACTION"))); 
            setRefferingAction(AbstractDAO.translateIndicatorToBoolean(rs.getString("REFFERING_ACTION")));
            setSortOrder(rs.getString("SORT_ORDER")); 
            setFormOfActionType(rs.getString("FORMAL_OR_INFORMAL"));
        } catch (SQLException sqle) {
            logger.warn("Optional field: " + sqle.getMessage());
        }
    }
    
    public static boolean isOpenningAction(String actionType) {
    	AFSEnforcementTypeDef type = (AFSEnforcementTypeDef)AFSEnforcementTypeDef.getData().getItems().getItem(actionType);
    	return type.isOpenningAction();
    }
    
    public static boolean isPenaltySepAmount(String actionType) {
    	AFSEnforcementTypeDef type = (AFSEnforcementTypeDef)AFSEnforcementTypeDef.getData().getItems().getItem(actionType);
    	return type.isPenaltySepAmount();
    }
    
    public static boolean isRefferingAction(String actionType) {
    	AFSEnforcementTypeDef type = (AFSEnforcementTypeDef)AFSEnforcementTypeDef.getData().getItems().getItem(actionType);
    	return type.isRefferingAction();
    }
    
    public static boolean isResolvingAction(String actionType) {
    	AFSEnforcementTypeDef type = (AFSEnforcementTypeDef)AFSEnforcementTypeDef.getData().getItems().getItem(actionType);
    	return type.isResolvingAction();
    }
    
    public static boolean isClosingAction(String actionType) {
    	AFSEnforcementTypeDef type = (AFSEnforcementTypeDef)AFSEnforcementTypeDef.getData().getItems().getItem(actionType);
    	return type.isClosingAction();
    }
    
    public static String getFormOfAction(String actionType) {
    	AFSEnforcementTypeDef type = (AFSEnforcementTypeDef)AFSEnforcementTypeDef.getData().getItems().getItem(actionType);
    	return type.getFormOfActionType();
    }

	public final boolean isPenaltySepAmount() {
		return penaltySepAmount;
	}

	public final void setPenaltySepAmount(boolean penaltySepAmount) {
		this.penaltySepAmount = penaltySepAmount;
	}

	public final boolean isOpenningAction() {
		return openningAction;
	}

	public final void setOpenningAction(boolean openningAction) {
		this.openningAction = openningAction;
	}

	public final boolean isClosingAction() {
		return closingAction;
	}

	public final boolean isRefferingAction() {
		return refferingAction;
	}

	public final void setRefferingAction(boolean refferingAction) {
		this.refferingAction = refferingAction;
	}

	public final void setClosingAction(boolean closingAction) {
		this.closingAction = closingAction;
	}

	public final boolean isResolvingAction() {
		return resolvingAction;
	}

	public final void setResolvingAction(boolean resolvingAction) {
		this.resolvingAction = resolvingAction;
	}

	public final String getSortOrder() {
		return sortOrder;
	}

	public final void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public final String getFormOfActionType() {
		return formOfActionType;
	}

	public final void setFormOfActionType(String formOfActionType) {
		this.formOfActionType = formOfActionType;
	}
	
	public final static boolean isFormal(String formOfActionType) {
		return formOfActionType.equals("F");
	}
	
	public final static boolean isInFormal(String formOfActionType) {
		return formOfActionType.equals("I");
	}
	
	public final static boolean isValidFormOfAction(String actionType, String formOfActionType) {
		boolean rtn;
		String formalType = AFSEnforcementTypeDef.getFormOfAction(actionType);
		if (isFormal(formOfActionType)) {
			rtn = formalType.equals("I") ? false : true;
		} else {
			rtn = formalType.equals("F") ? false : true;
		}
		
		return rtn;
	}
}
