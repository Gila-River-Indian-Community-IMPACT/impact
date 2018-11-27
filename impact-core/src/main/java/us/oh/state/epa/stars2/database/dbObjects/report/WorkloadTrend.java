package us.oh.state.epa.stars2.database.dbObjects.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class WorkloadTrend extends BaseDB {

    private Timestamp month;
    private Map<String, Map<String, Integer>> counts;

    public WorkloadTrend() {
    }

    public void populate(ResultSet rs) throws SQLException {
        
    }

    /**
     * @return the counts
     */
    public final Map<String, Map<String, Integer>> getCounts() {
        if (counts == null){
            counts = new HashMap<String, Map<String, Integer>>();
        }
        return counts;
    }

    /**
     * @param counts the counts to set
     */
    public final void setCounts(Map<String, Map<String, Integer>> counts) {
        this.counts = counts;
    }

    /**
     * @return the month
     */
    public final Timestamp getMonth() {
        return month;
    }

    /**
     * @param month the month to set
     */
    public final void setMonth(Timestamp month) {
        this.month = month;
    }

}
