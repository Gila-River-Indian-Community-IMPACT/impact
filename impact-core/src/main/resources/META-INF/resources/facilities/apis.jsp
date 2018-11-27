<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{facilityProfile.facilityApisWrapper}"
	binding="#{facilityProfile.facilityApisWrapper.table}"
	bandingInterval="1" banding="row" var="apiGroup" width="98%" id="apiTable">
	<af:column sortable="false" formatType="text" width="150"
		headerText="API">
		<af:commandLink text="#{apiGroup.apiNo}" useWindow="true"
			windowWidth="400" windowHeight="230" inlineStyle="padding:5px;"
			returnListener="#{facilityProfile.dialogDone}"
			action="#{facilityProfile.startToEditFacilityApi}" 
			rendered="#{!facilityProfile.publicApp}" />
		<af:outputText value="#{apiGroup.apiNo}" rendered="#{facilityProfile.publicApp}" />
	</af:column>
	<af:column sortable="false" formatType="text" width="150"
		headerText="Link"
		rendered="#{infraDefs.WOGCCAPIUrlPartAvailable}" >
		<af:goLink text="Show in WOGCC"  
			targetFrame="blank"
			destination="#{apiGroup.WOGCCAPIUrl}" />
	</af:column>
	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton useWindow="true" windowWidth="400"
					windowHeight="230" returnListener="#{facilityProfile.dialogDone}"
					disabled="#{facilityProfile.disabledUpdateButton or facilityProfile.readOnlyUser}"
					rendered="#{!facilityProfile.publicApp}"
					action="#{facilityProfile.startToAddFacilityApi}" text="Add API" />
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>