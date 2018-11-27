<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
	<h:panelGrid border="1"
		rendered="#{bulkOperationsCatalog.hasInspectionSearchResults}">
		<af:panelBorder>
			<af:showDetailHeader text="Inspection List" disclosed="true">

				<af:table value="#{bulkOperationsCatalog.inspections}"
					bandingInterval="1" banding="row" var="inspection"
					rows="#{bulkOperationsCatalog.pageLimit}">
					<f:facet name="header">
						<afh:rowLayout halign="left">
							<af:panelButtonBar>
								<af:commandButton action="#{bulkOperationsCatalog.selectAllInsp}"
									text="Select All" />
							</af:panelButtonBar>
							<af:objectSpacer height="1" width="6" />
							<af:panelButtonBar>
								<af:commandButton action="#{bulkOperationsCatalog.selectNoneInsp}"
									text="Select None" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
					<af:column sortable="true" sortProperty="selected" formatType="icon" headerText="Select">
						<af:selectBooleanCheckbox label="Selected"
							value="#{inspection.selected}" rendered="true">
							<f:selectItems value="#{inspection.selected}" />
						</af:selectBooleanCheckbox>
					</af:column>

					<af:column headerText="Inspection ID" sortable="true"
						sortProperty="inspId" formatType="text">
						<af:panelHorizontal valign="middle" halign="left">
							<af:outputText value="#{inspection.inspId}" />
						</af:panelHorizontal>
					</af:column>

					<af:column headerText="Facility ID" sortable="true"
						sortProperty="facilityId" formatType="text">
						<af:panelHorizontal valign="middle" halign="center">
							<af:outputText value="#{inspection.facilityId}" />
						</af:panelHorizontal>
					</af:column>

					<af:column headerText="Facility Name" sortable="true"
						sortProperty="facilityNm">
						<af:panelHorizontal valign="middle" halign="left">
							<af:inputText readOnly="true" value="#{inspection.facilityNm}" />
						</af:panelHorizontal>
					</af:column>

					<af:column sortProperty="operatingStatusCd" sortable="true" formatType="text"
						headerText="Operating Status">
						<af:panelHorizontal valign="middle" halign="center">
							<af:outputText
							value="#{facilityReference.operatingStatusDefs.itemDesc[(empty inspection.operatingStatusCd ? '' : inspection.operatingStatusCd)]}" />
						</af:panelHorizontal>
					</af:column>

					<af:column headerText="Staff Assigned" sortable="true"
						sortProperty="fullName" formatType="text">
						<af:panelHorizontal valign="middle" halign="left">
							<af:inputText readOnly="true" value="#{inspection.fullName}" />
						</af:panelHorizontal>
					</af:column>

					<af:column headerText="Quarter Scheduled" sortable="true"
						sortProperty="scheduledTimestamp" formatType="text">
						<af:panelHorizontal valign="middle" halign="left">
							<af:inputText readOnly="true" value="#{inspection.scheduled}" />
						</af:panelHorizontal>
					</af:column>

					<af:column headerText="Inspector" sortable="true"
						sortProperty="evalFullName">
						<af:panelHorizontal valign="middle" halign="left">
							<af:inputText readOnly="true" value="#{inspection.evalFullName}" />
						</af:panelHorizontal>
					</af:column>

					<af:column headerText="Date Inspection Completed" sortable="true"
						sortProperty="dateEvaluated" formatType="text">
						<af:panelHorizontal valign="middle" halign="left">
							<af:selectInputDate readOnly="true"
								value="#{inspection.dateEvaluated}" />
						</af:panelHorizontal>
					</af:column>

					<f:facet name="footer">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>

								<af:commandButton
									text="#{bulkOperationsCatalog.bulkOperation.buttonName}"
									useWindow="true" windowWidth="800" windowHeight="600"
									action="#{bulkOperationsCatalog.setSelectedInspections}" />
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

