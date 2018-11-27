<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{companyProfile.cmpContactsWrapper}"
	binding="#{companyProfile.cmpContactsWrapper.table}"
	bandingInterval="1" banding="row" var="cmpContacts" width="98%"
	rows="#{companyProfile.pageLimit}">

	<af:column sortProperty="c01" sortable="true" noWrap="true"
		formatType="text" headerText="Contact ID">
		<af:commandLink action="#{contactDetail.submitDetail}"
			text="#{cmpContacts.cntId}">
			<t:updateActionListener property="#{contactDetail.contactId}"
				value="#{cmpContacts.contactId}" />
			<t:updateActionListener property="#{menuItem_contactDetail.disabled}"
				value="false" />
		</af:commandLink>
	</af:column>

	<af:column sortProperty="c02" sortable="true" formatType="text"
		headerText="Last Name">
		<af:outputText value="#{cmpContacts.lastNm}" />
	</af:column>

	<af:column sortProperty="c03" sortable="true" formatType="text"
		headerText="First Name">
		<af:outputText value="#{cmpContacts.firstNm}" />
	</af:column>

	<af:column sortProperty="c04" sortable="true" formatType="text"
		headerText="Preferred Name">
		<af:outputText value="#{cmpContacts.preferredName}" />
	</af:column>

	<af:column sortProperty="c05" sortable="true" formatType="text"
		headerText="Phone">
		<af:outputText value="#{cmpContacts.phoneNo}"
			converter="#{infraDefs.phoneNumberConverter}" />
	</af:column>

	<af:column sortProperty="c06" sortable="true" formatType="text"
		headerText="Email">
		<af:goLink text="#{cmpContacts.emailAddressTxt}"
			destination="mailto:#{cmpContacts.emailAddressTxt}" />
	</af:column>

	<af:column sortProperty="c07" sortable="true" formatType="text"
		headerText="Active">
		<af:selectBooleanCheckbox readOnly="true"
			value="#{cmpContacts.active}" />
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
