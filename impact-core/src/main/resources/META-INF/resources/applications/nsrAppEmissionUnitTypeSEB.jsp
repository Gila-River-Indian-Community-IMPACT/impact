<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel"
	value="Emission Unit Type : Spray Booth/Electroplating/Sand Blasting"
	inlineStyle="font-size: 13px; font-weight: bold;" />
<af:objectSpacer height="10" />
<af:outputText id="unitTypeLabel"
	value="Unit Type : #{applicationDetail.selectedEU.euType.fpEuUnitType}"
	inlineStyle="font-size: 13px; font-weight: bold;" />
<af:objectSpacer height="15" />

<af:panelForm rows="1" width="600" labelWidth="150" maxColumns="2">
	<af:inputText id="materialUsage" label="Material Usage :" columns="12"
		maximumLength="12" readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.materialUsage}"
		showRequired="true">
		<af:convertNumber pattern=".00" />
	</af:inputText>


	<af:selectOneChoice id="unitMaterialUsageCd" label="Units :"
		readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.unitMaterialUsageCd}"
		showRequired="true">
		<f:selectItems
			value="#{applicationReference.appEUMateUsageDefs.items[(empty applicationDetail.selectedEU.euType.unitMaterialUsageCd ? '': applicationDetail.selectedEU.euType.unitMaterialUsageCd)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" width="600" labelWidth="150" maxColumns="2">
	<af:inputText id="voc" label="VOC Content (%) :" columns="12"
		maximumLength="12" readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.voc}" showRequired="true" />

	<af:inputText id="haps" label="HAPs Content (%) :" columns="12"
		maximumLength="12" readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.haps}"
		showRequired="true" />

</af:panelForm>

