package us.oh.state.epa.stars2.framework.daemon;

import java.util.Properties;

import us.oh.state.epa.stars2.framework.config.Component;
import us.oh.state.epa.stars2.framework.config.ComponentUtil;
import us.oh.state.epa.stars2.framework.exception.UnableToStartException;

/**
 * This subclass of <code>ManagedThread</code> allows subclasses to be
 * configured using the <code>CompMgr</code>. Subclasses should implement
 * <code>init()</code> which is called when the component is started. Calling
 * <code>util()</code> allows easy access to init parameters.
 *
 * @author wilcoxa
 * @version $Revision: 1.3 $
 * @see us.oh.state.epa.stars2.framework.util.ManagedThread;
 * @see java.lang.Thread
 * @see us.oh.state.epa.stars2.framework.config.Component
 * @see us.oh.state.epa.stars2.framework.config.CompMgr
 * @see us.oh.state.epa.stars2.framework.config.ComponentUtil
 */
public abstract class ManagedComponent extends ManagedThread implements
        Component {
    private String name;
    private ComponentUtil util;

    public boolean start(Properties initParameters, String instanceName)
            throws UnableToStartException {
        name = instanceName;
        util = new ComponentUtil(name, initParameters);
        return init();
    }

    /**
     * @see us.oh.state.epa.stars2.framework.config.Component#getInstanceName()
     */
    public String getInstanceName() {
        return name;
    }

    /**
     * @return
     */
    protected final ComponentUtil util() {
        return util;
    }

    /**
     * This method is called at the end of <code>Component.start()</code>.
     * @return TODO
     *
     * @throws UnableToStartException
     */
    protected abstract boolean init() throws UnableToStartException;

}
