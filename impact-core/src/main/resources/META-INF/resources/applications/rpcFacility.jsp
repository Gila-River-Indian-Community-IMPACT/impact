<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:panelGroup>
  <af:showDetailHeader text="Modification Request Information" disclosed="true">
    <%
    /* General info begin */
    %>
    <af:panelForm labelWidth="200">
      <af:selectInputDate id="applicationReceivedDate"
        label="Date request received :"
        readOnly="#{!applicationDetail.editMode}"
        rendered="#{applicationDetail.internalApp}"
        value="#{applicationDetail.application.receivedDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
      <af:selectOneChoice id="rpcTypeChoice" label="Modification Type :"
        unselectedLabel="Please select"
        readOnly="true"
        value="#{applicationDetail.application.rpcTypeCd}"
        autoSubmit="true">
        <f:selectItems value="#{applicationReference.rpcTypeDefs}" />
      </af:selectOneChoice>
      <af:selectOneChoice id="permitIdChoice"
        label="Permit to be Modified:" unselectedLabel="Please select"
        readOnly="true"
        rendered="#{not empty applicationDetail.application.rpcTypeCd}"
        value="#{applicationDetail.application.permitId}">
        <f:selectItems value="#{applicationDetail.permitsForRpc}" />
      </af:selectOneChoice>
    </af:panelForm>
    <af:panelForm>
      <af:outputLabel for="permitReasonPanel"
        value="Please summarize the reason this permit is being modified." />
      <af:panelGroup id="permitReasonPanel" layout="vertical">
        <af:inputText id="permitReasonText"
          readOnly="#{! applicationDetail.editMode}"
          value="#{applicationDetail.application.applicationDesc}"
          rows="4" columns="120" maximumLength="1000" />
      </af:panelGroup>
    </af:panelForm>
  </af:showDetailHeader>
    <%
    /* General info end */
    %>
  
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

  <%
  /* Contact info begin */
  %>
  <af:showDetailHeader disclosed="true"
    text="Modification Request Contact">
    <f:subview id="contact">
      <jsp:include page="contact.jsp" />
    </f:subview>
  </af:showDetailHeader>
  <%
  /* Contact info end */
  %>

</af:panelGroup>
