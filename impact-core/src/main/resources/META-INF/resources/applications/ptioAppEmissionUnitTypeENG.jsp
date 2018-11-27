<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel" value="Emission Unit Type : Engine"
	inlineStyle="font-size: 13px; font-weight: bold;" />
<af:objectSpacer height="15" />
<af:panelForm width="600" labelWidth="150" maxColumns="2">

	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
		<af:inputText id="btuContent" label="Btu Content :" columns="12"
			maximumLength="12" readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.btuContent}"
			showRequired="true">
			<af:convertNumber pattern=".00" />
		</af:inputText>

		<af:selectOneChoice id="engBtuUnitsCd" label="Units :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.engBtuUnitsCd}"
			showRequired="true">
			<f:selectItems
				value="#{applicationReference.appEUBtuUnitsDefs.items[(empty applicationDetail.selectedEU.euType.engBtuUnitsCd ? '' : applicationDetail.selectedEU.euType.engBtuUnitsCd)]}" />
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

		<af:selectOneChoice id="engSulfarUnitsCd" label="Units :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.engSulfarUnitsCd}"
			showRequired="true">
			<f:selectItems
				value="#{applicationReference.appEUFuelSulfurDefs.items[(empty applicationDetail.selectedEU.euType.engSulfarUnitsCd ? '' : applicationDetail.selectedEU.euType.engSulfarUnitsCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
		<af:selectOneChoice id="serviceType" label="Type of Service :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.serviceType}"
			showRequired="true">
			<f:selectItems
				value="#{applicationReference.appEUNEGServiceTypeDefs.items[(empty applicationDetail.selectedEU.euType.serviceType ? '' : applicationDetail.selectedEU.euType.serviceType)]}" />
		</af:selectOneChoice>
	</af:panelForm>

<af:panelGroup partialTriggers="dieselEngineEpaTierCertified"
		rendered="#{applicationDetail.selectedEU.euType.fpEuPrimaryFuelType == 'Diesel' ||
					applicationDetail.selectedEU.euType.fpEuSecondaryFuelType == 'Diesel'}">
	<af:panelForm rows="1" width="600" labelWidth="150" maxColumns="2">
			<af:selectOneChoice id="dieselEngineEpaTierCertified" showRequired="true"
				valueChangeListener="#{applicationDetail.refreshAttachments}" 
				label="Is diesel engine EPA Tier Certified?"
				readOnly="#{! applicationDetail.editMode}" unselectedLabel=" "
				value="#{applicationDetail.selectedEU.euType.dieselEngineEpaTierCertifiedFlag}" autoSubmit="true">
				<af:selectItem label="Yes" value="Y" />
				<af:selectItem label="No" value="N" />
		</af:selectOneChoice>
	</af:panelForm>
	
	<af:panelForm rows="1" width="600" labelWidth="150" maxColumns="2"
		rendered="#{applicationDetail.selectedEU.euType.dieselEngineEpaTierCertified}">
			<af:selectOneChoice id="tierRating" showRequired="true"
				valueChangeListener="#{applicationDetail.refreshAttachments}" 
				label="Tier Rating:"
				readOnly="#{! applicationDetail.editMode}" unselectedLabel=" "
				value="#{applicationDetail.selectedEU.euType.tierRating}" autoSubmit="true">
			<f:selectItems
				value="#{applicationReference.appEUENGTierRatingDefs.items[(empty applicationDetail.selectedEU.euType.tierRating ? '' : applicationDetail.selectedEU.euType.tierRating)]}" />
			</af:selectOneChoice>
	</af:panelForm>
</af:panelGroup>


</af:panelForm>
