<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document title="AFS Export/Import">
		<f:verbatim>
			<h:outputText value="#{facilityProfile.refreshStr}" escape="false" />
		</f:verbatim>

		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}"
				title="AFS Export/Import">
				<%@ include file="../util/header.jsp"%>
				<af:panelForm partialTriggers="EI obj if">
					<h:panelGrid border="1" align="center">
						<af:panelBorder>
							<afh:rowLayout halign="center">
								<afh:cellFormat>
									<af:panelForm>
										<af:selectOneRadio id="EI" label="What to Do"
											value="#{cetaImport.exportImport}"
											readOnly="#{cetaImport.operating}" autoSubmit="true">
											<f:selectItem itemLabel="Export New Records to AFS"
												itemValue="EX" />
											<f:selectItem
												itemLabel="Import to set AFS Sent Date on exported records"
												itemValue="IM" />
										</af:selectOneRadio>
										<af:inputText label="Export Record Count:"
											value="#{cetaImport.testCnt}" columns="6"
											readOnly="#{cetaImport.hasExportReview}"
											rendered="#{cetaImport.exportImport == 'EX'}" />
										<af:selectOneRadio id="if" label="Import Format"
											value="#{cetaImport.importType}"
											rendered="#{cetaImport.exportImport == 'IM' && cetaImport.objectType != 'sf'}"
											autoSubmit="true">
											<f:selectItem itemLabel="Import AFS File" itemValue="fileAfs" />
											<f:selectItem itemLabel="Import 9.4 File" itemValue="file94" />
										</af:selectOneRadio>
									</af:panelForm>
								</afh:cellFormat>
								<afh:cellFormat>
									<af:panelForm>
										<af:selectOneRadio id="obj" label="What Records"
											value="#{cetaImport.objectType}" autoSubmit="true"
											readOnly="#{cetaImport.operating}">
											<f:selectItem itemLabel="Scheduled Inspections" itemValue="sf" />
											<f:selectItem itemLabel="Completed Inspections" itemValue="fce" />
											<f:selectItem itemLabel="On Site Visits" itemValue="sv" />
											<f:selectItem itemLabel="Off Site Visits" itemValue="osv" />
											<f:selectItem itemLabel="Stack Tests" itemValue="et" />
											<f:selectItem itemLabel="Enforcement Actions" itemValue="ea" />
											<f:selectItem itemLabel="TV CCs" itemValue="tv" />
										</af:selectOneRadio>
										<af:selectOneChoice label="FFY Inspection Scheduled"
											unselectedLabel=""
											rendered="#{cetaImport.objectType == 'sf' && cetaImport.exportImport == 'EX'}"
											value="#{cetaImport.fceFfySched}">
											<mu:convertSigDigNumber pattern="####" />
											<f:selectItems
												value="#{cetaImport.yearsForFCEsDef.items[(empty cetaImport.fceFfySched ? '' : cetaImport.fceFfySched)]}" />
										</af:selectOneChoice>
									</af:panelForm>
								</afh:cellFormat>
							</afh:rowLayout>
							<af:panelForm partialTriggers="EI obj if"
								rendered="#{cetaImport.exportImport == 'EX' && cetaImport.objectType != null}">
								<afh:rowLayout halign="center">
									<af:outputText value="#{cetaImport.whatExported}"
										inlineStyle="color: rgb(0,0,255);" />
								</afh:rowLayout>
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Review Records"
											action="#{cetaImport.exportCetaData}" />
										<af:commandButton text="Reset" action="#{cetaImport.reset}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:panelForm>
							<af:panelForm
								rendered="#{cetaImport.exportImport == 'IM' && cetaImport.objectType != null}">
								<af:selectInputDate id="afsSentDate" label="AFS Sent Date : "
									value="#{cetaImport.afsSentDate}">
									<af:validateDateTimeRange minimum="1900-01-01" />
								</af:selectInputDate>
								<af:inputFile id="CETAImportFile"
									label="#{cetaImport.importButtonLabel}"
									value="#{cetaImport.cetaImportFile}" />
								<af:objectSpacer height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Set AFS Sent Date"
											action="#{cetaImport.importCetaData}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:panelForm>
						</af:panelBorder>
					</h:panelGrid>

					<afh:rowLayout halign="center" partialTriggers="EI obj if">
						<h:panelGrid border="1"
							rendered="#{cetaImport.hasExportReview && cetaImport.objectType == 'sv'}">
							<af:showDetailHeader text="Site Visit Records" disclosed="true">
								<af:table var="sv" bandingInterval="1" banding="row"
									width="100%" value="#{cetaImport.siteVisits}" rows="500">
									<af:column headerText="SCSC ID">
										<af:inputText value="#{sv.scscId}" readOnly="true" />
										<af:outputText value="Missing" rendered="#{sv.scscId == null}"
											inlineStyle="color: orange; font-weight: bold;" />
									</af:column>
									<%@ include file="../ceta/firstVisitColumns.jsp"%>
									<%@ include file="../ceta/visitColumns.jsp"%>
									<af:column formatType="text" headerText="AFS ID">
										<af:outputText value="#{sv.afsId}" />
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
						</h:panelGrid>

						<h:panelGrid border="1"
							rendered="#{cetaImport.hasExportReview && cetaImport.objectType == 'osv'}">
							<af:showDetailHeader text="Other(PCE) Records" disclosed="true">
								<af:table var="osv" bandingInterval="1" banding="row"
									width="100%" value="#{cetaImport.offSiteVisits}" rows="500">
									<af:column headerText="SCSC ID">
										<af:inputText value="#{osv.scscId}" readOnly="true" />
										<af:outputText value="Missing"
											rendered="#{osv.scscId == null}"
											inlineStyle="color: orange; font-weight: bold;" />
									</af:column>
									<af:column formatType="text" headerText="Other(PCE) ID">
										<af:outputText value="#{osv.offsitePceId}" />
									</af:column>

									<af:column formatType="text" headerText="Facility Id">
										<af:commandLink text="#{osv.facilityId}"
											action="#{facilityProfile.submitProfileById}"
											inlineStyle="white-space: nowrap;">
											<t:updateActionListener
												property="#{facilityProfile.facilityId}"
												value="#{osv.facilityId}" />
											<t:updateActionListener
												property="#{menuItem_facProfile.disabled}" value="false" />
										</af:commandLink>
									</af:column>
									<af:column headerText="FP ID" formatType="text" noWrap="true">
										<af:commandLink action="#{facilityProfile.submitProfile}"
											rendered="#{osv.versionId == -1}">
											<af:outputText value="Current (#{osv.fpId})" />
											<t:updateActionListener property="#{facilityProfile.fpId}"
												value="#{osv.fpId}" />
											<t:updateActionListener
												property="#{menuItem_facProfile.disabled}" value="false" />
										</af:commandLink>
										<af:commandLink action="#{facilityProfile.submitProfile}"
											rendered="#{osv.versionId != -1}">
											<af:outputText value="#{osv.fpId}" />
											<t:updateActionListener property="#{facilityProfile.fpId}"
												value="#{osv.fpId}" />
											<t:updateActionListener
												property="#{menuItem_facProfile.disabled}" value="false" />
										</af:commandLink>
									</af:column>
									<af:column formatType="text" headerText="Facility Name">
										<af:inputText readOnly="true" value="#{osv.facilityNm}" />
									</af:column>
									<af:column headerText="Enforcement Info">
										<af:column formatType="text" headerText="Enforcement Id">
											<af:commandLink rendered="#{osv.enforcementId != null}"
												text="#{osv.enforcementId}"
												action="#{enforcementDetail.submitEnforcement}">
												<af:setActionListener from="#{osv.enforcementId}"
													to="#{enforcementDetail.enforcementId}" />
												<t:updateActionListener
													property="#{menuItem_enforcementDetail.disabled}"
													value="false" />
												<t:updateActionListener
													property="#{enforcementDetail.fromTODOList}" value="false" />
											</af:commandLink>
										</af:column>
										<af:column formatType="text" headerText="Action Date">
											<af:commandLink rendered="#{osv.enforcementId != null}"
												action="#{enforcementDetail.startEditEnforcementAction}"
												useWindow="true" windowWidth="650" windowHeight="800">
												<af:selectInputDate readOnly="true"
													value="#{osv.actionDate}" />
												<af:setActionListener from="#{osv.enforcementId}"
													to="#{enforcementDetail.enforcementId}" />
												<af:setActionListener from="#{osv.actionId}"
													to="#{enforcementDetail.enforcementActionId}" />
												<af:setActionListener from="#{osv.facilityId}"
													to="#{enforcementDetail.facility}" />
											</af:commandLink>
											<af:selectInputDate readOnly="true"
												rendered="#{osv.enforcementId == null}"
												value="#{osv.actionDate}" />
										</af:column>
									</af:column>
									<af:column formatType="text" headerText="AFS ID">
										<af:outputText value="#{osv.afsId}" />
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
						</h:panelGrid>

						<h:panelGrid border="1"
							rendered="#{cetaImport.hasExportReview && cetaImport.objectType == 'fce'}">
							<af:showDetailHeader text="Completed Inspection Records"
								disclosed="true">
								<af:table id="fceTable" emptyText=" "
									value="#{cetaImport.fceList}" var="fce" bandingInterval="1"
									banding="row" rows="500">
									<af:column headerText="SCSC ID">
										<af:inputText value="#{fce.scscId}" readOnly="true" />
										<af:outputText value="Missing"
											rendered="#{fce.scscId == null}"
											inlineStyle="color: orange; font-weight: bold;" />
									</af:column>
									<af:column formatType="icon" headerText="Inspection ID">
										<af:commandLink rendered="true" text="#{fce.id}"
											action="#{fceDetail.submit}">
											<af:setActionListener from="#{fce.id}"
												to="#{fceDetail.fceId}" />
											<af:setActionListener from="#{fce.facilityId}"
												to="#{fceDetail.facilityId}" />
											<af:setActionListener from="#{true}"
												to="#{fceDetail.existingFceObject}" />
											<af:setActionListener from="#{fce.id}"
												to="#{fceSiteVisits.fceId}" />
										</af:commandLink>
									</af:column>
									<jsp:include flush="true" page="../ceta/firstFceList.jsp" />
									<jsp:include flush="true" page="../ceta/fceList.jsp" />
									<af:column formatType="text" headerText="AFS Complete ID">
										<af:outputText value="#{fce.evalAfsId}" />
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
						</h:panelGrid>

						<h:panelGrid border="1"
							rendered="#{cetaImport.hasExportReview && cetaImport.objectType == 'sf'}">
							<af:showDetailHeader text="Scheduled Inspection Records"
								disclosed="true">
								<af:table id="fceTable" emptyText=" "
									value="#{cetaImport.fceList}" var="fce" bandingInterval="1"
									banding="row" rows="500">
									<af:column headerText="SCSC ID">
										<af:inputText value="#{fce.scscId}" readOnly="true" />
										<af:outputText value="Missing"
											rendered="#{fce.scscId == null}"
											inlineStyle="color: orange; font-weight: bold;" />
									</af:column>
									<af:column formatType="icon" headerText="Inspection ID">
										<af:commandLink rendered="true" text="#{fce.id}"
											action="#{fceDetail.submit}">
											<af:setActionListener from="#{fce.id}"
												to="#{fceDetail.fceId}" />
											<af:setActionListener from="#{fce.facilityId}"
												to="#{fceDetail.facilityId}" />
											<af:setActionListener from="#{true}"
												to="#{fceDetail.existingFceObject}" />
											<af:setActionListener from="#{fce.id}"
												to="#{fceSiteVisits.fceId}" />
										</af:commandLink>
									</af:column>
									<jsp:include flush="true" page="../ceta/firstFceList.jsp" />
									<jsp:include flush="true" page="../ceta/fceList.jsp" />
									<af:column formatType="text" headerText="AFS Schedule ID">
										<af:outputText value="#{fce.schedAfsId}" />
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
						</h:panelGrid>

						<h:panelGrid border="1"
							rendered="#{cetaImport.hasExportReview && cetaImport.objectType == 'et'}">
							<af:showDetailHeader text="Stack Test Records"
								disclosed="true">
								<af:table var="st" bandingInterval="1" banding="row"
									width="100%" value="#{cetaImport.stackTests}" rows="500">
									<af:column headerText="SCSC ID">
										<af:inputText value="#{st.scscId}" readOnly="true" />
										<af:outputText value="Missing" rendered="#{st.scscId == null}"
											inlineStyle="color: orange; font-weight: bold;" />
									</af:column>
									<jsp:include flush="true"
										page="../ceta/firstStackTestColumns.jsp" />
									<af:column headerText="EU">
										<af:outputText value="#{st.epaEmuId}" />
									</af:column>
									<af:column headerText="SCC ID">
										<af:outputText value="#{st.sccId}" />
									</af:column>
									<af:column formatType="text" headerText="Stack Test Method">
										<af:selectOneChoice value="#{st.stackTestMethodCd}"
											readOnly="true">
											<f:selectItems
												value="#{compEvalDefs.stackTestMethodDef.items[(empty st.stackTestMethodCd ? '' : st.stackTestMethodCd)]}" />
										</af:selectOneChoice>
									</af:column>
									<af:column headerText="Pollutant">
										<af:column sortable="false" formatType="text"
											headerText="Name">
											<af:selectOneChoice value="#{st.pollutantCd}" readOnly="true">
												<f:selectItems
													value="#{facilityReference.pollutantDefs.items[(empty st.pollutantCd ? '' : st.pollutantCd)]}" />
											</af:selectOneChoice>
										</af:column>
										<af:column formatType="text" headerText="Code">
											<af:outputText value="#{st.pollutantCd}" />
										</af:column>
									</af:column>
									<af:column formatType="icon" headerText="Results">
										<af:selectOneChoice unselectedLabel="" id="testResults"
											inlineStyle="#{st.stackTestResultsCd=='FF'?'color: orange; font-weight: bold;':''}"
											readOnly="true" value="#{st.stackTestResultsCd}">
											<f:selectItems
												value="#{compEvalDefs.stackTestResultsDef.items[(empty st.stackTestResultsCd ? '' : st.stackTestResultsCd)]}" />
										</af:selectOneChoice>
									</af:column>
									<af:column formatType="text" headerText="Test Date"
										noWrap="true">
										<af:panelHorizontal>
											<af:selectInputDate readOnly="true" id="visitDt"
												value="#{st.testDate}" />
										</af:panelHorizontal>
									</af:column>
									<af:column formatType="icon" headerText="Inspection ID">
										<af:outputText id="fceId" value="#{st.fceId}" />
									</af:column>
									<af:column formatType="text" headerText="Reviewer">

										<af:selectOneChoice value="#{st.reviewerId}" readOnly="true"
											id="userSelectOne">
											<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
										</af:selectOneChoice>
									</af:column>
									<af:column formatType="icon" headerText="Date Evaluated">
										<af:selectInputDate readOnly="true"
											value="#{st.dateEvaluated}" />
									</af:column>
									<af:column headerText="AFS ID">
										<af:selectInputDate readOnly="true" value="#{st.afsId}" />
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
						</h:panelGrid>

						<h:panelGrid border="1"
							rendered="#{cetaImport.hasExportReview && cetaImport.objectType == 'ea'}">
							<af:showDetailHeader text="Enforcement Action Records"
								disclosed="true">

								<af:table var="action" bandingInterval="1" banding="row"
									value="#{cetaImport.enforceActionList}" rows="500">
									<af:column headerText="SCSC ID">
										<af:inputText value="#{action.enforcement.scscId}"
											readOnly="true" />
										<af:outputText value="Missing"
											rendered="#{action.enforcement.scscId == null}"
											inlineStyle="color: orange; font-weight: bold;" />
									</af:column>
									<af:column rendered="true" formatType="text"
										headerText="Enforcement Id">
										<af:commandLink rendered="true" text="#{action.enforcementId}"
											action="#{enforcementDetail.submitEnforcement}">
											<af:setActionListener from="#{action.enforcementId}"
												to="#{enforcementDetail.enforcementId}" />
											<t:updateActionListener
												property="#{menuItem_enforcementDetail.disabled}"
												value="false" />
											<t:updateActionListener
												property="#{enforcementDetail.fromTODOList}" value="false" />
										</af:commandLink>
									</af:column>

									<af:column formatType="text" headerText="Facility Id">
										<af:commandLink text="#{action.enforcement.facilityId}"
											action="#{facilityProfile.submitProfileById}"
											inlineStyle="white-space: nowrap;">
											<t:updateActionListener
												property="#{facilityProfile.facilityId}"
												value="#{action.enforcement.facilityId}" />
											<t:updateActionListener
												property="#{menuItem_facProfile.disabled}" value="false" />
										</af:commandLink>
									</af:column>

									<af:column formatType="text" headerText="Facility Name">
										<af:inputText readOnly="true"
											value="#{action.enforcement.facilityNm}" />
										<af:inputText readOnly="true"
											rendered="#{action.discoveryObjAfsId == null && action.enfDiscoveryId == -1}"
											value="Cannot be Exported since Discovery Object on this or previous Action has no ID or is not fully Exported"
											inlineStyle="color: orange; font-weight: bold;" />
									</af:column>

									<af:column formatType="icon" headerText="State Day Zero">
										<af:selectInputDate readOnly="true"
											value="#{action.enforcement.dayZeroDate}" />
									</af:column>

									<af:column formatType="text" headerText="Action Date">
										<af:panelHorizontal>
											<af:commandLink rendered="true"
												action="#{enforcementDetail.startEditEnforcementAction}"
												useWindow="true" windowWidth="650" windowHeight="800">
												<af:selectInputDate readOnly="true"
													value="#{action.actionDate}" />
												<af:setActionListener from="#{action.enforcementId}"
													to="#{enforcementDetail.enforcementId}" />
												<af:setActionListener from="#{action.actionId}"
													to="#{enforcementDetail.enforcementActionId}" />
												<af:setActionListener from="#{action.facilityId}"
													to="#{enforcementDetail.facility}" />

											</af:commandLink>
											<af:objectSpacer width="3" height="3" />
											<af:objectImage source="/images/Lock_icon1.png"
												rendered="#{action.afsId != null}" />
										</af:panelHorizontal>
									</af:column>

									<af:column formatType="text" headerText="Action Type">
										<af:selectOneChoice id="actionTypeChoice" readOnly="true"
											value="#{action.enfActionTypeCd}">
											<f:selectItems
												value="#{compEvalDefs.afsEnforcementTypeDef.items[(empty action.enfActionTypeCd ? '' : action.enfActionTypeCd)]}" />
										</af:selectOneChoice>
									</af:column>
									<af:column formatType="text" headerText="Cash Amount">
										<af:inputText readOnly="true"
											value="#{action.finalCashAmount}" />
									</af:column>
									<af:column formatType="text" headerText="HPV">
										<af:selectOneRadio id="hpvCheckBox"
											value="#{action.enforcement.addToHPVList}" readOnly="true">
											<f:selectItem itemLabel="Yes" itemValue="true" />
											<f:selectItem itemLabel="No" itemValue="false" />
										</af:selectOneRadio>
									</af:column>

									<af:column formatType="text" headerText="Inspector(s)">
										<af:selectManyListbox value="#{action.evaluators}"
											readOnly="true">
											<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
										</af:selectManyListbox>
									</af:column>

									<af:column formatType="text" headerText="Enforcement State">
										<af:selectOneChoice id="enfCaseState" readOnly="true"
											value="#{action.enforcement.enfCaseState}">
											<f:selectItems
												value="#{enfCaseStateDefs.enfCaseStateDef.items[(empty action.enforcement.enfCaseState ? '' : action.enforcement.enfCaseState)]}" />
										</af:selectOneChoice>
									</af:column>

									<af:column formatType="text" headerText="AFS ID">
										<af:outputText value="#{action.afsId}" />
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
						</h:panelGrid>

						<h:panelGrid border="1"
							rendered="#{cetaImport.hasExportReview && cetaImport.objectType == 'tv'}">
							<af:showDetailHeader text="TV CC Records" disclosed="true">
								<af:table width="100%" value="#{cetaImport.tvCcs}"
									bandingInterval="1" banding="row" emptyText=" " var="report"
									rows="500">
									<af:column headerText="SCSC ID">
										<af:inputText value="#{report.scscId}" readOnly="true" />
										<af:outputText value="Missing"
											rendered="#{report.scscId == null}"
											inlineStyle="color: orange; font-weight: bold;" />
									</af:column>
									<af:column noWrap="true" formatType="text"
										headerText="Report ID">
										<af:commandLink action="#{complianceReport.viewDetail}"
											rendered="#{!complianceReportSearch.enforcementLink}"
											text="#{report.reportId}">
											<t:updateActionListener
												property="#{complianceReport.reportId}"
												value="#{report.reportId}" />
											<t:updateActionListener
												property="#{menuItem_compReportDetail.disabled}"
												value="false" />
											<t:updateActionListener
												property="#{complianceReport.fromTODOList}" value="false" />
										</af:commandLink>
										<af:commandLink
											actionListener="#{enforcementDetail.cancelLinkToDiscovery}"
											rendered="#{complianceReportSearch.enforcementLink}"
											text="#{report.reportId}">
											<t:updateActionListener
												property="#{enforcementDetail.enfDiscoveryId}"
												value="#{report.reportId}" />
										</af:commandLink>
									</af:column>

									<af:column headerText="Facility ID" formatType="text">
										<af:panelHorizontal valign="middle" halign="center">
											<af:commandLink text="#{report.facilityId}"
												action="#{facilityProfile.submitProfileById}"
												inlineStyle="white-space: nowrap;">
												<t:updateActionListener
													property="#{facilityProfile.facilityId}"
													value="#{report.facilityId}" />
												<t:updateActionListener
													property="#{menuItem_facProfile.disabled}" value="false" />
											</af:commandLink>
										</af:panelHorizontal>
									</af:column>

									<af:column formatType="text" headerText="Facility Name">
										<af:outputText value="#{report.facilityName}" />
									</af:column>

									<af:column formatType="text"
										rendered="#{!complianceReportSearch.enforcementLink}"
										headerText="DO/LAA">
										<af:selectOneChoice id="doLaa" readOnly="true"
											value="#{report.doLaaCd}">
											<f:selectItems value="#{reportSearch.doLaas}" />
										</af:selectOneChoice>
									</af:column>

									<af:column formatType="text" headerText="Received Date">
										<af:panelHorizontal>
											<af:selectInputDate readOnly="true"
												value="#{report.receivedDate}" />
											<af:objectSpacer width="3" height="3" />
											<af:objectImage source="/images/Lock_icon1.png"
												rendered="#{report.tvccAfsId != null}" />
										</af:panelHorizontal>
									</af:column>

									<af:column formatType="text" headerText="Reviewed Date">
										<af:selectInputDate readOnly="true"
											value="#{report.reviewDate}" />
									</af:column>

									<af:column formatType="text" headerText="Period">
										<af:outputText value="#{report.reportingPeriodDesc}" />
									</af:column>

									<af:column formatType="text" headerText="Accepted">
										<af:selectOneChoice label="Accepted" readOnly="true"
											value="#{report.acceptable}">
											<f:selectItems
												value="#{complianceReport.complianceReportAcceptedDef.items[(empty '')]}" />
										</af:selectOneChoice>
									</af:column>

									<af:column formatType="text" headerText="Report Status">
										<af:selectOneChoice
											rendered="#{report.reportType != 'tvcc' || report.reportStatus=='drft'}"
											readOnly="true" value="#{report.reportStatus}">
											<f:selectItems
												value="#{complianceReport.complianceReportStatusDef.items[(empty '')]}" />
										</af:selectOneChoice>
										<af:selectOneChoice
											rendered="#{report.reportType == 'tvcc' && report.reportStatus!='drft'}"
											readOnly="true" value="#{report.complianceStatusCd}">
											<f:selectItems
												value="#{complianceReport.complianceReportTVCCComplianceStatusDef.items[(empty '')]}" />
										</af:selectOneChoice>
									</af:column>

									<af:column formatType="text" headerText="AFS ID">
										<af:outputText value="#{report.tvccAfsId}" />
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
						</h:panelGrid>
					</afh:rowLayout>
					<af:objectSpacer width="100%" height="15" />
					<afh:rowLayout halign="center" partialTriggers="EI obj if"
						rendered="#{cetaImport.hasExportReview && !cetaImport.hasExported}">
						<h:panelGrid border="0">
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="#{cetaImport.lockButton}"
										action="#{cetaImport.lockCetaData}" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</h:panelGrid>
					</afh:rowLayout>
					<h:panelGrid border="1" rendered="#{cetaImport.hasExported}">
						<af:showDetailHeader text="AFS 9.1 Plant General records"
							disclosed="true">
							<af:table var="t1" bandingInterval="1" banding="row" width="100%"
								value="#{cetaImport.afs_1_list}" rows="500">
								<af:column headerText="SCSC ID">
									<af:inputText value="#{t1.scscId}" readOnly="true" />
								</af:column>
								<af:column headerText="Facilitly Name">
									<af:inputText value="#{t1.facilityName}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Street Address">
									<af:inputText value="#{t1.address1}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="City Name">
									<af:inputText value="#{t1.city}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Zip Code">
									<af:inputText value="#{t1.zip}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Gov/Fac Code">
									<af:inputText value="#{t1.govFacCd}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="NAICS Code">
									<af:inputText value="#{t1.naicsCd}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="State Registration">
									<af:inputText value="#{t1.facilityId}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<f:facet name="footer">
									<afh:rowLayout halign="center">
										<af:panelButtonBar>
											<af:commandButton
												actionListener="#{tableExporter.printTable}"
												onclick="#{tableExporter.onClickScript}"
												text="Printable view" />
										</af:panelButtonBar>
									</afh:rowLayout>
								</f:facet>
							</af:table>
						</af:showDetailHeader>
						<af:showDetailHeader text="AFS 9.2 Plant General records"
							disclosed="true">
							<af:table var="t2" bandingInterval="1" banding="row" width="100%"
								value="#{cetaImport.afs_2_list}" rows="500">
								<af:column headerText="SCSC ID">
									<af:inputText value="#{t2.scscId}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Air Program Code">
									<af:inputText value="#{t2.airProgramCd}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Air Program Status">
									<af:inputText value="#{t2.airProgramOpStat}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="CFR Subparts">
									<af:inputText value="#{t2.allCfrSubparts1}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<f:facet name="footer">
									<afh:rowLayout halign="center">
										<af:panelButtonBar>
											<af:commandButton
												actionListener="#{tableExporter.printTable}"
												onclick="#{tableExporter.onClickScript}"
												text="Printable view" />
										</af:panelButtonBar>
									</afh:rowLayout>
								</f:facet>
							</af:table>
						</af:showDetailHeader>
						<af:showDetailHeader text="AFS 9.3 Pollutant records"
							disclosed="true">
							<af:table var="t3" bandingInterval="1" banding="row" width="100%"
								value="#{cetaImport.afs_3_list}" rows="500">
								<af:column headerText="SCSC ID">
									<af:inputText value="#{t3.scscId}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Air Program Code">
									<af:inputText value="#{t3.airProgramCd}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Pollutant">
									<af:inputText value="#{t3.pollutantCd}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="CAS NUM">
									<af:inputText value="#{t3.casNum}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Compliance Status">
									<af:inputText value="#{t3.complianceStat}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Class">
									<af:inputText value="#{t3.classCd}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Attainment Indicator">
									<af:inputText value="#{t3.attainment}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<f:facet name="footer">
									<afh:rowLayout halign="center">
										<af:panelButtonBar>
											<af:commandButton
												actionListener="#{tableExporter.printTable}"
												onclick="#{tableExporter.onClickScript}"
												text="Printable view" />
										</af:panelButtonBar>
									</afh:rowLayout>
								</f:facet>
							</af:table>
						</af:showDetailHeader>
						<af:showDetailHeader text="AFS 9.4 Actions records"
							disclosed="true">
							<af:table var="t4" bandingInterval="1" banding="row" width="100%"
								value="#{cetaImport.afs_4_list}" rows="500">
								<af:column headerText="SCSC ID">
									<af:inputText value="#{t4.scscId}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Action Number">
									<af:inputText value="#{t4.afsActionId}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Air Program Codes">
									<af:inputText value="#{t4.allAirProgramCds}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Action Type">
									<af:inputText value="#{t4.actionType}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Scheduled">
									<af:inputText value="#{t4.dateScheduled}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Achieved">
									<af:inputText value="#{t4.dateAchieved}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Penalty">
									<af:inputText value="#{t4.penaltyAmount}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Results CD">
									<af:inputText value="#{t4.resultsCode}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Pollutant CD">
									<af:inputText value="#{t4.pollutantCd}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="CAS NUM">
									<af:inputText value="#{t4.casNum}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Key Action Number">
									<af:inputText value="#{t4.keyActionNumber}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<af:column headerText="Comment">
									<af:inputText value="#{t4.comment}" readOnly="true"
										inlineStyle="#{st.recordInError?'color: orange; font-weight: bold;':''}" />
								</af:column>
								<f:facet name="footer">
									<afh:rowLayout halign="center">
										<af:panelButtonBar>
											<af:commandButton
												actionListener="#{tableExporter.printTable}"
												onclick="#{tableExporter.onClickScript}"
												text="Printable view" />
										</af:panelButtonBar>
									</afh:rowLayout>
								</f:facet>
							</af:table>
						</af:showDetailHeader>
					</h:panelGrid>

					<af:objectSpacer width="100%" height="15" />
					<afh:rowLayout halign="center">
						<h:panelGrid border="1" rendered="#{cetaImport.hasImportResults}">
							<af:showDetailHeader text="Updated TVCC Records" disclosed="true">
								<af:table width="100%" value="#{cetaImport.reportList}"
									bandingInterval="1" banding="row" emptyText=" " var="report"
									rows="#{cetaImport.pageLimit}">

									<af:column noWrap="true" formatType="text"
										headerText="Report Id">
										<af:commandLink action="#{complianceReport.viewDetail}"
											text="#{report.reportId}">
											<t:updateActionListener
												property="#{complianceReport.reportId}"
												value="#{report.reportId}" />
											<t:updateActionListener
												property="#{menuItem_compReportDetail.disabled}"
												value="false" />
										</af:commandLink>
									</af:column>

									<af:column headerText="Facility ID" formatType="text">
										<af:commandLink text="#{report.facilityId}"
											action="#{facilityProfile.submitProfileById}"
											inlineStyle="white-space: nowrap;">
											<t:updateActionListener
												property="#{facilityProfile.facilityId}"
												value="#{report.facilityId}" />
											<t:updateActionListener
												property="#{menuItem_facProfile.disabled}" value="false" />
										</af:commandLink>
									</af:column>

									<af:column rendered="#{!complianceReportSearch.singleFacility}"
										formatType="text" headerText="Facility Name">
										<af:outputText value="#{report.facilityName}" />
									</af:column>

									<af:column formatType="text" headerText="Submitted Date">
										<af:selectInputDate readOnly="true"
											value="#{report.receivedDate}" />
									</af:column>

									<af:column formatType="text" headerText="Period">
										<af:outputText value="#{report.reportingPeriodDesc}" />
									</af:column>

									<af:column formatType="text" headerText="Report Status">
										<af:selectOneChoice label="Report Status" readOnly="true"
											value="#{report.reportStatus}">
											<f:selectItems
												value="#{complianceReport.complianceReportStatusDef.items[(empty '')]}" />
										</af:selectOneChoice>

									</af:column>

									<af:column formatType="text" headerText="AFS ID">
										<af:outputText value="#{report.tvccAfsId}" />
									</af:column>
								</af:table>
							</af:showDetailHeader>
						</h:panelGrid>
					</afh:rowLayout>
					<afh:rowLayout halign="center" rendered="#{cetaImport.hasExported}">
						<af:goLink destination="#{cetaImport.docsZipFileURL}"
							text="#{cetaImport.docsZipFileName}" />
					</afh:rowLayout>
					<afh:rowLayout halign="center" partialTriggers="EI obj if">
						<h:panelGrid border="1" rendered="#{cetaImport.hasSchedExported}">
							<af:showDetailHeader text="Inspection Scheduled records" disclosed="true">
								<af:table var="fceS" bandingInterval="1" banding="row"
									width="100%" value="#{cetaImport.afs_sched_list}" rows="500">
									<af:column headerText="SCSC ID">
										<af:inputText value="#{fceS.scscId}" readOnly="true" />
									</af:column>
									<af:column formatType="text" headerText="Facility Id">
										<af:commandLink text="#{fceS.facilityId}"
											action="#{facilityProfile.submitProfileById}"
											inlineStyle="white-space: nowrap;">
											<t:updateActionListener
												property="#{facilityProfile.facilityId}"
												value="#{fceS.facilityId}" />
											<t:updateActionListener
												property="#{menuItem_facProfile.disabled}" value="false" />
										</af:commandLink>
									</af:column>
									<af:column headerText="Facilitly Name">
										<af:inputText value="#{fceS.facilityName}" readOnly="true" />
									</af:column>
									<af:column headerText="Inspection ID">
										<af:inputText value="#{fceS.fceId}" readOnly="true" />
									</af:column>
									<af:column headerText="Inspection Classification"
										formatType="icon">
										<af:inputText value="#{fceS.inspectClass}" readOnly="true" />
									</af:column>
									<af:column headerText="Federal Fiscal Year">
										<af:inputText value="#{fceS.year}" readOnly="true" />
									</af:column>
								</af:table>
							</af:showDetailHeader>
						</h:panelGrid>
					</afh:rowLayout>
					<afh:rowLayout halign="center"
						rendered="#{cetaImport.hasSchedExported}">
						<af:goLink destination="#{cetaImport.schedFileDoc}"
							text="#{cetaImport.docsSchedFileName}" />
					</afh:rowLayout>

				</af:panelForm>
			</af:page>
		</af:form>
	</af:document>
</f:view>