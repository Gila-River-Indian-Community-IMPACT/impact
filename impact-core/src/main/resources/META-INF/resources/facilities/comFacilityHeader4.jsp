<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelBox background="light" width="1080">
	<af:panelForm rows="2" maxColumns="4">
		<af:inputText label="Facility ID:" readOnly="True"
			value="#{facilityProfile.facility.facilityId}" />
		<af:inputText label="Facility Type:" readOnly="True"
			value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty facilityProfile.facility.facilityTypeCd ? '' : facilityProfile.facility.facilityTypeCd)]}" />
		<af:inputText label="Facility Name:" readOnly="True"
			value="#{facilityProfile.facility.name}" />
		<af:inputText label="Company Name:" readOnly="True"
			value="#{facilityProfile.facility.owner.company.name}" />
		<af:inputText label="Operating Status:" readOnly="True"
			value="#{facilityReference.operatingStatusDefs.itemDesc[(empty facilityProfile.facility.operatingStatusCd ? '' : facilityProfile.facility.operatingStatusCd)]}"
			inlineStyle="#{facilityProfile.facility.operatingStatusCd eq 'sd' ? 'color: orange; font-weight: bold;' : '' }" />
		<af:selectInputDate label="Version Start Date:"
			shortDesc="The date this version of the facility detail became active"
			readOnly="True" value="#{facilityProfile.facility.startDate}" />
		<af:selectInputDate label="Version End Date:"
			shortDesc="The date this version of the facility detail was replaced by a later version"
			readOnly="True"
			value="#{(facilityProfile.facility.endDate == null) ? 'Current' : facilityProfile.facility.endDate}" />
	</af:panelForm>

	<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
		<afh:rowLayout halign="center">
			<af:outputFormatted
				rendered="#{(facilityProfile.facility.versionId != -1)}"
				inlineStyle="color: orange; font-weight: bold;"
				value="<b>** Note: The data shown below is the current state, the start and end dates above do not apply. **</b>" />
		</afh:rowLayout>
	</afh:tableLayout>
</af:panelBox>