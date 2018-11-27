<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Advanced Permit Workflow Search">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Advanced Permit Workflow Search">
				<jsp:include flush="true" page="../util/header.jsp" />
				<mu:setProperty property="#{advancedPermitWorkflowSearch.by}"
					value="#{param.by}" />
				<mu:setProperty property="#{advancedPermitWorkflowSearch.section}"
					value="#{param.section}" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000">
						<af:panelBorder>
							<af:showDetailHeader text="Search Criteria" disclosed="true">
								<af:panelForm rows="5" maxColumns="10" width="200">
									<af:inputText columns="10" maximumLength="10"
										label="Facility ID"
										value="#{advancedPermitWorkflowSearch.facilityId}"
										tip="0000000000, 0%, %0%, *0*, 0*"
										id="advPermitWorkflowSearch_FacilityID" />
									<af:inputText columns="25" label="Facility Name"
										value="#{advancedPermitWorkflowSearch.facilityNm}"
										tip="acme%, %acme%, *acme*, acme*"
										id="advPermitWorkflowSearch_FacilityName" />
									<af:inputText tip="P0000002, P000000%, *0*, P*, *2"
										columns="10" label="Permit Number"
										value="#{advancedPermitWorkflowSearch.permitNumber}"
										id="advPermitWorkflowSearch_PermitNumber" />
									<af:inputText tip="A0000504, A%, %50%, *50*, A*"
										label="Application Number" columns="10" rows="1"
										value="#{advancedPermitWorkflowSearch.applicationNumber}"
										maximumLength="8"
										id="advPermitWorkflowSearch_ApplicationNumber" />
									<af:inputText tip="" label="Workflow Id" columns="10" rows="1"
										value="#{advancedPermitWorkflowSearch.processId}"
										maximumLength="8"
										id="advPermitWorkflowSearch_WorkflowID" />
									<af:selectOneChoice label="Staff" unselectedLabel=" "
										id="advPermitWorkflowSearch_Staff" 
										value="#{advancedPermitWorkflowSearch.userId}">
										<f:selectItems value="#{infraDefs.basicUsersDef.allItems}"/>
									</af:selectOneChoice>
									<af:selectOneChoice label="Permit Type" tip=""
										unselectedLabel=" "
										value="#{advancedPermitWorkflowSearch.permitType}"
										id="advPermitWorkflowSearch_PermitType" >
										<mu:selectItems
											value="#{advancedPermitWorkflowSearch.permitTypes}"/>
									</af:selectOneChoice>
									<af:selectOneChoice label="Task Name" tip=" "
										unselectedLabel=" "
										value="#{advancedPermitWorkflowSearch.activityNm}"
										id="advPermitWorkflowSearch_TaskName" >
										<f:selectItems value="#{workFlowDefs.permittingActivityTypes}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="Task State" tip=" "
										unselectedLabel=" "
										value="#{advancedPermitWorkflowSearch.activityStatusCd}"
										id="advPermitWorkflowSearch_TaskState" >
										<f:selectItems value="#{workFlowDefs.activityStatus}" />
									</af:selectOneChoice>
									<af:selectBooleanCheckbox label="Exclude Skipped"
										value="#{advancedPermitWorkflowSearch.filterSkipped}"
										selected="true"
										id="advPermitWorkspaceSearch_ExcludeSkipped" />
									<af:selectBooleanCheckbox label="Exclude Non-Started "
										value="#{advancedPermitWorkflowSearch.filterNonStarted}"
										selected="true"
										id="advPermitWorkflowSearch_ExcludeNonStarted" />
									<af:selectInputDate label="Start Date From"
										value="#{advancedPermitWorkflowSearch.startDateFrom}"
										id="advPermitWorkflowSearch_StartDateFrom" >
										<af:validateDateTimeRange minimum="1900-01-01" />
									</af:selectInputDate>
									<af:selectInputDate label="To"
										value="#{advancedPermitWorkflowSearch.startDateTo}"
										id="advPermitWorkflowSearch_StartDateTo" >
										<af:validateDateTimeRange minimum="1900-01-01"/>
									</af:selectInputDate>
									<af:selectInputDate label="End Date From"
										value="#{advancedPermitWorkflowSearch.endDateFrom}"
										id="advPermitWorkflowSearch_EndDateFrom" >
										<af:validateDateTimeRange minimum="1900-01-01"/>
									</af:selectInputDate>
									<af:selectInputDate label="To"
										value="#{advancedPermitWorkflowSearch.endDateTo}"
										id="advPermitWorkflowSearch_EndDateTo" >
										<af:validateDateTimeRange minimum="1900-01-01"/>
									</af:selectInputDate>

								</af:panelForm>
								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Submit"
											action="#{advancedPermitWorkflowSearch.submit}"
											id="advPermitWorkflowSearch_SubmitBtn" />
										<af:commandButton text="Reset"
											action="#{advancedPermitWorkflowSearch.reset}"
											id="advPermitWorkflowSearch_ResetBtn" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1"
						rendered="#{advancedPermitWorkflowSearch.hasSearchResults}">
						<af:panelBorder>
							<af:showDetailHeader text="Permit Workflow Search Results"
								disclosed="true">
								<af:table bandingInterval="1" banding="row" var="searchResult"
									emptyText=" " rows="#{advancedPermitWorkflowSearch.pageLimit}"
									width="98%"
									value="#{advancedPermitWorkflowSearch.searchResults}">
									<af:column noWrap="true" sortProperty="processId"
										sortable="true"  width="60px">
										<f:facet name="header">
											<af:outputText value="Workflow Id" />
										</f:facet>
										<af:commandLink action="#{workFlow2DDraw.submitProfile}">
											<af:outputText value="#{searchResult.processId}" />
											<t:updateActionListener
												property="#{workFlow2DDraw.processId}"
												value="#{searchResult.processId}" />
											<t:updateActionListener
												property="#{menuItem_workflowProfile.disabled}"
												value="false" />
										</af:commandLink>
									</af:column>
									<af:column noWrap="true" headerText="Application"
										bandingShade="light">
										<af:column noWrap="true" sortProperty="applicationNbr"
											sortable="true">
											<f:facet name="header">
												<af:outputText value="Number" />
											</f:facet>
											<af:outputText value="#{searchResult.applicationNbr}"
												inlineStyle="margin-right: 0.5em;" />
										</af:column>
										<af:column noWrap="true"
											sortProperty="applicationReceivedDate" sortable="true" width="60px" >
											<f:facet name="header">
												<af:outputText value="Received Date" />
											</f:facet>
											<af:outputText
												value="#{searchResult.applicationReceivedDate}"
												inlineStyle="margin-right: 0.5em;">
												<f:convertDateTime type="date" dateStyle="short" />
											</af:outputText>
										</af:column>
									</af:column>
									<af:column noWrap="true" sortProperty="companyNm"
										sortable="true" inlineStyle="color: rgb(255,255,0)">
										<f:facet name="header">
											<af:outputText value="Company Name" />
										</f:facet>
										<af:outputText value="#{searchResult.companyNm}"
											inlineStyle="margin-right: 0.5em;" />
									</af:column>
									<af:column noWrap="true" headerText="Facility"
										bandingShade="light">
										<af:column noWrap="true" sortProperty="facilityId"
											sortable="true">
											<f:facet name="header">
												<af:outputText value="Id" />
											</f:facet>
											<af:commandLink text="#{searchResult.facilityId}"
												action="#{facilityProfile.submitProfile}"
												inlineStyle="margin-right: 0.5em;">
												<t:updateActionListener property="#{facilityProfile.fpId}"
													value="#{searchResult.fpId}" />
												<t:updateActionListener
													property="#{menuItem_facProfile.disabled}" value="false" />
											</af:commandLink>
										</af:column>
										<af:column noWrap="true" sortProperty="facilityNm"
											sortable="true" inlineStyle="color: rgb(255,255,0)">
											<f:facet name="header">
												<af:outputText value="Name" />
											</f:facet>
											<af:outputText value="#{searchResult.facilityNm}"
												inlineStyle="margin-right: 0.5em;" />
										</af:column>
									</af:column>
									<af:column noWrap="true" headerText="Permit"
										bandingShade="light">
										<af:column noWrap="true" sortProperty="permitNumber"
											sortable="true" headerText="Number">
											<af:outputText value="#{searchResult.permitNumber}"
												inlineStyle="margin-right: 0.5em;" />
										</af:column>
										<af:column noWrap="true" sortProperty="permitTypeCd"
											sortable="true" headerText="Type">
											<af:outputText value="#{searchResult.permitTypeCd}"
												inlineStyle="margin-right: 0.5em;" />
										</af:column>
									</af:column>
									<af:column noWrap="true" headerText="Task" bandingShade="light">
										<af:column noWrap="true" sortProperty="activityId"
											sortable="true" headerText="Id">
											<af:outputText value="#{searchResult.activityId}"
												inlineStyle="margin-right: 0.5em;" />
										</af:column>
										<af:column noWrap="true" sortProperty="activityNm"
											sortable="true" headerText="Name">
											<af:outputText value="#{searchResult.activityNm}"
												inlineStyle="margin-right: 0.5em;" />
										</af:column>
										<af:column noWrap="true" sortProperty="userLastNm"
											sortable="true">
											<f:facet name="header">
												<af:outputText value="Staff" />
											</f:facet>
											<af:outputText
												value="#{searchResult.userLastNm}, #{searchResult.userFirstNm}"
												inlineStyle="margin-right: 0.5em;" />
										</af:column>
										<af:column noWrap="true" sortProperty="activityStatusCd"
											sortable="true">
											<f:facet name="header">
												<af:outputText value="State" />
											</f:facet>
											<af:selectOneChoice label="State"
												value="#{searchResult.activityStatusCd}" readOnly="true"
												inlineStyle="margin-right: 0.5em;">
												<f:selectItems value="#{workFlowDefs.activityStatus}" />
											</af:selectOneChoice>
										</af:column>
										<af:column noWrap="true" sortProperty="startDate"
											sortable="true">
											<f:facet name="header">
												<af:outputText value="Start Date" />
											</f:facet>
											<af:outputText value="#{searchResult.startDate}"
												inlineStyle="margin-right: 0.5em;">
												<f:convertDateTime type="date" dateStyle="short" />
											</af:outputText>
										</af:column>
										<af:column noWrap="true" sortProperty="endDate"
											sortable="true">
											<f:facet name="header">
												<af:outputText value="End Date" />
											</f:facet>
											<af:outputText value="#{searchResult.endDate}"
												inlineStyle="margin-right: 0.5em;">
												<f:convertDateTime type="date" dateStyle="short" />
											</af:outputText>
										</af:column>
										<af:column noWrap="true" headerText="Days"
											bandingShade="light">
											<af:column noWrap="true" sortProperty="agencyDays"
												sortable="true" formatType="number">
												<f:facet name="header">
													<af:outputText value="AQD" />
												</f:facet>
												<af:outputText value="#{searchResult.agencyDays}" />
											</af:column>
											<af:column noWrap="true" sortProperty="nonAgencyDays"
												sortable="true" formatType="number">
												<f:facet name="header">
													<af:outputText value="Non-AQD" />
												</f:facet>
												<af:outputText value="#{searchResult.nonAgencyDays}" />
											</af:column>
										</af:column>
									</af:column>
									<f:facet name="footer">
										<afh:rowLayout halign="center">
											<af:panelButtonBar>
												<af:commandButton
													actionListener="#{tableExporter.printTable}"
													onclick="#{tableExporter.onClickScript}"
													text="Printable view" />
												<af:commandButton
													actionListener="#{tableExporter.excelTable}"
													onclick="#{tableExporter.onClickScript}"
													text="Export to excel" />
											</af:panelButtonBar>
										</afh:rowLayout>
									</f:facet>
								</af:table>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1"
						rendered="#{advancedPermitWorkflowSearch.hasSearchResults}">
						<af:panelBorder>
							<af:showDetailHeader
								text="Permit Workflow Search Results Analysis" disclosed="true">
								<af:table value="#{advancedPermitWorkflowSearch.searchAnalyses}"
									bandingInterval="2" banding="row" var="analysis">
									<af:column>
										<af:table value="#{analysis.benchmarksArray}"
											bandingInterval="2" banding="row" var="benchmark"
											width="100%">
											<af:column headerText="#{analysis.activityName}">
												<af:column gridVisible="false">
													<af:column gridVisible="false">
														<af:outputText value="#{benchmark.predicateDescription}" />
													</af:column>
													<af:column gridVisible="false" headerText="Days"
														formatType="number">
														<af:outputText value="#{benchmark.value}" />
													</af:column>
												</af:column>
												<af:column formatType="number">
													<f:facet name="header">
														<af:outputText value="Applications" />
													</f:facet>
													<af:outputText value="#{benchmark.applicationCount}" />
												</af:column>
												<af:column formatType="number">
													<f:facet name="header">
														<af:outputText value="Percentage" />
													</f:facet>
													<af:outputText
														value="#{benchmark.applicationCount / analysis.applicationCount}"
														inlineStyle="font-weight:bold;">
														<af:convertNumber type="percent" />
													</af:outputText>
												</af:column>
											</af:column>
										</af:table>
										<af:outputText
											value="Total Applications: #{analysis.applicationCount}" />
										<af:objectSpacer width="100%" height="8" />
									</af:column>
								</af:table>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
