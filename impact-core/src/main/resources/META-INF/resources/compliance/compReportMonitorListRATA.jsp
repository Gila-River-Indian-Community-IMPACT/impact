<%@ page session="true" contentType="text/html;charset=windows-1252"%> 
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical" rendered="true">
	<af:showDetailHeader text="#{complianceReportMonitor.sectionLabel}" disclosed="true">
		<h:panelGrid columns="1" border="1"
			width="995">

			<af:panelBox background="light" width="100%">
				<af:panelForm rows="1" maxColumns="3">

						<af:inputText id="truncatedMonitorDetails" 
							label="Monitor Details:"
							readOnly="true"
							value="#{complianceReportMonitor.truncatedMonitorDetails}"
							shortDesc="#{complianceReportMonitor.monitorDetails}" />
											
						<af:selectInputDate id="testDate"
							label="Test Date:"
							readOnly="#{complianceReport.locked || !complianceReport.editMode}"
							value="#{complianceReportMonitor.testDate}" >
							<af:validateDateTimeRange minimum="1900-01-01"
								maximum="#{infraDefs.currentDate}" />
						</af:selectInputDate>

						<af:selectBooleanCheckbox label="This is a Certification"
							id="certificationFlag"
							readOnly="#{complianceReport.locked || !complianceReport.editMode}"
							value="#{complianceReportMonitor.certificationFlag}" />
					</af:panelForm>
			</af:panelBox>
											
					

			<af:panelGroup>
				<afh:rowLayout halign="center">
					<af:table id="monitorLimits" 
						value="#{complianceReportMonitor.complianceReportLimitList}"
						bandingInterval="1" banding="row" 
						width="990"
						var="monitorLimit" 
						rows="#{complianceReport.pageLimit}" emptyText=" ">

						<af:column id="includedFlag" headerText="Included in Report"
							width="5%" sortable="true" sortProperty="includedFlag"
							formatType="icon">
							<af:selectBooleanCheckbox label="Included in Report" id="includedFlag"
								readOnly="#{complianceReport.locked || !complianceReport.editMode}"
								value="#{monitorLimit.includedFlag}" />
						</af:column>

						<af:column id="limId" headerText="Limit" width="1%"
							sortable="true" sortProperty="limId" formatType="text">
							<af:outputText
								value="#{monitorLimit.limId}" />
						</af:column>
						
						<af:column id="limitDesc" headerText="Limit Description" width="50%"
							sortable="true" sortProperty="limitDesc" formatType="text">
							<af:outputText
								value="#{monitorLimit.limitDesc}" />
						</af:column>

						<af:column id="endDate" headerText="End Monitoring Limit" width="5%"
							sortable="true" sortProperty="endDate" formatType="text">
							<af:selectInputDate id="testDate"
								readOnly="true"
								value="#{monitorLimit.endDate}" />
						</af:column>

						<af:column id="limitStatus" headerText="RATA Result" width="10%"
							sortable="true" sortProperty="limitStatus" formatType="text">
							<af:selectOneChoice id="limitStatus" unselectedLabel=""
								label="RATA Result:"
								readOnly="#{complianceReport.locked || !complianceReport.editMode}"
								value="#{monitorLimit.limitStatus}">
								<f:selectItems
									value="#{complianceReport.complianceReportMonitorAndLimitAuditStatusDef.items[(empty monitorLimit.limitStatus ? '' : monitorLimit.limitStatus)]}" />
							</af:selectOneChoice>
						</af:column>



						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelGroup layout="horizontal">
									<af:commandButton actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}" text="Printable view" />
									<af:commandButton actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
								</af:panelGroup>
							</afh:rowLayout>
						</f:facet>
					</af:table>
				</afh:rowLayout>

				<af:objectSpacer height="10" />

			</af:panelGroup>
		</h:panelGrid>
	</af:showDetailHeader>
</af:panelGroup>


