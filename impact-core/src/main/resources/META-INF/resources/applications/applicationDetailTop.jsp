<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelBox background="light" width="100%">
	<af:panelForm rows="2" maxColumns="3" width="100%">
		<af:inputText label="Facility ID:" readOnly="true"
			value="#{applicationDetail.application.facility.facilityId}" />
		<af:inputText label="Facility Name:" readOnly="true"
			value="#{applicationDetail.application.facility.name}" />
		<af:inputText
			label="#{applicationDetail.application.requestType} Number: "
			readOnly="true"
			value="#{applicationDetail.application.applicationNumber}" />
		<af:inputText label="Request type: " readOnly="true"
			value="#{applicationDetail.application.requestType}" />
		<af:inputText label="Submitted: " readOnly="true"
			rendered="#{!applicationDetail.publicApp}"
			value="#{empty applicationDetail.application.submittedDate ? 'No': 'Yes'}" />
		<af:inputText label="Entered by: " readOnly="true"
			value="#{applicationDetail.application.submitValue}"
			rendered="#{!(empty applicationDetail.application.submittedDate) && !applicationDetail.publicApp}" />
	</af:panelForm>
</af:panelBox>
