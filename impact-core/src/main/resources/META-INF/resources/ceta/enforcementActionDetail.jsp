<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document onmousemove="#{infradefs.resize}" onload="#{infradefs.iframeReload}"
		 	title="Enforcement Action Detail">
    	<f:verbatim>
    		<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    	</f:verbatim>
    	<mu:setProperty value="enf" property="#{correspondenceSearch.fromLinkedTo}" />
	   	<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}"
				title="Enforcement Action Detail" id="eadetail">
				<af:inputHidden value="#{enforcementSearch.popupRedirect}"/>
				<%@include file="/util/branding.jsp"%>
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						type="#{foo.type}" disabled="#{foo.disabled}"
						rendered="#{foo.rendered}" icon="#{foo.icon}"
						onclick="if (#{enforcementActionDetail.editable}) { alert('Please save or discard your changes'); return false; }" />
				</f:facet>	
				<afh:rowLayout halign="center" width="1100">
					<h:panelGrid border="1">	
			      		<af:panelForm partialTriggers="sepFlag" id="temptest">
			      			<af:panelBox background="light" width="1100">
								<af:panelForm rows="1" maxColumns="4">
									<af:inputText id="facilityId" label="Facility ID:" readOnly="true"
										value="#{enforcementActionDetail.enforcementAction.facilityId}" />
									<af:inputText id="enfId" label="Enforcement Action ID:" readOnly="true"
										value="#{enforcementActionDetail.enforcementAction.enfId}" />
									<af:inputText id="facilityNm" label="Facility Name:" readOnly="true"
										value="#{enforcementActionDetail.enforcementAction.facilityNm}" />
									<af:inputText id="companyName" label="Company Name:" readOnly="true"
										value="#{enforcementActionDetail.enforcementAction.companyName}"/>		
									<af:selectOneChoice id="creator" label="Creator:" readOnly="true"
									value="#{enforcementActionDetail.enforcementAction.creatorId}">
									<f:selectItems
										value="#{infraDefs.basicUsersDef.items[(empty enforcementActionDetail.enforcementAction.creatorId ? 0 : enforcementActionDetail.enforcementAction.creatorId)]}"/>
									</af:selectOneChoice>		
									<af:selectInputDate id="createdDate" label="Created Date:" readOnly="true" 
										value="#{enforcementActionDetail.enforcementAction.createdDate}"/>		
								</af:panelForm>
							</af:panelBox>

							<afh:rowLayout halign="center">
							<af:panelForm rows="1" maxColumns="1">
								<af:selectOneRadio id="legacyFlag"
									label="Is this a legacy Enforcement Action? :"
									rendered="#{enforcementActionDetail.internalApp}"
									value="#{enforcementActionDetail.enforcementAction.legacyFlag}"
									readOnly="#{!enforcementActionDetail.editable}" layout="horizontal">
									<f:selectItem itemLabel="Yes" itemValue="true" />
									<f:selectItem itemLabel="No" itemValue="false" />
								</af:selectOneRadio>
							</af:panelForm>
							</afh:rowLayout>
							
							<af:objectSeparator />

							<afh:rowLayout halign="left">
							<af:panelForm rows="1" maxColumns="3" width="600px" labelWidth="200px">
								<af:selectInputDate id="potentialViolationStartDate"
									label="Potential Violation Start Date:"
									readOnly="#{!enforcementActionDetail.editable}"
									value="#{enforcementActionDetail.enforcementAction.potentialViolationStartDate}">
									<af:validateDateTimeRange minimum="1900-01-01"
										maximum="#{infraDefs.currentDate}" />
								</af:selectInputDate>
								<af:selectInputDate id="potentialViolationEndDate"
									label="Potential Violation End Date:"
									readOnly="#{!enforcementActionDetail.editable}"
									value="#{enforcementActionDetail.enforcementAction.potentialViolationEndDate}">
									<af:validateDateTimeRange minimum="1900-01-01"
										maximum="#{infraDefs.currentDate}" />
								</af:selectInputDate>
								<af:inputText id="docketNumber" label="Docket Number:"
								readOnly="#{!enforcementActionDetail.editable}"
								maximumLength="7" columns="10" tip="XXXX-XX" 
								value="#{enforcementActionDetail.enforcementAction.docketNumber}"/>
							</af:panelForm>
							</afh:rowLayout>
						
							<afh:rowLayout halign="left">
							<af:panelForm rows="1" maxColumns="3" width="600px" labelWidth="200px" 
									partialTriggers="enforcementActionType enforcementActionHPVCriterion enforcementActionFRVType">
								<af:selectOneChoice id="enforcementActionType" label="Enforcement Action Type:" unselectedLabel=""
									readOnly="#{!enforcementActionDetail.editable}"
									autoSubmit="true"
									value="#{enforcementActionDetail.enforcementAction.enforcementActionType}">
									<f:selectItems
										value="#{enforcementActionDetail.enforcementActionTypeDef.items[(empty enforcementActionDetail.enforcementAction.enforcementActionType ? 0 : enforcementActionDetail.enforcementAction.enforcementActionType)]}" />
								</af:selectOneChoice>
								<af:selectOneChoice id="enforcementActionHPVCriterion" label="Enforcement Action HPV Criterion:" unselectedLabel=""
									readOnly="#{!enforcementActionDetail.editable}"
									autoSubmit="true"
									rendered="#{enforcementActionDetail.showCriterion}"
									showRequired="#{enforcementActionDetail.showCriterion}"
									value="#{enforcementActionDetail.enforcementAction.enforcementActionHPVCriterion}">
									<f:selectItems
										value="#{enforcementActionDetail.enforcementActionHPVCriterionDef.items[(empty enforcementActionDetail.enforcementAction.enforcementActionHPVCriterion ? 0 : enforcementActionDetail.enforcementAction.enforcementActionHPVCriterion)]}" />
								</af:selectOneChoice>
								<af:selectOneChoice id="enforcementActionFRVType" label="Enforcement Action FRV Type:" unselectedLabel=""
									readOnly="#{!enforcementActionDetail.editable}"
									autoSubmit="true"
									rendered="#{enforcementActionDetail.showFRVType}"
									value="#{enforcementActionDetail.enforcementAction.enforcementActionFRVType}">
									<f:selectItems
										value="#{enforcementActionDetail.enforcementActionFRVTypeDef.items[(empty enforcementActionDetail.enforcementAction.enforcementActionFRVType ? 0 : enforcementActionDetail.enforcementAction.enforcementActionFRVType)]}" />
								</af:selectOneChoice>
							</af:panelForm>	
							</afh:rowLayout>
						

							<afh:rowLayout halign="left">
							<af:panelForm width="600px" labelWidth="200px"
								partialTriggers="referralType">		
								<af:selectManyListbox id="referralType" label="Referral Type:"
									readOnly="#{!enforcementActionDetail.editable}"
									autoSubmit="true"
									value="#{enforcementActionDetail.enforcementAction.referralTypes}">
									<f:selectItems
										value="#{enforcementActionDetail.enforcementActionReferralTypeDef.items[(empty enforcementActionDetail.enforcementAction.referralTypes ? 0 : enforcementActionDetail.enforcementAction.referralTypes)]}"/>
								</af:selectManyListbox>	
								<af:inputText	id="otherDescription" label="Description of Other:"
									readOnly="#{!enforcementActionDetail.editable}"
									rendered="#{enforcementActionDetail.enforcementAction.referralTypeOther}"
									columns="100" rows="5" maximumLength="1000"
									showRequired="#{enforcementActionDetail.enforcementAction.referralTypeOther}"
									value="#{enforcementActionDetail.enforcementAction.otherDescription}"/>
							</af:panelForm>
							</afh:rowLayout>
						
							<afh:rowLayout halign="left">
								<af:panelForm rows="2" maxColumns="1" width="600px" labelWidth="200px" partialTriggers="otherSARequirements">
									<af:selectOneChoice id="otherSARequirements" label="Other Requirements in SA?:" unselectedLabel=" "
										readOnly="#{!enforcementActionDetail.editable}"
										autoSubmit="true"
										value="#{enforcementActionDetail.enforcementAction.otherSARequirements}">
										<f:selectItem itemLabel="Yes" itemValue="Y" />
										<f:selectItem itemLabel="No" itemValue="N" />
									</af:selectOneChoice>	
								
									<af:selectOneChoice id="otherSARequirementsMet" label="Other Requirements in SA Met?:" unselectedLabel=" "
										readOnly="#{!enforcementActionDetail.editable}"
										rendered="#{enforcementActionDetail.enforcementAction.otherSARequirements=='Y'}"
										value="#{enforcementActionDetail.enforcementAction.otherSARequirementsMet}">
										<f:selectItem itemLabel="Yes" itemValue="Y" />
										<f:selectItem itemLabel="No" itemValue="N" />
									</af:selectOneChoice>	
								</af:panelForm>
							</afh:rowLayout>
							<afh:rowLayout halign="left">
							<af:panelForm width="600px" labelWidth="200px">	
								<af:inputText	id="potentialViolationDescription" label="Description of Potential Violations and Relevant Citations:"
									readOnly="#{!enforcementActionDetail.editable}"
									columns="100" rows="5" maximumLength="1000"
									showRequired="true" 
									value="#{enforcementActionDetail.enforcementAction.potentialViolationDescription}"/>	
							</af:panelForm>
							</afh:rowLayout>
						
						
							<afh:rowLayout halign="left">
							<af:panelForm rows="1" maxColumns="2" width="600px" labelWidth="200px"
								partialTriggers="environmentalImpact">
								<af:selectOneChoice id="environmentalImpact" label="Environmental Impact:" unselectedLabel=" "
									readOnly="#{!enforcementActionDetail.editable}"
									autoSubmit="true"
									value="#{enforcementActionDetail.enforcementAction.environmentalImpact}">
									<f:selectItem itemLabel="Yes" itemValue="Y" />
									<f:selectItem itemLabel="No" itemValue="N" />
								</af:selectOneChoice>		
								<af:inputText id="environmentalImpactDescription" label="Environmental Impact Description:"
									readOnly="#{!enforcementActionDetail.editable}"
									rendered="#{enforcementActionDetail.enforcementAction.environmentalImpact=='Y'}"
									columns="75" rows="5" maximumLength="1000"
									value="#{enforcementActionDetail.enforcementAction.environmentalImpactDescription}"/>
							</af:panelForm>
							</afh:rowLayout>
						
							<afh:rowLayout halign="left">
							<af:panelForm rows="1" maxColumns="2" width="600px" labelWidth="200px"
								partialTriggers="evidenceAttached">
								<af:selectOneChoice id="evidenceAttached" label="Evidence Attached?:" unselectedLabel=" "
									readOnly="#{!enforcementActionDetail.editable}"
									autoSubmit="true"
									value="#{enforcementActionDetail.enforcementAction.evidenceAttached}">
									<f:selectItem itemLabel="Yes" itemValue="Y" />
									<f:selectItem itemLabel="No" itemValue="N" />
								</af:selectOneChoice>
								<af:inputText id="evidence" label="Evidence:"
									readOnly="#{!enforcementActionDetail.editable}"
									rendered="#{enforcementActionDetail.enforcementAction.evidenceAttached=='Y'}"
									columns="75" rows="5" maximumLength="1000"
									value="#{enforcementActionDetail.enforcementAction.evidence}"/>	
							</af:panelForm>
							</afh:rowLayout>

							<af:objectSpacer height="10" />

							<afh:rowLayout halign="left">
								<af:panelForm rows="1" maxColumns="1" width="600px"
									labelWidth="200px">
									<af:selectOneChoice id="sepFlag"
										label="Supplemental Environmental Project (SEP)?:" unselectedLabel=" "
										readOnly="#{!enforcementActionDetail.editable}"
										autoSubmit="true"
										value="#{enforcementActionDetail.enforcementAction.sepFlag}">
										<f:selectItem itemLabel="Yes" itemValue="Y" />
										<f:selectItem itemLabel="No" itemValue="N" />
									</af:selectOneChoice>
								</af:panelForm>
							</afh:rowLayout>

							<afh:rowLayout halign="left">
								<af:panelForm rows="1" maxColumns="1" width="600px"
									labelWidth="200px" rendered="#{enforcementActionDetail.enforcementAction.sepFlag=='Y'}">
									<af:inputText label="SEP Offset Amount:"
										readOnly="#{!enforcementActionDetail.editable}"
										value="#{enforcementActionDetail.enforcementAction.sepOffsetAmount}"
										rendered="#{!enforcementActionDetail.editable}">
										<af:convertNumber type='currency' locale="en-US"
											minFractionDigits="2" />
									</af:inputText>

									<af:inputText label="SEP Offset Amount:" maximumLength="10" id="sepOffsetAmt"
										value="#{enforcementActionDetail.enforcementAction.sepOffsetAmount}"
										rendered="#{enforcementActionDetail.editable}" columns="10"
										showRequired="#{enforcementActionDetail.enforcementAction.sepFlag=='Y'}">
										<af:convertNumber type='number' locale="en-US"
											maxFractionDigits="6" />
									</af:inputText>
								</af:panelForm>
							</afh:rowLayout>

							<afh:rowLayout halign="left">
								<af:panelForm rows="1" maxColumns="1" width="600px"
									labelWidth="200px">
									<af:inputText label="Penalty:"
										readOnly="#{!enforcementActionDetail.editable}"
										value="#{enforcementActionDetail.enforcementAction.penaltyAmount}"
										rendered="#{!enforcementActionDetail.editable}">
										<af:convertNumber type='currency' locale="en-US"
											minFractionDigits="2" />
									</af:inputText>

									<af:inputText label="Penalty:" maximumLength="10" id="penalty"
										value="#{enforcementActionDetail.enforcementAction.penaltyAmount}"
										rendered="#{enforcementActionDetail.editable}" columns="10">
										<af:convertNumber type='number' locale="en-US"
											maxFractionDigits="6" />
									</af:inputText>
								</af:panelForm>
							</afh:rowLayout>

							<af:objectSpacer height="10"/>

							<f:subview id="tracking_event_dates">
								<jsp:include flush="true" page="enforcementActionEvents.jsp"/>
							</f:subview>
	
							<af:showDetailHeader text="Additional Notes" disclosed="true" id="addNotes">
								<af:panelForm rows="1" maxColumns="1" width="98%">
								<af:inputText id="memo"
									readOnly="#{!enforcementActionDetail.editable}"
									rows="7" columns="150" maximumLength="1000"
									value="#{enforcementActionDetail.enforcementAction.memo}"/>
								</af:panelForm>
							</af:showDetailHeader>

							<af:objectSpacer height="5"/>
						
							<af:showDetailHeader text="Correspondences" disclosed="true"
									rendered="#{enforcementActionDetail.enforcementAction.enforcementActionId != null}"
									id="linkedCorr">
									<jsp:include flush="true" page="../correspondence/enfCorrespondenceTable.jsp" />
							</af:showDetailHeader>

							<f:subview id="doc_attachments"
								rendered="#{enforcementActionDetail.enforcementAction.enforcementActionId != null}">
								<jsp:include flush="true" page="enfAttachments.jsp"/>
							</f:subview>
							<af:showDetailHeader text="Notes" disclosed="true"
								rendered="#{enforcementActionDetail.enforcementAction.enforcementActionId != null && enforcementActionDetail.internalApp}"
								id="enfNotes">
								<jsp:include flush="true" page="enforcementNotesTable.jsp"/>
							</af:showDetailHeader>


							<f:facet name="footer">
								<af:panelButtonBar>
									<af:commandButton text="Edit" id="editEnforcement"
										rendered="#{!enforcementActionDetail.editable}"
										action="#{enforcementActionDetail.startEditAction}"
										disabled="#{!enforcementActionDetail.enforcementActionEditAllowed}" />
									<af:commandButton text="Create Correspondence" id="createCorrespondence"
										rendered="#{!enforcementActionDetail.editable}"
										action="#{createCorrespondence.createCorrespondenceFromEA}"
										disabled="#{!enforcementActionDetail.enforcementActionEditAllowed}">
										<t:updateActionListener
											value="#{enforcementActionDetail.enforcementAction.facilityId}"
											property="#{createCorrespondence.facilityId}" />
										<t:updateActionListener
											value="#{enforcementActionDetail.enforcementAction.enforcementActionId}"
											property="#{createCorrespondence.linkedToId}" />
									</af:commandButton>
									<af:commandButton text="Save" id="saveEnforcement"
										rendered="#{enforcementActionDetail.editable}"
										actionListener="#{enforcementActionDetail.saveEnforcementAction}" />
									<af:commandButton text="Cancel" immediate="true" id="cancelEnforcement"
										rendered="#{enforcementActionDetail.editable}"
										action="#{enforcementActionDetail.cancelEditAction}" />
									<af:commandButton text="Delete"
										disabled="#{!enforcementActionDetail.enforcementActionEditAllowed || enforcementActionDetail.hasCorrespondences }"
										action="#{enforcementActionDetail.requestDelete}"
										useWindow="true" windowWidth="500" windowHeight="300"
										shortDesc="#{!enforcementActionDetail.hasCorrespondences ? 'Delete' : 
	                	'Delete is disabled because there is at least one Correspondence for this Enforcement Action. If you need to delete this Enforcement Action, please first dissociate the Correspondence(s) shown in the Correspondences table.'}"
										rendered="#{enforcementActionDetail.admin && !enforcementActionDetail.editable && enforcementActionDetail.hasCorrespondences}" />
									<af:commandButton text="Delete" id="deleteEnforcementNoCorrespondences"
										disabled="#{!enforcementActionDetail.enforcementActionEditAllowed || enforcementActionDetail.activeWorkflowProcess }"
										action="#{enforcementActionDetail.requestDelete}"
										useWindow="true" windowWidth="500" windowHeight="300"
										shortDesc="#{!enforcementActionDetail.activeWorkflowProcess ? 'Delete' : 
	                	'Delete is disabled because there is at least one active workflow for this Enforcement Action. If you need to delete this Enforcement Action, please first cancel the associated workflow(s).'}"
										rendered="#{enforcementActionDetail.admin && !enforcementActionDetail.editable && !enforcementActionDetail.hasCorrespondences}" />
									<af:commandButton text="Workflow Task"
											rendered="#{enforcementActionDetail.fromTODOList && !enforcementActionDetail.editable}"
											action="#{enforcementActionDetail.goToCurrentWorkflow}" />
								</af:panelButtonBar>
							</f:facet>
		      		</af:panelForm>
      			</h:panelGrid>
      		</afh:rowLayout>
      		</af:page>
    	</af:form>
	</af:document>
</f:view>