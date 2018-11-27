<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel"
	value="Emission Unit Type : Separator/Treater"
	inlineStyle="font-size: 13px; font-weight: bold;" />
<af:objectSpacer height="15" />

<af:panelForm rows="1" width="600" labelWidth="150" maxColumns="2">
	<af:inputText id="operatingTemperature"
		label="Operating Temperature (F) :" columns="12" maximumLength="12"
		readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.operatingTemperature}"
		showRequired="true" />

	<af:inputText id="operatingPressure"
		label="Operating Pressure (psig) :" columns="12" maximumLength="12"
		readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.operatingPressure}"
		showRequired="true">
		<af:convertNumber pattern=".00" />
	</af:inputText>
</af:panelForm>

