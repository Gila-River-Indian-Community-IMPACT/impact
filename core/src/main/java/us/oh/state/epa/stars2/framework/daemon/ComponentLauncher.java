package us.oh.state.epa.stars2.framework.daemon;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.framework.config.CompMgr;

/**
 * A simple class to launch Components as stand-alone Java programs
 *
 * @author wilcoxa
 * @version $Revision: 1.4 $
 * @see us.oh.state.epa.stars2.framework.config.Component
 * @see us.oh.state.epa.stars2.framework.daemon.ManagedComponent
 */
public class ComponentLauncher {
    private static Logger logger = Logger.getLogger(ComponentLauncher.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("use: ComponentLauncher <component> ");
            System.out.println("(where <component> is the the location "
                    + "of the component");
            System.out.println("definition in the ");
            System.out.println("configurgation. For example: "
                    + "components.foo.bar");
            System.exit(1);
        }

        try {
            ManagedComponent component = (ManagedComponent) CompMgr
                    .newInstance(args[0]);
            Thread t = new Thread(component);
            t.start();

            while (true) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            return;
        }
    }
}
