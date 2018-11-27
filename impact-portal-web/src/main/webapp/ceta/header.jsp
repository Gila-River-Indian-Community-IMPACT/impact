<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<%@ include file="../util/branding.jsp"%>
<f:facet name="nodeStamp">
	<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
		disabled="#{foo.disabled}" type="#{foo.type}"
		rendered="#{foo.rendered}" icon="#{foo.icon}"
		selected="#{foo.selected}"
		onclick="if (#{stackTestDetail.editable}) { alert('Please save or cancel your changes'); return false; }">
		<af:setActionListener from="#{foo.taskId}" to="#{myTasks.taskId}" />
	</af:commandMenuItem>
</f:facet>

<f:facet name="location">
	<af:panelGroup layout="horizontal"
		rendered="#{!stackTestDetail.viewOnly}">
		<af:commandLink text="Task - #{stackTestDetail.task.taskDescription}"
			action="stackTestDetail" />
		<f:facet name="separator">
			<af:objectSpacer width="10" height="1" />
		</f:facet>
		<af:outputText value=">" />
	</af:panelGroup>
</f:facet>