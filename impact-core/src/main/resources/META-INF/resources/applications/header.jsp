<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<%@ include file="../util/branding.jsp"%>
<f:facet name="nodeStamp">
	<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
		disabled="#{foo.disabled}" type="#{foo.type}"
		rendered="#{foo.rendered}" icon="#{foo.icon}"
		onclick="if (#{applicationDetail.editMode || applicationDetail.semiEditMode}) { alert('Please save or cancel your changes'); return false; }" />
</f:facet>
