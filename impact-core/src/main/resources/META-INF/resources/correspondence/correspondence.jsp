<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Correspondence Detail">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}" id="correspondence"
				title="Correspondence Detail">
				<%@ include file="header.jsp"%>

				<af:inputHidden value="#{correspondenceDetail.pageRedirect}" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000px">
						<f:subview id="correspondenceTop">
								<jsp:include page="correspondenceTop.jsp" />
						</f:subview>
						<af:panelBorder>
							<af:panelGroup layout="vertical"
								rendered="#{correspondenceDetail.correspondenceID != null}"
								partialTriggers="correspondenceDirection associatedWithFacility districtCd countyCd facilityId followUpAction">
								<af:panelHeader text="Correspondence Information" size="0" />
								<af:panelHorizontal halign="center">
									<af:panelForm maxColumns="1" labelWidth="220px" width="98%">
										<af:selectOneRadio id="legacyCorrespondence"
											label="Is this a legacy correspondence? :"
											readOnly="#{!correspondenceDetail.editable}"
											layout="horizontal"
											value="#{correspondenceDetail.correspondence.legacyFlag}">
											<f:selectItem itemLabel="Yes" itemValue="true" />
											<f:selectItem itemLabel="No" itemValue="false" />
										</af:selectOneRadio>
										<af:selectOneRadio label="Direction:" autoSubmit="true"
											readOnly="#{! correspondenceDetail.editable}"
											id="correspondenceDirection" showRequired="true"
											value="#{correspondenceDetail.correspondence.directionCd}">
											<f:selectItems
												value="#{correspondenceSearch.correspondenceDirectionDef}" />
										</af:selectOneRadio>
										<af:selectOneChoice label="Associated with Facility:"
											autoSubmit="true"
											readOnly="#{!(correspondenceDetail.editable && (correspondenceDetail.allowedToChangeFacilityId || !correspondenceDetail.associated))}"
											value="#{correspondenceDetail.associatedWithFacility}"
											id="associatedWithFacility" showRequired="true">
											<f:selectItem itemLabel="Yes" itemValue="Y" />
											<f:selectItem itemLabel="No" itemValue="N" />
										</af:selectOneChoice>
										<af:inputText label="Facility ID:"
											autoSubmit="true"
											rendered="#{correspondenceDetail.allowedToChangeFacilityId}"
											value="#{correspondenceDetail.correspondence.facilityID}"
											id="facilityId" columns="12" maximumLength="12"
											showRequired="#{(empty correspondenceDetail.correspondence.facilityID && correspondenceDetail.associated)}"
											readOnly="#{!correspondenceDetail.editable}"/>
										<af:selectOneChoice label="District:" autoSubmit="true" 
											readOnly="#{! correspondenceDetail.editable}"
											value="#{correspondenceDetail.correspondence.district}"
											rendered="#{(empty correspondenceDetail.correspondence.facilityID && !correspondenceDetail.associated) && infraDefs.districtVisible}"
											id="districtCd"
											showRequired="#{empty correspondenceDetail.correspondence.facilityID}">
											<f:selectItems value="#{infraDefs.districts}" />
										</af:selectOneChoice>
										<af:selectOneChoice label="County:" autoSubmit="true" unselectedLabel=" "
											readOnly="#{! correspondenceDetail.editable}"
											value="#{correspondenceDetail.correspondence.countyCd}"
											rendered="#{empty correspondenceDetail.correspondence.facilityID && !correspondenceDetail.associated && (!empty correspondenceDetail.correspondence.district)}"
											id="countyCd">
											<f:selectItems value="#{correspondenceDetail.correspondence.counties}" />
										</af:selectOneChoice>
										<af:selectOneChoice label="City:" unselectedLabel=" "
											readOnly="#{! correspondenceDetail.editable}"
											value="#{correspondenceDetail.correspondence.cityCd}"
											rendered="#{empty correspondenceDetail.correspondence.facilityID && !correspondenceDetail.associated && (!empty correspondenceDetail.correspondence.district) && (!empty correspondenceDetail.correspondence.countyCd)}"
											id="cityCd">
											<f:selectItems value="#{correspondenceDetail.correspondence.cities}" />
										</af:selectOneChoice>
									</af:panelForm>
								</af:panelHorizontal>
								<af:panelForm rows="2" labelWidth="160px" width="98%">
									<af:selectOneChoice label="Type:"
										readOnly="#{!correspondenceDetail.allowTypeUpdate}"
										id="correspondenceType" showRequired="true"
										value="#{correspondenceDetail.correspondenceTypeCode}">
										<f:selectItems
											value="#{correspondenceSearch.correspondenceDef}" />
									</af:selectOneChoice>
									<af:selectInputDate label="Receipt Date:"
										id="correspondenceReceiptDate"
										readOnly="#{! correspondenceDetail.editable}"
										showRequired="#{correspondenceDetail.correspondence.incoming}"
										value="#{correspondenceDetail.correspondence.receiptDate}">
										<af:validateDateTimeRange minimum="1900-01-01"
											maximum="#{correspondenceDetail.maxDate}" />
									</af:selectInputDate>
									<af:selectOneChoice label="Category:"
										readOnly="#{! correspondenceDetail.allowTypeUpdate }"
										showRequired="#{correspondenceDetail.correspondence.incoming}"
										id="correspondenceCategory"
										value="#{correspondenceDetail.correspondence.correspondenceCategoryCd}">
										<f:selectItems
											value="#{correspondenceSearch.correspondenceCategoryDef}" />
									</af:selectOneChoice>
									<af:selectInputDate label="Date Generated:"
										id="correspondenceGeneratedDate"
										readOnly="#{! correspondenceDetail.editable}"
										showRequired="#{correspondenceDetail.correspondence.outgoing}"
										value="#{correspondenceDetail.correspondence.dateGenerated}">
										<af:validateDateTimeRange minimum="1900-01-01"
											maximum="#{correspondenceDetail.maxDate}" />
									</af:selectInputDate>
								</af:panelForm>

								<af:showDetailHeader text="Message Details" disclosed="true"
									id="correspondenceMessage">
									<af:panelForm maxColumns="1" labelWidth="150px" width="98%">
										<af:inputText label="To:"
											readOnly="#{! correspondenceDetail.editable}" columns="50"
											rows="1" maximumLength="150"
											value="#{correspondenceDetail.correspondence.toPerson}" />
										<af:inputText label="From:"
											readOnly="#{! correspondenceDetail.editable}" columns="50"
											rows="1" maximumLength="150"
											value="#{correspondenceDetail.correspondence.fromPerson}" />
										<af:selectOneChoice id="divisionReviewer"
											label="Division Reviewer:"
											showRequired="#{correspondenceDetail.correspondence.followUpAction}"
											readOnly="#{! correspondenceDetail.editable}"
											value="#{correspondenceDetail.correspondence.reviewerId}">
											<f:selectItems
												value="#{infraDefs.basicUsersDef.items[(empty correspondenceDetail.correspondence.reviewerId?0:correspondenceDetail.correspondence.reviewerId)]}" />
										</af:selectOneChoice>
										<%--  not needed anymore since we now support multiple attachment capability for correspondence
										<afh:rowLayout
											rendered="#{correspondenceDetail.correspondence.documentID != null}">
											<af:panelGroup layout="horizontal">
												<af:inputText label="Attached: " readOnly="true" />
												<af:goLink
													text="Document #{correspondenceDetail.correspondence.documentID}"
													disabled="#{correspondenceDetail.editable}"
													destination="#{correspondenceDetail.correspondence.documentUrl}"
													targetFrame="_blank" />
											</af:panelGroup>
											<af:objectSpacer height="6" width="6" />
											<af:commandButton text="Delete"
												action="#{correspondenceDetail.deleteFile}" />
										</afh:rowLayout>
										<af:inputFile
											label="#{correspondenceDetail.correspondence.documentID != null?'Replacement File to Upload':'Optional File to Upload'}"
											id="attachment" rendered="#{correspondenceDetail.editable}"
											value="#{correspondenceDetail.fileToUpload}" />--%>
										<af:inputText label="Subject:"
											readOnly="#{! correspondenceDetail.editable}" columns="100"
											rows="1" maximumLength="150"
											value="#{correspondenceDetail.correspondence.regarding}" />
										<af:inputText label="Additional Info:" id="additionalInfo"
											readOnly="#{! correspondenceDetail.editable}" columns="100"
											rows="10"
											value="#{correspondenceDetail.correspondence.additionalInfo}" />
									</af:panelForm>
								</af:showDetailHeader>

								<af:showDetailHeader text="Enforcement Action Details"
									rendered="#{correspondenceDetail.associated}"
									disclosed="true" id="linkedEnforcementAction">
									<af:panelForm labelWidth="250px" rows="1" partialTriggers="linkedtoEnfAction">
										<af:selectOneRadio id="linkedtoEnfAction"
											label="Associated with Enforcement Action:"
											readOnly="#{!correspondenceDetail.editable}"
											layout="horizontal" autoSubmit="true"
											value="#{correspondenceDetail.correspondence.linkedtoEnfAction}">
											<f:selectItem itemLabel="No" itemValue="false" />
											<f:selectItem itemLabel="Yes" itemValue="true" />
										</af:selectOneRadio>
										<af:panelLabelAndMessage id="linkedToIdLnk"
											label="Enforcement Action:" 
												rendered="#{!correspondenceDetail.editable 
																&& correspondenceDetail.correspondence.linkedtoEnfAction}">
											<af:commandLink text="#{correspondenceDetail.correspondence.linkedEnfActionId}"
												action="#{enforcementActionDetail.submitEnforcementAction}"
												rendered="#{correspondenceDetail.enforcementActionUser}">
												<t:updateActionListener
													property="#{enforcementActionDetail.enforcementActionId}"
													value="#{correspondenceDetail.correspondence.linkedToId}" />
												<t:updateActionListener
													property="#{enforcementActionDetail.facilityId}"
													value="#{correspondenceDetail.correspondence.facilityID}" />
												<t:updateActionListener
	         										property="#{menuItem_enforcementActionDetail.disabled}" value="true" />
	         								</af:commandLink>
	         								<af:inputText value="#{correspondenceDetail.correspondence.linkedEnfActionId}"
	         									rendered="#{!correspondenceDetail.enforcementActionUser}" readOnly="true"/>
										</af:panelLabelAndMessage>		
            							<af:selectOneChoice id="linkedToId"
											label="Enforcement Action:" unselectedLabel=""
              								rendered="#{correspondenceDetail.editable 
              												&& correspondenceDetail.correspondence.linkedtoEnfAction}"
              								readOnly="false" showRequired="true"
              								value="#{correspondenceDetail.correspondence.linkedToId}">
              								<f:selectItems
                								value="#{correspondenceDetail.facilityEnforcementAction}" />	
            							</af:selectOneChoice>
									</af:panelForm>
									<af:panelForm rows="1" maxColumns="2" labelWidth="250px"
										width="98%" partialTriggers="hideAttachments linkedtoEnfAction">
										<af:selectBooleanCheckbox label="Hide Attachments (confidential information):"
											id="hideAttachments" autoSubmit="true"
											inlineStyle="margin-left: 0px;"
											rendered="#{enforcementActionDetail.enforcementActionEditAllowed && correspondenceDetail.correspondence.linkedtoEnfAction}"
											readOnly="#{! correspondenceDetail.editable}"
											value="#{correspondenceDetail.correspondence.hideAttachments}" />
									</af:panelForm>
								</af:showDetailHeader>

								<f:subview id="doc_attachments" rendered="#{!correspondenceDetail.correspondence.hideAttachments || enforcementActionDetail.enforcementActionEditAllowed}">
									<jsp:include flush="true"
										page="../doc_attachments/doc_attachments.jsp" />
								</f:subview>
								
								<af:showDetailHeader text="Follow-up Information"
									disclosed="true" id="correspondenceFollowUp"
									rendered="#{correspondenceDetail.associatedWithFacility == 'Y'}">
									<af:panelForm rows="1" maxColumns="2" labelWidth="150px"
										width="98%">
										<af:selectBooleanCheckbox label="Follow-up Action?"
											id="followUpAction" autoSubmit="true"
											readOnly="#{! correspondenceDetail.editable}"
											value="#{correspondenceDetail.correspondence.followUpAction}" />
										<af:selectInputDate label="Follow-up Action Date:"
											id="followUpActionDate"
											showRequired="#{correspondenceDetail.correspondence.followUpAction}"
											readOnly="#{! correspondenceDetail.editable}"
											rendered="#{correspondenceDetail.correspondence.followUpAction}"
											value="#{correspondenceDetail.correspondence.followUpActionDate}">
										</af:selectInputDate>
									</af:panelForm>
									<af:panelForm rows="1" maxColumns="2" labelWidth="150px"
										width="98%">
										<af:inputText label="Follow-up Action Description:"
											id="followUpActionDescription"
											showRequired="#{correspondenceDetail.correspondence.followUpAction}"
											readOnly="#{! correspondenceDetail.editable}" columns="100"
											rendered="#{correspondenceDetail.correspondence.followUpAction}"
											rows="10" maximumLength="1000"
											value="#{correspondenceDetail.correspondence.followUpActionDescription}" />
									</af:panelForm>
								</af:showDetailHeader>

								<af:objectSpacer height="25" />

								<afh:rowLayout halign="center"
									rendered="#{correspondenceDetail.correspondenceID != null}">
									<af:panelButtonBar>
										<af:commandButton text="Edit"
											action="#{correspondenceDetail.editCorrespondence}"
											rendered="#{! correspondenceDetail.editable && !correspondenceDetail.readOnlyUser && correspondenceDetail.internalApp}" />
										<af:commandButton id="deleteCorrBtn" text="Delete"
											rendered="#{!correspondenceDetail.editable && correspondenceDetail.stars2Admin && correspondenceDetail.internalApp}"
											action="dialog:deletCorrespondence" useWindow="true"
											disabled="#{correspondenceDetail.activeWorkflowProcess}"
											windowWidth="600" windowHeight="300" />
										<af:commandButton immediate="true"
											text="Show Current Facility Profile"
											rendered="#{! correspondenceDetail.editable && correspondenceDetail.internalApp && !(empty correspondenceDetail.correspondence.facilityID)}"
											action="#{facilityProfile.submitProfileById}">
											<t:updateActionListener
												property="#{facilityProfile.facilityId}"
												value="#{correspondenceDetail.correspondence.facilityID}" />
											<t:updateActionListener
												property="#{menuItem_facProfile.disabled}" value="false" />
										</af:commandButton>
										<af:commandButton text="Workflow Task"
											rendered="#{correspondenceDetail.internalApp && correspondenceDetail.fromTODOList  && ! correspondenceDetail.editable}"
											action="#{correspondenceDetail.goToCurrentWorkflow}" />
										<af:commandButton text="Save"
											action="#{correspondenceDetail.saveEditCorrespondence}"
											rendered="#{correspondenceDetail.editable}" />
										<af:commandButton text="Cancel"
											action="#{correspondenceDetail.cancelEdit}"
											rendered="#{correspondenceDetail.editable}" />
									</af:panelButtonBar>
								</afh:rowLayout>
								<afh:rowLayout halign="center"
									rendered="#{correspondenceDetail.correspondenceID == null}">
									<af:commandButton immediate="true"
										text="Show Current Facility Profile"
										rendered="#{! correspondenceDetail.editable && correspondenceDetail.internalApp && !(empty correspondenceDetail.correspondence.facilityID)}"
										action="#{facilityProfile.submitProfileById}">
										<t:updateActionListener
											property="#{facilityProfile.facilityId}"
											value="#{correspondenceDetail.correspondence.facilityID}" />
										<t:updateActionListener
											property="#{menuItem_facProfile.disabled}" value="false" />
									</af:commandButton>
								</afh:rowLayout>
							</af:panelGroup>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
