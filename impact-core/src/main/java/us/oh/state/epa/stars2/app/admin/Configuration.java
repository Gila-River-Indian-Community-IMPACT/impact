package us.oh.state.epa.stars2.app.admin;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.webcommon.AppBase;

/**
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2006 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @author Kbradley
 *
 */
@SuppressWarnings("serial")
public class Configuration extends AppBase {
    public Configuration() {
        super();
    }
    
    /**
     * Used to reload the configManager
     */
    public final String reload() {
        final ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        
        if (cfgMgr != null) {
            cfgMgr.reload();
        }
        return null;
    }

}
