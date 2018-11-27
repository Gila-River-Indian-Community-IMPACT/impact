<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="body" title="Periodic Facility Trend Report">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
			
			<%@ include	file="../scripts/navigate.js"%>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Periodic Facility Trend Report">

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
						</af:panelForm>
						
						<af:objectSpacer height="10" />

						<af:panelHorizontal halign="center">
							<af:table id="periodicLimitTrendWrapper"
								value="#{facilityProfile.periodicLimitTrendWrapper}"
								binding="#{facilityProfile.periodicLimitTrendWrapper.table}"
								bandingInterval="1" banding="row" emptyText=" " width="1000"
								var="limitTrend" rows="#{facilityProfile.pageLimit}">

								<af:column formatType="text" headerText="Report ID"
									sortProperty="reportCRPTId" sortable="true" id="reportCRPTId"
									width="15%">
									<af:commandLink 
										text="#{limitTrend.reportCRPTId}"
										onclick="setFocus()" >
										<t:updateActionListener value="#{limitTrend.reportId}"
											property="#{facilityProfile.trendRptComplianceReportId}" />
										<t:updateActionListener  value="false"
											property="#{menuItem_compReportDetail.disabled}" />
									</af:commandLink>
								</af:column>
								<af:column formatType="text" headerText="Monitor ID"
									sortProperty="monId" sortable="true" id="monId"
									width="5%">
									<af:inputText value="#{limitTrend.monId}" readOnly="true" />
								</af:column>
								<af:column formatType="text" headerText="Monitor Details"
									sortProperty="monitorDetails" sortable="true" id="monitorDetails"
									width="30%">
									<af:outputText value="#{limitTrend.monitorDetails}" truncateAt="50"
										shortDesc="#{limitTrend.monitorDetails}"/>
								</af:column>
								<af:column formatType="text" headerText="Limit ID"
									sortProperty="limId" sortable="true" id="limId"
									width="5%">
									<af:inputText value="#{limitTrend.limId}" readOnly="true" />
								</af:column>
								<af:column formatType="text" headerText="Limit Description"
									sortProperty="limitDesc" sortable="true" id="limitDesc"
									width="15%">
									<af:outputText value="#{limitTrend.limitDesc}" truncateAt="50"
										shortDesc="#{limitTrend.limitDesc}"/>
								</af:column>
								<af:column formatType="number" headerText="Report Year"
									sortProperty="reportYear" sortable="true" id="reportYear"
									width="5%">
									<af:inputText value="#{limitTrend.reportYear}" readOnly="true" />
								</af:column>
								<af:column formatType="number" headerText="Report Quarter"
									sortProperty="reportQuarter" sortable="true" id="reportQuarter"
									width="5%">
									<af:inputText value="#{limitTrend.reportQuarter}" readOnly="true" />
								</af:column>
								<af:column formatType="text" headerText="Received Date"
									sortProperty="receivedDate" sortable="true" id="receivedDate"
									width="10%">
									<af:selectInputDate value="#{limitTrend.receivedDate}" readOnly="true" />
								</af:column>
								<af:column formatType="text" headerText="QA/QC Accepted Date"
									sortProperty="QAQCAcceptedDate" sortable="true"
									id="QAQCAcceptedDate" width="10%">
									<af:selectInputDate value="#{limitTrend.QAQCAcceptedDate}" readOnly="true" />
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