<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{contactDetail.contactTypesWrapper}"
	bandingInterval="1"
	binding="#{contactDetail.contactTypesWrapper.table}"
	id="ContactTypesTab" banding="row" width="98%" var="contactType"
	rows="#{companyDetail.pageLimit}">
	<af:column sortProperty="c01" sortable="true" formatType="text"
		headerText="Contact Type">
		<af:commandLink
			text="#{infraDefs.contactTypes.itemDesc[(empty contactType.contactTypeCd ? '' : contactType.contactTypeCd)]}"
			id="viewContactType"
			action="#{contactDetail.viewContactTypeFacility}">
			<t:updateActionListener property="#{contactDetail.contactType}"
				value="#{contactType}" />
		</af:commandLink>
	</af:column>
	<af:column sortProperty="c02" sortable="true" noWrap="true"
		formatType="text" headerText="Facility ID">
		<af:commandLink action="#{facilityProfile.submitProfile}"
			text="#{contactType.facilityId}">
			<t:updateActionListener property="#{facilityProfile.fpId}"
				value="#{contactType.fpId}" />
			<t:updateActionListener property="#{menuItem_facProfile.disabled}"
				value="false" />
		</af:commandLink>
	</af:column>
	<af:column sortProperty="c03" sortable="true" formatType="text"
		headerText="Facility Name">
		<af:outputText value="#{contactType.facilityName}" truncateAt="50" shortDesc="#{contactType.facilityName}"/>
	</af:column>
	<af:column sortProperty="cmpId" sortable="true" formatType="text"
		headerText="Company ID">
		<af:commandLink text="#{contactType.cmpId}"
			returnListener="#{facilityProfile.dialogDone}"
			action="#{companyProfile.submitProfile}">
			<t:updateActionListener property="#{companyProfile.cmpId}"
				value="#{contactType.cmpId}" />
		</af:commandLink>
	</af:column>
	<af:column sortProperty="cmpName" sortable="true" formatType="text"
		headerText="Company Name">
		<af:outputText value="#{contactType.companyName}" />
	</af:column>
	<af:column sortProperty="c04" sortable="true" formatType="text"
		headerText="Start Date">
		<af:selectInputDate value="#{contactType.startDate}" readOnly="true" />
	</af:column>
	<af:column sortProperty="c05" sortable="true" formatType="text"
		headerText="End Date">
		<af:selectInputDate value="#{contactType.endDate}" readOnly="true" />
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
