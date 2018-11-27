package us.wy.state.deq.impact.portal;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.gricdeq.impact.UserPrincipal;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import us.oh.state.epa.stars2.bo.MonitoringService;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactRole;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.app.contact.ExternalOrganization;
import us.wy.state.deq.impact.bo.CompanyService;
import us.wy.state.deq.impact.database.dbObjects.company.Company;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroup;
import us.wy.state.deq.impact.security.FederationUtil;

/**
 * Servlet Filter implementation class FilestoreFilter
 */
//@WebFilter("/FilestoreFilter")
public class FilestoreFilter implements Filter {

	private CompanyService companyService;
	
	private MonitoringService monitoringService;
	
	private boolean testMode = "true".equals(Config.getEnvEntry("app/testMode"));
    
    /**
     * Default constructor. 
     */
    public FilestoreFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, 
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		boolean redirect = true;
		String loginURL = req.getContextPath();

		redirect = testMode? false : !isAuthorized(req);
		
		if (redirect) {
			res.sendRedirect(loginURL);			
		} else {
			chain.doFilter(request, response);
		}
	}

	private boolean isAuthorized(HttpServletRequest req) throws DAOException {
		Principal principal = req.getUserPrincipal();
		List<ContactRole> contactRoles = null;
		String username = null;
		if (principal instanceof UserPrincipal) {
			UserPrincipal userPrincipal = (UserPrincipal)principal;
			contactRoles = userPrincipal.getContact().getContactRoles();
			username = userPrincipal.getName();
		}

		for (ContactRole contactRole : contactRoles) {

			ExternalOrganization externalOrg = contactRole.getExternalRole().getOrganization();
			Company company = null;
			if (null != externalOrg) {
					company = this.companyService.retrieveCompanyByExternalCompanyId(
							externalOrg.getOrganizationId(), false);
			}
			
			if (null != company) {
	    		FacilityList[] authorizedFacilities = this.companyService
	    				.retrieveAuthorizedFacilities(company.getCmpId(), username);

	    		if (req.getRequestURI().contains("/MonitorGroup/")) {
    				// URI pattern example:
    				// http://impact/filestore/MonitorGroup/143/MonitorReport/895/142063.PNG
    				String uri = req.getRequestURI();

    				// URI substring example:
    				// MonitorGroup/143/MonitorReport/895/142063.PNG
    				String uriPart = uri.substring(uri.indexOf("MonitorGroup"));

    				if (null != uriPart) {
    					// Monitor group ID will be the 2nd array element after splitting on "/"
	    				String monitorGroupIdStr = uriPart.split("/")[1];

	    				if (null != monitorGroupIdStr && NumberUtils.isNumber(monitorGroupIdStr)) {
	    					int monitorGroupId = Integer.parseInt(monitorGroupIdStr);
	    					MonitorGroup monitorGroup = 
	    							this.monitoringService.retrieveMonitorGroup(monitorGroupId);
	    					if (null != monitorGroup && !monitorGroup.isAqdOwned()) {
		    					for (FacilityList facility : authorizedFacilities) {
		    						if (facility.getFacilityId().equals(monitorGroup.getFacilityId())) {
		    							return true;
		    						}
		    					}
	    					}
	    				}
    				}	    				
    			}

	    		for (FacilityList facility : authorizedFacilities) {
	    			if (req.getRequestURI().contains("/Facilities/" + facility.getFacilityId())) {
	    				return true;
	    			}
	    		}
			}
		}
		return false;
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		WebApplicationContext ctx = 
				WebApplicationContextUtils.getRequiredWebApplicationContext(
						fConfig.getServletContext());
		this.companyService = ctx.getBean(CompanyService.class);		
		this.monitoringService = ctx.getBean(MonitoringService.class);		
	}

}
