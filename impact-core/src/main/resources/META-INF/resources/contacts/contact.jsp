<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelHeader text="Contact Information" size="0" />
<af:objectSpacer height="20" />
<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText label="ID:" value="#{contactDetail.contact.cntId}"
		columns="40" readOnly="true" id="contact_id" />

	<af:selectBooleanCheckbox label="Active:"
		value="#{contactDetail.contact.active}"
		rendered="#{contactDetail.dapcUser}"
		readOnly="#{!contactDetail.editable}" id="statusFlag" />
</af:panelForm>
<af:panelForm rows="5" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice label="Title:"
		value="#{contactDetail.contact.titleCd}"
		readOnly="#{!contactDetail.editable}">
		<f:selectItems
			value="#{infraDefs.contactPersonTitles.items[(empty contactDetail.contact.titleCd ? '' : contactDetail.contact.titleCd)]}" />
	</af:selectOneChoice>
	<af:inputText label="First Name:"
		value="#{contactDetail.contact.firstNm}" columns="40"
		maximumLength="40" readOnly="#{!contactDetail.editable}" id="firstNm"
		showRequired="true" />
	<af:inputText label="Preferred Name:"
		value="#{contactDetail.contact.preferredName}" columns="40"
		maximumLength="40" readOnly="#{!contactDetail.editable}" id="prefNm" />
	<af:inputText label="Middle Name:"
		value="#{contactDetail.contact.middleNm}" columns="40"
		maximumLength="40" readOnly="#{! contactDetail.editable}"
		id="middleNm" />
	<af:inputText label="Last Name:"
		value="#{contactDetail.contact.lastNm}" columns="40"
		maximumLength="40" readOnly="#{! contactDetail.editable}" id="lastNm"
		showRequired="true" />
	<af:inputText label="Suffix:" value="#{contactDetail.contact.suffixCd}"
		columns="6" maximumLength="6" readOnly="#{! contactDetail.editable}" />

	<af:panelGroup layout="horizontal"
		rendered="#{! contactDetail.editable}">
		<af:inputText label="Email: " readOnly="true" />
		<af:goLink text="#{contactDetail.contact.emailAddressTxt}"
			destination="mailto:#{contactDetail.contact.emailAddressTxt}" />
	</af:panelGroup>
	<af:panelGroup layout="horizontal"
		rendered="#{! contactDetail.editable}">
		<af:inputText label="Secondary Email: " readOnly="true" />
		<af:goLink text="#{contactDetail.contact.emailAddressTxt2}"
			destination="mailto:#{contactDetail.contact.emailAddressTxt2}" />
	</af:panelGroup>

	<af:inputText label="Email:"
		value="#{contactDetail.contact.emailAddressTxt}" columns="40"
		maximumLength="40" readOnly="#{! contactDetail.editable}"
		rendered="#{contactDetail.editable}" id="emailAddressTxt"
		showRequired="true" />
	<af:inputText label="Secondary Email:"
		value="#{contactDetail.contact.emailAddressTxt2}" columns="40"
		maximumLength="40" readOnly="#{! contactDetail.editable}"
		rendered="#{contactDetail.editable}" id="emailAddressTxt2" />
	<af:inputText label="Job Title:"
		value="#{contactDetail.contact.companyTitle}" columns="40"
		maximumLength="100" readOnly="#{!contactDetail.editable}"
		id="companyTitle" />
	<af:selectOneChoice value="#{contactDetail.contact.companyId}"
		readOnly="#{!contactDetail.editable || !contactDetail.dapcUser}"
		label="Company Name: " showRequired="true" id="contactCompany">
		<f:selectItems value="#{infraDefs.companies}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="2" maxColumns="1" labelWidth="150" width="600"
	partialTriggers="loadAddress">
	<af:inputText label="Address 1:"
		value="#{contactDetail.contact.address.addressLine1}" columns="60"
		maximumLength="100" readOnly="#{! contactDetail.editable}"
		id="addressLine1" showRequired="true" />
	<af:inputText label="Address 2:"
		value="#{contactDetail.contact.address.addressLine2}" columns="60"
		maximumLength="100" readOnly="#{! contactDetail.editable}" />
</af:panelForm>

<af:panelForm rows="2" maxColumns="2" labelWidth="150" width="600"
	partialTriggers="loadAddress">
	<af:inputText label="City:"
		value="#{contactDetail.contact.address.cityName}" columns="30"
		maximumLength="50" readOnly="#{! contactDetail.editable}"
		id="cityName" showRequired="true" />
	<af:selectOneChoice label="State:"
		value="#{contactDetail.contact.address.state}"
		readOnly="#{! contactDetail.editable}" id="state" showRequired="true">
		<f:selectItems value="#{infraDefs.states}" />
	</af:selectOneChoice>
	<af:inputText label="Zip Code:"
		value="#{contactDetail.contact.address.zipCode}" id="zipCode"
		readOnly="#{! contactDetail.editable}" showRequired="true" />
</af:panelForm>
<af:objectSpacer width="100%" height="15" />
<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText label="Primary Phone No.:"
		value="#{contactDetail.contact.phoneNo}" columns="14"
		maximumLength="14" readOnly="#{! contactDetail.editable}" id="phoneNo"
		showRequired="true" converter="#{infraDefs.phoneNumberConverter}" />
	<af:inputText label="Primary Ext. No.:"
		value="#{contactDetail.contact.phoneExtensionVal}" columns="8"
		maximumLength="8" readOnly="#{! contactDetail.editable}" />
</af:panelForm>
<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText label="Secondary Phone No.:"
		value="#{contactDetail.contact.secondaryPhoneNo}"
		id="secondaryPhoneNo" columns="14" maximumLength="14"
		readOnly="#{! contactDetail.editable}"
		converter="#{infraDefs.phoneNumberConverter}" />
	<af:inputText label="Secondary Ext. No.:"
		value="#{contactDetail.contact.secondaryExtensionVal}" columns="8"
		maximumLength="8" readOnly="#{! contactDetail.editable}" />
</af:panelForm>
<af:panelForm rows="2" maxColumns="2" labelWidth="150" width="600">
	<af:inputText label="Mobile Phone No.:"
		value="#{contactDetail.contact.mobilePhoneNo}" id="mobilePhoneNo"
		columns="14" maximumLength="14" readOnly="#{! contactDetail.editable}"
		converter="#{infraDefs.phoneNumberConverter}" />
	<af:inputText label="Fax No.:" value="#{contactDetail.contact.faxNo}"
		id="faxNo" columns="14" maximumLength="14"
		readOnly="#{! contactDetail.editable}"
		converter="#{infraDefs.phoneNumberConverter}" />
</af:panelForm>

<af:showDetailHeader text="Contact Types" disclosed="true"
	id="ctContactTypes" rendered="#{contactDetail.dapcUser}">
	<jsp:include flush="true" page="contactTypesTable.jsp" />
</af:showDetailHeader>

<af:showDetailHeader text="Notes" disclosed="true" id="ctNotes"
	rendered="#{contactDetail.dapcUser}">
	<jsp:include flush="true" page="notesTable.jsp" />
</af:showDetailHeader>

<af:showDetailHeader text="Possible Duplicate Contacts" disclosed="true" id="duplicateContacts"
	rendered="#{contactDetail.dapcUser && contactDetail.hasDuplicates}">
	<jsp:include flush="true" page="duplicateContactsTable.jsp" />
</af:showDetailHeader>
