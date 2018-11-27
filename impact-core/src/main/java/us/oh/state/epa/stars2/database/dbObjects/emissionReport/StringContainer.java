package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class  StringContainer extends BaseDB {
    private String s;

    public StringContainer() {
        super();
    }
    
    public StringContainer(String s) {
        super();
        this.s = s;
    }

    public final void populate(ResultSet rs) {
        try {
            setStr(rs.getString("s"));

        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
    }

    public String getStr() {
        return s;
    }

    public void setStr(String s) {
        this.s = s;
    }
}


