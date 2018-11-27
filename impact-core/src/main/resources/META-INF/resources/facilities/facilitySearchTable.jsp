<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
	<h:panelGrid border="1" rendered="#{facilitySearch.hasSearchResults}">
		<af:panelBorder>
			<af:showDetailHeader text="Facility List" disclosed="true">

				<af:table value="#{facilitySearch.resultsWrapper}"
					binding="#{facilitySearch.resultsWrapper.table}"
					bandingInterval="1" banding="row" var="facility"
					rows="#{facilitySearch.pageLimit}">

					<af:column sortProperty="c01" sortable="true" noWrap="true"
						formatType="text" headerText="Facility ID">
						<af:commandLink action="#{facilityProfile.submitProfile}"
							text="#{facility.facilityId}">
							<t:updateActionListener property="#{facilityProfile.fpId}"
								value="#{facility.fpId}" />
							<t:updateActionListener
								property="#{menuItem_facProfile.disabled}" value="false" />
						</af:commandLink>
					</af:column>

					<af:column sortProperty="c02" sortable="true" formatType="text"
						headerText="Facility Name">
						<af:outputText value="#{facility.name}" />
					</af:column>

					<af:column sortProperty="c03" sortable="true" noWrap="true"
						formatType="text" headerText="Company ID">
						<af:commandLink action="#{companyProfile.submitProfile}"
							text="#{facility.cmpId}"
							rendered="#{!facilityProfile.publicApp}">
							<t:updateActionListener property="#{companyProfile.cmpId}"
								value="#{facility.cmpId}" />
							<t:updateActionListener
								property="#{menuItem_companyProfile.disabled}" value="false" />
						</af:commandLink>
						<af:outputText value="#{facility.cmpId}" rendered="#{facilityProfile.publicApp}"/>
					</af:column>

					<af:column sortProperty="c04" sortable="true" noWrap="true"
						formatType="text" headerText="Company Name">
						<af:outputText value="#{facility.companyName}" />
					</af:column>

					<af:column sortProperty="c05" sortable="true" formatType="text"
						headerText="Operating">
						<af:outputText
							value="#{facilityReference.operatingStatusDefs.itemDesc[(empty facility.operatingStatusCd ? '' : facility.operatingStatusCd)]}" />
					</af:column>

					<af:column sortProperty="c06" sortable="true" formatType="text"
						headerText="Facility Class">
						<af:outputText
							value="#{facilityReference.permitClassDefs.itemDesc[(empty facility.permitClassCd ? '' : facility.permitClassCd)]}" />
					</af:column>

					<af:column sortProperty="c07" sortable="true" formatType="text"
						headerText="Facility Type">
						<af:outputText
							value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty facility.facilityTypeCd ? '' : facility.facilityTypeCd)]}" />
					</af:column>

					<af:column sortProperty="c08" sortable="true" formatType="text"
						headerText="County">
						<af:selectOneChoice value="#{facility.countyCd}" readOnly="true">
							<f:selectItems value="#{infraDefs.counties}" />
						</af:selectOneChoice>
					</af:column>

					<af:column sortProperty="c09" sortable="true" formatType="text"
						headerText="Lat/Long">
						<af:goLink text="#{facility.phyAddr.latlong}" targetFrame="_new"
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
