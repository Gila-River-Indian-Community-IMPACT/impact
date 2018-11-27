<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
	<h:panelGrid border="1"
		rendered="#{bulkOperationsCatalog.hasCorrSearchResults}">
		<af:panelBorder>
			<af:showDetailHeader text="Facility List" disclosed="true">

				<af:table value="#{bulkOperationsCatalog.facilities}"
					bandingInterval="1" banding="row" var="facility"
					rows="#{bulkOperationsCatalog.pageLimit}">
					<f:facet name="header">
						<afh:rowLayout halign="left">
							<af:panelButtonBar>
								<af:commandButton action="#{bulkOperationsCatalog.selectAllF}"
									text="Select All" />
							</af:panelButtonBar>
							<af:objectSpacer height="1" width="6" />
							<af:panelButtonBar>
								<af:commandButton action="#{bulkOperationsCatalog.selectNoneF}"
									text="Select None" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
					<af:column sortable="true" sortProperty="selected" formatType="icon" headerText="Select">
						<af:selectBooleanCheckbox label="Selected"
							value="#{facility.selected}" rendered="true">
							<f:selectItems value="#{facility.selected}" />
						</af:selectBooleanCheckbox>
					</af:column>

					<af:column sortProperty="facilityId" sortable="true"
						noWrap="true" formatType="text" headerText="Facility Id">
						<af:commandLink action="#{facilityProfile.submitProfile}"
							text="#{facility.facilityId}">
							<t:updateActionListener property="#{facilityProfile.fpId}"
								value="#{facility.fpId}" />
							<t:updateActionListener
								property="#{menuItem_facProfile.disabled}" value="false" />
						</af:commandLink>
					</af:column>

					<af:column sortProperty="name" sortable="true"
						formatType="text" headerText="Facility Name">
						<af:outputText value="#{facility.name}" />
					</af:column>

					<af:column sortProperty="operatingStatusCd" sortable="true" formatType="text"
						headerText="Operating Status">
						<af:outputText
							value="#{facilityReference.operatingStatusDefs.itemDesc[(empty facility.operatingStatusCd ? '' : facility.operatingStatusCd)]}" />
					</af:column>

					<af:column sortProperty="reportingTypeCd" sortable="true" formatType="text"
						headerText="Reporting Category">
						<af:outputText
							value="#{facilityReference.emissionReportsDefs.itemDesc[(empty facility.reportingTypeCd ? '' : facility.reportingTypeCd)]}" />
					</af:column>
					<af:column sortProperty="permitClassCd" sortable="true" formatType="text"
						headerText="Permitting Classification">
						<af:outputText
							value="#{facilityReference.permitClassDefs.itemDesc[(empty facility.permitClassCd ? '' : facility.permitClassCd)]}" />
					</af:column>
					<af:column sortProperty="permitStatusCd" sortable="true" formatType="text"
						headerText="TV Permit Status">
						<af:selectOneChoice value="#{facility.permitStatusCd}"
							readOnly="true">
							<mu:selectItems value="#{facilityReference.permitStatusDefs}" />
						</af:selectOneChoice>
					</af:column>
					<f:facet name="footer">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>

								<af:commandButton
									text="#{bulkOperationsCatalog.bulkOperation.buttonName}"
									useWindow="true" windowWidth="800" windowHeight="600"
									action="#{bulkOperationsCatalog.setSelectedFacilities}" 
									disabled="#{facilityProfile.readOnlyUser}"/>
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
