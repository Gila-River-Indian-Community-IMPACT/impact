<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<af:panelBox background="light" width="1000"
	rendered="#{companyProfile.dapcUser}">
	<af:panelForm rows="2" maxColumns="3" labelWidth="30%" width="100%">
		<af:inputText label="Company ID:" readOnly="True"
			value="#{companyProfile.company.cmpId}" />
		<af:inputText label="City:" readOnly="True"
			value="#{companyProfile.company.address.cityName}" />

		<af:inputText label="Company Name:" readOnly="True"
			value="#{companyProfile.company.name}" />
		<af:inputText label="State:" readOnly="True"
			value="#{companyProfile.company.address.state}" />

		<af:inputText label="Company Alias:" readOnly="True"
			value="#{companyProfile.company.alias}" />
		<af:inputText label="Country:" readOnly="True"
			value="#{companyProfile.company.address.countryCd}" />
	</af:panelForm>
</af:panelBox>