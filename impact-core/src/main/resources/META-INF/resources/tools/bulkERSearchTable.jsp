<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
	<h:panelGrid border="1"
		rendered="#{bulkOperationsCatalog.hasEmissionsReportSearchResults}">
		<af:panelBorder>
			<af:showDetailHeader text="Emissions Inventory List" disclosed="true">

				<af:table value="#{bulkOperationsCatalog.emissionsReports}"
					bandingInterval="1" banding="row" var="emissionsReport"
					rows="#{bulkOperationsCatalog.pageLimit}">
					<f:facet name="header">
						<afh:rowLayout halign="left">
							<af:panelButtonBar>
								<af:commandButton action="#{bulkOperationsCatalog.selectAllE}"
									text="Select All" />
							</af:panelButtonBar>
							<af:objectSpacer height="1" width="6" />
							<af:panelButtonBar>
								<af:commandButton action="#{bulkOperationsCatalog.selectNoneE}"
									text="Select None" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
					<af:column sortable="true" sortProperty="selected" formatType="icon" headerText="Select">
						<af:selectBooleanCheckbox label="Selected"
							value="#{emissionsReport.selected}" rendered="true">
							<f:selectItems value="#{emissionsReport.selected}" />
						</af:selectBooleanCheckbox>
					</af:column>

					<af:column sortProperty="facilityId"
						sortable="true" noWrap="true" formatType="text"
						headerText="Facility Id">
						<af:commandLink action="#{facilityProfile.submitProfile}"
							text="#{emissionsReport.facilityId}">
							<t:updateActionListener property="#{facilityProfile.fpId}"
								value="#{emissionsReport.fpId}" />
							<t:updateActionListener
								property="#{menuItem_facProfile.disabled}" value="false" />
						</af:commandLink>
					</af:column>

					<af:column sortProperty="facilityName"
						sortable="true" formatType="text" headerText="Facility Name">
						<af:outputText value="#{emissionsReport.facilityName}" />
					</af:column>

					<af:column sortProperty="emissionsInventoryId"
						sortable="true" formatType="text" headerText="Inventory ID">
						<af:outputText value="#{emissionsReport.emissionsInventoryId}" />
					</af:column>

					<af:column sortProperty="year" sortable="true"
						formatType="text" headerText="Emissions Inventory Year">
						<af:outputText value="#{emissionsReport.year}" />
					</af:column>

					<f:facet name="footer">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>

								<af:commandButton
									text="#{bulkOperationsCatalog.bulkOperation.buttonName}"
									useWindow="true" windowWidth="800" windowHeight="600"
									action="#{bulkOperationsCatalog.setSelectedEmissionsReports}" />
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

