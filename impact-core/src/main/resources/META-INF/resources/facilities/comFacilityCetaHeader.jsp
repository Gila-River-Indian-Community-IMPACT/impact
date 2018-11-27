<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
  

<af:panelBox background="light" width="1000" rendered="#{facilityProfile.dapcUser}">

<af:panelForm rows="2" maxColumns="3">
  <af:inputText label="Facility ID:" readOnly="True" value="#{facilityProfile.facility.facilityId}" />
  <af:inputText label="Facility Name:" readOnly="True" value="#{facilityProfile.facility.name}" />
  <af:inputText label="Operating Status:"  readOnly="True" value="#{facilityReference.operatingStatusDefs.itemDesc[(empty facilityProfile.facility.operatingStatusCd ? '' : facilityProfile.facility.operatingStatusCd)]}" inlineStyle="#{facilityProfile.facility.operatingStatusCd eq 'sd' ? 'color: orange; font-weight: bold;' : '' }" />
  <af:selectInputDate label="Facility Inventory Start Date:" shortDesc="The date this version of the facility inventory became active" readOnly="True" value="#{facilityProfile.facility.startDate}" />
  <af:selectInputDate label="Facility Inventory End Date:" shortDesc="The date this version of the facility inventory was replaced by a later version" readOnly="True" value="#{(facilityProfile.facility.endDate == null) ? 'Current' : facilityProfile.facility.endDate}" />

</af:panelForm>
</af:panelBox>