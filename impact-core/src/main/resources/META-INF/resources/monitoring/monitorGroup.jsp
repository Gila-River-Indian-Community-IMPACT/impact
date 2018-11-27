<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Monitor Group Detail">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}" id="monitorGroup"
				title="Monitor Group Detail">
				<%@ include file="header.jsp"%>
				<af:inputHidden value="#{monitorGroupSearch.popupRedirect}" />
				<af:inputHidden value="#{monitorGroupDetail.popupRedirect}"
					rendered="#{!monitorGroupDetail.internalApp}" />
				
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000px">
						<f:subview id="monitorGroupTop">
								<jsp:include page="monitorGroupTop.jsp" />
						</f:subview>
						<af:panelBorder>
							<af:panelGroup layout="vertical"
								partialTriggers="company facility aqdOwned contractor">
								<af:panelHeader text="Monitor Group Information" size="0" />

								<af:panelForm maxColumns="1" labelWidth="220px" width="98%">
									<af:inputText label="Group Name:"
										value="#{monitorGroupDetail.monitorGroup.groupName}"
										id="groupName" columns="60" maximumLength="60"
										showRequired="true" 
										readOnly="#{!monitorGroupDetail.editable}"/>


									<af:selectOneRadio id="aqdOwned"
										label="AQD-owned group?"
										layout="horizontal"
										showRequired="true"
										autoSubmit="true" readOnly="#{!monitorGroupDetail.editable}"
										valueChangeListener="#{monitorGroupDetail.aqdOwnedSelected}"
										value="#{monitorGroupDetail.monitorGroup.aqdOwned}">
										<f:selectItem itemLabel="Yes" itemValue="true" />
										<f:selectItem itemLabel="No" itemValue="false" />
									</af:selectOneRadio>
									
				                  <af:selectOneChoice label="Monitor Reviewer:" tip=" " rendered="#{monitorGroupDetail.internalApp}"
										showRequired="true" readOnly="#{!monitorGroupDetail.editable}"
				                    	unselectedLabel=" " value="#{monitorGroupDetail.monitorGroup.monitorReviewerId}">
				                    <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
				                  </af:selectOneChoice>
				
									<af:selectOneChoice label="Company:" autoSubmit="true"
										value="#{monitorGroupDetail.monitorGroup.cmpId}"
										id="company" unselectedLabel=""
										showRequired="#{!monitorGroupDetail.monitorGroup.aqdOwned}" 
										rendered="#{!monitorGroupDetail.monitorGroup.aqdOwned}"
										valueChangeListener="#{monitorGroupDetail.companySelected}"
										readOnly="#{!monitorGroupDetail.editable}">
										<f:selectItems value="#{companySearch.allCompanies}"/>
									</af:selectOneChoice>
									 
									<af:selectOneChoice label="Contractor:" autoSubmit="true"
										value="#{monitorGroupDetail.monitorGroup.contractor}"
										id="contractor" unselectedLabel="" 
										rendered="#{monitorGroupDetail.monitorGroup.aqdOwned && monitorGroupDetail.internalApp}"
										valueChangeListener="#{monitorGroupDetail.contractorSelected}"
										readOnly="#{!monitorGroupDetail.editable}">
										<f:selectItems
										value="#{infraDefs.monitoringContractors.items[(empty monitorGroupDetail.monitorGroup.contractor ? '' : monitorGroupDetail.monitorGroup.contractor)]}" />
									</af:selectOneChoice>
								</af:panelForm>

								<af:panelGroup layout="vertical"
									rendered="#{(not empty monitorGroupDetail.facilitiesByCompany && monitorGroupDetail.monitorGroup.cmpId ne null && monitorGroupDetail.monitorGroup.facilityId ne null) || (monitorGroupDetail.monitorGroup.facilityId eq null && monitorGroupDetail.editable && not empty monitorGroupDetail.facilitiesByCompany && monitorGroupDetail.monitorGroup.cmpId ne null)}">
									<af:panelForm maxColumns="1" labelWidth="220px" width="98%">
										<af:selectOneChoice label="Facility:" autoSubmit="true"
											value="#{monitorGroupDetail.monitorGroup.facilityId}"
											id="facility" unselectedLabel=""
											showRequired="false"
						                    valueChangeListener="#{monitorGroupDetail.facilitySelected}"
											readOnly="#{!monitorGroupDetail.editable}">
											<f:selectItems value="#{monitorGroupDetail.facilitiesByCompany}" />
										</af:selectOneChoice>
									</af:panelForm>
										
									<af:panelForm maxColumns="2" rows="1" labelWidth="220px" width="98%">
										<af:selectOneChoice label="Facility Class:" inlineStyle="font-size:10pt;"
											value="#{monitorGroupDetail.monitorGroup.facilityClass}" 
											id="facilityClass"
											readOnly="true">
											<f:selectItems
												value="#{facilityReference.permitClassDefs.items[(empty monitorGroupDetail.monitorGroup.facilityClass ? '' : monitorGroupDetail.monitorGroup.facilityClass)]}" />
								    	</af:selectOneChoice>
										<af:selectOneChoice label="Facility Type:" inlineStyle="font-size:10pt;"
											value="#{monitorGroupDetail.monitorGroup.facilityType}" 
											id="facilityType"
											styleClass="FacilityTypeClass x6"
											readOnly="true">
											<f:selectItems
												value="#{facilityReference.facilityTypeDefs.items[(empty monitorGroupDetail.monitorGroup.facilityType ? '' : monitorGroupDetail.monitorGroup.facilityType)]}" />
										</af:selectOneChoice>
									</af:panelForm>
								</af:panelGroup>

								<af:panelForm maxColumns="1" labelWidth="220px" width="98%">
						          <af:inputText id="description" label="Description: " rows="4"
								        showRequired="true"
							            value="#{monitorGroupDetail.monitorGroup.description}"
										readOnly="#{!monitorGroupDetail.editable}"
							            columns="80" maximumLength="255" />
									
								</af:panelForm>

								<af:showDetailHeader text="Associated Sites" disclosed="true"
									id="associatedSites">
									<jsp:include flush="true" page="associatedSitesTable.jsp" />
								</af:showDetailHeader>

								<af:showDetailHeader text="Associated Ambient Monitor Reports"
									disclosed="true" id="associatedAmbientMonitorReports">
									<jsp:include flush="true" page="associatedAmbientMonitorReportsTable.jsp" />
								</af:showDetailHeader>


								<af:showDetailHeader text="Group Attachments" disclosed="true" 
									id="groupAttachments" 
									rendered="#{!monitorGroupDetail.monitorReportUploadUser}">
									<jsp:include flush="true" page="groupAttachmentsTable.jsp" />
								</af:showDetailHeader>

								<af:showDetailHeader text="Notes" disclosed="true" 
									id="notes" 
									rendered="#{monitorGroupDetail.internalApp && !monitorGroupDetail.monitorReportUploadUser}">
									<jsp:include flush="true" page="notesTable.jsp" />
								</af:showDetailHeader>

								<af:objectSpacer height="25" />

								<afh:rowLayout halign="center"
									rendered="#{monitorGroupDetail.groupId != null}">
									<af:panelButtonBar>
										<af:commandButton text="Edit"
											action="#{monitorGroupDetail.editGroup}"
											rendered="#{! monitorGroupDetail.editable && !monitorGroupDetail.readOnlyUser 
															&& !monitorGroupDetail.monitorReportUploadUser && monitorGroupDetail.internalApp}" />
										<af:commandButton id="deleteGroupBtn" text="Delete"
											rendered="#{!monitorGroupDetail.editable && monitorGroupDetail.stars2Admin && monitorGroupDetail.internalApp}"
											disabled="#{monitorGroupDetail.associatedWithSites 
														|| monitorGroupDetail.associatedWithAmbientMonitorReports}"
											action="dialog:deleteMonitorGroup" useWindow="true"
											windowWidth="600" windowHeight="300" />
										<af:commandButton text="Save"
											action="#{monitorGroupDetail.saveEditGroup}"
											rendered="#{monitorGroupDetail.editable && monitorGroupDetail.internalApp}" />
										<af:commandButton text="Cancel"
											action="#{monitorGroupDetail.cancelEdit}"
											rendered="#{monitorGroupDetail.editable && monitorGroupDetail.internalApp}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:panelGroup>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

			</af:page>
		</af:form>
<f:verbatim><%@ include
		file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
<f:verbatim><%@ include file="../scripts/wording-filter.js"%></f:verbatim>
<f:verbatim><%@ include
		file="../scripts/FacilityType-Option.js"%></f:verbatim>
<f:verbatim><%@ include
		file="../scripts/facility-detail.js"%></f:verbatim>
	</af:document>
</f:view>
		
