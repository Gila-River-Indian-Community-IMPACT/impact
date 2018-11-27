<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document
		title="Facilities With Air Programs Out-Of-Compliance Search">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page
				title="Facilities With Air Programs Out-Of-Compliance Search"
				var="foo" value="#{menuModel.model}">
				<%@ include file="../util/branding.jsp"%>
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						type="#{foo.type}" disabled="#{foo.disabled}"
						rendered="#{foo.rendered}" icon="#{foo.icon}" />
				</f:facet>
				<af:panelGroup layout="vertical">
					<af:panelBorder>
						<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
							<afh:rowLayout halign="left">
								<af:panelHorizontal>
									<af:outputFormatted
										value="<b>Data source:</b> current facility inventory<br><br>" />
									<af:objectSpacer width="10" height="5" />
									<af:commandButton text="Show Explanation"
										rendered="#{!complianceSearch.showExplain}"
										action="#{complianceSearch.turnOn}">
									</af:commandButton>
									<af:commandButton text="Hide Explanation"
										rendered="#{complianceSearch.showExplain}"
										action="#{complianceSearch.turnOff}">
									</af:commandButton>
								</af:panelHorizontal>
							</afh:rowLayout>
							<afh:rowLayout rendered="#{complianceSearch.showExplain}"
								halign="left">
								<af:outputFormatted
									value="The default search (after <b>Reset</b> clicked) searches for all <b>FENSR, Title V</b> and <b>Mega TV</b> facilities where any of the Air Programs have a compliance status of 'No'. &nbsp;'No' = not in compliance.<br><br>All of the Search Criteria must be satisfied EXCEPT the search only needs to satisfy at least one of the <b>Air Program Compliance Criteria</b> (multiple criteria results in an “or” statement – i.e., results include all the criteria as individually selected). To ignore an Air Program, use Ctrl-Click to unselect 'No' and 'On Schedule'. &nbsp;The <b>Change All</b> selection will change all of the Air Programs Criteria at the same time.<br><br>" />
							</afh:rowLayout>
						</afh:tableLayout>
						<h:panelGrid border="1" width="800">
							<af:panelBorder>
								<af:showDetailHeader text="Facilities Out-Of-Compliance Filter"
									disclosed="true">
									<af:panelGroup layout="horizontal">
										<afh:cellFormat>
											<af:panelForm rows="5" maxColumns="1">
												<af:inputText columns="25" label="Facility ID"
													value="#{complianceSearch.facilityId}"
													maximumLength="10" tip="0000000000, 0%, %0%, *0*, 0*" />
												<af:inputText tip="acme%, %acme% *acme*, acme*" columns="25"
													label="Facility Name"
													value="#{complianceSearch.facilityName}" valign="top" />
												<af:selectOneChoice label="Operating Status"
													unselectedLabel=""
													value="#{complianceSearch.operatingStatusCd}">
													<f:selectItems
														value="#{facilityProfile.facOperatingStatusDefs.items[(empty complianceSearch.operatingStatusCd ? '' : complianceSearch.operatingStatusCd)]}" />
												</af:selectOneChoice>
												<af:selectOneChoice label="County"
													value="#{complianceSearch.countyCd}" unselectedLabel="">
													<f:selectItems value="#{infraDefs.counties}" />
												</af:selectOneChoice>
												<af:selectOneChoice label="District" id="dolaa"
													value="#{complianceSearch.doLaaCd}" unselectedLabel="">
													<f:selectItems value="#{facilitySearch.doLaas}" />
												</af:selectOneChoice>
												<af:outputLabel value="Inspection" />
												<af:selectManyListbox label="Classification(s)"
													size="#{complianceSearch.numInspectClasses}"
													value="#{complianceSearch.selectedInspectClasses}">
													<f:selectItems
														value="#{complianceSearch.allInspectClasses}" />
												</af:selectManyListbox>
											</af:panelForm>
										</afh:cellFormat>
										<afh:cellFormat>
											<af:panelGroup layout="vertical">
												<afh:cellFormat>
													<af:panelForm rows="2" maxColumns="1">
														<af:panelGroup layout="horizontal">
															<afh:cellFormat>
																<afh:rowLayout halign="left">
																	<af:outputLabel value="Air Program Compliance Criteria" />
																</afh:rowLayout>
															</afh:cellFormat>
															<afh:cellFormat>
																<af:objectSpacer width="50" />
															</afh:cellFormat>
															<afh:cellFormat>
																<afh:rowLayout halign="right">
																	<af:outputText
																		value="Look for facilities matching any of these"
																		inlineStyle="color: rgb(0,0,255);" />
																</afh:rowLayout>
															</afh:cellFormat>
														</af:panelGroup>
														<h:panelGrid columns="1" border="1">
															<af:panelForm rows="4" maxColumns="3"
																partialTriggers="selAll">
																<af:selectManyListbox label="Change All" id="selAll"
																	autoSubmit="true"
																	size="#{complianceSearch.numCompliance}"
																	value="#{complianceSearch.selectedAll}">
																	<f:selectItems
																		value="#{complianceSearch.allCompliance}" />
																</af:selectManyListbox>
																<af:outputText value=" " />
																<af:outputText value=" " />
																<af:outputText value=" " />
																<af:selectManyListbox label="SIP" autoSubmit="true"
																	size="#{complianceSearch.numCompliance}"
																	value="#{complianceSearch.selectedSip}">
																	<f:selectItems
																		value="#{complianceSearch.allCompliance}" />
																</af:selectManyListbox>
																<af:selectManyListbox label="Title V" autoSubmit="true"
																	size="#{complianceSearch.numCompliance}"
																	value="#{complianceSearch.selectedTv}">
																	<f:selectItems
																		value="#{complianceSearch.allCompliance}" />
																</af:selectManyListbox>
																<af:selectManyListbox label="SM/FESOP" autoSubmit="true"
																	size="#{complianceSearch.numCompliance}"
																	value="#{complianceSearch.selectedSm}">
																	<f:selectItems
																		value="#{complianceSearch.allCompliance}" />
																</af:selectManyListbox>
																<af:selectManyListbox label="MACT" autoSubmit="true"
																	size="#{complianceSearch.numCompliance}"
																	value="#{complianceSearch.selectedMact}">
																	<f:selectItems
																		value="#{complianceSearch.allCompliance}" />
																</af:selectManyListbox>
																<af:selectManyListbox label="NESHAPS" autoSubmit="true"
																	size="#{complianceSearch.numCompliance}"
																	value="#{complianceSearch.selectedNeshaps}">
																	<f:selectItems
																		value="#{complianceSearch.allCompliance}" />
																</af:selectManyListbox>
																<af:selectManyListbox label="NSPS" autoSubmit="true"
																	size="#{complianceSearch.numCompliance}"
																	value="#{complianceSearch.selectedNsps}">
																	<f:selectItems
																		value="#{complianceSearch.allCompliance}" />
																</af:selectManyListbox>
																<af:selectManyListbox label="PSD" autoSubmit="true"
																	size="#{complianceSearch.numCompliance}"
																	value="#{complianceSearch.selectedPsd}">
																	<f:selectItems
																		value="#{complianceSearch.allCompliance}" />
																</af:selectManyListbox>
																<af:selectManyListbox label="NSR" autoSubmit="true"
																	size="#{complianceSearch.numCompliance}"
																	value="#{complianceSearch.selectedNsr}">
																	<f:selectItems
																		value="#{complianceSearch.allCompliance}" />
																</af:selectManyListbox>
															</af:panelForm>
														</h:panelGrid>
													</af:panelForm>
												</afh:cellFormat>
											</af:panelGroup>
										</afh:cellFormat>
									</af:panelGroup>
									<af:objectSpacer width="100%" height="15" />
									<af:panelGroup layout="vertical">
										<afh:rowLayout halign="center">
											<af:panelButtonBar>
												<af:commandButton text="Submit"
													action="#{complianceSearch.submitSearch}">
												</af:commandButton>
												<af:commandButton text="Reset"
													action="#{complianceSearch.reset}" />
											</af:panelButtonBar>
										</afh:rowLayout>
									</af:panelGroup>
								</af:showDetailHeader>
							</af:panelBorder>
						</h:panelGrid>
					</af:panelBorder>
					<af:objectSpacer width="100%" height="5" />
					<h:panelGrid border="1" width="100%">
						<af:panelBorder>
							<af:showDetailHeader
								text="Air Program(s) Out-Of-Compliance Information"
								rendered="#{complianceSearch.hasSearchResults}" disclosed="true">
								<af:table id="compSearch" emptyText=" "
									value="#{complianceSearch.complianceList}" var="comp"
									bandingInterval="1" banding="row"
									rows="#{complianceSearch.displayRows}">
									<af:column sortable="true" sortProperty="facilityId"
										headerText="Facility ID">
										<af:commandLink text="#{comp.facilityId}"
											action="#{facilityProfile.submitProfileById}"
											inlineStyle="white-space: nowrap;">
											<t:updateActionListener
												property="#{facilityProfile.facilityId}"
												value="#{comp.facilityId}" />
											<t:updateActionListener
												property="#{menuItem_facProfile.disabled}" value="false" />
										</af:commandLink>
									</af:column>
									<af:column sortable="true" sortProperty="facilityName"
										headerText="Facility Name">
										<af:outputText value="#{comp.facilityName}" />
									</af:column>
									<af:column sortProperty="operatingStatusCd" sortable="true"
										formatType="icon" headerText="Operating Status">
										<af:selectOneChoice id="opStat" readOnly="true"
											inlineStyle="#{(comp.operatingStatusCd == 'sd') ? 'color: orange; font-weight: bold;' : '' }"
											value="#{comp.operatingStatusCd}">
											<f:selectItems
												value="#{facilityReference.operatingStatusDefs.allSearchItems}" />
										</af:selectOneChoice>
										<af:selectInputDate value="#{comp.shutdownDate}"
											inlineStyle="color: orange; font-weight: bold;" readOnly="true"
											rendered="#{comp.shutdownDate != null}">
										</af:selectInputDate>
									</af:column>
									<af:column sortProperty="sipComp" sortable="true"
										formatType="icon" headerText="SIP">
										<af:selectOneChoice value="#{comp.sipComp}" readOnly="true">
											<f:selectItems value="#{complianceSearch.allCompliance}" />
										</af:selectOneChoice>
									</af:column>
									<af:column sortProperty="tvComp" sortable="true"
										formatType="icon" headerText="Title V">
										<af:selectOneChoice value="#{comp.tvComp}" readOnly="true">
											<f:selectItems value="#{complianceSearch.allCompliance}" />
										</af:selectOneChoice>
									</af:column>
									<af:column sortProperty="smComp" sortable="true"
										formatType="icon" headerText="SM/FESOP">
										<af:selectOneChoice value="#{comp.smComp}" readOnly="true">
											<f:selectItems value="#{complianceSearch.allCompliance}" />
										</af:selectOneChoice>
									</af:column>
									<af:column sortProperty="mactComp" sortable="true"
										formatType="icon" headerText="MACT">
										<af:selectOneChoice value="#{comp.mactComp}" readOnly="true">
											<f:selectItems value="#{complianceSearch.allCompliance}" />
										</af:selectOneChoice>
									</af:column>
									<af:column sortProperty="neshapsComp" sortable="true"
										formatType="icon" headerText="NESHAPS">
										<af:selectOneChoice value="#{comp.neshapsComp}"
											readOnly="true">
											<f:selectItems value="#{complianceSearch.allCompliance}" />
										</af:selectOneChoice>
									</af:column>
									<af:column sortProperty="nspsComp" sortable="true"
										formatType="icon" headerText="NSPS">
										<af:selectOneChoice value="#{comp.nspsComp}" readOnly="true">
											<f:selectItems value="#{complianceSearch.allCompliance}" />
										</af:selectOneChoice>
									</af:column>
									<af:column sortProperty="psdComp" sortable="true"
										formatType="icon" headerText="PSD">
										<af:selectOneChoice value="#{comp.psdComp}" readOnly="true">
											<f:selectItems value="#{complianceSearch.allCompliance}" />
										</af:selectOneChoice>
									</af:column>
									<af:column sortProperty="nsrComp" sortable="true"
										formatType="icon" headerText="NSR">
										<af:selectOneChoice value="#{comp.nsrComp}" readOnly="true">
											<f:selectItems value="#{complianceSearch.allCompliance}" />
										</af:selectOneChoice>
									</af:column>
									<f:facet name="footer">
										<h:panelGrid width="100%">
											<afh:rowLayout halign="center">
												<af:panelButtonBar>
													<af:commandButton text="Display All Rows" id="displayRows"
														action="#{complianceSearch.displayAllRows}"
														rendered="#{complianceSearch.showDisplayAll}">
													</af:commandButton>
													<af:commandButton text="Display Some Rows"
														id="displaySomeRows"
														action="#{complianceSearch.displaySomeRows}"
														rendered="#{complianceSearch.showDisplaySome}">
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
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</af:panelGroup>
			</af:page>
		</af:form>
	</af:document>
</f:view>
