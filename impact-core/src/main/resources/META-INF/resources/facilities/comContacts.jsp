<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<h:panelGrid border="1">
	<af:panelBorder>

		<f:facet name="top">
			<f:subview id="facilityHeader">
				<jsp:include page="comFacilityHeader2.jsp" />
			</f:subview>
		</f:facet>


		<f:facet name="right">
			<af:panelGroup layout="vertical">

				<af:showDetailHeader text="Contact Types" disclosed="true"
					id="facContactTypes">
					<h:panelGrid columns="1" border="1"
						style="margin-left:auto; margin-right:auto;">
						<af:panelGroup layout="vertical">
							<af:objectSpacer height="10" />
							<af:inputHidden value="#{submitTask.logUserOff}"
								rendered="#{!facilityProfile.internalApp}" />
							<af:panelForm>
								<af:table value="#{facilityProfile.facilityContactsWrapper}"
									bandingInterval="1" banding="row" var="contact" width="98%"
									id="ContactsTab"
									binding="#{facilityProfile.facilityContactsWrapper.table}">
									<af:column sortProperty="c01" sortable="true" formatType="text"
										headerText="Contact Type" width="100px" noWrap="true">
										<af:commandLink
											text="#{infraDefs.contactTypes.itemDesc[(empty contact.contactType.contactTypeCd ? '' : contact.contactType.contactTypeCd)]}"
											id="viewContactType" useWindow="true" windowWidth="700"
											windowHeight="500"
											returnListener="#{facilityProfile.dialogDone}"
											action="dialog:viewContType">
											<t:updateActionListener
												property="#{facilityProfile.facContact}" value="#{contact}" />
										</af:commandLink>
									</af:column>
									<af:column sortProperty="c02" sortable="true" formatType="text"
										headerText="Contact ID" width="65px">
										<af:commandLink text="#{contact.contact.cntId}"
											rendered="#{facilityProfile.dapcUser}"
											id="contactIdInternal" action="contactDetail">
											<t:updateActionListener property="#{contactDetail.contactId}"
												value="#{contact.contact.contactId}" />
											<t:updateActionListener
												property="#{menuItem_contactDetail.disabled}" value="false" />
										</af:commandLink>

										<af:commandLink text="#{contact.contact.cntId}"
											useWindow="true" windowWidth="950" windowHeight="950"
											rendered="#{!facilityProfile.dapcUser}"
											id="contactIdExternal"
											returnListener="#{facilityProfile.dialogDone}"
											action="#{contactDetail.goToDetail}">
											<t:updateActionListener property="#{contactDetail.contactId}"
												value="#{contact.contact.contactId}" />
											<t:updateActionListener property="#{contactDetail.facilityId}"
												value="#{facilityProfile.facility.facilityId}" />
											<t:updateActionListener property="#{contactDetail.staging}"
												value="#{!myTasks.fromHomeContact}" />												
											<t:updateActionListener
												property="#{menuItem_contactDetail.disabled}" value="false" />
										</af:commandLink>
									</af:column>
									<af:column sortProperty="c03" sortable="true" formatType="text"
										headerText="Contact Name" width="90px" noWrap="true">
										<af:outputText value="#{contact.contact.nameOnly}"
											id="contactName" />
									</af:column>
									<af:column sortProperty="c04" sortable="true" formatType="text"
										headerText="Phone Number" width="90px" noWrap="true">
										<af:outputText value="#{contact.contact.phoneNo}"
											converter="#{infraDefs.phoneNumberConverter}" />
									</af:column>
									<af:column sortProperty="c05" sortable="true" formatType="text"
										headerText="Email" width="5px" noWrap="true">
										<af:goLink text="#{contact.contact.emailAddressTxt}"
											destination="mailto:#{contact.contact.emailAddressTxt}"/>
									</af:column>
									<af:column id="startDate" sortProperty="startDate" sortable="true" formatType="text"
										headerText="Start Date" width="65px" noWrap="true">
										<af:selectInputDate value="#{contact.startDate}"
											readOnly="true">
									  </af:selectInputDate>
									</af:column>
									<af:column id="endDate" sortProperty="endDate" sortable="true"
										formatType="text" headerText="End Date" width="70px" noWrap="true">
										<af:selectInputDate value="#{contact.endDate}"
											readOnly="true">
											</af:selectInputDate>
									</af:column>
									<f:facet name="footer">
										<afh:rowLayout halign="center">
											<af:panelButtonBar>
												<af:commandButton
													actionListener="#{tableExporter.printTable}"
													onclick="#{tableExporter.onClickScript}"
													text="Printable view" />
												<af:commandButton
													actionListener="#{tableExporter.excelTable}"
													onclick="#{tableExporter.onClickScript}"
													text="Export to excel" />
											</af:panelButtonBar>
										</afh:rowLayout>
									</f:facet>
								</af:table>
							</af:panelForm>
							<afh:rowLayout halign="center"
								rendered="#{!facilityProfile.readOnlyUser && !myTasks.fromHomeContact}">
								<af:panelButtonBar>
									<af:commandButton text="Create Contact Person"
										id="AddContactButton"
										disabled="#{!facilityProfile.dapcUser && myTasks.fromHomeContact}"
										rendered="#{facilityProfile.dapcUser}"
										action="contacts.createContact" />
									<af:commandButton text="Assign Contact Type"
										id="AddContactTypeButton" useWindow="true" windowWidth="800"
										windowHeight="600"
										disabled="#{!facilityProfile.dapcUser && myTasks.fromHomeContact}"
										returnListener="#{facilityProfile.dialogDone}"
										action="#{facilityProfile.startAddTypeToContact}" />
									<af:commandButton text="Show All Contacts" id="ShowAllContacts"
										disabled="#{!facilityProfile.dapcUser && myTasks.fromHomeContact && facilityProfile.activeContacts}"
										rendered="#{facilityProfile.activeContacts && facilityProfile.dapcUser}"
										action="#{facilityProfile.getFacilityContacts}" />
									<af:commandButton text="Show Only Active Contacts"
										id="ShowActiveContacts"
										disabled="#{!facilityProfile.dapcUser && myTasks.fromHomeContact && !facilityProfile.activeContacts}"
										rendered="#{!facilityProfile.activeContacts && facilityProfile.dapcUser}"
										action="#{facilityProfile.getActiveFacilityContacts}" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</af:panelGroup>
					</h:panelGrid>
				</af:showDetailHeader>

				<af:showDetailHeader text="Contacts" disclosed="true"
					id="facOwnerContacts" rendered="#{!companyProfile.dapcUser}">
					<jsp:include flush="true" page="facilityOwnerContactsTable.jsp" />
				</af:showDetailHeader>

				<afh:rowLayout halign="center"
					rendered="#{!facilityProfile.readOnlyUser && !myTasks.fromHomeContact}">
					<af:panelButtonBar>
					 <%-- TODO replace deleted conditional shortDesc --%>
						<af:commandButton rendered="#{!facilityProfile.dapcUser}"
							disabled="#{!myTasks.hasSubmit || myTasks.fromHomeContact}" text="Submit"
							id="SubmitContactsButton"
							action="#{facilityProfile.submitContacts}"
							shortDesc="Submit"
							useWindow="true" windowWidth="#{submitTask.attestWidth}"
							windowHeight="#{submitTask.attestHeight}">
							<t:updateActionListener property="#{submitTask.type}"
								value="#{submitTask.yesNo}" />
							<t:updateActionListener property="#{submitTask.task}"
								value="#{facilityProfile.contactTask}" />
						</af:commandButton>
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:panelGroup>
		</f:facet>

	</af:panelBorder>
</h:panelGrid>