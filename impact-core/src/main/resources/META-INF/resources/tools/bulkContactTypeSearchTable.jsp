<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
	<h:panelGrid border="1"
		rendered="#{bulkOperationsCatalog.hasContactTypeSearchResults}">
		<af:panelBorder>
			<af:showDetailHeader text="Active Contact Type Records"
				disclosed="true">

				<af:table value="#{bulkOperationsCatalog.contactTypeResultsWrapper}"
					binding="#{bulkOperationsCatalog.contactTypeResultsWrapper.table}"
					bandingInterval="1" banding="row" var="contactType"
					rows="#{bulkOperationsCatalog.pageLimit}">
					<f:facet name="header">
						<afh:rowLayout halign="left">
							<af:panelButtonBar>
								<af:commandButton action="#{bulkOperationsCatalog.selectAllCT}"
									text="Select All" />
							</af:panelButtonBar>
							<af:objectSpacer height="1" width="6" />
							<af:panelButtonBar>
								<af:commandButton action="#{bulkOperationsCatalog.selectNoneCT}"
									text="Select None" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>

					<af:column sortable="true" sortProperty="selected"
						formatType="icon" headerText="Select">
						<af:selectBooleanCheckbox label="Selected"
							value="#{contactType.selected}" rendered="true">
							<f:selectItems value="#{contactType.selected}" />
						</af:selectBooleanCheckbox>
					</af:column>

					<af:column sortProperty="contactType" sortable="true" formatType="text"
						headerText="Contact Type">
						<af:commandLink
							text="#{infraDefs.contactTypes.itemDesc[(empty contactType.contactTypeCd ? '' : contactType.contactTypeCd)]}"
							id="viewContactType"
							action="#{contactDetail.viewContactTypeFacility}">
							<t:updateActionListener property="#{contactDetail.contactType}"
								value="#{contactType}" />
						</af:commandLink>
					</af:column>
					
					<af:column sortProperty="facilityId" sortable="true" noWrap="true"
						formatType="text" headerText="Facility ID">
						<af:commandLink action="#{facilityProfile.submitProfile}"
							text="#{contactType.facilityId}">
							<t:updateActionListener property="#{facilityProfile.fpId}"
								value="#{contactType.fpId}" />
							<t:updateActionListener
								property="#{menuItem_facProfile.disabled}" value="false" />
						</af:commandLink>
					</af:column>
					
					<af:column sortProperty="facilityName" sortable="true" formatType="text"
						headerText="Facility Name">
						<af:outputText value="#{contactType.facilityName}" truncateAt="50" shortDesc="#{contactType.facilityName}"/>
					</af:column>
					
					<af:column sortProperty="startDate" sortable="true" formatType="text"
						headerText="Start Date">
						<af:selectInputDate value="#{contactType.startDate}"
							readOnly="true" />
					</af:column>

					<f:facet name="footer">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>

								<af:commandButton
									text="#{bulkOperationsCatalog.bulkOperation.buttonName}"
									useWindow="true" windowWidth="800" windowHeight="600"
									action="#{bulkOperationsCatalog.setSelectedContactTypes}" 
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
