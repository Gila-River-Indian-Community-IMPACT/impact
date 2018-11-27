package us.oh.state.epa.stars2.framework.config;

import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Convenience methods for accessing required and optional
 * <code>Component</code> init parameters.
 *
 * @author wilcoxa
 * @version $Revision: 1.4 $
 *
 * @see us.oh.state.epa.stars2.framework.config.Component
 */
public class ComponentUtil {
    private String name;
    private Map<Object, Object> parameters;

    /**
     * @param componentName
     * @param parameters
     */
    public ComponentUtil(String componentName, Map<Object, Object> parameters) {
        this.name = componentName;
        this.parameters = parameters;
    }

    /**
     * @param parameterName
     * @return
     */
    public final String getParameter(String parameterName) {
        return getRequiredParameter(parameterName);
    }

    /**
     * @param parameterName
     * @param defaultValue
     * @return
     */
    public final String getParameter(String parameterName, String defaultValue) {
        String value = (String) parameters.get(parameterName);
        String ret = defaultValue;
        
        if (value != null) {
            ret = value;
        }

        return ret;
    }

    /**
     * @param parameterName
     * @return
     */
    public final int getParameterAsInt(String parameterName) {
        return Integer.parseInt(getRequiredParameter(parameterName));
    }

    /**
     * @param parameterName
     * @param defaultValue
     * @return
     */
    public int getParameterAsInt(String parameterName, int defaultValue) {
        String parameter = (String) parameters.get(parameterName);
        int ret = defaultValue;

        if (parameter != null && parameter.length() > 0) {
            ret = Integer.parseInt(parameter);
        }

        return ret;
    }

    /**
     * @param parameterName
     * @param defaultList
     * @return
     */
    public final String[] getParameterAsStringList(String parameterName,
            String[] defaultList) {
        String parameter = (String) parameters.get(parameterName);
        String[] ret = defaultList;

        if (parameter != null && parameter.trim().length() > 0) {
            ArrayList<String> list = new ArrayList<String>();
            StringTokenizer t = new StringTokenizer(parameter, ", ");

            while (t.hasMoreElements()) {
                list.add(t.nextToken());
            }
            ret = list.toArray(new String[list.size()]);
        }

        return ret;
    }

    protected final String getRequiredParameter(String paramName) {
        String value = (String) parameters.get(paramName);

        if (value == null || value.trim().length() == 0) {
            StringBuffer b = new StringBuffer("Missing required parameter ");
            b.append(paramName);
            b.append(" for component ");
            b.append(name);
            throw new RuntimeException(b.toString());
        }

        return value;
    }
}
