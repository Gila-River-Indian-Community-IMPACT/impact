<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{facilityProfile.naicsCdsWrapper}"
	binding="#{facilityProfile.naicsCdsWrapper.table}" bandingInterval="1"
	banding="row" var="naics" width="98%">
	<af:column sortable="false" formatType="text" width="150"
		headerText="NAICS">
		<af:commandLink useWindow="true" windowWidth="760"
			windowHeight="300" inlineStyle="padding-left:5px;"
			rendered="#{!facilityProfile.publicApp}"
			returnListener="#{facilityProfile.dialogDone}"
			action="#{facilityProfile.startToEditFacilityNaics}">
			<af:selectOneChoice value="#{naics.value}" 
				readOnly="true" inlineStyle="padding:0px;">
				<f:selectItems value="#{infraDefs.naicsSelectDefs}" />
			</af:selectOneChoice>	
		</af:commandLink>
		<af:outputText value="#{naics.value}" rendered="#{facilityProfile.publicApp}" />
	</af:column>
	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton useWindow="true" windowWidth="760"
					windowHeight="300" returnListener="#{facilityProfile.dialogDone}"
					disabled="#{facilityProfile.disabledUpdateButton}"
					rendered="#{!facilityProfile.publicApp}"
					action="#{facilityProfile.startToAddFacilityNaics}"
					text="Add NAICS" />
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>
<afh:rowLayout halign="center">
	<af:goLink text="NAICS reference information" targetFrame="_new"
		rendered="#{!facilityProfile.editable1 && !facilityProfile.publicApp}"
		destination="../util/externalReferences.jsf" />
</afh:rowLayout>