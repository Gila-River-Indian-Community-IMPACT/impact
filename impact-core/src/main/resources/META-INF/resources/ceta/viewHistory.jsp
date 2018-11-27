<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="viewHistory" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Air Program History Information">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true" id="form">
			<af:panelForm partialTriggers="airProgram">
				<af:messages />
				<afh:rowLayout halign="left"
					rendered="#{fceSiteVisits.facilityHistory==null}">
					<af:outputFormatted value="There is no history record" />
				</afh:rowLayout>
				<afh:rowLayout halign="left"
					rendered="#{fceSiteVisits.facilityHistory!=null}">
					<af:panelForm rows="3" maxColumns="1">
						<af:inputText label="Facility ID: "
							value="#{fceSiteVisits.histFacility.facilityId}" readOnly="true"
							rendered="#{fceSiteVisits.histFacility != null}" />
						<af:inputText label="Facility Name: "
							value="#{fceSiteVisits.histFacility.name}" readOnly="true"
							rendered="#{fceSiteVisits.histFacility != null}" />
						<af:selectOneChoice label="Air Program:"
							readOnly="#{!fceSiteVisits.okToEditComplianceStatus}"
							autoSubmit="true" id="airProgram"
							value="#{fceSiteVisits.airProgramCd}" unselectedLabel="NTV">
							<f:selectItems
								value="#{compEvalDefs.airProgramDefs.allSearchItems}" />
						</af:selectOneChoice>
					</af:panelForm>
				</afh:rowLayout>
				<afh:rowLayout halign="left"
					rendered="#{fceSiteVisits.facilityHistory!=null}">
					<af:panelForm rendered="#{fceSiteVisits.facilityHistory!=null}"
						partialTriggers="MactRuleCheckbox NeshapsRuleCheckbox NspsRuleCheckbox PsdRuleCheckbox NsrRuleCheckbox">
						<af:panelForm rows="2" maxColumns="3">
							<af:selectBooleanCheckbox label="Subject to Part 63 NESHAP:"
								value="#{fceSiteVisits.mact}"
								readOnly="#{!fceSiteVisits.okToEditComplianceStatus}"
								id="MactRuleCheckbox" autoSubmit="true" />
							<af:selectBooleanCheckbox label="Subject to Part 61 NESHAP:"
								value="#{fceSiteVisits.facilityHistory.neshaps}"
								readOnly="#{!fceSiteVisits.okToEditComplianceStatus}"
								id="NeshapsRuleCheckbox" autoSubmit="true" />
							<af:selectBooleanCheckbox label="Subject to NSPS:"
								value="#{fceSiteVisits.facilityHistory.nsps}"
								readOnly="#{!fceSiteVisits.okToEditComplianceStatus}"
								id="NspsRuleCheckbox" autoSubmit="true" />
							<af:selectBooleanCheckbox label="Subject to PSD:"
								value="#{fceSiteVisits.facilityHistory.psd}"
								readOnly="#{!fceSiteVisits.okToEditComplianceStatus}"
								id="PsdRuleCheckbox" autoSubmit="true" />
							<af:selectBooleanCheckbox label="Subject to non-attainment NSR:"
								value="#{fceSiteVisits.facilityHistory.nsrNonAttainment}"
								readOnly="#{!fceSiteVisits.okToEditComplianceStatus}"
								id="NsrRuleCheckbox" autoSubmit="true" />
						</af:panelForm>
						<af:panelHeader text="Air Programs" size="4" />
						<afh:rowLayout halign="center">
							<af:panelForm rows="1" maxColumns="1" width="600">
								<af:table value="#{fceSiteVisits.airPrograms}"
									bandingInterval="1" banding="row" var="airProgram"
									rows="#{fceSiteVisits.pageLimit}" width="100%">
									<af:column sortProperty="pollutantCd" sortable="true" formatType="text"
										headerText="Air Program">
										<af:selectOneChoice value="#{airProgram.pollutantCd}"
											readOnly="true" unselectedLabel="">
											<f:selectItems
												value="#{compEvalDefs.airPrograms.allSearchItems}" />
										</af:selectOneChoice>
									</af:column>
									<af:column sortProperty="complianceCd" sortable="true" formatType="icon"
										headerText="Compliance Status">
										<af:selectOneChoice value="#{airProgram.complianceCd}"
											unselectedLabel=""
											readOnly="#{!fceSiteVisits.okToEditComplianceStatus}"
											inlineStyle="#{'N' == airProgram.complianceCd?'color: orange; font-weight: bold;':''}">
											<f:selectItems
												value="#{compEvalDefs.complianceStatusDefs.allSearchItems}" />
										</af:selectOneChoice>
									</af:column>
								</af:table>
							</af:panelForm>
						</afh:rowLayout>
						<af:panelHeader
							text="Federal Rules Applicability - Part 61 NESHAPS Subparts" size="4"
							rendered="#{fceSiteVisits.facilityHistory.neshaps}" />
						<afh:rowLayout halign="center">
							<af:panelForm rows="1" maxColumns="1" width="600">
								<af:table value="#{fceSiteVisits.neshapsSubpartsWrapper}"
									binding="#{fceSiteVisits.neshapsSubpartsWrapper.table}"
									bandingInterval="1" banding="row" var="neshapsSubpart"
									rows="#{fceSiteVisits.pageLimit}"
									rendered="#{fceSiteVisits.facilityHistory.neshaps}" width="98%">
									<af:column headerText="Select" sortProperty="c00"
										sortable="true" formatType="icon"
										rendered="#{fceSiteVisits.okToEditComplianceStatus}">
										<af:selectBooleanCheckbox value="#{neshapsSubpart.delete}" />
									</af:column>
									<af:column sortProperty="pollutantCd" sortable="true" formatType="text"
										headerText="Part 61 NESHAPS Subpart">
										<af:selectOneChoice value="#{neshapsSubpart.pollutantCd}"
											unselectedLabel=""
											readOnly="#{!fceSiteVisits.okToEditComplianceStatus}">
											<f:selectItems
												value="#{infraDefs.neshapsSubparts.items[(empty neshapsSubpart.pollutantCd ? '' : neshapsSubpart.pollutantCd)]}" />
										</af:selectOneChoice>
									</af:column>
									<af:column sortProperty="complianceCd" sortable="true" formatType="icon"
										headerText="Compliance Status">
										<af:selectOneChoice value="#{neshapsSubpart.complianceCd}"
											unselectedLabel=""
											inlineStyle="#{'N' == neshapsSubpart.complianceCd?'color: orange; font-weight: bold;':''}"
											readOnly="#{!fceSiteVisits.okToEditComplianceStatus}">
											<f:selectItems
												value="#{compEvalDefs.complianceStatusDefs.allSearchItems}" />
										</af:selectOneChoice>
									</af:column>
									<f:facet name="footer">
										<afh:rowLayout halign="center">
											<af:panelButtonBar>
												<af:commandButton text="Add Subpart"
													rendered="#{fceSiteVisits.okToEditComplianceStatus}"
													action="#{fceSiteVisits.addNeshapsSubpart}">
												</af:commandButton>
												<af:commandButton text="Delete Selected Subparts"
													rendered="#{fceSiteVisits.okToEditComplianceStatus}"
													action="#{fceSiteVisits.deleteNeshapsSubparts}">
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
									</f:facet>
								</af:table>
							</af:panelForm>
						</afh:rowLayout>

						<af:panelHeader
							text="Federal Rules Applicability - NSPS Pollutants" size="4"
							rendered="#{fceSiteVisits.facilityHistory.nsps && false}" />
						<afh:rowLayout halign="center">
							<af:panelForm rows="1" maxColumns="1" width="600" rendered="false">
								<af:table value="#{fceSiteVisits.nspsPollutantsWrapper}"
									bandingInterval="1" banding="row" var="nspsPollutant"
									binding="#{fceSiteVisits.nspsPollutantsWrapper.table}"
									rendered="#{fceSiteVisits.facilityHistory.nsps}" width="98%">
									<af:column headerText="Select" sortProperty="c00"
										sortable="true" formatType="icon"
										rendered="#{fceSiteVisits.okToEditComplianceStatus}">
										<af:selectBooleanCheckbox value="#{nspsPollutant.delete}" />
									</af:column>
									<af:column sortProperty="pollutantCd" sortable="true" formatType="text"
										headerText="NSPS Pollutant">
										<af:selectOneChoice value="#{nspsPollutant.pollutantCd}"
											unselectedLabel=""
											readOnly="#{!fceSiteVisits.okToEditComplianceStatus}">
											<f:selectItems
												value="#{facilityReference.nspsPollutantDefs.items[(empty nspsPollutant.pollutantCd ? '' : nspsPollutant.pollutantCd)]}" />
										</af:selectOneChoice>
									</af:column>
									<af:column sortProperty="complianceCd" sortable="true" formatType="icon"
										headerText="Compliance Status">
										<af:selectOneChoice value="#{nspsPollutant.complianceCd}"
											unselectedLabel=""
											inlineStyle="#{'N' == nspsPollutant.complianceCd?'color: orange; font-weight: bold;':''}"
											readOnly="#{!fceSiteVisits.okToEditComplianceStatus}">
											<f:selectItems
												value="#{compEvalDefs.complianceStatusDefs.allSearchItems}" />
										</af:selectOneChoice>
									</af:column>
									<f:facet name="footer">
										<afh:rowLayout halign="center">
											<af:panelButtonBar>
												<af:commandButton text="Add Pollutant"
													rendered="#{fceSiteVisits.okToEditComplianceStatus}"
													action="#{fceSiteVisits.addNspsPollutant}">
												</af:commandButton>
												<af:commandButton text="Delete Selected Pollutants"
													rendered="#{fceSiteVisits.okToEditComplianceStatus}"
													action="#{fceSiteVisits.deleteNspsPollutants}">
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
									</f:facet>
								</af:table>
							</af:panelForm>
						</afh:rowLayout>

						<af:panelHeader
							text="Federal Rules Applicability - PSD Pollutants" size="4"
							rendered="#{fceSiteVisits.facilityHistory.psd && false}" />
						<afh:rowLayout halign="center">
							<af:panelForm rows="1" maxColumns="1" width="600" rendered="false">
								<af:table value="#{fceSiteVisits.psdPollutantsWrapper}"
									binding="#{fceSiteVisits.psdPollutantsWrapper.table}"
									bandingInterval="1" banding="row" var="psdPollutant"
									rendered="#{fceSiteVisits.facilityHistory.psd}" width="98%">
									<af:column headerText="Select" sortProperty="c00"
										sortable="true" formatType="icon"
										rendered="#{fceSiteVisits.okToEditComplianceStatus}">
										<af:selectBooleanCheckbox value="#{psdPollutant.delete}" />
									</af:column>
									<af:column sortProperty="pollutantCd" sortable="true" formatType="text"
										headerText="PSD Pollutant">
										<af:selectOneChoice value="#{psdPollutant.pollutantCd}"
											unselectedLabel=""
											readOnly="#{!fceSiteVisits.okToEditComplianceStatus}">
											<f:selectItems
												value="#{facilityReference.psdPollutantDefs.items[(empty psdPollutant.pollutantCd ? '' : psdPollutant.pollutantCd)]}" />
										</af:selectOneChoice>
									</af:column>
									<af:column sortProperty="complianceCd" sortable="true" formatType="icon"
										headerText="Compliance Status">
										<af:selectOneChoice value="#{psdPollutant.complianceCd}"
											unselectedLabel=""
											inlineStyle="#{'N' == psdPollutant.complianceCd?'color: orange; font-weight: bold;':''}"
											readOnly="#{!fceSiteVisits.okToEditComplianceStatus}">
											<f:selectItems
												value="#{compEvalDefs.complianceStatusDefs.allSearchItems}" />
										</af:selectOneChoice>
									</af:column>
									<f:facet name="footer">
										<afh:rowLayout halign="center">
											<af:panelButtonBar>
												<af:commandButton text="Add Pollutant"
													rendered="#{fceSiteVisits.okToEditComplianceStatus}"
													action="#{fceSiteVisits.addPsdPollutant}">
												</af:commandButton>
												<af:commandButton text="Delete Selected Pollutants"
													rendered="#{fceSiteVisits.okToEditComplianceStatus}"
													action="#{fceSiteVisits.deletePsdPollutants}">
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
									</f:facet>
								</af:table>
							</af:panelForm>
						</afh:rowLayout>

						<af:panelHeader
							text="Federal Rules Applicability - Non-attainment NSR Pollutants"
							size="4"
							rendered="#{fceSiteVisits.facilityHistory.nsrNonAttainment && false}" />
						<afh:rowLayout halign="center">
							<af:panelForm rows="1" maxColumns="1" width="600" rendered="false">
								<af:table value="#{fceSiteVisits.nsrPollutantsWrapper}"
									binding="#{fceSiteVisits.nsrPollutantsWrapper.table}"
									bandingInterval="1" banding="row" var="nsrPollutant"
									rendered="#{fceSiteVisits.facilityHistory.nsrNonAttainment}"
									width="98%">
									<af:column headerText="Select" sortProperty="c00"
										sortable="true" formatType="icon"
										rendered="#{fceSiteVisits.okToEditComplianceStatus}">
										<af:selectBooleanCheckbox value="#{nsrPollutant.delete}" />
									</af:column>
									<af:column sortProperty="pollutantCd" sortable="true" formatType="text"
										headerText="Non-attainment NSR Pollutant">
										<af:selectOneChoice value="#{nsrPollutant.pollutantCd}"
											unselectedLabel=""
											readOnly="#{!fceSiteVisits.okToEditComplianceStatus}">
											<f:selectItems
												value="#{facilityReference.nsrPollutantDefs.items[(empty nsrPollutant.pollutantCd ? '' : nsrPollutant.pollutantCd)]}" />
										</af:selectOneChoice>
									</af:column>
									<af:column sortProperty="complianceCd" sortable="true" formatType="icon"
										headerText="Compliance Status">
										<af:selectOneChoice value="#{nsrPollutant.complianceCd}"
											unselectedLabel=""
											inlineStyle="#{'N' == nsrPollutant.complianceCd?'color: orange; font-weight: bold;':''}"
											readOnly="#{!fceSiteVisits.okToEditComplianceStatus}">

											<f:selectItems
												value="#{compEvalDefs.complianceStatusDefs.allSearchItems}" />
										</af:selectOneChoice>
									</af:column>
									<f:facet name="footer">
										<afh:rowLayout halign="center">
											<af:panelButtonBar>
												<af:commandButton text="Add Pollutant"
													rendered="#{fceSiteVisits.okToEditComplianceStatus}"
													action="#{fceSiteVisits.addNsrPollutant}">
												</af:commandButton>
												<af:commandButton text="Delete Selected Pollutants"
													rendered="#{fceSiteVisits.okToEditComplianceStatus}"
													action="#{fceSiteVisits.deleteNsrPollutants}">
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
									</f:facet>
								</af:table>
							</af:panelForm>
						</afh:rowLayout>
					</af:panelForm>
				</afh:rowLayout>
				<af:objectSpacer height="10" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Edit"
							action="#{fceSiteVisits.editComplianceStatus}"
							disabled="#{!fceDetail.cetaUpdate}"
							rendered="#{!fceSiteVisits.okToEditComplianceStatus}" />
						<af:commandButton text="Save"
							action="#{fceSiteVisits.saveComplianceStatus}"
							rendered="#{fceSiteVisits.okToEditComplianceStatus}" />
						<af:commandButton text="Cancel"
							action="#{fceSiteVisits.cancelEditComplianceStatus}"
							rendered="#{fceSiteVisits.okToEditComplianceStatus}" />
						<af:commandButton text="Close" action="#{fceSiteVisits.close}"
							rendered="#{!fceSiteVisits.okToEditComplianceStatus}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>
