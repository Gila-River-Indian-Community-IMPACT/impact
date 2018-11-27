<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<f:view>
  <af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeResize}" title="IMPACT">
	 <f:facet name="metaContainer">
	   <f:verbatim>
	     <meta http-equiv="refresh" content="2; ${pageContext.request.contextPath}/scs/authenticateUser" />
	   </f:verbatim>
	 </f:facet>
    <af:form>
     <af:page title="IMPACT Login">
       <f:facet name="branding">
       </f:facet>
        <af:panelBorder>
          <f:facet name="innerLeft">
            <af:panelGroup layout="vertical">
              <af:panelForm labelWidth="40%">
                <af:outputText value="Logging in ... " />
              </af:panelForm>
            </af:panelGroup>
          </f:facet>
        </af:panelBorder>
      </af:page>
    </af:form>
  </af:document>
</f:view>
