<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<%@ include file="../util/branding.jsp"%>
<f:facet name="nodeStamp">
	<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
		disabled="#{foo.disabled}" type="#{foo.type}"
		rendered="#{foo.rendered}" icon="#{foo.icon}"
		selected="#{foo.selected}"
		onclick="if (#{reportDetail.disallowClick}) { alert('Please save or discard your changes'); return false; } else {if (#{reportDetail.openedForEdit}) { alert('Please click Validate and Recompute Totals/Save at the emissions inventory level before leaving the emissions inventory'); return false; }}">
		<af:setActionListener from="#{foo.taskId}" to="#{myTasks.taskId}" />
	</af:commandMenuItem>
</f:facet>

<f:facet name="location">
	<af:panelGroup layout="horizontal"
		rendered="#{reportProfile.internalEditable}">
		<af:commandLink text="Task - #{reportDetail.task.taskDescription}"
			action="TVDetail" />
		<f:facet name="separator">
			<af:objectSpacer width="10" height="1" />
		</f:facet>
		<af:outputText value=">" />
	</af:panelGroup>
</f:facet>