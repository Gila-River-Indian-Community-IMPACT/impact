<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm width="600px">
	<af:panelForm rows="4" maxColumns="2" labelWidth="130px" width="98%">
		<af:inputText label="Contact ID:"
			value="#{mergeContact.baseContact.cntId}" columns="40"
			readOnly="true" id="contact_id" />

		<af:inputText label="First Name:"
			value="#{mergeContact.baseContact.firstNm}" columns="40"
			maximumLength="40" readOnly="true" id="firstNm" />

		<af:inputText label="Last Name:"
			value="#{mergeContact.baseContact.lastNm}" columns="40"
			maximumLength="40" readOnly="true" id="lastNm" />

		<af:inputText label="Primary Phone No.:"
			value="#{mergeContact.baseContact.phoneNo}" columns="14"
			maximumLength="14" readOnly="true" id="phoneNo"
			converter="#{infraDefs.phoneNumberConverter}" />

		<af:selectBooleanCheckbox label="Active:"
			value="#{mergeContact.baseContact.active}"
			rendered="#{contactDetail.dapcUser}" readOnly="true" id="statusFlag" />

		<af:selectOneChoice value="#{mergeContact.baseContact.companyId}"
			readOnly="true" label="Company Name: " id="contactCompany">
			<f:selectItems value="#{infraDefs.companies}" />
		</af:selectOneChoice>

		<af:panelGroup layout="horizontal"
			rendered="#{! contactDetail.editable}">
			<af:inputText label="Email: " readOnly="true" />
			<af:goLink text="#{mergeContact.baseContact.emailAddressTxt}"
				destination="mailto:#{mergeContact.baseContact.emailAddressTxt}" />
		</af:panelGroup>

	</af:panelForm>
</af:panelForm>