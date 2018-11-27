
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table id="applicationEUFugitiveLeaksTable" emptyText=" "
	var="applicationEUFugitiveLeaks" bandingInterval="1" banding="row"
	value="#{applicationDetail.selectedEU.applicationEUFugitiveLeaks}"
	width="98%" varStatus="applicationEUFugitiveLeaksTableVs">
	<af:column id="edit" formatType="text" headerText="ID">
		<af:commandLink text="#{address.addressId}" useWindow="true"
			windowWidth="1080" windowHeight="500"
			returnListener="#{facilityProfile.dialogDone}"
			action="#{applicationDetail.startToEditApplicationEUFugitiveLeaks}">
			<af:inputText value="#{applicationEUFugitiveLeaksTableVs.index+1}"
				readOnly="true">
				<af:convertNumber pattern="000" />
			</af:inputText>
			<t:updateActionListener
				property="#{applicationDetail.applicationEUFugitiveLeaks}"
				value="#{applicationEUFugitiveLeaks}" />
		</af:commandLink>
	</af:column>
	<af:column formatType="text" headerText="Equipment and Service Type"
		id="equipmentServiceType">
		<af:selectOneChoice id="equipmentServiceTypeCd" readOnly="true"
			value="#{applicationEUFugitiveLeaks.equipmentServiceTypeCd}"
			showRequired="true">
			<f:selectItems
				value="#{applicationReference.appEUEquipServiceTypeDefs.items[(empty applicationDetail.applicationEUFugitiveLeaks.equipmentServiceTypeCd ? '' : applicationDetail.applicationEUFugitiveLeaks.equipmentServiceTypeCd)]}" />
		</af:selectOneChoice>
	</af:column>
	<af:column formatType="number"
		headerText="Number of New or Modified Equipment Types" id="equipmentTypeNo">
		<af:outputText
			value="#{applicationEUFugitiveLeaks.equipmentTypeNumber}" />
	</af:column>
	<af:column formatType="number" headerText="Leak Rate (ppm)"
		id="leakRate">
		<af:outputText id="leakRate"
			value="#{applicationEUFugitiveLeaks.leakRate}">
			<af:convertNumber pattern=".00" />
		</af:outputText>
	</af:column>
	<af:column formatType="number" headerText="Percent VOC" id="perVoc">
		<af:outputText value="#{applicationEUFugitiveLeaks.percentVoc}" />
	</af:column>
	<f:facet name="footer">
		<af:panelButtonBar>
			<af:commandButton text="Add" id="addApplicationEUFugitiveLeaks"
				useWindow="true" windowWidth="500" windowHeight="350" 
				rendered="#{applicationDetail.editMode}"
				returnListener="#{applicationDetail.emissionsDialogDone}"
				action="#{applicationDetail.startToAddApplicationEUFugitiveLeaks}">
			</af:commandButton>
			<af:commandButton actionListener="#{tableExporter.printTable}"
				onclick="#{tableExporter.onClickScript}" text="Printable view" />
			<af:commandButton actionListener="#{tableExporter.excelTable}"
				onclick="#{tableExporter.onClickScript}" text="Export to excel" />
		</af:panelButtonBar>
	</f:facet>
</af:table>
