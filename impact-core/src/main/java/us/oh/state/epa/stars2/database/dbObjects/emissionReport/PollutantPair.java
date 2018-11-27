package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class PollutantPair extends BaseDB {
    private String pollutantCd;
    private String groupCd;

    public PollutantPair() {
        super();
    }

    public final void populate(ResultSet rs) {
        try {
            setPollutantCd(rs.getString("sub_cd"));
            setGroupCd(rs.getString("sup_cd"));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }

    public String getGroupCd() {
        return groupCd;
    }

    public void setGroupCd(String groupCd) {
        this.groupCd = groupCd;
    }

    public String getPollutantCd() {
        return pollutantCd;
    }

    public void setPollutantCd(String pollutantCd) {
        this.pollutantCd = pollutantCd;
    }

}


