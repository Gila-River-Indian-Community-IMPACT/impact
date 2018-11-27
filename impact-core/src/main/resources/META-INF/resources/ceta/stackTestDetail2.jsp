<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<af:panelGroup layout="vertical">
	<af:panelBox background="light" width="100%"
		rendered="#{!stackTestDetail.error}">
		<af:panelForm rows="2" maxColumns="3" width="100%">
			<af:inputText label="Facility ID:" readOnly="True" id="StackTestDetail_FacilityID"
				value="#{stackTestDetail.facility.facilityId}" />
			<af:inputText label="Facility Name:" readOnly="true" id="StackTestDetail_FacilityName"
				value="#{stackTestDetail.facility.name}" />
			<af:selectOneChoice label="Facility Class:" id="StackTestDetail_FacilityClass"
				value="#{stackTestDetail.facility.permitClassCd}" readOnly="true">
				<f:selectItems
					value="#{facilityReference.permitClassDefs.items[(empty stackTestDetail.facility.permitClassCd ? '' : stackTestDetail.facility.permitClassCd)]}" />
			</af:selectOneChoice>
			<af:inputText label="Facility Type:" readOnly="True" id="StackTestDetail_FacilityType"
				value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty stackTestDetail.facility.facilityTypeCd ? '' : stackTestDetail.facility.facilityTypeCd)]}" />
			<af:inputText label="Stack Test ID:" readOnly="true" id="StackTestDetail_STID"
				value="#{stackTestDetail.stackTest.stckId}" />
			<af:inputText label="Associated Inspection ID:" readOnly="true" id="StackTestDetail_AIID"
				value="#{stackTestDetail.stackTest.inspId}" />
			<%--
		<af:inputText label="AFS:" id="afs" rendered="#{stackTestDetail.stars2Admin}"
			value="#{stackTestDetail.stackTest.afsLocked?'Locked':' '}#{stackTestDetail.stackTest.afsPartialLocked}"
			readOnly="true"
			inlineStyle="#{stackTestDetail.stackTest.afsPartialLocked != null?'color: orange; font-weight: bold;':''}" />
		<af:outputText value=" " rendered="#{!stackTestDetail.stars2Admin}"/>
		--%>
			<af:inputText label="Test State:" readOnly="true" id="StackTestDetail_TestState" rendered="#{!stackTestDetail.publicApp}" 
				value="#{compEvalDefs.emissionsTestStateDef.itemDesc[(empty stackTestDetail.stackTest.emissionTestState ? '' : stackTestDetail.stackTest.emissionTestState)]}" />
			<af:selectInputDate label="Reminder Date:" id="StackTestDetail_remDate"
				readOnly="true"
				rendered="#{stackTestDetail.stackTest.emissionTestState == 'rmdr'}"
				value="#{stackTestDetail.stackTest.reminderDate}">
			</af:selectInputDate>
		</af:panelForm>
	</af:panelBox>
	<af:objectSpacer height="5" />
	<afh:rowLayout halign="center">
		<af:selectOneRadio id="StackTestDetail_legacyStackTest"
			label="Is this a legacy stack test? :"
			readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}"
			rendered="#{stackTestDetail.internalApp}"
			layout="horizontal" value="#{stackTestDetail.stackTest.legacyFlag}">
			<f:selectItem itemLabel="Yes" itemValue="true" id="StackTestDetail_legacyYes"/>
			<f:selectItem itemLabel="No" itemValue="false" id="StackTestDetail_legacyNo"/>
		</af:selectOneRadio>
	</afh:rowLayout>
	<af:objectSpacer height="5" />
	<af:panelForm width="100%"
		partialTriggers="StackTestDetail_STMethod tab:selected testResults">
		<h:panelGrid columns="1" rendered="#{!stackTestDetail.error}"
			border="0">
			<af:panelForm rows="1" maxColumns="4" width="100%">
				<af:selectInputDate label="Scheduled Date:" id="StackTestDetail_schedDate"
					readOnly="#{ !stackTestDetail.editable || (stackTestDetail.locked && !stackTestDetail.general) }"
					rendered="#{stackTestDetail.internalApp || (!stackTestDetail.internalApp && stackTestDetail.stackTest.emissionTestState == 'sbmt') }"
					value="#{stackTestDetail.stackTest.dateScheduled}">
					<af:validateDateTimeRange minimum="1900-01-01" />
				</af:selectInputDate>
				<af:selectInputDate label="Date Results Received:" id="StackTestDetail_dtRec"
					readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}"
					rendered="#{stackTestDetail.internalApp || (!stackTestDetail.internalApp && stackTestDetail.stackTest.emissionTestState == 'sbmt') }"
					value="#{stackTestDetail.stackTest.dateReceived}">
					<af:validateDateTimeRange maximum="#{stackTestDetail.maxDate}" />
				</af:selectInputDate>
				<af:selectInputDate id="StackTestDetail_dateEvaluatedDt"
					label="Date Results Reviewed:"
					readOnly="#{!stackTestDetail.editable}"
					rendered="#{stackTestDetail.internalApp || (!stackTestDetail.internalApp && stackTestDetail.stackTest.emissionTestState == 'sbmt')}"
					value="#{stackTestDetail.stackTest.dateEvaluated}">
					<af:validateDateTimeRange maximum="#{stackTestDetail.maxDate}" />
				</af:selectInputDate>
				<af:selectOneChoice id="StackTestDetail_reviewer" label="Reviewer:" 
					readOnly="#{!stackTestDetail.editable}" unselectedLabel=""
					rendered="#{stackTestDetail.internalApp || (stackTestDetail.portalApp && stackTestDetail.stackTest.emissionTestState == 'sbmt')}"
					value="#{stackTestDetail.stackTest.reviewer}">
					<f:selectItems
						value="#{infraDefs.basicUsersDef.items[(empty stackTestDetail.stackTest.reviewer?0:stackTestDetail.stackTest.reviewer)]}" />
				</af:selectOneChoice>
			</af:panelForm>
			<%--   
			----------------------------------------------------------------------
			CAUTION - sorting is intentionally disabled for the Test Date(s) and Witnesses tables. Allowing sorting,
			even in non-Edit mode causes the results to be unreliable.
			----------------------------------------------------------------------
			--%>
			<af:showDetailHeader id="testPanel" disclosed="true"
				text="Stack Test Information">
				<afh:rowLayout halign="left" width="100%">
					<h:panelGrid columns="3" width="99%">
						<afh:cellFormat width="24%" halign="left">
							<afh:rowLayout width="98%">
								<afh:cellFormat halign="left" valign="top" width="100%">
									<afh:rowLayout width="100%">
										<af:table id="StackTest_TestDateTable" emptyText=" " width="100%"
											value="#{stackTestDetail.stackTest.visitDates}" var="svD"
											bandingInterval="1" banding="row">
											<af:column sortable="false"
												rendered="#{stackTestDetail.editable && !stackTestDetail.locked}"
												formatType="icon" headerText="Select">
												<af:selectBooleanCheckbox value="#{svD.selected}"
													readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}" />
											</af:column>
											<af:column sortable="false" formatType="text"
												headerText="Test Date(s)">
												<af:selectInputDate
													readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}"
													value="#{svD.testDate}" autoSubmit="true">
													<af:validateDateTimeRange minimum="1900-01-01" />
												</af:selectInputDate>
											</af:column>
											<f:facet name="footer">
												<afh:rowLayout halign="center">
													<af:panelButtonBar>
														<af:commandButton action="#{stackTestDetail.addTestDate}" id="StackTestDetail_AddTestDate"
															disabled="#{stackTestDetail.locked}"
															rendered="#{stackTestDetail.editable}"
															shortDesc="Click to add another date.  Blank out name to remove it."
															text="Add Date" />
														<af:commandButton action="#{stackTestDetail.deleteDates}" id="StackTestDetail_DeleteTestDate"
															disabled="#{stackTestDetail.locked}"
															rendered="#{stackTestDetail.editable}"
															shortDesc="Click to remove selected dates."
															text="Delete Selected Dates" />
													</af:panelButtonBar>
												</afh:rowLayout>
											</f:facet>
										</af:table>
									</afh:rowLayout>
								</afh:cellFormat>
							</afh:rowLayout>
						</afh:cellFormat>
						<%-- <af:objectSpacer height="5" width="20" />--%>
						<afh:cellFormat valign="top" width="50%">
							<afh:rowLayout width="98%">
								<afh:cellFormat halign="left" valign="top" width="100%">
									<afh:rowLayout width="100%">
										<af:panelForm>
											<af:inputText label="Company Conducting Test:" id="StackTestDetail_CompanyConductingTest"
												readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}"
												value="#{stackTestDetail.stackTest.company}"
												maximumLength="100" columns="30" />
											<af:selectOneChoice id="StackTestDetail_Audits"
												unselectedLabel="" label="Audits:"
												readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}"
												value="#{stackTestDetail.stackTest.auditsCd}">
												<f:selectItems
													value="#{compEvalDefs.stackTestAuditsDef.items[(empty stackTestDetail.stackTest.auditsCd ? '' : stackTestDetail.stackTest.auditsCd)]}" />
											</af:selectOneChoice>
											<af:selectOneChoice id="StackTestDetail_Category"
												unselectedLabel="" label="Category:"
												readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}"
												value="#{stackTestDetail.stackTest.categoryCd}">
												<f:selectItems
													value="#{compEvalDefs.stackTestCategoryDef.items[(empty stackTestDetail.stackTest.categoryCd ? '' : stackTestDetail.stackTest.categoryCd)]}" />
											</af:selectOneChoice>
											<af:selectOneChoice id="StackTestDetail_TestingMethod"
												unselectedLabel="" label="Testing Method:"
												readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}"
												value="#{stackTestDetail.stackTest.testingMethodCd}">
												<f:selectItems
													value="#{compEvalDefs.stackTestTestingMethodDef.items[(empty stackTestDetail.stackTest.testingMethodCd ? '' : stackTestDetail.stackTest.testingMethodCd)]}" />
											</af:selectOneChoice>
										</af:panelForm>
									</afh:rowLayout>
								</afh:cellFormat>
							</afh:rowLayout>
						</afh:cellFormat>
						<afh:cellFormat width="24%" halign="left">
							<afh:rowLayout width="98%">
								<afh:cellFormat halign="left" valign="top" width="100%">
									<afh:rowLayout width="100%"
										rendered="#{(stackTestDetail.internalApp && (stackTestDetail.stackTest.witnessesExist || stackTestDetail.editable)) || (stackTestDetail.portalApp && stackTestDetail.stackTest.emissionTestState == 'sbmt' && stackTestDetail.stackTest.witnessesExist) }">
										<af:table id="stTable" emptyText=" " width="100%"
											value="#{stackTestDetail.stackTest.witnesses}" var="ev"
											bandingInterval="1" banding="row">
											<af:column sortable="false"
												rendered="#{stackTestDetail.editable && !stackTestDetail.locked}"
												formatType="icon" headerText="Select">
												<af:selectBooleanCheckbox value="#{ev.selected}"
													readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}" />
											</af:column>
											<af:column sortable="false" formatType="text"
												headerText="Witness(es)">
												<af:selectOneChoice unselectedLabel=""
													value="#{ev.evaluator}"
													readOnly="#{ !stackTestDetail.editable || (stackTestDetail.locked && !stackTestDetail.general) }">
													<f:selectItems
														value="#{empty ev.evaluator ? stackTestDetail.stBasicUsersDef.items[0] : infraDefs.basicUsersDef.items[ev.evaluator]}" />
												</af:selectOneChoice>
											</af:column>
											<f:facet name="footer">
												<afh:rowLayout halign="center">
													<af:panelButtonBar>
														<af:commandButton id="StackTestDetail_AddWitness"
															action="#{stackTestDetail.addVisitEvaluator}"
															disabled="#{stackTestDetail.locked}"
															rendered="#{stackTestDetail.editable}"
															shortDesc="Click to add another person.  Blank out name to remove it."
															text="Add Witness" />
														<af:commandButton id="StackTestDetail_DeleteWitness"
															action="#{stackTestDetail.deleteEvaluators}"
															disabled="#{stackTestDetail.locked}"
															rendered="#{stackTestDetail.editable}"
															shortDesc="Click to remove selected persons."
															text="Delete Selected Witness(es)" />
													</af:panelButtonBar>
												</afh:rowLayout>
											</f:facet>
										</af:table>
									</afh:rowLayout>
									<afh:rowLayout halign="left"
										rendered="#{(stackTestDetail.internalApp && (!stackTestDetail.stackTest.witnessesExist && !stackTestDetail.editable)) || (stackTestDetail.portalApp && stackTestDetail.stackTest.emissionTestState == 'sbmt' && !stackTestDetail.stackTest.witnessesExist) }">
										<af:outputLabel value="Not Witnessed" />
									</afh:rowLayout>
								</afh:cellFormat>
							</afh:rowLayout>
						</afh:cellFormat>
					</h:panelGrid>
				</afh:rowLayout>
			</af:showDetailHeader>
			<af:showDetailHeader id="methodPanel" disclosed="true"
				text="Stack Test Method">
				<afh:rowLayout halign="left" width="100%">
					<afh:cellFormat halign="left" valign="top" width="98%">
						<af:panelForm maxColumns="2">
							<afh:rowLayout halign="left" width="100%">
								<af:selectOneChoice id="StackTestDetail_STMethod"
									label="Stack Test Method:" unselectedLabel="" autoSubmit="true"
									readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}"
									value="#{stackTestDetail.stackTestMethodCd}">
									<f:selectItems
										value="#{compEvalDefs.stackTestMethodDef.items[(empty stackTestDetail.stackTestMethodCd ? '' : stackTestDetail.stackTestMethodCd)]}" />
								</af:selectOneChoice>
								<af:selectOneChoice label="Conformed to Test Method:" id="StackTestDetail_ConformedToTestMethod"
									readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}"
									value="#{stackTestDetail.stackTest.conformedToTestMethod}"
									unselectedLabel="">
									<f:selectItem itemLabel="Yes" itemValue="Y" />
									<f:selectItem itemLabel="No" itemValue="N" />
								</af:selectOneChoice>
							</afh:rowLayout>
							<afh:rowLayout halign="left" width="100%">
								<af:inputText
									value="Add description in Memo about this test method -- other"
									inlineStyle="color: orange; font-weight: bold;" readOnly="true"
									rendered="#{stackTestDetail.stackTest.methodChangedToOther}" />
							</afh:rowLayout>
						</af:panelForm>
					</afh:cellFormat>
				</afh:rowLayout>
				<afh:rowLayout halign="left" width="100%">
					<af:panelForm>
						<af:table id="tab" emptyText=" "
							value="#{stackTestDetail.stackTest.allMethodPollutants}" var="p"
							bandingInterval="1" width="98%" banding="row">
							<af:column sortable="true" sortProperty="selected"
								formatType="icon" headerText="Tested" width="8%">
								<af:selectBooleanCheckbox
									readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}"
									value="#{p.selected}" id="selected" autoSubmit="true" />
							</af:column>
							<af:column formatType="icon"
								headerText="Method Pollutant Choices">
								<af:column sortable="true" sortProperty="pollutantCd"
									formatType="number" headerText="Code" width="20%">
									<af:inputText value="#{p.pollutantCd}" readOnly="true" id="StackTestDetail_MethodCode"/>
								</af:column>
								<af:column sortable="true" sortProperty="pollutantDsc"
									headerText="Description" width="70%">
									<af:inputText value="#{p.pollutantDsc}" readOnly="true" id="StackTestDetail_MethodDesc"/>
									<af:inputText value=" (deprecated)"
										inlineStyle="color: orange; font-weight: bold;" readOnly="true"
										rendered="#{p.deprecated}" />
								</af:column>
							</af:column>
						</af:table>
						<af:inputText value=" " readOnly="true"
							rendered="#{!stackTestDetail.editable}" />
					</af:panelForm>
				</afh:rowLayout>
			</af:showDetailHeader>
			<af:showDetailHeader id="euPanel" disclosed="true"
				text="Emissions Units Tested/Processes Tested">
				<afh:rowLayout halign="left" valign="top" width="100%">
					<af:table id="eqtTable" emptyText=" "
						partialTriggers="eqtTable:sel"
						rendered="#{!stackTestDetail.error}"
						value="#{stackTestDetail.stackTest.testedEmissionsUnits}" var="em"
						bandingInterval="1" width="98%" banding="row">
						<af:column sortable="true" sortProperty="selected"
							rendered="#{stackTestDetail.editable && !stackTestDetail.locked}"
							formatType="icon" headerText="Select" width="8%">
							<af:selectBooleanCheckbox id="sel" value="#{em.selected}"
								autoSubmit="true"
								readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}" />
						</af:column>
						<af:column sortable="true" sortProperty="epaEmuId"
							formatType="text" headerText="Emissions Unit" width="10%">
							<af:commandLink disabled="#{stackTestDetail.editable}"
								action="#{facilityProfile.submitFacEmissionUnit}">
								<af:outputText value="#{em.epaEmuId}" />
								<t:updateActionListener property="#{facilityProfile.epaEmuId}"
									value="#{em.epaEmuId}" />
								<t:updateActionListener property="#{facilityProfile.fpId}"
									value="#{stackTestDetail.stackTest.fpId}" />
							</af:commandLink>
						</af:column>
						<af:column sortable="true" sortProperty="sccs" formatType="number"
							headerText="SCC ID" width="10%">
							<af:inputText readOnly="true" value="#{em.sccs}" id="StackTestDetail_SCCID"/>
						</af:column>
						<af:column sortable="true" sortProperty="opStatus"
							formatType="text" headerText="EU Operating Status" width="10%">
							<af:inputText readOnly="true" value="#{em.opStatus}" id="StackTestDetail_EUOperatingStatus"
								inlineStyle="#{(em.shutdownDate!=null) ? 'color: orange; font-weight: bold;' : '' }" />
							<af:selectInputDate value="#{em.shutdownDate}" id="StackTestDetail_EUStatusShutdown"
								inlineStyle="color: orange; font-weight: bold;" readOnly="true"
								rendered="#{em.shutdownDate != null}">
							</af:selectInputDate>
						</af:column>
						<af:column sortable="true" sortProperty="description"
							formatType="text" headerText="Emissions Unit Description"
							width="20%">
							<af:outputText value="#{em.description}" truncateAt="35" shortDesc="#{em.description}" id="StackTestDetail_EUDesc"/>
							
						</af:column>
						<%--
							<af:column rendered="#{stackTestDetail.editable}">
								<af:commandButton text="Add/Delete SCCs"
									shortDesc="Click to add additional SCCs or delete SCCs."
									action="#{stackTestDetail.editSccs}" useWindow="true"
									windowWidth="500" windowHeight="600">
									<af:setActionListener from="#{em.epaEmuId}"
										to="#{stackTestDetail.selectedEpaEmuId}" />
								</af:commandButton>
							</af:column>
							--%>
						<af:column sortable="true" sortProperty="processDescription"
							formatType="text" headerText="Company Process Description"
							width="20%">
							<af:inputText readOnly="true" value="#{em.processDescription}" id="StackTestDetail_CompanyProcessDesc"/>
						</af:column>
						<af:column sortable="true" sortProperty="controlEquipment"
							headerText="Associated Control Equipment" width="20%">
							<af:inputText value="#{em.controlEquipment}" readOnly="true" id="StackTestDetail_AssociatedCE"/>
						</af:column>
						<f:facet name="footer">
							<afh:rowLayout halign="center"
								rendered="#{stackTestDetail.editable}">
								<af:commandButton text="Add Emissions Units/SCCs" id="StackTestDetail_AddEUSSCCs"
									disabled="#{stackTestDetail.locked}" useWindow="true"
									windowWidth="1000" windowHeight="450"
									action="#{stackTestDetail.lookupFacility}" />
								<af:objectSpacer width="10" height="5" />
								<af:commandButton id="StackTestDetail_DeleteEUSCCs"
									disabled="#{stackTestDetail.locked || !stackTestDetail.testedEuSelected}"
									action="#{stackTestDetail.deleteTestedEmissionsUnits}"
									rendered="#{stackTestDetail.editable}"
									shortDesc="Click to remove selected Emissions Units."
									text="Delete Selected Emissions Unit(s)" />
							</afh:rowLayout>
						</f:facet>
					</af:table>
				</afh:rowLayout>
				<afh:rowLayout width="100%">
					<afh:cellFormat halign="left">
						<afh:rowLayout halign="left" width="100%">
							<h:panelGrid columns="1">
								<afh:cellFormat halign="left" valign="top">
									<af:panelForm maxColumns="1">
										<af:outputLabel value="Control Equipment Used:" />
										<afh:rowLayout halign="left">
											<af:inputText id="StackTestDetail_CEUsed" label="" maximumLength="4000"
												columns="180" rows="3"
												readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}"
												value="#{stackTestDetail.stackTest.controlEquipUsed}" />
										</afh:rowLayout>
										<af:commandButton rendered="false"
											disabled="#{stackTestDetail.locked}"
											text="Edit Field & Optionally Copy Process Control Equipment Info From Profile"
											action="#{stackTestDetail.copyControlInfo}" useWindow="true"
											windowWidth="600" windowHeight="500" />
										<af:outputLabel
											value="Note: One or more of the selected processes has no control equipment specified in the facility inventory."
											rendered="#{stackTestDetail.controlEquipsMissing && stackTestDetail.editable}" />
									</af:panelForm>
								</afh:cellFormat>
							</h:panelGrid>
						</afh:rowLayout>
					</afh:cellFormat>
				</afh:rowLayout>
				<afh:rowLayout halign="left" width="100%">
					<afh:cellFormat halign="left">
						<afh:rowLayout>
							<h:panelGrid columns="1">
								<afh:cellFormat halign="left" valign="top">
									<af:panelForm maxColumns="1">
										<af:outputLabel value="Monitoring Equipment Used:" />
										<afh:rowLayout halign="left">
											<af:inputText id="StackTestDetail_MEUsed" label="" maximumLength="4000"
												columns="180" rows="3"
												readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}"
												value="#{stackTestDetail.stackTest.monitoringEquip}" />
										</afh:rowLayout>
									</af:panelForm>
								</afh:cellFormat>
							</h:panelGrid>
						</afh:rowLayout>
					</afh:cellFormat>
				</afh:rowLayout>
			</af:showDetailHeader>
			<%--    
			------------------------------------------------------------------------------
			CAUTION - sorting is intentionally disabled for this table. Allowing sorting,
			even in non-Edit mode causes the results to be unreliable.
			------------------------------------------------------------------------------
			--%>
			<af:showDetailHeader id="resultsPanel" disclosed="true"
				text="Stack Test Results">
				<af:objectSpacer height="10" />
				<af:panelForm>
					<afh:rowLayout halign="left" valign="top" width="100%">
						<af:table width="100%" emptyText=" "
							value="#{stackTestDetail.stackTest.testedPollutantsWrapper}"
							binding="#{stackTestDetail.stackTest.testedPollutantsWrapper.table}"
							bandingInterval="1" banding="row" var="pol"
							id="TestedPollutantsTab" rows="#{stackTestDetail.pageLimit}">
							<af:column sortable="false" sortProperty="epaEmuId"
								formatType="text" headerText="Emissions Unit" noWrap="true">
								<af:commandLink disabled="#{stackTestDetail.editable}"
									action="#{facilityProfile.submitFacEmissionUnit}">
									<af:outputText value="#{pol.epaEmuId}" />
									<t:updateActionListener property="#{facilityProfile.epaEmuId}"
										value="#{pol.epaEmuId}" />
									<t:updateActionListener property="#{facilityProfile.fpId}"
										value="#{stackTestDetail.stackTest.fpId}" />
								</af:commandLink>
								<af:objectSpacer width="3" height="3" />
								<%-- 
								<af:objectImage source="/images/Lock_icon1.png"
									rendered="#{pol.afsId!=null}" />
								--%>
							</af:column>
							<af:column sortable="false" sortProperty="sccId"
								formatType="number" headerText="SCC ID" noWrap="true">
								<af:inputText maximumLength="8" columns="8" readOnly="true"
									value="#{pol.sccId}" />
							</af:column>
							<af:column formatType="text" headerText="Pollutant">
								<af:column sortable="false" sortProperty="pollutantCd"
									formatType="number" headerText="Code">
									<af:inputText value="#{pol.pollutantCd}" readOnly="true" />
								</af:column>
								<af:column formatType="text" sortable="false"
									sortProperty="pollutantDsc" headerText="Description">
									<af:inputText value="#{pol.pollutantDsc}" readOnly="true" />
									<af:inputText value=" (deprecated)"
										inlineStyle="color: orange; font-weight: bold;" readOnly="true"
										rendered="#{pol.deprecated}" />
								</af:column>
							</af:column>
							<af:column formatType="text" headerText="Permitted">
								<af:column formatType="text" sortable="false"
									sortProperty="permitAllowRate"
									headerText="Allowable Emission Rate">
									<af:inputText value="#{pol.permitAllowRate}" maximumLength="48"
										columns="24"
										readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}" />
								</af:column>
								<af:column formatType="text" sortable="false"
									sortProperty="permitMaxRate"
									headerText="Maximum Operating Rate">
									<af:inputText value="#{pol.permitMaxRate}" maximumLength="48"
										columns="24"
										readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}" />
								</af:column>
							</af:column>
							<af:column formatType="text" headerText="Tested">
								<af:column formatType="text" sortable="false"
									sortProperty="testRate" headerText="Average Emission Rate">
									<af:inputText value="#{pol.testRate}" maximumLength="48"
										columns="24"
										readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}" />
								</af:column>
								<af:column formatType="text" sortable="false"
									sortProperty="testAvgOperRate"
									headerText="Average Operating Rate">
									<af:inputText value="#{pol.testAvgOperRate}" maximumLength="48"
										columns="24"
										readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}" />
								</af:column>
								<af:column sortable="false" sortProperty="stackTestResultsCd"
									formatType="text" headerText="Results">
									<af:selectOneChoice unselectedLabel="" id="testResults"
										inlineStyle="#{pol.stackTestResultsCd=='FF'?'color: orange; font-weight: bold;':''}"
										readOnly="#{!stackTestDetail.editable || stackTestDetail.locked}"
										autoSubmit="true" value="#{pol.stackTestResultsCd}">
										<f:selectItems
											value="#{compEvalDefs.stackTestResultsDef.items[(empty pol.stackTestResultsCd ? '' : pol.stackTestResultsCd)]}" />
									</af:selectOneChoice>
								</af:column>
							</af:column>
							<%--
					<af:column formatType="icon" headerText="AFS Information"
						rendered="#{stackTestDetail.stars2Admin}">
						<af:column sortable="true" sortProperty="afsSentDate"
							headerText="Date">
							<af:selectInputDate readOnly="#{!stackTestDetail.editable}"
								inlineStyle="#{stackTestDetail.stars2Admin?'color: orange; font-weight: bold;':''}"
								value="#{pol.afsSentDate}">
								<af:validateDateTimeRange minimum="1900-01-01" />
							</af:selectInputDate>
						</af:column>
						<af:column sortable="true" sortProperty="afsId" headerText="ID">
							<af:inputText value="#{pol.afsId}" maximumLength="8" columns="8"
								readOnly="#{!stackTestDetail.editable}"
								inlineStyle="#{stackTestDetail.stars2Admin?'color: orange; font-weight: bold;':''}" />
						</af:column>
					</af:column>
					--%>
						</af:table>
					</afh:rowLayout>
				</af:panelForm>
				<afh:rowLayout halign="left" width="100%">
					<afh:cellFormat halign="left">
						<afh:rowLayout>
							<h:panelGrid columns="1">
								<afh:cellFormat halign="left" valign="top">
									<af:panelForm maxColumns="1">
										<af:outputLabel value="Memo" />
										<afh:rowLayout halign="left">
											<af:inputText id="StackTestDetail_Memo" label=""
												columns="180" rows="#{stackTestDetail.memoRows}"
												readOnly="#{!stackTestDetail.editable && !stackTestDetail.memoEditable || stackTestDetail.locked}"
												value="#{stackTestDetail.stackTest.memo}" />
										</afh:rowLayout>
									</af:panelForm>
								</afh:cellFormat>
							</h:panelGrid>
						</afh:rowLayout>
					</afh:cellFormat>
				</afh:rowLayout>
			</af:showDetailHeader>
			<af:objectSpacer height="5" />
			<f:subview id="doc_attachments"
				rendered="#{!stackTestDetail.error && stackTestDetail.stackTest.id != null}">
				<jsp:include flush="true" page="stackTestDocAttachments.jsp" />
			</f:subview>
			<af:showDetailHeader text="Notes" disclosed="true"
				rendered="#{!stackTestDetail.error && stackTestDetail.stackTest.id != null && stackTestDetail.internalApp}"
				id="stackTestNotes">
				<jsp:include flush="true" page="notesStackTestTable.jsp" />
			</af:showDetailHeader>
		</h:panelGrid>
		<af:objectSpacer height="5" />
		<afh:rowLayout halign="center" rendered="#{!stackTestDetail.error}">
			<af:panelButtonBar>
				<af:commandButton text="Edit" action="#{stackTestDetail.edit}" id="StackTestDetail_EditBtn"
					disabled="#{!stackTestDetail.allowEditOperations}"
					rendered="#{(stackTestDetail.internalApp && ! stackTestDetail.editable && !stackTestDetail.memoEditable)
						|| (stackTestDetail.portalApp && ! stackTestDetail.editable && !stackTestDetail.memoEditable && !stackTestDetail.viewOnly)}"/>
				<af:commandButton text="Assign to District Engineer" id="StackTestDetail_AssignBtn"
					action="#{stackTestDetail.assignStackTest}" useWindow="true"
					windowWidth="500" windowHeight="300"
					disabled="#{!stackTestDetail.allowEditOperations}"
					rendered="#{! stackTestDetail.editable && !stackTestDetail.memoEditable && !stackTestDetail.readOnlyUser && stackTestDetail.internalApp}" />
				<af:commandButton text="Edit Memo" id="StackTestDetail_EditMemoBtn"
					action="#{stackTestDetail.editMemo}"
					rendered="#{stackTestDetail.showMemoEditable && ! stackTestDetail.editable && !stackTestDetail.memoEditable}" />
				<af:commandButton text="Delete" id="StackTestDetail_DeleteBtn"
					disabled="#{!stackTestDetail.allowEditOperations || stackTestDetail.locked || stackTestDetail.activeWorkflowProcess }"
					action="#{stackTestDetail.requestDelete}" useWindow="true"
					windowWidth="500" windowHeight="300"
					shortDesc="#{!stackTestDetail.activeWorkflowProcess ? 'Delete' : 
	                	'Delete is disabled because there is an active workflow for this Stack Test. If you need to delete this Stack Test, please first cancel the associated workflow.'}"
					rendered="#{(!stackTestDetail.admin && ! stackTestDetail.editable && !stackTestDetail.memoEditable && stackTestDetail.internalApp && stackTestDetail.stackTest.emissionTestState != 'sbmt') ||
					(stackTestDetail.admin && ! stackTestDetail.editable && !stackTestDetail.memoEditable && stackTestDetail.internalApp && !stackTestDetail.submittedFromPortal)}" />
				<af:commandButton text="Validate" id="StackTestDetail_ValidateBtn"
					action="#{stackTestDetail.validate}"
					disabled="#{stackTestDetail.readOnlyUser}"
					rendered="#{(stackTestDetail.internalApp && ! stackTestDetail.editable && !stackTestDetail.memoEditable && stackTestDetail.stackTest.emissionTestState != 'sbmt') 
					|| (stackTestDetail.portalApp && ! stackTestDetail.editable && !stackTestDetail.memoEditable && !stackTestDetail.viewOnly)}" />
				<af:commandButton text="Submit"  id="StackTestDetail_SubmitBtn"
					action="#{stackTestDetail.toSubmitState}" useWindow="true"
					windowWidth="500" windowHeight="300"
					disabled="#{!stackTestDetail.allowEditOperations}"
					rendered="#{! stackTestDetail.editable && !stackTestDetail.memoEditable && !stackTestDetail.readOnlyUser && stackTestDetail.validatedSuccessfully && stackTestDetail.stackTest.emissionTestState != 'sbmt' && stackTestDetail.internalApp}" />
				<af:commandButton id="StackTestDetail_ExtSubmitBtn" text="Submit"
	              	disabled="#{!myTasks.impactFullEnabled || !myTasks.hasSubmit}"
	                rendered="#{! stackTestDetail.editable && !stackTestDetail.memoEditable && !stackTestDetail.readOnlyUser && stackTestDetail.validatedSuccessfully && stackTestDetail.stackTest.emissionTestState != 'sbmt' && !stackTestDetail.viewOnly && stackTestDetail.portalApp}"
	                action="#{stackTestDetail.applySubmitFromPortal}"
	                useWindow="true" windowWidth="#{submitTask.attestWidth}"
	                windowHeight="#{submitTask.attestHeight}" 
	                shortDesc="#{myTasks.hasSubmit ? '' : 
	                	'Submit'}">
	                <t:updateActionListener property="#{submitTask.type}"
	                  value="#{submitTask.yesNo}" />
	                <t:updateActionListener property="#{submitTask.task}"
	                  value="#{stackTestDetail.task}" />
	              </af:commandButton>
				<af:commandButton text="Save" id="StackTestDetail_SaveBtn"
					action="#{stackTestDetail.modifyStackTest}"
					rendered="#{stackTestDetail.editable || stackTestDetail.memoEditable}" />
				<af:commandButton text="Cancel" immediate="true" id="StackTestDetail_CancelBtn"
					action="#{stackTestDetail.cancelEdit}"
					rendered="#{stackTestDetail.editable || stackTestDetail.memoEditable}" />
				<af:commandButton text="Set/Change Inspection Association" id="StackTestDetail_ChangeInspecAssociationBtn"
					action="#{stackTestDetail.chgFceAssign}" useWindow="true"
					windowWidth="1200" windowHeight="600"
					disabled="#{!stackTestDetail.cetaUpdate || stackTestDetail.locked}"
					rendered="#{!stackTestDetail.editable && !stackTestDetail.memoEditable && stackTestDetail.internalApp}">
				</af:commandButton>
				<af:commandButton text="Create Cloned Stack Test" id="StackTestDetail_CreateCloneSTBtn"
					action="#{stackTestDetail.cloneEmissionsTest}"
					disabled="#{!stackTestDetail.cetaUpdate}"
					rendered="#{!stackTestDetail.editable && !stackTestDetail.memoEditable && stackTestDetail.internalApp}" />
				<af:commandButton text="Set State to Reminder" id="StackTestDetail_SetStateReminderBtn"
					action="#{stackTestDetail.toReminderState}" useWindow="true"
					windowWidth="500" windowHeight="300"
					disabled="#{!stackTestDetail.allowEditOperations || stackTestDetail.stackTest.emissionTestState != 'drft'}"
					rendered="#{!stackTestDetail.editable && !stackTestDetail.memoEditable && stackTestDetail.stackTest.emissionTestState != 'rmdr' && stackTestDetail.internalApp}" />
				<af:commandButton text="Create Enforcement Action" id="StackTestDetail_CreateEABtn"
					disabled="#{!enforcementActionDetail.enforcementActionCreateAllowed}"
          			rendered="#{!stackTestDetail.editable && !stackTestDetail.memoEditable && stackTestDetail.internalApp}"
          			action="#{enforcementActionDetail.startNewEnforcementAction}">
          			<t:updateActionListener property="#{enforcementActionDetail.facilityId}"
						value="#{stackTestDetail.stackTest.facilityId}" />
					<t:updateActionListener property="#{enforcementActionDetail.fromStackTest}"
						value="true" />
				</af:commandButton>
			</af:panelButtonBar>
		</afh:rowLayout>
		<af:objectSpacer height="5" />
		<afh:rowLayout halign="center" rendered="#{!stackTestDetail.error}">
			<af:panelButtonBar>
				<%/*<af:commandButton id="viewFacility"
					text="Show Current Facility Inventory"
					rendered="#{! stackTestDetail.editable && !stackTestDetail.memoEditable && stackTestDetail.facility.versionId != -1 && stackTestDetail.internalApp}"
					action="#{facilityProfile.submitProfileById}">
					<t:updateActionListener property="#{facilityProfile.facilityId}"
						value="#{stackTestDetail.stackTest.facilityId}" />
					<t:updateActionListener property="#{menuItem_facProfile.disabled}"
						value="false" />
				</af:commandButton>

				<af:commandButton id="viewFacilityb"
					text="Show Current Facility Inventory"
					rendered="#{! stackTestDetail.editable && !stackTestDetail.memoEditable && stackTestDetail.facility.versionId == -1 && stackTestDetail.internalApp}"
					action="#{facilityProfile.submitProfileById}">
					<t:updateActionListener property="#{facilityProfile.facilityId}"
						value="#{stackTestDetail.stackTest.facilityId}" />
					<t:updateActionListener property="#{menuItem_facProfile.disabled}"
						value="false" />
				</af:commandButton>*/%>

				<af:commandButton id="StackTestDetail_ShowFacilityBtn"
					text="Show Associated Facility Inventory"
					rendered="#{(! stackTestDetail.editable && !stackTestDetail.memoEditable && stackTestDetail.internalApp) || (stackTestDetail.stackTest.emissionTestState != 'drft' && !stackTestDetail.internalApp)}"
					action="#{facilityProfile.submitHistoryProfile}">
					<t:updateActionListener property="#{facilityProfile.fpId}"
						value="#{stackTestDetail.stackTest.fpId}" />
					<t:updateActionListener property="#{menuItem_facProfile.disabled}"
						value="false" />
				</af:commandButton>

				<af:commandButton id="StackTestDetail_AssociateCurrentFIBtn"
					text="Associate with Current Facility Inventory"
					rendered="#{stackTestDetail.okToUpdateFacilityProfile && ! stackTestDetail.editable && !stackTestDetail.memoEditable && stackTestDetail.internalApp}"
					action="dialog:stackTestSynchProfileDetail" useWindow="true"
					windowWidth="500" windowHeight="300">
				</af:commandButton>

				<af:commandButton text="Download/Print" id="StackTestDetail_PrintBtn"
					rendered="#{! stackTestDetail.editable && !stackTestDetail.memoEditable}"
					action="#{stackTestDetail.printEmissionsTest}" useWindow="true"
					windowWidth="500" windowHeight="300">
					<t:updateActionListener value="true"
						property="#{stackTestDetail.hideTradeSecret}" />
				</af:commandButton>

				<af:commandButton id="StackTestDetail_PrintSecretBtn"
					text="Download/Print Trade Secret Version"
					rendered="#{! stackTestDetail.editable && ! stackTestDetail.memoEditable && stackTestDetail.tradeSecretVisible && !applicationDetail.publicReadOnlyUser}"
					useWindow="true" windowWidth="500" windowHeight="300"
					action="#{stackTestDetail.printEmissionsTest}">
					<t:updateActionListener value="false"
						property="#{stackTestDetail.hideTradeSecret}" />
				</af:commandButton>

				<af:commandButton text="Workflow Task" id="StackTestDetail_WorkflowTaskBtn"
					rendered="#{stackTestDetail.internalApp && stackTestDetail.fromTODOList && ! stackTestDetail.editable && stackTestDetail.stackTest.emissionTestState == 'sbmt'}"
					action="#{stackTestDetail.goToCurrentWorkflow}" />
				<af:commandButton id="StackTestDetail_ShowFacilityST"
					text="Show Facility Stack Tests"
					rendered="#{!stackTestDetail.editable && !stackTestDetail.memoEditable && stackTestDetail.internalApp}"
					action="#{stackTestDetail.goToSummaryPage}">
					<t:updateActionListener property="#{menuItem_facProfile.disabled}"
						value="false" />
				</af:commandButton>
			</af:panelButtonBar>
		</afh:rowLayout>
		<afh:rowLayout halign="center"
			rendered="#{stackTestDetail.error && stackTestDetail.newClone && stackTestDetail.internalApp}">
			<af:panelButtonBar>
				<af:commandButton text="Return to Stack Test" id="StackTestDetail_ReturnToST"
					action="#{stackTestDetail.restoreOrigStackTest}">
				</af:commandButton>
				<af:commandButton text="Show Facility Stack Test(s)" id="StackTestDetail_ShowFacST"
					rendered="#{!stackTestDetail.editable}"
					action="#{stackTestDetail.goToSummaryPage}">
					<t:updateActionListener property="#{menuItem_facProfile.disabled}"
						value="false" />
				</af:commandButton>
			</af:panelButtonBar>
		</afh:rowLayout>
	</af:panelForm>
</af:panelGroup>
