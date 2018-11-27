<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<%@ include file="../util/branding.jsp"%>
<f:facet name="nodeStamp">
  <af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
    type="#{foo.type}" disabled="#{foo.disabled}" icon="#{foo.icon}"
    rendered="#{foo.rendered}" immediate="true" 
    onclick="if (#{serviceCatalog.editingReport}) { alert('Please save or discard your changes'); return false; }" />
</f:facet>