<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>


<f:view>
	<af:document id="SerialTrackingBody" inlineStyle="width:1000px;" title="Serial Number Tracking">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<%@ include file="../util/validate.js"%>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				id="engineSerialTrackingPage" title="Serial Number Tracking">
				<af:showDetailHeader text="Current Engine"
					rendered="#{!facilityProfile.noEngineSerialNumbers && !facilityProfile.engineSerialTrackingEditMode}"
					disclosed="true">
					<af:table id="currentSerialNumberTable" width="98%"
						value="#{facilityProfile.currentSerialNumbers}"
						bandingInterval="1" banding="row"
						varStatus="currentSerialNumberTableVs" var="currentSerialNumber"
						rows="#{facilitySearch.pageLimit}">
						<af:column formatType="text" headerText="Serial Number">
							<af:outputText value="#{currentSerialNumber.serialNumber}" />
						</af:column>
						<af:column formatType="text" headerText="Manufacturer Name">
							<af:outputText value="#{currentSerialNumber.manufacturerName}" />
						</af:column>
						<af:column formatType="text"
							headerText="Construction / Installation Commencement Date"
							width="15%">
							<af:outputText value="#{currentSerialNumber.installDate}" />
						</af:column>
						<af:column formatType="text"
							headerText="Operation Commencement / Start-up Date" width="15%">
							<af:outputText
								value="#{currentSerialNumber.operationStartupDate}" />
						</af:column>
						<af:column formatType="text" headerText="Order Date" noWrap="true">
							<af:outputText value="#{currentSerialNumber.orderDate}" />
						</af:column>
						<af:column formatType="text" headerText="Manufacture Date"
							noWrap="true">
							<af:outputText value="#{currentSerialNumber.manufactureDate}" />
						</af:column>
						<af:column formatType="text" headerText="Shutdown Date"
							noWrap="true">
							<af:outputText value="#{currentSerialNumber.shutdownDate}" />
						</af:column>
						<af:column formatType="text" headerText="Removal Date"
							noWrap="true">
							<af:outputText value="#{currentSerialNumber.removalDate}" />
						</af:column>
					</af:table>
					<af:panelForm rows="1" maxColumns="2" labelWidth="35%" width="900"
						inlineStyle="margin-top:10px;">
						<af:selectInputDate id="shutdownDate"
							label="Current Engine Shutdown Date:"
							value="#{facilityProfile.currentEngineSerialNumber.shutdownDate}"
							showRequired="true" readOnly="#{!facilityProfile.editable1}">
							<af:validateDateTimeRange minimum="1900-01-01"
								maximum="#{infraDefs.currentDate}" />
						</af:selectInputDate>

						<af:selectInputDate id="removalDate"
							label="Current Engine Removal Date:"
							value="#{facilityProfile.currentEngineSerialNumber.removalDate}"
							showRequired="true" readOnly="#{!facilityProfile.editable1 }">
							<af:validateDateTimeRange minimum="1900-01-01"
								maximum="#{infraDefs.currentDate}" />
						</af:selectInputDate>
					</af:panelForm>
				</af:showDetailHeader>

				<af:showDetailHeader text="#{facilityProfile.serialNumberText}"
					disclosed="true">
					<af:panelForm rows="2" maxColumns="1" labelWidth="35%" width="900">
						<af:inputText id="serialNumber" label="Serial Number:"
							readOnly="#{! facilityProfile.editable1}"
							value="#{facilityProfile.emissionUnitReplacement.serialNumber}"
							showRequired="true" columns="50" maximumLength="50">
						</af:inputText>

						<af:inputText id="manufacturerName" label="Manufacturer Name:"
							readOnly="#{! facilityProfile.editable1}"
							value="#{facilityProfile.emissionUnitReplacement.manufacturerName}"
							columns="50" maximumLength="50">
						</af:inputText>
					</af:panelForm>
					<af:panelForm rows="2" maxColumns="2" labelWidth="35%" width="900"
						inlineStyle="margin-top:5px;">
						<af:selectInputDate id="installDate"
							label="Construction/Installation Commencement Date:"
							value="#{facilityProfile.emissionUnitReplacement.installDate}"
							readOnly="#{! facilityProfile.editable1}">
							<af:validateDateTimeRange minimum="1900-01-01"
								maximum="#{infraDefs.currentDate}" />
						</af:selectInputDate>

						<af:selectInputDate id="operationStartupDate"
							label="Operation Commencement/Start-up Date:"
							value="#{facilityProfile.emissionUnitReplacement.operationStartupDate}"
							readOnly="#{! facilityProfile.editable1}">
							<af:validateDateTimeRange minimum="1900-01-01"
								maximum="#{infraDefs.currentDate}" />
						</af:selectInputDate>

						<af:selectInputDate id="shutdownDate" label="Shutdown Date:"
							value="#{facilityProfile.emissionUnitReplacement.shutdownDate}"
							rendered="#{facilityProfile.engineSerialTrackingEditMode && facilityProfile.engineSerialNumberStatusEditable}"
							showRequired="true" readOnly="#{!facilityProfile.editable1}">
							<af:validateDateTimeRange minimum="1900-01-01"
								maximum="#{infraDefs.currentDate}" />
						</af:selectInputDate>

						<af:selectInputDate id="orderDate" label="Order Date:"
							value="#{facilityProfile.emissionUnitReplacement.orderDate}"
							readOnly="#{! facilityProfile.editable1}">
							<af:validateDateTimeRange minimum="1900-01-01"
								maximum="#{infraDefs.currentDate}" />
						</af:selectInputDate>

						<af:selectInputDate id="manufactureDate" label="Manufacture Date:"
							value="#{facilityProfile.emissionUnitReplacement.manufactureDate}"
							readOnly="#{! facilityProfile.editable1}">
							<af:validateDateTimeRange minimum="1900-01-01"
								maximum="#{infraDefs.currentDate}" />
						</af:selectInputDate>

						<af:selectInputDate id="removalDate" label="Removal Date:"
							value="#{facilityProfile.emissionUnitReplacement.removalDate}"
							rendered="#{facilityProfile.engineSerialTrackingEditMode && facilityProfile.engineSerialNumberStatusEditable}"
							showRequired="true" readOnly="#{!facilityProfile.editable1 }">
							<af:validateDateTimeRange minimum="1900-01-01"
								maximum="#{infraDefs.currentDate}" />
						</af:selectInputDate>
					</af:panelForm>
				</af:showDetailHeader>
				<af:objectSpacer height="20" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Edit"
							disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
							rendered="#{!facilityProfile.editable1}"
							action="#{facilityProfile.editEngineReplacement}" />
						<af:commandButton text="Save" id="saveButton"
							rendered="#{facilityProfile.editable1}"
							action="#{facilityProfile.saveEngineReplacement}" />
						<af:commandButton text="Cancel" id="cancelButton" immediate="true" 
							rendered="#{facilityProfile.editable1}"
							action="#{facilityProfile.cancelEditEngineReplacement}" />
						<af:commandButton text="Delete"
							disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
							rendered="#{!facilityProfile.editable1 && facilityProfile.stars2Admin}"
							action="#{facilityProfile.deleteEngineReplacement}" />
						<af:commandButton text="Close"
							rendered="#{!facilityProfile.editable1}"
							action="#{facilityProfile.closeAddrDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
