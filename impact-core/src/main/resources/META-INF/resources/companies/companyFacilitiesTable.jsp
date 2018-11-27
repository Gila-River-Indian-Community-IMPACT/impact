<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{companyProfile.cmpFacWrapper}"
	binding="#{companyProfile.cmpFacWrapper.table}" bandingInterval="1"
	banding="row" var="cmpFac" width="98%"
	rows="#{companyProfile.pageLimit}">

	<af:column sortProperty="c01" sortable="true" noWrap="true"
		formatType="text" headerText="Facility ID">
		<af:commandLink action="#{facilityProfile.submitProfile}"
			text="#{cmpFac.facilityId}">
			<t:updateActionListener property="#{facilityProfile.fpId}"
				value="#{cmpFac.fpId}" />
			<t:updateActionListener property="#{menuItem_facProfile.disabled}"
				value="false" />
		</af:commandLink>
	</af:column>

	<af:column sortProperty="c02" sortable="true" formatType="text"
		headerText="Facility Name">
		<af:outputText value="#{cmpFac.name}" />
	</af:column>


	<af:column sortProperty="c04" sortable="true" formatType="text"
		headerText="Operating">
		<af:outputText
			value="#{facilityReference.operatingStatusDefs.itemDesc[(empty cmpFac.operatingStatusCd ? '' : cmpFac.operatingStatusCd)]}" />
	</af:column>

	<af:column sortProperty="c05" sortable="true" formatType="text"
		headerText="Facility Class">
		<af:outputText
			value="#{facilityReference.permitClassDefs.itemDesc[(empty cmpFac.permitClassCd ? '' : cmpFac.permitClassCd)]}" />
	</af:column>

	<af:column sortProperty="c06" sortable="true" formatType="text"
		headerText="Facility Type">
		<af:outputText
			value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty cmpFac.facilityTypeCd ? '' : cmpFac.facilityTypeCd)]}" />
	</af:column>

	<af:column sortProperty="c07" sortable="true" formatType="text"
		headerText="County">
		<af:selectOneChoice value="#{cmpFac.countyCd}" readOnly="true">
			<f:selectItems value="#{infraDefs.counties}" />
		</af:selectOneChoice>
	</af:column>

	<af:column sortProperty="c08" sortable="true" formatType="text"
		headerText="Lat/Long">
		<af:goLink text="#{cmpFac.phyAddr.latlong}" targetFrame="_new"
			rendered="#{not empty cmpFac.googleMapsURL}"
			destination="#{cmpFac.googleMapsURL}"
			shortDesc="Clicking this will open Google Maps in a separate tab or window." />
	</af:column>

	<af:column sortProperty="startDate" sortable="true" formatType="text"
		headerText="Start Date">
		<af:selectInputDate value="#{cmpFac.startDate}" readOnly="true" />
	</af:column>

	<af:column sortProperty="endDate" sortable="true" formatType="text"
		headerText="End Date">
		<af:selectInputDate value="#{cmpFac.endDate}" readOnly="true" />
	</af:column>

	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>
