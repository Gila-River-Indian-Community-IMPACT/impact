package us.oh.state.epa.stars2.framework.util;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.config.Node;

/**
 * To turn this on, add the following in the configuration file serviced by the
 * <code>ConfigManager</code>:<br>
 * 
 * <code>app.CheckVariable</code><br>
 * <code>attribute-name: on value:true|yes</code>.
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @author Andrew Wilcox
 * @version 1.0
 */
public class CheckVariable {
    private static final String WHERE = "app.assert";
    private static boolean CHECK = true;

    static {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        Node node = cfgMgr.getNode(WHERE);

        if (node == null) {
            CHECK = false;
        }
    }

    /**
     * @param condition
     */
    public static final void isTrue(boolean condition) {
        if (CHECK && !condition) {
            throw new RuntimeException("Check failed.");
        }
    }

    /**
     * @param condition
     * @param message
     */
    public static final void isTrue(boolean condition, String message) {
        if (CHECK && !condition) {
            throw new RuntimeException(message);
        }
    }

    /**
     * @param condition
     */
    public static final void isFalse(boolean condition) {
        if (CHECK && condition) {
            throw new RuntimeException("Check failed.");
        }
    }

    /**
     * @param condition
     * @param message
     */
    public static final void isFalse(boolean condition, String message) {
        if (CHECK && condition) {
            throw new RuntimeException(message);
        }
    }

    /**
     * @param object
     */
    public static final void notNull(Object object) {
        if (CHECK && object == null) {
            throw new RuntimeException("Check failed.");
        }
    }

    /**
     * @param object
     * @param message
     */
    public static final void notNull(Object object, String message) {
        if (CHECK && object == null) {
            throw new RuntimeException(message);
        }
    }

    /**
     * @param object
     */
    public static final void isNull(Object object) {
        if (CHECK && object != null) {
            throw new RuntimeException("Check failed.");
        }
    }

    /**
     * @param object
     * @param message
     */
    public static final void isNull(Object object, String message) {
        if (CHECK && object != null) {
            throw new RuntimeException(message);
        }
    }
}
