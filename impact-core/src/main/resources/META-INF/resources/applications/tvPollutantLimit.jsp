<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Title V Pollutant Limit">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true"
			partialTriggers="complianceBox camFlag pollutantChoice">
			<af:messages />
			<af:panelForm width="700px">
				<af:objectSpacer width="100%" height="15" />
				<af:selectOneChoice id="reqBasis"
					readOnly="#{! applicationDetail.pollutantLimitModify}"
					label="Requirement Basis :"
					value="#{applicationDetail.pollutantLimit.reqBasisCd}"
					showRequired="true">
					<f:selectItems value="#{applicationReference.tvReqBasisDefs}" />
				</af:selectOneChoice>
				<af:inputText id="ruleCite" label="Permit/Waiver/State Rule/Federal Standard Cite :"
					maximumLength="40" columns="43"
					readOnly="#{! applicationDetail.pollutantLimitModify}"
					value="#{applicationDetail.pollutantLimit.ruleCite}"
					showRequired="true" />
				<af:inputText id="numLimitUnit" label="Numeric Limit and Unit :"
					maximumLength="40" columns="43"
					readOnly="#{! applicationDetail.pollutantLimitModify}"
					value="#{applicationDetail.pollutantLimit.numLimitUnit}"
					showRequired="true" />
				<af:selectOneChoice id="pollutantChoice" label="Pollutant :"
					unselectedLabel="Please select"
					value="#{applicationDetail.pollutantLimit.pollutantCd}"
					readOnly="#{! applicationDetail.pollutantLimitModify}"
					valueChangeListener="#{applicationDetail.pollutantValueChanged}"
					autoSubmit="true" showRequired="true">
					<f:selectItems
						value="#{applicationDetail.tvPollutantLimitPollutantDefs}" />
				</af:selectOneChoice>
				<af:inputText id="avgPeriod" label="Averaging Period :"
					maximumLength="40" columns="43"
					readOnly="#{! applicationDetail.pollutantLimitModify}"
					value="#{applicationDetail.pollutantLimit.avgPeriod}"
					showRequired="true" />
				<af:selectOneChoice id="complianceBox" label="In compliance? "
					value="#{applicationDetail.pollutantLimit.complianceFlag}"
					readOnly="#{! applicationDetail.pollutantLimitModify}"
					autoSubmit="true" showRequired="true">
					<f:selectItem itemLabel="Yes" itemValue="Y" />
					<f:selectItem itemLabel="No" itemValue="N" />
				</af:selectOneChoice>
				<af:outputText
					rendered="#{!applicationDetail.pollutantLimit.compliant && applicationDetail.pollutantLimit.complianceFlag != null}"
					value="Describe how compliance will be achieved with a milestone schedule in the Compliance Plan, required to be attached at the facility level in this permit application."
					inlineStyle="font-size:75%;color:#666" />

				<af:objectSpacer width="100%" height="20" />

				<af:panelForm width="700px" labelWidth="200px">
					<af:inputText id="potentialEmissions"
						disabled="#{applicationDetail.pollutantStatus != 'controlled'}"
						readOnly="#{! applicationDetail.pollutantLimitModify}"
						label="Pre-Controlled Potential Emissions (tons/yr) : "
						inlineStyle="#{applicationDetail.pollutantStatus != 'controlled' ? 'color: lightgray' : 'color: black'}"
						maximumLength="16" columns="15"
						value="#{applicationDetail.pollutantLimit.potentialEmissions}"
						showRequired="#{applicationDetail.pollutantStatus != 'controlled' ? false : true}">
						<af:convertNumber pattern="#,###,###,###.###" />
					</af:inputText>
					<af:selectOneChoice id="determinationMethod"
						disabled="#{applicationDetail.pollutantStatus != 'controlled'}"
						readOnly="#{! applicationDetail.pollutantLimitModify}"
						label="Method for Determination* :"
						inlineStyle="#{applicationDetail.pollutantStatus != 'controlled' ? 'color: lightgray' : 'color: black'}"
						value="#{applicationDetail.pollutantLimit.determinationBasisCd}"
						showRequired="#{applicationDetail.pollutantStatus != 'controlled' ? false : true}">
						<f:selectItems
							value="#{applicationDetail.patveuPteDeterminationBasisDefs}" />
					</af:selectOneChoice>
					<af:outputText
						value="*Attach a description, including calculations if appropriate."
						inlineStyle="#{applicationDetail.pollutantStatus != 'controlled' 
							? 'color: lightgray; font-size: 12px; font-style: italic;'
							: 'color: black; font-size: 12px; font-style: italic;'}"/>
						<af:objectSpacer width="100%" height="20" />
						<af:selectOneChoice id="camFlag"
							disabled="#{applicationDetail.pollutantStatus != 'controlled'}"
							label="Is this Control Device Subject to Compliance Assurance Monitoring (CAM)? "
							inlineStyle="#{applicationDetail.pollutantStatus != 'controlled' ? 'color: lightgray' : 'color: black'}"
							value="#{applicationDetail.pollutantLimit.camFlag}"
							readOnly="#{! applicationDetail.pollutantLimitModify}"
							autoSubmit="true"
							showRequired="#{applicationDetail.pollutantStatus != 'controlled' ? false : true}">
							<f:selectItem itemLabel="Yes" itemValue="Y" />
							<f:selectItem itemLabel="No" itemValue="N" />
						</af:selectOneChoice>
						<af:inputText id="complianceMethod" rows="4"
							rendered="#{applicationDetail.pollutantStatus != 'controlled'
								||(!applicationDetail.pollutantLimit.camCompliant && applicationDetail.pollutantLimit.camFlag != null)}"
							label="Method to Determine Compliance :" maximumLength="500"
							columns="43"
							readOnly="#{! applicationDetail.pollutantLimitModify}"
							value="#{applicationDetail.pollutantLimit.complianceMethod}"
							tip="Include frequency of monitoring/testing"
							showRequired="true" />
				</af:panelForm>
				
				<af:objectSpacer width="100%" height="20" />

				<f:facet name="footer">
					<af:panelButtonBar>
						<af:commandButton text="Save"
							rendered="#{applicationDetail.pollutantLimitModify}"
							actionListener="#{applicationDetail.applyEditPollutantLimit}" />
						<af:commandButton text="Edit"
							rendered="#{! applicationDetail.pollutantLimitModify}"
							actionListener="#{applicationDetail.editTVPollutantLimit}"
							disabled="#{!applicationDetail.euEditAllowed}" />
						<af:commandButton text="Delete"
							rendered="#{! applicationDetail.newPollutantLimit && ! applicationDetail.pollutantLimitModify}"
							actionListener="#{applicationDetail.removeTVPollutantLimit}"
							disabled="#{!applicationDetail.euEditAllowed}" />
						<af:commandButton text="Cancel" immediate="true"
							rendered="#{applicationDetail.pollutantLimitModify}"
							action="#{applicationDetail.cancelEditTVPollutantLimit}" />
						<af:commandButton text="Close" immediate="true"
							rendered="#{!applicationDetail.pollutantLimitModify}"
							actionListener="#{applicationDetail.closeTVPollutantLimit}" />
					</af:panelButtonBar>
				</f:facet>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>

