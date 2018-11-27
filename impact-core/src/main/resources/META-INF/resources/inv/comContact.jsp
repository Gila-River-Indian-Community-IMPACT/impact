<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelHeader text="Contact Name" size="0" />
<af:panelForm rows="5" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice label="Prefix:"
		value="#{invoiceDetail.invoice.contact.titleCd}"
		readOnly="#{! invoiceDetail.editable}">
		<f:selectItems value="#{infraDefs.contactTitles}" />
	</af:selectOneChoice>
	<af:inputText label="First Name:"
		value="#{invoiceDetail.invoice.contact.firstNm}" columns="40"
		maximumLength="40" readOnly="#{! invoiceDetail.editable}" id="firstNm"
		showRequired="true" />
	<af:inputText label="Middle Name:"
		value="#{invoiceDetail.invoice.contact.middleNm}" columns="40"
		maximumLength="40" readOnly="#{! invoiceDetail.editable}" />
	<af:inputText label="Last Name:"
		value="#{invoiceDetail.invoice.contact.lastNm}" columns="40"
		maximumLength="40" readOnly="#{! invoiceDetail.editable}" id="lastNm"
		showRequired="true" />
	<af:inputText label="Suffix:"
		value="#{invoiceDetail.invoice.contact.suffixCd}" columns="6"
		maximumLength="6" readOnly="#{! invoiceDetail.editable}" />
	<af:inputText label="Company Title:"
		value="#{invoiceDetail.invoice.contact.companyTitle}" columns="40"
		maximumLength="40" readOnly="#{! invoiceDetail.editable}"
		id="companyTitle" showRequired="" />
	<af:inputText label="Contact's Company Name:"
		value="#{invoiceDetail.invoice.contact.companyName}" columns="40"
		maximumLength="40" readOnly="#{! invoiceDetail.editable}" />
</af:panelForm>

<af:panelHeader text="Contact Address" size="0" />
<af:panelForm rows="2" maxColumns="1" labelWidth="150" width="600"
	partialTriggers="loadAddress">
	<af:inputText label="Address 1:"
		value="#{invoiceDetail.invoice.contact.address.addressLine1}"
		columns="60" maximumLength="100"
		readOnly="#{! invoiceDetail.editable}" id="addressLine1"
		showRequired="true" />
	<af:inputText label="Address 2:"
		value="#{invoiceDetail.invoice.contact.address.addressLine2}"
		columns="60" maximumLength="100"
		readOnly="#{! invoiceDetail.editable}" />
</af:panelForm>
<af:panelForm rows="2" maxColumns="2" labelWidth="150" width="600"
	partialTriggers="loadAddress">
	<af:inputText label="City:"
		value="#{invoiceDetail.invoice.contact.address.cityName}" columns="30"
		maximumLength="50" readOnly="#{! invoiceDetail.editable}"
		id="cityName" showRequired="true" />
	<af:selectOneChoice label="State:"
		value="#{invoiceDetail.invoice.contact.address.state}"
		readOnly="#{! invoiceDetail.editable}" id="state" showRequired="true">
		<f:selectItems value="#{infraDefs.states}" />
	</af:selectOneChoice>
	<af:inputText label="Zip Code:"
		value="#{invoiceDetail.invoice.contact.address.zipCode}" id="zipCode"
		readOnly="#{! invoiceDetail.editable}" id="zipCode"
		showRequired="true" />
</af:panelForm>
<af:objectSpacer width="100%" height="15" />

<af:panelHeader text="Contact Phone Numbers" size="0" />
<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText label="Primary Phone No.:"
		value="#{invoiceDetail.invoice.contact.phoneNo}" columns="14"
		maximumLength="14" readOnly="#{! invoiceDetail.editable}" id="phoneNo"
		showRequired="true" converter="#{infraDefs.phoneNumberConverter}" />
	<af:inputText label="Primary Ext. No.:"
		value="#{invoiceDetail.invoice.contact.phoneExtensionVal}" columns="8"
		maximumLength="8" readOnly="#{! invoiceDetail.editable}" />
</af:panelForm>
<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText label="Secondary Phone No.:"
		value="#{invoiceDetail.invoice.contact.secondaryPhoneNo}"
		id="secondaryPhoneNo" columns="14" maximumLength="14"
		readOnly="#{! invoiceDetail.editable}"
		converter="#{infraDefs.phoneNumberConverter}" />
	<af:inputText label="Secondary Ext. No.:"
		value="#{invoiceDetail.invoice.contact.secondaryExtensionVal}"
		columns="8" maximumLength="8" readOnly="#{! invoiceDetail.editable}" />
</af:panelForm>
<af:panelForm rows="2" maxColumns="2" labelWidth="150" width="600">
	<af:inputText label="Mobile Phone No.:"
		value="#{invoiceDetail.invoice.contact.mobilePhoneNo}"
		id="mobilePhoneNo" columns="14" maximumLength="14"
		readOnly="#{! invoiceDetail.editable}"
		converter="#{infraDefs.phoneNumberConverter}" />
	<af:inputText label="Fax No.:"
		value="#{invoiceDetail.invoice.contact.faxNo}" id="faxNo" columns="14"
		maximumLength="14" readOnly="#{! invoiceDetail.editable}"
		converter="#{infraDefs.phoneNumberConverter}" />
	<af:inputText label="Pager No.:"
		value="#{invoiceDetail.invoice.contact.pagerNo}" id="pagerNo"
		columns="14" maximumLength="14" readOnly="#{! invoiceDetail.editable}"
		converter="#{infraDefs.phoneNumberConverter}" />
	<af:inputText label="Pager PIN No.:"
		value="#{invoiceDetail.invoice.contact.pagerPinNo}" columns="10"
		maximumLength="10" readOnly="#{! invoiceDetail.editable}" />
</af:panelForm>

<af:panelHeader text="Contact Email" size="0" />
<af:panelForm rows="2" maxColumns="1" labelWidth="150" width="600">
	<af:inputText label="Email:"
		value="#{invoiceDetail.invoice.contact.emailAddressTxt}"
		showRequired="true" columns="80" maximumLength="80"
		readOnly="#{! invoiceDetail.editable}" id="emailAddressTxt"
		showRequired="true" />
	<af:inputText label="Secondary Email:"
		value="#{invoiceDetail.invoice.contact.emailAddressTxt2}"
		columns="80" maximumLength="80"
		readOnly="#{! invoiceDetail.editable}" id="emailAddressTxt2" />
</af:panelForm>
