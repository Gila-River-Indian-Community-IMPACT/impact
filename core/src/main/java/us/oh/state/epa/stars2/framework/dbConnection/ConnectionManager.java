package us.oh.state.epa.stars2.framework.dbConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import us.oh.state.epa.stars2.framework.config.Component;

/**
 * ConnectionManager.
 *
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @version 1.0
 * @author Andrew Wilcox
 */
public interface ConnectionManager extends Component {
    final String DEFAULT = "app.database.default";
    final String READONLY = "app.database.readOnly";
    final String STARS = "app.database.STARS";  // used by migration
    final String PRODUCTION = "app.database.PRODUCTION"; // used by migration
    final String J2EE = "app.database.j2ee";

    Connection getConnection() throws SQLException;

    void removeConnFromConnList(Connection conn);
    HashMap<Connection, Throwable> getConnList();
    
    String dbType();
}
