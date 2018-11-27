<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:panelGroup layout="vertical">

  <af:panelForm id="preAppForm">
    <af:selectInputDate id="applicationReceivedDate"
      label="Date application received :"
      readOnly="#{! applicationDetail.editMode}"
      rendered="#{applicationDetail.internalApp}"
      value="#{applicationDetail.application.receivedDate}">
      <af:validateDateTimeRange
        maximum="#{applicationDetail.maxReceivedDate}" />
    </af:selectInputDate>
    <af:objectSeparator />
  </af:panelForm>
  <%
      /* Section 1 */
  %>
  <%
      /* Item 1 */
  %>
  <af:showDetailHeader text="Reason for Application" disclosed="true">
    <af:panelForm partialTriggers="tivAppPurposeChoice">
      <af:selectOneRadio id="tivAppPurposeChoice"
        label="Please Identify the reason for this application :"
        readOnly="#{! applicationDetail.editMode}"
        value="#{applicationDetail.application.appPurposeCd}"
        autoSubmit="true">
        <f:selectItems value="#{applicationReference.tvAppPurposeDefs}" />
      </af:selectOneRadio>
      <af:selectOneChoice id="tivPermitReasonChoice"
        readOnly="#{! applicationDetail.editMode}"
        rendered="#{applicationDetail.permitReasonSelectable}"
        value="#{applicationDetail.application.reasonCd}"
        unselectedLabel="Please Select">
        <f:selectItems value="#{applicationReference.permitReasonDefs}" />
      </af:selectOneChoice>
    </af:panelForm>
    <af:panelForm>
      <af:outputText
        value="Please summarize the reason this permit is being applied for.  
        This text will be in the public notice that will appear in the newspaper 
        of the county where the facility is located"
        inlineStyle="font-size: 13px;font-weight:bold" />
      <af:inputText id="tivPermitReasonText"
        readOnly="#{! applicationDetail.editMode}"
        value="#{applicationDetail.application.applicationDesc}"
        rows="4" columns="135" maximumLength="1000" />
    </af:panelForm>
  </af:showDetailHeader>
  <%
      /* End Item 1 */
  %>

  <%
      /* Air Contaminant Sources begin */
  %>
  <af:showDetailHeader
    text="Air Contaminant Sources in this Application" disclosed="false">
      <af:outputText
      	rendered="#{applicationDetail.editMode}"
        value="Add/Remove sources in this application, by first clicking 'Save' or 'Cancel' below, 
        	then 'Select EUs'."
        inlineStyle="font-size: 13px;font-weight:bold" />
      <af:outputText
      	rendered="#{!applicationDetail.editMode}"
        value="Click 'Select EUs' below to add/remove sources in this application."
        inlineStyle="font-size: 13px;font-weight:bold" />
    <f:subview id="includedEUs">
      <jsp:include page="includedEUs.jsp" />
    </f:subview>
  </af:showDetailHeader>
  <%
      /* Air Contaminant Sources end */
  %>
</af:panelGroup>
