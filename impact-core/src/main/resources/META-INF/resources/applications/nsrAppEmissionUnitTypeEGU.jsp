<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel" value="Emission Unit Type : Electric Generating Unit"
	inlineStyle="font-size: 13px; font-weight: bold;"/>
<af:objectSpacer height="15"/>
<af:panelForm rows="1" width="600" labelWidth="150" maxColumns="2">
	<af:inputText id="btu" label="Btu Content :" columns="12"
		maximumLength="12" readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.btu}" showRequired="true">
		<af:convertNumber pattern=".00" />
	</af:inputText>


	<af:selectOneChoice id="unitsBtuCd" label="Units :"
		readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.unitsBtuCd}"
		showRequired="true">
		<f:selectItems
			value="#{applicationReference.appEUBtuUnitsDefs.items[(empty applicationDetail.selectedEU.euType.unitsBtuCd ? '': applicationDetail.selectedEU.euType.unitsBtuCd)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" width="600" labelWidth="150" maxColumns="2">
	<af:inputText id="fuelSulfur" label="Fuel Sulfur Content :"
		columns="12" maximumLength="12"
		readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.fuelSulfur}"
		showRequired="true">
		<af:convertNumber pattern=".00" />
	</af:inputText>
	<af:selectOneChoice id="unitsFuelSulfurCd" label="Units :"
		readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.unitsFuelSulfurCd}"
		showRequired="true">
		<f:selectItems
			value="#{applicationReference.appEUFuelSulfurDefs.items[(empty applicationDetail.selectedEU.euType.unitsFuelSulfurCd ? '' : applicationDetail.selectedEU.euType.unitsFuelSulfurCd)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" width="600" labelWidth="150" maxColumns="2">
	<af:inputText id="netElectricalOutput"
		label="Net Electrical Output (MW) :" columns="12" maximumLength="12"
		readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.netElectricalOutput}"
		showRequired="true" />
	<af:inputText id="grossElectricalOutput"
		label="Gross Electrical Output (MW) :" columns="12" maximumLength="12"
		readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.grossElectricalOutput}"
		showRequired="true" />
</af:panelForm>


<af:panelForm rows="2" width="600" labelWidth="150" maxColumns="1">
	<af:selectOneChoice id="turbineCycleTypeCd" label="Turbine Cycle Type :"
		readOnly="#{! applicationDetail.editMode}"
		rendered="#{applicationDetail.selectedEU.euType.turbine}"
		value="#{applicationDetail.selectedEU.euType.turbineCycleTypeCd}"
		showRequired="true">
		<f:selectItems
			value="#{applicationReference.appEUTurbineCycleTypeDefs.items[(empty applicationDetail.selectedEU.euType.turbineCycleTypeCd ? '' : applicationDetail.selectedEU.euType.turbineCycleTypeCd)]}" />
	</af:selectOneChoice>

	<af:selectOneChoice id="coalUsageTypeCd" label="Coal Usage Type :"
		readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.coalUsageTypeCd}"
		rendered="#{applicationDetail.selectedEU.euType.coal}"
		showRequired="true">
		<f:selectItems
			value="#{applicationReference.appEUCoalUsageTypeDefs.items[(empty applicationDetail.selectedEU.euType.coalUsageTypeCd ? '' : applicationDetail.selectedEU.euType.coalUsageTypeCd)]}" />
	</af:selectOneChoice>

</af:panelForm>
