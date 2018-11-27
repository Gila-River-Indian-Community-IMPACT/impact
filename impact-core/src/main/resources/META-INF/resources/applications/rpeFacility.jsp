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
    <af:panelForm labelWidth="200" partialTriggers="denied">
      <af:selectInputDate id="applicationReceivedDate"
        rendered="#{applicationDetail.internalApp}"
        label="Request Received Date :"
        readOnly="#{! applicationDetail.editMode}"
        value="#{applicationDetail.application.receivedDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
      <af:selectInputDate label="Termination Date :"
        readOnly="#{! applicationDetail.editMode}"
        value="#{applicationDetail.application.terminationDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
      <af:selectOneChoice label="Permit Number :" readOnly="true"
        value="#{applicationDetail.application.permitId}">
        <f:selectItems value="#{applicationDetail.permitList}" />
      </af:selectOneChoice>

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
        <f:selectItem itemLabel="Issued" itemValue="i" />
        <f:selectItem itemLabel="Denied" itemValue="d" />
        <f:selectItem itemLabel="Dead-ended" itemValue="e" />
      </af:selectOneChoice>

    </af:panelForm>
    <%
    /* General info end */
    %>
  </af:panelHeader>

  <af:showDetailHeader text="Invoice" size="2" disclosed="true"
    rendered="#{applicationDetail.application.submittedDate != null}">
    <af:panelForm labelWidth="200">

      <af:inputText label="Permit Amount :" columns="10"
        value="#{applicationDetail.oldPermit.totalAmount}"
        readOnly="true">
        <af:convertNumber type='currency' locale="en-US"
          minFractionDigits="2" />
      </af:inputText>
      <af:inputText label="Total Amount :" columns="10"
        value="#{applicationDetail.permit.totalAmount}" readOnly="true">
        <af:convertNumber type='currency' locale="en-US"
          minFractionDigits="2" />
      </af:inputText>
      <af:commandLink action="#{invoiceDetail.submit}"
        disabled="#{applicationDetail.permit.invoice.invoiceId == null}">
        <af:inputText label="Invoice Number :"
          value="#{applicationDetail.permit.invoice.invoiceId}"
          readOnly="true" />
        <t:updateActionListener property="#{invoiceDetail.invoiceId}"
          value="#{applicationDetail.permit.invoice.invoiceId}" />
      </af:commandLink>
      <af:inputText label="Invoice Amount :" columns="10"
        value="#{applicationDetail.permit.invoice.origAmount}"
        readOnly="true">
        <af:convertNumber type='currency' locale="en-US"
          minFractionDigits="2" />
      </af:inputText>
    </af:panelForm>

  </af:showDetailHeader>

  <af:showDetailHeader text="Issuances" size="2" disclosed="true"
    rendered="#{applicationDetail.application.submittedDate != null}">
    <jsp:include page="../util/issuancesTable.jsp" flush="true" />
  </af:showDetailHeader>
</af:panelGroup>
