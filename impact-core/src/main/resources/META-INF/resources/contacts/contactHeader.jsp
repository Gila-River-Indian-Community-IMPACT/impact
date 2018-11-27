<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<af:panelBox background="light" width="1080">
	<af:panelForm rows="2" maxColumns="3" labelWidth="30%" width="100%">
		<af:inputText label="Contact ID:" readOnly="True"
			value="#{contactDetail.contact.cntId}" />
		<af:inputText label="First Name:" readOnly="True"
			value="#{contactDetail.contact.firstNm}" />

		<af:inputText label="Last Name:" readOnly="True"
			value="#{contactDetail.contact.lastNm}" />
		<af:inputText label="Address 1:" readOnly="True"
			value="#{contactDetail.contact.address.addressLine1}" />

		<af:inputText label="City:" readOnly="True"
			value="#{contactDetail.contact.address.cityName}" />
		<af:inputText label="State:" readOnly="True"
			value="#{contactDetail.contact.address.state}" />
		<af:inputText label="Zip Code:" readOnly="True"
			value="#{contactDetail.contact.address.zipCode}" />
		<af:inputText label="Primary Phone No.:" readOnly="True"
			value="#{contactDetail.contact.phoneNo}"
			converter="#{infraDefs.phoneNumberConverter}" />
		<af:inputText label="Email:" readOnly="True"
			value="#{contactDetail.contact.emailAddressTxt}" />
	</af:panelForm>
</af:panelBox>