<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="continuousMonitorEqtBody"
		title="Monitor Equipment Detail">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<%@ include file="../util/validate.js"%>
		<af:form>
			<af:panelHeader text="Monitor Equipment Detail" size="0" />
			<af:panelForm rows="9" maxColumns="1" labelWidth="140">

				<afh:rowLayout halign="left">
					<af:inputText label="Manufacturer" id="manufacturerName"
						readOnly="#{!continuousMonitorDetail.editMode1}" valign="middle"
						showRequired="true"
						rows="1" columns="130" maximumLength="120"
						value="#{continuousMonitorDetail.modifyContinuousMonitorEqt.manufacturerName}">
					</af:inputText>
				</afh:rowLayout>

				<afh:rowLayout halign="left">
					<af:inputText label="Model Number" id="modelNumber"
						readOnly="#{!continuousMonitorDetail.editMode1}" valign="middle"
						showRequired="true"
						rows="1" columns="130" maximumLength="120"
						value="#{continuousMonitorDetail.modifyContinuousMonitorEqt.modelNumber}">
					</af:inputText>
				</afh:rowLayout>

				<afh:rowLayout halign="left">
					<af:inputText label="Serial Number" id="serialNumber"
						readOnly="#{!continuousMonitorDetail.editMode1}" valign="middle"
						showRequired="true"
						rows="1" columns="130" maximumLength="120"
						value="#{continuousMonitorDetail.modifyContinuousMonitorEqt.serialNumber}">
					</af:inputText>
				</afh:rowLayout>

				<afh:rowLayout halign="left">
					<af:selectInputDate label="QA/QC Submitted Date"
						id="QAQCSubmittedDate"
						readOnly="#{!continuousMonitorDetail.editMode1}" valign="middle"
						value="#{continuousMonitorDetail.modifyContinuousMonitorEqt.QAQCSubmittedDate}">
						<af:validateDateTimeRange minimum="1970-01-01"
							maximum="#{continuousMonitorDetail.todaysDate}" />
					</af:selectInputDate>
				</afh:rowLayout>

				<afh:rowLayout halign="left">
					<af:selectInputDate label="QA/QC Accepted Date"
						id="QAQCAcceptedDate"
						readOnly="#{!continuousMonitorDetail.editMode1}" valign="middle"
						value="#{continuousMonitorDetail.modifyContinuousMonitorEqt.QAQCAcceptedDate}">
						<af:validateDateTimeRange minimum="1970-01-01"
							maximum="#{continuousMonitorDetail.todaysDate}" />

					</af:selectInputDate>
				</afh:rowLayout>

				<afh:rowLayout halign="left">
					<af:selectInputDate label="Install Date" id="installDate"
						readOnly="#{!continuousMonitorDetail.editMode1}" valign="middle"
						value="#{continuousMonitorDetail.modifyContinuousMonitorEqt.installDate}">
						<af:validateDateTimeRange minimum="1970-01-01"
							maximum="#{continuousMonitorDetail.todaysDate}" />
					</af:selectInputDate>
				</afh:rowLayout>

				<afh:rowLayout halign="left">
					<af:selectInputDate label="Removal Date" id="removalDate"
						readOnly="#{!continuousMonitorDetail.editMode1}" valign="middle"
						value="#{continuousMonitorDetail.modifyContinuousMonitorEqt.removalDate}">
						<af:validateDateTimeRange minimum="1970-01-01"
							maximum="#{continuousMonitorDetail.todaysDate}" />
					</af:selectInputDate>
				</afh:rowLayout>

				<afh:rowLayout halign="left">
					<af:inputText label="Additional Information" id="addlInfo"
						readOnly="#{!continuousMonitorDetail.editMode1}" valign="middle"
						rows="7" columns="150" maximumLength="1000"
						value="#{continuousMonitorDetail.modifyContinuousMonitorEqt.addlInfo}">
					</af:inputText>
				</afh:rowLayout>

				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Edit" id="edit"
							rendered="#{!continuousMonitorDetail.editMode1 && continuousMonitorDetail.internalApp}"
							disabled="#{!continuousMonitorDetail.continuousMonitorEditAllowed || facilityProfile.disabledUpdateButton}"
							action="#{continuousMonitorDetail.editContinuousMonitorEqt}" />
						<af:commandButton text="Save" id="save"
							rendered="#{continuousMonitorDetail.editMode1}"
							action="#{continuousMonitorDetail.saveContinuousMonitorEqt}" />
						<af:commandButton text="Cancel" immediate="true" id="cancel"
							rendered="#{continuousMonitorDetail.editMode1}"
							action="#{continuousMonitorDetail.cancelContinuousMonitorEqt}" />
						<af:commandButton text="Delete" id="delete"
							rendered="#{!continuousMonitorDetail.editMode1 && continuousMonitorDetail.admin}"
							disabled="#{!continuousMonitorDetail.continuousMonitorDeleteAllowed}"
							action="#{continuousMonitorDetail.deleteContinuousMonitorEqt}" />
						<af:commandButton text="Close" id="close"
							rendered="#{!continuousMonitorDetail.editMode1}"
							action="#{continuousMonitorDetail.closeContinuousMonitorEqtDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>