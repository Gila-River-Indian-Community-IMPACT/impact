package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;


@SuppressWarnings("serial")
public class SelectString extends CetaBaseDB {
    private String str;
    
    public SelectString() {
        super();
    }
    
    public SelectString(String str) {
        super();
        this.str = str;
    }
    
    public SelectString(boolean selected, String str) {
        super();
        this.setSelected(selected);
        this.str = str;
    }
    
    public final void populate(java.sql.ResultSet rs)throws SQLException {

        try{
            setStr(rs.getString("str"));
            setSelected(false);
        } catch(SQLException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}
