package us.oh.state.epa.stars2.database.dbObjects.util;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/*  Meaning of emissions:
 *  On database:
 *   fugitive\total         !null                    null
 *      !null         stack = total-fugitive       stack = null
 *       null         stack = total               stack = null
 *       
 *    On Web Page:
 *    if  stack=null   then total=fugitive
 *    if fugitive=null then total=stack
 */

public class DbInteger extends BaseDB {
    private Integer cnt;

    public DbInteger() {
        super();
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((cnt == null) ? 0 : cnt.hashCode());
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
        final DbInteger other = (DbInteger) obj;
        if (cnt == null) {
            if (other.cnt != null)
                return false;
        } else if (!cnt.equals(other.cnt))
            return false;
        return true;
    }

    public final void populate(ResultSet rs) {
        try {
            setCnt(AbstractDAO
                    .getInteger(rs, "cnt"));
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
    }

    public Integer getCnt() {
        return cnt;
    }
    
    public Integer getInteger() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }
}
