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
			<af:page var="foo" value="#{menuModel.model}" id="homeMonitorGroup"
				title="Monitor Group Detail">
				<%@ include file="header.jsp"%>
				<af:inputHidden value="#{monitorGroupSearch.popupRedirect}" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000px">
						<f:subview id="homeMonitorGroupTop">
								<jsp:include page="homeMonitorGroupTop.jsp" />
						</f:subview>
						<af:panelBorder>
							<af:panelGroup layout="vertical"
								partialTriggers="company facility aqdOwned contractor">
								<af:panelHeader text="Monitor Group Information" size="0" />

								<af:panelForm maxColumns="1" labelWidth="220px" width="98%">
									<af:inputText label="Group Name:"
										value="#{homeMonitorGroupDetail.monitorGroup.groupName}"
										id="groupName" columns="60" maximumLength="60"
										showRequired="true" 
										readOnly="#{!homeMonitorGroupDetail.editable}"/>


									<af:selectOneRadio id="aqdOwned"
										label="AQD-owned group?"
										layout="horizontal"
										showRequired="true"
										autoSubmit="true" readOnly="#{!homeMonitorGroupDetail.editable}"
										valueChangeListener="#{homeMonitorGroupDetail.aqdOwnedSelected}"
										value="#{homeMonitorGroupDetail.monitorGroup.aqdOwned}">
										<f:selectItem itemLabel="Yes" itemValue="true" />
										<f:selectItem itemLabel="No" itemValue="false" />
									</af:selectOneRadio>
									
				                  <af:selectOneChoice label="Monitor Reviewer:" tip=" " rendered="#{homeMonitorGroupDetail.internalApp}"
										showRequired="true" readOnly="#{!homeMonitorGroupDetail.editable}"
				                    	unselectedLabel=" " value="#{homeMonitorGroupDetail.monitorGroup.monitorReviewerId}">
				                    <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
				                  </af:selectOneChoice>
				
									<af:selectOneChoice label="Company:" autoSubmit="true"
										value="#{homeMonitorGroupDetail.monitorGroup.cmpId}"
										id="company" unselectedLabel=""
										showRequired="#{!homeMonitorGroupDetail.monitorGroup.aqdOwned}" 
										rendered="#{!homeMonitorGroupDetail.monitorGroup.aqdOwned}"
										valueChangeListener="#{homeMonitorGroupDetail.companySelected}"
										readOnly="#{!homeMonitorGroupDetail.editable}">
										<f:selectItems value="#{companySearch.allCompanies}"/>
									</af:selectOneChoice>
									 
									<af:selectOneChoice label="Contractor:" autoSubmit="true"
										value="#{homeMonitorGroupDetail.monitorGroup.contractor}"
										id="contractor" unselectedLabel="" 
										rendered="#{homeMonitorGroupDetail.monitorGroup.aqdOwned}"
										valueChangeListener="#{homeMonitorGroupDetail.contractorSelected}"
										readOnly="#{!homeMonitorGroupDetail.editable}">
										<f:selectItems
										value="#{infraDefs.monitoringContractors.items[(empty homeMonitorGroupDetail.monitorGroup.contractor ? '' : homeMonitorGroupDetail.monitorGroup.contractor)]}" />
									</af:selectOneChoice>
								</af:panelForm>

								<af:panelGroup layout="vertical"
									rendered="#{(not empty homeMonitorGroupDetail.facilitiesByCompany && homeMonitorGroupDetail.monitorGroup.cmpId ne null && homeMonitorGroupDetail.monitorGroup.facilityId ne null) || (homeMonitorGroupDetail.monitorGroup.facilityId eq null && homeMonitorGroupDetail.editable && not empty homeMonitorGroupDetail.facilitiesByCompany && homeMonitorGroupDetail.monitorGroup.cmpId ne null)}">
									<af:panelForm maxColumns="1" labelWidth="220px" width="98%">
										<af:selectOneChoice label="Facility:" autoSubmit="true"
											value="#{homeMonitorGroupDetail.monitorGroup.facilityId}"
											id="facility" unselectedLabel=""
											showRequired="false"
						                    valueChangeListener="#{homeMonitorGroupDetail.facilitySelected}"
											readOnly="#{!homeMonitorGroupDetail.editable}">
											<f:selectItems value="#{homeMonitorGroupDetail.facilitiesByCompany}" />
										</af:selectOneChoice>
									</af:panelForm>
										
									<af:panelForm maxColumns="2" rows="1" labelWidth="220px" width="98%">
										<af:selectOneChoice label="Facility Class:" inlineStyle="font-size:10pt;"
											value="#{homeMonitorGroupDetail.monitorGroup.facilityClass}" 
											id="facilityClass"
											readOnly="true">
											<f:selectItems
												value="#{facilityReference.permitClassDefs.items[(empty homeMonitorGroupDetail.monitorGroup.facilityClass ? '' : homeMonitorGroupDetail.monitorGroup.facilityClass)]}" />
								    	</af:selectOneChoice>
										<af:selectOneChoice label="Facility Type:" inlineStyle="font-size:10pt;"
											value="#{homeMonitorGroupDetail.monitorGroup.facilityType}" 
											id="facilityType"
											styleClass="FacilityTypeClass x6"
											readOnly="true">
											<f:selectItems
												value="#{facilityReference.facilityTypeDefs.items[(empty homeMonitorGroupDetail.monitorGroup.facilityType ? '' : homeMonitorGroupDetail.monitorGroup.facilityType)]}" />
										</af:selectOneChoice>
									</af:panelForm>
								</af:panelGroup>

								<af:panelForm maxColumns="1" labelWidth="220px" width="98%">
						          <af:inputText id="description" label="Description: " rows="4"
								        showRequired="true"
							            value="#{homeMonitorGroupDetail.monitorGroup.description}"
										readOnly="#{!homeMonitorGroupDetail.editable}"
							            columns="80" maximumLength="255" />
									
								</af:panelForm>

								<af:showDetailHeader text="Associated Sites" disclosed="true"
									id="homeAssociatedSites">
									<jsp:include flush="true" page="homeAssociatedSitesTable.jsp" />
								</af:showDetailHeader>

								<af:showDetailHeader text="Associated Ambient Monitor Reports"
									disclosed="true" id="homeAssociatedAmbientMonitorReports">
									<jsp:include flush="true" page="homeAssociatedAmbientMonitorReportsTable.jsp" />
								</af:showDetailHeader>


								<af:showDetailHeader text="Group Attachments" disclosed="true" 
									id="homeGroupAttachments" rendered="true">
									<jsp:include flush="true" page="groupAttachmentsTable.jsp" />
								</af:showDetailHeader>

								<af:objectSpacer height="25" />

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
		
