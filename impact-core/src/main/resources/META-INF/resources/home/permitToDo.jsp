<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document title="Permit To Dos">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Permit To Dos">
				<%@ include file="../util/header.jsp"%>

				<mu:setProperty property="#{permitToDoSearch.showFacility}"
					value="true" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1100">
						<af:panelBorder>
							<af:showDetailHeader text="Search Criteria" disclosed="true">
								<af:panelForm rows="6" maxColumns="3" partialTriggers="pt">

									<af:inputText columns="25" label="Facility ID"
										tip="0000000000, 0%, %0%, *0*, 0*"
										value="#{permitToDoSearch.pa.facilityId}" />
									<af:inputText columns="25" label="Facility Name"
										tip="acme%, %acme%, *acme*, acme*"
										value="#{permitToDoSearch.pa.facilityNm}" />
									<af:selectOneChoice label="District" rendered="#{infraDefs.districtVisible}"
										value="#{permitToDoSearch.pa.doLaaCd}" unselectedLabel="">
										<f:selectItems value="#{facilitySearch.doLaas}" />
									</af:selectOneChoice>
									<%-- This is to make the page Layout unchanged with District not rendered --%>
									<af:inputHidden id="districtHidden" rendered="#{!infraDefs.districtVisible}"/>
									
									<af:selectOneChoice label="Task Name" tip=" "
										unselectedLabel=" "
										value="#{permitToDoSearch.pa.activityTemplateNm}">
										<f:selectItems value="#{permitToDoSearch.activityTypes}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="Task State" tip=" "
										unselectedLabel=" "
										value="#{permitToDoSearch.pa.activityStatusCd}">
										<f:selectItems value="#{workFlowDefs.activityStatus}" />
									</af:selectOneChoice>
									<af:outputLabel value=" " />
									<af:selectOneChoice label="Staff" tip=" " unselectedLabel=" "
										value="#{permitToDoSearch.pa.userId}">
										<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
									</af:selectOneChoice>

									<af:selectOneChoice label="Workflow Name" unselectedLabel=" "
										value="#{permitToDoSearch.pa.processTemplateNm}">
										<f:selectItems value="#{permitToDoSearch.workflows}" />
									</af:selectOneChoice>
									<af:inputText columns="10" label="Permit Number"
										value="#{permitToDoSearch.pa.permitNumber}" />
									<af:selectOneChoice label="Permit Type" id="pt" tip=""
										autoSubmit="TRUE" unselectedLabel=" "
										value="#{permitToDoSearch.pa.permitTypeCd}">
										<mu:selectItems value="#{permitReference.permitTypes}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="Permit Reason" unselectedLabel=" "
										value="#{permitToDoSearch.pa.permitReason}">
										<f:selectItems value="#{permitToDoSearch.permitReasons}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="Permit Publication Stage"
										unselectedLabel=" "
										value="#{permitToDoSearch.pa.permitGlobalStatusCd}"
										readOnly="false">
										<mu:selectItems
											value="#{permitToDoSearch.permitGlobalStatusDefs}" />
									</af:selectOneChoice>

									<af:inputText tip="A0000504, A%, %50% *50*, A*"
										label="Application number :" columns="10"
										value="#{permitToDoSearch.pa.applicationNumber}"
										maximumLength="8" />
									<af:selectOneChoice label="Date Field" id="df"
										autoSubmit="true" unselectedLabel=" "
										value="#{permitToDoSearch.pa.dateBy}" readOnly="false">
										<mu:selectItems value="#{permitToDoSearch.permitDateBy}" />
									</af:selectOneChoice>
									<af:selectInputDate label="After"
										readOnly="#{permitToDoSearch.pa.dateBy == null}"
										value="#{permitToDoSearch.pa.beginDt}" partialTriggers="df">
										<af:validateDateTimeRange minimum="1900-01-01" />
									</af:selectInputDate>
									<af:selectInputDate label="Before"
										readOnly="#{permitToDoSearch.pa.dateBy == null}"
										value="#{permitToDoSearch.pa.endDt}" partialTriggers="df">
										<af:validateDateTimeRange minimum="1900-01-01" />
									</af:selectInputDate>
									<af:selectBooleanCheckbox label="Looped"
										value="#{permitToDoSearch.pa.looped}" />
								</af:panelForm>
								<af:panelForm>
									<af:selectOneChoice label="NAICS"
										value="#{permitToDoSearch.pa.naicsCd}" unselectedLabel="">
										<f:selectItems value="#{infraDefs.naicsSelectAllDefs}" />
									</af:selectOneChoice>
								</af:panelForm>
								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Submit"
											action="#{permitToDoSearch.submit}" />
										<af:commandButton text="Reset"
											action="#{permitToDoSearch.reset}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="2000"
						rendered="#{permitToDoSearch.hasSearchResults}">
						<af:panelBorder>
							<af:showDetailHeader text="To Do List" disclosed="true">
								<af:table bandingInterval="1" banding="row" var="todos"
									emptyText=' ' rows="#{permitToDoSearch.pageLimit}" width="2000"
									value="#{permitToDoSearch.shortActivities}">

									<af:column sortProperty="loopCnt" sortable="true"
										headerText="Task ID" formatType="text">
										<af:commandLink action="#{activityProfile.submitProfile}">
											<af:outputText
												inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}"
												value="#{todos.processId}-#{todos.activityTemplateId}-#{todos.loopCnt}" />
											<t:updateActionListener
												property="#{menuItem_activityProfile.disabled}"
												value="false" />
											<t:updateActionListener
												property="#{activityProfile.processId}"
												value="#{todos.processId}" />
											<t:updateActionListener
												property="#{workFlow2DDraw.processId}"
												value="#{todos.processId}" />
											<t:updateActionListener property="#{activityProfile.loopCnt}"
												value="#{todos.loopCnt}" />
											<t:updateActionListener
												property="#{activityProfile.activityTemplateId}"
												value="#{todos.activityTemplateId}" />
											<t:updateActionListener
												property="#{activityProfile.fromExternal}"
												value="#{permitToDoSearch.fromExternal}" />
											<t:updateActionListener
												property="#{activityProfile.aggregate}"
												value="#{todos.aggregate}" />
											<t:updateActionListener
												property="#{menuItem_workflowProfile.disabled}"
												value="false" />
										</af:commandLink>
									</af:column>

									<af:column sortProperty="facilityId" sortable="true"
										noWrap="true" rendered="#{permitToDoSearch.showFacility}"
										headerText="Facility ID" formatType="text">
										<af:commandLink text="#{todos.facilityId}"
											action="#{facilityProfile.submitProfile}"
											inlineStyle="white-space: nowrap;">
											<t:updateActionListener property="#{facilityProfile.fpId}"
												value="#{todos.fpId}" />
											<t:updateActionListener
												property="#{menuItem_facProfile.disabled}" value="false" />
										</af:commandLink>
									</af:column>

									<af:column sortProperty="facilityNm" sortable="true"
										rendered="#{permitToDoSearch.showFacility}"
										headerText="Facility Name" formatType="text">
										<af:inputText readOnly="true" value="#{todos.facilityNm}" />
									</af:column>

									<af:column sortProperty="cmpId" sortable="true"
										rendered="#{toDoSearch.showFacility}"
										headerText="Company ID" formatType="text">
										<af:commandLink inlineStyle="white-space: nowrap;"
											text="#{todos.cmpId}"
											action="#{companyProfile.submitProfile}">
											<t:updateActionListener property="#{companyProfile.cmpId}"
												value="#{todos.cmpId}" />
											<t:updateActionListener
												property="#{menuItem_companyProfile.disabled}" value="false" />
										</af:commandLink>
									</af:column>

									<af:column sortProperty="cmpName" sortable="true"
										rendered="#{toDoSearch.showFacility}"
										headerText="Company Name" formatType="text">
										<af:inputText readOnly="true" value="#{todos.cmpName}" />
									</af:column>

									<af:column sortProperty="naicsCd" sortable="true"
										rendered="#{permitToDoSearch.showFacility}" 
										headerText="NAICS" formatType="text">
										<af:inputText readOnly="true" value="#{todos.naicsCd}" />
									</af:column>

									<af:column headerText="Task" bandingShade="light">

										<af:column sortProperty="activityTemplateNm" sortable="true"
											headerText="Name" formatType="text">
											<af:commandLink action="#{activityProfile.submitActProfile}">
												<af:outputText
													inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}"
													value="#{todos.activityTemplateNm}" />
												<t:updateActionListener
													property="#{menuItem_activityProfile.disabled}"
													value="false" />
												<t:updateActionListener
													property="#{activityProfile.processId}"
													value="#{todos.processId}" />
												<t:updateActionListener
													property="#{workFlow2DDraw.processId}"
													value="#{todos.processId}" />
												<t:updateActionListener
													property="#{activityProfile.loopCnt}"
													value="#{todos.loopCnt}" />
												<t:updateActionListener
													property="#{activityProfile.activityTemplateId}"
													value="#{todos.activityTemplateId}" />
												<t:updateActionListener
													property="#{activityProfile.fromExternal}"
													value="#{permitToDoSearch.fromExternal}" />
												<t:updateActionListener
													property="#{activityProfile.aggregate}"
													value="#{todos.aggregate}" />
												<t:updateActionListener
													property="#{menuItem_workflowProfile.disabled}"
													value="false" />
											</af:commandLink>
										</af:column>

										<af:column sortProperty="userName" headerText="Staff Assigned"
											sortable="true" formatType="text">
											<af:inputText readOnly="true" value="#{todos.userName}"
												inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}" />
										</af:column>

										<af:column sortProperty="activityStatusDesc" sortable="true"
											headerText="State" formatType="text">
											<af:inputText readOnly="true"
												value="#{todos.activityStatusDesc}"
												inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}" />
										</af:column>

										<af:column sortProperty="duration" sortable="true"
											headerText="Days" formatType="number">
											<af:inputText readOnly="true" value="#{todos.duration}"
												inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}#{todos.durationColor}" />
										</af:column>

										<af:column sortProperty="endDt" sortable="true"
											headerText="End Date" formatType="text">
											<af:selectInputDate value="#{todos.endDt}" readOnly="true"
												inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}" />
										</af:column>
									</af:column>

									<af:column headerText="Workflow">

										<af:column sortProperty="processTemplateNm" sortable="true"
											headerText="Name" formatType="text">
											<af:commandLink action="#{workFlow2DDraw.submitProfile}">
												<af:outputText value="#{todos.processTemplateNm}" />
												<t:updateActionListener
													property="#{workFlow2DDraw.processId}"
													value="#{todos.processId}" />
												<t:updateActionListener
													property="#{menuItem_workflowProfile.disabled}"
													value="false" />
											</af:commandLink>
										</af:column>

										<af:column sortProperty="status" sortable="true"
											headerText="Status" formatType="text">
											<h:panelGrid border="0" width="100%" align="left">
												<af:selectOneChoice label="Status" value="#{todos.status}"
													inlineStyle="#{todos.viewed ? 'font-weight:regular;background-color:#' : 'font-weight:bold;background-color:#'}#{todos.statusColor}"
													readOnly="true">
													<f:selectItems value="#{workFlowDefs.statusDef}" />
												</af:selectOneChoice>
											</h:panelGrid>
										</af:column>

										<af:column sortProperty="processStartDt" sortable="true"
											headerText="Start Date" formatType="text">
											<af:selectInputDate value="#{todos.processStartDt}"
												readOnly="true" />
										</af:column>

										<af:column sortProperty="processDuration" sortable="true"
											headerText="Days" formatType="number">
											<af:inputText readOnly="true"
												value="#{todos.processDuration}" />
										</af:column>

										<af:column sortProperty="processDueDt" sortable="true"
											headerText="Due Date" formatType="text">
											<af:selectInputDate value="#{todos.processDueDt}"
												readOnly="true" />
										</af:column>

									</af:column>
									<af:column headerText="Permit" bandingShade="light"
										formatType="text">

										<af:column sortProperty="permitNumber" sortable="true"
											headerText="Number" formatType="text">
											<af:commandLink text="#{todos.permitNumber}"
                        						action="#{permitDetail.loadPermit}" >	
				                          		<af:setActionListener to="#{permitDetail.permitID}"
												from="#{todos.permitID}" />
				                          		<t:updateActionListener property="#{permitDetail.fromTODOList}"
												value="false" />
											</af:commandLink>    
										</af:column>

										<af:column sortProperty="permitTypeDesc" sortable="true"
											headerText="Type" formatType="text">
											<af:inputText readOnly="true" value="#{todos.permitTypeDesc}" />
										</af:column>

										<af:column headerText="Reason(s)" sortable="true"
											sortProperty="reasons" formatType="text">
											<af:selectManyCheckbox label="Reason(s) :" valign="top"
												readOnly="true" value="#{todos.permitReasonCDs}"
												inlineStyle="white-space: nowrap;">
												<f:selectItems value="#{permitSearch.allPermitReasons}" />
											</af:selectManyCheckbox>
										</af:column>

										<af:column sortProperty="description" sortable="true"
											headerText="Description" width="500" formatType="text">
											<af:inputText readOnly="true" value="#{todos.description}" />
										</af:column>

										<%-- Task #1971
                 	
                 	The following lines are commented since Rush 
                 	column does not apply to AQD and IMPACT.
                 	
                    <af:column sortProperty="expedited" sortable="true"
                      headerText="Rush">
                      <af:inputText readOnly="true"
                        value="#{todos.expedited}" />
                    </af:column>

					--%>
										<af:column headerText="Application Numbers" sortable="true"
											sortProperty="applicationNumber" formatType="text">
											<af:panelHorizontal valign="middle" halign="left">

												<af:iterator value="#{todos.applicationNumbers}" var="app">
													<af:commandLink text="#{app}" action="applicationDetail">
														<af:setActionListener from="#{app}"
															to="#{applicationDetail.applicationNumber}" />
													</af:commandLink>
													<af:outputText value=" " />
												</af:iterator>
											</af:panelHorizontal>
										</af:column>

										<af:column headerText="Publication Stage" sortable="true"
											sortProperty="permitGlobalStatusCd" formatType="text">
											<af:selectOneChoice label="Permit status"
												value="#{todos.permitGlobalStatusCd}" readOnly="true"
												id="soc2">
												<mu:selectItems
													value="#{permitReference.permitGlobalStatusDefs}" id="soc2" />
											</af:selectOneChoice>
										</af:column>

										<af:column headerText="Public Notice Date" sortable="true"
											sortProperty="publicNoticePublishDate" formatType="text">
											<af:selectInputDate readOnly="true"
												value="#{todos.publicNoticePublishDate}" />
										</af:column>

										<af:column headerText="Expiration Date" sortable="true"
											sortProperty="expirationDate" formatType="text">
											<af:selectInputDate readOnly="true"
												value="#{todos.expirationDate}" />
										</af:column>

										<af:column headerText="Permit Sent Out Date" sortable="true"
											sortProperty="permitSentOutDate" formatType="text">
											<af:selectInputDate readOnly="true"
												value="#{todos.permitSentOutDate}" />
										</af:column>

										<af:column headerText="Effective Date" sortable="true"
											sortProperty="effectiveDate" formatType="text">
											<af:selectInputDate readOnly="true"
												value="#{todos.effectiveDate}" />
										</af:column>

										<af:column headerText="Final Issuance Date" sortable="true"
											sortProperty="finalIssueDate" formatType="text">
											<af:selectInputDate readOnly="true"
												value="#{todos.finalIssueDate}" />
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

			</af:page>
		</af:form>
	</af:document>
</f:view>
