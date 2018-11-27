package us.oh.state.epa.stars2.framework.config;

import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Supported properties:
 * <p>
 * <code>prefix</code> (default: <code>app.queries</code>)
 * </p>
 * 
 * @author Andrew Wilcox
 */
@Component
@SuppressWarnings("serial")
public class SQLLoader extends AbstractComponent {
    private static final String DEFAULT_PREFIX = "app.queries";
    private Logger logger = Logger.getLogger(SQLLoader.class);
    private String prefix = DEFAULT_PREFIX;
    private HashMap<String, String> cache = new HashMap<String, String>();;

    public final boolean start(Properties initParameters, String instanceName) {
        logger.debug("SQLLoader:start");
        setInstanceName(instanceName);
        prefix = initParameters.getProperty("prefix", DEFAULT_PREFIX);
        logger.debug(new StringBuffer("prefix: ").append(prefix).toString());
        return true;
    }

    protected final void startInternal() {
    }

    /**
     * This method looks in the configuration for a SQL statement that is stored
     * in <code>PREFIX.</code><i>name</i> (where <CODE>PREFIX</code> is a
     * component property which defaults to <code>app.queries</code>). <code>
     * name</code> can refer to a partial path like <code>lease.new</code>.
     * In that case, this method looks under <code>app.queries.lease.new</code>
     * for the SQL. If you want to override this default SQL with SQL that is
     * database specific, this can be done by placing the override query under
     * <code>app.queries.DB-TYPE<code> (where <code>DB-TYPE</code> is the 
     * database type that is returned by <code>ConnectionManager.dbType()
     * </code>). For example, in the example given above, if you wanted
     * to provide a PostgreSQL version of the SQL, you would put it under 
     * <code>PREFIX.lease.new.postgreSQL</code> (the string <code>
     * postgreSQL</code> comes from <code>
     * us.oh.state.epa.stars2.framework.Const.DATABASE_POSTGRESQL</code>. 
     * <p>
     * This method has two advantages over embedding SQL directly in the code:
     * <ol>
     * <li>The SQL is an an external file rather than being in the code.
     * This means that a change to the SQL doesn't require a recompile.</li>
     * <li>All of the SQL is in the same place.</li>
     * <li>This method allows you to override SQL for a particular 
     * database.</li>
     * </ol> 
     * </p>
     * This method also supports an &quot;include&quot; mechanism. If you
     * should like to include a SQL snippet in your SQL, you can do
     * so with <code>${location}</code> where the location is relative
     * to the <code>prefix</code>. For example:
     * <pre><code>
     *      select ${AccountSQL.COLUMN_NAMES} from account.
     * </code></pre>
     * 
     * @param name The name of the query (ex, lease)
     * @param dbType The name of the database connection. You get this 
     * from <code>ConnectionManager.dbType()</code>.
     * @return The SQL string that is referenced by the given name
     */
    public final String find(String name, String dbType) {
        String ret = null;

        ret = cache.get(name);

        if (ret == null) {
            Node n = findNode(name, dbType);

            if (n == null) {
                StringBuffer b = new StringBuffer(
                        "Unable to find requested node: ");
                b.append(name);
                throw new RuntimeException(b.toString());
            }

            ret = preprocessNode(n, dbType);

            if (ret != null) {
                synchronized (cache) {
                    cache.put(name, ret);
                }
            }
        }
        return ret;
    }

    protected final Node findNode(String name, String dbType) {
        StringBuffer b;

        if (dbType != null) {
            b = new StringBuffer(prefix);
            b.append(ConfigManager.DELIMITER);
            b.append(name);
            b.append(ConfigManager.DELIMITER);
            b.append(dbType);

            Node n = Config.findNode(b.toString());

            if (n != null) {
                return n;
            }
        }

        b = new StringBuffer(prefix);
        b.append(ConfigManager.DELIMITER);
        b.append(name);
        return Config.findNode(b.toString());
    }

    protected final String preprocessNode(Node n, String dbType) {
        String text = n.getText();
        StringBuffer b = new StringBuffer(text.length());
        char[] string = text.toCharArray();

        for (int i = 0; i < string.length; i++) {

            if (string[i] == '$' && i < string.length - 1
                    && string[i + 1] == '{') {
                Extract extract = extractLocation(string, i + 2);

                if (extract == null) {
                    StringBuffer message = new StringBuffer();
                    message.append("Malformed query at ");
                    message.append(n.getFullPath());
                    message.append(". Found \"${\" but no closing \"}.");
                    throw new RuntimeException(message.toString());
                }

                Node other = findNode(extract.location, dbType);
                String content = preprocessNode(other, dbType);
                b.append(content);
                i = extract.newIndex;
            } else {
                b.append(string[i]);
            }
        }

        return b.toString();
    }

    protected final Extract extractLocation(char[] string, int index) {
        StringBuffer b = new StringBuffer();
        boolean found = false;
        int i = index;

        for (; i < string.length; i++) {

            if (string[i] == '}') {
                found = true;
                break;
            }

            b.append(string[i]);
        }

        if (!found) {
            return null;
        }

        Extract e = new Extract();
        e.location = b.toString();
        e.newIndex = i;
        return e;
    }

    protected final class Extract {
        String location;

        int newIndex;
    }
}
