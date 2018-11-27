<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
	<h:panelGrid border="1"
		rendered="#{bulkOperationsCatalog.hasComplianceReportSearchResults}">
		<af:panelBorder>
			<af:showDetailHeader text="Compliance Report List" disclosed="true">

				<af:table value="#{bulkOperationsCatalog.complianceReports}"
					bandingInterval="1" banding="row" var="report"
					rows="#{bulkOperationsCatalog.pageLimit}">
					<f:facet name="header">
						<afh:rowLayout halign="left">
							<af:panelButtonBar>
								<af:commandButton action="#{bulkOperationsCatalog.selectAllCR}"
									text="Select All" />
							</af:panelButtonBar>
							<af:objectSpacer height="1" width="6" />
							<af:panelButtonBar>
								<af:commandButton action="#{bulkOperationsCatalog.selectNoneCR}"
									text="Select None" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
					<af:column sortable="true" sortProperty="selected" formatType="icon" headerText="Select">
						<af:selectBooleanCheckbox label="Selected"
							value="#{report.selected}" rendered="true">
							<f:selectItems value="#{report.selected}" />
						</af:selectBooleanCheckbox>
					</af:column>

					<af:column sortable="true" sortProperty="facilityId" formatType="text"
						headerText="Facility Id">
						<af:outputText value="#{report.facilityId}" />
					</af:column>

					<af:column sortable="true" sortProperty="facilityName" formatType="text"
						headerText="Facility Name">
						<af:outputText value="#{report.facilityName}" />
					</af:column>

					<af:column sortable="true" sortProperty="reportType" formatType="text"
						headerText="Report Type">
						<af:selectOneChoice label="Report Type" readOnly="true"
							value="#{report.reportType}">
							<f:selectItems
								value="#{complianceReport.complianceReportTypesDef.items[(empty '')]}" />
						</af:selectOneChoice>
					</af:column>

					<af:column sortable="true" sortProperty="description" rendered="true" formatType="text"
						headerText="Description">
						<af:outputText value="#{report.description}" />
					</af:column>

					<af:column sortable="true" sortProperty="otherTypeDsc" rendered="true" formatType="text"
						headerText="Category">
						<af:outputText value="#{report.otherTypeDsc}" />
					</af:column>

					<af:column sortable="true" sortProperty="submittedDate" formatType="text"
						headerText="Submitted Date">
						<af:selectInputDate readOnly="true" value="#{report.submittedDate}" />
					</af:column>

					<af:column sortable="true" sortProperty="reportingPeriodDesc" formatType="text" headerText="Period">
						<af:outputText value="#{report.reportingPeriodDesc}" />
					</af:column>

					<af:column sortable="true" sortProperty="acceptable" formatType="text" headerText="Accepted">
						<af:selectOneChoice label="Accepted" readOnly="true"
							value="#{report.acceptable}">
							<f:selectItems
								value="#{complianceReport.complianceReportAcceptedDef.items[(empty '')]}" />
						</af:selectOneChoice>
					</af:column>

					<af:column sortable="true" sortProperty="reportStatus" formatType="text"
						headerText="Report Status">
						<af:selectOneChoice label="Report Status" readOnly="true"
							value="#{report.reportStatus}">
							<f:selectItems
								value="#{complianceReport.complianceReportStatusDef.items[(empty '')]}" />
						</af:selectOneChoice>
					</af:column>

					<f:facet name="footer">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>

								<af:commandButton
									text="#{bulkOperationsCatalog.bulkOperation.buttonName}"
									useWindow="true" windowWidth="800" windowHeight="600"
									action="#{bulkOperationsCatalog.setSelectedComplianceReports}" />
								<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />
								<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />

							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
				</af:table>

			</af:showDetailHeader>
		</af:panelBorder>
	</h:panelGrid>
</afh:rowLayout>
