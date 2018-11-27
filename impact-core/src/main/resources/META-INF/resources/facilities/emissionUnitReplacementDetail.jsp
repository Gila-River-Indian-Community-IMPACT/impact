<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>


<f:view>
	<af:document id="ReplacementBody" inlineStyle="width:1000px;" title="Serial Number Tracking">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<%@ include file="../util/validate.js"%>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				id="emissionUnitReplacementPage" title="Serial Number Tracking">
				<af:panelForm rows="5" maxColumns="1" labelWidth="140" width="900">
					<af:inputText id="manufacturerName" label="Manufacturer Name:"
						readOnly="#{! facilityProfile.editable1}"
						value="#{facilityProfile.emissionUnitReplacement.manufacturerName}"
						showRequired="true" columns="50" maximumLength="50">
					</af:inputText>

					<af:inputText id="serialNumber" label="Serial Number:"
						readOnly="#{! facilityProfile.editable1}"
						value="#{facilityProfile.emissionUnitReplacement.serialNumber}"
						showRequired="true" columns="50" maximumLength="50">
					</af:inputText>

					<af:selectInputDate id="serialNumberEffectiveDate"
						label="Effective Date:"
						value="#{facilityProfile.emissionUnitReplacement.serialNumberEffectiveDate}"
						readOnly="#{! facilityProfile.editable1}" showRequired="true">
						<af:validateDateTimeRange minimum="1900-01-01"
							maximum="#{infraDefs.currentDate}" />
					</af:selectInputDate>
				</af:panelForm>

				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Edit"
							disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
							rendered="#{!facilityProfile.editable1}"
							action="#{facilityProfile.editEmissionUnitReplacement}" />
						<af:commandButton text="Save" id="saveButton"
							rendered="#{facilityProfile.editable1}"
							action="#{facilityProfile.saveEmissionUnitReplacement}" />
						<af:commandButton text="Cancel" id="cancelButton"
							rendered="#{facilityProfile.editable1}"
							action="#{facilityProfile.cancelEditEmissionUnitReplacement}" />
						<af:commandButton text="Delete"
							disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
							rendered="#{!facilityProfile.editable1}"
							action="#{facilityProfile.deleteEmissionUnitReplacement}" />
						<af:commandButton text="Close"
							rendered="#{!facilityProfile.editable1}"
							action="#{facilityProfile.closeAddrDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
