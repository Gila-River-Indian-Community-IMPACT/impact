<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:panelGroup>
  <af:panelHeader text="General information">
    <%
    /* General info begin */
    %>
    <af:panelForm labelWidth="200" partialTriggers="rep">
      <af:selectInputDate id="applicationReceivedDate"
        rendered="#{applicationDetail.internalApp}"
        label="Request Received Date :"
        readOnly="#{! applicationDetail.editMode}"
        value="#{applicationDetail.application.receivedDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
      <af:selectOneChoice label="RPR reason :"
        unselectedLabel="Please select"
        readOnly="#{! applicationDetail.editMode}"
        value="#{applicationDetail.application.rprReasonCd}">
        <f:selectItems
          value="#{applicationReference.rprReasonDefs.items[(empty applicationDetail.application.rprReasonCd? '': applicationDetail.application.rprReasonCd)]}" />
      </af:selectOneChoice>
      <af:selectOneChoice label="Permit Number :" readOnly="true"
        value="#{applicationDetail.application.permitId}">
        <f:selectItems value="#{applicationDetail.permitList}" />
      </af:selectOneChoice>
      <af:selectBooleanCheckbox label="Rescind Entire Permit :"
        readOnly="#{! applicationDetail.editMode}" id="rep" autoSubmit="true"
        value="#{applicationDetail.application.revokeEntirePermit}" />
      <af:inputText label="Basis for Rescission :" columns="80" rows="3"
        readOnly="#{! applicationDetail.editMode}"
        rendered="#{applicationDetail.application.revokeEntirePermit}"
        inlineStyle="#{!applicationDetail.editMode ? 'border-width: 0px; background-color: #ffffff;' : ''}"
        value="#{applicationDetail.application.basisForRevocation}" />
      <af:inputText label="Regulated Community Description :"
        rendered="false" columns="80" rows="3"
        readOnly="#{! applicationDetail.editMode}"
        inlineStyle="#{!applicationDetail.editMode ? 'border-width: 0px; background-color: #ffffff;' : ''}"
        value="#{applicationDetail.application.regulatedCmntyDsc}" />
      <af:inputText label="AQD Description :" columns="80" rows="3"
        readOnly="#{! applicationDetail.editMode}" rendered="false"
        inlineStyle="#{!applicationDetail.editMode ? 'border-width: 0px; background-color: #ffffff;' : ''}"
        value="#{applicationDetail.application.applicationDesc}" />

      <af:selectOneChoice label="Disposition :" id="Disposition"
        autoSubmit="true" unselectedLabel="Pending" readOnly="true"
        value="#{applicationDetail.application.dispositionFlag}">
        <f:selectItem itemLabel="Completed" itemValue="c" />
        <f:selectItem itemLabel="Returned" itemValue="r" />
        <f:selectItem itemLabel="Issued Proposed" itemValue="1" />
        <f:selectItem itemLabel="Issued Proposed/Final" itemValue="2" />
        <f:selectItem itemLabel="Issued Final" itemValue="3" />
      </af:selectOneChoice>

    </af:panelForm>
    <%
    /* General info end */
    %>
  </af:panelHeader>

  <%
  /* Air Contaminant Sources begin */
  %>
  <af:showDetailHeader text="Air Contaminant Sources in this Application" disclosed="false">
    <f:subview id="includedEUs">
      <jsp:include page="includedEUs.jsp" />
    </f:subview>
  </af:showDetailHeader>
  <%
  /* Air Contaminant Sources end */
  %>
  <af:showDetailHeader text="Issuances" size="2" disclosed="true"
    rendered="#{applicationDetail.application.submittedDate != null}">
    <jsp:include page="../util/issuancesTable.jsp" flush="true" />
  </af:showDetailHeader>
</af:panelGroup>
