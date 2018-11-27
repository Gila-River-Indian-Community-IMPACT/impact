package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * Title: DaemonInfo.
 * 
 * <p>
 * Description: Information about Daemon applications that has been stored in
 * the database.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 */

public class DaemonInfo extends BaseDB {
    private String daemonCd;
    private String description;
    private String hostname;
    private Integer portNumber;

    /**
     * Constructor.
     */
    public DaemonInfo() {
    }

    public DaemonInfo(DaemonInfo old) {
        super(old);

        if (old != null) {
            setDaemonCode(old.getDaemonCode());
            setDescription(old.getDescription());
            setHostName(old.getHostName());
            setPortNumber(old.getPortNumber());
        }
    }

    /**
     * Returns the four-character daemon type code, .e.g., "WF" for the
     * "WorkFlow" engine.
     * 
     * @return String Daemon type code.
     */
    public final String getDaemonCode() {
        return this.daemonCd;
    }

    /**
     * Sets the four-character daemon type code.
     * 
     * @param newCode
     *            String daemon type code.
     */
    public final void setDaemonCode(String newCode) {
        this.daemonCd = newCode;
    }

    /**
     * Returns the description for this type of Daemon. For example, the
     * description for daemon type "WF" is "WorkFlow Engine".
     * 
     * @return String Daemon description.
     */
    public final String getDescription() {
        return this.description;
    }

    /**
     * Sets the daemon type description.
     * 
     * @param newDesc
     *            String new daemon description.
     */
    public final void setDescription(String newDesc) {
        this.description = newDesc;
    }

    /**
     * Returns the hostname of the system actually executing the daemon. This
     * host name is suitable for creating a socket connection to the daemon.
     * 
     * @return String hostname.
     */
    public final String getHostName() {
        return this.hostname;
    }

    /**
     * Sets the daemon host name. Note that a hostname "localhost" will always
     * connect to a daemon running on the local host (given the correct port
     * number). Otherwise, the host name should be the name of a local system,
     * e.g., "mgdev001", or the fully specified name of a system, e.g.,
     * "mgdev001.mentorgen.com".
     * 
     * @param newName
     *            String daemon host name.
     */
    public final void setHostName(String newName) {
        this.hostname = newName;
    }

    /**
     * Returns the port number the daemon is using for socket connections. Using
     * the host name and port number, a remote application can open a socket
     * connection to the daemon.
     * 
     * @return Integer daemon port number.
     */
    public final Integer getPortNumber() {
        return this.portNumber;
    }

    /**
     * Sets the port number for the associated daemon.
     * 
     * @param newPort
     *            Integer port number.
     */
    public final void setPortNumber(Integer newPort) {
        this.portNumber = newPort;
    }

    public final void populate(ResultSet rs) {
        try {
            setDaemonCode(rs.getString("daemon_cd"));
            setDescription(rs.getString("daemon_dsc"));
            setHostName(rs.getString("hostname"));
            setPortNumber(AbstractDAO.getInteger(rs, "port_number"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }
}
