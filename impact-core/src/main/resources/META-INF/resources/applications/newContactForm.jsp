<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="5" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice label="Title:"
		value="#{applicationDetail.createApplicationContact.titleCd}">
		<f:selectItems value="#{infraDefs.contactTitles}" />
	</af:selectOneChoice>
	<af:inputText label="First Name:"
		value="#{applicationDetail.createApplicationContact.firstNm}" columns="40"
		maximumLength="40" id="firstNm"
		showRequired="#{applicationDetail.createApplicationContact.companyName == null || applicationDetail.createApplicationContact.companyName == ''}" />
	<af:inputText label="Preferred Name:"
		value="#{applicationDetail.createApplicationContact.preferredName}"
		columns="40" maximumLength="40" id="preferredName" />
	<af:inputText label="Middle Name:"
		value="#{applicationDetail.createApplicationContact.middleNm}" columns="40"
		maximumLength="40" id="middleNm" />
	<af:inputText label="Last Name:"
		value="#{applicationDetail.createApplicationContact.lastNm}" columns="40"
		maximumLength="40" id="lastNm"
		showRequired="#{applicationDetail.createApplicationContact.companyName == null || applicationDetail.createApplicationContact.companyName == ''}" />
	<af:inputText label="Suffix:"
		value="#{applicationDetail.createApplicationContact.suffixCd}" columns="6"
		maximumLength="6" />
	<af:inputText label="Email:"
		value="#{applicationDetail.createApplicationContact.emailAddressTxt}"
		columns="40" maximumLength="40" id="emailAddressTxt"
		showRequired="true" />
	<af:inputText label="Secondary Email:"
		value="#{applicationDetail.createApplicationContact.emailAddressTxt2}"
		columns="40" maximumLength="40" id="emailAddressTxt2" />
	<af:inputText label="Job Title:"
		value="#{applicationDetail.createApplicationContact.companyTitle}"
		columns="40" maximumLength="100" id="companyTitle" />
	<af:selectOneChoice
		value="#{applicationDetail.createApplicationContact.companyId}"
		readOnly="true" label="Company Name: " showRequired="true"
		id="contactCompany">
		<f:selectItems value="#{infraDefs.companies}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="2" maxColumns="1" labelWidth="150" width="600"
	partialTriggers="loadAddress">
	<af:inputText label="Address 1:"
		value="#{applicationDetail.createApplicationContact.address.addressLine1}"
		columns="60" maximumLength="100" id="addressLine1"
		showRequired="#{!applicationDetail.createApplicationContact.onSiteContact}" />
	<af:inputText label="Address 2:"
		value="#{applicationDetail.createApplicationContact.address.addressLine2}"
		columns="60" maximumLength="100" />
</af:panelForm>
<af:panelForm rows="2" maxColumns="2" labelWidth="150" width="600"
	partialTriggers="loadAddress">
	<af:inputText label="City:"
		value="#{applicationDetail.createApplicationContact.address.cityName}"
		columns="30" maximumLength="50" id="cityName"
		showRequired="#{!applicationDetail.createApplicationContact.onSiteContact}" />
	<af:selectOneChoice label="State:"
		value="#{applicationDetail.createApplicationContact.address.state}"
		id="state"
		showRequired="#{!applicationDetail.createApplicationContact.onSiteContact}">
		<f:selectItems value="#{infraDefs.states}" />
	</af:selectOneChoice>
	<af:inputText label="Zip Code:"
		value="#{applicationDetail.createApplicationContact.address.zipCode}"
		id="zipCode"
		showRequired="#{!applicationDetail.createApplicationContact.onSiteContact}" />
</af:panelForm>
<af:objectSpacer width="100%" height="15" />
<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText label="Primary Phone No.:"
		value="#{applicationDetail.createApplicationContact.phoneNo}" columns="14"
		maximumLength="14" id="phoneNo" showRequired="true"
		converter="#{infraDefs.phoneNumberConverter}" />
	<af:inputText label="Primary Ext. No.:"
		value="#{applicationDetail.createApplicationContact.phoneExtensionVal}"
		columns="8" maximumLength="8" />
</af:panelForm>
<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText label="Secondary Phone No.:"
		value="#{applicationDetail.createApplicationContact.secondaryPhoneNo}"
		id="secondaryPhoneNo" columns="14" maximumLength="14"
		converter="#{infraDefs.phoneNumberConverter}" />
	<af:inputText label="Secondary Ext. No.:"
		value="#{applicationDetail.createApplicationContact.secondaryExtensionVal}"
		columns="8" maximumLength="8" />
</af:panelForm>
<af:panelForm rows="2" maxColumns="2" labelWidth="150" width="600">
	<af:inputText label="Mobile Phone No.:"
		value="#{applicationDetail.createApplicationContact.mobilePhoneNo}"
		id="mobilePhoneNo" columns="14" maximumLength="14"
		converter="#{infraDefs.phoneNumberConverter}" />
	<af:inputText label="Fax No.:"
		value="#{applicationDetail.createApplicationContact.faxNo}" id="faxNo"
		columns="14" maximumLength="14"
		converter="#{infraDefs.phoneNumberConverter}" />
</af:panelForm>