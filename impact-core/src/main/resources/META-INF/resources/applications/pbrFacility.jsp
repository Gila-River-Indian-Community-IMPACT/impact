<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<af:panelGroup>
  <af:panelHeader text="General information"
    partialTriggers="pbrTypeChoice">
    <%
    /* General info begin */
    %>
    <af:panelForm labelWidth="21%" fieldWidth="79%"
      partialTriggers="accepted denied noApplicable">
      <af:selectOneChoice id="pbrTypeChoice" label="PBR Type :"
        readOnly="#{! applicationDetail.editMode}" autoSubmit="true"
        immediate="true"
        value="#{applicationDetail.application.pbrTypeCd}">
        <f:selectItems
          value="#{applicationReference.pbrTypeDefs.items[(empty applicationDetail.application.pbrTypeCd ? '': applicationDetail.application.pbrTypeCd)]}" />
      </af:selectOneChoice>
      <af:selectInputDate id="applicationReceivedDate"
        rendered="#{applicationDetail.internalApp}"
        label="Request Received Date :"
        readOnly="#{! applicationDetail.editMode}"
        value="#{applicationDetail.application.receivedDate}">
        <af:validateDateTimeRange maximum="#{applicationDetail.maxReceivedDate}"/>
      </af:selectInputDate>
      <af:selectOneChoice id="pbrReasonChoice" label="PBR Reason :"
        unselectedLabel="Please select"
        readOnly="#{! applicationDetail.editMode}"
        value="#{applicationDetail.application.pbrReasonCd}">
        <f:selectItems
          value="#{applicationReference.pbrReasonDefs.items[(empty applicationDetail.application.pbrReasonCd ? '': applicationDetail.application.pbrReasonCd)]}" />
      </af:selectOneChoice>
      <af:selectInputDate 
        rendered="#{applicationDetail.internalApp && applicationDetail.application.submittedDate != null}"
        label="PBR Effective Date :"
        readOnly="#{! applicationDetail.editMode}"
        value="#{applicationDetail.permit.effectiveDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

      <af:selectBooleanCheckbox label="Requesting Permit Rescission :"
        readOnly="#{! applicationDetail.editMode}" rendered="false"
        value="#{applicationDetail.application.requestingRevocationFlag}" />
      <af:inputText label="Regulated Community Description :"
        rendered="false" columns="80" rows="3"
        readOnly="#{! applicationDetail.editMode}"
        inlineStyle="#{!applicationDetail.editMode ? 'border-width: 0px; background-color: #ffffff;' : ''}"
        value="#{applicationDetail.application.regulatedCmntyDsc}" />
      <af:inputText label="AQD Description :" columns="80" rows="3"
        readOnly="#{! applicationDetail.editMode}" rendered="false"
        inlineStyle="#{!applicationDetail.editMode ? 'border-width: 0px; background-color: #ffffff;' : ''}"
        value="#{applicationDetail.application.applicationDesc}" />

      <af:selectOneChoice label="Disposition :"
        unselectedLabel="Not yet assigned" readOnly="true"
        value="#{applicationDetail.application.dispositionFlag}">
        <f:selectItem itemLabel="Notification Received" itemValue="r" />
        <f:selectItem itemLabel="Accepted" itemValue="a" />
        <f:selectItem itemLabel="Denied" itemValue="d" />
        <f:selectItem itemLabel="Superseded" itemValue="s" />
        <f:selectItem itemLabel="Terminated" itemValue="n" />
      </af:selectOneChoice>
      <af:outputText
        value="By requesting to install and/or operate under this PBR you are authorizing 
        AQD to revoke any applicable permits associated with the air contaminant source(s) 
        in this PBR notification." />

    </af:panelForm>

    <af:objectSpacer width="10" height="10" />

    <afh:rowLayout halign="center">
      <af:outputText value="#{applicationDetail.pbrTypeLongDesc}" />
    </afh:rowLayout>
    <afh:rowLayout halign="center">
      <af:goLink text="Download PBR forms from here"
        targetFrame="_blank"
        rendered="#{! applicationDetail.editMode}"
        destination="../util/externalReferences.jsf" />
    </afh:rowLayout>
  </af:panelHeader>
  <%
  /* General info end */
  %>
  
  <%
  /* Air Contaminant Sources begin */
  %>
  <af:showDetailHeader text="Air Contaminant Sources in this Application" disclosed="false">
    <f:subview id="includedEUs">
      <af:outputText
      	rendered="#{applicationDetail.editMode}"
        value="Add/Remove sources in this application, by first clicking 'Save' or 'Cancel' below, 
        	then 'Select EUs'."
        inlineStyle="font-size: 13px;font-weight:bold" />
      <af:outputText
      	rendered="#{!applicationDetail.editMode}"
        value="Click 'Select EUs' below to add/remove sources in this application."
        inlineStyle="font-size: 13px;font-weight:bold" />
    </f:subview>
  </af:showDetailHeader>
  <%
  /* Air Contaminant Sources end */
  %>

</af:panelGroup>
