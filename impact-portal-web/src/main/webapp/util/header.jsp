<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<%@ include file="branding.jsp"%>
<f:facet name="nodeStamp">
	<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
		type="#{foo.type}" disabled="#{foo.disabled}"
		rendered="#{foo.rendered}" icon="#{foo.icon}" immediate="true"
		inlineStyle="#{foo.type == 'global' ? 'color: #ff0000;font-weight:bold' : ''}">
			<af:setActionListener from="#{foo.taskId}" to="#{myTasks.taskId}" />
	</af:commandMenuItem>
</f:facet>
