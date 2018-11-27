<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel" value="Emission Unit Type : Tail Gas Treatment"
	inlineStyle="font-size: 13px; font-weight: bold;"/>
<af:objectSpacer height="15"/>
<af:panelForm>
	<af:panelForm maxColumns="2" rows="1" labelWidth="150px" width="600px">
		<af:inputText id="exhaustFlowRate" label="Exhaust Flow Rate (acfm/hr) :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.exhaustFlowRate}"
			showRequired="true" />
	</af:panelForm>

	<af:panelForm maxColumns="2" rows="1" labelWidth="150px" width="600px">
		<af:inputText id="inletSulfurConc"
			label="Inlet Sulfur Concentration (%) :" columns="12"
			maximumLength="12" readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.inletSulfurConc}"
			showRequired="true" />

		<af:inputText id="outletSulfurConc"
			label="Outlet Sulfur Concentration (%) :" columns="12"
			maximumLength="12" readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.outletSulfurConc}"
			showRequired="true" />
	</af:panelForm>
</af:panelForm>