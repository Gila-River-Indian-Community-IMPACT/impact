<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel"
	value="Emission Unit Type : Process Vent"
	inlineStyle="font-size: 13px; font-weight: bold;" />
<af:objectSpacer height="15" />
<af:panelForm>
	<af:panelForm maxColumns="2" rows="1" labelWidth="150px" width="600px">
		<af:inputText id="flowRateThroughput"
			label="Flow Rate or Throughput :" columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.flowRateThroughput}"
			showRequired="true" />

		<af:selectOneChoice id="flowRateThroughputUnitsCd" label="Units :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.flowRateThroughputUnitsCd}"
			unselectedLabel="Please select" showRequired="true">
			<f:selectItems
				value="#{applicationReference.appEUFlowRateUnitsDefs.items[(empty applicationDetail.selectedEU.euType.flowRateThroughputUnitsCd? '': applicationDetail.selectedEU.euType.flowRateThroughputUnitsCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>

	<af:panelForm maxColumns="2" rows="1" labelWidth="150px" width="600px">
		<af:inputText id="vocConc" label="VOC Concentration (%) :"
			columns="12" maximumLength="14"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.vocConc}"
			showRequired="true" />

		<af:inputText id="hapsConc" label="HAPs Concentration (%) :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.hapsConc}"
			showRequired="true" />
	</af:panelForm>
</af:panelForm>