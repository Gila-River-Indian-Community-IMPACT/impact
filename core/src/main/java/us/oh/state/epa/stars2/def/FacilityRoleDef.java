package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FacilityRoleDef extends SimpleDef {
    //public static final String DOLAA_PERMIT_WRITER = "perw";
    //public static final String CO_EIS_REVIEWER ="eisr";
    //public static final String CO_FACILITY_PROFILE_MAINTENANCE = "cfpm";
    //public static final String DOLAA_ER_REVIEWER ="emrr";
    //public static final String DOLAA_REPORT_REVIEWER ="curr";
    //public static final String DOLAA_REVIEWER_APPROVER = "reva";
    //public static final String DOLAA_FAC_PROF_MAINT = "dfpm";
    //public static final String EMISSION_BANKING_NOTIFICATION = "fera";
	public static final String TV_RO_REVIEW_ENGINEER = "TRE";
	public static final String NSR_PERMIT_ENGINEER = "NPE";
	public static final String TV_PERMIT_ENGINEER = "TPE";
	public static final String COMPLIANCE_REPORT_REVIEWER = "DEC";    // replaces DOLAA_REVIEWER_APPROVER from STARS2
	public static final String AQD_ADMINISTRATOR = "AQA";
	public static final String NSR_PUBLICATION_COORDINATOR = "NPC";
	public static final String EMISSIONS_INVENTORY_REVIEWER = "DEE";  // replaces DOLAA_ER_REVIEWER from STARS2
	public static final String UNDELIVERED_MAIL_ADMIN = "UMA";
	public static final String NSR_ADMIN_ASSISTANT = "NAA";
	public static final String TV_PUBLICATION_COORDINATOR = "TPC";
	public static final String FACILITY_PROFILE_ADMIN = "FPA";
	public static final String EMISSIONS_INVENTORY_INVOICER = "EII"; 
	public static final String NSR_PUBLIC_NOTICE_REVIEWER = "NPN"; 
	public static final String TV_ADMIN_ASSISTANT = "TAA"; 
	public static final String NSR_PERMIT_SUPERVISOR = "NPS"; 
	public static final String NSR_PERMIT_PEER_REVIEW_ENGINEER = "NPR"; 
	public static final String WDEQ_DIRECTOR = "WQD"; 
	public static final String DISTRICT_ENGINEER_UNDELIVERED_MAIL_REVIEWER = "DEU"; 
	public static final String TV_PUBLIC_NOTICE_REVIEWER = "TPN"; 
    public static final String ENFORCEMENT_REVIEWER_DISTRICT = "EFAD";
    public static final String ENFORCEMENT_REVIEWER_CHEYENNE = "EFAC";
    public static final String TV_PERMIT_SUPERVISOR = "TPS";
    public static final String TV_PERMIT_PEER_REVIEW_ENGINEER = "TPP";
    private static final String defName = "FacilityRoleDef";
    private String type;
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("WorkFlowSQL.retrieveFacilityRoleDefs", FacilityRoleDef.class); 

            cfgMgr.addDef(defName, data);
        } 
        
        return data;
    }
    
    public void populate(ResultSet rs) {
    	super.populate(rs);
    	
    	try {
    		setType(rs.getString("type"));
    	} catch (SQLException sqle) {
    		logger.warn("Optional field: " + sqle.getMessage());
    	}
    }
    
    public String getType() {
    	return type;
    }
    
    public void setType(String type) {
    	this.type = type;
    }
}
