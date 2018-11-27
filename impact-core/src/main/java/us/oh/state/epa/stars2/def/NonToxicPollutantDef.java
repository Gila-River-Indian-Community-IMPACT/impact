package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.webcommon.reports.ReportBaseCommon;

/*
 * The codes declared in this class must match the content of the
 * CM_POLLUTANT_DEF table
 * 
 * This is used to retrieve just the CAPs and HAPs not the Toxics
 */

public class NonToxicPollutantDef extends PollutantDef {
	
	private static final long serialVersionUID = 6934589718575933152L;

	private static final String defName = "NonToxicPollutantDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrievePollutants",
                    NonToxicPollutantDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    // Wyoming wants all pollutants that can be select from the service catalog, not just limit pollutants to HAP, CAP, and GHG.
    /*
    public boolean isDeprecated() {
        return super.isDeprecated() ||
            (!getCategory().toLowerCase().contains("hap") && !getCategory().toLowerCase().contains("cap")
                    && !getCategory().toLowerCase().contains("ghg"));
    }
    */
    
    public static String getTheDescription(String cd) {

    	String rtn;
        try {
            rtn = getPollutantBaseDef(cd).getDescription();
        } catch(ApplicationException e) {
            rtn = "code: '" + cd + "'";
        }

        return rtn;
    }
    
    public static String getNeiCode(String cd) throws ApplicationException{
        return getPollutantBaseDef(cd).getNeiCode();
    }
    
    public static String getTheCategory(String cd) throws ApplicationException{
        return getPollutantBaseDef(cd).getCategory();
    }
    
    public static NonToxicPollutantDef getPollutantBaseDef(String cd)
            throws ApplicationException{

    	NonToxicPollutantDef bd = (NonToxicPollutantDef)NonToxicPollutantDef.getData().getItems().getItem(cd);
        if(null == bd){
            throw new ApplicationException(" (" + ReportBaseCommon.dataProb +
                        ": pollutant_cd \"" + cd + "\" not found)");
        }

        return bd;
    }

}
