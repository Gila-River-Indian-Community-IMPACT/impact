<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<%@ include file="../util/branding.jsp"%>
<f:facet name="nodeStamp">
  <af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
    type="#{foo.type}" disabled="#{foo.disabled}" rendered="#{foo.rendered}"
    onclick="if (#{companyProfile.editable}) { alert('Please save or discard your changes'); return false; }" />
</f:facet>
