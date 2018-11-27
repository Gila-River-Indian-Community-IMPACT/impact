<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>



<afh:rowLayout halign="center">
	<h:panelGrid border="1"
		rendered="#{complianceReport.complianceReport.template eq true}">
		<af:panelBorder>
			<af:showDetailHeader text="Submit Report for Additional Facilities" disclosed="true">
				<af:outputText inlineStyle="font-size:75%;color:#666"
					value="This report can be submitted for additional facilities.
					Select the facilities you wish to include such a report for,
					in the submission, that contains the same data (description, dates,
					attachment(s), etc.). If you wish to submit reports that do not
					contain the same data, separate submissions are required.">
				</af:outputText>

				<af:table value="#{complianceReport.bulkComplianceReportFacilities}"
					bandingInterval="1" banding="row" var="facility" width="100%" 
					rows="#{complianceReport.pageLimit}">
					<f:facet name="header">
						<afh:rowLayout halign="left">
							<af:panelButtonBar>
								<af:commandButton action="#{complianceReport.selectAllF}"
									text="Select All" />
							</af:panelButtonBar>
							<af:objectSpacer height="1" width="6" />
							<af:panelButtonBar>
								<af:commandButton action="#{complianceReport.selectNoneF}"
									text="Select None" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>

					<af:column sortable="true" sortProperty="selected" formatType="icon" headerText="Select">
						<af:selectBooleanCheckbox label="Selected" autoSubmit="true"
							value="#{facility.selected}" rendered="true">
							<f:selectItems value="#{facility.selected}" />
						</af:selectBooleanCheckbox>
					</af:column>

					<af:column sortProperty="facilityId" sortable="true" noWrap="true"
						formatType="text" headerText="Facility ID">
						<af:commandLink action="#{facilityProfile.submitProfile}" rendered="#{complianceReport.internalApp eq true}"
							text="#{facility.facilityId}">
							<t:updateActionListener property="#{facilityProfile.fpId}"
								value="#{facility.fpId}" />
							<t:updateActionListener
								property="#{menuItem_facProfile.disabled}" value="false" />
						</af:commandLink>
						<af:outputText value="#{facility.facilityId}"  rendered="#{complianceReport.internalApp eq false}" />
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


											<af:column sortProperty="permitClassCd" sortable="true"
												formatType="text" headerText="Facility Class">
												<af:outputText
													value="#{facilityReference.permitClassDefs.itemDesc[(empty facility.permitClassCd ? '' : facility.permitClassCd)]}" />
											</af:column>

											<af:column sortProperty="facilityTypeDesc" sortable="true"
												formatType="text" headerText="Facility Type">
												<af:outputText value="#{facility.facilityTypeDesc}" />
											</af:column>

											<af:column sortProperty="countyName" sortable="true"
												formatType="text" headerText="County">
												<af:outputText value="#{facility.countyName}" />
											</af:column>

											<af:column sortProperty="latLong" sortable="true"
												formatType="text" headerText="Lat/Long">
												<af:goLink text="#{facility.latLong}"
													targetFrame="_new"
													rendered="#{not empty facility.googleMapsURL}"
													destination="#{facility.googleMapsURL}"
													shortDesc="Clicking this will open Google Maps in a separate tab or window." />
											</af:column>

					<f:facet name="footer">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>

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
