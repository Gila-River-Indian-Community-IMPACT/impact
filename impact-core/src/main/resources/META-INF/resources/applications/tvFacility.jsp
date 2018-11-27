<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical">
	<af:panelForm id="preAppForm">
		<af:inputText label="Correction to application number :"
			inlineStyle="color: orange; font-weight: bold;" readOnly="true"
			rendered="#{applicationDetail.application.applicationCorrected && !applicationDetail.application.applicationAmended}"
			value="#{applicationDetail.application.previousApplicationNumber}">
		</af:inputText>
		<af:inputText label="Reason for correction :"
			inlineStyle="color: orange; font-weight: bold;"
			readOnly="#{! applicationDetail.editMode}"
			rendered="#{applicationDetail.application.applicationCorrected && !applicationDetail.application.applicationAmended}"
			value="#{applicationDetail.application.applicationCorrectedReason}">
		</af:inputText>
		<af:selectInputDate id="applicationReceivedDate"
			label="Date application received :"
			readOnly="#{! applicationDetail.editMode}"
			rendered="#{applicationDetail.internalApp}"
			value="#{applicationDetail.application.receivedDate}">
			<af:validateDateTimeRange
				maximum="#{applicationDetail.maxReceivedDate}" />
		</af:selectInputDate>
		<af:inputText label="AQD Title V Application Number :"
			readOnly="#{! applicationDetail.editMode}"
			rendered="#{applicationDetail.internalApp}"
			value="#{applicationDetail.application.aqdTvApplicationNumber}"
			maximumLength="20">
		</af:inputText>
		<af:selectOneRadio id="legacyAppBox"
			label="Is this a legacy Title V Application? :"
			rendered="#{applicationDetail.internalApp}"
			value="#{applicationDetail.application.legacyStateTVApp}"
			readOnly="#{! applicationDetail.editMode}" layout="horizontal">
			<f:selectItem itemLabel="Yes" itemValue="true" />
			<f:selectItem itemLabel="No" itemValue="false" />
		</af:selectOneRadio>
		<af:objectSeparator />
	</af:panelForm>

	<af:showDetailHeader text="Reason for Application" disclosed="true">
		<af:showDetailHeader
			text="Explanation of Revision/Modification/Reopening"
			disclosed="false">
			<af:outputText
				value="Administrative Permit Amendment - Administrative amendments are for minor changes, such as correction of typographical errors, changes to the responsible official(s), address changes, or a change in ownership."
				inlineStyle="font-size:75%;color:#666" />
			<af:objectSpacer height="12" />
			<af:outputText
				value="502(b)(10) Change - A source may change operations without a Title V permit revision provided that the change meets the requirements of WAQSR Ch 6 Sec 3(d)(iii)."
				inlineStyle="font-size:75%;color:#666" />
			<af:objectSpacer height="12" />
			<af:outputText
				value="Minor Permit Modification - Minor permit modifications must meet the requirements of WAQSR Ch 6 Sec 3(d)(vi)."
				inlineStyle="font-size:75%;color:#666" />
			<af:objectSpacer height="12" />
			<af:outputText
				value="Significant Permit Modification – Any permit modification that does not qualify as a minor permit modification or administrative amendment."
				inlineStyle="font-size:75%;color:#666" />
			<af:objectSpacer height="12" />
			<af:outputText
				value="Reopening – Additional requirements (typically NSPS or NESHAP) become applicable to the facility and the current Title V permit has three or more years remaining in its term."
				inlineStyle="font-size:75%;color:#666" />
			<af:objectSpacer height="12" />
			<af:outputText
				value="Rescission Request – Through the removal of equipment or obtaining federally enforceable limits, the facility has become a minor source"
				inlineStyle="font-size:75%;color:#666" />
		</af:showDetailHeader>
		<af:objectSpacer height="10" />
		<af:panelForm partialTriggers="tvApplicationPurposeCd" maxColumns="1">
			<t:div style="margin-left:10px;">
				<af:inputText id="tvApplicationPurposeCdEmpty"
					readOnly="true"
					rendered="#{!applicationDetail.editMode && empty applicationDetail.application.tvApplicationPurposeCd}"
					label="Please Identify the reason for this application:"
					value="Not Selected"
					maximumLength="3000" />
				<af:selectOneRadio id="tvApplicationPurposeCd"
					label="Please Identify the reason for this application:"
					rendered="#{applicationDetail.editMode || not empty applicationDetail.application.tvApplicationPurposeCd}"
					showRequired="true" readOnly="#{!applicationDetail.editMode}"
					value="#{applicationDetail.application.tvApplicationPurposeCd}"
					valueChangeListener="#{applicationDetail.refreshAttachments}"
					autoSubmit="true">
					<f:selectItems value="#{applicationReference.tvAppPurposeDefs}" />
				</af:selectOneRadio>
			</t:div>
			<t:div style="margin-left:350px;"
				rendered="#{applicationDetail.permitReasonSelectable}">
				<af:selectOneRadio id="permitReasonCd"
					readOnly="#{!applicationDetail.editMode}"
					value="#{applicationDetail.application.permitReasonCd}"
					valueChangeListener="#{applicationDetail.refreshAttachments}"
					autoSubmit="true">
					<f:selectItems value="#{applicationReference.permitReasonDefs}" />
				</af:selectOneRadio>
			</t:div>
			<t:div style="margin-left:10px;">
				<af:inputText
					label="Please summarize the reason this permit is being applied for."
					readOnly="true" />
			</t:div>
		</af:panelForm>
		<af:panelForm width="500">
			<af:inputText id="tvPermitReasonText" rows="4"
				readOnly="#{! applicationDetail.editMode}"
				value="#{applicationDetail.application.applicationDesc}"
				columns="80" maximumLength="1000" />
		</af:panelForm>
	</af:showDetailHeader>

	<%
		/* Item 3 */
	%>
	<%
		/* PTE begin */
	%>
	<af:panelGroup partialTriggers="tvApplicationPurposeCd">
		<af:showDetailHeader disclosed="true"
			rendered="#{applicationDetail.pteSelectable}"
			text="Facility-wide Potential to Emit (PTE)">
			<af:outputText
				value="The following table is generated by the system and is the sum of the PTE’s for all emission units in this application. 
				If the facility-wide PTE located in the facility inventory for this facility is different from that represented below, 
				include an attachment describing the circumstances that change the PTE."
				inlineStyle="font-size:75%;color:#666" />
			<af:objectSpacer height="1" />
			<af:outputText value="Criteria Pollutants :"
				inlineStyle="font-size: 13px;font-weight:bold" />
			<af:table id="capTotalsTable" width="98%" emptyText=" "
				var="tvCapPteTotals" bandingInterval="1" banding="row"
				value="#{applicationDetail.application.capPteTotals}">
				<af:column id="capTotalPollutantCol" formatType="text"
					headerText="Pollutant">
					<af:selectOneChoice id="capPtePollutantChoice" readOnly="true"
						value="#{tvCapPteTotals.pollutantCd}">
						<f:selectItems value="#{applicationReference.pollutantDefs}" />
					</af:selectOneChoice>
				</af:column>
				<af:column id="capTotalPTECol" headerText="PTE (tons/year)"
					formatType="number">
					<af:inputText readOnly="true" value="#{tvCapPteTotals.pteEUTotal}"
						columns="10" label="EU Total">
						<mu:convertSigDigNumber
							pattern="#{applicationDetail.capEmissionsValueFormat}" />
					</af:inputText>
				</af:column>
			</af:table>
			<af:objectSpacer height="5" />
			<af:outputText value="Hazardous Air Pollutants (HAPs) :"
				inlineStyle="font-size: 13px;font-weight:bold" />
			<af:table id="hapTotalsTable" width="98%" emptyText=" "
				var="tvHapPteTotals" bandingInterval="1" banding="row"
				value="#{applicationDetail.application.hapPteTotals}">
				<af:column id="hapTotalPollutantCol" formatType="text"
					headerText="Pollutant">
					<af:selectOneChoice id="hapPollutantChoice" readOnly="true"
						value="#{tvHapPteTotals.pollutantCd}"
						inlineStyle="#{tvHapPteTotals.hapStat ? 'font-style: italic' : ''}">
						<f:selectItems value="#{applicationReference.allPollutantDefs}" />
					</af:selectOneChoice>
				</af:column>
				<af:column id="hapAdjPTEText" headerText="PTE (tons/year)"
					formatType="number">
					<af:inputText readOnly="true" label="EU Total"
						value="#{tvHapPteTotals.pteEUTotal}" columns="10"
						shortDesc="Sum of values from each EU included in the application"
						inlineStyle="#{tvHapPteTotals.hapStat ? 'font-style: italic' : ''}">
						<mu:convertSigDigNumber
							pattern="#{applicationDetail.hapEmissionsValueFormat}" />
					</af:inputText>
					<af:inputText readOnly="true" label="PTE (tons/year)"
						value="#{tvHapPteTotals.pteAdjusted}"
						shortDesc="Total sum of values from each EU included in the application"
						inlineStyle="#{tvHapPteTotals.hapStat ? 'font-style: italic' : ''}">
						<mu:convertSigDigNumber
							pattern="#{applicationDetail.hapEmissionsValueFormat}" />
					</af:inputText>
				</af:column>
				<af:column id="hapMajorStatus" headerText="Major Status">
					<af:inputText readOnly="true" value="#{tvHapPteTotals.majorStatus}"
						inlineStyle="#{tvHapPteTotals.hapStat ? 'font-style: italic' : ''}" />
				</af:column>
			</af:table>
			<af:objectSpacer height="2" />
			<af:outputText id="pteFootnoteText"
				inlineStyle="font-size:75%;color:#666"
				value="* Based on the sum of all pollutants provided in this table." />
			<af:objectSpacer height="5" />
			<af:outputText value="Greenhouse Gas Pollutants :"
				inlineStyle="font-size: 13px;font-weight:bold" />
			<af:table id="ghgTotalsTable" width="98%" emptyText=" "
				var="tvGhgPteTotals" bandingInterval="1" banding="row"
				value="#{applicationDetail.application.ghgPteTotals}">
				<af:column id="ghgTotalPollutantCol" formatType="text"
					headerText="Pollutant">
					<af:selectOneChoice id="ghgPollutantChoice" readOnly="true"
						value="#{tvGhgPteTotals.pollutantCd}"
						inlineStyle="#{tvGhgPteTotals.hapStat ? 'font-style: italic' : ''}">
						<f:selectItems value="#{applicationReference.allPollutantDefs}" />
					</af:selectOneChoice>
				</af:column>
				<af:column id="ghgAdjPTEText" headerText="Facility PTE (tons/year)"
					formatType="number">
					<af:inputText readOnly="true" label="EU Total"
						value="#{tvGhgPteTotals.pteEUTotal}"
						shortDesc="Sum of values from each EU included in the application"
						inlineStyle="#{tvGhgPteTotals.hapStat ? 'font-style: italic' : ''}">
						<mu:convertSigDigNumber
							pattern="#{applicationDetail.ghgEmissionsValueFormat}" />
					</af:inputText>
					<af:inputText readOnly="true" label="Facility PTE (tons/year)"
						value="#{tvGhgPteTotals.pteAdjusted}"
						shortDesc="Total Sum of values from each EU included in the application"
						inlineStyle="#{tvGhgPteTotals.hapStat ? 'font-style: italic' : ''}">
						<mu:convertSigDigNumber
							pattern="#{applicationDetail.ghgEmissionsValueFormat}" />
					</af:inputText>
				</af:column>
			</af:table>
			<af:objectSpacer height="5" />
			<af:outputText value="Other Regulated Pollutants :"
				inlineStyle="font-size: 13px;font-weight:bold" />
			<af:table id="OtherRegPollutantsTable" width="98%" emptyText=" "
				var="othEmissions" bandingInterval="1" banding="row"
				value="#{applicationDetail.application.othPteTotals}">
				<af:column id="OthTotalPollutantCol" formatType="text"
					headerText="Pollutant">
					<af:selectOneChoice id="OthPollutantChoice" readOnly="true"
						value="#{othEmissions.pollutantCd}"
						inlineStyle="#{othEmissions.hapStat ? 'font-style: italic' : ''}">
						<f:selectItems value="#{applicationReference.allPollutantDefs}" />
					</af:selectOneChoice>
				</af:column>
				<af:column id="othAdjPTEText" headerText="PTE (tons/year)"
					formatType="number">
					<af:inputText readOnly="true" label="EU Total"
						value="#{othEmissions.pteEUTotal}" columns="10"
						shortDesc="Sum of values from each EU included in the application"
						inlineStyle="#{tvGhgPteTotals.hapStat ? 'font-style: italic' : ''}">
						<mu:convertSigDigNumber
							pattern="#{applicationDetail.ghgEmissionsValueFormat}" />
					</af:inputText>
				</af:column>
			</af:table>

		</af:showDetailHeader>
	</af:panelGroup>
	<%
		/* PTE end */
	%>

	<%
		/* Item 4 */
	%>
	<af:panelGroup partialTriggers="tvApplicationPurposeCd permitReasonCd">
	  <af:showDetailHeader disclosed="true" text="Operations Description" 
		rendered="#{applicationDetail.application.tvApplicationPurposeCd == null || applicationDetail.pteSelectable || applicationDetail.permitReasonSPMSelectable}">
		<af:panelForm labelWidth="75%">
			<af:outputText
				value="Please provide a detailed description of the operations 
          performed at this facility."
				inlineStyle="font-size: 13px;font-weight:bold" />
			<af:inputText id="operationsDscText"
				readOnly="#{! applicationDetail.editMode}" columns="135" rows="10"
				value="#{applicationDetail.application.operationsDsc}"
				maximumLength="3000" />
		</af:panelForm>
	  </af:showDetailHeader>
	  <af:objectSpacer height="2" />
	  <af:panelForm width="100"
		rendered="#{applicationDetail.application.tvApplicationPurposeCd == null || applicationDetail.pteSelectable || applicationDetail.permitReasonSPMSelectable}">
		<af:selectOneChoice id="AlternateOperatingScenariosRBox"
			label="Are there any Alternate Operating Scenarios (AOS) authorized for multiple emission units at your facility? :"
			showRequired="true"
			value="#{applicationDetail.application.alternateOperatingScenarios}"
			readOnly="#{! applicationDetail.editMode}"
			valueChangeListener="#{applicationDetail.refreshAttachments}"
			autoSubmit="true">
			<f:selectItem itemLabel="Yes" itemValue="Y" />
			<f:selectItem itemLabel="No" itemValue="N" />
		</af:selectOneChoice>
		<af:outputText inlineStyle="font-size:75%;color:#666"
			value="* The attachment must include a list of each emissions unit affected by the scenario, the SIC code(s) for processes and products associated with the AOS, as well as the requirements that apply during the AOS." />
	  </af:panelForm>
	</af:panelGroup>

	<%
		/* Items 5-8 */
	%>
	<af:panelGroup partialTriggers="tvApplicationPurposeCd permitReasonCd">
	  <af:showDetailHeader disclosed="true" text="Clean Air Act Provisions"
		rendered="#{applicationDetail.application.tvApplicationPurposeCd == null || applicationDetail.pteSelectable || applicationDetail.permitReasonSPMSelectable}">
		<af:panelForm width="600" labelWidth="425" maxColumns="1"
			partialTriggers="subject112RBox subjectTIVBox plan112RSubmittedBox
			complianceWithApplicableEnhancedMonitoring subjectToEngineConfigRestrictions">
			<af:selectOneChoice id="subject112RBox"
				label="Is this facility subject to 112(r) of the Clean Air Act? :"
				showRequired="true" autoSubmit="true"
				readOnly="#{! applicationDetail.editMode}"
				value="#{applicationDetail.application.subjectTo112R}">
				<f:selectItem itemLabel="Yes" itemValue="Y" />
				<f:selectItem itemLabel="No" itemValue="N" />
			</af:selectOneChoice>
			<af:objectSpacer height="2" />
			<af:selectOneChoice id="plan112RSubmittedBox"
				label="Has a plan been submitted under 112(r)? :" autoSubmit="true"
				showRequired="true"
				rendered="#{!(empty applicationDetail.application.subjectTo112R) 
								&& applicationDetail.application.subjectTo112R == 'Y'}"
				readOnly="#{! applicationDetail.editMode}"
				value="#{applicationDetail.application.planSubmittedUnder112R}">
				<f:selectItem itemLabel="Yes" itemValue="Y" />
				<f:selectItem itemLabel="No" itemValue="N" />
			</af:selectOneChoice>
			<af:objectSpacer height="2" />
			<af:selectInputDate id="riskManagementPlanSubmitDate"
				label="Date the Risk Management Plan was submitted? :"
				showRequired="true"
				rendered="#{!(empty applicationDetail.application.planSubmittedUnder112R)
								&& applicationDetail.application.planSubmittedUnder112R == 'Y'}"
				readOnly="#{!applicationDetail.editMode}"
				value="#{applicationDetail.application.riskManagementPlanSubmitDate}">
				<af:validateDateTimeRange minimum="1900-01-01"
					maximum="#{infraDefs.currentDate}" />
			</af:selectInputDate>
			<af:selectOneChoice id="subjectTIVBox"
				label="Is this facility subject to Title IV of the Clean Air Act? :"
				showRequired="true" readOnly="#{! applicationDetail.editMode}"
				value="#{applicationDetail.application.subjectToTIV}"
				valueChangeListener="#{applicationDetail.refreshAttachments}"
				autoSubmit="true">
				<f:selectItem itemLabel="Yes" itemValue="Y" />
				<f:selectItem itemLabel="No" itemValue="N" />
			</af:selectOneChoice>
			<af:objectSpacer height="2" />
			<af:selectOneChoice id="complianceCertSubmitFrequency"
				label="Frequency of the submission of compliance certifications during the term of the permit? :"
				showRequired="true" readOnly="#{! applicationDetail.editMode}"
				value="#{applicationDetail.application.complianceCertSubmitFrequency}">
				<f:selectItems
					value="#{applicationReference.tvAppComplianceCertIntervalDefs}" />
			</af:selectOneChoice>
			<af:objectSpacer height="2" />
			<af:selectOneChoice id="complianceWithApplicableEnhancedMonitoring"
				label="Are the air contaminant sources identified in this application in compliance with applicable 
					enhanced monitoring (Compliance Assurance Monitoring) and compliance certification requirements 
					of the Act? :"
				showRequired="true" autoSubmit="true"
				readOnly="#{! applicationDetail.editMode}"
				value="#{applicationDetail.application.complianceWithApplicableEnhancedMonitoring}">
				<f:selectItem itemLabel="Yes" itemValue="Y" />
				<f:selectItem itemLabel="No" itemValue="N" />
			</af:selectOneChoice>
			<af:inputText id="complianceRequirementsNotMet" valign="middle"
				label="Describe which requirements are not being met :" columns="65"
				showRequired="true" rows="5" maximumLength="1000" wrap="soft"
				autoSubmit="true"
				rendered="#{!(empty applicationDetail.application.complianceWithApplicableEnhancedMonitoring)
								&& applicationDetail.application.complianceWithApplicableEnhancedMonitoring == 'N'}"
				readOnly="#{! applicationDetail.editMode}"
				value="#{applicationDetail.application.complianceRequirementsNotMet}" />
			<af:objectSpacer height="2" />
			<af:selectOneChoice id="subjectToEngineConfigRestrictions"
				label="Is the facility subject to engine configuration restrictions? :"
				showRequired="true" autoSubmit="true"
				readOnly="#{! applicationDetail.editMode}"
				value="#{applicationDetail.application.subjectToEngineConfigRestrictions}">
				<f:selectItem itemLabel="Yes" itemValue="Y" />
				<f:selectItem itemLabel="No" itemValue="N" />
			</af:selectOneChoice>
			<af:inputText id="nsrPermitNumber" label="NSR Permit Number? :"
				showRequired="true" columns="8" maximumLength="8" autoSubmit="true"
				rendered="#{!(empty applicationDetail.application.subjectToEngineConfigRestrictions)
								&& applicationDetail.application.subjectToEngineConfigRestrictions == 'Y'}"
				readOnly="#{! applicationDetail.editMode}"
				value="#{applicationDetail.application.nsrPermitNumber}">
			</af:inputText>
			<af:objectSpacer height="2" />
			<af:selectOneChoice id="subjectToWAQSR"
				label="Is the facility subject to WAQSR Chapter 14, Section 3 (Actual Emissions of SO2 in any calendar 
					year 2000 or later > 100 tons per year)? :"
				showRequired="true" readOnly="#{! applicationDetail.editMode}"
				value="#{applicationDetail.application.subjectToWAQSR}">
				<f:selectItem itemLabel="Yes" itemValue="Y" />
				<f:selectItem itemLabel="No" itemValue="N" />
			</af:selectOneChoice>
		</af:panelForm>
	  </af:showDetailHeader>
	</af:panelGroup>

	<%
		/* End Items 5-8 */
	%>
    <af:panelGroup partialTriggers="tvApplicationPurposeCd permitReasonCd">
	  <af:showDetailHeader text="Ambient Monitoring" disclosed="true"
		rendered="#{applicationDetail.application.tvApplicationPurposeCd == null || applicationDetail.pteSelectable || applicationDetail.permitReasonSPMSelectable}"
		partialTriggers="ambientMonitoringBox">
		<af:selectOneChoice id="ambientMonitoringBox"
			label="Is the facility required to conduct ambient monitoring? :"
			showRequired="true"
			value="#{applicationDetail.application.ambientMonitoring}"
			readOnly="#{! applicationDetail.editMode}"
			valueChangeListener="#{applicationDetail.refreshAttachments}"
			autoSubmit="true">
			<f:selectItem itemLabel="Yes" itemValue="Y" />
			<f:selectItem itemLabel="No" itemValue="N" />
		</af:selectOneChoice>
		<af:showDetailHeader text="Explanation"
			disclosed="#{applicationDetail.application.ambientMonitoringAllowed}">
			<af:outputText inlineStyle="font-size:75%;color:#666"
				value="If your facility is required to conduct ambient monitoring, attach a summary of the requirements including the following information :" />
			<af:objectSpacer height="1" />
			<af:outputText inlineStyle="font-size:75%;color:#666"
				value=" - The basis for this requirement" />
			<af:objectSpacer height="1" />
			<af:outputText inlineStyle="font-size:75%;color:#666"
				value=" - What pollutants are monitored" />
			<af:objectSpacer height="1" />
			<af:outputText inlineStyle="font-size:75%;color:#666"
				value=" - What meteorological parameters are monitored" />
			<af:objectSpacer height="1" />
			<af:outputText inlineStyle="font-size:75%;color:#666"
				value=" - The date of your most recent Quality Assurance Plan or plan revision" />
		</af:showDetailHeader>
	  </af:showDetailHeader>
    </af:panelGroup>

	<%
		/* Item 9 */
	%>

	<%
		/* Air Contaminant Sources begin */
	%>
	<af:showDetailHeader text="Air Contaminant Sources in this Application"
		disclosed="false">
		<f:subview id="includedEUs">
			<jsp:include page="includedEUs.jsp" />
		</f:subview>
	</af:showDetailHeader>
	<%
		/* Air Contaminant Sources end */
	%>
    <af:panelGroup partialTriggers="tvApplicationPurposeCd permitReasonCd">
	  <af:showDetailHeader disclosed="true" text="Facility-Wide Requirements" rendered="#{applicationDetail.application.tvApplicationPurposeCd == null || applicationDetail.pteSelectable || applicationDetail.permitReasonReopeningSelectable || applicationDetail.permitReasonSPMSelectable}"
		partialTriggers="nspsApplicableFlag neshapApplicableFlag mactApplicableFlag facilityWideRequirementFlag">
		<af:panelForm>
			<t:div id="divNSPS">
				<af:selectOneChoice id="nspsApplicableFlag" autoSubmit="true"
					label="New Source Performance Standards (NSPS) :"
					showRequired="true" readOnly="#{!applicationDetail.editMode}"
					unselectedLabel="Please select"
					value="#{applicationDetail.application.nspsApplicableFlag}">
					<f:selectItems
						value="#{applicationReference.federalRuleAppl1Defs.items[(empty applicationDetail.application.nspsApplicableFlag? '': applicationDetail.application.nspsApplicableFlag)]}" />
				</af:selectOneChoice>
				<af:inputText
					label="New Source Performance Standards are listed under 40 CFR"
					readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />
				<af:inputText
					label="60 - Standards of Performance for New Stationary Sources."
					readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />
				<af:table value="#{applicationDetail.tvNspsSubparts}"
					bandingInterval="1" banding="row" var="nspsSubpart"
					rendered="#{applicationDetail.displayTvNSPSSubparts}">
					<f:facet name="selection">
						<af:tableSelectMany rendered="#{applicationDetail.editMode}" />
					</f:facet>
					<af:column sortProperty="value" sortable="true" formatType="text"
						headerText="NSPS Subpart">
						<af:selectOneChoice value="#{nspsSubpart.value}"
							readOnly="#{! applicationDetail.editMode}">
							<f:selectItems value="#{applicationReference.nspsSubpartDefs}" />
						</af:selectOneChoice>
					</af:column>
					<f:facet name="footer">
						<afh:rowLayout halign="left">
							<af:panelButtonBar>
								<af:commandButton text="Add Subpart" id="addNSPS"
									rendered="#{applicationDetail.editMode}"
									actionListener="#{applicationDetail.initActionTable}"
									action="#{applicationDetail.addActionTableRow}">
									<t:updateActionListener
										property="#{applicationDetail.actionTableNewObject}"
										value="#{applicationDetail.newStars2Object}" />
								</af:commandButton>
								<af:commandButton text="Delete Selected Subparts"
									rendered="#{applicationDetail.editMode}"
									actionListener="#{applicationDetail.initActionTable}"
									action="#{applicationDetail.deleteActionTableRows}">
								</af:commandButton>
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
				</af:table>
			</t:div>
			<t:div id="divNESHAP">
				<af:selectOneChoice id="neshapApplicableFlag" autoSubmit="true"
					label="National Emission Standards for Hazardous Air Pollutants (NESHAP Part 61) :"
					showRequired="true" readOnly="#{!applicationDetail.editMode}"
					unselectedLabel="Please select"
					value="#{applicationDetail.application.neshapApplicableFlag}">
					<f:selectItems
						value="#{applicationReference.federalRuleAppl1Defs.items[(empty applicationDetail.application.neshapApplicableFlag? '': applicationDetail.application.neshapApplicableFlag)]}" />
				</af:selectOneChoice>
				<af:inputText
					label="National Emissions Standards for Hazardous Air Pollutants (NESHAP Part 61) are listed under 40"
					readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic; text-align: left;" />
				<af:inputText
					label="CFR 61. (These include asbestos, benzene, beryllium, mercury, and vinyl chloride)."
					readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic; text-align: left;" />
				<af:table value="#{applicationDetail.tvNeshapSubparts}"
					bandingInterval="1" banding="row" var="neshapSubpart"
					rendered="#{applicationDetail.displayTvNESHAPSubparts}">
					<f:facet name="selection">
						<af:tableSelectMany rendered="#{applicationDetail.editMode}" />
					</f:facet>
					<af:column sortProperty="value" sortable="true" formatType="text"
						headerText="Part 61 NESHAP Subpart">
						<af:selectOneChoice value="#{neshapSubpart.value}"
							readOnly="#{! applicationDetail.editMode}">
							<f:selectItems value="#{applicationReference.neshapSubpartDefs}" />
						</af:selectOneChoice>
					</af:column>
					<f:facet name="footer">
						<afh:rowLayout halign="left">
							<af:panelButtonBar>
								<af:commandButton text="Add Subpart" id="addNESHAP"
									rendered="#{applicationDetail.editMode}"
									actionListener="#{applicationDetail.initActionTable}"
									action="#{applicationDetail.addActionTableRow}">
									<t:updateActionListener
										property="#{applicationDetail.actionTableNewObject}"
										value="#{applicationDetail.newStars2Object}" />
								</af:commandButton>
								<af:commandButton text="Delete Selected Subparts"
									rendered="#{applicationDetail.editMode}"
									actionListener="#{applicationDetail.initActionTable}"
									action="#{applicationDetail.deleteActionTableRows}">
								</af:commandButton>
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
				</af:table>
			</t:div>
			<t:div id="divMACT">
				<af:selectOneChoice id="mactApplicableFlag" autoSubmit="true"
					label="National Emission Standards for Hazardous Air Pollutants (NESHAP Part 63) :"
					showRequired="true" readOnly="#{! applicationDetail.editMode}"
					unselectedLabel="Please select"
					value="#{applicationDetail.application.mactApplicableFlag}">
					<f:selectItems
						value="#{applicationReference.federalRuleAppl1Defs.items[(empty applicationDetail.application.mactApplicableFlag? '': applicationDetail.application.mactApplicableFlag)]}" />
				</af:selectOneChoice>
				<af:inputText
					label="National Emission Standards for Hazardous Air Pollutants (NESHAP Part 63) standards"
					readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />
				<af:inputText label="are listed under 40 CFR 63." readOnly="true"
					secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />
				<af:table value="#{applicationDetail.tvMactSubparts}"
					bandingInterval="1" banding="row" var="mactSubpart"
					rendered="#{applicationDetail.displayTvMACTSubparts}">
					<f:facet name="selection">
						<af:tableSelectMany rendered="#{applicationDetail.editMode}" />
					</f:facet>
					<af:column sortProperty="value" formatType="text"
						headerText="Part 63 NESHAP Subpart">
						<af:selectOneChoice value="#{mactSubpart.value}"
							readOnly="#{!applicationDetail.editMode}">
							<f:selectItems value="#{applicationReference.mactSubpartDefs}" />
						</af:selectOneChoice>
					</af:column>
					<f:facet name="footer">
						<afh:rowLayout halign="left">
							<af:panelButtonBar>
								<af:commandButton text="Add Subpart" id="addMACT"
									rendered="#{applicationDetail.editMode}"
									actionListener="#{applicationDetail.initActionTable}"
									action="#{applicationDetail.addActionTableRow}">
									<t:updateActionListener
										property="#{applicationDetail.actionTableNewObject}"
										value="#{applicationDetail.newStars2Object}" />
								</af:commandButton>
								<af:commandButton text="Delete Selected Subparts"
									rendered="#{applicationDetail.editMode}"
									actionListener="#{applicationDetail.initActionTable}"
									action="#{applicationDetail.deleteActionTableRows}">
								</af:commandButton>
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
				</af:table>
			</t:div>
			<t:div id="divPSD">
				<af:selectOneChoice id="psdApplicableFlag"
					label="Prevention of Significant Deterioration (PSD) :"
					showRequired="true" readOnly="#{!applicationDetail.editMode}"
					unselectedLabel="Please select"
					rendered="#{applicationDetail.application.tvApplicationPurposeCd == null || applicationDetail.pteSelectable || applicationDetail.permitReasonSPMSelectable}"
					value="#{applicationDetail.application.psdApplicableFlag}">
					<f:selectItems
						value="#{applicationReference.tvFedRuleAppDefs.items[(empty applicationDetail.application.psdApplicableFlag? '': applicationDetail.application.psdApplicableFlag)]}" />
				</af:selectOneChoice>
				<af:inputText
					rendered="#{applicationDetail.application.tvApplicationPurposeCd == null || applicationDetail.pteSelectable || applicationDetail.permitReasonSPMSelectable}"
					label="These rules are found under WAQSR Chapter 6, Section 4."
					readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />
			</t:div>
			<t:div id="divNSR">
				<af:selectOneChoice id="nsrApplicableFlag"
					label="Non-Attainment New Source Review :" showRequired="true"
					readOnly="#{!applicationDetail.editMode}"
					unselectedLabel="Please select"
					rendered="#{applicationDetail.application.tvApplicationPurposeCd == null || applicationDetail.pteSelectable || applicationDetail.permitReasonSPMSelectable}"
					value="#{applicationDetail.application.nsrApplicableFlag}">
					<f:selectItems
						value="#{applicationReference.tvFedRuleAppDefs.items[(empty applicationDetail.application.nsrApplicableFlag? '': applicationDetail.application.nsrApplicableFlag)]}" />
				</af:selectOneChoice>
				<af:inputText
					rendered="#{applicationDetail.application.tvApplicationPurposeCd == null || applicationDetail.pteSelectable || applicationDetail.permitReasonSPMSelectable}"
					label="These rules are found under WAQSR Chapter 6, Section 13."
					readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />
			</t:div>
			<t:div id="divOtherFacWideReqs">
				<af:selectOneChoice id="facilityWideRequirementFlag"
					label="Other Facility-Wide Requirements :" autoSubmit="true"
					showRequired="true" readOnly="#{!applicationDetail.editMode}"
					unselectedLabel="Please select"
					value="#{applicationDetail.application.facilityWideRequirementFlag}">
					<f:selectItems
						value="#{applicationReference.tvFacWideReqOptionDefs.items[(empty applicationDetail.application.facilityWideRequirementFlag? '': applicationDetail.application.facilityWideRequirementFlag)]}" />
				</af:selectOneChoice>
				<af:table value="#{applicationDetail.facWideReqWrapper}"
					bandingInterval="1" banding="row" var="facWideReq"
					varStatus="facWideReqVS"
					rendered="#{applicationDetail.application.facilityWideRequirementFlag == '1'}"
					binding="#{applicationDetail.facWideReqWrapper.table}" width="98%">
					<af:column sortProperty="requirementId" sortable="true"
						formatType="text" headerText="ID" width="20">
						<af:commandLink
							action="#{applicationDetail.startToEditFacWideReq}"
							rendered="#{!(empty facWideReq.requirementId) && !(facilityProfile.publicApp)}"
							shortDesc="Click to Edit and Delete the Facility-Wide Requirement item"
							useWindow="true" windowWidth="650" windowHeight="275">
							<af:inputText value="#{facWideReqVS.index+1}" readOnly="true">
								<af:convertNumber pattern="000" />
							</af:inputText>
						</af:commandLink>
						<af:outputText value="#{facWideReqVS.index+1}" rendered="#{facilityProfile.publicApp}">
							<af:convertNumber pattern="000" />
						</af:outputText>
					</af:column>
					<af:column sortProperty="description" sortable="true"
						formatType="text" headerText="Facility-Wide Requirement">
						<af:inputText value="#{facWideReq.description}" columns="40"
							rows="1" readOnly="true" />
					</af:column>
					<af:column sortProperty="proposedMethod" sortable="true"
						formatType="text"
						headerText="Proposed Method to Demonstrate Compliance">
						<af:inputText value="#{facWideReq.proposedMethod}" columns="40"
							rows="1" readOnly="true" />
					</af:column>
					<f:facet name="footer">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>
								<af:commandButton text="Add Requirement" useWindow="true"
									windowWidth="700" windowHeight="275"
									disabled="#{!applicationDetail.editAllowed}"
									rendered="#{!facilityProfile.publicApp}"
									action="#{applicationDetail.startToAddFacWideReq}">
								</af:commandButton>
								<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />
								<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
				</af:table>
				<af:inputText readOnly="true"
					label="Are there any proposed exemptions from otherwise applicable requirements for the facility? :" />
				<af:inputText readOnly="#{!applicationDetail.editMode}"
					value="#{applicationDetail.application.proposedExemptions}"
					maximumLength="500" columns="80" rows="4" />
			</t:div>
		</af:panelForm>
	  </af:showDetailHeader>
	</af:panelGroup>

	<%
		/* Start of Insignificant Activities*/
	%>
	<af:panelGroup partialTriggers="tvApplicationPurposeCd">
	  <af:showDetailHeader rendered="#{applicationDetail.application.tvApplicationPurposeCd == null || applicationDetail.pteSelectable}" disclosed="false" text="Insignificant Activities">
		<af:outputFormatted styleUsage="instruction"
			value="<p>Attach a list of activities incidental to the primary business of the facility and which result in emissions of less than one ton per year of a regulated pollutant or emissions less than 1000 pounds per year of a hazardous air pollutant.  
			By listing these sources, the applicant is certifying emissions are less than the above quantities, 
			and that the activity has no applicable requirements.  Flares, incinerators, and fuel burning equipment (no matter how small) have applicable requirements and may not be listed here.  
			Include in the list for each activity:</p>
			<ul><li>Activity Description</li><li>Pollutant(s)</li><li>Estimated Emissions (by pollutant)</li></ul>
			<p>WAQSR Chapter 6, Section 3(c)(ii)(A)(III)(1.) indicates a source is not required to provide detailed information on insignificant activities which are incidental to the primary business activity.  
			The insignificant activities must result in less than one ton of emissions of a regulated pollutant per year, or less than 1000 pounds of emissions from a hazardous air pollutant per year, and have no other applicable regulatory requirements.  The emissions level of 1 ton per year for each regulated pollutant or 1000 pounds per year for each hazardous pollutant should be applied to the same activity collectively.  
			For example, emissions from all maintenance-type painting operations at the facility should be totaled and listed collectively.  List these activities, their respective pollutant(s), and an emission estimate for each pollutant.  
			By listing the sources in this attachment, you are certifying that all activities and emission levels meet the aforementioned criteria.</p>" />
	  </af:showDetailHeader>
	</af:panelGroup>
	<%
		/* End of Insignificant Activities */
	%>
</af:panelGroup>