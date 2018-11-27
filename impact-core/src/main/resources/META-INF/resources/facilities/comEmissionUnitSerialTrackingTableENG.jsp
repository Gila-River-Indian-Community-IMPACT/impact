<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:objectSpacer height="25" />
<af:outputText styleClass="x3s" value="Serial Number Tracking" />
<af:table id="emissionUnitReplacementTable"
	value="#{facilityProfile.emissionUnit.emissionUnitReplacements}"
	width="98%" bandingInterval="1" banding="row"
	varStatus="emissionUnitReplacementTableVs"
	var="emissionUnitReplacement" rows="#{facilitySearch.pageLimit}">
	<af:column id="edit" formatType="text" headerText="Row Id"
		noWrap="true">
		<af:commandLink useWindow="true" windowWidth="1080" windowHeight="500"
			returnListener="#{facilityProfile.dialogDone}"
			rendered="#{!facilityProfile.publicApp}"
			action="#{facilityProfile.startToEditEngineReplacement}"
			actionListener="#{facilityProfile.saveEngine}">
			<af:inputText value="#{emissionUnitReplacementTableVs.index+1}"
				readOnly="true">
				<af:convertNumber pattern="000" />
			</af:inputText>
			<t:updateActionListener
				property="#{facilityProfile.emissionUnitReplacement}"
				value="#{emissionUnitReplacement}" />
		</af:commandLink>
		<af:outputText value="#{emissionUnitReplacementTableVs.index+1}" rendered="#{facilityProfile.publicApp}">
			<af:convertNumber pattern="000" />
		</af:outputText>
	</af:column>
	<af:column formatType="text" headerText="Serial Number">
		<af:outputText value="#{emissionUnitReplacement.serialNumber}" />
	</af:column>
	<af:column formatType="text" headerText="Manufacturer Name">
		<af:outputText value="#{emissionUnitReplacement.manufacturerName}" />
	</af:column>
	<af:column formatType="text"
		headerText="Construction / Installation Commencement Date" width="15%">
		<af:outputText value="#{emissionUnitReplacement.installDate}" />
	</af:column>
	<af:column formatType="text"
		headerText="Operation Commencement / Start-up Date" width="20%">
		<af:outputText value="#{emissionUnitReplacement.operationStartupDate}" />
	</af:column>
	<af:column formatType="text" headerText="Order Date" noWrap="true">
		<af:outputText value="#{emissionUnitReplacement.orderDate}" />
	</af:column>
	<af:column formatType="text" headerText="Manufacture Date"
		noWrap="true">
		<af:outputText value="#{emissionUnitReplacement.manufactureDate}" />
	</af:column>
	<af:column formatType="text" headerText="Shutdown Date" noWrap="true">
		<af:outputText value="#{emissionUnitReplacement.shutdownDate}" />
	</af:column>
	<af:column formatType="text" headerText="Removal Date" noWrap="true">
		<af:outputText value="#{emissionUnitReplacement.removalDate}" />
	</af:column>


	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="Add/Replace Current Engine"
					id="AddReplacementButton" useWindow="true" windowWidth="1080"
					windowHeight="500" returnListener="#{facilityProfile.dialogDone}"
					actionListener="#{facilityProfile.saveEngine}"
					disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
					action="#{facilityProfile.startToAddEngineReplacement}" />
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>
<af:outputText styleClass="x3s"
	value="*There must be at least one entry in the Serial Number Tracking table." />
