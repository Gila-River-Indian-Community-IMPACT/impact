<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Facility Contacts">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />

			<af:panelForm rendered="#{!applicationDetail.createNewContact}">
				<af:panelHeader text="Select Facility Contact" size="0" />
				<af:table id="facilityContactsTable" width="98%" emptyText=" "
					var="contact" bandingInterval="1" banding="row"
					binding="#{applicationDetail.facilityContactsWrapper.table}"
					value="#{applicationDetail.facilityContactsWrapper}">
					<f:facet name="selection">
						<af:tableSelectOne id="facilityContactTableSelection" />
					</f:facet>
					<af:column sortProperty="c01" sortable="true" formatType="text"
						headerText="Contact Type">
						<af:outputText
							value="#{infraDefs.contactTypes.itemDesc[(empty contact.contactType.contactTypeCd ? '' 
                : contact.contactType.contactTypeCd)]}" />
					</af:column>
					<af:column sortProperty="c02" sortable="true" formatType="text"
						headerText="Contact Person">
						<af:outputText value="#{contact.contact.nameOnly}" />
					</af:column>
					<af:column sortProperty="c03" sortable="true" formatType="text"
						headerText="Contact's Company Name">
						<af:outputText value="#{contact.contact.companyName}" />
					</af:column>
					<af:column sortProperty="c04" sortable="true" formatType="text"
						headerText="Phone Number">
						<af:outputText value="#{contact.contact.phoneNo}"
							converter="#{infraDefs.phoneNumberConverter}" />
					</af:column>
					<af:column sortProperty="c05" sortable="true" formatType="text"
						headerText="Email">
						<af:goLink text="#{contact.contact.emailAddressTxt}"
							destination="mailto:#{contact.contact.emailAddressTxt}" />
					</af:column>
					<af:column sortProperty="c06" sortable="true" formatType="text"
						headerText="Start Date">
						<af:selectInputDate value="#{contact.contactType.startDate}"
							readOnly="true" />
					</af:column>
					<af:column sortProperty="c07" sortable="true" formatType="text"
						headerText="End Date">
						<af:selectInputDate value="#{contact.contactType.endDate}"
							readOnly="true" />
					</af:column>
				</af:table>
			</af:panelForm>
			<af:panelForm rendered="#{!applicationDetail.createNewContact}">
				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton id="loadContactInfoBtn"
								text="Use Selected Contact"
								actionListener="#{applicationDetail.applyFacilityContact}" />
							<af:commandButton id="contactInfoCancelBtn" text="Cancel"
								immediate="true">
								<af:returnActionListener />
							</af:commandButton>
						</af:panelButtonBar>
					</afh:rowLayout>
				</f:facet>
			</af:panelForm>
			<af:panelForm rendered="#{!applicationDetail.createNewContact}">
				<af:objectSpacer height="20" />
				<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
					<afh:rowLayout halign="center">
						<af:outputFormatted
							value="Can't find the contact you were looking for? Click New Facility Contact below to create a new contact associated with this facility." />
					</afh:rowLayout>
				</afh:tableLayout>
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton id="newFacContact" text="New Facility Contact"
							action="#{applicationDetail.newFacilityContact}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:panelForm>



			<af:panelGroup layout="vertical"
				rendered="#{applicationDetail.createNewContact}">
				<af:panelForm>
					<af:panelHeader text="Create New Facility Contact" size="0" />

					<%
						/* Create Contact Form */
					%>
					<jsp:include flush="true" page="newContactForm.jsp" />
				</af:panelForm>

				<af:objectSpacer height="10" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Load New Facility Contact"
							action="#{applicationDetail.createFacilityContact}">
						</af:commandButton>
						<af:commandButton text="Reset" immediate="true"
							actionListener="#{applicationDetail.resetCreateFacilityContact}">
							<af:resetActionListener />
						</af:commandButton>
						<af:commandButton id="contactNewCancelBtn" text="Cancel"
							actionListener="#{applicationDetail.cancelCreateFacilityContact}"
							immediate="true" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:panelGroup>

		</af:form>
	</af:document>
</f:view>

