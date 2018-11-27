<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<f:view>
	<af:document title="Bulk Operations">
		<f:facet name="metaContainer">
			<f:verbatim>
				<h:outputText value="#{facilityProfile.refreshStr}" escape="false" />
			</f:verbatim>
		</f:facet>

		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Bulk Operations">
				<%@ include file="../util/header.jsp"%>

				<af:poll interval="3000" id="progressPoll"
					rendered="#{bulkOperationsCatalog.asyncRunning}" />
					
				<af:panelGroup layout="vertical"
					partialTriggers="progressPoll">
					<af:progressIndicator id="progressid"
						value="#{bulkOperationsCatalog.bulkOperation}"
						rendered="#{bulkOperationsCatalog.showProgressBar}"
						partialTriggers="progressPoll">
						<af:outputFormatted
							value="Processing search request..." />
					</af:progressIndicator>
				</af:panelGroup>

				<afh:rowLayout halign="center">
					<h:panelGrid border="1">
						<af:panelBorder>
							<f:facet name="left">
								<h:panelGrid columns="1" width="250">
									<t:tree2 id="bulkOperation" showRootNode="false"
										value="#{bulkOperationsCatalog.treeData}" var="node"
										varNodeToggler="t" clientSideToggle="true">
										<f:facet name="root">
											<h:panelGroup>
												<af:commandMenuItem immediate="true">
													<t:graphicImage value="/images/folder_open.gif" border="0"
														rendered="#{t.nodeExpanded}" />
													<t:graphicImage value="/images/folder_closed.gif"
														border="0" rendered="#{!t.nodeExpanded}" />
													<t:outputText value="#{node.description}"
														style="color:#FFFFFF; background-color:#000000"
														rendered="#{bulkOperationsCatalog.current == node.identifier}" />
													<t:outputText value="#{node.description}"
														rendered="#{bulkOperationsCatalog.current != node.identifier}" />
													<t:updateActionListener
														property="#{bulkOperationsCatalog.selectedTreeNode}"
														value="#{node}" />
													<t:updateActionListener
														property="#{bulkOperationsCatalog.current}"
														value="#{node.identifier}" />
												</af:commandMenuItem>
											</h:panelGroup>
										</f:facet>
										<f:facet name="group">
											<h:panelGroup>
												<af:commandMenuItem immediate="true">
													<t:graphicImage value="/images/folder_open.gif" border="0"
														rendered="#{t.nodeExpanded}" />
													<t:graphicImage value="/images/folder_closed.gif"
														border="0" rendered="#{!t.nodeExpanded}" />
													<t:outputText value="#{node.description}"
														style="color:#FFFFFF; background-color:#000000"
														rendered="#{bulkOperationsCatalog.current == node.identifier}" />
													<t:outputText value="#{node.description}"
														rendered="#{bulkOperationsCatalog.current != node.identifier}" />
													<t:updateActionListener
														property="#{bulkOperationsCatalog.selectedTreeNode}"
														value="#{node}" />
													<t:updateActionListener
														property="#{bulkOperationsCatalog.current}"
														value="#{node.identifier}" />
												</af:commandMenuItem>
											</h:panelGroup>
										</f:facet>
										<f:facet name="bulkOperation">
											<h:panelGroup>
												<af:commandMenuItem immediate="true">
													<t:graphicImage value="/images/definitions.gif" border="0" />
													<t:outputText value="#{node.description}"
														style="color:#FFFFFF; background-color:#000000"
														rendered="#{bulkOperationsCatalog.current == node.identifier}" />
													<t:outputText value="#{node.description}"
														rendered="#{bulkOperationsCatalog.current != node.identifier}" />
													<t:updateActionListener
														property="#{bulkOperationsCatalog.selectedTreeNode}"
														value="#{node}" />
													<t:updateActionListener
														property="#{bulkOperationsCatalog.current}"
														value="#{node.identifier}" />
												</af:commandMenuItem>
											</h:panelGroup>
										</f:facet>
									</t:tree2>
								</h:panelGrid>
							</f:facet>

							<f:facet name="innerLeft">
								<h:panelGrid width="1000">
									<af:panelGroup layout="vertical"
										rendered="#{bulkOperationsCatalog.selectedTreeNode.type == 'root' }">
										<af:panelHeader text="Bulk Operations Definition" />
									</af:panelGroup>

									<af:panelGroup layout="vertical"
										rendered="#{bulkOperationsCatalog.selectedTreeNode.type == 'group' }">
										<af:panelHeader text="Bulk Operation Definition" />
									</af:panelGroup>

									<af:panelGroup layout="vertical"
										rendered="#{bulkOperationsCatalog.selectedTreeNode.type == 'bulkOperation' }">

										<af:objectSpacer height="1" width="100%" />

										<af:selectOneChoice label="Select Facility Role To Change"
											value="#{bulkOperationsCatalog.facilityRole}"
											rendered="#{bulkOperationsCatalog.facilityRoleEnabled && !bulkOperationsCatalog.facilityRolesOnly}"
											required="true">
											<f:selectItems value="#{bulkOperationsCatalog.facilityRoles}" />
										</af:selectOneChoice>
										<af:selectOneChoice label="Select Facility Role To Change"
											value="#{bulkOperationsCatalog.facilityRole}"
											rendered="#{bulkOperationsCatalog.facilityRoleEnabled && bulkOperationsCatalog.facilityRolesOnly}"
											required="true">
											<f:selectItems
												value="#{bulkOperationsCatalog.facilityOnlyRoles}" />
										</af:selectOneChoice>

										<af:selectOneChoice label="Select staff member to reassign tasks from: "
											value="#{bulkOperationsCatalog.staffId}"
											rendered="#{bulkOperationsCatalog.staffIdEnabled}"
											required="true">
											<f:selectItems
												value="#{infraDefs.basicUsersDef.items[(empty bulkOperationsCatalog.staffId?0:bulkOperationsCatalog.staffId)]}" />
										</af:selectOneChoice>
										
										<af:selectOneChoice label="Select staff member to reassign roles from: "
											value="#{bulkOperationsCatalog.staffId}"
											rendered="#{bulkOperationsCatalog.facilityRoleStaffIdEnabled}"
											required="true">
											<f:selectItems
												value="#{infraDefs.basicUsersDef.items[(empty bulkOperationsCatalog.staffId?0:bulkOperationsCatalog.staffId)]}" />
										</af:selectOneChoice>										

										<af:selectOneChoice label="Select name of task(s) to reassign: "
											rendered="#{bulkOperationsCatalog.taskNameEnabled}"
											required="true" value="#{bulkOperationsCatalog.taskName}">
											<f:selectItems value="#{workFlowDefs.activityTypes}" />
										</af:selectOneChoice>

										<af:panelHeader text="Facility Search Criteria"
											rendered="#{bulkOperationsCatalog.bulkFacilitySearch}" />
										<af:panelForm
											rendered="#{bulkOperationsCatalog.bulkFacilitySearch}">

											<af:panelForm rows="2" width="100%" maxColumns="3">

												<af:inputText label="Facility ID"
													tip="0000000000, 0%, %0%, *0*, 0*"
													value="#{bulkOperationsCatalog.facilityId}"
													rendered="#{bulkOperationsCatalog.facilityIdEnabled}" />

												<af:inputText label="Facility Name"
													tip="acme%, %acme% *acme*, acme*"
													value="#{bulkOperationsCatalog.facilityNm}"
													rendered="#{bulkOperationsCatalog.facilityNmEnabled}" />

												<af:selectOneChoice label="Company"
													value="#{bulkOperationsCatalog.companyName}"
													rendered="#{bulkOperationsCatalog.companyNameEnabled}"
													unselectedLabel="">
													<f:selectItems value="#{companySearch.allCompanies}" />
												</af:selectOneChoice>

												<af:inputText label="Physical Location 1"
													tip="street%, %street%, *street*, street*"
													rendered="#{bulkOperationsCatalog.facilityAddress1Enabled}"
													value="#{bulkOperationsCatalog.address1}" />

												<af:selectOneChoice label="County"
													value="#{bulkOperationsCatalog.county}"
													rendered="#{bulkOperationsCatalog.countyEnabled}" unselectedLabel="">
													<f:selectItems value="#{infraDefs.counties}" />
												</af:selectOneChoice>

												<af:selectOneChoice label="District"
													value="#{bulkOperationsCatalog.doLaa}"
													rendered="#{bulkOperationsCatalog.doLaaEnabled}" unselectedLabel="">
													<f:selectItems value="#{infraDefs.districts}" />
												</af:selectOneChoice>

												<af:selectOneChoice label="Facility Class"
													value="#{bulkOperationsCatalog.permitClassCd}"
													rendered="#{bulkOperationsCatalog.facilityClassEnabled}"
													unselectedLabel="">
													<f:selectItems
														value="#{facilityReference.permitClassDefs.items[0]}" />
												</af:selectOneChoice>

												<af:selectOneChoice label="Facility Type"
													value="#{bulkOperationsCatalog.facilityTypeCd}"
													rendered="#{bulkOperationsCatalog.facilityTypeEnabled}"
													unselectedLabel="" styleClass="FacilityTypeClass x6">
													<f:selectItems
														value="#{facilityReference.facilityTypeDefs.items[0]}" />
												</af:selectOneChoice>

												<af:selectOneChoice label="Operating Status"
													unselectedLabel=""
													rendered="#{bulkOperationsCatalog.facilityOpStatusEnabled}"
													value="#{bulkOperationsCatalog.operatingStatusCd}">
													<f:selectItems
														value="#{facilityReference.operatingStatusDefs.items[0]}" />
												</af:selectOneChoice>

												<af:inputText label="Facility Description"
													value="#{bulkOperationsCatalog.facilityDesc}"
													rendered="#{bulkOperationsCatalog.facilityDescEnabled}" />

												<af:inputText label="City"
													value="#{bulkOperationsCatalog.city}"
													rendered="#{bulkOperationsCatalog.cityEnabled}" />


												<af:inputText label="Zip Code"
													value="#{bulkOperationsCatalog.zipCode}"
													rendered="#{bulkOperationsCatalog.zipCodeEnabled}" />

												<af:selectOneChoice label="Permitting Classification"
													value="#{bulkOperationsCatalog.permittingClassCd}"
													rendered="#{bulkOperationsCatalog.permittingClassCdEnabled}">
													<f:selectItems
														value="#{bulkOperationsCatalog.permittingClassCds}" />
												</af:selectOneChoice>

												<af:selectOneChoice label="Permitting Issuance Stage"
													value="#{bulkOperationsCatalog.issuanceStageCd}"
													rendered="#{bulkOperationsCatalog.issuanceStageCdEnabled}">
													<f:selectItems
														value="#{bulkOperationsCatalog.issuanceStageCds}" />
												</af:selectOneChoice>

												<af:selectOneChoice label="Permitting Issuance Status"
													value="#{bulkOperationsCatalog.globalStatusCd}"
													rendered="#{bulkOperationsCatalog.globalStatusCdEnabled}">
													<f:selectItems
														value="#{bulkOperationsCatalog.globalStatusCds}" />
												</af:selectOneChoice>

												<af:selectOneChoice label="Reporting Category"
													value="#{bulkOperationsCatalog.reportCategoryCd}"
													rendered="#{bulkOperationsCatalog.reportCategoryEnabled}">
													<f:selectItems
														value="#{bulkOperationsCatalog.reportCategoryCds}" />
												</af:selectOneChoice>

												<af:selectOneChoice label="TV Permitting Status"
													value="#{bulkOperationsCatalog.tvPermitStatus}"
													rendered="#{bulkOperationsCatalog.tvPermitStatusEnabled}">
													<f:selectItems
														value="#{bulkOperationsCatalog.reportCategoryCds}" />
												</af:selectOneChoice>

												<af:selectInputDate label="Start Date"
													value="#{bulkOperationsCatalog.startDate}"
													rendered="#{bulkOperationsCatalog.startDateEnabled}">
													<af:validateDateTimeRange minimum="1900-01-01" />
												</af:selectInputDate>

												<af:selectInputDate label="End Date"
													value="#{bulkOperationsCatalog.endDate}"
													rendered="#{bulkOperationsCatalog.endDateEnabled}">
													<af:validateDateTimeRange minimum="1900-01-01" />
												</af:selectInputDate>

												<af:selectInputDate label="Install Start Date"
													value="#{bulkOperationsCatalog.startDate}"
													rendered="#{bulkOperationsCatalog.installStartDateEnabled}">
													<af:validateDateTimeRange minimum="1900-01-01" />
												</af:selectInputDate>

												<af:selectInputDate label="Install End Date"
													value="#{bulkOperationsCatalog.endDate}"
													rendered="#{bulkOperationsCatalog.installEndDateEnabled}">
													<af:validateDateTimeRange minimum="1900-01-01" />
												</af:selectInputDate>

												<af:selectInputDate label="Issuance Start Date"
													value="#{bulkOperationsCatalog.startDate}"
													rendered="#{bulkOperationsCatalog.issuanceStartDateEnabled}">
													<af:validateDateTimeRange minimum="1900-01-01" />
												</af:selectInputDate>

												<af:selectInputDate label="Issuance End Date"
													value="#{bulkOperationsCatalog.endDate}"
													rendered="#{bulkOperationsCatalog.issuanceEndDateEnabled}">
													<af:validateDateTimeRange minimum="1900-01-01" />
												</af:selectInputDate>

												<af:selectInputDate label="Effective Start Date"
													value="#{bulkOperationsCatalog.startDate}"
													rendered="#{bulkOperationsCatalog.effectiveStartDateEnabled}">
													<af:validateDateTimeRange minimum="1900-01-01" />
												</af:selectInputDate>

												<af:selectInputDate label="Effective End Date"
													value="#{bulkOperationsCatalog.endDate}"
													rendered="#{bulkOperationsCatalog.effectiveEndDateEnabled}">
													<af:validateDateTimeRange minimum="1900-01-01" />
												</af:selectInputDate>

												<af:selectInputDate label="Balance Start Date"
													value="#{bulkOperationsCatalog.startDate}"
													rendered="#{bulkOperationsCatalog.balanceStartDateEnabled}">
													<af:validateDateTimeRange minimum="1900-01-01" />
												</af:selectInputDate>

												<af:selectInputDate label="Balance End Date"
													value="#{bulkOperationsCatalog.endDate}"
													rendered="#{bulkOperationsCatalog.balanceEndDateEnabled}">
													<af:validateDateTimeRange minimum="1900-01-01" />
												</af:selectInputDate>

												<af:selectOneChoice label="Operating Status"
													value="#{bulkOperationsCatalog.operatingStatus}"
													rendered="#{bulkOperationsCatalog.operatingStatusEnabled}">
													<f:selectItems
														value="#{bulkOperationsCatalog.operatingStatuses}" />
												</af:selectOneChoice>

												<af:selectOneChoice label="SIC Codes"
													value="#{bulkOperationsCatalog.sicCd}"
													rendered="#{bulkOperationsCatalog.sicCdEnabled}">
													<f:selectItems value="#{bulkOperationsCatalog.sicCds}" />
												</af:selectOneChoice>

												<af:selectOneChoice label="NAICS Codes"
													value="#{bulkOperationsCatalog.naicsCd}"
													rendered="#{bulkOperationsCatalog.naicsCdEnabled}">
													<f:selectItems value="#{bulkOperationsCatalog.naicsCds}" />
												</af:selectOneChoice>

												<af:selectBooleanCheckbox label="In Attainment"
													value="#{bulkOperationsCatalog.inAttainment}"
													rendered="#{bulkOperationsCatalog.inAttainmentEnabled}">
													<f:selectItems
														value="#{bulkOperationsCatalog.inAttainment}" />
												</af:selectBooleanCheckbox>

												<af:selectBooleanCheckbox label="Portable"
													value="#{bulkOperationsCatalog.portable}"
													rendered="#{bulkOperationsCatalog.portableEnabled}">
													<f:selectItems value="#{bulkOperationsCatalog.portable}" />
												</af:selectBooleanCheckbox>

												<af:selectBooleanCheckbox label="NESHAP Indicator"
													value="#{bulkOperationsCatalog.neshapsInd}"
													rendered="#{bulkOperationsCatalog.neshapsIndEnabled}">
													<f:selectItems value="#{bulkOperationsCatalog.neshapsInd}" />
												</af:selectBooleanCheckbox>

												<af:selectOneChoice label="NESAPS Subparts"
													value="#{bulkOperationsCatalog.neshapsSubpart}"
													rendered="#{bulkOperationsCatalog.neshapsSubpartEnabled}">
													<f:selectItems
														value="#{bulkOperationsCatalog.neshapsSubparts}" />
												</af:selectOneChoice>

												<af:selectBooleanCheckbox label="NSPS Indicator"
													value="#{bulkOperationsCatalog.nspsInd}"
													rendered="#{bulkOperationsCatalog.nspsIndEnabled}">
													<f:selectItems value="#{bulkOperationsCatalog.nspsInd}" />
												</af:selectBooleanCheckbox>

												<af:selectOneChoice label="NSPS Subparts"
													value="#{bulkOperationsCatalog.nspsSubpart}"
													rendered="#{bulkOperationsCatalog.nspsSubpartEnabled}">
													<f:selectItems
														value="#{bulkOperationsCatalog.nspsSubparts}" />
												</af:selectOneChoice>

												<af:selectOneChoice label="Pollutants"
													value="#{bulkOperationsCatalog.pollutant}"
													rendered="#{bulkOperationsCatalog.pollutantEnabled}">
													<f:selectItems value="#{bulkOperationsCatalog.pollutants}" />
												</af:selectOneChoice>

												<af:selectBooleanCheckbox label="MACT Indicator"
													value="#{bulkOperationsCatalog.mactInd}"
													rendered="#{bulkOperationsCatalog.mactIndEnabled}">
													<f:selectItems value="#{bulkOperationsCatalog.mactInd}" />
												</af:selectBooleanCheckbox>

												<af:selectOneChoice label="MACT Codes"
													value="#{bulkOperationsCatalog.mactCd}"
													rendered="#{bulkOperationsCatalog.mactCdEnabled}">
													<f:selectItems value="#{bulkOperationsCatalog.mactCds}" />
												</af:selectOneChoice>

												<af:selectOneChoice label="MACT Subparts"
													value="#{bulkOperationsCatalog.mactSubpart}"
													rendered="#{bulkOperationsCatalog.mactSubpartEnabled}">
													<f:selectItems
														value="#{bulkOperationsCatalog.mactSubparts}" />
												</af:selectOneChoice>

												<af:selectOneChoice label="Transit Statuses"
													value="#{bulkOperationsCatalog.transitStatus}"
													rendered="#{bulkOperationsCatalog.transitStatusEnabled}">
													<f:selectItems
														value="#{bulkOperationsCatalog.transitStatuses}" />
												</af:selectOneChoice>

												<af:selectOneChoice label="Portable Group Types"
													value="#{bulkOperationsCatalog.portableGroupType}"
													rendered="#{bulkOperationsCatalog.portableGroupTypeEnabled}">
													<f:selectItems
														value="#{bulkOperationsCatalog.portableGroupTypes}" />
												</af:selectOneChoice>

												<af:selectOneChoice label="Portable Group Names"
													value="#{bulkOperationsCatalog.portableGroupName}"
													rendered="#{bulkOperationsCatalog.portableGroupNameEnabled}">
													<f:selectItems
														value="#{bulkOperationsCatalog.portableGroupNames}" />
												</af:selectOneChoice>

												<af:inputText label="EU Description"
													value="#{bulkOperationsCatalog.euDesc}"
													rendered="#{bulkOperationsCatalog.euDescEnabled}" />

												<af:selectOneChoice label="Release Point Type Codes"
													value="#{bulkOperationsCatalog.egressPointTypeCd}"
													rendered="#{bulkOperationsCatalog.egressPointTypeEnabled}">
													<f:selectItems
														value="#{bulkOperationsCatalog.egressPointTypes}" />
												</af:selectOneChoice>

												<af:selectInputDate label="Correspondence Sent Date"
													value="#{bulkOperationsCatalog.correspondenceDate}"
													rendered="#{bulkOperationsCatalog.correspondenceDateEnabled}" />

											</af:panelForm>


											<af:objectSpacer height="15" width="100%" />
											<afh:rowLayout halign="center" partialTriggers="progressPoll">
												<af:panelButtonBar>
													<af:objectSpacer width="100%" height="20" />
													<af:commandButton text="Submit Search"
														action="#{bulkOperationsCatalog.submitFacilitySearch}"
														rendered="#{! bulkOperationsCatalog.syncSearchEnabled && !bulkOperationsCatalog.notReset}" />
													<af:commandButton text="Stop Search"
														action="#{bulkOperationsCatalog.refineSearch}"
														rendered="#{! bulkOperationsCatalog.syncSearchEnabled && bulkOperationsCatalog.notReset}" />
													<af:commandButton text="Submit Search"
														disabled="#{bulkOperationsCatalog.notReset}"
														action="#{bulkOperationsCatalog.submitSyncFacilitySearch}"
														rendered="#{bulkOperationsCatalog.syncSearchEnabled}" />
													<af:commandButton text="Reset"
														action="#{bulkOperationsCatalog.reset}" />
												</af:panelButtonBar>
											</afh:rowLayout>
										</af:panelForm>

										<af:panelHeader text="Permit Search Criteria"
											rendered="#{bulkOperationsCatalog.bulkPermitSearch}" />
										<af:panelForm
											rendered="#{bulkOperationsCatalog.bulkPermitSearch}">

											<af:inputText label="Facility ID"
												value="#{bulkOperationsCatalog.facilityId}"
												rendered="#{bulkOperationsCatalog.facilityIdEnabled}" />

											<af:inputText label="Facility Name"
												value="#{bulkOperationsCatalog.facilityNm}"
												rendered="#{bulkOperationsCatalog.facilityNmEnabled}" />

											<af:inputText label="Application Number"
												value="#{bulkOperationsCatalog.applicationNumber}"
												rendered="#{bulkOperationsCatalog.applicationNumberEnabled}" />

											<af:inputText label="Permit Number"
												value="#{bulkOperationsCatalog.permitNumber}"
												rendered="#{bulkOperationsCatalog.permitNumberEnabled}" />

											<af:inputText label="Permit Type"
												value="#{bulkOperationsCatalog.permitType}"
												rendered="#{bulkOperationsCatalog.permitTypeEnabled}" />

											<af:inputText label="Permit Reason"
												value="#{bulkOperationsCatalog.permitReason}"
												rendered="#{bulkOperationsCatalog.permitReasonEnabled}" />

											<af:inputText label="Permit Status Code"
												value="#{bulkOperationsCatalog.permitStatusCd}"
												rendered="#{bulkOperationsCatalog.permitStatusCdEnabled}" />

											<af:objectSpacer height="15" width="100%" />

											<afh:rowLayout halign="center">
												<af:objectSpacer height="20" width="100%" />
												<af:panelButtonBar>
													<af:commandButton text="Submit Search"
														disabled="#{bulkOperationsCatalog.notReset}"
														action="#{bulkOperationsCatalog.submitPermitSearch}" />
													<af:commandButton text="Reset"
														action="#{bulkOperationsCatalog.reset}" />
													<af:commandButton text="Refine Search"
														action="#{bulkOperationsCatalog.refineSearch}" />
												</af:panelButtonBar>
											</afh:rowLayout>
										</af:panelForm>

										<af:panelHeader text="Correspondence Search Criteria"
											rendered="#{bulkOperationsCatalog.bulkCorrespondenceSearch}" />
										<af:panelForm
											rendered="#{bulkOperationsCatalog.bulkCorrespondenceSearch}">

											<af:inputText label="Facility ID"
												value="#{bulkOperationsCatalog.facilityId}"
												rendered="#{bulkOperationsCatalog.facilityIdEnabled}" />

											<af:inputText label="Facility Name"
												value="#{bulkOperationsCatalog.facilityNm}"
												rendered="#{bulkOperationsCatalog.facilityNmEnabled}" />

											<af:selectOneChoice label="District"
												value="#{bulkOperationsCatalog.doLaa}"
												rendered="#{bulkOperationsCatalog.doLaaEnabled}">
												<f:selectItems value="#{bulkOperationsCatalog.doLaas}" />
											</af:selectOneChoice>

											<af:selectOneChoice label="County"
												value="#{bulkOperationsCatalog.county}"
												rendered="#{bulkOperationsCatalog.countyEnabled}">
												<f:selectItems value="#{infraDefs.counties}" />
											</af:selectOneChoice>

											<af:objectSpacer height="15" width="100%" />

											<afh:rowLayout halign="center">
												<af:objectSpacer height="20" width="100%" />
												<af:panelButtonBar>
													<af:commandButton text="Submit Search"
														disabled="#{bulkOperationsCatalog.notReset}"
														action="#{bulkOperationsCatalog.submitCorrespondenceSearch}" />
													<af:commandButton text="Reset"
														action="#{bulkOperationsCatalog.reset}" />
													<af:commandButton text="Refine Search"
														action="#{bulkOperationsCatalog.refineSearch}" />
												</af:panelButtonBar>
											</afh:rowLayout>
										</af:panelForm>


										<af:panelHeader text="Contact Type Search Criteria"
											rendered="#{bulkOperationsCatalog.bulkContactTypeSearch}" />
										<af:panelForm
											rendered="#{bulkOperationsCatalog.bulkContactTypeSearch}">
											<af:selectOneChoice
												value="#{bulkOperationsCatalog.contactId}"
												rendered="#{bulkOperationsCatalog.contactIdEnabled}"
												label="Contact: " showRequired="true" id="contactId">
												<f:selectItems value="#{bulkOperationsCatalog.contactsList}" />
											</af:selectOneChoice>

											<af:objectSpacer height="15" width="100%" />

											<afh:rowLayout halign="center">
												<af:objectSpacer height="20" width="100%" />
												<af:panelButtonBar>
													<af:commandButton text="Submit Search"
														disabled="#{bulkOperationsCatalog.notReset}"
														action="#{bulkOperationsCatalog.submitContactTypeSearch}" />
													<af:commandButton text="Reset"
														action="#{bulkOperationsCatalog.reset}" />
												</af:panelButtonBar>
											</afh:rowLayout>
										</af:panelForm>


									</af:panelGroup>
								</h:panelGrid>
							</f:facet>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
				<af:objectSpacer width="100%" height="15" />

				<af:panelGroup layout="vertical" partialTriggers="progressPoll">
					<jsp:include flush="true" page="bulkFacilitySearchTable.jsp" />

					<jsp:include flush="true" page="bulkFacilitySyncSearchTable.jsp" />

					<jsp:include flush="true" page="bulkCorrespondenceSearchTable.jsp" />

					<jsp:include flush="true" page="bulkContactTypeSearchTable.jsp" />
				</af:panelGroup>


			</af:page>
		</af:form>
		<f:verbatim><%@ include
				file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
		<f:verbatim><%@ include
				file="../scripts/FacilityType-Option.js"%></f:verbatim>
	</af:document>
</f:view>

