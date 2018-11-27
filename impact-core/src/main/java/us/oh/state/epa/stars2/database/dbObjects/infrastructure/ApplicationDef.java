package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * @author Kbradley
 * 
 */
public class ApplicationDef extends BaseDB {
    static final long serialVersionUID = 1L;
    private String appCd;
    private String appDesc;
    private Integer currentInstanceKey;

    public ApplicationDef() {
    }

    public ApplicationDef(ApplicationDef old) {
        super(old);

        if (old != null) {
            setAppCd(old.getAppCd());
            setAppDesc(old.getAppDesc());
            setCurrentInstanceKey(old.getCurrentInstanceKey());
        }
    }

    /**
     * @return
     */
    public final String getAppCd() {
        return appCd;
    }

    /**
     * @param appCd
     */
    public final void setAppCd(String appCd) {
        this.appCd = appCd;
    }

    /**
     * @return
     */
    public final String getAppDesc() {
        return appDesc;
    }

    /**
     * @param appDesc
     */
    public final void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    /**
     * @return
     */
    public final Integer getCurrentInstanceKey() {
        return currentInstanceKey;
    }

    /**
     * @param currentInstanceKey
     */
    public final void setCurrentInstanceKey(Integer currentInstanceKey) {
        if (currentInstanceKey == null) {
            this.currentInstanceKey = new Integer(1);
        } else {
            this.currentInstanceKey = currentInstanceKey;
        }
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            setAppCd(rs.getString("application_type_cd"));
            setAppDesc(rs.getString("application_type_dsc"));
            setCurrentInstanceKey(AbstractDAO.getInteger(rs, "current_key"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((appCd == null) ? 0 : appCd.hashCode());
        result = PRIME * result + ((appDesc == null) ? 0 : appDesc.hashCode());
        result = PRIME
                * result
                + ((currentInstanceKey == null) ? 0 : currentInstanceKey
                        .hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ApplicationDef other = (ApplicationDef) obj;
        if (appCd == null) {
            if (other.appCd != null)
                return false;
        } else if (!appCd.equals(other.appCd))
            return false;
        if (appDesc == null) {
            if (other.appDesc != null)
                return false;
        } else if (!appDesc.equals(other.appDesc))
            return false;
        if (currentInstanceKey == null) {
            if (other.currentInstanceKey != null)
                return false;
        } else if (!currentInstanceKey.equals(other.currentInstanceKey))
            return false;
        return true;
    }
}
