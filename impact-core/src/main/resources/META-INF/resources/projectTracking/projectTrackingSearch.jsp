<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document title="Project Tracking Search">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Project Search">
				<%@ include file="../util/header.jsp"%>

				<afh:rowLayout halign="center">
					<h:panelGrid border="1">

						<af:panelBorder>
							<af:showDetailHeader text="Search Criteria" disclosed="true">
								<af:panelForm rows="3" width="1000" maxColumns="4" partialTriggers="projectTypeCd">
									<af:inputText columns="10" maximumLength="10"
										label="Project ID:" tip="0000000000, 0%, %0%, *0*, 0*"
										value="#{projectTrackingSearch.projectId}" />

									<af:inputText tip="acme%, %acme% *acme*, acme*" columns="25"
										label="Project Name:" value="#{projectTrackingSearch.projectName}"
										maximumLength="250"
										valign="top" />

										<af:selectOneChoice
											label="Project Type:" id="projectTypeCd"
											unselectedLabel=""
											valueChangeListener="#{projectTrackingSearch.projectTypeChanged}"
											value="#{projectTrackingSearch.projectTypeCd}"
											autoSubmit="true">
											<f:selectItems
												value="#{projectTrackingReference.projectTypeDefs.items[0]}" />
										</af:selectOneChoice>		

									<af:selectOneChoice
										label="Project Status:"
										unselectedLabel=""
										value="#{projectTrackingSearch.projectStateCd}"
										>
										<f:selectItems
											value="#{projectTrackingReference.projectStateDefs.items[0]}" />
									</af:selectOneChoice>

									<af:selectOneChoice
										label="NEPA Level:" rendered="#{projectTrackingSearch.projectTypeCd eq 'PTYPE10'}"
										unselectedLabel=""
										value="#{projectTrackingSearch.projectNEPALevelCd}"
										>
										<f:selectItems
											value="#{projectTrackingReference.NEPALevelTypeDefs.items[0]}" />
									</af:selectOneChoice>

									<af:selectOneChoice
										label="Project Stage:" rendered="#{projectTrackingSearch.projectTypeCd eq 'PTYPE10'}"
										unselectedLabel=""
										value="#{projectTrackingSearch.projectStageCd}"
										>
										<f:selectItems
											value="#{projectTrackingReference.projectStageDefs.items[0]}" />
									</af:selectOneChoice>

									<af:selectOneChoice
										label="Outreach Category:" rendered="#{projectTrackingSearch.projectTypeCd eq 'PTYPE20'}"
										unselectedLabel=""
										value="#{projectTrackingSearch.projectOutreachCategoryCd}"
										>
										<f:selectItems
											value="#{projectTrackingReference.outreachCategoryDefs.items[0]}" />
									</af:selectOneChoice>

									<af:selectOneChoice
										label="Grant Status:" rendered="#{projectTrackingSearch.projectTypeCd eq 'PTYPE20'}"
										unselectedLabel=""
										value="#{projectTrackingSearch.projectGrantStatusCd}"
										>
										<f:selectItems
											value="#{projectTrackingReference.grantStatusDefs.items[0]}" />
									</af:selectOneChoice>

									<af:selectOneChoice
										label="Letter Type:" rendered="#{projectTrackingSearch.projectTypeCd eq 'PTYPE30'}"
										unselectedLabel=""
										value="#{projectTrackingSearch.projectLetterTypeCd}"
										>
										<f:selectItems
											value="#{projectTrackingReference.letterTypeDefs.items[0]}" />
									</af:selectOneChoice>

									<af:selectOneChoice
										label="Contract Type:" rendered="#{projectTrackingSearch.projectTypeCd eq 'PTYPE40'}"
										unselectedLabel=""
										value="#{projectTrackingSearch.projectContractTypeCd}"
										>
										<f:selectItems
											value="#{projectTrackingReference.contractTypeDefs.items[0]}" />
									</af:selectOneChoice>

									<af:inputText readOnly="true"
									 rendered="#{empty projectTrackingSearch.projectTypeCd}"
										value=" " />

									<af:inputText readOnly="true"
									 rendered="#{empty projectTrackingSearch.projectTypeCd or (projectTrackingSearch.projectTypeCd eq 'PTYPE30' or projectTrackingSearch.projectTypeCd eq 'PTYPE40')}"
										value=" " />

									<af:inputText readOnly="true"
									 rendered="true"
										value=" " />

									<af:inputText readOnly="true"
									 rendered="true"
										value=" " />

									<af:inputText label="Project Description:"
										tip="acme%, %acme% *acme*, acme*"
										maximumLength="1000"
										value="#{projectTrackingSearch.projectDescription}" />

									<af:inputText label="Project Summary:"
										tip="acme%, %acme% *acme*, acme*"
										maximumLength="1000"
										value="#{projectTrackingSearch.projectSummary}" />


									<af:selectOneChoice label="Project Lead:"
										unselectedLabel="" 
										value="#{projectTrackingSearch.projectLeadId}">
										<f:selectItems
											value="#{infraDefs.allUsersDef.allItems}" />
									</af:selectOneChoice>

									<af:inputText readOnly="true"
										value=" " />

									<af:selectOneChoice
										label="Event Type:" tip="Requires a Project Type selection."
										disabled="#{empty projectTrackingSearch.projectTypeCd}"
										unselectedLabel=""
										value="#{projectTrackingSearch.eventTypeCd}">
										<f:selectItems
											value="#{projectTrackingSearch.projectTypeTrackingEventDefs.items[0]}" />
									</af:selectOneChoice>
				                  <af:selectInputDate label="From:" tip="Search will return results inclusive of this date."
				                  	disabled="#{empty projectTrackingSearch.projectTypeCd}"
				                    value="#{projectTrackingSearch.eventDateFrom}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
				                  <af:selectInputDate label="To:" tip="Search will return results inclusive of this date."
				                  	disabled="#{empty projectTrackingSearch.projectTypeCd}"
				                    value="#{projectTrackingSearch.eventDateTo}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

								</af:panelForm>


								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:objectSpacer width="100%" height="20" />
										<af:commandButton text="Submit"
											action="#{projectTrackingSearch.submitSearch}">
										</af:commandButton>

										<af:commandButton text="Reset"
											action="#{projectTrackingSearch.reset}" />

									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />

				<jsp:include flush="true" page="projectTrackingSearchTable.jsp" />

			</af:page>
		</af:form>
	</af:document>
</f:view>