<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="timesheetEntryBody" title="Timesheet Entry Detail">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<%@ include file="../util/validate.js"%>
		<af:form>
			<af:panelHeader text="Timesheet Entry Detail" size="0" />
			<af:panelForm rows="9" maxColumns="1" labelWidth="140"
				partialTriggers="modifyFunction modifySection">

				<afh:rowLayout halign="left">
					<af:selectInputDate label="Date:"
						value="#{timesheetEntry.modifyRow.date}" showRequired="true"
						readOnly="#{!timesheetEntry.editMode1}" id="modifyDate">
						<af:validateDateTimeRange minimum="#{timesheetEntry.minDate}"
							maximum="#{timesheetEntry.maxDate}" />
					</af:selectInputDate>
				</afh:rowLayout>
				<afh:rowLayout halign="left"
					rendered="#{not empty timesheetEntry.functions}">
					<af:selectOneChoice label="Function:" id="modifyFunction"
						autoSubmit="true" showRequired="true"
						readOnly="#{!timesheetEntry.editMode1}"
						valueChangeListener="#{timesheetEntry.modifyFunctionSelected}"
						value="#{timesheetEntry.modifyRow.function}"
						unselectedLabel="">
						<mu:selectItems value="#{timesheetEntry.functions}" />
					</af:selectOneChoice>
				</afh:rowLayout>
				<afh:rowLayout halign="left"
					rendered="#{not empty timesheetEntry.modifySectionsByFunction}">
					<af:selectOneChoice label="Section:" id="modifySection"
						autoSubmit="true" showRequired="true"
						readOnly="#{!timesheetEntry.editMode1}"
						valueChangeListener="#{timesheetEntry.modifySectionSelected}"
						value="#{timesheetEntry.modifyRow.section}">
						<f:selectItems value="#{timesheetEntry.modifySectionsByFunction}" />
					</af:selectOneChoice>
				</afh:rowLayout>
				<afh:rowLayout halign="left"
					rendered="#{timesheetEntry.modifyNsrRequired}">
					<af:inputText columns="8" label="NSR:" rows="1"
						value="#{timesheetEntry.modifyRow.nsrId}" id="modifyNsrId"
						readOnly="#{!timesheetEntry.editMode1}" 
						showRequired="true" immediate="true"
						autoSubmit="true" maximumLength="8">
					</af:inputText>
				</afh:rowLayout>
				<afh:rowLayout halign="left"
					rendered="#{timesheetEntry.modifyTvRequired}">
					<af:inputText columns="8" label="Title V:" rows="1"
						value="#{timesheetEntry.modifyRow.tvId}" id="modifyTvId"
						readOnly="#{!timesheetEntry.editMode1}" 
						showRequired="true" immediate="true"
						autoSubmit="true" maximumLength="8">
					</af:inputText>
				</afh:rowLayout>
				<afh:rowLayout halign="left">
					<af:inputText label="Hours:" columns="4" rows="1" id="modifyHours"
						readOnly="#{!timesheetEntry.editMode1}" showRequired="true"
						value="#{timesheetEntry.modifyRow.hours}" maximumLength="4">
						<af:convertNumber type="number" pattern="##.#" />
					</af:inputText>
				</afh:rowLayout>
				<afh:rowLayout halign="left">
					<af:selectBooleanCheckbox label="OT:" id="modifyOvertime"
						readOnly="false" rendered="#{timesheetEntry.editMode1}"
						value="#{timesheetEntry.modifyRow.overtime}" />
					<af:inputText label="OT:" readOnly="true" id="modifyOvertimeYesNo"
						value="#{timesheetEntry.modifyRow.overtimeYesNo}"
						rendered="#{!timesheetEntry.editMode1}" />
				</afh:rowLayout>

				<afh:rowLayout halign="left">
					<af:selectBooleanCheckbox label="Invoiced:" id="modifyInvoiced"
						readOnly="false"
						rendered="#{timesheetEntry.editMode1 and timesheetEntry.timesheetEntryEditAllowed and timesheetEntry.admin}"
						valueChangeListener="#{timesheetEntry.invoicedValueChanged}"
						value="#{timesheetEntry.modifyRow.invoiced}" />
					<af:inputText label="Invoiced:" readOnly="true"
						id="modifyInvoicedYesNo"
						value="#{timesheetEntry.modifyRow.invoicedYesNo}"
						rendered="#{!timesheetEntry.editMode1 or !timesheetEntry.timesheetEntryEditAllowed or !timesheetEntry.admin}" />
				</afh:rowLayout>

				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Edit"
							rendered="#{!timesheetEntry.editMode1}"
							disabled="#{!timesheetEntry.timesheetEntryEditAllowed}"
							action="#{timesheetEntry.editTimesheetEntry}" />
						<af:commandButton text="Save"
							rendered="#{timesheetEntry.editMode1}"
							action="#{timesheetEntry.saveTimesheetEntry}" />
						<af:commandButton text="Cancel" immediate="true"
							rendered="#{timesheetEntry.editMode1}"
							action="#{timesheetEntry.cancelTimesheetEntry}" />
						<af:commandButton text="Delete"
							rendered="#{!timesheetEntry.editMode1}"
							disabled="#{!timesheetEntry.timesheetEntryEditAllowed}"
							action="#{timesheetEntry.deleteTimesheetEntry}" />
						<af:commandButton text="Close" immediate="true"
							rendered="#{!timesheetEntry.editMode1}"
							action="#{timesheetEntry.closeTimesheetEntryDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>