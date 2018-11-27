<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel"
	value="Emission Unit Type : Cooling Tower"
	inlineStyle="font-size: 13px; font-weight: bold;" />
<af:objectSpacer height="15" />
<af:panelForm>
	<af:panelForm maxColumns="2" rows="1" labelWidth="150px" width="600px">
		<af:inputText id="cellFlowRate" label="Cell Flow Rate (cu. ft/min) :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.cellFlowRate}"
			showRequired="true" />

		<af:inputText id="circulationRate"
			label="Circulation Rate (gallons/min) :" columns="12"
			maximumLength="12" readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.circulationRate}"
			showRequired="true" />
	</af:panelForm>

	<af:panelForm maxColumns="2" rows="1" labelWidth="150px" width="600px">
		<af:inputText id="voc" label="VOC Content (%) :" columns="12"
			maximumLength="12" readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.voc}"
			showRequired="true" />

		<af:inputText id="hap" label="HAP Content (%) :" columns="12"
			maximumLength="12" readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.hap}"
			showRequired="true" />
	</af:panelForm>

	<af:panelForm maxColumns="2" rows="1" labelWidth="150px" width="600px">
		<af:inputText id="cellNumber" label="Number of cells :" columns="12"
			maximumLength="12" readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.cellNumber}"
			showRequired="true" />
	</af:panelForm>
</af:panelForm>
