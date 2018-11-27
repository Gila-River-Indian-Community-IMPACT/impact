package us.oh.state.epa.stars2.framework.dbConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import us.oh.state.epa.stars2.framework.exception.UnableToStartException;

/**
 * ContainerConnectionManager.
 *
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2006 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @version 1.0
 * @author Alessandro Gherardi
 */
@Component
@SuppressWarnings("serial")
public class ContainerConnectionManager extends ConnectionManagerBase {
    private String instanceName;
    private DataSource dataSource;

    /**
     * initParameters consists of.
     * <ul>
     * <li>data-source</li>
     * </ul>
     */
    public boolean start(Properties initParameters, String inInstanceName)
            throws UnableToStartException {
        this.instanceName = inInstanceName;
        String dataSourceName = (String) initParameters.get("data-source");

        try {
        	Context initContext = new InitialContext();
        	Context envContext  = (Context)initContext.lookup("java:/comp/env");
        	dataSource = (DataSource)envContext.lookup(dataSourceName);
        } catch (NamingException ne) {
            throw new UnableToStartException(ne);
        }
        return true;
    }

    /**
     * @see us.oh.state.epa.stars2.framework.config.Component#getInstanceName()
     */
    public final String getInstanceName() {
        return instanceName;
    }

    /**
     * @see us.oh.state.epa.stars2.framework.dbConnection.ConnectionManagerBase#getConnection()
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
