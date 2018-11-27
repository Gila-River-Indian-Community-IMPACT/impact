<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Inspection Detail">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}"
				title="Inspection Detail" id="eval">
				<%@ include file="../util/branding.jsp"%>
				<af:inputHidden value="#{stackTests.popupRedirect}" />
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						type="#{foo.type}" disabled="#{foo.disabled}"
						rendered="#{foo.rendered}" icon="#{foo.icon}"
						onclick="if (#{fceDetail.editable}) { alert('Please save or discard your changes'); return false; }" />
				</f:facet>
				<afh:rowLayout halign="center" width="1000"
					rendered="#{!fceDetail.blankOutPage}">
					<h:panelGrid border="1">
						<af:panelBorder>
							<f:facet name="top">
								<f:subview id="fceHeader">
									<jsp:include page="fceHeader.jsp" />
								</f:subview>
							</f:facet>
							<af:objectSpacer height="5" />
							<%--
				<af:panelForm rows="2" maxColumns="2" rendered="#{!fceDetail.blankOutPage}">
							<af:inputText
								rendered="#{fceDetail.stars2Admin}"
								value="#{fceDetail.fce.schedAfsId}" label="Schedule Lock ID:"
								inlineStyle="#{fceDetail.stars2Admin?'color: orange; font-weight: bold;':''}"
								readOnly="#{!fceDetail.schedEditable}" maximumLength="8"
								columns="8" />
							<af:selectInputDate
								rendered="#{fceDetail.stars2Admin}"
								label="Schedule Sent to US EPA on:" id="sAfsDate"
								readOnly="#{!fceDetail.schedEditable}"
								inlineStyle="#{fceDetail.stars2Admin?'color: orange; font-weight: bold;':''}"
								value="#{fceDetail.fce.schedAfsDate}" >
								<af:validateDateTimeRange minimum="1900-01-01" />
							</af:selectInputDate>
							<af:inputText
								rendered="#{fceDetail.stars2Admin}"
								value="#{fceDetail.fce.evalAfsId}" label="AFS ID:"
								inlineStyle="#{fceDetail.stars2Admin?'color: orange; font-weight: bold;':''}"
								readOnly="#{!fceDetail.complEditable}" maximumLength="8"
								columns="10" />
							<af:selectInputDate
								rendered="#{fceDetail.stars2Admin}"
								label="AFS Sent Date:" id="cAfsDate"
								readOnly="#{!fceDetail.complEditable}"
								inlineStyle="#{fceDetail.stars2Admin?'color: orange; font-weight: bold;':''}"
								value="#{fceDetail.fce.evalAfsDate}" >
								<af:validateDateTimeRange minimum="1900-01-01" />
							</af:selectInputDate>
						</af:panelForm>
						--%>

							<afh:rowLayout halign="center">

								<af:inputText
									rendered="#{fceDetail.fce.legacyInspection || fceDetail.fce.pre10Legacy}"
									readOnly="true" inlineStyle="color: orange; font-weight: bold;"
									value="Note: Inspection records in IMPACT created before #{fceDetail.fce.version10InstallDate}, as well as those marked Legacy, may not have all of the inspection and workflow data required for current inspections." />

							</afh:rowLayout>

							<af:objectSpacer height="5" />

							<afh:rowLayout halign="center">
								<af:selectOneRadio id="legacyInspection"
									label="Is this a legacy inspection? :" autoSubmit="true"
									readOnly="#{!fceDetail.schedEditable || !fceDetail.allowSchedEditOperations || fceDetail.existingFceObject}"
									layout="horizontal" value="#{fceDetail.fce.legacyInspection}">
									<f:selectItem itemLabel="Yes" itemValue="true" />
									<f:selectItem itemLabel="No" itemValue="false" />
								</af:selectOneRadio>
							</afh:rowLayout>

							<af:objectSpacer height="5" />

							<af:panelForm maxColumns="1" partialTriggers="legacyInspection">
								<afh:rowLayout halign="left"
									rendered="#{!fceDetail.blankOutPage}" width="100%">
									<af:panelForm rows="1" maxColumns="3"
										partialTriggers="sched dateComp usCommittment">
										<af:selectOneChoice id="staff" label="Staff Assigned:"
											readOnly="#{!fceDetail.schedEditable || !fceDetail.allowSchedEditOperations}"
											value="#{fceDetail.fce.assignedStaff}" unselectedLabel="None">
											<f:selectItems
												value="#{infraDefs.basicUsersDef.items[(empty fceDetail.fce.assignedStaff?0:fceDetail.fce.assignedStaff)]}" />
										</af:selectOneChoice>
										<af:selectOneChoice id="sched" label="Quarter Scheduled:"
											unselectedLabel="Unscheduled"
											readOnly="#{!fceDetail.schedEditable || !fceDetail.allowSchedEditOperations}"
											value="#{fceDetail.fce.scheduledTimestamp}" autoSubmit="true">
											<f:selectItems value="#{fceDetail.scheduleChoices}" />
										</af:selectOneChoice>
										<af:selectOneChoice label="Inspection Type:"
											id="inspectionType"
											readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
											value="#{fceDetail.fce.inspectionType}" unselectedLabel="">
											<f:selectItems
												value="#{compEvalDefs.inspectionTypeDefs.items[(empty fceDetail.fce.inspectionType ? '' : fceDetail.fce.inspectionType)]}" />
										</af:selectOneChoice>
									</af:panelForm>
								</afh:rowLayout>
								<af:objectSpacer height="5" />
								<afh:rowLayout halign="left"
									rendered="#{!fceDetail.blankOutPage}" width="100%">
									<af:panelForm rows="1">
										<af:selectOneChoice id="evaluator" label="Inspector:"
											readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
											value="#{fceDetail.fce.evaluator}" unselectedLabel=" ">
											<f:selectItems
												value="#{infraDefs.basicUsersDef.items[(empty fceDetail.fce.evaluator?0:fceDetail.fce.evaluator)]}" />
										</af:selectOneChoice>
										<af:selectInputDate id="dateEvaluated"
											label="Date Inspection Completed:"
											disabled="#{fceDetail.enhancedNonLegacyInspection}"
											readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
											value="#{fceDetail.dateEvaluated}" autoSubmit="true">
										</af:selectInputDate>
										<af:selectInputDate id="dateReported"
											label="Date Report Completed:"
											disabled="#{fceDetail.enhancedNonLegacyInspection}"
											readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
											value="#{fceDetail.dateReported}">
										</af:selectInputDate>
										<af:selectInputDate id="dateSentToEPA"
											label="Date Sent to EPA:" disabled="false"
											readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
											value="#{fceDetail.dateSentToEPA}">
										</af:selectInputDate>
										<af:selectBooleanCheckbox
											label="Assessed Private Property Access:"
											id="privatePropertyAccess"
											disabled="#{!fceDetail.enhancedNonLegacyInspection}"
											readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
											value="#{fceDetail.fce.privatePropertyAccess}" />
										<af:selectBooleanCheckbox label="Announced Inspection:"
											id="announcedInspection"
											disabled="#{!fceDetail.enhancedNonLegacyInspection}"
											readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
											value="#{fceDetail.fce.announcedInspection}" />
									</af:panelForm>
								</afh:rowLayout>
								<afh:rowLayout width="100%">
									<afh:cellFormat width="25%" halign="left">
										<af:panelForm partialTriggers="complianceStatusCd">
											<afh:rowLayout width="100%">
												<afh:cellFormat halign="left" valign="top" width="100%">
													<afh:rowLayout width="100%"
														rendered="#{fceDetail.complEditable || fceDetail.fce.additionalAqdStaffPresentExists}">
														<af:table id="stTable" emptyText=" " width="100%"
															value="#{fceDetail.fce.additionalAqdStaffPresent}"
															var="ev" bandingInterval="1" banding="row">
															<af:column sortable="false"
																rendered="#{fceDetail.complEditable}" formatType="icon"
																headerText="Select">
																<af:selectBooleanCheckbox value="#{ev.selected}"
																	readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}" />
															</af:column>
															<af:column sortable="false" formatType="text"
																headerText="Additional AQD Staff">
																<af:selectOneChoice unselectedLabel=""
																	value="#{ev.evaluator}"
																	readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}">
																	<f:selectItems
																		value="#{empty ev.evaluator ? fceDetail.stBasicUsersDef.items[0] : fceDetail.stBasicUsersDef.items[ev.evaluator]}" />
																</af:selectOneChoice>
															</af:column>
															<f:facet name="footer">
																<afh:rowLayout halign="center">
																	<af:panelButtonBar>
																		<af:commandButton id="additionalAqdStaffPresent_Add"
																			action="#{fceDetail.addAdditionalAqdStaffPresent}"
																			rendered="#{fceDetail.complEditable}"
																			disabled="#{!fceDetail.enhancedNonLegacyInspection}"
																			shortDesc="Click to add another person.  Blank out name to remove it."
																			text="Add Staff" />
																		<af:commandButton
																			id="additionalAqdStaffPresent_Remove"
																			action="#{fceDetail.deleteAdditionalAqdStaffPresent}"
																			rendered="#{fceDetail.complEditable}"
																			disabled="#{!fceDetail.enhancedNonLegacyInspection}"
																			shortDesc="Click to remove selected persons."
																			text="Delete Selected Staff" />
																	</af:panelButtonBar>
																</afh:rowLayout>
															</f:facet>
														</af:table>
													</afh:rowLayout>
													<afh:rowLayout halign="left"
														rendered="#{(!fceDetail.complEditable || !fceDetail.allowComplEditOperations) && !fceDetail.fce.additionalAqdStaffPresentExists}">
														<af:outputLabel value="No Additional AQD Staff Added" />
													</afh:rowLayout>
												</afh:cellFormat>
											</afh:rowLayout>
										</af:panelForm>
									</afh:cellFormat>
									<afh:cellFormat width="75%" halign="left">
										<af:panelForm rows="1" width="100%">
											<af:inputText id="facilityStaffPresent"
												label="Facility Staff Present:"
												disabled="#{!fceDetail.enhancedNonLegacyInspection}"
												readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
												value="#{fceDetail.fce.facilityStaffPresent}" columns="80"
												rows="3" maximumLength="400" />
										</af:panelForm>
									</afh:cellFormat>
								</afh:rowLayout>

								<af:outputText inlineStyle="font-size:75%;color:#666"
									rendered="#{!fceDetail.enhancedNonLegacyInspection}"
									value="          * To create the inspection at least both Staff Assigned & Quarter Scheduled must be set OR Inspector, Date Inspection Completed, Inspection Type & Inspected Under Classification fields must be set" />
								<af:inputHidden id="errChoice"
									value="place holder for error message" />

								<af:showDetailHeader text="Ambient Conditions" disclosed="true">
									<af:panelForm rows="1" maxColumns="3">
										<af:selectInputDate id="dayOneInspectionDate"
											label="Inspection Date:"
											disabled="#{!fceDetail.enhancedNonLegacyInspection}"
											readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
											value="#{fceDetail.fce.dayOneAmbientConditions.inspectionDate}">
											<af:validateDateTimeRange minimum="1900-01-01"
												maximum="#{infraDefs.currentDate}" />
										</af:selectInputDate>
										<af:inputText id="dayOneAmbientTemperature"
											disabled="#{!fceDetail.enhancedNonLegacyInspection}"
											label="Ambient Temperature (°F):" columns="15"
											readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
											value="#{fceDetail.fce.dayOneAmbientConditions.ambientTemperature}" />
										<af:selectOneChoice id="dayOneSkyConditionCd"
											label="Sky Condition:"
											disabled="#{!fceDetail.enhancedNonLegacyInspection}"
											readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
											value="#{fceDetail.fce.dayOneAmbientConditions.skyConditionCd}"
											unselectedLabel=" ">
											<f:selectItems
												value="#{compEvalDefs.skyConditionsDef.items[(empty fceDetail.fce.dayOneAmbientConditions.skyConditionCd ? '' : fceDetail.fce.dayOneAmbientConditions.skyConditionCd)]}" />
										</af:selectOneChoice>
										<af:selectOneChoice id="dayOneArrivalTime"
											disabled="#{!fceDetail.enhancedNonLegacyInspection}"
											label="Arrival Time:" unselectedLabel=" "
											readOnly="#{!fceDetail.schedEditable || !fceDetail.allowSchedEditOperations}"
											value="#{fceDetail.fce.dayOneAmbientConditions.arrivalTime}">
											<f:selectItems value="#{fceDetail.arrivalDepartureTimes}" />
										</af:selectOneChoice>
										<af:selectOneChoice id="dayOneDepartureTime"
											disabled="#{!fceDetail.enhancedNonLegacyInspection}"
											label="Departure Time:" unselectedLabel=" "
											readOnly="#{!fceDetail.schedEditable || !fceDetail.allowSchedEditOperations}"
											value="#{fceDetail.fce.dayOneAmbientConditions.departureTime}">
											<f:selectItems value="#{fceDetail.arrivalDepartureTimes}" />
										</af:selectOneChoice>
										<af:selectOneChoice id="dayOneWindDirectionCd"
											label="Wind Direction:"
											disabled="#{!fceDetail.enhancedNonLegacyInspection}"
											readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
											value="#{fceDetail.fce.dayOneAmbientConditions.windDirectionCd}"
											unselectedLabel=" ">
											<f:selectItems
												value="#{compEvalDefs.windDirectionDef.items[(empty fceDetail.fce.dayOneAmbientConditions.windDirectionCd ? '' : fceDetail.fce.dayOneAmbientConditions.windDirectionCd)]}" />
										</af:selectOneChoice>
										<af:inputText id="dayOneWindSpeed"
											disabled="#{!fceDetail.enhancedNonLegacyInspection}"
											label="Wind Speed (Miles per hour):" columns="15"
											readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
											value="#{fceDetail.fce.dayOneAmbientConditions.windSpeed}" />
									</af:panelForm>
								</af:showDetailHeader>

								<af:showDetailHeader text="Ambient Conditions (2nd day)"
									disclosed="#{fceDetail.fce.dayTwoAmbientConditions.inspectionDate != null }">
									<af:panelForm rows="1" maxColumns="3">
										<af:selectInputDate id="dayTwoInspectionDate"
											label="Inspection Date:"
											disabled="#{!fceDetail.enhancedNonLegacyInspection}"
											readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
											value="#{fceDetail.fce.dayTwoAmbientConditions.inspectionDate}">
											<af:validateDateTimeRange minimum="1900-01-01"
												maximum="#{infraDefs.currentDate}" />
										</af:selectInputDate>
										<af:inputText id="dayTwoAmbientTemperature"
											label="Ambient Temperature (°F):" columns="15"
											disabled="#{!fceDetail.enhancedNonLegacyInspection}"
											readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
											value="#{fceDetail.fce.dayTwoAmbientConditions.ambientTemperature}" />
										<af:selectOneChoice id="dayTwoSkyConditionCd"
											label="Sky Condition:"
											disabled="#{!fceDetail.enhancedNonLegacyInspection}"
											readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
											value="#{fceDetail.fce.dayTwoAmbientConditions.skyConditionCd}"
											unselectedLabel=" ">
											<f:selectItems
												value="#{compEvalDefs.skyConditionsDef.items[(empty fceDetail.fce.dayTwoAmbientConditions.skyConditionCd ? '' : fceDetail.fce.dayTwoAmbientConditions.skyConditionCd)]}" />
										</af:selectOneChoice>
										<af:selectOneChoice id="dayTwoArrivalTime"
											label="Arrival Time:" unselectedLabel=" "
											disabled="#{!fceDetail.enhancedNonLegacyInspection}"
											readOnly="#{!fceDetail.schedEditable || !fceDetail.allowSchedEditOperations}"
											value="#{fceDetail.fce.dayTwoAmbientConditions.arrivalTime}">
											<f:selectItems value="#{fceDetail.arrivalDepartureTimes}" />
										</af:selectOneChoice>
										<af:selectOneChoice id="dayTwoDepartureTime"
											label="Departure Time:" unselectedLabel=" "
											disabled="#{!fceDetail.enhancedNonLegacyInspection}"
											readOnly="#{!fceDetail.schedEditable || !fceDetail.allowSchedEditOperations}"
											value="#{fceDetail.fce.dayTwoAmbientConditions.departureTime}">
											<f:selectItems value="#{fceDetail.arrivalDepartureTimes}" />
										</af:selectOneChoice>
										<af:selectOneChoice id="dayTwoWindDirectionCd"
											label="Wind Direction:"
											disabled="#{!fceDetail.enhancedNonLegacyInspection}"
											readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
											value="#{fceDetail.fce.dayTwoAmbientConditions.windDirectionCd}"
											unselectedLabel=" ">
											<f:selectItems
												value="#{compEvalDefs.windDirectionDef.items[(empty fceDetail.fce.dayTwoAmbientConditions.windDirectionCd ? '' : fceDetail.fce.dayTwoAmbientConditions.windDirectionCd)]}" />
										</af:selectOneChoice>
										<af:inputText id="dayTwoWindSpeed"
											disabled="#{!fceDetail.enhancedNonLegacyInspection}"
											label="Wind Speed (Miles per hour):" columns="15"
											readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
											value="#{fceDetail.fce.dayTwoAmbientConditions.windSpeed}" />
									</af:panelForm>
								</af:showDetailHeader>

								<af:showDetailHeader
									text="Facility Description/Compliance Status" disclosed="true">
									<af:panelForm maxColumns="1" labelWidth="150px">
										<afh:rowLayout halign="left">
											<af:inputText id="facilityDesc" label="Facility Description:"
												readOnly="true" rendered="true" disabled="true"
												value="#{fceDetail.facility.desc}" columns="100" rows="3" />
										</afh:rowLayout>
										<afh:rowLayout halign="left">
											<af:inputText id="noteTxt" label="Compliance Status:"
												readOnly="true"
												rendered="#{(fceDetail.fce.pre10Legacy || fceDetail.fce.legacyInspection) && !fceDetail.editable}"
												value="#{fceDetail.fce.memo}" columns="140" rows="4" />
											<af:inputText id="noteTxt2" label="Compliance Status:"
												rendered="#{(fceDetail.fce.pre10Legacy || fceDetail.fce.legacyInspection) && fceDetail.editable}"
												value="#{fceDetail.fce.memo}" columns="140" rows="7"
												maximumLength="4000" />

											<af:selectOneChoice id="complianceStatusCd"
												label="Compliance Status:" autoSubmit="true"
												rendered="#{!(fceDetail.fce.pre10Legacy || fceDetail.fce.legacyInspection)}"
												readOnly="#{!fceDetail.complEditable || !fceDetail.allowComplEditOperations}"
												value="#{fceDetail.fce.complianceStatusCd}"
												unselectedLabel=" ">
												<f:selectItems
													value="#{compEvalDefs.overallInspectionComplianceStatusDef.items[(empty fceDetail.fce.complianceStatusCd ? '' : fceDetail.fce.complianceStatusCd)]}" />
											</af:selectOneChoice>
										</afh:rowLayout>
										<afh:rowLayout halign="left">
											<af:inputText id="complianceStatusDesc"
												label="Compliance Status Description:" readOnly="true"
												disabled="true" partialTriggers="complianceStatusCd"
												rendered="#{!(fceDetail.fce.pre10Legacy || fceDetail.fce.legacyInspection)}"
												value="#{fceDetail.fce.complianceStatusLongDesc}"
												columns="100" rows="2" />
										</afh:rowLayout>
									</af:panelForm>
								</af:showDetailHeader>

								<afh:rowLayout halign="left" width="100%">
									<af:panelForm maxColumns="1"
										rendered="#{!fceDetail.blankOutPage}">
										<af:objectSpacer height="5" />
										<af:showDetailHeader text="Associated Site Visits"
											disclosed="true">
											<af:table id="svTable" emptyText=" " width="98%"
												value="#{fceDetail.fce.assocSiteVisits}" var="sv"
												bandingInterval="1" banding="row">
												<af:column formatType="text" headerText="Site Visit ID"
													sortable="true" sortProperty="id">
													<af:commandLink useWindow="true" windowWidth="800"
														disabled="#{fceDetail.editable}" windowHeight="400"
														text="#{sv.siteId}"
														action="#{siteVisitDetail.submitVisit}">
														<t:updateActionListener value="#{sv.id}"
															property="#{siteVisitDetail.visitId}" />
													</af:commandLink>
												</af:column>
												<%@ include file="visitColumns.jsp"%>
												<f:facet name="footer">
													<afh:rowLayout halign="center">
														<af:panelButtonBar>
															<af:commandButton useWindow="true" windowWidth="1000"
																windowHeight="600"
																text="Create and Associate Site Visit"
																action="#{fceSiteVisits.newAssocVisit}"
																shortDesc="Create a new visit that is associated with this Inspection"
																disabled="#{!fceDetail.cetaUpdate || fceDetail.inspectionDetailLocked}"
																rendered="#{!fceDetail.editable}">
																<t:updateActionListener
																	value="#{fceDetail.facility.fpId}"
																	property="#{fceSiteVisits.fpId}" />
																<t:updateActionListener
																	value="#{fceDetail.facility.facilityId}"
																	property="#{fceSiteVisits.facilityId}" />
																<t:updateActionListener value="#{fceDetail.fceId}"
																	property="#{fceSiteVisits.fceId}" />
																<t:updateActionListener
																	property="#{fceSiteVisits.fromFacility}" value="false" />
															</af:commandButton>
															<af:commandButton action="#{fceDetail.pickReassign}"
																useWindow="true" windowWidth="1000" windowHeight="700"
																rendered="#{!fceDetail.editable}"
																disabled="#{!fceDetail.cetaUpdate || fceDetail.fce.siteVisitsSize == '0' || fceDetail.inspectionDetailLocked}"
																shortDesc="#{fceDetail.associatedSiteVisitMsg}"
																text="Select Existing Site Visits To Associate" />
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
										<af:showDetailHeader text="Associated Stack Tests"
											disclosed="true">
											<af:table id="stackTestTable" emptyText=" " width="98%"
												value="#{fceDetail.fce.assocStackTests}" var="st"
												bandingInterval="1" banding="row">
												<af:column sortable="true" sortProperty="id"
													formatType="text" headerText="Stack Test ID">
													<af:commandLink text="#{st.stckId}"
														disabled="#{fceDetail.editable}"
														action="#{stackTestDetail.submitStackTest}">
														<af:setActionListener from="#{st.id}"
															to="#{stackTestDetail.id}" />
													</af:commandLink>
												</af:column>
												<jsp:include flush="true" page="../ceta/stackTestList.jsp" />
												<f:facet name="footer">
													<afh:rowLayout halign="center">
														<af:panelButtonBar>
															<af:commandButton text="Create and Associate Stack Test"
																useWindow="true" windowWidth="600" windowHeight="500"
																action="#{fceSiteVisits.newAssocStackTest}"
																shortDesc="Create a new stack test that is associated with this Inspection"
																disabled="#{!fceDetail.cetaUpdate || fceDetail.inspectionDetailLocked}"
																rendered="#{!fceDetail.editable}">
																<t:updateActionListener
																	value="#{fceDetail.facility.fpId}"
																	property="#{fceSiteVisits.fpId}" />
																<t:updateActionListener
																	value="#{fceDetail.facility.facilityId}"
																	property="#{fceSiteVisits.facilityId}" />
																<t:updateActionListener value="#{fceDetail.fceId}"
																	property="#{fceSiteVisits.fceId}" />
																<t:updateActionListener
																	property="#{stackTests.fromFacility}" value="false" />
															</af:commandButton>
															<af:commandButton action="#{fceDetail.pickReassignEt}"
																useWindow="true" windowWidth="1000" windowHeight="700"
																rendered="#{!fceDetail.editable}"
																disabled="#{!fceDetail.cetaUpdate || fceDetail.fce.stackTestsSize == '0' || fceDetail.inspectionDetailLocked}"
																shortDesc="#{fceDetail.associatedStackTestMsg}"
																text="Select Existing Stack Tests To Associate" />
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
										<af:objectSpacer height="5" />
										<f:subview id="doc_attachments"
											rendered="#{!fceDetail.blankOutPage && fceDetail.fce.id != null}">
											<jsp:include flush="true"
												page="fceAttachments.jsp" />
										</f:subview>
										<af:showDetailHeader text="Notes" disclosed="true"
											id="inspNotes" rendered="#{fceDetail.fce.id != null}">
											<jsp:include flush="true" page="notesInspectionTable.jsp" />
										</af:showDetailHeader>

										<af:objectSpacer height="15" />
										<af:panelForm rows="3" maxColumns="1"
											partialTriggers="dateEvaluated"
											onmouseover="triggerInspctionClassificationUpdate();">
											<afh:rowLayout halign="center">
												<af:panelButtonBar>
													<af:commandButton text="Edit" action="#{fceDetail.editFce}"
														disabled="#{!fceDetail.allowSchedEditOperations && !fceDetail.allowComplEditOperations || fceDetail.inspectionDetailLocked}"
														rendered="#{!fceDetail.editable}" />
													<af:commandButton text="Delete"
														action="#{fceDetail.fceDelete}" useWindow="true"
														windowWidth="600" windowHeight="300"
														rendered="#{fceDetail.allowDelete && !fceDetail.editable}" />
													<af:commandButton text="Save" action="#{fceDetail.save}"
														rendered="#{fceDetail.editable}" id="saveFce"
														onmouseover="triggerInspctionClassificationUpdate();" />
													<af:commandButton text="Cancel" id="cancelFce"
														action="#{fceDetail.cancelEdit}" immediate="true"
														rendered="#{fceDetail.editable}" />
													<af:commandButton text="Merge in Scheduled Information"
														action="#{fceDetail.mergeSched}" immediate="true"
														useWindow="true" windowWidth="900" windowHeight="600"
														rendered="#{fceDetail.showMergeSched}" />
													<af:commandButton text="Workflow Task"
														rendered="#{fceDetail.internalApp && fceDetail.fromTODOList  && ! fceDetail.editable}"
														action="#{fceDetail.goToCurrentWorkflow}" />
													<af:commandButton text="Preparing Inspection Report"
														id="prepareFceButton" action="#{fceDetail.prepareInspRpt}"
														rendered="#{fceDetail.internalApp && !fceDetail.editable && fceDetail.enhancedNonLegacyInspection && fceDetail.fce.reportStateInitial}"
														disabled="#{fceDetail.readOnlyUser}" useWindow="true"
														windowWidth="600" windowHeight="200" />
													<af:commandButton text="Inspection Report Completed"
														id="completeFceButton"
														action="#{fceDetail.completeInspRpt}"
														rendered="#{fceDetail.internalApp && !fceDetail.editable && fceDetail.enhancedNonLegacyInspection && fceDetail.fce.reportStatePrepare}"
														disabled="#{fceDetail.readOnlyUser}" useWindow="true"
														windowWidth="600" windowHeight="300" />
													<af:commandButton text="Inspection Report Finalized"
														id="finalizeFceButton"
														action="#{fceDetail.finalizeInspRpt}"
														rendered="#{fceDetail.internalApp && !fceDetail.editable && fceDetail.enhancedNonLegacyInspection && fceDetail.fce.reportStateComplete}"
														disabled="#{fceDetail.readOnlyUser || !fceDetail.inspectionFinalizer}"
														useWindow="true" windowWidth="600" windowHeight="300" />
												</af:panelButtonBar>
											</afh:rowLayout>
										</af:panelForm>
										<af:objectSpacer height="5" />
										<afh:rowLayout halign="center">
											<af:panelButtonBar>
												<af:commandButton id="viewAssocaitedInventory"
													text="Show Associated Facility Inventory"
													rendered="#{!fceDetail.editable && !fceDetail.fce.reportStateInitial && !fceDetail.fce.reportStatePrepare}"
													action="#{facilityProfile.submitProfileByFpId}">
													<t:updateActionListener property="#{facilityProfile.fpId}"
														value="#{fceDetail.fce.fpId}" />
													<t:updateActionListener
														property="#{menuItem_facProfile.disabled}" value="false" />
												</af:commandButton>
												
												<af:commandButton id="associateWithCurrentBtn"
													text="Associate with Current Facility Inventory"
													rendered="#{!fceDetail.editable && fceDetail.fce.reportStatePrepare}"
													disabled="#{fceDetail.readOnlyUser}"
													useWindow="true" windowWidth="#{confirmWindow.width}"  windowHeight="#{confirmWindow.height}"
													action="#{confirmWindow.confirm}">
													<t:updateActionListener property="#{confirmWindow.type}"
					                					value="#{confirmWindow.yesNo}" />
					                				<t:updateActionListener property="#{confirmWindow.message}"
					                				value="This operation will associate this inspection report with 
		              										the current version of the facility inventory. Click Yes to proceed 
		              										or No to cancel."/>
		              								<t:updateActionListener property="#{confirmWindow.method}"
					                					value="fceDetail.associateRptWithCurrentFacility" />	
												</af:commandButton>
																						
												<af:commandButton id="viewFacility"
													text="Show Current Facility Inventory"
													rendered="#{!fceDetail.editable}"
													action="#{facilityProfile.submitProfileById}">
													<t:updateActionListener
														property="#{facilityProfile.facilityId}"
														value="#{fceDetail.facilityId}" />
													<t:updateActionListener
														property="#{menuItem_facProfile.disabled}" value="false" />
												</af:commandButton>
												<af:commandButton id="viewHistoryInfo"
													text="View Air Program History Info"
													rendered="#{!fceDetail.editable && fceDetail.fce.facilityHistId != null}"
													action="#{fceSiteVisits.viewHistory}" useWindow="true"
													windowWidth="750" windowHeight="900">
													<t:updateActionListener
														property="#{fceSiteVisits.facilityHistory}"
														value="#{fceDetail.fce.facilityHistory}" />
													<t:updateActionListener property="#{fceSiteVisits.afsId}"
														value="#{fceDetail.fce.evalAfsId}" />
													<t:updateActionListener
														property="#{fceSiteVisits.histFacility}"
														value="#{fceDetail.facility}" />
												</af:commandButton>
												<af:commandButton text="go to Tools-->Generate Document"
													action="#{fceDetail.generateLetter}" immediate="true"
													rendered="#{!fceDetail.editable && false}" />
												<af:commandButton id="backToSummary"
													text="Show Facility Inspections"
													rendered="#{!fceDetail.editable}"
													action="#{fceDetail.goToSummaryPage}">
													<t:updateActionListener
														property="#{menuItem_facProfile.disabled}" value="false" />
												</af:commandButton>
												<af:commandButton text="Create Enforcement Action" id="startNewEnforcement"
													disabled="#{!enforcementActionDetail.enforcementActionCreateAllowed}"
													rendered="#{!fceDetail.editable && fceDetail.internalApp}"
													action="#{enforcementActionDetail.startNewEnforcementAction}">
													<t:updateActionListener
														property="#{enforcementActionDetail.facilityId}"
														value="#{fceDetail.facility.facilityId}" />
													<t:updateActionListener
														property="#{enforcementActionDetail.fromInspection}"
														value="true" />
												</af:commandButton>
												<af:commandButton immediate="true"
													text="Generate Inspection Report" useWindow="true"
													windowWidth="600" windowHeight="500"
													shortDesc="Button will be disabled for 'Legacy' / 'Pre #{fceDetail.fce.version10InstallDate}' Inspections and when the report is in 'Initial', 'Dead-ended' or 'Finalized' state."
													disabled="#{!fceDetail.allowedToGenerateInspectionReport}"
													rendered="#{fceDetail.internalApp && !fceDetail.editable}"
													action="#{fceDetail.displayGenerateInspReportPopup}">
												</af:commandButton>
												<af:commandButton immediate="true"
													text="Generate Inspection Info Doc" useWindow="true"
													windowWidth="500" windowHeight="300"
													disabled="#{fceDetail.readOnlyUser}"
													rendered="#{fceDetail.internalApp && !fceDetail.editable}"
													action="#{fceDetail.generateInspectionInfoDocument}">
												</af:commandButton>
											</af:panelButtonBar>
										</afh:rowLayout>
									</af:panelForm>
								</afh:rowLayout>
							</af:panelForm>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
			<%-- hidden controls for navigation to compliance report from popup --%>
			<%@ include file="../util/hiddenControls.jsp"%>
		</af:form>
		<f:verbatim>
			<script type="text/javascript">
				function triggerInspctionClassificationUpdate(){
					if(document.activeElement.id=="eval:dateEvaluated"){
			    		input = document.getElementById("eval:dateEvaluated");
			    		input.blur();
					} 
	
				}
			</script>
		</f:verbatim>
	</af:document>
</f:view>
