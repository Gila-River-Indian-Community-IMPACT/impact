<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup>
	<%
		/**************** Fed Rules *************************************/
	%>
	<af:showDetailHeader disclosed="true"
		text="Emissions Unit - Specific Requirements">
		<af:panelGroup id="fedRulesPanel"
			partialTriggers="NSPSChoice NESHAPChoice MACTChoice"
			layout="vertical">
			<af:panelForm id="fedRulesForm">
				<af:selectOneChoice id="NSPSChoice" autoSubmit="true"
					label="New Source Performance Standards (NSPS) :"
					readOnly="#{! applicationDetail.editMode}"
					unselectedLabel="Please select"
					value="#{applicationDetail.selectedEU.nspsApplicableFlag}">
					<f:selectItems
						value="#{applicationReference.federalRuleAppl1Defs.items[(empty applicationDetail.selectedEU.nspsApplicableFlag? '': applicationDetail.selectedEU.nspsApplicableFlag)]}" />
				</af:selectOneChoice>
				<af:inputText id="dummyNSPSText"
					label="New Source Performance Standards are listed under 40 CFR"
					readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />
				<af:inputText id="dummyNSPSText2"
					label="60 - Standards of Performance for New Stationary Sources."
					readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />
				<%
					/* NSPS Subparts table */
				%>
				<af:table value="#{applicationDetail.tvEuNspsSubparts}"
					bandingInterval="1" banding="row" var="nspsSubpart"
					rendered="#{applicationDetail.displayTvEuNSPSSubparts}">
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
				<af:selectOneChoice id="NESHAPChoice" autoSubmit="true"
					label="National Emission Standards for Hazardous Air Pollutants (NESHAP Part 61) :"
					readOnly="#{! applicationDetail.editMode}"
					unselectedLabel="Please select"
					value="#{applicationDetail.selectedEU.neshapApplicableFlag}">
					<f:selectItems
						value="#{applicationReference.federalRuleAppl1Defs.items[(empty applicationDetail.selectedEU.neshapApplicableFlag? '': applicationDetail.selectedEU.neshapApplicableFlag)]}" />
				</af:selectOneChoice>
				<af:inputText id="dummyNESHAPText1"
					label="National Emissions Standards for Hazardous Air Pollutants (NESHAP Part 61) are listed under 40"
					readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic; text-align: left;" />
				<af:inputText id="dummyNESHAPText2"
					label="CFR 61. (These include asbestos, benzene, beryllium, mercury, and vinyl chloride)."
					readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic; text-align: left;" />
				<%
					/* NESHAP Subparts table */
				%>
				<af:table value="#{applicationDetail.tvEuNeshapSubparts}"
					bandingInterval="1" banding="row" var="neshapSubpart"
					rendered="#{applicationDetail.displayTvEuNESHAPSubparts}">
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
				<af:selectOneChoice id="MACTChoice" autoSubmit="true"
					label="National Emission Standards for Hazardous Air Pollutants (NESHAP Part 63) :"
					readOnly="#{! applicationDetail.editMode}"
					unselectedLabel="Please select"
					value="#{applicationDetail.selectedEU.mactApplicableFlag}">
					<f:selectItems
						value="#{applicationReference.federalRuleAppl1Defs.items[(empty applicationDetail.selectedEU.mactApplicableFlag? '': applicationDetail.selectedEU.mactApplicableFlag)]}" />
				</af:selectOneChoice>
				<af:inputText id="dummyMACTText"
					label="National Emission Standards for Hazardous Air Pollutants (NESHAP Part 63) standards"
					readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />
				<af:inputText id="dummyMACTText2"
					label="are listed under 40 CFR 63." readOnly="true" secret="true"
					inlineStyle="font-size: 13px;font-weight:normal; font-style: italic;" />

				<%
					/* MACT Subparts table */
				%>
				<af:table value="#{applicationDetail.tvEuMactSubparts}"
					bandingInterval="1" banding="row" var="mactSubpart"
					rendered="#{applicationDetail.displayTvEuMACTSubparts}">
					<f:facet name="selection">
						<af:tableSelectMany rendered="#{applicationDetail.editMode}" />
					</f:facet>
					<af:column sortProperty="value" formatType="text"
						headerText="Part 63 NESHAP Subpart">
						<af:selectOneChoice value="#{mactSubpart.value}"
							readOnly="#{! applicationDetail.editMode}">
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
			</af:panelForm>
			<af:panelForm rendered="#{applicationDetail.tvEuFedRulesExemption}"
				partialTriggers="NSPSChoice NESHAPChoice MACTChoice">
				<af:outputLabel for="exemptionExplPanel"
					value="Please explain why you checked &quot;Subject, but exempt&quot; 
          in this question for one or more federal rules. Identify each 
          exemption and whether the entire facility and/or the specific air 
          contaminant sources included in this permit application is exempted. 
          Attach an additional page if necessary." />
				<af:panelGroup id="exemptionExplPanel">
					<af:inputText id="exemptionExplText"
						readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.federalRuleApplicabilityExplanation}"
						columns="120" rows="4" maximumLength="1000" />
				</af:panelGroup>
			</af:panelForm>
		</af:panelGroup>


		<%
			/**************** Pollutant Limits *************************************/
		%>
		<af:outputText value="Pollutant Limits : "
			inlineStyle="font-size: 13px;font-weight:bold" />
		<af:table emptyText=" " var="tvPollutantLimits" bandingInterval="1"
			banding="row" value="#{applicationDetail.selectedEU.pollutantLimits}"
			width="98%" id="tvPollutantLimitsTable"
			varStatus="tvPollutantLimitsTableVs">
			<af:column id="edit" headerText="Row Id" formatType="text">
				<af:commandLink
					action="#{applicationDetail.startViewTVPollutantLimit}"
					useWindow="true" windowWidth="800" windowHeight="600">
					<af:inputText value="#{tvPollutantLimitsTableVs.index+1}"
						readOnly="true">
						<af:convertNumber pattern="000" />
					</af:inputText>
					<t:updateActionListener
						property="#{applicationDetail.tvPollutantLimitsId}"
						value="tvPollutantLimits" />
				</af:commandLink>
			</af:column>
			<af:column id="reqBasisCol" formatType="text"
				headerText="Requirement Basis">
				<af:selectOneChoice id="reqBasis" readOnly="true"
					value="#{tvPollutantLimits.reqBasisCd}">
					<f:selectItems value="#{applicationReference.tvReqBasisDefs}" />
				</af:selectOneChoice>
			</af:column>
			<af:column id="ruleCiteCol"
				headerText="Permit/Waiver/State Rule/Federal Standard Cite" formatType="text">
				<af:inputText id="ruleCite" readOnly="true"
					value="#{tvPollutantLimits.ruleCite}" columns="35"
					maximumLength="50" label="Permit/Waiver/State Rule/Federal Standard Cite" />
			</af:column>
			<af:column id="numLimitUnitCol" headerText="Numeric Limit and Unit"
				formatType="text">
				<af:inputText id="numLimitUnit" readOnly="true"
					value="#{tvPollutantLimits.numLimitUnit}" maximumLength="40" />
			</af:column>
			<af:column id="pollutantCol" headerText="Pollutant">
				<af:selectOneChoice id="pollutant" readOnly="true"
					value="#{tvPollutantLimits.pollutantCd}">
					<f:selectItems
						value="#{applicationDetail.tvPollutantLimitPollutantDefs}" />
				</af:selectOneChoice>
			</af:column>
			<af:column id="avgPeriodCol" headerText="Averaging Period"
				formatType="text">
				<af:inputText id="avgPeriod" readOnly="true" maximumLength="30"
					value="#{tvPollutantLimits.avgPeriod}" />
			</af:column>
			<af:column id="complianceFlagCol" headerText="In Compliance?"
				formatType="text">
				<af:selectOneChoice id="complianceFlag" label="In Compliance?"
					readOnly="true" unselectedLabel=" "
					value="#{tvPollutantLimits.complianceFlag}">
					<af:selectItem label="Yes" value="Y" />
					<af:selectItem label="No" value="N" />
				</af:selectOneChoice>
			</af:column>
			<af:column id="potentialEmissionsCol"
				headerText="Pre-Controlled Potential Emissions (tons/yr)"
				formatType="number">
				<af:inputText id="potentialEmissions" readOnly="true"
					maximumLength="16" value="#{tvPollutantLimits.potentialEmissions}">
					<af:convertNumber pattern="#,###,###,###.###" />
				</af:inputText>
			</af:column>
			<af:column id="determBasisCol" formatType="text"
				headerText="Method for Determination">
				<af:selectOneChoice id="determBasis" readOnly="true"
					value="#{tvPollutantLimits.determinationBasisCd}">
					<f:selectItems
						value="#{applicationDetail.patveuPteDeterminationBasisDefs}" />
				</af:selectOneChoice>
			</af:column>
			<af:column id="camFlagCol" headerText="Subject to CAM*"
				formatType="text">
				<af:selectOneChoice id="camFlag" label="Subject to CAM*"
					readOnly="true" unselectedLabel=" "
					value="#{tvPollutantLimits.camFlag}">
					<af:selectItem label="Yes" value="Y" />
					<af:selectItem label="No" value="N" />
				</af:selectOneChoice>
			</af:column>
			<af:column id="complianceMethodCol" width = "20%"
				headerText="Method to Determine Compliance" formatType="text">
				<af:inputText id="complianceMethod" readOnly="true"
					value="#{tvPollutantLimits.complianceMethod}" />
			</af:column>
			<f:facet name="footer">
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Add Pollutant Limit"
							id="addPollutantLimit" useWindow="true" windowWidth="700"
							windowHeight="300"
							action="#{applicationDetail.startAddTVPollutantLimit}"
							disabled="#{!applicationDetail.euEditAllowed}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</f:facet>
		</af:table>

		<af:outputText
			value="*If the pre-control potential emissions for any pollutant are above the major source threshold for that pollutant, and this application is for the renewal or modification of a Title V permit, then the application must include a CAM (Compliance Assurance Monitoring) Plan for that control device.  The major source threshold for criteria pollutants is 100 tons; for an individual HAP is 10 tons; and for GHGs is 100,000 tons CO2e."
			inlineStyle="font-size:75%;color:#666" />

		<af:objectSpacer width="100%" height="15" />

		<%
			/**************** Operational Restrictions *************************************/
		%>
		<af:outputText value="Operational Restrictions : "
			inlineStyle="font-size: 13px;font-weight:bold" />
		<af:table emptyText=" " var="tvOperationalRestrictions"
			bandingInterval="1" banding="row"
			value="#{applicationDetail.selectedEU.operationalRestrictions}"
			width="98%" id="tvOpRestrictionsTable"
			varStatus="tvOpRestrictionsTableVs">
			<af:column id="opRestEdit" headerText="Row Id" formatType="text">
				<af:commandLink
					action="#{applicationDetail.startViewTVOperationalRestriction}"
					useWindow="true" windowWidth="800" windowHeight="600">
					<af:inputText value="#{tvOpRestrictionsTableVs.index+1}"
						readOnly="true">
						<af:convertNumber pattern="000" />
					</af:inputText>
					<t:updateActionListener
						property="#{applicationDetail.tvOperationalRestrictionId}"
						value="tvOperationalRestrictions" />
				</af:commandLink>
			</af:column>
			<af:column id="opRestReqBasisCol" formatType="text"
				headerText="Requirement Basis">
				<af:selectOneChoice id="opRestReqBasis" readOnly="true"
					value="#{tvOperationalRestrictions.reqBasisCd}">
					<f:selectItems value="#{applicationReference.tvReqBasisDefs}" />
				</af:selectOneChoice>
			</af:column>
			<af:column id="opRestRuleCiteCol"
				headerText="Permit/Waiver/State Rule/Federal Standard Cite" formatType="text">
				<af:inputText id="opRestRuleCite" readOnly="true"
					value="#{tvOperationalRestrictions.ruleCite}" columns="35"
					maximumLength="50" label="Permit/Waiver/State Rule/Federal Standard Cite" />
			</af:column>
			<af:column id="opRestRestrictionTypeCol"
				headerText="Restriction Type" formatType="text">
				<af:selectOneChoice id="opRestRestrictionType" readOnly="true"
					value="#{tvOperationalRestrictions.restrictionTypeCd}">
					<f:selectItems
						value="#{applicationReference.tvRestrictionTypeDefs}" />
				</af:selectOneChoice>
			</af:column>
			<af:column id="opRestRestrictionDescCol"
				headerText="Description of Restriction" formatType="text">
				<af:inputText id="restrictionDesc" readOnly="true"
					maximumLength="150"
					value="#{tvOperationalRestrictions.restrictionDesc}" />
			</af:column>
			<af:column id="opRestComplianceFlagCol" headerText="In Compliance?"
				formatType="text">
				<af:selectOneChoice id="opRestComplianceFlag" label="In Compliance?"
					readOnly="true" unselectedLabel=" "
					value="#{tvOperationalRestrictions.complianceFlag}">
					<af:selectItem label="Yes" value="Y" />
					<af:selectItem label="No" value="N" />
				</af:selectOneChoice>
			</af:column>
			<af:column id="opRestComplianceMethodCol"
				headerText="Method to Determine Compliance" formatType="text">
				<af:inputText id="opRestComplianceMethod" readOnly="true"
					maximumLength="75"
					value="#{tvOperationalRestrictions.complianceMethod}" />
			</af:column>
			<f:facet name="footer">
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Add Operational Restriction"
							id="addOperationalRestriction" useWindow="true" windowWidth="700"
							windowHeight="300"
							action="#{applicationDetail.startAddTVOperationalRestriction}"
							disabled="#{!applicationDetail.euEditAllowed}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</f:facet>
		</af:table>
	</af:showDetailHeader>
</af:panelGroup>
