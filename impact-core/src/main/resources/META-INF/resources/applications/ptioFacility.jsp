<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:panelGroup id="ptioFacilityPanel" layout="vertical">
	<%
		/* Amended Application info */
	%>
	<af:panelHeader text="NSR Application">
		<af:panelForm id="preAppForm" partialTriggers="legacyStatePTOApp knownIncompleteNSRApp"
			rendered="#{applicationDetail.application.applicationCorrected || applicationDetail.internalApp}">
			<af:inputText label="Correction to application number :"
				inlineStyle="color: orange; font-weight: bold;" readOnly="true"
				rendered="#{applicationDetail.application.applicationCorrected && !applicationDetail.application.applicationAmended}"
				value="#{applicationDetail.application.previousApplicationNumber}">
			</af:inputText>
			<af:inputText label="Reason for correction :"
				inlineStyle="color: orange; font-weight: bold;"
				readOnly="#{! applicationDetail.editMode}"
				rendered="#{applicationDetail.application.applicationCorrected && !applicationDetail.application.applicationAmended}"
				value="#{applicationDetail.application.applicationCorrectedReason}"
				wrap="" columns="100" rows="3">
			</af:inputText>
			<af:inputText label="Amendment to application number :"
				readOnly="true"
				rendered="#{applicationDetail.application.applicationAmended}"
				value="#{applicationDetail.application.previousApplicationNumber}">
			</af:inputText>
			<af:selectInputDate id="applicationReceivedDate"
				label="Date application received :"
				readOnly="#{! applicationDetail.editMode}"
				rendered="#{applicationDetail.internalApp}"
				value="#{applicationDetail.application.receivedDate}">
				<af:validateDateTimeRange
					maximum="#{applicationDetail.maxReceivedDate}" />
			</af:selectInputDate>
			<af:selectOneRadio id="legacyStatePTOApp"
				label="Is this a legacy NSR Application? :"
				autoSubmit="true"
				rendered="#{applicationDetail.internalApp}"
				value="#{applicationDetail.application.legacyStatePTOApp}"
				readOnly="#{! applicationDetail.editMode}" layout="horizontal">
				<f:selectItem itemLabel="Yes" itemValue="true" />
				<f:selectItem itemLabel="No" itemValue="false" />
			</af:selectOneRadio>
			<af:selectOneRadio id="knownIncompleteNSRApp"
				label="Is this a known incomplete NSR Application? :"
				autoSubmit="true"
				rendered="#{applicationDetail.internalApp}"
				value="#{applicationDetail.application.knownIncompleteNSRApp}"
				readOnly="#{!((applicationDetail.editMode || applicationDetail.semiEditMode) && (applicationDetail.NSRAdmin || applicationDetail.stars2Admin))}" layout="horizontal">
				<f:selectItem itemLabel="Yes" itemValue="true" />
				<f:selectItem itemLabel="No" itemValue="false" />
			</af:selectOneRadio>
			<af:objectSeparator />
		</af:panelForm>
		<f:subview id="ptioApplicationInstructions">
			<jsp:include page="ptioApplicationInstructions.jsp" />
		</f:subview>
		<af:objectSpacer height="2" />
		<h:panelGrid border="1" align="center" width="98%" rendered="#{applicationDetail.internalApp}">
			<af:panelForm>
				<af:selectManyCheckbox id="AppEUPurposesCheckbox"
					label="Emission Unit application reason summary.This is a system generated indicator : " disabled="true"
					inlineStyle="font-size:75%;color:#666"
					value="#{applicationDetail.nonEditablePtioApplicationPurposeCds}">
					<f:selectItems
						value="#{applicationDetail.nonEditablePtioApplicationPurposeDefs}" />
				</af:selectManyCheckbox>
				<af:inputText id="otherExplText" readOnly="true"
					label="Please explain : "
					rendered="#{applicationDetail.appPurposeCDsContainOther}"
					value="#{applicationDetail.application.otherPurposeDesc}" rows="4"
					columns="60" maximumLength="1000" />
			</af:panelForm>
		</h:panelGrid>
			<af:panelForm rendered="#{applicationDetail.internalApp}">
				<af:selectOneChoice id="FacilityTypePurposesDropdown"
					label="Facility Type(For AQD use only) : "
					disabled="#{!(applicationDetail.editMode || applicationDetail.semiEditMode)}" unselectedLabel=" "
					inlineStyle="color: #000000;"
					value="#{applicationDetail.application.potentialTitleVFlag}">
					<f:selectItems
						value="#{applicationDetail.ptioApplicationFacilityTypeDefs}"/>
				</af:selectOneChoice>
			</af:panelForm>
			<af:panelGroup partialTriggers="AppSageGrousePurposesDropdown" rendered="#{applicationDetail.internalApp}">
				<af:panelForm>
					<af:selectOneChoice id="AppSageGrousePurposesDropdown"
						label="Sage Grouse(For AQD use only) : " unselectedLabel=" "
						autoSubmit="true" disabled="#{!(applicationDetail.editMode || applicationDetail.semiEditMode)}"
						inlineStyle="color: #000000;"
						value="#{applicationDetail.application.sageGrouseCd}">
						<f:selectItems
							value="#{applicationDetail.ptioApplicationSageGrouseDefs}" />
					</af:selectOneChoice>
					<af:inputText id="agencyName" label="Name of Agency/Department :"
						readOnly="#{!(applicationDetail.editMode || applicationDetail.semiEditMode)}"
						rendered="#{!(empty applicationDetail.application.sageGrouseCd) && applicationDetail.application.sageGrouseCd == '0'}"
						value="#{applicationDetail.application.sageGrouseAgencyName}"
						columns="20" maximumLength="50" />
				</af:panelForm>
			</af:panelGroup>
		</af:panelHeader>
	<af:showDetailHeader text="Purpose of Application" disclosed="true">
		<%
			/* Application purpose begin */
		%>
		<af:panelForm>
			<af:outputLabel for="permitReasonPanel"
				value="Please summarize the reason this permit is being applied for." />
			<af:panelGroup id="permitReasonPanel" layout="vertical">
				<af:inputText id="permitReasonText"
					readOnly="#{! applicationDetail.editMode}"
					value="#{applicationDetail.application.applicationDesc}" rows="4"
					columns="120" maximumLength="1000" />
			</af:panelGroup>
		</af:panelForm>
		<af:panelGroup
			partialTriggers="facilityChangedLocationFlag containH2SFlag">
			<af:panelForm labelWidth="470">
				<af:selectOneChoice id="facilityChangedLocationFlag"
					unselectedLabel=" " autoSubmit="true" showRequired="true"
					readOnly="#{!applicationDetail.editMode}" 
					label="Has the facility changed location or is it a new/greenfield facility? :"
					valueChangeListener="#{applicationDetail.refreshAttachments}" 
					value="#{applicationDetail.application.facilityChangedLocationFlag}">
					<f:selectItem itemLabel="Yes" itemValue="Y" />
					<f:selectItem itemLabel="No" itemValue="N" />
				</af:selectOneChoice>
				<af:selectOneChoice id="landUsePlanningFlag" unselectedLabel=" "
					showRequired="true" autoSubmit="true" readOnly="#{!applicationDetail.editMode}"
					rendered="#{!(empty applicationDetail.application.facilityChangedLocationFlag) && applicationDetail.application.facilityChangedLocationFlag == 'Y'}"
					valueChangeListener="#{applicationDetail.refreshAttachments}" 
					label="Has a Land Use Planning document been included in this application? :"
					value="#{applicationDetail.application.landUsePlanningFlag}">
					<f:selectItem itemLabel="Yes" itemValue="Y" />
					<f:selectItem itemLabel="No" itemValue="N" />
				</af:selectOneChoice>
				<af:selectOneChoice id="containH2SFlag" unselectedLabel=" "
					inlineStyle="" showRequired="true" autoSubmit="true"
					readOnly="#{!applicationDetail.editMode}"
					label="Does production at this facility contain H2S? :"
					value="#{applicationDetail.application.containH2SFlag}" >
					<f:selectItem itemLabel="Yes" itemValue="Y" />
					<f:selectItem itemLabel="No" itemValue="N" />
				</af:selectOneChoice>

				<af:selectOneChoice id="divisionContacedFlag" unselectedLabel=" "
					showRequired="true"
					readOnly="#{!applicationDetail.editMode}"
					label="Has the Air Quality Division been contacted regarding this application? :"
					rendered="#{!(empty applicationDetail.application.containH2SFlag) && applicationDetail.application.containH2SFlag == 'Y'}"
					value="#{applicationDetail.application.divisionContacedFlag}">
					<f:selectItem itemLabel="Yes" itemValue="Y" />
					<f:selectItem itemLabel="No" itemValue="N" />
				</af:selectOneChoice>
			</af:panelForm>
		</af:panelGroup>
	</af:showDetailHeader>
	<%
		/* Application purpose  end */
	%>


	<%
		/* Federal rules begin */
	%>
	<af:showDetailHeader id="FederalRulesShowDetail" disclosed="true"
		text="State Rule Applicability – Application Subject to:">
		<af:panelGroup id="fedRulesPanel" layout="vertical">
			<af:panelForm id="fedRulesForm" labelWidth="470">
				<t:div id="psdSection">
					<af:selectOneChoice id="PSDChoice"
						label="Prevention of Significant Deterioration (PSD) :"
						valueChangeListener="#{applicationDetail.refreshAttachments}" 
						readOnly="#{! applicationDetail.editMode}"
						unselectedLabel="Please select"
						value="#{applicationDetail.application.psdApplicableFlag}" autoSubmit="true">
						<f:selectItems
							value="#{applicationReference.federalRuleAppl2Defs.items[(empty applicationDetail.application.psdApplicableFlag? '': applicationDetail.application.psdApplicableFlag)]}" />
					</af:selectOneChoice>
					<af:inputText
						label="These rules are found under WAQSR Chapter 6, Section 4."
						readOnly="true" secret="true"
						inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />
				</t:div>
				<t:div id="nsrSection">
					<af:selectOneChoice id="NSRChoice"
						label="Non-Attainment New Source Review :"
						readOnly="#{! applicationDetail.editMode}"
						unselectedLabel="Please select"
						value="#{applicationDetail.application.nsrApplicableFlag}">
						<f:selectItems
							value="#{applicationReference.federalRuleAppl2Defs.items[(empty applicationDetail.application.nsrApplicableFlag? '': applicationDetail.application.nsrApplicableFlag)]}" />
					</af:selectOneChoice>
					<af:inputText label="These rules are found under WAQSR Chapter 6, Section 13."
						readOnly="true" secret="true"
						inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />
				</t:div>
			</af:panelForm>
		</af:panelGroup>
	</af:showDetailHeader>
	<%
		/* Federal rules end */
	%>

	<%
		/* Trade Secret begin */
	%>
	<af:showDetailHeader text="Trade Secret Information" disclosed="true" rendered="#{!applicationDetail.publicApp}">
		<af:panelGroup layout="horizontal">
			<af:selectOneRadio id="tradeSecretInfoRadio"
				value="#{applicationDetail.tradeSecretValue}" disabled="true"
				layout="horizontal" inlineStyle="font-size:75%;color:#666">
				<f:selectItem itemLabel="Yes" itemValue="true" />
				<f:selectItem itemLabel="No" itemValue="false" />
			</af:selectOneRadio>
			<af:outputText
				value=" - One or more Emissions Units in this application contains trade secret information. This is a system generated indicator."
				inlineStyle="font-size:75%;color:#666" />
		</af:panelGroup>
	</af:showDetailHeader>
	<%
		/* Trade Secret end */
	%>

	<%
		/* Contact info begin */
	%>
	<af:showDetailHeader disclosed="true" text="Permit Application Contact" rendered="#{!applicationDetail.publicApp}">
		<f:subview id="contact">
			<jsp:include page="contact.jsp" />
		</f:subview>
	</af:showDetailHeader>
	<%
		/* Contact info end */
	%>

	<%
		/* Modeling begin */
	%>
	<af:showDetailHeader disclosed="true" text="Modeling Section">
		<af:panelGroup
			partialTriggers="preventionPsdFlag preAppMeetingFlag">

			<af:outputText value="Ambient Air Quality Impact Analysis: "
				inlineStyle="font-size:12px;font-family: Arial,Helvetica,Geneva,sans-serif;" />
			<af:outputText
				inlineStyle="font-size:12px;font-family: Arial,Helvetica,Geneva,sans-serif;word-wrap: break-word;width:180px;"
				value="WAQSR Chapter 6, Section 2(c)(ii) requires that permit applicants demonstrate that a proposed  
				facility will not prevent the attainment or maintenance of any ambient air quality standard." />
			<af:objectSpacer height="5" />
			<af:outputText value="If air quality modeling is used for this demonstration, 
					please review the Air Quality Division’s modeling guidance documents; available on the "
				inlineStyle="font-size:12px;font-family: Arial,Helvetica,Geneva,sans-serif;" />
			<af:goLink
				text="External References"
				rendered="#{!applicationDetail.publicApp}"
				targetFrame="_new" destination="../util/externalReferences.jsf" />
			<af:outputText value=" page."
				inlineStyle="font-size:12px;font-family: Arial,Helvetica,Geneva,sans-serif;" />
				
			<af:objectSpacer height="5" />
			<af:panelForm labelWidth="470">
				<af:selectOneChoice unselectedLabel=" " id="modelingContactFlag"
					showRequired="true" readOnly="#{!applicationDetail.editMode}"
					label="Has the applicant contacted Air Quality Division to determine if modeling is required? :"
					value="#{applicationDetail.application.modelingContactFlag}">
					<f:selectItem itemLabel="Yes" itemValue="Y" />
					<f:selectItem itemLabel="No" itemValue="N" />
				</af:selectOneChoice>
				<af:selectOneChoice unselectedLabel=" " id="modelingAnalysisFlag"
					valueChangeListener="#{applicationDetail.refreshAttachments}" 
					showRequired="true" readOnly="#{!applicationDetail.editMode}"
					label="Is a modeling analysis part of this application? :"
					value="#{applicationDetail.application.modelingAnalysisFlag}" autoSubmit="true">
					<f:selectItem itemLabel="Yes" itemValue="Y" />
					<f:selectItem itemLabel="No" itemValue="N" />
				</af:selectOneChoice>
				<af:selectOneChoice id="preventionPsdFlag" unselectedLabel=" "
					autoSubmit="true" showRequired="true" readOnly="#{!applicationDetail.editMode}"
					label="Is the proposed project subject to Prevention of Significant Deterioration (PSD) requirements? :"
					value="#{applicationDetail.application.preventionPsdFlag}">
					<f:selectItem itemLabel="Yes" itemValue="Y" />
					<f:selectItem itemLabel="No" itemValue="N" />
				</af:selectOneChoice>
				<af:selectOneChoice unselectedLabel=" " id="preAppMeetingFlag"
					autoSubmit="true" showRequired="true" readOnly="#{!applicationDetail.editMode}"
					rendered="#{!(empty applicationDetail.application.preventionPsdFlag) && applicationDetail.application.preventionPsdFlag == 'Y'}"
					label="Has the Air Quality Division been notified to schedule a pre-application meeting? :"
					value="#{applicationDetail.application.preAppMeetingFlag}">
					<f:selectItem itemLabel="Yes" itemValue="Y" />
					<f:selectItem itemLabel="No" itemValue="N" />
				</af:selectOneChoice>
				<af:selectOneChoice unselectedLabel=" " id="modelingProtocolSubmitFlag"
					showRequired="true" readOnly="#{!applicationDetail.editMode}"
					rendered="#{!(empty applicationDetail.application.preventionPsdFlag) && applicationDetail.application.preventionPsdFlag == 'Y' 
						&& !(empty applicationDetail.application.preAppMeetingFlag) && applicationDetail.application.preAppMeetingFlag == 'Y'}"
					label="Has a modeling protocol been submitted to and approved by the Air Quality Division? :"
					value="#{applicationDetail.application.modelingProtocolSubmitFlag}">
					<f:selectItem itemLabel="Yes" itemValue="Y" />
					<f:selectItem itemLabel="No" itemValue="N" />
				</af:selectOneChoice>
				<af:selectOneChoice unselectedLabel=" " id="aqrvAnalysisSubmitFlag"
					showRequired="true" readOnly="#{!applicationDetail.editMode}"
					rendered="#{!(empty applicationDetail.application.preventionPsdFlag) && applicationDetail.application.preventionPsdFlag == 'Y' 
						&& !(empty applicationDetail.application.preAppMeetingFlag) && applicationDetail.application.preAppMeetingFlag == 'Y'}"
					label="Has the Air Quality Division received a Q/D analysis to submit to the respective FLMs to determine the need for an AQRV analysis? :"
					value="#{applicationDetail.application.aqrvAnalysisSubmitFlag}">
					<f:selectItem itemLabel="Yes" itemValue="Y" />
					<f:selectItem itemLabel="No" itemValue="N" />
				</af:selectOneChoice>
			</af:panelForm>
		</af:panelGroup>
	</af:showDetailHeader>

	<%
		/* Modling end */
	%>

</af:panelGroup>