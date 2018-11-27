package us.oh.state.epa.stars2.framework.config;

import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * AbstractComponent.
 *
 * <DL>
 * <DT><B>Copyright:</B></DT><DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT><DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @version 1.0
 * @author Andrew Wilcox
 */
public abstract class AbstractComponent implements Component {

	private static final long serialVersionUID = 9103674377974057394L;

	private String instanceName;
    private Map<Object, Object> parameters = new TreeMap<Object, Object>();
    private ComponentUtil util;

    /**
     * @see us.oh.state.epa.stars2.framework.config.Component#start(java.util.Properties, java.lang.String)
     */
    public boolean start(Properties initParameters, String instanceName) {
        parameters.putAll(initParameters);
        this.instanceName = instanceName;
        util = new ComponentUtil(this.instanceName, parameters);
        startInternal();
        return true;
    }

    /**
     * @see us.oh.state.epa.stars2.framework.config.Component#getInstanceName()
     */
    public final String getInstanceName() {
        return instanceName;
    }

    /**
     * @param instanceName
     */
    public final void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    protected abstract void startInternal();

    protected final String[] getParameterAsStringList(String parameterName,
            String[] defaultList) {
        return util.getParameterAsStringList(parameterName, defaultList);
    }

    protected final int getParameterAsInt(String parameterName, int defaultValue) {
        return util.getParameterAsInt(parameterName, defaultValue);
    }

    protected final int getParameterAsInt(String parameterName) {
        return util.getParameterAsInt(parameterName);
    }

    protected final String getParameter(String parameterName) {
        return util.getParameter(parameterName);
    }
}
