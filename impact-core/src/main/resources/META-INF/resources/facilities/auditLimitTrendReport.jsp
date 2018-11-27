<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="body" title="Audit Limit Trend Report">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
			<%@ include	file="../scripts/navigate.js"%>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Audit Limit Trend Report">

				<h:panelGrid border="1">

					<af:panelBorder>
						<af:panelForm>
							<af:inputText id="facilityId" label="Facility ID: "
								value="#{facilityProfile.facility.facilityId}" readOnly="true" />
							<af:inputText id="facilityName" label="Facility Name: "
								value="#{facilityProfile.facility.name}" readOnly="true" />
							<af:inputText id="cmpId" label="Company ID: "
								value="#{facilityProfile.facility.owner.company.cmpId}"
								readOnly="true" />
							<af:inputText id="companyName" label="Company Name: "
								value="#{facilityProfile.facility.owner.company.name}"
								readOnly="true" />
							<af:inputText id="monId" label="Monitor: "
								value="#{facilityProfile.trendRptMonId}" readOnly="true" />
							<af:inputText id="monitorDetails" label="Monitor Details: "
								value="#{facilityProfile.trendRptMonitorDetails}"
								rows="6" columns="100"
								readOnly="true" />
							<af:inputText id="limId" label="Limit: "
								value="#{facilityProfile.trendRptLimId}" readOnly="true" />
							<af:inputText id="limitDesc" label="Limit Description: " 
								value="#{facilityProfile.trendRptLimitDesc}"
								rows="2" columns="100" 
								readOnly="true">
							</af:inputText>
						</af:panelForm>
						
						<af:objectSpacer height="10" />

						<af:panelHorizontal halign="center">
							<af:table id="auditLimitTrendWrapper"
								value="#{facilityProfile.auditLimitTrendWrapper}"
								binding="#{facilityProfile.auditLimitTrendWrapper.table}"
								bandingInterval="1" banding="row" emptyText=" " width="650"
								var="limitTrend" rows="#{facilityProfile.pageLimit}">

								<af:column formatType="text" headerText="Report ID"
									sortProperty="reportCRPTId" sortable="true" id="reportCRPTId"
									width="25%">
									<af:commandLink 
										text="#{limitTrend.reportCRPTId}"
										onclick="setFocus()" >
										<t:updateActionListener value="#{limitTrend.reportId}"
											property="#{facilityProfile.trendRptComplianceReportId}" />
										<t:updateActionListener  value="false"
											property="#{menuItem_compReportDetail.disabled}" />
									</af:commandLink>
								</af:column>
								<af:column formatType="number" headerText="Report Year"
									sortProperty="reportYear" sortable="true" id="reportYear"
									width="15%">
									<af:inputText value="#{limitTrend.reportYear}" readOnly="true" />
								</af:column>
								<af:column formatType="number" headerText="Report Quarter"
									sortProperty="reportQuarter" sortable="true" id="reportQuarter"
									width="10%">
									<af:inputText value="#{limitTrend.reportQuarter}" readOnly="true" />
								</af:column>


								
								<af:column formatType="text" headerText="Audit Type"
									sortProperty="auditType" sortable="true" id="auditType"
									width="25%">
									<af:inputText value="#{limitTrend.auditType}" readOnly="true" />
								</af:column>
								<af:column formatType="text" headerText="Audit Result"
									sortProperty="auditStatus" sortable="true" id="auditStatus"
									width="25%">
									<af:selectOneChoice id="status" unselectedLabel=""
										readOnly="true"	value="#{limitTrend.status}">
										<f:selectItems
											value="#{complianceReport.complianceReportMonitorAndLimitAuditStatusDef.items[(empty limitTrend.status ? '' : limitTrend.status)]}" />
									</af:selectOneChoice>
								</af:column>
								<af:column formatType="text" headerText="Audit Date"
									sortProperty="testDate" sortable="true" id="testDate"
									width="25%">
									<af:selectInputDate value="#{limitTrend.testDate}" readOnly="true" />
								</af:column>
								<af:column formatType="text" headerText="Certification"
									sortProperty="certification" sortable="true" id="certification"
									width="25%">
									<af:inputText value="#{limitTrend.certificationYesNo}" readOnly="true" />
								</af:column>

								
								
								<af:column formatType="text" headerText="Received Date"
									sortProperty="receivedDate" sortable="true" id="receivedDate"
									width="25%">
									<af:selectInputDate value="#{limitTrend.receivedDate}" readOnly="true" />
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
						</af:panelHorizontal>

					</af:panelBorder>
				</h:panelGrid>
			</af:page>
		</af:form>
	</af:document>
</f:view>