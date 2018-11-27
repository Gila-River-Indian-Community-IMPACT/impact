<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelGroup layout="vertical">
	<%
		/* Create Contact Form */
	%>
	<jsp:include flush="true" page="newContactForm.jsp" />

	<af:objectSpacer height="10" />
	<afh:rowLayout halign="center">
		<af:panelButtonBar>
			<af:commandButton text="Submit Create Contact" useWindow="true"
				rendered="#{contactDetail.dapcUser}"
				action="#{createContact.submitCreateContact}" />
			<af:commandButton text="Submit Create Contact"  useWindow="true"
				rendered="#{!contactDetail.dapcUser}"
				action="#{facilityProfile.submitCreateContactForStaging}"
				returnListener="#{facilityProfile.returnSubmitCreateContactForStaging}">
				<t:updateActionListener
					property="#{createContact.contact.facilityId}"
					value="#{facilityProfile.facility.facilityId}" />
				<t:updateActionListener
					property="#{createContact.contact.companyId}"
					value="#{facilityProfile.facility.owner.company.companyId}" />
			</af:commandButton>
			<af:commandButton text="Reset" immediate="true"
				actionListener="#{createContact.resetCreateContact}">
				<af:resetActionListener />
			</af:commandButton>
			<af:commandButton text="Cancel" immediate="true"
				rendered="#{!contactDetail.dapcUser}"
				action="#{facilityProfile.closeDialog}" />
		</af:panelButtonBar>
	</afh:rowLayout>
</af:panelGroup>