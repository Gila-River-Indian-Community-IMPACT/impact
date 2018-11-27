<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:outputText id="euTypeLabel" value="Emission Unit Type : Boiler"
	inlineStyle="font-size: 13px; font-weight: bold;" />
<af:objectSpacer height="15" />

<af:panelForm width="600" labelWidth="150" maxColumns="2">


	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
		<af:selectOneChoice id="boilerType" label="Boiler Type :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.boilerTypeCd}"
			showRequired="true">
			<f:selectItems
				value="#{applicationReference.appEUBoilerTypeDefs.items[(empty applicationDetail.selectedEU.euType.boilerTypeCd ? '' : applicationDetail.selectedEU.euType.boilerTypeCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>

	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
		<af:inputText id="btuContent" label="Btu Content :" columns="12"
			maximumLength="12" readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.btuContent}"
			showRequired="true">
			<af:convertNumber pattern=".00" />
		</af:inputText>

		<af:selectOneChoice id="bolBtuUnitsCd" label="Units :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.bolBtuUnitsCd}"
			showRequired="true">
			<f:selectItems
				value="#{applicationReference.appEUBtuUnitsDefs.items[(empty applicationDetail.selectedEU.euType.bolBtuUnitsCd ? '' : applicationDetail.selectedEU.euType.bolBtuUnitsCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>

	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
		<af:inputText id="fuelSulfurContent" label="Fuel Sulfur Content :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.fuelSulfurContent}"
			showRequired="true">
			<af:convertNumber pattern=".00" />
		</af:inputText>

		<af:selectOneChoice id="bolSulfurUnitsCd" label="Units :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.bolSulfurUnitsCd}"
			showRequired="true">
			<f:selectItems
				value="#{applicationReference.appEUFuelSulfurDefs.items[(empty applicationDetail.selectedEU.euType.bolSulfurUnitsCd ? '' : applicationDetail.selectedEU.euType.bolSulfurUnitsCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
		<af:inputText id="fuelAshContent" label="Fuel Ash Content (%) :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.fuelAshContent}"
			showRequired="true" />
		<af:selectOneChoice id="serviceTypeCd" label="Type of Service :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.serviceTypeCd}"
			showRequired="true">
			<f:selectItems
				value="#{applicationReference.appEUBOLServiceTypeDefs.items[(empty applicationDetail.selectedEU.euType.serviceTypeCd ? '' : applicationDetail.selectedEU.euType.serviceTypeCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>

</af:panelForm>
