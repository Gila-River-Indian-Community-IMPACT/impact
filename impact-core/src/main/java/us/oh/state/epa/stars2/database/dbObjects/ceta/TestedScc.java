package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.SQLException;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class TestedScc extends CetaBaseDB implements Comparable {
    
    // NOTE THIS IS NOT PUT IN THE DATABASE
    
    
    private String sccId;
    
    public TestedScc() {
        super();
    }
    
    public TestedScc(String sccId) {
        super();
        this.sccId = sccId;
    }
    
    public final int compareTo(Object b) {
        TestedScc rowB = (TestedScc)b;
        if(sccId == null || rowB.sccId == null) return 0;
        int i = sccId.compareTo(rowB.sccId);
        return i;
    }
    
    /** Populate this instance from a database ResultSet. */
    public final void populate(java.sql.ResultSet rs)throws SQLException {

    }
    
    static public ArrayList<TestedScc> buildTestedSccTable(String ss) {
        ArrayList<TestedScc> testedSccs = new ArrayList<TestedScc>();
        boolean more = ss.length() > 0;
        int cnt = 501;
        while(cnt-- > 0 && more) {
            String code;
            int i = ss.indexOf(' ');
            if(i == -1) {
                more = false;
                code = ss;
            } else {
                code = ss.substring(0, i);
                ss = ss.substring(i + 1);
            }
            TestedScc tScc = new TestedScc(code);
            testedSccs.add(tScc);
        }
        return testedSccs;
    }

    static public String removeAllParens(String sccs) {
        StringBuffer sb = new StringBuffer(sccs);
        while(true) {
            int left = sb.indexOf("(");
            if(left < 0) break;
            int right = sb.indexOf(")");
            if(right < 0) break;  // should not happen
            sb.delete(left, right + 1);
        }
        return sb.toString();
    }

    public String getSccId() {
        return sccId;
    }

    public void setSccId(String sccId) {
        this.sccId = sccId;
    }

}
