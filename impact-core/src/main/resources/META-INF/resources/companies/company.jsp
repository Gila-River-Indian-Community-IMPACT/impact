<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelGroup layout="vertical"
	partialTriggers="addressLine1 addressLine2 cityName zipCode state country phoneNo faxNo">

	<af:panelHeader text="Company Information" size="0" />

	<af:panelHorizontal halign="center">
		<af:panelForm maxColumns="2" width="100%">
			<af:inputText label="ID:" value="#{companyProfile.company.cmpId}"
				columns="10" readOnly="true" />
			<af:inputText label="Name:" value="#{companyProfile.company.name}"
				id="name" columns="55" maximumLength="55"
				readOnly="#{! companyProfile.editable}" showRequired="true" />
			<af:inputText label="Alias:" value="#{companyProfile.company.alias}"
				id="alias" columns="55" maximumLength="55"
				readOnly="#{! companyProfile.editable}" />
			<af:inputText label="CROMERR ID:"
				value="#{companyProfile.company.externalCompanyId}" id="externalId"
				columns="10" maximumLength="11"
				readOnly="#{! companyProfile.editable}" />
			<af:inputText label="Phone:" value="#{companyProfile.company.phone}"
				id="phone" columns="14" maximumLength="14"
				readOnly="#{! companyProfile.editable}"
				converter="#{infraDefs.phoneNumberConverter}" />
			<af:inputText label="Fax:" value="#{companyProfile.company.fax}"
				id="fax" columns="14" maximumLength="14"
				readOnly="#{! companyProfile.editable}"
				converter="#{infraDefs.phoneNumberConverter}" />
			<af:inputText label="Pay Key:" value="#{companyProfile.company.payKey}"
				id="payKey" columns="20" maximumLength="20"
				readOnly="#{! companyProfile.editable}"	/>
			<af:inputText label="Vendor Number:" value="#{companyProfile.company.vendorNumber}"
				id="vendorNumber" columns="10" maximumLength="10"
				readOnly="#{! companyProfile.editable}">
					<af:convertNumber pattern="0000000000"/>
			</af:inputText>				
		</af:panelForm>
	</af:panelHorizontal>

	<af:panelHorizontal halign="left">
		<af:panelForm rows="2" maxColumns="1" width="650">
			<af:inputText label="Address1:" id="addressLine1" columns="60"
				maximumLength="100"
				value="#{companyProfile.company.address.addressLine1}"
				readOnly="#{! companyProfile.editable}"
				tip="#{(! companyProfile.editable)?'':'When entering an address, City, State, Zip, and Country fields are required.'}"/>
			<af:inputText label="Address2:" id="addressLine2" columns="60"
				maximumLength="100"
				value="#{companyProfile.company.address.addressLine2}"
				readOnly="#{! companyProfile.editable}" />
		</af:panelForm>
	</af:panelHorizontal>

	<af:panelHorizontal halign="center">
		<af:panelForm rows="2" maxColumns="2" labelWidth="30%" width="600px">
			<af:inputText label="City:" id="city" columns="30" maximumLength="50"
				value="#{companyProfile.company.address.cityName}"
				readOnly="#{! companyProfile.editable}" />
			<af:selectOneChoice label="State:" id="state" unselectedLabel=""
				value="#{companyProfile.company.address.state}"
				readOnly="#{! companyProfile.editable}">
				<f:selectItems value="#{infraDefs.states}" />
			</af:selectOneChoice>
			<af:inputText label="Zip:" id="zipCode" columns="10"
				maximumLength="10" value="#{companyProfile.company.address.zipCode}"
				readOnly="#{! companyProfile.editable}" />
			<af:selectOneChoice label="Country:" id="country" unselectedLabel=""
				value="#{companyProfile.company.address.countryCd}"
				readOnly="#{! companyProfile.editable}">
				<f:selectItems value="#{infraDefs.countries}" />
			</af:selectOneChoice>
		</af:panelForm>
	</af:panelHorizontal>

	<af:showDetailHeader text="Facility Ownership" disclosed="true"
		id="cmpFacilitiess" rendered="#{companyProfile.dapcUser}">
		<jsp:include flush="true" page="companyFacilitiesTable.jsp" />
	</af:showDetailHeader>

	<af:showDetailHeader text="Employed Contacts" disclosed="true"
		id="cmpContactss" rendered="#{companyProfile.dapcUser}">
		<jsp:include flush="true" page="companyContactsTable.jsp" />
	</af:showDetailHeader>

	<af:showDetailHeader text="Notes" disclosed="true" id="cmpNotes"
		rendered="#{companyProfile.dapcUser}">
		<jsp:include flush="true" page="notesTable.jsp" />
	</af:showDetailHeader>

	<af:objectSpacer height="20" />
	<afh:rowLayout halign="center">
		<af:panelButtonBar>
			<af:commandButton text="Delete" useWindow="true" windowWidth="700"
				windowHeight="500"
				disabled="#{!empty companyProfile.companyFacilities or !empty companyProfile.companyContacts}"
				action="#{companyProfile.deleteCompany}"
				rendered="#{!companyProfile.editable && companyProfile.dapcUser}" />
			<af:commandButton text="Merge Company" useWindow="true"
				windowWidth="1000" windowHeight="800"
				action="#{companyProfile.mergeCompany}"
				rendered="#{companyProfile.companyMergeable}">
				<t:updateActionListener
					property="#{companyProfile.mergeDestinationCmpId}"
					value="#{companyProfile.company.cmpId}" />
			</af:commandButton>
			<af:commandButton text="Edit" action="#{companyProfile.editCompany}"
				disabled="#{companyProfile.disabledUpdateButton}"
				rendered="#{!companyProfile.editable && companyProfile.dapcUser}" />
			<af:commandButton text="Save"
				action="#{companyProfile.saveCompanyProfile}"
				rendered="#{companyProfile.editable}" />
			<af:commandButton text="Cancel"
				action="#{companyProfile.cancelEditCompany}"
				rendered="#{companyProfile.editable}" immediate="true" />
		</af:panelButtonBar>
	</afh:rowLayout>
</af:panelGroup>
