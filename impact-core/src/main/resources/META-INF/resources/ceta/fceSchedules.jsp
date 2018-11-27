<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Inspections Scheduling Search Criteria">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<f:verbatim><%@ include	file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
		<f:verbatim><%@ include file="../scripts/FacilityType-Option.js"%></f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Inspections Scheduling Search">
				<%@ include file="../util/branding.jsp"%>
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						type="#{foo.type}" disabled="#{foo.disabled}"
						rendered="#{foo.rendered}" icon="#{foo.icon}"
						onclick="if (#{fceSchedules.editable}) { alert('Please save or discard your changes'); return false; }" />
				</f:facet>
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000">
						<af:panelBorder>
							<af:showDetailHeader text="Inspections Scheduling Search Criteria"
								disclosed="#{fceSchedules.disclosed}">
								<af:panelForm rows="1" maxColumns="3">
									<af:inputText columns="25" label="Facility ID"
										value="#{fceSchedules.facilityId}"
										maximumLength="10" tip="0000000000, 0%, %0%, *0*, 0*" />
									<af:inputText tip="acme%, %acme% *acme*, acme*" columns="25"
										label="Facility Name" value="#{fceSchedules.facilityName}"
										valign="top" />
									<af:selectOneChoice label="Facility Class"
										value="#{fceSchedules.permitClassCd}" unselectedLabel="">
										<f:selectItems
											value="#{facilityReference.permitClassDefs.items[0]}" />
									</af:selectOneChoice>

									<af:selectOneChoice label="Facility Type"
										value="#{fceSchedules.facilityTypeCd}" unselectedLabel=""
										styleClass="FacilityTypeClass x6">
										<f:selectItems
											value="#{facilityReference.facilityTypeDefs.items[0]}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="County"
										value="#{fceSchedules.countyCd}" unselectedLabel="">
										<f:selectItems value="#{infraDefs.counties}" />
									</af:selectOneChoice>
									<af:selectBooleanCheckbox label="Show Already Scheduled"
										value="#{fceSchedules.schedOrNot}" />
									<af:selectOneChoice label="Company"
										value="#{fceSchedules.cmpId}" unselectedLabel="">
										<f:selectItems value="#{companySearch.allCompanies}" />
									</af:selectOneChoice>
									<af:outputText value="" />
									
									<af:selectOneChoice label="District" id="dolaa" rendered="#{infraDefs.districtVisible}"
										value="#{fceSchedules.doLaaCd}" unselectedLabel="">
										<f:selectItems value="#{facilitySearch.doLaas}" />
									</af:selectOneChoice>
									<%-- This is to make the page Layout unchanged with District not rendered --%>
									<af:inputHidden id="districtHidden" rendered="#{!infraDefs.districtVisible}"/>
									<af:selectOneChoice label="Show Through FFY"
										value="#{fceSchedules.showThruFfy}">
										<f:selectItems value="#{fceSchedules.choiceOfFfy}" />
									</af:selectOneChoice>
								</af:panelForm>
								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Submit"
											disabled="#{fceSchedules.editable}"
											action="#{fceSchedules.submitSearch}">
										</af:commandButton>
										<af:commandButton text="Reset" action="#{fceSchedules.reset}"
											disabled="#{fceSchedules.editable}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
							<af:commandLink text="Help To Schedule Inspection"
								useWindow="true" blocking="false" windowWidth="800"
								windowHeight="600" inlineStyle="margin-left:10px;padding:5px;"
								returnListener="#{fceSchedules.dialogDone}"
								action="#{fceSchedules.displayInspectionScheduleHelpInfo}" />
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout halign="center"
					partialTriggers="fceSched:oldAssigned fceSched:oldEval">
					<h:panelGrid border="1" 
						rendered="#{fceSchedules.hasSearchResults}">
						<af:showDetailHeader text="Inspection Schedule Information"
							disclosed="true">
							<af:table id="fceSched" emptyText=" "
								value="#{fceSchedules.scheduleList}" var="fceSched"
								bandingInterval="1" banding="row"
								rows="#{fceSchedules.displayRows}">
								<af:column sortable="true" sortProperty="facilityId"
									headerText="Facility ID">
									<af:outputText value="#{fceSched.facilityId}"
										inlineStyle="#{fceSched.flagErrorExisting?'color: orange; font-weight: bold;':''}"
										rendered="#{fceSchedules.editable}" />
									<af:commandLink text="#{fceSched.facilityId}"
										rendered="#{!fceSchedules.editable}"
										action="#{facilityProfile.submitProfileById}"
										inlineStyle="white-space: nowrap;">
										<t:updateActionListener
											property="#{facilityProfile.facilityId}"
											value="#{fceSched.facilityId}" />
										<t:updateActionListener
											property="#{menuItem_facProfile.disabled}" value="false" />
									</af:commandLink>
								</af:column>
								<af:column sortable="true" sortProperty="facilityName"
									headerText="Facility Name">
									<af:outputText value="#{fceSched.facilityName}"
										inlineStyle="#{fceSched.flagErrorExisting?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column sortProperty="permitClassCd" sortable="true" formatType="text"
									headerText="Facility Class">
									<af:outputText
										value="#{facilityReference.permitClassDefs.itemDesc[(empty fceSched.permitClassCd ? '' : fceSched.permitClassCd)]}" />
								</af:column>

								<af:column sortProperty="facilityTypeCd" sortable="true" formatType="text"
									headerText="Facility Type">
									<af:outputText
										value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty fceSched.facilityTypeCd ? '' : fceSched.facilityTypeCd)]}" />
								</af:column>

								<af:column sortProperty="cmpId" sortable="true" noWrap="true"
									formatType="text" headerText="Company ID">
									<af:commandLink action="#{companyProfile.submitProfile}"
										text="#{fceSched.cmpId}" inlineStyle="white-space: nowrap;">
										<t:updateActionListener property="#{companyProfile.cmpId}"
											value="#{fceSched.cmpId}" />
										<t:updateActionListener
											property="#{menuItem_companyProfile.disabled}" value="false" />
									</af:commandLink>
								</af:column>

								<af:column sortProperty="companyName" sortable="true" noWrap="true"
									formatType="text" headerText="Company Name">
									<af:outputText value="#{fceSched.companyName}" inlineStyle="#{fceSched.flagErrorExisting?'color: orange; font-weight: bold;':''}" />
								</af:column>

								<af:column sortProperty="countyCd" sortable="true"
									formatType="text" headerText="County">
									<af:selectOneChoice value="#{fceSched.countyCd}"
										readOnly="true"
										inlineStyle="#{fceSched.flagErrorExisting?'color: orange; font-weight: bold;':''}">
										<f:selectItems value="#{infraDefs.counties}" />
									</af:selectOneChoice>
								</af:column>
								<af:column sortProperty="operatingStatusCd" sortable="true"
									formatType="icon" headerText="Operating Status">
									<af:selectOneChoice id="opStat" readOnly="true"
										inlineStyle="#{fceSched.flagErrorExisting?'color: orange; font-weight: bold;':''}"
										value="#{fceSched.operatingStatusCd}">
										<f:selectItems
											value="#{facilityReference.operatingStatusDefs.allSearchItems}" />
									</af:selectOneChoice>
								</af:column>
								<af:column headerText="Last Completed Inspection">
									<af:column sortProperty="inFfy" sortable="true"
										formatType="text" headerText="FFY">		
											<af:outputText value="#{fceSched.inFfy}"
											shortDesc="Inspection ID = #{fceSched.completedFceId}" />
									</af:column>
									<af:column formatType="text" headerText="Evaluated Date"
										sortProperty="lastCompleted" sortable="true">
										<af:selectInputDate id="dateEvaluated" readOnly="true"
											rendered="#{fceSched.completedFceId != null && (!fceSchedules.editable || fceSched.lastCompleted != null)}"
											value="#{fceSched.lastCompleted}" >
											<af:validateDateTimeRange minimum="1900-01-01" />
										</af:selectInputDate>
									</af:column>
									<af:column sortProperty="oldEvaluator" sortable="true"
										formatType="text" headerText="Inspector">
										<afh:rowLayout rendered="#{fceSched.completedFceId != null}">
											<af:selectOneChoice id="pEval"
												readOnly="#{!fceSchedules.editable || fceSched.lastCompleted != null}"
												value="#{fceSched.oldEvaluator}" unselectedLabel="">
												<f:selectItems
													value="#{infraDefs.basicUsersDef.items[(empty fceSched.oldEvaluator?0:fceSched.oldEvaluator)]}" />
											</af:selectOneChoice>
											<af:selectBooleanCheckbox
												rendered="#{fceSchedules.editable && !fceSched.locked && fceSched.oldEvaluator!=null && fceSched.activeUser && fceSched.assignedStaff == null}"
												id="oldEval" autoSubmit="true"
												value="#{fceSched.useOldEvaluator}" />
										</afh:rowLayout>
									</af:column>
								</af:column>
								<af:column headerText=" Scheduled Inspections">
									<af:column sortProperty="neededBy" sortable="#{!fceSchedules.editable}"
										formatType="text" headerText="Req. FFY">
											<af:outputText value="#{fceSched.neededBy}"
												inlineStyle="#{fceSched.flagOldDate?'color: orange; font-weight: bold;':''}"
												shortDesc="Inspection ID = #{fceSched.scheduledFceId}" />
										<af:commandButton text="Add Another"
											action="#{fceSchedules.addAnother}"
											rendered="#{fceSchedules.editable && fceSched.allowAddAnother}">
											<t:updateActionListener value="#{fceSched.rowNum}"
												property="#{fceSchedules.addAnotherLikeRowNum}" />
										</af:commandButton>
									</af:column>
									<af:column sortProperty="nextScheduled" sortable="true"
										formatType="text" headerText="Next Scheduled">
										<af:selectOneChoice id="sched"
											inlineStyle="#{fceSched.flagErrorExisting  || fceSched.flagOldDate?'color: orange; font-weight: bold;':''}"
											readOnly="#{!fceSchedules.editable || fceSched.locked}"
											value="#{fceSched.nextScheduled}">
											<f:selectItems value="#{fceSched.pickListSchedule}" />
										</af:selectOneChoice>
									</af:column>
									<af:column sortProperty="assignedStaff" sortable="true"
										formatType="text" headerText="Next Assigned Staff">
										<af:selectOneChoice id="staff"
											inlineStyle="#{fceSched.flagErrorExisting?'color: orange; font-weight: bold;':''}"
											readOnly="#{!fceSchedules.editable || fceSched.locked}"
											value="#{fceSched.assignedStaff}" unselectedLabel="">
											<f:selectItems
												value="#{infraDefs.basicUsersDef.items[(empty fceSched.assignedStaff?0:fceSched.assignedStaff)]}" />
										</af:selectOneChoice>
									</af:column>
								</af:column>
								<f:facet name="footer">
									<h:panelGrid width="100%">
										<afh:rowLayout halign="center">
											<af:panelButtonBar>
												<af:commandButton text="Display All Rows" id="displayRows"
													action="#{fceSchedules.displayAllRows}"
													rendered="#{fceSchedules.showDisplayAll}">
												</af:commandButton>
												<af:commandButton text="Display Some Rows"
													id="displaySomeRows"
													action="#{fceSchedules.displaySomeRows}"
													rendered="#{fceSchedules.showDisplaySome}">
												</af:commandButton>
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
									</h:panelGrid>
								</f:facet>
							</af:table>
							<af:objectSpacer height="10" />
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="Edit" action="#{fceSchedules.edit}"
										disabled="#{!fceDetail.hasSchedAdminRole}"
										rendered="#{!fceSchedules.editable && !fceSchedules.readOnlyUser}" />
									<af:commandButton text="Save" action="#{fceSchedules.save}"
										rendered="#{fceSchedules.editable}" />
									<af:commandButton text="Cancel" action="#{fceSchedules.cancel}"
										rendered="#{fceSchedules.editable}" immediate="true" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</af:showDetailHeader>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
