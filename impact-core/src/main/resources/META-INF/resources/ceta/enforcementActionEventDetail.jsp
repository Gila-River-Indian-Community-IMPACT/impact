<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="enforcementActionEventBody"
		title="Enforcement Action Event Detail">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<%@ include file="../util/validate.js"%>
		<af:form>
			<af:panelHeader text="Enforcement Action Event Detail" size="0" />
			<af:panelForm rows="9" maxColumns="1" labelWidth="140">
				
				<afh:rowLayout halign="left">
					<af:selectInputDate label="Date" id="eventDate"
						readOnly="#{!enforcementActionDetail.editMode1}" valign="middle"
						showRequired="true"
						value="#{enforcementActionDetail.modifyEnforcementActionEvent.eventDate}">
						<af:validateDateTimeRange
							minimum="1970-01-01"
							maximum="#{enforcementActionDetail.todaysDate}" />
					</af:selectInputDate>
				</afh:rowLayout>
				<afh:rowLayout halign="left">
					<af:selectOneChoice label="Enforcement Action Event"
						readOnly="#{!enforcementActionDetail.editMode1}" id="eventCd"
						showRequired="true"
						value="#{enforcementActionDetail.modifyEnforcementActionEvent.eventCd}"
						unselectedLabel="">
						<mu:selectItems id="enforcementActionEvents"
							value="#{enforcementActionDetail.enforcementActionEventDefs}" />
					</af:selectOneChoice>
				</afh:rowLayout>
				
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Edit"
							rendered="#{!enforcementActionDetail.editMode1}"
							disabled="#{!enforcementActionDetail.enforcementActionEditAllowed}"
							action="#{enforcementActionDetail.editEnforcementActionEvent}" />
						<af:commandButton text="Save" rendered="#{enforcementActionDetail.editMode1}"
							action="#{enforcementActionDetail.saveEnforcementActionEvent}" />
						<af:commandButton text="Cancel"
							immediate="true"
							rendered="#{enforcementActionDetail.editMode1}"
							action="#{enforcementActionDetail.cancelEnforcementActionEvent}" />
						<af:commandButton text="Delete"
							rendered="#{!enforcementActionDetail.editMode1}"
							disabled="#{!enforcementActionDetail.enforcementActionEditAllowed}"
							action="#{enforcementActionDetail.deleteEnforcementActionEvent}" />
						<af:commandButton text="Close"
							immediate="true"
							rendered="#{!enforcementActionDetail.editMode1}"
							action="#{enforcementActionDetail.closeEnforcementActionEventDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>