<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="5" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice label="Title:"
		value="#{createContact.contact.titleCd}"
		readOnly="#{! createContact.editable1}">
		<f:selectItems value="#{infraDefs.contactTitles}" />
	</af:selectOneChoice>
	<af:inputText label="First Name:"
		value="#{createContact.contact.firstNm}" columns="40"
		maximumLength="40" readOnly="#{! createContact.editable1}"
		id="firstNm"
		showRequired="#{createContact.contact.companyName == null || createContact.contact.companyName == ''}" />
	<af:inputText label="Preferred Name:"
		value="#{createContact.contact.preferredName}" columns="40"
		maximumLength="40" readOnly="#{! createContact.editable1}"
		id="preferredName" />
	<af:inputText label="Middle Name:"
		value="#{createContact.contact.middleNm}" columns="40"
		maximumLength="40" readOnly="#{! createContact.editable1}"
		id="middleNm" />
	<af:inputText label="Last Name:"
		value="#{createContact.contact.lastNm}" columns="40"
		maximumLength="40" readOnly="#{! createContact.editable1}" id="lastNm"
		showRequired="#{createContact.contact.companyName == null || createContact.contact.companyName == ''}" />
	<af:inputText label="Suffix:" value="#{createContact.contact.suffixCd}"
		columns="6" maximumLength="6" readOnly="#{! createContact.editable1}" />
	<af:inputText label="Email:"
		value="#{createContact.contact.emailAddressTxt}" columns="40"
		maximumLength="40" readOnly="#{! createContact.editable1}"
		id="emailAddressTxt" showRequired="true" />
	<af:inputText label="Secondary Email:"
		value="#{createContact.contact.emailAddressTxt2}" columns="40"
		maximumLength="40" readOnly="#{! createContact.editable1}"
		id="emailAddressTxt2" />
	<af:inputText label="Job Title:"
		value="#{createContact.contact.companyTitle}" columns="40"
		maximumLength="100" readOnly="#{!createContact.editable1}"
		id="companyTitle" />
	<af:selectOneChoice value="#{createContact.contact.companyId}"
		readOnly="#{!createContact.editable1}" label="Company Name: "
		showRequired="true" id="contactCompany"
		rendered="#{contactDetail.dapcUser}">
		<f:selectItems value="#{infraDefs.companies}" />
	</af:selectOneChoice>
	<af:inputText value="#{facilityProfile.facility.owner.company.name}"
		readOnly="true" label="Company Name: " id="contactStagingCompany"
		rendered="#{!contactDetail.dapcUser}">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="2" maxColumns="1" labelWidth="150" width="600"
	partialTriggers="loadAddress">
	<af:inputText label="Address 1:"
		value="#{createContact.contact.address.addressLine1}" columns="60"
		maximumLength="100" readOnly="#{! createContact.editable1}"
		id="addressLine1"
		showRequired="#{!createContact.contact.onSiteContact}" />
	<af:inputText label="Address 2:"
		value="#{createContact.contact.address.addressLine2}" columns="60"
		maximumLength="100" readOnly="#{! createContact.editable1}" />
</af:panelForm>
<af:panelForm rows="2" maxColumns="2" labelWidth="150" width="600"
	partialTriggers="loadAddress">
	<af:inputText label="City:"
		value="#{createContact.contact.address.cityName}" columns="30"
		maximumLength="50" readOnly="#{! createContact.editable1}"
		id="cityName" showRequired="#{!createContact.contact.onSiteContact}" />
	<af:selectOneChoice label="State:"
		value="#{createContact.contact.address.state}"
		readOnly="#{! createContact.editable1}" id="state"
		showRequired="#{!createContact.contact.onSiteContact}">
		<f:selectItems value="#{infraDefs.states}" />
	</af:selectOneChoice>
	<af:inputText label="Zip Code:"
		value="#{createContact.contact.address.zipCode}" id="zipCode"
		readOnly="#{! createContact.editable1}"
		showRequired="#{!createContact.contact.onSiteContact}" />
</af:panelForm>
<af:objectSpacer width="100%" height="15" />
<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText label="Primary Phone No.:"
		value="#{createContact.contact.phoneNo}" columns="14"
		maximumLength="14" readOnly="#{! createContact.editable1}"
		id="phoneNo" showRequired="true"
		converter="#{infraDefs.phoneNumberConverter}" />
	<af:inputText label="Primary Ext. No.:"
		value="#{createContact.contact.phoneExtensionVal}" columns="8"
		maximumLength="8" readOnly="#{! createContact.editable1}" />
</af:panelForm>
<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText label="Secondary Phone No.:"
		value="#{createContact.contact.secondaryPhoneNo}"
		id="secondaryPhoneNo" columns="14" maximumLength="14"
		readOnly="#{! createContact.editable1}"
		converter="#{infraDefs.phoneNumberConverter}" />
	<af:inputText label="Secondary Ext. No.:"
		value="#{createContact.contact.secondaryExtensionVal}" columns="8"
		maximumLength="8" readOnly="#{! createContact.editable1}" />
</af:panelForm>
<af:panelForm rows="2" maxColumns="2" labelWidth="150" width="600">
	<af:inputText label="Mobile Phone No.:"
		value="#{createContact.contact.mobilePhoneNo}" id="mobilePhoneNo"
		columns="14" maximumLength="14"
		readOnly="#{! createContact.editable1}"
		converter="#{infraDefs.phoneNumberConverter}" />
	<af:inputText label="Fax No.:" value="#{createContact.contact.faxNo}"
		id="faxNo" columns="14" maximumLength="14"
		readOnly="#{! createContact.editable1}"
		converter="#{infraDefs.phoneNumberConverter}" />
</af:panelForm>