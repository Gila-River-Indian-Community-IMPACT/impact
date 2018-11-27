<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Create Correspondence">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}"
				id="createCorrespondence" title="Create Correspondence">
				<%@ include file="header.jsp"%>

				<af:inputHidden value="#{createCorrespondence.pageRedirect}" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000px">
						<af:panelBorder>
							<af:panelGroup layout="vertical"
								partialTriggers="correspondenceDirection associatedWithFacility districtCd countyCd followUpAction">
								<af:panelHeader text="Create Correspondence" size="0" />
								<af:panelHorizontal halign="center">
									<af:panelForm maxColumns="1" labelWidth="220px" width="98%">
										<af:selectOneRadio id="legacyCorrespondence"
											label="Is this a legacy correspondence? :"
											layout="horizontal"
											value="#{createCorrespondence.correspondence.legacyFlag}">
											<f:selectItem itemLabel="Yes" itemValue="true" />
											<f:selectItem itemLabel="No" itemValue="false" />
										</af:selectOneRadio>
										
										<af:selectOneRadio label="Direction:" autoSubmit="true"
											id="correspondenceDirection" showRequired="true"
											value="#{createCorrespondence.correspondence.directionCd}">
											<f:selectItems
												value="#{correspondenceSearch.correspondenceDirectionDef}" />
										</af:selectOneRadio>

										<af:selectOneChoice label="Associated with Facility:"
											autoSubmit="true"
											readOnly="#{!(createCorrespondence.allowedToChangeFacilityId || !createCorrespondence.associated)}"
											value="#{createCorrespondence.associatedWithFacility}"
											id="associatedWithFacility" unselectedLabel=""
											showRequired="true" valueChangeListener="#{createCorrespondence.setDefaultDistrict}">
											<f:selectItem itemLabel="Yes" itemValue="Y" />
											<f:selectItem itemLabel="No" itemValue="N" />
										</af:selectOneChoice>


										<af:inputText label="Facility ID:"
											autoSubmit="true"
											readOnly="#{!(createCorrespondence.allowedToChangeFacilityId)}"
											value="#{createCorrespondence.correspondence.facilityID}"
											id="facilityId" columns="12" maximumLength="12"
											showRequired="#{(empty createCorrespondence.correspondence.facilityID && createCorrespondence.associated)}"
											rendered="#{!empty createCorrespondence.correspondence.facilityID || createCorrespondence.allowedToChangeFacilityId}" />
										<af:inputText label="Facility Name:" readOnly="True"
											value="#{createCorrespondence.correspondence.facilityNm}"
											rendered="#{!empty createCorrespondence.correspondence.facilityNm}" />

										<af:selectOneChoice label="District:" autoSubmit="true"
											value="#{createCorrespondence.correspondence.district}"
											rendered="#{empty createCorrespondence.correspondence.facilityID && !createCorrespondence.associated && (!empty createCorrespondence.associatedWithFacility) && infraDefs.districtVisible}"
											id="districtCd"
											showRequired="#{empty createCorrespondence.correspondence.facilityID}">
											<f:selectItems value="#{infraDefs.districts}" />
										</af:selectOneChoice>										
										<af:selectOneChoice label="County:" autoSubmit="true" unselectedLabel=" "
											value="#{createCorrespondence.correspondence.countyCd}"
											rendered="#{empty createCorrespondence.correspondence.facilityID && !createCorrespondence.associated && (!empty createCorrespondence.associatedWithFacility) && (!empty createCorrespondence.correspondence.district)}"
											id="countyCd">
											<f:selectItems value="#{createCorrespondence.correspondence.counties}" />
										</af:selectOneChoice>
										<af:selectOneChoice label="City:" unselectedLabel=" "
											value="#{createCorrespondence.correspondence.cityCd}"
											rendered="#{empty createCorrespondence.correspondence.facilityID && !createCorrespondence.associated && (!empty createCorrespondence.associatedWithFacility) && (!empty createCorrespondence.correspondence.district) && (!empty createCorrespondence.correspondence.countyCd)}"
											id="cityCd">
											<f:selectItems value="#{createCorrespondence.correspondence.cities}" />
										</af:selectOneChoice>
									</af:panelForm>
								</af:panelHorizontal>
								<af:panelForm rows="2" labelWidth="160px" width="98%">
									<af:selectOneChoice label="Type:" id="correspondenceType"
										showRequired="true"
										value="#{createCorrespondence.correspondenceTypeCode}">
										<f:selectItems
											value="#{correspondenceSearch.correspondenceDef}" />
									</af:selectOneChoice>
									<af:selectInputDate label="Receipt Date:"
										id="correspondenceReceiptDate"
										showRequired="#{createCorrespondence.correspondence.incoming}"
										value="#{createCorrespondence.correspondence.receiptDate}">
										<af:validateDateTimeRange minimum="1900-01-01"
											maximum="#{createCorrespondence.maxDate}" />
									</af:selectInputDate>
									<af:selectOneChoice label="Category:"
										id="correspondenceCategory"
										showRequired="#{createCorrespondence.correspondence.incoming}"
										value="#{createCorrespondence.correspondence.correspondenceCategoryCd}">
										<f:selectItems
											value="#{correspondenceSearch.correspondenceCategoryDef}" />
									</af:selectOneChoice>
									<af:selectInputDate label="Date Generated:"
										id="correspondenceGeneratedDate"
										showRequired="#{createCorrespondence.correspondence.outgoing}"
										value="#{createCorrespondence.correspondence.dateGenerated}">
										<af:validateDateTimeRange minimum="1900-01-01"
											maximum="#{createCorrespondence.maxDate}" />
									</af:selectInputDate>
								</af:panelForm>

								<af:showDetailHeader text="Message Details" disclosed="true"
									id="correspondenceMessage">
									<af:panelForm maxColumns="1" labelWidth="150px" width="98%">
										<af:inputText label="To:" columns="50" rows="1"
											maximumLength="150"
											value="#{createCorrespondence.correspondence.toPerson}" />
										<af:inputText label="From:" columns="50" rows="1"
											maximumLength="150"
											value="#{createCorrespondence.correspondence.fromPerson}" />
										<af:selectOneChoice id="divisionReviewer"
											label="Division Reviewer:" 
											showRequired="#{createCorrespondence.correspondence.followUpAction}"
											value="#{createCorrespondence.correspondence.reviewerId}">
											<f:selectItems
												value="#{infraDefs.basicUsersDef.items[(empty createCorrespondence.correspondence.reviewerId?0:createCorrespondence.correspondence.reviewerId)]}" />
										</af:selectOneChoice>
										<%-- not needed anymore since we now support multiple attachments for correspondence
										<afh:rowLayout
											rendered="#{createCorrespondence.correspondence.documentID != null}">
											<af:goLink
												text="Document #{createCorrespondence.correspondence.documentID}"
												destination="#{createCorrespondence.correspondence.documentUrl}"
												targetFrame="_blank" />
											<af:objectSpacer height="6" width="6" />
											<af:commandButton text="Delete"
												action="#{createCorrespondence.deleteFile}"
												disabled="#{!createCorrespondence.allowFileChange}" />
										</afh:rowLayout> 
										<af:inputFile
											label="#{createCorrespondence.correspondence.documentID != null?'Replacement File to Upload':'Optional File to Upload'}"
											id="attachment"
											disabled="#{!createCorrespondence.allowFileChange}"
											value="#{createCorrespondence.fileToUpload}" />--%>
										<af:inputText label="Subject:" columns="100" rows="1"
											maximumLength="150"
											value="#{createCorrespondence.correspondence.regarding}" />
										<af:inputText label="Additional Info:" id="additionalInfo"
											columns="100" rows="10"
											value="#{createCorrespondence.correspondence.additionalInfo}" />
									</af:panelForm>
								</af:showDetailHeader>

								<af:showDetailHeader text="Enforcement Action Details"
									rendered="#{createCorrespondence.associated}"
									disclosed="true" id="linkedEnforcementAction">
									<af:panelForm labelWidth="250px" rows="1" 
										partialTriggers="linkedtoEnfAction facilityId">
										<af:selectOneRadio id="linkedtoEnfAction"
											label="Associated with Enforcement Action:"
											layout="horizontal" autoSubmit="true"
											value="#{createCorrespondence.correspondence.linkedtoEnfAction}">
											<f:selectItem itemLabel="No" itemValue="false" />
											<f:selectItem itemLabel="Yes" itemValue="true" />
										</af:selectOneRadio>
            							<af:selectOneChoice id="linkedToId"
											label="Enforcement Action:" unselectedLabel=""
              								rendered="#{createCorrespondence.correspondence.linkedtoEnfAction}"
              								readOnly="false" showRequired="true"
              								value="#{createCorrespondence.correspondence.linkedToId}">
              								<f:selectItems
                								value="#{createCorrespondence.facilityEnforcementAction}" />	
            							</af:selectOneChoice>
									</af:panelForm>
									<af:panelForm rows="1" maxColumns="2" labelWidth="250px"
										width="98%" partialTriggers="hideAttachments linkedtoEnfAction">
										<af:selectBooleanCheckbox label="Hide Attachments (confidential information):"
											inlineStyle="margin-left: 0px;"
											rendered="#{enforcementActionDetail.enforcementActionEditAllowed && createCorrespondence.correspondence.linkedtoEnfAction}"
											id="hideAttachments" autoSubmit="true"
											value="#{createCorrespondence.correspondence.hideAttachments}" />
									</af:panelForm>
								</af:showDetailHeader>
								
								<af:showDetailHeader text="Follow-up Information"
									disclosed="true" id="correspondenceFollowUp"
									rendered="#{createCorrespondence.associatedWithFacility == 'Y'}">
									<af:panelForm rows="1" maxColumns="2" labelWidth="150px"
										width="98%">
										<af:selectBooleanCheckbox label="Follow-up Action?"
											id="followUpAction" autoSubmit="true"
											value="#{createCorrespondence.correspondence.followUpAction}" />
										<af:selectInputDate label="Follow-up Action Date:"
											id="followUpActionDate"
											showRequired="#{createCorrespondence.correspondence.followUpAction}"
											rendered="#{createCorrespondence.correspondence.followUpAction}"
											value="#{createCorrespondence.correspondence.followUpActionDate}">
											<af:validateDateTimeRange
												minimum="#{createCorrespondence.minFollowUpDate}" />
										</af:selectInputDate>
									</af:panelForm>
									<af:panelForm rows="1" maxColumns="2" labelWidth="150px"
										width="98%">
										<af:inputText label="Follow-up Action Description:"
											id="followUpActionDescription" columns="100"
											showRequired="#{createCorrespondence.correspondence.followUpAction}"
											rendered="#{createCorrespondence.correspondence.followUpAction}"
											rows="10" maximumLength="1000"
											value="#{createCorrespondence.correspondence.followUpActionDescription}" />
									</af:panelForm>
								</af:showDetailHeader>

								<af:objectSpacer height="25" />

								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Create"
											action="#{createCorrespondence.createCorrespondence}" />
										<af:commandButton text="Reset"
											action="#{createCorrespondence.reset}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:panelGroup>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
