package us.oh.state.epa.stars2.framework.config;

import java.io.Serializable;
import java.util.Properties;

import us.oh.state.epa.stars2.framework.exception.UnableToStartException;

/**
 * The is interface can be used by a class that wished to be loaded using the
 * <code>ConfigManager</code>'s naming service. A class is not required to
 * realize this interface in order to be loaded by the
 * <code>ConfigManager</code>. However, a class that is loaded by the
 * <code>ConfigManager</code> and does not implement this interface will not
 * have any parameters sent to it.
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
public interface Component extends Serializable {
    boolean start(Properties initParameters, String instanceName)
            throws UnableToStartException;

    String getInstanceName();
}
