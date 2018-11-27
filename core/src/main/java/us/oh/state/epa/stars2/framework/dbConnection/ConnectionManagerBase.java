package us.oh.state.epa.stars2.framework.dbConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * ConnectionManagerBase.
 *
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @version 1.0
 * @author
 */

public abstract class ConnectionManagerBase implements ConnectionManager {
    protected static String DB_TYPE = "Undefined";
    protected int minPoolSize;
    protected int maxPoolSize;
    protected transient Logger logger;
    protected HashMap<Connection, Throwable> connList = new HashMap<Connection, Throwable>();
    
    public ConnectionManagerBase() {
        logger = Logger.getLogger(this.getClass());
    }
    
    public Connection getConnection() throws SQLException {
        return null;
    }

    /**
     * @see us.oh.state.epa.stars2.framework.dbConnection.ConnectionManager#dbType()
     */
    public final String dbType() {
        return DB_TYPE;
    }

    /**
     * @param conn
     */
    public final void removeConnection(Connection conn) {
    }

    /**
     * @see us.oh.state.epa.stars2.framework.dbConnection.ConnectionManager#getConnList()
     */
    public final HashMap<Connection, Throwable> getConnList() {
        return connList;
    }
    
    /**
     * @see us.oh.state.epa.stars2.framework.dbConnection.ConnectionManager#removeConnFromConnList(java.sql.Connection)
     */
    public final void removeConnFromConnList(Connection conn) {
        connList.remove(conn);
    }
    
    protected int string2int(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
}
