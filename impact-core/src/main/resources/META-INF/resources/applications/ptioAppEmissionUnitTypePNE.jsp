<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel"
	value="Emission Unit Type : Pneumatic Equipment"
	inlineStyle="font-size: 13px; font-weight: bold;" />
<af:objectSpacer height="15" />
<af:panelForm rows="1" maxColumns="1" labelWidth="275" width="750">
	<af:inputText id="newOrModifiedEqpCnt" label="Count of New or Modified Equipment :" columns="5"
		maximumLength="3" readOnly="#{!applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.newOrModifiedEqpCnt}" showRequired="true" />

	<af:selectOneChoice id="motiveForceCd" label="Motive Force :"
		readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.motiveForceCd}"
		showRequired="true">
		<f:selectItems
			value="#{applicationReference.appEUMotiveForceDefs.items[(empty applicationDetail.selectedEU.euType.motiveForceCd ? '' : applicationDetail.selectedEU.euType.motiveForceCd)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="275" width="750">
	<af:inputText id="voc" label="VOC Content (%) :" columns="25"
		maximumLength="12" readOnly="#{!applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.voc}" showRequired="true" />

	<af:inputText id="hap" label="HAP Content (%) :" columns="25"
		maximumLength="12" readOnly="#{!applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.hap}" showRequired="true" />
</af:panelForm>