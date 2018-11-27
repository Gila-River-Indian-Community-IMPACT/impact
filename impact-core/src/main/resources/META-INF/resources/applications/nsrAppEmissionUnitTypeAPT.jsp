<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:outputText id="euTypeLabel" value="Emission Unit Type : Acid Plant/Prill Tower"
		inlineStyle="font-size: 13px; font-weight: bold;"/>
<af:objectSpacer height="15"/>
<af:panelForm rows="1" width="600" labelWidth="150" maxColumns="2"
	partialTriggers="unitTypeCd">
	<af:selectOneChoice id="unitTypeCd" label="Unit Type :"
		readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.unitTypeCd}"
		showRequired="true" autoSubmit="true">
		<f:selectItems
			value="#{applicationReference.appEUAPTUnitTypeDefs.items[(empty applicationDetail.selectedEU.euType.unitTypeCd ? '': applicationDetail.selectedEU.euType.unitTypeCd)]}" />
	</af:selectOneChoice>
	<af:inputText id="exhaustFlowRate" label="Exhaust Flow Rate (acfm) :"
		rendered="#{applicationDetail.selectedEU.euType.prillTower}"
		columns="12" maximumLength="12"
		readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.exhaustFlowRate}"
		showRequired="true">
		<af:convertNumber pattern=".00" />
	</af:inputText>
</af:panelForm>



