<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel" value="Emission Unit Type : Storage Tank/Silo"
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
			<f:selectItems value="#{applicationReference.appEUTNKThroughputUnitsDefs.items[(empty applicationDetail.selectedEU.euType.maxHourlyThroughputUnitsCd ? '' : applicationDetail.selectedEU.euType.maxHourlyThroughputUnitsCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="1" width="600" labelWidth="150">
		<af:selectOneChoice id="isTankHeated" label="Is Tank Heated :"
			showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.isTankHeated}">
			<f:selectItem itemLabel="Yes" itemValue="Yes"/>
			<f:selectItem itemLabel="No" itemValue="No"/>
		</af:selectOneChoice>
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="2" width="600" labelWidth="150"
		rendered="#{applicationDetail.selectedEU.euType.fpEuMaterialTypeStored == 'Liquid'}">
		<af:inputText id="operatingPressure" label="Operating Pressure (psig) :"
			columns="12" maximumLength="12" showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.operatingPressure}">
			<af:convertNumber pattern=".00"/>
		</af:inputText>
		<af:inputText id="vaporPressureOfMaterialStored" label="Vapor Pressure of Material Stored (psig) :"
			columns="12" maximumLength="12" showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.vaporPressureOfMaterialStored}">
			<af:convertNumber pattern=".00"/>
		</af:inputText>
	</af:panelForm>	
</af:panelForm>