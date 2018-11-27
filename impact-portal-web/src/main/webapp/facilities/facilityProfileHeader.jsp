<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<%@ include file="../util/branding.jsp"%>
<f:facet name="nodeStamp">
	<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
		icon="#{foo.icon}" type="#{foo.type}" disabled="#{foo.disabled}"
		rendered="#{foo.rendered}" selected="#{foo.selected}"
		onclick="if (#{facilityProfile.editable}) { alert('Please save or discard your changes'); return false; }"
		inlineStyle="#{foo.type == 'global' ? 'color: #ff0000;font-weight:bold' : ''}">
		<af:setActionListener from="#{foo.taskId}" to="#{myTasks.taskId}" />
	</af:commandMenuItem>
</f:facet>

<f:facet name="location">
	<af:panelGroup layout="horizontal"
		rendered="#{facilityProfile.editStaging && facilityProfile.task != null}">
		<af:commandLink text="Task - #{facilityProfile.task.taskDescription}"
			action="#{myTasks.setStagingFacilityEditFlag}" />
		<f:facet name="separator">
			<af:objectSpacer width="10" height="1" />
		</f:facet>
		<af:outputText value=">" />
	</af:panelGroup>
</f:facet>
