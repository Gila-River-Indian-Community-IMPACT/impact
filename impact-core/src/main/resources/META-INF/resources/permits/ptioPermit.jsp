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
<af:panelForm partialTriggers="renewalRadioBtns">
  <af:panelGroup layout="horizontal">
  	<af:outputFormatted value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
  		rendered="#{permitDetail.permit.feptio}"/>
	 <af:selectBooleanCheckbox text="FENSR to avoid Title V"
	    rendered="#{permitDetail.permit.feptio}"
	    readOnly="#{! permitDetail.editMode}"
	    value="#{permitDetail.permit.fePtioTvAvoid}" />
    </af:panelGroup>
</af:panelForm>
<af:panelForm maxColumns="2" rows="6" partialTriggers="reasons early">
	<af:selectManyCheckbox label="Reason:" valign="top" id="reasons"
		readOnly="#{!permitDetail.editMode}" autoSubmit="true"
		value="#{permitDetail.permit.permitReasonCDs}">
		<f:selectItems value="#{permitDetail.permitReasons}" />
	</af:selectManyCheckbox>
	<%-- not valid for WY
	<af:selectOneChoice label="Original Permit No :"
		readOnly="#{! permitDetail.editMode}"
		rendered="#{permitDetail.originalPermitNeeded}" unselectedLabel="None"
		value="#{permitDetail.permit.originalPermitNo}">
		<mu:selectItems value="#{permitDetail.activePermits}" />
	</af:selectOneChoice>
	<af:outputText value=""
		rendered="#{!permitDetail.originalPermitNeeded}" /> --%>
	<%-- General Permit not valid for WY 
	<af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" label="General Permit :"
    readOnly="#{! permitDetail.editMode}"
    disabled="#{! permitDetail.generalPermitAllowed}"
    value="#{permitDetail.permit.generalPermit}" /> --%>
	<%-- Not valid for WY 
  <af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" label="Express Permit :" 
    readOnly="#{! permitDetail.editMode}"
    disabled="#{! permitDetail.expressPermitAllowed}"
    value="#{permitDetail.permit.express}" />
  <af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" label="Rush Permit :"
    readOnly="#{! permitDetail.editMode}"
    value="#{permitDetail.permit.rushFlag}" /> --%>
	<%-- not valid for WY
	<af:outputText value=""
		rendered="#{permitDetail.permit.permitType != 'PTIO'" />
	<afh:cellFormat rendered="#{permitDetail.permit.renewal}">
		<af:selectBooleanCheckbox label="Issue Upon Expiration :" id="early"
			readOnly="#{! permitDetail.editMode}" autoSubmit="true"
			rendered="#{permitDetail.permit.renewal}"
			value="#{permitDetail.permit.earlyRenewalFlag}" />
		<af:outputText
			value="WARNING: This permit will not be issued final until the facility's NSR expiration date. If it must be issued sooner deleselect this option and contact the CO PIER Supervisor."
			inlineStyle="color: orange; font-weight: bold; font-size:11px;"
			rendered="#{permitDetail.permit.earlyRenewalFlag}" />
	</afh:cellFormat>
	<af:outputText value="" rendered="#{!permitDetail.permit.renewal}" /> --%>
</af:panelForm>

    <af:inputText label="Legacy Permit Number: " 
      readOnly="#{ !permitDetail.editMode or !permitDetail.stars2Admin }"
      value="#{permitDetail.permit.legacyPermitNumber}" 
      rendered="#{ (not empty permitDetail.permit.legacyPermitNumber) or permitDetail.editMode }" />
      
<af:inputText id="ptioDesc" label="NSR  Permit Description:" columns="80" rows="3"
    inlineStyle="#{!permitDetail.editMode ? 'border-width: 0px; background-color: #ffffff;' : ''}"
    readOnly="#{! permitDetail.editMode}" maximumLength="2048"
    value="#{permitDetail.permit.description}" />
    
<af:selectBooleanCheckbox id="receiptLetterSent"
		inlineStyle="margin-left: 0px;" label="Receipt Letter Sent:"
		readOnly="#{!permitDetail.editMode}"
		value="#{permitDetail.permit.receiptLetterSent}"/>
		
<af:objectSpacer height="5"/>

<af:panelGroup layout="horizontal" partialTriggers="otherTypeOfDemonstrationReq">
	<af:selectOneChoice id="permitActionType" label="Type of Action:"
		autoSubmit="true" readOnly="#{!permitDetail.editMode}"
		value="#{permitDetail.permit.permitActionType}">
		<f:selectItems value="#{permitReference.permitActionTypes }" />
	</af:selectOneChoice>
	<af:objectSpacer width="50" height="1"/>
  	<af:selectOneChoice id="otherTypeOfDemonstrationReq" label="Other Type of Demonstration:"
		readOnly="#{!permitDetail.editMode}" autoSubmit="true"
		valueChangeListener="#{permitDetail.permit.otherTypeOfDemonstrationReqChanged}"
		value="#{permitDetail.permit.otherTypeOfDemonstrationReq}">
		<f:selectItem itemLabel="Yes" itemValue="Y" />
		<f:selectItem itemLabel="No" itemValue="N" />
	</af:selectOneChoice>

	<af:objectSpacer width="50" height="1"/>
	<af:selectBooleanCheckbox label="Demonstration/Offsets Verified:" id="offsetInformationVerified"
		readOnly="false" rendered="#{permitDetail.permit.otherTypeOfDemonstrationReq eq 'Y' && permitDetail.editMode}"
		value="#{permitDetail.permit.offsetInformationVerified}" />
	<af:inputText label="Demonstration/Offsets Verified:" readOnly="true" id="offsetInformationVerifiedYesNo"
		value="#{permitDetail.permit.offsetInformationVerifiedYesNo}"
		rendered="#{permitDetail.permit.otherTypeOfDemonstrationReq eq 'Y' && !permitDetail.editMode}" />

	<af:objectSpacer width="50" height="1"/>
	<af:selectOneChoice id="modelingRequired" label="Modeling:" unselectedLabel=" " valign="top"
		autoSubmit="true" readOnly="#{!permitDetail.editMode}"  
		value="#{permitDetail.permit.modelingRequired}">
		<f:selectItem itemLabel="Yes" itemValue="Y" />
		<f:selectItem itemLabel="No" itemValue="N" />
	</af:selectOneChoice>
</af:panelGroup>

<af:objectSpacer height="5"/>

<af:panelGroup layout="horizontal" partialTriggers="permitActionType">
	<af:selectOneChoice id="subjectToPsd" label="Subject to PSD:" unselectedLabel=" "
		autoSubmit="true" rendered="#{permitDetail.actionTypePermit}"
		readOnly="#{!permitDetail.editMode}"
		value="#{permitDetail.permit.subjectToPSD}">
		<f:selectItem itemLabel="Yes" itemValue="Y" />
		<f:selectItem itemLabel="No" itemValue="N" />
	</af:selectOneChoice>
	<af:objectSpacer width="60" height="1" rendered="#{permitDetail.actionTypePermit}"/>
	<af:selectOneChoice id="subjectToNANSR" label="Subject to Non-Attainment NSR:" unselectedLabel=" "
		autoSubmit="true" rendered="#{permitDetail.actionTypePermit}"
		readOnly="#{!permitDetail.editMode}"
		value="#{permitDetail.permit.subjectToNANSR}">
		<f:selectItem itemLabel="Yes" itemValue="Y" />
		<f:selectItem itemLabel="No" itemValue="N" />
	</af:selectOneChoice>
</af:panelGroup>

<af:objectSpacer height="5"/>

<af:panelGroup layout="horizontal">
	<af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" label="Avoiding Major NSR Synthetic Minor:"
    	readOnly="#{! permitDetail.editMode}"
    	value="#{permitDetail.permit.smtv}" />
    <af:objectSpacer width="50" height="1"/>
    <af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" label="Netting:"
    	readOnly="#{! permitDetail.editMode}"
    	value="#{permitDetail.permit.netting}" />	
    <%-- not valid for WY
  	<af:objectSpacer width="50" height="1"/>
  	<af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" label="Toxic Review:" 
    	readOnly="#{! permitDetail.editMode}"
    	value="#{permitDetail.permit.toxicReview}" /> --%>		
 </af:panelGroup>
 <af:objectSpacer height="5"/>
 <af:panelGroup layout="horizontal"> 	
    <af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" label="Avoiding Major GHG Synthetic Minor:" 
    	readOnly="#{! permitDetail.editMode}"
    	value="#{permitDetail.permit.avoidMajorGHGSM}" />
    <af:objectSpacer width="50" height="1"/>
    <af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" label="Modeling Submitted:"
    	readOnly="#{! permitDetail.editMode}"
    	value="#{permitDetail.permit.modelingSubmitted}" />	
  	<%-- not valid for WY
    <af:objectSpacer width="50" height="1"/>
    <af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" label="Variance:" 
    	readOnly="#{! permitDetail.editMode}"
    	rendered="#{! permitDetail.permit.tv}"
    	value="#{permitDetail.permit.variance}" /> --%>	
</af:panelGroup>

<af:objectSpacer height="5"/>

<af:panelGroup layout="horizontal" partialTriggers="permitActionType modelingRequired">
    <af:selectOneChoice id="receivedComments" unselectedLabel=" " label="Received Comments:"
		autoSubmit="true" readOnly="#{!permitDetail.editMode}"
		rendered="#{permitDetail.actionTypePermit}"
		value="#{permitDetail.permit.receivedComments}">
		<f:selectItems value="#{permitReference.receivedCommentsTypes}" />
	</af:selectOneChoice>
	<af:objectSpacer width="40" height="1" rendered="#{permitDetail.actionTypePermit}"/>
    <af:selectBooleanCheckbox id="updateFacilityDtlPTETable" 
    	label="Check/Update Facility Inventory PTE Table:"
		inlineStyle="margin-left: 0px;" readOnly="#{!permitDetail.editMode}"
		value="#{permitDetail.permit.updateFacilityDtlPTETableComments}"/>
	<af:objectSpacer width="30" height="1"/>
	<af:selectInputDate id="modelingCompletedDate" label="Modeling Completed:" valign="top" 
		autoSubmit="true" rendered="#{permitDetail.permit.modelingRequired == 'Y'}"
		readOnly="#{! permitDetail.editMode}"
		value="#{permitDetail.permit.modelingCompletedDate}">
	</af:selectInputDate>
</af:panelGroup>

<af:panelGroup layout="horizontal" partialTriggers="permitActionType">
	<af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" id="publicNoticeNeeded" label="Public Notice Needed?"
	  	autoSubmit="true"
	  	rendered="#{permitDetail.actionTypePermit}"
	  	readOnly="#{!permitDetail.editMode}"
		value="#{permitDetail.permit.issueDraft}"/>
	<af:objectSpacer width="10" height="1"/>	
	<af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" id="dapcHearingReqd" label="Hearing Requested?"
		autoSubmit="true"
	    rendered="#{permitDetail.actionTypePermit}"
	    readOnly="#{!permitDetail.editMode}"
		value="#{permitDetail.permit.dapcHearingReqd}" />
</af:panelGroup>

<%-- not valid for WY
<afh:tableLayout width="100%"
  rendered="#{permitDetail.permit.convertedToPTI}">
  <afh:rowLayout>
    <afh:cellFormat width="50%" halign="left" valign="top">
      <af:panelForm labelWidth="44%" fieldWidth="56%">
        <af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" label="Converted:" id="converted"
          autoSubmit="TRUE" readOnly="true"
          value="#{permitDetail.permit.convertedToPTI}" />
      </af:panelForm>
    </afh:cellFormat>
    <afh:cellFormat width="50%" halign="left" valign="top">
      <af:panelForm labelWidth="44%" fieldWidth="56%"
        partialTriggers="converted">
        <af:selectInputDate label="Converted Date:"
          rendered="#{permitDetail.permit.convertedToPTI}"
          readOnly="true"
          value="#{permitDetail.permit.convertedToPTIDate}" />
      </af:panelForm>
    </afh:cellFormat>
  </afh:rowLayout>
</afh:tableLayout> --%>

