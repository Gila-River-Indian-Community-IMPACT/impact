<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
	<h:panelGrid border="1" rendered="#{companySearch.hasSearchResults}">
		<af:panelBorder>
			<af:showDetailHeader text="Company List" disclosed="true">

				<af:table value="#{companySearch.resultsWrapper}"
					binding="#{companySearch.resultsWrapper.table}" bandingInterval="1"
					banding="row" var="company" rows="#{companySearch.pageLimit}">

					<af:column sortProperty="c01" sortable="true" noWrap="true"
						formatType="text" headerText="Company ID">
						<af:commandLink action="#{companyProfile.submitProfile}"
							text="#{company.cmpId}">
							<t:updateActionListener property="#{companyProfile.cmpId}"
								value="#{company.cmpId}" />
							<t:updateActionListener
								property="#{menuItem_companyProfile.disabled}" value="false" />
						</af:commandLink>
					</af:column>

					<af:column sortProperty="c02" sortable="true" formatType="text"
						headerText="Company Name">
						<af:outputText value="#{company.name}" />
					</af:column>

					<af:column sortProperty="c03" sortable="true" formatType="text"
						headerText="Company Alias">
						<af:outputText value="#{company.alias}" />
					</af:column>

					<af:column sortProperty="c04" sortable="true" formatType="text"
						headerText="Address 1">
						<af:outputText value="#{company.address.addressLine1}" />
					</af:column>

					<af:column sortProperty="c05" sortable="true" formatType="text"
						headerText="Address 2">
						<af:outputText value="#{company.address.addressLine2}" />
					</af:column>

					<af:column sortProperty="c06" sortable="true" formatType="text"
						headerText="City">
						<af:outputText value="#{company.address.cityName}" />
					</af:column>

					<af:column sortProperty="c07" sortable="true" formatType="text"
						headerText="State">
						<af:outputText value="#{company.address.state}" />
					</af:column>

					<af:column sortProperty="c08" sortable="true" formatType="text"
						headerText="Zip Code">
						<af:outputText value="#{company.address.zipCode}" />
					</af:column>

					<af:column sortProperty="c09" sortable="true" formatType="text"
						headerText="Country">
						<af:outputText value="#{company.address.countryCd}" />
					</af:column>

					<af:column sortProperty="c10" sortable="true" formatType="text"
						headerText="Phone">
						<af:outputText value="#{company.phone}"
							converter="#{infraDefs.phoneNumberConverter}" />
					</af:column>

					<af:column sortProperty="c11" sortable="true" formatType="text"
						headerText="Fax">
						<af:outputText value="#{company.fax}"
							converter="#{infraDefs.phoneNumberConverter}" />
					</af:column>

					<af:column sortProperty="externalId" sortable="true"
						formatType="text" headerText="CROMERR ID">
						<af:outputText value="#{company.externalCompanyId}" />
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
