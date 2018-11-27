<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel" value="Emission Unit Type : Incinerator"
	inlineStyle="font-size: 13px; font-weight: bold;" />
<af:objectSpacer height="15" />
<af:panelForm width="600" labelWidth="150" maxColumns="2">
	<af:panelForm rows="1" maxColumns="2" width="600" labelWidth="150">
		<af:selectOneChoice id="primaryFuelType" label="Primary Fuel Type :"
			readOnly="#{!applicationDetail.editMode}" showRequired="true"
			value="#{applicationDetail.selectedEU.euType.primaryFuelType}">
			<f:selectItems
				value="#{applicationReference.appEUINCPriFuelTypeDefs.items[(empty applicationDetail.selectedEU.euType.primaryFuelType ? '' : applicationDetail.selectedEU.euType.primaryFuelType)]}" />
		</af:selectOneChoice>
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="2" width="600" labelWidth="150">
		<af:inputText id="btuContent" label="Btu Content :" columns="12"
			maximumLength="12" showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.btuContent}">
			<af:convertNumber pattern=".00" />
		</af:inputText>
		<af:selectOneChoice id="btuContentUnitsCd" label="Units :"
			readOnly="#{!applicationDetail.editMode}" showRequired="true"
			value="#{applicationDetail.selectedEU.euType.btuContentUnitsCd}">
			<f:selectItems
				value="#{applicationReference.appEUBtuUnitsDefs.items[(empty applicationDetail.selectedEU.euType.btuContentUnitsCd ? '' : applicationDetail.selectedEU.euType.btuContentUnitsCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="2" width="600" labelWidth="150">
		<af:inputText id="fuelSulfurContent" label="Fuel Sulfur Content :"
			columns="12" maximumLength="12" showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.fuelSulfurContent}">
			<af:convertNumber pattern=".00" />
		</af:inputText>
		<af:selectOneChoice id="fuelSulfurContentUnitsCd" label="Units :"
			readOnly="#{!applicationDetail.editMode}" showRequired="true"
			value="#{applicationDetail.selectedEU.euType.fuelSulfurContentUnitsCd}">
			<f:selectItems
				value="#{applicationReference.appEUFuelSulfurDefs.items[(empty applicationDetail.selectedEU.euType.fuelSulfurContentUnitsCd ? '' : applicationDetail.selectedEU.euType.fuelSulfurContentUnitsCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>
</af:panelForm>