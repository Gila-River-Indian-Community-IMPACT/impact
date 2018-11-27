package us.oh.state.epa.stars2.framework.config;

/**
 * Some classes that use the <code>ConfigManager</code> might cache some of
 * the configuration information. This is not the recommended practice, but
 * there are cases when some sort of calculation is performed based on
 * configuration data. In cases like that, the consumer of configuration data
 * can implement this interface and register itself with the ConfigManager. If
 * the config data is changed, the ConfigManager will notify listeners by
 * calling the <code>configChanged()</code> method.
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
public interface ConfigListener {
    /**
     * @param configManager
     */
    void configChanged(ConfigManager configManager);
}
