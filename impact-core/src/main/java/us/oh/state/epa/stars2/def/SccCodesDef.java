package us.oh.state.epa.stars2.def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;

public class SccCodesDef extends SimpleDef{
	private static final long REFRESH_INTERVAL = 60 * 60 * 1000; // 1 hour
	private static HashMap<String, SccCode> sccCodes;
	private static long refreshTime = 0;
	private static transient Logger logger;
	
	public static final String defName = "SccCodesDef";
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB(
					"InfrastructureSQL.retrieveSccCodes",
					SccCodesDef.class);
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
	
	public static SccCode getSccCode(String sccCode) {
		if (sccCode == null) {
			return null;
		}
		
		if (logger == null) {
			logger = Logger.getLogger(new SccCodesDef().getClass());
		}
		
		if (sccCodes == null || System.currentTimeMillis() > refreshTime) {
			List<String> searchSccList = new ArrayList<String>();
			sccCodes = new HashMap<String, SccCode>();
			try {
				InfrastructureService svc = ServiceFactory.getInstance().getInfrastructureService();
	            SccCode tempSccs[] = svc.retrieveSccCodes(searchSccList);
	            for (SccCode tempScc : tempSccs) {
	            	sccCodes.put(tempScc.getSccId(), tempScc);
	            }	            
			} catch (Exception ex) {
	        	logger.error(ex.getMessage(), ex);
	        }
			refreshTime = System.currentTimeMillis() + REFRESH_INTERVAL;
		}
		
		if (sccCodes == null) {
			logger.error("SCC codes not cached.");
			return null;
		}
			
		return sccCodes.get(sccCode);
	}
	
	public static void forceRefresh(){
		sccCodes = null;
	}
}
