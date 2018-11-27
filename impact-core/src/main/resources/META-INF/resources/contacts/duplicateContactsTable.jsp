<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{contactDetail.possibleDuplicatesWrapper}"
	binding="#{contactDetail.possibleDuplicatesWrapper.table}"
	bandingInterval="1" banding="row" var="contact"
	rows="#{mergeContact.pageLimit}">

	<af:column sortProperty="c01" sortable="true" noWrap="true"
		formatType="text" headerText="Contact ID">
		<af:commandLink action="#{contactDetail.submitDetail}"
			text="#{contact.cntId}">
			<t:updateActionListener property="#{contactDetail.contactId}"
				value="#{contact.contactId}" />
			<t:updateActionListener property="#{menuItem_contactDetail.disabled}"
				value="false" />
		</af:commandLink>
	</af:column>

	<af:column sortProperty="c02" sortable="true" formatType="text"
		headerText="Last Name">
		<af:outputText value="#{contact.lastNm}" />
	</af:column>

	<af:column sortProperty="c03" sortable="true" formatType="text"
		headerText="First Name">
		<af:outputText value="#{contact.firstNm}" />
	</af:column>

	<af:column sortProperty="c04" sortable="true" formatType="text"
		headerText="Preferred Name">
		<af:outputText value="#{contact.preferredName}" />
	</af:column>

	<af:column sortProperty="c05" sortable="true" formatType="text"
		headerText="Phone">
		<af:outputText value="#{contact.phoneNo}"
			converter="#{infraDefs.phoneNumberConverter}" />
	</af:column>

	<af:column sortProperty="c06" sortable="true" formatType="text"
		headerText="Email">
		<af:goLink text="#{contact.emailAddressTxt}"
			destination="mailto:#{contact.emailAddressTxt}" />
	</af:column>
	<af:column sortProperty="c07" sortable="true" formatType="text"
		headerText="Company ID">
		<af:commandLink text="#{contact.cmpId}"
			returnListener="#{facilityProfile.dialogDone}"
			action="#{companyProfile.submitProfile}">
			<t:updateActionListener property="#{companyProfile.cmpId}"
				value="#{contact.cmpId}" />
		</af:commandLink>
	</af:column>
	<af:column sortProperty="c08" sortable="true" formatType="text"
		headerText="Company Name" width="400">
		<af:outputText value="#{contact.companyName}" />
	</af:column>

	<af:column sortProperty="c09" sortable="true" formatType="text"
		headerText="CROMERR Username">
		<af:outputText value="#{contact.externalUser.userName}" />
	</af:column>

	<af:column sortProperty="c10" sortable="true" formatType="icon"
		headerText="Active">
		<af:selectBooleanCheckbox value="#{contact.active}" readOnly="true" />
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
