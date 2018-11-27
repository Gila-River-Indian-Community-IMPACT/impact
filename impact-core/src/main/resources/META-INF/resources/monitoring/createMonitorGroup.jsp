<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Create Monitor Group">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}"
				id="createMonitorGroup" title="Create Monitor Group">
				<%@ include file="header.jsp"%>
				<af:inputHidden value="#{createMonitorGroup.pageRedirect}" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000px">
						<af:panelBorder>
							<af:panelGroup layout="vertical"
								partialTriggers="company facility aqdOwned contractor">
								<af:panelHeader text="Create Monitor Group" size="0" />

								<af:panelForm maxColumns="1" labelWidth="220px" width="98%">
									<af:inputText label="Group Name:"
										value="#{createMonitorGroup.monitorGroup.groupName}"
										id="groupName" columns="60" maximumLength="60"
										showRequired="true" 
										readOnly="false"/>


									<af:selectOneRadio id="aqdOwned"
										label="AQD-owned group?"
										layout="horizontal"
										showRequired="true"
										autoSubmit="true" readOnly="false"
										valueChangeListener="#{createMonitorGroup.aqdOwnedSelected}"
										value="#{createMonitorGroup.monitorGroup.aqdOwned}">
										<f:selectItem itemLabel="Yes" itemValue="true" />
										<f:selectItem itemLabel="No" itemValue="false" />
									</af:selectOneRadio>
									
									<af:selectOneChoice label="Monitor Reviewer:" tip=" "
										showRequired="true" readOnly="false"
				                    	unselectedLabel=" " value="#{createMonitorGroup.monitorGroup.monitorReviewerId}">
				                    <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
				                  </af:selectOneChoice>
									
									<af:selectOneChoice label="Company:" autoSubmit="true"
										value="#{createMonitorGroup.monitorGroup.cmpId}"
										id="company" unselectedLabel=""
										showRequired="#{!createMonitorGroup.monitorGroup.aqdOwned}" 
										rendered="#{!createMonitorGroup.monitorGroup.aqdOwned}"
										valueChangeListener="#{monitorGroupDetail.companySelected}"
										readOnly="false">
										<f:selectItems value="#{companySearch.allCompanies}"/>
									</af:selectOneChoice>
									
									<af:selectOneChoice label="Contractor:" autoSubmit="true"
										value="#{createMonitorGroup.monitorGroup.contractor}"
										id="contractor" unselectedLabel=""
										rendered="#{createMonitorGroup.monitorGroup.aqdOwned}"
										valueChangeListener="#{monitorGroupDetail.contractorSelected}"
										readOnly="false">
										<f:selectItems
										value="#{infraDefs.monitoringContractors.items[(empty createMonitorGroup.monitorGroup.contractor ? '' : createMonitorGroup.monitorGroup.contractor)]}" />
									</af:selectOneChoice>
								</af:panelForm>

								<af:panelGroup layout="vertical"
									rendered="#{(not empty monitorGroupDetail.facilitiesByCompany && createMonitorGroup.monitorGroup.cmpId ne null && createMonitorGroup.monitorGroup.facilityId ne null) || (createMonitorGroup.monitorGroup.facilityId eq null && not empty monitorGroupDetail.facilitiesByCompany && createMonitorGroup.monitorGroup.cmpId ne null)}">
									<af:panelForm maxColumns="1" labelWidth="220px" width="98%">
										<af:selectOneChoice label="Facility:" autoSubmit="true"
											value="#{createMonitorGroup.monitorGroup.facilityId}"
											id="facility" unselectedLabel=""
											showRequired="false"
											readOnly="false">
											<f:selectItems value="#{monitorGroupDetail.facilitiesByCompany}" />
										</af:selectOneChoice>
									</af:panelForm>
								</af:panelGroup>

								<af:panelForm maxColumns="1" labelWidth="220px" width="98%">
						          <af:inputText id="description" label="Description: " rows="4"
								        showRequired="true"
							            value="#{createMonitorGroup.monitorGroup.description}"
										readOnly="false"
							            columns="80" maximumLength="255" />
									
								</af:panelForm>

								<af:objectSpacer height="25" />

								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Create"
											action="#{createMonitorGroup.create}" />
										<af:commandButton text="Reset"
											action="#{createMonitorGroup.reset}" />
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
