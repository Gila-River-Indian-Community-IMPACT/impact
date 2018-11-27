<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelForm id="preAppForm">
  <af:inputText label="Amendment to application number :"
    readOnly="true"
    rendered="#{applicationDetail.application.applicationAmended}"
    value="#{applicationDetail.application.previousApplicationNumber}">
  </af:inputText>
  <af:selectInputDate id="applicationReceivedDate"
    label="Date application received :"
    readOnly="#{! applicationDetail.editMode}"
    value="#{applicationDetail.application.receivedDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
  <af:selectOneRadio id="legacyAppBox"
    label="Is this a legacy State PTO Application?"
    value="#{applicationDetail.application.legacyStatePTOApp}"
    readOnly="#{! applicationDetail.editMode}" layout="horizontal">
    <f:selectItem itemLabel="Yes" itemValue="true" />
    <f:selectItem itemLabel="No" itemValue="false" />
  </af:selectOneRadio>
  <af:objectSeparator />
</af:panelForm>
