package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class IntegerPair extends BaseDB {
    private Integer a;
    private Integer b;

    public IntegerPair() {
        super();
    }

    public final void populate(ResultSet rs) {
        try {
            setA(AbstractDAO.getInteger(rs, "a"));
            setB(AbstractDAO.getInteger(rs, "b"));
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
    }

    public Integer getA() {
        return a;
    }

    public void setA(Integer a) {
        this.a = a;
    }

    public Integer getB() {
        return b;
    }

    public void setB(Integer b) {
        this.b = b;
    }

}


