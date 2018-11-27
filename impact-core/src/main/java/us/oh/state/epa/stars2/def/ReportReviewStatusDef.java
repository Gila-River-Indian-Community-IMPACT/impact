package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ReportReviewStatusDef {
	
	public static final String MONITORING_REVIEW_DONE = "MRD";
	public static final String COMPLIANCE_REVIEW_DONE = "CRD";
	private static final String defName = "ReportReviewStatusDef";
	
	public static DefData getData() {
	  ConfigManager cfgMgr = ConfigManagerFactory.configManager();
	  DefData data = cfgMgr.getDef(defName);
	
	  if (data == null) {
	      data = new DefData();
	      data.loadFromDB("MO_MONITOR_RPT_REVIEW_STATUS_DEF", "MO_MONITOR_RPT_REVIEW_STATUS_CD", 
	    		  			"MO_MONITOR_RPT_REVIEW_STATUS_DSC", "deprecated");
	
	      cfgMgr.addDef(defName, data);
	  }
	  return data;
  }
	
}
