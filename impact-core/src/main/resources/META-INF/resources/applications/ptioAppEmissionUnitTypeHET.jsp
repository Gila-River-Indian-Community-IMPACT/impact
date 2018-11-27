<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel"
	value="Emission Unit Type : Heater/Chiller"
	inlineStyle="font-size: 13px; font-weight: bold;" />
<af:objectSpacer height="15" />
<af:panelForm rows="1" maxColumns="2" labelWidth="200" width="600">
	<af:inputText id="fuelsulfur" label="Fuel Sulfur Content :"
		columns="15" maximumLength="12"
		readOnly="#{!applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.fuelsulfur}">
	</af:inputText>
	<af:selectOneChoice id="unitsFuelSulfurCd" label="Units :"
		readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.unitsFuelSulfurCd}" unselectedLabel="">
		<f:selectItems
			value="#{applicationReference.appEUFuelSulfurDefs.items[(empty applicationDetail.selectedEU.euType.unitsFuelSulfurCd ? '' : applicationDetail.selectedEU.euType.unitsFuelSulfurCd)]}" />
	</af:selectOneChoice>
</af:panelForm>