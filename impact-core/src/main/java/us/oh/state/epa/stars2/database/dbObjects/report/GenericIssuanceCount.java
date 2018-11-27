package us.oh.state.epa.stars2.database.dbObjects.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;

public class GenericIssuanceCount extends BaseDB {

    private String issuanceTypeCd;
    private Map<String, Integer> counts;

    public GenericIssuanceCount() {
    }

    public void populate(ResultSet rs) throws SQLException {
        
    }

    public final List<SelectItem> getCountsList() {
        List<SelectItem> r = new ArrayList<SelectItem>();
        SelectItem t = null;
        for (String k : counts.keySet())
            if (k.equalsIgnoreCase("TOTAL"))
                t = new SelectItem(counts.get(k), k);
            else
                r.add(new SelectItem(counts.get(k), k));
        r.add(t);
        return r;
    }
    
    /**
     * @return the counts
     */
    public final Map<String, Integer> getCounts() {
        if (counts == null){
            counts = new HashMap<String, Integer>();
        }
        return counts;
    }

    /**
     * @param the counts to set
     */
    public final void setCounts(Map<String, Integer> counts) {
        this.counts = counts;
    }

    /**
     * @return the issuanceTypeCd
     */
    public final String getIssuanceTypeCd() {
        return issuanceTypeCd;
    }

    /**
     * @param issuanceTypeCd the issuanceTypeCd to set
     */
    public final void setIssuanceTypeCd(String issuanceTypeCd) {
        this.issuanceTypeCd = issuanceTypeCd;
    }


    public final List<SelectItem> getDoLaas() {
        List<SelectItem> ret = DoLaaDef.getData().getItems().getAllSearchItems();
        
        return null;//ret;
    }
}
