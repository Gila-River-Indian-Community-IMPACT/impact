<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
	<h:panelGrid border="1"
		rendered="#{bulkOperationsCatalog.hasApplicationSyncSearchResults}">
		<af:panelBorder>
			<af:showDetailHeader text="Application List" disclosed="true">

				<af:table value="#{bulkOperationsCatalog.applicationSyncResultsWrapper}"
				binding="#{bulkOperationsCatalog.applicationSyncResultsWrapper.table}"
					bandingInterval="1" banding="row" var="app"
					rows="#{bulkOperationsCatalog.pageLimit}">
					<f:facet name="header">
						<afh:rowLayout halign="left">
							<af:panelButtonBar>
								<af:commandButton action="#{bulkOperationsCatalog.selectAllA}"
									text="Select All" />
							</af:panelButtonBar>
							<af:objectSpacer height="1" width="6" />
							<af:panelButtonBar>
								<af:commandButton action="#{bulkOperationsCatalog.selectNoneA}"
									text="Select None" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
					<af:column sortable="true" sortProperty="selected" formatType="icon" headerText="Select">
						<af:selectBooleanCheckbox label="Selected" value="#{app.selected}"
							rendered="true">
							<f:selectItems value="#{app.selected}" />
						</af:selectBooleanCheckbox>
					</af:column>

					<af:column sortable="true" sortProperty="applicationNumber"
						formatType="text" headerText="Request number">
						<af:outputText value="#{app.applicationNumber}" />
					</af:column>

					<af:column headerText="Facility ID" sortable="true"
						sortProperty="facilityId" formatType="text">
						<af:panelHorizontal valign="middle" halign="center">
							<af:outputText value="#{app.facilityId}" />
						</af:panelHorizontal>
					</af:column>

					<af:column sortable="true" sortProperty="facilityName"
						headerText="Facility Name">
						<af:panelHorizontal valign="middle" halign="left">
							<af:inputText readOnly="true" value="#{app.facilityName}" />
						</af:panelHorizontal>
					</af:column>

					<af:column sortable="true" sortProperty="doLaaCd" formatType="text"
						headerText="District">
						<af:selectOneChoice id="doLaa" readOnly="true"
							value="#{app.doLaaCd}">
							<f:selectItems value="#{applicationSearch.doLaas}" />
						</af:selectOneChoice>
					</af:column>

					<af:column sortable="true" sortProperty="countyCd"
						formatType="text" headerText="County">
						<af:selectOneChoice value="#{app.countyCd}" readOnly="true">
							<f:selectItems value="#{infraDefs.counties}" />
						</af:selectOneChoice>
					</af:column>

					<af:column sortable="true" sortProperty="applicationTypeCd"
						formatType="text" headerText="Request Type">
						<af:selectOneChoice value="#{app.applicationTypeCd}"
							readOnly="true">
							<f:selectItems
								value="#{applicationReference.applicationTypeDefs}" />
						</af:selectOneChoice>
					</af:column>

					<af:column sortable="true" sortProperty="receivedDate"
						formatType="text" headerText="Received Date">
						<af:outputText
							value="#{empty app.receivedDate? 'Not Available': app.receivedDate}" />
					</af:column>

					<af:column sortable="true" sortProperty="submittedDate"
						formatType="text" headerText="Submitted?">
						<af:outputText value="#{empty app.submittedDate ? 'No': 'Yes'}" />
					</af:column>

					<af:column sortable="true" sortProperty="previousApplicationNumber"
						formatType="text" headerText="Previous application number">
						<af:outputText
							value="#{empty app.previousApplicationNumber ? 'N/A' : app.previousApplicationNumber}" />
					</af:column>

					<af:column sortable="true" sortProperty="generalPermit"
						formatType="text" headerText="General permit">
						<af:outputText value="#{app.generalPermit}" />
					</af:column>

					<f:facet name="footer">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>

								<af:commandButton
									text="#{bulkOperationsCatalog.bulkOperation.buttonName}"
									useWindow="true" windowWidth="800" windowHeight="600"
									action="#{bulkOperationsCatalog.setSelectedApplications}" />
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

