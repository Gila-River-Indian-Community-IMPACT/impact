<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
	<h:panelGrid border="1"
		rendered="#{bulkOperationsCatalog.hasFacilitySyncSearchResults}">
		<af:panelBorder>
			<af:showDetailHeader text="Facility List" disclosed="true">

				<af:table
					value="#{bulkOperationsCatalog.facilitySyncResultsWrapper}"
					binding="#{bulkOperationsCatalog.facilitySyncResultsWrapper.table}"
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

					<af:column sortable="true" sortProperty="selected"
						formatType="icon" headerText="Select">
						<af:selectBooleanCheckbox label="Selected"
							value="#{facility.selected}" rendered="true">
							<f:selectItems value="#{facility.selected}" />
						</af:selectBooleanCheckbox>
					</af:column>

					<af:column sortProperty="facilityId" sortable="true" noWrap="true"
						formatType="text" headerText="Facility ID">
						<af:commandLink action="#{facilityProfile.submitProfile}"
							text="#{facility.facilityId}">
							<t:updateActionListener property="#{facilityProfile.fpId}"
								value="#{facility.fpId}" />
							<t:updateActionListener
								property="#{menuItem_facProfile.disabled}" value="false" />
						</af:commandLink>
					</af:column>

					<af:column sortProperty="name" sortable="true" formatType="text"
						headerText="Facility Name">
						<af:outputText value="#{facility.name}" />
					</af:column>

					<af:column sortProperty="cmpId" sortable="true" noWrap="true"
						formatType="text" headerText="Company ID">
						<af:commandLink action="#{companyProfile.submitProfile}"
							text="#{facility.cmpId}">
							<t:updateActionListener property="#{companyProfile.cmpId}"
								value="#{facility.cmpId}" />
							<t:updateActionListener
								property="#{menuItem_companyProfile.disabled}" value="false" />
						</af:commandLink>
					</af:column>
					<af:column sortProperty="companyName" sortable="true"
						formatType="text" headerText="Company Name">
						<af:outputText value="#{facility.companyName}" />
					</af:column>
					
					<af:column sortProperty="doLaaCd" sortable="true"
						formatType="text" headerText="District">
						<af:selectOneChoice value="#{facility.doLaaCd}" readOnly="true">
							<f:selectItems value="#{infraDefs.districts}"/>
						</af:selectOneChoice>	
					</af:column>	

					<af:column sortProperty="operatingStatusCd" sortable="true"
						formatType="text" headerText="Operating">
						<af:outputText
							value="#{facilityReference.operatingStatusDefs.itemDesc[(empty facility.operatingStatusCd ? '' : facility.operatingStatusCd)]}" />
					</af:column>

					<af:column sortProperty="permitClassCd" sortable="true"
						formatType="text" headerText="Facility Class">
						<af:outputText
							value="#{facilityReference.permitClassDefs.itemDesc[(empty facility.permitClassCd ? '' : facility.permitClassCd)]}" />
					</af:column>

					<af:column sortProperty="facilityTypeCd" sortable="true"
						formatType="text" headerText="Facility Type">
						<af:outputText
							value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty facility.facilityTypeCd ? '' : facility.facilityTypeCd)]}" />
					</af:column>

					<af:column sortProperty="countyCd" sortable="true"
						formatType="text" headerText="County">
						<af:selectOneChoice value="#{facility.countyCd}" readOnly="true">
							<f:selectItems value="#{infraDefs.counties}" />
						</af:selectOneChoice>
					</af:column>

					<af:column sortProperty="c08" sortable="true" formatType="text"
						headerText="Lat/Long">
						<af:goLink text="#{facility.phyAddr.latlong}" targetFrame="_new"
							rendered="#{not empty facility.googleMapsURL}"
							destination="#{facility.googleMapsURL}"
							shortDesc="Clicking this will open Google Maps in a separate tab or window." />
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
