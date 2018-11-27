<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<h:panelGrid border="1" rendered="#{!facilityProfile.dapcUser}"
	width="950" style="margin-left:auto; margin-right:auto;">
	<af:panelGroup layout="vertical">
		<af:objectSpacer height="10" />
		<af:panelForm>
			<af:table value="#{facilityProfile.facOwnerContactsWrapper}"
				binding="#{facilityProfile.facOwnerContactsWrapper.table}"
				bandingInterval="1" banding="row" var="facOwnerContact"
				rows="#{facilityProfile.pageLimit}" width="98%">

				<af:column sortProperty="c01" sortable="true" noWrap="true"
					formatType="text" headerText="Contact ID">
					<af:commandLink text="#{facOwnerContact.cntId}" useWindow="true"
						windowWidth="700" windowHeight="500"
						rendered="#{!facilityProfile.dapcUser}"
						disabled="#{facilityProfile.readOnlyUser}" id="contactIdExternal"
						returnListener="#{facilityProfile.dialogDone}"
						action="#{contactDetail.goToDetail}">
						<t:updateActionListener property="#{contactDetail.contactId}"
							value="#{facOwnerContact.contactId}" />
						<t:updateActionListener property="#{contactDetail.facilityId}"
							value="#{facilityProfile.facility.facilityId}" />
						<t:updateActionListener property="#{contactDetail.staging}"
							value="#{!myTasks.fromHomeContact}" />
						<t:updateActionListener
							property="#{menuItem_contactDetail.disabled}" value="false" />
					</af:commandLink>
				</af:column>

				<af:column sortProperty="lastName" sortable="true" formatType="text"
					headerText="Last Name">
					<af:outputText value="#{facOwnerContact.lastNm}" />
				</af:column>

				<af:column sortProperty="c03" sortable="true" formatType="text"
					headerText="First Name">
					<af:outputText value="#{facOwnerContact.firstNm}" />
				</af:column>

				<af:column sortProperty="c04" sortable="true" formatType="text"
					headerText="Preferred Name">
					<af:outputText value="#{facOwnerContact.preferredName}" />
				</af:column>

				<af:column sortProperty="c05" sortable="true" formatType="text"
					headerText="Phone">
					<af:outputText value="#{facOwnerContact.phoneNo}"
						converter="#{infraDefs.phoneNumberConverter}" />
				</af:column>

				<af:column sortProperty="c06" sortable="true" formatType="text"
					headerText="Email">
					<af:goLink text="#{facOwnerContact.emailAddressTxt}"
						destination="mailto:#{facOwnerContact.emailAddressTxt}" />
				</af:column>
				<af:column sortProperty="c07" sortable="true" formatType="text"
					headerText="Company ID">
					<af:commandLink text="#{facOwnerContact.cmpId}" useWindow="true"
						inlineStyle="padding:5px;" rendered="#{!facilityProfile.dapcUser}"
						disabled="#{facilityProfile.readOnlyUser}"
						returnListener="#{facilityProfile.dialogDone}"
						action="#{companyProfile.goToProfile}">
						<t:updateActionListener property="#{companyProfile.cmpId}"
							value="#{facOwnerContact.cmpId}" />
					</af:commandLink>
				</af:column>
				<af:column sortProperty="c08" sortable="true" formatType="text"
					headerText="Company Name" width="400">
					<af:outputText value="#{facOwnerContact.companyName}" />
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
		</af:panelForm>

		<afh:rowLayout halign="center"
			rendered="#{!facilityProfile.readOnlyUser && !myTasks.fromHomeContact}">
			<af:panelButtonBar>
				<af:commandButton text="Create Contact Person" id="AddContactButton"
					disabled="#{!facilityProfile.dapcUser && myTasks.fromHomeContact}"
					useWindow="true" returnListener="#{facilityProfile.dialogDone}"
					action="dialog:addContact" />
			</af:panelButtonBar>
		</afh:rowLayout>

	</af:panelGroup>
</h:panelGrid>
