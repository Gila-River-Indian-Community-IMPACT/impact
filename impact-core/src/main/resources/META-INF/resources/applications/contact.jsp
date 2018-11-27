<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>


<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
	<afh:rowLayout halign="left">
		<af:outputText
			value="Newly created contacts and application contact changes will be saved when the application is saved."
			inlineStyle="font-size:75%;color:#666" />
	</afh:rowLayout>
</afh:tableLayout>


<af:panelForm id="contactPanelForm">
	<af:panelForm rendered="#{applicationDetail.newContact}" maxColumns="3"
		rows="1" labelWidth="44%" fieldWidth="56%">
		<af:panelLabelAndMessage
			rendered="#{applicationDetail.application.contact != null || applicationDetail.applicationContact.cntId != null}"
			label="ID :" for="cntId">
			<af:commandLink id="cntId" action="#{contactDetail.submitDetail}"
				text="#{applicationDetail.applicationContact.cntId}"
				rendered="#{facilityProfile.dapcUser}"
				disabled="#{applicationDetail.editMode}">
				<t:updateActionListener property="#{contactDetail.contactId}"
					value="#{applicationDetail.applicationContact.contactId}" />
				<t:updateActionListener
					property="#{menuItem_contactDetail.disabled}" value="false" />
			</af:commandLink>

			<af:commandLink text="#{applicationDetail.applicationContact.cntId}"
				useWindow="true" windowWidth="950" windowHeight="950"
				rendered="#{!facilityProfile.dapcUser}" id="contactIdExternal"
				action="#{contactDetail.goToDetail}">
				<t:updateActionListener property="#{contactDetail.contactId}"
					value="#{applicationDetail.applicationContact.contactId}" />
				<t:updateActionListener property="#{contactDetail.facilityId}"
					value="#{facilityProfile.facility.facilityId}" />
				<t:updateActionListener property="#{contactDetail.staging}"
					value="#{!applicationDetail.readOnly}" />
				<t:updateActionListener property="#{myTasks.fromHomeContact}"
					value="#{applicationDetail.readOnly}" />
				<t:updateActionListener
					property="#{menuItem_contactDetail.disabled}" value="false" />
			</af:commandLink>
		</af:panelLabelAndMessage>
		<af:inputText id="contactFirstName" label="First Name :"
			readOnly="true"
			value="#{applicationDetail.applicationContact.firstNm}" columns="20"
			maximumLength="50" />
		<af:inputText id="contactLastName" label="Last Name :" readOnly="true"
			value="#{applicationDetail.applicationContact.lastNm}" columns="20"
			maximumLength="50" />
		<af:inputText id="addressLine1" label="Address :" columns="20"
			readOnly="true"
			value="#{applicationDetail.applicationContact.address.addressLine1}"
			maximumLength="100" />
		<af:inputText id="cityName" label="City/Township :" readOnly="true"
			columns="20"
			value="#{applicationDetail.applicationContact.address.cityName}"
			maximumLength="50" />
		<af:selectOneChoice id="state" label="State :"
			value="#{applicationDetail.applicationContact.address.state}"
			readOnly="true">
			<f:selectItems value="#{infraDefs.states}" />
		</af:selectOneChoice>
		<af:inputText id="zipCode" label="Zip Code :" readOnly="true"
			columns="10"
			value="#{applicationDetail.applicationContact.address.zipCode}"
			maximumLength="10" />
		<af:inputText id="contactPhone" label="Phone Number :" readOnly="true"
			columns="20" value="#{applicationDetail.applicationContact.phoneNo}"
			converter="#{infraDefs.phoneNumberConverter}" maximumLength="20" />
		<af:inputText id="contactEmail" label="Email :" readOnly="true"
			columns="20"
			value="#{applicationDetail.applicationContact.emailAddressTxt}"
			maximumLength="80" />
		<af:inputText id="contactEmail2" label="Secondary Email :" readOnly="true"
			columns="20"
			value="#{applicationDetail.applicationContact.emailAddressTxt2}"
			maximumLength="80" />
		<af:inputText id="contactCompanyTitle" label="Company Title :"
			value="#{applicationDetail.applicationContact.companyTitle}"
			columns="20" maximumLength="100" readOnly="true">
		</af:inputText>
		<af:selectOneChoice
			value="#{applicationDetail.applicationContact.companyId}"
			readOnly="true" label="Contact's Company Name : " showRequired="true"
			id="contactCompany">
			<f:selectItems value="#{infraDefs.companies}" />
		</af:selectOneChoice>
	</af:panelForm>
	<af:panelForm maxColumns="3" rows="1" labelWidth="44%" fieldWidth="56%">
		<f:facet name="footer">
			<afh:rowLayout halign="center">
				<af:panelButtonBar>
					<af:commandButton id="loadFacilityContactsBtn"
						text="Load Contact From Facility Contacts" useWindow="true"
						rendered="#{applicationDetail.editMode}" windowWidth="950"
						windowHeight="400"
						action="#{applicationDetail.loadFacilityContact}">
						<t:updateActionListener value="false"
							property="#{applicationDetail.hideTradeSecret}" />
					</af:commandButton>
					<%-- 					<af:commandButton text="New Facility Contact" --%>
					<%-- 						rendered="#{applicationDetail.editMode}" --%>
					<%-- 						action="#{applicationDetail.newContact}" --%>
					<%-- 						shortDesc="Creates a new permitting contact for application's facility and then assigns the contact as the permit application's contact." /> --%>
					<af:commandButton text="Reset Contact"
						rendered="#{applicationDetail.editMode}"
						action="#{applicationDetail.resetContact}"
						shortDesc="Clear out contact data and make fields editable." />
				</af:panelButtonBar>
			</afh:rowLayout>
		</f:facet>
	</af:panelForm>
</af:panelForm>


