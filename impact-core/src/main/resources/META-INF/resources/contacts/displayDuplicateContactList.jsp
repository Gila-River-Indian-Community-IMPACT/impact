<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Duplicate Contacts Information">
		<af:form>
			<af:page title="Duplicate Contacts Information">
				
				<af:objectSpacer width="100%" height="5" />
				<afh:rowLayout halign="left">
					<af:outputFormatted
						value="The IMPACT system has found the following duplicate records. Are you sure you want to create a new contact?" />
				</afh:rowLayout>
				<af:objectSpacer width="100%" height="5" />

				<af:table value="#{createContact.duplicateSearchResultsWrapper}"
						binding="#{createContact.duplicateSearchResultsWrapper.table}"
						bandingInterval="1" banding="row" var="contact" width="98%">
					<af:column sortProperty="c1" sortable="true" noWrap="true" formatType="text" headerText="Contact ID" width="10%">
						<af:outputText value="#{contact.cntId}" />
					</af:column>
					<af:column sortProperty="c2" sortable="true" noWrap="true" formatType="text" headerText="First Name" width="13%">
						<af:outputText value="#{contact.firstNm}" />
					</af:column>
					<af:column sortProperty="c3" sortable="true" noWrap="true" formatType="text" headerText="Last Name" width="13%">
						<af:outputText value="#{contact.lastNm}" />
					</af:column>
					<af:column sortProperty="c4" sortable="true" noWrap="true" formatType="text" headerText="Phone" width="12%">
						<af:outputText value="#{contact.phoneNo}" converter="#{infraDefs.phoneNumberConverter}"  />
					</af:column>
					<af:column sortProperty="c4" sortable="true" noWrap="true" formatType="text" headerText="Email" width="17%">
						<af:outputText value="#{contact.emailAddressTxt}" />
					</af:column>
					<af:column sortProperty="c5" sortable="true" formatType="text" headerText="Company Name" width="30%"
						rendered="#{createContact.displayCompanyName}">
						<af:outputText value="#{contact.companyName}" />
					</af:column>
					<af:column sortProperty="c6" sortable="true" noWrap="true" formatType="icon" headerText="Active" width="5%">
						<af:selectBooleanCheckbox value="#{contact.active}" readOnly="true" />
					</af:column>
				</af:table>

				<af:objectSpacer width="100%" height="15" />

				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Yes" rendered="#{createContact.internalApp}"
							action="#{createContact.createContactFromDialog}" />
						<af:commandButton text="Yes" rendered="#{createContact.portalApp}"
							action="#{facilityProfile.createStagingContactFromDialog}" />
						<af:commandButton text="No" immediate="true"
							action="#{createContact.closeModelessDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>

			</af:page>
		</af:form>
	</af:document>
</f:view>
