<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center"
	rendered="#{permitDetail.permitAttachments}">
	<af:panelButtonBar>
		<af:commandButton text="Workflow Task"
			disabled="#{!permitDetail.fromTODOList}"
			action="#{permitDetail.goToCurrentWorkflow}" />
	</af:panelButtonBar>
</afh:rowLayout>
