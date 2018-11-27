<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Facilities - Bulk Operations">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:messages />
			<f:subview id="contact">
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" columns="1" width="400">
						<af:panelBorder>
							<af:panelHeader text="Add Contact Type" size="0" />

							<af:panelForm>

								<af:selectOneChoice label="Type:"
									readOnly="#{!bulkOperationsCatalog.bulkOperation.addContactTypeEditable}"
									value="#{bulkOperationsCatalog.bulkOperation.contactTypeCd}"
									id="contactType" showRequired="true">
									<f:selectItems
										value="#{infraDefs.notOwnerContactTypes.items[(empty bulkOperationsCatalog.bulkOperation.contactTypeCd ? '' : bulkOperationsCatalog.bulkOperation.contactTypeCd)]}" />
								</af:selectOneChoice>

								<af:selectOneChoice
									value="#{bulkOperationsCatalog.bulkOperation.contactId}"
									readOnly="#{!bulkOperationsCatalog.bulkOperation.addContactTypeEditable}"
									label="Contact: " showRequired="true" id="contactId"
									tip="If you do not see the person (contact) you wish to assign, return to the previous screen and navigate to the Contacts tab to add a new global contact.">
									<f:selectItems
										value="#{bulkOperationsCatalog.bulkOperation.contactsList}" />
								</af:selectOneChoice>

								<af:selectInputDate label="Start Date:" id="startDate"
									value="#{bulkOperationsCatalog.bulkOperation.startDate}"
									readOnly="#{!bulkOperationsCatalog.bulkOperation.addContactTypeEditable}"
									showRequired="true">
								</af:selectInputDate>

								<af:selectInputDate label="End Date:" id="endDate"
									value="#{bulkOperationsCatalog.bulkOperation.endDate}"
									readOnly="#{!bulkOperationsCatalog.bulkOperation.addContactTypeEditable}"
									showRequired="false">
								</af:selectInputDate>

								<af:objectSpacer height="10" />

								<afh:rowLayout halign="left">
									<af:outputFormatted
										rendered="#{bulkOperationsCatalog.bulkOperation.finalStep}"
										inlineStyle="color: orange; font-weight: bold;"
										value="#{bulkOperationsCatalog.bulkOperation.message}" />
								</afh:rowLayout>

								<af:objectSpacer height="10" />

								<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
									<afh:rowLayout halign="center">
										<af:outputFormatted
											rendered="#{bulkOperationsCatalog.bulkOperation.finalStep}"
											inlineStyle="color: orange; font-weight: bold;"
											value="Are you sure you would like to continue?" />
									</afh:rowLayout>
								</afh:tableLayout>

								<af:objectSpacer height="10" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>

										<af:commandButton text="Apply"
											action="#{bulkOperationsCatalog.bulkOperation.apply}"
											rendered="#{!bulkOperationsCatalog.bulkOperation.finalStep}" />
										<af:commandButton text="Yes"
											action="#{bulkOperationsCatalog.bulkOperation.applyFinalAction}"
											rendered="#{bulkOperationsCatalog.bulkOperation.finalStep}" />
										<af:commandButton text="No"
											action="#{bulkOperationsCatalog.bulkOperation.change}"
											rendered="#{bulkOperationsCatalog.bulkOperation.finalStep}" />
										<af:commandButton text="Cancel" immediate="true"
											action="#{bulkOperationsCatalog.bulkOperation.reset}"
											actionListener="#{bulkOperationsCatalog.bulkOperationCancel}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:panelForm>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />

				<h:panelGrid border="1"
					rendered="#{bulkOperationsCatalog.hasFacilitySyncSearchResults}">
					<af:panelBorder>
						<af:showDetailHeader text="Selected Facility List"
							disclosed="true">
							<af:table
								value="#{bulkOperationsCatalog.selectedFacResultsWrapper}"
								binding="#{bulkOperationsCatalog.selectedFacResultsWrapper.table}"
								rows="#{bulkOperationsCatalog.pageLimit}" bandingInterval="1"
								banding="row" var="facility">

								<af:column sortProperty="facilityId" sortable="true"
									noWrap="true" formatType="text" headerText="Facility ID">
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

								<af:column sortProperty="companyName" sortable="true"
									formatType="text" headerText="Company Name">
									<af:outputText value="#{facility.companyName}" />
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
									<af:selectOneChoice value="#{facility.countyCd}"
										readOnly="true">
										<f:selectItems value="#{infraDefs.counties}" />
									</af:selectOneChoice>
								</af:column>

								<af:column sortProperty="c08" sortable="true" formatType="text"
									headerText="Lat/Long">
									<af:goLink text="#{facility.phyAddr.latlong}"
										targetFrame="_new"
										rendered="#{not empty facility.googleMapsURL}"
										destination="#{facility.googleMapsURL}"
										shortDesc="Clicking this will open Google Maps in a separate tab or window." />
								</af:column>
								<f:facet name="footer">
									<afh:rowLayout halign="center">
										<af:panelButtonBar>
											<af:commandButton
												actionListener="#{tableExporter.printTable}"
												onclick="#{tableExporter.onClickScript}"
												text="Printable view" />
											<af:commandButton
												actionListener="#{tableExporter.excelTable}"
												onclick="#{tableExporter.onClickScript}"
												text="Export to excel" />
										</af:panelButtonBar>
									</afh:rowLayout>
								</f:facet>
							</af:table>
						</af:showDetailHeader>
					</af:panelBorder>
				</h:panelGrid>
			</f:subview>
		</af:form>
	</af:document>
</f:view>



