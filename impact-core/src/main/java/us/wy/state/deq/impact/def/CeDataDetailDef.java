package us.wy.state.deq.impact.def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;

public class CeDataDetailDef {
	private static final long REFRESH_INTERVAL = 60 * 60 * 1000; // 1 hour
	private static Map<String, ArrayList<DataDetail>> dataDetails;
	private static long refreshTime = 0;
	private static transient Logger logger;
	
	public static DataDetail[] getDataDetails(String controlEquipmentTypeCd) {
		List<DataDetail> ceTypeDataDetails = new ArrayList<DataDetail>();
		List<DataDetail> tempCeTypeDDs = new ArrayList<DataDetail>();
		if (controlEquipmentTypeCd == null) {
			return null;
		}
		
		if (logger == null) {
			logger = Logger.getLogger(new CeDataDetailDef().getClass());
		}
		
		if (dataDetails == null || System.currentTimeMillis() > refreshTime) {
			dataDetails = new HashMap<String, ArrayList<DataDetail>>();
			try {
				FacilityService svc = ServiceFactory.getInstance().getFacilityService();
				dataDetails = svc.retrieveControlEquipmentDataDetails();      
			} catch (Exception ex) {
	        	logger.error(ex.getMessage(), ex);
	        }
			refreshTime = System.currentTimeMillis() + REFRESH_INTERVAL;
		}
		
		if (dataDetails == null) {
			logger.error("Data Details not cached.");
			return null;
		} else {
			tempCeTypeDDs = dataDetails.get(controlEquipmentTypeCd);
		}
		
		if (tempCeTypeDDs == null) {
			logger.debug("CE Data Details not available (might not have any).");
			tempCeTypeDDs = new ArrayList<DataDetail>();
		}
		
		for(DataDetail dd : tempCeTypeDDs) {
			ceTypeDataDetails.add(new DataDetail(dd));
		}
		
		return ceTypeDataDetails.toArray(new DataDetail[0]);
	}
	
	public static void forceRefresh(){
		dataDetails = null;
	}
}
