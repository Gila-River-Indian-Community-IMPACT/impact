<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document title="Offsets Summary Report">
		<f:verbatim>
			<script>
				</f:verbatim><af:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Offsets Summary Report">
				<%@ include file="../util/header.jsp"%>

				<af:panelGroup>
					<afh:rowLayout halign="center">
						<h:panelGrid columns="1" border="1" width="1000">

							<af:showDetailHeader text="Search Criteria" disclosed="true">
								<af:panelForm rows="1" width="1000" maxColumns="3"
									partialTriggers="nonAttainmentAreaCd">

									<af:selectOneChoice id="cmpId" label="Company"
										unselectedLabel="" value="#{offsetSummaryReport.cmpId}">
										<f:selectItems value="#{companySearch.allCompanies}" />
									</af:selectOneChoice>

									<af:selectOneChoice id="nonAttainmentAreaCd" autoSubmit="true"
										label="Non-Attainment Area: " unselectedLabel=""
										valueChangeListener="#{offsetSummaryReport.nonAttainmentAreaValueChanged}"	
										value="#{offsetSummaryReport.nonAttainmentAreaCd}">
										<f:selectItems
											value="#{permitReference.offsetTrackingNonAttainmentAreaDefs.items[(empty offsetSummaryReport.nonAttainmentAreaCd ? '' : offsetSummaryReport.nonAttainmentAreaCd)]}" />
									</af:selectOneChoice>

									<af:selectOneChoice id="pollutantCd" label="Pollutant: "
										rendered="#{not empty offsetSummaryReport.nonAttainmentAreaCd}"
										unselectedLabel="" value="#{offsetSummaryReport.pollutantCd}">
										<f:selectItems
											value="#{offsetSummaryReport.nonAttainmentAreaPollutants}" />
									</af:selectOneChoice>

								</af:panelForm>

								<af:objectSpacer width="100%" height="15" />

								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:objectSpacer width="100%" height="20" />
										<af:commandButton text="Submit"
											action="#{offsetSummaryReport.generateOffsetSummaryReport}" />
										<af:commandButton text="Reset"
											action="#{offsetSummaryReport.reset}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</h:panelGrid>
					</afh:rowLayout>
				</af:panelGroup>

				<af:objectSpacer width="100%" height="20" />

				<af:panelGroup layout="vertical"
					rendered="#{offsetSummaryReport.hasSearchResults}">
					<afh:rowLayout halign="center">
						<h:panelGrid columns="1" border="1" width="1500">
							<af:panelGroup layout="vertical" rendered="true">
								<afh:rowLayout halign="center">
									<af:table id="offsetsSummary"
										value="#{offsetSummaryReport.offsetSummaryLineItems}"
										bandingInterval="1" banding="row" var="offsetSummary"
										rows="#{companyProfile.pageLimit}" emptyText=" ">

										<af:column id="cmpId" headerText="Company ID" width="5%"
											sortable="true" sortProperty="cmpId" formatType="text">
											<af:panelHorizontal valign="middle" halign="left">
												<af:commandLink action="#{companyProfile.submitProfile}"
													text="#{offsetSummary.cmpId}">
													<t:updateActionListener property="#{companyProfile.cmpId}"
														value="#{offsetSummary.cmpId}" />
													<t:updateActionListener
														property="#{menuItem_companyProfile.disabled}"
														value="false" />
												</af:commandLink>
											</af:panelHorizontal>
										</af:column>

										<af:column id="companyName" headerText="Company Name"
											width="8%" sortable="true" sortProperty="companyName"
											formatType="text">
											<af:panelHorizontal valign="middle" halign="left">
												<af:inputText label="Company Name" readOnly="true"
													value="#{offsetSummary.companyName}" />
											</af:panelHorizontal>
										</af:column>

										<af:column id="facilityId" headerText="Facility ID" width="5%"
											sortProperty="facilityId" sortable="true" formatType="text">
											<af:panelHorizontal valign="middle" halign="left">
												<af:commandLink text="#{offsetSummary.facilityId}"
													action="#{facilityProfile.submitProfile}"
													inlineStyle="white-space: nowrap;">
													<t:updateActionListener property="#{facilityProfile.fpId}"
														value="#{offsetSummary.fpId}" />
													<t:updateActionListener
														property="#{menuItem_facProfile.disabled}" value="false" />
												</af:commandLink>
											</af:panelHorizontal>
										</af:column>

										<af:column id="facilityName" headerText="Facility Name"
											width="8%" sortable="true" sortProperty="facilityName"
											formatType="text">
											<af:panelHorizontal valign="middle" halign="left">
												<af:inputText label="Facility Name" readOnly="true"
													value="#{offsetSummary.facilityName}" />
											</af:panelHorizontal>
										</af:column>

										<af:column id="permitNumber" headerText="Permit Number"
											width="5%" sortProperty="permitNumber" sortable="true"
											formatType="text">
											<af:panelHorizontal valign="middle" halign="left">
												<af:commandLink text="#{offsetSummary.permitNumber}"
													action="#{permitDetail.loadPermit}">
													<af:setActionListener to="#{permitDetail.permitID}"
														from="#{offsetSummary.permitId}" />
													<t:updateActionListener
														property="#{permitDetail.fromTODOList}" value="false" />
												</af:commandLink>
											</af:panelHorizontal>
										</af:column>

										<af:column id="permitFinalIssuanceDate"
											headerText="Permit Final Issuance Date" width="6%"
											sortProperty="permitFinalIssuanceDate" sortable="true"
											formatType="text">
											<af:panelHorizontal valign="middle" halign="left">
												<af:selectInputDate label="Permit Final Issuance Date"
													readOnly="true"
													value="#{offsetSummary.permitFinalIssuanceDate}" />
											</af:panelHorizontal>
										</af:column>

										<af:column id="emissionsOffsetAdjustmentDate"
											headerText="Emissions Offset Adjustment Date" width="8%"
											sortProperty="emissionsOffsetAdjustmentDate" sortable="true"
											formatType="text">
											<af:panelHorizontal valign="middle" halign="left">
												<af:selectInputDate label="Emissions Offset Adjustment Date"
													readOnly="true"
													value="#{offsetSummary.emissionsOffsetAdjustmentDate}" />
											</af:panelHorizontal>
										</af:column>

										<af:column id="nonAttainmentAreaCd"
											headerText="Non-Attainment Area" width="4%"
											sortProperty="nonAttainmentAreaCd" sortable="true"
											formatType="text">
											<af:panelHorizontal valign="middle" halign="left">
												<af:selectOneChoice label="Non-Attainment Area"
													readOnly="true"
													value="#{offsetSummary.nonAttainmentAreaCd}">
													<f:selectItems
														value="#{permitReference.offsetTrackingNonAttainmentAreaDefs.items[(empty offsetSummary.nonAttainmentAreaCd ? '' : offsetSummary.nonAttainmentAreaCd)]}" />
												</af:selectOneChoice>
											</af:panelHorizontal>
										</af:column>

										<af:column id="pollutantCd" headerText="Pollutant" width="10%"
											sortable="true" sortProperty="pollutantCd" formatType="text">
											<af:panelHorizontal valign="middle" halign="left">
												<af:selectOneChoice label="Pollutant" readOnly="true"
													value="#{offsetSummary.pollutantCd}">
													<f:selectItems
														value="#{facilityReference.pollutantDefs.items[(empty offsetSummary.pollutantCd ? '' : offsetSummary.pollutantCd)]}" />
												</af:selectOneChoice>
											</af:panelHorizontal>
										</af:column>

										<af:column id="currentOffset" headerText="Current (Tons)"
											width="6%" sortable="true" sortProperty="currentOffset"
											formatType="number">
												<af:outputText 
													value="#{offsetSummary.currentOffset}">
													<af:convertNumber type="number" pattern="###,###.###"
														minFractionDigits="3" />
												</af:outputText>
										</af:column>

										<af:column id="baseOffset" headerText="Base (Tons)" width="4%"
											sortProperty="baseOffset" sortable="true" formatType="number">
												<af:outputText
													value="#{offsetSummary.baseOffset}">
													<af:convertNumber type="number" pattern="###,###.###"
														minFractionDigits="3" />
												</af:outputText>
										</af:column>

										<af:column id="delta" headerText="Delta (Tons)" width="4%"
											sortProperty="delta" sortable="true" formatType="number">
												<af:outputText
													value="#{offsetSummary.delta}">
													<af:convertNumber type="number" pattern="###,###.###"
														minFractionDigits="3" />
												</af:outputText>
										</af:column>

										<af:column id="emissionsReductionMultiple"
											headerText="Emissions Reduction Multiplier" width="8%"
											sortProperty="emissionsReductionMultiple" sortable="true"
											formatType="number">
												<af:outputText
													value="#{offsetSummary.emissionsReductionMultiple}">
													<af:convertNumber type="number" pattern="#######.##"
														minFractionDigits="2" />
												</af:outputText>
										</af:column>

										<af:column id="offsetMultiple" headerText="Emissions Increase Multiplier"
											width="4%" sortProperty="offsetMultiple" sortable="true"
											formatType="number">
												<af:outputText
													value="#{offsetSummary.offsetMultiple}">
													<af:convertNumber type="number" pattern="#######.##"
														minFractionDigits="2" />
												</af:outputText>
										</af:column>

										<af:column id="offsetAmount" headerText="Offset Used(+) or Generated(-) (Tons)"
											width="4%" sortProperty="offsetAmount" sortable="true"
											formatType="number">
												<af:outputText
													value="#{offsetSummary.offsetAmount}">
													<af:convertNumber type="number" pattern="#,###,###.###"
														minFractionDigits="3" maxFractionDigits="3" />
												</af:outputText>
										</af:column>

										<af:column id="includeInTotal"
											headerText="Include in Total (Y/N)" width="4%"
											sortable="true" sortProperty="includeInTotal"
											formatType="text">
											<af:panelHorizontal valign="middle" halign="left">
												<af:selectOneChoice label="Include in Total (Y/N)"
													readOnly="true" value="#{offsetSummary.includeInTotal}">
													<f:selectItem itemLabel="Yes" itemValue="true" />
													<f:selectItem itemLabel="No" itemValue="false" />
												</af:selectOneChoice>
											</af:panelHorizontal>
										</af:column>

										<af:column id="comment" headerText="Comment" width="25%"
											sortable="true" sortProperty="comment" formatType="text">
											<af:panelHorizontal valign="middle" halign="left">
												<af:outputText truncateAt="35"
													value="#{offsetSummary.comment}" 
													shortDesc="#{offsetSummary.comment}"/>
											</af:panelHorizontal>
										</af:column>

										<f:facet name="footer">
											<afh:rowLayout halign="center">
												<af:panelGroup layout="horizontal">
													<af:commandButton
														actionListener="#{tableExporter.printTable}"
														onclick="#{tableExporter.onClickScript}"
														text="Printable view" />
													<af:commandButton
														actionListener="#{tableExporter.excelTable}"
														onclick="#{tableExporter.onClickScript}"
														text="Export to excel" />
												</af:panelGroup>
											</afh:rowLayout>
										</f:facet>
									</af:table>
								</afh:rowLayout>
							</af:panelGroup>
						</h:panelGrid>
					</afh:rowLayout>
				</af:panelGroup>
			</af:page>
		</af:form>
	</af:document>
</f:view>

