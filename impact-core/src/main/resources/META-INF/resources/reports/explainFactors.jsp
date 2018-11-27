<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<af:showDetailHeader text="Factor(s)" size="1"
	disclosed="#{!reportProfile.emissionRowMethod.emissionsMethod}">
	<af:outputFormatted styleUsage="instruction"
		value="The following are WebFIRE factors available for calculating this pollutant's emissions."
		rendered="#{reportProfile.fireWrapper.table.rowCount > 0}" />
	<af:outputFormatted styleUsage="instruction"
		value="There are no WebFIRE factors available for calculating this pollutant’s emissions. WebFIRE factors are based on the SCC associated with this Emissions Process in the Facility Inventory."
		rendered="#{reportProfile.fireWrapper.table.rowCount < 1}" />
	<af:objectSpacer height="10px" />
	<af:table value="#{reportProfile.fireWrapper}" bandingInterval="1"
		rendered="#{reportProfile.fireWrapper.table.rowCount > 0}"
		emptyText="No applicable FIRE Factors. Contact your EPA Agent."
		binding="#{reportProfile.fireWrapper.table}" id="fireTab"
		banding="row" width="98%" var="fireLine">

		<af:column sortable="true" sortProperty="selected" formatType="icon"
			headerText="Select"
			rendered="#{reportProfile.fireWrapper.table.rowCount > 1}">
			<af:selectBooleanRadio group="RadioButtons" rendered="true"
				readOnly="#{!reportProfile.fireEditable || reportProfile.closedForEdit}"
				value="#{fireLine.selected}">
			</af:selectBooleanRadio>
		</af:column>

		<af:column formatType="text" sortProperty="factorId" sortable="true"
			headerText="Fire ID">
			<af:outputText value="#{fireLine.factorId}" />
			<af:outputText value="expired FIRE factor"
				rendered="#{fireLine.notActive}"
				inlineStyle="color: orange; font-weight: bold;"
				shortDesc="This FIRE factor is no longer active. Pick a different FIRE factor or different calculation method">
			</af:outputText>
		</af:column>
		<af:column headerText="Pollutant">
			<af:column sortProperty="pollutantCd" sortable="true" formatType="text"
				headerText="Name">
				<af:selectOneChoice value="#{fireLine.pollutantCd}" readOnly="true">
					<f:selectItems
						value="#{facilityReference.pollutantDefs.items[(empty fireLine.pollutantCd ? '' : fireLine.pollutantCd)]}" />
				</af:selectOneChoice>
			</af:column>
			<af:column sortProperty="pollutantCd" sortable="true" formatType="text"
				headerText="Code">
				<af:outputText value="#{fireLine.pollutantCd}" />
			</af:column>
		</af:column>
		<af:column formatType="text" sortProperty="material" sortable="true"
			headerText="Material">
			<af:selectOneChoice value="#{fireLine.material}" readOnly="true">
				<f:selectItems
					value="#{facilityReference.materialDefs.items[(empty fireLine.material ? '' : fireLine.material)]}" />
			</af:selectOneChoice>
		</af:column>
		<af:column formatType="text" sortProperty="action" sortable="true"
			headerText="Action">
			<af:outputText value="#{fireLine.action}" />
		</af:column>
		<af:column formatType="text" sortProperty="measure" sortable="true"
			headerText="Units">
			<af:selectOneChoice value="#{fireLine.measure}" readOnly="true">
				<f:selectItems
					value="#{facilityReference.unitDefs.items[(empty fireLine.measure ? '' : fireLine.measure)]}" />
			</af:selectOneChoice>
		</af:column>
		<af:column headerText="Uncontrolled Emissions Factor" sortProperty="uncontrolledEmissionsFactor" sortable="false">
			<af:column formatType="text" sortProperty="factor" sortable="true"
				headerText="Factor">
				<af:outputText value="#{fireLine.factor}" />
			</af:column>
			<af:column formatType="text" sortProperty="formula" sortable="true"
				headerText="Formula">
				<af:outputText value="#{fireLine.formula}" />
			</af:column>
			<af:column formatType="text" sortProperty="unit" sortable="true"
				headerText="Numerator">
				<af:selectOneChoice value="#{fireLine.unit}" readOnly="true">
					<f:selectItems
						value="#{facilityReference.unitDefs.items[(empty fireLine.unit ? '' : fireLine.unit)]}" />
				</af:selectOneChoice>
			</af:column>
			<af:column formatType="text" sortProperty="measure" sortable="true"
				headerText="Denominator">
				<af:selectOneChoice value="#{fireLine.measure}" readOnly="true">
					<f:selectItems
						value="#{facilityReference.unitDefs.items[(empty fireLine.measure ? '' : fireLine.measure)]}" />
				</af:selectOneChoice>
			</af:column>
		</af:column>
		<af:column sortProperty="notes" sortable="true" headerText="Notes">
			<af:outputText value="#{fireLine.notes}" />
		</af:column>
		<af:column formatType="text" sortProperty="quality" sortable="true"
			headerText="Quality">
			<af:outputText value="#{fireLine.quality}" />
		</af:column>
		<af:column formatType="text" sortProperty="origin" sortable="true"
			headerText="Origin">
			<af:outputText value="#{fireLine.origin}" />
		</af:column>
		<af:column formatType="icon" sortProperty="created" sortable="true"
			headerText="First Active">
			<af:outputText value="#{fireLine.created}" />
		</af:column>
		<af:column formatType="icon" sortProperty="deprecated" sortable="true"
			headerText="First Inactive">
			<af:outputText value="#{fireLine.deprecated}" />
		</af:column>
		<af:column formatType="text" sortProperty="ap42Section" sortable="true"
			headerText="AP42 Section">
			<af:outputText value="#{fireLine.ap42Section}" />
		</af:column>
		<af:column formatType="text" sortProperty="refDesc" sortable="true"
			headerText="Reference Description">
			<af:outputText value="#{fireLine.refDesc}" />
		</af:column>
		<f:facet name="footer">
			<afh:rowLayout halign="center">
				<af:panelButtonBar>
					<af:commandButton actionListener="#{tableExporter.printTable}"
						onclick="#{tableExporter.onClickScript}" text="Printable view" />
					<af:commandButton actionListener="#{tableExporter.excelTable}"
						onclick="#{tableExporter.onClickScript}" text="Export to excel" />
				</af:panelButtonBar>
			</afh:rowLayout>
		</f:facet>
	</af:table>
</af:showDetailHeader>
