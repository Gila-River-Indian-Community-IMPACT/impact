<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Inspections Scheduling Help Information">
		<af:page var="foo" value="#{menuModel.model}"
			title="Inspections Scheduling  Help Information">
			<af:panelForm>
				<af:outputFormatted value="#{fceSchedules.howToUse}" />
			</af:panelForm>
		</af:page>
	</af:document>
</f:view>
