package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * @author Kbradley
 * 
 */
public class SccLevel extends BaseDB {
    private String sccLevelDsc;

    public SccLevel() {
    }

    public SccLevel(SccLevel old) {
        super(old);

        if (old != null) {
            setSccLevelDsc(old.getSccLevelDsc());
        }
    }

    /**
     * @return
     */
    public final String getSccLevelDsc() {
        return sccLevelDsc;
    }

    /**
     * @param sccLevelDsc
     */
    public final void setSccLevelDsc(String sccLevelDsc) {
        this.sccLevelDsc = sccLevelDsc;
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            setSccLevelDsc(rs.getString("scc_level_dsc"));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }
}
