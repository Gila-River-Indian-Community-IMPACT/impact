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
							<af:panelHeader text="Delete Contact Type" size="0" />


							<af:panelForm>
								<af:selectInputDate label="End Date:" id="endDate"
									value="#{bulkOperationsCatalog.bulkOperation.endDate}"
									readOnly="#{!bulkOperationsCatalog.bulkOperation.deleteContactTypeEditable}"
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
					rendered="#{bulkOperationsCatalog.hasContactTypeSearchResults}">
					<af:panelBorder>
						<af:showDetailHeader text="Selected Active Contact Type Records"
							disclosed="true">
							<af:table
								value="#{bulkOperationsCatalog.selectedCtypeResultsWrapper}"
								binding="#{bulkOperationsCatalog.selectedCtypeResultsWrapper.table}"
								rows="#{bulkOperationsCatalog.pageLimit}" bandingInterval="1"
								banding="row" var="contactType">

								<af:column sortProperty="contactType" sortable="true"
									formatType="text" headerText="Contact Type">
									<af:commandLink
										text="#{infraDefs.contactTypes.itemDesc[(empty contactType.contactTypeCd ? '' : contactType.contactTypeCd)]}"
										id="viewContactType"
										action="#{contactDetail.viewContactTypeFacility}">
										<t:updateActionListener
											property="#{contactDetail.contactType}"
											value="#{contactType}" />
									</af:commandLink>
								</af:column>

								<af:column sortProperty="facilityId" sortable="true"
									noWrap="true" formatType="text" headerText="Facility ID">
									<af:commandLink action="#{facilityProfile.submitProfile}"
										text="#{contactType.facilityId}">
										<t:updateActionListener property="#{facilityProfile.fpId}"
											value="#{contactType.fpId}" />
										<t:updateActionListener
											property="#{menuItem_facProfile.disabled}" value="false" />
									</af:commandLink>
								</af:column>

								<af:column sortProperty="facilityName" sortable="true"
									formatType="text" headerText="Facility Name">
									<af:outputText value="#{contactType.facilityName}"
										truncateAt="50" 
										shortDesc="#{contactType.facilityName}"/>
								</af:column>

								<af:column sortProperty="startDate" sortable="true"
									formatType="text" headerText="Start Date">
									<af:selectInputDate value="#{contactType.startDate}"
										readOnly="true" />
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



