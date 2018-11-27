package us.wy.state.deq.impact.pub.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import us.wy.state.deq.impact.pub.ApplicationAccessAgreement;

/**
 * Servlet Filter implementation class ApplicationAccessAcknowlegeFilter
 * This * filter checks to see if the user has acknowledged the site
 * disclaimer. If not, the user will be redirected to the site
 * disclaimer page.
 */
public class ApplicationAccessAcknowledgeFilter implements Filter {
	private static Logger logger = Logger.getLogger(ApplicationAccessAcknowledgeFilter.class);
	private FilterConfig filterConfig;

	public ApplicationAccessAcknowledgeFilter() {
		super();
	}

	public void destroy() {
		this.filterConfig = null;
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		boolean redirect = false;
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		String disclaimerUri = "/disclaimer.jsf";
		String redirectURL = req.getContextPath() + disclaimerUri;

		if (!req.getRequestURI().endsWith(disclaimerUri)) {
			// has the user already acknowledged the disclaimer
			WebApplicationContext ctx = WebApplicationContextUtils
					.getRequiredWebApplicationContext(this.filterConfig.getServletContext());
			
			ApplicationAccessAgreement appAccessAgreement = ctx.getBean(ApplicationAccessAgreement.class);
			
			if (null != appAccessAgreement && !appAccessAgreement.isHasAgreed()) {
				logger.warn("Attempt to access the system without acknowledging the disclaimer");
				redirect = true;
			}
		}

		if (redirect) {
			logger.debug("Redirecting to " + redirectURL);
			resp.sendRedirect(redirectURL);
		} else {
			chain.doFilter(request, response);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		this.filterConfig = fConfig;
	}
}
