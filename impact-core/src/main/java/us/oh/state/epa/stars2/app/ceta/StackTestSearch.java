package us.oh.state.epa.stars2.app.ceta;

import java.util.LinkedHashMap;

import us.oh.state.epa.stars2.database.dao.ceta.StackTestSQLDAO;
import us.oh.state.epa.stars2.webcommon.ceta.StackTestSearchCommon;

@SuppressWarnings("serial")
public class StackTestSearch extends StackTestSearchCommon {
	
    public StackTestSearch() {
        super();
    }
    
    /**
	 * @return
	 */
	public final LinkedHashMap<String, String> getStackTestDateBy() {
		return buildStackTestDateBy();
	}

	/**
	 * @return
	 */
	public static LinkedHashMap<String, String> buildStackTestDateBy() {
		LinkedHashMap<String, String> stackTestDateBy = new LinkedHashMap<String, String>();

		stackTestDateBy.put("Results Received Date",
				StackTestSQLDAO.RESULTS_RECEIVED_DATE);
		stackTestDateBy.put("Results Reviewed Date",
				StackTestSQLDAO.RESULTS_REVIEWD_DATE);
		stackTestDateBy.put("Scheduled Date", 
				StackTestSQLDAO.SCHEDULED_DATE);
		stackTestDateBy.put("Test Date", 
				StackTestSQLDAO.TEST_DATE);

		return stackTestDateBy;
	}
}
