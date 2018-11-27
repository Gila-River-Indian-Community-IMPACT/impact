/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package us.wy.state.deq.impact.security;

import java.io.StringReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.cxf.fediz.core.Claim;
import org.apache.cxf.fediz.tomcat8.FederationPrincipalImpl;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import us.wy.state.deq.impact.app.contact.ExternalOrganization;

public class FederationUtil {
	
    private static Logger logger = Logger.getLogger(FederationUtil.class);
    
    public static final String IMPACT_APPLICATION = "IMPACT";
    
	public static final String ENVITE_CLAIM_TYPE_USERNAME = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name";
	public static final String ENVITE_CLAIM_TYPE_FIRST_NAME = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname";
	public static final String ENVITE_CLAIM_TYPE_LAST_NAME = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname";
	public static final String ENVITE_CLAIM_TYPE_ENVITE_ROLES = "http://envite.deq.wyoming.gov/2013/01/claims/enviteroles";
	public static final String ENVITE_CLAIM_TYPE_EMAIL_ADDRESS = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress";
	public static final String ENVITE_CLAIM_TYPE_AUTH_METHOD = "http://schemas.microsoft.com/ws/2008/06/identity/claims/authenticationmethod";
	public static final String ENVITE_CLAIM_TYPE_AUTH_INSTANT = "http://schemas.microsoft.com/ws/2008/06/identity/claims/authenticationinstant";
	
	public static String[] getRoles() {
		ArrayList<String> roles = new ArrayList<String>();
    	Principal p = FacesContext.getCurrentInstance().getExternalContext()
    			.getUserPrincipal();
    	String[] availableRoles = {"eSign","Edit", "Trusted Partner"};
        if (p != null) {
        	for (String ar : availableRoles) {
        		if (FacesContext.getCurrentInstance().getExternalContext().isUserInRole(ar)) {
        			roles.add(ar);
        		}
        	}
        }
        return roles.toArray(new String[0]);
	}
	
    public static Claim[] getClaims() {
    	Claim[] claims = new Claim[0];
    	if (FacesContext.getCurrentInstance() != null) {
	        Principal p = FacesContext.getCurrentInstance().getExternalContext()
	    			.getUserPrincipal();
	        claims = getClaims(p);
    	}
    	return claims;
    }

    public static Claim[] getClaims(Principal principal) {
    	Claim[] claims = new Claim[0];
        if (principal instanceof FederationPrincipalImpl) {
            FederationPrincipalImpl fp = (FederationPrincipalImpl)principal;
            claims = fp.getClaims().toArray(claims);
        }
    	return claims;
    }

    public static String getClaimValue(Claim[] claims, String claimType) {
    	String claimValue = null;
    	for (Claim claim : claims) {
    		logger.debug("----> claim.getClaimType() = " + claim.getClaimType());
    		if (claim.getClaimType().toString().equals(claimType)) {
    			claimValue = (String)claim.getValue();
    		}
    	}
    	return claimValue;
    }
    
    public static String getClaimValue(String claimType) {
    	return getClaimValue(getClaims(),claimType);
    }
    
    public static String getClaimValue(Principal principal, String claimType) {
    	return getClaimValue(getClaims(principal),claimType);
    }
    
    public static ExternalOrganization[] getExternalOrganizations(Claim[] claims) {
    	return getExternalOrganizations(getClaimValue(claims,ENVITE_CLAIM_TYPE_ENVITE_ROLES));
    }
        
    public static ExternalOrganization[] getExternalOrganizations(String externalRolesClaimValue) {
    	return getExternalOrganizations(externalRolesClaimValue,true);
    }
    
    public static ExternalOrganization[] getExternalOrganizations(String externalRolesClaimValue, boolean claimRequestApproved) {
    	List<ExternalOrganization> externalOrgs = new ArrayList<ExternalOrganization>();

    	if (null != externalRolesClaimValue) {
	    	InputSource source = new InputSource(new StringReader(externalRolesClaimValue));
	    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder db;
	    	Document document;
	    	XPathFactory xpathFactory = XPathFactory.newInstance();
	    	XPath xpath = xpathFactory.newXPath();
			try {
				db = dbf.newDocumentBuilder();
				document = db.parse(source);
				NodeList nodes = (NodeList) xpath.evaluate("/envite/claim", document, XPathConstants.NODESET);
				
				for (int i = 0; i < nodes.getLength(); i++) {
					Node node = nodes.item(i);
					logger.debug(node);
					String claimApplication = xpath.evaluate("./application/text()", node);
					
					if (IMPACT_APPLICATION.equals(claimApplication)) {
						ExternalOrganization externalOrg = new ExternalOrganization();
						
						//TODO redo this
						
//						externalOrg.setOrganizationId(Integer.parseInt(xpath.evaluate("./organizationid/text()", node)));
//						externalOrg.setOrganization(xpath.evaluate("./organization/text()", node));
//						externalOrg.setRoleId(Integer.parseInt(xpath.evaluate("./roleid/text()", node)));
//						externalOrg.setClaimRequestApproved(claimRequestApproved);
						externalOrgs.add(externalOrg);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
    	}
    	return externalOrgs.toArray(new ExternalOrganization[0]);
    	
    }
    
    public static ExternalOrganization[] getExternalOrgs() {
    	return getExternalOrganizations(getClaimValue(ENVITE_CLAIM_TYPE_ENVITE_ROLES));
    }

    public static ExternalOrganization[] getExternalOrganizations(Principal principal) {
    	return getExternalOrganizations(getClaimValue(principal, ENVITE_CLAIM_TYPE_ENVITE_ROLES));
    }

}
