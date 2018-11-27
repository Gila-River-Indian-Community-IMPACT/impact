package us.wy.state.deq.impact.portal;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class StartupServlet
 */
@SuppressWarnings("serial")
@WebServlet("/StartupServlet")
public class StartupServlet extends HttpServlet {

	Logger logger = Logger.getLogger(getClass());
	
	// load this class
//	private Class<DefaultElementValueComparator> defaultElementValueComparator = DefaultElementValueComparator.class;

	public StartupServlet() throws ClassNotFoundException {
		super();
//		logger.debug("classloader = " + getClass().getClassLoader());
//		logger.debug("defaultElementValueComparator = " + defaultElementValueComparator);
//		
		logger.debug("parent classloader = " + getClass().getClassLoader().getParent());
		getClass().getClassLoader().getParent().loadClass("net.sf.ehcache.store.DefaultElementValueComparator");

//		DefaultElementValueComparator ehcache = new DefaultElementValueComparator(new CacheConfiguration());
//		logger.debug("ehcache = " + ehcache);
		
	}
	
}
