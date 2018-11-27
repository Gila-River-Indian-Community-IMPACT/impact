<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<f:view>

  <af:document title="IMPACT">
    <af:form>
      <af:page title="IMPACT Logout">
        <f:facet name="branding">
          <af:objectImage source="/images/stars2.png" />
        </f:facet>
        <af:panelBorder>
          <f:facet name="innerLeft">
            <af:panelGroup layout="vertical">
              <af:outputText value="User logged out successfully" />
            </af:panelGroup>
          </f:facet>
        </af:panelBorder>
      </af:page>
    </af:form>
  </af:document>
</f:view>