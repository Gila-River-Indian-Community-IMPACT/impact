<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%
/* General info begin */
%>
<af:panelForm maxColumns="2" rows="2" partialTriggers="reasons early">
  <af:selectManyCheckbox label="Reason(s) :" valign="top" id="reasons"
    autoSubmit="true" readOnly="#{!permitDetail.editMode}" 
    value="#{permitDetail.permit.permitReasonCDs}">
    <f:selectItems value="#{permitDetail.permitReasons}" />
   </af:selectManyCheckbox>
  <%-- not valid for WY<af:outputText value ="" rendered="#{!permitDetail.originalPermitNeeded}"/>
  <af:selectOneChoice label="Original Permit No :"
    readOnly="#{! permitDetail.editMode}"
    rendered="#{permitDetail.originalPermitNeeded}"
    unselectedLabel="None"
    value="#{permitDetail.permit.originalPermitNo}">
    <mu:selectItems value="#{permitDetail.activePermits}" />
  </af:selectOneChoice>  --%>
   <%-- Not required for WY
   <afh:cellFormat  rendered="#{permitDetail.permit.renewal}">
    <af:selectBooleanCheckbox label="Issue Early Renewal :" id="early"
      readOnly="#{! permitDetail.editMode}" autoSubmit="true"
      rendered="#{permitDetail.permit.renewal}"
      value="#{permitDetail.permit.earlyRenewalFlag}" />
    <af:outputText value="WARNING: This permit will not be issued final until the date is < or = 540 days before the next expiration date." 
       inlineStyle="color: orange; font-weight: bold; font-size:11px;" rendered="#{permitDetail.permit.earlyRenewalFlag}" />
  </afh:cellFormat>
  <af:outputText value ="" rendered="#{!permitDetail.permit.renewal}"/>
  --%>
</af:panelForm>
  
    <af:inputText label="Legacy Permit Number: " 
      readOnly="#{ !permitDetail.editMode or !permitDetail.stars2Admin }"
      value="#{permitDetail.permit.legacyPermitNumber}"
      rendered="#{ (not empty permitDetail.permit.legacyPermitNumber) or permitDetail.editMode }" />
      
 <af:inputText id="tvDesc" label="Permit Description :" columns="80" rows="3"
	inlineStyle="#{!permitDetail.editMode ? 'border-width: 0px; background-color: #ffffff;' : ''}"
    readOnly="#{! permitDetail.editMode}" maximumLength="2048"
    value="#{permitDetail.permit.description}" />
  <%-- not valid for WY
  <af:inputText label="ERAC Case Number :" columns="20"
    readOnly="#{! permitDetail.editMode}" maximumLength="20"
    value="#{permitDetail.permit.eracCaseNumber}" /> --%>
<af:panelGroup layout="horizontal">
	<af:selectBooleanCheckbox id="receiptLetterSent"
		inlineStyle="margin-left: 0px;" label="Receipt Letter Sent:"
		readOnly="#{!permitDetail.editMode}"
		value="#{permitDetail.permit.receiptLetterSent}"/>
	<af:objectSpacer width="50" height="1"/>
	<%-- not valid for WY
	<af:selectOneChoice label="Superseded Permit :"
          autoSubmit="TRUE" readOnly="#{! permitDetail.editMode}"
          id="supersededPermit" unselectedLabel=""
          value="#{permitDetail.permit.supersededPermitID}">
          <mu:selectItems value="#{permitDetail.supersedablePermits}" />
    </af:selectOneChoice>
    <af:objectSpacer width="50" height="1"/>
    <af:selectInputDate id="supersededDate" label="Superseded Date :" readOnly="true"
          rendered="#{!(permitDetail.permit.supersededDate == null)}"
          value="#{permitDetail.permit.supersededDate}" /> --%>
</af:panelGroup>
  
<af:panelGroup layout="horizontal">
    <af:selectOneChoice id="receivedComments" unselectedLabel=" " label="Received Comments:"
		autoSubmit="true" readOnly="#{!permitDetail.editMode}"
		value="#{permitDetail.permit.receivedComments}">
		<f:selectItems value="#{permitReference.receivedCommentsTypes}" />
	</af:selectOneChoice>
	<af:objectSpacer width="40" height="1"/>
    <af:selectBooleanCheckbox id="updateFacilityDtlPTETable" 
    	label="Check/Update Facility Inventory PTE Table:"
		inlineStyle="margin-left: 0px;" readOnly="#{!permitDetail.editMode}"
		value="#{permitDetail.permit.updateFacilityDtlPTETableComments}"/>

</af:panelGroup>
<%-- not valid for WY <afh:tableLayout width="100%">
   <afh:rowLayout
    rendered="#{!(permitDetail.permit.revocationDate == null)}">
    <afh:cellFormat width="50%" halign="left" valign="top">
      <af:panelForm labelWidth="44%" fieldWidth="56%">
        <af:selectOneChoice label="Rescission Number :" readOnly="true"
          id="revocation"
          value="#{permitDetail.permit.revocationAppIDtoEUs}">
          <mu:selectItems value="#{permitDetail.rprNumbers}" />
        </af:selectOneChoice>
      </af:panelForm>
    </afh:cellFormat>
    <afh:cellFormat width="50%" halign="left" valign="top">
      <af:panelForm labelWidth="44%" fieldWidth="56%">
        <af:selectInputDate label="Rescission Date :" readOnly="true"
          value="#{permitDetail.permit.revocationDate}" />
      </af:panelForm>
    </afh:cellFormat>
  </afh:rowLayout>
</afh:tableLayout>--%>

<af:objectSpacer height="5"/>

<af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" id="dapcHearingReqd" label="Hearing Requested?"
	readOnly="#{!permitDetail.editMode}"
	value="#{permitDetail.permit.dapcHearingReqd}" />