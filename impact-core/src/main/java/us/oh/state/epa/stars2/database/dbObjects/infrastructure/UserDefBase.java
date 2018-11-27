package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.io.ObjectStreamField;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class UserDefBase extends BaseDB {
    private String networkLoginNm;
    private Integer userId;
    private Timestamp passwordExpDt;

    public UserDefBase() {
    }

    public final String getNetworkLoginNm() {
        return networkLoginNm;
    }

    public final void setNetworkLoginNm(String networkLoginNm) {
        this.networkLoginNm = networkLoginNm;
    }

    public final Timestamp getPasswordExpDt() {
        return passwordExpDt;
    }

    public final void setPasswordExpDt(Timestamp passwordExpDt) {
        this.passwordExpDt = passwordExpDt;
    }

    public final Integer getUserId() {
        return userId;
    }

    public final void setUserId(Integer userId) {
        this.userId = userId;
    }

    private final static ObjectStreamField[]
                        serialPersistentFields = {
              new ObjectStreamField("networkLoginNm",String.class),
              new ObjectStreamField("userId",Integer.class),
              new ObjectStreamField("passwordExpDt",Timestamp.class)
    };
    
    public final void populate(ResultSet rs) {
        try {
            setNetworkLoginNm(rs.getString("network_login_nm"));
            setPasswordExpDt(rs.getTimestamp("password_exp_dt"));
            setUserId(AbstractDAO.getInteger(rs, "user_id"));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }
}
