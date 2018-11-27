package us.oh.state.epa.stars2.framework.config;

import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import us.oh.state.epa.stars2.framework.daemon.ManagedComponent;
import us.oh.state.epa.stars2.framework.exception.SystemException;
import us.oh.state.epa.stars2.framework.exception.UnableToStartException;
import us.wy.state.deq.impact.App;

/**
 * CompMgr.
 * 
 * The component manager is responsible for the loading of indivdual component
 * configuration information and instantiating the object.
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version 1.1
 * @author Andrew Wilcox
 */
@DependsOn("impactApp")
@Service
public class CompMgr {
	private static ConfigManager configManager = App.getApplicationContext()
			.getBean(ConfigManager.class);
	private static HashMap<String, Object> singletonPool = new HashMap<String, Object>();
	private static Logger logger = Logger.getLogger(CompMgr.class);
	private static String appName;
	// private static String timeZone;
	// private static boolean tomcat;

	static {
		Node app = configManager.getNode("app");
		appName = app.getAsString("application-code");

		// timeZone = Config.findNode("app.timeZone").getText();
		// System.setProperty("user.timezone", timeZone);

		loadStartupComponents();
	}

	/**
	 * @return
	 */
	public static final String getAppName() {
		return appName;
	}

	public static final void setAppName(String appName) {
		CompMgr.appName = appName;
	}

	/**
	 * Returns the component, given the name of the component.
	 * 
	 * @param name
	 *            Name of the component to retrieve.
	 * @return Object
	 */
	public static final Object newInstance(String name)
			throws UnableToStartException {
		Node theNode = configManager.getNode(name);

		if (theNode == null) {
			StringBuffer b = new StringBuffer("CompMgr was unable to find ");
			b.append("a component at '");
			b.append(name);
			b.append("'. The location appears to be invalid. ");
			throw new RuntimeException(b.toString());
		}

		return newInstance(theNode);
	}

	/**
	 * Returns the component, given the XML configuration "node" for the
	 * component.
	 * 
	 * @param theNode
	 *            XML Node of component to load.
	 * @return Object
	 */
	public static final Object newInstance(Node theNode)
			throws UnableToStartException {
		Object ret = null;

		if (theNode.getAsBoolean("is-singleton")) {
			String fullPath = theNode.getFullPath();
			ret = singletonPool.get(fullPath);

			if (ret == null) {
				ret = loadComponent(theNode);
				if (ret != null)
					singletonPool.put(fullPath, ret);
			}
		} else {
			ret = loadComponent(theNode);
		}

		return ret;
	}

	protected static final Object loadComponent(Node node)
			throws UnableToStartException {
		String objectClassName = node.getAsString("component-class");
		Object theObject = null;

		try {
			Class<?> theClass = Class.forName(objectClassName);
			theObject = theClass.newInstance();

			if (theObject instanceof Component) {
				Component theComponent = (Component) theObject;
				Properties parameters = null;

				if (node instanceof BasicNode) {
					parameters = ((BasicNode) node).getAttributes();
				} else {
					parameters = new Properties();

					for (String name : node.getAttributeNames()) {
						parameters.put(name, node.getAsString(name));
					}
				}
				if (!theComponent.start(parameters, node.getFullPath())) {
					theObject = null;
					logger.warn("Component " + objectClassName
							+ " is not started up.");
				} else {
					if (theComponent instanceof Thread) {
						((Thread) theComponent).setDaemon(true);
						((Thread) theComponent).start();
					}
				}
			}
		} catch (UnableToStartException use) {
			throw use;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			StringBuffer b = new StringBuffer("Unable to load component ");
			b.append(node.getFullPath());
			b.append(" of type ");
			b.append(objectClassName);
			throw new SystemException(b.toString());
		}

		return theObject;
	}

	private static void loadStartupComponents() {
		Node[] appNodes = configManager.getAllChildrenOf(configManager
				.getNode("app"));

		for (Node node : appNodes) {
			String loadAtStartup = node.getAsString("load-at-startup");
			if ((loadAtStartup != null)
					&& loadAtStartup.equalsIgnoreCase("true")) {
				try {
					newInstance(node);
				} catch (UnableToStartException utse) {
					logger.error("Unable to start " + node.getName() + " "
							+ utse.getMessage(), utse);
				}
			}
		}

		return;
	}

	public static void shutdown() {
		// Cleanup our static variables so we get cleaned up between deploys.
		configManager.shutdown();
		configManager = null;

		for (Object component : singletonPool.values()) {
			if (component instanceof ManagedComponent) {
				((ManagedComponent) component).shutdown();
			}
			singletonPool.remove(component);
			component = null;
		}

		singletonPool = null;
		logger = null;
		appName = null;
	}

}
