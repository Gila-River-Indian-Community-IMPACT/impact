<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel" value="Emission Unit Type : Loading/Unloading/Dump"
	inlineStyle="font-size: 13px; font-weight: bold;"/>
<af:objectSpacer height="15"/>
<af:panelForm width="600" labelWidth="150" maxColumns="2">
	
	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
		<af:inputText id="maxHourlyThroughput" label="Maximum Hourly Throughput :"
			columns="12" maximumLength="12"
			readOnly="#{!applicationDetail.editMode}" 
			value="#{applicationDetail.selectedEU.euType.maxHourlyThroughput}"/>
		<af:selectOneChoice id="maxHourlyThroughputUnitsCd" label="Units :"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.maxHourlyThroughputUnitsCd}">
			<f:selectItems value="#{applicationReference.appEULUDThroughputUnitsDefs.items[(empty applicationDetail.selectedEU.euType.maxHourlyThroughputUnitsCd ? '' : applicationDetail.selectedEU.euType.maxHourlyThroughputUnitsCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>

	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
		<af:inputText id="detailedDescription" label="Detailed Description of Loading/Unloading/Dump Source :"
			columns="100" rows="5" maximumLength="500" showRequired="true"
			readOnly="#{!applicationDetail.editMode}" 
			value="#{applicationDetail.selectedEU.euType.detailedDescription}"
			tip="*Provide detailed calculations documenting the potential emissions and emission factors used to calculate emissions from this source."/>
	</af:panelForm>
</af:panelForm>