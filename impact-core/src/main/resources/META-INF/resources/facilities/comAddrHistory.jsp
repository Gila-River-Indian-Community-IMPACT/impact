<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{facilityProfile.addrHistWrapper}" width="98%"
	binding="#{facilityProfile.addrHistWrapper.table}" bandingInterval="1"
	banding="row" var="address">
	<af:column sortProperty="addressId" sortable="true" headerText="ID">
		<af:commandLink text="#{address.addressId}" useWindow="true"
			windowWidth="1200" windowHeight="500" 
			returnListener="#{facilityProfile.dialogDone}"
			action="#{facilityProfile.startEditAddress}"
			rendered="#{!facilityProfile.publicApp}" />
		<af:outputText value="#{address.addressId}" rendered="#{facilityProfile.publicApp}"/>
	</af:column>
	<af:column sortProperty="fullAddress" sortable="true" formatType="text"
		headerText="Physical Address">
		<af:outputText value="#{address.fullAddress}"
			inlineStyle="padding:5px;" />
	</af:column>
	<af:column sortProperty="cityName" sortable="true" formatType="text"
		headerText="City">
		<af:outputText value="#{address.cityName}" inlineStyle="padding:5px;" />
	</af:column>
	<af:column sortProperty="county" sortable="true" formatType="text"
		headerText="County">
		<af:selectOneChoice value="#{address.countyCd}" readOnly="true"
			inlineStyle="padding:5px;">
			<f:selectItems value="#{infraDefs.counties}" />
		</af:selectOneChoice>
	</af:column>
	<af:column sortProperty="googleMapsURL" sortable="true" formatType="text"
		headerText="Lat/Long">
		<af:goLink text="#{address.latlong}" targetFrame="_new"
			rendered="#{not empty address.latlong}"
			destination="#{address.googleMapsURL}" inlineStyle="padding:5px;"
			shortDesc="Clicking this will open Google Maps in a separate tab or window." />
	</af:column>
	<af:column sortProperty="plss" sortable="true" formatType="text"
		headerText="PLSS">
		<af:outputText value="#{address.plss}" inlineStyle="padding:5px;" />
	</af:column>
	<af:column sortProperty="beginDate" sortable="true" formatType="icon"
		headerText="Effective Date">
		<af:selectInputDate value="#{address.beginDate}" readOnly="true"
			inlineStyle="padding:5px;" />
	</af:column>
	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="Create Location" id="AddLocationButton"
					useWindow="true" windowWidth="1200" windowHeight="500"
					disabled="#{facilityProfile.disabledUpdateButton or facilityProfile.readOnlyUser}"
					returnListener="#{facilityProfile.dialogDone}"
					action="#{facilityProfile.startAddAddress}"
					rendered="#{facilityProfile.facility.portable}" />
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>